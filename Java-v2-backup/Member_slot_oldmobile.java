
/***************************************************************************************
 *   Member_slot:  This servlet will process the 'Reserve Tee Time' request from
 *                    the Member's Sheet page.
 *
 *
 *   called by:  Member_sheet (doPost)
 *               self on cancel request
 *               Member_teelist (doPost)
 *
 *
 *   created: 1/14/2002   Bob P.
 *
 *   last updated:             ******* keep this accurate *******
 *
 *        5/02/14   Oak Hill CC (oakhillcc) - add custom for proshop user to specify if tee time is an advance guest time and track these (case 2361).
 *        4/14/14   CC of Naples - put in checks to catch cheaters that are getting in early for tee times.
 *        4/10/14   Baltusrol GC (baltusrolgc) - Added custom to restrict certain times to 2-some only times (case 2389).
 *        3/10/14   Greeley CC (fortcollins) - Removed custom that was limiting members to only one guest per time.
 *        3/06/14   Bay Hill Club (bayhill) - Added to restrictByOrig custom (case 2380).
 *        2/21/14   Bishops Gate GC (bishopsgategc) - Added custom to grab and block crossover times (case 2358).
 *        2/19/14   Plantation G & CC (plantationgcc) - Added to restrictByOrig custom (case 2373).
 *        2/11/14   Olympic Club (olyclub) - Updated custom to check for both "MNHGP" or "Member" for mships.
 *        2/10/14   Tartan Fields GC (tartanfields) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *        2/10/14   TPC Snoqualmie Ridge (snoqualmieridge) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *        2/10/14   Pinery CC (pinery) - Added to sendOakmontEmail for receiving staff email notificaitons for guest times.
 *        2/10/14   The Club at Pradera (pradera) - Added to senddOakmontEmail for receiving staff email notificaitons for guest times.
 *        2/10/14   Updated how the sendOakmontEmail customs are triggered to make it a simpler process.
 *        2/10/14   The originator of the tee time will now be passed through in the parmEmail object.
 *        1/30/14   Shady Canyon GC (shadycanyongolfclub) - Added to restrictByOrig custom (case 2359).
 *        1/29/14   Castle Pines CC (castlepines) - Updated custom message for X's (case 2003).
 *        1/28/14   Lakewood Ranch (lakewoodranch) - Do not display the 'WLK' MoT for members in tee times prior to 1:00pm every day (case 2355).
 *        1/16/14   Governors Club (governorsclub) - Pre-checkin custom updated to apply at 4:00pm ET instead (case 2351).
 *        1/16/14   Governors Club (governorsclub) - Added custom to utilize pre-checkin for tomorrow bookings if it's after 6:30pm ET today (case 2351).
 *        1/16/14   Marbella CC (marbellacc) - Added to restrictByOrig custom (case 2350).
 *        1/10/14   Pelican Marsh GC (pmarshgc) - Added custom to send the proshop an email notification whenever a member books a new tee time on the day of (case 2336).
 *       12/13/13   Silverleaf Club (silverleaf) - Updated caddie master email custom to also send emails for the "FOR" MoT (case 2329).
 *       12/10/13   Silverleaf Club (silverleaf) - Added custom to send the caddie master an email if tee times containing "CAD" MoTs are booked/modified/canceled (case 2329).
 *       11/05/13   BallenIsles CC (ballenisles) - Added to restrictByOrig custom (case 2320).
 *       11/01/13   Olympic Club (olyclub) - changed guest type "MNHGP w/guest" to "Member w/Guest" per club's request.
 *       10/16/13   Brookridge GF (brookridgegf) - Added to restrictByOrig custom.
 *       10/10/13   Ridge CC (ridgecc) - Added a custom message to be displayed to members when they hit a guest restriction message, which instructs them to call the shop for assistance.
 *       10/03/13   Belfair CC (belfair) - Added custom to send an email to 4 staff members whenever a tee time containing guests is canceled (case 2307).
 *        9/26/13   buildError method will no longer print the title line if a blank title String was passed to it.
 *        8/22/13   Bald Peak (baldpeak) - Added custom to only allow the originator of a tee time to make or change the Notes (case 2293).
 *        8/16/13   Philly Cricket Club (philcricket) - Added custom to default all player slots to 9-hole on the St Martins course.
 *        7/26/13   NJ PGA (pganj) - Moved custom to fill in 3 guests earlier in the code so it gets applied for mobile users as well.
 *        7/08/13   Oak Hill CC (oakhillcc) - Removed custom to send golf chair an email whenever someone cancels a time.
 *        6/27/13   Cherry Hills CC (cherryhills) - Updated junior custom to consider any mtype that ends with "Member" or "Spouse" to be an adult.
 *        6/20/13   Philly Cricket Club Recip Site (philcricketrecip) - Added custom to cutoff members from canceling times 2 days in advance at 12:00pm.
 *        6/06/13   Add the member's name at the end of the notes when they add or change the tee time notes (so staff can determine which member entered the notes).
 *        5/30/13   Philadelphia Cricket Club Recip Site (philcricketrecip) - copy all email notifications to the Pro for selected course.
 *        5/16/13   Oak Hill CC (oakhillcc) - send email to golf chair whenever someone cancels a tee time.
 *        5/03/13   Minikahda - add custom to remove all MOTs except carts and caddies on weekends and holidays.
 *        4/25/13   Expand the Guest Quota error message to include the number of guests allowed and the 'per' value (request from Interlachen).
 *        4/03/13   Updated restrictByOrig custom to fix an issue where a member couldn't cancel a tee time if they were the last player remaining, but had been added by another member.
 *        3/25/13   New Jersey PGA (pganj) - Players 2-4 will now default to "Guest" with a MoT of "CRT" (case 2231).
 *        3/06/13   Change the message prompt when a tee time is busy and an alternate is available.  Do use 'busy' as that is what the message says when there are no others avail.
 *        3/03/13   Desert Mountain (desertmountain) - Added to restrictByOrig custom (case 2236).
 *        2/21/13   Oakmont CC (oakmont) - Commented out old days in advance and member/guest ratio checks.  Will now be handled by custom in verifyCustom.checkCustoms1 (case 2223).
 *        2/20/13   Oakmont CC (oakmont) - Commented out checkOakmontGuestQuota call as it has been replaced by a new custom in verifyCustom.checkCustoms1 (case 1364).
 *        2/15/13   Call checkInUse instead of checkInUseN when in json mode.  No need to check for next available as the tee time has already been assigned.
 *        2/14/13   Clear the tee time from the user's session after updating it and clearing the in_use flag.
 *        2/05/13   Change the error msg when a mobile user does not enter a guest name when guest tracking is used.
 *        1/21/13   Rehoboth Beach CC (rehobothbeachcc) - Added custom to send the caddie master an email if tee times containing "CAD" or "CFC" MoTs are booked/modified/canceled (case 2217).
 *        1/03/13   Echo Lake CC (echolakecc) - Added call to determine custom twosome times (case 2204).
 *       12/27/12   Desert Mountain - do not allow Family Guest Play mship types to access any tee times.
 *       11/27/12   Tweak iframe resize code
 *       11/08/12   Add processing for new event option (memedit) to allow members to access event times once they have been moved to the tee sheet. 
 *       10/30/12   Fixed an issue where restriction verbiage was overwriting the existing verbiage for the restriction instead of appending to it.
 *       10/11/12   Added userg1-5 values to parme population code for sending emails to tracked guests that are present.
 *        8/15/12   Olympic Club (olyclub) - Added to restrictByOrig custom (case ?).
 *        7/17/12   Baltimore CC (baltimore) - Added custom to send the caddie master an email if tee times containing "CAD" MoTs are booked/modified/canceled (case 2158).
 *        6/29/12   Monterey Peninsula CC (mpccpb) - Added custom to allow members to enter tee times that are full, but contain at least one "Need A Player" guest.
 *        6/27/12   North Hills CC (northhills) - Adjusted advance times custom to use 7 days in advance instead of 6 when determining if the custom is used on holidays.
 *        6/13/12   Wayzata CC (wayzata) - Updated custom to start applying on 6/1 instead of 6/21 and to only run through 1:30pm for all MoTs (case 1855).
 *        5/29/12   Minikahda Club (minikahda) - Commented out guest times custom.  Handled in verifyCustom now.
 *        5/10/12   Huntingdon Valley CC (huntingdonvalleycc) - Updated verifySlot.checkHuntingdonValley() method calls to pass the course name as well (case 2139).
 *        4/30/12   Wisconsin Club (brynwoodcc) - Updated custom to apply from 5/12-9/22 instead of 5/1-9/30 (case 2115).
 *        4/30/12   Eagle Creek CC (eaglecreek) - Updated custom to catch cheaters to get member's days in advance parms instead of using hard values.
 *        4/23/12   Castle Pines CC (castlepines) - Added custom message for when X's are present.
 *        4/18/12   Fixed typo in restrictByOrig custom and updated a number of locations where "equals(user)" was being used to use "equalsIgnoreCase(user)" instead.
 *        4/13/12   Wayzata CC (wayzata) - Updated custom to only hide the 'WLK' MOT if it's prior to 1:30pm (case 1855).
 *        3/29/12   Wisconsin Club (brynwoodcc) - Added custom to hide any modes of transportation aside from 'GC', 'CAD', 'C/C', 'FC' (case 2115).
 *        3/29/12   Huntingdon Valley CC (huntingdonvalleycc) - Added call to determine custom twosome times (case 2139).
 *        3/29/12   Oakmont CC (oakmont) - Updated checkOakmontGuestQuota custom call to include May as well. (case 1364).
 *        3/27/12   Huntingdon Valley CC (huntingdonvalleycc) - Added custom to default all player slots to 9 holes on the "Centennial Nine" course (case 2120).
 *        3/22/12   Bay Club at Mattapoisett (bayclubmatt) - Added to restrictByOrig custom (case 2138).
 *        3/13/12   Display correct notification if unaccompanied guests are not allowed.  Problem existed on both old and new skin.
 *        3/09/12   Mobile - add a couple of blank lines at bottom of page to allow for the IOS Nav Bar on iPhones.
 *        3/08/12   Reflection Ridge GC (reflectionridgegolf) - Removed from restrictByOrig custom for time being (case 2131).
 *        3/08/12   Congressional - update customs to reflect new course names.
 *        3/08/12   Reflection Ridge GC (reflectionridgegolf) - Added to restrictByOrig custom (case 2131).
 *        3/07/12   Removed Gallery Golf error log message from 2009 to reduce clutter since we don't believe they've contacted us regarding the problem since then.
 *        2/28/12   Moselem Springs CC (moselemsprings) - Updated method calls to accommodate their custom double tee setup. No crossover times, but crossover gets set as crossover if back-side time is booked earlier, and vice-versa.
 *        2/15/12   Olympic Club (olyclub) - Added a custom to prevent the system from looking up alternate times when a conflict occurs, due to this placing members within a custom restriction (case 2118).
 *        1/31/12   Wildcat Run G&CC (wildcatruncc) - Added custom to use pre-checkin for times booked after 4:30pm ET 1 day in advance (case 2111).
 *        1/24/12   Rolling Hills GC - SA (rollinghillsgc) - Display "HDCP" in place of "Trans" as the header for the MoT column.
 *        1/17/12   In guest processing in verify, clear the userg values if no guests in the tee time.  This is done in case there had been one or more
 *                  guests in the tee time, and they were then removed.
 *        1/16/12   Updated help note regarding adding guests to no longer indicate that members need to type a space, since it's automatically plugged in for them already.
 *        1/16/12   Moved more slot notification text into jquery-foreTeeSlot -- still more to do when time allows
 *        1/13/12   Changed method of selecting elements to be submitted by jquery-foreTeesSlot
 *        1/11/12   More changes for new skin, begin implementing json response in verify slot
 *        1/10/12   Claremont CC (claremontcc) - Added to restrictByOrig custom (case 2099).
 *        1/10/12   Eagle Creek CC (eaglecreek) - Fixed a couple spots where the custom to catch cheaters didn't get updated from 3 days in advance to 2.
 *        1/07/12   Updates to tee time in_use for new skin; Dependent on Memeber_slot, Common_slot, verifySlot, parmSlotPage
 *        1/03/12   Fix javascript error in old skin, introduced with new skin changes.
 *        1/03/12   Desert Highlands GC (deserthighlands) - Added to restrictByOrig custom (case 2096).
 *       01/02/12   Begin implementing changes for new skin
 *       12/16/11   Eagle Creek CC (eaglecreek) - Updated custom to catch cheaters so it's active for times booked 2 days in advance instead of 3 days.
 *       12/08/11   Olympic Club (olyclub) - Added custom to remove the trans mode "Caddie" (CAD) from being selectable in tee times (used for events only) (case 2091).
 *       12/08/11   Indian Ridge CC (indianridgecc) - Added to restrictByOrig custom (case 2075).
 *       12/06/11   Long Cove Club (longcove) - Updated custom to auto-populate x's so that it no longer skips Sunday night to Tuesday morning (case 1375).
 *       12/02/11   Dove Canyon Club (dovecanyonclub) - Added to restrictByOrig custom (case 2088).
 *       12/01/11   Olympic Club (olyclub) - Do not display the "Event Guest" guest type in member side tee times.
 *       11/09/11   Piedmont Driving Club (piedmont) - Added an additional custom message to be displayed (case 1386).
 *       11/09/11   Piedmont Driving Club (piedmont) - Moved location of custom, and removed old custom that was always hiding the "CFC" trans mode from view (case 2064).
 *       11/07/11   The Plantation GC (theplantationgc) - Set default MoT to 'CRT' for all guest types.
 *       11/07/11   Piedmont Driving Club (piedmont) - Added custom to only display the "CFC" trans mode from 12:10pm - 2:30pm during DST, and 12:10pm - 1:30pm outside of DST (case 2064).
 *       11/07/11   Tavistock CC (tavistockcc) - Updated mode of trans hiding custom to only apply between 4/1 - 10/31 each year (case 1653).
 *       10/28/11   Updated restrictByOrig custom to fix some bugs when a member accesses a tee time where they are responsible for all the players in the tee time, but aren't a part of it, and aren't the orig_by.
 *       10/26/11   Long Cove Club (longcove) - Removed their club from the custom to catch cheaters entering tee times earlier.
 *       10/25/11   Wildcat Run CC (wildcatruncc) - Set default MoT to 'CCT' for all guest types.
 *       10/14/11   Yellowstone CC (yellowstonecc) - Added to restrictByOrig custom (case 2053).
 *       10/14/11   Wildcat Run CC (wildcatruncc) - Added to restrictByOrig custom (case 2052).
 *       10/04/11   Belfair GC (belfair) - Added to restrictByOrig custom (case 2047).
 *        9/29/11   Monterey Peninsula CC (mpccpb) - Set default MoT to 'WLK' for all guest types.
 *        9/27/11   Indian Ridge CC (indianridgecc) - Set default MoT to 'GC' for all guest types.
 *        9/23/11   Los Altos G&CC (lagcc) - Updated custom to not allow Tuesday morning tee times between 8:00am and 9:30am to be cancelled on the day of (case 1934).
 *        9/21/11   Chnage the error message when a member tries to cancel a tee time they are not part of - ended in a preposition.
 *        9/13/11   Olympic Club (olyclub) - Updated custom to determine time at which "Twilight Gst" guest type should be displayed based on Daylight Savings time and whether it's before or after 9/1.
 *        9/07/11   Naperville CC (napervillecc) - Prevent members from cancelling times booked as advance guest times once they're within 7 days in advance (case 2009).
 *        8/17/11   Cherry Valley CC (cherryvalleycc) - Added check for custom 2-some times (case 2018).
 *        7/12/11   Woodway CC (woodway) - Updated 2-some times custom to pass fb to the custom method as well (case 1053).
 *        7/11/11   Olympic Club (olyclub) - Set default MoT to 'WLK' for all guest types.
 *        7/05/11   Congressional CC (congressional) - Updated custom to run off indReal instead of ind, since ind gets set to 999, etc, when member comes in via the calendar/teelist page.
 *        7/01/11   Misquamicut Club (misquamicut) - Updated 2-some custom to call verifyCustom.checkMisquamicut2someTimes() instead of using local code (case 1996).
 *        6/27/11   Minnetonka CC (minnetonkacc) - Added custom for 3-some times Wed/Thurs (case 1989).
 *        6/21/11   PGA Golf Management UNL (pgmunl) - Only allow students to add and remove themselves from a tee time (case 1994).
 *        6/14/11   Misquamicut Club (misquamicut) - Added custom for 2-some times Sat/Sun/Holiday from 8:00am-8:30am (case 1996).
 *        6/13/11   Olympic Club - do not allow a guest type to be selected, and make it red, if it is fully restricted during the selected tee time.
 *                                 Also, do not show the 9-hole option.
 *        6/12/11   Olympic Club - only MHGP mship types have access to "MHGP w/guest" guest type..
 *        6/09/11   Olympic Club - do not allow a Jr Golf member without an adult.
 *        5/31/11   Olympic Club - only list the guest types that are allowed for the specified course.
 *        5/27/11   Olympic Club - must be at least 2 players per tee time.
 *        5/25/11   Cape Cod National (capecodnational) - Set default mode of trans to 'C' for all guest types.
 *        5/24/11   Scioto CC (sciotocc) - Commented out custom to restrict guest times being booked in advance (case 1713).
 *        5/20/11   Hawks Landing - use the 'time' variable in place of slotParms.time in custom that checks the current time vs the tee time.
 *        5/19/11   Royal Montreal GC (rmgc) - set default mode of trans to 'CRT' for all guest types.
 *        5/12/11   Sonnenalp (sonnenalp) - Added custom for 3-some times (case 1978).
 *        5/12/11   Royal Montreal GC (rmgc) - Added custom for 3-some times based on course, date (even/odd), and weekday/weekend.
 *        5/11/11   Turner Hill (turnerhill) - commented out custom to not allow members to cancel a tee time on the day of (case 1836).
 *        4/21/11   Hawks Landing GC (hawkslandinggolfclub) - Do not allow members to cancel tee times, and display a custom message regarding the no-show policy, if day-of and within 3 hrs of the tee time (case 1969).
 *        4/19/11   Crystal Lake CC (clcountryclub) - Only allow the originator of a tee time to remove people other than themselves or cancel the tee time (case 1971).
 *        4/14/11   Congressional CC (congressional) - Added custom prompt to replace standard promptOtherTime prompt when more than 9 days in advance.  Members should
 *                  not be given the option of entering another time, since we're using a custom to give them early access.
 *        4/06/11   Talbot CC (talbotcc) - Added 'Fam Wkend' and 'Fam Wkday' to be removed as part of the custom (case 1940).
 *        3/25/11   Talbot CC (talbotcc) - If Mon-Fri remove 'Wkend Guest' gtype, if Sat/Sun remove 'Wkday Guest' gtype (case 1940).
 *        3/21/11   Changed teecurr2 update call to set the last_mod_date to now
 *        3/14/11   Congressional (congressional) - Updated custom for guest times to also apply to Jones Course
 *        3/09/11   Governors Club (governorsclub) - Added to restrictByOrig custom (case ####).
 *        2/28/11   Pelican Marsh GC (pmarshgc) - Re-enabled custom to prevent players from removing others that have joined their times (case 1786).
 *        2/25/11   Rogue Valley CC (roguevalley) - Removed custom to prevent one/two-somes from booking in certain times
 *        2/21/11   Black Diamond Ranch (blackdiamondranch) - set default mode of trans to 'TT' if guest type equals "Turn Time"
 *        2/09/11   Default MoT will now be checked to verify they are valid MoTs for the current course before being added dynamically to the MoT selection drop-down.
 *        2/09/11   Tanoan CC (tanoan) - Set default MoT to 'TC' for all guest types.
 *        2/02/11   Los Altos G&CC (lagcc) - Do not allow Thursday morning tee times between 7:30am and 9:30am to be cancelled on the day of (case 1934).
 *        1/19/11   Mediterra - Commented out custom to remove the "Walking" mode of trans (Case 1263).
 *        1/19/11   Dataw Island Club (dataw) - Fixed default MoT custom to default certain guest types to "TF" instead of "TR".
 *        1/18/11   Golf Academy of America (non-class sites) - Adjustment to case to not allow members to cancel a tee time unless they are the only remaining player (case 1900).
 *        1/13/11   Oregon Golf Club (oregongolfclub) - Only allow the originator of a tee time to remove people other than themselves or cancel the tee time.
 *        1/12/11   Remove 3-some custom for Meadow Club (case 1761).
 *       12/17/10   Dataw Island Club (dataw) - Set default MoT depending on guest type.
 *       12/08/10   Long Cove Club (longcove) - Updated custom to not auto-populate x's on cancelled times 14 days in advance between 7:00am-7:30am (case 1375).
 *       12/08/10   Golf Academy of America (non-class sites) - Members are only allowed to add/remove themselves from tee times.  Member/guest/x selection lists will all be hidden (case 1900).
 *       12/08/10   Added skipMembers boolean to allow for easy hiding of the member/partner selection list as well as the alphabet table.  Updated code so that
 *                  table columns to the right of the player slots will not be included if all elements (members, guests, x's) are being hidden (centers player slots on screen).
 *       12/01/10   Long Cove - put in check to trace cheaters that are getting in early for tee times.
 *       10/20/10   Omaha CC (omahacc) - Set default MoT to 'GC' for all guest types.
 *       10/20/10   Populate new parmEmail fields
 *       10/15/10   Trophy Club CC (trophyclubcc) - Change from originator custom to restrictByOrig custom (case 1545).
 *       10/07/10   custom_int field will now be cleared when a tee time is cancelled
 *        9/28/10   Willow Ridge CC (willowridgecc) - Hide the 'WkEnd GST' guest type from members Mon-Thurs, and the 'WkDay GST' guest type Fri-Sun (case 1863).
 *        9/28/10   Shady Canyon GC (shadycanyongolfclub) - Hide the 'Fri-Sun/Holiday GST' guest type from members Mon-Thurs, and the 'Tues-Thurs GST' guest type Fri-Sun (case 1874).
 *        9/23/10   Hiwan CC (hiwan) - Added to restrictByOrig custom (case 1891).
 *        9/23/10   Adjustment to mobile-side guest tracking preparation to accomodate for 'TBA' guest entries
 *        9/09/10   Estancia Club (estanciaclub) - Set default MoT to 'GC' for all guest types.
 *        8/27/10   Shady Canyon GC (shadycanyongolfclub) - If notes were added/modified/removed in a submitted tee time, send custom notification email to staff members (case 1881).
 *        8/10/10   Changes to support for incoming encrypted tee time info
 *        8/02/10   Fort Collins CC (fortcollins) - Updated customs to include Fox Hill CC
 *        7/26/10   Tweak to restrictByOrig custom to fix members not being able to erase/edit themselves if they were added to a time by someone else.
 *        7/12/10   Burloaks CC (burloaks) - Set default MoT for 'Corp' guest type to 'Car'
 *        7/06/10   Silver Creek CC (silvercreekcountryclub) - Remove restrictByOrig custom (case 1841).
 *        7/06/10   Wayzata CC (wayzata) - Only display 'CAD' and 'CRT' tmodes between 6/1 - 8/31 on Wed/Fri/Sat/Sun before 3pm
 *        6/18/10   Tavistock CC (tavistockcc) - Remove tmodes 'CRY' and 'TRL' for members every day prior to 1:59pm (case 1653).
 *        5/07/10   Aliso Viejo CC (alisoviejo) - Added to restrictByOrig custom (case 1850).
 *        5/27/10   Change the order that names are listed - sort by the last name only (without any extensions) so families are grouped together.
 *        5/21/10   Turner Hill (turnerhill) - set default mode of trans to 'CRT' for all guest types.
 *        5/19/10   Changes to mobile check that weeds out player names with pipes.
 *        5/18/10   Updated restrictByorig custom to allow tee time to be cancelled if all players are empty or were added by the current user.
 *        5/14/10   Imperial GC (imperialgc) - Only apply MoT custom until 5/31/2010 - they are removing this restriction (case 1287).
 *        5/11/10   Turner Hill - do not allow members to cancel a tee time on the day of (case 1836).
 *        5/11/10   Cherry Creek CC (cherrycreek) - include 'Employee' guset type in default mode of trans custom
 *        5/07/10   Silver Creek CC (silvercreekcountryclub) - Use orig values to only allow players access themselves, or other members of a tee time that they added themselves.
 *        5/07/10   TPC Boston (tpcboston) - Use orig values to only allow players access themselves, or other members of a tee time that they added themselves.
 *        5/07/10   Added orig tracking.  Username of the player that booked each person in this tee time will now be stored in orig1-orig5
 *        5/06/10   Lake Forest CC - do not allow WLK on weekends until after 10:00 AM (case 1831).
 *        5/04/10   Cherry Creek CC (cherrycreek) - set default mode of trans to 'CI' for all guest types.
 *        4/28/10   Eagle Creek CC (eaglecreek) - Updated custom to catch cheaters to allow bookings up to 3 days in adv instead of 2
 *        4/22/10   Brae Burn CC - Only allow the originator of a tee time to remove people other than themselves or cancel the tee time (case 1826).
 *        4/22/10   Brae Burn CC (braeburncc) - set default mode of trans to 'NAP' for all guest types.
 *        4/22/10   Tartan Fields (tartanfields) - set default mode of trans to 'CRT' for all guest types.
 *        4/21/10   The Club At Nevillewood (theclubatnevillewood) - set default mode of trans to 'C' for all guest types.
 *        4/20/10   Ramsey - process 3-some only times (case 1816).
 *        4/18/10   Added guest tracking processing for mobile
 *        4/15/10   Peninsula Club - display a custom message next to the Walk mode of trans in legend (case 1820).
 *        4/15/10   Do not display pro-only modes of trans in the legend (case 1820).
 *        4/15/10   Silver Creek CC (silvercreekcountryclub) - set default mode of trans to 'GR' for all guest types.
 *        4/10/10   Pelican Marsh - comment out custom we just added until they want it (case 1786).
 *        4/09/10   Added guest tracking processing
 *        4/09/10   If mobile user then check to see if each player name contains a pipe (delim separating the tmode) and if so repopulate player name & their cw
 *        4/01/10   Longue Vue Club - Added call to verifyCustom for 2-some check for Case# 1798.
 *        3/30/10   Pelican Marsh - if tee time is within 48 hours of now, do not allow members to remove players unless they added them (case 1786).
 *        3/25/10   Interlachen - change 5-some guest restriction from no guests to 2 guests (case 1716).
 *        3/25/10   Interlachen - remove custom for gift packs (case 1369).
 *        3/25/10   Woodway CC (woodway) - Uncommented 2-some time custom to be used again this season (case 1053).
 *        3/24/10   Timarron - put in checks to catch cheaters that are getting in early for tee times.
 *        3/23/10   Central Washington Chapter PGA (cwcpga) - Display ghin numbers in name list
 *        2/02/10   Trim the notes in verify
 *        1/25/10   Updated moveguest Javascript function to handle the new use_guestdb value being passed to it.
 *        1/20/10   Pinehurst CC (pinehurstcountryclub) - Only allow the originator of a tee time to remove people other than themselves or cancel the tee time.
 *        1/08/10   Meadow Club - process 3-some only times (case 1761).
 *       12/29/09   Indian Hills CC (indianhillscc) - Always use twoSomeTimes for new 'Hitting Room' course
 *       12/04/09   Champion Hills - set default MOT for guests.
 *       12/02/09   Call to alphaTable.displayPartnerList added to print the partner list, outdated code removed
 *       11/17/09   Add support for Mobile users.
 *       11/16/09   Royal Oaks Dallas - do not show the Weekday guest type on Fri, Sat or Sundays (case 1688).
 *       11/16/09   If course only has one mode of trans, then use that as the member's default (case 1744).
 *       11/06/09   Eagle Creek - put in checks to catch cheaters that are getting in early for tee times.
 *       10/22/09   Round Hill CC (roundhill) - Update default MoT for guest types from 'RCH' to 'RHC'
 *       10/19/09   The Lakes - do not allow members to book a time with guests if 7 days in advance and before 7:30 AM (case 1736).
 *       10/14/09   Imperial GC (imperialgc) - Only apply MoT custom from 1/1 to 5/31 each year (case 1287).
 *       10/08/09   Ocean Reef CC (oceanreef) - set default mode of trans to 'CT' for all guest types.
 *       10/04/09   Added activity isolation to the buddy list
 *        9/24/09   Tualatin - force members to specify their modes of trans (case 1726).
 *        9/10/09   Woodlands CC - set default mode of trans to 'CRT' for all guest types (case 1722).
 *        9/09/09   Black Diamond Ranch (blackdiamondranch) - set default mode of trans to 'CCT' for all guest types
 *        9/03/09   Beverly - move custom guest quota to verifyCustom so proshop side will get it too.
 *        9/03/09   Scioto CC - do not allow guests more than 4 or 5 days in advance, display a custom member notice for this, and do not allow
 *                              members to replace members with guests when tee time was booked in non-guest window (case 1713).
 *        9/01/09   The Lakes - do not allow members to book a time with guests if more than 5 days in advance and before 11 AM (case 1691).
 *        9/01/09   Added check for Desert Forest 2-some times (case 1694).
 *        8/28/09   MountainGate CC (mtngatecc) - set default mode of trans to 'CF' for all guest types.
 *        8/27/09   Interlachen - change 5-some guest restriction to only apply between Mem Day and Labor Day (case 1716).
 *        8/26/09   Round Hill CC (roundhill) - set default mode of trans to 'RCH' for all guest types (case 1714).
 *        8/19/09   Westmoor CC (westmoor) - set default mode of trans to 'R' for all guest types.
 *        8/18/09   Gallery Golf - log rejection in verify if tee time taken so we can determine if multiple  members are getting
 *                                 the same tee time.
 *        8/17/09   Los Coyotes (loscoyotes) - change custom so player slots aren't grayed out if they contain the current member's name
 *        8/13/09   Change to populate slotParms.teecurr_id during verify
 *        8/11/09   Minnetonka CC (minnetonkacc) - set default mode of trans to 'WLK' for all guest types (case 1708).
 *        8/06/09   Timarron CC (timarroncc) - Additional changes to handle x's and guests
 *        7/30/09   Timarron CC (timarroncc) - Change custom to only allow players to remove themselves or players they personally added from tee times (case 1596)
 *        7/30/09   Add focus setting for moveguest js method to improve efficiency
 *        7/28/09   Sawgrass CC (sawgrass) - Set default mode of trans to 'CRT' for all guest types
 *        7/22/09   Gallery Golf - put in checks to catch cheaters that are getting in early for tee times.
 *        7/21/09   Timarron (timarroncc) - Change case to remove 24 hr check and apply former 'less than 24 hr' rule to ANY time instead (case 1596).
 *        7/10/09   Charlotte CC (charlottecc) - Added custom message to successful booking splash page for groups with 2 or more guests (case 1700).
 *        7/01/09   Woodway - remove 2-some custom.
 *        6/25/09   Bentwater CC (bentwaterclub) - remove lottery history when member removed from tee time (case 1698).
 *        6/25/09   Forest Highlands (foresthighlands) - Change 'Employee' guest type to default to 'EMP' instead of 'CMP'
 *        6/24/09   Mid Pacific (midpacific) - set default mode of trans to 'GC' for all guest types.
 *        6/19/09   Silver Lake CC (silverlakecc) - Remove 'WLK' tmode if not Tues/Thurs and before 3pm (case 1664).
 *        6/05/09   Interlachen - only allow members with a subtype of "Member guest Pass" to use the guest type
 *                                of Guest-Centennial (case 1686).
 *        5/20/09   Lakewood CC - set default mode of trans to 'CRT' for all guest types (sup req 265).
 *        5/20/09   CC of the Rockies - set default mode of trans to 'GCF' for all guest types (sup req 264).
 *        5/20/09   Forest Highlands - set default mode of trans to 'CMP' for certain guest types (sup req 262).
 *        5/18/09   Oakland Hills CC - Add custom "guest bag tag" checkbox to member and pro tee times
 *        5/20/09   Gallery Golf - put in checks to catch cheaters that are getting in early for tee times.
 *        5/07/09   Save the user's username in slotParms for custom processing.
 *        5/06/09   North Hills - change guest custom to use the configured days in advance and time values so we don't have to
 *                                keep updating it every year.
 *        5/05/09   Medinah CC - adjust custom for new 10 minute intervals on No 3 and days in advance changes (case 1673).
 *        4/29/09   Long Cove - do not fill cancelled tee time with X's if between Sunday 6:30 PM and Tues 7:00 AM.
 *        4/24/09   Add call to verifyCustom.checkCustomsGst after guests are assigned for custom guest restrictions.
 *                  This was done as part of a TPC unaccompanied guest custom (case 1663).
 *        4/17/09   Castle Pines - Highlight player's name in lime if their birthday matches the date of one of their booked tee times (case 1607).
 *        4/09/09   Blue Bell CC - set default mode of trans to 'CAR' for all guest types.
 *        4/08/09   Los Coyotes - do not allow members to make or change a tee time unless they are part of it (case 1647).
 *        4/06/09   Southview CC - set default mode of trans to 'WA' for all guest types.
 *        3/27/09   Woodway CC - Custom guest restriction - mship Restricted Golf may not have guests on Fri/Sat/Sun/Holidays (case 1510).
 *        3/20/09   The Oaks Club - Utilize pre-checkin for tomorrow bookings if it's after 6pm today (case 1589).
 *        3/19/09   Only allow 3-somes during certain times for Chartwell GCC (case 1554).
 *        3/19/09   TPC Sugarloaf - set default mode of trans to 'GCT' for all guest types.
 *        3/10/09   Admirals Cove - put in checks to catch cheaters that are getting in early for tee times.
 *        3/06/09   Imperial GC - Change pre-checkin from 2pm to 1 PM - Case #1327
 *        2/13/09   Tweaks to Dining Request prompt display
 *        2/12/09   Pelicans Nest & Eagle Creek - temp custom to try to catch cheaters - make history entry when setting tee time in use.
 *        1/08/09   Timarron - do not allow members to remove other members or cancel the tee time within 24 hrs of time (case 1596).
 *       12/22/08   Gulf Harbour GCC - set default mode of trans to 'CRT' for all guest types.
 *       12/18/08   Pelican Marsh GC - set default mode of trans to 'GC' for all guest types.
 *       12/05/08   All TPC clubs - do not allow members to cancel a tee time on the day of (case 1580).
 *       12/01/08   Timarron CC - Only allow the originator to cancel or erase players other than themselves (case #1591).
 *       11/21/08   Desert Highlands - remove option to Only allow the originator to cancel or erase players other than themselves (case 1582).
 *       11/21/08   Rivercrest - remove custom 3-some restrictions - similar to 2-somes (case 1492).
 *       11/12/08   Do not plug the user's name into the next available slot if not the first time here (case 1583).
 *       11/10/08   Desert Highlands - Only allow the originator to cancel or erase players other than themselves (case 1582).
 *       10/16/08   Add call verifyCustom.checkCustoms1 to check for custom restrictions - this will be the NEW
 *                  process for adding customs and should make it much easier.  Only verifyCustom should have to
 *                  be modified for future customs.
 *       10/10/08   Patterson Club - Custom guest restriction for weekend/holiday mornings (case 1470).
 *       10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *       10/03/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *       10/02/08   Gleneagles CC - Only allow the originator to cancel or erase players other than themselves (case #1558).
 *        9/24/08   Trophy Club CC - Only allow the originator to cancel or erase players other than themselves (case #1545).
 *        9/19/08   North Hills - Updated existing custom for time to open tee sheet on weekends
 *        8/26/08   Brookhaven CC - only allow the originator to cancel or erase players (case 1541).
 *        8/21/08   North Ridge - set the default mode of trans to 'CC' for all guest types (case #1535).
 *        8/19/08   Scioto CC - add a custom guest restrictions for days in advance (case 1534).
 *        8/11/08   Stonebridge Ranch - must be at least 2 players per tee time on Dye course (Case 1523) - ON HOLD.
 *        8/05/08   CC Naples - Utilize pre-checkin for tomorrow bookings if it's after 6pm today - Case #1528
 *        7/29/08   Los Coyotes - change to case 1397 - do not allow any member to change any player in existing tee times.
 *        7/08/08   Brooklawn - change the starting time on their custom 3-some restriction.
 *        7/07/08   Admirals Cove - set the default mode of trans to 'CF' for all guest types (case #1514).
 *        6/26/08   Added javascript to dynamically add/remove Pro Only tmodes to wc selection drop down boxes
 *        6/26/08   Baltusrol - add custom guest quota - max of 3 per member outstanding (case 1455).
 *        6/25/08   Wilmington - remove custom that removed CYO mode of trans during specified days and times.
 *        6/23/08   Changes to ProOnly tmode checking
 *        6/19/08   Minikahda - make some adjustments to custom guest restriction (see Bob's custom doc).
 *        6/17/08   Added verification processing for Pro-Only modes of transportation
 *        6/17/08   Minikahda - change custom guest restriction to process other standard guest restrictions per pro's request.
 *        6/16/08   Chartwell GC - set the default mode of trans to 'CAR' for all guest types (case #1504).
 *        6/13/08   Minikahda - change their custom guest restriction to not include weekends and holidays before 10 AM (case 1027).
 *        6/11/08   Sonnenalp - do not allow members to use ICP and FCP modes of trans unless it is their default (case 1452).
 *        6/11/08   Belle Meade - custom restriction for Primary Females on Sundays (case 1496).
 *        6/07/08   Trophy Club - set the default mode of trans to 'GC' for all guest types (case #1489).
 *        6/05/08   Rivercrest - add custom 3-some restrictions - similar to 2-somes (case 1492).
 *        6/03/08   St. Clair CC - always default the Terrace course to 9 holes (case 1476).
 *        5/16/08   Tamarack - remove lottery history when member removed from tee time (case 1479).
 *        5/14/08   Long Cove - remove custom that prevented members from removing player1.
 *        5/09/08   North Hills - adjust time of day in custom for weekends.
 *        5/06/08   Bentwater Club - remove custom added on 5/40/08 (case 1465).
 *        5/05/08   Beverly GC - add custom guest quota for Wed & Fri (case 1449).
 *        5/05/08   Comment out customs for mship types (custom_disp) - replace by new tflag feature.
 *        5/04/08   Bentwater Club - do not allow members to cancel a tee time or erase player1 (case 1465).
 *        4/24/08   Add member tag feature (tflag) where we flag certain members on pro tee sheet (case 1357).
 *        4/17/08   Sonnenalp - Start (Case# 1452)
 *        4/15/08   Wellesley - add Limited mship type when adding an special char to player name.
 *        4/11/08   Oak Hill CC - tee times must contain at least one guest in addition to the member(s) - case 1421.
 *        4/11/08   Baltusrol - tee times must contain at least one guest or an X in addition to the member(s) - case 1442.
 *        4/10/08   Aliso Viejo - Always hide the notes Case# 1409
 *        4/10/08   Dorset Field Club - Added call to verifyCustom for 2-some check for Case# 1440
 *        4/10/08   Bald Peak - Add check for twosome times  (Case #1428)
 *        4/10/08   Mayfield SR - Added call to verifyCustom for 2-some check for Case# 1424
 *        4/02/08   MN Valley - set the default mode of trans to 'WK' for all guest types (case #1439).
 *        4/02/08   Inverness Club - correct custom for addvance guest times to adjust for 'days in advance'
 *                  settings now at 365.
 *        3/18/08   Los Coyotes - change the minimum numbers of players from 3 to 2 for case. (case 1397)
 *        3/14/08   Los Coyotes - add some various restrictions when members editing tee times (case 1397).
 *        3/14/08   Long Cove - Remove the custom that allowed members to be part of up to 7 tee times.  When a member
 *                              cancels a tee time, put in 4 - X's.  Do not allow members to change player1.
 *        3/13/08   Marbella - remove the change for case 1380 where we were limiting the mode of trans options.
 *                             It was not what they wanted.  It would be difficult to do what they want.
 *        2/22/08   Long Cove - undo the changes made on 2/21 until the board makes a final decision.
 *        2/21/08   Long Cove - remove custom that does not allow members to cancel a tee time.  Instead, insert Golf Shop and
 *                              3 X's when a member cancels a tee time (case 1375).
 *        2/18/08   Interlachen - add a 'Gift pack' option for members to specify that their guests be given a gift (case 1369).
 *        2/18/08   Allow for possibility that the returned alternate tee time might be on a different F/B tee (case 1396).
 *        2/10/08   Marbella - do not allow members to change their mode of trans (case 1380).
 *        2/08/08   Valley Country Club - Remove custom to allow ladies to access Fridays 2 weeks in advance.  (Case #1388)
 *        1/24/08   Oakmont - add new guest quota - members can book up to 10 guest times in Feb, Mar, & Apr (case # 1364).
 *        1/07/08   Jonathan's Landing - Removed tmode custom (Case# 1330)
 *       12/12/07   Reject the request from Member_sheet of consecutive is less than one.
 *       12/04/07   Mediterra - Utilize pre-checkin for tomorrow bookings if it's after 5:30pm today - Case #1309
 *       12/04/07   Berkeley Hall - Remove the trans mode of 'REC' and 'CMP' (Case 1341)
 *       12/03/07   Imperial GC - Utilize pre-checkin for tomorrow bookings if it's after 2pm today - Case #1327
 *       12/03/07   Jonathan's Landing then remove certain options  (Case# 1330)
 *       11/29/07   Jonathan's Landing - force the mode of trans to 'CRT' for all guest types (case #1334).
 *       11/26/07   Eagle Creek - Add check for Social mships and limit to 6 rounds during season and must be with a Golf mship (Case #1284)
 *       11/07/07   Imperial Golf Club - Remove trans mode of Walk & Pull Cart everyday until 11:30AM EST (Case #1287)
 *       11/07/07   Colleton River Club - Do not allow a Dependent w/o an adult  (Case #1291)
 *       11/05/07   Mediterra - force the mode of trans to 'R' for all guest types (case #1315).
 *       10/26/07   Pelican's Nest - Utilize pre-checkin for tomorrow bookings if it's after 5pm today - Case #1296
 *       10/24/07   Add call to new checkMaxOrigBy for enforcing a max number of rounds that can be originated by a member
 *       10/16/07   Red Rocks - force the mode of trans to 'NA' for all guest types (case #1138).
 *       10/16/07   Pinery - force the mode of trans to 'GC' for all guest types (case #1252).
 *       10/15/07   Pinnacle Peak CC - Remove the trans mode of 'NC' (Case 1288)
 *       10/14/07   Put member's mship and mtype in slotParms for checkInUseN so it can check restrictions.
 *       10/07/07   Valley CC - add a date range to the ladies custom (case #1278).
 *       10/03/07   Claremont CC - Add check for twosome times  (Case #1281)
 *        9/25/07   Mediterra - Add call to verifyCustom.checkMediterraSports for checking Sports mship quotas during season
 *        9/27/07   North Hills - Change time from 6AM to 7AM for skipping the 'days in advance' test
 *        9/24/07   Hallbrook CC - send email to caddie master when a caddie is requested (case 1037).
 *        9/21/07   Mediterra - remove the trans mode of 'Walking' (Case 1263)
 *        9/21/07   Make the custom to prompt user with next available tee time standard for all clubs.
 *        9/05/07   Lakewood Ranch (FL) - if new tee time is busy, search for the next available time (Case #1246).
 *        8/30/07   Merion - force caddie (CAD) mode of trans on East course on specified days/times (Case #1236).
 *        8/28/07   Pelicans Nest - if new tee time is busy, search for the next available time (Case #1241).
 *        8/21/07   Merion - don't allow members to cancel tee times within 48 hrs of the tee time (Case #1234).
 *        8/20/07   Modified call to verifySlot.checkMemNotice
 *        8/19/07   Medinah CC - Removed 4:00 PM check for guest restrictions
 *        8/16/07   Los Coyotes - change the days in adv for Secondary Members from to 3 days  (Case #1191)
 *        8/13/07   Greenwich CC - If less then 3 players on specific dates/times then reject  (Case #1123)
 *        8/10/07   Greenwich CC - don't allow Guests in twosomes in more than 48 hrs from tee time (Case #1217)
 *        8/04/07   Valley Country Club - Allow ladies to make times Fridays 2 weeks in advance between 7:30-10:59  (Case #1160)
 *        7/27/07   Oahu CC - Add custom restrictions based on day of week and time for certain mships (Case #1221)
 *        7/27/07   Los Coyotes - make sure there are at least two members or 1 w/ guest for all tee times (Case #1211)
 *        7/17/07   Wilmington - add range privilege indicator to the tee time for display on tee sheet (case #1204).
 *        7/05/07   Olympia Fields CC - must always be more than 1 player in request.
 *        6/29/07   Merrill Hills - check for special mships so they can be marked on pro tee sheet (case #1183).
 *        6/21/07   New Canaan - add 2-some times on weekends and holidays (case 1174).
 *        6/18/07   Catamount Ranch - change max number of advance tee times back to 5, except for Founder members - they are unlimited (case #1124).
 *        6/18/07   Sonnenalp - check for max number of advance tee times (case #1089).
 *        6/14/07   Wellesley - custom to check mship types and flag in teecurr for Proshop_sheet display (case #1167).
 *        6/12/07   Allow for course=-ALL- option - return to course=all if selected.
 *        6/08/07   Congressional - add test for Gold course on weekdays to prevent member access (they are getting in somehow).
 *        6/05/07   Milwaukee - change guest restriction to allow any combination of players as long as there are at least 2 guests.
 *        5/29/07   Sonnenalp - add guest fees to the tee time for display on tee sheet (case #1070).
 *        5/24/07   Catamount Ranch - change max number of advance tee times from 5 to 14 (case #1124).
 *        5/23/07   Change parseNames to check for single names from pro as well as members.
 *        5/22/07   Brooklawn - change the custom for 3 players from 3:00 PM to 2:00 PM (end time).
 *        5/02/07   North Hills - change the custom time in advance value to 6:30 AM.
 *        4/25/07   Minikahda - Allow guest times up to 7 days in advance - 3 days for normal times (case #1027).
 *        4/25/07   Congressional - pass the date for the Course Name Labeling.
 *        4/24/07   Greenwich CC - add 2-some only times (case 1121).
 *        4/20/07   The Country Club - Changed the words member to guest for Member TBD box also set default tmode
 *        4/17/07   North Hills - change the custom time in advance value based on time of year.
 *        4/12/07   The CC - All tee times by members MUST be guest times.  They can have a guest or 'X'. (case #1087).
 *                         - Only allow 6 guests per month and 18 per season, per member (4/01 - 10/31).
 *        4/11/07   Congressional - allow members to access specific times on the Open Course for guest times (case #1075).
 *                                - Only allow 4 of these advance guest times per year per family (mnum).
 *                                - Send an email to pro if advance guest time cancelled within 9 days of tee time.
 *        4/10/07   Congressional - add 2-some only times (case 1060).
 *        4/10/07   Congressional - custom restriction - Dependent Non-Certified mtype must be with Adult (case #1059).
 *        4/10/07   Congressional - custom guest restriction - only 1 guest per 'Junior A' mship type (case #1048).
 *        4/09/07   Congressional - custom guest restriction - Cert Jr Guest must follow a Certified Dependent (case #1058).
 *        4/06/07   Do not include members that are inactive (new inact flag in member2b).
 *        4/04/07   The CC - add 2-some only times (case 1085).
 *        3/31/07   Added custom for Inverness Club - If weekday && guest part of tee time, can book through Oct. 31st (vase #1083).
 *        3/28/07   Oakmont - change Wed & Fri guest restriction to allow 2 guests & 2 members.
 *        3/26/07   Oakmont - change weekday guest restriction to allow 2 guests & 2 members.
 *        3/25/07   Add temp custom for Long Cove for thier Heritage Classic event (Case# 00001038)
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        3/15/07   If tee time is busy, return to tee sheet instead of calendar page.
 *        2/27/07   Add new 'Member Notice' processing - display pro-defined message if found.
 *        2/20/07   Merion - if more than 7 days in adv, and a w/e, check for more than 4 tee times.
 *        2/15/07   Fort Collins/Greeley - custom for guest types - only display their own.
 *        2/15/07   Awbrey Glen - Juniors must be with an adult at all times, Juniors Over 12
 *                                must be accompanied by an adult before Noon.
 *        2/09/07   Wilmington CC - check for max of 12 guests total during specified times.
 *                  Also, do not show CYO mode of trans during specified times.
 *        2/07/07   Fort Collins/Greeley - custom restrictions and default mode of trans.
 *        1/04/07   Peninsula Club - Juniors must be with an adult at all times.
 *       12/20/06   Long Cove - do not allow members to cancel a tee time - they use a wait list.
 *       12/20/06   El Niguel - Juniors must be with an adult on the back tees on Sundays.
 *       11/17/06   Riverside G&CC - check for max of 12 guests total on Sunday before noon.
 *       10/24/06   Columbine - custom to check for a 5-some with any guests (not allowed).
 *        9/14/06   North Hills - change the custom time in advance value based on time of year.
 *        9/07/06   Cherry Hills - do not allow Juniors to include guests w/o a Member or Spouse.
 *        7/26/06   Bearpath - custom restriciton for member types of 'CD plus'.
 *        7/11/06   Cherry Hills - force members to specify their modes of trans.
 *        7/06/06   Interlachen - custom to check for a 5-some with any guests (not allowed).
 *        7/01/06   Medinah - remove check for guest quota on #3 (per Mike Scully).
 *        6/09/06   Wellesley - custom guest restrictions.
 *        5/17/06   Rancho Bernardo - force the mode of trans to 'CCH' for all guest types.
 *        5/09/06   North Hills - change the time of day for days in adv for guest-only times.
 *        5/02/06   North Hills - check time of day and days in adv for guest-only times.
 *        4/25/06   Forest Highlands - do not allow members to use 'X'.
 *        4/25/06   CC at Castle Pines - Juniors must be with an adult at all times.
 *        4/24/06   CC at Castle Pines - force the mode of trans to 'CRT' for all guest types.
 *        4/24/06   Brooklawn - do not allow 2-somes during specific times of the day and days of the year.
 *        4/24/06   Remove check for adding players to a lottery - some clubs want to allow this.
 *        4/20/06   Wee Burn - add 2-some only times.
 *        4/17/06   Apawamis - add 2-some only times.
 *        4/17/06   Hazeltine - add check for consecutive singles or 2-somes.
 *        4/17/06   Medinah - remove ARR processing per Medinah's instructions.
 *        4/14/06   Add a check to see if the user is part of the tee time if it is full. This is because
 *                  some member actually hacked his way into a full tee time and removed some players.
 *                  Also, remove the doGet method and only use doPost.
 *        4/12/06   Oakmont - change limit for days in advance from 7 to 14.
 *        4/11/06   Catamount Ranch - check for max number of advance tee times.
 *        4/10/06   Inverness - do not allow members to cancel a tee time when hotel guests are present.
 *        4/05/06   New Canaan - add 2-some only times.
 *        3/17/06   The Stanwich Club - do not allow CRY mode of trans before 1 PM each day.
 *        3/15/06   New Canaan - do not allow any guest types for 'Special' mship types.
 *        3/15/06   CC of the Rockies - check for max number of advance tee times.
 *        3/15/06   Bearpath - check for guests on weekdays during member-only times.
 *        3/14/06   Oakland Hills - if tee time is 8 days in advance, then no guests and no X's.
 *        3/14/06   Oakland Hills - add a custom restriction for tee time requests more than 8 days in advance.
 *        3/14/06   Oakland Hills - add a custom restriction for member type of Dependent.
 *        3/02/06   Allow a member to cancel a tee time if they originated it (multiple tee time requests).
 *        3/01/06   North Hills - allow a tee time with 2 or more guests beyond the days in adv limits.
 *        2/28/06   Merion - do not list the 'A rate Guest' guest type to 'House' mship members.
 *        2/27/06   Whenever tee time is added or changed, call SystemUtils.updateHist to track it.
 *        2/27/06   Merion - add custom member restriction (checkMerionSched).
 *        2/27/06   Merion - add custom guest restriction (checkMerionGres).
 *        2/24/06   Display the Cancel Tee Time button if the user originated the tee time.
 *        2/23/06   Merion - add custom guest restriction (checkMerionG).
 *        2/15/06   Remove the 'please be patient' message in the doGet method to prevent system hang.
 *        1/18/06   Lakewood - change the 'Guest Types' box title.
 *       12/21/05   Skaneateles - add a custom restriction for member type of Dependent.
 *       12/05/05   Johns Island - change custom rest from at least 3 players to at least 2 players.
 *       12/05/05   Add call to checkRitz method for custom member restrictions for RitzCarlton.
 *       11/08/05   Always check for 'Exception' on the catch from calls to verifySlot.
 *       11/06/05   Cherry Hills - add a custom restriction for member types.
 *       11/03/05   The Lakes - force the mode of trans to 'NON' for all guest types.
 *       11/03/05   Cherry Hills - add a comment block to define the guest types.
 *       10/17/05   The Stanwich Club - Juniors must be with an adult at specified times.
 *       10/17/05   The Stanwich Club - add custom - limit use of 'Carry' mode of trans.
 *       10/12/05   Lakewood - add custom restriction for spouses.
 *       10/07/05   The Stanwich CLub - add 2-some only times.
 *        9/29/05   Johns Island - all tee times between 8:40 and 1:50 must have at least 3 players.
 *        9/15/05   Medinah - do not show guest types that are for one of the other courses.
 *        9/13/05   Piedmont - do not allow WNC mode anytime before 1:30 or 2:30 PM, any day.
 *        7/25/05   Add instructions for adding guests.
 *        7/14/05   Oakmont - change guest restrictions for Wed & Fri per Padge's instructions.
 *        6/27/05   Hartefeld Natl - do not allow tee times with only one player.
 *        6/24/05   North Shore CC - add check for 'guest-only' times (must be at least 2 guests).
 *        6/16/05   Westchester CC - allow members to change weighted lottery times.
 *        6/10/05   Oakmont CC - change the test for guest times within 30 days - allow member to add guest names.
 *        6/02/05   Green Bay CC - check for max of 9 guests per hour.
 *        5/25/05   Allow for old style of My Tee Times (list) - return to Member_teelist_list.
 *        5/17/05   Medinah CC - add custom restrictions.
 *        5/05/05   Reject request if a member has an empty mship, mtype, mnum or username (verifySlot.getUsers).
 *        5/04/05   Verify that the tee time has not changed since the member displayed the tee sheet (new parms - wasP1 - 5).
 *        4/25/05   Cordillera - Do not include Employees in member name list.
 *        4/25/05   Add no-cache controls to resp header to prevent client from caching this page.
 *                  This will ensure that the Cancel Tee Time buttons apears when it should.
 *        4/13/05   Custom for Portage - check Associate mships for max rounds per month and year.
 *        4/11/05   Pine Hills Custom - Juniors must be with an adult at specified times.
 *        4/11/05   Westchester Custom - Dependents must be with an adult on South course on
 *                  w/e and holidays between 10:30 and 1:45 PM.
 *        4/05/05   Rogue Valley Custom - must be > 2 players on Wed & W/E's before 2 PM.
 *        4/04/05   Hudson Natl - add 2-some only times.
 *        4/01/05   Put the 'X' option by itself to avoid confusion with guest types.
 *        3/30/05   Westchester Custom - must be > 2 players on w/e before 2 PM.
 *        3/25/05   Oakmont Custom - send email to caddied master for all guest times.
 *        3/24/05   Add Westchester and Woodway custom tee sheets & 2-some times.
 *        3/19/05   fix some javascript to be more compatible [Paul S]
 *        3/08/05   Add check in doGet processing for valid parameter values - reject if not.
 *        3/02/05   Ver 5 - add support for pre-checkin feature
 *        2/24/05   Piedmont - only allow 2 players during the 1st 4 tee times on Sat & Sun.
 *        2/17/05   Piedmont - do not display 2 of their modes of trans options..
 *                             Also, display custom messages based on the day and time of day.
 *        2/17/05   Ver 5 - add support for option to force members to specify a guest's name.
 *        2/16/05   Include the name of the guest restriction in error message.
 *        1/19/05   Ver 5 - if member does not have a default mode of trans, leave box blank in tee slot
 *                          to force them to specify the correct mode.
 *        1/06/05   Oakmont CC - change guest processing.
 *        1/05/05   Westchester CC - allow member to include 1 guest any time if 3 members in slot.
 *        1/05/05   Ver 5 - allow member to make up to 5 consecutive tee times at once.
 *       12/09/04   Ver 5 - Change Member Name Alphabit table to common table.
 *       11/30/04   Add special processing for Belle Haven CC.
 *       11/30/04   Add special processing for Milwaukee CC.
 *       10/12/04   Ver 5 - 'index' parm now passed as actual index rather than 'i + index'.
 *        9/22/04   Add special processing for Oakmont CC.
 *        9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *        9/07/04   Change submit buttons to help prevent members from cancelling a tee time when they
 *                  meant to remove themselves only.
 *        6/30/04   If club = Old Oaks, force members to enter a name for their guests.
 *        6/24/04   If no POS system for club, do not force guest names to follow members.
 *        5/24/04   Make some improvements to reduce the processing required to process a tee time request.
 *                  Remove the pop-up warning based on number of visits.
 *                  Also, output a 'Please Be Patient' page in case we are busy.
 *        2/09/04   Add separate 9-hole option.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/22/04   Add checks for 'days in advance' violations based on mship type.
 *       12/18/03   Enhancements for Version 4 of the software.
 *        1/11/04   JAG Modified to match new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add lottery processing - do not allow adds if lottery time (after processed).
 *        2/04/03   Add Member Number Restriction processing.
 *
 *        1/15/03   Add notes and other V2 changes.
 *
 *       12/18/02   Add changes for V2.
 *                  Add processing for X now optional, also number of hours can be specified.
 *                  Inform user if X is specifed that it must be filled 'xhrs' before tee time.
 *                  Add multiple 'Guest' names.
 *                  Add support for 5-somes.
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
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.alphaTable;
import com.foretees.common.BigDate;
import com.foretees.common.medinahCustom;
import com.foretees.common.congressionalCustom;
import com.foretees.common.Utilities;
import com.foretees.common.formUtil;
import com.foretees.common.parmSlotPage;
import com.foretees.common.Connect;
import com.foretees.common.reservationUtil;

public class Member_slot_oldmobile extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                       // Software Revision Level (Version)
    static long Hdate1 = ProcessConstants.memDay;            // Memorial Day
    static long Hdate2 = ProcessConstants.july4;             // 4th of July - Monday
    static long Hdate2b = ProcessConstants.july4b;           // 4th of July - ACTUAL 7/04
    static long Hdate3 = ProcessConstants.laborDay;          // Labor Day
    static long Hdate7 = ProcessConstants.tgDay;             // Thanksgiving Day
    static long Hdate8 = ProcessConstants.colDay;            // Columbus Day
    static long Hdate9 = ProcessConstants.colDayObsrvd;      // Columbus Day Observed

    //*************************************************************
    // Process the request from Member_sheet and processing below
    //*************************************************************
    //
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //
        //  If call from self, then go to doPost
        //
        //if (req.getParameter("go") != null) {         // if call from the following process

        doPost(req, resp);      // call doPost processing
        //}
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        String temps = "";
        int contimes = 1;          // default to a single tee time request (necessary for calls from _teelist, etc.)

        //
        //   Check for initial entry from Member_sheet and destined for Member_slotm
        //
        //      See if more than one tee time was requested
        //
        if (req.getParameter("contimes") != null) {        // if 'consecutive tee times' count provided

            temps = req.getParameter("contimes");
            contimes = Integer.parseInt(temps);

            if (contimes > 1) {                            // if more than one tee time requested

                Member_slotm slotm = new Member_slotm();      // create an instance of Member_slotm so we can call it (static vs non-static)

                slotm.doPost(req, resp);                     // call 'doPost' method in _slotm
                return;                                      // exit
            }
        }


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
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }

        //
        //  Get this session's username (to be saved in teecurr)
        //
        String club = (String) session.getAttribute("club");
        String user = (String) session.getAttribute("user");
        String name = (String) session.getAttribute("name");          // get users full name
        String userMship = (String) session.getAttribute("mship");    // get users mship type
        String mtype = (String) session.getAttribute("mtype");        // get users mtype
        String pcw = (String) session.getAttribute("wc");             // get users walk/cart preference
        int activity_id = (Integer) session.getAttribute("activity_id");
        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        int mobile = 0;

        //
        //  See if Mobile user
        //
        try {
            mobile = (Integer) session.getAttribute("mobile");
        } catch (Exception ignore) {
            mobile = 0;
        }


        //
        //  check for letter prompt from mobile device (user clicked on a letter or guest type)
        //
        if (mobile > 0 && (req.getParameter("letterPrompt") != null || req.getParameter("gstPrompt") != null)) {

            String gstPrompt = "";
            String letterPrompt = "";

            if (req.getParameter("gstPrompt") != null) {
                gstPrompt = req.getParameter("gstPrompt").trim();           // get the Guest Type, if selected
            }
            if (req.getParameter("letterPrompt") != null) {
                letterPrompt = req.getParameter("letterPrompt").trim();         // get the Letter, if selected
            }

            if (!gstPrompt.equals("") || !letterPrompt.equals("")) {          // only go there if one was actaully selected

                Common_mobile.namePrompt("teetime", club, user, req, out, con);      // prompt user for name
                return;                                                        // exit and wait for reply
            }
        }
        
        
        //
        //  Do not allow Dsert Mountain 'Family Guest Play' members to enter or change tee times 
        //
        if (club.equals("desertmountain") && userMship.startsWith("Family Guest")) {

            out.println(SystemUtils.HeadTitle("Access Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Access Error</H3>");
            out.println("<BR><BR>Sorry, your membership classification does not allow you to make or modify tee times.");
            out.println("<BR>Please have a member with full golf priveleges assist you or contact the golf shop.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }
        


        //
        //  parm block to hold the tee time parms
        //
        parmSlot slotParms = new parmSlot();          // allocate a parm block

        slotParms.club = club;                        // save club name

        //
        // Process request according to which 'submit' button was selected
        //
        //      'time:fb' - a request from Member_sheet
        //      'cancel'  - a cancel request from user via Member_slot (return with no changes)
        //      'letter'  - a request to list member names (from self)
        //      'submitForm'  - a reservation request (from self)
        //      'remove'  - a 'cancel reservation' request (from self - Cancel Tee Time)
        //      'return'  - a return from verify
        //
        if (req.getParameter("cancel") != null) {

            cancel(mobile, req, out, club, con, session);       // process cancel request
            return;
        }

        if ((req.getParameter("submitForm") != null) || (req.getParameter("remove") != null)) {

            verify(req, out, con, session, resp);                 // process reservation requests request

            return;

        }

        String jump = "0";                     // jump index - default to zero (for _sheet)

        if (req.getParameter("jump") != null) {            // if jump index provided

            jump = req.getParameter("jump");
        }

        //
        //   Submit = 'time:fb' or 'letter'
        //
        int in_use = 0;
        //int count = 0;
        int hr = 0;
        int min = 0;
        int time = 0;
        long mm = 0;
        long dd = 0;
        long yy = 0;
        long temp = 0;
        long date = 0;
        int fb = 0;
        int x = 0;
        int xCount = 0;
        int i = 0;
        int hide = 0;
        //int nowc = 0;
        int p91 = 0;
        int p92 = 0;
        int p93 = 0;
        int p94 = 0;
        int p95 = 0;
        int gp1 = 0;
        int gp2 = 0;
        int gp3 = 0;
        int gp4 = 0;
        int gp5 = 0;
        int ind = 0;
        int indReal = 0;
        int guest_id1 = 0;
        int guest_id2 = 0;
        int guest_id3 = 0;
        int guest_id4 = 0;
        int guest_id5 = 0;
        int custom_int = 0;

        int players_per_group = 5;
        int players = 5;
        int visible_players_per_group = 5;

        int thisYear = 0;
        int thisMonth = 0;
        int thisDay = 0;
        int thishr = 0;
        int thismin = 0;
        int thisTime = 0;
        int thisTimeAdjusted = 0;
        int week_of_year = 0;

        long shortDate = 0;
        long todayDate = 0;     // create a date field of yyyymmdd


        Calendar cal = new GregorianCalendar();       // get todays date
        Calendar cal3 = new GregorianCalendar();       // get todays date

        String player1 = "";
        String player2 = "";
        String player3 = "";
        String player4 = "";
        String player5 = "";
        String p1cw = "";
        String p2cw = "";
        String p3cw = "";
        String p4cw = "";
        String p5cw = "";
        String user1 = "";
        String user2 = "";
        String user3 = "";
        String user4 = "";
        String user5 = "";
        String orig1 = "";
        String orig2 = "";
        String orig3 = "";
        String orig4 = "";
        String orig5 = "";
        String msubtype = "";

        String sdate = "";
        String stime = "";
        String ampm = "";
        String sfb = "";
        String notes = "";
        String hides = "";
        String msg = "";
        String pname = "";
        String returnCourse = "";
        String backCourse = "";
        String orig_by = "";
        String displayOpt = "";              // display option for Mobile devices
        String course_disp = "";

        String custom_disp1 = "";
        String custom_disp2 = "";
        String custom_disp3 = "";
        String custom_disp4 = "";
        String custom_disp5 = "";

        String custom1 = "";
        String custom2 = "";
        String custom3 = "";
        String custom4 = "";
        String custom5 = "";

        boolean blockP1 = false;
        boolean blockP2 = false;
        boolean blockP3 = false;
        boolean blockP4 = false;
        boolean blockP5 = false;
        boolean first_call = true;                   // default to first time thru - from Member_sheet
        boolean restrictByOrig = false;              // Used for customs to use orig values to restrict access within a tee time
        boolean skipMembers = false;                 // If true, the Member List, Alphabet letter selection, and Partner list will be hidden
        boolean skipGuests = false;                  // If true, the Guest Type list will be hidden

        boolean json_mode = (req.getParameter("json_mode") != null);
        //boolean verify_use_mode = (req.getParameter("verify_use_mode") != null);

        //Map<String, Object> result_map = new linkedHashMap<String, Object>();  // Used for misc. json response
        //Map<String, String> guest_type_cw_map = new LinkedHashMap<String, String>();  // Map of default transport type to be passed to javascript
        Gson gson_obj = new Gson();

        //Map<String, Map<String, Object>> guest_types_map = new LinkedHashMap<String, Map<String, Object>>();


        //
        //  New tee time indicator (if true, the tee time was empty when it was selected from the tee sheet)
        //
        boolean newreq = false;

        //
        //  2-some indicator used for some custom requests
        //
        //boolean twoSomeOnly = false;
        //boolean threeSomeOnly = false;

        //
        //   Flag for Cancel Tee Time button (show or not show)
        //
        boolean allowCancel = true;              // default to 'allow'
        boolean hawksLandingCustomMsg = false;

        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(0, con);

        //
        //  parm block to hold the course parameters
        //
        parmCourse parmc = new parmCourse();          // allocate a parm block

        // Create fill slot page parameters we already know, fill in the rest later
        parmSlotPage slotPageParms = new parmSlotPage();

        slotPageParms.club = club;
        slotPageParms.club_name = clubName;
        slotPageParms.slot_url = "Member_slot";
        slotPageParms.notice_message = "";
        slotPageParms.slot_help_url = "../member_help_slot_instruct.htm";
        slotPageParms.slot_type = "Tee Time";
        slotPageParms.member_tbd_text = "Member";
        slotPageParms.page_title = "Member Tee Time Request Page";
        slotPageParms.bread_crumb = "Tee Time Registration";
        slotPageParms.show_fb = true;
        slotPageParms.show_transport = true;
        slotPageParms.user = user;
        slotPageParms.mship = userMship;
        slotPageParms.mtype = mtype;
        slotPageParms.zip_code = (String) session.getAttribute("zipcode");
        
        
        //
        // Store request parameters in our slotPageParms, in case we need them for call-back later
        // This will trigger a "uses unchecked or unsafe operations" warning while compiling.  
        // Perhaps there is a better way to do this, but for now it works.
        //
        List<String> reqNames = (ArrayList<String>) Collections.list((Enumeration<String>) req.getParameterNames());
        for (String reqName : reqNames) {
            slotPageParms.callback_map.put(reqName, req.getParameter(reqName));
        }
        slotPageParms.callback_map.put("json_mode", "true");

        
        //
        // Get all the parameters entered
        //
        String day_name = req.getParameter("day");       //  name of the day
        String index = req.getParameter("index");        //  index value of day (needed by Member_sheet when returning)
        String p5 = req.getParameter("p5");              //  5-somes supported
        String course = req.getParameter("course");      //  Name of Course

        // Start configure block.  We will break out of this if we encounter an issue.  
        configure_slot:
        {
            if (Utilities.getRestrictByOrig(req) || club.equals("tpcboston") || club.equals("alisoviejo") || club.equals("bluebellcc") || club.equals("hiwan") || club.equals("trophyclubcc")
                    || club.equals("pmarshgc") || club.equals("governorsclub") || club.equals("belfair") || club.equals("wildcatruncc") || club.equals("yellowstonecc")
                    || club.equals("dovecanyonclub") || club.equals("indianridgecc") || club.contains("deserthighlands") || club.contains("claremontcc")
                    || club.equals("bayclubmatt") || club.equals("olyclub") || club.equals("desertmountain") || club.equals("brookridgegf") || club.equals("ballenisles") 
                    || club.equals("marbellacc") || club.equals("shadycanyongolfclub") || club.equals("plantationgcc") || club.equals("bayhill") || club.equals("elgincc") 
                    || club.equals("castlepines")) {
                
                restrictByOrig = true;
            }


            if (req.getParameter("newreq") != null) {        // passed from Member_sheet (will be false if from any other page)

                newreq = true;          // new tee time request (players empty on tee sheet)
            }

            if (req.getParameter("returnCourse") != null) {

                returnCourse = req.getParameter("returnCourse");
            }

            if (returnCourse.equals("")) {            // if multi course club, get course to return to (ALL?)

                backCourse = course;

            } else {

                backCourse = returnCourse;              // use return course if multi
            }

            if (req.getParameter("fb") != null) {

                sfb = req.getParameter("fb");
            }

            if (req.getParameter("sdate") != null) {         // if date was passed in sdate

                sdate = req.getParameter("sdate");
            }

            if (req.getParameter("date") != null) {          // if date was passed in date

                sdate = req.getParameter("date");
            }

            //
            //  Get the Display Option if specified (Mobile Devices)
            //
            if (req.getParameter("displayOpt") != null) {

                displayOpt = req.getParameter("displayOpt");        // display option - morning, afternoon, etc.
            }


            if (req.getParameter("stime") != null) {         // if time was passed in stime

                stime = req.getParameter("stime");

            } else {                                         // call from Member_sheet

                if (req.getParameter("ttdata") != null) {

                    String tmp = Utilities.decryptTTdata(req.getParameter("ttdata"));

                    //out.println("<!-- " + req.getParameter("ttdata") + " = " + tmp + " -->");

                    StringTokenizer tok = new StringTokenizer(tmp, "|");     // separate name around the colon

                    stime = tok.nextToken();                          // shart hand time (9:35 AM)
                    sfb = tok.nextToken();                            // front/back indicator value
                    tmp = tok.nextToken();                            // username of member

                    if (!tmp.equalsIgnoreCase(user)) {

                        logoffAC(out, session, con);     // force logoff and exit
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

                //
                //  Make sure the number of times requested was passed (consecutive tee times) - should be 1
                //
                if (contimes < 1) {                            // if less than one tee time requested

                    if (new_skin && mobile == 0) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "Invalid Request";
                        slotPageParms.page_start_notifications.add("Sorry, but you must first select the number of tee times you are requesting.");
                        slotPageParms.page_start_notifications.add("Please select the count from the drop-down list to the right of the time button after you return.");
                        slotPageParms.page_start_notifications.add("Contact your golf shop if you feel you incorrectly received this message.");
                        break configure_slot;

                    } else if (mobile == 0) {            // if NOT Mobile

                        out.println(SystemUtils.HeadTitle("Invalid Access"));
                        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                        out.println("<CENTER><BR><BR><H2>Invalid Request</H2>");
                        out.println("<BR><BR>Sorry, but you must first select the number of tee times you are requesting.<BR>");
                        out.println("<BR>Please select the count from the drop-down list to the right of the time button after you return.");
                        out.println("<BR><BR>Contact your golf shop if you feel you incorrectly received this message.");
                        out.println("<BR><BR>");
                        if (index.equals("999")) {       // if from Member_teelist (my tee times)

                            out.println("<font size=\"2\">");
                            out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain.htm\">");
                            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                            out.println("</form></font>");
                        } else {

                            if (index.equals("995")) {       // if from Member_teelist_list (old my tee times)

                                out.println("<font size=\"2\">");
                                out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain2.htm\">");
                                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                out.println("</form></font>");

                            } else {

                                if (index.equals("888")) {       // if from Member_searchmem

                                    out.println("<font size=\"2\">");
                                    out.println("<form method=\"get\" action=\"/" + rev + "/member_searchmem.htm\">");
                                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                    out.println("</form></font>");

                                } else {                           // from tee sheet

                                    out.println("<font size=\"2\">");
                                    out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + backCourse + "\">");
                                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                    out.println("</form></font>");
                                }
                            }
                        }
                        out.println("</CENTER></BODY></HTML>");

                    } else {

                        //
                        //  Mobile user
                        //
                        out.println(SystemUtils.HeadTitleMobile("ForeTees Request List"));
                        out.println(SystemUtils.BannerMobile());

                        out.println("<div class=\"content\">");
                        out.println("<div class=\"headertext\">");    // output the heading
                        out.println("Invalid Request");
                        out.println("</div>");

                        out.println("<div class=\"smheadertext\">Sorry, there was a system error.<BR>Please try again.</div>");

                        out.println("<ul>");

                        if (index.equals("995")) {         // if came from Member_teelist_list

                            out.println("<li>");
                            out.println("<form action=\"Member_teelist_mobile\" method=\"get\">");
                            out.println("<input type=\"submit\" value=\"Return to List\"></form>");
                            out.println("</li>");

                        } else {

                            out.println("<li>");
                            out.println("<form action=\"Member_sheet\" method=\"post\">");
                            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                            out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
                            out.println("</li>");
                        }

                        out.println("</ul></div></body></html>");
                    }

                    out.close();
                    return;
                }
            }

            //
            //  Get today's date
            //
            cal = new GregorianCalendar();       // get todays date

            thisYear = cal.get(Calendar.YEAR);
            thisMonth = cal.get(Calendar.MONTH) + 1;
            thisDay = cal.get(Calendar.DAY_OF_MONTH);
            thishr = cal.get(Calendar.HOUR_OF_DAY);
            thismin = cal.get(Calendar.MINUTE);

            todayDate = (thisYear * 10000) + (thisMonth * 100) + thisDay;     // create a date field of yyyymmdd
            thisTime = (thishr * 100) + thismin;         // get current time (Central TIme!!)
            thisTimeAdjusted = Utilities.adjustTime(con, thisTime);

            //
            //  Convert the values from string to int
            //
            try {
                date = Long.parseLong(sdate);
                fb = Integer.parseInt(sfb);
            } catch (NumberFormatException e) {
                // ignore error
            }
            
            indReal = getDaysBetween(date);            // get # of days in between today and the date

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

            shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)
            
            cal3 = new GregorianCalendar();
            cal3.set((int)yy, (int)mm-1, (int)dd);    // Set to the date of the current tee time
            week_of_year = cal3.get(Calendar.WEEK_OF_YEAR);

            //
            //  Determine 'ind' value in case we came from Member_teelist or _searchmem (ind = 888 or 999 or 995)
            //
            ind = getDaysBetween(date);            // get # of days in between today and the date of the tee time


            if ((req.getParameter("return") != null) || (req.getParameter("memNotice") != null)
                    || (req.getParameter("promptOtherTime") != null)) {                                             // if this is a return from self
               
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

                ampm = " AM";

                if (hr > 11) {

                    ampm = " PM";

                    if (hr > 12) {

                        hr = hr - 12;
                    }
                }
                if (min < 10) {
                    stime = hr + ":0" + min + ampm;
                } else {
                    stime = hr + ":" + min + ampm;
                }

            } else {

                //
                //  Parse the time parm to separate hh, mm, am/pm and convert to military time
                //  (received as 'hh:mm xx'   where xx = am or pm)
                //
                StringTokenizer tok = new StringTokenizer(stime, ": ");     // space is the default token

                String shr = tok.nextToken();
                String smin = tok.nextToken();
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

                    if (hr != 12) {                    // 12xx will be PM, 00xx will be midnight

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
            //  if this is a call from self - user clicked on a letter or a return from verify
            //
            if ((req.getParameter("letter") != null) || (req.getParameter("return") != null)) {

                first_call = false;       // indicate NOT first call so we don't plug user's name into empty slot
            }


            if ((req.getParameter("letter") != null) || (req.getParameter("return") != null) || (req.getParameter("memNotice") != null)
                    || (req.getParameter("promptOtherTime") != null)) {                                             // if this is a return from self

                player1 = req.getParameter("player1");     // get the player info from the parms passed
                player2 = req.getParameter("player2");
                player3 = req.getParameter("player3");
                player4 = req.getParameter("player4");
                player5 = req.getParameter("player5");
                p1cw = req.getParameter("p1cw");
                p2cw = req.getParameter("p2cw");
                p3cw = req.getParameter("p3cw");
                p4cw = req.getParameter("p4cw");
                p5cw = req.getParameter("p5cw");
                notes = req.getParameter("notes");
                hides = req.getParameter("hide");
                if (req.getParameter("p91") != null) {
                    p91 = Integer.parseInt(req.getParameter("p91"));
                }
                if (req.getParameter("p92") != null) {
                    p92 = Integer.parseInt(req.getParameter("p92"));
                }
                if (req.getParameter("p93") != null) {
                    p93 = Integer.parseInt(req.getParameter("p93"));
                }
                if (req.getParameter("p94") != null) {
                    p94 = Integer.parseInt(req.getParameter("p94"));
                }
                if (req.getParameter("p95") != null) {
                    p95 = Integer.parseInt(req.getParameter("p95"));
                }
                if (req.getParameter("orig_by") != null) {
                    orig_by = req.getParameter("orig_by");
                }
                if (req.getParameter("custom_int") != null) {              
                    try { custom_int = Integer.parseInt(req.getParameter("custom_int")); } catch (Exception ignore) {}
                }
                if (req.getParameter("custom1") != null) {
                    custom1 = req.getParameter("custom1");   // custom parms added for Interlachen, but can be used by others too
                }
                if (req.getParameter("custom2") != null) {
                    custom2 = req.getParameter("custom2");
                }
                if (req.getParameter("custom3") != null) {
                    custom3 = req.getParameter("custom3");
                }
                if (req.getParameter("custom4") != null) {
                    custom4 = req.getParameter("custom4");
                }
                if (req.getParameter("custom5") != null) {
                    custom5 = req.getParameter("custom5");
                }
                if (req.getParameter("gp1") != null) {
                    custom1 = req.getParameter("gp1");           // custom parms used by oaklandhills (and interlachen)
                }
                if (req.getParameter("gp2") != null) {
                    custom2 = req.getParameter("gp2");
                }
                if (req.getParameter("gp3") != null) {
                    custom3 = req.getParameter("gp3");
                }
                if (req.getParameter("gp4") != null) {
                    custom4 = req.getParameter("gp4");
                }
                if (req.getParameter("gp5") != null) {
                    custom5 = req.getParameter("gp5");
                }
                guest_id1 = (req.getParameter("guest_id1") != null) ? Integer.parseInt(req.getParameter("guest_id1")) : 0;
                guest_id2 = (req.getParameter("guest_id2") != null) ? Integer.parseInt(req.getParameter("guest_id2")) : 0;
                guest_id3 = (req.getParameter("guest_id3") != null) ? Integer.parseInt(req.getParameter("guest_id3")) : 0;
                guest_id4 = (req.getParameter("guest_id4") != null) ? Integer.parseInt(req.getParameter("guest_id4")) : 0;
                guest_id5 = (req.getParameter("guest_id5") != null) ? Integer.parseInt(req.getParameter("guest_id5")) : 0;
                orig1 = (req.getParameter("orig1") != null) ? req.getParameter("orig1") : "";
                orig2 = (req.getParameter("orig2") != null) ? req.getParameter("orig2") : "";
                orig3 = (req.getParameter("orig3") != null) ? req.getParameter("orig3") : "";
                orig4 = (req.getParameter("orig4") != null) ? req.getParameter("orig4") : "";
                orig5 = (req.getParameter("orig5") != null) ? req.getParameter("orig5") : "";

                if (club.equals("timarroncc")) {
                    if (req.getParameter("custom_disp1") != null) {
                        custom_disp1 = req.getParameter("custom_disp1");
                    }
                    if (req.getParameter("custom_disp2") != null) {
                        custom_disp2 = req.getParameter("custom_disp2");
                    }
                    if (req.getParameter("custom_disp3") != null) {
                        custom_disp3 = req.getParameter("custom_disp3");
                    }
                    if (req.getParameter("custom_disp4") != null) {
                        custom_disp4 = req.getParameter("custom_disp4");
                    }
                    if (req.getParameter("custom_disp5") != null) {
                        custom_disp5 = req.getParameter("custom_disp5");
                    }
                    slotParms.custom_disp1 = custom_disp1;
                    slotParms.custom_disp2 = custom_disp2;
                    slotParms.custom_disp3 = custom_disp3;
                    slotParms.custom_disp4 = custom_disp4;
                    slotParms.custom_disp5 = custom_disp5;
                }


                //
                //  Convert hide from string to int
                //
                hide = 0;                       // init to No
                if (hides == null) {
                }
                if (!hides.equals("0")) {     // if not zero
                    hide = 1;
                }

            } else {
           

                //
                //  Get the players' names and check if this tee slot is already in use
                //
                slotParms.day = day_name;            // save day name
                slotParms.p5 = p5;                   // save 5-some indicator
                slotParms.index = index;
                slotParms.course = course;
                slotParms.returnCourse = returnCourse;
                slotParms.jump = jump;
                slotParms.date = date;
                slotParms.fb = fb;
                slotParms.sfb = sfb;
                slotParms.time = time;
                slotParms.mtype = mtype;
                slotParms.mship = userMship;    

                //
                //  Verify the required parms exist
                //
                if (date == 0 || time == 0 || course == null || user.equals("") || user == null) {

                    //
                    //  save message in /" +rev+ "/error.txt
                    //
                    msg = "Error in Member_slot - checkInUse Parms - for user " + user + " at " + club + ".  Date= " + date + ", time= " + time + ", course= " + course + ", fb= " + fb + ", index= " + index;   // build msg
                    SystemUtils.logError(msg);                                   // log it
                    in_use = 1;          // make like the time is busy

                } else {               // continue if parms ok

                    try {

                        if (newreq == true && json_mode == false) {      // if new request and 1st time through, find next avail if busy

                            boolean acError = false;
                            int daysInAdv = 0;
                            int advTime = 0;

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


                            //
                            //  customs to catch cheaters - log them off if cheating
                            //
                            if (club.equals("admiralscove") && ((ind == 3 && thisTime < 632) || ind > 3)) {   // if 3 days in adv & near 7:30 AM ET

                                acError = checkACearly(user, ind, 1);

                                if (acError == true) {

                                    logoffAC(out, session, con);     // force logoff and exit
                                    return;
                                }
                            }                        // end of custom

                            //
                            //  Check Gallery Golf for cheaters also
                            //
                            if (club.equals("gallerygolf") && !userMship.equals("Manager") && ind > 5) {

                                acError = checkGGearly(user, ind, con);

                                if (acError == true) {

                                    logoffAC(out, session, con);     // force logoff and exit
                                    return;
                                }
                            }


                            //
                            //  Check Eagle Creek for cheaters
                            //
                            if (club.equals("eaglecreek") && ((ind == daysInAdv && thisTime < 602) || ind > daysInAdv)) {  // 7:00 ET 2/3 days in adv

                                acError = verifyCustom.checkECearly(user, ind, 1, daysInAdv, advTime, con);   // check days in adv, time and slots requested

                                if (acError == true) {

                                    logoffAC(out, session, con);     // force logoff and exit
                                    return;
                                }
                            }



                            //
                            //  New Tee Time Request - Check if in use and if so, search for the next available time
                            //
                            in_use = verifySlot.checkInUseN(date, time, fb, course, user, slotParms, req);

                            //
                            //  temp to catch cheaters - make history entry if 7 days in adv and before 7:05 AM ET
                            //
                            if ((club.equals("pelicansnest") && ind == 7 && thisTime < 605)
                                    || (club.equals("eaglecreek") && daysInAdv > 0 && ind == daysInAdv && thisTime < 605)
                                    || (club.equals("timarroncc") && ind == 4 && thisTime < 703)
                                    || (club.equals("wollastongc") && ind == 4 && thisTime < 603)
                                    || (club.equals("ccnaples") && ind == 7 && thisTime < 603)
                                    || (club.equals("admiralscove") && ind == 3 && thisTime < 635)) {       // temp custom to catch cheaters !!!!!!!!!!!!!

                                //  make history entry to track the time entered
                                SystemUtils.updateHist(date, slotParms.day, time, fb, course, slotParms.player1, slotParms.player2, slotParms.player3,
                                        slotParms.player4, slotParms.player5, user, name, 0, con);
                            }   // end of custom


                            //
                            //  If we did not get the exact tee time requested, then ask the user if they want to proceed or go back.
                            //
                            if (in_use == 9 && !club.equals("olyclub")) {                     // if found, but different than requested

                                promptOtherTime(mobile, new_skin, out, slotParms, slotPageParms);    // send prompt
                                
                                if (new_skin && mobile == 0) {
                                    time = slotParms.time;
                                    fb = slotParms.fb;
                                    break configure_slot;
                                } else {
                                    return;                             // exit and wait for answer
                                }
                            }

                        } else {      // check in use - existing tee time or json_mode (2nd pass)

                            in_use = verifySlot.checkInUse(date, time, fb, course, user, slotParms, req);
                        }

                    } catch (Exception e1) {

                        msg = "Member_slot Check in use flag failed - Exception: " + e1.getMessage();

                        SystemUtils.logError(msg);                                   // log it

                        in_use = 1;          // make like the time is busy
                    }
                }

                
                int memedit = 0;          // event member edit option indicator
                
                if (!slotParms.event.equals("")) {   // if time slot part of an event

                    memedit = Utilities.getMemeditOpt(slotParms.event, con);       // get the memedit setting for this event
                }

                if (in_use != 0 || !slotParms.blocker.equals("") || (!slotParms.event.equals("") && memedit == 0)) {   // if time slot already in use or not allowed

                    if (new_skin && mobile == 0) {  
                                   
                        slotPageParms.page_start_button_go_back = true;
                        if ((!slotParms.event.equals("") && memedit == 0) || !slotParms.blocker.equals("")) {
                            slotPageParms.page_start_title = "Tee Time Not Allowed";
                            slotPageParms.page_start_notifications.add("Sorry, but you are not allowed to access this tee time.");
                            slotPageParms.page_start_notifications.add("Please select another time.");
                        } else if (msg.endsWith("after connection closed.")) {
                            slotPageParms.page_start_title = "Session Timed Out";
                            slotPageParms.page_start_notifications.add("Sorry, but your session has timed out or your database connection has been lost.");
                            slotPageParms.page_start_notifications.add("Please exit ForeTees and try again");
                        } else {
                            slotPageParms.page_start_title = "Tee Time Slot Busy";
                            slotPageParms.page_start_notifications.add("Sorry, but this tee time slot is currently busy.");
                            slotPageParms.page_start_notifications.add("Please select another time or try again later.");
                        }
                        break configure_slot;

                    } else if (mobile == 0) {         // if NOT mobile user  (old_skin)

                        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                        if ((!slotParms.event.equals("") && memedit == 0) || !slotParms.blocker.equals("")) {   // if time not allowed
                            out.println("<CENTER><BR><BR><H2>Tee Time Not Allowed</H2>");
                            out.println("<BR><BR>Sorry, but you are not allowed to access this tee time.<BR>");
                            out.println("<BR>Please select another time.");
                        } else {
                            if (msg.endsWith("after connection closed.")) {                           // if session timed out error
                                out.println("<CENTER><BR><BR><H2>Session Timed Out</H2>");
                                out.println("<BR><BR>Sorry, but your session has timed out or your database connection has been lost.<BR>");
                                out.println("<BR>Please exit ForeTees and try again.");
                            } else {
                                out.println("<CENTER><BR><BR><H2>Tee Time Slot Busy</H2>");
                                out.println("<BR><BR>Sorry, but this tee time slot is currently busy.<BR>");
                                out.println("<BR>Please select another time or try again later.");
                            }
                        }
                        out.println("<BR><BR>");
                        if (index.equals("999")) {       // if from Member_teelist (my tee times)

                            out.println("<font size=\"2\">");
                            out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain.htm\">");
                            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                            out.println("</form></font>");
                        } else {

                            if (index.equals("995")) {       // if from Member_teelist_list (old my tee times)

                                out.println("<font size=\"2\">");
                                out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain2.htm\">");
                                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                out.println("</form></font>");

                            } else {

                                if (index.equals("888")) {       // if from Member_searchmem

                                    out.println("<font size=\"2\">");
                                    out.println("<form method=\"get\" action=\"/" + rev + "/member_searchmem.htm\">");
                                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                    out.println("</form></font>");

                                } else {                           // from tee sheet

                                    out.println("<font size=\"2\">");
                                    //                  out.println("<form method=\"get\" action=\"/" +rev+ "/member_selmain.htm\">");
                                    out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + backCourse + "\">");
                                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                    out.println("</form></font>");
                                }
                            }
                        }
                        out.println("</CENTER>");

                    } else {

                        //
                        //  Mobile user
                        //
                        out.println(SystemUtils.HeadTitleMobile("ForeTees Request List"));
                        out.println(SystemUtils.BannerMobile());

                        out.println("<div class=\"content\">");
                        out.println("<div class=\"headertext\">");    // output the heading
                        out.println("Reservation Timer Expired");
                        out.println("</div>");

                        out.println("<div class=\"smheadertext\">Sorry, the request has timed out.<BR>Please try again.</div>");

                        out.println("<ul>");

                        if (index.equals("995")) {         // if came from Member_teelist_list

                            out.println("<li>");
                            out.println("<form action=\"Member_teelist_mobile\" method=\"get\">");
                            out.println("<input type=\"submit\" value=\"Return to List\"></form>");
                            out.println("</li>");

                        } else {

                            out.println("<li>");
                            out.println("<form action=\"Member_sheet\" method=\"post\">");
                            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                            out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
                            out.println("</li>");
                        }

                        out.println("</ul></div>");
                    }                               // end of IF Mobile

                    out.println("</BODY></HTML>");
                    out.close();
                    return;

                }

                //
                //  tee time is available - get current player info
                //
                player1 = slotParms.player1;
                player2 = slotParms.player2;
                player3 = slotParms.player3;
                player4 = slotParms.player4;
                player5 = slotParms.player5;
                p1cw = slotParms.p1cw;
                p2cw = slotParms.p2cw;
                p3cw = slotParms.p3cw;
                p4cw = slotParms.p4cw;
                p5cw = slotParms.p5cw;
                notes = slotParms.notes;
                hide = slotParms.hide;
                p91 = slotParms.p91;
                p92 = slotParms.p92;
                p93 = slotParms.p93;
                p94 = slotParms.p94;
                p95 = slotParms.p95;
                user1 = slotParms.user1;
                user2 = slotParms.user2;
                user3 = slotParms.user3;
                user4 = slotParms.user4;
                user5 = slotParms.user5;
                guest_id1 = slotParms.guest_id1;
                guest_id2 = slotParms.guest_id2;
                guest_id3 = slotParms.guest_id3;
                guest_id4 = slotParms.guest_id4;
                guest_id5 = slotParms.guest_id5;
                orig1 = slotParms.orig1;
                orig2 = slotParms.orig2;
                orig3 = slotParms.orig3;
                orig4 = slotParms.orig4;
                orig5 = slotParms.orig5;
                orig_by = slotParms.orig_by;

                custom_int = slotParms.custom_int;
                custom1 = slotParms.custom_disp1;      // added for Interlachen but can be used by others
                custom2 = slotParms.custom_disp2;
                custom3 = slotParms.custom_disp3;
                custom4 = slotParms.custom_disp4;
                custom5 = slotParms.custom_disp5;

                if (club.equals("timarroncc")) {

                    custom_disp1 = slotParms.custom_disp1;
                    custom_disp2 = slotParms.custom_disp2;
                    custom_disp3 = slotParms.custom_disp3;
                    custom_disp4 = slotParms.custom_disp4;
                    custom_disp5 = slotParms.custom_disp5;
                }


                //
                //  Now check if the tee time has changed since the member displayed the tee sheet.
                //  Get the player values passed from Member_sheet and compare against those now in the tee time.
                //
                if (!index.equals("888") && !index.equals("995") && !index.equals("999")) {  // if from Member_sheet

                    String wasP1 = req.getParameter("wasP1");     // get the player values from tee sheet
                    String wasP2 = req.getParameter("wasP2");
                    String wasP3 = req.getParameter("wasP3");
                    String wasP4 = req.getParameter("wasP4");
                    String wasP5 = req.getParameter("wasP5");

                    if (!wasP1.equals(player1) || !wasP2.equals(player2) || !wasP3.equals(player3) || !wasP4.equals(player4)
                            || !wasP5.equals(player5)) {

                        returnToMemSheet(date, time, fb, course, day_name, club, mobile, out, con, new_skin, slotPageParms);
                        if(new_skin){
                            break configure_slot;
                        }else{
                            return;
                        }
                    }
                }

                //
                //  Hacker check - if the tee time is full, then make sure this member is part of it
                //
                if (p5.equals("Yes")) {      // if 5-somes

                    if (!player1.equals("") && !player2.equals("")
                            && !player3.equals("") && !player4.equals("") && !player5.equals("") 
                            && (!club.equals("mpccpb") || (!player1.startsWith("Need A Player") && !player2.startsWith("Need A Player") && !player3.startsWith("Need A Player") 
                            && !player4.startsWith("Need A Player") && !player5.startsWith("Need A Player")))
                            && (!club.startsWith("demo") || (!player1.startsWith("Join Me") && !player2.startsWith("Join Me") && !player3.startsWith("Join Me") 
                            && !player4.startsWith("Join Me") && !player5.startsWith("Join Me")))) {      // if full

                        if (!user1.equalsIgnoreCase(user) && !user2.equalsIgnoreCase(user) && !user3.equalsIgnoreCase(user)
                                && !user4.equalsIgnoreCase(user) && !user5.equalsIgnoreCase(user) && !slotParms.orig_by.equalsIgnoreCase(user)
                                && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && orig2.equalsIgnoreCase(user) && orig3.equalsIgnoreCase(user) && orig4.equalsIgnoreCase(user) && orig5.equalsIgnoreCase(user)))) {   // if member not part of it

                            returnToMemSheet(date, time, fb, course, day_name, club, mobile, out, con, new_skin, slotPageParms);           // act like its busy
                            if(new_skin){
                                break configure_slot;
                            }else{
                                return;
                            }
                        }
                    }

                } else {

                    if (!player1.equals("") && !player2.equals("")
                            && !player3.equals("") && !player4.equals("")
                            && (!club.startsWith("demo") || (!player1.startsWith("Join Me") && !player2.startsWith("Join Me") && !player3.startsWith("Join Me") 
                            && !player4.startsWith("Join Me")))) {                              // if full

                        if (!user1.equalsIgnoreCase(user) && !user2.equalsIgnoreCase(user) && !user3.equalsIgnoreCase(user)
                                && !user4.equalsIgnoreCase(user) && !slotParms.orig_by.equalsIgnoreCase(user)
                                && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && orig2.equalsIgnoreCase(user) && orig3.equalsIgnoreCase(user) && orig4.equalsIgnoreCase(user)))) {               // if member not part of it

                            returnToMemSheet(date, time, fb, course, day_name, club, mobile, out, con, new_skin, slotPageParms);          // act like its busy
                            if(new_skin){
                                break configure_slot;
                            }else{
                                return;
                            }
                        }
                    }
                }

                //
                // Check to see if user has origined too many tee times for this day
                //
                boolean error = false;

                // if max_orig is enabled and user didn't originally book this tee time
                if (parm.max_originations > 0 && !slotParms.orig_by.equalsIgnoreCase(user)) {

                    // also don't check if user is part of the tee time
                    if (!user1.equalsIgnoreCase(user) && !user2.equalsIgnoreCase(user) && !user3.equalsIgnoreCase(user)
                            && !user4.equalsIgnoreCase(user) && !user5.equalsIgnoreCase(user) && !slotParms.orig_by.equalsIgnoreCase(user)) {

                        error = verifySlot.checkMaxOrigBy(user, date, parm.max_originations, con);
                    }
                }

                if (error == true) {

                    if (new_skin && mobile == 0) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "[options.notify.slotBusyTitle]";
                        slotPageParms.page_start_notifications.add("Sorry, but you are allowed to create up to " + parm.max_originations + " tee times for any given day.");
                        slotPageParms.page_start_notifications.add("You may still be able to create additional tee times for other days.");
                        slotPageParms.page_start_notifications.add("This means that you have created your allowed " + parm.max_originations + " tee times for this day.");
                        slotPageParms.page_start_notifications.add("Contact the Golf Shop if you have any questions.");
                        break configure_slot;

                    } else { // old skin and mobile

                        out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                        out.println("<BODY bgcolor=\"#CCCCAA\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><H3>Max Allowed Round Originations Reached</H3><BR>");
                        out.println("Sorry, but you are allowed to create up to " + parm.max_originations + " ");
                        out.println("tee times <br>for any given day.&nbsp; ");
                        out.println("This means that you have created your allowed " + parm.max_originations + " tee times for this day.<br>");
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
                        out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" name=\"cancel\">");

                        out.println("</form></center></html>");
                        out.close();
                        return;
                    }
                }
                              
                if (club.equals("bishopsgategc")) {
                
                    if (newreq == true && json_mode == true) {
                        try { custom_int = Integer.parseInt(req.getParameter("custom_int")); } catch (Exception ignore) {} 
                    }
                    
                    if (custom_int == -1) {
                        slotPageParms.default_fb_value = 1; // 0 or 1
                        slotPageParms.set_default_fb_value = true;
                        slotPageParms.lock_fb = true;
                    }
                    
                    if (req.getParameter("skip_custom_notice") == null) {
                        
                        if (custom_int == -1) {
                            slotPageParms.page_start_button_go_back = true;
                            slotPageParms.page_start_button_accept = true;
                            slotPageParms.page_start_title = "[options.notify.noticeFromGolfShopTitle]";
                            slotPageParms.page_start_notifications.add("Notice: There is no crossover period available for this tee time. Only a 9-hole round can be booked.");
                            slotPageParms.page_start_notifications.add("[options.notify.continueWithRequestPrompt]");
                            slotPageParms.callback_map.put("skip_custom_notice", "yes");
                            slotPageParms.callback_map.put("custom_int", custom_int);
                            break configure_slot;
                        } else if (custom_int == -2 || custom_int == -3) {
                            custom_int = 0;
                        }
                        
                        slotPageParms.callback_map.put("custom_int", custom_int);
                        
                    }
                }
                
                //
                //**********************************************
                //   Check for Member Notice from Pro
                //**********************************************
                //
                String memNotice = verifySlot.checkMemNotice(date, time, fb, course, day_name, "teetime", false, con);

                //
                //   Scioto CC - no guests allowed more than 4 or 5 days in advance based on day of week
                //
      /*
                if (club.equals( "sciotocc" ) && shortDate > 500 && shortDate < 1016 && ind > 4) {
                
                int sciotoDays = 5;
                
                if (slotParms.day.equals("Sunday") || slotParms.day.equals("Monday")) {
                
                sciotoDays = 4;           // guests allowed 4 days or less
                }
                
                if (ind > sciotoDays) {       // display custom member notice if no guests allowed in his tee time
                
                memNotice = memNotice + "Please be advised that guests will not be allowed in this tee time this far in advance. " +
                " Additionally, you will not be allowed to replace players in this tee time with guests at a later date.<BR><BR>";
                }
                }
                 */


                if (!memNotice.equals("") && (req.getParameter("skip_member_notice") == null)) {      // if message to display

                    //
                    //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
                    //
                    if (new_skin && mobile == 0) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_button_accept = true;
                        slotPageParms.page_start_title = "[options.notify.noticeFromGolfShopTitle]";
                        slotPageParms.page_start_notifications.add(memNotice);
                        slotPageParms.page_start_notifications.add("[options.notify.continueWithRequestPrompt]");
                        slotPageParms.callback_map.put("skip_member_notice", "yes");
                        break configure_slot;

                    } else if (mobile == 0) {                     // if NOT a mobile user

                        out.println("<HTML><HEAD>");
                        out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
                        out.println("<title>Member Notice For Tee Time Request</Title>");
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
                        out.println("<form action=\"Member_slot\" method=\"post\" name=\"can\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                        out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                        out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                        out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
                        out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");

                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\">");

                        out.println("</form></font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_slot\" method=\"post\">");
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                        out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                        out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                        out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
                        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
                        out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
                        out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
                        out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
                        out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
                        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                        out.println("<input type=\"hidden\" name=\"custom_int\" value=\"" + custom_int + "\">");
                        out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + custom1 + "\">");
                        out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + custom2 + "\">");
                        out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + custom3 + "\">");
                        out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + custom4 + "\">");
                        out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + custom5 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + orig_by + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig1\" value=\"" + orig1 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig2\" value=\"" + orig2 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig3\" value=\"" + orig3 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig4\" value=\"" + orig4 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig5\" value=\"" + orig5 + "\">");
                        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                        out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
                        if (club.equals("loscoyotes")) {
                            out.println("<input type=\"submit\" value=\"AGREE - Continue\">");
                        } else {
                            out.println("<input type=\"submit\" value=\"YES - Continue\">");
                        }
                        out.println("</form></font></td></tr>");
                        out.println("</table>");

                        out.println("</td>");
                        out.println("</tr>");
                        out.println("</table>");
                        out.println("</font></center></body></html>");


                    } else {

                        //
                        //  Mobile user
                        //
                        out.println(SystemUtils.HeadTitleMobile("ForeTees Member Notice"));
                        out.println(SystemUtils.BannerMobile());

                        out.println("<div class=\"content\">");
                        out.println("<div class=\"headertext\">");    // output the heading
                        out.println("NOTICE FROM YOUR GOLF SHOP");
                        out.println("</div>");

                        out.println("<div class=\"smheadertext\">" + memNotice + "</div>");

                        out.println("<div>Would you like to continue with this request?</div>");

                        out.println("<ul><li>");
                        out.println("<form action=\"Member_slot\" method=\"post\" name=\"can\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                        out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                        out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                        out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
                        out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                        out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\">");
                        out.println("</form></li>");

                        out.println("<li>");
                        out.println("<form action=\"Member_slot\" method=\"post\">");
                        out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                        out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                        out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                        out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
                        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
                        out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
                        out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
                        out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
                        out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig1\" value=\"" + orig1 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig2\" value=\"" + orig2 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig3\" value=\"" + orig3 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig4\" value=\"" + orig4 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig5\" value=\"" + orig5 + "\">");
                        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                        out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + custom1 + "\">");
                        out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + custom2 + "\">");
                        out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + custom3 + "\">");
                        out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + custom4 + "\">");
                        out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + custom5 + "\">");
                        out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + orig_by + "\">");
                        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                        out.println("<input type=\"hidden\" name=\"custom_int\" value=\"" + custom_int + "\">");
                        out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
                        if (club.equals("loscoyotes")) {
                            out.println("<input type=\"submit\" value=\"AGREE - Continue\">");
                        } else {
                            out.println("<input type=\"submit\" value=\"YES - Continue\">");
                        }
                        out.println("</form></li>");
                        out.println("</ul></div>");
                        out.println("<div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)
                        out.println("</body></html>");
                    }
                    out.close();
                    return;
                }

            }

            //
            //  Ensure that there are no null player fields
            //
            if (player1 == null) {
                player1 = "";
            }
            if (player2 == null) {
                player2 = "";
            }
            if (player3 == null) {
                player3 = "";
            }
            if (player4 == null) {
                player4 = "";
            }
            if (player5 == null) {
                player5 = "";
            }
            if (p1cw == null) {
                p1cw = "";
            }
            if (p2cw == null) {
                p2cw = "";
            }
            if (p3cw == null) {
                p3cw = "";
            }
            if (p4cw == null) {
                p4cw = "";
            }
            if (p5cw == null) {
                p5cw = "";
            }


            //
            //  Check if this is a new tee time request (empty time).  We need to do this now in case this is a return via letter, etc.
            //
            newreq = verifySlot.checkNewReq(date, time, fb, course, con);


            //
            //  Interlachen - use custom_disp fileds for the Gift Pack option
            //
            // if (club.equals("interlachen") || club.equals("oaklandhills")) {
            if (club.equals("oaklandhills")) {

                slotPageParms.show_gift_pack = true;
                slotPageParms.gift_pack_text = "";

                gp1 = 0;
                gp2 = 0;
                gp3 = 0;
                gp4 = 0;
                gp5 = 0;

                if (custom1.equals("1")) {
                    gp1 = 1;
                }
                if (custom2.equals("1")) {
                    gp2 = 1;
                }
                if (custom3.equals("1")) {
                    gp3 = 1;
                }
                if (custom4.equals("1")) {
                    gp4 = 1;
                }
                if (custom5.equals("1")) {
                    gp5 = 1;
                }
            }


            //
            //  Custom for Oakmont CC - if less than 30 days in advance, guest tee times cannot be changed -
            //                          players may be added, but not removed.  Also, do not allow cancel from
            //                          My Tee Times or Search (can't tell how far in advance).
            //
            boolean oakguests = false;

            if (club.equals("oakmont")) {      // if Oakmont CC

                oakguests = checkOakGuests(date, time, fb, course, con);   // check if any guests in tee time (if within 30 days)
            }

            //
            //  Get the walk/cart options available
            //
            try {

                getParms.getTmodes(con, parmc, course);
            } catch (Exception e1) {

                msg = "Get wc options. ";

                dbError(out, e1, msg);
                return;
            }
            
            // Set player count/size
            players_per_group = ((p5.equals("Yes")) ? 5 : 4);
            players = players_per_group;
            visible_players_per_group = reservationUtil.getMaxPlayersForTeeTime(req, (int)date, time, course, fb, players_per_group == 5);

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

            //
            //  if Piedmont Driving Club, remove 2 trans modes that are for events only
            //                  Also, check for 2-some time, and caddie only times.
            //
            int piedmontStatus = 0;

            if (club.equals("piedmont")) {

                piedmontStatus = verifySlot.checkPiedmont(date, time, day_name);     // check if special time

                Calendar cal2 = new GregorianCalendar();

                cal2.set((int) yy, (int) (mm - 1), (int) dd);

                boolean isDST = false;

                if (cal2.get(Calendar.DST_OFFSET) != 0) {
                    isDST = true;
                }

                if (time < 1210 || (isDST && time > 1430) || (!isDST && time > 1330)) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("CFC")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }

                for (i = 0; i < parmc.tmode_limit; i++) {

                    //
                    //  If "Walking No Caddie" and time requires a Caddie, remove this option
                    //
                    if (parmc.tmodea[i].equalsIgnoreCase("wnc") && piedmontStatus > 0) {

                        parmc.tmodea[i] = "";      // remove it
                    }

                    //
                    //  If "Cart With ForeCaddie" or "Walk With Caddie, remove this option (used for events only)
                    //
                    if (parmc.tmodea[i].equalsIgnoreCase("wwc")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

            //
            //  if The Stanwich Club and before 1:00 on any day, remove trans mode of 'Carry' (CRY)
            //
            if (club.equals("stanwichclub")) {

                if (time < 1300) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("cry")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
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
            //  if Imperial Golf Club and before 11:30 on any day between 1/1 - 5/31, remove trans mode of Walk & Pull Cart  (Case #1287)
            //
            if (club.equals("imperialgc")) {

                // long mmdd = (mm*100) + dd;

                // if (mmdd >= 101 && mmdd <= 531 && time < 1130) {

                if (date <= 20100531 && time < 1130) {        // this custom goes away after 5/31/2010

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("WLK") || parmc.tmodea[i].equalsIgnoreCase("PC")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            /*
            //
            //  if Wilmington, remove trans mode of 'Carry Your Own' (CYO) on specified times
            //
            if (club.equals( "wilmington" )) {
            
            boolean wilstrip = false;
            
            if ((day_name.equals( "Tuesday" ) || day_name.equals( "Wednesday" ) || day_name.equals( "Thursday" ) || day_name.equals( "Friday" )) &&
            date != Hdate2b) {       // if Tues - Fri and NOT 7/04
            
            if (time < 1300 || time > 1900) {
            
            wilstrip = true;
            }
            
            } else {
            
            if (day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ) || date == Hdate1 || date == Hdate3) {   // if a w/e, Mem Day or Labor Day
            
            if (time < 1100 || time > 1900) {
            
            wilstrip = true;
            }
            }
            }
            
            if (wilstrip == true) {
            
            for (i = 0; i < parmc.tmode_limit; i++) {
            
            if (parmc.tmodea[i].equalsIgnoreCase( "cyo" )) {
            
            parmc.tmodea[i] = "";      // remove it
            }
            }
            }
            }
             */


            /*   ***removed at request of club***
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
            //  If Pinnacle Peak remove the trans mode of 'NC' (Case 1288)
            //
            if (club.equals("pinnaclepeak")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("NC")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

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

            //
            //  If Silver Lake CC, remove the trans mode 'WLK' if any day but Tues/Thurs and before 3pm
            //
            if (club.equals("silverlakecc")) {

                if (!day_name.equals("Tuesday") && !day_name.equals("Thursday") && time < 1500) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("WLK")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }
            //
            //  If CapeCod National CC thurs-sunday before 1:10pm only Cart or Caddie for MOD.
            //
            if (club.equals("capecodnational")) {
                if (day_name.equals("Thursday") || day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday")) {
                    if (shortDate >= 620 && shortDate <= 901 && time < 1310) {
                        for (i = 0; i < parmc.tmode_limit; i++) {
                            if (parmc.tmodea[i].equalsIgnoreCase("W") || parmc.tmodea[i].equalsIgnoreCase("PC") || parmc.tmodea[i].equalsIgnoreCase("W9C") || parmc.tmodea[i].equalsIgnoreCase("P9C")) {

                                parmc.tmodea[i] = "";      // remove it
                            }
                        }
                    }
                }
            }

            //
            //  If Tavistock CC and between 4/1 and 10/31, remove the trans mode 'CRY' and 'TRL if any day but Tues/Thurs and before 3pm
            //
            if (club.equals("tavistockcc")) {

                if (shortDate >= 401 && shortDate <= 1031 && time < 1359) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("CRY") || parmc.tmodea[i].equalsIgnoreCase("TRL")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            //
            //  If Wayzata CC, remove all mode of trans aside from 'CAD' and 'CRT' if between 6/1 and 8/31 on Wed/Fri/Sat/Sun before 3PM
            //
            if (club.equals("wayzata")) {

                if (shortDate >= 601 && shortDate <= 831 && time < 1330
                        && (day_name.equals("Wednesday") || day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday"))) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (!parmc.tmodea[i].equalsIgnoreCase("CAD") && !parmc.tmodea[i].equalsIgnoreCase("CRT")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
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

           
                /*
                //
                //  If course is Club Course Gold today, then do not allow any access - walk-up times only (Tues - Fri)
                //
                String congCourse = congressionalCustom.getFullCourseName(date, (int) dd, course);

                if (twoSomeOnly == false && congCourse.equals("Club Course Gold") && (day_name.equals("Tuesday")
                        || day_name.equals("Wednesday") || day_name.equals("Thursday") || day_name.equals("Friday"))) {

                    // msg = "Member_slot Congressional - member accessing a restricted tee time on Gold course. User=" +user+ ", Date=" +date+ ", Time=" +time+ ", Index=" +index;

                    // SystemUtils.logError(msg);                                   // log it
                    if (new_skin && mobile == 0) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "Tee Time Not Available";
                        slotPageParms.page_start_notifications.add("Sorry, but you are not allowed access to tee times on this course for this day.");
                        slotPageParms.page_start_notifications.add("Please select another course or day of the week.");
                        slotPageParms.page_start_notifications.add("Please contact your golf shop if you feel you incorrectly received this message.");
                        break configure_slot;

                    } else if (mobile == 0) {       // if NOT Mobile

                        out.println(SystemUtils.HeadTitle("Invalid Access"));
                        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                        out.println("<CENTER><BR><BR><H2>Tee Time Not Available</H2>");
                        out.println("<BR><BR>Sorry, but you are not allowed access to tee times on this course for this day.<BR>");
                        out.println("<BR>Please select another course or day of the week.");
                        out.println("<BR><BR>Please contact your golf shop if you feel you incorrectly received this message.");
                        out.println("<BR><BR>");
                        if (index.equals("999")) {       // if from Member_teelist (my tee times)

                            out.println("<font size=\"2\">");
                            out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain.htm\">");
                            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                            out.println("</form></font>");
                        } else {

                            if (index.equals("995")) {       // if from Member_teelist_list (old my tee times)

                                out.println("<font size=\"2\">");
                                out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain2.htm\">");
                                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                out.println("</form></font>");

                            } else {

                                if (index.equals("888")) {       // if from Member_searchmem

                                    out.println("<font size=\"2\">");
                                    out.println("<form method=\"get\" action=\"/" + rev + "/member_searchmem.htm\">");
                                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                    out.println("</form></font>");

                                } else {                           // from tee sheet

                                    out.println("<font size=\"2\">");
                                    out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + backCourse + "\">");
                                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                    out.println("</form></font>");
                                }
                            }
                        }
                        out.println("</CENTER></BODY></HTML>");

                    } else {

                        //
                        //  Mobile user
                        //
                        out.println(SystemUtils.HeadTitleMobile("ForeTees Request List"));
                        out.println(SystemUtils.BannerMobile());

                        out.println("<div class=\"content\">");
                        out.println("<div class=\"headertext\">");    // output the heading
                        out.println("Tee Time Not Available");
                        out.println("</div>");
                        out.println("<div class=\"smheadertext\">Sorry, but you are not allowed access to tee times on this course for this day.<BR>Please select another course or day of the week.</div>");
                        out.println("<ul>");

                        if (index.equals("995")) {         // if came from Member_teelist_list

                            out.println("<li>");
                            out.println("<form action=\"Member_teelist_mobile\" method=\"get\">");
                            out.println("<input type=\"submit\" value=\"Return to List\"></form>");
                            out.println("</li>");

                        } else {

                            out.println("<li>");
                            out.println("<form action=\"Member_sheet\" method=\"post\">");
                            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                            out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
                            out.println("</li>");
                        }

                        out.println("</ul></div></body></html>");
                    }
                    out.close();
                    return;
                 }
                 */
            //}           // end of IF Congressional


            



            //
            //  Make sure the user's c/w option is still supported (pro may have changed config)
            //
            if (!pcw.equals("") && !club.equals("sonnenalp")) {    // must skip this for Sonnenalp so ICP and FCP are allowed for some members

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
            //   If Fort Collins and the Greeley course, then use GC as the default mode of trans
            //
            if (club.equals("fortcollins") && course.startsWith("Greeley")) {

                if (pcw.equals("")) {

                    pcw = "GC";
                }
            }

            //
            //   If Merion and the East course, then use CAD as the default mode of trans during specific times
            //

            boolean merionCaddie = false; // old skin

            if (club.equals("merion") && course.equals("East")) {

                if (((day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday")) && time < 1437)
                        || (day_name.equals("Friday") && time < 1537)
                        || ((day_name.equals("Saturday") || day_name.equals("Sunday")) && time < 1401)) {

                    pcw = "CAD";
                    merionCaddie = true; // old skin
                    slotPageParms.custom_caddie = true; // new skin
                }
            }

            if (club.equals("cherryhills") || club.equals("tualatincc")) {

                pcw = "";        // no mode of trans - they must specify
            }


            //
            //  Set user's name as first open player to be placed in name slot for them
            //
            //  First, check if first time here and user is already included in this slot.
            //  Member_sheet already checked if slot is full and user not one of them!!
            //
            if (first_call == true && !player1.equals(name) && !player2.equals(name) && !player3.equals(name) && !player4.equals(name) && !player5.equals(name)) {

                if (player1.equals("")) {

                    player1 = name;
                    p1cw = pcw;
                    guest_id1 = 0;

                } else if (player2.equals("") && visible_players_per_group > 1) {

                    player2 = name;
                    p2cw = pcw;
                    guest_id2 = 0;

                } else if (player3.equals("") && visible_players_per_group > 2) {

                    player3 = name;
                    p3cw = pcw;
                    guest_id3 = 0;

                } else if (player4.equals("") && visible_players_per_group > 3) {

                    player4 = name;
                    p4cw = pcw;
                    guest_id4 = 0;

                } else if (player5.equals("") && visible_players_per_group > 4) {

                    player5 = name;
                    p5cw = pcw;
                    guest_id5 = 0;
                }
            }

            //
            //  Los Coyotes
            //
            if (club.equals("loscoyotes") && newreq == false) {

                if (!name.equals(player1)) {
                    blockP1 = true;          // do not allow member to remove/change any player!
                }
                if (!name.equals(player2)) {
                    blockP2 = true;
                }
                if (!name.equals(player3)) {
                    blockP3 = true;          // everyone is now blocked !!
                }
                if (!name.equals(player4)) {
                    blockP4 = true;
                }
                if (!name.equals(player5)) {
                    blockP5 = true;
                }
            }                              // end of IF Los Coyotes

            if ((club.startsWith("gaa") && !club.endsWith("class")) || club.equals("pgmunl")) {

                PreparedStatement pstmttemp = null;
                ResultSet rstemp = null;

                String tempP1 = "";
                String tempP2 = "";
                String tempP3 = "";
                String tempP4 = "";
                String tempP5 = "";

                try {

                    pstmttemp = con.prepareStatement("SELECT player1, player2, player3, player4, player5 FROM teecurr2 WHERE date = ? AND time = ? AND courseName = ? AND fb = ?");
                    pstmttemp.clearParameters();
                    pstmttemp.setLong(1, date);
                    pstmttemp.setInt(2, time);
                    pstmttemp.setString(3, course);
                    pstmttemp.setInt(4, fb);

                    rstemp = pstmttemp.executeQuery();

                    if (rstemp.next()) {
                        tempP1 = rstemp.getString("player1");
                        tempP2 = rstemp.getString("player2");
                        tempP3 = rstemp.getString("player3");
                        tempP4 = rstemp.getString("player4");
                        tempP5 = rstemp.getString("player5");
                    }

                    pstmttemp.close();

                } catch (Exception exc) {

                    msg = "Check existing players";

                    dbError(out, exc, msg);
                }

                if (!name.equals(player1)) {
                    if (tempP1.equals(player1) && newreq == false) {
                        blockP1 = true;
                    } else {
                        player1 = name;
                    }
                }

                if (!name.equals(player2)) {
                    if (tempP2.equals(player2)) {
                        blockP2 = true;
                    } else {
                        player2 = name;
                    }
                }

                if (!name.equals(player3)) {
                    if (tempP3.equals(player3)) {
                        blockP3 = true;
                    } else {
                        player3 = name;
                    }
                }

                if (!name.equals(player4)) {
                    if (tempP4.equals(player4)) {
                        blockP4 = true;
                    } else {
                        player4 = name;
                    }
                }

                if (!name.equals(player5)) {
                    if (tempP5.equals(player5)) {
                        blockP5 = true;
                    } else {
                        player5 = name;
                    }
                }

                if (user.equalsIgnoreCase(user1) && user2.equals("") && user3.equals("")
                        && user4.equals("") && user5.equals("")) {

                    allowCancel = true;         // OK to cancel this tee time if only one member remaining
                } else {
                    allowCancel = false;        // NOT ok to cancel this tee time
                }
            }

            if (club.equals("hawkslandinggolfclub") && !newreq && index.equals("0")) {   // if Hawks Landing, today and not a new req

                int cur_time = Utilities.getTime(con);                 // get the current adjusted time

                if (cur_time > (time - 300)) {                         // if within 3 hours of the tee time
                    allowCancel = false;                               // do not allow member to cancel it
                    hawksLandingCustomMsg = true;
                }
            }

            //
            //  Brae Burn CC - only originator can remove players and cancel the tee time.
            //
            if (club.equals("braeburncc") && newreq == false && !user.equalsIgnoreCase(orig_by)) {      // if not a new request and not origintor

                blockP1 = true;              // do not allow user to change/erase this one
                blockP2 = true;
                blockP3 = true;
                blockP4 = true;
                blockP5 = true;
                allowCancel = false;        // NOT ok to cancel this tee time
            }


            //
            //  Custom - only Originator can remove players other than themselves or their guests
            //
            if ((club.equals("brookhavenclub") || club.equals("gleneaglesclub") || club.equals("pinehurstcountryclub") || club.equals("oregongolfclub") || club.equals("clcountryclub"))
                    && newreq == false && !user.equalsIgnoreCase(orig_by)) {      // if not origintor (if not owner of tee time)

                if (!name.equals(player1) && !user.equals(slotParms.userg1)) {     // if player is NOT this member or this member's guest

                    blockP1 = true;               // do not allow user to change/erase this one
                }
                if (!name.equals(player2) && !user.equals(slotParms.userg2)) {

                    blockP2 = true;
                }
                if (!name.equals(player3) && !user.equals(slotParms.userg3)) {

                    blockP3 = true;
                }
                if (!name.equals(player4) && !user.equals(slotParms.userg4)) {

                    blockP4 = true;
                }
                if (!name.equals(player5) && !user.equals(slotParms.userg5)) {

                    blockP5 = true;
                }

                if (user.equalsIgnoreCase(user1) && user2.equals("") && user3.equals("")
                        && user4.equals("") && user5.equals("")) {

                    allowCancel = true;         // OK to cancel this tee time if only one member remaining
                } else {
                    allowCancel = false;        // NOT ok to cancel this tee time
                }
            }

            //
            //  Custom to allow members in a request to only remove themselves, any members they added to the time, or any of their guests.
            //    *****add club name at top of doPost where restrictByOrig is set to opt in to this custom.*****
            //
            if (restrictByOrig && newreq == false) {

                if ((orig1.equals("") && !player1.equals("") && !player1.equals(name))
                        || (!orig1.equals("") && !player1.equals(name) && !player1.equalsIgnoreCase("x") && !user.equalsIgnoreCase(orig1) && !user.equalsIgnoreCase(slotParms.userg1))) {     // if player is NOT this member or this member's guest

                    blockP1 = true;               // do not allow him to change/erase this one
                }
                if ((orig2.equals("") && !player2.equals("") && !player2.equals(name))
                        || (!orig2.equals("") && !player2.equals(name) && !player2.equalsIgnoreCase("x") && !user.equalsIgnoreCase(orig2) && !user.equalsIgnoreCase(slotParms.userg2))) {

                    blockP2 = true;
                }
                if ((orig3.equals("") && !player3.equals("") && !player3.equals(name))
                        || (!orig3.equals("") && !player3.equals(name) && !player3.equalsIgnoreCase("x") && !user.equalsIgnoreCase(orig3) && !user.equalsIgnoreCase(slotParms.userg3))) {

                    blockP3 = true;
                }
                if ((orig4.equals("") && !player4.equals("") && !player4.equals(name))
                        || (!orig4.equals("") && !player4.equals(name) && !player4.equalsIgnoreCase("x") && !user.equalsIgnoreCase(orig4) && !user.equalsIgnoreCase(slotParms.userg4))) {

                    blockP4 = true;
                }
                if ((orig5.equals("") && !player5.equals("") && !player5.equals(name))
                        || (!orig5.equals("") && !player5.equals(name) && !player5.equalsIgnoreCase("x") && !user.equalsIgnoreCase(orig5) && !user.equalsIgnoreCase(slotParms.userg5))) {

                    blockP5 = true;
                }

                if ((player1.equals("") || orig1.equalsIgnoreCase(user) || user1.equalsIgnoreCase(user)) && (player2.equals("") || orig2.equalsIgnoreCase(user) || user2.equalsIgnoreCase(user))
                        && (player3.equals("") || orig3.equalsIgnoreCase(user) || user3.equalsIgnoreCase(user)) && (player4.equals("") || orig4.equalsIgnoreCase(user) || user4.equalsIgnoreCase(user))
                        && (player5.equals("") || orig5.equalsIgnoreCase(user) || user5.equalsIgnoreCase(user))) {

                    allowCancel = true;         // OK to cancel this tee time if only one member remaining
                } else {
                    allowCancel = false;        // NOT ok to cancel this tee time
                }
            }


            //
            //  Custom for Timarron - only Originator can remove players other than themselves.
            //                        All members in the request can only remove themselves (and their guests).
            //
            if (club.equals("timarroncc") && newreq == false) {

                if ((slotParms.custom_disp1.equals("") && !player1.equals("") && !player1.equals(name))
                        || (!slotParms.custom_disp1.equals("") && (!player1.equals(name) || !player1.equalsIgnoreCase("x")) && !user.equals(slotParms.custom_disp1) && !user.equals(slotParms.userg1))) {     // if player is NOT this member or this member's guest

                    blockP1 = true;               // do not allow him to change/erase this one
                }
                if ((slotParms.custom_disp2.equals("") && !player2.equals("") && !player2.equals(name))
                        || (!slotParms.custom_disp2.equals("") && (!player2.equals(name) || !player2.equalsIgnoreCase("x")) && !user.equals(slotParms.custom_disp2) && !user.equals(slotParms.userg2))) {

                    blockP2 = true;
                }
                if ((slotParms.custom_disp3.equals("") && !player3.equals("") && !player3.equals(name))
                        || (!slotParms.custom_disp3.equals("") && (!player3.equals(name) || !player3.equalsIgnoreCase("x")) && !user.equals(slotParms.custom_disp3) && !user.equals(slotParms.userg3))) {

                    blockP3 = true;
                }
                if ((slotParms.custom_disp4.equals("") && !player4.equals("") && !player4.equals(name))
                        || (!slotParms.custom_disp4.equals("") && (!player4.equals(name) || !player4.equalsIgnoreCase("x")) && !user.equals(slotParms.custom_disp4) && !user.equals(slotParms.userg4))) {

                    blockP4 = true;
                }
                if ((slotParms.custom_disp5.equals("") && !player5.equals("") && !player5.equals(name))
                        || (!slotParms.custom_disp5.equals("") && (!player5.equals(name) || !player5.equalsIgnoreCase("x")) && !user.equals(slotParms.custom_disp5) && !user.equals(slotParms.userg5))) {

                    blockP5 = true;
                }

                if (user.equalsIgnoreCase(user1) && user2.equals("") && user3.equals("")
                        && user4.equals("") && user5.equals("")) {

                    allowCancel = true;         // OK to cancel this tee time if only one member remaining
                } else {
                    allowCancel = false;        // NOT ok to cancel this tee time
                }
            }      // end of timarron custom


            //
            //  St Clair CC - Terrace course is always 9 holes
            //
            if ((club.equals("stclaircc") && course.equals("Terrace"))
                    || (club.equals("huntingdonvalleycc") && course.equals("Centennial Nine"))
                    || (club.equals("philcricket") && course.equals("St Martins"))
                    || (club.equals("spurwingcc") && course.equals("Challenge Course"))) {

                p91 = 1;
                p92 = 1;
                p93 = 1;
                p94 = 1;
                p95 = 1;
            }


            //
            //  Get usernames from tee time in case not already present (this is a security check and needs to be done)
            //
            msg = "";        // init error message

            if (user1.equals("") || user1 == null) {        // if user not already present

                try {

                    PreparedStatement pstmt = con.prepareStatement(
                            "SELECT username1, username2, username3, username4, username5 "
                            + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                    pstmt.clearParameters();        // clear the parms
                    pstmt.setLong(1, date);         // put the parm in pstmt
                    pstmt.setInt(2, time);
                    pstmt.setInt(3, fb);
                    pstmt.setString(4, course);
                    rs = pstmt.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        user1 = rs.getString(1);
                        user2 = rs.getString(2);
                        user3 = rs.getString(3);
                        user4 = rs.getString(4);
                        user5 = rs.getString(5);
                    }

                    pstmt.close();

                } catch (Exception e1) {

                    msg = "Exception Received Verifying Users.  Exception: " + e1.getMessage();
                }
            }

            //
            //  Make sure the user fields are valid
            //
            if (msg.equals("")) {        // if no error detected above

                //
                //  Do not allow user to cancel the tee time if not already in it
                //
                if (!user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3)
                        && !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5) && !user.equalsIgnoreCase(orig_by)
                        && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && orig2.equalsIgnoreCase(user) && orig3.equalsIgnoreCase(user) && orig4.equalsIgnoreCase(user) && orig5.equalsIgnoreCase(user)))) {

                    allowCancel = false;
                }

                if (club.equals("oakmont") && oakguests == true) {      // if Oakmont guests

                    allowCancel = false;                                   // DO NOT allow
                }

                if (club.equals("inverness") && (player1.startsWith("Hotel") || player2.startsWith("Hotel")
                        || player3.startsWith("Hotel") || player4.startsWith("Hotel") || player5.startsWith("Hotel"))) {

                    allowCancel = false;                  // If Inverness and Hotel Guests, DO NOT allow
                }

                if (club.equals("loscoyotes") || club.equals("pganj")) {    // Don't allow members to cancel tee times.

                    allowCancel = false;
                }

                if (club.startsWith("tpc") && index.equals("0")) {      // if any TPC Club and today

                    allowCancel = false;                                   // DO NOT allow on the day of
                }

                /*
                if (club.equals( "turnerhill" ) && index.equals( "0" )) {      // if any Turner Hill and today
                
                allowCancel = false;                                   // DO NOT allow on the day of
                }
                 */

                if (club.equals("lagcc") && index.equals("0") && ((day_name.equals("Tuesday") && time >= 800 && time <= 930) || (day_name.equals("Thursday") && time >= 730 && time <= 930))) {  // If Los Altos, Thursday, day of, and is one of the tee times between 7:30am and 9:30am

                    allowCancel = false;       // DO NOT allow cancellations
                }
                              
                if (club.equals("philcricketrecip") && (ind < 2 || (ind == 2 && thisTimeAdjusted >= 1200))) {
                    
                    allowCancel = false;
                }

            } else {

                //
                //  Error getting user field(s) - reject
                //
                out.println("</form></font></td></tr>");
                out.println("</table>");
                out.println("<br><br>");
                out.println("<font size=\"4\"><b>Error</b></font><br>");
                out.println("<font size=\"2\">");
                out.println("An error has occurred that prevents you from continuing.  The session cookie<br>");
                out.println("used by the system has been corrupted.  Please return, logout and then try again.");
                out.println("<br><br>If this continues, please email us at support@foretees.com and include this error message.");
                out.println("<br><br>Error: " + msg);
                out.println("<br><br>");
                out.println("<form action=\"Member_slot\" method=\"post\" name=\"can\">");
                out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
                out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
                out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                out.println("<input type=\"submit\" value=\"Return\" name=\"cancel\"></form>");
                out.println("</td></tr></table>");
                out.println("</center></body></html>");
                out.close();
                //
                //  save message in /" +rev+ "/error.txt
                //
                msg = "Error in Member_slot for " + name + ", user " + user + " at " + club + ".  Error = " + msg;   // build msg
                SystemUtils.logError(msg);                                   // log it
                return;
            }

            if (club.equals("pganj") && newreq) {
                player2 = "Guest";
                player3 = "Guest";
                player4 = "Guest";
                p2cw = "CRT";
                p3cw = "CRT";
                p4cw = "CRT";
            }


        
            //
            //  If mobile user, call common processing (shares with Member_slot)
            //
            if (mobile > 0) {

                parmMobile parmM = new parmMobile();              // allocate a parm block to hold parameters for mobile processing

                // If came from guest tracking prompt, need to look up the name of the guest they selected
                if (req.getParameter("guest_id") != null && !req.getParameter("guest_id").equals("")) {

                    int guest_id = Integer.parseInt(req.getParameter("guest_id"));

                    String guest_slot = req.getParameter("guest_slot");

                    if (guest_id < 0 && guest_id != -99) {

                        if (guest_slot.equals("player1")) {
                            player1 = "";
                            guest_id1 = 0;
                        } else if (guest_slot.equals("player2")) {
                            player2 = "";
                            guest_id2 = 0;
                        } else if (guest_slot.equals("player3")) {
                            player3 = "";
                            guest_id3 = 0;
                        } else if (guest_slot.equals("player4")) {
                            player4 = "";
                            guest_id4 = 0;
                        } else if (guest_slot.equals("player5")) {
                            player5 = "";
                            guest_id5 = 0;
                        }
                    } else {

                        String guest_name = Common_guestdb.getGuestName(guest_id, con);

                        if (guest_id == -99) {
                            guest_id = 0;    // TBA guest
                        }
                        if (guest_slot.equals("player1")) {
                            player1 += guest_name;
                            guest_id1 = guest_id;
                        } else if (guest_slot.equals("player2")) {
                            player2 += guest_name;
                            guest_id2 = guest_id;
                        } else if (guest_slot.equals("player3")) {
                            player3 += guest_name;
                            guest_id3 = guest_id;
                        } else if (guest_slot.equals("player4")) {
                            player4 += guest_name;
                            guest_id4 = guest_id;
                        } else if (guest_slot.equals("player5")) {
                            player5 += guest_name;
                            guest_id5 = guest_id;
                        }
                    }
                }

                // populate parmM with current values
                parmM.type = "teetime";            // indicate this is a Tee Time request
                parmM.mobile = mobile;
                parmM.user = user;
                parmM.day = day_name;
                parmM.time = time;
                parmM.stime = stime;
                parmM.mm = (int) mm;
                parmM.dd = (int) dd;
                parmM.yy = (int) yy;
                parmM.club = club;
                parmM.course = course;
                parmM.date = date;
                parmM.sdate = sdate;
                parmM.fb = fb;
                parmM.index = index;
                parmM.returnCourse = returnCourse;
                parmM.p5 = p5;
                parmM.lottName = "";
                parmM.slots = 1;                    // # of groups requested
                parmM.players = 4;                  // # of players allowed in this request (4 or 5 per group times # of slots selected)
                if (p5.equals("Yes")) {
                    parmM.players = 5;
                }
                parmM.notes = notes;
                parmM.hide = hide;
                parmM.displayOpt = displayOpt;
                parmM.allowCancel = allowCancel;
                parmM.newreq = newreq;
                int maxPlayers = reservationUtil.getMaxPlayersForTeeTime(req, (int)date, time, course, fb, p5.equals("Yes"));
                parmM.twoSomeOnly = maxPlayers == 2;
                parmM.threeSomeOnly = maxPlayers == 3;

                parmM.playerA[0] = player1;              // save player info in parmM
                parmM.playerA[1] = player2;
                parmM.playerA[2] = player3;
                parmM.playerA[3] = player4;
                parmM.playerA[4] = player5;
                parmM.pcwA[0] = p1cw;
                parmM.pcwA[1] = p2cw;
                parmM.pcwA[2] = p3cw;
                parmM.pcwA[3] = p4cw;
                parmM.pcwA[4] = p5cw;
                parmM.p9A[0] = p91;
                parmM.p9A[1] = p92;
                parmM.p9A[2] = p93;
                parmM.p9A[3] = p94;
                parmM.p9A[4] = p95;
                parmM.blockPA[0] = blockP1;
                parmM.blockPA[1] = blockP2;
                parmM.blockPA[2] = blockP3;
                parmM.blockPA[3] = blockP4;
                parmM.blockPA[4] = blockP5;
                parmM.guest_idA[0] = guest_id1;
                parmM.guest_idA[1] = guest_id2;
                parmM.guest_idA[2] = guest_id3;
                parmM.guest_idA[3] = guest_id4;
                parmM.guest_idA[4] = guest_id5;
                parmM.origA[0] = orig1;
                parmM.origA[1] = orig2;
                parmM.origA[2] = orig3;
                parmM.origA[3] = orig4;
                parmM.origA[4] = orig5;
                parmM.custom_int = custom_int;     // for customs

                //
                //  Custom to only allow the tee time originator to add/change/remove any existing notes (case 2293).
                //
                parmM.protect_notes = false;
                
                if (club.equals("baldpeak")) {

                   if (!notes.equals("") && newreq == false && !user.equalsIgnoreCase(orig_by)) {    // if existing notes, and existing tee time that this user did not originate

                      parmM.protect_notes = true;      // protect the notes - do not allow user to add/change/remove the notes
                   }
                }

        
                Common_mobile.doSlot(parm, parmc, parmM, out, con);    // prompt user for players
                return;                                               // exit and wait for user input
            }

            
            


            // Set guest types footer notes:
            if (club.equals("cherryhills")) {
                slotPageParms.guest_type_footer_notes.add("<b>Note:</b> IN-Town guests reside within 70 air miles of the club.");
            }

            // If one of the Golf Academy of America sites (non-classroom), do not display the member/partner/guest/x selection lists. Players can only add themselves.
            if ((club.startsWith("gaa") && !club.endsWith("class")) || club.equals("pgmunl")) {

                skipMembers = true;
                skipGuests = true;
                x = 0;
            }
            //
            //  Check if Guest Type table should be displayed
            //
            if (club.equals("newcanaan") && userMship.equals("Special")) {

                skipGuests = true;
            }
            //
            // Custom for Greenwich - don't allow Guests in twosomes if more than 48 hrs from tee time (Case #1217)
            //
            if (club.equals("greenwich") && visible_players_per_group == 2) {

                //
                //  Get adjusted date/time 48 hours out from now
                //
                Calendar cal2 = new GregorianCalendar();                       // get todays date
                cal2.add(Calendar.HOUR_OF_DAY, 48);                            // roll ahead 48 hours
                int okDate = cal2.get(Calendar.YEAR) * 10000;                  // create a date field of yyyymmdd
                okDate = okDate + ((cal2.get(Calendar.MONTH) + 1) * 100);
                okDate = okDate + cal2.get(Calendar.DAY_OF_MONTH);             // date = yyyymmdd (for comparisons)

                int okTime = (cal2.get(Calendar.HOUR_OF_DAY) * 100) + cal2.get(Calendar.MINUTE);
                okTime = SystemUtils.adjustTime(con, okTime);                  // adjust the time

                if ((okDate > date) || (okDate == date && okTime > time)) {

                    skipGuests = true;
                }
            } // end greenwich custom

            //
            // If Greenwich and specific date/time then don't allow members to use X
            //
            if (club.equals("greenwich")) {
                if ((day_name.equals("Saturday") || day_name.equals("Sunday")
                        || date == Hdate1 || date == Hdate2b || date == Hdate3 || date == Hdate9)
                        && (time > 1131 && time < 1229)) {

                    x = 0;
                }
            }

            if (club.equals("foresthighlands")) {
                x = 0;
            }

            if (club.equals("tcclub")) {
                slotPageParms.member_tbd_text = "Guest";
            }

            if ((club.equals("hawkslandinggolfclub") && hawksLandingCustomMsg)) {
                slotPageParms.slot_footer_notes.add("<b>No Show Policy</b> - Failure to cancel a reserved tee-time up to three (3) hours in advance from that time will result in a charge "
                        + "for the entire tee-time at the applicable rate.  Notice of reduction in players from the reserved tee-time must also be received by "
                        + "the golf shop at least one (1) hour prior to the tee-time, or the person who booked the tee-time will be charged for the spot(s) that "
                        + "were originally reserved and unused for that booking.  This policy is for members and the general public.  The only circumstance "
                        + "when the golf shop will not enforce this policy would be if an open tee-time exists immediately before or after the booking.");
            }

            if (club.equals("cherryhills") || club.equals("tualatincc")) {   // if cherry hills, no default c/w
                slotPageParms.use_default_member_tmode = false;
            }
            if (club.equals("sonnenalp")) {  // Allow member tmode of any type to be added to the tmode list
                slotPageParms.verify_member_tmode = false;
            }
            if (club.equals("cwcpga")) {
                slotPageParms.show_ghin_in_list = true;
            }
            if (club.equals("cordillera")) {
                // Remove any member with m_ship matching regular expression "^Employee.*", with "i" modifier (starts with "Employee", followed by any characters, case insensitive) from name list
                slotPageParms.name_list_filter_map.put("m_ship", new String[]{"^Employee.*", "i"});
            }

        } // end of configure_slot
        
        
        // Complete filling parameters for slot page

        
        //
        //  Custom to only allow the tee time originator to add/change/remove any existing notes (case 2293).
        //
        if (club.equals("baldpeak")) {

           if (!notes.equals("") && newreq == false && !user.equalsIgnoreCase(orig_by)) {    // if existing notes, and existing tee time that this user did not originate

              slotPageParms.protect_notes = true;      // protect the notes - do not allow user to add/change/remove the notes
           }
        }

        
        // Set default course name
        if (club.equals("congressional")) {
            course_disp = congressionalCustom.getFullCourseName(date, (int) dd, course);
        } else {
            course_disp = course;
        }

        slotPageParms.time_remaining = verifySlot.getInUseTimeRemaining(date, time, fb, course, session);
        slotPageParms.hide_notes = hide;
        slotPageParms.show_member_tbd = (x != 0);
        slotPageParms.edit_mode = (newreq == false);
        slotPageParms.show_tbd = (x != 0);
        if (club.equals("congressional") && visible_players_per_group == 2) {
            slotPageParms.default_fb_value = 1; // 0 or 1
            slotPageParms.set_default_fb_value = true;
            slotPageParms.lock_fb = true;
        }
        slotPageParms.allow_cancel = allowCancel;
        slotPageParms.show_member_select = (skipMembers == false);
        slotPageParms.show_guest_types = (skipGuests == false);

        slotPageParms.player_count = players;
        slotPageParms.players_per_group = players_per_group;
        slotPageParms.visible_players_per_group = visible_players_per_group;
        slotPageParms.jump = jump;
        slotPageParms.index = index;
        //slotPageParms.day_name = day_name;

        slotPageParms.fb = fb;
        slotPageParms.slots = 1;

        slotPageParms.yy = (int) yy;
        slotPageParms.mm = (int) mm;
        slotPageParms.dd = (int) dd;

        slotPageParms.course = course;
        slotPageParms.return_course = returnCourse;
        slotPageParms.day = day_name;
        slotPageParms.stime = stime;
        slotPageParms.course_disp = course_disp;
        slotPageParms.sdate = sdate;
        slotPageParms.date = Integer.parseInt(sdate);
        slotPageParms.time = time;
        //slotPageParms.transport_legend = transport_legend;
        slotPageParms.p5 = p5;
        slotPageParms.notes = notes;
        slotPageParms.name = name;

        slotPageParms.pcw = pcw; // User's default PCW

        slotPageParms.guest_id_a = new int[]{guest_id1, guest_id2, guest_id3, guest_id4, guest_id5};
        slotPageParms.p9_a = new int[]{p91, p92, p93, p94, p95};
        slotPageParms.gift_pack_a = new int[]{gp1, gp2, gp3, gp4, gp5};
        //slotPageParms.time_a = slotParms.getIntArrayByName("time%");

        slotPageParms.player_a = new String[]{player1, player2, player3, player4, player5};
        slotPageParms.pcw_a = new String[]{p1cw, p2cw, p3cw, p4cw, p5cw};

        //slotPageParms.tmodes_list = nonProTmodes;  // Tmode that will be displayed
        slotPageParms.allowed_tmodes_list = Arrays.asList(parmc.tmodea);  // Tmode that will be allowed when set as defaults

        slotPageParms.course_parms = parmc;

        // Set players that cannot be editied on form
        slotPageParms.lock_player_a = new boolean[]{blockP1, blockP2, blockP3, blockP4, blockP5};
        // loop over the lock_player array, and set properly
        for (int i2 = 0; i2 < slotPageParms.lock_player_a.length; i2++) {
            if ((!(!slotPageParms.player_a[i2].equals("") || club.equals("pgmunl") || (club.startsWith("gaa") && !club.endsWith("class")))) && slotPageParms.lock_player_a[i2] == true) {
                slotPageParms.lock_player_a[i2] = false; // Allow editing if player is empty, and custom does not match club
            }
        }

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
        
        if (club.equals("oakhillcc") && custom_int > 0) {
           
           slotPageParms.slot_submit_map.put("custom_int", "custom_int");
        }
        
        if (club.equals("philcricket") && !course.equals("Militia Hill")) {
            x = 0;
            slotPageParms.show_member_tbd = false;
            slotPageParms.show_tbd = false;
        }

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
            
// Old skin code commented out - use cmd+/ over selected lines to undo
//            
//            /**************************************
//             * Old Skin Output
//             **************************************/
//            //
//            //  NOT a Mobile user - Build the HTML page to prompt user for names
//            //
//            out.println("<HTML>");
//            out.println("<HEAD>");
//            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
//            out.println("<Title>Member Tee Slot Page</Title>");
//
//            // Json compatibility for older browsers
//            out.println("<script type=\"text/javascript\" src=\"/" + rev + "/assets/scripts/json2.js\"></script>");
//
//            //  Add script code to allow modal windows to be used
//            out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->"
//                    + "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>"
//                    + "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>"
//                    + "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");
//
//            out.println("<script type=\"text/javascript\">");
//            out.println("<!--");
//            out.println("function resizeIFrame(divHeight, iframeName) {");
//            out.println("document.getElementById(iframeName).height = divHeight;");
//            out.println("}");
//            out.println("// -->");
//            out.println("</script>");
//
//            //
//            //*******************************************************************
//            //  User clicked on a letter - submit the form for the letter
//            //*******************************************************************
//            //
//            out.println("<script type=\"text/javascript\">");            // Submit the form when clicking on a letter
//            out.println("<!--");
//            out.println("function subletter(x) {");
//
////      out.println("alert(x);");
//            out.println("document.forms['playerform'].letter.value = x;");         // put the letter in the parm
//            out.println("document.forms['playerform'].submit();");        // submit the form
//            out.println("}");                  // end of script function
//            out.println("// -->");
//            out.println("</script>");          // End of script
//
//            //
//            //*********************************************************************************
//            //  Erase player name (erase button selected next to player's name)
//            //
//            //    Remove the player's name and shift any other names up starting at player1
//            //*********************************************************************************
//            //
//
//            out.println("<script type=\"text/javascript\">");            // Erase name script    (Note:  Put these in file???)  what other files use these scripts, just proshop_slot?
//            out.println("<!--");
//
//            out.println("function erasename(pPlayerPos, pCWoption) {");
//            out.println("var p = eval(\"document.forms['playerform'].\" + pPlayerPos + \";\")");
//            out.println("var o = eval(\"document.forms['playerform'].\" + pCWoption + \";\")");
//            out.println("p.value = '';");        // clear player field
//
//            out.println("var pPlayerPos2 = pPlayerPos.replace('player', 'guest_id');");
//            out.println("document.playerform[pPlayerPos2].value = '0';");
//
//            // remove any Pro-Only tmodes from the wc field
//            out.println("var m=0");
//            out.println("var n=0");
//            out.println("var found = new Boolean(false)");
//            out.println("for (m = o.length - 1; m>=0; m--) {");
//            out.println("found = false;");
//            out.println("for (n=0; n<nonProCount; n++) {");
//            out.println("if (o.options[m].value == nonProTmodes[n]) {");
//            out.println("found = true;");
//            out.println("break;");
//            out.println("}");        // end of if
//            out.println("}");        // end inner for
//            out.println("if (found == false) {");
//            out.println("o.options[m] = null;");
//            out.println("}");        // end if
//            out.println("}");        // end for
//
//            out.println("o.selectedIndex = -1;");        // clear WC field
//            out.println("}");                  // end of script function
//            out.println("// -->");
//            out.println("</script>");          // End of script
//
//            //
//            //*******************************************************************
//            //  Erase text area - (Notes)      erasetext and movenotes
//            //*******************************************************************
//            //
//            out.println("<script type=\"text/javascript\">");            // Erase text area script
//            out.println("<!--");
//            out.println("function erasetext(pos1) {");
//            out.println("eval(\"document.forms['playerform'].\" + pos1 + \".value = '';\")");           // clear the player field
//            out.println("}");                  // end of script function
//            out.println("// -->");
//            out.println("</script>");          // End of script
//
//            out.println("<script type=\"text/javascript\">");             // Move Notes into textarea
//            out.println("<!--");
//            out.println("function movenotes() {");
//            out.println("var oldnotes = document.forms['playerform'].oldnotes.value;");
//            out.println("document.forms['playerform'].notes.value = oldnotes;");   // put notes in text area
//            out.println("}");                  // end of script function
//            out.println("// -->");
//            out.println("</script>");          // End of script
//
//            //
//            //**********************************************************************************
//            //  Add to drop down list - add options to drop down lists
//            //**********************************************************************************
//            //
//            // add option if not already in list (means it was member's default)
//            out.println("<script type=\"text/javascript\">");
//            out.println("function add(e, wc) {");
//            out.println("<!--");
//            out.println("  var i=0;");
//            out.println("  for (i=0;i<e.length;i++) {");
//            out.println("    if (e.options[i].value == wc) {");
//            out.println("      return;");
//            out.println("    }");        // end if
//            out.println("  }");      // end for
//            out.println("  for (i=0;i<tmodeCount;i++) {");
//            out.println("    if (tmodes[i] == wc) {");
//            out.println("      e.options[e.length] = new Option(wc, wc);");
//            out.println("    }");
//            out.println("  }");
//            out.println("}");            // End of function add()
//            out.println("// -->");
//            out.println("</script>");    // End of script
//
//            //
//            //*********************************************************************************
//            //  Move name script
//            //*********************************************************************************
//            //
//            out.println("<script type=\"text/javascript\">");            // Move name script
//            out.println("<!--");
//
//            out.println("function movename(namewc) {");
//
//            out.println("del = ':';");                               // deliminator is a colon
//            out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
//            out.println("var name = array[0];");
//            out.println("var wc = array[1];");
//            out.println("var default_member_wc_override = '" + slotPageParms.default_member_wc_override + "';");
//            out.println("var default_member_wc = '" + slotPageParms.default_member_wc + "';");
//            out.println("var f = document.forms['playerform'];");
//            out.println("skip = 0;");
//
//            out.println("var player1 = f.player1.value;");
//            out.println("var player2 = f.player2.value;");
//
//            if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)
//
//                out.println("var player3 = f.player3.value;");
//                if (threeSomeOnly == false) {               // If tee time NOT restricted to 3-somes (custom requests)
//                    out.println("var player4 = f.player4.value;");
//
//                    if (p5.equals("Yes")) {
//                        out.println("var player5 = f.player5.value;");
//                    }
//                }
//            }
//
//            out.println("if (( name != 'x') && ( name != 'X')) {");
//            out.println("if(default_member_wc_override.length > 0){wc = default_member_wc_override;}");
//            out.println("if(default_member_wc.length > 0 && ((wc == null) || (wc == ''))){wc = default_member_wc;}");
//
//            if (twoSomeOnly == true) {               // If tee time restricted to 2-somes (custom requests)
//                out.println("if (( name == player1) || ( name == player2)) {");
//            } else {
//                if (threeSomeOnly == true) {               // If tee time restricted to 3-somes (custom requests)
//                    out.println("if (( name == player1) || ( name == player2) || ( name == player3)) {");
//                } else {
//                    if (p5.equals("Yes")) {
//                        out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ( name == player5)) {");
//                    } else {
//                        out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4)) {");
//                    }
//                }
//            }
//            out.println("skip = 1;");
//            out.println("}");
//            out.println("}");
//
//            out.println("if (skip == 0) {");
//
//            out.println("if (player1 == '') {");                    // if player1 is empty
//            out.println("f.player1.value = name;");
//            out.println("f.guest_id1.value = '0';");
//            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
//            out.println("add(f.p1cw, wc);");                    // add wc option if Pro Only and player default
//            out.println("f.p1cw.value = wc;");
//            out.println("}");
//            out.println("} else {");
//
//            out.println("if (player2 == '') {");                    // if player2 is empty
//            out.println("f.player2.value = name;");
//            out.println("f.guest_id2.value = '0';");
//            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
//            out.println("add(f.p2cw, wc);");                    // add wc option if Pro Only and player default
//            out.println("f.p2cw.value = wc;");
//            out.println("}");
//
//            if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)
//                out.println("} else {");
//
//                out.println("if (player3 == '') {");                    // if player3 is empty
//                out.println("f.player3.value = name;");
//                out.println("f.guest_id3.value = '0';");
//                out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
//                out.println("add(f.p3cw, wc);");                    // add wc option if Pro Only and player default
//                out.println("f.p3cw.value = wc;");
//                out.println("}");
//                if (threeSomeOnly == false) {               // If tee time NOT restricted to 3-somes (custom requests)
//                    out.println("} else {");
//
//                    out.println("if (player4 == '') {");                    // if player4 is empty
//                    out.println("f.player4.value = name;");
//                    out.println("f.guest_id4.value = '0';");
//                    out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
//                    out.println("add(f.p4cw, wc);");                    // add wc option if Pro Only and player default
//                    out.println("f.p4cw.value = wc;");
//                    out.println("}");
//
//                    if (p5.equals("Yes")) {
//                        out.println("} else {");
//                        out.println("if (player5 == '') {");                    // if player5 is empty
//                        out.println("f.player5.value = name;");
//                        out.println("f.guest_id5.value = '0';");
//                        out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
//                        out.println("add(f.p5cw, wc);");                    // add wc option if Pro Only and player default
//                        out.println("f.p5cw.value = wc;");
//                        out.println("}");
//                        out.println("}");
//                    }
//
//                    out.println("}");
//                }                       // end of IF 3-some only time
//                out.println("}");
//            }                       // end of IF 2-some only time
//            out.println("}");
//            out.println("}");
//
//            out.println("}");                  // end of dup name chack
//
//            out.println("}");                  // end of script function
//            out.println("// -->");
//            out.println("</script>");                               // End of script
//
//            //
//            //*******************************************************************
//            //  Move a Guest Name or 'X' into the tee slot
//            //*******************************************************************
//            //
//            out.println("<script type=\"text/javascript\">");            // Move Guest Name script
//            out.println("<!--");
//
//            out.println("var guestid_slot;");
//            out.println("var player_slot;");
//
//            out.println("var transport_defaults = JSON.parse(unescape('" + StringEscapeUtils.escapeJavaScript(gson_obj.toJson(slotPageParms.guest_type_cw_map)) + "'));");
//            out.println("if(typeof(transport_defaults) != 'object'){transport_defaults = {'_default_':''}}; ");
//
//            out.println("function moveguest(namewc) {");
//
//            out.println("var f = document.forms['playerform'];");
//            //out.println("var name = namewc;");
//
//            out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
//            out.println("var name = array[0];");
//            out.println("var use_guestdb = array[1]");
//
//            out.println("var defCW = transport_defaults[name]");
//
//            out.println("if(typeof(defCW) != 'string'){defCW = transport_defaults['_default_'];}; ");
//            out.println("if(typeof(defCW) != 'string'){defCW = '';}; ");
//
//            // out.println("console.log('defCW:\"'+defCW+'\"');");
//
//            out.println("var player1 = f.player1.value;");
//            out.println("var player2 = f.player2.value;");
//
//            if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)
//
//                out.println("var player3 = f.player3.value;");
//
//                if (threeSomeOnly == false) {               // If tee time NOT restricted to 3-somes (custom requests)
//                    out.println("var player4 = f.player4.value;");
//
//                    if (p5.equals("Yes")) {
//                        out.println("var player5 = f.player5.value;");
//                    }
//                }
//            }
//
//            // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
//            out.println("if (use_guestdb == 1 && (player1 == '' || player2 == '' || player3 == '' || player4 == ''" + (p5.equals("Yes") ? " || player5 == ''" : "") + ")) {");
//            out.println("  loadmodal(0);");
//            out.println("}");
//
//            //  set spc to ' ' if name to move isn't an 'X'
//            out.println("var spc = '';");
//            out.println("if (name != 'X' && name != 'x') {");
//            out.println("   spc = ' ';");
//            out.println("}");
//
//            out.println("if (player1 == '') {");                    // if player1 is empty
//            out.println("if (use_guestdb == 1) {");
//            out.println("player_slot = f.player1;");
//            out.println("guestid_slot = f.guest_id1;");
//            out.println("f.player1.value = name + spc;");
//            out.println("} else {");
//            out.println("f.player1.focus();");                   // here for IE compat
//            out.println("f.player1.value = name + spc;");
//            out.println("f.player1.focus();");
//            out.println("f.p1cw.value = defCW;");
//            out.println("}");
//            out.println("} else {");
//
//            out.println("if (player2 == '') {");                    // if player2 is empty
//            out.println("if (use_guestdb == 1) {");
//            out.println("player_slot = f.player2;");
//            out.println("guestid_slot = f.guest_id2;");
//            out.println("f.player2.value = name + spc;");
//            out.println("} else {");
//            out.println("f.player2.focus();");                   // here for IE compat
//            out.println("f.player2.value = name + spc;");
//            out.println("f.player2.focus();");
//            out.println("}");
//            out.println("f.p2cw.value = defCW;");
//
//            if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)
//
//                out.println("} else {");
//
//                out.println("if (player3 == '') {");                    // if player3 is empty
//                out.println("if (use_guestdb == 1) {");
//                out.println("player_slot = f.player3;");
//                out.println("guestid_slot = f.guest_id3;");
//                out.println("f.player3.value = name + spc;");
//                out.println("} else {");
//                out.println("f.player3.focus();");                   // here for IE compat
//                out.println("f.player3.value = name + spc;");
//                out.println("f.player3.focus();");
//                out.println("}");
//                out.println("f.p3cw.value = defCW;");
//
//                if (threeSomeOnly == false) {               // If tee time NOT restricted to 3-somes (custom requests)
//
//                    out.println("} else {");
//
//                    out.println("if (player4 == '') {");                    // if player4 is empty
//                    out.println("if (use_guestdb == 1) {");
//                    out.println("player_slot = f.player4;");
//                    out.println("guestid_slot = f.guest_id4;");
//                    out.println("f.player4.value = name + spc;");
//                    out.println("} else {");
//                    out.println("f.player4.focus();");                   // here for IE compat
//                    out.println("f.player4.value = name + spc;");
//                    out.println("f.player4.focus();");
//                    out.println("}");
//                    out.println("f.p4cw.value = defCW;");
//
//                    if (p5.equals("Yes")) {
//                        out.println("} else {");
//                        out.println("if (player5 == '') {");                    // if player5 is empty
//                        out.println("if (use_guestdb == 1) {");
//                        out.println("player_slot = f.player5;");
//                        out.println("guestid_slot = f.guest_id5;");
//                        out.println("f.player5.value = name + spc;");
//                        out.println("} else {");
//                        out.println("f.player5.focus();");                   // here for IE compat
//                        out.println("f.player5.value = name + spc;");
//                        out.println("f.player5.focus();");
//                        out.println("}");
//                        out.println("f.p5cw.value = defCW;");
//
//                        out.println("}");
//                    }
//
//                    out.println("}");
//                }
//                out.println("}");
//            }
//            out.println("}");
//            out.println("}");
//
//            out.println("}");                  // end of script function
//            out.println("// -->");
//            out.println("</script>");                               // End of script
//            //*******************************************************************************************
//
//
//            out.println("</HEAD>");
//            out.println("<body onLoad=\"movenotes()\" bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
//            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
//
//            // gather list of Non-Pro-Only tmodes locally
//            /*
//            String[] nonProTmodes = new String[16];
//            int nonProCount = 0;
//            int tmodeCount = 0;
//            
//            for (int j = 0; j < parmc.tmode_limit; j++) {
//            if (parmc.tOpt[j] == 0 && !parmc.tmodea[j].equals("")) {
//            nonProTmodes[nonProCount] = parmc.tmodea[j];
//            nonProCount++;
//            }
//            }
//            // use local list to populate global array in script
//            out.println("<script type=\"text/javascript\">");
//            out.println("<!-- ");
//            out.println("var nonProCount = " + nonProCount + ";");
//            out.println("var nonProTmodes = Array()");
//            for (int j = 0; j < nonProCount; j++) {
//            out.println("nonProTmodes[" + j + "] = \"" + nonProTmodes[j] + "\";");
//            }
//             * 
//             */
//            out.println("<script type=\"text/javascript\">");
//            out.println("<!-- ");
//            out.println("var nonProCount = " + slotPageParms.tmodes_list.size() + ";");
//            out.println("var nonProTmodes = Array()");
//            for (int j = 0; j < slotPageParms.tmodes_list.size(); j++) {
//                out.println("nonProTmodes[" + j + "] = \"" + slotPageParms.tmodes_list.get(j) + "\";");
//            }
//
//            // Create global array of available tmodes for this course
//            out.println("var tmodes = Array()");
//            int tmodeCount = 0;
//            for (int j = 0; j < parmc.tmode_limit; j++) {
//                if (!parmc.tmodea[j].equals("")) {
//                    tmodeCount++;
//                    out.println("tmodes[" + j + "] = \"" + parmc.tmodea[j] + "\";");
//                }
//            }
//            out.println("var tmodeCount = " + tmodeCount + ";");
//            out.println("// -->");
//            out.println("</script>");
//
//            out.println("<table border=\"0\" width=\"100%\" valign=\"top\">");  // large table for whole page
//            out.println("<tr><td valign=\"top\">");
//
//            out.println("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#336633\" align=\"center\" valign=\"top\">");
//            out.println("<tr><td align=\"center\" width=\"160\" bgcolor=\"#F5F5DC\">");
//            out.println("<font color=\"Darkred\" size=\"3\">DO NOT USE");
//            out.println("<br>Your Browser's<br>Back Button!!</font>");
////     out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
//            out.println("</td>");
//
//            out.println("<td align=\"center\">");
//            out.println("<font color=\"#ffffff\" size=\"5\">ForeTees Member Reservation</font>");
//            out.println("</font></td>");
//
//            out.println("<td align=\"center\" width=\"160\">");
//            out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
//            out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
//            out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> " + thisYear + " All rights reserved.");
//            out.println("</font></td>");
//            out.println("</tr></table>");
//
//            out.println("<table width=\"100%\" border=\"0\" align=\"center\">");          // table for main page
//            out.println("<tr><td align=\"center\"><br>");
//
//            out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
//            out.println("<tr>");
//            out.println("<td width=\"620\" align=\"center\">");
//            out.println("<font size=\"2\" color=\"Darkred\">");
//            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this reservation.");
//            out.println("&nbsp; If you want to return without completing a reservation, <b>do not ");
//            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
//            out.println("option below.");
//            out.println("</font></td></tr>");
//            out.println("</table>");
//
//            out.println("<font size=\"2\" color=\"black\">");
//            out.println("<br>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
//            out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Tee Time:&nbsp;&nbsp;<b>" + stime + "</b>");
//            if (!course_disp.equals("")) {
//                out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course_disp + "</b>");
//            }
//            out.println("<br></font>");
//
//            out.println("<table border=\"0\" cellpadding=\"5\" cellspacing=\"5\" align=\"center\">"); // table to contain 4 tables below
//
//            out.println("<tr>");
//            out.println("<td align=\"center\" valign=\"top\">");         // col for Instructions and Go Back button
//
//            out.println("<br><br>");
//            out.println("<font size=\"2\" color=\"Darkred\">");
//            out.println("<form action=\"Member_slot\" method=\"post\" name=\"can\">");
//            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
//            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
//            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
//            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
//            out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
//            out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
//            out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
//            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
//            out.println("Return<br>w/o Changes:<br>");
//            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
//
//            out.println("<br><br><br><br>");
//            out.println("</font><font size=\"1\" color=\"black\">");
//            if (club.equals("oldoaks")) {
//                out.println("<a href=\"#\" onClick=\"window.open ('/" + rev + "/member_help_slot_instructo.htm', 'newwindow', config='Height=560, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
//            } else {
//                out.println("<a href=\"#\" onClick=\"window.open ('/" + rev + "/member_help_slot_instruct.htm', 'newwindow', config='Height=560, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
//            }
//            out.println("<img src=\"/" + rev + "/images/instructions.gif\" border=0>");
//            out.println("<br>Click for Help</a>");
//
//            out.println("</font></td>");
//
//            out.println("<form action=\"Member_slot\" method=\"post\" name=\"playerform\" id=\"playerform\">");
//
//            out.println("<td align=\"center\" valign=\"top\">");
//
//
//            //if (club.equals( "interlachen" )) {   // Interlachen gift pack option for guests
//            //   out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"430\">");  // table for player selection
//            //} else if (club.equals("oaklandhills")) {
//            if (club.equals("oaklandhills")) {
//                out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"500\">");
//            } else {
//                out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"370\">");  // table for player selection
//            }
//            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
//            out.println("<font color=\"#ffffff\" size=\"2\">");
//            out.println("<b>Add or Remove Players</b>&nbsp;&nbsp; Note: Click on Names -->");
//            out.println("</font></td></tr>");
//            out.println("<tr><td align=\"center\">");
//            out.println("<font size=\"2\">");
//
//            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//            if (club.equals("oaklandhills")) {
//                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//            }
//            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
//            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//            if (club.equals("olyclub")) {
//                out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");   // no 9-hole option
//            } else if (club.equals("rollinghillsgc")) {
//                out.println("&nbsp;&nbsp;&nbsp;HDCP&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/" + rev + "/images/9hole.gif\" height=17 width=22>&nbsp;");
//            } else {
//                out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/" + rev + "/images/9hole.gif\" height=17 width=22>&nbsp;");
//            }
//            //if (club.equals( "interlachen" )) {   // Interlachen gift pack option for guests
//            //   out.println("&nbsp;Gift Pack");
//            //} else if (club.equals("oaklandhills")) {
//            if (club.equals("oaklandhills")) {
//                out.println("Guest Bag Tag");
//            }
//            out.println("</b><br>");
//
//            // Print hidden guest_id inputs
//            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
//            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
//            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
//            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
//            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
//
//
//            //
//            //  Player 1
//            //
//            if ((!player1.equals("") || club.equals("pgmunl") || (club.startsWith("gaa") && !club.endsWith("class"))) && blockP1 == true) {    // IF option to not allow mems to change player1
//                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//                out.println("1:&nbsp;<input disabled type=\"text\" id=\"player1\" name=\"player1\" value=\"" + player1 + "\" size=\"20\" maxlength=\"43\">");
//                out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
//
//                out.println("&nbsp;&nbsp;&nbsp;<select disabled size=\"1\" name=\"p1cw\" id=\"p1cw\">");
//                if (p1cw.equals("")) {
//                    out.println("<option style=\"width:25px; height:15px\" value=\"\"></option>");
//                } else {
//                    out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
//
//                }
//                out.println("</select>");
//                out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
//
//                if (p91 == 1 || (club.equals("congressional") && twoSomeOnly == true)) {   // force 9-hole option
//                    out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"p91\" value=\"1\">");
//                    out.println("<input type=\"hidden\" name=\"p91\" value=\"1\">");
//                } else {
//                    out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"p91\" value=\"1\">");
//                }
//
//                // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                    if (gp1 == 1) {
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"gp1\" value=\"1\">");
//                        out.println("<input type=\"hidden\" name=\"gp1\" value=\"1\">");
//                    } else {
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"gp1\" value=\"1\">");
//                    }
//                }
//
//            } else {     // all others
//
//                out.println("<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player1', 'p1cw')\" style=\"cursor:hand\">");
//                out.println("1:&nbsp;<input type=\"text\" id=\"player1\" name=\"player1\" value=\"" + player1 + "\" size=\"20\" maxlength=\"43\">");
//
//                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\" id=\"p1cw\">");
//                out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
//                for (i = 0; i < 16; i++) {        // get all c/w options
//
//                    if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p1cw) && parmc.tOpt[i] == 0) {
//                        out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
//                    }
//                }
//                out.println("</select>");
//
//                if (!club.equals("olyclub")) {
//                    if (p91 == 1 || (club.equals("congressional") && twoSomeOnly == true)) {   // force 9-hole option
//                        out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p91\" value=\"1\">");
//                    } else {
//                        out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p91\" value=\"1\">");
//                    }
//                }
//
//                // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                    if (gp1 == 1) {
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"gp1\" value=\"1\">");
//                    } else {
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"gp1\" value=\"1\">");
//                    }
//                }
//
//            }
//
//            //
//            //  Player 2
//            //
//            out.println("<br>");
//            if ((!player2.equals("") || club.equals("pgmunl") || (club.startsWith("gaa") && !club.endsWith("class"))) && blockP2 == true) {    // if member cannot change this player
//                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//                out.println("2:&nbsp;<input disabled type=\"text\" id=\"player2\" name=\"player2\" value=\"" + player2 + "\" size=\"20\" maxlength=\"43\">");
//                out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
//
//                out.println("&nbsp;&nbsp;&nbsp;<select disabled size=\"1\" name=\"p2cw\" id=\"p2cw\">");
//                if (p2cw.equals("")) {
//                    out.println("<option style=\"width:25px; height:15px\" value=\"\"></option>");
//                } else {
//                    out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
//                }
//                out.println("</select>");
//                out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
//
//                if (p92 == 1 || (club.equals("congressional") && twoSomeOnly == true)) {   // force 9-hole option
//                    out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"p92\" value=\"1\">");
//                    out.println("<input type=\"hidden\" name=\"p92\" value=\"1\">");
//                } else {
//                    out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"p92\" value=\"1\">");
//                }
//
//                // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                    if (gp2 == 1) {
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"gp2\" value=\"1\">");
//                        out.println("<input type=\"hidden\" name=\"gp2\" value=\"1\">");
//                    } else {
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"gp2\" value=\"1\">");
//                    }
//                }
//
//            } else {     // all others
//                out.println("<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player2', 'p2cw')\" style=\"cursor:hand\">");
//                out.println("2:&nbsp;<input type=\"text\" id=\"player2\" name=\"player2\" value=\"" + player2 + "\" size=\"20\" maxlength=\"43\">");
//
//                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\" id=\"p2cw\">");
//
//                out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
//                for (i = 0; i < 16; i++) {
//
//                    if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p2cw) && parmc.tOpt[i] == 0) {
//                        out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
//                    }
//                }
//                out.println("</select>");
//
//                if (!club.equals("olyclub")) {
//                    if (p92 == 1 || (club.equals("congressional") && twoSomeOnly == true)) {   // force 9-hole option
//                        out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p92\" value=\"1\">");
//                    } else {
//                        out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p92\" value=\"1\">");
//                    }
//                }
//
//                // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                    if (gp2 == 1) {
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"gp2\" value=\"1\">");
//                    } else {
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"gp2\" value=\"1\">");
//                    }
//                }
//
//            }
//
//            //
//            //  Custom - check for 2-some only time
//            //
//            if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)
//
//                //
//                //  Player 3
//                //
//                out.println("<br>");
//                if ((!player3.equals("") || club.equals("pgmunl") || (club.startsWith("gaa") && !club.endsWith("class"))) && blockP3 == true) {    // if member cannot change this player
//
//                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//                    out.println("3:&nbsp;<input disabled type=\"text\" id=\"player3\" name=\"player3\" value=\"" + player3 + "\" size=\"20\" maxlength=\"43\">");
//                    out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
//
//                    out.println("&nbsp;&nbsp;&nbsp;<select disabled size=\"1\" name=\"p3cw\" id=\"p3cw\">");
//                    if (p3cw.equals("")) {
//                        out.println("<option style=\"width:25px; height:15px\" value=\"\"></option>");
//                    } else {
//                        out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
//
//                    }
//                    out.println("</select>");
//                    out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
//
//                    if (p93 == 1) {
//                        out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"p93\" value=\"1\">");
//                        out.println("<input type=\"hidden\" name=\"p93\" value=\"1\">");
//                    } else {
//                        out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"p93\" value=\"1\">");
//                    }
//
//                    // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                    if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                        if (gp3 == 1) {
//                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"gp3\" value=\"1\">");
//                            out.println("<input type=\"hidden\" name=\"gp3\" value=\"1\">");
//                        } else {
//                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"gp3\" value=\"1\">");
//                        }
//                    }
//
//                } else {     // all others
//
//                    out.println("<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player3', 'p3cw')\" style=\"cursor:hand\">");
//                    out.println("3:&nbsp;<input type=\"text\" id=\"player3\" name=\"player3\" value=\"" + player3 + "\" size=\"20\" maxlength=\"43\">");
//
//                    out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\" id=\"p3cw\">");
//
//                    out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
//                    for (i = 0; i < 16; i++) {         // get all c/w options
//
//                        if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p3cw) && parmc.tOpt[i] == 0) {
//                            out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
//                        }
//                    }
//                    out.println("</select>");
//
//                    if (!club.equals("olyclub")) {
//                        if (p93 == 1) {
//                            out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p93\" value=\"1\">");
//                        } else {
//                            out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p93\" value=\"1\">");
//                        }
//                    }
//
//                    // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                    if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                        if (gp3 == 1) {
//                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"gp3\" value=\"1\">");
//                        } else {
//                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"gp3\" value=\"1\">");
//                        }
//                    }
//                }
//
//                //
//                //  Custom - check for 3-some only time
//                //
//                if (threeSomeOnly == false) {               // If tee time NOT restricted to 3-somes (custom requests)
//
//                    //
//                    //  Player 4
//                    //
//                    out.println("<br>");
//                    if ((!player4.equals("") || club.equals("pgmunl") || (club.startsWith("gaa") && !club.endsWith("class"))) && blockP4 == true) {    // if member cannot change this player
//
//                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//                        out.println("4:&nbsp;<input disabled type=\"text\" id=\"player4\" name=\"player4\" value=\"" + player4 + "\" size=\"20\" maxlength=\"43\">");
//                        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
//
//                        out.println("&nbsp;&nbsp;&nbsp;<select disabled size=\"1\" name=\"p4cw\" id=\"p4cw\">");
//                        if (p4cw.equals("")) {
//                            out.println("<option style=\"width:25px; height:15px\" value=\"\"></option>");
//                        } else {
//                            out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
//                        }
//                        out.println("</select>");
//                        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
//
//                        if (p94 == 1) {
//                            out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"p94\" value=\"1\">");
//                            out.println("<input type=\"hidden\" name=\"p94\" value=\"1\">");
//                        } else {
//                            out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"p94\" value=\"1\">");
//                        }
//
//                        // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                        if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                            if (gp4 == 1) {
//                                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"gp4\" value=\"1\">");
//                                out.println("<input type=\"hidden\" name=\"gp4\" value=\"1\">");
//                            } else {
//                                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"gp4\" value=\"1\">");
//                            }
//                        }
//
//                    } else {     // all others
//                        out.println("<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player4', 'p4cw')\" style=\"cursor:hand\">");
//                        out.println("4:&nbsp;<input type=\"text\" id=\"player4\" name=\"player4\" value=\"" + player4 + "\" size=\"20\" maxlength=\"43\">");
//
//                        out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\" id=\"p4cw\">");
//
//                        out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
//                        for (i = 0; i < 16; i++) {       // get all c/w options
//
//                            if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p4cw) && parmc.tOpt[i] == 0) {
//                                out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
//                            }
//                        }
//                        out.println("</select>");
//
//                        if (!club.equals("olyclub")) {
//                            if (p94 == 1) {
//                                out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p94\" value=\"1\">");
//                            } else {
//                                out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p94\" value=\"1\">");
//                            }
//                        }
//
//                        // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                        if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                            if (gp4 == 1) {
//                                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"gp4\" value=\"1\">");
//                            } else {
//                                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"gp4\" value=\"1\">");
//                            }
//                        }
//
//                    }
//                    if (p5.equals("Yes")) {
//
//                        //
//                        //  Player 5
//                        //
//                        out.println("<br>");
//                        if ((!player5.equals("") || club.equals("pgmunl") || (club.startsWith("gaa") && !club.endsWith("class"))) && blockP5 == true) {    // if member cannot change this player
//
//                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
//                            out.println("5:&nbsp;<input disabled type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"20\" maxlength=\"43\">");
//                            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
//
//                            out.println("&nbsp;&nbsp;&nbsp;<select disabled size=\"1\" name=\"p5cw\" id=\"p5cw\">");
//                            if (p5cw.equals("")) {
//                                out.println("<option style=\"width:25px; height:15px\" value=\"\"></option>");
//                            } else {
//                                out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
//                            }
//                            out.println("</select>");
//                            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
//
//                            if (p95 == 1) {
//                                out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"p95\" value=\"1\">");
//                                out.println("<input type=\"hidden\" name=\"p95\" value=\"1\">");
//                            } else {
//                                out.println("&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"p95\" value=\"1\">");
//                            }
//
//                            // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                            if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                                if (gp5 == 1) {
//                                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" checked name=\"gp5\" value=\"1\">");
//                                    out.println("<input type=\"hidden\" name=\"gp5\" value=\"1\">");
//                                } else {
//                                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input disabled type=\"checkbox\" name=\"gp5\" value=\"1\">");
//                                }
//                            }
//                        } else {     // all others
//
//                            out.println("<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player5', 'p5cw')\" style=\"cursor:hand\">");
//                            out.println("5:&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"20\" maxlength=\"43\">");
//
//                            out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");
//
//                            out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
//                            for (i = 0; i < 16; i++) {      // get all c/w options
//
//                                if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p5cw) && parmc.tOpt[i] == 0) {
//                                    out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
//                                }
//                            }
//                            out.println("</select>");
//
//                            if (!club.equals("olyclub")) {
//                                if (p95 == 1) {
//                                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p95\" value=\"1\">");
//                                } else {
//                                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p95\" value=\"1\">");
//                                }
//                            }
//
//                            // if (club.equals( "interlachen" ) || club.equals("oaklandhills")) {   // Interlachen gift pack option for guests
//                            if (club.equals("oaklandhills")) {   // Gift pack option for guests
//                                if (gp5 == 1) {
//                                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"gp5\" value=\"1\">");
//                                } else {
//                                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"gp5\" value=\"1\">");
//                                }
//                            }
//                        }
//
//                    } else {
//
//                        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
//                        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
//                    }
//
//                } else {      // 3-some time
//
//                    out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
//                    out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
//                    out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
//                    out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
//                }
//
//            } else {      // 2-some time
//
//                out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
//                out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
//                out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
//                out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
//                out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
//                out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
//            }
//
//            //
//            //   Notes
//            //
//            //   Script will put any existing notes in the textarea (value= doesn't work)
//            //
//            out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script
//
//            // always hide notes for Aliso Viejo - Case #1409
//            if (hide != 0 || club.equals("alisoviejo")) {      // if proshop wants to hide the notes, do not display the text box or notes
//
//                out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">"); // pass existing notes
//
//            } else {
//
//                out.println("<br><br><img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
//                out.println("Notes to Pro:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"22\" rows=\"2\">");
//                out.println("</textarea>");
//            }
//            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
//            out.println("<input type=\"hidden\" name=\"sdate\" value=" + sdate + ">");
//            out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
//            out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
//            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
//            out.println("<input type=\"hidden\" name=\"mm\" value=" + mm + ">");
//            out.println("<input type=\"hidden\" name=\"yy\" value=" + yy + ">");
//            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
//            out.println("<input type=\"hidden\" name=\"skip\" value=\"no\">");
//            out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
//            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
//            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
//            out.println("<input type=\"hidden\" name=\"p5\" value=" + p5 + ">");
//            out.println("<input type=\"hidden\" name=\"hide\" value=" + hide + ">");
//            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
//            out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + custom1 + "\">");
//            out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + custom2 + "\">");
//            out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + custom3 + "\">");
//            out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + custom4 + "\">");
//            out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + custom5 + "\">");
//            out.println("<input type=\"hidden\" name=\"orig1\" value=\"" + orig1 + "\">");
//            out.println("<input type=\"hidden\" name=\"orig2\" value=\"" + orig2 + "\">");
//            out.println("<input type=\"hidden\" name=\"orig3\" value=\"" + orig3 + "\">");
//            out.println("<input type=\"hidden\" name=\"orig4\" value=\"" + orig4 + "\">");
//            out.println("<input type=\"hidden\" name=\"orig5\" value=\"" + orig5 + "\">");
//            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + orig_by + "\">");
//
//            if (club.equals("timarroncc")) {
//                out.println("<input type=\"hidden\" name=\"custom_disp1\" value=\"" + custom_disp1 + "\">");
//                out.println("<input type=\"hidden\" name=\"custom_disp2\" value=\"" + custom_disp2 + "\">");
//                out.println("<input type=\"hidden\" name=\"custom_disp3\" value=\"" + custom_disp3 + "\">");
//                out.println("<input type=\"hidden\" name=\"custom_disp4\" value=\"" + custom_disp4 + "\">");
//                out.println("<input type=\"hidden\" name=\"custom_disp5\" value=\"" + custom_disp5 + "\">");
//            }
//
//            out.println("<br><font size=\"1\">");
//            for (i = 0; i < parmc.tmode_limit; i++) {
//                if (!parmc.tmodea[i].equals("") && parmc.tOpt[i] == 0) {
//
//                    if (club.equals("peninsula") && parmc.tmodea[i].equals("WLK")) {
//
//                        out.println("WLK = <b>Walking Permitted Daily After 3:00 PM</b>&nbsp;&nbsp;&nbsp;");
//
//                    } else {
//
//                        out.println(parmc.tmodea[i] + " = " + parmc.tmode[i] + "&nbsp;&nbsp;");
//                    }
//                }
//            }
//            out.println("</font><br>");
//
//            //
//            //  Check if the 'Cancel Tee Time' button should be allowed
//            //
//            if (allowCancel == true) {
//
//                out.println("<input type=submit value=\"Cancel ENTIRE Tee Time\" name=\"remove\">&nbsp;&nbsp;&nbsp;");
//            }
//
//            if (newreq == false) {            // if this is a change (not a new tee time)
//                out.println("<input type=submit value=\"Submit Changes\" name=\"submitForm\">");
//            } else {
//                out.println("<input type=submit value=\"Submit\" name=\"submitForm\">");
//            }
//            out.println("</font></td></tr>");
//            out.println("</table>");
//
//            if (club.startsWith("tpc") && index.equals("0")) {      // if any TPC Club and today
//
//                out.println("<br>");
//                out.println("<font size=\"2\">");
//                out.println("<b>NOTICE:</b> You cannot cancel a tee time the day of the tee time.<br>Please contact the golf shop if you wish to cancel the entire tee time.");
//                out.println("</font>");
//            }
//
//            if (!club.equals("newcanaan") || !userMship.equals("Special")) {
//
//                out.println("<br>");
//                out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" align=\"left\" width=\"370\">");  // table for guest intructions
//                out.println("<tr><td>");
//                out.println("<font size=\"2\">");
//                out.println("<b>NOTE:</b> &nbsp;");
//                if (club.equals("lakewood")) {
//                    out.println("To add a Guest, click on one of the Guest types listed in the 'Player Options' box to the right. ");
//                } else {
//                    out.println("To add a Guest, click on one of the Guest types listed in the 'Guest Types' box to the right. ");
//                }
//                out.println("Add the guest immediately after the host member. ");
//                out.println("To include the name of a guest, type the name after the guest type word(s) in the player box above.");
//                out.println("</font></td></tr>");
//                out.println("</table>");
//            }
//
//            out.println("</td>");                                // end of table and column
//
//            // ********************************************************************************
//            //   If we got control from user clicking on a letter in the Member List,
//            //   then we must build the name list.
//            // ********************************************************************************
//            String letter = "";
//
//
//            if (!skipMembers) {
//
//                out.println("<td align=\"center\" valign=\"top\">");
//
//                if (req.getParameter("letter") != null && !skipMembers) {     // if user clicked on a name letter
//
//                    letter = req.getParameter("letter");
//
//                    if (!letter.equals("Partner List")) {      // if not Partner List request
//
//                        letter = letter + "%";
//
//                        String first = "";
//                        String mid = "";
//                        String last = "";
//                        String bname = "";
//                        String wname = "";
//                        String dname = "";
//                        String mship = "";
//                        String wc = "";
//                        String ghin = "";
//
//                        out.println("<table border=\"1\" width=\"140\" bgcolor=\"#f5f5dc\" valign=\"top\">");      // name list
//                        out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
//                        out.println("<font color=\"#ffffff\" size=\"2\">");
//                        out.println("<b>Name List</b>");
//                        out.println("</font></td>");
//                        out.println("</tr><tr>");
//                        out.println("<td align=\"center\">");
//                        out.println("<font size=\"2\">");
//                        out.println("Click on name to add");
//                        out.println("</font></td></tr>");
//
//                        try {
//
//                            /*
//                            PreparedStatement stmt2 = con.prepareStatement (
//                            "SELECT name_last, name_first, name_mi, m_ship, wc, ghin FROM member2b " +
//                            "WHERE name_last LIKE ? AND inact = 0 ORDER BY name_last, name_first, name_mi");
//                             */
//
//                            PreparedStatement stmt2 = con.prepareStatement(
//                                    "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, m_ship, wc, ghin "
//                                    + "FROM member2b "
//                                    + "WHERE name_last LIKE ? AND inact = 0 "
//                                    + "ORDER BY last_only, name_first, name_mi");
//
//                            stmt2.clearParameters();               // clear the parms
//                            stmt2.setString(1, letter);            // put the parm in stmt
//                            rs = stmt2.executeQuery();             // execute the prepared stmt
//
//                            out.println("<tr><td align=\"left\"><font size=\"2\">");
//                            out.println("<select size=\"20\" name=\"bname\" onClick=\"movename(this.value)\">"); // movename(this.form.bname.value)
//
//                            while (rs.next()) {
//
//                                last = rs.getString("name_last");
//                                first = rs.getString("name_first");
//                                mid = rs.getString("name_mi");
//                                mship = rs.getString("m_ship");
//                                wc = rs.getString("wc");           // walk/cart preference
//                                ghin = rs.getString("ghin");
//
//                                if (club.equals("cherryhills") || club.equals("tualatincc")) {   // if cherry hills, no default c/w
//
//                                    wc = "";
//
//                                } else {
//
//                                    if (!club.equals("sonnenalp")) {        // skip this if Sonnenalp
//
//                                        i = 0;
//                                        loopi3:
//                                        while (i < 16) {             // make sure wc is supported
//
//                                            if (parmc.tmodea[i].equals(wc)) {
//
//                                                break loopi3;
//                                            }
//                                            i++;
//                                        }
//                                        if (i > 15) {       // if we went all the way without a match
//
//                                            wc = parmc.tmodea[0];    // use default option
//                                        }
//                                    }
//                                }
//
//                                i = 0;
//
//                                if (mid.equals("")) {
//
//                                    bname = first + " " + last;
//                                    dname = last + ", " + first;
//                                } else {
//
//                                    bname = first + " " + mid + " " + last;
//                                    dname = last + ", " + first + " " + mid;
//                                }
//
//                                wname = bname + ":" + wc;              // combine name:wc for script
//
//                                if (club.equals("cordillera")) {
//
//                                    if (!mship.startsWith("Employee")) {       // if not an Employee (skip employees)
//
//                                        out.println("<option value=\"" + wname + "\">" + dname + "</option>");
//                                    }
//                                } else if (club.equals("cwcpga") && !ghin.equals("")) {
//                                    out.println("<option value=\"" + wname + "\">" + dname + " " + ghin + "</option>");
//                                } else {
//                                    out.println("<option value=\"" + wname + "\">" + dname + "</option>");
//                                }
//                            }
//
//                            out.println("</select>");
//                            out.println("</font></td></tr>");
//
//                            stmt2.close();
//                        } catch (Exception ignore) {
//                        }
//
//                        out.println("</table>");
//
//                    }        // end of IF Partner List or letter
//
//                }           // not letter display
//
//                if ((letter.equals("") || letter.equals("Partner List")) && !skipMembers) {  // if no letter or Partner List request
//
//                    alphaTable.displayPartnerList(user, activity_id, 0, con, out);
//
//                }        // end of if letter display
//
//                out.println("</td>");                                      // end of this column
//            }
//
//            if (!skipMembers || x != 0 || !skipGuests) {
//
//                out.println("<td width=\"200\" valign=\"top\">");
//
//                //
//                //   Output the Alphabit Table for Members' Last Names
//                //
//                if (!skipMembers) {
//                    alphaTable.getTable(out, user);
//                }
//
//
//                // call to getClub used to be here
//
//
//
//                if (x != 0) {  // if X supported and NOT Forest Highlands
//
//                    //
//                    //  add a table for 'x'
//                    //
//                    out.println("<font size=\"1\"><br></font>");
//                    out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\">");
//                    out.println("<tr bgcolor=\"#336633\">");
//                    out.println("<td align=\"center\">");
//                    out.println("<font color=\"#FFFFFF\" size=\"2\">");
//                    out.println("<b>" + ((club.equals("tcclub")) ? "Guest" : "Member") + " TBD</b>");
//                    out.println("</font></td>");
//                    out.println("</tr>");
//                    out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
//                    out.println("Use 'X' to reserve a position for a " + ((club.equals("tcclub")) ? "Guest" : "Member") + ".<br>");
//                    out.println("</font></td></tr>");
//                    out.println("<tr><td align=\"left\" bgcolor=\"#FFFFFF\">");
//                    out.println("<font size=\"2\">");
//                    out.println("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onClick=\"moveguest('X')\">X</a>");
//                    out.println("</font></td></tr></table>");      // end of this table
//                }
//
//                if (skipGuests == false) {
//
//                    if (club.equals("interlachen")) {
//
//                        try {
//
//                            //
//                            //  Get the member sub-type for this user
//                            //
//                            PreparedStatement pstmt1 = con.prepareStatement(
//                                    "SELECT msub_type FROM member2b WHERE username = ?");
//
//                            pstmt1.clearParameters();        // clear the parms
//                            pstmt1.setString(1, user);
//                            rs = pstmt1.executeQuery();      // execute the prepared stmt
//
//                            if (rs.next()) {
//
//                                msubtype = rs.getString(1);
//                            }
//
//                            pstmt1.close();
//                        } catch (Exception exc) {
//                        }
//                    }
//
//
//                    //
//                    //  add a table for the Guest Types
//                    //
//                    out.println("<font size=\"1\"><br></font>");
//                    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
//                    out.println("<tr bgcolor=\"#336633\">");
//                    out.println("<td align=\"center\">");
//                    out.println("<font color=\"#FFFFFF\" size=\"2\">");
//                    if (club.equals("lakewood")) {
//                        out.println("<b>Player Options</b>");
//                    } else {
//                        out.println("<b>Guest Types</b>");
//                    }
//                    out.println("</font></td>");
//                    out.println("</tr>");
//
//                    //
//                    //  first we must count how many fields there will be
//                    //
//                    xCount = 0;
//
//                    if (club.equals("olyclub")) {      // if Olympic Club - different guest types per course
//
//                        if (course.equals("Lake")) {
//
//                            xCount = 3;    // Lakes uses 3 types
//
//                        } else if (course.equals("Ocean")) {
//
//                            xCount = 7;    // Ocean uses all 7 types
//
//                        } else {
//
//                            xCount = 3;     // Cliffs uses 3 types
//                        }
//
//                    } else {
//
//                        for (i = 0; i < parm.MAX_Guests; i++) {
//
//                            if (!parm.guest[i].equals("") && parm.gOpt[i] == 0) {   // count the X and guest names
//
//                                xCount++;
//                            }
//                        }
//                    }
//
//                    i = 0;
//                    if (xCount != 0) {                       // if guest names, display them in list
//
//                        if (xCount < 2) {
//
//                            xCount = 2;             // set size to at least 2
//                        }
//                        if (xCount > 8) {
//
//                            xCount = 8;             // set size to no more than 8 showing at once (it will scroll)
//                        }
//                        out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
//                        out.println("<b>**</b> Add guests immediately<br><b>after</b> host member.<br>");
//
//                        if (club.equals("olyclub")) {     // custom to make restricted guest types red and unavailable
//
//                            out.println("Guest types in <b>red</b> are not allowed for this time.<br>");
//
//                            //  set up some parms for testing the guest types below
//                            slotParms.date = date;
//                            slotParms.time = time;
//                            slotParms.fb = fb;
//                            slotParms.course = course;
//                            slotParms.day = day_name;
//                            slotParms.activity_id = 0;
//                            slotParms.members = 1;       // just check if restricted to zero guests per member or tee time
//                            slotParms.oldPlayer1 = "";   // set to empty for verifySlot processing 
//                            slotParms.oldPlayer2 = "";
//                            slotParms.oldPlayer3 = "";
//                            slotParms.oldPlayer4 = "";
//                            slotParms.oldPlayer5 = "";
//                            slotParms.player2 = "";
//                            slotParms.player3 = "";
//                            slotParms.player4 = "";
//                            slotParms.player5 = "";
//                        }
//                        out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
//                        out.println("<select size=\"" + xCount + "\" name=\"xname\" onClick=\"moveguest(this.form.xname.value)\">");
//
//                        for (i = 0; i < parm.MAX_Guests; i++) {
//
//                            if (!parm.guest[i].equals("") && parm.gOpt[i] == 0) {   // if guest name is open for members
//
//                                boolean medinahSkip = false;
//
//                                //
//                                //  If Merion, then skip "A rate Guest" guest type if this member is a "House" mship type
//                                //
//                                if (club.equals("merion") && userMship.equalsIgnoreCase("House")) {
//
//                                    if (parm.guest[i].equalsIgnoreCase("A rate Guest")) {
//
//                                        medinahSkip = true;
//                                    }
//                                }
//
//                                //
//                                //  If Interlachen, then skip "Guest-Centennial" guest type if this member is NOT a "Member Guest Pass" msub type
//                                //
//                                if (club.equals("interlachen") && !msubtype.equalsIgnoreCase("Member Guest Pass")) {
//
//                                    if (parm.guest[i].equalsIgnoreCase("Guest-Centennial")) {
//
//                                        medinahSkip = true;
//                                    }
//                                }
//
//                                //
//                                //  If Royal Oaks Dallas, then skip "Weekday 1" guest type if Friday, Sat or Sun
//                                //
//                                if (club.equals("roccdallas") && (day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday"))) {
//
//                                    if (parm.guest[i].startsWith("Weekday")) {
//
//                                        medinahSkip = true;
//                                    }
//                                }
//
//                                if (club.equals("shadycanyongolfclub")) {
//
//                                    if (((day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday")) && parm.guest[i].equalsIgnoreCase("Tues-Thurs GST"))
//                                            || ((day_name.equals("Monday") || day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday")) && parm.guest[i].equalsIgnoreCase("Fri-Sun/Holiday GST"))) {
//
//                                        medinahSkip = true;
//                                    }
//                                }
//
//                                if (club.equals("willowridgecc")) {
//
//                                    if (((day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday")) && parm.guest[i].equalsIgnoreCase("WkDay GST"))
//                                            || ((day_name.equals("Monday") || day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday")) && parm.guest[i].equalsIgnoreCase("WkEnd GST"))) {
//
//                                        medinahSkip = true;
//                                    }
//                                }
//
//                                if (club.equals("talbotcc")) {
//
//                                    if (((parm.guest[i].equals("Wkday Guest") || parm.guest[i].equals("Fam Wkday")) && (day_name.equals("Saturday") || day_name.equals("Sunday")))
//                                            || (parm.guest[i].equals("Wkend Guest") || parm.guest[i].equals("Fam Wkend")) && (day_name.equals("Monday") || day_name.equals("Tuesday")
//                                            || day_name.equals("Wednesday") || day_name.equals("Thursday") || day_name.equals("Friday"))) {
//
//                                        medinahSkip = true;
//                                    }
//                                }
//
//                                //
//                                //  If Olympic Club, then only display guest types for the specified course
//                                //
//                                if (club.equals("olyclub")) {
//
//                                    if (course.equals("Lake")) {
//
//                                        if (parm.guest[i].startsWith("Coach Staff")
//                                                || parm.guest[i].startsWith("Junior GWL") || parm.guest[i].equals("MHGP Guest Outing")
//                                                || parm.guest[i].startsWith("Member w/Guest") || parm.guest[i].equals("Twilight Gst")) {
//
//                                            medinahSkip = true;
//                                        }
//
//                                    } else if (course.equals("Cliffs")) {
//
//                                        if (parm.guest[i].equals("Clergy") || parm.guest[i].startsWith("Coach Staff")
//                                                || parm.guest[i].startsWith("Junior GWL") || parm.guest[i].equals("MHGP Guest Outing")
//                                                || parm.guest[i].startsWith("Twilight Gst") || parm.guest[i].equals("Unacc Low Fee")
//                                                || parm.guest[i].equals("Unacc Gst") || parm.guest[i].equals("USGA")) {
//
//                                            medinahSkip = true;
//                                        }
//
//                                    } else if (parm.guest[i].equals("Twilight Gst")) {     // Twilight Gst is only used after 4:00 PM
//
//                                        int twilightTime = 0;
//
//                                        Calendar cal2 = new GregorianCalendar();
//
//                                        cal2.add(Calendar.DATE, Integer.parseInt(index));
//
//                                        boolean isDST = false;
//
//                                        if (cal2.get(Calendar.DST_OFFSET) != 0) {
//                                            isDST = true;
//                                        }
//
//                                        if (isDST) {
//
//                                            if (shortDate <= 831) {
//                                                twilightTime = 1600;
//                                            } else {
//                                                twilightTime = 1500;
//                                            }
//
//                                        } else {
//                                            twilightTime = 1400;
//                                        }
//
//                                        if (time < twilightTime) {
//                                            medinahSkip = true;
//                                        }
//                                    }
//
//                                    if ((parm.guest[i].equals("MHGP w/guest") && !userMship.equals("MHGP")) || parm.guest[i].equals("Event Guest")) {
//
//                                        medinahSkip = true;
//                                    }
//                                }      // end of IF olyclub
//
//
//                                //
//                                //  Add guest type to selection list if allowed
//                                //
//                                if (medinahSkip == false) {
//
//                                    if (club.equals("olyclub")) {     // custom to make restricted guest types red and unavailable
//
//                                        slotParms.player1 = parm.guest[i];   // check this guest type
//                                        slotParms.g1 = parm.guest[i];
//                                        boolean olyclubGR = false;
//
//                                        try {
//
//                                            olyclubGR = verifySlot.checkMaxGuests(slotParms, con);   // check this guest type to see if not allowed at all
//
//                                        } catch (Exception ignore) {
//                                        }
//
//                                        if (olyclubGR == true) {    // if restricted - make it Red
//
//                                            out.println("<option disabled value=\"" + parm.guest[i] + "|" + parm.gDb[i] + "\" style=\"color:red\">" + parm.guest[i] + "</option>");
//
//                                        } else {
//
//                                            out.println("</font><font size=\"2\" color=\"black\">");
//                                            out.println("<option value=\"" + parm.guest[i] + "|" + parm.gDb[i] + "\">" + parm.guest[i] + "</option>");
//                                        }
//
//                                    } else {
//
//                                        out.println("<option value=\"" + parm.guest[i] + "|" + parm.gDb[i] + "\">" + parm.guest[i] + "</option>");
//                                    }
//                                }
//                            }
//                        }
//                        out.println("</select>");
//                        if (club.equals("cherryhills")) {
//                            out.println("<br><b>Note:</b> IN-Town guests reside within 70 air miles of the club.");
//                        }
//                        out.println("</font></td></tr></table>");      // end of this table
//
//                    } else {
//
//                        out.println("</table>");      // end the table if none specified
//                    }
//                }            // end of IF skipGuests
//
//                out.println("</td>");             // end of this column
//            }
//
//            out.println("</tr>");
//            out.println("</form>");     // end of playerform
//
//            if (club.equals("hawkslandinggolfclub") && hawksLandingCustomMsg) {
//
//                out.println("<tr><td colspan=\"4\" align=\"left\">");
//                out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" align=\"center\" width=\"760\" cellpadding=\"5\"><tr><td><font size=\"2\">");
//                out.println("<b>No Show Policy</b> - Failure to cancel a reserved tee-time up to three (3) hours in advance from that time will result in a charge "
//                        + "for the entire tee-time at the applicable rate.  Notice of reduction in players from the reserved tee-time must also be received by "
//                        + "the golf shop at least one (1) hour prior to the tee-time, or the person who booked the tee-time will be charged for the spot(s) that "
//                        + "were originally reserved and unused for that booking.  This policy is for members and the general public.  The only circumstance "
//                        + "when the golf shop will not enforce this policy would be if an open tee-time exists immediately before or after the booking.");
//                out.println("</font></td></tr></table>");
//                out.println("</td></tr>");
//            }
//
//            out.println("</table>");      // end of large table containing 4 smaller tables (columns)
//
//            out.println("</font></td></tr>");
//            out.println("</table>");                      // end of main page table
//            //
//            //  End of HTML page
//            //
//            out.println("</td></tr>");
//            out.println("</table>");                      // end of whole page table
//            out.println("</font></body></html>");
//            out.close();
        } // End of old skin
    }  // end of doPost
    
    
    

    // *********************************************************
    //  Process reservation request from Member_slot (HTML)
    // *********************************************************
    private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


        ResultSet rs = null;

        int mobile = 0;              // Mobile user indicator


        //
        //  Get this session's user name
        //
        String user = (String) session.getAttribute("user");
        String fullName = (String) session.getAttribute("name");
        String club = (String) session.getAttribute("club");
        String posType = (String) session.getAttribute("posType");
        String userMship = (String) session.getAttribute("mship");    // get users mship type
        //int activity_id = (Integer) session.getAttribute("activity_id");
        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");

        Map<String, Object> result_map = new LinkedHashMap<String, Object>();
        Map<String, Object> hidden_field_map = new LinkedHashMap<String, Object>();

        Gson gson_obj = new Gson();

        //
        //  get Mobile user indicator
        //
        try {
            mobile = (Integer) session.getAttribute("mobile");
        } catch (Exception ignore) {
        }


        //
        // init all variables
        //
        int thisTime = 0;
        int time = 0;
        int dd = 0;
        int mm = 0;
        int yy = 0;
        int fb = 0;
        //int fb2 = 0;
        //int t_fb = 0;
        int x = 0;
        int xhrs = 0;
        int calYear = 0;
        int calMonth = 0;
        int thisMonth = 0;
        int calDay = 0;
        int calDayWk = 0;
        int calHr = 0;
        int calMin = 0;
        int week_of_year = 0;
        int memNew = 0;
        int memMod = 0;
        //int i = 0;
        int ind = 0;
        int xcount = 0;
        //int year = 0;
        //int month = 0;
        //int dayNum = 0;
        //int mtimes = 0;
        int sendemail = 0;
        int emailNew = 0;
        int emailMod = 0;
        int emailCan = 0;
        //int mems = 0;
        int players = 0;
        //int oldplayers = 0;
        int gi = 0;
        int adv_time = 0;
        int custom_int = 0;
        int customId = 0;
        int msgPlayerCount = 0;
        int todayTime = 0;
        int guest_id1 = 0;
        int guest_id2 = 0;
        int guest_id3 = 0;
        int guest_id4 = 0;
        int guest_id5 = 0;
        int eventType = 0;

        long temp = 0;
        long ldd = 0;
        long date = 0;
        long adv_date = 0;
        //long dateStart = 0;
        //long dateEnd = 0;
        long todayDate = 0;

        String player = "";
        String sfb = "";
        //String sfb2 = "";
        //String course2 = "";
        //String notes = "";
        //String notes2 = "";
        //String rcourse = "";
        //String period = "";
        //String mperiod = "";
        String msg = "";
        String plyr1 = "";
        String plyr2 = "";
        String plyr3 = "";
        String plyr4 = "";
        String plyr5 = "";
        String memberName = "";
        String p9s = "";
        String p1 = "";
        String msgDate = "";
        String custom_string = "";
        String custom_disp1 = "";
        String custom_disp2 = "";
        String custom_disp3 = "";
        String custom_disp4 = "";
        String custom_disp5 = "";
        //String customS1 = "";
        //String customS2 = "";
        //String customS3 = "";
        //String customS4 = "";
        //String customS5 = "";
        String displayOpt = "";              // display option for Mobile devices
        String msgHdr = "";
        String msgBody = "";
        String eventName = "";
        String oldNotes = "";

        boolean error = false;
        boolean skipGuestRest = false;
        //boolean guestError = false;
        boolean oakskip = false;
        boolean congressGstEmail = false;
        boolean sendShadyCanyonNotesEmail = false;
        boolean sendCustomEmail = false;

        //
        //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
        //
        String lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided


        //
        //  Arrays to hold member & guest names to tie guests to members
        //
        String[] memA = new String[5];     // members
        String[] usergA = new String[5];   // guests' associated member (username)

        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(0, con);

        //
        //  parm block to hold the tee time parms
        //
        parmSlot slotParms = new parmSlot();          // allocate a parm block

        //  parm block to hold the course parameters
        parmCourse courseParms = new parmCourse();       // allocate a parm block


        slotParms.hndcp1 = 99;     // init handicaps
        slotParms.hndcp2 = 99;
        slotParms.hndcp3 = 99;
        slotParms.hndcp4 = 99;
        slotParms.hndcp5 = 99;

        //
        // Get all the parameters entered
        //
        String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
        String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
        String smm = req.getParameter("mm");               //  month of tee time
        String syy = req.getParameter("yy");               //  year of tee time
        String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
        slotParms.p5 = req.getParameter("p5");                //  5-somes supported for this slot
        slotParms.course = req.getParameter("course");        //  name of course
        slotParms.returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
        slotParms.player1 = req.getParameter("player1");
        slotParms.player2 = req.getParameter("player2");
        slotParms.player3 = req.getParameter("player3");
        slotParms.player4 = req.getParameter("player4");
        slotParms.player5 = req.getParameter("player5");
        slotParms.p1cw = req.getParameter("p1cw");
        slotParms.p2cw = req.getParameter("p2cw");
        slotParms.p3cw = req.getParameter("p3cw");
        slotParms.p4cw = req.getParameter("p4cw");
        slotParms.p5cw = req.getParameter("p5cw");
        slotParms.guest_id1 = (req.getParameter("guest_id1") != null ? Integer.parseInt(req.getParameter("guest_id1")) : 0);
        slotParms.guest_id2 = (req.getParameter("guest_id2") != null ? Integer.parseInt(req.getParameter("guest_id2")) : 0);
        slotParms.guest_id3 = (req.getParameter("guest_id3") != null ? Integer.parseInt(req.getParameter("guest_id3")) : 0);
        slotParms.guest_id4 = (req.getParameter("guest_id4") != null ? Integer.parseInt(req.getParameter("guest_id4")) : 0);
        slotParms.guest_id5 = (req.getParameter("guest_id5") != null ? Integer.parseInt(req.getParameter("guest_id5")) : 0);
        slotParms.day = req.getParameter("day");                      // name of day
        sfb = req.getParameter("fb");                       // Front/Back indicator
        slotParms.notes = req.getParameter("notes").trim();    // Notes
        slotParms.hides = req.getParameter("hide");            // Hide Notes
        slotParms.custom_int = (req.getParameter("custom_int") != null ? Integer.parseInt(req.getParameter("custom_int")) : 0);

        //
        //  set 9-hole options
        //
        slotParms.p91 = 0;                       // init to 18 holes
        slotParms.p92 = 0;
        slotParms.p93 = 0;
        slotParms.p94 = 0;
        slotParms.p95 = 0;

        if (req.getParameter("p91") != null) {
            slotParms.p91 = Integer.parseInt(req.getParameter("p91"));             // get 9-hole indicators if they were checked
        }
        if (req.getParameter("p92") != null) {
            slotParms.p92 = Integer.parseInt(req.getParameter("p92"));
        }
        if (req.getParameter("p93") != null) {
            slotParms.p93 = Integer.parseInt(req.getParameter("p93"));
        }
        if (req.getParameter("p94") != null) {
            slotParms.p94 = Integer.parseInt(req.getParameter("p94"));
        }
        if (req.getParameter("p95") != null) {
            slotParms.p95 = Integer.parseInt(req.getParameter("p95"));
        }

        /*
        if (club.equals("interlachen")) {               // Interlachen Gift Pack options
        
        customS1 = "0";                          // default to NO gift pack
        customS2 = "0";
        customS3 = "0";
        customS4 = "0";
        customS5 = "0";
        
        if (req.getParameter("gp1") != null) customS1 = req.getParameter("gp1");
        if (req.getParameter("gp2") != null) customS2 = req.getParameter("gp2");
        if (req.getParameter("gp3") != null) customS3 = req.getParameter("gp3");
        if (req.getParameter("gp4") != null) customS4 = req.getParameter("gp4");
        if (req.getParameter("gp5") != null) customS5 = req.getParameter("gp5");
        }
         */


        //
        //  Get the Display Option if specified (Mobile Devices)
        //
        if (req.getParameter("displayOpt") != null) {

            displayOpt = req.getParameter("displayOpt");
        }

        slotParms.displayOpt = displayOpt;            // save for returns


        //
        //  Ensure that there are no null player fields
        //
        if (slotParms.player1 == null) {
            slotParms.player1 = "";
        }
        if (slotParms.player2 == null) {
            slotParms.player2 = "";
        }
        if (slotParms.player3 == null) {
            slotParms.player3 = "";
        }
        if (slotParms.player4 == null) {
            slotParms.player4 = "";
        }
        if (slotParms.player5 == null) {
            slotParms.player5 = "";
        }
        if (slotParms.p1cw == null) {
            slotParms.p1cw = "";
        }
        if (slotParms.p2cw == null) {
            slotParms.p2cw = "";
        }
        if (slotParms.p3cw == null) {
            slotParms.p3cw = "";
        }
        if (slotParms.p4cw == null) {
            slotParms.p4cw = "";
        }
        if (slotParms.p5cw == null) {
            slotParms.p5cw = "";
        }


        //  retrieve course parameters
        try {
            getParms.getTmodes(con, courseParms, slotParms.course);
        } catch (Exception e) {
        }

        //
        //  Convert date & time from string to int
        //
        try {
            date = Long.parseLong(sdate);
            time = Integer.parseInt(stime);
            mm = Integer.parseInt(smm);
            yy = Integer.parseInt(syy);
            fb = Integer.parseInt(sfb);
        } catch (NumberFormatException e) {
            // ignore error
        }

        long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)

        //
        //  convert the index value from string to numeric - save both
        //
        try {
            ind = Integer.parseInt(index);
        } catch (NumberFormatException e) {
        }

        String jump = "0";                     // jump index - default to zero (for _sheet)

        if (req.getParameter("jump") != null) {            // if jump index provided

            jump = req.getParameter("jump");
        }

        //
        //  Get the length of Notes (max length of 254 chars)
        //
        int notesL = 0;

        if (!slotParms.notes.equals("")) {

            notesL = slotParms.notes.length();       // get length of notes
        }

        //
        //   use yy and mm and date to determine dd (from tee time's date)
        //
        temp = yy * 10000;
        temp = temp + (mm * 100);
        ldd = date - temp;            // get day of month from date

        dd = (int) ldd;               // convert to int

        //
        //  put parms in Parameter Object for portability
        //
        slotParms.date = date;
        slotParms.time = time;
        slotParms.mm = mm;
        slotParms.yy = yy;
        slotParms.dd = dd;
        slotParms.fb = fb;
        slotParms.ind = ind;      // index value
        slotParms.sfb = sfb;
        slotParms.jump = jump;
        slotParms.club = club;    // name of club
        slotParms.user = user;    // this user's username


        //
        //  Determine 'ind' value in case we came from Member_teelist or _searchmem (ind = 888 or 999 or 995)
        //
        int indReal = getDaysBetween(date);            // get # of days in between today and the date
        
        //
        //  We need the 'week of year' for customs below
        //
        Calendar cal = new GregorianCalendar();       // get todays date
        cal.set(Calendar.YEAR,yy);                    // set year in cal for tee time date
        cal.set(Calendar.MONTH,mm);                   // set month in cal
        cal.set(Calendar.DAY_OF_MONTH,dd);            // set day in cal
        
        week_of_year = cal.get(Calendar.WEEK_OF_YEAR);  // get the tee time's week of the year

        //
        //  Get today's date
        //
        cal = new GregorianCalendar();                 // get todays date
        calYear = cal.get(Calendar.YEAR);
        calMonth = cal.get(Calendar.MONTH) + 1;
        calDay = cal.get(Calendar.DAY_OF_MONTH);
        calDayWk = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07, 1 = Sun, 2 = Mon, etc. - 0 not used)
        calHr = cal.get(Calendar.HOUR_OF_DAY);         // 24 hr clock (0 - 23)
        calMin = cal.get(Calendar.MINUTE);

        thisMonth = calMonth;                          // save this month

        todayDate = calYear * 10000;                      // create a date field of yyyymmdd
        todayDate = todayDate + (calMonth * 100);
        todayDate = todayDate + calDay;                    // date = yyyymmdd (for comparisons)

        todayTime = (calHr * 100) + calMin;               // hhmm (CT)


        //
        //  Check if this tee slot is still 'in use' and still in use by this user??
        //
        //  This is necessary because the user may have gone away while holding this slot.  If the
        //  slot timed out (system timer), the slot would be marked 'not in use' and another
        //  user could pick it up.  The original holder could be trying to use it now.
        //
        try {

            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT player1, player2, player3, player4, username1, username2, username3, "
                    + "username4, p1cw, p2cw, p3cw, p4cw, in_use, in_use_by, "
                    + "show1, show2, show3, show4, player5, username5, p5cw, show5, "
                    + "lottery, memNew, memMod, orig_by, pos1, pos2, pos3, pos4, pos5, "
                    + "custom_disp1, custom_disp2, custom_disp3, custom_disp4, custom_disp5, custom_string, custom_int, "
                    + "tflag1, tflag2, tflag3, tflag4, tflag5, teecurr_id, "
                    + "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, "
                    + "orig1, orig2, orig3, orig4, orig5, event, event_type, notes "
                    + "FROM teecurr2 "
                    + "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt
            pstmt.setInt(2, time);
            pstmt.setInt(3, fb);
            pstmt.setString(4, slotParms.course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

                slotParms.oldPlayer1 = rs.getString(1);
                slotParms.oldPlayer2 = rs.getString(2);
                slotParms.oldPlayer3 = rs.getString(3);
                slotParms.oldPlayer4 = rs.getString(4);
                slotParms.oldUser1 = rs.getString(5);
                slotParms.oldUser2 = rs.getString(6);
                slotParms.oldUser3 = rs.getString(7);
                slotParms.oldUser4 = rs.getString(8);
                slotParms.oldp1cw = rs.getString(9);
                slotParms.oldp2cw = rs.getString(10);
                slotParms.oldp3cw = rs.getString(11);
                slotParms.oldp4cw = rs.getString(12);
                slotParms.in_use = rs.getInt(13);
                slotParms.in_use_by = rs.getString(14);
                slotParms.show1 = rs.getShort(15);
                slotParms.show2 = rs.getShort(16);
                slotParms.show3 = rs.getShort(17);
                slotParms.show4 = rs.getShort(18);
                slotParms.oldPlayer5 = rs.getString(19);
                slotParms.oldUser5 = rs.getString(20);
                slotParms.oldp5cw = rs.getString(21);
                slotParms.show5 = rs.getShort(22);
                slotParms.lottery = rs.getString(23);
                memNew = rs.getInt(24);
                memMod = rs.getInt(25);
                slotParms.orig_by = rs.getString(26);
                slotParms.pos1 = rs.getShort(27);
                slotParms.pos2 = rs.getShort(28);
                slotParms.pos3 = rs.getShort(29);
                slotParms.pos4 = rs.getShort(30);
                slotParms.pos5 = rs.getShort(31);
                custom_disp1 = rs.getString("custom_disp1");            // for customs
                custom_disp2 = rs.getString("custom_disp2");
                custom_disp3 = rs.getString("custom_disp3");
                custom_disp4 = rs.getString("custom_disp4");
                custom_disp5 = rs.getString("custom_disp5");
                slotParms.orig1 = rs.getString("orig1");
                slotParms.orig2 = rs.getString("orig2");
                slotParms.orig3 = rs.getString("orig3");
                slotParms.orig4 = rs.getString("orig4");
                slotParms.orig5 = rs.getString("orig5");
                custom_string = rs.getString("custom_string");
                custom_int = rs.getInt("custom_int");
                slotParms.tflag1 = rs.getString("tflag1");
                slotParms.tflag2 = rs.getString("tflag2");
                slotParms.tflag3 = rs.getString("tflag3");
                slotParms.tflag4 = rs.getString("tflag4");
                slotParms.tflag5 = rs.getString("tflag5");
                slotParms.teecurr_id = rs.getInt("teecurr_id");
                slotParms.oldguest_id1 = rs.getInt("guest_id1");
                slotParms.oldguest_id2 = rs.getInt("guest_id2");
                slotParms.oldguest_id3 = rs.getInt("guest_id3");
                slotParms.oldguest_id4 = rs.getInt("guest_id4");
                slotParms.oldguest_id5 = rs.getInt("guest_id5");
                eventName = rs.getString("event");
                eventType = rs.getInt("event_type");
                oldNotes = rs.getString("notes");
            }
            pstmt.close();

            if (club.equals("oaklandhills")) {

                // Clear custom_disp fields
                custom_disp1 = "";
                custom_disp2 = "";
                custom_disp3 = "";
                custom_disp4 = "";
                custom_disp5 = "";

                // See if any are still checked
                if (req.getParameter("gp1") != null) {
                    custom_disp1 = req.getParameter("gp1");
                } else if (req.getParameter("custom1") != null) {
                    custom_disp1 = req.getParameter("custom1");
                }

                if (req.getParameter("gp2") != null) {
                    custom_disp2 = req.getParameter("gp2");
                } else if (req.getParameter("custom2") != null) {
                    custom_disp2 = req.getParameter("custom2");
                }

                if (req.getParameter("gp3") != null) {
                    custom_disp3 = req.getParameter("gp3");
                } else if (req.getParameter("custom3") != null) {
                    custom_disp3 = req.getParameter("custom3");
                }

                if (req.getParameter("gp4") != null) {
                    custom_disp4 = req.getParameter("gp4");
                } else if (req.getParameter("custom4") != null) {
                    custom_disp4 = req.getParameter("custom4");
                }

                if (req.getParameter("gp5") != null) {
                    custom_disp5 = req.getParameter("gp5");
                } else if (req.getParameter("custom5") != null) {
                    custom_disp5 = req.getParameter("custom5");
                }
            }

            if (slotParms.orig_by.equals("")) {    // if originator field still empty

                slotParms.orig_by = user;             // set this user as the originator
            }

            if ((slotParms.in_use == 0) || (!slotParms.in_use_by.equalsIgnoreCase(user))) {    // if time slot NOT in use OR not by this user

                if (mobile == 0) {       // if not mobile user

                    out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
                    out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system.<BR>");
                    out.println("<BR>The system timed out and released the tee time.");
                    out.println("<BR><BR>");
                    if (index.equals("999")) {      // if from Member_teelist

                        out.println("<font size=\"2\">");
                        out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain.htm\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");

                    } else {

                        if (index.equals("995")) {      // if from Member_teelist_list (old)

                            out.println("<font size=\"2\">");
                            out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain2.htm\">");
                            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                            out.println("</form></font>");

                        } else {

                            if (index.equals("888")) {       // if from Member_searchmem

                                out.println("<font size=\"2\">");
                                out.println("<form method=\"get\" action=\"/" + rev + "/member_searchmem.htm\">");
                                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                out.println("</form></font>");

                            } else {

                                out.println("<font size=\"2\">");
                                out.println("<form method=\"get\" action=\"/" + rev + "/member_selmain.htm\">");
                                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                out.println("</form></font>");
                            }
                        }
                    }
                    out.println("</CENTER></BODY></HTML>");

                } else {        // Mobile user

                    out.println(SystemUtils.HeadTitleMobile("ForeTees Request"));
                    out.println(SystemUtils.BannerMobile());

                    out.println("<div class=\"content\">");
                    out.println("<div class=\"headertext\">");    // output the heading
                    out.println("Reservation Timer Expired");
                    out.println("</div>");

                    out.println("<div class=\"smheadertext\">Sorry, the request has timed out.<BR>Please try again.</div>");

                    out.println("<ul>");

                    if (index.equals("995")) {         // if came from Member_teelist_list

                        out.println("<li>");
                        out.println("<form action=\"Member_teelist_mobile\" method=\"get\">");
                        out.println("<input type=\"submit\" value=\"Return to List\"></form>");
                        out.println("</li>");

                    } else {

                        out.println("<li>");
                        out.println("<form action=\"Member_sheet\" method=\"post\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                        out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                        out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
                        out.println("</li>");
                    }

                    out.println("</ul></div>");
                    out.println("</body></html>");
                }
                out.close();
/*
                //
                //  If Gallery Golf and early morning, then log this to see how many members are being rejected.
                //  They claim multiple members are getting the same tee time, adding players, then getting rejected.
                //
                if (club.equals("gallerygolf") && todayTime < 800 && todayTime < 830) {   // log it if 8:00 - 8:30 CT

                    SystemUtils.logError("Gallery Golf Error - Member tee time rejected (_slot). User=" + user + ", In-use_by=" + slotParms.in_use_by + ", In-use=" + slotParms.in_use + ", Date=" + date + ", Time=" + time + ", Course=" + slotParms.course);
                }
*/
                return;
            }
        } catch (Exception e) {

            msg = "Check if busy. ";

            dbError(out, e, msg);
            return;
        }

        //
        //  If Congressional, save the 'days in adv' value in custom_int if this is a new tee time request (may not be needed any longer 3/08/12 BP)
        //
        if (club.equals("congressional")) {

            if (slotParms.oldPlayer1.equals("") && slotParms.oldPlayer2.equals("") && slotParms.oldPlayer3.equals("")
                    && slotParms.oldPlayer4.equals("") && slotParms.oldPlayer5.equals("")) {

                custom_int = ind;
            }
        }

        //
        //  Save the custom fields in slotParms in case they are needed elsewhere
        //
        slotParms.custom_string = custom_string;
        slotParms.custom_int = custom_int;
        slotParms.custom_disp1 = custom_disp1;
        slotParms.custom_disp2 = custom_disp2;
        slotParms.custom_disp3 = custom_disp3;
        slotParms.custom_disp4 = custom_disp4;
        slotParms.custom_disp5 = custom_disp5;


        // Merion - don't allow members to cancel tee times within 48 hrs of the tee time (Case #1234)
        boolean disallowCancel = false; // set default
        if (club.equals("merion")) {

            //
            //  Get adjusted date/time 48 hours out from now
            //
            Calendar cal2 = new GregorianCalendar();                       // get todays date
            cal2.add(Calendar.HOUR_OF_DAY, 48);                            // roll ahead 48 hours
            int okDate = cal2.get(Calendar.YEAR) * 10000;                  // create a date field of yyyymmdd
            okDate = okDate + ((cal2.get(Calendar.MONTH) + 1) * 100);
            okDate = okDate + cal2.get(Calendar.DAY_OF_MONTH);             // date = yyyymmdd (for comparisons)

            int okTime = (cal2.get(Calendar.HOUR_OF_DAY) * 100) + cal2.get(Calendar.MINUTE);
            okTime = SystemUtils.adjustTime(con, okTime);                  // adjust the time

            if ((okDate > date) || (okDate == date && okTime > time)) {

                disallowCancel = true;
            }

        }

        if (club.equals("loscoyotes")) {      // if Los Coyotes and after 3/16/08

            disallowCancel = true;
        }

        // If Naperville CC and time was booked as an advance guest time, do not allow members to cancel within the normal booking window (prevent them from gaming the system)
        if (club.equals("napervillecc") && custom_int > 7 && ind <= 7) {

            disallowCancel = true;
        }


        //
        //  If request is to 'Cancel This Res', then clear all fields for this slot
        //
        //  First, make sure user is already on tee slot or originated it for unaccompanied guests
        //
        if (req.getParameter("remove") != null) {

            if (disallowCancel == false) {      // allow cancel if not Los Coyotes and not Merion (set above)

                try {
                    PreparedStatement pstmt4 = con.prepareStatement(
                            "SELECT player1, player2, player3, player4, player5 "
                            + "FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                            + "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ? OR orig_by = ?) "
                            + "AND date = ? AND time = ? AND fb = ? AND courseName = ?");


                    pstmt4.clearParameters();        // clear the parms
                    pstmt4.setString(1, user);
                    pstmt4.setString(2, user);
                    pstmt4.setString(3, user);
                    pstmt4.setString(4, user);
                    pstmt4.setString(5, user);
                    pstmt4.setString(6, user);
                    pstmt4.setString(7, user);
                    pstmt4.setString(8, user);
                    pstmt4.setString(9, user);
                    pstmt4.setString(10, user);
                    pstmt4.setString(11, user);
                    pstmt4.setLong(12, date);
                    pstmt4.setInt(13, time);
                    pstmt4.setInt(14, fb);
                    pstmt4.setString(15, slotParms.course);
                    rs = pstmt4.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        plyr1 = rs.getString(1);
                        plyr2 = rs.getString(2);
                        plyr3 = rs.getString(3);
                        plyr4 = rs.getString(4);
                        plyr5 = rs.getString(5);

                    } else {

                        msgHdr = "Procedure Error";

                        msgBody = "You cannot cancel a reservation unless you are part of that reservation.<BR><BR>Please click the Go Back button if you wish to return to the tee sheet.";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }

                    pstmt4.close();

                } catch (Exception e4) {

                    msg = "Check user on tee time. ";

                    dbError(out, e4, msg);
                    return;
                }

                //
                //  See if we need to ask for a confirmation
                //
                players = 0;

                if (!plyr1.equals("") && !plyr1.equalsIgnoreCase("x")) {       // if member name or guest

                    players++;
                }
                if (!plyr2.equals("") && !plyr2.equalsIgnoreCase("x")) {

                    players++;
                }
                if (!plyr3.equals("") && !plyr3.equalsIgnoreCase("x")) {

                    players++;
                }
                if (!plyr4.equals("") && !plyr4.equalsIgnoreCase("x")) {

                    players++;
                }
                if (!plyr5.equals("") && !plyr5.equalsIgnoreCase("x")) {

                    players++;
                }

                //
                //  Now see if this action has been confirmed yet
                //
                if ((req.getParameter("ack_remove") != null) || (players < 2)) {  // if remove has been confirmed or 1 player

                    String notes = slotParms.notes;

                    slotParms.player1 = "";                  // set reservation fields to null
                    slotParms.player2 = "";
                    slotParms.player3 = "";
                    slotParms.player4 = "";
                    slotParms.player5 = "";
                    slotParms.p1cw = "";
                    slotParms.p2cw = "";
                    slotParms.p3cw = "";
                    slotParms.p4cw = "";
                    slotParms.p5cw = "";
                    slotParms.user1 = "";
                    slotParms.user2 = "";
                    slotParms.user3 = "";
                    slotParms.user4 = "";
                    slotParms.user5 = "";
                    slotParms.userg1 = "";
                    slotParms.userg2 = "";
                    slotParms.userg3 = "";
                    slotParms.userg4 = "";
                    slotParms.userg5 = "";
                    slotParms.guest_id1 = 0;
                    slotParms.guest_id2 = 0;
                    slotParms.guest_id3 = 0;
                    slotParms.guest_id4 = 0;
                    slotParms.guest_id5 = 0;
                    slotParms.show1 = 0;
                    slotParms.show2 = 0;
                    slotParms.show3 = 0;
                    slotParms.show4 = 0;
                    slotParms.show5 = 0;
                    slotParms.pos1 = 0;
                    slotParms.pos2 = 0;
                    slotParms.pos3 = 0;
                    slotParms.pos4 = 0;
                    slotParms.pos5 = 0;
                    slotParms.notes = "";
                    slotParms.mNum1 = "";
                    slotParms.mNum2 = "";
                    slotParms.mNum3 = "";
                    slotParms.mNum4 = "";
                    slotParms.mNum5 = "";
                    slotParms.orig_by = "";
                    slotParms.p91 = 0;
                    slotParms.p92 = 0;
                    slotParms.p93 = 0;
                    slotParms.p94 = 0;
                    slotParms.p95 = 0;
                    slotParms.custom_disp1 = "";
                    slotParms.custom_disp2 = "";
                    slotParms.custom_disp3 = "";
                    slotParms.custom_disp4 = "";
                    slotParms.custom_disp5 = "";
                    slotParms.custom_int = 0;
                    slotParms.tflag1 = "";
                    slotParms.tflag2 = "";
                    slotParms.tflag3 = "";
                    slotParms.tflag4 = "";
                    slotParms.tflag5 = "";
                    slotParms.orig1 = "";
                    slotParms.orig2 = "";
                    slotParms.orig3 = "";
                    slotParms.orig4 = "";
                    slotParms.orig5 = "";

                    emailCan = 1;      // send email notification for Cancel Request
                    sendemail = 1;

                    memMod++;      // increment number of mods for reports

                    //
                    //  if Oakmont, init the custom field used to track the month tee time was made
                    //
                    if (club.equals("oakmont")) {

                        custom_int = 0;
                    }

                    //
                    //  Oak Hill CC - track the Cancel if this tee time was an advance tee time
                    //
                    if (club.equals("oakhillcc") && custom_int > 0) {

                        verifyCustom.logOakhillAdvGst(slotParms.teecurr_id, slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                                       slotParms.player4, slotParms.player5, user, fullName, slotParms.notes, 3, con);

                        custom_int = 0;
                    }


                    //
                    //  If Congressional, check for any Non Local Guest types in the tee time.
                    //     If so and if less than 10 days in adv, then let the pro know via email.
                    //
                   /*        No longer used as of 3/07/12 BP
                    * 
                    if (club.equals("congressional") && indReal < 10) {

                        if (plyr2.startsWith("Non Local Guest") || plyr3.startsWith("Non Local Guest")
                                || plyr4.startsWith("Non Local Guest") || plyr5.startsWith("Non Local Guest")) {

                            if (custom_int > 9) {                // if tee time was originally created more than 9 days in advance

                                congressGstEmail = true;          // send email to pro
                            }
                        }

                        custom_int = 0;                         // reset the days in adv for the next time
                    }
                    */


                    if (club.equals("longcove")) {        // if Long Cove fill in the tee time so only proshop can change

                        //
                        // Do Not fill with X's if between Sunday 6:30 PM and Tues 7:00 AM (ET) or 14 days in advance from 7:00am-7:30am
                        //
                        boolean lcFill = true;                     // default to fill

                        if (/*(calDayWk == 1 && todayTime > 1729) || calDayWk == 2 || (calDayWk == 3 && todayTime < 600) ||*/(slotParms.ind == 14 && todayTime >= 600 && todayTime <= 630)) {

                            lcFill = false;
                        }

                        if (lcFill == true) {

                            slotParms.player1 = "X";
                            slotParms.player2 = "X";
                            slotParms.player3 = "X";
                            slotParms.player4 = "X";
                        }
                    }

                    if (club.equals("shadycanyongolfclub") && !notes.equals("")) {

                        sendShadyCanyonNotesEmail = true;
                    }


                } else {    // not acked yet - display confirmation page

                    if (mobile == 0) {     // if NOT Mobile

                        out.println(SystemUtils.HeadTitle("Cancel Tee Time Confirmation Prompt"));
                        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><font size=\"6\" color=\"red\"><b>***WARNING***</b><BR>");
                        out.println("</font><font size=\"3\"><BR>This will remove ALL players from the tee time.<BR>");
                        out.println("<BR>If this is what you want to do, then click on 'Continue' below.<BR>");
                        out.println("<BR>");

                    } else {

                        //
                        //  Mobile user
                        //
                        out.println(SystemUtils.HeadTitleMobile("ForeTees Request List"));
                        out.println(SystemUtils.BannerMobile());
                        out.println("<div class=\"content\">");
                        out.println("<div class=\"headertext\">***WARNING***</div>");    // output the heading
                        out.println("<div class=\"smheadertext\">This will remove ALL players from the tee time.<BR>If this is what you want to do, then click on 'Continue' below.</div>");
                        out.println("<ul><li>");
                    }

                    out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"remove\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"ack_remove\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                    out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                    out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                    out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                    out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                    out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                    out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                    out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                    out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                    out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                    out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                    out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                    out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                    out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                    out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
                    out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
                    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                    out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                    out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
                    out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                    out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                    out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                    out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
                    out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                    out.println("<input type=\"submit\" value=\"Continue\" name=\"submitForm\"></form>");

                    if (mobile == 0) {     // if NOT Mobile

                        out.println("<BR>If you only want to remove yourself, or a portion of the players,<BR>");
                        out.println("click on 'Return' below. Then use the 'erase' and 'Submit' buttons<BR>");
                        out.println("to remove only those players you wish to remove.<BR>");
                        out.println("<BR>");

                    } else {

                        out.println("</li></ul>");
                        out.println("<div class=\"smheadertext\">If you only want to remove yourself, or a portion of the players, click on 'Return' below. Then use the 'erase' and 'Submit' buttons to remove only those players you wish to remove.</div>");
                    }

                    returnToSlot(mobile, out, slotParms);

                    return;    // wait for acknowledgement
                }

            } else {

                //
                //  Merion - do not allow members to cancel a tee time within 48 hours of time.
                //  Long Cove - prior to 3/17/2008 - do not allow members to cancel
                //  Los Coyotes - do not allow members to cancel
                //
                if (mobile == 0) {       // if not mobile user

                    out.println(SystemUtils.HeadTitle("Member Tee Slot Page"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center><img src=\"/" + rev + "/images/foretees.gif\"><hr width=\"40%\">");
                    out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<p>&nbsp;</p>");
                    out.println("<p>&nbsp;Sorry, you must call the golf shop to cancel this tee time.<br><br>");
                    if (club.equals("merion")) {
                        out.println("(610) 642-5600");
                    }
                    out.println("</p><p>&nbsp;</p></font>");

                    if (index.equals("999")) {         // if came from Member_teelist

                        out.println("<font size=\"2\">");
                        out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain.htm\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");

                    } else {

                        if (index.equals("995")) {         // if came from Member_teelist_list (old)

                            out.println("<font size=\"2\">");
                            out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain2.htm\">");
                            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                            out.println("</form></font>");

                        } else {

                            if (index.equals("888")) {       // if from Member_searchmem

                                out.println("<font size=\"2\">");
                                out.println("<form method=\"get\" action=\"/" + rev + "/member_searchmem.htm\">");
                                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                out.println("</form></font>");

                            } else {                                // return to Member_sheet - must rebuild frames first

                                out.println("<font size=\"2\">");
                                out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
                                out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                                out.println("</form></font>");
                            }
                        }
                    }
                    out.println("</center></font></body></html>");

                } else {        // Mobile user

                    out.println(SystemUtils.HeadTitleMobile("ForeTees Request"));
                    out.println(SystemUtils.BannerMobile());

                    out.println("<div class=\"content\">");
                    out.println("<div class=\"headertext\">");    // output the heading
                    out.println("Reservation Timer Expired");
                    out.println("</div>");

                    out.println("<div class=\"smheadertext\">Sorry, you must call the golf shop to cancel this tee time.</div>");
                    if (club.equals("merion")) {
                        out.println("<div class=\"smheadertext\">(610) 642-5600</div>");
                    }

                    out.println("<ul>");

                    if (index.equals("995")) {         // if came from Member_teelist_list

                        out.println("<li>");
                        out.println("<form action=\"Member_teelist_mobile\" method=\"get\">");
                        out.println("<input type=\"submit\" value=\"Return to List\"></form>");
                        out.println("</li>");

                    } else {

                        out.println("<li>");
                        out.println("<form action=\"Member_sheet\" method=\"post\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                        out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                        out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
                        out.println("</li>");
                    }

                    out.println("</ul></div>");
                    out.println("</body></html>");
                }
                out.close();

                //
                //  Clear the 'in_use' flag for this time slot in teecurr
                //
                try {

                    PreparedStatement pstmt1 = con.prepareStatement(
                            "UPDATE teecurr2 SET in_use = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                    pstmt1.clearParameters();        // clear the parms
                    pstmt1.setLong(1, date);         // put the parm in pstmt1
                    pstmt1.setInt(2, time);
                    pstmt1.setInt(3, fb);
                    pstmt1.setString(4, slotParms.course);
                    pstmt1.executeUpdate();      // execute the prepared stmt

                    pstmt1.close();

                } catch (Exception ignore) {
                    return;
                }
                return;
            }                // end of IF Long Cove and Cancel processing (remove)

        } else {        //  not a 'Cancel Tee Time' request

            //
            //  Normal request -
            //
            //   Get the guest names and other parms specified for this club
            //
            try {
                parm.club = club;                   // set club name
                parm.course = slotParms.course;     // and course

                getClub.getParms(con, parm);        // get the club parms

                x = parm.x;
                xhrs = parm.xhrs;                      // save for later tests
                slotParms.rnds = parm.rnds;
                slotParms.hrsbtwn = parm.hrsbtwn;
            } catch (Exception ignore) {
            }

            //
            //  if Forest Highlands, do not allow any X's (only pros can use them)
            //
            if (club.equals("foresthighlands")) {

                x = 0;
            }


            //
            //  Shift players up if any empty spots
            //
            verifySlot.shiftUp(slotParms);


            //
            // If mobile user then check to see if each player name contains a pipe (delim separating the tmode)
            //
            if (mobile == 1) {

                if (slotParms.player1.indexOf("|") != -1) {

                    slotParms.p1cw = getPlayerCW(slotParms.player1);
                    slotParms.player1 = getPlayerName(slotParms.player1);
                }

                if (slotParms.player2.indexOf("|") != -1) {

                    slotParms.p2cw = getPlayerCW(slotParms.player2);
                    slotParms.player2 = getPlayerName(slotParms.player2);
                }
                if (slotParms.player3.indexOf("|") != -1) {

                    slotParms.p3cw = getPlayerCW(slotParms.player3);
                    slotParms.player3 = getPlayerName(slotParms.player3);
                }
                if (slotParms.player4.indexOf("|") != -1) {

                    slotParms.p4cw = getPlayerCW(slotParms.player4);
                    slotParms.player4 = getPlayerName(slotParms.player4);
                }
                if (slotParms.player5.indexOf("|") != -1) {

                    slotParms.p5cw = getPlayerCW(slotParms.player5);
                    slotParms.player5 = getPlayerName(slotParms.player5);
                }

            }


            //
            //  Check if any player names are guest names
            //
            try {

                verifySlot.parseGuests(slotParms, con);

            } catch (Exception ignore) {
            }

            //
            //  Reject if any player is a guest type that uses the guest tracking system, but the guest_id is blank or doesn't match the guest name entered
            //
            if (!slotParms.gplayer.equals("") && slotParms.hit4 == true) {                      // if error was name doesn't match guest_id

                msgHdr = "Data Entry Error";

                if (mobile == 0) {
                    
                    msgBody = "<BR><BR><b>" + slotParms.gplayer + "</b> appears to have been manually entered or "
                            + "<br>modified after selecting a different guest from the Guest Selection window."
                            + "<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' "
                            + "<BR>next to the current guest's name, then click the desired guest type from the Guest "
                            + "<BR>Types list, and finally select a guest from the displayed guest selection window.";
                } else {       
                    
                    msgBody = "<BR><BR>The selected guest type requires a name. Please select a name or TBA.";     // different msg for mobile users
                }

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }

            //
            //  Reject if any player was a guest type that is not allowed for members
            //
            if (!slotParms.gplayer.equals("")) {

                msgHdr = "Data Entry Error";

                if (slotParms.hit3 == true) {                      // if error was name not specified
                    msgBody = "<BR><BR>You must specify the name of your guest(s)."
                            + "<BR><b>" + slotParms.gplayer + "</b> does not include a valid name (must be at least first & last names)."
                            + "<BR><BR>To specify the name, click in the player box where the guest is specified, "
                            + "<BR>move the cursor (use the arrow keys or mouse) to the end of the guest type value, "
                            + "<BR>use the space bar to enter a space and then type the guest's name.";
                } else {
                    msgBody = "<BR><BR><b>" + slotParms.gplayer + "</b> specifies a Guest Type that is not allowed for member use.";
                }

                msgBody += "<BR><BR>If the Golf Shop had originally entered this guest, then it <b>must not</b> be changed."
                        + "<BR><BR>Please correct this and try again."
                        + "<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }

            error = false;

            if (parm.unacompGuest == 0) {      // if unaccompanied guests not supported

                //
                //  Make sure at least 1 player contains a member
                //
                if (((slotParms.player1.equals("")) || (slotParms.player1.equalsIgnoreCase("x")) || (!slotParms.g1.equals("")))
                        && ((slotParms.player2.equals("")) || (slotParms.player2.equalsIgnoreCase("x")) || (!slotParms.g2.equals("")))
                        && ((slotParms.player3.equals("")) || (slotParms.player3.equalsIgnoreCase("x")) || (!slotParms.g3.equals("")))
                        && ((slotParms.player4.equals("")) || (slotParms.player4.equalsIgnoreCase("x")) || (!slotParms.g4.equals("")))
                        && ((slotParms.player5.equals("")) || (slotParms.player5.equalsIgnoreCase("x")) || (!slotParms.g5.equals("")))) {

                    msgHdr = "Data Entry Error";

                    msgBody = "<BR><BR>Member name not found. You must specify at least one member in the request."
                            + "<BR>Member names must be specified exactly as they exist in the system."
                            + "<BR><BR>Please correct this and try again.";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

            } else {           // guests are ok

                //
                //  Make sure at least 1 player contains a member
                //
                if (((slotParms.player1.equals("")) || (slotParms.player1.equalsIgnoreCase("x")))
                        && ((slotParms.player2.equals("")) || (slotParms.player2.equalsIgnoreCase("x")))
                        && ((slotParms.player3.equals("")) || (slotParms.player3.equalsIgnoreCase("x")))
                        && ((slotParms.player4.equals("")) || (slotParms.player4.equalsIgnoreCase("x")))
                        && ((slotParms.player5.equals("")) || (slotParms.player5.equalsIgnoreCase("x")))) {

                    msgHdr = "Data Entry Error";

                    msgBody = "<BR><BR>Required field has not been completed or is invalid."
                            + "<BR><BR>At least one player field must contain a name."
                            + "<BR>If you want to cancel the reservation, use the 'Cancel Tee Time' button under the player fields.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            //
            //  Check the number of X's against max specified by proshop
            //
            xcount = 0;

            if (slotParms.player1.equalsIgnoreCase("x")) {

                xcount++;
            }

            if (slotParms.player2.equalsIgnoreCase("x")) {

                xcount++;
            }

            if (slotParms.player3.equalsIgnoreCase("x")) {

                xcount++;
            }

            if (slotParms.player4.equalsIgnoreCase("x")) {

                xcount++;
            }

            if (slotParms.player5.equalsIgnoreCase("x")) {

                xcount++;
            }

            if (xcount > x) {

                msgHdr = "Data Entry Error";

                msgBody = "<BR><BR>The number of X's requested (" + xcount + ") exceeds the number allowed (" + x + ")."
                        + "<BR>Please try again.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }

            //
            //  At least 1 Player is present - Make sure a C/W was specified for all players
            //
            if (((!slotParms.player1.equals("")) && (!slotParms.player1.equalsIgnoreCase("x")) && (slotParms.p1cw.equals("")))
                    || ((!slotParms.player2.equals("")) && (!slotParms.player2.equalsIgnoreCase("x")) && (slotParms.p2cw.equals("")))
                    || ((!slotParms.player3.equals("")) && (!slotParms.player3.equalsIgnoreCase("x")) && (slotParms.p3cw.equals("")))
                    || ((!slotParms.player4.equals("")) && (!slotParms.player4.equalsIgnoreCase("x")) && (slotParms.p4cw.equals("")))
                    || ((!slotParms.player5.equals("")) && (!slotParms.player5.equalsIgnoreCase("x")) && (slotParms.p5cw.equals("")))) {

                msgHdr = "Data Entry Error";

                msgBody = "<BR><BR>Required field has not been completed or is invalid."
                        + "<BR><BR>You must specify a Cart or Walk option for all players.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }

            //
            //  Custom for Hartefeld National, Olympia Fields, and Olympic Club
            //
            if (club.equals("hartefeld") || club.equals("olympiafieldscc") || club.equals("olyclub")) {  // Must be at least 2 players

                //
                //  Must be more than 1 player
                //
                if (slotParms.player2.equals("") && slotParms.player3.equals("")
                        && slotParms.player4.equals("") && slotParms.player5.equals("")) {

                    msgHdr = "Invalid Number of Players";

                    msgBody = "<BR><BR>Sorry, you are not allowed to reserve tee times with only one player."
                            + "<BR><BR>Please add more players or contact the golf shop for assistance.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            // Custom for Oahu Case #1221
            if (club.equals("oahucc") && !slotParms.mship1.startsWith("Regular")
                    && (slotParms.mship1.startsWith("Intermediate")
                    || slotParms.mship1.startsWith("Limited")
                    || slotParms.mship1.equals("Super Senior")
                    || slotParms.mship1.equals("SS50")
                    || slotParms.mship2.startsWith("Intermediate")
                    || slotParms.mship2.startsWith("Limited")
                    || slotParms.mship2.equals("Super Senior")
                    || slotParms.mship2.equals("SS50")
                    || slotParms.mship3.startsWith("Intermediate")
                    || slotParms.mship3.startsWith("Limited")
                    || slotParms.mship3.equals("Super Senior")
                    || slotParms.mship3.equals("SS50")
                    || slotParms.mship4.startsWith("Intermediate")
                    || slotParms.mship4.startsWith("Limited")
                    || slotParms.mship4.equals("Super Senior")
                    || slotParms.mship4.equals("SS50")
                    || slotParms.mship5.startsWith("Intermediate")
                    || slotParms.mship5.startsWith("Limited")
                    || slotParms.mship5.equals("Super Senior")
                    || slotParms.mship5.equals("SS50"))) {

                if (slotParms.day.equals("Saturday") && slotParms.time > 659 && slotParms.time < 1453) {

                    msgHdr = "Invalid Days in Advance";

                    msgBody = "<BR><BR>Sorry, you are not allowed to reserve tee times more than one day in advance for this time of day."
                            + "<BR><BR>Please choose another time of day or contact the golf shop for assistance.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

                if ((slotParms.day.equals("Sunday") || (slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate2b || slotParms.date == Hdate3)) && slotParms.time > 629 && slotParms.time < 858) {

                    msgHdr = "Invalid Days in Advance";

                    msgBody = "<BR><BR>Sorry, you are not allowed to reserve tee times more than one day in advance for this time of day."
                            + "<BR><BR>Please choose another time of day or contact the golf shop for assistance.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            } // end Oahu custom


            if (club.equals("johnsisland")) {        // if Johns Island

                //
                //  Must be at least 2 players between 8:40 and 1:50 (members or guests)
                //
                if (slotParms.time > 839 && slotParms.time < 1351) {

                    int jicount = 0;

                    if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("x")) {

                        jicount++;
                    }
                    if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("x")) {

                        jicount++;
                    }
                    if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("x")) {

                        jicount++;
                    }
                    if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("x")) {

                        jicount++;
                    }
                    if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) {

                        jicount++;
                    }

                    if (jicount < 2) {       // if less than 2 players

                        msgHdr = "Invalid Number of Players";

                        msgBody = "<BR><BR>Sorry, you are not allowed to reserve tee times with less than 2 players at this time."
                                + "<BR><BR>All tee time requests from 8:40 AM to 1:50 PM must include at least 2 members and/or guests."
                                + "<BR><BR>Please add more players or contact the golf shop for assistance.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }  // end if Johns Island


//            if (club.equals("brooklawn")) {        // if Brooklawn CC
//
//                //
//                //  Must be at least 3 players between 10:28 and 2:00 (members or guests)
//                //  on Sat, Sun and Holidays between 5/27 to 9/04.
//                //
//                if (shortDate > 521 && shortDate < 905 && (date == Hdate1 || date == Hdate2 || date == Hdate2b || date == Hdate3
//                        || slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday"))) {
//
//                    if (slotParms.time > 1027 && slotParms.time < 1401) {
//
//                        int brcount = 0;
//
//                        if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("x")) {
//
//                            brcount++;
//                        }
//                        if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("x")) {
//
//                            brcount++;
//                        }
//                        if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("x")) {
//
//                            brcount++;
//                        }
//                        if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("x")) {
//
//                            brcount++;
//                        }
//                        if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) {
//
//                            brcount++;
//                        }
//
//                        if (brcount < 3) {       // if less than 3 players
//
//                            msgHdr = "Invalid Number of Players";
//
//                            msgBody = "<BR><BR>Sorry, you are not allowed to reserve tee times with less than 3 players at this time."
//                                    + "<BR><BR>All tee time requests from 10:30 AM to 2:00 PM on weekends and holidays must include at least 3 players."
//                                    + "<BR><BR>Please add more players or contact the golf shop for assistance.<BR><BR>";
//
//                            buildError(msgHdr, msgBody, mobile, out);       // output the error message
//
//                            returnToSlot(mobile, out, slotParms);
//                            return;
//                        }
//                    }
//                }
//            }  // end if Brooklawn CC

            //
            //  Make sure there are no duplicate names
            //
            player = "";

            if ((!slotParms.player1.equals("")) && (!slotParms.player1.equalsIgnoreCase("x")) && (slotParms.g1.equals(""))) {

                if ((slotParms.player1.equalsIgnoreCase(slotParms.player2)) || (slotParms.player1.equalsIgnoreCase(slotParms.player3))
                        || (slotParms.player1.equalsIgnoreCase(slotParms.player4)) || (slotParms.player1.equalsIgnoreCase(slotParms.player5))) {

                    player = slotParms.player1;
                }
            }

            if ((!slotParms.player2.equals("")) && (!slotParms.player2.equalsIgnoreCase("x")) && (slotParms.g2.equals(""))) {

                if ((slotParms.player2.equalsIgnoreCase(slotParms.player3)) || (slotParms.player2.equalsIgnoreCase(slotParms.player4))
                        || (slotParms.player2.equalsIgnoreCase(slotParms.player5))) {

                    player = slotParms.player2;
                }
            }

            if ((!slotParms.player3.equals("")) && (!slotParms.player3.equalsIgnoreCase("x")) && (slotParms.g3.equals(""))) {

                if ((slotParms.player3.equalsIgnoreCase(slotParms.player4))
                        || (slotParms.player3.equalsIgnoreCase(slotParms.player5))) {

                    player = slotParms.player3;
                }
            }

            if ((!slotParms.player4.equals("")) && (!slotParms.player4.equalsIgnoreCase("x")) && (slotParms.g4.equals(""))) {

                if (slotParms.player4.equalsIgnoreCase(slotParms.player5)) {

                    player = slotParms.player4;
                }
            }

            if (!player.equals("")) {          // if dup name found

                msgHdr = "Data Entry Error";

                msgBody = "<BR><BR><b>" + player + "</b> was specified more than once."
                        + "<BR><BR>Please correct this and try again.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }


            //
            //  Parse the names to separate first, last & mi
            //
            try {
                error = verifySlot.parseNames(slotParms, "mem");

            } catch (Exception ignore) {

                error = true;
            }

            if (error == true) {          // if problem

                msgHdr = "Invalid Data Received";

                msgBody = "<BR><BR>Sorry, a name you entered is not valid."
                        + "<BR><BR>You entered: '" + slotParms.player1 + "', '" + slotParms.player2 + "', '" + slotParms.player3 + "', '" + slotParms.player4 + "', '" + slotParms.player5 + "'"
                        + "<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them)."
                        + "<BR><BR>Please use the Partner List or Member List on the right side of the page to select the member names."
                        + "<BR>Simply <b>click on the desired name</b> in the list to add the member to the tee time.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }


            //
            //  Get the usernames, membership types, & hndcp's for players if matching name found
            //
            try {

                verifySlot.getUsers(slotParms, con);

            } catch (Exception e1) {

                msg = "Check guest names. ";

                dbError(out, e1, msg);                        // reject
                return;
            }
            
            /*
            //  Check each player's mship to see if it has permission to be a part of reservations for this activity (mship has an entry in mship5 for this activity)
            if (verifySlot.checkMemberAccess(slotParms, con)) {          // if problem with player mship access

                msgHdr = "Membership Restricted";

                msgBody = "<BR><BR>Sorry, <b>" +slotParms.player+ "</b> is not allowed to be a part of Golf reservations due to membership privileges."
                        + "<BR><BR>Please remove this player and submit the reservation again."
                        + "<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }*/
            
            
            //
            //  No players are using Pro-Only transportation modes without authorization
            //
            if (courseParms.hasProOnlyTmodes && !verifySlot.checkProOnlyMOT(slotParms, courseParms, con) && !club.equals("thedeuce") && !club.equals("thenationalgolfclub")) {

                msgHdr = "Access Error";

                msgBody = "<BR><BR><b>'" + slotParms.player + "'</b> is not authorized to use that mode of transportation."
                        + "<BR><BR>Please select another mode of transportation."
                        + "<BR>Contact your club if you require assistance with restricted modes of transportation.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }



            //
            //  Save the members' usernames for guest association
            //
            memA[0] = slotParms.user1;
            memA[1] = slotParms.user2;
            memA[2] = slotParms.user3;
            memA[3] = slotParms.user4;
            memA[4] = slotParms.user5;

            //
            //  Check if any of the names are invalid.
            //
            int invalNum = 0;
            p1 = "";

            if (slotParms.inval1 > 0) {

                p1 = slotParms.player1;                        // reject
                invalNum = slotParms.inval1;
            }
            if (slotParms.inval2 > 0) {

                p1 = slotParms.player2;                        // reject
                invalNum = slotParms.inval2;
            }
            if (slotParms.inval3 > 0) {

                p1 = slotParms.player3;                        // reject
                invalNum = slotParms.inval3;
            }
            if (slotParms.inval4 > 0) {

                p1 = slotParms.player4;                        // reject
                invalNum = slotParms.inval4;
            }
            if (slotParms.inval5 > 0) {

                p1 = slotParms.player5;                        // reject
                invalNum = slotParms.inval5;
            }

            if (invalNum > 0) {          // if rejected

                out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");

                if (invalNum == 2) {        // if incomplete member record

                    msgHdr = "Incomplete Member Record";

                    msgBody = "<BR><BR>Sorry, a member you entered has an imcomplete member record and cannot be included at this time."
                            + "<BR><BR>Member Name:&nbsp;&nbsp;&nbsp;'" + p1 + "'"
                            + "<BR><BR>Please inform your golf professional of this error."
                            + "<BR>You will have to remove this name from your tee time request.<BR><BR>";

                } else {

                    msgHdr = "Invalid Member Name Received";

                    msgBody = "<BR><BR>Sorry, a name you entered is not recognized as a valid member."
                            + "<BR><BR>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "'"
                            + "<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them)."
                            + "<BR><BR>Please use the Partner List or Member List on the right side of the page to select the member names."
                            + "<BR>Simply <b>click on the desired name</b> in the list to add the member to the tee time.<BR><BR>";
                }

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }


            //
            //  If any X's requested, make sure its not too late to request an X
            //
            //    from above - x = max x's allowed, xcount = # of x's requested, xhrs = # hrs in advance to remove x's
            //
            if (xcount > 0) {       // if any x's requested in tee time

                if (xhrs != 0) {     // if club wants to remove X's

                    //
                    //  Set date/time values to be used to check for X's in tee sheet
                    //
                    //  Get today's date and then go up by 'xhrs' hours
                    //
                    cal = new GregorianCalendar();       // get todays date

                    cal.add(Calendar.HOUR_OF_DAY, xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

                    calYear = cal.get(Calendar.YEAR);
                    calMonth = cal.get(Calendar.MONTH);
                    calDay = cal.get(Calendar.DAY_OF_MONTH);
                    calHr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
                    calMin = cal.get(Calendar.MINUTE);

                    calMonth = calMonth + 1;                            // month starts at zero

                    adv_date = calYear * 10000;                      // create a date field of yyyymmdd
                    adv_date = adv_date + (calMonth * 100);
                    adv_date = adv_date + calDay;                    // date = yyyymmdd (for comparisons)

                    adv_time = calHr * 100;                          // create time field of hhmm
                    adv_time = adv_time + calMin;

                    //
                    //  Compare the tee time's date/time to the X deadline
                    //
                    if ((date < adv_date) || ((date == adv_date) && (time <= adv_time))) {

                        msgHdr = "Invalid use of the X option.";

                        msgBody = "<BR><BR>Sorry, 'X' is not allowed for this tee time."
                                + "<BR>It is not far enough in advance to reserve a player position with an X.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }        // end of IF xcount


            //
            //***************************************************************************************************
            //
            //  CUSTOMS - check all possible customs here - those that are not dependent on guest info!!!!!!!!
            //
            //     verifyCustom.checkCustoms1 will process the individual custom and return any error message.
            //
            //    *** USE THIS FOR ALL FUTURE CUSTOMS WHEN APPROPRIATE !!!!!!!!!!!!!  ***   10/16/08
            //
            //***************************************************************************************************
            //
            String errorMsg = verifyCustom.checkCustoms1(slotParms, req);     // go check for customs

            if (!errorMsg.equals("")) {         // if error encountered - reject

                msgHdr = "";

                msgBody = errorMsg;                // use message from verifyCustom (header is built in)

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }

            //
            //  MOVE THE FOLLOWING CUSTOMS TO USE ABOVE PROCESS !!!!!!!!!!!!!!
            //

            if (club.equals("westchester")) {

                int westPlayers = verifySlot.checkWestPlayers(slotParms);

                if (westPlayers > 0 && westPlayers < 3) {    // if w/e or holiday and 1 or 2 players

                    msgHdr = "Insufficient Number of Players";

                    msgBody = "<BR><BR>Sorry, you have not specified enough players for this day and time."
                            + "<BR><BR>All Tee Times must include at least 3 players on Weekends & Holidays before 2 PM."
                            + "<BR>Please add more players or select a different time of the day.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

                error = false;

                if (slotParms.course.equals("South")) {

                    error = verifySlot.checkWestDependents(slotParms);

                    if (error == true) {    // if w/e or holiday and dependend w/o adult

                        msgHdr = "Dependent Without An Adult";

                        msgBody = "<BR><BR>Sorry, dependents must be accompanied by an adult for this day and time."
                                + "<BR><BR>All Tee Times must include at least 1 Adult on the South Course on Weekends & Holidays between 10:30 and 1:45."
                                + "<BR>Please add more players or select a different time of the day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }

            if (club.equals("pinehills")) {

                error = verifySlot.checkPineDependents(slotParms);     // check for Junior w/o an Adult

                if (error == true) {

                    msgHdr = "Junior Without An Adult";

                    msgBody = "<BR><BR>Sorry, juniors must be accompanied by an adult for this day and time."
                            + "<BR><BR>All Tee Times with a Junior Over 14 must include at least 1 Adult during times specified by the golf shop."
                            + "<BR>Please add more players or select a different time of the day.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            if (club.equals("stanwichclub")) {

                error = verifySlot.checkStanwichDependents(slotParms);     // check for Dependent w/o an Adult

                if (error == true) {

                    msgHdr = "Dependent Without An Adult";

                    msgBody = "<BR><BR>Sorry, dependents must be accompanied by an adult for this day and time."
                            + "<BR><BR>All Tee Times with a Dependent must include at least 1 Adult during times specified by the golf shop."
                            + "<BR>Please add an adult player or select a different time of the day.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            if (club.equals("castlepines")) {

                error = verifySlot.checkCastleDependents(slotParms);     // check for Dependent w/o an Adult

                if (error == true) {

                    msgHdr = "Dependent Without An Adult";

                    msgBody = "<BR><BR>Sorry, dependents must be accompanied by an adult at all times."
                            + "<BR><BR>Please add an adult player or return to the tee sheet."
                            + "<BR>If you have any questions, please contact your golf shop staff.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            if (club.equals("awbreyglen")) {

                error = verifyCustom.checkAwbreyDependents(slotParms);     // check for Dependent w/o an Adult

                if (error == true) {

                    msgHdr = "Junior Without An Adult";

                    msgBody = "<BR><BR>Sorry, Juniors must be accompanied by an adult at all times."
                            + "<BR>Juniors 12 and Over must be accompanied by an adult before Noon."
                            + "<BR><BR>Please add an adult player or return to the tee sheet."
                            + "<BR>If you have any questions, please contact your golf shop staff.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            if (club.equals("elniguelcc") && slotParms.day.equals("Sunday") && slotParms.time > 659 && slotParms.time < 1100 && fb == 1) {   // if Sunday, 7 - 11 AM and Back Tee

                error = verifyCustom.checkElNiguelDependents(slotParms);     // check for Dependent w/o an Adult

                if (error == true) {

                    msgHdr = "Dependent Without An Adult";

                    msgBody = "<BR><BR>Sorry, dependents must be accompanied by an adult between 7 AM and 11 AM on the back tee."
                            + "<BR><BR>Please add an adult player or return to the tee sheet."
                            + "<BR>If you have any questions, please contact your golf shop staff.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            if (club.equals("peninsula")) {    // Peninsula Club - juniors must be accompanied by an adult at all times

                //
                //  Check for any dependents
                //
                if (slotParms.mtype1.startsWith("Junior") || slotParms.mtype2.startsWith("Junior") || slotParms.mtype3.startsWith("Junior")
                        || slotParms.mtype4.startsWith("Junior") || slotParms.mtype5.startsWith("Junior")) {

                    //
                    //  Make sure at least 1 adult
                    //
                    if (!slotParms.mtype1.startsWith("Primary") && !slotParms.mtype1.startsWith("Spouse")
                            && !slotParms.mtype2.startsWith("Primary") && !slotParms.mtype2.startsWith("Spouse")
                            && !slotParms.mtype3.startsWith("Primary") && !slotParms.mtype3.startsWith("Spouse")
                            && !slotParms.mtype4.startsWith("Primary") && !slotParms.mtype4.startsWith("Spouse")
                            && !slotParms.mtype5.startsWith("Primary") && !slotParms.mtype5.startsWith("Spouse")) {       // if no adults

                        msgHdr = "Junior Without An Adult";

                        msgBody = "<BR><BR>Sorry, junior members must be accompanied by an adult at all times."
                                + "<BR><BR>Please add an adult player or return to the tee sheet."
                                + "<BR>If you have any questions, please contact your golf shop staff.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }

//            if (club.equals("bellemeadecc") && slotParms.day.equals("Sunday") && slotParms.time > 759 && slotParms.time < 1251) {   // if Sunday, 8 - 12:50
//
//                error = verifyCustom.checkBelleMeadeFems(slotParms);     // check for Female w/o a Male
//
//                if (error == true) {
//
//                    msgHdr = "Member Not Allowed";
//
//                    msgBody = "<BR><BR>Sorry, Primary Females must be accompanied by a Primary Male between 8 AM and 12:50 PM on Sundays."
//                            + "<BR><BR>Please add a Primary Male member or return to the tee sheet."
//                            + "<BR>If you have any questions, please contact your golf shop staff.<BR><BR>";
//
//                    buildError(msgHdr, msgBody, mobile, out);       // output the error message
//
//                    returnToSlot(mobile, out, slotParms);
//                    return;
//                }
//            }

            /*
            if (club.equals( "happyhollowclub" )) {    // Happy Hollow Club - dependents must be accompanied by an adult at all times
            
            //
            //  Check for any dependents
            //
            if (slotParms.mtype1.startsWith( "Dependent" ) || slotParms.mtype2.startsWith( "Dependent" ) || slotParms.mtype3.startsWith( "Dependent" ) ||
            slotParms.mtype4.startsWith( "Dependent" ) || slotParms.mtype5.startsWith( "Dependent" )) {
            
            //
            //  Make sure at least 1 adult
            //
            if (!slotParms.mtype1.startsWith( "Primary" ) && !slotParms.mtype1.startsWith( "Spouse" ) &&
            !slotParms.mtype2.startsWith( "Primary" ) && !slotParms.mtype2.startsWith( "Spouse" ) &&
            !slotParms.mtype3.startsWith( "Primary" ) && !slotParms.mtype3.startsWith( "Spouse" ) &&
            !slotParms.mtype4.startsWith( "Primary" ) && !slotParms.mtype4.startsWith( "Spouse" ) &&
            !slotParms.mtype5.startsWith( "Primary" ) && !slotParms.mtype5.startsWith( "Spouse" )) {       // if no adults
            
            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><BR><H3>Dependent Without An Adult</H3>");
            out.println("<BR><BR>Sorry, dependent members must be accompanied by an adult at all times.");
            out.println("<BR><BR>Please add an adult player or return to the tee sheet.");
            out.println("<BR><BR>If you have any questions, please contact your golf shop staff.");
            out.println("<BR><BR>");
            
            returnToSlot(mobile, out, slotParms);
            return;
            }
            }
            }
             */

            /*
            if (club.equals( "roguevalley" ) && slotParms.course.equals( "Rogue Front Nine" )) {
            
            // ***************************************************************************************
            //  Custom check for Rogue Valley - Wed & Sat before 2 PM, Sun before 1 PM - no singles or 2-somes!!
            // ***************************************************************************************
            //
            if (((slotParms.day.equalsIgnoreCase( "wednesday" ) || slotParms.day.equalsIgnoreCase( "saturday" )) &&
            slotParms.time < 1400) ||
            (slotParms.day.equalsIgnoreCase( "sunday" ) && slotParms.time < 1300)) {
            
            int rogplayers = 0;
            
            //
            //  Make sure at least 3 players
            //
            if (!slotParms.player1.equals( "" ) && !slotParms.player1.equalsIgnoreCase( "x" )) {  // if member or guest
            
            rogplayers++;
            }
            if (!slotParms.player2.equals( "" ) && !slotParms.player2.equalsIgnoreCase( "x" )) {  // if member or guest
            
            rogplayers++;
            }
            if (!slotParms.player3.equals( "" ) && !slotParms.player3.equalsIgnoreCase( "x" )) {  // if member or guest
            
            rogplayers++;
            }
            if (!slotParms.player4.equals( "" ) && !slotParms.player4.equalsIgnoreCase( "x" )) {  // if member or guest
            
            rogplayers++;
            }
            if (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" )) {  // if member or guest
            
            rogplayers++;
            }
            
            if (rogplayers < 3) {
            
            msgHdr = "Insufficient Number of Players";
            
            msgBody = "<BR><BR>Sorry, you have not specified enough players for this day and time." +
            "<BR><BR>All Tee Times must include at least 3 players on Wed & Sat before 2 PM, and Sun before 1 PM." +
            "<BR>Please add more players or select a different time of the day.<BR><BR>";
            
            buildError(msgHdr, msgBody, mobile, out);       // output the error message
            
            returnToSlot(mobile, out, slotParms);
            return;
            }
            }
            }
             */

            //
            //************************************************************************
            //  Check any membership types for max rounds per week, month or year
            //************************************************************************
            //
            if (!slotParms.mship1.equals("")
                    || !slotParms.mship2.equals("")
                    || !slotParms.mship3.equals("")
                    || !slotParms.mship4.equals("")
                    || !slotParms.mship5.equals("")) {                // if at least one name exists then check number of rounds

                error = false;                             // init error indicator

                try {

                    error = verifySlot.checkMaxRounds(slotParms, con);

                    if (error == false) {       // if ok, check for Hazeltine or Portage special processing

                        //
                        //  If Hazeltine National, then process the Unaccompanied Guests (Sponsored Group)
                        //
                        if (club.equals("hazeltine")) {      // if Hazeltine National

                            error = verifySlot.checkNational(slotParms, con);  // check for max rounds for National mships
                        }
                        //
                        //  If Portage CC, then process any Associate Memberships (2 rounds per month, 6 per year)
                        //
                        if (club.equals("portage")) {      // if Portage

                            error = verifySlot.checkPortage(slotParms, con);  // check for Associate mships
                        }
                    }

                } catch (Exception ignore) {
                }

                if (error == true) {      // a member exceed the max allowed tee times per week, month or year

                    msgHdr = "Member Exceeded Max Allowed Rounds";

                    msgBody = "<BR><BR>Sorry, " + slotParms.player + " is a " + slotParms.mship + " member and has exceeded the<BR>"
                            + "maximum number of tee times allowed for this " + slotParms.period + ".<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

            }  // end of mship if


            //
            // If Los Coyotes then make sure there are at least two members or 1 w/ guest for all tee times (Case 1211)
            // Changed - must be at least 2 players for all tee times (Case 1397)
            //
            //  Also - member MUST be part of the tee time!! (case 1647)
            //
            if (club.equals("loscoyotes")) {

                int loscount = 0;

                if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("x")) {

                    loscount++;
                }
                if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("x")) {

                    loscount++;
                }
                if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("x")) {

                    loscount++;
                }
                if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("x")) {

                    loscount++;
                }
                if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) {

                    loscount++;
                }

                if (loscount < 2) {       // if less than 2 players - changed on 3-18-08 per Larry

                    msgHdr = "Invalid Number of Players";

                    msgBody = "<BR><BR>Sorry, you are not allowed to reserve tee times with less than two named players."
                            + "<BR><BR>Please add another member or guest, or contact the golf shop for assistance.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

                //
                //  Los Coyotes - Check for Secondary member with Primary more than 3 days in advance
                //
                if (indReal > 3) {

                    error = verifyCustom.checkLCSpouses(slotParms);

                    if (error == true) {

                        msgHdr = "Secondary Without Primary";

                        msgBody = "<BR><BR>Sorry, Secondary members must be accompanied by the Primary family member<BR>when the group is scheduled more than 3 days in advance."
                                + "<BR><BR>Please add the Primary member or return to the tee sheet."
                                + "<BR><BR>If you have any questions, please contact your golf shop staff.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }

                //
                //  Los Coyotes - User must be part of the tee time (case 1647)
                //
                if (!user.equalsIgnoreCase(slotParms.user1) && !user.equalsIgnoreCase(slotParms.user2) && !user.equalsIgnoreCase(slotParms.user3)
                        && !user.equalsIgnoreCase(slotParms.user4) && !user.equalsIgnoreCase(slotParms.user5)) {

                    msgHdr = "Invalid Request";

                    msgBody = "<BR><BR>Sorry, you cannot create or modify a tee time request<BR>unless you are a part of the tee time."
                            + "<BR><BR>Please add yourself or return to the tee sheet.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

            }       // end of IF Los Coyotes

            // If Greenwich and less then 3 players on the listed dates/times then reject  (Case #1123)
            if (club.equals("greenwich")) {

                error = verifySlot.checkGreenwichMinPlayers(slotParms, xcount);

                if (error == true) {

                    msgHdr = "Invalid Number of Players";

                    msgBody = "<BR><BR>Sorry, you are not allowed to reserve tee times with less than three players."
                            + "<BR><BR>Please add another member or guest, or contact the golf shop for assistance.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

            } // end is Greenwich


            //
            //  Custom for Minikahda CC - check for special guest times (case #1027)
            //
            /*
            if (club.equals("minikahda")) {

                thisTime = SystemUtils.getTime(con);               // get the current adjusted time

                if (indReal > 3 || (indReal == 3 && thisTime < 800)) {     // if beyond normal days in adv (guest times only)

                    if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")
                            || date == Hdate1 || date == Hdate2b || date == Hdate3) && time < 1000) {      // nothing allowed if w/e or holiday and before 10 AM

                        msgHdr = "Invalid Request - Reject";

                        msgBody = "<BR><BR>Sorry, you are not allowed to book a tee time this far in advance on weekends and holidays before 10:00 AM."
                                + "<BR><BR>These times can be reserved up to 3 days in advance.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;

                    } else {

                        if (slotParms.day.equals("Thursday") && time < 1500 && slotParms.guests > 0
                                && shortDate > 521 && shortDate < 905) {                                          // No guests allowed on Thurs before 3:00 PM

                            msgHdr = "Invalid Tee Time Request";

                            msgBody = "<BR><BR>Sorry, guests are not allowed before 3:00 PM on Thursdays."
                                    + "<BR><BR>Please remove the guest(s) or select another time.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;

                        } else {

                            //
                            //  Must be 1 member and 3 guests (only)
                            //
                            if (slotParms.guests < 3 || slotParms.members != 1) {

                                msgHdr = "Invalid Guest Time Request";

                                msgBody = "<BR><BR>Sorry, only guest times are accepted at this time."
                                        + "<BR><BR>Your request must contain 1 member and 3 guests.<BR><BR>";

                                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                                returnToSlot(mobile, out, slotParms);
                                return;

                            } else {

                                //
                                //   Now check to see if there are already 2 guest times this hour.
                                //
                                error = verifyCustom.checkMiniGuestTimes(slotParms, con);

                                if (error == true) {

                                    msgHdr = "Invalid Guest Time Request";

                                    msgBody = "<BR><BR>Sorry, there are already 2 guest times scheduled this hour."
                                            + "<BR><BR>Please select a different time of the day.<BR><BR>";

                                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                                    returnToSlot(mobile, out, slotParms);
                                    return;
                                }
                            }
                        }
                    }

                } else {    // within normal days in adv

                    //
                    //  If 1 member and 3 guests, then check for max already requested during this hour (other combinations are allowed per normal restrictions)
                    //
                    if (slotParms.guests == 3 && slotParms.members == 1) {

                        //
                        //   Now check to see if there are already 2 guest times this hour.
                        //
                        error = verifyCustom.checkMiniGuestTimes(slotParms, con);

                        if (error == true) {

                            msgHdr = "Invalid Guest Time Request";

                            msgBody = "<BR><BR>Sorry, there are already 2 guest times scheduled this hour."
                                    + "<BR><BR>Please select a different time of the day.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;
                        }
                    }
                }
            }      // end of IF minikahda
            */


            //
            // **************************************
            //  Check for max # of guests exceeded (per member or per tee time)
            // **************************************
            //
            if (slotParms.guests != 0) {      // if any guests were included

                error = false;                             // init error indicator

                //
                //  Custom for Westchester - if 1 guest and 3 members, then always ok (do not check restrictions)
                //
                if (club.equals("westchester") && slotParms.guests == 1 && slotParms.members > 2) {   // if ok to check (not Westchester)
                    
                    skipGuestRest = true;
                    
                } else if (club.equals("lochlloyd") && (slotParms.mship1.equalsIgnoreCase("Owner") ||  slotParms.mship2.equalsIgnoreCase("Owner") 
                        ||  slotParms.mship3.equalsIgnoreCase("Owner") ||  slotParms.mship4.equalsIgnoreCase("Owner") ||  slotParms.mship5.equalsIgnoreCase("Owner"))) {
                    
                    // If at least one "Owner" mship is present, this tee time is exempt from all guest restrictions
                    skipGuestRest = true;
                    
                }

                if (skipGuestRest == false) {   // if ok to check

                    //
                    //  Check Guest Restrictions - ALL clubs
                    //
                    try {

                        error = verifySlot.checkMaxGuests(slotParms, con);

                    } catch (Exception e5) {

                        msg = "Check Memberships and Guest Numbers. ";

                        dbError(out, e5, msg);
                        return;
                    }

                    if (error == true) {      // a member exceed the max allowed tee times per month

                        msgHdr = "Number of Guests Exceeded Limit";

                        msgBody = "<BR><BR>Sorry, the maximum number of guests allowed for the<BR>time you are requesting is " + slotParms.grest_num + " per " + slotParms.grest_per + "."
                                + "<BR><BR>Guest Restriction = " + slotParms.rest_name + "<BR><BR>";
                        
                        if (club.equals("ridgecc")) {
                            msgBody += "Please contact the golf shop at (773) 238-9405 for assistance with this tee time.<BR><BR>";
                        }

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }

                    //
                    //  Custom for Oakmont CC
                    //
                    if (club.equals("oakmont")) {      // if Oakmont CC

                        if (slotParms.guests > 1) {       // if 2 or more guests and Oakmont CC

                            //
                            // **********************************************************************
                            //  Oakmont - Check for max # of family guest tee times exceeded
                            // **********************************************************************
                            //
                            error = false;                             // init error indicator

                            try {

                                error = verifySlot.oakmontGuests(slotParms, con);

                            } catch (Exception e5) {

                                msg = "Check Oakmont Guest Tee Times. ";

                                dbError(out, e5, msg);
                                return;
                            }

                            if (error == true) {      // a member exceed the max allowed tee times per month

                                msgHdr = "Number of Family Guest Tee Times Exceeded Limit For The Day";

                                msgBody = "<BR><BR>Sorry, there are already 2 tee times with family guests"
                                        + "<BR>scheduled for today.  You are only allowed one family guest per member.<BR><BR>";

                                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                                returnToSlot(mobile, out, slotParms);
                                return;
                            }
                        }
                    }      // end of IF Oakmont

                    //
                    //  If Hazeltine National or Old Oaks, then make sure member entered a guest's name after each guest type
                    //
                    if (club.equals("hazeltine") || club.equals("oldoaks")) { // if Hazeltine National or Old Oaks

                        error = verifySlot.checkGuestNames(slotParms, con);

                        if (error == true) {      // a member exceed the max allowed tee times per month

                            msgHdr = "Invalid Guest Request";

                            msgBody = "<BR><BR>Sorry, you must provide the full name of your guest(s).<BR>Please enter a space followed by the guest's name immediately after the guest type"
                                    + "<BR>in the player field.  Click your mouse in the player field, move the cursor"
                                    + "<BR><BR>to the end of the guest type, hit the space bar and then type the full name.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;
                        }
                    }      // end of if Hazeltine

                }      // end of IF skipGuestRest

                //
                //  If Green Bay, then check if more than 9 guests per hour
                //
                if (club.equals("greenbay")) {           // if Green Bay CC

                    error = verifySlot.checkGBguests(slotParms, con);

                    if (error == true) {      // more than 9 guest this hour

                        msgHdr = "Maximum Number of Guests Exceeded";

                        msgBody = "<BR><BR>Sorry, but there are already guests scheduled during this hour.<BR>No more than 9 guests are allowed per hour.  This request would exceed that total."
                                + "<BR><BR>Please remove one or more guests, or try another time of the day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }      // end of if Green Bay CC

                //
                //  If Riverside G&CC & Sunday before Noon, then check if more than 12 guests total
                //
                if (club.equals("riverside") && slotParms.day.equals("Sunday") && slotParms.time < 1200) {     // if Riverside, Sunday & < Noon

                    error = verifyCustom.checkRSguests(slotParms, con);

                    if (error == true) {      // more than 12 guests before noon

                        msgHdr = "Maximum Number of Guests Exceeded";

                        msgBody = "<BR><BR>Sorry, but there are already 12 guests scheduled today.<BR>No more than 12 guests are allowed before Noon.  This request would exceed that total."
                                + "<BR><BR>Please remove one or more guests, or try another time of the day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }      // end of if Riverside G&CC

                //
                //  If The Patterson Club & Sat/Sun between 7-9:30, then check if more than 12 guests total
                //
                if (club.equals("pattersonclub")
                        && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || date == ProcessConstants.memDay || date == ProcessConstants.july4 || date == ProcessConstants.laborDay)
                        && slotParms.time > 659 && slotParms.time < 931) {

                    error = verifyCustom.checkPattersonGuests(slotParms, con);

                    if (error == true) {      // more than 12 guests before noon

                        msgHdr = "Maximum Number of Guests Exceeded";

                        msgBody = "<BR><BR>Sorry, but there are already 12 guests scheduled.<BR>No more than 12 guests are allowed between 7:00am and 9:30am on weekends and holidays.<BR>This request would exceed that total."
                                + "<BR><BR>Please remove one or more guests, or try another time of the day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }      // end of if Patterson Club

                //
                //  If Wilmington CC then check if more than 12 guests total
                //
                if (club.equals("wilmington")) {

                    error = verifyCustom.checkWilmingtonGuests(slotParms, con);

                    if (error == true) {      // more than 12 guests

                        msgHdr = "Maximum Number of Guests Exceeded";

                        msgBody = "<BR><BR>Sorry, but there are already 12 guests scheduled today.<BR>No more than 12 guests are allowed during the selected time period.<BR>This request would exceed that total."
                                + "<BR><BR>Please remove one or more guests, or try another time of the day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }      // end of if Wilmington

                //
                //  If Merion, then check for max guest times per hour
                //
                if (club.equals("merion") && slotParms.course.equals("East")
                        && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday"))) {   // Merion, East course, and a w/e

                    error = verifySlot.checkMerionG(slotParms, con);

                    if (error == true) {      // more guest times this hour than allowed

                        msgHdr = "Maximum Number of Guest Times Exceeded";

                        msgBody = "<BR><BR>Sorry, but the maximum number of guest times are already scheduled during this hour."
                                + "<BR><BR>Please try another time of the day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }      // end of if Merion


                //
                //  If Congressional, then check for 'Cert Jr Guest' types - must only follow a Certified Dependent
                //
                if (club.equals("congressional")) {           // if congressional

                    error = congressionalCustom.checkCertGuests(slotParms);

                    if (error == true) {      // no guests allowed

                        msgHdr = "Guest Type Not Allowed";

                        msgBody = "<BR><BR>Sorry, but the guest type 'Cert Jr Guest' can only follow a Certified Dependent"
                                + "<BR>and a dependent may only have one guest.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }      // end of if Congressional


                //
                //  If Bearpath, then check for member-only time
                //
                if (club.equals("bearpath")) {           // if Bearpath

                    error = verifySlot.checkBearpathGuests(slotParms.day, slotParms.date, slotParms.time, slotParms.ind);

                    if (error == true) {      // no guests allowed

                        msgHdr = "Guests Not Allowed";

                        msgBody = "<BR><BR>Sorry, but guests are not allowed during this time.  This is a member-only time."
                                + "<BR><BR>Please try another time of the day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }      // end of if Bearpath


                //
                //  If Cherry Hills, then check for Juniors with guests and no parent
                //
                if (club.equals("cherryhills")) {           // if Cherry Hills

                    error = false;

                    //
                    //  Check if any Juniors in the request
                    //
                    if (slotParms.mtype1.startsWith("Junior") || slotParms.mtype2.startsWith("Junior") || slotParms.mtype3.startsWith("Junior")
                            || slotParms.mtype4.startsWith("Junior") || slotParms.mtype5.startsWith("Junior")) {

                        error = true;     // default = error

                        //
                        //  Now check if any Adults
                        //
                        if (slotParms.mtype1.endsWith("Member") || slotParms.mtype2.endsWith("Member") || slotParms.mtype3.endsWith("Member")
                                || slotParms.mtype4.endsWith("Member") || slotParms.mtype5.endsWith("Member")
                                || slotParms.mtype1.endsWith("Spouse") || slotParms.mtype2.endsWith("Spouse") || slotParms.mtype3.endsWith("Spouse")
                                || slotParms.mtype4.endsWith("Spouse") || slotParms.mtype5.endsWith("Spouse")) {

                            error = false;     // ok if adult included
                        }
                    }

                    if (error == true) {      // no guests allowed

                        msgHdr = "Guests Not Allowed";

                        msgBody = "<BR><BR>Sorry, but you are not allowed to request a time with guests when an adult is not included."
                                + "<BR><BR>Please remove the guests or add an adult.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }      // end of if Cherry Hills


                //
                //  If Sonnenalp - we know we have guests so go get their rates to be displayed on the tee sheet (saved in custom_dispx)
                //
//                if (club.equals("sonnenalp")) {           // if Sonnenalp
//
//                    verifyCustom.addGuestRates(slotParms);         // get rates for each guest
//                }

            } else {      // No guests in this request

                //
                //  Baltusrol - Must be at least one guest or one X in every request, except during Ladies Day
                //
                if (club.equals("baltusrolgc")) {           // if Baltusrol

                    boolean baltusrolSkip = false;
                   
                    if (slotParms.day.equals("Tuesday") && slotParms.time >= 700 && slotParms.time <= 1000 && slotParms.date >= 20140422 && slotParms.date <= 20141014 
                              && ((week_of_year % 2 == 0 && slotParms.course.equals("Lower")) || (week_of_year % 2 == 1 && slotParms.course.equals("Upper")))) {

                         baltusrolSkip = true;    // skip guest check if Ladies Day time
                         
                    } else if (slotParms.day.equals("Wednesday") && slotParms.time >= 800 && slotParms.time <= 1000 && slotParms.date >= 20140423 && slotParms.date <= 20141022 
                              && ((week_of_year % 2 == 1 && slotParms.course.equals("Lower")) || (week_of_year % 2 == 0 && slotParms.course.equals("Upper")))) {

                         baltusrolSkip = true;    // skip guest check if Ladies Day time
                    }
                   
                    if (!slotParms.player2.equalsIgnoreCase("x") && !slotParms.player3.equalsIgnoreCase("x") && !slotParms.player4.equalsIgnoreCase("x") &&
                        baltusrolSkip == false) {

                        //
                        //  No guests and no X's - reject
                        //
                        msgHdr = "Invalid Guest Time";

                        msgBody = "<BR><BR>Sorry, but you are not allowed to request a time without at least one guest or an X."
                                + "<BR><BR>Please add one or more guests or add an X.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }           // end of IF Baltusrol

                //
                //  Oak Hill CC - Must be at least one guest in every request
                //
                if (club.equals("oakhillcc")) {           // if Oak Hill CC

                    msgHdr = "Invalid Guest Time";

                    msgBody = "<BR><BR>Sorry, but you are not allowed to request a time without at least one guest."
                            + "<BR><BR>Please add one or more guests or return to the tee sheet.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }           // end of IF Oak Hill CC

            }      // end of if guests


            //
            //  If Congressional, then check for Dependent w/o an adult
            //
            if (club.equals("congressional")) {           // if congressional

                //
                //  Check if any 'Dependent Non-Certified' mtypes are in the request
                //
                if (slotParms.mtype1.equals("Dependent Non-Certified") || slotParms.mtype2.equals("Dependent Non-Certified") || slotParms.mtype3.equals("Dependent Non-Certified")
                        || slotParms.mtype4.equals("Dependent Non-Certified") || slotParms.mtype5.equals("Dependent Non-Certified")) {

                    error = true;     // default = error

                    //
                    //  Now check if any Adults
                    //
                    if (slotParms.mtype1.startsWith("Primary") || slotParms.mtype2.startsWith("Primary") || slotParms.mtype3.startsWith("Primary")
                            || slotParms.mtype4.startsWith("Primary") || slotParms.mtype5.startsWith("Primary")
                            || slotParms.mtype1.startsWith("Spouse") || slotParms.mtype2.startsWith("Spouse") || slotParms.mtype3.startsWith("Spouse")
                            || slotParms.mtype4.startsWith("Spouse") || slotParms.mtype5.startsWith("Spouse")) {

                        error = false;     // ok if adult included
                    }
                }

                if (error == true) {      // if dependent w/o an adult

                    msgHdr = "Dependent Without An Adult";

                    msgBody = "<BR><BR>Sorry, but a Non-Certified Dependent is not allowed when an adult is not included."
                            + "<BR><BR>Please remove the dependent or add an adult.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }


            //
            //  If Colleton River Club, then check for Dependent w/o an adult  (Case #1291)
            //
            if (club.equals("colletonriverclub")) {

                //
                //  Check if any 'Dependents' mtypes are in the request
                //
                if (slotParms.mtype1.equals("Dependents") || slotParms.mtype2.equals("Dependents") || slotParms.mtype3.equals("Dependents")
                        || slotParms.mtype4.equals("Dependents") || slotParms.mtype5.equals("Dependents")) {

                    error = true;     // default = error

                    //
                    //  Now check if any Adults
                    //
                    if (slotParms.mtype1.startsWith("Adult") || slotParms.mtype2.startsWith("Adult") || slotParms.mtype3.startsWith("Adult")
                            || slotParms.mtype4.startsWith("Adult") || slotParms.mtype5.startsWith("Adult")) {

                        error = false;     // ok if adult included
                    }
                }

                if (error == true) {      // if dependent w/o an adult

                    msgHdr = "Dependent Without An Adult";

                    msgBody = "<BR><BR>Sorry, but a Dependent is not allowed when an adult is not included."
                            + "<BR><BR>Please remove the dependent or add an adult.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }


            //
            //  if The CC, must always be a guest time (member plus a guest or 'X')
            //
            if (club.equals("tcclub")) {

                error = false;

                if (slotParms.user1.equals("")) {      // if player1 is NOT a member

                    error = true;

                } else {                                 // ok so far

                    if (slotParms.guests == 0) {      // if no guests were included check for an 'X'

                        error = true;

                        if (slotParms.player2.equalsIgnoreCase("x") || slotParms.player3.equalsIgnoreCase("x")
                                || slotParms.player4.equalsIgnoreCase("x") || slotParms.player5.equalsIgnoreCase("x")) {

                            error = false;
                        }
                    }
                }

                if (error == true) {

                    msgHdr = "Invalid Request";

                    msgBody = "<BR><BR>Sorry, you must include at least 1 member and 1 guest (or X) when requesting a tee time.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            //
            //  Custom for Oakmont CC - if more than 14 days in advance, must be 2 or 3 guests and 1 or 2 members (3 & 1, or 2 & 2)
            //
            //    Note:  this is only for weekdays.  Member_sheet will not allow access on weekends more than 14 days in adv)
            //
            if (club.equals("oakmont")) {      // if Oakmont CC

                if (((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.day.equals("Monday")) && slotParms.ind > 7) || 
                    ((slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Thursday") || slotParms.day.equals("Friday")) && slotParms.ind > 14)) {          // if this date is more than 14 days ahead

                    //
                    //  More than 14 days in advance - must be a guest time !!
                    //
                    //error = false;
                    oakskip = true;                 // set in case we make it through here (for later)

                    /*
                    if (slotParms.members > 2 || slotParms.members == 0) {

                        error = true;             // must be error

                    } else {

                        if (slotParms.guests < 2) {

                            error = true;             // must be error
                        }
                    }

                    if (error == false) {        // if ok so far

                        if (slotParms.guests == 2 && slotParms.members < 2) {

                            error = true;             // must be error
                        }
                    }

                    if (error == true) {        // if too many guests

                        msgHdr = "Invalid Number of Guests Specified";

                        msgBody = "<BR><BR>Sorry, you must have 3 guests and 1 member, or 2 guests and 2 members"
                                + "<BR>when requesting a tee time more than 14 days in advance.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }*/

                }

                if ((slotParms.day.equals("Wednesday") || slotParms.day.equals("Friday")) && date != Hdate2b) {  // if Wednesday or Friday

                    //
                    // **********************************************************************
                    //  Oakmont - Check for dedicated guest tee times  (Wed & Fri)
                    // **********************************************************************
                    //
                    error = false;                             // init error indicator

                    try {

                        error = verifySlot.oakmontGuestsWF(slotParms, con);

                    } catch (Exception e5) {

                        msg = "Check Oakmont Guest Tee Times Wed & Fri. ";

                        dbError(out, e5, msg);
                        return;
                    }

                    if (error == true) {      // a member exceed the max allowed tee times per month

                        msgHdr = "Invalid Number of Guests Specified";

                        msgBody = "<BR><BR>Sorry, you must have 3 guests and 1 member, or 2 guests and 2 members"
                                + "<BR>during the selected time for this day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }      // end of IF Oakmont

            //
            //  Custom for Oakmont CC - if less than 30 days in advance, guest tee times cannot be changed -
            //                          players may be added, but not removed.  Names can be changed however.
            //
            if (club.equals("oakmont") && indReal < 30) {      // if Oakmont CC

                //
                //  See if any guests were in tee time prior to this request (player was specified, but not member)
                //
                if ((!slotParms.oldPlayer1.equals("") && !slotParms.oldPlayer1.equalsIgnoreCase("x") && slotParms.oldUser1.equals(""))
                        || (!slotParms.oldPlayer2.equals("") && !slotParms.oldPlayer2.equalsIgnoreCase("x") && slotParms.oldUser2.equals(""))
                        || (!slotParms.oldPlayer3.equals("") && !slotParms.oldPlayer3.equalsIgnoreCase("x") && slotParms.oldUser3.equals(""))
                        || (!slotParms.oldPlayer4.equals("") && !slotParms.oldPlayer4.equalsIgnoreCase("x") && slotParms.oldUser4.equals(""))
                        || (!slotParms.oldPlayer5.equals("") && !slotParms.oldPlayer5.equalsIgnoreCase("x") && slotParms.oldUser5.equals(""))) {

                    //
                    //   At least 1 guest had already been in the tee time - make sure no guests were removed
                    //
                    //   If it was a guest, make sure it still is, if not, then must still be something.
                    //
                    if (!slotParms.oldPlayer1.equals("") && !slotParms.oldPlayer1.equalsIgnoreCase("x") && slotParms.oldUser1.equals("")) {

                        if (slotParms.player1.equals("") || slotParms.player1.equalsIgnoreCase("x") || !slotParms.user1.equals("")) {

                            error = true;          // error, was a guest but not now
                        }
                    }
                    if (!slotParms.oldPlayer2.equals("") && !slotParms.oldPlayer2.equalsIgnoreCase("x") && slotParms.oldUser2.equals("")) {

                        if (slotParms.player2.equals("") || slotParms.player2.equalsIgnoreCase("x") || !slotParms.user2.equals("")) {

                            error = true;          // error, was a guest but not now
                        }
                    }
                    if (!slotParms.oldPlayer3.equals("") && !slotParms.oldPlayer3.equalsIgnoreCase("x") && slotParms.oldUser3.equals("")) {

                        if (slotParms.player3.equals("") || slotParms.player3.equalsIgnoreCase("x") || !slotParms.user3.equals("")) {

                            error = true;          // error, was a guest but not now
                        }
                    }
                    if (!slotParms.oldPlayer4.equals("") && !slotParms.oldPlayer4.equalsIgnoreCase("x") && slotParms.oldUser4.equals("")) {

                        if (slotParms.player4.equals("") || slotParms.player4.equalsIgnoreCase("x") || !slotParms.user4.equals("")) {

                            error = true;          // error, was a guest but not now
                        }
                    }

                    if (error == true) {        // guest time changed - old player changed

                        msgHdr = "Players Changed in a Guest Time";

                        msgBody = "<BR><BR>Sorry, you cannot remove players from a tee time containing guests"
                                + "<BR>within 30 days of the tee time.  You may only add new members or guests.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }      // end of if Oakmont

            //
            //  Custom for Medinah CC - if Course 'No 1' or 'No 3' and before 4:00 PM (members do not have access to Member Only times)
            //                          then all times available to members must contain 2 or 3 guests.
            //
            //                        - Course 'No 2' - Member/Guest times at 8, 9, 10, 11 & 12 on w/e's and holidays.
            //
            //        Acceptable combinations:  M G G
            //                                  M G G G
            //                                  M G G M
            //                                  M M G G
            //
            //     Exception:  Course #3 at 8:00 & 8:12 on Tuesdays (Spouse only times)
            //                 These must have 1 or 2 spouses and at least one guest.
            //
            if (club.equals("medinahcc")) {          // && slotParms.time < 1601

                error = false;

                if (slotParms.course.equals("No 3") && slotParms.day.equals("Tuesday")
                        && (slotParms.time == 800 || slotParms.time == 810)) {

                    //
                    //  Spouse/Guest Times - make sure there is at least one spouse and one guest
                    //
                    if (slotParms.members > 2 || slotParms.guests > 2) {    // cannot be more than 2 members or guests

                        error = true;

                    } else {

                        if (slotParms.members == 0 || slotParms.guests == 0) {    // must be at least one of each

                            error = true;

                        } else {

                            if (slotParms.members < slotParms.guests) {        // cannot be more guests than members

                                error = true;
                            }
                        }
                    }

                    if (error == false) {     // if ok, then check mtypes of members - must be spouses

                        if (!slotParms.mtype1.equals("")) {         // if player is a member

                            if (!slotParms.mtype1.equals("Regular Spouse") && !slotParms.mtype1.equals("Jr. Spouse")
                                    && !slotParms.mtype1.equals("Reg Prob Spouse")) {

                                error = true;
                            }
                        }
                        if (!slotParms.mtype2.equals("")) {         // if player is a member

                            if (!slotParms.mtype2.equals("Regular Spouse") && !slotParms.mtype2.equals("Jr. Spouse")
                                    && !slotParms.mtype2.equals("Reg Prob Spouse")) {

                                error = true;
                            }
                        }
                        if (!slotParms.mtype3.equals("")) {         // if player is a member

                            if (!slotParms.mtype3.equals("Regular Spouse") && !slotParms.mtype3.equals("Jr. Spouse")
                                    && !slotParms.mtype3.equals("Reg Prob Spouse")) {

                                error = true;
                            }
                        }
                        if (!slotParms.mtype4.equals("")) {         // if player is a member

                            if (!slotParms.mtype4.equals("Regular Spouse") && !slotParms.mtype4.equals("Jr. Spouse")
                                    && !slotParms.mtype4.equals("Reg Prob Spouse")) {

                                error = true;
                            }
                        }
                    }

                    if (error == true) {

                        msgHdr = "Invalid Request for Spouse/Guest Time";

                        msgBody = "<BR><BR>Sorry, you must specify 1 or 2 Spouse Members and at least 1 guest, but no more than 1 guest per member."
                                + "<BR>Only Spouses and their guests are allowed to play during this time."
                                + "<BR><BR>Be sure to place the guests immediately following their host member.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }

                } else {   // Not Tues at 8:00 or 8:10 on #3

                    if (slotParms.course.equals("No 2")) {

                        //
                        //  If 8:00, 9:00, 10:00, 11:00 or 12:00 then check Member/Guest Times
                        //
                        if (slotParms.time == 800 || slotParms.time == 900 || slotParms.time == 1000
                                || slotParms.time == 1100 || slotParms.time == 1200) {

                            error = medinahCustom.checkMG2(slotParms, con);
                        }

                    } else {    // Not course #2

                        if (slotParms.guests < 2) {      // if less than 2 guests were included

                            error = true;

                        } else {

                            if (!slotParms.g1.equals("")) {         // if player 1 is a guest

                                error = true;

                            } else {                                  // Player 1 is a member

                                if (!slotParms.g2.equals("")) {      // if player 2 is a guest

                                    if (slotParms.g3.equals("")) {    // then player 3 must be a guest

                                        error = true;                    // error if not
                                    }

                                } else {                               // Player 2 is also a member

                                    if (slotParms.g3.equals("") || slotParms.g4.equals("")) { // then players 3 & 4 must be guests

                                        error = true;                    // error if not
                                    }
                                }
                            }
                        }
                    }

                    if (error == true) {

                        msgHdr = "Invalid Request for Member/Guest Time";

                        msgBody = "<BR><BR>Sorry, you must specify at least 2 guests for 1 member during this time."
                                + "<BR>Be sure to place the guests immediately following their host member.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }

                    //
                    //  If #2 and 1:00 or 2:00 PM, must be at least 1 guest
                    //
                    if (slotParms.course.equals("No 2") && (slotParms.time == 1300 || slotParms.time == 1400)) {

                        error = medinahCustom.checkMG2b(slotParms, con);

                        if (error == true) {

                            msgHdr = "Invalid Request for Member/Guest Time";

                            msgBody = "<BR><BR>Sorry, you must specify at least 1 guest during this time."
                                    + "<BR><BR>Be sure to place the guest(s) immediately following the host member.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;
                        }
                    }
                }
            }      // end of if Medinah CC

            //
            //  Custom for Milwaukee CC - if back tee time between 12:00 & 2:30 and a weekday, must be at least 2 guests
            //
            boolean mccskip = false;

            if (club.equals("milwaukee") && slotParms.time > 1200 && slotParms.time < 1431 && slotParms.fb == 1
                    && !slotParms.day.equals("Saturday") && !slotParms.day.equals("Sunday") && !slotParms.day.equals("Monday")) {

                //         if (!slotParms.user1.equalsIgnoreCase( user ) || !slotParms.user4.equals( "" ) || slotParms.guests < 2) {
                if (slotParms.guests < 2) {      // if less than 2 guests were included

                    msgHdr = "Invalid Guest Time Request";

                    msgBody = "<BR><BR>Sorry, you must specify at least 2 guests when making"
                            + "<BR>a tee time on the back 9 after 12:00 PM this far in advance.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;

                } else {   // MCC Guest Time ok

                    mccskip = true;     // ok to skip the 'days in advance' test
                }
            }

            //
            //  Custom for Congressional CC - check for special guest times (case #1075)
            //
            if (club.equals("congressional")) {

                thisTime = SystemUtils.getTime(con);               // get the current adjusted time

                int congTime = 1700;                               // default

                if (slotParms.mship1.startsWith("Beneficiary") || slotParms.mship1.startsWith("Honorar") || slotParms.mship1.equals("Resident Active")
                        || slotParms.mship1.equals("Resident Twenty")) {

                    congTime = 1500;
                }

                if (indReal > 9 || (indReal == 9 && thisTime < congTime)) {     // if a special guest time

                    //
                    //  Must be at least one '30 Day Advance Guest' in the group
                    //
                    if (slotParms.player2.startsWith("30 Day Advance Guest") || slotParms.player3.startsWith("30 Day Advance Guest")
                            || slotParms.player4.startsWith("30 Day Advance Guest") || slotParms.player5.startsWith("30 Day Advance Guest")) {

                        mccskip = true;     // ok to skip the 'days in advance' test

                        //
                        //  Make sure this member does not have too many advance times already scheduled
                        //
                        /*
                        error = congressionalCustom.checkAdvTimes(slotParms, con);

                        if (error == true) {

                            msgHdr = "Invalid Guest Time Request";

                            msgBody = "<BR><BR>Sorry, " + slotParms.player + " already has 4 advance guest times scheduled this year."
                                    + "<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;
                        }
                         */

                    } else {   // not a valid Guest Time

                        msgHdr = "Invalid Guest Time Request";

                        msgBody = "<BR><BR>Sorry, you must include at least one 30 Day Advance Guest in the group"
                                + "<BR>when making a tee time this far in advance.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }

            //
            //  If North Hills, check for days in adv based on day of week.  Allow guest times if past allowed days.
            //
            if (club.equals("northhills")) {

                int northTime = 0;
                int northDays = 0;

                //
                //  use the member's mship type to determine which 'days in advance' and 'time' values to use
                //
                verifySlot.getDaysInAdv(con, parm, userMship);        // get the days in adv & time data for this member

                //
                //   Get the days in advance and time of day values for the day of this tee time
                //
                if (slotParms.day.equals("Sunday")) {

                    northDays = parm.advdays1;
                    northTime = parm.advtime1;

                } else if (slotParms.day.equals("Monday")) {

                    northDays = parm.advdays2;
                    northTime = parm.advtime2;

                } else if (slotParms.day.equals("Tuesday")) {

                    northDays = parm.advdays3;
                    northTime = parm.advtime3;

                } else if (slotParms.day.equals("Wednesday")) {

                    northDays = parm.advdays4;
                    northTime = parm.advtime4;

                } else if (slotParms.day.equals("Thursday")) {

                    northDays = parm.advdays5;
                    northTime = parm.advtime5;

                } else if (slotParms.day.equals("Friday")) {

                    northDays = parm.advdays6;
                    northTime = parm.advtime6;

                } else {

                    northDays = parm.advdays7;
                    northTime = parm.advtime7;
                }

                thisTime = SystemUtils.getTime(con);                  // get the current adjusted time

                if ((date == Hdate1 || date == Hdate2b || date == Hdate3)
                        && ind == 7 && thisTime < northTime) {                            // if Holiday & 6 days in adv before time

                    mccskip = true;                   // ok to skip the 'days in advance' test (guest time), but check for guests

                } else {

                    if ((date == Hdate1 || date == Hdate2b || date == Hdate3) && ind > 7) {   // if Holiday & more than 6 days in adv

                        mccskip = true;                 // ok to skip the 'days in advance' test, but check for guests

                    } else {                    // NOT a Holiday - use configured values for this mship and day

                        if ((ind == northDays && thisTime < northTime) || ind > northDays) {

                            mccskip = true;     // ok to skip the 'days in advance' test, but check for guests
                        }
                    }
                }

                //
                //  If only guest times are allowed (beyond normal days in adv), then check here
                //
                if (mccskip == true && slotParms.guests < 2) {     // if less than 2 guests in request

                    msgHdr = "Invalid Guest Time Request";

                    msgBody = "<BR><BR>Sorry, you must specify at least 2 guests following your name<BR>when making a tee time this far in advance."
                            + "<BR><BR>Days = " + northDays + ", Time = " + northTime + "<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }        // end of North Hills custom



            //
            // *******************************************************************************
            //  Check member restrictions
            //
            //     First, find all restrictions within date & time constraints
            //     Then, find the ones for this day
            //     Then, find any for this member type or membership type (all players)
            //
            // *******************************************************************************
            //
            error = false;                             // init error indicator

            try {

                error = verifySlot.checkMemRests(slotParms, con);

            } catch (Exception e7) {

                msg = "Check Member Restrictions. ";

                dbError(out, e7, msg);
                return;
            }                             // end of member restriction tests

            if (error == true) {          // if we hit on a restriction

                msgHdr = "Member Restricted";

                msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time."
                        + "<BR><BR>This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b>"
                        + "<BR><BR>Please remove this player or try a different time.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }


            //
            //  If Medinah process custom restrictions
            //
            if (club.equals("medinahcc")) {

                //
                // *******************************************************************************
                //  Medinah CC - Check Contingent Member Restrictions
                //
                //     on return - 'medError' contains the error code
                //
                // *******************************************************************************
                //
                int medError = medinahCustom.checkContingent(slotParms);      // go check rest's

                if (medError > 0) {          // if we hit on a restriction

                    msgHdr = "Member Restricted";

                    msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.";

                    if (medError == 1) {
                        msgBody += "<BR><BR>A Family Member (8 - 11) must be accompanied by an adult.<br><br>";
                    }
                    if (medError == 2) {
                        msgBody += "<BR><BR>A Family Member (12 & 13) must be accompanied by an adult.<br><br>";
                    }
                    if (medError == 3) {
                        msgBody += "<BR><BR>A Family Member (14 - 16) must be accompanied by a Member.<br><br>";
                    }
                    if (medError == 5 || medError == 10) {
                        msgBody += "<BR><BR>A Family Member (17 and Over) must be accompanied by a Member.<br><br>";
                    }
                    if (medError == 6) {
                        msgBody += "<BR><BR>A Family Member (12 & 13) must be accompanied by a Member.<br><br>";
                    }
                    if (medError == 7) {
                        msgBody += "<BR><BR>A Family Member (8 - 11) must be accompanied by an adult.<br><br>";
                    }
                    if (medError == 4 || medError == 8 || medError == 9) {
                        msgBody += "<BR><BR>A Spouse must be accompanied by a Member.<br><br>";
                    }
                    if (medError == 11) {
                        msgBody += "<BR><BR>A Family Member (14 - 16) must be accompanied by an adult.<br><br>";
                    }

                    msgBody += "Please remove this player or try a different time.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

            }       // end of IF Medinah


            //
            // *******************************************************************************
            //  Check Member Number restrictions
            //
            //     First, find all restrictions within date & time constraints
            //     Then, find the ones for this day
            //     Then, check all players' member numbers against all others in the time period
            //
            // *******************************************************************************
            //
            error = false;                             // init error indicator

            try {

                error = verifySlot.checkMemNum(slotParms, con);

            } catch (Exception e7) {

                msg = "Check Member Number Restrictions. ";

                dbError(out, e7, msg);
                return;
            }                             // end of member restriction tests

            if (error == true) {          // if we hit on a restriction

                msgHdr = "Member Restricted by Member Number";

                msgBody = "<BR><BR>Sorry, ";

                if (!slotParms.pnum1.equals("")) {
                    msgBody += "<b>" + slotParms.pnum1 + "</b> ";
                }
                if (!slotParms.pnum2.equals("")) {
                    msgBody += "<b>" + slotParms.pnum2 + "</b> ";
                }
                if (!slotParms.pnum3.equals("")) {
                    msgBody += "<b>" + slotParms.pnum3 + "</b> ";
                }
                if (!slotParms.pnum4.equals("")) {
                    msgBody += "<b>" + slotParms.pnum4 + "</b> ";
                }
                if (!slotParms.pnum5.equals("")) {
                    msgBody += "<b>" + slotParms.pnum5 + "</b> ";
                }

                msgBody += "is/are restricted from playing during this time because the"
                        + "<BR>number of members with the same member number has exceeded the maximum allowed.<br><br>"
                        + "This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b>"
                        + "<BR><BR>Please remove this player(s) or try a different time.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }

            //
            //***********************************************************************************************
            //
            //    Now check if any of the players are already scheduled today (only 1 res per day)
            //
            //***********************************************************************************************
            //
            slotParms.hit = false;                             // init error indicator
            slotParms.hit2 = false;                             // init error indicator
            String tmsg = "";
            int thr = 0;
            int tmin = 0;

            try {

                verifySlot.checkSched(slotParms, con);

            } catch (Exception e21) {

                msg = "Check Members Already Scheduled. ";

                dbError(out, e21, msg);
                return;
            }

            if (slotParms.hit == true || slotParms.hit2 == true || slotParms.hit3 == true) { // if we hit on a duplicate res

                if (slotParms.time2 != 0) {                                  // if other time was returned

                    thr = slotParms.time2 / 100;                      // set time string for message
                    tmin = slotParms.time2 - (thr * 100);
                    if (thr == 12) {
                        if (tmin < 10) {
                            tmsg = thr + ":0" + tmin + " PM";
                        } else {
                            tmsg = thr + ":" + tmin + " PM";
                        }
                    } else {
                        if (thr > 12) {
                            thr = thr - 12;
                            if (tmin < 10) {
                                tmsg = thr + ":0" + tmin + " PM";
                            } else {
                                if (tmin < 10) {
                                    tmsg = thr + ":0" + tmin + " PM";
                                } else {
                                    tmsg = thr + ":" + tmin + " PM";
                                }
                            }
                        } else {
                            if (tmin < 10) {
                                tmsg = thr + ":0" + tmin + " AM";
                            } else {
                                tmsg = thr + ":" + tmin + " AM";
                            }
                        }
                    }
                    if (!slotParms.course2.equals("")) {        // if course provided

                        tmsg = tmsg + " on the " + slotParms.course2 + " course";
                    }
                }

                msgHdr = "Member Already Playing";

                if (slotParms.rnds > 1) {       // if multiple rounds per day supported
                    if (slotParms.hit3 == true) {       // if rounds too close together
                        msgBody = "<BR>Sorry, <b>" + slotParms.player + "</b> is scheduled to play another round within " + slotParms.hrsbtwn + " hours.<br><br>"
                                + slotParms.player + " is already scheduled to play on this date at <b>" + tmsg + "</b>.<br><br>";
                    } else {

                        msgBody = "<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play the maximum number of times.<br><br>"
                                + "A player can only be scheduled " + slotParms.rnds + " times per day.<br><br>";
                    }
                } else {
                    if (slotParms.hit2 == true) {
                        if (club.equals("oldoaks")) {
                            msgBody = "<BR>Sorry, <b>" + slotParms.player + "</b> is part of a tee time request for this date.<br><br>";
                        } else if (!lotteryText.equals("")) {
                            msgBody = "<BR>Sorry, <b>" + slotParms.player + "</b> is part of a " + lotteryText + " for this date.<br><br>";
                        } else {
                            msgBody = "<BR>Sorry, <b>" + slotParms.player + "</b> is part of a lottery request for this date.<br><br>";
                        }
                    } else {
                        msgBody = "<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play on this date at <b>" + tmsg + "</b>.<br><br>";
                    }
                    msgBody += "A player can only be scheduled once per day.<br><br>";
                }


                msgBody += "Please remove this player(s) or try a different time."
                        + "<BR>Contact the Golf Shop if you have any questions.<br><br>"
                        + "If you are already scheduled for this date and would like to remove yourself<br>from that tee time, use the 'Go Back' button to return to the tee sheet and <br>"
                        + "locate the time stated above, or click on the 'My Tee Times' tab.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }


            //
            //   If Merion and East course, then check if any other family members are scheduled today - not allowed.
            //
            if (club.equals("merion") && slotParms.course.equals("East")) {

                slotParms.hit = false;                             // init error indicator

                try {

                    verifySlot.checkMerionSched(slotParms, con);

                } catch (Exception e21) {

                    msg = "Check Merion Members Already Scheduled. ";

                    dbError(out, e21, msg);
                    return;
                }

                if (slotParms.hit == true) {      // if another family member is already booked today

                    msgHdr = "Member Already Scheduled";

                    msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> already has a family member scheduled to play today."
                            + "<BR>Only one player per membership is allowed each day."
                            + "<BR><BR>Please remove this player or try a different date.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }


                //
                //  Merion - Now check if more than 7 days in adv and a w/e, no more than 4 adv tee times per day
                //
                if (indReal > 7 && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday"))) {   // if more than 7 days and a w/e

                    error = false;                             // init error indicator

                    try {

                        error = verifySlot.checkMerionWE(slotParms, con);

                    } catch (Exception e21) {

                        msg = "Check Merion Tee Times Already Scheduled. ";

                        dbError(out, e21, msg);
                        return;
                    }

                    if (error == true) {      // if another family member is already booked today

                        msgHdr = "Advance Tee Time Limit";

                        msgBody = "<BR><BR>Sorry, there are already 4 advance tee times scheduled for this day."
                                + "<BR><BR>Please try a different date.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }

            }


            //
            //***********************************************************************************************
            //
            //    Now check all players for 'days in advance' - based on membership types
            //
            //***********************************************************************************************
            //
            if (!slotParms.mship1.equals("") || !slotParms.mship2.equals("") || !slotParms.mship3.equals("")
                    || !slotParms.mship4.equals("") || !slotParms.mship5.equals("")) {

                //
                //  skip if Oakmont guest time, or Milwaukee Guest Time
                //
                if (!club.equals("oakmont") || oakskip == false) {

                    if (mccskip == false) {                                 // skip if Milwaukee Guest Time

                        try {

                            error = verifySlot.checkDaysAdv(slotParms, con);

                        } catch (Exception e21) {

                            msg = "Check Days in Advance Error. ";

                            dbError(out, e21, msg);
                            return;
                        }

                        /*
                        if (club.equals( "valleycc" ) && shortDate > 416 && shortDate < 917) {   // Ladies custom for summer season
                        
                        if (error == true && indReal < 15 && time > 729 && time < 1100 &&
                        slotParms.day.equals( "Friday" ) ) {
                        
                        error = false;
                        }
                        }
                         */


                        if (error == true) {

                            msgHdr = "Days in Advance Exceeded for Member";

                            msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to be part of a tee time this far in advance.";

                            if (x > 0) {
                                msgBody += "<br>You can use an 'X' to reserve this position until the player is allowed.<br><br>";
                            } else {
                                msgBody += "<br>Contact the golf shop if you wish to add this person at this time.<br><br>";
                            }

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;

                        } else {

                            //
                            //  Lakewood CC - if more than 1 day in advance, there must be at least one Primary Member in the
                            //                group.  That is, there cannot be spouses only.
                            //
                            if (club.equals("lakewood") && slotParms.ind > 1) {

                                //
                                //  Check if at least one Spouse in group and no Primary Members
                                //
                                if (slotParms.mship1.endsWith("Spouse") || slotParms.mship2.endsWith("Spouse") || slotParms.mship3.endsWith("Spouse")
                                        || slotParms.mship4.endsWith("Spouse") || slotParms.mship5.endsWith("Spouse")) {

                                    if ((!slotParms.mship1.equals("") && !slotParms.mship1.endsWith("Spouse"))
                                            || (!slotParms.mship2.equals("") && !slotParms.mship2.endsWith("Spouse"))
                                            || (!slotParms.mship3.equals("") && !slotParms.mship3.endsWith("Spouse"))
                                            || (!slotParms.mship4.equals("") && !slotParms.mship4.endsWith("Spouse"))
                                            || (!slotParms.mship5.equals("") && !slotParms.mship5.endsWith("Spouse"))) {

                                        boolean lakewoodOK = true;      // just do this to do something here

                                    } else {     // error

                                        msgHdr = "Days in Advance Exceeded for Spouse";

                                        msgBody = "<BR><BR>Sorry, at least one Member must be included in the group when"
                                                + "<BR>scheduling a tee time more than 1 day in advance.<BR><BR>";

                                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                                        returnToSlot(mobile, out, slotParms);
                                        return;
                                    }
                                }
                            }         // end of IF lakewood
                        }
                    }
                }

                //
                //  if Belle Haven - check for 'Elective' membership types - limited to 10 rounds per year on w/e's
                //
                if (club.equals("bellehaven")) {

                    try {

                        error = verifySlot.checkBelleHaven(slotParms, con);

                    } catch (Exception e22) {

                        msg = "Check Belle Haven Error. ";

                        dbError(out, e22, msg);
                        return;
                    }

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Weekend Tee Time Limit Exceeded for Member";

                        msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> is an Elective member and has<BR>already played 10 times on weekends or holidays this year."
                                + "<BR><BR>Remove this player and try again.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }


            //
            //***********************************************************************************************
            //
            //    Other Custom Checks
            //
            //***********************************************************************************************

            //
            //   Inverness Club - if more than 30 days out - must have a guest in the tee time
            //
            if (club.equals("invernessclub") && slotParms.ind > 30 && slotParms.guests == 0) {

                msgHdr = "Days in Advance Exceeded for Member";

                msgBody = "<BR><BR>Sorry, you cannot reserve a tee time more than 30 days <br>"
                        + "in advance unless there is at least one guest included.<BR><BR>";

                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                returnToSlot(mobile, out, slotParms);
                return;
            }


            //
            //  Olympic Club - do not allow a Jr Golf member without an adult
            //
            if (club.equals("olyclub")) {

                if (slotParms.mship1.equals("Jr Golf")) {

                    error = true;

                    if (slotParms.mship2.equals("MHGP") || slotParms.mship2.equals("MNHGP") || slotParms.mship2.equals("Member") || slotParms.mship2.equals("WSGPS")
                            || slotParms.mship3.equals("MHGP") || slotParms.mship3.equals("MNHGP") || slotParms.mship3.equals("Member") || slotParms.mship3.equals("WSGPS")
                            || slotParms.mship4.equals("MHGP") || slotParms.mship4.equals("MNHGP") || slotParms.mship4.equals("Member") || slotParms.mship4.equals("WSGPS")
                            || slotParms.mship5.equals("MHGP") || slotParms.mship5.equals("MNHGP") || slotParms.mship5.equals("Member") || slotParms.mship5.equals("WSGPS")) {

                        error = false;
                    }
                }

                if (slotParms.mship2.equals("Jr Golf")) {

                    error = true;

                    if (slotParms.mship1.equals("MHGP") || slotParms.mship1.equals("MNHGP") || slotParms.mship1.equals("Member") || slotParms.mship1.equals("WSGPS")
                            || slotParms.mship3.equals("MHGP") || slotParms.mship3.equals("MNHGP") || slotParms.mship3.equals("Member") || slotParms.mship3.equals("WSGPS")
                            || slotParms.mship4.equals("MHGP") || slotParms.mship4.equals("MNHGP") || slotParms.mship4.equals("Member") || slotParms.mship4.equals("WSGPS")
                            || slotParms.mship5.equals("MHGP") || slotParms.mship5.equals("MNHGP") || slotParms.mship5.equals("Member") || slotParms.mship5.equals("WSGPS")) {

                        error = false;
                    }
                }

                if (slotParms.mship3.equals("Jr Golf")) {

                    error = true;

                    if (slotParms.mship2.equals("MHGP") || slotParms.mship2.equals("MNHGP") || slotParms.mship2.equals("Member") || slotParms.mship2.equals("WSGPS")
                            || slotParms.mship1.equals("MHGP") || slotParms.mship1.equals("MNHGP") || slotParms.mship1.equals("Member") || slotParms.mship1.equals("WSGPS")
                            || slotParms.mship4.equals("MHGP") || slotParms.mship4.equals("MNHGP") || slotParms.mship4.equals("Member") || slotParms.mship4.equals("WSGPS")
                            || slotParms.mship5.equals("MHGP") || slotParms.mship5.equals("MNHGP") || slotParms.mship5.equals("Member") || slotParms.mship5.equals("WSGPS")) {

                        error = false;
                    }
                }

                if (slotParms.mship4.equals("Jr Golf")) {

                    error = true;

                    if (slotParms.mship2.equals("MHGP") || slotParms.mship2.equals("MNHGP") || slotParms.mship2.equals("Member") || slotParms.mship2.equals("WSGPS")
                            || slotParms.mship3.equals("MHGP") || slotParms.mship3.equals("MNHGP") || slotParms.mship3.equals("Member") || slotParms.mship3.equals("WSGPS")
                            || slotParms.mship1.equals("MHGP") || slotParms.mship1.equals("MNHGP") || slotParms.mship1.equals("Member") || slotParms.mship1.equals("WSGPS")
                            || slotParms.mship5.equals("MHGP") || slotParms.mship5.equals("MNHGP") || slotParms.mship5.equals("Member") || slotParms.mship5.equals("WSGPS")) {

                        error = false;
                    }
                }

                if (error == true) {

                    msgHdr = "Invalid Request";

                    msgBody = "<BR><BR>Sorry, a Jr Golf member must be <br>"
                            + "accompanied by a non Jr Golf member.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }        // end of Olympic Club


            //
            //   Scioto CC - no guests allowed more than 4 or 5 days in advance based on day of week (also see more code below after guests assigned).
            //
      /*
            if (club.equals( "sciotocc" ) && shortDate > 500 && shortDate < 1016 && slotParms.guests > 0 && slotParms.ind > 4) {
            
            int sciotoDays = 5;
            
            if (slotParms.day.equals("Sunday") || slotParms.day.equals("Monday")) {
            
            sciotoDays = 4;           // guests allowed 4 days or less
            }
            
            if (slotParms.ind > sciotoDays) {       // display custom member notice if no guests allowed in this tee time
            
            msgHdr = "Guests Not Allowed";
            
            msgBody = "<BR><BR>Sorry, guests are not allowed to be part of a tee time this far in advance." +
            "<BR><BR>Please remove the guests or contact the golf shop for assistance.<BR><BR>";
            
            buildError(msgHdr, msgBody, mobile, out);       // output the error message
            
            returnToSlot(mobile, out, slotParms);
            return;
            }
            }
             */


            //
            //  If North Shore - check for 'guest-only' times
            //
            if (club.equals("northshore")) {

                try {

                    error = verifySlot.checkNSGuestTimes(slotParms, con);

                } catch (Exception e21) {

                    msg = "Check North Shore Guest Times. ";

                    dbError(out, e21, msg);
                    return;
                }

                if (error == true) {          // if we hit on a violation

                    msgHdr = "Guest Time Violation";

                    msgBody = "<BR><BR>Sorry, you must include at least 2 guests during this time."
                            + "<BR><BR>Please contact the golf shop if you have any questions.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            //
            //  If Ritz-Carlton - check for max 'Club Golf' and 'Recip' times this hour
            //
            if (club.equals("ritzcarlton")) {

                error = verifySlot.checkRitz(slotParms, con);

                if (error == true) {          // if we hit on a violation

                    msgHdr = "Special Tee Time Quota Exceeded";

                    msgBody = "<BR><BR>Sorry, there are already 2 tee times with Club Golf members<BR>or Recip guests scheduled this hour."
                            + "<BR><BR>Please select a different time of day, or change the players.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            //
            //  If Skaneateles - check for Dependent Restriction
            //
            if (club.equals("skaneateles")) {

                error = verifySlot.checkSkaneateles(slotParms);

                if (error == true) {          // if we hit on a violation

                    msgHdr = "Unaccompanied Dependents Not Allowed";

                    msgBody = "<BR><BR>Sorry, dependents must be accompanied by an adult after 4:00 PM each day."
                            + "<BR><BR>Please select a different time of day, or change the players.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            //
            //  If Oakland Hills - check for Dependents - must be accompanied by adult (always)
            //
            if (club.equals("oaklandhills")) {

                error = verifySlot.checkOaklandKids(slotParms);

                if (error == true) {          // if we hit on a violation

                    msgHdr = "Unaccompanied Dependents Not Allowed";

                    msgBody = "<BR><BR>Sorry, dependents must be accompanied by an adult."
                            + "<BR><BR>Please contact the golf shop if you have any questions.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

                //
                //  Check for advance times if more than 5 days in adv (limit varies based on day of week and guests or no guests)
                //
                if (slotParms.ind > 5) {        // if more than 5 days in advance

                    error = verifySlot.checkOaklandAdvTime1(slotParms, con);

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Member Has Already Used An Advance Request";

                        msgBody = "<BR><BR>Sorry, each membership is entitled to only one advance tee time request."
                                + "<BR>" + slotParms.player + " has already used his/her advance tee time request for the season."
                                + "<BR><BR>Please contact the golf shop if you have any questions.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }

                    error = verifySlot.checkOaklandAdvTime2(slotParms, con);

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Maximum Allowed Advanced Tee Times Exist";

                        msgBody = "<BR><BR>Sorry, the maximum number of advanced tee time requests already exist on the selected date."
                                + "<BR><BR>Please contact the golf shop if you have any questions.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }

                //
                //   No X's allowed more than 7 days in advance
                //
                if (slotParms.ind > 7) {

                    //
                    //  Cannot have X's in tee time!!
                    //
                    if (slotParms.player1.equalsIgnoreCase("x") || slotParms.player2.equalsIgnoreCase("x")
                            || slotParms.player3.equalsIgnoreCase("x") || slotParms.player4.equalsIgnoreCase("x")
                            || slotParms.player5.equalsIgnoreCase("x")) {

                        msgHdr = "Invalid Player Selection";

                        msgBody = "<BR><BR>Sorry, you cannot reserve player positions with an X more than 7 days in advance."
                                + "<BR><BR>Please contact the golf shop if you have any questions.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }           // end of IF Oakland Hills

            //
            //  If CC of the Rockies & Catamount Ranch - check for max number of advance tee times
            //
            if ((club.equals("ccrockies") || club.equals("catamount") || club.equals("sonnenalp")) && slotParms.ind > 0) {       // if not today

                error = verifySlot.checkRockies(slotParms, con);

                if (error == true) {          // if we hit on a violation

                    msgHdr = "Member Already Has Max Allowed Advance Requests";

                    if (club.equals("sonnenalp")) {
                        msgBody = "<BR>Sorry, " + slotParms.player + " already has 12 advance tee time requests scheduled.";
                    } else {
                        msgBody = "<BR>Sorry, " + slotParms.player + " already has 5 advance tee time requests scheduled.";
                    }

                    msgBody += "<BR><BR>Please remove this player from your request.<BR>Contact the golf shop if you have any questions.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }           // end of IF CC of the Rockies

            //
            //  Cherry Hills - custom member type and membership restrictions
            //
            if (club.equals("cherryhills")) {

                error = verifySlot.checkCherryHills(slotParms);    // process custom restrictions

                if (error == true) {

                    msgHdr = "Player Not Allowed";

                    msgBody = "<BR><BR>Sorry, one or more players are not allowed to be part of a tee time for this day and time.";

                    if (slotParms.day.equals("Monday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Friday")) {
                        msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
                    } else {
                        if (slotParms.day.equals("Tuesday")) {
                            if (slotParms.time > 1100) {
                                msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
                            } else {
                                msgBody += "<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 11 AM on Tuesdays.";
                            }
                        } else {
                            if (slotParms.day.equals("Thursday")) {
                                if (slotParms.time > 1000) {
                                    msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
                                } else {
                                    msgBody += "<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 10 AM on Thursdays.";
                                }
                            } else {
                                if (slotParms.day.equals("Sunday")) {
                                    if (slotParms.time > 1000) {
                                        msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
                                    } else {
                                        msgBody += "<BR><BR>Only Members may be included in a tee time before 10 AM on Sundays.";
                                    }
                                } else {       // Saturday or Holiday
                                    if (slotParms.time > 1100) {
                                        msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
                                    } else {
                                        msgBody += "<BR><BR>Player not allowed to make a tee time more than 24 hours in advance on Saturdays and Holidays before 11 AM.";
                                    }
                                }
                            }
                        }
                    }
                    msgBody += "<BR><BR>Please change players or select a different day or time of day.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }

            //
            //  Hazeltine - check for consecutive singles or 2-somes
            //
            if (club.equals("hazeltine")) {

                if (slotParms.player3.equals("") && slotParms.player4.equals("") && slotParms.player5.equals("")) {  // if 1 or 2 players

                    error = verifySlot.checkHazGrps(slotParms, con);    // process custom restriction

                    if (error == true) {

                        msgHdr = "Request Not Allowed";

                        msgBody = "<BR><BR>Sorry, there is already a small group immediately before or after this time.<BR>There cannot be 2 consecutive small groups during this time."
                                + "<BR><BR>Please add players or select a different time of day.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
            }


            //
            //  Bearpath - check for 'CD plus' member types
            //
            if (club.equals("bearpath")) {

                error = verifyCustom.checkBearpathMems(slotParms);    // process custom restriction

                if (error == true) {

                    msgHdr = "Request Not Allowed";

                    msgBody = "<BR><BR>Sorry, CD Plus members are not allowed to play at this time<BR>unless accompanied by an authorized member."
                            + "<BR><BR>Please select a different time of day.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }
            }


            //
            //  Fort Collins 5-some checks
            //
            if (club.equals("fortcollins")) {

                error = false;

                if (!slotParms.player1.equals(slotParms.oldPlayer1)
                        || !slotParms.player2.equals(slotParms.oldPlayer2)
                        || !slotParms.player3.equals(slotParms.oldPlayer3)
                        || !slotParms.player4.equals(slotParms.oldPlayer4)
                        || !slotParms.player5.equals(slotParms.oldPlayer5)) {   // if group not already accepted by pro

                    if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) {  // if 5-some

                        if (slotParms.course.equals("Greeley CC")) {       // if Greeley CC course

                            //
                            //  5-some on Greeley course - cannot be all Fort Collins members
                            //
                            if (!slotParms.mtype1.endsWith("Greeley") && !slotParms.mtype2.endsWith("Greeley")
                                    && !slotParms.mtype3.endsWith("Greeley") && !slotParms.mtype4.endsWith("Greeley")
                                    && !slotParms.mtype5.endsWith("Greeley")) {

                                error = true;     // no FC members - error
                            }

                        } else if (slotParms.course.equals("Fox Hill CC")) {

                            //
                            //  5-some on Fox Hill course - cannot be all non-Fox Hill members
                            //
                            if (!slotParms.mtype1.endsWith("Fox Hill") && !slotParms.mtype2.endsWith("Fox Hill")
                                    && !slotParms.mtype3.endsWith("Fox Hill") && !slotParms.mtype4.endsWith("Fox Hill")
                                    && !slotParms.mtype5.endsWith("Fox Hill")) {

                                error = true;     // no FC members - error
                            }

                        } else {       // Fort Collins Course

                            //
                            //  5-some on Fort Collins course - cannot be all Greeley members
                            //
                            error = true;       // assume error

                            if ((!slotParms.mtype1.equals("") && !slotParms.mtype1.endsWith("Greeley") && !slotParms.mtype1.endsWith("Fox Hill"))
                                    || (!slotParms.mtype2.equals("") && !slotParms.mtype2.endsWith("Greeley") && !slotParms.mtype2.endsWith("Fox Hill"))
                                    || (!slotParms.mtype3.equals("") && !slotParms.mtype3.endsWith("Greeley") && !slotParms.mtype3.endsWith("Fox Hill"))
                                    || (!slotParms.mtype4.equals("") && !slotParms.mtype4.endsWith("Greeley") && !slotParms.mtype4.endsWith("Fox Hill"))
                                    || (!slotParms.mtype5.equals("") && !slotParms.mtype5.endsWith("Greeley") && !slotParms.mtype5.endsWith("Fox Hill"))) {

                                error = false;     // at least one FC member - ok
                            }
                        }

                        if (error == true) {

                            msgHdr = "Request Not Allowed";

                            msgBody = "<BR><BR>Sorry, 5-somes are not allowed without a member from that club."
                                    + "<BR><BR>Please limit the request to 4 players or include a member of the club.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;
                        }
                    }
                }
            }     // end of IF Fort Collins


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
                //***********************************************************************************************
                //
                if (slotParms.guests > 0) {

                    //
                    //  At least 1 guest requested in tee time.  If The Lakes, then no guests allowed before 11 AM if more than 5 days in advance.
                    //   Also, if 7 days in advance, they cannot book guest times before 7:30 AM.
                    //
                    if (club.equals("lakes")) {

                        if (slotParms.ind == 7 && todayTime < 930) {   // if 7 days in advance and earlier than 7:30 AM PT

                            msgHdr = "Request Not Allowed";

                            msgBody = "<BR><BR>Sorry, you cannot book guest times before 7:30 AM when booking 7 days in advance."
                                    + "<BR><BR>Please remove the guests or return to the tee sheet.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;

                        } else if (slotParms.ind > 5 && slotParms.time < 1101) {

                            msgHdr = "Request Not Allowed";

                            msgBody = "<BR><BR>Sorry, guests are not allowed this far in advance until after 11:00 AM."
                                    + "<BR><BR>Please remove the guests or return to the tee sheet.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;
                        }
                    }     // end of IF The Lakes



                    //
                    //  If no members requested and Unaccompanied Guests are ok at this club
                    //
                    if (slotParms.members == 0 && parm.unacompGuest == 1) {

                        if (!slotParms.g1.equals("")) {  // if player is a guest

                            slotParms.userg1 = user;        // set username for guests
                        }
                        if (!slotParms.g2.equals("")) {

                            slotParms.userg2 = user;
                        }
                        if (!slotParms.g3.equals("")) {

                            slotParms.userg3 = user;
                        }
                        if (!slotParms.g4.equals("")) {

                            slotParms.userg4 = user;
                        }
                        if (!slotParms.g5.equals("")) {

                            slotParms.userg5 = user;
                        }

                        //
                        //  If Hazeltine National, then process the Unaccompanied Guests (Sponsored Group)
                        //
                        if (club.equals("hazeltine")) {      // if Hazeltine National

                            int rcode = 0;

                            try {

                                rcode = verifySlot.checkSponsGrp(slotParms, con);  // verify Sponsored Group for Hazeltine
                            } catch (Exception e29) {

                                msg = "Check Hazeltine's Sponsored Group - Member_slot. ";

                                dbError(out, e29, msg);
                                return;
                            }

                            if (rcode > 0) {          // if we hit on a violation

                                msgHdr = "Restriction For Sponsored Group Request";

                                msgBody = "<BR><BR>Your request for a Sponsored Group has been rejected for the following reason:<BR>";

                                if (rcode == 1) {
                                    msgBody += "The maximum number of Sponsored Groups have already been scheduled for this day.<br><br>";
                                } else {
                                    if (rcode == 2) {
                                        msgBody += "Sponsored Groups are not allowed at this time of day.<br><br>";
                                    } else {
                                        msgBody += "You already have 2 Sponsored Groups scheduled today.<br><br>";
                                    }
                                }
                                msgBody += "Please change this request or try a different date.<br>"
                                        + "Contact the  golf shop if you have any questions.<BR><BR>";

                                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                                returnToSlot(mobile, out, slotParms);
                                return;
                            }
                        }

                    } else {

                        //
                        //  At least 1 guest requested in tee time.  If Interlachen or Columbine, check for a 5-some request.
                        //  Guests are not allowed in any 5-some group.  Interlachen - allows up to 2 guests.
                        //
//            **** Custom removed at club's request 9/26/14 ****
//                        if (club.equals("columbine")) {         // if Columbine
//
//                            if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) {  // if 5-some
//
//                                if (!slotParms.player1.equals(slotParms.oldPlayer1)
//                                        || !slotParms.player2.equals(slotParms.oldPlayer2)
//                                        || !slotParms.player3.equals(slotParms.oldPlayer3)
//                                        || !slotParms.player4.equals(slotParms.oldPlayer4)
//                                        || !slotParms.player5.equals(slotParms.oldPlayer5)) {   // if group not already accepted by pro
//
//                                    msgHdr = "Request Not Allowed";
//
//                                    msgBody = "<BR><BR>Sorry, guests are not allowed in a 5-some."
//                                            + "<BR><BR>Please limit the request to 4 players or remove the guest(s).<BR><BR>";
//
//                                    buildError(msgHdr, msgBody, mobile, out);       // output the error message
//
//                                    returnToSlot(mobile, out, slotParms);
//                                    return;
//                                }
//                            }
//                        }     // end of IF Columbine
                        
                        
//              
//            **** CUSTOM HANDLED IN verifyCustom.checkCustoms1 NOW **** 
//             
//
//                        if (club.equals("interlachen")) {        // if Interlachen
//
//                            if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x") && slotParms.guests > 2) {  // if 5-some with > 2 guests
//
//                                if (!slotParms.player1.equals(slotParms.oldPlayer1)
//                                        || !slotParms.player2.equals(slotParms.oldPlayer2)
//                                        || !slotParms.player3.equals(slotParms.oldPlayer3)
//                                        || !slotParms.player4.equals(slotParms.oldPlayer4)
//                                        || !slotParms.player5.equals(slotParms.oldPlayer5)) {   // if group not already accepted by pro
//
//                                    msgHdr = "Request Not Allowed";
//
//                                    msgBody = "<BR><BR>Sorry, no more than 2 guests are allowed in a 5-some."
//                                            + "<BR><BR>Please limit the request to 4 players or 2 guests.<BR><BR>";
//
//                                    buildError(msgHdr, msgBody, mobile, out);       // output the error message
//
//                                    returnToSlot(mobile, out, slotParms);
//                                    return;
//                                }
//                            }
//                        }     // end of IF Interlachen

                        //
                        //  At least 1 guest requested in tee time.  If Fort Collins - Greeley course, check for a 5-some request.
                        //  More than 1 Guest is not allowed in any 5-some group.
                        //
                        /*
                        if (club.equals("fortcollins") && slotParms.course.equals("Greeley CC") && slotParms.guests > 1) {

                            if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) {  // if 5-some

                                if (!slotParms.player1.equals(slotParms.oldPlayer1)
                                        || !slotParms.player2.equals(slotParms.oldPlayer2)
                                        || !slotParms.player3.equals(slotParms.oldPlayer3)
                                        || !slotParms.player4.equals(slotParms.oldPlayer4)
                                        || !slotParms.player5.equals(slotParms.oldPlayer5)) {   // if group not already accepted by pro

                                    msgHdr = "Request Not Allowed";

                                    msgBody = "<BR><BR>Sorry, you may not have more than one guest in a 5-some."
                                            + "<BR><BR>Please limit the request to 4 players or remove the guest(s).<BR><BR>";

                                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                                    returnToSlot(mobile, out, slotParms);
                                    return;
                                }
                            }
                        }     // end of IF Fort Collins
                        */



                        if (slotParms.members > 0) {     // if at least one member

                            //
                            //  Both guests and members specified (member verified above) - determine guest owners by order
                            //
                            gi = 0;
                            memberName = "";

                            while (gi < 5) {                  // cycle thru arrays and find guests/members

                                if (!slotParms.gstA[gi].equals("")) {

                                    usergA[gi] = memberName;       // get the last players username, if any
                                } else {
                                    usergA[gi] = "";               // init array entry
                                }
                                if (!memA[gi].equals("")) {

                                    memberName = memA[gi];        // get players username
                                }
                                gi++;
                            }
                            slotParms.userg1 = usergA[0];        // set usernames for guests in teecurr
                            slotParms.userg2 = usergA[1];
                            slotParms.userg3 = usergA[2];
                            slotParms.userg4 = usergA[3];
                            slotParms.userg5 = usergA[4];
                        }

                        if (slotParms.members > 1 || !slotParms.g1.equals("")) {  // if multiple members OR slot 1 is a guest

                            //
                            //  At least one guest and 2 members have been specified, or P1 is a guest.
                            //  Prompt user to verify the order.
                            //
                            //  Only require positioning if a POS system was specified for this club (saved in Login)
                            //
                            if (new_skin && mobile == 0) {

                                // Pull the arrays into local variable, incase we want to use them later
                                String[] player_a = slotParms.getPlayerArray(5);
                                String[] pcw_a = slotParms.getCwArray(5);
                                int[] p9_a = slotParms.getP9Array(5);
                                String[] userg_a = slotParms.getUsergArray(5);
                                int[] guest_id_a = slotParms.getGuestIdArray(5);

                                // Fill that field map with values that will be used when calling back
                                hidden_field_map.put("skip8", "yes");
                                hidden_field_map.put("date", date);
                                hidden_field_map.put("time", time);
                                hidden_field_map.put("mm", mm);
                                hidden_field_map.put("yy", yy);
                                hidden_field_map.put("index", index);
                                hidden_field_map.put("p5", slotParms.p5);
                                hidden_field_map.put("course", slotParms.course);
                                hidden_field_map.put("returnCourse", slotParms.returnCourse);
                                hidden_field_map.put("day", slotParms.day);
                                hidden_field_map.put("fb", fb);
                                hidden_field_map.put("notes", slotParms.notes);
                                hidden_field_map.put("hide", slotParms.hides);
                                hidden_field_map.put("jump", jump);
                                hidden_field_map.put("displayOpt", displayOpt);
                                hidden_field_map.put("player%", player_a);
                                hidden_field_map.put("p%cw", pcw_a);
                                hidden_field_map.put("p9%", p9_a);
                                hidden_field_map.put("userg%", userg_a);
                                hidden_field_map.put("guest_id%", guest_id_a);
                                hidden_field_map.put("submitForm", "YES - continue");

                                // Build the player list
                                String player_list_html = "<ul class=\"indented_list\">";
                                for (int i2 = 0; i2 < player_a.length; i2++) {
                                    if (!player_a[i2].equals("")) {
                                        player_list_html += "<li class=\"" + ((!userg_a[i2].equals("")) ? "guest_item" : "player_item") + "\">" + player_a[i2] + "</li>";
                                    }
                                }
                                player_list_html += "</ul>";

                                // Fill the result map
                                result_map.put("title", "Player/Guest Association Prompt");
                                result_map.put("prompt_yes_no", true);
                                result_map.put("successful", false);
                                result_map.put("callback_map", hidden_field_map);
                                if (!slotParms.g1.equals("") && !posType.equals("") && !slotParms.oldPlayer1.equals(slotParms.player1)) {
                                    result_map.put("message_array", new String[]{
                                                "Guests must be specified <b>immediately after</b> the member they belong to.",
                                                "The first player position cannot contain a guest.",
                                                "Please correct the order of players.",
                                                "<b>This is what you requested:</b>",
                                                player_list_html,
                                                "Would you like to process the request as is?"});
                                } else {
                                    result_map.put("message_array", new String[]{
                                                "Guests should be specified <b>immediately after</b> the member they belong to.",
                                                "<b>Please verify the following order:</b>",
                                                player_list_html,
                                                "Would you like to process the request as is?"});

                                }

                                // Send results as json string
                                out.print(gson_obj.toJson(result_map));

                                out.close();
                                return;

                            } else { // old skin / mobile
                                msgHdr = "Player/Guest Association Prompt";

                                //
                                //  if player1 is a guest & POS & not already assigned
                                //
                                if (!slotParms.g1.equals("") && !posType.equals("") && !slotParms.oldPlayer1.equals(slotParms.player1)) {

                                    msgBody = "<BR>Guests must be specified <b>immediately after</b> the member they belong to.<br><br>"
                                            + "The first player position cannot contain a guest.<BR>Please correct the order of players.<br>"
                                            + "This is what you requested:";

                                } else {

                                    msgBody = "<BR>Guests should be specified <b>immediately after</b> the member they belong to.<br><br>"
                                            + "Please verify the following order:";
                                }
                                msgBody += "<BR><BR>" + slotParms.player1 + " <BR>" + slotParms.player2 + " <BR>";
                                if (!slotParms.player3.equals("")) {
                                    msgBody += slotParms.player3 + " <BR>";
                                }
                                if (!slotParms.player4.equals("")) {
                                    msgBody += slotParms.player4 + " <BR>";
                                }
                                if (!slotParms.player5.equals("")) {
                                    msgBody += slotParms.player5 + " <BR>";
                                }

                                if (slotParms.g1.equals("") || posType.equals("") || slotParms.oldPlayer1.equals(slotParms.player1)) {

                                    msgBody += "<BR>Would you like to process the request as is?";
                                }

                                msgBody += "<BR>";

                                buildError(msgHdr, msgBody, mobile, out);       // output the error message


                                //
                                //  Return to _slot to change the player order
                                //
                                if (mobile == 0) {              // if NOT Mobile
                                    out.println("<font size=\"2\">");
                                } else {
                                    out.println("<ul><li>");
                                }
                                out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
                                out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                                out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                                out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                                out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
                                out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                                out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                                out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                                out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                                out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                                out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                                out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                                out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                                out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                                out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                                out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                                out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                                out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
                                out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                                out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                                out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                                out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");

                                if (slotParms.g1.equals("") || posType.equals("") || slotParms.oldPlayer1.equals(slotParms.player1)) {

                                    out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline;\">");

                                } else {

                                    out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                                }

                                if (mobile == 0) {              // if NOT Mobile
                                    out.println("</form></font>");
                                } else {
                                    out.println("</form></li>");
                                }

                                if (slotParms.g1.equals("") || posType.equals("") || slotParms.oldPlayer1.equals(slotParms.player1)) {

                                    //
                                    //  Return to process the players as they are
                                    //
                                    if (mobile == 0) {              // if NOT Mobile
                                        out.println("<font size=\"2\">");
                                    } else {
                                        out.println("<li>");
                                    }

                                    out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
                                    out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">");
                                    out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                                    out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                                    out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                                    out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                                    out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                                    out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                                    out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                                    out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                                    out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                                    out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                                    out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                                    out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                                    out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                                    out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                                    out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                                    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                                    out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                                    out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
                                    out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
                                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                    out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
                                    out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                                    out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                                    out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                                    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                                    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                                    out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
                                    out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
                                    out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
                                    out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
                                    out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
                                    out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
                                    out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
                                    out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
                                    out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
                                    out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
                                    out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                                    out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");

                                    if (mobile == 0) {              // if NOT Mobile
                                        out.println("</form></font>");
                                    } else {
                                        out.println("</form></li>");
                                    }
                                }

                                if (mobile == 0) {              // if NOT Mobile
                                    out.println("</CENTER></BODY></HTML>");
                                } else {
                                    out.println("</ul></BODY></HTML>");
                                }
                                out.close();
                                return;
                            }

                        }   // end of IF more than 1 member or guest in spot #1
                    }      // end of IF no members and unaccompanied guests are ok
                    
                } else {        // NO Guests in this tee time

                    slotParms.userg1 = "";        // make sure member assignments are cleared (in case a guest was removed)
                    slotParms.userg2 = "";
                    slotParms.userg3 = "";
                    slotParms.userg4 = "";
                    slotParms.userg5 = "";
                }         // end of IF any guests specified

            } else {   // skip 8 requested
                //
                //  User has responded to the guest association prompt - process tee time request in specified order
                //
                slotParms.userg1 = req.getParameter("userg1");
                slotParms.userg2 = req.getParameter("userg2");
                slotParms.userg3 = req.getParameter("userg3");
                slotParms.userg4 = req.getParameter("userg4");
                slotParms.userg5 = req.getParameter("userg5");
            }         // end of IF skip8


            //
            //***********************************************************************************************
            //
            //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
            //
            //***********************************************************************************************
            //
            if (!slotParms.userg1.equals("") || !slotParms.userg2.equals("") || !slotParms.userg3.equals("")
                    || !slotParms.userg4.equals("") || !slotParms.userg5.equals("")) {

                try {

                    error = verifySlot.checkGuestQuota(slotParms, con);

                } catch (Exception e22) {

                    msg = "Check Guest Quotas. ";

                    dbError(out, e22, msg);
                    return;
                }

                if (error == true) {          // if we hit on a violation

                    msgHdr = "Guest Quota Exceeded for Member";

                    msgBody = "<BR><BR>Sorry, requesting <b>" + slotParms.player + "</b> exceeds the guest quota established by the Golf Shop."
                            + "<BR><BR>You are allowed " + slotParms.grest_num + " of these guests per " +slotParms.grest_per+ " during a timeframe defined by club policy and you have met or exceeded that limit."
                            + "<BR><BR>You will have to remove the guest in order to complete this request.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
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
                String gstCustomMsg = verifyCustom.checkCustomsGst(slotParms, con);     // go check for customs

                if (!gstCustomMsg.equals("")) {         // if error encountered - reject

                    msgHdr = "";

                    msgBody = gstCustomMsg + "<BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

                //
                //  MOVE THE FOLLOWING CUSTOMS TO USE ABOVE PROCESS !!!!!!!!!!!!!!
                //


                //
                //  Medinah Custom - check for guest quotas on Course #1 (max 12 guest per family between 6/01 - 8/31)
                //
                if (club.equals("medinahcc")) {

                    if (slotParms.course.equals("No 1")) {          // If Course #1

                        error = medinahCustom.checkNonRes1(slotParms, con);
                    }

                    //   Removed per Mike Skully on 7/01/06
                    //            if (slotParms.course.equals( "No 3" )) {          // If Course #3

                    //               error = medinahCustom.checkGuests3(slotParms, con);
                    //            }

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Guest Quota Exceeded for Member";

                        msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> has already met the guest quota for June through August."
                                + "<BR><BR>You will have to remove the guest in order to complete this request.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }

                    //
                    //  Also, make sure spouses do not have guests after 4:00 PM on weekdays (#1 & #3)
                    //
                    if ((slotParms.course.equals("No 1") || slotParms.course.equals("No 3"))
                            && slotParms.time > 1600) {

                        error = medinahCustom.checkSpouseGuest(slotParms, con);
                    }

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Guest Quota Exceeded for Member";

                        msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest at this time."
                                + "<BR><BR>You will have to remove the guest in order to complete this request.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }

                //
                //  Merion Custom - check for guest quotas on East Course
                //
                if (club.equals("merion") && slotParms.course.equals("East")) {

                    error = verifySlot.checkMerionGres(slotParms, con);

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Guest Time Quota Exceeded for Member";

                        msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> has already met the quota for Guest Times.<br>Each membership (family) is limited to a specified number of guest times that may be scheduled in advance."
                                + "<BR><BR>You will have to remove the guest(s) in order to complete this request.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }

                //
                //  Congressional Custom - check for guest quotas for 'Junior A' mships
                //
                /*
                if (club.equals("congressional")) {

                    error = congressionalCustom.checkJrAGuests(slotParms);

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Guest Quota Exceeded for Member";

                        msgBody = "<BR><BR>Sorry, Junior A members can only have one guest per member<br>on the Open Course on weekdays."
                                + "<BR><BR>You will have to remove the extra guest(s) in order to complete this request.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }
                 */


                //
                //  The CC - guest quotas
                //
                if (club.equals("tcclub")) {

                    //
                    //  Check for total guests per family if in season (4/01 - 10/31) and Main or Championship Course
                    //
                    if (shortDate > 400 && shortDate < 1032
                            && (slotParms.course.startsWith("Main Cours") || slotParms.course.startsWith("Championship Cours"))) {

                        error = verifyCustom.checkTCCguests(slotParms, con);

                        if (error == true) {

                            msgHdr = "Invalid Request";

                            msgBody = "<BR><BR>Sorry, " + slotParms.player + " has already reached the maximum limit of guests."
                                    + "<BR><BR>Each membership is allowed 6 guests per month and 18 guests per season.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;
                        }
                    }
                }


                //
                //  Wellesley Custom - check for guest restrictions
                //
                if (club.equals("wellesley")) {

                    int wellError = verifyCustom.wellesleyGuests(slotParms, con);

                    //
                    //  check for any error
                    //
                    if (wellError > 0) {          // if we hit on a violation

                        msgHdr = "Guests Restricted for Member";

                        if (wellError == 1) {          // if we hit on a violation

                            msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest.";

                        } else if (wellError == 2 || wellError == 3) {          // if we hit on a violation

                            msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest on this day.";

                        } else if (wellError == 4) {          // if we hit on a violation

                            msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest on this date.";

                        } else if (wellError == 5) {          // if we hit on a violation

                            msgBody = "<BR><BR>Sorry, <b>" + slotParms.player + "</b> has already reached the yearly guest quota.";
                        }

                        msgBody += "<br><br>You will have to remove the guest(s) in order to complete this request."
                                + "<br><br>Contact the Golf Shop if you have any questions.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }


                //
                //  Custom for Oakmont -
                //      If Feb, Mar or Apr check if member already has 10 advance guest times scheduled (any time during year).
                //      If so, then reject.  Members can only reserve 10 guest times in each month, but the guest times can be any time
                //      during the season (advance times).  After Apr they can book an unlimited number of guest times.
                //
                //      The month (01 = Jan, 02 = Feb, etc.) is saved in custom_int so we know when the tee time was booked.
                //
                /*
                if (club.equals("oakmont") && slotParms.oldPlayer1.equals("")) {   // oakmont and new tee time request

                    if (thisMonth > 0 && thisMonth < 6) {         // if Jan, Feb, Mar, Apr, May (tee sheets closed in Jan, but check anyway)

                        error = verifyCustom.checkOakmontGuestQuota(slotParms, thisMonth, con);

                        if (error == true) {

                            msgHdr = "Monthly Guest Quota Exceeded";

                            msgBody = "<BR><BR>Sorry,  " + slotParms.player + " has already scheduled the max allowed guest times this month.<BR>There is a limit to the number of advance guest rounds that can be scheduled in Feb, Mar, Apr, and May."
                                    + "<BR><BR>Please contact the golf shop if you have any questions.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;
                        }
                    }

                    slotParms.custom_int = thisMonth;                // save month value for teecurr

                }          // end of IF oakmont and new tee time request
                 */


                //
                //  Custom for Baltusrol -  each member can only have 3 outstanding guest times
                //
                if (club.equals("baltusrolgc")) {

                    error = verifyCustom.checkBaltusrolGuestQuota(slotParms, con);

                    if (error == true) {

                        msgHdr = "Guest Quota Exceeded";

                        msgBody = "<BR><BR>Sorry, " + slotParms.player + " has already scheduled the max allowed guest times.<BR>There is a limit to the number of guest times that can be scheduled in advance."
                                + "<BR><BR>Please contact the golf shop if you have any questions.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }          // end of IF baltusrol

                if (club.equals("woodway")) {

                    error = verifyCustom.checkWoodwayGuests(slotParms, con);

                    if (error == true) {

                        msgHdr = "Guests Restricted for Member";

                        msgBody = "<BR><BR>Sorry,  " + slotParms.player + " is not allowed to have a guest on this date."
                                + "<BR><BR>You will have to remove the guest(s) in order to complete this request.<BR><BR>";

                        buildError(msgHdr, msgBody, mobile, out);       // output the error message

                        returnToSlot(mobile, out, slotParms);
                        return;
                    }
                }          // end of IF woodway

            }            // end of IF any GUESTS included in request


            //
            //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
            //
            verifySlot.checkTFlag(slotParms, con);



            //*******************************************************************************
            //  Standard verification and processing complete - add final customs here!!!!  *
            //*******************************************************************************


            //
            //   Scioto CC - no guests allowed more than 4 or 5 days in advance AND members cannot replace members with guests later!!
            //
      /*
            if (club.equals( "sciotocc" ) && shortDate > 500 && shortDate < 1016) {
            
            int sciotoDays = 5;
            
            if (slotParms.day.equals("Sunday") || slotParms.day.equals("Monday")) {
            
            sciotoDays = 4;           // guests allowed 4 days or less
            }
            
            if (slotParms.ind > sciotoDays) {       // all players must be members - save members' usernames to be checked later
            
            slotParms.custom_disp1 = slotParms.user1;      // save member ids if included in tee time
            slotParms.custom_disp2 = slotParms.user2;
            slotParms.custom_disp3 = slotParms.user3;
            slotParms.custom_disp4 = slotParms.user4;      // 5-somes not allowed at this club
            
            } else {
            
            //
            //  Make sure this member is not removing a member that was added outside the guest window and replacing with a guest now
            //
            if ((!slotParms.g1.equals( "" ) && !slotParms.custom_disp1.equals( "" )) || (!slotParms.g2.equals( "" ) && !slotParms.custom_disp2.equals( "" )) ||
            (!slotParms.g3.equals( "" ) && !slotParms.custom_disp3.equals( "" )) || (!slotParms.g4.equals( "" ) && !slotParms.custom_disp4.equals( "" ))) {
            
            //  at least one position was a member and is now a guest (member was added when guests were not allowed - cannot be replaced now)
            
            msgHdr = "Guests Not Allowed";
            
            msgBody = "<BR><BR>Sorry, you cannot replace a member with a guest when the<br>member was added more than " +sciotoDays+ " days in advance." +
            "<BR><BR>Please remove the guest or contact the golf shop for assistance.<BR><BR>";
            
            buildError(msgHdr, msgBody, mobile, out);       // output the error message
            
            returnToSlot(mobile, out, slotParms);
            return;
            }
            }
            }
             */


            //
            //  Wilmington Custom - check mship subtypes for those that have range privileges
            //
            if (club.equals("wilmington")) {

                slotParms.custom_disp1 = "";
                slotParms.custom_disp2 = "";
                slotParms.custom_disp3 = "";
                slotParms.custom_disp4 = "";
                slotParms.custom_disp5 = "";

                if (!slotParms.mstype1.equals("")) {          // if value in sub-type

                    slotParms.custom_disp1 = slotParms.mstype1;          // to be added to player name in Proshop_sheet
                }
                if (!slotParms.mstype2.equals("")) {

                    slotParms.custom_disp2 = slotParms.mstype2;
                }
                if (!slotParms.mstype3.equals("")) {

                    slotParms.custom_disp3 = slotParms.mstype3;
                }
                if (!slotParms.mstype4.equals("")) {

                    slotParms.custom_disp4 = slotParms.mstype4;
                }
                if (!slotParms.mstype5.equals("")) {

                    slotParms.custom_disp5 = slotParms.mstype5;
                }
            }      // end of Wilmington custom

            //
            //  Custom for Eagle Creek - check to see if Social members have exceeded their # of rounds in season (Case #1284)
            //    - and make sure Socials are accompanied by a Golf mship
            //
            if (club.equals("eaglecreek")) {

                // now check to see if this member is Social and if so they must be accompanied by a Golf mship
                int tmp_yy = (int) slotParms.date / 10000;         // get year
                int tmp_sdate = (yy * 10000) + 1101;               // yyyy1101
                int tmp_edate = ((yy + 1) * 10000) + 430;        // yyyy0430

                //
                //  Only check quota if tee time is within the Golf Year
                //
                if (slotParms.date > tmp_sdate && slotParms.date < tmp_edate) {

                    if (slotParms.mship1.equals("Social") || slotParms.mship2.equals("Social") || slotParms.mship3.equals("Social")
                            || slotParms.mship4.equals("Social") || slotParms.mship5.equals("Social")) {

                        // at least one player is a Social member now check for golf mship
                        if (slotParms.mship1.equals("Golf") || slotParms.mship2.equals("Golf") || slotParms.mship3.equals("Golf")
                                || slotParms.mship4.equals("Golf") || slotParms.mship5.equals("Golf")) {

                            // ok because we found a Golf mship
                            // but now lets see if the Social members are over their allowed limit
                            error = verifyCustom.checkEagleCreekSocial(slotParms, con);

                            if (error == true) {

                                msgHdr = "Member Exceeded Max Allowed Rounds";

                                msgBody = "<BR><BR>Sorry, " + slotParms.player + " is a Social member and has exceeded the<BR>"
                                        + "maximum number of tee times allowed for this season (November 1 thru April 30).<BR><BR>";

                                buildError(msgHdr, msgBody, mobile, out);       // output the error message

                                returnToSlot(mobile, out, slotParms);
                                return;
                            }

                        } else {

                            // we didn't find a Golf mship so disallow
                            msgHdr = "Member Exceeded Max Allowed Rounds";

                            msgBody = "<BR><BR>Sorry, members with Social memberships must be accompanied by a member with"
                                    + "<BR>a Golf membership classification from November 1 thru April 30.<BR><BR>";

                            buildError(msgHdr, msgBody, mobile, out);       // output the error message

                            returnToSlot(mobile, out, slotParms);
                            return;

                        } // and if golf mship found
                    } // end if social mship found
                } // end date range check

            } // end if eaglecreek

            //
            //  Custom for Mediterra CC - check to see if Sports members have exceeded their # of rounds in season (case #1262)
            //    - keep this after all other standard verifications because it will trigger an email to pro if sports member exceed their rounds
            //
            if (club.equals("mediterra")) {

                error = verifyCustom.checkMediterraSports(slotParms, con);

                if (error == true) {

                    msgHdr = "Member Exceeded Max Allowed Rounds";

                    msgBody = "<BR><BR>Sorry, " + slotParms.player + " is a Sports member and has exceeded the"
                            + "<BR>maximum number of tee times allowed for this season.<BR><BR>";

                    buildError(msgHdr, msgBody, mobile, out);       // output the error message

                    returnToSlot(mobile, out, slotParms);
                    return;
                }

            }


            /*
            if (club.equals( "interlachen" )) {      // Interlachen Gift Pack custom
            
            slotParms.custom_disp1 = customS1;    // save Gift Pack options from tee time form (for Proshop_sheet)
            slotParms.custom_disp2 = customS2;
            slotParms.custom_disp3 = customS3;
            slotParms.custom_disp4 = customS4;
            slotParms.custom_disp5 = customS5;
            }
             */


            if (club.equals("castlepines")) {
                if (Utilities.checkBirthday(slotParms.user1, slotParms.date, con)) {
                    slotParms.custom_disp1 = "bday";
                }
                if (Utilities.checkBirthday(slotParms.user2, slotParms.date, con)) {
                    slotParms.custom_disp2 = "bday";
                }
                if (Utilities.checkBirthday(slotParms.user3, slotParms.date, con)) {
                    slotParms.custom_disp3 = "bday";
                }
                if (Utilities.checkBirthday(slotParms.user4, slotParms.date, con)) {
                    slotParms.custom_disp4 = "bday";
                }
                if (Utilities.checkBirthday(slotParms.user5, slotParms.date, con)) {
                    slotParms.custom_disp5 = "bday";
                }
            }

            //
            //  Custom for Timarron CC.  Set custom_disp values so we know who added which members to the tee time.  They'd like for
            //  players to only be able to remove themselves or other players they personally added to the tee time
            //
            if (club.equals("timarroncc")) {

                if (slotParms.player1.equals("")) {
                    slotParms.custom_disp1 = "";
                } else if (slotParms.custom_disp1.equals("") && (slotParms.oldPlayer1.equals("") || !slotParms.player1.equals(slotParms.oldPlayer1))) {
                    slotParms.custom_disp1 = user;
                }

                if (slotParms.player2.equals("")) {
                    slotParms.custom_disp2 = "";
                } else if (slotParms.custom_disp2.equals("") && (slotParms.oldPlayer2.equals("") || !slotParms.player2.equals(slotParms.oldPlayer2))) {
                    slotParms.custom_disp2 = user;
                }

                if (slotParms.player3.equals("")) {
                    slotParms.custom_disp3 = "";
                } else if (slotParms.custom_disp3.equals("") && (slotParms.oldPlayer3.equals("") || !slotParms.player3.equals(slotParms.oldPlayer3))) {
                    slotParms.custom_disp3 = user;
                }

                if (slotParms.player4.equals("")) {
                    slotParms.custom_disp4 = "";
                } else if (slotParms.custom_disp4.equals("") && (slotParms.oldPlayer4.equals("") || !slotParms.player4.equals(slotParms.oldPlayer4))) {
                    slotParms.custom_disp4 = user;
                }

                if (slotParms.player5.equals("")) {
                    slotParms.custom_disp5 = "";
                } else if (slotParms.custom_disp5.equals("") && (slotParms.oldPlayer5.equals("") || !slotParms.player5.equals(slotParms.oldPlayer5))) {
                    slotParms.custom_disp5 = user;
                }

            }

            //
            // Set orig values so we know who added which players to the tee time for later use
            //
            if (slotParms.player1.equals("")) {
                slotParms.orig1 = "";
            } else if (slotParms.orig1.equals("") && (slotParms.oldPlayer1.equals("") || !slotParms.player1.equals(slotParms.oldPlayer1))) {
                slotParms.orig1 = user;
            }

            if (slotParms.player2.equals("")) {
                slotParms.orig2 = "";
            } else if (slotParms.orig2.equals("") && (slotParms.oldPlayer2.equals("") || !slotParms.player2.equals(slotParms.oldPlayer2))) {
                slotParms.orig2 = user;
            }

            if (slotParms.player3.equals("")) {
                slotParms.orig3 = "";
            } else if (slotParms.orig3.equals("") && (slotParms.oldPlayer3.equals("") || !slotParms.player3.equals(slotParms.oldPlayer3))) {
                slotParms.orig3 = user;
            }

            if (slotParms.player4.equals("")) {
                slotParms.orig4 = "";
            } else if (slotParms.orig4.equals("") && (slotParms.oldPlayer4.equals("") || !slotParms.player4.equals(slotParms.oldPlayer4))) {
                slotParms.orig4 = user;
            }

            if (slotParms.player5.equals("")) {
                slotParms.orig5 = "";
            } else if (slotParms.orig5.equals("") && (slotParms.oldPlayer5.equals("") || !slotParms.player5.equals(slotParms.oldPlayer5))) {
                slotParms.orig5 = user;
            }

            //**************************************************************
            //  Verification Complete !!!!!!!!
            //**************************************************************

            sendemail = 0;         // init email flags
            emailNew = 0;
            emailMod = 0;

            //
            //  Pre-checkin feature - normally sets an indicator if the tee time is created or changed the same day (day of)
            //                        as the tee time itself.  This creates a visual for the proshop user on the tee sheet
            //                        so they can easily see which tee times are new that day.
            //
            // set to show values to 2 if feature is supported and teetime is today
            GregorianCalendar cal_pci = new GregorianCalendar();
            short tmp_pci = (parm.precheckin == 1
                    && mm == (cal_pci.get(cal_pci.MONTH) + 1)
                    && dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                    && yy == cal_pci.get(cal_pci.YEAR)) ? (short) 2 : (short) 0;


            //
            //  Custom changes for Pre-checkin - make sure the time is adjusted for time zone!!!!!!!!!!!
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //

            // Custom for Pelican's Nest - Utilize pre-checkin for tomorrow bookings if it's after 5pm today - Case# 1296
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("pelicansnest")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (parm.precheckin == 1
                        && mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 17) ? (short) 2 : (short) 0;
            }

            // Custom for Imperial GC - Utilize pre-checkin for tomorrow bookings if it's after 1pm ET today - Case# 1327
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("imperialgc")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (parm.precheckin == 1
                        && mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 12) ? (short) 2 : (short) 0;
            }


            // Custom for Mediterra - Utilize pre-checkin for tomorrow bookings if it's after 5:30pm today - Case# 1309
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("mediterra")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (parm.precheckin == 1
                        && mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && yy == cal_pci.get(cal_pci.YEAR)
                        && ((cal_pci.get(Calendar.HOUR_OF_DAY) >= 17 && cal_pci.get(Calendar.MINUTE) >= 30)
                        || cal_pci.get(Calendar.HOUR_OF_DAY) >= 18)) ? (short) 2 : (short) 0;
            }

            // Custom for CC of Naples - Utilize pre-checkin for tomorrow bookings if it's after 6pm today - Case# 1528
            // Added The Oaks Club to this custom - Case# 1589
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && (club.equals("ccnaples") || club.equals("theoaksclub"))) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (parm.precheckin == 1
                        && mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 18) ? (short) 2 : (short) 0;
            }

            // Custom for Wildcat Run G & CC - Utilize pre-checkin for tomorrow bookings if it's after 4:30pm ET today - Case# 2111
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("wildcatruncc")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (parm.precheckin == 1
                        && mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && yy == cal_pci.get(cal_pci.YEAR)
                        && ((cal_pci.get(Calendar.HOUR_OF_DAY) >= 15 && cal_pci.get(Calendar.MINUTE) >= 30)
                        || cal_pci.get(Calendar.HOUR_OF_DAY) >= 16)) ? (short) 2 : (short) 0;
            }

            // Custom for Governors Club - Utilize pre-checkin for tomorrow bookings if it's after 4:00pm ET today - Case# 2351
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("governorsclub")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (parm.precheckin == 1
                        && mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 15) ? (short) 2 : (short) 0;
            }



            //
            //  If players changed, then init the no-show flag and send emails, else use the old no-show value
            //
            if (!slotParms.player1.equals(slotParms.oldPlayer1)) {

                slotParms.show1 = tmp_pci;        // init no-show flag
                sendemail = 1;    // player changed - send email notification to all
            }

            if (!slotParms.player2.equals(slotParms.oldPlayer2)) {

                slotParms.show2 = tmp_pci;        // init no-show flag
                sendemail = 1;    // player changed - send email notification to all
            }

            if (!slotParms.player3.equals(slotParms.oldPlayer3)) {

                slotParms.show3 = tmp_pci;        // init no-show flag
                sendemail = 1;    // player changed - send email notification to all
            }

            if (!slotParms.player4.equals(slotParms.oldPlayer4)) {

                slotParms.show4 = tmp_pci;        // init no-show flag
                sendemail = 1;    // player changed - send email notification to all
            }

            if (!slotParms.player5.equals(slotParms.oldPlayer5)) {

                slotParms.show5 = tmp_pci;        // init no-show flag
                sendemail = 1;    // player changed - send email notification to all
            }

            //
            //   Set email type based on new or update request (cancel set above)
            //   Also, bump stats counters for reports
            //
            if ((!slotParms.oldPlayer1.equals("")) || (!slotParms.oldPlayer2.equals("")) || (!slotParms.oldPlayer3.equals(""))
                    || (!slotParms.oldPlayer4.equals("")) || (!slotParms.oldPlayer5.equals(""))) {

                emailMod = 1;  // tee time was modified
                memMod++;      // increment number of mods

            } else {

                emailNew = 1;  // tee time is new
                memNew++;      // increment number of new tee times
            }
            
            
            //
            //  If new notes added or if previous notes were changed, then add this member's name to the end for identification purposes.
            //
            if (!slotParms.notes.equals("") && !slotParms.notes.equals(oldNotes) && !slotParms.notes.endsWith("(" +fullName+ ")")) {
               
               slotParms.notes = slotParms.notes + " (" +fullName+ ")";     
            }           
            

            //
            //  Oak Hill CC - track the Cancel if this tee time was an advance tee time
            //
            if (club.equals("oakhillcc") && custom_int > 0) {

               verifyCustom.logOakhillAdvGst(slotParms.teecurr_id, slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                              slotParms.player4, slotParms.player5, user, fullName, slotParms.notes, 2, con);
            }
      
      


        }  // end of IF 'cancel this res' ELSE 'process tee time request'
        
        
        

        // Custom for Shady Canyon GC - If notes have changed, send custom email to pro
        if (club.equals("shadycanyongolfclub") && emailCan == 0) {

            sendShadyCanyonNotesEmail = false;

            if (emailNew == 1 && !slotParms.notes.equals("")) {  // new request and contains notes, send email.

                sendShadyCanyonNotesEmail = true;

            } else if (emailMod == 1) {  // Updated request, see if the notes were changed at all.  If so, send email

                try {

                    PreparedStatement pstmtTemp = null;
                    ResultSet rsTemp = null;

                    pstmtTemp = con.prepareStatement("SELECT notes FROM teecurr2 WHERE teecurr_id = ?");
                    pstmtTemp.clearParameters();
                    pstmtTemp.setLong(1, slotParms.teecurr_id);

                    rsTemp = pstmtTemp.executeQuery();

                    if (rsTemp.next()) {
                        if (!slotParms.notes.equals(rsTemp.getString("notes"))) {
                            sendShadyCanyonNotesEmail = true;
                        }
                    }

                    pstmtTemp.close();

                } catch (Exception exc) {
                    sendShadyCanyonNotesEmail = false;
                }
            }
        }
        
        if (club.equals("bishopsgategc") && (slotParms.members > 0 || slotParms.guests > 0) && slotParms.custom_int == 0
                && (slotParms.player1 == "" || slotParms.p91 == 1) 
                && (slotParms.player2 == "" || slotParms.p92 == 1) 
                && (slotParms.player3 == "" || slotParms.p93 == 1) 
                && (slotParms.player4 == "" || slotParms.p94 == 1) 
                && (slotParms.player5 == "" || slotParms.p95 == 1)) {
            
            slotParms.custom_int = -1;
        }


        //
        //  Verification complete -
        //  Update the tee slot in teecurr
        //
        try {

            PreparedStatement pstmt6 = con.prepareStatement(
                    "UPDATE teecurr2 "
                    + "SET last_mod_date = now(), "
                    + "player1 = ?, player2 = ?, player3 = ?, player4 = ?, "
                    + "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, "
                    + "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, "
                    + "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, "
                    + "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, memNew = ?, memMod = ?, "
                    + "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, "
                    + "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, "
                    + "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, "
                    + "orig_by = ?, p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, "
                    + "pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ?, "
                    + "custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, custom_disp5 = ?, "
                    + "custom_string = ?, custom_int = ?, "
                    + "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, "
                    + "orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ? "
                    + "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt6.clearParameters();
            pstmt6.setString(1, slotParms.player1);
            pstmt6.setString(2, slotParms.player2);
            pstmt6.setString(3, slotParms.player3);
            pstmt6.setString(4, slotParms.player4);
            pstmt6.setString(5, slotParms.user1);
            pstmt6.setString(6, slotParms.user2);
            pstmt6.setString(7, slotParms.user3);
            pstmt6.setString(8, slotParms.user4);
            pstmt6.setString(9, slotParms.p1cw);
            pstmt6.setString(10, slotParms.p2cw);
            pstmt6.setString(11, slotParms.p3cw);
            pstmt6.setString(12, slotParms.p4cw);
            pstmt6.setFloat(13, slotParms.hndcp1);
            pstmt6.setFloat(14, slotParms.hndcp2);
            pstmt6.setFloat(15, slotParms.hndcp3);
            pstmt6.setFloat(16, slotParms.hndcp4);
            pstmt6.setShort(17, slotParms.show1);
            pstmt6.setShort(18, slotParms.show2);
            pstmt6.setShort(19, slotParms.show3);
            pstmt6.setShort(20, slotParms.show4);
            pstmt6.setString(21, slotParms.player5);
            pstmt6.setString(22, slotParms.user5);
            pstmt6.setString(23, slotParms.p5cw);
            pstmt6.setFloat(24, slotParms.hndcp5);
            pstmt6.setShort(25, slotParms.show5);
            pstmt6.setString(26, slotParms.notes);
            pstmt6.setInt(27, memNew);
            pstmt6.setInt(28, memMod);
            pstmt6.setString(29, slotParms.mNum1);
            pstmt6.setString(30, slotParms.mNum2);
            pstmt6.setString(31, slotParms.mNum3);
            pstmt6.setString(32, slotParms.mNum4);
            pstmt6.setString(33, slotParms.mNum5);
            pstmt6.setString(34, slotParms.userg1);
            pstmt6.setString(35, slotParms.userg2);
            pstmt6.setString(36, slotParms.userg3);
            pstmt6.setString(37, slotParms.userg4);
            pstmt6.setString(38, slotParms.userg5);
            pstmt6.setInt(39, slotParms.guest_id1);
            pstmt6.setInt(40, slotParms.guest_id2);
            pstmt6.setInt(41, slotParms.guest_id3);
            pstmt6.setInt(42, slotParms.guest_id4);
            pstmt6.setInt(43, slotParms.guest_id5);
            pstmt6.setString(44, slotParms.orig_by);
            pstmt6.setInt(45, slotParms.p91);
            pstmt6.setInt(46, slotParms.p92);
            pstmt6.setInt(47, slotParms.p93);
            pstmt6.setInt(48, slotParms.p94);
            pstmt6.setInt(49, slotParms.p95);
            pstmt6.setInt(50, slotParms.pos1);
            pstmt6.setInt(51, slotParms.pos2);
            pstmt6.setInt(52, slotParms.pos3);
            pstmt6.setInt(53, slotParms.pos4);
            pstmt6.setInt(54, slotParms.pos5);
            pstmt6.setString(55, slotParms.custom_disp1);
            pstmt6.setString(56, slotParms.custom_disp2);
            pstmt6.setString(57, slotParms.custom_disp3);
            pstmt6.setString(58, slotParms.custom_disp4);
            pstmt6.setString(59, slotParms.custom_disp5);
            pstmt6.setString(60, slotParms.custom_string);
            pstmt6.setInt(61, slotParms.custom_int);
            pstmt6.setString(62, slotParms.tflag1);
            pstmt6.setString(63, slotParms.tflag2);
            pstmt6.setString(64, slotParms.tflag3);
            pstmt6.setString(65, slotParms.tflag4);
            pstmt6.setString(66, slotParms.tflag5);
            pstmt6.setString(67, slotParms.orig1);
            pstmt6.setString(68, slotParms.orig2);
            pstmt6.setString(69, slotParms.orig3);
            pstmt6.setString(70, slotParms.orig4);
            pstmt6.setString(71, slotParms.orig5);

            pstmt6.setLong(72, date);
            pstmt6.setInt(73, time);
            pstmt6.setInt(74, fb);
            pstmt6.setString(75, slotParms.course);
            pstmt6.executeUpdate();      // execute the prepared stmt

            pstmt6.close();

        } catch (Exception e6) {

            msg = "Update Tee Time. ";

            dbError(out, e6, msg);
            return;
        }
        
        
        // 
        //   Remove this tee time from the user's session
        //
        verifySlot.clearInSession(date, time, fb, slotParms.course, session);
        
        

        //
        //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
        //
        if (slotParms.oldPlayer1.equals("") && slotParms.oldPlayer2.equals("") && slotParms.oldPlayer3.equals("")
                && slotParms.oldPlayer4.equals("") && slotParms.oldPlayer5.equals("")) {

            //  new tee time
            SystemUtils.updateHist(date, slotParms.day, time, fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                    slotParms.player4, slotParms.player5, user, fullName, 0, con);

        } else {

            //  update tee time
            SystemUtils.updateHist(date, slotParms.day, time, fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                    slotParms.player4, slotParms.player5, user, fullName, 1, con);

            //
            //  If Tamarack - check if any members were removed and remove lottery history if yes
            //
            if (club.equals("tamarack") || club.equals("bentwaterclub")) {

                verifyCustom.removeHist(slotParms, con);
            }
        }

        //  Attempt to add hosts for any accompanied tracked guests
        if (slotParms.guest_id1 > 0 && !slotParms.userg1.equals("")) {
            Common_guestdb.addHost(slotParms.guest_id1, slotParms.userg1, con);
        }
        if (slotParms.guest_id2 > 0 && !slotParms.userg2.equals("")) {
            Common_guestdb.addHost(slotParms.guest_id2, slotParms.userg2, con);
        }
        if (slotParms.guest_id3 > 0 && !slotParms.userg3.equals("")) {
            Common_guestdb.addHost(slotParms.guest_id3, slotParms.userg3, con);
        }
        if (slotParms.guest_id4 > 0 && !slotParms.userg4.equals("")) {
            Common_guestdb.addHost(slotParms.guest_id4, slotParms.userg4, con);
        }
        if (slotParms.guest_id5 > 0 && !slotParms.userg5.equals("")) {
            Common_guestdb.addHost(slotParms.guest_id5, slotParms.userg5, con);
        }

        //
        //  If Hazeltine National, then check for an associated tee time (w/e's and holidays)
        //
        if (club.equals("hazeltine") || club.equals("moselemsprings") || club.equals("bishopsgategc")) {      // if Hazeltine National

            verifySlot.Htoggle(date, time, fb, slotParms, con);
        }

        /*
        //
        //  If Medianh CC, then count if ARRs if necessary
        //
        if (club.equals( "medinahcc" ) && slotParms.ind > 2) {      // if Medinah and more than 2 days in adv
        
        if (!slotParms.user1.equals( "" ) || !slotParms.user2.equals( "" ) || !slotParms.user3.equals( "" ) ||
        !slotParms.user4.equals( "" )) {                      // if not a cancel request
        
        medinahCustom.addARR(slotParms, con);
        }
        }
         */

        //
        //  Build the HTML page to confirm reservation for user
        //
        if (mobile == 0) {                 // if NOT a Mobile user

            out.println(SystemUtils.HeadTitle("Member Tee Slot Page"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<center><img src=\"/" + rev + "/images/foretees.gif\"><hr width=\"40%\">");
            out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

            if (req.getParameter("remove") != null) {

                out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;The reservation has been cancelled.</p>");
            } else {

                out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;Your reservation has been accepted and processed.</p>");

                if (xcount > 0 && xhrs > 0) {            // if any X's were specified

                    if (club.equals("castlepines")) {
                        
                        out.println("<p>All player positions reserved by an X must be filled by 12:00 pm. two days prior to the reserved tee time.");
                        out.println("<br /><br />If not, the system will automatically remove the X.</p>");
                        
                    } else {
                        out.println("<p>&nbsp;</p>All player positions reserved by an 'X' must be filled within " + xhrs + " hours of the tee time.");
                        out.println("<br>If not, the system will automatically remove the X.<br>");
                    }
                }

                if (club.equals("piedmont")) {        // if Piedmont Driving Club

                    int piedmontStatus = verifySlot.checkPiedmont(date, time, slotParms.day);     // check if special time

                    if (piedmontStatus == 1) {      // if Sat or Sun and before noon

                        out.println("<p><b>Notice From Golf Shop:</b>&nbsp;&nbsp;Please be aware that your group will be assigned");
                        out.println("<br>a caddie or forecaddie regardless of the number of players.</p>");

                    } else if (piedmontStatus == 2) {      // if other special time

                        out.println("<p><b>Notice From Golf Shop:</b>&nbsp;&nbsp;If this tee time becomes a threesome or ");
                        out.println("foursome and <br>a caddie is not already requested, a forecaddie will be assigned ");
                        out.println("to your group.</p>");

                    } else if (piedmontStatus == 4) {

                        out.println("<p><b>Notice From Golf Shop:</b>&nbsp;&nbsp;If this tee time includes two or more guests ");
                        out.println("and a caddie is not already requested, a forecaddie will be assigned to your group.</p>");

                    }
                }

                if (club.equals("charlottecc") && slotParms.guests >= 2) {        // If Charlotte CC and 2 or more guests

                    out.println("<p><br><b>Notice from Golf Professional Staff:</b>"
                            + "<br><br>\"Any golf group that has two (2) or more Guests must have a caddie if walking, "
                            + "<br>or a forecaddie if riding in golf carts.\" - Thank You</p>");
                }

                if (notesL > 254) {

                    out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
                }
            }

            out.println("<p>&nbsp;</p></font>");

            msgDate = yy + "-" + mm + "-" + dd;
            msgPlayerCount = 1;  // count player1 by default
            if (!slotParms.player2.equals("")) {
                msgPlayerCount++;
            }
            if (!slotParms.player3.equals("")) {
                msgPlayerCount++;
            }
            if (!slotParms.player4.equals("")) {
                msgPlayerCount++;
            }
            if (!slotParms.player5.equals("")) {
                msgPlayerCount++;
            }

            if (index.equals("999")) {         // if came from Member_teelist

                if (Utilities.checkDiningLink("mem_teetime", con) && req.getParameter("remove") == null) {
                    customId = Utilities.printDiningPrompt(out, con, msgDate, slotParms.day, user, msgPlayerCount, "teetime", "&sub=teemain", false);
                }

                out.println("<font size=\"2\">");
                out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain.htm\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");

            } else {

                if (index.equals("995")) {         // if came from Member_teelist_list (old)

                    if (Utilities.checkDiningLink("mem_teetime", con) && req.getParameter("remove") == null) {
                        customId = Utilities.printDiningPrompt(out, con, msgDate, slotParms.day, user, msgPlayerCount, "teetime", "&sub=teemain2", false);
                    }

                    out.println("<font size=\"2\">");
                    out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain2.htm\">");
                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                    out.println("</form></font>");

                } else {

                    if (index.equals("888")) {       // if from Member_searchmem

                        if (Utilities.checkDiningLink("mem_teetime", con) && req.getParameter("remove") == null) {
                            customId = Utilities.printDiningPrompt(out, con, msgDate, slotParms.day, user, msgPlayerCount, "teetime", "&sub=searchmem", false);
                        }

                        out.println("<font size=\"2\">");
                        out.println("<form method=\"get\" action=\"/" + rev + "/member_searchmem.htm\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");

                    } else {                                // return to Member_sheet - must rebuild frames first

                        if (Utilities.checkDiningLink("mem_teetime", con) && req.getParameter("remove") == null) {
                            String dCourse = "";
                            if (!slotParms.returnCourse.equals("")) {
                                dCourse = slotParms.returnCourse;
                            } else {
                                dCourse = slotParms.course;
                            }
                            customId = Utilities.printDiningPrompt(out, con, msgDate, slotParms.day, user, msgPlayerCount, "teetime", "&index=" + index + "&course=" + dCourse + "&jump=" + jump, false);
                        }

                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                        if (!slotParms.returnCourse.equals("")) {    // if multi course club, get course to return to (ALL?)
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
                        } else {
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                        }
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        out.println("</form></font>");
                    }
                }
            }

            //
            //  End of HTML page
            //
            out.println("</center></font></body></html>");

        } else {       // Mobile user

            //
            //  Mobile user
            //
            out.println(SystemUtils.HeadTitleMobile("ForeTees Request Complete"));
            out.println(SystemUtils.BannerMobile());

            out.println("<div class=\"content\">");

            if (req.getParameter("remove") != null) {

                out.println("<div class=\"headertext\">Thank you!&nbsp;&nbsp;The reservation has been cancelled.</div>");
            } else {

                out.println("<div class=\"headertext\">Thank you!&nbsp;&nbsp;Your reservation has been accepted and processed.</div>");

                if (xcount > 0 && xhrs > 0) {            // if any X's were specified

                    if (club.equals("castlepines")) {
                        
                        out.println("<div class=\"smheadertext\">All player positions reserved by an X must be filled by 5:00 pm. two days prior to the reserved tee time.");
                        out.println("<br /><br />Tuesday at 5:00 pm for Thursday tee times");
                        out.println("<br />Wednesday at 5:00 pm for Friday tee times");
                        out.println("<br />Thursday at 5:00 pm for Saturday tee times");
                        out.println("<br />Friday at 5:00 pm for Sunday tee times");
                        out.println("<br />Saturday at 5:00 pm for Monday Holiday tee times");
                        out.println("<br />Sunday at 5:00 pm for Tuesday tee times");
                        out.println("<br />Monday at 5:00 pm for Wednesday tee times");
                        out.println("<br /><br />If not, the system will automatically remove the X.</div>");
                        
                    } else {
                        out.println("<div class=\"smheadertext\">All player positions reserved by an 'X' must be filled within " + xhrs + " hours of the tee time.");
                        out.println("<br>If not, the system will automatically remove the X.</div>");
                    }
                }

                if (club.equals("piedmont")) {        // if Piedmont Driving Club

                    int piedmontStatus = verifySlot.checkPiedmont(date, time, slotParms.day);     // check if special time

                    if (piedmontStatus == 1) {      // if Sat or Sun and before noon

                        out.println("<div class=\"smheadertext\"><b>Notice From Golf Shop:</b>&nbsp;&nbsp;Please be aware that your group will be assigned");
                        out.println("<br>a caddie or forecaddie regardless of the number of players.</div>");

                    } else {

                        if (piedmontStatus == 2) {      // if other special time

                            out.println("<div class=\"smheadertext\"><b>Notice From Golf Shop:</b>&nbsp;&nbsp;If this tee time becomes a threesome or ");
                            out.println("foursome and <br>a caddie is not already requested, a forecaddie will be assigned ");
                            out.println("to your group.</div>");
                        }
                    }
                }

                if (club.equals("charlottecc") && slotParms.guests >= 2) {        // If Charlotte CC and 2 or more guests

                    out.println("<div class=\"smheadertext\"><br><b>Notice from Golf Professional Staff:</b>"
                            + "<br><br>\"Any golf group that has two (2) or more Guests must have a caddie if walking, "
                            + "<br>or a forecaddie if riding in golf carts.\" - Thank You</div>");
                }
            }

            out.println("<ul>");

            if (index.equals("995")) {         // if came from Member_teelist_list

                out.println("<li>");
                out.println("<form action=\"Member_teelist_mobile\" method=\"get\">");
                out.println("<input type=\"submit\" value=\"Return to List\"></form>");
                out.println("</li>");

            } else {

                out.println("<li>");
                out.println("<form action=\"Member_sheet\" method=\"post\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
                out.println("</li>");
            }
            out.println("</ul></div></body></html>");
        }

        out.close();

        try {

            resp.flushBuffer();      // force the repsonse to complete

        } catch (Exception ignore) {
        }
        
        if (club.equals("baltimore") || club.equals("riverside")) {
            
            sendCustomEmail = false;
            
            if ((slotParms.p1cw.equals("CAD") && !slotParms.oldp1cw.equals("CAD"))
                    || (slotParms.p2cw.equals("CAD") && !slotParms.oldp2cw.equals("CAD"))
                    || (slotParms.p3cw.equals("CAD") && !slotParms.oldp3cw.equals("CAD"))
                    || (slotParms.p4cw.equals("CAD") && !slotParms.oldp4cw.equals("CAD"))
                    || (slotParms.p5cw.equals("CAD") && !slotParms.oldp5cw.equals("CAD"))) {    // if new caddie requested

                sendCustomEmail = true;

            } else {      // check if any caddies were removed

                if ((!slotParms.p1cw.equals("CAD") && slotParms.oldp1cw.equals("CAD"))
                        || (!slotParms.p2cw.equals("CAD") && slotParms.oldp2cw.equals("CAD"))
                        || (!slotParms.p3cw.equals("CAD") && slotParms.oldp3cw.equals("CAD"))
                        || (!slotParms.p4cw.equals("CAD") && slotParms.oldp4cw.equals("CAD"))
                        || (!slotParms.p5cw.equals("CAD") && slotParms.oldp5cw.equals("CAD"))) {    // if caddie changed

                    sendCustomEmail = true;
                }
            }
            
        } else if (club.equals("rehobothbeachcc")) {
            
            sendCustomEmail = false;
            
            if (((slotParms.p1cw.equals("CAD") || slotParms.p1cw.equals("CFC")) && !slotParms.oldp1cw.equals("CAD") && !slotParms.oldp1cw.equals("CFC"))
                    || ((slotParms.p2cw.equals("CAD") || slotParms.p2cw.equals("CFC")) && !slotParms.oldp2cw.equals("CAD") && !slotParms.oldp2cw.equals("CFC"))
                    || ((slotParms.p3cw.equals("CAD") || slotParms.p3cw.equals("CFC")) && !slotParms.oldp3cw.equals("CAD") && !slotParms.oldp3cw.equals("CFC"))
                    || ((slotParms.p4cw.equals("CAD") || slotParms.p4cw.equals("CFC")) && !slotParms.oldp4cw.equals("CAD") && !slotParms.oldp4cw.equals("CFC"))
                    || ((slotParms.p5cw.equals("CAD") || slotParms.p5cw.equals("CFC")) && !slotParms.oldp5cw.equals("CAD") && !slotParms.oldp5cw.equals("CFC"))) {    // if new caddie requested

                sendCustomEmail = true;

            } else if ((!slotParms.p1cw.equals("CAD") && !slotParms.p1cw.equals("CFC") && (slotParms.oldp1cw.equals("CAD") || slotParms.oldp1cw.equals("CFC")))  
                    || (!slotParms.p2cw.equals("CAD") && !slotParms.p2cw.equals("CFC") && (slotParms.oldp2cw.equals("CAD") || slotParms.oldp2cw.equals("CFC")))
                    || (!slotParms.p3cw.equals("CAD") && !slotParms.p3cw.equals("CFC") && (slotParms.oldp3cw.equals("CAD") || slotParms.oldp3cw.equals("CFC")))
                    || (!slotParms.p4cw.equals("CAD") && !slotParms.p4cw.equals("CFC") && (slotParms.oldp4cw.equals("CAD") || slotParms.oldp4cw.equals("CFC")))
                    || (!slotParms.p5cw.equals("CAD") && !slotParms.p5cw.equals("CFC") && (slotParms.oldp5cw.equals("CAD") || slotParms.oldp5cw.equals("CFC")))) {    // check if any caddies were removed
                
                sendCustomEmail = true;
                
            } else if ((slotParms.p1cw.equals("CAD") && slotParms.oldp1cw.equals("CFC")) || (slotParms.p1cw.equals("CFC") && slotParms.oldp1cw.equals("CAD")) 
                    || (slotParms.p2cw.equals("CAD") && slotParms.oldp2cw.equals("CFC")) || (slotParms.p2cw.equals("CFC") && slotParms.oldp2cw.equals("CAD"))
                    || (slotParms.p3cw.equals("CAD") && slotParms.oldp3cw.equals("CFC")) || (slotParms.p3cw.equals("CFC") && slotParms.oldp3cw.equals("CAD"))
                    || (slotParms.p4cw.equals("CAD") && slotParms.oldp4cw.equals("CFC")) || (slotParms.p4cw.equals("CFC") && slotParms.oldp4cw.equals("CAD"))
                    || (slotParms.p5cw.equals("CAD") && slotParms.oldp5cw.equals("CFC")) || (slotParms.p5cw.equals("CFC") && slotParms.oldp5cw.equals("CAD"))) {    // check if changed from one caddie type to the other.
                
                sendCustomEmail = true;
            }
            
        } else if (club.equals("silverleaf")) {
            
            sendCustomEmail = false;
            
            if (((slotParms.p1cw.equals("CAD") || slotParms.p1cw.equals("FOR")) && !slotParms.oldp1cw.equals("CAD") && !slotParms.oldp1cw.equals("FOR"))
                    || ((slotParms.p2cw.equals("CAD") || slotParms.p2cw.equals("FOR")) && !slotParms.oldp2cw.equals("CAD") && !slotParms.oldp2cw.equals("FOR"))
                    || ((slotParms.p3cw.equals("CAD") || slotParms.p3cw.equals("FOR")) && !slotParms.oldp3cw.equals("CAD") && !slotParms.oldp3cw.equals("FOR"))
                    || ((slotParms.p4cw.equals("CAD") || slotParms.p4cw.equals("FOR")) && !slotParms.oldp4cw.equals("CAD") && !slotParms.oldp4cw.equals("FOR"))
                    || ((slotParms.p5cw.equals("CAD") || slotParms.p5cw.equals("FOR")) && !slotParms.oldp5cw.equals("CAD") && !slotParms.oldp5cw.equals("FOR"))) {    // if new caddie requested

                sendCustomEmail = true;

            } else if ((!slotParms.p1cw.equals("CAD") && !slotParms.p1cw.equals("FOR") && (slotParms.oldp1cw.equals("CAD") || slotParms.oldp1cw.equals("FOR")))  
                    || (!slotParms.p2cw.equals("CAD") && !slotParms.p2cw.equals("FOR") && (slotParms.oldp2cw.equals("CAD") || slotParms.oldp2cw.equals("FOR")))
                    || (!slotParms.p3cw.equals("CAD") && !slotParms.p3cw.equals("FOR") && (slotParms.oldp3cw.equals("CAD") || slotParms.oldp3cw.equals("FOR")))
                    || (!slotParms.p4cw.equals("CAD") && !slotParms.p4cw.equals("FOR") && (slotParms.oldp4cw.equals("CAD") || slotParms.oldp4cw.equals("FOR")))
                    || (!slotParms.p5cw.equals("CAD") && !slotParms.p5cw.equals("FOR") && (slotParms.oldp5cw.equals("CAD") || slotParms.oldp5cw.equals("FOR")))) {    // check if any caddies were removed
                
                sendCustomEmail = true;
                
            } else if ((slotParms.p1cw.equals("CAD") && slotParms.oldp1cw.equals("FOR")) || (slotParms.p1cw.equals("FOR") && slotParms.oldp1cw.equals("CAD")) 
                    || (slotParms.p2cw.equals("CAD") && slotParms.oldp2cw.equals("FOR")) || (slotParms.p2cw.equals("FOR") && slotParms.oldp2cw.equals("CAD"))
                    || (slotParms.p3cw.equals("CAD") && slotParms.oldp3cw.equals("FOR")) || (slotParms.p3cw.equals("FOR") && slotParms.oldp3cw.equals("CAD"))
                    || (slotParms.p4cw.equals("CAD") && slotParms.oldp4cw.equals("FOR")) || (slotParms.p4cw.equals("FOR") && slotParms.oldp4cw.equals("CAD"))
                    || (slotParms.p5cw.equals("CAD") && slotParms.oldp5cw.equals("FOR")) || (slotParms.p5cw.equals("FOR") && slotParms.oldp5cw.equals("CAD"))) {    // check if changed from one caddie type to the other.
                
                sendCustomEmail = true;
            }
        }

        // Send email to staff member if a guest is, or was, present in a tee time.
        if (club.equals("pradera") || club.equals("pinery") || club.equals("tartanfields")) {

            if (!slotParms.g1.equals("") || !slotParms.g2.equals("") || !slotParms.g3.equals("") || !slotParms.g4.equals("") || !slotParms.g5.equals("")
                    || (!slotParms.oldPlayer1.equals("") && slotParms.oldUser1.equals("") && !slotParms.oldPlayer1.equalsIgnoreCase("x"))
                    || (!slotParms.oldPlayer2.equals("") && slotParms.oldUser2.equals("") && !slotParms.oldPlayer2.equalsIgnoreCase("x"))
                    || (!slotParms.oldPlayer3.equals("") && slotParms.oldUser3.equals("") && !slotParms.oldPlayer3.equalsIgnoreCase("x"))
                    || (!slotParms.oldPlayer4.equals("") && slotParms.oldUser4.equals("") && !slotParms.oldPlayer4.equalsIgnoreCase("x"))
                    || (!slotParms.oldPlayer5.equals("") && slotParms.oldUser5.equals("") && !slotParms.oldPlayer5.equalsIgnoreCase("x"))) {

                sendCustomEmail = true;
            }
        }

        //
        //***********************************************
        //  Send email notification if necessary
        //***********************************************
        //
        if (sendemail != 0 || club.equals("pmarshgc") || sendCustomEmail) {
            
            //
            //  allocate a parm block to hold the email parms
            //
            parmEmail parme = new parmEmail();          // allocate an Email parm block

            //
            //  Set the values in the email parm block
            //
            parme.activity_id = 0;
            parme.club = club;
            parme.guests = slotParms.guests;
            parme.type = "tee";         // type = tee time
            parme.date = date;
            parme.time = time;
            parme.fb = fb;
            parme.mm = mm;
            parme.dd = dd;
            parme.yy = yy;
            parme.etype = 0;    
            
            //
            //  If tee time is part of a shotgun event, then change the time and indicate its a shotgun
            //
            if (!eventName.equals("") && eventType == 1) {
                
                parme.time = Utilities.getEventTime(eventName, con);   // get the actual time of the shotgun
                
                parme.etype = 1;       // indicate shotgun tee time
            }

            parme.user = user;
            parme.orig_by = slotParms.orig_by;
            parme.emailNew = emailNew;
            parme.emailMod = emailMod;
            parme.emailCan = emailCan;

            parme.p91 = slotParms.p91;
            parme.p92 = slotParms.p92;
            parme.p93 = slotParms.p93;
            parme.p94 = slotParms.p94;
            parme.p95 = slotParms.p95;

            parme.course = slotParms.course;
            parme.day = slotParms.day;
            parme.notes = slotParms.notes;

            parme.player1 = slotParms.player1;
            parme.player2 = slotParms.player2;
            parme.player3 = slotParms.player3;
            parme.player4 = slotParms.player4;
            parme.player5 = slotParms.player5;

            parme.oldplayer1 = slotParms.oldPlayer1;
            parme.oldplayer2 = slotParms.oldPlayer2;
            parme.oldplayer3 = slotParms.oldPlayer3;
            parme.oldplayer4 = slotParms.oldPlayer4;
            parme.oldplayer5 = slotParms.oldPlayer5;

            parme.user1 = slotParms.user1;
            parme.user2 = slotParms.user2;
            parme.user3 = slotParms.user3;
            parme.user4 = slotParms.user4;
            parme.user5 = slotParms.user5;

            parme.olduser1 = slotParms.oldUser1;
            parme.olduser2 = slotParms.oldUser2;
            parme.olduser3 = slotParms.oldUser3;
            parme.olduser4 = slotParms.oldUser4;
            parme.olduser5 = slotParms.oldUser5;

            parme.pcw1 = slotParms.p1cw;
            parme.pcw2 = slotParms.p2cw;
            parme.pcw3 = slotParms.p3cw;
            parme.pcw4 = slotParms.p4cw;
            parme.pcw5 = slotParms.p5cw;

            parme.oldpcw1 = slotParms.oldp1cw;
            parme.oldpcw2 = slotParms.oldp2cw;
            parme.oldpcw3 = slotParms.oldp3cw;
            parme.oldpcw4 = slotParms.oldp4cw;
            parme.oldpcw5 = slotParms.oldp5cw;

            parme.guest_id1 = slotParms.guest_id1;
            parme.guest_id2 = slotParms.guest_id2;
            parme.guest_id3 = slotParms.guest_id3;
            parme.guest_id4 = slotParms.guest_id4;
            parme.guest_id5 = slotParms.guest_id5;

            parme.oldguest_id1 = slotParms.oldguest_id1;
            parme.oldguest_id2 = slotParms.oldguest_id2;
            parme.oldguest_id3 = slotParms.oldguest_id3;
            parme.oldguest_id4 = slotParms.oldguest_id4;
            parme.oldguest_id5 = slotParms.oldguest_id5;

            parme.userg1 = slotParms.userg1;
            parme.userg2 = slotParms.userg2;
            parme.userg3 = slotParms.userg3;
            parme.userg4 = slotParms.userg4;
            parme.userg5 = slotParms.userg5;

            //
            //  Send the email
            //
            if (sendemail != 0) sendEmail.sendIt(parme, con);      // in common (include sendemail verification since customs can enter with it set to 0)


            //
            //  If Hallbrook CC, then check for any caddies in the tee time - if so, send an email to Caddie Master
            //
            if (club.equals("hallbrookcc")) {

                if ((slotParms.p1cw.equals("CAD") && !slotParms.oldp1cw.equals("CAD"))
                        || (slotParms.p2cw.equals("CAD") && !slotParms.oldp2cw.equals("CAD"))
                        || (slotParms.p3cw.equals("CAD") && !slotParms.oldp3cw.equals("CAD"))
                        || (slotParms.p4cw.equals("CAD") && !slotParms.oldp4cw.equals("CAD"))
                        || (slotParms.p5cw.equals("CAD") && !slotParms.oldp5cw.equals("CAD"))) {    // if new caddie requested

                    sendEmail.sendOakmontEmail(parme, con, club);      // send an email to Caddie Master

                } else {      // check if any caddies were removed

                    if ((!slotParms.p1cw.equals("CAD") && slotParms.oldp1cw.equals("CAD"))
                            || (!slotParms.p2cw.equals("CAD") && slotParms.oldp2cw.equals("CAD"))
                            || (!slotParms.p3cw.equals("CAD") && slotParms.oldp3cw.equals("CAD"))
                            || (!slotParms.p4cw.equals("CAD") && slotParms.oldp4cw.equals("CAD"))
                            || (!slotParms.p5cw.equals("CAD") && slotParms.oldp5cw.equals("CAD"))) {    // if caddie changed

                        sendEmail.sendOakmontEmail(parme, con, club);      // send an email to Caddie Master
                    }
                }
            }
            
            if (sendCustomEmail) {
                sendEmail.sendOakmontEmail(parme, con, club);      // send custom email to staff
            }

            if (congressGstEmail == true) {            // if guest found in cancelled tee time

                sendEmail.sendCongressEmail(parme, con);      // send an email to Head Pro
            }

            if (sendShadyCanyonNotesEmail) {

                sendEmail.sendOakmontEmail(parme, con, club);
            }
            
            /*
            if (club.equals("oakhillcc") && req.getParameter("remove") != null) {   

                sendEmail.sendOakmontEmail(parme, con, club);   // Oak Hill CC - send email to Golf Chair whenever someone cancels a tee time   
            }*/
            
            if (club.equals("philcricketrecip")) {               

                sendEmail.sendOakmontEmail(parme, con, club);   // Philly Cricket Recip Site - send emails for everything to Pro at selected course   
            }
            
            // Send notification to pros at Pelican Marsh if a member books a new tee time on the day of.
            if (club.equals("pmarshgc") && emailNew != 0 && date == todayDate) {
                sendEmail.sendOakmontEmail(parme, con, club);
            }
                
            if (club.equals("belfair") && emailCan != 0) {
                
                if ((slotParms.oldUser1.equals("") && !slotParms.oldPlayer1.equals("") && !slotParms.oldPlayer1.equalsIgnoreCase("X")) 
                        || (slotParms.oldUser2.equals("") && !slotParms.oldPlayer2.equals("") && !slotParms.oldPlayer2.equalsIgnoreCase("X"))
                        || (slotParms.oldUser3.equals("") && !slotParms.oldPlayer3.equals("") && !slotParms.oldPlayer3.equalsIgnoreCase("X"))
                        || (slotParms.oldUser4.equals("") && !slotParms.oldPlayer4.equals("") && !slotParms.oldPlayer4.equalsIgnoreCase("X"))
                        || (slotParms.oldUser5.equals("") && !slotParms.oldPlayer5.equals("") && !slotParms.oldPlayer5.equalsIgnoreCase("X"))) {
                    
                    sendEmail.sendOakmontEmail(parme, con, club);
                }
            }

        } else if (club.equals("shadycanyongolfclub") && sendShadyCanyonNotesEmail) {    // end of IF sendemail


            //  Send a custom email message to Shady Canyon pro if notes have changed
            //  allocate a parm block to hold the email parms
            parmEmail parme = new parmEmail();          // allocate an Email parm block

            //  Set the values in the email parm block
            parme.type = "tee";         // type = tee time
            parme.date = date;
            parme.time = time;
            parme.fb = fb;
            parme.mm = mm;
            parme.dd = dd;
            parme.yy = yy;

            parme.user = user;
            parme.emailNew = emailNew;
            parme.emailMod = emailMod;
            parme.emailCan = emailCan;

            parme.p91 = slotParms.p91;
            parme.p92 = slotParms.p92;
            parme.p93 = slotParms.p93;
            parme.p94 = slotParms.p94;
            parme.p95 = slotParms.p95;

            parme.course = slotParms.course;
            parme.day = slotParms.day;
            parme.notes = slotParms.notes;

            parme.player1 = slotParms.player1;
            parme.player2 = slotParms.player2;
            parme.player3 = slotParms.player3;
            parme.player4 = slotParms.player4;
            parme.player5 = slotParms.player5;

            parme.oldplayer1 = slotParms.oldPlayer1;
            parme.oldplayer2 = slotParms.oldPlayer2;
            parme.oldplayer3 = slotParms.oldPlayer3;
            parme.oldplayer4 = slotParms.oldPlayer4;
            parme.oldplayer5 = slotParms.oldPlayer5;

            parme.user1 = slotParms.user1;
            parme.user2 = slotParms.user2;
            parme.user3 = slotParms.user3;
            parme.user4 = slotParms.user4;
            parme.user5 = slotParms.user5;

            parme.olduser1 = slotParms.oldUser1;
            parme.olduser2 = slotParms.oldUser2;
            parme.olduser3 = slotParms.oldUser3;
            parme.olduser4 = slotParms.oldUser4;
            parme.olduser5 = slotParms.oldUser5;

            parme.pcw1 = slotParms.p1cw;
            parme.pcw2 = slotParms.p2cw;
            parme.pcw3 = slotParms.p3cw;
            parme.pcw4 = slotParms.p4cw;
            parme.pcw5 = slotParms.p5cw;

            parme.oldpcw1 = slotParms.oldp1cw;
            parme.oldpcw2 = slotParms.oldp2cw;
            parme.oldpcw3 = slotParms.oldp3cw;
            parme.oldpcw4 = slotParms.oldp4cw;
            parme.oldpcw5 = slotParms.oldp5cw;

            parme.guest_id1 = slotParms.guest_id1;
            parme.guest_id2 = slotParms.guest_id2;
            parme.guest_id3 = slotParms.guest_id3;
            parme.guest_id4 = slotParms.guest_id4;
            parme.guest_id5 = slotParms.guest_id5;

            parme.oldguest_id1 = slotParms.oldguest_id1;
            parme.oldguest_id2 = slotParms.oldguest_id2;
            parme.oldguest_id3 = slotParms.oldguest_id3;
            parme.oldguest_id4 = slotParms.oldguest_id4;
            parme.oldguest_id5 = slotParms.oldguest_id5;

            parme.userg1 = slotParms.userg1;
            parme.userg2 = slotParms.userg2;
            parme.userg3 = slotParms.userg3;
            parme.userg4 = slotParms.userg4;
            parme.userg5 = slotParms.userg5;

            sendEmail.sendOakmontEmail(parme, con, club);
        }

    }       // end of verify

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
    //  Gallery Golf - check if user is cheating and came in early (before 6:00 AM Arizona Time)
    // ************************************************************************
    private boolean checkGGearly(String user, int ind, Connection con) {


        String errMsg = "Gallery Golf Member attempting to access tee time early.  Error = ";
        boolean error = false;

        //
        //  Get this exact time and see if the user is trying to get into a tee time early
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int thishr = cal.get(Calendar.HOUR_OF_DAY);
        int thismin = cal.get(Calendar.MINUTE);
        int thissec = cal.get(Calendar.SECOND);

        int thisTime = (thishr * 100) + thismin;         // get current time (Central Time!! - hhmm)

        // Gallery Golf is in Arizona - we must adjust the time
        thisTime = SystemUtils.adjustTime(con, thisTime);

        thisTime = (thisTime * 100) + thissec;                // create time value with seconds (hhmmss)


        if (ind > 6) {                 // too many days in adv?

            error = true;
            errMsg = errMsg + ind + " days in advance. User = " + user;

        } else if (ind == 6 && thisTime < 60002) {       //  6 days in advance and earlier than 6:00:02

            error = true;
            errMsg = errMsg + "Too early. Time = " + thisTime + " CT, User = " + user;
        }

        if (error == true) {

            SystemUtils.logError(errMsg);             // log it
        }

        return (error);

    }       // end of checkGGearly

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
        out.println("<BR>forth by your club. As a result you will be logged off and have to log on again.");
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

    // ************************************************************************
    //  Process 'check for guests' request for Oalmont CC
    // ************************************************************************
    private boolean checkOakGuests(long date, int time, int fb, String course, Connection con) {


        ResultSet rs = null;

        boolean guests = false;

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


        int ind = getDaysBetween(date);            // get # of days in between today and the date

        if (ind < 30) {

            //
            //   Within 30 days - check if there are already guests in this tee time
            //
            try {

                PreparedStatement pstmt = con.prepareStatement(
                        "SELECT player1, player2, player3, player4, username1, username2, username3, username4, player5, username5 "
                        + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                pstmt.clearParameters();        // clear the parms
                pstmt.setLong(1, date);         // put the parm in pstmt
                pstmt.setInt(2, time);
                pstmt.setInt(3, fb);
                pstmt.setString(4, course);
                rs = pstmt.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

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
                }
                pstmt.close();

                //
                //  Check if any guests - if player specified, but not a member
                //
                if ((!player1.equals("") && !player1.equalsIgnoreCase("x") && user1.equals(""))
                        || (!player2.equals("") && !player2.equalsIgnoreCase("x") && user2.equals(""))
                        || (!player3.equals("") && !player3.equalsIgnoreCase("x") && user3.equals(""))
                        || (!player4.equals("") && !player4.equalsIgnoreCase("x") && user4.equals(""))
                        || (!player5.equals("") && !player5.equalsIgnoreCase("x") && user5.equals(""))) {

                    guests = true;
                }

            } catch (Exception ignore) {
            }
        }

        return (guests);

    }       // end of Oakmont checkOakGuests

    // ************************************************************************
    //  Get number of days between today and the date provided
    // ************************************************************************
    private int getDaysBetween(long date) {

        return Utilities.getDaysBetween(date);    // Method moved to Utilities for global use

    }       // end of getDaysBetween

    // ************************************************************************
    //  Process cancel request (Return w/o changes) from Member_slot (HTML)
    // ************************************************************************
    private void cancel(int mobile, HttpServletRequest req, PrintWriter out, String club, Connection con, HttpSession session) {
     
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        
        int time = 0;
        int fb = 0;
        int teecurr_id = 0;
        long date = 0;
        String displayOpt = "";

        //
        // Get all the parameters entered
        //
        String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
        String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
        String sfb = req.getParameter("fb");               //  front/back indicator
        String index = req.getParameter("index");          //  index value of day (needed by Member_sheet when returning)
        String course = req.getParameter("course");        //  name of course (needed by Member_sheet when returning)
        String returnCourse = req.getParameter("returnCourse");        //  name of course to return to
        String day = req.getParameter("day");              //  name of the day
        String user = (String) session.getAttribute("user");

        //
        //  Get the Display Option if specified (Mobile Devices)
        //
        if (req.getParameter("displayOpt") != null) {

            displayOpt = req.getParameter("displayOpt");
        }

        //
        //  Convert the values from string to int
        //
        try {
            date = Long.parseLong(sdate);
            time = Integer.parseInt(stime);
            fb = Integer.parseInt(sfb);
        } catch (NumberFormatException e) {
            // ignore error
        }
        
        if (club.equals("bishopsgategc")) {
            
            try {
                pstmt1 = con.prepareStatement("SELECT teecurr_id FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");
                pstmt1.clearParameters();
                pstmt1.setLong(1, date);
                pstmt1.setInt(2, time);
                pstmt1.setInt(3, fb);
                pstmt1.setString(4, course);
                
                rs = pstmt1.executeQuery();
                
                if (rs.next()) {
                    teecurr_id = rs.getInt("teecurr_id");
                }
                
            } catch (Exception exc) {
                Utilities.logError("Member_slot.cancel - " + club + " - Error looking up teecurr_id - ERR: " + exc.toString());
            } finally {
                
                try { rs.close(); }
                catch (Exception ignore) {}
                
                try { pstmt1.close(); }
                catch (Exception ignore) {}
            }
        }

        //
        //  Clear the 'in_use' flag for this time slot in teecurr
        //
        if (verifySlot.checkInSession(date, time, fb, course, session)) {
            try {

                pstmt1 = con.prepareStatement(
                        "UPDATE teecurr2 SET in_use = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND in_use_by = ?");

                pstmt1.clearParameters();
                pstmt1.setLong(1, date);
                pstmt1.setInt(2, time);
                pstmt1.setInt(3, fb);
                pstmt1.setString(4, course);
                pstmt1.setString(5, user);
                pstmt1.executeUpdate();

                pstmt1.close();

            } catch (Exception ignore) {
            }
            verifySlot.clearInSession(date, time, fb, course, session);
        }

        //
        //  If Hazeltine National, then check for an associated tee time (w/e's and holidays)
        //
        if (club.equals("hazeltine") || club.equals("moselemsprings") || club.equals("bishopsgategc")) {      // if Hazeltine National

            verifySlot.HclearInUse(date, time, fb, course, day, club, teecurr_id, con);
        }
            
        //
        //  Prompt user to return to Member_sheet or Member_teelist (index = 999)
        //
        if (mobile == 0) {

            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Member Tee Slot Page</Title>");

            if (index.equals("999")) {       // if from Member_teelist

                out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/member_teemain.htm\">");
                out.println("</HEAD>");
                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
                out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
                out.println("<BR><BR>");

                out.println("<font size=\"2\">");
                out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain.htm\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");

            } else {

                if (index.equals("995")) {       // if from Member_teelist_list (old)

                    out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/member_teemain2.htm\">");
                    out.println("</HEAD>");
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
                    out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
                    out.println("<BR><BR>");

                    out.println("<font size=\"2\">");
                    out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain2.htm\">");
                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                    out.println("</form></font>");

                } else {

                    if (index.equals("888")) {       // if from Member_searchmem

                        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/member_searchmem.htm\">");
                        out.println("</HEAD>");
                        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
                        out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
                        out.println("<BR><BR>");

                        out.println("<font size=\"2\">");
                        out.println("<form method=\"get\" action=\"/" + rev + "/member_searchmem.htm\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");

                    } else {

                        if (!returnCourse.equals("")) {    // if multi course club, get course to return to (ALL?)
                            course = returnCourse;
                        }
                        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_jump?index=" + index + "&course=" + course + "\">");
                        out.println("</HEAD>");
                        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
                        out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
                        out.println("<BR><BR>");

                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");

                    }
                }
            }
            out.println("</CENTER></BODY></HTML>");

        } else {

            //
            //  Mobile user
            //
            out.println(SystemUtils.HeadTitleMobile("ForeTees Request List"));
            out.println(SystemUtils.BannerMobile());

            out.println("<div class=\"content\">");
            out.println("<div class=\"headertext\">");    // output the heading
            out.println("Return Requested");
            out.println("</div>");

            out.println("<div class=\"smheadertext\">The tee time has been returned to the system without changes.</div>");

            out.println("<ul>");

            if (index.equals("995")) {         // if came from Member_teelist_list

                out.println("<li>");
                out.println("<form action=\"Member_teelist_mobile\" method=\"get\">");
                out.println("<input type=\"submit\" value=\"Return to List\"></form>");
                out.println("</li>");

            } else {

                out.println("<li>");
                out.println("<form action=\"Member_sheet\" method=\"post\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
                out.println("</li>");
            }

            out.println("</ul></div>");
            out.println("</body></html>");
        }

        out.close();
    }

    // ************************************************************************
    //  Process return to Member_sheet if tee time has changed
    // ************************************************************************
    private void returnToMemSheet(long date, int time, int fb, String course, String day, String club, int mobile, PrintWriter out, Connection con, boolean new_skin, parmSlotPage slotPageParms) {

        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        
        int teecurr_id = 0;
        
        if (club.equals("bishopsgategc")) {
            
            try {
                pstmt1 = con.prepareStatement("SELECT teecurr_id FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");
                pstmt1.clearParameters();
                pstmt1.setLong(1, date);
                pstmt1.setInt(2, time);
                pstmt1.setInt(3, fb);
                pstmt1.setString(4, course);
                
                rs = pstmt1.executeQuery();
                
                if (rs.next()) {
                    teecurr_id = rs.getInt("teecurr_id");
                }
                
            } catch (Exception exc) {
                Utilities.logError("Member_slot.returnToMemSheet - " + club + " - Error looking up teecurr_id - ERR: " + exc.toString());
            } finally {
                
                try { rs.close(); }
                catch (Exception ignore) {}
                
                try { pstmt1.close(); }
                catch (Exception ignore) {}
            }
        }
        
        //
        //  Clear the 'in_use' flag for this time slot in teecurr
        //
        try {

            pstmt1 = con.prepareStatement(
                    "UPDATE teecurr2 SET in_use = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setLong(1, date);         // put the parm in pstmt1
            pstmt1.setInt(2, time);
            pstmt1.setInt(3, fb);
            pstmt1.setString(4, course);
            pstmt1.executeUpdate();

            pstmt1.close();

        } catch (Exception ignore) {
        }

        //
        //  If Hazeltine National, then check for an associated tee time (w/e's and holidays)
        //
        if (club.equals("hazeltine") || club.equals("moselemsprings") || club.equals("bishopsgategc")) {      // if Hazeltine National

            verifySlot.HclearInUse(date, time, fb, course, day, club, teecurr_id, con);
        }

        //
        //  Prompt user to return to Member_sheet
        //
        if(new_skin){
            slotPageParms.page_start_button_go_back = true;
            slotPageParms.page_start_title = "Tee Time Slot Busy";
            slotPageParms.page_start_notifications.add("Sorry, but this tee time slot is currently busy.");
            slotPageParms.page_start_notifications.add("Please select another time or try again later.");

        } else if (mobile == 0) {

            out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H1>Tee Time Slot Busy</H1>");
            out.println("<BR><BR>Sorry, but this tee time slot is currently busy.<BR>");
            out.println("<BR>Please select another time or try again later.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" + rev + "/member_selmain.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();

        } else {

            //
            //  Mobile user
            //
            out.println(SystemUtils.HeadTitleMobile("ForeTees Request"));
            out.println(SystemUtils.BannerMobile());
            out.println("<div class=\"content\">");
            out.println("<div class=\"headertext\">Tee Time Busy</div>");    // output the heading
            out.println("<div class=\"smheadertext\">Sorry, this tee time is currently busy.<BR>Please try again.</div>");
            out.println("<ul><li>");
            out.println("<form action=\"Member_select\" method=\"get\">");
            out.println("<input type=\"submit\" value=\"Return To Date Selection\"></form>");
            out.println("</li>");
            out.println("</ul></div></body></html>");
            out.close();
        }
        
    }

    // *********************************************************
    //  Return to Member_slot
    // *********************************************************
    private void returnToSlot(int mobile, PrintWriter out, parmSlot slotParms) {

        //
        //  Return to _slot to change the player order
        //
        if (mobile > 0) {       // if Mobile

            out.println("<ul><li>");
        }

        out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
        out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
        out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
        out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
        out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
        out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
        out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
        out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
        out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
        out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + slotParms.displayOpt + "\">");

        if (slotParms.club.equals("oaklandhills")) {       // Include custom_disp values if present for oaklandhills

            out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + slotParms.custom_disp1 + "\">");
            out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + slotParms.custom_disp2 + "\">");
            out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + slotParms.custom_disp3 + "\">");
            out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + slotParms.custom_disp4 + "\">");
            out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + slotParms.custom_disp5 + "\">");
        }
        if (slotParms.club.equals("timarroncc")) {

            out.println("<input type=\"hidden\" name=\"custom_disp1\" value=\"" + slotParms.custom_disp1 + "\">");
            out.println("<input type=\"hidden\" name=\"custom_disp2\" value=\"" + slotParms.custom_disp2 + "\">");
            out.println("<input type=\"hidden\" name=\"custom_disp3\" value=\"" + slotParms.custom_disp3 + "\">");
            out.println("<input type=\"hidden\" name=\"custom_disp4\" value=\"" + slotParms.custom_disp4 + "\">");
            out.println("<input type=\"hidden\" name=\"custom_disp5\" value=\"" + slotParms.custom_disp5 + "\">");
        }

        out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
        out.println("</form>");

        if (mobile > 0) {       // if Mobile

            out.println("</li></ul></div></body></html>");

        } else {

            out.println("</CENTER></BODY></HTML>");
        }
        out.close();
    }
    
    

    // *********************************************************
    //  Prompt user when a different tee time is available.
    // *********************************************************
    private void promptOtherTime(int mobile, boolean new_skin, PrintWriter out, parmSlot parm, parmSlotPage slotPageParms) {


        String stime = "";
        String ampm = "";
        String omit = "";

        String sfb = "Front";

        if (parm.fb == 1) {
            sfb = "Back";
        }

        int time = parm.time;
        int hr = 0;
        int min = 0;
        int index = 0;

        boolean customPrompt = false;

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

        if (parm.club.equals("congressional")) {

            try {
                index = Integer.parseInt(parm.index);
            } catch (Exception exc) {
                customPrompt = true;
            }

            if (index > 9) {
                customPrompt = true;
            }
        }

        //
        //  Prompt the user to either accept the times available or return to the tee sheet
        //
        if (new_skin && mobile == 0) {

            slotPageParms.page_start_title = "Notice";
            slotPageParms.page_start_button_go_back = true;
            if (customPrompt) {
                slotPageParms.page_start_notifications.add("Sorry, the tee time you requested is not available.");
                slotPageParms.page_start_notifications.add("Please return to the tee sheet and select another time.");
            } else {
                slotPageParms.page_start_notifications.add("Sorry, the tee time you requested is not available. Would you like an alternate time?");
                slotPageParms.page_start_button_continue = true;
                // Set slot time to that of newly reserved time
                slotPageParms.callback_map.put("time:0", stime);
                slotPageParms.callback_map.put("ttdata", Utilities.encryptTTdata(stime + "|" + parm.fb + "|" + slotPageParms.user));
                slotPageParms.page_start_notifications.add("The next available tee time is: <b>" + stime + " on the " + sfb + "</b>");
                slotPageParms.page_start_notifications.add("Select \"[modalOptions.slotPageLoadNotification.continueButton]\" to use this alternate time.");
            }

        } else if (mobile == 0) {               // if NOT Mobile

            out.println("<HTML><HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Member Prompt - Alternate Tee Time Request</Title>");
            out.println("</HEAD>");

            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
            out.println("<tr><td valign=\"top\" align=\"center\">");
            out.println("<p>&nbsp;&nbsp;</p>");
            out.println("<p>&nbsp;&nbsp;</p>");

            if (customPrompt) {

                out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
                out.println("<tr>");
                out.println("<td width=\"580\" align=\"center\">");
                out.println("<font size=\"3\">");
                out.println("<b>NOTICE</b><br></font>");
                out.println("<font size=\"2\">");
                out.println("<br>The tee time you requested is currently busy.<br>");
                out.println("<br>Please return to the tee sheet and select another time.<br>");
                out.println("</font><font size=\"3\">");
                out.println("<br><b>DO NOT use you browser's BACK button!</b><br>");
                out.println("</font></td></tr>");
                out.println("</table><br>");

                out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
                out.println("<tr><td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<form action=\"Member_slot\" method=\"post\" name=\"can\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
                out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
                out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time + "\">");
                out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
                out.println("<input type=\"hidden\" name=\"cancel\" value=\"yes\">");
                out.println("<input type=\"submit\" value=\"Return to Tee Sheet\"></form>");
                out.println("</font></td></tr>");
                out.println("</table>");

                out.println("</td>");
                out.println("</tr>");
                out.println("</table>");

            } else {

                out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
                out.println("<tr>");
                out.println("<td width=\"580\" align=\"center\">");
                out.println("<font size=\"3\">");
                out.println("<b>NOTICE</b><br></font>");
                out.println("<font size=\"2\">");
                out.println("<br>The tee time you requested is currently busy.<br>");
                out.println("The following tee time is the next available:<br><br>");
                out.println("&nbsp;&nbsp;&nbsp;" + stime + " on the " + sfb + "<br>");
                out.println("<br>Would you like to accept this time?<br>");
                out.println("</font><font size=\"3\">");
                out.println("<br><b>Please select your choice below. DO NOT use you browser's BACK button!</b><br>");
                out.println("</font></td></tr>");
                out.println("</table><br>");

                out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
                out.println("<tr><td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<form action=\"Member_slot\" method=\"post\" name=\"can\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
                out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
                out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time + "\">");
                out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
                out.println("<input type=\"hidden\" name=\"cancel\" value=\"yes\">");
                out.println("<input type=\"submit\" value=\"NO - Return to Tee Sheet\"></form>");
                out.println("</font></td></tr>");

                out.println("<tr><td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<form action=\"Member_slot\" method=\"post\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
                out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
                out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
                out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time + "\">");
                out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
                out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
                out.println("<input type=\"hidden\" name=\"player1\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"player2\" value=\"" + omit + "\">");    // new tee time requested
                out.println("<input type=\"hidden\" name=\"player3\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"player4\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"player5\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"notes\" value=\"" + omit + "\">");
                out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + parm.orig_by + "\">");
                out.println("<input type=\"hidden\" name=\"hide\" value=\"0\">");
                out.println("<input type=\"hidden\" name=\"promptOtherTime\" value=\"yes\">");
                out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
                out.println("</font></td></tr>");
                out.println("</table>");

                out.println("</td>");
                out.println("</tr>");
                out.println("</table>");
            }

            out.println("</font></center></body></html>");

        } else {

            //
            //  Mobile user
            //
            out.println(SystemUtils.HeadTitleMobile("ForeTees Request List"));
            out.println(SystemUtils.BannerMobile());

            out.println("<div class=\"content\">");
            out.println("<div class=\"headertext\">");    // output the heading
            out.println("Tee Time is Busy.");
            out.println("</div>");

            out.println("<div class=\"smheadertext\">Sorry, the tee time you requested is busy.<BR>This is the next available time:<BR>"
                    + "&nbsp;" + stime + " on the " + sfb + "<br><br>Would you like to accept this time?</div>");

            out.println("<ul>");

            out.println("<li><form action=\"Member_slot\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + parm.displayOpt + "\">");
            out.println("<input type=\"hidden\" name=\"cancel\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"NO - Return to Tee Sheet\"></form></li>");

            out.println("<li><form action=\"Member_slot\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + omit + "\">");    // new tee time requested
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + omit + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + parm.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + parm.displayOpt + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"0\">");
            out.println("<input type=\"hidden\" name=\"promptOtherTime\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\"></form></li>");

            out.println("</ul></div></body></html>");
        }
        if (!new_skin || mobile > 0) {  // With new skin, we will return and allow the slotpage to display the message (only close old skin and mobile)
            out.close();
        }
    }          // end of promptOtherTime
    
    

    // ***************************************************************************************
    //   Display Error Msg based on user - used when we do not need to return to _lott page
    // ***************************************************************************************
    private void buildError(String title, String content, int mobile, PrintWriter out) {

        if (mobile == 0) {       // if NOT mobile user

            out.println(SystemUtils.HeadTitle("Member Request Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            if (!title.equals("")) {
                out.println("<BR><BR><H3>" + title + "</H3>");
            }
            out.println("<BR>" + content);
            out.println("<BR><BR>");

        } else {

            //  Mobile user

            out.println(SystemUtils.HeadTitleMobile("Member Request Error"));
            out.println("<div class=\"headertext\">" + title + "</div>");
            out.println("<div class=\"smheadertext\">" + content + "</div>");
            out.println("<div class=\"content\">&nbsp;</div>");
        }

    }

    // *********************************************************
    //  Database Error
    // *********************************************************
    private void dbError(PrintWriter out, Exception e1, String msg) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact your club manager.");
        out.println("<BR><BR>Process: " + msg + "<br>  Exception: " + e1.getMessage());
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(1)\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
    }

    private String getPlayerName(String playerValue) {

        StringTokenizer tok = new StringTokenizer(playerValue, "|");

        String player = playerValue;

        if (tok.countTokens() > 0) {
            player = tok.nextToken();
        }

        return (player);

    }

    private String getPlayerCW(String playerValue) {

        String tmode = "";
        StringTokenizer tok = new StringTokenizer(playerValue, "|");

        if (tok.countTokens() > 1) {
            tok.nextToken();        // eat first value (player name)
            tmode = tok.nextToken();
        }

        return (tmode);

    }
}
