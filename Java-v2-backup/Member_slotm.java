
/***************************************************************************************
 *   Member_slotm:  This servlet will process the 'Consecutive Tee Times' request from
 *                    the Member's Sheet page (via Member_slot) for Sawgrass only.
 *
 *
 *   called by:  Member_sheet via Member_slot (doGet)
 *
 *
 *   created: 12/21/2004   Bob P. for Sawgrass
 *
 *   last updated:       ******* keep this accurate *******
 *
 *        4/14/14   CC of Naples - put in checks to catch cheaters that are getting in early for tee times.
 *        3/28/14   El Camino CC (elcaminocc) - The processing for handling >5 consecutive times will now apply when booking 2-5 consecutive times (case 2385).
 *        2/10/14   Tartan Fields GC (tartanfields) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *        2/10/14   TPC Snoqualmie Ridge (snoqualmieridge) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *        2/10/14   Pinery CC (pinery) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *        2/10/14   The Club at Pradera (pradera) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *        1/28/14   Lakewood Ranch (lakewoodranch) - Do not display the 'WLK' MoT for members in tee times prior to 1:00pm every day (case 2355).
 *       12/13/13   Silverleaf (silverleaf) - Added custom to send the caddie master an email if tee times containing "CAD" or "FOR" MoTs are booked/modified/canceled (case 2329).
 *       10/31/13   Fixed an issue that was causing an error when selecting consecutive times on the back-nine and got prompted for alternate times.
 *        9/16/13   Added call to verifySlot.shiftUpM to shift players up into black slots only within their local time (they will not be slid into an earlier tee time in the group).
 *        6/07/13   Elgin CC (elgincc) - Added custom to prevent members from booking advance tee times that contain more than 7 guests for a single member (case 2226).
 *        6/06/13   Add the member's name at the end of the notes when they add tee time notes (so staff can determine which member entered the notes).
 *        5/03/13   Minikahda - add custom to remove all MOTs except carts and caddies on weekends and holidays.
 *        4/25/13   Expand the Guest Quota error message to include the number of guests allowed and the 'per' value (request from Interlachen).
 *        3/06/13   Remove debug messages - no longer needed.
 *        2/20/13   Enhance the 'timed out' error message in verify to explain that navigating away will release the tee times.
 *        2/19/13   Beef up the checks in checkInUseBy method (called by verify) to ensure that player1 is empty in each time and report a specific error if one found.
 *        2/14/13   Pass the session to verifySlot.addMteeTime so tee times can be removed from the user's session.
 *        2/11/13   When in json mode call checkInUseSession instead of checkInUseMN.  Skip other processing too as it is not necessary.
 *        2/05/13   Updated debug messages for double booking catches to include additional inormation (date, course, fb, etc).
 *        1/30/13   Added additional debug message that gets output when a member enters the consecutive time processing.
 *        1/22/13   Added debug message logging to try and figure out how multiple members are being allowed into overlapping times.
 *        1/21/13   Rehoboth Beach CC (rehobothbeachcc) - Added custom to send the caddie master an email if tee times containing "CAD" or "CFC" MoTs are booked/modified/canceled (case 2217).
 *        1/11/13   Updated doPost processing to ensure that certain fields in slotParms are being set prior to calling Common_slot.verifyMassConsec.
 *       12/25/12   Added code to accommodate booking 6-15 tee times.  Attempting to do so will break out of the normal processing and use a new format to quickly book 6-15 tee times in their name.
 *       11/27/12   Tweak iframe resize code
 *       11/14/12   Beef up the checks in checkInUseBy method (called by verify) to ensure that all requested tee times are still available.
 *                  Also, remove tee time values in the email parm if user did not add members to all the tee times they requested (so empty times are not added to email).
 *        7/17/12   Baltimore CC (baltimore) - Added custom to send the caddie master an email if tee times containing "CAD" MoTs are booked/modified/canceled (case 2158).
 *        7/03/12   The 'slots' parameter will now get populated in the parmSlot instance that gets passed to verifyCustom.checkCustoms1 for each individual tee time.
 *        7/03/12   Minikahda CC (minikahda) - Removed old custom code, since it is being handled in verifyCustom now.
 *        4/30/12   Wisconsin Club (brynwoodcc) - Updated custom to apply from 5/12-9/22 instead of 5/1-9/30 (case 2115).
 *        4/30/12   Eagle Creek CC (eaglecreek) - Updated custom to catch cheaters to get member's days in advance parms instead of using hard values.
 *        3/29/12   Wisconsin Club (brynwoodcc) - Added custom to hide any modes of transportation aside from 'GC', 'CAD', 'C/C', 'FC' (case 2115).
 *        3/27/12   Huntingdon Valley CC (huntingdonvalleycc) - Added custom to default all player slots to 9 holes on the "Centennial Nine" course (case 2120).
 *        3/23/12   Fixed bug with old skin where selecting consecutive times and getting presented with a member notice kept looping the member notice page.
 *        1/31/12   Wildcat Run G&CC (wildcatruncc) - Added custom to use pre-checkin for times booked after 4:30pm ET 1 day in advance (case 2111).
 *        1/16/12   Moved more slot notification text into jquery-foreTeeSlot -- still more to do when time allows
 *        1/13/12   Changed method of selecting elements to be submitted by jquery-foreTeesSlot
 *        1/11/12   More changes for new skin, begin implementing json response in verify slot
 *        1/10/12   Eagle Creek CC (eaglecreek) - Updated custom to catch cheaters so it's active for times booked 2 days in advance instead of 3 days.
 *        1/07/12   Updates to tee time in_use for new skin; Dependent on Memeber_slot, Memeber_slotm, Common_slot, verifySlot, parmSlotPage
 *       12/15/11   Fixed bug in old skin mode, introduced with new skin, affecting guest defCW
 *       12/15/11   Fixed issue in setting default player name
 *       12/14/11   Started changes for new skin; Moved new skin slot page creation to slotPage
 *       12/08/11   Olympic Club (olyclub) - Added custom to remove the trans mode "Caddie" (CAD) from being selectable in tee times (used for events only) (case 2091).
 *       12/06/11   Long Cove Club (longcove) - Remove from custom to catch cheaters.
 *       11/01/11   parm block values custom_int1-5 values returned from verifyCustom.checkCustoms1 will be preserved.
 *       10/25/11   Wildcat Run CC (wildcatruncc) - Set default MoT to 'CCT' for all guest types.
 *        9/29/11   Monterey Peninsula CC (mpccpb) - Set default MoT to 'WLK' for all guest types.
 *        9/26/11   Wildcat Run CC (wildcatruncc) - Use the non-consecutive option for checkInUseMN (case 2036).
 *        5/25/11   Cape Cod National (capecodnational) - Set default mode of trans to 'C' for all guest types.
 *        5/19/11   Royal Montreal GC (rmgc) - set default mode of trans to 'CRT' for all guest types.
 *        4/06/11   Talbot CC (talbotcc) - Added 'Fam Wkend' and 'Fam Wkday' to be removed as part of the custom (case 1940).
 *        3/25/11   Talbot CC (talbotcc) - If Mon-Fri remove 'Wkend Guest' gtype, if Sat/Sun remove 'Wkday Guest' gtype (case 1940).
 *        9/09/09   Black Diamond Ranch (blackdiamondranch) - set default mode of trans to 'TT' if guest type equals "Turn Time"
 *        2/09/11   Default MoT will now be checked to verify they are valid MoTs for the current course before being added dynamically to the MoT selection drop-down.
 *        2/09/11   Tanoan CC (tanoan) - Set default MoT to 'TC' for all guest types.
 *        1/19/11   Mediterra - Commented out custom to remove the "Walking" mode of trans (Case 1263).
 *        1/19/11   Dataw Island Club (dataw) - Fixed default MoT custom to default certain guest types to "TF" instead of "TR".
 *       12/17/10   Dataw Island Club (dataw) - Set default MoT depending on guest type.
 *       12/01/10   Long Cove - put in check to trace cheaters that are getting in early for tee times.
 *       10/20/10   Populate new parmEmail fields
 *        8/27/10   Shady Canyon GC (shadycanyongolfclub) - If a note is included with a tee time, send a custom email notification to staff members (case 1881).
 *        8/10/10   Changes to support for incoming encrypted tee time info
 *        6/07/10   Added code to guest tracking verification to allow for the guest "TBA" option
 *        5/27/10   Change the order that names are listed - sort by the last name only (without any extensions) so families are grouped together.
 *        5/21/10   Turner Hill (turnerhill) - set default mode of trans to 'CRT' for all guest types.
 *        5/14/10   Imperial GC (imperialgc) - Only apply MoT custom until 5/31/2010 - they are removing this restriction (case 1287).
 *        5/06/10   Lake Forest CC - do not allow WLK on weekends until after 10:00 AM (case 1831).
 *        4/28/10   Eagle Creek CC (eaglecreek) - Updated custom to catch cheaters to allow bookings up to 3 days in adv instead of 2
 *        4/18/10   Added guest tracking proccessing
 *        4/15/10   Peninsula Club - display a custom message next to the Walk mode of trans in legend (case 1820).
 *        4/15/10   Do not display pro-only modes of trans in the legend (case 1820).
 *        3/30/10   Pelican Marsh - if tee time is within 48 hours of now, do not allow members to remove players unless they added them (case 1786).
 *        3/26/10   Add userg, members and guest counts to parm in checkCustoms1.
 *        2/02/10   Trim the notes in verify
 *       12/04/09   Champion Hills - set default MOT for guests.
 *       12/02/09   Call to alphaTable.displayPartnerList added to print the partner list, outdated code removed
 *       11/16/09   If course only has one mode of trans, then use that as the member's default (case 1744).
 *       11/06/09   Eagle Creek - put in checks to catch cheaters that are getting in early for tee times.
 *       10/19/09   The Lakes - do not allow members to book a time with guests if 7 days in advance and before 7:30 AM (case 1736).
 *       10/14/09   Imperial GC (imperialgc) - Only apply MoT custom from 1/1 to 5/31 each year (case 1287).
 *       10/08/09   Ocean Reef CC (oceanreef) - set default mode of trans to 'CT' for all guest types.
 *       10/04/09   Added activity isolation to the buddy list
 *        9/10/09   Woodlands CC - set default mode of trans to 'CRT' for all guest types (case 1722).
 *        9/09/09   Black Diamond Ranch (blackdiamondranch) - set default mode of trans to 'CCT' for all guest types
 *        9/03/09   Beverly - move custom guest quota to verifyCustom so proshop side will get it too.
 *        9/01/09   The Lakes - do not allow members to book a time with guests if more than 5 days in advance and before 11 AM (case 1691).
 *        8/27/09   Add call verifyCustom.checkCustoms1 to check for custom restrictions - this will be the NEW
 *                  process for adding customs and should make it much easier.  Only verifyCustom should have to
 *                  be modified for future customs.
 *        8/11/09   Minnetonka CC (minnetonkacc) - set default mode of trans to 'WLK' for all guest types (case 1708).
 *        7/30/09   Add focus setting for moveguest js method to improve efficiency
 *        7/28/09   Sawgrass CC (sawgrass) - Set default mode of trans to 'CRT' for all guest types
 *        7/10/09   Charlotte CC (charlottecc) - Added custom message to successful booking splash page for groups with 2 or more guests (case 1700).
 *        5/07/09   Save the user's username in slotParms for custom processing.
 *        4/04/09   The Oaks Club - use the non-consecutive option for checkInUseMN (case 1671). 
 *        4/24/09   Add call to verifyCustom.checkCustomsGst after guests are assigned for custom guest restrictions.
 *                  This was done as part of a TPC unaccompanied guest custom (case 1663).
 *        4/09/09   Blue Bell CC - set default mode of trans to 'CAR' for all guest types.
 *        3/20/09   The Oaks Club - Utilize pre-checkin for tomorrow bookings if it's after 6pm today (case 1589).
 *        3/19/09   TPC Sugarloaf - set default mode of trans to 'GCT' for all guest types.
 *        3/10/09   Admirals Cove - put in checks to catch cheaters that are getting in early for tee times.
 *        3/06/09   Imperial GC - Change pre-checkin from 2pm to 1 PM - Case #1327
 *        2/13/09   Added dining request prompt processing
 *        2/12/09   Pelicans Nest - temp custom to try to catch cheaters - make history entry when setting tee time in use.
 *       12/26/08   Gulf Harbour GCC - set default mode of trans to 'CRT' for all guest types.
 *       12/18/08   Pelican Marsh GC - set default mode of trans to 'GC' for all guest types.
 *       12/09/08   Added guest restriction suspension handling
 *       12/04/08   Update parseNames to rebuild the players' names if members to make sure we get the proper titlecase (case 1576).
 *       11/12/08   Do not plug the user's name into the next available slot if not the first time here (case 1583). 
 *       11/12/08   Lakewood Ranch - use the non-consecutive option for checkInUseMN (case 1246). 
 *       10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *        7/07/08   Admirals Cove - set the default mode of trans to 'CF' for all guest types (case #1514).
 *        6/26/08   Added javascript to dynamically add/remove Pro Only tmodes to wc selection drop down boxes
 *        6/26/08   Get the teecurr_id from each tee time and save in parm so we can more easily id the tee times.
 *        6/25/08   Wilmington - remove custom that removed CYO mode of trans during specified days and times.
 *        6/23/08   Changes to ProOnly tmode checking
 *        6/22/08   Changes to checkTmodes
 *        6/19/08   Added checkTmodes() method to verify Pro-Only tmodes
 *        6/19/08   Minikahda - make some adjustments to custom guest restriction (see Bob's custom doc).
 *        6/16/08   Chartwell GC - set the default mode of trans to 'CAR' for all guest types (case #1504).
 *        6/13/08   Minikahda - change their custom guest restriction to not include weeknds and holidays before 10 AM (case 1027).
 *        6/11/08   Sonnenalp - do not allow members to use ICP and FCP modes of trans unless it is their default (case 1452).
 *        6/07/08   Trophy Club - set the default mode of trans to 'GC' for all guest types (case #1489).
 *        6/03/08   St. Clair CC - always default the Terrace course to 9 holes (case 1476).
 *        5/16/08   checkInUseMN was moved from verifyCusotm to verifySlot.
 *        5/05/08   Beverly GC - add custom guest quota for Wed & Fri (case 1449).
 *        5/05/08   Comment out customs for mship types (custom_disp) - replace by new tflag feature.
 *        4/24/08   Add member tag feature (tflag) where we flag certain members on pro tee sheet (case 1357).
 *        4/11/08   Oak Hill CC - tee times must contain at least one guest in addition to the member(s) - case 1421.
 *        4/02/08   MN Valley - set the default mode of trans to 'WK' for all guest types (case #1439).
 *        4/02/08   Inverness Club - correct custom for addvance guest times to adjust for 'days in advance' 
 *                  settings now at 365.
 *        3/12/08   Long Cove - as of 3/17/08 remove the custom that allowed members to book 7 tee times per day.
 *        2/29/08   Add the individual tee times to the groups in emails of multiple tee time requests (case 1231). 
 *        1/07/08   Jonathan's Landing - Removed tmode custom (Case# 1330)
 *       12/12/07   Plantation at Ponte Vedra - use the non-consecutive option for checkInUseMN. 
 *       12/04/07   Mediterra - Utilize pre-checkin for tomorrow bookings if it's after 5:30pm today - Case #1309
 *       12/04/07   Berkeley Hall - Remove the trans mode of 'REC' and 'CMP' (Case 1341)
 *       12/04/07   Pelican's Nest - Utilize pre-checkin for tomorrow bookings if it's after 5pm today - Case #1296
 *       12/04/07   Reposition the max_originations check so that multiple times are not marked as in-use
 *       12/03/07   Imperial GC - Utilize pre-checkin for tomorrow bookings if it's after 2pm today - Case #1327
 *       12/03/07   Jonathan's Landing then remove certain options  (Case# 1330)
 *       11/29/07   Jonathans landing - force the mode of trans to 'CRT' for all guest types (case #1334).
 *       11/13/07   Bug fix for default tmode (javascript error defCW/defWC)
 *       11/08/07   Imperial GC - use the non-consecutive option for checkInUseMN (Case #1319). 
 *       11/07/07   Imperial Golf Club - Remove trans mode of Walk & Pull Cart everyday until 11:30AM EST (Case #1287)
 *       11/05/07   Mediterra - force the mode of trans to 'R' for all guest types (case #1315).
 *       10/24/07   Add call to new checkMaxOrigBy for enforcing a max number of rounds that can be originated by a member
 *       10/16/07   Red Rocks - force the mode of trans to 'NA' for all guest types (case #1138).
 *       10/16/07   Pinery - force the mode of trans to 'GC' for all guest types (case #1252).
 *       10/11/07   Put member's mship and mtype in slotParms for checkInUseMn so it can check restrictions.
 *       10/06/07   Eagle Creek CC - use the non-consecutive option for checkInUseMN (case #1283). 
 *        9/22/07   Pelicans Nest - use the non-consecutive option for checkInUseMN. 
 *        9/21/07   Mediterra - remove the trans mode of 'Walking' (Case 1263)
 *        9/21/07   Make the custom to prompt user with next available tee time standard for all clubs.
 *        9/28/07   Lakewood Ranch (FL) - Use checkInUseMn to allocate tee times in case one or more is busy (case #1246).
 *        8/28/07   Meadowbrook CC (WI) - Use checkInUseMn to allocate tee times in case one or more is busy (case #1220).
 *        8/20/07   Modified call to verifySlot.checkMemNotice
 *        7/17/07   Wilmington - add range privilege indicator to the tee time for display on tee sheet (case #1204).
 *        7/05/07   Olympia Fields CC - must always be more than 1 player in request.
 *        6/29/07   Merrill Hills - check for special mships so they can be marked on pro tee sheet (case #1183).
 *        6/26/07   When pulling a handicap from member2b get it from the g_hancap field instead of c_hancap
 *        6/18/07   Catamount Ranch - change max number of advance tee times back to 5, except for Founder members - they are unlimited (case #1124).
 *        6/18/07   Sonnenalp - check for max number of advance tee times (case #1089).
 *        6/12/07   Allow for course=-ALL- option - return to course=all if selected.
 *        5/29/07   Sonnenalp - add guest fees to the tee time for display on tee sheet (case #1070).
 *        5/24/07   Catamount Ranch - change max number of advance tee times from 5 to 14 (case #1124).
 *        4/26/07   Long Cove - Use checkInUseMn to allocate tee times in case one or more is busy (case #1149).
 *        4/26/07   Minikahda - Custom Guest restriction for 3 guests & 1 member (case #1027).
 *        4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *        4/23/07   Change call to verifyCustom.checkInUseMn to pass a new 'consecutive' option,
 *        4/06/07   Do not include members that are inactive (new inact flag in member2b).
 *        3/31/07   Added custom for Inverness Club - If weekday && guest part of tee time, can book through Oct. 31st. 
 *        3/29/07   Add call to verifyCustom.checkInUseMn for CC of Jackson - if one time of a multi request is busy,
 *                  check for other available times (case 1074).
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        2/27/07   Add new 'Member Notice' processing - display pro-defined message if found.
 *        2/15/07   Set the clubname and course name for getClub.getParms.
 *        2/15/07   Awbrey Glen - Juniors must be with an adult at all times, Juniors Over 12
 *                                must be accompanied by an adult before Noon.
 *        2/09/07   Wilmington CC - check for max of 12 guests total during specified times.
 *                  Also, do not show CYO mode of trans during specified times.
 *        1/04/07   Peninsula Club - Juniors must be with an adult at all times.
 *       12/08/06   Long Cove - if one time is not available, grab the ones that are (verifyCustom.checkInUseMc).
 *                              Prompt user to see if they want to accept less times.
 *       11/01/06   Long Cove - TEMP to allow members to book more than one time per day.
 *        8/31/06   Long Cove - allow multiple players with the same name.
 *        4/11/06   Catamount Ranch - check for max number of advance tee times.
 *        2/27/06   Whenever tee time is added, call SystemUtils.updateHist to track it.
 *        2/15/06   Allow for new call from Member_slot doGet method.
 *        1/18/06   Lakewood - change the 'Guest Types' box title.
 *       11/08/05   Always check for 'Exception' on the catch from calls to verifySlot.
 *       10/12/05   Lakewood - add custom restriction for spouses.
 *        6/27/05   Hartefeld National - must always be more than 1 player in request.
 *        6/24/05   North Shore CC - add check for 'guest-only' times (must be at least 2 guests).
 *        6/07/05   Allow X's to be specified without a mode of trans.
 *        4/25/05   Cordillera - Do not include Employees in member name list.
 *        4/25/05   Add no-cache controls to resp header to prevent client from caching this page.
 *                  This will ensure that the Cancel Tee Time buttons apears when it should.
 *        4/01/05   Put the 'X' option by itself to avoid confusion with guest types.
 *        3/19/05   fix some javascript to be more compatible [Paul S]
 *        2/17/05   Piedmont - do not display 2 of their modes of trans options..
 *        2/17/05   Ver 5 - add support for option to force members to specify a guest's name.
 *        2/16/05   Include the name of the guest restriction in error message.
 *        2/08/05   Change name lists to match _slot.
 *        2/07/05   Do not move names up - allow empty slots in groups.
 *        1/19/05   Ver 5 - if member does not have a default mode of trans, leave box blank in tee slot
 *                          to force them to specify the correct mode.
 *
 *
 ***************************************************************************************
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.ProcessConstants;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.parmSlotm;
import com.foretees.common.parmSlot;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.alphaTable;
import com.foretees.common.BigDate;
import com.foretees.common.congressionalCustom;
import com.foretees.common.Utilities;
import com.foretees.common.nameLists;
import com.foretees.common.formUtil;
import com.foretees.common.parmSlotPage;
import com.foretees.common.Connect;
import com.foretees.common.timeUtil;

public class Member_slotm extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
    static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
    static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
    static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
    static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
    static long Hdate7 = ProcessConstants.tgDay;      // Thanksgiving Day
    static long Hdate8 = ProcessConstants.colDay;     // Columbus Day

    //*****************************************************
    // Process the request from Member_slot
    //*****************************************************
    //
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //
        //  If call from self, then go to doPost
        //
        if (req.getParameter("go") != null) {         // if call from the following process

            doPost(req, resp);      // call doPost processing
        }
    }

    //*****************************************************
    // Process the request from doGet above or self
    //*****************************************************
    //
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        //
        //  Prevent caching so all buttons are properly displayed, etc.
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();


        ResultSet rs = null;

        HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

        if (session == null) {

            return;
        }

        Connection con = Connect.getCon(req);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, please contact customer support.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }


        //
        // Process request according to which 'submit' button was selected
        //
        //      'time:fb'   - request from Member_sheet
        //      'continue'  - request details from Member_slotm
        //      'submitForm' - submit request from Member_slotm
        //      'letter'    - request to list member names from Member_slotm
        //      'cancel'    - user clicked on the 'Go Back' button (return w/o changes)
        //      'return'    - a return to Member_slotm from verify
        //
        if (req.getParameter("cancel") != null) {

            cancel(req, out, con, session);                      // process cancel request
            return;
        }

        if (req.getParameter("submitForm") != null) {

            verify(req, out, con, session, resp);                 // process reservation requests
            return;
        }

        String jump = "0";                     // jump index - default to zero (for _sheet)

        if (req.getParameter("jump") != null) {            // if jump index provided

            jump = req.getParameter("jump");
        }

        String returnCourse = "";

        //
        //  Get this session's username
        //
        String club = (String) session.getAttribute("club");
        String user = (String) session.getAttribute("user");
        String name = (String) session.getAttribute("name");   // get users full name
        int activity_id = (Integer) session.getAttribute("activity_id");
        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        String index = req.getParameter("index");          //  index value of day (needed by Member_sheet when returning)
        String course = req.getParameter("course");        //  Name of Course

        course = SystemUtils.filter(course);              // replace html special characters with real ones

        //
        //  parm block to hold the tee time parms
        //
        parmSlotm slotParms = new parmSlotm();          // allocate a parm block

        slotParms.club = club;                          // save club name & other parms
        slotParms.course = course;
        slotParms.index = index;
        slotParms.mship = (String) session.getAttribute("mship");            // get member's mship type
        slotParms.mtype = (String) session.getAttribute("mtype");            // get member type

        //
        //  Request from Member_sheet or Member_slotm 
        //
        //int count = 0;
        int hr = 0;
        int min = 0;
        int time = 0;
        int fb = 0;
        int x = 0;
        int xCount = 0;
        int i = 0;
        int hide = 0;
        //int nowc = 0;
        int slots = 0;
        int players = 0;
        //int dhr = 0;
        //int dmin = 0;
        //int lmin = 0;
        //int dfb = 0;
        int in_use = 0;
        //int groups = 0;
        int ind = 0;


        int players_per_group = 0;

        long mm = 0;
        long dd = 0;
        long yy = 0;
        long temp = 0;
        long date = 0;

        String pcw = "";

        //String dampm = "";
        String sdate = "";
        String stime = "";
        //String ltime = "";
        String sfb = "";
        //String dsfb = "";
        String notes = "";
        String hides = "";
        //String smins_before = "";              // 'minutes before' selected time
        //String smins_after = "";               // 'minutes after' selected time
        String shr = "";
        String smin = "";
        String ampm = "";
        //String in_use_by = "";
        String temps = "";
        String pname = "";
        String course_disp = "";
        
        boolean use_alt_time = false;

        boolean first_call = true;                   // default to first time thru - from Member_sheet
        
        boolean openPlayShotgun = false;

        boolean json_mode = (req.getParameter("json_mode") != null);

        Gson gson_obj = new Gson();

        //
        //  Get today's date 
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH) + 1;
        int thisDay = cal.get(Calendar.DAY_OF_MONTH);
        int thishr = cal.get(Calendar.HOUR_OF_DAY);
        int thismin = cal.get(Calendar.MINUTE);

        long today_date = (thisYear * 10000) + (thisMonth * 100) + thisDay;     // create a date field of yyyymmdd
        long shortDate = 0;

        int thisTime = (thishr * 100) + thismin;         // get current time (Central TIme!!)

        // Create fill slot page parameters we already know, fill in the rest later
        parmSlotPage slotPageParms = new parmSlotPage();
        slotPageParms.club = club;
        slotPageParms.club_name = clubName;
        slotPageParms.slot_url = "Member_slotm";
        slotPageParms.notice_message = "";
        slotPageParms.slot_help_url = "../member_help_slot_instruct.htm";
        slotPageParms.slot_type = "Tee Time";
        slotPageParms.member_tbd_text = "Member";
        slotPageParms.page_title = "Member Tee Time Request Page";
        slotPageParms.bread_crumb = "Tee Time Registration";
        slotPageParms.show_fb = true;
        slotPageParms.show_transport = true;
        slotPageParms.show_gift_pack = false;
        slotPageParms.show_fb = true;
        slotPageParms.show_transport = true;
        slotPageParms.show_tbd = true;
        slotPageParms.show_member_select = true;
        slotPageParms.show_guest_types = true;
        slotPageParms.user = user;
        slotPageParms.mship = slotParms.mship;
        slotPageParms.mtype = slotParms.mtype;
        slotPageParms.zip_code = (String) session.getAttribute("zipcode");

        // Store request parameters in our slotPageParms, in case we need them for call-back later
        // This will trigger a "uses unchecked or unsafe operations" warning while compiling.  
        // Perhaps there is a better way to do this, but for now it works.
        List<String> reqNames = (ArrayList<String>) Collections.list((Enumeration<String>) req.getParameterNames());
        for (String reqName : reqNames) {
            slotPageParms.callback_map.put(reqName, req.getParameter(reqName));
        }
        slotPageParms.callback_map.put("json_mode", "true");
        
        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(0, con);

        //
        //  parm block to hold the course parameters
        //
        parmCourse parmc = new parmCourse();          // allocate a parm block



        //
        // Get all the parameters entered
        //
        String day_name = req.getParameter("day");         //  name of the day
        String p5 = req.getParameter("p5");                //  5-somes supported

        String sslots = "";

        if (req.getParameter("contimes") != null) {        // if this is a new call from _sheet

            sslots = req.getParameter("contimes");          //  # of groups requested

        } else {                                           // call from self

            sslots = req.getParameter("slots");
        }

        //
        //  Convert the string values to ints
        //
        try {
            slots = Integer.parseInt(sslots);
        } catch (NumberFormatException e) {
            // ignore error
        }

        if (req.getParameter("fb") != null) {

            sfb = req.getParameter("fb");
        }

        if (req.getParameter("sdate") != null) {

            sdate = req.getParameter("sdate");
        }

        if (req.getParameter("date") != null) {          // if date was passed in date

            sdate = req.getParameter("date");
        }

        // Start configure block.  We will break out of this if we encounter an issue.  
        configure_slot:
        {

            if (req.getParameter("stime") != null) {         // if time was passed in stime

                stime = req.getParameter("stime");

            } else {                                           // call from Member_sheet

                if (req.getParameter("ttdata") != null) {

                    String tmp = Utilities.decryptTTdata(req.getParameter("ttdata"));

                    //out.println("<!-- " + req.getParameter("ttdata") + " = " + tmp + " -->");

                    StringTokenizer tok = new StringTokenizer(tmp, "|");     // separate name around the colon

                    stime = tok.nextToken();                          // shart hand time (9:35 AM)
                    sfb = tok.nextToken();                            // front/back indicator value
                    tmp = tok.nextToken();                            // username of member

                    if (!tmp.equals(user)) {

                        out.println("Stop hacking!");
                        return;

                    }

                } else {

                    out.println(SystemUtils.HeadTitle("DB Error"));
                    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
                    out.println("<BR><BR>Unable to process your request.");
                    out.println("<BR>Please try again later.");
                    out.println("<BR><BR>If problem persists, please contact customer support.");
                    out.println("<BR><BR>");
                    out.println("<a href=\"javascript:history.back(1)\">Return</a>");
                    out.println("</CENTER></BODY></HTML>");
                    out.close();
                    return;

                    /*
                    out.println("<P>THIS REQUEST WOULD FAIL</P>");
                    
                    //
                    //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
                    //
                    Enumeration enum1 = req.getParameterNames();     // get the parm name passed
                    
                    while (enum1.hasMoreElements()) {
                    
                    pname = (String) enum1.nextElement();             // get parm name
                    
                    if (pname.startsWith( "time" )) {
                    
                    stime = req.getParameter(pname);              //  value = time of tee time requested (hh:mm AM/PM)
                    
                    StringTokenizer tok = new StringTokenizer( pname, ":" );     // separate name around the colon
                    
                    sfb = tok.nextToken();                        // skip past 'time '
                    sfb = tok.nextToken();                        // get the front/back indicator value
                    }
                    }
                     */
                }
            }

            if (req.getParameter("returnCourse") != null) {        // if returnCourse provided

                returnCourse = req.getParameter("returnCourse");
            }

            //
            //  determine # of players allowed for this request
            //
            //   Players will = 4, 5, 8, 10, 12, 15, 16, 20 or 25 (based on 4 or 5-somes and number of slots/groups)
            //
            if (p5.equals("Yes")) {                  // 5-somes allowed ?

                p5 = "Yes";
                players = slots * 5;                              // Yes, set total # of players

            } else {

                p5 = "No";
                players = slots * 4;                              // No
            }

            //
            //  Convert the values from string to int
            //
            try {
                date = Long.parseLong(sdate);
                fb = Integer.parseInt(sfb);
            } catch (NumberFormatException e) {
                // ignore error
            }

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
            //  Determine 'ind' value in case we came from Member_teelist or _searchmem (ind = 888 or 999 or 995)
            //
            ind = getDaysBetween(date);            // get # of days in between today and the date of the tee time


            if ((req.getParameter("return") != null) || (req.getParameter("memNotice") != null)) {   // if this is a return from verify - time = hhmm

                try {
                    time = Integer.parseInt(stime);
                } catch (NumberFormatException e) {
                    // ignore error
                }

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

            } else {

                //
                //  Parse the time parm to separate hh, mm, am/pm and convert to military time
                //  (received as 'hh:mm xx'   where xx = am or pm)
                //
                StringTokenizer tok = new StringTokenizer(stime, ": ");     // space is the default token

                shr = tok.nextToken();
                smin = tok.nextToken();
                ampm = tok.nextToken();

                //
                //  Convert the values from string to int
                //
                try {
                    hr = Integer.parseInt(shr);
                    min = Integer.parseInt(smin);
                } catch (NumberFormatException e) {
                    // ignore error
                }

                if (ampm.equalsIgnoreCase("PM")) {

                    if (hr != 12) {

                        hr = hr + 12;
                    }
                }

                time = hr * 100;
                time = time + min;          // military time
            }


            //
            //     Check the club db table for X and guests
            //
            try {

                parm.club = club;                   // set club name
                parm.course = course;               // and course name

                getClub.getParms(con, parm);        // get the club parms

                x = parm.x;
            } catch (Exception exc) {             // SQL Error - ignore guest and x

                x = 0;
            }


            //
            //  if this is a call from self - user clicked on a letter, buddy list, or a return from verify
            //
            if ((req.getParameter("letter") != null) || (req.getParameter("buddy") != null) || (req.getParameter("return") != null)) {

                first_call = false;       // indicate NOT first call so we don't plug user's name into empty slot
            }


            //
            //  if this is a call from self (a return of some type)
            //
            if ((req.getParameter("letter") != null) || (req.getParameter("buddy") != null) || (req.getParameter("return") != null)
                    || (req.getParameter("promptLessTimes") != null) || (req.getParameter("memNotice") != null)) {

                slotParms.setTime(formUtil.getIntArrayFromReq(req, "time%", 15, 0));
                slotParms.time1 = time;
                slotParms.setPlayer(formUtil.getStringArrayFromReq(req, "player%", 25, ""));
                slotParms.setCw(formUtil.getStringArrayFromReq(req, "p%cw", 25, ""));
                slotParms.setP9(formUtil.getIntArrayFromReq(req, "p9%", 25, 0));
                slotParms.setGuestId(formUtil.getIntArrayFromReq(req, "guest_id%", 25, 0));

                int lastP9 = formUtil.findNullInReq(req, "p9%", 25);
                String p9s = "";
                if (lastP9 > 0) {
                    p9s = req.getParameter("p9" + lastP9);
                }

                if (req.getParameter("notes") != null) {
                    notes = req.getParameter("notes");
                    hides = req.getParameter("hide");

                    //
                    //  Convert hide from string to int
                    //
                    try {
                        hide = Integer.parseInt(hides);
                    } catch (NumberFormatException e) {
                        // ignore error
                    }
                }

                slotParms.day = day_name;            // save day name
                slotParms.slots = slots;             // save # of tee times requested
                slotParms.p5 = p5;
                slotParms.fb = fb;
                slotParms.sfb = sfb;
                slotParms.date = date;
                slotParms.mm = (int)mm;
                slotParms.dd = (int)dd;
                slotParms.yy = (int)yy;
                slotParms.returnCourse = returnCourse;
                
                // If we were able to get times successfully and it's a mass consecutive booking, break out into special processing
                if (slots > 5 || ((club.equals("desertmountain") || club.equals("elcaminocc") || club.equals("dmgcc")) && slots > 1)) {

                    if (Common_slot.verifyMassConsec(slotParms, user, user, con)) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "[options.notify.massConsecutiveTimeTitle]";
                        slotPageParms.page_start_notifications.add("[options.notify.massConsecutiveTimeSuccess]");
                        break configure_slot;

                    } else {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "[options.notify.massConsecutiveTimeTitle]";
                        slotPageParms.page_start_notifications.add("[options.notify.massConsecutiveTimeNotice]");
                        break configure_slot;
                    }
                }

            } else {        // not a letter request or return

                //
                // Check to see if user has origined too many tee times for this day
                //
                boolean error2 = false;
                if (parm.max_originations > 0 && slots <= 5) {
                    error2 = verifySlot.checkMaxOrigBy(user, date, parm.max_originations - (slots - 1), con);
                }

                if (error2 == true) {

                    if (new_skin) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "Max Allowed Round Originations Reached";
                        slotPageParms.page_start_notifications.add("Sorry, but you are allowed to create up to " + parm.max_originations + " tee times for any given day.");
                        slotPageParms.page_start_notifications.add("You may still be able to create additional tee times for other days.");
                        slotPageParms.page_start_notifications.add("This means you've either created your allowed " + parm.max_originations + " tee times for the elected day, or that by booking these " + slots + " rounds you would be over your limit.");
                        slotPageParms.page_start_notifications.add("Contact the Golf Shop if you have any questions.");
                        break configure_slot;

                    } else { // old skin
                        out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                        out.println("<BODY bgcolor=\"#CCCCAA\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><H3>Max Allowed Round Originations Reached</H3><BR>");
                        out.println("Sorry, but you are allowed only to create up to " + parm.max_originations + " ");
                        out.println("tee times <br>for any given day.&nbsp; ");
                        out.println("This means you've either created your allowed " + parm.max_originations + " tee times for the<br>");
                        out.println("selected day, or that by booking these " + slots + " rounds you would be over your limit.<br>");
                        out.println("You may still be able to create additional tee times for other days.");
                        out.println("<BR><BR>Contact the Golf Shop if you have any questions.<br>");
                        out.println("<BR><BR>");

                        out.println("<center>");
                        out.println("<form action=\"Member_slot\" method=\"post\" name=\"can\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                        out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                        out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                        out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
                        out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" name=\"cancel\">");

                        out.println("</form></center>");

                        return;
                    }
                }

                //
                //  Get the players' names and check if any of the tee slots are already in use
                //
                //  NOTE: All tee times requested MUST be empty and NOT in use at this time.
                //
                slotParms.day = day_name;            // save day name
                slotParms.slots = slots;             // save # of tee times requested
                slotParms.p5 = p5;
                slotParms.fb = fb;
                slotParms.sfb = sfb;
                slotParms.date = date;
                slotParms.mm = (int)mm;
                slotParms.dd = (int)dd;
                slotParms.yy = (int)yy;
                slotParms.returnCourse = returnCourse;
                
                shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)

                //
                //  Verify the required parms exist
                //
                if (date == 0 || time == 0 || course == null || user.equals("") || user == null) {

                    //
                    //  save message in /" +rev+ "/error.txt
                    //
                    String msg = "Error in Member_slotm - checkInUseMn Parms - for user " + user + " at " + club + ".  Date= " + date + ", time= " + time + ", course= " + course + ", fb= " + fb;   // build msg
                    SystemUtils.logError(msg);                                   // log it

                    in_use = 1;          // make like the time is busy

                } else {               // continue if parms ok
                    
                    //
                    //  custom to catch cheaters - log them off if cheating
                    //
                    boolean acError = false;
                    int daysInAdv = 0;
                    int advTime = 0;

                    if (json_mode == false) {      // skip this if 2nd time thru here

                        if (club.equals("eaglecreek")) {

                            verifySlot.getDaysInAdv(con, parm, slotParms.mship);   // get days in adv and time parms for this user

                            //
                            //   Get the days in advance and time of day values for the day of this tee time
                            //
                            if (slotParms.day.equals("Sunday")) {

                                daysInAdv = parm.advdays1;
                                advTime = parm.advtime1;

                            } else if (slotParms.day.equals("Monday")) {

                                daysInAdv = parm.advdays2;
                                advTime = parm.advtime2;

                            } else if (slotParms.day.equals("Tuesday")) {

                                daysInAdv = parm.advdays3;
                                advTime = parm.advtime3;

                            } else if (slotParms.day.equals("Wednesday")) {

                                daysInAdv = parm.advdays4;
                                advTime = parm.advtime4;

                            } else if (slotParms.day.equals("Thursday")) {

                                daysInAdv = parm.advdays5;
                                advTime = parm.advtime5;

                            } else if (slotParms.day.equals("Friday")) {

                                daysInAdv = parm.advdays6;
                                advTime = parm.advtime6;

                            } else {

                                daysInAdv = parm.advdays7;
                                advTime = parm.advtime7;
                            }
                        }                         // end of custom


                        if (club.equals("admiralscove")) {

                            acError = checkACearly(user, ind, slots);   // check days in adv, time and slots requested

                            if (acError == true) {

                                logoffAC(out, session, con);     // force logoff and exit
                                return;
                            }
                        }                         // end of custom

                        if (club.equals("eaglecreek")) {

                            acError = verifyCustom.checkECearly(user, ind, slots, daysInAdv, advTime, con);   // check days in adv, time and slots requested

                            if (acError == true) {

                                logoffAC(out, session, con);     // force logoff and exit
                                return;
                            }
                        }                         // end of custom
                    }             // end of IF json mode


                    try {
                        
                        in_use = 0;           // init in use flag
                        
                        if (json_mode == false) {      // skip if 2nd time thru here


                            //
                            //  Set tee times busy if not already, and if not during an event or blocker!!!!!!!!
                            //
                            boolean consecutive = true;         // return consecutive tee times only

                            if (club.equals("demobradx") || club.equals("ccjackson") || club.equals("longcove") || club.equals("pelicansnest")
                                    || club.equals("eaglecreek") || club.equals("imperialgc") || club.equals("plantationpv")
                                    || club.equals("lakewoodranch") || club.equals("theoaksclub") || club.equals("wildcatruncc")) {

                                consecutive = false;         // consecutive tee times not necessary
                            }

                            // Check to debug double bookings
                            // if ((time >= 558 && time <= 1010) || club.startsWith("demo")) {
                            //    Utilities.logDebug("BSK", "DBC - Log slotm entry (" + Utilities.get_ms_timestamp(con) + ") - " + club + " - user: " + user + ", slots: " + slots + ", date: " + date + ", time: " + time + ", fb: " + fb + ", course: " + course);
                            // }

                            
                            //
                            //  Custom for Open Play Shotguns - simple mode
                            //
                            String shotgunEvent = "";
                            openPlayShotgun = false;

                            if (club.equals("demov4") || club.equals("mosscreek")) {              // check if Open Play Shotgun event during this time
                                
                                shotgunEvent = checkShotgunEvent(date, time, fb, course, con);
                                consecutive = true;
                            }                
                                              
                            if (!shotgunEvent.equals("")) {        // if Open Play Shotgun Event times requested

                                openPlayShotgun = true;
                                                                
                                in_use = verifySlot.checkInUseMOPS(consecutive, shotgunEvent, date, time, fb, course, user, slotParms, con, session);   // custom check
                                
                                if (in_use == 0) {                          // if we got our times 
                                    
                                    //time = slotParms.time1;                 // the first time may have changed
                                    time = Utilities.getEventTime(shotgunEvent, con);   // get the actual time of the shotgun
                                    use_alt_time = true;
                                    
                                    stime = timeUtil.get12HourTime(time);   // convert to string (i.e.  8:00 AM)
                                }
                                
                            } else {
                            
                                //*****************************************************************************
                                //   Check if tee times are available and set them in use if they are
                                //*****************************************************************************
                                //
                                in_use = verifySlot.checkInUseMn(consecutive, date, time, fb, course, user, slotParms, con, session);   // custom check
                            }


                            //
                            //  temp to catch cheaters - make history entry if 7 days in adv and before 7:05 AM ET
                            //
                            if ((club.equals("pelicansnest") && ind == 7 && thisTime < 605)
                                    || (club.equals("eaglecreek") && daysInAdv > 0 && ind == daysInAdv && thisTime < 605)
                                    || (club.equals("ccnaples") && ind == 7 && thisTime < 603)
                                    || (club.equals("admiralscove") && ind == 3 && thisTime < 635)) {       // temp custom to catch cheaters !!!!!!!!!!!!!

                                //  make history entry to track the time entered
                                SystemUtils.updateHist(date, slotParms.day, time, fb, course, slotParms.player1, slotParms.player2, slotParms.player3,
                                        slotParms.player4, slotParms.player5, user, name, 0, con);
                            }   // end of custom


                            //
                            //  If we did not get the exact tee times requested, then ask the user if they want to proceed or go back.
                            //
                            if (in_use == 9) {                     // if found, but different than requested
                                promptLessTimes(new_skin, out, slotParms, slotPageParms);    // send prompt
                                if (new_skin) {
                                    time = slotParms.time1;
                                    fb = slotParms.fb;
                                    break configure_slot;
                                } else {
                                    return;                             // exit and wait for answer
                                }
                            }
                            
                        } else {       // json mode (2nd pass) - make sure we still own the tee times and get all the times in slotParms (needed if there is a member notice)
                            
                            in_use = verifySlot.checkInUseSession(user, slotParms, con, session); 

                        }                 // end of IF json mode
                        

                        // If we were able to get times successfully and it's a mass consecutive booking, break out into special processing
                        if (in_use == 0 && (slots > 5 || ((club.equals("desertmountain") || club.equals("elcaminocc") || club.equals("dmgcc")) && slots > 1))) {

                            if (Common_slot.verifyMassConsec(slotParms, user, user, con)) {

                                slotPageParms.page_start_button_go_back = true;
                                slotPageParms.page_start_title = "[options.notify.massConsecutiveTimeTitle]";
                                slotPageParms.page_start_notifications.add("[options.notify.massConsecutiveTimeSuccess]");
                                break configure_slot;

                            } else {

                                slotPageParms.page_start_button_go_back = true;
                                slotPageParms.page_start_title = "[options.notify.massConsecutiveTimeTitle]";
                                slotPageParms.page_start_notifications.add("[options.notify.massConsecutiveTimeNotice]");
                                break configure_slot;
                            }
                        }

                    } catch (Exception e1) {

                        String msg = "Member_slotm Check in use flag failed - Exception: " + e1.getMessage();

                        SystemUtils.logError(msg);                                   // log it
                        //out.println(e1);

                        in_use = 1;          // make like the time is busy
                    }
             
                }            // end of IF parms ok

                if (new_skin && in_use > 0) {

                    slotPageParms.page_start_button_go_back = true;

                    slotPageParms.page_start_title = "[options.notify.slotBusyTitle]";
                    slotPageParms.page_start_notifications.add("[options.notify.slotBusyNotice]");

                    break configure_slot;

                } else { // Old skin

                    if (in_use == 1) {              // if time slot already in use

                        if (!returnCourse.equals("")) {    // if multi course club, get course to return to (ALL?)
                            course = returnCourse;
                        }
                        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                        out.println("<CENTER><BR><BR><H3>Tee Time Slot Busy</H3>");
                        out.println("<BR><BR>Sorry, but at least one of the requested tee times are currently busy.");
                        out.println("<BR><BR>All " + slots + " tee times must be on the same nine and completely unoccupied.");
                        out.println("<BR><BR>Please select another time or try again later.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");
                        out.println("</CENTER></BODY></HTML>");
                        out.close();
                        return;
                    }

                    if (in_use == 2) {              // if one or more tee times during an event

                        if (!returnCourse.equals("")) {    // if multi course club, get course to return to (ALL?)
                            course = returnCourse;
                        }
                        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                        out.println("<CENTER><BR><BR><H3>Tee Time Slot Busy</H3>");
                        out.println("<BR><BR>Sorry, but at least one of the requested tee times are reserved for an event.");
                        out.println("<BR><BR>Please select another time or try again later.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");
                        out.println("</CENTER></BODY></HTML>");
                        out.close();
                        return;
                    }

                    if (in_use == 3) {              // if one or more tee times during a blocker

                        if (!returnCourse.equals("")) {    // if multi course club, get course to return to (ALL?)
                            course = returnCourse;
                        }
                        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                        out.println("<CENTER><BR><BR><H3>Tee Time Slot Busy</H3>");
                        out.println("<BR><BR>Sorry, but at least one of the requested tee times has been blocked by the Golf Shop.");
                        out.println("<BR><BR>Please select another time or try again later.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");
                        out.println("</CENTER></BODY></HTML>");
                        out.close();
                        return;
                    }

                    if (in_use == 4) {              // db error - most likely not enough tee times left in the day

                        if (!returnCourse.equals("")) {    // if multi course club, get course to return to (ALL?)
                            course = returnCourse;
                        }
                        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                        out.println("<CENTER><BR><BR><H3>Tee Time Slots Not Available</H3>");
                        out.println("<BR><BR>Sorry, but at least one of the requested tee times are not currently available.");
                        out.println("<BR>It could be that you are requesting too many times for this time of the day.");
                        out.println("<BR><BR>Please select another time or try again later.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");
                        out.println("</CENTER></BODY></HTML>");
                        out.close();
                        return;
                    }
                }
            }
            
            // max originations check was here (moved to above)

            //
            //**********************************************
            //   Check for Member Notice from Pro
            //**********************************************
            //
            String memNotice = verifySlot.checkMemNotice(date, time, fb, course, day_name, "teetime", false, con);

            if (!memNotice.equals("") && (req.getParameter("skip_member_notice") == null)) {      // if message to display

                //
                //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
                //
                if (new_skin) {

                    slotPageParms.page_start_button_go_back = true;
                    slotPageParms.page_start_button_accept = true;
                    slotPageParms.page_start_title = "[options.notify.noticeFromGolfShopTitle]";
                    slotPageParms.page_start_notifications.add(memNotice);
                    slotPageParms.page_start_notifications.add("[options.notify.continueWithRequestPrompt]");
                    slotPageParms.callback_map.put("skip_member_notice", "yes");
                    break configure_slot;

                } else {
                    out.println("<HTML><HEAD>");
                    out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
                    out.println("<Title>Member Notice For Tee Time Request</Title>");
                    out.println("</HEAD>");

                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

                    out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
                    out.println("<tr><td valign=\"top\" align=\"center\">");
                    out.println("<p>&nbsp;&nbsp;</p>");
                    out.println("<p>&nbsp;&nbsp;</p>");
                    out.println("<font size=\"3\">");
                    out.println("<b>NOTICE FROM YOUR GOLF SHOP</b><br><br><br></font>");

                    out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
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
                    out.println("<form action=\"Member_slotm\" method=\"post\" name=\"can\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                    out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                    out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                    out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
                    out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
                    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                    out.println("<input type=\"hidden\" name=\"time2\" value=\"" + slotParms.time2 + "\">");
                    out.println("<input type=\"hidden\" name=\"time3\" value=\"" + slotParms.time3 + "\">");
                    out.println("<input type=\"hidden\" name=\"time4\" value=\"" + slotParms.time4 + "\">");
                    out.println("<input type=\"hidden\" name=\"time5\" value=\"" + slotParms.time5 + "\">");
                    out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                    out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\"></form>");

                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font size=\"2\">");
                    out.println("<form action=\"Member_slotm\" method=\"post\">");
                    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                    out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                    out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                    out.println("<input type=\"hidden\" name=\"time2\" value=\"" + slotParms.time2 + "\">");
                    out.println("<input type=\"hidden\" name=\"time3\" value=\"" + slotParms.time3 + "\">");
                    out.println("<input type=\"hidden\" name=\"time4\" value=\"" + slotParms.time4 + "\">");
                    out.println("<input type=\"hidden\" name=\"time5\" value=\"" + slotParms.time5 + "\">");
                    out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                    out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                    out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                    out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                    out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                    out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                    out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                    out.println("<input type=\"hidden\" name=\"player6\" value=\"" + slotParms.player6 + "\">");
                    out.println("<input type=\"hidden\" name=\"player7\" value=\"" + slotParms.player7 + "\">");
                    out.println("<input type=\"hidden\" name=\"player8\" value=\"" + slotParms.player8 + "\">");
                    out.println("<input type=\"hidden\" name=\"player9\" value=\"" + slotParms.player9 + "\">");
                    out.println("<input type=\"hidden\" name=\"player10\" value=\"" + slotParms.player10 + "\">");
                    out.println("<input type=\"hidden\" name=\"player11\" value=\"" + slotParms.player11 + "\">");
                    out.println("<input type=\"hidden\" name=\"player12\" value=\"" + slotParms.player12 + "\">");
                    out.println("<input type=\"hidden\" name=\"player13\" value=\"" + slotParms.player13 + "\">");
                    out.println("<input type=\"hidden\" name=\"player14\" value=\"" + slotParms.player14 + "\">");
                    out.println("<input type=\"hidden\" name=\"player15\" value=\"" + slotParms.player15 + "\">");
                    out.println("<input type=\"hidden\" name=\"player16\" value=\"" + slotParms.player16 + "\">");
                    out.println("<input type=\"hidden\" name=\"player17\" value=\"" + slotParms.player17 + "\">");
                    out.println("<input type=\"hidden\" name=\"player18\" value=\"" + slotParms.player18 + "\">");
                    out.println("<input type=\"hidden\" name=\"player19\" value=\"" + slotParms.player19 + "\">");
                    out.println("<input type=\"hidden\" name=\"player20\" value=\"" + slotParms.player20 + "\">");
                    out.println("<input type=\"hidden\" name=\"player21\" value=\"" + slotParms.player21 + "\">");
                    out.println("<input type=\"hidden\" name=\"player22\" value=\"" + slotParms.player22 + "\">");
                    out.println("<input type=\"hidden\" name=\"player23\" value=\"" + slotParms.player23 + "\">");
                    out.println("<input type=\"hidden\" name=\"player24\" value=\"" + slotParms.player24 + "\">");
                    out.println("<input type=\"hidden\" name=\"player25\" value=\"" + slotParms.player25 + "\">");
                    out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.pcw1 + "\">");
                    out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.pcw2 + "\">");
                    out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.pcw3 + "\">");
                    out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.pcw4 + "\">");
                    out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.pcw5 + "\">");
                    out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + slotParms.pcw6 + "\">");
                    out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + slotParms.pcw7 + "\">");
                    out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + slotParms.pcw8 + "\">");
                    out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + slotParms.pcw9 + "\">");
                    out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + slotParms.pcw10 + "\">");
                    out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + slotParms.pcw11 + "\">");
                    out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + slotParms.pcw12 + "\">");
                    out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + slotParms.pcw13 + "\">");
                    out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + slotParms.pcw14 + "\">");
                    out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + slotParms.pcw15 + "\">");
                    out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + slotParms.pcw16 + "\">");
                    out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + slotParms.pcw17 + "\">");
                    out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + slotParms.pcw18 + "\">");
                    out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + slotParms.pcw19 + "\">");
                    out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + slotParms.pcw20 + "\">");
                    out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + slotParms.pcw21 + "\">");
                    out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + slotParms.pcw22 + "\">");
                    out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + slotParms.pcw23 + "\">");
                    out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + slotParms.pcw24 + "\">");
                    out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + slotParms.pcw25 + "\">");
                    out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                    out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                    out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                    out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                    out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                    out.println("<input type=\"hidden\" name=\"p96\" value=\"" + slotParms.p96 + "\">");
                    out.println("<input type=\"hidden\" name=\"p97\" value=\"" + slotParms.p97 + "\">");
                    out.println("<input type=\"hidden\" name=\"p98\" value=\"" + slotParms.p98 + "\">");
                    out.println("<input type=\"hidden\" name=\"p99\" value=\"" + slotParms.p99 + "\">");
                    out.println("<input type=\"hidden\" name=\"p910\" value=\"" + slotParms.p910 + "\">");
                    out.println("<input type=\"hidden\" name=\"p911\" value=\"" + slotParms.p911 + "\">");
                    out.println("<input type=\"hidden\" name=\"p912\" value=\"" + slotParms.p912 + "\">");
                    out.println("<input type=\"hidden\" name=\"p913\" value=\"" + slotParms.p913 + "\">");
                    out.println("<input type=\"hidden\" name=\"p914\" value=\"" + slotParms.p914 + "\">");
                    out.println("<input type=\"hidden\" name=\"p915\" value=\"" + slotParms.p915 + "\">");
                    out.println("<input type=\"hidden\" name=\"p916\" value=\"" + slotParms.p916 + "\">");
                    out.println("<input type=\"hidden\" name=\"p917\" value=\"" + slotParms.p917 + "\">");
                    out.println("<input type=\"hidden\" name=\"p918\" value=\"" + slotParms.p918 + "\">");
                    out.println("<input type=\"hidden\" name=\"p919\" value=\"" + slotParms.p919 + "\">");
                    out.println("<input type=\"hidden\" name=\"p920\" value=\"" + slotParms.p920 + "\">");
                    out.println("<input type=\"hidden\" name=\"p921\" value=\"" + slotParms.p921 + "\">");
                    out.println("<input type=\"hidden\" name=\"p922\" value=\"" + slotParms.p922 + "\">");
                    out.println("<input type=\"hidden\" name=\"p923\" value=\"" + slotParms.p923 + "\">");
                    out.println("<input type=\"hidden\" name=\"p924\" value=\"" + slotParms.p924 + "\">");
                    out.println("<input type=\"hidden\" name=\"p925\" value=\"" + slotParms.p925 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + slotParms.guest_id6 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + slotParms.guest_id7 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + slotParms.guest_id8 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + slotParms.guest_id9 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + slotParms.guest_id10 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + slotParms.guest_id11 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + slotParms.guest_id12 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + slotParms.guest_id13 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + slotParms.guest_id14 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + slotParms.guest_id15 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + slotParms.guest_id16 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + slotParms.guest_id17 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + slotParms.guest_id18 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + slotParms.guest_id19 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + slotParms.guest_id20 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + slotParms.guest_id21 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + slotParms.guest_id22 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + slotParms.guest_id23 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + slotParms.guest_id24 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + slotParms.guest_id25 + "\">");
                    out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                    out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                    out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"skip_member_notice\" value=\"yes\">");
                    out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
                    out.println("</font></td></tr>");
                    out.println("</table>");

                    out.println("</td>");
                    out.println("</tr>");
                    out.println("</table>");
                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }


            }              // end of 'letter' if

            //
            //  get user's walk/cart preference
            //
            try {

                PreparedStatement pstmt2 = con.prepareStatement(
                        "SELECT wc FROM member2b WHERE username = ?");

                pstmt2.clearParameters();        // clear the parms
                pstmt2.setString(1, user);       // put the parm in pstmt2
                rs = pstmt2.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

                    pcw = rs.getString(1);        // user's walk/cart pref
                }
                pstmt2.close();              // close the stmt

            } catch (Exception e9) {

                dbError(out, e9);
                return;
            }

            //
            //  Get the walk/cart options available
            //
            try {

                getParms.getCourse(con, parmc, course);
            } catch (Exception e1) {

                dbError(out, e1);
                return;
            }

            /*
            //
            //  If Jonathan's Landing then remove certain options  (Case# 1330)
            //
            if (club.equals("jonathanslanding")) {
            
            for (i = 0; i < parmc.tmode_limit; i++) {
            
            if (parmc.tmodea[i].equalsIgnoreCase( "TF" ) || parmc.tmodea[i].equalsIgnoreCase( "ACF" ) || parmc.tmodea[i].equalsIgnoreCase( "WLK" )) {
            
            parmc.tmodea[i] = "";      // remove it
            }
            }
            }
             */

            // Set players per group
            players_per_group = (p5.equals("Yes") ? 5 : 4);

            //
            //  if Piedmont Driving Club, remove 2 trans modes that are for events only
            //
            if (club.equals("piedmont")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("cfc") || parmc.tmodea[i].equalsIgnoreCase("wwc")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }


            //
            //  if Lake Forest CC and a weekend and before 10:01, remove trans mode of 'Walk' (WLK)
            //
            if (club.equals("lakeforestcc")) {

                if ((day_name.equals("Saturday") || day_name.equals("Sunday")) && time < 1001) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("WLK") || parmc.tmodea[i].equalsIgnoreCase("P/C")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            //
            //  if Olympic Club, remove trans mode of 'Caddie' (CAD)
            //
            if (club.equals("olyclub")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("CAD")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

            //
            //  if Imperial Golf Club and before 11:30 on any day, remove trans mode of Walk & Pull Cart  (Case #1287)
            //
            if (club.equals("imperialgc")) {

                // long mmdd = (mm*100) + dd;

                // if (mmdd >= 101 && mmdd <= 531 && time < 1130) {

                if (date <= 20100531 && time < 1130) {         // this custom goes away after 5/31/2010

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("WLK") || parmc.tmodea[i].equalsIgnoreCase("PC")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            /*  ***removed at request of club***
            //
            //  If Mediterra remove the trans mode of 'Walking' (Case 1263)
            //
            if (club.equals( "mediterra" )) {
            
            for (i = 0; i < parmc.tmode_limit; i++) {
            
            if (parmc.tmodea[i].equalsIgnoreCase( "w" )) {
            
            parmc.tmodea[i] = "";      // remove it
            }
            }
            }
             */

            //
            //  If Sonnenalp remove the trans mode of 'ICP' and 'FCP' (Case 1452)
            //
            if (club.equals("sonnenalp")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("ICP") || parmc.tmodea[i].equalsIgnoreCase("FCP")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

            //
            //  If Berkeley Hall remove the trans mode of 'REC' and 'CMP' (Case 1341)
            //
            if (club.equals("berkeleyhall")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("REC") || parmc.tmodea[i].equalsIgnoreCase("CMP")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }
            
            //  If Wisconsin Club (wisconsinclub), remove all mode of trans aside from 'GC', 'CAD', 'C/C', 'FC' on Sat between 7-9am from 5/1-9/30
            if (club.equals("wisconsinclub")) {
                
                if (shortDate >= 501 && shortDate <= 930 && time >= 700 && time < 900 && day_name.equals("Saturday")) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (!parmc.tmodea[i].equalsIgnoreCase("GC") && !parmc.tmodea[i].equalsIgnoreCase("CAD") && !parmc.tmodea[i].equalsIgnoreCase("C/C") && !parmc.tmodea[i].equalsIgnoreCase("FC")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }


            //
            //  If Minikahda remove all mode of trans aside from 'CRT', 'CAD', 'C/C' on weekends and holidays and some Fridays
            //
            if (club.equals("minikahda")) {
                
                if ((((shortDate >= 501 && shortDate <= 930 && (day_name.equals("Saturday") || day_name.equals("Sunday"))) || (date == Hdate1 || date == Hdate2 || date == Hdate3))
                        && time < 1200) || (shortDate >= 524 && shortDate <= 830 && day_name.equals("Friday") && time >= 1000 && time < 1400)) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (!parmc.tmodea[i].equalsIgnoreCase("CRT") && !parmc.tmodea[i].equalsIgnoreCase("CAD") && !parmc.tmodea[i].equalsIgnoreCase("C/C")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }
            
            // If Lakewood Ranch, don't display 'WLK' tmode in tee times prior to 1:00pm.
            if (club.equals("lakewoodranch")) {
                
                if (time < 1300) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (parmc.tmodea[i].equalsIgnoreCase("WLK")) {

                            parmc.tmodea[i] = "";      // remove it
                            break;
                        }
                    }
                }
            }

            
            //
            //  Make sure the user's c/w option is still supported (pro may have changed config)
            //
            if (!pcw.equals("") && !club.equals("sonnenalp")) {     // do not check Sonnenalp - must allow ICP and FCP for some members

                i = 0;
                loopi1:
                while (i < parmc.tmode_limit) {

                    if (parmc.tmodea[i].equals(pcw)) {

                        break loopi1;
                    }
                    i++;
                }
                if (i > parmc.tmode_limit - 1) {       // if we went all the way without a match

                    pcw = "";        // force user to specify one
                }
            }
            i = 0;

            //
            //  If selected course only has one mode of trans, then make that the default
            //
            if (!club.equals("sonnenalp")) {    // skip this for Sonnenalp so ICP and FCP are allowed for some members

                if (!parmc.tmodea[0].equals("") && parmc.tmodea[1].equals("")) {     // if only one tmode specified

                    pcw = parmc.tmodea[0];         // use that as default
                }
            }


            //
            //  Set user's name as first open player to be placed in name slot for them
            //
            //  First, check if first time here and user is not already included in this slot.
            //  Member_sheet already checked if slot is full and user not one of them!!
            //
            if (first_call == true && !slotParms.player1.equals(name) && !slotParms.player2.equals(name) && !slotParms.player3.equals(name) && !slotParms.player4.equals(name) && !slotParms.player5.equals(name)) {
                for (int i2 = 0; i2 < players_per_group; i2++) {
                    if (slotParms.getPlayer(i2).equals("")) {
                        slotParms.setPlayer(name, i2);
                        slotParms.setUser(user, i2);
                        slotParms.setCw(pcw, i2);
                        break;
                    }
                }
            }


            //
            //  St Clair CC - Terrace course is always 9 holes
            //
            if ((club.equals("stclaircc") && course.equals("Terrace"))
                    || (club.equals("huntingdonvalleycc") && course.equals("Centennial Nine"))) {

                slotParms.setP9(formUtil.getIntArrayFromReq(req, "_nonexistant_parameter_", 25, 1));

            }

        } // end of configure_slot
        
        // Check to debug double bookings
        /*
        if (json_mode == false) {      // skip if 2nd time thru here
            if ((time >= 558 && time <= 1010) || club.startsWith("demo")) {
                Utilities.logDebug("BSK", "DBC - Times Confirmed (" + Utilities.get_ms_timestamp(con) + ") - " + club + " - user: " + user + ", slots: " + slots + ", date: " + date + ", fb: " + fb + ", course: " + course 
                        + ", time1: " + slotParms.time1 + ", time2: " + slotParms.time2 + ", time3: " + slotParms.time3 + ", time4: " + slotParms.time4 + ", time5: " + slotParms.time5);
            }
        }
        */

        // Fill parameters for slot page

        // Set default course name
        if (club.equals("congressional")) {
            course_disp = congressionalCustom.getFullCourseName(date, (int) dd, course);
        } else {
            course_disp = course;
        }
        
        slotPageParms.time_a = slotParms.getTimeArray(15);         // get all the tee times
        
        slotPageParms.group_titles = new ArrayList<String>();
        for(int i2 = 0; i2 < slots; i2++){
            if(use_alt_time){
                slotPageParms.group_titles.add(stime + " Shotgun");
            } else {
                slotPageParms.group_titles.add(timeUtil.get12HourTime(slotPageParms.time_a[i2]));
            }
        }

        if(use_alt_time){
            slotPageParms.time_remaining = verifySlot.getInUseTimeRemaining(date, slotParms.getTime(0), fb, course, session);
        } else {
            slotPageParms.time_remaining = verifySlot.getInUseTimeRemaining(date, time, fb, course, session);
        }
        
        slotPageParms.hide_notes = hide;
        slotPageParms.show_member_tbd = (x != 0);

        slotPageParms.player_count = players;
        slotPageParms.players_per_group = players_per_group;
        slotPageParms.jump = jump;
        slotPageParms.index = index;
        //slotPageParms.day_name = day_name;
        slotPageParms.time = time;
        slotPageParms.fb = fb;
        slotPageParms.slots = slots;
        slotPageParms.date = (int) date;
        slotPageParms.yy = (int) yy;
        slotPageParms.mm = (int) mm;
        slotPageParms.dd = (int) dd;

        slotPageParms.course = course;
        slotPageParms.return_course = returnCourse;
        slotPageParms.day = day_name;
        slotPageParms.stime = stime;
        slotPageParms.course_disp = course_disp;
        slotPageParms.sdate = sdate;
        slotPageParms.notice_message = "";
        //slotPageParms.transport_legend = transport_legend;
        slotPageParms.p5 = p5;
        slotPageParms.notes = notes;
        slotPageParms.name = name;


        slotPageParms.pcw = pcw; // User's default PCW

        slotPageParms.guest_id_a = slotParms.getGuestIdArray(25);
        slotPageParms.p9_a = slotParms.getP9Array(25);
        

        slotPageParms.player_a = slotParms.getPlayerArray(25);  // get all the players
        slotPageParms.user_a = slotParms.getUserArray(25);  // get all the players
        slotPageParms.pcw_a = slotParms.getCwArray(25);

        slotPageParms.allowed_tmodes_list = Arrays.asList(parmc.tmodea);  // Tmode that will be allowed when set as defaults

        slotPageParms.course_parms = parmc;

        // Set tranport types
        Common_slot.setDefaultTransportTypes(slotPageParms);
        // Set transport legend
        Common_slot.setTransportLegend(slotPageParms, parmc, new_skin);
        // Set transport modes
        Common_slot.setTransportModes(slotPageParms, parmc);
        // Set guest types
        Common_slot.setGuestTypes(con, slotPageParms, parm, slotParms);
        
        // Define the fields we will include when submitting the form
        slotPageParms.slot_submit_map.put("date", "date");
        slotPageParms.slot_submit_map.put("sdate", "sdate");
        slotPageParms.slot_submit_map.put("day", "day");
        slotPageParms.slot_submit_map.put("stime", "stime");
        slotPageParms.slot_submit_map.put("time", "time");
        slotPageParms.slot_submit_map.put("time%", "time_a");
        slotPageParms.slot_submit_map.put("fb", "fb");
        slotPageParms.slot_submit_map.put("mm", "mm");
        slotPageParms.slot_submit_map.put("yy", "yy");
        slotPageParms.slot_submit_map.put("dd", "dd");
        slotPageParms.slot_submit_map.put("index", "index");
        slotPageParms.slot_submit_map.put("course", "course");
        slotPageParms.slot_submit_map.put("returnCourse", "return_course");
        slotPageParms.slot_submit_map.put("p5", "p5");
        slotPageParms.slot_submit_map.put("jump", "jump");
        slotPageParms.slot_submit_map.put("slots", "slots");
        slotPageParms.slot_submit_map.put("hide", "hide_notes");
        slotPageParms.slot_submit_map.put("notes", "notes");
        slotPageParms.slot_submit_map.put("player%", "player_a");
        slotPageParms.slot_submit_map.put("p9%", "p9_a");
        slotPageParms.slot_submit_map.put("p%cw", "pcw_a");
        slotPageParms.slot_submit_map.put("guest_id%", "guest_id_a");

        if (new_skin) {

            /**************************************
             * New Skin Output
             **************************************/
            if (json_mode) {
                out.print(Common_slot.slotJson(slotPageParms));
            } else {
                Common_slot.displaySlotPage(out, slotPageParms, req, con);
            }



        } else { // end of new-skin

            /**************************************
             * Old Skin Output
             **************************************/
            //
            //  Build the HTML page to prompt user for names
            //
            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Member Tee Time Request Page</Title>");

            // Json compatibility for older browsers
            out.println("<script type=\"text/javascript\" src=\"/" + rev + "/assets/scripts/json2.js\"></script>");
            //  Add script code to allow modal windows to be used
            out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->"
                    + "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>"
                    + "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>"
                    + "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

            out.println("<script type=\"text/javascript\">");
            out.println("<!--");
            out.println("function resizeIFrame(divHeight, iframeName) {");
            out.println("document.getElementById(iframeName).height = divHeight;");
            out.println("}");
            out.println("// -->");
            out.println("</script>");
            //
            //*******************************************************************
            //  User clicked on a letter - submit the form for the letter
            //*******************************************************************
            //
            out.println("<script type=\"text/javascript\">");            // Submit the form when clicking on a letter
            out.println("<!--");
            out.println("function subletter(x) {");

//      out.println("alert(x);");
            out.println("document.playerform.letter.value = x;");         // put the letter in the parm
            out.println("playerform.submit();");        // submit the form
            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");          // End of script

            //
            //*******************************************************************
            //  Erase player name (erase button selected next to player's name)
            //
            //    Remove the player's name and shift any other names up starting at player1
            //*******************************************************************
            //
            out.println("<script type=\"text/javascript\">");            // Erase name script    (Note:  Put these in file???)  what other files use these scripts, just proshop_slot?
            out.println("<!--");

            out.println("function erasename(pPlayerPos, pCWoption) {");
            out.println("var p = eval(\"document.forms['playerform'].\" + pPlayerPos + \";\")");
            out.println("var o = eval(\"document.forms['playerform'].\" + pCWoption + \";\")");
            out.println("p.value = '';");        // clear player field

            out.println("var pPlayerPos2 = pPlayerPos.replace('player', 'guest_id');");
            out.println("document.playerform[pPlayerPos2].value = '0';");           // clear the guest_id field

            // remove any Pro-Only tmodes from the wc field
            out.println("var m=0");
            out.println("var n=0");
            out.println("var found = new Boolean(false)");
            out.println("for (m = o.length - 1; m>=0; m--) {");
            out.println("found = false;");
            out.println("for (n=0; n<nonProCount; n++) {");
            out.println("if (o.options[m].value == nonProTmodes[n]) {");
            out.println("found = true;");
            out.println("break;");
            out.println("}");        // end of if
            out.println("}");        // end inner for
            out.println("if (found == false) {");
            out.println("o.options[m] = null;");
            out.println("}");        // end if
            out.println("}");        // end for

            out.println("o.selectedIndex = -1;");        // clear WC field
            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");          // End of script

            //
            //**********************************************************************************
            //  Add to drop down list - add options to drop down lists
            //**********************************************************************************
            //
            // add option if not already in list (means it was member's default)
            out.println("<script type=\"text/javascript\">");
            out.println("<!-- ");
            out.println("function add(e, wc) {");
            out.println("  var i=0;");
            out.println("  for (i=0;i<e.length;i++) {");
            out.println("    if (e.options[i].value == wc) {");
            out.println("      return;");
            out.println("    }");        // end if
            out.println("  }");      // end for
            out.println("  for (i=0;i<tmodeCount;i++) {");
            out.println("    if (tmodes[i] == wc) {");
            out.println("      e.options[e.length] = new Option(wc, wc);");
            out.println("    }");
            out.println("  }");
            out.println("}");        // End of function add()
            out.println("// -->");
            out.println("</script>");    // End of script

            //
            //*******************************************************************
            //  Erase text area - (Notes)
            //*******************************************************************
            //
            out.println("<script type=\"text/javascript\">");            // Erase text area script
            out.println("<!--");
            out.println("function erasetext(pos1) {");
            out.println(" eval(\"document.forms['playerform'].\" + pos1 + \".value = '';\")");           // clear the player field
            out.println("}");
            out.println("function movenotes() {");
            out.println(" var oldnotes = document.forms['playerform'].oldnotes.value;");
            out.println(" document.forms['playerform'].notes.value = oldnotes;");   // put notes in text area
            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");          // End of script

            //
            //*******************************************************************
            //  Move a member name into the tee slot
            //*******************************************************************
            //
            out.println("<script type=\"text/javascript\">");            // Move name script
            out.println("<!--");

            out.println("function movename(namewc) {");

            out.println("del = ':';");                               // deliminator is a colon
            out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
            out.println("var name = array[0];");
            out.println("var wc = array[1];");
            out.println("var f = document.forms['playerform'];");
            out.println("skip = 0;");

            out.println("var player1 = f.player1.value;");
            out.println("var player2 = f.player2.value;");
            out.println("var player3 = f.player3.value;");
            out.println("var player4 = f.player4.value;");

            if (players > 4) {
                out.println("var player5 = f.player5.value;");
            } else {
                out.println("var player5 = '';");
            }
            if (players > 5) {
                out.println("var player6 = f.player6.value;");
                out.println("var player7 = f.player7.value;");
                out.println("var player8 = f.player8.value;");
            } else {
                out.println("var player6 = '';");
                out.println("var player7 = '';");
                out.println("var player8 = '';");
            }
            if (players > 8) {
                out.println("var player9 = f.player9.value;");
                out.println("var player10 = f.player10.value;");
            } else {
                out.println("var player9 = '';");
                out.println("var player10 = '';");
            }
            if (players > 10) {
                out.println("var player11 = f.player11.value;");
                out.println("var player12 = f.player12.value;");
            } else {
                out.println("var player11 = '';");
                out.println("var player12 = '';");
            }
            if (players > 12) {
                out.println("var player13 = f.player13.value;");
                out.println("var player14 = f.player14.value;");
                out.println("var player15 = f.player15.value;");
            } else {
                out.println("var player13 = '';");
                out.println("var player14 = '';");
                out.println("var player15 = '';");
            }
            if (players > 15) {
                out.println("var player16 = f.player16.value;");
            } else {
                out.println("var player16 = '';");
            }
            if (players > 16) {
                out.println("var player17 = f.player17.value;");
                out.println("var player18 = f.player18.value;");
                out.println("var player19 = f.player19.value;");
                out.println("var player20 = f.player20.value;");
            } else {
                out.println("var player17 = '';");
                out.println("var player18 = '';");
                out.println("var player19 = '';");
                out.println("var player20 = '';");
            }
            if (players > 20) {
                out.println("var player21 = f.player21.value;");
                out.println("var player22 = f.player22.value;");
                out.println("var player23 = f.player23.value;");
                out.println("var player24 = f.player24.value;");
                out.println("var player25 = f.player25.value;");
            } else {
                out.println("var player21 = '';");
                out.println("var player22 = '';");
                out.println("var player23 = '';");
                out.println("var player24 = '';");
                out.println("var player25 = '';");
            }

//   if (!club.equals( "longcove" ) || today_date > 20080316) {

            out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ");
            out.println("    ( name == player5) || ( name == player6) || ( name == player7) || ( name == player8) || ");
            out.println("    ( name == player9) || ( name == player10) || ( name == player11) || ( name == player12) || ");
            out.println("    ( name == player13) || ( name == player14) || ( name == player15) || ( name == player16) || ");
            out.println("    ( name == player17) || ( name == player18) || ( name == player19) || ( name == player20) || ");
            out.println("    ( name == player21) || ( name == player22) || ( name == player23) || ( name == player24) || ");
            out.println("    ( name == player25)) {");
            out.println("skip = 1;");
            out.println("}");
//   }

            out.println("if (skip == 0) {");

            out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("f.player1.value = name;");
            out.println("f.guest_id1.value = '0';");
            out.println("add(f.p1cw, wc);");                    // add wc option if Pro Only and player default
            out.println("f.p1cw.value = wc;");
            out.println("} else {");

            out.println("if (player2 == '') {");                    // if player2 is empty
            out.println("f.player2.value = name;");
            out.println("f.guest_id2.value = '0';");
            out.println("add(f.p2cw, wc);");                    // add wc option if Pro Only and player default
            out.println("f.p2cw.value = wc;");
            out.println("} else {");

            out.println("if (player3 == '') {");                    // if player3 is empty
            out.println("f.player3.value = name;");
            out.println("f.guest_id3.value = '0';");
            out.println("add(f.p3cw, wc);");                    // add wc option if Pro Only and player default
            out.println("f.p3cw.value = wc;");
            out.println("} else {");

            out.println("if (player4 == '') {");                    // if player4 is empty
            out.println("f.player4.value = name;");
            out.println("f.guest_id4.value = '0';");
            out.println("add(f.p4cw, wc);");                    // add wc option if Pro Only and player default
            out.println("f.p4cw.value = wc;");

            if (players > 4) {
                out.println("} else {");
                out.println("if (player5 == '') {");                    // if player5 is empty
                out.println("f.player5.value = name;");
                out.println("f.guest_id5.value = '0';");
                out.println("add(f.p5cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p5cw.value = wc;");
            }
            if (players > 5) {
                out.println("} else {");
                out.println("if (player6 == '') {");                    // if player6 is empty
                out.println("f.player6.value = name;");
                out.println("f.guest_id6.value = '0';");
                out.println("add(f.p6cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p6cw.value = wc;");
                out.println("} else {");
                out.println("if (player7 == '') {");                    // if player7 is empty
                out.println("f.player7.value = name;");
                out.println("f.guest_id7.value = '0';");
                out.println("add(f.p7cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p7cw.value = wc;");
                out.println("} else {");
                out.println("if (player8 == '') {");                    // if player8 is empty
                out.println("f.player8.value = name;");
                out.println("f.guest_id8.value = '0';");
                out.println("add(f.p8cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p8cw.value = wc;");
            }
            if (players > 8) {
                out.println("} else {");
                out.println("if (player9 == '') {");                    // if player9 is empty
                out.println("f.player9.value = name;");
                out.println("f.guest_id9.value = '0';");
                out.println("add(f.p9cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p9cw.value = wc;");
                out.println("} else {");
                out.println("if (player10 == '') {");                    // if player10 is empty
                out.println("f.player10.value = name;");
                out.println("f.guest_id10.value = '0';");
                out.println("add(f.p10cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p10cw.value = wc;");
            }
            if (players > 10) {
                out.println("} else {");
                out.println("if (player11 == '') {");                    // if player11 is empty
                out.println("f.player11.value = name;");
                out.println("f.guest_id11.value = '0';");
                out.println("add(f.p11cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p11cw.value = wc;");
                out.println("} else {");
                out.println("if (player12 == '') {");                    // if player12 is empty
                out.println("f.player12.value = name;");
                out.println("f.guest_id12.value = '0';");
                out.println("add(f.p12cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p12cw.value = wc;");
            }
            if (players > 12) {
                out.println("} else {");
                out.println("if (player13 == '') {");                    // if player13 is empty
                out.println("f.player13.value = name;");
                out.println("f.guest_id13.value = '0';");
                out.println("add(f.p13cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p13cw.value = wc;");
                out.println("} else {");
                out.println("if (player14 == '') {");                    // if player14 is empty
                out.println("f.player14.value = name;");
                out.println("f.guest_id14.value = '0';");
                out.println("add(f.p14cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p14cw.value = wc;");
                out.println("} else {");
                out.println("if (player15 == '') {");                    // if player15 is empty
                out.println("f.player15.value = name;");
                out.println("f.guest_id15.value = '0';");
                out.println("add(f.p15cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p15cw.value = wc;");
            }
            if (players > 15) {
                out.println("} else {");
                out.println("if (player16 == '') {");                    // if player16 is empty
                out.println("f.player16.value = name;");
                out.println("f.guest_id16.value = '0';");
                out.println("add(f.p16cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p16cw.value = wc;");
            }
            if (players > 16) {
                out.println("} else {");
                out.println("if (player17 == '') {");                    // if player17 is empty
                out.println("f.player17.value = name;");
                out.println("f.guest_id17.value = '0';");
                out.println("add(f.p17cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p17cw.value = wc;");
                out.println("} else {");
                out.println("if (player18 == '') {");                    // if player18 is empty
                out.println("f.player18.value = name;");
                out.println("f.guest_id18.value = '0';");
                out.println("add(f.p18cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p18cw.value = wc;");
                out.println("} else {");
                out.println("if (player19 == '') {");                    // if player19 is empty
                out.println("f.player19.value = name;");
                out.println("f.guest_id19.value = '0';");
                out.println("add(f.p19cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p19cw.value = wc;");
                out.println("} else {");
                out.println("if (player20 == '') {");                    // if player20 is empty
                out.println("f.player20.value = name;");
                out.println("f.guest_id20.value = '0';");
                out.println("add(f.p20cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p20cw.value = wc;");
            }
            if (players > 20) {
                out.println("} else {");
                out.println("if (player21 == '') {");                    // if player21 is empty
                out.println("f.player21.value = name;");
                out.println("f.guest_id21.value = '0';");
                out.println("add(f.p21cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p21cw.value = wc;");
                out.println("} else {");
                out.println("if (player22 == '') {");                    // if player22 is empty
                out.println("f.player22.value = name;");
                out.println("f.guest_id22.value = '0';");
                out.println("add(f.p22cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p22cw.value = wc;");
                out.println("} else {");
                out.println("if (player23 == '') {");                    // if player23 is empty
                out.println("f.player23.value = name;");
                out.println("f.guest_id23.value = '0';");
                out.println("add(f.p23cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p23cw.value = wc;");
                out.println("} else {");
                out.println("if (player24 == '') {");                    // if player24 is empty
                out.println("f.player24.value = name;");
                out.println("f.guest_id24.value = '0';");
                out.println("add(f.p24cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p24cw.value = wc;");
                out.println("} else {");
                out.println("if (player25 == '') {");                    // if player25 is empty
                out.println("f.player25.value = name;");
                out.println("f.guest_id25.value = '0';");
                out.println("add(f.p25cw, wc);");                    // add wc option if Pro Only and player default
                out.println("f.p25cw.value = wc;");

                out.println("}");         // p25
                out.println("}");         // p24
                out.println("}");         // p23
                out.println("}");         // p22
                out.println("}");         // p21
                out.println("}");         // p20
                out.println("}");         // p19
                out.println("}");         // p18
                out.println("}");         // p17
                out.println("}");         // p16
                out.println("}");         // p15
                out.println("}");         // p14
                out.println("}");         // p13
                out.println("}");         // p12
                out.println("}");         // p11
                out.println("}");         // p10
                out.println("}");         // p9
                out.println("}");         // p8
                out.println("}");         // p7
                out.println("}");         // p6
                out.println("}");         // p5
            } else {
                if (players > 16) {
                    out.println("}");         // p20
                    out.println("}");         // p19
                    out.println("}");         // p18
                    out.println("}");         // p17
                    out.println("}");         // p16
                    out.println("}");         // p15
                    out.println("}");         // p14
                    out.println("}");         // p13
                    out.println("}");         // p12
                    out.println("}");         // p11
                    out.println("}");         // p10
                    out.println("}");         // p9
                    out.println("}");         // p8
                    out.println("}");         // p7
                    out.println("}");         // p6
                    out.println("}");         // p5
                } else {
                    if (players > 15) {
                        out.println("}");         // p16
                        out.println("}");         // p15
                        out.println("}");         // p14
                        out.println("}");         // p13
                        out.println("}");         // p12
                        out.println("}");         // p11
                        out.println("}");         // p10
                        out.println("}");         // p9
                        out.println("}");         // p8
                        out.println("}");         // p7
                        out.println("}");         // p6
                        out.println("}");         // p5
                    } else {
                        if (players > 12) {
                            out.println("}");         // p15
                            out.println("}");         // p14
                            out.println("}");         // p13
                            out.println("}");         // p12
                            out.println("}");         // p11
                            out.println("}");         // p10
                            out.println("}");         // p9
                            out.println("}");         // p8
                            out.println("}");         // p7
                            out.println("}");         // p6
                            out.println("}");         // p5
                        } else {
                            if (players > 10) {
                                out.println("}");         // p12
                                out.println("}");         // p11
                                out.println("}");         // p10
                                out.println("}");         // p9
                                out.println("}");         // p8
                                out.println("}");         // p7
                                out.println("}");         // p6
                                out.println("}");         // p5
                            } else {
                                if (players > 8) {
                                    out.println("}");         // p10
                                    out.println("}");         // p9
                                    out.println("}");         // p8
                                    out.println("}");         // p7
                                    out.println("}");         // p6
                                    out.println("}");         // p5
                                } else {
                                    if (players > 5) {
                                        out.println("}");         // p8
                                        out.println("}");         // p7
                                        out.println("}");         // p6
                                        out.println("}");         // p5
                                    } else {
                                        if (players > 4) {
                                            out.println("}");         // p5

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            out.println("}");         // p4
            out.println("}");         // p3
            out.println("}");         // p2
            out.println("}");         // p1

            out.println("}");                  // end of dup name chack (if skip = 0)

            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");                               // End of script


            //
            //*******************************************************************
            //  Move a Guest Name into the tee slot
            //*******************************************************************
            //
            out.println("<script type=\"text/javascript\">");            // Move Guest Name script
            out.println("<!--");

            out.println("var guestid_slot;");
            out.println("var player_slot;");

            out.println("var transport_defaults = JSON.parse(unescape('" + StringEscapeUtils.escapeJavaScript(gson_obj.toJson(slotPageParms.guest_type_cw_map)) + "'));");

            out.println("if(typeof(transport_defaults) != 'object'){transport_defaults = {'_default_':''}}; ");


            out.println("function moveguest(namewc) {");

            //out.println("var name = namewc;");

            out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
            out.println("var name = array[0];");
            out.println("var use_guestdb = array[1]");

            out.println("var f = document.forms['playerform'];");
            out.println("var defCW = transport_defaults[name]");

            out.println("if(typeof(defCW) != 'string'){defCW = transport_defaults['_default_'];}; ");
            out.println("if(typeof(defCW) != 'string'){defCW = '';}; ");

            // out.println("console.log('defCW:\"'+defCW+'\"');");


            out.println("var player1 = f.player1.value;");
            out.println("var player2 = f.player2.value;");
            out.println("var player3 = f.player3.value;");
            out.println("var player4 = f.player4.value;");

            if (players > 4) {
                out.println("var player5 = f.player5.value;");
            } else {
                out.println("var player5 = '';");
            }
            if (players > 5) {
                out.println("var player6 = f.player6.value;");
                out.println("var player7 = f.player7.value;");
                out.println("var player8 = f.player8.value;");
            } else {
                out.println("var player6 = '';");
                out.println("var player7 = '';");
                out.println("var player8 = '';");
            }
            if (players > 8) {
                out.println("var player9 = f.player9.value;");
                out.println("var player10 = f.player10.value;");
            } else {
                out.println("var player9 = '';");
                out.println("var player10 = '';");
            }
            if (players > 10) {
                out.println("var player11 = f.player11.value;");
                out.println("var player12 = f.player12.value;");
            } else {
                out.println("var player11 = '';");
                out.println("var player12 = '';");
            }
            if (players > 12) {
                out.println("var player13 = f.player13.value;");
                out.println("var player14 = f.player14.value;");
                out.println("var player15 = f.player15.value;");
            } else {
                out.println("var player13 = '';");
                out.println("var player14 = '';");
                out.println("var player15 = '';");
            }
            if (players > 15) {
                out.println("var player16 = f.player16.value;");
            } else {
                out.println("var player16 = '';");
            }
            if (players > 16) {
                out.println("var player17 = f.player17.value;");
                out.println("var player18 = f.player18.value;");
                out.println("var player19 = f.player19.value;");
                out.println("var player20 = f.player20.value;");
            } else {
                out.println("var player17 = '';");
                out.println("var player18 = '';");
                out.println("var player19 = '';");
                out.println("var player20 = '';");
            }
            if (players > 20) {
                out.println("var player21 = f.player21.value;");
                out.println("var player22 = f.player22.value;");
                out.println("var player23 = f.player23.value;");
                out.println("var player24 = f.player24.value;");
                out.println("var player25 = f.player25.value;");
            } else {
                out.println("var player21 = '';");
                out.println("var player22 = '';");
                out.println("var player23 = '';");
                out.println("var player24 = '';");
                out.println("var player25 = '';");
            }

            // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
            out.println("if (use_guestdb == 1 && (player1 == '' || player2 == '' || player3 == '' || player4 == ''"
                    + (players > 4 ? " || player5 == ''" : "")
                    + (players > 5 ? " || player6 == '' || player7 == '' || player8 == ''" : "")
                    + (players > 8 ? " || player9 == '' || player10 == ''" : "")
                    + (players > 10 ? " || player11 == '' || player12 == ''" : "")
                    + (players > 12 ? " || player13 == '' || player14 == '' || player15 == ''" : "")
                    + (players > 15 ? " || player16 == ''" : "")
                    + (players > 16 ? " || player17 == '' || player18 == '' || player19 == '' || player20 == ''" : "")
                    + (players > 20 ? " || player21 == '' || player22 == '' || player23 == '' || player24 == '' || player25 == ''" : "")
                    + ")) {");
            out.println("  loadmodal(0);");
            out.println("}");

            //  set spc to ' ' if name to move isn't an 'X'
            out.println("var spc = '';");
            out.println("if (name != 'X' && name != 'x') {");
            out.println("   spc = ' ';");
            out.println("}");

            out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player1;");
            out.println("guestid_slot = f.guest_id1;");
            out.println("f.player1.value = name + spc;");
            out.println("} else {");
            out.println("f.player1.focus();"); // here for IE compat
            out.println("f.player1.value = name + spc;");
            out.println("f.player1.focus();");
            out.println("}");
            out.println("if (defCW != '') {");
            out.println("f.p1cw.value = defCW;");
            out.println("}");
            out.println("} else {");

            out.println("if (player2 == '') {");                    // if player2 is empty
            out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player2;");
            out.println("guestid_slot = f.guest_id2;");
            out.println("f.player2.value = name + spc;");
            out.println("} else {");
            out.println("f.player2.focus();"); // here for IE compat
            out.println("f.player2.value = name + spc;");
            out.println("f.player2.focus();");
            out.println("}");
            out.println("if (defCW != '') {");
            out.println("f.p2cw.value = defCW;");
            out.println("}");
            out.println("} else {");

            out.println("if (player3 == '') {");                    // if player3 is empty
            out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player3;");
            out.println("guestid_slot = f.guest_id3;");
            out.println("f.player3.value = name + spc;");
            out.println("} else {");
            out.println("f.player3.focus();"); // here for IE compat
            out.println("f.player3.value = name + spc;");
            out.println("f.player3.focus();");
            out.println("}");
            out.println("if (defCW != '') {");
            out.println("f.p3cw.value = defCW;");
            out.println("}");
            out.println("} else {");

            out.println("if (player4 == '') {");                    // if player4 is empty
            out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player4;");
            out.println("guestid_slot = f.guest_id4;");
            out.println("f.player4.value = name + spc;");
            out.println("} else {");
            out.println("f.player4.focus();"); // here for IE compat
            out.println("f.player4.value = name + spc;");
            out.println("f.player4.focus();");
            out.println("}");
            out.println("if (defCW != '') {");
            out.println("f.p4cw.value = defCW;");
            out.println("}");

            if (players > 4) {
                out.println("} else {");
                out.println("if (player5 == '') {");                    // if player5 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player5;");
                out.println("guestid_slot = f.guest_id5;");
                out.println("f.player5.value = name + spc;");
                out.println("} else {");
                out.println("f.player5.focus();"); // here for IE compat
                out.println("f.player5.value = name + spc;");
                out.println("f.player5.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p5cw.value = defCW;");
                out.println("}");
            }
            if (players > 5) {
                out.println("} else {");
                out.println("if (player6 == '') {");                    // if player6 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player6;");
                out.println("guestid_slot = f.guest_id6;");
                out.println("f.player6.value = name + spc;");
                out.println("} else {");
                out.println("f.player6.focus();"); // here for IE compat
                out.println("f.player6.value = name + spc;");
                out.println("f.player6.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p6cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player7 == '') {");                    // if player7 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player7;");
                out.println("guestid_slot = f.guest_id7;");
                out.println("f.player7.value = name + spc;");
                out.println("} else {");
                out.println("f.player7.focus();"); // here for IE compat
                out.println("f.player7.value = name + spc;");
                out.println("f.player7.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p7cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player8 == '') {");                    // if player8 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player8;");
                out.println("guestid_slot = f.guest_id8;");
                out.println("f.player8.value = name + spc;");
                out.println("} else {");
                out.println("f.player8.focus();"); // here for IE compat
                out.println("f.player8.value = name + spc;");
                out.println("f.player8.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p8cw.value = defCW;");
                out.println("}");
            }
            if (players > 8) {
                out.println("} else {");
                out.println("if (player9 == '') {");                    // if player9 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player9;");
                out.println("guestid_slot = f.guest_id9;");
                out.println("f.player9.value = name + spc;");
                out.println("} else {");
                out.println("f.player9.focus();"); // here for IE compat
                out.println("f.player9.value = name + spc;");
                out.println("f.player9.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p9cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player10 == '') {");                    // if player10 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player10;");
                out.println("guestid_slot = f.guest_id10;");
                out.println("f.player10.value = name + spc;");
                out.println("} else {");
                out.println("f.player10.focus();"); // here for IE compat
                out.println("f.player10.value = name + spc;");
                out.println("f.player10.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p10cw.value = defCW;");
                out.println("}");
            }
            if (players > 10) {
                out.println("} else {");
                out.println("if (player11 == '') {");                    // if player11 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player11;");
                out.println("guestid_slot = f.guest_id11;");
                out.println("f.player11.value = name + spc;");
                out.println("} else {");
                out.println("f.player11.focus();"); // here for IE compat
                out.println("f.player11.value = name + spc;");
                out.println("f.player11.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p11cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player12 == '') {");                    // if player12 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player12;");
                out.println("guestid_slot = f.guest_id12;");
                out.println("f.player12.value = name + spc;");
                out.println("} else {");
                out.println("f.player12.focus();"); // here for IE compat
                out.println("f.player12.value = name + spc;");
                out.println("f.player12.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p12cw.value = defCW;");
                out.println("}");
            }
            if (players > 12) {
                out.println("} else {");
                out.println("if (player13 == '') {");                    // if player13 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player13;");
                out.println("guestid_slot = f.guest_id13;");
                out.println("f.player13.value = name + spc;");
                out.println("} else {");
                out.println("f.player13.focus();"); // here for IE compat
                out.println("f.player13.value = name + spc;");
                out.println("f.player13.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p13cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player14 == '') {");                    // if player14 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player14;");
                out.println("guestid_slot = f.guest_id14;");
                out.println("f.player14.value = name + spc;");
                out.println("} else {");
                out.println("f.player14.focus();"); // here for IE compat
                out.println("f.player14.value = name + spc;");
                out.println("f.player14.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p14cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player15 == '') {");                    // if player15 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player15;");
                out.println("guestid_slot = f.guest_id15;");
                out.println("f.player15.value = name + spc;");
                out.println("} else {");
                out.println("f.player15.focus();"); // here for IE compat
                out.println("f.player15.value = name + spc;");
                out.println("f.player15.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p15cw.value = defCW;");
                out.println("}");
            }
            if (players > 15) {
                out.println("} else {");
                out.println("if (player16 == '') {");                    // if player16 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player16;");
                out.println("guestid_slot = f.guest_id16;");
                out.println("f.player16.value = name + spc;");
                out.println("} else {");
                out.println("f.player16.focus();"); // here for IE compat
                out.println("f.player16.value = name + spc;");
                out.println("f.player16.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p16cw.value = defCW;");
                out.println("}");
            }
            if (players > 16) {
                out.println("} else {");
                out.println("if (player17 == '') {");                    // if player17 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player17;");
                out.println("guestid_slot = f.guest_id17;");
                out.println("f.player17.value = name + spc;");
                out.println("} else {");
                out.println("f.player17.focus();"); // here for IE compat
                out.println("f.player17.value = name + spc;");
                out.println("f.player17.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p17cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player18 == '') {");                    // if player18 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player18;");
                out.println("guestid_slot = f.guest_id18;");
                out.println("f.player18.value = name + spc;");
                out.println("} else {");
                out.println("f.player18.focus();"); // here for IE compat
                out.println("f.player18.value = name + spc;");
                out.println("f.player18.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p18cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player19 == '') {");                    // if player19 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player19;");
                out.println("guestid_slot = f.guest_id19;");
                out.println("f.player19.value = name + spc;");
                out.println("} else {");
                out.println("f.player19.focus();"); // here for IE compat
                out.println("f.player19.value = name + spc;");
                out.println("f.player19.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p19cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player20 == '') {");                    // if player20 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player20;");
                out.println("guestid_slot = f.guest_id20;");
                out.println("f.player20.value = name + spc;");
                out.println("} else {");
                out.println("f.player20.focus();"); // here for IE compat
                out.println("f.player20.value = name + spc;");
                out.println("f.player20.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p20cw.value = defCW;");
                out.println("}");
            }
            if (players > 20) {
                out.println("} else {");
                out.println("if (player21 == '') {");                    // if player21 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player21;");
                out.println("guestid_slot = f.guest_id21;");
                out.println("f.player21.value = name + spc;");
                out.println("} else {");
                out.println("f.player21.focus();"); // here for IE compat
                out.println("f.player21.value = name + spc;");
                out.println("f.player21.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p21cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player22 == '') {");                    // if player22 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player22;");
                out.println("guestid_slot = f.guest_id22;");
                out.println("f.player22.value = name + spc;");
                out.println("} else {");
                out.println("f.player22.focus();"); // here for IE compat
                out.println("f.player22.value = name + spc;");
                out.println("f.player22.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p22cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player23 == '') {");                    // if player23 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player23;");
                out.println("guestid_slot = f.guest_id23;");
                out.println("f.player23.value = name + spc;");
                out.println("} else {");
                out.println("f.player23.focus();"); // here for IE compat
                out.println("f.player23.value = name + spc;");
                out.println("f.player23.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p23cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player24 == '') {");                    // if player24 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player24;");
                out.println("guestid_slot = f.guest_id24;");
                out.println("f.player24.value = name + spc;");
                out.println("} else {");
                out.println("f.player24.focus();"); // here for IE compat
                out.println("f.player24.value = name + spc;");
                out.println("f.player24.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p24cw.value = defCW;");
                out.println("}");
                out.println("} else {");
                out.println("if (player25 == '') {");                    // if player25 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player25;");
                out.println("guestid_slot = f.guest_id25;");
                out.println("f.player25.value = name + spc;");
                out.println("} else {");
                out.println("f.player25.focus();"); // here for IE compat
                out.println("f.player25.value = name + spc;");
                out.println("f.player25.focus();");
                out.println("}");
                out.println("if (defCW != '') {");
                out.println("f.p25cw.value = defCW;");
                out.println("}");

                out.println("}");         // p25
                out.println("}");         // p24
                out.println("}");         // p23
                out.println("}");         // p22
                out.println("}");         // p21
                out.println("}");         // p20
                out.println("}");         // p19
                out.println("}");         // p18
                out.println("}");         // p17
                out.println("}");         // p16
                out.println("}");         // p15
                out.println("}");         // p14
                out.println("}");         // p13
                out.println("}");         // p12
                out.println("}");         // p11
                out.println("}");         // p10
                out.println("}");         // p9
                out.println("}");         // p8
                out.println("}");         // p7
                out.println("}");         // p6
                out.println("}");         // p5
            } else {
                if (players > 16) {
                    out.println("}");         // p20
                    out.println("}");         // p19
                    out.println("}");         // p18
                    out.println("}");         // p17
                    out.println("}");         // p16
                    out.println("}");         // p15
                    out.println("}");         // p14
                    out.println("}");         // p13
                    out.println("}");         // p12
                    out.println("}");         // p11
                    out.println("}");         // p10
                    out.println("}");         // p9
                    out.println("}");         // p8
                    out.println("}");         // p7
                    out.println("}");         // p6
                    out.println("}");         // p5
                } else {
                    if (players > 15) {
                        out.println("}");         // p16
                        out.println("}");         // p15
                        out.println("}");         // p14
                        out.println("}");         // p13
                        out.println("}");         // p12
                        out.println("}");         // p11
                        out.println("}");         // p10
                        out.println("}");         // p9
                        out.println("}");         // p8
                        out.println("}");         // p7
                        out.println("}");         // p6
                        out.println("}");         // p5
                    } else {
                        if (players > 12) {
                            out.println("}");         // p15
                            out.println("}");         // p14
                            out.println("}");         // p13
                            out.println("}");         // p12
                            out.println("}");         // p11
                            out.println("}");         // p10
                            out.println("}");         // p9
                            out.println("}");         // p8
                            out.println("}");         // p7
                            out.println("}");         // p6
                            out.println("}");         // p5
                        } else {
                            if (players > 10) {
                                out.println("}");         // p12
                                out.println("}");         // p11
                                out.println("}");         // p10
                                out.println("}");         // p9
                                out.println("}");         // p8
                                out.println("}");         // p7
                                out.println("}");         // p6
                                out.println("}");         // p5
                            } else {
                                if (players > 8) {
                                    out.println("}");         // p10
                                    out.println("}");         // p9
                                    out.println("}");         // p8
                                    out.println("}");         // p7
                                    out.println("}");         // p6
                                    out.println("}");         // p5
                                } else {
                                    if (players > 5) {
                                        out.println("}");         // p8
                                        out.println("}");         // p7
                                        out.println("}");         // p6
                                        out.println("}");         // p5
                                    } else {
                                        if (players > 4) {
                                            out.println("}");         // p5

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            out.println("}");         // p4
            out.println("}");         // p3
            out.println("}");         // p2
            out.println("}");         // p1

            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");                               // End of script

            out.println("</HEAD>");

            // ********* end of scripts **********

            out.println("<body onLoad=\"movenotes()\" bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\" topmargin=\"0\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            // use local list to populate global array in script
            out.println("<script type=\"text/javascript\">");
            out.println("<!-- ");
            /*
            out.println("var nonProCount = " + nonProCount + ";");
            out.println("var nonProTmodes = Array()");
            for (int j = 0; j < nonProCount; j++) {
            out.println("nonProTmodes[" + j + "] = \"" + nonProTmodes[j] + "\";");
            }
             * 
             */
            out.println("var nonProCount = " + slotPageParms.tmodes_list.size() + ";");
            out.println("var nonProTmodes = Array()");
            for (int j = 0; j < slotPageParms.tmodes_list.size(); j++) {
                out.println("nonProTmodes[" + j + "] = \"" + slotPageParms.tmodes_list.get(j) + "\";");
            }

            // Create global array of available tmodes for this course
            int tmodeCount = 0;
            out.println("var tmodes = Array()");
            for (int j = 0; j < parmc.tmode_limit; j++) {
                if (!parmc.tmodea[j].equals("")) {
                    tmodeCount++;
                    out.println("tmodes[" + j + "] = \"" + parmc.tmodea[j] + "\";");
                }
            }
            out.println("var tmodeCount = " + tmodeCount + ";");
            out.println("// -->");
            out.println("</script>");

            out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
            out.println("<tr><td valign=\"top\" align=\"center\">");

            out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"336633\" align=\"center\" valign=\"top\">");
            out.println("<tr><td align=\"left\" width=\"300\">");
            out.println("<img src=\"/" + rev + "/images/foretees.gif\" border=0>");
            out.println("</td>");

            out.println("<td align=\"center\">");
            out.println("<font color=\"#ffffff\" size=\"5\">Member Tee Time Request</font>");
            out.println("</font></td>");

            out.println("<td align=\"center\" width=\"300\">");
            out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
            out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
            out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> " + thisYear + " All rights reserved.");
            out.println("</font></td>");
            out.println("</tr></table>");

            out.println("<table border=\"0\" align=\"center\">");                           // table for main page
            out.println("<tr><td align=\"center\">");

            out.println("<br>");
            out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
            out.println("<tr>");
            out.println("<td width=\"620\" align=\"center\">");
            out.println("<font size=\"3\">");
            out.println("<b>Tee Time Registration</b><br></font>");
            out.println("<font size=\"1\">");
            out.println(" Add players to the group(s) and click on 'Submit Request' to enter the request. ");
            out.println("<br><b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this reservation.");
            out.println("&nbsp; If you want to return without completing a reservation, <b>do not ");
            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
            out.println("option below.");
            out.println("</font></td></tr>");
            out.println("</table>");

            out.println("<font size=\"2\"><br>");
            out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
            out.println(" &nbsp;&nbsp;&nbsp;&nbsp;First Time Requested:&nbsp;&nbsp;<b>" + stime + "</b>");
            if (!course_disp.equals("")) {

                out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course_disp + "</b>");

            }

            out.println("<table border=\"0\" align=\"center\" valign=\"top\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 4 tables below

            out.println("<tr>");
            out.println("<td align=\"center\" valign=\"top\">");         // col for Instructions

            out.println("<br><br><br><font size=\"1\">");
            out.println("<a href=\"#\" onClick=\"window.open ('/" + rev + "/member_help_slot_instruct.htm', 'newwindow', config='Height=500, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("<img src=\"/" + rev + "/images/instructions.gif\" border=0>");
            out.println("<br>Click for Help</a>");

            out.println("</font><font size=\"2\">");
            out.println("<br><br><br>");

            out.println("<form action=\"Member_slotm\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
            out.println("<input type=\"hidden\" name=\"time2\" value=\"" + slotParms.time2 + "\">");
            out.println("<input type=\"hidden\" name=\"time3\" value=\"" + slotParms.time3 + "\">");
            out.println("<input type=\"hidden\" name=\"time4\" value=\"" + slotParms.time4 + "\">");
            out.println("<input type=\"hidden\" name=\"time5\" value=\"" + slotParms.time5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
            out.println("Return<br>w/o Changes:<br>");
            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
            out.println("</font></td>");

            out.println("<form action=\"Member_slotm\" method=\"post\" name=\"playerform\" id=\"playerform\">");
            out.println("<td align=\"center\" valign=\"top\">");

            out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"370\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#ffffff\" size=\"2\">");
            out.println("<b>Add or Remove Players</b>&nbsp;&nbsp; Note: Click on Names -->");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"left\">");
            out.println("<font size=\"2\">");

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes</b><br>");


            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + slotParms.guest_id6 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + slotParms.guest_id7 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + slotParms.guest_id8 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + slotParms.guest_id9 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + slotParms.guest_id10 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + slotParms.guest_id11 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + slotParms.guest_id12 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + slotParms.guest_id13 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + slotParms.guest_id14 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + slotParms.guest_id15 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + slotParms.guest_id16 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + slotParms.guest_id17 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + slotParms.guest_id18 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + slotParms.guest_id19 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + slotParms.guest_id20 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + slotParms.guest_id21 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + slotParms.guest_id22 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + slotParms.guest_id23 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + slotParms.guest_id24 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + slotParms.guest_id25 + "\">");

            for (int i2 = 0; i2 < players; i2++) {
                if ((i2 % players_per_group == 0) && i2 > 0) {  // New group
                    out.println("</font></td></tr>");
                    out.print("<tr><td align=\"left\">");     // new row for new group
                    out.print("<font size=\"2\">");
                } else if (i2 > 0) {  // Next player
                    out.print("<br>");
                }
                out.println("&nbsp;<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player" + (i2 + 1) + "', 'p" + (i2 + 1) + "cw')\" style=\"cursor:hand\">");
                out.print("" + (i2 + 1) + ":");
                if (i2 < 9) {
                    out.print("&nbsp;");
                }
                out.println("&nbsp;<input type=\"text\" name=\"player" + (i2 + 1) + "\" value=\"" + slotParms.getPlayer(i2) + "\" size=\"20\" maxlength=\"43\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p" + (i2 + 1) + "cw\">");

                out.println("<option selected value=" + slotParms.getCw(i2) + ">" + slotParms.getCw(i2) + "</option>");
                for (int i3 = 0; i3 < 16; i3++) {

                    if (!parmc.tmodea[i3].equals("") && !parmc.tmodea[i3].equals(slotParms.getCw(i2)) && parmc.tOpt[i3] == 0) {
                        out.println("<option value=\"" + parmc.tmodea[i3] + "\">" + parmc.tmodea[i3] + "</option>");
                    }
                }
                out.println("</select>");
                if (slotParms.getP9(i2) == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p9" + (i2 + 1) + "\" value=\"1\">");
                } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p9" + (i2 + 1) + "\" value=\"1\">");
                }
            }

            out.println("</font></td></tr>");

            out.println("<tr><td align=\"center\">");     // new row for notes, etc.
            out.println("<font size=\"2\">");

            //
            //   Notes
            //
            //   Script will put any existing notes in the textarea (value= doesn't work)
            //
            out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

            if (hide != 0) {      // if proshop wants to hide the notes, do not display the text box or notes

                out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">"); // pass existing notes

            } else {

                out.println("<br><img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
                out.println("Notes to Pro:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"22\" rows=\"2\">");
                out.println("</textarea>");
            }

            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
            out.println("<input type=\"hidden\" name=\"time2\" value=\"" + slotParms.time2 + "\">");
            out.println("<input type=\"hidden\" name=\"time3\" value=\"" + slotParms.time3 + "\">");
            out.println("<input type=\"hidden\" name=\"time4\" value=\"" + slotParms.time4 + "\">");
            out.println("<input type=\"hidden\" name=\"time5\" value=\"" + slotParms.time5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
            out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
            out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");

            out.println("<font size=\"1\"><br>");
            out.println(slotPageParms.transport_legend);
            out.println("(9 = 9 holes)</font><br>");
            out.println("<input type=submit value=\"Submit Request\" name=\"submitForm\">");
            out.println("</font></td></tr>");
            out.println("</table>");
            out.println("</td>");
            out.println("<td valign=\"top\">");

            // ********************************************************************************
            //   If we got control from user clicking on a letter in the Member List,
            //   then we must build the name list.
            // ********************************************************************************
            String letter = "";

            if (req.getParameter("letter") != null) {     // if user clicked on a name letter

                letter = req.getParameter("letter");

                if (!letter.equals("Partner List")) {      // if not Partner List request

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

                        PreparedStatement stmt2 = con.prepareStatement(
                                "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, wc "
                                + "FROM member2b "
                                + "WHERE name_last LIKE ? AND inact = 0 "
                                + "ORDER BY last_only, name_first, name_mi");

                        stmt2.clearParameters();               // clear the parms
                        stmt2.setString(1, letter);            // put the parm in stmt
                        rs = stmt2.executeQuery();             // execute the prepared stmt

                        out.println("<tr><td align=\"left\"><font size=\"2\">");
                        out.println("<select size=\"20\" name=\"bname\" onClick=\"movename(this.form.bname.value)\" style=\"cursor:hand\">");

                        while (rs.next()) {

                            last = rs.getString("name_last");
                            first = rs.getString("name_first");
                            mid = rs.getString("name_mi");
                            mship = rs.getString("m_ship");
                            wc = rs.getString("wc");           // walk/cart preference

                            if (!club.equals("sonnenalp")) {         // skip test if Sonnenalp

                                i = 0;
                                loopi3:
                                while (i < 16) {             // make sure wc is supported

                                    if (parmc.tmodea[i].equals(wc)) {

                                        break loopi3;
                                    }
                                    i++;
                                }
                                if (i > 15) {       // if we went all the way without a match

                                    wc = parmc.tmodea[0];    // use default option
                                }
                            }

                            i = 0;

                            if (mid.equals("")) {

                                bname = first + " " + last;
                                dname = last + ", " + first;
                            } else {

                                bname = first + " " + mid + " " + last;
                                dname = last + ", " + first + " " + mid;
                            }

                            wname = bname + ":" + wc;              // combine name:wc for script

                            if (club.equals("cordillera")) {

                                if (!mship.startsWith("Employee")) {       // if not an Employee (skip employees)

                                    out.println("<option value=\"" + wname + "\">" + dname + "</option>");
                                }
                            } else {
                                out.println("<option value=\"" + wname + "\">" + dname + "</option>");
                            }
                        }

                        out.println("</select>");
                        out.println("</font></td></tr>");

                        stmt2.close();
                    } catch (Exception ignore) {
                    }

                }        // end of IF Partner List or letter

            }           // not letter display

            if (letter.equals("") || letter.equals("Partner List")) {  // if no letter or Partner List request

                alphaTable.displayPartnerList(user, activity_id, 0, con, out);

            }        // end of if letter display

            out.println("</table></td>");

            out.println("</td>");                                      // end of this column
            out.println("<td width=\"200\" valign=\"top\">");


            //
            //   Output the Alphabit Table for Members' Last Names
            //
            alphaTable.getTable(out, user);

            // call to getClub was here, moved to above

            if (x != 0) {

                //
                //  add a table for 'x'
                //
                out.println("<font size=\"1\"><br></font>");
                out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\">");
                out.println("<tr bgcolor=\"#336633\">");
                out.println("<td align=\"center\">");
                out.println("<font color=\"#FFFFFF\" size=\"2\">");
                out.println("<b>Member TBD</b>");
                out.println("</font></td>");
                out.println("</tr>");
                out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
                out.println("Use 'X' to reserve a position for a Member.<br>");
                out.println("</font></td></tr>");
                out.println("<tr><td align=\"left\" bgcolor=\"#FFFFFF\">");
                out.println("<font size=\"2\">");
                out.println("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onClick=\"moveguest('X')\">X</a>");
                out.println("</font></td></tr></table>");      // end of this table
            }

            //
            //  add a table for the Guest Types
            //
            out.println("<font size=\"1\"><br></font>");
            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
            out.println("<tr bgcolor=\"#336633\">");
            out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            if (club.equals("lakewood")) {
                out.println("<b>Player Options</b>");
            } else {
                out.println("<b>Guest Types</b>");
            }
            out.println("</font></td>");
            out.println("</tr>");

            if (slotPageParms.guest_types_map.size() > 0) {                       // if guest names, display them in list

                xCount = slotPageParms.guest_types_map.size();

                if (xCount < 2) {

                    xCount = 2;             // set size to at least 2
                }
                if (xCount > 8) {

                    xCount = 8;             // set size to no more than 8 showing at once (it will scroll)
                }
                out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
                out.println("<b>**</b> Add guests immediately<br><b>after</b> host member.<br>");
                out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<select size=\"" + xCount + "\" name=\"xname\" onClick=\"moveguest(this.form.xname.value)\">");

                for (Map<String, Object> guest_type_entry : slotPageParms.guest_types_map.values()) {

                    out.println("<option value=\"" + ((String) (guest_type_entry.get("guest_type"))) + "|" + ((Integer) (guest_type_entry.get("guest_type_db"))) + "\">" + ((String) (guest_type_entry.get("guest_type"))) + "</option>");

                }
                out.println("</select>");
                out.println("</font></td></tr></table>");      // end of this table

            } else {

                out.println("</table>");      // end the table if none specified
            }

            out.println("</td>");             // end of this column
            out.println("</tr>");
            out.println("</form>");     // end of playerform
            out.println("</table>");      // end of large table containing 4 smaller tables (columns)

            out.println("</font></td></tr>");
            out.println("</table>");                      // end of main page table
            //
            //  End of HTML page
            //
            out.println("</td></tr>");
            out.println("</table>");                      // end of whole page table
            out.println("</font></body></html>");
            out.close();
        } // End of old-skin output

    }  // end of doPost

    // *********************************************************
    //  Process reservation request from Member_slotm (HTML)
    // *********************************************************
    private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


        ResultSet rs = null;



        /*
        // ********* temp - save for future debug !!! **********
        //
        Enumeration enum = req.getParameterNames();
        
        out.println(SystemUtils.HeadTitle("Database Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H1>Proshop_club Parameters</H1>");
        
        out.println("<BR><BR>Query String: ");
        out.println(req.getQueryString());
        out.println();
        
        out.println("<BR><BR>Request Parms: ");
        
        while (enum.hasMoreElements()) {
        
        String name = (String) enum.nextElement();
        String values[] = req.getParameterValues(name);
        if (values != null) {
        for (int i=0; i<values.length; i++) {
        
        out.println("<BR><BR>" +name+ " (" +i+ "): " +values[i]);
        }
        }
        }
        
        out.println("<BR><BR>");
        out.println("<BR><BR><a href=\"Member_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        // ********* temp **********
         */


        //
        //  Get this session's user name
        //
        String user = (String) session.getAttribute("user");
        String fullName = (String) session.getAttribute("name");
        String club = (String) session.getAttribute("club");
        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");

        Map<String, Object> result_map = new LinkedHashMap<String, Object>();
        Map<String, Object> hidden_field_map = new LinkedHashMap<String, Object>();

        Gson gson_obj = new Gson();

        int players = 0;
        int slots = 0;
        int time = 0;
        int dd = 0;
        int mm = 0;
        int yy = 0;
        int grest_num = 0;
        int fb = 0;
        int i = 0;
        int i2 = 0;
        int temp = 0;
        int in_use = 0;
        int hide = 0;
        int gi = 0;
        int ind = 0;
        int customId = 0;
        int msgPlayerCount = 0;

        long date = 0;

        String player = "";
        String rest_name = "";
        String grest_recurr = "";
        String day = "";
        String in_use_by = "";
        String err_name = "";
        String sfb = "";
        String rest_fb = "";
        String notes = "";
        String rcourse = "";
        String memberName = "";
        String orig_by = user;
        String p9s = "";
        String temps = "";
        String errorMsg = "";
        String returnCourse = "";
        String msgDate = "";

        boolean hit = false;
        boolean check = false;
        boolean error = false;
        boolean guestError = false;

        String[] userg = new String[25];     // and user guest names
        //String [] rguest = new String [36];    // array to hold the Guest Restriction guest names
        ArrayList<String> rguest = new ArrayList<String>();

        //
        //  Arrays to hold member & guest names to tie guests to members (gstA[] resides in parmSlotm)
        //
        String[] memA = new String[25];     // members
        String[] usergA = new String[25];   // guests' associated member (username)

        //
        //  parm block to hold verify's parms
        //
        parmSlotm parm = new parmSlotm();          // allocate a parm block

        parm.club = club;                          // save club name

        for (i = 0; i < 25; i++) {           // init the arrays

            userg[i] = "";
            parm.userg[i] = "";
            usergA[i] = "";
        }

        //
        // Get all the parameters entered
        //
        String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
        String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
        String smm = req.getParameter("mm");               //  month of tee time
        String syy = req.getParameter("yy");               //  year of tee time
        String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
        String p5 = req.getParameter("p5");                //  5-somes supported for this slot
        String course = req.getParameter("course");        //  name of course
        String sslots = req.getParameter("slots");         //  # of groups allowed for the Lottery

        if (req.getParameter("returnCourse") != null) {
            returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
        }

        //
        //  Put all the parms in a parm block
        //
        parm.player1 = req.getParameter("player1");
        parm.player2 = req.getParameter("player2");
        parm.player3 = req.getParameter("player3");
        parm.player4 = req.getParameter("player4");
        if (req.getParameter("player5") != null) {
            parm.player5 = req.getParameter("player5");
        }
        if (req.getParameter("player6") != null) {
            parm.player6 = req.getParameter("player6");
        }
        if (req.getParameter("player7") != null) {
            parm.player7 = req.getParameter("player7");
        }
        if (req.getParameter("player8") != null) {
            parm.player8 = req.getParameter("player8");
        }
        if (req.getParameter("player9") != null) {
            parm.player9 = req.getParameter("player9");
        }
        if (req.getParameter("player10") != null) {
            parm.player10 = req.getParameter("player10");
        }
        if (req.getParameter("player11") != null) {
            parm.player11 = req.getParameter("player11");
        }
        if (req.getParameter("player12") != null) {
            parm.player12 = req.getParameter("player12");
        }
        if (req.getParameter("player13") != null) {
            parm.player13 = req.getParameter("player13");
        }
        if (req.getParameter("player14") != null) {
            parm.player14 = req.getParameter("player14");
        }
        if (req.getParameter("player15") != null) {
            parm.player15 = req.getParameter("player15");
        }
        if (req.getParameter("player16") != null) {
            parm.player16 = req.getParameter("player16");
        }
        if (req.getParameter("player17") != null) {
            parm.player17 = req.getParameter("player17");
        }
        if (req.getParameter("player18") != null) {
            parm.player18 = req.getParameter("player18");
        }
        if (req.getParameter("player19") != null) {
            parm.player19 = req.getParameter("player19");
        }
        if (req.getParameter("player20") != null) {
            parm.player20 = req.getParameter("player20");
        }
        if (req.getParameter("player21") != null) {
            parm.player21 = req.getParameter("player21");
        }
        if (req.getParameter("player22") != null) {
            parm.player22 = req.getParameter("player22");
        }
        if (req.getParameter("player23") != null) {
            parm.player23 = req.getParameter("player23");
        }
        if (req.getParameter("player24") != null) {
            parm.player24 = req.getParameter("player24");
        }
        if (req.getParameter("player25") != null) {
            parm.player25 = req.getParameter("player25");
        }
        if (req.getParameter("p1cw") != null) {
            parm.pcw1 = req.getParameter("p1cw");
        }
        if (req.getParameter("p2cw") != null) {
            parm.pcw2 = req.getParameter("p2cw");
        }
        if (req.getParameter("p3cw") != null) {
            parm.pcw3 = req.getParameter("p3cw");
        }
        if (req.getParameter("p4cw") != null) {
            parm.pcw4 = req.getParameter("p4cw");
        }
        if (req.getParameter("p5cw") != null) {
            parm.pcw5 = req.getParameter("p5cw");
        }
        if (req.getParameter("p6cw") != null) {
            parm.pcw6 = req.getParameter("p6cw");
        }
        if (req.getParameter("p7cw") != null) {
            parm.pcw7 = req.getParameter("p7cw");
        }
        if (req.getParameter("p8cw") != null) {
            parm.pcw8 = req.getParameter("p8cw");
        }
        if (req.getParameter("p9cw") != null) {
            parm.pcw9 = req.getParameter("p9cw");
        }
        if (req.getParameter("p10cw") != null) {
            parm.pcw10 = req.getParameter("p10cw");
        }
        if (req.getParameter("p11cw") != null) {
            parm.pcw11 = req.getParameter("p11cw");
        }
        if (req.getParameter("p12cw") != null) {
            parm.pcw12 = req.getParameter("p12cw");
        }
        if (req.getParameter("p13cw") != null) {
            parm.pcw13 = req.getParameter("p13cw");
        }
        if (req.getParameter("p14cw") != null) {
            parm.pcw14 = req.getParameter("p14cw");
        }
        if (req.getParameter("p15cw") != null) {
            parm.pcw15 = req.getParameter("p15cw");
        }
        if (req.getParameter("p16cw") != null) {
            parm.pcw16 = req.getParameter("p16cw");
        }
        if (req.getParameter("p17cw") != null) {
            parm.pcw17 = req.getParameter("p17cw");
        }
        if (req.getParameter("p18cw") != null) {
            parm.pcw18 = req.getParameter("p18cw");
        }
        if (req.getParameter("p19cw") != null) {
            parm.pcw19 = req.getParameter("p19cw");
        }
        if (req.getParameter("p20cw") != null) {
            parm.pcw20 = req.getParameter("p20cw");
        }
        if (req.getParameter("p21cw") != null) {
            parm.pcw21 = req.getParameter("p21cw");
        }
        if (req.getParameter("p22cw") != null) {
            parm.pcw22 = req.getParameter("p22cw");
        }
        if (req.getParameter("p23cw") != null) {
            parm.pcw23 = req.getParameter("p23cw");
        }
        if (req.getParameter("p24cw") != null) {
            parm.pcw24 = req.getParameter("p24cw");
        }
        if (req.getParameter("p25cw") != null) {
            parm.pcw25 = req.getParameter("p25cw");
        }

        day = req.getParameter("day");                      // name of day
        sfb = req.getParameter("fb");                       // Front/Back indicator
        notes = req.getParameter("notes").trim();           // Member Notes
        String hides = req.getParameter("hide");            // Hide Notes Indicator
        String jump = req.getParameter("jump");             // jump index for _sheet

        //
        //  set 9-hole options
        //
        parm.p91 = 0;                       // init to 18 holes
        parm.p92 = 0;
        parm.p93 = 0;
        parm.p94 = 0;
        parm.p95 = 0;
        parm.p96 = 0;
        parm.p97 = 0;
        parm.p98 = 0;
        parm.p99 = 0;
        parm.p910 = 0;
        parm.p911 = 0;                       // init to 18 holes
        parm.p912 = 0;
        parm.p913 = 0;
        parm.p914 = 0;
        parm.p915 = 0;
        parm.p916 = 0;
        parm.p917 = 0;
        parm.p918 = 0;
        parm.p919 = 0;
        parm.p920 = 0;
        parm.p921 = 0;                       // init to 18 holes
        parm.p922 = 0;
        parm.p923 = 0;
        parm.p924 = 0;
        parm.p925 = 0;

        if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
            p9s = req.getParameter("p91");
            parm.p91 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p92") != null) {
            p9s = req.getParameter("p92");
            parm.p92 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p93") != null) {
            p9s = req.getParameter("p93");
            parm.p93 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p94") != null) {
            p9s = req.getParameter("p94");
            parm.p94 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p95") != null) {
            p9s = req.getParameter("p95");
            parm.p95 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p96") != null) {
            p9s = req.getParameter("p96");
            parm.p96 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p97") != null) {
            p9s = req.getParameter("p97");
            parm.p97 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p98") != null) {
            p9s = req.getParameter("p98");
            parm.p98 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p99") != null) {
            p9s = req.getParameter("p99");
            parm.p99 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p910") != null) {
            p9s = req.getParameter("p910");
            parm.p910 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p911") != null) {             // get 9-hole indicators if they were checked
            p9s = req.getParameter("p911");
            parm.p911 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p912") != null) {
            p9s = req.getParameter("p912");
            parm.p912 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p913") != null) {
            p9s = req.getParameter("p913");
            parm.p913 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p914") != null) {
            p9s = req.getParameter("p914");
            parm.p914 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p915") != null) {
            p9s = req.getParameter("p915");
            parm.p915 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p916") != null) {
            p9s = req.getParameter("p916");
            parm.p916 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p917") != null) {
            p9s = req.getParameter("p917");
            parm.p917 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p918") != null) {
            p9s = req.getParameter("p918");
            parm.p918 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p919") != null) {
            p9s = req.getParameter("p919");
            parm.p919 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p920") != null) {
            p9s = req.getParameter("p920");
            parm.p920 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p921") != null) {             // get 9-hole indicators if they were checked
            p9s = req.getParameter("p921");
            parm.p921 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p922") != null) {
            p9s = req.getParameter("p922");
            parm.p922 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p923") != null) {
            p9s = req.getParameter("p923");
            parm.p923 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p924") != null) {
            p9s = req.getParameter("p924");
            parm.p924 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p925") != null) {
            p9s = req.getParameter("p925");
            parm.p925 = Integer.parseInt(p9s);
        }

        parm.guest_id1 = (req.getParameter("guest_id1") != null) ? Integer.parseInt(req.getParameter("guest_id1")) : 0;
        parm.guest_id2 = (req.getParameter("guest_id2") != null) ? Integer.parseInt(req.getParameter("guest_id2")) : 0;
        parm.guest_id3 = (req.getParameter("guest_id3") != null) ? Integer.parseInt(req.getParameter("guest_id3")) : 0;
        parm.guest_id4 = (req.getParameter("guest_id4") != null) ? Integer.parseInt(req.getParameter("guest_id4")) : 0;
        parm.guest_id5 = (req.getParameter("guest_id5") != null) ? Integer.parseInt(req.getParameter("guest_id5")) : 0;
        parm.guest_id6 = (req.getParameter("guest_id6") != null) ? Integer.parseInt(req.getParameter("guest_id6")) : 0;
        parm.guest_id7 = (req.getParameter("guest_id7") != null) ? Integer.parseInt(req.getParameter("guest_id7")) : 0;
        parm.guest_id8 = (req.getParameter("guest_id8") != null) ? Integer.parseInt(req.getParameter("guest_id8")) : 0;
        parm.guest_id9 = (req.getParameter("guest_id9") != null) ? Integer.parseInt(req.getParameter("guest_id9")) : 0;
        parm.guest_id10 = (req.getParameter("guest_id10") != null) ? Integer.parseInt(req.getParameter("guest_id10")) : 0;
        parm.guest_id11 = (req.getParameter("guest_id11") != null) ? Integer.parseInt(req.getParameter("guest_id11")) : 0;
        parm.guest_id12 = (req.getParameter("guest_id12") != null) ? Integer.parseInt(req.getParameter("guest_id12")) : 0;
        parm.guest_id13 = (req.getParameter("guest_id13") != null) ? Integer.parseInt(req.getParameter("guest_id13")) : 0;
        parm.guest_id14 = (req.getParameter("guest_id14") != null) ? Integer.parseInt(req.getParameter("guest_id14")) : 0;
        parm.guest_id15 = (req.getParameter("guest_id15") != null) ? Integer.parseInt(req.getParameter("guest_id15")) : 0;
        parm.guest_id16 = (req.getParameter("guest_id16") != null) ? Integer.parseInt(req.getParameter("guest_id16")) : 0;
        parm.guest_id17 = (req.getParameter("guest_id17") != null) ? Integer.parseInt(req.getParameter("guest_id17")) : 0;
        parm.guest_id18 = (req.getParameter("guest_id18") != null) ? Integer.parseInt(req.getParameter("guest_id18")) : 0;
        parm.guest_id19 = (req.getParameter("guest_id19") != null) ? Integer.parseInt(req.getParameter("guest_id19")) : 0;
        parm.guest_id20 = (req.getParameter("guest_id20") != null) ? Integer.parseInt(req.getParameter("guest_id20")) : 0;
        parm.guest_id21 = (req.getParameter("guest_id21") != null) ? Integer.parseInt(req.getParameter("guest_id21")) : 0;
        parm.guest_id22 = (req.getParameter("guest_id22") != null) ? Integer.parseInt(req.getParameter("guest_id22")) : 0;
        parm.guest_id23 = (req.getParameter("guest_id23") != null) ? Integer.parseInt(req.getParameter("guest_id23")) : 0;
        parm.guest_id24 = (req.getParameter("guest_id24") != null) ? Integer.parseInt(req.getParameter("guest_id24")) : 0;
        parm.guest_id25 = (req.getParameter("guest_id25") != null) ? Integer.parseInt(req.getParameter("guest_id25")) : 0;

        //
        //  init and get the other tee times
        //
        parm.time1 = 0;
        parm.time2 = 0;
        parm.time3 = 0;
        parm.time4 = 0;
        parm.time5 = 0;

        if (req.getParameter("time1") != null) {
            temps = req.getParameter("time1");
            parm.time1 = Integer.parseInt(temps);
        }

        if (req.getParameter("time2") != null) {
            temps = req.getParameter("time2");
            parm.time2 = Integer.parseInt(temps);
        }
        if (req.getParameter("time3") != null) {
            temps = req.getParameter("time3");
            parm.time3 = Integer.parseInt(temps);
        }
        if (req.getParameter("time4") != null) {
            temps = req.getParameter("time4");
            parm.time4 = Integer.parseInt(temps);
        }
        if (req.getParameter("time5") != null) {
            temps = req.getParameter("time5");
            parm.time5 = Integer.parseInt(temps);
        }

        //
        //  Convert the string values to ints
        //
        try {

            slots = Integer.parseInt(sslots);
            time = parm.time1; //Integer.parseInt(stime);
            mm = Integer.parseInt(smm);
            yy = Integer.parseInt(syy);
            fb = Integer.parseInt(sfb);
            date = Long.parseLong(sdate);

        } catch (NumberFormatException e) {

            Utilities.logError("NumberFormat Error in Member_slotm: " + e);

        }


        if (!course.equals("")) {
            //
            //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
            //
            course = SystemUtils.filter(course);
        }

        //
        //  convert the index value from string to numeric - save both
        //
        String num = index;

        if (num.startsWith("i")) {

            StringTokenizer tok = new StringTokenizer(num, "i");     // space is the default token - use 'i'

            num = tok.nextToken();      // get just the index number
        }

        try {
            ind = Integer.parseInt(num);
        } catch (NumberFormatException e) {
        }

        //
        //  Save some of the parms in the parm table
        //
        parm.date = date;
        parm.time1 = time;
        parm.fb = fb;
        parm.mm = mm;
        parm.dd = dd;
        parm.yy = yy;
        parm.day = day;
        parm.course = course;
        parm.returnCourse = returnCourse;
        parm.jump = jump;
        parm.index = index;
        parm.ind = ind;
        parm.p5 = p5;
        parm.notes = notes;
        parm.hides = hides;

        //
        //  Init other parm fields
        //
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

        //
        //  See if user wants to hide any notes from the Members
        //
        hide = 0;      // init

        if (hides.equals("Yes")) {

            hide = 1;
        }

        parm.hide = hide;

        //
        //  Get the length of Notes (max length of 254 chars)
        //
        int notesL = 0;

        if (!notes.equals("")) {

            notesL = notes.length();       // get length of notes
        }

        //
        //   use yy and mm and date to determine dd (from tee time's date)
        //
        temp = yy * 10000;
        temp = temp + (mm * 100);
        parm.dd = (int) date - temp;            // get day of month from date

        //
        //  Get today's date 
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        //int calYear = cal.get(Calendar.YEAR);
        int calMonth = cal.get(Calendar.MONTH) + 1;
        int calDay = cal.get(Calendar.DAY_OF_MONTH);
        int calHr = cal.get(Calendar.HOUR_OF_DAY);         // 24 hr clock (0 - 23)
        int calMin = cal.get(Calendar.MINUTE);

        //long today_date = (calYear * 10000) + (calMonth * 100) + calDay;     // create a date field of yyyymmdd

        long shortDate = (calMonth * 100) + calDay;                       // create a date field of mmdd for customs

        int todayTime = (calHr * 100) + calMin;               // hhmm (CT)


        //
        //  Check if these tee times are still 'in use' and still in use by this user??
        //
        String inUseError = checkInUseBy(con, parm, session, user, club);

        if (!inUseError.equals("")) {    // if one of the time slots are in use and not by this user
            
            boolean json_mode = ((req.getParameter("json_mode") != null) ? true : false);     // check this for debug info

            // Check to debug double bookings
            Utilities.logDebug("BSK", "DBC - Double booking catch error (" + Utilities.get_ms_timestamp(con) + ") - club: " + club + ", user: " + user + ", slots: " + slots
                    + ", Json: " +json_mode+ ", Error: " +inUseError+ ", date: " + date + ", fb: " + fb + ", course: " + course 
                    + ", time1: " + parm.time1 + ", time2: " + parm.time2 + ", time3: " + parm.time3 + ", time4: " + parm.time4 + ", time5: " + parm.time5);            
            
            out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H3>Tee Times Not Available</H3>");
            out.println("<BR><BR>Sorry, but one or more of the times you requested are no longer available.<BR>");
            out.println("<BR>It is possible the request timed out. &nbsp;Tee times must be completed within 6 minutes.");
            out.println("<BR><BR><strong>NOTE:</strong> &nbsp;This error can also result when you leave the tee time page prior to"
                         + " completing the request.  &nbsp;If you return to the tee sheet, directly or via another tab or window, the tee times will be released.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form action=\"Member_jump\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            if (!returnCourse.equals("")) {    // if multi course club, get course to return to (ALL?)
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            }
            out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }

        in_use = parm.in_use;
        in_use_by = parm.in_use_by;
        parm.orig_by = orig_by;          // this is new tee time, so set this user as originator

        
        // Shift players up into blank slots, but only move them within their current tee time
        verifySlot.shiftUpM(parm);
        
        
        //
        //  determine # of groups and # of players requested
        //
        parm.slots = slots;
        i = 0;

        if (!parm.player1.equals("")) {
            i++;
        }
        if (!parm.player2.equals("")) {
            i++;
        }
        if (!parm.player3.equals("")) {
            i++;
        }
        if (!parm.player4.equals("")) {
            i++;
        }
        if (!parm.player5.equals("")) {
            i++;
        }
        if (!parm.player6.equals("")) {
            i++;
        }
        if (!parm.player7.equals("")) {
            i++;
        }
        if (!parm.player8.equals("")) {
            i++;
        }
        if (!parm.player9.equals("")) {
            i++;
        }
        if (!parm.player10.equals("")) {
            i++;
        }
        if (!parm.player11.equals("")) {
            i++;
        }
        if (!parm.player12.equals("")) {
            i++;
        }
        if (!parm.player13.equals("")) {
            i++;
        }
        if (!parm.player14.equals("")) {
            i++;
        }
        if (!parm.player15.equals("")) {
            i++;
        }
        if (!parm.player16.equals("")) {
            i++;
        }
        if (!parm.player17.equals("")) {
            i++;
        }
        if (!parm.player18.equals("")) {
            i++;
        }
        if (!parm.player19.equals("")) {
            i++;
        }
        if (!parm.player20.equals("")) {
            i++;
        }
        if (!parm.player21.equals("")) {
            i++;
        }
        if (!parm.player22.equals("")) {
            i++;
        }
        if (!parm.player23.equals("")) {
            i++;
        }
        if (!parm.player24.equals("")) {
            i++;
        }
        if (!parm.player25.equals("")) {
            i++;
        }

        parm.players = i;
        players = i;             // save count for request

        //
        //  Make sure at least 1 player contains a name
        //
        if (players == 0) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>At least 1 Player field must contain a valid entry.");
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
        }

        //
        //  Make sure at least 1 player in the first 4 contains a name
        //
        if ((parm.player1.equals("")) && (parm.player2.equals("")) && (parm.player3.equals("")) && (parm.player4.equals(""))) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>At least 1 of the first 4 Player fields must contain a valid entry.");
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
        }

        if (club.equals("hartefeld") || club.equals("olympiafieldscc")) {        // if Hartefeld National or Olympia Fields - must be at least 2 players

            //
            //  Must be more than 1 player
            //
            if (players < 2) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Invalid Number of Players</H3>");
                out.println("<BR>Sorry, you are not allowed to reserve tee times with only one player.");
                out.println("<BR><BR>Please add more players or contact the golf shop for assistance.");
                out.println("<BR><BR>");

                goReturn(out, parm);
                return;
            }
        }

        //
        //  At least 1 Player field is present - Make sure a C/W was specified for all players
        //
        if ((!parm.player1.equals("") && parm.pcw1.equals(""))
                || (!parm.player2.equals("") && !parm.player2.equalsIgnoreCase("X") && parm.pcw2.equals(""))
                || (!parm.player3.equals("") && !parm.player3.equalsIgnoreCase("X") && parm.pcw3.equals(""))
                || (!parm.player4.equals("") && !parm.player4.equalsIgnoreCase("X") && parm.pcw4.equals(""))
                || (!parm.player5.equals("") && !parm.player5.equalsIgnoreCase("X") && parm.pcw5.equals(""))
                || (!parm.player6.equals("") && !parm.player6.equalsIgnoreCase("X") && parm.pcw6.equals(""))
                || (!parm.player7.equals("") && !parm.player7.equalsIgnoreCase("X") && parm.pcw7.equals(""))
                || (!parm.player8.equals("") && !parm.player8.equalsIgnoreCase("X") && parm.pcw8.equals(""))
                || (!parm.player9.equals("") && !parm.player9.equalsIgnoreCase("X") && parm.pcw9.equals(""))
                || (!parm.player10.equals("") && !parm.player10.equalsIgnoreCase("X") && parm.pcw10.equals(""))
                || (!parm.player11.equals("") && !parm.player11.equalsIgnoreCase("X") && parm.pcw11.equals(""))
                || (!parm.player12.equals("") && !parm.player12.equalsIgnoreCase("X") && parm.pcw12.equals(""))
                || (!parm.player13.equals("") && !parm.player13.equalsIgnoreCase("X") && parm.pcw13.equals(""))
                || (!parm.player14.equals("") && !parm.player14.equalsIgnoreCase("X") && parm.pcw14.equals(""))
                || (!parm.player15.equals("") && !parm.player15.equalsIgnoreCase("X") && parm.pcw15.equals(""))
                || (!parm.player16.equals("") && !parm.player16.equalsIgnoreCase("X") && parm.pcw16.equals(""))
                || (!parm.player17.equals("") && !parm.player17.equalsIgnoreCase("X") && parm.pcw17.equals(""))
                || (!parm.player18.equals("") && !parm.player18.equalsIgnoreCase("X") && parm.pcw18.equals(""))
                || (!parm.player19.equals("") && !parm.player19.equalsIgnoreCase("X") && parm.pcw19.equals(""))
                || (!parm.player20.equals("") && !parm.player20.equalsIgnoreCase("X") && parm.pcw20.equals(""))
                || (!parm.player21.equals("") && !parm.player21.equalsIgnoreCase("X") && parm.pcw21.equals(""))
                || (!parm.player22.equals("") && !parm.player22.equalsIgnoreCase("X") && parm.pcw22.equals(""))
                || (!parm.player23.equals("") && !parm.player23.equalsIgnoreCase("X") && parm.pcw23.equals(""))
                || (!parm.player24.equals("") && !parm.player24.equalsIgnoreCase("X") && parm.pcw24.equals(""))
                || (!parm.player25.equals("") && !parm.player25.equalsIgnoreCase("X") && parm.pcw25.equals(""))) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>You must specify a Mode of Transportation option for all players.");
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
        }

        //
        //  parm block to hold the club parameters
        //
        parmClub parm2 = new parmClub(0, con);

        //
        //   Get the guest names specified for this club
        //
        try {

            parm2.club = club;                   // set club name
            parm2.course = course;               // and course name

            getClub.getParms(con, parm2);        // get the club parms

        } catch (Exception ignore) {
        }

        //
        //  Make sure there are no duplicate names.
        //  Also, Parse the names to separate first, last & mi
        //  (Member does not verify single tokens - check for guest)
        //
        error = parseNames(out, parm, parm2, con);               // process members and guests

        if (error == true) {

            if (!parm.gplayer.equals("")) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Data Entry Error</H3>");
                out.println("<BR><BR><b>" + parm.gplayer + "</b> appears to have been manually entered or "
                        + "<br>modified after selecting a different guest from the Guest Selection window.");
                out.println("<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' ");
                out.println("<BR>next to the current guest's name, then click the desired guest type from the Guest ");
                out.println("<BR>Types list, and finally select a guest from the displayed guest selection window.<br>");

                goReturn(out, parm);

            } else {
                return;           // exit if error encountered and reported
            }
        }


        // Ensure that Pro-Only modes of transportation aren't being used without permission
        if (!checkTmodes(con, out, parm)) {

            out.println(SystemUtils.HeadTitle("Access Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Access Error</H3>");
            out.println("<BR><BR><b>'" + parm.player + "'</b> is not authorized to use that mode of transportation.");
            out.println("<BR><BR>Please select another mode of transportation.");
            out.println("<BR>Contact your club if you require assistance with restricted modes of transportation.");
            out.println("<BR><BR>");

            goReturn(out, parm);
            return;
        }

        //
        //  make sure that position 1 is not a guest
        //
        if (!parm.g[0].equals("") && !club.startsWith("tpc")) {           // allow for TPCs - custom in place to check for unaccomp guests

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Player position #1 contains a guest.");
            out.println("<BR><BR>The first player position must contain a member name.");
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
        }
        
        // Make sure there's at least one real player in each group.
        if (!club.equals("mosscreek") && !club.equals("colletonriverclub")) {
            
            for (int j = 0; j < parm.slots; j++) {

                parmSlot slotParmTemp = parm.getSingleParmSlot(j, req);

                if (slotParmTemp.hasPlayers() && !slotParmTemp.hasRealPlayers()) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Data Entry Error</H3>");
                    out.println("<BR><BR>Required field has not been completed or is invalid.");
                    out.println("<BR><BR>At least one player field must contain a name.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;
                }
            }
        }

        //
        //  Save the members' usernames for guest association
        //
        memA[0] = parm.user1;
        memA[1] = parm.user2;
        memA[2] = parm.user3;
        memA[3] = parm.user4;
        memA[4] = parm.user5;
        memA[5] = parm.user6;
        memA[6] = parm.user7;
        memA[7] = parm.user8;
        memA[8] = parm.user9;
        memA[9] = parm.user10;
        memA[10] = parm.user11;
        memA[11] = parm.user12;
        memA[12] = parm.user13;
        memA[13] = parm.user14;
        memA[14] = parm.user15;
        memA[15] = parm.user16;
        memA[16] = parm.user17;
        memA[17] = parm.user18;
        memA[18] = parm.user19;
        memA[19] = parm.user20;
        memA[20] = parm.user21;
        memA[21] = parm.user22;
        memA[22] = parm.user23;
        memA[23] = parm.user24;
        memA[24] = parm.user25;

        //
        //  Check if any of the names are invalid. 
        //
        if (parm.inval25 != 0) {

            err_name = parm.player25;
        }
        if (parm.inval24 != 0) {

            err_name = parm.player24;
        }
        if (parm.inval23 != 0) {

            err_name = parm.player23;
        }
        if (parm.inval22 != 0) {

            err_name = parm.player22;
        }
        if (parm.inval21 != 0) {

            err_name = parm.player21;
        }
        if (parm.inval20 != 0) {

            err_name = parm.player20;
        }
        if (parm.inval19 != 0) {

            err_name = parm.player19;
        }
        if (parm.inval18 != 0) {

            err_name = parm.player18;
        }
        if (parm.inval17 != 0) {

            err_name = parm.player17;
        }
        if (parm.inval16 != 0) {

            err_name = parm.player16;
        }
        if (parm.inval15 != 0) {

            err_name = parm.player15;
        }
        if (parm.inval14 != 0) {

            err_name = parm.player14;
        }
        if (parm.inval13 != 0) {

            err_name = parm.player13;
        }
        if (parm.inval12 != 0) {

            err_name = parm.player12;
        }
        if (parm.inval11 != 0) {

            err_name = parm.player11;
        }
        if (parm.inval10 != 0) {

            err_name = parm.player10;
        }
        if (parm.inval9 != 0) {

            err_name = parm.player9;
        }
        if (parm.inval8 != 0) {

            err_name = parm.player8;
        }
        if (parm.inval7 != 0) {

            err_name = parm.player7;
        }
        if (parm.inval6 != 0) {

            err_name = parm.player6;
        }
        if (parm.inval5 != 0) {

            err_name = parm.player5;
        }
        if (parm.inval4 != 0) {

            err_name = parm.player4;
        }
        if (parm.inval3 != 0) {

            err_name = parm.player3;
        }
        if (parm.inval2 != 0) {

            err_name = parm.player2;
        }
        if (parm.inval1 != 0) {

            err_name = parm.player1;
        }

        if (!err_name.equals("")) {      // invalid name received

            out.println(SystemUtils.HeadTitle("Player Not Found - Prompt"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Player's Name Not Found in System</H3><BR>");
            out.println("<BR><BR>Warning:  " + err_name + " does not exist in the system database.");
            out.println("<BR><BR>Please use the correct name (from Member List) or specify a guest.");
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
        }
            
        /*    
        //  Check each player's mship to see if it has permission to be a part of reservations for this activity (mship has an entry in mship5 for this activity)
        if (verifySlot.checkMemberAccessM(parm, con)) {          // if problem with player mship access

            out.println(SystemUtils.HeadTitle("Membership Restricted"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Membership Restricted</H3>");
            out.println("<BR><BR>Sorry, <b>" + parm.player + "</b> is not allowed to be a part of Golf reservations due to membership privileges.");
            out.println("<BR><BR>Please remove this player and submit the reservation again.");
            out.println("<BR><BR>");

            goReturn(out, parm);
            return;
        }
        */

        //
        //  Check for too many X's per group and if it is too late to specify X's
        //
        error = checkXcount(out, parm, parm2);    // check the X's

        if (error == true) {

            return;                                     // exit if error encountered and reported
        }

        //
        //************************************************************************
        //  Check any membership types for max rounds per week, month or year
        //  Also, check for 'days in advance' limits based on membership types
        //************************************************************************
        //
        if (!parm.mship1.equals("")
                || !parm.mship2.equals("")
                || !parm.mship3.equals("")
                || !parm.mship4.equals("")
                || !parm.mship5.equals("")
                || !parm.mship6.equals("")
                || !parm.mship7.equals("")
                || !parm.mship8.equals("")
                || !parm.mship9.equals("")
                || !parm.mship10.equals("")
                || !parm.mship11.equals("")
                || !parm.mship12.equals("")
                || !parm.mship13.equals("")
                || !parm.mship14.equals("")
                || !parm.mship15.equals("")
                || !parm.mship16.equals("")
                || !parm.mship17.equals("")
                || !parm.mship18.equals("")
                || !parm.mship19.equals("")
                || !parm.mship20.equals("")
                || !parm.mship21.equals("")
                || !parm.mship22.equals("")
                || !parm.mship23.equals("")
                || !parm.mship24.equals("")
                || !parm.mship25.equals("")) {                // if at least one name exists then check number of rounds

            //
            // *******************************************************************************
            //  Check Membership Restrictions for Max Rounds
            // *******************************************************************************
            //
            check = checkMemship(con, out, parm, day);      // go check

            if (parm.error == true) {          // if we hit a db error

                return;
            }

            if (check == true) {      // a member exceed the max allowed tee times per month

                out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><H3>Member Exceeded Limit</H3><BR>");
                out.println("<BR><BR>Warning:  " + parm.player + " is a " + parm.mship + " member and has exceeded the<BR>");
                out.println("maximum number of tee times allowed for this " + parm.period + ".");
                out.println("<BR><BR>");
                //
                //  Return to _lott
                //
                goReturn(out, parm);
                return;
            }

            //
            // *******************************************************************************
            //  Check Membership Restrictions for Days in Advance Limits
            // *******************************************************************************
            //
            check = checkDaysInAdv(parm, out, con);      // go check

            if (parm.error == true) {          // if we hit a db error

                return;

            } else {

                //
                //  Lakewood CC - if more than 1 day in advance, there must be at least one Primary Member in the
                //                group.  That is, there cannot be spouses only.
                //
                if (club.equals("lakewood") && parm.player.equals("Spouse Error")) {    // if error found

                    out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                    out.println("<BODY><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><BR><H3>Days in Advance Exceeded for Spouse</H3><BR>");
                    out.println("<BR>Sorry, at least one Member must be included in the group when");
                    out.println("<BR>scheduling a tee time more than 1 day in advance.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;
                }

                //
                //  Catamount Ranch - check if non-Founder player has more than 5 tee times already
                //
                if (club.equals("catamount") && parm.player.startsWith("Founder Error")) {    // if error found

                    StringTokenizer tok = new StringTokenizer(parm.player, "/");   // parse the error message

                    parm.player = tok.nextToken();     // skip Founder Error
                    parm.player = tok.nextToken();     // get Player name

                    out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                    out.println("<BODY><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><BR><H3>Member Already Has Max Allowed Advance Requests</H3>");
                    out.println("<BR>Sorry, " + parm.player + " already has 14 advance tee time requests scheduled.<br><br>");
                    out.println("Please remove this player from your request.  Contact the golf shop if you have any questions.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;
                }

                //
                //  Sonnenalp - check if player has more than 12 tee times already
                //
                if (club.equals("sonnenalp") && parm.player.startsWith("Sonnenalp Advance Error")) {    // if error found

                    StringTokenizer tok = new StringTokenizer(parm.player, "/");   // parse the error message

                    parm.player = tok.nextToken();     // skip Advance Error
                    parm.player = tok.nextToken();     // get Player name

                    out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                    out.println("<BODY><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><BR><H3>Member Already Has Max Allowed Advance Requests</H3>");
                    out.println("<BR>Sorry, " + parm.player + " already has 12 advance tee time requests scheduled.<br><br>");
                    out.println("Please remove this player from your request.  Contact the golf shop if you have any questions.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;
                }
            }
        }      // end of mship if


        //
        //   Inverness Club - if more than 30 days out - must have a guest in the tee time
        //
        if (club.equals("invernessclub") && ind > 30 && parm.guests == 0) {

            out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Days in Advance Exceeded for Member</H3><BR>");
            out.println("<BR><BR>Sorry, you cannot reserve a tee time more than 30 days <br>");
            out.println("in advance unless there is at least one guest included.");
            out.println("<BR><BR>");

            goReturn(out, parm);
            return;
        }
        
        if (club.equals("elgincc") && parm.guests > 7
                && ((parm.day.equals("Saturday") && parm.ind > 3) // 3 days in advance on Saturdays
                || (parm.day.equals("Sunday") && parm.ind > 4) // 4 days on Sun
                || (parm.day.equals("Monday") && parm.ind > 5) // 5 days on Mon
                || (parm.ind > 7))) {
            
            out.println(SystemUtils.HeadTitle("Max Guest Limit Exceeded"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Max Guest Limit Exceeded</H3><BR>");
            out.println("<BR><BR>Sorry, advance tee times cannot contain more than 7 guests for a particular member.");
            out.println("<BR><BR>Please contact the golf shop if you have any questions.");
            out.println("<BR><BR>");

            goReturn(out, parm);
            return;
        }

        //
        //************************************************************************
        //  Check for max # of guests exceeded (per member or per tee time)
        //************************************************************************
        //
        if (parm.guests != 0) {      // if any guests were included

            int guests = 0;                  // init # of guests count

            if (fb == 0) {                   // is Tee time for Front 9?

                sfb = "Front";
            }

            if (fb == 1) {                   // is it Back 9?

                sfb = "Back";
            }

            String per = "Member";

            try {

                PreparedStatement pstmt5 = con.prepareStatement(
                        "SELECT * "
                        + "FROM guestres2 "
                        + "WHERE sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? AND activity_id = 0");

                pstmt5.clearParameters();        // clear the parms
                pstmt5.setLong(1, date);
                pstmt5.setLong(2, date);
                pstmt5.setInt(3, time);
                pstmt5.setInt(4, time);
                rs = pstmt5.executeQuery();      // execute the prepared stmt

                loop1:
                while (rs.next()) {

                    rest_name = rs.getString("name");
                    grest_recurr = rs.getString("recurr");
                    grest_num = rs.getInt("num_guests");
                    rcourse = rs.getString("courseName");
                    rest_fb = rs.getString("fb");
                    per = rs.getString("per");

                    // now look up the guest types for this restriction
                    PreparedStatement pstmt2 = con.prepareStatement(
                            "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

                    pstmt2.clearParameters();
                    pstmt2.setInt(1, rs.getInt("id"));

                    ResultSet rs2 = pstmt2.executeQuery();

                    while (rs2.next()) {

                        rguest.add(rs2.getString("guest_type"));

                    }

                    pstmt2.close();

                    /*
                    rguest[0] = rs.getString("guest1");
                    rguest[1] = rs.getString("guest2");
                    rguest[2] = rs.getString("guest3");
                    rguest[3] = rs.getString("guest4");
                    rguest[4] = rs.getString("guest5");
                    rguest[5] = rs.getString("guest6");
                    rguest[6] = rs.getString("guest7");
                    rguest[7] = rs.getString("guest8");
                    rguest[8] = rs.getString("guest9");
                    rguest[9] = rs.getString("guest10");
                    rguest[10] = rs.getString("guest11");
                    rguest[11] = rs.getString("guest12");
                    rguest[12] = rs.getString("guest13");
                    rguest[13] = rs.getString("guest14");
                    rguest[14] = rs.getString("guest15");
                    rguest[15] = rs.getString("guest16");
                    rguest[16] = rs.getString("guest17");
                    rguest[17] = rs.getString("guest18");
                    rguest[18] = rs.getString("guest19");
                    rguest[19] = rs.getString("guest20");
                    rguest[20] = rs.getString("guest21");
                    rguest[21] = rs.getString("guest22");
                    rguest[22] = rs.getString("guest23");
                    rguest[23] = rs.getString("guest24");
                    rguest[24] = rs.getString("guest25");
                    rguest[25] = rs.getString("guest26");
                    rguest[26] = rs.getString("guest27");
                    rguest[27] = rs.getString("guest28");
                    rguest[28] = rs.getString("guest29");
                    rguest[29] = rs.getString("guest30");
                    rguest[30] = rs.getString("guest31");
                    rguest[31] = rs.getString("guest32");
                    rguest[32] = rs.getString("guest33");
                    rguest[33] = rs.getString("guest34");
                    rguest[34] = rs.getString("guest35");
                    rguest[35] = rs.getString("guest36");
                     */
                    check = false;       // init 'check guests' flag

                    guests = 0;          // reset # of guests in tee time

                    //
                    //  Check if course matches that specified in restriction
                    //
                    if ((rcourse.equals("-ALL-")) || (rcourse.equals(course))) {

                        if (!verifySlot.checkRestSuspend(-99, rs.getInt("id"), 0, (int) date, time, day, course, con)) {        // check if this guest restriction is suspended for this time)

                            //
                            //  We must check the recurrence for this day (Monday, etc.) and guest types
                            //
                            //     parm.g[x] = guest types specified in player name fields
                            //     rguest[x] = guest types from restriction gotten above
                            //
                            if (grest_recurr.equalsIgnoreCase("every " + day)) {

                                if ((rest_fb.equals("Both")) || (rest_fb.equals(sfb))) {

                                    i = 0;
                                    while (i < 25) {                        // check all possible players

                                        if (!parm.g[i].equals("")) {      // if player is a guest

                                            i2 = 0;
                                            gloop1:
                                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                                                if (rguest.get(i2).equals(parm.g[i])) {

                                                    check = true;                  // indicate check num of guests
                                                    guests++;                      // bump number of restricted guests in tee time
                                                    break gloop1;
                                                }
                                                i2++;
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }

                            if (grest_recurr.equalsIgnoreCase("every day")) {   // if everyday

                                if ((rest_fb.equals("Both")) || (rest_fb.equals(sfb))) {

                                    i = 0;
                                    while (i < 25) {                        // check all possible players

                                        if (!parm.g[i].equals("")) {      // if player is a guest

                                            i2 = 0;
                                            gloop2:
                                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                                                if (rguest.get(i2).equals(parm.g[i])) {

                                                    check = true;                  // indicate check num of guests
                                                    guests++;                      // bump number of restricted guests in tee time
                                                    break gloop2;
                                                }
                                                i2++;
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }

                            if ((grest_recurr.equalsIgnoreCase("all weekdays"))
                                    && (!day.equalsIgnoreCase("saturday"))
                                    && (!day.equalsIgnoreCase("sunday"))) {

                                if ((rest_fb.equals("Both")) || (rest_fb.equals(sfb))) {

                                    i = 0;
                                    while (i < 25) {                        // check all possible players

                                        if (!parm.g[i].equals("")) {      // if player is a guest

                                            i2 = 0;
                                            gloop3:
                                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                                                if (rguest.get(i2).equals(parm.g[i])) {

                                                    check = true;                  // indicate check num of guests
                                                    guests++;                      // bump number of restricted guests in tee time
                                                    break gloop3;
                                                }
                                                i2++;
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }

                            //
                            //  if Weekends and its Saturday
                            //
                            if ((grest_recurr.equalsIgnoreCase("all weekends"))
                                    && (day.equalsIgnoreCase("saturday"))) {

                                if ((rest_fb.equals("Both")) || (rest_fb.equals(sfb))) {

                                    i = 0;
                                    while (i < 25) {                        // check all possible players

                                        if (!parm.g[i].equals("")) {      // if player is a guest

                                            i2 = 0;
                                            gloop4:
                                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                                                if (rguest.get(i2).equals(parm.g[i])) {

                                                    check = true;                  // indicate check num of guests
                                                    guests++;                      // bump number of restricted guests in tee time
                                                    break gloop4;
                                                }
                                                i2++;
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }

                            //
                            //  if Weekends and its Sunday
                            //
                            if ((grest_recurr.equalsIgnoreCase("all weekends"))
                                    && (day.equalsIgnoreCase("sunday"))) {

                                if ((rest_fb.equals("Both")) || (rest_fb.equals(sfb))) {

                                    i = 0;
                                    while (i < 25) {                        // check all possible players

                                        if (!parm.g[i].equals("")) {      // if player is a guest

                                            i2 = 0;
                                            gloop5:
                                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                                                if (rguest.get(i2).equals(parm.g[i])) {

                                                    check = true;                  // indicate check num of guests
                                                    guests++;                      // bump number of restricted guests in tee time
                                                    break gloop5;
                                                }
                                                i2++;
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }
                        }
                    }      // end of IF course matches

                    if (check == true && guests > 0) {   // if restriction exists for this day and time

                        // if num of guests req'd (guests) > num allowed (grest_num) per member or per tee time (slot)
                        //
                        //       to get here guests is > 0
                        //       grest_num is 0 - 4 (max allowed)
                        //       memg1-5 is number of members for each group/slot
                        //       parm.guestsg1-5 is number of guests for each group/slot
                        //
                        float xguests = 0;               // number of guests per member requested
                        int memcount = 0;
                        guestError = false;              // init error flag

                        if (per.equals("Member")) {       // if restriction is per member

                            memcount = parm.memg1 + parm.memg2 + parm.memg3 + parm.memg4 + parm.memg5;  // total members in req

                            if (memcount == 0) {

                                guestError = true;
                                break loop1;

                            } else {

                                xguests = guests / memcount;          // # of guests per member (in entire request)

                                if (xguests > grest_num) {            // too many guests per member?

                                    guestError = true;
                                    break loop1;
                                }
                            }

                        } else {          // restriction is per tee time

                            if (parm.guestsg1 > grest_num || parm.guestsg2 > grest_num || parm.guestsg3 > grest_num
                                    || parm.guestsg4 > grest_num || parm.guestsg5 > grest_num) {

                                guestError = true;
                                break loop1;
                            }
                        }
                    }

                }   // end of loop1 while loop

                pstmt5.close();

                if (guestError == true) {         // if too many guests

                    out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
                    out.println("<BR><BR>Sorry, the maximum number of guests allowed for the<BR>");
                    out.println("time you are requesting is " + grest_num + " per " + per + ".");
                    out.println("<BR><BR>Guest Restriction = " + rest_name);
                    out.println("<BR><BR>");
                    //
                    //  Return to _lott
                    //
                    goReturn(out, parm);
                    return;
                }
            } catch (Exception e5) {

                dbError(out, e5);
                return;
            }


            //
            //  Guest requested - if Wilmington, check for maxx guests
            //
            if (club.equals("wilmington")) {

                check = checkWilGuests(con, parm);

                if (check == true) {      // more than 12 guests

                    out.println(SystemUtils.HeadTitle("Max Number of Guests Exceeded - Reject"));
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><BR><H3>Maximum Number of Guests Exceeded</H3>");
                    out.println("<BR>Sorry, but there are already 12 guests scheduled today.");
                    out.println("<BR>No more than 12 guests are allowed during the selected time period.  This request would exceed that total.");
                    out.println("<BR><BR>Please remove one or more guests, or try another time of the day.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;
                }
            }      // end of if Wilmington


            //
            //  If Sonnenalp - we know we have guests so go get their rates to be displayed on the tee sheet (saved in custom_dispx)
            //
//            if (club.equals("sonnenalp")) {           // if Sonnenalp
//
//                verifyCustom.addGuestRatesM(parm);         // get rates for each guest
//            }

            //
            //  At least 1 guest requested in tee time.  If The Lakes, then no guests allowed before 11 AM if more than 5 days in advance.
            //
            if (club.equals("lakes")) {

                if (parm.ind == 7 && todayTime < 930) {   // if 7 days in advance and earlier than 7:30 AM PT

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
                    out.println("<BR><BR>Sorry, you cannot book guest times before 7:30 AM when booking 7 days in advance.");
                    out.println("<BR><BR>Please remove the guests or return to the tee sheet.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;

                } else if (parm.ind > 5 && parm.time1 < 1101) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
                    out.println("<BR><BR>Sorry, guests are not allowed this far in advance until after 11:00 AM.");
                    out.println("<BR><BR>Please remove the guests or return to the tee sheet.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;
                }
            }     // end of IF The Lakes


        }      // end of if guests

        //
        // *******************************************************************************
        //  Check Member Restrictions
        // *******************************************************************************
        //
        check = checkMemRes(con, out, parm, day);      // go check

        if (parm.error == true) {          // if we hit a db error

            return;
        }

        if (check == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" + parm.player + "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + parm.rest_name + "</b><br><br>");
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
        }

        //
        // *******************************************************************************
        //  Check Member Number restrictions
        //
        // *******************************************************************************
        //
        check = checkMemNum(con, out, parm, day);      // go check

        if (parm.error == true) {          // if we hit a db error

            return;
        }

        if (check == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted by Member Number</H3><BR>");
            out.println("<BR>Sorry, ");
            if (!parm.pNum1.equals("")) {
                out.println("<b>" + parm.pNum1 + "</b> ");
            }
            if (!parm.pNum2.equals("")) {
                out.println("<b>" + parm.pNum2 + "</b> ");
            }
            if (!parm.pNum3.equals("")) {
                out.println("<b>" + parm.pNum3 + "</b> ");
            }
            if (!parm.pNum4.equals("")) {
                out.println("<b>" + parm.pNum4 + "</b> ");
            }
            if (!parm.pNum5.equals("")) {
                out.println("<b>" + parm.pNum5 + "</b> ");
            }
            if (!parm.pNum6.equals("")) {
                out.println("<b>" + parm.pNum6 + "</b> ");
            }
            if (!parm.pNum7.equals("")) {
                out.println("<b>" + parm.pNum7 + "</b> ");
            }
            if (!parm.pNum8.equals("")) {
                out.println("<b>" + parm.pNum8 + "</b> ");
            }
            if (!parm.pNum9.equals("")) {
                out.println("<b>" + parm.pNum9 + "</b> ");
            }
            if (!parm.pNum10.equals("")) {
                out.println("<b>" + parm.pNum10 + "</b> ");
            }
            if (!parm.pNum11.equals("")) {
                out.println("<b>" + parm.pNum11 + "</b> ");
            }
            if (!parm.pNum12.equals("")) {
                out.println("<b>" + parm.pNum12 + "</b> ");
            }
            if (!parm.pNum13.equals("")) {
                out.println("<b>" + parm.pNum13 + "</b> ");
            }
            if (!parm.pNum14.equals("")) {
                out.println("<b>" + parm.pNum14 + "</b> ");
            }
            if (!parm.pNum15.equals("")) {
                out.println("<b>" + parm.pNum15 + "</b> ");
            }
            if (!parm.pNum16.equals("")) {
                out.println("<b>" + parm.pNum16 + "</b> ");
            }
            if (!parm.pNum17.equals("")) {
                out.println("<b>" + parm.pNum17 + "</b> ");
            }
            if (!parm.pNum18.equals("")) {
                out.println("<b>" + parm.pNum18 + "</b> ");
            }
            if (!parm.pNum19.equals("")) {
                out.println("<b>" + parm.pNum19 + "</b> ");
            }
            if (!parm.pNum20.equals("")) {
                out.println("<b>" + parm.pNum20 + "</b> ");
            }
            if (!parm.pNum21.equals("")) {
                out.println("<b>" + parm.pNum21 + "</b> ");
            }
            if (!parm.pNum22.equals("")) {
                out.println("<b>" + parm.pNum22 + "</b> ");
            }
            if (!parm.pNum23.equals("")) {
                out.println("<b>" + parm.pNum23 + "</b> ");
            }
            if (!parm.pNum24.equals("")) {
                out.println("<b>" + parm.pNum24 + "</b> ");
            }
            if (!parm.pNum25.equals("")) {
                out.println("<b>" + parm.pNum25 + "</b> ");
            }
            out.println("is/are restricted from playing during this time because the");
            out.println("<BR> number of members with the same member number has exceeded the maximum allowed.");
            out.println("<br><br>This time slot has the following restriction:  <b>" + parm.rest_name + "</b>");
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
        }


        //
        //  Custom Restriction Processing
        //
        hit = false;

        if (club.equals("peninsula")) {    // Peninsula Club - juniors must be accompanied by an adult at all times

            //
            //  Check for any dependents
            //
            hit = chkPeninsulaJuniors(parm);

            if (hit == true) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><BR><H3>Junior Without An Adult</H3>");
                out.println("<BR><BR>Sorry, junior members must be accompanied by an adult at all times.");
                out.println("<BR><BR>Please add an adult player or return to the tee sheet.");
                out.println("<BR><BR>If you have any questions, please contact your golf shop staff.");
                out.println("<BR><BR>");

                goReturn(out, parm);
                return;
            }
        }

        if (club.equals("awbreyglen")) {

            //
            //  Check for any dependents
            //
            hit = chkAwbreyJuniors(parm);

            if (hit == true) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><BR><H3>Junior Without An Adult</H3>");
                out.println("<BR><BR>Sorry, Juniors must be accompanied by an adult at all times.");
                out.println("<BR>Juniors 12 and Over must be accompanied by an adult before Noon.");
                out.println("<BR><BR>Please add an adult player or return to the tee sheet.");
                out.println("<BR><BR>If you have any questions, please contact your golf shop staff.");
                out.println("<BR><BR>");

                goReturn(out, parm);
                return;
            }
        }


        //
        //  Minikahda, check for maxx guest times per hour and other custom guest restrictions
        //
        /*
        if (club.equals("minikahda")) {

            int thisTime = SystemUtils.getTime(con);               // get the current adjusted time

            if (ind > 3 || (ind == 3 && thisTime < 800)) {        // if beyond normal days in adv (guest times only)

                if ((day.equals("Saturday") || day.equals("Sunday")
                        || date == Hdate1 || date == Hdate2b || date == Hdate3) && parm.time1 < 1000) {      // nothing allowed if w/e or holiday and before 10 AM

                    out.println(SystemUtils.HeadTitle("Invalid Request - Reject"));
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><H3>Invalid Tee Time Request</H3><BR>");
                    out.println("<BR><BR>Sorry, you are not allowed to book a tee time this far in advance on weekends and holidays before 10:00 AM.");
                    out.println("<BR><BR>These times can be reserved up to 3 days in advance.");
                    out.println("<BR><BR>");
                    goReturn(out, parm);
                    return;

                } else {

                    if (day.equals("Thursday") && parm.time1 < 1500 && parm.guests > 0
                            && shortDate > 521 && shortDate < 905) {                                  // No guests allowed on Thurs before 3:00 PM

                        out.println(SystemUtils.HeadTitle("Invalid Request - Reject"));
                        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><H3>Invalid Tee Time Request</H3><BR>");
                        out.println("<BR><BR>Sorry, guests are not allowed before 3:00 PM on Thursdays.");
                        out.println("<BR><BR>Please remove the guest(s) or select another time.");
                        out.println("<BR><BR>");
                        goReturn(out, parm);
                        return;

                    } else {

                        //
                        //  Must be 1 member and 3 guests (only)
                        //
                        check = checkMiniGuestsT(con, parm);

                        if (check == true) {      // if not 1 member & 3 guests in each tee time

                            out.println(SystemUtils.HeadTitle("Invalid Guest Time - Reject"));
                            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                            out.println("<hr width=\"40%\">");
                            out.println("<BR><H3>Invalid Guest Time Request</H3><BR>");
                            out.println("<BR><BR>Sorry, only guest times are accepted at this time.");
                            out.println("<BR><BR>Your request must contain 1 member and 3 guests in each tee time.");
                            out.println("<BR><BR>");
                            goReturn(out, parm);
                            return;

                        } else {

                            //
                            //   Now check to see if there are already 2 guest times this hour.
                            //
                            check = checkMiniGuests(con, parm);

                            if (check == true) {      // more than 2 guest times this hour

                                out.println(SystemUtils.HeadTitle("Invalid Guest Time - Reject"));
                                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                                out.println("<hr width=\"40%\">");
                                out.println("<BR><H3>Invalid Guest Time Request</H3><BR>");
                                out.println("<BR><BR>Sorry, there are already 2 guest times scheduled this hour.");
                                out.println("<BR><BR>Please select a different time of the day.");
                                out.println("<BR><BR>");
                                goReturn(out, parm);
                                return;
                            }
                        }
                    }
                }

            } else {    // within normal days in adv

                //
                //  If 1 member and 3 guests, then check for max already requested during this hour (other combinations are allowed per normal restrictions)
                //
                check = checkMiniGuests(con, parm);

                if (check == true) {      // more than 2 guest times this hour

                    out.println(SystemUtils.HeadTitle("Invalid Guest Time - Reject"));
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><H3>Invalid Guest Time Request</H3><BR>");
                    out.println("<BR><BR>Sorry, there are already 2 guest times scheduled this hour.");
                    out.println("<BR><BR>Please select a different time of the day.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;
                }
            }

        }      // end of if Minikahda
        */


        //
        //***********************************************************************************************
        //
        //    Now check if any of the players are already scheduled today (club5.rndsperday)
        //
        //***********************************************************************************************
        //
        hit = false;
        
        for (int j = 0; j < 25; j++) {
            
            if (!parm.getPlayer(j).equals("") && !parm.getPlayer(j).equalsIgnoreCase("X") && parm.g[j].equals("") 
                    && checkPlayer(parm.getUser(j), parm, club, req) >= parm2.rnds) {
                player = parm.getPlayer(j);
                hit = true;
                break;
            }
        }
        
/////////   **** This code has been replaced by the loop above **** //////////
//        
//        if (!parm.player1.equals("") && !parm.player1.equalsIgnoreCase("X") && parm.g[0].equals("")) {
//
//            player = parm.player1;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user1, parm, club, req) >= parm2.rnds);
////            hit = (checkPlayer(parm.user1, parm, club, req) >= parm2.rnds || checkPlayerProxim(parm.user1, parm, club, req));  // Need to figure out correct time
//        }
//
//        if (!parm.player2.equals("") && !parm.player2.equalsIgnoreCase("X") && parm.g[1].equals("") && hit == false) {
//
//            player = parm.player2;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user2, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player3.equals("") && !parm.player3.equalsIgnoreCase("X") && parm.g[2].equals("") && hit == false) {
//
//            player = parm.player3;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user3, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player4.equals("") && !parm.player4.equalsIgnoreCase("X") && parm.g[3].equals("") && hit == false) {
//
//            player = parm.player4;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user4, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player5.equals("") && !parm.player5.equalsIgnoreCase("X") && parm.g[4].equals("") && hit == false) {
//
//            player = parm.player5;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user5, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player6.equals("") && !parm.player6.equalsIgnoreCase("X") && parm.g[5].equals("") && hit == false) {
//
//            player = parm.player6;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user6, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player7.equals("") && !parm.player7.equalsIgnoreCase("X") && parm.g[6].equals("") && hit == false) {
//
//            player = parm.player7;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user7, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player8.equals("") && !parm.player8.equalsIgnoreCase("X") && parm.g[7].equals("") && hit == false) {
//
//            player = parm.player8;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user8, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player9.equals("") && !parm.player9.equalsIgnoreCase("X") && parm.g[8].equals("") && hit == false) {
//
//            player = parm.player9;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user9, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player10.equals("") && !parm.player10.equalsIgnoreCase("X") && parm.g[9].equals("") && hit == false) {
//
//            player = parm.player10;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user10, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player11.equals("") && !parm.player11.equalsIgnoreCase("X") && parm.g[10].equals("") && hit == false) {
//
//            player = parm.player11;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user11, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player12.equals("") && !parm.player12.equalsIgnoreCase("X") && parm.g[11].equals("") && hit == false) {
//
//            player = parm.player12;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user12, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player13.equals("") && !parm.player13.equalsIgnoreCase("X") && parm.g[12].equals("") && hit == false) {
//
//            player = parm.player13;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user13, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player14.equals("") && !parm.player14.equalsIgnoreCase("X") && parm.g[13].equals("") && hit == false) {
//
//            player = parm.player14;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user14, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player15.equals("") && !parm.player15.equalsIgnoreCase("X") && parm.g[14].equals("") && hit == false) {
//
//            player = parm.player15;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user15, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player16.equals("") && !parm.player16.equalsIgnoreCase("X") && parm.g[15].equals("") && hit == false) {
//
//            player = parm.player16;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user16, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player17.equals("") && !parm.player17.equalsIgnoreCase("X") && parm.g[16].equals("") && hit == false) {
//
//            player = parm.player17;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user17, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player18.equals("") && !parm.player18.equalsIgnoreCase("X") && parm.g[17].equals("") && hit == false) {
//
//            player = parm.player18;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user18, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player19.equals("") && !parm.player19.equalsIgnoreCase("X") && parm.g[18].equals("") && hit == false) {
//
//            player = parm.player19;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user19, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player20.equals("") && !parm.player20.equalsIgnoreCase("X") && parm.g[19].equals("") && hit == false) {
//
//            player = parm.player20;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user20, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player21.equals("") && !parm.player21.equalsIgnoreCase("X") && parm.g[20].equals("") && hit == false) {
//
//            player = parm.player21;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user21, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player22.equals("") && !parm.player22.equalsIgnoreCase("X") && parm.g[21].equals("") && hit == false) {
//
//            player = parm.player22;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user22, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player23.equals("") && !parm.player23.equalsIgnoreCase("X") && parm.g[22].equals("") && hit == false) {
//
//            player = parm.player23;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user23, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player24.equals("") && !parm.player24.equalsIgnoreCase("X") && parm.g[23].equals("") && hit == false) {
//
//            player = parm.player24;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user24, parm, club, req) >= parm2.rnds);
//        }
//
//        if (!parm.player25.equals("") && !parm.player25.equalsIgnoreCase("X") && parm.g[24].equals("") && hit == false) {
//
//            player = parm.player25;              // get player for message
//
////            hit = chkPlayer(con, player, date, time, fb, course);
//            hit = (checkPlayer(parm.user25, parm, club, req) >= parm2.rnds);
//        }

        if (hit == true) {    // if we hit on a conflict

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
            out.println("<BR>Sorry, <b>" + player + "</b> is already scheduled to play their maximum allowed rounds for this date.<br><br>");
            out.println("A player can only be scheduled for " + parm2.rnds + " round(s) per day.<br><br>");
            out.println("<BR><BR>");
            
            //  Return to slot page
            goReturn(out, parm);
            return;
        }
        
        
        //
        //***************************************************************************************************************************
        //
        //    Now check if any of the players are already scheduled to play within too close of a time today (club5.hrsbtwn)
        //          ** Only check if hrsbtwn > 0, since there's no limitation if it's 0 **
        //
        //***************************************************************************************************************************
        //
        hit = false;
        
        if (parm2.hrsbtwn > 0) {
            for (int j = 0; j < 25; j++) {

                if (!parm.getPlayer(j).equals("") && !parm.getPlayer(j).equalsIgnoreCase("X") && parm.g[j].equals("")) {

                    int nearest = checkPlayerProxim(j, parm, club, req); 

                    if (nearest >= 0 && nearest < parm2.hrsbtwn) {
                        player = parm.getPlayer(j);
                        hit = true;
                        break;
                    }
                }
            }
        }

        if (hit == true) {    // if we hit on a conflict

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
            out.println("<BR>Sorry, <b>" + player + "</b> has an existing tee time within " + parm2.hrsbtwn + " hr" + (parm2.hrsbtwn > 1 ? "s":"") + " of this one.<br><br>");
            out.println("A player must have at least " + parm2.hrsbtwn + " hr" + (parm2.hrsbtwn > 1 ? "s":"") + " between all tee times.<br><br>");
            out.println("<BR><BR>");
            
            //  Return to slot page
            goReturn(out, parm);
            return;
        }


        //
        //***************************************************************************************************
        //
        //  CUSTOMS - check all possible customs here - those that are not dependent on guest info!!!!!!!!
        //
        //     verifyCustom.checkCustoms1 will process the individual custom and return any error message.
        //
        //    *** USE THIS FOR ALL FUTURE CUSTOMS WHEN APPROPRIATE !!!!!!!!!!!!!  ***   8/27/09
        //
        //***************************************************************************************************
        //
        String custErrorMsg = checkCustoms1(req, parm, user);     // go check for customs

        if (!custErrorMsg.equals("")) {         // if error encountered - reject

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR><BR><BR>");
            out.println("<hr width=\"40%\"><BR>");
            out.println(custErrorMsg);           // add custom error msg
            out.println("<BR><BR>");

            goReturn(out, parm);
            return;
        }

        //
        //  MOVE ANY APPROPRIATE CUSTOMS SO THEY USE ABOVE PROCESS !!!!!!!!!!!!!!
        //        


        //
        //  Check if user has approved of the member/guest sequence (guest association)
        //
        //  If this skip is set, then we've already been through these tests.
        //
        if (req.getParameter("skip8") == null) {

            //
            //***********************************************************************************************
            //
            //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
            //
            //        parm.members = # of members requested
            //        parm.guests  = # of guests requested
            //***********************************************************************************************
            //
            if (parm.guests > 0) {

                //
                //  Guests specified - determine guest owners by order
                //
                memberName = user;                       // default to this user in case no members

                for (gi = 0; gi < 25; gi++) {            // cycle thru arrays and find guests/members

                    if (!parm.gstA[gi].equals("")) {    // if player is a guest

                        usergA[gi] = memberName;           // get last member's username

                    } else {

                        usergA[gi] = "";                   // not a guest
                        memberName = memA[gi];             // get member's username
                    }
                }

                for (i = 0; i < 25; i++) {

                    userg[i] = usergA[i];             // set usernames for guests in teecurr
                    parm.userg[i] = usergA[i];        // save in parms
                }


                if (parm.members > 0) {             // if at least one member

                    if (parm.members > 1 || !parm.gstA[0].equals("")) {  // if slot 1 is a guest or multiple members

                        //
                        //  At least two members OR player1 is a guest.
                        //  Prompt user to verify the order.
                        //
                        if (new_skin) {

                            // Pull the arryas into local variable, incase we want to use them later
                            String[] player_a = parm.getPlayerArray(25);
                            String[] pcw_a = parm.getCwArray(25);
                            int[] p9_a = parm.getP9Array(25);
                            String[] userg_a = parm.userg;
                            int[] guest_id_a = parm.getGuestIdArray(25);

                            // Fill that field map with values that will be used when calling back
                            hidden_field_map.put("skip8", "yes");
                            hidden_field_map.put("date", parm.date);
                            hidden_field_map.put("time", parm.time1);
                            hidden_field_map.put("time%", parm.getTimeArray(15));
                            hidden_field_map.put("mm", parm.mm);
                            hidden_field_map.put("yy", parm.yy);
                            hidden_field_map.put("index", parm.index);
                            hidden_field_map.put("slots", parm.slots);
                            hidden_field_map.put("p5", parm.p5);
                            hidden_field_map.put("course", parm.course);
                            hidden_field_map.put("returnCourse", parm.returnCourse);
                            hidden_field_map.put("day", parm.day);
                            hidden_field_map.put("fb", parm.fb);
                            hidden_field_map.put("notes", parm.notes);
                            hidden_field_map.put("hide", parm.hides);
                            hidden_field_map.put("jump", parm.jump);
                            //hidden_field_map.put("displayOpt", displayOpt);
                            hidden_field_map.put("player%", player_a);
                            hidden_field_map.put("p%cw", pcw_a);
                            hidden_field_map.put("p9%", p9_a);
                            hidden_field_map.put("userg%", userg_a);
                            hidden_field_map.put("guest_id%", guest_id_a);
                            hidden_field_map.put("submitForm", "YES - continue");

                            // Build the player list
                            String player_list_html = "<ul class=\"indented_list\">";
                            for (int i3 = 0; i3 < player_a.length; i3++) {
                                if (!player_a[i3].equals("")) {
                                    player_list_html += "<li class=\"" + ((!userg_a[i3].equals("")) ? "guest_item" : "player_item") + "\">" + player_a[i3] + "</li>";
                                }
                            }
                            player_list_html += "</ul>";

                            // Fill the result map
                            result_map.put("title", "Player/Guest Association Prompt");
                            result_map.put("prompt_yes_no", true);
                            result_map.put("successful", false);
                            result_map.put("callback_map", hidden_field_map);

                            result_map.put("message_array", new String[]{
                                        "Guests should be specified <b>immediately after</b> the member they belong to.",
                                        "<b>Please verify the following order:</b>",
                                        player_list_html,
                                        "Would you like to process the request as is?"});

                            // Send results as json string
                            out.print(gson_obj.toJson(result_map));

                            out.close();
                            return;

                        } else {
                            out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
                            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                            out.println("<hr width=\"40%\">");
                            out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");
                            out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
                            out.println("Please verify that the following order is correct:");
                            out.println("<BR><BR>");
                            out.println(parm.player1 + " <BR>");
                            out.println(parm.player2 + " <BR>");
                            if (!parm.player3.equals("")) {
                                out.println(parm.player3 + " <BR>");
                            }
                            if (!parm.player4.equals("")) {
                                out.println(parm.player4 + " <BR>");
                            }
                            if (!parm.player5.equals("")) {
                                out.println(parm.player5 + " <BR>");
                            }
                            if (!parm.player6.equals("")) {
                                out.println(parm.player6 + " <BR>");
                            }
                            if (!parm.player7.equals("")) {
                                out.println(parm.player7 + " <BR>");
                            }
                            if (!parm.player8.equals("")) {
                                out.println(parm.player8 + " <BR>");
                            }
                            if (!parm.player9.equals("")) {
                                out.println(parm.player9 + " <BR>");
                            }
                            if (!parm.player10.equals("")) {
                                out.println(parm.player10 + " <BR>");
                            }
                            if (!parm.player11.equals("")) {
                                out.println(parm.player11 + " <BR>");
                            }
                            if (!parm.player12.equals("")) {
                                out.println(parm.player12 + " <BR>");
                            }
                            if (!parm.player13.equals("")) {
                                out.println(parm.player13 + " <BR>");
                            }
                            if (!parm.player14.equals("")) {
                                out.println(parm.player14 + " <BR>");
                            }
                            if (!parm.player15.equals("")) {
                                out.println(parm.player15 + " <BR>");
                            }
                            if (!parm.player16.equals("")) {
                                out.println(parm.player16 + " <BR>");
                            }
                            if (!parm.player17.equals("")) {
                                out.println(parm.player17 + " <BR>");
                            }
                            if (!parm.player18.equals("")) {
                                out.println(parm.player18 + " <BR>");
                            }
                            if (!parm.player19.equals("")) {
                                out.println(parm.player19 + " <BR>");
                            }
                            if (!parm.player20.equals("")) {
                                out.println(parm.player20 + " <BR>");
                            }
                            if (!parm.player21.equals("")) {
                                out.println(parm.player21 + " <BR>");
                            }
                            if (!parm.player22.equals("")) {
                                out.println(parm.player22 + " <BR>");
                            }
                            if (!parm.player23.equals("")) {
                                out.println(parm.player23 + " <BR>");
                            }
                            if (!parm.player24.equals("")) {
                                out.println(parm.player24 + " <BR>");
                            }
                            if (!parm.player25.equals("")) {
                                out.println(parm.player25 + " <BR>");
                            }
                            out.println("<BR>Would you like to process the request as is?");
                            //
                            //  Return to _lott to change the player order
                            //
                            goReturn2(out, parm);
                            return;
                        }

                    }   // end of IF guest name in slot #1 or multiple members

                }      // endof IF members included


            } else {    // NO guests in this request 

                //
                //  Oak Hill CC - Must be at least one guest in every request
                //
                if (club.equals("oakhillcc")) {           // if Oak Hill CC

                    out.println(SystemUtils.HeadTitle("Guest Error - Reject"));
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><BR><H3>Invalid Guest Time</H3>");
                    out.println("<BR>Sorry, but you are not allowed to request a time without at least one guest.");
                    out.println("<BR><BR>Please add one or more guests or return to the tee sheet.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    return;
                }           // end of IF Oak Hill CC             

            }      // end of IF any guests specified

        } else {   // skip 8 requested

            //
            //  User has responded to the guest association prompt - process tee time request in specified order
            //
            // for (i = 0; i < 25; i++) {
            userg = formUtil.getStringArrayFromReq(req, "userg%", 25, "");
            //userg[i] = req.getParameter("userg" + i);
            parm.userg = userg;                   // save in parms
            //}
        }         // end of IF skip8

        //
        //***********************************************************************************************
        //
        //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
        //
        //***********************************************************************************************
        //
        boolean guest_ass = false;

        //  Any guests assigned?
        for (i = 0; i < 25; i++) {
            if (!userg[i].equals("")) {
                guest_ass = true;
            }
        }

        if (guest_ass == true) {

            check = checkGuestQuota(parm, con);      // go check

            if (check == true) {          // if we hit on a violation

                out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
                out.println("<BR>Sorry, requesting <b>" + parm.player + "</b> exceeds the guest quota established by the Golf Shop."
                             + "<BR><BR>You are allowed " + parm.grest_num + " of these guests per " +parm.grest_per+ " during a timeframe defined by club policy and you have met or exceeded that limit.");
                out.println("<br><br>You will have to remove the guest in order to complete this request.");
                out.println("<br><br>Contact the Golf Shop if you have any questions.<br>");
                out.println("<BR><BR>");
                //
                //  Return to _sheet
                //
                goReturn(out, parm);
                return;
            }


            //
            //*******************************************************************************************
            //   
            //   Guests were included in the tee time and processed (and assigned) -  
            //   
            //        Now perform any guest related customs !!!!!!!!!!!!!!! 
            //   
            //*******************************************************************************************
            //           
            String gstcustomMsg = checkCustomsGst(con, parm, user);     // go check for customs

            if (!gstcustomMsg.equals("")) {         // if error encountered - reject

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center><BR>");
                out.println(gstcustomMsg);           // add custom error msg
                out.println("<BR><BR>");

                goReturn(out, parm);
                return;
            }

            //
            //  MOVE THE GUEST CUSTOMS BELOW TO USE ABOVE PROCESS !!!!!!!!!!!!!!
            //

        }


        //
        //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
        //
        checkTFlag(parm, con);



        //
        // *******************************************************************************
        //  Custom checks
        // *******************************************************************************
        //
        //  If North Shore CC - check for 'guest only' times - must be at least 2 guest per group
        //
        if (club.equals("northshore")) {

            check = checkNSGuests(con, out, parm, day);      // go check

            if (parm.error == true) {          // if we hit a db error

                return;
            }

            if (check == true) {          // if we hit on a restriction

                out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                out.println("<BODY><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><BR><H3>Guest Time Violation</H3>");
                out.println("<BR>Sorry, you must include at least 2 guests per group during this time.<br><br>");
                out.println("Please contact the golf shop if you have any questions.<br><br>");
                out.println("<BR><BR>");

                //
                //  Return to _lott
                //
                goReturn(out, parm);
                return;
            }
        }


        //
        //  Wilmington Custom - check mship subtypes for those that have range privileges
        //
        if (club.equals("wilmington")) {

            verifyCustom.checkWilmington(parm);         // go flag mships
        }



        //
        //  Pre-checkin feature - normally sets an indicator if the tee time is created or changed the same day (day of) 
        //                        as the tee time itself.  This creates a visual for the proshop user on the tee sheet
        //                        so they can easily see which tee times are new that day.
        //
        // set the precheckin value in parmSlotm to 1 if we are using the feature for this teetime
        parm.precheckin = (parm2.precheckin == 1 && index.equals("0")) ? 1 : 0;

        //    
        //  Custom changes for Pre-checkin - make sure the time is adjusted for time zone!!!!!!!!!!!
        //

        // Custom for Imperial GC - Utilize pre-checkin for tomorrow bookings if it's after 1pm ET today - Case# 1327
        if (club.equals("imperialgc") && index.equals("1")) {

            GregorianCalendar cal_pci = new GregorianCalendar();
            if (cal_pci.get(Calendar.HOUR_OF_DAY) >= 12) {

                parm.precheckin = 1;
            }
        }

        // Custom for Pelican's Nest - Utilize pre-checkin for tomorrow bookings if it's after 5pm today - Case #1296
        if (club.equals("pelicansnest") && index.equals("1")) {

            GregorianCalendar cal_pci = new GregorianCalendar();
            if (cal_pci.get(Calendar.HOUR_OF_DAY) >= 17) {

                parm.precheckin = 1;
            }
        }

        // Custom for Mediterra - Utilize pre-checkin for tomorrow bookings if it's after 5:30pm today - Case #1309
        if (club.equals("mediterra") && index.equals("1")) {

            GregorianCalendar cal_pci = new GregorianCalendar();
            if ((cal_pci.get(Calendar.HOUR_OF_DAY) >= 17 && cal_pci.get(Calendar.MINUTE) >= 30)
                    || cal_pci.get(Calendar.HOUR_OF_DAY) >= 18) {

                parm.precheckin = 1;
            }
        }

        // Custom for The Oaks Club - Utilize pre-checkin for tomorrow bookings if it's after 6pm today - Case #1589
        if (club.equals("theoaksclub") && index.equals("1")) {

            GregorianCalendar cal_pci = new GregorianCalendar();
            if (cal_pci.get(Calendar.HOUR_OF_DAY) >= 18) {

                parm.precheckin = 1;
            }
        }

        // Custom for Wildcat Run G&CC - Utilize pre-checkin for tomorrow bookings if it's after 4:30pm ET today - Case #2111
        if (club.equals("wildcatruncc") && index.equals("1")) {

            GregorianCalendar cal_pci = new GregorianCalendar();
            if ((cal_pci.get(Calendar.HOUR_OF_DAY) >= 15 && cal_pci.get(Calendar.MINUTE) >= 30)
                    || cal_pci.get(Calendar.HOUR_OF_DAY) >= 16) {

                parm.precheckin = 1;
            }
        }


        //
        //  If new notes added, then add this member's name to the end for identification purposes.
        //
        if (!parm.notes.equals("")) {

            parm.notes = parm.notes + " (" +fullName+ ")";     
        }           
            

        //
        //  Verification complete -
        //    Add tee times and send email notifications
        //
        int newTimes = 0;

        try {

            newTimes = verifySlot.addMteeTime(parm, req);

        } catch (Exception e1) {

            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H2>Database Access Error</H2>");
            out.println("<BR><BR>Error encountered while attempting to save the request.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR>" + e1.getMessage());
            out.println("<BR><BR>");
            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
        }

        //
        //  Track the new tee times in the history table (teehist)
        //
        if (newTimes > 0) {      // if times added

            addHistm(parm, user, fullName, con);       // update the history
            
            // Check to debug double bookings
            //    if ((time >= 558 && time <= 1010) || club.startsWith("demo")) {
            //        Utilities.logDebug("BSK", "DBC - Times Booked Successfully (" + Utilities.get_ms_timestamp(con) + ") - " + club + " - user: " + user + ", slots: " + slots + ", date: " + date + ", fb: " + fb + ", course: " + course
            //                + ", time1: " + parm.time1 + ", time2: " + parm.time2 + ", time3: " + parm.time3 + ", time4: " + parm.time4 + ", time5: " + parm.time5);
            //    }
        }

        //  Attempt to add hosts for any accompanied tracked guests
        if (parm.guest_id1 > 0 && !parm.userg[0].equals("")) {
            Common_guestdb.addHost(parm.guest_id1, parm.userg[0], con);
        }
        if (parm.guest_id2 > 0 && !parm.userg[1].equals("")) {
            Common_guestdb.addHost(parm.guest_id2, parm.userg[1], con);
        }
        if (parm.guest_id3 > 0 && !parm.userg[2].equals("")) {
            Common_guestdb.addHost(parm.guest_id3, parm.userg[2], con);
        }
        if (parm.guest_id4 > 0 && !parm.userg[3].equals("")) {
            Common_guestdb.addHost(parm.guest_id4, parm.userg[3], con);
        }
        if (parm.guest_id5 > 0 && !parm.userg[4].equals("")) {
            Common_guestdb.addHost(parm.guest_id5, parm.userg[4], con);
        }
        if (parm.guest_id6 > 0 && !parm.userg[5].equals("")) {
            Common_guestdb.addHost(parm.guest_id6, parm.userg[5], con);
        }
        if (parm.guest_id7 > 0 && !parm.userg[6].equals("")) {
            Common_guestdb.addHost(parm.guest_id7, parm.userg[6], con);
        }
        if (parm.guest_id8 > 0 && !parm.userg[7].equals("")) {
            Common_guestdb.addHost(parm.guest_id8, parm.userg[7], con);
        }
        if (parm.guest_id9 > 0 && !parm.userg[8].equals("")) {
            Common_guestdb.addHost(parm.guest_id9, parm.userg[8], con);
        }
        if (parm.guest_id10 > 0 && !parm.userg[9].equals("")) {
            Common_guestdb.addHost(parm.guest_id10, parm.userg[9], con);
        }
        if (parm.guest_id11 > 0 && !parm.userg[10].equals("")) {
            Common_guestdb.addHost(parm.guest_id11, parm.userg[10], con);
        }
        if (parm.guest_id12 > 0 && !parm.userg[11].equals("")) {
            Common_guestdb.addHost(parm.guest_id12, parm.userg[11], con);
        }
        if (parm.guest_id13 > 0 && !parm.userg[12].equals("")) {
            Common_guestdb.addHost(parm.guest_id13, parm.userg[12], con);
        }
        if (parm.guest_id14 > 0 && !parm.userg[13].equals("")) {
            Common_guestdb.addHost(parm.guest_id14, parm.userg[13], con);
        }
        if (parm.guest_id15 > 0 && !parm.userg[14].equals("")) {
            Common_guestdb.addHost(parm.guest_id15, parm.userg[14], con);
        }
        if (parm.guest_id16 > 0 && !parm.userg[15].equals("")) {
            Common_guestdb.addHost(parm.guest_id16, parm.userg[15], con);
        }
        if (parm.guest_id17 > 0 && !parm.userg[16].equals("")) {
            Common_guestdb.addHost(parm.guest_id17, parm.userg[16], con);
        }
        if (parm.guest_id18 > 0 && !parm.userg[17].equals("")) {
            Common_guestdb.addHost(parm.guest_id18, parm.userg[17], con);
        }
        if (parm.guest_id19 > 0 && !parm.userg[18].equals("")) {
            Common_guestdb.addHost(parm.guest_id19, parm.userg[18], con);
        }
        if (parm.guest_id20 > 0 && !parm.userg[19].equals("")) {
            Common_guestdb.addHost(parm.guest_id20, parm.userg[19], con);
        }
        if (parm.guest_id21 > 0 && !parm.userg[20].equals("")) {
            Common_guestdb.addHost(parm.guest_id21, parm.userg[20], con);
        }
        if (parm.guest_id22 > 0 && !parm.userg[21].equals("")) {
            Common_guestdb.addHost(parm.guest_id22, parm.userg[21], con);
        }
        if (parm.guest_id23 > 0 && !parm.userg[22].equals("")) {
            Common_guestdb.addHost(parm.guest_id23, parm.userg[22], con);
        }
        if (parm.guest_id24 > 0 && !parm.userg[23].equals("")) {
            Common_guestdb.addHost(parm.guest_id24, parm.userg[23], con);
        }
        if (parm.guest_id25 > 0 && !parm.userg[24].equals("")) {
            Common_guestdb.addHost(parm.guest_id25, parm.userg[24], con);
        }


        //
        //  Build the HTML page to confirm reservation for user
        //
        //
        msgDate = yy + "-" + mm + "-" + parm.dd;
        msgPlayerCount = 1;  // count player1 by default
        if (!parm.player2.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player3.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player4.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player5.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player6.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player7.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player8.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player9.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player10.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player11.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player12.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player13.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player14.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player15.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player16.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player17.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player18.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player19.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player20.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player21.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player22.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player23.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player24.equals("")) {
            msgPlayerCount++;
        }
        if (!parm.player25.equals("")) {
            msgPlayerCount++;
        }

        out.println(SystemUtils.HeadTitle("Member Tee Time Request Page"));
        out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

        out.println("<center><img src=\"/" + rev + "/images/foretees.gif\"><hr width=\"40%\">");
        out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

        out.println("<p>&nbsp;</p><p><b>Thank you!</b>&nbsp;&nbsp;Your request has been accepted and processed.</p>");
        out.println("</font><font size=\"2\">");
        out.println("<br><p>You will be able to access each tee time individually should changes be required.</p>");

        if (club.equals("charlottecc") && (parm.guestsg1 >= 2 || parm.guestsg2 >= 2 || parm.guestsg3 >= 2)) {        // If Charlotte CC and 2 or more guests in any group

            out.println("<p><br><b>Notice from Golf Professional Staff:</b>"
                    + "<br><br>\"Any golf group that has two (2) or more Guests must have a caddie if walking, "
                    + "<br>or a forecaddie if riding in golf carts.\" - Thank You</p>");
        }

        if (notesL > 254) {

            out.println("</font><font size=\"3\">");
            out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
        }
        out.println("<p>There were " + newTimes + " tee times reserved.</p>");
        out.println("<p>&nbsp;</p></font>");

        if (Utilities.checkDiningLink("mem_teetime", con)) {
            String dCourse = "";
            if (!parm.returnCourse.equals("")) {
                dCourse = parm.returnCourse;
            } else {
                dCourse = parm.course;
            }
            customId = Utilities.printDiningPrompt(out, con, msgDate, parm.day, user, msgPlayerCount, "teetime", "&index=" + index + "&course=" + dCourse + "&jump=" + jump, false);
        }

        out.println("<font size=\"2\">");
        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        if (!returnCourse.equals("")) {    // if multi course club, get course to return to (ALL?)
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        }
        out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
        out.println("</form></font>");
        out.println("</center></font></body></html>");
        out.close();

        try {

            resp.flushBuffer();      // force the repsonse to complete

        } catch (Exception ignore) {
        }

        //
        //***********************************************
        //  Send email notifications
        //***********************************************
        //
        sendMail(con, parm, user);        // send emails

    }       // end of Verify

    // *******************************************************************************
    //  Track all tee times - add to history
    // *******************************************************************************
    //
    private void addHistm(parmSlotm parm, String user, String fullName, Connection con) {


        String p1 = "";
        String p2 = "";
        String p3 = "";
        String p4 = "";
        String p5 = "";

        int count = parm.slots;    // # of tee times to be added

        p1 = parm.player1;
        p2 = parm.player2;
        p3 = parm.player3;
        p4 = parm.player4;

        if (parm.p5.equals("Yes")) {

            p5 = parm.player5;

        } else {

            p5 = "";
        }

        //
        //  track the new tee time
        //
        SystemUtils.updateHist(parm.date, parm.day, parm.time1, parm.fb, parm.course, p1, p2, p3,
                p4, p5, user, fullName, 0, con);


        //
        //  Add next tee time, if present
        //
        if (count > 1) {

            if (parm.p5.equals("Yes")) {

                p1 = parm.player6;
                p2 = parm.player7;
                p3 = parm.player8;
                p4 = parm.player9;
                p5 = parm.player10;

            } else {

                p1 = parm.player5;
                p2 = parm.player6;
                p3 = parm.player7;
                p4 = parm.player8;
                p5 = "";
            }

            //
            //  track the new tee time if players exist
            //
            if (!p1.equals("") || !p2.equals("") || !p3.equals("") || !p4.equals("") || !p5.equals("")) {

                SystemUtils.updateHist(parm.date, parm.day, parm.time2, parm.fb, parm.course, p1, p2, p3,
                        p4, p5, user, fullName, 0, con);
            }
        }

        //
        //  Add next tee time, if present
        //
        if (count > 2) {

            if (parm.p5.equals("Yes")) {

                p1 = parm.player11;
                p2 = parm.player12;
                p3 = parm.player13;
                p4 = parm.player14;
                p5 = parm.player15;

            } else {

                p1 = parm.player9;
                p2 = parm.player10;
                p3 = parm.player11;
                p4 = parm.player12;
                p5 = "";
            }

            //
            //  track the new tee time if players exist
            //
            if (!p1.equals("") || !p2.equals("") || !p3.equals("") || !p4.equals("") || !p5.equals("")) {

                SystemUtils.updateHist(parm.date, parm.day, parm.time3, parm.fb, parm.course, p1, p2, p3,
                        p4, p5, user, fullName, 0, con);
            }
        }

        //
        //  Add next tee time, if present
        //
        if (count > 3) {

            if (parm.p5.equals("Yes")) {

                p1 = parm.player16;
                p2 = parm.player17;
                p3 = parm.player18;
                p4 = parm.player19;
                p5 = parm.player20;

            } else {

                p1 = parm.player13;
                p2 = parm.player14;
                p3 = parm.player15;
                p4 = parm.player16;
                p5 = "";
            }

            //
            //  track the new tee time if players exist
            //
            if (!p1.equals("") || !p2.equals("") || !p3.equals("") || !p4.equals("") || !p5.equals("")) {

                SystemUtils.updateHist(parm.date, parm.day, parm.time4, parm.fb, parm.course, p1, p2, p3,
                        p4, p5, user, fullName, 0, con);
            }
        }

        //
        //  Add next tee time, if present
        //
        if (count > 4) {

            if (parm.p5.equals("Yes")) {

                p1 = parm.player21;
                p2 = parm.player22;
                p3 = parm.player23;
                p4 = parm.player24;
                p5 = parm.player25;

            } else {

                p1 = parm.player17;
                p2 = parm.player18;
                p3 = parm.player19;
                p4 = parm.player20;
                p5 = "";
            }

            //
            //  track the new tee time if players exist
            //
            if (!p1.equals("") || !p2.equals("") || !p3.equals("") || !p4.equals("") || !p5.equals("")) {

                SystemUtils.updateHist(parm.date, parm.day, parm.time5, parm.fb, parm.course, p1, p2, p3,
                        p4, p5, user, fullName, 0, con);
            }
        }

    }       // end of addHistm

    
    
    // *******************************************************************************
    //  Check if request is in use by user
    // *******************************************************************************
    //
    private String checkInUseBy(Connection con, parmSlotm parm, HttpSession session, String user, String club) {


        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String in_use_by = "";
        int in_use = 0;
        
        String error = "";
        String player1 = "";
        

        //
        //  Check if the requested tee times are still 'in use' and still in use by this user??
        //
        //  This is necessary because the user may have gone away while holding this req.  If the
        //  slot timed out (system timer), the slot would be marked 'not in use' and another
        //  user could pick it up.  The original holder could be trying to use it now.
        //
        try {

            pstmt = con.prepareStatement(
                    "SELECT teecurr_id, in_use, in_use_by, player1 "
                    + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, parm.date);         // put the parm in pstmt
            pstmt.setInt(2, parm.time1);
            pstmt.setInt(3, parm.fb);
            pstmt.setString(4, parm.course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

                parm.teecurr_idA[0] = rs.getInt(1);
                in_use = rs.getInt(2);
                in_use_by = rs.getString(3);
                player1 = rs.getString("player1");
            }
            pstmt.close();
            
            if (in_use == 0) error = "In Use = 0";            // error if not in use by this user
            
            if (!in_use_by.equalsIgnoreCase(user)) error += " and in_use_by != user";     
            
            if (!player1.equals("")) error += " and player1 of 1 = " +player1;     

            //
            //  Get the teecurr_id's of the others and check them too
            //
            if (parm.time2 > 0 && error.equals("")) {         // if another tee time
                
                in_use_by = "";      // init to make sure we get the tee time

                pstmt = con.prepareStatement(
                        "SELECT teecurr_id, in_use, in_use_by, player1 "
                        + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                pstmt.clearParameters();        // clear the parms
                pstmt.setLong(1, parm.date);         // put the parm in pstmt
                pstmt.setInt(2, parm.time2);
                pstmt.setInt(3, parm.fb);
                pstmt.setString(4, parm.course);
                rs = pstmt.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

                    parm.teecurr_idA[1] = rs.getInt(1);
                    in_use = rs.getInt(2);
                    in_use_by = rs.getString(3);
                    player1 = rs.getString("player1");
                }
                pstmt.close();

                if (in_use == 0) error = "In Use = 0";            // error if not in use by this user

                if (!in_use_by.equalsIgnoreCase(user)) error += " and in_use_by != user";            // error if not in use by this user
            
                if (!player1.equals("")) error += " and player1 of 2 = " +player1;     
            }

            if (parm.time3 > 0 && error.equals("")) {         // if another tee time

                in_use_by = "";      // init to make sure we get the tee time

                pstmt = con.prepareStatement(
                        "SELECT teecurr_id, in_use, in_use_by, player1 "
                        + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                pstmt.clearParameters();        // clear the parms
                pstmt.setLong(1, parm.date);         // put the parm in pstmt
                pstmt.setInt(2, parm.time3);
                pstmt.setInt(3, parm.fb);
                pstmt.setString(4, parm.course);
                rs = pstmt.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

                    parm.teecurr_idA[2] = rs.getInt(1);
                    in_use = rs.getInt(2);
                    in_use_by = rs.getString(3);
                    player1 = rs.getString("player1");
                }
                pstmt.close();

                if (in_use == 0) error = "In Use = 0";            // error if not in use by this user

                if (!in_use_by.equalsIgnoreCase(user)) error += " and in_use_by != user";            // error if not in use by this user
            
                if (!player1.equals("")) error += " and player1 of 3 = " +player1;     
            }

            if (parm.time4 > 0 && error.equals("")) {         // if another tee time

                in_use_by = "";      // init to make sure we get the tee time

                pstmt = con.prepareStatement(
                        "SELECT teecurr_id, in_use, in_use_by, player1 "
                        + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                pstmt.clearParameters();        // clear the parms
                pstmt.setLong(1, parm.date);         // put the parm in pstmt
                pstmt.setInt(2, parm.time4);
                pstmt.setInt(3, parm.fb);
                pstmt.setString(4, parm.course);
                rs = pstmt.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

                    parm.teecurr_idA[3] = rs.getInt(1);
                    in_use = rs.getInt(2);
                    in_use_by = rs.getString(3);
                    player1 = rs.getString("player1");
                }
                pstmt.close();

                if (in_use == 0) error = "In Use = 0";            // error if not in use by this user

                if (!in_use_by.equalsIgnoreCase(user)) error += " and in_use_by != user";            // error if not in use by this user
            
                if (!player1.equals("")) error += " and player1 of 4 = " +player1;     
            }

            if (parm.time5 > 0 && error.equals("")) {         // if another tee time

                in_use_by = "";      // init to make sure we get the tee time

                pstmt = con.prepareStatement(
                        "SELECT teecurr_id, in_use, in_use_by, player1 "
                        + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                pstmt.clearParameters();        // clear the parms
                pstmt.setLong(1, parm.date);         // put the parm in pstmt
                pstmt.setInt(2, parm.time5);
                pstmt.setInt(3, parm.fb);
                pstmt.setString(4, parm.course);
                rs = pstmt.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

                    parm.teecurr_idA[4] = rs.getInt(1);
                    in_use = rs.getInt(2);
                    in_use_by = rs.getString(3);
                    player1 = rs.getString("player1");
                }
                pstmt.close();

                if (in_use == 0) error = "In Use = 0";            // error if not in use by this user

                if (!in_use_by.equalsIgnoreCase(user)) error += " and in_use_by != user";            // error if not in use by this user
            
                if (!player1.equals("")) error += " and player1 of 5 = " +player1;     
            }

        } catch (Exception e1) {     
            
            error = "Exception Recvd - " +e1.getMessage();            // bomb this out
            
            Utilities.logError("Error in Member_slotm.checkInUseBy for club "+club+". Error=" +e1.getMessage());     // log it
       }
        
        if (error.equals("")) {      // if ok
                        
            parm.in_use = in_use;
            parm.in_use_by = in_use_by;
        }
        
        return(error);
        
    }       // end of checkInUseBy
    
    

    // *******************************************************************************
    //  Count X's and check against max allowed and time frame allowed
    // *******************************************************************************
    //
    private boolean checkXcount(PrintWriter out, parmSlotm parm, parmClub parm2) {


        boolean error = false;

        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        int count5 = 0;

        int x = parm2.x;                // get max allowed X's per group
        int xhrs = parm2.xhrs;          // get hours in advance that x's are allowed


        //
        //  Count the number of X's in each group
        //
        if (parm.p5.equals("Yes")) {          // if 5-somes allowed

            if (parm.player1.equalsIgnoreCase("x")) {

                count1++;
            }
            if (parm.player2.equalsIgnoreCase("x")) {

                count1++;
            }
            if (parm.player3.equalsIgnoreCase("x")) {

                count1++;
            }
            if (parm.player4.equalsIgnoreCase("x")) {

                count1++;
            }
            if (parm.player5.equalsIgnoreCase("x")) {

                count1++;
            }

            if (parm.player6.equalsIgnoreCase("x")) {       // count 2nd group

                count2++;
            }
            if (parm.player7.equalsIgnoreCase("x")) {

                count2++;
            }
            if (parm.player8.equalsIgnoreCase("x")) {

                count2++;
            }
            if (parm.player9.equalsIgnoreCase("x")) {

                count2++;
            }
            if (parm.player10.equalsIgnoreCase("x")) {

                count2++;
            }

            if (parm.player11.equalsIgnoreCase("x")) {       // count 3rd group

                count3++;
            }
            if (parm.player12.equalsIgnoreCase("x")) {

                count3++;
            }
            if (parm.player13.equalsIgnoreCase("x")) {

                count3++;
            }
            if (parm.player14.equalsIgnoreCase("x")) {

                count3++;
            }
            if (parm.player15.equalsIgnoreCase("x")) {

                count3++;
            }

            if (parm.player16.equalsIgnoreCase("x")) {       // count 4th group

                count4++;
            }
            if (parm.player17.equalsIgnoreCase("x")) {

                count4++;
            }
            if (parm.player18.equalsIgnoreCase("x")) {

                count4++;
            }
            if (parm.player19.equalsIgnoreCase("x")) {

                count4++;
            }
            if (parm.player20.equalsIgnoreCase("x")) {

                count4++;
            }

            if (parm.player21.equalsIgnoreCase("x")) {          // count 5th group

                count5++;
            }
            if (parm.player22.equalsIgnoreCase("x")) {

                count5++;
            }
            if (parm.player23.equalsIgnoreCase("x")) {

                count5++;
            }
            if (parm.player24.equalsIgnoreCase("x")) {

                count5++;
            }
            if (parm.player25.equalsIgnoreCase("x")) {

                count5++;
            }

        } else {                                // 4-somes only

            if (parm.player1.equalsIgnoreCase("x")) {

                count1++;
            }
            if (parm.player2.equalsIgnoreCase("x")) {

                count1++;
            }
            if (parm.player3.equalsIgnoreCase("x")) {

                count1++;
            }
            if (parm.player4.equalsIgnoreCase("x")) {

                count1++;
            }

            if (parm.player5.equalsIgnoreCase("x")) {       // count 2nd group

                count2++;
            }
            if (parm.player6.equalsIgnoreCase("x")) {

                count2++;
            }
            if (parm.player7.equalsIgnoreCase("x")) {

                count2++;
            }
            if (parm.player8.equalsIgnoreCase("x")) {

                count2++;
            }

            if (parm.player9.equalsIgnoreCase("x")) {         // count 3rd group

                count3++;
            }
            if (parm.player10.equalsIgnoreCase("x")) {

                count3++;
            }
            if (parm.player11.equalsIgnoreCase("x")) {

                count3++;
            }
            if (parm.player12.equalsIgnoreCase("x")) {

                count3++;
            }

            if (parm.player13.equalsIgnoreCase("x")) {        // count 4th group

                count4++;
            }
            if (parm.player14.equalsIgnoreCase("x")) {

                count4++;
            }
            if (parm.player15.equalsIgnoreCase("x")) {

                count4++;
            }
            if (parm.player16.equalsIgnoreCase("x")) {

                count4++;
            }

            if (parm.player17.equalsIgnoreCase("x")) {        // count 5th group

                count5++;
            }
            if (parm.player18.equalsIgnoreCase("x")) {

                count5++;
            }
            if (parm.player19.equalsIgnoreCase("x")) {

                count5++;
            }
            if (parm.player20.equalsIgnoreCase("x")) {

                count5++;
            }
        }

        //
        //   Verify the date/time - are X's allowed
        //
        if (count1 > 0 || count2 > 0 || count3 > 0 || count4 > 0 || count5 > 0) {

            if (xhrs > 0) {           // if limit was specified

                //
                //  Set date/time values to be used to check for X's in tee sheet
                //
                //  Get today's date and then go up by 'xhrs' hours
                //
                Calendar cal = new GregorianCalendar();       // get todays date

                cal.add(Calendar.HOUR_OF_DAY, xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

                int calYear = cal.get(Calendar.YEAR);
                int calMonth = cal.get(Calendar.MONTH);
                int calDay = cal.get(Calendar.DAY_OF_MONTH);
                int calHr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
                int calMin = cal.get(Calendar.MINUTE);

                calMonth = calMonth + 1;                             // month starts at zero

                long adv_date = calYear * 10000;                     // create a date field of yyyymmdd
                adv_date = adv_date + (calMonth * 100);
                adv_date = adv_date + calDay;                        // date = yyyymmdd (for comparisons)

                int adv_time = calHr * 100;                          // create time field of hhmm
                adv_time = adv_time + calMin;

                //
                //  Compare the tee time's date/time to the X deadline
                //
                if ((parm.date < adv_date) || ((parm.date == adv_date) && (parm.time1 <= adv_time))) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Data Entry Error</H3>");
                    out.println("<BR><BR>Sorry, 'X' is not allowed at this time.");
                    out.println("<BR><BR>It is too close to the actual tee time to reserve positions with an X.");
                    out.println("<BR><BR>Please correct this and try again.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    error = true;               // return error
                }
            }
        }

        if (error == false) {          // if still ok

            //
            //   Verify the counts
            //
            if (count1 > x || count2 > x || count3 > x || count4 > x || count5 > x) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Data Entry Error</H3>");
                out.println("<BR><BR>You are attempting to reserve more player positions than allowed.");
                out.println("<BR><BR>The maximum number of X's allowed per group is " + x + ".");
                out.println("<BR><BR>Please correct this and try again.");
                out.println("<BR><BR>");

                goReturn(out, parm);
                error = true;               // return error
            }
        }

        return (error);
    }

    // *******************************************************************************
    //  Parse Member Names and identify Guests
    // *******************************************************************************
    //
    private boolean parseNames(PrintWriter out, parmSlotm parm, parmClub parm2, Connection con) {


        ResultSet rs = null;

        StringTokenizer tok = null;

        String fname = "";
        String lname = "";
        String mi = "";
        String gplayer = "";
        String user = "";

        int i = 0;

        boolean found = false;
        boolean error = false;
        boolean invalidGuest = false;
        boolean guestdbTbaAllowed = false;

        boolean[] skipNameCheck = new boolean[25];


        //  If guest tracking is in use, determine whether names are optional or required
        if (Utilities.isGuestTrackingConfigured(0, con) && Utilities.isGuestTrackingTbaAllowed(0, false, con)) {
            guestdbTbaAllowed = true;
        }

        //
        //   Remove any guest types that are null - for tests below
        //
        for (i = 0; i < parm2.MAX_Guests; i++) {

            if (parm2.guest[i].equals("")) {

                parm2.guest[i] = "$@#!^&*";      // make so it won't match player name
            }
        }         // end of while loop

        i = 0;
        while (i < 25) {

            parm.gstA[i] = "";    // init guest array and indicators
            skipNameCheck[i] = false;
            i++;
        }


        //
        //  Get today's date 
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int calYear = cal.get(Calendar.YEAR);
        int calMonth = cal.get(Calendar.MONTH) + 1;
        int calDay = cal.get(Calendar.DAY_OF_MONTH);

        //long today_date = (calYear * 10000) + (calMonth * 100) + calDay;     // create a date field of yyyymmdd


        try {

            PreparedStatement pstmt1 = con.prepareStatement(
                    "SELECT username, m_ship, m_type, g_hancap, memNum, msub_type, name_last, name_first, name_mi "
                    + "FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ? AND inact = 0");

            //
            //  Check each player for Member or Guest
            //
            parm.g[0] = "";
            gplayer = "";
            found = false;
            if (!parm.player1.equals("") && !parm.player1.equalsIgnoreCase("x")) {      // if not empty and not 'x'

                i = 0;
                loop1:
                while (i < parm2.MAX_Guests) {                                                     // is it a guest ?

                    if (parm.player1.startsWith(parm2.guest[i])) {

                        parm.g[0] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[0] = parm.player1;       // save guest value
                        parm.guests++;                     // increment number of guests this request
                        parm.guestsg1++;                   // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id1 != 0 || !parm.player1.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id1 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player1, parm.guest_id1, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player1;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[0] = true;
                            }
                        }

                        found = true;

                        if (parm2.gOpt[i] > 0) {                    // if Proshop-only guest type

                            gplayer = parm.player1;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop1;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player1.equalsIgnoreCase(parm.player2)) || (parm.player1.equalsIgnoreCase(parm.player3))
                            || (parm.player1.equalsIgnoreCase(parm.player4)) || (parm.player1.equalsIgnoreCase(parm.player5))
                            || (parm.player1.equalsIgnoreCase(parm.player6)) || (parm.player1.equalsIgnoreCase(parm.player7))
                            || (parm.player1.equalsIgnoreCase(parm.player8)) || (parm.player1.equalsIgnoreCase(parm.player9))
                            || (parm.player1.equalsIgnoreCase(parm.player10)) || (parm.player1.equalsIgnoreCase(parm.player11))
                            || (parm.player1.equalsIgnoreCase(parm.player12)) || (parm.player1.equalsIgnoreCase(parm.player13))
                            || (parm.player1.equalsIgnoreCase(parm.player14)) || (parm.player1.equalsIgnoreCase(parm.player15))
                            || (parm.player1.equalsIgnoreCase(parm.player16)) || (parm.player1.equalsIgnoreCase(parm.player17))
                            || (parm.player1.equalsIgnoreCase(parm.player18)) || (parm.player1.equalsIgnoreCase(parm.player19))
                            || (parm.player1.equalsIgnoreCase(parm.player20)) || (parm.player1.equalsIgnoreCase(parm.player21))
                            || (parm.player1.equalsIgnoreCase(parm.player22)) || (parm.player1.equalsIgnoreCase(parm.player23))
                            || (parm.player1.equalsIgnoreCase(parm.player24)) || (parm.player1.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player1);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player1);     // space is the default token

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player1);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() < 2) {          // single names or X not allowed in player1 spot

                        invData(out, parm.player1);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname1 = tok.nextToken();
                        parm.lname1 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname1 = tok.nextToken();
                        parm.mi1 = tok.nextToken();
                        parm.lname1 = tok.nextToken();
                    }

                    if ((!parm.fname1.equals("")) && (!parm.lname1.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname1);
                        pstmt1.setString(2, parm.fname1);
                        pstmt1.setString(3, parm.mi1);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user1 = rs.getString(1);
                            parm.mship1 = rs.getString(2);
                            parm.mtype1 = rs.getString(3);
                            parm.hndcp1 = rs.getFloat(4);
                            parm.mNum1 = rs.getString(5);
                            parm.mstype1 = rs.getString(6);
                            parm.lname1 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname1 = rs.getString(8);
                            parm.mi1 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg1++;           // group 1
                            user = parm.user1;      // save as last member (for guests)

                            parm.player1 = buildName(parm.lname1, parm.fname1, parm.mi1);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval1 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[1] = "";
            found = false;
            if (!parm.player2.equals("") && !parm.player2.equalsIgnoreCase("x")) {

                i = 0;
                loop2:
                while (i < parm2.MAX_Guests) {

                    if (parm.player2.startsWith(parm2.guest[i])) {

                        parm.g[1] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[1] = parm.player2;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg1++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id2 != 0 || !parm.player2.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id2 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player2, parm.guest_id2, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player2;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[1] = true;
                            }
                        }

                        found = true;
                        parm.userg[1] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player2;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop2;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player2.equalsIgnoreCase(parm.player3))
                            || (parm.player2.equalsIgnoreCase(parm.player4)) || (parm.player2.equalsIgnoreCase(parm.player5))
                            || (parm.player2.equalsIgnoreCase(parm.player6)) || (parm.player2.equalsIgnoreCase(parm.player7))
                            || (parm.player2.equalsIgnoreCase(parm.player8)) || (parm.player2.equalsIgnoreCase(parm.player9))
                            || (parm.player2.equalsIgnoreCase(parm.player10)) || (parm.player2.equalsIgnoreCase(parm.player11))
                            || (parm.player2.equalsIgnoreCase(parm.player12)) || (parm.player2.equalsIgnoreCase(parm.player13))
                            || (parm.player2.equalsIgnoreCase(parm.player14)) || (parm.player2.equalsIgnoreCase(parm.player15))
                            || (parm.player2.equalsIgnoreCase(parm.player16)) || (parm.player2.equalsIgnoreCase(parm.player17))
                            || (parm.player2.equalsIgnoreCase(parm.player18)) || (parm.player2.equalsIgnoreCase(parm.player19))
                            || (parm.player2.equalsIgnoreCase(parm.player20)) || (parm.player2.equalsIgnoreCase(parm.player21))
                            || (parm.player2.equalsIgnoreCase(parm.player22)) || (parm.player2.equalsIgnoreCase(parm.player23))
                            || (parm.player2.equalsIgnoreCase(parm.player24)) || (parm.player2.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player2);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player2);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player2);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player2);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname2 = tok.nextToken();
                        parm.lname2 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname2 = tok.nextToken();
                        parm.mi2 = tok.nextToken();
                        parm.lname2 = tok.nextToken();
                    }

                    if ((!parm.fname2.equals("")) && (!parm.lname2.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname2);
                        pstmt1.setString(2, parm.fname2);
                        pstmt1.setString(3, parm.mi2);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user2 = rs.getString(1);
                            parm.mship2 = rs.getString(2);
                            parm.mtype2 = rs.getString(3);
                            parm.hndcp2 = rs.getFloat(4);
                            parm.mNum2 = rs.getString(5);
                            parm.mstype2 = rs.getString(6);
                            parm.lname2 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname2 = rs.getString(8);
                            parm.mi2 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg2++;           // group 1
                            user = parm.user2;      // save as last member (for guests)

                            parm.player2 = buildName(parm.lname2, parm.fname2, parm.mi2);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval2 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[2] = "";
            found = false;
            if (!parm.player3.equals("") && !parm.player3.equalsIgnoreCase("x")) {

                i = 0;
                loop3:
                while (i < parm2.MAX_Guests) {

                    if (parm.player3.startsWith(parm2.guest[i])) {

                        parm.g[2] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[2] = parm.player3;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg1++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id3 != 0 || !parm.player3.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id3 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player3, parm.guest_id3, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player3;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[2] = true;
                            }
                        }

                        found = true;
                        parm.userg[2] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player3;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop3;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player3.equalsIgnoreCase(parm.player4)) || (parm.player3.equalsIgnoreCase(parm.player5))
                            || (parm.player3.equalsIgnoreCase(parm.player6)) || (parm.player3.equalsIgnoreCase(parm.player7))
                            || (parm.player3.equalsIgnoreCase(parm.player8)) || (parm.player3.equalsIgnoreCase(parm.player9))
                            || (parm.player3.equalsIgnoreCase(parm.player10)) || (parm.player3.equalsIgnoreCase(parm.player11))
                            || (parm.player3.equalsIgnoreCase(parm.player12)) || (parm.player3.equalsIgnoreCase(parm.player13))
                            || (parm.player3.equalsIgnoreCase(parm.player14)) || (parm.player3.equalsIgnoreCase(parm.player15))
                            || (parm.player3.equalsIgnoreCase(parm.player16)) || (parm.player3.equalsIgnoreCase(parm.player17))
                            || (parm.player3.equalsIgnoreCase(parm.player18)) || (parm.player3.equalsIgnoreCase(parm.player19))
                            || (parm.player3.equalsIgnoreCase(parm.player20)) || (parm.player3.equalsIgnoreCase(parm.player21))
                            || (parm.player3.equalsIgnoreCase(parm.player22)) || (parm.player3.equalsIgnoreCase(parm.player23))
                            || (parm.player3.equalsIgnoreCase(parm.player24)) || (parm.player3.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player3);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player3);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player3);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player3);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname3 = tok.nextToken();
                        parm.lname3 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname3 = tok.nextToken();
                        parm.mi3 = tok.nextToken();
                        parm.lname3 = tok.nextToken();
                    }

                    if ((!parm.fname3.equals("")) && (!parm.lname3.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname3);
                        pstmt1.setString(2, parm.fname3);
                        pstmt1.setString(3, parm.mi3);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user3 = rs.getString(1);
                            parm.mship3 = rs.getString(2);
                            parm.mtype3 = rs.getString(3);
                            parm.hndcp3 = rs.getFloat(4);
                            parm.mNum3 = rs.getString(5);
                            parm.mstype3 = rs.getString(6);
                            parm.lname3 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname3 = rs.getString(8);
                            parm.mi3 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg1++;           // group 1
                            user = parm.user3;      // save as last member (for guests)

                            parm.player3 = buildName(parm.lname3, parm.fname3, parm.mi3);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval3 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[3] = "";
            found = false;
            if (!parm.player4.equals("") && !parm.player4.equalsIgnoreCase("x")) {

                i = 0;
                loop4:
                while (i < parm2.MAX_Guests) {

                    if (parm.player4.startsWith(parm2.guest[i])) {

                        parm.g[3] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[3] = parm.player4;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg1++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id4 != 0 || !parm.player4.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id4 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player4, parm.guest_id4, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player4;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[3] = true;
                            }
                        }

                        found = true;
                        parm.userg[3] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player4;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop4;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player4.equalsIgnoreCase(parm.player5))
                            || (parm.player4.equalsIgnoreCase(parm.player6)) || (parm.player4.equalsIgnoreCase(parm.player7))
                            || (parm.player4.equalsIgnoreCase(parm.player8)) || (parm.player4.equalsIgnoreCase(parm.player9))
                            || (parm.player4.equalsIgnoreCase(parm.player10)) || (parm.player4.equalsIgnoreCase(parm.player11))
                            || (parm.player4.equalsIgnoreCase(parm.player12)) || (parm.player4.equalsIgnoreCase(parm.player13))
                            || (parm.player4.equalsIgnoreCase(parm.player14)) || (parm.player4.equalsIgnoreCase(parm.player15))
                            || (parm.player4.equalsIgnoreCase(parm.player16)) || (parm.player4.equalsIgnoreCase(parm.player17))
                            || (parm.player4.equalsIgnoreCase(parm.player18)) || (parm.player4.equalsIgnoreCase(parm.player19))
                            || (parm.player4.equalsIgnoreCase(parm.player20)) || (parm.player4.equalsIgnoreCase(parm.player21))
                            || (parm.player4.equalsIgnoreCase(parm.player22)) || (parm.player4.equalsIgnoreCase(parm.player23))
                            || (parm.player4.equalsIgnoreCase(parm.player24)) || (parm.player4.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player4);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player4);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player4);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player4);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname4 = tok.nextToken();
                        parm.lname4 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname4 = tok.nextToken();
                        parm.mi4 = tok.nextToken();
                        parm.lname4 = tok.nextToken();
                    }

                    if ((!parm.fname4.equals("")) && (!parm.lname4.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname4);
                        pstmt1.setString(2, parm.fname4);
                        pstmt1.setString(3, parm.mi4);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user4 = rs.getString(1);
                            parm.mship4 = rs.getString(2);
                            parm.mtype4 = rs.getString(3);
                            parm.hndcp4 = rs.getFloat(4);
                            parm.mNum4 = rs.getString(5);
                            parm.mstype4 = rs.getString(6);
                            parm.lname4 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname4 = rs.getString(8);
                            parm.mi4 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg1++;           // group 1
                            user = parm.user4;      // save as last member (for guests)

                            parm.player4 = buildName(parm.lname4, parm.fname4, parm.mi4);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval4 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[4] = "";
            found = false;
            if (!parm.player5.equals("") && !parm.player5.equalsIgnoreCase("x")) {

                i = 0;
                loop5:
                while (i < parm2.MAX_Guests) {

                    if (parm.player5.startsWith(parm2.guest[i])) {

                        parm.g[4] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[4] = parm.player5;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg1++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg2++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id5 != 0 || !parm.player5.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id5 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player5, parm.guest_id5, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player5;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[4] = true;
                            }
                        }

                        found = true;
                        parm.userg[4] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player5;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop5;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player5.equalsIgnoreCase(parm.player6)) || (parm.player5.equalsIgnoreCase(parm.player7))
                            || (parm.player5.equalsIgnoreCase(parm.player8)) || (parm.player5.equalsIgnoreCase(parm.player9))
                            || (parm.player5.equalsIgnoreCase(parm.player10)) || (parm.player5.equalsIgnoreCase(parm.player11))
                            || (parm.player5.equalsIgnoreCase(parm.player12)) || (parm.player5.equalsIgnoreCase(parm.player13))
                            || (parm.player5.equalsIgnoreCase(parm.player14)) || (parm.player5.equalsIgnoreCase(parm.player15))
                            || (parm.player5.equalsIgnoreCase(parm.player16)) || (parm.player5.equalsIgnoreCase(parm.player17))
                            || (parm.player5.equalsIgnoreCase(parm.player18)) || (parm.player5.equalsIgnoreCase(parm.player19))
                            || (parm.player5.equalsIgnoreCase(parm.player20)) || (parm.player5.equalsIgnoreCase(parm.player21))
                            || (parm.player5.equalsIgnoreCase(parm.player22)) || (parm.player5.equalsIgnoreCase(parm.player23))
                            || (parm.player5.equalsIgnoreCase(parm.player24)) || (parm.player5.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player5);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player5);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player5);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player5);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname5 = tok.nextToken();
                        parm.lname5 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname5 = tok.nextToken();
                        parm.mi5 = tok.nextToken();
                        parm.lname5 = tok.nextToken();
                    }

                    if ((!parm.fname5.equals("")) && (!parm.lname5.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname5);
                        pstmt1.setString(2, parm.fname5);
                        pstmt1.setString(3, parm.mi5);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user5 = rs.getString(1);
                            parm.mship5 = rs.getString(2);
                            parm.mtype5 = rs.getString(3);
                            parm.hndcp5 = rs.getFloat(4);
                            parm.mNum5 = rs.getString(5);
                            parm.mstype5 = rs.getString(6);
                            parm.lname5 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname5 = rs.getString(8);
                            parm.mi5 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg1++;           // group 1
                            } else {
                                parm.memg2++;           // group 2
                            }
                            user = parm.user5;      // save as last member (for guests)

                            parm.player5 = buildName(parm.lname5, parm.fname5, parm.mi5);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval5 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[5] = "";
            found = false;
            if (!parm.player6.equals("") && !parm.player6.equalsIgnoreCase("x")) {

                i = 0;
                loop6:
                while (i < parm2.MAX_Guests) {

                    if (parm.player6.startsWith(parm2.guest[i])) {

                        parm.g[5] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[5] = parm.player6;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg2++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id6 != 0 || !parm.player6.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id6 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player6, parm.guest_id6, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player6;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[5] = true;
                            }
                        }

                        found = true;
                        parm.userg[5] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player6;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop6;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player6.equalsIgnoreCase(parm.player7))
                            || (parm.player6.equalsIgnoreCase(parm.player8)) || (parm.player6.equalsIgnoreCase(parm.player9))
                            || (parm.player6.equalsIgnoreCase(parm.player10)) || (parm.player6.equalsIgnoreCase(parm.player11))
                            || (parm.player6.equalsIgnoreCase(parm.player12)) || (parm.player6.equalsIgnoreCase(parm.player13))
                            || (parm.player6.equalsIgnoreCase(parm.player14)) || (parm.player6.equalsIgnoreCase(parm.player15))
                            || (parm.player6.equalsIgnoreCase(parm.player16)) || (parm.player6.equalsIgnoreCase(parm.player17))
                            || (parm.player6.equalsIgnoreCase(parm.player18)) || (parm.player6.equalsIgnoreCase(parm.player19))
                            || (parm.player6.equalsIgnoreCase(parm.player20)) || (parm.player6.equalsIgnoreCase(parm.player21))
                            || (parm.player6.equalsIgnoreCase(parm.player22)) || (parm.player6.equalsIgnoreCase(parm.player23))
                            || (parm.player6.equalsIgnoreCase(parm.player24)) || (parm.player6.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player6);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player6);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player6);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player6);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname6 = tok.nextToken();
                        parm.lname6 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname6 = tok.nextToken();
                        parm.mi6 = tok.nextToken();
                        parm.lname6 = tok.nextToken();
                    }

                    if ((!parm.fname6.equals("")) && (!parm.lname6.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname6);
                        pstmt1.setString(2, parm.fname6);
                        pstmt1.setString(3, parm.mi6);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user6 = rs.getString(1);
                            parm.mship6 = rs.getString(2);
                            parm.mtype6 = rs.getString(3);
                            parm.hndcp6 = rs.getFloat(4);
                            parm.mNum6 = rs.getString(5);
                            parm.mstype6 = rs.getString(6);
                            parm.lname6 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname6 = rs.getString(8);
                            parm.mi6 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg2++;           // group 2
                            user = parm.user6;      // save as last member (for guests)

                            parm.player6 = buildName(parm.lname6, parm.fname6, parm.mi6);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval6 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[6] = "";
            found = false;
            if (!parm.player7.equals("") && !parm.player7.equalsIgnoreCase("x")) {

                i = 0;
                loop7:
                while (i < parm2.MAX_Guests) {

                    if (parm.player7.startsWith(parm2.guest[i])) {

                        parm.g[6] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[6] = parm.player7;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg2++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id7 != 0 || !parm.player7.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id7 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player7, parm.guest_id7, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player7;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[6] = true;
                            }
                        }

                        found = true;
                        parm.userg[6] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player7;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop7;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player7.equalsIgnoreCase(parm.player8)) || (parm.player7.equalsIgnoreCase(parm.player9))
                            || (parm.player7.equalsIgnoreCase(parm.player10)) || (parm.player7.equalsIgnoreCase(parm.player11))
                            || (parm.player7.equalsIgnoreCase(parm.player12)) || (parm.player7.equalsIgnoreCase(parm.player13))
                            || (parm.player7.equalsIgnoreCase(parm.player14)) || (parm.player7.equalsIgnoreCase(parm.player15))
                            || (parm.player7.equalsIgnoreCase(parm.player16)) || (parm.player7.equalsIgnoreCase(parm.player17))
                            || (parm.player7.equalsIgnoreCase(parm.player18)) || (parm.player7.equalsIgnoreCase(parm.player19))
                            || (parm.player7.equalsIgnoreCase(parm.player20)) || (parm.player7.equalsIgnoreCase(parm.player21))
                            || (parm.player7.equalsIgnoreCase(parm.player22)) || (parm.player7.equalsIgnoreCase(parm.player23))
                            || (parm.player7.equalsIgnoreCase(parm.player24)) || (parm.player7.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player7);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player7);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player7);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player7);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname7 = tok.nextToken();
                        parm.lname7 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname7 = tok.nextToken();
                        parm.mi7 = tok.nextToken();
                        parm.lname7 = tok.nextToken();
                    }

                    if ((!parm.fname7.equals("")) && (!parm.lname7.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname7);
                        pstmt1.setString(2, parm.fname7);
                        pstmt1.setString(3, parm.mi7);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user7 = rs.getString(1);
                            parm.mship7 = rs.getString(2);
                            parm.mtype7 = rs.getString(3);
                            parm.hndcp7 = rs.getFloat(4);
                            parm.mNum7 = rs.getString(5);
                            parm.mstype7 = rs.getString(6);
                            parm.lname7 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname7 = rs.getString(8);
                            parm.mi7 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg2++;           // group 2
                            user = parm.user7;      // save as last member (for guests)

                            parm.player7 = buildName(parm.lname7, parm.fname7, parm.mi7);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval7 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[7] = "";
            found = false;
            if (!parm.player8.equals("") && !parm.player8.equalsIgnoreCase("x")) {

                i = 0;
                loop8:
                while (i < parm2.MAX_Guests) {

                    if (parm.player8.startsWith(parm2.guest[i])) {

                        parm.g[7] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[7] = parm.player8;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg2++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id8 != 0 || !parm.player8.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id8 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player8, parm.guest_id8, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player8;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[7] = true;
                            }
                        }

                        found = true;
                        parm.userg[7] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player8;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop8;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player8.equalsIgnoreCase(parm.player9))
                            || (parm.player8.equalsIgnoreCase(parm.player10)) || (parm.player8.equalsIgnoreCase(parm.player11))
                            || (parm.player8.equalsIgnoreCase(parm.player12)) || (parm.player8.equalsIgnoreCase(parm.player13))
                            || (parm.player8.equalsIgnoreCase(parm.player14)) || (parm.player8.equalsIgnoreCase(parm.player15))
                            || (parm.player8.equalsIgnoreCase(parm.player16)) || (parm.player8.equalsIgnoreCase(parm.player17))
                            || (parm.player8.equalsIgnoreCase(parm.player18)) || (parm.player8.equalsIgnoreCase(parm.player19))
                            || (parm.player8.equalsIgnoreCase(parm.player20)) || (parm.player8.equalsIgnoreCase(parm.player21))
                            || (parm.player8.equalsIgnoreCase(parm.player22)) || (parm.player8.equalsIgnoreCase(parm.player23))
                            || (parm.player8.equalsIgnoreCase(parm.player24)) || (parm.player8.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player8);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player8);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player8);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player8);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname8 = tok.nextToken();
                        parm.lname8 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname8 = tok.nextToken();
                        parm.mi8 = tok.nextToken();
                        parm.lname8 = tok.nextToken();
                    }

                    if ((!parm.fname8.equals("")) && (!parm.lname8.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname8);
                        pstmt1.setString(2, parm.fname8);
                        pstmt1.setString(3, parm.mi8);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user8 = rs.getString(1);
                            parm.mship8 = rs.getString(2);
                            parm.mtype8 = rs.getString(3);
                            parm.hndcp8 = rs.getFloat(4);
                            parm.mNum8 = rs.getString(5);
                            parm.mstype8 = rs.getString(6);
                            parm.lname8 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname8 = rs.getString(8);
                            parm.mi8 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg2++;           // group 2
                            user = parm.user8;      // save as last member (for guests)

                            parm.player8 = buildName(parm.lname8, parm.fname8, parm.mi8);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval8 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[8] = "";
            found = false;
            if (!parm.player9.equals("") && !parm.player9.equalsIgnoreCase("x")) {

                i = 0;
                loop9:
                while (i < parm2.MAX_Guests) {

                    if (parm.player9.startsWith(parm2.guest[i])) {

                        parm.g[8] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[8] = parm.player9;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg2++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg3++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id9 != 0 || !parm.player9.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id9 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player9, parm.guest_id9, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player9;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[8] = true;
                            }
                        }
                        found = true;
                        parm.userg[8] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player9;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop9;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player9.equalsIgnoreCase(parm.player10)) || (parm.player9.equalsIgnoreCase(parm.player11))
                            || (parm.player9.equalsIgnoreCase(parm.player12)) || (parm.player9.equalsIgnoreCase(parm.player13))
                            || (parm.player9.equalsIgnoreCase(parm.player14)) || (parm.player9.equalsIgnoreCase(parm.player15))
                            || (parm.player9.equalsIgnoreCase(parm.player16)) || (parm.player9.equalsIgnoreCase(parm.player17))
                            || (parm.player9.equalsIgnoreCase(parm.player18)) || (parm.player9.equalsIgnoreCase(parm.player19))
                            || (parm.player9.equalsIgnoreCase(parm.player20)) || (parm.player9.equalsIgnoreCase(parm.player21))
                            || (parm.player9.equalsIgnoreCase(parm.player22)) || (parm.player9.equalsIgnoreCase(parm.player23))
                            || (parm.player9.equalsIgnoreCase(parm.player24)) || (parm.player9.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player9);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player9);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player9);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player9);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname9 = tok.nextToken();
                        parm.lname9 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname9 = tok.nextToken();
                        parm.mi9 = tok.nextToken();
                        parm.lname9 = tok.nextToken();
                    }

                    if ((!parm.fname9.equals("")) && (!parm.lname9.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname9);
                        pstmt1.setString(2, parm.fname9);
                        pstmt1.setString(3, parm.mi9);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user9 = rs.getString(1);
                            parm.mship9 = rs.getString(2);
                            parm.mtype9 = rs.getString(3);
                            parm.hndcp9 = rs.getFloat(4);
                            parm.mNum9 = rs.getString(5);
                            parm.mstype9 = rs.getString(6);
                            parm.lname9 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname9 = rs.getString(8);
                            parm.mi9 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg2++;           // group 2
                            } else {
                                parm.memg3++;           // group 3
                            }
                            user = parm.user9;      // save as last member (for guests)

                            parm.player9 = buildName(parm.lname9, parm.fname9, parm.mi9);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval9 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[9] = "";
            found = false;
            if (!parm.player10.equals("") && !parm.player10.equalsIgnoreCase("x")) {

                i = 0;
                loop10:
                while (i < parm2.MAX_Guests) {

                    if (parm.player10.startsWith(parm2.guest[i])) {

                        parm.g[9] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[9] = parm.player10;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg2++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg3++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id10 != 0 || !parm.player10.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id10 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player10, parm.guest_id10, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player10;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[9] = true;
                            }
                        }

                        found = true;
                        parm.userg[9] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player10;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop10;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player10.equalsIgnoreCase(parm.player11))
                            || (parm.player10.equalsIgnoreCase(parm.player12)) || (parm.player10.equalsIgnoreCase(parm.player13))
                            || (parm.player10.equalsIgnoreCase(parm.player14)) || (parm.player10.equalsIgnoreCase(parm.player15))
                            || (parm.player10.equalsIgnoreCase(parm.player16)) || (parm.player10.equalsIgnoreCase(parm.player17))
                            || (parm.player10.equalsIgnoreCase(parm.player18)) || (parm.player10.equalsIgnoreCase(parm.player19))
                            || (parm.player10.equalsIgnoreCase(parm.player20)) || (parm.player10.equalsIgnoreCase(parm.player21))
                            || (parm.player10.equalsIgnoreCase(parm.player22)) || (parm.player10.equalsIgnoreCase(parm.player23))
                            || (parm.player10.equalsIgnoreCase(parm.player24)) || (parm.player10.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player10);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player10);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player10);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player10);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname10 = tok.nextToken();
                        parm.lname10 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname10 = tok.nextToken();
                        parm.mi10 = tok.nextToken();
                        parm.lname10 = tok.nextToken();
                    }

                    if ((!parm.fname10.equals("")) && (!parm.lname10.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname10);
                        pstmt1.setString(2, parm.fname10);
                        pstmt1.setString(3, parm.mi10);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user10 = rs.getString(1);
                            parm.mship10 = rs.getString(2);
                            parm.mtype10 = rs.getString(3);
                            parm.hndcp10 = rs.getFloat(4);
                            parm.mNum10 = rs.getString(5);
                            parm.mstype10 = rs.getString(6);
                            parm.lname10 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname10 = rs.getString(8);
                            parm.mi10 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg2++;           // group 2
                            } else {
                                parm.memg3++;           // group 3
                            }
                            user = parm.user10;      // save as last member (for guests)

                            parm.player10 = buildName(parm.lname10, parm.fname10, parm.mi10);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval10 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[10] = "";
            found = false;
            if (!parm.player11.equals("") && !parm.player11.equalsIgnoreCase("x")) {

                i = 0;
                loop11:
                while (i < parm2.MAX_Guests) {

                    if (parm.player11.startsWith(parm2.guest[i])) {

                        parm.g[10] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[10] = parm.player11;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg3++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id11 != 0 || !parm.player11.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id11 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player11, parm.guest_id11, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player11;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[10] = true;
                            }
                        }

                        found = true;
                        parm.userg[10] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player11;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop11;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player11.equalsIgnoreCase(parm.player12)) || (parm.player11.equalsIgnoreCase(parm.player13))
                            || (parm.player11.equalsIgnoreCase(parm.player14)) || (parm.player11.equalsIgnoreCase(parm.player15))
                            || (parm.player11.equalsIgnoreCase(parm.player16)) || (parm.player11.equalsIgnoreCase(parm.player17))
                            || (parm.player11.equalsIgnoreCase(parm.player18)) || (parm.player11.equalsIgnoreCase(parm.player19))
                            || (parm.player11.equalsIgnoreCase(parm.player20)) || (parm.player11.equalsIgnoreCase(parm.player21))
                            || (parm.player11.equalsIgnoreCase(parm.player22)) || (parm.player11.equalsIgnoreCase(parm.player23))
                            || (parm.player11.equalsIgnoreCase(parm.player24)) || (parm.player11.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player11);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player11);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player11);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player11);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname11 = tok.nextToken();
                        parm.lname11 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname11 = tok.nextToken();
                        parm.mi11 = tok.nextToken();
                        parm.lname11 = tok.nextToken();
                    }

                    if ((!parm.fname11.equals("")) && (!parm.lname11.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname11);
                        pstmt1.setString(2, parm.fname11);
                        pstmt1.setString(3, parm.mi11);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user11 = rs.getString(1);
                            parm.mship11 = rs.getString(2);
                            parm.mtype11 = rs.getString(3);
                            parm.hndcp11 = rs.getFloat(4);
                            parm.mNum11 = rs.getString(5);
                            parm.mstype11 = rs.getString(6);
                            parm.lname11 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname11 = rs.getString(8);
                            parm.mi11 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg3++;           // group 3
                            user = parm.user11;      // save as last member (for guests)

                            parm.player11 = buildName(parm.lname11, parm.fname11, parm.mi11);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval11 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[11] = "";
            found = false;
            if (!parm.player12.equals("") && !parm.player12.equalsIgnoreCase("x")) {

                i = 0;
                loop12:
                while (i < parm2.MAX_Guests) {

                    if (parm.player12.startsWith(parm2.guest[i])) {

                        parm.g[11] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[11] = parm.player12;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg3++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id12 != 0 || !parm.player12.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id12 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player12, parm.guest_id12, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player12;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[11] = true;
                            }
                        }

                        found = true;
                        parm.userg[11] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player12;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop12;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player12.equalsIgnoreCase(parm.player13))
                            || (parm.player12.equalsIgnoreCase(parm.player14)) || (parm.player12.equalsIgnoreCase(parm.player15))
                            || (parm.player12.equalsIgnoreCase(parm.player16)) || (parm.player12.equalsIgnoreCase(parm.player17))
                            || (parm.player12.equalsIgnoreCase(parm.player18)) || (parm.player12.equalsIgnoreCase(parm.player19))
                            || (parm.player12.equalsIgnoreCase(parm.player20)) || (parm.player12.equalsIgnoreCase(parm.player21))
                            || (parm.player12.equalsIgnoreCase(parm.player22)) || (parm.player12.equalsIgnoreCase(parm.player23))
                            || (parm.player12.equalsIgnoreCase(parm.player24)) || (parm.player12.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player12);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player12);     // space is the default token

                    if (tok.countTokens() == 1) {    // if not X

                        invData(out, parm.player12);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player12);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname12 = tok.nextToken();
                        parm.lname12 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname12 = tok.nextToken();
                        parm.mi12 = tok.nextToken();
                        parm.lname12 = tok.nextToken();
                    }

                    if ((!parm.fname12.equals("")) && (!parm.lname12.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname12);
                        pstmt1.setString(2, parm.fname12);
                        pstmt1.setString(3, parm.mi12);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user12 = rs.getString(1);
                            parm.mship12 = rs.getString(2);
                            parm.mtype12 = rs.getString(3);
                            parm.hndcp12 = rs.getFloat(4);
                            parm.mNum12 = rs.getString(5);
                            parm.mstype12 = rs.getString(6);
                            parm.lname12 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname12 = rs.getString(8);
                            parm.mi12 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg3++;           // group 3
                            user = parm.user12;      // save as last member (for guests)

                            parm.player12 = buildName(parm.lname12, parm.fname12, parm.mi12);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval12 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[12] = "";
            found = false;
            if (!parm.player13.equals("") && !parm.player13.equalsIgnoreCase("x")) {

                i = 0;
                loop13:
                while (i < parm2.MAX_Guests) {

                    if (parm.player13.startsWith(parm2.guest[i])) {

                        parm.g[12] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[12] = parm.player13;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg3++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg4++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id13 != 0 || !parm.player13.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id13 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player13, parm.guest_id13, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player13;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[12] = true;
                            }
                        }

                        found = true;
                        parm.userg[12] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player13;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop13;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player13.equalsIgnoreCase(parm.player14)) || (parm.player13.equalsIgnoreCase(parm.player15))
                            || (parm.player13.equalsIgnoreCase(parm.player16)) || (parm.player13.equalsIgnoreCase(parm.player17))
                            || (parm.player13.equalsIgnoreCase(parm.player18)) || (parm.player13.equalsIgnoreCase(parm.player19))
                            || (parm.player13.equalsIgnoreCase(parm.player20)) || (parm.player13.equalsIgnoreCase(parm.player21))
                            || (parm.player13.equalsIgnoreCase(parm.player22)) || (parm.player13.equalsIgnoreCase(parm.player23))
                            || (parm.player13.equalsIgnoreCase(parm.player24)) || (parm.player13.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player13);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player13);     // space is the default token

                    if (tok.countTokens() == 1) {    // if not X

                        invData(out, parm.player13);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player13);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname13 = tok.nextToken();
                        parm.lname13 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname13 = tok.nextToken();
                        parm.mi13 = tok.nextToken();
                        parm.lname13 = tok.nextToken();
                    }

                    if ((!parm.fname13.equals("")) && (!parm.lname13.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname13);
                        pstmt1.setString(2, parm.fname13);
                        pstmt1.setString(3, parm.mi13);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user13 = rs.getString(1);
                            parm.mship13 = rs.getString(2);
                            parm.mtype13 = rs.getString(3);
                            parm.hndcp13 = rs.getFloat(4);
                            parm.mNum13 = rs.getString(5);
                            parm.mstype13 = rs.getString(6);
                            parm.lname13 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname13 = rs.getString(8);
                            parm.mi13 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg3++;           // group 3
                            } else {
                                parm.memg4++;           // group 4
                            }
                            user = parm.user13;      // save as last member (for guests)

                            parm.player13 = buildName(parm.lname13, parm.fname13, parm.mi13);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval13 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[13] = "";
            found = false;
            if (!parm.player14.equals("") && !parm.player14.equalsIgnoreCase("x")) {

                i = 0;
                loop14:
                while (i < parm2.MAX_Guests) {

                    if (parm.player14.startsWith(parm2.guest[i])) {

                        parm.g[13] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[13] = parm.player14;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg3++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg4++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id14 != 0 || !parm.player14.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id14 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player14, parm.guest_id14, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player14;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[13] = true;
                            }
                        }

                        found = true;
                        parm.userg[13] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player14;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop14;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player14.equalsIgnoreCase(parm.player15))
                            || (parm.player14.equalsIgnoreCase(parm.player16)) || (parm.player14.equalsIgnoreCase(parm.player17))
                            || (parm.player14.equalsIgnoreCase(parm.player18)) || (parm.player14.equalsIgnoreCase(parm.player19))
                            || (parm.player14.equalsIgnoreCase(parm.player20)) || (parm.player14.equalsIgnoreCase(parm.player21))
                            || (parm.player14.equalsIgnoreCase(parm.player22)) || (parm.player14.equalsIgnoreCase(parm.player23))
                            || (parm.player14.equalsIgnoreCase(parm.player24)) || (parm.player14.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player14);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player14);     // space is the default token

                    if (tok.countTokens() == 1) {    // if not X

                        invData(out, parm.player14);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player14);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname14 = tok.nextToken();
                        parm.lname14 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname14 = tok.nextToken();
                        parm.mi14 = tok.nextToken();
                        parm.lname14 = tok.nextToken();
                    }

                    if ((!parm.fname14.equals("")) && (!parm.lname14.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname14);
                        pstmt1.setString(2, parm.fname14);
                        pstmt1.setString(3, parm.mi14);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user14 = rs.getString(1);
                            parm.mship14 = rs.getString(2);
                            parm.mtype14 = rs.getString(3);
                            parm.hndcp14 = rs.getFloat(4);
                            parm.mNum14 = rs.getString(5);
                            parm.mstype14 = rs.getString(6);
                            parm.lname14 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname14 = rs.getString(8);
                            parm.mi14 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg3++;           // group 3
                            } else {
                                parm.memg4++;           // group 4
                            }
                            user = parm.user14;      // save as last member (for guests)

                            parm.player14 = buildName(parm.lname14, parm.fname14, parm.mi14);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval14 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[14] = "";
            found = false;
            if (!parm.player15.equals("") && !parm.player15.equalsIgnoreCase("x")) {

                i = 0;
                loop15:
                while (i < parm2.MAX_Guests) {

                    if (parm.player15.startsWith(parm2.guest[i])) {

                        parm.g[14] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[14] = parm.player15;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg3++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg4++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id15 != 0 || !parm.player15.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id15 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player15, parm.guest_id15, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player15;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[14] = true;
                            }
                        }

                        found = true;
                        parm.userg[14] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player15;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop15;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player15.equalsIgnoreCase(parm.player16)) || (parm.player15.equalsIgnoreCase(parm.player17))
                            || (parm.player15.equalsIgnoreCase(parm.player18)) || (parm.player15.equalsIgnoreCase(parm.player19))
                            || (parm.player15.equalsIgnoreCase(parm.player20)) || (parm.player15.equalsIgnoreCase(parm.player21))
                            || (parm.player15.equalsIgnoreCase(parm.player22)) || (parm.player15.equalsIgnoreCase(parm.player23))
                            || (parm.player15.equalsIgnoreCase(parm.player24)) || (parm.player15.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player15);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player15);     // space is the default token

                    if (tok.countTokens() == 1) {    // if not X

                        invData(out, parm.player15);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player15);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname15 = tok.nextToken();
                        parm.lname15 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname15 = tok.nextToken();
                        parm.mi15 = tok.nextToken();
                        parm.lname15 = tok.nextToken();
                    }

                    if ((!parm.fname15.equals("")) && (!parm.lname15.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname15);
                        pstmt1.setString(2, parm.fname15);
                        pstmt1.setString(3, parm.mi15);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user15 = rs.getString(1);
                            parm.mship15 = rs.getString(2);
                            parm.mtype15 = rs.getString(3);
                            parm.hndcp15 = rs.getFloat(4);
                            parm.mNum15 = rs.getString(5);
                            parm.mstype15 = rs.getString(6);
                            parm.lname15 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname15 = rs.getString(8);
                            parm.mi15 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg3++;           // group 3
                            } else {
                                parm.memg4++;           // group 4
                            }
                            user = parm.user15;      // save as last member (for guests)

                            parm.player15 = buildName(parm.lname15, parm.fname15, parm.mi15);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval15 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[15] = "";
            found = false;
            if (!parm.player16.equals("") && !parm.player16.equalsIgnoreCase("x")) {

                i = 0;
                loop16:
                while (i < parm2.MAX_Guests) {

                    if (parm.player16.startsWith(parm2.guest[i])) {

                        parm.g[15] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[15] = parm.player16;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg4++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id16 != 0 || !parm.player16.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id16 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player16, parm.guest_id16, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player16;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[15] = true;
                            }
                        }

                        found = true;
                        parm.userg[15] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player16;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop16;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player16.equalsIgnoreCase(parm.player17))
                            || (parm.player16.equalsIgnoreCase(parm.player18)) || (parm.player16.equalsIgnoreCase(parm.player19))
                            || (parm.player16.equalsIgnoreCase(parm.player20)) || (parm.player16.equalsIgnoreCase(parm.player21))
                            || (parm.player16.equalsIgnoreCase(parm.player22)) || (parm.player16.equalsIgnoreCase(parm.player23))
                            || (parm.player16.equalsIgnoreCase(parm.player24)) || (parm.player16.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player16);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player16);     // space is the default token

                    if (tok.countTokens() == 1) {    // if not X

                        invData(out, parm.player16);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player16);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname16 = tok.nextToken();
                        parm.lname16 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname16 = tok.nextToken();
                        parm.mi16 = tok.nextToken();
                        parm.lname16 = tok.nextToken();
                    }

                    if ((!parm.fname16.equals("")) && (!parm.lname16.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname16);
                        pstmt1.setString(2, parm.fname16);
                        pstmt1.setString(3, parm.mi16);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user16 = rs.getString(1);
                            parm.mship16 = rs.getString(2);
                            parm.mtype16 = rs.getString(3);
                            parm.hndcp16 = rs.getFloat(4);
                            parm.mNum16 = rs.getString(5);
                            parm.mstype16 = rs.getString(6);
                            parm.lname16 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname16 = rs.getString(8);
                            parm.mi16 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg4++;           // group 4
                            user = parm.user16;      // save as last member (for guests)

                            parm.player16 = buildName(parm.lname16, parm.fname16, parm.mi16);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval16 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[16] = "";
            found = false;
            if (!parm.player17.equals("") && !parm.player17.equalsIgnoreCase("x")) {

                i = 0;
                loop17:
                while (i < parm2.MAX_Guests) {

                    if (parm.player17.startsWith(parm2.guest[i])) {

                        parm.g[16] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[16] = parm.player17;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg4++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg5++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id17 != 0 || !parm.player17.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id17 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player17, parm.guest_id17, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player17;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[16] = true;
                            }
                        }

                        found = true;
                        parm.userg[16] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player17;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop17;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    if ((parm.player17.equalsIgnoreCase(parm.player18)) || (parm.player17.equalsIgnoreCase(parm.player19))
                            || (parm.player17.equalsIgnoreCase(parm.player20)) || (parm.player17.equalsIgnoreCase(parm.player21))
                            || (parm.player17.equalsIgnoreCase(parm.player22)) || (parm.player17.equalsIgnoreCase(parm.player23))
                            || (parm.player17.equalsIgnoreCase(parm.player24)) || (parm.player17.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player17);                        // reject
                        error = true;
                        return (error);
                    }

                    tok = new StringTokenizer(parm.player17);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player17);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player17);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname17 = tok.nextToken();
                        parm.lname17 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname17 = tok.nextToken();
                        parm.mi17 = tok.nextToken();
                        parm.lname17 = tok.nextToken();
                    }

                    if ((!parm.fname17.equals("")) && (!parm.lname17.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname17);
                        pstmt1.setString(2, parm.fname17);
                        pstmt1.setString(3, parm.mi17);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user17 = rs.getString(1);
                            parm.mship17 = rs.getString(2);
                            parm.mtype17 = rs.getString(3);
                            parm.hndcp17 = rs.getFloat(4);
                            parm.mNum17 = rs.getString(5);
                            parm.mstype17 = rs.getString(6);
                            parm.lname17 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname17 = rs.getString(8);
                            parm.mi17 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg4++;           // group 4
                            } else {
                                parm.memg5++;           // group 5
                            }
                            user = parm.user17;      // save as last member (for guests)

                            parm.player17 = buildName(parm.lname17, parm.fname17, parm.mi17);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval17 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[17] = "";
            found = false;
            if (!parm.player18.equals("") && !parm.player18.equalsIgnoreCase("x")) {

                i = 0;
                loop18:
                while (i < parm2.MAX_Guests) {

                    if (parm.player18.startsWith(parm2.guest[i])) {

                        parm.g[17] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[17] = parm.player18;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg4++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg5++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id18 != 0 || !parm.player18.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id18 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player18, parm.guest_id18, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player18;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[17] = true;
                            }
                        }

                        found = true;
                        parm.userg[17] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player18;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop18;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    //  if (!parm.club.equals( "longcove" ) || today_date > 20080316) {      // TEMP *********** can remove after 3/17/2008

                    if ((parm.player18.equalsIgnoreCase(parm.player19))
                            || (parm.player18.equalsIgnoreCase(parm.player20)) || (parm.player18.equalsIgnoreCase(parm.player21))
                            || (parm.player18.equalsIgnoreCase(parm.player22)) || (parm.player18.equalsIgnoreCase(parm.player23))
                            || (parm.player18.equalsIgnoreCase(parm.player24)) || (parm.player18.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player18);                        // reject
                        error = true;
                        return (error);
                    }
                    //  }

                    tok = new StringTokenizer(parm.player18);     // space is the default token

                    if (tok.countTokens() == 1) {    // if not X

                        invData(out, parm.player18);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player18);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname18 = tok.nextToken();
                        parm.lname18 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname18 = tok.nextToken();
                        parm.mi18 = tok.nextToken();
                        parm.lname18 = tok.nextToken();
                    }

                    if ((!parm.fname18.equals("")) && (!parm.lname18.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname18);
                        pstmt1.setString(2, parm.fname18);
                        pstmt1.setString(3, parm.mi18);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user18 = rs.getString(1);
                            parm.mship18 = rs.getString(2);
                            parm.mtype18 = rs.getString(3);
                            parm.hndcp18 = rs.getFloat(4);
                            parm.mNum18 = rs.getString(5);
                            parm.mstype18 = rs.getString(6);
                            parm.lname18 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname18 = rs.getString(8);
                            parm.mi18 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg4++;           // group 4
                            } else {
                                parm.memg5++;           // group 5
                            }
                            user = parm.user18;      // save as last member (for guests)

                            parm.player18 = buildName(parm.lname18, parm.fname18, parm.mi18);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval18 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[18] = "";
            found = false;
            if (!parm.player19.equals("") && !parm.player19.equalsIgnoreCase("x")) {

                i = 0;
                loop19:
                while (i < parm2.MAX_Guests) {

                    if (parm.player19.startsWith(parm2.guest[i])) {

                        parm.g[18] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[18] = parm.player19;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg4++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg5++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id19 != 0 || !parm.player19.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id19 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player19, parm.guest_id19, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player19;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[18] = true;
                            }
                        }

                        found = true;
                        parm.userg[18] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player19;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop19;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    //  if (!parm.club.equals( "longcove" ) || today_date > 20080316) {      // TEMP *********** can remove after 3/17/2008

                    if ((parm.player19.equalsIgnoreCase(parm.player20)) || (parm.player19.equalsIgnoreCase(parm.player21))
                            || (parm.player19.equalsIgnoreCase(parm.player22)) || (parm.player19.equalsIgnoreCase(parm.player23))
                            || (parm.player19.equalsIgnoreCase(parm.player24)) || (parm.player19.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player19);                        // reject
                        error = true;
                        return (error);
                    }
                    //  }

                    tok = new StringTokenizer(parm.player19);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player19);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player19);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname19 = tok.nextToken();
                        parm.lname19 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname19 = tok.nextToken();
                        parm.mi19 = tok.nextToken();
                        parm.lname19 = tok.nextToken();
                    }

                    if ((!parm.fname19.equals("")) && (!parm.lname19.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname19);
                        pstmt1.setString(2, parm.fname19);
                        pstmt1.setString(3, parm.mi19);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user19 = rs.getString(1);
                            parm.mship19 = rs.getString(2);
                            parm.mtype19 = rs.getString(3);
                            parm.hndcp19 = rs.getFloat(4);
                            parm.mNum19 = rs.getString(5);
                            parm.mstype19 = rs.getString(6);
                            parm.lname19 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname19 = rs.getString(8);
                            parm.mi19 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg4++;           // group 4
                            } else {
                                parm.memg5++;           // group 5
                            }
                            user = parm.user19;      // save as last member (for guests)

                            parm.player19 = buildName(parm.lname19, parm.fname19, parm.mi19);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval19 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[19] = "";
            found = false;
            if (!parm.player20.equals("") && !parm.player20.equalsIgnoreCase("x")) {

                i = 0;
                loop20:
                while (i < parm2.MAX_Guests) {

                    if (parm.player20.startsWith(parm2.guest[i])) {

                        parm.g[19] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[19] = parm.player20;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        if (parm.p5.equals("Yes")) {
                            parm.guestsg4++;                 // increment number of guests this slot
                        } else {
                            parm.guestsg5++;                 // increment number of guests this slot
                        }

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id20 != 0 || !parm.player20.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id20 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player20, parm.guest_id20, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player20;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[19] = true;
                            }
                        }

                        found = true;
                        parm.userg[19] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player20;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop20;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    //  if (!parm.club.equals( "longcove" ) || today_date > 20080316) {      // TEMP *********** can remove after 3/17/2008

                    if ((parm.player20.equalsIgnoreCase(parm.player21))
                            || (parm.player20.equalsIgnoreCase(parm.player22)) || (parm.player20.equalsIgnoreCase(parm.player23))
                            || (parm.player20.equalsIgnoreCase(parm.player24)) || (parm.player20.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player20);                        // reject
                        error = true;
                        return (error);
                    }
                    //  }

                    tok = new StringTokenizer(parm.player20);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player20);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player20);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname20 = tok.nextToken();
                        parm.lname20 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname20 = tok.nextToken();
                        parm.mi20 = tok.nextToken();
                        parm.lname20 = tok.nextToken();
                    }

                    if ((!parm.fname20.equals("")) && (!parm.lname20.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname20);
                        pstmt1.setString(2, parm.fname20);
                        pstmt1.setString(3, parm.mi20);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user20 = rs.getString(1);
                            parm.mship20 = rs.getString(2);
                            parm.mtype20 = rs.getString(3);
                            parm.hndcp20 = rs.getFloat(4);
                            parm.mNum20 = rs.getString(5);
                            parm.mstype20 = rs.getString(6);
                            parm.lname20 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname20 = rs.getString(8);
                            parm.mi20 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            if (parm.p5.equals("Yes")) {
                                parm.memg4++;           // group 4
                            } else {
                                parm.memg5++;           // group 5
                            }
                            user = parm.user20;      // save as last member (for guests)

                            parm.player20 = buildName(parm.lname20, parm.fname20, parm.mi20);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval20 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[20] = "";
            found = false;
            if (!parm.player21.equals("") && !parm.player21.equalsIgnoreCase("x")) {

                i = 0;
                loop21:
                while (i < parm2.MAX_Guests) {

                    if (parm.player21.startsWith(parm2.guest[i])) {

                        parm.g[20] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[20] = parm.player21;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg5++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id21 != 0 || !parm.player21.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id21 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player21, parm.guest_id21, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player21;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[20] = true;
                            }
                        }

                        found = true;
                        parm.userg[20] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player21;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop21;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    // if (!parm.club.equals( "longcove" ) || today_date > 20080316) {      // TEMP *********** can remove after 3/17/2008

                    if ((parm.player21.equalsIgnoreCase(parm.player22)) || (parm.player21.equalsIgnoreCase(parm.player23))
                            || (parm.player21.equalsIgnoreCase(parm.player24)) || (parm.player21.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player21);                        // reject
                        error = true;
                        return (error);
                    }
                    //  }

                    tok = new StringTokenizer(parm.player21);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player21);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player21);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname21 = tok.nextToken();
                        parm.lname21 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname21 = tok.nextToken();
                        parm.mi21 = tok.nextToken();
                        parm.lname21 = tok.nextToken();
                    }

                    if ((!parm.fname21.equals("")) && (!parm.lname21.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname21);
                        pstmt1.setString(2, parm.fname21);
                        pstmt1.setString(3, parm.mi21);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user21 = rs.getString(1);
                            parm.mship21 = rs.getString(2);
                            parm.mtype21 = rs.getString(3);
                            parm.hndcp21 = rs.getFloat(4);
                            parm.mNum21 = rs.getString(5);
                            parm.mstype21 = rs.getString(6);
                            parm.lname21 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname21 = rs.getString(8);
                            parm.mi21 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg5++;           // group 5
                            user = parm.user21;      // save as last member (for guests)

                            parm.player21 = buildName(parm.lname21, parm.fname21, parm.mi21);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval21 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[21] = "";
            found = false;
            if (!parm.player22.equals("") && !parm.player22.equalsIgnoreCase("x")) {

                i = 0;
                loop22:
                while (i < parm2.MAX_Guests) {

                    if (parm.player22.startsWith(parm2.guest[i])) {

                        parm.g[21] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[21] = parm.player22;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg5++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id22 != 0 || !parm.player22.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id22 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player22, parm.guest_id22, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player22;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[21] = true;
                            }
                        }

                        found = true;
                        parm.userg[21] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player22;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop22;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    //  if (!parm.club.equals( "longcove" ) || today_date > 20080316) {      // TEMP *********** can remove after 3/17/2008

                    if ((parm.player22.equalsIgnoreCase(parm.player23))
                            || (parm.player22.equalsIgnoreCase(parm.player24)) || (parm.player22.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player22);                        // reject
                        error = true;
                        return (error);
                    }
                    //  }

                    tok = new StringTokenizer(parm.player22);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player22);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player22);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname22 = tok.nextToken();
                        parm.lname22 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname22 = tok.nextToken();
                        parm.mi22 = tok.nextToken();
                        parm.lname22 = tok.nextToken();
                    }

                    if ((!parm.fname22.equals("")) && (!parm.lname22.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname22);
                        pstmt1.setString(2, parm.fname22);
                        pstmt1.setString(3, parm.mi22);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user22 = rs.getString(1);
                            parm.mship22 = rs.getString(2);
                            parm.mtype22 = rs.getString(3);
                            parm.hndcp22 = rs.getFloat(4);
                            parm.mNum22 = rs.getString(5);
                            parm.mstype22 = rs.getString(6);
                            parm.lname22 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname22 = rs.getString(8);
                            parm.mi22 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg5++;           // group 5
                            user = parm.user22;      // save as last member (for guests)

                            parm.player22 = buildName(parm.lname22, parm.fname22, parm.mi22);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval22 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[22] = "";
            found = false;
            if (!parm.player23.equals("") && !parm.player23.equalsIgnoreCase("x")) {

                i = 0;
                loop23:
                while (i < parm2.MAX_Guests) {

                    if (parm.player23.startsWith(parm2.guest[i])) {

                        parm.g[22] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[22] = parm.player23;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg5++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id23 != 0 || !parm.player23.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id23 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player23, parm.guest_id23, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player23;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[22] = true;
                            }
                        }

                        found = true;
                        parm.userg[22] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player23;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop23;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    //  if (!parm.club.equals( "longcove" ) || today_date > 20080316) {      // TEMP *********** can remove after 3/17/2008

                    if ((parm.player23.equalsIgnoreCase(parm.player24)) || (parm.player23.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player23);                        // reject
                        error = true;
                        return (error);
                    }
                    //  }

                    tok = new StringTokenizer(parm.player23);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player23);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player23);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname23 = tok.nextToken();
                        parm.lname23 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname23 = tok.nextToken();
                        parm.mi23 = tok.nextToken();
                        parm.lname23 = tok.nextToken();
                    }

                    if ((!parm.fname23.equals("")) && (!parm.lname23.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname23);
                        pstmt1.setString(2, parm.fname23);
                        pstmt1.setString(3, parm.mi23);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user23 = rs.getString(1);
                            parm.mship23 = rs.getString(2);
                            parm.mtype23 = rs.getString(3);
                            parm.hndcp23 = rs.getFloat(4);
                            parm.mNum23 = rs.getString(5);
                            parm.mstype23 = rs.getString(6);
                            parm.lname23 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname23 = rs.getString(8);
                            parm.mi23 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg5++;           // group 5
                            user = parm.user23;      // save as last member (for guests)

                            parm.player23 = buildName(parm.lname23, parm.fname23, parm.mi23);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval23 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[23] = "";
            found = false;
            if (!parm.player24.equals("") && !parm.player24.equalsIgnoreCase("x")) {

                i = 0;
                loop24:
                while (i < parm2.MAX_Guests) {

                    if (parm.player24.startsWith(parm2.guest[i])) {

                        parm.g[23] = parm2.guest[i];       // indicate player is a guest name and save name
                        parm.gstA[23] = parm.player24;    // save guest value
                        parm.guests++;                 // increment number of guests this request
                        parm.guestsg5++;                 // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id24 != 0 || !parm.player24.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id24 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player24, parm.guest_id24, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player24;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[23] = true;
                            }
                        }

                        found = true;
                        parm.userg[23] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player24;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop24;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    //  if (!parm.club.equals( "longcove" ) || today_date > 20080316) {      // TEMP *********** can remove after 3/17/2008

                    if ((parm.player24.equalsIgnoreCase(parm.player25))) {

                        dupData(out, parm.player24);                        // reject
                        error = true;
                        return (error);
                    }
                    //  }

                    tok = new StringTokenizer(parm.player24);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player24);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player24);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname24 = tok.nextToken();
                        parm.lname24 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname24 = tok.nextToken();
                        parm.mi24 = tok.nextToken();
                        parm.lname24 = tok.nextToken();
                    }

                    if ((!parm.fname24.equals("")) && (!parm.lname24.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname24);
                        pstmt1.setString(2, parm.fname24);
                        pstmt1.setString(3, parm.mi24);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user24 = rs.getString(1);
                            parm.mship24 = rs.getString(2);
                            parm.mtype24 = rs.getString(3);
                            parm.hndcp24 = rs.getFloat(4);
                            parm.mNum24 = rs.getString(5);
                            parm.mstype24 = rs.getString(6);
                            parm.lname24 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname24 = rs.getString(8);
                            parm.mi24 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg5++;           // group 5
                            user = parm.user24;      // save as last member (for guests)

                            parm.player24 = buildName(parm.lname24, parm.fname24, parm.mi24);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval24 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            parm.g[24] = "";
            found = false;
            if (!parm.player25.equals("") && !parm.player25.equalsIgnoreCase("x")) {

                i = 0;
                loop25:
                while (i < parm2.MAX_Guests) {

                    if (parm.player25.startsWith(parm2.guest[i])) {

                        parm.g[24] = parm2.guest[i];      // indicate player is a guest name and save name
                        parm.gstA[24] = parm.player25;     // save guest value
                        parm.guests++;                     // increment number of guests this request
                        parm.guestsg5++;                   // increment number of guests this slot

                        if (parm2.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || parm.guest_id25 != 0 || !parm.player25.equals(parm2.guest[i] + " TBA")) {

                                if (parm.guest_id25 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(parm.player25, parm.guest_id25, parm2.guest[i], parm.club, con);
                                }

                                if (invalidGuest) {
                                    parm.gplayer = parm.player25;    // indicate error
                                    error = true;
                                    return (error);
                                }
                            } else {
                                skipNameCheck[24] = true;
                            }
                        }

                        found = true;
                        parm.userg[24] = user;             // set guest of last member found

                        if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                            gplayer = parm.player25;                   // indicate error (ok if it was already entered by pro)
                        }
                        break loop25;
                    }
                    i++;
                }         // end of while loop

                if (found == false) {               // if guest type not found - must be member

                    tok = new StringTokenizer(parm.player25);     // space is the default token

                    if (tok.countTokens() == 1) {

                        invData(out, parm.player25);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() > 3) {          // too many name fields

                        invData(out, parm.player25);                        // reject
                        error = true;
                        return (error);
                    }

                    if (tok.countTokens() == 2) {         // first name, last name

                        parm.fname25 = tok.nextToken();
                        parm.lname25 = tok.nextToken();
                    }

                    if (tok.countTokens() == 3) {         // first name, mi, last name

                        parm.fname25 = tok.nextToken();
                        parm.mi25 = tok.nextToken();
                        parm.lname25 = tok.nextToken();
                    }

                    if ((!parm.fname25.equals("")) && (!parm.lname25.equals(""))) {

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, parm.lname25);
                        pstmt1.setString(2, parm.fname25);
                        pstmt1.setString(3, parm.mi25);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            parm.user25 = rs.getString(1);
                            parm.mship25 = rs.getString(2);
                            parm.mtype25 = rs.getString(3);
                            parm.hndcp25 = rs.getFloat(4);
                            parm.mNum25 = rs.getString(5);
                            parm.mstype25 = rs.getString(6);
                            parm.lname25 = rs.getString(7);      // use name from db table to ensure correct titlecase
                            parm.fname25 = rs.getString(8);
                            parm.mi25 = rs.getString(9);

                            parm.members++;         // increment number of members this res.
                            parm.memg5++;           // group 5
                            user = parm.user25;      // save as last member (for guests)

                            parm.player25 = buildName(parm.lname25, parm.fname25, parm.mi25);    // rebuild the player name using the db name values        

                        } else {
                            parm.inval25 = 1;        // indicate invalid name entered
                        }
                    }
                }
            }

            pstmt1.close();

            //
            //  Reject if any player was a guest type that is not allowed for members
            //
            if (!gplayer.equals("")) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Data Entry Error</H3>");
                out.println("<BR><BR><b>" + gplayer + "</b> specifies a Guest Type that is not allowed for member use.");
                out.println("<BR><BR>Please correct this and try again.");
                out.println("<BR><BR>");

                goReturn(out, parm);
                error = true;               // return error
            }

            if (error == false) {

                //
                //  Reject if no members specified and unaccompanied guests are not allowed
                //
                if (((parm.memg1 == 0 && parm.guestsg1 > 0) || (parm.memg2 == 0 && parm.guestsg2 > 0) || (parm.memg3 == 0 && parm.guestsg3 > 0) 
                        || (parm.memg4 == 0 && parm.guestsg4 > 0) || (parm.memg5 == 0 && parm.guestsg5 > 0)) && parm2.unacompGuest == 0) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Data Entry Error</H3>");
                    out.println("<BR><BR>Member name not found. You must specify at least one member per tee time in the request.");
                    out.println("Member names must be specified exactly as they exist in the system.");
                    out.println("<BR><BR>Please correct this and try again.");
                    out.println("<BR><BR>");

                    goReturn(out, parm);
                    error = true;               // return error
                }
            }

            //
            //  Check for guest names if requested for this club
            //
            if (error == false && parm2.forceg > 0 && parm.guests > 0) {

                i = 0;
                gnloop1:
                while (i < 25) {        // check all players

                    if (!parm.gstA[i].equals("") && !skipNameCheck[i]) {           // if player is a guest

                        error = verifySlot.checkGstName(parm.gstA[i], parm.g[i], parm.club);      // go check for a name

                        if (error == true) {            // reject if name not specified

                            out.println(SystemUtils.HeadTitle("Data Entry Error"));
                            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                            out.println("<center>");
                            out.println("<BR><BR><H3>Data Entry Error</H3>");
                            out.println("<BR><BR>You must specify the name of your guest(s).");
                            out.println("<BR><b>" + parm.gstA[i] + "</b> does not include a valid name (must be at least first & last names).");
                            out.println("<BR><BR>To specify the name, click in the player box where the guest is specified, ");
                            out.println("<BR>move the cursor (use the arrow keys or mouse) to the end of the guest type value, ");
                            out.println("<BR>use the space bar to enter a space and then type the guest's name.");
                            out.println("<BR><BR>Please correct this and try again.");
                            out.println("<BR><BR>");

                            goReturn(out, parm);
                            error = true;               // return error
                            break gnloop1;              // exit loop
                        }
                    }

                    i++;
                }           // end of WHILE loop
            }

        } catch (Exception e1) {

            dbError(out, e1);

            error = true;
        }

        return (error);
    }

    /**
    //************************************************************************
    //
    //   buildName - build the player name with the values received
    //
    //************************************************************************
     **/
    private String buildName(String lname, String fname, String mi) {


        StringBuffer mem_name = new StringBuffer();      // for name


        //
        //  Rebuild the player name using the name values received
        //
        mem_name = new StringBuffer(fname);             // get first name

        if (!mi.equals("")) {
            mem_name.append(" ");
            mem_name.append(mi);                          // add mi if present
        }
        mem_name.append(" " + lname);                    // add last name

        String mname = mem_name.toString();             // set as player name

        return (mname);
    }

    /**
    //************************************************************************
    //
    //  checkGuestQuota - checks for maximum number of guests exceeded per member
    //                    or per membership during a specified period.
    //
    //************************************************************************
     **/
    public boolean checkGuestQuota(parmSlotm slotParms, Connection con) {


        //Statement stmt = null;
        ResultSet rs = null;

        boolean error = false;
        boolean check = false;

        int i = 0;
        int i2 = 0;
        int guests = 0;

        int stime = 0;
        int etime = 0;

        long sdate = 0;
        long edate = 0;

        String rcourse = "";
        String rest_fb = "";
        String per = "";                        // per = 'Member' or 'Tee Time'

        int[] guestsA = new int[25];         // array to hold the Guest Counts for each player position

        String[] userg = new String[25];     // array to hold the Associated Member's username for each Guest

        //String [] rguest = new String [36];    // array to hold the Guest Restriction's guest types
        ArrayList<String> rguest = new ArrayList<String>();


        if (slotParms.fb == 0) {                   // is Tee time for Front 9?

            slotParms.sfb = "Front";
        }

        if (slotParms.fb == 1) {                   // is it Back 9?

            slotParms.sfb = "Back";
        }

        try {

            PreparedStatement pstmt5 = con.prepareStatement(
                    "SELECT * "
                    + "FROM guestqta4 "
                    + "WHERE sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? AND activity_id = 0");

            pstmt5.clearParameters();        // clear the parms
            pstmt5.setLong(1, slotParms.date);
            pstmt5.setLong(2, slotParms.date);
            pstmt5.setInt(3, slotParms.time1);
            pstmt5.setInt(4, slotParms.time1);
            rs = pstmt5.executeQuery();      // execute the prepared stmt

            loop1:
            while (rs.next()) {

                sdate = rs.getLong("sdate");
                edate = rs.getLong("edate");
                stime = rs.getInt("stime");
                etime = rs.getInt("etime");
                slotParms.grest_num = rs.getInt("num_guests");
                rcourse = rs.getString("courseName");
                rest_fb = rs.getString("fb");
                per = rs.getString("per");

                // now look up the guest types for this restriction
                PreparedStatement pstmt3 = con.prepareStatement(
                        "SELECT guest_type FROM guestqta4_gtypes WHERE guestqta_id = ?");

                pstmt3.clearParameters();
                pstmt3.setInt(1, rs.getInt("id"));

                ResultSet rs2 = pstmt3.executeQuery();

                while (rs2.next()) {

                    rguest.add(rs2.getString("guest_type"));

                }
                pstmt3.close();
 
                check = false;       // init 'check guests' flag

                for (i = 0; i < 25; i++) {
                    userg[i] = "";         // init usernames
                }

                //
                //  Check if course and f/b match that specified in restriction
                //
                if ((rcourse.equals("-ALL-")) || (rcourse.equals(slotParms.course))) {

                    if ((rest_fb.equals("Both")) || (rest_fb.equals(slotParms.sfb))) {  // if f/b matches

                        //  compare guest types in tee time against those specified in restriction
                        i = 0;
                        ploop1:
                        while (i < rguest.size()) {

                            //
                            //     slotParms.gx = guest types specified in player name fields
                            //     rguest[x] = guest types from restriction gotten above
                            //
                            for (i2 = 0; i2 < 25; i2++) {

                                if (!slotParms.g[i2].equals("")) {
                                    if (slotParms.g[i2].equals(rguest.get(i))) {
                                        check = true;                          // indicate check num of guests
                                        userg[i2] = slotParms.userg[i2];       // save member associated with this guest
                                    }
                                }
                            }
                            i++;
                        }
                    }
                }      // end of IF course matches

                if (check == true) {   // if restriction exists for this day and time and there are guests in tee time

                    //
                    //  Determine the member assigned to the guest and calculate their quota count
                    //
                    for (i = 0; i < 25; i++) {
                        guestsA[i] = 0;          // init # of guests for each member
                    }

                    //
                    //  Check each member for duplicates and count these guests first
                    //
                    for (i = 0; i < 25; i++) {

                        i2 = i + 1;              // point to next user  

                        if (!userg[i].equals("")) {

                            guestsA[i]++;               // count the guest

                            while (i2 < 25) {           // loop for dups

                                if (userg[i].equals(userg[i2])) {

                                    guestsA[i]++;            // count the guest
                                    userg[i2] = "";          // remove dup
                                }
                                i2++;
                            }

                            // go count the number of guests for this member
                            guestsA[i] += countGuests(con, userg[i], slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                        }
                    }

                    //
                    //  Process according to the 'per' value; member or member number
                    //
                    if (per.startsWith("Membership")) {

                        for (i = 0; i < 25; i++) {

                            if (!userg[i].equals("")) {

                                // go count the number of guests for this member number
                                guestsA[i] += checkMnums(con, userg[i], slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                            }
                        }
                    }

                    // if num of guests in quota count (guests_) > num allowed (grest_num) per member
                    //
                    //       to get here guests_ is = # of guests accumulated for member
                    //       grest_num is 0 - 999 (restriction quota)
                    //       per is 'Member' or 'Membership Number'
                    //
                    slotParms.grest_per = per;    // save this rest's per value

                    if (guestsA[0] > slotParms.grest_num) {

                        slotParms.player = slotParms.player1;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[1] > slotParms.grest_num) {

                        slotParms.player = slotParms.player2;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[2] > slotParms.grest_num) {

                        slotParms.player = slotParms.player3;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[3] > slotParms.grest_num) {

                        slotParms.player = slotParms.player4;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[4] > slotParms.grest_num) {

                        slotParms.player = slotParms.player5;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[5] > slotParms.grest_num) {

                        slotParms.player = slotParms.player6;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[6] > slotParms.grest_num) {

                        slotParms.player = slotParms.player7;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[7] > slotParms.grest_num) {

                        slotParms.player = slotParms.player8;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[8] > slotParms.grest_num) {

                        slotParms.player = slotParms.player9;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[9] > slotParms.grest_num) {

                        slotParms.player = slotParms.player10;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[10] > slotParms.grest_num) {

                        slotParms.player = slotParms.player11;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[11] > slotParms.grest_num) {

                        slotParms.player = slotParms.player12;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[12] > slotParms.grest_num) {

                        slotParms.player = slotParms.player13;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[13] > slotParms.grest_num) {

                        slotParms.player = slotParms.player14;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[14] > slotParms.grest_num) {

                        slotParms.player = slotParms.player15;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[15] > slotParms.grest_num) {

                        slotParms.player = slotParms.player16;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[16] > slotParms.grest_num) {

                        slotParms.player = slotParms.player17;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[17] > slotParms.grest_num) {

                        slotParms.player = slotParms.player18;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[18] > slotParms.grest_num) {

                        slotParms.player = slotParms.player19;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[19] > slotParms.grest_num) {

                        slotParms.player = slotParms.player20;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[20] > slotParms.grest_num) {

                        slotParms.player = slotParms.player21;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[21] > slotParms.grest_num) {

                        slotParms.player = slotParms.player22;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[22] > slotParms.grest_num) {

                        slotParms.player = slotParms.player23;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[23] > slotParms.grest_num) {

                        slotParms.player = slotParms.player24;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }
                    if (guestsA[24] > slotParms.grest_num) {

                        slotParms.player = slotParms.player25;   // save member name
                        error = true;                 // set error flag
                        break loop1;                  // done checking - exit while loop
                    }

                }    // end of IF true
            }       // end of loop1 while loop

            pstmt5.close();

        } catch (Exception e) {
        }

        return (error);

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
    public static int checkMnums(Connection con, String user, parmSlotm slotParms, long sdate, long edate, int stime, int etime,
            String rfb, String rcourse, ArrayList<String> rguest)
            throws Exception {

        ResultSet rs = null;

        int guests = 0;

        String mNum = "";
        String tuser = "";


        //****************************************************************************
        //  per = Membership Number  -  check all members with the same Member Number
        //****************************************************************************

        try {

            //  get this user's mNum
            PreparedStatement pstmt3 = con.prepareStatement(
                    "SELECT memNum FROM member2b WHERE username = ?");

            pstmt3.clearParameters();        // clear the parms
            pstmt3.setString(1, user);
            rs = pstmt3.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

                mNum = rs.getString(1);       // get this user's member number
            }
            pstmt3.close();

            if (!mNum.equals("")) {     // if there is one specified

                //  get all users with matching mNum and put in userm array
                PreparedStatement pstmt4 = con.prepareStatement(
                        "SELECT username FROM member2b WHERE memNum = ?");

                pstmt4.clearParameters();        // clear the parms
                pstmt4.setString(1, mNum);
                rs = pstmt4.executeQuery();      // execute the prepared stmt

                while (rs.next()) {

                    tuser = rs.getString(1);       // get the username
                    if (!tuser.equals("") && !tuser.equalsIgnoreCase(user)) {   // if exists and not this user

                        // go count the number of guests for this member
                        guests += countGuests(con, tuser, slotParms, sdate, edate, stime, etime, rfb, rcourse, rguest);

                    }
                }
                pstmt4.close();
            }

        } catch (Exception e) {

            throw new UnavailableException("Error Checking Mnums for Guest Rest - verifySlot: " + e.getMessage());
        }

        return (guests);
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
    public static int countGuests(Connection con, String user, parmSlotm slotParms, long sdate, long edate, int stime, int etime,
            String rfb, String rcourse, ArrayList<String> rguest)
            throws Exception {

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

        //  convert restriction's f/b
        if (rfb.equalsIgnoreCase("Front")) {
            rest_fb = 0;
        } else {
            rest_fb = 1;
        }


        //
        //  Count all guests with matching guest types that are associated with this member (teecurr)
        //
        try {

            PreparedStatement pstmt1 = con.prepareStatement(
                    "SELECT date, time, courseName, player1, player2, player3, player4, fb, player5, "
                    + "userg1, userg2, userg3, userg4, userg5 "
                    + "FROM teecurr2 "
                    + "WHERE (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) "
                    + "AND date <= ? AND date >= ? AND time <= ? AND time >= ?");

            pstmt1.clearParameters();        // clear the parms and check player 1
            pstmt1.setString(1, user);
            pstmt1.setString(2, user);
            pstmt1.setString(3, user);
            pstmt1.setString(4, user);
            pstmt1.setString(5, user);
            pstmt1.setLong(6, edate);
            pstmt1.setLong(7, sdate);
            pstmt1.setInt(8, etime);
            pstmt1.setInt(9, stime);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

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
                if ((date != slotParms.date) || (time != slotParms.time1) || (!course.equals(slotParms.course))) {

                    //
                    //  Check if course and f/b match that specified in restriction
                    //
                    if ((rcourse.equals("-ALL-")) || (rcourse.equals(course))) {

                        if ((rfb.equals("Both")) || (rest_fb == fb)) {  // if f/b matches

                            // check if any players from the tee time are a restricted guest
                            if (user.equalsIgnoreCase(userg1)) {
                                i = 0;
                                loop1:
                                while (i < rguest.size()) {
                                    if (player1.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                        guests++;             // bump count of guests for quota check
                                        break loop1;          // exit loop
                                    }
                                    i++;
                                }
                            }
                            if (user.equalsIgnoreCase(userg2)) {
                                i = 0;
                                loop2:
                                while (i < rguest.size()) {
                                    if (player2.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                        guests++;             // bump count of guests for quota check
                                        break loop2;          // exit loop
                                    }
                                    i++;
                                }
                            }
                            if (user.equalsIgnoreCase(userg3)) {
                                i = 0;
                                loop3:
                                while (i < rguest.size()) {
                                    if (player3.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                        guests++;             // bump count of guests for quota check
                                        break loop3;          // exit loop
                                    }
                                    i++;
                                }
                            }
                            if (user.equalsIgnoreCase(userg4)) {
                                i = 0;
                                loop4:
                                while (i < rguest.size()) {
                                    if (player4.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                        guests++;             // bump count of guests for quota check
                                        break loop4;          // exit loop
                                    }
                                    i++;
                                }
                            }
                            if (user.equalsIgnoreCase(userg5)) {
                                i = 0;
                                loop5:
                                while (i < rguest.size()) {
                                    if (player5.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
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

            pstmt1.close();

            //
            //  Count all guests with matching guest types that are associated with this member (teepast)
            //
            PreparedStatement pstmt2 = con.prepareStatement(
                    "SELECT courseName, player1, player2, player3, player4, fb, player5, "
                    + "userg1, userg2, userg3, userg4, userg5 "
                    + "FROM teepast2 "
                    + "WHERE (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) "
                    + "AND date <= ? AND date >= ? AND time <= ? AND time >= ?");

            pstmt2.clearParameters();        // clear the parms and check player 1
            pstmt2.setString(1, user);
            pstmt2.setString(2, user);
            pstmt2.setString(3, user);
            pstmt2.setString(4, user);
            pstmt2.setString(5, user);
            pstmt2.setLong(6, edate);
            pstmt2.setLong(7, sdate);
            pstmt2.setInt(8, etime);
            pstmt2.setInt(9, stime);
            rs = pstmt2.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

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
                if ((rcourse.equals("-ALL-")) || (rcourse.equals(course))) {

                    if ((rfb.equals("Both")) || (rest_fb == fb)) {  // if f/b matches

                        //
                        //  matching tee time found in teecurr
                        //  check if any players from the tee time are a restricted guest
                        //
                        if (user.equalsIgnoreCase(userg1)) {
                            i = 0;
                            loop11:
                            while (i < rguest.size()) {
                                if (player1.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                    guests++;             // bump count of guests for quota check
                                    break loop11;          // exit loop
                                }
                                i++;
                            }
                        }
                        if (user.equalsIgnoreCase(userg2)) {
                            i = 0;
                            loop12:
                            while (i < rguest.size()) {
                                if (player2.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                    guests++;             // bump count of guests for quota check
                                    break loop12;          // exit loop
                                }
                                i++;
                            }
                        }
                        if (user.equalsIgnoreCase(userg3)) {
                            i = 0;
                            loop13:
                            while (i < rguest.size()) {
                                if (player3.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                    guests++;             // bump count of guests for quota check
                                    break loop13;          // exit loop
                                }
                                i++;
                            }
                        }
                        if (user.equalsIgnoreCase(userg4)) {
                            i = 0;
                            loop14:
                            while (i < rguest.size()) {
                                if (player4.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                    guests++;             // bump count of guests for quota check
                                    break loop14;          // exit loop
                                }
                                i++;
                            }
                        }
                        if (user.equalsIgnoreCase(userg5)) {
                            i = 0;
                            loop15:
                            while (i < rguest.size()) {
                                if (player5.startsWith(rguest.get(i))) {    // if matchiing guest type & associated with this user
                                    guests++;             // bump count of guests for quota check
                                    break loop15;          // exit loop
                                }
                                i++;
                            }
                        }
                    }
                }
            }   // end of WHILE

            pstmt2.close();

        } catch (Exception e) {

            throw new UnavailableException("Error Counting Guests for Guest Rest - Member_slotm: " + e.getMessage());
        }

        return (guests);
    }

    // *******************************************************************************
    //  Check membership restrictions - max rounds per week, month or year
    // *******************************************************************************
    //
    private boolean checkMemship(Connection con, PrintWriter out, parmSlotm parm, String day) {


        boolean check = false;
        boolean go = false;
        parm.error = false;               // init

        String mship = "";
        String player = "";
        String period = "";

        //
        //  Allocate a new parm block for each tee time and call common method to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
        parm1.oldPlayer1 = "";       // always empty from here
        parm1.oldPlayer2 = "";
        parm1.oldPlayer3 = "";
        parm1.oldPlayer4 = "";
        parm1.oldPlayer5 = "";
        parm1.fb = parm.fb;
        parm1.ind = parm.ind;      // index value
        parm1.sfb = parm.sfb;
        parm1.club = parm.club;    // name of club


        //
        //  Do all members, one group at a time
        //
        try {

            go = false;                             // init to 'No Go'

            if (parm.p5.equals("Yes")) {

                if (!parm.mship1.equals("") || !parm.mship2.equals("") || !parm.mship3.equals("")
                        || !parm.mship4.equals("") || !parm.mship5.equals("")) {

                    go = true;                // go process this group

                    //
                    //  set parms for first group
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
                }

            } else {                       // 4-somes only

                if (!parm.mship1.equals("") || !parm.mship2.equals("") || !parm.mship3.equals("")
                        || !parm.mship4.equals("")) {

                    go = true;                // go process this group

                    //
                    //  set parms for first group
                    //
                    parm1.time = parm.time1;
                    parm1.player1 = parm.player1;
                    parm1.player2 = parm.player2;
                    parm1.player3 = parm.player3;
                    parm1.player4 = parm.player4;
                    parm1.user1 = parm.user1;
                    parm1.user2 = parm.user2;
                    parm1.user3 = parm.user3;
                    parm1.user4 = parm.user4;
                    parm1.mship1 = parm.mship1;
                    parm1.mship2 = parm.mship2;
                    parm1.mship3 = parm.mship3;
                    parm1.mship4 = parm.mship4;
                    parm1.mtype1 = parm.mtype1;
                    parm1.mtype2 = parm.mtype2;
                    parm1.mtype3 = parm.mtype3;
                    parm1.mtype4 = parm.mtype4;
                    parm1.mstype1 = parm.mstype1;
                    parm1.mstype2 = parm.mstype2;
                    parm1.mstype3 = parm.mstype3;
                    parm1.mstype4 = parm.mstype4;
                    parm1.mNum1 = parm.mNum1;
                    parm1.mNum2 = parm.mNum2;
                    parm1.mNum3 = parm.mNum3;
                    parm1.mNum4 = parm.mNum4;
                    parm1.player5 = "";
                    parm1.user5 = "";
                    parm1.mship5 = "";
                    parm1.mtype5 = "";
                    parm1.mstype5 = "";
                    parm1.mNum5 = "";
                }
            }

            if (go == true) {          // if mships found

                check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

                if (check == true) {          // if we hit on a restriction

                    player = parm1.player;
                    period = parm1.period;
                    mship = parm1.mship;
                }
            }

            if (check == false) {           // if we can keep going

                //
                //  Do 2nd group
                //
                go = false;                             // init to 'No Go'

                if (parm.p5.equals("Yes")) {

                    if (!parm.mship6.equals("") || !parm.mship7.equals("") || !parm.mship8.equals("")
                            || !parm.mship9.equals("") || !parm.mship10.equals("")) {

                        go = true;                // go process this group

                        //
                        //  set parms for this group
                        //
                        parm1.time = parm.time2;
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
                    }

                } else {                       // 4-somes only

                    if (!parm.mship5.equals("") || !parm.mship6.equals("") || !parm.mship7.equals("")
                            || !parm.mship8.equals("")) {

                        go = true;                // go process this group

                        //
                        //  set parms for this group
                        //
                        parm1.time = parm.time2;
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
                }

                if (go == true) {          // if mships found

                    check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

                    if (check == true) {          // if we hit on a restriction

                        player = parm1.player;
                        period = parm1.period;
                        mship = parm1.mship;
                    }
                }

                if (check == false) {           // if we can keep going

                    //
                    //  Do 3rd group
                    //
                    go = false;                             // init to 'No Go'

                    if (parm.p5.equals("Yes")) {

                        if (!parm.mship11.equals("") || !parm.mship12.equals("") || !parm.mship13.equals("")
                                || !parm.mship14.equals("") || !parm.mship15.equals("")) {

                            go = true;                // go process this group

                            //
                            //  set parms for this group
                            //
                            parm1.time = parm.time3;
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
                        }

                    } else {                       // 4-somes only

                        if (!parm.mship9.equals("") || !parm.mship10.equals("") || !parm.mship11.equals("")
                                || !parm.mship12.equals("")) {

                            go = true;                // go process this group

                            //
                            //  set parms for this group
                            //
                            parm1.time = parm.time3;
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
                    }

                    if (go == true) {          // if mships found

                        check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

                        if (check == true) {          // if we hit on a restriction

                            player = parm1.player;
                            period = parm1.period;
                            mship = parm1.mship;
                        }
                    }

                    if (check == false) {           // if we can keep going

                        //
                        //  Do 4th group
                        //
                        go = false;                             // init to 'No Go'

                        if (parm.p5.equals("Yes")) {

                            if (!parm.mship16.equals("") || !parm.mship17.equals("") || !parm.mship18.equals("")
                                    || !parm.mship19.equals("") || !parm.mship20.equals("")) {

                                go = true;                // go process this group

                                //
                                //  set parms for this group
                                //
                                parm1.time = parm.time4;
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
                            }

                        } else {                       // 4-somes only

                            if (!parm.mship13.equals("") || !parm.mship14.equals("") || !parm.mship15.equals("")
                                    || !parm.mship16.equals("")) {

                                go = true;                // go process this group

                                //
                                //  set parms for this group
                                //
                                parm1.time = parm.time4;
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
                        }

                        if (go == true) {          // if mships found

                            check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

                            if (check == true) {          // if we hit on a restriction

                                player = parm1.player;
                                period = parm1.period;
                                mship = parm1.mship;
                            }
                        }

                        if (check == false) {           // if we can keep going

                            //
                            //  Do 5th group
                            //
                            go = false;                             // init to 'No Go'

                            if (parm.p5.equals("Yes")) {

                                if (!parm.mship21.equals("") || !parm.mship22.equals("") || !parm.mship23.equals("")
                                        || !parm.mship24.equals("") || !parm.mship25.equals("")) {

                                    go = true;                // go process this group

                                    //
                                    //  set parms for this group
                                    //
                                    parm1.time = parm.time5;
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
                                }

                            } else {                       // 4-somes only

                                if (!parm.mship17.equals("") || !parm.mship18.equals("") || !parm.mship19.equals("")
                                        || !parm.mship20.equals("")) {

                                    go = true;                // go process this group

                                    //
                                    //  set parms for this group
                                    //
                                    parm1.time = parm.time5;
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
                            }

                            if (go == true) {          // if mships found

                                check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

                                if (check == true) {          // if we hit on a restriction

                                    player = parm1.player;
                                    period = parm1.period;
                                    mship = parm1.mship;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e7) {

            dbError(out, e7);
            parm.error = true;               // inform caller of error
        }

        //
        //  save parms if error
        //
        parm.player = player;
        parm.mship = mship;
        parm.period = period;

        return (check);

    }         // end of checkMemShip

    // *******************************************************************************
    //  Check member restrictions
    //
    //     First, find all restrictions within date & time constraints on this course.
    //     Then, find the ones for this day.
    //     Then, find any for this member type or membership type (all 25 possible players).
    //
    // *******************************************************************************
    //
    private boolean checkMemRes(Connection con, PrintWriter out, parmSlotm parm, String day) {


        boolean check = false;
        parm.error = false;               // init

        String player = "";
        String rest_name = "";

        //
        //  Allocate a new parm block for each tee time and call common meehtod to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
        parm1.oldPlayer1 = "";       // always empty from here
        parm1.oldPlayer2 = "";
        parm1.oldPlayer3 = "";
        parm1.oldPlayer4 = "";
        parm1.oldPlayer5 = "";
        parm1.fb = parm.fb;
        parm1.ind = parm.ind;      // index value
        parm1.sfb = parm.sfb;
        parm1.club = parm.club;    // name of club


        //
        //  Do each tee time - one at a time
        //
        try {


            // 
            //  set parms for first tee time
            //
            parm1.time = parm.time1;
            parm1.player1 = parm.player1;
            parm1.player2 = parm.player2;
            parm1.player3 = parm.player3;
            parm1.player4 = parm.player4;
            parm1.user1 = parm.user1;
            parm1.user2 = parm.user2;
            parm1.user3 = parm.user3;
            parm1.user4 = parm.user4;
            parm1.mship1 = parm.mship1;
            parm1.mship2 = parm.mship2;
            parm1.mship3 = parm.mship3;
            parm1.mship4 = parm.mship4;
            parm1.mtype1 = parm.mtype1;
            parm1.mtype2 = parm.mtype2;
            parm1.mtype3 = parm.mtype3;
            parm1.mtype4 = parm.mtype4;
            parm1.mNum1 = parm.mNum1;
            parm1.mNum2 = parm.mNum2;
            parm1.mNum3 = parm.mNum3;
            parm1.mNum4 = parm.mNum4;

            if (parm.p5.equals("Yes")) {

                parm1.player5 = parm.player5;
                parm1.user5 = parm.user5;
                parm1.mship5 = parm.mship5;
                parm1.mtype5 = parm.mtype5;
                parm1.mNum5 = parm.mNum5;

            } else {

                parm1.player5 = "";
                parm1.user5 = "";
                parm1.mship5 = "";
                parm1.mtype5 = "";
                parm1.mNum5 = "";
            }

            check = verifySlot.checkMemRests(parm1, con);                   // check for restrictions !!!!!

            if (check == true) {          // if we hit on a restriction

                player = parm1.player;
                rest_name = parm1.rest_name;
            }

            if (check == false && parm.time2 > 0) {        // if we can keep going    

                //
                //  do next tee time
                //
                parm1.time = parm.time2;

                if (parm.p5.equals("Yes")) {

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
                    parm1.mNum1 = parm.mNum5;
                    parm1.mNum2 = parm.mNum6;
                    parm1.mNum3 = parm.mNum7;
                    parm1.mNum4 = parm.mNum8;

                    parm1.player5 = "";
                    parm1.user5 = "";
                    parm1.mship5 = "";
                    parm1.mtype5 = "";
                    parm1.mNum5 = "";
                }

                check = verifySlot.checkMemRests(parm1, con);                      // check for restrictions !!!!!

                if (check == true) {          // if we hit on a restriction

                    player = parm1.player;
                    rest_name = parm1.rest_name;
                }

                if (check == false && parm.time3 > 0) {        // if we can keep going

                    //
                    //  do next tee time
                    //
                    parm1.time = parm.time3;

                    if (parm.p5.equals("Yes")) {

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
                        parm1.mNum1 = parm.mNum9;
                        parm1.mNum2 = parm.mNum10;
                        parm1.mNum3 = parm.mNum11;
                        parm1.mNum4 = parm.mNum12;

                        parm1.player5 = "";
                        parm1.user5 = "";
                        parm1.mship5 = "";
                        parm1.mtype5 = "";
                        parm1.mNum5 = "";
                    }

                    check = verifySlot.checkMemRests(parm1, con);                      // check for restrictions !!!!!

                    if (check == true) {          // if we hit on a restriction

                        player = parm1.player;
                        rest_name = parm1.rest_name;
                    }

                    if (check == false && parm.time4 > 0) {        // if we can keep going

                        //
                        //  do next tee time
                        //
                        parm1.time = parm.time4;

                        if (parm.p5.equals("Yes")) {

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
                            parm1.mNum1 = parm.mNum13;
                            parm1.mNum2 = parm.mNum14;
                            parm1.mNum3 = parm.mNum15;
                            parm1.mNum4 = parm.mNum16;

                            parm1.player5 = "";
                            parm1.user5 = "";
                            parm1.mship5 = "";
                            parm1.mtype5 = "";
                            parm1.mNum5 = "";
                        }

                        check = verifySlot.checkMemRests(parm1, con);         // check for restrictions !!!!!

                        if (check == true) {          // if we hit on a restriction

                            player = parm1.player;
                            rest_name = parm1.rest_name;
                        }

                        if (check == false && parm.time5 > 0) {        // if we can keep going

                            //
                            //  do next tee time
                            //
                            parm1.time = parm.time5;

                            if (parm.p5.equals("Yes")) {

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
                                parm1.mNum1 = parm.mNum17;
                                parm1.mNum2 = parm.mNum18;
                                parm1.mNum3 = parm.mNum19;
                                parm1.mNum4 = parm.mNum20;

                                parm1.player5 = "";
                                parm1.user5 = "";
                                parm1.mship5 = "";
                                parm1.mtype5 = "";
                                parm1.mNum5 = "";
                            }

                            check = verifySlot.checkMemRests(parm1, con);         // check for restrictions !!!!!

                            if (check == true) {          // if we hit on a restriction

                                player = parm1.player;
                                rest_name = parm1.rest_name;
                            }
                        }
                    }
                }
            }

        } catch (Exception e7) {

            dbError(out, e7);
            parm.error = true;               // inform caller of error
        }

        //
        //  save parms if error
        //
        parm.player = player;
        parm.rest_name = rest_name;

        return (check);

    }         // end of checkMemRes

    // *******************************************************************************
    //
    //  North Shore CC - Check for 'guest-only' times (must be at least 2 guests per group)
    //
    // *******************************************************************************
    //
    private boolean checkNSGuests(Connection con, PrintWriter out, parmSlotm parm, String day) {


        boolean check = false;
        parm.error = false;               // init

        String player = "";
        String rest_name = "";

        //
        //  Allocate a new parm block for each tee time and call common meehtod to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
        parm1.oldPlayer1 = "";       // always empty from here
        parm1.oldPlayer2 = "";
        parm1.oldPlayer3 = "";
        parm1.oldPlayer4 = "";
        parm1.oldPlayer5 = "";
        parm1.fb = parm.fb;
        parm1.ind = parm.ind;      // index value
        parm1.sfb = parm.sfb;
        parm1.club = parm.club;    // name of club


        //
        //  Do each tee time - one at a time
        //
        try {

            //
            //  set parms for first tee time
            //
            parm1.time = parm.time1;
            parm1.player1 = parm.player1;
            parm1.player2 = parm.player2;
            parm1.player3 = parm.player3;
            parm1.player4 = parm.player4;
            parm1.user1 = parm.user1;
            parm1.user2 = parm.user2;
            parm1.user3 = parm.user3;
            parm1.user4 = parm.user4;
            parm1.g1 = parm.g[0];
            parm1.g2 = parm.g[1];
            parm1.g3 = parm.g[2];
            parm1.g4 = parm.g[3];

            if (parm.p5.equals("Yes")) {

                parm1.player5 = parm.player5;
                parm1.user5 = parm.user5;
                parm1.g5 = parm.g[4];

            } else {

                parm1.player5 = "";
                parm1.user5 = "";
                parm1.g5 = "";
            }

            check = verifySlot.checkNSGuestTimes(parm1, con);                   // check for restrictions !!!!!


            if (check == false && parm.time2 > 0) {        // if we can keep going

                //
                //  do next tee time
                //
                parm1.time = parm.time2;

                if (parm.p5.equals("Yes")) {

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
                    parm1.g1 = parm.g[5];
                    parm1.g2 = parm.g[6];
                    parm1.g3 = parm.g[7];
                    parm1.g4 = parm.g[8];
                    parm1.g5 = parm.g[9];

                } else {

                    parm1.player1 = parm.player5;
                    parm1.player2 = parm.player6;
                    parm1.player3 = parm.player7;
                    parm1.player4 = parm.player8;
                    parm1.user1 = parm.user5;
                    parm1.user2 = parm.user6;
                    parm1.user3 = parm.user7;
                    parm1.user4 = parm.user8;
                    parm1.g1 = parm.g[4];
                    parm1.g2 = parm.g[5];
                    parm1.g3 = parm.g[6];
                    parm1.g4 = parm.g[7];

                    parm1.player5 = "";
                    parm1.user5 = "";
                    parm1.g5 = "";
                }

                check = verifySlot.checkNSGuestTimes(parm1, con);                   // check for restrictions !!!!!


                if (check == false && parm.time3 > 0) {        // if we can keep going

                    //
                    //  do next tee time
                    //
                    parm1.time = parm.time3;

                    if (parm.p5.equals("Yes")) {

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
                        parm1.g1 = parm.g[10];
                        parm1.g2 = parm.g[11];
                        parm1.g3 = parm.g[12];
                        parm1.g4 = parm.g[13];
                        parm1.g5 = parm.g[14];

                    } else {

                        parm1.player1 = parm.player9;
                        parm1.player2 = parm.player10;
                        parm1.player3 = parm.player11;
                        parm1.player4 = parm.player12;
                        parm1.user1 = parm.user9;
                        parm1.user2 = parm.user10;
                        parm1.user3 = parm.user11;
                        parm1.user4 = parm.user12;
                        parm1.g1 = parm.g[8];
                        parm1.g2 = parm.g[9];
                        parm1.g3 = parm.g[10];
                        parm1.g4 = parm.g[11];

                        parm1.player5 = "";
                        parm1.user5 = "";
                        parm1.g5 = "";
                    }

                    check = verifySlot.checkNSGuestTimes(parm1, con);                   // check for restrictions !!!!!


                    if (check == false && parm.time4 > 0) {        // if we can keep going

                        //
                        //  do next tee time
                        //
                        parm1.time = parm.time4;

                        if (parm.p5.equals("Yes")) {

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
                            parm1.g1 = parm.g[15];
                            parm1.g2 = parm.g[16];
                            parm1.g3 = parm.g[17];
                            parm1.g4 = parm.g[18];
                            parm1.g5 = parm.g[19];

                        } else {

                            parm1.player1 = parm.player13;
                            parm1.player2 = parm.player14;
                            parm1.player3 = parm.player15;
                            parm1.player4 = parm.player16;
                            parm1.user1 = parm.user13;
                            parm1.user2 = parm.user14;
                            parm1.user3 = parm.user15;
                            parm1.user4 = parm.user16;
                            parm1.g1 = parm.g[12];
                            parm1.g2 = parm.g[13];
                            parm1.g3 = parm.g[14];
                            parm1.g4 = parm.g[15];

                            parm1.player5 = "";
                            parm1.user5 = "";
                            parm1.g5 = "";
                        }

                        check = verifySlot.checkNSGuestTimes(parm1, con);                   // check for restrictions !!!!!


                        if (check == false && parm.time5 > 0) {        // if we can keep going

                            //
                            //  do next tee time
                            //
                            parm1.time = parm.time5;

                            if (parm.p5.equals("Yes")) {

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
                                parm1.g1 = parm.g[20];
                                parm1.g2 = parm.g[21];
                                parm1.g3 = parm.g[22];
                                parm1.g4 = parm.g[23];
                                parm1.g5 = parm.g[24];

                            } else {

                                parm1.player1 = parm.player17;
                                parm1.player2 = parm.player18;
                                parm1.player3 = parm.player19;
                                parm1.player4 = parm.player20;
                                parm1.user1 = parm.user17;
                                parm1.user2 = parm.user18;
                                parm1.user3 = parm.user19;
                                parm1.user4 = parm.user20;
                                parm1.g1 = parm.g[16];
                                parm1.g2 = parm.g[17];
                                parm1.g3 = parm.g[18];
                                parm1.g4 = parm.g[19];

                                parm1.player5 = "";
                                parm1.user5 = "";
                                parm1.g5 = "";
                            }

                            check = verifySlot.checkNSGuestTimes(parm1, con);                   // check for restrictions !!!!!
                        }
                    }
                }
            }

        } catch (Exception e7) {

            dbError(out, e7);
            parm.error = true;               // inform caller of error
        }

        return (check);

    }         // end of checkNSGuests

    // *******************************************************************************
    //
    //  Wilmington CC - Check for max guests during time frame
    //
    // *******************************************************************************
    //
    private boolean checkWilGuests(Connection con, parmSlotm parm) {


        boolean check = false;
        parm.error = false;               // init

        //
        //  Allocate a new parm block and call common method to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

        //
        //  Setup the new single parm block
        //
        parm1.date = parm.date;
        parm1.mm = parm.mm;
        parm1.yy = parm.yy;
        parm1.dd = parm.dd;
        parm1.course = parm.course;
        parm1.day = parm.day;
        parm1.fb = parm.fb;
        parm1.ind = parm.ind;      // index value
        parm1.sfb = parm.sfb;
        parm1.club = parm.club;    // name of club
        parm1.time = parm.time1;
        parm1.player1 = parm.player1;
        parm1.player2 = parm.player2;
        parm1.player3 = parm.player3;
        parm1.player4 = parm.player4;
        parm1.user1 = parm.user1;
        parm1.user2 = parm.user2;
        parm1.user3 = parm.user3;
        parm1.user4 = parm.user4;
        parm1.player5 = parm.player5;
        parm1.user5 = parm.user5;
        parm1.g5 = parm.g[4];
        parm1.guests = parm.guests;          // # of guests in this multi request

        //
        //  Do both tee times together - Wilmington only allows 2 tee times for members
        //
        check = verifyCustom.checkWilmingtonGuests(parm1, con);             // check for max guests

        return (check);

    }         // end of checkWilGuests

    // *******************************************************************************
    //
    //  Minikahda CC - Check for max guest times during hour of day
    //
    // *******************************************************************************
    //
    private boolean checkMiniGuests(Connection con, parmSlotm parm) {


        boolean check = false;
        parm.error = false;               // init

        int memcount = 0;


        //
        //  Allocate a new parm block and call common method to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time


        //
        // check if 1st group is a Guest Time (1 member & 3 guests) - Minikahda only allows 2 consecutive tee times for members and NO 5-somes
        //
        if (!parm.player1.equals("") && !parm.player2.equals("")
                && !parm.player3.equals("") && !parm.player4.equals("")) {       // if 4 players

            memcount = 0;

            if (!parm.user1.equals("")) {         // 4 players - see how many are members

                memcount++;
            }
            if (!parm.user2.equals("")) {

                memcount++;
            }
            if (!parm.user3.equals("")) {

                memcount++;
            }
            if (!parm.user4.equals("")) {

                memcount++;
            }

            if (memcount == 1) {         // if 1 member & 3 guests

                //
                //  Setup the new single parm block
                //
                parm1.date = parm.date;
                parm1.mm = parm.mm;
                parm1.yy = parm.yy;
                parm1.dd = parm.dd;
                parm1.course = parm.course;
                parm1.day = parm.day;
                parm1.fb = parm.fb;
                parm1.ind = parm.ind;      // index value
                parm1.sfb = parm.sfb;
                parm1.club = parm.club;    // name of club
                parm1.time = parm.time1;
                parm1.player1 = parm.player1;
                parm1.player2 = parm.player2;
                parm1.player3 = parm.player3;
                parm1.player4 = parm.player4;
                parm1.user1 = parm.user1;
                parm1.user2 = parm.user2;
                parm1.user3 = parm.user3;
                parm1.user4 = parm.user4;

                check = verifyCustom.checkMiniGuestTimes(parm1, con);   // check for max guest times

            }
        }

        //
        // check if 2nd group is a Guest Time (1 member & 3 guests) - Minikahda only allows 2 consecutive tee times for members and NO 5-somes
        //
        if (check == false && !parm.player5.equals("") && !parm.player6.equals("")
                && !parm.player7.equals("") && !parm.player8.equals("")) {                   // if 4 players

            memcount = 0;

            if (!parm.user5.equals("")) {         // 4 players - see how many are members

                memcount++;
            }
            if (!parm.user6.equals("")) {

                memcount++;
            }
            if (!parm.user7.equals("")) {

                memcount++;
            }
            if (!parm.user8.equals("")) {

                memcount++;
            }

            if (memcount == 1) {         // if 1 member & 3 guests

                //
                //  Setup the new single parm block
                //
                parm1.date = parm.date;
                parm1.mm = parm.mm;
                parm1.yy = parm.yy;
                parm1.dd = parm.dd;
                parm1.course = parm.course;
                parm1.day = parm.day;
                parm1.fb = parm.fb;
                parm1.ind = parm.ind;      // index value
                parm1.sfb = parm.sfb;
                parm1.club = parm.club;    // name of club
                parm1.time = parm.time2;
                parm1.player1 = parm.player5;
                parm1.player2 = parm.player6;
                parm1.player3 = parm.player7;
                parm1.player4 = parm.player8;
                parm1.user1 = parm.user5;
                parm1.user2 = parm.user6;
                parm1.user3 = parm.user7;
                parm1.user4 = parm.user8;

                check = verifyCustom.checkMiniGuestTimes(parm1, con);   // check for max guest times

            }
        }

        return (check);

    }         // end of checkMiniGuests

    // *******************************************************************************
    //
    //  Minikahda CC - Check for 1 member & 3 guests in each tee time
    //
    // *******************************************************************************
    //
    private boolean checkMiniGuestsT(Connection con, parmSlotm parm) {


        boolean check = false;
        parm.error = false;               // init

        int memcount = 0;


        //
        // check if 1st group is a Guest Time (1 member & 3 guests) - Minikahda only allows 2 consecutive tee times for members and NO 5-somes
        //
        if (!parm.player1.equals("") && !parm.player2.equals("")
                && !parm.player3.equals("") && !parm.player4.equals("")
                && !parm.player1.equalsIgnoreCase("x") && !parm.player2.equalsIgnoreCase("x")
                && !parm.player3.equalsIgnoreCase("x") && !parm.player4.equalsIgnoreCase("x")) {       // if 4 players

            memcount = 0;

            if (!parm.user1.equals("")) {         // 4 players - see how many are members

                memcount++;
            }
            if (!parm.user2.equals("")) {

                memcount++;
            }
            if (!parm.user3.equals("")) {

                memcount++;
            }
            if (!parm.user4.equals("")) {

                memcount++;
            }

            if (memcount > 1) {

                check = true;          // more than one member - error
            }

        } else {       // not 4 players - error

            check = true;
        }

        //
        // check if 2nd group is a Guest Time (1 member & 3 guests) - Minikahda only allows 2 consecutive tee times for members and NO 5-somes
        //
        if (check == false && !parm.player5.equals("") && !parm.player6.equals("")
                && !parm.player7.equals("") && !parm.player8.equals("")
                && !parm.player5.equalsIgnoreCase("x") && !parm.player6.equalsIgnoreCase("x")
                && !parm.player7.equalsIgnoreCase("x") && !parm.player8.equalsIgnoreCase("x")) {       // if 4 players


            memcount = 0;

            if (!parm.user5.equals("")) {         // 4 players - see how many are members

                memcount++;
            }
            if (!parm.user6.equals("")) {

                memcount++;
            }
            if (!parm.user7.equals("")) {

                memcount++;
            }
            if (!parm.user8.equals("")) {

                memcount++;
            }

            if (memcount > 1) {         // if NOT 1 member & 3 guests

                check = true;
            }

        } else {       // not 4 players - error

            check = true;
        }

        return (check);

    }         // end of checkMiniGuestsT

    // *******************************************************************************
    //  Check Days In Advance limits for membership types
    // *******************************************************************************
    //
    private boolean checkDaysInAdv(parmSlotm parm, PrintWriter out, Connection con) {


        boolean check = false;
        boolean go = false;
        parm.error = false;               // init

        String player = "";

        //
        //  Allocate a new parm block for each tee time and call common method to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
        parm1.oldPlayer1 = "";       // always empty from here
        parm1.oldPlayer2 = "";
        parm1.oldPlayer3 = "";
        parm1.oldPlayer4 = "";
        parm1.oldPlayer5 = "";
        parm1.fb = parm.fb;
        parm1.ind = parm.ind;      // index value
        parm1.sfb = parm.sfb;
        parm1.club = parm.club;    // name of club


        //
        //  Do all members, one group at a time
        //
        try {

            go = false;                             // init to 'No Go'

            if (parm.p5.equals("Yes")) {

                if (!parm.mship1.equals("") || !parm.mship2.equals("") || !parm.mship3.equals("")
                        || !parm.mship4.equals("") || !parm.mship5.equals("")) {

                    go = true;                // go process this group

                    //
                    //  set parms for first group
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
                }

            } else {                       // 4-somes only

                if (!parm.mship1.equals("") || !parm.mship2.equals("") || !parm.mship3.equals("")
                        || !parm.mship4.equals("")) {

                    go = true;                // go process this group

                    //
                    //  set parms for first group
                    //
                    parm1.time = parm.time1;
                    parm1.player1 = parm.player1;
                    parm1.player2 = parm.player2;
                    parm1.player3 = parm.player3;
                    parm1.player4 = parm.player4;
                    parm1.user1 = parm.user1;
                    parm1.user2 = parm.user2;
                    parm1.user3 = parm.user3;
                    parm1.user4 = parm.user4;
                    parm1.mship1 = parm.mship1;
                    parm1.mship2 = parm.mship2;
                    parm1.mship3 = parm.mship3;
                    parm1.mship4 = parm.mship4;
                    parm1.mtype1 = parm.mtype1;
                    parm1.mtype2 = parm.mtype2;
                    parm1.mtype3 = parm.mtype3;
                    parm1.mtype4 = parm.mtype4;
                    parm1.mstype1 = parm.mstype1;
                    parm1.mstype2 = parm.mstype2;
                    parm1.mstype3 = parm.mstype3;
                    parm1.mstype4 = parm.mstype4;
                    parm1.mNum1 = parm.mNum1;
                    parm1.mNum2 = parm.mNum2;
                    parm1.mNum3 = parm.mNum3;
                    parm1.mNum4 = parm.mNum4;
                    parm1.player5 = "";
                    parm1.user5 = "";
                    parm1.mship5 = "";
                    parm1.mtype5 = "";
                    parm1.mstype5 = "";
                    parm1.mNum5 = "";
                }
            }

            if (go == true) {          // if mships found

                check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

                if (check == true) {          // if we hit on a restriction

                    player = parm1.player;

                } else {

                    //
                    //  Lakewood CC - if more than 1 day in advance, there must be at least one Primary Member in the
                    //                group.  That is, there cannot be spouses only.
                    //
                    if (parm1.club.equals("lakewood") && parm1.ind > 1) {

                        //
                        //  Check if at least one Spouse in group and no Primary Members
                        //
                        if (parm1.mship1.endsWith("Spouse") || parm1.mship2.endsWith("Spouse") || parm1.mship3.endsWith("Spouse")
                                || parm1.mship4.endsWith("Spouse") || parm1.mship5.endsWith("Spouse")) {

                            if ((!parm1.mship1.equals("") && !parm1.mship1.endsWith("Spouse"))
                                    || (!parm1.mship2.equals("") && !parm1.mship2.endsWith("Spouse"))
                                    || (!parm1.mship3.equals("") && !parm1.mship3.endsWith("Spouse"))
                                    || (!parm1.mship4.equals("") && !parm1.mship4.endsWith("Spouse"))
                                    || (!parm1.mship5.equals("") && !parm1.mship5.endsWith("Spouse"))) {

                                player = "";

                            } else {     // error

                                player = "Spouse Error";
                            }
                        }
                    }         // end of IF lakewood

                    //
                    //  If Catamount Ranch - check members for max advance requests
                    //
                    if (parm1.club.equals("catamount") && parm1.ind > 0) {

                        check = verifySlot.checkRockies(parm1, con);

                        if (check == true) {

                            player = "Founder Error/" + parm1.player;
                        }
                    }         // end of IF Catamount

                    //
                    //  If Sonnenalp - check for max advance requests
                    //
                    if (parm1.club.equals("sonnenalp") && parm1.ind > 0) {

                        check = verifySlot.checkRockies(parm1, con);

                        if (check == true) {

                            player = "Sonnenalp Advance Error/" + parm1.player;
                        }
                    }         // end of IF Sonnenalp
                }
            }

            if (check == false) {           // if we can keep going

                //
                //  Do 2nd group
                //
                go = false;                             // init to 'No Go'

                if (parm.p5.equals("Yes")) {

                    if (!parm.mship6.equals("") || !parm.mship7.equals("") || !parm.mship8.equals("")
                            || !parm.mship9.equals("") || !parm.mship10.equals("")) {

                        go = true;                // go process this group

                        //
                        //  set parms for this group
                        //
                        parm1.time = parm.time2;
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
                    }

                } else {                       // 4-somes only

                    if (!parm.mship5.equals("") || !parm.mship6.equals("") || !parm.mship7.equals("")
                            || !parm.mship8.equals("")) {

                        go = true;                // go process this group

                        //
                        //  set parms for this group
                        //
                        parm1.time = parm.time2;
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
                }

                if (go == true) {          // if mships found

                    check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

                    if (check == true) {          // if we hit on a restriction

                        player = parm1.player;

                    } else {

                        //
                        //  Lakewood CC - if more than 1 day in advance, there must be at least one Primary Member in the
                        //                group.  That is, there cannot be spouses only.
                        //
                        if (parm1.club.equals("lakewood") && parm1.ind > 1) {

                            //
                            //  Check if at least one Spouse in group and no Primary Members
                            //
                            if (parm1.mship1.endsWith("Spouse") || parm1.mship2.endsWith("Spouse") || parm1.mship3.endsWith("Spouse")
                                    || parm1.mship4.endsWith("Spouse") || parm1.mship5.endsWith("Spouse")) {

                                if ((!parm1.mship1.equals("") && !parm1.mship1.endsWith("Spouse"))
                                        || (!parm1.mship2.equals("") && !parm1.mship2.endsWith("Spouse"))
                                        || (!parm1.mship3.equals("") && !parm1.mship3.endsWith("Spouse"))
                                        || (!parm1.mship4.equals("") && !parm1.mship4.endsWith("Spouse"))
                                        || (!parm1.mship5.equals("") && !parm1.mship5.endsWith("Spouse"))) {

                                    player = "";

                                } else {     // error

                                    player = "Spouse Error";
                                }
                            }
                        }         // end of IF lakewood

                        //
                        //  If Catamount Ranch - check members for max advance requests
                        //
                        if (parm1.club.equals("catamount") && parm1.ind > 0) {

                            check = verifySlot.checkRockies(parm1, con);

                            if (check == true) {

                                player = "Founder Error/" + parm1.player;
                            }
                        }         // end of IF Catamount

                        //
                        //  If Sonnenalp - check for max advance requests
                        //
                        if (parm1.club.equals("sonnenalp") && parm1.ind > 0) {

                            check = verifySlot.checkRockies(parm1, con);

                            if (check == true) {

                                player = "Sonnenalp Advance Error/" + parm1.player;
                            }
                        }         // end of IF Sonnenalp
                    }
                }

                if (check == false) {           // if we can keep going

                    //
                    //  Do 3rd group
                    //
                    go = false;                             // init to 'No Go'

                    if (parm.p5.equals("Yes")) {

                        if (!parm.mship11.equals("") || !parm.mship12.equals("") || !parm.mship13.equals("")
                                || !parm.mship14.equals("") || !parm.mship15.equals("")) {

                            go = true;                // go process this group

                            //
                            //  set parms for this group
                            //
                            parm1.time = parm.time3;
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
                        }

                    } else {                       // 4-somes only

                        if (!parm.mship9.equals("") || !parm.mship10.equals("") || !parm.mship11.equals("")
                                || !parm.mship12.equals("")) {

                            go = true;                // go process this group

                            //
                            //  set parms for this group
                            //
                            parm1.time = parm.time3;
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
                    }

                    if (go == true) {          // if mships found

                        check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

                        if (check == true) {          // if we hit on a restriction

                            player = parm1.player;

                        } else {

                            //
                            //  If Catamount Ranch - check members for max advance requests
                            //
                            if (parm1.club.equals("catamount") && parm1.ind > 0) {

                                check = verifySlot.checkRockies(parm1, con);

                                if (check == true) {

                                    player = "Founder Error/" + parm1.player;
                                }
                            }         // end of IF Catamount

                            //
                            //  If Sonnenalp - check for max advance requests
                            //
                            if (parm1.club.equals("sonnenalp") && parm1.ind > 0) {

                                check = verifySlot.checkRockies(parm1, con);

                                if (check == true) {

                                    player = "Sonnenalp Advance Error/" + parm1.player;
                                }
                            }         // end of IF Sonnenalp
                        }
                    }

                    if (check == false) {           // if we can keep going

                        //
                        //  Do 4th group
                        //
                        go = false;                             // init to 'No Go'

                        if (parm.p5.equals("Yes")) {

                            if (!parm.mship16.equals("") || !parm.mship17.equals("") || !parm.mship18.equals("")
                                    || !parm.mship19.equals("") || !parm.mship20.equals("")) {

                                go = true;                // go process this group

                                //
                                //  set parms for this group
                                //
                                parm1.time = parm.time4;
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
                            }

                        } else {                       // 4-somes only

                            if (!parm.mship13.equals("") || !parm.mship14.equals("") || !parm.mship15.equals("")
                                    || !parm.mship16.equals("")) {

                                go = true;                // go process this group

                                //
                                //  set parms for this group
                                //
                                parm1.time = parm.time4;
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
                        }

                        if (go == true) {          // if mships found

                            check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

                            if (check == true) {          // if we hit on a restriction

                                player = parm1.player;

                            } else {

                                //
                                //  If Catamount Ranch - check members for max advance requests
                                //
                                if (parm1.club.equals("catamount") && parm1.ind > 0) {

                                    check = verifySlot.checkRockies(parm1, con);

                                    if (check == true) {

                                        player = "Founder Error/" + parm1.player;
                                    }
                                }         // end of IF Catamount

                                //
                                //  If Sonnenalp - check for max advance requests
                                //
                                if (parm1.club.equals("sonnenalp") && parm1.ind > 0) {

                                    check = verifySlot.checkRockies(parm1, con);

                                    if (check == true) {

                                        player = "Sonnenalp Advance Error/" + parm1.player;
                                    }
                                }         // end of IF Sonnenalp
                            }
                        }

                        if (check == false) {           // if we can keep going

                            //
                            //  Do 5th group
                            //
                            go = false;                             // init to 'No Go'

                            if (parm.p5.equals("Yes")) {

                                if (!parm.mship21.equals("") || !parm.mship22.equals("") || !parm.mship23.equals("")
                                        || !parm.mship24.equals("") || !parm.mship25.equals("")) {

                                    go = true;                // go process this group

                                    //
                                    //  set parms for this group
                                    //
                                    parm1.time = parm.time5;
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
                                }

                            } else {                       // 4-somes only

                                if (!parm.mship17.equals("") || !parm.mship18.equals("") || !parm.mship19.equals("")
                                        || !parm.mship20.equals("")) {

                                    go = true;                // go process this group

                                    //
                                    //  set parms for this group
                                    //
                                    parm1.time = parm.time5;
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
                            }

                            if (go == true) {          // if mships found

                                check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

                                if (check == true) {          // if we hit on a restriction

                                    player = parm1.player;

                                } else {

                                    //
                                    //  If Catamount Ranch - check members for max advance requests
                                    //
                                    if (parm1.club.equals("catamount") && parm1.ind > 0) {

                                        check = verifySlot.checkRockies(parm1, con);

                                        if (check == true) {

                                            player = "Founder Error/" + parm1.player;
                                        }
                                    }         // end of IF Catamount

                                    //
                                    //  If Sonnenalp - check for max advance requests
                                    //
                                    if (parm1.club.equals("sonnenalp") && parm1.ind > 0) {

                                        check = verifySlot.checkRockies(parm1, con);

                                        if (check == true) {

                                            player = "Sonnenalp Advance Error/" + parm1.player;
                                        }
                                    }         // end of IF Sonnenalp
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e7) {

            dbError(out, e7);
            parm.error = true;               // inform caller of error
        }

        //
        //  save parms if error
        //
        parm.player = player;

        return (check);

    }         // end of checkDaysInAdv

    // *******************************************************************************
    //  Beverly GC
    //
    //     Guest Quota
    //
    // *******************************************************************************
    //
    private boolean chkBeverlyGuests(parmSlotm parm, Connection con) {


        boolean error = false;               // init

        //
        //  Allocate a new parm block 
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

        //
        //  Setup the new single parm block
        //
        parm1.date = parm.date;
        parm1.time = parm.time1;
        parm1.day = parm.day;
        parm1.guests = parm.guests;


        error = verifyCustom.beverlyGuests(parm1, con);

        return (error);

    }         // end of chkBeverlyGuests

    // *******************************************************************************
    //  Check CUSTOMS
    //
    //     This will group the request into 4-somes or 5-somes and process each
    //     group individually for customs.
    //
    //  NOTE:  The individual customs are processed in verifyCustom!!!
    //         No special code needed here - refer to verifyCustom.checkCustoms1
    //
    // *******************************************************************************
    //
    private String checkCustoms1(HttpServletRequest req, parmSlotm parm, String user) {

        Connection con = Connect.getCon(req);

        parm.error = false;               // init

        String errorMsg = "";
        String rest_name = "";

        //
        //  Allocate a new parm block for each tee time and call common method to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
        parm1.oldPlayer1 = "";       // always empty from here
        parm1.oldPlayer2 = "";
        parm1.oldPlayer3 = "";
        parm1.oldPlayer4 = "";
        parm1.oldPlayer5 = "";
        parm1.fb = parm.fb;
        parm1.ind = parm.ind;      // index value
        parm1.sfb = parm.sfb;
        parm1.club = parm.club;    // name of club
        parm1.slots = parm.slots;  // number of consecutive slots requested
        parm1.user = user;         // username of this user


        //
        //  Do each tee time - one at a time
        //

        // 
        //  set parms for first tee time
        //
        parm1.time = parm.time1;
        parm1.player1 = parm.player1;
        parm1.player2 = parm.player2;
        parm1.player3 = parm.player3;
        parm1.player4 = parm.player4;
        parm1.user1 = parm.user1;
        parm1.user2 = parm.user2;
        parm1.user3 = parm.user3;
        parm1.user4 = parm.user4;
        parm1.mship1 = parm.mship1;
        parm1.mship2 = parm.mship2;
        parm1.mship3 = parm.mship3;
        parm1.mship4 = parm.mship4;
        parm1.mtype1 = parm.mtype1;
        parm1.mtype2 = parm.mtype2;
        parm1.mtype3 = parm.mtype3;
        parm1.mtype4 = parm.mtype4;
        parm1.mNum1 = parm.mNum1;
        parm1.mNum2 = parm.mNum2;
        parm1.mNum3 = parm.mNum3;
        parm1.mNum4 = parm.mNum4;
        parm1.userg1 = parm.userg[0];
        parm1.userg2 = parm.userg[1];
        parm1.userg3 = parm.userg[2];
        parm1.userg4 = parm.userg[3];
        parm1.members = parm.memg1;
        parm1.guests = parm.guestsg1;
        parm1.groups = 1; // to indicate this is the first group from the request

        if (parm.p5.equals("Yes")) {

            parm1.player5 = parm.player5;
            parm1.user5 = parm.user5;
            parm1.mship5 = parm.mship5;
            parm1.mtype5 = parm.mtype5;
            parm1.mNum5 = parm.mNum5;
            parm1.userg5 = parm.userg[4];

        } else {

            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
            parm1.userg5 = "";
        }


        //
        //  Go check for any customs
        //
        errorMsg = verifyCustom.checkCustoms1(parm1, req);     // go check for customs  

        // copy over the custom_int value for this group to the main parm block
        parm.custom_int1 = parm1.custom_int;

        if (errorMsg.equals("") && parm.time2 > 0) {        // if we can keep going    

            //
            //  do next tee time
            //
            parm1.time = parm.time2;
            parm1.members = parm.memg2;
            parm1.guests = parm.guestsg2;
            parm1.groups = 2;

            if (parm.p5.equals("Yes")) {

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
                parm1.mNum1 = parm.mNum6;
                parm1.mNum2 = parm.mNum7;
                parm1.mNum3 = parm.mNum8;
                parm1.mNum4 = parm.mNum9;
                parm1.mNum5 = parm.mNum10;
                parm1.userg1 = parm.userg[5];
                parm1.userg2 = parm.userg[6];
                parm1.userg3 = parm.userg[7];
                parm1.userg4 = parm.userg[8];
                parm1.userg5 = parm.userg[9];

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
                parm1.mNum1 = parm.mNum5;
                parm1.mNum2 = parm.mNum6;
                parm1.mNum3 = parm.mNum7;
                parm1.mNum4 = parm.mNum8;
                parm1.userg1 = parm.userg[4];
                parm1.userg2 = parm.userg[5];
                parm1.userg3 = parm.userg[6];
                parm1.userg4 = parm.userg[7];

                parm1.player5 = "";
                parm1.user5 = "";
                parm1.mship5 = "";
                parm1.mtype5 = "";
                parm1.mNum5 = "";
                parm1.userg5 = "";
            }

            //
            //  Go check for any customs
            //
            errorMsg = verifyCustom.checkCustoms1(parm1, req);     // go check for customs

            // copy over the custom_int value for this group to the main parm block
            parm.custom_int2 = parm1.custom_int;

            if (errorMsg.equals("") && parm.time3 > 0) {        // if we can keep going    

                //
                //  do next tee time
                //
                parm1.time = parm.time3;
                parm1.members = parm.memg3;
                parm1.guests = parm.guestsg3;
                parm1.groups = 3;

                if (parm.p5.equals("Yes")) {

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
                    parm1.mNum1 = parm.mNum11;
                    parm1.mNum2 = parm.mNum12;
                    parm1.mNum3 = parm.mNum13;
                    parm1.mNum4 = parm.mNum14;
                    parm1.mNum5 = parm.mNum15;
                    parm1.userg1 = parm.userg[10];
                    parm1.userg2 = parm.userg[11];
                    parm1.userg3 = parm.userg[12];
                    parm1.userg4 = parm.userg[13];
                    parm1.userg5 = parm.userg[14];

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
                    parm1.mNum1 = parm.mNum9;
                    parm1.mNum2 = parm.mNum10;
                    parm1.mNum3 = parm.mNum11;
                    parm1.mNum4 = parm.mNum12;
                    parm1.userg1 = parm.userg[8];
                    parm1.userg2 = parm.userg[9];
                    parm1.userg3 = parm.userg[10];
                    parm1.userg4 = parm.userg[11];

                    parm1.player5 = "";
                    parm1.user5 = "";
                    parm1.mship5 = "";
                    parm1.mtype5 = "";
                    parm1.mNum5 = "";
                    parm1.userg5 = "";
                }

                //
                //  Go check for any customs
                //
                errorMsg = verifyCustom.checkCustoms1(parm1, req);     // go check for customs

                // copy over the custom_int value for this group to the main parm block
                parm.custom_int3 = parm1.custom_int;

                if (errorMsg.equals("") && parm.time4 > 0) {        // if we can keep going    

                    //
                    //  do next tee time
                    //
                    parm1.time = parm.time4;
                    parm1.members = parm.memg4;
                    parm1.guests = parm.guestsg4;
                    parm1.groups = 4;

                    if (parm.p5.equals("Yes")) {

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
                        parm1.mNum1 = parm.mNum16;
                        parm1.mNum2 = parm.mNum17;
                        parm1.mNum3 = parm.mNum18;
                        parm1.mNum4 = parm.mNum19;
                        parm1.mNum5 = parm.mNum20;
                        parm1.userg1 = parm.userg[15];
                        parm1.userg2 = parm.userg[16];
                        parm1.userg3 = parm.userg[17];
                        parm1.userg4 = parm.userg[18];
                        parm1.userg5 = parm.userg[19];

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
                        parm1.mNum1 = parm.mNum13;
                        parm1.mNum2 = parm.mNum14;
                        parm1.mNum3 = parm.mNum15;
                        parm1.mNum4 = parm.mNum16;
                        parm1.userg1 = parm.userg[12];
                        parm1.userg2 = parm.userg[13];
                        parm1.userg3 = parm.userg[14];
                        parm1.userg4 = parm.userg[15];

                        parm1.player5 = "";
                        parm1.user5 = "";
                        parm1.mship5 = "";
                        parm1.mtype5 = "";
                        parm1.mNum5 = "";
                        parm1.userg5 = "";
                    }

                    //
                    //  Go check for any customs
                    //
                    errorMsg = verifyCustom.checkCustoms1(parm1, req);     // go check for customs

                    // copy over the custom_int value for this group to the main parm block
                    parm.custom_int4 = parm1.custom_int;

                    if (errorMsg.equals("") && parm.time5 > 0) {        // if we can keep going    

                        //
                        //  do next tee time
                        //
                        parm1.time = parm.time5;
                        parm1.members = parm.memg5;
                        parm1.guests = parm.guestsg5;
                        parm1.groups = 5;

                        if (parm.p5.equals("Yes")) {

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
                            parm1.mNum1 = parm.mNum21;
                            parm1.mNum2 = parm.mNum22;
                            parm1.mNum3 = parm.mNum23;
                            parm1.mNum4 = parm.mNum24;
                            parm1.mNum5 = parm.mNum25;
                            parm1.userg1 = parm.userg[20];
                            parm1.userg2 = parm.userg[21];
                            parm1.userg3 = parm.userg[22];
                            parm1.userg4 = parm.userg[23];
                            parm1.userg5 = parm.userg[24];

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
                            parm1.mNum1 = parm.mNum17;
                            parm1.mNum2 = parm.mNum18;
                            parm1.mNum3 = parm.mNum19;
                            parm1.mNum4 = parm.mNum20;
                            parm1.userg1 = parm.userg[16];
                            parm1.userg2 = parm.userg[17];
                            parm1.userg3 = parm.userg[18];
                            parm1.userg4 = parm.userg[19];

                            parm1.player5 = "";
                            parm1.user5 = "";
                            parm1.mship5 = "";
                            parm1.mtype5 = "";
                            parm1.mNum5 = "";
                            parm1.userg5 = "";
                        }

                        //
                        //  Go check for any customs
                        //
                        errorMsg = verifyCustom.checkCustoms1(parm1, req);     // go check for customs

                        // copy over the custom_int value for this group to the main parm block
                        parm.custom_int5 = parm1.custom_int;

                    }
                }
            }
        }

        return (errorMsg);

    }         // end of checkCustoms1

    // *******************************************************************************
    //  Check CUSTOMS - Guests (after Guests are assigned)
    //
    //     This will group the request into 4-somes or 5-somes and process each
    //     group individually for customs.
    //
    //  NOTE:  The individual customs are processed in verifyCustom!!!
    //         No special code needed here - refer to verifyCustom.checkCustomsGst
    //
    // *******************************************************************************
    //
    private String checkCustomsGst(Connection con, parmSlotm parm, String user) {


        parm.error = false;               // init

        String errorMsg = "";

        //
        //  Allocate a new parm block for each tee time and call common method to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
        parm1.oldPlayer1 = "";       // always empty from here
        parm1.oldPlayer2 = "";
        parm1.oldPlayer3 = "";
        parm1.oldPlayer4 = "";
        parm1.oldPlayer5 = "";
        parm1.fb = parm.fb;
        parm1.ind = parm.ind;      // index value
        parm1.sfb = parm.sfb;
        parm1.club = parm.club;    // name of club
        parm1.user = user;         // username of this user


        //
        //  Do each tee time - one at a time
        //

        // 
        //  set parms for first tee time
        //
        parm1.time = parm.time1;
        parm1.player1 = parm.player1;
        parm1.player2 = parm.player2;
        parm1.player3 = parm.player3;
        parm1.player4 = parm.player4;
        parm1.user1 = parm.user1;
        parm1.user2 = parm.user2;
        parm1.user3 = parm.user3;
        parm1.user4 = parm.user4;
        parm1.userg1 = parm.userg[0];
        parm1.userg2 = parm.userg[1];
        parm1.userg3 = parm.userg[2];
        parm1.userg4 = parm.userg[3];
        parm1.mship1 = parm.mship1;
        parm1.mship2 = parm.mship2;
        parm1.mship3 = parm.mship3;
        parm1.mship4 = parm.mship4;
        parm1.mtype1 = parm.mtype1;
        parm1.mtype2 = parm.mtype2;
        parm1.mtype3 = parm.mtype3;
        parm1.mtype4 = parm.mtype4;
        parm1.mNum1 = parm.mNum1;
        parm1.mNum2 = parm.mNum2;
        parm1.mNum3 = parm.mNum3;
        parm1.mNum4 = parm.mNum4;

        parm1.members = parm.memg1;
        parm1.guests = parm.guestsg1;

        if (parm.p5.equals("Yes")) {

            parm1.player5 = parm.player5;
            parm1.user5 = parm.user5;
            parm1.userg5 = parm.userg[4];
            parm1.mship5 = parm.mship5;
            parm1.mtype5 = parm.mtype5;
            parm1.mNum5 = parm.mNum5;

        } else {

            parm1.player5 = "";
            parm1.user5 = "";
            parm1.userg5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
        }


        //
        //  Go check for any customs
        //
        errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs      

        if (errorMsg.equals("") && parm.time2 > 0) {        // if we can keep going    

            //
            //  do next tee time
            //
            parm1.time = parm.time2;

            parm1.members = parm.memg2;
            parm1.guests = parm.guestsg2;

            if (parm.p5.equals("Yes")) {

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
                parm1.userg1 = parm.userg[5];
                parm1.userg2 = parm.userg[6];
                parm1.userg3 = parm.userg[7];
                parm1.userg4 = parm.userg[8];
                parm1.userg5 = parm.userg[9];
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
                parm1.userg1 = parm.userg[4];
                parm1.userg2 = parm.userg[5];
                parm1.userg3 = parm.userg[6];
                parm1.userg4 = parm.userg[7];
                parm1.mship1 = parm.mship5;
                parm1.mship2 = parm.mship6;
                parm1.mship3 = parm.mship7;
                parm1.mship4 = parm.mship8;
                parm1.mtype1 = parm.mtype5;
                parm1.mtype2 = parm.mtype6;
                parm1.mtype3 = parm.mtype7;
                parm1.mtype4 = parm.mtype8;
                parm1.mNum1 = parm.mNum5;
                parm1.mNum2 = parm.mNum6;
                parm1.mNum3 = parm.mNum7;
                parm1.mNum4 = parm.mNum8;

                parm1.player5 = "";
                parm1.user5 = "";
                parm1.userg5 = "";
                parm1.mship5 = "";
                parm1.mtype5 = "";
                parm1.mNum5 = "";
            }

            //
            //  Go check for any customs
            //
            errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs


            if (errorMsg.equals("") && parm.time3 > 0) {        // if we can keep going    

                //
                //  do next tee time
                //
                parm1.time = parm.time3;

                parm1.members = parm.memg3;
                parm1.guests = parm.guestsg3;

                if (parm.p5.equals("Yes")) {

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
                    parm1.userg1 = parm.userg[10];
                    parm1.userg2 = parm.userg[11];
                    parm1.userg3 = parm.userg[12];
                    parm1.userg4 = parm.userg[13];
                    parm1.userg5 = parm.userg[14];
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
                    parm1.userg1 = parm.userg[8];
                    parm1.userg2 = parm.userg[9];
                    parm1.userg3 = parm.userg[10];
                    parm1.userg4 = parm.userg[11];
                    parm1.mship1 = parm.mship9;
                    parm1.mship2 = parm.mship10;
                    parm1.mship3 = parm.mship11;
                    parm1.mship4 = parm.mship12;
                    parm1.mtype1 = parm.mtype9;
                    parm1.mtype2 = parm.mtype10;
                    parm1.mtype3 = parm.mtype11;
                    parm1.mtype4 = parm.mtype12;
                    parm1.mNum1 = parm.mNum9;
                    parm1.mNum2 = parm.mNum10;
                    parm1.mNum3 = parm.mNum11;
                    parm1.mNum4 = parm.mNum12;

                    parm1.player5 = "";
                    parm1.user5 = "";
                    parm1.userg5 = "";
                    parm1.mship5 = "";
                    parm1.mtype5 = "";
                    parm1.mNum5 = "";
                }

                //
                //  Go check for any customs
                //
                errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs

                if (errorMsg.equals("") && parm.time4 > 0) {        // if we can keep going    

                    //
                    //  do next tee time
                    //
                    parm1.time = parm.time4;

                    parm1.members = parm.memg4;
                    parm1.guests = parm.guestsg4;

                    if (parm.p5.equals("Yes")) {

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
                        parm1.userg1 = parm.userg[15];
                        parm1.userg2 = parm.userg[16];
                        parm1.userg3 = parm.userg[17];
                        parm1.userg4 = parm.userg[18];
                        parm1.userg5 = parm.userg[19];
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
                        parm1.userg1 = parm.userg[12];
                        parm1.userg2 = parm.userg[13];
                        parm1.userg3 = parm.userg[14];
                        parm1.userg4 = parm.userg[15];
                        parm1.mship1 = parm.mship13;
                        parm1.mship2 = parm.mship14;
                        parm1.mship3 = parm.mship15;
                        parm1.mship4 = parm.mship16;
                        parm1.mtype1 = parm.mtype13;
                        parm1.mtype2 = parm.mtype14;
                        parm1.mtype3 = parm.mtype15;
                        parm1.mtype4 = parm.mtype16;
                        parm1.mNum1 = parm.mNum13;
                        parm1.mNum2 = parm.mNum14;
                        parm1.mNum3 = parm.mNum15;
                        parm1.mNum4 = parm.mNum16;

                        parm1.player5 = "";
                        parm1.user5 = "";
                        parm1.userg5 = "";
                        parm1.mship5 = "";
                        parm1.mtype5 = "";
                        parm1.mNum5 = "";
                    }

                    //
                    //  Go check for any customs
                    //
                    errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs

                    if (errorMsg.equals("") && parm.time5 > 0) {        // if we can keep going    

                        //
                        //  do next tee time
                        //
                        parm1.time = parm.time5;

                        parm1.members = parm.memg5;
                        parm1.guests = parm.guestsg5;

                        if (parm.p5.equals("Yes")) {

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
                            parm1.userg1 = parm.userg[20];
                            parm1.userg2 = parm.userg[21];
                            parm1.userg3 = parm.userg[22];
                            parm1.userg4 = parm.userg[23];
                            parm1.userg5 = parm.userg[24];
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
                            parm1.userg1 = parm.userg[16];
                            parm1.userg2 = parm.userg[17];
                            parm1.userg3 = parm.userg[18];
                            parm1.userg4 = parm.userg[19];
                            parm1.mship1 = parm.mship17;
                            parm1.mship2 = parm.mship18;
                            parm1.mship3 = parm.mship19;
                            parm1.mship4 = parm.mship20;
                            parm1.mtype1 = parm.mtype17;
                            parm1.mtype2 = parm.mtype18;
                            parm1.mtype3 = parm.mtype19;
                            parm1.mtype4 = parm.mtype20;
                            parm1.mNum1 = parm.mNum17;
                            parm1.mNum2 = parm.mNum18;
                            parm1.mNum3 = parm.mNum19;
                            parm1.mNum4 = parm.mNum20;

                            parm1.player5 = "";
                            parm1.user5 = "";
                            parm1.userg5 = "";
                            parm1.mship5 = "";
                            parm1.mtype5 = "";
                            parm1.mNum5 = "";
                        }

                        //
                        //  Go check for any customs
                        //
                        errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs

                    }
                }
            }
        }

        return (errorMsg);

    }         // end of checkCustomsGst

    // *******************************************************************************
    //  Check For Tee Sheet Flags
    //
    //     Checks each member in the tee times for any configured flags to be shown on pro tee sheet.
    //
    // *******************************************************************************
    //
    private void checkTFlag(parmSlotm parm, Connection con) {


        parm.error = false;               // init

        //
        //  Allocate a new parm block for each tee time and call common method to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

        //
        //  Setup the new single parm block
        //
        parm1.date = parm.date;

        parm.tflag1 = "";       // init the flags
        parm.tflag2 = "";
        parm.tflag3 = "";
        parm.tflag4 = "";
        parm.tflag5 = "";
        parm.tflag6 = "";
        parm.tflag7 = "";
        parm.tflag8 = "";
        parm.tflag9 = "";
        parm.tflag10 = "";
        parm.tflag11 = "";
        parm.tflag12 = "";
        parm.tflag13 = "";
        parm.tflag14 = "";
        parm.tflag15 = "";
        parm.tflag16 = "";
        parm.tflag17 = "";
        parm.tflag18 = "";
        parm.tflag19 = "";
        parm.tflag20 = "";
        parm.tflag21 = "";
        parm.tflag22 = "";
        parm.tflag23 = "";
        parm.tflag24 = "";
        parm.tflag25 = "";


        if (parm.p5.equals("Yes")) {

            if (!parm.user1.equals("") || !parm.user2.equals("") || !parm.user3.equals("")
                    || !parm.user4.equals("") || !parm.user5.equals("")) {

                //
                //  set parms for this group - only need usernames
                //
                parm1.time = parm.time1;
                parm1.user1 = parm.user1;
                parm1.user2 = parm.user2;
                parm1.user3 = parm.user3;
                parm1.user4 = parm.user4;
                parm1.user5 = parm.user5;

                verifySlot.checkTFlag(parm1, con);         // check for tflags

                parm.tflag1 = parm1.tflag1;                // save the flags
                parm.tflag2 = parm1.tflag2;
                parm.tflag3 = parm1.tflag3;
                parm.tflag4 = parm1.tflag4;
                parm.tflag5 = parm1.tflag5;
            }

            if (!parm.user6.equals("") || !parm.user7.equals("") || !parm.user8.equals("")
                    || !parm.user9.equals("") || !parm.user10.equals("")) {

                //
                //  set parms for this group - only need usernames
                //
                parm1.time = parm.time2;
                parm1.user1 = parm.user6;
                parm1.user2 = parm.user7;
                parm1.user3 = parm.user8;
                parm1.user4 = parm.user9;
                parm1.user5 = parm.user10;

                verifySlot.checkTFlag(parm1, con);         // check for tflags

                parm.tflag6 = parm1.tflag1;                // save the flags
                parm.tflag7 = parm1.tflag2;
                parm.tflag8 = parm1.tflag3;
                parm.tflag9 = parm1.tflag4;
                parm.tflag10 = parm1.tflag5;
            }

            if (!parm.user11.equals("") || !parm.user12.equals("") || !parm.user13.equals("")
                    || !parm.user14.equals("") || !parm.user15.equals("")) {

                //
                //  set parms for this group - only need usernames
                //
                parm1.time = parm.time3;
                parm1.user1 = parm.user11;
                parm1.user2 = parm.user12;
                parm1.user3 = parm.user13;
                parm1.user4 = parm.user14;
                parm1.user5 = parm.user15;

                verifySlot.checkTFlag(parm1, con);         // check for tflags

                parm.tflag11 = parm1.tflag1;                // save the flags
                parm.tflag12 = parm1.tflag2;
                parm.tflag13 = parm1.tflag3;
                parm.tflag14 = parm1.tflag4;
                parm.tflag15 = parm1.tflag5;
            }

            if (!parm.user16.equals("") || !parm.user17.equals("") || !parm.user18.equals("")
                    || !parm.user19.equals("") || !parm.user20.equals("")) {

                //
                //  set parms for this group - only need usernames
                //
                parm1.time = parm.time4;
                parm1.user1 = parm.user16;
                parm1.user2 = parm.user17;
                parm1.user3 = parm.user18;
                parm1.user4 = parm.user19;
                parm1.user5 = parm.user20;

                verifySlot.checkTFlag(parm1, con);         // check for tflags

                parm.tflag16 = parm1.tflag1;                // save the flags
                parm.tflag17 = parm1.tflag2;
                parm.tflag18 = parm1.tflag3;
                parm.tflag19 = parm1.tflag4;
                parm.tflag20 = parm1.tflag5;
            }

            if (!parm.user21.equals("") || !parm.user22.equals("") || !parm.user23.equals("")
                    || !parm.user24.equals("") || !parm.user25.equals("")) {

                //
                //  set parms for this group - only need usernames
                //
                parm1.time = parm.time5;
                parm1.user1 = parm.user21;
                parm1.user2 = parm.user22;
                parm1.user3 = parm.user23;
                parm1.user4 = parm.user24;
                parm1.user5 = parm.user25;

                verifySlot.checkTFlag(parm1, con);         // check for tflags

                parm.tflag21 = parm1.tflag1;                // save the flags
                parm.tflag22 = parm1.tflag2;
                parm.tflag23 = parm1.tflag3;
                parm.tflag24 = parm1.tflag4;
                parm.tflag25 = parm1.tflag5;
            }

        } else {                       // 4-somes only

            if (!parm.user1.equals("") || !parm.user2.equals("") || !parm.user3.equals("")
                    || !parm.user4.equals("")) {

                //
                //  set parms for this group
                //
                parm1.time = parm.time1;
                parm1.user1 = parm.user1;
                parm1.user2 = parm.user2;
                parm1.user3 = parm.user3;
                parm1.user4 = parm.user4;
                parm1.user5 = "";

                verifySlot.checkTFlag(parm1, con);         // check for tflags    

                parm.tflag1 = parm1.tflag1;                // save the flags
                parm.tflag2 = parm1.tflag2;
                parm.tflag3 = parm1.tflag3;
                parm.tflag4 = parm1.tflag4;
            }

            if (!parm.user5.equals("") || !parm.user6.equals("") || !parm.user7.equals("")
                    || !parm.user8.equals("")) {

                //
                //  set parms for this group
                //
                parm1.time = parm.time2;
                parm1.user1 = parm.user5;
                parm1.user2 = parm.user6;
                parm1.user3 = parm.user7;
                parm1.user4 = parm.user8;
                parm1.user5 = "";

                verifySlot.checkTFlag(parm1, con);         // check for tflags    

                parm.tflag5 = parm1.tflag1;                // save the flags
                parm.tflag6 = parm1.tflag2;
                parm.tflag7 = parm1.tflag3;
                parm.tflag8 = parm1.tflag4;
            }

            if (!parm.user9.equals("") || !parm.user10.equals("") || !parm.user11.equals("")
                    || !parm.user12.equals("")) {

                //
                //  set parms for this group
                //
                parm1.time = parm.time3;
                parm1.user1 = parm.user9;
                parm1.user2 = parm.user10;
                parm1.user3 = parm.user11;
                parm1.user4 = parm.user12;
                parm1.user5 = "";

                verifySlot.checkTFlag(parm1, con);         // check for tflags    

                parm.tflag9 = parm1.tflag1;                // save the flags
                parm.tflag10 = parm1.tflag2;
                parm.tflag11 = parm1.tflag3;
                parm.tflag12 = parm1.tflag4;
            }

            if (!parm.user13.equals("") || !parm.user14.equals("") || !parm.user15.equals("")
                    || !parm.user16.equals("")) {

                //
                //  set parms for this group
                //
                parm1.time = parm.time4;
                parm1.user1 = parm.user13;
                parm1.user2 = parm.user14;
                parm1.user3 = parm.user15;
                parm1.user4 = parm.user16;
                parm1.user5 = "";

                verifySlot.checkTFlag(parm1, con);         // check for tflags    

                parm.tflag13 = parm1.tflag1;                // save the flags
                parm.tflag14 = parm1.tflag2;
                parm.tflag15 = parm1.tflag3;
                parm.tflag16 = parm1.tflag4;
            }

            if (!parm.user17.equals("") || !parm.user18.equals("") || !parm.user19.equals("")
                    || !parm.user20.equals("")) {

                //
                //  set parms for this group
                //
                parm1.time = parm.time5;
                parm1.user1 = parm.user17;
                parm1.user2 = parm.user18;
                parm1.user3 = parm.user19;
                parm1.user4 = parm.user20;
                parm1.user5 = "";

                verifySlot.checkTFlag(parm1, con);         // check for tflags    

                parm.tflag17 = parm1.tflag1;                // save the flags
                parm.tflag18 = parm1.tflag2;
                parm.tflag19 = parm1.tflag3;
                parm.tflag20 = parm1.tflag4;
            }
        }

    }         // end of checkTFlag

    // *******************************************************************************
    //  Check Member Number restrictions
    //
    //     First, find all restrictions within date & time constraints
    //     Then, find the ones for this day
    //     Then, check all players' member numbers against all others in the time period
    //
    // *******************************************************************************
    //
    private boolean checkMemNum(Connection con, PrintWriter out, parmSlotm parm, String day) {


        ResultSet rs = null;

        boolean check = false;
        parm.error = false;               // init

        String rest_name = "";
        String rest_recurr = "";
        String rest_course = "";
        String rest_fb = "";
        String sfb = "";
        String course = parm.course;

        int rest_stime = 0;
        int rest_etime = 0;
        int mems = 0;
        int ind = 0;
        int time = parm.time1;

        long date = parm.date;


        try {

            PreparedStatement pstmt7b = con.prepareStatement(
                    "SELECT name, stime, etime, recurr, courseName, fb, num_mems "
                    + "FROM mnumres2 WHERE sdate <= ? AND edate >= ? AND "
                    + "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");


            pstmt7b.clearParameters();          // clear the parms
            pstmt7b.setLong(1, date);
            pstmt7b.setLong(2, date);
            pstmt7b.setInt(3, time);
            pstmt7b.setInt(4, time);
            pstmt7b.setString(5, course);

            rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

            check = false;                    // init 'hit' flag
            ind = 0;                          // init matching member count

            if (parm.fb == 0) {                    // is Tee time for Front 9?

                sfb = "Front";
            }

            if (parm.fb == 1) {                    // is it Back 9?

                sfb = "Back";
            }

            loop3:
            while (rs.next()) {              // check all matching restrictions for this day & F/B

                rest_name = rs.getString("name");
                rest_stime = rs.getInt("stime");
                rest_etime = rs.getInt("etime");
                rest_recurr = rs.getString("recurr");
                rest_course = rs.getString("courseName");
                rest_fb = rs.getString("fb");
                mems = rs.getInt("num_mems");

                //
                //  We must check the recurrence for this day (Monday, etc.)
                //
                if ((rest_recurr.equals("Every " + day)) || // if this day
                        (rest_recurr.equalsIgnoreCase("every day")) || // or everyday
                        ((rest_recurr.equalsIgnoreCase("all weekdays")) && // or all weekdays (and this is one)
                        (!day.equalsIgnoreCase("saturday"))
                        && (!day.equalsIgnoreCase("sunday")))
                        || ((rest_recurr.equalsIgnoreCase("all weekends")) && // or all weekends (and this is one)
                        (day.equalsIgnoreCase("saturday")))
                        || ((rest_recurr.equalsIgnoreCase("all weekends"))
                        && (day.equalsIgnoreCase("sunday")))) {

                    //
                    //  Now check if F/B matches this tee time
                    //
                    if (rest_fb.equals("Both") || rest_fb.equals(sfb)) {

                        //
                        //  Found a restriction that matches date, time, day, course & F/B - check each member player
                        //
                        //   Check Player 1
                        //
                        if (!parm.mNum1.equals("")) {           // if this player is a member and member number exists

                            ind = checkmNum(parm.mNum1, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum1 = parm.player1;  // save this player name for error msg

                            if (parm.mNum1.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum1.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }

                        }  // end of member 1 restrictions if

                        //
                        //   Check Player 2
                        //
                        if ((check == false) && (!parm.mNum2.equals(""))) {   // if this player is a member

                            ind = checkmNum(parm.mNum2, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum2 = parm.player2;  // save this player name for error msg

                            if (parm.mNum2.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum2.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }

                        }  // end of member 2 restrictions if

                        //
                        //   Check Player 3
                        //
                        if ((check == false) && (!parm.mNum3.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum3, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum3 = parm.player3;  // save this player name for error msg

                            if (parm.mNum3.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum3.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }

                        }  // end of member 3 restrictions if

                        //
                        //   Check Player 4
                        //
                        if ((check == false) && (!parm.mNum4.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum4, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum4 = parm.player4;  // save this player name for error msg

                            if (parm.mNum4.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum4.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 4 restrictions if

                        //
                        //   Check Player 5
                        //
                        if ((check == false) && (!parm.mNum5.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum5, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum5 = parm.player5;  // save this player name for error msg

                            if (parm.mNum5.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum5.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 5 restrictions if

                        //
                        //   Check Player 6
                        //
                        if ((check == false) && (!parm.mNum6.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum6, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum6 = parm.player6;  // save this player name for error msg

                            if (parm.mNum6.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum6.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 6 restrictions if

                        //
                        //   Check player 7
                        //
                        if ((check == false) && (!parm.mNum7.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum7, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum7 = parm.player7;  // save this player name for error msg

                            if (parm.mNum7.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum7.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 7 restrictions if

                        //
                        //   Check Player 8
                        //
                        if ((check == false) && (!parm.mNum8.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum8, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum8 = parm.player8;  // save this player name for error msg

                            if (parm.mNum8.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum8.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 8 restrictions if

                        //
                        //   Check Player 9
                        //
                        if ((check == false) && (!parm.mNum9.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum9, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum9 = parm.player9;  // save this player name for error msg

                            if (parm.mNum9.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum9.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 9 restrictions if

                        //
                        //   Check Player 10
                        //
                        if ((check == false) && (!parm.mNum10.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum10, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum10 = parm.player10;  // save this player name for error msg

                            if (parm.mNum10.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum10.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 10 restrictions if

                        //
                        //   Check Player 11
                        //
                        if ((check == false) && (!parm.mNum11.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum11, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum11 = parm.player11;  // save this player name for error msg

                            if (parm.mNum11.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum11.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 11 restrictions if

                        //
                        //   Check Player 12
                        //
                        if ((check == false) && (!parm.mNum12.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum12, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum12 = parm.player12;  // save this player name for error msg

                            if (parm.mNum12.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum12.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 12 restrictions if

                        //
                        //   Check Player 13
                        //
                        if ((check == false) && (!parm.mNum13.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum13, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum13 = parm.player13;  // save this player name for error msg

                            if (parm.mNum13.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum13.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 13 restrictions if

                        //
                        //   Check Player 14
                        //
                        if ((check == false) && (!parm.mNum14.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum14, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum14 = parm.player14;  // save this player name for error msg

                            if (parm.mNum14.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum14.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 14 restrictions if

                        //
                        //   Check Player 15
                        //
                        if ((check == false) && (!parm.mNum15.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum15, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum15 = parm.player15;  // save this player name for error msg

                            if (parm.mNum15.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum15.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 15 restrictions if

                        //
                        //   Check Player 16
                        //
                        if ((check == false) && (!parm.mNum16.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum16, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum16 = parm.player16;  // save this player name for error msg

                            if (parm.mNum16.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum16.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 16 restrictions if

                        //
                        //   Check player 17
                        //
                        if ((check == false) && (!parm.mNum17.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum17, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum17 = parm.player17;  // save this player name for error msg

                            if (parm.mNum17.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum17.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 17 restrictions if

                        //
                        //   Check Player 18
                        //
                        if ((check == false) && (!parm.mNum18.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum18, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum18 = parm.player18;  // save this player name for error msg

                            if (parm.mNum18.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum18.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 18 restrictions if

                        //
                        //   Check Player 19
                        //
                        if ((check == false) && (!parm.mNum19.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum19, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum19 = parm.player19;  // save this player name for error msg

                            if (parm.mNum19.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum19.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 19 restrictions if

                        //
                        //   Check Player 20
                        //
                        if ((check == false) && (!parm.mNum20.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum20, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum20 = parm.player20;  // save this player name for error msg

                            if (parm.mNum20.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum20.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 20 restrictions if

                        //
                        //   Check Player 21
                        //
                        if ((check == false) && (!parm.mNum21.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum21, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum21 = parm.player21;  // save this player name for error msg

                            if (parm.mNum21.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum21.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 21 restrictions if

                        //
                        //   Check Player 22
                        //
                        if ((check == false) && (!parm.mNum22.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum22, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum22 = parm.player22;  // save this player name for error msg

                            if (parm.mNum22.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum22.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 22 restrictions if

                        //
                        //   Check Player 23
                        //
                        if ((check == false) && (!parm.mNum23.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum23, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum23 = parm.player23;  // save this player name for error msg

                            if (parm.mNum23.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            if (parm.mNum23.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 23 restrictions if

                        //
                        //   Check Player 24
                        //
                        if ((check == false) && (!parm.mNum24.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum24, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum24 = parm.player24;  // save this player name for error msg

                            if (parm.mNum24.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;    // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum24.equals(parm.mNum25)) {

                                ind++;
                                parm.pNum25 = parm.player25;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }       // end of member 24 restrictions if

                        //
                        //   Check Player 25
                        //
                        if ((check == false) && (!parm.mNum25.equals(""))) {           // if this player is a member

                            ind = checkmNum(parm.mNum25, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                            //
                            //  Now check if any other members in this tee time match
                            //
                            parm.pNum25 = parm.player25;  // save this player name for error msg

                            if (parm.mNum25.equals(parm.mNum1)) {

                                ind++;
                                parm.pNum1 = parm.player1;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum2)) {

                                ind++;
                                parm.pNum2 = parm.player2;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum3)) {

                                ind++;
                                parm.pNum3 = parm.player3;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum4)) {

                                ind++;
                                parm.pNum4 = parm.player4;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum5)) {

                                ind++;
                                parm.pNum5 = parm.player5;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum6)) {

                                ind++;
                                parm.pNum6 = parm.player6;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum7)) {

                                ind++;
                                parm.pNum7 = parm.player7;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum8)) {

                                ind++;
                                parm.pNum8 = parm.player8;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum9)) {

                                ind++;
                                parm.pNum9 = parm.player9;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum10)) {

                                ind++;
                                parm.pNum10 = parm.player10;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum11)) {

                                ind++;
                                parm.pNum11 = parm.player11;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum12)) {

                                ind++;
                                parm.pNum12 = parm.player12;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum13)) {

                                ind++;
                                parm.pNum13 = parm.player13;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum14)) {

                                ind++;
                                parm.pNum14 = parm.player14;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum15)) {

                                ind++;
                                parm.pNum15 = parm.player15;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum16)) {

                                ind++;
                                parm.pNum16 = parm.player16;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum17)) {

                                ind++;
                                parm.pNum17 = parm.player17;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum18)) {

                                ind++;
                                parm.pNum18 = parm.player18;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum19)) {

                                ind++;
                                parm.pNum19 = parm.player19;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum20)) {

                                ind++;
                                parm.pNum20 = parm.player20;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum21)) {

                                ind++;
                                parm.pNum21 = parm.player21;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum22)) {

                                ind++;
                                parm.pNum22 = parm.player22;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum23)) {

                                ind++;
                                parm.pNum23 = parm.player23;  // match found for player - save for error msg
                            }
                            if (parm.mNum25.equals(parm.mNum24)) {

                                ind++;
                                parm.pNum24 = parm.player24;  // match found for player - save for error msg
                            }
                            //
                            //  Check if number of matches exceeds the max allowed
                            //
                            if (ind >= mems) {

                                check = true;      // max # exceeded - reject
                            }
                        }  // end of member 25 restrictions if

                        if (check == true) {          // if restriction hit

                            break loop3;
                        }
                    }     // end of IF F/B matches
                }     // end of 'day' if
            }       // end of while (no more restrictions)

            pstmt7b.close();

        } catch (Exception e7) {

            dbError(out, e7);
            parm.error = true;               // inform caller of error
        }

        //
        //  save parms if error
        //
        parm.rest_name = rest_name;

        return (check);
    }          // end of member restriction tests

    // *********************************************************
    //  Peninsula Club - check for unescorted juniors
    // *********************************************************
    private boolean chkPeninsulaJuniors(parmSlotm parm) {


        boolean error = false;

        //
        //  Check for any dependents
        //
        if (parm.mtype1.startsWith("Junior") || parm.mtype2.startsWith("Junior") || parm.mtype3.startsWith("Junior")
                || parm.mtype4.startsWith("Junior") || parm.mtype5.startsWith("Junior")
                || parm.mtype6.startsWith("Junior") || parm.mtype7.startsWith("Junior") || parm.mtype8.startsWith("Junior")
                || parm.mtype9.startsWith("Junior") || parm.mtype10.startsWith("Junior")
                || parm.mtype11.startsWith("Junior") || parm.mtype12.startsWith("Junior") || parm.mtype13.startsWith("Junior")
                || parm.mtype14.startsWith("Junior") || parm.mtype15.startsWith("Junior")
                || parm.mtype16.startsWith("Junior") || parm.mtype17.startsWith("Junior") || parm.mtype18.startsWith("Junior")
                || parm.mtype19.startsWith("Junior") || parm.mtype20.startsWith("Junior")
                || parm.mtype21.startsWith("Junior") || parm.mtype22.startsWith("Junior") || parm.mtype23.startsWith("Junior")
                || parm.mtype24.startsWith("Junior") || parm.mtype25.startsWith("Junior")) {

            //
            //  Make sure at least 1 adult
            //
            if (!parm.mtype1.startsWith("Primary") && !parm.mtype1.startsWith("Spouse")
                    && !parm.mtype2.startsWith("Primary") && !parm.mtype2.startsWith("Spouse")
                    && !parm.mtype3.startsWith("Primary") && !parm.mtype3.startsWith("Spouse")
                    && !parm.mtype4.startsWith("Primary") && !parm.mtype4.startsWith("Spouse")
                    && !parm.mtype5.startsWith("Primary") && !parm.mtype5.startsWith("Spouse")
                    && !parm.mtype6.startsWith("Primary") && !parm.mtype6.startsWith("Spouse")
                    && !parm.mtype7.startsWith("Primary") && !parm.mtype7.startsWith("Spouse")
                    && !parm.mtype8.startsWith("Primary") && !parm.mtype8.startsWith("Spouse")
                    && !parm.mtype9.startsWith("Primary") && !parm.mtype9.startsWith("Spouse")
                    && !parm.mtype10.startsWith("Primary") && !parm.mtype10.startsWith("Spouse")
                    && !parm.mtype11.startsWith("Primary") && !parm.mtype11.startsWith("Spouse")
                    && !parm.mtype12.startsWith("Primary") && !parm.mtype12.startsWith("Spouse")
                    && !parm.mtype13.startsWith("Primary") && !parm.mtype13.startsWith("Spouse")
                    && !parm.mtype14.startsWith("Primary") && !parm.mtype14.startsWith("Spouse")
                    && !parm.mtype15.startsWith("Primary") && !parm.mtype15.startsWith("Spouse")
                    && !parm.mtype16.startsWith("Primary") && !parm.mtype16.startsWith("Spouse")
                    && !parm.mtype17.startsWith("Primary") && !parm.mtype17.startsWith("Spouse")
                    && !parm.mtype18.startsWith("Primary") && !parm.mtype18.startsWith("Spouse")
                    && !parm.mtype19.startsWith("Primary") && !parm.mtype19.startsWith("Spouse")
                    && !parm.mtype20.startsWith("Primary") && !parm.mtype20.startsWith("Spouse")
                    && !parm.mtype21.startsWith("Primary") && !parm.mtype21.startsWith("Spouse")
                    && !parm.mtype22.startsWith("Primary") && !parm.mtype22.startsWith("Spouse")
                    && !parm.mtype23.startsWith("Primary") && !parm.mtype23.startsWith("Spouse")
                    && !parm.mtype24.startsWith("Primary") && !parm.mtype24.startsWith("Spouse")
                    && !parm.mtype25.startsWith("Primary") && !parm.mtype25.startsWith("Spouse")) {

                error = true;       // error if no adults  
            }
        }

        return (error);

    }          // end of Peninsula custom restriction

    // *********************************************************
    //  Awbrey Glen - check for unescorted juniors
    // *********************************************************
    private boolean chkAwbreyJuniors(parmSlotm parm) {


        boolean error = false;
        boolean check = false;

        //
        //  Check for any 'Junior 12 & over' (male or female)
        //
        if (parm.mtype1.endsWith("over") || parm.mtype2.endsWith("over") || parm.mtype3.endsWith("over")
                || parm.mtype4.endsWith("over") || parm.mtype5.endsWith("over")
                || parm.mtype6.endsWith("over") || parm.mtype7.endsWith("over") || parm.mtype8.endsWith("over")
                || parm.mtype9.endsWith("over") || parm.mtype10.endsWith("over")
                || parm.mtype11.endsWith("over") || parm.mtype12.endsWith("over") || parm.mtype13.endsWith("over")
                || parm.mtype14.endsWith("over") || parm.mtype15.endsWith("over")
                || parm.mtype16.endsWith("over") || parm.mtype17.endsWith("over") || parm.mtype18.endsWith("over")
                || parm.mtype19.endsWith("over") || parm.mtype20.endsWith("over")
                || parm.mtype21.endsWith("over") || parm.mtype22.endsWith("over") || parm.mtype23.endsWith("over")
                || parm.mtype24.endsWith("over") || parm.mtype25.endsWith("over")) {

            if (parm.time1 < 1200) {       // if before Noon

                check = true;              // found - check for adults 
            }

        } else {      // check for Juniors (male or female, under 12)

            if (parm.mtype1.startsWith("Junior") || parm.mtype2.startsWith("Junior") || parm.mtype3.startsWith("Junior")
                    || parm.mtype4.startsWith("Junior") || parm.mtype5.startsWith("Junior")
                    || parm.mtype6.startsWith("Junior") || parm.mtype7.startsWith("Junior") || parm.mtype8.startsWith("Junior")
                    || parm.mtype9.startsWith("Junior") || parm.mtype10.startsWith("Junior")
                    || parm.mtype11.startsWith("Junior") || parm.mtype12.startsWith("Junior") || parm.mtype13.startsWith("Junior")
                    || parm.mtype14.startsWith("Junior") || parm.mtype15.startsWith("Junior")
                    || parm.mtype16.startsWith("Junior") || parm.mtype17.startsWith("Junior") || parm.mtype18.startsWith("Junior")
                    || parm.mtype19.startsWith("Junior") || parm.mtype20.startsWith("Junior")
                    || parm.mtype21.startsWith("Junior") || parm.mtype22.startsWith("Junior") || parm.mtype23.startsWith("Junior")
                    || parm.mtype24.startsWith("Junior") || parm.mtype25.startsWith("Junior")) {

                check = true;      // found - check for adults (all day, every day)
            }
        }

        if (check == true) {        // check for an adult?

            //
            //  Make sure at least 1 adult
            //
            if (!parm.mtype1.startsWith("Adult")
                    && !parm.mtype2.startsWith("Adult")
                    && !parm.mtype3.startsWith("Adult")
                    && !parm.mtype4.startsWith("Adult")
                    && !parm.mtype5.startsWith("Adult")
                    && !parm.mtype6.startsWith("Adult")
                    && !parm.mtype7.startsWith("Adult")
                    && !parm.mtype8.startsWith("Adult")
                    && !parm.mtype9.startsWith("Adult")
                    && !parm.mtype10.startsWith("Adult")
                    && !parm.mtype11.startsWith("Adult")
                    && !parm.mtype12.startsWith("Adult")
                    && !parm.mtype13.startsWith("Adult")
                    && !parm.mtype14.startsWith("Adult")
                    && !parm.mtype15.startsWith("Adult")
                    && !parm.mtype16.startsWith("Adult")
                    && !parm.mtype17.startsWith("Adult")
                    && !parm.mtype18.startsWith("Adult")
                    && !parm.mtype19.startsWith("Adult")
                    && !parm.mtype20.startsWith("Adult")
                    && !parm.mtype21.startsWith("Adult")
                    && !parm.mtype22.startsWith("Adult")
                    && !parm.mtype23.startsWith("Adult")
                    && !parm.mtype24.startsWith("Adult")
                    && !parm.mtype25.startsWith("Adult")) {

                error = true;       // error if no adults
            }
        }

        return (error);

    }          // end of Awbrey Glen custom restriction

    // *********************************************************
    //  Send email to members in this request
    // *********************************************************
    private void sendMail(Connection con, parmSlotm parms, String user) {


        //
        //  parm block to hold verify's parms
        //
        parmEmail parme = new parmEmail();          // allocate an Email parm block

        //
        //  Get the parms passed in the parm block and put them in the Email Parm Block
        //
        parme.activity_id = 0;
        parme.club = parms.club;
        parme.guests = parms.guests;
        parme.date = parms.date;
        parme.time = parms.time1;        // first tee time
        parme.time2 = parms.time2;
        parme.time3 = parms.time3;
        parme.time4 = parms.time4;
        parme.time5 = parms.time5;
        parme.fb = parms.fb;
        parme.mm = parms.mm;
        parme.dd = parms.dd;
        parme.yy = parms.yy;

        parme.type = "tee";          // use tee time message
        parme.user = user;
        parme.emailNew = 1;          // always new tee times from here
        parme.emailMod = 0;
        parme.emailCan = 0;

        parme.course = parms.course;
        parme.day = parms.day;
        parme.notes = parms.notes;

        parme.p91 = parms.p91;
        parme.p92 = parms.p92;
        parme.p93 = parms.p93;
        parme.p94 = parms.p94;

        parme.player1 = parms.player1;
        parme.player2 = parms.player2;
        parme.player3 = parms.player3;
        parme.player4 = parms.player4;

        parme.user1 = parms.user1;
        parme.user2 = parms.user2;
        parme.user3 = parms.user3;
        parme.user4 = parms.user4;

        parme.pcw1 = parms.pcw1;
        parme.pcw2 = parms.pcw2;
        parme.pcw3 = parms.pcw3;
        parme.pcw4 = parms.pcw4;

        parme.guest_id1 = parms.guest_id1;
        parme.guest_id2 = parms.guest_id2;
        parme.guest_id3 = parms.guest_id3;
        parme.guest_id4 = parms.guest_id4;
        
        //
        //  Custom for Open Play Shotguns - simple mode
        //
        if (parms.club.equals("demov4") || parms.club.equals("mosscreek")) {              // check if Open Play Shotgun event during this time

            String shotgunEvent = checkShotgunEvent(parms.date, parms.time1, parms.fb, parms.course, con);
           
            if (!shotgunEvent.equals("")) {
                
                parme.etype = 1;     // set Shotgun Event so times are correct
                
                parme.time = Utilities.getEventTime(shotgunEvent, con);   // get the actual time of the shotgun
                parme.time2 = parme.time;
                parme.time3 = parme.time;
                parme.time4 = parme.time;      // ??????? will this work?
                parme.time5 = parme.time;
            }
        }                
        

        //
        //  Add remainder of players based on 4-somes or 5-somes
        //
        if (parms.p5.equals("Yes")) {     // if 5-somes

            parme.p95 = parms.p95;
            parme.p96 = parms.p96;
            parme.p97 = parms.p97;
            parme.p98 = parms.p98;
            parme.p99 = parms.p99;
            parme.p910 = parms.p910;
            parme.p911 = parms.p911;
            parme.p912 = parms.p912;
            parme.p913 = parms.p913;
            parme.p914 = parms.p914;
            parme.p915 = parms.p915;
            parme.p916 = parms.p916;
            parme.p917 = parms.p917;
            parme.p918 = parms.p918;
            parme.p919 = parms.p919;
            parme.p920 = parms.p920;
            parme.p921 = parms.p921;
            parme.p922 = parms.p922;
            parme.p923 = parms.p923;
            parme.p924 = parms.p924;
            parme.p925 = parms.p925;

            parme.player5 = parms.player5;
            parme.player6 = parms.player6;
            parme.player7 = parms.player7;
            parme.player8 = parms.player8;
            parme.player9 = parms.player9;
            parme.player10 = parms.player10;
            parme.player11 = parms.player11;
            parme.player12 = parms.player12;
            parme.player13 = parms.player13;
            parme.player14 = parms.player14;
            parme.player15 = parms.player15;
            parme.player16 = parms.player16;
            parme.player17 = parms.player17;
            parme.player18 = parms.player18;
            parme.player19 = parms.player19;
            parme.player20 = parms.player20;
            parme.player21 = parms.player21;
            parme.player22 = parms.player22;
            parme.player23 = parms.player23;
            parme.player24 = parms.player24;
            parme.player25 = parms.player25;

            parme.user5 = parms.user5;
            parme.user6 = parms.user6;
            parme.user7 = parms.user7;
            parme.user8 = parms.user8;
            parme.user9 = parms.user9;
            parme.user10 = parms.user10;
            parme.user11 = parms.user11;
            parme.user12 = parms.user12;
            parme.user13 = parms.user13;
            parme.user14 = parms.user14;
            parme.user15 = parms.user15;
            parme.user16 = parms.user16;
            parme.user17 = parms.user17;
            parme.user18 = parms.user18;
            parme.user19 = parms.user19;
            parme.user20 = parms.user20;
            parme.user21 = parms.user21;
            parme.user22 = parms.user22;
            parme.user23 = parms.user23;
            parme.user24 = parms.user24;
            parme.user25 = parms.user25;

            parme.pcw5 = parms.pcw5;
            parme.pcw6 = parms.pcw6;
            parme.pcw7 = parms.pcw7;
            parme.pcw8 = parms.pcw8;
            parme.pcw9 = parms.pcw9;
            parme.pcw10 = parms.pcw10;
            parme.pcw11 = parms.pcw11;
            parme.pcw12 = parms.pcw12;
            parme.pcw13 = parms.pcw13;
            parme.pcw14 = parms.pcw14;
            parme.pcw15 = parms.pcw15;
            parme.pcw16 = parms.pcw16;
            parme.pcw17 = parms.pcw17;
            parme.pcw18 = parms.pcw18;
            parme.pcw19 = parms.pcw19;
            parme.pcw20 = parms.pcw20;
            parme.pcw21 = parms.pcw21;
            parme.pcw22 = parms.pcw22;
            parme.pcw23 = parms.pcw23;
            parme.pcw24 = parms.pcw24;
            parme.pcw25 = parms.pcw25;

            parme.guest_id5 = parms.guest_id5;
            parme.guest_id6 = parms.guest_id6;
            parme.guest_id7 = parms.guest_id7;
            parme.guest_id8 = parms.guest_id8;
            parme.guest_id9 = parms.guest_id9;
            parme.guest_id10 = parms.guest_id10;
            parme.guest_id11 = parms.guest_id11;
            parme.guest_id12 = parms.guest_id12;
            parme.guest_id13 = parms.guest_id13;
            parme.guest_id14 = parms.guest_id14;
            parme.guest_id15 = parms.guest_id15;
            parme.guest_id16 = parms.guest_id16;
            parme.guest_id17 = parms.guest_id17;
            parme.guest_id18 = parms.guest_id18;
            parme.guest_id19 = parms.guest_id19;
            parme.guest_id20 = parms.guest_id20;
            parme.guest_id21 = parms.guest_id21;
            parme.guest_id22 = parms.guest_id22;
            parme.guest_id23 = parms.guest_id23;
            parme.guest_id24 = parms.guest_id24;
            parme.guest_id25 = parms.guest_id25;
            
            if (parme.time2 > 0 && parme.player6.equals("")) parme.time2 = 0;  // remove time value in case member requested more times than they actually used   
            if (parme.time3 > 0 && parme.player11.equals("")) parme.time3 = 0;     
            if (parme.time4 > 0 && parme.player16.equals("")) parme.time4 = 0;     
            if (parme.time5 > 0 && parme.player21.equals("")) parme.time5 = 0;     

        } else {                          // 4-somes      

            parme.p95 = 0;
            parme.p96 = parms.p95;
            parme.p97 = parms.p96;
            parme.p98 = parms.p97;
            parme.p99 = parms.p98;
            parme.p910 = 0;
            parme.p911 = parms.p99;
            parme.p912 = parms.p910;
            parme.p913 = parms.p911;
            parme.p914 = parms.p912;
            parme.p915 = 0;
            parme.p916 = parms.p913;
            parme.p917 = parms.p914;
            parme.p918 = parms.p915;
            parme.p919 = parms.p916;
            parme.p920 = 0;
            parme.p921 = parms.p917;
            parme.p922 = parms.p918;
            parme.p923 = parms.p919;
            parme.p924 = parms.p920;
            parme.p925 = 0;

            parme.player5 = "";
            parme.player6 = parms.player5;
            parme.player7 = parms.player6;
            parme.player8 = parms.player7;
            parme.player9 = parms.player8;
            parme.player10 = "";
            parme.player11 = parms.player9;
            parme.player12 = parms.player10;
            parme.player13 = parms.player11;
            parme.player14 = parms.player12;
            parme.player15 = "";
            parme.player16 = parms.player13;
            parme.player17 = parms.player14;
            parme.player18 = parms.player15;
            parme.player19 = parms.player16;
            parme.player20 = "";
            parme.player21 = parms.player17;
            parme.player22 = parms.player18;
            parme.player23 = parms.player19;
            parme.player24 = parms.player20;
            parme.player25 = "";

            parme.user5 = "";
            parme.user6 = parms.user5;
            parme.user7 = parms.user6;
            parme.user8 = parms.user7;
            parme.user9 = parms.user8;
            parme.user10 = "";
            parme.user11 = parms.user9;
            parme.user12 = parms.user10;
            parme.user13 = parms.user11;
            parme.user14 = parms.user12;
            parme.user15 = "";
            parme.user16 = parms.user13;
            parme.user17 = parms.user14;
            parme.user18 = parms.user15;
            parme.user19 = parms.user16;
            parme.user20 = "";
            parme.user21 = parms.user17;
            parme.user22 = parms.user18;
            parme.user23 = parms.user19;
            parme.user24 = parms.user20;
            parme.user25 = "";

            parme.pcw5 = "";
            parme.pcw6 = parms.pcw5;
            parme.pcw7 = parms.pcw6;
            parme.pcw8 = parms.pcw7;
            parme.pcw9 = parms.pcw8;
            parme.pcw10 = "";
            parme.pcw11 = parms.pcw9;
            parme.pcw12 = parms.pcw10;
            parme.pcw13 = parms.pcw11;
            parme.pcw14 = parms.pcw12;
            parme.pcw15 = "";
            parme.pcw16 = parms.pcw13;
            parme.pcw17 = parms.pcw14;
            parme.pcw18 = parms.pcw15;
            parme.pcw19 = parms.pcw16;
            parme.pcw20 = "";
            parme.pcw21 = parms.pcw17;
            parme.pcw22 = parms.pcw18;
            parme.pcw23 = parms.pcw19;
            parme.pcw24 = parms.pcw20;
            parme.pcw25 = "";

            parme.guest_id5 = 0;
            parme.guest_id6 = parms.guest_id5;
            parme.guest_id7 = parms.guest_id6;
            parme.guest_id8 = parms.guest_id7;
            parme.guest_id9 = parms.guest_id8;
            parme.guest_id10 = 0;
            parme.guest_id11 = parms.guest_id9;
            parme.guest_id12 = parms.guest_id10;
            parme.guest_id13 = parms.guest_id11;
            parme.guest_id14 = parms.guest_id12;
            parme.guest_id15 = 0;
            parme.guest_id16 = parms.guest_id13;
            parme.guest_id17 = parms.guest_id14;
            parme.guest_id18 = parms.guest_id15;
            parme.guest_id19 = parms.guest_id16;
            parme.guest_id20 = 0;
            parme.guest_id21 = parms.guest_id17;
            parme.guest_id22 = parms.guest_id18;
            parme.guest_id23 = parms.guest_id19;
            parme.guest_id24 = parms.guest_id20;
            parme.guest_id25 = 0;
            
            if (parme.time2 > 0 && parme.player6.equals("")) parme.time2 = 0;  // remove time value in case member requested more times than they actually used   
            if (parme.time3 > 0 && parme.player11.equals("")) parme.time3 = 0;     
            if (parme.time4 > 0 && parme.player16.equals("")) parme.time4 = 0;     
            if (parme.time5 > 0 && parme.player21.equals("")) parme.time5 = 0;     
        }

        parme.oldplayer1 = "";
        parme.oldplayer2 = "";
        parme.oldplayer3 = "";
        parme.oldplayer4 = "";
        parme.oldplayer5 = "";
        parme.oldplayer6 = "";
        parme.oldplayer7 = "";
        parme.oldplayer8 = "";
        parme.oldplayer9 = "";
        parme.oldplayer10 = "";
        parme.oldplayer11 = "";
        parme.oldplayer12 = "";
        parme.oldplayer13 = "";
        parme.oldplayer14 = "";
        parme.oldplayer15 = "";
        parme.oldplayer16 = "";
        parme.oldplayer17 = "";
        parme.oldplayer18 = "";
        parme.oldplayer19 = "";
        parme.oldplayer20 = "";
        parme.oldplayer21 = "";
        parme.oldplayer22 = "";
        parme.oldplayer23 = "";
        parme.oldplayer24 = "";
        parme.oldplayer25 = "";

        parme.olduser1 = "";
        parme.olduser2 = "";
        parme.olduser3 = "";
        parme.olduser4 = "";
        parme.olduser5 = "";
        parme.olduser6 = "";
        parme.olduser7 = "";
        parme.olduser8 = "";
        parme.olduser9 = "";
        parme.olduser10 = "";
        parme.olduser11 = "";
        parme.olduser12 = "";
        parme.olduser13 = "";
        parme.olduser14 = "";
        parme.olduser15 = "";
        parme.olduser16 = "";
        parme.olduser17 = "";
        parme.olduser18 = "";
        parme.olduser19 = "";
        parme.olduser20 = "";
        parme.olduser21 = "";
        parme.olduser22 = "";
        parme.olduser23 = "";
        parme.olduser24 = "";
        parme.olduser25 = "";

        parme.oldpcw1 = "";
        parme.oldpcw2 = "";
        parme.oldpcw3 = "";
        parme.oldpcw4 = "";
        parme.oldpcw5 = "";
        parme.oldpcw6 = "";
        parme.oldpcw7 = "";
        parme.oldpcw8 = "";
        parme.oldpcw9 = "";
        parme.oldpcw10 = "";
        parme.oldpcw11 = "";
        parme.oldpcw12 = "";
        parme.oldpcw13 = "";
        parme.oldpcw14 = "";
        parme.oldpcw15 = "";
        parme.oldpcw16 = "";
        parme.oldpcw17 = "";
        parme.oldpcw18 = "";
        parme.oldpcw19 = "";
        parme.oldpcw20 = "";
        parme.oldpcw21 = "";
        parme.oldpcw22 = "";
        parme.oldpcw23 = "";
        parme.oldpcw24 = "";
        parme.oldpcw25 = "";

        parme.oldguest_id1 = 0;
        parme.oldguest_id2 = 0;
        parme.oldguest_id3 = 0;
        parme.oldguest_id4 = 0;
        parme.oldguest_id5 = 0;
        parme.oldguest_id6 = 0;
        parme.oldguest_id7 = 0;
        parme.oldguest_id8 = 0;
        parme.oldguest_id9 = 0;
        parme.oldguest_id10 = 0;
        parme.oldguest_id11 = 0;
        parme.oldguest_id12 = 0;
        parme.oldguest_id13 = 0;
        parme.oldguest_id14 = 0;
        parme.oldguest_id15 = 0;
        parme.oldguest_id16 = 0;
        parme.oldguest_id17 = 0;
        parme.oldguest_id18 = 0;
        parme.oldguest_id19 = 0;
        parme.oldguest_id20 = 0;
        parme.oldguest_id21 = 0;
        parme.oldguest_id22 = 0;
        parme.oldguest_id23 = 0;
        parme.oldguest_id24 = 0;
        parme.oldguest_id25 = 0;

        if (parms.club.equals("shadycanyongolfclub")) {

            sendEmail.sendOakmontEmail(parme, con, parms.club);
            
        } else if (parms.club.equals("baltimore")  
               && (parme.pcw1.equals("CAD") || parme.pcw2.equals("CAD") || parme.pcw3.equals("CAD") || parme.pcw4.equals("CAD") || parme.pcw5.equals("CAD") 
                || parme.pcw6.equals("CAD") || parme.pcw7.equals("CAD") || parme.pcw8.equals("CAD") || parme.pcw9.equals("CAD") || parme.pcw10.equals("CAD"))) {
            
            sendEmail.sendOakmontEmail(parme, con, parms.club);
            
        } else if (parms.club.equals("rehobothbeachcc")
                && (parme.pcw1.equals("CAD") || parme.pcw1.equals("CFC")
                ||  parme.pcw2.equals("CAD") || parme.pcw2.equals("CFC")
                ||  parme.pcw3.equals("CAD") || parme.pcw3.equals("CFC")
                ||  parme.pcw4.equals("CAD") || parme.pcw4.equals("CFC")
                ||  parme.pcw5.equals("CAD") || parme.pcw5.equals("CFC")
                ||  parme.pcw6.equals("CAD") || parme.pcw6.equals("CFC")
                ||  parme.pcw7.equals("CAD") || parme.pcw7.equals("CFC")
                ||  parme.pcw8.equals("CAD") || parme.pcw8.equals("CFC")
                ||  parme.pcw9.equals("CAD") || parme.pcw9.equals("CFC")
                ||  parme.pcw10.equals("CAD") || parme.pcw10.equals("CFC")
                ||  parme.pcw11.equals("CAD") || parme.pcw11.equals("CFC")
                ||  parme.pcw12.equals("CAD") || parme.pcw12.equals("CFC")
                ||  parme.pcw13.equals("CAD") || parme.pcw13.equals("CFC")
                ||  parme.pcw14.equals("CAD") || parme.pcw14.equals("CFC")
                ||  parme.pcw15.equals("CAD") || parme.pcw15.equals("CFC"))) {
            
            sendEmail.sendOakmontEmail(parme, con, parms.club);
            
        } else if (parms.club.equals("silverleaf")
                && (parme.pcw1.equals("CAD") || parme.pcw1.equals("FOR")
                ||  parme.pcw2.equals("CAD") || parme.pcw2.equals("FOR")
                ||  parme.pcw3.equals("CAD") || parme.pcw3.equals("FOR")
                ||  parme.pcw4.equals("CAD") || parme.pcw4.equals("FOR")
                ||  parme.pcw5.equals("CAD") || parme.pcw5.equals("FOR")
                ||  parme.pcw6.equals("CAD") || parme.pcw6.equals("FOR")
                ||  parme.pcw7.equals("CAD") || parme.pcw7.equals("FOR")
                ||  parme.pcw8.equals("CAD") || parme.pcw8.equals("FOR")
                ||  parme.pcw9.equals("CAD") || parme.pcw9.equals("FOR")
                ||  parme.pcw10.equals("CAD") || parme.pcw10.equals("FOR"))) {
            
            sendEmail.sendOakmontEmail(parme, con, parms.club);
            
        } else if (parms.club.equals("pradera") || parms.club.equals("pinery") || parms.club.equals("tartanfields")) {
            
            // If tee time contains a guest, copy a staff email in on the notification
            if (!parms.g[1].equals("") || !parms.g[2].equals("") || !parms.g[3].equals("") || !parms.g[4].equals("") || !parms.g[5].equals("") 
                    || !parms.g[6].equals("") || !parms.g[7].equals("") || !parms.g[8].equals("") || !parms.g[9].equals("") || !parms.g[10].equals("") 
                    || !parms.g[11].equals("") || !parms.g[12].equals("") || !parms.g[13].equals("") || !parms.g[14].equals("") || !parms.g[15].equals("") 
                    || !parms.g[16].equals("") || !parms.g[17].equals("") || !parms.g[18].equals("") || !parms.g[19].equals("") || !parms.g[20].equals("") 
                    || !parms.g[21].equals("") || !parms.g[22].equals("") || !parms.g[23].equals("") || !parms.g[24].equals("") || !parms.g[25].equals("")) {
                
                sendEmail.sendOakmontEmail(parme, con, parms.club);
            }
        }

        //
        //  Send the email
        //
        sendEmail.sendIt(parme, con);      // in common

    }    // end of sendMail method

    // *********************************************************
    //  Return to _slotm 
    // *********************************************************
    private void goReturn(PrintWriter out, parmSlotm parm) {

        out.println("<font size=\"2\">");
        out.println("<form action=\"Member_slotm\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
        out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time1 + "\">");
        out.println("<input type=\"hidden\" name=\"time2\" value=\"" + parm.time2 + "\">");
        out.println("<input type=\"hidden\" name=\"time3\" value=\"" + parm.time3 + "\">");
        out.println("<input type=\"hidden\" name=\"time4\" value=\"" + parm.time4 + "\">");
        out.println("<input type=\"hidden\" name=\"time5\" value=\"" + parm.time5 + "\">");
        out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
        out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
        out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
        out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
        out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
        out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
        out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
        out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
        out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
        out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
        out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
        out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
        out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
        out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
        out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
        out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
        out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
        out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
        out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
        out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
        out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
        out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
        out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
        out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
        out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
        out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
        out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
        out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
        out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
        out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
        out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
        out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
        out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
        out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
        out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
        out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
        out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
        out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
        out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
        out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
        out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
        out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
        out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
        out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
        out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
        out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
        out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
        out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
        out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
        out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
        out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
        out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
        out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
        out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
        out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
        out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
        out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
        out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
        out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
        out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
        out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
        out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
        out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
        out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
        out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
        out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
        out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
        out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    // *********************************************************
    //  Return to _slotm
    // *********************************************************
    private void goReturn2(PrintWriter out, parmSlotm parm) {

        out.println("<font size=\"2\">");
        out.println("<form action=\"Member_slotm\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
        out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time1 + "\">");
        out.println("<input type=\"hidden\" name=\"time2\" value=\"" + parm.time2 + "\">");
        out.println("<input type=\"hidden\" name=\"time3\" value=\"" + parm.time3 + "\">");
        out.println("<input type=\"hidden\" name=\"time4\" value=\"" + parm.time4 + "\">");
        out.println("<input type=\"hidden\" name=\"time5\" value=\"" + parm.time5 + "\">");
        out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
        out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
        out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
        out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
        out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
        out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
        out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
        out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
        out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
        out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
        out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
        out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
        out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
        out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
        out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
        out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
        out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
        out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
        out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
        out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
        out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
        out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
        out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
        out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
        out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
        out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
        out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
        out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
        out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
        out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
        out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
        out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
        out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
        out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
        out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
        out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
        out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
        out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
        out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
        out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
        out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
        out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
        out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
        out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
        out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
        out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
        out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
        out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
        out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
        out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
        out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
        out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
        out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
        out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
        out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
        out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
        out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
        out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
        out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
        out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
        out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
        out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
        out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
        out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
        out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
        out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
        out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
        out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");
        //
        //  Return to process the players as they are
        //
        out.println("<font size=\"2\">");
        out.println("<form action=\"Member_slotm\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
        out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
        out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
        out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
        out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
        out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
        out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
        out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
        out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
        out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
        out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
        out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
        out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
        out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
        out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
        out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
        out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
        out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
        out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
        out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
        out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
        out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
        out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
        out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
        out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
        out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
        out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
        out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
        out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
        out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
        out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
        out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
        out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
        out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
        out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
        out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
        out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
        out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
        out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
        out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
        out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
        out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
        out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
        out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
        out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
        out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
        out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
        out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
        out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
        out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
        out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
        out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
        out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
        out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
        out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
        out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
        out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
        out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
        out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
        out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
        out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
        out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
        out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
        out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
        out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
        out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
        out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time1 + "\">");
        out.println("<input type=\"hidden\" name=\"time2\" value=\"" + parm.time2 + "\">");
        out.println("<input type=\"hidden\" name=\"time3\" value=\"" + parm.time3 + "\">");
        out.println("<input type=\"hidden\" name=\"time4\" value=\"" + parm.time4 + "\">");
        out.println("<input type=\"hidden\" name=\"time5\" value=\"" + parm.time5 + "\">");
        out.println("<input type=\"hidden\" name=\"mm\" value=\"" + parm.mm + "\">");
        out.println("<input type=\"hidden\" name=\"yy\" value=\"" + parm.yy + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
        out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
        for (int i = 0; i < 25; i++) {
            out.println("<input type=\"hidden\" name=\"userg" + i + "\" value=\"" + parm.userg[i] + "\">");
        }
        out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    // *********************************************************
    // Check if player already scheduled
    // *********************************************************
    private boolean chkPlayer(Connection con, String player, long date, int time, int fb, String course) {


        boolean hit = false;

        ResultSet rs = null;

        int time2 = 0;
        int fb2 = 0;
        long id2 = 0;

        String course2 = "";

        try {

            PreparedStatement pstmt21 = con.prepareStatement(
                    "SELECT time, fb, courseName FROM teecurr2 "
                    + "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND date = ?");

            pstmt21.clearParameters();        // clear the parms and check player 1
            pstmt21.setString(1, player);
            pstmt21.setString(2, player);
            pstmt21.setString(3, player);
            pstmt21.setString(4, player);
            pstmt21.setString(5, player);
            pstmt21.setLong(6, date);
            rs = pstmt21.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

                time2 = rs.getInt("time");
                fb2 = rs.getInt("fb");
                course2 = rs.getString("courseName");

                if ((time2 != time) || (fb2 != fb) || (!course2.equals(course))) {      // if not this tee time

                    hit = true;                    // player already scheduled on this date
                }
            }
            pstmt21.close();

            //
            //  check if player already on a lottery request
            //
            PreparedStatement pstmt22 = con.prepareStatement(
                    "SELECT id FROM lreqs3 "
                    + "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR "
                    + "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR "
                    + "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR "
                    + "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR "
                    + "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ?");

            pstmt22.clearParameters();        // clear the parms and check player 1
            pstmt22.setString(1, player);
            pstmt22.setString(2, player);
            pstmt22.setString(3, player);
            pstmt22.setString(4, player);
            pstmt22.setString(5, player);
            pstmt22.setString(6, player);
            pstmt22.setString(7, player);
            pstmt22.setString(8, player);
            pstmt22.setString(9, player);
            pstmt22.setString(10, player);
            pstmt22.setString(11, player);
            pstmt22.setString(12, player);
            pstmt22.setString(13, player);
            pstmt22.setString(14, player);
            pstmt22.setString(15, player);
            pstmt22.setString(16, player);
            pstmt22.setString(17, player);
            pstmt22.setString(18, player);
            pstmt22.setString(19, player);
            pstmt22.setString(20, player);
            pstmt22.setString(21, player);
            pstmt22.setString(22, player);
            pstmt22.setString(23, player);
            pstmt22.setString(24, player);
            pstmt22.setString(25, player);
            pstmt22.setLong(26, date);
            rs = pstmt22.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

                hit = true;                    // player already scheduled on this date
            }
            pstmt22.close();
        } catch (Exception ignore) {
        }

        return hit;
    }
    
    private int checkPlayer(String username, parmSlotm parm, String club, HttpServletRequest req) {
        
        Connection con = Connect.getCon(req);
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int count = 0;
        
        // Get a count of tee times this member is a part of on the current date.
        try {
            
            pstmt = con.prepareStatement("SELECT count(*) AS round_count FROM teecurr2 WHERE date = ? AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
            pstmt.clearParameters();
            pstmt.setLong(1, parm.date);
            
            for (int i = 2; i <= 6; i++) {
                pstmt.setString(i, username);
            }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("round_count");
            }         
            
        } catch (Exception e) {
            Utilities.logError("Member_slotm.checkPlayer - " + club + " - Failed looking up tee times for this day - Error=" + e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        // Get a count of lottery requests this member is a part of for the current date.
        try {
            
            pstmt = con.prepareStatement("SELECT count(*) AS round_count FROM lreqs3 WHERE date = ? "
                    + "AND (user1 = ? OR user2 = ? OR user3 = ? OR user4 = ? OR user5 = ? OR user6 = ? OR user7 = ? OR user8 = ? OR user9 = ? OR user10 = ? "
                    + "OR user11 = ? OR user12 = ? OR user13 = ? OR user14 = ? OR user15 = ? OR user16 = ? OR user17 = ? OR user18 = ? OR user19 = ? "
                    + "OR user20 = ? OR user21 = ? OR user22 = ? OR user23 = ? OR user24 = ? OR user25 = ?)");
            pstmt.clearParameters();
            pstmt.setLong(1, parm.date);
            
            for (int i = 2; i <= 26; i++) {
                pstmt.setString(i, username);
            }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count += rs.getInt("round_count");
            }
            
        } catch (Exception e) {
            Utilities.logError("Member_slotm.checkPlayer - " + club + " - Failed looking up lottery requests for this day - Error=" + e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return count;
    }
    
    private int checkPlayerProxim(int player_index, parmSlotm parm, String club, HttpServletRequest req) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int nearest = -1;      
        
        try {
            
            pstmt = Connect.getCon(req).prepareStatement("SELECT IFNULL(MIN(ABS(TIME_FORMAT(TIMEDIFF(?, CONCAT(TIME, '00')), '%H'))), -1) AS nearest FROM teecurr2 "
                    + "WHERE date = ? AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
            
            pstmt.clearParameters();
            pstmt.setInt(1, parm.getPlayerTime(player_index) * 100);
            pstmt.setLong(2, parm.date);
            pstmt.setString(3, parm.getUser(player_index));
            pstmt.setString(4, parm.getUser(player_index));
            pstmt.setString(5, parm.getUser(player_index));
            pstmt.setString(6, parm.getUser(player_index));
            pstmt.setString(7, parm.getUser(player_index));
            
            rs = pstmt.executeQuery();
                                  
            if (rs.next()) {
                nearest = rs.getInt("nearest");
            }
            
        } catch (Exception e) {
            Utilities.logError("Member_slotm.checkPlayerProx - " + club + " - Failed looking up nearest tee time for user=" + parm.getUser(player_index) + " - Error=" + e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return nearest;
    }

    // *********************************************************
    // Check Member Number Restrictions
    // *********************************************************
    private int checkmNum(String mNum, long date, int rest_etime, int rest_stime, int time, String course, String rest_fb, String rest_course, Connection con) {


        ResultSet rs7 = null;

        int ind = 0;
        int time2 = 0;
        int t_fb = 0;

        String course2 = "";
        String sfb2 = "";
        String rmNum1 = "";
        String rmNum2 = "";
        String rmNum3 = "";
        String rmNum4 = "";
        String rmNum5 = "";

        try {

            PreparedStatement pstmt7c = con.prepareStatement(
                    "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 "
                    + "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? "
                    + "AND time <= ? AND time >= ?");

            pstmt7c.clearParameters();        // clear the parms and check player 1
            pstmt7c.setString(1, mNum);
            pstmt7c.setString(2, mNum);
            pstmt7c.setString(3, mNum);
            pstmt7c.setString(4, mNum);
            pstmt7c.setString(5, mNum);
            pstmt7c.setLong(6, date);
            pstmt7c.setInt(7, rest_etime);
            pstmt7c.setInt(8, rest_stime);
            rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

            while (rs7.next()) {

                time2 = rs7.getInt("time");
                t_fb = rs7.getInt("fb");
                course2 = rs7.getString("courseName");
                rmNum1 = rs7.getString("mNum1");
                rmNum2 = rs7.getString("mNum2");
                rmNum3 = rs7.getString("mNum3");
                rmNum4 = rs7.getString("mNum4");
                rmNum5 = rs7.getString("mNum5");

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
                if ((time2 != time) || (!course2.equals(course))) {  // either time or course is diff

                    if ((rest_fb.equals("Both") || rest_fb.equals(sfb2))
                            && (rest_course.equals("-ALL-") || rest_course.equals(course2))) {

                        if (mNum.equals(rmNum1)) {
                            ind++;
                        }
                        if (mNum.equals(rmNum2)) {
                            ind++;
                        }
                        if (mNum.equals(rmNum3)) {
                            ind++;
                        }
                        if (mNum.equals(rmNum4)) {
                            ind++;
                        }
                        if (mNum.equals(rmNum5)) {
                            ind++;
                        }
                    }
                }

            } // end of while members

            pstmt7c.close();

        } catch (Exception ignore) {
        }

        return (ind);
    }

    // *********************************************************
    //  Process cancel request from Member_slotm (HTML)
    // *********************************************************
    private void cancel(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {

        int count = 0;
        int time = 0;
        int time2 = 0;
        int time3 = 0;
        int time4 = 0;
        int time5 = 0;
        int time6 = 0;
        int time7 = 0;
        int time8 = 0;
        int time9 = 0;
        int time10 = 0;
        int time11 = 0;
        int time12 = 0;
        int time13 = 0;
        int time14 = 0;
        int time15 = 0;
        int slots = 0;
        int fb = 0;
        long date = 0;

        //
        // Get all the parameters entered
        //
        String index = req.getParameter("index");          //  index value of day (needed by Member_sheet when returning)
        String course = req.getParameter("course");        //  name of course
        String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
        String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
        String stime2 = req.getParameter("time2");
        String stime3 = req.getParameter("time3");
        String stime4 = req.getParameter("time4");
        String stime5 = req.getParameter("time5");
        String stime6 = req.getParameter("time6");
        String stime7 = req.getParameter("time7");
        String stime8 = req.getParameter("time8");
        String stime9 = req.getParameter("time9");
        String stime10 = req.getParameter("time10");
        String stime11 = req.getParameter("time11");
        String stime12 = req.getParameter("time12");
        String stime13 = req.getParameter("time13");
        String stime14 = req.getParameter("time14");
        String stime15 = req.getParameter("time15");
        String sfb = req.getParameter("fb");               //  front/back indicator
        String slotss = req.getParameter("slots");         //  # of times requested
        String returnCourse = req.getParameter("returnCourse");        //  name of course to return to

        boolean json_mode = ((req.getParameter("json_mode") != null) ? true : false);

        //
        //  Convert the values from string to int
        //
        try {
            
            date = Long.parseLong(sdate);
            slots = Integer.parseInt(slotss);
            fb = Integer.parseInt(sfb);
            time = Integer.parseInt(stime);
            time2 = Integer.parseInt(stime2);
            time3 = Integer.parseInt(stime3);
            time4 = Integer.parseInt(stime4);
            time5 = Integer.parseInt(stime5);
            time6 = Integer.parseInt(stime6);
            time7 = Integer.parseInt(stime7);
            time8 = Integer.parseInt(stime8);
            time9 = Integer.parseInt(stime9);
            time10 = Integer.parseInt(stime10);
            time11 = Integer.parseInt(stime11);
            time12 = Integer.parseInt(stime12);
            time13 = Integer.parseInt(stime13);
            time14 = Integer.parseInt(stime14);
            time15 = Integer.parseInt(stime15);
            
        } catch (NumberFormatException e) {
            // log the error
            Utilities.logError("Member_slotm.cancel - Exception converting strings to ints - ERR: " + e.toString());
        }

        //
        //  Clear the 'in_use' flag for this request
        //
        try {

            verifySlot.clearInUseM(date, time, time2, time3, time4, time5, time6, time7, time8, time9, time10, time11, time12, time13, time14, time15,  fb, course, con, session);

        } catch (Exception e2) {
            // log the error
            Utilities.logError("Member_slotm.cancel - Exception clearing the tee time in use flag - ERR: " + e2.toString());
        }

        if (!returnCourse.equals("")) {       // if multi course club, get course to return to (ALL?)
            course = returnCourse;
        }

        if (json_mode) {
            
            Gson gson_obj = new Gson();
            Map<String, Object> result_map = new LinkedHashMap<String, Object>();
            result_map.put("index", index);
            result_map.put("course", course);
            out.print(gson_obj.toJson(result_map));
            
        } else {
            //
            //  Prompt user to return to Member_sheet or Member_teelist (index = 888)
            //
            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Member Tee Time Request Page</Title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_jump?index=" + index + "&course=" + course + "\">");
            out.println("</HEAD>");
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
            out.println("<BR><BR>Thank you, no changes were made to the tee times selected.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
        }
    }

    // ************************************************************************
    //  Get number of days between today and the date provided
    // ************************************************************************
    private int getDaysBetween(long date) {


        //
        //  break down date provided
        //
        long yy = date / 10000;                             // get year
        long mm = (date - (yy * 10000)) / 100;              // get month
        long dd = (date - (yy * 10000)) - (mm * 100);       // get day

        int month = (int) mm;
        int day = (int) dd;
        int year = (int) yy;

        //
        //  Check if this tee time is within 30 days of the current date (today)
        //
        BigDate today = BigDate.localToday();                 // get today's date
        BigDate thisdate = new BigDate(year, month, day);     // get requested date

        int ind = (thisdate.getOrdinal() - today.getOrdinal());   // number of days between

        return (ind);

    }       // end of getDaysBetween

    // *********************************************************
    //  Prompt user when not all tee times requested are available.
    // *********************************************************
    private void promptLessTimes(boolean new_skin, PrintWriter out, parmSlotm parm, parmSlotPage slotPageParms) {


        String[] stimeA = new String[15];
        String ampm = "";

        int[] timeA = new int[15];
        int hr = 0;
        int min = 0;


        timeA[0] = parm.time1;        // put time values in array for working
        timeA[1] = parm.time2;
        timeA[2] = parm.time3;
        timeA[3] = parm.time4;
        timeA[4] = parm.time5;
        timeA[5] = parm.time6;
        timeA[6] = parm.time7;
        timeA[7] = parm.time8;
        timeA[8] = parm.time9;
        timeA[9] = parm.time10;
        timeA[10] = parm.time11;
        timeA[11] = parm.time12;
        timeA[12] = parm.time13;
        timeA[13] = parm.time14;
        timeA[14] = parm.time15;

        //
        //  create a time string for display
        //
        for (int i = 0; i < 15; i++) {

            if (timeA[i] > 0) {         // if time exists

                hr = timeA[i] / 100;
                min = timeA[i] - (hr * 100);

                ampm = "AM";

                if (hr > 11) {

                    ampm = "PM";

                    if (hr > 12) {

                        hr = hr - 12;
                    }
                }
                if (min < 10) {
                    stimeA[i] = hr + ":0" + min + " " + ampm;
                } else {
                    stimeA[i] = hr + ":" + min + " " + ampm;
                }

            } else {

                stimeA[i] = "";
            }
        }

        //
        //  Prompt the user to either accept the times available or return to the tee sheet
        //
        if (new_skin) {

            // Set slot time to that of newly reserved time
            slotPageParms.callback_map.remove("ttdata");
            slotPageParms.callback_map.put("stime", stimeA[0]);
            slotPageParms.callback_map.put("time2", timeA[1]);
            slotPageParms.callback_map.put("time3", timeA[2]);
            slotPageParms.callback_map.put("time4", timeA[3]);
            slotPageParms.callback_map.put("time5", timeA[4]);
            slotPageParms.callback_map.put("time6", timeA[5]);
            slotPageParms.callback_map.put("time7", timeA[6]);
            slotPageParms.callback_map.put("time8", timeA[7]);
            slotPageParms.callback_map.put("time9", timeA[8]);
            slotPageParms.callback_map.put("time10", timeA[9]);
            slotPageParms.callback_map.put("time11", timeA[10]);
            slotPageParms.callback_map.put("time12", timeA[11]);
            slotPageParms.callback_map.put("time13", timeA[12]);
            slotPageParms.callback_map.put("time14", timeA[13]);
            slotPageParms.callback_map.put("time15", timeA[14]);
            slotPageParms.callback_map.put("fb", parm.fb);
            slotPageParms.callback_map.put("promptLessTimes", "yes");

            //slotPageParms.callback_map.put("time:0", stimeA[0]);
            //slotPageParms.callback_map.put("ttdata", Utilities.encryptTTdata(stimeA[0] + "|" + parm.fb + "|" + slotPageParms.user));
            slotPageParms.page_start_title = "Notice";
            slotPageParms.page_start_notifications.add("One or more of the tee times you requested is currently busy.");
            slotPageParms.page_start_button_go_back = true;
            slotPageParms.page_start_button_continue = true;
            slotPageParms.page_start_notifications.add("There are " + parm.slots + " tee times available, as follows:");
            String timeListHtml = "";
            for (int i = 0; i < stimeA.length; i++) {
                if (!stimeA[i].equals("")) {
                    timeListHtml += "<li>" + stimeA[i] + "</li>";
                }
            }
            slotPageParms.page_start_notifications.add("<ul class=\"indented_list\">" + timeListHtml + "</ul>");
            slotPageParms.page_start_notifications.add("Select \"[modalOptions.slotPageLoadNotification.continueButton]\" to use these alternate times.");

        } else {
            out.println("<HTML><HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Member Prompt - Multiple Tee Time Request</Title>");
            out.println("</HEAD>");

            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\">");
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
            out.println("<br>One or more of the tee times you requested is currently busy.<br>");
            out.println("There are " + parm.slots + " tee times available, as follows:<br><br>");
            out.println("&nbsp;&nbsp;&nbsp;" + stimeA[0] + "<br>");
            out.println("&nbsp;&nbsp;&nbsp;" + stimeA[1] + "<br>");
            if (!stimeA[2].equals("")) {
                out.println("&nbsp;&nbsp;&nbsp;" + stimeA[2] + "<br>");
            }
            if (!stimeA[3].equals("")) {
                out.println("&nbsp;&nbsp;&nbsp;" + stimeA[3] + "<br>");
            }
            if (!stimeA[4].equals("")) {
                out.println("&nbsp;&nbsp;&nbsp;" + stimeA[4] + "<br>");
            }
            out.println("<br>Would you like to accept these times?<br>");
            out.println("</font><font size=\"3\">");
            out.println("<br><b>Please select your choice below. DO NOT use you browser's BACK button!</b><br>");
            out.println("</font></td></tr>");
            out.println("</table><br>");

            out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<form action=\"Member_slotm\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + timeA[0] + "\">");
            out.println("<input type=\"hidden\" name=\"time2\" value=\"" + timeA[1] + "\">");
            out.println("<input type=\"hidden\" name=\"time3\" value=\"" + timeA[2] + "\">");
            out.println("<input type=\"hidden\" name=\"time4\" value=\"" + timeA[3] + "\">");
            out.println("<input type=\"hidden\" name=\"time5\" value=\"" + timeA[4] + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
            out.println("<input type=\"hidden\" name=\"cancel\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"NO - Return to Tee Sheet\"></form>");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<form action=\"Member_slotm\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stimeA[0] + "\">");
            out.println("<input type=\"hidden\" name=\"time2\" value=\"" + timeA[1] + "\">");
            out.println("<input type=\"hidden\" name=\"time3\" value=\"" + timeA[2] + "\">");
            out.println("<input type=\"hidden\" name=\"time4\" value=\"" + timeA[3] + "\">");
            out.println("<input type=\"hidden\" name=\"time5\" value=\"" + timeA[4] + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
            out.println("<input type=\"hidden\" name=\"promptLessTimes\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
            out.println("</font></td></tr>");
            out.println("</table>");

            out.println("</td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</font></center></body></html>");
            out.close();
        }
    }

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
        //out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>" + e1);
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(1)\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        //out.close();
        if (true) {
            throw new RuntimeException(e1);
        }
        return;

    }

    // *********************************************************
    // Invalid data received - reject request
    // *********************************************************
    private void invData(PrintWriter out, String player) {

        out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><H3>Invalid Data Received</H3><BR>");
        out.println("<BR><BR>Sorry, a name you entered (<b>" + player + "</b>) is not valid.<BR>");
        out.println("Please check the names and try again.");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    // *********************************************************
    // Invalid data received - reject request
    // *********************************************************
    private void dupData(PrintWriter out, String player) {

        out.println(SystemUtils.HeadTitle("Data Entry Error"));
        out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<BR><BR><H3>Data Entry Error</H3>");
        out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
        out.println("<BR><BR>Please correct this and try again.");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    // *********************************************************
    // checkTmodes - Checks if Pro-Only tmodes are being used without permission
    // returns false if errors found
    // *********************************************************
    private boolean checkTmodes(Connection con, PrintWriter out, parmSlotm parm) {


        parm.error = false;               // init
        boolean check = false;

        String player = "";
        String rest_name = "";


        //
        //  Allocate a new parm block for each tee time and call common meehtod to process each.
        //
        parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

        //  Allocate a new parm block for the course parameters
        parmCourse parmc = new parmCourse();

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
        parm1.oldPlayer1 = "";       // always empty from here
        parm1.oldPlayer2 = "";
        parm1.oldPlayer3 = "";
        parm1.oldPlayer4 = "";
        parm1.oldPlayer5 = "";
        parm1.fb = parm.fb;
        parm1.ind = parm.ind;      // index value
        parm1.sfb = parm.sfb;
        parm1.club = parm.club;    // name of club

        //  Get the walk/cart options available  //
        try {
            getParms.getTmodes(con, parmc, parm.course);
        } catch (Exception e1) {
            dbError(out, e1);
        }

        //
        //  Check in groups of 5
        //
        if (parmc.hasProOnlyTmodes) {
            try {
                // if first group of 5 contains any players, check their tmodes
                if (!parm.player1.equals("") || !parm.player2.equals("") || !parm.player3.equals("") || !parm.player4.equals("") || !parm.player5.equals("")) {

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
                    parm1.p1cw = parm.pcw1;
                    parm1.p2cw = parm.pcw2;
                    parm1.p3cw = parm.pcw3;
                    parm1.p4cw = parm.pcw4;
                    parm1.p5cw = parm.pcw5;

                    if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                        parm.player = parm1.player;
                        return false;
                    }
                }

                // check 2nd group
                if (!parm.player6.equals("") || !parm.player7.equals("") || !parm.player8.equals("") || !parm.player9.equals("") || !parm.player10.equals("")) {

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
                    parm1.p1cw = parm.pcw6;
                    parm1.p2cw = parm.pcw7;
                    parm1.p3cw = parm.pcw8;
                    parm1.p4cw = parm.pcw9;
                    parm1.p5cw = parm.pcw10;

                    if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                        parm.player = parm1.player;
                        return false;

                    }
                }

                // check 3rd group
                if (!parm.player11.equals("") || !parm.player12.equals("") || !parm.player13.equals("") || !parm.player14.equals("") || !parm.player15.equals("")) {

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
                    parm1.p1cw = parm.pcw11;
                    parm1.p2cw = parm.pcw12;
                    parm1.p3cw = parm.pcw13;
                    parm1.p4cw = parm.pcw14;
                    parm1.p5cw = parm.pcw15;

                    if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                        parm.player = parm1.player;
                        return false;
                    }
                }

                // check 4th group
                if (!parm.player16.equals("") || !parm.player17.equals("") || !parm.player18.equals("") || !parm.player19.equals("") || !parm.player20.equals("")) {

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
                    parm1.p1cw = parm.pcw16;
                    parm1.p2cw = parm.pcw17;
                    parm1.p3cw = parm.pcw18;
                    parm1.p4cw = parm.pcw19;
                    parm1.p5cw = parm.pcw20;

                    if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                        parm.player = parm1.player;
                        return false;
                    }
                }

                // check 5th group
                if (!parm.player21.equals("") || !parm.player22.equals("") || !parm.player23.equals("") || !parm.player24.equals("") || !parm.player25.equals("")) {

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
                    parm1.p1cw = parm.pcw21;
                    parm1.p2cw = parm.pcw22;
                    parm1.p3cw = parm.pcw23;
                    parm1.p4cw = parm.pcw24;
                    parm1.p5cw = parm.pcw25;

                    if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                        parm.player = parm1.player;
                        return false;
                    }
                }
            } catch (Exception e7) {

                dbError(out, e7);
                parm.error = true;               // inform caller of error
            }
        }

        return true;

    }         // end of checkTmodes
    
    
    
    
    // *********************************************************
    // checkShotgunEvent - Custom check for Open Play Shotgun Event
    // returns true if it is
    // *********************************************************
    private String checkShotgunEvent(long date, int time, int fb, String course, Connection con) {


        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String event = "";
        
        int cat_id = 13;      // Open Play Shotguns must be id 13 for Moss Creek

    
        //
        //  Check to see if the first requested tee time is part of an Open Play Shotgun Event
        //
        try {
        
            pstmt = con.prepareStatement(
                    "SELECT event "
                    + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt.clearParameters();      
            pstmt.setLong(1, date);     
            pstmt.setInt(2, time);
            pstmt.setInt(3, fb);
            pstmt.setString(4, course);
            rs = pstmt.executeQuery();     

            if (rs.next()) {

                event = rs.getString("event");     // get event name if one exists
            }
        
        } catch (Exception exc) {

            event = "";
            
        } finally {

            try { pstmt.close(); }
            catch (SQLException ignored) {}
        }
    
        if (!event.equals("")) {     // if event exists
            
            int event_id = Utilities.getEventIdFromName(event, con);       // check event category for "Open Play Shotguns"

            ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
            temp_category_ids.add(cat_id);                                 // cat_id must equal the id for Open Play Shotguns

            String cat_name = Utilities.checkEventCategoryBindings(event_id, temp_category_ids, con);   

            if (!cat_name.equals("")) {     // cat_name will be empty if it matches

                event = "";        // NOT an Open Play Shotgun
            }
        }

        return (event);

    }         // end of checkShotgunEvent
    
    
    
    
    // ************************************************************************
    //  Admirals Cove - check if user is cheating and came in early
    // ************************************************************************
    private boolean checkACearly(String user, int ind, int count) {


        String errMsg = "Admirals Cove Member attempting to access tee time early.  Error = ";
        boolean error = false;

        //
        //  Get this exact time and see if the user is trying to get into a tee time early
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int thishr = cal.get(Calendar.HOUR_OF_DAY);
        int thismin = cal.get(Calendar.MINUTE);
        int thissec = cal.get(Calendar.SECOND);

        int thisTime = (thishr * 10000) + (thismin * 100) + thissec;         // get current time (Central TIme!! - hhmmss)


        if (ind > 3) {                 // too many days in adv?

            error = true;
            errMsg = errMsg + ind + " days in advance. User = " + user;

        } else if (ind == 3 && thisTime < 63002) {

            error = true;
            errMsg = errMsg + "Too early. Time = " + thisTime + " CT, User = " + user;

        } else if (count > 2) {

            error = true;
            errMsg = errMsg + "Too many times requested. Times = " + count + " CT, User = " + user;
        }

        if (error == true) {

            SystemUtils.logError(errMsg);             // log it
        }

        return (error);

    }       // end of checkACearly

    
    
    // ************************************************************************
    //  Admirals Cove - force a logoff, member is cheating
    // ************************************************************************
    private void logoffAC(PrintWriter out, HttpSession session, Connection con) {


        out.println("<HTML><HEAD><Title>Force Exit Page</Title>");
        out.println("</HEAD>");
        out.println("<BODY><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<p>&nbsp;</p>");
        out.println("<BR><BR><H2>Unauthorized Access</H2><BR>");
        out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\">");
        out.println("ForeTees has detected that you have accessed a tee time prior to the parameters set");
        out.println("<BR>forth by your club staff. As a result you will be logged off and have to log on again.");
        out.println("</td></tr></table><br>");
        out.println("<br><br><font size=\"2\">");
        out.println("<form><input type=\"button\" value=\"RETURN\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form>");
        out.println("</font>");
        out.println("</CENTER></BODY></HTML>");


        if (con != null) {

            try {

              //con.rollback();    // abandon any unfinished transactions

                con.close();       // return/close the connection (it should already be closed!!)

            } catch (SQLException e) {
            }
        }

        // clear the users session variables
        session.removeAttribute("user");
        session.removeAttribute("member_id");
        session.removeAttribute("club");
        session.removeAttribute("connect");

        // end the users session
        session.invalidate();

    }       // end of logoffAC
}
