/***************************************************************************************
 *   Proshop_sheet:  This servlet will process the 'View Tee Sheet' request from
 *                    the Proshop's Select page.
 *
 *
 *   called by:  Proshop_select (doPost) - via Proshop_jump
 *               Proshop_sheet (on a refresh)
 *               Proshop_sheet (on a print)
 *               Proshop_sheet (on a 'checkallin')
 *               Proshop_sheet (on a 'show notes')
 *               Proshop_slot (on a cancel) - via Proshop_jump
 *               Proshop_slot (on a return) -    "
 *               Proshop_lott (on a cancel) - via Proshop_jump
 *               Proshop_lott (on a return) -    "
 *
 *
 *   created: 1/04/2002   Bob P.
 *
 *   last updated:
 *
 *        8/02/10   Fort Collins CC (fortcollins) - Updated customs to include Fox Hill CC
 *        7/20/10   Perry Park CC (perryparkcc) - If any player has an mship of 'Annual Pass Member', color the cell for that player Crimson
 *        6/30/10   Cordillera - set default courses based on the user (case 1862).
 *        6/04/10   Remove the jump script and use the jump parm on the url string (built in Proshop_jump).
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        4/27/10   Cordillera - restore custom for lodge times - new owners changed their minds. (case 1830)
 *        4/26/10   Update the js that calls the Print Options Menu for AWM 5.3.824
 *        4/21/10   All clubs - if Back Tee change the BG color of the F/B column to yellow.  Do the same for the notes column if Notes are present.
 *        4/20/10   Oakley CC - if Back Tee change the BG color of the F/B column to yellow (case 1796).
 *        4/19/10   Updated check for guest tracking guests to make sure guest tracking is active before taking action.
 *        3/25/10   Interlachen - remove custom for gift packs (case 1369).
 *        3/25/10   Add temp custom for tahoedonner - teesheet export link in control panel
 *        3/21/10   Central Washington Chapter PGA (cwcpga) - Display ghin numbers behing player names.
 *        3/21/10   Guestdb - Guest names will be a link to config that guest if a guest_id is found (red with underline if required info is missing)
 *        3/12/10   Columbine CC (columbine) - Changed "hide 5th player" custom to not use the year when determining if 5th player should be hidden.
 *        2/18/10   Added Tee Sheet Notes feature and removed instructions to increase available space on top of page.
 *        2/12/10   Allow the Frost Delay link to appear if index < 3 (was if 0)
 *        2/07/10   Updated calendars to hilite the day the tee sheet is for in green.
 *        2/05/10   Added floating calendar
 *        2/04/10   Cordillera - remove custom for lodge times (new owners don't want it).
 *        1/22/10   Oakmont - make an exception for one day in the custom that checks for shotgun events (per Jason Marciniec's request).
 *       12/23/09   Morgan Run - change the colors of all 3 courses in ALL mode so they stands out more - mainly for outside staff (case 1762).
 *       12/15/09   Lottery - when setting the state and state 4 determined, check if lottery has actually been processed by SystemUtils.
 *       12/09/09   When looking for events only check those that are active.
 *       12/03/09   Champion Hills (championhills) - Allow up to 10 events in the legend and on sheet
 *       11/29/09   Check for activity id = 0 when gathering events and restrictions.
 *       11/28/09   The Forest CC - allow up to 10 events in legend and on sheet.
 *       11/25/09   Change the event, restriction and lottery save areas from individual strings to string arrays
 *                  and use 'count' values to determine how many are allowed.  This will make it much easier to 
 *                  customize the number that are displayed in the legend.
 *       11/12/09   Pass the returnCourse to Proshop_dsheet when calling to approve a lottery.
 *       11/10/09   Woodlands CC - change the color of the East Course in ALL mode so it stands out more - mainly for outside staff (case 1724).
 *       11/05/09   The Lakes (lakes) - hide 5th player position between Nov 1 and May 31 (case 1656).
 *       11/04/09   CC of Naples (ccnaples) - Update to previous custom.  Make sure note is blank for unaccomp times before weeding it out!
 *       10/27/09   Gallery GC (gallerygolf) - Add club to "Show Lottery Times" custom to make the Control Panel option available for use
 *        9/25/09   Add the time of the shotgun to the Shotgun button for times during a shotgun event (i.e. 8:30 Shotgun).
 *        9/24/09   Tualatin - add new column for "Group has Teed Off" for the group (case 1726).
 *        9/24/09   Show the checkin box to limited access users that do not have the authority to check players in.  They can now
 *                  see, but they still cannot check players in.  Requested by Bent Tree.
 *        9/14/09   Added link to custom report on the control panel for Mid Pacific (also available on demov4)
 *        9/10/09   Now passing mrest_id when using the Tee Sheet Legend link to edit a member type restriction
 *        8/28/09   If lottery specified for this day, add an option to Control Panel to show the tee times so pro can pre-book (case 1703).
 *        8/18/09   St Clair CC (stclaircc) - Added 'Emeritus' mship to custom pos charge code processing
 *        7/28/09   Sawgrass CC (sawgrass) - Display courses in order they were entered when -ALL- is selected && don't display abbreviations in Tee Sheet Legend
 *        7/14/09   Desert Forest GC (desertforestgolfclub) - Do not display abbreviations portion of the Tee Sheet Legend (case 1702).
 *        6/29/09   Royal Oaks Houston (royaloakscc) - Updated membership type charge codes for new weekend "Sports Club w/Golf" member rule
 *        6/17/09   Add a new check-in image to show when player is checked in and POS charges have been sent.
 *                  Also, add new value (3) for pos flags in teecurr to indicate charges processed and sent.
 *        6/09/09   Cherry Hills - fix proshop4 user custom - show the check-in image but do not allow them to select it.
 *        5/18/09   Oakland Hills CC - Add custom "guest bag tag" coloring for players who selected the option during booking (case 1453).
 *        5/14/09   Move the Pro-ShopKeeper POS processing to Proshop_sheet_checkin to go with the Check-in processing.
 *        5/13/09   Change Pay Now processing to use Proshop_sheet_checkin so we don't have to refresh the sheet on each click.
 *        5/12/09   Change Check-in processing to use Proshop_sheet_checkin so we don't have to refresh the sheet on each check-in.
 *                  Also, do not show the check-in boxes on future sheets unless custom pre-checkin used.
 *        4/23/09   Mount Vernon CC - Display last name first on the tee sheet.
 *        4/22/09   Beverly GC - Color the calendar days green to display what days members currently have access too for proshop ease of booking (case 1467).
 *        4/21/09   Chartwell GCC - Allow booking of consecutive tee times for shotgun event tee times (case 1556).
 *        4/17/09   Castle Pines - Highlight player's name in lime if their birthday matches the date of one of their booked tee times (case 1607).
 *        4/03/09   Medinah - Remove 7:36 and 9:00 from Member Times list on weekdays, change 2:00 on weekdays
 *                            to an Outside Time - all on Course 3 (case 1631).
 *        4/03/09   Columbine CC - Hide the 5th player position on tee sheet between 4/01 and 9/30 (case 1640).
 *        4/01/09   Royal Oaks Houston - restore custom item codes for Jonas POS I/F (was commented out).
 *        3/30/09   St. Clair CC - Changed POS charge processing
 *        3/27/09   Southview - Highlight guest type "Join Me" on teesheet (case 1624).
 *        3/24/09   Forest Highlands - Hide the 5th player position on tee sheet between 4/25 and 9/30 (case 1613).
 *        3/12/09   Adjustment to how member notices are displayed to reduce teesheet stretching
 *        3/11/09   St. Clair CC (stclaircc) - Changed POS charge processing
 *        2/19/09   Add support for new ProShopKeeper Interface - Club Prophet Systems V3 (refer to Proshop_sheet_checkin).
 *       11/25/08   Changed format for checking suspensions for mrest display on the tee sheet legend.
 *       11/06/08   St. Clair CC (stclaircc) - Changed POS charge processing, added social mships and other missing mships
 *       10/08/08   Loxahatchee - Display usernames instead of member numbers when proshop user is set to display member numbers
 *        9/22/08   Replace any embedded single quotes in members' last names when building the pos string
 *                  for the PSK POS interface.  The single quotes caused a javascript error on the client.
 *        9/18/08   Charlotte CC - Display usernames instead of member numbers when proshop user is set to display member numbers
 *        9/14/08   Removed restrictions on new features
 *        9/10/08   Added member notice display banner for member notices using the teesheet and proside options.
 *        9/04/08   Only display POS Report link in Control Panel if proshop user has POS and REPORTS access
 *        9/03/08   Add support for the ClubSoft POS. 
 *        9/02/08   Various Limited Access Proshop User restriction adjustments for view only setup
 *        8/25/08   Portland GC - check for Walk-Up Only times (case 1527).
 *        8/18/08   Added handling for limited access proshop user Tee Sheet options (hdcp, mnum, bag)
 *        8/15/08   Correct link to Proshop_dsheet (was dshett) for lottery #3.
 *        8/15/08   IF consecutive tee times allowed and lottery - allow cons times if lottery has been proessed (state > 4).
 *        8/12/08   Stonebridge Ranch - limit the course options based on user (case 1530).
 *        8/07/08   Changed links from dlott to new dsheet
 *        8/01/08   Modified limited access proshop users checks (waitlist restrictions)
 *        7/24/08   Added limited access proshop users checks
 *        7/22/08   Add POS Report to Control Panel if today (case 1429).
 *        7/22/08   St. Clair CC (stclaircc) - Changed mships type for POS charges
 *        7/17/08   Log an entry in pos_hist table each time a POS charge is built.  For history and reports.  (case 1429)
 *        7/16/08   IBS POS (for AGC) - display the guest fee after the player name if player is a guest and there is an associated fee (case 1429).
 *        7/16/08   POS 'Pay Now' option - add 'Pay Now' and 'Paid' buttons so pro can mark a player as Paid if that player chooses to pay
 *                  up front.  In POS processing, do not include charges if player has already paid. (case 1429, items 1 - 4)
 *        7/14/08   American Golf (all their clubs) - add new column for "Group has Teed Off" for the group (case 1429, item 9).
 *        7/08/08   Add shading to tee sheet rows if time is covered by an active wait list

 *        7/17/08   Log an entry in pos_hist table each time a POS charge is built.  For history and reports.  (case 1429)
 *        7/16/08   IBS POS (for AGC) - display the guest fee after the player name if player is a guest and there is an associated fee (case 1429).
 *        7/16/08   POS 'Pay Now' option - add 'Pay Now' and 'Paid' buttons so pro can mark a player as Paid if that player chooses to pay
 *                  up front.  In POS processing, do not include charges if player has already paid. (case 1429, items 1 - 4)
 *        7/14/08   American Golf (all their clubs) - add new column for "Group has Teed Off" for the group (case 1429, item 9).
 *        7/22/08   St. Clair CC - Changed mships type for POS charges
 *        7/08/08   Add shading to tee sheet rows if time is covered by an active wait list
 *        7/07/08   Admirals Cove - Added custom to default to -ALL- courses. This now uses new fields in the club5
 *                  table for determining the default course.  Still a custom though as no config available yet.  (case 1513).
 *        7/07/08   When adding the tflag to player field, check for a user to avoid a tflag with no member name.
 *                  This is a band-aid fix until the real problem is resolved (tflag displayed in empty slots).
 *        6/30/08   Changes for wait list feature
 *        6/26/08   Do not include Season Long events in the legend.
 *        6/26/08   Always display the course name column for multi-course clubs (case 1509).
 *        6/11/08   Snoqualmie Ridge - proshop5 user setup for bag room staff - no edit access and show bag numbers (case 1501).
 *        6/07/08   Los Coyotes - change the flags added to names - use gender and pri/sec (case 1482).
 *        6/07/08   The CC - display member numbers on tee sheet (Case #1488)
 *        6/07/08   Mayfield Sandridge - default course to Sand Ridge for proshop1 (case 1491).
 *        6/06/08   Tavistock CC - Remove custom to Allow blockers starting with "GOLFSHOP" to show up on the tee sheet  (Case 1425)
 *        6/03/08   CC of Virginia - do not add the 9 or 18 hole indicator to item code if item code is empty.
 *        6/03/08   St. Clair CC - add custom mship item codes for Jonas POS I/F (case 1494).
 *        5/29/08   Patterson Club (and others) - update the reservation number for each tee time in the Jonas POS processing.
 *                                   This is necessary if the user wants to use the 'Quick Check-In' feature on the
 *                                   Jonas POS system.  Refer to the 'One Chit per Reservation Number' option on 
 *                                   Page 3 and the check-in process on Page 20 of the
 *                                   Jonas Club and Hotel - Point of Sale brief (TeeTimeReservationInterface.pdf).
 *        5/16/08   Royal Oaks Houston - add custom item codes for Jonas POS I/F.
 *        5/12/08   Show 'Approve Lottery' link in the Control Panel for all lstate > 3
 *        5/03/08   Beverly GC - set proshop5 user (bag room) as view only.
 *        5/02/08   Remove customs to display special chars after names - replaced by standard feature (case 1357).
 *        5/01/08   Ridge Club - display member numbers on tee sheet (Case #1466)
 *        5/01/08   Brackett's - display bag numbers on tee sheet (Case #1472)
 *        4/28/08   Add comment on customs where tflag has been configured for club and custom can soon be removed (case 1357).
 *        4/23/08   Add new feature to append special characters to player names using tflag (case 1357).
 *        4/22/08   Quick fix for the 'Approve Lottery' link in the Control Panel
 *        4/22/08   St. Clair CC - add custom POS codes for guest types based on course (case 1460).
 *        4/10/08   Brookings CC - Display Bag Storage Numbers on the Tee Sheet for proshop users (Case# 1441)
 *        4/07/08   CC of Virginia - adjust the list of guest types that do not get charged in POS Processing.
 *        4/02/08   Oak Hill CC - only allow proshop4 user to see the extra 5 minute interval tee times - 
 *                                show only the 15 min intervals to others (case 1422).
 *        4/02/08   Oak Hill CC - set default course to East Course (case 1433).
 *        3/13/08   Tavistock CC - Allow blockers starting with "GOLFSHOP" to show up on the tee sheet  (Case 1425)
 *        3/07/08   Tartan Fields - Display "*N" next to players with National mships (Case 1404).
 *        3/07/08   Eagle Creek - Display "_$" next to players with Renter mships (Case 1390).
 *        2/27/08   Restrict proshop5 user from demov4 site from some control panel options to allow ProshopKeeper 
 *                  access for testing their POS interface.
 *        2/21/08   Sonnenalp - set the background color of the time cell to yellow when specified guest types in tee time (case 1310).
 *        2/19/08   Interlachen - change background color of guest players that have the Gift Pack option (case 1369).
 *        2/05/05   CC of Birmingham - show up to 8 restrictions in the legend.
 *        1/29/08   CC of Jackson - Set default course - Case #1373
 *       12/17/07   Mediterra - Added custom to default to -ALL- courses (Case #1343)
 *       12/12/07   Submit the tee time form immediately when the number of tee consecutive tee times is selected (drop-down list).
 *       12/06/07   Berkeley Hall - proshop2 - display view-only tee sheet with member num #.
 *       12/03/07   Fix coloring for pace column on the tee sheet
 *       11/27/07   CC of Virginia - Add custom item codes for Abacus21 interface (for Reciprocal guests).
 *       11/25/07   Peninsula Club - Color the tmode cell according to mships - now finshed (Case 1079)
 *       11/13/07   Merion GC - display an H or a NR next to player names for House or Non-Resident (Case #1135)
 *       11/13/07   Valley Club - display an tag next to players depending on their mships (Case #1151)
 *       11/13/07   Sonnenalp - make the time button a different color if there are notes in the tee time.
 *       11/07/07   Eagle Creek - Display "_*" next to players with Social mships (Case 1285)
 *       11/06/07   Imperial GC - Added to default to -ALL- courses list
 *       10/24/07   Custom for Grandezza - proshop2 - display view-only tee sheet with member num #
 *       10/22/07   Custom for Orchid Island - proshop2 - display view-only tee sheet with member num #
 *       10/16/07   Scioto Reserve - custom for proshop2, 3, 4, & 5 (view only and mem nums)
 *       10/16/07   Medinah - change item codes for Guests on Course 3.
 *       10/06/07   Change the alignment for player names from center so the check-in boxes align.
 *       10/02/07   Medinah - change the item code for 2CA mode of trans.
 *       10/02/07   Pinnacle Peak - Jonas I/F - use posid instead of mnum.
 *        9/28/07   Oahu CC - customize the sorting on the tee sheet do accompany their double tees  (Case 1239)
 *        9/25/07   Peninsula Club - Color the tmode cell according to mships  (Case 1079)
 *        9/24/07   Mediterra CC - proshop5 - view only and display member numbers.
 *        9/21/07   St. Albans - Color the tmode TD cell if it's CAD  (Case 1190)
 *        9/21/07   Mediterra CC - Display "_SM" next to players with Sports mships (Case 1261)
 *        9/21/07   Palm Valley CC - Display "H/O" next to players with certain mships (Case 1243)
 *        9/21/07   CC of Virginia - Add custom item codes for Abacus21 interface (case 1254)
 *        9/18/07   Belle Haven CC - display the member number next to each member name (case 1253)
 *        9/06/07   CC of Lincoln - Do not allow proshop5 user to check players in (case 1072)
 *        9/05/07   Rolling Hills - Set proshop1 user to view bag numbers (case 1054)
 *        8/31/07   Medinah - add some modes of trans charge items.
 *        8/30/07   Cherry Hills - do not allow proshop4 (bag room) to check players in or out.
 *        8/30/07   Mirasol CC - make proshop2 user auto-refresh.
 *        8/28/07   Add new parm (newreq) for call to _slot so we know if request is for a new tee time.
 *        8/28/07   Mirasol CC - display the member numbers for all proshop users (case #1227)
 *        8/23/07   Tavistock - proshop3 - view only for GM to use.
 *        8/22/07   Jonas POS - make sure that the Reservation Number (resnum) is updated for each new tee time.
 *        8/22/07   Cherry Hills - Display handicap values as integers (no decimal)
 *        8/21/07   Medinah - Do NOT charge trans modes for certain guest types (Abacus POS).
 *        7/31/07   Brantford - Add $ next to certain membership types on the tee sheet
 *        7/31/07   Black Diamond Ranch - Added to default to -ALL- courses list
 *        7/26/07   Valley Club - Added to default to -ALL- courses list
 *        7/23/07   Changed hdcp index values to be displayed as double not integers (commented out the rounding)
 *        7/17/07   Wilmington - display an R next to the member's name if they have Range Privileges (case #1204).
 *        7/09/07   Brantford - proshop5 - view only.
 *        6/29/07   Merrill Hills - custom to display an * or $ after names with certain mships (case #1183).
 *        6/25/07   Changed buttons on POS popup window to disabled themselves while the processing runs
 *        6/14/07   Wellesley - custom to display an * after names with mships that are charged to play (case #1167).
 *        6/11/07   Enabled new lottery management to all clubs
 *        6/08/07   Medinah - change the color of teh 7:36 and 2:00 member times on weekends.
 *        6/06/07   Forest Highlands - color the tmode TD cell for caddies
 *        5/31/07   Sonnenalp - display the guest rates next to the guest name (case #1070).
 *        5/31/07   Los Coyotes - List the courses in the order they were specified when course = -ALL- (case #1172).
 *        5/29/07   Sonnenalp - remove custom POS processing as they aren't using it.
 *        5/24/07   Blackhawk (CA) - default course to Lakeside for proshop5.
 *        5/16/07   Pecan Plantation - fix the column for "Group has Teed Off" - add course name.
 *        5/11/07   Pinery - add new column for "Group has Teed Off" for the group.
 *        4/28/07   Congressional CC - custom on POS processing - use custom item codes based on course (case 1153).
 *        4/27/07   Muirfield GC - display the member number next to each member name (case 1145).
 *        4/27/07   Congressional CC - display the member number next to each member name (case 1153).
 *        4/27/07   The International - special processing for proshop5 - 10 minute refresh and show mnums next to names.
 *        4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *        4/20/07   Brantford - special processing for proshop5 - 10 minute refresh and show mnums next to names.
 *        4/18/07   Inverness GC (IL) - special processing for proshop4 & 5 - 10 minute refresh and show mnums next to names.
 *        4/17/07   Changed courseName order by for Edison Club.
 *        4/09/07   Sonnenalp - Set proshop5 user to view only and auto refresh and bag numbers (case 1110).
 *        4/05/07   The International - Added custom to default to -ALL- courses.
 *        4/05/07   CC of Virginia - Set proshop5 user to view only and auto refresh (case 1104).
 *        4/05/07   Los Coyotes - display the member number & gender next to each member name (case 1069).
 *        4/04/07   The Valley Club - special processing for proshop5 - 10 minute refresh and view only.
 *        4/02/07   Pinery - Added custom to default to -ALL- courses.
 *        3/28/07   CC of Virginia - display the member number next to each member name (case 1069).
 *        3/28/07   Denver CC - display the member number next to each member name (case 1078).
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        3/07/06   Hallbrook - special processing for proshop4 - 9 minute refresh and show bag #s next to names.
 *        3/07/07   Fort Collins - display FC or G in place of the member number next to each member name.
 *        3/06/07   Merion GC - display a membership indicator next to each member name.
 *        2/09/07   Wilmington CC - special processing for proshop5 - 10 minute refresh and view only.
 *        2/07/07   Fort Collins/Greeley - do not allow the -ALL- course option, and
 *                  list the appropriate course first based on which club the user is from.
 *        2/07/07   Greeley CC - they now share Fort Collins site/db - check for proshop4 or proshop5 user.
 *        2/05/07   Remove test for numeric only member numbers in member lookup js.
 *        2/01/07   Oakmont - change the friday guest times.
 *        2/01/07   IBS POS - allow for an empty sales tax string when prompting for sales tax.
 *        1/30/07   Added custom for Black Diamond Ranch - show up to 8 events and 8 restrictions.
 *        1/21/07   Changes for TLT system.  Make _dhseet the default in CP and quit using _insert
 *        1/18/07   Cordillera - change the date checks for custom rest's.
 *        1/16/07   Sonnenalp - add custom batch codes and fees for IBS POS interface.
 *        1/16/07   Greeley CC - display the member number next to each member name.
 *        1/08/07   Roayl Oaks Houston - display the member number next to each member name.
 *       12/28/06   Oakmont CC - change some of the Wed & Fri guest times.
 *       12/08/06   Added Control Panel link Manage Notifications for TLT system
 *       10/24/06   Columbine CC - special processing for proshop5 - 10 minute refresh and view only.
 *       10/05/06   Fixed bug (div by zero) in tee sheet summary debug code
 *       10/04/06   Move the report print processing to Proshop_sheet_reports.
 *        9/30/06   Removed club restrictions for Pace of Play - Updated pace.gif to PP
 *        9/30/06   Add sales tax to the IBS POS file.
 *        9/28/06   CC of St Albans - display Member Number with player name (members).
 *        9/18/06   Added Daily PoP Report to the Control Panel
 *        9/13/06   buildNS POS method - do not build the charges if posid not found for player.
 *        8/28/06   Add support for IBS POS system.
 *        8/21/06   Added custom to default to -ALL- courses for Fairbanks Ranch
 *        8/18/06   Added custom for Long Cove in Jonas POS processing - do not charge mode of trans fees for certain guest types.
 *        8/16/06   Added custom for Forest Highlands (TEMP) for the Mid Am Practice Rounds - change F/B values for 9/7 & 9/8.
 *        8/16/06   Added custom for Baltimore - special POS charges for certain mship types.
 *        8/09/06   Added custom to default to -ALL- courses for Lakewood Ranch and Pelican's Nest
 *        8/03/06   Desert Highlands - special processing for proshop5 - 9 minute refresh and show bag #s next to names
 *        7/21/06   Added Pace of Play feature
 *        7/19/06   Sunnehanna - special processing for proshop2 - 10 minute refresh and show bag #s next to names.
 *        7/19/06   Desert Highlands - make the time button a different color if there are notes in the tee time.
 *        7/18/06   POS Build Line methods - check if player is a member before processing mship type charges.
 *        6/26/06   Add support for TAI Club Management POS system.
 *        6/14/06   Outdoor CC - special processing for proshop2 - 10 minute refresh.
 *        5/30/06   Added support for displaying shotgun hole assignments
 *        5/04/06   Add POS Interface for ClubSystems Group (CSG) POS system.
 *        5/03/06   In promptPOS2 - if Jonas, set the Content Type before building the file so it works with large files.
 *        5/03/06   In buildJonas - skip charges if mNum/posid is empty.
 *        4/25/06   Remove test for numeric only member numbers in member lookup js. ***  didn't seem to be in here added it 2/5/07
 *        4/20/06   All clubs - change tee time button to 'Shotgun' during shotgun event.
 *        4/19/06   Use the correct time values when determining the lottery states.
 *        4/19/06   Pass 'shotgunevent' parm to Proshop_slot if tee time is during a shotgun event.
 *        4/19/06   Cherry Hills & Mission Viejo - change tee time button to 'Shotgun' during shotgun event.
 *        4/17/06   Medinah - customize the POS interface - manually set the item codes based on course.
 *        4/07/06   Cherry Hills - special processing for proshop4 - 9 minute refresh and show bag #s next to names.
 *        4/03/06   Change the filename for Abacus21 (Medinah) and customize the item codes, etc.
 *        3/20/06   Added "Check All In" as a Control Panel option - only available for today's sheet and not using ProShopKeeper POS
 *        3/15/06   Bearpath - color code the member-only times.
 *        3/14/06   Meadow Springs - change tee time button to 'Shotgun' during shotgun event.
 *        3/07/06   Oswego lake - make the time button a different color if there are notes in the tee time.
 *        3/03/06   Change the path for the pos folder for northstar for new servers.
 *        3/01/06   Cherry Hills - add new column for "# of Caddies Assigned" for the group.
 *                                The caddie master sets this when caddies have been assigned.
 *        2/03/06   Cordillera - add custom member restrictions that correspond to the custom hotel restrictions (checkCordillera).
 *        1/30/06   The Lakes - change tee time button to 'Shotgun' during shotgun event.
 *        1/12/06   Add div tags around selects for menus to work properly.
 *        1/12/06   Diablo CC - display the member number next to each members' name (like Medinah).
 *        1/12/06   The Ranch - add new column for "Group has Teed Off" for the group.
 *        1/03/06   When checking in players from course=ALL, display all courses when done.
 *       12/19/05   Change sql statements for tee sheet summary to reflect partially full tee times as well as full times.
 *       12/15/05   Lakewood - use posid instead of mNum in buildJonas.
 *       12/10/05   Add Tee Sheet Summary feature to show how much of the tee sheet is occupied.
 *       11/03/05   Cherry Hills - refresh the page every 9 minutes for bag room & caddie master (proshop4).
 *       10/24/05   Allow for 20 courses and -ALL- for multi course clubs.
 *       10/03/05   Add 'div' tag to form for drop-down course selector so main menus will show over the drop-down box.
 *        9/14/05   Add processing for Abacus21 POS system - similar to Jonas.
 *        9/05/05   Pecan Plantation - add new column for "Group has Teed Off" for the group.
 *        9/01/05   Make sure 'num' (the index value) is not null - default to zero.
 *        8/19/05   Move displayNaotes method to SystemUtils so others can use it.
 *        7/21/05   Medinah - display Member Number with player name (members).
 *        7/15/05   Add processing for NorthStar POS system - similar to Jonas.
 *        7/07/05   Custom for Forest Highlands - default course depends on login id.
 *        6/27/05   Do not allow multiple tee time request if F/B is a cross-over.
 *        6/16/05   Add new report - Alphabetical List by Members and Tee Times.
 *        6/01/05   Medinah CC - add 'Print Tee Time' feature - ability to print an individual tee time.
 *        5/19/05   Updated calendars to use new calv30 version - the dynamic calendar is now sticky
 *        5/17/05   Medinah CC - add custom to check for 'Member Times' (walk-up times).
 *        5/03/05   Cordillera - add a column for a Forecaddie indicator (Y or N).
 *        5/03/05   Old Warson - refresh the page every 4 minutes for bag room (proshop5).
 *        5/01/05   Add counters for # of tee sheets - in SystemUtils.
 *        4/29/05   RDP Add new report to List All Notes for the day.
 *        4/20/05   RDP Add global Gzip counters to count number of tee sheets using gzip.
 *        4/10/05   Interlachen - add new column for "# of Caddies Assigned" for the group.
 *                                The caddie master sets this when caddies have been assigned.
 *        4/04/05   Cordillera - do not display the Short course when course=ALL.
 *        3/10/05   Ver 5 - add course column shading to tee sheet printing when displaying all courses
 *        3/09/05   Ver 5 - add double line option for tee sheet printing
 *        3/02/05   Ver 5 - add visual pre-checking notifiction to tee sheet and printed reports
 *        2/19/05   Ver 5 - add Control Panel item for Diary feature.
 *        2/18/05   Ironwood - change tee time button to 'Shotgun' during shotgun event.
 *        2/18/05   Do not display lottery in legend or use color on sheet if state = 5 (processed).
 *        1/19/05   Ver 5 - add Control Panel item to send emails to all on tee sheet.
 *        1/18/05   Ver 5 - display up to 4 events in the legend.
 *        1/10/05   Oakmont CC - change some of the Wed guest times for April, Sept & Oct.
 *        1/05/05   Add a new Edit Tee Sheet link to request that emails not be sent.
 *        1/05/05   Ver 5 - allow pro to make up to 5 consecutive tee times at once.
 *        1/05/05   Ver 5 - add Frost Delay link in Control Panel.
 *       12/29/04   Blackhawk CC - List alphabetical mode of trans report in the order that the
 *                  modes are specified in the course setup.
 *       12/14/04   Ver 5 - Change Control Panel to be a Menu with drop-downs.
 *       11/29/04   Milwaukee CC - add checks for special tee times that allow Multiple Guests per member.
 *       11/17/04   Oakmont CC - add checks for special tee times that allow Multiple Guests per member.
 *       10/06/04   Change the Jonas POS reporting to support the "POS 3rd Party Tee Time to Jonas Interface".
 *        9/16/04   Ver 5 - change getClub from SystemUtils to common.
 *        9/01/04   Add a report that lists all players, their guests, member #'s and mode of trans for the day.
 *        8/20/04   Add fivesAll option to indicate if 5-somes are supported on any course (course=ALL).
 *        7/14/04   Do not use GZIP if call is for Notes display.
 *        7/07/04   Get the events, restrictions & lotteries in order for the legend display.
 *        6/17/04   Change the way we use gzip - use byte buffer so the length of output is
 *                  set properly.  Fixes problem with garbled screen.
 *        6/09/04   RDP Add an 'ALL' option for multiple course facilities.
 *        5/27/04   Add support for 4 lotteries per tee sheet.
 *        5/25/04   Change call to Proshop_slot a Get instead of a Post to allow 'Be Patient' page.
 *        5/11/04   RDP Make legend items (events, restrictions, lotteries) buttons so user
 *                  can click to view pop-up window describing the item.
 *        2/25/04   Add support for Jonas POS - 'end of day' report
 *        2/12/04   Add support Pro-shopKeeper POS - Check-in and Check-out.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/16/04   Change calendar to use js to allow for 365 days. (by Paul S.)
 *       12/15/02   Bob P.   Do not show member restriction in legend if show=no.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add Lottery processing.
 *       12/18/02   Enhancements for Version 2 of the software.
 *                  Add support for 'courseName' parm - select tee times for specific course.
 *                  Add support for Tee Time 'Blockers'.
 *
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.zip.*;
import java.sql.*;
import java.lang.Math;
import java.text.*;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.medinahCustom;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.cordilleraCustom;
import com.foretees.common.ProcessConstants;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.congressionalCustom;
import com.foretees.common.parmItem;
import com.foretees.common.getItem;
import com.foretees.common.getWaitList;
import com.foretees.common.Utilities;


public class Proshop_sheet extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
   
   int filen = 0;                             // Jonas filename (unique id)
   int resn = 0;                              // Jonas reservation number (unique id)
   int ttidn = 0;                             // Jonas tee time id (unique id)

   //
   //  Holidays
   //
   private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
   private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
   private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
   private static long Hdate4 = ProcessConstants.gFriDay;    // Good Friday
   private static long Hdate5 = ProcessConstants.tgDay;      // Thanksgiving Day

   

 //*****************************************************
 // Process the return from Proshop_slot (no longer used)
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }

 //*****************************************************
 // Process the request from Proshop_jump
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   if (req.getParameter("dumptoday") != null) {

       dumpSheet(req, resp);
       return;
   }

   //
   //  Prevent caching so sessions are not mangled (do not do here so the images can be cached!!!)
   //
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   PrintWriter out;

   PreparedStatement pstmtc = null;
   Statement stmt = null;
   //Statement stmtc = null;
   ResultSet rs = null;
   //ResultSet rs2 = null;

   ByteArrayOutputStream buf = null;
   String encodings = "";               // browser encodings

   boolean Gzip = false;        // default = no gzip

   String report_type = "";
   String excel = "";
   String showlott = "";
  
   //
   //  If report or print and excel format requested
   //
   if (req.getParameter("excel") != null) {

      excel = req.getParameter("excel");
   }

   if (req.getParameter("print") != null) {

      report_type = req.getParameter("print");
        
      if (!report_type.equals( "pos" )) {     // if NOT pos report type
        
         //
         //  Go to Proshop_sheet_reports to process the reports !!!!!!!!!!!!!!
         //
         try {
           
            Proshop_sheet_reports.prtReport(report_type, req, resp);  
            return;
              
         }
         catch (Exception exp) {
            SystemUtils.logError("Proshop_sheet - exception from Proshop_sheet_reports. Error = " +exp.getMessage());
            return;
         }
      }
   }
   
   //
   //  If the show lottery times option included
   //
   if (req.getParameter("showlott") != null) {

      showlott = req.getParameter("showlott");
   }

   
   
   
   /*
   // ********* temp **********

   if (req.getParameter("playern") != null) {

      out = resp.getWriter();                                         // normal output stream

      Enumeration enum1 = req.getParameterNames();

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Proshop_club Parameters</H1>");

      out.println("<BR><BR>Query String: ");
      out.println(req.getQueryString());
      out.println();

      out.println("<BR><BR>Request Parms: ");

      while (enum1.hasMoreElements()) {

         String name = (String) enum1.nextElement();
         String values[] = req.getParameterValues(name);
         if (values != null) {
            for (int i=0; i<values.length; i++) {

               out.println("<BR><BR>" +name+ " (" +i+ "): " +values[i]);
            }
         }
      }

      out.println("<BR><BR>");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   // ********* temp **********
    */
   
   

   //
   // if notes display, event/restriction/lottery display, POS Report Requested, or Notes Report
   //
   if (req.getParameter("notes") != null || req.getParameter("event") != null || req.getParameter("rest") != null ||
       req.getParameter("lottery") != null || req.getParameter("caddynum") != null || report_type.equals( "pos" ) ||
       req.getParameter("prtTeeTime") != null) {

      out = resp.getWriter();                                         // normal output stream

   } else {
      //
      //  use GZip (compression) if supported by browser
      //
      encodings = req.getHeader("Accept-Encoding");               // browser encodings

      if ((encodings != null) && (encodings.indexOf("gzip") != -1)) {    // if browser supports gzip

         Gzip = true;
         resp.setHeader("Content-Encoding", "gzip");                     // indicate gzip

         buf = new ByteArrayOutputStream();

         GZIPOutputStream gzipOut = new GZIPOutputStream(buf);
         OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipOut, "UTF-8");
         out = new PrintWriter(outputStreamWriter);

      } else {
         out = resp.getWriter();                                         // normal output stream
      }
   }


   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      resp.setContentType("text/html");

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TS_VIEW", con, out)) {
       SystemUtils.restrictProshop("TS_VIEW", out);
   }

   //
   //  Check for Send POS selected from CP - do before setting resp content type
   //
   if (report_type.equals( "pos" )) {         // if Send POS Requested

      promptPOS2(req, resp, out, session, con);      // go prompt user
      return;
   }

   resp.setContentType("text/html");      // ok to set content type now

   //
   //  First, check for Event or Restriction calls - user clicked on an event or restriction in the Legend
   //
   if (req.getParameter("event") != null) {

      String eventName = req.getParameter("event");

      displayEvent(eventName, out, con);             // display the information
      return;
   }
   if (req.getParameter("rest") != null) {

      int restID = Integer.parseInt(req.getParameter("rest"));

      displayRest(restID, out, con);             // display the information
      return;
   }
   if (req.getParameter("lottery") != null) {

      String lottName = req.getParameter("lottery");

      displayLottery(lottName, out, con);             // display the information
      return;
   }

   //
   //  Check for 'Print Tee Time' request - print button on individual tee time (currently just Medinah)
   //
   if (req.getParameter("prtTeeTime") != null) {

      printTeeTime(req, resp, out, session, con);      // go prompt user
      return;
   }


   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");      // get club name
   String user = (String)session.getAttribute("user");      // get user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lotteryS = Integer.parseInt(templott);

   boolean use_dlott = true;
/*
   use_dlott = (club.startsWith("demo") || club.equals("sugarmill") || club.equals("santaana") || 
                club.equals("westchester") || club.equals("bracketts") || club.equals("denvercc") || 
                club.equals("martindowns") || club.equals("cherryhills") || club.equals("elniguelcc") || 
                club.equals("missionviejo") || club.equals("ranchobernardo") || club.equals("columbine") || 
                club.equals("mirasolcc") || club.equals("bluehill") || club.equals("pecanplantation") || 
                club.equals("pinery") || club.equals("boulder") || 
                club.equals("diablocc") || club.equals("northridge") || club.equals("lakewood"));
*/
   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
/*
   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };
*/
   //
   //  Num of days in each month
   //
   //int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
/*
   int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };
*/
   //int count = 0;
   int courseCount = 0;
   int p = 0;
   int i = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int type = 0;                         
   int in_use = 0;                         // event type
   int shotgun = 1;                      // event type = shotgun
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
   int noSubmit = 0;
   int numCaddies = 0;             // Interlachen - number of caddies assigned, Cordillera - forecaddie indicator
   int k = 0;                      // Interlachen & Cordillera & Pecan Plantation - form counter
   int teecurr_id = 0;
   int pace_status_id = 0;
   int paceofplay = 0;  // hold indicator of wether of not PoP is enabled
   int mrest_id = 0;

   int [] mrest_ids = new int[8];
     
   short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;
   //short posPayValue = 0;
   short hideNotes = 0;
   String courseNameT = "";
   String courseTemp = "";
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String rest_recurr = "";
   String rest5 = "";
   //String player = "";
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
   String mnum1 = "";
   String mnum2 = "";
   String mnum3 = "";
   String mnum4 = "";
   String mnum5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String guest_uid1 = "";
   String guest_uid2 = "";
   String guest_uid3 = "";
   String guest_uid4 = "";
   String guest_uid5 = "";
   //String p1 = "";
   //String p2 = "";
   //String p3 = "";
   //String p4 = "";
   //String p5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String bag1 = "";
   String bag2 = "";
   String bag3 = "";
   String bag4 = "";
   String bag5 = "";
   String ghin1 = "";
   String ghin2 = "";
   String ghin3 = "";
   String ghin4 = "";
   String ghin5 = "";
   String ampm = "";
   //String event_rest = "";
   String bgcolor = "";
   String wait_list_color = "";
   String bgcolor1 = "";
   String bgcolor2 = "";
   String bgcolor3 = "";
   String bgcolor4 = "";
   String bgcolor5 = "";
   String timecolor = "";
   String stime = "";
   //String sshow = "";
   String sfb = "";
   String sfb2 = "";
   String submit = "";
   String num = "";
   // String jumps = "";
   String hole = "";              // hole assignment
   String custom_disp1 = "";      // custom display values
   String custom_disp2 = "";   
   String custom_disp3 = "";
   String custom_disp4 = "";
   String custom_disp5 = "";
   String tflag1 = "";
   String tflag2 = "";
   String tflag3 = "";
   String tflag4 = "";
   String tflag5 = "";
   String gType1 = "";
   String gType2 = "";
   String gType3 = "";
   String gType4 = "";
   String gType5 = "";
   //String tempS = "";
   String blocker = "";
   String notes = "";
   //String bag = "";
   String conf = "";
   //String orig_by = "";
   //String orig_name = "";
   //String lname = "";
   //String fname = "";
   //String mi = "";
   //String tmode = "";
   //String order = "";
   String errorMsg = "";
   

   //
   //  Arrays to hold the info required for Events, Restrictions, and Lotteries that will be displayed
   //  in the tee sheet legend and used when building the tee sheet.
   //
   int count_event = 4;      // number of events to show in legend
   int count_rest = 4;       // number of restrictions 
   int count_lott = 1;       // number of lotteries (min of 1 for tests)
   
   if (lotteryS > 0) count_lott = 4;                 // if lotteries support for this club
   
   if (club.equals("blackdiamondranch")) count_event = 8;
   if (club.equals("theforestcc")) count_event = 10;
   if (club.equals("championhills")) count_event = 10;           // max of 10 events for Champion Hills

   if (club.equals("blackdiamondranch") || club.equals("ccbham")) count_rest = 8;

   String [] eventA = new String [count_event];      // Event info
   String [] ecolorA = new String [count_event];
   
   int [] event_start_hrA = new int [count_event];    
   int [] event_start_minA = new int [count_event];    
   
   String [] restA = new String [count_rest];        // Restriction info
   String [] rcolorA = new String [count_rest];
   
   String [] lottA = new String [count_lott];        // Lottery info
   String [] lcolorA = new String [count_lott];
   
   int [] sdaysA = new int [count_lott];      // days in advance to start taking requests
   int [] sdtimeA = new int [count_lott];     // time of day to start taking requests
   int [] edaysA = new int [count_lott];      // days in advance to stop taking requests
   int [] edtimeA = new int [count_lott];     // time of day to stop taking requests
   int [] pdaysA = new int [count_lott];      // days in advance to process the lottery
   int [] ptimeA = new int [count_lott];      // time of day to process the lottery
   int [] slotsA = new int [count_lott];      // # of consecutive groups allowed
   int [] lskipA = new int [count_lott];      // skip tee time displays
   int [] lstateA = new int [count_lott];     // lottery state
                                              //    1 = before time to take requests (too early for requests)
                                              //    2 = after start time, before stop time (ok to take requests)
                                              //    3 = after stop time, before process time (late, but still ok for pro)
                                              //    4 = requests have been processed but not approved (no new tee times now)
                                              //    5 = requests have been processed & approved (ok for all tee times now)
  
   
   //int act_hr = 0;                   // Event data
   //int act_min = 0;
   
   //
   //  init the above arrays
   //  
   for (i=0; i<count_event; i++) {
      eventA[i] = "";
   }
   for (i=0; i<count_rest; i++) {
      restA[i] = "";
   }
   for (i=0; i<count_lott; i++) {
      lottA[i] = "";
   }
   i = 0;      // reset
   
     
   String cordRestColor = "burlywood";        // color for Cordillera's custom member restriction
   String cordStarterColor = "sienna";        // color for Cordillera's Starter Times

   //String interlGPcolor = "yellow";        // color for Interlachen's Gift Pack guest option
   String oaklandGBTcolor = "lightgreen";         // color for Oakland Hills' Guest Bag Tag option

   //
   //  Lottery information storage area
   //
   //    lottery calculations done only once so we don't have to check each time while building sheet
   //
   String lottery = "";
   String lottery_color = "";
   String lottery_recurr = "";
     
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;
   int slots = 0;
   int curr_time = 0;
   int lskip = 0;
   int lstate = 0;           // lottery state
   int templstate = 0;       // temp lottery state
     
   //int lyear = 0;
   //int lmonth = 0;
   //int lday = 0;
   int advance_days = 0;       // copy of 'index' = # of days between today and the day of this sheet


   // **************** end of lottery save area ***********

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   int hndcp = 0;
   int j = 0;
   // int jump = 0;

   int index = 0;
   int multi = 0;               // multiple course support
   int fives = 0;
   int fivesALL = 0;
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
   //int st2 = 2;
   //int POS = 0;
   
   //boolean charges = false;
   //boolean noShow = false;
   //boolean checkedOut = false;
   boolean disp_hndcp = true;
   boolean disp_mnum = false;
   boolean disp_bag = false;
   boolean medinahMemTime = false;        // for Medinah Custom
   boolean restrictAll = false;
   boolean skipTime = false;
   boolean clubAGC = false;
   boolean skipALL = false;            // do not include the -ALL- option for course selection if true 
   boolean walkup = false;
   boolean suspend = false;            // Member restriction suspension
   boolean useTOcol = false;
   boolean use_guestdb = Utilities.isGuestTrackingConfigured(0, con);
   
   // Check proshop user feature access for appropriate access rights
   boolean popAccess = SystemUtils.verifyProAccess(req, "TS_PACE_VIEW", con, out);
   boolean popUpdate = SystemUtils.verifyProAccess(req, "TS_PACE_UPDATE", con, out);
   boolean posAccess = SystemUtils.verifyProAccess(req, "TS_POS", con, out);
   boolean checkinAccess = SystemUtils.verifyProAccess(req, "TS_CHECKIN", con, out);
   boolean updateAccess = SystemUtils.verifyProAccess(req, "TS_UPDATE", con, out);
   boolean wlViewAccess = SystemUtils.verifyProAccess(req, "WAITLIST_VIEW", con, out);
   boolean wlUpdateAccess = SystemUtils.verifyProAccess(req, "WAITLIST_UPDATE", con, out);
   boolean wlManageAccess = SystemUtils.verifyProAccess(req, "WAITLIST_MANAGE", con, out);
   boolean lottApproveAccess = SystemUtils.verifyProAccess(req, "LOTT_APPROVE", con, out);
   boolean dispHdcpOption = SystemUtils.verifyProAccess(req, "display_hdcp", con, out);
   boolean dispMnumOption = SystemUtils.verifyProAccess(req, "display_mnum", con, out);
   boolean dispBagOption = SystemUtils.verifyProAccess(req, "display_bag", con, out);
   boolean sysConfigEvent = SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out);
   boolean sysConfigRest = SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out);
   boolean sysConfigLott = SystemUtils.verifyProAccess(req, "SYSCONFIG_LOTTERY", con, out);
   boolean tsNotesView = SystemUtils.verifyProAccess(req, "TS_NOTES_VIEW", con, out);
   
   
   
   //
   //  Number of days to display the check-in boxes (normally just on today's sheet) - refer to Member_slot and SystemUtils for rest of custom
   //
   int checkboxdays = 0;
   
   if (club.equals("pelicansnest") || club.equals("imperialgc") || club.equals("mediterra") || 
           club.equals("ccnaples") || club.equals("theoaksclub")) {
          
      checkboxdays = 1;          // display on next day's tee sheet too - refer to Member_slot for rest of customs
   }
   
   
   
   //
   //  American Golf - set the clubAGC flag if club is part of AGC
   //
   if (club.equals("palmvalley-cc") || club.equals("loscoyotes") || club.equals("tanoan") || club.equals("marbellacc")) {
      
      clubAGC = true;      // AGC club - used for customs below
   }
   
      
   if (club.equals( "pecanplantation" ) || club.equals( "theranchcc" ) || club.equals( "pinery" ) || club.equals( "tualatincc" ) || clubAGC == true) {

      useTOcol = true;       // use the Teed Off Column on tee sheet to indicate when players have actually started their rounds
   }
      
      
   //
   //  parm block to hold the Course Colors
   //
   parmCourseColors colors = new parmCourseColors();          // allocate a parm block

   int colorMax = colors.colorMax;             // max number of colors defined
   
   //
   //  Array to hold the course names
   //
   int cMax = 0;                                       // max of 20 courses plus allow room for '-ALL-'
   String courseName = "";
   
   ArrayList<String> course = new ArrayList<String>();
  
   ArrayList<Integer> fivesA = new ArrayList<Integer> ();        // array list to hold 5-some option for each course
     
   // String [] course = new String [cMax];
   // int [] fivesA = new int [cMax];                  // array to hold 5-some option for each course
   
   int tmp_i = 0; // counter for course[], shading of course field
   
   
   /*   moved to parmCourseColors
    * 
   // set default course colors  // NOTE: CHANES TO THIS ARRAY NEED TO BE DUPLICATED IN Proshop_dsheet.java:doBlockers()
   course_color[0] = "#F5F5DC";   // beige shades
   course_color[1] = "#DDDDBE";
   course_color[2] = "#B3B392";
   course_color[3] = "#8B8970";
   course_color[4] = "#E7F0E7";   // greens shades
   course_color[5] = "#C6D3C6";
   course_color[6] = "#648A64";
   course_color[7] = "#407340";
   course_color[8] = "#FFE4C4";   // bisque
   course_color[9] = "#95B795";
   course_color[10] = "#66CDAA";  // medium aquamarine
   course_color[11] = "#20B2AA";  // light seagreen
   course_color[12] = "#3CB371";  // medium seagreen
   course_color[13] = "#F5DEB3";  // wheat
   course_color[14] = "#D2B48C";  // tan
   course_color[15] = "#999900";  //
   course_color[16] = "#FF9900";  // red-orange??
   course_color[17] = "#33FF66";  //
   course_color[18] = "#7FFFD4";  // aquamarine
   course_color[19] = "#33FFFF";  //
   course_color[20] = "#FFFFFF";  // white
    */
   
   //
   //  Woodlands CC - custom to make East Course stand out so outside staff (starter, etc) can better differentiate the courses (case 1724)
   //
   if (club.equals("woodlandscountryclub")) {
      
      colors.course_color[1] = "lawngreen";           // use Lawn Green for East Course (2nd in list)
   }
   
   if (club.equals("morganrun")) {             // MORGAN RUN - change colors of all 3 courses
      
      colors.course_color[0] = "gold";        // Course A           
      colors.course_color[1] = "green";       // Course B
      colors.course_color[2] = "beige";       // Course C
   }
   


   //**********************************************************************************
   //  Oakmont tee time arrays for Wednesday & Friday (special guest restrictions) see also verifySlot
   //**********************************************************************************
   //
   int wedcount = 19;    // 19 tee times on Wednesday
   int [] wedtimes = { 820, 830, 840, 850, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150, 1430, 1440, 1450, 1500 };

   int fricount = 21;    // 21 tee times
   int [] fritimes = { 830, 840, 850, 900, 910, 920, 930, 940, 950, 1000, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150 };

//   int fricount = 12;    // 12 tee times on Friday
//   int [] fritimes = { 830, 840, 910, 920, 940, 1010, 1020, 1040, 1110, 1120, 1140, 1150 };

   boolean oakshotgun = false;    // indicator for shotgun event this day

   //**********************************************************************************

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  parm block to hold the POS parameters
   //
   parmPOS parmp = new parmPOS();          // allocate a parm block for POS parms
   
   //
   //  parm block to hold the member restrictions for this date and member
   //
   parmRest parmr = new parmRest();          // allocate a parm block

   // check proshop user access for booking and changing tee times
   if (!updateAccess) {
       restrictAll = true;
   }
   
   try {

      //
      // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

   }
   catch (Exception exc) {
       out.println("<!-- ERROR CALLING getClub.getParms(): " + exc.toString() + " -->");
   }   
      
   //
   //  Get the golf course name requested
   //
   String courseName1 = "";
   String courseCheckIn = "";

   if (req.getParameter("course") != null) {

      courseName1 = req.getParameter("course");
      
   } else {

      if (parm.multi != 0) {           // if multiple courses supported for this club

         //
         //  No course name provided and this club has more than one - check for a default course
         //
         if (!parm.default_course_pro.equals( "" )) {

            courseName1 = parm.default_course_pro;            // get default course from club5 !!!!

         } else {

            //
            //    NOTE:  Replace these customs by manually putting the default value in club5!!!!!!!!!!!!!!
            //         
      
            // Custom to default to -ALL- courses
            if ( club.equals("lakewoodranch") || club.equals("pelicansnest") || 
                 club.equals("fairbanksranch") || club.equals("pinery") || 
                 club.equals("international") || club.equals("valleyclub") || club.equals("mediterra") || 
                 club.equals("blackdiamondranch") || club.equals("imperialgc")) {

               courseName1 = "-ALL-";
            }
         }
         
         if (club.equals( "stonebridgeranchcc" )) {
            
            if (user.equals("proshop1") || user.equals("proshop2")) {
            
               courseName1 = "Dye";              // set default course for these users
            }
            
            if (user.equals("proshop4") || user.equals("proshop5")) {
            
               courseName1 = "-ALL-";            // set default course for these users
            }
         }
                  
      }      // end of IF multi
   }         // end of IF course
   
   
   if (req.getParameter("courseCheckIn") != null) {

      courseCheckIn = req.getParameter("courseCheckIn");      // get actual course name for check-in calls (in case course=ALL)
   }

   
   
   //
   //  check to see if the checkallin parameter is present    
   //
   //      NOTE:  keep this here for now, do not move to Proshop_sheet_checkin!!! (maybe later)
   //
   if (req.getParameter("checkallin") != null) {
      
      // run sql statement to set show# values to 1
      String tmpWhereClause = "";
      String tmpSQL = "";
      long today = 0;
      
      try {
          
        //
        //  Get today's date and current time and calculate date & time values
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        int cal_hour = cal.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
        int cal_min = cal.get(Calendar.MINUTE);
        curr_time = (cal_hour * 100) + cal_min;             // get time in hhmm format
        curr_time = SystemUtils.adjustTime(con, curr_time); // adjust the time

        if (curr_time < 0) {                                // if negative, then we went back or ahead one day

            curr_time = 0 - curr_time;                      // convert back to positive value

            if (curr_time < 1200) {                         // if AM, then we rolled ahead 1 day

                //
                // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                //
                cal.add(Calendar.DATE,1);                   // get next day's date

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

            } else {                                        // we rolled back 1 day

                //
                // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                //
                cal.add(Calendar.DATE,-1);                  // get yesterday's date

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
            }
        }

        month++;                                            // month starts at zero

        today = (year * 10000) + (month * 100) + day;       // create value of yyyymmdd
     
         PreparedStatement pstmt3 = null;
         
         // if course is -ALL- then we won't be including the course name in the clause
         tmpWhereClause = (courseName1.equals("-ALL-")) ? ";" : " AND courseName = ?;";
         
         // shouldn't have to worry about supporting 5somes, since the update should
         // will only affect rows the the player# field is already set
         for (int i7=1; i7<6; i7++) {
             tmpSQL = "UPDATE teecurr2 SET show" + i7 + " = 1 WHERE player" + i7 + " <> '' AND date = ?" + tmpWhereClause;
             pstmt3 = con.prepareStatement (tmpSQL);
             pstmt3.clearParameters();        // clear the parms
             pstmt3.setLong(1, today);
             if (!courseName1.equals("-ALL-")) pstmt3.setString(2, courseName1);
             pstmt3.executeUpdate();      // execute the prepared stmt
             pstmt3.close();
         }
    
      } catch (Exception exp) {

          displayDatabaseErrMsg("Error checking in all players for this tee-sheet.", exp.getMessage(), out);

      }
      
   }
   
   
   /*        no longer used - see Proshop_jump (jump now done in url)
   //
   //  get the jump parm if provided (location on page to jump to)
   //
   if (req.getParameter("jump") != null) {

      jumps = req.getParameter("jump");         //  jump index value for where to jump to on the page

      try {
         jump = Integer.parseInt(jumps);
      }
      catch (NumberFormatException e) {
         jump = 0;
      }
   }

   //
   //   Adjust jump so we jump to the selected line minus 3 so its not on top of page
   //
   if (jump > 3) {

      jump -= 3;

   } else {

      jump = 0;         // jump to top of page
   }
    */
   

   try {

      multi = parm.multi;
      paceofplay = parm.paceofplay;
      i = 0;
      
      errorMsg = "Error getting course names.";

      //
      //   Get course names if multi-course facility
      //
      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
          
         courseCount = course.size();
         

         if (club.equals( "fortcollins" )) {
            
            skipALL = true;       // do not include the -ALL- option
         }
         
         
         if (club.equals( "stonebridgeranchcc" )) {
            
            if (user.equals("proshop1") || user.equals("proshop2")) {
            
               skipALL = true;               // do not include the -ALL- option
            }
         }
         
      
         if (skipALL == false && courseCount > 1) {  
            
            course.add ("-ALL-");      // add '-ALL-' option
         }
           
         //
         //  Make sure we have a course (in case we came directly from the Today's Tee Sheet menu)
         //
         if (courseName1.equals( "" )) {
           
            courseName1 = course.get(0);    // grab the first one
              
            if (club.equals( "foresthighlands" )) {         // change for Forest Highlands 

               if (user.equalsIgnoreCase( "proshop3" ) || user.equalsIgnoreCase( "proshop4" )) {

                  courseName1 = "Meadow";      // setup default course (top of the list)

               } else {

                  courseName1 = "Canyon";
               }
              
            } else if (club.equals( "cordillera" )) {     // Cordillera - case 1862

               if (user.equalsIgnoreCase( "proshop1" )) { 

                  courseName1 = "Valley";      // setup default course (top of the list)

               } else if (user.equalsIgnoreCase( "proshop2" )) { 

                  courseName1 = "Mountain";      // setup default course (top of the list)

               } else if (user.equalsIgnoreCase( "proshop3" )) { 

                  courseName1 = "Summit";      // setup default course (top of the list)

               } else if (user.equalsIgnoreCase( "proshop4" )) { 

                  courseName1 = "Short";      // setup default course (top of the list)
               }

            } else if (club.equals( "blackhawk" ) && user.equalsIgnoreCase( "proshop5" )) {

               courseName1 = "Lakeside";

            } else if (club.equals( "mayfieldsr" ) && user.equalsIgnoreCase( "proshop1" )) {

               courseName1 = "Sand Ridge";    

            } else if (club.equals( "fortcollins" )) {         // change for Fort Collins

               if (user.equalsIgnoreCase( "proshop4" ) || user.equalsIgnoreCase( "proshop5" )) {   // if Greeley Pro

                  courseName1 = "Greeley CC";      // setup default course (top of the list)

               } else if (user.equalsIgnoreCase("proshopfox")) {

                  courseName1 = "Fox Hill CC";

               } else {

                  courseName1 = "Fort Collins CC";
               }
            
            } else if (club.equals("oakhillcc")) { 

                courseName1 = "East Course";          // Oak Hill CC - default = East Course (case 1433)
            
            } else if (club.equals("ccjackson")) { 

                courseName1 = "Cypress to Cypress";        // Set default course - Case #1373
            }
         }
      }
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database at this time (get multi parms).");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + errorMsg);
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }

   //
   //  Get POS parms if course not '-ALL-' (Jonus Report will not show when course = ALL)
   //
   courseTemp = "";                         // init

   if (!courseName1.equals( "-ALL-" )) {
     
      courseTemp = courseName1;             // use this course
        
   } else {

      if (!courseCheckIn.equals( "" )) {         // if individual course specified on check-in form

         courseTemp = courseCheckIn;             // use this course
      }
   }

   try {
      //
      //  Get the POS System Parameters for this Club & Course
      //
      getClub.getPOS(con, parmp, courseTemp);

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database at this time (get course parms).");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }
 
   
   //
   //  Custom for Cherry Hills - do NOT allow POS access or Check-in for proshop4 user (bag room) 
   //
   if ((club.equals( "cherryhills" ) && user.equalsIgnoreCase( "proshop4" )) ||
       (club.equals( "sciotoreserve" ) && !user.equalsIgnoreCase( "proshop1" )) ||
       (club.equals( "lincoln" ) && user.equalsIgnoreCase( "proshop5" ))) {

       posAccess = false;           
       checkinAccess = false;
   }
         

   // if posAccess is false, set paynow options to 0;
   if (!posAccess) {
       parmp.pos_paynow = 0;
   }
   
   // restrict checkin access for Pro-ShopKeeper users that do not have POS access
   if (!posAccess && (parmp.posType.equalsIgnoreCase( "Pro-ShopKeeper" ) || parmp.posType.equals( "ClubProphetV3" ))) {
       checkinAccess = false;
   }

   
   
   //
   //    'index' contains an index value representing the date selected
   //    (0 = today, 1 = tomorrow, etc.)
   //
   num = req.getParameter("index");         // get the index value of the day selected
 

      
   //
   //  Convert the index value from string to int
   //
   if (num == null || num.equals( "" ) || num.equalsIgnoreCase( "null" )) {
     
      index = 0;
      num = "0";
        
   } else {
     
      index = Integer.parseInt(num);
   }

   //
   //  save the index value for lottery computations
   //
   advance_days = index;

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();

   int date = (int)Utilities.getDate(con, index);   // get the date for the date selected by the index

   out.println("<!-- date="+date+", index="+index+" -->");

   //
   //  isolate yy, mm, dd
   //
   year = date / 10000;
   int temp = year * 10000;
   month = date - temp;
   temp = month / 100;
   temp = temp * 100;
   day = month - temp;
   month = month / 100;

   cal.set(year, month-1, day);

   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   String day_name = day_table[day_num];         // get name for day

   long dateShort = (month * 100) + day;         // create date of mmdd for customs

   out.println("<!-- dateShort="+dateShort+" -->");
   //
   //   if call was for Show Notes then get the notes and display a new page
   //
   if (req.getParameter("notes") != null) {

      stime = req.getParameter("time");         //  time of the slot
      sfb = req.getParameter("fb");             //  front/back indicator
        
      SystemUtils.displayNotes(stime, sfb, date, courseName1, out, con);             // display the information
      return;
   }

   //
   //   if Interlachen or Cherry Hills and call was to update number of caddies assigned to the group - go process
   //
   if ((club.equals( "interlachen" ) || club.equals( "cherryhills" )) && req.getParameter("caddynum") != null) {

      String caddies = req.getParameter("caddynum");         //  get number of caddies
      stime = req.getParameter("time");                      //  time of the slot
      sfb = req.getParameter("fb");                          //  front/back indicator

      updateCaddies(stime, sfb, date, caddies, out, con);   // update the record & continue to display the sheet
   }

   //
   //   if Pecan Plantation or The Ranch (or others) and call was to update the 'teed off' indicator for the group - go process
   //
   if (useTOcol == true && req.getParameter("teedOff") != null) {

      String teedOff = req.getParameter("teedOff");          //  get 'teed off' indicator
      stime = req.getParameter("time");                      //  time of the slot
      sfb = req.getParameter("fb");                          //  front/back indicator
      courseNameT = req.getParameter("courseT");             //  course name for tee time

      updateTeedOff(courseNameT, stime, sfb, date, teedOff, out, con);   // update the record & continue to display the sheet
   }

   //
   //   if Cordillera and call was to update the forecaddie indicator - go process
   //
   if (club.equals( "cordillera" ) && req.getParameter("forecaddy") != null) {

      String forecaddy = req.getParameter("forecaddy");      //  get forecaddie indicator
      stime = req.getParameter("time");                      //  time of the slot
      sfb = req.getParameter("fb");                          //  front/back indicator
      courseNameT = req.getParameter("courseT");             //  course name for tee time

      updateForeCaddie(stime, sfb, date, forecaddy, courseNameT, out, con);   // update the record & continue to display the sheet
   }

      
   //
   //  Determine if this club wants to display handicaps for the members
   //
   disp_hndcp = (parm.hndcpProSheet != 0);

   //
   //  Determine if this club wants to display Member Numbers for the members
   //
   if (club.equals( "medinahcc" ) || club.equals( "diablocc" ) || club.equals( "stalbans" ) ||
       club.equals( "royaloakscc" ) || club.equals( "merion" ) || club.equals( "fortcollins" ) || club.equals( "denvercc" ) ||
       club.equals( "virginiacc" ) || club.equals( "loscoyotes" ) || club.equals( "congressional" ) || club.equals( "muirfield" ) ||
       club.equals( "mirasolcc" ) || (club.equals( "bellehaven" ) && user.equals( "proshop4" )) ||
       (club.equals( "sciotoreserve" ) && (user.equals( "proshop4" ) || user.equals( "proshop5" ))) ||
       (club.equals( "mediterra" ) && user.equals( "proshop5" )) || club.equals( "ridgeclub" ) || club.equals( "tcclub" ) ) {
     
      disp_mnum = true;
   }

   // all computation and db queries are wrapped inside this one try/catch block
   // the end of this block is the end of doPost
   try {
      
      errorMsg = "Error getting course parameters.";

      //
      //  Get the System Parameters for this Course
      //
      if (courseName1.equals( "-ALL-" )) {

         //
         //  Check all courses for 5-some support
         //
         i = 0;
         loopc:
         while (i < courseCount) {

            courseName = course.get(i);       // get a course name

            if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

               if (courseName.equals( "" ) || courseName == null) {      // done if null
                  break loopc;
               }
               getParms.getCourse(con, parmc, courseName);
               
               fivesA.add (parmc.fives);      // get fivesome option
               
               if (parmc.fives == 1) {
                  fives = 1;
               }
            }
            i++;
         }

      } else {       // single course requested

         getParms.getCourse(con, parmc, courseName1);

         fives = parmc.fives;      // get fivesome option
      }

      fivesALL = fives;            // save 5-somes option for table display below

      //
      //   Remove any guest types that are null - for tests below
      //
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name  (hey that's my cousins name!)
         }
         i++;
      }         // end of while loop

      errorMsg = "Error getting restriction settings.";

      //
      //  Get all restrictions for this day and user (for use when checking each tee time below)
      //
      parmr.user = user;
      parmr.mship = "";
      parmr.mtype = "";
      parmr.date = date;
      parmr.day = day_name;
      parmr.course = courseName1;

      getRests.getAll(con, parmr);       // get the restrictions
      
      errorMsg = "Error getting restrictions for this date.";

      //
      //   Statements to find any restrictions, events or lotteries for today
      //
      PreparedStatement pstmt7b = null;
      PreparedStatement pstmt7c = null;
      PreparedStatement pstmt7d = null;
      String string7b = "";
      String string7c = "";
      String string7d = "";

      if (courseName1.equals( "-ALL-" )) {
         string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND showit = 'Yes' AND activity_id = 0 ORDER BY stime";
      } else {
         string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes' AND activity_id = 0 ORDER BY stime";
      }

      if (courseName1.equals( "-ALL-" )) {
         string7c = "SELECT name, color, act_hr, act_min FROM events2b WHERE date = ? AND season = 0 AND activity_id = 0 AND inactive = 0 ORDER BY stime";
      } else {
         string7c = "SELECT name, color, act_hr, act_min FROM events2b WHERE date = ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND season = 0 AND activity_id = 0 AND inactive = 0 ORDER BY stime";
      }

      if (lotteryS > 0) {               // if lottery supported 
         
         if (courseName1.equals( "-ALL-" )) {
            string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                       "FROM lottery3 WHERE sdate <= ? AND edate >= ? ORDER BY stime";
         } else {
            string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                       "FROM lottery3 WHERE sdate <= ? AND edate >= ? " +
                       "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
         }
         
         pstmt7d = con.prepareStatement (string7d);
      }

      pstmt7b = con.prepareStatement (string7b);
      pstmt7c = con.prepareStatement (string7c);

      //
      //  Scan the events, restrictions and lotteries to build the legend
      //
      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
        
      if (!courseName1.equals( "-ALL-" )) {
         pstmt7b.setString(3, courseName1);
      }
      
      i =0;

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      while (rs.next() && i < count_rest) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);
         mrest_id = rs.getInt("id");
         
         boolean showRest = getRests.showRest(rs.getInt("id"), -99, rs.getInt("stime"), rs.getInt("etime"), date, day_name, courseName1, con);
         
         if (showRest) {    // Only display on legend if not suspended for entire day
           
             //
             //  We must check the recurrence for this day (Monday, etc.)
             //
             if ((rest_recurr.equals( "Every " + day_name )) ||          // if this day
                 (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                 ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                   (!day_name.equalsIgnoreCase( "saturday" )) &&
                   (!day_name.equalsIgnoreCase( "sunday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                  (day_name.equalsIgnoreCase( "saturday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                  (day_name.equalsIgnoreCase( "sunday" )))) {


                restA[i] = rest;          // save the restriction info for legend and tee sheet
                rcolorA[i] = rcolor;
                mrest_ids[i] = mrest_id;

                if (rcolorA[i].equalsIgnoreCase( "default" )) rcolorA[i] = "#F5F5DC";
                
                i++;
             }
         }
      }                  // end of while
      pstmt7b.close();

      
      errorMsg = "Error getting events for this date.";

      //
      //  Get the events for this date and course
      //
      pstmt7c.clearParameters();          // clear the parms
      pstmt7c.setLong(1, date);

      if (!courseName1.equals( "-ALL-" )) {
         pstmt7c.setString(2, courseName1);
      }

      i = 0;
      
      rs = pstmt7c.executeQuery();      // find all matching events, if any

      while (rs.next() && i < count_event) {

         eventA[i] = rs.getString(1);
         ecolorA[i] = rs.getString(2);
         event_start_hrA[i] = rs.getInt(3);
         event_start_minA[i] = rs.getInt(4);

         if (ecolorA[i].equalsIgnoreCase( "default" )) ecolorA[i] = "#F5F5DC";
         
         i++;
      }                  // end of while
      pstmt7c.close();

      
      if (lotteryS > 0) {               // if lottery supported 
         
         errorMsg = "Error getting lotteries for this date.";

         //
         //  check for lotteries
         //
         pstmt7d.clearParameters();          // clear the parms
         pstmt7d.setLong(1, date);
         pstmt7d.setLong(2, date);

         if (!courseName1.equals( "-ALL-" )) {
            pstmt7d.setString(3, courseName1);
         }

         i = 0;

         rs = pstmt7d.executeQuery();      // find all matching lotteries, if any

         while (rs.next() && i < count_lott) {

            lottery = rs.getString(1);
            lottery_recurr = rs.getString(2);
            lottery_color = rs.getString(3);
            sdays = rs.getInt(4);
            sdtime = rs.getInt(5);
            edays = rs.getInt(6);
            edtime = rs.getInt(7);
            pdays = rs.getInt(8);
            ptime = rs.getInt(9);
            slots = rs.getInt(10);

            //
            //  We must check the recurrence for this day (Monday, etc.)
            //
            if ((lottery_recurr.equals( "Every " + day_name )) ||          // if this day
                (lottery_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                ((lottery_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                  (!day_name.equalsIgnoreCase( "saturday" )) &&
                  (!day_name.equalsIgnoreCase( "sunday" ))) ||
                ((lottery_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                 (day_name.equalsIgnoreCase( "saturday" ))) ||
                ((lottery_recurr.equalsIgnoreCase( "all weekends" )) &&
                 (day_name.equalsIgnoreCase( "sunday" )))) {


               lottA[i] = lottery;             // save Lottery info
               lcolorA[i] = lottery_color;
               sdaysA[i] = sdays;
               sdtimeA[i] = sdtime;
               edaysA[i] = edays;
               edtimeA[i] = edtime;
               pdaysA[i] = pdays;
               ptimeA[i] = ptime;
               slotsA[i] = slots;

               if (lottery_color.equalsIgnoreCase( "default" )) {

                  lcolorA[i] = "#F5F5DC";
               }
               i++;
            }
         }                  // end of while
         pstmt7d.close();

         //
         //  Process the lotteries if there are any for this day
         //
         //    Determine which state we are in (before req's, during req's, before process, after process)
         //
         String string12 = "";
         String string12b = "";

         if (courseName1.equals( "-ALL-" )) {

            string12 = "SELECT state FROM lreqs3 " +
                       "WHERE name = ? AND date = ?";

            string12b = "SELECT ptime FROM actlott3 " +
                       "WHERE name = ? AND date = ?";          // used to check if lottery has been processed by SystemUtils

         } else {
            
            string12 = "SELECT state FROM lreqs3 " +
                       "WHERE name = ? AND date = ? AND courseName = ?";
            
            string12b = "SELECT ptime FROM actlott3 " +
                       "WHERE name = ? AND date = ? AND courseName = ?";
         }

         if (!lottA[0].equals( "" )) {      // if any lotteries

            errorMsg = "Error getting lottery states.";

            //
            //  Get the current time
            //
            Calendar cal3 = new GregorianCalendar();    // get todays date
            int cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time)
            int cal_min = cal3.get(Calendar.MINUTE);

            curr_time = (cal_hour * 100) + cal_min;

            curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

            if (curr_time < 0) {          // if negative, then we went back or ahead one day

               curr_time = 0 - curr_time;        // convert back to positive value
            }

            //
            //  process each lottery        
            //
            for (i=0; i<count_lott; i++) {    

               if (!lottA[i].equals( "" )) {       // if lottery to process

                  //
                  //  check the day and time values        
                  //
                  if (advance_days > sdaysA[i]) {       // if we haven't reached the start day yet

                     lstateA[i] = 1;                    // before time to take requests

                  } else {

                     if (advance_days == sdaysA[i]) {   // if this is the start day

                        if (curr_time >= sdtimeA[i]) {   // have we reached the start time?

                           lstateA[i] = 2;              // after start time, before stop time to take requests

                        } else {

                           lstateA[i] = 1;              // before time to take requests
                        }
                     } else {                        // we are past the start day

                        lstateA[i] = 2;                 // after start time, before stop time to take requests
                     }

                     if (advance_days == edaysA[i]) {   // if this is the stop day

                        if (curr_time >= edtimeA[i]) {   // have we reached the stop time?

                           lstateA[i] = 3;              // after stop time, before process time 
                        }
                     }

                     if (advance_days < edaysA[i]) {   // if we are past the stop day

                        lstateA[i] = 3;                // after stop time, before process time
                     }
                  }

                  if (lstateA[i] == 3) {                // if we are now in state 3, check for state 4

                     if (advance_days == pdaysA[i]) {   // if this is the process day

                        if (curr_time >= ptimeA[i]) {    // have we reached the process time?

                           lstateA[i] = 4;              // after process time
                        }
                     }

                     if (advance_days < pdaysA[i]) {   // if we are past the process day

                        lstateA[i] = 4;                // after process time
                     }
                  }

                  if (lstateA[i] == 4) {                // if we are now in state 4, check for pending approval

                     PreparedStatement pstmt12 = con.prepareStatement (string12);

                     pstmt12.clearParameters();        // clear the parms
                     pstmt12.setString(1, lottA[i]);
                     pstmt12.setLong(2, date);

                     if (!courseName1.equals( "-ALL-" )) {
                        pstmt12.setString(3, courseName1);
                     }

                     rs = pstmt12.executeQuery();

                     if (!rs.next()) {             // if none waiting approval

                        lstateA[i] = 5;              // state 5 - after process & approval time

                     } else {                     // still some reqs waiting

                        templstate = rs.getInt(1);  // get its state

                        if (templstate == 5) {      // if we processed already (some not assigned)

                           lstateA[i] = 5;
                           
                        } else {                   // check if lottery has actually been processed (has timer kicked it yet?)
                           
                           PreparedStatement pstmt12b = con.prepareStatement (string12b);

                           pstmt12b.clearParameters();        // clear the parms
                           pstmt12b.setString(1, lottA[i]);
                           pstmt12b.setLong(2, date);

                           if (!courseName1.equals( "-ALL-" )) {
                              pstmt12b.setString(3, courseName1);
                           }

                           rs = pstmt12b.executeQuery();

                           if (rs.next()) {             // if Lottery still waiting to be processed (timer has not kicked it yet)

                              lstateA[i] = 3;           // state 3 - waiting process & approval time
                           }
                           
                           pstmt12b.close();
                        }
                     }
                     pstmt12.close();

                  }

                  out.println("<!-- LOTTERY: " + lottA[i] + ", lstate=" + lstateA[i] + " -->");

               }  // end if lottery name set
            }  // end lottery loop
         }  // end if any lotteries found for this day
      }  // end of IF lottery supported        
            
      i =0;
      
      errorMsg = "Error checking custom settings.";

      //
      //  Special processing for Oakmont CC - check if there is a Shotgun Event for today (if Friday)
      //
      if (club.equals( "oakmont" ) && day_name.equals("Friday") && date != 20100827) {

         pstmtc = con.prepareStatement (
            "SELECT dd " +
            "FROM teecurr2 " +
            "WHERE date = ? AND event_type = ?");

         pstmtc.clearParameters();        // clear the parms
         pstmtc.setLong(1, date);
         pstmtc.setInt(2, shotgun);
         rs = pstmtc.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            oakshotgun = true;         // shotgun event today
         }
         pstmtc.close();
      }

      //
      //  Special processing for Milwaukee CC - check if there are back tees
      //                                        after noon, and it is a weekday (Tues - Fri)
      //
      boolean mccGuestDay = false;

      if (club.equals( "milwaukee" ) && (day_name.equals("Tuesday") || day_name.equals("Wednesday") ||
          day_name.equals("Thursday") || day_name.equals("Friday"))) {

         pstmtc = con.prepareStatement (
            "SELECT dd " +
            "FROM teecurr2 " +
            "WHERE date = ? AND fb = 1 AND time > 1200 AND time < 1431");  // this day, back tee, noon to 2:30

         pstmtc.clearParameters();        // clear the parms
         pstmtc.setLong(1, date);
         rs = pstmtc.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mccGuestDay = true;         // Guest Times exist for this day
         }
         pstmtc.close();
      }

      
      //
      //  parm block to hold the wait lists 
      //
      parmItem parmWaitLists = new parmItem();          // allocate a parm block
      try {
          getItem.getWaitLists(date, courseName1, day_name, parmWaitLists, con);
      } catch (Exception e) {
          SystemUtils.buildDatabaseErrMsg("Error loading wait list data.", e.getMessage(), out, true);
      }
      
      /*** WAIT LIST DEBUG
      out.println("<!-- WAIT LISTS -->");
      out.print("<!-- count=" + parmWaitLists.count + " -->");
      out.print("<!-- id=" + parmWaitLists.id[0] + " -->");
      out.print("<!-- name=" + parmWaitLists.name[0] + " -->");
      out.print("<!-- course=" + parmWaitLists.courseName[0] + " -->");
      out.print("<!-- stime=" + parmWaitLists.stime[0] + " -->");
      out.print("<!-- etime=" + parmWaitLists.etime[0] + " -->");
      out.println("<!-- color=" + parmWaitLists.color[0] + " -->");
      */
      
      //
      //  Count the number of tee sheets displayed since last tomcat bounce
      //
      Calendar calCount = new GregorianCalendar();       // get todays date

      int hourCount = calCount.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23) Central Time

      SystemUtils.sheetCountsPro[hourCount]++;

      //
      //  Auto Refresh Controls
      //
      boolean autoRefresh = false;
      int arSecs = 0;

      //
      //  Custom for Old Warson - refresh the tee sheet every 4 minutes for proshop5 (bag room) between 7 AM and 6 PM.
      //
      if (club.equals( "oldwarson" ) && user.equalsIgnoreCase( "proshop5" )) {

         if (hourCount > 6 && hourCount < 18) {        // if between 7 AM and 6 PM (**hourCount is 0-23 CT**)

            autoRefresh = true;                        // use auto refresh
            arSecs = 240;                              // every 4 minutes
         }
      }

      //
      //  Custom for Cherry Hills - refresh the tee sheet every 10 minutes for proshop4 (bag room) between 6 AM and 6 PM.
      //
      if (club.equals( "cherryhills" ) && user.equalsIgnoreCase( "proshop4" )) {

         if (hourCount > 7 && hourCount < 19) {        // if between 7 AM and 7 PM CT (6 AM - 6 PM MT)

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

      //
      //  Custom for Inverness GC (IL) - refresh the tee sheet every 10 minutes for proshop4 & 5.
      //
      if (club.equals( "invernessgc" ) && (user.equalsIgnoreCase( "proshop4" ) || user.equalsIgnoreCase( "proshop5" ))) {

         if (hourCount > 6 && hourCount < 18) {        // if between 6 AM and 6 PM CT

            restrictAll = true;                        // restrict all tee times to view only
            disp_mnum = true;                          // display member numbers
            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

      //
      //  Custom for Brantford & The International - refresh the tee sheet every 10 minutes for proshop5.
      //
      if ((club.equals( "brantford" ) || club.equals( "international" )) && user.equalsIgnoreCase( "proshop5" )) {

         if (hourCount > 5 && hourCount < 17) {        // if between 6 AM and 6 PM CT

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }

         disp_mnum = true;                          // display member numbers
         restrictAll = true;                        // do not allow access to tee times (view only)
      }

      //
      //  Custom for Beverly GC for proshop5 - view only.
      //
      if ((club.equals( "beverlygc" ) || club.equals( "snoqualmieridge" )) && user.equalsIgnoreCase( "proshop5" )) {

         restrictAll = true;                        // do not allow access to tee times (view only)
      }

      //
      //  Custom for Mirasol CC - refresh the tee sheet every 10 minutes for proshop2
      //
      if (club.equals( "mirasolcc" ) && user.equalsIgnoreCase( "proshop2" )) {

         if (hourCount > 4 && hourCount < 16) {        // if between 5 AM and 5 PM CT (6 AM - 6 PM ET)

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

      //
      //  Custom for The Valley Club - refresh the tee sheet every 10 minutes for proshop5 (bag room) between 6 AM and 6 PM.
      //
      if (club.equals( "valleyclub" ) && user.equalsIgnoreCase( "proshop5" )) {

         if (hourCount > 7 && hourCount < 19) {        // if between 8 AM and 6 PM CT (7 AM - 5 PM MT)

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

      //
      //  Custom for Hallbrook - refresh the tee sheet every 10 minutes for proshop4 (bag room) between 6 AM and 6 PM.
      //
      if (club.equals( "hallbrookcc" ) && user.equalsIgnoreCase( "proshop4" )) {

         if (hourCount > 6 && hourCount < 18) {        // if between 6 AM and 6 PM CT 

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

      //
      //  Custom for Desert Highlands & Columbine & Sonnenalp - refresh the tee sheet every 9 minutes for proshop5 (bag room) between 7 AM and 7 PM.
      //
      if ((club.equals( "deserthighlands" ) || club.equals( "columbine" ) || club.equals( "sonnenalp" )) && user.equalsIgnoreCase( "proshop5" )) {

         if (hourCount > 7 && hourCount < 19) {        // if between 7 AM and 7 PM CT (6 AM - 6 PM MT)

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

      //
      //  Custom for wilmington & CC of Virginia - refresh the tee sheet every 10 minutes for proshop5 (locker room) between 6 AM and 6 PM ET.
      //
      if ((club.equals( "wilmington" ) || club.equals( "virginiacc" )) && user.equalsIgnoreCase( "proshop5" )) {

         if (hourCount > 5 && hourCount < 17) {        // if between 6 AM - 6 PM ET

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

      //
      //  Custom for Sunnehanna - refresh the tee sheet every 10 minutes for proshop2 (bag room) between 6 AM and 6 PM.
      //
      if (club.equals( "sunnehanna" ) && user.equalsIgnoreCase( "proshop2" )) {

         if (hourCount > 5 && hourCount < 17) {        // if between 5 AM and 5 PM CT (6 AM - 6 PM ET)

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

      //
      //  Custom for Outdoor CC - refresh the tee sheet every 10 minutes for proshop2 (bag room) between 6 AM and 6 PM.
      //
      if (club.equals( "outdoor" ) && user.equalsIgnoreCase( "proshop4" )) {

         if (hourCount > 5 && hourCount < 17) {        // if between 5 AM and 5 PM CT (6 AM - 6 PM ET)

            autoRefresh = true;                        // use auto refresh
            arSecs = 600;                              // every 10 minutes
         }
      }

     errorMsg = "Error building tee sheet summary.";

     //
     // Start Tee Sheet Summary
     //
     String courseClause = (courseName1.equals("-ALL-")) ? "" : "courseName = ? AND";
     String sqlQuery1 =  "";
     String sqlQuery1a =  "";
     String sqlQuery2 = "";
     String sqlQuery3 = "";
     String tmp_error = "";

     if (fives == 0) {
         // query  to count the total # of tee times available
         sqlQuery1 =  "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = \"\";"; //  AND restriction = \"\"
         // query to count the total # of tee time player positions occupied
         sqlQuery2 = "SELECT SUM(subtotal) AS total FROM (" +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player1 <> '' " +
             "UNION ALL " +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player2 <> '' " +
             "UNION ALL " +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player3 <> '' " +
             "UNION ALL " +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player4 <> '' " +
             ") AS gtotal;";
         // query to count the # of occupied tee times (full or partially full)
         sqlQuery3 = "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = '' AND (player1 <> '' OR player2 <> '' OR player3 <> '' OR player4 <> '');"; // AND restriction = ''
     } else {
         // query  to count the total # of tee times available
         sqlQuery1 =  "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = \"\";"; //  AND restriction = \"\"
         sqlQuery1a =  "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = \"\" AND rest5 = \"\";";  // restriction = \"\" AND
         // query to count the total # of tee time player positions occupied
        sqlQuery2 = "SELECT SUM(subtotal) AS total FROM (" +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player1 <> '' " +
             "UNION ALL " +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player2 <> '' " +
             "UNION ALL " +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player3 <> '' " +
             "UNION ALL " +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player4 <> '' " +
             "UNION ALL " +
             "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player5 <> '' " +
             ") AS gtotal;";
         // query to count the # of occupied tee times (full or partially full)
         sqlQuery3 = "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = '' AND (player1 <> '' OR player2 <> '' OR player3 <> '' OR player4 <> '' OR player5 <> '');"; // AND restriction = ''

     }

     int available_teetimes = 0;
     int available_slots = 0;
     int occupied_slots = 0;
     int full_teetimes = 0;
     int available_teetimes5 = 0;

     //out.println("<!-- " + sqlQuery1 + " -->");
     //out.println("<!-- " + sqlQuery1a + " -->");
     //out.println("<!-- " + sqlQuery2 + " -->");
     //out.println("<!-- " + sqlQuery3 + " -->");

     // 1st query
     tmp_error = "SQL - Available Tee Times";
     PreparedStatement pstmt1 = con.prepareStatement (sqlQuery1);
     pstmt1.clearParameters();

     if (courseName1.equals("-ALL-")) {
         pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
     } else {
         pstmt1.setString(1, courseName1);
         pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
     }

     rs = pstmt1.executeQuery();
     tmp_error = "SQL - Available Tee Times - Query ran";
     if (rs.next()) {
         available_teetimes = rs.getInt("total");
         //available_slots = available_teetimes * ((fives == 0) ? 4 : 5);
         available_slots = available_teetimes * 4; // fives will be added to this value from query 1a
     }

     // only run query 1a if fives==1
     if (fives == 1) {
         tmp_error = "SQL - Available 5th slot times";
         pstmt1 = con.prepareStatement (sqlQuery1a);
         pstmt1.clearParameters();

         if (courseName1.equals("-ALL-")) {
             pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
         } else {
             pstmt1.setString(1, courseName1);
             pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
         }

         rs = pstmt1.executeQuery();
         tmp_error = "SQL - Available 5th slot times - Query ran";
         if (rs.next()) {
             available_teetimes5 = rs.getInt("total");
             available_slots += available_teetimes5;
         }
     }

     // 2nd query
     tmp_error = "SQL - Occupied Player Positions";

     pstmt1 = con.prepareStatement (sqlQuery2);
     pstmt1.clearParameters();

     if (courseName1.equals("-ALL-")) {
         pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
         pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
         pstmt1.setLong(3, (year * 10000) + (month * 100) + day);
         pstmt1.setLong(4, (year * 10000) + (month * 100) + day);
         if (fives == 1) pstmt1.setLong(5, (year * 10000) + (month * 100) + day);
     } else {
         pstmt1.setString(1, courseName1);
         pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
         pstmt1.setString(3, courseName1);
         pstmt1.setLong(4, (year * 10000) + (month * 100) + day);
         pstmt1.setString(5, courseName1);
         pstmt1.setLong(6, (year * 10000) + (month * 100) + day);
         pstmt1.setString(7, courseName1);
         pstmt1.setLong(8, (year * 10000) + (month * 100) + day);
         if (fives == 1) {
             pstmt1.setString(9, courseName1);
             pstmt1.setLong(10, (year * 10000) + (month * 100) + day);
         }
     }

     rs = pstmt1.executeQuery();
     tmp_error = "SQL - Occupied Player Positions - Query ran";
     if (rs.next()) occupied_slots = rs.getInt("total");


     // 3rd query
     tmp_error = "SQL - Full Tee Times";

     pstmt1 = con.prepareStatement (sqlQuery3);
     pstmt1.clearParameters();

     if (courseName1.equals("-ALL-")) {
         pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
     } else {
         pstmt1.setString(1, courseName1);
         pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
     }

     rs = pstmt1.executeQuery();
     tmp_error = "SQL - Full Tee Times - Query ran";
     if (rs.next()) full_teetimes = rs.getInt("total");

     String tmpA_percent = "";
     String tmpB_percent = "";

     NumberFormat nf;
     nf = NumberFormat.getNumberInstance();

     /*out.println("<!-- occupied_slots=" + occupied_slots + " -->");
     out.println("<!-- available_slots=" + available_slots + " -->");
     out.println("<!-- full_teetimes=" + full_teetimes + " -->");
     out.println("<!-- available_teetimes=" + available_teetimes + " -->");
     out.println("<!-- slots%=" + (100 * occupied_slots / available_slots) + "  -->");
     out.println("<!-- times%=" + (100 * full_teetimes / available_teetimes) + "  -->");*/

     if (occupied_slots != 0 && available_slots != 0) {
         tmpA_percent = " &nbsp;(";
         if ((100 * occupied_slots / available_slots) < 1) {
             tmpA_percent += "<1";
         } else {
             tmpA_percent += nf.format(100 * occupied_slots / available_slots);
         }
         tmpA_percent += "%)";
     }
     if (full_teetimes != 0 && available_teetimes != 0) {
         tmpB_percent = " &nbsp;(";
         if ((100 * full_teetimes / available_teetimes) < 1) {
             tmpB_percent += "<1";
         } else {
             tmpB_percent += nf.format(100 * full_teetimes / available_teetimes);
         }
         tmpB_percent += "%)";
     }


     //
     // done running tee time summary queries


     //
     // Load PoP status colors into an array for quick access
     //

     String tmp_color = "";
     String [] aryPopStatusColors = new String [5];
     int tmp_id = 0;

     try {

         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT * FROM pace_status ORDER BY pace_status_sort");

         String tmp_name = "";

         while (rs.next()) {

             tmp_id = rs.getInt("pace_status_id");
             tmp_color = rs.getString("pace_status_color");
             aryPopStatusColors[tmp_id] = tmp_color;
         }

     } catch (Exception e) {

         SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
     }


     errorMsg = "Error building tee sheet for this date.";

     //
     //  Build the HTML page to prompt user for a specific time slot
     //
     out.println(SystemUtils.HeadTitle2("Proshop Tee Sheet"));

     if (autoRefresh == true && arSecs > 29) {         // should we refresh the tee sheet automatically??

         out.println("<meta http-equiv=\"Refresh\" content=\"" +arSecs+ "; url=/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "\">");
     }

     /*      no longer used - see #jump in url built by Proshop_jump
     out.println("<script type=\"text/javascript\">");          // Jump script
     out.println("<!--");

     out.println("function jumpToHref(anchorstr) {");

     out.println("if (location.href.indexOf(anchorstr)<0) {");

     out.println("location.href=anchorstr; }");
     out.println("}");
     out.println("// -->");
     out.println("</script>");                               // End of script
      */

     out.println("</HEAD>");

     // include files for dynamic calendars
     if (club.equals("beverlygc")) {  // Different style sheets needed for color-coding days in adv for BeverlyGC
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\">");
         out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");
     } else {
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
         out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
     }

     //out.println("<body onLoad=\"jumpToHref('#jump" + jump + "');\" bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
     out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");

     SystemUtils.getProshopSubMenu(req, out, lotteryS);        // required to allow submenus on this page

     //
     // Add hidden iframe for check-in and psk pos processing
     //
     out.println("<span style=\"position:absolute;top:0px;left:-100px\"><iframe src=\"about:blank\" id=\"fraCheckIn\" style=\"width:0px;height:0px\"></iframe></span>");
     out.println("<script type=\"text/javascript\">");
     out.println("function doCheckIn(teecurr_id, playerNum, courseNameT, imgId) {");
     out.println(" var iframe = document.getElementById('fraCheckIn');");
     out.println(" var doc = null;");
     out.println(" if (iframe.contentDocument) {");         // Firefox, Safari, Opera
     out.println("  doc = iframe.contentDocument;");
     out.println(" } else if (iframe.contentWindow) {");    // IE
     out.println("  doc = iframe.contentWindow;");
     out.println(" } else if (iframe.document) {");         // last ditch effor?
     out.println("  doc = iframe.document;");
     out.println(" }");
     out.println(" if (doc == null) {");
     out.println("  throw 'Unable to process check-in request.  Your browser does not seem to be supported.';");
     out.println(" } else {");
     out.println("  doc.location.href='/"+rev+"/servlet/Proshop_sheet_checkin?tid='+teecurr_id+'&pNum='+playerNum+'&course='+courseNameT+'&imgId='+imgId;");
     out.println(" }");
     out.println("}");
     out.println("</script>");

     //out.println("  document.getElementById('fraCheckIn').contentDocument.location.href='/"+rev+"/servlet/Proshop_sheet_checkin?tid='+teecurr_id+'&pNum='+playerNum+'&course='+courseNameT+'&imgId='+imgId;");

     //
     // Add floating calendar to left side of page
     //
     out.print("<div id=\"fCal\" style=\"position:absolute\" onmouseover=\"FCAL_show_cal()\" onmouseout=\"FCAL_hide_cal()\">");
     out.print("<div id=\"fCal_marker\"><img src=\"/" + rev + "/images/cal_marker.gif\" width=\"24\" height=\"83\" border=\"0\" alt=\"Cal\"></div>");
     out.print("<div id=cal_elem_2 style=\"display:none;position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println("</div>");
     out.println("<script type=\"text/javascript\">");
     out.println("var FCAL_ns = (navigator.appName.indexOf('Netscape') != -1);");
     out.println("");
     out.println("function FCAL_show_cal() {");
     out.println(" document.getElementById('cal_elem_2').style.display='block'");
     out.println(" document.getElementById('fCal_marker').style.display='none'");
     out.println("}");
     out.println("function FCAL_hide_cal() {");
     out.println(" document.getElementById('cal_elem_2').style.display='none'");
     out.println(" document.getElementById('fCal_marker').style.display='block'");
     out.println("}");
     out.println("function FCAL_position_cal(id, sx, sy) {");
     out.println("	var el=document.getElementById?document.getElementById(id):document.all?document.all[id]:document.layers[id];");
     out.println("	var px = document.layers ? '' : 'px';");
     out.println("	window[id + '_obj'] = el;");
     out.println("	if(document.layers)el.style=el;");
     out.println("	el.cx = el.sx = sx;");
     out.println("	el.cy = el.sy = sy;");
     out.println("	el.sP=function(x,y){this.style.left=x+px;this.style.top=y+px;};");
     out.println("	el.move_cal=function() {");
     out.println("		var pX, pY;");
     out.println("		pX = (this.sx >= 0) ? 0 : FCAL_ns ? innerWidth : ");
     out.println("		document.documentElement && document.documentElement.clientWidth ? ");
     out.println("		document.documentElement.clientWidth : document.body.clientWidth;");
     out.println("		pY = FCAL_ns ? pageYOffset : document.documentElement && document.documentElement.scrollTop ? ");
     out.println("		document.documentElement.scrollTop : document.body.scrollTop;");
     out.println("		if(this.sy<0) ");
     out.println("		pY += FCAL_ns ? innerHeight : document.documentElement && document.documentElement.clientHeight ? ");
     out.println("		document.documentElement.clientHeight : document.body.clientHeight;");
     out.println("		this.cx += (pX + this.sx - this.cx)/7;");
     out.println("		this.cy += (pY + this.sy - this.cy)/7;");
     out.println("		if (this.cy < FCAL_ceiling) this.cy = FCAL_ceiling;");
     out.println("		this.sP(this.cx, this.cy);");
     out.println("		setTimeout(this.id + '_obj.move_cal()', 40);");
     out.println("	}");
     out.println("	return el;");
     out.println("}");
     out.println("</script>");


     //
     //  Add Print Options Menu
     //
     if (SystemUtils.verifyProAccess(req, "TS_PRINT", con, out)) {
         out.println("<span id='xawmMenuPathImg-foreteesControlPanel' style='position:absolute;top:-50px'>");
         out.println("<img name='awmMenuPathImg-foreteesControlPanel' id='awmMenuPathImg-foreteesControlPanel' src='/" +rev+ "/web utilities/proshop/awmmenupath.gif' alt=''></span>");
         out.println("<script type=\"text/javascript\">var MenuLinkedBy=\"AllWebMenus [4]\", awmBN=\"824\"; awmAltUrl=\"\";</script>");
         out.println("<script charset=\"UTF-8\" src=\"/" +rev+ "/web utilities/proshop/foreteesControlPanel.js\" type=\"text/javascript\"></script>");
         out.println("<script type='text/javascript'>awmBuildMenu();</script>");
     }

     //
     //  Build Tee Sheet Page
     //
     out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

     out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
     out.println("<tr id=\"jump0\"><td align=\"center\">");

     out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // table for cmd tbl & cals
     out.println("<tr valign=\"top\"><td align=\"center\" rowspan=\"2\" width=\"150\">");

         //
         //  Add Print Menu Form
         //
         out.println("<form name=\"cpHlp\" action=\"/" +rev+ "/servlet/Proshop_sheet\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
         out.println("<input type=\"hidden\" name=\"print\" value=\"\">");
         out.println("<input type=\"hidden\" name=\"fsz\" value=\"\">");   // to be completed by foretees.js
         out.println("<input type=\"hidden\" name=\"excel\" value=\"\">");
         out.println("<input type=\"hidden\" name=\"order\" value=\"\">");
         out.println("<input type=\"hidden\" name=\"double_line\" value=\"\">");
         out.println("<input type=\"hidden\" name=\"csv\" value=\"\">");
         out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" +rev+ "\">");
         out.println("</form>");


         //
         //  Add Pace Report Form
         //
         out.println("<form method=post action=\"/v5/servlet/Proshop_report_pace\" name=frmPaceReport id=frmPaceReport target=\"_blank\">");
         out.println("<input type=hidden name=todo value=\"today\">");
         out.println("<input type=hidden name=cal_box_0 value=\"" + ((year * 10000) + (month * 100) + day) + "\">");
         out.println("<input type=hidden name=course value=\"" + courseName1 + "\">");
         out.println("</form>");


         //
         //  Output Client Scripts for CP
         //
         out.println("<script type=\"text/javascript\">");
         out.println("function openDiaryWindow() {");
         out.println(" w = window.open ('/" +rev+ "/servlet/Proshop_diary?index=" +num+ "&course=" +courseName1+ "&year=" +year+ "&month=" +month+ "&day=" +day+ "','diaryPopup','width=640,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
         out.println(" w.creator = self;");
         out.println("}");
         out.println("function checkAllIn() {");
         out.println(" var y = confirm('Are you sure you want to check in all players on this tee-sheet?');");
         out.println(" if (y != true) return;");
         out.println(" document.location.href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "&checkallin=true\";");
         out.println("}");
         out.println("function memberLookup() {");
         out.println(" var y = prompt('Enter the member number you would like to lookup.', '');");
         out.println(" if (y==null) return;");
         out.println(" y=y.replace(/^\\s+|\\s+$/g, '');"); // trim leading & trailing
         out.println(" if (y == '') return;");
         //out.println(" var ex = /^[0-9]{1,10}$/;"); // regex to enforce numeric only and 1-10 digits
         //out.println(" if (!ex.test(y)) { alert('Enter numeric characters only.'); return; }");
         out.println(" w = window.open ('/" +rev+ "/servlet/Proshop_member_lookup?mem_num='+y,'memberLookupPopup','width=480,height=200,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');"); // add modal=yes to non-ie browsers
         out.println(" w.creator = self;");
         //out.println(" window.showModalDialog('/" +rev+ "/servlet/Proshop_member_lookup?mem_num='+y,'memberLookupPopup','status:no;dialogWidth:620px;dialogHeight:280px;resizable:yes;center:yes;help=no');");
         out.println("}");
         out.println("function showTodaysPace() {");
         out.println(" var f = document.forms['frmPaceReport'];");
         out.println(" f.submit();");
         out.println("}");
         out.println("function showNotifications() {");
         out.println("}");
         out.println("</script>");

         //
         //  Build Control Panel HTML
         //
         out.println("<br><br><br>");
         out.println("<table border=\"1\" width=\"150\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"center\">");
         out.println("<tr>");
         out.println("<td align=\"center\"><font color=\"#000000\" size=\"3\"><b>Control Panel</b><br>");
         out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "\" title=\"Refresh This Page\" alt=\"Refresh\">");
         out.println("Refresh Sheet</a>");

         if (club.equals("midpacific") || club.equals("demov4")) {
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_report_midpacific\" target=\"_blank\" title=\"Report # of rounds played\" alt=\"Member Round Report\">");
             out.println("Member Round Report</a>");
         }

         //
         // if Abacus21, Jonas, Northstar, or TAI, and today
         //
         if (posAccess) {
            
             if ((parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" ) || parmp.posType.equals( "NorthStar" ) ||
                  parmp.posType.equals( "TAI Club Management" ) || parmp.posType.equals( "IBS" ) || parmp.posType.equals( "ClubSoft" )) &&
                  index == 0) {

                if (!courseName1.equals( "-ALL-" )) {

                   out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
                   out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "&print=pos\" target=\"_blank\" title=\"Generate POS Charges\" alt=\"Send POS Charges\">");
                   //out.println("<a href=\"javascript:void(0)\" onclick=\"return sendPOS()\" alt=\"Send POS Charges\">");
                   out.println("Send POS Charges</a>");
                }
             }

             if (!parmp.posType.equals( "" ) && !parmp.posType.equals( "None" ) && index == 0) {  // any POS type and today

                if (SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
                   out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
                   out.println("<a href=\"/" +rev+ "/servlet/Proshop_report_pos?date=" +date+ "&course=" +courseName1+ "\" target=\"_blank\" title=\"Generate POS Report For This Date\" alt=\"POS Report\">");
                   out.println("POS Report</a>");
                }
             }
         }

         if (!club.equals( "demov4" ) || !user.equalsIgnoreCase( "proshop5" )) {     // if NOT proshop5 on demov4 site (ProshopKeeper Testing)
      
            if (index < 3 && !courseName1.equals( "-ALL-" ) && SystemUtils.verifyProAccess(req, "TS_CTRL_FROST", con, out)) {   // if today and not course=all

               out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
               out.println("<a href=\"/" +rev+ "/servlet/Proshop_frost?course=" +courseName1+ "&index=" + index + "\" title=\"Adjust Tee Times for Frost Delay\" alt=\"Frost Delay\">");
               out.println("Frost Delay</a>");
            }

            if (SystemUtils.verifyProAccess(req, "TS_CTRL_TSEDIT", con, out)) {
                out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
                out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?index=" +num+ "&course=" +courseName1+ "&email=yes\" target=\"_top\" title=\"Edit This Tee Sheet - Send Emails\" alt=\"Edit Tee Sheet w/ Emails\">");
                out.println("Edit Sheet w/ Emails</a>");

                out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
                out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?index=" +num+ "&course=" +courseName1+ "&email=no\" target=\"_top\" title=\"Edit This Tee Sheet - Do Not Send Emails\" alt=\"Edit Tee Sheet w/o Emails\">");
                out.println("Edit Sheet w/o Emails</a>");

                out.println("</font></td></tr>");
            }

            if (index == 0) {            // only display when tee sheet is on current day and if user has reports access
          
                if (SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
                    out.println("<tr><td align=\"center\"><font size=\"2\"><a href=\"javascript:void(0)\" onclick=\"openDiaryWindow(); return false;\" title=\"Diary\" alt=\"Diary\">");
                    out.println("Make Diary Entry</a>");
                    out.println("</font></td></tr>");
                }
            }

            if (SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
                out.println("<tr><td align=\"center\"><font size=\"2\">");
                out.println("<a href=\"javascript:sendEmail('/" +rev+ "/servlet/Send_email', 'addToList')\" target=\"bot\" title=\"Send Email To All Members On This Tee Sheet\" alt=\"Send Email\">");
                out.println("Send Email to Members</a></td></tr>");
            }
                
            // build a form for sending the email
            out.println("<form name=\"pgFrm\" >");
            out.println("<input type=\"hidden\" name=\"nextAction\" value=\"\">");
            out.println("<input type=\"hidden\" name=\"" + ActionHelper.SEARCH_TYPE + "\" value=\"" + ActionHelper.SEARCH_TEESHEET + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
            out.println("</form><tr><td align=\"center\"><font size=\"2\">");

            if (index == 0 && (!parmp.posType.equals( "Pro-ShopKeeper" ) && !parmp.posType.equals( "ClubProphetV3" )) && checkinAccess)     // only display when tee sheet is on current day and POS is not Pro-ShopKeeper and user has checkin access
            {
            out.println("<a href=\"javascript:void(0)\" onclick=\"checkAllIn(); return false;\" title=\"Check In All\" alt=\"Check In All\">");
            out.println("Check In All</a>");
            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
            }

            out.println("<a href=\"javascript:void(0)\" onclick=\"memberLookup(); return false;\" title=\"Member Look-Up\" alt=\"Member Look-Up\">");
            out.println("Member Look-Up</a>");
            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");

            if (paceofplay != 0 && index == 0 && popAccess == true) {

                errorMsg = "Error getting pace of play info.";

                //
                // Decide if we are going to show the pace report link
                //
                int pace_count = 0;
                try {

                    pstmt1 = con.prepareStatement("" +
                            "SELECT COUNT(*) AS total " +
                            "FROM pace_entries pe, teecurr2 t " +
                            "WHERE pe.teecurr_id = t.teecurr_id " +
                            "AND t.date = ? " +
                            ((courseName1.equals("-ALL-")) ? "" : "AND t.courseName = ? ") +
                            "AND (" +
                                "pe.invert = 18 " +
                                "OR (" +
                                    "(pe.invert = 9 AND t.p91 = 1) OR " +
                                    "(pe.invert = 9 AND t.p92 = 1) OR " +
                                    "(pe.invert = 9 AND t.p93 = 1) OR " +
                                    "(pe.invert = 9 AND t.p94 = 1) OR " +
                                    "(pe.invert = 9 AND t.p95 = 1)" +
                                ") " +
                            ")");
                    pstmt1.clearParameters();
                    pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
                    if (!courseName1.equals( "-ALL-" )) pstmt1.setString(2, courseName1);
                    rs = pstmt1.executeQuery();

                    if (rs.next()) {

                        pace_count = rs.getInt(1);
                    }

                } catch (Exception e) {

                    SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
                }
                if (pace_count > 0) {
                    out.println("<a href=\"javascript:void(0)\" onclick=\"showTodaysPace(); return false;\" title=\"Pace Report\" alt=\"Pace Report\">");
                    out.println("Today's Pace (" + pace_count + ")</a>");
                    out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
                }
            }

            //
            //  See if we are in the timeless tees mode
            //
            int tmp_tlt = (Integer)session.getAttribute("tlt");
            boolean IS_TLT = (tmp_tlt == 1) ? true : false;

            if (IS_TLT) {

                //
                // Decide if we are going to show the manage notifications link
                //
                int notifications_count = 0;

                try {

                    pstmt1 = con.prepareStatement("" +
                           "SELECT COUNT(*) " +
                           "FROM notifications " +
                           "WHERE DATE(req_datetime) = ? AND converted = 0");
                    pstmt1.clearParameters();
                    pstmt1.setString(1, year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day));
                    //if (!courseName1.equals( "-ALL-" )) pstmt1.setString(2, courseName1);
                    rs = pstmt1.executeQuery();
                    if (rs.next()) notifications_count = rs.getInt(1);

                } catch (Exception e) {

                    SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
                }

                if (notifications_count > 0) {

                    out.println("<a href=\"/" + rev + "/servlet/Proshop_dsheet?index=" +num+ "&course=" +courseName1+ "&email=no\" title=\"Manage Notifications\" alt=\"Manage Notifications\" target=\"_top\">");
                    out.println("Manage Notifications (" + notifications_count + ")</a>");
                    out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
                }
            }

            if (lotteryS > 0) {               // if lottery supported 
         
               if (!lottA[0].equals("") && lstateA[0] > 3 && lottApproveAccess) { // check lstate1 to equals ?
                   out.println("<a href=\"/" + rev + "/servlet/Proshop_dsheet?mode=LOTT&index=" +num+ "&name=" + lottA[0] + "&course=" +courseName1+ "&returnCourse=" +courseName1+ "&hide=1\" title=\"Manage Lottery Results\" alt=\"Manage Lottery Results\" target=\"_top\">");
                   out.println("Approve Lottery</a>");
                   out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
               }
            }                  
         }       // end of IF PSK testing
         
         
         //
         //  Add option to view tee times during lotteries so pros can pre-book tee times
         //
         if (lotteryS > 0) {               // if lottery supported 

            errorMsg = "Error building lottery Hide/Show buttons.";

            if (!lottA[0].equals("") && lottApproveAccess && ((club.equals("theforestcc") || club.equals("gallerygolf")) || club.startsWith("demo"))) {     // if Pro has access and any lotteries defined

               lottloop1:
               for (i=0; i<count_lott; i++) {
                  
                  if (lstateA[i] > 0 && lstateA[i] < 4) {   // if any lotteries to hide or show

                     if (showlott.equalsIgnoreCase( "yes" )) {       // if we are currently showing the lottery times

                        out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "&showlott=no\" title=\"Hide Lottery Times\" alt=\"Hide Lottery Times\">");
                        out.println("Hide Lottery Times</a>");
                        out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");

                     } else {       // default or 'no' - Not currently showing the lottery times - add Show option

                        out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "&showlott=yes\" title=\"Show Lottery Times\" alt=\"Show Lottery Times\">");
                        out.println("Show Lottery Times</a>");
                        out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
                     }
                     break lottloop1;
                  }
               }
            }
         }
         
         i = 0;
            
            
         //
         // Build links to any/all wait lists accepting signups today
         //
         if (wlViewAccess || wlUpdateAccess || wlManageAccess) {          // If user has none of these access types, don't display waitlist links
             
             errorMsg = "Error building wait list links.";

             for (int z = 0; z < parmWaitLists.count; z++) {

                 if ( (parmWaitLists.courseName[z].equals(courseName1) || 
                      parmWaitLists.courseName[z].equals("-ALL-") || 
                      courseName1.equals("-ALL-")) && 
                      (index > 0 || (index == 0 && parmWaitLists.etime[z] > curr_time)) ) {

                     //out.println("<!-- index=" + index  + ", curr_time=" + curr_time + ", parmWaitLists.etime["+z+"]=" + parmWaitLists.etime[z] + " -->");

                     try {

                         parmWaitLists.unc_signups[z] = getWaitList.getListCount(parmWaitLists.id[z], (int)date, index, curr_time, true, con);

                     } catch (Exception ignore) {}

                     out.println("<a href=\"/" + rev + "/servlet/Proshop_waitlist?waitListId=" + parmWaitLists.id[z] + "&date=" + date + "&day=" + day_name + "&index=" +num+ "&returnCourse=" +courseName1+ "\" alt=\"New Wait List Entry\">"); //  target=\"_top\"
                     out.println("Wait List &nbsp;(" + parmWaitLists.unc_signups[z] + ")");
                     out.println("<br> " + SystemUtils.getSimpleTime(parmWaitLists.stime[z]) + " - " + SystemUtils.getSimpleTime(parmWaitLists.etime[z]) + "</a>");
                     out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");

                 }
             }
         }


         if (club.equals("tahoedonner") && index == 0) {
            out.println("<a href=\"/" + rev + "/servlet/Proshop_sheet?dumptoday\" title=\"Download Today's Tee Sheet\" alt=\"Download Today's Tee Sheet\" target=\"_top\">");
            out.println("Download Today's Tee Sheet</a>");
            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
         }

         out.println("</font></td></tr></table>");         // close out the CP table

         //
         // END of Control Panel
         //
         
         out.println("</td>");                                 // end of column for CP

      
      errorMsg = "Error building course selection options.";

      //
      //   Add calendars
      //
      out.println("<td align=\"center\">");     // column for calendars & course selector

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      if (multi != 0) {           // if multiple courses supported for this club

         String caldate = month + "/" + day + "/" + year;       // create date for _jump

         //
         //  use 2 forms so you can switch by clicking either a course or a date
         //
         if (courseCount < 5) {        // if < 5 courses, use buttons

            out.println("<p><font size=\"3\">");
            out.println("<b>Select Course or Date:</b>&nbsp;&nbsp;");

            for (i=0; i < course.size(); i++) {    // allow one more for -ALL-

               courseName = course.get(i);      // get course name from array
               
               out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?i" +num+ "=a&jump=select&calDate=" +caldate+ "&course=" +courseName+ "\" style=\"color:blue\" target=\"_top\" title=\"Switch to new course\" alt=\"" +courseName+ "\">");
               if (club.equals("congressional")) {
                   out.println(congressionalCustom.getFullCourseName(date, day, courseName) + "</a>");
               } else {
                   out.println(courseName + "</a>");
               }
               out.println("&nbsp;&nbsp;&nbsp;");
            }
            out.println("</p>");

         } else {     // use drop-down menu

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" name=\"cform\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"i" + num + "\" value=\"\">");   // use current date
            out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +caldate+ "\">");

            out.println("<b>Course:</b>&nbsp;&nbsp;");
            out.println("<div id=\"awmobject1\">");                      // allow menus to show over this box
            out.println("<select size=\"1\" name=\"course\" onChange=\"document.cform.submit()\">");

            for (i=0; i < course.size(); i++) {    // allow one more for -ALL-

               courseName = course.get(i);      // get course name from array
               
               if (courseName.equals( courseName1 )) {
                  out.println("<option selected value=\"" + courseName + "\">" + (club.equals("congressional") ? congressionalCustom.getFullCourseName(date, day, courseName) : courseName) + "</option>");
               } else {
                  out.println("<option value=\"" + courseName + "\">" + (club.equals("congressional") ? congressionalCustom.getFullCourseName(date, day, courseName) : courseName) + "</option>");
               }
            }
            out.println("</select></div>");
            out.println("</form>");
         }
      } // end if multi

      errorMsg = "Error building calendars.";

     //
     //  start a new form for the dates so you can switch by clicking either a course or a date
     //
     out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\" name=\"frmLoadDay\">");
     out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
     out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName1+ "\">");
     out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
     out.println("</form>");

     // table for calendars built by js
     out.println("<table align=center border=0 height=150>\n<tr valign=top>\n<td>");    // was 190 !!!

     out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

     out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");

     out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

     out.println("</td>\n<tr>\n</table>");

     Calendar cal_date = new GregorianCalendar();

     int today = (int)Utilities.getDate(con, 0);

     //out.println("<!-- today="+today+" -->");

     int cal_year = today / 10000;
     temp = cal_year * 10000;
     int cal_month = today - temp;
     temp = cal_month / 100;
     temp = temp * 100;
     int cal_day = cal_month - temp;
     cal_month = cal_month / 100;
     
     cal.set(cal_year, cal_month-1, cal_day);

     int cal_year2 = cal_year; // save these for comparison later
     int cal_month2 = cal_month;

     out.println("<script type=\"text/javascript\">");

     out.println("<!-- ");
     out.println("var g_cal_bg_color = '#F5F5DC';");
     out.println("var g_cal_header_color = '#8B8970';");
     out.println("var g_cal_border_color = '#8B8970';");

     out.println("var g_cal_count = 3;"); // number of calendars on this page
     out.println("var g_cal_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

     out.println("var g_hilite_curr_day = new Boolean(true);");


     // set calendar date parts
     out.println("g_cal_month[0] = " + cal_month + ";");
     out.println("g_cal_year[0] = " + cal_year + ";");
     out.println("g_cal_beginning_month[0] = " + cal_month + ";");
     out.println("g_cal_beginning_year[0] = " + cal_year + ";");
     out.println("g_cal_beginning_day[0] = " + cal_day + ";");
     out.println("g_cal_ending_month[0] = " + cal_month + ";");
     out.println("g_cal_ending_day[0] = 31;");
     out.println("g_cal_ending_year[0] = " + cal_year + ";");

     cal_date.add(Calendar.MONTH, 1); // add a month
     cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
     cal_year = cal_date.get(Calendar.YEAR);
     out.println("g_cal_beginning_month[1] = " + cal_month + ";");
     out.println("g_cal_beginning_year[1] = " + cal_year + ";");
     out.println("g_cal_beginning_day[1] = 0;");
     cal_date.add(Calendar.MONTH, -1); // subtract a month

     cal_date.add(Calendar.YEAR, 1); // add a year
     cal_year = cal_date.get(Calendar.YEAR);
     cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
     out.println("g_cal_ending_month[1] = " + cal_month + ";");
     out.println("g_cal_ending_day[1] = " + cal_day + ";");
     out.println("g_cal_ending_year[1] = " + cal_year + ";");
     cal_date.add(Calendar.YEAR, -1); // subtract a year

     // now lets roll ahead to the day this sheet is for
     cal_date.add(Calendar.DAY_OF_MONTH, index); // add the # of days ahead of today this tee sheet is for

     out.println("g_current_day = " + cal_date.get(Calendar.DAY_OF_MONTH) + ";");
     out.println("g_current_month = " + (cal_date.get(Calendar.MONTH) + 1) + ";");
     out.println("g_current_year = " + cal_date.get(Calendar.YEAR) + ";");
     
     // if the date we're displaying is in the current month then make the second calendar
     // start on the next month - else make it start on the month this tee sheet is for
     if (cal_date.get(Calendar.MONTH) + 1 == cal_month2 && cal_date.get(Calendar.YEAR) == cal_year2) {

         // the date this tee sheet is for IS in the current month
         cal_date.add(Calendar.MONTH, 1);
         cal_year = cal_date.get(Calendar.YEAR);
         cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

         out.println("g_cal_month[1] = " + cal_month + ";");
         out.println("g_cal_year[1] = " + cal_year + ";");
         out.println("g_cal_month[2] = g_cal_month[0];");
         out.println("g_cal_year[2] = g_cal_year[0];");

     } else {

         // the date this tee sheet is for is NOT in the current month
         cal_year = cal_date.get(Calendar.YEAR);
         cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

         out.println("g_cal_month[1] = " + cal_month + ";");
         out.println("g_cal_year[1] = " + cal_year + ";");
         out.println("g_cal_month[2] = " + cal_month + ";");
         out.println("g_cal_year[2] = " + cal_year + ";");

     }

     // set the floating calendar to use the same date parts from the 2nd cal
     out.println("g_cal_beginning_month[2] = g_cal_beginning_month[0];");
     out.println("g_cal_beginning_year[2] = g_cal_beginning_year[0];");
     out.println("g_cal_beginning_day[2] = g_cal_beginning_day[0];");
     out.println("g_cal_ending_month[2] = g_cal_ending_month[1];");
     out.println("g_cal_ending_day[2] = 31;");
     out.println("g_cal_ending_year[2] = g_cal_ending_year[1];");

     if (club.equals("beverlygc")) {   //add color coding for Beverly GC
         
         out.print("var daysArray = new Array(");
         int js_index = 0;
         int max = 365;
         int[] days = new int[max+1];
         for (int m=0; m<max+1; m++) {
             if (m<=7) {
                 days[m] = 1;
             } else {
                 days[m] = 0;
             }
         }
         
         for (js_index = 0; js_index <= max; js_index++) {
             out.print(days[js_index]);
             if (js_index != max) out.print(",");
         }
         out.println(");");

         out.println("var max = " + max + ";");
     }
     
      out.println("// -->");
     out.println("</script>");

     out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
     out.println("<script type=\"text/javascript\">\ndoCalendar('1');\n</script>");
     out.println("<script type=\"text/javascript\">\ndoCalendar('2');\n</script>");

     out.println("<script type=\"text/javascript\">");
      out.println("<!-- ");
      out.println("function openHistoryWindow(index, course, time, fb, tid) {");
      out.println("w = window.open ('/" +rev+ "/servlet/Proshop_teetime_history?index=' +index+ '&course=' +course+ '&time=' +time+ '&fb=' +fb+ '&tid=' + tid + '&history=yes','historyPopup','width=800,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
      out.println("w.creator = self;");
      out.println("}");
      out.println("function openPaceWindow(tid) {");
      out.println("w = window.open ('/" +rev+ "/servlet/Proshop_pace?tid=' +tid,'pacePopup','width=690,height=470,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
      out.println("w.creator = self;");
      out.println("}");
      out.println("function openPaceWindowDetails(tid) {");
      out.println("w = window.open ('/" +rev+ "/servlet/Proshop_pace?tid=' +tid+ '&details','pacePopup','width=690,height=470,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
      out.println("w.creator = self;");
      out.println("}");
      out.println("function openCSGWindow(index, course, time, fb) {");
      out.println("w = window.open ('/" +rev+ "/servlet/Proshop_sheet?index=' +index+ '&course=' +course+ '&time=' +time+ '&fb=' +fb+ '&print=pos','csgPopup','width=800,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
      out.println("w.creator = self;");
      out.println("}");
      out.println("function openNotesWindow(index, course, time, fb) {");
      //out.println("if (w != null) w.focus();");
      out.println("w = window.open ('/" +rev+ "/servlet/Proshop_sheet?index=' +index+ '&course=' +course+ '&time=' +time+ '&fb=' +fb+ '&notes=yes','notesyPopup','width=640,height=360,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
      out.println("w.creator = self;");
      out.println("}");
      out.println("// -->");
     out.println("</script>");






     //
     //  Add Tee Sheet Notes
     //
     if (tsNotesView) {

         out.println("<table cellpadding=0 cellspacing=0 style=\"border: 2px solid darkGreen\">");
         //out.println("<tr><td align=\"center\" bgcolor=\"darkGreen\">");
         //out.println("<font size=\"4\" color=\"white\">");
         //out.println("<b>Tee Sheet Notes</b>");
         //out.println("</font></td></tr>");
         out.println("<tr><td style=\"padding: 5px\" align=\"center\">");
         out.println("<div id=\"fTSHeader\" width=\"100%\" onclick=\"toggleTSN()\" style=\"cursor: pointer; padding: 5px; background-color: darkGreen; font-size: 16px; color: white; font-weight: bold\">");
         out.println("&nbsp; &nbsp; &nbsp; Golf Shop Tee Sheet Notes &nbsp; &nbsp; &nbsp;");
         out.println("</div>");
         out.println("<div id=\"fTSNotes\" width=\"100%\">"); // style=\"position:relative\" onmouseover=\"fTSNotes_show()\" onmouseout=\"fTSNotes_hide()\"
         out.println("<iframe src=\"/" + rev + "/servlet/Proshop_sheet_notes?date=" + date + "&activity_id=0\" scrolling=\"no\" id=\"fraTSNotes\" name=\"fraTSNotes\" width=\"100%\" style=\"width:485px;height:145px\" frameborder=\"0\"></iframe>");
         out.println("</div></td></tr>");
         out.println("</table><br>");

         out.println("<script type=\"text/javascript\">");
         out.println("var fTSN_visible = false;");
         out.println("function toggleTSN() {");
         out.println(" fTSN_visible = (!fTSN_visible);");
         out.println(" if (fTSN_visible) {");
         out.println("  fTNotes_show();");
         out.println(" } else {");
         out.println("  fTNotes_hide();");
         out.println(" }");
         out.println("}");
         out.println("function fTNotes_show() {");
         out.println(" document.getElementById('fTSNotes').style.display='block'");
         out.println("}");
         out.println("function fTNotes_hide() {");
         out.println(" document.getElementById('fTSNotes').style.display='none'");
         out.println("}");
         out.println("</script>");

     }




     out.println("</td><td width=\"150\" rowspan=\"2\" align=\"right\">");

     errorMsg = "Error building tee sheet summary.";

     //
     // Start Tee Sheet Summary
     //
     out.println("<br><br><table width=\"150\" border=\"1\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"center\">");
     out.println("<tr><td align=\"center\"><font color=\"#000000\" size=\"3\"><b>Tee Sheet Summary</b><br></font></td></tr>");

     out.println("<tr><td>");

     out.println("<table border=\"0\" cellspacing=\"3\" cellpadding=\"1\">");

     out.println("<tr>");
     out.println("<th></th>");
     out.println("<th nowrap><font size=\"2\" color=\"#FFFFFF\">Total</th>");
     out.println("<th><font size=\"2\" color=\"#FFFFFF\">Taken</font></th>");
     out.println("</tr>");

     out.println("<tr>");
     out.println("<td nowrap><font size=\"2\" color=\"#FFFFFF\">");
     out.println("Tee Times:</td>");
     out.println("<td><font size=\"2\" color=\"#FFFFFF\">" + available_teetimes + "</font></td>");
     out.println("<td nowrap><font size=\"2\" color=\"#FFFFFF\">" + full_teetimes + tmpB_percent + "</font></td>");
     out.println("</tr>");

     out.println("<tr>");
     out.println("<td nowrap><font size=\"2\" color=\"#FFFFFF\">");
     out.println("Player Slots:</td>");
     out.println("<td><font size=\"2\" color=\"#FFFFFF\">" + available_slots + "</font></td>");
     out.println("<td nowrap><font size=\"2\" color=\"#FFFFFF\">" + occupied_slots + tmpA_percent + "</font></td>");
     out.println("</tr>");

     out.println("</table>");

     out.println("</td></tr></table>"); // end tee sheet summary

     out.println("</td></tr>");
     out.println("</table>");

     //out.println("<tr><td>"); // colspan=2

     //**********************************************************
     //  Continue with instructions and tee sheet
     //**********************************************************
/*
     out.println("<table cellpadding=\"3\" align=\"center\" width=\"90%\">");
     out.println("<tr><td bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
     out.println("<b>Instructions:</b>  To select a tee time, ");
     out.println("just click on the button containing the time (1st column). ");
     out.println("Empty 'player' cells indicate available positions. Click on empty box near names to ");
     out.println("'check them in'.  Special Events and Restrictions, if any, are colored (see legend below).");
     if ((club.equals( "oakmont" ) && oakshotgun == false && (day_name.equals("Wednesday") ||
         day_name.equals("Friday"))) || (mccGuestDay == true)) {
         out.println("<br>Times when Multiple Guests are allowed are indicated by the green time button.");
     }
     if (parm.constimesp > 1) {              // if consecutive tee times supported
         out.println("<br>To make multiple consecutive tee times, select the number of tee times next to the ");
         out.println("earliest time desired.  Then select that time.  The following time(s) must be available.");
     }
     if (club.equals( "medinahcc" ) || club.equals( "bearpath" )) {
         out.println("<br>Colored Time Buttons: Member Times are green, Starter Times are red, Outside Play Times are blue.");
     }
     out.println("</font></td></tr></table>");
*/
     //
     //
     // end upper portion of tee sheet
     //out.println("</td></tr></table><br>");



     // display date and course name for tee sheet
     out.println("<font size=\"5\">");
     out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
     if (!courseName1.equals( "" )) {
         if (club.equals("congressional")) {
             out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + congressionalCustom.getFullCourseName(date, day, courseName1) + "</b>");
         } else {
             out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + courseName1 + "</b>");
         }
     }
     out.println("</font><font size=\"2\">");

     errorMsg = "Error building tee sheet legend.";

      //
      // If there is an event, restriction or lottery then show the applicable legend
      //
      if ((!eventA[0].equals( "" ) || !restA[0].equals( "" ) || !lottA[0].equals( "" )) && (sysConfigEvent || sysConfigRest || sysConfigLott)) {

         // legend title
         out.println("<br><b>Tee Sheet Legend</b> (click on buttons to view info)<br>");

         if (sysConfigEvent) {
            
            for (i=0; i<count_event; i++) {     // check for events
             
                if (!eventA[i].equals( "" )) {

                   out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +eventA[i]+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                   out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolorA[i] + "\">" + eventA[i] + "</button></a>");
                   out.println("&nbsp;&nbsp;&nbsp;");
                }
             }
         }  // end if sysConfigEvent

         // show any restrictions if proshop user has appropriate access
         if (sysConfigRest) {
             
            for (i=0; i<count_rest; i++) {     // check for restrictions
             
               if (!restA[i].equals( "" )) {

                   out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?rest=" + mrest_ids[i] + "', 'newwindow', 'height=380, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                   out.println("<button type=\"button\" style=\"text-decoration:none; background:" +rcolorA[i]+ "\">" +restA[i]+ "</button></a>");
                   out.println("&nbsp;&nbsp;&nbsp;");
                }
             }
         }  // end if sysConfigRest

         //
         //  Custom check for Cordillera Canned Restrictions - add to legend
         //
         if (club.equals( "cordillera" ) && dateShort > 413 && dateShort < 1101) {

            out.println("<button type=\"button\" style=\"background:" +cordRestColor+ "\">Lodge Tee Times</button>");
            out.println("&nbsp;&nbsp;&nbsp;");

            if (dateShort > 531 && dateShort < 1001) {

               out.println("<button type=\"button\" style=\"background:" +cordStarterColor+ "\">Starter Times</button>");
               out.println("&nbsp;&nbsp;&nbsp;");
            }
         }
        

         // show any lottery if proshop user has appropriate access
         if (sysConfigLott && lotteryS > 0) {
             
            for (i=0; i<count_lott; i++) {     // check for restrictions
             
                if (!lottA[i].equals( "" ) && lstateA[i] < 5) {

                   out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?lottery=" +lottA[i]+ "', 'newwindow', 'height=480, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                   out.println("<button type=\"button\" style=\"text-decoration:none; background:" +lcolorA[i]+ "\">" +lottA[i]+ "</button></a>");
                   out.println("&nbsp;&nbsp;&nbsp;");
                }
            }
         }
         
         i = 0;

      } else {          // no events, restrictions or lotteries for this day

         out.println("<br><b>Tee Sheet Legend</b>");

         //
         //  Custom check for Cordillera Canned Restrictions - add to legend
         //
         if (club.equals( "cordillera" ) && dateShort > 413 && dateShort < 1101) {

            out.println("<br>");
            out.println("<button type=\"button\" style=\"background:" +cordRestColor+ "\">Lodge Tee Times</button>");

            if (dateShort > 531 && dateShort < 1001) {

               out.println("&nbsp;&nbsp;&nbsp;");
               out.println("<button type=\"button\" style=\"background:" +cordStarterColor+ "\">Starter Times</button>");
            }
         }
         
      }
     
      //
      // Display a button for any active wait lists currently running
      //
      if (wlViewAccess || wlUpdateAccess || wlManageAccess) {          // If user has none of these access types, don't display waitlist links
          for (int z = 0; z < parmWaitLists.count; z++) {
             /*
              * THIS BUTTON WILL TAKE THE USER TO THE WAIT LIST MANAGEMENT PAGE
             out.println("<button type=\"button\" style=\"background:" + parmWaitLists.color[z] + "\" " +
                     "onclick=\"parent.location.href='/" + rev + "/servlet/Proshop_dsheet?mode=WAITLIST&name=" + parmWaitLists.name[z] + "&index=" + index + "&course=" + parmWaitLists.courseName[z] + "'\">" + parmWaitLists.name[z] + "</button>");
             */

             // use parent.location - to open in a new window 

             // this if block can be used to hide todays wait list if it has expired 
             //if (index > 0 || (index == 0 && parmWaitLists.etime[z] > curr_time)) {

                out.println("<button type=\"button\" style=\"background:" + parmWaitLists.color[z] + "\" " +
                     "onclick=\"location.href='/" + rev + "/servlet/Proshop_waitlist?waitListId=" + parmWaitLists.id[z] + "&date=" + date + "&day=" + day_name + "&index=" +num+ "&returnCourse=" +courseName1+ "'\">" + parmWaitLists.name[z] + "</button>");
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
             //}
          }
      }
     
      //  Do not display abbreviations portion for tee sheet legend for Desert Forest GC
      if (club.equals("desertforestgolfclub") || club.equals("sawgrass")) {

          out.println("<br>");
          
      } else {

          out.println("<br></font><font size=\"1\">");

          out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event<br>");

          out.println("<b>C/W:</b>&nbsp;&nbsp;&nbsp;&nbsp;");

          for (int ic=0; ic<16; ic++) {

             if (!parmc.tmodea[ic].equals( "" )) {
                out.println(parmc.tmodea[ic]+ " = " +parmc.tmode[ic]+ "&nbsp;&nbsp;&nbsp;");
             }
          }
          out.println("<br>(9 = 9 holes)");

          if (checkinAccess == true && index <= checkboxdays) {
              out.println("&nbsp;&nbsp;&nbsp;<b>X:</b> Check In/Out Players");
          }

          if (updateAccess) {
              out.println("&nbsp;&nbsp;&nbsp;<b>N:</b> Notes Attached (click to view)");
              out.println("&nbsp;&nbsp;&nbsp;<b>H:</b> Tee Time History");
          }

          if (paceofplay != 0 && index == 0 && popAccess == true) out.println("&nbsp;&nbsp;&nbsp;<b>PP:</b> Pace of Play");

          if (parmp.posType.equals( "ClubSystems Group" ) && index == 0 && !courseName1.equals( "-ALL-" )) {

             out.println("&nbsp;&nbsp;&nbsp;<b>P:</b> Send POS Charges");
          }

          if (club.equals( "interlachen" ) || club.equals( "cherryhills" )) {      // if Interlachen or Cherry Hills

             out.println("&nbsp;&nbsp;&nbsp;<b>CA:</b> # of Caddies Assigned to Group");
          }
          if (club.equals( "cordillera" )) {                  // if Cordillera

             out.println("&nbsp;&nbsp;&nbsp;<b>FC:</b> Forecaddie Requested");
          }
          if (useTOcol == true && index == 0) {        // if Pecan Plantation (or others) and today

             out.println("&nbsp;&nbsp;&nbsp;<b>TO:</b> Teed Off");
          }
          if (club.equals( "medinahcc" )) {                  // if Medinah

             out.println("&nbsp;&nbsp;&nbsp;<b>P:</b> Print Tee Time");
          }
          if (parmp.pos_paynow == 1 && index == 0) {          // if Pay Now feature and today

             out.println("<br><b>PN:</b> Pay Now &nbsp;&nbsp;&nbsp;<b>PD:</b> Paid");
          }

          out.println("</font>");
      }

      //
      // DONE WITH LEGEND
      //
      
      errorMsg = "Error processing member notices.";

      //
      //**********************************************
      //   Check for Member Notice from Pro
      //**********************************************
      //
      String memNoticeMsg = verifySlot.checkMemNotice(date, 0, 0, courseName1, day_name, "teesheet", true, con);
      
      if (!memNoticeMsg.equals("")) {
          
          int notice_mon = 0;
          int notice_tue = 0;
          int notice_wed = 0;
          int notice_thu = 0;
          int notice_fri = 0;
          int notice_sat = 0;
          int notice_sun = 0;
          
          String notice_msg = "";
          String notice_bgColor = "";
          
          try {
              
              // Get relevent member notice data from database
              ResultSet notice_rs = null;
              PreparedStatement notice_pstmt = con.prepareStatement(
                      "SELECT mon, tue, wed, thu, fri, sat, sun, message, bgColor " +
                      "FROM mem_notice " +
                      "WHERE sdate <= ? AND edate >= ? AND " +
                      "(courseName = ? OR courseName = ?) AND teesheet=1 AND proside=1");
              
              notice_pstmt.clearParameters();        // clear the parms and check player 1
              notice_pstmt.setLong(1, date);
              notice_pstmt.setLong(2, date);
              notice_pstmt.setString(3, courseName1);
              notice_pstmt.setString(4, "-ALL-");
              notice_rs = notice_pstmt.executeQuery();      // execute the prepared stmt
              
              out.println("<br><br><table border=\"2\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"0\" cellspacing=\"0\">");
              out.println("<tr><td><table border=\"0\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
              out.println("<tr><td align=\"center\" valign=\"center\"><font size=\"4\"><b>*** Important Notice ***</b></font></td></tr>");
              
              while (notice_rs.next()) {
                  
                  notice_mon = notice_rs.getInt("mon");
                  notice_tue = notice_rs.getInt("tue");
                  notice_wed = notice_rs.getInt("wed");
                  notice_thu = notice_rs.getInt("thu");
                  notice_fri = notice_rs.getInt("fri");
                  notice_sat = notice_rs.getInt("sat");
                  notice_sun = notice_rs.getInt("sun");
                  notice_msg = notice_rs.getString("message");
                  notice_bgColor = notice_rs.getString("bgColor");
                  
                  
                  if ((notice_mon == 1 && day_name.equals( "Monday")) || (notice_tue == 1 && day_name.equals( "Tuesday")) || (notice_wed == 1 && day_name.equals( "Wednesday")) ||
                          (notice_thu == 1 && day_name.equals( "Thursday")) || (notice_fri == 1 && day_name.equals( "Friday")) || (notice_sat == 1 && day_name.equals( "Saturday")) ||
                          (notice_sun == 1 && day_name.equals( "Sunday"))) {
                      
                      out.println("<tr>");
                      if (!notice_bgColor.equals("")) {
                          out.println("<td width=\"700\" bgColor=\"" + notice_bgColor + "\" align=\"center\">");
                      } else {
                          out.println("<td width=\"700\" align=\"center\">");
                      }
                      out.println("<font size=\"2\">" + notice_msg + "</font></td></tr>");
                  }
                  
              }  // end WHILE loop
              
              out.println("</table></td></tr></table>");
              
              notice_pstmt.close();

         } catch (Exception e1) {

             out.println(SystemUtils.HeadTitle("DB Error"));
             out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
             out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
             out.println("<BR><BR><H2>Database Access Error</H2>");
             out.println("<BR><BR>Unable to access the Database.");
             out.println("<BR>Please try again later.");
             out.println("<BR><BR>If problem persists, contact your club manager.");
             out.println("<BR><BR>" + e1.getMessage());
             out.println("<BR><BR>");
             out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
             out.println("</CENTER></BODY></HTML>");
             out.close();
         }
     }



      //
      //  Custom for Cherry Hills & Hallbrook - proshop4 (bag room) - display view-only tee sheet with bag room #s
      //
      if ((club.equals( "cherryhills" ) || club.equals( "hallbrookcc" )) && user.equalsIgnoreCase( "proshop4" )) {

         restrictAll = true;           // restrict all tee times to view only
         disp_mnum = false;            // use this flag to display the bag numbers
         disp_hndcp = false;           // do not use this flag
      }

      //
      //  Custom for Desert Highlands & Columbine & Valley Club - proshop5 (bag room) - display view-only tee sheet with bag room #s
      //
      if ((club.equals( "deserthighlands" ) || club.equals( "columbine" ) || club.equals( "wilmington" ) || club.equals( "valleyclub" ) ||
           club.equals( "sonnenalp" )) &&
           user.equalsIgnoreCase( "proshop5" )) {

         restrictAll = true;           // restrict all tee times to view only
         disp_mnum = true;             // use this flag to display the bag numbers
         disp_hndcp = false;           // do not use this flag
      }

      //
      //  Custom for CC of Virginia - proshop5 - display view-only tee sheet
      //
      if ((club.equals( "virginiacc" ) && user.equalsIgnoreCase( "proshop5" )) ||
          (club.equals( "sciotoreserve" ) && !user.equalsIgnoreCase( "proshop1" ) && !user.equalsIgnoreCase( "proshop4tea" )) || 
          (club.equals( "mediterra" ) && user.equals( "proshop5" )) ) {

         restrictAll = true;           // restrict all tee times to view only
      }

      //
      //  Custom for Tavistock CC - proshop3 - display view-only tee sheet for GM's use
      //
      if (club.equals( "tavistockcc" ) && user.equalsIgnoreCase( "proshop3" )) {

         restrictAll = true;           // restrict all tee times to view only
      }

      //
      //  Custom for Sunnehanna - proshop2 (bag room) - display view-only tee sheet with bag room #s
      //
      if (club.equals( "sunnehanna" ) && user.equalsIgnoreCase( "proshop2" )) {

         restrictAll = true;           // restrict all tee times to view only
         disp_mnum = false;            // display the bag slot numbers
         disp_hndcp = false;           // do not use this flag
      }
       
      //
      //  Custom for Orchid Island, Berkeley Hall, & Grandezza - proshop2 - display view-only tee sheet with member num #
      //
      if ( (club.equals( "orchidisland" ) && user.equalsIgnoreCase( "proshop2" )) || 
           (club.equals( "berkeleyhall" ) && user.equalsIgnoreCase( "proshop2" )) ||
           (club.equals( "grandezzacc" ) && user.equalsIgnoreCase( "proshop2" )) ) {

         restrictAll = true;           // restrict all tee times to view only
         disp_mnum = true;             // display the member numbers
         disp_hndcp = false;           // do not use this flag
      }
      
      //  Override customs and default course settings if proshop user is set to display hdcp, mnum, or bag#
      if (dispHdcpOption) disp_hndcp = true;
      if (dispMnumOption) disp_mnum = true;
      if (dispBagOption) disp_bag = true;

      //
      // start tee sheet header
      //
      out.println("<img src=\"/" + rev + "/images/shim.gif\" id=\"cal_cap\" name=\"cal_cap\" width=\"1\" height=\"1\" border=\"0\"><br>");
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"90%\">");

      out.println("<tr bgcolor=\"#336633\">");
         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Time</b></u>");
            out.println("</font></td>");

         if (parm.constimesp > 1) {         // if Consecutive Tee Times allowed

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>#</b></u>");
            out.println("</font></td>");
         }

//         if (courseName1.equals( "-ALL-" )) {
         if (multi != 0) {                          // case 1509

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Course</b></u>");
               out.println("</font></td>");
         }

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>F/B</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;<u><b>Player 1</b></u>");
            out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
            if (disp_hndcp == true) {
               out.println("&nbsp;&nbsp;<u>hndcp</u>");
            }
            if (disp_mnum == true) {
               // Display 'User' instead for Charlotte CC
               if (club.equals("charlottecc") || club.equals("loxahatchee")) {
                   out.println("&nbsp;&nbsp;<u>User</u>");
               } else {
                   out.println("&nbsp;&nbsp;<u>Mem#</u>");
               }
            }
            if (disp_bag == true) {
               out.println("&nbsp;&nbsp;<u>Bag#</u>");
            }
            out.println("&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;<u><b>Player 2</b></u>");
            out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
            if (disp_hndcp == true) {
               out.println("&nbsp;&nbsp;<u>hndcp</u>");
            }
            if (disp_mnum == true) {
               // Display 'User' instead for Charlotte CC, Loxahatchee
               if (club.equals("charlottecc") || club.equals("loxahatchee")) {
                   out.println("&nbsp;&nbsp;<u>User</u>");
               } else {
                   out.println("&nbsp;&nbsp;<u>Mem#</u>");
               }
            }
            if (disp_bag == true) {
               out.println("&nbsp;&nbsp;<u>Bag#</u>");
            }
            out.println("&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;<u><b>Player 3</b></u>");
            out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
            if (disp_hndcp == true) {
               out.println("&nbsp;&nbsp;<u>hndcp</u>");
            }
            if (disp_mnum == true) {
               // Display 'User' instead for Charlotte CC
               if (club.equals("charlottecc") || club.equals("loxahatchee")) {
                   out.println("&nbsp;&nbsp;<u>User</u>");
               } else {
                   out.println("&nbsp;&nbsp;<u>Mem#</u>");
               }
            }
            if (disp_bag == true) {
               out.println("&nbsp;&nbsp;<u>Bag#</u>");
            }
            out.println("&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;<u><b>Player 4</b></u>");
            out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
            if (disp_hndcp == true) {
               out.println("&nbsp;&nbsp;<u>hndcp</u>");
            }
            if (disp_mnum == true) {
               // Display 'User' instead for Charlotte CC
               if (club.equals("charlottecc") || club.equals("loxahatchee")) {
                   out.println("&nbsp;&nbsp;<u>User</u>");
               } else {
                   out.println("&nbsp;&nbsp;<u>Mem#</u>");
               }
            }
            if (disp_bag == true) {
               out.println("&nbsp;&nbsp;<u>Bag#</u>");
            }
            out.println("&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         //
         //  Forest Highlands - don't allow 5-somes or show 5th player column during specified date range
         //
         if (club.equals("foresthighlands") && date > 20090424 && date < 20091001) {
             
             fives = 0;
             fivesALL = 0;
         }
            
         //
         // Columbine CC - don't allow 5-somes or show 5th player column during specified date range
         //
         if (club.equals("columbine")) {

            long month_day = (month * 100) + day;     // get adjusted date

            if (month_day > 331 && month_day < 1001) {
                fives = 0;
                fivesALL = 0;
            }
         }

         //
         //  The Lakes CC - don't allow 5-somes or show 5th player column during specifid date range
         //
         if (club.equals("lakes") && (month < 6 || month > 10)) {

             fives = 0;
             fivesALL = 0;
         }
            
         if (fivesALL != 0) {

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 5</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               if (disp_hndcp == true) {
                  out.println("&nbsp;&nbsp;<u>hndcp</u>");
               }
               if (disp_mnum == true) {
                   // Display 'User' instead for Charlotte CC
                   if (club.equals("charlottecc") || club.equals("loxahatchee")) {
                       out.println("&nbsp;&nbsp;<u>User</u>");
                   } else {
                       out.println("&nbsp;&nbsp;<u>Mem#</u>");
                   }
               }
               if (disp_bag == true) {
                   out.println("&nbsp;&nbsp;<u>Bag#</u>");
               }
               out.println("&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");
         }

            if (checkinAccess == true && index <= checkboxdays) {
                out.println("<td align=\"center\">");
                out.println("<font color=\"#FFFFFF\" size=\"1\">");
                out.println("<u><b>X</b></u>");
                out.println("</font></td>");
            }

            if (updateAccess) {
                out.println("<td align=\"center\">");
                out.println("<font color=\"#FFFFFF\" size=\"1\">");
                out.println("<u><b>N</b></u>");
                out.println("</font></td>");

                out.println("<td align=\"center\">");
                out.println("<font color=\"#FFFFFF\" size=\"1\">");
                out.println("<u><b>H</b></u>");
                out.println("</font></td>");
            }

            if (paceofplay != 0 && index == 0 && popAccess == true) {            // only show for today and if club has PoP enabled
                out.println("<td align=\"center\">");
                out.println("<font color=\"#FFFFFF\" size=\"1\">");
                out.println("<u><b>PP</b></u>");
                out.println("</font></td>");
            }

         if (parmp.posType.equals( "ClubSystems Group" ) && index == 0 && !courseName1.equals( "-ALL-" )) {

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>P</b></u>");
               out.println("</font></td>");
         }

         if (club.equals( "interlachen" ) || club.equals( "cherryhills" )) {    // if Interlachen or Cherry Hills

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>CA</b></u>");
               out.println("</font></td>");
         }
         if (club.equals( "cordillera" )) {          // if Cordillera

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>FC</b></u>");
               out.println("</font></td>");
         }
         if (useTOcol == true && index == 0) {          // if Pecan Plantation (or others) and today

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>TO</b></u>");
               out.println("</font></td>");
         }
         if (club.equals( "medinahcc" )) {          // if Medinah

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>P</b></u>");
               out.println("</font></td>");
         }

         out.println("</tr>");


      errorMsg = "Error getting or building tee times.";

      //
      //  Get the tee sheet for this date
      //
      String stringTee = "";

      if (courseName1.equals( "-ALL-" )) {

         if (club.equals( "cordillera" )) {         // do not show the Short course if Cordillera

            stringTee = "SELECT * " +
                        "FROM teecurr2 WHERE date = ? AND courseName != 'Short' ORDER BY time, courseName, fb";

         } else if (club.equals( "edisonclub" ) || club.equals( "loscoyotes" ) || club.equals( "sawgrass" )) {
           
            // sort courses by the order in which they where entered
            stringTee = "" +
                    "SELECT t.* " +
                    "FROM teecurr2 t, clubparm2 c " +
                    "WHERE t.date = ? AND t.courseName = c.courseName " +
                    "ORDER BY t.time, c.clubparm_id, t.fb;";
         
         } else {

            // select all tee times for all courses
            stringTee = "SELECT * " +
                        "FROM teecurr2 WHERE date = ? ORDER BY time, courseName, fb";
         } // end if block - Cordillera customization

      } else {

         // select all tee times for a particular course
         stringTee = "SELECT * " +
                     "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb";

      } // end if all or 1 course
      
      if (club.equals("oahucc")) {
         
         // display all the front side times within the double tee first, then the back side times for double tee
         stringTee = "" +
            "SELECT t1.*, IF(t3.teecurr_id IS NULL,'single','double') AS tee_type, " +
               "(SELECT MAX(t6.time) FROM " +
                   "(SELECT t2.time, t4.teecurr_id FROM teecurr2 t2 " +
                       "LEFT OUTER JOIN teecurr2 t4 ON t4.date = ? " +
                       "AND t4.time = t2.time AND t2.fb <> t4.fb " +
                   "WHERE t2.date = ? " +
                   ") AS t6 " +
               "WHERE t6.time < t1.time " +
               "AND IF(t6.teecurr_id IS NULL,'single','double') <> IF(t3.teecurr_id IS NULL,'single','double') " +
               ") AS prev_time " +
            "FROM teecurr2 t1 " +
               "LEFT OUTER JOIN teecurr2 t3 ON t3.date = t1.date " +
               "AND t3.time = t1.time AND t1.fb <> t3.fb " +
            "WHERE t1.date = ? " +
            "ORDER BY prev_time, t1.fb, t1.time; ";  
      }
      
      PreparedStatement pstmt = con.prepareStatement (stringTee);
      
      pstmt.clearParameters();
      pstmt.setLong(1, date);
      
      if (!courseName1.equals( "-ALL-" ) && !club.equals("oahucc")) {
         pstmt.setString(2, courseName1);
      } else if (club.equals("oahucc")) {
         pstmt.setLong(2, date);
         pstmt.setLong(3, date);
      }
      
      rs = pstmt.executeQuery();      // execute the prepared stmt
      
      while (rs.next()) {
         
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
         in_use = rs.getInt("in_use");
         type = rs.getInt("event_type");
         hndcp1 = rs.getFloat("hndcp1");
         hndcp2 = rs.getFloat("hndcp2");
         hndcp3 = rs.getFloat("hndcp3");
         hndcp4 = rs.getFloat("hndcp4");
         show1 = rs.getShort("show1");
         show2 = rs.getShort("show2");
         show3 = rs.getShort("show3");
         show4 = rs.getShort("show4");
         fb = rs.getShort("fb");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         p5cw = rs.getString("p5cw");
         hndcp5 = rs.getFloat("hndcp5");
         show5 = rs.getShort("show5");
         notes = rs.getString("notes");
         hideNotes = rs.getShort("hideNotes");
         lottery = rs.getString("lottery");
         courseNameT = rs.getString("courseName");
         blocker = rs.getString("blocker");
         rest5 = rs.getString("rest5");
         bgcolor5 = rs.getString("rest5_color");
         mnum1 = rs.getString("mNum1");
         mnum2 = rs.getString("mNum2");
         mnum3 = rs.getString("mNum3");
         mnum4 = rs.getString("mNum4");
         mnum5 = rs.getString("mNum5");
         lottery_color = rs.getString("lottery_color");
         conf = rs.getString("conf");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         if (club.equals( "interlachen" ) || club.equals( "cherryhills" )) {         // If Interlachen - get # of caddies assigned
            numCaddies = rs.getInt("hotelNew");      // use this field since not used by Interlachen
         }
         if (useTOcol == true) {                     // If Pecan Plantation (or others) - get teed off indicator
            numCaddies = rs.getInt("hotelNew");      // use this field since not used by this club
         }
         if (club.equals( "cordillera" )) {          // If Cordillera - get forecaddie indicator (saved in pos5)
            numCaddies = rs.getInt("pos5");          // use this field since not used by Cordillera
         }
         pos1 = rs.getInt("pos1");
         pos2 = rs.getInt("pos2");
         pos3 = rs.getInt("pos3");
         pos4 = rs.getInt("pos4");
         pos5 = rs.getInt("pos5");
         hole = rs.getString("hole");
         pace_status_id = rs.getInt("pace_status_id");
         teecurr_id = rs.getInt("teecurr_id");
         custom_disp1 = rs.getString("custom_disp1");
         custom_disp2 = rs.getString("custom_disp2");
         custom_disp3 = rs.getString("custom_disp3");
         custom_disp4 = rs.getString("custom_disp4");
         custom_disp5 = rs.getString("custom_disp5");
         tflag1 = rs.getString("tflag1");
         tflag2 = rs.getString("tflag2");
         tflag3 = rs.getString("tflag3");
         tflag4 = rs.getString("tflag4");
         tflag5 = rs.getString("tflag5");
         guest_id1 = rs.getInt("guest_id1");
         guest_id2 = rs.getInt("guest_id2");
         guest_id3 = rs.getInt("guest_id3");
         guest_id4 = rs.getInt("guest_id4");
         guest_id5 = rs.getInt("guest_id5");
         
         skipTime = true;         // init flag to skip this time on tee sheet display
         

         //
         //  If course=ALL requested, then set 'fives' option according to this course
         //
         if (courseName1.equals( "-ALL-" )) {

            if (club.equals("bellerive2006") || (club.equals("foresthighlands") && date > 20090424 && date < 20091001) ||
               (club.equals("columbine") && date > 20090331 && date < 20091001)) {

               fives = 0;

            } else {

               i = 0;
               loopall:
               while (i < course.size()) {
                  if (courseNameT.equals( course.get(i) )) {
                     
                     fives = fivesA.get(i);      // get the 5-some option for this course
                     break loopall;              // exit loop
                  }
                  i++;
               }
            }
         }
         
         //
         //  Custom check for Blackhawk -
         //
         //      If proshop1 or proshop5, limit to 8 days in advance for accessing tee times
         //
         noSubmit = 0;
         if ((user.equalsIgnoreCase( "proshop1" ) || user.equalsIgnoreCase( "proshop5" )) &&
             index > 8 && club.equals( "blackhawk" )) {

            noSubmit = 1;     // do not allow submit button
         }


         lskip = 0;                      // init skip switch
               
         //
         //  if not event, then check for lottery time (events override lotteries)
         //
         //  determine if we should skip this slot - display only one slot per lottery before its processed
         //
         if (blocker.equals( "" )) {     // check for lottery if tee time not blocked

            if (event.equals("") && !lottery.equals("")) {
               
               lottloop2:
               for (i=0; i<count_lott; i++) {     // check for matching lottery

                  if (lottery.equals( lottA[i] )) {    // if match found

                     if (lstateA[i] > 3 || !showlott.equalsIgnoreCase( "yes" )) {  // if lottery not taking requests OR pro not requested to show times

                        if (lstateA[i] < 5) {             // if lottery has not been processed (times allotted)

                           if (lskipA[i] != 0) {          // if we were here already

                              lskip = 1;                  // skip this slot
                           }
                           lskipA[i] = 1;                 // make sure its set now
                        } else {
                           lottery_color = "";         // already processed, do not use color
                        }
                     }
                     break lottloop2;
                  }  
               }
               i = 0;
            }             // end of IF lottery
         }                // end of IF blocker

         if (blocker.equals( "" ) && lskip == 0) {

            skipTime = false;        // DO NOT skip this time
         }
    
         
         
         //
         //  Custom for Oak Hill CC Starter (proshop4) - only proshop4 user can see and access the 5 minute intervals
         //                                              built for the day of (today).
         //
         if (club.equals("oakhillcc") && !user.equalsIgnoreCase("proshop4") && index == 0) {
         
            if (min != 0 && min != 15 && min != 30 && min != 45) {     // if not a 15 min interval
               
               skipTime = true;                                        // do NOT include this one
            }            
         }
         
         
         //
         //  Stonebridge Ranch - proshop4 & proshop5 users should not see the Dye course when viewing ALL courses
         //
         if (club.equals("stonebridgeranchcc") && courseName1.equals( "-ALL-" )) {

            if ((user.equals("proshop4") || user.equals("proshop5")) && courseNameT.equals( "Dye" )) {
            
               skipTime = true;                 // skip this one
            }
         }         

         //  Central Washington Chapter PGA Custom - populate ghin numbers
         if (club.equals("cwcpga")) {

             if (!user1.equals("")) ghin1 = Utilities.getHdcpNum(user1, con);
             if (!user2.equals("")) ghin2 = Utilities.getHdcpNum(user2, con);
             if (!user3.equals("")) ghin3 = Utilities.getHdcpNum(user3, con);
             if (!user4.equals("")) ghin4 = Utilities.getHdcpNum(user4, con);
             if (!user5.equals("")) ghin5 = Utilities.getHdcpNum(user5, con);
         }
         
         //
         //  if OK, then list the tee time
         //
         if (skipTime == false) {        // if OK to include this time on tee sheet
            
            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            bgcolor = "#F5F5DC";                // default
            wait_list_color = "";               // reset
            
            //
            // Shade the rows if time is covered by an active wait list
            //
            for (int z = 0; z < parmWaitLists.count; z++) {
                
                if ( parmWaitLists.courseName[z].equals(courseNameT) || 
                     parmWaitLists.courseName[z].equals("-ALL-") ) {

                    if (time >= parmWaitLists.stime[z] && time <= parmWaitLists.etime[z]) {
                        
                       wait_list_color = parmWaitLists.color[z];
                    }
                }
            }
            
            
            if (!event.equals("") && !ecolor.equals("")) {      // if event 
               
               bgcolor = ecolor;                                // use that color
               
            } else {

               if (!lottery.equals("") && !lottery_color.equals("")) {     // if lottery
                  
                  bgcolor = lottery_color;                                 // use that color
                  
               } else {

                  if (!rest.equals("") && !rcolor.equals("")) {    // if restriction
                     
                     bgcolor = rcolor;                             // use its color
                  }                  
               }
            }

            if (bgcolor.equals("Default")) {
               bgcolor = "#F5F5DC";              //default
            }


            timecolor = "#F5F5DC";               //default color for the cell holding the time value (or time button)

            if (!wait_list_color.equals("")) timecolor = wait_list_color;
            
            //
            //  Custom to change the background color of the time cell - if certain guest types exist
            //
            if (club.equals("sonnenalp")) {
               
               if (player1.startsWith("Property Owner") || player2.startsWith("Property Owner") || player3.startsWith("Property Owner") ||
                   player4.startsWith("Property Owner") || player5.startsWith("Property Owner") || player1.startsWith("Hotel") || 
                   player2.startsWith("Hotel") || player3.startsWith("Hotel") || player4.startsWith("Hotel") || 
                   player5.startsWith("Hotel") || player1.startsWith("Public") || player2.startsWith("Public") || 
                   player3.startsWith("Public") || player4.startsWith("Public") || player5.startsWith("Public")) {
               
                  timecolor = "yellow";          // change to yellow so it stands out
               }
            }
            
            
            //
            //  Custom check for Cordillera Canned Restrictions
            //
            if (club.equals( "cordillera" )) {

               if (dateShort > 413 && dateShort < 1101) {       // if this is within the custom date range

                  boolean corRest = cordilleraCustom.checkCordillera(date, time, courseNameT, "proshop"); // go check if member time

                  if (corRest == false) {                    // if restricted to members (hotel only time)
                     bgcolor = cordRestColor;                // set color for this slot
                  }
                  
                  if (dateShort > 531 && dateShort < 1001) {       // if this is within the custom date range for Starter Times

                     corRest = cordilleraCustom.checkStarterTime(date, time, courseNameT, "proshop"); // go check if Starter time

                     if (corRest == false) {                    // if Starter Time
                        bgcolor = cordStarterColor;             // set color for this slot
                     }
                  }
               }
            }       // end of Cordillera custom
           
            
            if (bgcolor5.equals("")) {
               bgcolor5 = bgcolor;               // player5 bgcolor = others if 5-somes not restricted
            }
            if (p91 == 1) {          // if 9 hole round
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
            gType1 = "";
            gType2 = "";
            gType3 = "";
            gType4 = "";
            gType5 = "";

            //
            //  Check if any player names are guest names
            //
            if (!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" ) && user1.equals( "" )) {

               i = 0;
               ploop1:
               while (i < parm.MAX_Guests) {
                  if (player1.startsWith( parm.guest[i] )) {

                     g1 = 1;                       // indicate player1 is a guest name
                     gType1 = parm.guest[i];       // save guest type

                     // Check if guest is linked to the guest tracking system
                     if (use_guestdb && guest_id1 > 0) {

                         // Check if guest database is to be used with this guest type.  If not, set the guest_id to 0
                         if (parm.gDb[i] == 0) {
                             guest_id1 = 0;
                         } else {

                             // Get the unique_id string for this guest for display later
                             guest_uid1 = Common_guestdb.buildUIDString(guest_id1, con);

                             // See if the guest database record for this guest is missing required information, if so, flag the id by making it negative.
                             if (Common_guestdb.checkGuestReqInfo(guest_id1, 0, true, con, out)) {
                                 guest_id1 = guest_id1 * -1;
                             }
                         }
                     } else {
                         guest_id1 = 0;
                     }
                     
                     break ploop1;
                  }
                  i++;
               }
            }
            if (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" ) && user2.equals( "" )) {

               i = 0;
               ploop2:
               while (i < parm.MAX_Guests) {
                  if (player2.startsWith( parm.guest[i] )) {

                     g2 = 1;                       // indicate player2 is a guest name
                     gType2 = parm.guest[i];       // save guest type

                     // Check if guest is linked to the guest tracking system
                     if (use_guestdb && guest_id2 > 0) {

                         // Check if guest database is to be used with this guest type.  If not, set the guest_id to 0
                         if (parm.gDb[i] == 0) {
                             guest_id2 = 0;
                         } else {

                             // Get the unique_id string for this guest for display later
                             guest_uid2 = Common_guestdb.buildUIDString(guest_id2, con);

                             // See if the guest database record for this guest is missing required information, if so, flag the id by making it negative.
                             if (Common_guestdb.checkGuestReqInfo(guest_id2, 0, true, con, out)) {
                                 guest_id2 = guest_id2 * -1;
                             }
                         }
                     } else {
                         guest_id2 = 0;
                     }

                     break ploop2;
                  }
                  i++;
               }
            }
            if (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" ) && user3.equals( "" )) {

               i = 0;
               ploop3:
               while (i < parm.MAX_Guests) {
                  if (player3.startsWith( parm.guest[i] )) {

                     g3 = 1;                       // indicate player3 is a guest name
                     gType3 = parm.guest[i];       // save guest type

                     // Check if guest is linked to the guest tracking system
                     if (use_guestdb && guest_id3 > 0) {

                         // Check if guest database is to be used with this guest type.  If not, set the guest_id to 0
                         if (parm.gDb[i] == 0) {
                             guest_id3 = 0;
                         } else {

                             // Get the unique_id string for this guest for display later
                             guest_uid3 = Common_guestdb.buildUIDString(guest_id3, con);

                             // See if the guest database record for this guest is missing required information, if so, flag the id by making it negative.
                             if (Common_guestdb.checkGuestReqInfo(guest_id3, 0, true, con, out)) {
                                 guest_id3 = guest_id3 * -1;
                             }
                         }
                     } else {
                         guest_id3 = 0;
                     }

                     break ploop3;
                  }
                  i++;
               }
            }
            if (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" ) && user4.equals( "" )) {

               i = 0;
               ploop4:
               while (i < parm.MAX_Guests) {
                  if (player4.startsWith( parm.guest[i] )) {

                     g4 = 1;                       // indicate player4 is a guest name
                     gType4 = parm.guest[i];       // save guest type

                     // Check if guest is linked to the guest tracking system
                     if (use_guestdb && guest_id4 > 0) {

                         // Check if guest database is to be used with this guest type.  If not, set the guest_id to 0
                         if (parm.gDb[i] == 0) {
                             guest_id4 = 0;
                         } else {

                             // Get the unique_id string for this guest for display later
                             guest_uid4 = Common_guestdb.buildUIDString(guest_id4, con);

                             // See if the guest database record for this guest is missing required information, if so, flag the id by making it negative.
                             if (Common_guestdb.checkGuestReqInfo(guest_id4, 0, true, con, out)) {
                                 guest_id4 = guest_id4 * -1;
                             }
                         }
                     } else {
                         guest_id4 = 0;
                     }

                     break ploop4;
                  }
                  i++;
               }
            }
            if (!player5.equals( "" ) && !player5.equalsIgnoreCase( "x" ) && user5.equals( "" )) {

               i = 0;
               ploop5:
               while (i < parm.MAX_Guests) {
                  if (player5.startsWith( parm.guest[i] )) {

                     g5 = 1;                      // indicate player5 is a guest name
                     gType5 = parm.guest[i];       // save guest type

                     // Check if guest is linked to the guest tracking system
                     if (use_guestdb && guest_id5 > 0) {

                         // Check if guest database is to be used with this guest type.  If not, set the guest_id to 0
                         if (parm.gDb[i] == 0) {
                             guest_id5 = 0;
                         } else {

                             // Get the unique_id string for this guest for display later
                             guest_uid5 = Common_guestdb.buildUIDString(guest_id5, con);

                             // See if the guest database record for this guest is missing required information, if so, flag the id by making it negative.
                             if (Common_guestdb.checkGuestReqInfo(guest_id5, 0, true, con, out)) {
                                 guest_id5 = guest_id5 * -1;
                             }
                         }
                     } else {
                         guest_id5 = 0;
                     }

                     break ploop5;
                  }
                  i++;
               }
            }

            //
            //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
            //
            sfb = "F";       // default Front 9
            sfb2 = "Front";

            if (fb == 1) {

               sfb = "B";
               sfb2 = "Back";
            }

            if (fb == 9) {

               sfb = "O";
               sfb2 = "O";
            }

            if (type == shotgun) {

               sfb = "S";            // there's an event and its type is 'shotgun'
            }

            //
            //  TEMP Custom for Forest Highlands (only for 2006!!!)
            //
            if (club.equals( "foresthighlands" ) && (date == 20060907 || date == 20060908) && courseNameT.equals( "Canyon" )) {

               if (sfb.equals( "F" )) {     // if front

                  sfb = "#1";               // change to #1

               } else {

                  if (sfb.equals( "B" )) {     // if back

                     sfb = "#8";               // change to #8
                  }
               }
            }


            //
            // if restriction for this slot and its not the first time for a lottery, check restriction for this member
            //
            if (!rest.equals("") && !rcolor.equals("")) {
                
                int ind = 0;
                while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {

                    if (parmr.restName[ind].equals(rest)) {

                        // Check to make sure no suspensions apply
                        suspend = false;                        
                        for (int m=0; m<parmr.MAX; m++) {

                            if (parmr.susp[ind][m][0] == 0 && parmr.susp[ind][m][1] == 0) {
                                m = parmr.MAX;   // don't bother checking any more
                            } else if (parmr.susp[ind][m][0] <= time && parmr.susp[ind][m][1] >= time) {    //time falls within a suspension
                                suspend = true;                       
                                m = parmr.MAX;     // don't bother checking any more
                            }
                        }      // end of for loop

                        if (suspend) {
                            
                            if (bgcolor5.equals(bgcolor)) { // Reset bgcolor5 if color was that of the suspended restriction
                                bgcolor5 = "";
                            }
                            
                            if ((parmr.courseName[ind].equals( "-ALL-" )) || (parmr.courseName[ind].equals( courseNameT ))) {  // course ?

                                if ((parmr.fb[ind].equals( "Both" )) || (parmr.fb[ind].equals( sfb2 ))) {    // matching f/b ?

                                    //
                                    //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                    //
                                    if (event.equals("") && lottery.equals("")) {           // change color back to default if no event

                                        // Search for the first non-suspended color to apply, or default if non found
                                        bgcolor = "#F5F5DC";   // default color

                                        int ind2 = 0;
                                        while (ind2 < parmr.MAX && !parmr.restName[ind2].equals("")) {

                                            // make sure it's not the default restriction/color, and has a non-blank, non-default color
                                            // and applies to this time
                                            if (!parmr.restName[ind2].equals(rest) && !parmr.color[ind2].equals("") && !parmr.color[ind2].equalsIgnoreCase("Default") && 
                                                    parmr.stime[ind2] <= time && parmr.etime[ind2] >= time) {      

                                                // Check to make sure no suspensions apply
                                                suspend = false;                        
                                                for (int m=0; m<parmr.MAX; m++) {

                                                    if (parmr.susp[ind2][m][0] == 0 && parmr.susp[ind2][m][1] == 0) {
                                                        m = parmr.MAX;   // don't bother checking any more
                                                    } else if (parmr.susp[ind2][m][0] <= time && parmr.susp[ind2][m][1] >= time) {    //time falls within a suspension
                                                        suspend = true;                       
                                                        m = parmr.MAX;     // don't bother checking any more
                                                    }
                                                }

                                                if (!suspend) {

                                                    if ((parmr.courseName[ind2].equals( "-ALL-" )) || (parmr.courseName[ind2].equals( courseNameT ))) {  // course ?

                                                        if ((parmr.fb[ind2].equals( "Both" )) || (parmr.fb[ind2].equals( sfb2 ))) {    // matching f/b ?

                                                            //
                                                            //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                            //
                                                            if (event.equals("") && lottery.equals("")) {           // change color if no event

                                                                if (!parmr.color[ind2].equals("Default")) {     // if not default

                                                                    bgcolor = parmr.color[ind2];
                                                                    ind2 = parmr.MAX;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            ind2++;
                                        }

                                        if (bgcolor5.equals( "" )) {
                                            bgcolor5 = bgcolor;         // same as others if not specified
                                        }
                                    }
                                }
                             }
                         }
                     }
                     ind++;
                 }      // end of while loop
            }     // end of if rest exists in teecurr


            submit = "time:" + fb;       // create a name for the submit button to include 'time:' & fb

            j++;                                              // increment the jump label index (where to jump on page)
            out.println("<tr id=\"jump" + j + "\">");         // new table row with jump id (put here so Firefox & Safari will work)

            // out.println("<a name=\"jump" + j + "\"></a>"); // create a jump label for 'noshow' returns

            
            if (in_use == 0 && noSubmit == 0 && restrictAll == false) {     // if not in use, then create submit button

               //
               //  if not event, then check for lottery time (events override lotteries)
               //
               if (event.equals("") && !lottery.equals("")) {  // lotteries are not in use before processing date/time

                  boolean foundLott = false;
                  
                  lottloop3:
                  for (i=0; i<count_lott; i++) {     // find matching lottery
                     
                     if (lottery.equals( lottA[i] )) {

                        if (lstateA[i] < 4 && !showlott.equalsIgnoreCase( "yes" )) {

                           out.println("<form action=\"/" +rev+ "/servlet/Proshop_lott\" method=\"post\" target=\"_top\">");
                           out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstateA[i] + "\">");
                           out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottA[i] + "\">");
                           out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slotsA[i] + "\">");

                        } else {

                           if (lstateA[i] == 4) {         // waiting for approval - force to Proshop_mlottery

                              if ( use_dlott ) {

                                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
                                  out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
                                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                  out.println("<input type=\"hidden\" name=\"name\" value=\"" + lottA[i] + "\">");
                                  out.println("<input type=\"hidden\" name=\"hide\" value=\"1\">");

                              } else {

                                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_mlottery\" method=\"post\">");
                                  out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
                                  out.println("<input type=\"hidden\" name=\"approve\" value=\"yes\">");
                                  out.println("<input type=\"hidden\" name=\"dd\" value=\"" + day + "\">");
                                  out.println("<input type=\"hidden\" name=\"mm\" value=\"" + month + "\">");
                                  out.println("<input type=\"hidden\" name=\"yy\" value=\"" + year + "\">");

                              }

                           } else {      // state 5 or pro wants access to times so he can pre-book

                              out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" target=\"_top\">");
                              out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
                           }
                        }
                        lstate = lstateA[i];
                        foundLott = true;
                        break lottloop3;
                     }
                  }
                  
                  i = 0;          // reset index

                  if (foundLott == false) {      // if match not found

                     errorMsg = "Proshop_sheet: More than " +count_lott+ " lotteries defined for one day at " + club;    // build error msg
                     SystemUtils.logError(errorMsg);                           // log it

                     out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" target=\"_top\">");
                  }
                  
               } else {   // no lottery for this tee time

                  lstate = 0;
                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" name=\"mform" +j+ "\" target=\"_top\">");

                  if (type == shotgun) {    // if shotgun event - inform _slot

                     out.println("<input type=\"hidden\" name=\"shotgunevent\" value=\"yes\">");
                  }
               }
               
               out.println("<td align=\"center\" bgcolor=\"" +timecolor+ "\">");
               out.println("<font size=\"2\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
               if (lstate == 4) {      // if lottery ready to approve - pass the course that we are currently displaying (not the tee time course)
                   out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
               } else {
                   out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
               }
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + courseName1 + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
               //
               //  **** Check for 5-some restriction ********* Always allow for proshop.
               //             But inform Proshop_slot so we can warn proshop.
               //
               if (fives != 0) {

                  out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");

                  if (!rest5.equals( "" )) {          // if 5-somes are restricted

                     out.println("<input type=\"hidden\" name=\"p5rest\" value=\"Yes\">");  // tell _slot
                  } else {
                     out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                  }
               } else {
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                  out.println("<input type=\"hidden\" name=\"p5rest\" value=\"Yes\">");  // 5-somes restricted
               }

               if (lstate > 0 && lstate < 5) {

                  if (lstate == 4) {
                      if (lottApproveAccess) {
                          out.println("<input type=\"submit\" name=\"submit\" value=\"Approve\">");
                      }

                  } else {

                      if (SystemUtils.verifyProAccess(req, "LOTT_UPDATE", con, out)) {
                         if (!showlott.equalsIgnoreCase("yes")) {
                             out.println("<input type=\"submit\" value=\"Lottery\">");
                             out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\" alt=\"submit\">");
                         } else {
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");
                         }
                      } else {
                          out.println("Lottery");
                      }
                  }
                  
               } else {

                  //
                  //  If Milwaukee CC, check for Special Guest Time
                  //
                  boolean mcctime = false;

                  if (club.equals("milwaukee") && mccGuestDay == true) {

                     if (fb == 1 && time > 1200 && time < 1431) {

                        mcctime = true;      // special guest time - make it a green button
                     }
                  }

                  //
                  //  If Bearpath CC check for member-only times
                  //
                  boolean beartime = false;

                  if (club.equals("bearpath")) {

                     beartime = verifySlot.checkBearpathGuests(day_name, date, time, index);
                  }

                  //
                  //  If Oakmont CC and Wed or Fir, and not a shotgun today, check if this time is one when multiple guests are allowed.
                  //  If so, make the submit button green.
                  //
                  boolean oaktime = false;

                  if (event.equals("") && club.equals("oakmont") && oakshotgun == false && (day_name.equals("Wednesday") || day_name.equals("Friday"))) {

                     //
                     //  Check the time of this tee time against those allowed for this day
                     //
                     if (day_name.equals( "Friday" )) {       // if Friday

                        oakloop1:
                        for (int oak = 0; oak < fricount; oak++) {

                           if (time == fritimes[oak]) {

                              oaktime = true;       // tee time is ok
                              break oakloop1;
                           }
                        }

                     } else {    // Wednesday

                        oakloop3:
                        for (int oak = 0; oak < wedcount; oak++) {      // check normal Wed times

                           if (time == wedtimes[oak]) {

                              oaktime = true;       // tee time is ok
                              break oakloop3;
                           }
                        }
                     }
                  }

                  //
                  //  If Medinah CC check for Member Time (walk-up time)
                  //
                  if (club.equals( "medinahcc" )) {

                     medinahMemTime = medinahCustom.checkMemTime(courseNameT, day_name, time, date);
                  }

                  String tmp_btnColor = "";

                  //
                  //  Create the Submit Button (time)
                  //
                  if (medinahMemTime == true) {    // if Medinah special time

                     if ((time == 736 || time == 1400) && courseNameT.equals( "No 3" ) && (day_name.equals("Saturday") || day_name.equals("Sunday"))) {

                        if (time == 736) {   // 7:36 is green on w/e's (Member Times)

                            tmp_btnColor = "lightgreen";

                        } else {             // 2:00 is blue on w/e's (Outside Time)

                            tmp_btnColor = "lightblue";
                        }

                     } else {

                     //  if (time == 736 && courseNameT.equals( "No 3" )) {   // if Outside Play time on weekday
                          
                     //      tmp_btnColor = "lightblue";

                     //  } else {
                          
                       if (time == 1400 && courseNameT.equals( "No 3" )) {   // if Outside Play time on weekday
                          
                           tmp_btnColor = "lightblue";   // Outside Time (Unaccompanied)

                       } else {
                          
                     //     if ((time == 900 || time == 1100) && courseNameT.equals( "No 3" )) {   // if Starter time (Red)
                          if (time == 1100 && courseNameT.equals( "No 3" )) {   // if Starter time (Red)

                             tmp_btnColor = "#A0522D";       // Red - Starter Time

                          } else {                           // Member time (Walk-up Time) - light green

                             tmp_btnColor = "lightgreen";

                          }
                       }
                     }

                     out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:" + tmp_btnColor + "\">");

                  } else {

                     if (oaktime == true || mcctime == true || beartime == true) { // if Bearpath, Oakmont or Milwaukee special time

                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:lightgreen\">");

                     } else {

                        if (type == shotgun) {                             // if Shotgun Event
                           
                           String shotgunLabel = "";
                           
                           eventloop1:
                           for (i=0; i<count_event; i++) {
                           
                              if (event.equals(eventA[i])) {
                                 
                                  shotgunLabel = SystemUtils.getSimpleTime(event_start_hrA[i], event_start_minA[i]);
                                  break eventloop1;
                              }
                           }
                           
                           i = 0;       // reset index

                           if (!shotgunLabel.equals( "" )) {       // if time gathered above
                              
                              StringTokenizer toks = new StringTokenizer(shotgunLabel, " " );     // delimiters are space (8:30 AM)

                              shotgunLabel = toks.nextToken();                             // get the time only (8:30)
                           }
                           
                           shotgunLabel += " Shotgun";            // i.e.  9:30 Shotgun

                           out.println("<input type=\"submit\" name=\"shotgun\" value=\"" +shotgunLabel+ "\" alt=\"submit\">");
                           out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">");

                        } else {

                           //
                           // if Oswego Lake or Desert Highlands or Sonnenalp and Notes included - orange button
                           //
                           if ((club.equals( "oswegolake" ) || club.equals( "deserthighlands" ) || club.equals( "sonnenalp" )) && !notes.equals( "" )) {

                              tmp_btnColor = "style=\"text-decoration:none; background:orange\"";
                              //out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:orange\">");

                           } else {
                               
                               // check to see if there is an active wait list running and if so color the button
                               //tmp_btnColor = "style=\"text-decoration:none; background:" + wait_list_color + "\""; 
                               
                           }
                           
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" " + tmp_btnColor + ">");

                        }
                     }
                  }
               }
               out.println("</font></td>");

               //
               //  if Consecutive Tee Times allowed, no lottery or event, and tee time is empty,
               //  and tee is not a cros-over, allow member to select more than one tee time
               //
               if (parm.constimesp > 1) {

                  out.println("<td align=\"center\">");

                  if (player1.equals( "" ) && player2.equals( "" ) && player3.equals( "" ) &&
                      player4.equals( "" ) && player5.equals( "" ) &&
                      (event.equals("") || (club.equals("chartwellgcc") && type == shotgun)) && 
                      fb != 9 && (lottery.equals("") || lstate > 4)) {

                     out.println("<select size=\"1\" name=\"contimes\" onChange=\"document.mform" +j+ ".submit()\">");
                     out.println("<option value=\"1\">1</option>");
                     out.println("<option value=\"2\">2</option>");
                     if (parm.constimesp > 2) {
                        out.println("<option value=\"3\">3</option>");
                     }
                     if (parm.constimesp > 3) {
                        out.println("<option value=\"4\">4</option>");
                     }
                     if (parm.constimesp > 4) {
                        out.println("<option value=\"5\">5</option>");
                     }
                     out.println("</select>");

                        
                     //  add hidden time parm so value passed when this item is selected
                     //
                     out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">");

                  } else {

                     out.println("&nbsp;");
                  }
                  out.println("</td>");
               }
                 
               //
               //  Check if this is an empty tee time.  If so, indicate such for Proshop_slot so we know that the user is trying to select
               //  a new tee time.
               //
               if (player1.equals( "" ) && player2.equals( "" ) && player3.equals( "" ) && player4.equals( "" ) && player5.equals( "" )) {

                  out.println("<input type=\"hidden\" name=\"newreq\" value=\"yes\">");
               }
  
               out.println("</form>");

            } else {                          // slot is currently in use - no submit button

               out.println("<td align=\"center\" bgcolor=\"" +timecolor+ "\">");
               out.println("<font size=\"2\">");
               out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
               out.println("</font></td>");

               if (parm.constimesp > 1) {
                  out.println("<td align=\"center\">");
                  out.println("&nbsp;");
                  out.println("</td>");
               }
            }

            if (multi != 0) {                          // case 1509
               
               if (courseName1.equals( "-ALL-" )) {

                  //
                  //  Course Name - set tmp_i equal to course index # (alternate the bg colors)
                  //
                  for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                      if (courseNameT.equals(course.get(tmp_i))) break;      // find index value for this course
                  }

                  out.println("<td bgcolor=\"" + colors.course_color[tmp_i] + "\" align=\"center\">");  // assign a bg color

               } else {
                  
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               }

               out.println("<font size=\"2\">");
               out.println(courseNameT);
               out.println("</font></td>");
            }

            //
            //  Front/Back Indicator
            //
            // if (club.equals("oakleycountryclub") && sfb.equals("B")) {          // if Oakley CC and a Back Tee
            if (sfb.equals("B")) {                                               // if a Back Tee (all clubs)
                out.println("<td bgcolor=\"yellow\" align=\"center\">");         // highlight it
            } else {
                out.println("<td bgcolor=\"white\" align=\"center\">");
            }
            out.print("<font size=\"2\">");
            // display if there is something in hole, then display that instead of the fb
            out.print((!hole.equals("")) ? "  " + hole + "  " : sfb);
            out.println("</font></td>");

            //
            //  Sonnenalp - display the guest fees with any guests (saved when tee time created/changed).
            //
            if (club.equals( "sonnenalp" ) && !user.equalsIgnoreCase( "proshop5" )) {      // do NOT do for proshop5 (see below)

                if (g1 != 0 && !custom_disp1.equals( "" )) {    // if guest and rate was found

                   player1 = player1 + " " + custom_disp1;
                }
                if (g2 != 0 && !custom_disp2.equals( "" )) {    // if guest and rate was found

                   player2 = player2 + " " + custom_disp2;
                }
                if (g3 != 0 && !custom_disp3.equals( "" )) {    // if guest and rate was found

                   player3 = player3 + " " + custom_disp3;
                }
                if (g4 != 0 && !custom_disp4.equals( "" )) {    // if guest and rate was found

                   player4 = player4 + " " + custom_disp4;
                }
            }

                              
            //
            //  Wilmington - display an *R next to players with Range Privileges..
            //
            if (club.equals( "wilmington" )) {    // Cannot replace this custom yet with mship flags - need member2b flag!!!!!

                if (custom_disp1.equals( "R" ) && tflag1.equals("")) {

                   player1 = player1 + " *R";
                }
                if (custom_disp2.equals( "R" ) && tflag2.equals("")) {

                   player2 = player2 + " *R";
                }
                if (custom_disp3.equals( "R" ) && tflag3.equals("")) {

                   player3 = player3 + " *R";
                }
                if (custom_disp4.equals( "R" ) && tflag4.equals("")) {

                   player4 = player4 + " *R";
                }
                if (custom_disp5.equals( "R" ) && tflag5.equals("")) {

                   player5 = player5 + " *R";
                }
            }

               
            //
            //  Custom - display view-only tee sheet with bag room #s
            //
            if (((club.equals( "cherryhills" ) && user.equalsIgnoreCase( "proshop4" )) ||
                (club.equals( "hallbrookcc" ) && user.equalsIgnoreCase( "proshop4" )) ||
                (club.equals( "deserthighlands" ) && user.equalsIgnoreCase( "proshop5" )) ||
                (club.equals( "columbine" ) && user.equalsIgnoreCase( "proshop5" )) ||
                (club.equals( "valleyclub" ) && user.equalsIgnoreCase( "proshop5" )) ||
                (club.equals( "sonnenalp" ) && user.equalsIgnoreCase( "proshop5" )) ||
                (club.equals( "rollinghillscc" ) && user.equalsIgnoreCase( "proshop1" )) ||
                (club.equals( "brookingscc" )) ||
                (club.equals( "sunnehanna" ) && user.equalsIgnoreCase( "proshop2" )) ||
                (club.equals( "snoqualmieridge" ) && user.equalsIgnoreCase( "proshop5" )) ||
                 club.equals( "bracketts" )) || dispBagOption) {

               disp_bag = true;                 // use this flag to display the bag numbers
               
               ///////// TEMP - REMOVE LATER!  ?????????????????
               disp_mnum = false;
               ///////// TEMP - REMOVE LATER!

               bag1 = "";                       // init
               bag2 = "";
               bag3 = "";
               bag4 = "";
               bag5 = "";
               
               if (!user1.equals( "" )) {

                  bag1 = getBag(user1, con);    // get bag# and save in mnum for display
               }
               if (!user2.equals( "" )) {

                  bag2 = getBag(user2, con);
               }
               if (!user3.equals( "" )) {

                  bag3 = getBag(user3, con);
               }
               if (!user4.equals( "" )) {

                  bag4 = getBag(user4, con);
               }
               if (!user5.equals( "" )) {

                  bag5 = getBag(user5, con);
               }
            }

            //
            //  Custom for Los Coyotes - display the Gender with the Member Number
            //
            if (club.equals( "loscoyotes" ) && disp_mnum == true) {

               if (!user1.equals( "" )) {

                  mnum1 = verifyCustom.getLCGender(user1, mnum1, con);    // get Gender and save in mnum for display
               }
               if (!user2.equals( "" )) {

                  mnum2 = verifyCustom.getLCGender(user2, mnum2, con);
               }
               if (!user3.equals( "" )) {

                  mnum3 = verifyCustom.getLCGender(user3, mnum3, con);
               }
               if (!user4.equals( "" )) {

                  mnum4 = verifyCustom.getLCGender(user4, mnum4, con);
               }
               if (!user5.equals( "" )) {

                  mnum5 = verifyCustom.getLCGender(user5, mnum5, con);
               }
            }

            //
            //  Custom for Greeley CC - display additional information with mNum
            //
            //  If Fort Collins or Greeley CC proshop user (they share)
            //
            if (club.equals( "fortcollins" )) {

               if (!user1.equals( "" )) {

                  mnum1 = getCart(user, user1, con);    // get Cart Pass and Range Pass data and save in mnum for display
               }
               if (!user2.equals( "" )) {

                  mnum2 = getCart(user, user2, con);
               }
               if (!user3.equals( "" )) {

                  mnum3 = getCart(user, user3, con);
               }
               if (!user4.equals( "" )) {

                  mnum4 = getCart(user, user4, con);
               }
               if (!user5.equals( "" )) {

                  mnum5 = getCart(user, user5, con);
               }
            }

            //
            //  Custom for Merion/Peninsula - display mship information in place of mnum 
            //
            if ( club.equals( "merion" ) || club.equals( "peninsula" ) ) {

               if (!user1.equals( "" )) {

                  mnum1 = getMship(user1, club, con);    // get Cart Pass and Range Pass data and save in mnum for display
               }
               if (!user2.equals( "" )) {

                  mnum2 = getMship(user2, club, con);
               }
               if (!user3.equals( "" )) {

                  mnum3 = getMship(user3, club, con);
               }
               if (!user4.equals( "" )) {

                  mnum4 = getMship(user4, club, con);
               }
               if (!user5.equals( "" )) {

                  mnum5 = getMship(user5, club, con);
               }
            }

               
            //
            //  If Portland GC check for Member Time (walk-up time)
            //
            if (club.equals( "portlandgc" ) && ecolor.equals("")) {   // if Portland GC & Not an Event time

               walkup = verifyCustom.checkPGCwalkup(date, time, day_name);   // Walk-Up time?

               if (walkup == true) {

                  bgcolor = "burlywood";       // custom color for Walk-Up times
               }
            }        
               
               
            //
            //  Set the background color for each player slot (do not change 5th player - already set by 5-some restriction)
            //
            bgcolor1 = bgcolor;       // use bgcolor already set for tee time 
            bgcolor2 = bgcolor;
            bgcolor3 = bgcolor;
            bgcolor4 = bgcolor;
               
            
            //
            //  Interlachen - change bgcolor of any player/guest that requests a Gift Pack
            //
            /*
            if (club.equals("interlachen")) {
               
               if (custom_disp1.equals("1")) {
                  
                  bgcolor1 = interlGPcolor;     // change the bgcolor for this player
               }
               if (custom_disp2.equals("1")) {
                  
                  bgcolor2 = interlGPcolor;     // change the bgcolor for this player
               }
               if (custom_disp3.equals("1")) {
                  
                  bgcolor3 = interlGPcolor;     // change the bgcolor for this player
               }
               if (custom_disp4.equals("1")) {
                  
                  bgcolor4 = interlGPcolor;     // change the bgcolor for this player
               }
               if (custom_disp5.equals("1")) {
                  
                  bgcolor5 = interlGPcolor;     // change the bgcolor for this player
               }
            }
             */
               
            
            //
            //  Oakland Hills - change bgcolor of any player/guest that requests a Gift Pack
            //
            if (club.equals("oaklandhills")) {
               
               if (custom_disp1.equals("1")) {
                  
                  bgcolor1 = oaklandGBTcolor;     // change the bgcolor for this player
               }
               if (custom_disp2.equals("1")) {
                  
                  bgcolor2 = oaklandGBTcolor;     // change the bgcolor for this player
               }
               if (custom_disp3.equals("1")) {
                  
                  bgcolor3 = oaklandGBTcolor;     // change the bgcolor for this player
               }
               if (custom_disp4.equals("1")) {
                  
                  bgcolor4 = oaklandGBTcolor;     // change the bgcolor for this player
               }
               if (custom_disp5.equals("1")) {
                  
                  bgcolor5 = oaklandGBTcolor;     // change the bgcolor for this player
               }
            }
               
            //
            //  If present add the contents of tflag to the player's name.  The tflag values come from the proshop config 
            //  for mship types and from the admin member record.  These 2 values are combined into one when the tee time
            //  is made or changed.  This is used to mark certain members so the proshop can easily identify them.
            //
            if (!tflag1.equals("") && !user1.equals("")) {              // if flag present
               
               player1 = player1 + " " + tflag1;     // add it to player name
            }
            if (!tflag2.equals("") && !user2.equals("")) {              // if flag present
               
               player2 = player2 + " " + tflag2;     // add it to player name
            }
            if (!tflag3.equals("") && !user3.equals("")) {              // if flag present
               
               player3 = player3 + " " + tflag3;     // add it to player name
            }
            if (!tflag4.equals("") && !user4.equals("")) {              // if flag present
               
               player4 = player4 + " " + tflag4;     // add it to player name
            }
            if (!tflag5.equals("") && !user5.equals("")) {              // if flag present
               
               player5 = player5 + " " + tflag5;     // add it to player name
            }
            
            
            //
            //  If IBS POS - add the guest fee to any player that is a guest
            //
            if (!club.equals( "sonnenalp" ) && parmp.posType.equals( "IBS" ) && index == 0) {    // if IBS and Today
               
               if (g1 == 1) {      // if player is a guest
                  
                  player1 = checkIBSguest(player1, gType1, p91, con);           // add guest fee to player name if there is a fee
               }
               if (g2 == 1) {      // if player is a guest
                  
                  player2 = checkIBSguest(player2, gType2, p92, con);           // add guest fee to player name if there is a fee
               }
               if (g3 == 1) {      // if player is a guest
                  
                  player3 = checkIBSguest(player3, gType3, p93, con);           // add guest fee to player name if there is a fee
               }
               if (g4 == 1) {      // if player is a guest
                  
                  player4 = checkIBSguest(player4, gType4, p94, con);           // add guest fee to player name if there is a fee
               }
               if (g5 == 1) {      // if player is a guest
                  
                  player5 = checkIBSguest(player5, gType5, p95, con);           // add guest fee to player name if there is a fee
               }
               
            }
            
            // Check player birthdates for Castle Pines CC and highlight if it's today.
            if (club.equals("castlepines")) {
                if (custom_disp1.equals("bday")) bgcolor1 = "Lime";
                if (custom_disp2.equals("bday")) bgcolor2 = "Lime";
                if (custom_disp3.equals("bday")) bgcolor3 = "Lime";
                if (custom_disp4.equals("bday")) bgcolor4 = "Lime";
                if (custom_disp5.equals("bday")) bgcolor5 = "Lime";
            }

            //  Custom to change the bgcolor of player cell to Crimson if player's mship = 'Annual Pass Member
            if (club.equals("perryparkcc")) {
                if (verifyCustom.checkPerryParkMship(user1, con)) bgcolor1 = "Crimson";
                if (verifyCustom.checkPerryParkMship(user2, con)) bgcolor2 = "Crimson";
                if (verifyCustom.checkPerryParkMship(user3, con)) bgcolor3 = "Crimson";
                if (verifyCustom.checkPerryParkMship(user4, con)) bgcolor4 = "Crimson";
                if (verifyCustom.checkPerryParkMship(user5, con)) bgcolor5 = "Crimson";
            }
            
            //
            //   Check if we should show the checkin boxes for the players
            //
            boolean showcheckbox = true;          // default to yes
            
            if (index > checkboxdays) {   // if user cannot check-in players OR beyond days to show check boxes
               
               showcheckbox = false;          // do not show the check boxes
            }

            //
            //  Build each of the player TD's for this row
            //
            if (club.equals("mayfieldsr") && verifyCustom.checkMayfieldSR(date, time, day_name)) {
                
                buildPlayerTD(out, 1, show1, player1, bgcolor1, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp1, ghin1, g1, guest_id1, guest_uid1, p1cw, disp_mnum, mnum1, disp_bag, bag1, courseName1, club, parmp, pos1, showcheckbox, checkinAccess, user1, teecurr_id);
                buildPlayerTD(out, 2, show2, player2, bgcolor2, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp2, ghin2, g2, guest_id2, guest_uid2, p2cw, disp_mnum, mnum2, disp_bag, bag2, courseName1, club, parmp, pos2, showcheckbox, checkinAccess, user2, teecurr_id);
                out.println("<td></td><td></td>");
                out.println("<td></td><td></td>");
                out.println("<td></td><td></td>");
                
            } else {
               
                buildPlayerTD(out, 1, show1, player1, bgcolor1, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp1, ghin1, g1, guest_id1, guest_uid1, p1cw, disp_mnum, mnum1, disp_bag, bag1, courseName1, club, parmp, pos1, showcheckbox, checkinAccess, user1, teecurr_id);
                buildPlayerTD(out, 2, show2, player2, bgcolor2, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp2, ghin2, g2, guest_id2, guest_uid2, p2cw, disp_mnum, mnum2, disp_bag, bag2, courseName1, club, parmp, pos2, showcheckbox, checkinAccess, user2, teecurr_id);
                buildPlayerTD(out, 3, show3, player3, bgcolor3, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp3, ghin3, g3, guest_id3, guest_uid3, p3cw, disp_mnum, mnum3, disp_bag, bag3, courseName1, club, parmp, pos3, showcheckbox, checkinAccess, user3, teecurr_id);
                buildPlayerTD(out, 4, show4, player4, bgcolor4, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp4, ghin4, g4, guest_id4, guest_uid4, p4cw, disp_mnum, mnum4, disp_bag, bag4, courseName1, club, parmp, pos4, showcheckbox, checkinAccess, user4, teecurr_id);

                //
                //  Add Player 5 if supported
                //
                if (fivesALL != 0) {        // if 5-somes supported on any course

                   if (fives != 0) {        // if 5-somes on this course

                     buildPlayerTD(out, 5, show5, player5, bgcolor5, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp5, ghin5, g5, guest_id5, guest_uid5, p5cw, disp_mnum, mnum5, disp_bag, bag5, courseName1, club, parmp, pos5, showcheckbox, checkinAccess, user5, teecurr_id);

                   } else {         // 5-somes supported on at least 1 course, but not this one (if course=ALL)

                      out.print("<td bgcolor=\"black\" align=\"center\">");   // no 5-somes
                      out.print("<font size=\"2\">");
                      out.print("&nbsp;");
                      out.print("</font></td>");
                      out.print("<td bgcolor=\"black\" align=\"center\">");
                      out.print("<font size=\"2\">");
                      out.print("&nbsp;");
                      out.println("</font></td>");
                   }
                }
            }

            //
            //  Next column for 'check-in all' box (add box if any players in slot)
            //  only allow if user has CheckIn access
            if (checkinAccess == true && index <= checkboxdays) {
               
                if (((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) ||
                    ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) ||
                    ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) ||
                    ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) ||
                    ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" )))) {

                   //
                   //   Use new check-in processing for all clubs now
                   //
                   out.print("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.print("&nbsp;<img onclick=\"doCheckIn("+teecurr_id+",0,'"+courseNameT+"',this.id);\" id=\"chkbox_"+teecurr_id+"_0\" src=\"/" +rev+ "/images/checkall.gif\" border=\"1\" title=\"Click here to check all players in.\">");    
                   out.println("</td>");
                   

                } else {

                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">&nbsp;</td>");   // no players
                }
            }

            //
            boolean unaccomp = false;
            boolean showUnaccompNote = true;
            if (g1 + g2 + g3 + g4 + g5 != 0) {    // if 1 or more guests present

               unaccomp = true;     // default = unaccompanied guests

               // if any of these players are a member then set unaccomp to false (changed 3-2-05 paul)
               unaccomp = (!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" ) && g1 == 0) ? false : unaccomp; // (player1 is here, not an x, and g1 is 0)
               unaccomp = (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" ) && g2 == 0) ? false : unaccomp;
               unaccomp = (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" ) && g3 == 0) ? false : unaccomp;
               unaccomp = (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" ) && g4 == 0) ? false : unaccomp;
               unaccomp = (!player5.equals( "" ) && !player5.equalsIgnoreCase( "x" ) && g5 == 0) ? false : unaccomp;
            }

            // if club is ccnaples and no user is assigned to a tee time with all unaccompanied guests, do NOT display an empty note
            if (club.equals("ccnaples") && notes.equals("") && unaccomp && userg1.equals("") && userg2.equals("") && userg3.equals("") && userg4.equals("") && userg5.equals("")) {
                showUnaccompNote = false;
            }
            
            if (updateAccess) {
                //
                //  Column for 'Notes' box if proshop user has TS update access
                //
                if ((!notes.equals("") || !conf.equals( "" ) || unaccomp == true) && showUnaccompNote) {

                    out.println("<form>");
                    out.println("<td bgcolor=\"yellow\" align=\"center\">");
                    out.println("<font size=\"2\">");
                    out.println("<input type=\"image\" src=\"/" +rev+ "/images/notes.jpg\" border=\"0\" onclick=\"openNotesWindow(" + index + ",'" + courseNameT + "', " + time + "," + fb + ");return false;\" title=\"Click here to view notes.\">");
                    out.println("</font></td></form>");         // end of the notes col

                } else {

                   // out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">&nbsp;</td>");   // no notes
                    out.println("<td bgcolor=\"white\" align=\"center\">&nbsp;</td>");   // no notes
                }

                //
                //  Column for 'TeeTime History' box if proshop user has TS update access
                //
                out.println("<form><td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                out.println("<input type=\"image\" src=\"/" +rev+ "/images/history.gif\" width=\"12\" height=\"13\" border=\"0\" onclick=\"openHistoryWindow(" + index + ",'" + courseNameT + "', " + time + "," + fb + ", " + teecurr_id + ");return false;\" title=\"Click here to view tee time history.\">");
                out.println("</td></form>");         // end of the teetime history col
            }

            //
            // AdPace of Play link
            //
            if (paceofplay != 0 && index == 0 && popAccess == true) {        // if PP column

               tmp_color = "";                          // reset pace color
                
               if (!player1.equals("")) {               // if PP button should be displayed

                   tmp_color = bgcolor;
                   if (pace_status_id != 0) {

                        tmp_color = aryPopStatusColors[pace_status_id];
                   }
                   // check to see if PoP status flag is set (not zero) and if so display the corresponding color

                   out.println("<form><td bgcolor=\"" + tmp_color + "\" align=\"center\">");
                   if (popUpdate == true) {
                       out.println("<input type=\"image\" src=\"/" +rev+ "/images/pp_pace.gif\" width=\"24\" height=\"13\" border=\"1\" onclick=\"openPaceWindow(" + teecurr_id + ");return false;\" title=\"Click here to manage Pace of Play for this tee time.\">");
                   } else {
                       out.println("<input type=\"image\" src=\"/" +rev+ "/images/pp_pace.gif\" width=\"24\" height=\"13\" border=\"1\" onclick=\"openPaceWindowDetails(" + teecurr_id + ");return false;\" title=\"Click here to manage Pace of Play for this tee time.\">");
                   }
                   out.println("</td></form>");
 
               } else {            // no button

                   out.println("<td align=\"center\">");
                   out.println("&nbsp;");
                   out.println("</td>");
               }
            }

            //
            // if CSG and today and checked in - add 'P' button to process POS charges for this tee time
            //
            if (parmp.posType.equals( "ClubSystems Group" ) && index == 0 && !courseName1.equals( "-ALL-" )) {

               if ((show1 == 1 && pos1 == 0) || (show2 == 1 && pos2 == 0) || (show3 == 1 && pos3 == 0) ||
                   (show4 == 1 && pos4 == 0) || (show5 == 1 && pos5 == 0)) {

                  out.println("<form><td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<input type=\"image\" src=\"/" +rev+ "/images/sendpos.gif\" width=\"12\" height=\"13\" border=\"0\" onclick=\"openCSGWindow(" + index + ",'" + courseName1 + "', " + time + "," + fb + ");return false;\" title=\"Click here to generate POS charges.\">");
                  out.println("</td></form>");


                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "&time=" +time+ "&fb=" +fb+ "&print=pos\" target=\"_blank\" title=\"Generate POS Charges\" alt=\"Send POS Charges\">");
                  out.println("P</a>");
                  out.println("</font></td>");


               } else {

                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");
               }
            }

            if (club.equals( "medinahcc" )) {        // if Medinah - add Print Tee Time col

               out.println("<td bgcolor=\"#8B8970\" align=\"center\">");
               out.println("<font size=\"2\">");

               if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" )) {
                  out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/servlet/Proshop_sheet?prtTeeTime=yes&time=" +time+ "&fb=" +fb+ "&date=" +date+ "&course=" +courseNameT+ "', 'newwindow', 'Height=380, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
                  out.println("P</a>");
               } else {
                  out.println("&nbsp;");
               }
               out.println("</font></td></form>");         // end of the col
            }

            if (club.equals( "interlachen" )) {      // if Interlachen - add Caddies Assigned col

               out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet#jump" +j+ "\" name=\"caform" +k+ "\">");
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");

               if (p1cw.startsWith ( "CA" ) || p2cw.startsWith ( "CA" ) || p3cw.startsWith ( "CA" ) ||
                   p4cw.startsWith ( "CA" ) || p5cw.startsWith ( "CA" ) ||
                   p1cw.startsWith ( "WA" ) || p2cw.startsWith ( "WA" ) || p3cw.startsWith ( "WA" ) ||
                   p4cw.startsWith ( "WA" ) || p5cw.startsWith ( "WA" )) {

                  out.println("<select size=\"1\" name=\"caddynum\" onChange=\"document.caform" +k+ ".submit()\">");
                  if (numCaddies == 0) {
                     out.println("<option selected value=\"0\">0</option>");
                  } else {
                     out.println("<option value=\"0\">0</option>");
                  }
                  if (numCaddies == 1) {
                     out.println("<option selected value=\"1\">1</option>");
                  } else {
                     out.println("<option value=\"1\">1</option>");
                  }
                  if (numCaddies == 2) {
                     out.println("<option selected value=\"2\">2</option>");
                  } else {
                     out.println("<option value=\"2\">2</option>");
                  }
                  if (numCaddies == 3) {
                     out.println("<option selected value=\"3\">3</option>");
                  } else {
                     out.println("<option value=\"3\">3</option>");
                  }
                  if (numCaddies == 4) {
                     out.println("<option selected value=\"4\">4</option>");
                  } else {
                     out.println("<option value=\"4\">4</option>");
                  }
                  if (numCaddies == 5) {
                     out.println("<option selected value=\"5\">5</option>");
                  } else {
                     out.println("<option value=\"5\">5</option>");
                  }
                  out.println("</select>");

                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                  // out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

               } else {
                  out.println("&nbsp;");
               }
               k++;                                     // increment form id counter
               out.println("</font></td></form>");
            }

            if (club.equals( "cherryhills" )) {      // if Cherry Hills - add Caddies Assigned col

               out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet#jump" +j+ "\" name=\"caform" +k+ "\">");
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");

               if (p1cw.startsWith ( "CDH" ) || p2cw.startsWith ( "CDH" ) || p3cw.startsWith ( "CDH" ) ||
                   p4cw.startsWith ( "CDH" ) || p5cw.startsWith ( "CDH" ) ||
                   p1cw.startsWith ( "CDA" ) || p2cw.startsWith ( "CDA" ) || p3cw.startsWith ( "CDA" ) ||
                   p4cw.startsWith ( "CDA" ) || p5cw.startsWith ( "CDA" ) ||
                   p1cw.startsWith ( "CDB" ) || p2cw.startsWith ( "CDB" ) || p3cw.startsWith ( "CDB" ) ||
                   p4cw.startsWith ( "CDB" ) || p5cw.startsWith ( "CDB" )) {

                  out.println("<select size=\"1\" name=\"caddynum\" onChange=\"document.caform" +k+ ".submit()\">");
                  if (numCaddies == 0) {
                     out.println("<option selected value=\"0\">0</option>");
                  } else {
                     out.println("<option value=\"0\">0</option>");
                  }
                  if (numCaddies == 1) {
                     out.println("<option selected value=\"1\">1</option>");
                  } else {
                     out.println("<option value=\"1\">1</option>");
                  }
                  if (numCaddies == 2) {
                     out.println("<option selected value=\"2\">2</option>");
                  } else {
                     out.println("<option value=\"2\">2</option>");
                  }
                  if (numCaddies == 3) {
                     out.println("<option selected value=\"3\">3</option>");
                  } else {
                     out.println("<option value=\"3\">3</option>");
                  }
                  if (numCaddies == 4) {
                     out.println("<option selected value=\"4\">4</option>");
                  } else {
                     out.println("<option value=\"4\">4</option>");
                  }
                  if (numCaddies == 5) {
                     out.println("<option selected value=\"5\">5</option>");
                  } else {
                     out.println("<option value=\"5\">5</option>");
                  }
                  out.println("</select>");

                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                  // out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

               } else {
                  out.println("&nbsp;");
               }
               k++;                                     // increment form id counter
               out.println("</font></td></form>");
            }

            if (club.equals( "cordillera" )) {         // if Cordillera - add ForeCaddie Assigned col

               out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet#jump" +j+ "\" name=\"caform" +k+ "\">");
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");

               out.println("<select size=\"1\" name=\"forecaddy\" onChange=\"document.caform" +k+ ".submit()\">");
               if (numCaddies == 0) {
                  out.println("<option selected value=\" \"> </option>");
                  out.println("<option value=\"Y\">Y</option>");
               } else {
                  out.println("<option selected value=\"Y\">Y</option>");
                  out.println("<option value=\" \"> </option>");
               }
               out.println("</select>");

               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
               out.println("<input type=\"hidden\" name=\"courseT\" value=\"" + courseNameT + "\">");
               // out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

               k++;                                     // increment form id counter
               out.println("</font></td></form>");
            }

            if ((club.equals( "pecanplantation" ) || clubAGC == true) && index == 0) {   // add 'teed off' col for Pecan Plantation

               out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet#jump" +j+ "\" name=\"caform" +k+ "\">");
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"1\">");

               out.println("<select size=\"1\" name=\"teedOff\" onChange=\"document.caform" +k+ ".submit()\">");
               if (numCaddies == 0) {
                  out.println("<option selected value=\" \"> </option>");
                  out.println("<option value=\"X\">X</option>");
               } else {
                  out.println("<option selected value=\"X\">X</option>");
                  out.println("<option value=\" \"> </option>");
               }
               out.println("</select>");

               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
               out.println("<input type=\"hidden\" name=\"courseT\" value=\"" + courseNameT + "\">");
               // out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

               k++;                                     // increment form id counter
               out.println("</font></td></form>");
            }

            if ((club.equals( "theranchcc" ) || club.equals( "pinery" ) || club.equals( "tualatincc" )) && index == 0) {   // add 'teed off' col for The Ranch CC

               if (!player1.equals("")) {               // if TO option should be displayed

                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet#jump" +j+ "\" name=\"caform" +k+ "\">");
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"1\">");

                  out.println("<select size=\"1\" name=\"teedOff\" onChange=\"document.caform" +k+ ".submit()\">");
                  if (numCaddies == 0) {
                     out.println("<option selected value=\" \"> </option>");
                     out.println("<option value=\"O\">O</option>");
                     out.println("<option value=\"X\">X</option>");
                  } else {
                     if (numCaddies == 1) {
                        out.println("<option selected value=\"X\">X</option>");
                        out.println("<option value=\"O\">O</option>");
                        out.println("<option value=\" \"> </option>");
                     } else {
                        out.println("<option selected value=\"O\">O</option>");
                        out.println("<option value=\"X\">X</option>");
                        out.println("<option value=\" \"> </option>");
                     }
                  }
                  out.println("</select>");

                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                  out.println("<input type=\"hidden\" name=\"courseT\" value=\"" + courseNameT + "\">");
                  // out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                  k++;                                     // increment form id counter
                  out.println("</font></td></form>");
                     
               } else {
                 
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("&nbsp;");
                  out.println("</td>");
               }
            }

            out.println("</tr>");         // end of the row

         }  // end of IF skipTime (blocker, etc)

      }  // end of while (rs loop of all tee times)

      pstmt.close();

      out.println("</table>");                         // end of tee sheet table
      out.println("</td></tr>");
      out.println("</table><br>");                            // end of main page table


      //
      // Final script portion to make the floating calendar start
      // ceiling is the postion of the cal_top image that is hidden at
      // the top of the tee sheet header
      //
      out.println("<script type=\"text/javascript\">");
       out.println("var FCAL_ceiling = (document.getElementById('cal_cap').offsetTop) + 7;");
       out.println("FCAL_position_cal('fCal', 0,30).move_cal();");
      out.println("</script>");
     
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + errorMsg);
      out.println("<BR><BR>" + e1.toString());
      out.println("<BR><BR>" + e1.toString());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }

   //
   //  End of HTML page
   //
   out.println("</center></body></html>");
   out.close();
   if (Gzip == true) {
      resp.setContentLength(buf.size());                 // set output length
      resp.getOutputStream().write(buf.toByteArray());
   }

 }  // end of doPost

 

 // ********************************************************************************************
 //  Process the POS charges for this day - Abacus21, Jonas, CSG, NorthStar, IBS, TAI or ClubSoft
 // ********************************************************************************************

 private void promptPOS2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con) 
         throws ServletException, IOException {


   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2s = null;

   ResultSet rs = null;
   //ResultSet rs2 = null;

   //String player = "";
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
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   //String lineNS = "";
   String filename = "";
   String temp = "";
   String day = "";
   String sfb = "";
   String stime = "";
   String ampm = "";


   int guest = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int i = 0;
   //int p = 0;
   int ttid = 0;
   int fb = 0;
   int tfb = 0;
   int hr = 0;
   int min = 0;
   int sec = 0;
   int time = 0;
   int ttime = 0;
   int time2 = 0;
   int done = 0;
   int masterDone = 0;
   int singleDone = 0;

   long resnum = 0;
   long resnum2 = 0;
   long ttidnum = 0;
   long ttidnum2 = 0;

   double salestax = 0;
   double salestaxCurr = 0;

   //
   //  get the club name from the session
   //
   String club = (String)session.getAttribute("club");      // get club name

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);

   //
   //  parm block to hold the course parameters
   //
   //parmCourse parmc = new parmCourse();

   //
   //  parm block to hold the POS parameters
   //
   parmPOS parmp = new parmPOS();     

   //
   //  Get the golf course name requested
   //
   String course = req.getParameter("course");
   String index = req.getParameter("index");         // get the index value of the day selected

   if (req.getParameter("time") != null) {

      stime = req.getParameter("time");         // get time if provided (CSG) 
   }

   if (req.getParameter("fb") != null) {

      sfb = req.getParameter("fb");            // get fb if provided (CSG)
   }


   try {
      //
      // Get the Guest Types from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

      //
      //  Get the POS System Parameters for this Club & Course
      //
      getClub.getPOS(con, parmp, course);

   }
   catch (Exception e1) {

      resp.setContentType("text/html");                   // normal html response

      out.println(SystemUtils.HeadTitle("Proshop - POS Prompt"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Error processing the POS Request");
      out.println("<BR><BR>Error: " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<form>");
      out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close  \" onClick='self.close()' alt=\"Close\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //   Save club name in parmp
   //
   parmp.club = club;
     
   if (parmp.posType.equals( "IBS" )) {       // get sales tax rate if IBS

      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT salestax FROM club5");

         if (rs.next()) {

            salestaxCurr = rs.getDouble("salestax");
         }
         stmt.close();

      }
      catch (Exception ignore) {
      }
   }


   //
   //  Check if first time here, or we should build the report
   //
   if (req.getParameter("POScontinue") == null) {

      //  First time here - prompt to continue
        
      resp.setContentType("text/html");                   // normal html response

      out.println(SystemUtils.HeadTitle("Proshop - POS Prompt"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H3>Send POS Charges Request</H3>");
      out.println("You have requested that all current POS related charges be sent to the POS system.<br><br>");
      out.println("Only charges for players that have been 'Checked In' will be transferred.");
      out.println("<BR><BR>Would you like to proceed with this report?");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
        
      if (parmp.posType.equals( "Jonas" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_jonas.htm', 'newwindow', 'height=540, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      if (parmp.posType.equals( "Abacus21" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_abacus21.htm', 'newwindow', 'height=540, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      if (parmp.posType.equals( "ClubSystems Group" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_csg.htm', 'newwindow', 'height=540, width=560, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      if (parmp.posType.equals( "TAI Club Management" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_tai.htm', 'newwindow', 'height=540, width=560, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      if (parmp.posType.equals( "IBS" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_ibs.htm', 'newwindow', 'height=540, width=560, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      if (parmp.posType.equals( "ClubSoft" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_clubsoft.htm', 'newwindow', 'height=540, width=560, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      out.println("<script>");
      out.println("function submitForm() {");
      out.println(" var f = document.forms[0];");
      out.println(" f.btnSubmit.value='Please Wait';");
      out.println(" f.btnSubmit.disabled=true;");
      out.println(" f.btnClose.value='Please Wait';");
      out.println(" f.btnClose.disabled=true;");
      out.println(" f.submit();");
      out.println("}");
      
      out.println("</script>");
      
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\" id=frmPOS>");

      if (parmp.posType.equals( "IBS" )) {

         out.println("<br></font><font size=\"3\">");
         out.println("<b>NOTE:</b> IBS requires that we include any sales tax charges.<br>");
         out.println("Please confirm or update the following rate.<br><br>");
         out.println("<input type=\"text\" name=\"salestax\" value=\"" +salestaxCurr+ "\" size=\"5\" maxlength=\"8\">&nbsp;&nbsp;<b>Enter as decimal value (i.e. 6% = 0.06)</b>");
         out.println("<BR><BR>");
      }

      out.println("<input type=\"hidden\" name=\"POScontinue\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"print\" value=\"pos\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + stime + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + sfb + "\">");
      // out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
      out.println("<input type=\"button\" name=\"btnSubmit\" value=\"Yes - Continue\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\" onclick=\"submitForm()\">");
      out.println("</font><br><br>");

      out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" name=\"btnClose\" value=\"Return\" onclick=\"self.close()\" alt=\"Close\">");
      out.println("</form></CENTER></BODY></HTML>");
      out.close();
      return;

   } else {
     
      //********************************************************************
      //  User was prompted and opted to continue - build report
      //********************************************************************
      //
      if (req.getParameter("salestax") != null) {

         temp = req.getParameter("salestax");            // get sales tax rate if provided (IBS)
           
         if (temp.endsWith( "%" )) {                     // strip the percent sign if they entered it
           
            temp = stripLast(temp);      
         }
           
         try {
            salestax = Double.parseDouble(temp);
         }
         catch (NumberFormatException e) {
            salestax = salestaxCurr;                     // use existing sales tax if error
         }
  
         parmp.salestax = salestax;
           
         //
         //  save sales tax value if changed and not zero
         //
         if (salestax != salestaxCurr && salestax != 0) {
           
            try {

               pstmt = con.prepareStatement (
                  "UPDATE club5 SET salestax = ? " +
                  "WHERE clubName != ''");

               pstmt.clearParameters();            // clear the parms
               pstmt.setDouble(1, salestax);

               pstmt.executeUpdate();              // execute the prepared stmt

               pstmt.close();

            }
            catch (Exception ignore) {
            }
         }
      }

      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) +1;
      int daynum = cal.get(Calendar.DAY_OF_MONTH);
      hr = cal.get(Calendar.HOUR_OF_DAY);           // 24 hr clock (0 - 23)
      min = cal.get(Calendar.MINUTE);
      sec = cal.get(Calendar.SECOND);

      long date = year * 10000;                     // create a date field of yyyymmdd
      date = date + (month * 100);
      date = date + daynum;                            // date = yyyymmdd

      //
      //  Get time and fb if provided (CSG)
      //
      if (!stime.equals( "" )) {
        
         ttime = Integer.parseInt(stime);
      }
      if (!sfb.equals( "" )) {

         tfb = Integer.parseInt(sfb);
      }

      if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" ) || parmp.posType.equals( "TAI Club Management" ) ||
          parmp.posType.equals( "ClubSoft" )) {

         //
         //  Set the values for the Reservation Number and Tee Time Id fields (yymmddnn, where nn changes)
         //
         resnum2 = date - 20000000;                     // resnum = yymmdd
         resnum = resnum2 * 1000;                        // shift over for nnn (1 - 990)
         ttidnum = resnum;                             // ditto
         ttidnum2 = resnum;                            // ditto - save this portion

         resn++;                                       // get the next res #
         ttidn++;                                      // get the next tee time num

         if (resn > 990) {
            resn = 1;          // init
         }
         if (ttidn > 990) {
            ttidn = 1;          // init
         }

         resnum += resn;                               // create new res num
         ttidnum += ttidn;                             // create new tee time id num

         parmp.sdate = String.valueOf(date);           // save string value of date for file

         filen++;                                                     // get next unique id value
         if (filen > 999) {
            filen = 1;         // reset
         }

         //
         // establish an ASCII filename
         //
         if (club.equals( "medinahcc" ) && parmp.posType.equals( "Abacus21" )) {    // if Medinah and Abacus21
           
            filename = String.valueOf( resnum2 );           // yymmdd
            temp = String.valueOf( filen );                 // nnn
              
            if (course.endsWith( "1" )) {                    // file name based on course (No 1, No 2, or No 3)
              
               filename = "C1-" + filename + "-" + temp + ".ASC";  // C1-yymmdd-nnn.ASC (where nnn = sequential value)
  
            } else {

               if (course.endsWith( "2" )) {

                  filename = "C2-" + filename + "-" + temp + ".ASC";  // C2-yymmdd-nnn.ASC (where nnn = sequential value)

               } else {

                  filename = "C3-" + filename + "-" + temp + ".ASC";  // C3-yymmdd-nnn.ASC (where nnn = sequential value)
               }
            }

         } else {     // NOT Medinah & Abacus

            filename = String.valueOf( filen );
            
            if (parmp.posType.equals( "ClubSoft" )) {
            
               filename = "ForeTees" + filename + ".CSV";         // name = ForeTeesnn.CSV
            
            } else {
               
               filename = "ForeTees" + filename + ".ASC";         // name = ForeTeesnn.ASC
            }
         }
           
         if (parmp.posType.equals( "TAI Club Management" ) || parmp.posType.equals( "ClubSoft" )) {

            if (daynum < 10) {

               parmp.sdate = String.valueOf(month) + "/0" + String.valueOf(daynum) + "/" + String.valueOf(year);  // date for file

            } else {

               parmp.sdate = String.valueOf(month) + "/" + String.valueOf(daynum) + "/" + String.valueOf(year);  // date for file
            }
         }

      } else {         // NorthStar or CSG or IBS - date = mm/dd/yyyy

         parmp.count = 0;                 // init record counter

         //
         //  build the header for the text file - mm/dd/yyyy hh:mm:ss
         //
         //  establish an ASCII filename       (clubname-mmddyyyy-hhmmss.txt)
         //
         String tempNS = String.valueOf(month);
           
         if (month < 10) {
           
            tempNS = "0" + String.valueOf(month);
         }
         
         if (parmp.posType.equals( "IBS" )) {    // if IBS

            filename = "FT_" + tempNS;          // FT_mm

         } else {

            filename = club + "-" + tempNS;          // clubname-mm
         }

         parmp.sdate = tempNS + "/";              // mm/

         tempNS = String.valueOf(daynum);

         if (daynum < 10) {

            tempNS = "0" + String.valueOf(daynum);
         }

         filename = filename + tempNS + String.valueOf(year) + "-";                 // clubname-mmddyyyy-  OR FT_mmddyyyy-

         parmp.sdate = parmp.sdate + tempNS + "/" + String.valueOf(year);     // mm/dd/yyyy


         if (parmp.posType.equals( "ClubSystems Group" )) {    // if CSG - only 1 tee time per file!!

            hr = ttime / 100;              // use tee time for time value
            min = ttime - (hr * 100);
            sec = 0;
              
            ampm = " AM";
              
            if (hr == 12) {
              
               ampm = " PM";
            }
              
            if (hr > 12) {
              
               hr = hr - 12;
               ampm = " PM";
            }
              
            tempNS = String.valueOf(hr);

            if (hr < 10) {

               tempNS = "0" + String.valueOf(hr);
            }

            filename = filename + tempNS;                           // clubname-mmddyy-hh

            parmp.stime = tempNS + ":";                             // hh:

            tempNS = String.valueOf(min);

            if (min < 10) {

               tempNS = "0" + String.valueOf(min);
            }

            filename = filename + tempNS;                           // clubname-mmddyy-hhmm

            parmp.stime = parmp.stime + tempNS + ":";               // hh:mm:

            tempNS = String.valueOf(sec);

            if (sec < 10) {

               tempNS = "0" + String.valueOf(sec);
            }

            filename = filename + tempNS + ".dat";                // clubname-mmddyy-hhmmss.dat (CSG file name)

            parmp.stime = parmp.stime + tempNS + " " +ampm;       // hh:mm:ss AM (or PM)

            //
            //  Set the output type to Text File - must do this now!!
            //
            resp.setContentType("text/csv");                         // text file
            resp.setHeader("Content-Disposition", "attachment;filename=\"" +filename+ "\"");         // default file name


         } else {            // Northstar or IBS
           
            tempNS = String.valueOf(hr);

            if (hr < 10) {

               tempNS = "0" + String.valueOf(hr);
            }

            filename = filename + tempNS;                           // clubname-mmddyy-hh

            if (!parmp.posType.equals( "IBS" )) {    // if NOT IBS

               parmp.sdate = parmp.sdate + " " + tempNS + ":";         // mm/dd/yyyy hh:
            }

            tempNS = String.valueOf(min);

            if (min < 10) {

               tempNS = "0" + String.valueOf(min);
            }

            filename = filename + tempNS;                           // clubname-mmddyy-hhmm

            if (!parmp.posType.equals( "IBS" )) {    // if NOT IBS

               parmp.sdate = parmp.sdate + tempNS + ":";               // mm/dd/yyyy hh:mm:
            }

            tempNS = String.valueOf(sec);

            if (sec < 10) {

               tempNS = "0" + String.valueOf(sec);
            }

            filename = filename + tempNS;                      // clubname-mmddyy-hhmmss (".txt" to be added below)

            if (parmp.posType.equals( "IBS" )) {    // if IBS

               filename = filename + ".asc";                   // IBS = FT_mmddyy-hhmmss.asc
                 
            } else {

               parmp.sdate = parmp.sdate + tempNS;                // mm/dd/yyyy hh:mm:ss
            }
         }
      }

      masterDone = 0;             // init master done flag

      //
      // create text file (one row per charge)
      //
      //  Get today's tee sheet and process the players one at a time.
      //
      try {

         if (parmp.posType.equals( "ClubSystems Group" )) {    // if CSG - only 1 tee time per file!!

            pstmt2s = con.prepareStatement (
               "SELECT * " +
               "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt2s.clearParameters();        // clear the parms
            pstmt2s.setLong(1, date);
            pstmt2s.setInt(2, ttime);
            pstmt2s.setInt(3, tfb);
            pstmt2s.setString(4, course);

         } else {

            pstmt2s = con.prepareStatement (
               "SELECT * " +
               "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb");

            pstmt2s.clearParameters();        // clear the parms
            pstmt2s.setLong(1, date);
            pstmt2s.setString(2, course);
         }

         rs = pstmt2s.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            day = rs.getString("day");
            hr = rs.getInt("hr");
            min = rs.getInt("min");
            time = rs.getInt("time");
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
            show1 = rs.getInt("show1");
            show2 = rs.getInt("show2");
            show3 = rs.getInt("show3");
            show4 = rs.getInt("show4");
            fb = rs.getInt("fb");
            player5 = rs.getString("player5");
            user5 = rs.getString("username5");
            p5cw = rs.getString("p5cw");
            show5 = rs.getInt("show5");
            userg1 = rs.getString("userg1");
            userg2 = rs.getString("userg2");
            userg3 = rs.getString("userg3");
            userg4 = rs.getString("userg4");
            userg5 = rs.getString("userg5");
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

            done = 0;            // init done some flag for this tee time
            singleDone = 0;      // init single tee time done flag

            parmp.day = day;       // save tee time values in parmp
            parmp.time = time;
            parmp.date = date;
            parmp.course = course;
            parmp.hist_fb = fb;
            

            if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

               //
               //  Determine the time and save string for charge records
               //
               StringBuffer tempSB = null;
               time2 = time;                    // save time

               if (time2 > 1159) {               // if PM

                  if (time2 > 1259) {            // if 1 PM or later

                     time2 = time2 - 1200;
                  }

                  parmp.stime = String.valueOf( time2 );      // convert time to string value
                  tempSB = new StringBuffer(parmp.stime);     // put in string buffer
                  tempSB.append(",P,");                       // indicate PM

               } else {

                  parmp.stime = String.valueOf( time2 );      // convert time to string value
                  tempSB = new StringBuffer(parmp.stime);     // put in string buffer
                  tempSB.append(",A,");                       // indicate AM
               }
               parmp.stime = tempSB.toString();               // save as string value
                 
               //
               //  Set the output type to Text File - must do this now!!
               //
               resp.setContentType("text/csv");                         // text file
               resp.setHeader("Content-Disposition", "attachment;filename=\"" +filename+ "\"");         // default file name
            }

            if (parmp.posType.equals( "TAI Club Management" ) || parmp.posType.equals( "IBS" ) || parmp.posType.equals( "ClubSoft" )) {

               if (min < 10) {
                 
                  parmp.stime = String.valueOf(hr) + ":0" + String.valueOf(min);  // tee time for file
                     
               } else {
                 
                  parmp.stime = String.valueOf(hr) + ":" + String.valueOf(min);  // tee time for file
               }

               //
               //  Set the output type to Text File - must do this now!!
               //
               resp.setContentType("text/csv");                         // text file
               resp.setHeader("Content-Disposition", "attachment;filename=\"" +filename+ "\"");         // default file name
            }

            //
            //  Set TTID (player id) for CSG (must be 10 digits)
            //
            ttid = 1000000010;

            //
            //  Process one player at a time to determine any charges
            //
            if (!player1.equalsIgnoreCase( "x" ) && !player1.equals( "" ) && show1 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user1.equals( "" )) {            // if no username for this player

                  ploop1:
                  while (i < parm.MAX_Guests) {
                     if (player1.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player1 is a guest name
                        break ploop1;
                     }
                     i++;
                  }
               }
               parmp.pcw = p1cw;
               parmp.p9 = p91;

               if (guest == 0) {        // if member

                  if (!user1.equals( "" ) && pos1 == 0) {      // skip if no user name found or already processed

                     parmp.player = "";   // indicate member
                     parmp.user = user1;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos1 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos1 = 1;            // set as processed, not sent
                     }
                  }

               } else {          // else guest

                  if (!userg1.equals( "" ) && pos1 == 0) {      // skip if no member associated with this guest

                     parmp.player = player1;   // indicate guest - pass the guest type
                     parmp.user = userg1;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest
                          
                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos1 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos1 = 1;            // set as processed, not sent
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null

            ttid+= 10;                // next player id
              
            if (!player2.equalsIgnoreCase( "x" ) && !player2.equals( "" ) && show2 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user2.equals( "" )) {            // if no username for this player

                  ploop2:
                  while (i < parm.MAX_Guests) {
                     if (player2.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player2 is a guest name
                        break ploop2;
                     }
                     i++;
                  }
               }
               parmp.pcw = p2cw;
               parmp.p9 = p92;

               if (guest == 0) {        // if member

                  if (!user2.equals( "" ) && pos2 == 0) {      // skip if no user name found

                     parmp.player = "";   // indicate member
                     parmp.user = user2;
                       
                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos2 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos2 = 1;            // set as processed, not sent
                     }
                  }

               } else {          // else guest

                  if (!userg2.equals( "" ) && pos2 == 0) {      // skip if no member associated with this guest

                     parmp.player = player2;   // indicate guest - pass the guest type
                     parmp.user = userg2;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos2 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos2 = 1;            // set as processed, not sent
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null

            ttid+= 10;                // next player id

            if (!player3.equalsIgnoreCase( "x" ) && !player3.equals( "" ) && show3 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user3.equals( "" )) {            // if no username for this player

                  ploop3:
                  while (i < parm.MAX_Guests) {
                     if (player3.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player3 is a guest name
                        break ploop3;
                     }
                     i++;
                  }
               }
               parmp.pcw = p3cw;
               parmp.p9 = p93;

               if (guest == 0) {        // if member

                  if (!user3.equals( "" ) && pos3 == 0) {      // skip if no user name found

                     parmp.player = "";   // indicate member
                     parmp.user = user3;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos3 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos3 = 1;            // set as processed, not sent
                     }
                  }

               } else {          // else guest

                  if (!userg3.equals( "" ) && pos3 == 0) {      // skip if no member associated with this guest

                     parmp.player = player3;   // indicate guest - pass the guest type
                     parmp.user = userg3;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos3 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos3 = 1;            // set as processed, not sent
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null

            ttid+= 10;                // next player id

            if (!player4.equalsIgnoreCase( "x" ) && !player4.equals( "" ) && show4 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user4.equals( "" )) {            // if no username for this player

                  ploop4:
                  while (i < parm.MAX_Guests) {
                     if (player4.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player4 is a guest name
                        break ploop4;
                     }
                     i++;
                  }
               }
               parmp.pcw = p4cw;
               parmp.p9 = p94;

               if (guest == 0) {        // if member

                  if (!user4.equals( "" ) && pos4 == 0) {      // skip if no user name found

                     parmp.player = "";   // indicate member
                     parmp.user = user4;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos4 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos4 = 1;            // set as processed, not sent
                     }
                  }

               } else {          // else guest

                  if (!userg4.equals( "" ) && pos4 == 0) {      // skip if no member associated with this guest

                     parmp.player = player4;   // indicate guest - pass the guest type
                     parmp.user = userg4;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos4 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos4 = 1;            // set as processed, not sent
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null

            ttid+= 10;                // next player id

            if (!player5.equalsIgnoreCase( "x" ) && !player5.equals( "" ) && show5 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user5.equals( "" )) {            // if no username for this player

                  ploop5:
                  while (i < parm.MAX_Guests) {
                     if (player5.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player5 is a guest name
                        break ploop5;
                     }
                     i++;
                  }
               }
               parmp.pcw = p5cw;
               parmp.p9 = p95;

               if (guest == 0) {        // if member

                  if (!user5.equals( "" ) && pos5 == 0) {      // skip if no user name found

                     parmp.player = "";   // indicate member
                     parmp.user = user5;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos5 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos5 = 1;            // set as processed, not sent
                     }
                  }

               } else {          // else guest

                  if (!userg5.equals( "" ) && pos5 == 0) {      // skip if no member associated with this guest

                     parmp.player = player5;   // indicate guest - pass the guest type
                     parmp.user = userg5;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "IBS" )) {

                        done = buildIBS(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSoft" )) {

                        done = buildClubSoft(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos5 = 3;            // set as processed and sent
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                        
                     } else {
                        
                        pos5 = 1;            // set as processed, not sent
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null
  
            //
            //  Now set this tee time as processed (if we did)
            //
            if (singleDone == 1) {          // if we did any in this tee time
              
               PreparedStatement pstmt3 = con.prepareStatement (
                   "UPDATE teecurr2 SET pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ? " +
                   "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               //
               //  execute the prepared statement to update the tee time slot
               //
               pstmt3.clearParameters();        // clear the parms
               pstmt3.setInt(1, pos1);
               pstmt3.setInt(2, pos2);
               pstmt3.setInt(3, pos3);
               pstmt3.setInt(4, pos4);
               pstmt3.setInt(5, pos5);

               pstmt3.setLong(6, date);
               pstmt3.setInt(7, time);
               pstmt3.setInt(8, fb);
               pstmt3.setString(9, course);
               pstmt3.executeUpdate();

               pstmt3.close();
                 
               //
               //  If POS uses Reservation Numbers (identifies each tee time)
               //
               //if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" ) || parmp.posType.equals( "TAI Club Management" )) {
               //if (club.equals("altolakes") || club.equals("pattersonclub")) {
               //
               //  5/29/08 - NOT SURE why this was ever changed for Jonas, but some clubs want us to change the res num for each tee time.
               //            This is necessary to use the "Quick Check-In" feture in Jonas POS - see P. 20 of their user doc.
               //            We may need to change this to not update resnum for any club that complains.
               //
               if (parmp.posType.equals( "Jonas" )) {
                             
                  //
                  //  Increment Reservation Number for next Tee Time
                  //
                  resnum++;             // res num for records in file (this can keep incrementing)
                  resn++;                         
                     
                  if (resn > 990) {         // don't let resn get too big
                    
                     resn = 1;              // start over
                  }
               }
  
            }
  
         }     // end of WHILE

         pstmt2s.close();

      }
      catch (Exception e1) {

         String errorMsg1 = "Error1 in Proshop_sheet promptPOS2: ";
         errorMsg1 = errorMsg1 + e1.getMessage();                                // build error msg

         SystemUtils.logError(errorMsg1);                                       // log it
      }

      //
      // done with text file - check if we built any records
      //
      if (masterDone == 1) {   // if we built any records

         if (parmp.posType.equals( "NorthStar" )) {

            //
            //  NorthStar - close out the file (add counter and footer)
            //
            String counts = String.valueOf( parmp.count );     // create string value from count

            addLineNS(filename, counts);                       // put in file

            addLineNS(filename, "EOF");                        // add End Of File indicator (footer)
              
            resp.setContentType("text/html");                   // normal html response
            resp.setHeader("Content-Disposition", "inline");    // undo attachment change from above

            out.println(SystemUtils.HeadTitle("Proshop - POS Prompt"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>POS Charges Sent</H3>");
            out.println("<br>The POS charges have been sent to the POS system.");
            out.println("<BR><BR>");
            out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return  \" onClick='self.close()' alt=\"Close\">");
            out.println("</CENTER></BODY></HTML>");
         }

      } else {

         resp.setContentType("text/html");                   // normal html response
         resp.setHeader("Content-Disposition", "inline");    // undo attachment change from above

         out.println(SystemUtils.HeadTitle("Proshop - POS Prompt"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>No POS Charges To Send</H3>");
         out.println("<br>There are no new POS charges to send.");
         out.println("<BR><BR>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return  \" onClick='self.close()' alt=\"Close\">");
         out.println("</CENTER></BODY></HTML>");
      }

      out.close();              // close the file
  
   }

   return;
 }                   // end of promptPOS2


 // ********************************************************************
 //  Process the Abacus or Jonas POS charges for an individual member
 //
 //  Build an ASCII file containing the following:
 //
 //   Columns:
 //       1 Member Number or POSID                                Alpha 10
 //       2 Member? (Y/N)     (always 'Y')                        Alpha 1
 //       3 Number Of Guests                                      Alpha 3
 //       4 Last Name                                             Alpha 30
 //       5 First Name                                            Alpha 20
 //       6 Telephone Number   (skip)                             Alpha 15
 //       7 Tee off Date                                          Date YYYYMMDD
 //       8 Tee Off Time                                          Alpha HHMM
 //       9 AM / PM (A or P)                                      Alpha 1
 //       10 Course Code       ('A' if only 1 course)             Alpha 2
 //       11 Start Tee/Group Number    (skip)                     Alpha 3
 //       12 UNUSED (reserved for Deposit Amount)                 Numeric 0000.00
 //       13 Credit Card Type          (skip)                     Alpha 4
 //       14 Credit Card Number        (skip)                     Alpha 20
 //       15 Credit Card Expiry        (skip)                     Alpha YYYYMM
 //       16 Tee Time Sales Item                                  Alpha 6
 //       17 Units Sold                                           Numeric 0000
 //       18 Confirmation/Reservation Number                      Alpha 10
 //       19 UNUSED (Tee Time Sales Item Price) (skip)            Numeric 0000.00
 //       20 Tee-Time ID number                                   Alpha 10
 //       21 Green Fee Credit Card Authorization Code  (skip)     Alpha 9
 //       22 Credit Card Authorization Date            (skip)     Date YYYYMMDD
 //       23 Prepaid Indicator (Y-yes, N-no)           ('N')      Alpha 1
 //       24 Res. Fee Credit Card Authorization Code   (skip)     Alpha 9
 //       25 Reservation Fee Amount                    (skip)     Numeric ####.00
 //       26 Checked In Indicator (Y-yes, N-no)        ('Y')      Alpha 1
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public int buildJonas(parmPOS parmp, PrintWriter out, Connection con, long resnum, long ttidnum, String club) {


   ResultSet rs = null;

   String mship = "";
   String mnum = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String item = "";
   String line = "";
   String tcourse = "";        // temp course name for customs

   int i = 0;
   int p9c = 0;
   int done = 0;
   int daynum = (int)(parmp.date - ((parmp.date / 100) * 100));    // dd = yyyymmdd - yyyymm00
     

   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         mnum = rs.getString(4);
         posid = rs.getString(5);
      }
      pstmtc.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildJonas for club: " + club;
      errorMsg1 = errorMsg1 + ", Exception: " + e1.getMessage();      // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   //
   //  if Lakewood CC - use the member posid instead of the mnum
   //
   if ((club.equals( "lakewood" ) || club.equals( "pinnaclepeak" )) & !posid.equals( "" )) {
     
      mnum = posid;
   }

   //
   //  Skip if no mNum/posid - otherwise entire file will fail
   //
   if (!mnum.equals( "" )) {

      try {

         //*******************************************************************************************
         //  First check if there is a charge amount associated with this member's mode of trans
         //*******************************************************************************************
         //
         if (club.equals( "medinahcc" )) {      // if Medinah do manual item codes

            if (!parmp.player.startsWith( "Comp" ) && !parmp.player.startsWith( "PGA" ) && 
                !parmp.player.startsWith( "Country Club" ) && !parmp.player.startsWith( "Credit Card" )) {  // DO NOT charge these guest types

               if (parmp.pcw.equals( "4BG" )) {          // 4 Bagger

                  item = "20190";
               }

               if (parmp.pcw.equals( "CAR" )) {          // Half Cart

                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = "20130";
                  } else {
                     item = "20110";
                  }
               }

               if (parmp.pcw.equals( "FCA" )) {          // Full Cart

                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = "20120";
                  } else {
                     item = "20100";
                  }
               }

               if (parmp.pcw.equals( "2CA" )) {          //  Two Cart

                  item = "20000";
               }

               if (parmp.pcw.equals( "1.5" )) {         //  One & a half cart Cart

                  item = "20105";
               }

               if (parmp.pcw.equals( "PC" )) {          //  Pull Cart

                  item = "20140";
               }

            }

         } else {        // not Medinah

            i = 0;
            loop1:
            while (i < parmp.MAX_Tmodes) {

               if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

                  tmode = parmp.tmode[i];             // get full description of tmode
                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.t9pos[i];               // get Item Group # for tmode
                  } else {
                     item = parmp.tpos[i];               // get Item Group # for tmode
                  }
                  break loop1;
               }
               i++;
            }
         }

         // 
         // If Long Cove and a certain Guest Type, then do not charge for mode of trans
         //
         if (club.equals( "longcove" )) {  

            if (parmp.player.startsWith( "Comp A" ) || parmp.player.startsWith( "PGA" ) || parmp.player.startsWith( "Recip Guest" ) || 
                parmp.player.startsWith( "Employee" ) || parmp.player.startsWith( "Tournament Guest" )) {

               item = "";        // no charge         
            }
         }


         //
         // If Virginia CC and a certain Guest Type, then do not charge for mode of trans
         //
         if (club.equals( "virginiacc" )) {

//            if (parmp.player.startsWith( "Employee" ) || parmp.player.startsWith( "Industry Comp" ) || parmp.player.startsWith( "Reciprocal" ) ||
//                parmp.player.startsWith( "Outing Guest" ) || parmp.player.startsWith( "Guest" ) || parmp.player.startsWith( "VCU" ) ||
//                parmp.player.startsWith( "U of R" ) || parmp.player.startsWith( "St Chris" ) || parmp.player.startsWith( "Collegiate" ) ||
//                parmp.player.startsWith( "St Catherines" ) || parmp.player.startsWith( "Assoc Guest" )) {
            
            if (parmp.player.startsWith( "Reciprocal" )) {

               item = "";        // no charge
            }
         }


         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            //
            //  We can now build the charge string for Mode of Trans charge
            //
            StringBuffer tempSB = new StringBuffer(mnum);     // put member # in string buffer
            tempSB.append(",Y,1,");                           // indicator, # of guests (players?)
            tempSB.append(lname);                             // last name
            tempSB.append(",");
            tempSB.append(fname);                             // first name
            tempSB.append(",,");                              // skip phone #
            tempSB.append(parmp.sdate);                       // date of tee time
            tempSB.append(",");
            tempSB.append(parmp.stime);                       // time (includes A or P for AM or PM and ,)
            tempSB.append(parmp.courseid);                    // Course Code
            tempSB.append(",,,,,,");                          // skips
            tempSB.append(item);                              // Sales Item #
            tempSB.append(",1,");                             // units sold
            tempSB.append(resnum);                            // Reservation #
            tempSB.append(",,");                              // unused
            tempSB.append(ttidnum);                           // Tee Time Id #
            tempSB.append(",,,N,,,Y");                        // units sold, skips, Prepaid, Checked-in

            line = tempSB.toString();                         // save as string value

            out.print(line);
            out.println();      // output the line

            done = 1;           // indicate charge sent

            ttidn++;            // bump tee time id number
            ttidnum++;
                 
            //
            //  Save charge data in pos_hist for reports
            //
            parmp.hist_posid = mnum;
            if (!parmp.player.equals( "" )) {
               parmp.hist_player = parmp.player;          // if guest
            } else {
               parmp.hist_player = fname + " " + lname;   // else use member name
            }
            parmp.hist_price = "";
            parmp.hist_item_name = parmp.pcw;
            parmp.hist_item_num = item;

            add_POS_hist(parmp, con);       // go make the entry 
         }


         //
         //*******************************************************************************************
         //  get the mship class and charge amount, if any and if member!
         //*******************************************************************************************
         //
         i = 0;
         item = "";
           
         if (parmp.player.equals( "" )) {       // if member

            loop2:
            while (i < parmp.MAX_Mships) {

               if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.mship9I[i];               // get mship item group #
                  } else {
                     item = parmp.mshipI[i];               // get mship item group #
                  }
                  break loop2;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               //
               //  IF Congressional - check for custom codes based on Course
               //
               if (club.equals( "congressional" )) {

                  if (mship.equals( "Non Resident" )) {

                     tcourse = congressionalCustom.getFullCourseName(parmp.date, daynum, parmp.course);   // get course name for this date

                     item = "000G39";                     // item code for Blue course

                     if (tcourse.endsWith( "Gold" )) {

                        item = "000G40";                 // item code for Gold course
                     }
                  }
               }       // end of IF Congressional
               
   
               //
               //  IF St. Clair CC - check for custom codes based on Course
               //
               if (club.equals( "stclaircc" )) {

                  if (parmp.course.equals( "Championship" )) {
                      
                      if (mship.equals( "Voting" ) || mship.equals( "Senior" ) || mship.equals( "Intermediate" ) || 
                              mship.equals( "Spouse Golf" ) || mship.equals( "Junior Golf" ) || mship.equals( "Assoc20" ) || 
                              mship.equals( "Assoc Spouse20" ) || mship.equals( "Assoc Jr20" ) || mship.equals( "Associate Golf" ) || 
                              mship.equals( "Associate Spouse" ) || mship.equals( "Associate Jr" )) {
                          
                          item = "18MNON";                 
                          
                          if (parmp.p9 == 1) {                   // if 9 holes
                              
                              item = "09MNON";  
                          }

                     } else if (mship.equals( "Limited Golf" ) || mship.equals( "Spouse Golf 9" )  || mship.equals( "NR Golf" ) ||
                                mship.equals( "Limited Spouse" ) || mship.equals( "Limited Jr" )) {
                         
                         item = "18MPAR";                 
                         
                         if (parmp.p9 == 1) {                   // if 9 holes
                             
                             item = "09MPAR";  
                         }
                         
                     } else if (mship.equals("Social Golf") || mship.equals("Soc Golf Spouse") || mship.equals("Soc Jr Golf") ||
                                mship.equals("Emeritus")) {
                         
                         item = "18MFUL";
                         
                         if (parmp.p9 == 1) {
                             
                             item = "09MFUL";
                         }
                         
                     } else {                // Social or others
                          
                          item = "";           // others entered as guest
                     }
                     
                  } else {     // Terrace course - same for most mships


                     if (mship.equals("Emeritus")) {

                         item = "18TFUL";

                         if (parmp.p9 == 1) {                   // if 9 holes

                            item = "09TFUL";
                         }

                     } else {

                         item = "18TNON";

                         if (parmp.p9 == 1) {                   // if 9 holes

                            item = "09TNON";
                         }
                     }
                  }
               }       // end of IF St. Clair CC
               
   
            
               //
               //  If Royal Oaks Houston - process by mship and day
               //
               if (club.equals("royaloakscc")) {
                  
                  if (mship.equals( "Golf" ) || mship.equals( "Sampler Golf" ) || mship.equals( "Preview Golf" )) {
                  
                     if (parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                         parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3 ||
                         parmp.date == Hdate4 || parmp.date == Hdate5) {

                        item = "54GF03";          
                        
                        if (parmp.p9 == 1) {                   // if 9 holes
                        
                           item = "54GF04";  
                        }

                     } else {

                        if (parmp.day.equals( "Monday" ) || parmp.day.equals( "Tuesday" ) ||
                            parmp.day.equals( "Wednesday" ) || parmp.day.equals( "Thursday" )) {

                           item = "54GF21";          

                           if (parmp.p9 == 1) {                   // if 9 holes

                              item = "54GF22";  
                           }

                        } else {
                           
                           if (parmp.time < 1200) {          // Friday - AM or PM?

                              item = "54GF23";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "54GF24";  
                              }
                              
                           } else {
                              
                              item = "54GF25";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "54GF26";  
                              }
                           }
                        }
                     }                    
                  }
                  
                  if (mship.startsWith( "Executiv" ) || mship.endsWith( "Executive" )) {
                  
                     if (parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                         parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3 ||
                         parmp.date == Hdate4 || parmp.date == Hdate5) {

                        item = "54GF07";          
                        
                        if (parmp.p9 == 1) {                   // if 9 holes
                        
                           item = "54GF08";  
                        }

                     } else {

                        if (parmp.day.equals( "Monday" ) || parmp.day.equals( "Tuesday" ) ||
                            parmp.day.equals( "Wednesday" ) || parmp.day.equals( "Thursday" )) {

                           item = "54GF11";          

                           if (parmp.p9 == 1) {                   // if 9 holes

                              item = "54GF12";  
                           }

                        } else {

                           if (parmp.time < 1200) {          // Friday - AM or PM?

                              item = "54GF13";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "54GF14";  
                              }
                              
                           } else {
                              
                              item = "54GF15";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "54GF16";  
                              }
                           }
                        }
                     }                    
                  }
                  
                  if (mship.equals( "Honorary" )) {
                  
                     if (parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                         parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3 ||
                         parmp.date == Hdate4 || parmp.date == Hdate5) {

                        item = "55GF03";          
                        
                        if (parmp.p9 == 1) {                   // if 9 holes
                        
                           item = "55GF04";  
                        }

                     } else {

                        if (parmp.day.equals( "Monday" ) || parmp.day.equals( "Tuesday" ) ||
                            parmp.day.equals( "Wednesday" ) || parmp.day.equals( "Thursday" )) {

                           item = "55GF10";          

                           if (parmp.p9 == 1) {                   // if 9 holes

                              item = "55GF11";  
                           }

                        } else {

                           if (parmp.time < 1200) {          // Friday - AM or PM?

                              item = "55GF12";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "55GF13";  
                              }
                              
                           } else {
                              
                              item = "55GF14";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "55GF15";  
                              }
                           }
                        }
                     }                    
                  }
                  
                  if (mship.startsWith( "Sports Club" )) {
                  
                     if (parmp.day.equals( "Monday" ) || parmp.day.equals( "Tuesday" ) ||
                         parmp.day.equals( "Wednesday" ) || parmp.day.equals( "Thursday" )) {

                        item = "54GF17";          

                        if (parmp.p9 == 1) {                   // if 9 holes

                           item = "54GF18";  
                        }

                     } else {

                        if (parmp.day.equals( "Friday" ) && parmp.time < 1200) {          // Friday - AM or PM?

                           item = "54GF19";          

                           if (parmp.p9 == 1) {                   // if 9 holes

                              item = "54GF20";  
                           }

                        } else {

                           item = "49GF03";
                        }
                     }                    
                  }               
               }        // end of IF Royal Oaks CC
          
               
          
               if (!item.equals("")) {

                  //
                  //  We can now build the charge string
                  //
                  StringBuffer tempSB = new StringBuffer(mnum);     // put member # in string buffer
                  tempSB.append(",Y,1,");                           // indicator, # of guests (players?)
                  tempSB.append(lname);                             // last name
                  tempSB.append(",");
                  tempSB.append(fname);                             // first name
                  tempSB.append(",,");                              // skip phone #
                  tempSB.append(parmp.sdate);                       // date of tee time
                  tempSB.append(",");
                  tempSB.append(parmp.stime);                       // time (includes A or P for AM or PM and ,)
                  tempSB.append(parmp.courseid);                    // Course Code
                  tempSB.append(",,,,,,");                          // skips
                  tempSB.append(item);                              // Sales Item #
                  tempSB.append(",1,");                             // units sold
                  tempSB.append(resnum);                            // Reservation #
                  tempSB.append(",,");                              // unused
                  tempSB.append(ttidnum);                           // Tee Time Id #
                  tempSB.append(",,,N,,,Y");                        // units sold, skips, Prepaid, Checked-in

                  line = tempSB.toString();                         // save as string value

                  out.print(line);
                  out.println();      // output the line

                  done = 1;           // indicate charge sent

                  ttidn++;            // bump tee time id number
                  ttidnum++;
                 
                  //
                  //  Save charge data in pos_hist for reports
                  //
                  parmp.hist_posid = mnum;
                  parmp.hist_player = fname + " " + lname;   // use member name
                  parmp.hist_price = "";
                  parmp.hist_item_name = "Green Fee";
                  parmp.hist_item_num = item;

                  add_POS_hist(parmp, con);       // go make the entry 
               }
                 
            }      // end of Mship Charge processing

         } else {

            
            //
            //*******************************************************************************************
            //  player passed is a GUEST - charge the member for this too
            //*******************************************************************************************
            //
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                  gtype = parmp.gtype[i];               // set guest type description
                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.gst9I[i];                 // set guest item group #
                  } else {
                     item = parmp.gstI[i];                 // set guest item group #
                  }
                  break loop3;
               }
               i++;
            }

            //
            //  Custom item codes
            //
            if (club.equals( "medinahcc" )) {      // if Medinah do manual item codes

               if (parmp.course.equals( "No 1" )) {         // Course #1

                  if (parmp.player.startsWith( "Guest" )) {         // normal Guest

                     if (parmp.time > 1600) {

                        item = "120100";              // after 4 PM

                     } else {

                        if ((parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                            parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3) && parmp.time < 1100) {
//                            parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3) && parmp.time < 1200) {  // changed 6/05/07

                           item = "130100";           // S-S-H before noon

                        } else {

                           item = "110100";           // T-F and others
                        }
                     }
                  }

                  if (parmp.player.startsWith( "Unaccom" )) {       // Unaccompanied Guest

                     item = "140100";
                  }

                  if (parmp.player.startsWith( "Replay" )) {        // Replay Guest

                     item = "140400";
                  }
               }                             // end of course #1

               if (parmp.course.equals( "No 2" )) {         // Course #2

                  if (parmp.player.startsWith( "Guest" )) {         // normal Guest

                     if (parmp.time > 1600) {

                        item = "120200";              // after 4 PM

                     } else {

                        if ((parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                            parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3) && parmp.time < 1100) {
//                            parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3) && parmp.time < 1200) {  // changed 6/05/07

                           item = "130200";           // S-S-H before noon

                        } else {

                           item = "110200";           // T-F and others
                        }
                     }
                  }

                  if (parmp.player.startsWith( "Unaccom" )) {       // Unaccompanied Guest

                     item = "140200";
                  }

                  if (parmp.player.startsWith( "Replay" )) {        // Replay Guest

                     item = "140400";
                  }
               }                             // end of course #2

               if (parmp.course.equals( "No 3" )) {         // Course #3

                  if (parmp.player.startsWith( "Guest" )) {         // normal Guest
                     
                     //  changed on 10/16/07 as follows
                     //
                     //   1.  After 4:00 PM every day = 120300 CHANGE to After 2:00PM on Weekends and 
                     //       Holidays and after 4:00PM on Weekdays
                     //
                     //   2.  Before 11:00 AM on Sat, Sun and holidays = 130300- CHANGE to 130400
                     //
                     //   3.  If not 1 or 2, then = 110300 CORRECT
                      
                     item = "110300";             // default

                     if (parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                         parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3) {

                        if (parmp.time > 1400) {

                           item = "120300";              // after 2 PM on w/e & holidays
                        
                        } else {
                           
                           if (parmp.time < 1100) {

                              item = "130400";              // before 11 AM on w/e & holidays
                           }
                        }
                     
                     } else {      // Week Day
                     
                        if (parmp.time > 1600) {

                           item = "120300";              // after 4 PM on weekdays
                        }
                     }
                  }

                  if (parmp.player.startsWith( "Unaccom" )) {       // Unaccompanied Guest

                     item = "140300";
                  }
               }                             // end of course #3
            }

            //
            //  Custom item codes for Virginia CC
            //
            if (club.equals( "virginiacc" )) {      // if Virginia CC do manual item codes

               item = "";                           // init item code
                 
               if (parmp.course.equals( "James River" )) {         // James River Course

                  if (parmp.player.startsWith( "Weekday" )) {       

                     item = "FEEGFJRWD";
                  }
                  if (parmp.player.startsWith( "Weekend" )) {

                     item = "FEEGFJRWE";
                  }
                  if (parmp.player.startsWith( "WkDay Family" )) {

                     item = "FEEGFJRFWD";
                     
                     if (parmp.p9 == 1) {
                      
                        item = "FEEGXFWDF";
                     }
                  }
                  if (parmp.player.startsWith( "WkEnd Family" )) {

                     item = "FEEGFWEF";

                     if (parmp.p9 == 1) {
                      
                        item = "FEEGFJRWEF";
                     }
                  }
                  if (parmp.player.startsWith( "Junior WkDay" )) {

                     item = "FEEGFJRWDJ";
                  }
                  if (parmp.player.startsWith( "Junior WkEnd" )) {

                     item = "FEEGFJRWEJ";
                  }

                  if (!item.equals( "" )) {
                     
                     if (parmp.p9 == 1) {                   // if 9 holes

                        item = item + "9";

                     } else {

                        item = item + "18";
                     }
                  }                  

                  if (parmp.player.startsWith( "Employee" ) || parmp.player.startsWith( "Industry Comp" ) || 
                      parmp.player.startsWith( "VCU" ) || parmp.player.startsWith( "Collegiate" ) ||
                      parmp.player.startsWith( "U of R" ) || parmp.player.startsWith( "St Chris" ) ||
                      parmp.player.startsWith( "Reciprocal" ) || 
                      parmp.player.startsWith( "St Catherines" ) || parmp.player.startsWith( "Assoc Guest" )) {

                     item = "FEEGFJRCOMP";
                  }
               }                             // end of James River

               if (parmp.course.equals( "Tuckahoe Creek" )) {         // Tuckahoe Creek Course

                  if (parmp.player.startsWith( "Weekday" )) {

                     item = "GFTCWD";

                     if (parmp.p9 == 1) {
                      
                        item = "FEEGFTCWD";
                     }
                  }
                  if (parmp.player.startsWith( "Weekend" )) {

                     item = "FEEGFTCWE";
                  }
                  if (parmp.player.startsWith( "WkDay Family" )) {

                     item = "FEEGFTCWDF";
                  }
                  if (parmp.player.startsWith( "WkEnd Family" )) {

                     item = "FEEGFTCWEF";
                  }
                  if (parmp.player.startsWith( "Junior WkDay" )) {

                     item = "FEEGFTCWDJ";
                  }
                  if (parmp.player.startsWith( "Junior WkEnd" )) {

                     item = "FEEGFTCWEJ";
                  }


                  if (!item.equals( "" )) {
                     
                     if (parmp.p9 == 1) {                   // if 9 holes

                        item = item + "9";

                     } else {

                        item = item + "18";
                     }
                  }

                  if (parmp.player.startsWith( "Employee" ) || parmp.player.startsWith( "Industry Comp" ) ||
                      parmp.player.startsWith( "VCU" ) || parmp.player.startsWith( "Collegiate" ) ||
                      parmp.player.startsWith( "U of R" ) || parmp.player.startsWith( "St Chris" ) ||
                      parmp.player.startsWith( "Reciprocal" ) || 
                      parmp.player.startsWith( "St Catherines" ) || parmp.player.startsWith( "Assoc Guest" )) {

                     item = "FEEGFTCCOMP";
                  }
               }                             // end of Tuckahoe Creek

               if (parmp.course.equals( "Westhampton" )) {         // Westhampton Course

                  if (parmp.player.startsWith( "Weekday" )) {

                     item = "FEEGFWHWK";

                     if (parmp.p9 == 1) {
                      
                        item = "FEEGFWHWDAY";
                     }
                  }
                  if (parmp.player.startsWith( "Weekend" )) {

                     item = "FEEGFWHWE";
                  }
                  if (parmp.player.startsWith( "WkDay Family" )) {

                     item = "FEEGFWHWDF";
                  
                     if (parmp.p9 == 1) {
                      
                        item = "FEEGFWHWD";
                     }
                  }
                  if (parmp.player.startsWith( "WkEnd Family" )) {

                     item = "FEEGFWHWEF";
                  }
                  if (parmp.player.startsWith( "Junior WkDay" )) {

                     item = "FEEGFWHWDJ";
                  }
                  if (parmp.player.startsWith( "Junior WkEnd" )) {

                     item = "FEEGFWHWEJ";
                  }

                  if (!item.equals( "" )) {
                     
                     if (parmp.p9 == 1) {                   // if 9 holes

                        item = item + "9";

                     } else {

                        item = item + "18";
                     }
                  }

                  if (parmp.player.startsWith( "Employee" ) || parmp.player.startsWith( "Industry Comp" ) ||
                      parmp.player.startsWith( "VCU" ) || parmp.player.startsWith( "Collegiate" ) ||
                      parmp.player.startsWith( "U of R" ) || parmp.player.startsWith( "St Chris" ) ||
                      parmp.player.startsWith( "Reciprocal" ) || 
                      parmp.player.startsWith( "St Catherines" ) || parmp.player.startsWith( "Assoc Guest" )) {

                     item = "FEEGFWHCOMP";
                  }
               }                             // end of Westhampton Course
            }        // end of Virginia CC
            
            
            //
            //  St. Clair CC - custom guest codes based on course
            //
            if (club.equals( "stclaircc" )) {
            
               if (parmp.course.equals( "Championship" )) {         // Championship Course

                  if (parmp.player.startsWith( "Regular GST" ) || parmp.player.startsWith( "Unaccomp" ) ||
                      parmp.player.startsWith( "Reciprocal" )) {

                     item = "18MFUL";

                     if (parmp.p9 == 1) {
                      
                        item = "09MFUL";
                     }
                  }

                  if (parmp.player.startsWith( "Family GST" )) {

                     item = "18MPAR";

                     if (parmp.p9 == 1) {
                      
                        item = "09MPAR";
                     }
                  }

                  if (parmp.player.startsWith( "PGA" ) || parmp.player.startsWith( "Employees" ) || 
                      parmp.player.startsWith( "Comp" ) || parmp.player.startsWith( "High School Golf" ) ) {

                     item = "18MNON";

                     if (parmp.p9 == 1) {
                      
                        item = "09MNON";
                     }
                  }

                  
               } else {                
                  
                  
                  //
                  // Terrace Course
                  //
                  if (parmp.player.startsWith( "Regular GST" ) || parmp.player.startsWith( "Unaccomp" ) ||
                      parmp.player.startsWith( "Reciprocal" ) || parmp.player.startsWith( "Family GST" )) {

                     item = "18TFUL";

                     if (parmp.p9 == 1) {
                      
                        item = "09TFUL";
                     }
                  }

                  if (parmp.player.startsWith( "PGA" ) || parmp.player.startsWith( "Employees" ) || 
                      parmp.player.startsWith( "Comp" ) || parmp.player.startsWith( "High School Golf" ) ) {

                     item = "18TNON";

                     if (parmp.p9 == 1) {
                      
                        item = "09TNON";
                     }
                  }                  
               }                           
            }          // end of IF St. Clair CC
            


            //
            //  Continue if item code found
            //
            if (!item.equals( "" ) && !item.equals( "0" )) {   // if GUEST pos charge found

               //
               //  IF Congressional - check for custom codes based on Course
               //
               if (club.equals( "congressional" )) {

                  tcourse = congressionalCustom.getFullCourseName(parmp.date, daynum, parmp.course);   // get course name for this date

                  if (parmp.player.startsWith( "Local Guest" )) {

                     item = "000G42";                     // item code for Blue course

                     if (tcourse.endsWith( "Gold" )) {

                        item = "000G43";                 // item code for Gold course
                     }
                  }
                  if (parmp.player.startsWith( "Non Local Guest" )) {

                     item = "000G42";                     // item code for Blue course

                     if (tcourse.endsWith( "Gold" )) {

                        item = "000G43";                 // item code for Gold course
                     }
                  }
                  if (parmp.player.startsWith( "Blue Coupon" ) || parmp.player.startsWith( "Gold Coupon" )) {

                     item = "000G42";                     // item code for Blue course

                     if (tcourse.endsWith( "Gold" )) {

                        item = "000G43";                 // item code for Gold course
                     }
                  }
                  if (parmp.player.startsWith( "Unaccomp Guest" )) {

                     item = "000G52";                     // item code for Blue course

                     if (tcourse.endsWith( "Gold" )) {

                        item = "000G53";                 // item code for Gold course
                     }
                  }
                  if (parmp.player.startsWith( "Cert Jr" )) {

                     item = "000G45";                     // item code for Blue course

                     if (tcourse.endsWith( "Gold" )) {

                        item = "000G44";                 // item code for Gold course
                     }
                  }
               }


            
               //
               //  If Royal Oaks Houston - process by guest type and day
               //
               if (club.equals("royaloakscc")) {
                  
                  if (parmp.player.startsWith( "Guest" )) {
                  
                     if (parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                         parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3 ||
                         parmp.date == Hdate4 || parmp.date == Hdate5) {

                        item = "49GF03";          
                        
                        if (parmp.p9 == 1) {                   // if 9 holes
                        
                           item = "49GF04";  
                        }

                     } else {

                        if (parmp.day.equals( "Monday" ) || parmp.day.equals( "Tuesday" ) ||
                            parmp.day.equals( "Wednesday" ) || parmp.day.equals( "Thursday" )) {

                           item = "49GF15";          

                           if (parmp.p9 == 1) {                   // if 9 holes

                              item = "49GF16";  
                           }

                        } else {
                           
                           if (parmp.time < 1200) {          // Friday - AM or PM?

                              item = "49GF17";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "49GF18";  
                              }
                              
                           } else {
                              
                              item = "49GF19";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "49GF20";  
                              }
                           }
                        }
                     }                    
                  }
                  
                  if (parmp.player.startsWith( "Hon Guest" )) {
                  
                     if (parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                         parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3 ||
                         parmp.date == Hdate4 || parmp.date == Hdate5) {

                        item = "55GF16";          
                        
                        if (parmp.p9 == 1) {                   // if 9 holes
                        
                           item = "55GF17";  
                        }

                     } else {

                        if (parmp.day.equals( "Monday" ) || parmp.day.equals( "Tuesday" ) ||
                            parmp.day.equals( "Wednesday" ) || parmp.day.equals( "Thursday" )) {

                           item = "55GF18";          

                           if (parmp.p9 == 1) {                   // if 9 holes

                              item = "55GF19";  
                           }

                        } else {
                           
                           if (parmp.time < 1200) {          // Friday - AM or PM?

                              item = "55GF20";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "55GF21";  
                              }
                              
                           } else {
                              
                              item = "55GF22";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "55GF23";  
                              }
                           }
                        }
                     }                    
                  }
                  
                  if (parmp.player.startsWith( "Ext Family" )) {
                  
                     if (parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                         parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3 ||
                         parmp.date == Hdate4 || parmp.date == Hdate5) {

                        item = "49GF07";          
                        
                        if (parmp.p9 == 1) {                   // if 9 holes
                        
                           item = "49GF08";  
                        }

                     } else {

                        if (parmp.day.equals( "Monday" ) || parmp.day.equals( "Tuesday" ) ||
                            parmp.day.equals( "Wednesday" ) || parmp.day.equals( "Thursday" )) {

                           item = "49GF21";          

                           if (parmp.p9 == 1) {                   // if 9 holes

                              item = "49GF22";  
                           }

                        } else {
                           
                           if (parmp.time < 1200) {          // Friday - AM or PM?

                              item = "49GF23";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "49GF24";  
                              }
                              
                           } else {
                              
                              item = "49GF25";          

                              if (parmp.p9 == 1) {                   // if 9 holes

                                 item = "49GF26";  
                              }
                           }
                        }
                     }                    
                  }
                  
                  if (parmp.player.startsWith( "Unaccomp" )) {
                  
                     if (parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                         parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3 ||
                         parmp.date == Hdate4 || parmp.date == Hdate5) {

                        item = "49GF33";          
                        
                     } else {

                        if (parmp.day.equals( "Monday" ) || parmp.day.equals( "Tuesday" ) ||
                            parmp.day.equals( "Wednesday" ) || parmp.day.equals( "Thursday" )) {

                           item = "49GF27";          

                        } else {
                           
                           if (parmp.time < 1200) {          // Friday - AM or PM?

                              item = "49GF29";          

                           } else {
                              
                              item = "49GF31";          
                           }
                        }
                     }                    
                  }                  
               }      // end of IF Royal Oaks CC
               
                  
                  
                  
               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(mnum);     // put member # in string buffer
               tempSB.append(",Y,1,");                           // indicator, # of guests (players?)
               tempSB.append(lname);                             // last name
               tempSB.append(",");
               tempSB.append(fname);                             // first name
               tempSB.append(",,");                              // skip phone #
               tempSB.append(parmp.sdate);                       // date of tee time
               tempSB.append(",");
               tempSB.append(parmp.stime);                       // time (includes A or P for AM or PM and ,)
               tempSB.append(parmp.courseid);                    // Course Code
               tempSB.append(",,,,,,");                          // skips
               tempSB.append(item);                              // Sales Item #
               tempSB.append(",1,");                             // units sold
               tempSB.append(resnum);                            // Reservation #
               tempSB.append(",,");                              // unused
               tempSB.append(ttidnum);                           // Tee Time Id #
               tempSB.append(",,,N,,,Y");                        // units sold, skips, Prepaid, Checked-in

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent

               ttidn++;            // bump tee time id number
               ttidnum++;
                 
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = mnum;
               parmp.hist_player = parmp.player;          // if guest
               parmp.hist_price = "";
               parmp.hist_item_name = "Guest Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 
            }
         }     // end of guest processing

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in Proshop_sheet buildJonas for club: " +club;
         errorMsg2 = errorMsg2 + ", Exception: " + e2.getMessage();                 // build error msg

         SystemUtils.logError(errorMsg2);                                           // log it
      }
   }

   return(done);
 }                   // end of buildJonas


 // ********************************************************************
 //  Process the TAI POS charges for an individual member
 //
 //  Build an ASCII file containing the following:
 //
 //   Columns:
 //       1 Course Id
 //       2 Date of Tee Time (mm/dd/yyyy)
 //       3 Time of Tee Time (hh:mm)
 //       4 Member Id (mNum)
 //       5 Member Name (member responsible for the charge)
 //       6 Quantity (always 1)
 //       7 SKU Number (item charge code)
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public int buildTAI(parmPOS parmp, PrintWriter out, Connection con, String club) {


   ResultSet rs = null;

   String mship = "";
   String mnum = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String item = "";
   String line = "";

   int i = 0;
   int p9c = 0;
   int done = 0;


   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         mnum = rs.getString(4);
         posid = rs.getString(5);
      }
      pstmtc.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildTAI for club: " + club;
      errorMsg1 = errorMsg1 + ", Exception: " + e1.getMessage();      // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   //
   //  Skip if no mNum/posid - otherwise entire file will fail
   //
   if (!mnum.equals( "" )) {

      try {

         //
         //  First check if there is a charge amount associated with this member's mode of trans
         //
         i = 0;
         loop1:
         while (i < parmp.MAX_Tmodes) {

            if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

               tmode = parmp.tmode[i];             // get full description of tmode
               if (parmp.p9 == 1) {                   // if 9 holes
                  item = parmp.t9pos[i];               // get Item Group # for tmode
               } else {
                  item = parmp.tpos[i];               // get Item Group # for tmode
               }
               break loop1;
            }
            i++;
         }

         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(parmp.courseid);  // put Course Id in string buffer
            tempSB.append(",");
            tempSB.append(parmp.sdate);                              // date of tee time
            tempSB.append(",");
            tempSB.append(parmp.stime);                              // time (includes A or P for AM or PM and ,)
            tempSB.append(",");
            tempSB.append(mnum);                                     // member number
            tempSB.append(",");
            tempSB.append(fname);                                    // first name
            tempSB.append(" ");                                      // space
            tempSB.append(lname);                                    // last name
            tempSB.append(",1,");                                    // quantity
            tempSB.append(item);                                     // Sales Item Code
              
            line = tempSB.toString();                         // save as string value

            out.print(line);
            out.println();      // output the line

            done = 1;           // indicate charge sent
                 
            //
            //  Save charge data in pos_hist for reports
            //
            parmp.hist_posid = mnum;
            if (!parmp.player.equals( "" )) {
               parmp.hist_player = parmp.player;          // if guest
            } else {
               parmp.hist_player = fname + " " + lname;   // else use member name
            }
            parmp.hist_price = "";
            parmp.hist_item_name = parmp.pcw;
            parmp.hist_item_num = item;

            add_POS_hist(parmp, con);       // go make the entry 
         }

         //
         //  get the mship class and charge amount, if any and if player is a member!
         //
         if (parmp.player.equals( "" )) {    // if member
           
            i = 0;
            item = "";
            loop2:
            while (i < parmp.MAX_Mships) {

               if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.mship9I[i];               // get mship item group #
                  } else {
                     item = parmp.mshipI[i];               // get mship item group #
                  }
                  break loop2;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(parmp.courseid);  // put Course Id in string buffer
               tempSB.append(",");
               tempSB.append(parmp.sdate);                              // date of tee time
               tempSB.append(",");
               tempSB.append(parmp.stime);                              // time (includes A or P for AM or PM and ,)
               tempSB.append(",");
               tempSB.append(mnum);                                     // member number
               tempSB.append(",");
               tempSB.append(fname);                                    // first name
               tempSB.append(" ");                                      // space
               tempSB.append(lname);                                    // last name
               tempSB.append(",1,");                                    // quantity
               tempSB.append(item);                                     // Sales Item Code

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
                 
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = mnum;
               parmp.hist_player = fname + " " + lname;   // use member name
               parmp.hist_price = "";
               parmp.hist_item_name = "Green Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 
                 
            }      // end of Mship Charge processing

         } else {

            //
            //  player passed is a guest - charge the member for this too
            //
            //
            //  First check if there is a charge amount associated with this guest type
            //
            i = 0;
            item = "";
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                  gtype = parmp.gtype[i];               // set guest type description
                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.gst9I[i];                 // set guest item group #
                  } else {
                     item = parmp.gstI[i];                 // set guest item group #
                  }
                  break loop3;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(parmp.courseid);  // put Course Id in string buffer
               tempSB.append(",");
               tempSB.append(parmp.sdate);                              // date of tee time
               tempSB.append(",");
               tempSB.append(parmp.stime);                              // time (includes A or P for AM or PM and ,)
               tempSB.append(",");
               tempSB.append(mnum);                                     // member number
               tempSB.append(",");
               tempSB.append(fname);                                    // first name
               tempSB.append(" ");                                      // space
               tempSB.append(lname);                                    // last name
               tempSB.append(",1,");                                    // quantity
               tempSB.append(item);                                     // Sales Item Code

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
                 
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = mnum;
               parmp.hist_player = parmp.player;          // if guest
               parmp.hist_price = "";
               parmp.hist_item_name = "Guest Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 
            }
         }     // end of guest processing

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in Proshop_sheet buildTAI for club: " +club;
         errorMsg2 = errorMsg2 + ", Exception: " + e2.getMessage();                 // build error msg

         SystemUtils.logError(errorMsg2);                                           // log it
      }
   }

   return(done);
 }                   // end of buildTAI


 // ********************************************************************
 //  Process the IBS POS charges for an individual member
 //
 //  Build an ASCII file containing the following:
 //
 //   Columns:
 //       1 Member Number
 //       2 Extension (000, 001, 002, etc. - 000 is default)
 //       3 Date of Tee Time (mm/dd/yyyy)
 //       4 Batch Code (Item Code)
 //       5 Fee (charge amount for item - dd.cc)
 //       6 Sales Tax amount
 //       7 Item Description
 //       8 Ticket # (we use the time)
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public int buildIBS(parmPOS parmp, PrintWriter out, Connection con, String club) {


   ResultSet rs = null;

   String mship = "";
   String mnum = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String tpos = "";
   String tposc = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String item = "";
   String cost = "";
   String desc = "";
   String line = "";
   String ext = "";
   String temp = "";
   String stime = "";

   int i = 0;
   int p9c = 0;
   int done = 0;
     
   double fee = 0;
   double salestax = 0;

   long sdate = parmp.date - ((parmp.date / 10000) * 10000);       // get mmdd (short date)
     

   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         mnum = rs.getString(4);
         posid = rs.getString(5);
      }
      pstmtc.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildIBS for club: " + club;
      errorMsg1 = errorMsg1 + ", Exception: " + e1.getMessage();      // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   //
   //  Skip if no mNum/posid - otherwise entire file will fail
   //
   if (!mnum.equals( "" )) {

      try {

         //
         //  First get the member's pos extension id, if available (i.e.  2740-001)
         //
         ext = "000";        // default extension
           
         if (!posid.equals( "" )) {       // if posid exists
           
            StringTokenizer tok = new StringTokenizer( posid, "-" );     // delimiters are comma

            temp = tok.nextToken();          // skip mnum

            if ( tok.countTokens() > 0 ) {

               ext = tok.nextToken();        // get ext if present
            }
         }

         //
         //  Get the tee time in string format
         //
         stime = String.valueOf( parmp.time );       // get tee time in military format


         //
         //  Check if there is a charge amount associated with this member's mode of trans
         //
         i = 0;
         loop1:
         while (i < parmp.MAX_Tmodes) {

            if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

               desc = parmp.tmode[i];                  // get full description of tmode
               if (parmp.p9 == 1) {                    // if 9 holes
                  item = parmp.t9pos[i];               // get Item Group # for tmode
                  cost = parmp.t9posc[i];              // get Item Fee for tmode
               } else {
                  item = parmp.tpos[i];                // get Item Group # for tmode
                  cost = parmp.tposc[i];               // get Item Fee for tmode
               }
               break loop1;
            }
            i++;
         }

         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            if (parmp.salestax != 0 && !cost.equals( "0" )) {   // if sales tax required

               fee = Double.parseDouble(cost);       // convert the item cost
                 
               salestax = (fee * parmp.salestax);  // calculate the sales tax amount

               salestax = roundST(salestax);       // round to dd.cc value
            }

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(mnum);            // put Member Id in string buffer
            tempSB.append(",");
            tempSB.append(ext);                                      // member number extension (000, 001, etc.)
            tempSB.append(",");
            tempSB.append(parmp.sdate);                              // date of tee time
            tempSB.append(",");
            tempSB.append(item);                                     // Sales Item Code (Batch Code in IBS terms)
            tempSB.append(",");
            tempSB.append(cost);                                     // Fee  (dd.cc)
            tempSB.append(",");
            tempSB.append(salestax);                                 // Sales Tax amount (dd.cc)
            tempSB.append(",");
            tempSB.append(desc);                                     // Item description
            tempSB.append(",");
            tempSB.append(stime);                                    // Ticket # (tee time)

            line = tempSB.toString();                         // save as string value

            out.print(line);
            out.println();      // output the line

            done = 1;           // indicate charge sent
                 
            //
            //  Save charge data in pos_hist for reports
            //
            parmp.hist_posid = mnum;
            if (!parmp.player.equals( "" )) {
               parmp.hist_player = parmp.player;          // use guest name
            } else {
               parmp.hist_player = fname + " " + lname;   // use member name
            }
            parmp.hist_price = "$" + cost;
            parmp.hist_item_name = parmp.pcw;
            parmp.hist_item_num = item;

            add_POS_hist(parmp, con);       // go make the entry 
         }

         //
         //  get the mship class and charge amount, if any and if player is a member!
         //
         if (parmp.player.equals( "" )) {    // if member

            i = 0;
            item = "";
            loop2:
            while (i < parmp.MAX_Mships) {

               if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                  desc = "Greens Fee for Membership Classification";       // item description

                  if (parmp.p9 == 1) {                      // if 9 holes
                     item = parmp.mship9I[i];               // get mship item group #
                     cost = parmp.m9posc[i];                // get Item Fee 
                  } else {
                     item = parmp.mshipI[i];                // get mship item group #
                     cost = parmp.mposc[i];                 // get Item Fee
                  }
                  break loop2;
               }
               i++;
            }

            /*
            //
            //  Sonnenalp decided not to use the IBS interface since they do so many public rounds and it isn't a good fit. 
            //
            if (club.equals( "sonnenalp" ) && item.equals( "140" )) {   // if Sonnenalp and mship type = Employee Hotel

               //
               //  Sonnenalp Hotel Employee - determine batch code and fee based on time of year and time of day
               //
               //    Low Season  = 4/01 - 6/14 and 9/01 - 10/28
               //    High Season = 6/15 - 8/31 
               //
               //    Twilight = after 3:30
               //
               if (sdate > 614 && sdate < 901) {       // if High Season 
                 
                  if (parmp.time > 1530) {             // if twilight 
                    
                     desc = "Greens Fee for Hotel Employee - High Twilight";       // item description

                     item = "142";                                                  // batch code
                       
                     cost = "25.00";
                       
                     if (parmp.p9 == 1) {             // if 9 hole round
                       
                        cost = "12.50";
                     }
  
                  } else {                  
                    
                     desc = "Greens Fee for Hotel Employee - High Season";       // item description

                     item = "141";                                                  // batch code

                     cost = "25.00";

                     if (parmp.p9 == 1) {             // if 9 hole round

                        cost = "12.50";
                     }
                  }
  
               } else {    // Low Season
                 
                  if (parmp.time > 1530) {             // if twilight

                     desc = "Greens Fee for Hotel Employee - Low Twilight";       // item description

                     item = "144";                                                  // batch code

                     cost = "12.00";

                     if (parmp.p9 == 1) {             // if 9 hole round

                        cost = "6.00";
                     }
                       
                  } else {

                     desc = "Greens Fee for Hotel Employee - Low Season";       // item description

                     item = "143";                                                  // batch code

                     cost = "25.00";

                     if (parmp.p9 == 1) {             // if 9 hole round

                        cost = "12.50";
                     }
                  }
               }
            }
            */
              

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               if (parmp.salestax != 0 && !cost.equals( "0" )) {   // if sales tax required

                  fee = Double.parseDouble(cost);       // convert the item cost

                  salestax = (fee * parmp.salestax);  // calculate the sales tax amount

                  salestax = roundST(salestax);       // round to dd.cc value
               }

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(mnum);            // put Member Id in string buffer
               tempSB.append(",");
               tempSB.append(ext);                                      // member number extension (000, 001, etc.)
               tempSB.append(",");
               tempSB.append(parmp.sdate);                              // date of tee time
               tempSB.append(",");
               tempSB.append(item);                                     // Sales Item Code (Batch Code in IBS terms)
               tempSB.append(",");
               tempSB.append(cost);                                     // Fee  (dd.cc)
               tempSB.append(",");
               tempSB.append(salestax);                                 // Sales Tax amount (dd.cc)
               tempSB.append(",");
               tempSB.append(desc);                                     // Item description
               tempSB.append(",");
               tempSB.append(stime);                                    // Ticket # (tee time)

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
                 
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = mnum;
               parmp.hist_player = fname + " " + lname;   // use member name
               parmp.hist_price = "$" + cost;
               parmp.hist_item_name = "Green Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 

            }      // end of Mship Charge processing

         } else {

            //
            //  player passed is a guest - charge the member for this too
            //
            //
            //  First check if there is a charge amount associated with this guest type
            //
            i = 0;
            item = "";
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                  desc = parmp.gtype[i];                   // set guest type description

                  if (parmp.p9 == 1) {                      // if 9 holes
                     item = parmp.gst9I[i];                 // set guest item group #
                     cost = parmp.gpos[i];                  // get Item Fee
                  } else {
                     item = parmp.gstI[i];                  // set guest item group #
                     cost = parmp.gpos[i];                  // get Item Fee
                  }
                  break loop3;
               }
               i++;
            }

            /*
            //
            //  Sonnenalp decided not to use the IBS interface since they do so many public rounds and it isn't a good fit.
            //
            if (club.equals( "sonnenalp" ) && !item.equals( "" )) {    // if Sonnenalp and batch code foound for guest type

               //
               //  Sonnenalp Guests - determine batch code and fee based on time of year and time of day
               //
               //    Low Season  = 4/01 - 6/14 and 9/01 - 10/28
               //    High Season = 6/15 - 8/31
               //
               //    Twilight = after 3:30
               //
               if (item.equals( "101" )) {            // if guest type = Hotel 

                  if (sdate > 614 && sdate < 901) {       // if High Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Hotel Guest - High Twilight";       // item description

                        item = "107";                                                  // batch code

                        cost = "50.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "25.00";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Hotel Guest - High 18";       // item description

                           item = "108";                                                  // batch code

                           cost = "125.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "62.50";
                           }
                             
                        } else {         // Mid Day (1:30 - 3:20)
                          
                           desc = "Greens Fee for Hotel Guest - High Mid Day";       // item description

                           item = "171";                                                  // batch code

                           cost = "100.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "50.00";
                           }
                        } 
                     }

                  } else {    // Low Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Hotel Guest - Low Twilight";       // item description

                        item = "103";                                                  // batch code

                        cost = "35.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "17.50";
                        }

                     } else {        // NOT twilight  

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Hotel Guest - Low 18";       // item description

                           item = "101";                                                  // batch code

                           cost = "85.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "42.50";
                           }

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Hotel Guest - Low Mid Day";       // item description

                           item = "170";                                                  // batch code

                           cost = "60.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "30.00";
                           }
                        }
                     }
                  }
               }     // end of Hotel Guest
                 
               //
               //  Escorted Guest
               //
               if (item.equals( "114" )) {            // if guest type = Escorted Guest

                  if (sdate > 614 && sdate < 901) {       // if High Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Escorted Guest - High Twilight";       // item description

                        item = "117";                                                  // batch code

                        cost = "50.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "25.00";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Escorted Guest - High 18";       // item description

                           item = "116";                                                  // batch code

                           cost = "100.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "50.00";
                           }

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Escorted Guest - High Mid Day";       // item description

                           item = "163";                                                  // batch code

                           cost = "75.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "37.50";
                           }
                        }
                     }

                  } else {    // Low Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Escorted Guest - Low Twilight";       // item description

                        item = "115";                                                  // batch code

                        cost = "25.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "12.50";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Escorted Guest - Low 18";       // item description

                           item = "114";                                                  // batch code

                           cost = "75.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "37.50";
                           }

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Escorted Guest - Low Mid Day";       // item description

                           item = "162";                                                  // batch code

                           cost = "50.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "25.00";
                           }
                        }
                     }
                  }
               }     // end of Escorted Guest

               //
               //  Unescorted Guest
               //
               if (item.equals( "122" )) {            // if guest type = Unescorted Guest

                  if (sdate > 614 && sdate < 901) {       // if High Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Unescorted Guest - High Twilight";       // item description

                        item = "125";                                                  // batch code

                        cost = "50.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "25.00";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Unescorted Guest - High 18";       // item description

                           item = "124";                                                  // batch code

                           cost = "125.00";          // no 9 hole fee

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Unescorted Guest - High Mid Day";       // item description

                           item = "165";                                                  // batch code

                           cost = "100.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "50.00";
                           }
                        }
                     }

                  } else {    // Low Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Unescorted Guest - Low Twilight";       // item description

                        item = "123";                                                  // batch code

                        cost = "35.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "17.50";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Unescorted Guest - Low 18";       // item description

                           item = "122";                                                  // batch code

                           cost = "85.00";        // no 9 hole fee

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Unescorted Guest - Low Mid Day";       // item description

                           item = "164";                                                  // batch code

                           cost = "60.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "30.00";
                           }
                        }
                     }
                  }
               }     // end of Unescorted Guest

               //
               //  Public Guest
               //
               if (item.equals( "126" )) {            // if guest type = Public Guest

                  if (sdate > 614 && sdate < 901) {       // if High Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Public Guest - High Twilight";       // item description

                        item = "129";                                                  // batch code

                        cost = "75.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "37.50";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Public Guest - High 18";       // item description

                           item = "128";                                                  // batch code

                           cost = "175.00";          // no 9 hole fee

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Public Guest - High Mid Day";       // item description

                           item = "169";                                                  // batch code

                           cost = "125.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "62.50";
                           }
                        }
                     }

                  } else {    // Low Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Public Guest - Low Twilight";       // item description

                        item = "127";                                                  // batch code

                        cost = "50.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "25.00";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Public Guest - Low 18";       // item description

                           item = "126";                                                  // batch code

                           cost = "100.00";        // no 9 hole fee

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Public Guest - Low Mid Day";       // item description

                           item = "168";                                                  // batch code

                           cost = "75.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "37.50";
                           }
                        }
                     }
                  }
               }     // end of Public Guest

               //
               //  Property Owner Guest
               //
               if (item.equals( "148" )) {            // if guest type = Property Owner Guest

                  if (sdate > 614 && sdate < 901) {       // if High Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Property Owner Guest - High Twilight";       // item description

                        item = "151";                                                  // batch code

                        cost = "50.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "25.00";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Property Owner Guest - High 18";       // item description

                           item = "149";                                                  // batch code

                           cost = "125.00";          // no 9 hole fee

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Property Owner Guest - High Mid Day";       // item description

                           item = "167";                                                  // batch code

                           cost = "100.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "50.00";
                           }
                        }
                     }

                  } else {    // Low Season

                     if (parmp.time > 1530) {             // if twilight

                        desc = "Greens Fee for Property Owner Guest - Low Twilight";       // item description

                        item = "150";                                                  // batch code

                        cost = "35.00";

                        if (parmp.p9 == 1) {             // if 9 hole round

                           cost = "17.50";
                        }

                     } else {        // NOT twilight

                        if (parmp.time < 1330) {             // if normal time (before 1:30)

                           desc = "Greens Fee for Property Owner Guest - Low 18";       // item description

                           item = "148";                                                  // batch code

                           cost = "85.00";        // no 9 hole fee

                        } else {         // Mid Day (1:30 - 3:20)

                           desc = "Greens Fee for Property Owner Guest - Low Mid Day";       // item description

                           item = "166";                                                  // batch code

                           cost = "60.00";

                           if (parmp.p9 == 1) {             // if 9 hole round

                              cost = "30.00";
                           }
                        }
                     }
                  }
               }     // end of Property Owner Guest

            }     // end of IF sonnenalp
            */


            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

               if (parmp.salestax != 0 && !cost.equals( "0" )) {   // if sales tax required

                  fee = Double.parseDouble(cost);       // convert the item cost

                  salestax = (fee * parmp.salestax);  // calculate the sales tax amount

                  salestax = roundST(salestax);       // round to dd.cc value
               }

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(mnum);            // put Member Id in string buffer
               tempSB.append(",");
               tempSB.append(ext);                                      // member number extension (000, 001, etc.)
               tempSB.append(",");
               tempSB.append(parmp.sdate);                              // date of tee time
               tempSB.append(",");
               tempSB.append(item);                                     // Sales Item Code (Batch Code in IBS terms)
               tempSB.append(",");
               tempSB.append(cost);                                     // Fee  (dd.cc)
               tempSB.append(",");
               tempSB.append(salestax);                                 // Sales Tax amount (dd.cc)
               tempSB.append(",");
               tempSB.append(desc);                                     // Item description
               tempSB.append(",");
               tempSB.append(stime);                                    // Ticket # (tee time)

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
                 
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = mnum;
               parmp.hist_player = parmp.player;          // use guest name
               parmp.hist_price = "$" + cost;
               parmp.hist_item_name = "Guest Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 
            }
         }     // end of guest processing

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in Proshop_sheet buildIBS for club: " +club;
         errorMsg2 = errorMsg2 + ", Exception: " + e2.getMessage();                 // build error msg

         SystemUtils.logError(errorMsg2);                                           // log it
      }
   }

   return(done);
 }                   // end of buildIBS


 // ********************************************************************
 //  Process the ClubSoft POS charges for an individual member
 //
 //  Build an ASCII file containing the following:
 //
 //   Columns:
 //       1 Member Number
 //       2 Date of Tee Time (mm/dd/yyyy)
 //       3 Time of Tee Time (hh:mm)
 //       4 Batch Code (Item Code)
 //       5 Quantity (always 1)
 //
 //   File Name = ForeTeesnn.CSV (nn = 01, 02, 03, etc)
 //
 // ********************************************************************

 public int buildClubSoft(parmPOS parmp, PrintWriter out, Connection con, String club) {


   ResultSet rs = null;

   String mship = "";
   String mnum = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String tpos = "";
   String tposc = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String item = "";
   String line = "";
   String temp = "";

   int i = 0;
   int p9c = 0;
   int done = 0;
     
   long sdate = parmp.date - ((parmp.date / 10000) * 10000);       // get mmdd (short date)
     

   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         mnum = rs.getString(4);       // get both mNum & posid in case we need to use the other
         posid = rs.getString(5);
      }
      pstmtc.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildClubSoft for club: " + club;
      errorMsg1 = errorMsg1 + ", Exception: " + e1.getMessage();      // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }
   
   if (posid.equals("")) {        // if POSID not present
      
      posid = mnum;               // use mNum
   }

   //
   //  Skip if no mNum/posid - otherwise entire file will fail
   //
   if (!posid.equals( "" )) {

      try {

         //
         //  Check if there is a charge amount associated with this member's mode of trans
         //
         i = 0;
         loop1:
         while (i < parmp.MAX_Tmodes) {

            if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

               if (parmp.p9 == 1) {                    // if 9 holes
                  item = parmp.t9pos[i];               // get Item Group # for tmode
               } else {
                  item = parmp.tpos[i];                // get Item Group # for tmode
               }
               break loop1;
            }
            i++;
         }

         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(posid);           // put Member Id in string buffer
            tempSB.append(",");
            tempSB.append(parmp.sdate);                              // date of tee time (mm/dd/yyyy)
            tempSB.append(",");
            tempSB.append(parmp.stime);                              // time of tee time (hh:mm)
            tempSB.append(",");
            tempSB.append(item);                                     // Sales Item Code
            tempSB.append(",1");                                     // quantity

            line = tempSB.toString();                         // save as string value

            out.print(line);
            out.println();      // output the line

            done = 1;           // indicate charge sent
                 
            //
            //  Save charge data in pos_hist for reports
            //
            parmp.hist_posid = posid;
            if (!parmp.player.equals( "" )) {
               parmp.hist_player = parmp.player;          // use guest name
            } else {
               parmp.hist_player = fname + " " + lname;   // use member name
            }
            parmp.hist_price = "";
            parmp.hist_item_name = parmp.pcw;
            parmp.hist_item_num = item;

            add_POS_hist(parmp, con);       // go make the entry 
         }

         //
         //  get the mship class and charge amount, if any and if player is a member!
         //
         if (parmp.player.equals( "" )) {    // if member

            i = 0;
            item = "";
            loop2:
            while (i < parmp.MAX_Mships) {

               if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                  if (parmp.p9 == 1) {                      // if 9 holes
                     item = parmp.mship9I[i];               // get mship item group #
                  } else {
                     item = parmp.mshipI[i];                // get mship item group #
                  }
                  break loop2;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(posid);            // put Member Id in string buffer
               tempSB.append(",");
               tempSB.append(parmp.sdate);                              // date of tee time (mm/dd/yyyy)
               tempSB.append(",");
               tempSB.append(parmp.stime);                              // time of tee time (hh:mm)
               tempSB.append(",");
               tempSB.append(item);                                     // Sales Item Code
               tempSB.append(",1");                                     // quantity

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
                 
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = posid;
               parmp.hist_player = fname + " " + lname;   // use member name
               parmp.hist_price = "";
               parmp.hist_item_name = "Green Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 

            }      // end of Mship Charge processing

         } else {

            //
            //  player passed is a guest - charge the member for this too
            //
            //
            //  First check if there is a charge amount associated with this guest type
            //
            i = 0;
            item = "";
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                  if (parmp.p9 == 1) {                      // if 9 holes
                     item = parmp.gst9I[i];                 // set guest item group #
                  } else {
                     item = parmp.gstI[i];                  // set guest item group #
                  }
                  break loop3;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(posid);            // put Member Id in string buffer
               tempSB.append(",");
               tempSB.append(parmp.sdate);                              // date of tee time (mm/dd/yyyy)
               tempSB.append(",");
               tempSB.append(parmp.stime);                              // time of tee time (hh:mm)
               tempSB.append(",");
               tempSB.append(item);                                     // Sales Item Code
               tempSB.append(",1");                                     // quantity

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
                 
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = posid;
               parmp.hist_player = parmp.player;          // use guest name
               parmp.hist_price = "";
               parmp.hist_item_name = "Guest Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 
            }
         }     // end of guest processing

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in Proshop_sheet buildClubSoft for club: " +club;
         errorMsg2 = errorMsg2 + ", Exception: " + e2.getMessage();                 // build error msg

         SystemUtils.logError(errorMsg2);                                           // log it
      }
   }

   return(done);
 }                   // end of buildClubSoft


 // ********************************************************************
 //  Process the CSG POS charges for an individual member
 //
 //  Build an ASCII file (.dat) containing the following:
 //
 //   Columns:
 //       1 Reservation ID                                        Text 27
 //       2 TTID                                                  Num  10
 //       3 Course ID                                             Text  4
 //       4 Course Name                                           Text 30
 //       5 Reservation Date (mm/dd/yyyy)                         Date 10
 //       6 Reservation Time (hh:mm:ss AM/PM)                     Time 11   
 //       7 Member Number                                         Text 10
 //       8 Player First Name                                     Text 15
 //       9 Player Mid Name                                       Text  1
 //       10 Player Last Name                                     Text 25
 //       11 Item Name (leave blank - spaces)                     Text 30
 //       12 Item SKU                                             Text 12
 //       13 Item Price (leave blank - spaces)                    Text 10
 //       14 Player Status (1 = member, 2 = guest)                Text  1
 //       15 Address (not used - spaces)                          Text 50
 //       16 City (not used - spaces)                             Text 15
 //       17 State (not used - spaces)                            Text  2
 //       18 Zip Code (not used - spaces)                         Text 10
 //       19 Email Address (not used - spaces)                    Text 40
 //       20 Instruction (not used - spaces)                      Text 30
 //       21 CR LF                                                Text  2
 //
 //    Check for charges and build the record
 // ********************************************************************

 public int buildCSG(parmPOS parmp, PrintWriter out, Connection con, String club, int ttid) {


   ResultSet rs = null;

   String mship = "";
   String mnum = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String mi = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String line = "";
   String item = "";
   String itemName = "                              ";        // 30 spaces
   String itemPrice = "          ";                           // 10 spaces
   String address = "                                                  "; // 50 spaces
   String city = "               ";                           // 15 spaces
   String state = "  ";                                       //  2 spaces
   String zip = "          ";                                 // 10 spaces
   String email = "                                        "; // 40 spaces
   String instruct = "                              ";        // 30 spaces
   String courseid = parmp.courseid;
   String course = parmp.course;
   String resid = parmp.sdate+ " " +parmp.stime+ " ";       // 23 chars counting the spaces

   int i = 0;
   int p9c = 0;
   int done = 0;

   //
   //  Build the reservation id field
   //
   int len = parmp.courseid.length();        // get length of course id     
  
   if (len < 5) {                            // if 4 or less characters
     
      resid = resid + parmp.courseid;        // combine
   }
        
   len = resid.length();                     // get length of resid now

   while (len < 27) {

      resid = resid + " ";                  // add a space filler
      len++;
   }
        
   //
   //  Build the course id field (4 chars)
   //
   len = courseid.length();                   // get length of course id

   if (len < 4) {                            // if < 4 characters

      while (len < 4) {

         courseid = courseid + " ";         // add a space filler
         len++;
      }
        
   } else {

      if (len > 4) {                            // if > 4 characters

         while (len > 4) {

            courseid = stripOne(courseid);     // strip the first char from id
            len--;
         }
      }
   }

   //
   //  Get course name (30 chars) - can't be more than 30 since we define it as 30
   //
   len = course.length();                   // get length of course name

   if (len < 30) {                            // if < 4 characters

      while (len < 30) {

         course = course + " ";         // add a space filler
         len++;
      }
   }


   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, name_mi, m_ship, memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mi = rs.getString(3);
         mship = rs.getString(4);
         mnum = rs.getString(5);
         posid = rs.getString(6);
      }
      pstmtc.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildCSG for club: " + club;
      errorMsg1 = errorMsg1 + ", Exception: " + e1.getMessage();      // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   //
   //  Skip if no posid - otherwise entire file will fail
   //
   if (!posid.equals( "" )) {

      //
      //  Build the POS ID field (10 chars)
      //
      len = posid.length();                   // get length of pos id

      if (len < 10) {                         // if < 10 characters

         while (len < 10) {

            posid = posid + " ";             // add a space filler
            len++;
         }

      } else {

         if (len > 10) {                         // if > 10 characters

            while (len > 10) {

               posid = stripOne(posid);         // strip the first char from id
               len--;
            }
         }
      }

      //
      //  Build the Name fields (15 & 25 chars)
      //
      len = fname.length();                   // get length of first name

      if (len < 15) {                         // if < 15 characters

         while (len < 15) {

            fname = fname + " ";             // add a space filler
            len++;
         }

      } else {

         if (len > 15) {                         // if > 15 characters

            while (len > 15) {

               fname = stripOne(fname);         // strip the first char
               len--;
            }
         }
      }

      len = lname.length();                   // get length of last name

      if (len < 25) {                         // if < 25 characters (will be - we max at 20)

         while (len < 25) {

            lname = lname + " ";             // add a space filler
            len++;
         }
      }

      if (mi.equals( "" )) {                // if mi not provided
        
         mi = " ";
      }


      try {

         //
         //  First check if there is a charge amount associated with this player's mode of trans
         //
         i = 0;
         loop1:
         while (i < parmp.MAX_Tmodes) {

            if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

               tmode = parmp.tmode[i];             // get full description of tmode
               if (parmp.p9 == 1) {                   // if 9 holes
                  item = parmp.t9pos[i];               // get Item Group # for tmode
               } else {
                  item = parmp.tpos[i];               // get Item Group # for tmode
               }
               break loop1;
            }
            i++;
         }
           
         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            //
            //  Build the Item/SKU field (12 chars)
            //
            len = item.length();                   // get length of item code

            if (len < 12) {                         // if < 12 characters

               while (len < 12) {

                  item = item + " ";             // add a space filler
                  len++;
               }

            } else {

               if (len > 12) {                         // if > 12 characters

                  while (len > 12) {

                     item = stripOne(item);         // strip the first char
                     len--;
                  }
               }
            }

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(resid);    // put reservation id in string buffer
            tempSB.append(ttid);                              // player id
            tempSB.append(courseid);                          // course id
            tempSB.append(course);                            // course name
            tempSB.append(parmp.sdate);                       // date - mm/dd/yyyy
            tempSB.append(parmp.stime);                       // tee time - hh:mm:ss AM (or PM)
            tempSB.append(posid);                             // POS ID
            tempSB.append(fname);                             // first name
            tempSB.append(mi);                                // mi
            tempSB.append(lname);                             // last name
            tempSB.append(itemName);                          // blank
            tempSB.append(item);                              // item code (SKU)
            tempSB.append(itemPrice);                         // blank
            tempSB.append("2");                               // player status (1 = main member, 2 = reg member or guest)
            tempSB.append(address);                           // blank
            tempSB.append(city);                              // blank
            tempSB.append(state);                             // blank
            tempSB.append(zip);                               // blank
            tempSB.append(email);                             // blank
            tempSB.append(instruct);                          // blank
            tempSB.append("\r\n");                            // EOF = CR LF

            line = tempSB.toString();                         // save as string value

            out.print(line);
            out.println();      // output the line

            done = 1;           // indicate charge sent
         
            //
            //  Save charge data in pos_hist for reports
            //
            parmp.hist_posid = posid;
            if (parmp.player.equals("")) {                // if player is a member
               parmp.hist_player = fname + " " + lname;
            } else {                                      // or guest
               parmp.hist_player = parmp.player;
            }
            parmp.hist_price = "";
            parmp.hist_item_name = parmp.pcw;
            parmp.hist_item_num = item;

            add_POS_hist(parmp, con);       // go make the entry      
         }

         //
         //  get the mship class and charge amount, if any and if player is a member!
         //
         if (parmp.player.equals( "" )) {      // if member
           
            i = 0;
            item = "";
            loop2:
            while (i < parmp.MAX_Mships) {

               if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.mship9I[i];               // get mship item group #
                  } else {
                     item = parmp.mshipI[i];               // get mship item group #
                  }
                  break loop2;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               //
               //  Build the Item/SKU field (12 chars)
               //
               len = item.length();                   // get length of item code

               if (len < 12) {                         // if < 12 characters

                  while (len < 12) {

                     item = item + " ";             // add a space filler
                     len++;
                  }

               } else {

                  if (len > 12) {                         // if > 12 characters

                     while (len > 12) {

                        item = stripOne(item);         // strip the first char
                        len--;
                     }
                  }
               }

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(resid);    // put reservation id in string buffer
               tempSB.append(ttid);                              // player id
               tempSB.append(courseid);                          // course id
               tempSB.append(course);                            // course name
               tempSB.append(parmp.sdate);                       // date - mm/dd/yyyy
               tempSB.append(parmp.stime);                       // tee time - hh:mm:ss AM (or PM)
               tempSB.append(posid);                             // POS ID
               tempSB.append(fname);                             // first name
               tempSB.append(mi);                                // mi
               tempSB.append(lname);                             // last name
               tempSB.append(itemName);                          // blank
               tempSB.append(item);                              // item code (SKU)
               tempSB.append(itemPrice);                         // blank
               tempSB.append("2");                               // player status (1 = main member, 2 = reg member or guest)
               tempSB.append(address);                           // blank
               tempSB.append(city);                              // blank
               tempSB.append(state);                             // blank
               tempSB.append(zip);                               // blank
               tempSB.append(email);                             // blank
               tempSB.append(instruct);                          // blank
               tempSB.append("\r\n");                            // EOF = CR LF

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent

               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = posid;
               parmp.hist_player = fname + " " + lname;
               parmp.hist_price = "";
               parmp.hist_item_name = "Green Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry      

            }      // end of Mship Charge processing

         } else {

            //
            //  player passed is a guest - charge the member for this too
            //
            //
            //  First check if there is a charge amount associated with this guest type
            //
            i = 0;
            item = "";
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                  gtype = parmp.gtype[i];               // set guest type description
                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.gst9I[i];                 // set guest item group #
                  } else {
                     item = parmp.gstI[i];                 // set guest item group #
                  }
                  break loop3;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

               //
               //  Build the Item/SKU field (12 chars)
               //
               len = item.length();                   // get length of item code

               if (len < 12) {                         // if < 12 characters

                  while (len < 12) {

                     item = item + " ";             // add a space filler
                     len++;
                  }

               } else {

                  if (len > 12) {                         // if > 12 characters

                     while (len > 12) {

                        item = stripOne(item);         // strip the first char
                        len--;
                     }
                  }
               }

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(resid);    // put reservation id in string buffer
               tempSB.append(ttid);                              // player id
               tempSB.append(courseid);                          // course id
               tempSB.append(course);                            // course name
               tempSB.append(parmp.sdate);                       // date - mm/dd/yyyy
               tempSB.append(parmp.stime);                       // tee time - hh:mm:ss AM (or PM)
               tempSB.append(posid);                             // POS ID
               tempSB.append(fname);                             // first name
               tempSB.append(mi);                                // mi
               tempSB.append(lname);                             // last name
               tempSB.append(itemName);                          // blank
               tempSB.append(item);                              // item code (SKU)
               tempSB.append(itemPrice);                         // blank
               tempSB.append("2");                               // player status (1 = main member, 2 = reg member or guest)
               tempSB.append(address);                           // blank
               tempSB.append(city);                              // blank
               tempSB.append(state);                             // blank
               tempSB.append(zip);                               // blank
               tempSB.append(email);                             // blank
               tempSB.append(instruct);                          // blank
               tempSB.append("\r\n");                            // EOF = CR LF

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
               
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = posid;
               parmp.hist_player = parmp.player;
               parmp.hist_price = "";
               parmp.hist_item_name = "Guest Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry      

            }
         }     // end of guest processing

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in Proshop_sheet buildCSG for club: " +club;
         errorMsg2 = errorMsg2 + ", Exception: " + e2.getMessage();                 // build error msg

         SystemUtils.logError(errorMsg2);                                           // log it
      }
   }

   return(done);
 }                   // end of buildCSG


 // ********************************************************************
 //  Process the NorthStar POS charges for an individual member
 //
 //  Build an ASCII file containing the following (this will build one record):
 //
 //
 //      mm/dd/yyyy hh:mm:ss                    (current date & time)
 //      mNum, mm/dd/yyyy, pos item#, quantity  (one record per charge)
 //      #                                      (total # of records included)
 //      EOF
 //
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public int buildLineNS(parmPOS parmp, String filename, PrintWriter out, Connection con) {


   ResultSet rs = null;

   String mship = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String item = "";
   String line = "";

   int i = 0;
   int p9c = 0;
   int done = 0;


   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         posid = rs.getString(4);
      }
      pstmtc.close();

      //
      //  Skip if no posid - otherwise entire file will fail
      //
      if (!posid.equals( "" )) {

         //
         //  First check if there is a charge amount associated with this player's mode of trans
         //
         i = 0;
         loop1:
         while (i < parmp.MAX_Tmodes) {

            if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

               tmode = parmp.tmode[i];                // get full description of tmode
               if (parmp.p9 == 1) {                   // if 9 holes
                  item = parmp.t9pos[i];              // get Item Group # for tmode
               } else {
                  item = parmp.tpos[i];               // get Item Group # for tmode
               }
               break loop1;
            }
            i++;
         }

         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            if (parmp.count == 0) {                         // if first item to be charged

               addHdrNS(parmp, filename);                   // go create the file and build the header
            }

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(posid);     // put member's posid in string buffer
            tempSB.append(",");
            tempSB.append(parmp.sdate);                        // date (mm/dd/yyyy)
            tempSB.append(",");
            tempSB.append(item);                               // item's POS Id
            tempSB.append(",1");                               // quantity = 1

            line = tempSB.toString();                         // save as string value

            addLineNS(filename, line);                        // go add this record

            parmp.count++;      // bump record counter

            done = 1;           // indicate charge sent
                 
            //
            //  Save charge data in pos_hist for reports
            //
            parmp.hist_posid = posid;
            if (!parmp.player.equals( "" )) {
               parmp.hist_player = parmp.player;          // if guest
            } else {
               parmp.hist_player = fname + " " + lname;   // else use member name
            }
            parmp.hist_price = "";
            parmp.hist_item_name = parmp.pcw;
            parmp.hist_item_num = item;

            add_POS_hist(parmp, con);       // go make the entry 
         }

         //
         //  get the mship class and charge amount, if any and if player is a member!
         //
         if (parmp.player.equals( "" )) {     // if player is a member

            i = 0;
            item = "";

            if (parmp.club.equals( "baltimore" )) {


               //
               //  Baltimore CC - certain mship types are charged at all times based on the course
               //
               if (mship.equals( "No Package" ) || mship.equals( "Non Golf" )) {

                  if (parmp.course.startsWith( "West" )) {

                     item = "1620450185";          // West Course Code

                  } else {

                     item = "1785145609";          // East Course Code
                  }
                    
               } else {

                  if (mship.equals( "Non Resident No Package" )) {

                     if (parmp.course.startsWith( "West" )) {

                        item = "1897823435";          // West Course Code

                     } else {

                        item = "997456107";          // East Course Code
                     }

                  } else {

                     if (mship.equals( "Non Season Golfer" )) {

                        if (parmp.course.startsWith( "West" )) {

                           if (parmp.p9 == 1) {                // if 9 holes

                              item = "603561154";              // West Course Code - 9 holes

                           } else {

                              item = "1801649527";              // West Course Code - 18 holes
                           }

                        } else {

                           if (parmp.p9 == 1) {                // if 9 holes

                              item = "1421916998";             // East Course Code - 9 holes

                           } else {

                              item = "1225472267";              // East Course Code - 18 holes
                           }
                        }
                     }
                  }
               }

            } else {   // NOT Baltimore

               loop2:
               while (i < parmp.MAX_Mships) {

                  if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                     if (parmp.p9 == 1) {                   // if 9 holes
                        item = parmp.mship9I[i];               // get mship item group #
                     } else {
                        item = parmp.mshipI[i];               // get mship item group #
                     }
                     break loop2;
                  }
                  i++;
               }
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               if (parmp.count == 0) {                         // if first item to be charged

                  addHdrNS(parmp, filename);                   // go create the file and build the header
               }

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(posid);     // put member's posid in string buffer
               tempSB.append(",");
               tempSB.append(parmp.sdate);                        // date (mm/dd/yyyy)
               tempSB.append(",");
               tempSB.append(item);                               // item's POS Id
               tempSB.append(",1");                               // quantity = 1

               line = tempSB.toString();                         // save as string value

               addLineNS(filename, line);                        // go add this record

               parmp.count++;      // bump record counter

               done = 1;           // indicate charge sent
                 
               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = posid;
               parmp.hist_player = fname + " " + lname;   // else use member name
               parmp.hist_price = "";
               parmp.hist_item_name = "Green Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 
               
            }      // end of Mship Charge processing

         } else {

            //
            //  player passed is a guest - charge the member for this too
            //

            //
            //  First check if there is a charge amount associated with this guest type
            //
            i = 0;
            item = "";
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                  gtype = parmp.gtype[i];               // set guest type description
                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.gst9I[i];                 // set guest item group #
                  } else {
                     item = parmp.gstI[i];                 // set guest item group #
                  }
                  break loop3;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

               if (parmp.count == 0) {                         // if first item to be charged

                  addHdrNS(parmp, filename);                   // go create the file and build the header
               }

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(posid);     // put member's posid in string buffer
               tempSB.append(",");
               tempSB.append(parmp.sdate);                        // date (mm/dd/yyyy)
               tempSB.append(",");
               tempSB.append(item);                               // item's POS Id
               tempSB.append(",1");                               // quantity = 1

               line = tempSB.toString();                         // save as string value

               addLineNS(filename, line);                        // go add this record

               parmp.count++;      // bump record counter

               done = 1;           // indicate charge sent

               //
               //  Save charge data in pos_hist for reports
               //
               parmp.hist_posid = posid;
               parmp.hist_player = parmp.player;          // if guest
               parmp.hist_price = "";
               parmp.hist_item_name = "Guest Fee";
               parmp.hist_item_num = item;

               add_POS_hist(parmp, con);       // go make the entry 
            }
         }     // end of guest processing
           
      }        // end of IF no posid

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildLineNS: ";
      errorMsg1 = errorMsg1 + e1.getMessage();                                // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   return(done);
 }                   // end of buildLineNS


 // ********************************************************************
 //
 //  NorthStar POS - Create a new ASCII file and add the header.
 //
 // ********************************************************************

 public void addHdrNS(parmPOS parmp, String filename) {


    addLineNS(filename, parmp.sdate);                // go create file and add header

    //
    //  Now strip the time stamp from sdate so we can use the date for each record
    //
    //   "mm/dd/yyyy hh:mm:ss" -> "mm/dd/yyyy"
    //
    StringTokenizer tok = new StringTokenizer( parmp.sdate, " " );     // delimiters are space

    parmp.sdate = tok.nextToken();        // date only
 }
   

 // ********************************************************************
 //  Print an Individual Tee Time (prtTeeTime=yes)
 // ********************************************************************

 private void printTeeTime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, 
                           Connection con) {


   ResultSet rs = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
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
   String day = "";
   String ampm = " AM";
   String course = "";
   String fbs = "";
   String dates = "";
   String times = "";

   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int fb = 0;
   int time = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;

   long date = 0;

   //
   //  get the club name from the session
   //
   String club = (String)session.getAttribute("club");      // get club name

   //
   //  Get the tee time parms passed
   //
   course = req.getParameter("course");
   dates = req.getParameter("date");
   times = req.getParameter("time");
   fbs = req.getParameter("fb");

   fb = Integer.parseInt(fbs);
   time = Integer.parseInt(times);
   date = Long.parseLong(dates);


   try {

      //
      //  Get the tee time info for the time requested and display it
      //
      PreparedStatement pstmt2s = con.prepareStatement (
         "SELECT * " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt2s.clearParameters();        // clear the parms
      pstmt2s.setLong(1, date);
      pstmt2s.setInt(2, time);
      pstmt2s.setInt(3, fb);
      pstmt2s.setString(4, course);

      rs = pstmt2s.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         mm = rs.getInt("mm");
         dd = rs.getInt("dd");
         yy = rs.getInt("yy");
         day = rs.getString("day");
         hr = rs.getInt("hr");
         min = rs.getInt("min");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         show1 = rs.getInt("show1");
         show2 = rs.getInt("show2");
         show3 = rs.getInt("show3");
         show4 = rs.getInt("show4");
         player5 = rs.getString("player5");
         p5cw = rs.getString("p5cw");
         show5 = rs.getInt("show5");
         mNum1 = rs.getString("mNum1");
         mNum2 = rs.getString("mNum2");
         mNum3 = rs.getString("mNum3");
         mNum4 = rs.getString("mNum4");
         mNum5 = rs.getString("mNum5");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
      }
        
      pstmt2s.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet printTeeTime: ";
      errorMsg1 = errorMsg1 + e1.getMessage();                                // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error in Proshop_sheet printTeeTime.");
      out.println("<BR>Error:" + e1.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
      return;
   }

   //
   //  Create time value
   //
   if (hr == 0) {
      hr = 12;                 // change to 12 AM (midnight)
   } else {
      if (hr == 12) {
         ampm = " PM";         // change to Noon
      }
   }
   if (hr > 12) {
      hr = hr - 12;
      ampm = " PM";             // change to 12 hr clock
   }

   fbs = "Front";
     
   if (fb == 1) {
     
      fbs = "Back";
   }

   if (player1.equals( "" ) || player1 == null) {
     
      player1 = "&nbsp;";
      p1cw = "&nbsp;";
      mNum1 = "&nbsp;";
      p91 = 0;
      show1 = 0;
   }
   if (player2.equals( "" ) || player2 == null) {

      player2 = "&nbsp;";
      p2cw = "&nbsp;";
      mNum2 = "&nbsp;";
      p92 = 0;
      show2 = 0;
   }
   if (player3.equals( "" ) || player3 == null) {

      player3 = "&nbsp;";
      p3cw = "&nbsp;";
      mNum3 = "&nbsp;";
      p93 = 0;
      show3 = 0;
   }
   if (player4.equals( "" ) || player4 == null) {

      player4 = "&nbsp;";
      p4cw = "&nbsp;";
      mNum4 = "&nbsp;";
      p94 = 0;
      show4 = 0;
   }

   if (mNum1.equals( "" ) || mNum1 == null) {

      mNum1 = "&nbsp;";
   }
   if (mNum2.equals( "" ) || mNum2 == null) {

      mNum2 = "&nbsp;";
   }
   if (mNum3.equals( "" ) || mNum3 == null) {

      mNum3 = "&nbsp;";
   }
   if (mNum4.equals( "" ) || mNum4 == null) {

      mNum4 = "&nbsp;";
   }
   if (mNum5.equals( "" ) || mNum5 == null) {

      mNum5 = "&nbsp;";
   }

   if (p91 == 1) {
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


   //
   //  Output the page to display the tee time (new window)
   //
   out.println(SystemUtils.HeadTitle("Proshop Tee Time Display"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<font size=\"3\">");
   out.println("<b>Individual Tee Time Display</b>");
   out.println("</font>");
   out.println("<font size=\"2\"><br><br>");
   out.println("<b>Date:</b>&nbsp;&nbsp;" +day+ ",&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("<b>Time:</b>&nbsp;&nbsp; " + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
   out.println("<br><br>");

   if (!course.equals( "" )) {

      out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   }

   out.println("<b>Front/Back Nine:</b>&nbsp;&nbsp; " + fbs + "<br><br>");

   out.println("<table border=\"1\" valign=\"top\" cellpadding=\"5\">");       // table for player info
   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println("<b>Player</b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b>Member #</b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b>Mode of Trans</b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b>Checked In?</b>");
   out.println("</font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println(player1);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(mNum1);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(p1cw);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (!player1.equals( "&nbsp;" )) {
      if (show1 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
   } else {
      out.println("&nbsp;");
   }
   out.println("</font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println(player2);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(mNum2);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(p2cw);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (!player2.equals( "&nbsp;" )) {
      if (show2 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
   } else {
      out.println("&nbsp;");
   }
   out.println("</font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println(player3);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(mNum3);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(p3cw);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (!player3.equals( "&nbsp;" )) {
      if (show3 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
   } else {
      out.println("&nbsp;");
   }
   out.println("</font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println(player4);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(mNum4);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(p4cw);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (!player4.equals( "&nbsp;" )) {
      if (show4 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
   } else {
      out.println("&nbsp;");
   }
   out.println("</font></td>");
   out.println("</tr>");

   if (!player5.equals( "" ) && player5 != null) {

      out.println("<tr>");
      out.println("<td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println(player5);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println(mNum5);
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println(p5cw);
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      if (show5 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
      out.println("</font></td>");
      out.println("</tr>");
   }

   out.println("</table><br>");
   out.println("<table border=\"0\" valign=\"top\">");       // table for main page
   out.println("<tr><td align=\"center\">");
   out.println("<form>");
   out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"Close\" onClick='self.close()' alt=\"Close\">");
   out.println("</form>");
   out.println("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"center\">");
   out.println("<form method=\"link\" action=\"javascript:self.print()\">");
   out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print</button>");
   out.println("</form>");
   out.println("</td></tr></table>");
   out.println("</center></font></body></html>");
   out.close();
   return;
 }                   // end of printTeeTime


 // ******************************************************************************
 //  Get a member's bag slot number
 // ******************************************************************************

 private String getBag(String user, Connection con) { 


   ResultSet rs = null;

   String bag = "";
   

   try {
     
      if (!user.equals( "" )) {

         PreparedStatement pstmte1 = con.prepareStatement (
                  "SELECT bag FROM member2b WHERE username = ?");

         pstmte1.clearParameters();        // clear the parms
         pstmte1.setString(1, user);
         rs = pstmte1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            bag = rs.getString(1);         // user's bag room slot#
         }

         pstmte1.close();                  // close the stmt
      }

   }
   catch (Exception ignore) {
   }

   return(bag);
     
 }                   // end of getBag


 // ******************************************************************************
 //  Custom - Get a member's Range Pass and Cart Pass data 
 // ******************************************************************************

 private String getCart(String prouser, String user, Connection con) {


   ResultSet rs = null;

   String mnum = "";
   String cartpass = "";


   try {

      if (!user.equals( "" )) {

         PreparedStatement pstmte1 = con.prepareStatement (
                  "SELECT memNum, webid FROM member2b WHERE username = ?");

         pstmte1.clearParameters();        // clear the parms
         pstmte1.setString(1, user);
         rs = pstmte1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mnum = rs.getString(1);        // user's mNum
            cartpass = rs.getString(2);    //  Range Pass and Cart Pass info (saved in webid)   
         }

         pstmte1.close();                  // close the stmt
      }

   }
   catch (Exception ignore) {
   }
        
   if (prouser.equalsIgnoreCase( "proshop4" ) || prouser.equalsIgnoreCase( "proshop5" )) {   // if Greeley Pro

      if (!cartpass.equals( "" )) {

         mnum = mnum + " " + cartpass;
      }
        
   } else if (prouser.equalsIgnoreCase("proshopfox")) {

       // Fox Hill CC  Update later if needed

   } else {           // Fort Collins user

      if (mnum.startsWith( "FC" )) {        // is user is from Fort Collins

         mnum = "FC";
           
      } else {

         mnum = "G";
      }

   }

   return(mnum);

 }                   // end of getCart


 // ******************************************************************************
 //  Get a member's Mship data and display in place of the mNum
 // ******************************************************************************

 private String getMship(String user, String club, Connection con) {

   String mship = "";

   try {

      if (!user.equals( "" )) {

         PreparedStatement pstmte1 = con.prepareStatement (
                  "SELECT m_ship FROM member2b WHERE username = ?");

         pstmte1.clearParameters();        // clear the parms
         pstmte1.setString(1, user);
         ResultSet rs = pstmte1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mship = rs.getString(1);        // user's mship type
         }

         pstmte1.close();                  // close the stmt
         
         if (club.equals("merion")) {
             
             //
             //  check type
             //
             if (mship.equals( "House" )) {

                mship = "H";

             } else {

                if (mship.equals( "Non-Resident" )) {

                   mship = "NR";

                } else {

                   mship = "";
                }
             }
         } // end if merion
         
      } // end if user empty

   }
   catch (Exception ignore) {
   }

   return(mship);

 }                   // end of getMship


 // ******************************************************************************
 //  IBS POS - Player is a Guest - add charge fee if it exists
 // ******************************************************************************

 private String checkIBSguest(String player, String gType, int p9, Connection con) {

   String fee = "";
   String fee9 = "";
   String fee18 = "";
    
   try {

      if (!gType.equals( "" )) {

         PreparedStatement pstmte1 = con.prepareStatement (
                  "SELECT gpos, g9pos FROM guest5 WHERE guest = ?");

         pstmte1.clearParameters();        // clear the parms
         pstmte1.setString(1, gType);
         ResultSet rs = pstmte1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            fee18 = rs.getString(1);       
            fee9 = rs.getString(2);        
         }
         pstmte1.close();                  // close the stmt
         
         if (p9 == 1) {                     // if 9-hole round
            
            fee = fee9;
            
         } else {
            
            fee = fee18;
         }
         
         if (!fee.equals("")) {
        
            player = player + " $" + fee;    // add fee to player name
         } 
         
      } 

   }
   catch (Exception ignore) {
   }

   return(player);

 }                   // end of checkIBSguest


 // ******************************************************************************
 //  Interlachen & Cherry Hills Custom - update # of caddies assigned to group
 // ******************************************************************************

 private void updateCaddies(String stime, String sfb, long date, String numCaddies, PrintWriter out, Connection con) {


   //
   //  Convert the common string values to int's
   //
   int caddies = Integer.parseInt(numCaddies);
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   if (caddies < 6) {     // ensure correct values
     
      try {

         PreparedStatement pstmt3 = con.prepareStatement (
              "UPDATE teecurr2 SET hotelNew = ? " +
              "WHERE date = ? AND time = ? AND fb = ?");

          //
          //  execute the prepared statement to update the tee time slot
          //
          pstmt3.clearParameters();        // clear the parms
          pstmt3.setInt(1, caddies);
          pstmt3.setLong(2, date);
          pstmt3.setInt(3, time);
          pstmt3.setInt(4, fb);
          pstmt3.executeUpdate();

          pstmt3.close();

      }
      catch (Exception ignore) {
      }
   }
 }      // end of updateCaddies


 // **************************************************************************************
 //  Pecan Plantation and The Ranch Custom and AGC - update 'teed off' indicator for group
 // **************************************************************************************

 private void updateTeedOff(String course, String stime, String sfb, long date, String teedOff, PrintWriter out, Connection con) {


   int teed = 0;
     
   //
   //  Convert the common string values to int's
   //
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   if (teedOff.equalsIgnoreCase( "x" )) {     // if 'teed off' specified

      teed = 1;
        
   } else {

      if (teedOff.equalsIgnoreCase( "o" )) {     // if 'on deck' specified

         teed = 2;
      }
   }

   try {

      PreparedStatement pstmt3 = con.prepareStatement (
           "UPDATE teecurr2 SET hotelNew = ? " +
           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

       //
       //  execute the prepared statement to update the tee time slot
       //
       pstmt3.clearParameters();        // clear the parms
       pstmt3.setInt(1, teed);
       pstmt3.setLong(2, date);
       pstmt3.setInt(3, time);
       pstmt3.setInt(4, fb);
       pstmt3.setString(5, course);
       pstmt3.executeUpdate();

       pstmt3.close();

   }
   catch (Exception ignore) {
   }
     
 }      // end of updateCaddies


 // *********************************************************
 //  Cordillera Custom - update the forecaddie indicator for group
 // *********************************************************

 private void updateForeCaddie(String stime, String sfb, long date, String foreCaddie, String course, 
                               PrintWriter out, Connection con) {


   //
   //  Convert the common string values to int's
   //
   int fc = 0;
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   if (foreCaddie.equals( "Y" )) {     // if ForeCaddies = Yes
     
      fc = 1;
   }

   try {

      PreparedStatement pstmt3 = con.prepareStatement (
           "UPDATE teecurr2 SET pos5 = ? " +
           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

       //
       //  execute the prepared statement to update the tee time slot
       //
       pstmt3.clearParameters();        // clear the parms
       pstmt3.setInt(1, fc);
       pstmt3.setLong(2, date);
       pstmt3.setInt(3, time);
       pstmt3.setInt(4, fb);
       pstmt3.setString(5, course);
       pstmt3.executeUpdate();

       pstmt3.close();

   }
   catch (Exception ignore) {
   }
     
 }      // end of updateForeCaddie


 // *********************************************************
 //  Display event information in new pop-up window
 // *********************************************************

 private void displayEvent(String name, PrintWriter out, Connection con) {

   ResultSet rs = null;

   int year = 0;
   int month = 0;
   int day = 0;
   int act_hr = 0;
   int act_min = 0;
   int signUp = 0;
   int type = 0;
   int holes = 0;
   int max = 0;
   int size = 0;
   int guests = 0;
   int teams = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_time = 0;
   int c_hr = 0;
   int c_min = 0;

   String course = "";
   String format = "";
   String pairings = "";
   String memcost = "";
   String gstcost = "";
   String itin = "";
   String c_ampm = "";
   String act_ampm = "";
   String fb = "";

   //
   //  Locate the event and display the content
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM events2b " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         type = rs.getInt("type");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         course = rs.getString("courseName");
         signUp = rs.getInt("signUp");
         format = rs.getString("format");
         pairings = rs.getString("pairings");
         size = rs.getInt("size");
         max = rs.getInt("max");
         guests = rs.getInt("guests");
         memcost = rs.getString("memcost");
         gstcost = rs.getString("gstcost");
         c_month = rs.getInt("c_month");
         c_day = rs.getInt("c_day");
         c_year = rs.getInt("c_year");
         c_time = rs.getInt("c_time");
         itin = rs.getString("itin");
         holes = rs.getInt("holes");
         fb = rs.getString("fb");

      } else {           // name not found - try filtering it

         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            year = rs.getInt("year");
            month = rs.getInt("month");
            day = rs.getInt("day");
            type = rs.getInt("type");
            act_hr = rs.getInt("act_hr");
            act_min = rs.getInt("act_min");
            course = rs.getString("courseName");
            signUp = rs.getInt("signUp");
            format = rs.getString("format");
            pairings = rs.getString("pairings");
            size = rs.getInt("size");
            max = rs.getInt("max");
            guests = rs.getInt("guests");
            memcost = rs.getString("memcost");
            gstcost = rs.getString("gstcost");
            c_month = rs.getInt("c_month");
            c_day = rs.getInt("c_day");
            c_year = rs.getInt("c_year");
            c_time = rs.getInt("c_time");
            itin = rs.getString("itin");
            holes = rs.getInt("holes");
            fb = rs.getString("fb");
         }
      }
      stmt.close();

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

      c_hr = c_time / 100;
      c_min = c_time - (c_hr * 100);

      c_ampm = "AM";

      if (c_hr == 0) {

         c_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (c_hr == 12) {

            c_ampm = "PM";         // change to Noon
         }
      }
      if (c_hr > 12) {

         c_hr = c_hr - 12;
         c_ampm = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Proshop Event Information"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<font size=\"3\">");
      out.println("Event: <b>" + name + "</b>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\"><br><br>");
      out.println("<b>Date:</b>&nbsp;&nbsp; " + month + "/" + day + "/" + year);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (act_min < 10) {
         out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":0" + act_min + " " + act_ampm);
      } else {
         out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":" + act_min + " " + act_ampm);
      }
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (type != 0) {
         out.println("<b>Type:</b>&nbsp;&nbsp; Shotgun<br><br>");
      } else {
         out.println("<b>Type:</b>&nbsp;&nbsp; Tee Times<br><br>");
      }

      if (!course.equals( "" )) {

         out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }

      out.println("<b>Front/Back Tees:</b>&nbsp;&nbsp; " + fb + "<br><br>");

      if (signUp != 0) {       // if members can sign up

         out.println("<b>Format:</b>&nbsp;&nbsp; " + format + "<br><br>");
         out.println("<b>Pairings by:</b>&nbsp;&nbsp; " + pairings);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b># of Teams:</b>&nbsp;&nbsp; " + max);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b>Team Size:</b>&nbsp;&nbsp; " + size);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b>Holes:</b>&nbsp;&nbsp; " + holes + "<br><br>");
         out.println("<b>Guests per Member:</b>&nbsp;&nbsp;" + guests);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b>Cost per Guest:</b>&nbsp;&nbsp;" + gstcost);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b>Cost per Member:</b>&nbsp;&nbsp;" + memcost + "<br><br>");
         if (c_min < 10) {
            out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":0" + c_min + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
         } else {
            out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":" + c_min + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
         }
         out.println("<br><br>");
         out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");

      } else {
         out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br><br>");
         out.println("Online sign up was not selected for this event.");
      }

      //
      //  End of HTML page
      //
      out.println("</font></td></tr></table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td><td>&nbsp;&nbsp;");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Edit Event\">");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }


 // *********************************************************
 //  Display restriction information in new pop-up window
 // *********************************************************

 private void displayRest(int id, PrintWriter out, Connection con) {

   ResultSet rs = null;

   int year1 = 0;
   int month1 = 0;
   int day1 = 0;
   int year2 = 0;
   int month2 = 0;
   int day2 = 0;
   int hr1 = 0;
   int min1 = 0;
   int hr2 = 0;
   int min2 = 0;
   int i = 0;

   String course = "";
   String recurr = "";
   String fb = "";
   String ampm1 = "AM";
   String ampm2 = "AM";
   String name = "";

   String [] mtype = new String [8];                     // member types
   String [] mship = new String [8];                     // membership types

   //
   //  Locate the event and display the content
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM restriction2 " +
         "WHERE id = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setInt(1, id);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         name = rs.getString("name");
         month1 = rs.getInt("start_mm");
         day1 = rs.getInt("start_dd");
         year1 = rs.getInt("start_yy");
         hr1 = rs.getInt("start_hr");
         min1 = rs.getInt("start_min");
         month2 = rs.getInt("end_mm");
         day2 = rs.getInt("end_dd");
         year2 = rs.getInt("end_yy");
         hr2 = rs.getInt("end_hr");
         min2 = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         mtype[0] = rs.getString("mem1");
         mtype[1] = rs.getString("mem2");
         mtype[2] = rs.getString("mem3");
         mtype[3] = rs.getString("mem4");
         mtype[4] = rs.getString("mem5");
         mtype[5] = rs.getString("mem6");
         mtype[6] = rs.getString("mem7");
         mtype[7] = rs.getString("mem8");
         mship[0] = rs.getString("mship1");
         mship[1] = rs.getString("mship2");
         mship[2] = rs.getString("mship3");
         mship[3] = rs.getString("mship4");
         mship[4] = rs.getString("mship5");
         mship[5] = rs.getString("mship6");
         mship[6] = rs.getString("mship7");
         mship[7] = rs.getString("mship8");
         course = rs.getString("courseName");
         fb = rs.getString("fb");

      } else {         // not found - try filtering the name
/*
         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            month1 = rs.getInt("start_mm");
            day1 = rs.getInt("start_dd");
            year1 = rs.getInt("start_yy");
            hr1 = rs.getInt("start_hr");
            min1 = rs.getInt("start_min");
            month2 = rs.getInt("end_mm");
            day2 = rs.getInt("end_dd");
            year2 = rs.getInt("end_yy");
            hr2 = rs.getInt("end_hr");
            min2 = rs.getInt("end_min");
            recurr = rs.getString("recurr");
            mtype[0] = rs.getString("mem1");
            mtype[1] = rs.getString("mem2");
            mtype[2] = rs.getString("mem3");
            mtype[3] = rs.getString("mem4");
            mtype[4] = rs.getString("mem5");
            mtype[5] = rs.getString("mem6");
            mtype[6] = rs.getString("mem7");
            mtype[7] = rs.getString("mem8");
            mship[0] = rs.getString("mship1");
            mship[1] = rs.getString("mship2");
            mship[2] = rs.getString("mship3");
            mship[3] = rs.getString("mship4");
            mship[4] = rs.getString("mship5");
            mship[5] = rs.getString("mship6");
            mship[6] = rs.getString("mship7");
            mship[7] = rs.getString("mship8");
            course = rs.getString("courseName");
            fb = rs.getString("fb");
         }
 */
      }
      stmt.close();

      //
      //  Create time values
      //
      if (hr1 == 0) {
         hr1 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr1 == 12) {
            ampm1 = "PM";         // change to Noon
         }
      }
      if (hr1 > 12) {
         hr1 = hr1 - 12;
         ampm1 = "PM";             // change to 12 hr clock
      }

      if (hr2 == 0) {
         hr2 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr2 == 12) {
            ampm2 = "PM";         // change to Noon
         }
      }
      if (hr2 > 12) {
         hr2 = hr2 - 12;
         ampm2 = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Proshop Restriction Information"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
        
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"3\">");
      out.println("Restriction: <b>" + name + "</b>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\"><br><br>");
      out.println("<b>Start Date of Restriction:</b>&nbsp;&nbsp; " + month1 + "/" + day1 + "/" + year1);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min1 < 10) {
         out.println("<b>Start Time:</b>&nbsp;&nbsp; " + hr1 + ":0" + min1 + " " + ampm1);
      } else {
         out.println("<b>Start Time:</b>&nbsp;&nbsp; " + hr1 + ":" + min1 + " " + ampm1);
      }
      out.println("<br><br><b>End Date of Restriction:</b>&nbsp;&nbsp; " + month2 + "/" + day2 + "/" + year2);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min2 < 10) {
         out.println("<b>End Time:</b>&nbsp;&nbsp; " + hr2 + ":0" + min2 + " " + ampm2);
      } else {
         out.println("<b>End Time:</b>&nbsp;&nbsp; " + hr2 + ":" + min2 + " " + ampm2);
      }

      out.println("<br><br><b>Recurrence:</b>&nbsp;&nbsp; " +recurr+ "<br><br>");

      if (!course.equals( "" )) {

         out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }

      out.println("<b>Front/Back Tees:</b>&nbsp;&nbsp; " + fb + "<br><br>");

      // if any member types specified
      if (!mtype[0].equals("") || !mtype[1].equals("") || !mtype[2].equals("") || !mtype[3].equals("") ||
          !mtype[4].equals("") || !mtype[5].equals("") || !mtype[6].equals("") || !mtype[7].equals("")) {

         out.println("<b>Member Types Restricted:</b>");
         if (!mtype[0].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[0]);
         }
         if (!mtype[1].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[1]);
         }
         if (!mtype[2].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[2]);
         }
         if (!mtype[3].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[3]);
         }
         if (!mtype[4].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[4]);
         }
         if (!mtype[5].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[5]);
         }
         if (!mtype[6].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[6]);
         }
         if (!mtype[7].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[7]);
         }
         out.println("<br><br>");
      }

      // if any membership types specified
      if (!mship[0].equals("") || !mship[1].equals("") || !mship[2].equals("") || !mship[3].equals("") ||
          !mship[4].equals("") || !mship[5].equals("") || !mship[6].equals("") || !mship[7].equals("")) {

         out.println("<b>Membership Types Restricted:</b>");
         if (!mship[0].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[0]);
         }
         if (!mship[1].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[1]);
         }
         if (!mship[2].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[2]);
         }
         if (!mship[3].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[3]);
         }
         if (!mship[4].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[4]);
         }
         if (!mship[5].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[5]);
         }
         if (!mship[6].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[6]);
         }
         if (!mship[7].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[7]);
         }
         out.println("<br><br>");
      }

      //
      //  End of HTML page
      //
      out.println("</font></td></tr></table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td><td>&nbsp;&nbsp;");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_mrest\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Edit Restriction\">");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }


 // *********************************************************
 //  Display Lottery information in new pop-up window
 // *********************************************************

 private void displayLottery(String name, PrintWriter out, Connection con) {

   ResultSet rs = null;

   int year1 = 0;
   int month1 = 0;
   int day1 = 0;
   int year2 = 0;
   int month2 = 0;
   int day2 = 0;
   int hr1 = 0;
   int min1 = 0;
   int hr2 = 0;
   int min2 = 0;
   int hr3 = 0;
   int min3 = 0;
   int hr4 = 0;
   int min4 = 0;
   int hr5 = 0;
   int min5 = 0;
   int sdays = 0;
   int edays = 0;
   int pdays = 0;
   int slots = 0;
   int players = 0;
   int members = 0;

   String course = "";
   String recurr = "";
   String fb = "";
   String ampm1 = "AM";
   String ampm2 = "AM";
   String ampm3 = "AM";
   String ampm4 = "AM";
   String ampm5 = "AM";

   //
   //  Locate the lottery and display the content
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM lottery3 " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         month1 = rs.getInt("start_mm");
         day1 = rs.getInt("start_dd");
         year1 = rs.getInt("start_yy");
         hr1 = rs.getInt("start_hr");
         min1 = rs.getInt("start_min");
         month2 = rs.getInt("end_mm");
         day2 = rs.getInt("end_dd");
         year2 = rs.getInt("end_yy");
         hr2 = rs.getInt("end_hr");
         min2 = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         course = rs.getString("courseName");
         fb = rs.getString("fb");
         sdays = rs.getInt("sdays");
         hr3 = rs.getInt("sd_hr");
         min3 = rs.getInt("sd_min");
         edays = rs.getInt("edays");
         hr4 = rs.getInt("ed_hr");
         min4 = rs.getInt("ed_min");
         pdays = rs.getInt("pdays");
         hr5 = rs.getInt("p_hr");
         min5 = rs.getInt("p_min");
         slots = rs.getInt("slots");
         members = rs.getInt("members");
         players = rs.getInt("players");

      } else {        // not found - try filtering the name

         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            month1 = rs.getInt("start_mm");
            day1 = rs.getInt("start_dd");
            year1 = rs.getInt("start_yy");
            hr1 = rs.getInt("start_hr");
            min1 = rs.getInt("start_min");
            month2 = rs.getInt("end_mm");
            day2 = rs.getInt("end_dd");
            year2 = rs.getInt("end_yy");
            hr2 = rs.getInt("end_hr");
            min2 = rs.getInt("end_min");
            recurr = rs.getString("recurr");
            course = rs.getString("courseName");
            fb = rs.getString("fb");
            sdays = rs.getInt("sdays");
            hr3 = rs.getInt("sd_hr");
            min3 = rs.getInt("sd_min");
            edays = rs.getInt("edays");
            hr4 = rs.getInt("ed_hr");
            min4 = rs.getInt("ed_min");
            pdays = rs.getInt("pdays");
            hr5 = rs.getInt("p_hr");
            min5 = rs.getInt("p_min");
            slots = rs.getInt("slots");
            members = rs.getInt("members");
            players = rs.getInt("players");
         }
      }
      stmt.close();

      //
      //  Create time values
      //
      if (hr1 == 0) {
         hr1 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr1 == 12) {
            ampm1 = "PM";         // change to Noon
         }
      }
      if (hr1 > 12) {
         hr1 = hr1 - 12;
         ampm1 = "PM";             // change to 12 hr clock
      }

      if (hr2 == 0) {
         hr2 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr2 == 12) {
            ampm2 = "PM";         // change to Noon
         }
      }
      if (hr2 > 12) {
         hr2 = hr2 - 12;
         ampm2 = "PM";             // change to 12 hr clock
      }

      if (hr3 == 0) {
         hr3 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr3 == 12) {
            ampm3 = "PM";         // change to Noon
         }
      }
      if (hr3 > 12) {
         hr3 = hr3 - 12;
         ampm3 = "PM";             // change to 12 hr clock
      }

      if (hr4 == 0) {
         hr4 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr4 == 12) {
            ampm4 = "PM";         // change to Noon
         }
      }
      if (hr4 > 12) {
         hr4 = hr4 - 12;
         ampm4 = "PM";             // change to 12 hr clock
      }

      if (hr5 == 0) {
         hr5 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr5 == 12) {
            ampm5 = "PM";         // change to Noon
         }
      }
      if (hr5 > 12) {
         hr5 = hr5 - 12;
         ampm5 = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Proshop Lottery Information"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"3\">");
      out.println("Lottery: <b>" + name + "</b>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\"><br><br>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("Lottery Period:&nbsp;&nbsp; From<b>&nbsp;&nbsp; " + month1 + "/" + day1 + "/" + year1+ "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("To<b>&nbsp;&nbsp; " + month2 + "/" + day2 + "/" + year2+ "</b>");
      out.println("<br><br>");
      if (min1 < 10) {
         out.println("Start Time:<b>&nbsp;&nbsp; " + hr1 + ":0" + min1 + " " + ampm1+ "</b>");
      } else {
         out.println("Start Time:<b>&nbsp;&nbsp; " + hr1 + ":" + min1 + " " + ampm1+ "</b>");
      }
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min2 < 10) {
         out.println("End Time:<b>&nbsp;&nbsp; " + hr2 + ":0" + min2 + " " + ampm2+ "</b>");
      } else {
         out.println("End Time:<b>&nbsp;&nbsp; " + hr2 + ":" + min2 + " " + ampm2+ "</b>");
      }

      out.println("<br><br>Recurrence:<b>&nbsp;&nbsp; " +recurr+ "</b><br><br>");

      if (!course.equals( "" )) {

         out.println("Course:<b>&nbsp;&nbsp; " + course+ "</b>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }

      out.println("Front/Back Tees:<b>&nbsp;&nbsp; " + fb + "</b><br><br>");

      out.println("Days in Advance to Start Taking Requests:<b>&nbsp;&nbsp; " +sdays+ "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min3 < 10) {
         out.println("Time:<b>&nbsp;&nbsp; " + hr3 + ":0" + min3 + " " + ampm3+ "</b>");
      } else {
         out.println("Time:<b>&nbsp;&nbsp; " + hr3 + ":" + min3 + " " + ampm3+ "</b>");
      }
      out.println("<br><br>");

      out.println("Days in Advance to Stop Taking Requests:<b>&nbsp;&nbsp; " +edays+ "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min4 < 10) {
         out.println("Time:<b>&nbsp;&nbsp; " + hr4 + ":0" + min4 + " " + ampm4+ "</b>");
      } else {
         out.println("Time:<b>&nbsp;&nbsp; " + hr4 + ":" + min4 + " " + ampm4+ "</b>");
      }
      out.println("<br><br>");

      out.println("Days in Advance to Process Requests:<b>&nbsp;&nbsp; " +pdays+ "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min5 < 10) {
         out.println("Time:<b>&nbsp;&nbsp; " + hr5 + ":0" + min5 + " " + ampm5+ "</b>");
      } else {
         out.println("Time:<b>&nbsp;&nbsp; " + hr5 + ":" + min5 + " " + ampm5+ "</b>");
      }

      out.println("<br><br>Number of consecutive tee times member can request:<b>&nbsp;&nbsp; " +slots+ "</b>");
      out.println("<br><br>Minimum number of players per request:<b>&nbsp;&nbsp; " +players+ "</b>");
      out.println("<br><br>Minimum number of members per request:<b>&nbsp;&nbsp; " +members+ "</b>");

      //
      //  End of HTML page
      //
      out.println("</font></td></tr></table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td><td>&nbsp;&nbsp;");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_lottery\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Edit Lottery\">");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }

 
 // *********************************************************
 //  Create POS History entry in pos_hist table
 // *********************************************************

 private void add_POS_hist(parmPOS parmp, Connection con) {


   //
   //   Use the info in parmp to create a history entry
   try {

      PreparedStatement pstmt = con.prepareStatement (
        "INSERT INTO pos_hist (date, time, course, fb, member_id, player, " +
        "item_num, item_name, price, p9) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?)");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, parmp.date);
      pstmt.setInt(2, parmp.time);
      pstmt.setString(3, parmp.course);     
      pstmt.setInt(4, parmp.hist_fb);
      pstmt.setString(5, parmp.hist_posid);      
      pstmt.setString(6, parmp.hist_player);      
      pstmt.setString(7, parmp.hist_item_num);      
      pstmt.setString(8, parmp.hist_item_name);      
      pstmt.setString(9, parmp.hist_price);      
      pstmt.setInt(10, parmp.p9);

      pstmt.executeUpdate();          // execute the prepared stmt

      pstmt.close();   // close the stmt

   }
   catch (Exception exc) {

     SystemUtils.logError("Proshop_sheet - exception from Proshop_sheet.add_POS_hist. Error = " +exc.getMessage());
     return;
   } 
  
 }

 

 //************************************************************************
 //  addLineNS - create a text file (if not already done) and add a line to it.
 //
 //  Text file = clubname__.txt for NorthStar POS charges (__ is date and time)
 //
 //    The file is built as "clubname__.temp".  Once it is complete it is renamed.
 //    This prevents the possibility of NS picking up the file before it is complete.
 //
 //************************************************************************

 public void addLineNS(String fname, String line) {

   int fail = 0;

   String dirname = "//home//northstar//pos//";  // create directory name
   String filename = fname + ".temp";            // create full file name (temp file)
   String filename2 = fname + ".txt";            // create full file name (complete file)
   String fileDest = dirname + filename;         // destination (temp file)
   String fileDest2 = dirname + filename2;       // destination (complete file)


   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter(fileDest, true));

      //
      //  Put header line in text file
      //
      fout1.print(line);
      fout1.println();                            // output the line

      fout1.close();

      //
      //  Rename the file if this is the end
      //
      if (line.equals( "EOF" )) {                  // if end of the file
        
         File tempf = new File(fileDest);          // get temp file
         File tempf2 = new File(fileDest2);        // get complete file name
         tempf.renameTo(tempf2);                   // rename it (file is now complete and ready)
      }

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      dirname = "c:\\java\\tomcat\\webapps\\" +rev+ "\\northstar\\";     // create directory name
      filename = fname + ".temp";                                        // create full file name (temp file)
      filename2 = fname + ".txt";                                        // create full file name (complete file)
      fileDest = dirname + filename;                                     // destination (temp file)
      fileDest2 = dirname + filename2;                                   // destination (complete file)

      try {
         //
         //  dir path for test pc
         //
         PrintWriter fout = new PrintWriter(new FileWriter(fileDest, true));

         //
         //  Put header line in text file
         //
         fout.print(line);
         fout.println();                              // output the line

         fout.close();

         //
         //  Rename the file if this is the end
         //
         if (line.equals( "EOF" )) {                  // if end of the file

            File tempf = new File(fileDest);          // get temp file
            File tempf2 = new File(fileDest2);        // get complete file name
            tempf.renameTo(tempf2);                   // rename it (file is now complete and ready)
         }

      }
      catch (Exception ignore) {
      }
   }
 }  // end of addLineNS


 // *********************************************************
 //  Strip 1 char from the start of a string
 // *********************************************************

 private final static String stripOne( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i+1];
         ca2[i] = oldLetter;
      } 

      return new String (ca2);

 } // end stripOne


 // *********************************************************
 //  Strip 1 char from the END of a string
 // *********************************************************

 private final static String stripLast( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i];
         ca2[i] = oldLetter;
      }

      return new String (ca2);

 } // end stripLast



 // *********************************************************
 //  Build a player cell for the tee sheet
 // *********************************************************

 private void buildPlayerTD(PrintWriter out, int playern, short show, String player, String bgcolor, int time, 
                            String num, String courseNameT, short fb, int j, boolean disp_hndcp, int hndcp, float hndcpn,
                            String ghin, int guest, int guest_id, String guest_uid, String pcw, boolean disp_mnum, String mnum,
                            boolean disp_bag, String bag, String courseName1, String club, parmPOS parmp, int pos, 
                            boolean showcheckbox, boolean checkinAccess, String username, int teecurr_id) {
    
    String tmp_title = "";

    //
    //  Add Player
    //
    if (!player.equals("")) {

      if (player.equalsIgnoreCase("x")) {

         out.print("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
         out.print("<font size=\"2\">" + player + "</font></td>");

      } else {      // not 'x'

         //
         //  Add the check-in button
         //
         if (club.equals("southview") && player.startsWith("Join Me")) {
             bgcolor = "lime";
         }
         
         if (club.equals("mountvernoncc") && guest == 0) {  // display player name in lname, fname mi format for Mount Vernon CC
             player = Utilities.swapNameOrder(club, player);
         }
         
         out.print("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet#jump" +j+ "\" target=\"bot\">");
         out.print("<td bgcolor=\"" + bgcolor + "\" align=\"left\">");
         out.print("<font size=\"2\">");
         out.print("<input type=\"hidden\" name=\"playern\" value=\"" + playern + "\">");  // player #
         out.print("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
         out.print("<input type=\"hidden\" name=\"show\" value=\"" + show + "\">");
         out.print("<input type=\"hidden\" name=\"name\" value=\"" + num + "\">");
         out.print("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
         out.print("<input type=\"hidden\" name=\"courseCheckIn\" value=\"" + courseNameT + "\">");
         out.print("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         // out.print("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

         String p;
         p = player;                                       // copy name only

         if (guest == 0) {                                     // if not a guest

            if (disp_hndcp == true) {
               if ((hndcpn == 99) || (hndcpn == -99)) {
                  p = p + " NH";
               } else {
                                      
                  if (hndcpn < 0) {
                     hndcpn = 0 - hndcpn;                          // convert to non-negative
                     //hndcp = Math.round(hndcpn);                   // round it off
                     p = (p + " " + hndcpn);
                  } else {
                     //hndcp = Math.round(hndcpn);                   // round it off
                     p = (p + " +" + hndcpn);
                  }
                  
                  if (club.equals("cherryhills") && p.endsWith(".0")) p = p.substring(0, p.length() - 2);
               }
            }
            if (disp_mnum == true && !mnum.equals( "" )) {        // add Member # if present and requested
               
               // Display username instead of mnum for Charlotte CC 
               if (club.equals("charlottecc") || club.equals("loxahatchee")) {
                   p = p + "  " +username;
               } else {
                   p = p + "  " +mnum;
               }
            }
            if (disp_bag == true && !bag.equals( "" )) {          // add Bag # if present and requested
               p = p + "  " +bag;
            }

            if (club.equals("cwcpga") && !ghin.equals("")) {
                p += " " + ghin;
            }
         } // end if not guest
         
         if (showcheckbox == true || (club.equals("cherryhills") && num.equals("0"))) { // only print button if proshop user has access to checkin (or Cherry Hills proshop4 user & today)
             
             if (showcheckbox == false || checkinAccess == false) {   // just show image if Cherry Hills user OR Limited Access user w/o checkin access       
               
                out.print("&nbsp;<img src=\"/" +rev+ "/images/");    // must be cherry hills proshop4 user - show it but don't allow click

             } else {
               
                out.print("&nbsp;<img onclick=\"doCheckIn("+teecurr_id+","+playern+",'"+courseNameT+"',this.id);\" id=\"chkbox_"+teecurr_id+"_"+playern+"\" src=\"/" +rev+ "/images/");
             }

             switch (show) {
                 case 1:
                    tmp_title = "Click here to set as a no-show (blank).";
                    if (pos == 3) {
                       out.print("xboxsent.gif");     // charges sent
                    } else {
                       out.print("xbox.gif");         // charges NOT sent
                    }
                    break;
                 case 2:
                    tmp_title = "Click here to acknowledge new signup (pre-check in).";
                    out.print("rmtbox.gif");
                    break;
                 default:
                    tmp_title = "Click here to check player in (x).";
                    out.print("mtbox.gif");
                    break;
             }

             //   check-in image and player name
             out.print("\" border=\"1\" name=\"noShow\" title=\"" + tmp_title + "\">");
         }

         // If guest and part of the guestdb, display name as link to guest edit page for this guest_id.
         if (guest == 1 && guest_id != 0) {
             out.print("&nbsp;<a href=\"javascript: void(0)\" title=\"" + guest_uid + "\" " + (guest_id < 0 ? "style=\"color: red;\" " : "style=\"color: black; text-decoration: none\" ") + "onclick=\"window.open('/" +rev+ "/servlet/Common_guestdb?caller=sheet&guest_id=" + (guest_id < 0 ? (guest_id * -1) : guest_id) + "', 'guestEdit', 'height=500, width=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, directories=no, status=no')\"" + (guest_id > 0 ? " onmouseover=\"this.style.textDecoration='underline'\" onmouseout=\"this.style.textDecoration='none'\"" : "") + ">" + p + "</a></font>");
         } else {
             out.print("&nbsp;" + p + "</font>");
         }

         
         //
         //  If POS 'Pay Now' option selected, Today and POS Charges NOT already sent, add a 'Pay Now' or 'Paid' button so they can toggle players who pay in person
         //
         if (parmp.pos_paynow == 1 && num.equals("0") && pos != 1 && pos != 3) {
            
             out.print("&nbsp;<img onclick=\"doCheckIn("+teecurr_id+","+playern+",'"+courseNameT+"',this.id);\" id=\"paybox_"+teecurr_id+"_"+playern+"\" src=\"/" +rev+ "/images/");
             
             if (pos == 0) {      // if currently NOT paid
                
                 tmp_title = "Click here to mark player as Paid.";
                 out.print("pospaynow.gif");
                 
             } else {      // if currently paid
                
                 tmp_title = "Click here to mark player as NOT Paid.";
                 out.print("pospaid.gif");
             }

             //   check-in image and player name
             out.print("\" border=\"1\" name=\"paynow\" title=\"" + tmp_title + "\">");
         
         }        // end of Pay Now
      
         
         out.print("</td></form>");    // end player cell and check-in form or Pay Now form
         
      } // end if not "x"

    } // end if player != ""
    else 
    { 
        // player string was empty
        out.print("<td bgcolor=\"" + bgcolor + "\">");
        out.print("<font size=\"2\">");
        out.print("&nbsp;");
        out.print("</font></td>");
    }
    
    //
    //  Custom for foresthighlands & stalbans, hi-lite the tmode option if caddie
    //
    String tmode_color = "white";
    if ((club.equals("foresthighlands") && pcw.equalsIgnoreCase("CD")) || 
        (club.equals("stalbans") && pcw.equalsIgnoreCase("CAD")) || 
        (club.equals("demov4") && pcw.equalsIgnoreCase("CAD"))) {
        
        tmode_color = "yellow";
    }
    
    if (club.equals("peninsula")) {
        
        // their mship is based in place of mnum
        if (mnum.equals("Sports")) {
            tmode_color = "blue";
        } else if (mnum.equals("") && player.startsWith("Comp")) {
            tmode_color = "orange";
        } else if (mnum.equals("") && player.startsWith("Unacc")) {
            tmode_color = "green";
        } else if (mnum.equals("") && !player.equals("")) {
            tmode_color = "pink";
        }
    }
    
    out.print("<td bgcolor=\"" + tmode_color + "\" align=\"center\">");
    out.print("<font size=\"1\">");
    out.print(((!player.equals("")) && (!player.equalsIgnoreCase( "x" ))) ? pcw : "&nbsp;");
    out.println("</font></td>");
    
 }
 

 // *********************************************************
 //   Round the sales tax amount to dd.cc
 // *********************************************************
 //
 private double roundST(double salestax) {


    if (salestax != 0) {
      
       // round up or leave alone (cc.dd)
         
       salestax = (salestax * 100);       // increment for rounding to work

       salestax = Math.round(salestax);   // round to whole number

       salestax = (salestax / 100);       // convert back to dd.cc
    }  

    return(salestax);
 }

 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
     out.println(SystemUtils.HeadTitle("Database Error"));
     out.println("<BODY><CENTER>");
     out.println("<BR><BR><H1>Database Access Error</H1>");
     out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
     out.println("<BR>Please try again later.");
     out.println("<BR><br>Fatal Error: " + pMessage);
     out.println("<BR><br>Exception: " + pException);
     out.println("<BR><BR>If problem persists, contact customer support.");
     out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
     out.println("</CENTER></BODY></HTML>");
     out.close();
 }


 private void dumpSheet(HttpServletRequest req, HttpServletResponse resp) {


    resp.setContentType("text/csv");                         // text file
    resp.setHeader("Content-Disposition", "attachment;filename=\"teesheet.csv\"");         // default file name

    PrintWriter out = null;

    try { out = resp.getWriter();
    } catch (Exception ignore) {}

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement (
           "SELECT * " +
           "FROM teecurr2 " +
           "WHERE date = DATE_FORMAT(now(), '%Y%m%d') AND player1 <> '' " +
           "ORDER BY time");

        pstmt.clearParameters();
        //pstmt.setInt(1, slotParms.sheet_ids.get(i));
        rs = pstmt.executeQuery();

        while (rs.next()) {

            out.print("\"" + rs.getInt("yy") + "-" + rs.getInt("mm") + "-" + rs.getInt("dd") + " " + rs.getInt("hr") + ":" + Utilities.ensureDoubleDigit(rs.getInt("min")) + ":00\",");
            out.print("\"" + rs.getString("event") + "\",");
            out.print("\"" + rs.getString("player1") + "\",");
            out.print("\"" + rs.getString("player2") + "\",");
            out.print("\"" + rs.getString("player3") + "\",");
            out.print("\"" + rs.getString("player4") + "\",");
            out.print("\"" + rs.getString("player5") + "\",");
            out.print("\"" + rs.getString("p1cw") + "\",");
            out.print("\"" + rs.getString("p2cw") + "\",");
            out.print("\"" + rs.getString("p3cw") + "\",");
            out.print("\"" + rs.getString("p4cw") + "\",");
            out.print("\"" + rs.getString("p5cw") + "\",");
            out.print("\"" + rs.getString("mNum1") + "\",");
            out.print("\"" + rs.getString("mNum2") + "\",");
            out.print("\"" + rs.getString("mNum3") + "\",");
            out.print("\"" + rs.getString("mNum4") + "\",");
            out.print("\"" + rs.getString("mNum5") + "\",");
            out.print("\"" + rs.getInt("p91") + "\",");
            out.print("\"" + rs.getInt("p92") + "\",");
            out.print("\"" + rs.getInt("p93") + "\",");
            out.print("\"" + rs.getInt("p94") + "\",");
            out.print("\"" + rs.getInt("p95") + "\",");
            out.print("\"" + rs.getString("notes") + "\","); // need to esacpe double-quotes and ???
            out.print("\"" + rs.getInt("hideNotes") + "\",");
            out.println("\"" + rs.getString("orig_by") + "\"");
        }

    } catch (Exception exc) {

        Utilities.logError("Proshop_sheet.dumpSheet(): Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }

    return;

 }
 
}