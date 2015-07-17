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
 *   created:  6/09/2006   Bob P.
 *
 *
 *   last updated:
 *
 *      3/14/14  Olympic Club (olyclub) - Updated checkOlyStarterTime() custom to free up some requested times.
 *      3/07/14  Minikahda (minikahda) - Adjusted the guest custom in checkMinikahdaGuests for 2014 (case 1027).
 *      3/07/14  Bay Hill Club (bayhill) - Updated custom to properly require 2 players (was requiring 3 accidentally) (case 2381).
 *      3/07/14  Philly Cricket Club (philcricket) - Updated custom message verbiage as per the club's request (case 2349).
 *      3/06/14  Philly Cricket Club (philcricket) - Added custom to only allow 3 "Golf-in-Waiting B" members per day, per course, to be on the tee sheet at any given time (case 2349).
 *      3/06/14  Bay Hill Club (bayhill) - Added custom to require that all tee times booked between 12/1 and 4/30 contain at least 2 players (X's included) (case 2381).
 *      3/03/14  Pine Orchard Y & CC (poycc) - Added custom to require that members have at least 3 players in Sat/Sun/Holiday tee times from 5/1 - 9/30 between 6:30am and 10:30am (case 2371).
 *      2/28/14  Oakmont CC (oakmont) - Updated guest limit custom to adjust one of the date values from last year (case 2223).
 *      2/27/14  Added checkEventCategoryCounts for use with a Merion GC (merion) custom, but set up to be used for other clubs if needed (case 2369).
 *      2/21/14  Brookridge GF (brookridgegf) - Updated checkRestLift custom so that it's properly not opening until 8 on days when it's supposed to (case 2311).
 *      2/18/14  Brookridge GF (brookridgegf) - Updated checkRestLift custom with dates & times for 2014 (case 2311).
 *      1/16/14  Ballen Isles CC (ballenisles) - Added generateLottAssignsFromEvent method to generate lottery assignments for all registered members of an event (case 2347).
 *      1/08/14  Plantation CC (plantationcc) - Added custom to checkRestLift to lift all 'Short Notice Tee Times' restrictions 1 day in advance at 7:00am MT (case 2339).
 *     12/23/13  Olympic Club (olyclub) -Made a couple additional tweaks for MLK and Presidents day.
 *     12/20/13  Desert Forest GC (desertforestgolfclub) - Updated twosome time custom to no longer apply to Tuesday and Thursday (case 1694).
 *     12/20/13  Olympic Club (olyclub) - Updated checkOlyStarterTime to have the proper 2014 dates for MLK Jr day and Presidents Day.
 *     12/16/13  Mayfield Sand Ridge (mayfieldsr) - Updated checkMayfieldSR custom with new date range and adjustment of time range for 2014.
 *     12/09/13  Cape Cod National (capecodnational) - Updated guest custom with additional periods for the in-season weekend period (case 1828).
 *     11/19/13  Brookridge GF (brookridgegf) - Slight change to custom to tidy up conditional statement (case 2311).
 *     11/05/13  Brookridge GF (brookridgegf) - Updated checkRestLift custom to release the restriction later during the week from 11/1-2/28 each year (case 2311).
 *     10/22/13  Olympic Club (olyclub) - Updated checkOlyStarterTime() to always apply the Thanksgiving time settings to the day after Thanksgiving, as well.
 *     10/17/13  Quechee Club Tennis (quecheeclubtennis) - Added custom to checkSubtypeActivities() to restrict members from certain activities based on privileges indicated in their member subtype.
 *     10/17/13  Updated checkPhilCricketActivities() method to be named checkSubtypeActivities() instead, so it can be used for other clubs as well.
 *     10/10/13  Tamarisk CC (tamariskcc) - Updated checkTamariskAdvTime() to not apply to their custom to the opening day of their season (11/8/2013) (case 1657). 
 *     10/10/13  Olympic Club (olyclub) - Updated checkOlyStarterTime() custom to run the seasons based on Daylight Savings Time instead of specific dates, and updated Winter Season Tu-Fri times.
 *     10/09/13  Ridge CC (ridgecc) - Added custom to prevent Social members from booking tee times with guests outside of Sat/Sun after 2:30 pm (case 2309).
 *     10/08/13  Brookridge GF (brookridgegf) - Added custom to checkRestLift to move their "Day of Tee Times" restriction on the day of at 7:00am CT (case 2311).
 *     10/03/13  Portland GC (portlandgc) - Updated checkPGCwalkup() to remove the 4/30/2013 conditional and instead apply the time values associated with that at all times.
 *      9/28/13  Olympic Club (olyclub) - Updated checkOlyStarterTime to adjust the times for DST on the Lake Course.
 *      9/27/13  Wildcat Run CC (wildcatruncc) - Updated custom to prevent "Gated Community Preview" members from booking tee times with guests until 5 days in advance at 7am (case 2044).
 *      9/26/13  Walpole CC (walpolecc) - Added custom to require at least 2 players on Friday 11:00am-1:30pm, and Sat/Sun prior to 3:00pm (case 2304).
 *      9/25/13  Awbrey Glen CC (awbreyglen) - Fixed issue with checkAwbreyGlenMnumRoundCounts() method that was causing it to count past rounds that were played under different mships (case 2186).
 *      9/12/13  Imperial GC (imperialgc) - Updated custom winter round restrictions to start on 12/1 instead of 11/1 (case 2238).
 *      8/27/13  Misquamicut Club - adjust their 2-some times in checkMisquamicut2someTimes.
 *      8/27/13  Mayfiled Sandridge - Added one more day (9/07) to the 2-some times in checkMayfieldSR.
 *      8/26/13  Los Coyotes (loscoyotes) - Add Friday 7:52 time to checkLosCoyotesTimes.
 *      8/22/13  The Country Club (tcclub) - Chenaged 2-some times for Sept and Oct.
 *      8/21/13  Saticoy CC (saticoycountryclub) - Add custom to checkRestLift to lift Tues restriction on Fri morning (case 2292).
 *      8/12/13  Champions Run (championsrun) - Added custom to prevent dependents from being in a tee time without an adult from 7am-12pm Sat/Sun, 4/1 - 9/30 each year (case 2276).
 *      8/08/13  Lakewood CC - Dallas (lakewoodccdallas) - Added custom to prevent members from booking one-somes and two-somes during certain periods on Fri/Sat/Sun (case 2262).
 *      7/30/13  Olympic Club (olyclub) - Updated checkOlyStarterTime to adjust the times for Labor Day.
 *      7/22/13  Mayfield Sand Ridge (mayfieldsr) - Updated checkMayfieldSR to include the 6:50 an 6:55 tee times as 2-some times, when present.
 *      7/15/13  Mid Pacific (midpacific) - Added custom to restrict spouses to playing within 7 days in advance. If within that range, request is passed along to their normal custom processing.
 *      7/12/13  Elgin CC (elgincc) - Fixed issue with advance time custom that was getting hung up by index values passed from the tee time calendar/list/search (case 2226).
 *      7/08/13  Sonnenalp GC (sonnenalp) - Updated checkSonnenalp3someTimes() to not apply the custom on 7/23/13 (case 1978).
 *      6/21/13  Pine Brook CC (pinebrookcc) - Fixed issue with checkPineBrookNonIndoorTimes() custom that was causing blank player slots to cause the restriction to hit (case 2028).
 *      6/18/13  The Country Club (tcclub) - Updated checkTheCC() 2-some time custom to apply to Fridays as well.
 *      6/17/13  Awbrey Glen CC (awbreyglen) - Fixed issues with checkAwbreyGlenMnumRoundCounts() method that were causing the method to return an incorrect round count (case 2186).
 *      6/17/13  Riverton CC (rivertoncc) - Added checkRivertonCCMnumRoundCounts() custom method to restrict House members to 10 rounds from 4/1-10/31 each year (case 2270).
 *      6/14/13  Pine Brook CC (pinebrookcc) - Updated checkPineBrookNonIndoorTimes() custom to accommodate their new mship values (case 2028).
 *      6/11/13  Minnetonka CC (minnetonka) - Updated 3-some custom to not apply Thursday processing on 6/13/13 or 6/20/13 (case 1989).
 *      6/11/13  Oakmont CC (oakmont) - Updated guest limit custom to allow unlimited advance guest times once 6/1 has pased (case 2223).
 *      6/10/13  Brooklawn CC (brooklawn) - Added custom to require tee times to have at least 3 players (not including X's) between 10:28am - 2:00pm from 5/22 through 9/4 on Sat/Sun (case 2266).
 *      6/10/13  Green Acres CC (greenacrescountryclub) - Added custom to restrict 5-some times to including no more than 1 guest (case 2268).
 *      6/10/13  Minneapolis GC (minneapolis) - Added custom to restrict 5-some times to including no more than 2 guests (case 2272).
 *      6/07/13  Elgin CC (elgincc) - Removed portion of custom that was restricting members to a total of 7 guests across advance times at any given time (case 2226).
 *      6/04/13  Added checkPhilcricketRecip for Philly Cricket's recip site - set limits on member play per course.
 *      6/03/13  Olympic Club (olyclub) - Updated checkOlyStarterTime to make some tweaks to the 7/5 Lake Course starter times.
 *      6/03/13  Interlachen CC (interlachen) - Updated custom to not apply to Sundays (case 2267).
 *      5/30/13  Olympic Club (olyclub) - Updated checkOlyStarterTime to add 7/5 to the holiday/Saturday schedule for the Ocean course.
 *      5/29/13  Interlachen CC (interlachen) - Updated verbiage in custom message and updated custom to only check occupied player slots (case 2267).
 *      5/28/13  Dorset FC (dorsetfc) - Updated checkDorsetFC() method with new dates for 2013 (case 1440).
 *      5/28/13  Interlachen CC (interlachen) - Added custom to prevent non-junior members from selecting the Walk MoT from 7:30am-2:50pm from 5/28 - 9/2 (case 2267).
 *      5/23/13  Olympic Club (olyclub) - Updated checkOlyclubGstQuotas() to not count "MHGP Guest Outing" in the guest quota counts.
 *      5/15/13  Philly Cricket Club (philcricket) - Added checkPhilCricketActivities() method to ensure that all members in this reservation have access to this activity.
 *      5/13/13  Brooklawn CC (brooklawn) - Add Memorial Day and 4th of July to custom in checkRestLift to lift restriction on Thurs morning.
 *      4/25/13  Minikahda - adjusted the guest custom for 2013 - in checkMinikahdaGuests.
 *      4/29/13  Sonnenalp GC (sonnenalp) - Updated getGuestRates with new guest rates for 2013 (case 1070).
 *      4/09/13  Olympic Club (olyclub) - Updated checkOlyStarterTime with a time adjustment for Saturdays on the Ocean course.
 *      4/04/13  Elgin CC (elgincc) - Added custom to allow members except 'Social Member' mship and 'Certified Junior' and 'Non Certified Junior' mtypes to book advance guest times up to 365 days
 *               in advance.  They can have 7 of these on the books at any time.  Certified Juniors are not allowed to accompany a guest in these times (case 2226).
 *      3/25/13  Portland GC (portlandgc) - Updated checkPGCwalkup() method to add additional walkup times for April 2013.
 *      3/25/13  Added 2 more Member Restrictions in checkInterlachenRest to be lifted 48 hours in advance (per Nathan's request).
 *      3/21/13  Canterbury GC (canterburygc) - Updated custom to require guests further than 14 days in advance, instead of 7 (case 1800).
 *      3/17/13  Brooklawn CC (brooklawn) - Add custom to checkRestLift to lift Sat & Sun restriction on Thurs morning.
 *      3/14/13  Add checkRestLift to check for customs to lift a member restriction based on the date requested.
 *      3/11/13  Loch Lloyd CC (lochlloyd) - Added custom to prevent booking guests into tee times more than 5 days in advance (case 2241).
 *      3/06/13  Imperial GC (imperialgc) - Added custom using checkWinterSeasonMshipRoundCountsByMnum() to limit 'Dining Summer Golf' members to 6 18-hole rounds each year between 11/1 and 4/30, not including event times (case 2238).
 *      2/22/13  Portland GC (portlandgc) - Removed two-ball portion of their custom walkup times, since it is no longer needed.
 *      2/21/13  Oakmont CC (oakmont) - Added custom to handle their advance guest times going forward.  Replaces processing in Member_slot and Proshop_slot (case 2223).
 *      2/21/13  Oakmont CC (oakmont) - Commented out old checkOakmontGuestQuota() method, as it has been replaced.
 *      2/19/13  Portland GC (portlandgc) - Updated checkPGCwalkup() method to adjust some date and time ranges.
 *      2/18/13  Desert Forest GC (desertforestgolfclub) - Updates times and dates for 2-some times custom (case 1694).
 *      2/08/13  Cape Cod National (capecodnational) - Updated guest custom with different in-season date range (case 1828).
 *      2/08/13  Canterbury GC (canterburygc) - Fixed issue of custom being commented out unintentionally (case 1800).
 *      2/07/13  Discovery Bay CC (discoverybay) - Added custom to prevent Single Golf members from being associated with a Junior guest type in tee times (case 2219).
 *      1/21/13  Desert Horizons CC (deserthorizonscc) - Added 2-part custom to restrict guests between 8am-10am on Su/M/Th/F from 1/1/13-4/15/13. 
 *               Only 6 guests total will be allowed during this period per day, and guests during this time must be 1/mem.
 *      1/21/13  Mission Valley CC (missionvalleycc) - Commented out custom Social member restriction at the request of the club (case 2078).
 *      1/10/13  Windstar on Naples Bay (windstarclub) - Added custom using checkWinterSeasonMshipRoundCountsByMnum() to limit "Social Golf" members to 5 18-hole rounds (or 10 9-hole) each year between 11/15 and 4/15 (case 2201).
 *      1/10/13  Updated checkImperialGCMshipRoundCounts() to be named checkWinterSeasonMshipRoundCountsByMnum() instead, and made it able to be used generally for winter season mship customs of this type.
 *      1/08/13  Imperial GC (imperialgc) - Added checkImperialGCMshipRoundCounts() method to determine if 'Associate' members are over their winter season round limit (case 2197).
 *      1/03/13  Echo Lake CC (echolakecc) - Added checkEchoLake2someTimes() method to determine if a tee time is a 2some time (case 2204).
 *     12/20/12  Olympic Club (olyclub) - Updated checkOlyStarterTime with 2013 holiday dates.
 *     12/07/12  Canterbury GC - add custom to check mships of 'Social w/Golf' for a max of 5 rounds per season.
 *     11/25/12  Olympic Club (olyclub) - Updated checkOlyStarterTime with 2012 holiday dates for Christmas through New Years (again!).
 *     11/12/12  Ocean Reef Club (oceanreef) - Updated checkOceanReefMship to include the new "Junior Legacy" mship (case 1731).
 *     11/03/12  CC of Fairfax (ccfairfax) - Updated both their customs to only apply between 4/1 and 10/31 (case 2165 & case 2026).
 *     11/02/12  Olympic Club (olyclub) - Fixed issue with recently changed checkOlyStarterTime times.
 *     10/31/12  Olympic Club (olyclub) - Updated checkOlyStarterTime with 2012 holiday dates for Thanksgiving through New Years.
 *     10/17/12  Olympic Club (olyclub) - Updated checkOlyStarterTime with additional morning open times on Saturday and Sunday.
 *     10/11/12  Awbrey Glen CC (awbreyglen) - Fixed bug in checkAwbreyGlenMnumRoundCounts().  Was looking at member2b instead of teepast2.
 *     10/02/12  Tamarisk CC (tamariskcc) - Updated checkTamariskAdvTime() to start on 11/7 instead of 11/8 (case 1657).
 *      9/18/12  The Country Club (tcclub) - Updated checkTCCGuests to only query rounds from the Main Course and Championship Course.
 *      9/13/12  Awbrey Glen CC (awbreyglen) - Added custom to restrict Non-Resident Golf members to 30 rounds per family member number per year (case 2186).
 *      9/13/12  The Country Club (tcclub) - Updated checkTCCGuests to only count rounds with the "Guest" gtype instead of all guest types aside from "Tournament".
 *      8/22/12  Interlachen CC (interlachen) - Fixed time issue with weekday guest issue. One location hadn't been updated to 10:30 with last year's update. (case 1791).
 *      8/22/12  Olympic Club (olyclub) - Updated checkOlyStarterTime with an exclusion for 9/11/2012 since they're running a lottery.
 *      8/07/12  Olympic Club (olyclub) - Updated checkOlyStarterTime to opt out 9/4/2012 (day after Labor Day) from displaying the blue starter times.
 *      8/01/12  Oronoque CC (oronoquecc) - Added custom to prevent tee times from being booked with less than 3 players on weekends prior to 12:00pm (case 2179).
 *      7/24/12  The Quechee Club (quecheeclub) - Removed custom member restriction for weekend times including guests (case 2143).
 *      7/12/12  Misquamicut Club (misquamicut) - Updated checkMisquamicut2someTimes() to include Thursday times as well between 7/1 and 8/30 of each year (case 1996).
 *      7/11/12  Portland GC (portlandgc) - Updated checkPGCwalkup() with additional walkup times on Tuesdays.
 *      7/05/12  The Quechee Club (quecheeclub) - Added a custom to restrict members from booking times including guests on Sat/Sun until 5 days in advance at 7:30AM Eastern (case 2143).
 *      7/03/12  Oahu CC (oahucc) - Fixed an issue with the custom that was causing it to not account for consecutive times properly (case 2155).
 *      7/02/12  CC of Fairfax (ccfairfax) - Added custom to require at least 3 players in tee times prior to 12pm on Sat/Sun/Mon (member side only) (case 2165).
 *      6/29/12  MN Valley CC (mnvalleycc) - Removed the '5th Player Block' custom since the club would like this guest type to be usable in any player slot.
 *      6/22/12  Minikahda CC (minikahda) - Fixed a typo in their custom that was allowing 3 guest times to be booked during restricted times on Thursday.
 *      6/19/12  MN Valley CC (mnvalleycc) - Added custom to prevent members from using the '5th Player Block' guest type in any slots but the 5th player slot.
 *      6/15/12  Oahu CC (oahucc) - Added custom check to limit each member number to originating two rounds each Sunday (case 2155).
 *      6/11/12  Portland GC (portlandgc) - Updated checkPGCwalkup() method with time changes.
 *      6/06/12  Olympic Club (olyclub) - Updated checkOlyStarterTime to check for the 4th of July prior to all other days/dates.
 *      5/31/12  Olympic Club (olyclub) - Updated checkOlyStarterTime with a slight adjustment to the setup for the 4th of July.
 *      5/29/12  Minikahda Club (minikahda) - Added custom to check guest time limits per hour and special rules between Memorial Day and Labor Day.
 *      5/29/12  Dorset FC (dorsetfc) - Updated checkDorsetFC() method with new dates for 2012 (case 1440).
 *      5/22/12  Olympic Club (olyclub) - Updated checkOlyclubGstQuotas() to include custom quarters and an adjusted guest limit for the period following the US Open.
 *      5/15/12  Mayfield Sand Ridge (mayfieldsr) - Updated checkMayfieldSR with a new starting date for the custom.
 *      4/30/12  Portland GC (portlandgc) - Updated checkPGCwalkup() method to skip 7/4 in the Wednesday code, and made the Monday code start a couple days earlier.
 *      4/30/12  Add checkECearly to check if user is getting into a tee time too early (Eagle Creek) - moved from Member_slot and _slotm.
 *      4/27/12  Olympic Club (olyclub) - minor tweaks for Memorial Day - checkOlyStarter.
 *      4/04/12  Sonnenalp GC (sonnenalp) - Updated getGuestRate method with new guest rates for 2012 (case 1070).
 *      4/03/12  Tamarisk CC (tamariskcc) - Updated checkTamariskAdvTime() method to affect times 8:05-11:07 instead of 8:00-11:07 (case 1657).
 *      3/29/12  Oakmont CC (oakmont) - Updated checkOakmontGuestQuota to limit members to 2 per month instead of 3 (case 1364).
 *      3/23/12  Hallbrook CC (hallbrookcc) - Fixed a couple of the custom error messages (case 2114).
 *      3/22/12  Hallbrook CC (hallbrookcc) - Added custom to prevent "Junior" and "Dependant" mtypes from accessing specific days and times, some requiring an Adult member (case 2114).
 *      3/08/12  Rolling Hills CC - CO (rhillscc) - Removed and/or modified customs as per the club's request (case 1852 - part removed, part modified) (case 1853 - removed)
 *      2/24/12  Olympic Club (olyclub) - Updated checkOlyStarterTime with changes to Saturday afternoons during the summer season.
 *      2/23/12  Portland GC (portlandgc) - Updated walk-up time custom with date range changes for Tues and Wed.
 *      2/22/12  Pelican Marsh GC (pmarshgc) - Commented out custom to prevent members from booking tee times with only one player (case 1951).
 *      1/25/12  GAA Clubs (gaa*) - Updated custom that restricts students to only adding themselves to a tee time, since username check was case-sensitive, which caused issues if the student logged in using upper-case letters.
 *      1/23/12  Discovery Bay CC (discoverybay) - Added custom processing to prevent X's from being booked into a time after the X's have been removed from the tee sheet (case 2108).
 *     12/18/11  Olympic Club (olyclub) - Updated checkOlyStarterTime to apply Saturday processing to 1/16 and 2/20, and to not do starter times 1/17 and 2/21 since they're closed.
 *     12/04/11  (dataw) - Moved lottery registration verification from Member_lottery to verifyCustom.checkLotteryRegistrationAccess
 *     11/39/11  Mission Valley CC (missionvalleycc) - Added checkMissionValleyMships() custom method to restrict Social members from booking more than 6 rounds per membership number between 11/1 - 4/30 each year (case 2078).
 *     11/28/11  Olympic Club (olyclub) - Added checkOlyClubIsProshopEventSignup() method to check the event_log entries to determine if the proshop created a given event team (case 2068).
 *     11/23/11  Olympic Club (olyclub) - Updated checkOlyStarterTime to add a full date parameter.  Determining the full date inside the method causes issues when the available booking dates straddle new years.
 *     11/22/11  Ocean Reef Club (oceanreef) - Updated checkOceanReefMship to also be passed the time of the tee time, and only apply the custom to times prior to noon EST (case 1731).
 *     11/22/11  Olympic Club (olyclub) - Updated checkOlyStarterTime with holiday times for Christmas/New Years.
 *     11/20/11  Olympic Club (olyclub) - Another fix for checkOlyStarterTime times for Thanksgiving/Black Friday.
 *     11/18/11  Olympic Club (olyclub) - Updated checkOlyStarterTime with a completely separate set of times for Thanksgiving day and Black Friday 2011.
 *     11/14/11  Olympic Club (olyclub) - Updated checkOlyStarterTime so it will bypass the Thursday/Friday processing for the Thanksgiving holiday days, since it needs to get to the Saturday processing.
 *     11/09/11  Mira Vista CC (miravista) - Added custom checkMiraVistaGuestTimes() method to restrict there from being more than 2 tee times per hour that include guests (case 2062).
 *     10/28/11  Olympic Club (olyclub) - Updated checkOlyStarterTime to apply Saturday processing to Friday, 11/25/2011 as well as Thanksgiving Day.
 *     10/28/11  Olympic Club (olyclub) - Updated checkOlyStarterTime to apply Saturday processing to Thanksgiving Day.
 *     10/21/11  FireRock CC (firerockcc) - Added custom checkFireRockMships() method to prevent Sports members from playing more than 14 times between Oct 20th and May 15th each year (case 2050).
 *     10/18/11  Interlachen CC (interlachen) - Updated weekday guest custom to only run between 5/01 and 10/17 (case 1791).
 *     10/17/11  Turner Hill CC (turnerhill) - Added custom to warn proshop users when they try to book more than 4 tee times containing Unaccomp Guests  (case 2049).
 *     10/15/11  Tamarisk CC (tamariskcc) - Updated checkTamariskAdvTime() method to run from 11/8-4/30 this year instead of 11/1 as in previous years (case 1657).
 *     10/05/11  Wildcat Run CC (wildcatruncc) - Added custom to prevent "Golf Equity" from booking a tee time including guests before 7am 6 days in advance, and "Golf Annual" until 3 days in advance at 7am (case 2044).
 *     10/04/11  Race Brook CC (racebrook) - Updated checkRaceBrookStarterNumTime() method to run off custom_disp values instead of show values.
 *     10/03/11  Capital City Club (capitalcityclub) - Added custom to restrict Dependent mtypes from being included in times booked before 8:30am on the same day that tee time becomes available (case 2031).
 *      9/27/11  Indian Ridge CC (indianridgecc) - Added checkIndianRidgeMships() custom method to limit Limited Equity Golf Fitness/Social mships to 30 rounds between 11/1 and 6/30 each year (case 2038).
 *      9/23/11  Pine Brook CC (pinebrookcc) - Added checkPineBrookNonIndoorTimes() method to check if any Non-Indoor Tennis members in the current reservation have already played 1 round that month on the indoor courts (case 2028).
 *      9/22/11  Mira Vista CC (miravista) - Added custom to require that members include at least one guest in any tee time bookings for Tuesday-Friday (case 2023).
 *      9/14/11  CC of Fairfax (ccfairfax) - Added custom to restrict Sat tee times booked between 8am-11:59am Thurs, and Sun/Mon times booked between 8am-11:59am Fri if they don't contain at least 3 Primary members (case 2026).
 *      9/13/11  Desert Forest GC (desertforestgolfclub) - Updates times and dates for 2-some times custom (case 1694).
 *      9/07/11  Naperville CC (napervillecc) - Added checkNapervilleAdvGuestTimes custom to allow members to book times up to 30 days out as long as one guest and one member are present.
 *               The custom will apply these rules to times booked as adv guest times even once they are within the normal booking window, and at that point members will need to call the shop to cancel these times (case 2009).
 *      9/06/11  TPC Boston (tpcboston) - Added checkTPCBostonMships custom to prevent certain membership types from originating more than one tee time per day per member number (case 1842).
 *      9/01/11  Olympic Club (olyclub) - Updated checkOlyStarterTime to not flag the special MHGP 24 hour advance times as starter times on Labor Day.
 *      8/17/11  Cherry Valley CC (cherryvalleycc) - Added checkCherryValley2someTimes() method (case 2018).
 *      8/12/11  The Estancia Club (estanciaclub) - Updated checkEstanciaAdvTimes methods to not count existing advance times towards a member's quota once those times are within 30 days.
 *                  Also updated custom to require that advance times include at least one guest (case 1897).
 *      7/25/11  CC at Castle Pines (castlepines) - Added custom processing to prevent X's from being booked into a time after the X's have been removed from the tee sheet (case 2003).
 *      7/12/11  checkOlyStarterTime = skip Christmas Day as the club is closed.
 *      7/11/11  Wollaston GC (wollastongc) - Removed custom to restrict members from accessing tee sheets for any day on Mondays (case 1819).
 *      7/06/11  Bearpath G & CC (bearpath) - Added custom to prevent Dependent, Certified Dependent, and CD Plus members from booking more than 4 days in advance when not accompanied
 *               by a Primary or Spouse member (case 1993).
 *      7/05/11  Club at Mediterra (mediterra) - Updated checkMediterraSports method with new start date, and increased round limit from 4 to 6 (case 1262).
 *      7/05/11  Engineers CC (engineerscc) - Added custom to prevent Child members from being booked without being accompanied by an Adult Member (Primary or Spouse) (case 2001).
 *      7/01/11  Misquamicut CC (misquamicut) - Added custom checkMisquamicut2someTimes() method to see if 2some times should be applied (case 1996).
 *      6/27/11  Baltusrol GC (baltusrolgc) - Added custom to restrict House, House Awaiting Golf, and Special House members from booking times with guests on Sat/Sun/Holidays (case 1959).
 *      6/27/11  Minnetonka CC (minnetonkacc) - Added custom checkMinnetonka3someTimes() method to see if 3some times should be applied (case 1989).
 *      6/22/11  Add checkOlyclubGstQuotas for the Olympic Club - custom guest quotas based on the member's privileges.
 *      6/21/11  PGA Golf Management UNL (pgmunl) - Only allow students to add and remove themselves from a tee time (case 1994).
 *      6/21/11  Portland GC (portlandgc) - Updated walk-up time custom to not hide walk-up times on Monday 7/4/11.
 *      6/21/11  Cape Cod National (capecodnational) - Updated weekend/holiday date/time values for custom guest quota (case 1828).
 *      6/20/11  Mayfield Sand Ridge (mayfieldsr) - Updated 2-some custom to include 4th of July.
 *      6/16/11  Desert Highlands GC (deserthighlands) - Added custom to require that members include a minimum of 2 players when booking prior to 1 day in advance at 7:00am (case 1988).
 *      6/16/11  Cherry Creek CC (cherrycreek) - Added custom to prevent members from booking Accompanied and Family guest types with any mode of transportation other than Cart Included (CI).
 *      6/08/11  Race Brook CC (racebrook) - Added checkRaceBrookStarterNumTime() method to return the latest time slot that's been checked in.
 *      6/08/11  Portland GC (portlandgc) - Updated walk-up time custom to include a special case for Mondays during the summer.
 *      6/07/11  Interlachen CC (interlachen) - Updated Fri guest custom to run 10:30-11:30 instead of 10:00-11:30 (case 1791).
 *      6/06/11  Cape Cod National (capecodnational) - Updated date/time values for custom guest quota (case 1828).
 *      6/02/11  Minikhada (minikhada) - Updated custom to restrict guest times per hour (case 1027).
 *      5/26/11  Add checkOlyStarterTime for Olympic Club to check if tee time is to be reserved for golf shop use.
 *      5/24/11  Minikhada (minikhada) - Updated custom to restrict guest times per hour (case 1027).
 *      5/23/11  Updated checkDorsetFC for 2-some check for 2011 (Case# 1440).
 *      5/17/11  Mayfield Sand Ridge (mayfieldsr) - Updated date range for 2-some custom.
 *      5/13/11  Royal Montreal GC (rmgc) - Updated checkRMGC4Ball custom to correct conditionals.
 *      5/12/11  Sonnenalp (sonnenalp) - Added checkSonnenalp3someTimes() custom method to determine whether 3-some times is in effect or not (case 1978).
 *      5/11/11  Royal Montreal GC (rmgc) - Added checkRMGC4Ball custom method to determine whether the passed course, date, and day name correspond to a 4-ball day.
 *      5/09/11  Pelican Marsh GC (pmarshgc) - Do not enforce custom check for single player after 4/30 each year (case 1951).
 *      5/02/11  Desert Forest GC (desertforestgolfclub) - Updates to 2-some times custom (case 1694).
 *      4/25/11  Tamarisk (tamarisk) - Update checkTamariskAdvTime custom to include 11/1 and 4/30 - was actually only running 11/2 - 4/29 previously (case 1657).
 *      4/21/11  Hawks Landing GC (hawkslandinggolfclub) - Custom to not allow members to decrease the number of players within 1 hr of the tee time and display the no-show policy if attempted(case 1969).
 *      3/28/11  The Country Club (tcclub) - Added additional clause to twosome times custom method checkTheCC to build afternoon times from 4/1-10/31.
 *      3/18/11  Mirasol CC (mirasolcc) - Removed custom to not allow players to book tee times with only 1 player (case 1244).
 *      3/17/11  Pelican Marsh GC (pmarshgc) - Do not allow tee times to be booked with only 1 player (not including x's) (case 1951).
 *      2/07/11  Mirasol CC (mirasolcc) - Do not allow tee times to be booked with only 1 player (case 1244).
 *      1/25/11  Sonnenalp - change some guest rates.
 *      1/25/11  Oakmont CC (oakmont) - change the max # of guests per month in checkOakmontGuestQuota from 8 to 3 (case 1364).
 *      1/18/11  Golf Academy of America (non-class sites) - Updated custom to handle names being shifted up when a member removes themselves from a tee time (case 1900).
 *      1/17/11  Lakewood CC (lakewoodcc) - Added checkLakewoodActivityTimes() method to determine whether or not a time was booked within 24 hours (case 1901).
 *      1/14/11  Southern Hills CC (southernhillscc) - Adjusted date and time ranges for Friday custom (case 1689).
 *      1/05/10  Monterey Peninsula CC (mpccpb) - Added an additional chunk of time 1-2 somes are able to play Fri-Sun mornings (case 1904).
 *     12/08/10  Golf Academy of America (non-classroom sites) - Custom to prevent members from typing in another member in place of themselves (case 1900).
 *     11/01/10  Mesa Verde CC - Must be at least 2 players in every tee time (case 1905).
 *     10/27/10  Portland GC - adjust the date range for Tuesdays to allow for Ladies Day season.
 *     10/26/10  The Estancia Club (estanciaclub) - Updated custom check to not require that Advance Times contain at least one member and one guest (case 1897).
 *     10/26/10  Los Coyotes (loscoyotes) - Changes made to primary times custom (case 1740).
 *     10/20/10  Monterey Peninsula CC (mpccpb) - Fri-Sun tee times prior to 12:00pm (1:00pm during DST) must have at least 3 players (case 1904).
 *     10/15/10  The Estancia Club (estanciaclub) - Added overloaded checkEstanciaAdvTimes method to check and return the count for a specific member instead of an entire tee time (case 1897).
 *     10/11/10  The Estancia Club (estanciaclub) - Additional tweaks made to "advance times" custom to close loopholes (case 1897).
 *     10/07/10  The Estancia Club (estanciaclub) - Track and limit members to only 4 "advance times" on the books at any given time.  Advance Time = booked
 *               more than 30 days in advance.  Advance times must contain at least one member and one guest (case 1897).
 *      9/10/10  Update checkNaplesAssocBQuota so it counts 9-hole rounds as 1 and 18-hole rounds as 2, then check for max rounds played.
 *      8/24/10  Wee Burn CC (weeburn) - Updated custom to allow "WAITING FOR GOLF" members to bring up to 3 "Friday Guest" guests on Fridays between 10:00am and 1:00pm (case 1681).
 *      8/04/10  Desert Forest GC (desertforestgolfclub) - Updates to 2-some times custom
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
   private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday near the 4th
   private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - actual day (always 7/04/xxxx)
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
   //  break down date of tee time
   //
   int date = (int)slotParms.date;
   int yy = date / 10000;                             // get year
   int mm = (date - (yy * 10000)) / 100;              // get month
   int dd = (date - (yy * 10000)) - (mm * 100);       // get day

   int shortDate = (mm * 100) + dd;

   //
   //  Process according to the club
   //

   if (slotParms.club.equals("tualatincc")) {     // TUALATIN CC

      //
      //  Tualatin - Check for any Juniors without an Adult
      //
      error = checkTualatinJr(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Juniors Not Allowed Without An Adult</H3>" +
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

         returnMsg = "<H3>Juniors Not Allowed Without An Adult</H3>" +
                     "<BR>Sorry, but there must be at least one adult member in the group when" +
                     "<BR>one or more juniors 11 - 14 are included on Saturday or Sunday afternoons." +
                     "<BR><BR>Please include an adult in the group, or try another time of the day.";
         
      } /* else {           // Removed since the club wants the 5th Player Block to be usable in any player slot, and the limit to only one is being handled via a standard guest restriction.
         
          // Check to ensure that the "5th Player Block" guest type isn't used in player slots 1-4.
          if (slotParms.player1.startsWith("5th Player Block") || slotParms.player2.startsWith("5th Player Block") || slotParms.player3.startsWith("5th Player Block") 
           || slotParms.player4.startsWith("5th Player Block") || slotParms.player5.startsWith("5th Player Block")) {
              
              error = true;
              returnMsg = "<H3>Invalid Guest</H3>" +
                      "<BR>Sorry, but the '5th Player Block' guest type may only be used to fill the 5th player position." +
                      "<BR><BR>Please remove this guest type from any other player slots and try again.";
          }
      }*/

   } else if (slotParms.club.equals("cherokeecountryclub")) {     // CHEROKEE CC

      error = checkCherokeeJr(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Dependents Not Allowed Without An Adult</H3>" +
                     "<BR>Dependents that aren't accompanied by an adult member,<BR>" +
                     "must call the golf shop for tee time approval.";
      }

   } else if (slotParms.club.equals("elmcrestcc")) {     // ELMCREST CC

      //
      //  Elmcrest - Check for any Dependents without an Adult
      //
      error = checkElmcrestJr(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Dependents Not Allowed Without An Adult</H3>" +
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

         returnMsg = "<H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " has already played (or scheduled to play) the maximum allowed rounds for the season." +
                     "<BR><BR>Please remove this player or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("jonathanslanding")) {     // JONATHANS LANDING GC

      //
      //  Jonathans Landing - Check for Membership Quotas
      //
      error = checkJLGCmships(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " (and family) have already played, or are scheduled to play, the maximum allowed rounds for the season." +
                     "<BR><BR>Please remove the player(s) or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("gulfharbourgcc")) {     // GULF HARBOUR G&CC

      //
      //  Gulf Harbour - Check for Sports Membership Quotas
      //
      error = checkGulfHarbourSports(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " (and family) have already played, or are scheduled to play, the maximum allowed rounds for the month." +
                     "<BR><BR>Please remove the player(s) or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("noaks")) {     // NORTH OAKS GC

      //
      //  North Oaks - check for juniors accompanied by an adult during specific times
      //
      error = checkNorthOaksJrs(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Unaccompanied Juniors Restricted</H3>" +
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

            returnMsg = "<H3>Blue Flag Cart Limit</H3>" +
                        "<BR>Limit reached for player: <b>" +slotParms.player + "</b><BR>" +
                        "<BR>WARNING: This is the third temporary flag used without a doctor's note. The next time a flag " +
                        "<BR>is requested by the member, a doctor's note must be provided or " +
                        "a flag will not be granted. " +
                        "<BR><BR>Please contact the Professional Staff with any concerns. ";

         } else  if (errorFlag > 3) {         // if member has now exceeded 3 uses

            returnMsg = "<H3>Blue Flag Cart Limit</H3>" +
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

         returnMsg = "<H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " has already played, or are scheduled to play, the maximum allowed weekend rounds for this month." +
                     "<BR><BR>Please remove the player(s) or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("foxdencountryclub") && !slotParms.user.startsWith("proshop")) {     // Fox Den & Member

      //
      //  Fox Den - check for X's on weekends
      //
      error = checkFoxDenX(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Invalid Use of X's</H3>" +
                     "<BR>Sorry, but you cannot specify more than one X per group on weekends." +
                     "<BR><BR>Please remove the extra X's or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("desertforestgolfclub")) {

      //
      //  Desert Forest - check for 2-some time and if more than 2 players
      //
      error = checkDesertForest(slotParms.date, slotParms.time, slotParms.fb, slotParms.day);

      if (error == true && !slotParms.player3.equals("")) {    // if 2-some time and more than 2 players

         returnMsg = "<H3>Max Number of Members Exceeded</H3>" +
                     "<BR>Sorry, this is a 2-some time and you requested more than 2 players." +
                     "<BR><BR>Please remove the extra players or return to the tee sheet.";
      }

   } else if (slotParms.club.equals("oahucc")) {    // if Oahu CC

      //
      //  Oahu
      //
      if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) {

         error = checkOahuWeekends(slotParms);

         if (error == true) {        // if less than 3 players

            returnMsg = "<H3>X's Not Allowed</H3>" +
                        "<BR>Sorry, X's cannot be selected prior to 11 AM." +
                        "<BR><BR>Please remove all X's or return to the tee sheet.";
         }
      }
      
      
      // If no error, check for member number round limit on Sundays
      if (!error && slotParms.day.equals("Sunday")) {
          
          error = checkOahuSundayLimit(slotParms, con);
          
          if (error) {

              returnMsg = "<H3>Max Number of Tee Times Exceeded</H3>" +
                      "<BR>Sorry, " + slotParms.player + " (and family) are scheduled to play the maximum allowed tee times for on this day." +
                      "<BR><BR>A maximum of 2 tee times can be booked by members under one member number each Sunday, and this time would exceed that limit." +
                      "<BR><BR>Please remove this player or return to the tee sheet.";
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

               returnMsg = "<H3>Days in Advance Exceed</H3>" +
                           "<BR>Sorry, the tee time you requested is reserved for Primary members " +
                           "<BR>and cannot be reserved this far in advance." +
                           "<BR><BR>Please return to the tee sheet and select a differnt time.";

         } else {

            error = checkLosCoyotesPrimary(slotParms, con);     // make sure all members are mtype=Primary...

            if (error == true) {                   // if 1 or more non-Primary Members in request

               returnMsg = "<H3>Member Not Allowed</H3>" +
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

            returnMsg = "<H3>Member Not Allowed</H3>" +
                        "<BR>Sorry, " +slotParms.player+ " is not allowed to play before 12:30. " +
                        "<BR><BR>Please remove this player or return to the tee sheet and select a different time.";

      } else {

         error = checkNaplesAssocBQuota(slotParms, con);     // check max rounds for Assoc B mships

         if (error == true) {                   // if 1 or more non-Primary Members in request

            returnMsg = "<H3>Member Exceeded Max Rounds</H3>" +
                        "<BR>Sorry, " +slotParms.player+ " has exceeded his/her max allowed rounds for this month." +
                        "<BR><BR>Please remove the player or return to the tee sheet.";
         }
      }

   } else if (slotParms.club.equals("longuevueclub")) {

      error = check2LongueVue(slotParms.date, slotParms.time, slotParms.day);    // check for 2-some time

      if (error == true && !slotParms.player3.equals("")) {    // if 2-some time and more than 2 players requested

            returnMsg = "<H3>Invalid Request</H3>" +
                        "<BR>Sorry, the time you requested is reserved for 2-somes only. " +
                        "<BR><BR>Please remove the extra players or return to the tee sheet and select a different time.";
      }

   } else if (slotParms.club.equals("canterburygc")) {

      //
      //  Canterbury - check for guest time only - any time beyond the normal 7 days in advance (case 1800)
      //
      error = checkCanterburyGst(slotParms);
       
      if (error == true) {              // No guests in request

            returnMsg = "<H3>Invalid Request</H3>" +
                        "<BR>Sorry, but there must be at least one member and one guest in the request. " +
                        "<BR>Only guest times are allowed when reserving a tee time more than 7 days in advance. " +
                        "<BR><BR>Please add the missing player or return to the tee sheet and select a different date.";
      } else {

            //
            //  Canterbury - Check for 'Social w/Golf' Membership Quotas
            //
            error = checkCGCmships(slotParms, con);

            if (error == true) {         // if we hit an error

                returnMsg = "<H3>Quota Exceeded for Membership</H3>" +
                            "<BR>Sorry, but " +slotParms.player+ " has already played, or is scheduled to play, the maximum allowed rounds for the season." +
                            "<BR><BR>Please remove the player or return to the tee sheet.";
            }
      }

   } else if (slotParms.club.equals("southernhillscc")) {

      //
      //  Southern Hills CC - Friday times during certain dates/time periods must contain at least 1 member and 2 guests (case 1689)
      //
      error = checkSouthernHillsGst(slotParms);

      if (error == true) {              // No guests in request

            returnMsg = "<H3>Invalid Request</H3>" +
                        "<BR>Sorry, but there must be at least one member and two guests in this request. " +
                        "<BR><BR>Please add the missing player/guests or return to the tee sheet and select a different time or date.";
      }

   } else if (slotParms.club.equals("yankeehill") || slotParms.club.equals("dhgc") || slotParms.club.equals("oakleycountryclub") || slotParms.club.equals("mesaverdecc")) {

      //
      //  Make sure there are at least 2 players (members and/or guests) in every tee time
      //
      error = check2Players(slotParms);

      if (error == true) {              // No guests in request

            returnMsg = "<H3>Invalid Request</H3>" +
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

            returnMsg = "<H3>Invalid Request</H3>" +
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
               returnMsg = "<H3>Quota Exceeded for Membership</H3>" +
                       "<BR>Sorry, but " +slotParms.player+ " has already scheduled or played the maximum allowed weekday" +
                       "<BR><BR>rounds for this year.  Please remove the player or return to the tee sheet.";
           } else {
               returnMsg = "<H3>Quota Exceeded for Membership</H3>" +
                       "<BR>Sorry, but " +slotParms.player+ " has already scheduled or played the maximum allowed weekend" +
                       "<BR><BR>rounds for this year.  Please remove the player or return to the tee sheet.";
           }
       }

   } else if (slotParms.club.equals("capecodnational")) {     // Cape Cod National

       //  Check for "Wequassett Guest" (Hotel) type guests and enforce the club's limitations
       error = checkCapeCodGsts(slotParms, con);

       if (error) {

           returnMsg = "<H3>Wequassett Guest Quota Reached</H3>" +
                       "<BR>Sorry, but the quota for Wequassett Guests has already been reached for this day and time." +
                       "<BR><BR>Please select a different time of the day or a different day.";
       }

   } else if (slotParms.club.equals("hazeltine2010")) {     // Hazeltine's temp site

      //
      //  Hazeltine temp site - Check for Member Quotas per course (resciprocal clubs)
      //
      error = checkHazeltine2010(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " has already played, or is scheduled to play, the maximum allowed times on this course." +
                     "<BR><BR>Please remove the player or return to the tee sheet and try another course.";
      }

   } else if (slotParms.club.equals("philcricketrecip")) {     // Philly Cricket's temp site

      //
      //  Philly Cricket's temp site - Check for Member Quotas per course (resciprocal clubs)
      //
      returnMsg = checkPhilcricketRecip(slotParms, con);


   } else if (slotParms.club.equals("midpacific")) {

       int index = slotParms.ind;
       
       if (index > 365) {
           index = 0;
       }
       
       if (index > 7) {
           if (slotParms.mtype1.startsWith("Spouse")) {
               slotParms.player = slotParms.player1;
               error = true;
           } else if (slotParms.mtype2.startsWith("Spouse")) {
               slotParms.player = slotParms.player2;
               error = true;
           } else if (slotParms.mtype3.startsWith("Spouse")) {
               slotParms.player = slotParms.player3;
               error = true;
           } else if (slotParms.mtype4.startsWith("Spouse")) {
               slotParms.player = slotParms.player4;
               error = true;
           } else if (slotParms.mtype5.startsWith("Spouse")) {
               slotParms.player = slotParms.player5;
               error = true;
           }
       }
       
       if (error) {

           returnMsg = "<H3>Member Restricted</H3><BR>" +
                   "<BR><BR>Sorry, but " + slotParms.player + " is not allowed to be a part of a tee time this far in advance.<BR>" +
                   "<BR>By club policy, spouses are not allowed to be a part of a tee time booked more than 7 days in advance." +
                   "<BR>Please remove this player or return to the tee sheet.";
           
       } else {
           
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
       }

   } else if (slotParms.club.equals("edisonclub")) {

       // CASE: 1834
       if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && slotParms.time >= 700 && slotParms.time <= 1100 && slotParms.date >= 20100501 && slotParms.date <= 20100907) {

           if (slotParms.members < 2 || (slotParms.members == 2 && slotParms.guests == 0)) {

               error = true;

           }

           if (error) {

               returnMsg = "<H3>Minimum Number of Players Not Present</H3><BR>" +
                          "<BR><BR>When booking tee times between 7:00AM and 11:00AM you must specifiy at least three players.<BR>" +
                          "<BR>TBA or 'X' do not count and there must be at least two members included.";

               if (slotParms.groups > 0) {

                   returnMsg += "<BR><BR>Group #" + slotParms.groups + " starting at " + Utilities.getSimpleTime(slotParms.time) + " is violating this restriction.";

               }

           }

       } // end if within time & date range


   } else if (slotParms.club.equals("rhillscc")) {      //  Rolling Hills CC - Colorado

       //if ((!slotParms.day.equals("Tuesday") && !slotParms.day.equals("Thursday")) || slotParms.time < 700 || slotParms.time > 933) {
/*
           // CASE: 1852        // Custom removed at the customers request
           error = checkRHCCdays(slotParms);

           if (error) {

                returnMsg = "<H3>Days In Advance Limit Exceeded</H3><BR>" +
                           "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to be part of a tee time this far in advance." +
                           "<BR><BR>Please remove this player or select another day.";

           } else {
*/
              //  Check for Guests and more than one day in advance

              error = checkRHCCguests(slotParms);

              if (error) {

                   returnMsg = "<H3>Days In Advance Limit Exceeded</H3><BR>" +
                              "<BR><BR>Sorry, guests are not allowed to be part of a tee time more than 5 day in advance." +
                              "<BR><BR>Please remove this player or select another day.";

              } /* else {   // Custom removed at the customers request

                 //  If Sunday - check for non-Primary members in the morning

                 if (slotParms.day.equals("Sunday")) {

                    error = checkRHCCsunday(slotParms);

                    if (error) {

                         returnMsg = "<H3>Member Restricted</H3><BR>" +
                                    "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to be part of this tee time without a primary member." +
                                    "<BR>Secondary members and dependents must be accompanied by a primary member on Sunday mornings." +
                                    "<BR><BR>Please remove this player or select a later time (after 10:00 AM).";
                    }
                 }
              } */
           // }
      // }   
   // end of rhillscc
   } else if (slotParms.club.equals("estanciaclub")) {

       boolean existingTime = false;
       int advTimeCount = 0;

       // See if any slots were updated.  If so, we don't want to update the custom_int value.
       if (slotParms.custom_int > 30) {

           existingTime = true;
       }

       // If more than 30 days in advance, or time has already been designated as an Advance Time, do custom processing.
       if (slotParms.ind > 30) {

           if (slotParms.guests > 0) {

               advTimeCount = checkEstanciaAdvTimes(slotParms, con);

               // Check number of adv times for all members of this tee time.
               if (advTimeCount < 4 || (existingTime && advTimeCount == 4)) {

                   if (!existingTime) slotParms.custom_int = slotParms.ind;

               } else {

                   returnMsg = "<H3>Member Restricted</H3><BR>" +
                           "<BR><BR>Sorry, " +slotParms.player+ " already has 4 advance times booked." +
                           "<BR>Members may only have 4 advance times on the books at any given time." +
                           "<BR><BR>Please remove this player or select a tee time that is not designated as an advance time.";
               }

           } else {

               returnMsg = "<H3>No Guests Found</H3><BR>" +
                           "<BR><BR>Sorry, tee times booked more than 30 days in advance must contain at least one guest." +
                           "<BR><BR>Please add a guest or select a tee time that is not designated as an advance time.";
           }

       } else {

           slotParms.custom_int = 0;
       }

   } else if (slotParms.club.equals("mpccpb")) {        // Friday-Sunday, must have 3 players before 12:00pm (1:00pm during DST)

       Calendar cal2 = new GregorianCalendar();

       cal2.add(Calendar.DATE, slotParms.ind);

       boolean isDST = false;

       if (cal2.get(Calendar.DST_OFFSET) != 0) {
           isDST = true;
       }

       error = checkMontereyPlayers(slotParms, isDST);

       if (error) {

            returnMsg = "<H3>Invalid Request</H3>" +
                        "<BR>Sorry, but this does not contain enough players." +
                        "<BR>Tee times prior to " + (isDST ? "1:00pm" : "12:00pm") + " must contain at least 3 players." +
                        "<BR><BR>Please add additional players or return to the tee sheet.";
       }

   } else if ((slotParms.club.startsWith("gaa") && !slotParms.club.endsWith("class")) || slotParms.club.equals("pgmunl")) {

       // Only perform for members
       if (!ProcessConstants.isProshopUser(slotParms.user)) {

           // If any player has changed and does not contain the current user, flag them and restrict them from booking time.
           if (!slotParms.player1.equals("") && !slotParms.oldPlayer1.equals(slotParms.player1) && !slotParms.oldPlayer2.equals(slotParms.player1) && !slotParms.user1.equalsIgnoreCase(slotParms.user)) {
               slotParms.player = slotParms.player1;
               error = true;
           } else if (!slotParms.player2.equals("") && !slotParms.oldPlayer2.equals(slotParms.player2) && !slotParms.oldPlayer3.equals(slotParms.player2) && !slotParms.user2.equalsIgnoreCase(slotParms.user)) {
               slotParms.player = slotParms.player2;
               error = true;
           } else if (!slotParms.player3.equals("") && !slotParms.oldPlayer3.equals(slotParms.player3) && !slotParms.oldPlayer4.equals(slotParms.player3) && !slotParms.user3.equalsIgnoreCase(slotParms.user)) {
               slotParms.player = slotParms.player3;
               error = true;
           } else if (!slotParms.player4.equals("") && !slotParms.oldPlayer4.equals(slotParms.player4) && !slotParms.oldPlayer5.equals(slotParms.player4) && !slotParms.user4.equalsIgnoreCase(slotParms.user)) {
               slotParms.player = slotParms.player4;
               error = true;
           } else if (!slotParms.player5.equals("") && !slotParms.oldPlayer5.equals(slotParms.player5) && !slotParms.user5.equalsIgnoreCase(slotParms.user)) {
               slotParms.player = slotParms.player5;
               error = true;
           }

           if (error) {
               returnMsg = "<H3>Invalid Request</H3>" +
                       "<BR>Sorry, an invalid member was entered." +
                       "<BR>Members may only add/remove themselves from tee times." +
                       "<BR><BR>Please correct the modified player name or return to the tee sheet.";
           }
       }

   } else if (slotParms.club.equals("hawkslandinggolfclub")) {

       int cur_time = Utilities.getTime(con);
       long cur_date = Utilities.getDate(con);

       // If less than one hour prior to the tee time, Make sure the number of players hasn't decreased.
       if (slotParms.date == cur_date && (cur_time > (slotParms.time - 100))) {

           int playerCount = 0;
           int oldPlayerCount = 0;

           if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("x")) playerCount++;
           if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("x")) playerCount++;
           if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("x")) playerCount++;
           if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("x")) playerCount++;
           if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) playerCount++;

           if (!slotParms.oldPlayer1.equals("") && !slotParms.oldPlayer1.equalsIgnoreCase("x")) oldPlayerCount++;
           if (!slotParms.oldPlayer2.equals("") && !slotParms.oldPlayer2.equalsIgnoreCase("x")) oldPlayerCount++;
           if (!slotParms.oldPlayer3.equals("") && !slotParms.oldPlayer3.equalsIgnoreCase("x")) oldPlayerCount++;
           if (!slotParms.oldPlayer4.equals("") && !slotParms.oldPlayer4.equalsIgnoreCase("x")) oldPlayerCount++;
           if (!slotParms.oldPlayer5.equals("") && !slotParms.oldPlayer5.equalsIgnoreCase("x")) oldPlayerCount++;

           if (playerCount < oldPlayerCount) {

               returnMsg = "<H3>Invalid Request</h3>" +
                       "<br>Sorry, but this action would go against a club-enforced policy:" +
                       "<br><br>No Show Policy - Failure to cancel a reserved tee-time up to three (3) hours in advance from that time will result in a charge for the " +
                       "<br>entire tee-time at the applicable rate.  Notice of reduction in players from the reserved tee-time must also be received by the golf shop " +
                       "<br>at least one (1) hour prior to the tee-time, or the person who booked the tee-time will be charged for the spot(s) that were originally " +
                       "<br>reserved and unused for that booking.  This policy is for members and the general public.  The only circumstance when the golf shop will " +
                       "<br>not enforce this policy would be if an open tee-time exists immediately before or after the booking." +
                       "<br><br>Please contact the golf shop staff in order to reduce the number of players in this tee time.";
           }
       }
   } else if (slotParms.club.equals("cherrycreek")) {

       if (!slotParms.player1.equals("") && (slotParms.player1.startsWith("Accompanied") || slotParms.player1.startsWith("Family")) && !slotParms.p1cw.equals("CI")) {
           slotParms.player = slotParms.player1;
           error = true;
       } else if (!slotParms.player2.equals("") && (slotParms.player2.startsWith("Accompanied") || slotParms.player2.startsWith("Family")) && !slotParms.p2cw.equals("CI")) {
           slotParms.player = slotParms.player2;
           error = true;
       } else if (!slotParms.player3.equals("") && (slotParms.player3.startsWith("Accompanied") || slotParms.player3.startsWith("Family")) && !slotParms.p3cw.equals("CI")) {
           slotParms.player = slotParms.player3;
           error = true;
       } else if (!slotParms.player4.equals("") && (slotParms.player4.startsWith("Accompanied") || slotParms.player4.startsWith("Family")) && !slotParms.p4cw.equals("CI")) {
           slotParms.player = slotParms.player4;
           error = true;
       } else if (!slotParms.player5.equals("") && (slotParms.player5.startsWith("Accompanied") || slotParms.player5.startsWith("Family")) && !slotParms.p5cw.equals("CI")) {
           slotParms.player = slotParms.player5;
           error = true;
       }
       
       if (error) {
           returnMsg = "<H3>Invalid Request</H3>" +
                   "<BR>Sorry, an invalid mode of transportation was selected." +
                   "<BR>Accompanied and Family guest types must have a mode of transportation of Cart Included (CI)." +
                   "<BR><BR>Please correct the mode of transportation values or return to the tee sheet.";
       }


   } else if (slotParms.club.equals("deserthighlands")) {

       int cur_time = Utilities.getTime(con);
       int playerCount = 0;

       if (!slotParms.player1.equals("")) playerCount++;
       if (!slotParms.player2.equals("")) playerCount++;
       if (!slotParms.player3.equals("")) playerCount++;
       if (!slotParms.player4.equals("")) playerCount++;
       if (!slotParms.player5.equals("")) playerCount++;

       if (playerCount == 1 && (slotParms.ind > 1 || (slotParms.ind == 1 && cur_time < 700))) {

           returnMsg = "<H3>Invalid Request</H3>" +
                   "<BR>Sorry, not enough players found." +
                   "<BR>By club policy, tee times must include a minimum of two (2) players prior to one (1) day in advance at 7:00am." +
                   "<BR><BR>Please add additional players or return to the tee sheet.";
       }


   } else if (slotParms.club.equals("baltusrolgc")) {

       if (slotParms.guests > 0 && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3)) {

           if (slotParms.mship1.equalsIgnoreCase("House") || slotParms.mship1.equalsIgnoreCase("House Awaiting Golf") || slotParms.mship1.equalsIgnoreCase("Special House")) {
               slotParms.player = slotParms.player1;
               error = true;
           } else if (slotParms.mship2.equalsIgnoreCase("House") || slotParms.mship2.equalsIgnoreCase("House Awaiting Golf") || slotParms.mship2.equalsIgnoreCase("Special House")) {
               slotParms.player = slotParms.player2;
               error = true;
           } else if (slotParms.mship3.equalsIgnoreCase("House") || slotParms.mship3.equalsIgnoreCase("House Awaiting Golf") || slotParms.mship3.equalsIgnoreCase("Special House")) {
               slotParms.player = slotParms.player3;
               error = true;
           } else if (slotParms.mship4.equalsIgnoreCase("House") || slotParms.mship4.equalsIgnoreCase("House Awaiting Golf") || slotParms.mship4.equalsIgnoreCase("Special House")) {
               slotParms.player = slotParms.player4;
               error = true;
           } else if (slotParms.mship5.equalsIgnoreCase("House") || slotParms.mship5.equalsIgnoreCase("House Awaiting Golf") || slotParms.mship5.equalsIgnoreCase("Special House")) {
               slotParms.player = slotParms.player5;
               error = true;
           }

           if (error) {

               returnMsg = "<H3>Member Restricted</H3><BR>" +
                       "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to bring guests on this day." +
                       "<BR>House, House Awaiting Golf, and Special House members are not allowed to bring guests on weekends and holidays." +
                       "<BR><BR>Please remove all guests from this tee time or return to the tee sheet.";
           }
       }
       
   } else if (slotParms.club.equals("engineerscc")) {

       // If one player slot contains a "Child" mship, but none of the player slots contain a Primary or Spouse member type, restrict member from booking.
       if ((slotParms.mship1.equals("Child") || slotParms.mship2.equals("Child") || slotParms.mship3.equals("Child") || slotParms.mship4.equals("Child") || slotParms.mship5.equals("Child")) &&
           (!slotParms.mtype1.startsWith("Primary") && !slotParms.mtype1.startsWith("Spouse") &&
            !slotParms.mtype2.startsWith("Primary") && !slotParms.mtype2.startsWith("Spouse") &&
            !slotParms.mtype3.startsWith("Primary") && !slotParms.mtype3.startsWith("Spouse") &&
            !slotParms.mtype4.startsWith("Primary") && !slotParms.mtype4.startsWith("Spouse") &&
            !slotParms.mtype5.startsWith("Primary") && !slotParms.mtype5.startsWith("Spouse"))) {

           error = true;

           if (slotParms.mship1.equals("Child")) {
               slotParms.player = slotParms.player1;
           } else if (slotParms.mship2.equals("Child")) {
               slotParms.player = slotParms.player2;
           } else if (slotParms.mship3.equals("Child")) {
               slotParms.player = slotParms.player3;
           } else if (slotParms.mship4.equals("Child")) {
               slotParms.player = slotParms.player4;
           } else if (slotParms.mship5.equals("Child")) {
               slotParms.player = slotParms.player5;
           }

           returnMsg = "<H3>Member Restricted</H3><BR>" +
                   "<BR><BR>Sorry, " +slotParms.player+ " is not accompanied by a Adult Member." +
                   "<BR>Child Members must be accompanied by an Adult Member in all tee times." +
                   "<BR><BR>Please add an Adult Member to this tee time or return to the tee sheet.";
       }

   } else if (slotParms.club.equals("bearpath")) {

       //  If more than 4 days in advance, Dependent, Certified Dependent, and CD Plus members must be accompanied by a Primary or Spouse member.
       if (slotParms.ind > 4 && 
          (slotParms.mtype1.equals("Dependent") || slotParms.mtype1.equals("Certified Dependent") || slotParms.mtype1.equals("CD Plus") || 
           slotParms.mtype2.equals("Dependent") || slotParms.mtype2.equals("Certified Dependent") || slotParms.mtype2.equals("CD Plus") || 
           slotParms.mtype3.equals("Dependent") || slotParms.mtype3.equals("Certified Dependent") || slotParms.mtype3.equals("CD Plus") || 
           slotParms.mtype4.equals("Dependent") || slotParms.mtype4.equals("Certified Dependent") || slotParms.mtype4.equals("CD Plus") || 
           slotParms.mtype5.equals("Dependent") || slotParms.mtype5.equals("Certified Dependent") || slotParms.mtype5.equals("CD Plus")) &&
           !slotParms.mtype1.startsWith("Primary") && !slotParms.mtype1.startsWith("Spouse") &&
           !slotParms.mtype2.startsWith("Primary") && !slotParms.mtype2.startsWith("Spouse") &&
           !slotParms.mtype3.startsWith("Primary") && !slotParms.mtype3.startsWith("Spouse") &&
           !slotParms.mtype4.startsWith("Primary") && !slotParms.mtype4.startsWith("Spouse") &&
           !slotParms.mtype5.startsWith("Primary") && !slotParms.mtype5.startsWith("Spouse")) {

           error = true;

           if (slotParms.mtype1.equals("Dependent") || slotParms.mtype1.equals("Certified Dependent") || slotParms.mtype1.equals("CD Plus")) {
               slotParms.player = slotParms.player1;
           } else if (slotParms.mtype2.equals("Dependent") || slotParms.mtype2.equals("Certified Dependent") || slotParms.mtype2.equals("CD Plus")) {
               slotParms.player = slotParms.player2;
           } else if (slotParms.mtype3.equals("Dependent") || slotParms.mtype3.equals("Certified Dependent") || slotParms.mtype3.equals("CD Plus")) {
               slotParms.player = slotParms.player3;
           } else if (slotParms.mtype4.equals("Dependent") || slotParms.mtype4.equals("Certified Dependent") || slotParms.mtype4.equals("CD Plus")) {
               slotParms.player = slotParms.player4;
           } else if (slotParms.mtype5.equals("Dependent") || slotParms.mtype5.equals("Certified Dependent") || slotParms.mtype5.equals("CD Plus")) {
               slotParms.player = slotParms.player5;
           }

           returnMsg = "<H3>Member Restricted</H3><BR>" +
                   "<BR><BR>Sorry, " +slotParms.player+ " must be accompanied by a Primary or Spouse member." +
                   "<BR><BR>All Dependent categories must be accompanied by a Primary or Spouse member when booking a tee time beyond 4 days in advance. " +
                   "<BR>If no Primary or Spouse members will be playing in this tee time, it must be booked within 4 days in advance." +
                   "<BR><BR>Please add a Primary or Spouse member to this tee time or return to the tee sheet.";
       }
       
   } else if (slotParms.club.equals("castlepines")) {

       // Do not allow X's to be booked into tee times after they have been cleared out for that day.
       int advdays = 2;
       int cur_time = Utilities.getTime(con);

       if (slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday")) {
           advdays = 3;
       }

       if ((slotParms.ind < advdays || (slotParms.ind == advdays && cur_time >= 1700)) &&
           (slotParms.player1.equalsIgnoreCase("x") || slotParms.player2.equalsIgnoreCase("x") || slotParms.player3.equalsIgnoreCase("x") ||
            slotParms.player4.equalsIgnoreCase("x") || slotParms.player5.equalsIgnoreCase("x"))) {

           returnMsg = "<H3>Invalid Request</H3>" +
                   "<BR>Sorry, X's are no longer allowed in this tee time." +
                   "<BR>X's can no longer be used in a tee time after the X's for that day have been cleared from the tee sheets." +
                   "<BR><BR>Please remove all X's from this tee time or return to the tee sheet.";
       }

   } else if (slotParms.club.equals("discoverybay")) {

       // Do not allow X's to be booked into tee times after they have been cleared out for that day.
       int cur_time = Utilities.getTime(con);

       if ((slotParms.ind < 1 || (slotParms.ind == 1 && cur_time >= 1200)) &&
           (slotParms.player1.equalsIgnoreCase("x") || slotParms.player2.equalsIgnoreCase("x") || slotParms.player3.equalsIgnoreCase("x") ||
            slotParms.player4.equalsIgnoreCase("x") || slotParms.player5.equalsIgnoreCase("x"))) {

           returnMsg = "<H3>Invalid Request</H3>" +
                   "<BR>Sorry, X's are no longer allowed in this tee time." +
                   "<BR>X's can no longer be used in a tee time after the X's for that day have been cleared from the tee sheets." +
                   "<BR><BR>Please remove all X's from this tee time or return to the tee sheet.";
       }

   } else if (slotParms.club.equals("tpcboston")) {

      //
      //  TPC Boston - Certain mships are only allowed one round per member number per day.
      //
      error = checkTPCBostonMships(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Daily Tee Time Origination Limit Reached</H3>" +
                     "<BR>Sorry, but you, or another member with the same member number, already originated another tee time on this day." +
                     "<BR><BR>Please return to the tee sheet and select another day.";
      }
      
   } else if (slotParms.club.equals("napervillecc")) {

       boolean existingTime = false;

       // See if this is an existing advance guest time.  If so, we don't want to update the custom_int value.
       if (slotParms.custom_int > 7) {

           existingTime = true;
       }

       // If more than 7 days in advance, or time has already been designated as an Advance Guest Time, do custom processing.
       if (slotParms.ind > 7 || slotParms.custom_int > 7) {

           if (slotParms.guests > 0 && slotParms.members > 0) {

               if (!existingTime) slotParms.custom_int = slotParms.ind;

           } else {

                   returnMsg = "<H3>No " + (slotParms.guests == 0 ? "Guests" : "Members") + " Found</H3><BR>" +
                               "<BR><BR>Sorry, tee times booked more than 7 days in advance must contain at least one guest and one member." +
                               "<BR><BR>Please add a " + (slotParms.guests == 0 ? "guest" : "member") + " or select a tee time that is not designated as an advance time.";
           }

       } else {

           slotParms.custom_int = 0;
       }

   } else if (slotParms.club.equals("ccfairfax")) {

      if (shortDate >= 401 && shortDate <= 1031) {
          
          int mtype_count = 0;
          int cur_time = Utilities.getTime(con);
          long cur_date = Utilities.getDate(con);

          String day_name = Utilities.getDayNameFromDate((int)cur_date);

          //
          //  CC of Fairfax - Tee times booked between 8:00am-11:59am on Thursday for the following Saturday, or Friday for the following Sunday or Monday must contain at least 3 primary members.
          //
          if (cur_time >= 800 && cur_time <= 1159 && ((day_name.equals("Thursday") && slotParms.date == Utilities.getDate(con, 2)) ||
                  (day_name.equals("Friday") && (slotParms.date == Utilities.getDate(con, 2) || slotParms.date == Utilities.getDate(con, 3))))) {

              if (slotParms.mtype1.startsWith("Primary")) mtype_count++;
              if (slotParms.mtype2.startsWith("Primary")) mtype_count++;
              if (slotParms.mtype3.startsWith("Primary")) mtype_count++;
              if (slotParms.mtype4.startsWith("Primary")) mtype_count++;
              if (slotParms.mtype5.startsWith("Primary")) mtype_count++;

              if (mtype_count < 3) {
                  error = true;
              }
          }

          if (error == true) {         // if we hit an error

             if (day_name.equals("Thursday")) {
                 returnMsg = "<H3>Not Enough Primary Members</H3>" +
                             "<BR>Sorry, but tee times booked between 8:00am and 11:59am on Thursday for the following Saturday must contain at least 3 Primary members (" + mtype_count + " found)." +
                             "<BR><BR>Please add additional Primary members to this time or select another day.";
             } else if (day_name.equals("Friday")) {
                 returnMsg = "<H3>Not Enough Primary Members</H3>" +
                             "<BR>Sorry, but tee times booked between 8:00am and 11:59am on Friday for the following Sunday or Monday Holiday must contain at least 3 Primary members (" + mtype_count + " found)." +
                             "<BR><BR>Please add additional Primary members to this time or select another day.";
             }
          }

          // If we haven't already hit on the previous custom, check to ensure that bookings for Sat/Sun/Mon prior to noon include at least 3 players.
          if (!error) {

               if (!ProcessConstants.isProshopUser(slotParms.user) && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.day.equals("Monday")) && slotParms.time < 1200 && (slotParms.members + slotParms.guests < 3)) {

                   error = true;

                   returnMsg = "<H3>Not Enough Players</H3>"
                           + "<BR>Sorry, but tee times on Saturday, Sunday, and Monday prior to 12:00pm must contain at least 3 players."
                           + "<BR><BR>Please add additional players to this time or select another day.";
               }
          }
      }

   } else if (slotParms.club.equals("miravista")) {

       error = checkMiraVistaGuestTimes(slotParms, con);

       if (error) {

           returnMsg = "<H3>Guest Limit Exceeded</H3>" +
                   "<BR>Sorry, there are already 2 guest times booked this hour." +
                   "<BR>Only 2 tee times including guests are allowed per hour Tuesday-Friday after 7am." +
                   "<BR><BR>Please return to the tee sheet and select another tee time during a different hour, or on a different day.";
           
       } else if (!ProcessConstants.isProshopUser(slotParms.user)) {
           
           if (slotParms.guests == 0 && (slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Thursday") || slotParms.day.equals("Friday"))) {

               returnMsg = "<H3>No Guests Found</H3>" +
                       "<BR>Sorry, tee time bookings for Tuesday-Friday must include at least one guest." +
                       "<BR><BR>Please add a guest or return to the tee sheet and select another day.";
           }
       }

   } else if (slotParms.club.equals("indianridgecc")) {

       //
       //  Indian Ridge CC - Certain mships are only allowed 30 rounds between 11/1 and 6/30 each year.
       //
       error = checkIndianRidgeMships(slotParms, con);

       if (error) {

           returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                   "<BR>Sorry, but " +slotParms.player+ " has already played, or is scheduled to play, their maximum allowed rounds." +
                   "<BR>Limited Equity Golf Fitness and Limited Equity Golf Social members are limited to 30 rounds between 11/1 and 6/30 each year." +
                   "<BR><BR>Please remove this player or return to the tee sheet.";
       }

   } else if (slotParms.club.equals("capitalcityclub")) {

       //
       //  Capital City Club - Dependents cannot be part of a tee time booked prior to 8:30am
       //
       int cur_time = Utilities.getTime(con);

       if (cur_time < 830 && 
          ((slotParms.ind == 3 && (slotParms.day.equals("Sunday") || slotParms.day.equals("Monday") || slotParms.day.equals("Friday") || slotParms.day.equals("Saturday"))) ||
           (slotParms.ind == 4 && slotParms.day.equals("Tuesday")) ||
           (slotParms.ind == 5 && slotParms.day.equals("Wednesday")) ||
           (slotParms.ind == 6 && slotParms.day.equals("Thursday"))) &&
          (slotParms.mtype1.equals("Dependent") || slotParms.mtype2.equals("Dependent") || slotParms.mtype3.equals("Dependent") || slotParms.mtype4.equals("Dependent") || slotParms.mtype5.equals("Dependent"))) {

           error = true;

           if (slotParms.mtype1.equals("Dependent")) {
               slotParms.player = slotParms.player1;
           } else if (slotParms.mtype2.equals("Dependent")) {
               slotParms.player = slotParms.player2;
           } else if (slotParms.mtype3.equals("Dependent")) {
               slotParms.player = slotParms.player3;
           } else if (slotParms.mtype4.equals("Dependent")) {
               slotParms.player = slotParms.player4;
           } else if (slotParms.mtype5.equals("Dependent")) {
               slotParms.player = slotParms.player5;
           }
       }

       if (error) {

           returnMsg = "<H3>Member Restricted</H3><BR>" +
                   "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to be a part of tee times booked this early." +
                   "<BR><BR>Dependents cannot be included in tee times booked prior to 8:30am the morning of the day they become available for booking. " +
                   "<BR><BR>Please remove any dependents from this tee time or return to the tee sheet.";
       }
       
   } else if (slotParms.club.equals("wildcatruncc")) {
       
       
       // Wildcat Run CC - Golf Equity members cannot book a tee time with guests until 6 days in advance at 7am, Golf Annual can't until 3 days in advance at 7am.
       PreparedStatement pstmtx = null;
       ResultSet rsx = null;
       
       String curr_mship = "";
       
       int cur_time = Utilities.getTime(con);
       
       // Look up the current user's membership type
       try {
           
           pstmtx = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
           pstmtx.clearParameters();
           pstmtx.setString(1, slotParms.user);
           
           rsx = pstmtx.executeQuery();
           
           if (rsx.next()) {
               curr_mship = rsx.getString("m_ship");
           }
           
       } catch (Exception exc) {
           Utilities.logError("verifyCustom.checkCustoms1 - wildcatruncc - Error looking up member's mship - Err: " + exc.toString());
       } finally {
           
           try { rsx.close(); }
           catch (Exception ignore) { }
           
           try { pstmtx.close(); }
           catch (Exception ignore) { }
       }
       
       if (!curr_mship.equals("") && slotParms.guests > 0 && (
              (curr_mship.equalsIgnoreCase("Golf Equity") && (slotParms.ind > 6 || (slotParms.ind == 6 && cur_time < 700))) || 
              (curr_mship.equalsIgnoreCase("Golf Annual") && (slotParms.ind > 3 || (slotParms.ind == 3 && cur_time < 700))) ||
              (curr_mship.equalsIgnoreCase("Gated Community Preview") && slotParms.ind > 5 || (slotParms.ind == 5 && cur_time < 700)))) {
           
           error = true;
       }
       
       if (error) {
           
           returnMsg = "<H3>Guests Not Allowed</H3><BR>" +
                   "<BR><BR>Sorry, " + curr_mship + " members are not allowed to book tee times including guests this far in advance." +
                   "<BR><BR>Guests can not be included in tee times booked by " + curr_mship + " members until " + (curr_mship.equalsIgnoreCase("Golf Equity") ? "6" : (curr_mship.equalsIgnoreCase("Golf Annual") ? "3" : "5")) + " days in advance at 7:00am. " +
                   "<BR><BR>Please remove any guests from this tee time or return to the tee sheet.";
       }
     
   } else if (slotParms.club.equals("turnerhill")) {
       
       PreparedStatement pstmtx = null;
       ResultSet rsx = null;
       
       int unaccompCount = 0;
       
       // Only check for Proshop users
       if (ProcessConstants.isProshopUser(slotParms.user)) {
           
           try {

               pstmtx = con.prepareStatement("SELECT teecurr_id FROM teecurr2 WHERE date = ? AND teecurr_id <> ? AND "
                       + "(player1 like 'Unaccomp Guest%' OR player2 like 'Unaccomp Guest%' OR player3 like 'Unaccomp Guest%' OR player4 like 'Unaccomp Guest%' OR player5 like 'Unaccomp Guest%')");
               pstmtx.clearParameters();
               pstmtx.setLong(1, slotParms.date);
               pstmtx.setInt(2, slotParms.teecurr_id);

               rsx = pstmtx.executeQuery();

               while (rsx.next()) {
                   unaccompCount++;
               }

               if (unaccompCount >= 4) {
                   error = true;
               }

           } catch (Exception exc) {

           } finally {

               try { rsx.close(); }
               catch (Exception ignore) { }

               try { pstmtx.close(); }
               catch (Exception ignore) { }
           }

           if (error) {

               returnMsg = "<H3>Unaccomp Guest Quota Reached</H3>" +
                       "<BR><BR>Sorry, there are already 4 tee times including Unaccomp Guest rounds booked for this day." +
                       "<BR><BR>Please remove any Unaccomp Guests from this tee time or return to the tee sheet.";
           }
       }
       
   } else if (slotParms.club.equals("firerockcc")) {
       
       error = checkFireRockMships(slotParms, con);

       if (error) {

           returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                   "<BR>Sorry, but " +slotParms.player+ " has already played, or is scheduled to play, their maximum allowed rounds." +
                   "<BR>Sports members are limited to 14 rounds between 10/20 and 5/15 each year." +
                   "<BR><BR>Please remove this player or return to the tee sheet.";
       }
       
   } else if (slotParms.club.equals("missionvalleycc")) {
       /* Custom removed at club's request.  May need again later.
       // Custom only applies between November 1 and April 30
       if (shortDate >= 1101 || shortDate <= 430) {
           
           error = checkMissionValleyMships(slotParms, con);

           if (error) {

               returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                       "<BR>Sorry, but " + slotParms.player + ", or another member with the same member number, has already played, or is scheduled to play, their maximum allowed rounds." +
                       "<BR>Social members are limited to 6 rounds per membership number between 11/1 and 4/30 each year." +
                       "<BR><BR>Please remove this player or return to the tee sheet.";
           }
       }
        */
       
   } else if (slotParms.club.equals("hallbrookcc")) {
     
       // Restrictions only apply from 4/1 - 10/31
       if (shortDate >= 401 && shortDate <= 1031) {
           
           int err = 0;
           
           err = checkHallbrookJrsAndDeps(slotParms);
           
           if (err > 0) {
               
               switch(err) {
                   
                   case 1:
                       returnMsg = "<H3>Member Restricted</H3><BR>" +
                               "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to be a part of this tee time." +
                               "<BR><BR>Junior members cannot be a part of tee times earlier than 3:00pm. " +
                               "<BR><BR>Please remove this member from this tee time or return to the tee sheet.";
                       break;
                   case 2:
                       returnMsg = "<H3>Member Restricted</H3><BR>" +
                               "<BR><BR>Sorry, " +slotParms.player+ " must be accompanied by an Adult Member at this time." +
                               "<BR><BR>Junior members cannot be a part of tee times that do not include at least one Adult member. " +
                               "<BR><BR>Please add an Adult member to this tee time or return to the tee sheet.";
                       break;
                   case 3:
                       returnMsg = "<H3>Member Restricted</H3><BR>" +
                               "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to be a part of tee time." +
                               "<BR><BR>Dependent members cannot be a part of tee times between 11:30am and 1:00pm on Fridays. " +
                               "<BR><BR>Please remove this member from this tee time or return to the tee sheet.";
                       break;
                   case 4:
                       returnMsg = "<H3>Member Restricted</H3><BR>" +
                               "<BR><BR>Sorry, " +slotParms.player+ " is not allowed to be a part of tee time." +
                               "<BR><BR>Dependent members cannot be a part of tee times earlier than 10:00am on Weekends and Holidays. " +
                               "<BR><BR>Please remove this member from this tee time or return to the tee sheet.";
                       break;
                   case 5:
                       returnMsg = "<H3>Member Restricted</H3><BR>" +
                               "<BR><BR>Sorry, " +slotParms.player+ " must be accompanied by an Adult Member at this time." +
                               "<BR><BR>Dependent members cannot be a part of tee times between 10:00am and 12:00pm on Weekends and Holidays that do not include at least one Adult member. " +
                               "<BR><BR>Please add an Adult member to this tee time or return to the tee sheet.";
                       break;
                   default:
                       break;
               }
           }
       }
       
       
   } else if (slotParms.club.equals("minikahda")) {
       
       returnMsg = checkMinikahdaGuests(date, shortDate, slotParms, con);
       
      
   } else if (slotParms.club.equals("oronoquecc")) {
       
       if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && slotParms.time < 1200) {
           
           int playerCount = 0;

           if (!slotParms.player1.equals("")) {
               playerCount++;
           }
           if (!slotParms.player2.equals("")) {
               playerCount++;
           }
           if (!slotParms.player3.equals("")) {
               playerCount++;
           }
           if (!slotParms.player4.equals("")) {
               playerCount++;
           }
           if (!slotParms.player5.equals("")) {
               playerCount++;
           }

           if (playerCount < 3) {

               returnMsg = "<H3>Not Enough Players</H3>" +
                       "<BR>Sorry, but weekend tee times prior to 12:00PM must contain at least 3 players." +
                       "<BR><BR>Please add additional players or return to the tee sheet.";
           }
       }
       
   } else if (slotParms.club.equals("awbreyglen")) {
     
       // Non-Resident members are limited to 30 rounds per family member number per calendar year.
       error = checkAwbreyGlenMnumRoundCounts(slotParms, con);

       if (error) {

           returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                   "<BR>Sorry, but " + slotParms.player + ", or another member with the same member number, has already played, or is scheduled to play, their maximum allowed rounds." +
                   "<BR>Non-Resident Golf members are limited to 30 rounds per membership number each year." +
                   "<BR><BR>Please remove this player or return to the tee sheet.";
       }
       
   } else if (slotParms.club.equals("imperialgc")) {
       
       // Custom only runs between 12/1 and 4/30 each year.
       if (shortDate >= 1101 || shortDate <= 430) {
           
           // "Associate" members are allowed 35 18-hole (or combined 9-hole) rounds per member number each year between 12/1 and 4/30.
           error = checkWinterSeasonMshipRoundCountsByMnum(slotParms, "Associate", 1201, 430, 35, true, con);

           if (error) {

               returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                       "<BR>Sorry, but " + slotParms.player + ", or another member with the same member number, has already played, or is scheduled to play, their maximum allowed rounds." +
                       "<BR><BR>Associate members are limited to 35 18-hole rounds (or combined 9-hole rounds) per membership number between 11/1 and 4/30 each year." +
                       "<BR><BR>Please remove this player or return to the tee sheet.";           
           } else {
               
               //Dining Summer Golf members are allowed 6 18-hole rounds (or 12 9-hole) per family member number between 12/1 and 4/30. 
               error = checkWinterSeasonMshipRoundCountsByMnum(slotParms, "Dining Summer Golf", 1201, 430, 6, false, con);

               if (error) {

                   returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                           "<BR>Sorry, but " + slotParms.player + ", or another member with the same member number, has already played, or is scheduled to play, their maximum allowed rounds." +
                           "<BR><BR>Dining Summer Golf members are limited to 6 18-hole rounds (or combined 9-hole rounds) per membership number between 11/1 and 4/30 each year." +
                           "<BR><BR>Please remove this player or return to the tee sheet.";           
               }
           }
       }
    
          
   } else if (slotParms.club.equals("windstarclub")) {
       
       // Custom only runs between 11/15 and 4/15 each year.
       if (shortDate >= 1115 || shortDate <= 415) {
           
           // "Social Golf" members are allowed 5 18-hole (or combined 9-hole) rounds per member number each year between 11/15 and 4/15.
           error = checkWinterSeasonMshipRoundCountsByMnum(slotParms, "Social Golf", 1115, 415, 5, true, con);

           if (error) {

               returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                       "<BR>Sorry, but " + slotParms.player + ", or another member with the same member number, has already played, or is scheduled to play, their maximum allowed rounds." +
                       "<BR><BR>Social Golf members are limited to 5 18-hole rounds (or combined 9-hole rounds) per membership number between 11/15 and 4/15 each year." +
                       "<BR><BR>Please remove this player or return to the tee sheet.";           
           }
       }
       
   } else if (slotParms.club.equals("deserthorizonscc")) {
       //Monday, Thursday, Friday and Sunday during the week 1-1-2013 to 4-15-2013 from 8:00-10:00 AM
       
       // Desert Horizons CC - Check guests to make sure they are on a 1 to 1 ratio with members in this tee time. Applies Su/M/Th/F 1/1/13-4/15/13 8am-10am.
       //if (slotParms.date >= 20130101 && slotParms.date <= 20130415 && slotParms.time >= 800 && slotParms.time <= 1000 
       if (shortDate >= 101 && shortDate <= 415 && slotParms.time >= 800 && slotParms.time <= 1000 
               && (slotParms.day.equals("Sunday") || slotParms.day.equals("Monday") || slotParms.day.equals("Thursday") || slotParms.day.equals("Friday"))) {

           int gcount = 0;
           
           gcount = checkDesertHorizonsCCGuestCount(slotParms, con);

           if (gcount > 6) {
               
               int gcount_disp = gcount - 6;
               
               if (gcount_disp > slotParms.guests) {
                   gcount_disp = slotParms.guests;
               }
               
               returnMsg = "<H3>Guest Limit Exceeded</H3>" +
                       "<BR>Sorry, but this tee time would exceed the limit of guests allowed this morning." +
                       "<BR>Only 6 guests total are permitted Sunday, Monday, Thursday, and Friday between 8:00AM and 10:00AM." +
                       "<BR><BR>Please remove at least " + gcount_disp + " guest" + (gcount_disp > 1 ? "s" : "") + " or return to the tee sheet.";

           }
       }
   } else if (slotParms.club.equals("oakmont")) {       // Oakmont custom starting in 2013. Check case 2223 in Salesforce for details.
           
       // Custom only applies to advance times
       if (((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.day.equals("Monday")) && slotParms.ind > 7) || slotParms.ind > 14) {
           
           if (slotParms.members != 1 || slotParms.guests != 3 
                   || (!slotParms.player2.startsWith("$75 Guest") && !slotParms.player2.startsWith("Guest") && !slotParms.player2.startsWith("Family Guest"))
                   || (!slotParms.player3.startsWith("$75 Guest") && !slotParms.player3.startsWith("Guest") && !slotParms.player3.startsWith("Family Guest"))
                   || (!slotParms.player4.startsWith("$75 Guest") && !slotParms.player4.startsWith("Guest") && !slotParms.player4.startsWith("Family Guest"))) {
               
               // Only print an error if it's a Member, or if it's a Proshop user and the number of 1 mem and 3 guest ratio is off.
               if (!ProcessConstants.isProshopUser(slotParms.user) || slotParms.members != 1 || slotParms.guests != 3) {

                   returnMsg = "<H3>Invalid Advance Time</H3>" +
                           "<BR>Sorry, but tee times booked outside of the standard days in advance must contain 1 member and 3 guests." +
                           "<BR><BR>Please adjust the players accordingly or return to the tee sheet.";
               }
               
           } else if (shortDate >= 601) {   // If tee time is prior to 6/1, as long as it has 1 mem and 3 guests of the appropriate types, no limitations apply.

               int gcount = 0;
               
               long cur_date = Utilities.getDate(con);
               long cur_yy = cur_date / 10000;
               long cur_mm = (cur_date - (cur_yy * 10000)) / 100;
               long cur_dd = (cur_date - (cur_yy * 10000)) - (cur_mm * 100);       // get day
               long cur_shortDate = (cur_mm * 100) + cur_dd;
               
               int max_guests = 2;      // The current max allowed after 6/1 increases by 2 on 4/15 and 5/14.

               if (cur_shortDate >= 513) {
                   max_guests = 6;
               } else if (cur_shortDate >= 415) {
                   max_guests = 4;
               }     

               // Guest limit only applies before 6/1 has passed. Once passed, members can book an unlimited number of these advance times
               if (cur_shortDate < 601) {
                   
                   gcount = checkOakmontGuestCount(slotParms, slotParms.user1, con);

                   if (gcount >= max_guests) {

                       returnMsg = "<H3>Guest Limit Exceeded</H3>" +
                               "<BR>Sorry, but this tee time would exceed the limit of advance guest times allowed per member." +
                               "<BR>At this time, members may only have " + max_guests + " advance guest times from June 1st through the end of the season." +
                               "<BR><BR>Please return to the tee sheet and select an earlier date.";

                   }
               }
           }
           
           
       }
       
   } else if (slotParms.club.equals("lochlloyd")) {
       
       if (slotParms.ind > 5 && slotParms.guests > 0) {
           
           returnMsg = "<H3>Guests Not Allowed</H3>"
                   + "<BR>Sorry, but guests cannot be booked this far in advance."
                   + "<BR>Guests are not allowed in tee times booked more than 5 days in advance."
                   + "<BR><BR>Please remove all guests from this time, or return to the tee sheet and select an earlier date.";
       }
       
   } else if (slotParms.club.equals("elgincc")) {

       int index = slotParms.ind;
       
       if (index > 365) {
           index = 0;
       }
       
       // Determine if we're outside of standard days in advance or not
       if ((slotParms.day.equals("Saturday") && index > 3) // 3 days in advance on Saturdays
               || (slotParms.day.equals("Sunday") && index > 4) // 4 days on Sun
               || (slotParms.day.equals("Monday") && index > 5) // 5 days on Mon
               || (index > 7)) {
       
           // Advance times must have at least one guest
           if (slotParms.guests == 0) {

               returnMsg = "<H3>Guest Not Found</H3>"
                       + "<BR>Sorry, but all tee times booked outside of the normal days in advance must contain at least one guest."
                       + "<BR><BR>Please add a guest to this tee time or return to the tee sheet.";
           }
       } else {
                    
           // If tee time was originally booked as a guest time, member cannot sub them out once within standard booking window
           if (slotParms.guests == 0 && slotParms.custom_int > 0) {

               returnMsg = "<H3>Guest Not Found</H3>"
                       + "<BR>Sorry, but tee times that were originally booked outside normal days in advance must contain at least one guest."
                       + "<BR><BR>Please add a guest to this tee time or return to the tee sheet.";
           }
       }
       
       
   } else if (slotParms.club.equals("interlachen")) {
     
       if (shortDate >= 528 && shortDate <= 902 && !slotParms.day.equals("Sunday") && slotParms.time >= 730 && slotParms.time <= 1450 
              && ((!slotParms.player1.equals("") && slotParms.p1cw.equals("WA") && !slotParms.mtype1.startsWith("Jr")) 
               || (!slotParms.player2.equals("") && slotParms.p2cw.equals("WA") && !slotParms.mtype2.startsWith("Jr")) 
               || (!slotParms.player3.equals("") && slotParms.p3cw.equals("WA") && !slotParms.mtype3.startsWith("Jr")) 
               || (!slotParms.player4.equals("") && slotParms.p4cw.equals("WA") && !slotParms.mtype4.startsWith("Jr")) 
               || (!slotParms.player5.equals("") && slotParms.p5cw.equals("WA") && !slotParms.mtype5.startsWith("Jr")))) {
           
           returnMsg = "<H3>Mode of Transportation Not Allowed</H3>"
                   + "<BR>Sorry, but only Jr. members are allowed to carry in tee times between 7:30AM - 2:50PM from Memorial Day to Labor Day."
                   + "<BR><BR>Please select caddie or motor cart or return to the tee sheet.";
       }
       
   } else if (slotParms.club.equals("minneapolis")) {
       
       if (!slotParms.player1.equals("") && !slotParms.player2.equals("") && !slotParms.player3.equals("") && !slotParms.player4.equals("") && !slotParms.player5.equals("")
               && slotParms.guests > 2) {
           
           returnMsg = "<H3>Guest Limit Exceeded</H3>"
                   + "<BR>Sorry, but tee times with 5 players may contain no more than 2 guests."
                   + "<BR><BR>Please adjust the players accordingly, or return to the tee sheet.";
       }
       
   } else if (slotParms.club.equals("greenacrescountryclub")) {
       
       if (!slotParms.player1.equals("") && !slotParms.player2.equals("") && !slotParms.player3.equals("") && !slotParms.player4.equals("") && !slotParms.player5.equals("")
               && slotParms.guests > 1) {
           
           returnMsg = "<H3>Guest Limit Exceeded</H3>"
                   + "<BR>Sorry, but tee times with 5 players may contain no more than 1 guest."
                   + "<BR><BR>Please adjust the players accordingly, or return to the tee sheet.";
       }
       
   } else if (slotParms.club.equals("brooklawn")) {
     
       if (((shortDate >= 522 && shortDate <= 904 && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday"))) || slotParms.date == Hdate1 || slotParms.date == Hdate3)
               && slotParms.time >= 1028 && slotParms.time <= 1400) {
           
           int playerCount = 0;

           if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("X")) {
               playerCount++;
           }

           if (playerCount < 3) {

               returnMsg = "<H3>Not Enough Players</H3>" +
                       "<BR>Sorry, but weekend tee times between 10:28 AM and 2:00 PM from 5/22 through 9/4 must contain at least 3 players (not including X's)." +
                       "<BR><BR>Please add additional players or return to the tee sheet.";
           }
       }
       
   } else if (slotParms.club.equals("rivertoncc")) {
     
       // House members are limited to 10 rounds per family member number from 4/1 through 10/31 each year.
       error = checkRivertonCCMnumRoundCounts(slotParms, con);

       if (error) {

           returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                   "<BR>Sorry, but " + slotParms.player + ", or another member with the same member number, has already played, or is scheduled to play, their maximum allowed rounds." +
                   "<BR>House members are limited to 10 rounds per membership number between 4/1 and 10/31 each year." +
                   "<BR><BR>Please remove this player or return to the tee sheet.";
       }
   } else if (slotParms.club.equals("lakewoodccdallas")) {
       
       if ((slotParms.day.equals("Friday") && slotParms.time >= 1100 && slotParms.time <= 1350) 
               || ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && slotParms.time >= 800 && slotParms.time <= 1350)) {
           
           int playerCount = 0;

           if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("X")) {
               playerCount++;
           }

           if (playerCount < 3) {

               if (slotParms.day.equals("Friday")) {

                   returnMsg = "<H3>Not Enough Players</H3>" +
                           "<BR>Sorry, but Friday tee times between 11:00 AM and 1:50 PM must contain at least 3 players (not including X's)." +
                           "<BR><BR>Please add additional players or return to the tee sheet.";
               } else {
                   
                   returnMsg = "<H3>Not Enough Players</H3>" +
                           "<BR>Sorry, but tee times on Saturday and Sunday between 8:00 AM and 1:50 PM must contain at least 3 players (not including X's)." +
                           "<BR><BR>Please add additional players or return to the tee sheet.";
               }
           }
       }
   } else if (slotParms.club.equals("championsrun")) {
       
       if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && shortDate >= 401 && shortDate <= 930 && slotParms.time >= 700 && slotParms.time < 1200 
               && (slotParms.mtype1.startsWith("Dependent") || slotParms.mtype2.startsWith("Dependent") || slotParms.mtype3.startsWith("Dependent") || slotParms.mtype4.startsWith("Dependent"))
               && !slotParms.mtype1.startsWith("Adult") && !slotParms.mtype2.startsWith("Adult") && !slotParms.mtype3.startsWith("Adult") && !slotParms.mtype4.startsWith("Adult")) {
           
           if (slotParms.mtype1.startsWith("Dependent")) {
               slotParms.player = slotParms.player1;
           } else if (slotParms.mtype2.startsWith("Dependent")) {
               slotParms.player = slotParms.player2;
           } else if (slotParms.mtype3.startsWith("Dependent")) {
               slotParms.player = slotParms.player3;
           } else if (slotParms.mtype4.startsWith("Dependent")) {
               slotParms.player = slotParms.player4;
           } else if (slotParms.mtype5.startsWith("Dependent")) {
               slotParms.player = slotParms.player5;
           }
           
           returnMsg = "<H3>Member Restricted</H3><BR>" +
                   "<BR><BR>Sorry, " +slotParms.player+ " is not accompanied by a Adult Member." +
                   "<BR>Dependent Members must be accompanied by an Adult Member in tee times between 7am and 12pm, Saturdays and Sundays." +
                   "<BR><BR>Please add an Adult Member to this tee time or return to the tee sheet.";
       }
   } else if (slotParms.club.equals("walpolecc")) {
       
       if ((slotParms.day.equals("Friday") && slotParms.time >= 1100 && slotParms.time < 1330) 
               || ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday")) && slotParms.time < 1500)) {
           
           int playerCount = 0;

           if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("X")) {
               playerCount++;
           }
           
           if (playerCount < 2) {
               if (slotParms.day.equals("Friday")) {

                   returnMsg = "<H3>Not Enough Players</H3>"
                           + "<BR>Sorry, but Friday tee times between 11:00 AM and 1:30 PM must contain at least 2 players (not including X's)."
                           + "<BR><BR>Please add additional players or return to the tee sheet.";
               } else {

                   returnMsg = "<H3>Not Enough Players</H3>"
                           + "<BR>Sorry, but Weekend tee times prior to 3:00 PM must contain at least 2 players (not including X's)."
                           + "<BR><BR>Please add additional players or return to the tee sheet.";
               }
           }
       }
   } else if (slotParms.club.equals("ridgecc")) {
       
       if (slotParms.guests > 0 && (slotParms.mship1.equalsIgnoreCase("Social") || slotParms.mship2.equalsIgnoreCase("Social") 
               || slotParms.mship3.equalsIgnoreCase("Social") || slotParms.mship4.equalsIgnoreCase("Social") || slotParms.mship5.equalsIgnoreCase("Social")) 
               && ((!slotParms.day.equals("Saturday") && !slotParms.day.equals("Sunday")) || slotParms.time < 1430)) {
           
           
           returnMsg = "<H3>Guests Not Allowed</H3>"
                   + "<BR>Sorry, but Social members may only book tee times with guests on Saturday and Sunday after 2:30pm."
                   + "<BR><BR>Please adjust the players accordingly, or return to the tee sheet.";
       }
       
   } else if (slotParms.club.equals("poycc")) {
       
       if ((slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.date == Hdate1 || slotParms.date == Hdate2b || slotParms.date == Hdate3) 
               && shortDate >= 501 && shortDate <= 930 && slotParms.time >= 630 && slotParms.time <= 1030) {
           
           int playerCount = 0;

           if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("X")) {
               playerCount++;
           }
           if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("X")) {
               playerCount++;
           }

           if (playerCount < 3) {

               returnMsg = "<H3>Not Enough Players</H3>" +
                       "<BR>Sorry, but tee times on Saturday, Sunday, and Holidays from 5/1 through 9/30 between 6:30 AM and 10:30 AM must contain at least 3 players (not including X's)." +
                       "<BR><BR>Please add additional players or return to the tee sheet.";
           }
       }       
       
   } else if (slotParms.club.equals("philcricket")) {
       
       if (slotParms.mship1.equalsIgnoreCase("Golf-in-Waiting B") || slotParms.mship2.equalsIgnoreCase("Golf-in-Waiting B") || slotParms.mship3.equalsIgnoreCase("Golf-in-Waiting B") 
               || slotParms.mship4.equalsIgnoreCase("Golf-in-Waiting B") || slotParms.mship5.equalsIgnoreCase("Golf-in-Waiting B")) {
           
           int round_count = checkPhilCricketGolfInWaitingRounds("Golf-in-Waiting B", slotParms, con);
           
           if (round_count > 3) {

               returnMsg = "<H3>Member Limit Exceeded</H3>" +
                       "<BR>Sorry, only three rounds by 'Golf-in-Waiting B' members per day, per course, can be on the tee sheet at any given time. This reservation would exceed that limit" +
                       "<BR><BR>Please contact the golf shop if you need assistance - (215)247-6113 x2";
           }
       }
       
   } else if (slotParms.club.equals("bayhill")) {
       
       if (shortDate >= 1201 || shortDate <= 430) {
           
           int playerCount = 0;

           if (!slotParms.player1.equals("")) {
               playerCount++;
           }
           if (!slotParms.player2.equals("")) {
               playerCount++;
           }
           if (!slotParms.player3.equals("")) {
               playerCount++;
           }
           if (!slotParms.player4.equals("")) {
               playerCount++;
           }
           if (!slotParms.player5.equals("")) {
               playerCount++;
           }

           if (playerCount < 2) {

               returnMsg = "<H3>Not Enough Players</H3>" +
                       "<BR>Sorry, but all tee times between 12/1 and 4/30 must contain at least two players." +
                       "<BR><BR>Please add additional players or return to the tee sheet.";
           }
       }       
       
   }
   
   
   /*else if (slotParms.club.equals("quecheeclub")) {  // Custom removed at club's request
       
       int cur_time = Utilities.getTime(con);
       
       // If between 5/26 and 9/30, or Memorial Day, and a Saturday or Sunday, tee times with guests can't be booked prior to 5 days in advance at 7:30am Eastern
       if (((shortDate >= 526 && shortDate <= 930) || date == Hdate1) && (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday"))) {
           
           if (slotParms.guests > 0 && (slotParms.ind > 5 || slotParms.ind == 5 && cur_time < 730)) {
               
               returnMsg = "<H3>Invalid Request</H3>" +
                       "<BR>Sorry, tee times with guests on weekends may only be booked within 5 days in advance starting at 7:30AM." +
                       "<BR><BR>Please remove any guests from this tee time or select a different day from the tee sheet.";
           }
       }
       
   } /*else if (slotParms.club.equals("demobradx")) {
       
       int modifiedSlots = 0;
       
       for (int i=0; i<5; i++) {
           
       }
       
       if (!slotParms.player1.equals("") && (slotParms.oldPlayer1.equals("") || !slotParms.player1.equals(slotParms.oldPlayer1))) modifiedSlots++;
       if (!slotParms.player2.equals("") && (slotParms.oldPlayer2.equals("") || !slotParms.player2.equals(slotParms.oldPlayer2))) modifiedSlots++;
       if (!slotParms.player3.equals("") && (slotParms.oldPlayer3.equals("") || !slotParms.player3.equals(slotParms.oldPlayer3))) modifiedSlots++;
       if (!slotParms.player4.equals("") && (slotParms.oldPlayer4.equals("") || !slotParms.player4.equals(slotParms.oldPlayer4))) modifiedSlots++;
       if (!slotParms.player5.equals("") && (slotParms.oldPlayer5.equals("") || !slotParms.player5.equals(slotParms.oldPlayer5))) modifiedSlots++;
       
       if (modifiedSlots == 1) {    // Only an issue if the number of modified slots is less than 2, but greater than 0, which means only if a single slot was changed

           returnMsg = "<H3>Not Enough New Players</H3>" +
                   "<BR>Sorry, but due to club policy, you must add at least 2 new players when booking or modifying a tee time." +
                   "<BR>If you would like to add a single player, please contact the golf shop for assistance." + 
                   "<BR><BR>Please add additional players or return to the tee sheet.";
       }
       
   }
   */
   
   /*else if (slotParms.club.equals("pmarshgc")) {     // Pelican Marsh GC

      //
      //  Pelican Marsh - Check for Membership Quotas (by family)
      //
      error = checkPMarshMNums(slotParms, con);

      if (error == true) {         // if we hit an error

         returnMsg = "<H3>Quota Exceeded for Membership</H3>" +
                     "<BR>Sorry, but " +slotParms.player+ " (and family) have already scheduled the maximum allowed rounds for the day." +
                     "<BR><BR>Please remove the player(s) or return to the tee sheet.";
      }

      if (returnMsg.equals("") && shortDate < 431) {      // if above ok and tee time is in Jan - April

          int playerCount = 0;
          int oldPlayerCount = 0;

          if (!slotParms.player1.equals("") && !slotParms.player1.equalsIgnoreCase("x")) playerCount++;
          if (!slotParms.player2.equals("") && !slotParms.player2.equalsIgnoreCase("x")) playerCount++;
          if (!slotParms.player3.equals("") && !slotParms.player3.equalsIgnoreCase("x")) playerCount++;
          if (!slotParms.player4.equals("") && !slotParms.player4.equalsIgnoreCase("x")) playerCount++;
          if (!slotParms.player5.equals("") && !slotParms.player5.equalsIgnoreCase("x")) playerCount++;
          
          if (!slotParms.oldPlayer1.equals("") && !slotParms.oldPlayer1.equalsIgnoreCase("x")) oldPlayerCount++;
          if (!slotParms.oldPlayer2.equals("") && !slotParms.oldPlayer2.equalsIgnoreCase("x")) oldPlayerCount++;
          if (!slotParms.oldPlayer3.equals("") && !slotParms.oldPlayer3.equalsIgnoreCase("x")) oldPlayerCount++;
          if (!slotParms.oldPlayer4.equals("") && !slotParms.oldPlayer4.equalsIgnoreCase("x")) oldPlayerCount++;
          if (!slotParms.oldPlayer5.equals("") && !slotParms.oldPlayer5.equalsIgnoreCase("x")) oldPlayerCount++;


          if (playerCount < 2) {

              String tempmsg = "";

              if (oldPlayerCount > 1) {
                  tempmsg = "Please contact the Golf Shop to make adjustments to this reservation.";
              } else {
                  tempmsg = "Please add additional players or return to the tee sheet.";
              }

              returnMsg = "<H3>Not Enough Players</H3>" +
                      "<BR>Sorry, but this tee time must include at least 2 players (not including x's)." +
                      "<BR><BR>" + tempmsg +
                      "<BR><BR>";

          }
      }

   }
*/
   // end of club checks*/

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
   //  break down date of tee time
   //
   int date = (int)slotParms.date;
   int yy = date / 10000;                             // get year
   int mm = (date - (yy * 10000)) / 100;              // get month
   int dd = (date - (yy * 10000)) - (mm * 100);       // get day

   int shortDate = (mm * 100) + dd;

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

            returnMsg = "<H3>Member Not Allowed To Book Unaccompanied Group</H3>" +
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

         returnMsg = "<H3>Guest Type Not Allowed</H3>" +
                     "<BR>Sorry, but " + slotParms.player + " is not allowed to specify the Guest-Centennial guest type." +
                     "<BR><BR>Please select a different guest type for this member or return to the tee sheet.";

      } else {

         //
         //  Custom to check for a max of 3 guest times on weekdays between 10:00 and 11:30
         //
         if (slotParms.guests > 1 && !slotParms.day.equals( "Saturday" ) && !slotParms.day.equals( "Sunday" ) &&
             !slotParms.day.equals( "Monday" ) && slotParms.time >= 1030 && slotParms.time < 1131 && shortDate >= 501 && shortDate <= 1017) {

            error = checkInterlachenFriGsts(slotParms, con);

            if (error == true) {         // if we hit an error

               returnMsg = "<H3>Guest Time Not Allowed</H3>" +
                           "<BR>Sorry, there are already 3 guest times (with more than 1 guest) scheduled this morning." +
                           "<BR>There can only be 3 tee times with 2 or 3 guests between 10:30 and 11:30 on weekdays." +
                           "<BR><BR>Please limit your request to 1 guest or select a different time of day.";
            }
         }
      }           // end of Interlachen checks

   } else if (slotParms.club.equals("olyclub")) {               //  Olympic Club

      //
      //  Olympic Club - check guest quotas based on member sub-type and course
      //
      if (slotParms.guests > 0 && (slotParms.course.equals("Ocean") || slotParms.course.equals("Lake"))) {      // if guests in request and not the Cliffs course

         //
         //  Custom Guest Quotas for the Ocean and Lake courses based on the membership privileges
         //
         error = checkOlyclubGstQuotas(slotParms, con);

         if (error == true) {         // if we hit an error

            returnMsg = "<H3>Guest Time Not Allowed</H3>" +
                        "<BR>Sorry, this request would exceed the guest quota allowed for " +slotParms.player+ "." +
                        "<BR><BR>Please contact the golf shop for assistance.";
         }
      }           // end of Olympic Club checks

   } else if (slotParms.club.equals("kinsale")) {                   //  Kinsale

       //
       //  Kinsale - Check for Weekend morning guest restriction, but exclude Marquee Golf members
       //
       error = checkKinsaleGuests(slotParms, con);

       if (error == true) {         // if we hit an error

           returnMsg = "<H3>Guest Type Not Allowed</H3>" +
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

         returnMsg = "<H3>Guest Quota Exceeded</H3><BR>" +
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

       if (error) {         // if we hit an error

           if (slotParms.day.equalsIgnoreCase("Saturday") && slotParms.time >= 1200) {
               returnMsg = "<H3>Guest Limit Exceeded</H3>" +
                       "<BR>Sorry, but " + slotParms.player + " is only allowed to have up to 3 guests in this tee time." +
                       "<BR><BR>Please remove some guests or return to the tee sheet.";
           } else {
               returnMsg = "<H3>Guest Limit Exceeded</H3>" +
                       "<BR>Sorry, but " + slotParms.player + " is not allowed to bring guests for this tee time." +
                       "<BR><BR>Please remove any guests associated with this member or return to the tee sheet.";
           }
       }
   } else if (slotParms.club.equals("deserthorizonscc")) {
       //Monday, Thursday, Friday and Sunday during the week 1-1-2013 to 4-15-2013 from 8:00-10:00 AM
       
       // Desert Horizons CC - Check guests to make sure they are on a 1 to 1 ratio with members in this tee time. Applies Su/M/Th/F 1/1/13-4/15/13 8am-10am.
       //if (slotParms.date >= 20130101 && slotParms.date <= 20130415 && slotParms.time >= 800 && slotParms.time <= 1000 
       if (shortDate >= 101 && shortDate <= 415 && slotParms.time >= 800 && slotParms.time <= 1000 
               && (slotParms.day.equals("Sunday") || slotParms.day.equals("Monday") || slotParms.day.equals("Thursday") || slotParms.day.equals("Friday"))) {

           error = checkDesertHorizonsCCGuestsPerMember(slotParms, con);
           
           if (error) {
               returnMsg = "<H3>Guest Limit Exceeded</H3>" +
                       "<BR>Sorry, but " + slotParms.player + " is only allowed to have 1 guest in this tee time." +
                       "<BR>Only 1 guest per member is permitted Sunday, Monday, Thursday, and Friday between 8:00AM and 10:00AM." +
                       "<BR><BR>Please remove some guests or return to the tee sheet.";

           }
       }
   } else if (slotParms.club.equals("discoverybay")) {
       
       error = checkDiscoveryBayGuests(slotParms, con);

       if (error == true) {         // if we hit an error

           returnMsg = "<H3>Guest Type Not Allowed</H3>" +
                   "<BR>Sorry, but " + slotParms.player + " is not allowed to specify the Junior guest type." +
                   "<BR>Single Golf members are not permitted to bring a Junior guest at any time." +
                   "<BR><BR>Please select a different guest type for this member or return to the tee sheet.";
       }
   } else if (slotParms.club.equals("elgincc")) {
       
       /** Normal days in advance are as follows:
        * Sunday    = 4 days
        * Monday    = 5 days
        * Tuesday   = 7 days
        * Wednesday = 7 days
        * Thursday  = 7 days
        * Friday    = 7 days
        * Saturday  = 3 days
        */
          
       int err = 0;
       int maxGuests = 7;
       int index = slotParms.ind;
       
       if (index > 365) {
           index = 0;
       }
              
       err = checkElginAdvanceGuests(slotParms, maxGuests, con);
       
       switch (err) {
           case 0:
               if (((slotParms.day.equals("Saturday") && index > 3)    // 3 days in advance on Saturdays
                    || (slotParms.day.equals("Sunday") && index > 4)    // 4 days on Sun
                    || (slotParms.day.equals("Monday") && index > 5)    // 5 days on Mon
                    || (index > 7))) {
                   slotParms.custom_int = index;
               } else {
                   slotParms.custom_int = 0;
               }
               break;
           
           case 1:
               returnMsg = "<H3>Member Restricted</H3>" +
                       "<BR>Sorry, but " + slotParms.player + " is not allowed to bring guests in an advance tee time." +
                       "<BR>'Social Member', 'Certified Junior', and 'Non Certified Junior' members are not permitted to bring a guest in tee times outside of their normal days in advance." +
                       "<BR><BR>Please remove the guest for this member or return to the tee sheet.";
               break;
               /*
           case 2:
               returnMsg = "<H3>Guest Quota Exceeded</H3>" +
                       "<BR>Sorry, but this tee time would exceed the maximum number of allowed advance guests for " + slotParms.player + "." +
                       "<BR>Members may only have 7 guests booked as part of advance tee times at any given time." +
                       "<BR><BR>Please remove the guest(s) for this member or return to the tee sheet.";
               break;
                */
               
           case 3:
               returnMsg = "<H3>Guest Not Allowed</H3>" +
                       "<BR>Sorry, but " + slotParms.player + " is not allowed to bring guests at this time." +
                       "<BR>'Certified Junior' members may not bring guests prior to 1:00 PM on any day." +
                       "<BR><BR>Please remove the guest(s) for this member or return to the tee sheet.";
               break;
               
           default:
               break;
       }
   }

   return(returnMsg);

 }    // end of checkCustomsGst
 
 
 
 
//**************************************************************************
//
//  checkRestLift
//
//      Check if a restriction should be skipped.  This method is for lifting 
//      restrictions x hours or days in advance.
//
//  called by:  verifySlot.checkMemRests
//              getRests.getAll 
//
//**************************************************************************
//
 public static boolean checkRestLift (long date, String day_name, String rest_name, String club, int activity_id, Connection con) {

    boolean skipit = false;
        
    //
    //  Custom for Interlachen - lift restriction if one of a list and within 48 hours
    //
    if (club.equals("interlachen") && activity_id == 0) {

        skipit = checkInterlachenRest(date, rest_name, con); 
    }
         
    //
    //  Custom for Brooklawn - lift Sat & Sun restrictions at 9:00 AM on Thurs
    //
    if (club.equals("brooklawn") && activity_id == 0) {

        skipit = checkBrooklawnRest(date, day_name, rest_name, con); 
    }

    //
    //  Custom for Saticoy CC - lift Tuesday restriction at 8:00 AM ET on Friday (case 2292)
    //
    if (club.equals("saticoycountryclub") && activity_id == 0) {

        skipit = checkSaticoyRest(date, day_name, rest_name, con); 
    }
    
    if (club.equals("brookridgegf") && activity_id == 0) {
        
        skipit = checkBrookridgeRest(date, day_name, rest_name, con);
    }

    if (club.equals("plantationcc")) {
        
        skipit = checkPlantationCCRest(date, rest_name, con);
    }
    
    //
    //  Other customs can follow here ..........
    //
    
 
    return skipit;

 }   // end of checkRestLift

 
 
 
 
    //**************************************************************************
    //
    //  checkInterlachenRest
    //
    //      Check if a restriction should be skipped.  If name is in the selected 
    //      list and it is currently within 2 days of the tee time, then skip it.
    //
    //  called by:  checkMemRests 
    //              getRests.getAll 
    //
    //**************************************************************************
    //
    public static boolean checkInterlachenRest (long date, String rest_name, Connection con) {

        boolean skipit = false;
        
        String[] restsA = {"Mens Preferred Thursday",
                           "Mens Preferred Time Wednesday",
                           "Womens 18 Hole League",
                           "Womens 18 Hole League 1",
                           "Womens 18 Hole League 2",
                           "Womens 9 Hole League",
                           "Womens 9 Hole League 1",
                           "Womens 9 Hole League 2",
                           "Womens 9 Hole League 3",
                           "Womens Preferred 1",
                           "Womens Preferred 4",
                           "Womens Preferred Fall 1",
                           "Womens Preferred Fall 2",
                           "Womens Preferred Thursday",
                           "Womens Preferred Thursday 2",
                           "Womens Preferred Tuesday",
                           "Womens Preferred Tuesday 1",
                           "Mens Wednesday Night League",
                           "Willie Kidd Guests",
                           "Demov4 Test For Lift"};         // This last one is for testing on demov4 site !!!!!
        
        int count = restsA.length;  

        Calendar cal = new GregorianCalendar();        // get todays date
        cal.add(Calendar.DATE, 2);                     // roll ahead 2 days
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        long date2 = (year * 10000) + (month * 100) + day;  
        int curtime = (hr * 100) + min;

        //
        //  See if we should skip this restriction
        //
        if (date < date2 || (date == date2 && curtime > 659)) {  // if within 48 hours 

            for (int i=0; i<count; i++) {    // loop through the restriction names listed above
            
                if (rest_name.equals(restsA[i])) {
                    
                    skipit = true;       // name found - skip this restriction
                    break;               // done
                }         
            }
        }
    
        return skipit;

    }   // end of checkInterlachenRest

 
 
    //**************************************************************************
    //
    //  checkBrooklawnRest
    //
    //      Check if a restriction should be skipped.  The Sat & Sun morning tee
    //      times open a 9:00 AM ET on Thurs.
    //
    //  called by:  checkMemRests 
    //              getRests.getAll 
    //
    //**************************************************************************
    //
    public static boolean checkBrooklawnRest (long date, String day_name, String rest_name, Connection con) {

        boolean skipit = false;
        
        String restriction = "Times open at 9am Thurs";           // restrictions to skip
        String restriction2 = "MDay Times open at 9am Thurs";     // Memorial Day
        String restriction3 = "July 4 Times open at 9am Thurs";   // July 4th    
        
        int days = 0;
        
        if (rest_name.equals(restriction) && (day_name.equalsIgnoreCase( "saturday" ) || 
            day_name.equalsIgnoreCase( "sunday" ))) {                                    // should we check this?
            
            //  skip this restriction if date is a Sat or Sun and today is the previous Thurs, Fri, or Sat or Sun
            
            if (day_name.equalsIgnoreCase( "saturday" )) days = 2;  // days in advance to lift restriction
            
            if (day_name.equalsIgnoreCase( "sunday" )) days = 3;
            
        } else if (rest_name.equals(restriction2) && date == Hdate1) {    // Tee time on Memorial Day?
            
            //  skip this restriction if date is Memorial Day and today is the previous Thurs, Fri, Sat, Sun or Mem Day
            
            days = 4;  // days in advance to lift restriction
            
            
        } else if (rest_name.equals(restriction3) && date == Hdate2b) {    // Tee time on 4th of July?
            
            //  skip this restriction if date is 4th of July and today is during the previous week
            
            days = 7;  // days in advance to lift restriction
        }
         
        //
        //   Check today's date to see if the restriction should be lifted
        //
        if (days > 0) {   // if a restriction found above    
        
            Calendar cal = new GregorianCalendar();           // get todays date
            cal.add(Calendar.DATE, days);                     // roll ahead 2 or 3 days
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hr = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);

            long date2 = (year * 10000) + (month * 100) + day;  
            int curtime = (hr * 100) + min;

            //
            //  See if we should skip this restriction
            //
            if (date < date2 || (date == date2 && curtime > 759)) {     // if within 2 or 3 days (or exactly and at least 9:00 ET) 

                skipit = true;       // name found - skip this restriction
            } 
        }
    
        return skipit;

    }   // end of checkBrooklawnRest

 
 

    //**************************************************************************
    //
    //  checkSaticoyRest
    //
    //      Check if a restriction should be lifted.  The Tues morning tee
    //      times open a 8:00 AM ET on Fri.
    //
    //  called by:  checkRestLift above
    //
    //**************************************************************************
    //
    public static boolean checkSaticoyRest (long date, String day_name, String rest_name, Connection con) {

        boolean skipit = false;
        
        String restriction = "Tuesday Sweeps";           // restrictions to skip
        
        int days = 0;
        
        if (rest_name.equals(restriction) && day_name.equalsIgnoreCase( "tuesday" )) {   // should we check this?
            
            //  lift this restriction on the previous Fri
            
            days = 4;  // days in advance to lift restriction
        }
         
        //
        //   Check today's date to see if the restriction should be lifted
        //
        if (days > 0) {   // if a restriction found above    
        
            Calendar cal = new GregorianCalendar();           // get todays date
            cal.add(Calendar.DATE, days);                     // roll ahead
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hr = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);

            long date2 = (year * 10000) + (month * 100) + day;  
            int curtime = (hr * 100) + min;

            //
            //  See if we should skip this restriction
            //
            if (date < date2 || (date == date2 && curtime > 659)) {     // if within 4 days (or exactly and at least 8:00 ET) 

                skipit = true;       // name found - skip this restriction
            } 
        }
    
        return skipit;

    }   // end of checkSaticoyRest
 
 
    //**************************************************************************
    //
    //  checkBrookridgeRest
    //
    //      Lift the "Day of Tee Times" restriction on the day of at 7:00 am CT
    //
    //  called by:  checkRestLift above
    //
    //**************************************************************************
    //
    public static boolean checkBrookridgeRest (long date, String day_name, String rest_name, Connection con) {

        boolean skipit = false;
        
        String restriction = "Day of Tee Times";           // restrictions to skip
        
        if (rest_name.equals(restriction)) {   // should we check this?    
            
            Calendar cal = new GregorianCalendar();           // get todays date
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hr = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);

            long date2 = (year * 10000) + (month * 100) + day;  
            int curtime = (hr * 100) + min;
            int shortDate = (month * 100) + day;
         
            /*
             * See if we should skip this restriction
             * 
             * 2014 dates/times:
             * 
             * May 1 - August 31 on all Weekends open up at 6:00am.
             * November 1- February 28 on all Weekdays open up at 8:00am.
             * All other days and months that are not mentioned open up at 7:00am
             * 
             */
            if (date == date2) {
                
                if (shortDate >= 501 && shortDate <= 831) {
                    
                    if (((day_name.equals("Saturday") || day_name.equals("Sunday")) && curtime >= 600) || curtime >= 700) {
                        skipit = true;
                    }
                    
                } else if (shortDate >= 1101 || shortDate <= 228) {
                    
                    if (((day_name.equals("Saturday") || day_name.equals("Sunday")) && curtime >= 700) || curtime >= 800) {
                        skipit = true;
                    }
                    
                } else {
                    
                    if (curtime >= 700) {
                        skipit = true;
                    }
                }
            }
        }
    
        return skipit;

    }   // end of checkBrookridgeRest

    //**************************************************************************
    //
    //  checkMacGregorDownsRest
    //
    //      Check if a guest restriction should be lifted.  The Tues morning tee
    //      times open a 8:00 AM ET on Fri.
    //
    //  called by:  checkRestLift above
    //
    //**************************************************************************
    //
    public static boolean checkMacGregorDownsRest (long date, String day_name, String rest_name, Connection con) {

        boolean skipit = false;
        
        String restriction = "Friday Member Only 12 to 150PM";           // restrictions to skip
        
        int days = 0;
        
        if (rest_name.equals(restriction) && day_name.equalsIgnoreCase( "friday" )) {   // should we check this?
            
            //  lift this restriction on the previous Fri
            
            days = 2;  // days in advance to lift restriction
        }
         
        //
        //   Check today's date to see if the restriction should be lifted
        //
        if (days > 0) {   // if a restriction found above    
        
            Calendar cal = new GregorianCalendar();           // get todays date
            cal.add(Calendar.DATE, days);                     // roll ahead
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hr = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);

            long date2 = (year * 10000) + (month * 100) + day;  
            int curtime = (hr * 100) + min;

            //
            //  See if we should skip this restriction
            //
            if (date < date2 || (date == date2 && curtime > 659)) {     // if within 4 days (or exactly and at least 8:00 ET) 

                skipit = true;       // name found - skip this restriction
            } 
        }
    
        return skipit;

    }   // end of checkMacGregorDownsRest

    //**************************************************************************
    //
    //  checkPlantationCCRest
    //
    //      Check if a member type restriction should be lifted.  All 'Short Notice Tee Times' restrictions
    //      to be lifted 1 day in advance at 7am MT
    //
    //  called by:  checkRestLift above
    //
    //**************************************************************************
    //
    public static boolean checkPlantationCCRest (long date, String rest_name, Connection con) {

        boolean skipit = false;
        
        String restriction = "Short Notice Tee Times";           // restrictions to skip
        
        int days = 0;
        
        if (rest_name.startsWith(restriction)) {   // should we check this?
            
            days = 1;  // days in advance to lift restriction
        }
         
        //
        //   Check today's date to see if the restriction should be lifted
        //
        if (days > 0) {   // if a restriction found above    
        
            Calendar cal = new GregorianCalendar();           // get todays date
            cal.add(Calendar.DATE, days);                     // roll ahead
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hr = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);

            long date2 = (year * 10000) + (month * 100) + day;  
            int curtime = (hr * 100) + min;

            //
            //  See if we should skip this restriction
            //
            if (date < date2 || (date == date2 && curtime >= 800)) {     // if within 1 days (or exactly and at least 7:00 MT) 

                skipit = true;       // name found - skip this restriction
            } 
        }
    
        return skipit;

    }   // end of checkPlantationCCRest

 
 



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
 //  Minikahda CC - Check for guest times per hour.
 //
 //    Called by:  Member_slot (new requests only)
 //
 //************************************************************************
 **/

 public static String checkMinikahdaGuests(long date, int shortDate, parmSlot slotParms, Connection con) {

   String returnMsg = "";
      
   boolean is3GuestGroup = false;
   boolean is4GuestGroup = false;
   boolean count4GuestGroups = false;

   int cur_time = Utilities.getTime(con);
   int hour = slotParms.time / 100;            // isolate the hour

   // Determine if this is a 3 or 4 guest time or not
   if (slotParms.guests == 3 && slotParms.members == 1) {
      is3GuestGroup = true;
   } else if (slotParms.guests == 4) {
      is4GuestGroup = true;
   }

   // First, if a 4-guest group, determine if it's allowed, or if it should be treated as a 3-guest group at this time
   if (is4GuestGroup) {

      is3GuestGroup = true;            // Treat this as a 3-guest group (proshop only as members cannot book unaccompanied groups)
      count4GuestGroups = true;        // Make sure 4 Guest Groups get included in the hourly count

               
     /*   2012 logic follows -

      if (slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Thursday") || slotParms.day.equals("Friday")) {

            if (slotParms.time >= 1200 && slotParms.time <= 1400) {

               // error - not allowed Tues-Fri between 12-2pm

               returnMsg = "<H3>Invalid Guest Time Request</H3>" +
                        "<BR>Sorry, outside foursomes are not allowed between 12:00 PM and 2:00 PM, Tuesday-Friday." +
                        "<BR><BR>Please select a different time of the day.";

            } else if (slotParms.time >= 1100 && slotParms.time <= 1500) {

               is3GuestGroup = true;        // Treat this as a 3-guest group
               count4GuestGroups = true;        // Make sure 4 Guest Groups get included in the hourly count
            }

      } else if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || date == Hdate1 || date == Hdate2 || date == Hdate3) {

            if (slotParms.time < 1000) {

               // error - not allowed Sat/Sun/Holiday before 10am

               returnMsg = "<H3>Invalid Guest Time Request</H3>" +
                        "<BR>Sorry, outside foursomes are not allowed before 10:00 AM on Saturday, Sunday, or Holidays." +
                        "<BR><BR>Please select a different time of the day.";
            } else {
               is3GuestGroup = true;        // Treat this as a 3-guest group
               count4GuestGroups = true;        // Make sure 4 Guest Groups get included in the hourly count
            }
      }
      * 
      */
   }
      
      
   if (returnMsg.equals("")) {      // if no other error detected

      // 
      //  If NOT a guest group (1 member & 3 guests) then check if beyond days in advance for normal times
      //
      if (is3GuestGroup == false) {

         // If not a 3-guest group, see if we're outside of normal booking
         if (slotParms.ind > 3 || (slotParms.ind == 3 && cur_time < 800)) {

            // error - non-guest times not allowed this far in advance
            returnMsg = "<H3>Invalid Guest Time Request</H3>" +
                     "<BR>Sorry, only guest times are accepted at this time." +
                     "<BR><BR>Your request must contain 1 member and 3 guests.";
         }

      } else {     // Guest Time (1 member & 3 guests)
      
         // Determine if we're in season
         if (shortDate >= 524 && date <= Hdate3) {   // In Season = the Sat before Memorial Day until Labor Day

            // Special processing applies between Memorial Day and Labor Day (2013 rules)
            //
            //     Sat, Sun & Holidays - No groups prior to 10:00, 2 groups per hour after 10:00
            //
            //     Tues thru Fri - 1 group per hour Noon to 2:00, 2 groups per hour rest of day
            //
            if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || date == Hdate1 || date == Hdate2 || date == Hdate3) {

                  if (slotParms.time < 1000) {      // Sat, Sun, or Holiday and before 10:00 AM

                     // error - no 3-guest groups before 10am Sat/Sun/Holiday Mem-Labor
                     returnMsg = "<H3>Invalid Guest Time Request</H3>" +
                                 "<BR>Sorry, guest times are not allowed before 10:00 AM on Saturday, Sunday, or Holidays." +
                                 "<BR><BR>Please select a different time of the day.";

                  } else if (checkMiniGuestTimes(slotParms, hour, count4GuestGroups, con) >= 2) {   // 10:00 or later (2 groups per hour)

                     // error - only 2 group per hour allowed in this period
                     returnMsg = "<H3>Invalid Guest Time Request</H3>" +
                           "<BR>Sorry, there are already 2 guest times scheduled this hour." +
                           "<BR><BR>Please select a different time of the day.";
                  }

            } else if (slotParms.time >= 1200 && slotParms.time <= 1400) {       // Tues thru Fri

               if (checkMiniGuestTimes(slotParms, hour, count4GuestGroups, con) >= 1) {    // 1 group per hour between Noon and 2:00

                     // error - only 1 group allowed in this period
                     returnMsg = "<H3>Invalid Guest Time Request</H3>" +
                           "<BR>Sorry, there is already another guest time scheduled for this period. Only one guest time is allowed per hour between Noon and 2:00 PM." +
                           "<BR><BR>Please select a different time of the day.";
               }

            } else {     // Tues thru Fri and NOT Noon to 2:00

               if (checkMiniGuestTimes(slotParms, hour, count4GuestGroups, con) >= 2) {    // 2 groups per hour

                     // error - only 2 group allowed in this period
                     returnMsg = "<H3>Invalid Guest Time Request</H3>" +
                           "<BR>Sorry, there are already 2 guest times scheduled this hour." +
                           "<BR><BR>Please select a different time of the day.";
               }
            }


         } else if (shortDate < 501 || shortDate > 930) {      // if out of season (opener to April 30 and Oct 1st to close)

            //  No restrictions

            returnMsg = "";


         } else {        // Shoulder Season - May 1st to the Fri before Memorial Day and from Tues after Labor Day to Sept 30

            //  2 guest groups per hour (1 member & 3 guests)

            if (checkMiniGuestTimes(slotParms, hour, count4GuestGroups, con) >= 2) {    // 2 groups per hour

                  // error - only 2 group allowed in this period
                  returnMsg = "<H3>Invalid Guest Time Request</H3>" +
                        "<BR>Sorry, there are already 2 guest times scheduled this hour." +
                        "<BR><BR>Please select a different time of the day.";
            }

         }   // end of season checks

      }         // end of IF guest time
       
   }      // end of IF error
      
   return(returnMsg);

 } // end of checkMinikahdaGuests


    
    
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


      // Determine whether time should be restricted based on guest time limit per hour.
      if (date >= Hdate1 && date <= Hdate3) {

          if (slotParms.day.equals("Thursday")) {

              if (hour >= 12 && hour <= 14) {

                  error = true;

              } else {

                  if (count > 1) {

                      error = true;
                  }
              }

          } else {

              if (hour < 8) {

                  if (count > 0) {

                      error = true;
                  }

              } else if (hour < 12 || hour > 14) {

                  if (count > 1) {

                      error = true;
                  }

              } else {      // 12pm - 2pm

                  if (count > 0) {

                      error = true;
                  }
              }
          }

      } else {

          if (hour < 8) {

              if (count > 0) {

                  error = true;
              }

          } else if (count > 1) {

              error = true;
          }
      }

      //
      //  If 2 guest times already scheduled, then set error (3 guests on Tues/Wed/Thurs except between 12-2pm)
      //
      /*
      if ((slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday")) && (hour < 12 || hour > 14)) {

         if (count > 1) {      // only 3 times allowed

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
      */

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


 public static int checkMiniGuestTimes(parmSlot slotParms, int hour, boolean count4GuestGroups, Connection con) {


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

   int count = 0;
   int memcount = 0;

   long date = slotParms.date;



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

            if (memcount == 1 && (!count4GuestGroups || memcount == 0)) {         // if 1 member & 3 guests or 4 guests and 4 guest groups are being counted

               count++;                 // count # of guest times
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

   return(count);
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
      if (shortDate >= 621 && shortDate <= 901) {        // 6/26 thru Labor Day

         //
         //  In Season
         //
         if (slotParms.day.equals("Friday") || slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") ||
             slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3) {                      // if w/e or holiday

             if (slotParms.time <= 1329) {
                 
                 stime = 900;        // max of 2 guest times between 9:00am and 1:30pm.
                 etime = 1329;
                 quota = 2;        // max of 2 guest times
                 
             } else {
                 
                 stime = 1330;        // max of 1 guest times between 1:30pm and end of day.
                 etime = 2000;
                 quota = 1;        // max of 1 guest time
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

 public static boolean checkSonnenalp3someTimes(long date, int time, String day_name) {

     boolean status = false;

     long year = date / 10000;
     long month = (date - (year * 10000)) / 100;
     long day = date - ((year * 10000) + (month * 100));
     long mmdd = (month * 100) + day;                        // create mmdd value

     //   3-some ONLY times from 8:00 AM to 10:10 AM on Thursdays from 5/24 to 8/16
     if (mmdd >= 524 && mmdd <= 816 && date != 20140722 && day_name.equals( "Tuesday" ) && time >= 800 && time <= 1010) {

         status = true;                        // 3-some only time
     }

     return status;
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
   //   2-some ONLY times from 7:40AM to 7:59AM on Mon, Tues, Wed, Fri from May 28 thru Aug 30
   //
   if ( (mmdd >= 528 && mmdd <= 830) &&
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
   //   2-some ONLY times from 7:00 AM to 7:24 AM on Weekdends and holidays from May 26 thru Sept 1
   //
   if (((mmdd >= 526 && mmdd <= 901) && (day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ))) ||
           date == Hdate1 || date == Hdate2 || date == Hdate3) {

      if (time >= 700 && time <= 724) {         // if tee time is between 7:00 and 7:24 AM

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
   if (mmdd >= 1019 || mmdd <= 515) {   // if in season

      //
      //   Check each day for 2-some ONLY times
      //
      if (day_name.equals( "Friday" )) {

         if (time >= 833 && time <= 1033) {         // if tee time is between 8:33 and 10:03 AM

            //
            //   Alternate Front & Back tees each week
            //
            if (fb == 0 && (mmdd == 1021 || mmdd == 1104 || mmdd == 1118 || mmdd == 1202 || mmdd == 1216 || mmdd == 1230 ||
                mmdd == 113 || mmdd == 127 || mmdd == 210 || mmdd == 224 || mmdd == 309 || mmdd == 323 || mmdd == 406 ||
                mmdd == 420)) {

               status = true;                        // 2-some only time

            } else if (fb == 1 && (mmdd == 1028 || mmdd == 1111 || mmdd == 1125 || mmdd == 1209 || mmdd == 1223 || mmdd == 106 ||
                mmdd == 120 || mmdd == 203 || mmdd == 217 || mmdd == 302 || mmdd == 316 || mmdd == 330 || mmdd == 413 ||
                mmdd == 427)) {

               status = true;                        // 2-some only time
            }
         }

      } else if (day_name.equals( "Sunday" )) {

         if (time >= 800 && time <= 1030 && fb == 0) {   // if tee time is between 8:00 and 10:30 AM on the Front

            status = true;                        // 2-some only time
         }

      }/* else if (day_name.equals( "Tuesday" )) {

         if (time >= 800 && time <= 842) {   // if tee time is between 8:00 and 8:42 AM

            status = true;                        // 2-some only time
         }

      }*/ else if (day_name.equals( "Wednesday" )) {

         if (time >= 800 && time <= 830) {   // if tee time is between 8:00 and 8:30 AM

            status = true;                        // 2-some only time
         }

      }/* else if (day_name.equals( "Thursday" )) {

         if (time >= 800 && time <= 854) {   // if tee time is between 8:00 and 8:54 AM

            status = true;                        // 2-some only time
         }

      }*/

   } else if (mmdd >= 522 && mmdd <= 924) {       // end of IF in season

       if (day_name.equals( "Sunday" )) {

         if (time >= 700 && time <= 900 && fb == 0) {   // if tee time is between 7:00 and 9:00 AM on the Front

            status = true;                        // 2-some only time
         }
      }
   }

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
 
 public static boolean checkOahuSundayLimit(parmSlot slotParms, Connection con) {
     
     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;
     
     boolean error = false;
     
     int teeTimeCount = 0;
     int roundCount = 0;
     int slots = 0;

     String [] userA = new String [5];        // array to hold the usernames
     String [] playerA = new String [5];      // array to hold the player's names
     String [] mNumA = new String [5];       // array to hold the players' membership types
     
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
     
     mNumA[0] = slotParms.mNum1;
     mNumA[1] = slotParms.mNum2;
     mNumA[2] = slotParms.mNum3;
     mNumA[3] = slotParms.mNum4;
     mNumA[4] = slotParms.mNum5;
     
     slots = slotParms.slots;
     
     if (slots == 0) slots = 1;
     
     mainloop:
     for (int i=0; i<5; i++) {
         
         teeTimeCount = 0;
         roundCount = 0;
         
         if (playerA[i].equals("")) {
             break mainloop;
         }
         
         // Count member rounds in current time
         for (int j = 0; j < 5; j++) {
             if (j == i || mNumA[i] == mNumA[j]) {
                 roundCount++;
             }
         }
         
         try {
             // Look up members with the same member number as the current player.
             pstmt = con.prepareStatement("SELECT username FROM member2b WHERE memNum = (SELECT memNum FROM member2b WHERE username = ?)");
             pstmt.clearParameters();
             pstmt.setString(1, userA[i]);
             
             rs = pstmt.executeQuery();
             
             while (rs.next()) {
                 
                 try {
                     
                     pstmt2 = con.prepareStatement("SELECT * FROM teecurr2 WHERE date = ? AND orig_by = ? AND teecurr_id <> ?");
                     pstmt2.clearParameters();
                     pstmt2.setLong(1, slotParms.date);
                     pstmt2.setString(2, rs.getString(1));
                     pstmt2.setInt(3, slotParms.teecurr_id);
                     
                     rs2 = pstmt2.executeQuery();
                     
                     while (rs2.next()) {
                         teeTimeCount++;
                     }
                     
                 } catch (Exception exc) {
                     Utilities.logError("verifyCustom.checkOahuSundayLimit - oahucc - Error looking up member number times - ERR: " + exc.toString());
                 } finally {

                     try { rs2.close(); }
                     catch (Exception ignore) { }

                     try { pstmt2.close(); }
                     catch (Exception ignore) { }
                 }
                 
                 // If their number of existing tee times plus the number of tee times they're currently booking exceeds 2, reject them.
                 if (teeTimeCount + slots > 2) {
                     error = true;
                     slotParms.player = playerA[i];
                     break mainloop;
                 }
                 
                 try {
                     
                     pstmt2 = con.prepareStatement("SELECT * FROM teecurr2 WHERE date = ? AND (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND teecurr_id <> ?");
                     pstmt2.clearParameters();
                     pstmt2.setLong(1, slotParms.date);
                     pstmt2.setString(2, mNumA[i]);
                     pstmt2.setString(3, mNumA[i]);
                     pstmt2.setString(4, mNumA[i]);
                     pstmt2.setString(5, mNumA[i]);
                     pstmt2.setString(6, mNumA[i]);
                     pstmt2.setInt(7, slotParms.teecurr_id);
                     
                     rs2 = pstmt2.executeQuery();
                     
                     while (rs2.next()) {
                         if (rs2.getString("mNum1").equalsIgnoreCase(mNumA[i])) {
                             roundCount++;
                         }
                         if (rs2.getString("mNum2").equalsIgnoreCase(mNumA[i])) {
                             roundCount++;
                         }
                         if (rs2.getString("mNum3").equalsIgnoreCase(mNumA[i])) {
                             roundCount++;
                         }
                         if (rs2.getString("mNum4").equalsIgnoreCase(mNumA[i])) {
                             roundCount++;
                         }
                         if (rs2.getString("mNum5").equalsIgnoreCase(mNumA[i])) {
                             roundCount++;
                         }
                     }
                     
                 } catch (Exception exc) {
                     Utilities.logError("verifyCustom.checkOahuSundayLimit - oahucc - Error looking up member number rounds - ERR: " + exc.toString());
                 } finally {

                     try { rs2.close(); }
                     catch (Exception ignore) { }

                     try { pstmt2.close(); }
                     catch (Exception ignore) { }
                 }
                 
                 if (roundCount > 2) {
                     error = true;
                     slotParms.player = playerA[i];
                     break mainloop;
                 }
             }

         } catch (Exception exc) {
                 Utilities.logError("verifyCustom.checkOahuSundayLimit - oahucc - Error looking up family members - ERR: " + exc.toString());
         } finally {

             try { rs.close(); }
             catch (Exception ignore) { }

             try { pstmt.close(); }
             catch (Exception ignore) { }
         }
         
         if (error) {
             break;
         }
     }
     
     return error;
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

      } else if (day_name.equals( "Thursday" ) && course.equals("Lake Vista")) {

         if (time == 1104 || time == 1200 || time == 1256) {

            status = true;                        // primary only time
         }

      } else if (day_name.equals( "Friday" ) && course.equals("Valley Vista")) {

         if (time == 656 || time == 752 || time == 800 || time == 856 || time == 1000 || time == 1056 || time == 1200 ||
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
   if (mmdd >= 401 && mmdd <= 831 && (day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday") || day_name.equals("Friday"))) {

      if (time >= 730 && time <= 800) {         // if tee time is between 7:30 and 8:00 AM OR 4:30-5PM

         status = true;                        // 2-some only time
      }
   }

   if (mmdd >= 901 && mmdd <= 930 && (day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday") || day_name.equals("Friday"))) {

      if (time >= 730 && time <= 800) {         // if tee time is between 7:30 and 8:00 AM

         status = true;                        // 2-some only time
      }
   }

   if (mmdd >= 1001 && mmdd <= 1031 && (day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday") || day_name.equals("Friday"))) {

      if (time >= 800 && time <= 830) {         // if tee time is between 8:00 and 8:30 AM

         status = true;                        // 2-some only time
      }
   }

   if (mmdd >= 401 && mmdd <= 1031 && (day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday") || day_name.equals("Friday"))) {

      if (time >= 1600) {         // if tee time is on or after 4:00 PM

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
            if (usergA[0].equals( userA[i] ) && playerA[0].startsWith("Guest")) {

               countm++;           // count # of guests
               counts++;
            }
            if (usergA[1].equals( userA[i] ) && playerA[1].startsWith("Guest")) {

               countm++;           // count # of guests
               counts++;
            }
            if (usergA[2].equals( userA[i] ) && playerA[2].startsWith("Guest")) {

               countm++;           // count # of guests
               counts++;
            }
            if (usergA[3].equals( userA[i] ) && playerA[3].startsWith("Guest")) {

               countm++;           // count # of guests
               counts++;
            }
            if (usergA[4].equals( userA[i] ) && playerA[4].startsWith("Guest")) {

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
                        "WHERE date < ? AND date > ? AND event = '' AND (courseName = 'Main Course' OR courseName = 'Championship Course') AND " +
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

                        if (userg1.equals( user ) && player1.startsWith("Guest")) {

                           counts++;     // bump # of guests
                        }
                        if (userg2.equals( user ) && player2.startsWith("Guest")) {

                           counts++;     // bump # of guests
                        }
                        if (userg3.equals( user ) && player3.startsWith("Guest")) {

                           counts++;     // bump # of guests
                        }
                        if (userg4.equals( user ) && player4.startsWith("Guest")) {

                           counts++;     // bump # of guests
                        }
                        if (userg5.equals( user ) && player5.startsWith("Guest")) {

                           counts++;     // bump # of guests
                        }
                     }      // end of WHILE

                     pstmt.close();

                     pstmt = con.prepareStatement (
                        "SELECT date, time, player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                        "FROM teecurr2 " +
                        "WHERE date < ? AND date > ? AND event = '' AND (courseName = 'Main Course' OR courseName = 'Championship Course') AND " +
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

                           if (userg1.equals( user ) && player1.startsWith("Guest")) {

                              counts++;     // bump # of guests
                           }
                           if (userg2.equals( user ) && player2.startsWith("Guest")) {

                              counts++;     // bump # of guests
                           }
                           if (userg3.equals( user ) && player3.startsWith("Guest")) {

                              counts++;     // bump # of guests
                           }
                           if (userg4.equals( user ) && player4.startsWith("Guest")) {

                              counts++;     // bump # of guests
                           }
                           if (userg5.equals( user ) && player5.startsWith("Guest")) {

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
                        "WHERE mm = ? AND yy = ? AND event = '' AND (courseName = 'Main Course' OR courseName = 'Championship Course') AND " +
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

                        if (userg1.equals( user ) && player1.startsWith("Guest")) {

                           countm++;     // bump # of guests
                        }
                        if (userg2.equals( user ) && player2.startsWith("Guest")) {

                           countm++;     // bump # of guests
                        }
                        if (userg3.equals( user ) && player3.startsWith("Guest")) {

                           countm++;     // bump # of guests
                        }
                        if (userg4.equals( user ) && player4.startsWith("Guest")) {

                           countm++;     // bump # of guests
                        }
                        if (userg5.equals( user ) && player5.startsWith("Guest")) {

                           countm++;     // bump # of guests
                        }
                     }      // end of WHILE

                     pstmt.close();

                     pstmt = con.prepareStatement (
                        "SELECT date, time, player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                        "FROM teecurr2 " +
                        "WHERE mm = ? AND yy = ? AND event = '' AND (courseName = 'Main Course' OR courseName = 'Championship Course') AND " +
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

                           if (userg1.equals( user ) && player1.startsWith("Guest")) {

                              countm++;     // bump # of guests
                           }
                           if (userg2.equals( user ) && player2.startsWith("Guest")) {

                              countm++;     // bump # of guests
                           }
                           if (userg3.equals( user ) && player3.startsWith("Guest")) {

                              countm++;     // bump # of guests
                           }
                           if (userg4.equals( user ) && player4.startsWith("Guest")) {

                              countm++;     // bump # of guests
                           }
                           if (userg5.equals( user ) && player5.startsWith("Guest")) {

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
   //    Low Season  = Open - 6/14 and 9/16 - Close
   //    High Season = 6/15 - 9/15
   //
   //    Morning = open up to 1:20 (inclusive)
   //    Mid Day = 1:30 - 3:20 (inclusive)
   //    Twilight = after 3:29
   //
   if (gtype.equals( "Hotel" ) || gtype.equals( "Unescorted Guest" )) {   // if guest type = Hotel or Unescorted

      if (sdate > 614 && sdate < 916) {       // if High Season

         if (time > twilight) {             // if twilight

            cost = "75.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "37.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "140.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "67.50";
               }

            } else {         // Mid Day (1:30 - 3:20)

               cost = "110.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "55.00";
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

               cost = "95.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "47.50";
               }

            } else {         // Mid Day (1:30 - 3:20)

               cost = "70.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "35.00";
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

            cost = "55.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "27.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "110.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "55.00";
               }

            } else {         // Mid Day (1:30 - 3:20)

               cost = "80.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "40.00";
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

               cost = "80.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "40.00";
               }

            } else {         // Mid Day (1:30 - 3:20)

               cost = "60.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "30.00";
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

            cost = "85.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "42.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "170.00";          // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "145.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "67.50";
               }
            }
         }

      } else {    // Low Season

         if (time > twilight) {             // if twilight

            cost = "60.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "30.00";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "110.00";        // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "85.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "42.50";
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

            cost = "85.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "42.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "170.00";          // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "120.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "60.00";
               }
            }
         }

      } else {    // Low Season

         if (time > twilight) {             // if twilight

            cost = "55.00";

            if (p9 == 1) {             // if 9 hole round

               cost = "27.50";
            }

         } else {        // NOT twilight

            if (time < morning) {             // if normal time (before 1:30)

               cost = "110.00";        // no 9 hole fee

            } else {         // Mid Day (1:30 - 3:20)

               cost = "75.00";

               if (p9 == 1) {             // if 9 hole round

                  cost = "37.50";
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
        int max = 6;                                 // max of rounds

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
                    if (count == 5) {

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
 //     in this tee time and it is Interlachen.
 //
 //     Restrictions:
 //
 //        Only members with a sub-type of "Member Guest Pass" are allowed
 //        to have "Guest-Centennial" guests.
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
           "WHERE teecurr_id != ? AND date = ? AND time >= 1030 AND time <= 1130 AND (userg1 != '' OR userg2 != '' OR userg3 != '' OR userg4 != '' OR userg5 != '')");

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
 //************************************************************************
 //
 //  Olympic Club - special Guest Quota processing.
 //
 //     At this point we know there is more than one guest, the request is for
 //     the Lake or Ocean course, and its the Olympic Club.
 //
 //     Restrictions:
 //
 //        Members are restricted to the number of guests they can have based
 //        on their membership type (indicated in sub-type) and the course.
 //
 //************************************************************************
 **/

 public static boolean checkOlyclubGstQuotas(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String [] usergA = new String[5];
   usergA[0] = "";
   usergA[1] = "";
   usergA[2] = "";
   usergA[3] = "";
   usergA[4] = "";
   
   String sub_type = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String name = "";

   int max = 0;
   int guests = 0;
   int i = 0;
   
   long sdate = 0;
   long edate = 0;
   
   long date = slotParms.date;
   long year = slotParms.date / 10000;                       // break down the tee time date
   long month = (slotParms.date - (year * 10000)) / 100;

   boolean error = false;
   boolean bothCourses = false;
   boolean doCheck = false;
   
   
   //
   //  Get the members that have guests
   //
   if (!slotParms.userg1.equals("")) {
      
      usergA[i] = slotParms.userg1;         // save this member
   }
   if (!slotParms.userg2.equals("") && !slotParms.userg2.equals(usergA[i])) {
      
      if (!usergA[i].equals("")) i++;
      
      usergA[i] = slotParms.userg2;
   }
   if (!slotParms.userg3.equals("") && !slotParms.userg3.equals(usergA[i])) {
      
      if (!usergA[i].equals("")) i++;
      
      usergA[i] = slotParms.userg3;
   }
   if (!slotParms.userg4.equals("") && !slotParms.userg4.equals(usergA[i])) {
      
      if (!usergA[i].equals("")) i++;
      
      usergA[i] = slotParms.userg4;
   }
   if (!slotParms.userg5.equals("") && !slotParms.userg5.equals(usergA[i])) {
      
      if (!usergA[i].equals("")) i++;
      
      usergA[i] = slotParms.userg5;
   }
    
   //
   //  Process the guest quota for each member that has a guest in this tee time
   //
   for (i=0; i<5; i++) {
      
      if (!usergA[i].equals("") && error == false) {      // if a member to process
   
         //  Get this member's name & sub-type (membership privilege code)
         
         sub_type = "";    // init
         
         try {
         
            pstmt = con.prepareStatement("SELECT name_last, name_first, name_mi, msub_type FROM member2b WHERE username = ?");
            pstmt.clearParameters();
            pstmt.setString(1, usergA[i]);

            rs = pstmt.executeQuery();

            if (rs.next()) {    

               // Get the member's full name - get from member2b in case unaccompanied guest (member name not in the tee time)

               StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

               String mi = rs.getString("name_mi");                                // middle initial
               if (!mi.equals( "" )) {
                  mem_name.append(" ");
                  mem_name.append(mi);
               }
               mem_name.append(" " + rs.getString("name_last"));                     // last name

               name = mem_name.toString();                          // convert to one string

               sub_type = rs.getString("msub_type");
            }
         
         } catch (Exception e) {

            Utilities.logError("Error in verifyCustom.checkOlyclubGstQuotas " + e.getMessage());

         } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { pstmt.close(); }
             catch (Exception ignore) {}

         }
            
         doCheck = false;
   
         //
         //  Process according to sub-type
         //
         if (sub_type.equals("G80") || sub_type.equals("MN") || sub_type.equals("NN") || sub_type.equals("FJ") || sub_type.equals("FN")) {
            
            bothCourses = false;         // Ocean Course Only
            max = 1;                     // only 1 guest per Month on the Ocean course allowed for this member
            
            sdate = (year * 10000) + (month * 100) + 1;     //  first of the month of this tee time
            edate = (year * 10000) + (month * 100) + 31;    //  last of the month of this tee time
            
            doCheck = true;
            
         } else if (sub_type.equals("AP") || sub_type.equals("NP") || sub_type.equals("BG") || sub_type.equals("GWL")) {
         
            doCheck = true;
            
            if (month < 4) {
               
               sdate = (year * 10000) + 101;     //  1st Quarter
               edate = (year * 10000) + 331;    
               
            } else if (month < 7 && (year != 2012 || date < 20120626)) {
               
               sdate = (year * 10000) + 401;     //  2nd Quarter
               
               if (year == 2012) {
                   edate = (year * 10000) + 625;    
               } else {               
                   edate = (year * 10000) + 631;    
               }
               
            } else if (month < 10) {
               
               if (year == 2012) {
                   sdate = (year * 10000) + 626;     //  3rd Quarter
               } else {
                   sdate = (year * 10000) + 701;     //  3rd Quarter
               }
               
               edate = (year * 10000) + 931;    
               
            } else {
               
               sdate = (year * 10000) + 1001;     //  4th Quarter
               edate = (year * 10000) + 1231;    
            }
               
            if (sub_type.equals("GWL")) {
         
               bothCourses = false;         // Ocean Course Only
               max = 5;                     // only 5 guests per Quarter on the Ocean course allowed for this member
            
            } else {
               
               bothCourses = true;         // Ocean & Lake Courses
               max = 12;                   // 12 guests per Quarter on the Ocean 7 Lake courses allowed for this member
               
            
               // Custom for period after the US Open in 2012
               if (date >= 20120626 && date <= 20120907) {
                   max = 6;
               }
            }
         }
   
         if (doCheck == true) {      // check this member for guests?

            guests = 0;             // init guest count
            
            //
            //  First check how many guests in this tee time
            //
            if (usergA[i].equals(slotParms.userg1) && !slotParms.player1.startsWith("MHGP Guest Outing")) guests++;
            if (usergA[i].equals(slotParms.userg2) && !slotParms.player2.startsWith("MHGP Guest Outing")) guests++;
            if (usergA[i].equals(slotParms.userg3) && !slotParms.player3.startsWith("MHGP Guest Outing")) guests++;
            if (usergA[i].equals(slotParms.userg4) && !slotParms.player4.startsWith("MHGP Guest Outing")) guests++;
            if (usergA[i].equals(slotParms.userg5) && !slotParms.player5.startsWith("MHGP Guest Outing")) guests++;
            
            //
            //  Now check the number of guests against the max allowed
            //
            if (guests > max) {        // if this request will exceed the max allowed

               slotParms.player = name;   // get this member's name for the error message

               error = true;
        
            } else {

               //
               //  Count # of guest times in the specified date range and on the course(s) specified
               //
               try {
                  
                 if (bothCourses == true) {

                    pstmt = con.prepareStatement (
                       "SELECT userg1, userg2, userg3, userg4, userg5, player1, player2, player3, player4, player5 " +
                       "FROM teecurr2 " +
                       "WHERE teecurr_id != ? AND date >= ? AND date <= ? AND (courseName = 'Ocean' OR courseName = 'Lake') AND " +
                       "(userg1 != '' OR userg2 != '' OR userg3 != '' OR userg4 != '' OR userg5 != '')");

                 } else {

                    pstmt = con.prepareStatement (
                       "SELECT userg1, userg2, userg3, userg4, userg5, player1, player2, player3, player4, player5 " +
                       "FROM teecurr2 " +
                       "WHERE teecurr_id != ? AND date >= ? AND date <= ? AND courseName = 'Ocean' AND " +
                       "(userg1 != '' OR userg2 != '' OR userg3 != '' OR userg4 != '' OR userg5 != '')");
                 }

                 pstmt.setInt(1, slotParms.teecurr_id);
                 pstmt.setLong(2, sdate);
                 pstmt.setLong(3, edate);

                 rs = pstmt.executeQuery();

                 while (rs.next()) {

                     userg1 = rs.getString("userg1");
                     userg2 = rs.getString("userg2");
                     userg3 = rs.getString("userg3");
                     userg4 = rs.getString("userg4");
                     userg5 = rs.getString("userg5");

                     if (userg1.equals(usergA[i]) && !rs.getString("player1").startsWith("MHGP Guest Outing")) guests++;
                     if (userg2.equals(usergA[i]) && !rs.getString("player2").startsWith("MHGP Guest Outing")) guests++;
                     if (userg3.equals(usergA[i]) && !rs.getString("player3").startsWith("MHGP Guest Outing")) guests++;
                     if (userg4.equals(usergA[i]) && !rs.getString("player4").startsWith("MHGP Guest Outing")) guests++;
                     if (userg5.equals(usergA[i]) && !rs.getString("player5").startsWith("MHGP Guest Outing")) guests++;
                 }


                 //
                 //  Now check teepast
                 //
                 if (bothCourses == true) {

                    pstmt = con.prepareStatement (
                       "SELECT userg1, userg2, userg3, userg4, userg5, player1, player2, player3, player4, player5 " +
                       "FROM teepast2 " +
                       "WHERE teecurr_id != ? AND date >= ? AND date <= ? AND (courseName = 'Ocean' OR courseName = 'Lake') AND " +
                       "(userg1 != '' OR userg2 != '' OR userg3 != '' OR userg4 != '' OR userg5 != '')");

                 } else {

                    pstmt = con.prepareStatement (
                       "SELECT userg1, userg2, userg3, userg4, userg5, player1, player2, player3, player4, player5 " +
                       "FROM teepast2 " +
                       "WHERE teecurr_id != ? AND date >= ? AND date <= ? AND courseName = 'Ocean' AND " +
                       "(userg1 != '' OR userg2 != '' OR userg3 != '' OR userg4 != '' OR userg5 != '')");
                 }

                 pstmt.setInt(1, slotParms.teecurr_id);
                 pstmt.setLong(2, sdate);
                 pstmt.setLong(3, edate);

                 rs = pstmt.executeQuery();

                 while (rs.next()) {

                     userg1 = rs.getString("userg1");
                     userg2 = rs.getString("userg2");
                     userg3 = rs.getString("userg3");
                     userg4 = rs.getString("userg4");
                     userg5 = rs.getString("userg5");

                     if (userg1.equals(usergA[i]) && !rs.getString("player1").startsWith("MHGP Guest Outing")) guests++;
                     if (userg2.equals(usergA[i]) && !rs.getString("player2").startsWith("MHGP Guest Outing")) guests++;
                     if (userg3.equals(usergA[i]) && !rs.getString("player3").startsWith("MHGP Guest Outing")) guests++;
                     if (userg4.equals(usergA[i]) && !rs.getString("player4").startsWith("MHGP Guest Outing")) guests++;
                     if (userg5.equals(usergA[i]) && !rs.getString("player5").startsWith("MHGP Guest Outing")) guests++;
                 }

                 //
                 //  Now check the number of guests against the max allowed
                 //
                 if (guests > max) {        // if NOT allowed

                    slotParms.player = name;   // get this member's name for the error message

                    error = true;
                 }

               } catch (Exception e) {

                  Utilities.logError("Error in verifyCustom.checkOlyclubGstQuotas " + e.getMessage());

               } finally {

                   try { rs.close(); }
                   catch (Exception ignore) {}

                   try { pstmt.close(); }
                   catch (Exception ignore) {}
               }
            }
            
         }      // end of IF doCheck
            
      }    // end of IF member
   }       // end of FOR loop
   
   return(error);
 }                    // end of checkOlyclubGstQuotas



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
  * checkTamariskAdvTime - Check for preferred times during the season (times between 8:05am and 11:07am)
  *
  * @param time - Time of the current tee time
  * @return allow - true if before 8:05am or after 11:07am, false otherwise
  *
  * called by:  Member_sheet
  *
  */
 public static boolean checkTamariskAdvTime(int time, long date, int index, int curr_time) {

     boolean allow = true;

     long shortDate = date - ((date / 10000) * 10000);   // get mmdd

     if ((shortDate <= 430 || shortDate >= 1109) && (time >= 805 && time <= 1107)) {      // if in-season and preferred time

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
     if (slotParms.ind > 5 || (slotParms.ind == 5 && cal_time < 800)) {

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
 //  checkMorrisCGCmships - checks certain membership types for max rounds per season.
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
 //  Canterbury GC
 //
 //  Checks Social w/Golf membership type for max rounds during the season.
 //
 //************************************************************************
 **/

 public static boolean checkCGCmships(parmSlot slotParms, Connection con) {


   ResultSet rs = null;
   PreparedStatement pstmt2m = null;
   PreparedStatement pstmt3m = null;

   boolean error = false;

   int count = 0;
   int i = 0;
   int i2 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int max = 10;               // max of 5 full rounds per season (9-hole counts as 1, and 18-hole counts as 2)

   long sdate = 504;         // season is defined as 5/04 thru 9/28
   long edate = 928;

   String mship = "Social ";   // Membership type to check
   String user = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";

   String [] mshipA = new String [5];
   String [] mnumA = new String [5];
   int [] p9A = new int [5];

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
   p9A[0] = slotParms.p91;
   p9A[1] = slotParms.p92;
   p9A[2] = slotParms.p93;
   p9A[3] = slotParms.p94;
   p9A[4] = slotParms.p95;


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
   //
   if (mmdd < sdate || mmdd > edate) {         // if out of season

      sdate = 0;
      edate = 0;       // out of season - skip checks
      
   } else {
       
       sdate = (year * 10000) + sdate;     // get full date values for query
       edate = (year * 10000) + edate;
   }

   //
   //  Count the existing tee times if in season and at least one of the members is a Social w/Golf mship type
   //
   if (sdate > 0  &&
       (mshipA[0].startsWith(mship) || mshipA[1].startsWith(mship) ||
        mshipA[2].startsWith(mship) || mshipA[3].startsWith(mship) ||
        mshipA[4].startsWith(mship))) {

      try {

         //
         // statements for queries
         //
         pstmt2m = con.prepareStatement (
            "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, p91, p92, p93, p94, p95 " +
            "FROM teecurr2 WHERE teecurr_id != ? AND date >= ? AND date <= ? AND " +
                       "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");

         pstmt3m = con.prepareStatement (
            "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, p91, p92, p93, p94, p95 " +
            "FROM teepast2 WHERE date >= ? AND date <= ? AND " +
                       "(mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");


         //
         //  Check each player/family
         //
         i = 0;
         while (i < 5 && error == false) {

            if (mshipA[i].startsWith(mship)) {

                if (p9A[i] > 0) {
                    
                  count = 1;         // count 9-hole round as 1

                } else {
                    
                  count = 2;         // count 18-hole round as 2
                }

               //
               //  Count number of rounds in this tee time for this family
               //
               i2 = i + 1;        // next player

               while (i2 < 5) {

                  if (mnumA[i].equals(mnumA[i2])) {

                      if (p9A[i2] > 0) {

                          count++;           // count 9-hole round as 1

                      } else {

                          count++;         // count 18-hole round as 2
                          count++;       
                      }
                  }
                  i2++;
               }

               //
               //  Count the other existing rounds for this family
               //
               pstmt2m.clearParameters();
               pstmt2m.setInt(1, slotParms.teecurr_id);      // make sure not this tee time
               pstmt2m.setLong(2, sdate);
               pstmt2m.setLong(3, edate);
               pstmt2m.setString(4, mnumA[i]);
               pstmt2m.setString(5, mnumA[i]);
               pstmt2m.setString(6, mnumA[i]);
               pstmt2m.setString(7, mnumA[i]);
               pstmt2m.setString(8, mnumA[i]);
               rs = pstmt2m.executeQuery();

               while (rs.next()) {

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

                  if (mnumA[i].equals(mNum1)) {
                      if (p91 > 0) {
                          count++;         // count 9-hole round as 1
                      } else {
                          count++;         // count 18-hole round as 2
                          count++;       
                      }
                  }
                  if (mnumA[i].equals(mNum2)) {
                      if (p92 > 0) {
                          count++;         // count 9-hole round as 1
                      } else {
                          count++;         // count 18-hole round as 2
                          count++;       
                      }
                  }
                  if (mnumA[i].equals(mNum3)) {
                      if (p93 > 0) {
                          count++;         // count 9-hole round as 1
                      } else {
                          count++;         // count 18-hole round as 2
                          count++;       
                      }
                  }
                  if (mnumA[i].equals(mNum4)) {
                      if (p94 > 0) {
                          count++;         // count 9-hole round as 1
                      } else {
                          count++;         // count 18-hole round as 2
                          count++;       
                      }
                  }
                  if (mnumA[i].equals(mNum5)) {
                      if (p95 > 0) {
                          count++;         // count 9-hole round as 1
                      } else {
                          count++;         // count 18-hole round as 2
                          count++;       
                      }
                  }
               }

               pstmt3m.clearParameters();
               pstmt3m.setLong(1, sdate);
               pstmt3m.setLong(2, edate);
               pstmt3m.setString(3, mnumA[i]);
               pstmt3m.setString(4, mnumA[i]);
               pstmt3m.setString(5, mnumA[i]);
               pstmt3m.setString(6, mnumA[i]);
               pstmt3m.setString(7, mnumA[i]);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

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

                  if (mnumA[i].equals(mNum1)) {
                      if (p91 > 0) {
                          count+= 1;         // count 9-hole round as 1
                      } else {
                          count+= 2;         // count 18-hole round as 2
                      }
                  }
                  if (mnumA[i].equals(mNum2)) {
                      if (p92 > 0) {
                          count+= 1;         // count 9-hole round as 1
                      } else {
                          count+= 2;         // count 18-hole round as 2
                      }
                  }
                  if (mnumA[i].equals(mNum3)) {
                      if (p93 > 0) {
                          count+= 1;         // count 9-hole round as 1
                      } else {
                          count+= 2;         // count 18-hole round as 2
                      }
                  }
                  if (mnumA[i].equals(mNum4)) {
                      if (p94 > 0) {
                          count+= 1;         // count 9-hole round as 1
                      } else {
                          count+= 2;         // count 18-hole round as 2
                      }
                  }
                  if (mnumA[i].equals(mNum5)) {
                      if (p95 > 0) {
                          count+= 1;         // count 9-hole round as 1
                      } else {
                          count+= 2;         // count 18-hole round as 2
                      }
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

          Utilities.logError("SQL Error Checking Max Rounds - verifyCustom.checkCGCmships " + e1.getMessage());        // log the error message
      }

      catch (Exception e) {

          Utilities.logError("Exception Checking Max Rounds - verifyCustom.checkCGCmships " + e.getMessage());        // log the error message

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
 }                  // end of checkCGCmships



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
 //  philcricketrecip - temp site while one of their courses is down
 //
 //  checkPhilcricketRecip - checks members for max rounds per year per course.
 //
 //************************************************************************
 **/

 public static String checkPhilcricketRecip(parmSlot slotParms, Connection con) {


   ResultSet rs = null;
   PreparedStatement pstmt2m = null;
   PreparedStatement pstmt3m = null;

   boolean error = false;

   int count = 0;
   int i = 0;
   int max = 0;               // max rounds per year
   
   int year = (int)slotParms.date / 10000;   // extract the year from the tee time date

   String returnMsg = "";
   String course = slotParms.course;     // shorten the course label

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

   
   // 
   //  Determine the number of times each member can play the selected course
   //
   if (course.equals("Lancaster") || course.equals("Rivercrest")) {
      
      max = 1;
      
   } else if (course.equals("Aronimink") || course.equals("Lookaway") || course.equals("Manufacturers") || course.equals("Stonewall")) {
      
      max = 2;
      
   } else if (course.equals("Ace Club") || course.equals("Huntsville") || course.equals("Jericho National") || course.equals("Ledgerock") || 
              course.equals("Lehigh") || course.equals("Llanerch") || course.equals("North Hills") || course.equals("Overbrook") || 
              course.equals("Philadelphia") || course.equals("Riverton") || course.equals("Running Deer") || course.equals("Sandy Run") || 
              course.equals("St Davids") || course.equals("Sunnybrook") || course.equals("Talamore") || course.equals("Tavistock") || 
              course.equals("Trenton") || course.equals("Waynesborough") || course.equals("Whitemarsh Valley") || course.equals("Wilmington")) {
      
      max = 3;
      
   } else if (course.equals("Applebrook") || course.equals("Bidermann") || course.equals("Cedarbrook") || course.equals("Chester Valley") || 
              course.equals("Green Valley") || course.equals("Gulph Mills") || course.equals("Hersheys Mill") || course.equals("Hidden Creek") || 
              course.equals("Huntingdon Valley") || course.equals("Indian Valley") || course.equals("Lulu") || course.equals("Meadowlands") || 
              course.equals("Old York Road") || course.equals("Radley Run") || course.equals("Radnor Valley") || course.equals("White Manor") || 
              course.equals("Whitford")) {
      
      max = 4;
      
   } else if (course.equals("Blue Bell") || course.equals("Burlington") || course.equals("Commonwealth") || course.equals("Concord") || 
              course.equals("Doylestown") || course.equals("Fieldstone") || course.equals("French Creek") || course.equals("Galloway") || 
              course.equals("Greate Bay") || course.equals("Laurel Creek") || course.equals("Little Mill") || course.equals("Plymouth") || 
              course.equals("Spring Mill") || course.equals("SpringFord") || course.equals("Springhaven") || course.equals("Torresdale Frankford") || 
              course.equals("Trump National")) {
      
      max = 5;
      
   } else {
      
      max = 0;    // no limit
   }
   
   
   if (max > 0) {      // if limited

      //
      //  Count the existing tee times for the month and course of this tee time
      //
      try {

         //
         // statements for queries
         //
         pstmt2m = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM teecurr2 WHERE teecurr_id != ? AND yy = ? AND courseName = ? AND " +
                     "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

         pstmt3m = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM teepast2 WHERE yy = ? AND courseName = ? AND " +
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
               pstmt2m.setInt(2, year);
               pstmt2m.setString(3, slotParms.course);      // and course
               pstmt2m.setString(4, userA[i]);
               pstmt2m.setString(5, userA[i]);
               pstmt2m.setString(6, userA[i]);
               pstmt2m.setString(7, userA[i]);
               pstmt2m.setString(8, userA[i]);
               rs = pstmt2m.executeQuery();

               if (rs.next()) {

                  count = rs.getInt(1);
               }

               pstmt3m.clearParameters();
               pstmt3m.setInt(1, year);
               pstmt3m.setString(2, slotParms.course);      // and course
               pstmt3m.setString(3, userA[i]);
               pstmt3m.setString(4, userA[i]);
               pstmt3m.setString(5, userA[i]);
               pstmt3m.setString(6, userA[i]);
               pstmt3m.setString(7, userA[i]);
               rs = pstmt3m.executeQuery();

               if (rs.next()) {

                  count += rs.getInt(1);
               }

               if (count >= max)  {               // if this time would put the member over the max

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

         Utilities.logError("SQL Error Checking Max Rounds - verifyCustom.checkPhilcricketRecip " + e1.getMessage());        // log the error message
      }

      catch (Exception e) {

         Utilities.logError("Exception Checking Max Rounds - verifyCustom.checkPhilcricketRecip " + e.getMessage());        // log the error message

      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt2m.close(); }
         catch (Exception ignore) {}

         try { pstmt3m.close(); }
         catch (Exception ignore) {}

      }

      if (error == true) {

         returnMsg = "<H3>Quota Exceeded for Member</H3>" +
                        "<BR>Sorry, but " +slotParms.player+ " has already played, or is scheduled to play, the maximum allowed times (" +max+ ") on this course." +
                        "<BR><BR>Please remove the player or return to the tee sheet and try another course.";     
      }
   }

   return(returnMsg);
   
 }                  // end of checkPhilcricketRecip

 
 
 
 
 public static int checkOakmontGuestCount(parmSlot slotParms, String user, Connection con) {

     int gcount = 0;
     
     PreparedStatement pstmtx = null;
     ResultSet rsx = null;
   
     Calendar cal = new GregorianCalendar();       // get todays date
     int calYear = cal.get(Calendar.YEAR);

     try {

         pstmtx = con.prepareStatement("SELECT teecurr_id FROM teecurr2 WHERE teecurr_id <> ? AND yy = ? AND mm >= 6 AND username1 = ? "
                 + "AND (player2 LIKE '$75 Guest%' OR player2 LIKE 'Guest%' OR player2 LIKE 'Family Guest%') "
                 + "AND (player3 LIKE '$75 Guest%' OR player3 LIKE 'Guest%' OR player3 LIKE 'Family Guest%') "
                 + "AND (player4 LIKE '$75 Guest%' OR player4 LIKE 'Guest%' OR player4 LIKE 'Family Guest%')");

         pstmtx.clearParameters();
         pstmtx.setInt(1, slotParms.teecurr_id);
         pstmtx.setInt(2, calYear);
         pstmtx.setString(3, user);

         rsx = pstmtx.executeQuery();

         while (rsx.next()) {
             gcount++;
         }

     } catch (Exception exc) {
         Utilities.logError("verifyCustom.checkOakmontGuestCount - oakmont - Error looking up existing advance guest times - ERR: " + exc.toString());
     } finally {

         try { rsx.close(); }
         catch (Exception ignore) {}

         try { pstmtx.close(); }
         catch (Exception ignore) {}
     }
     
     return (gcount);
 }


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
/*
 public static boolean checkOakmontGuestQuota(parmSlot slotParms, int month, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   int i = 0;
   int i2 = 0;
   int count = 0;
   int max = 1;                             // max is 2 advance guest times per month - use > 1 for compare (was 8 in 2010 and 10 in 2009) // changed to 2 in 2012

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
  */
 

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
      if (day.equals("Monday") && shortDate >= 617 && shortDate <= 819 && time < 1300) {

         status = false;

      } else if (day.equals("Tuesday") && date != 20120702 && shortDate >= 319 && shortDate <= 1031 && time < 1100) {  // exception for Ladies Day (Tues open to 1:00 PM)

         status = false;

      } else if (day.equals("Wednesday") && date != 20120704 && shortDate >= 403 && shortDate <= 1023 && time < 952) {  // exception for Ladies 9-Holers

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
     //  Members can book normal tee times 14 days in advance starting at 8:00 AM ET.
     //  They can book guest times (at least one member and one guest) up to 6 months in advance.
     //  We configured the members for 180 days in advance so now we need to make sure any tee time
     //  that is more than 14 days in advance contains at least 1 mem and 1 guest.
     //
     if (slotParms.ind > 14 || (slotParms.ind == 14 && cal_time < 700)) {   // if more than 14 days in adv

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
      
      During March 29 - October 9:30 - 10:30        *updated 1/13/11*
      1:30 - 2:30
     */
     if (slotParms.day.equals("Friday") &&
        ((((shortdate >= 328 && shortdate <= 531) || (shortdate >= 816 && shortdate <= 1031)) && ((slotParms.time >= 1330 && slotParms.time <= 1430))))) {

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
 /*
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
  */




/**
  ***************************************************************************************
  *
  *   Check if tee time contains at least 2 players (members or guests)
  *
  ***************************************************************************************
  **/
 public static boolean check2Players(parmSlot slotParms) {

     return checkPlayers(slotParms, 2);
 }

/**
  ***************************************************************************************
  *
  *   Replaces the above check2Players with arbitrary number version.
  *   Check to see if tee time contains less than a specific amount of players, as designated by 'num' parameter
  *
  ***************************************************************************************
  **/
 public static boolean checkPlayers(parmSlot slotParms, int num) {

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

     if (count < num) check = true;        // error if only 1 player

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
     int [] p9A = new int [5];                    // array to hold the players' 9-hole indicator

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

     p9A[0] = slotParms.p91;
     p9A[1] = slotParms.p92;
     p9A[2] = slotParms.p93;
     p9A[3] = slotParms.p94;
     p9A[4] = slotParms.p95;

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
                 max = 6;        // max for Singles (no more than 4 - assume this is one) - allow for 9-hole rounds (1 = 9-hole, 2 = 18-hole)

                 if (p9A[i] == 1) max = 7;     // if this is a 9-hole round, then allow one more 1/2 round

                 //
                 //   Check teepast2
                 //
                 pstmt = con.prepareStatement (
                    "SELECT username1, username2, username3, username4, username5, show1, show2, show3, show4, show5, " +
                    "p91, p92, p93, p94, p95 " +
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

                     if (rs.getString("username1").equals(userA[i]) && rs.getInt("show1") == 1) {
                        count++;
                        if (rs.getInt("p91") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }
                     if (rs.getString("username2").equals(userA[i]) && rs.getInt("show2") == 1) {
                        count++;
                        if (rs.getInt("p92") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }
                     if (rs.getString("username3").equals(userA[i]) && rs.getInt("show3") == 1) {
                        count++;
                        if (rs.getInt("p93") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }
                     if (rs.getString("username4").equals(userA[i]) && rs.getInt("show4") == 1) {
                        count++;
                        if (rs.getInt("p94") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }
                     if (rs.getString("username5").equals(userA[i]) && rs.getInt("show5") == 1) {
                        count++;
                        if (rs.getInt("p95") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }

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
                    "SELECT username1, username2, username3, username4, username5, p91, p92, p93, p94, p95 " +
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

                     if (rs.getString("username1").equals(userA[i]) ) {
                        count++;
                        if (rs.getInt("p91") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }
                     if (rs.getString("username2").equals(userA[i]) ) {
                        count++;
                        if (rs.getInt("p92") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }
                     if (rs.getString("username3").equals(userA[i]) ) {
                        count++;
                        if (rs.getInt("p93") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }
                     if (rs.getString("username4").equals(userA[i]) ) {
                        count++;
                        if (rs.getInt("p94") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }
                     if (rs.getString("username5").equals(userA[i]) ) {
                        count++;
                        if (rs.getInt("p95") == 0) {
                           count++;      // count 2 for 18-hole round
                        }
                     }

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

                     if (rs.getString("s.username1").equals(userA[i]) ) count += 2;   // count all events as 18-hole rounds
                     if (rs.getString("s.username2").equals(userA[i]) ) count += 2;
                     if (rs.getString("s.username3").equals(userA[i]) ) count += 2;
                     if (rs.getString("s.username4").equals(userA[i]) ) count += 2;
                     if (rs.getString("s.username5").equals(userA[i]) ) count += 2;

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
                 max = 16;          // max rounds for family (allow for 9-hole rounds - double the max allowed)
                 count = 0;

                 //
                 //  Start with the number of family members in this request
                 //
                 if (mnumA[0].equals(mnumA[i]) ) {
                    count++;
                    if (p9A[0] == 0) count++;      // count 2 if 18-hole round
                 }
                 if (mnumA[1].equals(mnumA[i]) ) {
                    count++;
                    if (p9A[1] == 0) count++;
                 }
                 if (mnumA[2].equals(mnumA[i]) ) {
                    count++;
                    if (p9A[2] == 0) count++;
                 }
                 if (mnumA[3].equals(mnumA[i]) ) {
                    count++;
                    if (p9A[3] == 0) count++;
                 }
                 if (mnumA[4].equals(mnumA[i]) ) {
                    count++;
                    if (p9A[4] == 0) count++;
                 }

                 //
                 //   Check teepast2
                 //
                 pstmt = con.prepareStatement (
                    "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5, " +
                    "p91, p92, p93, p94, p95 " +
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

                     if (rs.getString("mNum1").equals(mnumA[i]) && rs.getInt("show1") == 1) {
                        count++;
                        if (rs.getInt("p91") == 0) count++;      // count 2 for 18-hole round
                     }
                     if (rs.getString("mNum2").equals(mnumA[i]) && rs.getInt("show2") == 1) {
                        count++;
                        if (rs.getInt("p92") == 0) count++;      // count 2 for 18-hole round
                     }
                     if (rs.getString("mNum3").equals(mnumA[i]) && rs.getInt("show3") == 1) {
                        count++;
                        if (rs.getInt("p93") == 0) count++;      // count 2 for 18-hole round
                     }
                     if (rs.getString("mNum4").equals(mnumA[i]) && rs.getInt("show4") == 1) {
                        count++;
                        if (rs.getInt("p94") == 0) count++;      // count 2 for 18-hole round
                     }
                     if (rs.getString("mNum5").equals(mnumA[i]) && rs.getInt("show5") == 1) {
                        count++;
                        if (rs.getInt("p95") == 0) count++;      // count 2 for 18-hole round
                     }

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
                    "SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5, " +
                    "p91, p92, p93, p94, p95 " +
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

                     if (rs.getString("mNum1").equals(mnumA[i]) ) {
                        count++;
                        if (rs.getInt("p91") == 0) count++;      // count 2 for 18-hole round
                     }
                     if (rs.getString("mNum2").equals(mnumA[i]) ) {
                        count++;
                        if (rs.getInt("p92") == 0) count++;      // count 2 for 18-hole round
                     }
                     if (rs.getString("mNum3").equals(mnumA[i]) ) {
                        count++;
                        if (rs.getInt("p93") == 0) count++;      // count 2 for 18-hole round
                     }
                     if (rs.getString("mNum4").equals(mnumA[i]) ) {
                        count++;
                        if (rs.getInt("p94") == 0) count++;      // count 2 for 18-hole round
                     }
                     if (rs.getString("mNum5").equals(mnumA[i]) ) {
                        count++;
                        if (rs.getInt("p95") == 0) count++;      // count 2 for 18-hole round
                     }

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

                     count += 2;                         // all events count as an 18-hole round

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

 public static boolean checkOceanReefMship(long date, int index2, int tee_time, int cal_time, String mship) {

     boolean allow = true;

     if ((date > 1014 || date < 516) && tee_time < 1200) {      // if between Oct 15 and May 15 inclusive and only tee times prior to 12:00pm

        if (mship.equals("Social") || mship.equals("Social Legacy") || mship.equals("Charter") ||
            mship.equals("Charter Legacy") || mship.equals("Multi-Game Card") || mship.equals("Junior Legacy")) {   // days = 3 at 7:00 AM

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

             // Check circumstances based on day of week and time of day
             if (slotParms.day.equalsIgnoreCase("Friday") && slotParms.time >= 1000 && slotParms.time < 1300) {

                 // On Fridays between 10:00am and 1:00pm, they may bring up to 3 guests, but must be guest type "Friday Guest"
                 if (guestCount > 3) {  // More than 3 guests is not allowed, regardless of guest type
                     error = true;
                     slotParms.player = playerA[i];
                     break loop1;

                 } else if (guestCount > 0) {   // If guests are present, but less than 3, make sure they are all "Friday Guest"

                     for (int j=0; j<5; j++) {
                         if (usergA[j].equals(userA[i]) && !playerA[j].startsWith("Friday Guest")) {
                             error = true;
                             slotParms.player = playerA[i];
                             break loop1;
                         }
                     }
                 }

             } else if (slotParms.day.equalsIgnoreCase("Saturday") && slotParms.time >= 1200) {

                 // On Saturdays after 12:00pm, they may bring up to 3 guests (any guest type)
                 if (guestCount > 3) {
                     error = true;
                     slotParms.player = playerA[i];
                     break loop1;
                 }
             } else {

                 // Any other restricted day/time, no guests are allowed
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


 public static int checkEstanciaAdvTimes(parmSlot slotParms, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int count = 0;

     ArrayList<String> userA = new ArrayList<String>();
     ArrayList<String> playerA = new ArrayList<String>();
     ArrayList<String> oldPlayerA = new ArrayList<String>();

     // Populate array lists with current values
     userA.add(slotParms.user1);
     userA.add(slotParms.user2);
     userA.add(slotParms.user3);
     userA.add(slotParms.user4);
     userA.add(slotParms.user5);
     playerA.add(slotParms.player1);
     playerA.add(slotParms.player2);
     playerA.add(slotParms.player3);
     playerA.add(slotParms.player4);
     playerA.add(slotParms.player5);
     oldPlayerA.add(slotParms.oldPlayer1);
     oldPlayerA.add(slotParms.oldPlayer2);
     oldPlayerA.add(slotParms.oldPlayer3);
     oldPlayerA.add(slotParms.oldPlayer4);
     oldPlayerA.add(slotParms.oldPlayer5);

     try {

         loop1:
         for (int i=0; i < 5; i++) {

             if (!playerA.get(i).equals("") && !userA.get(i).equals( "" ) && !playerA.get(i).equals(oldPlayerA.get(i))) {

                 count = 0;

                 pstmt = con.prepareStatement (
                         "SELECT teecurr_id " +
                         "FROM teecurr2 " +
                         "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND teecurr_id <> ? AND custom_int > 30 AND DATEDIFF(date, now()) > 30");
                 pstmt.clearParameters();
                 pstmt.setString(1, userA.get(i));
                 pstmt.setString(2, userA.get(i));
                 pstmt.setString(3, userA.get(i));
                 pstmt.setString(4, userA.get(i));
                 pstmt.setString(5, userA.get(i));
                 pstmt.setInt(6, slotParms.teecurr_id);

                 rs = pstmt.executeQuery();

                 while (rs.next()) {

                     count++;
                 }

                 pstmt.close();

                 if (count >= 4) {
                     slotParms.player = playerA.get(i);      // save player's name for error message
                     break loop1;
                 }
             }
         }

     } catch (Exception exc) {
         count = -1;
     }

     return count;
 }


 public static int checkEstanciaAdvTimes(String user, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int count = 0;

     try {

         if (!user.equals( "" )) {

             count = 0;

             pstmt = con.prepareStatement (
                     "SELECT teecurr_id " +
                     "FROM teecurr2 " +
                     "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) AND custom_int > 30 AND DATEDIFF(date, now()) > 30");
             pstmt.clearParameters();
             pstmt.setString(1, user);
             pstmt.setString(2, user);
             pstmt.setString(3, user);
             pstmt.setString(4, user);
             pstmt.setString(5, user);

             rs = pstmt.executeQuery();

             while (rs.next()) {

                 count++;
             }

             pstmt.close();
         }

     } catch (Exception exc) {
         count = -1;
     }

     return count;
 }

 //
 //  Friday-Sunday, at least 3 players required before 12:00pm (1:00pm during DST)
 //
 public static boolean checkMontereyPlayers(parmSlot slotParms, boolean isDST) {

     boolean error = false;

     // Determine if it's the weekend and during the designated time period
     if ((slotParms.day.equalsIgnoreCase("Friday") || slotParms.day.equalsIgnoreCase("Saturday") || slotParms.day.equalsIgnoreCase("Sunday")) &&
          ((!isDST && slotParms.time < 1200) || (isDST && slotParms.time < 1300)) && (slotParms.time < 730 || slotParms.time > 750)) {

         // Check to see if fewer than 3 players are present
         error = checkPlayers(slotParms, 3);
     }

     return error;
 }


 //
 //  Determine whether time was originally booked within 24 hours of time slot
 //
 public static boolean checkLakewoodActivityTimes(int sheet_id, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int slot_time = 0;
     int res_time = 0;
     int diff = 0;

     boolean result = false;

     try {

         pstmt = con.prepareStatement("SELECT DATE_FORMAT(date_time, '%k%i') AS slot_time, DATE_FORMAT(reserved_at, '%k%i') as res_time, " +
                 "DATEDIFF(date_time, reserved_at) AS diff FROM activity_sheets WHERE sheet_id = ? AND reserved_at <> '0000-00-00 00:00:00'");
         pstmt.clearParameters();
         pstmt.setInt(1, sheet_id);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {

             slot_time = rs.getInt("slot_time");
             res_time = rs.getInt("res_time");
             diff = rs.getInt("diff");

             if (diff == 0 || diff == 1 && res_time >= slot_time) {
                 result = true;
             }
         }

         pstmt.close();

     } catch (Exception exc) {
         
         result = false;
     }

     return result;
 }


 /**
  * checkRMGC4Ball - Checks custom intervals for Royal Montreal GC
  *
  * @param course Course in question
  * @param date Date in question
  * @param day Name of day in question
  *
  * @return result - True if this course, date, and day are 4 ball, false if 3/4 ball
  */
 public static boolean checkRMGC4Ball(String course, long date, String day) {

     boolean result = false;

     if (course.equals("Red Course")) {

         if (Utilities.isEvenDate(date)) {  // Even day

             result = false;

         } else {   // Odd day

             result = true;
         }

     } else if (course.equals("Blue Course")) {

         if (Utilities.isEvenDate(date)) {  // Even day

             result = true;

         } else {   // Odd day

             result = false;
         }
     }

     return result;
 }

 
/**
 // *********************************************************
 //  Check for custom Starter Times for Olympic Club
 // *********************************************************
 **/
 public static boolean checkOlyStarterTime(long date, long ldate, int time, String dayname, String course, String caller) {

   boolean starterTime = false;
   
   /*
   long ldate = 0;
   
   Calendar cal = new GregorianCalendar();       // get todays date
   int calYear = cal.get(Calendar.YEAR);
   
   ldate = (calYear * 10000) + date; 
   */
   
   long yy = ldate / 10000;
   long mm = (ldate - (yy * 10000)) / 100;
   long dd = ldate - ((yy * 10000) + (mm * 100));
   
   Calendar cal = new GregorianCalendar();

   cal.set((int) yy, (int) (mm - 1), (int) dd);
   
   boolean isDST = false;
   
   if (cal.get(Calendar.DST_OFFSET) != 0) {
       isDST = true;
   }

   //
   //   Starter times are based on the season
   //
   if (ldate == 20130528 || ldate == 20130903) {    // Summer Season closed days
       
       starterTime = false;
       
   } else if (isDST) {   // if Summer Season  

      if (course.equals( "Lake" )) {   

         //
         //  Check if requested tee time is a starter time - based on date and course
         //
         if (ldate == Hdate2 || ldate == 20130705 || ldate == Hdate3) {       // if 4th of July or Labor Day
             
            if (time < 1100 || time == 1110 || time == 1130 || time == 1150 || time == 1210 || time == 1230 || time == 1240 || time == 1250 
             || time == 1330 || time == 1350 || time == 1410 || time == 1440 || time == 1500 || time == 1520 || time == 1540 
             || time == 1600 || time == 1620 || time == 1640 || time == 1700 || time == 1700 || time == 1720 || time == 1740 || time == 1800 
             || time == 1820) {

               starterTime = true;                             // indicate we hit a Starter time
            }
             
         } else {     // NOT 4th of July

             if (dayname.equals( "Tuesday" )) {

                if ((time < 1000 && time != 940) || time > 1700 || time == 1010 || time == 1030 || time == 1050 || time == 1110 || 
                    time == 1340 || time == 1400 || time == 1420 || time == 1440 || time == 1500 || time == 1520 || time == 1540 || 
                    time == 1600 || time == 1620 || time == 1640) {

                   starterTime = true;                             // indicate we hit a Starter time
                }
                
             } else if (dayname.equals( "Wednesday" )) {

                if ((time < 900 && time != 840) || time > 1700 || time == 910 || time == 930 || time == 950 ||
                    time == 1010 || time == 1030 || time == 1050 || time == 1110 || 
                    time == 1340 || time == 1400 || time == 1420 || time == 1440 || time == 1500 || time == 1520 || time == 1540 || 
                    time == 1600 || time == 1620 || time == 1640) {

                   starterTime = true;                             // indicate we hit a Starter time
                }

             } else if (dayname.equals( "Wednesday" ) || dayname.equals( "Thursday" ) || (dayname.equals( "Friday" ) && ldate != 20130329)) {

                if (time < 900 || time > 1700 || time == 910 || time == 930 || time == 950 ||
                    time == 1010 || time == 1030 || time == 1050 || time == 1110 || 
                    time == 1340 || time == 1400 || time == 1420 || time == 1440 || time == 1500 || time == 1520 || time == 1540 || 
                    time == 1600 || time == 1620 || time == 1640) {

                   starterTime = true;                             // indicate we hit a Starter time
                }

             } else if (dayname.equals( "Saturday" ) || ldate == Hdate1 || ldate == 20130329) {  // Sat or Mem Day or Labor Day

                if (ldate == Hdate1) {       // if Mem Day

                    if (time < 1100 || time > 1700 || time == 1110 || time == 1130 || time == 1150 || time == 1210 || time == 1230 || time == 1240 || 
                        time == 1250 || time == 1310 || time == 1330 || time == 1350 || time == 1410 || time == 1440) {

                       starterTime = true;                             // indicate we hit a Starter time
                    }

                } else if (time < 1200 || time > 1700 || time == 1210 || time == 1230 || time == 1250 || 
                    time == 1310 || time == 1330 || time == 1350 || time == 1410 || time == 1440 || time == 1500 ||
                    time == 1520 || time == 1540 || time == 1600 || time == 1620 || time == 1640 || time == 1700) {

                   starterTime = true;                             // indicate we hit a Starter time
                }

             } else if (dayname.equals( "Sunday" )) {

                if (time < 1000 || time > 1700 || time == 1010 || time == 1030 || time == 1050 ||
                    time == 1110 || time == 1130 || time == 1150 || time == 1210 || time == 1230 || time == 1250 || 
                    time == 1310 || time == 1330 || time == 1350 || time == 1410 || time == 1430 || time == 1450 ||
                    time == 1510 || time == 1530 || time == 1550 || time == 1610 || time == 1630 || time == 1650) {

                   starterTime = true;                             // indicate we hit a Starter time
                }
             }  

             // 
             // Now check for mid-day times that are only open for MHGP members 24 hours in advance - they must call for these
             //
             if (ldate == 20120529 || ldate == 20120904) {        // if Tues after Mem Day

                 starterTime = false;

             } else if (starterTime == false) {       // if still ok

                if (dayname.equals( "Tuesday" ) || dayname.equals( "Wednesday" ) || dayname.equals( "Thursday" ) || dayname.equals( "Friday" )) {

                   if (time > 1139 && time < 1321 && ((!dayname.equals("Tuesday") && !dayname.equals("Wednesday") || time != 1310))) {          //  11:40 - 1:20

                      starterTime = true;                     // indicate we hit a Starter time
                   }
                }
             }
             
             //
             //  Check for special DST times to be open for members
             //
             if (dayname.equals( "Tuesday" ) && ldate > 20130930 && ldate < 20131031) {     // Special DST times for members

                if (time == 930 || time == 950 || time == 1300 || time == 1320) {

                   starterTime = false;     // Open these to members
                }
             }

             if (dayname.equals( "Wednesday" ) && ldate > 20130930 && ldate < 20131031) {     // Special DST times for members

                if (time == 830 || time == 850 || time == 1300 || time == 1320) {

                   starterTime = false;     // Open these to members
                }
             }

             if (dayname.equals( "Saturday" ) && ldate > 20130930 && ldate < 20131103) {     // Special DST times for members

                if (time == 1340 || time == 1400 || time == 1420) {

                   starterTime = false;     // Open these to members
                }
             }
             
         }        // end of IF NOT 4th of July
         
      } else if (course.equals( "Ocean" )) {
         
          if (ldate == Hdate2 || ldate == 20130705 || ldate == Hdate3) {

            if (time <= 1020 || time == 1040 || time == 1100 || time == 1120 || time == 1140 || time == 1200 || time == 1220 
             || time == 1240 || time == 1300 || time == 1320 || time == 1340 || time == 1400 || time == 1420 || time == 1440 
             || time == 1500 || time == 1520 || time == 1540 || time == 1600 || time == 1620 || time == 1640 || time == 1700 
             || time == 1720 || time == 1740 || time == 1800 || time == 1820) {

               starterTime = true;                             // indicate we hit a Starter time
            }
              
          } else if (dayname.equals( "Tuesday" ) && (ldate != 20120911 || time != 800)) {

            if (time < 1000 || time > 1700 || time == 1010 || time == 1030 || time == 1050 || 
                time == 1110 || time == 1130 || time == 1150 || 
                time == 1340 || time == 1400 || time == 1420 || time == 1440 || time == 1500 || time == 1520 || time == 1540 || 
                time == 1600 || time == 1620 || time == 1640) {

               starterTime = true;                             // indicate we hit a Starter time
            }

         } else if (dayname.equals( "Wednesday" ) || dayname.equals( "Thursday" ) || (dayname.equals( "Friday" ) && ldate != 20130329)) {

            if (time < 900 || time > 1700 || time == 910 || time == 930 || time == 950 ||
                time == 1010 || time == 1030 || time == 1050 || time == 1110 || time == 1140 || time == 1150 || 
                time == 1340 || time == 1400 || time == 1420 || time == 1440 || time == 1500 || time == 1520 || time == 1540 || 
                time == 1600 || time == 1620 || time == 1640) {

               starterTime = true;                             // indicate we hit a Starter time
            }
            
         } else if (dayname.equals( "Saturday" ) || ldate == Hdate1 || ldate == 20130329) {

            if (time < 1000 || time > 1700 || time == 1010 || time == 1030 || time == 1050 || (time > 1100 && time < 1200) || 
                time == 1210 || time == 1230 || time == 1250 || time == 1310 || time == 1330 || time == 1350 || time == 1410 || 
                time == 1440 || time == 1500 || time == 1520 || time == 1540 || time == 1600 || time == 1620 || time == 1640 || time == 1700) {

               starterTime = true;                             // indicate we hit a Starter time
            }
            
         } else if (dayname.equals( "Sunday" )) {

            if (time < 1000 || time > 1700 || time == 1010 || time == 1030 || time == 1050 ||
                time == 1120 || time == 1140 || time == 1200 || time == 1220 || time == 1240 || 
                time == 1300 || time == 1320 || time == 1340 || time == 1400 || time == 1420 || time == 1440 ||
                time == 1500 || time == 1520 || time == 1540 || time == 1600 || time == 1620 || time == 1640) {

               starterTime = true;                             // indicate we hit a Starter time
            }
         }  
         
         // 
         // Now check for mid-day times that are only open for MHGP members 24 hours in advance - they must call for these
         //
         if (starterTime == false && ldate != Hdate2 && ldate != 20130705) {       // if still ok
            
            if (dayname.equals( "Tuesday" ) || dayname.equals( "Wednesday" ) || dayname.equals( "Thursday" ) || dayname.equals( "Friday" ) ||
                ldate == Hdate1) {

               if (time > 1139 && time < 1321) {          //  11:40 - 1:20

                  starterTime = true;                     // indicate we hit a Starter time
               }
            }
         }
         
      }      // end of course and date checks

    
   } else if (date == 1225 || ldate == 20130102 || ldate == 20140121 || ldate == 20140218) {             // if Christmas Day or day after New Years
      
      starterTime = false;                  // Club Closed
      
   } else {        // Winter Season   
      
      if (course.equals( "Lake" )) {   

         //
         //  Check if requested tee time is a starter time - based on date and course
         //
         if (date == 1224 || date == 1231 || date == 101 || ldate == 20140120 || ldate == 20140217) {      // Holiday ?

            if (time < 1030 || time > 1700 || time == 1040 || time == 1100 || time == 1120 || 
                time == 1140 || time == 1200 || time == 1220 || time == 1240 || time == 1300 || 
                time == 1320 || time == 1340 || time == 1400 || time == 1420 || time == 1440 || time == 1500 ||
                time == 1520 || time == 1540 || time == 1600 || time == 1620 || time == 1640) {

               starterTime = true;                             // indicate we hit a Starter time
            }
            
         } else if (dayname.equals( "Tuesday" )) {

            if (time < 1000 || time > 1700 || (time >= 1110 && time <= 1250) || time == 1010 || time == 1030 || time == 1050 || time == 1310 ||  
                time == 1330 || time == 1350 || time == 1410 || time == 1430 || time == 1450 || time == 1510 || time == 1530 || 
                time == 1550 || time == 1610 || time == 1630 || time == 1650) {

               starterTime = true;                             // indicate we hit a Starter time
            }

         } else if (dayname.equals( "Wednesday" ) || (dayname.equals( "Thursday" ) && ldate != Hdate7) || (dayname.equals( "Friday" ) && ldate != 20131129 && ldate != (Hdate7 + 1))) {

            if (time < 900 || time > 1700 || (time >= 1110 && time <= 1250) || time == 910 || time == 930 || time == 950 ||
                time == 1010 || time == 1030 || time == 1050 || time == 1310 || time == 1330 || 
                time == 1350 || time == 1410 || time == 1430 || time == 1450 || time == 1510 || time == 1530 || time == 1550 || 
                time == 1610 || time == 1630 || time == 1650) {

               starterTime = true;                             // indicate we hit a Starter time
            }
            
         } else if (dayname.equals( "Saturday" ) || ldate == Hdate7 || ldate == (Hdate7 + 1) || ldate == 20131129) {

            if (time < 1130 || time > 1700 || time == 1140 || time == 1200 || time == 1220 || time == 1240 || time == 1300 || 
                time == 1320 || time == 1340 || time == 1400 || time == 1420 || time == 1440 || time == 1500 ||
                time == 1520 || time == 1540 || time == 1600 || time == 1620 || time == 1640) {

               starterTime = true;                             // indicate we hit a Starter time
            }
            
         } else if (dayname.equals( "Sunday" )) {

            if (time < 1000 || time > 1700 || time == 1010 || time == 1030 || time == 1050 ||
                time == 1110 || time == 1130 || time == 1150 || time == 1210 || time == 1230 || time == 1250 || 
                time == 1310 || time == 1330 || time == 1350 || time == 1410 || time == 1430 || time == 1450 ||
                time == 1510 || time == 1530 || time == 1550 || time == 1610 || time == 1630 || time == 1650) {

               starterTime = true;                             // indicate we hit a Starter time
            }
         }  
         
         
      } else if (course.equals( "Ocean" )) {
         
         if (ldate == Hdate7 || ldate == (Hdate7 + 1) || date == 1224 || date == 1231 || date == 101 || ldate == 20140120 || ldate == 20140217) {

            if (time < 1000 || time > 1700 || time == 1010 || time == 1030 || time == 1050 ||
                time == 1110 || time == 1130 || time == 1150 || time == 1210 || time == 1230 || time == 1250 || 
                time == 1310 || time == 1330 || time == 1350 || time == 1410 || time == 1430 || time == 1450 ||
                time == 1510 || time == 1530 || time == 1550 || time == 1610 || time == 1630 || time == 1650) {

                starterTime = true;                          // indicate we hit a Starter time 
            }
               
            /*
            if (time < 1130 || time > 1700 || time == 1140 || time == 1200 || time == 1220 || time == 1240 || time == 1300 || 
                time == 1320 || time == 1340 || time == 1400 || time == 1420 || time == 1440 || time == 1500 ||
                time == 1520 || time == 1540 || time == 1600 || time == 1620 || time == 1640) {

               starterTime = true;                             // indicate we hit a Starter time
            }
               
            if (time < 1110 || time > 1700 || time == 1120 || time == 1140 || time == 1200 || time == 1220 || 
                time == 1240 || time == 1300 || time == 1320 || time == 1340 || time == 1400 || time == 1420 || 
                time == 1440 || time == 1500 || time == 1520 || time == 1540 || time == 1600 || time == 1620 || 
                time == 1640) {

               starterTime = true;                             // indicate we hit a Starter time
            }
              */
             
         } else if (dayname.equals( "Tuesday" )) {

            if (time < 1000 || time > 1700 || (time >= 1110 && time <= 1250) || time == 1010 || time == 1030 || time == 1050 || 
                time == 1310 || time == 1330 || time == 1350 || 
                time == 1410 || time == 1430 || time == 1450 || time == 1510 || time == 1530 || time == 1550 || 
                time == 1610 || time == 1630 || time == 1650) {

               starterTime = true;                             // indicate we hit a Starter time
            }

         } else if (dayname.equals( "Wednesday" ) || dayname.equals( "Thursday" ) || (dayname.equals( "Friday" ))) {

            if (time < 900 || time > 1700 || (time >= 1110 && time <= 1250) || time == 910 || time == 930 || time == 950 ||
                time == 1010 || time == 1030 || time == 1050 || time == 1310 || time == 1330 || 
                time == 1350 || time == 1410 || time == 1430 || time == 1450 || time == 1510 || time == 1530 || time == 1550 || 
                time == 1610 || time == 1630 || time == 1650) {

               starterTime = true;                             // indicate we hit a Starter time
            }
            
         } else if (dayname.equals( "Saturday" )) {

            if (time < 1000 || time > 1700 || time == 1010 || time == 1030 || time == 1050 || (time > 1100 && time < 1130) || 
                time == 1140 || time == 1200 || time == 1220 || time == 1240 || time == 1300 || 
                time == 1320 || time == 1340 || time == 1400 || time == 1420 || time == 1440 || 
                time == 1500 || time == 1520 || time == 1540 || time == 1600 || time == 1620 || time == 1640) {

               starterTime = true;                             // indicate we hit a Starter time
            }
            
         } else if (dayname.equals( "Sunday" )) {

            if (time < 1000 || time > 1700 || time == 1010 || time == 1030 || time == 1050 ||
                time == 1110 || time == 1130 || time == 1150 || time == 1210 || time == 1230 || time == 1250 || 
                time == 1310 || time == 1330 || time == 1350 || time == 1410 || time == 1430 || time == 1450 ||
                time == 1510 || time == 1530 || time == 1550 || time == 1610 || time == 1630 || time == 1650) {

               starterTime = true;                             // indicate we hit a Starter time
            }
         }  
      }      // end of course and date checks
      
   }           // end of season checks 

   return(starterTime);

 }  // end of checkOlyStarterTime


 public static int checkRaceBrookStarterNumTime(long date, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int time = 0;

     try {

         pstmt = con.prepareStatement("SELECT MAX(custom_int) FROM teecurr2 WHERE DATE = ? AND (custom_disp1 = '1' OR custom_disp2 = '1' OR custom_disp3 = '1' OR custom_disp4 = '1' OR custom_disp5 = '1');");
         pstmt.clearParameters();
         pstmt.setLong(1, date);

         rs = pstmt.executeQuery();

         if (rs.next()) {
             time = rs.getInt(1);
         }

     } catch (Exception ignore) {

     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }
     }

     return time;
 }


 public static boolean checkMinnetonka3someTimes(long date, int time, String day_name, int fb) {

     boolean result = false;

     int yy = (int)date / 10000;                             // get year
     int mm = ((int)date - (yy * 10000)) / 100;              // get month
     int dd = ((int)date - (yy * 10000)) - (mm * 100);       // get day

     int startWeek = 0;
     int curWeek = 0;

     int shortDate = (mm * 100) + dd;

     // If in league season
     if ((day_name.equals("Wednesday") && shortDate >= 518 && shortDate <= 824 && time >= 817 && time <= 949) ||
         (day_name.equals("Thursday") && shortDate >= 518 && shortDate <= 818 && date != 20130613 && date != 20130620 && time >= 759 && time <= 931)) {

         if (day_name.equals("Thursday") && fb == 0) {      // Thursday is always on the front
             result = true;
         } else {           // Wednesday alternates between front and back each week
             Calendar calStart = new GregorianCalendar();
             Calendar calCur = new GregorianCalendar();

             calStart.set(2011, 4, 18);
             calCur.set(yy, mm - 1, dd);

             startWeek = calStart.get(Calendar.WEEK_OF_YEAR);
             curWeek = calCur.get(Calendar.WEEK_OF_YEAR);

             if (((startWeek % 2) == (curWeek % 2) && fb == 0) ||
                 ((startWeek % 2) != (curWeek % 2) && fb == 1)) {     // Every other week is front or back 9, check if the original week is even or odd, and if the current week matches or not to determine this.

                 result = true;
             }
         }


     }

     return result;
 }

 public static boolean checkMisquamicut2someTimes(long date, int time, String day_name) {

     boolean result = false;

     int yy = (int)date / 10000;                             // get year
     int mm = ((int)date - (yy * 10000)) / 100;              // get month
     int dd = ((int)date - (yy * 10000)) - (mm * 100);       // get day

     int shortDate = (mm * 100) + dd;

     /*   // updated on 8/27 - see below 
     if (((day_name.equals("Saturday") || day_name.equals("Sunday") || date == Hdate1 || date == Hdate2 || date == Hdate3) && time >= 800 && time <= 830) ||
          (day_name.equals("Monday") && date != Hdate1 && date != Hdate2 && date != Hdate3 && time >= 1000 && time <= 1030) || 
          (day_name.equals("Thursday") && shortDate >= 701 && shortDate <= 830 && time >= 800 && time <= 825)) {
          * 
          */

     if (day_name.equals("Monday") && time >= 1000 && time <= 1020) {

         result = true;
         
     } else if (!day_name.equals("Monday") && time >= 800 && time <= 820) {

         result = true;
     }

     return result;
 }

 
 public static boolean checkCherryValley2someTimes(long date, int time, String day_name) {

     boolean result = false;

     int yy = (int)date / 10000;                             // get year
     int mm = ((int)date - (yy * 10000)) / 100;              // get month
     int dd = ((int)date - (yy * 10000)) - (mm * 100);       // get day

     int shortDate = (mm * 100) + dd;

     if ((day_name.equals("Saturday") || day_name.equals("Sunday") || day_name.equals("Monday")) &&
        ((((shortDate >= 501 && shortDate <= 531) || (date > Hdate3 && shortDate <= 930)) && time >= 730 && time <= 750) ||
        (shortDate >= 601 && date <= Hdate3 && time >= 700 && time <= 720))) {

         result = true;
     }

     return result;
 }

 
 public static boolean checkEchoLake2someTimes(long date, int time, String day_name) {

     boolean result = false;

     if (day_name.equals("Wednesday") && date >= 20140424 && date <= 20141009 && time >= 800 && time <= 1029) {

         result = true;
     }

     return result;
 }

 public static boolean checkTPCBostonMships(parmSlot slotParms, Connection con) {

     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     PreparedStatement pstmt3 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;
     ResultSet rs3 = null;

     boolean result = false;

     String mship = "";
     String mNum = "";

     try {

         // Look up current user's mship and mNum
         pstmt = con.prepareStatement("SELECT m_ship, memNum FROM member2b WHERE username = ?");
         pstmt.clearParameters();
         pstmt.setString(1, slotParms.user);

         rs = pstmt.executeQuery();

         if (rs.next()) {

             mship = rs.getString("m_ship");
             mNum = rs.getString("memNum");

             // If current user's membership type is one of the following, check for any existing tee time originations by their family on this day
             if (mship.equals("AFFINITY PARTNER") || mship.equals("CHARTER") || mship.equals("CORPORATE ASSOCIATE") ||
                     mship.equals("INTERNATIONAL") || mship.equals("INTERNATIONAL CORPORATE") || mship.equals("JUNIOR") ||
                     mship.equals("LEGACY") || mship.equals("OUT OF TOWN") || mship.equals("PATRON") || mship.equals("TRADITIONAL") ||
                     mship.equals("WOUNDED WARRIOR") || mship.equals("YOUNG PROFESSIONAL")) {
                 
                 try {

                     // Look up any family members (matching mNum) of current user
                     pstmt2 = con.prepareStatement("SELECT username FROM member2b WHERE memNum = ?");
                     pstmt2.clearParameters();
                     pstmt2.setString(1, mNum);

                     rs2 = pstmt2.executeQuery();

                     while (rs2.next()) {

                         String tempUser = rs2.getString("username");

                         try {

                             // Check each family member to see if they've originated any other tee times on this day
                             pstmt3 = con.prepareStatement("SELECT teecurr_id FROM teecurr2 WHERE date = ? AND teecurr_id <> ? AND orig_by = ? LIMIT 1");
                             pstmt3.setLong(1, slotParms.date);
                             pstmt3.setInt(2, slotParms.teecurr_id);
                             pstmt3.setString(3, tempUser);

                             rs3 = pstmt3.executeQuery();

                             if (rs3.next()) {

                                 // Existing tee time origination found, flag as error and break out of family loop
                                 result = true;
                                 break;
                             }

                         } catch (Exception exc) {
                                 Utilities.logError("verifyCustom.checkTPCBostonMships - " + slotParms.club + " - Error looking up family tee time originations for user:" + slotParms.user + " - Err: " + exc.toString());
                         } finally {

                             try { rs3.close(); }
                             catch (Exception ignore) { }

                             try { pstmt3.close(); }
                             catch (Exception ignore) { }
                         }
                     }

                 } catch (Exception exc) {
                         Utilities.logError("verifyCustom.checkTPCBostonMships - " + slotParms.club + " - Error looking up family members for user " + slotParms.user + " - Err: " + exc.toString());
                 } finally {

                     try { rs2.close(); }
                     catch (Exception ignore) { }

                     try { pstmt2.close(); }
                     catch (Exception ignore) { }
                 }
             }
         }

     } catch (Exception exc) {
         Utilities.logError("verifyCustom.checkTPCBostonMships - " + slotParms.club + " - Error looking up mship/mNum data for user:" + slotParms.user + " - Err: " + exc.toString());
     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }
     }

     return result;
 }


 public static boolean checkPineBrookNonIndoorTimes(parmSlot slotParms, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean error = false;

     int count = 0;

     String[] playerA = new String[5];
     String[] userA = new String[5];
     String[] mshipA = new String[5];

     playerA[0] = slotParms.player1;                 // get the player names
     playerA[1] = slotParms.player2;
     playerA[2] = slotParms.player3;
     playerA[3] = slotParms.player4;
     playerA[4] = slotParms.player5;

     userA[0] = slotParms.user1;                 // get the new users
     userA[1] = slotParms.user2;
     userA[2] = slotParms.user3;
     userA[3] = slotParms.user4;
     userA[4] = slotParms.user5;

     mshipA[0] = slotParms.mship1;                 // get the new mships
     mshipA[1] = slotParms.mship2;
     mshipA[2] = slotParms.mship3;
     mshipA[3] = slotParms.mship4;
     mshipA[4] = slotParms.mship5;

     if (slotParms.activity_id == 2 || slotParms.activity_id == 3 || slotParms.activity_id == 4 || slotParms.activity_id == 5) {

         for (int i=0; i<5; i++) {

             if (!mshipA[i].equals("") && (mshipA[i].equals("Non-Indoor Tennis") || !mshipA[i].endsWith("& Indoor"))) {

                 try {
                     pstmt = con.prepareStatement("" +
                             "SELECT count(*) " +
                             "FROM activity_sheets_players asp " +
                             "LEFT OUTER JOIN activity_sheets ash ON ash.sheet_id = asp.activity_sheet_id " +
                             "WHERE asp.username = ? AND (ash.activity_id = 2 OR ash.activity_id = 3 OR ash.activity_id = 4 OR ash.activity_id = 5) AND " +
                             "DATE_FORMAT(ash.date_time,'%Y') = ? AND DATE_FORMAT(ash.date_time,'%m') = ? AND ash.sheet_id <> ? AND report_ignore = 0");
                     pstmt.clearParameters();
                     pstmt.setString(1, userA[i]);
                     pstmt.setInt(2, slotParms.yy);
                     pstmt.setInt(3, slotParms.mm);
                     pstmt.setInt(4, slotParms.slot_id);

                     rs = pstmt.executeQuery();

                     if (rs.next()) {
                         count = rs.getInt(1);
                     }

                     if (count > 0) {
                         slotParms.player = playerA[i];
                         error = true;
                     }

                 } catch (Exception exc) {
                     count = 0;
                     Utilities.logError("verifyCustom.checkPineBrookNonIndoorTimes - Error looking up play history for " + userA[i] + " - Err: " + exc.toString());
                 } finally {

                     try { rs.close(); }
                     catch (Exception ignore) { }

                     try { pstmt.close(); }
                     catch (Exception ignore) { }
                 }
             }
         }
     }

     return error;
 }


 public static boolean checkIndianRidgeMships(parmSlot slotParms, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean error = false;

     int date = (int)slotParms.date;
     int yy = date / 10000;                             // get year
     int mm = (date - (yy * 10000)) / 100;              // get month
     int dd = (date - (yy * 10000)) - (mm * 100);       // get day

     int shortDate = (mm * 100) + dd;

     int sdate = 0;
     int edate = 0;
     int count = 0;

     String[] playerA = new String[5];
     String[] mshipA = new String[5];
     String[] userA = new String[5];

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

     userA[0] = slotParms.user1;
     userA[1] = slotParms.user2;
     userA[2] = slotParms.user3;
     userA[3] = slotParms.user4;
     userA[4] = slotParms.user5;

     // If between Nov 1 and June 30, check for relevant mships
     if (shortDate >= 1101 || shortDate <= 630) {

         for (int i=0; i<5; i++) {

             count = 0;

             if (mshipA[i].equalsIgnoreCase("LIMITED EQUITY GOLF FITNESS") || mshipA[i].equalsIgnoreCase("LIMITED EQUITY GOLF SOCIAL")) {

                 // Determine start and end dates for searched based on where current date is (know what year values to use)
                 if (shortDate <= 630) {

                     sdate = ((yy - 1) * 10000) + 1101;     // yyyy1101 (previous year)
                     edate = (yy * 10000) + 630;            // yyyy0630 (current year)

                 } else {

                     sdate = (yy * 10000) + 1101;           // yyyy1101 (current year)
                     edate = ((yy + 1) * 10000) + 630;      // yyyy0630 (next year)
                 }

                 // Search past times
                 try {

                     // Check past rounds
                     pstmt = con.prepareStatement("SELECT count(*) FROM teepast2 WHERE date >= ? AND date <= ? AND " +
                             "((username1 = ? AND show1 = 1) OR (username2 = ? AND show2 = 1) OR (username3 = ? AND show3 = 1) OR (username4 = ? AND show4 = 1) OR (username5 = ? AND show5 = 1))");
                     pstmt.clearParameters();
                     pstmt.setInt(1, sdate);
                     pstmt.setInt(2, edate);
                     pstmt.setString(3, userA[i]);
                     pstmt.setString(4, userA[i]);
                     pstmt.setString(5, userA[i]);
                     pstmt.setString(6, userA[i]);
                     pstmt.setString(7, userA[i]);

                     rs = pstmt.executeQuery();

                     if (rs.next()) {
                         count = rs.getInt(1);
                     }

                 } catch (Exception exc) {
                     Utilities.logError("verifyCustom.checkIndianRidgeMships - Error looking up play history for " + userA[i] + " - Err: " + exc.toString());
                 } finally {

                     try { rs.close(); }
                     catch (Exception ignore) { }

                     try { pstmt.close(); }
                     catch (Exception ignore) { }
                 }

                 // Search current times
                 try {

                     // Check past rounds
                     pstmt = con.prepareStatement("SELECT count(*) FROM teecurr2 WHERE date >= ? AND date <= ? AND teecurr_id <> ? AND " +
                             "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
                     pstmt.clearParameters();
                     pstmt.setInt(1, sdate);
                     pstmt.setInt(2, edate);
                     pstmt.setInt(3, slotParms.teecurr_id);
                     pstmt.setString(4, userA[i]);
                     pstmt.setString(5, userA[i]);
                     pstmt.setString(6, userA[i]);
                     pstmt.setString(7, userA[i]);
                     pstmt.setString(8, userA[i]);

                     rs = pstmt.executeQuery();

                     if (rs.next()) {
                         count += rs.getInt(1);
                     }

                 } catch (Exception exc) {
                     Utilities.logError("verifyCustom.checkIndianRidgeMships - Error looking up play history for " + userA[i] + " - Err: " + exc.toString());
                 } finally {

                     try { rs.close(); }
                     catch (Exception ignore) { }

                     try { pstmt.close(); }
                     catch (Exception ignore) { }
                 }

                 if (count >= 30) {
                     slotParms.player = playerA[i];
                     error = true;
                 }
             }
         }
     }

     return error;
 }

 
 public static boolean checkFireRockMships(parmSlot slotParms, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean error = false;
     
     Calendar cal = new GregorianCalendar();       // get todays date
     int calYear = cal.get(Calendar.YEAR);
     
     int sdate = (calYear * 10000) + 1020;
     int edate = ((calYear + 1) * 10000) + 515;
     int count = 0;

     String[] playerA = new String[5];
     String[] mshipA = new String[5];
     String[] userA = new String[5];

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

     userA[0] = slotParms.user1;
     userA[1] = slotParms.user2;
     userA[2] = slotParms.user3;
     userA[3] = slotParms.user4;
     userA[4] = slotParms.user5;
     
     for (int i=0; i<5; i++) {
         
         count = 0;
         
         if (mshipA[i].equalsIgnoreCase("Sports")) {
             
             try {
                 
                 // Get rounds from teepast
                 pstmt = con.prepareStatement("SELECT teepast_id FROM teepast2 WHERE date >= ? AND ((username1 = ? AND mship1 = 'Sports' AND show1 = 1) OR (username2 = ? AND mship2 = 'Sports' AND show2 = 1) OR "
                         + "(username3 = ? AND mship3 = 'Sports' AND show3 = 1) OR (username4 = ? AND mship4 = 'Sports' AND show4 = 1) OR (username5 = ? AND mship5 = 'Sports' AND show5 = 1))");
                 pstmt.clearParameters();
                 pstmt.setInt(1, sdate);
                 pstmt.setString(2, userA[i]);
                 pstmt.setString(3, userA[i]);
                 pstmt.setString(4, userA[i]);
                 pstmt.setString(5, userA[i]);
                 pstmt.setString(6, userA[i]);
                 
                 rs = pstmt.executeQuery();
                 
                 while (rs.next()) {
                     count++;
                 }
                 
             } catch (Exception exc) {
                 Utilities.logError("verifyCustom.checkFireRockMships - firerockcc - Error checking teepast rounds - Err: " + exc.toString());
             } finally {
                 
                 try { rs.close(); }
                 catch (Exception ignore) { }
                 
                 try { pstmt.close(); }
                 catch (Exception ignore) { }
             }
             
             try {
                 
                 // Get rounds from teecurr
                 pstmt = con.prepareStatement("SELECT teecurr_id FROM teecurr2 WHERE date <= ? AND teecurr_id <> ? AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
                 pstmt.clearParameters();
                 pstmt.setInt(1, edate);
                 pstmt.setInt(2, slotParms.teecurr_id);
                 pstmt.setString(3, userA[i]);
                 pstmt.setString(4, userA[i]);
                 pstmt.setString(5, userA[i]);
                 pstmt.setString(6, userA[i]);
                 pstmt.setString(7, userA[i]);
                 
                 rs = pstmt.executeQuery();
                 
                 while (rs.next()) {
                     count++;
                 }
                 
             } catch (Exception exc) {
                 Utilities.logError("verifyCustom.checkFireRockMships - firerockcc - Error checking teepast rounds - Err: " + exc.toString());
             } finally {
                 
                 try { rs.close(); }
                 catch (Exception ignore) { }
                 
                 try { pstmt.close(); }
                 catch (Exception ignore) { }
             }
             
             if (count >= 14) {
                 
                 slotParms.player = playerA[i];
                 error = true;
             }
             
         }
     }
     
     return error;
 }
 
 public static boolean checkMiraVistaGuestTimes(parmSlot slotParms, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     boolean error = false;
     
     if (slotParms.guests > 0 && slotParms.time >= 700 && (slotParms.day.equals("Tuesday") || slotParms.day.equals("Wednesday") || slotParms.day.equals("Thursday") || slotParms.day.equals("Friday"))) {
         
         int hr = slotParms.time / 100;
         int count = 0;
         
         String temp_player = "";
         
         try {
         
             parmClub clubParm = new parmClub(0, con);

             getClub.getParms(con, clubParm, 0);
             
             pstmt = con.prepareStatement("SELECT player1, player2, player3, player4, player5 FROM teecurr2 WHERE teecurr_id <> ? AND date = ? AND hr = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, slotParms.teecurr_id);
             pstmt.setLong(2, slotParms.date);
             pstmt.setInt(3, hr);
             
             rs = pstmt.executeQuery();
             
             loop1:
             while (rs.next()) {
                 
                 for (int i=1; i<6; i++) {
                     
                     temp_player = rs.getString("player" + i);
                     
                     if (!temp_player.equals("")) {

                         for (int j=0; j < clubParm.MAX_Guests; j++) {

                             if (!clubParm.guest[j].equals( "" ) && temp_player.startsWith(clubParm.guest[j])) {   // if guest name is open for members
                                 
                                 count++;
                                 continue loop1;
                             }
                         }
                     }
                 }
             }
             
         } catch (Exception exc) {
             Utilities.logError("verifyCustom.checkMiraVistaGuestTimes - miravista - Error looking up existing guest times - ERR: " + exc.toString());
         } finally {
             
             try { rs.close(); }
             catch (Exception ignore) { }
             
             try { pstmt.close(); }
             catch (Exception ignore) { }
         }
         
         if (count >= 2) {
             
             error = true;
         }
     }
     
     return error;
 }
 
 /**
  * checkOlyClubIsProshopEventSignup - Method to determine whether or not a given event signup was originally created by a member or proshop user.
  * 
  * @param event_signup_id ID of the event signup in question
  * @param con Connection to the club database
  * 
  * @return result - True if Proshop user, false if member
  */
 public static boolean checkOlyClubIsProshopEventSignup(int event_signup_id, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     boolean result = false;
     
     try {
         
         pstmt = con.prepareStatement("SELECT event_log_id FROM event_log WHERE event_signup_id = ? AND action = 'CREATE' AND user like 'proshop%'");
         pstmt.clearParameters();
         pstmt.setInt(1, event_signup_id);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             result = true;
         }
         
         
     } catch (Exception exc) {
         Utilities.logError("verifyCustom.checkOlyClubIsProshopEventSignup - olyclub - Error looking up event_log data - ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return result;
 }
 
 public static boolean checkMissionValleyMships(parmSlot slotParms, Connection con) {
     
     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;
     
     boolean error = false;
     
     String[] mNumA = new String[5];
     String[] playerA = new String[5];
     String[] mshipA = new String[5];

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

     mNumA[0] = slotParms.mNum1;
     mNumA[1] = slotParms.mNum2;
     mNumA[2] = slotParms.mNum3;
     mNumA[3] = slotParms.mNum4;
     mNumA[4] = slotParms.mNum5;
     
     int count = 0;
     int sdate = 0;
     int edate = 0;
     
     // Determine the correct start and end of the date range to check within
     if (slotParms.mm >= 11) {
         
         sdate = (slotParms.yy * 10000) + 1101;
         edate = ((slotParms.yy + 1) * 10000) + 430;
         
     } else if (slotParms.mm <= 4) {
         
         
         sdate = ((slotParms.yy - 1) * 10000) + 1101;
         edate = (slotParms.yy * 10000) + 430;
     }
     
     // loop through the players
     for (int i=0; i<5; i++) {
         
         // Custom only applies to social members
         if (mshipA[i].equalsIgnoreCase("Social")) {
         
             count = 0;     // reset counter

             // See how many are in the current tee time
             if (slotParms.mNum1.equalsIgnoreCase(mNumA[i])) count++;
             if (slotParms.mNum2.equalsIgnoreCase(mNumA[i])) count++;
             if (slotParms.mNum3.equalsIgnoreCase(mNumA[i])) count++;
             if (slotParms.mNum4.equalsIgnoreCase(mNumA[i])) count++;
             if (slotParms.mNum5.equalsIgnoreCase(mNumA[i])) count++;

             // Don't bother continuing if we're already over the limit
             if (count <= 6) {

                 try {

                     // Get count from currently booked rounds
                     pstmt = con.prepareStatement("SELECT mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 WHERE teecurr_id <> ? AND date >= ? AND date <= ? AND (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");
                     pstmt.clearParameters();
                     pstmt.setInt(1, slotParms.teecurr_id);
                     pstmt.setInt(2, sdate);
                     pstmt.setInt(3, edate);
                     pstmt.setString(4, mNumA[i]);
                     pstmt.setString(5, mNumA[i]);
                     pstmt.setString(6, mNumA[i]);
                     pstmt.setString(7, mNumA[i]);
                     pstmt.setString(8, mNumA[i]);

                     rs = pstmt.executeQuery();

                     while (rs.next()) {

                         if (rs.getString("mNum1").equalsIgnoreCase(mNumA[i])) count++;
                         if (rs.getString("mNum2").equalsIgnoreCase(mNumA[i])) count++;
                         if (rs.getString("mNum3").equalsIgnoreCase(mNumA[i])) count++;
                         if (rs.getString("mNum4").equalsIgnoreCase(mNumA[i])) count++;
                         if (rs.getString("mNum5").equalsIgnoreCase(mNumA[i])) count++;
                     }


                 } catch (Exception exc) {
                     Utilities.logError("verifyCustom.checkMissionViejoMships - missionviejo - Error looking up currently booked rounds for member number data - ERR: " + exc.toString());
                 } finally {

                     try { rs.close(); }
                     catch (Exception ignore) { }

                     try { pstmt.close(); }
                     catch (Exception ignore) { }
                 }
             }

             // Don't bother continuing if we're already over the limit
             if (count <= 6) {

                 try {

                     // Get count from past rounds
                     pstmt = con.prepareStatement("SELECT mNum1, mNum2, mNum3, mNum4, mNum5 FROM teepast2 WHERE date >= ? AND date <= ? AND (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");
                     pstmt.clearParameters();
                     pstmt.setInt(1, sdate);
                     pstmt.setInt(2, edate);
                     pstmt.setString(3, mNumA[i]);
                     pstmt.setString(4, mNumA[i]);
                     pstmt.setString(5, mNumA[i]);
                     pstmt.setString(6, mNumA[i]);
                     pstmt.setString(7, mNumA[i]);

                     rs = pstmt.executeQuery();

                     while (rs.next()) {

                         if (rs.getString("mNum1").equalsIgnoreCase(mNumA[i])) count++;
                         if (rs.getString("mNum2").equalsIgnoreCase(mNumA[i])) count++;
                         if (rs.getString("mNum3").equalsIgnoreCase(mNumA[i])) count++;
                         if (rs.getString("mNum4").equalsIgnoreCase(mNumA[i])) count++;
                         if (rs.getString("mNum5").equalsIgnoreCase(mNumA[i])) count++;
                     }


                 } catch (Exception exc) {
                     Utilities.logError("verifyCustom.checkMissionViejoMships - missionviejo - Error looking up past rounds for member number data - ERR: " + exc.toString());
                 } finally {

                     try { rs.close(); }
                     catch (Exception ignore) { }

                     try { pstmt.close(); }
                     catch (Exception ignore) { }
                 }
             }

             if (count > 6) {
                 
                 slotParms.player = playerA[i];
                 error = true;
                 break;
             }
         }
     }
     
     return error;     
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
   userA[4] = slotParms.user5;

   olduserA[0] = slotParms.oldUser1;           // get the old users
   olduserA[1] = slotParms.oldUser2;
   olduserA[2] = slotParms.oldUser3;
   olduserA[3] = slotParms.oldUser4;
   olduserA[4] = slotParms.oldUser5;


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
 
 public static String checkLotteryRegistrationAccess(HttpSession session, HttpServletRequest req, Connection con) {
     
    String club = (String) session.getAttribute("club");      // get club name
    String mship = (String) session.getAttribute("mship");
    
    String errorMessage = "";  // empty string = allow access
    
    if (club.equals("dataw")) {
        if (mship.equalsIgnoreCase("Island Social") || mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Sports Membership")) {

            errorMessage = "Sorry, you are restricted from submitting lottery requests due to membership type.";

        }
    }
    
    return errorMessage;
    
 }
 
 public static int checkHallbrookJrsAndDeps(parmSlot slotParms) {
     
     String[] playerA = new String[5];
     String[] mtypeA = new String[5];
     
     playerA[0] = slotParms.player1;
     playerA[1] = slotParms.player2;
     playerA[2] = slotParms.player3;
     playerA[3] = slotParms.player4;
     playerA[4] = slotParms.player5;

     mtypeA[0] = slotParms.mtype1;
     mtypeA[1] = slotParms.mtype2;
     mtypeA[2] = slotParms.mtype3;
     mtypeA[3] = slotParms.mtype4;
     mtypeA[4] = slotParms.mtype5;
     
     int error = 0;
     
     // Loop through the players and check their mtypes for "Junior" or "Dependant"
     for (int i=0; i<5; i++) {
         
         if (mtypeA[i].equalsIgnoreCase("Junior")) {
             
             if (slotParms.time < 1500) {
                 
                error = 1;
                slotParms.player = playerA[i];
                
             } else if (!mtypeA[0].startsWith("Adult") && !mtypeA[1].startsWith("Adult") && !mtypeA[2].startsWith("Adult")
                     && !mtypeA[3].startsWith("Adult") && !mtypeA[4].startsWith("Adult")) {
                 
                error = 2;
                slotParms.player = playerA[i];
             }
             
         } else if (mtypeA[i].equalsIgnoreCase("Dependant")) {
             
             if (slotParms.day.equals("Friday") && slotParms.time >= 1130 && slotParms.time < 1300) {
                 
                error = 3;
                slotParms.player = playerA[i];
                
             } else if (slotParms.day.equals("Saturday") || slotParms.day.equals("Sunday") || slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3) {
                 
                 if (slotParms.time < 1000) {
                     
                     error = 4;
                     slotParms.player = playerA[i];
                
                 } else if (slotParms.time < 1200 && !mtypeA[0].startsWith("Adult") && !mtypeA[1].startsWith("Adult") && !mtypeA[2].startsWith("Adult")
                         && !mtypeA[3].startsWith("Adult") && !mtypeA[4].startsWith("Adult")) {
                     
                     error = 5;
                     slotParms.player = playerA[i];
                 }
                 
             } 
         }
     }
     
     return error;
 }
 
 
    // ************************************************************************
    //  Eagle Creek - check if user is cheating and came in early
    // ************************************************************************
    public static boolean checkECearly(String user, int ind, int count, int daysInAdv, int advTime, Connection con) {


        String errMsg = "Eagle Creek Member attempting to access tee time early.  Error = ";
        boolean error = false;

        //
        //  Get this exact time and see if the user is trying to get into a tee time early
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int thishr = cal.get(Calendar.HOUR_OF_DAY);
        int thismin = cal.get(Calendar.MINUTE);
        int thissec = cal.get(Calendar.SECOND);

        int thisTime = (thishr * 100) + thismin;   // get current time (Central TIme - hhmm)
        
        thisTime = Utilities.adjustTime(con, thisTime);   // adjust the time for time zone

        thisTime = (thisTime * 100) + thissec;      // i.e.  70012 (7:00 + 12 seconds)

        advTime = (advTime * 100) + 2;              // i.e.  70002 (7:00 + 2 seconds)

        //
        //  Is user too early or requesting too many tee times?
        //
        if (ind > daysInAdv) {                 // too many days in adv?

            error = true;
            errMsg = errMsg + ind + " days in advance. User = " + user;

        } else if (ind == daysInAdv && thisTime < advTime) {  // if 2/3 days in adv and 7:00 AM ET or earlier

            error = true;
            errMsg = errMsg + "Too early. Time = " + thisTime + " CT, User = " + user;

        } else if (count > 2) {

            error = true;
            errMsg = errMsg + "Too many times requested. Times = " + count + " CT, User = " + user;
        }

        if (error == true) {

            Utilities.logError(errMsg);             // log it
        }

        return (error);

    }       // end of checkECearly
    
    public static boolean checkAwbreyGlenMnumRoundCounts(parmSlot slotParms, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean error = false;

        Calendar cal = new GregorianCalendar();       // get todays date
        int calYear = cal.get(Calendar.YEAR);

        int sdate = (calYear * 10000) + 1020;
        int edate = ((calYear + 1) * 10000) + 515;

        String[] playerA = new String[5];
        String[] mshipA = new String[5];
        String[] mNumA = new String[5];
        String[] userA = new String[5];

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
        
        int roundCount = 0;
        int maxRounds = 30;
        
        for (int i = 0; i < 5; i++) {
            
            roundCount = 0;
            
            if (mshipA[i].equals("Non-Resident Golf")) {

                for (int j = 0; j < 5; j++) {

                    // Check this tee time for players with this member number
                    if (mNumA[j].equalsIgnoreCase(mNumA[i])) {

                        roundCount++;
                    } 
                }

                try {

                    // Check past tee times from this year for this member number
                    pstmt = con.prepareStatement("SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5, mship1, mship2, mship3, mship4, mship5 FROM teepast2 WHERE yy = ? AND "
                            + "((mNum1 = ? AND show1 = 1) OR (mNum2 = ? AND show3 = 1) OR (mNum3 = ? AND show3 = 1) OR (mNum4 = ? AND show4 = 1) OR (mNum5 = ? AND show5 = 1))");
                    pstmt.clearParameters();
                    pstmt.setInt(1, calYear);
                    pstmt.setString(2, mNumA[i]);
                    pstmt.setString(3, mNumA[i]);
                    pstmt.setString(4, mNumA[i]);
                    pstmt.setString(5, mNumA[i]);
                    pstmt.setString(6, mNumA[i]);

                    rs = pstmt.executeQuery();

                    while (rs.next()) {
                        if (rs.getInt("show1") == 1 && rs.getString("mship1").equals("Non-Resident Golf") && rs.getString("mNum1").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getInt("show2") == 1 && rs.getString("mship2").equals("Non-Resident Golf") && rs.getString("mNum2").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getInt("show3") == 1 && rs.getString("mship3").equals("Non-Resident Golf") && rs.getString("mNum3").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getInt("show4") == 1 && rs.getString("mship4").equals("Non-Resident Golf") && rs.getString("mNum4").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getInt("show5") == 1 && rs.getString("mship5").equals("Non-Resident Golf") && rs.getString("mNum5").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                    }

                } catch (Exception exc) {
                    Utilities.logError("verifyCustom.checkAwbreyGlenMnumCounts - Error looking up past tee times - ERR: " + exc.toString());
                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }

                try {

                    // Check past tee times from this year for this member number
                    pstmt = con.prepareStatement("SELECT mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 WHERE yy = ? AND teecurr_id <> ? AND (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");
                    pstmt.clearParameters();
                    pstmt.setInt(1, calYear);
                    pstmt.setInt(2, slotParms.teecurr_id);
                    pstmt.setString(3, mNumA[i]);
                    pstmt.setString(4, mNumA[i]);
                    pstmt.setString(5, mNumA[i]);
                    pstmt.setString(6, mNumA[i]);
                    pstmt.setString(7, mNumA[i]);

                    rs = pstmt.executeQuery();

                    while (rs.next()) {
                        if (rs.getString("mNum1").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getString("mNum2").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getString("mNum3").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getString("mNum4").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getString("mNum5").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                    }

                } catch (Exception exc) {
                    Utilities.logError("verifyCustom.checkAwbreyGlenMnumCounts - Error looking up current tee times - ERR: " + exc.toString());
                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }
               
                if (roundCount > maxRounds) {
                    slotParms.player = playerA[i];
                    error = true;
                }
            }
        }
        
        return error;
    }
    
    
    /**
     * Checks members round counts for specified mship for the current winter season to ensure they haven't hit their max number of 18 hole rounds (or combined 9 hole rounds) for the season
     * 
     * @param slotParms Parm block containing tee time data
     * @param mshipToCheck Membership type restriction applies to
     * @param shortDate_start mmdd format date signifying the start of the date range where this restriction applies
     * @param shortDate_end mmdd format date signifying the end of the date range where this restriction applies
     * @param max Max # of 18-hole rounds (or combined 9-hole rounds) each member number is allowed to play.
     * @param con Connection to club database
     * @return boolean - True if member is over limit, false if not
     */
    public static boolean checkWinterSeasonMshipRoundCountsByMnum(parmSlot slotParms, String mshipToCheck, int shortDate_start, int shortDate_end, int max, boolean count_event_times, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        boolean error = false;
        
        String event_query = "";
        
        int yy_start = 0;
        int yy_end = 0;
        int max_rounds = max * 2;    // Double the max so we can count 9-hole and 18-hole rounds easier (9 = 1, 18 = 2)
        int count = 0;
        
        String[] mshipA = new String[5];
        String[] userA = new String[5];
        String[] mNumA = new String[5];
        String[] playerA = new String[5];
        
        int[] p9A = new int[5];
        
        for (int i = 0; i < 5; i++) {
            mshipA[i] = "";
            userA[i] = "";
            mNumA[i] = "";
            playerA[i] = "";
            p9A[i] = 0;
        }
        
        if (slotParms.mm >= 11) {
            yy_start = slotParms.yy;
            yy_end = slotParms.yy + 1;
        } else if (slotParms.mm <= 4) {
            yy_start = slotParms.yy - 1;
            yy_end = slotParms.yy;
        }
        
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
        
        p9A[0] = slotParms.p91;
        p9A[1] = slotParms.p92;
        p9A[2] = slotParms.p93;
        p9A[3] = slotParms.p94;
        p9A[4] = slotParms.p95;
        
        // If we're not counting event times, add a condition to the query that ignores them
        if (!count_event_times) {
            event_query = " AND event = ''";
        }
        
        // loop through players and see if any are an 'Associate" member
        mainLoop:
        for (int i = 0; i < 5; i++) {
            
            count = 0;  // reset count
            
            // If any player has an mship of "Associate", check their play history for the current year.
            if (mshipA[i].equalsIgnoreCase(mshipToCheck)) {
                
                // Tally up round count for the round being booked
                for (int j = 0; j < 5; j++) {
                    if (j == i || mNumA[j].equalsIgnoreCase(mNumA[i])) {
                        if (p9A[j] == 0) {
                            count += 2;
                        } else {
                            count++;
                        }
                            
                    }
                }            
                
                try {
                    
                    pstmt = con.prepareStatement(""
                            + "SELECT p91, p92, p93, p94, p95, mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5 "
                            + "FROM teepast2 "
                            + "WHERE ((mNum1 = ? AND show1 = 1) OR (mNum2 = ? AND show2 = 1) OR (mNum3 = ? AND show3 = 1) OR (mNum4 = ? AND show4 = 1) OR (mNum5 = ? AND show5 = 1)) "
                            + "AND ((yy = ? AND ((mm * 100) + dd) >= ?) OR (yy = ? AND ((mm * 100) + dd) <= ?))" 
                            + event_query);
                    
                    pstmt.clearParameters();
                    pstmt.setString(1, mNumA[i]);
                    pstmt.setString(2, mNumA[i]);
                    pstmt.setString(3, mNumA[i]);
                    pstmt.setString(4, mNumA[i]);
                    pstmt.setString(5, mNumA[i]);
                    pstmt.setInt(6, yy_start);
                    pstmt.setInt(7, shortDate_start);
                    pstmt.setInt(8, yy_end);
                    pstmt.setInt(9, shortDate_end);
                    
                    rs = pstmt.executeQuery();
                    
                    // Loop through tee times and tally rounds for this mNum in each.
                    while (rs.next()) {
                        
                        // Loop through results and tally any players in this tee time that match the mNum we're currently checking.
                        for (int k = 1; k <= 5; k++) {
                            
                            if (rs.getString("mNum" + k).equalsIgnoreCase(mNumA[i]) && rs.getInt("show" + k) == 1) {
                                
                                // If 18 hole round, add 2 to count, if 9 hole add 1.
                                if (rs.getInt("p9" + k) == 0) {
                                    count += 2;
                                } else {
                                    count++;
                                }
                            }
                        }
                    }
                   
                } catch (Exception exc) {
                    Utilities.logError("verifyCustom.checkWinterSeasonMshipRoundCounts - Error looking up past rounds - ERR: " + exc.toString());
                } finally {
                    
                    try { rs.close(); }
                    catch (Exception ignore) {}
                    
                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }
                
                // Only bother checking current tee times if we're still under the max_rounds.
                if (count <= max_rounds) {

                    try {

                        pstmt = con.prepareStatement(""
                                + "SELECT p91, p92, p93, p94, p95, mNum1, mNum2, mNum3, mNum4, mNum5 "
                                + "FROM teecurr2 "
                                + "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND teecurr_id <> ? "
                                + "AND ((yy = ? AND ((mm * 100) + dd) >= ?) OR (yy = ? AND ((mm * 100) + dd) <= ?))"
                                + event_query);

                        pstmt.clearParameters();
                        pstmt.setString(1, mNumA[i]);
                        pstmt.setString(2, mNumA[i]);
                        pstmt.setString(3, mNumA[i]);
                        pstmt.setString(4, mNumA[i]);
                        pstmt.setString(5, mNumA[i]);
                        pstmt.setInt(6, slotParms.teecurr_id);
                        pstmt.setInt(7, yy_start);
                        pstmt.setInt(8, shortDate_start);
                        pstmt.setInt(9, yy_end);
                        pstmt.setInt(10, shortDate_end);

                        rs = pstmt.executeQuery();

                        // Loop through tee times and tally rounds for this mNum in each.
                        while (rs.next()) {

                            // Loop through results and tally any players in this tee time that match the mNum we're currently checking.
                            for (int k = 1; k <= 5; k++) {

                                if (rs.getString("mNum" + k).equalsIgnoreCase(mNumA[i])) {

                                    // If 18 hole round, add 2 to count, if 9 hole add 1.
                                    if (rs.getInt("p9" + k) == 0) {
                                        count += 2;
                                    } else {
                                        count++;
                                    }
                                }
                            }
                        }

                    } catch (Exception exc) {
                        Utilities.logError("verifyCustom.checkWinterSeasonMshipRoundCounts - Error looking up current rounds - ERR: " + exc.toString());
                    } finally {

                        try { rs.close(); }
                        catch (Exception ignore) {}

                        try { pstmt.close(); }
                        catch (Exception ignore) {}
                    }
                }
                            
                // If they're over the max_rounds, break out of the main loop and return the error
                if (count > max_rounds) {
                    error = true;
                    slotParms.player = playerA[i];
                    break mainLoop;
                }
            }
        }
        
        return (error);
    }
    
    public static boolean checkDesertHorizonsCCGuestsPerMember(parmSlot slotParms, Connection con) {
        
        boolean error = false;
        
        int guest_count = 0;
        
        String[] userA = new String[5];
        String[] usergA = new String[5];
        String[] playerA = new String[5];
        
        for (int i = 0; i < 5; i++) {
            userA[i] = "";
            usergA[i] = "";
            playerA[i] = "";
        }
                
        userA[0] = slotParms.user1;
        userA[1] = slotParms.user2;
        userA[2] = slotParms.user3;
        userA[3] = slotParms.user4;
        userA[4] = slotParms.user5;
                
        usergA[0] = slotParms.userg1;
        usergA[1] = slotParms.userg2;
        usergA[2] = slotParms.userg3;
        usergA[3] = slotParms.userg4;
        usergA[4] = slotParms.userg5;
        
        playerA[0] = slotParms.player1;
        playerA[1] = slotParms.player2;
        playerA[2] = slotParms.player3;
        playerA[3] = slotParms.player4;
        playerA[4] = slotParms.player5;
        
        for (int i = 0; i < 5; i++) {
            
            guest_count = 0;
            
            if (!userA[i].equals("")) {

                for (int j = 0; j < 5; j++) {              

                    if (j != i && !usergA[j].equals("") && usergA[j].equalsIgnoreCase(userA[i])) {
                        guest_count++;
                    }
                }

                if (guest_count > 1) {
                    slotParms.player = playerA[i];
                    error = true;
                }
            }
        }
        
        return (error);        
    }
    
    
    public static int checkDesertHorizonsCCGuestCount(parmSlot slotParms, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int gcount = slotParms.guests;
        
        try {
            
            // Query gathers the sum of all guests (either userg is set or starts with 'Unaccompanied') during the date and time range in question
            pstmt = con.prepareStatement("SELECT (SUM(IF(userg1 <> '' OR player1 LIKE 'Unaccompanied%', 1, 0)) + SUM(IF(userg2 <> '' OR player2 LIKE 'Unaccompanied%', 1, 0)) "
                    + "+ SUM(IF(userg3 <> '' OR player3 LIKE 'Unaccompanied%', 1, 0)) + SUM(IF(userg4 <> '' OR player4 LIKE 'Unaccompanied%', 1, 0)) + SUM(IF(userg5 <> '' OR player5 LIKE 'Unaccompanied%', 1, 0))) AS gcount "
                    + "FROM teecurr2 WHERE date = ? AND time >= 800 AND time <= 1000 AND teecurr_id <> ?");
            pstmt.clearParameters();
            pstmt.setLong(1, slotParms.date);
            pstmt.setInt(2, slotParms.teecurr_id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                if (rs.getInt("gcount") > 0) {
                    gcount += rs.getInt("gcount");
                }
            }
            
        } catch (Exception exc) {
            Utilities.logError("verifyCustom.checkDesertHorizonsCCGuestCount - " + slotParms.club + " - Error looking up guest count for date = " + slotParms.date + " - ERR: " + exc.toString());
        } finally {
            
            try { rs.close(); }
            catch (Exception ignore) {}
            
            try { pstmt.close(); }
            catch (Exception ignore) {}
        }
        
        return (gcount);
    }
    
    
    public static boolean checkDiscoveryBayGuests(parmSlot slotParms, Connection con) {
        
        boolean error = false;
        
        String[] userA = new String[5];
        String[] usergA = new String[5];
        String[] playerA = new String[5];
        String[] mshipA = new String[5];
        
        for (int i = 0; i < 5; i++) {
            userA[i] = "";
            usergA[i] = "";
            playerA[i] = "";
            mshipA[i] = "";
        }
                
        userA[0] = slotParms.user1;
        userA[1] = slotParms.user2;
        userA[2] = slotParms.user3;
        userA[3] = slotParms.user4;
        userA[4] = slotParms.user5;
                
        usergA[0] = slotParms.userg1;
        usergA[1] = slotParms.userg2;
        usergA[2] = slotParms.userg3;
        usergA[3] = slotParms.userg4;
        usergA[4] = slotParms.userg5;
        
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
        
        mainLoop:
        for (int i = 0; i < 4; i++) {
            
            if (!userA[i].equals("") && mshipA[i].equalsIgnoreCase("Single Golf")) {
                
                for (int j = i+1; j < 5; j++) {
                    
                    if (playerA[j].startsWith("Junior") && usergA[j].equalsIgnoreCase(userA[i])) {
                        
                        slotParms.player = playerA[i];
                        error = true;
                        break mainLoop;
                    }
                }
            }
        }
        
        return (error);
    }
    
    public static int checkElginAdvanceGuests(parmSlot slotParms, int maxGuests, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int error = 0;
        int guestCount = 0;
        int index = slotParms.ind;
        
        if (index > 365) {
            index = 0;
        }
        
        String[] userA = new String[5];
        String[] usergA = new String[5];
        String[] playerA = new String[5];
        String[] mshipA = new String[5];
        String[] mtypeA = new String[5];

        for (int i = 0; i < 5; i++) {
            userA[i] = "";
            usergA[i] = "";
            playerA[i] = "";
            mshipA[i] = "";
            mtypeA[i] = "";
        }

        userA[0] = slotParms.user1;
        userA[1] = slotParms.user2;
        userA[2] = slotParms.user3;
        userA[3] = slotParms.user4;
        userA[4] = slotParms.user5;

        usergA[0] = slotParms.userg1;
        usergA[1] = slotParms.userg2;
        usergA[2] = slotParms.userg3;
        usergA[3] = slotParms.userg4;
        usergA[4] = slotParms.userg5;

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

        mtypeA[0] = slotParms.mtype1;
        mtypeA[1] = slotParms.mtype2;
        mtypeA[2] = slotParms.mtype3;
        mtypeA[3] = slotParms.mtype4;
        mtypeA[4] = slotParms.mtype5;

        mainLoop:
        for (int i = 0; i < 5; i++) {

            guestCount = 0;

            if (!userA[i].equals("")) {
                
                // Determine if we're outside of standard days in advance or not
                if ((slotParms.day.equals("Saturday") && index > 3)    // 3 days in advance on Saturdays
                        || (slotParms.day.equals("Sunday") && index > 4)    // 4 days on Sun
                        || (slotParms.day.equals("Monday") && index > 5)    // 5 days on Mon
                        || (index > 7)) {   // 7 days all the rest

                    // Social Members, Certified Juniors, and Non Certified Juniors are not allowed to bring guests in advance of their booking privileges. 
                    if (mshipA[i].equalsIgnoreCase("Social Member") || mtypeA[i].equalsIgnoreCase("Certified Junior") || mtypeA[i].equalsIgnoreCase("Non Certified Junior")) {

                        for (int j = i + 1; j < 5; j++) {

                            if (usergA[j].equals(userA[i])) {
                                error = 1;    
                                slotParms.player = playerA[i];
                                break mainLoop;
                            }
                        }
                    } else {
                        
                        /*
                        if (usergA[0].equals(userA[i])) guestCount++;
                        if (usergA[1].equals(userA[i])) guestCount++;
                        if (usergA[2].equals(userA[i])) guestCount++;
                        if (usergA[3].equals(userA[i])) guestCount++;
                        if (usergA[4].equals(userA[i])) guestCount++;

                        try {

                            pstmt = con.prepareStatement("SELECT userg1, userg2, userg3, userg4, userg5 FROM teecurr2 WHERE teecurr_id <> ? AND custom_int <> 0 AND lottery = '' "
                                    + "AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) "
                                    + "AND (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");
                            pstmt.clearParameters();
                            pstmt.setInt(1, slotParms.teecurr_id);
                            pstmt.setString(2, userA[i]);
                            pstmt.setString(3, userA[i]);
                            pstmt.setString(4, userA[i]);
                            pstmt.setString(5, userA[i]);
                            pstmt.setString(6, userA[i]);
                            pstmt.setString(7, userA[i]);
                            pstmt.setString(8, userA[i]);
                            pstmt.setString(9, userA[i]);
                            pstmt.setString(10, userA[i]);
                            pstmt.setString(11, userA[i]);

                            rs = pstmt.executeQuery();

                            while (rs.next()) {
                                if (rs.getString("userg1").equals(userA[i])) guestCount++;
                                if (rs.getString("userg2").equals(userA[i])) guestCount++;
                                if (rs.getString("userg3").equals(userA[i])) guestCount++;
                                if (rs.getString("userg4").equals(userA[i])) guestCount++;
                                if (rs.getString("userg5").equals(userA[i])) guestCount++;
                            }

                        } catch (Exception exc) {
                            Utilities.logError("verifyCustom.checkElginAdvanceGuests - " + slotParms.club + " - Error looking up advance guest rounds - ERR: " + exc.toString());
                        } finally {

                            try { rs.close(); }
                            catch (Exception ignore) {}

                            try { pstmt.close(); }
                            catch (Exception ignore) {}
                        }

                        if (guestCount > maxGuests) {                      

                            error = 2;
                            slotParms.player = playerA[i];
                            break mainLoop;
                        }
                        */
                    } 
                        
                } else {

                    // Normal booking window
                    if (mtypeA[i].equalsIgnoreCase("Certified Junior") && slotParms.time <= 1300) {

                        for (int j = i + 1; j < 5; j++) {

                            if (usergA[j].equals(userA[i])) {
                                error = 3;    
                                slotParms.player = playerA[i];
                                break mainLoop;
                            }
                        }
                    }
                }
            }
        }
        
        return (error);
    }
    
    public static boolean checkSubtypeActivities(parmSlot slotParms, int activity_id, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        boolean error = false;
                
        String act_char = "";
        String msub_type = "";

        if (slotParms.club.equals("philcricket")) {
            if (activity_id == 1) {
                act_char = "T";
            } else if (activity_id == 2) {
                act_char = "S";
            } else if (activity_id == 3) {
                act_char = "P";
            }
        } else if (slotParms.club.equals("quecheeclubtennis")) {
            if (activity_id == 1) {
                act_char = "T";
            } else if (activity_id == 13) {
                act_char = "P";
            } else if (activity_id == 16) {
                act_char = "S";
            }
        }
        
        String[] userA = new String[5];
        String[] playerA = new String[5];

        for (int i = 0; i < 5; i++) {
            userA[i] = "";
            playerA[i] = "";
        }

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

        mainLoop:
        for (int i = 0; i < 5; i++) {

            if (!userA[i].equals("")) {
                
                try {
                    pstmt = con.prepareStatement("SELECT msub_type FROM member2b WHERE username = ?");
                    pstmt.clearParameters();
                    pstmt.setString(1, userA[i]);
                    
                    rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        msub_type = rs.getString(1);
                    }
                    
                    if (!msub_type.contains(act_char)) {
                        slotParms.player = playerA[i];
                        error = true;
                        break mainLoop;
                    }
                    
                } catch (Exception exc) {
                    
                } finally {
                    
                    try { rs.close(); }
                    catch (Exception ignore) {}
                    
                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }
            }
        }
        
        return (error);
    }
    
    public static boolean checkRivertonCCMnumRoundCounts(parmSlot slotParms, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean error = false;

        Calendar cal = new GregorianCalendar();       // get todays date
        int calYear = cal.get(Calendar.YEAR);

        int sdate = (calYear * 10000) + 401;
        int edate = (calYear * 10000) + 1031;

        String[] playerA = new String[5];
        String[] mshipA = new String[5];
        String[] mNumA = new String[5];
        String[] userA = new String[5];

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
        
        int roundCount = 0;
        int maxRounds = 10;
        
        for (int i = 0; i < 5; i++) {
            
            roundCount = 0;
            
            if (mshipA[i].equals("House")) {

                for (int j = 0; j < 5; j++) {

                    // Check this tee time for players with this member number
                    if (mNumA[j].equalsIgnoreCase(mNumA[i])) {

                        roundCount++;
                    } 
                }

                try {

                    // Check past tee times from this year for this member number
                    pstmt = con.prepareStatement("SELECT mNum1, mNum2, mNum3, mNum4, mNum5, show1, show2, show3, show4, show5 FROM teepast2 WHERE yy = ? AND "
                            + "((mNum1 = ? AND show1 = 1) OR (mNum2 = ? AND show3 = 1) OR (mNum3 = ? AND show3 = 1) OR (mNum4 = ? AND show4 = 1) OR (mNum5 = ? AND show5 = 1))");
                    pstmt.clearParameters();
                    pstmt.setInt(1, calYear);
                    pstmt.setString(2, mNumA[i]);
                    pstmt.setString(3, mNumA[i]);
                    pstmt.setString(4, mNumA[i]);
                    pstmt.setString(5, mNumA[i]);
                    pstmt.setString(6, mNumA[i]);

                    rs = pstmt.executeQuery();

                    while (rs.next()) {
                        if (rs.getInt("show1") == 1 && rs.getString("mNum1").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getInt("show2") == 1 && rs.getString("mNum2").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getInt("show3") == 1 && rs.getString("mNum3").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getInt("show4") == 1 && rs.getString("mNum4").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getInt("show5") == 1 && rs.getString("mNum5").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                    }

                } catch (Exception exc) {
                    Utilities.logError("verifyCustom.checkAwbreyGlenMnumCounts - Error looking up past tee times - ERR: " + exc.toString());
                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }

                try {

                    // Check past tee times from this year for this member number
                    pstmt = con.prepareStatement("SELECT mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 WHERE yy = ? AND teecurr_id <> ? AND (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?)");
                    pstmt.clearParameters();
                    pstmt.setInt(1, calYear);
                    pstmt.setInt(2, slotParms.teecurr_id);
                    pstmt.setString(3, mNumA[i]);
                    pstmt.setString(4, mNumA[i]);
                    pstmt.setString(5, mNumA[i]);
                    pstmt.setString(6, mNumA[i]);
                    pstmt.setString(7, mNumA[i]);

                    rs = pstmt.executeQuery();

                    while (rs.next()) {
                        if (rs.getString("mNum1").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getString("mNum2").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getString("mNum3").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getString("mNum4").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                        if (rs.getString("mNum5").equalsIgnoreCase(mNumA[i])) {
                            roundCount++;
                        }
                    }

                } catch (Exception exc) {
                    Utilities.logError("verifyCustom.checkAwbreyGlenMnumCounts - Error looking up current tee times - ERR: " + exc.toString());
                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }              
               
                if (roundCount > maxRounds) {
                    slotParms.player = playerA[i];
                    error = true;
                }
            }
        }
        
        return error;
    }
    
    /** 
     * Generates a fake lottery assignment for all registered members for this event, so that the event round will be factored into their normal lottery weighting on a later date.
     * @param event_id - ID of the event to process
     * @param con - Connection to club database
     */
    public static int generateLottAssignsFromEvent(String user, int event_id, Connection con) {
        
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        
        String event_name = "";
        String courseName = "";
        String username = "";
        String player = "";
        
        long date = 0;
        
        int time = 0;
        int count = 0;
        int total = 0;
        
        //INSERT INTO lassigns5 (username, lname, DATE) SELECT 'SAger', 'Email Test', 20130928 FROM DUAL WHERE NOT EXISTS (SELECT username FROM lassigns5 WHERE username = 'SAger' AND lname = 'Email Test' AND DATE = 20130928)
        
        try {
            
            pstmt = con.prepareStatement("SELECT name, date, courseName, ((act_hr * 100) + act_min) as act_time FROM events2b WHERE event_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, event_id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                
                event_name = rs.getString("name");
                date = rs.getLong("date");
                courseName = rs.getString("courseName");
                time = rs.getInt("act_time");
            }
            
        } catch (Exception exc) {
            Utilities.logError("verifyCustom.generateLottAssignsFromEvent - Error looking up event details - ERR: " + exc.toString());
        } finally {
            try { rs.close(); }
            catch (Exception ignore) {}
            
            try { pstmt.close(); }
            catch (Exception ignore) {}
        }
        
        try {
            
            pstmt = con.prepareStatement("SELECT * FROM evntsup2b WHERE name = ? AND inactive = 0 AND wait = 0");
            pstmt.clearParameters();
            pstmt.setString(1, event_name);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                
                if (!rs.getString("username1").equals("")) {
                
                    count = 0;
                    username = rs.getString("username1");
                    player = rs.getString("player1");
                    
                    // Only insert a new entry if the same entry doesn't already exist
                    pstmt2 = con.prepareStatement("INSERT INTO lassigns5 (username, lname, date, mins, time_req, course_req, time_assign, course_assign, weight, grp_weight, lreq_id) "
                            + "SELECT ?, ?, ?, 0, ?, ?, ?, ?, 0, 0, 0 "
                            + "FROM DUAL "
                            + "WHERE NOT EXISTS (SELECT username FROM lassigns5 WHERE username = ? AND lname = ? AND date = ?)");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, username);
                    pstmt2.setString(2, event_name);
                    pstmt2.setLong(3, date);
                    pstmt2.setInt(4, time);
                    pstmt2.setString(5, courseName);
                    pstmt2.setInt(6, time);
                    pstmt2.setString(7, courseName);
                    
                    pstmt2.setString(8, username);
                    pstmt2.setString(9, event_name);
                    pstmt2.setLong(10, date);
                    
                    count = pstmt2.executeUpdate();
                    
                    // if successfully added, insert event log entry
                    if (count > 0) {
                        total++;
                        Utilities.createEventLogEntry(user, event_id, rs.getInt("id"), "MISC", "Lottery assignment generated for " + player, 0, con);
                    }
                    
                    try { pstmt2.close(); }
                    catch (Exception ignore) {}
                }
                
                if (!rs.getString("username2").equals("")) {
                
                    count = 0;
                    username = rs.getString("username2");
                    player = rs.getString("player2");
                    
                    // Only insert a new entry if the same entry doesn't already exist
                    pstmt2 = con.prepareStatement("INSERT INTO lassigns5 (username, lname, date, mins, time_req, course_req, time_assign, course_assign, weight, grp_weight, lreq_id) "
                            + "SELECT ?, ?, ?, 0, ?, ?, ?, ?, 0, 0, 0 "
                            + "FROM DUAL "
                            + "WHERE NOT EXISTS (SELECT username FROM lassigns5 WHERE username = ? AND lname = ? AND date = ?)");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, username);
                    pstmt2.setString(2, event_name);
                    pstmt2.setLong(3, date);
                    pstmt2.setInt(4, time);
                    pstmt2.setString(5, courseName);
                    pstmt2.setInt(6, time);
                    pstmt2.setString(7, courseName);
                    
                    pstmt2.setString(8, username);
                    pstmt2.setString(9, event_name);
                    pstmt2.setLong(10, date);
                    
                    count = pstmt2.executeUpdate();
                    
                    // if successfully added, insert event log entry
                    if (count > 0) {
                        total++;
                        Utilities.createEventLogEntry(user, event_id, rs.getInt("id"), "MISC", "Lottery assignment generated for " + player, 0, con);
                    }
                    
                    try { pstmt2.close(); }
                    catch (Exception ignore) {}
                }
                
                if (!rs.getString("username3").equals("")) {
                
                    count = 0;
                    username = rs.getString("username3");
                    player = rs.getString("player3");
                    
                    // Only insert a new entry if the same entry doesn't already exist
                    pstmt2 = con.prepareStatement("INSERT INTO lassigns5 (username, lname, date, mins, time_req, course_req, time_assign, course_assign, weight, grp_weight, lreq_id) "
                            + "SELECT ?, ?, ?, 0, ?, ?, ?, ?, 0, 0, 0 "
                            + "FROM DUAL "
                            + "WHERE NOT EXISTS (SELECT username FROM lassigns5 WHERE username = ? AND lname = ? AND date = ?)");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, username);
                    pstmt2.setString(2, event_name);
                    pstmt2.setLong(3, date);
                    pstmt2.setInt(4, time);
                    pstmt2.setString(5, courseName);
                    pstmt2.setInt(6, time);
                    pstmt2.setString(7, courseName);
                    
                    pstmt2.setString(8, username);
                    pstmt2.setString(9, event_name);
                    pstmt2.setLong(10, date);
                    
                    count = pstmt2.executeUpdate();
                    
                    // if successfully added, insert event log entry
                    if (count > 0) {
                        total++;
                        Utilities.createEventLogEntry(user, event_id, rs.getInt("id"), "MISC", "Lottery assignment generated for " + player, 0, con);
                    }
                    
                    try { pstmt2.close(); }
                    catch (Exception ignore) {}
                }
                
                if (!rs.getString("username4").equals("")) {
                
                    count = 0;
                    username = rs.getString("username4");
                    player = rs.getString("player4");
                    
                    // Only insert a new entry if the same entry doesn't already exist
                    pstmt2 = con.prepareStatement("INSERT INTO lassigns5 (username, lname, date, mins, time_req, course_req, time_assign, course_assign, weight, grp_weight, lreq_id) "
                            + "SELECT ?, ?, ?, 0, ?, ?, ?, ?, 0, 0, 0 "
                            + "FROM DUAL "
                            + "WHERE NOT EXISTS (SELECT username FROM lassigns5 WHERE username = ? AND lname = ? AND date = ?)");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, username);
                    pstmt2.setString(2, event_name);
                    pstmt2.setLong(3, date);
                    pstmt2.setInt(4, time);
                    pstmt2.setString(5, courseName);
                    pstmt2.setInt(6, time);
                    pstmt2.setString(7, courseName);
                    
                    pstmt2.setString(8, username);
                    pstmt2.setString(9, event_name);
                    pstmt2.setLong(10, date);
                    
                    count = pstmt2.executeUpdate();
                    
                    // if successfully added, insert event log entry
                    if (count > 0) {
                        total++;
                        Utilities.createEventLogEntry(user, event_id, rs.getInt("id"), "MISC", "Lottery assignment generated for " + player, 0, con);
                    }
                    
                    try { pstmt2.close(); }
                    catch (Exception ignore) {}
                }
                
                if (!rs.getString("username5").equals("")) {
                
                    count = 0;
                    username = rs.getString("username5");
                    player = rs.getString("player5");
                    
                    // Only insert a new entry if the same entry doesn't already exist
                    pstmt2 = con.prepareStatement("INSERT INTO lassigns5 (username, lname, date, mins, time_req, course_req, time_assign, course_assign, weight, grp_weight, lreq_id) "
                            + "SELECT ?, ?, ?, 0, ?, ?, ?, ?, 0, 0, 0 "
                            + "FROM DUAL "
                            + "WHERE NOT EXISTS (SELECT username FROM lassigns5 WHERE username = ? AND lname = ? AND date = ?)");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, username);
                    pstmt2.setString(2, event_name);
                    pstmt2.setLong(3, date);
                    pstmt2.setInt(4, time);
                    pstmt2.setString(5, courseName);
                    pstmt2.setInt(6, time);
                    pstmt2.setString(7, courseName);
                    
                    pstmt2.setString(8, username);
                    pstmt2.setString(9, event_name);
                    pstmt2.setLong(10, date);
                    
                    count = pstmt2.executeUpdate();
                    
                    // if successfully added, insert event log entry
                    if (count > 0) {
                        total++;
                        Utilities.createEventLogEntry(user, event_id, rs.getInt("id"), "MISC", "Lottery assignment generated for " + player, 0, con);
                    }
                }
            }
            
        } catch (Exception exc) {
            Utilities.logError("verifyCustom.generateLottAssignsFromEvent - Error generating lottery assignments - ERR: " + exc.toString());
        } finally {
            try { rs.close(); }
            catch (Exception ignore) {}
            
            try { pstmt.close(); }
            catch (Exception ignore) {}
            
            try { pstmt2.close(); }
            catch (Exception ignore) {}
        }
        
        return total;
    }
    
    public static int checkPhilCricketGolfInWaitingRounds(String mship_to_check, parmSlot slotParms, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int round_count = 0;
        
        String[] playerA = new String[5];
        String[] mshipA = new String[5];
        String[] mNumA = new String[5];
        String[] userA = new String[5];

        mshipA[0] = slotParms.mship1;
        mshipA[1] = slotParms.mship2;
        mshipA[2] = slotParms.mship3;
        mshipA[3] = slotParms.mship4;
        mshipA[4] = slotParms.mship5;
        
        // Count number of these mships in current time
        for (int i = 0; i < 5; i++) {
            if (mshipA[i].equalsIgnoreCase(mship_to_check)) {
                round_count++;
            }
        }
        
        try {
                    
            pstmt = con.prepareStatement("SELECT count(*) AS round_count FROM ("
                    + "(SELECT player1 AS player FROM teecurr2 t INNER JOIN member2b m ON t.username1 = m.username WHERE username1 <> '' AND m_ship = ? AND date = ? AND courseName = ? AND teecurr_id <> ?)"
                    + "UNION ALL "
                    + "(SELECT player2 AS player FROM teecurr2 t INNER JOIN member2b m ON t.username2 = m.username WHERE username2 <> '' AND m_ship = ? AND date = ? AND courseName = ? AND teecurr_id <> ?)"
                    + "UNION ALL "
                    + "(SELECT player3 AS player FROM teecurr2 t INNER JOIN member2b m ON t.username3 = m.username WHERE username3 <> '' AND m_ship = ? AND date = ? AND courseName = ? AND teecurr_id <> ?)"
                    + "UNION ALL "
                    + "(SELECT player4 AS player FROM teecurr2 t INNER JOIN member2b m ON t.username4 = m.username WHERE username4 <> '' AND m_ship = ? AND date = ? AND courseName = ? AND teecurr_id <> ?)"
                    + "UNION ALL "
                    + "(SELECT player5 AS player FROM teecurr2 t INNER JOIN member2b m ON t.username5 = m.username WHERE username5 <> '' AND m_ship = ? AND date = ? AND courseName = ? AND teecurr_id <> ?)"
                    + ") AS result");
            
            pstmt.clearParameters();
            pstmt.setString(1, mship_to_check);
            pstmt.setLong(2, slotParms.date);
            pstmt.setString(3, slotParms.course);
            pstmt.setInt(4, slotParms.teecurr_id);
            pstmt.setString(5, mship_to_check);
            pstmt.setLong(6, slotParms.date);
            pstmt.setString(7, slotParms.course);
            pstmt.setInt(8, slotParms.teecurr_id);
            pstmt.setString(9, mship_to_check);
            pstmt.setLong(10, slotParms.date);
            pstmt.setString(11, slotParms.course);
            pstmt.setInt(12, slotParms.teecurr_id);
            pstmt.setString(13, mship_to_check);
            pstmt.setLong(14, slotParms.date);
            pstmt.setString(15, slotParms.course);
            pstmt.setInt(16, slotParms.teecurr_id);
            pstmt.setString(17, mship_to_check);
            pstmt.setLong(18, slotParms.date);
            pstmt.setString(19, slotParms.course);
            pstmt.setInt(20, slotParms.teecurr_id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                round_count += rs.getInt("round_count");
            }
            
            
        } catch (Exception exc) {
            Utilities.logError("verifyCustom.checkPhilCricketGolfInWaitingRounds - " + slotParms.club + " - Error looking up teecurr2 rounds - ERR: " + exc.toString());
        } finally {
            
            try { rs.close(); }
            catch (Exception ignore) {}
            
            try { pstmt.close(); }
            catch (Exception ignore) {}
            
        }
        
        return round_count;
    }
    
    /**
     * Returns the number of events in the specified category that the specified member is currently signed up for.
     * @param user Username of member in question
     * @param event_category Event category to include all events for
     * @param con Connection to club database
     * @return int - count of events the member is signed up for
     */
    public static int checkEventCategoryCounts(String user, String club, int event_category_id, Connection con) {
        
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        
        int count = 0;
        
        try {
            
            pstmt = con.prepareStatement(""
                    + "SELECT e2b.name "
                    + "FROM event_category_bindings ecb "
                    + "LEFT OUTER JOIN event_categories ec ON ecb.category_id = ec.category_id "
                    + "LEFT OUTER JOIN events2b e2b ON e2b.event_id = ecb.event_id "
                    + "WHERE ec.category_id = ? and inactive = 0;");
            pstmt.clearParameters();
            pstmt.setInt(1, event_category_id);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
            
                try {

                    pstmt2 = con.prepareStatement("SELECT id FROM evntsup2b WHERE name = ? AND inactive = 0 AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, rs.getString("e2b.name"));
                    pstmt2.setString(2, user);
                    pstmt2.setString(3, user);
                    pstmt2.setString(4, user);
                    pstmt2.setString(5, user);
                    pstmt2.setString(6, user);
                    
                    rs2 = pstmt2.executeQuery();
                    
                    if (rs2.next()) {
                        count++;
                    }
                                        
                } catch (Exception exc) {
                    Utilities.logError("verifyCustom.checkEventCategoryCounts - " + club + " - event signups for member (" + user + ") for event (" + rs.getString("e2b.name") + " - ERR: " + exc.toString());
                } finally {

                    try { rs2.close(); }
                    catch (Exception ignore) {}

                    try { pstmt2.close(); }
                    catch (Exception ignore) {}
                }
            }

        } catch (Exception exc) {
            Utilities.logError("verifyCustom.checkEventCategoryCounts - " + club + " - Error looking up events under category_id (" + event_category_id + ") - ERR: " + exc.toString());
        } finally {
            
            try { rs.close(); }
            catch (Exception ignore) {}
            
            try { pstmt.close(); }
            catch (Exception ignore) {}
        }
        
        return count;
    }
        
}  // end of verifyCustom class

