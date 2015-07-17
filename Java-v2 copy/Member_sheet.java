
/***************************************************************************************
 *   Member_sheet:  This servlet will process the 'View Tee Sheet' request from
 *                    the Member's Select page.
 *
 *
 *   called by:  Member_select (doPost)
 *               Member_slot (via Member_jump on a cancel)
 *
 *
 *   created: 1/14/2002   Bob P.
 *
 *   NOTICE:  Customs for Sheet Generation must be done twice; once for old and once for new skin.
 * 
 *   last updated:        ******* keep this accurate *******
 *
 *        3/06/14   La Grange CC (lagrangecc) - Do not display the 5th player slot for members (case 2382).
 *        2/20/14   Chattanooga G & CC (chattanoogagcc) - Do not display the 5th player slot for members (case 2372).
 *        2/04/14   El Camino CC (elcaminocc) - Sort the tee sheet by fb first, then time (case 2362).
 *        1/27/14   Los Altos G & CC (lagcc) - Added custom to allow members access to the tee sheet at 7:00 on 3/15/2014 only (case 2353).
 *        1/24/14   Rio Verde CC (rioverdecc) - Commented out custom to hide 6:24 time.
 *        1/23/14   Rio Verde CC (rioverdecc) - Hide the 6:24am tee time since it's only there to keep the lottery button at the top of the page.
 *        1/09/14   May River GC (mayrivergc) - Added custom to hide the 5th player from the member tee sheet (case 2341).
 *       12/19/13   Philly Cricket Club (philcricket) - "Join Me" guest type will now be highlighted in lime on the tee sheet (case 2337).
 *       12/04/13   Updated Utilities.isAGCClub calls to use the new Utilities.isClubInGroup method instead.
 *       10/24/13   Champions Run (championsrun) - Added custom to restrict members from viewing any times on a day's tee sheet until those tee times are available for booking (case 2290).
 *       10/22/13   Added processing to hide member/guest names, or the entire tee time, based on the new Make Tee Time Private option as set by proshop users when booking tee times.
 *       10/15/13   If first tee time in a lottery has members booked skip it - wait for an empty tee time to display for the lottery button.
 *       10/09/13   Tee sheet will now properly handle the hiding of restriction colors when restriction lifting customs are being used.
 *        9/27/13   Governors Club (governorsclub) - Updated custom consecutive times to use the same processing for Sat as the other days (case 1930).
 *        9/23/13   Fixed positive handicaps so they show with the proper "+" ahead of them.
 *        9/17/13   Get member sub_type from the session instead of member2b.
 *        8/30/13   Brooklawn CC (brooklawn) - Updated 18 Holer ladies day to apply to Thursday for 9/5/13 only (case 1493).
 *        8/21/13   Desert Mountain (desertmountain) - Added restrictByOrig custom to allow members access to tee time if they are responsible for any of the players in the tee time.
 *        8/14/13   Bent Creek CC (bentcreek) - Added custom to display the 5th player on the member tee sheet, even if a 5-some restriction is present.
 *        7/24/13   Members will now be able to access tee times covered by a waitlist with an active waitlist signup covering those times if the tee time is partially 
 *                  filled, or if they are part of the tee time or its originator.
 *        7/08/13   Mira Vista CC (miravista) - Allow "18 Holer" mship access to Thursday morning times 3 weeks in advance (case 2278).
 *        6/20/13   Philly Cricket Club Recip Site (philcricketrecip) - Cut off member access to tee times 2 days in advance at 12:00pm.
 *        6/13/13   Fixed issue where 5some restriction coloring was not being displayed in the 5th player slot for the member side.
 *        6/13/13   Commented out old skin code in doPost to avoid confusion, since it is no longer used.
 *        6/12/13   Riviera CC (rivieracc) - 3-some times will now be applied to tee times between 8:02am and 10:58am on Tuesdays from 6/25/13 to 9/24/13 (except some specific dates) (case 2273). 
 *        6/12/13   Philadelphia Cricket Club (philcricket) - Updated recip button to have a yellow background to be more visible.
 *        6/04/13   Olympia Fields CC (olympiafieldscc) - Guest names will now be hidden unless the current user is the originator of a tee time, or a part of that tee time (case 2235).
 *        5/30/13   Philadelphia Cricket Club (philcricket) - add seamless link to their Recip site.
 *        5/28/13   Lakeside CC (lakesidecc) - Added custom to prevent members from accessing other members tee times if they aren't the originator or a part of the tee time (case 2269).
 *        5/28/13   Los Altos GCC (lagcc) - Updated custom to only allow frontside times again on Thursdays since they called back (case 1934).
 *        5/24/13   Estancia Club (estanciaclub) - Updated custom to hide 5th player slot starting 5/16 instead of 6/1 (case 1884).
 *        5/22/13   Overlake G&CC (overlakegcc) - Added custom to allow "18 Holer" msubtypes to access Tuesday and Friday morning tee times 7 days in advance (case 2259).
 *        5/22/13   Set the skip_waitlist flag if member does not have access to tee times for the selected date (prevents early waitlist entries).
 *        5/21/13   Mayfield Sand Ridge (mayfieldsr) - Updated 2-some time custom to only apply to the Sand Ridge course.
 *        5/15/13   Los Altos GCC (lagcc) - Updated custom to only allow access to backside times on Thursdays as well for women (case 1934).
 *        5/14/13   Mayfield Sand Ridge (mayfieldsr) - Updated 2-some time custom to only apply to the Sand Ridge course.
 *        5/08/13   Brookside CC (brooksidecountryclub) - Temporarily commented out custom until club is ready, can delete this when so (case 2251).
 *        5/06/13   Birmingham CC (bhamcc) - Added custom to set days in advance to 6 and make the tee sheet open up at 5:30pm on Thursdays for Adult Female members (case 2261).
 *        5/06/13   Brooklawn CC (brooklawn) - Updated customs for early event access with 2013 dates (case 1985)(case 2151)(case 2265).
 *        5/03/13   Brookside CC (brooksidecountryclub) - Added custom to allow "League" msubtype to access Tuesday, Wednesday, and Thursday (5/30/13 only) tee sheets up to 14 days in advance (standard = 7) (case 2251).
 *        5/01/13   Estancia Club (estanciaclub) - Updated custom to hide 5th player slot starting 6/1 instead of 5/16 (case 1884).
 *        4/26/13   Canterbury GC (canterburygc) - Updated custom to apply more than 14 days in advance, instead of 7, since their standard booking window has increased (case 1800).
 *        4/24/13   Add custom for Lakewood Ranch (possible new feature) to allow member to select the consecutive times before going to the tee sheet.
 *                  This will make it faster and easier to get multiple times (when they are racing for the popular times).  Value passed from Member_select.
 *        4/22/13   Add "weeks_in_selected_X" class to member_sheet_calendar to give css clues on how to avoid page "jumping" when rendering calendar
 *        4/18/13   Dove Canyon Club (dovecanyonclub) - Separated dovecanyonclub from AGC club processing since they are no longer managed by AGC.
 *        4/04/13   Elgin CC (elgincc) - Added custom to prevent Certified Junior mtypes from seeing the Lottery time slots, since they cannot sign themselves up for lotteries (case 2227).
 *        4/04/13   Elgin CC (elgincc) - Added custom to prevent Non Certified Junior mtypes from booking tee times on any day (other members can book them in to unrestricted times) (case 2227).
 *        4/04/13   Elgin CC (elgincc) - Allow members to book 365 days in advance for guest times ('Social Member' mship and 'Certified Junior'/'Non Certified Junior' mtypes not allowed to do this) (case 2226).
 *        4/04/13   Change the way the select_jump option from Member_select displays the tee sheet.  It will now completely skip the calendar and instructions so the
 *                  page does not have to adjust after it renders (this was too slow).
 *        4/01/13   Long Cove Club (longcove) - Updated custom to prevent consecutive booking with new dates for 2013 (case 1038).
 *        3/29/13   Increase the height of the instruction box to allow for the largest calendar possible (6 weeks).  This will prevent the tee sheet from
 *                  jumping when the calendar is completed.  The jumping occurs after the entire tee sheet is rendered which can interfere with the member's
 *                  tee time selection (when they are trying to click as fast as possible).
 *        3/28/13   Correct the style sheet info for member notices.
 *        3/26/13   If tee time is in use, leave it that way.  It was clearing the in_use indicator if owned by the user.  This allowed a member to access
 *                  the same tee time(s) from multiple tabs/windows.
 *        3/01/13   The National GC of Kansas City (thenationalgolfclub) - Do not display the 5th player slot for members (case 2232).
 *        2/07/13   CC of York (ccyork) - Do not display the 5th player slot for members (case 2224).
 *        2/05/13   Rehoboth Beach CC (rehobothbeachcc) - Do not display the 5th player slot for members (case 2222).
 *        1/29/13   Set up all demo sites to work with the Join Me guest type. Join Me guests will be highlighted on the tee sheet, and full times will be accessible if there is a Join Me guest in them.
 *        1/17/13   Indian Ridge CC - Add clock to page as a custom (also added for demo sites)
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *        1/03/13   Echo Lake CC (echolakecc) - Added call to determine custom twosome times (case 2204).
 *        1/02/13   Add multiple tee time requests to Mobile site for Desert Mountain (not finished - too much work required in slotm).
 *       12/27/12   Desert Mountain - do not allow Family Guest Play mship types to access any tee times.
 *       12/25/12   Updated consecutive drop-down to allow up to 15 tee times to be selected, based on the Club Options setting.
 *       12/07/12   Rolling Hills CC - CO (rhillscc) - Commented out the Friday "Friday Boys" custom access at the request of the club (case 1987).
 *       11/30/12   When getting the member notices only grab the ones for golf (activity_id=0).
 *       11/21/12   Add logic for hiding tee times booked via TSP
 *       11/19/12   Rolling Hills CC - CO (rhillscc) - Updated custom to expand the window for Friday Boys times to 7 days from 3 days (case 1987).
 *       11/08/12   Add processing for new event option (memedit) to allow members to access event times once they have been moved to the tee sheet. 
 *       10/17/12   Riviera CC (rivieracc) - Updated custom to allow "RWGA" member subtype access to certain Thursday morning times 14 day in advance (case 2152).
 *        9/27/12   Pinehurst CC (pinehurstcc) - Updated custom to restrict all mships from accessing Pfluger times until 1 day in advance at 6am (case 1766).
 *        9/06/12   Added some js to auto scroll page when select_jump is passed in from Member_select
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        8/29/12   Los Altos GC (lagcc) - Updated custom to allow Friday access the week of Labor day since Tuesday play is pushed back (case 1934).
 *        6/29/12   Monterey Peninsula CC (mpccpb) - Added custom to allow members to enter tee times that are full, but contain at least one "Need A Player" guest.
 *        6/28/12   Monterey Peninsula CC (mpccpb) - Added custom to highlight player slots containing the "Need A Player" guest type in lime.
 *        6/28/12   Brooklawn CC (brooklawn) - Updated Ladies 9 Holer custom to not apply on 7/4/12.
 *        6/22/12   Oakland Hills CC (oaklandhills) - Updated custom to fix an issue where nested conditionals were allowing members to access other members times (case 2166).
 *        6/12/12   Cherry Hills CC (cherryhills) - Updated custom processing to properly restrict and allow Suriving Spouse and Former Spouse members.
 *        6/06/12   Oakland Hills - add custom to display the names during the 18-Hole Ladies times (lottery) on Thursdays (case 2166).
 *        6/04/12   Interlachen CC (interlachen) - Added custom to allow "After Hours" subtype access to 5:00-6:30pm Wednesday times.  2 days in advance those times will open to everyone (case 2167).
 *        5/31/12   Rolling Hills CC - CO (rhillscc) - Commented out Wednesday GOM custom access at the request of the club (case 1987).
 *        5/30/12   Sankaty Head GC (sankatyheadgc) - Added custom to allow Primary/Spouse Female members access to 8-10 am times on 6/12, 7/17, 8/7, and 9/18 7 days in advance (case 2149).
 *        5/25/12   Teton Pines Resort (tetonpines) - Adjusted custom to hide "Resort" guests so that instead of displaying "Guest" it strips the guest type off and displays only the guest's name (case 2157).
 *        5/23/12   Brooklawn CC (brooklawn) - Updated early custom for early access with two additional dates (similar to case 1985).
 *        3/29/12   Riviera CC (rivieracc) - Added custom to allow all 'Adult Female' members access to Tuesdays 7 days in advance (case 2000).
 *        5/11/12   Teton Pines Resort (tetonpines) - Added custom to hide "Resort" guest types on the member tee sheet (case 2157).
 *        5/10/12   Riviera CC (rivieracc) - Added custom to allow women's "RWGA" member subtype access to Tues morning times 14 days in adv (case 2152).
 *        5/10/12   Huntingdon Valley CC (huntingdonvalleycc) - Updated verifySlot.checkHuntingdonValley() method calls to pass the course name as well (case 2139).
 *        5/04/12   Lakes CC (lakes) - Updated custom to hide 5th player position so that it applies from 11/1-5/14 instead of through 5/31.
 *        5/02/12   Brooklawn CC (brooklawn) - Allow access to tee times on 6/16/11 for Male members from 8:30 to 13:59 for tee time event (case 1985).
 *        4/25/12   Scioto CC (sciotocc) - Updated custom processing for spouse access with different days in advance.
 *        4/24/12   Oaklnad Hills - Added custom to only show Lottery button to members with a mship of '18-Hole Ladies'.
 *        4/20/12   Merion GC (merion) - Added custom notice to better inform members of why they can click days outside 7 days in adv, but can't select times.
 *        4/09/12   NCR CC (ncrcc) - Added custom to adjust the course colors so they are more distinct.
 *        4/06/12   Awbrey Glen GC (awbreyglen) - Added custom to hide the 5th player position from the member tee sheet.
 *        3/30/12   Look for a jump option from Member_select that user can select if they want to show more tee times on the tee sheet.
 *        3/29/12   Riviera CC (rivieracc) - Added custom to allow all 'Adult Female' members access to tee times 8:02am-10:58am Tuesdays, 7 days in advance (case 2000).
 *        3/29/12   Huntingdon Valley CC (huntingdonvalleycc) - Added call to determine custom twosome times (case 2139).
 *        3/26/12   Long Cove Club (longcove) - Uppdated custom to prevent consecutive booking with new dates for 2012 (case 1038).
 *        3/22/12   Los Altos GCC (lagcc) - Updated Ladies custom so that the 14 day early access custom cuts off on the day when their normal privileges kick in (1934).
 *        3/13/12   Correct custom coloring for "if multiple guests are allowed, make the submit button green"
 *        3/08/12   Congressional - update customs to reflect new course names.
 *        2/13/12   Claremont CC (claremontcc) - Commented out custom for giving Adult Females early access to Tuesday times (case 1361).
 *        1/25/12   Aliso Viejo CC (alisoviejo) - Added a custom to only hide member names if the tee time is full (case 2110).
 *        1/11/12   More updates for new skin buttons
 *        1/11/12   Updated html for new skin buttons
 *        1/11/12   Los Altos G&CC (lagcc) - Updated custom Ladies access so it properly blocks access to front-side times (was only fixed on new skin code previously) (case 1934).
 *       12/08/11   Updates for New skin Events modal windows
 *       12/05/11   Change Event buttons for new skin.  Add courseName to event SQL.
 *       12/05/11   Fix "lottery only" button for new skin;  Added notice comment for old and new skin customs (above)
 *       11/30/11   Los Altos G&CC (lagcc) - Updated custom Ladies access so it properly blocks access to front-side times (case 1934).
 *       11/30/11   More changes for new skin.
 *       11/23/11   Olympic Club (olyclub) - Updated checkOlyStarterTime() method calls to also pass the full date.
 *       11/22/11   Ocean Reef Club (oceanreef) - Updated verifyCustom.checkOceanReefMship method calls to also pass the time of the tee time (case 1731).
 *       11/11/11   Begin changes for new skin
 *       11/02/11   Rolling Hills CC - CO (rhillscc) - Updated Ladies custom to allow them access to specific times during the off-season as well (case 1866).
 *       11/01/11   The Plantation GC (theplantationgc) - Updated custom to open up times at 7:00am Pacific instead of 6:30am (case 2057).
 *       10/31/11   Fixed a couple customs that were using cal_time as though it were not adjusted to their local time.
 *       10/28/11   Wildcat Run CC (wildcatruncc) - Added restrictByOrig custom to allow members access to tee time if they are responsible for any of the players in the tee time.
 *       10/28/11   The Plantation GC (theplantationgc) - Added custom to prevent members from accessing tee times prior to 10:29am until 6:30am Pacific time, 1 day in advance (case 2057).
 *       10/26/11   Dellwood Hills GC (dellwood) - Highlight guest type "Join Me" on teesheet (case 2029).
 *       10/19/11   Governors Club (governorsclub) - Updated custom for number of consecutive times to apply different amounts between 11/1 and 3/15 on the Primary Rotation (case 1930).
 *       10/10/11   Los Altos G&CC (lagcc) - Updated custom to make Tuesday ladies day only apply to back-side tee times (case 1934).
 *       10/05/11   Capital City Club (capitalcityclub) - Don't allow Dependents to access the day's tee sheet until 8:30am (normal is 7:30am) (case 2031).
 *       10/05/11   Capital City Club (capitalcityclub) - Removed custom to prevent members from accessing non-Crabapple course times on weekends when viewing -ALL- (case 1955).
 *        9/30/11   Indian Ridge CC (indianridgecc) - Don't allow members access to lottery times after they've been processed until after 7:00am Pacific 7 days in advance (case 2039).
 *        9/27/11   Indian Ridge CC (indianridgecc) - Added custom to hide guest names for any time that contains at least one "Comp" guest type (case 2037).
 *        9/27/11   Indian Ridge CC (indianridgecc) - Use custom colors for courses when viewing all: Arroyo = "goldenrod" and Grove = "green" (case 2041).
 *        9/23/11   Los Altos G&CC (lagcc) - Updated ladies time custom to also allow 14 day access to Tuesday 8:00am-9:30am times (case 1934).
 *        9/07/11   Naperville CC (napervillecc) - Color days from 8-30 days in advance yellow on the calendar, and display a custom note message in the instructions box regarding advance guest times (case 2009).
 *        8/19/11   Forest Hills CC - IL (foresthillscc) - Added custom to hide 5th player positions on the tee sheets between 5/1 and 9/30 (case 2011).
 *        8/17/11   Cherry Valley CC (cherryvalleycc) - Added check for custom 2-some times (case 2018).
 *        8/11/11   Merion GC (merion) - Updated custom to allow "Non-Resident" members 365 days advance booking on weekends so the existing custom functions properly (case 1017).
 *        8/09/11   Congressional CC (congressional) - Reverted custom back to 29 days (30 for them), and custom will no longer apply to 40 minute times (case 1075).
 *        8/02/11   Cordillera (cordillera) - Remove custom starter times.
 *        7/14/11   Congressional CC (congressional) - Updated custom to allow bookings 9 (10 for them) days in advance instead of 29 (30 for them) (case 1075).
 *        7/12/11   Woodway CC (woodway) - Updated 2-some times custom to pass fb to the custom method as well (case 1053).
 *        7/11/11   Added button in event info window to bring member to the event signup page.  This will allow members to sign up for an event without leaving the tee sheet.
 *        7/11/11   Wollaston GC (wollastongc) - Removed custom to restrict members from accessing tee sheets for any day on Mondays (case 1819).
 *        7/06/11   Royal Oaks WA (royaloaks) - Updated Ladies Day custom to restrict access to Friday morning front side times until normal booking window starts at 7:30am (case 1684).
 *        7/01/11   Misquamicut Club (misquamicut) - Updated 2-some custom to call verifyCustom.checkMisquamicut2someTimes() instead of using local code (case 1996).
 *        6/30/11   Rolling Hills CC - CO (rhillscc) - Updated Wednesday Group (GOM) custom times to use a different time ranges for certain ranges of dates (case 1987).
 *        6/27/11   Minnetonka CC (minnetonkacc) - Added custom for 3-some times Wed/Thurs (case 1989).
 *        6/14/11   Misquamicut Club (misquamicut) - Added custom for 2-some times Sat/Sun/Holiday from 8:00am-8:30am (case 1996).
 *        6/14/11   Rolling Hills CC - CO (rhillscc) - Updated the time range for Wednesday Group (GOM) times (case 1987).
 *        6/13/11   Brooklawn CC (brooklawn) - Allow access to tee times on 7/16/11 for members from 7:00 to 13:00 for tee time event.
 *        6/09/11   Olympic Club (olyclub) - Allow up to 10 restrictions in legend.  Do not allow members to book on Cliffs Course.
 *        6/03/11   Rolling Hills CC - CO (rhillscc) - Member Subtype "GOM" will have access to Wed 11:57-12:33 times 3 days in advance, and member subtype "Friday Boys" will have access
 *                  to Fri 12:15-12:51 times 3 days in advance.  No one else will have access to these times at any time (case 1987).
 *        5/26/11   Olympic Club - check for canned starter times and do not allow access if tee time is saved for golf shop use.
 *        5/24/11   Scioto CC (sciotocc) - Adjusted days in advance that spouses have access to Friday tee times.
 *        5/20/11   Willow Ridge - do not show the Wait List button if there is a lottery and it hasn't yet been processed (members were getting on
 *                                 the wait list instead of the lottery).
 *        5/20/11   Piedmont - add Memorial Day and Labor Day to days when we check for 2-some times.
 *        5/12/11   Royal Montreal GC (rmgc) - Added custom to allow members access to empty lottery times 3 days in advance at 7:00am Eastern.
 *        5/12/11   Sonnenalp (sonnenalp) - Added custom for 3-some times (case 1978).
 *        5/12/11   Royal Montreal GC (rmgc) - Added custom for 3-some times based on course, date (even/odd), and weekday/weekend.
 *        5/12/11   Brooklawn CC (brooklawn) - Adjusted custom to also allow dependents (case 1985).
 *        5/10/11   Brooklawn CC (brooklawn) - Allow access to tee times on 6/18/11 for Male members from 9:00 to 13:59 for tee time event (case 1985).
 *        5/10/11   Royal Oaks WA (royaloaks) - Removed Wednesday from Ladies time custom (case 1684).
 *        5/04/11   Hiwan (hiwan) - Commented out custom to hide full times that the member is not a part of (case 1943).
 *        4/29/11   Remove Cordillera custom lodge restrictions for now.
 *        4/28/11   Congressional CC (congressional) - Include 40 minute times and 9:30am for advance guest times on the Jones Course (case 1075).
 *        4/25/11   Algonquin GC (algonquin) - Added custom to allow women's "9 Holer" member subtype access to Wed morning times 14 days in adv (case 1967).
 *        4/21/11   Minikahda (minikahda) - Updated custom guest day message.
 *        4/06/11   CC of Fairfax (ccfairfax) - Added custom to allow women's "18 Holer" and "9 Holer" member subtypes access to Wed morning times 14 days in adv (case 1960).
 *        4/01/11   Long Cove Club (longcove) - Updated custom to not allow members to book consecutive times between 4/18-4/24 (case 1038).
 *        3/30/11   Capital City Club (capitalcityclub) - Adjustments to custom processing (was not working) (case 1955).
 *        3/24/11   Capital City Club (capitalcityclub) - Custom to allow members access to the Crabapple course only at 7:30am, 3 days in advance for Fri/Sat/Sun (case 1955).
 *        3/16/11   Congressional (congressional) - Changed back to allowing bookings 29 days in advance since they do not count today when figuring days in advance (case 1075).
 *        3/08/11   Congressional (congressional) - Do not apply custom to 9:30am or 6:30pm (case 1075).
 *        3/01/11   Congressional (congressional) - Added Jones Course to guest day custom and changed it to allow bookings 30 days in advance instead of 29 (case 1075).
 *        2/28/11   Governors Club (governorsclub) - Updated custom times for Tuesday and Thursday on the Primary Rotation (case 1930).
 *        2/21/11   Governors Club (governorsclub) - fix for custom so that it's looking at the courseName of the tee time instead of the course being viewed (case 1930).
 *        2/18/11   Hiwan - do not show tee time if its full and member is not part of it (case 1943).
 *        2/09/11   Colorado GC (coloradogc) - Do not show the 5th player position or allow 5-somes on the members side (case 1938).
 *        2/03/11   Indian Hills CC (indianhills) - Allow "18 Holer" female members to access Tuesday morning times 14 days in advance 4/01-10/15 (case 1899).
 *        2/03/11   Tualatin CC (tualatincc) - Allow Primary/Spouse Female members to access Tuesday morning times 7 days in advance (case 1928).
 *        2/02/11   Los Altos G&CC (lagcc) - Allow female members to access Thursday morning times 14 days in advance (case 1934).
 *        1/26/11   Governors Club (governorsclub) - Restrict the number of consecutive times members can make based on course, date, and time. Display blank column if
 *                  only 1 consec time allowed so the tee sheet doesn't get mis-aligned (case 1930).
 *        1/20/11   Mission Viejo CC (missionviejo) - Do not allow single members to join an existing 4-some (case 1927).
 *        1/12/11   Remove 3-some custom for Meadow Club (case 1761).
 *        1/10/11   Ocean Reef CC (oceanreef) - do not check custom if restrictAll is set to true (cutoff time reached).
 *        1/07/11   Los Altos G&CC (lagcc) - Do not display middle initials on the tee sheet (case 1926).
 *        1/07/11   GAA Phoenix (gaaphoenix) - Added custom cutoff time message.
 *       12/06/10   The Lakes CC (lakes) - Remove custom preventing members from booking consecutive times Sat/Sun (case 1739).
 *       12/02/10   Mobile - add Mid-Day option.
 *       10/29/10   Royal Oaks WA (royaloaks) - Added date range to Ladies' times custom (case 1684).
 *       10/18/10   Mediterra (mediterra) - Allow up to 10 restrictions to be displayed on the tee sheet.
 *       10/15/10   The Estancia Club (estanciaclub) - Color days greater than 30 days in advance yellow on the calendar (case 1884).
 *       10/05/10   Pinehurst CC (pinehurstcountryclub) - Update custom to only allow Ladies access betweeen 4/1 and 9/30 (case 1766).
 *       10/04/10   Forest Highlands CC (foresthighlands) - Updated custom to hide 5th player positions to run off a month and day value, regardless of year (case 1613).
 *        9/28/10   Bellevue CC (bellevuecc) - Between 5/4 and 8/31, do not allow access to 3:50pm-6:10pm times on Tuesday until 9pm, 7 days in advance
 *                  (normal is 8am, 7 days in advance) (case 1854).
 *        9/24/10   The Estancia Club (estanciaclub) - Do not show 5th player position on the sheet between Oct 15th and May 15th (case 1884).
 *        9/16/10   Edina CC 2010 (edina2010) - Do not allow members to enter tee times on the 2010 site
 *        8/10/10   Changes to support passing encrypted tee time info to _slot page
 *        8/06/10   American Golf - If guest type is 'Platinum', strip the guest type from member view.
 *        8/02/10   Fort Collins CC (fortcollins) - Updated customs to include Fox Hill CC
 *        7/21/10   Rolling Hills CC - CO (rhillscc) - added support for 'Ladies' msubtype access to Tuesday and Thursday times 14 days in advance (case 1866).
 *        7/01/10   Cordillera - only show the Walk and Cart modes of trans so the members do not see all the billable modes (case 1864).
 *        6/29/10   Cherry Hills CC (cherryhills) - Update custom to apply to Mondays and 4th of July observance day instead of actual 4th
 *        6/16/10   Tahoe Donner - do not allow multiple tee times on weekend mornings (case 1845).
 *        6/10/10   Brooklawn CC (brooklawn) - Starting after 6:45 on 6/13/10, allow access to tee times on 7/17/09 for Male members from 7:00 to 14:00 for tee time event.
 *        5/18/10   Fix noAccessAfterLottery custom processing to check for lottery_color in teecurr to determine if lottery processed (case 1827).
 *        5/18/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        5/17/10   Colorado GC (coloradogc) - Do not display message to contact the golf shop for tee times when Monday and all times are restricted for the day.
 *        5/11/10   Royal Oaks WA - allow 9-Hole Women to book more days in adv for Friday mornings. (case 1843).
 *        4/27/10   Cordillera - restore custom for lodge times - new owners changed their minds. (case 1830)
 *        4/26/10   Brae Burn - Do not allow members to access tee times after a lottery is approved (case 1827).
 *        4/21/10   All clubs - Change the BG color for the F/B column to yellow if a Back 9 tee time (case 1796).  We decided to do this for all clubs.
 *        4/21/10   Ramsey - Move wait list custom to Proshop_dsheet - move start date after the lottery has been processed (case 1823).
 *        4/20/10   Oakley CC - Change the BG color for the F/B column to yellow if a Back 9 tee time (case 1796).
 *        4/20/10   Ramsey - process 3-some only times (case 1816).
 *        4/20/10   Bonnie Briar - do not allow members to access tee times after the lottery has been processed (case 1822).
 *        4/20/10   Ramsey - Ignore the wait list after the lottery has been processed (case 1823).
 *        4/19/10   Pinehurst CC (pinehurstcountryclub) - minor tweak to Ladies Days times so all women can access (case 1766).
 *        4/16/10   Wollaston GC - do not allow members to access tee times on Monday - all day (case 1819).
 *        4/15/10   Peninsula Club - display a custom message next to the Walk mode of trans in legend (case 1820).
 *        4/15/10   Do not display pro-only modes of trans in the legend (case 1820).
 *        4/07/10   Cherry Hills - do not allow Non-Resident members to use the Lottery (case 1661).
 *        4/06/10   Inverness Club (invernessclub) - Re-add custom to not look at days in advance for lotteries,
 *                  appears to have been removed accidentally (case 1095).
 *        4/06/10   Pinehurst CC (pinehurstcountryclub) - Updated custom to not block women from Ladies Days times (case 1766).
 *        4/01/10   Longue Vue - Added check for 2-some times (case 1798).
 *        3/30/10   Palo Alto Hills - pass club and msubtype in parmRest so 18 Hole ladies can access restricted times.
 *                                    Also, allow 18 hole ladies to access Thurs mornings 30 days in adv. (case 1785).
 *        3/30/10   Long Cove Club (longcove) - Updated custom to not allow members to book consecutive times between 4/14-4/18
 *        3/26/10   Canterbury - allow members to book guest times up to 6 mos in advance with some restrictions based
 *                               on their normal 7 days in advance starting at 8:00 AM (case 1800).
 *        3/25/10   Pinehurst CC (pinehurstcountryclub) - Updated custom to not block lottery times (case 1766).
 *        3/25/10   Woodway CC (woodway) - Uncommented 2-some time custom to be used again this season (case 1053).
 *        3/21/10   Central Washington Chapter PGA (cwcpga) - Display ghin numbers behing player names.
 *        3/12/10   Columbine CC (columbine) - Changed "hide 5th player" custom to not use the year when determining if 5th player should be hidden.
 *        2/22/10   Piedmont - if not during custom times, reset piedmontStatus variable to '0' instead of retaining the previous value.
 *        2/17/10   Philly Cricket - Remove custom to not allow members to access the 9-hole course - St Martins (case 1770).
 *        2/04/10   Cordillera - remove custom for lodge times (new owners don't want it).
 *        2/02/10   Pinehurst CC - Proprietary members can only access the Plfuger 9 course on the day of at 6:00 AM (case 1766).
 *        1/22/10   Oakmont - make an exception for one day in the custom that checks for shotgun events (per Jason Marciniec's request).
 *        1/13/10   TPC River's Bend (tpcriversbend) - Opt out of TPC custom that hides tee times with 4 players
 *        1/08/10   Meadow Club - check for 3-some times (case 1761).
 *        1/07/10   Philly Cricket - Do not allow members to access the 9-hole course - St Martins (case 1770).
 *       12/29/09   Indian Hills CC (indianhillscc) - Always use twoSomeTimes for new 'Hitting Room' course
 *       12/23/09   Jonathans Landing - change the 'Contact Golf Shop' message.
 *       12/23/09   Morgan Run (ClubCorp) - do not show the pro-only guest type values in the player slots, only the name (case 1763).
 *       12/15/09   Champion Hills - do not hide the 5th player when present and a 5-some restriction in place (case 1760).
 *       12/15/09   Player5 - display the name if it is an event time regardless of a 5-some restriction.
 *       12/09/09   When looking for events only check those that are active.
 *       12/04/09   Seacliff CC - add women ladies-day - more days in adv for Tues. (case 1750).
 *       12/03/09   Champion Hills (championhills) - Allow up to 10 events in the legend and on sheet
 *       12/02/09   Allow for Saudi time zone (if rolled ahead one day)
 *       11/29/09   Check for activity id = 0 when gathering events and restrictions.
 *       11/28/09   The Forest CC - allow up to 10 events in legend and on sheet.
 *       11/25/09   Change the event, restriction and lottery save areas from individual strings to string arrays
 *                  and use 'count' values to determine how many are allowed.  This will make it much easier to
 *                  customize the number that are displayed in the legend.
 *       11/09/09   Update checkTamariskAdvTime to check the days in advance and time of day.
 *       11/08/09   CC of Naples - Associate B mships can only access times after 12:30 in season (case 1704).
 *       11/05/09   Los Coyotes - check for Primary-Only times and if so, only allow Primary members access 3 days in advance (case 1740).
 *       11/05/09   The Lakes (lakes) - hide 5th player position between Nov 1 and May 31 (case 1656).
 *       10/28/09   The Lakes - do not allow multiple tee time requests on Sat or Sun (case 1739).
 *       10/21/09   Ocean Reef Club - from Oct 15 thru May 15 on the Dolphin Course, limit the tee time access based on the
 *                                    mship type of the user logged in (case 1731).
 *       10/07/09   Do not allow a Wait List to override a lottery if lottery is in state 2 (when members can register for lottery).
 *                  This came from Ben based on a problem reported by Mountaingate CC.
 *        9/30/09   Only display golf events and restrictions on the tee sheet
 *        9/01/09   Added check for Desert Forest 2-some times (case 1694).
 *        7/30/09   Sawgrass CC (sawgrass) - Don't display abbreviations portion of Tee Sheet Legend & when displaying -ALL- courses, order courses by the order they were entered into the database
 *        7/30/09   Add support for Mobile devices.
 *        7/15/09   For clubs with viewable days = bookable days in advance (same days in adv for ALL days of the week), allow members to load
 *                  a day's tee sheet even if they aren't able to book on that day yet, but do NOT display ANY of the tee times!!
 *                  This allows members to select this day on Member_select as the time rolls over without reloading the page, while still keeping
 *                  the teesheet hidden from view until the designated time.
 *        7/14/09   Desert Forest GC (desertforestgolfclub) - Do not display abbreviations portion of the Tee Sheet Legend (case 1702).
 *        7/10/09   Interlachen - always display CA for mode of trans when player is using a caddie (CA, CAA, CAB, or CAH).
 *        7/02/09   Tamarisk CC (tamariskcc) - Added check to allow access 7 days in advance (normal is 3) for times before 8:00am and after 11:07 on all days (case 1657).
 *        7/01/09   Woodway - remove 2-some custom.
 *        6/30/09   Brooklawn - allow for subtype of "9/18 Holer".
 *        6/29/09   Small change to previous lottery check (6/26 change)
 *        6/26/09   Added check so that lotteries that are approved but are still beyond the normal member booking window are not open to members
 *        6/15/09   Brooklawn - allow access to tee times on 7/18/09 for Male members from 7:00 to 14:00 for tee time event.
 *        6/08/09   Hudson National - Make wait list legend button do nothing when clicked.
 *        6/02/09   Royal Oaks WA - allow women to book more days in adv for Tues mornings. (case 1684).
 *        6/02/09   Medinah CC - remove time range for custom to hide tee times completely - per Mike Scully's request.
 *        5/19/09   Medinah CC - change time range for custom to hide tee times - per Mike Scully's request.
 *        5/15/09   TPC Wakefield - do not block tee times from members (undo case 1581 for this club).
 *        5/06/09   If the days in advance equals the days to view, use the time value to determine if member can view the sheet.
 *        5/05/09   Medinah CC - remove custom days in advance (case 1673).
 *        4/30/09   Pass the today's adjusted date to Member_jump to allow for other time zones around midnight.
 *        4/29/09   TPC (all) - allow for unaccompanied guest times in custom - if member originated the
 *                              tee time, then allow them access.
 *        4/27/09   Merion - allow access to Memorial Day and Labor Day 365 days in advance.
 *        4/22/09   Cherry Hills - do not allow Resident Emeritus mship types to submit Lottery Reqs.
 *        4/21/09   Brooklawn - add women 9 holers - more days in adv for Wed. (case 1637).
 *        4/15/09   Added check for Mayfield Sand Ridge 2-some times
 *        4/07/09   TPC TC - show full tee times if no guests included (overrides case 1581).
 *        4/03/09   Medinah - Remove 7:36 and 9:00 from Member Times list on weekdays, change 2:00 on weekdays
 *                            to an Outside Time - all on Course 3 (case 1631).
 *        4/03/09   Columbine CC - Hide the 5th player position on tee sheet between 4/01 and 9/30 (case 1640).
 *        3/31/09   Fixed another bug with calendars affecting max days
 *                  (if you jump ahead 1 month, then jump back 1 month you may not be on the same day!)
 *        3/27/09   Southview - Highlight guest type "Join Me" on teesheet (case 1624).
 *        3/24/09   Forest Highlands - Hide the 5th player position on tee sheet between 4/25 and 9/30 (case 1613).
 *        3/19/09   Only allow 3-somes during certain times for Chartwell GCC (case 1554).
 *        3/12/09   Adjustment to how member notices are displayed to reduce teesheet stretching
 *        3/11/09   Long Cove - Custom to not allow consecutive times from 4/13 - 4/19 (case 1038).
 *        3/04/09   Fixed bug with calendars (was using CST date not adjusted time/date)
 *        2/10/09   Fixed bug with calendars (max days not always correct)
 *        2/03/09   Removed rounding of the handicap value that is display on the tee sheet (req. by DiabloCC)
 *        1/22/09   Green Hills CC - Adjusted access time for the case 1574 custom.
 *        1/02/09   Timarron CC - add custom days in advance to view tee sheets (case 1595).
 *       12/31/08   Green Hills CC - add women ladies-day - more days in adv for Thurs. (case 1574).
 *       12/18/08   When checking if ok to display drop-down for constimesm ignore lottery if in state 5.
 *       12/05/08   All TPC clubs - do not show the tee time if it is full and not an event time (case 1581).
 *       12/05/08   Timarron CC (ClubCorp) - remove custom to not show the guest type values in the player slots (case 1591).
 *       12/01/08   Timarron CC (ClubCorp) - do not show the guest type values in the player slots, only the name (case 1591).
 *       11/25/08   Changed format for checking suspensions for mrest display on the tee sheet legend.
 *       11/21/08   Rivercrest - remove custom 3-some restrictions - similar to 2-somes (case 1492).
 *       10/21/08   Add support for new wait list option 'Member Access to Tee Sheet'
 *       10/03/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        9/26/08   Medinah - do not show any tee times that are populated where the user is not part of it (case 1553).
 *        9/24/08   Fix wait list button not showing when first wait list time is blocked
 *        9/09/08   Added member notice display banner for member notices using the teesheet option.
 *        8/25/08   Portland GC - check for Walk-Up Only times (case 1527).
 *        8/18/08   Sharon Heights - add women 18 holers - more days in adv for Tues. (case 1533).
 *        8/11/08   Stonebridge Ranch - limit the course options based on mship type (case 1529).
 *        7/29/08   Baltusrol - do not hide tee times that member is part of (Case 1519).
 *        7/11/08   Custom for baltusrolgc - hide ANY non-event tee time on ANY day that has ANY number of players in it (Case #1519)
 *        7/07/08   Admirals Cove - Added custom to default to -ALL- courses. This now uses new fields in the club5
 *                  table for determining the default course.  Still a custom though as no config available yet.  (case 1513).
 *        7/01/08   Seville (ClubCorp) - do not show the guest type values in the player slots, only the name (case 1508).
 *        6/26/08   Do not include Season Long events in the legend.
 *        6/26/08   Always display the course name column for multi-course clubs (case 1509).
 *        6/19/08   Congressional - adjust some customs to allow for member selecting course=ALL.
 *        6/11/08   Remove customs for restricting access the day of (new feature replaces these).
 *        6/11/08   Updates for wait list feature.
 *        6/05/08   Rivercrest - add custom 3-some restrictions - similar to 2-somes (case 1492).
 *        6/02/08   Brooklawn - add women 18 holers - more days in adv for Tues. (case 1493).
 *        5/14/08   Add new feature to cutoff member access to today's tee sheet.  This is to replace the
 *                  many customs added for this over the years.  (case 1480).
 *        5/04/08   Chartwell - Restrict all tee times for today
 *        4/22/08   Change to fix potential problem dayInAdv on calendar
 *        4/18/08   Claremont CC - change days in adv parms for Adult Females on Tues (case 1361).
 *        4/07/08   Johns Island - Restrict access to today's tee sheet
 *        4/07/08   Oak Hill CC - Change days in adv from 30 to 31 (update to case 1422).
 *        4/02/08   Oak Hill CC - Restrict access to today's tee sheet (case 1422).
 *        4/02/08   Oak Hill CC - do not allow member access starting June 1st if more than 30 days in advance.
 *                                Days prior to june 1st can be made any time of the year (case 1423).
 *        4/02/08   Oak Hill CC - set default course to East Course (case 1433).
 *        4/01/08   Inverness Club - change the date check for tee time access from yyyymmdd to mmdd.
 *        3/27/08   Bentwater - change the time when we cutoff access to tee times (case 1367).
 *        3/07/08   Mesa Verde - do not show the guest types or guest names, but show member names (case 1416).
 *        3/06/08   Cordillera - change custom lodge time check to change bgcolor even when allow=false already set.
 *        3/03/08   Long Cove - do not allow multiple tee times 4/14 - 4/20 for Heritage Classic event (case 1038).
 *        2/22/08   Birmingham CC - expand number of restrictions displayed from 8 to 10.
 *        2/08/08   Robert Trent Jones - do not show the 5th player column to members ever (case #1387).
 *        2/08/08   Valley Country Club - Remove custom to allow ladies to access Fridays 2 weeks in advance.  (Case #1388)
 *        2/05/05   CC of Birmingham - show up to 8 restrictions in the legend.
 *        1/29/08   CC of Jackson - Set default course - Case #1373
 *        1/28/08   Bentwater - Restrict access to today's tee sheet
 *        1/09/08   Merion - only allow members to access the East course.
 *        1/07/08   Jonathans Landing - Removed days in advance custom #1328
 *        1/03/08   Bears Paw - Restrict access to today's tee sheet
 *       12/18/07   Fixed case issue when checking orig_by field
 *       12/17/07   Scarsdale GC - Restrict access to today's tee sheet
 *       12/17/07   Mediterra - Added custom to default to -ALL- courses (Case #1343)
 *       12/13/07   Changed the interactive calendar (2nd one) to default to the month/year of the tee sheet
 *       12/12/07   Plantation at Ponte Vedra - force members to select the number of tee times (add 0 to consecutive count).
 *       12/12/07   Submit the tee time form immediately when the number of tee consecutive tee times is selected (drop-down list).
 *       12/05/07   Jonathan's Landing - if days in advance is 6 block access to each course except Hills - Case# 1328
 *       12/04/07   Jonathans Landing - Added custom to default to -ALL- courses (Case #1338)
 *       12/04/07   Berkeley Hall - Restrict access to today's tee sheet
 *       11/06/07   Tualantin CC - Restrict access to today's tee sheet
 *       11/06/07   Imperial GC - Added to default to -ALL- courses list
 *       10/26/07   Iron Wood CC - restrict access to todays sheet at 6:30 AM (case #1299).
 *       10/15/07   Colleton River Club - restrict access to current day at midnight the day of.
 *       10/14/07   Shadow Ridge - Show the word "Member" on the tee sheets instead of Member/Guest (Case #1266)
 *       10/03/07   Claremont CC - Add check for twosome times  (Case #1281)
 *        9/21/07   Shadow Ridge - Show the word "Player" on the tee sheets instead of Member/Guest (Case #1266)
 *        8/29/07   Palm Valley CC - restrict access to current day at midnight the day of.
 *        8/28/07   Add new parm (newreq) for call to _slot so we know if request is for a new tee time.
 *        8/22/07   Cherry Hills - Display handicap values as integers (no decimal)
 *        8/21/07   Congressional - do not restrict 2-some times on Tues on Club Course Gold.
 *        8/13/07   Los Coyotes - change the days in adv for Secondary Members from to 3 days  (Case #1191)
 *        8/12/07   Medinah CC - if Monday (today) and days in adv = 30, change to 29  (Case #1225)
 *        8/04/07   Valley Country Club - Allow ladies to make select times on Fri mornings 2 weeks in advance.  (Case #1160)
 *        7/31/07   Black Diamond Ranch - Added to default to -ALL- courses list
 *        7/26/07   Valley Club - Added to default to -ALL- courses list
 *        7/24/07   Inverness Club - do not hide names on w/e's or holidays (case 1223).
 *        7/16/07   If hide names selected, then do not allow member access to times with any players (case 1210).
 *                  This used to check for members in the tee time - now check for any player.
 *        6/21/07   New Canaan - add 2-some times on weekends and holidays (case 1174).
 *        6/12/07   Pass the displayed course name (courseName1) to _slot so it can return to same course (or ALL).
 *        6/08/07   Los Coyotes - restrict access to current day at 5:00 AM the day of (case #1186).
 *                                Also, default to -ALL- courses (case #1187).
 *        6/07/07   Congressional - replace specified member names with "Member" to hide them (case #1184).
 *        5/31/07   Sonnenalp - display the guest rates next to the guest name (case #1070).
 *        5/31/07   Los Coyotes - List the courses in the order they were specified when course = -ALL- (case #1172).
 *        5/24/07   Congressional - do not display the 'Contact Golf Shop' message (case #1176).
 *        5/23/07   Milwaukee - make some adjustments to calendar custom..
 *        5/22/07   Remove some customs for days to view tee sheets.
 *        5/11/07   Sugar Mill - restrict access to current day at 7:00 AM the day of.
 *        5/09/07   DaysAdv array no longer stored in session block - Using call to SystemUtils.daysInAdv
 *        5/02/07   Desert Highlands - restrict access at 5:00 PM the previous day.
 *        4/30/07   Upgraded calendars to version 4.0 - now all clubs use dynamic calenders
 *        4/26/07   Northland CC - use 365 day calendars and allow tee times 365 days in advance.
 *        4/25/07   Minikahda - Allow guest times up to 7 days in advance - 3 days for normal times (case #1027).
 *        4/25/07   Congressional - pass the date for the course Name Labeling.
 *        4/24/07   Greenwich CC - do not allow members to access 2-some times if they are full (case 1121).
 *        4/21/07   Colorado GC - do not allow members to access tee times on the day of at midnight.
 *        4/17/07   Changed courseName order by for Edison Club.
 *        4/12/07   Greenwich - do not allow members to access tee times on the day of (today) at midnight (case 1122).
 *        4/11/07   The Congressional - allow members to access specific times on the Open Course for guest times (case #1075).
 *        4/10/07   The Congressional - do not allow members to access any times when the course is the Club Course Gold (case #1071).
 *        4/10/07   The Congressional - do not allow members to access 2-some times if they are full (case 1060).
 *        4/05/07   Sunset Ridge - do not allow members to access tee times on the day of (today) at midnight (case 1106).
 *        4/05/07   The International - Added custom to default to -ALL- courses.
 *        4/04/07   The CC - do not allow members to access 2-some times if they are full (case 1085).
 *        4/04/07   The CC - do not allow members to access tee times on the day of (today) at midnight (case 1085).
 *        4/02/07   Pinery - Added custom to default to -ALL- courses.
 *        4/02/07   Brantford - do not allow members to access tee times on the day of (today) at midnight (case 1039).
 *        4/02/07   CC of Virginia - do not allow members to access tee times on the day of (today) at midnight (case 1040).
 *        4/02/07   Inverness Club - members can access all configured lotteries regardless of days in advance settting (case 1095)
 *        3/31/07   Inverness Club - use 365 day calendars && If weekday can book through Oct. 31st.
 *        3/28/07   Inverness GC - do not allow members to access tee times on the day of (today) at midnight (case 1065).
 *        3/28/07   Green Gables - do not allow members to access tee times on the day of (today) at 5 AM (case 1063).
 *        3/28/07   Congressional - do not allow members to access tee times on the day of (today) at 5 AM (case 1076).
 *        3/28/07   Black Diamond - do not allow members to access tee times on the day of (today) at 7 AM (case 1077).
 *        3/25/07   Add temp custom for Long Cove for thier Heritage Classic event (Case# 00001038)
 *        3/20/07   Congressional - abstract the course name depending on the day (Course Name Labeling, case #1046))
 *        3/01/07   Custom for Fox Hill to override parm.constimesm to 4 on the weekdays
 *        2/14/07   Mission Viejo - custom, only allow 10 days in advance for members to view tee sheets.
 *        2/07/07   Fort Collins/Greeley - do not allow the -ALL- course option, and
 *                  list the appropriate course first based on which club the user is from.
 *        1/30/07   Added custom for Black Diamond Ranch - show up to 8 events and 8 restrictions.
 *        1/24/07   Peninsula Club - use 365 day calendars for members.
 *        1/18/07   Cordillera - change the date checks for custom rest's.
 *        1/11/07   El Niguel - change days in adv parms for Adult Females on Tues & Thurs.
 *        1/04/07   Peninsula Club - shut off tee times the day of at midnight.
 *        1/04/07   Royal Oaks Houston - use 14 day calendars, and shut off tee times the day of at 6 AM.
 *       12/28/06   Oakmont CC - change some of the Wed & Fri guest times for 2007.
 *       12/13/06   Pinehurst - do not allow members to access tee times on the day of (today).
 *       12/11/06   Cherry Hills - custom for 'Resident Emeritus' mship types.
 *       12/04/06   Belfair - do not allow members to access tee times on the day of (today).
 *       11/20/06   Pinnacle Peak - do not allow members to access tee times on the day of (today).
 *       11/13/06   Disabled Santa Ana Womens retriction - per Larry
 *       11/08/06   Columbia-Edgewater - custom days in adv for Spouse member types.
 *       10/18/06   Westchester - Use 90 day calendars.
 *       10/17/06   Blackstone - do not allow members to access tee times the day of.
 *       10/11/06   Blackstone - modify the hide names feature (like Forest Highlands).
 *       10/01/06   Cherry Hills - change custom to only be effective duting normal season.
 *        8/21/06   Added custom to default to -ALL- courses for Fairbanks Ranch
 *        8/10/06   Added custom for Balimore to cut off tee times for tomorrow tee sheet at 6pm today
 *        8/09/06   Added custom to default to -ALL- courses for Lakewood Ranch and Pelican's Nest
 *        7/31/06   Bonnie Brair - do not allow members to access tee times on the day of (today).
 *        7/19/05   Custom for Merion - do not hide names if user is Steven Smith (user = S1).
 *        7/16/05   Custom for Santa Ana Women - change the time for days in advance for Tues and Fri from 7:30 to midnight.
 *        6/28/06   Desert Highlands - put the word 'Player' in player slots when tee time is full.
 *                                     Also, only view 7 days in advance, not 30.
 *        6/28/06   Cherry Hills - change custom to check holidays before days of the week. Also, check 7/04 not 7/03.
 *        6/14/06   scioto - custom days in adv for Spouse member types.
 *        5/30/06   Added support for displaying shotgun hole assignments and event start time
 *        5/23/06   Westchester - do not allow members to access tee times starting at 6:00 PM the day before.
 *        5/20/06   Init the twosomeonly flag on each tee time.
 *        5/19/06   TPC-TC - allow Corporate mships to make 2 tee times at once.
 *        5/17/06   Wee Burn - do not allow members to access tee times on the day of (today).
 *        5/11/06   Forest Highlands - display 'Player' in place of X.
 *        4/26/06   Do not allow members to access tee times if they are 2-some only times and full.
 *        4/20/06   Cordillera - do not allow members to access tee times on the day of (today).
 *        4/19/06   Fix the tests for lottery state - use the correct time values.
 *        4/18/06   Nakoma - Use 90 day calendars.
 *        4/17/06   Medinah - change special times (member times, etc.) per Medinah's instructions.
 *        4/17/06   Medinah - remove ARR processing per Medinah's instructions.
 *        4/14/06   Change calls to Member_slot from doGet to doPost calls - security reasons.
 *        4/14/06   Bearpath - do not allow members to access tee times on the day of (today).
 *        4/13/06   Inverness - Use 90 day calendars.
 *        4/12/06   Oakmont CC - do not allow members to make/change times on weekends more than 14 days
 *                               in advance (they changed their minds on this again).
 *        4/11/06   Catamount Ranch - use 365 day calendars and allow members to book 180 days in advance.
 *        4/05/06   New Canaan & Des Moines - do not allow members to access tee times within 3 hours of the tee time (today).
 *        3/30/06   Forest Highlands - modify the hide names feature.
 *        3/28/06   North Hills - do not allow members to access tee times on the day of (today).
 *        3/15/06   CC of the Rockies - use 365 day calendars and allow members to book all year.
 *        3/15/06   Bearpath - color code the member-only times.
 *        3/14/06   Tamarack - do not allow members to access tee times on the day of (today).
 *        3/14/06   Oakland Hills - use 365 day calendars and allow members to book certain times beyond the 8 days in adv limit.
 *        3/02/06   Oakmont - change the special Wed. guest times for Sept.
 *        3/01/06   North Hills - allow 30 days in advance for guest times.
 *        2/28/06   Merion - do not show member and guest names whenever there is a guest in the tee time.
 *        2/23/06   Merion - use 365 day calendars.
 *        2/03/06   Cordillera - add custom member restrictions that correspond to the custom hotel restrictions (checkCordillera).
 *        2/01/06   Cordillera - remove custom to change days in advance based on course.
 *        1/30/06   Do not allow member to access a tee time if not part of it when names are not displayed (config option).
 *       11/18/05   Ritz-Carlton - use 365 day calendars.
 *       11/04/05   Cherry Hills - custom member restrictions.
 *       10/31/05   Potowomut - do not allow members to access tee times on the day of (today).
 *       10/26/05   Send lottery requests to Member_mlottery so member can select to view other request or start a new request.
 *       10/24/05   Allow for 20 courses and -ALL- for multi course clubs.
 *       10/16/05   Meadow Springs - do not allow multiple tee time requests on Fri, Sat or Sun.
 *       10/03/05   Add 'div' tag to form for drop-down course selector so main menus will show over the drop-down box.
 *        9/08/05   Mission Viejo - do not allow members to access tee times on the day of (today).
 *        9/08/05   If days in adv is zero and the current time is less than the adv time, do not allow access.
 *        9/06/05   Hartefeld National - do not allow members to access tee times on the day of (today).
 *        7/20/05   Medinah - change ARR members from 30 days adv to 1 month adv.
 *        7/20/05   Forest Highlands - custom, only allow 5 days in advance for members to view tee sheets.
 *        7/12/05   Oakmont CC - do not allow members to make/change times on weekends more than 7 days
 *                               in advance (they changed their minds on this).
 *        7/07/05   Forest Highlands & PFC - do not allow members to access tee times on the day of (today).
 *        6/27/05   Do not allow multiple tee time request if F/B is not front or back.
 *        6/08/05   Cordillera - check member number and course to determine days in advance.
 *        5/17/05   Medinah CC - add custom to check for 'Member Times' (walk-up times).
 *                             - members can book times 7 days in advance on Course #2.
 *                             - Some members use Advanced Res Rights (ARR) to book 30 days in adv on #1 & #3.
 *        5/04/05   Pass player values to slot so it can verify that tee time has not changed.
 *        5/01/05   Add counters for # of tee sheets - in SystemUtils.
 *        4/26/05   Custom for Santa Ana Women - Increase days in advance for Tues and Fri.
 *        4/20/05   RDP Add global Gzip counters to count number of tee sheets using gzip.
 *        4/18/05   RDP Inverness - do not display the Itinerary on events if signup = no.
 *        3/25/05   RDP Custom for Oakmont and Saucon Valley - use 365 day calendars.
 *        3/27/05   Change the Milwaukee custom code to color the guest times even if within 7 days.
 *        3/22/05   Ver 5 - add course column shading to tee sheet printing when displaying all courses
 *        2/18/05   Do not display lottery in legend or use color on sheet if state = 5 (processed).
 *        1/20/05   RDP Correct the way the days in advance is adjusted for the current time.
 *        1/18/05   Ver 5 - display up to 4 events in the legend.
 *        1/10/05   Oakmont CC - change some of the Wed guest times for April, Sept & Oct.
 *        1/05/05   Ver 5 - allow member to make up to 5 consecutive tee times at once.
 *       12/09/04   Ver 5 - Add new club option to hide players' names on the tee sheet.
 *       11/29/04   Milwaukee CC - add checks for special tee times that allow Multiple Guests per member.
 *       11/17/04   Oakmont CC - add checks for special tee times that allow Multiple Guests per member.
 *       10/06/04   Ver 5 - add sub-menu support.
 *        9/22/04   RDP Add special processing for Oakmont - allow 90 days in advance for guest times.
 *        9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *        8/20/04   Add fivesAll option to indicate if 5-somes are supported on any course (course=ALL).
 *        7/07/04   Get the events, restrictions & lotteries in order for the legend display.
 *        6/30/04   Custom change for Old Oaks.  If lottery for date requested and state is 2,
 *                  then only display the lottery button - no tee sheet.
 *        6/17/04   Change the way we use gzip - use byte buffer so the length of output is
 *                  set properly.  Fixes problem with Compuserve 7.0.
 *        5/27/04   Add support for 4 lotteries per tee sheet.
 *        5/24/04   Change call to Member_slot a Get instead of a Post to allow 'Be Patient' page.
 *        5/06/04   RDP Make legend items (events, restrictions, lotteries) buttons so user
 *                  can click to view pop-up window describing the item.
 *        5/03/04   RDP Add an 'ALL' option for multiple course facilities.
 *        4/22/04   RDP Add custom processing for Hazeltine Natl.  Allow women 14 days in adv.
 *        3/17/04   Remove the 'jump' javascript as it will not work on MACs with IE.
 *        2/25/04   Allow access to full tee time if member is associated with unaccompanied guests.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/21/04   RDP Allow for 'Days in Adv' parms to be based on membership type.
 *        1/11/04   JAG Modified to match new color scheme
 *       12/15/02   Bob P.   Do not show member restriction in legend if show=no.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add Lottery processing.
 *       12/20/02   Bob P.   V2 Changes.
 *                           Add support for 'courseName' parm - select tee times for specific course.
 *                           Add support for Tee Time 'Blockers'.
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
import org.apache.commons.lang.*;

import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.parmClub;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.getWaitList;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.medinahCustom;
import com.foretees.common.ProcessConstants;
import com.foretees.common.cordilleraCustom;
import com.foretees.common.congressionalCustom;
import com.foretees.common.parmItem;
import com.foretees.common.getItem;
import com.foretees.common.Utilities;
import com.foretees.common.BigDate;
import com.foretees.common.Connect;

public class Member_sheet extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
    //
    //  Holidays
    //
    private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
    private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
    private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
    private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day

    //*****************************************************
    // Process the return from Member_slot
    //*****************************************************
    //
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        doPost(req, resp);      // call doPost processing
    }

    //*****************************************************
    // Process the request from Member_select
    //*****************************************************
    //
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        //
        //  Prevent caching so sessions are not mangled
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        //resp.setHeader("Expires", "-1"); // Try to work-around IOS5 caching bug
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        PrintWriter out;


        PreparedStatement pstmt1 = null;
        Statement stmt = null;
        ResultSet rs = null;

        ByteArrayOutputStream buf = null;
        String encodings = "";               // browser encodings

        boolean Gzip = false;        // default = no gzip

       // if (req.getParameter("event") != null) {

            out = resp.getWriter();                                         // normal output stream
            /*
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
             * 
             */

        HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

        if (session == null) {

            return;
        }

        Connection con = SystemUtils.getCon(session);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"ccccaa\"><CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            if (Gzip == true) {
                resp.setContentLength(buf.size());                 // set output length
                resp.getOutputStream().write(buf.toByteArray());
            }
            return;
        }

        //
        //  Get this user's full name & username
        //
        String full_name = (String) session.getAttribute("name");
        String user = (String) session.getAttribute("user");
        String caller = (String) session.getAttribute("caller");
        String club = (String) session.getAttribute("club");              // get club name
        String mship = (String) session.getAttribute("mship");            // get member's mship type
        String mtype = (String) session.getAttribute("mtype");            // get member type
        String msubtype = (String)session.getAttribute("msubtype");       // get member's sub_type
        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
        int activity_id = (Integer) session.getAttribute("activity_id");

        // DEBUG CODE - TESTING CON
        if (Connect.testConnection(con) == false) {
            Utilities.logError("Bad connection detected within Member_sheet.doPost[657] - was just pulled from seesion. user=" + user + ", club=" + club);
            con = Connect.ensureConnection(club, con);
            if (con == null) {
                Utilities.logErrorTxt("v5", "Could not establish db connection within Member_sheet.doPost[660] - ensureConnection failed! user=" + user + ", club=" + club);
                Utilities.logError("Could not establish db connection within Member_sheet.doPost[661] - ensureConnection failed! user=" + user + ", club=" + club);
            }
        }

        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        //
        //  See if Mobile user
        //
        int mobile = 0;              // Mobile user

        try {
            mobile = (Integer) session.getAttribute("mobile");
        } catch (Exception ignore) {
            mobile = 0;
        }


        // Setup the daysArray
        DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'
        daysArray = SystemUtils.daysInAdv(daysArray, club, mship, mtype, user, con);

        //
        //  First, check for Event call - user clicked on an event in the Legend
        //
        if (req.getParameter("event") != null) {

            String eventName = req.getParameter("event");

            displayEvent(eventName, club, out, con);             // display the information
            return;
        }

        // DEBUG CODE - TESTING CON
        if (Connect.testConnection(con) == false) {
            Utilities.logError("Bad connection detected within Member_sheet.doPost[696] - was just used. user=" + user + ", club=" + club);
            con = Connect.ensureConnection(club, con);
            if (con == null) {
                Utilities.logErrorTxt("v5", "Could not establish db connection within Member_sheet.doPost[699] - ensureConnection failed! user=" + user + ", club=" + club);
                Utilities.logError("Could not establish db connection within Member_sheet.doPost[700] - ensureConnection failed! user=" + user + ", club=" + club);
            }
        }
        
        
        //
        //   Check this user's session for any residue tee times and release them/clear them if found
        //
        verifySlot.clearSessionTimes(user, club, con, session);     // logs any tee times found in the v5 debug log
        
        
        
        //  This will log all parms for testing purposes !!!!!!!!!     
        /*
        Enumeration enum9 = req.getParameterNames();
        String parmString = "";
        while (enum9.hasMoreElements()) {

            String enumname = (String) enum9.nextElement();
            String values[] = req.getParameterValues(enumname);
            if (values != null) {
                for (int i=0; i<values.length; i++) {

                    parmString += "  " +enumname+ "=" +values[i];
                }
            }
        }
        Utilities.logDebug("BP", "Member_sheet parm test.  Parms = " + parmString + ".", con);
        * 
        */
       
        
        
        

        String[] day_table = {"inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        String[] mm_table = {"inv", "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

        //
        //  Num of days in each month
        //
        int[] numDays_table = {0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        //
        //  Num of days in Feb indexed by year starting with 2000 - 2040
        //
        int[] feb_table = {29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, +28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29};

        int i = 0;
        int hr = 0;
        int min = 0;
        int tee_time = 0;
        int year = 0;
        int month = 0;
        int day = 0;
        int day_num = 0;
        int week_count = 0;
        int type = 0;                       // event type
        int shotgun = 1;                    // event type = shotgun
        int in_use;
        //int Max7 = 7;                // days in advance for Medinah Course #2 !!!!!!
        int Max14 = 14;              // days in advance for Oakmont
        int piedmontStatus = 0;      // custom piedmont status
        int memedit = 0;
        short fb = 0;

        String name = "";
        String courseNameT = "";
        //String lastCourse = "";
        String submit = "";
        String event = "";
        String ecolor = "";
        String rest = "";
        String rcolor = "";
        String rest5 = "";
        String bgcolor5 = "";
        String player = "";
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
        String userg1 = "";
        String userg2 = "";
        String userg3 = "";
        String userg4 = "";
        String userg5 = "";
        String user1 = "";
        String user2 = "";
        String user3 = "";
        String user4 = "";
        String user5 = "";
        String orig_by = "";
        String orig1 = "";
        String orig2 = "";
        String orig3 = "";
        String orig4 = "";
        String orig5 = "";
        String ampm = "";
        //String event_rest = "";
        String bgcolor = "";
        String sfb = "";
        String hole = "";              // hole assignment
        String oldbgcolor = "";
        String custom_disp1 = "";      // custom display values
        String custom_disp2 = "";
        String custom_disp3 = "";
        String custom_disp4 = "";
        String custom_disp5 = "";
        String ghin1 = "";
        String ghin2 = "";
        String ghin3 = "";
        String ghin4 = "";
        String ghin5 = "";

        String blocker = "";

        /*
        String mem1 = "";
        String mem2 = "";
        String mem3 = "";
        String mem4 = "";
        String mem5 = "";
        String mem6 = "";
        String mem7 = "";
        String mem8 = "";
        String mship1 = "";
        String mship2 = "";
        String mship3 = "";
        String mship4 = "";
        String mship5 = "";
        String mship6 = "";
        String mship7 = "";
        String mship8 = "";
         */

        String rest_recurr = "";
        //String rest_name = "";
        String sfb2 = "";
        //String rest_color = "";
        //String rest_fb = "";
        String jumps = "";
        String num = "";
        String displayOpt = "";              // display option for Mobile devices

        String memberLabel = "Member";       // for clubs that do not wish to display member names
        String guestLabel = "Guest";

        //String cordRestColor = "burlywood";        // color for Cordillera's custom member restriction
        String cordRestColor = "sienna";           // color for Cordillera's custom Starter Times

        String lotteryText = "";

        Gson gson_obj = new Gson(); // Create Json response for later use
        String jsonHashMap = ""; // Temp string for converting json to hashMap
        Map<String, Object> hashMap = new LinkedHashMap<String, Object>(); // Create hashmap response for later use
        String shortTermTmp = "";  // Temporary string, used for very short terms

        //***********************************************************************************
        //  Lottery information storage area
        //
        //    lottery calculations done only once so we don't have to check each time while building sheet
        //
        String lottery = "";
        //String lottery1 = "";
        //String lottery2 = "";
        String lottery_color = "";
        String lottery_recurr = "";

        //long date2 = 0;

        int lott = 0;            // lottery supported indicator
        int ldays = 0;
        int sdays = 0;
        int sdtime = 0;
        int edays = 0;
        int edtime = 0;
        int pdays = 0;
        int ptime = 0;
        int slots = 0;
        int curr_time = 0;
        int lskip = 0;
        int firstLott = 0;
        //int st2 = 2;
        int lstate = 0;           // lottery state
        int templstate = 0;       // temp lottery state

        //int lyear = 0;
        //int lmonth = 0;
        //int lday = 0;
        int advance_days = 0;       // copy of 'index' = # of days between today and the day of this sheet


        //
        //  Arrays to hold the info required for Events, Restrictions, and Lotteries that will be displayed
        //  in the tee sheet legend and used when building the tee sheet.
        //
        int count_event = 4;      // number of events to show in legend
        int count_rest = 4;       // number of restrictions
        int count_lott = 4;       // number of lotteries (min of 1 for tests)

        if (club.equals("theforestcc")) {
            count_event = 10;             // max of 10 events for The Forest CC
        }
        if (club.equals("championhills")) {
            count_event = 10;           // max of 10 events for Champion Hills
        }
        if (club.equals("blackdiamondranch")) {
            count_event = 8;        // max of 8 events for Black Diamond Ranch
        }
        if (club.equals("blackdiamondranch") || club.equals("ccbham") || club.equals("mediterra") || club.equals("olyclub")) {
            count_rest = 10;  // max of 10 rest's
        }
        String[] eventA = new String[count_event];      // Event info
        String[] ecourseA = new String[count_event];
        String[] ecolorA = new String[count_event];
        
        int[] memeditA = new int[count_event];          // member edit option for each event 
        int[] act_hrA = new int[count_event];
        int[] act_minA = new int[count_event];

        String[] restA = new String[count_rest];        // Restriction info
        String[] rcolorA = new String[count_rest];

        String[] lottA = new String[count_lott];        // Lottery info
        String[] lcolorA = new String[count_lott];

        int[] sdaysA = new int[count_lott];      // days in advance to start taking requests
        int[] sdtimeA = new int[count_lott];     // time of day to start taking requests
        int[] edaysA = new int[count_lott];      // days in advance to stop taking requests
        int[] edtimeA = new int[count_lott];     // time of day to stop taking requests
        int[] pdaysA = new int[count_lott];      // days in advance to process the lottery
        int[] ptimeA = new int[count_lott];      // time of day to process the lottery
        int[] slotsA = new int[count_lott];      // # of consecutive groups allowed
        int[] lskipA = new int[count_lott];      // skip tee time displays
        int[] lstateA = new int[count_lott];     // lottery state
        //    1 = before time to take requests (too early for requests)
        //    2 = after start time, before stop time (ok to take requests)
        //    3 = after stop time, before process time (late, but still ok for pro)
        //    4 = requests have been processed but not approved (no new tee times now)
        //    5 = requests have been processed & approved (ok for all tee times now)


        //
        //  init the above arrays
        //
        for (i = 0; i < count_event; i++) {
            eventA[i] = "";
        }
        for (i = 0; i < count_rest; i++) {
            restA[i] = "";
        }
        for (i = 0; i < count_lott; i++) {
            lottA[i] = "";
        }
        i = 0;      // reset


        // **************** end of event, restriction and lottery save area ***********


        float hndcp1 = 0;
        float hndcp2 = 0;
        float hndcp3 = 0;
        float hndcp4 = 0;
        float hndcp5 = 0;

        int days1 = 0;               // days in advance that members can make tee times
        int days2 = 0;               //         one per day of week (Sun - Sat)
        int days3 = 0;
        int days4 = 0;
        int days5 = 0;
        int days6 = 0;
        int days7 = 0;
        int oldDays3 = 0;
        int oldDays6 = 0;
        int daysT = 0;
        //int hndcp = 0;
        int index = 0;
        int index2 = 0;
        int days = 0;
        int orig_days = 0;
        int multi = 0;               // multiple course support
        int fives = 0;               // support 5-somes for individual course
        int fivesALL = 0;            // support 5-somes for page display below
        int g1 = 0;                  // guest indicators
        int g2 = 0;
        int g3 = 0;
        int g4 = 0;
        int g5 = 0;
        int p91 = 0;
        int p92 = 0;
        int p93 = 0;
        int p94 = 0;
        int p95 = 0;
        int ind = 0;
        int j = 0;
        int jump = 0;
        int hideNames = 0;
        int hideN = 0;
        int hideG = 0;
        int hideSubmit = 0;
        int make_private = 0;
        int contimesDefault = 0;     // default # of consecutive tee times to use (passed from Member_select)

        int stop_time = 0;           // time to stop allowing tee times
        int cal_time = 0;            // calendar time for compares
        int cal_hour = 0;
        int cal_min = 0;

        boolean allow = true;
        boolean disp_hndcp = true;
        boolean lotteryOnly = false;
        boolean restrictAll = false;
        boolean restrictByOrig = false;
        boolean congGuestDay = false;
        boolean miniGuestDay = false;
        //boolean arrmem = false;             // Medinah ARR member indicator
        //boolean threeSomeDay = false;       // Rivercrest 3-some day flag
        boolean skipconstimes = false;      // skip consecutive tee time option for selected tee time
        boolean skipALL = false;            // do not include the -ALL- option for course selection if true
        boolean walkup = false;             // walk-up time (for customs)
        boolean suspend = false;            // Member restriction suspension
        boolean viewable = true;            // Allow viewing of tee times on a given day
        boolean cordallow = false;          // Cordillera custom flag
        boolean skip_waitlist = false;      // flag to skip waitlist button
        boolean allowMulti = false;         // custom flag to allow multiple tee time requests on Mobile devices
        boolean select_jump = false;        // indicator of select_jump parm from Member_select (skip calendar and instructions)

        //
        //  boolean for ClubCorp clubs that wish to hide the guest type values from the player names (case 1508).
        //
        boolean hideGuestCC = false;      // used for Club Corp and Cordillera

        if (club.equals("sevillegcc") || club.equals("morganrun") || club.equals("cordillera") || club.equals("dovecanyonclub") || Utilities.isClubInGroup(club, "AGC") || club.equals("tetonpines")) {   // add other clubs here!!

            hideGuestCC = true;
        }
        
        
        //
        //  Allow multi tee time requests on Mobile ??  (needs much more work in Member_slotm to handle this)
        //
        /*
        if (mobile > 0 && (club.equals("desertmountain") || club.equals("demov4"))) {
            
            allowMulti = true;
        }
        * 
        */


        //
        //  boolean for clubs that want to block member access to tee times after a lottery has been processed.
        //
        //  NOTE:  see same flag in Member_teelist and Member_teelist_list and Member_teelist_mobile !!!!!!!!!!!!!!!!!!
        //
        boolean noAccessAfterLottery = false;

        if (club.equals("bonniebriar") || club.equals("braeburncc")) {   // add other clubs here!!

            noAccessAfterLottery = true;      // no member access after lottery processed
        }

        // Set restrictByOrig in use, which will allow members access to a tee time if they are responsible for any players in that tee time, even if they normally wouldn't have access (runs off orig1-5 values).
        if (club.equals("wildcatruncc") || club.equals("desertmountain")) {

            restrictByOrig = true;
        }


        //int arrMax = 0;                     // days in advance that ARR members can make tee times

        //
        //  2-some indicator used for some custom requests
        //
        boolean twoSomeOnly = false;
        boolean threeSomeOnly = false;

        //
        //  parm block to hold the Course Colors
        //
        parmCourseColors colors = new parmCourseColors();          // allocate a parm block

        int cMax = 0;                               // max courses
        int colorMax = colors.colorMax;             // max number of colors defined

        //
        //  Array to hold the course names
        //
        String courseName = "";
        // String [] course = new String [cMax];
        ArrayList<String> course = new ArrayList<String>();

        /*
        String [] course_color = new String [colorMax];
        
        // set default course colors
        course_color[0] = "#F5F5DC";    // beige (4 shades)
        course_color[1] = "#DDDDBE";
        course_color[2] = "#B3B392";
        course_color[3] = "#8B8970";
        course_color[4] = "#E7F0E7";    // greens (5 shades)
        course_color[5] = "#C6D3C6";
        course_color[6] = "#95B795";
        course_color[7] = "#648A64";
        course_color[8] = "#407340";
        course_color[9] = "#F0F8FF";    // Alice Blue
        course_color[10] = "#ADD8E6";   // Light Blue
        course_color[11] = "#87CEEB";   // Sky Blue
        course_color[12] = "#1E90FF";   // Dodger Blue
        course_color[13] = "#6495ED";   // Cornflower Blue
        course_color[14] = "#6A5ACD";   // Slate Blue
        course_color[15] = "#FFF5EE";   // Sea Shell
        course_color[16] = "#FFF0F5";   // Lavender Blush
        course_color[17] = "#FFDAB9";   // Peach Puff
        course_color[18] = "#FFA07A";   // Light Salmon
        course_color[19] = "#FF7F50";   // Coral
        course_color[20] = "#FF4500";   // Orange Red
        course_color[21] = "#FF0000";   // Red
        course_color[22] = "#DAA520";   // Golden Rod
        course_color[23] = "#CD853F";   // Peru
        course_color[24] = "#B8860B";   // Dark Golden Rod
        course_color[25] = "#A0522D";   // Sienna
        course_color[26] = "#808000";   // Olive
        course_color[27] = "#FFFFFF";   //
        course_color[28] = "#FFFFFF";   //
        course_color[29] = "#FFFFFF";   //
         */

        int tmp_i = 0;                 // counter for course[], shading of course field
        int courseCount = 0;           // total courses for this club

        // int [] fivesA = new int [cMax];
        ArrayList<Integer> fivesA = new ArrayList<Integer>();        // array list to hold 5-some option for each course


        // wait list variables
        boolean waitlist = false;
        //boolean firstWait_displayed = false;
        int ci = 0;
        //int last_ci = 0;
        //int firstWait = 0;
        int wait_list_id = 0;
        //int last_wait_list_id = 0;

        //
        //  Array to hold the 'Days in Advance' value for each day of the week
        //
        int[] advdays = new int[7];                        // 0=Sun, 6=Sat
        int[] advtimes = new int[7];                       // 0=Sun, 6=Sat
        int[] origdays = new int[7];                       // 0=Sun, 6=Sat

        //**********************************************************************************
        //  Oakmont tee time arrays for Wednesday & Friday (special guest restrictions) see also verifySlot
        //**********************************************************************************
        //
        int wedcount = 19;    // 19 tee times on Wednesday
        int[] wedtimes = {820, 830, 840, 850, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150, 1430, 1440, 1450, 1500};

        int fricount = 21;    // 21 tee times
        int[] fritimes = {830, 840, 850, 900, 910, 920, 930, 940, 950, 1000, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150};

        boolean oakshotgun = false;    // indicator for shotgun event this day

        //**********************************************************************************

        //
        // Flag for using "Player" for clubs that want to hide the member's names if they do not want to distinquish between Member or Guest.
        //
        if (club.equals("foresthighlands") || club.equals("deserthighlands") || club.equals("blackstone")) {

            memberLabel = "Player";
            guestLabel = "Player";
        }
        if (club.equals("shadowridgecc")) {

            memberLabel = "Member";       // use Member for both
            guestLabel = "Member";
        }


        // DEBUG CODE - TESTING CON
        if (Connect.testConnection(con) == false) Utilities.logError("Bad connection detected within Member_sheet.doPost[1135]. user=" + user + ", club=" + club + ", referer=" + req.getHeader("Referer"));


        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(0, con);

        //
        //  parm block to hold the course parameters
        //
        parmCourse parmc = new parmCourse();          // allocate a parm block

        //
        //  parm block to hold the member restrictions for this date and member
        //
        parmRest parmr = new parmRest();          // allocate a parm block

        //
        //  Check for the 'index' parm.  If not, get the index from the submit button (from Member_select).
        //

        String calDate = req.getParameter("calDate");       //  get the date requested (mm/dd/yyyy)

        if (req.getParameter("index") != null) {

            num = req.getParameter("index");

        } else if (req.getParameter("calDate") != null) {



            //
            //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
            //
            StringTokenizer tok = new StringTokenizer(calDate, "/");     // space is the default token - use '/'

            String num1 = tok.nextToken();                    // get the mm value

            int month1 = Integer.parseInt(num1);

            num1 = tok.nextToken();                    // get the dd value

            int day1 = Integer.parseInt(num1);

            num1 = tok.nextToken();                    // get the yyyy value

            int year1 = Integer.parseInt(num1);

            //
            //  Convert today's adjusted date from string (yyyymmdd) to ints (month, day, year)
            //
            int today = (int) Utilities.getDate(con);
            int yy = today / 10000;
            int mm = (today - (yy * 10000)) / 100;
            int dd = today - ((yy * 10000) + (mm * 100));

            //
            // Calculate the number of days between today and the date requested (=> ind)
            //
            //BigDate today = BigDate.localToday();                 // get today's date
            BigDate todaydate = new BigDate(yy, mm, dd);            // get today's adjusted date (adjusted for time zone)
            BigDate thisdate = new BigDate(year1, month1, day1);       // get requested date

            index = (thisdate.getOrdinal() - todaydate.getOrdinal());   // number of days between

            num = Integer.toString(index);

        } else {

            //
            //    The name of the submit button is an index value preceeded by the letter 'i' (must start with alpha)
            //    (0 = today, 1 = tomorrow, etc.)
            //
            //    Other parms passed:  course - name of course
            //
            name = "";                                      // init

            Enumeration enum1 = req.getParameterNames();     // get the parm names passed

            loop1:
            while (enum1.hasMoreElements()) {

                name = (String) enum1.nextElement();          // get name of parm

                if (name.startsWith("i")) {

                    break loop1;                              // done - exit while loop
                }
            }

            //
            //  make sure we have the index value
            //
            if (!name.startsWith("i")) {

                out.println(SystemUtils.HeadTitle("Procedure Error"));
                out.println("<BODY bgcolor=\"ccccaa\"><CENTER>");
                out.println("<BR><BR><H3>Access Procedure Error</H3>");
                out.println("<BR><BR>Required Parameter is Missing - Member_sheet.");
                out.println("<BR>Please exit and try again.");
                out.println("<BR><BR>If problem persists, report this error to your golf shop staff.");
                out.println("<BR><BR>");
                out.println("<a href=\"Member_announce\">Return</a>");
                out.println("</CENTER></BODY></HTML>");
                out.close();
                if (Gzip == true) {
                    resp.setContentLength(buf.size());                 // set output length
                    resp.getOutputStream().write(buf.toByteArray());
                }
                return;
            }

            //
            //  Convert the index value from string to int
            //
            StringTokenizer tok = new StringTokenizer(name, "i");     // space is the default token - use 'i'

            num = tok.nextToken();                // get just the index number (name= parm must start with alpha)
        }

        if (num.equals("0")) {           // for some reason zero is very slow

            index = 0;

        } else {

            try {
                index = Integer.parseInt(num);
            } catch (NumberFormatException e) {
                // ignore error
            }
        }

        index2 = index;     // save for later (number of days from today)

        //
        //  save the index value for lottery computations
        //
        advance_days = index;

        try {

            //
            // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
            //
            getClub.getParms(con, parm);        // get the club parms

        } catch (Exception ignore) {
        }


        //
        //  Get the golf course name requested
        //
        String courseName1 = "";

        if (req.getParameter("course") != null) {

            courseName1 = req.getParameter("course");

        } else {    // must be Today's sheet

            if (parm.multi != 0) {           // if multiple courses supported for this club

                //
                //  No course name provided and this club has more than one - check for a default course
                //
                if (!parm.default_course_mem.equals("")) {

                    courseName1 = parm.default_course_mem;            // get default course from club5 !!!!

                } else {

                    //
                    //    NOTE:  Replace these customs by manually putting the default value in club5!!!!!!!!!!!!!!
                    //

                    // Custom to default to -ALL- courses
                    if (club.equals("lakewoodranch") || club.equals("pelicansnest") || club.equals("fairbanksranch")
                            || club.equals("lakes") || club.equals("edisonclub")
                            || club.equals("pinery") || club.equals("international") || club.equals("imperialgc")
                            || club.equals("jonathanslanding") || club.equals("mediterra")
                            || club.equals("loscoyotes") || club.equals("valleyclub") || club.equals("blackdiamondranch")) {

                        courseName1 = "-ALL-";
                    }
                }
            }
        }
        

        //
        //  Get the Default # of Consectuive Tee Times from Member_select, if passed
        //
        if (req.getParameter("select_times") != null) {

           try {
              
              contimesDefault = Integer.parseInt(req.getParameter("select_times"));  // get the default # of consecutive tee times

           } catch (Exception e) {
              
              contimesDefault = 1;      // use 1 if not provided (as it always has been)
           }
             
        } else {
           
            contimesDefault = 1;      // use 1 if not provided (as it always has been)
        }


        //
        //  get the jump parm if provided (location on page to jump to)
        //
        if (req.getParameter("select_jump") != null) {    // if jump indicated from Member_select (check box)
            
            select_jump = true;          // jump past calendar and instructions to show more tee times
            
        } else {
            
            if (req.getParameter("jump") != null) {

                jumps = req.getParameter("jump");         //  jump index value for where to jump to on the page

                try {
                    jump = Integer.parseInt(jumps);
                } catch (NumberFormatException e) {
                    // ignore error
                    jump = 0;
                }
            }

            //
            //   Adjust jump so we jump to the selected line minus 3 so its not on top of page
            //
            if (jump > 3) {

                jump = jump - 3;

            } else {

                jump = 0;         // jump to top of page
            }
        }


        //
        //  Get the Display Option if specified (Mobile Devices)
        //
        if (req.getParameter("displayOpt") != null) {

            displayOpt = req.getParameter("displayOpt");
        }


        if (club.equals("indianridgecc") || club.equals("ncrcc")) {      // Indian Ridge CC - Change colors for both courses

            colors.course_color[0] = "goldenrod";
            colors.course_color[1] = "green";
        }


        //
        //  Get today's date and then use the value passed to locate the requested date
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07, Sun - Sat)
        cal_hour = cal.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)
        cal_min = cal.get(Calendar.MINUTE);

        //
        //    Adjust the time based on the club's time zone (we are Central)
        //
        cal_time = (cal_hour * 100) + cal_min;     // get time in hhmm format

        cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

        if (cal_time < 0) {          // if negative, then we went back or ahead one day

            cal_time = 0 - cal_time;        // convert back to positive value

            if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi Arabia and others east of us)

                //
                // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                //
                cal.add(Calendar.DATE, 1);                     // get next day's date

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

            } else {                        // we rolled back 1 day

                //
                // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                //
                cal.add(Calendar.DATE, -1);                     // get yesterday's date

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
            }
        }

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        long thisDate = (year * 10000) + (month * 100) + day;         // get adjusted date for today


        //
        //   Adjust the calendar to get the slected date
        //
        if (index > 0) {

            cal.add(Calendar.DATE, index);                  // roll ahead 'index' days
        }

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07, Sun - Sat)
        week_count = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        String day_name = day_table[day_num];           // get name for day

        long date = (year * 10000) + (month * 100) + day;     // get adjusted date

        long dateShort = (month * 100) + day;                 // create date of mmdd for customs

        int max = 31;                                 // default value for max # of days to display  (was daysArray.MAXDAYS)
        boolean use365 = false;                       // default not to use dynamic calendars

        //
        // if 365 day calendars requested by club (NOTE: we can get rid of this now but must first config each club!!!!)
        //
        if (club.equals("oakmont") || club.equals("sauconvalleycc") || club.equals("ritzcarlton")
                || club.equals("ccrockies") || club.equals("peninsula") || club.equals("invernessclub")
                || club.equals("northland")
                || (club.equals("oaklandhills") && courseName1.equals("South Course"))
                || (club.equals("merion") && courseName1.equals("East")) || (club.equals("catamount") && mship.equals("Founder"))) {

            use365 = true;
            max = 366;
        }

        use365 = true;

        //
        //  If today, then do not allow members to access any tee times within 3 hours - for the following clubs:
        //      New Canaan, Des Moines
        //
        if (index == 0 && (club.equals("newcanaan") || club.equals("desmoines"))) {

            stop_time = cal_time + 300;        // add 3 hours to current time (already adjusted for time zone)
        }


        //
        //  If Oakmont, a weekend day and more than 14 days in advance - do not allow access to any times
        //
        if (index > 14 && club.equals("oakmont") && (day_name.equals("Saturday") || day_name.equals("Sunday"))) {

            restrictAll = true;         // indicate no member access
        }


        //
        //  If Merion, a weekend day or holiday and more than 7 days in advance - do not allow access to any times
        //
        //     Exception:  Non-Resident mships can make some tee times on weekends
        //
        //if (club.equals( "merion" ) && (index > 7 || (index == 7 && cal_time < 600)) &&
        //    (date == Hdate1 || date == Hdate2 || date == Hdate2b || date == Hdate3 ||
        //     day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ))) {
        if (club.equals("merion") && (index > 7 || (index == 7 && cal_time < 600))
                && (date == Hdate2 || date == Hdate2b
                || day_name.equals("Saturday") || day_name.equals("Sunday"))) {

            if (!mship.equals("Non-Resident")) {      // if NOT non-resident member

                restrictAll = true;         // indicate no member access

            } else {     // Non-Resident - allow some times on w/e

                if ((!day_name.equals("Saturday") && !day_name.equals("Sunday")) || !courseName1.equals("East")) {   // if NOT a w/e on East course

                    restrictAll = true;         // indicate no member access
                }
            }
        }


        //
        //  Oak Hill CC - do not allow access the day of, OR if June 1 or later and this is more than 31 days in advance.
        //
        if (club.equals("oakhillcc") && ((index > 31 && dateShort > 531) || index == 0)) {

            restrictAll = true;         // indicate no member access
        }


        //
        //  Wollaston GC - if today is Monday, then do not allow access to any tee times, no matter what day the tee sheet is for (case 1819).
        //
   /*
        if (club.equals( "wollastongc" )) {
        
            restrictAll = verifyCustom.checkWollastonMon();         // restrict all day if today is Monday
        }
         */

        
        //
        //  Do not allow Dsert Mountain 'Family Guest Play' members to enter or change tee times 
        //
        if (club.equals("desertmountain") && mship.startsWith("Family Guest")) {

            restrictAll = true;
        }
        
        // Non Certified Juniors at Elgin CC are not allowed to book tee times, but can be booked into times by other members.
        if (club.equals("elgincc") && mtype.equals("Non Certified Junior")) {
            restrictAll = true;
        }


        try {

            multi = parm.multi;
            lott = parm.lottery;
            hideNames = parm.hiden;
            
            //
            //  Check for Member Cutoff specified in Club Options
            //
            if (parm.cutoffdays < 99) {        // if option specified

                if (parm.cutoffdays == 0 && index == 0 && cal_time > parm.cutofftime) {

                    restrictAll = true;         // indicate no member access

                } else {

                    if (parm.cutoffdays == 1 && (index == 0 || (index == 1 && cal_time > parm.cutofftime))) {

                        restrictAll = true;         // indicate no member access
                    }
                }
            }

            // Cut off member access 2 days in advance at 12:00pm
            if (club.equals("philcricketrecip")) {
                
                if (index < 2 || (index == 2 && cal_time >= 1200)) {
                    
                    restrictAll = true;
                }
            }


            //
            //  Determine if this club wants to display handicaps for the members
            //
            if (parm.hndcpMemSheet == 0) {

                disp_hndcp = false;
            }


            /*
            if (club.equals("hazeltine") || club.equals("rhillscc") || club.equals("interlachen") || club.equals("brooksidecountryclub") || club.equals("overlakegcc") || club.equals("miravista")
                    || ((club.equals("brooklawn") || club.equals("sharonheights") || club.equals("greenhills") || club.equals("seacliffcc") || club.equals("paloaltohills")
                    || club.equals("indianhillscc") || club.equals("ccfairfax") || club.equals("algonquin") || club.equals("rivieracc"))
                    && mtype.endsWith("Female"))) {

                //
                //  Get the member sub-type for this user
                //
                pstmt1 = con.prepareStatement(
                        "SELECT msub_type FROM member2b WHERE username = ?");

                pstmt1.clearParameters();        // clear the parms
                pstmt1.setString(1, user);
                rs = pstmt1.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

                    msubtype = rs.getString(1);
                }

                pstmt1.close();
            }
            *         // comes from the session now
            */

            //
            //  use the member's mship type to determine which 'days in advance' parms to use
            //
            verifySlot.getDaysInAdv(con, parm, mship);        // get the days in adv data for this member

            days1 = parm.advdays1;     // get days in adv for this type
            days2 = parm.advdays2;
            days3 = parm.advdays3;
            days4 = parm.advdays4;
            days5 = parm.advdays5;
            days6 = parm.advdays6;
            days7 = parm.advdays7;

            //
            //  Save the original days in adv
            //
            origdays[0] = days1;
            origdays[1] = days2;
            origdays[2] = days3;
            origdays[3] = days4;
            origdays[4] = days5;
            origdays[5] = days6;
            origdays[6] = days7;

            //
            //  Save the adv times
            //
            advtimes[0] = parm.advtime1;
            advtimes[1] = parm.advtime2;
            advtimes[2] = parm.advtime3;
            advtimes[3] = parm.advtime4;
            advtimes[4] = parm.advtime5;
            advtimes[5] = parm.advtime6;
            advtimes[6] = parm.advtime7;

            max = parm.memviewdays;        // days this member can view tee sheets


            //
            //  Custom 1595 for Timarron - days to view must match the days in advance so members cannot view any sooner than they can book.
            //
            if (club.equals("timarroncc")) {   // per Pro's request

                max = 4;                // normally 4 days in advance

                if (day_num == 2 || cal_time < 700) {     // if Monday (any time), then do not allow access to Friday, OR if before 7:00 AM

                    max = 3;
                }
            }


            //
            //  If Mobile User, then do NOT allow consecutive times!!!!!!!
            //
            if (mobile > 0 && allowMulti == false) {
                parm.constimesm = 1;
            }


            //
            //  If Meadow Springs, do not allow multiple tee time requests on Fri, Sat or Sun
            //
            if (club.equals("meadowsprings")) {

                if (day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday")) {

                    parm.constimesm = 1;        // no consecutive tee times (only 1 time per request)
                }
            }


            //
            //  If The Lakes, do not allow multiple tee time requests on Sat or Sun (This custom was requested to be removed)
            //
      /*
            if (club.equals( "lakes" )) {
            
            if (day_name.equals( "Saturday" ) || day_name.equals( "Sunday" )) {
            
            parm.constimesm = 1;        // no consecutive tee times (only 1 time per request)
            }
            }
             */


            //
            // Custom for Long Cove to override parm.constimesm from 4/16/13 - 4/21/13 (changes every year!!!!!)
            // Case #: 00001038
            //
            if (club.equals("longcove") && date >= 20130416 && date <= 20130421) {  // week of the Heritage Classic

                parm.constimesm = 1;
            }


            //
            //
            //
            if (club.equals("tpctc") && mship.equals("Corporate")) {

                parm.constimesm = 2;        // allow 2 consecutive tee times
            }


            if (multi != 0) {           // if multiple courses supported for this club

                /*
                for (i=0; i<cMax; i++) {
                course[i] = "";       // init the course array
                }
                i = 0;
                
                //
                //   If Stonebridge Ranch CC - course selection based on mship type
                //
                if (club.equals( "stonebridgeranchcc" ) && !mship.equals("Dual")) {
                
                if (mship.equals("Dye")) {       // if DYE mship
                
                course[0] = "Dye";            // Only Dye course
                i = 1;                        // 1 course
                
                } else {                         // Hills mship gets all but Dye
                
                course[0] = "Chisholm";
                course[1] = "Cimmarron";
                course[2] = "Saddleback";
                i = 3;                        // 3 courses
                }
                
                } else {              // all others
                
                //
                //  Get the names of all courses for this club
                //
                stmt = con.createStatement();        // create a statement
                
                rs = stmt.executeQuery("SELECT courseName " +
                "FROM clubparm2 WHERE first_hr != 0");
                
                while (rs.next() && i < cMax) {
                
                courseName = rs.getString(1);
                
                course[i] = courseName;      // add course name to array
                i++;
                }
                stmt.close();
                }
                
                courseCount = i; // save the total # of courses for later (course shading)
                
                if (club.equals( "fortcollins" ) || (club.equals( "stonebridgeranchcc" ) && mship.equals("Dye"))) {
                
                skipALL = true;       // do not include the -ALL- option
                }
                
                
                if (skipALL == false && i > 1 && i < cMax) { // if ok and more than 1 course
                
                course[i] = "-ALL-";
                }
                
                 */


                //
                //   If Stonebridge Ranch CC - course selection based on mship type
                //
                if (club.equals("stonebridgeranchcc") && !mship.equals("Dual")) {

                    if (mship.equals("Dye")) {       // if DYE mship

                        course.add("Dye");            // Only Dye course

                    } else {                         // Hills mship gets all but Dye

                        course.add("Chisholm");
                        course.add("Cimmarron");
                        course.add("Saddleback");
                    }

                } else {              // all others

                    course = Utilities.getCourseNames(con);     // get all the course names
                }


                if (mobile > 0 || club.equals("fortcollins") || (club.equals("stonebridgeranchcc") && mship.equals("Dye"))) {

                    skipALL = true;       // do not include the -ALL- option
                }


                if (skipALL == false && course.size() > 1) {   // if ok and more than 1 course, add -ALL- option

                    course.add("-ALL-");
                }


                //
                //  Make sure we have a course name (in case we came directly from the menu for today's tee sheet)
                //
                if (courseName1.equals("")) {

                    courseName1 = course.get(0);              // grab the first one

                    if (club.equals("fortcollins")) {         // change for Fort Collins

                        if (mtype.endsWith("Greeley")) {          // if Greeley member

                            courseName1 = "Greeley CC";      // setup default course (top of the list)

                        } else if (mtype.endsWith("Fox Hill")) {

                            courseName1 = "Fox Hill CC";

                        } else {

                            courseName1 = "Fort Collins CC";
                        }
                    }

                    if (club.equals("merion")) {         // Merion members - East course only

                        courseName1 = "East";
                    }

                    if (club.equals("oakhillcc")) {

                        courseName1 = "East Course";          // Oak Hill CC - default = East Course (case 1433)
                    }

                    if (club.equals("ccjackson")) {         // CC of Jackson - set default course members - Case # 1373

                        courseName1 = "Cypress to Cypress";
                    }
                }

            }          // end of IF multi


            //
            //  Get the System Parameters for this Course
            //
            if (courseName1.equals("-ALL-")) {

                //
                //  Check all courses for 5-some support
                //
                cMax = course.size();     // number of courses
                i = 0;
                loopc:
                while (i < cMax) {

                    courseName = course.get(i);       // get a course name

                    if (!courseName.equals("-ALL-")) {   // skip if -ALL-

                        if (courseName.equals("")) {      // done if null
                            break loopc;
                        }

                        getParms.getCourse(con, parmc, courseName);       // get parms for this course

                        fivesA.add(parmc.fives);            // get fivesome option

                        if (parmc.fives == 1) {
                            fives = 1;                          // 5-somes supported on at least one course
                        }
                    }
                    i++;
                }


            } else {       // single course requested

                getParms.getCourse(con, parmc, courseName1);

                fives = parmc.fives;      // get fivesome option
            }


            //
            // Robert Trent Jones or Colorado GC - do not allow 5-somes or show the 5th player column at any time for members.
            //
            if (club.equals("rtjgc") || club.equals("coloradogc") || club.equals("awbreyglen") || club.equals("rehobothbeachcc") || club.equals("ccyork") || club.equals("thenationalgolfclub")
                    || club.equals("mayrivergc") || club.equals("chattanoogagcc") || club.equals("lagrangecc")) {

                fives = 0;
            }

            //
            // Forest Highlands - don't allow 5-somes or show 5th player column during specified date range
            //
            if (club.equals("foresthighlands")) {

                long month_day = (month * 100) + day;     // get adjusted date

                if (month_day > 424 && month_day < 1001) {
                    fives = 0;
                }
            }

            //
            // Columbine CC - don't allow 5-somes or show 5th player column during specified date range
            //
            if (club.equals("columbine")) {

                long month_day = (month * 100) + day;     // get adjusted date

                if (month_day > 331 && month_day < 1001) {
                    fives = 0;
                }
            }

            //
            // The Estancia Club - don't allow 5-somes or show 5th player column during specified date range
            //
            if (club.equals("estanciaclub")) {

                long month_day = (month * 100) + day;     // get adjusted date

                if (month_day <= 515 || month_day >= 1015) {
                    fives = 0;
                }
            }

            //
            // Forest Hills CC - IL - Don't allow 5-somes or show 5th player column during specified date range
            //
            if (club.equals("foresthillscc")) {

                long month_day = (month * 100) + day;

                if (month_day >= 501 && month_day <= 930) {
                    fives = 0;
                }
            }

            //
            //  The Lakes CC - don't allow 5-somes or show 5th player column during specifid date range
            //
            if (club.equals("lakes")) {

                long month_day = (month * 100) + day;
                
                if (month_day < 515 || month_day > 1031) {
                
                    fives = 0;
                }
            }

            fivesALL = fives;            // save 5-somes option for table display below


            //
            //   Remove any guest types that are null - for tests below
            //
            i = 0;
            while (i < parm.MAX_Guests) {

                if (parm.guest[i].equals("")) {

                    parm.guest[i] = "$@#!^&*";      // make so it won't match player name
                }
                i++;
            }         // end of while loop

            //
            //  Get all restrictions for this day and user (for use when checking each tee time below)
            //
            parmr.user = user;
            parmr.mship = mship;
            parmr.mtype = mtype;
            parmr.date = date;
            parmr.day = day_name;
            parmr.course = courseName1;
            parmr.club = club;              // add this for customs
            parmr.msubtype = msubtype;      // must be set above in custom query (paloaltohills and others)

            getRests.getAll(con, parmr);       // get the restrictions

            //
            //   Statements to find any restrictions, events or lotteries for today
            //
            String string7b = "";
            String string7c = "";

            if (courseName1.equals("-ALL-")) {
                string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? "
                        + "AND showit = 'Yes' AND activity_id = 0 ORDER BY stime";
            } else {
                string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? "
                        + "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes' AND activity_id = 0 ORDER BY stime";
            }

            if (courseName1.equals("-ALL-")) {
                string7c = "SELECT name, color, act_hr, act_min, courseName, memedit FROM events2b WHERE date = ? AND season = 0 AND activity_id = 0 AND inactive = 0 ORDER BY stime";
            } else {
                string7c = "SELECT name, color, act_hr, act_min, courseName, memedit FROM events2b WHERE date = ? "
                        + "AND (courseName = ? OR courseName = '-ALL-') AND season = 0 AND activity_id = 0 AND inactive = 0 ORDER BY stime";
            }

            PreparedStatement pstmt7b = con.prepareStatement(string7b);
            PreparedStatement pstmt7c = con.prepareStatement(string7c);

            //
            //  Scan the events, restrictions and lotteries to build the legend
            //
            pstmt7b.clearParameters();          // clear the parms
            pstmt7b.setLong(1, date);
            pstmt7b.setLong(2, date);

            if (!courseName1.equals("-ALL-")) {
                pstmt7b.setString(3, courseName1);
            }

            rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

            i = 0;

            while (rs.next() && i < count_rest) {

                rest = rs.getString(1);
                rest_recurr = rs.getString(2);
                rcolor = rs.getString(3);

                boolean showRest = true;
                
                if (verifyCustom.checkRestLift((int)date, day_name, rest, club, 0, con) 
                        || !getRests.showRest(rs.getInt("id"), -99, rs.getInt("stime"), rs.getInt("etime"), date, day_name, courseName1, con)) {
                    
                    if (!club.equals("interlachen")) {
                        showRest = false;
                    }
                }

                if (showRest) {    // Only display on legend if not suspended for entire day
                    //
                    //  We must check the recurrence for this day (Monday, etc.)
                    //
                    if ((rest_recurr.equals("Every " + day_name)) || // if this day
                            (rest_recurr.equalsIgnoreCase("every day")) || // or everyday
                            ((rest_recurr.equalsIgnoreCase("all weekdays")) && // or all weekdays (and this is one)
                            (!day_name.equalsIgnoreCase("saturday"))
                            && (!day_name.equalsIgnoreCase("sunday")))
                            || ((rest_recurr.equalsIgnoreCase("all weekends")) && // or all weekends (and this is one)
                            (day_name.equalsIgnoreCase("saturday")))
                            || ((rest_recurr.equalsIgnoreCase("all weekends"))
                            && (day_name.equalsIgnoreCase("sunday")))) {

                        restA[i] = rest;          // save the restriction info for legend and tee sheet
                        rcolorA[i] = rcolor;

                        if (rcolorA[i].equalsIgnoreCase("default")) {
                            rcolorA[i] = "#F5F5DC";
                        }

                        i++;
                    }
                }
            } // end of while

            pstmt7b.close();


            //
            //   Get Events
            //
            pstmt7c.clearParameters();          // clear the parms
            pstmt7c.setLong(1, date);

            if (!courseName1.equals("-ALL-")) {
                pstmt7c.setString(2, courseName1);
            }

            rs = pstmt7c.executeQuery();      // find all matching events, if any

            i = 0;

            // loop thru all events
            while (rs.next() && i < count_event) {

                eventA[i] = rs.getString(1);
                ecolorA[i] = rs.getString(2);
                act_hrA[i] = rs.getInt(3);
                act_minA[i] = rs.getInt(4);
                ecourseA[i] = rs.getString(5);
                memeditA[i] = rs.getInt("memedit");

                if (ecolorA[i].equalsIgnoreCase("default")) {
                    ecolorA[i] = "#F5F5DC";
                }

                i++;
            }                  // end of while

            pstmt7c.close();


            //
            //  check for lotteries
            //
            if (lott != 0) {                       // if supported for this club

                //
                //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
                //
                lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided

                String string7d = "";

                if (courseName1.equals("-ALL-")) {

                    string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots "
                            + "FROM lottery3 WHERE sdate <= ? AND edate >= ? ORDER BY stime";
                } else {

                    string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots "
                            + "FROM lottery3 WHERE sdate <= ? AND edate >= ? "
                            + "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
                }

                PreparedStatement pstmt7d = con.prepareStatement(string7d);

                pstmt7d.clearParameters();          // clear the parms
                pstmt7d.setLong(1, date);
                pstmt7d.setLong(2, date);

                if (!courseName1.equals("-ALL-")) {
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
                    if ((lottery_recurr.equals("Every " + day_name)) || // if this day
                            (lottery_recurr.equalsIgnoreCase("every day")) || // or everyday
                            ((lottery_recurr.equalsIgnoreCase("all weekdays")) && // or all weekdays (and this is one)
                            (!day_name.equalsIgnoreCase("saturday"))
                            && (!day_name.equalsIgnoreCase("sunday")))
                            || ((lottery_recurr.equalsIgnoreCase("all weekends")) && // or all weekends (and this is one)
                            (day_name.equalsIgnoreCase("saturday")))
                            || ((lottery_recurr.equalsIgnoreCase("all weekends"))
                            && (day_name.equalsIgnoreCase("sunday")))) {


                        lottA[i] = lottery;             // save Lottery info
                        lcolorA[i] = lottery_color;
                        sdaysA[i] = sdays;
                        sdtimeA[i] = sdtime;
                        edaysA[i] = edays;
                        edtimeA[i] = edtime;
                        pdaysA[i] = pdays;
                        ptimeA[i] = ptime;
                        slotsA[i] = slots;

                        if (lottery_color.equalsIgnoreCase("default")) {

                            lcolorA[i] = "#F5F5DC";
                        }
                        i++;
                    }
                }                  // end of while

                pstmt7d.close();

                i = 0;

                //
                //  Process the lotteries if there are any for this day
                //
                //    Determine which state we are in (before req's, during req's, before process, after process)
                //
                String string12 = "";

                if (courseName1.equals("-ALL-")) {

                    string12 = "SELECT state FROM lreqs3 "
                            + "WHERE name = ? AND date = ?";
                } else {
                    string12 = "SELECT state FROM lreqs3 "
                            + "WHERE name = ? AND date = ? AND courseName = ?";
                }

                if (!lottA[0].equals("")) {       // if any lotteries found

                    //
                    //  Get the current time (date is correct from _select)
                    //
                    Calendar cal3 = new GregorianCalendar();    // get todays date
                    cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time)
                    cal_min = cal3.get(Calendar.MINUTE);

                    curr_time = (cal_hour * 100) + cal_min;
                    curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

                    if (curr_time < 0) {          // if negative, then we went back or ahead one day

                        curr_time = 0 - curr_time;        // convert back to positive value
                    }

                    //
                    //  process each lottery
                    //
                    for (i = 0; i < count_lott; i++) {

                        if (!lottA[i].equals("")) {       // if lottery to process

                            //
                            //  check the day and time values
                            //
                            if (advance_days > sdaysA[i] && !club.equals("invernessclub")) {       // if we haven't reached the start day yet

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

                                PreparedStatement pstmt12 = con.prepareStatement(string12);

                                pstmt12.clearParameters();        // clear the parms
                                pstmt12.setString(1, lottA[i]);
                                pstmt12.setLong(2, date);

                                if (!courseName1.equals("-ALL-")) {
                                    pstmt12.setString(3, courseName1);
                                }

                                rs = pstmt12.executeQuery();

                                if (!rs.next()) {             // if none waiting approval

                                    lstateA[i] = 5;              // state 5 - after process & approval time

                                } else {                     // still some reqs waiting

                                    templstate = rs.getInt(1);  // get its state

                                    if (templstate == 5) {      // if we processed already (some not assigned)

                                        lstateA[i] = 5;
                                    }
                                }
                                pstmt12.close();

                            }
                        }
                    }

                    //
                    //  Custom change for Old Oaks CC
                    //    If lottery for this day, only display the lottery button - no table or tee times
                    //
                    if (club.equals("oldoaks") && lstateA[0] == 2) {   // old oaks and members can make lottery req's

                        lotteryOnly = true;
                    }

                    //
                    //  Custom change for Willow Ridge
                    //    If lottery for this day, and members can still register, then do not show the Wait List button
                    //
                    if (club.equals("willowridgecc") && lstateA[0] < 3) {

                        skip_waitlist = true;
                    }

                }   // end of IF any lotteries found

            }      // end of IF lottery supported

            i = 0;    // reset index


            //
            //  Special processing for Oakmont CC - check if there is a Shotgun Event for today (if Friday)
            //
            if (club.equals("oakmont") && day_name.equals("Friday") && date != 20100827) {    // skip Aug 27th 2010

                PreparedStatement pstmtc = con.prepareStatement(
                        "SELECT dd "
                        + "FROM teecurr2 "
                        + "WHERE date = ? AND event_type = ?");

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

            if (club.equals("milwaukee") && (day_name.equals("Tuesday") || day_name.equals("Wednesday")
                    || day_name.equals("Thursday") || day_name.equals("Friday"))) {

                PreparedStatement pstmtc = con.prepareStatement(
                        "SELECT dd "
                        + "FROM teecurr2 "
                        + "WHERE date = ? AND fb = 1 AND time > 1200 AND time < 1431");  // this day, back tee, noon to 2:30

                pstmtc.clearParameters();        // clear the parms
                pstmtc.setLong(1, date);
                rs = pstmtc.executeQuery();      // execute the prepared stmt

                if (rs.next()) {

                    mccGuestDay = true;         // Guest Times exist for this day
                }
                pstmtc.close();
            }


            /*
            //
            //  Rivercrest Golf Club & Preserve (case 1492)
            //
            if (club.equals( "rivercrestgc" )) {
            
            threeSomeDay = verifyCustom.checkRivercrestDay(date, day_name);     // check if 3-some day
            }
             */


            //
            //  Count the number of tee sheets displayed since last tomcat bounce
            //
            Calendar calCount = new GregorianCalendar();       // get todays date

            //int hourCount = calCount.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)

            SystemUtils.sheetCountsMem[calCount.get(Calendar.HOUR_OF_DAY)]++;


            //
            //  Get today's date and setup parms to use when building the calendar
            //
            Calendar cal2 = new GregorianCalendar();        // get todays date
            int year2 = cal2.get(Calendar.YEAR);
            int month2 = cal2.get(Calendar.MONTH);
            int day2 = cal2.get(Calendar.DAY_OF_MONTH);
            int day_num2 = cal2.get(Calendar.DAY_OF_WEEK);  // day of week (01 - 07)
            cal_hour = cal2.get(Calendar.HOUR_OF_DAY);
            cal_min = cal2.get(Calendar.MINUTE);
            int cal_sec = cal2.get(Calendar.SECOND);

            cal_time = (cal_hour * 100) + cal_min;

            cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

            if (cal_time < 0) {          // if negative, then we went back or ahead one day

                cal_time = 0 - cal_time;        // convert back to positive value

                if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi Arabia and others east of us)

                    //
                    // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                    //
                    cal2.add(Calendar.DATE, 1);                     // get next day's date

                    year2 = cal2.get(Calendar.YEAR);
                    month2 = cal2.get(Calendar.MONTH);
                    day2 = cal2.get(Calendar.DAY_OF_MONTH);
                    day_num2 = cal2.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

                } else {                        // we rolled back 1 day

                    //
                    // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                    //
                    cal2.add(Calendar.DATE, -1);                     // get yesterday's date

                    year2 = cal2.get(Calendar.YEAR);
                    month2 = cal2.get(Calendar.MONTH);
                    day2 = cal2.get(Calendar.DAY_OF_MONTH);
                    day_num2 = cal2.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
                }
            }

            int today2 = day2;                              // save today's number

            month2 = month2 + 1;                            // month starts at zero

            String mm = mm_table[month2];                   // month name

            int numDays = numDays_table[month2];            // number of days in month

            if (numDays == 0) {                             // if Feb

                int leapYear = year2 - 2000;
                numDays = feb_table[leapYear];               // get days in Feb
            }

            long thisDate2 = (year2 * 10000) + (month2 * 100) + day2;         // get adjusted date for today

            //
            //  Create a current time value for display on the tee sheet page
            //
            String s_time = "";

            cal_hour = cal_time / 100;                // get adjusted hour
            cal_min = cal_time - (cal_hour * 100);    // get minute value
            int cal_am_pm = 0;                            // preset to AM

            if (cal_hour > 11) {
                cal_am_pm = 1;                // PM
                cal_hour = cal_hour - 12;     // set to 12 hr clock
            }
            if (cal_hour == 0) {
                cal_hour = 12;
            }

            if (cal_min < 10) {
                s_time = cal_hour + ":0" + cal_min;
            } else {
                s_time = cal_hour + ":" + cal_min;
            }

            if (cal_sec < 10) {
                s_time = s_time + ":0" + cal_sec;
            } else {
                s_time = s_time + ":" + cal_sec;
            }
            if (cal_am_pm == 0) {
                s_time = s_time + " AM";
            } else {
                s_time = s_time + " PM";
            }

            //
            //  If Merion's East course change the days in adv to 365
            //
            if (club.equals("merion") && courseName1.equals("East")) {

                days1 = 7;
                days2 = 365;
                days3 = 365;
                days4 = 365;
                days5 = 365;
                days6 = 365;
                days7 = 7;

                if (mship.equals("Non-Resident")) {
                    days1 = 365;
                    days7 = 365;
                }
            }

            //
            //  If Oakland Hills or CC of the Rockies or Northland CC change the days in adv to 365
            //
            if ((club.equals("oaklandhills") && courseName1.equals("South Course")) || club.equals("ccrockies") || club.equals("northland") 
                    || (club.equals("elgincc") && !mship.equals("Social Member") && !mtype.equals("Certified Junior") && !mtype.equals("Non Certified Junior"))) {

                days1 = 365;
                days2 = 365;
                days3 = 365;
                days4 = 365;
                days5 = 365;
                days6 = 365;
                days7 = 365;
            }

            //
            //  If Catamount Ranch change the days in adv to 180
            //
            if (club.equals("catamount") && mship.equals("Founder")) {

                days1 = 180;
                days2 = 180;
                days3 = 180;
                days4 = 180;
                days5 = 180;
                days6 = 180;
                days7 = 180;
            }

            //
            //  If North Hills change the days in adv to 30
            //
            if (club.equals("northhills")) {

                days1 = 30;
                days2 = 30;
                days3 = 30;
                days4 = 30;
                days5 = 30;
                days6 = 30;
                days7 = 30;
            }


            /*
            //
            //  If Valley Country Club and a lady change the days in adv to 14 for Fridays.  (Removed by case #1388)
            //
            if (club.equals( "valleycc" ) && mtype.endsWith( "Female" ) ) {
            days6 = 14;
            }
             */


            //
            //   Santa Ana Custom - increase the days in adv for Women - Tues = 4, Fri = 7 - REMOVED 11/13/06 per Larry
            //
          /*
            if (club.equals( "santaana" ) && mtype.equals( "Adult Female" )) {
            
            oldDays3 = days3;     // save original days
            oldDays6 = days6;
            days3 = 4;          // Tues = 4 days in advance (starting at 12:01 AM)
            days6 = 7;          // Fri = 7 days in advance (starting at 12:01 AM)
            }
             */

            //
            //   scioto Custom - change the days in adv for Spouses - Sun, Mon, Thur, Fri, Sat = 2, Tue, Wed = 3
            //
            if (club.equals("sciotocc") && mtype.startsWith("Spouse")) {

                days1 = 3;          // Sun = 3 days in advance (starting at 7:30 AM)
                days2 = 3;          // Mon = 3 days in advance (starting at 7:30 AM)
                days3 = 4;          // Tue = 4 days in advance (starting at 7:30 AM)
                days4 = 4;          // Wed = 4 days in advance (starting at 7:30 AM)
                days5 = 4;          // Thu = 4 days in advance (starting at 7:30 AM)
                days6 = 3;          // Fri = 3 days in advance (starting at 7:30 AM)
                days7 = 3;          // Sat = 3 days in advance (starting at 7:30 AM)

                //advtimes[5] = 1200;   // Changed back to 7:30

            } else if (club.equals("cecc") && mtype.startsWith("Spouse")) {      //   Columbia-Edgewater Custom - change the days in adv for Spouses

                oldDays3 = days3;     // save original days
                oldDays6 = days6;
                days3 = 7;          // 7 days in advance (starting at 8:00 AM)
                days6 = 7;
                parm.advdays3 = 7;
                parm.advdays6 = 7;

            } else if (club.equals("elniguelcc") && mtype.equals("Adult Female")) {      //  If El Niguel and an Adult Female - change the Tuesday Days and Time

                days3 = 4;               // Tues = 4 (normally is 2)
                advtimes[2] = 1300;       // at 1:00 PM

            } else if (club.equals("seacliffcc") && msubtype.equals("18 Holer")) {   // all 18 Holers

                days3 = 30;                // Tues = 30
                parm.advdays3 = 30;
                max = 30;                  // allow female 18 holers to view ahead 30 days (vs 7)

            } else if (club.equals("paloaltohills") && msubtype.equals("18 Holer")) {   // all 18 Holers

                days5 = 30;                // Thurs = 30
                max = 30;                  // allow female 18 holers to view ahead 30 days (vs 7)

            } else if (club.equals("rhillscc") && msubtype.equals("Ladies")) {

                days3 = 14;
                parm.advdays3 = 14;
                days5 = 14;
                parm.advdays5 = 14;
                max = 14;

            } else if (club.equals("lagcc") && mtype.endsWith("Female")) {

                days3 = 14;
                parm.advdays3 = 14;

                days5 = 14;
                parm.advdays5 = 14;
                max = 14;
               
                if (date == 20120907) {
                    days6 = 14;
                    parm.advdays6 = 14;
                    max = 14;
                }

            } else if (club.equals("tualatincc") && (mtype.equals("Primary Female") || mtype.equals("Spouse Female"))) {      // If Tualatin CC and Primary/Spouse female - change Tuesday days in adv.

                days3 = 7;
                parm.advdays3 = 7;
                max = 7;

            } else if (club.equals("indianhillscc") && msubtype.equals("18 holer")) {

                days3 = 14;
                parm.advdays3 = 14;
                max = 14;

            } else if (club.equals("ccfairfax") && (msubtype.equals("18 Holer") || msubtype.equals("9 Holer"))) {    // If CC of Fairfax and a Ladies "18 Holer" or "9 Holer" subtype - Change Wed days in adv.

                days4 = 14;
                parm.advdays4 = 14;
                max = 14;

            } else if (club.equals("algonquin") && msubtype.equals("9 Holer")) {

                days4 = 14;
                parm.advdays4 = 14;
                max = 14;

            } else if (club.equals("capitalcityclub") && mtype.equals("Dependent")) {

                for (int k = 0; k < 7; k++) {
                    advtimes[k] = 830;
                }
                
            } else if (club.equals("rivieracc") && msubtype.equals("RWGA")) {

                days3 = 14;
                days5 = 14;
                parm.advdays3 = 14;
                parm.advdays5 = 14;
                max = 14;
                
            } else if (club.equals("brooksidecountryclub") && msubtype.equals("League")) {
                
                /*
                if (mtype.endsWith("Male")) {
                    days3 = 14;
                    parm.advdays3 = 14;
                }
                
                if (mtype.endsWith("Female") || date == 20130529) {
                    days4 = 14;
                    parm.advdays4 = 14;
                }
                
                if (date == 20130530 && mtype.endsWith("Male")) {
                    days5 = 14;
                    parm.advdays5 = 14;
                }
                max = 14;
                
                 */
            } else if (club.equals("bhamcc") && mtype.equalsIgnoreCase("Adult Female")) {
                
                days5 = 6;
                parm.advdays5 = 6;
                advtimes[4] = 1730;
                
            } else if (club.equals("overlakegcc") && msubtype.equals("18 Holer")) {
                
                days3 = 7;
                days6 = 7;
                parm.advdays3 = 7;
                parm.advdays6 = 7;
                max = 7;
                
            } else if (club.equals("miravista") && msubtype.equals("18 Holer")) {
                
                days5 = 21;
                parm.advdays5 = 21;
                max = 21;
            }
            
            /* **Moved from above so it could be commented out**
               else if (club.equals("claremontcc") && mtype.equals("Adult Female")) {     //  If Claremont CC and an Adult Female - change the Tuesday days (case 1361)

                days3 = 30;               // Tues = 30 (normally is 3)

            } 
             */


            /*
            //
            //  If Jonathan's Landing and an certain member types - change days in advance to 6  (Case #1328)
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
            
            //
            //   Medinah Custom - if Monday (today) or Tues before 6:00AM and days in adv = 30, change to 29 (proshop closed on Mondays)
            //
            if ( club.equals( "medinahcc" ) && ( day_num2 == 2 || ( day_num2 == 3 && cal_time < 600 ) ) ) {     // if Medinah and today is Monday or Tues b4 6:00am
            
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
             */

            //
            //   Los Coyotes Custom - change the days in adv for Secondary Members from 8 to 3 days  (Case #1191)
            //
            if (club.equals("loscoyotes") && mtype.startsWith("Secondary")) {

                days1 = 3;
                days2 = 3;
                days3 = 3;
                days4 = 3;
                days5 = 3;
                days6 = 3;
                days7 = 3;
            }

            //
            //   Capital City Club - Set days in advance to 3 and advance time to 7:30am for Fri/Sat/Sun when viewing Crabapple course or All.
            //
           /*
            if (club.equals("capitalcityclub") && (courseName1.equals("Crabapple") || courseName1.equals("-ALL-"))) {
            
            days1 = 3;
            days6 = 3;
            days7 = 3;
            
            advtimes[0] = 730;
            advtimes[5] = 730;
            advtimes[6] = 730;
            }
             */

            if (club.equals("championsrun")) {

                // Set the view days equal to the days in advance. If we're further out than that, block view of the tee sheet regardless. If we're on the day where access opens up, block view until access opens
                if ((advtimes[day_num - 1] > cal_time && ((day_num == 1 && index == days1) || (day_num == 2 && index == days2) || (day_num == 3 && index == days3) 
                        || (day_num == 4 && index == days4 || (day_num == 5 && index == days5) || (day_num == 6 && index == days6) || (day_num == 7 && index == days7))))
                        || ((day_num == 1 && index > days1) || (day_num == 2 && index > days2) || (day_num == 3 && index > days3) 
                        || (day_num == 4 && index > days4 || (day_num == 5 && index > days5) || (day_num == 6 && index > days6) || (day_num == 7 && index > days7)))) {

                    viewable = false;
                }
            }

            //
            //  if its earlier than the time specified for days in advance, do not allow the last day_in_advance
            //
            if (advtimes[0] > cal_time) {

                //
                //  If this club set the max days to view equal to the days in advance, and all days in advance are the same, and
                //  all times of the days are the same, then adjust the days to view.  This is the only way we can do this!!
                //  - Also make sure member is viewing the currently max day in advance
                //
                if (max > 0 && index == max && max == days1 && max == days2 && max == days3 && max == days4 && max == days5 && max == days6 && max == days7
                        && advtimes[0] == advtimes[1] && advtimes[0] == advtimes[2] && advtimes[0] == advtimes[3] && advtimes[0] == advtimes[4]
                        && advtimes[0] == advtimes[5] && advtimes[0] == advtimes[6]) {

                    viewable = false;       // Check for this below and, if false, DO NOT display any of the tee times for that day

                    //max--;        // Instead of decrementing the max viewable days, set a boolean to block view of the tee times later
                }

                if (days1 > 0) {

                    days1--;
                }
            }

            if (advtimes[1] > cal_time) {

                if (days2 > 0) {

                    days2--;
                }
            }

            //if (!club.equals( "santaana" ) || !mtype.equals( "Adult Female" )) {   // do not adjust if Santa Ana and Woman (removed 11/13/06)

            if (advtimes[2] > cal_time) {

                if (days3 > 0) {

                    days3--;
                }
            }
            //}

            if (advtimes[3] > cal_time) {

                if (days4 > 0) {

                    days4--;
                }
            }

            if (advtimes[4] > cal_time) {

                if (days5 > 0) {

                    days5--;
                }
            }

            //if (!club.equals( "santaana" ) || !mtype.equals( "Adult Female" )) {   // do not adjust if Santa Ana and Woman (removed 11/13/06)

            if (advtimes[5] > cal_time) {

                if (days6 > 0) {

                    days6--;
                }
            }
            //}

            if (advtimes[6] > cal_time) {

                if (days7 > 0) {

                    days7--;
                }
            }

            //
            //    adv time values have been set based on the mship type
            //    calendar time (cal_time) has been adjusted already for the time zone specified
            //
            advdays[0] = days1;     // put 'days in adv' values in array
            advdays[1] = days2;
            advdays[2] = days3;
            advdays[3] = days4;
            advdays[4] = days5;
            advdays[5] = days6;
            advdays[6] = days7;

            //
            //  Adjust days array values if necessary (in case the time has now reached the set value)
            //
            int day_numT = day_num2;                     // get today's day of the week (1 - 7)
            day_numT--;                                  // convert to index (0 - 6)

            if (club.equals("northhills")) {

                for (i = 8; i < 31; i++) {

                    daysArray.days[i] = 102;             // special color
                }

            } else {

                for (i = 0; i < daysArray.MAXDAYS; i++) {

                    daysT = advdays[day_numT];           // get days in advance for day of the week
                    day_numT++;                          // bump to next day of week
                    if (day_numT > 6) {
                        day_numT = 0;      // if wrapped past end of week
                    }
                    //
                    // check if this day can be accessed by members (initially set in Login)
                    //
                    //    0 = No, 1 = Yes, 2 = Yes for Lottery only (set in Login only)
                    //
                    if (club.equals("milwaukee")) {

                        if (daysT >= i) {                 // if ok for this day

                            // use default colors
                            daysArray.days[i] = 1;         // set ok in array

                        } else {

                            if (day_numT == 3 || day_numT == 4 || day_numT == 5 || day_numT == 6) {  // if Tues - Fri (day_numT already adjusted for next day!)

                                daysArray.days[i] = 101;        // mark this one in red (sienna)
                            }
                        }

                    } else if (club.equals("estanciaclub")) {

                        if (i >= 30) {
                            daysArray.days[i] = 103;    // mark days more than 30 out in yellow
                        }

                    } else if (club.equals("napervillecc")) {

                        if (i > 7 && i <= 30) {
                            daysArray.days[i] = 103;    // mark days more than 7 out in yellow
                        }

                    } else {

                        if (daysT >= i) {                 // if ok for this day

                            // if (club.equals( "medinahcc" ) && courseName1.equals( "No 2" )) {

                            //    daysArray.days[i] = (i <= Max7) ? 1 : 0;       // always use 7 days in adv

                            // } else {

                            daysArray.days[i] = 1;      // set ok in array
                            // }
                        } else if (daysArray.days[i] != 2) {

                            daysArray.days[i] = 0;    // set to no access in array if no lottery found

                        }/*else {
                        
                        daysArray.days[i] = 0;         // set to no access in array
                        }*/

                        if (club.equals("brooklawn")) {

                            if (thisDate <= 20130615 && (mtype.endsWith("Male") || mtype.equalsIgnoreCase("Cert Dependent over 16") || mtype.endsWith("Dependent"))) {    // Brooklawn Custom for 6/15/13

                                if ((thisDate > 20130509 || (thisDate == 20130509 && cal_time >= 900)) && i > 0) {

                                    //  Allow member access to 6/15/13 on 5/9/13 for a tee time event
                                    if (date == 20130615) {         // if day of event

                                        daysArray.days[i] = 1;        // set ok in array

                                        if (max < index) {
                                            max = index;     // bump max days to view if necessary
                                        }
                                    }
                                }
                            }

                            if (thisDate <= 20130619) {

                                if (thisDate > 20130515 || (thisDate == 20130515 && cal_time >= 900) && i > 0) {

                                    if (date == 20130619) {         // if day of event

                                        daysArray.days[i] = 1;        // set ok in array

                                        if (max < index) {
                                            max = index;     // bump max days to view if necessary
                                        }
                                    }

                                }
                            }

                            if (thisDate <= 20130713) {

                                if (thisDate > 20130526 || (thisDate == 20130526 && cal_time >= 700) && i > 0) {

                                    //  Allow member access to 7/13/13 on 5/26/13 for a tee time event
                                    if (date == 20130713) {         // if day of event

                                        daysArray.days[i] = 1;        // set ok in array

                                        if (max < index) {
                                            max = index;     // bump max days to view if necessary
                                        }
                                    }

                                }
                            }
                            
                        } else if (club.equals("sankatyheadgc")) {      // This appears to be old skin only....
                            
                            if (mtype.equalsIgnoreCase("Primary Female") || mtype.equalsIgnoreCase("Spouse Female")) {

                                Calendar calB = new GregorianCalendar();             // get current date
                                calB.add(Calendar.DATE, i);                       // get this day's date
                                int yearB = calB.get(Calendar.YEAR);
                                int monthB = calB.get(Calendar.MONTH) +1;
                                int dayB = calB.get(Calendar.DAY_OF_MONTH);

                                long DateB = (yearB * 10000) + (monthB * 100) + dayB;         // get adjusted date
                                                      
                                if ((i < 7 || (i == 7 && cal_time >= 700)) && (DateB == 20120612 || DateB == 20120717 || DateB == 20120807 || DateB == 20120918)) {         // if day of event

                                    daysArray.days[i] = 1;        // set ok in array

                                    if (max < index) {
                                        max = index;     // bump max days to view if necessary
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /*
            if (club.equals( "santaana" ) && mtype.equals( "Adult Female" )) {
            
            advdays[2] = oldDays3;     // restore original days
            advdays[5] = oldDays6;
            }
             */

            if (club.equals("cecc") && mtype.startsWith("Spouse")) {

                advdays[2] = oldDays3;     // restore original days
                advdays[5] = oldDays6;
            }

            //
            // determine days in advance for this day (day of sheet)
            //
            day_numT = day_num;                   // get current tee sheet's day of the week (1 - 7)

            day_numT--;                           // convert day_num to index (0 - 6)
            days = advdays[day_numT];             // get days in advance
            orig_days = origdays[day_numT];       // get original days in advance (before adjustments)

            //
            //  If original 'days in adv' for this day is 0 and we are before the adv time, then do not allow access to any tee times
            //
            if (orig_days == 0 && cal_time < advtimes[day_numT]) {

                restrictAll = true;         // indicate no member access
            }
            

            //
            //  If Hazeltine, check if days in adv should change
            //
            if (club.equals("hazeltine")) {

                if (day_num == 3) {     // if Tuesday

                    //
                    //  If a Female and sub-type is 'After Hours', '9 holer', or combo, then set Tuesdays to 14 days adv.
                    //
                    if ((mtype.equals("Adult Female")) && (msubtype.equals("After Hours") || msubtype.equals("9 Holer")
                            || msubtype.startsWith("AH-") || msubtype.equals("9/18 Holer"))) {

                        days = 14;      // set 14 days in advance for Tuesdays (all 'After Hours' and 9-Holers)
                    }

                } else {

                    if (day_num == 5) {     // if Thursday

                        if ((mtype.equals("Adult Female")) && (msubtype.equals("18 Holer") || msubtype.startsWith("AH-9/18")
                                || msubtype.startsWith("AH-18") || msubtype.equals("9/18 Holer"))) {

                            days = 14;      // set 14 days in advance for Thursdays (all 18-Holers)
                        }
                    }
                }
            }


            //
            //  If Brooklawn, check if days in adv should change
            //
            if (club.equals("brooklawn")) {

                //
                //  If Tues and a Female and sub-type is '18 holer', then set Tuesdays to 7 days adv.
                //
                if ((day_num == 3 || (day_num == 5 && date == 20130905)) && (msubtype.equals("18 Holer") || msubtype.equals("9/18 Holer")) && (index < 7 || (index == 7 && cal_time > 1459))) {

                    days = 7;      // set 7 days in advance for Tuesdays
                }

                //
                //  If Wed and a Female and sub-type is '9 holer', then set Wednesdays to 7 days adv.
                //
                if (day_num == 4 && (msubtype.equals("9 Holer") || msubtype.equals("9/18 Holer")) && (index < 7 || (index == 7 && cal_time > 729))) {

                    days = 7;      // set 7 days in advance for Wednesdays
                }
            }


            //
            //  If Sharon Heights, check if days in adv should change
            //
            if (club.equals("sharonheights")) {

                //
                //  If Tues and a Female and sub-type is '18 holer', then set Tuesdays to 14 days adv.
                //
                if (day_num == 3 && msubtype.equals("18 Holer") && (index < 14 || (index == 14 && cal_time > 659))) {

                    days = 14;      // set 14 days in advance for Tuesdays
                }
            }

            //
            //  If Seaclif CC, check if days in adv should change
            //
            if (club.equals("seacliffcc")) {

                //
                //  If Tues and a Female and sub-type is '18 holer', then set Tuesdays to 30 days adv.
                //
                if (day_num == 3 && msubtype.equals("18 Holer") && (index < 30 || (index == 30 && cal_time > 559))) {

                    days = 30;      // set 30 days in advance for Tuesdays
                }
            }

            //
            //  If Green Hills CC, check if days in adv should change
            //
            if (club.equals("greenhills")) {

                //
                //  If Thurs and a Female and sub-type is 'Ladies', then set Thursdays to 7 days adv.
                //
                if (day_num == 5 && msubtype.equals("Ladies") && (index < 7 || (index == 7 && cal_time > 1529))) {

                    days = 7;      // set 7 days in advance for Thursdays
                }
            }

            //
            //  If Green Hills CC, check if days in adv should change
            //
            if (club.equals("rhillscc")) {

                //
                //  If Thurs and a Female and sub-type is 'Ladies', then set Thursdays to 7 days adv.
                //
                if ((day_num == 3 || day_num == 5) && msubtype.equals("Ladies") && (index < 14 || (index == 14 && cal_time > 659))) {

                    days = 14;      // set 7 days in advance for Thursdays
                }
            }

            //
            //  If Los Altos G&CC and Thursday morning, female members are allowed to book 14 days out.
            //
            if (club.equals("lagcc")) {

                //
                //  If Thurs and a Female and sub-type is 'Ladies', then set Thursdays to 7 days adv.
                //
                if (((day_num == 3 && index > 3) || (day_num == 5 && index > 1) || (date == 20120907 && day_num == 6 && index > 3)) && mtype.endsWith("Female") && (index < 14 || (index == 14 && cal_time >= 100))) {

                    days = 14;      // set 7 days in advance for Thursdays
                }
            }


            //
            //  If Palo Alto Hills, check if days in adv should change
            //
            if (club.equals("paloaltohills")) {

                //
                //  If Thurs and a Female and sub-type is 'Ladies', then set Thursdays to 7 days adv.
                //
                if (day_num == 5 && msubtype.equals("18 Holer") && (index < 30 || (index == 30 && cal_time > 559))) {

                    days = 30;      // set 7 days in advance for Thursdays
                }
            }


            //
            //  If Royal Oaks WA, check if days in adv should change
            //
            if (club.equals("royaloaks")) {

                //
                //  If Tues, Wed or Fri and a Female then set that day to 7 days adv.
                //
                if ((dateShort >= 301 && dateShort <= 1031) && (day_num == 3 || day_num == 6) && mtype.equals("Adult Female") && (index < 7 || (index == 7 && cal_time > 729))) {

                    days = 7;      // set 7 days in advance for Tuesdays
                }
            }

            //
            //  If Indian Hills CC, check if days in adv should change
            //
            if (club.equals("indianhillscc")) {

                if (dateShort >= 401 && dateShort <= 1015 && day_num == 3 && msubtype.equals("18 Holer") && (index < 14 || index == 14 && cal_time >= 800)) {

                    days = 14;
                }
            }

            if (club.equals("ccfairfax")) {

                if (dateShort >= 401 && dateShort <= 1031 && day_num == 4 && (msubtype.equals("18 Holer") || msubtype.equals("9 Holer")) && index <= 14) {

                    days = 14;
                }
            }

            if (club.equals("algonquin")) {

                if (dateShort >= 401 && dateShort <= 1031 && day_num == 4 && msubtype.equals("9 Holer") && index <= 14) {

                    days = 14;
                }
            }

            if (club.equals("rivieracc")) {

                if ((day_num == 3 || day_num == 5) && msubtype.equals("RWGA") && index <= 14) {

                    days = 14;
                }
            }

            //  If Tualatin CC and Tuesday morning, Primary/Spouse Female members are allowed to book up to 7 days in advance.
            if (club.equals("tualatincc")) {

                if (day_num == 3 && (mtype.equals("Primary Female") || mtype.equals("Spouse Female")) && (index < 7 || index == 7 && cal_time >= 700)) {

                    days = 7;
                }
            }
            
            // If Brookside CC and Tue/Wed/Thu, "League" members are allowed to book up to 14 days in advance (Tue has a different starting time than Wed/Thu).
            if (club.equals("brooksidecountryclub")) {
                /*
                if ((day_num == 3 || day_num == 4 || day_num == 5) && msubtype.equals("League") && (index < 14 || (index == 14 
                        && ((day_num == 3 && cal_time >= 1000) || ((day_num == 4 || (day_num == 5 && date == 20130530)) && cal_time >= 800))))) {
                    
                    days = 14;
                }
                 */
            }
            
            if (club.equals("bhamcc")) {
                
                if (day_num == 5 && mtype.equals("Adult Female")) {
                    
                    days = 6;
                }
            }

            if (club.equals("overlakegcc")) {
                
                if ((day_num == 3 || day_num == 6) && msubtype.equals("18 Holer") && (index < 7 || index == 7 && cal_time >= 600)) {
                    
                    days = 7;
                }
            }
            
            if (club.equals("miravista")) {
                
                if (day_num == 5 && msubtype.equals("18 Holer") && index2 <= 21) {
                    
                    days = 21;
                }
            }
            
            /*        // ON HOLD (case 1486)
            //
            //  If Los Coyotes, check if days in adv should change
            //
            if (club.equals( "loscoyotes" ) && mtype.startsWith( "Secondary" )) {
            
            //
            //  If Tues, then set Tuesdays to 8 days adv.
            //
            if (day_num == 3 && (index < 8 || (index == 8 && cal_time > 559))) {
            
            days = 8;      // set 8 days in advance for Tuesdays
            }
            }
             */


            if (club.equals("congressional")) {      // Congressional - check for guests-only day

               // if (index2 > days && (courseName1.startsWith("Open Cours") || courseName1.equals("Jones Course") || courseName1.equals("-ALL-"))) {   // if beyond normal days in adv and Open Course
                if (index2 > days) {   // if beyond normal days in adv 

                    if (mship.startsWith("Beneficiary") || mship.startsWith("Honorar") || mship.equals("Resident Active")
                            || mship.equals("Resident Twenty") || mship.equals("Test")) {

                        if (index2 < 29 || (index2 == 29 && cal_time > 1459)) {   // if within the 30 day limit for guest times

                            congGuestDay = true;                                    // indicate this is a Guest Day
                        }

                    } else {

                        if (mship.startsWith("Junior") || mship.startsWith("Non Residen") || mship.equals("Resident Absent")
                                || mship.equals("Resident Inactive")) {

                            if (index2 < 29 || (index2 == 29 && cal_time > 1659)) {   // if within the 30 day limit for guest times

                                congGuestDay = true;                                    // indicate this is a Guest Day
                            }
                        }
                    }
                }
            }

            if (club.equals("canterburygc")) {                      // Canterbury - check for guests-only day

                if (index2 > 14 || (index2 == 14 && cal_time < 800)) {       // if beyond their normal days in advance

                    congGuestDay = true;                                    // indicate this is a Guest Day
                }
            }

            if (club.equals("minikahda")) {      // Minikahda - check for guests-only day

                if (days == 6) {          // if days in adv has not reached 7, then its too early for max days in adv (before 8:00 AM)

                    if (index2 > 2) {      // if beyond normal days in adv

                        miniGuestDay = true;       // indicate this is a Guest Day
                    }

                } else {                  // after 8:00 AM

                    if (index2 > 3) {      // if beyond normal days in adv

                        miniGuestDay = true;       // indicate this is a Guest Day
                    }
                }
            }


            //
            //  If beyond days in advance for this member, then do not allow access to any Wait Lists
            //
            if (index > days || (index == days && cal_time < advtimes[day_numT])) {

                skip_waitlist = true;         // indicate no member access to Wait Lists
            }
            
            
            //
            //   Get Wait List info
            //
            parmItem parmWaitLists = new parmItem();          // allocate a parm block
            try {
                getItem.getWaitLists(date, courseName1, day_name, parmWaitLists, con);
            } catch (Exception e) {
                SystemUtils.buildDatabaseErrMsg("Error loading wait list data.", e.getMessage(), out, true);
            }

            //int WL_count = 0;
            int WL_index = 0;

            boolean[] WLbtn_displayed = new boolean[parmWaitLists.count];
            int[] WLbtn_index = new int[parmWaitLists.count];

            //int [] WLbehavior_index = new int [parmWaitLists.count]; // parmWaitLists.member_view_teesheet
         /*
            try {
            
            PreparedStatement pstmtW = con.prepareStatement (
            "SELECT wait_list_id, member_view_teesheet " +
            "FROM wait_list " +
            "WHERE " +
            "DATE_FORMAT(sdatetime, '%Y%m%d') <= ? AND DATE_FORMAT(edatetime, '%Y%m%d') >= ? AND " +
            ((courseName1.equals("") || courseName1.equals("-ALL-")) ? "" : "(course = ? OR course = '-ALL-') AND ") +
            day_name + " = 1 AND enabled = 1");
            
            pstmtW.clearParameters();
            pstmtW.setLong(1, date);
            pstmtW.setLong(2, date);
            if (!courseName1.equals("") && !courseName1.equals("-ALL-")) pstmtW.setString(3, courseName1);
            ResultSet rsW = pstmtW.executeQuery();
            
            while (rsW.next()) {
            
            WLbtn_index[WL_count] = rsW.getInt(1);
            WLbehavior_index[WL_count] = rsW.getInt(2);
            WLbtn_displayed[WL_count] = false;
            WL_count++;
            }
            
            pstmtW.close();
            
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
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</BODY></HTML>");
            out.close();
            return;
            }
             */
            //out.println("<!-- parmWaitLists.count="+parmWaitLists.count+", curr_time=" + curr_time + " -->");




            if (new_skin) {  // New skin starts here

                //
                //  Check for Mobile user
                //
                if (mobile == 0) {

                    //
                    //  Non-Mobile Browser - Build the HTML page to prompt user for a specific time slot
                    //
                    Common_skin.outputHeader(club, activity_id, "Member Tee Sheet", false, out, req);
                    /*
                    if (req.getParameter("select_jump") != null) {
                        out.println("<script type=\"text/javascript\">");
                        out.println("$(document).ready(function() {");
                        out.println("$('html, body').animate({");
                        out.println("     scrollTop: $(\"#jump999\").offset().top");
                        out.println(" }, 250);");
                        out.println("});");
                        out.println("</script>");
                    }
                    *      // replaced with select_jump boolean
                    */
                    out.println("</head>");
                    Common_skin.outputBody(club, activity_id, out, req);
                    Common_skin.outputTopNav(req, club, activity_id, out, con);
                    Common_skin.outputBanner(club, activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);    // no zip code for Dining
                    Common_skin.outputSubNav(club, activity_id, out, con, req);
                    Common_skin.outputPageStart(club, activity_id, out, req);
                    Common_skin.outputBreadCrumb(club, activity_id, out, "Select a Tee Time", req);
                    Common_skin.outputLogo(club, activity_id, out, req);


                    //**********************************************************
                    //  Build Course selection
                    //**********************************************************
                    //

                    if (select_jump == false) {                  // if select_jump NOT requested from Member_select
                        
                        // Start Calendar / Instrictions container
                        out.println("<div class=\"member_sheet tabular_container\">");
                        out.println("<div class=\"tabular_row\">");

                        //**********************************************************
                        //  Build calendar for selecting a new day
                        //**********************************************************
                        //

                        out.println("<div class=\"tabular_cell member_sheet_left\">");
                        out.println("<div id=\"member_sheet_calendar\" class=\"calendar member weeks_in_selected_" + week_count + "\" data-ftdefaultdate=\"" + month + "/" + day + "/" + year + "\"></div>");
                        out.println("</div>");
                        //Calendar cal_date = new GregorianCalendar(); //Calendar.getInstance();
                        //int cal_year = cal_date.get(Calendar.YEAR);
                        //int cal_month = cal_date.get(Calendar.MONTH) + 1;
                        //int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

                        boolean useTeeSheetDates = false;
                        //if (cal_month != month && cal_year != year) {
                        if (month2 != month && year2 != year) {
                            useTeeSheetDates = true;
                        }


                        //**********************************************************
                        //  Continue with instructions and tee sheet
                        //**********************************************************

                        // Start right container div
                        out.println("<div class=\"tabular_cell member_sheet_right\">");

                        //
                        //  If multiple courses, then add a drop-down box for course names
                        //
                        if (multi != 0) {           // if multiple courses supported for this club

                            if (!club.equals("merion")) {      // Merion members can only view East Course - do not allow them to switch

                                //
                                //  use 2 forms so you can switch by clicking either a course or a date
                                //
                                // Start corse select div
                                out.println("<div class=\"select\">");
                                out.println("<form action=\"Member_sheet\" method=\"get\" name=\"cform\">");
                                //out.println("<input type=\"hidden\" name=\"i" + index2 + "\" value=\"\">");   // use current date
                                out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + month + "/" + day + "/" + year + "\">");   // use current date

                                cMax = course.size();            // number of courses

                                out.println("<b>Course:</b>&nbsp;&nbsp;");
                                out.println("<select size=\"1\" name=\"course\" onChange=\"$(this).closest('form').submit();\">");

                                for (i = 0; i < cMax; i++) {

                                    courseName = course.get(i);      // get course name from array

                                    boolean skipCourse = false;

                                    if (club.equals("olyclub") && courseName.equals("Cliffs")) {
                                        skipCourse = true;
                                    }

                                    if (skipCourse == false) {

                                        if (courseName.equals(courseName1)) {
                                            out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                                        } else {
                                            out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                                        }
                                    }
                                }
                                out.println("</select>");
                                
                                 //
                                 //  Custom for Philadelphia Cricket Club - add seamless link to their Recip site
                                 //
                                 if (club.equals("philcricket")) {    

                                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                                    out.println("<a class=\"standard_button\" href=\"http://web.foretees.com/v5/servlet/Login?clubname=philcricketrecip&caller=PDG4735&user_name=" +user+ "\" "
                                            + "target=\"_blank\" style=\"background-color: yellow;\" title=\"Link to Recip Site\">"); 
                                    out.println("Go to RECIP Site</a>");
                                 }
                                
                                out.println("</form>");
                                // End corse select div
                                out.println("</div>");


                            }
                        }

                        // Start Instructions div
                        out.println("<div class=\"sub_instructions\">");

                        out.println("<h2>Instructions:</h2>");
                        // check max allowed days in advance for members
                        if (index2 <= days || lstateA[0] == 2
                                || club.equals("oakmont") || mccGuestDay == true || club.equals("invernessclub")) {


                            out.println("<p>To select a tee time, just click on the button containing the time (1st column). "
                                    + "&nbsp;Special Events and Restrictions, if any, are colored (see legend below). "
                                    + "&nbsp;To display a different day's tee sheet, select the date from the calendar on the left.<BR /><BR />");

                            if (club.equals("oakmont") && oakshotgun == false && (day_name.equals("Wednesday") || day_name.equals("Friday"))) {

                                out.println("Times when Multiple Guests are allowed are indicated by the green time button. ");

                            } else if (club.equals("milwaukee") && mccGuestDay == true) {

                                out.println("Times when Multiple Guests are allowed are indicated by the green time button. ");

                            } else if (club.equals("bearpath")) {

                                out.println("Member-only times are indicated by the green time button (if any on this date). ");
                            }

                            if (parm.constimesm > 1) {        // if consecutive tee times supported
                                out.println("To request multiple tee times, select the number of tee times next to the ");
                                out.println("earliest time desired. ");
                            }
                            out.print("</p>");

                            if (club.equals("invernessclub")) {

                                out.println("<p><b>Note: <i>Tee times are only allowed on this day if a guest is included.</i></b></p>");

                            } else if (club.equals("minikahda") && miniGuestDay == true) {

                                out.println("<p><b>Note: <i>Only Guest Times (2 per hour) are allowed on this day. There must be 3 guests included.</i></b></p>");

                            } else if (club.equals("canterburygc") && congGuestDay == true) {

                                out.println("<p><b>Note:</b> This date is not yet available for members to make normal tee times.");
                                out.println("However, you may be allowed to make Guest times (if any on this date).");
                                out.println("Guest times are indicated by the green time button.</p>");

                            } else if ((club.equals("estanciaclub")) && index >= 30) {

                                out.println("<p><b>Note: <i>Only Advance Guest Times may be booked more than 30 days in advance</i></b></p>");

                            } else if (club.equals("napervillecc") && index > 7 && index <= 30) {

                                out.println("<p><b>Note: <i>Only Advance Guest Times may be booked more than 7 days in advance</i></b></p>");
                            }

                        } else {

                            if (club.equals("congressional") && congGuestDay == true) {
                                out.println("<p><b>Note:</b> This date is not yet available for members to make normal tee times.");
                                out.println("However, you may be allowed to make Guest times (if any on this date).");
                                out.println("Guest times are indicated by the green time button.</p>");
                            } else if (!viewable) {        // If tee sheet viewing and booking are blocked at this time
                                out.println("<p><b>Note:</b> This date is not yet available for members to make tee times or view the tee sheet.</p>");
                            } else {
                                out.println("<p><b>Note:</b>This date is not yet available for members to make tee times.");
                                out.println("You are allowed to view this sheet for planning purposes only.</p>");
                            }
                        }

                        //  End Instructions div
                        out.println("</div>");
                        //  End right container
                        out.println("</div>");
                        // Float fix
                        out.println("<div class=\"clearFix\"></div>"); // clear the float

                        // End Calendar/ Instructions row
                        out.println("</div>");
                        // End Calendar/ Instructions container
                        out.println("</div>");
                        
                    } else {               // Member wants to skip the calendar and instructions so more of the tee sheet shows
                        
                        if (courseName1.equals("")) {   // add link to show the full page
                            
                            out.println("<p align=\"center\"><a class=\"tip_text\" style=\"font-size:15px;\" href=\"Member_sheet?index=" +index+ "\">Show Calendar (Change Date)</a></p><BR />");
                        
                        } else {
                            
                            if (club.equals("merion")) {      // Merion members can only view East Course - do not allow them to switch

                                out.println("<p align=\"center\"><a class=\"tip_text\" style=\"font-size:15px;\" href=\"Member_sheet?index=" +index+ "&course=" +courseName1+ "\">Show Calendar (Change Date or Course)</a></p><BR />");                        

                            } else {
                                
                                //
                                //  allow user to switch courses
                                //
                                out.println("<div class=\"select\">");
                                out.println("<form action=\"Member_sheet\" method=\"get\" name=\"cform\">");
                                out.println("<input type=\"hidden\" name=\"select_jump\" value=\"\">");         // use select_jump on course change too
                                out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");  

                                cMax = course.size();            // number of courses

                                out.println("<b>Course:</b>&nbsp;&nbsp;");
                                out.println("<select size=\"1\" name=\"course\" onChange=\"document.cform.submit()\">");

                                for (i = 0; i < cMax; i++) {

                                    courseName = course.get(i);      // get course name from array

                                    boolean skipCourse = false;

                                    if (club.equals("olyclub") && courseName.equals("Cliffs")) {
                                        skipCourse = true;
                                    }

                                    if (skipCourse == false) {

                                        if (courseName.equals(courseName1)) {
                                            out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                                        } else {
                                            out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                                        }
                                    }
                                }
                                out.println("</select>");
                                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"tip_text\" style=\"font-size:15px;\" href=\"Member_sheet?index=" +index+ "&course=" +courseName1+ "\">Show Calendar (Change Date)</a>");                        
                                out.println("</form></div>");    // End corse select div
                            }
                        }
                    }                      // end of IF select_jump

                    //out.println("<a id=\"jump999\" name=\"jump999\"></a>");     // create a jump label for jump from Member_select if user checked the jump option
                    
                    out.println("<div class=\"main_instructions date_course\">");
                    
                    
                    // Start Date / Course
                    out.println("<span>Date: <b>" + day_name + "</b> <b>" + month + "/" + day + "/" + year + "</b></span>");

                    if (club.equals("indianridgecc")) {

                        out.println("<span>The Server Time is: <b class=\"jquery_server_clock\" data-ftclub=\"" + club + "\"></b></span>");
                        //  help link to describe the server clock
                        //out.println("<a class=\"tip_text\" href=\"javascript:void(0);\" onclick=\"showInfo(); return false;\">How is this clock used?</a>");
                    }

                    if (!courseName1.equals("")) {

                        if (club.equals("congressional")) {
                            // The Open Course will be Blue Course on Even Days and Gold Course on Odd Days
                            // The Club Course will be Blue Course on Odd Days and the Gold Course on Even days
                            out.println("<span>Course: <b>" + congressionalCustom.getFullCourseName(date, day, courseName1) + "</b></span>");
                        } else {
                            out.println("<span>Course: <b>" + courseName1 + "</b></span>");
                        }

                    } // end if courseName1 empty
                    
                    //if (!eventA[0].equals( "" )) {

                    //   out.println("<h2>Tee Sheet Legend: <span>(click on Event button to view info)</span></h2>");

                    //} else {
                    
                    out.println("</div>");
                    

                    // Build wait list elements
                    String teesheet_buttons = "";
                    String teesheet_colorlegend = "";
                    
                    if (skip_waitlist == false && restrictAll == false) {    // if ok to show wait list buttons

                        for (int z = 0; z < parmWaitLists.count; z++) {

                            WLbtn_displayed[z] = false;
                            if ((index > 0 || (index == 0 && parmWaitLists.etime[z] > curr_time))) {
                                hashMap.clear();
                                hashMap.put("type", "Member_waitlist");
                                hashMap.put("waitListId", parmWaitLists.id[z]);
                                hashMap.put("course", parmWaitLists.courseName[z]);
                                hashMap.put("returnCourse", courseName1);
                                hashMap.put("index", num);
                                hashMap.put("date", date);

                                if (club.equals("hudsonnatl")) {
                                    teesheet_colorlegend += buildLegendColorItem("restriction", parmWaitLists.color[z], parmWaitLists.name[z]);
                                    //    out.println("<button type=\"button\" style=\"background:" + parmWaitLists.color[z] + "\">" + parmWaitLists.name[z] + "</button>");
                                } else {
                                    //teesheet_buttons += buildLegendLinkItem("waitlist", rcolorA[i], restA[i], String.valueOf(date));
                                    teesheet_buttons += buildLegendLinkItem("waitlist", parmWaitLists.color[z], parmWaitLists.name[z], StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)));
                                    //out.println("<a href=\"javascript:void(0);\" class=\"sheet-legend-waitlist\" style=\"background:" + parmWaitLists.color[z] + "\" " +
                                    //       "onclick=\"window.location.href='Member_waitlist?waitListId=" + parmWaitLists.id[z] + "&date=" + date + "&day=" + day_name + "&index=" +num+ "&returnCourse=" +courseName1+ "&course=" +parmWaitLists.courseName[z]+"'\">" + parmWaitLists.name[z] + "</a>");
                                }

                            }
                        }
                    }
                    
                    // Build event elements
                    if (!eventA[0].equals("")) {       // if any events to display

                        for (i = 0; i < count_event; i++) {     // check for events
                            
                            hashMap.clear();
                            hashMap.put("type", "Member_events2");
                            hashMap.put("name", eventA[i]);
                            hashMap.put("course", ecourseA[i]);
                            hashMap.put("index", 0);

                            if (!eventA[i].equals("")) {
                                teesheet_buttons += buildLegendLinkItem("event", ecolorA[i], eventA[i], StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)));
                                //out.println("<a href=\"javascript:void(0);\" class=\"sheet-legend-event\" title=\"Click for more information on this event.\" style=\"background:" + ecolorA[i] + "\" onClick=\"window.open('Member_sheet?event=" +eventA[i]+ "', 'newwindow', 'height=700, width=900, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">" + eventA[i] + "</a>");
                            }
                        }
                    }

                    // Build restriction color legend
                    if (!restA[0].equals("")) {       // if any restrictions to display

                        for (i = 0; i < count_rest; i++) {     // check for restrictions

                            if (!restA[i].equals("")) {
                                teesheet_colorlegend += buildLegendColorItem("restriction", rcolorA[i], restA[i]);
                                //out.println("<div class=\"sheet-legend-restriction\" style=\"background:" + rcolorA[i] + "\">" + restA[i] + "</div>");
                            }
                        }
                    }

                    //
                    //  Custom check for Cordillera Canned Restrictions - add to legend
                    //
              /*
                    if (club.equals( "cordillera" ) && dateShort > 413 && dateShort < 1101 && index < 4) {  // if in season and within 3 days in advance
                    
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("<button type=\"button\" style=\"background:" +cordRestColor+ "\">Starter Times</button>");
                    }
                     */

                    boolean lottDisp = false;
                    // Build lottery color legend
                    if (!lottA[0].equals("")) {       // if any lotteries to display
                        for (i = 0; i < count_lott; i++) {     // check for restrictions

                            if (!lottA[i].equals("") && lstateA[i] < 5) {
                                teesheet_colorlegend += buildLegendColorItem("lottery", lcolorA[i], lottA[i]);
                                //out.println("<div class=\"sheet-legend-lottery\" style=\"background:" + lcolorA[i] + "\">" +lottA[i]+ "</div>");
                                // out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                                lottDisp = true;
                            }
                        }
                    }

                    i = 0;
                    if ((teesheet_buttons.length() > 0) || (teesheet_colorlegend.length() > 0)) {
                        // Start Tee Sheet Color/Button Legend
                        out.println("<div class=\"tee_sheet_color_legend\">");
                        if (teesheet_buttons.length() > 0) {       // if any events or waitlists to display

                            out.println("<p class=\"button_legend\">");
                            out.println(teesheet_buttons);
                            out.println("</p>");

                        }

                        // Output any color legends
                        if (teesheet_colorlegend.length() > 0) {

                            out.println("<p class=\"color_legend\">");
                            out.println(teesheet_colorlegend);
                            out.println("<p>");

                        }

                        out.println("</div>");
                        // End Tee Sheet Color/Button Legend
                    }

                    //
                    //  Display a note if members are not allowed to access tee times today
                    //
                    if (restrictAll == true && !club.equals("congressional") && (!club.equals("coloradogc") || !day_name.equals("Monday"))) {

                        if (club.equals("jonathanslanding")) {
                            out.println("<div class=\"notice_element sub_instructions\">Please contact the Starter's Booth for tee times on this day (772-231-1188).</div>");
                        } else if (club.equals("gaaphoenix")) {
                            out.println("<div class=\"notice_element sub_instructions\">Please contact the GAA Phoenix Campus for a tee time for today after 7:30 AM (480) 857-1574.</div>");
                        } else if (club.equals("merion")) {
                            out.println("<div class=\"notice_element sub_instructions\">Tee times cannot yet be booked for this date because it falls outside of the one week time frame for resident members.<br /><br />Registration for this date's tee sheet will open up for all members exactly one week prior to this date.</div>");
                        } else {
                            out.println("<div class=\"notice_element sub_instructions\">Please contact the Golf Shop for tee times on this day.</div>");
                        }
                    }

                    if (club.equals("minikahda") && miniGuestDay == true) {

                        out.println("<div class=\"notice_element sub_instructions\">This far in advance only three-guest groups can be made per hour.</div>");

                        parm.constimesm = 1;        // do not allow consecutive tee times for this day
                    }

                    //
                    //  If Old Oaks CC and this is a 'Lottery Only" Day, then only display the lottery button.
                    //
                    if (lotteryOnly == true) {

                        //
                        //  This is Old Oaks CC and there is only a lottery for this date.
                        //  Just display a lottery request button.
                        //
                        hashMap.clear();
                        hashMap.put("type", "Member_mlottery");
                        hashMap.put("lstate", lstateA[0]);
                        hashMap.put("lname", lottA[0]);
                        hashMap.put("slots", slotsA[0]);
                        hashMap.put("date", date);
                        hashMap.put("index", index);
                        hashMap.put("course", courseName1);
                        hashMap.put("p5", "No");// they always restrict to 4

                        out.println("<div class=\"tee_sheet_color_legend\">" + buildLegendLinkItem("lottery", lcolorA[0], "Note: Click Here to Request a Tee Time", StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap))) + "</div>");
                        //
                        //  End of HTML page
                        //
                        Common_skin.outputPageEnd(club, activity_id, out, req);
                        out.close();
                        if (Gzip == true) {
                            resp.setContentLength(buf.size());                 // set output length
                            resp.getOutputStream().write(buf.toByteArray());
                        }
                        return;
                    }


                    //
                    //**********************************************
                    //   Check for Member Notice from Pro
                    //**********************************************
                    //
                    String memNoticeMsg = verifySlot.checkMemNotice(date, 0, 0, courseName1, day_name, "teesheet", false, con);

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
                                    "SELECT mon, tue, wed, thu, fri, sat, sun, message, bgColor "
                                    + "FROM mem_notice "
                                    + "WHERE sdate <= ? AND edate >= ? AND "
                                    + "(courseName = ? OR courseName = ?) AND teesheet=1 AND activity_id = 0");

                            notice_pstmt.clearParameters();        // clear the parms and check player 1
                            notice_pstmt.setLong(1, date);
                            notice_pstmt.setLong(2, date);
                            notice_pstmt.setString(3, courseName1);
                            notice_pstmt.setString(4, "-ALL-");
                            notice_rs = notice_pstmt.executeQuery();      // execute the prepared stmt

                            //out.println("<div class=\"pro_member_notices sub_instructions\"><h3>Important Notice:</h3>");
                            out.println("<div class=\"mem_notices\"><h3>*** Important Notice ***</h3>");

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
                                if (notice_bgColor.equals("")) {
                                    notice_bgColor = "inherit";
                                }

                                //  NOTE:  the bgclor option does not work.  It needs to be in the div above, but we can't because there might be more than one
                                //         member notice for this tee sheet, each with a different color.  No one has complained yet, so we might as well just let it go.
                                //
                                //   Also, there is no pro_member_notice or pro_member_notices style sheet tags found anywhere???????  It is just using sub_instructions.

                                if ((notice_mon == 1 && day_name.equals("Monday")) || (notice_tue == 1 && day_name.equals("Tuesday")) || (notice_wed == 1 && day_name.equals("Wednesday"))
                                        || (notice_thu == 1 && day_name.equals("Thursday")) || (notice_fri == 1 && day_name.equals("Friday")) || (notice_sat == 1 && day_name.equals("Saturday"))
                                        || (notice_sun == 1 && day_name.equals("Sunday"))) {
                                    out.println("<div class=\"mem_notice\" style=\"background-color:" + notice_bgColor + ";\">" + notice_msg + "</div>");
                                }

                            }  // end WHILE loop

                            out.println("</div>");

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
                            out.println("<a href=\"Member_announce\">Return</a>");
                            out.println("</BODY></HTML>");
                            out.close();
                        }

                    } // end if member notice


                    //
                    // Only display tee sheet if 1) days in advance = viewable days in advance and all days have the same days in advance 2) member viewing furthest day in advance before viewable time
                    //
                    if (viewable == false) {

                        //
                        // Print out a message telling the member this day is not available for booking/viewing
                        //

                        out.println("<div class=\"notice_block sub_instructions\">");
                        out.println("<h2>Notice:</h2>");
                        out.println("<p>This day is not yet available for booking or viewing of tee times.</p>");
                        out.println("<p>Please use the calendars above to select a different day's tee sheet or to reload this day's tee sheet.</p>");
                        out.println("</div>");

                        //
                        //  End of HTML page
                        //
                        Common_skin.outputPageEnd(club, activity_id, out, req);
                        out.close();
                        if (Gzip == true) {
                            resp.setContentLength(buf.size());                 // set output length
                            resp.getOutputStream().write(buf.toByteArray());
                        }
                        return; // Exit servlet
                    }


                    // Start Tee Sheet Table

                    out.println("<table class=\"member_sheet_table standard_list_table\">");    // tee sheet table
                    out.println("<caption>");
                    // Start Tee Sheet Legend
                    //out.println("<h2>Tee Sheet Legend:</h2>");
                    //}
                    //  Do not display abbreviations portion for tee sheet legend for Desert Forest GC
                    if (club.equals("desertforestgolfclub") || club.equals("sawgrass")) {
                        //out.println("<br>");
                    } else {

                        out.println("<p class=\"fb_legend\"><b>F/B:</b>");
                        out.println(buildLegendItem("F", "Front Nine"));
                        out.println(buildLegendItem("B", "Back Nine"));
                        out.println(buildLegendItem("O", "Open (for cross-overs)"));
                        out.println(buildLegendItem("S", "Shotgun Event"));
                        out.println("</p>");

                        out.println("<p class=\"cw_legend\"><b>C/W:</b>");

                        if (club.equals("cordillera")) {     // only show the following modes for Cordillera

                            out.println(buildLegendItem("CG", "Golf Car"));
                            out.println(buildLegendItem("W", "Walk"));

                        } else {

                            for (int ic = 0; ic < parmc.tmode_limit; ic++) {
                                if (!parmc.tmodea[ic].equals("") && parmc.tOpt[ic] == 0) {                  // if tmode exists and NOT pro-only

                                    if (club.equals("peninsula") && parmc.tmodea[ic].equals("WLK")) {
                                        out.println(buildLegendItem(parmc.tmodea[ic], "<b>Walking Permitted Daily After 3:00 PM</b>"));
                                        //   out.println(buildLegendItem("WLK", "<b>Walking Permitted Daily After 3:00 PM</b>", ((ic+1)<parmc.tmode_limit))));
                                    } else {
                                        out.println(buildLegendItem(parmc.tmodea[ic], parmc.tmode[ic]));
                                    }
                                }
                            }
                        }       // end of IF Cordillera

                        out.println("<span>(__9 = 9 holes)</span>");
                        out.println("</p>");
                    }
    

                    out.println("</caption>");
                    // Define column classes
                    out.println("<colgroup>");
                    out.println("<col class=\"col-time\" />");
                    if (parm.constimesm > 1) {      // if Consecutive Tee Times allowed
                        out.println("<col class=\"col-ctime\" />");
                    }
                    if (multi != 0) {               // if multi course
                        out.println("<col class=\"col_course\" />");
                    }
                    out.println("<col class=\"col_fb\" />");
                    out.println("<col class=\"col_player col_player1\" />");
                    out.println("<col class=\"col_cw col_player1cw\" />");
                    out.println("<col class=\"col_player col_player2\" />");
                    out.println("<col class=\"col_cw col_player2cw\" />");
                    out.println("<col class=\"col_player col_player3\" />");
                    out.println("<col class=\"col_cw col_player3cw\" />");
                    out.println("<col class=\"col_player col_player4\" />");
                    out.println("<col class=\"col_cw col_player4cw\" />");
                    if (fivesALL != 0) {
                        out.println("<col class=\"col_player col_player5\" />");
                        out.println("<col class=\"col_cw col_player5cw\" />");
                    }

                    out.println("</colgroup>");
                    // Create table headers
                    
                    out.println("<thead><tr>");
                    out.println("<th>Time</th>");

                    if (parm.constimesm > 1) {      // if Consecutive Tee Times allowed
                        out.println("<th>#</th>");
                    }

                    //           if (courseName1.equals( "-ALL-" )) {
                    if (multi != 0) {        // case 1509
                        out.println("<th>Course</th>");
                    }

                    out.println("<th>F/B</th>");

                    String hndcp_text = "";
                    if (disp_hndcp) {
                        hndcp_text = " <span class=\"small-text\">hndcp</span>";
                    }

                    out.println("<th>Player 1" + hndcp_text + "</th>");
                    out.println("<th>C/W</th>");

                    out.println("<th>Player 2" + hndcp_text + "</th>");
                    out.println("<th>C/W</th>");

                    out.println("<th>Player 3" + hndcp_text + "</th>");
                    out.println("<th>C/W</th>");

                    out.println("<th>Player 4" + hndcp_text + "</th>");
                    out.println("<th>C/W</th>");

                    if (fivesALL != 0) {
                        out.println("<th>Player 5" + hndcp_text + "</th>");
                        out.println("<th>C/W</th>");
                    }
                    // End table header
                    out.println("</tr></thead>");

                    // Start table body
                    out.println("<tbody>");


                } else {

                    //
                    //***************************************************************
                    //  MOBILE USER - start the output page for a mobile device
                    //***************************************************************
                    //
                    out.println(SystemUtils.HeadTitleMobile("ForeTees Member Tee Sheet"));
                    out.println(SystemUtils.BannerMobile());

                    //
                    // Only display tee sheet if< 1) days in advance = viewable days in advance and all days have the same days in advance 2) member viewing furthest day in advance before viewable time
                    //
                    if (viewable == false) {

                        //
                        // Print out a message telling the member this day is not available for booking/viewing
                        //
                        out.println("<div class=\"headertext\">**NOTICE**</div>");
                        out.println("<div class=\"content\">");

                        out.println("<div>This day is not yet available for booking or viewing of tee times.<br><br>");
                        out.println("Please use the calendars above to select a different day's tee sheet or to reload this day's tee sheet.</div>");

                        out.println("<form action=\"Member_select\" method=\"get\">");
                        out.println("<input type=submit value=\"Return\" name=\"Return\">");

                        out.println("</div></body></html>");
                        out.close();
                        if (Gzip == true) {
                            resp.setContentLength(buf.size());                 // set output length
                            resp.getOutputStream().write(buf.toByteArray());
                        }
                        return;
                    }

                    //
                    //  Ok for user to display this tee sheet
                    //
                    out.println("<div class=\"headertext\">Tee Sheet</div>");
                    out.println("<div class=\"smheadertext\"> <a href=\"Member_select\">Change Date</a> </div>");
                    out.println("<div class=\"smheadertext\"><strong>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year);   // Date
                    out.println("<br /> " + courseName1);                                                          // Course
                    out.println("</strong></div>");

                    // allow user to switch the display option
                    out.println("<form action=\"Member_sheet\" method=\"post\" name=\"mobileform\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");

                    out.println("<div class=\"smheadertext\">Tee Sheet - Display:");
                    out.println("<select size=\"1\" name=\"displayOpt\" onChange=\"document.mobileform.submit()\">");
                    if (displayOpt.equals("Morning")) {
                        out.println("<option selected value=\"Morning\">Morning</option>");
                    } else {
                        out.println("<option value=\"Morning\">Morning</option>");
                    }
                    if (displayOpt.equals("Mid-Day")) {
                        out.println("<option selected value=\"Mid-Day\">Mid-Day</option>");
                    } else {
                        out.println("<option value=\"Mid-Day\">Mid-Day</option>");
                    }
                    if (displayOpt.equals("Afternoon")) {
                        out.println("<option selected value=\"Afternoon\">Afternoon</option>");
                    } else {
                        out.println("<option value=\"Afternoon\">Afternoon</option>");
                    }
                    if (displayOpt.equals("All")) {
                        out.println("<option selected value=\"All\">Entire Day</option>");
                    } else {
                        out.println("<option value=\"All\">Entire Day</option>");
                    }
                    if (displayOpt.equals("Available")) {
                        out.println("<option selected value=\"Available\">Open Times Only</option>");
                    } else {
                        out.println("<option value=\"Available\">Open Times Only</option>");
                    }
                    out.println("</select>");
                    out.println("</div></form>");


                    //
                    // Build start of tee sheet table for Mobile users
                    //
                    out.println("<div class=\"content\">");
                    out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"times\">");
                    out.println("<tr class=\"tableheader\">");
                    out.println("<td><strong>Time</strong></td>");
                    if (allowMulti == true) {
                        out.println("<td><strong>#</strong></td>");
                    }
                    out.println("<td><strong>F/B</strong></td>");
                    out.println("<td><strong>Players</strong></td>");
                    out.println("</tr>");

                }    // end of IF Mobile User



                //
                // Determin if club uses a Tee Sheet Provider
                // and if so then load the configured guest 
                // types and determin how we are going to display them
                //
                boolean tsp_active = false;
                boolean tsp_hide_outside_rounds = false;
                boolean tmp_allow_members_to_join = true;   // default for optimization later on when checking
                String tsp_display_name = "";               // text to be displayed in place of outside guest type and player name
                String tsp_guest_type = "";                 // make this an arraylist we can cycle through if a club wants multiple tsp


                try {

                    // for the purpose of rendering the member tee sheet lets grab all 
                    ResultSet tmp_rs = null;
                    PreparedStatement tmp_pstmt = con.prepareStatement(
                            "SELECT guest_type, display_name, allow_members_to_join, hide_outside_times " +
                            "FROM teesheet_partner_config " +
                            "WHERE enabled = 1");

                    tmp_pstmt.clearParameters();
                  //tmp_pstmt.setInt(1, 0);
                    tmp_rs = tmp_pstmt.executeQuery();

                    while (tmp_rs.next()) {

                        tsp_active = true;

                        tsp_hide_outside_rounds = (tmp_rs.getInt("hide_outside_times") == 1);
                        tmp_allow_members_to_join = (tmp_rs.getInt("allow_members_to_join") == 1);
                        tsp_display_name = tmp_rs.getString("display_name");
                        tsp_guest_type = tmp_rs.getString("guest_type");
                    }

                    tmp_pstmt.close();

                } catch (Exception exc) {

                    Utilities.logError("Member_sheet: Error loading TSP data. club=" + club + ", tsp_active=" + tsp_active + ", err=" + exc.toString());

                }


                //
                //  Get the tee sheet for this date and course
                //
                String stringTee = "";
                String orderBy = "";

                if (courseName1.equals("-ALL-")) {

                    if (club.equals("edisonclub") || club.equals("loscoyotes") || club.equals("sawgrass")) {
                        
                        orderBy = "t.time, c.clubparm_id, t.fb";

                        // sort courses by the order in which they where entered
                        stringTee = ""
                                + "SELECT t.* "
                                + "FROM teecurr2 t, clubparm2 c "
                                + "WHERE t.date = ? AND t.courseName = c.courseName "
                                + "ORDER BY " + orderBy;
                    } else {

                        orderBy = "time, courseName, fb";
                        
                        stringTee = "SELECT * "
                                + "FROM teecurr2 WHERE date = ? ORDER BY " + orderBy;
                    }

                } else {
                    
                    if (club.equals("elcaminocc")) {
                        orderBy = "fb, time";
                    } else {
                        orderBy = "time, fb";
                    }
                    
                    stringTee = "SELECT * "
                            + "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY " + orderBy;
                }
                PreparedStatement pstmt = con.prepareStatement(stringTee);

                pstmt.clearParameters();        // clear the parms
                pstmt.setLong(1, date);         // put the parm in pstmt

                if (!courseName1.equals("-ALL-")) {
                    pstmt.setString(2, courseName1);
                }

                rs = pstmt.executeQuery();      // execute the prepared stmt

                loop1:
                while (rs.next()) {

                    hr = rs.getInt("hr");
                    min = rs.getInt("min");
                    tee_time = rs.getInt("time");
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
                    fb = rs.getShort("fb");
                    player5 = rs.getString("player5");
                    user5 = rs.getString("username5");
                    p5cw = rs.getString("p5cw");
                    hndcp5 = rs.getFloat("hndcp5");
                    lottery = rs.getString("lottery");
                    courseNameT = rs.getString("courseName");
                    blocker = rs.getString("blocker");
                    rest5 = rs.getString("rest5");
                    bgcolor5 = rs.getString("rest5_color");
                    lottery_color = rs.getString("lottery_color");
                    userg1 = rs.getString("userg1");
                    userg2 = rs.getString("userg2");
                    userg3 = rs.getString("userg3");
                    userg4 = rs.getString("userg4");
                    userg5 = rs.getString("userg5");
                    orig_by = rs.getString("orig_by");
                    p91 = rs.getInt("p91");
                    p92 = rs.getInt("p92");
                    p93 = rs.getInt("p93");
                    p94 = rs.getInt("p94");
                    p95 = rs.getInt("p95");
                    hole = rs.getString("hole");
                    custom_disp1 = rs.getString("custom_disp1");
                    custom_disp2 = rs.getString("custom_disp2");
                    custom_disp3 = rs.getString("custom_disp3");
                    custom_disp4 = rs.getString("custom_disp4");
                    custom_disp5 = rs.getString("custom_disp5");
                    orig1 = rs.getString("orig1");
                    orig2 = rs.getString("orig2");
                    orig3 = rs.getString("orig3");
                    orig4 = rs.getString("orig4");
                    orig5 = rs.getString("orig5");
                    make_private = rs.getInt("make_private");

                    skipconstimes = false;      // init flag to skip consecutive time option
                    
                    // If in use, check if it's in use by our session
                    /*
                    if(in_use > 0 && user.equals(rs.getString("in_use_by"))){
                        if(verifySlot.checkInSession(date, tee_time, (int) fb, courseNameT, session)){
                            in_use = 0; // It's in use by us, mark it as not in use for this list
                        }
                    }
                    *        // not sure why we would want to do this, but it allows users to open multiple tabs and access the same tee time from each
                    *        //  Also, we now release any tee times that are in the user's session when they enter this servlet.
                    */
                    
                    // Check if "Hide Tee Time" option was checked for this tee time.
                    if (make_private == 2) {
                        
                        if (!user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3)
                                    && !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5) && !orig_by.equalsIgnoreCase(user)) {
                            continue;
                        }
                    }
                    
                    /*
                    if (club.equals("rioverdecc") && tee_time == 624) {
                        continue;
                    }
                     */


                    //
                    //  If Mobile user - check if user requested we limit the tee times displayed
                    //
                    if (mobile > 0 && !displayOpt.equals("All")) {

                        if (displayOpt.equals("Morning")) {          // only show AM times?

                            if (tee_time > 1159) {

                                blocker = "AAPL";    //  Do not show this tee time
                            }

                        } else if (displayOpt.equals("Mid-Day")) {       // only show mid-day times (10:00 - 2:00)?

                            if (tee_time < 1000 || tee_time > 1400) {

                                blocker = "AAPL";    //  Do not show this tee time
                            }

                        } else if (displayOpt.equals("Afternoon")) {       // only show PM times?

                            if (tee_time < 1200) {

                                blocker = "AAPL";    //  Do not show this tee time
                            }

                        } else if (displayOpt.equals("Available")) {       // only show available times?

                            if (!player1.equals("")) {

                                blocker = "AAPL";    //  Do not show this tee time
                            }
                        }
                    }



                    //
                    //  If course=ALL requested, then set 'fives' option according to this course
                    //
                    if (courseName1.equals("-ALL-")) {
                        i = 0;
                        cMax = course.size();     // number of courses
                        loopall:
                        while (i < cMax) {
                            if (courseNameT.equals(course.get(i))) {
                                fives = fivesA.get(i);           // get the 5-some option for this course
                                break loopall;                   // exit loop
                            }
                            i++;
                        }
                    }

                    //
                    //  Check for guests in this tee time
                    //
                    g1 = 0;          // init guest indicators
                    g2 = 0;
                    g3 = 0;
                    g4 = 0;
                    g5 = 0;

                    //
                    //  Check if any player names are guest names
                    //
                    if (!player1.equals("")) {

                        i = 0;
                        ploop1:
                        while (i < parm.MAX_Guests) {
                            if (player1.startsWith(parm.guest[i])) {

                                g1 = 1;       // indicate player1 is a guest name
                                break ploop1;
                            }
                            i++;
                        }
                    }
                    if (!player2.equals("")) {

                        i = 0;
                        ploop2:
                        while (i < parm.MAX_Guests) {
                            if (player2.startsWith(parm.guest[i])) {

                                g2 = 1;       // indicate player2 is a guest name
                                break ploop2;
                            }
                            i++;
                        }
                    }
                    if (!player3.equals("")) {

                        i = 0;
                        ploop3:
                        while (i < parm.MAX_Guests) {
                            if (player3.startsWith(parm.guest[i])) {

                                g3 = 1;       // indicate player3 is a guest name
                                break ploop3;
                            }
                            i++;
                        }
                    }
                    if (!player4.equals("")) {

                        i = 0;
                        ploop4:
                        while (i < parm.MAX_Guests) {
                            if (player4.startsWith(parm.guest[i])) {

                                g4 = 1;       // indicate player4 is a guest name
                                break ploop4;
                            }
                            i++;
                        }
                    }
                    if (!player5.equals("")) {

                        i = 0;
                        ploop5:
                        while (i < parm.MAX_Guests) {
                            if (player5.startsWith(parm.guest[i])) {

                                g5 = 1;       // indicate player5 is a guest name
                                break ploop5;
                            }
                            i++;
                        }
                    }

                    i = 0;   // init


                    //*************************************************************
                    //  Check for 2-some only times
                    //*************************************************************
                    //
                    twoSomeOnly = false;             // init the flag

                    if (club.equals("piedmont") && tee_time < 1000
                            && (day_name.equals("Saturday") || day_name.equals("Sunday") || date == Hdate1 || date == Hdate3)) {

                        piedmontStatus = verifySlot.checkPiedmont(date, tee_time, day_name);     // check if special time

                        if (piedmontStatus == 3) {

                            twoSomeOnly = true;             // Only allow 2-somes for this tee time
                        }

                    } else {  // If we're not in the special times, reset piedmontStatus!!
                        piedmontStatus = 0;
                    }

                    // If Indian Hills CC and the 'Hitting Room', always set to two some times
                    if (club.equals("indianhillscc") && courseNameT.equalsIgnoreCase("Hitting Room")) {

                        twoSomeOnly = true;
                    }

                    //
                    //  if Westchester CC, check if tee time is for 2-somes only on the South course
                    //
                    if (club.equals("westchester") && courseNameT.equalsIgnoreCase("south")) {

                        twoSomeOnly = verifySlot.checkWestchester(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  if The CC, check if tee time is for 2-somes only on the Main Course
                    //
                    if (club.equals("tcclub") && courseNameT.equals("Main Course")) {

                        twoSomeOnly = verifyCustom.checkTheCC(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  if New Canaan, check if tee time is for 2-somes only
                    //
                    if (club.equals("newcanaan")) {

                        twoSomeOnly = verifyCustom.checkNewCan(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  if Greenwich CC, check if tee time is for 2-somes only
                    //
                    if (club.equals("greenwich")) {

                        twoSomeOnly = verifyCustom.checkGreenwich(date, tee_time);     // check if special time
                    }

                    //
                    //  if The Congressional, check if tee time is for 2-somes only on the Club Course
                    //
                    if (club.equals("congressional") && courseNameT.equals("Gold Course") && day_name.equals("Tuesday")) {

                        twoSomeOnly = congressionalCustom.check2Somes(date, tee_time);     // check if special time
                    }

                    //
                    //  if Woodway CC, check if tee time is for 2-somes only
                    //
                    if (club.equals("woodway")) {

                        twoSomeOnly = verifySlot.checkWoodway(date, tee_time, fb);     // check if special time
                    }
                    
                    //  if Huntingdon Valley CC, check if tee time is for 2-somes only
                    if (club.equals("huntingdonvalleycc")) {
                        
                        twoSomeOnly = verifySlot.checkHuntingdonValley(date, tee_time, courseNameT, fb);     // check if special time
                    }

                    //
                    //  if Hudson National, check if tee time is for 2-somes only
                    //
                    if (club.equals("hudsonnatl")) {

                        twoSomeOnly = verifySlot.checkHudson(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  if The Stanwich Club, check if tee time is for 2-somes only
                    //
                    if (club.equals("stanwichclub")) {

                        twoSomeOnly = verifySlot.checkStanwich(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  New Canaan, check if tee time is for 2-somes only
                    //
                    if (club.equals("newcanaan")) {

                        twoSomeOnly = verifySlot.checkNewCanaan(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  Apawamis, check if tee time is for 2-somes only
                    //
                    if (club.equals("apawamis")) {

                        twoSomeOnly = verifySlot.checkApawamis(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  Wee Burn, check if tee time is for 2-somes only
                    //
                    if (club.equals("weeburn")) {

                        twoSomeOnly = verifySlot.checkWeeburn(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  Claremont, check if tee time is for 2-somes only  (Case #1281)
                    //
                    if (club.equals("claremontcc")) {

                        if (day_name.equals("Sunday") && tee_time >= 1030 && tee_time <= 1115) {
                            twoSomeOnly = true;
                        }
                    }

                    //
                    //  Misquamicut, check if tee time is for 2-somes only (case #1996)
                    //
                    if (club.equals("misquamicut")) {

                        twoSomeOnly = verifyCustom.checkMisquamicut2someTimes(date, tee_time, day_name);
                    }

                    //
                    //  Mayfield Sand Ridge - check to see if tee time is for 2-somes only
                    //
                    if (club.equals("mayfieldsr") && courseNameT.equals("Sand Ridge")) {

                        twoSomeOnly = verifyCustom.checkMayfieldSR(date, tee_time, day_name);       // check if special time
                    }

                    //
                    //  Longu Vue Club - check to see if tee time is for 2-somes only
                    //
                    if (club.equals("longuevueclub")) {

                        twoSomeOnly = verifyCustom.check2LongueVue(date, tee_time, day_name);       // check if special time
                    }

                    //
                    //  Desert Forest - check to see if tee time is for 2-somes only
                    //
                    if (club.equals("desertforestgolfclub")) {

                        twoSomeOnly = verifyCustom.checkDesertForest(date, tee_time, fb, day_name);       // check if special time
                    }

                    //
                    //  Cherry Valley CC - check to see if tee time is for 2-somes only
                    //
                    if (club.equals("cherryvalleycc")) {

                        twoSomeOnly = verifyCustom.checkCherryValley2someTimes(date, tee_time, day_name);
                    }
                    
                    if (club.equals("echolakecc")) {
                        
                        twoSomeOnly = verifyCustom.checkEchoLake2someTimes(date, tee_time, day_name);
                    }


                    //***********************************************
                    //  3-some processing - customs
                    //***********************************************
                    //
                    threeSomeOnly = false;             // init the flag


                    if (club.equals("chartwellgcc") && day_name.equals("Thursday") && date > 20090331 && date < 20091102
                            && tee_time > 759 && tee_time < 1001) {

                        threeSomeOnly = true;
                    }

                    //
                    //  Meadow Club  (case 1761) - removed until further notice (1/12/2011)
                    //
              /*
                    if (club.equals( "meadowclub" )) {
                    
                    threeSomeOnly = verifyCustom.checkMeadowClub(date, tee_time, day_name);     // check if 3-some time
                    }
                     */


                    //
                    //  Ramsey  (case 1816)
                    //
                    if (club.equals("ramseycountryclub")) {

                        threeSomeOnly = verifyCustom.checkRamsey3someTime(date, tee_time, day_name);     // check if 3-some time

                        if (threeSomeOnly == true) {

                            skipconstimes = true;      // set flag to skip consecutive time option
                        }
                    }

                    // Sonnenalp (case 1978) - check for 3-some times
                    if (club.equals("sonnenalp")) {

                        threeSomeOnly = verifyCustom.checkSonnenalp3someTimes(date, tee_time, day_name);
                    }

                    // Royal Montreal GC - Check for 3-some times
                    if (club.equals("rmgc")) {

                        threeSomeOnly = !verifyCustom.checkRMGC4Ball(courseNameT, date, day_name);
                    }

                    if (club.equals("minnetonkacc")) {

                        threeSomeOnly = verifyCustom.checkMinnetonka3someTimes(date, tee_time, day_name, fb);

                        if (threeSomeOnly == true) {

                            skipconstimes = true;      // set flag to skip consecutive time option
                        }
                    }

                    // Riviera CC - On Tuesdays between 6/25/13 and 9/24/13 (except some specific dates), tee times between 8:02am and 10:58am need to be 3-some only.
                    if (club.equals("rivieracc")) {
                        
                        if (date >= 20130625 && date <= 20130924 && date != 20130709 && date != 20130820 && (day_name.equals("Tuesday") || (day_name.equals("Thursday") && (date == 20130711 || date == 20130822))) 
                                && tee_time >= 802 && tee_time <= 1058) {
                            threeSomeOnly = true;
                        }
                    }


                    /*
                    //
                    //  Rivercrest Golf Club & Preserve (case 1492)
                    //
                    if (club.equals( "rivercrestgc" )) {
                    
                    threeSomeOnly = verifyCustom.checkRivercrest(date, tee_time, day_name);     // check if 3-some time
                    }
                     */



                    //
                    //  Aliases - assign an alias to specified members (Congressional)
                    //
                    if (club.equals("congressional")) {

                        if (user1.equals("25191") || user1.equals("26084")) {   // if Robert Bedingfield or Chris Kubasik

                            player1 = "Member";
                        }
                        if (user2.equals("25191") || user2.equals("26084")) {

                            player2 = "Member";
                        }
                        if (user3.equals("25191") || user3.equals("26084")) {

                            player3 = "Member";
                        }
                        if (user4.equals("25191") || user4.equals("26084")) {

                            player4 = "Member";
                        }
                        if (user5.equals("25191") || user5.equals("26084")) {

                            player5 = "Member";
                        }
                    }


                    //
                    //  Cordillera custom to only display Walk or Cart modes of trans options
                    //
                    if (club.equals("cordillera")) {

                        if (!p1cw.equals("") && !p1cw.equalsIgnoreCase("W")) {

                            p1cw = "GC";       // All but Walk = Golf Car
                        }
                        if (!p2cw.equals("") && !p2cw.equalsIgnoreCase("W")) {

                            p2cw = "GC";
                        }
                        if (!p3cw.equals("") && !p3cw.equalsIgnoreCase("W")) {

                            p3cw = "GC";
                        }
                        if (!p4cw.equals("") && !p4cw.equalsIgnoreCase("W")) {

                            p4cw = "GC";
                        }
                        if (!p5cw.equals("") && !p5cw.equalsIgnoreCase("W")) {

                            p5cw = "GC";
                        }
                    }


                    //
                    //****************************************************************************************
                    //  Hide Names Feature - if club opts to hide the member names, then hide all names
                    //                       except for any group that this user is part of.
                    //****************************************************************************************
                    //
                    hideN = 0;           // default to 'do not hide member names'
                    hideG = 0;           // default to 'do not hide guest names' (this is for customs)
                    hideSubmit = 0;      // default to 'do not hide the submit button'

                    if (club.equals("merion") && user.equalsIgnoreCase("S1")) {  // if Merion and user is Steven Smith

                        hideNames = 0;      // do not hide names
                    }

                    //
                    //  Inverness Club - do NOT hide names on w/e's and holidays
                    //
                    if (club.equals("invernessclub")) {

                        if (day_name.equals("Saturday") || day_name.equals("Sunday") || date == Hdate1 || date == Hdate2b || date == Hdate3) {

                            hideNames = 0;      // do not hide names
                        }
                    }

                    if (club.equals("merion") && !user.equalsIgnoreCase("S1")) {  // if Merion and NOT Steven Smith

                        //
                        //  Merion - hide names whenever there is a guest in the tee time (and its not this member's time)
                        //
                        if (!userg1.equals("") || !userg2.equals("") || !userg3.equals("") || !userg4.equals("") || !userg5.equals("")) {

                            hideN = 1;        // hide names in this guest group

                            if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3)
                                    || user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

                                hideN = 0;     // do not hide this group
                            }
                        }

                    } else {

                        if (hideNames > 0 || make_private == 1) {
                         
                            hideN = 1;        // hide names in this group

                            if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3)
                                    || user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5) || orig_by.equalsIgnoreCase(user)) {    // if user is in this group

                                hideN = 0;                // do not hide this group

                            } else {    // user not part of this tee time

                                if (club.equals("foresthighlands") || club.equals("deserthighlands") || club.equals("blackstone") || club.equals("alisoviejo")) {

                                    //
                                    //  Forest or Desert Highlands - do not hide names unless the tee time is full (4-somes only!)
                                    //
                                    if (player1.equals("") || player2.equals("") || player3.equals("") || player4.equals("")) {

                                        hideN = 0;                // do not hide this group
                                    }

                                } else if (club.equals("oaklandhills") && dateShort > 420 && dateShort < 1015 && day_name.equals("Thursday") 
                                        && !lottery_color.equals("") && tee_time > 730 && tee_time < 1000) {
                                    
                                    hideN = 0;                // do not hide this group (must be Ladies 18-Holers)

                                } else {

                                    //
                                    //  Check if any players exist in this tee time.  If so, then do not allow this member access
                                    //
                                    //    if (!user1.equals("") || !user2.equals("") || !user3.equals("") ||
                                    //        !user4.equals("") || !user5.equals("")) {

                                    if (!player1.equals("") || !player2.equals("") || !player3.equals("")
                                            || !player4.equals("") || !player5.equals("")) {

                                        hideSubmit = 1;                // do not allow access to tee time
                                    }
                                }
                            }
                        }
                    }


                    //
                    //  Mesa Verde Custom - hide guest names, but not member names
                    //
                    if (club.equals("mesaverdecc")) {

                        hideG = 1;        // hide guest names
                    }

                    if (club.equals("indianridgecc")) {

                        if (player1.startsWith("Comp") || player2.startsWith("Comp") || player3.startsWith("Comp") || player4.startsWith("Comp") || player5.startsWith("Comp")) {
                            hideG = 1;
                        } else {
                            hideG = 0;
                        }
                    }
                    
                    if (club.equals("olympiafieldscc")) {
                        
                        if (!user1.equals(user) && !user2.equals(user) && !user3.equals(user) && !user4.equals(user) && !user5.equals(user) && !orig_by.equals(user)) {
                            hideG = 1;
                        } else {
                            hideG = 0;
                        }
                    }

                    //  Central Washington Chapter PGA Custom - populate ghin numbers
                    if (club.equals("cwcpga")) {

                        if (!user1.equals("")) {
                            ghin1 = Utilities.getHdcpNum(user1, con);
                        }
                        if (!user2.equals("")) {
                            ghin2 = Utilities.getHdcpNum(user2, con);
                        }
                        if (!user3.equals("")) {
                            ghin3 = Utilities.getHdcpNum(user3, con);
                        }
                        if (!user4.equals("")) {
                            ghin4 = Utilities.getHdcpNum(user4, con);
                        }
                        if (!user5.equals("")) {
                            ghin5 = Utilities.getHdcpNum(user5, con);
                        }
                    }

                    //
                    //  if not event, then check for lottery time (events override lotteries)
                    //
                    //  determine if we should skip this slot - display only one slot per lottery before its processed
                    //
                    lskip = 0;                      // init skip switch
                    ldays = 0;                      // init lottery days value
                    firstLott = 0;                  // init first lottery flag

                    if (blocker.equals("")) {     // check for lottery if tee time not blocked

                        if (event.equals("") && !lottery.equals("")) {
                            
                            if ((club.equals("oaklandhills") && !mship.equals("18-Hole Ladies")) || (club.equals("elgincc") && mtype.equals("Certified Junior"))) {   // ONLY 18-Hole Ladies can use the Lottery
                
                                lskip = 1;       // always skip the lottery times
                                
                            } else {

                                lottloop2:
                                for (i = 0; i < count_lott; i++) {     // check for matching lottery

                                    if (lottery.equals(lottA[i])) {    // if match found

                                        ldays = sdaysA[i];                // save lottery's advance days

                                        if (lstateA[i] < 5) {             // if lottery has not been processed (times allotted)

                                            if (lskipA[i] != 0) {          // if we were here already

                                                lskip = 1;                 // skip this slot
                                            }

                                            if (player1.equals("")) {       // if tee time is empty
                                               
                                                lskipA[i] = 1;              // make sure its set now
                                                firstLott = 1;              // indicate 1st lottery time
                                                
                                            } else {
                                               
                                               lskip = 1;                   // just skip this one, check again on the next one
                                            }   

                                        } else {

                                            lottery_color = "";         // already processed, do not use color
                                        }
                                        break lottloop2;
                                    }
                                }
                                i = 0;
                            }         // end of IF Oakland Hills
                        }          // end of IF lottery
                    }          // end of IF blocker

                    // get the course index for the current course (course for this tee time row)
              /*
                    loopci:
                    while (ci < cMax) {
                    if (courseNameT.equals( course.get [ci] )) {
                    break loopci;
                    }
                    ci++;
                    }
                     */

                    ci = course.indexOf(courseNameT);     // get the index value of this course


                    int tmp_currTime = SystemUtils.getTime(con);

                    if (index == 0 && tmp_currTime > tee_time) {

                        // it's today and this tee time has past so no need check
                        wait_list_id = 0;

                    } else {

                        // see if this tee time is covered by an active wait list
                        wait_list_id = SystemUtils.isWaitListTime(date, tee_time, tmp_currTime, courseNameT, day_name, con);
                        //out.println("<!-- wait_list_id=" + wait_list_id + ", date=" + date + ", tee_time=" + tee_time + ", tmp_currTime=" + tmp_currTime + ", courseNameT=" + courseNameT + ", day_name=" + day_name + " -->");

                    }

                    // translate to boolean
                    waitlist = (wait_list_id != 0);

                    /*
                    if (waitlist) {
                    
                    if (wait_list_id == last_wait_list_id) {
                    
                    firstWait = 0;
                    
                    } else {
                    
                    firstWait = 1;
                    last_wait_list_id = wait_list_id;
                    
                    }
                    
                    } else {
                    
                    if (ci == last_ci) {
                    
                    last_wait_list_id = 0;
                    
                    }
                    
                    }
                     */

                    if (waitlist) {

                        WL_index = 0; // reset
                        loopWL:
                        while (WL_index < parmWaitLists.count) {

                            //if (WLbtn_index[WL_index] == wait_list_id) break loopWL;
                            if (parmWaitLists.id[WL_index] == wait_list_id) {
                                break loopWL;
                            }
                            WL_index++;
                        }
                    }


                    // Custom for baltusrolgc - hide ANY non event tee time on ANY day that has ANY number of players in it
                    //boolean hide_baltusrol = (club.equals("baltusrolgc") && !player1.equals("")) ? true : false;
                    if (club.equals("baltusrolgc") && !player1.equals("") && blocker.equals("") && event.equals("")
                            && !user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3)
                            && !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5)) {

                        blocker = "AAPL";    //  Do not show this tee time if taken and this user is not part of it
                    }


                    //
                    //   Medinah Custom - hide the tee time if between 7:30 & 6:00 and user is not part of it (ALL courses - per Mike Scully - original)
                    //   Medinah Custom - hide the tee time if between 2:00 PM & 6:00 PM and user is not part of it (ALL courses - per Mike Scully 5/19/09)
                    //   Medinah Custom - DO NOT hide the tee times at all (ALL courses - per Mike Scully 6/02/09)
                    //
              /*
                    if ( club.equals( "medinahcc" ) && tee_time > 1359 && tee_time < 1801 ) {
                    
                    if (!player1.equals("") && blocker.equals("") && event.equals("") &&
                    !user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3) &&
                    !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5)) {
                    
                    blocker = "AAPL";    //  Do not show this tee time if taken and this user is not part of it
                    }
                    }
                     */


                    //
                    //   TPC Custom - hide the tee time if at least 4 players and user is not part of it (ALL TPC Clubs)
                    //
                    if (club.startsWith("tpc") && !club.equals("tpcriversbend") && !club.equals("tpcwakefieldplantation")
                            && !orig_by.equalsIgnoreCase(user)) {    // do not block if originated by this user!!

                        if (!player4.equals("") && blocker.equals("") && event.equals("")
                                && !user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3)
                                && !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5)) {

                            if (!club.equals("tpctc")) {          // if NOT TPC TC

                                blocker = "AAPL";    //  Do not show this tee time if taken and this user is not part of it

                            } else {       // TPC TC - only block if guest included

                                if (g1 == 1 || g2 == 1 || g3 == 1 || g4 == 1 || g5 == 1) {   // if any guests

                                    blocker = "AAPL";    //  Do not show this tee time if taken and one or more guests included
                                }
                            }
                        }
                    }


                    //
                    //   Hiwan - hide the tee time if at least 4 players and user is not part of it
                    //
              /*
                    if ( club.equals( "hiwan" ) && !orig_by.equalsIgnoreCase( user ) ) {    // do not block if originated by this user!!
                    
                    if (!player4.equals("") && blocker.equals("") && event.equals("") &&
                    !user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3) &&
                    !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5)) {
                    
                    blocker = "AAPL";    //  Do not show this tee time if taken and this user is not part of it
                    }
                    }
                     */


                    //
                    //  Custom for Stonebridge Ranch - Hills mship types - do not show any times for Dye course
                    //
                    if (club.equals("stonebridgeranchcc") && mship.equals("Hills") && courseNameT.equals("Dye")) {

                        blocker = "AAPL";       //  Do not show this tee time
                    }


                    //
                    //  Custom check for Olympic Club's starter times
                    //
                    if (club.equals("olyclub")) {

                        if (courseNameT.equals("Cliffs")) {

                            blocker = "AAPL";       //  Do not show this tee time (no member access on this course)

                        } else {

                            boolean olyRest = false;

                            olyRest = verifyCustom.checkOlyStarterTime(dateShort, date, tee_time, day_name, courseNameT, "member");   // go check if Starter time

                            if (olyRest == true) {                     // if Starter Time

                                blocker = "AAPL";       //  Do not show this tee time
                            }
                        }
                    }       // end of Olympic Club custom



                    //
                    //  Custom check for Cordillera Canned Restrictions (see below for Starter Times check)
                    //
              /*
                    if (club.equals( "cordillera" ) && dateShort > 413 && dateShort < 1101) {  // if within the custom date range
                    
                    cordallow = true;           // default to allow
                    
                    if (index > 14) {          // if more than 14 days in advance - check for dedicated Lodge Times
                    
                    cordallow = cordilleraCustom.checkCordillera(date, tee_time, courseNameT, "member"); // go check if this time is restricted
                    
                    if (cordallow == false) {                  // if restricted
                    
                    lskip = 1;                              // skip this time (do not show to members
                    }
                    }
                    }
                     */


                    // debug
                    //out.println("<!-- tee_time="+tee_time+", wait_list_id="+wait_list_id+", last_wait_list_id="+last_wait_list_id+", firstWait="+firstWait+", courseNameT="+courseNameT+", lastCourse="+lastCourse+", blocker="+blocker+", event="+event+", WL_index="+WL_index+", WLbtn_index["+WL_index+"]="+WLbtn_index[WL_index]+", WLbtn_displayed["+WL_index+"]="+WLbtn_displayed[WL_index]+" -->");

                    //if ( blocker.equals( "" ) && lskip == 0 && (!waitlist || (waitlist && firstWait == 1) || (waitlist && firstWait == 0 && firstWait_displayed == false)) ) {    // continue if tee time not blocked & not lottery & not wait list - else skip


                    //
                    // TSP - Add a check here is we want to supress the entire tee time if player1 starts with tsp_guest_type
                    //

                    // continue if tee time not blocked & not lottery & either not wait list or is wait list that hasn't been displayed yet,
                    // or is wait list that is set to always show tee times - else skip
                    if (blocker.equals("") && lskip == 0
                            && (!waitlist || (waitlist && !WLbtn_displayed[WL_index]) || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0))) {

                        // NOTE:  else - skip everything and go to next tee time!

                        ampm = " AM";
                        if (hr == 12) {
                            ampm = " PM";
                        }
                        if (hr > 12) {
                            ampm = " PM";
                            hr = hr - 12;
                        }

                        bgcolor = "#F5F5DC"; // default

                        if (!event.equals("")) {
                            bgcolor = ecolor;

                        } else {

                            // an event wasn't specified, lets check for lotterys
                            if (!lottery.equals("") && !lottery_color.equals("")) {
                                bgcolor = lottery_color;
                            } else {
                                if (!rest.equals("")) {
                                    bgcolor = rcolor;
                                }
                            }
                        }

                        if (bgcolor.equals("Default")) {
                            bgcolor = "#F5F5DC";              // default
                        }

                        if (bgcolor5.equals("")) {
                            bgcolor5 = bgcolor;              // same as others if not specified
                        }


                        //
                        //  Interlachen - always show CA when mode of trans is any caddie
                        //
                        if (club.equals("interlachen")) {

                            if (p1cw.startsWith("CA")) {       // if any caddie
                                p1cw = "CA";                    //  make generic caddie
                            }
                            if (p2cw.startsWith("CA")) {
                                p2cw = "CA";
                            }
                            if (p3cw.startsWith("CA")) {
                                p3cw = "CA";
                            }
                            if (p4cw.startsWith("CA")) {
                                p4cw = "CA";
                            }
                            if (p5cw.startsWith("CA")) {
                                p5cw = "CA";
                            }
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

                        //
                        //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                        //
                        sfb = "F";       // default Front 9
                        sfb2 = "Front";       // default Front 9

                        if (fb == 1) {

                            sfb = "B";
                            sfb2 = "Back";
                        }

                        if (fb == 9) {

                            sfb = "O";
                            sfb2 = "O";
                        }

                        if (type == shotgun) {

                            //sfb = "S";            // there's an event and its type is 'shotgun'
                            sfb = (!hole.equals("")) ? hole : "";            // there's an event and its type is 'shotgun'
                        }


                        //
                        // check if we should allow user to select this slot
                        //
                        // check max allowed days in advance (normal time and lottery time), special event & cross-over time
                        //
                        allow = true;

                        //
                        //  Check if members are not allowed to access tee times today
                        //
                        if (restrictAll == true) {

                            allow = false;            // not today!
                        }

                        //
                        //  Save the Days in Advance value
                        //
                        int tempDays = days;        // days in adv for test

                        //
                        //  Philly Cricket - do not allow members to access the 9-hole course St Martins
                        //
                 /*
                        if (club.equals("philcricket") && courseNameT.equals("St Martins")) {
                        
                        allow = false;
                        }
                         */

                        //
                        //  If Milwaukee CC, check for Special Guest Time
                        //
                        boolean mcctime = false;

                        if (club.equals("milwaukee") && mccGuestDay == true) {

                            if (fb == 1 && tee_time > 1200 && tee_time < 1431) {

                                mcctime = true;      // special guest time - make it a green button
                            }
                        }

                        //
                        //  If Santa Ana CC or Columbia-Edgewater, check for Special Women's Time
                        //
                        boolean satime = false;
                        /*  (removed 11/13/06)
                        if (club.equals( "santaana" ) && mtype.equals( "Adult Female" )) {
                        
                        if (day_name.equals("Tuesday") && index2 > 2 && index2 < 5 &&
                        ((tee_time > 744 && tee_time < 946 && fb == 0) ||
                        (tee_time > 744 && tee_time < 831 && fb == 1))) {
                        
                        satime = true;      // special time - allow it
                        }
                        
                        if (day_name.equals("Friday") && index2 > 5 && index2 < 8 &&
                        ((tee_time > 736 && tee_time < 1001 && fb == 0) ||
                        (tee_time > 751 && tee_time < 816 && fb == 1))) {
                        
                        satime = true;      // special time - allow it
                        }
                        }
                         */
                        if (club.equals("cecc") && mtype.startsWith("Spouse")) {

                            if ((day_name.equals("Tuesday") || day_name.equals("Friday")) && index2 > 2 && index2 < 8
                                    && tee_time > 759 && tee_time < 1025) {

                                satime = true;      // special time - allow it

                                if (index2 == 7 && cal_time < 800) {        // if 7 days in adv and not 8 AM yet - do not allow

                                    satime = false;
                                }
                            }
                        }


                        //
                        //  Mission Viejo CC - If time is already occupied by a 4-some, restrict members that are not part of the reservation from accessing the tee time
                        //
                        if (club.equals("missionviejo")) {

                            if (!player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("")
                                    && !user1.equals(user) && !user2.equals(user) && !user3.equals(user) && !user4.equals(user)) {

                                allow = false;
                            }
                        }


                        //
                        //  Pinehurst CC - if Proprietary member only allow access the day of, starting at 6:00 AM MT.
                        //               - allow Women to access tee times on Tues & Thurs mornings (Ladies Days) 4/1-9/30
                        //
                        if (allow == true && club.equals("pinehurstcountryclub") && courseNameT.equals("Pfluger 9") && firstLott == 0) {

                            if ((day_name.equals("Tuesday") || day_name.equals("Thursday")) && tee_time < 1100
                                    && dateShort >= 401 && dateShort <= 930 && mtype.endsWith("Female")) {  // let women access their special times
                                allow = true;
                            } else if (index > 1 || (index == 1 && cal_time < 600)) {
                                allow = false;         // indicate no member access
                            }
                        }
                        
                        if (club.equals("lakesidecc")) {
                            
                            // If tee time not empty and current user isn't a part of the tee time, restrict access to that tee time.
                            if ((!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals(""))
                                    && !user1.equalsIgnoreCase(user) && !user2.equalsIgnoreCase(user) && !user3.equalsIgnoreCase(user) && !user4.equalsIgnoreCase(user) && !user5.equalsIgnoreCase(user)
                                    && !orig_by.equalsIgnoreCase(user)) {
                                
                                allow = false;
                            }
                        }

                        //
                        //  Capital City Club - if displaying all courses, viewing Fri/Sat/Sun tee sheet, do not allow early access to Brookhaven course times.
                        //
                 /*
                        if (club.equals("capitalcityclub") && courseName1.equals("-ALL-")) {
                        
                        if (courseNameT.equals("Brookhaven") && (day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday")) &&
                        index2 == 3 && cal_time < 830) {
                        allow = false;
                        }
                        }
                         */


                        //
                        //  If Los Coyotes check for Primary-only times
                        //
                        if (club.equals("loscoyotes") && allow == true) {

                            allow = verifyCustom.checkLosCoyotesTimes(date, tee_time, day_name, courseNameT);    // check for Primary-Only time

                            if (allow == true) {         // if Primary Only time

                                if (index2 > 3) {         // if more than 3 days in adv, then do not allow regardless of user

                                    allow = false;

                                } else if (!mtype.startsWith("Primary")) {

                                    allow = false;     // NOT a primary - do not allow access to this time
                                }

                            } else {

                                allow = true;       // NOT a primary time - reset to ok
                            }
                        }


                        //
                        //  If Bearpath CC check for member-only times
                        //
                        boolean beartime = false;

                        if (club.equals("bearpath")) {

                            beartime = verifySlot.checkBearpathGuests(day_name, date, tee_time, index2);
                        }

                        //
                        //  If Congressional CC & a Guest Day, check for guest times
                        //
                        boolean congresstime = false;

                        if (club.equals("congressional") && congGuestDay == true && !day_name.equals("Saturday") && !day_name.equals("Sunday") && !day_name.equals("Monday")) {

                            if (courseNameT.equals("Blue Course")) {

                                if (tee_time > 920 && tee_time < 1525 && min == 24) {       // if xx:24
                                    
                                    congresstime = true;                 // make it a green button
                                    
                                    if (tee_time == 1324 && day_name.equals("Friday") && (dateShort == 302 || dateShort == 316 || dateShort == 330 || 
                                        dateShort == 413 || dateShort == 427 || dateShort == 511 || dateShort == 525 || dateShort == 608 || dateShort == 622 || 
                                        dateShort == 706 || dateShort == 720 || dateShort == 803 || dateShort == 817 || dateShort == 831 || dateShort == 914 || 
                                        dateShort == 928 || dateShort == 1012 || dateShort == 1026)) {
                                        
                                        congresstime = false;            // skip this time every other Friday (change each year!!!)
                                    }
                                }

                            } else if (courseNameT.equals("Gold Course")) {

                                if (day_name.equals("Wednesday") && tee_time > 800 && tee_time < 940 && dateShort > 430 && dateShort < 1101) {
                                    
                                    congresstime = false;                 // do NOT make it a green button (Ladies Day)

                                } else if (min == 20 && tee_time < 1531) {       // if xx:20 

                                    congresstime = true;                 // make it a green button
                                    
                                    if (tee_time == 1320 && day_name.equals("Friday") && (dateShort == 309 || dateShort == 323 || dateShort == 406 || 
                                        dateShort == 420 || dateShort == 504 || dateShort == 518 || dateShort == 601 || dateShort == 615 || dateShort == 629 || 
                                        dateShort == 713 || dateShort == 727 || dateShort == 810 || dateShort == 824 || dateShort == 907 || dateShort == 921 || 
                                        dateShort == 1005 || dateShort == 1019)) {
                                        
                                        congresstime = false;            // skip this time every other Friday (change each year!!!)
                                    }
                                }
                            }
                            
                        }


                        //
                        //  If Canterbury GC & a Guest Day, check for guest times
                        //
                        if (club.equals("canterburygc") && congGuestDay == true) {    // if Canterbury and beyond normal days in advance for members

                            congresstime = false;

                            //
                            //  check for weekend or Memorial Day or Labor Day (mornings not allowed)
                            //
                            if ((day_name.equals("Saturday") && tee_time < 1030) || (day_name.equals("Sunday") && tee_time < 1000)
                                    || (date == Hdate1 && tee_time < 1000) || (date == Hdate3 && tee_time < 1000)) {

                                allow = false;                       // do not allow this time

                            } else {

                                congresstime = true;                 // make it a green button and allow
                            }
                        }


                        //
                        //  If Oakmont CC check for selected times on Wed and Fri that are available
                        //
                        boolean oaktime = false;

                        if (club.equals("oakmont")) {

                            //
                            //  If Wed or Fri, and not a shotgun today, check if this time is one when multiple guests are allowed.
                            //  If so, make the submit button green and allow this tee time.
                            //
                            if (event.equals("") && oakshotgun == false && (day_name.equals("Wednesday") || day_name.equals("Friday"))) {

                                if (index2 > Max14) {                // Wed or Fri and more than 14 days in advance

                                    allow = false;                     // do not allow this time unless ok below
                                }

                                //
                                //  Check the time of this tee time against those allowed for this day
                                //
                                if (day_name.equals("Friday")) {       // if Friday

                                    oakloop1:
                                    for (int oak = 0; oak < fricount; oak++) {

                                        if (tee_time == fritimes[oak]) {

                                            oaktime = true;       // tee time is ok
                                            allow = true;
                                            break oakloop1;
                                        }
                                    }

                                } else {    // Wednesday

                                    oakloop3:
                                    for (int oak = 0; oak < wedcount; oak++) {      // check normal Wed times

                                        if (tee_time == wedtimes[oak]) {

                                            oaktime = true;       // tee time is ok
                                            allow = true;
                                            break oakloop3;
                                        }
                                    }
                                }
                            }
                        }         // end of IF Oakmont


                        /*
                        if (club.equals("congressional")) {

                            //
                            //  If course is Club Course Gold today, then do not allow any access - walk-up times only (Tues - Fri)
                            //
                            String congCourse = congressionalCustom.getFullCourseName(date, day, courseNameT);

                            if (congCourse.equals("Club Course Gold") && (day_name.equals("Tuesday") || day_name.equals("Wednesday")
                                    || day_name.equals("Thursday") || day_name.equals("Friday"))) {

                                //
                                //  If twosome time and Tues, then allow access - others NO ACCESS on this course
                                //
                                if (twoSomeOnly == false || !day_name.equals("Tuesday")) {

                                    allow = false;            // not today!
                                }
                            }
                        }
                         */


                        if (club.equals("oceanreef") && courseNameT.equals("Dolphin") && !restrictAll) {

                            //
                            //  Ocean Reef & Dolphin Course - days in advance based on Mship Type (normally 30 days, but can be less)
                            //
                            allow = verifyCustom.checkOceanReefMship(dateShort, index2, tee_time, cal_time, mship);
                        }


                        if (club.equals("ccnaples") && (mship.startsWith("Associate B") || mship.startsWith("Associate  B"))) {

                            //  CC of Naples - check 'Associate B ..." mship types (Nov 1 - Apr 30)

                            if ((dateShort > 1031 || dateShort < 431) && tee_time < 1230) {  // if in season and before 12:30

                                allow = false;       // not allowed
                            }
                        }

                        if (club.equals("bellevuecc")) {

                            // Bellevue CC - Between 5/4 and 8/31, do not allow access to 3:50pm-6:10pm times on Tuesday until 9pm, 7 days in advance (normal is 8am, 7 days in advance)
                            if (dateShort >= 504 && dateShort <= 831 && day_name.equals("Tuesday") && index == 7
                                    && tee_time >= 1550 && tee_time <= 1810 && cal_time < 2100) {

                                allow = false;
                            }
                        }


                        if (allow == true) {     // if still ok, check more

                            //
                            // if restriction for this slot and its not the first time for a lottery, check restriction for this member
                            //
                            if (!rest.equals("") && firstLott == 0) {

                                ind = 0;
                                while (ind < parmr.MAX && allow == true && !parmr.restName[ind].equals("")) {     // check all possible restrictions

                                    if (parmr.applies[ind] == 1 && parmr.stime[ind] <= tee_time && parmr.etime[ind] >= tee_time) {      // matching time ?

                                        // Check to make sure no suspensions apply
                                        suspend = false;
                                        for (int k = 0; k < parmr.MAX; k++) {

                                            if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {
                                                k = parmr.MAX;   // don't bother checking any more
                                            } else if (parmr.susp[ind][k][0] <= tee_time && parmr.susp[ind][k][1] >= tee_time) {    //tee_time falls within a suspension                                   // init fields

                                                suspend = true;
                                                k = parmr.MAX;     // don't bother checking any more
                                            }
                                        }

                                        if (!suspend) {

                                            if ((parmr.courseName[ind].equals("-ALL-")) || (parmr.courseName[ind].equals(courseNameT))) {  // course ?

                                                if ((parmr.fb[ind].equals("Both")) || (parmr.fb[ind].equals(sfb2))) {    // matching f/b ?

                                                    //
                                                    //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                    //
                                                    if (event.equals("")) {           // change color if no event

                                                        if (!parmr.color[ind].equals("Default")) {     // if not default

                                                            bgcolor = parmr.color[ind];

                                                            if (bgcolor5.equals("")) {
                                                                bgcolor5 = bgcolor;         // same as others if not specified
                                                            }
                                                        }
                                                    }
                                                    allow = false;                    // match found
                                                }
                                            }
                                        }
                                    }
                                    ind++;
                                }               // end of while (loop2)

                                boolean defaultFound = false;
                                boolean colorChanged = false;
                                
                                if (allow && !rcolor.equals("")) {     // No applicable restrictions found, check for suspensions for restrictions that don't apply.

                                    ind = 0;
                                    while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {

                                        if (parmr.restName[ind].equals(rest)) {
                                            
                                            defaultFound = true;

                                            // Check to make sure no suspensions apply
                                            suspend = false;
                                            for (int k = 0; k < parmr.MAX; k++) {

                                                if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {
                                                    k = parmr.MAX;   // don't bother checking any more
                                                } else if (parmr.susp[ind][k][0] <= tee_time && parmr.susp[ind][k][1] >= tee_time) {    //tee_time falls within a suspension
                                                    suspend = true;
                                                    k = parmr.MAX;     // don't bother checking any more
                                                }
                                            }      // end of for loop

                                            if (suspend) {

                                                if ((parmr.courseName[ind].equals("-ALL-")) || (parmr.courseName[ind].equals(courseNameT))) {  // course ?

                                                    if ((parmr.fb[ind].equals("Both")) || (parmr.fb[ind].equals(sfb2))) {    // matching f/b ?

                                                        //
                                                        //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                        //
                                                        if (event.equals("")) {           // change color back to default if no event

                                                            // Search for the first non-suspended color to apply, or default if non found
                                                            bgcolor = "#F5F5DC";   // default color
                                                            colorChanged = true;

                                                            int ind2 = 0;
                                                            while (ind2 < parmr.MAX && !parmr.restName[ind2].equals("")) {

                                                                // make sure it's not the default restriction/color, and has a non-blank, non-default color
                                                                // and applies to this tee_time
                                                                if (!parmr.restName[ind2].equals(rest) && !parmr.color[ind2].equals("") && !parmr.color[ind2].equalsIgnoreCase("Default")
                                                                        && parmr.stime[ind2] <= tee_time && parmr.etime[ind2] >= tee_time) {

                                                                    // Check to make sure no suspensions apply
                                                                    suspend = false;
                                                                    for (int k = 0; k < parmr.MAX; k++) {

                                                                        if (parmr.susp[ind2][k][0] == 0 && parmr.susp[ind2][k][1] == 0) {
                                                                            k = parmr.MAX;   // don't bother checking any more
                                                                        } else if (parmr.susp[ind2][k][0] <= tee_time && parmr.susp[ind2][k][1] >= tee_time) {    //tee_time falls within a suspension
                                                                            suspend = true;
                                                                            k = parmr.MAX;     // don't bother checking any more
                                                                        }
                                                                    }

                                                                    if (!suspend) {

                                                                        if ((parmr.courseName[ind2].equals("-ALL-")) || (parmr.courseName[ind2].equals(courseNameT))) {  // course ?

                                                                            if ((parmr.fb[ind2].equals("Both")) || (parmr.fb[ind2].equals(sfb2))) {    // matching f/b ?

                                                                                //
                                                                                //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                                                //
                                                                                if (event.equals("")) {           // change color if no event

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

                                                            if (bgcolor5.equals("")) {
                                                                bgcolor5 = bgcolor;         // same as others if not specified
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        ind++;
                                    }      // end of while loop
                                    
                                    // If the restriction stored in the tee time isn't present (most likely lifted via custom), and the color hasn't been changed to that of another restriction, do not display the color.
                                    if (!defaultFound && !colorChanged) {
                                        
                                        if (bgcolor5.equals(bgcolor) && !colorChanged) {
                                            bgcolor5 = "#F5F5DC";
                                        }
                                        
                                        bgcolor = "#F5F5DC";   // default color
                                    }
                                }
                            }     // end of if rest exists in teecurr
                            
                        } else {
                            
                            boolean defaultFound = false;
                                
                            for (int ind2 = 0; ind2 < parmr.MAX && !parmr.restName[ind2].equals(""); ind2++) {
                                
                                if (parmr.restName[ind2].equals(rest)) {
                                    defaultFound = true;
                                    break;
                                }
                            }
                            
                            if (!defaultFound && bgcolor.equals(rcolor)) {
                                
                                if (bgcolor5.equals(bgcolor)) {
                                    bgcolor5 = "#F5F5DC";
                                }
                                
                                bgcolor = "#F5F5DC";
                            }
                        }


                        //
                        //  If Portland GC check for Member Time (walk-up time)
                        //
                        if (allow == true && club.equals("portlandgc")) {

                            walkup = verifyCustom.checkPGCwalkup(date, tee_time, day_name);   // Walk-Up time?

                            if (walkup == true) {

                                allow = false;

                                if (ecolor.equals("")) {        // if no event color

                                    bgcolor = "burlywood";       // custom color for Walk-Up times
                                }
                            }
                        }

                        if (bgcolor5.equals("")) {
                            bgcolor5 = bgcolor;
                        }

                        //
                        //  If Medinah CC check for Member Time (walk-up time) and days in adv for Course #2
                        //
                        if (allow == true && club.equals("medinahcc")) {

                            //
                            //  8:00 & 8:10 tee times on Course #3 on Tues are reserved for Spouses
                            //
                            if (courseNameT.equals("No 3") && day_name.equals("Tuesday")
                                    && (tee_time == 800 || tee_time == 810)) {

                                if (!mtype.equals("Regular Spouse") && !mtype.equals("Jr. Spouse")
                                        && !mtype.equals("Reg Prob Spouse")) {

                                    allow = false;                     // 8:00 & 8:10 reserved for spouses

                                } else {       // spouse - these times available 6 days in adv starting at 7:00 AM

                                    if (index2 > 6 || (index2 == 6 && cal_time < 700)) {

                                        allow = false;

                                    } else {

                                        tempDays = 6;       //  force days in adv to 6 days for this time only
                                    }
                                }

                            } else {

                                //
                                // Medinah, not Tues at 8:00 or 8:10 on No 3
                                //
                                boolean medinahMemTime = medinahCustom.checkMemTime(courseNameT, day_name, tee_time, date);

                                if (medinahMemTime == true) {

                                    allow = false;     // Member Time (Walk-up Time) or Unaccompanied Time - do not allow

                                } else {

                                    if (!courseNameT.equals("No 2")) {       // if Course #2
                                        //  if (courseNameT.equals( "No 2" )) {       // if Course #2

                                        // if (index2 > Max7) {                  // more than 7 days in advance?

                                        //    allow = false;                     // Max of 7 days in adv for Course #2
                                        // }

                                        // } else {

                                        //
                                        //  #1 or #3 and NOT a Member Time - do not allow spouses anytime other than those above
                                        //
                                        if (mtype.equals("Regular Spouse") || mtype.equals("Jr. Spouse")
                                                || mtype.equals("Reg Prob Spouse") || mtype.equals("NR Spouse")
                                                || mtype.equals("Soc Spouse")) {

                                            allow = false;                     // no spouses can make times
                                        }
                                    }
                                }
                            }

                            //
                            //  Allow 7 days in adv for Course #2 (all mships are config'd for 2 days)
                            //
                            // if (courseNameT.equals( "No 2" )) {

                            // tempDays = 7;         //  force medinah's Course #2 to 7 days
                            // }

                        }      // end of Medinah Custom


                        /*
                        //
                        //  If Valley Country Club and a lady change the days in adv to 14 for Fridays.   (Removed by case 1388)
                        //
                        boolean tmp_valleycc = false;
                        if (allow == true && club.equals( "valleycc" ) && day_name.equals( "Friday" ) && index2 < tempDays) { //&& index2 > 14 (tempDays is set to 14 above.  need to reset to 1 (default for Fridays) after we check mship and time
                        
                        //out.println("<!-- HERE: ldays=" +ldays+ ", index2=" +index2+ ", tempDays=" +tempDays+ ", tee_time="+tee_time+ "  -->");
                        
                        if (mtype.endsWith( "Female" ) && tee_time > 729 && tee_time < 1100) {
                        
                        //out.println("<!-- HERE-ALLOWING: tee_time="+tee_time+ "  -->");
                        tmp_valleycc = true;
                        }
                        tempDays = 1;
                        }
                         */



                        if (allow == true) {     // if still ok, check more

                            //
                            //  Check if we are past the allowed Days in Advance
                            //
                            if (!club.equals("oakmont") && !club.equals("invernessclub") && (index2 > tempDays && index2 > ldays)) {   // if beyond days in adv

                                //                     if (mcctime == false && satime == false && congresstime == false && tmp_valleycc == false) {      // allow if mcctime or SantaAna or Congressional time
                                if (mcctime == false && satime == false && congresstime == false) {      // allow if mcctime or SantaAna or Congressional time

                                    allow = false;
                                }
                            }

                            if (fb == 9) {          // cross-over ?

                                allow = false;
                            }

                            //
                            //   EVENT checks
                            //
                            if (allow == true && !event.equals("")) {    // event ?
                                
                                allow = false;          // default to no access
                                
                                if (!player1.equals("") && !player1.equalsIgnoreCase("X")) {    // if a member group
                                
                                    //
                                    //  Event time with at least 1 member - check if members can access event times once they are on the sheet (event config option)
                                    //
                                    i = 0;
                                    memedit = 0;

                                    // find this event in the legend array
                                    loopmemedit:
                                    while (i < count_event) {

                                        if (eventA[i].equals(event)) {   // if event name found

                                            memedit = memeditA[i];      // get the option value (1 = ok to edit)
                                            break loopmemedit;
                                        }
                                        i++;
                                    }                  // end of while

                                    if (memedit > 0) {        // if ok to edit event times on tee sheet

                                        allow = true;         // allow access to this tee time (if it passes the tests below)
                                    }
                                }
                            }          // end of IF event

                            

                            //
                            //  Brooklawn - check for tee times event on Sat 7/18/09 and allow Male members to access (see also SystemUtils). Remove after 7/18/09 !!!
                            //
                            if (club.equals("brooklawn")) {

                                if ((thisDate > 20130509 || thisDate == 20130509 && cal_time >= 900) && date == 20130615 && tee_time >= 839 && tee_time <= 1400
                                        && (mtype.endsWith("Male") || mtype.equalsIgnoreCase("Cert Dependent over 16") || mtype.endsWith("Dependent"))) {

                                    allow = true;      // ok
                                }

                                if ((thisDate > 20130515 || thisDate == 20130515 && cal_time >= 900) && date == 20130619 && tee_time >= 1130 && tee_time <= 1430) {

                                    allow = true;      // ok
                                }

                                if ((thisDate > 20130526 || thisDate == 20130526 && cal_time >= 700) && date == 20130713 && tee_time >= 700 && tee_time <= 1230) {

                                    allow = true;      // ok
                                }
                                
                            } else if (club.equals("sankatyheadgc")) {
                            
                                if (mtype.equalsIgnoreCase("Primary Female") || mtype.equalsIgnoreCase("Spouse Female")) {
                                    
                                    if ((index2 < 7 || (index2 == 7 && cal_time >= 700)) && (date == 20120612 || date == 20120717 || date == 20120807 || date == 20120918) && tee_time >= 800 && tee_time <= 1000) {
                                        
                                        allow = true;
                                    }
                                }
                                
                            } else if (club.equals("lagcc")) {
                                
                                if (date == 20140315 && (index2 < 1 || (index2 == 1 && cal_time >= 700))) {
                                    
                                    allow = true;
                                }
                            }
                        }
                        
                        //
                        //  If Inverness Club then members can make tee times w/ guests up to Oct 31 on weekdays
                        //
                        if (allow == true && club.equals("invernessclub")) {

                            // if more than 30 days in advance and sheet date is after 10-31-07 restrict
                            if (dateShort > 1031 && index2 > 30) {

                                allow = false;
                            }
                        }

                        //
                        //  Custom check for Cordillera Custom Starter Times (if more than 3 days in advance)
                        //
                 /*
                        if (club.equals( "cordillera" ) && dateShort > 531 && dateShort < 1001) {  // if within the custom date range
                        
                        if (index > 3 || (index == 3 && cal_time < 600)) {          // if more than 3 days in advance
                        
                        cordallow = cordilleraCustom.checkStarterTime(date, tee_time, courseNameT, "member"); // go check if this time is restricted
                        
                        if (cordallow == false) {                      // if restricted (starter time)
                        
                        bgcolor = cordRestColor;                    // set color for this slot
                        allow = false;                              // do not allow member access to this time
                        }
                        }
                        }
                         */


                        //
                        //  Custom check for Oakland Hills (for days beyond the 8 day limit)
                        //
                        if (club.equals("oaklandhills") && allow == true && index2 > 8 && courseNameT.equals("South Course")) {

                            allow = verifySlot.checkOaklandAdvTime(date, tee_time, day_name); // go check if this time is restricted
                        }

                        //
                        //  Custom check for Tamarisk CC - Cannot book times between 8am and 11:07 am on any day during season more than 3 days in advance
                        //
                        if (club.equals("tamariskcc") && allow == true && index2 > 2 && index2 < 8) {   // between 3 and 7 days in advance - go check times

                            allow = verifyCustom.checkTamariskAdvTime(tee_time, date, index2, cal_time);
                        }

                        //
                        //   More custom checks
                        //
                        if (allow == true) {     // if still ok, check more

                            //
                            //  If Cherry Hills and in normal season, check mtype and day
                            //
                            if (club.equals("cherryhills") && dateShort > 414 && dateShort < 931) {

                                //
                                //  Process according to the day of week (and holidays)
                                //
                                if (date == Hdate1 || date == Hdate2 || date == Hdate3) {   // if Mem Day, 7/04 or Labor Day

                                    if ((day_name.equals("Monday") || tee_time < 1101) && (!mtype.endsWith("ember") || mship.equals("Resident Emeritus") || mship.equalsIgnoreCase("Surviving Spouse") || mship.equalsIgnoreCase("Former Spouse"))) {

                                        allow = false;
                                    }

                                } else {

                                    if (day_name.equals("Monday") && (!mtype.endsWith("ember") || mship.equalsIgnoreCase("Surviving Spouse") || mship.equalsIgnoreCase("Former Spouse"))) {

                                        allow = false;
                                    }
                                    if (day_name.equals("Tuesday") && index2 > 1) {
                                        
                                        if (tee_time <= 1100 && mtype.endsWith("ember") && !mship.equalsIgnoreCase("Surviving Spouse") && !mship.equalsIgnoreCase("Former Spouse")) {
                                            
                                            allow = false;
                                            
                                        } else if (tee_time > 1100 && mtype.endsWith("ember") && (mship.equalsIgnoreCase("Surviving Spouse") || mship.equalsIgnoreCase("Former Spouse"))) {
                                            
                                            allow = false;
                                        }
                                    }
                                    if (day_name.equals("Thursday") && index2 > 1 && tee_time < 1000 && mtype.endsWith("ember")) {

                                        if (tee_time < 1000 && mtype.endsWith("ember") && !mship.equalsIgnoreCase("Surviving Spouse") && !mship.equalsIgnoreCase("Former Spouse")) {
                                            
                                            allow = false;
                                            
                                        } else if (tee_time >= 1000 && mtype.endsWith("ember") && (mship.equalsIgnoreCase("Surviving Spouse") || mship.equalsIgnoreCase("Former Spouse"))) {
                                            
                                            allow = false;
                                        }
                                    }
                                    if (day_name.equals("Saturday") && tee_time < 1101 && (!mtype.endsWith("ember") || mship.equals("Resident Emeritus") || mship.equalsIgnoreCase("Surviving Spouse") || mship.equalsIgnoreCase("Former Spouse"))) {

                                        allow = false;
                                    }
                                    if (day_name.equals("Sunday") && tee_time < 1001 && (!mtype.endsWith("ember") || mship.equals("Resident Emeritus") || mship.equalsIgnoreCase("Surviving Spouse") || mship.equalsIgnoreCase("Former Spouse"))) {

                                        allow = false;
                                    }
                                }
                            }


                            //
                            //  If Merion, a weekend day, more than 7 days in advance, and a non-resident - check for limited times
                            //
                            if (club.equals("merion") && (index > 7 || (index == 7 && cal_time < 600))
                                    && (day_name.equals("Saturday") || day_name.equals("Sunday")) && mship.equals("Non-Resident")) {

                                if (day_name.equals("Saturday") && tee_time < 1031) {

                                    allow = false;
                                }
                                if (day_name.equals("Sunday") && tee_time < 901) {

                                    allow = false;
                                }
                            }


                            //
                            //  If El Niguel and Adult Female
                            //
                            if (club.equals("elniguelcc") && mtype.equals("Adult Female")) {

                                //
                                //  Process according to the day of week
                                //
                                //    Ladies can book times (7:55 to 11:59) for Tues starting 4 days in adv at 1:00 PM
                                //    instead of the normal 2 days in adv at 6:30 AM (days in adv changed above).
                                //
                                //    Ladies can book times (7:25 to 10:59) for Thurs starting 2 days in adv at 6:00 PM
                                //    instead of the normal 2 days in adv at 6:30 AM.
                                //
                                if (day_name.equals("Tuesday") && (index2 > 2 || (index2 == 2 && cal_time < 630))
                                        && (tee_time < 755 || tee_time > 1159)) {

                                    allow = false;      // do not allow if outside adv parms
                                }

                                if (day_name.equals("Thursday") && index2 == 2 && cal_time < 1800
                                        && tee_time < 1100 && tee_time > 724) {

                                    allow = false;      // do not allow 7:25 to 10:59 before 6 PM 2 days in adv
                                }
                            }

                            //
                            //  If Claremont CC and Adult Female
                            //
                            /*
                            if (club.equals("claremontcc") && mtype.equals("Adult Female")) {

                                //
                                //  Process according to the day of week
                                //    Ladies can book times (8:00 to 11:59) for Tues starting 30 days in advance
                                //
                                if (day_name.equals("Tuesday") && index2 > 3
                                        && (tee_time < 800 || tee_time > 1159)) {

                                    allow = false;      // do not allow if outside adv parms
                                }
                            }
                            */

                            //
                            //  If Seacliff CC and Adult Female 18 Holer
                            //
                            if (club.equals("seacliffcc") && msubtype.equals("18 Holer")) {

                                //
                                //  Process according to the day of week
                                //    Ladies can book times (7:00 to 11:00) for Tues up to 30 days in advance
                                //
                                if (day_name.equals("Tuesday") && index2 > 7
                                        && (tee_time < 700 || tee_time > 1100)) {

                                    allow = false;      // do not allow if outside adv parms
                                }
                            }

                            //
                            //  If Palo Alto Hills and Adult Female 18 Holer
                            //
                            if (club.equals("paloaltohills") && msubtype.equals("18 Holer")) {

                                //
                                //  Process according to the day of week
                                //    Ladies can book times (6:00 to 11:50) for Thurs up to 30 days in advance
                                //
                                if (day_name.equals("Thursday") && index2 > 7
                                        && (tee_time > 1159)) {

                                    allow = false;      // do not allow if outside adv parms and Ladies Day
                                }
                            }

                            //
                            //  If Brooklawn CC and Adult Female 18 Holer
                            //
                            if (club.equals("brooklawn")) {

                                if (day_name.equals("Tuesday")) {

                                    if (msubtype.equals("18 Holer") || msubtype.equals("9/18 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    18 Holers can book times (8:00 to 9:28) for Tues starting 7 days in advance
                                        //
                                        if (index2 > 2 && (tee_time < 800 || tee_time > 936)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not an 18 holer - restrict from 18 holer times

                                        if (tee_time > 759 && tee_time < 937) {    // if 18 holer time (Women's group)

                                            allow = false;      // do not allow non 18 holers (custom restriction)
                                        }
                                    }

                                } else if (day_name.equals("Wednesday") && date != 20120704) {

                                    if (msubtype.equals("9 Holer") || msubtype.equals("9/18 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    9 Holers can book times (8:00 to 10:00) for Wed starting 7 days in advance
                                        //
                                        if (index2 > 2 && (tee_time < 800 || tee_time > 1000)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not an 9 holer - restrict from 9 holer times

                                        if (tee_time > 759 && tee_time < 937) {    // if 9 holer time (Women's group)

                                            allow = false;      // do not allow non 18 holers (custom restriction)
                                        }
                                    }
                                }
                            }     // end of IF brooklawn


                            //
                            //  If Sharon Heights and Adult Female 18 Holer
                            //
                            if (club.equals("sharonheights") && day_name.equals("Tuesday")) {

                                if (msubtype.equals("18 Holer")) {

                                    //
                                    //  Process according to the day of week
                                    //    18 Holers can book times (7:30 to 9:54) for Tues starting 7 days in advance
                                    //
                                    if (index2 > 2 && (tee_time < 730 || tee_time > 954)) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not an 18 holer - restrict from 18 holer times

                                    if (tee_time > 729 && tee_time < 955) {    // if 18 holer time (Women's group)

                                        allow = false;      // do not allow non 18 holers (custom restriction)
                                    }
                                }
                            }     // end of IF sharonheights


                            //
                            //  If Green Hills CC and Ladies msubtype
                            //
                            if (club.equals("greenhills") && day_name.equals("Thursday")) {

                                if (msubtype.equals("Ladies")) {

                                    //
                                    //  Process according to the day of week
                                    //    "Ladies" can book times (6:45 to 10:30) for Thurs starting 7 days in advance
                                    //
                                    if (index2 > 2 && (tee_time < 645 || tee_time > 1030)) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not a "Ladies" golfer - restrict from Ladies times

                                    if (tee_time > 645 && tee_time < 1030) {    // if Ladies time (Women's group)

                                        allow = false;      // do not allow non Ladies (custom restriction)
                                    }
                                }
                            }     // end of IF greenhills


                            //
                            //  If Rolling Hills CC - CO and Ladies msubtype
                            //
                            if (club.equals("rhillscc")) {

                                if ((day_name.equals("Tuesday") || day_name.equals("Thursday"))) {

                                    if (dateShort >= 401 && dateShort <= 1031) {      // In season

                                        if (msubtype.equals("Ladies")) {

                                            //
                                            //  Process according to the day of week
                                            //    "Ladies" can book times (7:00 to 9:33) for Tues & Thurs starting 14 days in advance
                                            //
                                            if (index2 > 4 && (tee_time < 700 || tee_time > 933)) {

                                                allow = false;      // do not allow if outside adv parms
                                            }

                                        } else {    // not a "Ladies" golfer - restrict from Ladies times

                                            if (tee_time >= 700 && tee_time <= 933) {    // if Ladies time (Women's group)

                                                allow = false;      // do not allow non Ladies (custom restriction)
                                            }
                                        }

                                    } else {     // Out of season

                                        if (msubtype.equals("Ladies")) {

                                            //
                                            //  Process according to the day of week
                                            //    "Ladies" can book times (9:24 to 9:42) for Tues & Thurs starting 14 days in advance
                                            //
                                            if (index2 > 4 && (tee_time < 924 || tee_time > 942)) {

                                                allow = false;      // do not allow if outside adv parms
                                            }

                                        } else {    // not a "Ladies" golfer - restrict from Ladies times

                                            if (tee_time >= 924 && tee_time <= 942) {    // if Ladies time (Women's group)

                                                allow = false;      // do not allow non Ladies (custom restriction)
                                            }
                                        }
                                    }

                                } else if (day_name.equals("Wednesday")) {

                                    /*      // Removed at club's request - 5/31/12
                                    if ((dateShort >= 615 && dateShort <= 727 && tee_time >= 1215 && tee_time <= 1251)
                                            || ((dateShort < 615 || dateShort > 727) && tee_time >= 1157 && tee_time <= 1233)) {

                                        // Wednesday Group can book Wednesday 11:57-12:33 starting 3 days in advance
                                        if (index2 <= 3) {
                                            if (msubtype.equals("GOM")) {
                                                allow = true;
                                            } else {
                                                allow = false;
                                            }
                                        } else {
                                            allow = false;
                                        }
                                    }
                                    */

                                } else if (day_name.equals("Friday") && tee_time >= 1215 && tee_time <= 1251) {

                                    /*
                                    // Friday Group can book Friday 12:15-12:51 starting 7 days in advance
                                    if (index2 <= 7) {
                                        if (msubtype.equals("Friday Boys")) {
                                            allow = true;
                                        } else {
                                            allow = false;
                                        }
                                    } else {
                                        allow = false;
                                    }
                                    */
                                }
                            }     // end of IF greenhills
                            
                            
                            //  If Interlachen CC and Wednesday
                            if (club.equals("interlachen") && day_name.equals("Wednesday") && tee_time >= 1700 && tee_time <= 1830 
                                    && date >= 20120613 && date <= 20120829 && date != 20120704 && date != 20120718) {
                                
                                //  Only apply custom restrition if more than 2 days in advance. Once it's within 2 days, open up to everyone.
                                if (index > 2) {

                                    if (msubtype.equals("After Hours")) {
                                        allow = true;
                                    } else {
                                        allow = false;
                                    }
                                }
                            }


                            //
                            //  If Tualatin CC and Primary/Spouse Female member type
                            //
                            if (club.equals("tualatincc") && day_name.equals("Tuesday")) {

                                if (mtype.equals("Primary Female") || mtype.equals("Spouse Female")) {

                                    //
                                    //  Process according to the day of week
                                    //    Female members can book times (6:45 to 10:30) for Thurs starting 7 days in advance
                                    //
                                    if (index2 > 3 && (tee_time < 700 || tee_time > 1000)) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not a "Ladies" golfer - restrict from Ladies times

                                    if (tee_time >= 700 && tee_time <= 1000) {    // if Ladies time (Women's group)

                                        allow = false;      // do not allow non Ladies (custom restriction)
                                    }
                                }
                            }     // end of IF lagcc


                            //
                            //  If Los Altos G&CC and Female member type
                            //
                            if (club.equals("lagcc") && (day_name.equals("Tuesday") || day_name.equals("Thursday") || (date == 20120907 && day_name.equals("Friday")))) {

                                if (mtype.endsWith("Female")) {

                                    //
                                    //  Process according to the day of week
                                    //    Female members can book times (6:45 to 10:30) for Thurs starting 7 days in advance
                                    //
                                    if (((day_name.equals("Tuesday") || (date == 20120907 && day_name.equals("Friday"))) && index2 > 3 && (tee_time < 800 || tee_time > 930 || fb == 0))
                                            || (day_name.equals("Thursday") && index2 > 1 && (tee_time < 730 || tee_time > 930))) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not a "Ladies" golfer - restrict from Ladies times

                                    if (((day_name.equals("Tuesday") || (date == 20120907 && day_name.equals("Friday"))) && tee_time >= 800 && tee_time <= 930 && fb == 1)
                                            || (day_name.equals("Thursday") && tee_time >= 730 && tee_time <= 930)) {    // if Ladies time (Women's group)

                                        allow = false;      // do not allow non Ladies (custom restriction)
                                    }
                                }
                            }     // end of IF lagcc


                            //
                            //  If Indian Hills CC and 18 holer member subtype
                            //
                            if (club.equals("indianhillscc") && day_name.equals("Tuesday")) {

                                if (dateShort >= 401 && dateShort <= 1015) {

                                    if (msubtype.equals("18 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    Female members can book times (6:45 to 10:30) for Thurs starting 7 days in advance
                                        //
                                        if (index2 > 7 && tee_time > 1159 && fb != 1) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not a "Ladies" golfer - restrict from Ladies times

                                        if (tee_time <= 1159) {    // if Ladies time (Women's group)

                                            allow = false;      // do not allow non Ladies (custom restriction)
                                        }
                                    }
                                }
                            }     // end of IF indianhillscc


                            //
                            //  If CC of Fairfax and 18 holer or 9 Holer member subtype
                            //
                            if (club.equals("ccfairfax") && day_name.equals("Wednesday")) {

                                if (dateShort >= 401 && dateShort <= 1031) {

                                    if (msubtype.equals("18 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    Female 18 Holer members can book times 8:00am to 10:00am on the front only for Wed starting 14 days in advance
                                        //
                                        if (index2 > 1 && (tee_time > 1000 || fb != 0)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else if (msubtype.equals("9 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    Female 9 Holer members can book times 8:00am to 10:00am on the back only for Wed starting 14 days in advance
                                        //
                                        if (index2 > 1 && (tee_time > 1000 || fb != 1)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not a "18 Holer" or "9 Holer" golfer - restrict from Ladies times

                                        if (tee_time <= 1000) {    // if Ladies time (Women's group)

                                            allow = false;      // do not allow non 18/9 Holers (custom restriction)
                                        }
                                    }
                                }
                            }     // end of IF ccfairfax
                            
                            if (club.equals("miravista") && day_name.equals("Thursday")) {
                                
                                if (msubtype.equals("18 Holer")) {
                                    
                                    if (index2 > 2 && (tee_time > 1059 || tee_time < 700)) {
                                        allow = false;
                                    }
                                } else {
                                    if (index2 > 2 && (tee_time <= 1059 && tee_time >= 700)) {
                                        allow = false;
                                    }
                                }
                            }


                            //
                            //  If Algonquin GC and 9 Holer member subtype
                            //
                            if (club.equals("algonquin") && day_name.equals("Wednesday")) {

                                if (dateShort >= 401 && dateShort <= 1031) {

                                    if (msubtype.equals("9 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    Female 9 Holer members can book times 8:00am to 10:00am on the front or back for Wed starting 14 days in advance
                                        //
                                        if (index2 > 4 && (tee_time < 800 || tee_time > 1000)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not a "9 Holer" golfer - restrict from Ladies times

                                        if (tee_time >= 800 && tee_time <= 1000) {    // if Ladies time (Women's group)

                                            allow = false;      // do not allow non 9 Holers (custom restriction)
                                        }
                                    }
                                }
                            }     // end of IF algonquin


                            //
                            //  If Riviera CC and RWGA member subtype
                            //
                            if (club.equals("rivieracc")) {

                                if (msubtype.equals("RWGA")) {

                                    //
                                    //  Process according to the day of week
                                    //    Female RWGA members can book times 8:02am to 10:58am on the front or back for Tues starting 14 days in advance
                                    //
                                    if ((day_name.equals("Tuesday") && index2 > 2 && (tee_time < 802 || tee_time > 1058)) || 
                                        (day_name.equals("Thursday") && index2 > 2 && (tee_time != 858 && tee_time != 906))) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not a "RWGA" golfer - restrict from Ladies times

                                    if ((day_name.equals("Tuesday") && tee_time >= 802 && tee_time <= 1058) ||
                                        (day_name.equals("Thursday") && (tee_time == 858 || tee_time == 906))) {    // if Ladies time (Women's group)

                                        allow = false;      // do not allow non RWGA (custom restriction)
                                    }
                                }
                            }     // end of IF rivieracc
                            
                            if (club.equals("overlakegcc")) {
                                
                                if (msubtype.equals("18 Holer")) {
                                    
                                    // Ladies '18 Holer' subtypes can access Tuesday times 7:30AM - 10:30AM, and Friday 7:30AM - 9:30AM
                                    if ((day_name.equals("Tuesday") && (tee_time < 730 || tee_time > 1020)) ||
                                        (day_name.equals("Friday") && index2 > 3 && (tee_time < 730 || tee_time > 920))) {
                                        
                                        allow = false;
                                    }
                                    
                                } else {
                                    
                                    if ((day_name.equals("Tuesday") && (tee_time >= 730 && tee_time <= 1020)) ||
                                        (day_name.equals("Friday") && index2 > 3 && (tee_time >= 730 && tee_time <= 920))) {
                                        
                                        allow = false;
                                    }
                                }
                            }
                            
                            if (club.equals("brooksidecountryclub") && date >= 20130430 && date <= 20130703) {
                                /*
                                if (msubtype.equals("League")) {
                                    
                                    if ((day_name.equals("Tuesday") && (tee_time < 1500 || tee_time > 1820)) 
                                     || (day_name.equals("Wednesday") && ((date == 20130529 && (tee_time < 1500 || tee_time > 1820)) || (date != 20130529 && (tee_time < 1600 || tee_time > 1800))))
                                     || (day_name.equals("Thursday") && date == 20130530 && (tee_time < 1600 || tee_time > 1820))) {
                                        
                                        allow = false;
                                    }
                                    
                                } else {
                                    
                                    if ((day_name.equals("Tuesday") && tee_time >= 1500 && tee_time <= 1820) 
                                     || (day_name.equals("Wednesday") && ((date == 20130529 && tee_time >= 1500 && tee_time <= 1820) || (date != 20130529 && tee_time >= 1600 && tee_time <= 1800)))
                                     || (day_name.equals("Thursday") && date == 20130530 && tee_time >= 1600 && tee_time <= 1820)) {
                                        
                                        allow = false;
                                    }
                                }
                                 */
                            }

                            //
                            //  If Royal Oaks in WA and Adult Female
                            //
                            if (club.equals("royaloaks") && mtype.equals("Adult Female")
                                    && (day_name.equals("Tuesday") || day_name.equals("Friday"))) {


                                if (dateShort >= 301 && dateShort <= 1031) {
                                    //
                                    //    Women can book times (8:00 to 12:29) for Tues starting 7 days in advance
                                    //
                                    if (day_name.equals("Tuesday")) {

                                        if (index2 > 2 && (tee_time < 800 || tee_time > 1229)) {

                                            allow = false;      // do not allow if not an 18-Hole ladies time
                                        }

                                    } else if (day_name.equals("Friday")) {

                                        if ((index2 > 2 || (index == 2 && cal_time < 730)) && (tee_time > 1000 || fb != 1)) {

                                            allow = false;      // do not allow if not a 9-Hole ladies time
                                        }
                                    }
                                }

                            }     // end of IF royaloaks


                            // If The Plantation GC, don't allow members access to times before 10:29 until 6:30am Pacific 1 day in advance.
                            if (club.equals("theplantationgc")) {

                                if (tee_time <= 1029 && (index2 > 1 || (index2 == 1 && cal_time <= 700))) {

                                    allow = false;
                                }
                            }


                            // If Royal Montreal GC and 3 days in advance or less, open lottery times that have no players in them for booking.
                            if (club.equals("rmgc")) {  // Open up times 3 days in adv at 7:00am Eastern

                                if (index2 < 3 || (index2 == 3 && cal_time >= 700)) {

                                    if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("") && player5.equals("")) {
                                        noAccessAfterLottery = false;
                                    } else {
                                        noAccessAfterLottery = true;
                                    }

                                } else {
                                    noAccessAfterLottery = true;
                                }
                            }


                            // Indian Ridge CC - Don't allow members access to lottery times after they've been processed until after 7:00am Pacific 7 days in advance
                            if (club.equals("indianridgecc")) {

                                if (day_name.equals("Wednesday") && tee_time >= 739 && tee_time <= 918 && (index2 > 7 || (index2 == 7 && cal_time < 700))) {

                                    noAccessAfterLottery = true;
                                }
                            }


                            /*        // ON HOLD (case 1486)
                            //
                            //  If Los Coyotes CC and Secondary mtype
                            //
                            if (club.equals( "loscoyotes" ) && mtype.startsWith( "Secondary" ) && index > 3) {
                            
                            if (day_name.equals( "Tuesday" )) {
                            
                            if (tee_time < 700 || tee_time > 900) {
                            
                            allow = false;      // do not allow other times this far in adv
                            }
                            
                            } else {    // not Tues
                            
                            allow = false;      // do not allow other days this far in advance
                            }
                            }     // end of IF loscoyotes
                             */

                        }     // end of IF allow still true


                                
                        /*
                        //
                        //  If Jonathan's Landing and days in advance is 6 block access to each course except Hills - Case# 1328
                        //
                        if (club.equals( "jonathanslanding" ) && !courseNameT.equals( "Hills" ) && index == 6) {
                        
                        allow = false;
                        }
                         */
                        // waitlist check was here!
                        if (allow == true) {         // if still ok

                            //
                            //  if all spots taken and this player is not one of them or assigned to guest,
                            //  or did not originate the request, do not allow select
                            //
                            if (twoSomeOnly == true) {      // if 2-some ONLY time

                                if (!player1.equals("") && !player2.equals("")) {

                                    if (!player1.equals(full_name) && !player2.equals(full_name)) {

                                        if (!userg1.equals(user) && !userg2.equals(user) && !orig_by.equalsIgnoreCase(user)
                                                && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && !orig2.equalsIgnoreCase(user)))) {

                                            allow = false;     // all spots taken and this player not one of them
                                        }
                                    }
                                }

                            } else {         // NOT a 2-some time

                                if (threeSomeOnly == true) {      // if 3-some ONLY time

                                    if (!player1.equals("") && !player2.equals("") && !player3.equals("")) {

                                        if (!player1.equals(full_name) && !player2.equals(full_name) && !player3.equals(full_name)) {

                                            if (!userg1.equals(user) && !userg2.equals(user) && !userg3.equals(user) && !orig_by.equalsIgnoreCase(user)
                                                    && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && !orig2.equalsIgnoreCase(user) && !orig3.equalsIgnoreCase(user)))) {

                                                allow = false;     // all spots taken and this player not one of them
                                            }
                                        }
                                    }

                                } else {         // normal tee time

                                    if ((fives != 0) && (rest5.equals(""))) {   // if 5-somes and not restricted

                                        if ((!player1.equals("")) && (!player2.equals(""))
                                                && (!player3.equals("")) && (!player4.equals("")) && (!player5.equals(""))) {

                                            if ((!player1.equals(full_name)) && (!player2.equals(full_name))
                                                    && (!player3.equals(full_name)) && (!player4.equals(full_name)) && (!player5.equals(full_name))) {

                                                if (!userg1.equals(user) && !userg2.equals(user) && !userg3.equals(user)
                                                        && !userg4.equals(user) && !userg5.equals(user) && !orig_by.equalsIgnoreCase(user)
                                                        && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && !orig2.equalsIgnoreCase(user) && !orig3.equalsIgnoreCase(user)
                                                        && !orig4.equalsIgnoreCase(user) && !orig5.equalsIgnoreCase(user)))) {

                                                    allow = false;     // all spots taken and this player not one of them
                                                }
                                            }
                                        }
                                    } else {        // 4-some time

                                        if ((!player1.equals("")) && (!player2.equals(""))
                                                && (!player3.equals("")) && (!player4.equals(""))) {

                                            if ((!player1.equals(full_name)) && (!player2.equals(full_name))
                                                    && (!player3.equals(full_name)) && (!player4.equals(full_name))) {

                                                if (!userg1.equals(user) && !userg2.equals(user) && !userg3.equals(user)
                                                        && !userg4.equals(user) && !orig_by.equalsIgnoreCase(user)
                                                        && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && !orig2.equalsIgnoreCase(user) && !orig3.equalsIgnoreCase(user)
                                                        && !orig4.equalsIgnoreCase(user)))) {

                                                    allow = false;     // all spots taken and this player not one of them
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //
                            //  if today's sheet and the tee time is less than the current time do not allow select
                            //
                            if ((index2 == 0) && (tee_time <= cal_time)) {

                                allow = false;     // do not allow select
                            }

                            //
                            //  if today's sheet and the tee time is less than the stop time do not allow select (custom)
                            //
                            if (allow == true && index2 == 0 && stop_time > 0) {

                                if (tee_time <= stop_time) {

                                    allow = false;     // do not allow select
                                }
                            }

                        }

                        if (club.equals("mpccpb")) {
                            
                            if (!player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("") && !player5.equals("") 
                            && (player1.startsWith("Need A Player") || player2.startsWith("Need A Player") || player3.startsWith("Need A Player") || player4.startsWith("Need A Player") || player5.startsWith("Need A Player"))) {
                              
                                allow = true;
                            }
                        }
                        
                        if (club.startsWith("demo")) {
                            
                            if (!player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("") 
                            && (player1.startsWith("Join Me") || player2.startsWith("Join Me") || player3.startsWith("Join Me") || player4.startsWith("Join Me") || player5.startsWith("Join Me"))) {
                              
                                allow = true;
                            }
                        }
                                
                        submit = "time:" + fb;       // create a name for the submit button

                        j++;                         // increment the jump label index (where to jump on page)

                        //
                        //  Start this tee time row
                        //
                        if (mobile == 0) {
                            hashMap.clear(); // clear the hashmap for use in form data collection
                            //j++;                         // increment the jump label index (where to jump on page)
                            out.println("<tr data-ftrowtype=\""+StringEscapeUtils.escapeHtml(courseNameT+":"+sfb)+"\">");         // start of tee slot (row)
                            //out.println("<a name=\"jump" + j + "\"></a>"); // <!-- allow=" + ((allow) ? "true" : "false") + " firstLott=" + firstLott + " firstWait=" + firstWait + " tee_time=" + tee_time + " -->"); // create a jump label for 'noshow' returns

                        } else {

                            out.println("<tr class=\"tablerow\"><td>");         // start of tee slot row and time column for mobile users (must have form inside of the td!)
                        }


                        //
                        //   // USE THE FOLLOWING NOW SO WE WILL NOT OVERRIDE A LOTTERY IN STATE 2 !!!!!!!!!!!


                        if (allow == true && in_use == 0) {          // can user select this slot and not in use?

                            lstate = 0;                               // init as no lottery
                            slots = 0;

                            //
                            //  if not event, then check for lottery time (events override lotteries)
                            //
                            if (event.equals("") && !lottery.equals("")) {

                                lottloop3:
                                for (i = 0; i < count_lott; i++) {     // check for matching lottery

                                    if (lottery.equals(lottA[i])) {    // if match found

                                        lstate = lstateA[i];
                                        slots = slotsA[i];
                                        break lottloop3;
                                    }
                                }
                                i = 0;
                            }


                            /*     moved to Proshop_dsheet
                            //
                            //  Custom for Ramsey - if lottery processed, then ignore the wait list.  The wait list is only used during lottery signup.
                            //
                            if (club.equals("ramseycountryclub") && lstate == 5) {
                            
                            waitlist = false;        // allow members to access the tee times after the lottery is processed
                            }
                             */



                            //
                            //  if not event, then check for lottery time (events override lotteries)
                            //
                            if (event.equals("") && !lottery.equals("")) {  // lotteries are not in use before processing date/time

                                if (lstate == 2) {        // if lottery and ok to make lottery request - THIS OVERRIDES A WAIT LIST !!!!!

                                    if (mobile == 0) {
                                        hashMap.put("type", "Member_mlottery");
                                        hashMap.put("lstate", lstate);
                                        hashMap.put("lname", lottery);
                                        hashMap.put("slots", slots);
                                    } else {
                                        out.println("<form action=\"Member_mlottery\" method=\"get\">");
                                        out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
                                        out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottery + "\">");
                                        out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                                    }

                                } else {

                                    if (!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) {   // if no Wait List or WL not to be selected

                                        if (lstate == 5 && index <= days && noAccessAfterLottery == false) {  // lottery is done - normal request (lottery)

                                            if (mobile == 0) {
                                                hashMap.put("type", "Member_slot");
                                                hashMap.put("lstate", lstate);
                                            } else {
                                                out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
                                                out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
                                            }

                                        } else {      // not valid for members

                                            allow = false;
                                        }
                                    }
                                    /*                // no where to put this now ????  time will not be allowed if more than 4 lotteries defined (allow=false)
                                    } else {     // more than 4 lotteries in one day !!!
                                    
                                    //
                                    //  REMOVED BECAUSE TRANSITVALLEY IS FLOODING THE LOG WITH THIS MESSAGE
                                    //
                                    //String errorMsg = "Member_sheet: More than 4 lotteries defined for one day at " + club;    // build error msg
                                    //SystemUtils.logError(errorMsg);                           // log it
                                    
                                    out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
                                    }
                                     */
                                }

                            } else {   // no active lottery for this tee time

                                lstate = 0;

                                //
                                //   lottery is cleared from teecurr2 after lottery completely processed (in dsheet) - check lottery_color for "no access" custom!!
                                //
                                if (!lottery_color.equals("") && noAccessAfterLottery == true) {   // if this time was part of a lottery and club wants to block members from changing it

                                    allow = false;                                                  // then block access

                                } else {

                                    if (!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) {   // if no Wait List or WL not to be selected

                                        if (mobile == 0) {

                                            hashMap.put("type", "Member_slot");
                                            hashMap.put("lstate", lstate);

                                        } else {

                                            out.println("<form action=\"Member_slot\" method=\"post\" name=\"mform" + j + "\">");  // no target for mobile
                                        }
                                    }
                                }
                            }

                            //
                            //  Check if this is an empty tee time.  If so, indicate such for Member_slot so we know that the user is trying to select
                            //  a new tee time.
                            //
                            if (!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) {   // if no Wait List or WL not to be selected

                                if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("") && player5.equals("") && allow == true) {
                                    if (mobile == 0) {
                                        hashMap.put("newreq", "yes");
                                    } else {
                                        out.println("<input type=\"hidden\" name=\"newreq\" value=\"yes\">");
                                    }
                                }
                            }
                        }        // end of if OK



                        //
                        //  If Hazeltine and more than 7 days in advance
                        //
                        if (club.equals("hazeltine") && index2 > 7) {

                            if ((allow == true) && (in_use == 0)) {         // if still ok
                                //
                                //  Custom processing for Hazeltine
                                //
                                //    Certain women are allowed to book times at specific times on Tuesdays and
                                //    Thursdays 14 days in advance (vs. 7 days normally).
                                //
                                //    If allow is still true, then they must have the right to edit tee times on this day.
                                //    We must limit access to the specified times for that day.
                                //
                                if (day_name.equals("Thursday")) {

                                    if (tee_time < 724 || tee_time > 930) {         // must be between 7:24 and 9:30

                                        allow = false;
                                    }
                                }
                                if (day_name.equals("Tuesday")) {

                                    allow = false;    // default to not allowed so both types can be checked

                                    if (msubtype.equals("After Hours") || msubtype.startsWith("AH")) {   // if 'After Hours' type

                                        if (tee_time > 1631 & tee_time < 1831) {    // must be between 4:32 and 6:30

                                            allow = true;
                                        }
                                    }
                                    if (msubtype.equals("9 Holer") || msubtype.equals("AH-9 Holer")
                                            || msubtype.equals("AH-9/18 Holer") || msubtype.equals("9/18 Holer")) {  // if '9 Holer' type

                                        if (tee_time > 723 && tee_time < 921) {    // must be between 7:24 and 9:20

                                            allow = true;
                                        }
                                    }
                                }
                            }
                        }

                        boolean showWLimg = false;

                        if (lstate != 2) {         // if lottery signup, then override wait list

                            if (waitlist && parmWaitLists.member_view_teesheet[WL_index] == 2) {

                                // fake as busy only if there is an existing signup covering this tee time
                                if (getWaitList.checkForSignups(wait_list_id, (int) date, tee_time, con)) {
                                    //out.println("<!-- FOUND SIGNUP COVERING THIS TEETIME! wait_list_id="+wait_list_id+", date="+date+", tee_time="+tee_time+" -->");
                       
                                    if (player1.equals("") 
                                            || (!player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("") 
                                            & !user1.equalsIgnoreCase(user) && !user2.equalsIgnoreCase(user) && !user3.equalsIgnoreCase(user) && !user4.equalsIgnoreCase(user) && !orig_by.equalsIgnoreCase(user))) {
                                        in_use = 1;
                                        showWLimg = true;
                                    }
                                }

                            } else if (waitlist && parmWaitLists.member_view_teesheet[WL_index] == 1) {

                                // fake all tee times as in use, (this option is for display the tee sheet but not allowing access)
                                in_use = 1;
                            }
                        }

                        // show the wait list button for upcoming tee times but not past ones
                        if (lstate != 2 && waitlist && parmWaitLists.member_view_teesheet[WL_index] == 0) { // && !(index2 == 0 && tee_time <= cal_time) ) {

                            WLbtn_displayed[WL_index] = true;       // do not show other times covered by this wait list

                            if (mobile == 0) {        // do not support wait list for mobile user (maybe in future)

                                hashMap.put("type", "Member_waitlist");
                                hashMap.put("waitListId", wait_list_id);
                                hashMap.put("course", courseNameT);
                                hashMap.put("returnCourse", courseName1);
                                hashMap.put("jump", j);
                                hashMap.put("index", index);
                                hashMap.put("date", date);
                                // hashMap.put("day", day_name);

                                //jsonHashMap = gson_obj.toJson(hashMap);

                                // out.println("<form action=\"Member_waitlist\" method=\"post\" target=\"_top\">");
                                out.print("<td>");
                                out.print("<a href=\"#\" class=\"waitlist_button standard_button\"" + getBgColor(bgcolor) + " data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\"" + getBgColor(bgcolor) + ">Wait List</a>");
                                //  out.println("<input type=\"submit\" value=\"Wait List\" style=\"background-color:" + getWaitList.getColor(wait_list_id, con) + "\">");
                                //  out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
                                //   out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                                //   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + courseName1 + "\">");
                                //   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                                //   out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                //   out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                                //   out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                                out.println("</td>");
                                //  out.println("</form>");
                                if (parm.constimesm > 1) {
                                    out.println("<td></td>");
                                }
                            }
                            // suppress the player names
                            player1 = "";
                            player2 = "";
                            player3 = "";
                            player4 = "";
                            player5 = "";

                        } else {
                            
                            String shotgunLabel = "";
                            String teetimeLabel = "";

                            if (type == shotgun) {

                                eventloop1:
                                for (i = 0; i < count_event; i++) {

                                    if (event.equals(eventA[i])) {

                                        shotgunLabel = SystemUtils.getSimpleTime(act_hrA[i], act_minA[i]);
                                        break eventloop1;
                                    }
                                }

                                i = 0;       // reset index

                                if (!shotgunLabel.equals("")) {       // if time gathered above

                                    StringTokenizer toks = new StringTokenizer(shotgunLabel, " ");     // delimiters are space (8:30 AM)

                                    shotgunLabel = toks.nextToken();                             // get the time only (8:30)
                                }

                                shotgunLabel += " Shotgun";            // i.e.  9:30 Shotgun
                                
                                teetimeLabel = shotgunLabel;           // use this in the tee time if it passes the 'allow' test
                                
                            } else {
                                
                                teetimeLabel = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;
                            }


                            // restrict member access to times booked via a tsp
                            if (!tmp_allow_members_to_join && !player1.equals("") && player1.startsWith(tsp_guest_type)) {
                                    
                                // check to see if this member is already part of this tee time
                                if (!user.equals(user1) && !user.equals(user2) && !user.equals(user3) && !user.equals(user4) && !user.equals(user5)) {

                                    allow = false;
                                }
                            }


                            if (allow == true && in_use == 0 && hideSubmit == 0) {         // if still ok
                            
                                if (fives != 0 && rest5.equals("")) {   // if 5-somes and not restricted
                                    shortTermTmp = "Yes";
                                } else {
                                    shortTermTmp = "No";
                                }

                                if (mobile == 0) {
                                    out.print("<td>");
                                    hashMap.put("displayOpt", displayOpt);
                                    hashMap.put("ttdata", Utilities.encryptTTdata(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "|" + fb + "|" + user));
                                    hashMap.put("date", date);
                                    hashMap.put("index", index);
                                    hashMap.put("course", courseNameT);
                                    hashMap.put("returnCourse", courseName1);
                                    hashMap.put("jump", j);
                                    hashMap.put("wasP1", player1);
                                    hashMap.put("wasP2", player2);
                                    hashMap.put("wasP3", player3);
                                    hashMap.put("wasP4", player4);
                                    hashMap.put("wasP5", player5);
                                    hashMap.put("p5", shortTermTmp);
                                    hashMap.put("time:0", (hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm));
                                } else {
                                    // Mobile user
                                    // out.println("<td>");  // moved to follow the tr above
                                    out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                                    out.println("<input type=\"hidden\" name=\"ttdata\" value=\"" + Utilities.encryptTTdata(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "|" + fb + "|" + user) + "\">");

                                    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                                    out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + courseName1 + "\">");
                                    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                                    out.println("<input type=\"hidden\" name=\"wasP1\" value=\"" + player1 + "\">");
                                    out.println("<input type=\"hidden\" name=\"wasP2\" value=\"" + player2 + "\">");
                                    out.println("<input type=\"hidden\" name=\"wasP3\" value=\"" + player3 + "\">");
                                    out.println("<input type=\"hidden\" name=\"wasP4\" value=\"" + player4 + "\">");
                                    out.println("<input type=\"hidden\" name=\"wasP5\" value=\"" + player5 + "\">");
                                    out.println("<input type=\"hidden\" name=\"p5\" value=\"" + shortTermTmp + "\">");  // tell _slot to do 5's
                                }



                                if (lstate == 2) {       // if ok for lottery request

                                    if (club.equals("cherryhills") && (mship.equals("Resident Emeritus") || mship.equals("Non-Resident"))) {

                                        out.println("<div class=\"lottery_slot\">Lottery</div>");       // do not allow these members to submit a Lottery Req

                                    } else {

                                        //out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");
                                        if (lotteryText.equals("")) {
                                            shortTermTmp = "Lottery";
                                        } else { // members do not like the word Lottery - use something else
                                            shortTermTmp = "Request Time";
                                        }
                                        if (mobile == 0) {
                                            out.print("<a href=\"#\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\" class=\"lottery_button standard_button\"" + getBgColor(bgcolor) + ">" + shortTermTmp + "</a>");
                                        } else {
                                            out.println("<input type=\"submit\" value=\"" + shortTermTmp + "\">");
                                        }
                                    }

                                } else {

                                    //
                                    //  If Oakmont CC and Wed or Fri, or Milwaukee, and not a shotgun today, and this is a time when
                                    //  multiple guests are allowed, make the submit button green.
                                    //
                                    shortTermTmp = ""; // By default, no override style
                                    if (oaktime == true || mcctime == true || beartime == true || congresstime == true) {
                                        if (mobile == 0) {
                                            shortTermTmp = " teetime_button_alternate1";
                                        } else {
                                            // This conditional for mobile could be removed, if "teetime_button_alternate1" is added to mobile css.
                                            shortTermTmp = "\" style=\"text-decoration:none; background:lightgreen";  // Override style fore mobile.
                                        }
                                    }
                                    if (mobile == 0) {
                                       // out.print("<a href=\"#\" class=\"teetime_button standard_button" + shortTermTmp + "\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "</a>");
                                        out.print("<a href=\"#\" class=\"teetime_button standard_button" + shortTermTmp + "\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + teetimeLabel + "</a>");
                                    } else {
                                       // out.println("<input type=\"submit\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\"" + shortTermTmp + ">"); //name=\"" + submit + "\" id=\"" + submit + "\"
                                        out.println("<input type=\"submit\" value=\"" + teetimeLabel + "\"" + shortTermTmp + ">"); //name=\"" + submit + "\" id=\"" + submit + "\"
                                    }
                                }

                                if (mobile > 0 && allowMulti == false) {
                                    out.println("<input type=\"hidden\" name=\"contimes\" value=\"1\">");
                                    out.println("</form>");                                                 // end form here
                                }
                                out.println("</td>");

                                //
                                //  if Consecutive Tee Times allowed, no lottery or event, and tee time is empty,
                                //   and F or B tee, allow member to select more than one tee time
                                //

                                // Custom for Fox Hill to override parm.constimesm on the weekdays
                                if (club.equals("hoxhill") && !day_name.equals("Saturday") && !day_name.equals("Sunday")) {

                                    parm.constimesm = 4;
                                }

                                //
                                //  Governors Club - Determine # of consec times available to members based on course, day, and time.
                                //
                                if (club.equals("governorsclub")) {

                                    if (courseNameT.equals("Primary Rotation")) {

                                        if (day_name.equals("Tuesday") || (day_name.equals("Wednesday") || day_name.equals("Thursday") || day_name.equals("Friday") 
                                                || day_name.equals("Saturday") || day_name.equals("Sunday"))) {

                                            if (tee_time >= 800 && tee_time <= 950) {

                                                if (dateShort <= 315 || dateShort >= 1001) {
                                                    parm.constimesm = 2;
                                                } else {
                                                    parm.constimesm = 1;
                                                }

                                            } else {
                                                parm.constimesm = 4;
                                            }

                                        }/* else if (day_name.equals("Saturday")) {

                                            if (dateShort <= 315 || dateShort >= 1001) {

                                                if (tee_time >= 800 && tee_time <= 950) {
                                                    parm.constimesm = 2;
                                                } else {
                                                    parm.constimesm = 4;
                                                }

                                            } else {

                                                if (tee_time >= 800 && tee_time <= 1030) {
                                                    parm.constimesm = 1;
                                                } else {
                                                    parm.constimesm = 4;
                                                }
                                            }
                                        }*/

                                    } else if (courseNameT.equals("Secondary Rotation")) {
                                        parm.constimesm = 2;
                                    }
                                }


                                //
                                //  If Mobile User, then do NOT allow consecutive times!!!!!!!
                                //
                                if (mobile > 0 && allowMulti == false) {
                                    parm.constimesm = 1;           // make sure this is still 1 after the customs
                                }


                                /*
                                //
                                //  Rivercrest Golf Club & Preserve (case 1492)
                                //
                                if (club.equals( "rivercrestgc" ) && threeSomeDay == true && tee_time < 1031) {

                                    skipconstimes = true;
                                }
                                */


                                //
                                //  Tahoe Donner (case 1845)
                                //
                                if (club.equals("tahoedonner") && (day_name.equals("Saturday") || day_name.equals("Sunday")) && tee_time < 1151) {

                                    skipconstimes = true;
                                }


                                if (mobile == 0 || allowMulti == true) {   // only if NOT mobile OR custom to allow it on Mobile

                                    if (parm.constimesm > 1) {           // if consecutive times allowed and ok for this tee time

                                        out.print("<td>");

                                        if (allow == true && player1.equals("") && player2.equals("") && player3.equals("")
                                                && player4.equals("") && player5.equals("")
                                                && event.equals("") && (lottery.equals("") || lstate > 4) && (fb == 0 || fb == 1) && skipconstimes == false) {

                                            if (mobile > 0 && allowMulti == true) {                                
                                                out.println("<select size=\"1\" name=\"contimes\" onChange=\"document.mform" + j + ".submit()\">");
                                            } else {
                                                out.print("<select>");
                                            }
                                            if (club.equals("plantationpv")) {
                                                out.print("<option selected value=\"0\">0</option>");
                                                out.print("<option value=\"1\">1</option>");
                                            } else {
                                               if (contimesDefault == 1) {      // default to vlaue passed from Member_select (or 1 if not passed)
                                                   out.print("<option selected value=\"1\">1</option>");
                                               } else {                                                  
                                                   out.print("<option value=\"1\">1</option>");
                                               }
                                            }
                                            
                                            for (int k = 2; k <= parm.constimesm; k++) {
                                               if (contimesDefault == k) {      // default to vlaue passed from Member_select (or 1 if not passed)
                                                   out.println("<option selected value=\"" + k + "\">" + k + "</option>");
                                               } else {
                                                   out.println("<option value=\"" + k + "\">" + k + "</option>");
                                               }
                                            }
                                            
                                            out.print("</select>");

                                            if (mobile > 0 && allowMulti == true) {                                
                                                //  add hidden time parm so value passed when this item is selected
                                                //
                                                out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">");
                                            }

                                        } else {
                                            // out.println("<input type=\"hidden\" name=\"contimes\" value=\"1\">");
                                        }
                                        out.println("</td>");

                                    } else {

                                        if (club.equals("governorsclub")) {
                                            out.print("<td>");
                                            //out.println("<input type=\"hidden\" name=\"contimes\" value=\"1\">");
                                            out.println("</td>");
                                        } else {
                                            //out.println("<input type=\"hidden\" name=\"contimes\" value=\"1\">");
                                        }
                                    }
                                    
                                    if (mobile > 0 && allowMulti == true) {                                
                                        out.println("</form>");
                                    }
                                }

                            } else {        // not wait list and not ok to access

                                if (mobile == 0) {
                                    out.print("<td>");
                                    // } else {
                                    // out.println("<td>");      // let style sheet dictate color, etc. (moved to tr above)
                                }

                                if (type == shotgun) {

                                    out.print("<div class=\"shotgun_slot\">" + shotgunLabel + "</div>");

                                } else {

                                    if (showWLimg) {
                                        if (mobile == 0) {       // do not allow user to enter wait list if mobile user
                                            hashMap.put("type", "Member_waitlist");
                                            hashMap.put("waitListId", parmWaitLists.id[WL_index]);
                                            hashMap.put("course", courseNameT);
                                            hashMap.put("returnCourse", parmWaitLists.courseName[WL_index]);
                                            hashMap.put("jump", j);
                                            hashMap.put("index", num);
                                            hashMap.put("date", date);
                                            out.print("<a href=\"#\" class=\"standard_button waitlist_image_link\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "</a>");
                                        } else {
                                            out.print("Wait List<BR>");
                                        }
                                    }else{

                                        // default time displayed if no access
                                        out.print("<div class=\"time_slot\">" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "</div>");
                                    }

                                }
                                out.println("</td>");

                                if (parm.constimesm > 1 || club.equals("governorsclub")) {
                                    out.print("<td>");
                                    //out.println("&nbsp;");
                                    out.print("</td>");
                                }

                            }           // end of time column and possible constimes col

                        }               // end of IF waitlist


                        //
                        //  Check for Mobile user
                        //
                        if (mobile == 0) {

                            //
                            //  Standard Device - Add Course Name
                            //
                            if (multi != 0) {        // case 1509

                                if (courseName1.equals("-ALL-")) {

                                    ci = course.indexOf(courseNameT);     // get the index value of this course

                                    if (ci < colorMax) {                  // if color defined for this course - use it

                                        out.print("<td" + getBgColor(colors.course_color[ci]) + ">");     // use course color

                                    } else {

                                        out.print("<td>");
                                    }

                                } else {
                                    out.print("<td" + getBgColor(bgcolor) + ">");
                                }
                                out.print(courseNameT);
                                out.println("</td>");
                            }
                        }         // end of IF NOT Mobile

                        //
                        //  Front/Back indicator
                        //
                        if (mobile == 0) {

                            // if (club.equals("oakleycountryclub") && sfb.equals("B")) {          // if Oakley CC and a Back Tee
                            if (sfb.equals("B")) {                                              // if Back Tee
                                out.print("<td class=\"highlight\">");         // highlight it
                            } else {
                                out.print("<td>");
                            }
                            out.print((!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) ? sfb : "");
                            out.println("</td>");
                        } else {
                            // out.println("<td align=\"center\" style=\"background-color:#F5F5DC\">");
                            out.println("<td>");     // let css file dictate color, etc.
                            out.println((!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) ? sfb : "&nbsp;");
                            out.println("</td>");
                        }


                        //
                        //  Sonnenalp Custom - add guest fees to guest names if they exist (no 5-somes!!)
                        //
                        if (club.equals("sonnenalp")) {           // if Sonnenalp

                            if (g1 != 0 && !custom_disp1.equals("")) {    // if guest and rate was found

                                player1 = player1 + " " + custom_disp1;
                            }
                            if (g2 != 0 && !custom_disp2.equals("")) {    // if guest and rate was found

                                player2 = player2 + " " + custom_disp2;
                            }
                            if (g3 != 0 && !custom_disp3.equals("")) {    // if guest and rate was found

                                player3 = player3 + " " + custom_disp3;
                            }
                            if (g4 != 0 && !custom_disp4.equals("")) {    // if guest and rate was found

                                player4 = player4 + " " + custom_disp4;
                            }
                        }

                        String p = "";

                        oldbgcolor = bgcolor;

                        //
                        // If a tee_sheet_partner is active for this club and we are to mask the outside rounds
                        // then set the hideG and specify the display name
                        //
                        if (tsp_active && tsp_hide_outside_rounds) {

                            if (player1.startsWith(tsp_guest_type)) {
                                g1 = 1; // set to guest
                                hideG = 1;
                                guestLabel = tsp_display_name;
                            }
                            
                            if (player2.startsWith(tsp_guest_type)) {
                                g2 = 1; // set to guest
                                hideG = 1;
                                guestLabel = tsp_display_name;
                            }
                            
                            if (player3.startsWith(tsp_guest_type)) {
                                g3 = 1; // set to guest
                                hideG = 1;
                                guestLabel = tsp_display_name;
                            }
                            
                            if (player4.startsWith(tsp_guest_type)) {
                                g4 = 1; // set to guest
                                hideG = 1;
                                guestLabel = tsp_display_name;
                            }
                            
                            if (player5.startsWith(tsp_guest_type)) {
                                g5 = 1; // set to guest
                                hideG = 1;
                                guestLabel = tsp_display_name;
                            }
                        }

                        //
                        //  Check for Mobile user
                        //
                        if (mobile == 0) {

                            //
                            //  Add Player 1
                            //
                            if (club.equals("southview") || club.equals("dellwood") || club.equals("philcricket") || club.startsWith("demo")) {
                                if (player1.startsWith("Join Me")) {
                                    bgcolor = "lime";
                                } else {
                                    bgcolor = oldbgcolor;
                                }
                            }
                            out.print("<td" + getBgColor(bgcolor) + ">");
                            if (!player1.equals("")) {

                                player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g1, hideG, hndcp1, ghin1, player1, guestLabel, memberLabel, club);

                                out.print(player);

                            } else {     // player is empty
                                // out.println("&nbsp;");
                            }
                            out.println("</td>");
                            out.print("<td>");
                            if ((!player1.equals("")) && (!player1.equalsIgnoreCase("x"))) {
                                out.print(p1cw);
                            } else {
                                // out.print("&nbsp;");
                            }
                            out.println("</td>");

                            //
                            //  Add Player 2
                            //
                            if (club.equals("southview") || club.equals("dellwood") || club.equals("philcricket") || club.startsWith("demo")) {
                                if (player2.startsWith("Join Me")) {
                                    bgcolor = "lime";
                                } else {
                                    bgcolor = oldbgcolor;
                                }
                            } else if (club.equals("mpccpb")) {
                                if (player2.startsWith("Need A Player")) {
                                    bgcolor = "lime";
                                } else {
                                    bgcolor = oldbgcolor;
                                }
                            }
                            out.print("<td" + getBgColor(bgcolor) + ">");

                            if (!player2.equals("")) {

                                player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g2, hideG, hndcp2, ghin2, player2, guestLabel, memberLabel, club);

                                out.print(player);

                            } else {     // player is empty
                                //out.println("&nbsp;");
                            }
                            out.print("</td>");

                            out.print("<td>");
                            if ((!player2.equals("")) && (!player2.equalsIgnoreCase("x"))) {
                                out.print(p2cw);
                            } else {
                                // out.println("&nbsp;");
                            }
                            out.println("</td>");

                            if (club.equals("mayfieldsr") && courseNameT.equalsIgnoreCase("sand ridge") && verifyCustom.checkMayfieldSR(date, tee_time, day_name)) {

                                out.println("<td></td><td></td>");       // blank player 3
                                out.println("<td></td><td></td>");       // blank player 4
                                out.println("<td></td><td></td>");       // blank player 5

                            } else {
                                //
                                //  Add Player 3
                                //
                                if (club.equals("southview") || club.equals("dellwood") || club.equals("philcricket") || club.startsWith("demo")) {
                                    if (player3.startsWith("Join Me")) {
                                        bgcolor = "lime";
                                    } else {
                                        bgcolor = oldbgcolor;
                                    }
                                } else if (club.equals("mpccpb")) {
                                    if (player3.startsWith("Need A Player")) {
                                        bgcolor = "lime";
                                    } else {
                                        bgcolor = oldbgcolor;
                                    }
                                }
                                out.print("<td" + getBgColor(bgcolor) + ">");

                                if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                                    out.println("X");            // 2-somes ONLY

                                } else {                  // not Piedmont special time

                                    if (!player3.equals("")) {

                                        player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g3, hideG, hndcp3, ghin3, player3, guestLabel, memberLabel, club);

                                        out.print(player);

                                    } else {     // player is empty
                                        //out.println("&nbsp;");
                                    }
                                }
                                out.println("</td>");

                                out.print("<td>");
                                if ((!player3.equals("")) && (!player3.equalsIgnoreCase("x"))) {
                                    out.print(p3cw);
                                } else {
                                    //out.println("&nbsp;");
                                }
                                out.println("</td>");

                                //
                                //  Add Player 4
                                //
                                if (club.equals("southview") || club.equals("dellwood") || club.equals("philcricket") || club.startsWith("demo")) {
                                    if (player4.startsWith("Join Me")) {
                                        bgcolor = "lime";
                                    } else {
                                        bgcolor = oldbgcolor;
                                    }
                                } else if (club.equals("mpccpb")) {
                                    if (player4.startsWith("Need A Player")) {
                                        bgcolor = "lime";
                                    } else {
                                        bgcolor = oldbgcolor;
                                    }
                                }
                                out.print("<td" + getBgColor(bgcolor) + ">");

                                if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                                    out.println("X");            // 2-somes ONLY

                                } else {                  // not Piedmont special time

                                    if (!player4.equals("")) {

                                        player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g4, hideG, hndcp4, ghin4, player4, guestLabel, memberLabel, club);

                                        out.print(player);

                                    } else {     // player is empty
                                        //out.println("&nbsp;");
                                    }
                                }
                                out.println("</td>");

                                out.print("<td>");
                                if ((!player4.equals("")) && (!player4.equalsIgnoreCase("x"))) {
                                    out.print(p4cw);
                                } else {
                                    // out.println("&nbsp;");
                                }
                                out.println("</td>");

                                //
                                //  Add Player 5 if supported
                                //
                                if (fivesALL != 0) {        // if 5-somes supported on any course

                                    if (fives != 0) {        // if 5-somes on this course

                                        if (!rest5.equals("") && event.equals("")) {       // if 5-somes are restricted & not an event

                                            out.print("<td" + getBgColor(bgcolor5) + ">");
                                            if (!player5.equals("") && (club.equals("championhills") || club.equals("bentcreek"))) {
                                                player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g5, hideG, hndcp5, ghin5, player5, guestLabel, memberLabel, club);
                                                out.print(player);
                                            } else {
                                                //out.println("&nbsp;");
                                            }

                                        } else {        // no 5-some rest or this is an event time

                                            if (club.equals("southview") || club.equals("dellwood") || club.equals("philcricket") || club.startsWith("demo")) {
                                                if (player5.startsWith("Join Me")) {
                                                    bgcolor = "lime";
                                                } else {
                                                    bgcolor = oldbgcolor;
                                                }
                                            } else if (club.equals("mpccpb")) {
                                                if (player5.startsWith("Need A Player")) {
                                                    bgcolor = "lime";
                                                } else {
                                                    bgcolor = oldbgcolor;
                                                }
                                            }

                                            out.print("<td" + getBgColor(bgcolor) + ">");

                                            if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                                                out.print("X");            // 2-somes ONLY

                                            } else {                  // not Piedmont special time

                                                if (!player5.equals("")) {

                                                    player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g5, hideG, hndcp5, ghin5, player5, guestLabel, memberLabel, club);

                                                    out.print(player);

                                                } else {     // player is empty
                                                    //out.println("&nbsp;");
                                                }
                                            }
                                        }
                                        out.println("</td>");

                                        out.print("<td>");
                                        if ((!player5.equals("")) && (!player5.equalsIgnoreCase("x")) && (club.equals("championhills") || club.equals("bentcreek"))) {
                                            out.print(p5cw);
                                        } else {
                                            //out.println("&nbsp;");
                                        }
                                        out.println("</td>");

                                    } else {         // 5-somes supported on at least 1 course, but not this one (if course=ALL)

                                        out.println("<td class=\"blackout\"></td>");   // no 5-somes
                                        out.println("<td class=\"blackout\"></td>");
                                    }
                                }
                            }


                        } else {

                            //
                            //  Mobile User - display all players in one cell
                            //
                            out.println("<td style=\"background-color:" + bgcolor + "\">");

                            if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("") && player5.equals("")) {

                                out.println("&nbsp");

                            } else {

                                StringBuffer playerBfr = new StringBuffer();       // get buffer for players

                                //   Add Player 1
                                if (!player1.equals("")) {

                                    player = buildPlayerMobile(hideGuestCC, hideN, g1, hideG, player1, guestLabel, memberLabel, club);

                                    playerBfr.append(player);

                                }

                                //   Add Player 2
                                if (!player2.equals("")) {

                                    playerBfr.append("<BR>");             // add seperator

                                    player = buildPlayerMobile(hideGuestCC, hideN, g2, hideG, player2, guestLabel, memberLabel, club);

                                    playerBfr.append(player);
                                }

                                //   Add Player 3
                                if (!player3.equals("")) {

                                    playerBfr.append("<BR>");             // add seperator

                                    player = buildPlayerMobile(hideGuestCC, hideN, g3, hideG, player3, guestLabel, memberLabel, club);

                                    playerBfr.append(player);
                                }

                                //   Add Player 4
                                if (!player4.equals("")) {

                                    playerBfr.append("<BR>");             // add seperator

                                    player = buildPlayerMobile(hideGuestCC, hideN, g4, hideG, player4, guestLabel, memberLabel, club);

                                    playerBfr.append(player);
                                }

                                if (fives != 0 && rest5.equals("")) {    // if 5-somes on this course

                                    //   Add Player 5
                                    if (!player5.equals("")) {

                                        playerBfr.append("<BR>");             // add seperator

                                        player = buildPlayerMobile(hideGuestCC, hideN, g5, hideG, player5, guestLabel, memberLabel, club);

                                        playerBfr.append(player);
                                    }
                                }

                                out.println(playerBfr.toString());     // add the players
                            }

                            out.println("</td>");


                        }         // end of IF Mobile User


                        out.println("</tr>");       // end of this row

                    }  // end of IF blocker or Lottery test

                }  // end of while loop1

                pstmt.close();
                if (mobile == 0) {
                    out.println("</tbody>");
                }
                out.println("</table>");                   // end of tee sheet table

                if (mobile > 0) {

                    out.println("</div>");
                    out.println("<div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)
                }


                //
                //  End of HTML page
                //
                Common_skin.outputPageEnd(club, activity_id, out, req);

                out.close();
                if (Gzip == true) {
                    resp.setContentLength(buf.size());                 // set output length
                    resp.getOutputStream().write(buf.toByteArray());
                }
                
                
                
                
                

                // ***********   New skin ends here   ******************************** 
                
            } else {  // Old skin starts here
               /*
               

                //  6/13/13 - Commented out old skin code to avoid confusion, since it should no longer be needed - BSK
                  
                //
                //  Check for Mobile user
                //
                if (mobile == 0) {

                    //
                    //  Non-Mobile Browser - Build the HTML page to prompt user for a specific time slot
                    //
                    out.println(SystemUtils.HeadTitle2("Member Tee Sheet"));

                    //
                    // if 365 day calendars requested by club
                    //
                    //if (use365) {

                    // include files for dynamic calendars
                    out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/calv40-styles.css\">");
                    out.println("<script type=\"text/javascript\" src=\"/" + rev + "/calv40-scripts.js\"></script>");
                    //}
                    out.println("</HEAD>");
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");

                    SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\"></font><center>");

                    out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)

                    out.println("<table border=\"0\" align=\"center\" width=\"95%\">");         // table for main page

                    out.println("<tr><td valign=\"top\" align=\"center\">");
                }

                //**********************************************************
                //  Build calendar for selecting a new day
                //**********************************************************
    
                int count = 0;                    // init day counter
                int col = 0;                       // init column counter
                int d = 0;                        // 'days in advance' value for current day of week
                 ///
                 

                //
                //  Check for Mobile user
                //
                if (mobile == 0) {        // if Standard Device (NOT Mobile user)

                    //
                    //  If multiple courses, then add a drop-down box for course names
                    //
                    if (multi != 0) {           // if multiple courses supported for this club

                        if (!club.equals("merion")) {      // Merion members can only view East Course - do not allow them to switch

                            //
                            //  use 2 forms so you can switch by clicking either a course or a date
                            //
                            out.println("<form action=\"Member_sheet\" method=\"post\" name=\"cform\" target=\"bot\">");
                            out.println("<input type=\"hidden\" name=\"i" + index2 + "\" value=\"\">");   // use current date

                            out.println("<div id=\"awmobject1\">");        // allow menus to show over this box

                            cMax = course.size();            // number of courses

                            out.println("<b>Course:</b>&nbsp;&nbsp;");
                            out.println("<select size=\"1\" name=\"course\" onChange=\"document.cform.submit()\">");

                            for (i = 0; i < cMax; i++) {

                                courseName = course.get(i);      // get course name from array

                                boolean skipCourse = false;

                                if (club.equals("olyclub") && courseName.equals("Cliffs")) {
                                    skipCourse = true;
                                }

                                if (skipCourse == false) {

                                    if (courseName.equals(courseName1)) {
                                        out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                                    } else {
                                        out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                                    }
                                }
                            }
                            out.println("</select></div>");
                            out.println("</form>");
                        }
                    }

                    //
                    //  build one large table to hold one table for each month required
                    //
                    out.println("<table border=\"0\" cellpadding=\"5\"><tr><td align=\"center\" valign=\"top\"><font size=\"2\">");

                    out.println("<font size=\"2\">");

                    // this is the form that gets submitted when the user selects a day from the calendar
                    out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\" name=\"frmLoadDay\">");
                    out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
                    out.println("<input type=\"hidden\" name=\"thisDate\" value=\"" + thisDate2 + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");

                    out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
                    out.println("</form>");

                    // table for calendars built by js
                    out.println("<table align=center border=0 height=150>\n<tr valign=top>\n<td>");    // was 190 !!!

                    out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

                    out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");

                    out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

                    out.println("</td>\n<tr>\n</table>");

                    //Calendar cal_date = new GregorianCalendar(); //Calendar.getInstance();
                    //int cal_year = cal_date.get(Calendar.YEAR);
                    //int cal_month = cal_date.get(Calendar.MONTH) + 1;
                    //int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

                    boolean useTeeSheetDates = false;
                    //if (cal_month != month && cal_year != year) {
                    if (month2 != month && year2 != year) {
                        useTeeSheetDates = true;
                    }

                    out.println("<script type=\"text/javascript\">");

                    out.println("var g_cal_bg_color = '#F5F5DC';");
                    out.println("var g_cal_header_color = '#8B8970';");
                    out.println("var g_cal_border_color = '#8B8970';");

                    out.println("var g_cal_count = 2;"); // number of calendars on this page
                    out.println("var g_cal_year = new Array(g_cal_count - 1);");
                    out.println("var g_cal_month = new Array(g_cal_count - 1);");
                    out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
                    out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
                    out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
                    out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
                    out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
                    out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

                    // set calendar date parts
                    out.println("g_cal_month[0] = " + month2 + ";");
                    out.println("g_cal_year[0] = " + year2 + ";");
                    out.println("g_cal_beginning_month[0] = " + month2 + ";");
                    out.println("g_cal_beginning_year[0] = " + year2 + ";");
                    out.println("g_cal_beginning_day[0] = " + day2 + ";");
                    out.println("g_cal_ending_month[0] = " + month2 + ";");
                    out.println("g_cal_ending_day[0] = 31;");
                    out.println("g_cal_ending_year[0] = " + year2 + ";");

                    cal2.add(Calendar.MONTH, 1); // add a month
                    int cal_month = cal2.get(Calendar.MONTH) + 1; // month is zero based
                    int cal_year = cal2.get(Calendar.YEAR);

                    out.println("g_cal_beginning_month[1] = " + cal_month + ";");
                    out.println("g_cal_beginning_year[1] = " + cal_year + ";");
                    out.println("g_cal_beginning_day[1] = 0;");

                    Calendar cal_date = new GregorianCalendar(year2, month2 - 1, day2);

                    cal_date.add(Calendar.DAY_OF_MONTH, max);
                    cal_year = cal_date.get(Calendar.YEAR);
                    cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
                    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
                    out.println("g_cal_ending_month[1] = " + cal_month + ";");
                    out.println("g_cal_ending_day[1] = " + cal_day + ";");
                    out.println("g_cal_ending_year[1] = " + cal_year + ";");

                    cal_date = new GregorianCalendar();
                    cal_date.add(Calendar.MONTH, 1);
                    cal_year = cal_date.get(Calendar.YEAR);
                    cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

                    out.println("g_cal_month[1] = " + ((useTeeSheetDates) ? month : cal_month) + ";");
                    out.println("g_cal_year[1] = " + ((useTeeSheetDates) ? year : cal_year) + ";");

                    out.print("var daysArray = new Array(");
                    int js_index = 0;
                    for (js_index = 0; js_index <= max; js_index++) {
                        out.print(daysArray.days[js_index]);
                        if (js_index != max) {
                            out.print(",");
                        }
                    }
                    out.println(");");

                    out.println("var max = " + max + ";");

                    out.println("</script>");

                    out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
                    out.println("<script type=\"text/javascript\">\ndoCalendar('1');\n</script>");


                    //
                    // end of calendar row
                    //
                    out.println("</td></tr></table>");
                    out.println("</form>");

                    //**********************************************************
                    //  Continue with instructions and tee sheet
                    //**********************************************************

                    // check max allowed days in advance for members
                    if (index2 <= days || lstateA[0] == 2
                            || club.equals("oakmont") || mccGuestDay == true || club.equals("invernessclub")) {

                        out.println("<table cols=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"680\">");
                        out.println("<tr><td align=\"left\"><font color=\"#FFFFFF\" size=\"2\">");

                        out.println("<b>Instructions:</b>  To select a tee time, just click on the button containing the time (1st column). ");
                        out.println(" Special Events and Restrictions, if any, are colored (see legend below). ");
                        out.println(" To display a different day's tee sheet, select the date from the calendar above.");

                        if (club.equals("oakmont") && oakshotgun == false && (day_name.equals("Wednesday") || day_name.equals("Friday"))) {

                            out.println("<br>Times when Multiple Guests are allowed are indicated by the green time button.");

                        } else if (club.equals("milwaukee") && mccGuestDay == true) {

                            out.println("<br>Times when Multiple Guests are allowed are indicated by the green time button.");

                        } else if (club.equals("bearpath")) {

                            out.println("<br>Member-only times are indicated by the green time button (if any on this date).");
                        }

                        if (parm.constimesm > 1) {        // if consecutive tee times supported
                            out.println("<br>To make multiple consecutive tee times, select the number of tee times next to the ");
                            out.println("earliest time desired.  Then select that time.  The following time(s) must be available.");
                        }

                        if (club.equals("invernessclub")) {

                            out.println("<br><b>Note: <i>Tee times are only allowed on this day if a guest is included.</i></b>");

                        } else if (club.equals("minikahda") && miniGuestDay == true) {

                            out.println("<br><b>Note: <i>Only Guest Times (2 per hour) are allowed on this day. There must be 3 guests included.</i></b>");

                        } else if (club.equals("canterburygc") && congGuestDay == true) {

                            out.println("<BR><b>NOTE:</b>&nbsp;&nbsp;This date is not yet available for members to make normal tee times.");
                            out.println("&nbsp;&nbsp;However, you may be allowed to make Guest times (if any on this date).");
                            out.println("&nbsp;&nbsp;Guest times are indicated by the green time button.");

                        } else if ((club.equals("estanciaclub")) && index >= 30) {

                            out.println("<br><br><b>Note: <i>Only Advance Guest Times may be booked more than 30 days in advance</i></b>");

                        } else if (club.equals("napervillecc") && index > 7 && index <= 30) {

                            out.println("<br><br><b>Note: <i>Only Advance Guest Times may be booked more than 7 days in advance</i></b>");
                        }

                    } else {

                        out.println("<table cols=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"600\">");
                        out.println("<tr><td align=\"left\"><font color=\"#FFFFFF\" size=\"2\">");
                        if (club.equals("congressional") && congGuestDay == true) {
                            out.println("<b>Note:</b>&nbsp;&nbsp;This date is not yet available for members to make normal tee times.");
                            out.println("&nbsp;&nbsp;However, you may be allowed to make Guest times (if any on this date).");
                            out.println("&nbsp;&nbsp;Guest times are indicated by the green time button.");
                        } else if (!viewable) {        // If tee sheet viewing and booking are blocked at this time
                            out.println("<b>Note:</b>&nbsp;&nbsp;This date is not yet available for members to make tee times or view the tee sheet.");
                        } else {
                            out.println("<b>Note:</b>&nbsp;&nbsp;This date is not yet available for members to make tee times.");
                            out.println(" You are allowed to view this sheet for planning purposes only.");
                        }
                    }
                    out.println("</font></td></tr></table>");

                    out.println("<font size=\"4\">");
                    out.println("<p>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");

                    if (!courseName1.equals("")) {

                        if (club.equals("congressional")) {
                            // The Open Course will be Blue Course on Even Days and Gold Course on Odd Days
                            // The Club Course will be Blue Course on Odd Days and the Gold Course on Even days
                            out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + congressionalCustom.getFullCourseName(date, day, courseName1) + "</b>");
                        } else {
                            out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + courseName1 + "</b>");
                        }

                    } // end if courseName1 empty

                    out.println("</p></font>");

                    //
                    // Only display tee sheet if 1) days in advance = viewable days in advance and all days have the same days in advance 2) member viewing furthest day in advance before viewable time
                    //
                    if (viewable == false) {

                        //
                        // Print out a message telling the member this day is not available for booking/viewing
                        //
                        out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"50%\">");
                        out.println("<tr><td align=\"center\">");
                        out.println("<h3><b>**NOTICE**</b></h3>");
                        out.println("This day is not yet available for booking or viewing of tee times."
                                + "<br><br>Please use the calendars above to select a different day's tee sheet or to reload this day's tee sheet.");
                        out.println("</tr></td>");
                        out.println("</table>");
                        out.println("</td></tr>");
                        out.println("</table>");                   // end of main page table
                        out.println("</center></body></html>");
                        out.close();
                        if (Gzip == true) {
                            resp.setContentLength(buf.size());                 // set output length
                            resp.getOutputStream().write(buf.toByteArray());
                        }
                        return;
                    }

                    out.println("<font size=\"2\">");

                    //
                    //  Display a note if members are not allowed to access tee times today
                    //
                    if (restrictAll == true && !club.equals("congressional") && (!club.equals("coloradogc") || !day_name.equals("Monday"))) {

                        if (club.equals("jonathanslanding")) {
                            out.println("<button type=\"button\" style=\"background:#F5F5DC\">Please contact the Starter's Booth for tee times on this day (772-231-1188).</button><br>");
                        } else if (club.equals("gaaphoenix")) {
                            out.println("<button type=\"button\" style=\"background:#F5F5DC\">Please contact the GAA Phoenix Campus for a tee time for today after 7:30 AM (480) 857-1574.</button><br>");
                        } else {                       
                            out.println("<button type=\"button\" style=\"background:#F5F5DC\">Please contact the Golf Shop for tee times on this day.</button><br>");
                        }
                    }

                    if (club.equals("minikahda") && miniGuestDay == true) {

                        out.println("<button type=\"button\" style=\"background:#F5F5DC\">This far in advance only three-guest groups can be made per hour.</button><br>");

                        parm.constimesm = 1;        // do not allow consecutive tee times for this day
                    }


                    if (!eventA[0].equals("")) {

                        out.println("<b>Tee Sheet Legend</b> (click on Event button to view info)");

                    } else {

                        out.println("<b>Tee Sheet Legend</b>");
                    }

                    out.println("</font><font size=\"1\"><br>");

                    if (!eventA[0].equals("")) {       // if any events to display

                        for (i = 0; i < count_event; i++) {     // check for events

                            if (!eventA[i].equals("")) {

                                out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('Member_sheet?event=" + eventA[i] + "', 'newwindow', 'height=700, width=900, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                                out.println("<button type=\"button\" style=\"background:" + ecolorA[i] + "\">" + eventA[i] + "</button></a>");
                                out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                            }
                        }
                    }

                    if (!restA[0].equals("")) {       // if any restrictions to display

                        for (i = 0; i < count_rest; i++) {     // check for restrictions

                            if (!restA[i].equals("")) {

                                out.println("<button type=\"button\" style=\"background:" + rcolorA[i] + "\">" + restA[i] + "</button>");
                                out.println("&nbsp;&nbsp;&nbsp;");
                            }
                        }
                    }

                    //
                    //  Custom check for Cordillera Canned Restrictions - add to legend
                    //
              /*
                    if (club.equals( "cordillera" ) && dateShort > 413 && dateShort < 1101 && index < 4) {  // if in season and within 3 days in advance
                    
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("<button type=\"button\" style=\"background:" +cordRestColor+ "\">Starter Times</button>");
                    }
                     ///


                    boolean lottDisp = false;

                    if (!lottA[0].equals("")) {       // if any lotteries to display

                        for (i = 0; i < count_lott; i++) {     // check for restrictions

                            if (!lottA[i].equals("") && lstateA[i] < 5) {

                                out.println("<button type=\"button\" style=\"background:" + lcolorA[i] + "\">" + lottA[i] + "</button>");
                                out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                                lottDisp = true;
                            }
                        }
                    }

                    i = 0;

                    // if (!eventA[0].equals( "" ) || !restA[0].equals( "" ) || lottDisp == true || (club.equals( "cordillera" ) && dateShort > 413 && dateShort < 1101 && index < 4)) {
                    if (!eventA[0].equals("") || !restA[0].equals("") || lottDisp == true) {

                        out.println("<br>");
                    }


                    if (skip_waitlist == false) {    // if ok to show wait list buttons

                        out.println("<center>");

                        for (int z = 0; z < parmWaitLists.count; z++) {

                            WLbtn_displayed[z] = false;

                            if ((index > 0 || (index == 0 && parmWaitLists.etime[z] > curr_time))) {

                                if (club.equals("hudsonnatl")) {

                                    out.println("<button type=\"button\" style=\"background:" + parmWaitLists.color[z] + "\">" + parmWaitLists.name[z] + "</button>");
                                } else {
                                    out.println("<button type=\"button\" style=\"background:" + parmWaitLists.color[z] + "\" "
                                            + "onclick=\"top.location.href='Member_waitlist?waitListId=" + parmWaitLists.id[z] + "&date=" + date + "&day=" + day_name + "&index=" + num + "&returnCourse=" + courseName1 + "&course=" + parmWaitLists.courseName[z] + "'\">" + parmWaitLists.name[z] + "</button>");
                                }

                                out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

                            }
                        }
                        out.println("</center>");
                    }


                    //
                    //  If Old Oaks CC and this is a 'Lottery Only" Day, then only display the lottery button.
                    //
                    if (lotteryOnly == true) {

                        //
                        //  This is Old Oaks CC and there is only a lottery for this date.
                        //  Just display a lottery request button.
                        //
                        out.println("<br><br><br>");
                        out.println("<form action=\"Member_mlottery\" method=\"get\">");
                        out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstateA[0] + "\">");
                        out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottA[0] + "\">");
                        out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slotsA[0] + "\">");
                        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                        out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");  // they always restrict to 4

                        out.println("<input type=\"submit\" value=\"Note: Click Here to Request a Tee Time\" style=\"background:" + lcolorA[0] + "\">");
                        out.println("</td></tr>");
                        out.println("</table>");                   // end of main page table
                        out.println("</center></body></html>");
                        out.close();
                        if (Gzip == true) {
                            resp.setContentLength(buf.size());                 // set output length
                            resp.getOutputStream().write(buf.toByteArray());
                        }
                        return;
                    }

                    //  Do not display abbreviations portion for tee sheet legend for Desert Forest GC
                    if (club.equals("desertforestgolfclub") || club.equals("sawgrass")) {

                        out.println("<br>");

                    } else {

                        out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event<br>");

                        out.println("<b>C/W:</b>&nbsp;&nbsp;&nbsp;&nbsp;");

                        if (club.equals("cordillera")) {     // only show the following modes for Cordillera

                            out.println("GC = Golf Car&nbsp;&nbsp;&nbsp;W = Walk&nbsp;&nbsp;&nbsp;");

                        } else {

                            for (int ic = 0; ic < parmc.tmode_limit; ic++) {

                                if (!parmc.tmodea[ic].equals("") && parmc.tOpt[ic] == 0) {                  // if tmode exists and NOT pro-only

                                    if (club.equals("peninsula") && parmc.tmodea[ic].equals("WLK")) {

                                        out.println("WLK = <b>Walking Permitted Daily After 3:00 PM</b>&nbsp;&nbsp;&nbsp;");

                                    } else {

                                        out.println(parmc.tmodea[ic] + " = " + parmc.tmode[ic] + "&nbsp;&nbsp;&nbsp;");
                                    }
                                }
                            }
                        }       // end of IF Cordillera

                        out.println("(__9 = 9 holes)");
                    }

                    //
                    //**********************************************
                    //   Check for Member Notice from Pro
                    //**********************************************
                    //
                    String memNoticeMsg = verifySlot.checkMemNotice(date, 0, 0, courseName1, day_name, "teesheet", false, con);

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
                                    "SELECT mon, tue, wed, thu, fri, sat, sun, message, bgColor "
                                    + "FROM mem_notice "
                                    + "WHERE sdate <= ? AND edate >= ? AND "
                                    + "(courseName = ? OR courseName = ?) AND teesheet=1");

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


                                if ((notice_mon == 1 && day_name.equals("Monday")) || (notice_tue == 1 && day_name.equals("Tuesday")) || (notice_wed == 1 && day_name.equals("Wednesday"))
                                        || (notice_thu == 1 && day_name.equals("Thursday")) || (notice_fri == 1 && day_name.equals("Friday")) || (notice_sat == 1 && day_name.equals("Saturday"))
                                        || (notice_sun == 1 && day_name.equals("Sunday"))) {

                                    out.println("<tr>");
                                    if (!notice_bgColor.equals("")) {
                                        out.println("<td width=\"700\" bgColor=\"" + notice_bgColor + "\" align=\"center\">");
                                    } else {
                                        out.println("<td width=\"700\" align=\"center\">");
                                    }
                                    out.println("<font size=\"2\">" + notice_msg + "</font></td></tr>");
                                }

                            }  // end WHILE loop

                            out.println("</table></td></tr></table><br><br>");

                            notice_pstmt.close();

                        } catch (Exception e1) {

                            out.println(SystemUtils.HeadTitle("DB Error"));
                            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                            out.println("<BR><BR><H2>Database Access Error</H2>");
                            out.println("<BR><BR>Unable to access the Database. (Notice 2)");
                            out.println("<BR>Please try again later.");
                            out.println("<BR><BR>If problem persists, contact your club manager.");
                            out.println("<BR><BR>" + e1.getMessage());
                            out.println("<BR><BR>");
                            out.println("<a href=\"Member_announce\">Return</a>");
                            out.println("</BODY></HTML>");
                            out.close();
                        }

                    } // end if member notice

                    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"95%\">");    // tee sheet table
                    out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
                    out.println("<font color=\"#FFFFFF\" size=\"2\">");
                    out.println("<u><b>Time</b></u>");
                    out.println("</font></td>");

                    if (parm.constimesm > 1) {      // if Consecutive Tee Times allowed

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<u><b>#</b></u>");
                        out.println("</font></td>");
                    }

                    //               if (courseName1.equals( "-ALL-" )) {
                    if (multi != 0) {        // case 1509

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
                    out.println("<u><b>Player 1</b></u> ");
                    if (disp_hndcp == false) {
                        out.println("&nbsp;");
                    } else {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                    }
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font color=\"#FFFFFF\" size=\"1\">");
                    out.println("<u><b>C/W</b></u>");
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font color=\"#FFFFFF\" size=\"2\">");
                    out.println("<u><b>Player 2</b></u> ");
                    if (disp_hndcp == false) {
                        out.println("&nbsp;");
                    } else {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                    }
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font color=\"#FFFFFF\" size=\"1\">");
                    out.println("<u><b>C/W</b></u>");
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font color=\"#FFFFFF\" size=\"2\">");
                    out.println("<u><b>Player 3</b></u> ");
                    if (disp_hndcp == false) {
                        out.println("&nbsp;");
                    } else {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                    }
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font color=\"#FFFFFF\" size=\"1\">");
                    out.println("<u><b>C/W</b></u>");
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font color=\"#FFFFFF\" size=\"2\">");
                    out.println("<u><b>Player 4</b></u> ");
                    if (disp_hndcp == false) {
                        out.println("&nbsp;");
                    } else {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                    }
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font color=\"#FFFFFF\" size=\"1\">");
                    out.println("<u><b>C/W</b></u>");
                    out.println("</font></td>");

                    if (fivesALL != 0) {

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<u><b>Player 5</b></u> ");
                        if (disp_hndcp == false) {
                            out.println("&nbsp;");
                        } else {
                            out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                        }
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"1\">");
                        out.println("<u><b>C/W</b></u>");
                        out.println("</font></td>");
                    }
                    out.println("</tr>");


                } else {

                    //
                    //***************************************************************
                    //  MOBILE USER - start the output page for a mobile device
                    //***************************************************************
                    //
                    out.println(SystemUtils.HeadTitleMobile("ForeTees Member Tee Sheet"));
                    out.println(SystemUtils.BannerMobile());

                    //
                    // Only display tee sheet if 1) days in advance = viewable days in advance and all days have the same days in advance 2) member viewing furthest day in advance before viewable time
                    //
                    if (viewable == false) {

                        //
                        // Print out a message telling the member this day is not available for booking/viewing
                        //
                        out.println("<div class=\"headertext\">**NOTICE**</div>");
                        out.println("<div class=\"content\">");

                        out.println("<div>This day is not yet available for booking or viewing of tee times.<br><br>");
                        out.println("Please use the calendars above to select a different day's tee sheet or to reload this day's tee sheet.</div>");

                        out.println("<form action=\"Member_select\" method=\"get\">");
                        out.println("<input type=submit value=\"Return\" name=\"Return\">");

                        out.println("</div></body></html>");
                        out.close();
                        if (Gzip == true) {
                            resp.setContentLength(buf.size());                 // set output length
                            resp.getOutputStream().write(buf.toByteArray());
                        }
                        return;
                    }

                    //
                    //  Ok for user to display this tee sheet
                    //
                    out.println("<div class=\"headertext\">Tee Sheet</div>");
                    out.println("<div class=\"smheadertext\"> <a href=\"Member_select\">Change Date</a> </div>");
                    out.println("<div class=\"smheadertext\"><strong>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year);   // Date
                    out.println("<br /> " + courseName1);                                                          // Course
                    out.println("</strong></div>");

                    // allow user to switch the display option
                    out.println("<form action=\"Member_sheet\" method=\"post\" name=\"mobileform\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");

                    out.println("<div class=\"smheadertext\">Tee Sheet - Display:");
                    out.println("<select size=\"1\" name=\"displayOpt\" onChange=\"document.mobileform.submit()\">");
                    if (displayOpt.equals("Morning")) {
                        out.println("<option selected value=\"Morning\">Morning</option>");
                    } else {
                        out.println("<option value=\"Morning\">Morning</option>");
                    }
                    if (displayOpt.equals("Mid-Day")) {
                        out.println("<option selected value=\"Mid-Day\">Mid-Day</option>");
                    } else {
                        out.println("<option value=\"Mid-Day\">Mid-Day</option>");
                    }
                    if (displayOpt.equals("Afternoon")) {
                        out.println("<option selected value=\"Afternoon\">Afternoon</option>");
                    } else {
                        out.println("<option value=\"Afternoon\">Afternoon</option>");
                    }
                    if (displayOpt.equals("All")) {
                        out.println("<option selected value=\"All\">Entire Day</option>");
                    } else {
                        out.println("<option value=\"All\">Entire Day</option>");
                    }
                    if (displayOpt.equals("Available")) {
                        out.println("<option selected value=\"Available\">Open Times Only</option>");
                    } else {
                        out.println("<option value=\"Available\">Open Times Only</option>");
                    }
                    out.println("</select>");
                    out.println("</div></form>");


                    //
                    // Build start of tee sheet table for Mobile users
                    //
                    out.println("<div class=\"content\">");
                    out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"times\">");
                    out.println("<tr class=\"tableheader\">");
                    out.println("<td><strong>Time</strong></td>");
                    out.println("<td><strong>F/B</strong></td>");
                    out.println("<td><strong>Players</strong></td>");
                    out.println("</tr>");

                }    // end of IF Mobile User


                //
                //  Get the tee sheet for this date and course
                //
                String stringTee = "";

                if (courseName1.equals("-ALL-")) {

                    if (club.equals("edisonclub") || club.equals("loscoyotes") || club.equals("sawgrass")) {

                        // sort courses by the order in which they where entered
                        stringTee = ""
                                + "SELECT t.* "
                                + "FROM teecurr2 t, clubparm2 c "
                                + "WHERE t.date = ? AND t.courseName = c.courseName "
                                + "ORDER BY t.time, c.clubparm_id, t.fb;";
                    } else {

                        stringTee = "SELECT * "
                                + "FROM teecurr2 WHERE date = ? ORDER BY time, courseName, fb";
                    }

                } else {
                    stringTee = "SELECT * "
                            + "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb";
                }
                PreparedStatement pstmt = con.prepareStatement(stringTee);

                pstmt.clearParameters();        // clear the parms
                pstmt.setLong(1, date);         // put the parm in pstmt

                if (!courseName1.equals("-ALL-")) {
                    pstmt.setString(2, courseName1);
                }

                rs = pstmt.executeQuery();      // execute the prepared stmt

                loop1:
                while (rs.next()) {

                    hr = rs.getInt("hr");
                    min = rs.getInt("min");
                    tee_time = rs.getInt("time");
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
                    fb = rs.getShort("fb");
                    player5 = rs.getString("player5");
                    user5 = rs.getString("username5");
                    p5cw = rs.getString("p5cw");
                    hndcp5 = rs.getFloat("hndcp5");
                    lottery = rs.getString("lottery");
                    courseNameT = rs.getString("courseName");
                    blocker = rs.getString("blocker");
                    rest5 = rs.getString("rest5");
                    bgcolor5 = rs.getString("rest5_color");
                    lottery_color = rs.getString("lottery_color");
                    userg1 = rs.getString("userg1");
                    userg2 = rs.getString("userg2");
                    userg3 = rs.getString("userg3");
                    userg4 = rs.getString("userg4");
                    userg5 = rs.getString("userg5");
                    orig_by = rs.getString("orig_by");
                    p91 = rs.getInt("p91");
                    p92 = rs.getInt("p92");
                    p93 = rs.getInt("p93");
                    p94 = rs.getInt("p94");
                    p95 = rs.getInt("p95");
                    hole = rs.getString("hole");
                    custom_disp1 = rs.getString("custom_disp1");
                    custom_disp2 = rs.getString("custom_disp2");
                    custom_disp3 = rs.getString("custom_disp3");
                    custom_disp4 = rs.getString("custom_disp4");
                    custom_disp5 = rs.getString("custom_disp5");
                    orig1 = rs.getString("orig1");
                    orig2 = rs.getString("orig2");
                    orig3 = rs.getString("orig3");
                    orig4 = rs.getString("orig4");
                    orig5 = rs.getString("orig5");

                    skipconstimes = false;      // init flag to skip consecutive time option


                    //
                    //  If Mobile user - check if user requested we limit the tee times displayed
                    //
                    if (mobile > 0 && !displayOpt.equals("All")) {

                        if (displayOpt.equals("Morning")) {          // only show AM times?

                            if (tee_time > 1159) {

                                blocker = "AAPL";    //  Do not show this tee time
                            }

                        } else if (displayOpt.equals("Mid-Day")) {       // only show mid-day times (10:00 - 2:00)?

                            if (tee_time < 1000 || tee_time > 1400) {

                                blocker = "AAPL";    //  Do not show this tee time
                            }

                        } else if (displayOpt.equals("Afternoon")) {       // only show PM times?

                            if (tee_time < 1200) {

                                blocker = "AAPL";    //  Do not show this tee time
                            }

                        } else if (displayOpt.equals("Available")) {       // only show available times?

                            if (!player1.equals("")) {

                                blocker = "AAPL";    //  Do not show this tee time
                            }
                        }
                    }



                    //
                    //  If course=ALL requested, then set 'fives' option according to this course
                    //
                    if (courseName1.equals("-ALL-")) {
                        i = 0;
                        cMax = course.size();     // number of courses
                        loopall:
                        while (i < cMax) {
                            if (courseNameT.equals(course.get(i))) {
                                fives = fivesA.get(i);           // get the 5-some option for this course
                                break loopall;                   // exit loop
                            }
                            i++;
                        }
                    }

                    //
                    //  Check for guests in this tee time
                    //
                    g1 = 0;          // init guest indicators
                    g2 = 0;
                    g3 = 0;
                    g4 = 0;
                    g5 = 0;

                    //
                    //  Check if any player names are guest names
                    //
                    if (!player1.equals("")) {

                        i = 0;
                        ploop1:
                        while (i < parm.MAX_Guests) {
                            if (player1.startsWith(parm.guest[i])) {

                                g1 = 1;       // indicate player1 is a guest name
                                break ploop1;
                            }
                            i++;
                        }
                    }
                    if (!player2.equals("")) {

                        i = 0;
                        ploop2:
                        while (i < parm.MAX_Guests) {
                            if (player2.startsWith(parm.guest[i])) {

                                g2 = 1;       // indicate player2 is a guest name
                                break ploop2;
                            }
                            i++;
                        }
                    }
                    if (!player3.equals("")) {

                        i = 0;
                        ploop3:
                        while (i < parm.MAX_Guests) {
                            if (player3.startsWith(parm.guest[i])) {

                                g3 = 1;       // indicate player3 is a guest name
                                break ploop3;
                            }
                            i++;
                        }
                    }
                    if (!player4.equals("")) {

                        i = 0;
                        ploop4:
                        while (i < parm.MAX_Guests) {
                            if (player4.startsWith(parm.guest[i])) {

                                g4 = 1;       // indicate player4 is a guest name
                                break ploop4;
                            }
                            i++;
                        }
                    }
                    if (!player5.equals("")) {

                        i = 0;
                        ploop5:
                        while (i < parm.MAX_Guests) {
                            if (player5.startsWith(parm.guest[i])) {

                                g5 = 1;       // indicate player5 is a guest name
                                break ploop5;
                            }
                            i++;
                        }
                    }

                    i = 0;   // init


                    //*************************************************************
                    //  Check for 2-some only times
                    //*************************************************************
                    //
                    twoSomeOnly = false;             // init the flag

                    if (club.equals("piedmont") && tee_time < 1000
                            && (day_name.equals("Saturday") || day_name.equals("Sunday") || date == Hdate1 || date == Hdate3)) {

                        piedmontStatus = verifySlot.checkPiedmont(date, tee_time, day_name);     // check if special time

                        if (piedmontStatus == 3) {

                            twoSomeOnly = true;             // Only allow 2-somes for this tee time
                        }

                    } else {  // If we're not in the special times, reset piedmontStatus!!
                        piedmontStatus = 0;
                    }

                    // If Indian Hills CC and the 'Hitting Room', always set to two some times
                    if (club.equals("indianhillscc") && courseNameT.equalsIgnoreCase("Hitting Room")) {

                        twoSomeOnly = true;
                    }

                    //
                    //  if Westchester CC, check if tee time is for 2-somes only on the South course
                    //
                    if (club.equals("westchester") && courseNameT.equalsIgnoreCase("south")) {

                        twoSomeOnly = verifySlot.checkWestchester(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  if The CC, check if tee time is for 2-somes only on the Main Course
                    //
                    if (club.equals("tcclub") && courseNameT.equals("Main Course")) {

                        twoSomeOnly = verifyCustom.checkTheCC(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  if New Canaan, check if tee time is for 2-somes only
                    //
                    if (club.equals("newcanaan")) {

                        twoSomeOnly = verifyCustom.checkNewCan(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  if Greenwich CC, check if tee time is for 2-somes only
                    //
                    if (club.equals("greenwich")) {

                        twoSomeOnly = verifyCustom.checkGreenwich(date, tee_time);     // check if special time
                    }

                    //
                    //  if The Congressional, check if tee time is for 2-somes only on the Club Course
                    //
                    if (club.equals("congressional") && courseNameT.equals("Gold Course") && day_name.equals("Tuesday")) {

                        twoSomeOnly = congressionalCustom.check2Somes(date, tee_time);     // check if special time
                    }

                    //
                    //  if Woodway CC, check if tee time is for 2-somes only
                    //
                    if (club.equals("woodway")) {

                        twoSomeOnly = verifySlot.checkWoodway(date, tee_time, fb);     // check if special time
                    }
                    
                    //  if Huntingdon Valley CC, check if tee time is for 2-somes only
                    if (club.equals("huntingdonvalleycc")) {
                        
                        twoSomeOnly = verifySlot.checkHuntingdonValley(date, tee_time, courseNameT, fb);     // check if special time
                    }

                    //
                    //  if Hudson National, check if tee time is for 2-somes only
                    //
                    if (club.equals("hudsonnatl")) {

                        twoSomeOnly = verifySlot.checkHudson(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  if The Stanwich Club, check if tee time is for 2-somes only
                    //
                    if (club.equals("stanwichclub")) {

                        twoSomeOnly = verifySlot.checkStanwich(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  New Canaan, check if tee time is for 2-somes only
                    //
                    if (club.equals("newcanaan")) {

                        twoSomeOnly = verifySlot.checkNewCanaan(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  Apawamis, check if tee time is for 2-somes only
                    //
                    if (club.equals("apawamis")) {

                        twoSomeOnly = verifySlot.checkApawamis(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  Wee Burn, check if tee time is for 2-somes only
                    //
                    if (club.equals("weeburn")) {

                        twoSomeOnly = verifySlot.checkWeeburn(date, tee_time, day_name);     // check if special time
                    }

                    //
                    //  Claremont, check if tee time is for 2-somes only  (Case #1281)
                    //
                    if (club.equals("claremontcc")) {

                        if (day_name.equals("Sunday") && tee_time >= 1030 && tee_time <= 1115) {
                            twoSomeOnly = true;
                        }
                    }

                    //
                    //  Misquamicut, check if tee time is for 2-somes only (case #1996)
                    //
                    if (club.equals("misquamicut")) {

                        twoSomeOnly = verifyCustom.checkMisquamicut2someTimes(date, tee_time, day_name);
                    }

                    //
                    //  Mayfield Sand Ridge - check to see if tee time is for 2-somes only
                    //
                    if (club.equals("mayfieldsr") && courseNameT.equalsIgnoreCase("sand ridge")) {

                        twoSomeOnly = verifyCustom.checkMayfieldSR(date, tee_time, day_name);       // check if special time
                    }

                    //
                    //  Longu Vue Club - check to see if tee time is for 2-somes only
                    //
                    if (club.equals("longuevueclub")) {

                        twoSomeOnly = verifyCustom.check2LongueVue(date, tee_time, day_name);       // check if special time
                    }

                    //
                    //  Desert Forest - check to see if tee time is for 2-somes only
                    //
                    if (club.equals("desertforestgolfclub")) {

                        twoSomeOnly = verifyCustom.checkDesertForest(date, tee_time, fb, day_name);       // check if special time
                    }

                    //
                    //  Cherry Valley CC - check to see if tee time is for 2-somes only
                    //
                    if (club.equals("cherryvalleycc")) {

                        twoSomeOnly = verifyCustom.checkCherryValley2someTimes(date, tee_time, day_name);
                    }
                    
                    if (club.equals("echolakecc")) {
                        
                        twoSomeOnly = verifyCustom.checkEchoLake2someTimes(date, tee_time, day_name);
                    }


                    //***********************************************
                    //  3-some processing - customs
                    //***********************************************
                    //
                    threeSomeOnly = false;             // init the flag


                    if (club.equals("chartwellgcc") && day_name.equals("Thursday") && date > 20090331 && date < 20091102
                            && tee_time > 759 && tee_time < 1001) {

                        threeSomeOnly = true;
                    }

                    //
                    //  Meadow Club  (case 1761) - removed until further notice (1/12/2011)
                    //
              /*
                    if (club.equals( "meadowclub" )) {
                    
                    threeSomeOnly = verifyCustom.checkMeadowClub(date, tee_time, day_name);     // check if 3-some time
                    }
                     ///


                    //
                    //  Ramsey  (case 1816)
                    //
                    if (club.equals("ramseycountryclub")) {

                        threeSomeOnly = verifyCustom.checkRamsey3someTime(date, tee_time, day_name);     // check if 3-some time

                        if (threeSomeOnly == true) {

                            skipconstimes = true;      // set flag to skip consecutive time option
                        }
                    }

                    // Sonnenalp (case 1978) - check for 3-some times
                    if (club.equals("sonnenalp")) {

                        threeSomeOnly = verifyCustom.checkSonnenalp3someTimes(date, tee_time, day_name);
                    }

                    // Royal Montreal GC - Check for 3-some times
                    if (club.equals("rmgc")) {

                        threeSomeOnly = !verifyCustom.checkRMGC4Ball(courseNameT, date, day_name);
                    }

                    if (club.equals("minnetonkacc")) {

                        threeSomeOnly = verifyCustom.checkMinnetonka3someTimes(date, tee_time, day_name, fb);

                        if (threeSomeOnly == true) {

                            skipconstimes = true;      // set flag to skip consecutive time option
                        }
                    }


                    /*
                    //
                    //  Rivercrest Golf Club & Preserve (case 1492)
                    //
                    if (club.equals( "rivercrestgc" )) {
                    
                    threeSomeOnly = verifyCustom.checkRivercrest(date, tee_time, day_name);     // check if 3-some time
                    }
                     ///



                    //
                    //  Aliases - assign an alias to specified members (Congressional)
                    //
                    if (club.equals("congressional")) {

                        if (user1.equals("25191") || user1.equals("26084")) {   // if Robert Bedingfield or Chris Kubasik

                            player1 = "Member";
                        }
                        if (user2.equals("25191") || user2.equals("26084")) {

                            player2 = "Member";
                        }
                        if (user3.equals("25191") || user3.equals("26084")) {

                            player3 = "Member";
                        }
                        if (user4.equals("25191") || user4.equals("26084")) {

                            player4 = "Member";
                        }
                        if (user5.equals("25191") || user5.equals("26084")) {

                            player5 = "Member";
                        }
                    }


                    //
                    //  Cordillera custom to only display Walk or Cart modes of trans options
                    //
                    if (club.equals("cordillera")) {

                        if (!p1cw.equals("") && !p1cw.equalsIgnoreCase("W")) {

                            p1cw = "GC";       // All but Walk = Golf Car
                        }
                        if (!p2cw.equals("") && !p2cw.equalsIgnoreCase("W")) {

                            p2cw = "GC";
                        }
                        if (!p3cw.equals("") && !p3cw.equalsIgnoreCase("W")) {

                            p3cw = "GC";
                        }
                        if (!p4cw.equals("") && !p4cw.equalsIgnoreCase("W")) {

                            p4cw = "GC";
                        }
                        if (!p5cw.equals("") && !p5cw.equalsIgnoreCase("W")) {

                            p5cw = "GC";
                        }
                    }


                    //
                    //****************************************************************************************
                    //  Hide Names Feature - if club opts to hide the member names, then hide all names
                    //                       except for any group that this user is part of.
                    //****************************************************************************************
                    //
                    hideN = 0;           // default to 'do not hide member names'
                    hideG = 0;           // default to 'do not hide guest names' (this is for customs)
                    hideSubmit = 0;      // default to 'do not hide the submit button'

                    if (club.equals("merion") && user.equalsIgnoreCase("S1")) {  // if Merion and user is Steven Smith

                        hideNames = 0;      // do not hide names
                    }

                    //
                    //  Inverness Club - do NOT hide names on w/e's and holidays
                    //
                    if (club.equals("invernessclub")) {

                        if (day_name.equals("Saturday") || day_name.equals("Sunday") || date == Hdate1 || date == Hdate2b || date == Hdate3) {

                            hideNames = 0;      // do not hide names
                        }
                    }

                    if (club.equals("merion") && !user.equalsIgnoreCase("S1")) {  // if Merion and NOT Steven Smith

                        //
                        //  Merion - hide names whenever there is a guest in the tee time (and its not this member's time)
                        //
                        if (!userg1.equals("") || !userg2.equals("") || !userg3.equals("") || !userg4.equals("") || !userg5.equals("")) {

                            hideN = 1;        // hide names in this guest group

                            if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3)
                                    || user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

                                hideN = 0;     // do not hide this group
                            }
                        }

                    } else {

                        if (hideNames > 0) {

                            hideN = 1;        // hide names in this group

                            if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3)
                                    || user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

                                hideN = 0;                // do not hide this group

                            } else {    // user not part of this tee time

                                if (club.equals("foresthighlands") || club.equals("deserthighlands") || club.equals("blackstone") || club.equals("alisoviejo")) {

                                    //
                                    //  Forest or Desert Highlands - do not hide names unless the tee time is full (4-somes only!)
                                    //
                                    if (player1.equals("") || player2.equals("") || player3.equals("") || player4.equals("")) {

                                        hideN = 0;                // do not hide this group
                                    }

                                } else {

                                    //
                                    //  Check if any players exist in this tee time.  If so, then do not allow this member access
                                    //
                                    //    if (!user1.equals("") || !user2.equals("") || !user3.equals("") ||
                                    //        !user4.equals("") || !user5.equals("")) {

                                    if (!player1.equals("") || !player2.equals("") || !player3.equals("")
                                            || !player4.equals("") || !player5.equals("")) {

                                        hideSubmit = 1;                // do not allow access to tee time
                                    }
                                }
                            }
                        }
                    }


                    //
                    //  Mesa Verde Custom - hide guest names, but not member names
                    //
                    if (club.equals("mesaverdecc")) {

                        hideG = 1;        // hide guest names
                    }

                    if (club.equals("indianridgecc")) {

                        if (player1.startsWith("Comp") || player2.startsWith("Comp") || player3.startsWith("Comp") || player4.startsWith("Comp") || player5.startsWith("Comp")) {
                            hideG = 1;
                        } else {
                            hideG = 0;
                        }
                    }

                    //  Central Washington Chapter PGA Custom - populate ghin numbers
                    if (club.equals("cwcpga")) {

                        if (!user1.equals("")) {
                            ghin1 = Utilities.getHdcpNum(user1, con);
                        }
                        if (!user2.equals("")) {
                            ghin2 = Utilities.getHdcpNum(user2, con);
                        }
                        if (!user3.equals("")) {
                            ghin3 = Utilities.getHdcpNum(user3, con);
                        }
                        if (!user4.equals("")) {
                            ghin4 = Utilities.getHdcpNum(user4, con);
                        }
                        if (!user5.equals("")) {
                            ghin5 = Utilities.getHdcpNum(user5, con);
                        }
                    }

                    //
                    //  if not event, then check for lottery time (events override lotteries)
                    //
                    //  determine if we should skip this slot - display only one slot per lottery before its processed
                    //
                    lskip = 0;                      // init skip switch
                    ldays = 0;                      // init lottery days value
                    firstLott = 0;                  // init first lottery flag

                    if (blocker.equals("")) {     // check for lottery if tee time not blocked

                        if (event.equals("") && !lottery.equals("")) {

                            lottloop2:
                            for (i = 0; i < count_lott; i++) {     // check for matching lottery

                                if (lottery.equals(lottA[i])) {    // if match found

                                    ldays = sdaysA[i];                // save lottery's advance days

                                    if (lstateA[i] < 5) {             // if lottery has not been processed (times allotted)

                                        if (lskipA[i] != 0) {          // if we were here already

                                            lskip = 1;                 // skip this slot
                                        }

                                        lskipA[i] = 1;              // make sure its set now
                                        firstLott = 1;              // indicate 1st lottery time

                                    } else {

                                        lottery_color = "";         // already processed, do not use color
                                    }
                                    break lottloop2;
                                }
                            }
                            i = 0;

                        }          // end of IF lottery
                    }          // end of IF blocker

                    // get the course index for the current course (course for this tee time row)
              /*
                    loopci:
                    while (ci < cMax) {
                    if (courseNameT.equals( course.get [ci] )) {
                    break loopci;
                    }
                    ci++;
                    }
                     ///

                    ci = course.indexOf(courseNameT);     // get the index value of this course


                    int tmp_currTime = SystemUtils.getTime(con);

                    if (index == 0 && tmp_currTime > tee_time) {

                        // it's today and this tee time has past so no need check
                        wait_list_id = 0;

                    } else {

                        // see if this tee time is covered by an active wait list
                        wait_list_id = SystemUtils.isWaitListTime(date, tee_time, tmp_currTime, courseNameT, day_name, con);
                        //out.println("<!-- wait_list_id=" + wait_list_id + ", date=" + date + ", tee_time=" + tee_time + ", tmp_currTime=" + tmp_currTime + ", courseNameT=" + courseNameT + ", day_name=" + day_name + " -->");

                    }

                    // translate to boolean
                    waitlist = (wait_list_id != 0);

                    /*
                    if (waitlist) {
                    
                    if (wait_list_id == last_wait_list_id) {
                    
                    firstWait = 0;
                    
                    } else {
                    
                    firstWait = 1;
                    last_wait_list_id = wait_list_id;
                    
                    }
                    
                    } else {
                    
                    if (ci == last_ci) {
                    
                    last_wait_list_id = 0;
                    
                    }
                    
                    }
                     ///

                    if (waitlist) {

                        WL_index = 0; // reset
                        loopWL:
                        while (WL_index < parmWaitLists.count) {

                            //if (WLbtn_index[WL_index] == wait_list_id) break loopWL;
                            if (parmWaitLists.id[WL_index] == wait_list_id) {
                                break loopWL;
                            }
                            WL_index++;
                        }
                    }


                    // Custom for baltusrolgc - hide ANY non event tee time on ANY day that has ANY number of players in it
                    //boolean hide_baltusrol = (club.equals("baltusrolgc") && !player1.equals("")) ? true : false;
                    if (club.equals("baltusrolgc") && !player1.equals("") && blocker.equals("") && event.equals("")
                            && !user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3)
                            && !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5)) {

                        blocker = "AAPL";    //  Do not show this tee time if taken and this user is not part of it
                    }


                    //
                    //   Medinah Custom - hide the tee time if between 7:30 & 6:00 and user is not part of it (ALL courses - per Mike Scully - original)
                    //   Medinah Custom - hide the tee time if between 2:00 PM & 6:00 PM and user is not part of it (ALL courses - per Mike Scully 5/19/09)
                    //   Medinah Custom - DO NOT hide the tee times at all (ALL courses - per Mike Scully 6/02/09)
                    //
              /*
                    if ( club.equals( "medinahcc" ) && tee_time > 1359 && tee_time < 1801 ) {
                    
                    if (!player1.equals("") && blocker.equals("") && event.equals("") &&
                    !user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3) &&
                    !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5)) {
                    
                    blocker = "AAPL";    //  Do not show this tee time if taken and this user is not part of it
                    }
                    }
                    ///


                    //
                    //   TPC Custom - hide the tee time if at least 4 players and user is not part of it (ALL TPC Clubs)
                    //
                    if (club.startsWith("tpc") && !club.equals("tpcriversbend") && !club.equals("tpcwakefieldplantation")
                            && !orig_by.equalsIgnoreCase(user)) {    // do not block if originated by this user!!

                        if (!player4.equals("") && blocker.equals("") && event.equals("")
                                && !user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3)
                                && !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5)) {

                            if (!club.equals("tpctc")) {          // if NOT TPC TC

                                blocker = "AAPL";    //  Do not show this tee time if taken and this user is not part of it

                            } else {       // TPC TC - only block if guest included

                                if (g1 == 1 || g2 == 1 || g3 == 1 || g4 == 1 || g5 == 1) {   // if any guests

                                    blocker = "AAPL";    //  Do not show this tee time if taken and one or more guests included
                                }
                            }
                        }
                    }


                    //
                    //   Hiwan - hide the tee time if at least 4 players and user is not part of it
                    //
              /*
                    if ( club.equals( "hiwan" ) && !orig_by.equalsIgnoreCase( user ) ) {    // do not block if originated by this user!!
                    
                    if (!player4.equals("") && blocker.equals("") && event.equals("") &&
                    !user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3) &&
                    !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5)) {
                    
                    blocker = "AAPL";    //  Do not show this tee time if taken and this user is not part of it
                    }
                    }
                     ///


                    //
                    //  Custom for Stonebridge Ranch - Hills mship types - do not show any times for Dye course
                    //
                    if (club.equals("stonebridgeranchcc") && mship.equals("Hills") && courseNameT.equals("Dye")) {

                        blocker = "AAPL";       //  Do not show this tee time
                    }


                    //
                    //  Custom check for Olympic Club's starter times
                    //
                    if (club.equals("olyclub")) {

                        if (courseNameT.equals("Cliffs")) {

                            blocker = "AAPL";       //  Do not show this tee time (no member access on this course)

                        } else {

                            boolean olyRest = false;

                            olyRest = verifyCustom.checkOlyStarterTime(dateShort, date, tee_time, day_name, courseNameT, "member");   // go check if Starter time

                            if (olyRest == true) {                     // if Starter Time

                                blocker = "AAPL";       //  Do not show this tee time
                            }
                        }
                    }       // end of Olympic Club custom



                    //
                    //  Custom check for Cordillera Canned Restrictions (see below for Starter Times check)
                    //
              /*
                    if (club.equals( "cordillera" ) && dateShort > 413 && dateShort < 1101) {  // if within the custom date range
                    
                    cordallow = true;           // default to allow
                    
                    if (index > 14) {          // if more than 14 days in advance - check for dedicated Lodge Times
                    
                    cordallow = cordilleraCustom.checkCordillera(date, tee_time, courseNameT, "member"); // go check if this time is restricted
                    
                    if (cordallow == false) {                  // if restricted
                    
                    lskip = 1;                              // skip this time (do not show to members
                    }
                    }
                    }
                     ///


                    // debug
                    //out.println("<!-- tee_time="+tee_time+", wait_list_id="+wait_list_id+", last_wait_list_id="+last_wait_list_id+", firstWait="+firstWait+", courseNameT="+courseNameT+", lastCourse="+lastCourse+", blocker="+blocker+", event="+event+", WL_index="+WL_index+", WLbtn_index["+WL_index+"]="+WLbtn_index[WL_index]+", WLbtn_displayed["+WL_index+"]="+WLbtn_displayed[WL_index]+" -->");

                    //if ( blocker.equals( "" ) && lskip == 0 && (!waitlist || (waitlist && firstWait == 1) || (waitlist && firstWait == 0 && firstWait_displayed == false)) ) {    // continue if tee time not blocked & not lottery & not wait list - else skip

                    if (blocker.equals("") && lskip == 0
                            && (!waitlist || (waitlist && !WLbtn_displayed[WL_index]) || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0))) {    // continue if tee time not blocked & not lottery & either not wait list or is wait list that hasn't been displayed yet, OR is wait list that is set to always show tee times - else skip

                        // NOTE:  else - skip everything and go to next tee time!

                        ampm = " AM";
                        if (hr == 12) {
                            ampm = " PM";
                        }
                        if (hr > 12) {
                            ampm = " PM";
                            hr = hr - 12;
                        }

                        bgcolor = "#F5F5DC"; // default

                        if (!event.equals("")) {
                            bgcolor = ecolor;

                        } else {

                            // an event wasn't specified, lets check for lotterys
                            if (!lottery.equals("") && !lottery_color.equals("")) {
                                bgcolor = lottery_color;
                            } else {
                                if (!rest.equals("")) {
                                    bgcolor = rcolor;
                                }
                            }
                        }

                        if (bgcolor.equals("Default")) {
                            bgcolor = "#F5F5DC";              // default
                        }

                        if (bgcolor5.equals("")) {
                            bgcolor5 = bgcolor;              // same as others if not specified
                        }


                        //
                        //  Interlachen - always show CA when mode of trans is any caddie
                        //
                        if (club.equals("interlachen")) {

                            if (p1cw.startsWith("CA")) {       // if any caddie
                                p1cw = "CA";                    //  make generic caddie
                            }
                            if (p2cw.startsWith("CA")) {
                                p2cw = "CA";
                            }
                            if (p3cw.startsWith("CA")) {
                                p3cw = "CA";
                            }
                            if (p4cw.startsWith("CA")) {
                                p4cw = "CA";
                            }
                            if (p5cw.startsWith("CA")) {
                                p5cw = "CA";
                            }
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

                        //
                        //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                        //
                        sfb = "F";       // default Front 9
                        sfb2 = "Front";       // default Front 9

                        if (fb == 1) {

                            sfb = "B";
                            sfb2 = "Back";
                        }

                        if (fb == 9) {

                            sfb = "O";
                            sfb2 = "O";
                        }

                        if (type == shotgun) {

                            //sfb = "S";            // there's an event and its type is 'shotgun'
                            sfb = (!hole.equals("")) ? hole : "";            // there's an event and its type is 'shotgun'
                        }


                        //
                        // check if we should allow user to select this slot
                        //
                        // check max allowed days in advance (normal time and lottery time), special event & cross-over time
                        //
                        allow = true;

                        //
                        //  Check if members are not allowed to access tee times today
                        //
                        if (restrictAll == true) {

                            allow = false;            // not today!
                        }

                        //
                        //  Save the Days in Advance value
                        //
                        int tempDays = days;        // days in adv for test

                        //
                        //  Philly Cricket - do not allow members to access the 9-hole course St Martins
                        //
                 /*
                        if (club.equals("philcricket") && courseNameT.equals("St Martins")) {
                        
                        allow = false;
                        }
                         ///

                        //
                        //  If Milwaukee CC, check for Special Guest Time
                        //
                        boolean mcctime = false;

                        if (club.equals("milwaukee") && mccGuestDay == true) {

                            if (fb == 1 && tee_time > 1200 && tee_time < 1431) {

                                mcctime = true;      // special guest time - make it a green button
                            }
                        }

                        //
                        //  If Santa Ana CC or Columbia-Edgewater, check for Special Women's Time
                        //
                        boolean satime = false;
                        /*  (removed 11/13/06)
                        if (club.equals( "santaana" ) && mtype.equals( "Adult Female" )) {
                        
                        if (day_name.equals("Tuesday") && index2 > 2 && index2 < 5 &&
                        ((tee_time > 744 && tee_time < 946 && fb == 0) ||
                        (tee_time > 744 && tee_time < 831 && fb == 1))) {
                        
                        satime = true;      // special time - allow it
                        }
                        
                        if (day_name.equals("Friday") && index2 > 5 && index2 < 8 &&
                        ((tee_time > 736 && tee_time < 1001 && fb == 0) ||
                        (tee_time > 751 && tee_time < 816 && fb == 1))) {
                        
                        satime = true;      // special time - allow it
                        }
                        }
                         ///
                        if (club.equals("cecc") && mtype.startsWith("Spouse")) {

                            if ((day_name.equals("Tuesday") || day_name.equals("Friday")) && index2 > 2 && index2 < 8
                                    && tee_time > 759 && tee_time < 1025) {

                                satime = true;      // special time - allow it

                                if (index2 == 7 && cal_time < 800) {        // if 7 days in adv and not 8 AM yet - do not allow

                                    satime = false;
                                }
                            }
                        }


                        //
                        //  Mission Viejo CC - If time is already occupied by a 4-some, restrict members that are not part of the reservation from accessing the tee time
                        //
                        if (club.equals("missionviejo")) {

                            if (!player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("")
                                    && !user1.equals(user) && !user2.equals(user) && !user3.equals(user) && !user4.equals(user)) {

                                allow = false;
                            }
                        }


                        //
                        //  Pinehurst CC - if Proprietary member only allow access the day of, starting at 6:00 AM MT.
                        //               - allow Women to access tee times on Tues & Thurs mornings (Ladies Days) 4/1-9/30
                        //
                        if (allow == true && club.equals("pinehurstcountryclub") && courseNameT.equals("Pfluger 9") && firstLott == 0) {

                            if ((day_name.equals("Tuesday") || day_name.equals("Thursday")) && tee_time < 1100
                                    && dateShort >= 401 && dateShort <= 930 && mtype.endsWith("Female")) {  // let women access their special times
                                allow = true;
                            } else if (index > 1 || (index == 1 && cal_time < 600)) {
                                allow = false;         // indicate no member access
                            }
                        }

                        //
                        //  Capital City Club - if displaying all courses, viewing Fri/Sat/Sun tee sheet, do not allow early access to Brookhaven course times.
                        //
                 /*
                        if (club.equals("capitalcityclub") && courseName1.equals("-ALL-")) {
                        
                        if (courseNameT.equals("Brookhaven") && (day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday")) &&
                        index2 == 3 && cal_time < 830) {
                        allow = false;
                        }
                        }
                         ///


                        //
                        //  If Los Coyotes check for Primary-only times
                        //
                        if (club.equals("loscoyotes") && allow == true) {

                            allow = verifyCustom.checkLosCoyotesTimes(date, tee_time, day_name, courseNameT);    // check for Primary-Only time

                            if (allow == true) {         // if Primary Only time

                                if (index2 > 3) {         // if more than 3 days in adv, then do not allow regardless of user

                                    allow = false;

                                } else if (!mtype.startsWith("Primary")) {

                                    allow = false;     // NOT a primary - do not allow access to this time
                                }

                            } else {

                                allow = true;       // NOT a primary time - reset to ok
                            }
                        }


                        //
                        //  If Bearpath CC check for member-only times
                        //
                        boolean beartime = false;

                        if (club.equals("bearpath")) {

                            beartime = verifySlot.checkBearpathGuests(day_name, date, tee_time, index2);
                        }

                        //
                        //  If Congressional CC & a Guest Day, check for guest times
                        //
                        boolean congresstime = false;

                        if (club.equals("congressional") && congGuestDay == true && !day_name.equals("Saturday") && !day_name.equals("Sunday") && !day_name.equals("Monday")) {

                            if (courseNameT.equals("Blue Course")) {

                                if (hr > 8 && min == 24 && tee_time < 1525) {       // if xx:24

                                    congresstime = true;                 // make it a green button
                                }

                            } else if (courseNameT.equals("Gold Course")) {

                                if (day_name.equals("Wednesday") && tee_time > 800 && tee_time < 940 && dateShort > 430 && dateShort < 1101) {
                                    
                                    congresstime = false;                 // do NOT make it a green button (Ladies Day)

                                } else if (min == 20 && tee_time < 1531) {       // if xx:20 

                                    congresstime = true;                 // make it a green button
                                }
                            }
                            
                        }

                        //
                        //  If Canterbury GC & a Guest Day, check for guest times
                        //
                        if (club.equals("canterburygc") && congGuestDay == true) {    // if Canterbury and beyond normal days in advance for members

                            congresstime = false;

                            //
                            //  check for weekend or Memorial Day or Labor Day (mornings not allowed)
                            //
                            if ((day_name.equals("Saturday") && tee_time < 1030) || (day_name.equals("Sunday") && tee_time < 1000)
                                    || (date == Hdate1 && tee_time < 1000) || (date == Hdate3 && tee_time < 1000)) {

                                allow = false;                       // do not allow this time

                            } else {

                                congresstime = true;                 // make it a green button and allow
                            }
                        }


                        //
                        //  If Oakmont CC check for selected times on Wed and Fri that are available
                        //
                        boolean oaktime = false;

                        if (club.equals("oakmont")) {

                            //
                            //  If Wed or Fri, and not a shotgun today, check if this time is one when multiple guests are allowed.
                            //  If so, make the submit button green and allow this tee time.
                            //
                            if (event.equals("") && oakshotgun == false && (day_name.equals("Wednesday") || day_name.equals("Friday"))) {

                                if (index2 > Max14) {                // Wed or Fri and more than 14 days in advance

                                    allow = false;                     // do not allow this time unless ok below
                                }

                                //
                                //  Check the time of this tee time against those allowed for this day
                                //
                                if (day_name.equals("Friday")) {       // if Friday

                                    oakloop1:
                                    for (int oak = 0; oak < fricount; oak++) {

                                        if (tee_time == fritimes[oak]) {

                                            oaktime = true;       // tee time is ok
                                            allow = true;
                                            break oakloop1;
                                        }
                                    }

                                } else {    // Wednesday

                                    oakloop3:
                                    for (int oak = 0; oak < wedcount; oak++) {      // check normal Wed times

                                        if (tee_time == wedtimes[oak]) {

                                            oaktime = true;       // tee time is ok
                                            allow = true;
                                            break oakloop3;
                                        }
                                    }
                                }
                            }
                        }         // end of IF Oakmont


                            /*
                        if (club.equals("congressional")) {

                            //
                            //  If course is Club Course Gold today, then do not allow any access - walk-up times only (Tues - Fri)
                            //
                            //
                            String congCourse = congressionalCustom.getFullCourseName(date, day, courseNameT);

                            if (congCourse.equals("Club Course Gold") && (day_name.equals("Tuesday") || day_name.equals("Wednesday")
                                    || day_name.equals("Thursday") || day_name.equals("Friday"))) {

                                //
                                //  If twosome time and Tues, then allow access - others NO ACCESS on this course
                                //
                                if (twoSomeOnly == false || !day_name.equals("Tuesday")) {

                                    allow = false;            // not today!
                                }
                            }
                        }
                             ///


                        if (club.equals("oceanreef") && courseNameT.equals("Dolphin") && !restrictAll) {

                            //
                            //  Ocean Reef & Dolphin Course - days in advance based on Mship Type (normally 30 days, but can be less)
                            //
                            allow = verifyCustom.checkOceanReefMship(dateShort, index2, tee_time, cal_time, mship);
                        }


                        if (club.equals("ccnaples") && (mship.startsWith("Associate B") || mship.startsWith("Associate  B"))) {

                            //  CC of Naples - check 'Associate B ..." mship types (Nov 1 - Apr 30)

                            if ((dateShort > 1031 || dateShort < 431) && tee_time < 1230) {  // if in season and before 12:30

                                allow = false;       // not allowed
                            }
                        }

                        if (club.equals("bellevuecc")) {

                            // Bellevue CC - Between 5/4 and 8/31, do not allow access to 3:50pm-6:10pm times on Tuesday until 9pm, 7 days in advance (normal is 8am, 7 days in advance)
                            if (dateShort >= 504 && dateShort <= 831 && day_name.equals("Tuesday") && index == 7
                                    && tee_time >= 1550 && tee_time <= 1810 && cal_time < 2100) {

                                allow = false;
                            }
                        }


                        if (allow == true) {     // if still ok, check more

                            //
                            // if restriction for this slot and its not the first time for a lottery, check restriction for this member
                            //
                            if (!rest.equals("") && firstLott == 0) {

                                ind = 0;
                                while (ind < parmr.MAX && allow == true && !parmr.restName[ind].equals("")) {     // check all possible restrictions

                                    if (parmr.applies[ind] == 1 && parmr.stime[ind] <= tee_time && parmr.etime[ind] >= tee_time) {      // matching time ?

                                        // Check to make sure no suspensions apply
                                        suspend = false;
                                        for (int k = 0; k < parmr.MAX; k++) {

                                            if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {
                                                k = parmr.MAX;   // don't bother checking any more
                                            } else if (parmr.susp[ind][k][0] <= tee_time && parmr.susp[ind][k][1] >= tee_time) {    //tee_time falls within a suspension                                   // init fields

                                                suspend = true;
                                                k = parmr.MAX;     // don't bother checking any more
                                            }
                                        }

                                        if (!suspend) {

                                            if ((parmr.courseName[ind].equals("-ALL-")) || (parmr.courseName[ind].equals(courseNameT))) {  // course ?

                                                if ((parmr.fb[ind].equals("Both")) || (parmr.fb[ind].equals(sfb2))) {    // matching f/b ?

                                                    //
                                                    //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                    //
                                                    if (event.equals("")) {           // change color if no event

                                                        if (!parmr.color[ind].equals("Default")) {     // if not default

                                                            bgcolor = parmr.color[ind];

                                                            if (bgcolor5.equals("")) {
                                                                bgcolor5 = bgcolor;         // same as others if not specified
                                                            }
                                                        }
                                                    }
                                                    allow = false;                    // match found
                                                }
                                            }
                                        }
                                    }
                                    ind++;
                                }               // end of while (loop2)

                                if (allow && !rcolor.equals("")) {     // No applicable restrictions found, check for suspensions for restrictions that don't apply.

                                    ind = 0;
                                    while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {

                                        if (parmr.restName[ind].equals(rest)) {

                                            // Check to make sure no suspensions apply
                                            suspend = false;
                                            for (int k = 0; k < parmr.MAX; k++) {

                                                if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {
                                                    k = parmr.MAX;   // don't bother checking any more
                                                } else if (parmr.susp[ind][k][0] <= tee_time && parmr.susp[ind][k][1] >= tee_time) {    //tee_time falls within a suspension
                                                    suspend = true;
                                                    k = parmr.MAX;     // don't bother checking any more
                                                }
                                            }      // end of for loop

                                            if (suspend) {

                                                if ((parmr.courseName[ind].equals("-ALL-")) || (parmr.courseName[ind].equals(courseNameT))) {  // course ?

                                                    if ((parmr.fb[ind].equals("Both")) || (parmr.fb[ind].equals(sfb2))) {    // matching f/b ?

                                                        //
                                                        //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                        //
                                                        if (event.equals("")) {           // change color back to default if no event

                                                            // Search for the first non-suspended color to apply, or default if non found
                                                            bgcolor = "#F5F5DC";   // default color

                                                            int ind2 = 0;
                                                            while (ind2 < parmr.MAX && !parmr.restName[ind2].equals("")) {

                                                                // make sure it's not the default restriction/color, and has a non-blank, non-default color
                                                                // and applies to this tee_time
                                                                if (!parmr.restName[ind2].equals(rest) && !parmr.color[ind2].equals("") && !parmr.color[ind2].equalsIgnoreCase("Default")
                                                                        && parmr.stime[ind2] <= tee_time && parmr.etime[ind2] >= tee_time) {

                                                                    // Check to make sure no suspensions apply
                                                                    suspend = false;
                                                                    for (int k = 0; k < parmr.MAX; k++) {

                                                                        if (parmr.susp[ind2][k][0] == 0 && parmr.susp[ind2][k][1] == 0) {
                                                                            k = parmr.MAX;   // don't bother checking any more
                                                                        } else if (parmr.susp[ind2][k][0] <= tee_time && parmr.susp[ind2][k][1] >= tee_time) {    //tee_time falls within a suspension
                                                                            suspend = true;
                                                                            k = parmr.MAX;     // don't bother checking any more
                                                                        }
                                                                    }

                                                                    if (!suspend) {

                                                                        if ((parmr.courseName[ind2].equals("-ALL-")) || (parmr.courseName[ind2].equals(courseNameT))) {  // course ?

                                                                            if ((parmr.fb[ind2].equals("Both")) || (parmr.fb[ind2].equals(sfb2))) {    // matching f/b ?

                                                                                //
                                                                                //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                                                //
                                                                                if (event.equals("")) {           // change color if no event

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

                                                            if (bgcolor5.equals("")) {
                                                                bgcolor5 = bgcolor;         // same as others if not specified
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        ind++;
                                    }      // end of while loop
                                }
                            }     // end of if rest exists in teecurr
                        }

                        //
                        //  If Portland GC check for Member Time (walk-up time)
                        //
                        if (allow == true && club.equals("portlandgc")) {

                            walkup = verifyCustom.checkPGCwalkup(date, tee_time, day_name);   // Walk-Up time?

                            if (walkup == true) {

                                allow = false;

                                if (ecolor.equals("")) {        // if no event color

                                    bgcolor = "burlywood";       // custom color for Walk-Up times
                                }
                            }
                        }


                        //
                        //  If Medinah CC check for Member Time (walk-up time) and days in adv for Course #2
                        //
                        if (allow == true && club.equals("medinahcc")) {

                            //
                            //  8:00 & 8:10 tee times on Course #3 on Tues are reserved for Spouses
                            //
                            if (courseNameT.equals("No 3") && day_name.equals("Tuesday")
                                    && (tee_time == 800 || tee_time == 810)) {

                                if (!mtype.equals("Regular Spouse") && !mtype.equals("Jr. Spouse")
                                        && !mtype.equals("Reg Prob Spouse")) {

                                    allow = false;                     // 8:00 & 8:10 reserved for spouses

                                } else {       // spouse - these times available 6 days in adv starting at 7:00 AM

                                    if (index2 > 6 || (index2 == 6 && cal_time < 700)) {

                                        allow = false;

                                    } else {

                                        tempDays = 6;       //  force days in adv to 6 days for this time only
                                    }
                                }

                            } else {

                                //
                                // Medinah, not Tues at 8:00 or 8:10 on No 3
                                //
                                boolean medinahMemTime = medinahCustom.checkMemTime(courseNameT, day_name, tee_time, date);

                                if (medinahMemTime == true) {

                                    allow = false;     // Member Time (Walk-up Time) or Unaccompanied Time - do not allow

                                } else {

                                    if (!courseNameT.equals("No 2")) {       // if Course #2
                                        //  if (courseNameT.equals( "No 2" )) {       // if Course #2

                                        // if (index2 > Max7) {                  // more than 7 days in advance?

                                        //    allow = false;                     // Max of 7 days in adv for Course #2
                                        // }

                                        // } else {

                                        //
                                        //  #1 or #3 and NOT a Member Time - do not allow spouses anytime other than those above
                                        //
                                        if (mtype.equals("Regular Spouse") || mtype.equals("Jr. Spouse")
                                                || mtype.equals("Reg Prob Spouse") || mtype.equals("NR Spouse")
                                                || mtype.equals("Soc Spouse")) {

                                            allow = false;                     // no spouses can make times
                                        }
                                    }
                                }
                            }

                            //
                            //  Allow 7 days in adv for Course #2 (all mships are config'd for 2 days)
                            //
                            // if (courseNameT.equals( "No 2" )) {

                            // tempDays = 7;         //  force medinah's Course #2 to 7 days
                            // }

                        }      // end of Medinah Custom


                        /*
                        //
                        //  If Valley Country Club and a lady change the days in adv to 14 for Fridays.   (Removed by case 1388)
                        //
                        boolean tmp_valleycc = false;
                        if (allow == true && club.equals( "valleycc" ) && day_name.equals( "Friday" ) && index2 < tempDays) { //&& index2 > 14 (tempDays is set to 14 above.  need to reset to 1 (default for Fridays) after we check mship and time
                        
                        //out.println("<!-- HERE: ldays=" +ldays+ ", index2=" +index2+ ", tempDays=" +tempDays+ ", tee_time="+tee_time+ "  -->");
                        
                        if (mtype.endsWith( "Female" ) && tee_time > 729 && tee_time < 1100) {
                        
                        //out.println("<!-- HERE-ALLOWING: tee_time="+tee_time+ "  -->");
                        tmp_valleycc = true;
                        }
                        tempDays = 1;
                        }
                         ///



                        if (allow == true) {     // if still ok, check more

                            //
                            //  Check if we are past the allowed Days in Advance
                            //
                            if (!club.equals("oakmont") && !club.equals("invernessclub") && (index2 > tempDays && index2 > ldays)) {   // if beyond days in adv

                                //                     if (mcctime == false && satime == false && congresstime == false && tmp_valleycc == false) {      // allow if mcctime or SantaAna or Congressional time
                                if (mcctime == false && satime == false && congresstime == false) {      // allow if mcctime or SantaAna or Congressional time

                                    allow = false;
                                }
                            }

                            if (!event.equals("") || fb == 9) {    // event or cross-over ?

                                allow = false;
                            }


                            //
                            //  Brooklawn - check for tee times event on Sat 7/18/09 and allow Male members to access (see also SystemUtils). Remove after 7/18/09 !!!
                            // OLD SKIN
                            if (club.equals("brooklawn")) {

                                if ((thisDate > 20130509 || thisDate == 20130509 && cal_time >= 900) && date == 20130615 && tee_time >= 839 && tee_time <= 1400
                                        && (mtype.endsWith("Male") || mtype.equalsIgnoreCase("Cert Dependent over 16") || mtype.endsWith("Dependent"))) {

                                    allow = true;      // ok
                                }

                                if ((thisDate > 20130515 || thisDate == 20130515 && cal_time >= 900) && date == 20130619 && tee_time >= 1130 && tee_time <= 1430) {

                                    allow = true;      // ok
                                }

                                if ((thisDate > 20130526 || thisDate == 20130526 && cal_time >= 700) && date == 20130713 && tee_time >= 700 && tee_time <= 1230) {

                                    allow = true;      // ok
                                }
                            }
                        }

                        //
                        //  If Inverness Club then members can make tee times w/ guests up to Oct 31 on weekdays
                        //
                        if (allow == true && club.equals("invernessclub")) {

                            // if more than 30 days in advance and sheet date is after 10-31-07 restrict
                            if (dateShort > 1031 && index2 > 30) {

                                allow = false;
                            }
                        }

                        //
                        //  Custom check for Cordillera Custom Starter Times (if more than 3 days in advance)
                        //
                 /*
                        if (club.equals( "cordillera" ) && dateShort > 531 && dateShort < 1001) {  // if within the custom date range
                        
                        if (index > 3 || (index == 3 && cal_time < 600)) {          // if more than 3 days in advance
                        
                        cordallow = cordilleraCustom.checkStarterTime(date, tee_time, courseNameT, "member"); // go check if this time is restricted
                        
                        if (cordallow == false) {                      // if restricted (starter time)
                        
                        bgcolor = cordRestColor;                    // set color for this slot
                        allow = false;                              // do not allow member access to this time
                        }
                        }
                        }
                        ///


                        //
                        //  Custom check for Oakland Hills (for days beyond the 8 day limit)
                        //
                        if (club.equals("oaklandhills") && allow == true && index2 > 8 && courseNameT.equals("South Course")) {

                            allow = verifySlot.checkOaklandAdvTime(date, tee_time, day_name); // go check if this time is restricted
                        }

                        //
                        //  Custom check for Tamarisk CC - Cannot book times between 8am and 11:07 am on any day during season more than 3 days in advance
                        //
                        if (club.equals("tamariskcc") && allow == true && index2 > 2 && index2 < 8) {   // between 3 and 7 days in advance - go check times

                            allow = verifyCustom.checkTamariskAdvTime(tee_time, date, index2, cal_time);
                        }

                        //
                        //   More custom checks
                        //
                        if (allow == true) {     // if still ok, check more

                            //
                            //  If Cherry Hills and in normal season, check mtype and day
                            //
                            if (club.equals("cherryhills") && dateShort > 414 && dateShort < 931) {

                                //
                                //  Process according to the day of week (and holidays)
                                //
                                if (date == Hdate1 || date == Hdate2 || date == Hdate3) {   // if Mem Day, 7/04 or Labor Day

                                    if ((!mtype.endsWith("ember") && day_name.equals("Monday"))
                                            || (tee_time < 1101 && (!mtype.endsWith("ember") || mship.equals("Resident Emeritus")))) {

                                        allow = false;
                                    }

                                } else {

                                    if (day_name.equals("Monday") && !mtype.endsWith("ember")) {

                                        allow = false;
                                    }
                                    if (day_name.equals("Tuesday") && index2 > 1 && tee_time < 1101 && mtype.endsWith("ember")) {

                                        allow = false;
                                    }
                                    if (day_name.equals("Thursday") && index2 > 1 && tee_time < 1000 && mtype.endsWith("ember")) {

                                        allow = false;
                                    }
                                    if (day_name.equals("Saturday") && tee_time < 1101 && (!mtype.endsWith("ember") || mship.equals("Resident Emeritus"))) {

                                        allow = false;
                                    }
                                    if (day_name.equals("Sunday") && tee_time < 1001 && (!mtype.endsWith("ember") || mship.equals("Resident Emeritus"))) {

                                        allow = false;
                                    }
                                }
                            }


                            //
                            //  If Merion, a weekend day, more than 7 days in advance, and a non-resident - check for limited times
                            //
                            if (club.equals("merion") && (index > 7 || (index == 7 && cal_time < 600))
                                    && (day_name.equals("Saturday") || day_name.equals("Sunday")) && mship.equals("Non-Resident")) {

                                if (day_name.equals("Saturday") && tee_time < 1031) {

                                    allow = false;
                                }
                                if (day_name.equals("Sunday") && tee_time < 901) {

                                    allow = false;
                                }
                            }


                            //
                            //  If El Niguel and Adult Female
                            //
                            if (club.equals("elniguelcc") && mtype.equals("Adult Female")) {

                                //
                                //  Process according to the day of week
                                //
                                //    Ladies can book times (7:55 to 11:59) for Tues starting 4 days in adv at 1:00 PM
                                //    instead of the normal 2 days in adv at 6:30 AM (days in adv changed above).
                                //
                                //    Ladies can book times (7:25 to 10:59) for Thurs starting 2 days in adv at 6:00 PM
                                //    instead of the normal 2 days in adv at 6:30 AM.
                                //
                                if (day_name.equals("Tuesday") && (index2 > 2 || (index2 == 2 && cal_time < 630))
                                        && (tee_time < 755 || tee_time > 1159)) {

                                    allow = false;      // do not allow if outside adv parms
                                }

                                if (day_name.equals("Thursday") && index2 == 2 && cal_time < 1800
                                        && tee_time < 1100 && tee_time > 724) {

                                    allow = false;      // do not allow 7:25 to 10:59 before 6 PM 2 days in adv
                                }
                            }

                            //
                            //  If Claremont CC and Adult Female
                            //
                            /*
                            if (club.equals("claremontcc") && mtype.equals("Adult Female")) {

                                //
                                //  Process according to the day of week
                                //    Ladies can book times (8:00 to 11:59) for Tues starting 30 days in advance
                                //
                                if (day_name.equals("Tuesday") && index2 > 3
                                        && (tee_time < 800 || tee_time > 1159)) {

                                    allow = false;      // do not allow if outside adv parms
                                }
                            }
                            ///

                            //
                            //  If Seacliff CC and Adult Female 18 Holer
                            //
                            if (club.equals("seacliffcc") && msubtype.equals("18 Holer")) {

                                //
                                //  Process according to the day of week
                                //    Ladies can book times (7:00 to 11:00) for Tues up to 30 days in advance
                                //
                                if (day_name.equals("Tuesday") && index2 > 7
                                        && (tee_time < 700 || tee_time > 1100)) {

                                    allow = false;      // do not allow if outside adv parms
                                }
                            }

                            //
                            //  If Palo Alto Hills and Adult Female 18 Holer
                            //
                            if (club.equals("paloaltohills") && msubtype.equals("18 Holer")) {

                                //
                                //  Process according to the day of week
                                //    Ladies can book times (6:00 to 11:50) for Thurs up to 30 days in advance
                                //
                                if (day_name.equals("Thursday") && index2 > 7
                                        && (tee_time > 1159)) {

                                    allow = false;      // do not allow if outside adv parms and Ladies Day
                                }
                            }

                            //
                            //  If Brooklawn CC and Adult Female 18 Holer
                            //
                            if (club.equals("brooklawn")) {

                                if (day_name.equals("Tuesday")) {

                                    if (msubtype.equals("18 Holer") || msubtype.equals("9/18 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    18 Holers can book times (8:00 to 9:28) for Tues starting 7 days in advance
                                        //
                                        if (index2 > 2 && (tee_time < 800 || tee_time > 936)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not an 18 holer - restrict from 18 holer times

                                        if (tee_time > 759 && tee_time < 937) {    // if 18 holer time (Women's group)

                                            allow = false;      // do not allow non 18 holers (custom restriction)
                                        }
                                    }

                                } else if (day_name.equals("Wednesday")) {

                                    if (msubtype.equals("9 Holer") || msubtype.equals("9/18 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    9 Holers can book times (8:00 to 10:00) for Wed starting 7 days in advance
                                        //
                                        if (index2 > 2 && (tee_time < 800 || tee_time > 1000)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not an 9 holer - restrict from 9 holer times

                                        if (tee_time > 759 && tee_time < 937) {    // if 9 holer time (Women's group)

                                            allow = false;      // do not allow non 18 holers (custom restriction)
                                        }
                                    }
                                }
                            }     // end of IF brooklawn


                            //
                            //  If Sharon Heights and Adult Female 18 Holer
                            //
                            if (club.equals("sharonheights") && day_name.equals("Tuesday")) {

                                if (msubtype.equals("18 Holer")) {

                                    //
                                    //  Process according to the day of week
                                    //    18 Holers can book times (7:30 to 9:54) for Tues starting 7 days in advance
                                    //
                                    if (index2 > 2 && (tee_time < 730 || tee_time > 954)) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not an 18 holer - restrict from 18 holer times

                                    if (tee_time > 729 && tee_time < 955) {    // if 18 holer time (Women's group)

                                        allow = false;      // do not allow non 18 holers (custom restriction)
                                    }
                                }
                            }     // end of IF sharonheights


                            //
                            //  If Green Hills CC and Ladies msubtype
                            //
                            if (club.equals("greenhills") && day_name.equals("Thursday")) {

                                if (msubtype.equals("Ladies")) {

                                    //
                                    //  Process according to the day of week
                                    //    "Ladies" can book times (6:45 to 10:30) for Thurs starting 7 days in advance
                                    //
                                    if (index2 > 2 && (tee_time < 645 || tee_time > 1030)) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not a "Ladies" golfer - restrict from Ladies times

                                    if (tee_time > 645 && tee_time < 1030) {    // if Ladies time (Women's group)

                                        allow = false;      // do not allow non Ladies (custom restriction)
                                    }
                                }
                            }     // end of IF greenhills


                            //
                            //  If Rolling Hills CC - CO and Ladies msubtype
                            //
                            if (club.equals("rhillscc")) {

                                if ((day_name.equals("Tuesday") || day_name.equals("Thursday"))) {

                                    if (dateShort >= 401 && dateShort <= 1031) {      // In season

                                        if (msubtype.equals("Ladies")) {

                                            //
                                            //  Process according to the day of week
                                            //    "Ladies" can book times (7:00 to 9:33) for Tues & Thurs starting 14 days in advance
                                            //
                                            if (index2 > 4 && (tee_time < 700 || tee_time > 933)) {

                                                allow = false;      // do not allow if outside adv parms
                                            }

                                        } else {    // not a "Ladies" golfer - restrict from Ladies times

                                            if (tee_time >= 700 && tee_time <= 933) {    // if Ladies time (Women's group)

                                                allow = false;      // do not allow non Ladies (custom restriction)
                                            }
                                        }

                                    } else {     // Out of season

                                        if (msubtype.equals("Ladies")) {

                                            //
                                            //  Process according to the day of week
                                            //    "Ladies" can book times (9:24 to 9:42) for Tues & Thurs starting 14 days in advance
                                            //
                                            if (index2 > 4 && (tee_time < 924 || tee_time > 942)) {

                                                allow = false;      // do not allow if outside adv parms
                                            }

                                        } else {    // not a "Ladies" golfer - restrict from Ladies times

                                            if (tee_time >= 924 && tee_time <= 942) {    // if Ladies time (Women's group)

                                                allow = false;      // do not allow non Ladies (custom restriction)
                                            }
                                        }
                                    }

                                } else if (day_name.equals("Wednesday")) {

                                    /*  removed at request of club - 5/31/12
                                    if ((dateShort >= 615 && dateShort <= 727 && tee_time >= 1215 && tee_time <= 1251)
                                            || ((dateShort < 615 || dateShort > 727) && tee_time >= 1157 && tee_time <= 1233)) {

                                        // Wednesday Group can book Wednesday 11:57-12:33 starting 3 days in advance
                                        if (index2 <= 3) {
                                            if (msubtype.equals("GOM")) {
                                                allow = true;
                                            } else {
                                                allow = false;
                                            }
                                        } else {
                                            allow = false;
                                        }
                                    }
                                    ///

                                } else if (day_name.equals("Friday") && tee_time >= 1215 && tee_time <= 1251) {

                                    /*
                                    // Friday Group can book Friday 12:15-12:51 starting 3 days in advance
                                    if (index2 <= 7) {
                                        if (msubtype.equals("Friday Boys")) {
                                            allow = true;
                                        } else {
                                            allow = false;
                                        }
                                    } else {
                                        allow = false;
                                    }
                                    ///
                                }
                            }     // end of IF greenhills
                            
                            
                            //
                            //  If Tualatin CC and Primary/Spouse Female member type
                            //
                            if (club.equals("tualatincc") && day_name.equals("Tuesday")) {

                                if (mtype.equals("Primary Female") || mtype.equals("Spouse Female")) {

                                    //
                                    //  Process according to the day of week
                                    //    Female members can book times (6:45 to 10:30) for Thurs starting 7 days in advance
                                    //
                                    if (index2 > 3 && (tee_time < 700 || tee_time > 1000)) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not a "Ladies" golfer - restrict from Ladies times

                                    if (tee_time >= 700 && tee_time <= 1000) {    // if Ladies time (Women's group)

                                        allow = false;      // do not allow non Ladies (custom restriction)
                                    }
                                }
                            }     // end of IF lagcc


                            //
                            //  If Los Altos G&CC and Female member type
                            //
                            if (club.equals("lagcc") && (day_name.equals("Tuesday") || day_name.equals("Thursday") || (date == 20120907 && day_name.equals("Friday")))) {

                                if (mtype.endsWith("Female")) {

                                    //
                                    //  Process according to the day of week
                                    //    Female members can book times (6:45 to 10:30) for Thurs starting 7 days in advance
                                    //
                                    if (((day_name.equals("Tuesday") || (date == 20120907 && day_name.equals("Friday"))) && index2 > 3 && (tee_time < 800 || tee_time > 930 || fb == 0))
                                            || (day_name.equals("Thursday") && index2 > 1 && (tee_time < 730 || tee_time > 930))) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not a "Ladies" golfer - restrict from Ladies times

                                    if (((day_name.equals("Tuesday") || (date == 20120907 && day_name.equals("Friday"))) && tee_time >= 800 && tee_time <= 930 && fb == 1)
                                            || (day_name.equals("Thursday") && tee_time >= 730 && tee_time <= 930)) {    // if Ladies time (Women's group)

                                        allow = false;      // do not allow non Ladies (custom restriction)
                                    }
                                }
                            }     // end of IF lagcc


                            //
                            //  If Indian Hills CC and 18 holer member subtype
                            //
                            if (club.equals("indianhillscc") && day_name.equals("Tuesday")) {

                                if (dateShort >= 401 && dateShort <= 1015) {

                                    if (msubtype.equals("18 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    Female members can book times (6:45 to 10:30) for Thurs starting 7 days in advance
                                        //
                                        if (index2 > 7 && tee_time > 1159 && fb != 1) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not a "Ladies" golfer - restrict from Ladies times

                                        if (tee_time <= 1159) {    // if Ladies time (Women's group)

                                            allow = false;      // do not allow non Ladies (custom restriction)
                                        }
                                    }
                                }
                            }     // end of IF indianhillscc


                            //
                            //  If CC of Fairfax and 18 holer or 9 Holer member subtype
                            //
                            if (club.equals("ccfairfax") && day_name.equals("Wednesday")) {

                                if (dateShort >= 401 && dateShort <= 1031) {

                                    if (msubtype.equals("18 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    Female 18 Holer members can book times 8:00am to 10:00am on the front only for Wed starting 14 days in advance
                                        //
                                        if (index2 > 1 && (tee_time > 1000 || fb != 0)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else if (msubtype.equals("9 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    Female 9 Holer members can book times 8:00am to 10:00am on the back only for Wed starting 14 days in advance
                                        //
                                        if (index2 > 1 && (tee_time > 1000 || fb != 1)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not a "18 Holer" or "9 Holer" golfer - restrict from Ladies times

                                        if (tee_time <= 1000) {    // if Ladies time (Women's group)

                                            allow = false;      // do not allow non 18/9 Holers (custom restriction)
                                        }
                                    }
                                }
                            }     // end of IF ccfairfax


                            //
                            //  If Algonquin GC and 9 Holer member subtype
                            //
                            if (club.equals("algonquin") && day_name.equals("Wednesday")) {

                                if (dateShort >= 401 && dateShort <= 1031) {

                                    if (msubtype.equals("9 Holer")) {

                                        //
                                        //  Process according to the day of week
                                        //    Female 9 Holer members can book times 8:00am to 10:00am on the front or back for Wed starting 14 days in advance
                                        //
                                        if (index2 > 4 && (tee_time < 800 || tee_time > 1000)) {

                                            allow = false;      // do not allow if outside adv parms
                                        }

                                    } else {    // not a "9 Holer" golfer - restrict from Ladies times

                                        if (tee_time >= 800 && tee_time <= 1000) {    // if Ladies time (Women's group)

                                            allow = false;      // do not allow non 9 Holers (custom restriction)
                                        }
                                    }
                                }
                            }     // end of IF algonquin


                            //
                            //  If Riviera CC and RWGA member subtype
                            //
                            if (club.equals("rivieracc")) {

                                if (msubtype.equals("RWGA")) {

                                    //
                                    //  Process according to the day of week
                                    //    Female RWGA members can book times 8:02am to 10:58am on the front or back for Tues starting 14 days in advance
                                    //
                                    if ((day_name.equals("Tuesday") && index2 > 2 && (tee_time < 802 || tee_time > 1058)) || 
                                        (day_name.equals("Thursday") && index2 > 2 && (tee_time != 858 && tee_time != 906))) {

                                        allow = false;      // do not allow if outside adv parms
                                    }

                                } else {    // not a "RWGA" golfer - restrict from Ladies times

                                    if ((day_name.equals("Tuesday") && tee_time >= 802 && tee_time <= 1058) ||
                                        (day_name.equals("Thursday") && (tee_time == 858 || tee_time == 906))) {    // if Ladies time (Women's group)

                                        allow = false;      // do not allow non RWGA (custom restriction)
                                    }
                                }
                            }     // end of IF rivieracc
                            
                            if (club.equals("overlakegcc")) {
                                
                                if (msubtype.equals("18 Holer")) {
                                    
                                    // Ladies '18 Holer' subtypes can access Tuesday times 7:30AM - 10:20AM, and Friday 7:30AM - 9:20AM
                                    if ((day_name.equals("Tuesday") && (tee_time < 730 || tee_time > 1020)) ||
                                        (day_name.equals("Friday") && index2 > 3 && (tee_time < 730 || tee_time > 920))) {
                                        
                                        allow = false;
                                    }
                                    
                                } else {
                                    
                                    if ((day_name.equals("Tuesday") && (tee_time >= 730 && tee_time <= 1020)) ||
                                        (day_name.equals("Friday") && index2 > 3 && (tee_time >= 730 && tee_time <= 920))) {
                                        
                                        allow = false;
                                    }
                                }
                            }
                            
                            if (club.equals("brooksidecountryclub") && date >= 20130430 && date <= 20130703) {
                                /*
                                if (msubtype.equals("League")) {
                                    
                                    if ((day_name.equals("Tuesday") && (tee_time < 1500 || tee_time > 1820)) 
                                     || (day_name.equals("Wednesday") && ((date == 20130529 && (tee_time < 1500 || tee_time > 1820)) || (date != 20130529 && (tee_time < 1600 || tee_time > 1800))))
                                     || (day_name.equals("Thursday") && date == 20130530 && (tee_time < 1600 || tee_time > 1820))) {
                                        
                                        allow = false;
                                    }
                                    
                                } else {
                                    
                                    if ((day_name.equals("Tuesday") && tee_time >= 1500 && tee_time <= 1820) 
                                     || (day_name.equals("Wednesday") && ((date == 20130529 && tee_time >= 1500 && tee_time <= 1820) || (date != 20130529 && tee_time >= 1600 && tee_time <= 1800)))
                                     || (day_name.equals("Thursday") && date == 20130530 && tee_time >= 1600 && tee_time <= 1820)) {
                                        
                                        allow = false;
                                    }
                                }
                                 ///
                            }

                            //
                            //  If Royal Oaks in WA and Adult Female
                            //
                            if (club.equals("royaloaks") && mtype.equals("Adult Female")
                                    && (day_name.equals("Tuesday") || day_name.equals("Friday"))) {


                                if (dateShort >= 301 && dateShort <= 1031) {
                                    //
                                    //    Women can book times (8:00 to 12:29) for Tues starting 7 days in advance
                                    //
                                    if (day_name.equals("Tuesday")) {

                                        if (index2 > 2 && (tee_time < 800 || tee_time > 1229)) {

                                            allow = false;      // do not allow if not an 18-Hole ladies time
                                        }

                                    } else if (day_name.equals("Friday")) {

                                        if ((index2 > 2 || (index == 2 && cal_time < 730)) && (tee_time > 1000 || fb != 1)) {

                                            allow = false;      // do not allow if not a 9-Hole ladies time
                                        }
                                    }
                                }

                            }     // end of IF royaloaks


                            // If The Plantation GC, don't allow members access to times before 10:29 until 6:30am Pacific 1 day in advance.
                            if (club.equals("theplantationgc")) {

                                if (tee_time <= 1029 && (index2 > 1 || (index2 == 1 && cal_time <= 700))) {

                                    allow = false;
                                }
                            }


                            // If Royal Montreal GC and 3 days in advance or less, open lottery times that have no players in them for booking.
                            if (club.equals("rmgc")) {  // Open up times 3 days in adv at 7:00am Eastern

                                if (index2 < 3 || (index2 == 3 && cal_time >= 700)) {

                                    if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("") && player5.equals("")) {
                                        noAccessAfterLottery = false;
                                    } else {
                                        noAccessAfterLottery = true;
                                    }

                                } else {
                                    noAccessAfterLottery = true;
                                }
                            }


                            // Indian Ridge CC - Don't allow members access to lottery times after they've been processed until after 7:00am Pacific 7 days in advance
                            if (club.equals("indianridgecc")) {

                                if (day_name.equals("Wednesday") && tee_time >= 739 && tee_time <= 918 && (index2 > 7 || (index2 == 7 && cal_time < 700))) {

                                    noAccessAfterLottery = true;
                                }
                            }


                            /*        // ON HOLD (case 1486)
                            //
                            //  If Los Coyotes CC and Secondary mtype
                            //
                            if (club.equals( "loscoyotes" ) && mtype.startsWith( "Secondary" ) && index > 3) {
                            
                            if (day_name.equals( "Tuesday" )) {
                            
                            if (tee_time < 700 || tee_time > 900) {
                            
                            allow = false;      // do not allow other times this far in adv
                            }
                            
                            } else {    // not Tues
                            
                            allow = false;      // do not allow other days this far in advance
                            }
                            }     // end of IF loscoyotes
                             ///

                        }     // end of IF allow still true


                        /*
                        //
                        //  If Jonathan's Landing and days in advance is 6 block access to each course except Hills - Case# 1328
                        //
                        if (club.equals( "jonathanslanding" ) && !courseNameT.equals( "Hills" ) && index == 6) {
                        
                        allow = false;
                        }
                         ///
                        // waitlist check was here!
                        if (allow == true) {         // if still ok

                            //
                            //  if all spots taken and this player is not one of them or assigned to guest,
                            //  or did not originate the request, do not allow select
                            //
                            if (twoSomeOnly == true) {      // if 2-some ONLY time

                                if (!player1.equals("") && !player2.equals("")) {

                                    if (!player1.equals(full_name) && !player2.equals(full_name)) {

                                        if (!userg1.equals(user) && !userg2.equals(user) && !orig_by.equalsIgnoreCase(user)
                                                && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && !orig2.equalsIgnoreCase(user)))) {

                                            allow = false;     // all spots taken and this player not one of them
                                        }
                                    }
                                }

                            } else {         // NOT a 2-some time

                                if (threeSomeOnly == true) {      // if 3-some ONLY time

                                    if (!player1.equals("") && !player2.equals("") && !player3.equals("")) {

                                        if (!player1.equals(full_name) && !player2.equals(full_name) && !player3.equals(full_name)) {

                                            if (!userg1.equals(user) && !userg2.equals(user) && !userg3.equals(user) && !orig_by.equalsIgnoreCase(user)
                                                    && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && !orig2.equalsIgnoreCase(user) && !orig3.equalsIgnoreCase(user)))) {

                                                allow = false;     // all spots taken and this player not one of them
                                            }
                                        }
                                    }

                                } else {         // normal tee time

                                    if ((fives != 0) && (rest5.equals(""))) {   // if 5-somes and not restricted

                                        if ((!player1.equals("")) && (!player2.equals(""))
                                                && (!player3.equals("")) && (!player4.equals("")) && (!player5.equals(""))) {

                                            if ((!player1.equals(full_name)) && (!player2.equals(full_name))
                                                    && (!player3.equals(full_name)) && (!player4.equals(full_name)) && (!player5.equals(full_name))) {

                                                if (!userg1.equals(user) && !userg2.equals(user) && !userg3.equals(user)
                                                        && !userg4.equals(user) && !userg5.equals(user) && !orig_by.equalsIgnoreCase(user)
                                                        && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && !orig2.equalsIgnoreCase(user) && !orig3.equalsIgnoreCase(user)
                                                        && !orig4.equalsIgnoreCase(user) && !orig5.equalsIgnoreCase(user)))) {

                                                    allow = false;     // all spots taken and this player not one of them
                                                }
                                            }
                                        }
                                    } else {        // 4-some time

                                        if ((!player1.equals("")) && (!player2.equals(""))
                                                && (!player3.equals("")) && (!player4.equals(""))) {

                                            if ((!player1.equals(full_name)) && (!player2.equals(full_name))
                                                    && (!player3.equals(full_name)) && (!player4.equals(full_name))) {

                                                if (!userg1.equals(user) && !userg2.equals(user) && !userg3.equals(user)
                                                        && !userg4.equals(user) && !orig_by.equalsIgnoreCase(user)
                                                        && (!restrictByOrig || (!orig1.equalsIgnoreCase(user) && !orig2.equalsIgnoreCase(user) && !orig3.equalsIgnoreCase(user)
                                                        && !orig4.equalsIgnoreCase(user)))) {

                                                    allow = false;     // all spots taken and this player not one of them
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //
                            //  if today's sheet and the tee time is less than the current time do not allow select
                            //
                            if ((index2 == 0) && (tee_time <= cal_time)) {

                                allow = false;     // do not allow select
                            }

                            //
                            //  if today's sheet and the tee time is less than the stop time do not allow select (custom)
                            //
                            if (allow == true && index2 == 0 && stop_time > 0) {

                                if (tee_time <= stop_time) {

                                    allow = false;     // do not allow select
                                }
                            }

                        }

                        submit = "time:" + fb;       // create a name for the submit button

                        //
                        //  Start this tee time row
                        //
                        if (mobile == 0) {

                            out.println("<tr>");         // start of tee slot (row)

                            j++;                         // increment the jump label index (where to jump on page)

                            out.println("<a name=\"jump" + j + "\"></a>"); // <!-- allow=" + ((allow) ? "true" : "false") + " firstLott=" + firstLott + " firstWait=" + firstWait + " tee_time=" + tee_time + " -->"); // create a jump label for 'noshow' returns

                        } else {

                            out.println("<tr class=\"tablerow\"><td>");         // start of tee slot row and time column for mobile users (must have form inside of the td!)
                        }


                        //
                        //   // USE THE FOLLOWING NOW SO WE WILL NOT OVERRIDE A LOTTERY IN STATE 2 !!!!!!!!!!!


                        if (allow == true && in_use == 0) {          // can user select this slot and not in use?

                            lstate = 0;                               // init as no lottery
                            slots = 0;

                            //
                            //  if not event, then check for lottery time (events override lotteries)
                            //
                            if (event.equals("") && !lottery.equals("")) {

                                lottloop3:
                                for (i = 0; i < count_lott; i++) {     // check for matching lottery

                                    if (lottery.equals(lottA[i])) {    // if match found

                                        lstate = lstateA[i];
                                        slots = slotsA[i];
                                        break lottloop3;
                                    }
                                }
                                i = 0;
                            }


                            /*     moved to Proshop_dsheet
                            //
                            //  Custom for Ramsey - if lottery processed, then ignore the wait list.  The wait list is only used during lottery signup.
                            //
                            if (club.equals("ramseycountryclub") && lstate == 5) {
                            
                            waitlist = false;        // allow members to access the tee times after the lottery is processed
                            }
                             ///



                            //
                            //  if not event, then check for lottery time (events override lotteries)
                            //
                            if (event.equals("") && !lottery.equals("")) {  // lotteries are not in use before processing date/time

                                if (lstate == 2) {        // if lottery and ok to make lottery request - THIS OVERRIDES A WAIT LIST !!!!!

                                    out.println("<form action=\"Member_mlottery\" method=\"get\">");
                                    out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
                                    out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottery + "\">");
                                    out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");

                                } else {

                                    if (!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) {   // if no Wait List or WL not to be selected

                                        if (lstate == 5 && index <= days && noAccessAfterLottery == false) {  // lottery is done - normal request (lottery)

                                            out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
                                            out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");

                                        } else {      // not valid for members

                                            allow = false;
                                        }
                                    }
                                    /*                // no where to put this now ????  time will not be allowed if more than 4 lotteries defined (allow=false)
                                    } else {     // more than 4 lotteries in one day !!!
                                    
                                    //
                                    //  REMOVED BECAUSE TRANSITVALLEY IS FLOODING THE LOG WITH THIS MESSAGE
                                    //
                                    //String errorMsg = "Member_sheet: More than 4 lotteries defined for one day at " + club;    // build error msg
                                    //SystemUtils.logError(errorMsg);                           // log it
                                    
                                    out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
                                    }
                                     ///
                                }

                            } else {   // no active lottery for this tee time

                                lstate = 0;

                                //
                                //   lottery is cleared from teecurr2 after lottery completely processed (in dsheet) - check lottery_color for "no access" custom!!
                                //
                                if (!lottery_color.equals("") && noAccessAfterLottery == true) {   // if this time was part of a lottery and club wants to block members from changing it

                                    allow = false;                                                  // then block access

                                } else {

                                    if (!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) {   // if no Wait List or WL not to be selected

                                        if (mobile == 0) {

                                            out.println("<form action=\"Member_slot\" method=\"post\" name=\"mform" + j + "\" target=\"_top\">");

                                        } else {

                                            out.println("<form action=\"Member_slot\" method=\"post\" name=\"mform" + j + "\">");  // no target for mobile
                                        }
                                    }
                                }
                            }

                            //
                            //  Check if this is an empty tee time.  If so, indicate such for Member_slot so we know that the user is trying to select
                            //  a new tee time.
                            //
                            if (!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) {   // if no Wait List or WL not to be selected

                                if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("") && player5.equals("") && allow == true) {

                                    out.println("<input type=\"hidden\" name=\"newreq\" value=\"yes\">");
                                }
                            }
                        }        // end of if OK





                        //
                        //  If Hazeltine and more than 7 days in advance
                        //
                        if (club.equals("hazeltine") && index2 > 7) {

                            if ((allow == true) && (in_use == 0)) {         // if still ok
                                //
                                //  Custom processing for Hazeltine
                                //
                                //    Certain women are allowed to book times at specific times on Tuesdays and
                                //    Thursdays 14 days in advance (vs. 7 days normally).
                                //
                                //    If allow is still true, then they must have the right to edit tee times on this day.
                                //    We must limit access to the specified times for that day.
                                //
                                if (day_name.equals("Thursday")) {

                                    if (tee_time < 724 || tee_time > 930) {         // must be between 7:24 and 9:30

                                        allow = false;
                                    }
                                }
                                if (day_name.equals("Tuesday")) {

                                    allow = false;    // default to not allowed so both types can be checked

                                    if (msubtype.equals("After Hours") || msubtype.startsWith("AH")) {   // if 'After Hours' type

                                        if (tee_time > 1631 & tee_time < 1831) {    // must be between 4:32 and 6:30

                                            allow = true;
                                        }
                                    }
                                    if (msubtype.equals("9 Holer") || msubtype.equals("AH-9 Holer")
                                            || msubtype.equals("AH-9/18 Holer") || msubtype.equals("9/18 Holer")) {  // if '9 Holer' type

                                        if (tee_time > 723 && tee_time < 921) {    // must be between 7:24 and 9:20

                                            allow = true;
                                        }
                                    }
                                }
                            }
                        }

                        boolean showWLimg = false;

                        if (lstate != 2) {         // if lottery signup, then override wait list

                            if (waitlist && parmWaitLists.member_view_teesheet[WL_index] == 2) {

                                // fake as busy only if there is an existing signup covering this tee time
                                if (getWaitList.checkForSignups(wait_list_id, (int) date, tee_time, con)) {
                                    out.println("<!-- FOUND SIGNUP COVERING THIS TEETIME! wait_list_id=" + wait_list_id + ", date=" + date + ", tee_time=" + tee_time + " -->");
                                    in_use = 1;
                                    showWLimg = true;
                                }

                            } else if (waitlist && parmWaitLists.member_view_teesheet[WL_index] == 1) {

                                // fake all tee times as in use, (this option is for display the tee sheet but not allowing access)
                                in_use = 1;
                            }
                        }

                        // show the wait list button for upcoming tee times but not past ones
                        if (lstate != 2 && waitlist && parmWaitLists.member_view_teesheet[WL_index] == 0) { // && !(index2 == 0 && tee_time <= cal_time) ) {

                            WLbtn_displayed[WL_index] = true;       // do not show other times covered by this wait list

                            if (mobile == 0) {        // do not support wait list for mobile user (maybe in future)

                                out.println("<form action=\"Member_waitlist\" method=\"post\" target=\"_top\">");
                                out.println("<td align=\"center\">");
                                out.println("<input type=\"submit\" value=\"Wait List\" style=\"background-color:" + getWaitList.getColor(wait_list_id, con) + "\">");
                                out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
                                out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                                out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + courseName1 + "\">");
                                out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                                out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                                out.println("</td></form>");
                                if (parm.constimesm > 1) {
                                    out.println("<td align=\"center\">&nbsp</td>");
                                }
                            }
                            // suppress the player names
                            player1 = "";
                            player2 = "";
                            player3 = "";
                            player4 = "";
                            player5 = "";

                        } else if (allow == true && in_use == 0 && hideSubmit == 0) {         // if still ok

                            if (mobile == 0) {
                                out.println("<td align=\"center\">");
                                out.println("<font size=\"2\">");
                            } else {
                                // Mobile user
                                // out.println("<td>");  // moved to follow the tr above
                                out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                            }

                            out.println("<input type=\"hidden\" name=\"ttdata\" value=\"" + Utilities.encryptTTdata(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "|" + fb + "|" + user) + "\">");

                            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                            out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + courseName1 + "\">");
                            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                            out.println("<input type=\"hidden\" name=\"wasP1\" value=\"" + player1 + "\">");
                            out.println("<input type=\"hidden\" name=\"wasP2\" value=\"" + player2 + "\">");
                            out.println("<input type=\"hidden\" name=\"wasP3\" value=\"" + player3 + "\">");
                            out.println("<input type=\"hidden\" name=\"wasP4\" value=\"" + player4 + "\">");
                            out.println("<input type=\"hidden\" name=\"wasP5\" value=\"" + player5 + "\">");

                            if (fives != 0 && rest5.equals("")) {   // if 5-somes and not restricted

                                out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");  // tell _slot to do 5's
                            } else {
                                out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                            }

                            if (lstate == 2) {       // if ok for lottery request

                                if (club.equals("cherryhills") && (mship.equals("Resident Emeritus") || mship.equals("Non-Resident"))) {

                                    out.println("Lottery");       // do not allow these members to submit a Lottery Req

                                } else {

                                    //out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");

                                    if (lotteryText.equals("")) {
                                        out.println("<input type=\"submit\" value=\"Lottery\">");
                                    } else {                                    // members do not like the word Lottery - use something else
                                        out.println("<input type=\"submit\" value=\"Request Time\">");
                                    }
                                }

                            } else {

                                //
                                //  If Oakmont CC and Wed or Fri, or Milwaukee, and not a shotgun today, and this is a time when
                                //  multiple guests are allowed, make the submit button green.
                                //
                                if (oaktime == true || mcctime == true || beartime == true || congresstime == true) {

                                    out.println("<input type=\"submit\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" style=\"text-decoration:none; background:lightgreen\">");

                                } else {

                                    out.println("<input type=\"submit\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">"); //name=\"" + submit + "\" id=\"" + submit + "\"

                                }
                            }

                            if (mobile == 0 && allowMulti == false) {
                                out.println("</font>");
                            } else {
                                out.println("<input type=\"hidden\" name=\"contimes\" value=\"1\">");
                                out.println("</form>");                                                 // end form here
                            }
                            out.println("</td>");

                            //
                            //  if Consecutive Tee Times allowed, no lottery or event, and tee time is empty,
                            //   and F or B tee, allow member to select more than one tee time
                            //

                            // Custom for Fox Hill to override parm.constimesm on the weekdays
                            if (club.equals("hoxhill") && !day_name.equals("Saturday") && !day_name.equals("Sunday")) {

                                parm.constimesm = 4;
                            }

                            //
                            //  Governors Club - Determine # of consec times available to members based on course, day, and time.
                            //
                            if (club.equals("governorsclub")) {

                                if (courseNameT.equals("Primary Rotation")) {

                                    if (day_name.equals("Tuesday") || (day_name.equals("Wednesday") || day_name.equals("Thursday") || day_name.equals("Friday") || day_name.equals("Sunday"))) {

                                        if (tee_time >= 800 && tee_time <= 950) {

                                            if (dateShort <= 315 || dateShort >= 1101) {
                                                parm.constimesm = 2;
                                            } else {
                                                parm.constimesm = 1;
                                            }

                                        } else {
                                            parm.constimesm = 4;
                                        }

                                    } else if (day_name.equals("Saturday")) {

                                        if (dateShort <= 315 || dateShort >= 1101) {

                                            if (tee_time >= 800 && tee_time <= 950) {
                                                parm.constimesm = 2;
                                            } else {
                                                parm.constimesm = 4;
                                            }

                                        } else {

                                            if (tee_time >= 800 && tee_time <= 1030) {
                                                parm.constimesm = 1;
                                            } else {
                                                parm.constimesm = 4;
                                            }
                                        }
                                    }

                                } else if (courseNameT.equals("Secondary Rotation")) {
                                    parm.constimesm = 2;
                                }
                            }


                            //
                            //  If Mobile User, then do NOT allow consecutive times!!!!!!!
                            //
                            if (mobile > 0) {
                                parm.constimesm = 1;           // make sure this is still 1 after the customs
                            }


                            /*
                            //
                            //  Rivercrest Golf Club & Preserve (case 1492)
                            //
                            if (club.equals( "rivercrestgc" ) && threeSomeDay == true && tee_time < 1031) {
                            
                            skipconstimes = true;
                            }
                             //


                            //
                            //  Tahoe Donner (case 1845)
                            //
                            if (club.equals("tahoedonner") && (day_name.equals("Saturday") || day_name.equals("Sunday")) && tee_time < 1151) {

                                skipconstimes = true;
                            }



                            if (mobile == 0) {               // only if NOT mobile

                                if (parm.constimesm > 1) {           // if consecutive times allowed and ok for this tee time

                                    out.println("<td align=\"center\">");

                                    if (allow == true && player1.equals("") && player2.equals("") && player3.equals("")
                                            && player4.equals("") && player5.equals("")
                                            && event.equals("") && (lottery.equals("") || lstate > 4) && (fb == 0 || fb == 1) && skipconstimes == false) {

                                        out.println("<select size=\"1\" name=\"contimes\" onChange=\"document.mform" + j + ".submit()\">");
                                        if (club.equals("plantationpv")) {
                                            out.println("<option selected value=\"0\">0</option>");
                                            out.println("<option value=\"1\">1</option>");
                                        } else {
                                            out.println("<option selected value=\"1\">1</option>");
                                        }
                                        out.println("<option value=\"2\">2</option>");
                                        if (parm.constimesm > 2) {
                                            out.println("<option value=\"3\">3</option>");
                                        }
                                        if (parm.constimesm > 3) {
                                            out.println("<option value=\"4\">4</option>");
                                        }
                                        if (parm.constimesm > 4) {
                                            out.println("<option value=\"5\">5</option>");
                                        }
                                        out.println("</select>");

                                        //  add hidden time parm so value passed when this item is selected
                                        //
                                        out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">");

                                    } else {

                                        out.println("&nbsp;");
                                        out.println("<input type=\"hidden\" name=\"contimes\" value=\"1\">");
                                    }
                                    out.println("</td>");

                                } else {

                                    if (club.equals("governorsclub")) {
                                        out.println("<td align=\"center\">&nbsp;");
                                        out.println("<input type=\"hidden\" name=\"contimes\" value=\"1\">");
                                        out.println("</td>");
                                    } else {
                                        out.println("<input type=\"hidden\" name=\"contimes\" value=\"1\">");
                                    }
                                }
                                out.println("</form>");
                            }

                        } else {        // not wait list and not ok to access

                            if (mobile == 0) {
                                out.println("<td align=\"center\">");
                                out.println("<font size=\"2\">");
                                // } else {
                                // out.println("<td>");      // let style sheet dictate color, etc. (moved to tr above)
                            }

                            if (type == shotgun) {

                                String shotgunLabel = "";

                                eventloop1:
                                for (i = 0; i < count_event; i++) {

                                    if (event.equals(eventA[i])) {

                                        shotgunLabel = SystemUtils.getSimpleTime(act_hrA[i], act_minA[i]);
                                        break eventloop1;
                                    }
                                }

                                i = 0;       // reset index

                                if (!shotgunLabel.equals("")) {       // if time gathered above

                                    StringTokenizer toks = new StringTokenizer(shotgunLabel, " ");     // delimiters are space (8:30 AM)

                                    shotgunLabel = toks.nextToken();                             // get the time only (8:30)
                                }

                                shotgunLabel += " Shotgun";            // i.e.  9:30 Shotgun

                                out.println(shotgunLabel);

                            } else {

                                if (showWLimg) {
                                    if (mobile == 0) {       // do not allow user to enter wait list if mobile user
                                        out.print("<a href=\"javascript: void(0)\" onclick=\"top.location.href='Member_waitlist?waitListId=" + parmWaitLists.id[WL_index] + "&date=" + date + "&day=" + day_name + "&index=" + num + "&returnCourse=" + courseName1 + "&course=" + parmWaitLists.courseName[WL_index] + "'\" title='Access Wait List'>");
                                        out.print("<img src='/" + rev + "/images/WL.gif' height='13' width='26' alt='Access Wait List' border='0'>");
                                        out.print("</a>&nbsp;");
                                    } else {
                                        out.print("Wait List<BR>");
                                    }
                                }
                                out.print(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);

                            }
                            if (mobile == 0) {
                                out.println("</font></td>");
                            } else {
                                out.println("</td>");
                            }

                            if (parm.constimesm > 1 || club.equals("governorsclub")) {
                                out.println("<td align=\"center\">");
                                out.println("&nbsp;");
                                out.println("</td>");
                            }

                        }           // end of time column and possible constimes col



                        //
                        //  Check for Mobile user
                        //
                        if (mobile == 0) {

                            //
                            //  Standard Device - Add Course Name
                            //
                            if (multi != 0) {        // case 1509

                                if (courseName1.equals("-ALL-")) {

                                    ci = course.indexOf(courseNameT);     // get the index value of this course

                                    if (ci < colorMax) {                  // if color defined for this course - use it

                                        out.println("<td bgcolor=\"" + colors.course_color[ci] + "\" align=\"center\">");     // use course color

                                    } else {

                                        out.println("<td bgcolor=\"white\" align=\"center\">");
                                    }

                                } else {
                                    out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                                }

                                out.println("<font size=\"2\">");
                                out.println(courseNameT);
                                out.println("</font></td>");
                            }
                        }         // end of IF NOT Mobile

                        //
                        //  Front/Back indicator
                        //
                        if (mobile == 0) {

                            // if (club.equals("oakleycountryclub") && sfb.equals("B")) {          // if Oakley CC and a Back Tee
                            if (sfb.equals("B")) {                                              // if Back Tee
                                out.println("<td bgcolor=\"yellow\" align=\"center\">");         // highlight it
                            } else {
                                out.println("<td bgcolor=\"white\" align=\"center\">");
                            }
                            out.println("<font size=\"2\">");
                            out.println((!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) ? sfb : "&nbsp;");
                            out.println("</font></td>");
                        } else {
                            // out.println("<td align=\"center\" style=\"background-color:#F5F5DC\">");
                            out.println("<td>");     // let css file dictate color, etc.
                            out.println((!waitlist || (waitlist && parmWaitLists.member_view_teesheet[WL_index] != 0)) ? sfb : "&nbsp;");
                            out.println("</td>");
                        }


                        //
                        //  Sonnenalp Custom - add guest fees to guest names if they exist (no 5-somes!!)
                        //
                        if (club.equals("sonnenalp")) {           // if Sonnenalp

                            if (g1 != 0 && !custom_disp1.equals("")) {    // if guest and rate was found

                                player1 = player1 + " " + custom_disp1;
                            }
                            if (g2 != 0 && !custom_disp2.equals("")) {    // if guest and rate was found

                                player2 = player2 + " " + custom_disp2;
                            }
                            if (g3 != 0 && !custom_disp3.equals("")) {    // if guest and rate was found

                                player3 = player3 + " " + custom_disp3;
                            }
                            if (g4 != 0 && !custom_disp4.equals("")) {    // if guest and rate was found

                                player4 = player4 + " " + custom_disp4;
                            }
                        }
                        
                        String p = "";

                        oldbgcolor = bgcolor;


                        //
                        //  Check for Mobile user
                        //
                        if (mobile == 0) {

                            //
                            //  Add Player 1
                            //
                            if (club.equals("southview") || club.equals("dellwood")) {
                                if (player1.startsWith("Join Me")) {
                                    bgcolor = "lime";
                                } else {
                                    bgcolor = oldbgcolor;
                                }
                            }
                            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                            out.println("<font size=\"2\">");

                            if (!player1.equals("")) {

                                player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g1, hideG, hndcp1, ghin1, player1, guestLabel, memberLabel, club);

                                out.println(player);

                            } else {     // player is empty

                                out.println("&nbsp;");
                            }
                            out.println("</font></td>");

                            if ((!player1.equals("")) && (!player1.equalsIgnoreCase("x"))) {
                                out.println("<td bgcolor=\"white\" align=\"center\">");
                                out.println("<font size=\"1\">");
                                out.println(p1cw);
                            } else {
                                out.println("<td bgcolor=\"white\" align=\"center\">");
                                out.println("<font size=\"2\">");
                                out.println("&nbsp;");
                            }
                            out.println("</font></td>");

                            //
                            //  Add Player 2
                            //
                            if (club.equals("southview") || club.equals("dellwood")) {
                                if (player2.startsWith("Join Me")) {
                                    bgcolor = "lime";
                                } else {
                                    bgcolor = oldbgcolor;
                                }
                            }
                            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                            out.println("<font size=\"2\">");

                            if (!player2.equals("")) {

                                player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g2, hideG, hndcp2, ghin2, player2, guestLabel, memberLabel, club);

                                out.println(player);

                            } else {     // player is empty

                                out.println("&nbsp;");
                            }
                            out.println("</font></td>");

                            if ((!player2.equals("")) && (!player2.equalsIgnoreCase("x"))) {
                                out.println("<td bgcolor=\"white\" align=\"center\">");
                                out.println("<font size=\"1\">");
                                out.println(p2cw);
                            } else {
                                out.println("<td bgcolor=\"white\" align=\"center\">");
                                out.println("<font size=\"2\">");
                                out.println("&nbsp;");
                            }
                            out.println("</font></td>");

                            if (club.equals("mayfieldsr") && courseNameT.equalsIgnoreCase("sand ridge") && verifyCustom.checkMayfieldSR(date, tee_time, day_name)) {

                                out.println("<td></td><td></td>");       // blank player 3
                                out.println("<td></td><td></td>");       // blank player 4
                                out.println("<td></td><td></td>");       // blank player 5

                            } else {
                                //
                                //  Add Player 3
                                //
                                if (club.equals("southview") || club.equals("dellwood")) {
                                    if (player3.startsWith("Join Me")) {
                                        bgcolor = "lime";
                                    } else {
                                        bgcolor = oldbgcolor;
                                    }
                                }
                                out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                                out.println("<font size=\"2\">");

                                if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                                    out.println("X");            // 2-somes ONLY

                                } else {                  // not Piedmont special time

                                    if (!player3.equals("")) {

                                        player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g3, hideG, hndcp3, ghin3, player3, guestLabel, memberLabel, club);

                                        out.println(player);

                                    } else {     // player is empty

                                        out.println("&nbsp;");
                                    }
                                }
                                out.println("</font></td>");

                                if ((!player3.equals("")) && (!player3.equalsIgnoreCase("x"))) {
                                    out.println("<td bgcolor=\"white\" align=\"center\">");
                                    out.println("<font size=\"1\">");
                                    out.println(p3cw);
                                } else {
                                    out.println("<td bgcolor=\"white\" align=\"center\">");
                                    out.println("<font size=\"2\">");
                                    out.println("&nbsp;");
                                }
                                out.println("</font></td>");

                                //
                                //  Add Player 4
                                //
                                if (club.equals("southview") || club.equals("dellwood")) {
                                    if (player4.startsWith("Join Me")) {
                                        bgcolor = "lime";
                                    } else {
                                        bgcolor = oldbgcolor;
                                    }
                                }
                                out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                                out.println("<font size=\"2\">");

                                if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                                    out.println("X");            // 2-somes ONLY

                                } else {                  // not Piedmont special time

                                    if (!player4.equals("")) {

                                        player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g4, hideG, hndcp4, ghin4, player4, guestLabel, memberLabel, club);

                                        out.println(player);

                                    } else {     // player is empty

                                        out.println("&nbsp;");
                                    }
                                }
                                out.println("</font></td>");

                                if ((!player4.equals("")) && (!player4.equalsIgnoreCase("x"))) {
                                    out.println("<td bgcolor=\"white\" align=\"center\">");
                                    out.println("<font size=\"1\">");
                                    out.println(p4cw);
                                } else {
                                    out.println("<td bgcolor=\"white\" align=\"center\">");
                                    out.println("<font size=\"2\">");
                                    out.println("&nbsp;");
                                }
                                out.println("</font></td>");

                                //
                                //  Add Player 5 if supported
                                //
                                if (fivesALL != 0) {        // if 5-somes supported on any course

                                    if (fives != 0) {        // if 5-somes on this course

                                        if (!rest5.equals("") && event.equals("")) {       // if 5-somes are restricted & not an event

                                            out.println("<td bgcolor=\"" + bgcolor5 + "\" align=\"center\">");
                                            out.println("<font size=\"2\">");
                                            if (!player5.equals("") && club.equals("championhills")) {
                                                player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g5, hideG, hndcp5, ghin5, player5, guestLabel, memberLabel, club);
                                                out.println(player);
                                            } else {
                                                out.println("&nbsp;");
                                            }

                                        } else {        // no 5-some rest or this is an event time

                                            if (club.equals("southview") || club.equals("dellwood")) {
                                                if (player5.startsWith("Join Me")) {
                                                    bgcolor = "lime";
                                                } else {
                                                    bgcolor = oldbgcolor;
                                                }
                                            }

                                            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                                            out.println("<font size=\"2\">");

                                            if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                                                out.println("X");            // 2-somes ONLY

                                            } else {                  // not Piedmont special time

                                                if (!player5.equals("")) {

                                                    player = buildPlayer(hideGuestCC, disp_hndcp, hideN, g5, hideG, hndcp5, ghin5, player5, guestLabel, memberLabel, club);

                                                    out.println(player);

                                                } else {     // player is empty

                                                    out.println("&nbsp;");
                                                }
                                            }
                                        }
                                        out.println("</font></td>");

                                        if ((!player5.equals("")) && (!player5.equalsIgnoreCase("x"))) {
                                            out.println("<td bgcolor=\"white\" align=\"center\">");
                                            out.println("<font size=\"1\">");
                                            out.println(p5cw);
                                        } else {
                                            out.println("<td bgcolor=\"white\" align=\"center\">");
                                            out.println("<font size=\"2\">");
                                            out.println("&nbsp;");
                                        }
                                        out.println("</font></td>");

                                    } else {         // 5-somes supported on at least 1 course, but not this one (if course=ALL)

                                        out.println("<td bgcolor=\"black\" align=\"center\">");   // no 5-somes
                                        out.println("<font size=\"2\">");
                                        out.println("&nbsp;");
                                        out.println("</font></td>");
                                        out.println("<td bgcolor=\"black\" align=\"center\">");
                                        out.println("<font size=\"2\">");
                                        out.println("&nbsp;");
                                        out.println("</font></td>");
                                    }
                                }
                            }


                        } else {

                            //
                            //  Mobile User - display all players in one cell
                            //
                            out.println("<td style=\"background-color:" + bgcolor + "\">");

                            if (player1.equals("") && player2.equals("") && player3.equals("") && player4.equals("") && player5.equals("")) {

                                out.println("&nbsp");

                            } else {

                                StringBuffer playerBfr = new StringBuffer();       // get buffer for players

                                //   Add Player 1
                                if (!player1.equals("")) {

                                    player = buildPlayerMobile(hideGuestCC, hideN, g1, hideG, player1, guestLabel, memberLabel, club);

                                    playerBfr.append(player);

                                }

                                //   Add Player 2
                                if (!player2.equals("")) {

                                    playerBfr.append("<BR>");             // add seperator

                                    player = buildPlayerMobile(hideGuestCC, hideN, g2, hideG, player2, guestLabel, memberLabel, club);

                                    playerBfr.append(player);
                                }

                                //   Add Player 3
                                if (!player3.equals("")) {

                                    playerBfr.append("<BR>");             // add seperator

                                    player = buildPlayerMobile(hideGuestCC, hideN, g3, hideG, player3, guestLabel, memberLabel, club);

                                    playerBfr.append(player);
                                }

                                //   Add Player 4
                                if (!player4.equals("")) {

                                    playerBfr.append("<BR>");             // add seperator

                                    player = buildPlayerMobile(hideGuestCC, hideN, g4, hideG, player4, guestLabel, memberLabel, club);

                                    playerBfr.append(player);
                                }

                                if (fives != 0 && rest5.equals("")) {    // if 5-somes on this course

                                    //   Add Player 5
                                    if (!player5.equals("")) {

                                        playerBfr.append("<BR>");             // add seperator

                                        player = buildPlayerMobile(hideGuestCC, hideN, g5, hideG, player5, guestLabel, memberLabel, club);

                                        playerBfr.append(player);
                                    }
                                }

                                out.println(playerBfr.toString());     // add the players
                            }

                            out.println("</td>");


                        }         // end of IF Mobile User


                        out.println("</tr>");       // end of this row

                    }  // end of IF blocker or Lottery test

                }  // end of while loop1

                pstmt.close();

                out.println("</table>");                   // end of tee sheet table

                if (mobile > 0) {

                    out.println("</div>");
                    out.println("<div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)

                } else {

                    out.println("</td></tr>");
                    out.println("</table>");                   // end of main page table
                }


                //
                //  End of HTML page
                //
                out.println("</body></html>");
                out.close();
                if (Gzip == true) {
                    resp.setContentLength(buf.size());                 // set output length
                    resp.getOutputStream().write(buf.toByteArray());
                }

*/
            }
            // Old skin ends here

            // Common to old and new skin starts here

        } catch (Exception e1) {

            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<BR><BR><H2>Database Access Error</H2>");
            out.println("<BR><BR>Unable to access the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>" + e1.getMessage());
            out.println("<BR><BR>" + e1.toString());
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</BODY></HTML>");
            out.close();
            if (Gzip == true) {
                resp.setContentLength(buf.size());                 // set output length
                resp.getOutputStream().write(buf.toByteArray());
            }
        }

    }  // end of doPost

    // *********************************************************
    //  Display event information in new pop-up window
    // *********************************************************
    
    public void displayEvent(String name, String club, PrintWriter out, Connection con) {

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

            PreparedStatement stmt = con.prepareStatement(
                    "SELECT * FROM events2b "
                    + "WHERE name = ?");

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
            out.println(SystemUtils.HeadTitle("Member Event Information"));
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

            if (!course.equals("")) {

                out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            }

            out.println("<b>Front/Back Tees:</b>&nbsp;&nbsp; " + fb + "<br><br>");

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

            if (signUp != 0) {       // if members can sign up

                if (c_min < 10) {
                    out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":0" + c_min + " " + c_ampm + " on " + c_month + "/" + c_day + "/" + c_year);
                } else {
                    out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":" + c_min + " " + c_ampm + " on " + c_month + "/" + c_day + "/" + c_year);
                }
                out.println("<br><br>");
                out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");

                out.println("<br>");

                out.println("<form action=\"Member_events2\" method=\"post\">");
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"0\">");
                //out.println("To register for this event click on the <b>Events</b> tab after closing this window.<br>");
                out.println("To register for this event, <input type=\"submit\" name=\"teesheet\" value=\"Click Here\"><br>");
                out.println("</form>");

            } else {

                if (!club.equals("inverness")) {          // if NOT Inverness

                    out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");
                    out.println("<br><br>");
                }
                out.println("Online sign up was not selected for this event.");
            }
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            //
            //  End of HTML page
            //
            out.println("<p align=\"center\"><br><form>");
            out.println("<input type=\"button\" value=\"CLOSE\" onClick='self.close();'>");
            out.println("</form></p>");
            out.println("</font></td>");
            out.println("</tr></table>");
            out.println("</center></font></body></html>");
            out.close();

        } catch (Exception exc) {
            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Database Access Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
            out.println("<BR>Error:" + exc.getMessage());
            out.println("<BR><BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
            out.println("<br><br><form>");
            out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
            out.println("</form>");
            out.println("</center></font></body></html>");
            out.close();
        }
    }  // end of displayEvent

    // *********************************************************
    //  Create the string to display for the player value passed
    // *********************************************************
    public String buildPlayer(boolean hideGuestCC, boolean disp_hndcp, int hideN, int g1, int hideG, float hndcp,
            String ghin, String player, String guestLabel, String memberLabel, String club) {


        String playerDisplay = "";
        String hndcps = "";

        //
        //  Build the player value to be displayed based on the input
        //
        if (player.equalsIgnoreCase("x")) {   // if 'x'

            playerDisplay = "X";

        } else {       // not 'x' or guest

            if (hideN == 0) {             // if ok to display names

                if (g1 != 0) {             // if guest

                    if (hideG == 1) {        // if custom to hide guest names

                        playerDisplay = guestLabel;

                    } else {

                        if (hideGuestCC == true) {        // if ClubCorp custom to hide guest types

                            player = stripGuestType(player, club);      // remove the guest type - just show name if corp guest
                        }

                        playerDisplay = player;
                    }

                } else {                     // must be a member

                    if (club.equals("lagcc")) {
                        player = Utilities.swapNameOrder(club, player);
                    }

                    if (disp_hndcp == false) {
                        playerDisplay = player;
                    } else {
                        
                        if ((hndcp == 99) || (hndcp == -99)) {
                            playerDisplay = player + "  NH";
                        } else {
                            if (hndcp < 0) {
                                hndcp = 0 - hndcp;                       // convert to non-negative
                                
                                hndcps = String.valueOf(hndcp);
                            } else {
                                hndcps = "+" + hndcp;
                            }
                            // COMMENTED OUT ROUND 2-3-09 FOR DIABLOCC
                            //hndcp = Math.round(hndcp1);                   // round it off
                            playerDisplay = player + " " + hndcps;
                            if (club.equals("cherryhills") && playerDisplay.endsWith(".0")) {
                                playerDisplay = playerDisplay.substring(0, playerDisplay.length() - 2);
                            }
                        }
                    }

                    if (club.equals("cwcpga") && !ghin.equals("")) {
                        playerDisplay += " " + ghin;
                    }
                }

            } else {                          // do not display member names

                if (g1 != 0) {               // if guest

                    playerDisplay = guestLabel;

                } else {                    // must be a member

                    playerDisplay = memberLabel;
                }
            }
        }

        return (playerDisplay);

    }  // end of buildPlayer

    // *********************************************************
    //  Create the string to display for the player value passed (for Mobile devices)
    // *********************************************************
    public String buildPlayerMobile(boolean hideGuestCC, int hideN, int g1, int hideG,
            String player, String guestLabel, String memberLabel, String club) {


        String playerDisplay = "";

        //
        //  Build the player value to be displayed based on the input
        //
        if (player.equalsIgnoreCase("x")) {   // if 'x'

            playerDisplay = "X";

        } else {       // not 'x' or guest

            if (hideN == 0) {             // if ok to display names

                if (g1 != 0) {             // if guest

                    if (hideG == 1) {        // if custom to hide guest names

                        playerDisplay = guestLabel;

                    } else {

                        if (hideGuestCC == true) {        // if ClubCorp custom to hide guest types

                            player = stripGuestType(player, club);      // remove the guest type - just show name if corp guest
                        }

                        playerDisplay = player;
                    }

                } else {                     // must be a member

                    playerDisplay = player;
                }

            } else {                           // do not display member names

                if (g1 != 0) {               // if guest

                    playerDisplay = guestLabel;

                } else {                     // must be a member

                    playerDisplay = memberLabel;
                }
            }
        }

        return (playerDisplay);

    }  // end of buildPlayerMobile

    // *********************************************************
    //  ClubCorp or Cordillera - strip the guest type from player
    // *********************************************************
    public String stripGuestType(String player, String club) {


        String name = "";
        String skip = "";
        String token1 = "";

        int count = 0;


        //
        //  Determine value and number of tokens in the Guest Type to strip
        //

        /*   Timarron changed their minds
        if (club.equals("timarroncc")) {
        
        if (player.startsWith ("Sig Gold")) {
        
        token1 = "Sig Gold";
        count = 2;
        }
        
        if (player.startsWith ("Society")) {
        
        token1 = "Society";
        count = 1;
        }
        
        if (player.startsWith ("Prospective Member")) {
        
        token1 = "Prospective Member";
        count = 2;
        }
        
        if (player.startsWith ("Gift Certificate")) {
        
        token1 = "Gift Certificate";
        count = 2;
        }
        
        if (player.startsWith ("Employee")) {
        
        token1 = "Employee";
        count = 1;
        }
        
        if (player.startsWith ("Comp")) {
        
        token1 = "Comp";
        count = 1;
        }
        
        } else {
         */


        if (club.equals("cordillera")) {

            //
            //   Cordillera - remove the Hotel guest types
            //
            if (player.startsWith("Lodge")) {

                token1 = "Lodge";
                count = 1;
            }


        } else if (club.equals("morganrun")) {

            //
            //   Morgan Run - remove the pro-only guest types
            //
            if (player.startsWith("Hotel")) {

                token1 = "Hotel";
                count = 1;
            }
            if (player.startsWith("Signature Gold")) {

                token1 = "Signature Gold";
                count = 2;
            }
            if (player.startsWith("Outing")) {

                token1 = "Outing";
                count = 1;
            }
            if (player.startsWith("Comp")) {

                token1 = "Comp";
                count = 1;
            }
            if (player.startsWith("Employee")) {

                token1 = "Employee";
                count = 1;
            }
            if (player.startsWith("Junior")) {

                token1 = "Junior";
                count = 1;
            }
            if (player.startsWith("Sponsored Gst")) {

                token1 = "Sponsored Gst";
                count = 2;
            }
            if (player.startsWith("Society")) {

                token1 = "Society";
                count = 1;
            }
            if (player.startsWith("Gift Certificate")) {

                token1 = "Gift Certificate";
                count = 2;
            }
            if (player.startsWith("Sig Gold Guest")) {

                token1 = "Sig Gold Guest";
                count = 3;
            }
            if (player.startsWith("Prospective Member")) {

                token1 = "Prospective Member";
                count = 2;
            }

        } else if (Utilities.isClubInGroup(club, "AGC") || club.equals("dovecanyonclub")) {     // American Golf

            if (player.startsWith("Platinum")) {

                token1 = "Platinum";
                count = 1;
            }

        } else if (club.equals("tetonpines")) {     // Teton Pines
          
            if (player.startsWith("Resort")) {

                token1 = "Resort";
                count = 1;
            }
            
        } else {


            //
            //  Seville
            //
            if (player.startsWith("Replay")) {

                token1 = "Replay";
                count = 1;
            }

            if (player.startsWith("Outing")) {

                token1 = "Outing";
                count = 1;
            }

            if (player.startsWith("Courtesy")) {

                token1 = "Courtesy";
                count = 1;
            }


            if (player.startsWith("Unacc GST")) {

                token1 = "Unacc GST";
                count = 2;
            }

            if (player.startsWith("Recip GST")) {

                token1 = "Recip GST";
                count = 2;
            }

            if (player.startsWith("Sev Emp")) {

                token1 = "Sev Emp";
                count = 2;
            }

            if (player.startsWith("Sev Fam")) {

                token1 = "Sev Fam";
                count = 2;
            }

            if (player.startsWith("AZS GST")) {

                token1 = "AZS GST";
                count = 2;
            }

            if (player.startsWith("SG GST")) {

                token1 = "SG GST";
                count = 2;
            }

            if (player.startsWith("SG Member")) {

                token1 = "SG Member";
                count = 2;
            }
        }

        //
        //  If none of the above, then do not strip.  Else strip off the guest type value to expose just the name.
        //
        if (!token1.equals("") && count > 0) {

            StringTokenizer tok = new StringTokenizer(player, " ");      // delimiter is space - token1 does not work!!!!

            if (tok.countTokens() > 0) {                                   // name provded ?

                if (count == 1) {                              // single word guest type?

                    skip = tok.nextToken();                     // skip past guest type value

                } else {

                    skip = tok.nextToken();                     // skip past guest type value
                    skip = tok.nextToken();
                }

                while (tok.countTokens() > 0) {              // get name

                    skip = tok.nextToken();                     // rebuild the name

                    name = name + " " + skip;
                }
            }

            if (name.equals("") || name.equals(" ") || name == null) {       // if name not included

                name = "Guest";
            }

        } else {

            name = player;          // return full name if not a guest type
        }

        return (name);

    }    // end of stripGuestType

    public String buildLegendItem(String code, String desc) {

        String result = "";
        result = "<span class=\"code_legend_item code_legend_" + code + "\"><span>" + code + "</span> = <span>" + desc + "</span></span>";


        return result;

    }

    public String buildLegendLinkItem(String type, String color, String desc, String data) {

        String result = "";
        String tags = "";
        if (data != null) {
            tags += " data-ftjson=\"" + data + "\"";
        }
        if (type != null) {
            tags += " class=\"standard_button button_legend_" + type + "\"";
        }
        if (color != null) {
            tags += " style=\"background-color:" + color + ";\"";
        }
        if (desc != null) {
            result = "<a href=\"#\"" + tags + ">" + desc + "</a>";
        }

        return result;

    }

    public String buildLegendColorItem(String type, String color, String desc) {

        String result = "";

        result = "<span class=\"color_legend_item color_legend_" + type + "\" style=\"background-color:" + color + ";\">" + desc + "</span>";

        return result;

    }

    public String getBgColor(String bgcolor) {

        if (bgcolor.equalsIgnoreCase("#F5F5DC")) {
            return "";
        } else {
            return " style=\"background-color:" + bgcolor + ";\"";
        }

    }
}
