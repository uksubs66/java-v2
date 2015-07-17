
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
 *        7/18/14   Remove oldskin and old mobile code paths. Fork old mobile off to Member_slot_oldmobile.     
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
//import com.google.gson.*; // for json

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
//import com.foretees.common.alphaTable;
import com.foretees.common.BigDate;
import com.foretees.common.medinahCustom;
import com.foretees.common.congressionalCustom;
import com.foretees.common.Utilities;
import com.foretees.common.formUtil;
import com.foretees.common.parmSlotPage;
import com.foretees.common.Connect;
import com.foretees.common.reqUtil;
import com.foretees.common.reservationUtil;
import com.foretees.common.teeCurrGroupDetail;
import com.foretees.common.timeUtil;
import com.foretees.common.ArrayUtil;
import com.foretees.common.htmlUtil;
import com.foretees.common.slotPostBack;
import com.foretees.common.reservationPlayer;
import com.foretees.common.Labels;

public class Member_slot extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                       // Software Revision Level (Version)
    //static long Hdate1 = ProcessConstants.memDay;            // Memorial Day
    //static long Hdate2 = ProcessConstants.july4;             // 4th of July - Monday
    //static long Hdate2b = ProcessConstants.july4b;           // 4th of July - ACTUAL 7/04
    //static long Hdate3 = ProcessConstants.laborDay;          // Labor Day
    //static long Hdate7 = ProcessConstants.tgDay;             // Thanksgiving Day
    //static long Hdate8 = ProcessConstants.colDay;            // Columbus Day
    //static long Hdate9 = ProcessConstants.colDayObsrvd;      // Columbus Day Observed

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
        
        PrintWriter out = resp.getWriter();
        
        HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder
        
        if (session == null) {
            return;
        }
        
        // If we're in old mobile mode, divert control to the old Member_slow_oldmobile
        // All "old" mobile (non responsive mobile) is being stripped out of Member_slot
        if (reqUtil.getSessionInteger(session, "mobile", 0) > 0) {
            Member_slot_oldmobile slotoldmobile = new Member_slot_oldmobile();
            slotoldmobile.doPost(req, resp);
            return;
        }

        //
        //  Prevent caching so all buttons are properly displayed, etc.
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        
        boolean rwd = Utilities.getRequestBoolean(req,ProcessConstants.RQA_RWD,false);
        
        ResultSet rs = null;

        Connection con = Connect.getCon(req);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<p><H3>Database Connection Error</H3></p>");
            out.println("<p>Unable to connect to the Database.</p>");
            out.println("<p>Please try again later.</p>");
            out.println("<p>If problem persists, contact your club manager.</p>");
            out.println("<p><BR></p>");
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
        //String pcw = (String) session.getAttribute("wc");             // get users walk/cart preference
        //int activity_id = (Integer) session.getAttribute("activity_id");
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        
        //
        //  Do not allow Dsert Mountain 'Family Guest Play' members to enter or change tee times 
        // //  We need to change the way we check and report this error to be compatible with json
        if (club.equals("desertmountain") && userMship.startsWith("Family Guest")) {

            out.println(SystemUtils.HeadTitle("Access Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<p><H3>Member Access Error</H3></p>");
            out.println("<p>Sorry, your membership classification does not allow you to make or modify tee times.</p>");
            out.println("<p>Please have a member with full golf priveleges assist you or contact the golf shop.</p>");
            out.println("<p><BR></p>");
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
        //      'submitForm'  - a reservation request (from self)
        //      'remove'  - a 'cancel reservation' request (from self - Cancel Tee Time)
        //
        if (req.getParameter("cancel") != null) {

            cancel(req, out, club, con, session);       // process cancel request
            return;
        }

        if ((req.getParameter("submitForm") != null) || (req.getParameter("remove") != null)) {
            
            out.print(Common_slot.slotJson(processRequest(req)));
            return;

        }

        /*
        String jump = "0";                     // jump index - default to zero (for _sheet)

        if (req.getParameter("jump") != null) {            // if jump index provided

            jump = req.getParameter("jump");
        }
         */
        //
        //   Submit = 'time:fb' or 'letter'
        //
        int in_use = 0;
        int hr = 0;
        int min = 0;
        int time = 0;
        long mm = 0;
        long dd = 0;
        long yy = 0;
        long temp = 0;
        long date = 0;
        int fb = 0;
        //int x = 0;
        //int hide = 0;
        
        
        int custom_int = 0;

        int players_per_group = 5;
        //int max_players = 0;

        int thishr = 0;
        int thismin = 0;
        int thisTime = 0;
        //int thisTimeAdjusted = 0;

        Calendar cal = new GregorianCalendar();       // get todays date
        Calendar cal3 = new GregorianCalendar();       // get todays date

        
        //String[] usergA = {};
        //String[] origA = {};

        String sdate = "";
        String stime = "";
        String ampm = "";
        String sfb = "";
        //String notes = "";
        String msg = "";
        //String orig_by = "";
        String course_disp = "";

        //String[] custom_dispA = {};

        //String[] customA = {};
        
        String day_name = "";

        //int[] gpA = {};
        //boolean[] blockPA = {};

        //boolean restrictByOrig = false;              // Used for customs to use orig values to restrict access within a tee time
        //boolean skipMembers = false;                 // If true, the Member List, Alphabet letter selection, and Partner list will be hidden
        //boolean skipGuests = false;                  // If true, the Guest Type list will be hidden

        boolean groupEdit = false;
        boolean groupOwner = false;
        boolean showFBOnTitle = false;
        boolean showCourseOnTitle = false;
        
        boolean json_mode = (req.getParameter("json_mode") != null);
        boolean force_transitional = req.getParameter("force_transitional") != null;
        if(!rwd && force_transitional){
            req.setAttribute(ProcessConstants.RQA_FORCE_TRANSITIONAL, true);
        }
        //
        //  New tee time indicator (if true, the tee time was empty when it was selected from the tee sheet)
        //
        boolean newreq = false;

        boolean openPlayShotgun = false;       // custom for Open Play Shotgun simple mode
        
        //
        //   Flag for Cancel Tee Time button (show or not show)
        //
        //boolean allowCancel = true;              // default to 'allow'
        //boolean hawksLandingCustomMsg = false;

        //
        //  parm block to hold the club parameters
        //
        //parmClub parm = new parmClub(0, con);


        // Create fill slot page parameters we already know, fill in the rest later
        parmSlotPage slotPageParms = new parmSlotPage();

        slotPageParms.club = club;
        slotPageParms.pcw = reqUtil.getSessionString(req, "wc", "");
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
        slotPageParms.zip_code = reqUtil.getSessionString(req, "zipcode", "");
        slotPageParms.clientSideCancel = false;

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
        //String day_name = req.getParameter("day");       //  name of the day
        //String index = req.getParameter("index");        //  index value of day (needed by Member_sheet when returning)
        //String p5 = req.getParameter("p5");              //  5-somes supported
        String course = req.getParameter("course");      //  Name of Course
        
        List<teeCurrGroupDetail> groupDetails = new ArrayList<teeCurrGroupDetail>();
        List<parmSlot> parmList = new ArrayList<parmSlot>();
        List<parmSlot> mySlots = new ArrayList<parmSlot>();
        List<parmSlot> lockedSlots = new ArrayList<parmSlot>();
        
        List<String> teecurr_ids = new ArrayList<String>();

        // Start configure block.  We will break out of this if we encounter an issue.  
        configure_slot:
        {

            //if (req.getParameter("newreq") != null) {        // passed from Member_sheet (will be false if from any other page)
            // This is now done below by checking the slot, not trusting the client
            //    newreq = true;          // new tee time request (players empty on tee sheet)
            //}
            
            sfb = reqUtil.getParameterString(req, "fb", "");
            sdate = reqUtil.getParameterString(req, "sdate", reqUtil.getParameterString(req, "date", ""));

            if (req.getParameter("stime") != null) {         // if time was passed in stime

                stime = req.getParameter("stime");

            } else {                                         // call from Member_sheet

                if (req.getParameter("ttdata") != null) {

                    String tmp = Utilities.decryptTTdata(req.getParameter("ttdata"));

                    StringTokenizer tok = new StringTokenizer(tmp, "|");     // separate name around the colon

                    stime = tok.nextToken();                          // shart hand time (9:35 AM)
                    sfb = tok.nextToken();                            // front/back indicator value
                    tmp = tok.nextToken();                            // username of member

                    if (!tmp.equalsIgnoreCase(user)) {

                        logoffAC(out, session, con);     // force logoff and exit
                        return;

                    }

                } else {
                    
                    slotPageParms.page_start_button_go_back = true;
                    slotPageParms.page_start_title = "Database Access Error";
                    slotPageParms.page_start_notifications.add("Unable to process your request.");
                    slotPageParms.page_start_notifications.add("Please try again later.");
                    slotPageParms.page_start_notifications.add("If problem persists, please contact customer support.");
                    break configure_slot;

                }

                //
                //  Make sure the number of times requested was passed (consecutive tee times) - should be 1
                //
                if (contimes < 1) {                            // if less than one tee time requested

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "Invalid Request";
                        slotPageParms.page_start_notifications.add("Sorry, but you must first select the number of tee times you are requesting.");
                        slotPageParms.page_start_notifications.add("Please select the count from the drop-down list to the right of the time button after you return.");
                        slotPageParms.page_start_notifications.add("Contact your golf shop if you feel you incorrectly received this message.");
                        break configure_slot;
                }
            }

            //
            //  Get today's date
            //
            cal = new GregorianCalendar();       // get todays date

            thishr = cal.get(Calendar.HOUR_OF_DAY);
            thismin = cal.get(Calendar.MINUTE);

            thisTime = (thishr * 100) + thismin;         // get current time (Central TIme!!)
            //thisTimeAdjusted = Utilities.adjustTime(con, thisTime);

            //
            //  Convert the values from string to int
            //
            try {
                date = Long.parseLong(sdate);
                fb = Integer.parseInt(sfb);
            } catch (NumberFormatException e) {
                // ignore error
            }
            
            //indReal = getDaysBetween(date);            // get # of days in between today and the date

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

            //shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)
            
            cal3 = new GregorianCalendar();
            cal3.set((int)yy, (int)mm-1, (int)dd);    // Set to the date of the current tee time
            //week_of_year = cal3.get(Calendar.WEEK_OF_YEAR);

            //
            //  Determine 'ind' value in case we came from Member_teelist or _searchmem (ind = 888 or 999 or 995)
            //
            //ind = getDaysBetween(date);            // get # of days in between today and the date of the tee time

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

            //
            //     Check the club db table for X and guests
            //
                /*
            try {

                parm.club = club;                   // set club name
                parm.course = course;               // and course name

                getClub.getParms(con, parm);        // get the club parms

                x = parm.x;
            } catch (Exception exc) {             // SQL Error - ignore guest and x

                x = 0;
            }
                 * 
                 */

            //
            //  Check if this is a new tee time request (empty time).
            //
            newreq = verifySlot.checkNewReq(date, time, fb, course, con);
            
            //
            //  Check if we're using group edit mode, and that we're loading the first item in the group
            //  and that we're using RWD (group edit is only supported by the RWD slot page)
            //
            
            if(Utilities.usesGroupedSlotEdit(req) && !newreq){
                groupDetails = reservationUtil.getTeeCurrGroupDetails(date, time, fb, course, con);
                if(groupDetails.size() > 1){
                    teeCurrGroupDetail firstDetail = groupDetails.get(0);
                    if(firstDetail.username.equalsIgnoreCase(user)){
                        // Current user owns this group.
                        if(req.getParameter("edit_group") != null){
                            groupEdit = true;
                            groupOwner = true;
                            if(firstDetail.date != date || firstDetail.time != time || firstDetail.fb != fb || !firstDetail.course.equalsIgnoreCase(course)){
                                // The currently selected time is not the first in the group.
                                // We'll need to switch to that time
                                date = firstDetail.date;
                                sdate = Integer.toString((int) firstDetail.date);
                                time = firstDetail.time;
                                fb = firstDetail.fb;
                                sfb = Integer.toString(firstDetail.fb);
                                stime = timeUtil.get12HourTime(firstDetail.time);
                                course = firstDetail.course;
                                newreq = verifySlot.checkNewReq(date, time, fb, course, con);

                            }
                        } else if(req.getParameter("edit_single") == null) {
                            slotPageParms.page_start_title = "Group Tee Time Edit";
                            String group_edit_message = ""
                                    + "This tee time is part of a group.<br><br>"
                                    + "Would you like to edit the "+timeUtil.get12HourTime(time) +" time alone, or would you like to edit the entire group?";
                            slotPageParms.page_start_notifications.add(group_edit_message);
                            slotPageParms.callback_button_map.put("edit_single", formUtil.makeButton("Edit "+timeUtil.get12HourTime(time), formUtil.keyValMap(new String[]{"edit_single", "yes"})));
                            if(rwd){
                                // Just give a simple option to edit the group
                                slotPageParms.callback_button_map.put("edit_group", formUtil.makeButton("Edit Group", formUtil.keyValMap(new String[]{"edit_group", "yes"})));
                            } else {
                                // Replace the current non-rwd slot page with a transitional rwd slot page
                                // so group editing can continue

                                Map<String, Object> forceRwd = formUtil.makeButton("Edit Group");
                                Map<String, Object> uriMap = new HashMap<String, Object>();
                                uriMap.putAll(slotPageParms.callback_map);
                                uriMap.put("target", "Member_slot");
                                uriMap.put("force_transitional", true);
                                uriMap.put("suppress_unload", true);
                                uriMap.put("edit_group", "yes");
                                forceRwd.put("replace", uriMap);
                                slotPageParms.callback_button_map.put("force_transitional", forceRwd);
                            }

                            slotPageParms.page_start_button_go_back = false;
                            slotPageParms.page_start_button_continue = false;
                            slotPageParms.page_start_button_accept = false;
                            break configure_slot;
                        } else {
                            groupOwner = true;
                        }
                    }
                }
            }
            
                //
                //  Get the players' names and check if this tee slot is already in use
                //
                
                Set<Integer> fbtypes = new HashSet<Integer>();
                Set<String> coursetypes = new HashSet<String>();

                if(groupEdit){
                    
                    int i = 0;
                    for(teeCurrGroupDetail groupDetail : groupDetails){
                        
                        parmSlot newParm = new parmSlot(groupDetail.teecurr_id, req);
                        //newParm.p5 = p5; // Now set automatically
                        //newParm.index = index;
                        newParm.mtype = mtype;
                        newParm.mship = userMship;
                        fbtypes.add(groupDetail.fb); // count how many different fb we have
                        coursetypes.add(groupDetail.course.toLowerCase()); // count how many different courses we have
                        parmList.add(newParm);
                        newParm.group_slots = parmList;
                        newParm.group_index = i;
                        i++;
                        
                    }
                } else {
                    parmSlot newParm = new parmSlot(date, time, fb, course, req);
                    //newParm.p5 = p5; // Now set automatically
                    //newParm.index = index;
                    newParm.mtype = mtype;
                    newParm.mship = userMship;
                    parmList.add(newParm);
                    newParm.group_slots = parmList;
                    newParm.group_index = 0;
                    if(newParm.orig_by.equalsIgnoreCase(user)){
                        groupOwner = true;
                    }
                    
                }
               
                showFBOnTitle = fbtypes.size() > 1; // If we're using multiple times and they have different FBs, set this flag.
                showCourseOnTitle = coursetypes.size() > 1;
                
                slotParms = parmList.get(0); // We'll use the first in the list as our default, until all customs below are built to accomodate more.
                day_name = slotParms.day;
                
                // Set slot access, options, customs
                // Do _NOT_ add access customs  in Member_slot! (edit/position locks/cancel, etc.)
                // Put them in parmSlot.setAccess()
                slotParms.setAccessOnGroup(req);
                
                custom_int = slotParms.custom_int;
                //notes = slotParms.notes;
                //hide = slotParms.hide;
                //orig_by = slotParms.orig_by;

                //
                //  Verify the required parms exist
                //
                if (date == 0 || time == 0 || course == null || user == null || user.equals("")) {

                    //
                    //  save message in /" +rev+ "/error.txt
                    //
                    msg = "Error in Member_slot - checkInUse Parms - for user " + user + " at " + club + ".  Date= " + date + ", time= " + time + ", course= " + course + ", fb= " + fb + ", index= " + slotParms.index;   // build msg
                    SystemUtils.logError(msg);                                   // log it
                    in_use = 1;          // make like the time is busy

                } else {               // continue if parms ok

                    try {
                            
                        // Check for 5-some restriction, if applicable, and reject if course allows 5-somes and has a 5-some restriction covering it.
                        if (slotParms.allow_edit && slotParms.countPlayers() == 4 && !slotParms.hasUser(user) && !slotParms.hasOrig(user)
                                && Utilities.check5SomeRests(date, time, course, fb, day_name, club, req)) {

                            slotParms.allow_edit = false;
                        }

                        if (newreq == true && json_mode == false && force_transitional == false) {      // if new request and 1st time through, find next avail if busy

                            boolean acError = false;
                            int daysInAdv = 0;
                            int advTime = 0;

                            if (club.equals("eaglecreek") || club.equalsIgnoreCase("orchidisland") || club.equalsIgnoreCase("ironwood")) {

                                verifySlot.getDaysInAdv(con, slotParms.club_parm, slotParms.mship);   // get days in adv and time parms for this user

                                //
                                //   Get the days in advance and time of day values for the day of this tee time
                                //   This should all be moved to parmSlot.setAccess()
                                //
                                if (slotParms.day.equals("Sunday")) {

                                    daysInAdv = slotParms.club_parm.advdays1;
                                    advTime = slotParms.club_parm.advtime1;

                                } else if (slotParms.day.equals("Monday")) {

                                    daysInAdv = slotParms.club_parm.advdays2;
                                    advTime = slotParms.club_parm.advtime2;

                                } else if (slotParms.day.equals("Tuesday")) {

                                    daysInAdv = slotParms.club_parm.advdays3;
                                    advTime = slotParms.club_parm.advtime3;

                                } else if (slotParms.day.equals("Wednesday")) {

                                    daysInAdv = slotParms.club_parm.advdays4;
                                    advTime = slotParms.club_parm.advtime4;

                                } else if (slotParms.day.equals("Thursday")) {

                                    daysInAdv = slotParms.club_parm.advdays5;
                                    advTime = slotParms.club_parm.advtime5;

                                } else if (slotParms.day.equals("Friday")) {

                                    daysInAdv = slotParms.club_parm.advdays6;
                                    advTime = slotParms.club_parm.advtime6;

                                } else {

                                    daysInAdv = slotParms.club_parm.advdays7;
                                    advTime = slotParms.club_parm.advtime7;
                                }
                            }                         // end of custom


                            //
                            //  customs to catch cheaters - log them off if cheating
                            //
                            if (club.equals("admiralscove") && ((slotParms.ind == 3 && thisTime < 632) || slotParms.ind > 3)) {   // if 3 days in adv & near 7:30 AM ET

                                acError = checkACearly(user, slotParms.ind, 1);

                                if (acError == true) {

                                    logoffAC(out, session, con);     // force logoff and exit
                                    return;
                                }
                            }                        // end of custom

                            //
                            //  Check Gallery Golf for cheaters also
                            //
                            if (club.equals("gallerygolf") && !userMship.equals("Manager") && slotParms.ind > 5) {

                                acError = checkGGearly(user, slotParms.ind, con);

                                if (acError == true) {

                                    logoffAC(out, session, con);     // force logoff and exit
                                    return;
                                }
                            }


                            //
                            //  Check Eagle Creek for cheaters
                            //
                            if (club.equals("eaglecreek") && ((slotParms.ind == daysInAdv && thisTime < 602) || slotParms.ind > daysInAdv)) {  // 7:00 ET 2/3 days in adv

                                acError = verifyCustom.checkECearly(user, slotParms.ind, 1, daysInAdv, advTime, con);   // check days in adv, time and slots requested

                                if (acError == true) {

                                    logoffAC(out, session, con);     // force logoff and exit
                                    return;
                                }
                            }



                            //
                            //  New Tee Time Request - Check if in use and if so, search for the next available time
                            //
                            
                            //in_use = verifySlot.checkInUseN(date, time, fb, course, user, slotParms, con, session);
                            
                            
                            // Iterate all parms to verify that we have access to all
                            int test_in_use = 0;
                            for(parmSlot vParm : parmList){
                                test_in_use = verifySlot.checkInUseN(vParm.date, vParm.time, vParm.fb, vParm.course, user, vParm, req);
                                if(test_in_use > 0){
                                    in_use = test_in_use;
                                    lockedSlots.add(vParm);
                                    //vParm.user_has_lock = true; // This would have previously been set to false
                                    vParm.user_has_partial_lock = true; // This would have previously been set to false
                                } else {
                                    // Slot doesn't seem to be in use.  Add it to our list incase we need to roll back
                                    mySlots.add(vParm);
                                }
                            }
                            if(in_use > 0){
                                // At least one of our slots is in use.  Unlock the slots we may have already locked
                                for(parmSlot rParm : mySlots){
                                    verifySlot.clearInUse(rParm.date, rParm.time, rParm.fb, rParm.course, user, con, session);
                                }
                            }

                            

                            //
                            //  temp to catch cheaters - make history entry if 7 days in adv and before 7:05 AM ET
                            //
                            if ((club.equals("pelicansnest") && slotParms.ind == 7 && thisTime < 605)
                                    || (club.equals("eaglecreek") && daysInAdv > 0 && slotParms.ind == daysInAdv && thisTime < 605)
                                    || (club.equals("timarroncc") && slotParms.ind == 4 && thisTime < 703)
                                    || (club.equals("wollastongc") && slotParms.ind == 4 && thisTime < 603)
                                    || (club.equals("ccnaples") && slotParms.ind == 7 && thisTime < 603)
                                    || (club.equals("admiralscove") && slotParms.ind == 3 && thisTime < 635)                               
                                    || (club.equals("orchidisland") && daysInAdv > 0 && slotParms.ind == daysInAdv && thisTime < 633)
                                    || (club.equals("ironwood") && daysInAdv > 0 && slotParms.ind == daysInAdv && thisTime < 833)) {    // temp custom to catch cheater !!!!!!!!!!!!!
                                //  make history entry to track the time entered
                                SystemUtils.updateHist(date, slotParms.day, time, fb, course, slotParms.player1, slotParms.player2, slotParms.player3,
                                        slotParms.player4, slotParms.player5, user, name, 0, con);
                            }   // end of custom


                            //
                            //  If we did not get the exact tee time requested, then ask the user if they want to proceed or go back.
                            //
                            if (in_use == 9 && !club.equals("olyclub")) {                     // if found, but different than requested

                                promptOtherTime(slotParms, slotPageParms);    // send prompt

                                time = slotParms.time;
                                fb = slotParms.fb;
                                break configure_slot;

                            }

                        } else {      // check in use - existing tee time or json_mode (2nd pass)

                            //in_use = verifySlot.checkInUse(date, time, fb, course, user, slotParms, con, session);
                            // Iterate all parms to verify that we have access to all
                            int test_in_use = 0;
                            for(parmSlot vParm : parmList){
                                test_in_use = verifySlot.checkInUse(vParm.date, vParm.time, vParm.fb, vParm.course, user, vParm, req);
                                if(test_in_use > 0){
                                    in_use = test_in_use;
                                    lockedSlots.add(vParm);
                                    //vParm.user_has_lock = true; // This would have previously been set to false
                                    vParm.user_has_partial_lock = true; // This would have previously been set to false
                                } else {
                                    // Slot doesn't seem to be in use.  Add it to our list incase we need to roll back
                                    mySlots.add(vParm);
                                }
                            }
                            if(in_use > 0){
                                // At least one of our slots is in use.  Unlock the slots we may have already locked
                                for(parmSlot rParm : mySlots){
                                    verifySlot.clearInUse(rParm.date, rParm.time, rParm.fb, rParm.course, user, con, session);
                                }
                            }
                            
                        }

                    } catch (Exception e1) {

                        msg = "Member_slot Check in use flag failed - Exception: " + e1.getMessage();

                        SystemUtils.logError(msg);                                   // log it

                        in_use = 1;          // make like the time is busy
                    }
                }

                
                //
                //  Custom for Open Play Shotguns - simple mode
                //
                openPlayShotgun = false;
                
                if ((club.equals("engineerscc") || club.equals("mosscreek")) && !slotParms.event.equals("")) {    // if event during this time
                    
                    int cat_id = 1;
                    
                    if (club.equals("mosscreek")) cat_id = 13;       // category id for "Open Play Shotguns"
                    
                    int event_id = Utilities.getEventIdFromName(slotParms.event, con);       // check event category for "Open Play Shotguns"
                    
                    ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
                    temp_category_ids.add(cat_id);                                        // Open Play Shotguns must be id 13

                    String cat_name = Utilities.checkEventCategoryBindings(event_id, temp_category_ids, con);   
                    
                    if (cat_name.equals("")) {     // cat_name will be empty if it matches
                    
                        openPlayShotgun = true;
                    }
                }                
                
                
                int memedit = 0;          // event member edit option indicator
                
                if (!slotParms.event.equals("")) {   // if time slot part of an event

                    memedit = Utilities.getMemeditOpt(slotParms.event, con);       // get the memedit setting for this event
                }
                
                if (in_use != 0 || !slotParms.blocker.equals("") || (!slotParms.event.equals("") && memedit == 0 && openPlayShotgun == false)) {   // if time slot already in use or not allowed
                                   
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
                            if(groupEdit){
                                List<String> blockedTimes = new ArrayList<String>();
                                for(parmSlot lparm : lockedSlots){
                                    blockedTimes.add(timeUtil.get12HourTime(lparm.time));
                                }
                                slotPageParms.page_start_title = "Tee Time Slot Busy";
                                
                                slotPageParms.page_start_notifications.add("Sorry, but the "+htmlUtil.joinList(blockedTimes) +" tee time slot"+(blockedTimes.size()>1?"s are":" is")+" currently busy.");
                                slotPageParms.page_start_notifications.add("Please select another time or try again later.");
                            } else {
                                slotPageParms.page_start_title = "Tee Time Slot Busy";
                                slotPageParms.page_start_notifications.add("Sorry, but this tee time slot is currently busy.");
                                slotPageParms.page_start_notifications.add("Please select another time or try again later.");
                            }
                            
                        }
                        break configure_slot;

                }

                //
                //  Hacker check - if the tee time is full, then make sure this member is part of it
                //
                
                //boolean allow = false;
                
                if (!slotParms.allow_edit) {   // if member not part of it or not empty
                    slotPageParms.page_start_button_go_back = true;
                    if(slotParms.block_reason != null){
                        slotPageParms.page_start_title = "Access Restriction";
                        slotPageParms.page_start_notifications.add(slotParms.block_reason);
                    } else {
                        slotPageParms.page_start_title = "Tee Time Slot Busy";
                        slotPageParms.page_start_notifications.add("Sorry, but this tee time slot is currently busy.");
                        slotPageParms.page_start_notifications.add("Please select another time or try again later.");
                    }
                    break configure_slot;

                }
                
                //
                // Check to see if user has origined too many tee times for this day
                //
                //boolean error = false;

                // if max_orig is enabled and user didn't originally book this tee time, or is not part of this tee time
                if (newreq && slotParms.club_parm.max_originations > 0 && !slotParms.orig_by.equalsIgnoreCase(user) && !slotParms.hasUser(user)
                        && verifySlot.checkMaxOrigBy(user, date, slotParms.club_parm.max_originations, con)) {
                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "[options.notify.slotBusyTitle]";
                        if (club.equalsIgnoreCase("discoverybay")) {
                            slotPageParms.page_start_notifications.add("Contact the Golf Shop if you have any questions.");
                        } else {
                            
                            slotPageParms.page_start_notifications.add("Sorry, but you are allowed to create up to " + slotParms.club_parm.max_originations + " tee times for any given day.");
                            slotPageParms.page_start_notifications.add("You may still be able to create additional tee times for other days.");
                            slotPageParms.page_start_notifications.add("This means that you have created your allowed " + slotParms.club_parm.max_originations + " tee times for this day.");
                            slotPageParms.page_start_notifications.add("Contact the Golf Shop if you have any questions.");
                        }
                        break configure_slot;
                }
        
                if (club.equals("bishopsgategc")) {
                
                    if (newreq == true && json_mode == true) {
                        
                        // This just doesn't seem right.  I don't understand the way
                        // custom_int is being used here.  It doesn't seem like a value that should be overriden
                        if(req.getParameter("custom_int") != null){
                            custom_int = reqUtil.getParameterInteger(req, "custom_int", 0);
                        }
                        
                        //try { custom_int = Integer.parseInt(req.getParameter("custom_int")); } catch (Exception ignore) {} 
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
                        
                        // Don't like passing custom_int around...  need more information on this.
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
                " Additionally, you will not be allowed to replace players in this tee time with guests at a later date.";
                }
                }
                 */


                if (!memNotice.isEmpty() && (req.getParameter("skip_member_notice") == null)) {      // if message to display

                    //
                    //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
                    //

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_button_accept = true;
                        slotPageParms.page_start_title = "[options.notify.noticeFromGolfShopTitle]";
                        slotPageParms.page_start_notifications.add(memNotice);
                        slotPageParms.page_start_notifications.add("[options.notify.continueWithRequestPrompt]");
                        slotPageParms.callback_map.put("skip_member_notice", "yes");
                        break configure_slot;

                }

            //}


            


            //
            //  Custom for Oakmont CC - if less than 30 days in advance, guest tee times cannot be changed -
            //                          players may be added, but not removed.  Also, do not allow cancel from
            //                          My Tee Times or Search (can't tell how far in advance).
            //
            //boolean oakguests = false;

            //if (club.equals("oakmont")) {      // if Oakmont CC

            //    oakguests = checkOakGuests(date, time, fb, course, con);   // check if any guests in tee time (if within 30 days)
            //}
            
                        // Set player count/size
            //players_per_group = 5;
            //players = (p5.equals("Yes")?5:4);
            



            

            
            
            
            // Set guest types footer notes:
            if (club.equals("cherryhills")) {
                slotPageParms.guest_type_footer_notes.add("<b>Note:</b> IN-Town guests reside within 70 air miles of the club.");
            }

            

            if (club.equals("tcclub")) {
                slotPageParms.member_tbd_text = "Guest";
            }
            if (club.equals("hudsonnatl")) {
                int shortDate = (slotParms.mm * 100) + slotParms.dd;
                if (( shortDate == 525 || shortDate == 907 || shortDate == 1012 || slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && slotParms.time >= 730 && slotParms.time <= 954) {
                slotParms.member_tbd = false;
                }
            }

            if ((club.equals("hawkslandinggolfclub") && slotParms.checkHawksLandingBlockCancel(req))) {
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

           if (!slotParms.notes.equals("") && newreq == false && !user.equalsIgnoreCase(slotParms.orig_by)) {    // if existing notes, and existing tee time that this user did not originate

              slotPageParms.protect_notes = true;      // protect the notes - do not allow user to add/change/remove the notes
           }
        }
        if (club.equals("llnhga")) {
            int shortDate = (slotParms.mm * 100) + slotParms.dd;
            
            if (shortDate >= 401 && shortDate < 1031) {

                int[] advtimeA = new int[7];      //hold adv times for 7 days of week
                int[] daysA = new int[7];         //hold days in advance for 7 days of week      
                String[] advpmA = new String[7];  //hold adv times AM OR PM for 7 days of week

                int year = 0;
                int month = 0;
                int day = 0;
                int days = 0;
                int dayNum = 0;
                int advtime = 0;

                PreparedStatement pstmt = null;
                ResultSet rs1 = null;

                try {

                    pstmt = con.prepareStatement("SELECT * from mship5 where mship = ?");
                    pstmt.clearParameters();
                    pstmt.setString(1, slotParms.mship);
                    rs1 = pstmt.executeQuery();

                    if (rs1.next()) {
                        advtimeA[0] = rs1.getInt("advhrd1");
                        advtimeA[1] = rs1.getInt("advhrd2");
                        advtimeA[2] = rs1.getInt("advhrd3");
                        advtimeA[3] = rs1.getInt("advhrd4");
                        advtimeA[4] = rs1.getInt("advhrd5");
                        advtimeA[5] = rs1.getInt("advhrd6");
                        advtimeA[6] = rs1.getInt("advhrd7");
                        daysA[0] = rs1.getInt("days1");
                        daysA[1] = rs1.getInt("days2");
                        daysA[2] = rs1.getInt("days3");
                        daysA[3] = rs1.getInt("days4");
                        daysA[4] = rs1.getInt("days5");
                        daysA[5] = rs1.getInt("days6");
                        daysA[6] = rs1.getInt("days7");
                        advpmA[0] = rs1.getString("advamd1");
                        advpmA[1] = rs1.getString("advamd2");
                        advpmA[2] = rs1.getString("advamd3");
                        advpmA[3] = rs1.getString("advamd4");
                        advpmA[4] = rs1.getString("advamd5");
                        advpmA[5] = rs1.getString("advamd6");
                        advpmA[6] = rs1.getString("advamd7");
                    }


                } catch (Exception e) {

                } finally {
                    Connect.close(rs1, pstmt);

                }

                for (int i = 0; i < advtimeA.length; i++) {           //Adjust time to military time
                    advtimeA[i] = advtimeA[i] * 100;
                    if (advpmA[i].equalsIgnoreCase("PM")) {
                        advtimeA[i] += 1200;
                    }
                }




                month = slotParms.mm;                    // get month
                day = slotParms.dd;                      // get day
                year = slotParms.yy;                     // get year

                BigDate today = BigDate.localToday();                 // get today's date
                BigDate thisdate = new BigDate(year, month, day);     // get requested date
                dayNum = thisdate.getDayOfWeek();                     //get the day of the week  

                int ind = (thisdate.getOrdinal() - today.getOrdinal());     //how many days out from time
                int playCount = slotParms.countOrig(user);                  //count number of times originated by user

                Calendar ca = new GregorianCalendar();             // get current date & time (Central Time)
                int cal_hourDay = ca.get(Calendar.HOUR_OF_DAY);    // 24 hr clock (0 - 23)
                int cal_min = ca.get(Calendar.MINUTE);
                int cal_time = (cal_hourDay * 100) + cal_min;     // get time in hhmm format
                cal_time = Utilities.adjustTime(con, cal_time);   // adjust the time

                parmClub parm = new parmClub(slotParms.root_activity_id, con);



                days = daysA[dayNum];
                advtime = advtimeA[dayNum];      // get the time value based on the day of the week

                if (ind > days || (ind == days && cal_time < advtime)) {        // if more than normal days in advance or =days in advance and before advance time
                                                                                // only allow member to book themselved and one other member or guest
                    if (slotParms.countRealPlayers() == 0) {
                        slotParms.lock_player[2] = true;                        // if no one currently in time lock 3 & 4 (and 5)
                        slotParms.lock_player[3] = true;
                        slotParms.lock_player[4] = true;
                    } else if (slotParms.countRealPlayers() == 1) {             // if 1 currently in time
                        if (playCount == 1) {                                   // if orig of 1 time lock 3 & 4 (and 5)  
                            slotParms.lock_player[2] = true;
                            slotParms.lock_player[3] = true;
                            slotParms.lock_player[4] = true;
                        } else {
                            slotParms.lock_player[3] = true;                    // else lock 4 (and 5)
                            slotParms.lock_player[4] = true;
                        }
                    } else if (slotParms.countRealPlayers() == 2) {             //if 2 currently in time                                                                            
                        if (playCount == 2) {                                   //lock 3 & 4 (and 5) 
                            slotParms.lock_player[2] = true;
                            slotParms.lock_player[3] = true;
                            slotParms.lock_player[4] = true;
                        } else if (playCount == 1) {                            //lock 4 (and 5)
                            slotParms.lock_player[3] = true;
                            slotParms.lock_player[4] = true;
                        }
                    } else if (slotParms.countRealPlayers() == 3) {             //if 3 currently in time
                        if (playCount == 2) {                                   //if orig of 2 times
                            slotParms.lock_player[3] = true;                    //lock 4 (and 5)
                            slotParms.lock_player[4] = true;
                        } else if (playCount == 1) {                            //(lock 5)
                            slotParms.lock_player[4] = true;
                        }
                    }
                }
            }
        }
        // Set default course name
        if (club.equals("congressional")) {
            course_disp = congressionalCustom.getFullCourseName(date, (int) dd, course);
        } else {
            course_disp = course;
        }
        
        // Load data from all times in group for slot page
        String[] playerA = {};     
        String[] pcwA = {};
        String[] userA = {};
        String[] customA = {};
        int[] p9A = {};
        int[] guest_idA = {};
        boolean[] lock_playerA = {};
        List<String> group_titles = new ArrayList<String>();
        long lock_time_remaining = verifySlot.getInUseTimeRemaining(slotParms.date, slotParms.time, slotParms.fb, slotParms.course, req);
        //boolean has_lock = slotParms.user_has_partial_lock;
        boolean has_lock = slotParms.userHasLock(req);
        //List<String> debug_time_locks = new ArrayList<String>();
        
        
        if(slotParms.group_slots.size() > 0){
            // We've loaded slots.  Use them
            for(parmSlot sParm : parmList){

                playerA = ArrayUtil.combine(playerA, sParm.getPlayerArray(5));
                pcwA = ArrayUtil.combine(pcwA, sParm.getCwArray(5));
                p9A = ArrayUtil.combine(p9A, sParm.getP9Array(5));
                userA = ArrayUtil.combine(userA, sParm.getUserArray(5));
                guest_idA = ArrayUtil.combine(guest_idA, sParm.getGuestIdArray(5));
                customA = ArrayUtil.combine(customA, sParm.getCustomDispArray(5));
                lock_playerA = ArrayUtil.combine(lock_playerA, sParm.lock_player);

                if(groupEdit){
                    group_titles.add(
                            timeUtil.get12HourTime(sParm.time)
                            +(showCourseOnTitle?" - "+sParm.course:"")
                            +(showFBOnTitle?" - "+Labels.fbText[fb]:"")
                            );
                }
                teecurr_ids.add(Utilities.encryptTTdata(Long.toString(sParm.teecurr_id)));
                // Get the minimum lock time from all times in the group.
                lock_time_remaining = Math.min(lock_time_remaining, verifySlot.getInUseTimeRemaining(sParm.date, sParm.time, sParm.fb, sParm.course, req));
                //if(!sParm.user_has_partial_lock){
                if(!sParm.userHasLock(req)){
                    has_lock = false;
                }
                slotPageParms.debug.put("group_owner_names_"+sParm.group_index, sParm.group_owner_names);
                slotPageParms.debug.put("group_owner_ids_"+sParm.group_index, sParm.group_owner_ids);
                //debug_time_locks.add("FL:"+sParm.user_has_lock+", PL:"+sParm.user_has_partial_lock+", SL:"+verifySlot.checkInSession(sParm.date, sParm.time, sParm.fb, sParm.course, session)+", SL2:"+verifySlot.checkInSession(sParm.date, sParm.time, sParm.fb, sParm.course, req));
            }
        } else {
            // Probably didn't make it past the configure slots section, set some defaults
            playerA = new String[5]; 
            Arrays.fill(playerA, "");
            pcwA = new String[5];
            Arrays.fill(pcwA, "");
            userA = new String[5];
            Arrays.fill(userA, "");
            customA = new String[5];
            Arrays.fill(customA, "");
            p9A = new int[5];
            guest_idA = new int[5];
            lock_playerA = new boolean[5];
        }
        
        if(!has_lock && slotPageParms.page_start_notifications.isEmpty()){
            // Somehow we got here without the user having a lock on all times in the group.  Can't let them go any further.
            slotPageParms.page_start_button_go_back = true;
            slotPageParms.page_start_title = "Tee Time Slot Busy";
            slotPageParms.page_start_notifications.add("Sorry, but this tee time slot is currently busy.");
            slotPageParms.page_start_notifications.add("Please select another time or try again later.");
            //for(String entry : debug_time_locks){
            //    slotPageParms.page_start_notifications.add("Debug: "+entry);
            //}
            
        }
        
        
        //
        //  Use custom_disp fileds for the Gift Pack option
        //
        int[] gift_packA = new int[customA.length];
        if (slotParms.use_gift_pack) {

            slotPageParms.show_gift_pack = true;
            slotPageParms.gift_pack_text = "";

            for(int i2 = 0; i2 < customA.length; i2++){
                if(customA[i2].equals("1")){
                    gift_packA[i2] = 1;
                }
            }
            
            slotPageParms.gift_pack_a = gift_packA;
            slotPageParms.slot_submit_map.put("gift_pack_%", "gift_pack_a");

        }
        
        
        //
        //   Check for Shotgun Event
        //
        if (!slotParms.event.equals("") && slotParms.event_type == 1) {       // if shotgun event during this time
                    
            int shotgunTime = Utilities.getEventTime(slotParms.event, con);   // get the actual time of the shotgun
            
            stime = timeUtil.get12HourTime(shotgunTime);                      // convert to string (i.e.  8:00 AM)
        }
        
        
        
        //
        //  Set user's name as first open player to be placed in name slot for them
        //
        //  First, check if first time here and user is already included in this slot.
        //
        if (!slotParms.hasUser(user) && !groupOwner && slotParms.group_slots.size() > 0) {
            Integer firstEmpty = slotParms.firstEmpty(slotParms.visible_players);
            if(firstEmpty != null){
                playerA[firstEmpty] = name;
                userA[firstEmpty] = user;
                pcwA[firstEmpty] = slotPageParms.pcw;
                guest_idA[firstEmpty] = 0;
            }
        }
        
        // Prefill guests for pganj if new request
        if (club.equals("pganj") && newreq) {
            Arrays.fill(playerA,1,4,"Guest");
            Arrays.fill(pcwA,1,4,"CRT");
        }

        // Finish configuration of the slot page
        slotPageParms.time_remaining = lock_time_remaining;
        slotPageParms.hide_notes = slotParms.hide;
        slotPageParms.show_member_tbd = slotParms.member_tbd;
        slotPageParms.show_tbd = slotParms.member_tbd; // why is this listed twice in parmsSlotPage??
        slotPageParms.edit_mode = (newreq == false);
        
        if (club.equals("congressional") && slotParms.visible_players == 2) {
            slotPageParms.default_fb_value = 1; // 0 or 1
            slotPageParms.set_default_fb_value = true;
            slotPageParms.lock_fb = true;
        }
        slotPageParms.allow_cancel = slotParms.allow_cancel;
        slotPageParms.show_member_select = slotParms.member_select;
        slotPageParms.show_guest_types = slotParms.guest_select;

        slotPageParms.player_count = playerA.length;
        slotPageParms.players_per_group = players_per_group;
        slotPageParms.visible_players_per_group = slotParms.max_visible_players;
        //slotPageParms.index = slotParms.index;

        slotPageParms.fb = fb;
        slotPageParms.slots = playerA.length/5;
        
        slotPageParms.group_titles = group_titles;

        slotPageParms.yy = (int) yy;
        slotPageParms.mm = (int) mm;
        slotPageParms.dd = (int) dd;

        slotPageParms.course = slotParms.course;
        slotPageParms.day = slotParms.day;
        slotPageParms.stime = stime;
        slotPageParms.course_disp = course_disp;
        slotPageParms.sdate = Integer.toString((int)slotParms.date);
        slotPageParms.date = (int)slotParms.date;
        slotPageParms.time = time;
        slotPageParms.id_list = teecurr_ids;
        if(teecurr_ids.size() > 0){
            slotPageParms.id_hash = Utilities.encryptTTdata(StringUtils.join(teecurr_ids,":"));
        }
        //slotPageParms.transport_legend = transport_legend;
        //slotPageParms.p5 = p5;
        slotPageParms.notes = slotParms.notes;
        slotPageParms.name = name;

        //slotPageParms.pcw = pcw; // Now done by slotPageParms.setParmCourse(req)

        slotPageParms.guest_id_a = guest_idA;
        slotPageParms.p9_a = p9A;
        

        slotPageParms.player_a = playerA;
        slotPageParms.user_a = userA;
        slotPageParms.pcw_a = pcwA;

        // Set players that cannot be editied on form
        slotPageParms.lock_player_a = lock_playerA;

        slotPageParms.setParmCourse(req); // For now, this must be before Common_slot.setTransportLegend and Common_slot.setTransportModes
        // Set tranport types
        Common_slot.setDefaultTransportTypes(slotPageParms);
        // Set transport legend
        Common_slot.setTransportLegend(slotPageParms, slotPageParms.course_parms, true);
        // Set transport modes
        Common_slot.setTransportModes(slotPageParms, slotPageParms.course_parms);
        // Set guest types
        Common_slot.setGuestTypes(con, slotPageParms, slotParms.club_parm, slotParms);

        // Define the fields we will include when submitting the form
        slotPageParms.slot_submit_map.put("teecurr_id%", "id_list");
        slotPageParms.slot_submit_map.put("id_hash", "id_hash");
        //slotPageParms.slot_submit_map.put("index", "index"); // Soon we will do away with this
        //slotPageParms.slot_submit_map.put("p5", "p5"); // Soon we will do away with this
        slotPageParms.slot_submit_map.put("hide", "hide_notes");
        slotPageParms.slot_submit_map.put("notes", "notes");
        slotPageParms.slot_submit_map.put("player%", "player_a");
        slotPageParms.slot_submit_map.put("user%", "user_a");
        slotPageParms.slot_submit_map.put("p9%", "p9_a");
        slotPageParms.slot_submit_map.put("p%cw", "pcw_a");
        slotPageParms.slot_submit_map.put("guest_id%", "guest_id_a");
        //slotPageParms.slot_submit_map.put("allow_cancel", "allow_cancel"); // we need to move away from passing this.  THis is a temprary hack.
        slotPageParms.slot_submit_map.put("custom_disp_%", "custom_disp_a");
        
//        if (club.equals("oakhillcc") && custom_int > 0) {
//            // Need more information on custom_int.  It seems wrong to pass this along.
//            // It's then trusted by proccessSlot from req, and could be overridden by the client!
//            // Seems like a security issue.  And why only pass it for oakhillcc?
//            // It probably should be calculated in parmSlot.setAccess?
//            //
//           slotPageParms.slot_submit_map.put("custom_int", "custom_int");
//        }

        /**************************************
         * New Skin Output
         **************************************/
        if (json_mode) {
            out.print(Common_slot.slotJson(slotPageParms));
        } else {
            Common_slot.displaySlotPage(out, slotPageParms, req, con);
        }

    }  // end of doPost
    
    
    

    // *********************************************************
    //  Process reservation request from Member_slot (HTML)
    //
    //  TODO: Move more of the common processing and validation to parmSlot.
    //  Idealy, this method should be very small.
    // *********************************************************
    private void verify(HttpServletRequest req, parmSlot slotParms, slotPostBack postback) {

        //ResultSet rs = null;        
        //HttpSession session = req.getSession(false);
 
        //
        //  Get this session's user name
        //
        String user = reqUtil.getSessionString(req, "user", "");
        String fullName = reqUtil.getSessionString(req, "name", "");
        String club = reqUtil.getSessionString(req, "club", "");
        String posType = reqUtil.getSessionString(req, "posType", "");
        String userMship = reqUtil.getSessionString(req, "mship", "");
        
        boolean rwd = Utilities.getRequestBoolean(req,ProcessConstants.RQA_RWD,false);

        Connection con = Connect.getCon(req);

        //
        // init all variables
        //
        int thisTime = 0;
        //int x = 0;
        //int xhrs = 0;
        int calYear = 0;
        int calMonth = 0;
        int calDay = 0;
        int calHr = 0;
        int calMin = 0;
        int week_of_year = 0;
        //int memNew = 0;
        //int memMod = 0;
        int xcount = 0;
        //int players = 0;
        //int gi = 0;
        int adv_time = 0;
        int custom_int = 0;
        int todayTime = 0;

        long adv_date = 0;
        long todayDate = 0;

        String player = "";
        String msg = "";

        List<String> tempList = new ArrayList<String>();
        
        //String memberName = "";
        //String p1 = "";
        String msgHdr = "";
        String msgBody = "";
        //String oldNotes = "";

        boolean error = false;
        boolean skipGuestRest = false;
        boolean oakskip = false;

        //
        //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
        //
        String lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided


        //
        //  Arrays to hold member & guest names to tie guests to members
        //
        //String[] memA = new String[5];     // members
        //String[] usergA = new String[5];   // guests' associated member (username)

        //
        //  parm block to hold the club parameters
        //
        //parmClub parm = new parmClub(0, con);

        //  parm block to hold the course parameters
        //parmCourse courseParms = new parmCourse();       // allocate a parm block

        /*
        if (club.equals("interlachen")) {               // Interlachen Gift Pack options
         * 
         * NOTE:  If you need to enable this (or any other) custom, it will need to be done another way.  
         * This method MUST NOT assign variables using req.getParameter(xxx).  These MUST be bassed in
         * parmSlot by the caller!
        
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


        //  retrieve course parameters
        //try {
        //    getParms.getTmodes(con, courseParms, slotParms.course);
        //} catch (Exception e) {
        //}


        long shortDate = slotParms.date - ((slotParms.date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)

        //
        //  Get the length of Notes (max length of 254 chars)
        //
        int notesL = 0;

        if (!slotParms.notes.isEmpty()) {
            notesL = slotParms.notes.length();       // get length of notes
        }

        //
        //  put parms in Parameter Object for portability
        //
        //int[] dateA = timeUtil.parseIntDate((int)slotParms.date);
        //slotParms.mm = dateA[timeUtil.MONTH];
        //slotParms.yy = dateA[timeUtil.YEAR];
        //slotParms.dd = dateA[timeUtil.DAY];
        //slotParms.club = club;    // name of club
        //slotParms.user = user;    // this user's username


        //
        //  Determine 'ind' value in case we came from Member_teelist or _searchmem (ind = 888 or 999 or 995)
        //
        //int indReal = getDaysBetween(slotParms.date);            // get # of days in between today and the date
        
        //
        //  We need the 'week of year' for customs below
        //
        Calendar cal = new GregorianCalendar();       // get todays date
        cal.set(Calendar.YEAR,slotParms.yy);                    // set year in cal for tee time date
        cal.set(Calendar.MONTH,slotParms.mm);                   // set month in cal
        cal.set(Calendar.DAY_OF_MONTH,slotParms.dd);            // set day in cal
        
        week_of_year = cal.get(Calendar.WEEK_OF_YEAR);  // get the tee time's week of the year

        //
        //  Get today's date
        //
        cal = new GregorianCalendar();                 // get todays date
        calYear = cal.get(Calendar.YEAR);
        calMonth = cal.get(Calendar.MONTH) + 1;
        calDay = cal.get(Calendar.DAY_OF_MONTH);
        //calDayWk = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07, 1 = Sun, 2 = Mon, etc. - 0 not used)
        calHr = cal.get(Calendar.HOUR_OF_DAY);         // 24 hr clock (0 - 23)
        calMin = cal.get(Calendar.MINUTE);

        //thisMonth = calMonth;                          // save this month

        todayDate = calYear * 10000;                      // create a date field of yyyymmdd
        todayDate = todayDate + (calMonth * 100);
        todayDate = todayDate + calDay;                    // date = yyyymmdd (for comparisons)

        todayTime = (calHr * 100) + calMin;               // hhmm (CT)
        
        boolean cancelTime = false;


            
            custom_int = slotParms.oldCustom_int;
            //memNew = slotParms.memNew;
            //    memMod = slotParms.memMod;
                //eventName = slotParms.event;
                //eventType = slotParms.event_type;
                //oldNotes = slotParms.oldNotes;

            if (slotParms.orig_by.equals("")) {    // if originator field still empty
                slotParms.orig_by = user;             // set this user as the originator
            }



        /*
        //
        //  If Congressional, save the 'days in adv' value in custom_int if this is a new tee time request (may not be needed any longer 3/08/12 BP)
        //
        if (club.equals("congressional")) {

            if (slotParms.oldPlayer1.equals("") && slotParms.oldPlayer2.equals("") && slotParms.oldPlayer3.equals("")
                    && slotParms.oldPlayer4.equals("") && slotParms.oldPlayer5.equals("")) {

                custom_int = ind;
            }
        }
        */
        slotParms.custom_int = custom_int;

        
        //boolean disallowCancel = !slotParms.allow_cancel; // set default
        /*
         * This is all now done in parmSlot
         * 
        // Merion - don't allow members to cancel tee times within 48 hrs of the tee time (Case #1234)
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

            if ((okDate > slotParms.date) || (okDate == slotParms.date && okTime > slotParms.time)) {

                disallowCancel = true;
            }

        }
        if (club.equals("loscoyotes")) {      // if Los Coyotes and after 3/16/08

            disallowCancel = true;
        }
        // If Naperville CC and time was booked as an advance guest time, do not allow members to cancel within the normal booking window (prevent them from gaming the system)
        if (club.equals("napervillecc") && slotParms.custom_int > 7 && slotParms.ind <= 7) {

            disallowCancel = true;
        }
         * 
         * 
         */
        
        //
        //  Check if we should be allow to edit this time
        //
        if(!slotParms.allow_edit){
            msgHdr = "Access Error";
            
            msgBody += "<p>Sorry, you do not have access to make changes to this tee time.</p>"
                    + "<p>Please contact the golf shop with any questions.</p>";
            
            postback.back_to_slotpage = false; // They shouldn't have been able to get here in the first place
                                               // Most likley a hack attempt, or access rules changed while 
                                               // they were on the slot page
            buildError(msgHdr, msgBody, postback);       // output the error message
            return;
        }

        //
        //  If request is to 'Cancel This Res', then clear all fields for this slot
        //
        //  First, make sure user is already on tee slot or originated it for unaccompanied guests
        //
        cancelTime = req.getParameter("remove") != null || (!slotParms.hasPlayers() && slotParms.hasOldPlayers());
        
        if (cancelTime) {

            if (slotParms.allow_cancel) {      // allow cancel if not Los Coyotes and not Merion (set above)
                
                /*
                String[] playerA = new String[5];
                Arrays.fill(playerA, "");

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
                    pstmt4.setLong(12, slotParms.date);
                    pstmt4.setInt(13, slotParms.time);
                    pstmt4.setInt(14, slotParms.fb);
                    pstmt4.setString(15, slotParms.course);
                    rs = pstmt4.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {
                        playerA = new String[]{rs.getString("player1"), rs.getString("player2"), rs.getString("player3"), rs.getString("player4"), rs.getString("player5")};
                    } else {

                        postback.title = "Procedure Error";
                        postback.notice_list.add("You cannot cancel a reservation unless you are part of that reservation.");
                        postback.continue_loop_on_error = false;
                        postback.prompt_close_continue = false;
                        postback.prompt_yes_no = false;
                        return;
                    }

                    pstmt4.close();

                } catch (Exception e4) {
                    dbError(e4, "Check user on tee time. ", postback);
                    return;
                }

                //
                //  See if we need to ask for a confirmation
                //
                players = 0;
                
                for(int i2 = 0; i2 < playerA.length; i2++){
                    if (!playerA[i2].equals("") && !playerA[i2].equalsIgnoreCase("x")) {       // if member name or guest
                        players++;
                    }
                }
                 * */
                if(!slotParms.orig_by.equalsIgnoreCase(user) &&
                    !ArrayUtil.containsIgnoreCase(slotParms.getOldUserArray(5), user)
                        && !slotParms.group_owner_names.contains(user.toLowerCase())){
                    // Shouldn't be able to get here.  If we do, something is wrong in slotParms.setAccess();
                    postback.title = "Procedure Error";
                    postback.notice_list.add("You cannot cancel a reservation unless you are part of that reservation.");
                    postback.continue_loop_on_error = false;
                    postback.prompt_close_continue = false;
                    postback.prompt_yes_no = false;
                    return;
                }
                
                // Check if this slot is in more than one group
                if(slotParms.group_ids.size() > 1 && !slotParms.isDescendantOrOrigOfAll(user)){
                    // A tee time could belong to more than one group due to the proshop dragging and dropping
                    // two lottery requests on to the same time, joining them.
                    // Canceling the time would remove it from both owner's group.
                    // If this tee time is part of more than one group, and the current user doesn't appear to have 
                    // originated everyone in the group, then don't allow them to cancel the reservation.
                    postback.title = "Unable to cancel reservation";
                    postback.notice_list.add("This reservation belongs to more than one group and cannot be canceled as a result.");
                    postback.notice_list.add("Please contact the Golf Shop with any questions.");
                    postback.continue_loop_on_error = false;
                    postback.prompt_close_continue = false;
                    postback.prompt_yes_no = false;
                    return;
                }
                
                //players = slotParms.countRealPlayers();
                
                //
                //  Now see if this action has been confirmed yet
                //
                if ((req.getParameter("ack_remove") != null) || (slotParms.countRealPlayers() < 2 && slotParms.group_slots.size() == 1)) {  // if remove has been confirmed or 1 player

                    String notes = slotParms.notes;

                    //
                    //  Oak Hill CC - track the Cancel if this tee time was an advance tee time
                    //
                    if (club.equals("oakhillcc") && custom_int > 0) {

                        verifyCustom.logOakhillAdvGst(slotParms.teecurr_id, slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                                       slotParms.player4, slotParms.player5, user, fullName, slotParms.notes, 3, con);
                    }

                    // Clear reservation fields
                    slotParms.fillPlayer("", 5);
                    slotParms.fillCw("", 5);
                    slotParms.fillUser("", 5);
                    slotParms.fillUserg("", 5);
                    slotParms.fillGuestId(0, 5);
                    slotParms.fillShow((short)0, 5);
                    slotParms.fillPos((short)0, 5);
                    slotParms.fillMnum("", 5);
                    slotParms.fillP9(0, 5);
                    slotParms.fillCustomDisp("", 5);
                    slotParms.fillTflag("", 5);
                    slotParms.fillOrig("", 5);

                    slotParms.notes = "";
                    slotParms.orig_by = "";
                    slotParms.custom_int = 0;
                    slotParms.emailCan = 1;      // send email notification for Cancel Request
                    slotParms.sendemail = 1;

                    //memMod++;      // increment number of mods for reports

                    //
                    //  if Oakmont, init the custom field used to track the month tee time was made
                    //
                    if (club.equals("oakmont")) {

                        custom_int = 0;
                    }

                    if (club.equals("longcove")) {        // if Long Cove fill in the tee time so only proshop can change
                        //
                        // Do Not fill with X's if between Sunday 6:30 PM and Tues 7:00 AM (ET) or 14 days in advance from 7:00am-7:30am
                        //
                        boolean lcFill = true;                     // default to fill

                        if (/*(calDayWk == 1 && todayTime > 1729) || calDayWk == 2 || (calDayWk == 3 && todayTime < 600) ||*/
                                (slotParms.ind == 14 && todayTime >= 600 && todayTime <= 630)) {
                            lcFill = false;
                        }

                        if (lcFill == true) {
                            slotParms.fillPlayer("X", 4);
                        }
                    }

                    if (club.equals("shadycanyongolfclub") && !notes.equals("")) {
                        slotParms.sendShadyCanyonNotesEmail = true;
                    }


                } else {    
                    // not acked yet - display confirmation page
                    postback.group_confirm_title = "Cancel Tee Time Confirmation";
                    
                    if(!postback.group_confirm_type.equals("cancel_time")){
                        postback.group_confirm_list.clear();
                    }
                    postback.group_confirm_type = "cancel_time";
                    
                    if(slotParms.group_slots.size() > 1){
                       postback.group_confirm_header = "This will:";
                       postback.group_confirm_list.add("- Remove ALL players from the <b>" + timeUtil.get12HourTime(slotParms.time)  +"</b> tee time, removing the tee time from this group. ");
                       if(postback.group_confirm_list.size() == slotParms.group_slots.size()){
                           postback.group_confirm_list.clear();
                           postback.group_confirm_header = "";
                           postback.group_confirm_list.add("This will remove ALL players from every tee time in this group. ");
                       }
                    } else if(slotParms.group_ids.size() > 0 && slotParms.group_owner_names.contains(user.toLowerCase())) {
                        // We're editing/canceling a single time that is part of a group, and I'm the group owner of one of the groups
                        Long teecurr_group_id = slotParms.group_ids.get(slotParms.group_owner_names.indexOf(user.toLowerCase()));
                        List<teeCurrGroupDetail> groupDetails = reservationUtil.getTeeCurrGroupDetailsByGroupId(teecurr_group_id, con);
                        postback.group_confirm_list.add("This will remove ALL players from the tee time, and will remove this time from a group of "+groupDetails.size()+".");
                    } else {
                        // Not part of a group, or I'm not the group owner
                        postback.group_confirm_list.add("This will remove ALL players from the tee time. ");
                    }
                    if(req.getParameter("remove") != null){
                        postback.group_confirm_callback_map.put("remove","yes");
                    }
                    postback.group_confirm_footer ="If this is what you want to do, then click on \"Continue\" below."
                            + "<br><br>"
                            + "If you only want to remove yourself, or a portion of the players, "
                            + "click on \"Close\" below. Then use the \"erase\" and \"Submit\" buttons to "
                            + "remove only those players you wish to remove.";
                    postback.group_confirm_callback_map.put("ack_remove","yes");
                    
                    postback.group_confirm_close_continue = true;
                    postback.continue_loop_on_error = true;

                    return;    // wait for acknowledgement
                }

            } else {

                //
                //  Merion - do not allow members to cancel a tee time within 48 hours of time.
                //  Long Cove - prior to 3/17/2008 - do not allow members to cancel
                //  Los Coyotes - do not allow members to cancel
                //

                postback.title = "Member Tee Slot Page";
                if(slotParms.group_slots.size() > 1 && req.getParameter("remove") == null){
                    // We're here because all names have been removed from the tee time of a group.
                    postback.notice_list.add("Sorry, you must call the golf shop to cancel the "+timeUtil.get12HourTime(slotParms.time) +" tee time.");
                } else {
                    postback.notice_list.add("Sorry, you must call the golf shop to cancel this tee time.");
                }
                
                if (club.equals("merion")) {
                    postback.message_list.add("(610) 642-5600");
                }
                postback.continue_loop_on_error = false;
                postback.prompt_close_continue = false;
                postback.prompt_yes_no = false;

                return;
            }                // end of IF Long Cove and Cancel processing (remove)

        } else {        //  not a 'Cancel Tee Time' request

            //
            //  Normal request -
            //
            //   Get the guest names and other parms specified for this club
            //
            //parmClub parm = slotParms.club_parm;
            //parmCourse courseParms = slotParms.course_parm;
            //try {
                
                //parm.club = club;                   // set club name
                //parm.course = slotParms.course;     // and course

                //getClub.getParms(con, parm);        // get the club parms

                //x = slotParms.club_parm.x;
                //xhrs = parm.xhrs;                      // save for later tests
                //slotParms.rnds = slotParms.club_parm.rnds;
                //slotParms.hrsbtwn = slotParms.club_parm.hrsbtwn;
            //} catch (Exception ignore) {
            //}
            

            //
            //  if Forest Highlands, do not allow any X's (only pros can use them)
            //  (this is done in parmSlot setAccessOnGroup() now)
                /*
            if (club.equals("foresthighlands")) {

                x = 0;
            }
                 * 
                 */


            //
            //  Shift players up if any empty spots
            //
            verifySlot.shiftUp(slotParms);

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
 
                    msgBody = "<p><b>" + slotParms.gplayer + "</b> appears to have been manually entered or "
                            + "modified after selecting a different guest from the Guest Selection window.</p>"
                            + "<p>Since this guest type uses the Guest Tracking feature, please click 'erase' "
                            + "next to the current guest's name, then click the desired guest type from the Guest "
                            + " Types list, and finally select a guest from the displayed guest selection window.</p>";


                buildError(msgHdr, msgBody, postback);       // output the error message
                return;
            }

            //
            //  Reject if any player was a guest type that is not allowed for members
            //
            if (!slotParms.gplayer.equals("")) {

                msgHdr = "Unspecified Guests";

                if (slotParms.hit3 == true) {                      // if error was name not specified
                    msgBody = "<p>You must specify the name of your guest(s).</p>"
                            + "<p><b>" + slotParms.gplayer + "</b> does not include a valid name (must be at least first & last names).</p>"
                            + "<p>To specify the name, click in the player box where the guest is specified, "
                            + "move the cursor (use the arrow keys or mouse) to the end of the guest type value, "
                            + "use the space bar to enter a space and then type the guest's name.</p>";
                } else {
                    msgBody = "<p><b>" + slotParms.gplayer + "</b> specifies a Guest Type that is not allowed for member use.</p>";
                }

                msgBody += "<p>If the Golf Shop had originally entered this guest, then it <b>must not</b> be changed.</p>"
                        + "<p>Please correct this and try again.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message
                return;
            }

            error = false;

            if (slotParms.club_parm.unacompGuest == 0) {      // if unaccompanied guests not supported

                //
                //  Make sure at least 1 player contains a member
                //
                if (((slotParms.player1.equals("")) || (slotParms.player1.equalsIgnoreCase("x")) || (!slotParms.g1.equals("")))
                        && ((slotParms.player2.equals("")) || (slotParms.player2.equalsIgnoreCase("x")) || (!slotParms.g2.equals("")))
                        && ((slotParms.player3.equals("")) || (slotParms.player3.equalsIgnoreCase("x")) || (!slotParms.g3.equals("")))
                        && ((slotParms.player4.equals("")) || (slotParms.player4.equalsIgnoreCase("x")) || (!slotParms.g4.equals("")))
                        && ((slotParms.player5.equals("")) || (slotParms.player5.equalsIgnoreCase("x")) || (!slotParms.g5.equals("")))) {

                    msgHdr = "Member name not found";

                    if(rwd){
                        msgBody = "<p>You must specify at least one member in the request.</p>"
                            + "<p>Please correct this and try again.</p>";
                    } else {
                        msgBody = "<p>You must specify at least one member in the request.</p>"
                            + "<p>Member names must be specified exactly as they exist in the system.</p>"
                            + "<p>Please correct this and try again.</p>";
                    }
                    

                    buildError(msgHdr, msgBody, postback);       // output the error message
                    return;
                }
                if(slotParms.player1.equalsIgnoreCase("x") /* && !slotParms.oldPlayer1.equalsIgnoreCase(slotParms.player1) */){
                    msgHdr = "Data Entry Error";

                    msgBody = "The first player position of the "+timeUtil.get12HourTime(slotParms.time) +" tee time cannot be \"X\".";

                    buildError(msgHdr, msgBody, postback);       // output the error message
                    return;
                }

            } else {           // guests are ok

                //
                //  Make sure at least 1 player contains a player name
                //
                if (!slotParms.hasRealPlayers()) {

                    msgHdr = "Data Entry Error";

                    msgBody = "<p>Required field has not been completed or is invalid.</p>"
                            + "<p>At least one player field must contain a name.</p>"
                            + "<p>If you want to cancel the reservation, use the 'Cancel Tee Time' button under the player fields.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message
                    return;
                }
            }

            //
            //  Check the number of X's against max specified by proshop
            //
            xcount = 0;
            for(int i = 0; i < 5; i++){
                if(slotParms.getPlayer(i).equalsIgnoreCase("x")){
                    xcount++;
                }
            }

            if (xcount > slotParms.club_parm.x) {

                msgHdr = "Data Entry Error";

                msgBody = "<p>The number of X's requested (" + xcount + ") exceeds the number allowed (" + slotParms.club_parm.x + ").</p>"
                        + "<p>Please try again.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message
                return;
            }

            //
            //  At least 1 Player is present - Make sure a C/W was specified for all players
            //
            tempList.clear();
            for(int i = 0; i < 5; i++){
                player = slotParms.getPlayer(i);
                if(!player.isEmpty() && !player.equalsIgnoreCase("x") && slotParms.getCw(i).isEmpty()){
                    tempList.add(player);
                }
            }
            if(tempList.size() > 0){
                msgHdr = "Invalid Mode of Transportation";

                msgBody = "<p><b>Mode of Transportation</b> not selected for <b>" + htmlUtil.joinList(tempList) +"</b>.</p>"
                        + "<p>You must specify a Mode of Transportation for all players.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message
                return;
            }
            /*
            if (((!slotParms.player1.equals("")) && (!slotParms.player1.equalsIgnoreCase("x")) && (slotParms.p1cw.equals("")))
                    || ((!slotParms.player2.equals("")) && (!slotParms.player2.equalsIgnoreCase("x")) && (slotParms.p2cw.equals("")))
                    || ((!slotParms.player3.equals("")) && (!slotParms.player3.equalsIgnoreCase("x")) && (slotParms.p3cw.equals("")))
                    || ((!slotParms.player4.equals("")) && (!slotParms.player4.equalsIgnoreCase("x")) && (slotParms.p4cw.equals("")))
                    || ((!slotParms.player5.equals("")) && (!slotParms.player5.equalsIgnoreCase("x")) && (slotParms.p5cw.equals("")))) {

                msgHdr = "Invalid Mode of Transportation";

                msgBody = "<p>Required field has not been completed or is invalid.</p>"
                        + "<p>You must specify a Mode of Transportation for all players.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message
                return;
            }
            */
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

                    msgBody = "<p>Sorry, you are not allowed to reserve tee times with only one player.</p>"
                            + "<p>Please add more players or contact the golf shop for assistance.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message
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

                    msgBody = "<p>Sorry, you are not allowed to reserve tee times more than one day in advance for this time of day.</p>"
                            + "<p>Please choose another time of day or contact the golf shop for assistance.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message
                    return;
                }

                if ((slotParms.day.equals("Sunday") || (slotParms.date == ProcessConstants.memDay || slotParms.date == ProcessConstants.july4 || slotParms.date == ProcessConstants.july4b || slotParms.date == ProcessConstants.laborDay)) && slotParms.time > 629 && slotParms.time < 858) {

                    msgHdr = "Invalid Days in Advance";

                    msgBody = "<p>Sorry, you are not allowed to reserve tee times more than one day in advance for this time of day.</p>"
                            + "<p>Please choose another time of day or contact the golf shop for assistance.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message
                    return;
                }
            } // end Oahu custom


            if (club.equals("johnsisland")) {        // if Johns Island

                //
                //  Must be at least 2 players between 8:40 and 1:50 (members or guests)
                //
                if (slotParms.time > 839 && slotParms.time < 1351) {

                    if (slotParms.countRealPlayers() < 2) {       // if less than 2 players

                        msgHdr = "Invalid Number of Players";

                        msgBody = "<p>Sorry, you are not allowed to reserve tee times with less than 2 players at this time.</p>"
                                + "<p>All tee time requests from 8:40 AM to 1:50 PM must include at least 2 members and/or guests.</p>"
                                + "<p>Please add more players or contact the golf shop for assistance.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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
//                            msgBody = "<p>Sorry, you are not allowed to reserve tee times with less than 3 players at this time.</p>"
//                                    + "<p>All tee time requests from 10:30 AM to 2:00 PM on weekends and holidays must include at least 3 players.</p>"
//                                    + "<p>Please add more players or contact the golf shop for assistance.</p>";
//
//                            buildError(msgHdr, msgBody, postback);       // output the error message
//
//                            
//                            return;
//                        }
//                    }
//                }
//            }  // end if Brooklawn CC

            //
            //  Make sure there are no duplicate names
            // 
            Set<String> playerTest = new HashSet<String>();
            Set<Integer> guestDbTest = new HashSet<Integer>();
            int guestIdTest;
            for(int i = 0; i < 5; i++){
                player = slotParms.getPlayer(i);
                if(!player.isEmpty() && !player.equalsIgnoreCase("x")){
                    // Member, or guest?
                    if(slotParms.getG(i).isEmpty()){
                        // looks like a member
                        if(!playerTest.contains(player)){
                            // First time we've seen this member so far.
                            playerTest.add(player);
                        } else {
                            // Duplicate member detected.
                            tempList.add(player);
                        }
                    } else {
                        // A guest.  See if they are from the guest DB
                        guestIdTest = slotParms.getGuestId(i);
                        if(guestIdTest > 0){
                            if(!guestDbTest.contains(guestIdTest)){
                                guestDbTest.add(guestIdTest);
                            } else {
                                // Duplicate guest from guest db detected.
                                tempList.add(player);
                            }
                        }
                    }
                }
            }
            if(tempList.size() > 0){
                // Client side validation should stop this from ever happening.  Probably should be recorded as a hack attempt?
                msgHdr = "Duplicate Players Found";

                msgBody = "<p><b>" + htmlUtil.joinList(tempList) + "</b> "+(tempList.size()>1?"were":"was")
                        + " specified more than once.</p>"
                        + "<p>Please correct this and try again.</p>";
                buildError(msgHdr, msgBody, postback);       // output the error message
                return;
            }

            /*
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

                msgBody = "<p><b>" + player + "</b> was specified more than once.</p>"
                        + "<p>Please correct this and try again.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message

                
                return;
            }
             */

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

                msgBody = "<p>Sorry, a name you entered is not valid.</p>"
                        + "<p>You entered: '" + htmlUtil.joinList(Arrays.asList(slotParms.getPlayerArray(5))) + "'</p>"
                        + "<p>Member names must be entered exactly as they exist in the system (so we can identify them).</p>"
                        + "<p>Please use the Partner List or Member List on the right side of the page to select the member names.</p>"
                        + "<p>Simply <b>click on the desired name</b> in the list to add the member to the tee time.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message

                
                return;
            }


            //
            //  Get the usernames, membership types, & hndcp's for players if matching name found
            //
            try {

                verifySlot.getUsers(slotParms, con);

            } catch (Exception e1) {

                msg = "Check guest names. ";

                dbError(e1, msg, postback);                        // reject
                return;
            }
            
            /*
            //  Check each player's mship to see if it has permission to be a part of reservations for this activity (mship has an entry in mship5 for this activity)
            if (verifySlot.checkMemberAccess(slotParms, con)) {          // if problem with player mship access

                msgHdr = "Membership Restricted";

                msgBody = "<p>Sorry, <b>" +slotParms.player+ "</b> is not allowed to be a part of Golf reservations due to membership privileges.</p>"
                        + "<p>Please remove this player and submit the reservation again.</p>"
                        + "<p><BR></p>";

                buildError(msgHdr, msgBody, postback);       // output the error message

                
                return;
            }*/
            
            
            //
            //  No players are using Pro-Only transportation modes without authorization
            //
            if (slotParms.course_parm.hasProOnlyTmodes && !verifySlot.checkProOnlyMOT(slotParms, slotParms.course_parm, con) && !club.equals("thedeuce") && !club.equals("thenationalgolfclub")) {

                msgHdr = "Access Error";

                msgBody = "<p><b>'" + slotParms.player + "'</b> is not authorized to use that mode of transportation.</p>"
                        + "<p>Please select another mode of transportation.</p>"
                        + "<p>Contact your club if you require assistance with restricted modes of transportation.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message

                
                return;
            }



            //
            //  Save the members' usernames for guest association
            //
            //memA[0] = slotParms.user1;
            //memA[1] = slotParms.user2;
            //memA[2] = slotParms.user3;
            //memA[3] = slotParms.user4;
            //memA[4] = slotParms.user5;
            //
            //  Check if any of the names are invalid.
            //
            List<String> rejectList = new ArrayList<String>();
            int invalTest;
            for(int i = 0; i < 5; i++){
                invalTest = slotParms.getInval(i);
                if(invalTest > 0){
                    if (invalTest == 2) {        
                        // if incomplete member record
                        tempList.add(slotParms.getPlayer(i));
                    } else {
                        // unknown member
                        rejectList.add(slotParms.getPlayer(i));
                    }
                }
            }
            if(tempList.size() > 0 || rejectList.size() > 0){
                    // Client side validation should stop us from ever getting here.  Probably a hack attempt.
                    msgHdr = "Invalid Member";
                    StringBuilder sbmsg = new StringBuilder();
                    msgBody = "";
                    if(tempList.size() > 0){
                        sbmsg.append("<p>The player");
                        sbmsg.append(htmlUtil.addS(tempList.size()));
                        sbmsg.append(" <b>");
                        sbmsg.append(htmlUtil.joinList(tempList));
                        sbmsg.append("</b> ");
                        sbmsg.append(tempList.size()>1?"have ":"has an ");
                        sbmsg.append("incomplete member record");
                        sbmsg.append(htmlUtil.addS(tempList.size()));
                        sbmsg.append(" and cannot be included at this time.</p><p>Please inform your golf professional of this error.</p>");
                    }
                    if(rejectList.size() > 0){
                        sbmsg.append("<p>The player");
                        sbmsg.append(htmlUtil.addS(rejectList.size()));
                        sbmsg.append(" <b>");
                        sbmsg.append(htmlUtil.joinList(rejectList));
                        sbmsg.append("</b> ");
                        sbmsg.append(rejectList.size()>1?"are not recognized as valid member names.":"is not recognized as a valid member name.</p>");
                        sbmsg.append("<p>Member names must be entered exactly as they exist in the system.</p>");
                    }
                    
                    sbmsg.append("<p>You will have to remove ");
                    sbmsg.append(tempList.size()+rejectList.size()>1?"these names ":"this name ");
                    sbmsg.append("from your tee time request.</p>");

                    buildError(msgHdr, sbmsg.toString(), postback);       // output the error message

                return;
            }
            
            /*
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


                if (invalNum == 2) {        // if incomplete member record

                    msgHdr = "Incomplete Member Record";

                    msgBody = "<p>Sorry, a member you entered has an imcomplete member record and cannot be included at this time.</p>"
                            + "<p>Member Name:&nbsp;&nbsp;&nbsp;'" + p1 + "'</p>"
                            + "<p>Please inform your golf professional of this error.</p>"
                            + "<p>You will have to remove this name from your tee time request.</p>";

                } else {

                    msgHdr = "Invalid Member Name Received";

                    msgBody = "<p>Sorry, a name you entered is not recognized as a valid member.</p>"
                            + "<p>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "'</p>"
                            + "<p>Member names must be entered exactly as they exist in the system (so we can identify them).</p>"
                            + "<p>Please use the Partner List or Member List on the right side of the page to select the member names.</p>"
                            + "<p>Simply <b>click on the desired name</b> in the list to add the member to the tee time.</p>";
                }

                buildError(msgHdr, msgBody, postback);       // output the error message

                
                return;
            }
             */

            //
            //  If any X's requested, make sure its not too late to request an X
            //
            //    from above - x = max x's allowed, xcount = # of x's requested, xhrs = # hrs in advance to remove x's
            //
            if (xcount > 0) {       // if any x's requested in tee time

                if (slotParms.club_parm.xhrs != 0) {     // if club wants to remove X's

                    //
                    //  Set date/time values to be used to check for X's in tee sheet
                    //
                    //  Get today's date and then go up by 'xhrs' hours
                    //
                    cal = new GregorianCalendar();       // get todays date

                    cal.add(Calendar.HOUR_OF_DAY, slotParms.club_parm.xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

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

                    adv_time = Utilities.adjustTime(con, adv_time);
                    
                    //
                    //  Compare the tee time's date/time to the X deadline
                    //
                    if ((slotParms.date < adv_date) || ((slotParms.date == adv_date) && (slotParms.time <= adv_time))) {

                        msgHdr = "Invalid use of the X option.";

                        msgBody = "<p>Sorry, 'X' is not allowed for this tee time.</p>"
                                + "<p>It is not far enough in advance to reserve a player position with an X.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                buildError(msgHdr, msgBody, postback);       // output the error message

                
                return;
            }

            //
            //  MOVE THE FOLLOWING CUSTOMS TO USE ABOVE PROCESS !!!!!!!!!!!!!!
            //

            if (club.equals("westchester")) {

                int westPlayers = verifySlot.checkWestPlayers(slotParms);

                if (westPlayers > 0 && westPlayers < 3) {    // if w/e or holiday and 1 or 2 players

                    msgHdr = "Insufficient Number of Players";

                    msgBody = "<p>Sorry, you have not specified enough players for this day and time.</p>"
                            + "<p>All Tee Times must include at least 3 players on Weekends & Holidays before 2 PM.</p>"
                            + "<p>Please add more players or select a different time of the day.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }

                error = false;

                if (slotParms.course.equals("South")) {

                    error = verifySlot.checkWestDependents(slotParms);

                    if (error == true) {    // if w/e or holiday and dependend w/o adult

                        msgHdr = "Dependent Without An Adult";

                        msgBody = "<p>Sorry, dependents must be accompanied by an adult for this day and time.</p>"
                                + "<p>All Tee Times must include at least 1 Adult on the South Course on Weekends & Holidays between 10:30 and 1:45.</p>"
                                + "<p>Please add more players or select a different time of the day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }
                }
            }

            if (club.equals("pinehills")) {

                error = verifySlot.checkPineDependents(slotParms);     // check for Junior w/o an Adult

                if (error == true) {

                    msgHdr = "Junior Without An Adult";

                    msgBody = "<p>Sorry, juniors must be accompanied by an adult for this day and time.</p>"
                            + "<p>All Tee Times with a Junior Over 14 must include at least 1 Adult during times specified by the golf shop.</p>"
                            + "<p>Please add more players or select a different time of the day.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }
            }

            if (club.equals("stanwichclub")) {

                error = verifySlot.checkStanwichDependents(slotParms);     // check for Dependent w/o an Adult

                if (error == true) {

                    msgHdr = "Dependent Without An Adult";

                    msgBody = "<p>Sorry, dependents must be accompanied by an adult for this day and time.</p>"
                            + "<p>All Tee Times with a Dependent must include at least 1 Adult during times specified by the golf shop.</p>"
                            + "<p>Please add an adult player or select a different time of the day.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }
            }

            if (club.equals("castlepines")) {

                error = verifySlot.checkCastleDependents(slotParms);     // check for Dependent w/o an Adult

                if (error == true) {

                    msgHdr = "Dependent Without An Adult";

                    msgBody = "<p>Sorry, dependents must be accompanied by an adult at all times.</p>"
                            + "<p>Please add an adult player or return to the tee sheet.</p>"
                            + "<p>If you have any questions, please contact your golf shop staff.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }
            }

            if (club.equals("awbreyglen")) {

                error = verifyCustom.checkAwbreyDependents(slotParms);     // check for Dependent w/o an Adult

                if (error == true) {

                    msgHdr = "Junior Without An Adult";

                    msgBody = "<p>Sorry, Juniors must be accompanied by an adult at all times.</p>"
                            + "<p>Juniors 12 and Over must be accompanied by an adult before Noon.</p>"
                            + "<p>Please add an adult player or return to the tee sheet.</p>"
                            + "<p>If you have any questions, please contact your golf shop staff.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }
            }

            if (club.equals("elniguelcc") && slotParms.day.equals("Sunday") && slotParms.time > 659 && slotParms.time < 1100 && slotParms.fb == 1) {   // if Sunday, 7 - 11 AM and Back Tee

                error = verifyCustom.checkElNiguelDependents(slotParms);     // check for Dependent w/o an Adult

                if (error == true) {

                    msgHdr = "Dependent Without An Adult";

                    msgBody = "<p>Sorry, dependents must be accompanied by an adult between 7 AM and 11 AM on the back tee.</p>"
                            + "<p>Please add an adult player or return to the tee sheet.</p>"
                            + "<p>If you have any questions, please contact your golf shop staff.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                        msgBody = "<p>Sorry, junior members must be accompanied by an adult at all times.</p>"
                                + "<p>Please add an adult player or return to the tee sheet.</p>"
                                + "<p>If you have any questions, please contact your golf shop staff.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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
//                    msgBody = "<p>Sorry, Primary Females must be accompanied by a Primary Male between 8 AM and 12:50 PM on Sundays.</p>"
//                            + "<p>Please add a Primary Male member or return to the tee sheet.</p>"
//                            + "<p>If you have any questions, please contact your golf shop staff.</p>";
//
//                    buildError(msgHdr, msgBody, postback);       // output the error message
//
//                    
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
            out.println("<p><BR><H3>Dependent Without An Adult</H3></p>");
            out.println("<p>Sorry, dependent members must be accompanied by an adult at all times.</p>");
            out.println("<p>Please add an adult player or return to the tee sheet.</p>");
            out.println("<p>If you have any questions, please contact your golf shop staff.</p>");
            out.println("<p><BR></p>");
            
            
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
            
            msgBody = "<p>Sorry, you have not specified enough players for this day and time.</p>" +
            "<p>All Tee Times must include at least 3 players on Wed & Sat before 2 PM, and Sun before 1 PM.</p>" +
            "<p>Please add more players or select a different time of the day.</p>";
            
            buildError(msgHdr, msgBody, postback);       // output the error message
            
            
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

                    msgBody = "<p>Sorry, " + slotParms.player + " is a " + slotParms.mship + " member and has exceeded the<BR></p>"
                            + "maximum number of tee times allowed for this " + slotParms.period + ".";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                if (slotParms.countRealPlayers() < 2) {       // if less than 2 players - changed on 3-18-08 per Larry

                    msgHdr = "Invalid Number of Players";

                    msgBody = "<p>Sorry, you are not allowed to reserve tee times with less than two named players.</p>"
                            + "<p>Please add another member or guest, or contact the golf shop for assistance.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }

                //
                //  Los Coyotes - Check for Secondary member with Primary more than 3 days in advance
                //
                if (slotParms.ind > 3) {

                    error = verifyCustom.checkLCSpouses(slotParms);

                    if (error == true) {

                        msgHdr = "Secondary Without Primary";

                        msgBody = "<p>Sorry, Secondary members must be accompanied by the Primary family member<BR>when the group is scheduled more than 3 days in advance.</p>"
                                + "<p>Please add the Primary member or return to the tee sheet.</p>"
                                + "<p>If you have any questions, please contact your golf shop staff.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }
                }

                //
                //  Los Coyotes - User must be part of the tee time (case 1647)
                //
                if (!user.equalsIgnoreCase(slotParms.user1) && !user.equalsIgnoreCase(slotParms.user2) && !user.equalsIgnoreCase(slotParms.user3)
                        && !user.equalsIgnoreCase(slotParms.user4) && !user.equalsIgnoreCase(slotParms.user5)) {

                    msgHdr = "Invalid Request";

                    msgBody = "<p>Sorry, you cannot create or modify a tee time request<BR>unless you are a part of the tee time.</p>"
                            + "<p>Please add yourself or return to the tee sheet.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }

            }       // end of IF Los Coyotes

            // If Greenwich and less then 3 players on the listed dates/times then reject  (Case #1123)
            if (club.equals("greenwich")) {

                error = verifySlot.checkGreenwichMinPlayers(slotParms, xcount);

                if (error == true) {

                    msgHdr = "Invalid Number of Players";

                    msgBody = "<p>Sorry, you are not allowed to reserve tee times with less than three players.</p>"
                            + "<p>Please add another member or guest, or contact the golf shop for assistance.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                        msgBody = "<p>Sorry, you are not allowed to book a tee time this far in advance on weekends and holidays before 10:00 AM.</p>"
                                + "<p>These times can be reserved up to 3 days in advance.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;

                    } else {

                        if (slotParms.day.equals("Thursday") && time < 1500 && slotParms.guests > 0
                                && shortDate > 521 && shortDate < 905) {                                          // No guests allowed on Thurs before 3:00 PM

                            msgHdr = "Invalid Tee Time Request";

                            msgBody = "<p>Sorry, guests are not allowed before 3:00 PM on Thursdays.</p>"
                                    + "<p>Please remove the guest(s) or select another time.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
                            return;

                        } else {

                            //
                            //  Must be 1 member and 3 guests (only)
                            //
                            if (slotParms.guests < 3 || slotParms.members != 1) {

                                msgHdr = "Invalid Guest Time Request";

                                msgBody = "<p>Sorry, only guest times are accepted at this time.</p>"
                                        + "<p>Your request must contain 1 member and 3 guests.</p>";

                                buildError(msgHdr, msgBody, postback);       // output the error message

                                
                                return;

                            } else {

                                //
                                //   Now check to see if there are already 2 guest times this hour.
                                //
                                error = verifyCustom.checkMiniGuestTimes(slotParms, con);

                                if (error == true) {

                                    msgHdr = "Invalid Guest Time Request";

                                    msgBody = "<p>Sorry, there are already 2 guest times scheduled this hour.</p>"
                                            + "<p>Please select a different time of the day.</p>";

                                    buildError(msgHdr, msgBody, postback);       // output the error message

                                    
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

                            msgBody = "<p>Sorry, there are already 2 guest times scheduled this hour.</p>"
                                    + "<p>Please select a different time of the day.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
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

                        dbError(e5, msg, postback); 
                        return;
                    }

                    if (error == true) {      // a member exceed the max allowed tee times per month

                        msgHdr = "Number of Guests Exceeded Limit";

                        msgBody = "<p>Sorry, the maximum number of guests allowed for the<BR>time you are requesting is " + slotParms.grest_num + " per " + slotParms.grest_per + ".</p>"
                                + "<p>Guest Restriction = " + slotParms.rest_name + "<p></p></p>";
                        
                        if (club.equals("ridgecc")) {
                            msgBody += "Please contact the golf shop at (773) 238-9405 for assistance with this tee time.";
                        }

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                                dbError(e5, msg, postback); 
                                return;
                            }

                            if (error == true) {      // a member exceed the max allowed tee times per month

                                msgHdr = "Number of Family Guest Tee Times Exceeded Limit For The Day";

                                msgBody = "<p>Sorry, there are already 2 tee times with family guests</p>"
                                        + "<p>scheduled for today.  You are only allowed one family guest per member.</p>";

                                buildError(msgHdr, msgBody, postback);       // output the error message

                                
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

                            msgBody = "<p>Sorry, you must provide the full name of your guest(s).<BR>Please enter a space followed by the guest's name immediately after the guest type</p>"
                                    + "<p>in the player field.  Click your mouse in the player field, move the cursor</p>"
                                    + "<p>to the end of the guest type, hit the space bar and then type the full name.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
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

                        msgBody = "<p>Sorry, but there are already guests scheduled during this hour.<BR>No more than 9 guests are allowed per hour.  This request would exceed that total.</p>"
                                + "<p>Please remove one or more guests, or try another time of the day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, but there are already 12 guests scheduled today.<BR>No more than 12 guests are allowed before Noon.  This request would exceed that total.</p>"
                                + "<p>Please remove one or more guests, or try another time of the day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }
                }      // end of if Riverside G&CC

                //
                //  If The Patterson Club & Sat/Sun between 7-9:30, then check if more than 12 guests total
                //
                if (club.equals("pattersonclub")
                        && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.date == ProcessConstants.memDay || slotParms.date == ProcessConstants.july4 || slotParms.date == ProcessConstants.laborDay)
                        && slotParms.time > 659 && slotParms.time < 931) {

                    error = verifyCustom.checkPattersonGuests(slotParms, con);

                    if (error == true) {      // more than 12 guests before noon

                        msgHdr = "Maximum Number of Guests Exceeded";

                        msgBody = "<p>Sorry, but there are already 12 guests scheduled.<BR>No more than 12 guests are allowed between 7:00am and 9:30am on weekends and holidays.<BR>This request would exceed that total.</p>"
                                + "<p>Please remove one or more guests, or try another time of the day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, but there are already 12 guests scheduled today.<BR>No more than 12 guests are allowed during the selected time period.<BR>This request would exceed that total.</p>"
                                + "<p>Please remove one or more guests, or try another time of the day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, but the maximum number of guest times are already scheduled during this hour.</p>"
                                + "<p>Please try another time of the day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, but the guest type 'Cert Jr Guest' can only follow a Certified Dependent</p>"
                                + "<p>and a dependent may only have one guest.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, but guests are not allowed during this time.  This is a member-only time.</p>"
                                + "<p>Please try another time of the day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, but you are not allowed to request a time with guests when an adult is not included.</p>"
                                + "<p>Please remove the guests or add an adult.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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
                   
                    if ((slotParms.day.equals("Tuesday") 
                            && ((slotParms.time >= 730 && slotParms.time <= 1000 
                                && ((week_of_year % 2 == 1 && slotParms.course.equals("Lower") && slotParms.date >= 20150421 && slotParms.date <= 20151006) 
                                 || (week_of_year % 2 == 0 && slotParms.course.equals("Upper") && slotParms.date >= 20150428 && slotParms.date <= 20150929))) 
                            || (slotParms.time >= 800 && slotParms.time <= 900 
                                && ((week_of_year % 2 == 0 && slotParms.course.equals("Lower") && slotParms.date >= 20150428 && slotParms.date <= 20150929) 
                                 || (week_of_year % 2 == 1 && slotParms.course.equals("Upper") && slotParms.date >= 20150421 && slotParms.date <= 20151006)))))
                     || (slotParms.day.equals("Thursday") && slotParms.course.equals("Upper") && (slotParms.date == 20150528 || slotParms.date == 20151015) && slotParms.time >= 730 && slotParms.time <= 1000)) {

                         baltusrolSkip = true;    // skip guest check if Ladies Day time
                         
                    } else if (slotParms.day.equals("Wednesday") && slotParms.time >= 800 && slotParms.time <= 1000  
                            && ((slotParms.course.equals("Upper") && ((slotParms.date >= 20150422 && slotParms.date <= 20150715 && week_of_year % 2 == 1) 
                              || slotParms.date == 20150909 || slotParms.date == 20150916 || slotParms.date == 20150923 || slotParms.date == 20151007)) 
                             || (slotParms.course.equals("Lower") && ((slotParms.date >= 20150429 && slotParms.date <= 20150722 && week_of_year % 2 == 0) || (slotParms.date >= 20150729 && slotParms.date <= 20150819) 
                              || slotParms.date == 20150902 || slotParms.date == 20150930 || slotParms.date == 20151014)))) {

                         baltusrolSkip = true;    // skip guest check if Ladies Day time
                    }
                   
                    if (!slotParms.player2.equalsIgnoreCase("x") && !slotParms.player3.equalsIgnoreCase("x") && !slotParms.player4.equalsIgnoreCase("x") &&
                        baltusrolSkip == false) {

                        //
                        //  No guests and no X's - reject
                        //
                        msgHdr = "Invalid Guest Time";

                        msgBody = "<p>Sorry, but you are not allowed to request a time without at least one guest or an X.</p>"
                                + "<p>Please add one or more guests or add an X.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }
                }           // end of IF Baltusrol

                //
                //  Oak Hill CC - Must be at least one guest in every request
                //
                if (club.equals("oakhillcc")) {           // if Oak Hill CC

                    msgHdr = "Invalid Guest Time";

                    msgBody = "<p>Sorry, but you are not allowed to request a time without at least one guest.</p>"
                            + "<p>Please add one or more guests or return to the tee sheet.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                    msgBody = "<p>Sorry, but a Non-Certified Dependent is not allowed when an adult is not included.</p>"
                            + "<p>Please remove the dependent or add an adult.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                    msgBody = "<p>Sorry, but a Dependent is not allowed when an adult is not included.</p>"
                            + "<p>Please remove the dependent or add an adult.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                    msgBody = "<p>Sorry, you must include at least 1 member and 1 guest (or X) when requesting a tee time.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                        msgBody = "<p>Sorry, you must have 3 guests and 1 member, or 2 guests and 2 members</p>"
                                + "<p>when requesting a tee time more than 14 days in advance.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }*/

                }

                if ((slotParms.day.equals("Wednesday") || slotParms.day.equals("Friday")) && slotParms.date != ProcessConstants.july4b) {  // if Wednesday or Friday

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

                        dbError(e5, msg, postback); 
                        return;
                    }

                    if (error == true) {      // a member exceed the max allowed tee times per month

                        msgHdr = "Invalid Number of Guests Specified";

                        msgBody = "<p>Sorry, you must have 3 guests and 1 member, or 2 guests and 2 members</p>"
                                + "<p>during the selected time for this day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }
                }
            }      // end of IF Oakmont

            //
            //  Custom for Oakmont CC - if less than 30 days in advance, guest tee times cannot be changed -
            //                          players may be added, but not removed.  Names can be changed however.
            //
            // This custom probably needs updating to deal with players being movable, and to use the new locking system in
            // parmSlot.setAccess().  foreTeesSlot may need to be extened to lock positions, but allow editing of guest names?
            //
            if (club.equals("oakmont") && slotParms.ind < 30) {      // if Oakmont CC

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

                        msgBody = "<p>Sorry, you cannot remove players from a tee time containing guests</p>"
                                + "<p>within 30 days of the tee time.  You may only add new members or guests.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        String test;
                        for(int i = 0; i < 4; i++){ // only check players 1-4?
                            test = slotParms.getMtype(i);
                            if(!test.isEmpty() 
                                    && !test.equals("Regular Spouse")
                                    && !test.equals("Jr. Spouse")
                                    && !test.equals("Reg Prob Spouse")){
                                error = true;
                                break;
                            }
                        }
                        /*
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
                         * 
                         */
                    }

                    if (error == true) {

                        msgHdr = "Invalid Request for Spouse/Guest Time";

                        msgBody = "<p>Sorry, you must specify 1 or 2 Spouse Members and at least 1 guest, but no more than 1 guest per member.</p>"
                                + "<p>Only Spouses and their guests are allowed to play during this time.</p>"
                                + "<p>Be sure to place the guests immediately following their host member.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, you must specify at least 2 guests for 1 member during this time.</p>"
                                + "<p>Be sure to place the guests immediately following their host member.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }

                    //
                    //  If #2 and 1:00 or 2:00 PM, must be at least 1 guest
                    //
                    if (slotParms.course.equals("No 2") && (slotParms.time == 1300 || slotParms.time == 1400)) {

                        error = medinahCustom.checkMG2b(slotParms, con);

                        if (error == true) {

                            msgHdr = "Invalid Request for Member/Guest Time";

                            msgBody = "<p>Sorry, you must specify at least 1 guest during this time.</p>"
                                    + "<p>Be sure to place the guest(s) immediately following the host member.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
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

                    msgBody = "<p>Sorry, you must specify at least 2 guests when making</p>"
                            + "<p>a tee time on the back 9 after 12:00 PM this far in advance.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                if (slotParms.ind > 9 || (slotParms.ind == 9 && thisTime < congTime)) {     // if a special guest time

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

                            msgBody = "<p>Sorry, " + slotParms.player + " already has 4 advance guest times scheduled this year.</p>"
                                    + "<p><BR></p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
                            return;
                        }
                         */

                    } else {   // not a valid Guest Time

                        msgHdr = "Invalid Guest Time Request";

                        msgBody = "<p>Sorry, you must include at least one 30 Day Advance Guest in the group</p>"
                                + "<p>when making a tee time this far in advance.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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
                verifySlot.getDaysInAdv(con, slotParms.club_parm, userMship);        // get the days in adv & time data for this member

                //
                //   Get the days in advance and time of day values for the day of this tee time
                //
                if (slotParms.day.equals("Sunday")) {

                    northDays = slotParms.club_parm.advdays1;
                    northTime = slotParms.club_parm.advtime1;

                } else if (slotParms.day.equals("Monday")) {

                    northDays = slotParms.club_parm.advdays2;
                    northTime = slotParms.club_parm.advtime2;

                } else if (slotParms.day.equals("Tuesday")) {

                    northDays = slotParms.club_parm.advdays3;
                    northTime = slotParms.club_parm.advtime3;

                } else if (slotParms.day.equals("Wednesday")) {

                    northDays = slotParms.club_parm.advdays4;
                    northTime = slotParms.club_parm.advtime4;

                } else if (slotParms.day.equals("Thursday")) {

                    northDays = slotParms.club_parm.advdays5;
                    northTime = slotParms.club_parm.advtime5;

                } else if (slotParms.day.equals("Friday")) {

                    northDays = slotParms.club_parm.advdays6;
                    northTime = slotParms.club_parm.advtime6;

                } else {

                    northDays = slotParms.club_parm.advdays7;
                    northTime = slotParms.club_parm.advtime7;
                }

                thisTime = SystemUtils.getTime(con);                  // get the current adjusted time

                if ((slotParms.date == ProcessConstants.memDay || slotParms.date == ProcessConstants.july4b || slotParms.date == ProcessConstants.laborDay)
                        && slotParms.ind == 7 && thisTime < northTime) {                            // if Holiday & 6 days in adv before time

                    mccskip = true;                   // ok to skip the 'days in advance' test (guest time), but check for guests

                } else {

                    if ((slotParms.date == ProcessConstants.memDay || slotParms.date == ProcessConstants.july4b || slotParms.date == ProcessConstants.laborDay) && slotParms.ind > 7) {   // if Holiday & more than 6 days in adv

                        mccskip = true;                 // ok to skip the 'days in advance' test, but check for guests

                    } else {                    // NOT a Holiday - use configured values for this mship and day

                        if ((slotParms.ind == northDays && thisTime < northTime) || slotParms.ind > northDays) {

                            mccskip = true;     // ok to skip the 'days in advance' test, but check for guests
                        }
                    }
                }

                //
                //  If only guest times are allowed (beyond normal days in adv), then check here
                //
                if (mccskip == true && slotParms.guests < 2) {     // if less than 2 guests in request

                    msgHdr = "Invalid Guest Time Request";

                    msgBody = "<p>Sorry, you must specify at least 2 guests following your name<BR>when making a tee time this far in advance.</p>"
                            + "<p>Days = " + northDays + ", Time = " + northTime + "</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                dbError(e7, msg, postback); 
                return;
            }                             // end of member restriction tests

            if (error == true) {          // if we hit on a restriction

                msgHdr = "Member Restricted";

                msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.</p>"
                        + "<p>This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b></p>"
                        + "<p>Please remove this player or try a different time.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message

                
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

                    msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.</p>";

                    if (medError == 1) {
                        msgBody += "<p>A Family Member (8 - 11) must be accompanied by an adult.</p>";
                    }
                    if (medError == 2) {
                        msgBody += "<p>A Family Member (12 & 13) must be accompanied by an adult.</p>";
                    }
                    if (medError == 3) {
                        msgBody += "<p>A Family Member (14 - 16) must be accompanied by a Member.</p>";
                    }
                    if (medError == 5 || medError == 10) {
                        msgBody += "<p>A Family Member (17 and Over) must be accompanied by a Member.</p>";
                    }
                    if (medError == 6) {
                        msgBody += "<p>A Family Member (12 & 13) must be accompanied by a Member.</p>";
                    }
                    if (medError == 7) {
                        msgBody += "<p>A Family Member (8 - 11) must be accompanied by an adult.</p>";
                    }
                    if (medError == 4 || medError == 8 || medError == 9) {
                        msgBody += "<p>A Spouse must be accompanied by a Member.</p>";
                    }
                    if (medError == 11) {
                        msgBody += "<p>A Family Member (14 - 16) must be accompanied by an adult.</p>";
                    }

                    msgBody += "Please remove this player or try a different time.";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                dbError(e7, msg, postback); 
                return;
            }                             // end of member restriction tests

            if (error == true) {          // if we hit on a restriction

                msgHdr = "Member Restricted by Member Number";
                
                List<String> restList = new ArrayList<String>();
                String temp;
                for(int i = 0; i < 5; i++){
                    temp = slotParms.getPnum(i);
                    if (!temp.isEmpty()) {
                        restList.add("<b>"+temp+"</b>");
                    }
                }

                /*
                msgBody = "<p>Sorry, </p>";

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
                 */
                msgBody += "<p>Sorry, " + htmlUtil.joinList(restList) + (restList.size()>1?" are":" is") + " restricted from playing during this time because the "
                        + " number of members with the same member number has exceeded the maximum allowed.</p>"
                        + "<p>This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b></p>"
                        + "<p>Please remove "+ (restList.size()>1?"these players":"this player") + " or try a different time.</p>";

                buildError(msgHdr, msgBody, postback);       // output the error message

                
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
            //int thr = 0;
            //int tmin = 0;

            try {

                verifySlot.checkSched(slotParms, con);

            } catch (Exception e21) {

                msg = "Check Members Already Scheduled. ";

                dbError(e21, msg, postback); 
                return;
            }

            if (slotParms.hit == true || slotParms.hit2 == true || slotParms.hit3 == true) { // if we hit on a duplicate res
                
                // Check if this player was already on the time

                if(true){

                    if (slotParms.time2 != 0) {                                  // if other time was returned
                        if (!slotParms.course2.isEmpty()) {        // if course provided
                            tmsg = timeUtil.get12HourTime(slotParms.time2)+" on the " + slotParms.course2 + " course";
                        }
                        /*
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
                         * 
                         */
                    }

                    msgHdr = "Member Already Playing";

                    if (slotParms.rnds > 1) {       // if multiple rounds per day supported
                        if (slotParms.hit3 == true) {       // if rounds too close together
                            msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is scheduled to play another round within " + slotParms.hrsbtwn + " hours.</p>"
                                    + slotParms.player + " is already scheduled to play on this date at <b>" + tmsg + "</b>.";
                        } else {

                            msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play the maximum number of times.</p>"
                                    + "A player can only be scheduled " + slotParms.rnds + " times per day.";
                        }
                    } else {
                        if (slotParms.hit2 == true) {
                            if (club.equals("oldoaks")) {
                                msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is part of a tee time request for this date.</p>";
                            } else if (!lotteryText.equals("")) {
                                msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is part of a " + lotteryText + " for this date.</p>";
                            } else {
                                msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is part of a lottery request for this date.</p>";
                            }
                        } else {
                            msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play on this date at <b>" + tmsg + "</b>.</p>";
                        }
                        msgBody += "<p>A player can only be scheduled once per day.</p>";
                    }


                    msgBody += "<p>Please remove this player(s) or try a different time.</p>"
                            + "<p>Contact the Golf Shop if you have any questions.</p>"
                            + "<p>If you are already scheduled for this date and would like to remove yourself<br>from that tee time, use the 'Go Back' button to return to the tee sheet and "
                            + "locate the time stated above, or click on the 'My Tee Times' tab.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    return;

                }
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

                    dbError(e21, msg, postback); 
                    return;
                }

                if (slotParms.hit == true) {      // if another family member is already booked today

                    msgHdr = "Member Already Scheduled";

                    msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> already has a family member scheduled to play today.</p>"
                            + "<p>Only one player per membership is allowed each day.</p>"
                            + "<p>Please remove this player or try a different date.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }


                //
                //  Merion - Now check if more than 7 days in adv and a w/e, no more than 4 adv tee times per day
                //
                if (slotParms.ind > 7 && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday"))) {   // if more than 7 days and a w/e

                    error = false;                             // init error indicator

                    try {

                        error = verifySlot.checkMerionWE(slotParms, con);

                    } catch (Exception e21) {

                        msg = "Check Merion Tee Times Already Scheduled. ";

                        dbError(e21, msg, postback); 
                        return;
                    }

                    if (error == true) {      // if another family member is already booked today

                        msgHdr = "Advance Tee Time Limit";

                        msgBody = "<p>Sorry, there are already 4 advance tee times scheduled for this day.</p>"
                                + "<p>Please try a different date.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                            dbError(e21, msg, postback); 
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

                            msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is not allowed to be part of a tee time this far in advance.</p>";

                            if (slotParms.club_parm.x > 0) {
                                msgBody += "<p>You can use an 'X' to reserve this position until the player is allowed.</p>";
                            } else {
                                msgBody += "<p>Contact the golf shop if you wish to add this person at this time.</p>";
                            }

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
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

                                        msgBody = "<p>Sorry, at least one Member must be included in the group when</p>"
                                                + "<p>scheduling a tee time more than 1 day in advance.</p>";

                                        buildError(msgHdr, msgBody, postback);       // output the error message

                                        
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

                        dbError(e22, msg, postback); 
                        return;
                    }

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Weekend Tee Time Limit Exceeded for Member";

                        msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is an Elective member and has<BR>already played 10 times on weekends or holidays this year.</p>"
                                + "<p>Remove this player and try again.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                msgBody = "<p>Sorry, you cannot reserve a tee time more than 30 days <br></p>"
                        + "in advance unless there is at least one guest included.";

                buildError(msgHdr, msgBody, postback);       // output the error message

                
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

                    msgBody = "<p>Sorry, a Jr Golf member must be <br></p>"
                            + "accompanied by a non Jr Golf member.";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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
            
            msgBody = "<p>Sorry, guests are not allowed to be part of a tee time this far in advance.</p>" +
            "<p>Please remove the guests or contact the golf shop for assistance.</p>";
            
            buildError(msgHdr, msgBody, postback);       // output the error message
            
            
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

                    dbError(e21, msg, postback); 
                    return;
                }

                if (error == true) {          // if we hit on a violation

                    msgHdr = "Guest Time Violation";

                    msgBody = "<p>Sorry, you must include at least 2 guests during this time.</p>"
                            + "<p>Please contact the golf shop if you have any questions.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message
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

                    msgBody = "<p>Sorry, there are already 2 tee times with Club Golf members<BR>or Recip guests scheduled this hour.</p>"
                            + "<p>Please select a different time of day, or change the players.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                    msgBody = "<p>Sorry, dependents must be accompanied by an adult after 4:00 PM each day.</p>"
                            + "<p>Please select a different time of day, or change the players.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                    msgBody = "<p>Sorry, dependents must be accompanied by an adult.</p>"
                            + "<p>Please contact the golf shop if you have any questions.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
                    return;
                }

                //
                //  Check for advance times if more than 5 days in adv (limit varies based on day of week and guests or no guests)
                //
                if (slotParms.ind > 5) {        // if more than 5 days in advance

                    error = verifySlot.checkOaklandAdvTime1(slotParms, con);

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Member Has Already Used An Advance Request";

                        msgBody = "<p>Sorry, each membership is entitled to only one advance tee time request.</p>"
                                + "<p>" + slotParms.player + " has already used his/her advance tee time request for the season.</p>"
                                + "<p>Please contact the golf shop if you have any questions.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }

                    error = verifySlot.checkOaklandAdvTime2(slotParms, con);

                    if (error == true) {          // if we hit on a violation

                        msgHdr = "Maximum Allowed Advanced Tee Times Exist";

                        msgBody = "<p>Sorry, the maximum number of advanced tee time requests already exist on the selected date.</p>"
                                + "<p>Please contact the golf shop if you have any questions.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, you cannot reserve player positions with an X more than 7 days in advance.</p>"
                                + "<p>Please contact the golf shop if you have any questions.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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
                        msgBody = "<p>Sorry, " + slotParms.player + " already has 12 advance tee time requests scheduled.</p>";
                    } else {
                        msgBody = "<p>Sorry, " + slotParms.player + " already has 5 advance tee time requests scheduled.</p>";
                    }

                    msgBody += "<p>Please remove this player from your request.<BR>Contact the golf shop if you have any questions.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                    msgBody = "<p>Sorry, one or more players are not allowed to be part of a tee time for this day and time.</p>";

                    if (slotParms.day.equals("Monday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Friday")) {
                        msgBody += "<p>A Member must be included when making the request more than 1 day in advance.</p>";
                    } else {
                        if (slotParms.day.equals("Tuesday")) {
                            if (slotParms.time > 1100) {
                                msgBody += "<p>A Member must be included when making the request more than 1 day in advance.</p>";
                            } else {
                                msgBody += "<p>Only Spouses may make a request more than 1 day in advance for a tee time before 11 AM on Tuesdays.</p>";
                            }
                        } else {
                            if (slotParms.day.equals("Thursday")) {
                                if (slotParms.time > 1000) {
                                    msgBody += "<p>A Member must be included when making the request more than 1 day in advance.</p>";
                                } else {
                                    msgBody += "<p>Only Spouses may make a request more than 1 day in advance for a tee time before 10 AM on Thursdays.</p>";
                                }
                            } else {
                                if (slotParms.day.equals("Sunday")) {
                                    if (slotParms.time > 1000) {
                                        msgBody += "<p>A Member must be included when making the request more than 1 day in advance.</p>";
                                    } else {
                                        msgBody += "<p>Only Members may be included in a tee time before 10 AM on Sundays.</p>";
                                    }
                                } else {       // Saturday or Holiday
                                    if (slotParms.time > 1100) {
                                        msgBody += "<p>A Member must be included when making the request more than 1 day in advance.</p>";
                                    } else {
                                        msgBody += "<p>Player not allowed to make a tee time more than 24 hours in advance on Saturdays and Holidays before 11 AM.</p>";
                                    }
                                }
                            }
                        }
                    }
                    msgBody += "<p>Please change players or select a different day or time of day.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                        msgBody = "<p>Sorry, there is already a small group immediately before or after this time.<BR>There cannot be 2 consecutive small groups during this time.</p>"
                                + "<p>Please add players or select a different time of day.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                    msgBody = "<p>Sorry, CD Plus members are not allowed to play at this time<BR>unless accompanied by an authorized member.</p>"
                            + "<p>Please select a different time of day.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                            msgBody = "<p>Sorry, 5-somes are not allowed without a member from that club.</p>"
                                    + "<p>Please limit the request to 4 players or include a member of the club.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
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
            //if (req.getParameter("skip8") == null) {

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

                            msgBody = "<p>Sorry, you cannot book guest times before 7:30 AM when booking 7 days in advance.</p>"
                                    + "<p>Please remove the guests or return to the tee sheet.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            return;

                        } else if (slotParms.ind > 5 && slotParms.time < 1101) {

                            msgHdr = "Request Not Allowed";

                            msgBody = "<p>Sorry, guests are not allowed this far in advance until after 11:00 AM.</p>"
                                    + "<p>Please remove the guests or return to the tee sheet.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            return;
                        }
                    }     // end of IF The Lakes



                    //
                    //  If no members requested and Unaccompanied Guests are ok at this club
                    //
                    if (slotParms.members == 0 && slotParms.club_parm.unacompGuest == 1) {
                        
                        for(int i = 0; i < 5; i++){
                            if(!slotParms.getG(i).isEmpty()){ // if player is a guest
                                slotParms.setUserg(user, i); // set username for guests
                            }
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

                                dbError(e29, msg, postback); 
                                return;
                            }

                            if (rcode > 0) {          // if we hit on a violation

                                msgHdr = "Restriction For Sponsored Group Request";

                                msgBody = "<p>Your request for a Sponsored Group has been rejected for the following reason:<BR></p>";

                                if (rcode == 1) {
                                    msgBody += "The maximum number of Sponsored Groups have already been scheduled for this day.";
                                } else {
                                    if (rcode == 2) {
                                        msgBody += "Sponsored Groups are not allowed at this time of day.";
                                    } else {
                                        msgBody += "You already have 2 Sponsored Groups scheduled today.";
                                    }
                                }
                                msgBody += "Please change this request or try a different date.<br>"
                                        + "Contact the  golf shop if you have any questions.";

                                buildError(msgHdr, msgBody, postback);       // output the error message

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
//                                    msgBody = "<p>Sorry, guests are not allowed in a 5-some.</p>"
//                                            + "<p>Please limit the request to 4 players or remove the guest(s).</p>";
//
//                                    buildError(msgHdr, msgBody, postback);       // output the error message
//
//                                    
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
//                                    msgBody = "<p>Sorry, no more than 2 guests are allowed in a 5-some.</p>"
//                                            + "<p>Please limit the request to 4 players or 2 guests.</p>";
//
//                                    buildError(msgHdr, msgBody, postback);       // output the error message
//
//                                    
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

                                    msgBody = "<p>Sorry, you may not have more than one guest in a 5-some.</p>"
                                            + "<p>Please limit the request to 4 players or remove the guest(s).</p>";

                                    buildError(msgHdr, msgBody, postback);       // output the error message

                                    
                                    return;
                                }
                            }
                        }     // end of IF Fort Collins
                        */


                        int firstGuest = 5;
                        int firstMember = 5;
                        if (slotParms.members > 0) {     // if at least one member

                            //
                            //  Both guests and members specified (member verified above) - determine guest owners by order
                            //
                            String memberName = "";
                            String temp;

                            for (int i = 0; i < 5; i++) {                  // cycle thru arrays and find guests/members
                                temp = slotParms.getUser(i);
                                if (!temp.isEmpty()) {
                                    firstMember = Math.min(i, firstMember);
                                    memberName = temp;        // get players username
                                } else if (!slotParms.gstA[i].isEmpty()) {
                                    firstGuest = Math.min(i, firstGuest);
                                    slotParms.setUserg(memberName, i); // set usernames for guests in teecurr
                                } else {
                                    slotParms.setUserg("", i); // set usernames for guests in teecurr
                                }
                            }
                        }

                        if ((slotParms.members > 1 || !slotParms.g1.isEmpty() || firstGuest < firstMember) && req.getParameter("skip8") == null) {  // if multiple members OR slot 1 is a guest
                            
                            if(postback.group_confirm_type.isEmpty()
                                    || postback.group_confirm_type.equals("guestassc_error")
                                    || postback.group_confirm_type.equals("guestassc_prompt")){
                                postback.group_confirm_title = "Player/Guest Association";
                                //
                                //  At least one guest and 2 members have been specified, or P1 is a guest.
                                //  Prompt user to verify the order.
                                //
                                //  Only require positioning if a POS system was specified for this club (saved in Login)
                                //

                                    // Fill that field map with values that will be used when calling back
                                    postback.group_confirm_callback_map.put("skip8", "yes");
                                    
                                    postback.continue_loop_on_error = true;

                                    // Build the player list
                                    StringBuilder player_list_html = new StringBuilder();
                                    if(slotParms.group_slots.size() > 1){
                                        player_list_html.append("<b class=\"list_header\">"+timeUtil.get12HourTime(slotParms.time) +" Tee Time</b>");
                                    }
                                    player_list_html.append("<ul class=\"indented_list guest_assoc\">");
                                    boolean guest_error = false;
                                    for (int i2 = 0; i2 < 5; i2++) {
                                        boolean isGuest = false;
                                        String playerName = slotParms.getPlayer(i2);
                                        String userName = slotParms.getUser(i2);
                                        if (!playerName.isEmpty()) {
                                            reservationPlayer pl = null;
                                            if(userName.isEmpty() && slotParms.getUserg(i2) != null){
                                                pl = slotParms.getUserLookupMap().get(slotParms.getUserg(i2).toLowerCase());
                                                isGuest = true;
                                            }
                                            player_list_html.append("<li class=\"");
                                            player_list_html.append((!slotParms.getG(i2).isEmpty() || !slotParms.gstA[i2].isEmpty())? "guest_item" : "player_item");
                                            player_list_html.append(((!slotParms.getG(i2).isEmpty() || !slotParms.gstA[i2].isEmpty()) && pl == null) ? " no_owner" : "");
                                            player_list_html.append("\"><span>");
                                            player_list_html.append(playerName);
                                            
                                            if(pl != null){
                                                player_list_html.append("</span> <span>Guest of: ");
                                                player_list_html.append(pl.name);
                                            } else if(userName.isEmpty() && !playerName.equalsIgnoreCase("X")) {
                                                guest_error = true;
                                                player_list_html.append("</span> <span>Guest of: Unspecified");
                                            }
                                            player_list_html.append("</span></li>");
                                        }
                                    }
                                    player_list_html.append("</ul>");

                                    // Fill the result map
                                    //postback.title = "";
                                    //postback.group_confirm_yes_no = true;
                                    if ((guest_error && !posType.equals("") && !slotParms.oldPlayer1.equals(slotParms.player1))
                                            || postback.group_confirm_type.equals("guestassc_error")) {
                                        postback.group_confirm_type = "guestassc_error";
                                        postback.group_confirm_header = ""
                                                + "<span class=\"guest_item\">Guests</span> must be specified <i><u>immediately after</u></i> the <span class=\"player_item\">member</span> they belong to.<br><br>"
                                                + "<span class=\"guest_item no_owner\">The first player position of a tee time cannot contain a guest.</span><br><br>"
                                                + "<b>Please correct the order of players.</b><br><br>"
                                                + "<b>This is what you requested:</b>";
                                        postback.group_confirm_list.add(player_list_html.toString());
                                        postback.group_confirm_footer = "";
                                        postback.group_confirm_close_continue = false;
                                        postback.group_confirm_yes_no = false;
                                        //postback.continue_button = null;
                                    } else {
                                        postback.group_confirm_type = "guestassc_prompt";
                                        postback.group_confirm_header = ""
                                                + "<span class=\"guest_item\">Guests</span> should be specified <i><u>immediately after</u></i> the <span class=\"player_item\">member</span> they belong to.<br><br>"
                                                + "<b>Please verify the following order:</b>";
                                        postback.group_confirm_list.add(player_list_html.toString());
                                        postback.group_confirm_footer = "Would you like to process the request as is?";
                                        //postback.continue_button = "YES, continue";
                                        postback.group_confirm_close_continue = true;
                                        postback.group_confirm_yes_no = true;
                                    }
                                    
                                    return;
                                }

                        }   // end of IF more than 1 member or guest in spot #1
                    }      // end of IF no members and unaccompanied guests are ok
                    
                } else {        // NO Guests in this tee time

                    slotParms.fillUserg("", 5); // make sure member assignments are cleared (in case a guest was removed)

                }         // end of IF any guests specified
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

                    dbError(e22, msg, postback); 
                    return;
                }

                if (error == true) {          // if we hit on a violation

                    msgHdr = "Guest Quota Exceeded for Member";

                    msgBody = "<p>Sorry, requesting <b>" + slotParms.player + "</b> exceeds the guest quota established by the Golf Shop.</p>"
                            + "<p>You are allowed " + slotParms.grest_num + " of these guests per " +slotParms.grest_per+ " during a timeframe defined by club policy and you have met or exceeded that limit.</p>"
                            + "<p>You will have to remove the guest in order to complete this request.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                    msgHdr = "Notice"; // Was empty?

                    msgBody = gstCustomMsg; // was just "<BR>"?

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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

                        msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> has already met the guest quota for June through August.</p>"
                                + "<p>You will have to remove the guest in order to complete this request.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest at this time.</p>"
                                + "<p>You will have to remove the guest in order to complete this request.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> has already met the quota for Guest Times.<br>Each membership (family) is limited to a specified number of guest times that may be scheduled in advance.</p>"
                                + "<p>You will have to remove the guest(s) in order to complete this request.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                        msgBody = "<p>Sorry, Junior A members can only have one guest per member<br>on the Open Course on weekdays.</p>"
                                + "<p>You will have to remove the extra guest(s) in order to complete this request.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
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

                            msgBody = "<p>Sorry, " + slotParms.player + " has already reached the maximum limit of guests.</p>"
                                    + "<p>Each membership is allowed 6 guests per month and 18 guests per season.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
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
                        switch(wellError){
                            
                            case 1:
                                msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest.</p>";
                                break;
                                
                            case 2:
                            case 3:
                                msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest on this day.</p>";
                                break;
                            
                            case 4:
                                msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest on this date.</p>";
                                break;
                                
                            case 5:
                                msgBody = "<p>Sorry, <b>" + slotParms.player + "</b> has already reached the yearly guest quota.</p>";
                                break;
                        }
                        
                        msgBody += "<p>You will have to remove the guest(s) in order to complete this request.</p>"
                                + "<p>Contact the Golf Shop if you have any questions.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

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

                            msgBody = "<p>Sorry,  " + slotParms.player + " has already scheduled the max allowed guest times this month.<BR>There is a limit to the number of advance guest rounds that can be scheduled in Feb, Mar, Apr, and May.</p>"
                                    + "<p>Please contact the golf shop if you have any questions.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
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

                        msgBody = "<p>Sorry, " + slotParms.player + " has already scheduled the max allowed guest times.<BR>There is a limit to the number of guest times that can be scheduled in advance.</p>"
                                + "<p>Please contact the golf shop if you have any questions.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }
                }          // end of IF baltusrol

                if (club.equals("woodway")) {

                    error = verifyCustom.checkWoodwayGuests(slotParms, con);

                    if (error == true) {

                        msgHdr = "Guests Restricted for Member";

                        msgBody = "<p>Sorry,  " + slotParms.player + " is not allowed to have a guest on this date.</p>"
                                + "<p>You will have to remove the guest(s) in order to complete this request.</p>";

                        buildError(msgHdr, msgBody, postback);       // output the error message

                        
                        return;
                    }
                }          // end of IF woodway

            }            // end of IF any GUESTS included in request


            //
            //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
            //
            verifySlot.checkTFlag(slotParms, con);
            
            // Create some Maps/Sets we'll use later (only create these after we're done modifying key player data in slotParms)
            slotParms.checkModifications(true); // Force recreation of the maps (positions may have changed due)
            Set<Integer> newPlayers = slotParms.getNew();
            //Map<Integer, Integer> modifiedPlayers = slotParms.getModified();
            Map<Integer, Integer> unmodifiedPlayers = slotParms.getUnModifiedNames(); // cw, p9, etc., may have been modified. Just not the player name.
            Set<Integer> empty = slotParms.getEmpty();
            Set<Integer> removed = slotParms.getRemoved(); // index of old players that were removed
            Map<Integer, Integer> modifiedPlayers = slotParms.getModified();
            Map<Integer, Integer> onlyPositionModified = slotParms.getOnlyPositionChanged();
            Map<Integer, Integer> onlyUserGModified = slotParms.getModifiedOnlyGuestUsers();
            
            // postback.debug is only used for debuging.  Can be commented out when development is complete.
            postback.debug.put("locked_players_"+slotParms.group_index,slotParms.lock_player);
            postback.debug.put("newPlayers_"+slotParms.group_index,newPlayers);
            postback.debug.put("removed_"+slotParms.group_index,removed);
            postback.debug.put("modifiedPlayers_"+slotParms.group_index,modifiedPlayers);
            postback.debug.put("unmodifiedPlayers_"+slotParms.group_index,unmodifiedPlayers);
            postback.debug.put("onlyUserGModified_"+slotParms.group_index,onlyUserGModified);
            postback.debug.put("empty_",empty);

            // Check if any locked players were modified.
            
            List<String> lockedButModified = new ArrayList<String>();
            // modified by removal
            for(Integer i : removed){
                if(slotParms.lock_player[i]){
                    lockedButModified.add(slotParms.getOldPlayer(i));
                }
            }
            // modified by cw/p9/etc.
            for(Integer i : modifiedPlayers.values()){
                if(slotParms.lock_player[i]){
                    lockedButModified.add(slotParms.getOldPlayer(i));
                }
            }
            
            if (lockedButModified.size() > 0) {
                // Some locked players were modified.  Complain.
                // They shouldn't have been able to get here in the first place
                // Most likley a hack attempt, or access rules changed while 
                // they were on the slot page
                msgHdr = "Unable to Modify";
                msgBody += "<p>Sorry, you do not have access to make changes "
                        + "to the locked player" + (lockedButModified.size() > 1 ? "s" : "") + " "
                        + "<b>" + htmlUtil.joinList(lockedButModified) + "</b>.</p>"
                        + "<p>Please contact the golf shop with any questions.</p>";
                buildError(msgHdr, msgBody, postback);       // output the error message
                Utilities.logError("Member_slot - Attempted modification of locked player.  Possible hack attempt. Club:" + club + ",  User:" + user, con);
                return;
            }
            
            // Check if Userg was modified
            for(Integer i : onlyUserGModified.values()){
                if(slotParms.lock_player[i]){
                    lockedButModified.add(slotParms.getOldPlayer(i));
                }
            }
            
            if (lockedButModified.size() > 0) {
                // A locked guest's userg was modified (A different memebr is now associated with the guest)
                msgHdr = "Unable to Change Locked Guest";
                msgBody += "<p>Sorry, unable to change the associated member for the locked guest" + (lockedButModified.size() > 1 ? "s" : "") + " "
                        + "<b>" + htmlUtil.joinList(lockedButModified) + "</b>.</p>"
                        + "<p>Please contact the golf shop with any questions.</p>";
                
                buildError(msgHdr, msgBody, postback);       // output the error message
                Utilities.logError("Member_slot - Attempted modification of locked guest.  Possible hack attempt. Club:" + club + ",  User:" + user, con);
                return;
            }
            
            
            // Check if any locked positions were modified.
            for(Integer i : newPlayers){
                if(slotParms.lock_player[i]){
                    lockedButModified.add(slotParms.getPlayer(i));
                }
            }
            
            if (lockedButModified.size() > 0) {
                // A new player was added to a locked position.  Complain.
                // They shouldn't have been able to get here in the first place
                // Most likley a hack attempt, or access rules changed while 
                // they were on the slot page
                msgHdr = "Unable to Change Locked Position";
                msgBody += "<p>Sorry, unable to add the player" + (lockedButModified.size() > 1 ? "s" : "") + " "
                        + "<b>" + htmlUtil.joinList(lockedButModified) + "</b> to a locked position.</p>"
                        + "<p>Please contact the golf shop with any questions.</p>";
                
                buildError(msgHdr, msgBody, postback);       // output the error message
                Utilities.logError("Member_slot - Attempted modification of locked position.  Possible hack attempt. Club:" + club + ",  User:" + user, con);
                return;
            }
            
            

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
            
            msgBody = "<p>Sorry, you cannot replace a member with a guest when the<br>member was added more than " +sciotoDays+ " days in advance.</p>" +
            "<p>Please remove the guest or contact the golf shop for assistance.</p>";
            
            buildError(msgHdr, msgBody, postback);       // output the error message
            
            
            return;
            }
            }
            }
             */


            //
            //  Wilmington Custom - check mship subtypes for those that have range privileges
            //
            if (club.equals("wilmington")) {
                slotParms.setCustomDisp(slotParms.getMstypeArray(5));
                
                /*
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
                 * 
                 */
            }      // end of Wilmington custom

            //
            //  Custom for Eagle Creek - check to see if Social members have exceeded their # of rounds in season (Case #1284)
            //    - and make sure Socials are accompanied by a Golf mship
            //
            if (club.equals("eaglecreek")) {

                // now check to see if this member is Social and if so they must be accompanied by a Golf mship
                //int tmp_yy = (int) slotParms.date / 10000;         // get year
                int tmp_sdate = (slotParms.yy * 10000) + 1101;               // yyyy1101
                int tmp_edate = ((slotParms.yy + 1) * 10000) + 430;        // yyyy0430

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

                                msgBody = "<p>Sorry, " + slotParms.player + " is a Social member and has exceeded the<BR></p>"
                                        + "maximum number of tee times allowed for this season (November 1 thru April 30).";

                                buildError(msgHdr, msgBody, postback);       // output the error message

                                
                                return;
                            }

                        } else {

                            // we didn't find a Golf mship so disallow
                            msgHdr = "Member Exceeded Max Allowed Rounds";

                            msgBody = "<p>Sorry, members with Social memberships must be accompanied by a member with</p>"
                                    + "<p>a Golf membership classification from November 1 thru April 30.</p>";

                            buildError(msgHdr, msgBody, postback);       // output the error message

                            
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

                    msgBody = "<p>Sorry, " + slotParms.player + " is a Sports member and has exceeded the</p>"
                            + "<p>maximum number of tee times allowed for this season.</p>";

                    buildError(msgHdr, msgBody, postback);       // output the error message

                    
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
                for(int i = 0; i < 5; i++){
                    if (Utilities.checkBirthday(slotParms.getUser(i), slotParms.date, con)) {
                        slotParms.setCustomDisp("bday", i);
                    }
                }
                /*
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
                 * 
                 */
            }

            //
            //  Custom for Timarron CC.  Set custom_disp values so we know who added which members to the tee time.  They'd like for
            //  players to only be able to remove themselves or other players they personally added to the tee time
            //
            if (club.equals("timarroncc")) {

                
                // Set custom_disp to current user for any modified or new entries
                
                for(Integer i : newPlayers){
                    slotParms.setCustomDisp(user, i);
                }

                // Set the custom_disp as it was for any modified/unmodified (they may have moved position, cw, p9, etc.)
                for(Map.Entry<Integer, Integer> entry : unmodifiedPlayers.entrySet()){
                    Integer playerIndex = entry.getKey();
                    Integer oldPlayerIndex = entry.getValue();
                    slotParms.setCustomDisp(slotParms.oldCustom_disp[oldPlayerIndex].isEmpty()?user:slotParms.oldCustom_disp[oldPlayerIndex], playerIndex);
                }
                
                // Set any empty players 
                for(Integer i : empty){
                    slotParms.setCustomDisp("", i);
                }

                /*
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
                 * */
                

            }

            //
            // Set orig values so we know who added which players to the tee time for later use
            //
            
            // Set defaults for any added players
            for(Integer i : newPlayers){
                // Check if this player was moved from another Tee Time in this group
                Integer[] movedPlayerIndex = slotParms.findMovedFromGroup(i);
                if(movedPlayerIndex == null){
                    // Looks like a new user
                    // Set some defaults
                    slotParms.setOrig(user, i);
                    //postback.debug.put("NEW_PLAYER_"+slotParms.group_index+"_"+i,"NEW");
                } else {
                    // Probably moved from another tee time ("Probably" because we can't be sure with guests).
                    // Use some values from there
                    slotParms.setOrig(slotParms.group_slots.get(movedPlayerIndex[0]).oldOrig[movedPlayerIndex[1]], i);
                    //postback.debug.put("NEW_PLAYER_"+slotParms.group_index+"_"+i,"MOVED_FROM:"+movedPlayerIndex);
                }
                
            }
            
            // Set as it was for any unmodified (they may have moved position, cw, p9, etc.)
            for(Map.Entry<Integer, Integer> entry : unmodifiedPlayers.entrySet()){
                Integer playerIndex = entry.getKey();
                Integer oldPlayerIndex = entry.getValue();
                slotParms.setOrig(slotParms.oldOrig[oldPlayerIndex].isEmpty()?user:slotParms.oldOrig[oldPlayerIndex], playerIndex);
            }
            
            for(Integer i : empty){
                slotParms.setOrig("", i);
                slotParms.setCw("", i);
                slotParms.setUserg("", i);
                slotParms.setP9(0, i);
                slotParms.setGuestId(0, i);
                slotParms.setShow((short) 0, i);
            }
            
            /*
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
             * */
            //**************************************************************
            //  Verification Complete !!!!!!!!
            //**************************************************************

            slotParms.sendemail = 0;         // init email flags
            slotParms.emailNew = 0;
            slotParms.emailMod = 0;

            //
            //  Pre-checkin feature - normally sets an indicator if the tee time is created or changed the same day (day of)
            //                        as the tee time itself.  This creates a visual for the proshop user on the tee sheet
            //                        so they can easily see which tee times are new that day.
            //
            // set to show values to 2 if feature is supported and teetime is today
            GregorianCalendar cal_pci = new GregorianCalendar();
            short tmp_pci = (slotParms.club_parm.precheckin == 1
                    && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                    && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                    && slotParms.yy == cal_pci.get(cal_pci.YEAR)) ? (short) 2 : (short) 0;


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
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 17) ? (short) 2 : (short) 0;
            }

            // Custom for Imperial GC - Utilize pre-checkin for tomorrow bookings if it's after 1pm ET today - Case# 1327
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("imperialgc")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 12) ? (short) 2 : (short) 0;
            }


            // Custom for Mediterra - Utilize pre-checkin for tomorrow bookings if it's after 5:30pm today - Case# 1309
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("mediterra")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
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
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 18) ? (short) 2 : (short) 0;
            }

            // Custom for Wildcat Run G & CC - Utilize pre-checkin for tomorrow bookings if it's after 4:30pm ET today - Case# 2111
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("wildcatruncc")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
                        && ((cal_pci.get(Calendar.HOUR_OF_DAY) >= 15 && cal_pci.get(Calendar.MINUTE) >= 30)
                        || cal_pci.get(Calendar.HOUR_OF_DAY) >= 16)) ? (short) 2 : (short) 0;
            }

            // Custom for Governors Club - Utilize pre-checkin for tomorrow bookings if it's after 4:00pm ET today - Case# 2351
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("governorsclub")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 15) ? (short) 2 : (short) 0;
            }
            
            // Custom for Laurel Oak CC - Utilize pre-checkin for tomorrow bookings if it's after 6:00pm ET today - Case# 2484
            //a
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("laureloak")) {

                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
                        && cal_pci.get(Calendar.HOUR_OF_DAY) >= 17) ? (short) 2 : (short) 0;
            }
            
            // Custom for Quail Ridge Country Club  - Utilize pre-checkin for tomorrow bookings if it's after 5:00pm ET today - Case# 2495
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("quailridgecc")) {
                int time = Utilities.getTime(con);
                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
                        && time >= 1700) ? (short) 2 : (short) 0;
            } 
            // Custom for Moss Creek Golf Club  - Utilize pre-checkin for tomorrow bookings if it's after 5:00pm ET today - Case# 2565
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //
            if (tmp_pci != 2 && club.equals("mosscreek")) {
                int time = Utilities.getTime(con);
                cal_pci.add(cal_pci.DAY_OF_MONTH, +1);
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.mm == (cal_pci.get(cal_pci.MONTH) + 1)
                        && slotParms.dd == cal_pci.get(cal_pci.DAY_OF_MONTH)
                        && slotParms.yy == cal_pci.get(cal_pci.YEAR)
                        && time >= 1700) ? (short) 2 : (short) 0;
            }
            // Custom for Country Club of St. Albans - Utilize pre-checkin for tomorrow bookings if it's after 6:00pm - Case# 2579
            //
            //   NOTE:  club must be added to check in Proshop_sheet so check-in boxes will be shown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //                         
            if (tmp_pci != 2 && club.equals("ccstalbans")) {
                int time = Utilities.getTime(con);
                long date = Utilities.getDate(con,1);
                tmp_pci = (slotParms.club_parm.precheckin == 1
                        && slotParms.date == date
                        && time >= 1800) ? (short) 2 : (short) 0;
            }
            


            //
            //  If players changed, then init the no-show flag and send emails, else use the old no-show value
            //
            
            for(Integer i : newPlayers){
                slotParms.setShow(tmp_pci, i);
                slotParms.sendemail = 1;    // new player - send email notification to all
            }
            
            // Set as it was for any unmodified (they may have moved position, cw, p9, etc., but name did not change)
            for(Map.Entry<Integer, Integer> entry : unmodifiedPlayers.entrySet()){
                Integer playerIndex = entry.getKey();
                Integer oldPlayerIndex = entry.getValue();
                slotParms.setShow(slotParms.oldShow[oldPlayerIndex], playerIndex);
            }
            
            if(removed.size() > 0){
                // Some players have been removed - send email notification to all
                slotParms.sendemail = 1; 
            }
            /*
            if (!slotParms.player1.equals(slotParms.oldPlayer1)) {

                slotParms.show1 = tmp_pci;        // init no-show flag
                slotParms.sendemail = 1;    // player changed - send email notification to all
            }

            if (!slotParms.player2.equals(slotParms.oldPlayer2)) {

                slotParms.show2 = tmp_pci;        // init no-show flag
                slotParms.sendemail = 1;    // player changed - send email notification to all
            }

            if (!slotParms.player3.equals(slotParms.oldPlayer3)) {

                slotParms.show3 = tmp_pci;        // init no-show flag
                slotParms.sendemail = 1;    // player changed - send email notification to all
            }

            if (!slotParms.player4.equals(slotParms.oldPlayer4)) {

                slotParms.show4 = tmp_pci;        // init no-show flag
                slotParms.sendemail = 1;    // player changed - send email notification to all
            }

            if (!slotParms.player5.equals(slotParms.oldPlayer5)) {

                slotParms.show5 = tmp_pci;        // init no-show flag
                slotParms.sendemail = 1;    // player changed - send email notification to all
            }
             * */
            //
            //   Set email type based on new or update request (cancel set above)
            //   Also, bump stats counters for reports
            //
            if ((!slotParms.oldPlayer1.equals("")) || (!slotParms.oldPlayer2.equals("")) || (!slotParms.oldPlayer3.equals(""))
                    || (!slotParms.oldPlayer4.equals("")) || (!slotParms.oldPlayer5.equals(""))) {

                slotParms.emailMod = 1;  // tee time was modified
                //memMod++;      // increment number of mods

            } else {

                slotParms.emailNew = 1;  // tee time is new
                //memNew++;      // increment number of new tee times
            }
            
            
            //
            //  If new notes added or if previous notes were changed, then add this member's name to the end for identification purposes.
            //
            if (!slotParms.notes.equals("") && !slotParms.notes.equals(slotParms.oldNotes) && !slotParms.notes.endsWith("(" +fullName+ ")")) {
               
               slotParms.notes = slotParms.notes + " (" +fullName+ ")";     
            }           
            

            //
            //  Oak Hill CC - track the Cancel if this tee time was an advance tee time
            //
            if (club.equals("oakhillcc") && custom_int > 0) {

               verifyCustom.logOakhillAdvGst(slotParms.teecurr_id, slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                              slotParms.player4, slotParms.player5, user, fullName, slotParms.notes, 2, con);
            }
            
            if (club.equals("ccstalbans") && tmp_pci == 2) {
                parmEmail parme = new parmEmail();          // allocate an Email parm block

                //  Set the values in the email parm block
                parme.type = "tee";         // type = tee time
                parme.date = slotParms.date;
                parme.time = slotParms.time;
                parme.fb = slotParms.fb;
                parme.mm = slotParms.mm;
                parme.dd = slotParms.dd;
                parme.yy = slotParms.yy;

                parme.user = user;
                parme.emailNew = slotParms.emailNew;
                parme.emailMod = slotParms.emailMod;
                parme.emailCan = slotParms.emailCan;

                parme.course = slotParms.course;
                parme.day = slotParms.day;
                parme.notes = slotParms.notes;
                
                parme.setP9(slotParms.getP9Array(5));
                parme.setPlayer(slotParms.getPlayerArray(5));
                parme.setUser(slotParms.getUserArray(5));
                parme.setCw(slotParms.getCwArray(5));
                parme.setGuestId(slotParms.getGuestIdArray(5));
                parme.setUserg(slotParms.getUsergArray(5));
                
                parme.setOldPlayer(slotParms.getOldPlayerArray(5));
                parme.setOldUser(slotParms.getOldUserArray(5));
                parme.setOldCw(slotParms.getOldCwArray(5));
                parme.setOldGuestId(slotParms.getOldGuestIdArray(5));

                sendEmail.sendOakmontEmail(parme, con, club);
            }           
      
      


        }  // end of IF 'cancel this res' ELSE 'process tee time request'
        
        
        

        // Custom for Shady Canyon GC - If notes have changed, send custom email to pro
        if (club.equals("shadycanyongolfclub") && slotParms.emailCan == 0) {

            slotParms.sendShadyCanyonNotesEmail = false;

            if (slotParms.emailNew == 1 && !slotParms.notes.equals("")) {  // new request and contains notes, send email.

                slotParms.sendShadyCanyonNotesEmail = true;

            } else if (slotParms.emailMod == 1) {  // Updated request, see if the notes were changed at all.  If so, send email

                try {

                    PreparedStatement pstmtTemp = null;
                    ResultSet rsTemp = null;

                    pstmtTemp = con.prepareStatement("SELECT notes FROM teecurr2 WHERE teecurr_id = ?");
                    pstmtTemp.clearParameters();
                    pstmtTemp.setLong(1, slotParms.teecurr_id);

                    rsTemp = pstmtTemp.executeQuery();

                    if (rsTemp.next()) {
                        if (!slotParms.notes.equals(rsTemp.getString("notes"))) {
                            slotParms.sendShadyCanyonNotesEmail = true;
                        }
                    }

                    pstmtTemp.close();

                } catch (Exception exc) {
                    slotParms.sendShadyCanyonNotesEmail = false;
                }
            }
        }
        
        if (club.equals("bishopsgategc") && (slotParms.members > 0 || slotParms.guests > 0) && slotParms.custom_int == 0
                && (slotParms.player1.isEmpty() || slotParms.p91 == 1) 
                && (slotParms.player2.isEmpty() || slotParms.p92 == 1) 
                && (slotParms.player3.isEmpty() || slotParms.p93 == 1) 
                && (slotParms.player4.isEmpty() || slotParms.p94 == 1) 
                && (slotParms.player5.isEmpty() || slotParms.p95 == 1)) {
            
            slotParms.custom_int = -1;
        }


        //
        //  Verification complete -
        //  Update the tee slot in teecurr
        //
        
        if(slotParms.saveSlot(req) == 0){
            buildError("Unknown Error Saving Slot", "Encountered an unknown error when saving this slot.  Please try again, or contact the golf shop if this continues.", postback);
            return;
        }
        
        
        /*
        try {

            PreparedStatement pstmt6 = con.prepareStatement(
                    "UPDATE teecurr2 "
                    + "SET last_mod_date = now(), "
                    + "player1 = ?, player2 = ?, player3 = ?, player4 = ?, "
                    + "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, "
                    + "p2cw = ?, p3cw = ?, p4cw = ?, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, "
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

            pstmt6.setLong(72, slotParms.date);
            pstmt6.setInt(73, slotParms.time);
            pstmt6.setInt(74, slotParms.fb);
            pstmt6.setString(75, slotParms.course);
            pstmt6.executeUpdate();      // execute the prepared stmt

            pstmt6.close();

        } catch (Exception e6) {

            msg = "Update Tee Time. ";

            dbError(e6, msg, postback); 
            return;
        }
        */
        
        // 
        //   Remove this tee time from the user's session (Not here!  Caller of verify method will now do this)
        //
        //verifySlot.clearInSession(slotParms.date, slotParms.time, slotParms.fb, slotParms.course, session);
        
        

        //
        //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
        //
        if (slotParms.oldPlayer1.equals("") && slotParms.oldPlayer2.equals("") && slotParms.oldPlayer3.equals("")
                && slotParms.oldPlayer4.equals("") && slotParms.oldPlayer5.equals("")) {

            //  new tee time
            SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                    slotParms.player4, slotParms.player5, user, fullName, 0, con);

        } else {

            //  update tee time
            SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
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
        //  (We can still do this here, since teecur2 is INNODB, and rollback will undo this)
        //
        if (club.equals("hazeltine") || club.equals("moselemsprings") || club.equals("bishopsgategc")) {      // if Hazeltine National

            verifySlot.Htoggle(slotParms.date, slotParms.time, slotParms.fb, slotParms, con);
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
        //  confirm reservation for user
        //
        if(!postback.group_confirm_type.isEmpty()){
            // We wont process the request fully if we've have started a confirmation prompt
            return;
        }
        
        
        
        //
        //   Check for Shotgun Event
        //
        String stime = timeUtil.get12HourTime(slotParms.time);                // time of this tee time
        
        if (!slotParms.event.equals("") && slotParms.event_type == 1) {       // if shotgun event during this time
                    
            int shotgunTime = Utilities.getEventTime(slotParms.event, con);   // get the actual time of the shotgun
            
            stime = timeUtil.get12HourTime(shotgunTime);                      // convert to string (i.e.  8:00 AM)
        }
        
        
        
        postback.successful = true;
        postback.back_to_slotpage = false;
        postback.title = "Member Tee Slot Page";
        
        postback.group_response.put("thanks","Thank you!");

            if (cancelTime) {
                
                postback.group_response.put("canceled"+slotParms.group_index,"The "+ stime +" reservation has been cancelled.");

            } else {
                
                //if(slotParms.hasOldPlayers()){
                //    postback.group_response.put("confirmed","Changes to your "+timeUtil.get12HourTime(slotParms.time) +" reservation has been accepted and processed.");
                //} else {
                    postback.group_response.put("confirmed"+slotParms.group_index,"Your "+ stime +" reservation has been accepted and processed.");
                //}

                if (xcount > 0 && slotParms.club_parm.xhrs > 0) {            // if any X's were specified

                    if (club.equals("castlepines")) {
                        
                        postback.group_response.put("x-notice",
                                "All player positions reserved by an X must be filled by 12:00 pm. two days prior to the reserved tee time.<br>"
                                + "If not, the system will automatically remove the X.");
                        
                    } else {
                        
                        postback.group_response.put("x-notice",
                                "All player positions reserved by an 'X' must be filled within " + slotParms.club_parm.xhrs + " hours of the tee time.<br>"
                                + "If not, the system will automatically remove the X.");
                       
                    }
                }

                if (club.equals("piedmont")) {        // if Piedmont Driving Club

                    int piedmontStatus = verifySlot.checkPiedmont(slotParms.date, slotParms.time, slotParms.day);     // check if special time

                    if (piedmontStatus == 1) {      // if Sat or Sun and before noon
                        
                        postback.group_response.put("pieadmon-1",
                                "<b>Notice From Golf Shop:</b>&nbsp;&nbsp;Please be aware that your group will be assigned "
                                + "a caddie or forecaddie regardless of the number of players.");

                    } else if (piedmontStatus == 2) {      // if other special time

                        postback.group_response.put("pieadmon-2",
                                "<b>Notice From Golf Shop:</b>&nbsp;&nbsp;If this tee time becomes a threesome or "
                                + "foursome and a caddie is not already requested, a forecaddie will be assigned "
                                + "to your group.");

                    } else if (piedmontStatus == 4) {

                        postback.group_response.put("pieadmon-2",
                                "<b>Notice From Golf Shop:</b>&nbsp;&nbsp;If this tee time includes two or more guests "
                                + "and a caddie is not already requested, a forecaddie will be assigned to your group.");

                    }
                }

                if (club.equals("charlottecc") && slotParms.guests >= 2) {        // If Charlotte CC and 2 or more guests

                    postback.group_response.put("charlottecc-1",
                                "<b>Notice from Golf Professional Staff:</b><br>"
                                + "\"Any golf group that has two (2) or more Guests must have a caddie if walking, "
                            + "or a forecaddie if riding in golf carts.\" - Thank You");
                }
                
              

                if (notesL > 254) {
                    postback.group_response.put("note-size",
                                "<b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.");
                }
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
        out.println("<p><H2>Unauthorized Access</H2><BR></p>");
        out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\">");
        out.println("ForeTees has detected that you have accessed a tee time prior to the parameters set");
        out.println("<p>forth by your club. As a result you will be logged off and have to log on again.</p>");
        out.println("</td></tr></table><br>");
        out.println("<p><font size=\"2\"></p>");
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

    

    /*
    // ************************************************************************
    //  Get number of days between today and the date provided
    // ************************************************************************
    private int getDaysBetween(long date) {

        return Utilities.getDaysBetween(date);    // Method moved to Utilities for global use

    }       // end of getDaysBetween
*/
    // ************************************************************************
    //  Process cancel request (Return w/o changes) from Member_slot (HTML)
    // ************************************************************************
    private void cancel(HttpServletRequest req, PrintWriter out, String club, Connection con, HttpSession session) {
     
        String user = (String) session.getAttribute("user");
        
        //StringBuilder testTime = new StringBuilder();
        
        List<String> teecurr_ids_enc = formUtil.getStringListFromReq(req, "teecurr_id%");

        // Initialize and pre-load slot parms for verification
        for (String enc_id : teecurr_ids_enc) {
            Long id = Utilities.decryptTTdataLong(enc_id);
            parmSlot parm = new parmSlot(id, req);

            //
            //  Clear the 'in_use' flag for this time slot in teecurr
            //
            //boolean clearResult = verifySlot.clearInUse(date, time, fb, course, user, con, session);
            //testTime.append((clearResult?"yes":"no")+";");
            
            verifySlot.clearInUse(parm.date, parm.time, parm.fb, parm.course, user, con, session);

            //
            //  If Hazeltine National, then check for an associated tee time (w/e's and holidays)
            //
            if (club.equals("hazeltine") || club.equals("moselemsprings") || club.equals("bishopsgategc")) {      // if Hazeltine National

                verifySlot.HclearInUse(parm.date, parm.time, parm.fb, parm.course, parm.day, club, parm.teecurr_id, con);
            }
        }
            
        //
        //  Prompt user to return to Member_sheet or Member_teelist (index = 999)
        //  TODO:  Change this to json

            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Member Tee Slot Page</Title>");
            out.println("</HEAD>");
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            //out.println("<b>"+testTime.toString()+"</b><br>");
            out.println("<p><H3>Return/Cancel Requested</H3></p>");
            out.println("<p>Thank you, the time slot has been returned to the system without changes.</p>");
            out.println("<p><BR></p>");

            out.println("<font size=\"2\">");
            out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
            //out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");

            out.println("</CENTER></BODY></HTML>");

  
        out.close();
    }

    private void unlockSlots(List<parmSlot> slotList, HttpServletRequest req){
        
        Connection con = Connect.getCon(req);
        //String club = reqUtil.getSessionString(req, "club", "");
        String user = reqUtil.getSessionString(req, "user", "");
        HttpSession session = req.getSession(false);
        
        for (parmSlot rParm : slotList) {
            verifySlot.clearInUse(rParm.date, rParm.time, rParm.fb, rParm.course, user, con, session);
        }

    }
   
    private parmSlotPage processRequest(HttpServletRequest req) {

        Connection con = Connect.getCon(req);
        String club = reqUtil.getSessionString(req, "club", "");
        String user = reqUtil.getSessionString(req, "user", "");
        //HttpSession session = req.getSession(false);
        
        String id_hash = reqUtil.getParameterString(req, "id_hash", "");

        parmSlotPage slotPageParms = new parmSlotPage();
        slotPostBack postback = new slotPostBack();
        slotPageParms.process_postback = postback;
        postback.successful = false;
        postback.title = "Unknown Condition"; // this will(should) always change

        List<String> teecurr_ids_enc = formUtil.getStringListFromReq(req, "teecurr_id%");
        List<Long> teecurr_ids = new ArrayList<Long>();

        List<parmSlot> slotList = new ArrayList<parmSlot>();

        int players = teecurr_ids_enc.size() * 5;
        
        boolean lastACState = Connect.getAutoCommitMode(con);
        Connect.setAutoCommitMode(con, false);

        // Check if our teecurr_ids were tampered with
        if(!Utilities.encryptTTdata(StringUtils.join(teecurr_ids_enc,":")).equals(id_hash)){
            // Bad data from client (probably a hack attempt -- the hash of ids did not match the ids sent)
            postback.back_to_slotpage = false; // We can't return to the slotpage.
            postback.title = "Invalid Tee Time ID given.";
            postback.notice_list.add("An invalid tee time ID was detected.  Unable to process request.  If this problem continues, please contact the golf shop.  This activity has been logged.");
            Utilities.logError("Member_slot.processRequest - Tampered teecurr ID or hash detected.  Possible hack attempt. Club:" + club + ",  User:" + user, con);

            Connect.rollback(con);
            Connect.setAutoCommitMode(con, lastACState);
            //unlockSlots(slotList, req);
            return slotPageParms;
        }
        
        // Initialize and pre-load slot parms for verification
        int i = 0;
        for (String enc_id : teecurr_ids_enc) {
            Long id = Utilities.decryptTTdataLong(enc_id);
            if (id == null) {
                // Bad data from client (probably a hack attempt -- the encrypted id failed to decrypt or decode to long)
                postback.back_to_slotpage = false; // We can't return to the slotpage.
                postback.title = "Malformed Tee Time ID given.";
                postback.notice_list.add("A malformed tee time ID was detected.  Unable to process request.  If this problem continues, please contact the golf shop.  This activity has been logged.");
                Utilities.logError("Member_slot.processRequest - Malformed teecurr ID detected.  Possible hack attempt. Club:" + club + ",  User:" + user, con);

                Connect.rollback(con);
                Connect.setAutoCommitMode(con, lastACState);
                //unlockSlots(slotList, req);
                return slotPageParms;

            } else {
                // Seems to be a good id
                teecurr_ids.add(id);
                parmSlot parm = new parmSlot(id, req);
                parm.group_slots = slotList;
                parm.group_index = i;
                
                //if(club.equals("oakhillcc") && req.getParameter("custom_int") != null){
                //    parm.custom_int = reqUtil.getParameterInteger(req, "custom_int", 0);
               //}
                
                if (parm.teecurr_id != id) {
                    // Couldn't load the teecurr.  Could be a database error, could be an invalid teecurr id
                    postback.title = "Unable to load Tee Time.";
                    postback.notice_list.add("Sorry, there was an issue processing tee time id #" + id + ".  This may be a temporary condition, please try again.  If this problem continues, please contact the golf shop.");
                    Utilities.logError("Member_slot.processRequest - Error loading teecurr ID " + id + ";  Club:" + club + ",  User:" + user, con);

                    Connect.rollback(con);
                    Connect.setAutoCommitMode(con, lastACState);
                    return slotPageParms;

                } else {
                    // Looks like the tee time loaded.  Let's add it to our list and configure it for the verify process.
                    slotList.add(parm);
                }
            }
            i++;
        }
        
        // Set Access/Option/Custom settings for slot group 
        // (This HAS to be done before loading form data, but after loading slot group from DB)
        if(slotList.size() > 0){
            slotList.get(0).setAccessOnGroup(req);
        } else {
            // Shouldn't ever get here.
            postback.title = "Unable to load Tee Time";
            postback.notice_list.add("Sorry, there was an unknown issue processing tee times.  This may be a temporary condition, please try again.  If this problem continues, please contact the golf shop.");
            Utilities.logError("Member_slot.processRequest - Error loading time slots;  Club:" + club + ",  User:" + user, con);

            Connect.rollback(con);
            Connect.setAutoCommitMode(con, lastACState);
            return slotPageParms;
        }
        
        // Load posted data into slot parms
        for (parmSlot parm : slotList) {
            
            parm.setOld(); // Move existing entries to "old" for later comparison.
            int start = parm.group_index * 5;

            parm.fillHndcp(99,5);  // init handicaps
            //
            // Get all the parameters entered
            //
            parm.notes = req.getParameter("notes").trim();  // (parm.group_index == 0 ? req.getParameter("notes").trim() : "");    // Notes
            parm.hides = req.getParameter("hide");            // Hide Notes
//            if(club.equals("oakhillcc") && req.getParameter("custom_int") != null){
//                parm.custom_int = reqUtil.getParameterInteger(req, "custom_int", 0);
//            }

            parm.setPlayer(formUtil.getStringArrayFromReq(req, "player%", start, 5, ""));
            parm.setUser(formUtil.getStringArrayFromReq(req, "user%", start, 5, ""));
            parm.setCw(formUtil.getStringArrayFromReq(req, "p%cw", start, 5, ""));
            parm.setGuestId(formUtil.getIntArrayFromReq(req, "guest_id%", start, 5, 0));
            parm.setP9(formUtil.getIntArrayFromReq(req, "p9%", start, 5, 0));
            
            if(parm.use_gift_pack){
                parm.setCustomDisp(formUtil.getStringArrayFromReq(req, "gift_pack%", start, 5, ""));
            } else {
                parm.setCustomDisp(formUtil.getStringArrayFromReq(req, "custom_disp%", start, 5, ""));
            }
  
        }

        // Verify and process the tee times
        for (parmSlot parm : slotList) {
            
            postback.successful = false;
            postback.message_list.clear();
            postback.notice_list.clear();
            postback.callback_form_list.clear();
            postback.warning_list.clear();
            //postback.continue_loop_on_error = false;
            
            // Configure callback map that will be used by any postbacks with the option to continue.
            // Should be benign for any that do't
            postback.callback_map.clear();
            postback.callback_form_list.clear();
            postback.callback_map.put("submitted_players", players);
            postback.callback_map.put("teecurr_id%", teecurr_ids_enc);
            postback.callback_map.put("id_hash", id_hash);
            postback.callback_map.put("hide", req.getParameter("hide"));
            postback.callback_map.put("notes", req.getParameter("notes"));
            postback.callback_map.put("player%", formUtil.getStringListFromReq(req, "player%"));
            postback.callback_map.put("user%", formUtil.getStringListFromReq(req, "user%"));
            postback.callback_map.put("p%cw", formUtil.getStringListFromReq(req, "p%cw"));
            postback.callback_map.put("p9%", formUtil.getIntegerListFromReq(req, "p9%"));
            postback.callback_map.put("guest_id%", formUtil.getIntegerListFromReq(req, "guest_id%"));
            if (req.getParameter("custom_int") != null) {
                postback.callback_map.put("custom_int", reqUtil.getParameterInteger(req, "custom_int", 0));
            }
            if(req.getParameter("ack_remove") != null){
                postback.callback_map.put("ack_remove", req.getParameter("ack_remove"));
            }
            if(req.getParameter("skip8") != null){
                postback.callback_map.put("skip8", req.getParameter("skip8"));
            }
            if(parm.use_gift_pack){
                postback.callback_map.put("gift_pack%", reqUtil.getParameterInteger(req, "gift_pack%", 0));
            } else {
                postback.callback_map.put("custom_disp%", formUtil.getStringListFromReq(req, "custom_disp%"));
            }
            
            if (!parm.userHasLock(req)) {
                // We don't own this slot any longer.  We'll need to release all of them and notify
                postback.back_to_slotpage = false; // We can't return to the slotpage.
                postback.title = "Reservation Timer Expired";
                postback.notice_list.add("Sorry, your exclusive lock on this request has timed out.</p><p>Please try again.</p>");
                //postback.notice_list.add(parm.last_user+":"+parm.time);
                //postback.notice_list.add(verifySlot.checkInSession(parm.date, parm.time, parm.fb, parm.course, session)?"in session":"not in session");
                // At least one of our slots are in use by a different user.  Unlock the slots we may have already locked
                Connect.rollback(con);
                Connect.setAutoCommitMode(con, lastACState);
                unlockSlots(slotList, req);
                return slotPageParms;
            }

            // process reservation requests request
            verify(req, parm, postback);                 
            if (!postback.successful && !postback.continue_loop_on_error) {
                // Things didn't go well, or we need confirmation.  Pass it on to the user.
                // If we're not returning to thwe slot page, free up the slot
                Connect.rollback(con);
                Connect.setAutoCommitMode(con, lastACState);
                if(!postback.back_to_slotpage){
                    unlockSlots(slotList, req);
                }
                
                return slotPageParms;
            }

        }

        // Check if there was a group response error/prompt
        if (!postback.group_confirm_type.isEmpty() || postback.continue_loop_on_error) {
            postback.title = postback.group_confirm_title;
            postback.group_confirm_title = "";
            // There's been some errors/confirmation requests
            // prompt the user.
            if(!postback.group_confirm_header.isEmpty()){
                postback.notice_list.add(postback.group_confirm_header);
                postback.group_confirm_header = "";
            }
            postback.notice_list.addAll(postback.group_confirm_list);
            if(!postback.group_confirm_footer.isEmpty()){
                postback.notice_list.add(postback.group_confirm_footer);
                postback.group_confirm_footer = "";
            }
            postback.group_confirm_list.clear();
            postback.callback_map.putAll(postback.group_confirm_callback_map);
            postback.group_confirm_callback_map.clear();
            postback.prompt_close_continue = postback.group_confirm_close_continue;
            postback.prompt_yes_no = postback.group_confirm_yes_no;
            postback.back_to_slotpage = postback.group_confirm_back_to_slotpage;
            Connect.rollback(con);
            Connect.setAutoCommitMode(con, lastACState);
            return slotPageParms;
        }

        // If we got here, the slots must have been processed O.K.
        Connect.commit(con); // Commit all changes to the database
        Connect.setAutoCommitMode(con, lastACState); // Revert autocommit mode to what it was before we started
        unlockSlots(slotList, req); // Release this user's lock on the slots.
        // Configure the response
        // group_response map was used to deduplicate messages from verify
        for(String message : postback.group_response.values()){
            postback.message_list.add(message);
        }
        postback.group_response.clear();
        
        // Now let's send notification emails, if needed.
        for (parmSlot parm : slotList) {

            boolean sendCustomEmail = false;
            
            // Get map of players that had a change in thier CW option
            Map<Integer, Integer> cwChanged = parm.getModifiedCw();
            Set<Integer> newPlayers = parm.getNew();
            Set<Integer> removedPlayers = parm.getRemoved();
            
            // Check them
            Set<String> caddieTypes = new HashSet<String>();

            if(club.equals("baltimore") || club.equals("riverside") || club.equals("hallbrookcc")){
                
                caddieTypes.add("CAD");
                
            } else if(club.equals("rehobothbeachcc")){
                
                caddieTypes.add("CAD");
                caddieTypes.add("CFC");
                
            } else if(club.equals("silverleaf")){
                
                caddieTypes.add("CAD");
                caddieTypes.add("FOR");
                
            }
            
            if (caddieTypes.size() > 0) {
                // Check if any of the modified players apply
                Integer pi;
                Integer oi;
                String newCw;
                String oldCw;
                for (Map.Entry<Integer, Integer> e : cwChanged.entrySet()) {
                    pi = e.getKey();
                    oi = e.getValue();
                    newCw = parm.getCw(pi);
                    oldCw = parm.getOldCw(oi);
                    if (
                            (caddieTypes.contains(newCw) && !oldCw.equals(newCw)) // Caddie requested, or type changed
                            || 
                            (caddieTypes.contains(oldCw) && !newCw.equals(oldCw)) // Caddie was removed
                            ) {
                        sendCustomEmail = true;
                    }
                }
                // Check if any of the new players apply
                for (Integer i2 : newPlayers){
                    if(caddieTypes.contains(parm.getCw(i2))){
                        sendCustomEmail = true;
                    }
                }
                // Check if any removed players apply
                for (Integer i2 : removedPlayers){
                    if(caddieTypes.contains(parm.getOldCw(i2))){
                        sendCustomEmail = true;
                    }
                }
            }
            

/*
            if (club.equals("baltimore") || club.equals("riverside")) {

                if ((parm.p1cw.equals("CAD") && !parm.oldp1cw.equals("CAD"))
                        || (parm.p2cw.equals("CAD") && !parm.oldp2cw.equals("CAD"))
                        || (parm.p3cw.equals("CAD") && !parm.oldp3cw.equals("CAD"))
                        || (parm.p4cw.equals("CAD") && !parm.oldp4cw.equals("CAD"))
                        || (parm.p5cw.equals("CAD") && !parm.oldp5cw.equals("CAD"))) {    // if new caddie requested

                    sendCustomEmail = true;

                } else {      // check if any caddies were removed

                    if ((!parm.p1cw.equals("CAD") && parm.oldp1cw.equals("CAD"))
                            || (!parm.p2cw.equals("CAD") && parm.oldp2cw.equals("CAD"))
                            || (!parm.p3cw.equals("CAD") && parm.oldp3cw.equals("CAD"))
                            || (!parm.p4cw.equals("CAD") && parm.oldp4cw.equals("CAD"))
                            || (!parm.p5cw.equals("CAD") && parm.oldp5cw.equals("CAD"))) {    // if caddie changed

                        sendCustomEmail = true;
                    }
                }

            } else if (club.equals("rehobothbeachcc")) {

                if (((parm.p1cw.equals("CAD") || parm.p1cw.equals("CFC")) && !parm.oldp1cw.equals("CAD") && !parm.oldp1cw.equals("CFC"))
                        || ((parm.p2cw.equals("CAD") || parm.p2cw.equals("CFC")) && !parm.oldp2cw.equals("CAD") && !parm.oldp2cw.equals("CFC"))
                        || ((parm.p3cw.equals("CAD") || parm.p3cw.equals("CFC")) && !parm.oldp3cw.equals("CAD") && !parm.oldp3cw.equals("CFC"))
                        || ((parm.p4cw.equals("CAD") || parm.p4cw.equals("CFC")) && !parm.oldp4cw.equals("CAD") && !parm.oldp4cw.equals("CFC"))
                        || ((parm.p5cw.equals("CAD") || parm.p5cw.equals("CFC")) && !parm.oldp5cw.equals("CAD") && !parm.oldp5cw.equals("CFC"))) {    // if new caddie requested

                    sendCustomEmail = true;

                } else if ((!parm.p1cw.equals("CAD") && !parm.p1cw.equals("CFC") && (parm.oldp1cw.equals("CAD") || parm.oldp1cw.equals("CFC")))
                        || (!parm.p2cw.equals("CAD") && !parm.p2cw.equals("CFC") && (parm.oldp2cw.equals("CAD") || parm.oldp2cw.equals("CFC")))
                        || (!parm.p3cw.equals("CAD") && !parm.p3cw.equals("CFC") && (parm.oldp3cw.equals("CAD") || parm.oldp3cw.equals("CFC")))
                        || (!parm.p4cw.equals("CAD") && !parm.p4cw.equals("CFC") && (parm.oldp4cw.equals("CAD") || parm.oldp4cw.equals("CFC")))
                        || (!parm.p5cw.equals("CAD") && !parm.p5cw.equals("CFC") && (parm.oldp5cw.equals("CAD") || parm.oldp5cw.equals("CFC")))) {    // check if any caddies were removed

                    sendCustomEmail = true;

                } else if ((parm.p1cw.equals("CAD") && parm.oldp1cw.equals("CFC")) || (parm.p1cw.equals("CFC") && parm.oldp1cw.equals("CAD"))
                        || (parm.p2cw.equals("CAD") && parm.oldp2cw.equals("CFC")) || (parm.p2cw.equals("CFC") && parm.oldp2cw.equals("CAD"))
                        || (parm.p3cw.equals("CAD") && parm.oldp3cw.equals("CFC")) || (parm.p3cw.equals("CFC") && parm.oldp3cw.equals("CAD"))
                        || (parm.p4cw.equals("CAD") && parm.oldp4cw.equals("CFC")) || (parm.p4cw.equals("CFC") && parm.oldp4cw.equals("CAD"))
                        || (parm.p5cw.equals("CAD") && parm.oldp5cw.equals("CFC")) || (parm.p5cw.equals("CFC") && parm.oldp5cw.equals("CAD"))) {    // check if changed from one caddie type to the other.

                    sendCustomEmail = true;
                }

            } else if (club.equals("silverleaf")) {

                if (((parm.p1cw.equals("CAD") || parm.p1cw.equals("FOR")) && !parm.oldp1cw.equals("CAD") && !parm.oldp1cw.equals("FOR"))
                        || ((parm.p2cw.equals("CAD") || parm.p2cw.equals("FOR")) && !parm.oldp2cw.equals("CAD") && !parm.oldp2cw.equals("FOR"))
                        || ((parm.p3cw.equals("CAD") || parm.p3cw.equals("FOR")) && !parm.oldp3cw.equals("CAD") && !parm.oldp3cw.equals("FOR"))
                        || ((parm.p4cw.equals("CAD") || parm.p4cw.equals("FOR")) && !parm.oldp4cw.equals("CAD") && !parm.oldp4cw.equals("FOR"))
                        || ((parm.p5cw.equals("CAD") || parm.p5cw.equals("FOR")) && !parm.oldp5cw.equals("CAD") && !parm.oldp5cw.equals("FOR"))) {    // if new caddie requested

                    sendCustomEmail = true;

                } else if ((!parm.p1cw.equals("CAD") && !parm.p1cw.equals("FOR") && (parm.oldp1cw.equals("CAD") || parm.oldp1cw.equals("FOR")))
                        || (!parm.p2cw.equals("CAD") && !parm.p2cw.equals("FOR") && (parm.oldp2cw.equals("CAD") || parm.oldp2cw.equals("FOR")))
                        || (!parm.p3cw.equals("CAD") && !parm.p3cw.equals("FOR") && (parm.oldp3cw.equals("CAD") || parm.oldp3cw.equals("FOR")))
                        || (!parm.p4cw.equals("CAD") && !parm.p4cw.equals("FOR") && (parm.oldp4cw.equals("CAD") || parm.oldp4cw.equals("FOR")))
                        || (!parm.p5cw.equals("CAD") && !parm.p5cw.equals("FOR") && (parm.oldp5cw.equals("CAD") || parm.oldp5cw.equals("FOR")))) {    // check if any caddies were removed

                    sendCustomEmail = true;

                } else if ((parm.p1cw.equals("CAD") && parm.oldp1cw.equals("FOR")) || (parm.p1cw.equals("FOR") && parm.oldp1cw.equals("CAD"))
                        || (parm.p2cw.equals("CAD") && parm.oldp2cw.equals("FOR")) || (parm.p2cw.equals("FOR") && parm.oldp2cw.equals("CAD"))
                        || (parm.p3cw.equals("CAD") && parm.oldp3cw.equals("FOR")) || (parm.p3cw.equals("FOR") && parm.oldp3cw.equals("CAD"))
                        || (parm.p4cw.equals("CAD") && parm.oldp4cw.equals("FOR")) || (parm.p4cw.equals("FOR") && parm.oldp4cw.equals("CAD"))
                        || (parm.p5cw.equals("CAD") && parm.oldp5cw.equals("FOR")) || (parm.p5cw.equals("FOR") && parm.oldp5cw.equals("CAD"))) {    // check if changed from one caddie type to the other.

                    sendCustomEmail = true;
                }
            }
*/
            // Send email to staff member if new player is guest
            if (club.equals("pradera") || club.equals("pinery") || club.equals("tartanfields")) {
                for(Integer i2 : newPlayers){
                    if(!parm.getG(i2).isEmpty()){
                        sendCustomEmail = true;
                    }
                }
            }
            // Send email to staff member if a guest was removed
            if (club.equals("pradera") || club.equals("pinery") || club.equals("tartanfields")) {
                for(Integer i2 : removedPlayers){
                    if(parm.getOldUser(i2).isEmpty()){
                        sendCustomEmail = true;
                    }
                }
            }

            /*
            // Send email to staff member if a guest is, or was, present in a tee time.
            if (club.equals("oakmont") || club.equals("pradera") || club.equals("pinery") || club.equals("tartanfields")) {

                if (!parm.g1.equals("") || !parm.g2.equals("") || !parm.g3.equals("") || !parm.g4.equals("") || !parm.g5.equals("")
                        || (!parm.oldPlayer1.equals("") && parm.oldUser1.equals("") && !parm.oldPlayer1.equalsIgnoreCase("x"))
                        || (!parm.oldPlayer2.equals("") && parm.oldUser2.equals("") && !parm.oldPlayer2.equalsIgnoreCase("x"))
                        || (!parm.oldPlayer3.equals("") && parm.oldUser3.equals("") && !parm.oldPlayer3.equalsIgnoreCase("x"))
                        || (!parm.oldPlayer4.equals("") && parm.oldUser4.equals("") && !parm.oldPlayer4.equalsIgnoreCase("x"))
                        || (!parm.oldPlayer5.equals("") && parm.oldUser5.equals("") && !parm.oldPlayer5.equalsIgnoreCase("x"))) {

                    sendCustomEmail = true;
                }
            }*/

            //
            //***********************************************
            //  Send email notification if necessary
            //***********************************************
            //
            if (parm.sendemail != 0 || club.equals("pmarshgc") || sendCustomEmail) {

                //
                //  allocate a parm block to hold the email parms
                //
                parmEmail parme = new parmEmail();          // allocate an Email parm block

                //
                //  Set the values in the email parm block
                //
                parme.activity_id = 0;
                parme.club = club;
                parme.guests = parm.guests;
                parme.type = "tee";         // type = tee time
                parme.date = parm.date;
                parme.time = parm.time;
                parme.fb = parm.fb;
                parme.mm = parm.mm;
                parme.dd = parm.dd;
                parme.yy = parm.yy;
                parme.etype = 0;

                //
                //  If tee time is part of a shotgun event, then change the time and indicate its a shotgun
                //
                if (!parm.event.equals("") && parm.event_type == 1) {

                    parme.time = Utilities.getEventTime(parm.event, con);   // get the actual time of the shotgun

                    parme.etype = 1;       // indicate shotgun tee time
                }

                parme.user = user;
                parme.orig_by = parm.orig_by;
                parme.emailNew = parm.emailNew;
                parme.emailMod = parm.emailMod;
                parme.emailCan = parm.emailCan;

                parme.course = parm.course;
                parme.day = parm.day;
                parme.notes = parm.notes;

                parme.setP9(parm.getP9Array(5));
                parme.setPlayer(parm.getPlayerArray(5));
                parme.setUser(parm.getUserArray(5));
                parme.setCw(parm.getCwArray(5));
                parme.setGuestId(parm.getGuestIdArray(5));
                parme.setUserg(parm.getUsergArray(5));
                
                parme.setOldPlayer(parm.getOldPlayerArray(5));
                parme.setOldUser(parm.getOldUserArray(5));
                parme.setOldCw(parm.getOldCwArray(5));
                parme.setOldGuestId(parm.getOldGuestIdArray(5));

                //
                //  Send the email
                //
                if (parm.sendemail != 0) {
                    sendEmail.sendIt(parme, con);      // in common (include sendemail verification since customs can enter with it set to 0)
                }

                //
                //  If Hallbrook CC, then check for any caddies in the tee time - if so, send an email to Caddie Master
                //
                /* Done above now
                if (club.equals("hallbrookcc")) {

                    if ((parm.p1cw.equals("CAD") && !parm.oldp1cw.equals("CAD"))
                            || (parm.p2cw.equals("CAD") && !parm.oldp2cw.equals("CAD"))
                            || (parm.p3cw.equals("CAD") && !parm.oldp3cw.equals("CAD"))
                            || (parm.p4cw.equals("CAD") && !parm.oldp4cw.equals("CAD"))
                            || (parm.p5cw.equals("CAD") && !parm.oldp5cw.equals("CAD"))) {    // if new caddie requested

                        sendEmail.sendOakmontEmail(parme, con, club);      // send an email to Caddie Master

                    } else {      // check if any caddies were removed

                        if ((!parm.p1cw.equals("CAD") && parm.oldp1cw.equals("CAD"))
                                || (!parm.p2cw.equals("CAD") && parm.oldp2cw.equals("CAD"))
                                || (!parm.p3cw.equals("CAD") && parm.oldp3cw.equals("CAD"))
                                || (!parm.p4cw.equals("CAD") && parm.oldp4cw.equals("CAD"))
                                || (!parm.p5cw.equals("CAD") && parm.oldp5cw.equals("CAD"))) {    // if caddie changed

                            sendEmail.sendOakmontEmail(parme, con, club);      // send an email to Caddie Master
                        }
                    }
                }
                */
                
                if (sendCustomEmail) {
                    sendEmail.sendOakmontEmail(parme, con, club);      // send custom email to staff
                }

                if (parm.congressGstEmail == true) {            // if guest found in cancelled tee time

                    sendEmail.sendCongressEmail(parme, con);      // send an email to Head Pro
                }

                if (parm.sendShadyCanyonNotesEmail) {

                    sendEmail.sendOakmontEmail(parme, con, club);
                }

                /*
                if (club.equals("oakhillcc") && req.getParameter("remove") != null) {   
                
                sendEmail.sendOakmontEmail(parme, con, club);   // Oak Hill CC - send email to Golf Chair whenever someone cancels a tee time   
                }*/

                if (club.equals("philcricketrecip") || club.equals("rollinghillscccarecip")) {

                    sendEmail.sendOakmontEmail(parme, con, club);   // Philly Cricket Recip Site - send emails for everything to Pro at selected course   
                }

                // Send notification to pros at Pelican Marsh if a member books a new tee time on the day of.
                if (club.equals("pmarshgc") && parm.emailNew != 0 && parm.date == timeUtil.getClubDate(req)) {
                    sendEmail.sendOakmontEmail(parme, con, club);
                }

                /*  Done above now
                if (club.equals("belfair") && parm.emailCan != 0) {

                    if ((parm.oldUser1.equals("") && !parm.oldPlayer1.equals("") && !parm.oldPlayer1.equalsIgnoreCase("X"))
                            || (parm.oldUser2.equals("") && !parm.oldPlayer2.equals("") && !parm.oldPlayer2.equalsIgnoreCase("X"))
                            || (parm.oldUser3.equals("") && !parm.oldPlayer3.equals("") && !parm.oldPlayer3.equalsIgnoreCase("X"))
                            || (parm.oldUser4.equals("") && !parm.oldPlayer4.equals("") && !parm.oldPlayer4.equalsIgnoreCase("X"))
                            || (parm.oldUser5.equals("") && !parm.oldPlayer5.equals("") && !parm.oldPlayer5.equalsIgnoreCase("X"))) {

                        sendEmail.sendOakmontEmail(parme, con, club);
                    }
                }
                 * 
                 */

            } else if (club.equals("shadycanyongolfclub") && parm.sendShadyCanyonNotesEmail) {    // end of IF sendemail


                //  Send a custom email message to Shady Canyon pro if notes have changed
                //  allocate a parm block to hold the email parms
                parmEmail parme = new parmEmail();          // allocate an Email parm block

                //  Set the values in the email parm block
                parme.type = "tee";         // type = tee time
                parme.date = parm.date;
                parme.time = parm.time;
                parme.fb = parm.fb;
                parme.mm = parm.mm;
                parme.dd = parm.dd;
                parme.yy = parm.yy;

                parme.user = user;
                parme.emailNew = parm.emailNew;
                parme.emailMod = parm.emailMod;
                parme.emailCan = parm.emailCan;

                parme.course = parm.course;
                parme.day = parm.day;
                parme.notes = parm.notes;
                
                parme.setP9(parm.getP9Array(5));
                parme.setPlayer(parm.getPlayerArray(5));
                parme.setUser(parm.getUserArray(5));
                parme.setCw(parm.getCwArray(5));
                parme.setGuestId(parm.getGuestIdArray(5));
                parme.setUserg(parm.getUsergArray(5));
                
                parme.setOldPlayer(parm.getOldPlayerArray(5));
                parme.setOldUser(parm.getOldUserArray(5));
                parme.setOldCw(parm.getOldCwArray(5));
                parme.setOldGuestId(parm.getOldGuestIdArray(5));

                sendEmail.sendOakmontEmail(parme, con, club);
            }
        }

        return slotPageParms;

    }



    // *********************************************************
    //  Prompt user when a different tee time is available.
    // *********************************************************
    private void promptOtherTime(parmSlot parm, parmSlotPage slotPageParms) {


        String stime = timeUtil.get12HourTime(parm.time);

        String sfb = Labels.fbText[parm.fb];

        boolean customPrompt = false;

        if (parm.club.equals("congressional")) {

            if (parm.ind > 9) {
                customPrompt = true;
            }
        }

        //
        //  Prompt the user to either accept the times available or return to the tee sheet
        //

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

    }          // end of promptOtherTime
    
    

    // ***************************************************************************************
    //   Display Error Msg based on user - used when we do not need to return to _lott page
    // ***************************************************************************************
    private void buildError(String title, String content, slotPostBack postback) {
        
        postback.title = title;
        postback.notice_list.add(content);
        postback.continue_loop_on_error = false;
        postback.prompt_close_continue = false;
        postback.prompt_yes_no = false;
        postback.back_to_slotpage = true;

    }

    // *********************************************************
    //  Database Error
    // *********************************************************
    private void dbError(Exception e1, String msg, slotPostBack postback) {

        postback.title = "Unexpected Database Error";
        postback.notice_list.add("Sorry, there has been an unexpected database error.");
        postback.notice_list.add("If problem persists, contact your club manager.");
        postback.notice_list.add("Process: " + msg + "<p>  Exception: </p>" + e1.getMessage());
        postback.continue_loop_on_error = false;
        postback.prompt_close_continue = false;
        postback.prompt_yes_no = false;
        
    }
    
    // *********************************************************
    //  Database Error Html
    // *********************************************************
    private void dbErrorStart(Exception e1, String msg, parmSlotPage slotPageParms) {
        
        slotPageParms.page_start_button_go_back = true;
        slotPageParms.page_start_title = "Database Access Error";
        slotPageParms.page_start_notifications.add("Unable to access the Database.<br>Please try again later.");
        slotPageParms.page_start_notifications.add("If problem persists, contact your club manager.");
        slotPageParms.page_start_notifications.add("Process: " + msg + "<br>  Exception: " + e1.getMessage());

    }

}
