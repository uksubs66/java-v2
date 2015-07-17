/***************************************************************************************
 *   Common_sync:  This servlet will look in the v_/roster folder for a text file
 *                 that contains member records for the club being processed.  The
 *                 member records will be compared against those already in the system.
 *
 *
 *   called:  rosterSync - by TimerSync.java (timer mechanism)
 *            Support_sync - to process a single club
 *
 *
 *   created:  2/22/05 Bob P.
 *
 *
 *   updated:
 *
 *       07/30/10   MF - Edina CC (edina) - updated mship processing
 *       07/16/10   CE - Baltimore CC (baltimore) - Update to mship/mtype processing
 *       07/15/10   Changed how clubSync() passes the club name for mfirst clubs so we can map multiple clubs to a single roster easier
 *       07/14/10   FS - Colorado Springs CC (coloradospringscountryclub) - Add zeroes to posids that are less than length 8
 *       07/12/10   CE - Cherry Creek CC (cherrycreek) - Commented out sync processing.  Moved to Jonas some time ago and sync is not doing anything.
 *       07/07/10   MF - Fix to catch duplicate username exceptions with useWebidQuery option.  Was causing sync to bomb out prematurely.
 *       07/01/10   MF - Hideaway Beach Club (hideawaybeachclub) - Change to mship processing: Set mship to 'Renter' if mNum starts with 7.
 *       06/24/10   CT - Added Rolling Hills CC - CO (rhillscc) to clubTecSync()
 *       06/24/10   Added clubTecSync() method to allow syncing with ClubTec
 *       06/23/10   MF - Added Edina 2010 (edina2010) to mFirstSync().  Uses same roster as edina
 *       06/23/10   NS - Bellevue CC (bellevuecc) - Update to mship processing.
 *       06/22/10   CE - CC of Virginia (virginiacc) - Update to mtype processing to handle Honorary mtypes.
 *       06/18/10   MF - Portland GC (portlandgc) - Add mship processing for "Senior" mship
 *       06/17/10   CE - Members will now default to getting set active if they sync.
 *       06/16/10   MF - Oahu CC (oahucc) - Add handling for the de_los_Reyes family.  Last name consists of 3 words.
 *       06/15/10   MF - Philly Cricket Club (philcricket) - Change to differentiate usernames based on relationshp value
 *       06/11/10   FS - Temp disable of sync for Colorado Springs CC
 *       06/09/10   MF - Added Hideaway Beach Club (hideawaybeachclub) to mFirstSync()
 *       06/09/10   MF - Lakewood Ranch CC (lakewoodranch) - Change to mship processing for 'SMR Members and Staff' mship
 *       06/09/10   MF - Castle Pines (castlepines) - Do not title case last names
 *       06/04/10   MF - Added Quechee Club - Tennis (quecheeclubtennis) to mFirstSync().  Uses same roster as quecheeclub
 *       06/01/10   All - trim email addresses before we verify them.  Also, clear bounce flags if email address changes.
 *       05/26/10   MF - Tamarak - change the way we extract the mNum from the memid (they now have -1 extensions rather than an alpha).
 *       05/25/10   All - moved setRSind (set Roster Sync flag) calls to end of methods so we only set this flag if the file was processed (if found).
 *       05/24/10   MF - Philly Cricket Club (philcricket) - Update to mship processing.
 *       05/24/10   MF - Added The Club at Mediterra (mediterra) to mFirstSync().
 *       05/24/10   MF - Commented out sync processing for Bald Peak (baldpeak).  No longer with MF.
 *       05/19/10   CE - Wee Burn CC (weeburn) - Add 'Dependent Child' processing to rsync
 *       05/19/10   MF - Peninsula Club (peninsula) - Added 'Tennis' to list of mships not to filter
 *       05/10/10   FS - Ocean Reef (oceanreef) - Updated mship processing
 *       05/07/10   FS - Colorado Springs CC (coloradospringscountryclub) - Added mship filtering
 *       05/03/10   MF - Turner Hill (turnerhill) - Added gender processing
 *       05/03/10   CE - Added Sea Pines CC (seapines) to ceSync() - They have decided to not use Sync for now.  Commented out in case they chose to sync later
 *       04/29/10   CE - Philly Cricket Club (philcricket) - Move philcricket processing from mFirstSync() to ceSync()
 *       04/19/10   MF - Philly Cricket Club (philcricket) - Update to mship and mtype processing.
 *       04/19/10   CE - Baltimore CC (baltimore) - Update to mship/mtype processing
 *       04/15/10   CE - International CC (internationalcc) - No longer filtering 'Non Resident' mship
 *       04/14/10   MF - Philly Cricket Club (philcricket) - Do not title case first names
 *       04/13/10   FS - Added Colorado Springs CC (coloradospringscountryclub) to flexSync()
 *       04/13/10   FS - Updated flexSync() to make it simpler to add additional clubs
 *       04/12/10   MF - Added Turner Hill (turnerhill) to mFirstSync()
 *       04/09/10   CE - International CC (internationalcc) - Changes to mship processing, filter out additional types.
 *       04/08/10   CE - Congressional - do not add a spouse if their mship or primary member is not found.
 *       04/07/10   MF - Castle Pines - add.  they switched from CE to MF.
 *       04/02/10   FS - Four Bridges (fourbridges) - re-enable code to set those who have synced before but didn't sync this time to inactive
 *       03/31/10   MF - Oakley CC (oakleycountryclub) - If mtype field is populated, use that for the mship, otherwise use mship field
 *       03/29/10   MF - Navesink CC (navesinkcc) - Fix so that mship value gets set properly when mtype doesn't start with "Member Type"
 *       03/29/10   CE - Orchid Island (orchidisland) - Updated mtype processing to not default all primary members to Adult Male
 *       03/26/10   MF - Navesink CC (navesinkcc) - Do not change mtype if old mtype is "Primary Male GP"
 *       03/25/10   CE - Point Lake (pointlake) - Deactivate CE sync
 *       03/18/10   MF - Edina CC (edina) - Changes to mship processing. Clergy and Honorary Social map to Social
 *       03/17/10   MF - Philly Cricket Club (philcricket) - No longer skipping blank mships
 *       03/16/10   CE - Orchid Island (orchidisland) - Allow member numbers to be updated.
 *       03/10/10   MF - Added Happy Hollow Club (happyhollowclub) to mFirstSync()
 *       03/10/10   CE - Orchid Island (orchidisland) - Added 'Invitational Golf' to mship grabbing query
 *       03/08/10   MF - Longue Vue Club (longuevueclub) - Activated roster sync processing and made tweaks to work with new roster file
 *       03/03/10   MF - Edina CC (edina) - Changes to mship processing.  Let Pool/Tennis and Social come through as they are, make everything else Golf
 *       03/03/10   CE - Point Lake (pointlake) - Changes to mship processing.  Filter out 'CSH' mship.
 *       03/03/10   MF - Added The Edison Club (edisonclub) to mFirstSync() (one time sync, commented out for possible future use)
 *       02/26/10   MF - Added date of birth junior check for Tahoe Donner (non-roster-sync club)
 *       02/25/10   MF - Added Longue Vue Club (longuevueclub) to mFirstSync()
 *       02/23/10   MF - Added Black Rock CC (blackrockcountryclub) to mFirstSync()
 *       02/23/10   MF - Added Oakley CC (oakleycountryclub) to mFirstSync()
 *       02/11/10   CE - Orchid Island (orchidisland) - Added 'Invitational Golf' to mship processing
 *       02/10/10   FS - Ocean Reef (oceanreef) - Update to mtype processing.
 *       02/05/10   CE - International CC (internationalcc) - Changes to mship processing, no longer sync 'parties'
 *       02/04/10   MF - Edina CC (edina) - Changes to mship processing to allow additional members access for FlxRez
 *       01/27/10   CE - Mission Viejo CC (missionviejo) - Temporarily disable sync processing
 *       01/26/10   CE - International CC (internationalcc) - Use the Primary's existing mship from member2b for Spouses/Dependents (case 1704).
 *       01/19/10   MF - Philly Cricket Club (philcricket) - Skip 'Leave of Absence' mtype
 *       01/13/10   MF - Philly Cricket Club (philcricket) - Update to mship and mtype processing.  Use mtype for mship now.
 *       12/31/09   CE - TPC Wakefield Plantation (tpcwakefieldplantation) - Ignore the active_indicator field temporarily
 *       12/31/09   MF - FountainGrove Golf (fountaingrovegolf) - Changes to POSID processing
 *       12/30/09   MF - DC Ranch (ccdcranch) - Fixed typo in mship processing.
 *       12/29/09   FS - Ocean Reef (oceanreef) - Update mship processing with new category.  Remove custom to not update mtypes
 *       12/21/09   MF - Added CC at DC Ranch (ccdcranch) to mFirstSync()
 *       12/16/09   MF - Added Morgan Run (morganrun) to mFirstSync()
 *       12/15/09   MF - Don't send copies of MF report emails to support@foretees.com.  Will turn on again if needed.
 *       12/11/09   MF - FountainGrove Golf (fountaingrovegolf) - Changes to POSID processing
 *       12/11/09   CE - Added Cherry Hills CC (cherryhills) to ceSync()
 *       12/08/09   MF - Austin CC (austincountryclub) - Disabled roster sync due to leaving ForeTees
 *       12/07/09   FS - Ocean Reef - do not update member types.
 *       12/03/09   MF - Portland GC (portlandgc) - Change to mship processing
 *       12/01/09   Updated updBuddy method call to the new updPartner method
 *       12/01/09   MF - Change their alert email address.
 *       12/01/09   MF - FountainGrove Golf (fountaingrovegolf) - Changes to POSID processing
 *       11/09/09   MF - Austin CC (austincountryclub) - Use webid for memid instead of mNum.  Changes to mship processing.
 *       11/04/09   MF - Troon CC (trooncc) - Changes to mship processing
 *       11/04/09   MF - Imperial GC (imperialgc) - Bypass dup-user flagging if name username and mnum have changed, but the fname/mi/lname match up and update
 *                       the record instead of skipping.
 *       10/27/09   FS - Ocean Reef (oceanreef) - Update mship processing, remove custom processing to not update mships for 'Charter' members
 *       10/26/09   MF - The Reserve Club (thereserveclub) - Do not titlecase first names either
 *       10/22/09   MF - The Reserve Club (thereserveclub) - Do not titlecase last names
 *       10/21/09   MF - Troon CC (trooncc) - Changes to mship processing
 *       10/19/09   MF - Robert Trent Jones GC (rtjgc) - Fixes to roster sync processing
 *       10/16/09   CE - Colleton River Club (colletonriverclub) - No longer update email2 field
 *       10/16/09   MF - Added Robert Trent Jones GC (rtjgc) to mFirstSync(), commented out old sync processing from ceSync()
 *       10/13/09   MF - Druid Hills GC (dhgc) - No longer stripping alpha characters from end of member number
 *       10/12/09   FS - Ocean Reef (oceanreef) - Update to mship processing, and no longer update membership type for 'Charter' members
 *       10/12/09   MF - Wilmington CC (wilmington) - Added 'Clerical' to mship processing
 *       10/09/09   CE - Baltimore (baltimore) - Mship processing fixes
 *       10/09/09   CE - Orchid Island (orchidisland) - No longer filter "Employee" membership type
 *       10/08/09   MF - Druid Hills GC (dhgc) - Filter "House" membership type from sync
 *       10/08/09   FS - Ocean Reef (oceanreef) - Filter "Dependent" member type from sync
 *       10/07/09   MF - Druid Hills GC (dhgc) - Strip leading zeroes from member numbers
 *       10/07/09   MF - Tamarack (tamarack) - Reenabled roster sync, updated mship processing
 *       10/06/09   CE - Baltimore (baltimore) - Reactivated Baltimore sync
 *       10/02/09   MF - Tamarack (tamarack) - Temporarily disable roster sync
 *       10/01/09   MF - Added The Reserve Club (thereserveclub) to mFirstSync()
 *        9/30/09   MF - Added Druid Hills GC (dhgc) to mFirstSync()
 *        9/30/09   FS - Added Ocean Reef (oceanreef) to flexSync()
 *        9/30/09   CE - Baltimore - temporarily suspend processing until we verify their new file (mships).
 *        9/24/09   MF - Long Cove (longcove) - Allow duplicate names in roster for members that own two properties at once (do not flag as dup on matching name check)
 *        9/23/09   CE - Baltimore CC (baltimore) - Replaced mship processing with newly updated liste of filters
 *        9/14/09   CE - Congressional CC (congressional) - Add processing for 'AG' mship to processing, now filter 'SG' instead
 *        9/09/09   CE - Colleton River Club (colletonriverclub) - Add mship processing to map 'FULL3' mship to 'FULL'
 *        9/09/09   MF - Palo Alto Hills (paloaltohills) - Don't title-case last names for Palo Alto Hills
 *        9/02/09   MF - FountainGrove Golf (fountaingrovegolf) - Don't update birthdates for FountainGrove
 *        8/26/09   MF - Filter out a specific incorrect email for woodside plantation (paulcross@).
 *        8/19/09   MF - Ballantyne CC (ballantyne) - Added 2 membership type classifications to sync processing
 *        8/06/09   CE - Westchester CC (westchester) - Stop filtering "SENIORHSE" membership type from sync
 *        8/04/09   FS - Four Bridges CC (fourbridges) - Temporarily removed processing where members get set inactive if skipped in the sync
 *        8/03/09   FS - Four Bridges CC (fourbridges) - Add call to cleanRecord4 to avoid blank cells (',,' was not getting changed to ',?,'
 *                       as was necessary to not drop below tokenCount threshhold
 *        7/29/09   NS - Brooklawn CC (brooklawn) - Start updating birthdates again instead of skipping the processing
 *        7/23/09   CE - Baltimore CC (baltimore) - Added missing error log code to report missing/unknown membership types
 *        7/22/09   CE - Baltimore CC (baltimore) - Added check for mship = LM Gent Season Golf in mship processing
 *        7/21/09   CE - International CC (internationalcc) - swap primary and spouse for mNum 1217 (ON HOLD, CURRENTLY COMMENTED OUT)
 *        7/15/09   MF - Sawgrass CC (sawgrass) - Small tweak to mship processing
 *        7/14/09   MF - Tamarack (tamarack) - uncommented roster sync processing so they sync again
 *        7/13/09   MF - Sawgrass CC (sawgrass) - Small tweak to mship processing
 *        7/10/09   MF - Sawgrass CC (sawgrass) - Use mtype field to determine mship, but still filter on certain given mships first
 *        7/09/09   MF - Sawgrass CC (sawgrass) - Added mship filtering for membership types starting with 'House'
 *        7/08/09   MF - St Clair CC (stclaircc) - Modify dependent processing to handle additional suffix letter
 *        6/29/09   MF - Baltusrol GC (baltusrolgc) - Change to mship processing for handling Junior mships
 *        6/25/09   MF - Modify mship processing for St. Claire CC (stclaircc) - add 'Emeritus'
 *        6/18/09   MF - Sawgrass CC (sawgrass) - Added mship filtering
 *        6/16/09   CE - Royal Oaks CC, Houston (royaloakscc), no longer with CE, commented out roster sync for now.
 *        6/12/09   MF - The Oaks Club - Enhanced mship processing to accomodate additional mships
 *        6/12/09   MF - Changed 'sawgrasscountryclub' to 'sawgrass'
 *        6/11/09   MF - Added Sawgrass CC (sawgrasscountryclub) to mFirstSync()
 *        6/08/09   NS - Additional mtype processing for Green Acres CC
 *        6/05/09   CE - Allow mNum updates and member types of "Dependent" for Oak Hill CC
 *        6/04/09   NS - Added Green Acres CC (greenacrescountryclub) to northstarSync()
 *        5/18/09   MF - Remove temporary fix for Brookhaven
 *        5/15/09   MF - Temporarily disable Tamarack roster sync while missing mships in roster are resolved.
 *        5/15/09   MF - Temporary fix for Brookhaven member to not be set inactive while fix in the work
 *        5/15/09   CE - Allow member numbers to update for Virginia CC
 *        5/15/09   TPC - Change username processing to use webid for username instead of mnum (TPC Potomac only at the moment)
 *        5/14/09   CE - Re-enable International CC sync
 *        5/13/09   CE - Temporarily disable International CC while they fix their membership types from being all "member"
 *        5/11/09   TPC - Change username processing for Sugarloaf to use letter naming scheme instead of numbers.  Allow username changes.
 *        5/07/09   MF - The Country Club - Added dependent processing
 *        5/07/09   MF - Quechee Club - Changed posid processing to add a 0 or 1 based on primary/spouse
 *        5/07/09   CE - Added missing error logging to TPC clubs
 *        5/06/09   Brooklawn - accept email addresses from NorthStar - oever-write our emails.
 *        4/24/09   MF - Clubcorp - Changed to skip ALL members with missing gender
 *        4/23/09   Northstar - Change mNum processing for Brooklawn
 *        4/23/09   CE - If we receive a blank email for any tpc club, allow the blank email to be applied as their "new" email, blanking their email slot
 *        4/22/09   MF - Change mship & dependent processing for Navesink CC
 *        4/21/09   MF - Change mship processing for Brookhaven Club (again!)
 *        4/17/09   MF - Change mship processing for Brookhaven Club
 *        4/17/09   MF - Don't title case last names for Pelican Marsh GC
 *        4/17/09   CE - TPC Potomac - Format change for spouse/dep usernames, allow sync to update usernames for tpc potomac
 *        4/14/09   MF - Added Treesdale G&CC (treesdalegolf)(ClubCorp) to mFirstSync()
 *        4/14/09   MF - Added Austin Country Club (austincountryclub) to mFirstSync()
 *        4/07/09   CE - TPC Southwind - Format change for spouse/dep usernames, allow sync to update usernames for tpc southwind
 *        4/02/09   Hyperion - no roster sync, but check birth dates for mtype changes (case # 1633).
 *        4/02/09   CE - update member numbers for Mission Viejo (missionviejo)
 *        4/01/09   CE - Added Mission Viejo (missionviejo) to ceSync()
 *        4/01/09   TPC - custom mship mapping for tpcboston
 *        3/30/09   Brooklawn - Skip dependents of age 24 and older
 *        3/17/09   TPC - Additional tweaks for various TPC clubs
 *        3/16/09   TPC - update the processing for all TPC's to reflect the roster file changes.
 *        3/11/09   MF - Modify mship processing for Pelican Marsh GC (pmarshgc)
 *        3/11/09   MF - Modify mship processing for St. Claire CC (stclaircc)
 *        3/10/09   TPC - All - in ceSync common processing allow member numbers to change.
 *        3/10/09   Bracketts Crossing - strip quotes from all fields.
 *        3/09/09   TPC - update the processing for all TPC's to reflect the roster file changes.
 *        2/26/09   MF - Modify mship processing for St. Claire CC (stclaircc)
 *        2/23/09   Add Dream World Sync for Bracketts Crossing.
 *        2/23/09   MF - Change to mship processing for Charlotte CC
 *        2/20/09   CE - Uncommented Claremont so they are syncing again
 *        2/20/09   MF - Change to mship processing for Charlotte CC
 *        2/19/09   MF - Don't title case last names for Troon CC
 *        2/18/09   Mount Vernon - no roster sync, but check birth dates for mtype changes (case # 1616).
 *        2/11/09   CE - Claremont CC commented out while they fix their roster file with CE.
 *        2/11/09   CE - Allow mNum updating for International CC (internationalcc).
 *        1/28/09   MF - Added additional mship filtering for Fountaingrove Golf (fountaingrovegolf)
 *        1/22/09   MF - Pradera (pradera) - Added a new membership type to the processing.
 *        1/12/09   MF - Added Fountaingrove GAC to mFirstSync()
 *        1/06/09   CE - Added titlecase to mship for tpctc.
 *       12/30/08   CE - Added TPC Twin Cities (tpctc) to ceSync()
 *       12/19/08   MF - Temporarily filter out a specific incorrect email for woodside plantation
 *       12/15/08   CE - Added TPC San Francisco Bay (tpcsfbay) to ceSync()
 *       12/04/08   CE - Added TPC Craig Ranch (tpccraigranch) to ceSync()
 *       11/24/08   MF - Added Timarron CC (timarroncc)(ClubCorp) to mFirstSync()
 *       11/24/08   MF - Do not change mtype for Palo Alto Hills (paloaltohills)
 *       11/21/08   CE - Added Royal Oaks CC - Hourson (royaloakscc) to ceSync()
 *       11/17/08   MF - Added Woodside Plantation CC (woodsideplantation)(ClubCorp) to mFirstSync()
 *       11/17/08   MF - Added Palo Alto Hills (paloaltohills) to mFirstSync()
 *       11/14/08   MF - Commented out processing for Edgewood
 *       11/11/08   MF - Added Ozaukee CC (ozaukeecc) to mFirstSync()
 *       11/05/08   MF - Modified mship processing for Quechee Club (quecheeclub)
 *       10/31/08   CE - Added TPC Boston (tpcboston) to ceSync()
 *       10/31/08   CE - Added TPC Jasna Polana (tpcjasnapolana) to ceSync()
 *       10/30/08   CE - Changes to mtype specifications for new TPC clubs
 *       10/28/08   CE - Modifications to TPC Southwind processing
 *       10/28/08   Bent Tree CC - changed the mship processing to weed out old values and translate the new values.
 *       10/28/08   CE - Added TPC River's Bend (tpcriversbend) to ceSync()
 *       10/28/08   CE - Added TPC Wakefield Plantation (tpcwakefieldplantation) to ceSync()
 *       10/28/08   CE - Added TPC Sugarloaf (tpcsugarloaf) to ceSync()
 *       10/28/08   CE - Added TPC River Highlands (tpcriverhighlands) to ceSync()
 *       10/28/08   CE - Added TPC Southwind (tpcsouthwind) to ceSync()
 *       10/24/08   MF - modified processing for The Oaks Club
 *       10/20/08   MF - do not change first names for Charlotte CC & additional mship processing for Charlotte CC added.
 *       10/14/08   MF - remove Out Door CC - switched to Jonas.
 *       10/10/08   MF - Admiral's Cove (admiralscove) added to mFirstSync()
 *        9/26/08   MF - The Oaks Club (theoaksclub) added to mFirstSync()
 *        9/23/08   Modified mship processing for silverlakecc
 *        9/23/08   MF - Quechee Club (quecheeclub) added to mFirstSync()
 *        9/09/08   MF - Do NOT change mi for ClubCorp clubs, mFirstSync is now passed a clubcorp boolean argument
 *        9/05/08   Portland GC (MF) - do not titlecase lname
 *        9/05/08   Wellesley (MF) - do not titlecase lname
 *        8/25/08   Update to gleneaglesclub processing in mFirstSync
 *        8/12/08   Update to portlandgc processing in mFirstSync
 *        8/11/08   MF - Portland GC (portlandgc) added to mFirstSync
 *        8/11/08   Mesa Verde CC - Never change emails with sync - added for email2.
 *        8/04/08   useWebid = true for silverlakecc
 *        7/24/08   Change to member type classifying for Brantford
 *        7/23/08   Close the connnection in rosterSync after processing each club.  In clubSync set failed=true
 *                  if club name not found in ClubCorp conversion table so we don't call MFirstSync for all clubs.
 *                  This was causing rsync to be set in club5 for all clubs.
 *                  Clear the rsync flag in club5 for ALL clubs in rosterSync and let the existing methods set
 *                  rsync for any club that is actually processed.
 *        7/23/08   Added posid setting code to Weeburn
 *        7/23/08   Added handling for a new Blue Hill membership type
 *        7/22/08   MF - Gleneagles CC (gleneaglesclub) added to mFirstSync
 *        7/18/08   MF - Snoqualmie Ridge (snoqualmieridge) added to mFirstSync
 *        7/18/08   MF - Palo Alto Hills G&CC (paloaltohills) added to mFirstSync() (Commented out for now)
 *        7/18/08   Berkeley Hall will be syncing again
 *        7/17/08   Stonebridge Ranch CC - Changes in mship processing
 *        7/17/08   Mesa Verde CC - Never change emails with sync
 *        7/16/08   MF - Charlotte CC (charlottecc) added to mFirstSync(), small Stonebridge Rance changes
 *        7/15/08   MF - Stonebridge Ranch CC (stonebridgeranchcc) added mFirstSync()
 *        7/15/08   Do NOT sync if fname or lname is 'Admin' or 'admin', do NOT sync if memid is blank once it reaches common processing
 *        7/15/08   Pradera (MF) - do not titlecase lname and check for 2-part lname and suffix (both).
 *        7/11/08   Fixes for Navesink CC, was filtering empty mship field, their mship is in the mtype field
 *        7/10/08   MF - Brookhaven CC (brookhavenclub) added to mFirstSync()
 *        7/10/08   MF - Hackberry Creek CC (hackberrycreekcc) added to mFirstSync()
 *        7/10/08   Edgewood - will be switching to Legendary marketing - prepare mNums by stripping leading zeros.
 *        7/09/08   MFirst - check for missing mNum when mNum is required.
 *        7/09/08   Switched ordering on getting of roster files around and removed localhost file checks to reduce processing.  Added check for MFirst to look up clubcorp_## files
 *        7/01/08   Pradera fixed so they're syncing properly
 *        7/01/08   Edina - do not change the bag storage numbers.
 *        6/26/08   Changed Saucon Valley CC lname processing, do not change lname if old lname ends with '_*'
 *        6/25/08   Edina uncommented, they will now be syncing again
 *        6/24/08   Changes to roccdallas
 *        6/20/08   St. Clair - seperated Associate Golf and Limited Golf mships (was Assoc/Ltd before)
 *        6/18/08   Edina commented out, fix for skipping old data (esp bag nums for Edina) when dupuser found
 *        6/18/08   St. Clair - adjust some mship types so we get them all.
 *        6/17/08   Baltusrol - finish the processing - mships and fnames.
 *        6/16/08   Don't title case last name for pattersonclub and baldpeak
 *        6/13/08   MF - Royal Oaks CC - Dallas added to mFirstSync(), club-specific error reporting added for ceSync()
 *        6/12/08   Fixed gender trimming issue with ClubCorp rosters, fixed error log issues and changed how errors are stored (ArrayList instead of single String)
 *        6/11/08   mFirstSync() club-specific error reporting completed, general code cleanup
 *        6/10/08   MF - Seville GCC (sevillegcc) added to mFisrtSync()
 *        6/10/08   Numerous changes to error log reporting
 *        6/09/08   MF - Edina CC (edina) added to mFirstSync()
 *        6/06/08   Removed (commented out) Berkeley Hall from ceSync()
 *        6/06/08   Furhter work on error log changes:  core system now implemented and formatting should be complete
 *        6/05/08   Further work on error log changes:  now logging skipped members and using more formatting of output
 *        6/04/08   Added initial processing for outputting errors to file, SystemUtils.logError calls now call SystemUtils.logErrorToFile instead for ce/mf
 *        6/04/08   MF - Silver Lake CC (silverlakecc) processing added in mFirstSync.
 *        6/03/08   Ballantyne (MF) - do not titleCase the last name.
 *        6/03/08   stripDash() method added that removes a dash and any following characters from the end of a string
 *        6/03/08   MF - Pelican Marsh GC (pmarshgc) processing added in mFirstSync.
 *        5/31/08   MF - trim gender before processing record - file for Trophy Club has spaces after gender value.
 *        5/30/08   Added Trophy Club CC to mFirstSync()
 *        5/29/08   Brooklawn - NorthStar - update the email addresses with those received from NS.
 *        5/29/08   Changes for St Clair CC
 *        5/28/08   Changes for Sharon Heights
 *        5/23/08   Added St Clair CC to mFirstSync
 *        5/23/08   Virginia CC - move name titlecase to common area so suffix is not titlecased.
 *        5/20/08   Add Navesink CC to MF
 *        5/09/08   The CC - temporarily disable processing until MF fixes roster issues with ClubTec.
 *        5/08/08   CE - allow mNums to change for Cherry Creek.  For some reason, we don't normally change mNums for CE clubs.
 *        5/08/08   Fix mships for Patterson.
 *        5/03/08   Remove hop Meadow - pro wants to maintain roster himself.
 *        5/02/08   Add Greenwich to CE.
 *        5/02/08   Remove Meridian until CE fixes their problems - wil use primary i/f for now.
 *        4/29/08   Hop Meadow - Added "Senior" mship type
 *        4/25/08   Add Tamarack to MF.
 *        4/23/08   Scarsdale - change mtypes.
 *        4/17/08   Clean up the lname and suffix processing in ceSync - always append the suffix if provided.
 *        4/16/08   Add Patterson Club to MF.
 *        4/16/08   Add Scarsdale to MF.
 *        4/16/08   Add errorLog entry for CE dup users where diff webid & mnum but same full name found
 *        4/15/08   Bald Peak & Sharon Heights - Strip the trailing alpha char from the mNum before saving as posid.
 *        4/14/08   Add The Club at Pradera for MF.
 *        4/09/08   Dorset FC - turn off roster sync.  They don't want their roster to change - Pro will manage it.
 *        4/09/08   ceSync (Oak Hill CC) - check lname for 2-part name (i.e. Van Buren) and merge them if so.
 *        4/07/08   Dorset FC - skip some of the membership classes.
 *        4/03/08   Add Baltusrol CC to MF.
 *        3/28/08   Meridian GC (CE) - do not change the mtype until they fix their roster.
 *        3/28/08   Add Dorset FC to MF.
 *        3/28/08   Remove extra data from fname & lname fields in CE Sync.
 *        3/27/08   Edgewood - Change m_ship to skip (allow social now).
 *        3/25/08   Add Portland CC to MF.
 *        3/19/08   Edgewood - Skip Sport and Dining mship types
 *        3/13/08   Cherry Creek - update mship mapping
 *        3/10/08   CEsync - clean up the last name field - strip off extra names, etc.
 *        3/07/08   International CC - filter some mship types (non-golf members).
 *        3/06/08   Mesa Verde - filter some mship types (non-golf members).
 *        3/05/08   Add The International CC for CE.
 *        2/26/08   Add Mesa Verde CC to MF sync.
 *        2/26/08   Add Bald Peak CC to MF sync.
 *        2/22/08   Add Oak Hill CC to CE sync.
 *        2/15/08   CE - do not attempt to change member's memid/webid when dup names found for Congressional, Virginia CC
 *                       and Algonquin.  They do not want fnames changed and this causes update to fail (no fname).
 *        2/15/08   Clean up - make sure fname and lname are not empty (all clubs/providers) and fix sql when inserting.
 *        2/14/08   Bentwater - if dup name then check mship for highest priority and update accordingly.
 *        2/08/08   Four Bridges (FlexScape) - change all the usernames to prevent contention with mNums.
 *        2/07/08   Algonquin (CE) - strip leading zeros from mNums.
 *        2/07/08   CE - check for 2 birth date formats.
 *        2/06/08   Add Robert Trent Jones to CE.
 *        2/06/08   Awbrey Glen - do not allow employees in roster.
 *        1/28/08   Sharon Heights - add to MF.
 *        1/24/08   Bentwater - add to MF.
 *        1/21/08   Colleton River Club - skips records with mship of "Member" - was mapped to Resident
 *        1/18/08   Oahu - force mtype to Spouse for all surviving spouse mships.
 *        1/15/08   Add Indian Hills CC to the CE sync.
 *        1/13/08   CE - change the webid and/or username if dup name found that matches existing mnum.
 *        1/11/08   Interlachen CC - no roster sync, but check birth dates for mtype changes (case # 1362).
 *        1/05/08   Rosters that provide gender are now saving that data into the member2b table
 *       12/28/07   Added Berkeley Hall to CE.
 *       12/18/07   Add 2 mship types for Imperial Golf.
 *       11/17/07   Add Meridian GC for CE.
 *       11/17/07   Tualatin CC - no roster sync, but check birth dates for mtype changes. Case# 1326
 *       11/04/07   Reset error message text after each call to logerror.
 *       11/02/07   Add Claremont CC for CE (change mship types when CE fixes file!!).
 *       10/22/07   CE Bent Tree - add processing to fix some members that are marked inactive due to mNum changes.
 *       10/10/07   Add Colleton River to CE.
 *       10/10/07   Add Hop Meadow CC to MF.
 *       10/09/07   CE - Orchid Island - change the order that we check mships.
 *       10/07/07   Cherry Creek - shorten a mship type so as to not exceed our max length.
 *       10/07/07   CE - Orchid Island - when setting mtype and memid check the primary value in addition to mship.
 *       10/02/07   MF - do NOT titlecase last names unless they need it (only Blue Hill and Pinery to date).
 *       10/02/07   Congressional - do not change first names.
 *        9/25/07   Add Orchid Island for CE.
 *        9/25/07   Add Imperial GC to MF.
 *        9/21/07   VirginiaCC - use webid to allow for juniors that turn 21 and must start a new mnum.
 *        9/14/07   Add gender and pri_indicator settings for MFirst records.
 *        9/14/07   Add Troon CC for MFirst.
 *        9/12/07   Add Scioto Reserve for Flexscape.
 *        9/12/07   Set the roster sync indicator in the club table for any club that has a RS file.
 *        9/07/07   Allow the emails to change based on what we receive from CE - for all CE clubs.
 *        9/06/07   Add Bent Tree for CE.
 *        8/16/07   Add additional mship for Oahu CC.
 *        8/14/07   Add additional checks for email addresses - make sure email is populated b4 email2 and email2 <> email
 *        8/02/07   Add Ballantyne CC for MFirst.
 *        8/01/07   Add Fairbanks Ranch for MFirst (we need gender in file before we can run this!!).
 *        7/26/07   MFirst - correct how existing member record is located when useWebid is true (Oahu).
 *        7/25/07   Algonquin - allow for additional junior mship types and skip the record if the mship type does not match any.
 *        7/19/07   Do not change first names for Algonquin - CE.
 *        7/16/07   Add Algonquin for CE.
 *        7/13/07   Brantford - add Curling members (CURL..., LDS CURL).
 *        7/07/07   Only send status email if server is master.
 *        7/07/07   Add Oahu CC for MFirst.
 *        7/06/07   Move Providence from Meritsoft to CE processing.
 *        7/06/07   CE & MF - do not titlecase the suffix.
 *        6/29/07   Send an email to our support to report each club that roster sync ran for.
 *        6/28/07   MFirst - Wilmington - use custom1 field to determine if member has Range privileges.
 *        6/28/07   MFirst - add 3 custom fields to record format (unique usage per club).
 *        6/28/07   CC of Virginia - do NOT allow roster sync to change first names.
 *        6/25/07   Brantford - add social members (SOCIAL...., and SOCGLF....).
 *        6/22/07   Remove Meritsoft - Providence moved to CE.
 *        6/22/07   Congressional - process email changes.
 *        6/22/07   Remove Edison Club for MFirst - lost them.
 *        6/19/07   CE & MF - restore the processing to change the first names except for selected clubs.
 *        6/19/07   Congressional (CE) - check the act/inactive indicator in the file and set our flag accordingly.
 *        6/12/07   Add membership types for Baltimore.
 *        6/08/07   Add Senior Transfer mships for Wilmington.
 *        6/07/07   Virginia CC - (CE) allow emails to change.  For some reason we weren't allowing this for CE clubs.
 *        6/07/07   The CC - change the '65 & Above - Exempt' mship type because the '-' is not correct.
 *        6/05/07   Wellseley - allow roster sync to change first names.
 *        6/05/07   Saucon Valley - change the mtype value for "Senior Limited Golf Privileges" to remain as is rather
 *                                  than changing it for gender.  The result is too long for mtype field in db.
 *        6/04/07   ALL - truncate mi and suffix to prevent truncation error in new JDBC version.
 *        5/31/07   MF - always titlecase the names.
 *        5/24/07   Baltimore - CE - use webid instead of username to allow for new ids from CE.
 *        5/18/07   Add Edison Club for MFirst.
 *        5/17/07   CE - allow mNums to change for Weeburn.  For some reason, we don't normally change mNums for CE clubs.
 *        5/15/07   Add Cherry Creek for CE.
 *        5/15/07   Four Bridges - allow for automated roster sync - replace 2 dbl quotes with a '?'.
 *        5/10/07   Fix Congressional's member ids - we were allowing multiple entries for the same person.
 *        5/07/07   Send an email to our support and MF's support if members received with dup names.
 *        5/07/07   When a new member is added or an existing member is updated, make sure they are set to active.
 *        5/07/07   MF - always change our email addresses if MF's email was changed or removed (because members cannot change ours).
 *        5/04/07   Do not change the first names of existing members (all clubs) - most clubs like to change them in ForeTees.
 *        5/04/07   Add Congressional for CE.
 *        4/24/07   MFirst syncs - allow email addresses to change when a different addr is received.
 *        4/24/07   Brantford - check the age of members and set the mship accordingly (case #1130).
 *        4/24/07   Four Bridges - add check for 'Full Family Golf' (7004, 7005, 7009) members.
 *        4/24/07   Green Hills - do not change the mtype if member already exists (let them change it in ForeTees).
 *        4/19/07   Add Green Hills for MFirst.
 *        4/10/07   Remove Bellerive for the time being.
 *        4/10/07   Set member records inactive if last_sync_date is old (member not included in file).
 *        4/10/07   Add Oak Lane for MFirst.
 *        4/10/07   Add processing to check for duplicate name before adding a member (all web sites).
 *        4/06/07   Add processing to save the current date in member2b when the member was last included in a sync.
 *        4/04/07   Add Brantford for CE.
 *        4/02/07   Bellerive - do not change first names.
 *        4/02/07   Pinery - do not change first names.
 *        4/02/07   CE Rosters - skip the member if inactive.
 *        3/27/07   Add Nashawtuc for MFirst.
 *        3/27/07   Add Blue Hill for MFirst.
 *        3/26/07   Allow any member type for Philly Cricket and add an astericks to some names - MFirst.
 *        3/15/07   Add The International for MFirst.
 *        3/15/07   Add The Country Club for MFirst.
 *        3/07/07   Add Wichita for CE.
 *        3/07/07   Add Senior Special mships for Wilmington.
 *        2/19/07   Add The Point Lake Club for CE.
 *        2/16/07   Check the birth dates of dependents for Wilmington and set mtype accordingly.
 *        2/09/07   Add CC of Virginia for CE.
 *        2/08/07   Add The Pinery for MFirst.
 *        2/08/07   Add Awbrey Glen for MFirst.
 *        2/07/07   Add Belevue CC - Northstar.
 *        2/06/07   Remove Blackrock from MFirst.
 *        1/18/07   Add Wilmington for MFirst.
 *       12/18/06   Add The Peninsula Club for MFirst.
 *       12/05/06   Remove Martin Downs from MFirst.
 *       11/03/06   Add Bellerive for MFirst.
 *       11/01/06   Four Bridges - use webid instead of username to check for existing member.
 *       10/12/06   Change some Baltimore mship types (per Ben's instructions).
 *        9/07/06   Add Flexscape and Four Bridges (manual upload for now).
 *        9/05/06   Verify the email addresses.
 *        9/05/06   Add membership types for Baltimore.
 *        8/14/06   Remove titlecase call for Lakewood Ranch.
 *        8/05/06   Add Baltimore for CE.
 *        6/29/06   Add Martin Downs for MFirst.
 *        6/29/06   Add Long Cove for MFirst.
 *        6/23/06   Add Lakewood Ranch for MFirst.
 *        6/01/06   Add Wee Burn and CE.
 *        5/25/06   Add Wellesley to MFirst.
 *        5/25/06   Do not process club file if not ready for it.
 *        4/07/06   Add Rhode Island to MFirst.
 *        4/07/06   Add method to receive control from Support to process one club.
 *        3/30/06   Add Edgewood and Out Door to MFirst.
 *        3/05/06   Change path statements for roster files - now on backup MySQL server.
 *        3/01/06   Add Common_Server class file processing where we check the id of the server we are running in
 *                  when timer processes are triggered.  If not server #1, do not process the timer (let it run though).
 *         2/07/06  Add webid to member2b.
 *         1/04/06  Add Ritz-Carlton CC - Northstar.
 *        11/09/05  Add MeritSoft and Providence CC.
 *        08/22/05  Add Black Rock - MFirst.
 *        07/18/05  Add northstarSync method to process roster syncs from NorthStar (POS).
 *        04/21/05  Temporarily comment out the code that changes our email addresses
 *                  until all MFirst clubs are using this system, then we will remove
 *                  email options from our member settings page.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;
import com.foretees.common.FeedBack;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;


public class Common_sync {

 static final int TIMER_SERVER = SystemUtils.TIMER_SERVER;

 static String rev = SystemUtils.REVLEVEL;

 static String host = SystemUtils.HOST;
 static String port = SystemUtils.PORT;
 static String efrom = SystemUtils.EFROM;
 static String emailFT = "support@foretees.com";


 //
 //*****************************************************************
 //   Method to process all roster sync files - called by Timer
 //*****************************************************************
 //
 public static void rosterSync() {

   Connection con = null;                 // init DB objects
   Connection con2 = null;                 // init DB objects
   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String errorMsg = "Error in Common_sync.rosterSync: ";

   String emailMsgAll = "Roster Sync Results: \n\n";


   boolean failed = false;
   boolean changed = false;
   boolean skip = false;

   int result = 0;
   int clubCount = 0;

   //
   //  This must be the master server!!!  If not, let the timer run in case master goes down.
   //
   if (Common_Server.SERVER_ID == TIMER_SERVER) {

      //
      // Load the JDBC Driver and connect to DB
      //
      String club = rev;        // get v_ db name

      try {
         con2 = dbConn.Connect(club);

      }
      catch (Exception exc) {
         return;
      }

      try {
         //
         //  Search through each club in the system and look for a corresponding file
         //
         stmt2 = con2.createStatement();              // create a statement

         rs2 = stmt2.executeQuery("SELECT clubname FROM clubs");

         while (rs2.next()) {

            club = rs2.getString(1);                 // get a club name

            con = dbConn.Connect(club);              // get a connection to this club's db

            //
            //  Init the rsync flag for ALL clubs so only those that actually still use RSync will be set.
            //
            try {

               stmt = con.createStatement();              // create a statement

               stmt.executeUpdate("UPDATE club5 SET rsync = 0");

               stmt.close();

            }
            catch (Exception exc) {
            }

            //
            //  Do custom processing for clubs that do not use RS
            //
            if (club.equals("tualatincc")) {       // Tualatin CC

               checkTualatin(con);                 // check birth dates for mtypes (Juniors) - no rsoter sync

            } else if (club.equals("interlachen")) {       // Interlachen CC

               checkInterlachen(con);                 // check birth dates for mtypes (Juniors) - no roster sync

            } else if (club.equals("mountvernoncc")) {       // Mount Vernon CC

               checkMountVernon(con);                 // check birth dates for mtypes (Juniors) - no roster sync

            } else if (club.equals("hyperion")) {       // Hyperion Field Club

               checkHyperion(con);                 // check birth dates for mtypes (Juniors) - no roster sync

            } else if (club.equals("tahoedonner")) {

               checkTahoeDonner(con);             // check birth dates for mtypes (Juniors) - no roster sync

            } else {

               //
               //  Go process this club
               //
               result = clubSync(club, con);

               if (result == 0) {
                  //emailMsgAll = emailMsgAll + "Roster Not Found for: " + club + "\n\n";
               } else if (result == 1) {
                  emailMsgAll += "Roster Found for: " + club + "\n\n";
                  clubCount++;
               } else if (result == -1) {
                  //emailMsgAll += "Roster Sync Failed for: " + club + "\n\n";
               }

            }

            con.close();       // return/close the connection

         } // end of WHILE clubs

         stmt2.close();              // close the stmt

      }
      catch (Exception exc) {

         errorMsg = errorMsg + " Error getting club db for " +club+ ", " + exc.getMessage();   // build msg
         SystemUtils.logError(errorMsg);                                                       // log it
      }

   }

   //
   //  Reset the Roster Sync Timer for next night
   //
   TimerSync sync_timer = new TimerSync();

   try {

      if (con != null) {

         con.close();       // return/close the connection
      }
   }
   catch (SQLException e) {
   }

   try {

      if (con2 != null) {

         con2.close();       // return/close the connection
      }
   }
   catch (SQLException e) {
   }

   //
   //  Send an email to our support if any clubs were processed
   //
   if (Common_Server.SERVER_ID == TIMER_SERVER && clubCount > 0) {

      try {

         Properties properties = new Properties();
         properties.put("mail.smtp.host", host);                      // set outbound host address
         properties.put("mail.smtp.port", port);                      // set outbound port
         properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

         Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

         MimeMessage message = new MimeMessage(mailSess);

         message.setFrom(new InternetAddress(efrom));                                    // set from addr

         message.setSubject( "ForeTees Roster Sync Report: " +clubCount+ " Clubs" );     // set subject line
         message.setSentDate(new java.util.Date());                                      // set date/time sent

         message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailFT));   // set our support email addr

         message.setText( emailMsgAll );  // put msg in email text area

         Transport.send(message);         // send it!!

      }
      catch (Exception ignore) {
      }
   }

 }


 //
 //*****************************************************************
 //   Method to process each individual club - called from above or Support !!
 //*****************************************************************
 //
 public static int clubSync(String club, Connection con) {


   boolean failed = false;
   boolean found = false;
   boolean changed = false;
   boolean skip = false;
   boolean clubcorp = false;


   //
   //  Process each club
   //
   //  Check for MFirst Rosters - must be named 'club.txt' in v_/rosters/mfirst (club = club name)
   //
   //  Then check NorthStar Rosters - must be named 'club.csv' in /home/rosters/northstar (club = club name)
   //
   //  Then check for CE files - must be named 'club.csv' in /home/rosters/ce
   //
   //  Then check for Flexscape files - must be named 'club.csv' in /home/rosters/flexscape (manual upload)
   //
   //  Then check for Dream World files - must be named 'club.csv' in /home/rosters/dreamworld
   //
   FileReader fr = null;
   File fileNS = null;               // use file for NorthStar's csv files (no CR LF)
   FileInputStream fis = null;
   InputStreamReader isr = null;

   Connection con2 = null;
   ResultSet rs = null;

   try {                             // check CE on server

       fileNS = new File("//home//rosters//ce//" +club+ ".csv");

       fis = new FileInputStream(fileNS);

       isr = new InputStreamReader(fis);

   }
   catch (Exception e1) {

       failed = true;
   }

   //
   //  if we found a CE file for this club - go process
   //
   if (failed == false) {

       ceSync(con, isr, club);        // go process CE roster

       try {

           fis.close();

       }
       catch (Exception ignore) {
       }

   } else {

       failed = false;

       try {                             // check MFirst on server

           String clubname = club;

           if (clubname.equals("edina")) {
               clubname = "edina2010";
           } else if (clubname.equals("quecheeclubtennis")) {
               clubname = "quecheeclub";
           }

           fr = new FileReader("//home//rosters//mfirst//" +clubname+ ".txt");
           //fr = new FileReader("//home//rosters//mfirst//" +club+ ".txt");  changed to run off temp 'clubname' var instead of the actual 'club' var for use with customs

       }
       catch (Exception e1) {

          failed = true;
       }

       if (failed == true) {

           try {

               failed = false;

               con2 = dbConn.Connect(rev);

               // Check clubcorp table for name conversion
               PreparedStatement stmt = con2.prepareStatement("SELECT cc_name FROM clubcorp WHERE ft_name = ?");

               stmt.clearParameters();
               stmt.setString(1, club);
               rs = stmt.executeQuery();

               if (rs.next()) {

                   String ccClub = "";
                   ccClub = rs.getString("cc_name");
                   clubcorp = true;

                   try {
                       fr = new FileReader("//home//rosters//mfirst//" +ccClub+ ".txt");
                   } catch (Exception e2) {
                       failed = true;
                   }

               } else {

                  failed = true;        // club name not found
               }

               stmt.close();

           } catch (Exception exc) {

               failed = true;
           }
       }

       //
       //  if we found a MFirst file for this club - go process
       //
       if (failed == false) {

          mFirstSync(con, fr, club, clubcorp);        // go process MFirst roster

       } else {

           //
           //  Check Jonas folder for a roster for this club
           //
           failed = true;       //TEMP
           /*
           failed = false;                   // init

           try {                             // check NStar on server

               fileNS = new File("//home//rosters//jonas//" +club+ ".csv");

               fis = new FileInputStream(fileNS);

               isr = new InputStreamReader(fis);

           }
           catch (Exception e1) {

               failed = true;
           }
            */
       }

       //
       //  if we found a Jonas file for this club - go process
       //
       if (failed == false) {

          //jonasSync(con, isr, club);        // go process Jonas roster

       } else {

         //
         //  Check NorthStar folder for a roster for this club
         //
         failed = false;                   // init

         try {                             // check NStar on server

            fileNS = new File("//home//rosters//northstar//" +club+ ".csv");

            fis = new FileInputStream(fileNS);

            isr = new InputStreamReader(fis);

         }
         catch (Exception e1) {

            failed = true;
         }


         //
         //  if we found a NStar file for this club - go process
         //
         if (failed == false) {

            northstarSync(con, isr, club);        // go process NStar roster

            try {

               fis.close();

            }
            catch (Exception ignore) {
            }

         } else {

           /*                // no longer supported
            //
            //  Check MeritSoft folder for a roster for this club
            //
            failed = false;                   // init

            try {                             // check MeritSoft on server

               fileNS = new File("//home//rosters//meritsoft//" +club+ ".csv");

               fis = new FileInputStream(fileNS);

               isr = new InputStreamReader(fis);

            }
            catch (Exception e1) {

               failed = true;
            }

            //
            //  if we found a MeritSoft file for this club - go process
            //
            if (failed == false) {

               meritsoftSync(con, isr, club);        // go process MeritSoft roster

               try {

                  fis.close();

               }
               catch (Exception ignore) {
               }

            } else {
            */

               //
               //  Check FlexScape folder for a roster for this club (added manually for now!!!)
               //
               failed = false;                   // init

               try {                             // check server

                  fileNS = new File("//home//rosters//flexscape//" +club+ ".csv");

                  fis = new FileInputStream(fileNS);

                  isr = new InputStreamReader(fis);

               }
               catch (Exception e1) {

                  failed = true;
               }

               //
               //  if we found a FlexScape file for this club - go process
               //
               if (failed == false) {

                  flexSync(con, isr, club);        // go process FlexScape roster

                  try {

                     fis.close();

                  }
                  catch (Exception ignore) {
                  }

               } else {

                  //
                  //  Check Dream World folder for a roster for this club
                  //
                  failed = false;                   // init

                  try {                             // check server

                     fileNS = new File("//home//rosters//dreamworld//" +club+ ".csv");

                     fis = new FileInputStream(fileNS);

                     isr = new InputStreamReader(fis);

                  }
                  catch (Exception e1) {

                     failed = true;
                  }

                  //
                  //  if we found a Dream WOrld file for this club - go process
                  //
                  if (failed == false) {

                     dreamworldSync(con, isr, club);        // go process Dream World roster

                     try {

                        fis.close();

                     }
                     catch (Exception ignore) {
                     }
                     
                  } else {     // end of Dream World

                      //
                      //  Check ClubTec folder for a roster for this club
                      //
                      failed = false;                   // init

                      try {                             // check server

                         fileNS = new File("//home//rosters//clubtec//" +club+ ".csv");

                         fis = new FileInputStream(fileNS);

                         isr = new InputStreamReader(fis);

                      }
                      catch (Exception e1) {

                         failed = true;
                      }

                      //
                      //  if we found a ClubTec file for this club - go process
                      //
                      if (failed == false) {

                         clubTecSync(con, isr, club);        // go process ClubTec roster

                         try {

                            fis.close();

                         }
                         catch (Exception ignore) {
                         }
                      }     // end of ClubTec
                  }
               }   // end of IF Flexscape
            }
//       }      // end of IF Meritsoft
      }
   }              // end of IF file found for this club

   //
   //  if we found a file for this club - make entry in email
   //
   if (failed == false) {

      //emailMsgAll = emailMsgAll + "Roster Sync Processing Complete For Club = " +club+ ".\n\n";
       // File found
       return 1;
   } else {

      //emailMsgAll = emailMsgAll + "Roster Sync File Not Found for Club = " +club+ ".\n\n";
      // file not found
      return 0;
   }

 }


 //
 //*****************************************************************
 //   Method to process files from Members First
 //*****************************************************************
 //
 private static void mFirstSync(Connection con, FileReader fr, String club, boolean clubcorp) {

   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;
   int inact = 0;
   int pri_indicator = 0;

   // Values from MFirst records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String msub_type = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String mobile = "";
   String primary = "";
   String webid = "";
   String custom1 = "";
   String custom2 = "";
   String custom3 = "";

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;

   // Values from ForeTees records
   //
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";
   String msub_type_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;

   // Values for New ForeTees records
   //
   String memid_new = "";
   String webid_new = "";
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";
   String msub_type_new = "";
   String dupuser = "";
   String dupwebid = "";
   String dupmnum = "";
   String emailMF = "support_alerts@memfirst.com";
   String subject = "Roster Sync Warning from ForeTees for " +club;

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int errCount = 0;
   int warnCount = 0;
   int totalErrCount = 0;
   int totalWarnCount = 0;
   int email_bounce1 = 0;
   int email_bounce2 = 0;

   String errorMsg = "";
   String errMemInfo = "";
   String errMsg = "";
   String warnMsg = "";
   String emailMsg1 = "Duplicate names found in MembersFirst file during ForeTees Roster Sync processing for club: " +club+ ".\n\n";
   String emailMsg2 = "\nThis indicates that either 2 members have the exact same names (not allowed), or MF's member id has changed.\n\n";

   ArrayList<String> errList = new ArrayList<String>();
   ArrayList<String> warnList = new ArrayList<String>();

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean found = false;
   boolean sendemail = false;
   boolean genderMissing = false;
   boolean useWebid = false;
   boolean useWebidQuery = false;


   // Overwrite log file with fresh one for today's logs
   SystemUtils.logErrorToFile("Members First: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //   format of each line in the file:
      //
      //     memid, mNum, fname, mi, lname, suffix, mship, mtype, gender, email, email2,
      //     phone, phone2, bag, hndcp#, uhndcp, chndcp, birth, posid, mobile, primary
      //
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         //  Remove the dbl quotes and check for embedded commas

         line = cleanRecord( line );

         //  parse the line to gather all the info

         StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

         if ( tok.countTokens() > 20 ) {     // enough data ?

            memid = tok.nextToken();
            mNum = tok.nextToken();
            fname = tok.nextToken();
            mi = tok.nextToken();
            lname = tok.nextToken();
            suffix = tok.nextToken();
            mship = tok.nextToken();     // col G
            mtype = tok.nextToken();     // col H
            gender = tok.nextToken();
            email = tok.nextToken();
            email2 = tok.nextToken();
            phone = tok.nextToken();
            phone2 = tok.nextToken();
            bag = tok.nextToken();
            ghin = tok.nextToken();
            u_hndcp = tok.nextToken();
            c_hndcp = tok.nextToken();
            temp = tok.nextToken();
            posid = tok.nextToken();
            mobile = tok.nextToken();
            primary = tok.nextToken();     // col U

            if ( tok.countTokens() > 0 ) {

               custom1 = tok.nextToken();
            }
            if ( tok.countTokens() > 0 ) {

               custom2 = tok.nextToken();
            }
            if ( tok.countTokens() > 0 ) {

               custom3 = tok.nextToken();
            }


            //   trim gender in case followed be spaces
            gender = gender.trim();

            //
            //  Check for ? (not provided)
            //
            if (memid.equals( "?" )) {

               memid = "";
            }
            if (mNum.equals( "?" )) {

               mNum = "";
            }
            if (fname.equals( "?" )) {

               fname = "";
            }
            if (mi.equals( "?" )) {

               mi = "";
            }
            if (lname.equals( "?" )) {

               lname = "";
            }
            if (suffix.equals( "?" )) {

               suffix = "";
            }
            if (mship.equals( "?" )) {

               mship = "";
            }
            if (mtype.equals( "?" )) {

               mtype = "";
            }
            if (gender.equals( "?" ) || (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F"))) {

                  if (gender.equals( "?" )) {
                      genderMissing = true;
                  } else {
                      genderMissing = false;
                  }
                  gender = "";
               }
            if (email.equals( "?" )) {

               email = "";
            }
            if (email2.equals( "?" )) {

               email2 = "";
            }
            if (phone.equals( "?" )) {

               phone = "";
            }
            if (phone2.equals( "?" )) {

               phone2 = "";
            }
            if (bag.equals( "?" )) {

               bag = "";
            }
            if (ghin.equals( "?" )) {

               ghin = "";
            }
            if (u_hndcp.equals( "?" )) {

               u_hndcp = "";
            }
            if (c_hndcp.equals( "?" )) {

               c_hndcp = "";
            }
            if (temp.equals( "?" )) {

               birth = 0;

            } else {

               birth = Integer.parseInt(temp);
            }
            if (posid.equals( "?" )) {

               posid = "";
            }
            if (mobile.equals( "?" )) {

               mobile = "";
            }
            if (primary.equals( "?" )) {

               primary = "";
            }

            //
            //  Determine if we should process this record (does it meet the minimum requirements?)
            //
            if (!memid.equals( "" ) && !mNum.equals( "" ) &&
                !lname.equals( "" ) && !fname.equals( "" )) {

               //
               //  Remove spaces, etc. from name fields
               //
               tok = new StringTokenizer( fname, " " );     // delimiters are space
               fname = tok.nextToken();                     // remove any spaces and middle name

               if ( tok.countTokens() > 0 && mi.equals( "" )) {

                    mi = tok.nextToken();
               }

               if (!suffix.equals( "" )) {                     // if suffix provided

                  tok = new StringTokenizer( suffix, " " );     // delimiters are space
                  suffix = tok.nextToken();                     // remove any extra (only use one value)
               }

               tok = new StringTokenizer( lname, " " );     // delimiters are space
               lname = tok.nextToken();                     // remove suffix and spaces

               if (!suffix.equals( "" ) && tok.countTokens() > 0 && club.equals("pradera")) {     // Pradera - if suffix AND 2-part lname, use both

                   String lpart2 = tok.nextToken();

                   lname = lname + "_" + lpart2;          // combine them (i.e.  Van Ess = Van_Ess)

               } else {

                  if (suffix.equals( "" ) && tok.countTokens() > 0) {                   // if suffix not provided

                     suffix = tok.nextToken();
                  }
               }

               //
               //  Make sure name is titled (most are already)
               //
               if (!club.equals("thereserveclub")) {
                   fname = toTitleCase(fname);
               }

               if (!club.equals("lakewoodranch") && !club.equals("ballantyne") && !club.equals("pattersonclub") && !club.equals("baldpeak") && !club.equals("pradera") &&
                       !club.equals("wellesley") && !club.equals("portlandgc") && !club.equals("trooncc") && !club.equals("pmarshgc") && !club.equals("paloaltohills") &&
                       !club.equals("thereserveclub") && !club.equals("castlepines")) {

                  lname = toTitleCase(lname);
               }

               if (!suffix.equals( "" )) {                  // if suffix provided

                  lname = lname + "_" + suffix;             // append suffix to last name
               }

               //
               //  Determine the handicaps
               //
               u_hcap = -99;                    // indicate no hndcp
               c_hcap = -99;                    // indicate no c_hndcp

               if (!u_hndcp.equals( "" ) && !u_hndcp.equalsIgnoreCase("NH") && !u_hndcp.equalsIgnoreCase("NHL")) {

                  u_hndcp = u_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                  u_hndcp = u_hndcp.replace('H', ' ');    //         or 'H' if present
                  u_hndcp = u_hndcp.replace('N', ' ');    //         or 'N' if present
                  u_hndcp = u_hndcp.replace('J', ' ');    //         or 'J' if present
                  u_hndcp = u_hndcp.replace('R', ' ');    //         or 'R' if present
                  u_hndcp = u_hndcp.trim();

                  u_hcap = Float.parseFloat(u_hndcp);                   // usga handicap

                  if ((!u_hndcp.startsWith("+")) && (!u_hndcp.startsWith("-"))) {

                     u_hcap = 0 - u_hcap;                       // make it a negative hndcp (normal)
                  }
               }

               if (!c_hndcp.equals( "" ) && !c_hndcp.equalsIgnoreCase("NH") && !c_hndcp.equalsIgnoreCase("NHL")) {

                  c_hndcp = c_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                  c_hndcp = c_hndcp.replace('H', ' ');    //         or 'H' if present
                  c_hndcp = c_hndcp.replace('N', ' ');    //         or 'N' if present
                  c_hndcp = c_hndcp.replace('J', ' ');    //         or 'J' if present
                  c_hndcp = c_hndcp.replace('R', ' ');    //         or 'R' if present
                  c_hndcp = c_hndcp.trim();

                  c_hcap = Float.parseFloat(c_hndcp);                   // usga handicap

                  if ((!c_hndcp.startsWith("+")) && (!c_hndcp.startsWith("-"))) {

                     c_hcap = 0 - c_hcap;                       // make it a negative hndcp (normal)
                  }
               }

               password = lname;

               //
               //  if lname is less than 4 chars, fill with 1's
               //
               int length = password.length();

               while (length < 4) {

                  password = password + "1";
                  length++;
               }

               //
               //  Verify the email addresses
               //
               if (!email.equals( "" )) {      // if specified
                  
                  email = email.trim();           // remove spaces

                  FeedBack feedback = (member.isEmailValid(email));

                  if (!feedback.isPositive()) {    // if error

                     email = "";                   // do not use it
                  }
               }
               if (!email2.equals( "" )) {      // if specified

                  email2 = email2.trim();           // remove spaces

                  FeedBack feedback = (member.isEmailValid(email2));

                  if (!feedback.isPositive()) {    // if error

                     email2 = "";                   // do not use it
                  }
               }

               // if email #1 is empty then assign email #2 to it
               if (email.equals("")) email = email2;

               skip = false;
               errCount = 0;        // reset error count
               warnCount = 0;       // reset warning count
               errMsg = "";         // reset error message
               warnMsg = "";        // reset warning message
               errMemInfo = "";     // reset error member info
               found = false;    // init club found


               //
               //  Format member info for use in error logging before club-specific manipulation
               //
               errMemInfo = "Member Details:\n" +
                                      "  name: " + lname + ", " + fname + " " + mi + "\n" +
                                      "  mtype: " + mtype + "  mship: " + mship + "\n" +
                                      "  memid: " + memid + "  mNum: " + mNum + "  gender: " + gender;

               // if gender is incorrect or missing, flag a warning in the error log
               if (gender.equals("")) {

                   // report only if not a club that uses blank gender fields
                   if (!club.equals("roccdallas") && !club.equals("charlottecc") && !club.equals("sawgrass") && !clubcorp) {

                       warnCount++;
                       if (genderMissing) {
                         warnMsg = warnMsg + "\n" +
                                 "  -GENDER missing! (Defaulted to 'M')";
                       } else {
                         warnMsg = warnMsg + "\n" +
                                 "  -GENDER incorrect! (Defaulted to 'M')";
                       }

                       gender = "M";

                   } else if (club.equals("charlottecc") || club.equals("sawgrass")) {         // default to female instead

                       warnCount++;
                       if (genderMissing) {
                         warnMsg = warnMsg + "\n" +
                                 "  -GENDER missing! (Defaulted to 'F')";
                       } else {
                         warnMsg = warnMsg + "\n" +
                                 "  -GENDER incorrect! (Defaulted to 'F)";
                       }

                       gender = "F";

                   } else if (clubcorp) {

                       errCount++;
                       skip = true;
                       if (genderMissing) {
                           errMsg = errMsg + "\n" +
                                   "  -SKIPPED: GENDER missing!";
                       } else {
                           errMsg = errMsg + "\n" +
                                   "  -SKIPPED: GENDER incorrect!";
                       }
                   }
               }

               //
               //  Skip entries with first/last names of 'admin'
               //
               if (fname.equalsIgnoreCase("admin") || lname.equalsIgnoreCase("admin")) {
                   errCount++;
                   skip = true;
                   errMsg = errMsg + "\n" +
                           "  -INVALID NAME! 'Admin' or 'admin' not allowed for first or last name";
               }

               //
               // *********************************************************************
               //
               //   The following will be dependent on the club - customized
               //
               // *********************************************************************
               //

               //******************************************************************
               //   Saucon Valley Country Club
               //******************************************************************
               //
               if (club.equals( "sauconvalleycc" )) {

                  found = true;    // club found

                  //
                  //  Determine if we should process this record
                  //
                  if (!mship.equalsIgnoreCase( "No Privileges" ) && !mtype.equalsIgnoreCase( "Social" ) &&
                      !mtype.equalsIgnoreCase( "Recreational" )) {

                     //
                     //  determine member type
                     //
                     if (mtype.equals( "" )) {     // if not specified
                         mtype = "Staff";           // they are staff
                     }
                     if (mship.equals( "" )) {     // if not specified
                         mship = "Staff";           // they are staff
                     }

                     if (gender.equals( "" )) {     // if not specified

                        gender = "M";               // default to Male
                     }

                     //
                     //  The Member Types and Mship Types for this club are backwards.
                     //  We must set our fields accordingly.
                     //
                     String memType = mship;        // set actual mtype value
                     mship = mtype;                 // set actual mship value

                     if (memType.equals( "Full Golf Privileges" )) {

                        mtype = "Full Golf Privileges Men";

                        if (gender.equals( "F" )) {

                           mtype = "Full Golf Privileges Women";
                        }
                     }

                     if (memType.equals( "Limited Golf Privileges" )) {

                        mtype = "Limited Golf Privileges Men";

                        if (gender.equals( "F" )) {

                           mtype = "Limited Golf Privileges Women";
                        }
                     }

                     if (memType.equals( "Senior Limited Golf Privileges" )) {

                        mtype = "Senior Limited Golf Privileges";
                     }

                     //
                     //  set posid according to mNum
                     //
                     if (mNum.endsWith( "-1" )) {        // if spouse

                        tok = new StringTokenizer( mNum, "-" );     // delimiter is '-'
                        posid = tok.nextToken();                    // get mNum without extension
                        posid = stripA(posid);                      // remove the ending '0' (i.e. was 2740-1)
                        posid = posid + "1";                        // add a '1'             (now 2741)

                     } else {

                        if (mNum.endsWith( "-2" )) {        // if spouse or other

                           tok = new StringTokenizer( mNum, "-" );     // delimiter is '-'
                           posid = tok.nextToken();                    // get mNum without extension
                           posid = stripA(posid);                      // remove the ending '0' (i.e. was 2740-2)
                           posid = posid + "2";                        // add a '2'             (now 2742)

                        } else {

                           if (mNum.endsWith( "-3" )) {        // if spouse or other

                              tok = new StringTokenizer( mNum, "-" );     // delimiter is '-'
                              posid = tok.nextToken();                    // get mNum without extension
                              posid = stripA(posid);                      // remove the ending '0' (i.e. was 2740-3)
                              posid = posid + "3";                        // add a '3'             (now 2743)

                           } else {

                              if (mNum.endsWith( "-4" )) {        // if spouse or other

                                 tok = new StringTokenizer( mNum, "-" );     // delimiter is '-'
                                 posid = tok.nextToken();                    // get mNum without extension
                                 posid = stripA(posid);                      // remove the ending '0' (i.e. was 2740-4)
                                 posid = posid + "4";                        // add a '4'             (now 2744)

                              } else {

                                 posid = mNum;                   // primary posid = mNum
                              }
                           }
                        }
                     }

                     suffix = "";     // done with suffix for now

                     //
                     //   Check if member is over 70 yrs old - if so, add '_*' to the end of the last name
                     //                                        so proshop will know
                     //
                     if (birth > 0 && birth < 19500000) {     // if worth checking

                        //
                        //  Get today's date and then go back 70 years
                        //
                        Calendar cal = new GregorianCalendar();       // get todays date

                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH) +1;
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        year = year - 70;                              // go back 70 years

                        int oldDate = (year * 10000) + (month * 100) + day;   // get date

                        if (birth <= oldDate) {     // if member is 70+ yrs old

                           lname = lname + "_*";          // inidicate such
                        }
                     }

                  } else {

                     skip = true;             // skip this record
                     warnCount++;
                     warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                  }

               }         // end of IF club = sauconvalleycc

               //******************************************************************
               //   Crestmont CC
               //******************************************************************
               //
               if (club.equals( "crestmontcc" )) {

                  found = true;    // club found

                  //
                  //  determine member type
                  //

                  if (gender.equals( "" )) {     // if not specified

                     gender = "M";               // default to Male
                  }

                  mtype = "T Designated Male";

                  if (gender.equals( "F" )) {
                      mtype = "T Designated Female";
                  }

               }         // end of IF club = crestmontcc

               //******************************************************************
               //   Black Rock CC
               //******************************************************************
               //
        /*
               if (club.equals( "blackrock" )) {

                  found = true;    // club found

                  //
                  //  remove the 'A' from spouses mNum
                  //
                  if (mNum.endsWith( "A" ) || mNum.endsWith( "B" ) || mNum.endsWith( "C" ) ||
                      mNum.endsWith( "D" ) || mNum.endsWith( "E" ) || mNum.endsWith( "F" )) {

                     mNum = stripA(mNum);            // remove the ending 'A'
                  }

                  //
                  //  Set POS Id in case they ever need it
                  //
                  posid = mNum;

                  //
                  //  determine member type
                  //
                  if (gender.equals( "" )) {     // if not specified

                     gender = "M";               // default to Male
                  }

                  if (!mtype.equals( "Dependents" )) {       // if not a junior

                     if (gender.equals( "F" )) {

                        if (mtype.equals( "Primary" )) {

                           mtype = "Member Female";

                        } else {

                           mtype = "Spouse Female";
                        }

                     } else {       // Male

                        if (mtype.equals( "Primary" )) {

                           mtype = "Member Male";

                        } else {

                           mtype = "Spouse Male";
                        }
                     }
                  }

               }         // end of IF club = blackrock
       */

               //******************************************************************
               //   John's Island CC
               //******************************************************************
               //
               if (club.equals( "johnsisland" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is not a blank or admin record
                  //
                  if (mship.equals( "" )) {

                     skip = true;          // skip it
                     errCount++;
                     errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (lname.equalsIgnoreCase( "admin" )) {

                      skip = true;         // skip it
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Admin' MEMBERSHIP TYPE!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine membership type
                     //
                     if (mship.equals( "Golf Member" )) {
                         mship = "Golf";
                     } else if (mship.startsWith( "Golf Swap" )) {
                         mship = "Golf Swap";
                         lname = lname + "*";          // mark these members
                     } else if (mship.equals( "Sport/Social Member" )) {
                         mship = "Sport Social";
                         lname = lname + "*";          // mark these members
                     } else if (mship.startsWith( "Sport/Social Swap" )) {
                         mship = "Sport Social Swap";
                     } else {
                         mship = "Golf";
                     }

                     //
                     //  determine member type
                     //
                     if (gender.equals( "" )) {     // if not specified

                        gender = "M";               // default to Male
                     }

                     if (gender.equals( "M" ) && primary.equals( "P" )) {
                         mtype = "Primary Male";
                     } else if (gender.equals( "F" ) && primary.equals( "P" )) {
                         mtype = "Primary Female";
                     } else if (gender.equals( "M" ) && primary.equals( "S" )) {
                         mtype = "Spouse Male";
                     } else if (gender.equals( "F" ) && primary.equals( "S" )) {
                         mtype = "Spouse Female";
                     } else {
                         mtype = "Primary Male";
                     }
                  }
               }         // end of IF club = johnsisland

/*
               //******************************************************************
               //   Philadelphia Cricket Club
               //******************************************************************
               //
               if (club.equals( "philcricket" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is not an admin record or missing mship/mtype
                  //
                  if (mtype.equals( "" )) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing! (*Note* mship located in mtype field)";

                  } else if (lname.equalsIgnoreCase( "admin" )) {

                      skip = true;         // skip it
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Admin' MEMBERSHIP TYPE!";
                  } else {

                     if (mtype.equalsIgnoreCase("Leave of Absence")) {
                         
                         skip = true;
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                     }

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine member type
                     //
                     if (mtype.equalsIgnoreCase( "golf stm family" ) || mtype.equalsIgnoreCase( "golf stm ind." )) {

                        lname = lname + "*";       // add an astericks
                     }

                     // if mtype = no golf, add ^ to their last name
                     if (mtype.equalsIgnoreCase( "no golf" )) {

                        lname += "^";
                     }

                     mship = toTitleCase(mtype);       // mship = mtype

                     if (gender.equalsIgnoreCase("F")) {
                         mtype = "Adult Female";
                     } else {
                         mtype = "Adult Male";
                     }


               /*
                     //
                     //  determine member type
                     //
                     if (mtype.equalsIgnoreCase( "2 junior ft golfers" ) || mtype.equalsIgnoreCase( "add'l jr. ft golfers" ) ||
                         mtype.equalsIgnoreCase( "ft golf full 18-20" ) || mtype.equalsIgnoreCase( "ft jr. 17 & under" ) ||
                         mtype.equalsIgnoreCase( "jr 17 & under w/golf" ) || mtype.equalsIgnoreCase( "jr 18-20 w/golf" ) ||
                         mtype.equalsIgnoreCase( "jr. activity/no chg" )) {

                        mtype = "Certified Juniors";

                     } else {

                        if (gender.equals( "" )) {     // if not specified

                           gender = "M";               // default to Male
                        }

                        if (mtype.equalsIgnoreCase( "ft assoc golf 21-30" ) || mtype.equalsIgnoreCase( "ft assoc ind golf" ) ||
                            mtype.equalsIgnoreCase( "ft assoc ind/stm fm" )) {

                           if (gender.equals( "M" )) {

                              mtype = "Associate Male";

                           } else {

                              mtype = "Associate Female";
                           }

                        } else {

                           if (mtype.equalsIgnoreCase( "ft full fm/ind 21-30" ) || mtype.equalsIgnoreCase( "ft full ind/sm fm" ) ||
                               mtype.equalsIgnoreCase( "ft golf full fam." ) || mtype.equalsIgnoreCase( "ft golf full ind." ) ||
                               mtype.equalsIgnoreCase( "ft golf honorary" )) {

                              if (gender.equals( "M" )) {

                                 mtype = "Full Male";

                              } else {

                                 mtype = "Full Female";
                              }

                           } else {

                              if (mtype.equalsIgnoreCase( "golf stm family" ) || mtype.equalsIgnoreCase( "golf stm ind." )) {

                                 if (gender.equals( "M" )) {

                                    mtype = "STM Male";

                                 } else {

                                    mtype = "STM Female";
                                 }

                              } else {

                                 if (mtype.equalsIgnoreCase( "golf stm (17-20)" ) || mtype.equalsIgnoreCase( "golf stm 16 & under" )) {

                                    mtype = "STM Junior";

                                 } else {

                                    mtype = "Non-Golfing";
                                 }
                              }
                           }
                        }
                     }
                  }

               }         // end of IF club = philcricket

              */
               /*
               //******************************************************************
               //   Edgewood CC
               //******************************************************************
               //
               if (club.equals( "edgewood" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                  } else if (mship.equalsIgnoreCase( "Sport" )) {

                      skip = true;         // skip it
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Sport' MEMBERSHIP TYPE!";
                  } else if (mship.equalsIgnoreCase( "Dining" )) {

                      skip = true;         // skip it
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Dining' MEMBERSHIP TYPE!";
                  } else {

                     //
                     //  Make sure mship is titled
                     //
                     mship = toTitleCase(mship);

                     //
                     //  May have to strip -1 off end of mnum
                     //
                     tok = new StringTokenizer( mNum, "-" );       // delimiter is '-'

                     if ( tok.countTokens() > 1 ) {
                         mNum = tok.nextToken();                    // get mNum without extension
                     }

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Strip any leading zeros from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero
                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     //
                     //  determine member type
                     //
                     if (gender.equals( "" ) || gender.equals( "U" )) {     // if not specified or U (??)

                        gender = "M";               // default to Male
                     }

                     if (primary.equals( "P" )) {

                        if (gender.equals( "M" )) {
                            mtype = "Primary Male";
                        } else {
                            mtype = "Primary Female";
                        }

                     } else {

                        if (gender.equals( "M" )) {
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";
                        }
                     }
                  }
               }         // end of IF club = edgewood
             */
             /*
               //******************************************************************
               //   Out Door CC
               //******************************************************************
               //
               if (club.equals( "outdoor" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                  } else {

                     //
                     //  Translate the mship value - remove the '00x-' prefix
                     //
                     tok = new StringTokenizer( mship, "-" );       // delimiter is '-'

                     if ( tok.countTokens() > 1 ) {

                        mship = tok.nextToken();                    // get prefix
                        mship = tok.nextToken();                    // get mship without prefix
                     }


                     //
                     //  May have to strip -1 off end of mnum
                     //
                     tok = new StringTokenizer( mNum, "-" );       // delimiter is '-'

                     if ( tok.countTokens() > 1 ) {
                         mNum = tok.nextToken();                    // get mNum without extension
                     }

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine member type
                     //
                     if (gender.equals( "" ) || gender.equals( "U" )) {     // if not specified or U (??)

                        gender = "M";               // default to Male
                     }

                     if (primary.equals( "P" )) {

                        if (gender.equals( "M" )) {
                            mtype = "Member Male";
                        } else {
                            mtype = "Member Female";
                        }

                     } else {

                        if (gender.equals( "M" )) {
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";
                        }
                     }
                  }
               }         // end of IF club = outdoor
              */


               //******************************************************************
               //   Rhode Island CC
               //******************************************************************
               //
               if (club.equals( "rhodeisland" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                  } else {

                     //
                     //  May have to strip -1 off end of mnum
                     //
                     tok = new StringTokenizer( mNum, "-" );       // delimiter is '-'

                     if ( tok.countTokens() > 1 ) {
                         mNum = tok.nextToken();                    // get mNum without extension
                     }

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine member type
                     //
                     if (gender.equals( "" ) || gender.equals( "U" )) {     // if not specified or U (??)

                        gender = "M";               // default to Male
                     }

                     if (primary.equals( "P" )) {

                        if (gender.equals( "M" )) {
                            mtype = "Primary Male";
                        } else {
                            mtype = "Primary Female";
                        }

                     } else {

                        if (gender.equals( "M" )) {
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";
                        }
                     }
                  }
               }         // end of IF club = rhodeisland

               //******************************************************************
               //   Wellesley CC
               //******************************************************************
               //
               if (club.equals( "wellesley" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine member type (mship value plus 'Male' or 'Female' - i.e. 'Golf Male')
                     //
                     if (gender.equals( "F" )) {     // if Female
                         mtype = mship + " Female";
                     } else {
                         mtype = mship + " Male";
                     }
                  }
               }         // end of IF club = wellesley

               //******************************************************************
               //   Lakewood Ranch CC
               //******************************************************************
               //
               if (club.equals( "lakewoodranch" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine member type
                     //
                     if (gender.equals( "" ) || gender.equals( "U" )) {     // if not specified or U (??)

                        gender = "M";               // default to Male
                     }

                     if (mship.equalsIgnoreCase("SMR Members and Staff")) {
                         mship = "SMR Members";
                     }

                     if (primary.equals( "P" )) {

                        if (gender.equals( "M" )) {
                            mtype = "Primary Male";
                        } else {
                            mtype = "Primary Female";
                        }

                     } else {

                        if (gender.equals( "M" )) {
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";
                        }
                     }
                  }
               }         // end of IF club = lakewoodranch

               //******************************************************************
               //   Long Cove CC
               //******************************************************************
               //
               if (club.equals( "longcove" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine member sub-type (MGA or LGA)
                     //
                     msub_type = "";

                     if (!mtype.equals( "" )) {     // if mtype specified (actually is sub-type)

                        msub_type = mtype;          // set it in case they ever need it
                     }

                     //
                     //  determine member type
                     //
                     if (gender.equals( "" ) || gender.equals( "U" )) {     // if not specified or U (??)

                        gender = "M";               // default to Male
                     }

                     if (gender.equals( "M" )) {
                         mtype = "Adult Male";
                     } else {
                         mtype = "Adult Female";
                     }
                  }
               }         // end of IF club = longcove

               //******************************************************************
               //   Bellerive CC
               //******************************************************************
               //
          /*
               if (club.equals( "bellerive" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {                // mship exist ?

                      skip = true;                          // no - skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine member type
                     //
                     if (gender.equals( "" ) || gender.equals( "U" )) {     // if not specified or U (??)

                        if (primary.equalsIgnoreCase( "P" )) {

                           gender = "M";               // default to Male

                        } else {

                           gender = "F";               // default to Female
                        }
                     }

                     if (gender.equals( "M" )) {

                        mtype = "Adult Male";

                     } else {

                        mtype = "Adult Female";
                     }

                     //
                     //  Strip any extra chars from mNum
                     //
                     if (mNum.endsWith( "A" ) || mNum.endsWith( "B" ) || mNum.endsWith( "C" ) ||
                         mNum.endsWith( "D" ) || mNum.endsWith( "E" ) || mNum.endsWith( "F" )) {

                        mNum = stripA(mNum);            // remove the ending 'A'
                     }

                     if (mNum.startsWith( "0" )) {    // if starts with a zero

                        mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member

          //  *** see oahucc and common MF processing below if we use this again *****************

                     webid = memid;                 // use id from MF

                     //
                     //  Set the proper mship type
                     //
                     if (mship.equalsIgnoreCase( "Active" ) || mship.equalsIgnoreCase( "Associate" ) ||
                         mship.equalsIgnoreCase( "Junior" ) || mship.equalsIgnoreCase( "Life" )) {

                        mship = "Golf";
                     }

                     if (mship.equalsIgnoreCase( "Non-Golf" )) {

                        mship = "Non-Golf Senior";
                     }

                     if (mship.equalsIgnoreCase( "Non-Res" )) {

                        mship = "Non-Resident";
                     }

                     if (mship.equalsIgnoreCase( "Spouse" )) {

                        //
                        //  See if we can locate the primary and use his/her mship (else leave as is, it will change next time)
                        //
                        pstmt2 = con.prepareStatement (
                                 "SELECT m_ship FROM member2b WHERE memNum = ? AND webid != ?");

                        pstmt2.clearParameters();
                        pstmt2.setString(1, mNum);
                        pstmt2.setString(2, webid);
                        rs = pstmt2.executeQuery();

                        if(rs.next()) {

                           mship = rs.getString("m_ship");
                        }
                        pstmt2.close();              // close the stmt
                     }
                  }
               }         // end of IF club = bellerive
          */

               //******************************************************************
               //   Peninsula Club
               //******************************************************************
               //
               if (club.equals( "peninsula" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals("")) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                  } else if (!mship.equalsIgnoreCase( "Full" ) && !mship.equalsIgnoreCase( "Sports" ) && !mship.equalsIgnoreCase( "Corp Full" ) &&
                             !mship.equalsIgnoreCase( "Tennis" )) {   // mship ok ?

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  determine member type
                     //
                     if (primary.equalsIgnoreCase( "P" )) {         // if Primary member

                        if (gender.equalsIgnoreCase( "F" )) {
                            mtype = "Primary Female";
                        } else {
                            mtype = "Primary Male";
                        }

                     } else {                                    // Spouse

                        if (gender.equalsIgnoreCase( "M" )) {
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";
                        }
                     }
                  }
               }         // end of IF club = peninsula


               //******************************************************************
               //   Wilmington CC
               //******************************************************************
               //
               if (club.equals( "wilmington" )) {

                  found = true;    // club found

                  //  Make sure this is ok
                  if (mship.equals( "" )) {           // mship missing ?

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";
                  } else {

                     //  Set POS Id in case they ever need it
                     posid = mNum;

                     //  determine membership type and member type
                     if (mship.equalsIgnoreCase( "Child" )) {    // if child

                        mship = "Associate";

                        //   Check if member is 13 or over
                        if (birth > 0) {

                           //  Get today's date and then go back 13 years
                           Calendar cal = new GregorianCalendar();       // get todays date

                           int year = cal.get(Calendar.YEAR);
                           int month = cal.get(Calendar.MONTH) +1;
                           int day = cal.get(Calendar.DAY_OF_MONTH);

                           year = year - 13;                              // go back 13 years

                           int oldDate = (year * 10000) + (month * 100) + day;   // get date

                           if (birth <= oldDate) {     // if member is 13+ yrs old
                              mtype = "Dependent";
                           } else {
                              mtype = "Dependent - U13";
                           }
                        }

                     } else {

                        if (mship.equalsIgnoreCase( "Senior" ) || mship.equalsIgnoreCase( "Associate" ) || mship.equalsIgnoreCase("Clerical")) {      // if Senior or spouse
                            skip = false;                         // ok
                        } else if (mship.endsWith( "Social" )) {      // if any Social
                            mship = "Social";                   // convert all to Social
                        } else if (mship.equalsIgnoreCase( "Senior Special" )) {      // if Senior Special
                            mship = "Associate";                      // convert to Associate
                        } else if (mship.equalsIgnoreCase( "Senior Transfer" )) {      // if Senior Special
                            mship = "Senior";                      // convert to Senior
                        } else {
                            skip = true;                          //  skip this one
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                    "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                        }

                        if (gender.equalsIgnoreCase( "M" )) {
                            mtype = "Adult Male";
                        } else {
                            mtype = "Adult Female";
                        }
                     }

                     //  Check if member has range privileges
                     msub_type = "";            // init

                     if (custom1.equalsIgnoreCase( "range" )) {
                        msub_type = "R";         // yes, indicate this so it can be displayed on Pro's tee sheet
                     }

                  }
               }         // end of IF club = wilmington


               //******************************************************************
               //   Awbrey Glen CC
               //******************************************************************
               //
               if (club.equals( "awbreyglen" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {           // mship missing ?

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (mship.equalsIgnoreCase("Employee")) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Set mtype
                     //
                     if (gender.equals( "" )) {            // if gender not specified

                        gender = "M";                      // default to Male

                        if (primary.equalsIgnoreCase( "S" )) {     // if Spouse

                           gender = "F";                           // assume Female
                        }
                     }

                     if (gender.equalsIgnoreCase( "F" )) {
                         mtype = "Adult Female";
                     } else {
                         mtype = "Adult Male";
                     }
                  }
               }         // end of IF club = awbreyglen


               //******************************************************************
               //   The Pinery CC
               //******************************************************************
               //
               if (club.equals( "pinery" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;          // skip it
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (mship.equalsIgnoreCase( "Tennis Dues" ) || mship.equalsIgnoreCase( "Social Dues" ) ||
                      mship.equalsIgnoreCase( "Premier Tennis Dues" ) || mship.equalsIgnoreCase( "Premier Social Prepaid" ) ||
                      mship.equalsIgnoreCase( "Premier Social Dues" ) || mship.equalsIgnoreCase( "Premier Dining Dues" ) ||
                      mship.equalsIgnoreCase( "Dining Dues" )) {

                      skip = true;                //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Make sure mship is titled
                     //
                     mship = toTitleCase(mship);

                     //
                     //  Set mtype
                     //
                     if (primary.equals( "" )) {            // if primary not specified

                        primary = "P";                      // default to Primary
                     }

                     if (gender.equals( "" )) {            // if gender not specified

                        gender = "M";                      // default to Male

                        if (primary.equalsIgnoreCase( "S" )) {     // if Spouse

                           gender = "F";                           // assume Female
                        }
                     }

                     if (primary.equalsIgnoreCase( "S" )) {          // if Spouse

                        if (gender.equalsIgnoreCase( "M" )) {        // if Male
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";                  // default Spouse
                        }

                     } else {                            // Primary

                        if (gender.equalsIgnoreCase( "F" )) {        // if Female
                            mtype = "Primary Female";
                        } else {
                            mtype = "Primary Male";             // default Primary
                        }
                     }

                  }
               }         // end of IF club = pinery



               //******************************************************************
               //   The Country Club
               //******************************************************************
               //
               if (club.equals( "tcclub" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else {

                     if (mship.startsWith( "65 & above " )) {

                        mship = "65 & Above Exempt";          // remove garbage character
                     }

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Set mtype
                     //
                     if (primary.equals( "" )) {            // if primary not specified

                        primary = "P";                      // default to Primary
                     }

                     if (gender.equals( "" )) {            // if gender not specified

                        gender = "M";                      // default to Male
                     }

                     if (primary.equalsIgnoreCase( "S" )) {          // if Spouse

                        if (gender.equalsIgnoreCase( "M" )) {        // if Male
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";                  // default Spouse
                        }

                     } else {                            // Primary

                        if (gender.equalsIgnoreCase( "F" )) {        // if Female
                            mtype = "Primary Female";
                        } else {
                            mtype = "Primary Male";             // default Primary
                        }
                     }

                     //
                     //  Check for dependents
                     //
                     if (mNum.endsWith("-2") || mNum.endsWith("-3") || mNum.endsWith("-4") || mNum.endsWith("-5") ||
                         mNum.endsWith("-6") || mNum.endsWith("-7") || mNum.endsWith("-8") || mNum.endsWith("-9")) {
                        if (gender.equalsIgnoreCase( "F ")) {
                            mtype = "Dependent Female";
                        } else {
                            mtype = "Dependent Male";
                        }
                     }

                  }
               }         // end of IF club = tcclub

               //******************************************************************
               //   Navesink Country Club
               //******************************************************************
               //
               if (club.equals( "navesinkcc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok - mship is in the mtype field for this club!!!!!!!!
                  //
                  if (mtype.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                  } else {

                     useWebid = true;
                     webid = memid;
                     memid = mNum;      // use mNum for username, they are unique!


                     if (mtype.startsWith( "Member Type" )) {

                        mship = mtype.substring(12);          // remove garbage character

                     } else {
                        mship = mtype;
                     }

                     if (mship.equalsIgnoreCase("SS") || mship.equalsIgnoreCase("SSQ") || mship.equalsIgnoreCase("WMF") ||
                         mship.equalsIgnoreCase("WMS") || mship.equalsIgnoreCase("HQ") || mship.equalsIgnoreCase("HM") ||
                         mship.equalsIgnoreCase("H") || mship.equalsIgnoreCase("HL") || mship.equalsIgnoreCase("HR") ||
                         mship.equalsIgnoreCase("JSQ") || mship.equalsIgnoreCase("SHL") || mship.equalsIgnoreCase("SP") ||
                         mship.equalsIgnoreCase("SPJ") || mship.equalsIgnoreCase("SPQ")) {

                         skip = true;                //  skip this one
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                     } else {

                         StringTokenizer navTok = null;

                         // check Primary/Spouse
                         if (mNum.endsWith("-1")) {
                             primary = "S";
                             mNum = mNum.substring(0, mNum.length() - 2);

                         } else if (mNum.endsWith("-2") || mNum.endsWith("-3") || mNum.endsWith("-4") ||
                                    mNum.endsWith("-5") || mNum.endsWith("-6") || mNum.endsWith("-7") ||
                                    mNum.endsWith("-8") || mNum.endsWith("-9")) {
                             primary = "D";
                             mNum = mNum.substring(0, mNum.length() - 2);
                         } else {
                             primary = "P";
                         }

                         //
                         //  Set POS Id in case they ever need it
                         //
                         posid = mNum;

                         if (primary.equalsIgnoreCase( "S" )) {          // if Spouse

                            if (gender.equalsIgnoreCase( "M" )) {        // if Male
                                mtype = "Spouse Male";
                            } else {
                                mtype = "Spouse Female";                  // default Spouse
                            }

                         } else if (primary.equalsIgnoreCase( "D" )) {

                             //
                             //  Dependent mtype based on age
                             //
                             Calendar cal = new GregorianCalendar();       // get todays date

                             int year = cal.get(Calendar.YEAR);
                             int month = cal.get(Calendar.MONTH) +1;
                             int day = cal.get(Calendar.DAY_OF_MONTH);

                             year = year - 18;             // backup 18 years

                             int oldDate = (year * 10000) + (month * 100) + day;   // get date

                             if (birth > oldDate || birth == 0) {       // dependent is under 18 or no bday provided

                                 mtype = "Dependent Under 18";
                             } else {
                                 mtype = "Dependent 18-24";
                             }

                         } else {                            // Primary

                            if (gender.equalsIgnoreCase( "F" )) {        // if Female
                                mtype = "Primary Female";
                            } else {
                                mtype = "Primary Male";             // default Primary
                            }
                         }

                         if (mship.equalsIgnoreCase("SR") || mship.equalsIgnoreCase("SRQ")) {
                             mship = "SR";
                         } else if (mship.equalsIgnoreCase("JR") || mship.equalsIgnoreCase("JRQ")) {
                             mship = "JR";
                         } else if (mship.equalsIgnoreCase("NR") || mship.equalsIgnoreCase("NRQ")) {
                             mship = "NR";
                         } else if (mship.equalsIgnoreCase("R") || mship.equalsIgnoreCase("RQ")) {
                             mship = "R";
                         }
                     }
                  }
               }         // end of IF club = navesinkcc


               //******************************************************************
               //   The International
               //******************************************************************
               //
               if (club.equals( "international" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      mship = "Unknown";              // allow for missing mships for now
                  }

                  //
                  //  Set POS Id in case they ever need it
                  //
                  posid = mNum;

                  //
                  //  Set mtype
                  //
                  if (gender.equalsIgnoreCase( "F" )) {        // if Female
                      mtype = "Primary Female";
                  } else {
                      mtype = "Primary Male";             // default Primary
                  }

                  //
                  //  Set mship
                  //
                  if (mship.equals( "Other" ) || mship.equals( "Social" ) || mship.startsWith( "Spouse of" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                  } else {

                     if (mship.startsWith( "Corporate" ) || mship.startsWith( "ITT" )) {
                        mship = "Corporate";
                     } else if (mship.equals( "Individual" ) || mship.endsWith( "Individual" ) || mship.startsWith( "Honorary" ) ||
                             mship.startsWith( "Owner" ) || mship.equals( "Trade" )) {
                        mship = "Individual";
                     } else if (mship.startsWith( "Family" ) || mship.startsWith( "Senior Family" )) {
                        mship = "Family";
                     } else if (mship.startsWith( "Out of Region" )) {
                        mship = "Out of Region";
                     } else if (mship.startsWith( "Associat" )) {
                        mship = "Associate";
                     } else if (mship.startsWith( "Staff" )) {
                        mship = "Staff";
                     }

                  }
               }         // end of IF club = international


               //******************************************************************
               //   Blue Hill
               //******************************************************************
               //
               if (club.equals( "bluehill" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok - mship is in the mtype field for this club!!!!!!!!
                  //
                  if (mtype.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";
                  } else {

                     mship = mtype;                                // move over

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Set mtype
                     //
                     if (primary.equalsIgnoreCase( "S" )) {        // if Spouse

                        if (gender.equalsIgnoreCase( "M" )) {        // if Male
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";
                        }

                     } else {                                 // must be Primary

                        if (gender.equalsIgnoreCase( "F" )) {        // if Female
                            mtype = "Primary Female";
                        } else {
                            mtype = "Primary Male";             // default Primary
                        }
                     }

                     //
                     //  Set mship
                     //
                     if (mship.startsWith( "Social" )) {
                         mship = "Social Golf";
                     } else if (mship.startsWith( "Associat" )) {
                         mship = "Associate";
                     } else if (mship.startsWith( "Corporate" )) {
                         mship = "Corporate";
                     } else if (!mship.equals( "Junior" )) {
                         mship = "Regular Golf";             // all others (Junior remains as Junior)
                     }
                  }
               }         // end of IF club = bluehill


               //******************************************************************
               //   Oak Lane
               //******************************************************************
               //
               if (club.equals( "oaklane" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok - mship is in the mtype field for this club!!!!!!!!!!!!!!!!!
                  //
                  if (mtype.equals( "" )) {

                      skip = true;              // skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing or not allowed! (*NOTE* mship located in mtype field)";
                  } else {

                     mship = mtype;                                // move over

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Remove '-n' from mNum
                     //
                     tok = new StringTokenizer( mNum, "-" );     // delimiters are space

                     if ( tok.countTokens() > 1 ) {
                         mNum = tok.nextToken();                     // get main mNum
                     }

                     //
                     //  Set mtype
                     //
                     if (primary.equalsIgnoreCase( "S" )) {        // if Spouse

                        if (gender.equalsIgnoreCase( "M" )) {        // if Male
                            mtype = "Spouse Male";
                        } else {
                            mtype = "Spouse Female";
                        }

                     } else {                                 // must be Primary

                        if (gender.equalsIgnoreCase( "F" )) {        // if Female
                            mtype = "Primary Female";
                        } else {
                            mtype = "Primary Male";             // default Primary
                        }
                     }

                     //
                     //  Set mship
                     //
                     if (mship.startsWith( "Senior Social" ) || mship.startsWith( "Social" )) {
                         mship = "Social";
                     } else if (mship.startsWith( "Senior Tennis" ) || mship.startsWith( "Summer Tennis" ) || mship.startsWith( "Tennis" )) {
                         mship = "Tennis";
                     }
                  }
               }         // end of IF club = oaklane


               //******************************************************************
               //   Green Hills
               //******************************************************************
               //
               if (club.equals( "greenhills" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok - mship is in the mtype field for this club!!!!!!!!!!!!!!!!!
                  //
                  if (mtype.equals( "" )) {

                      skip = true;              // skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";
                  } else {

                     mship = mtype;                                // move over

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Set mtype
                     //
                     mtype = "Primary Male";             // default Primary

                     if (mNum.endsWith( "-1" )) {        // if Spouse
                         mtype = "Spouse Female";
                     }

                     //
                     //  Remove '-n' from mNum
                     //
                     tok = new StringTokenizer( mNum, "-" );     // delimiters are space

                     if ( tok.countTokens() > 1 ) {
                         mNum = tok.nextToken();                     // get main mNum
                     }
                  }
               }         // end of IF club = greenhills


               //******************************************************************
               //   Oahu CC
               //******************************************************************
               //
               if (club.equals( "oahucc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF
                     memid = mNum;                  // use mNum for username (each is unique) - if new member!!!
                                                    //   mNum can change so we can't count on this being the username for existing members!!

                     // This family has a last name consisting of 3 words.  If them, just plug in the correct name
                     if (memid.equals("4081") || memid.equals("4081-1")) {
                         lname = "de_los_Reyes";
                     }

                     //
                     //  Convert some mships
                     //
                     if (mship.equalsIgnoreCase( "Surviving Spouse 50 Year Honorary" )) {
                        mship = "Surv Sp 50 Year Honorary";
                     } else if (mship.equalsIgnoreCase( "Surviving Spouse - Non-Resident" )) {
                        mship = "Surviving Spouse Non-Resident";
                     } else if (mship.equalsIgnoreCase( "Surviving Spouse Non-Resident - Golf" )) {
                        mship = "Surv Sp Non-Resident - Golf";
                     } else if (mship.equalsIgnoreCase( "Surviving Spouse Super Senior - Golf" )) {
                        mship = "Surv Sp Super Senior - Golf";
                     } else if (mship.equalsIgnoreCase( "Surviving Spouse Super Senior - Social" )) {
                        mship = "Surv Sp Super Senior - Social";
                     }

                     //
                     //  Set mtype
                     //
                     if (mship.startsWith("Surv") || mship.equalsIgnoreCase("SS50")) {                  // if Surviving Spouse

                        mtype = "Spouse";                             // always Spouse

                     } else {

                        if (primary.equalsIgnoreCase( "S" )) {        // if spouse
                            mtype = "Spouse";
                        } else {
                            mtype = "Primary";                     // default to Primary
                        }
                     }

                     //
                     //  Check for Junior Legacy members last
                     //
                     if (mship.startsWith( "Jr" )) {

                        mship = "Junior Legacy";
                        mtype = "Spouse";
                     }
                  }
               }         // end of IF club = oahucc



               //******************************************************************
               //   Ballantyne CC
               //******************************************************************
               //
               if (club.equals( "ballantyne" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF
                     memid = mNum;                  // use mNum for username (each is unique) - if new member!!!
                                                    //   mNum can change so we can't count on this being the username for existing members!!

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero
                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     tok = new StringTokenizer( mNum, "-" );

                     if ( tok.countTokens() > 1 ) {
                         mNum = tok.nextToken();
                     }

                     //
                     //  Convert some mships              ************** finish this!!!!!!!!!  ************
                     //
                     if (mtype.equalsIgnoreCase( "Full Golf" ) || mship.equalsIgnoreCase("Trial Member - Golf")) {

                        mship = "Golf";
                        skip = false;

                     } else if (mtype.equalsIgnoreCase( "Limited Golf" ) || mship.equalsIgnoreCase( "Master Member" ) || mship.equalsIgnoreCase("Trial Sports Membership")) {

                        mship = "Sports";
                        skip = false;

                     } else {

                        skip = true;
                        warnCount++;
                        warnMsg = warnMsg + "\n" +
                                "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                     }

                     //
                     //  Set mtype  ??????????            **************** and this !!!!  **************
                     //
                     if (primary.equalsIgnoreCase( "S" )) {        // if spouse
                        mtype = "Adult Female";
                     } else {
                        mtype = "Adult Male";                     // default to Primary
                     }
                  }
               }  // end of IF club is ballantyne


               //******************************************************************
               //   Troon CC
               //******************************************************************
               //
               if (club.equals( "trooncc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero
                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;                  // use mNum for username (each is unique) - if new member!!!
                                                    //   mNum can change so we can't count on this being the username for existing members!!

                     tok = new StringTokenizer( mNum, "-" );

                     if ( tok.countTokens() > 1 ) {
                         mNum = tok.nextToken();
                     }

                     if (mship.equalsIgnoreCase("Golf") || mship.equalsIgnoreCase("Intermediate Golf") ||
                         mship.equalsIgnoreCase("Social to Golf Upgrade") || mship.equalsIgnoreCase("Founding")) {
                         mship = "Golf";
                     } else if (mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Social from Golf")) {
                         mship = "Social";
                     } else if (!mship.equalsIgnoreCase("Senior") && !mship.equalsIgnoreCase("Dependent")) {
                         skip = true;
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                     }

                     try {
                         if (mship.equalsIgnoreCase("Dependent") && (mNum.endsWith("A") || mNum.endsWith("a") || mNum.endsWith("B") || mNum.endsWith("b"))) {
                             String mNumTemp = mNum.substring(0, mNum.length() - 1);
                             PreparedStatement pstmtTemp = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");

                             pstmtTemp.clearParameters();
                             pstmtTemp.setString(1, mNumTemp);

                             ResultSet rsTemp = pstmtTemp.executeQuery();

                             if (rsTemp.next()) {
                                 mship = rsTemp.getString("m_ship");
                             }

                             pstmtTemp.close();
                         }
                     } catch (Exception exc) {
                         mship = "Unknown";
                     }

                     //
                     //  Set mtype
                     //
                     if (gender.equalsIgnoreCase( "F" )) {        // if spouse
                         mtype = "Adult Female";
                     } else {
                         mtype = "Adult Male";                     // default to Primary
                     }
                  }
               }  // end of IF club = trooncc


               //******************************************************************
               //   Imperial GC
               //******************************************************************
               //
               if (club.equals( "imperialgc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (mship.startsWith( "Account" ) || mship.equals( "Dining Full" ) ||
                      mship.equals( "Dining Honorary" ) || mship.equals( "Dining Single" ) ||
                      mship.equals( "Resigned" ) || mship.equals( "Suspended" )) {

                      skip = true;
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                  } else {
                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     //useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero
                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;                  // use mNum for username (each is unique) - if new member!!!
                                                    //   mNum can change so we can't count on this being the username for existing members!!

                     tok = new StringTokenizer( mNum, "-" );

                     if ( tok.countTokens() > 1 ) {

                        mNum = tok.nextToken();
                     }


                     //
                     //  Convert some mships
                     //
                     if (mship.startsWith( "Associate" )) {
                        mship = "Associate";
                     } else if (mship.startsWith( "Dining Summer" )) {
                        mship = "Dining Summer Golf";
                     } else if (mship.startsWith( "Golf Royal" )) {
                        mship = "Golf Royal";
                     } else if (mship.startsWith( "Golf Single" ) || mship.equalsIgnoreCase("RISINGLE")) {
                        mship = "Golf Single";
                     } else if (mship.equalsIgnoreCase("RIMEMBER")) {
                        mship = "Golf Full";
                     } else if (mship.startsWith( "Limited Convert" )) {
                        mship = "Limited Convertible";
                     } else if (mship.startsWith( "Resigned" )) {
                        mship = "Resigned PCD";
                     }

                     //
                     //  Set mtype
                     //
                     if (gender.equals( "" )) {

                        gender = "M";

                        if (primary.equalsIgnoreCase( "S" )) {        // if spouse

                           gender = "F";
                        }
                     }

                     if (gender.equalsIgnoreCase( "F" )) {
                         mtype = "Adult Female";
                     } else {
                         mtype = "Adult Male";                     // default to Primary
                     }
                  }
               }  // end of IF club = imperialgc



            /*    Disable - Pro wants to maintain roster himself !!!
             *
               //******************************************************************
               //   Hop Meadow GC
               //******************************************************************
               //
               if (club.equals( "hopmeadowcc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member

                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero

                        mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;                  // use mNum for username (each is unique) - if new member!!!

                     tok = new StringTokenizer( mNum, "-" );

                     if ( tok.countTokens() > 1 ) {

                        mNum = tok.nextToken();                     // keep mnum same for all family members
                     }


                     //
                     //  Convert some mships
                     //
                     if (mship.startsWith( "Sporting" )) {

                        mship = "Sporting Member";

                     } else {

                        if (mship.startsWith( "Dependent" )) {

                           mship = "Dependents";

                        } else {

                          if (mship.startsWith( "Non-Resid" )) {

                              mship = "Non Resident";

                          } else {

                             if (mship.equals( "Clergy" ) || mship.startsWith( "Full Privilege" ) ||
                                 mship.equals( "Honorary" ) || mship.startsWith( "Retired Golf" )) {

                                 mship = "Full Privilege Golf";

                             } else if (mship.equals("Senior")) {

                                 // let Senior come through as-is

                             } else {

                                skip = true;        // skip all others
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                             }
                          }
                        }
                     }

                     //
                     //  Set mtype
                     //
                     if (gender.equals( "" )) {

                        gender = "M";
                     }

                     if (primary.equalsIgnoreCase( "S" )) {        // if spouse

                        mtype = "Spouse Female";

                        if (!gender.equalsIgnoreCase( "F")) {

                           mtype = "Spouse Male";
                        }

                     } else {

                        mtype = "Primary Male";

                        if (gender.equalsIgnoreCase( "F" )) {

                           mtype = "Primary Female";
                        }
                     }
                  }
               }  // end of IF club = hopmeadowcc
             */



               //******************************************************************
               //   Bentwater Yacht & CC
               //******************************************************************
               //
               if (club.equals( "bentwaterclub" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero

                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;                  // use mNum for username (each is unique) - if new member!!!

                     //
                     //  Convert the mships ("Member Type:xxx" - just take the xxx)
                     //
                     tok = new StringTokenizer( mship, ":" );

                     if ( tok.countTokens() > 1 ) {

                        mship = tok.nextToken();       // skip 1st part
                        mship = tok.nextToken();       //  get actual mship
                     }


                     //
                     //  Set mtype
                     //
                     mtype = "Member";                   // same for all
                  }
               }  // end of IF club = bentwaterclub


               //******************************************************************
               //   Sharon Heights
               //******************************************************************
               //
               if (club.equals( "sharonheights" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else {

                     //
                     //  Make sure mship is titled
                     //
                     mship = toTitleCase(mship);

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member

                     webid = memid;                 // use id from MF


                     //
                     //  Set POS Id - leave leading zeros, but strip trailing alpha!!
                     //
                     if (!mNum.endsWith( "0" ) && !mNum.endsWith( "1" ) && !mNum.endsWith( "2" ) && !mNum.endsWith( "3" ) &&
                         !mNum.endsWith( "4" ) && !mNum.endsWith( "5" ) && !mNum.endsWith( "6" ) && !mNum.endsWith( "7" ) &&
                         !mNum.endsWith( "8" ) && !mNum.endsWith( "9" )) {

                        posid = stripA(mNum);           // remove trailing alpha for POSID
                     }


                     //
                     //  Strip any leading zeros and from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero

                        mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;       // use mNum w/o zeros for username (each is unique - MUST include the trailing alpha!!)

                     //
                     //  Set mtype
                     //
                     mtype = "Adult Male";                 // default

                     if (!gender.equals("")) {

                        if (gender.equalsIgnoreCase("F")) {

                           mtype = "Adult Female";
                        }

                     } else {

                        if (mNum.endsWith("B") || mNum.endsWith("b")) {

                           mtype = "Adult Female";
                        }
                     }

                     //
                     //  Now remove the trainling alpha from mNum
                     //
                     if (!mNum.endsWith( "0" ) && !mNum.endsWith( "1" ) && !mNum.endsWith( "2" ) && !mNum.endsWith( "3" ) &&
                         !mNum.endsWith( "4" ) && !mNum.endsWith( "5" ) && !mNum.endsWith( "6" ) && !mNum.endsWith( "7" ) &&
                         !mNum.endsWith( "8" ) && !mNum.endsWith( "9" )) {

                        mNum = stripA(mNum);           // remove trailing alpha
                     }
                  }
               }  // end of IF club = sharonheights


/*
               //******************************************************************
               //   Bald Peak
               //******************************************************************
               //
               if (club.equals( "baldpeak" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else {

                     //
                     //  Make sure mship is titled
                     //
                     mship = toTitleCase(mship);

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use MF's memid
                     memid = mNum;                  // use mNum for username (each is unique)


                     if (!mNum.endsWith( "0" ) && !mNum.endsWith( "1" ) && !mNum.endsWith( "2" ) && !mNum.endsWith( "3" ) &&
                         !mNum.endsWith( "4" ) && !mNum.endsWith( "5" ) && !mNum.endsWith( "6" ) && !mNum.endsWith( "7" ) &&
                         !mNum.endsWith( "8" ) && !mNum.endsWith( "9" )) {

                        mNum = stripA(mNum);           // remove trailing alpha
                     }

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;


                     //
                     //  Set mtype
                     //
                     mtype = "Adult Male";                 // default

                     if (!gender.equals("")) {

                        if (gender.equalsIgnoreCase("F")) {

                           mtype = "Adult Female";
                        }

                     } else {

                        if (primary.equalsIgnoreCase("S")) {

                           mtype = "Adult Female";
                           gender = "F";
                        }
                     }
                  }
               }  // end of IF club = baldpeak
*/

               //******************************************************************
               //   Mesa Verde CC
               //******************************************************************
               //
               if (club.equals( "mesaverdecc" )) {

                  found = true;         // club found

                  //
                  //  Make sure mship is titled
                  //
                  mship = toTitleCase(mship);

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (mship.startsWith( "Social" )) {

                      skip = true;
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else if (fname.equalsIgnoreCase( "survey" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Survey' FIRST NAME!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero
                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;                  // use mNum for username (each is unique)

                     //
                     //  Set mtype
                     //
                     mtype = "Member";                 // default

                     if (!gender.equals("")) {

                        if (gender.equalsIgnoreCase("F")) {

                           mtype = "Auxiliary";
                        }

                     } else {

                        if (primary.equalsIgnoreCase("S")) {

                           mtype = "Auxiliary";
                           gender = "F";
                        }
                     }
                  }
               }  // end of IF club = mesaverdecc


               //******************************************************************
               //   Portland CC
               //******************************************************************
               //
               if (club.equals( "portlandcc" )) {

                  found = true;         // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else if (fname.equalsIgnoreCase( "survey" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Survey' FIRST NAME!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero
                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;                  // use mNum for username (each is unique)

                     //
                     //  Set mtype
                     //
                     if (gender.equalsIgnoreCase("F")) {

                        if (primary.equalsIgnoreCase("P")) {
                            mtype = "Primary Female";
                        } else {
                            mtype = "Spouse Female";
                        }

                     } else {

                        if (primary.equalsIgnoreCase("P")) {
                            mtype = "Primary Male";
                        } else {
                            mtype = "Spouse Male";
                        }
                     }

                     //
                     //  Set mship
                     //
                     if (mship.endsWith("10") || mship.endsWith("11") || mship.endsWith("12") || mship.endsWith("14") ||
                         mship.endsWith("15") || mship.endsWith("16") || mship.endsWith("17")) {

                        mship = "Active";

                     } else if (mship.endsWith("20") || mship.endsWith("21")) {
                         mship = "Social";
                     } else  if (mship.endsWith("30") || mship.endsWith("31")) {
                         mship = "Senior Active";
                     } else if (mship.endsWith("40") || mship.endsWith("41")) {
                         mship = "Junior Active";
                     } else if (mship.endsWith("18") || mship.endsWith("19")) {
                         mship = "Junior Social";
                     } else if (mship.endsWith("50") || mship.endsWith("51") || mship.endsWith("60")) {
                         mship = "Unattached";
                     } else if (mship.endsWith("78") || mship.endsWith("79")) {
                         mship = "Social Non-Resident";
                     } else if (mship.endsWith("80") || mship.endsWith("81")) {
                         mship = "Active Non-Resident";
                     } else if (mship.endsWith("70") || mship.endsWith("71") || mship.endsWith("72")) {
                         mship = "Spousal";
                     } else {

                         skip = true;                          //  skip this one
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                     }
                  }
               }  // end of IF club = portlandcc


               /*
               //******************************************************************
               //   Dorset FC   -   The Pro does not want to use RS - he will maintain the roster
               //******************************************************************
               //
               if (club.equals( "dorsetfc" )) {

                  found = true;         // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else if (fname.equalsIgnoreCase( "survey" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Survey' FIRST NAME!";
                  } else {

                     if (!mship.endsWith( ":5" ) && !mship.endsWith( ":9" ) && !mship.endsWith( ":17" ) && !mship.endsWith( ":21" )) {

                         skip = true;                          //  skip this one
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                     } else {

                        //
                        //  Set POS Id in case they ever need it
                        //
                        posid = mNum;

                        //
                        //  use memid as webid !! (do NOT change the username in records)
                        //
                        useWebid = true;               // use webid to locate member

                        webid = memid;                 // use id from MF

                        //
                        //  Strip any leading zeros and extension from mNum
                        //
                        while (mNum.startsWith( "0" )) {    // if starts with a zero

                           mNum = remZeroS(mNum);           // remove the leading zero
                        }

                        memid = mNum;                  // use mNum for username (each is unique)


                        //
                        //  Set mtype
                        //
                        mtype = "Adult Male";                 // default

                        if (gender.equalsIgnoreCase("F")) {

                           mtype = "Adult Female";
                        }

                        //
                        //  Set mship
                        //
                        mship = "Family Full";
                     }
                  }
               }  // end of IF club = dorsetfc
                */


               //******************************************************************
               //   Baltusrol GC
               //******************************************************************
               //
               if (club.equals( "baltusrolgc" )) {

                  found = true;         // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else if (fname.equalsIgnoreCase( "survey" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Survey' FIRST NAME!";
                  } else {

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member

                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero

                        mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;                  // use mNum for username (each is unique)


                     //
                     //  Set mtype
                     //
                     if (memid.endsWith("-1")) {                   // if Spouse

                        if (gender.equalsIgnoreCase("M")) {

                           mtype = "Spouse Male";

                        } else {

                           mtype = "Spouse Female";

                           fname = "Mrs_" + fname;         // change to Mrs....
                        }

                     } else {                          // Primary

                        if (gender.equalsIgnoreCase("F")) {

                           mtype = "Primary Female";

                        } else {

                           mtype = "Primary Male";
                        }
                     }

                     //
                     //  Set the mship type
                     //
                     if (mship.startsWith("Junior")) {         // Funnel anything starting with 'Junior' to be simply "Junior"

                         mship = "Junior";

                     } else if (mship.equalsIgnoreCase("Staff") || mship.startsWith("Type")) {

                         skip = true;                          //  skip this one
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                     }
                  }
               }  // end of IF club = baultusrolcc




               //******************************************************************
               //   The Club at Pradera
               //******************************************************************
               //
               if (club.equals( "pradera" )) {

                  found = true;         // club found

                  //
                  //  Make sure this is ok
                  //
                  if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else if (fname.equalsIgnoreCase( "survey" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Survey' FIRST NAME!";
                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";
                  } else {

                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero
                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     memid = mNum;                  // use mNum for username (each is unique)

                     //
                     //  Set mtype
                     //
                     if (mNum.endsWith("-1")) {                  // if Spouse
                         mNum = mNum.substring(0, mNum.length()-2);
                         if (!genderMissing && gender.equalsIgnoreCase("M")) {
                             mtype = "Spouse Male";
                         } else {
                             mtype = "Spouse Female";
                             gender = "F";
                         }
                     } else {
                         if (gender.equalsIgnoreCase("F")) {
                             mtype = "Primary Spouse";
                         } else {
                             mtype = "Primary Male";
                         }
                     }

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Set the mship type
                     //
                     mship = "Golf";               // default

                     if (mNum.startsWith("I")) {
                         mship = "Invitational";
                     } else if (mNum.startsWith("P")) {
                         mship = "Prestige";
                     } else if (mNum.startsWith("F")) {
                         mship = "Founding";
                     } else if (mNum.startsWith("J")) {
                         mship = "Junior Executive";
                     } else if (mNum.startsWith("C")) {
                         mship = "Corporate";
                     } else if (mNum.startsWith("H")) {
                         mship = "Honorary";
                     } else if (mNum.startsWith("Z")) {
                         mship = "Employee";
                     } else if (mNum.startsWith("S")) {
                         mship = "Sports";
                     } else if (mNum.startsWith("L") || mNum.startsWith("l")) {
                         mship = "Lifetime";
                     } else if (mNum.startsWith("X")) {

                         skip = true;                          //  skip this one
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                     }
                  }
               }  // end of IF club = pradera



               //******************************************************************
               //   Scarsdale GC
               //******************************************************************
               //
               if (club.equals( "scarsdalegolfclub" )) {

                  found = true;         // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else if (fname.equalsIgnoreCase( "survey" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Survey' FIRST NAME!";
                  } else if (fname.equalsIgnoreCase( "admin" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Admin' FIRST NAME!";
                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";
                  } else {
                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero

                        mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     if (!mNum.endsWith("-1")) {

                         memid = mNum;                  // use mNum for username

                     } else {

                        tok = new StringTokenizer( mNum, "-" );

                        if ( tok.countTokens() > 1 ) {
                            mNum = tok.nextToken();        // isolate mnum
                        }

                        memid = mNum + "A";
                     }

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Set mtype
                     //
                     if (primary.equalsIgnoreCase("S")) {

                        if (gender.equalsIgnoreCase("F")) {
                            mtype = "Spouse Female";
                        } else {
                            mtype = "Spouse Male";
                        }

                     } else {

                        if (gender.equalsIgnoreCase("F")) {
                            mtype = "Primary Female";
                        } else {
                            mtype = "Primary Male";
                        }
                     }

                     //
                     //  Set mship
                     //
                     if (mship.endsWith("egular")) {       // catch all Regulars
                         mship = "Regular";
                     } else if (mship.endsWith("ssociate")) {       // catch all Associates
                         mship = "Associate";
                     } else if (mship.endsWith("Social")) {       // catch all Socials
                         mship = "House Social";
                     } else if (mship.endsWith("Sports")) {      // catch all Sports
                         mship = "House Sports";
                     } else if (mship.equals("P") || mship.equals("Privilege")) {
                         mship = "Privilege";
                     } else if (!mship.equals("Special Visitors") && !mship.equals("Non-Resident") && !mship.equals("Honorary")) {

                         skip = true;                          //  skip if not one of the above
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                     }
                  }
               }  // end of IF club is Scarsdale



               //******************************************************************
               //   Patterson Club
               //******************************************************************
               //
               if (club.equals( "pattersonclub" )) {

                  found = true;         // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else if (fname.equalsIgnoreCase( "survey" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Survey' FIRST NAME!";
                  } else if (fname.equalsIgnoreCase( "admin" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Admin' FIRST NAME!";
                  } else if (mship.startsWith( "DS" ) || mship.startsWith( "Employee" ) || mship.equals( "LOA" ) ||
                      mship.equals( "Resigned" ) || mship.equals( "Honorary" )) {

                      skip = true;                      // skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";
                  } else {
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member

                     webid = memid;                 // use id from MF


                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;


                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero

                        mNum = remZeroS(mNum);           // remove the leading zero
                     }


                     memid = mNum;                  // use mNum for username


                     tok = new StringTokenizer( mNum, "-" );

                     /*
                     if ( tok.countTokens() > 1 ) {

                        mNum = tok.nextToken();        // isolate mnum

                        if (memid.endsWith("-1")) {

                           memid = mNum + "A";                 // use nnnA

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Spouse Female";
                           } else {
                               mtype = "Spouse Male";
                           }

                        } else {
                            mtype = "Dependent";
                        }

                     } else {

                        if (gender.equalsIgnoreCase("F")) {
                            mtype = "Primary Female";
                        } else {
                            mtype = "Primary Male";
                        }
                     }
                     */

                     if (mNum.endsWith("A")) {
                         if (gender.equalsIgnoreCase("F")) {
                             mtype = "Spouse Female";
                         } else {
                             mtype = "Spouse Male";
                         }
                     } else {
                         if (gender.equalsIgnoreCase("F")) {
                             mtype = "Primary Female";
                         } else {
                             mtype = "Primary Male";
                         }
                     }


                     //
                     //  Set mship - these do not exist any longer, but leave just in case
                     //
                     if (mship.equalsIgnoreCase("FP-Intermediate")) {
                         mship = "FP";
                     } else {
                         if (mship.equalsIgnoreCase("HTP Intermediate")) {
                             mship = "HTP";
                         }
                     }       // Accept others as is
                  }
               }  // end of IF club = pattersonclub


               //******************************************************************
               //   Tamarack
               //******************************************************************
               //
               if (club.equals( "tamarack" )) {

                  found = true;         // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  } else if (fname.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -FIRST NAME missing!";
                  } else if (fname.equalsIgnoreCase( "survey" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Survey' FIRST NAME!";
                  } else if (fname.equalsIgnoreCase( "admin" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: 'Admin' FIRST NAME!";
                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";
                  } else {
                     //
                     //  use memid as webid !! (do NOT change the username in records)
                     //
                     useWebid = true;               // use webid to locate member
                     webid = memid;                 // use id from MF

                     //
                     //  Strip any leading zeros and extension from mNum
                     //
                     while (mNum.startsWith( "0" )) {    // if starts with a zero
                         mNum = remZeroS(mNum);           // remove the leading zero
                     }

                     //
                     //  Set username to mNum (it is unique)
                     //
                     memid = mNum;

                     //
                     //  Remove extension from mNum if not primary
                     //
                     StringTokenizer tok9 = new StringTokenizer( mNum, "-" );     // look for a dash (i.e.  1234-1)

                     if ( tok9.countTokens() > 1 ) {    

                        mNum = tok9.nextToken();       // get just the mNum if it contains an extension
                     }

                     /*
                     if (!primary.equalsIgnoreCase("P")) {
                         mNum = stripA(mNum);
                     }
                      */

                     //
                     //  Set POS Id in case they ever need it
                     //
                     posid = mNum;

                     //
                     //  Set mtype
                     //
                     if (mship.startsWith("SP-")) {

                         if (gender.equalsIgnoreCase("F")) {
                             mtype = "Spouse Female";
                         } else {
                             mtype = "Spouse Male";
                         }

                         mship = mship.substring(3);

                     } else if (mship.startsWith("DEP-")) {

                         mtype = "Dependent";

                         mship = mship.substring(4);

                     } else {

                         if (gender.equalsIgnoreCase("F")) {
                             mtype = "Primary Female";
                         } else {
                             mtype = "Primary Male";
                         }
                     }

                     if (memid.contains("-")) {
                         memid = memid.substring(0, memid.length() - 2) + memid.substring(memid.length() - 1);
                     }
                    

                     //
                     //  Set mship
                     //
                     if (mship.equalsIgnoreCase("Associate") || mship.equalsIgnoreCase("Corporate") || mship.equalsIgnoreCase("Dependent") ||
                         mship.equalsIgnoreCase("Junior") || mship.equalsIgnoreCase("Non-Resident") || mship.equalsIgnoreCase("Senior") ||
                         mship.equalsIgnoreCase("Regular") || mship.equalsIgnoreCase("Sr Cert") || mship.equalsIgnoreCase("Intermed/C") ||
                         mship.equalsIgnoreCase("Widow")) {

                         // Do nothing

                     } else if (mship.equalsIgnoreCase("Certificate")) {
                         mship = "Certificat";
                     } else if (mship.equalsIgnoreCase("Intermediate")) {
                         mship = "Intermedia";
                     } else {

                         skip = true;
                         warnCount++;
                         warnMsg = warnMsg + "\n" +
                                 "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                     }
                  }
               }  // end of IF club = tamarack

               //******************************************************************
               //   St. Clair Country Club
               //******************************************************************
               //
               if (club.equals( "stclaircc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mtype.equals( "" )) {                 // this club has its mship values in mtype field!!

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";
                  } else {

                      // strip "Member Type:" from mship if present
                      if (mtype.startsWith("Member Type:")) {
                          mtype = mtype.substring(12);
                      }

                      // set mship
                      if (mtype.equalsIgnoreCase("ACTIVE") || mtype.equalsIgnoreCase("VOTING")) {
                          mship = "Voting";
                      } else if (mtype.equalsIgnoreCase("ACTIVESR") || mtype.equalsIgnoreCase("SENIOR")) {
                          mship = "Senior";
                      } else if (mtype.startsWith("INT")) {
                          mship = "Intermediate";
                      } else if (mtype.equalsIgnoreCase("ASSOC20")) {
                          mship = "Assoc20";
                      } else if (mtype.equalsIgnoreCase("ASSOCIATE")) {
                          mship = "Associate Golf";
                      } else if (mtype.equalsIgnoreCase("LTD GOLF")) {
                          mship = "Limited Golf";
                      } else if (mtype.equalsIgnoreCase("SOCGLF")) {
                          mship = "Social Golf";
                      } else if (mtype.equalsIgnoreCase("NRGP")) {
                          mship = "NR Golf";
                      } else if (mtype.equalsIgnoreCase("FAMILY GP") || mtype.equalsIgnoreCase("SPOUSE GP")) {
                          mship = "Spouse Golf";
                      } else if (mtype.equalsIgnoreCase("FAMILYSPGP") || mtype.equalsIgnoreCase("SPOUSESPGP")) {
                          mship = "Spouse Golf 9";
                      } else if (mtype.equalsIgnoreCase("ASSP20GP")) {
                          mship = "Assoc Spouse20";
                      } else if (mtype.equalsIgnoreCase("ASGP")) {
                          mship = "Assoc/Ltd Spouse";
                      } else if (mtype.equalsIgnoreCase("LTDSP GP") || mtype.equalsIgnoreCase("ASGP")) {
                          mship = "Limited Spouse";
                      } else if (mtype.equalsIgnoreCase("ASSSPGP")) {
                          mship = "Associate Spouse";
                      } else if (mtype.equalsIgnoreCase("SOCSP GP") || mtype.equalsIgnoreCase("ASRGP")) {
                          mship = "Soc Golf Spouse";
                      } else if (mtype.equalsIgnoreCase("JR 12-17") || mtype.equalsIgnoreCase("JR 18-24")) {
                          mship = "Junior Golf";
                      } else if (mtype.equalsIgnoreCase("ASSOC20J") || mtype.equalsIgnoreCase("ASSOC20J18")) {
                          mship = "Assoc Jr20";
                      } else if (mtype.equalsIgnoreCase("ASSOCJR") || mtype.equalsIgnoreCase("ASSOCJR18")) {
                          mship = "Associate Jr";
                      } else if (mtype.startsWith("LTD JR")) {
                          mship = "Limited Jr";
                      } else if (mtype.equalsIgnoreCase("SOCJR<18") || mtype.equalsIgnoreCase("SOCJR>18")) {
                          mship = "Soc Jr Golf";
                      } else if (mtype.equalsIgnoreCase("EMERITUS")) {
                          mship = "Emeritus";
                      } else {

                          skip = true;
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                      }

                      // set other values
                      posid = mNum;     // set posid in case we ever need it
                      while (mNum.startsWith("0")){
                          mNum = remZeroS(mNum);
                      }

                      useWebid = true;       // use these webids
                      webid = memid;
                      memid = mNum;

                      // set mtype
                      if (mNum.endsWith("S")) {
                          if (gender.equalsIgnoreCase("M")) {
                              mtype = "Spouse Male";
                          } else {
                              mtype = "Spouse Female";
                          }
                          mNum = mNum.substring(0,mNum.length()-1);         // remove extension char
                      } else if (mNum.endsWith("J") || mNum.endsWith("K") || mNum.endsWith("L") || mNum.endsWith("M") || mNum.endsWith("N") || mNum.endsWith("O") || mNum.endsWith("P")) {
                          mtype = "Dependent";
                          mNum = mNum.substring(0,mNum.length()-1);         // remove extension char
                      } else {
                          if (gender.equalsIgnoreCase("M")) {
                              mtype = "Primary Male";
                          } else {
                              mtype = "Primary Female";
                          }
                      }
                  }

               }  // end of IF club = stclaircc


               //******************************************************************
               //   The Trophy Club Country Club
               //******************************************************************
               //
               if (club.equals( "trophyclubcc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mship.equals( "" )) {

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";


                  } else {

                      useWebid = true;
                      webid = memid;
                      posid = mNum;

                      mship = "Golf";

                      if (gender.equalsIgnoreCase("F")) {
                          mtype = "Adult Female";
                      } else {
                          mtype = "Adult Male";
                      }

                  }
               } // end of IF club = trophyclubcc



               //******************************************************************
               //   Pelican Marsh Golf Club
               //******************************************************************
               //
               if (club.equals( "pmarshgc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mtype.equals( "" )) {                 // this club has its mship values in mtype field!!

                      skip = true;                          //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      useWebid = true;
                      webid = memid;
                      memid = mNum;

                      mNum = stripDash(mNum);          // remove the -00 etc from end of mNums

                      posid = mNum;

                      // check for proper membership types
                      if (mtype.equalsIgnoreCase("Equity Golf") || mtype.equalsIgnoreCase("Non-Equity Golf") ||
                          mtype.equalsIgnoreCase("Trial Golf")) {
                          mship = "Golf";
                      } else if (mtype.equalsIgnoreCase("Equity Social")) {
                          mship = "Social";
                      } else {
                          skip = true;
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                      }

                      if (gender.equalsIgnoreCase("F")) {
                          mtype = "Adult Female";
                      } else {
                          mtype = "Adult Male";
                      }

                  }
               } // end of IF club is pmarshgc


               //******************************************************************
               //   Silver Lake Country Club
               //******************************************************************
               //
               if (club.equals( "silverlakecc" )) {

                  found = true;    // club found

                  //
                  //  Make sure this is ok
                  //
                  if (mtype.equals( "" )) {                // this club has its mship values in mtype field!!

                     skip = true;                          //  skip this one
                     errCount++;
                     errMsg = errMsg + "\n" +
                             "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      posid = mNum;
                      mNum = remZeroS(mNum);

                      useWebid = true;     // use webid for this club
                      webid = memid;     // use memid for webid

                      if (mtype.startsWith("Social")) {
                          mship = "Social Elite";
                      } else {
                          mship = "Full Golf";
                      }

                      //Will need to add "Social Elite" eventually!

                      if (gender.equalsIgnoreCase("F")) {
                          mtype = "Adult Female";
                          memid = mNum + "A";
                      } else {
                          mtype = "Adult Male";
                          memid = mNum;
                      }

                  }
               } // end of IF club is silverlakecc


               //******************************************************************
               //   Edina Country Club
               //******************************************************************
               //
               if (club.equals("edina") || club.equals("edina2010")) {

                  found = true;      // club found

                  if (mship.equals( "" )) {

                      skip = true;       //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      useWebid = true;     // use webid for this club
                      webid = memid;     // use memid for webid

                      StringTokenizer tempTok = new StringTokenizer(mNum, "-");
                      String suf = "0";

                      if (tempTok.countTokens() > 1){     // if mNum contains a - then it is a spouse
                          mNum = stripDash(mNum);
                          suf = "1";
                      }

                      posid = mNum;       // set posid before zeros are removed

                      while (mNum.startsWith("0")) {
                          mNum = remZeroS(mNum);
                      }

                      memid = mNum + suf;     // set memid

                      // ignore specific membership types
                      if (mship.equalsIgnoreCase("Other Clubs") || mship.equalsIgnoreCase("Party Account") ||
                              mship.equalsIgnoreCase("Resigned with Balance Due")) {
                          skip = true;
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                      } else if (mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Honorary Social") || mship.equalsIgnoreCase("Clergy") || mship.equalsIgnoreCase("Social Widow")) {

                          mship = "Social";

                      } else if (mship.equalsIgnoreCase("Pool/Tennis")) {

                          mship = "Pool/Tennis";

                      } else {  // leave these two as they are, everything else = golf
                          
                          mship = "Golf";
                      }

                      // set member type based on gender
                      if (primary.equalsIgnoreCase("P") || primary.equalsIgnoreCase("S")) {
                          if (gender.equalsIgnoreCase("F")) {
                              mtype = "Adult Female";
                          } else {
                              mtype = "Adult Male";
                          }
                      } else {
                          mtype = "Dependent";
                      }
                      
                      
                      //
                      //  Custom to filter out a member's 2nd email address - she doesn't want ForeTees emails on this one, but wants it in MF
                      //
                      if (webid.equals("2720159")) {
                         
                         email2 = "";
                      }
                                            
                  }

               } // end if edina

               //******************************************************************
               //   Seville Golf & Country Club
               //******************************************************************
               //
               if (club.equals("sevillegcc")) {

                  found = true;      // club found

                  if (mtype.equals( "" )) {              // this club has its mship values in mtype field!!

                      skip = true;                       //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      posid = mNum;       // set posid before zeros are removed

                      while (mNum.startsWith("0")) {
                          mNum = remZeroS(mNum);
                      }

                      // ignore specific membership types
                      if (mtype.startsWith("Sports")) {
                          mship = "Sports Golf";
                      } else {
                          mship = "Full Golf";
                      }

                      // set member type and memid based on gender
                      if (gender.equalsIgnoreCase("F")) {
                          mtype = "Adult Female";
                          memid = mNum + "A";
                      } else {
                          mtype = "Adult Male";
                          memid = mNum;
                      }
                  }

               } // end if sevillegcc


               //******************************************************************
               //   Royal Oaks CC - Dallas
               //******************************************************************
               //
               if (club.equals("roccdallas")) {

                  found = true;      // club found

                  if (mship.equals( "" )) {

                      skip = true;                       //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (mship.equalsIgnoreCase("Tennis Member") || mship.equalsIgnoreCase("Tennis Special Member") ||
                          mship.equalsIgnoreCase("Junior Tennis Member") || mship.equalsIgnoreCase("Social Member") ||
                          mship.equalsIgnoreCase("Dining Member")) {

                      skip = true;
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      posid = mNum;       // set posid before zeros are removed
                     //  memid = mNum;       // user mNum as memid, mNums ARE UNIQUE!  - USE MF's ID !!!!

                      if (!mNum.endsWith( "0" ) && !mNum.endsWith( "1" ) && !mNum.endsWith( "2" ) && !mNum.endsWith( "3" ) &&
                          !mNum.endsWith( "4" ) && !mNum.endsWith( "5" ) && !mNum.endsWith( "6" ) && !mNum.endsWith( "7" ) &&
                          !mNum.endsWith( "8" ) && !mNum.endsWith( "9" )) {

                         mNum = stripA(mNum);           // remove trailing alpha
                      }


                      // handle 'Spouse of member' membership type
                      if (mship.equalsIgnoreCase("Spouse of member")) {

                          primary = "S";        // they are a Spouse

                          // if Spouse: determine mship from 2nd character of mNum
                          if (!mNum.equals("")) {

                              if (mNum.charAt(1) == 'P') {
                                  mship = "Golf Associate Member";
                              } else if (mNum.charAt(1) == 'E') {
                                  mship = "Special Member";
                              } else if (mNum.charAt(1) == 'G') {
                                  mship = "Senior Member";
                              } else if (mNum.charAt(1) == 'J') {
                                  mship = "Junior Member";
                              } else if (mNum.charAt(1) == 'N') {
                                  mship = "Non Resident Member";
                              } else if (mNum.charAt(1) == 'F') {
                                  mship = "Temp Non Certificate";
                              } else if (mNum.charAt(1) == 'L') {
                                  mship = "Ladies Member";
                              } else if (mNum.charAt(1) == 'K') {
                                  mship = "Associate Resident Member";
                              } else if (mNum.charAt(1) == 'H') {
                                  mship = "Honorary";
                              } else if (mNum.charAt(1) == 'B') {
                                  mship = "Tennis with Golf";
                              } else if (mNum.charAt(1) == 'D' || mNum.charAt(1) == 'T' || mNum.charAt(1) == 'R' || mNum.charAt(1) == 'S') {
                                  skip = true;
                                  warnCount++;
                                  warnMsg = warnMsg + "\n" +
                                          "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                              } else {          // no letter for 2nd char of mNum
                                  mship = "Resident Member";
                              }
                          } else {

                              skip = true;
                              errCount++;
                              errMsg = errMsg + "\n" +
                                      "  -MEMBERSHIP TYPE could not be determined! (mNum missing)";
                          }
                      }

                      // set member type based on gender
                      // blank gender and mNum ending with 'A' = female, otherwise male
                      if (gender.equalsIgnoreCase("F") || (gender.equalsIgnoreCase("") && mNum.toLowerCase().endsWith("a"))) {

                          gender = "F";
                          mtype = "Adult Female";
                      } else {

                          gender = "M";
                          mtype = "Adult Male";
                      }
                  }
               } // end if roccdallas


               //******************************************************************
               //   Hackberry Creek CC - hackberrycreekcc
               //******************************************************************
               //
               if (club.equals("hackberrycreekcc")) {

                  found = true;      // club found

                  if (mship.equals( "" )) {

                      skip = true;       //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      posid = mNum;         // set posid in case we need it in the future

                      useWebid = true;      // use webid for this club
                      webid = memid;        // use memid for webid

                      mship = "Golf";       // everyone changed to "Golf"

                      // set member type and memid based on gender
                      if (gender.equalsIgnoreCase("F")) {
                          mtype = "Adult Female";
                          memid = mNum + "A";
                      } else {
                          mtype = "Adult Male";
                          memid = mNum;
                      }
                  }
               }  // end if hackberrycreekcc

               //******************************************************************
               //   Brookhaven CC - brookhavenclub
               //******************************************************************
               //
               if (club.equals("brookhavenclub")) {

                  found = true;      // club found

                  if (mship.equals( "" )) {

                      skip = true;       //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      posid = mNum;         // set posid in case we need it in the future

                      useWebid = true;      // use webid for this club
                      webid = memid;        // use memid for webid


                      if (mtype.startsWith("DFW")) {
                          mship = "DFWY";
                      } else {
                          mship = "Golf";       // everyone changed to "Golf"
                      }

                      // set member type and memid based on gender
                      if (gender.equalsIgnoreCase("F")) {
                          mtype = "Adult Female";
                          memid = mNum + "A";
                      } else {
                          mtype = "Adult Male";
                          memid = mNum;
                      }
                  }
               }  // end if brookhavenclub

               //******************************************************************
               //   Stonebridge Ranch CC - stonebridgeranchcc
               //******************************************************************
               //
               if (club.equals("stonebridgeranchcc")) {

                   found = true;      // club found

                   if (mtype.equals( "" )) {              // this club has its mship values in mtype field!!

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                      posid = mNum;         // set posid in case we need it in the future

                      useWebid = true;      // use webid for this club
                      webid = memid;        // use memid for webid

                      if (mtype.equalsIgnoreCase("Dual Club") || mtype.equalsIgnoreCase("Dual Club Distant") || mtype.equalsIgnoreCase("Dual Club Society") ||
                          mtype.equalsIgnoreCase("Honorary") || mtype.equalsIgnoreCase("Honorary Society") || mtype.equalsIgnoreCase("Prem Charter Select") ||
                          mtype.equalsIgnoreCase("Prem Chrtr Sel Scty") || mtype.equalsIgnoreCase("Prem Club Corp Scty") || mtype.equalsIgnoreCase("Prem Mbr Sel Society") ||
                          mtype.equalsIgnoreCase("Prem Member Charter") || mtype.equalsIgnoreCase("Prem Member Select") || mtype.equalsIgnoreCase("Prem Mbrshp Society") ||
                          mtype.equalsIgnoreCase("Premier Club Corp D") || mtype.equalsIgnoreCase("Premier Club Jr") || mtype.equalsIgnoreCase("Premier Corporate") ||
                          mtype.equalsIgnoreCase("Premier Membership") || mtype.equalsIgnoreCase("Premier Nr") || mtype.equalsIgnoreCase("Prem Mbr Chrtr Scty") ||
                          mtype.equalsIgnoreCase("Premier Club Jr Scty") || mtype.equalsIgnoreCase("Westerra") || mtype.equalsIgnoreCase("Premier Club C Ppd") ||
                          mtype.equalsIgnoreCase("Premier Club Corp Ds") || mtype.equalsIgnoreCase("Preview")) {

                          mship = "Dual";

                      } else if (mtype.equalsIgnoreCase("Pr SB  Sel Scty") || mtype.equalsIgnoreCase("Prem Stnbrdge Select") || mtype.equalsIgnoreCase("Prem Stonbrdg Scty ") ||
                                 mtype.equalsIgnoreCase("Premier Stonebridge") || mtype.equalsIgnoreCase("Stonebridge Assoc.") || mtype.equalsIgnoreCase("Stonebridge Charter") ||
                                 mtype.equalsIgnoreCase("Stonebridge Golf") || mtype.equalsIgnoreCase("Stonebridge Golf Soc") || mtype.equalsIgnoreCase("Options II") ||
                                 mtype.equalsIgnoreCase("Options II Society") || mtype.equalsIgnoreCase("Options I ") || mtype.equalsIgnoreCase("Options I Society") ||
                                 mtype.equalsIgnoreCase("Stonebridge Distn Gf") || mtype.equalsIgnoreCase("Sb Premier Nr") || mtype.equalsIgnoreCase("Prem Stonbrdg Scty") ||
                                 mtype.equalsIgnoreCase("Stonebridge Soc Scty") || mtype.equalsIgnoreCase("Stnbrdge Assoc Scty") || mtype.equalsIgnoreCase("Sb Golf Legacy Soc.")) {

                          mship = "Dye";

                      } else if (mtype.equalsIgnoreCase("Pr Rcc Pr 6/96 Scty") || mtype.equalsIgnoreCase("Pr Rnch Aft 6/96 Sct") || mtype.equalsIgnoreCase("Prem Rcc Jr Select") ||
                                 mtype.equalsIgnoreCase("Prem Rcc Prior 6/96") || mtype.equalsIgnoreCase("Prem Rnch After 6/96") || mtype.equalsIgnoreCase("Prem Rnch Select Sty") ||
                                 mtype.equalsIgnoreCase("Premier Ranch Select") || mtype.equalsIgnoreCase("Prm Rcc Sel Aft") || mtype.equalsIgnoreCase("Prm Rcc Sel Aft 96st") ||
                                 mtype.equalsIgnoreCase("Ranch Charter") || mtype.equalsIgnoreCase("Ranch Golf") || mtype.equalsIgnoreCase("Ranch Golf Legacy") ||
                                 mtype.equalsIgnoreCase("Ranch Golf Non-Res") || mtype.equalsIgnoreCase("Ranch Golf Society") || mtype.equalsIgnoreCase("Special Golf") ||
                                 mtype.equalsIgnoreCase("Prem Rcc Pr Nr") || mtype.equalsIgnoreCase("Prem Rnch Sports Sty") || mtype.equalsIgnoreCase("Ranch Nr Society") ||
                                 mtype.equalsIgnoreCase("Pr Rcc Aft799 Society") || mtype.equalsIgnoreCase("Ranch Non Resident") || mtype.equalsIgnoreCase("Ranch Ppd Rcc Golf")) {

                          mship = "Hills";

                      } else {
                          skip = true;
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                      }

                      // set member type and memid based on gender
                      if (gender.equalsIgnoreCase("F")) {
                          mtype = "Adult Female";
                      } else {
                          mtype = "Adult Male";
                      }
                   }

               }  // end if stonebridgeranchcc


               //******************************************************************
               //   Charlotte CC - charlottecc
               //******************************************************************
               //
               if (club.equals("charlottecc")) {

                  found = true;      // club found

                  if (mship.equals( "" )) {

                      skip = true;       //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      useWebid = true;      // use webid for this club
                      webid = memid;        // use memid for webid

                      memid = mNum.toUpperCase();       // use mNum for memid, they are unique

                      // Set primary/spouse value
                      if (mNum.toUpperCase().endsWith("S")) {
                          primary = "S";
                          mNum = stripA(mNum);
                      } else {
                          primary = "P";
                      }

                      posid = mNum;         // set posid in case it's ever needed

                      // set mtype based on gender
                      if (gender.equalsIgnoreCase("F")) {
                          mtype = "Adult Female";
                      } else {
                          mtype = "Adult Male";
                      }

                      // if mship starts with 'Spousal-surviving' or 'Spousal', remove the prefix
                      if (mship.equalsIgnoreCase("Spousal-surviving Resident")) {
                          mship = "Dependent Spouse";
                      } else {
                          if (mship.startsWith("Spousal-surviving")) {
                              mship = mship.substring(18, mship.length() - 1);
                          }
                          if (mship.startsWith("Spousal") && !mship.equalsIgnoreCase("Spousal Member")) {
                              mship = mship.substring(8, mship.length() - 1);
                          }

                          // set mship
                          if (mship.startsWith("Resident") || mship.equalsIgnoreCase("Ministerial-NM")) {
                              mship = "Resident";
                          } else if (mship.startsWith("Non-Resident")) {
                              mship = "Non Resident";
                          } else if (mship.startsWith("Dependant")) {
                              mship = "Dependent Spouse";
                          } else if (mship.startsWith("Honorary")) {
                              mship = "Honorary";
                          } else if (mship.startsWith("Lady") || mship.equalsIgnoreCase("Spousal Member")) {
                              mship = "Lady";
                          } else {
                              skip = true;
                              warnCount++;
                              warnMsg = warnMsg + "\n" +
                                      "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                          }
                      }
                  }
               }  // end if charlottecc


               //******************************************************************
               //   Gleneagles CC - gleneaglesclub
               //******************************************************************
               //
               if (club.equals("gleneaglesclub")) {

                   found = true;      // club found

                   if (mtype.equals( "" )) {              // this club has its mship values in mtype field!!

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                      posid = mNum;         // set posid in case we need it in the future

                      useWebid = true;      // use webid for this club
                      webid = memid;        // use memid for webid

                      mship = "Golf";       // everyone changed to "Golf"

                      // set member type and memid based on gender
                      if (primary.equalsIgnoreCase("S")) {
                          if (gender.equalsIgnoreCase("F")) {
                              mtype = "Spouse Female";
                          } else {
                              mtype = "Spouse Male";
                          }
                          memid = mNum + "A";

                      } else if (primary.equalsIgnoreCase("P")) {
                          if (gender.equalsIgnoreCase("F")) {
                              mtype = "Member Female";
                          } else {
                              mtype = "Member Male";
                          }
                          memid = mNum;
                      } else {              // Dependent
                          if (gender.equalsIgnoreCase("F")) {
                              mtype = "Dependent Female";
                          } else {
                              mtype = "Dependent Male";
                          }
                          // use provided memid
                      }
                   }

               }  // end if gleneaglesclub




               //******************************************************************
               //   Portland CC - portlandgc
               //******************************************************************
               //
               if (club.equals("portlandgc")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       if (mNum.length() == 6) {

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Female Spouse";
                           } else {
                               mtype = "Male Spouse";
                           }

                           mNum = mNum.substring(0, mNum.length() - 1);   // get rid of extra number on end of spouse mNums
                           primary = "S";

                           memid = mNum;

                           while (memid.startsWith("0")) {       // strip leading zeros
                               memid = remZeroS(memid);
                           }
                           memid = memid + "A";

                       } else {

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Female Member";
                           } else {
                               mtype = "Male Member";
                           }

                           primary = "P";

                           memid = mNum;

                           while (memid.startsWith("0")) {       // strip leading zeros
                               memid = remZeroS(memid);
                           }
                       }

                       posid = mNum;         // set posid in case we need it in the future

                       if (mship.equalsIgnoreCase("AAR-FG") || mship.equalsIgnoreCase("NON-RES") ||
                               mship.equalsIgnoreCase("REGULAR") || mship.equalsIgnoreCase("TEMPORARY")) { mship = "Regular"; }
                       else if (mship.equalsIgnoreCase("30YEARS")) { mship = "30 Year Social"; }
                       else if (mship.equalsIgnoreCase("EMPLOYEE")) { mship = "Employee"; }
                       else if (mship.equalsIgnoreCase("HONORARY")) { mship = "Honorary"; }
                       else if (mship.equalsIgnoreCase("JUNIOR")) { mship = "Junior Associate"; }
                       else if (mship.equalsIgnoreCase("SOCIAL")) { mship = "Social"; }
                       else if (mship.startsWith("L")) { mship = "Leave of Absence"; }
                       else if (mship.equalsIgnoreCase("SPOUSE")) { mship = "Spouse Associate"; }
                       else if (mship.equalsIgnoreCase("Member Status:SENIOR")) { mship = "Senior"; }
                       else {
                          skip = true;
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                       }
                   }
               }  // end if portlandgc


               //******************************************************************
               //   Quechee Club
               //******************************************************************
               //
               if (club.equals("quecheeclub")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       memid = mNum;


                       mNum = mNum.substring(0, mNum.length() - 1);     // get rid of trailing primary indicator # on mNum

                       if (memid.endsWith("0")) {       // Primary
                           primary = "P";
                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Primary Female";
                           } else {
                               mtype = "Primary Male";
                           }
                       } else if (memid.endsWith("1")) {        // Spouse
                           primary = "S";
                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Spouse Female";
                           } else {
                               mtype = "Spouse Male";
                           }
                       } else {         // Dependent
                           primary = "D";
                           mtype = "Dependent";
                       }

                       // Set the posid
                       if (primary.equals("S")) {
                           posid = mNum + "1";         // set posid in case we need it in the future
                       } else {
                           posid = mNum + "0";
                       }

                       if (mship.equalsIgnoreCase("ALL-F")) {
                           mship = "ALL Family";
                       } else if (mship.equalsIgnoreCase("ALL-S")) {
                           mship = "ALL Single";
                       } else if (mship.equalsIgnoreCase("GAP-F")) {
                           mship = "GAP Family";
                       } else if (mship.equalsIgnoreCase("GAP-S")) {
                           mship = "GAP Single";
                       } else if (mship.equalsIgnoreCase("GAPM-F")) {
                           mship = "GAPM Family";
                       } else if (mship.equalsIgnoreCase("GAPM-S")) {
                           mship = "GAPM Single";
                       } else if (mship.equalsIgnoreCase("NON-GAP")) {
                           mship = "NON-GAP";
                       } else {
                          skip = true;
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                       }
                   }
               }  // end if quecheeclub



               //******************************************************************
               //   Quechee Club - Tennis
               //******************************************************************
               //
               if (club.equals("quecheeclubtennis")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       memid = mNum;


                       mNum = mNum.substring(0, mNum.length() - 1);     // get rid of trailing primary indicator # on mNum

                       if (memid.endsWith("0")) {       // Primary
                           primary = "P";
                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Primary Female";
                           } else {
                               mtype = "Primary Male";
                           }
                       } else if (memid.endsWith("1")) {        // Spouse
                           primary = "S";
                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Spouse Female";
                           } else {
                               mtype = "Spouse Male";
                           }
                       } else {         // Dependent
                           primary = "D";
                           mtype = "Dependent";
                       }

                       // Set the posid
                       if (primary.equals("S")) {
                           posid = mNum + "1";         // set posid in case we need it in the future
                       } else {
                           posid = mNum + "0";
                       }

                       if (mship.equalsIgnoreCase("ALL-F")) {
                           mship = "ALL Family";
                       } else if (mship.equalsIgnoreCase("ALL-S")) {
                           mship = "ALL Single";
                       } else if (custom1.equalsIgnoreCase("Tennis-F")) {
                           mship = "TAP Family";
                       } else if (custom1.equalsIgnoreCase("Tennis-S")) {
                           mship = "TAP Single";
                       } else if (custom1.equals("?") || custom1.equals("")) {
                           mship = "NON-TAP";
                       } else {
                          skip = true;
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                       }
                   }
               }  // end if quecheeclubtennis

               //******************************************************************
               //   The Oaks Club
               //******************************************************************
               //
               if (club.equals("theoaksclub")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       memid = mNum;

                       if (mNum.endsWith("-1")) {

                           primary = "S";
                           gender = "F";
                           mtype = "Spouse Female";

                           mNum = mNum.substring(0, mNum.length() - 2);

                           if (mship.startsWith("Dependent")) {     // Use the primary's mship
                               try {
                                   ResultSet oaksRS = null;
                                   PreparedStatement oaksStmt = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                                   oaksStmt.clearParameters();
                                   oaksStmt.setString(1, mNum);
                                   oaksRS = oaksStmt.executeQuery();

                                   if (oaksRS.next()) {
                                       mship = oaksRS.getString("m_ship");
                                   } else {
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SPOUSE with DEPENDENT membership type - NO PRIMARY FOUND!";
                                   }

                                   oaksStmt.close();

                               } catch (Exception exc) { }
                           }

                       } else if (mNum.endsWith("-2") || mNum.endsWith("-3") || mNum.endsWith("-4") || mNum.endsWith("-5") ||
                                  mNum.endsWith("-6") || mNum.endsWith("-7") || mNum.endsWith("-8") || mNum.endsWith("-9")) {

                           primary = "D";
                           mtype = "Dependent";
                           mNum = mNum.substring(0, mNum.length() - 2);

                       } else {

                           primary = "P";
                           gender = "M";
                           mtype = "Primary Male";
                       }

                       posid = mNum;         // set posid in case we need it in the future

                       if (mship.equalsIgnoreCase("Regular Equity") || mship.equalsIgnoreCase("Member Type:001")) {
                           mship = "Regular Equity";
                       } else if (mship.equalsIgnoreCase("Golf") || mship.equalsIgnoreCase("Member Type:010")) {
                           mship = "Golf";
                       } else if (mship.equalsIgnoreCase("Social Property Owner") || mship.equalsIgnoreCase("Member Type:002")) {
                           mship = "Social Property Owner";
                       } else if (mship.equalsIgnoreCase("Tennis Associate") || mship.equalsIgnoreCase("Member Type:020")) {
                           mship = "Tennis Associate";
                       } else if (mship.equalsIgnoreCase("Member Type:022")) {
                           mship = "Jr Tennis Associate";
                       } else if (mship.startsWith("Dependent")) {
                           mship = "Dependent";
                       } else if (mship.equalsIgnoreCase("Member Type:085")) {
                           mship = "General Manager";
                       } else {
                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                       }

                   }
               }  // end if theoaksclub


               //******************************************************************
               //   Admirals Cove
               //******************************************************************
               //
               if (club.equals("admiralscove")) {

                   found = true;      // club found

                   if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       memid = mNum;

                       if (mNum.endsWith("A")) {

                           primary = "S";
                           mNum = mNum.substring(0, mNum.length() - 1);

                       } else {
                           primary = "P";
                       }

                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Adult Female";
                       } else {
                           mtype = "Adult Male";
                       }

                       if (mship.equals( "" )) {
                           //
                           //  Spouse or Dependent - look for Primary mship type and use that
                           //
                           try {
                               pstmt2 = con.prepareStatement (
                                       "SELECT m_ship FROM member2b WHERE username != ? AND m_ship != '' AND memNum = ?");

                               pstmt2.clearParameters();
                               pstmt2.setString(1, memid);
                               pstmt2.setString(2, mNum);
                               rs = pstmt2.executeQuery();

                               if(rs.next()) {
                                   mship = rs.getString("m_ship");            // use primary mship type
                               } else {                                       //
                                   skip = true;       //  skip this one
                                   mship = "";
                                   errCount++;
                                   errMsg = errMsg + "\n" +
                                           "  -MEMBERSHIP TYPE missing!";
                               }

                               pstmt2.close();

                           } catch (Exception e1) { }

                       }

                       posid = mNum;         // set posid in case we need it in the future

                       if (!mship.equals("")) {

                           if (mship.startsWith("Golf") || mship.equals("Full Golf")) {
                               mship = "Full Golf";
                           } else if (mship.startsWith("Sports")) {
                               mship = "Sports";
                           } else if (!mship.equals("Social") && !mship.equals("Marina") && !mship.equals("Tennis")) {
                               skip = true;
                               warnCount++;
                               warnMsg = warnMsg + "\n" +
                                       "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                           }
                       }
                   }
               }  // end if admiralscove


               //******************************************************************
               //   Ozaukee CC
               //******************************************************************
               //
               if (club.equals("ozaukeecc")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       if (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       if (mNum.endsWith("A") || mNum.endsWith("a") || mNum.endsWith("B") || mNum.endsWith("C") || mNum.endsWith("D")) {

                           primary = "S";
                           mNum = mNum.substring(0, mNum.length() - 1);     // strip trailing 'A'

                       } else {
                           primary = "P";
                       }

                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Adult Female";
                       } else {
                           mtype = "Adult Male";
                       }

                       if (mship.equalsIgnoreCase("EM")) {
                           mship = "Emeritus";
                       }

                       if (mship.equalsIgnoreCase("Curler") || mship.equalsIgnoreCase("Resigned") ||
                           mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Summer Social")) {
                               skip = true;
                               warnCount++;
                               warnMsg = warnMsg + "\n" +
                                       "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                       }

                       posid = mNum;         // set posid in case we need it in the future
                   }
               }  // end if ozaukeecc

               //******************************************************************
               //   Palo Alto Hills G&CC - paloaltohills
               //******************************************************************
               //
               if (club.equals("paloaltohills")) {

                   found = true;      // club found

                   if (mtype.equals( "" )) {              // this club has its mship values in mtype field!!

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                      useWebid = true;      // use webid for this club
                      webid = memid;        // use memid for webid

                      // trim off leading zeroes
                      while (mNum.startsWith("0")) {
                          mNum = mNum.substring(1);
                      }

                      memid = mNum;

                      if (gender.equals("F")) {
                          primary = "S";
                          //mtype = "Secondary Female";     // no longer setting mtype with roster sync
                      } else {
                          primary = "P";
                          //mtype = "Primary Male";     // no longer setting mtype with roster sync
                          gender = "M";
                      }

                      if (mNum.endsWith("A")) {
                          mNum = mNum.substring(0, mNum.length() - 1);
                      }

                      if (memid.startsWith("1") || memid.startsWith("4") || memid.startsWith("6") || memid.startsWith("8")) {
                          mship = "Golf";
                      } else if (memid.startsWith("2")) {
                          mship = "Social";
                      } else {
                          skip = true;
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                      }
                   }

               }  // end if paloaltohills


               //******************************************************************
               //   Woodside Plantation CC - wakefieldplantation
               //******************************************************************
               //
               if (club.equals("woodsideplantation")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       posid = mNum;         // set posid in case we need it in the future


                       if (memid.equals("399609")) {   // Marvin Cross has an invalid email address (paulcross@bellsouth.net) - belongs to a church in FL
                           email2 = "";
                       }


                       if (primary.equalsIgnoreCase("P")) {
                           memid = mNum;
                       } else {
                           memid = mNum + "A";
                       }

                       if (gender.equalsIgnoreCase("F")) {
                           gender = "F";
                           mtype = "Adult Female";
                       } else {
                           gender = "M";
                           mtype = "Adult Male";
                       }

                   }
               }  // end if woodsideplantation

               //******************************************************************
               //   Timarron CC - timarroncc
               //******************************************************************
               //
               if (club.equals("timarroncc")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       posid = mNum;         // set posid in case we need it in the future

                       mship = "Golf";

                       if (primary.equalsIgnoreCase("S")) {
                           memid = mNum + "A";
                       } else {
                           memid = mNum;
                       }

                       if (gender.equalsIgnoreCase("F")) {
                           gender = "F";
                           mtype = "Adult Female";
                       } else {
                           gender = "M";
                           mtype = "Adult Male";
                       }
                   }
               }  // end if timarroncc

               //******************************************************************
               //   Fountaingrove Golf & Athletic Club - fountaingrovegolf
               //******************************************************************
               //
               if (club.equals("fountaingrovegolf")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       String posSuffix = "";

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       posid = mNum;
                       
                       if (mNum.endsWith("A")) {
                           primary = "P";
                           mNum = mNum.substring(0, mNum.length() - 1);
                           memid = mNum;
                       } else {
                           primary = "S";
                           mNum = mNum.substring(0, mNum.length() - 1);
                           memid = mNum + "A";
                       }

                       if (mship.equalsIgnoreCase("Golf") || mship.equalsIgnoreCase("Employee")) {
                           mship = "Golf";
                       } else {
                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                       }

                       if (gender.equalsIgnoreCase("F")) {
                           gender = "F";
                           mtype = "Adult Female";
                       } else {
                           gender = "M";
                           mtype = "Adult Male";
                       }
                   }
               }  // end if fountaingrovegolf


/*  Disabled, left ForeTees
               //******************************************************************
               //   Austin Country Club - austincountryclub
               //******************************************************************
               //
               if (club.equals("austincountryclub")) {

                   found = true;      // club found

                   if (primary.equalsIgnoreCase("P") && mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       if (mNum.toUpperCase().endsWith("A")) {
                           mNum = stripA(mNum);
                       }

                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }
                       posid = mNum;         // set posid in case we need it in the future

                       if (gender.equalsIgnoreCase("F")) {
                           gender = "F";
                           mtype = "Adult Female";
                       } else {
                           gender = "M";
                           mtype = "Adult Male";
                       }

                       // If a spouse member, retrieve the primary user's membership type to use for them
                       if (primary.equalsIgnoreCase("S")) {
                           try {
                               PreparedStatement pstmtAus = null;
                               ResultSet rsAus = null;

                               pstmtAus = con.prepareStatement("SELECT m_ship FROM member2b WHERE memNum = ? AND m_ship<>''");
                               pstmtAus.clearParameters();
                               pstmtAus.setString(1, mNum);

                               rsAus = pstmtAus.executeQuery();

                               if (rsAus.next()) {
                                   mship = rsAus.getString("m_ship");
                               } else {
                                   mship = "";
                               }

                               pstmtAus.close();

                           } catch (Exception ignore) {

                               mship = "";
                               skip = true;                          //  skip this one
                               warnCount++;
                               warnMsg = warnMsg + "\n" +
                                       "  -SKIPPED: Membership Type could not be retrieved from Primary Member record!";
                           }
                       }

                       if (mship.equalsIgnoreCase("JRF") || mship.equalsIgnoreCase("Former Junior")) {
                           mship = "Former Junior";
                       } else if (mship.equalsIgnoreCase("HON") || mship.equalsIgnoreCase("Honorary")) {
                           mship = "Honorary";
                       } else if (mship.equalsIgnoreCase("JR") || mship.equalsIgnoreCase("Junior")) {
                           mship = "Junior";
                       } else if (mship.equalsIgnoreCase("N-R") || mship.equalsIgnoreCase("Non-Resident")) {
                           mship = "Non-Resident";
                       } else if (mship.equalsIgnoreCase("RES") || mship.equalsIgnoreCase("Resident")) {
                           mship = "Resident";
                       } else if (mship.equalsIgnoreCase("SR") || mship.equalsIgnoreCase("SRD") || mship.equalsIgnoreCase("Senior")) {
                           mship = "Senior";
                       } else {
                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                       }


                   }
               }  // end if austincountryclub
 */

               //******************************************************************
               //   Treesdale Golf & Country Club - treesdalegolf
               //******************************************************************
               //
               if (club.equals("treesdalegolf")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid
                       memid = mNum;

                       posid = mNum;         // set posid in case we need it in the future

                       if (primary.equalsIgnoreCase("P")) {

                           if (gender.equalsIgnoreCase("F")) {
                               gender = "F";
                               mtype = "Primary Female";
                           } else {
                               gender = "M";
                               mtype = "Primary Male";
                           }

                       } else if (primary.equalsIgnoreCase("S")) {

                           if (gender.equalsIgnoreCase("F")) {
                               gender = "F";
                               mtype = "Spouse Female";
                           } else {
                               gender = "M";
                               mtype = "Spouse Male";
                           }

                           memid += "A";

                       } else {
                           mtype = "Junior";
                       }

                       mship = "Golf";

                   }
               }  // end if treesdalegolf

               //******************************************************************
               //   Sawgrass Country Club - sawgrass
               //******************************************************************
               //
               if (club.equals("sawgrass")) {

                   found = true;      // club found

                   if (mtype.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid
                       memid = mNum;

                       if (mNum.endsWith("-1")) {
                           mNum = mNum.substring(0, mNum.length() - 2);
                       }

                       posid = mNum;         // set posid in case we need it in the future

                       // Still filter on given mships even though using mtype field to determine what mship will be set to
                       if (mship.equalsIgnoreCase("Associate Member") || mship.equalsIgnoreCase("Complimentary Employee") || mship.equalsIgnoreCase("Complimentary Other") ||
                           mship.startsWith("House")) {

                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE! (mship)";
                       }

                       if (mtype.equalsIgnoreCase("7DAY")) {
                           mship = "Sports";
                       } else if (mtype.equalsIgnoreCase("3DAY")) {
                           mship = "Social";
                       } else {

                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE! (mtype)";
                       }


                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Member Female";
                       } else {
                           mtype = "Member Male";
                       }

                   }
               }  // end if sawgrass

            /*
               //******************************************************************
               //   TPC at SnoaQualmie Ridge
               //******************************************************************
               //
               if (club.equals("snoqualmieridge")) {

                  found = true;      // club found

                  if (mship.equals( "" )) {

                      skip = true;       //  skip this one
                      errCount++;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";

                  } else if (mNum.equals( "" )) {

                      skip = true;                          //  skip this one
                      warnCount++;
                      warnMsg = warnMsg + "\n" +
                              "  -SKIPPED: Member Number Missing!";

                  } else {

                      useWebid = true;      // use webid for this club
                      webid = memid;        // use memid for webid

                      posid = mNum;         // set posid in case it's ever needed

                      // Set primary/spouse value
                      if (mNum.toUpperCase().endsWith("A")) {
                          primary = "P";
                          mNum = stripA(mNum);
                      } else {
                         if (mNum.toUpperCase().endsWith("B")) {
                             primary = "S";
                             mNum = stripA(mNum);
                         } else {
                            if (mNum.toUpperCase().endsWith("C") || mNum.toUpperCase().endsWith("D") ||
                                mNum.toUpperCase().endsWith("E") || mNum.toUpperCase().endsWith("F") ||
                                mNum.toUpperCase().endsWith("G") || mNum.toUpperCase().endsWith("H") ||
                                mNum.toUpperCase().endsWith("I") || mNum.toUpperCase().endsWith("J")) {
                                primary = "D";

                                memid = mNum.toUpperCase();       // use mNum for memid, they are unique

                                mNum = stripA(mNum);

                            } else {
                               primary = "P";       // if no alpha - assume primary
                            }
                         }
                      }

                      // set mtype based on gender and relationship
                      if (gender.equalsIgnoreCase("F")) {

                         if (primary.equals("P") || primary.equals("S")) {

                            mtype = "Adult Female";

                            memid = mNum + "F";        // memid for Adult Females

                         } else {

                            mtype = "Dependent Female";
                         }

                      } else {    // Male

                         if (primary.equals("P") || primary.equals("S")) {
                            mtype = "Adult Male";

                            memid = mNum + "M";        // memid for Adult Males

                         } else {

                            mtype = "Dependent Male";
                         }
                     }

                      //  mships ?????????????

                  }
               }        // end if snoqualmieridge
             */

               //******************************************************************
               //   Druid Hills Golf Club - dhgc
               //******************************************************************
               //
               if (club.equals("dhgc")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid

                       posid = mNum;         // set posid in case we need it in the future

                       // Remove leading zeroes
                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       if (mship.equalsIgnoreCase("House")) {

                           skip = true;
                           errCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                       }

                       if (primary.equalsIgnoreCase("S")) {
                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Spouse Female";
                           } else {
                               mtype = "Spouse Male";
                           }
                       } else {
                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Primary Female";
                           } else {
                               mtype = "Primary Male";
                           }
                       }
                   }
               }  // end if dhgc

               //******************************************************************
               //   The Reserve Club - thereserveclub
               //******************************************************************
               //
               if (club.equals("thereserveclub")) {

                   found = true;      // club found

                   if (mtype.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       useWebid = true;      // use webid for this club
                       webid = memid;        // use memid for webid
                       memid = mNum;

                       if (mNum.endsWith("-1")) {
                           mNum = mNum.substring(0, mNum.length() - 2);
                       }

                       posid = mNum;         // set posid in case we need it in the future

                       mship = mtype;

                       if (mship.endsWith(" - Spouse")) {
                           mship = mship.substring(0, mship.length() - 9);
                       }

                       if (mship.startsWith("Social")) {

                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE! (mtype)";
                       }

                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Adult Female";
                       } else {
                           mtype = "Adult Male";
                       }
                   }
               }  // end if thereserveclub


               //******************************************************************
               //   Robert Trent Jones - rtjgc
               //******************************************************************
               //
               if (club.equals("rtjgc")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //
                       //  use memid as webid !!
                       //
                       useWebid = true;               // use webid to locate member

                       webid = memid;                 // use webid for this club

                       //
                       //  use the mnum for memid
                       //
                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       if (mNum.endsWith("-1")) {

                           memid = mNum;

                           while (memid.length() < 5) {
                               memid  = "0" + memid;
                           }

                           if (gender.equals("F")) {
                               memid += "A";        // spouse or female
                           }
                       } else {
                           memid = mNum;              // primary males
                       }


                       //
                       //  Set the member type
                       //
                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Primary Female";
                       } else {
                           mtype = "Primary Male";
                       }

                   }

               }  // end if rtjgc


               //******************************************************************
               //   Morgan Run - morganrun
               //******************************************************************
               //
               if (club.equals("morganrun")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //
                       //  use memid as webid !!
                       //
                       useWebid = true;               // use webid to locate member

                       webid = memid;                 // use webid for this club

                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;
                       
                       if (primary.equalsIgnoreCase("S")) {
                           memid += "A";
                       }

                       mship = "Golf";

                       //
                       //  Set the member type
                       //
                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Adult Female";
                       } else {
                           mtype = "Adult Male";
                       }

                   }

               }  // end if morganrun


               //******************************************************************
               //   CC at DC Ranch - ccdcranch
               //******************************************************************
               //
               if (club.equals("ccdcranch")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //
                       //  use memid as webid !!
                       //
                       useWebid = true;               // use webid to locate member

                       webid = memid;                 // use webid for this club

                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       if (mNum.endsWith("A")) {
                           mNum = mNum.substring(0, mNum.length() - 1);
                       }

                       if (mship.equalsIgnoreCase("COR") || mship.equalsIgnoreCase("GOLF")) {
                           mship = "Full Golf";
                       } else if (mship.equalsIgnoreCase("SPS")) {
                           mship = "Sports/Social";
                       } else if (mship.equalsIgnoreCase("SECONDARY")) {
                           if (mNum.startsWith("1") || mNum.startsWith("5")) {
                               mship = "Full Golf";
                           } else if (mNum.startsWith("3")) {
                               mship = "Sports/Social";
                           } else {

                               skip = true;
                               warnCount++;
                               warnMsg = warnMsg + "\n" +
                                       "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                           }
                       } else {

                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                       }

                       //
                       //  Set the member type
                       //
                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Adult Female";
                       } else {
                           mtype = "Adult Male";
                       }

                   }

               }  // end if ccdcranch


               //******************************************************************
               //   Oakley CC - oakleycountryclub
               //******************************************************************
               //
               if (club.equals("oakleycountryclub")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //  use memid as webid
                       useWebid = true;               // use webid to locate member

                       webid = memid;                 // use webid for this club

                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       if (mNum.endsWith("A")) {
                           mNum = mNum.substring(0, mNum.length() - 1);
                       }

                       if (!mtype.equals("")) {
                           mship = mtype;
                       }

                       //  Set the member type
                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Adult Female";
                       } else {
                           gender = "M";
                           mtype = "Adult Male";
                       }

                   }

               }  // end if oakleycountryclub


               //******************************************************************
               //   Black Rock CC - blackrockcountryclub
               //******************************************************************
               //
               if (club.equals("blackrockcountryclub")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //  use memid as webid
                       useWebid = true;               // use webid to locate member

                       webid = memid;                 // use webid for this club

                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       if (mNum.endsWith("A") || mNum.endsWith("D") || mNum.endsWith("Z")) {
                           mNum = mNum.substring(0, mNum.length() - 1);
                       }

                       if (primary.equalsIgnoreCase("S")) {
                           if (gender.equalsIgnoreCase("M")) {
                               mtype = "Spouse Male";
                           } else {
                               mtype = "Spouse Female";
                               gender = "F";
                           }
                       } else {
                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Primary Female";
                           } else {
                               mtype = "Primary Male";
                               gender = "M";
                           }
                       }

                   }

               }  // end if blackrockcountryclub

/*
               //******************************************************************
               //   The Edison Club - edisonclub
               //******************************************************************
               //
               if (club.equals("edisonclub")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       while (posid.startsWith("0")) {
                           posid = posid.substring(1);
                       }

                       //  use memid as webid
                       useWebid = true;               // use webid to locate member

                       webid = memid;                 // use webid for this club

                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       if (mNum.endsWith("A")) {
                           mNum = mNum.substring(0, mNum.length() - 1);
                       }

                       if (primary.equalsIgnoreCase("S")) {
                           if (gender.equalsIgnoreCase("M")) {
                               mtype = "Spouse Male";
                           } else {
                               mtype = "Spouse Female";
                               gender = "F";
                           }
                       } else {
                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Primary Female";
                           } else {
                               mtype = "Primary Male";
                               gender = "M";
                           }
                       } 

                   }

               }  // end if edisonclub
 */


               //******************************************************************
               //   Longue Vue Club - longuevueclub
               //******************************************************************
               //
               if (club.equals("longuevueclub")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //  use memid as webid
                       useWebid = true;               // use webid to locate member

                       webid = memid;                 // use webid for this club

                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       if (mtype.equals("")) {
                           mtype = "Adult Male";
                       }

                       if (mtype.endsWith(" Female")) {
                           gender = "F";
                       } else {
                           gender = "M";
                       }
                   }
               }  // end if longuevueclub


               //******************************************************************
               //   Happy Hollow Club - happyhollowclub
               //******************************************************************
               //
               if (club.equals("happyhollowclub")) {

                   found = true;      // club found

                   if (mtype.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing! (*NOTE* mship located in mtype field)";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //  use memid as webid
                       useWebid = true;               // use webid to locate member

                       webid = memid;                 // use webid for this club

                       memid = mNum;

                       while (memid.startsWith("0")) {
                           memid = memid.substring(1);
                       }

                       // If member number does not end with the first letter of member's last name, strip off the last character
                       if (!mNum.endsWith(lname.substring(0,1))) {
                           mNum = mNum.substring(0, mNum.length() - 1);
                       }

                       // Add leading zeroes until mNum has a length of 5
                       while (mNum.length() < 5) {
                           mNum = "0" + mNum;
                       }


                       // Remove "Member Status:" from beginning of mship
                       if (mtype.startsWith("Member Status:")) {
                           mtype = mtype.replace("Member Status:", "");
                       }

                       // Filter mtypes and mships
                       if (mtype.equalsIgnoreCase("DEPEND A") || mtype.equalsIgnoreCase("DEPEND B") || mtype.equalsIgnoreCase("DEPEND SOC")) {

                           if (mtype.equalsIgnoreCase("DEPEND A") || mtype.equalsIgnoreCase("DEPEND B")) {
                               mship = "Golf";
                           } else if (mtype.equalsIgnoreCase("DEPEND SOC")) {
                               mship = "Social";
                           }

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Dependent Female";
                           } else {
                               mtype = "Dependent Male";
                           }

                       } else if (mtype.equalsIgnoreCase("EMPLOYEE") || mtype.equalsIgnoreCase("GOLF A") || mtype.equalsIgnoreCase("GOLF B") || mtype.equalsIgnoreCase("SOCIAL")) {

                           if (mtype.equalsIgnoreCase("EMPLOYEE")) {
                               mship = "Employee";
                           } else if (mtype.equalsIgnoreCase("GOLF A") || mtype.equalsIgnoreCase("GOLF B")) {
                               mship = "Golf";
                           } else if (mtype.equalsIgnoreCase("SOCIAL")) {
                               mship = "Social";
                           }

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Primary Female";
                           } else {
                               mtype = "Primary Male";
                           }
                           
                       } else if (mtype.equalsIgnoreCase("SPOUSE A") || mtype.equalsIgnoreCase("SPOUSE B") || mtype.equalsIgnoreCase("SPOUSE SOC")) {

                           if (mtype.equalsIgnoreCase("SPOUSE A") || mtype.equalsIgnoreCase("SPOUSE B")) {
                               mship = "Golf";
                           } else if (mtype.equalsIgnoreCase("SPOUSE SOC")) {
                               mship = "Social";
                           }

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Spouse Female";
                           } else {
                               mtype = "Spouse Male";
                           }
                       } else {

                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP/MEMBER TYPE!";
                       }

                   }
               }  // end if happyhollowclub

               
               
               //******************************************************************
               //   CC of Castle Pines
               //******************************************************************
               //
               if (club.equals("castlepines")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //  use memid as webid
                       useWebid = true;               // use webid for member
                       useWebidQuery = true;          // use webid to locate member in Query

                       webid = memid;                 // use webid for this club

                       while (mNum.startsWith("0")) {         // strip any leading zeros
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       // Isolate the member number
                       StringTokenizer tok9 = new StringTokenizer( mNum, "-" );     // look for a dash (i.e.  1234-1)

                       if ( tok9.countTokens() > 1 ) {    

                          mNum = tok9.nextToken();       // get just the mNum if it contains an extension
                       }

                       // Filter mtypes and mships
                       if (mship.equalsIgnoreCase("golf") || mship.equalsIgnoreCase("corporate")) {

                           if (mship.equalsIgnoreCase("Corporate")) {
                               mship = "Corporate";
                           } else {
                               mship = "Regular Member";     // convert Golf to Regular Member
                           }

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Adult Female";
                           } else {
                               mtype = "Adult Male";
                           }

                        } else {

                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP/MEMBER TYPE!";
                       }

                   }
               }  // end if castlepines



               //******************************************************************
               //   Golf Club at Turner Hill - turnerhill
               //******************************************************************
               //
               if (club.equals("turnerhill")) {

                   found = true;      // club found

                   if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //  use memid as webid
                       useWebid = true;               // use webid for member
                       useWebidQuery = true;          // use webid to locate member in Query

                       webid = memid;                 // use webid for this club

                       memid = mNum;

                       mship = "Golf";

                       if (!gender.equalsIgnoreCase("F")) {
                           gender = "M";
                       }

                       if (mNum.endsWith("A")) {

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Spouse Female";
                           } else {
                               mtype = "Spouse Male";
                           }

                           mNum = mNum.substring(0, mNum.length() - 1);

                       } else {

                           if (gender.equalsIgnoreCase("F")) {
                               mtype = "Primary Female";
                           } else {
                               mtype = "Primary Male";
                           }
                       }

                   }
               }  // end if turnerhill


               //******************************************************************
               //   The Club at Mediterra - mediterra
               //******************************************************************
               //
               if (club.equals("mediterra")) {

                   found = true;      // club found

                   if (mship.equals( "" )) {

                       skip = true;       //  skip this one
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBERSHIP TYPE missing!";

                   } else if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //  use memid as webid
                       useWebid = true;               // use webid for member
                       useWebidQuery = true;          // use webid to locate member in Query

                       webid = memid;                 // use webid for this club

                       //  Get ride of all leading zeroes.
                       while (mNum.startsWith("0")) {
                           mNum = mNum.substring(1);
                       }

                       memid = mNum;

                       // Strip off special character
                       if (mNum.endsWith("A") || mNum.endsWith("a") || mNum.endsWith("B") || mNum.endsWith("b")) {
                           mNum = mNum.substring(0, mNum.length() - 1);
                       }

                       if (mship.equalsIgnoreCase("G") || mship.equalsIgnoreCase("GNF")) {
                           mship = "Golf";
                       } else if (mship.equalsIgnoreCase("S")) {
                           mship = "Sports";
                       } else if (mship.equalsIgnoreCase("D")) {

                           try {

                               pstmt2 = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                               pstmt2.clearParameters();
                               pstmt2.setString(1, mNum);

                               rs = pstmt2.executeQuery();

                               if (rs.next()) {
                                   mship = rs.getString("m_ship");
                               } else {

                                   skip = true;
                                   warnCount++;
                                   warnMsg = warnMsg + "\n" +
                                           "  -SKIPPED: NO PRIMARY MEMBERSHIP TYPE!";
                               }

                               pstmt2.close();

                           } catch (Exception exc) {

                               skip = true;
                               warnCount++;
                               warnMsg = warnMsg + "\n" +
                                       "  -SKIPPED: ERROR WHILE LOOKING UP MEMBERSHIP TYPE!";
                           }

                       } else {

                           skip = true;
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP/MEMBER TYPE!";
                       }

                       if (gender.equalsIgnoreCase("F")) {
                           mtype = "Adult Female";
                       } else {
                           mtype = "Adult Male";
                       }
                   }
               }  // end if mediterra


               //******************************************************************
               //   Hideaway Beach Club - hideawaybeachclub
               //******************************************************************
               //
               if (club.equals("hideawaybeachclub")) {

                   found = true;      // club found

                   if (mNum.equals( "" )) {

                       skip = true;                          //  skip this one
                       warnCount++;
                       warnMsg = warnMsg + "\n" +
                               "  -SKIPPED: Member Number Missing!";

                   } else {

                       posid = mNum;                 // use their member numbers as their posid

                       //  use memid as webid
                       useWebid = true;               // use webid for member
                       useWebidQuery = true;          // use webid to locate member in Query

                       webid = memid;                 // use webid for this club

                       memid = mNum;

                       if (mNum.endsWith("A") || mNum.endsWith("a")) {
                           mNum = mNum.substring(0, mNum.length() - 1);
                       }

                       if (mNum.startsWith("7")) {
                           mship = "Renter";
                       } else {
                           mship = "Golf";
                       }

                       if (gender.equalsIgnoreCase("F")) {
                           gender = "F";
                           mtype = "Adult Female";
                       } else {
                           gender = "M";
                           mtype = "Adult Male";
                       }

                   }
               }  // end if hideawaybeachclub


               
               //******************************************************************
               //   All clubs
               //******************************************************************
               //
               if (skip == false && found == true && !fname.equals("") && !lname.equals("") && !memid.equals("")) {

                  //
                  //   now determine if we should update an existing record or add the new one
                  //
                  fname_old = "";
                  lname_old = "";
                  mi_old = "";
                  mship_old = "";
                  mtype_old = "";
                  email_old = "";
                  mNum_old = "";
                  ghin_old = "";
                  bag_old = "";
                  posid_old = "";
                  email2_old = "";
                  phone_old = "";
                  phone2_old = "";
                  suffix_old = "";
                  u_hcap_old = 0;
                  c_hcap_old = 0;
                  birth_old = 0;
                  msub_type_old = "";
                  email_bounce1 = 0;
                  email_bounce2 = 0;


                  //
                  //  Truncate the string values to avoid sql error
                  //
                  if (!mi.equals( "" )) {       // if mi specified

                     mi = truncate(mi, 1);           // make sure it is only 1 char
                  }
                  if (!memid.equals( "" )) {

                     memid = truncate(memid, 15);
                  }
                  if (!password.equals( "" )) {

                     password = truncate(password, 15);
                  }
                  if (!lname.equals( "" )) {

                     lname = truncate(lname, 20);
                  }
                  if (!fname.equals( "" )) {

                     fname = truncate(fname, 20);
                  }
                  if (!mship.equals( "" )) {

                     mship = truncate(mship, 30);
                  }
                  if (!mtype.equals( "" )) {

                     mtype = truncate(mtype, 30);
                  }
                  if (!email.equals( "" )) {

                     email = truncate(email, 50);
                  }
                  if (!email2.equals( "" )) {

                     email2 = truncate(email2, 50);
                  }
                  if (!mNum.equals( "" )) {

                     mNum = truncate(mNum, 10);
                  }
                  if (!ghin.equals( "" )) {

                     ghin = truncate(ghin, 16);
                  }
                  if (!bag.equals( "" )) {

                     bag = truncate(bag, 12);
                  }
                  if (!posid.equals( "" )) {

                     posid = truncate(posid, 15);
                  }
                  if (!phone.equals( "" )) {

                     phone = truncate(phone, 24);
                  }
                  if (!phone2.equals( "" )) {

                     phone2 = truncate(phone2, 24);
                  }
                  if (!suffix.equals( "" )) {

                     suffix = truncate(suffix, 4);
                  }
                  if (!webid.equals( "" )) {

                     webid = truncate(webid, 15);
                  }
                  if (!msub_type.equals( "" )) {

                     msub_type = truncate(msub_type, 30);
                  }

                  //
                  //  Set Gender and Primary values
                  //
                  if (!gender.equalsIgnoreCase( "M" ) && !gender.equalsIgnoreCase( "F" )) {

                     gender = "";
                  }

                  pri_indicator = 0;                            // default = Primary

                  if (primary.equalsIgnoreCase( "S" )) {        // Spouse

                     pri_indicator = 1;
                  }
                  if (primary.equalsIgnoreCase( "D" )) {        // Dependent

                     pri_indicator = 2;
                  }


                  //
                  //  See if a member already exists with this id (username or webid)
                  //
                  //  **** NOTE:  memid and webid MUST be set to their proper values before we get here!!!!!!!!!!!!!!!
                  //
                  //
                  //  4/07/2010
                  //    We now use 2 booleans to indicate what to do with the webid field.
                  //    useWebid is the original boolean that was used to indicate that we should use the webid to identify the member.
                  //       We lost track of when this flag was used and why.  It was no longer used to indicate the webid should be 
                  //       used in the query, so we don't know what its real purpose is.  We don't want to disrupt any clubs that are
                  //       using it, so we created a new boolean for the query.
                  //    useWebidQuery is the new flag to indicate that we should use the webid when searching for the member.  You should use
                  //       both flags if you want to use this one.
                  //
                  try {
                      
                      if (useWebid == true) {            // use webid to locate member?

                         webid_new = webid;                  // yes, set new ids
                         memid_new = "";

                      } else {                            // DO NOT use webid

                         webid_new = "";                  // set new ids
                         memid_new = memid;
                      }

                      if (useWebidQuery == false) {

                         pstmt2 = con.prepareStatement (
                                   "SELECT * FROM member2b WHERE username = ?");

                         pstmt2.clearParameters();               // clear the parms
                         pstmt2.setString(1, memid);            // put the parm in stmt

                      } else {    // use the webid field

                         pstmt2 = con.prepareStatement (
                                   "SELECT * FROM member2b WHERE webid = ?");

                         pstmt2.clearParameters();               // clear the parms
                         pstmt2.setString(1, webid);            // put the parm in stmt
                      }

                      rs = pstmt2.executeQuery();            // execute the prepared stmt

                      if (rs.next()) {

                         memid = rs.getString("username");            // get username in case we used webid (use this for existing members)
                         lname_old = rs.getString("name_last");
                         fname_old = rs.getString("name_first");
                         mi_old = rs.getString("name_mi");
                         mship_old = rs.getString("m_ship");
                         mtype_old = rs.getString("m_type");
                         email_old = rs.getString("email");
                         mNum_old = rs.getString("memNum");
                         ghin_old = rs.getString("ghin");
                         bag_old = rs.getString("bag");
                         birth_old = rs.getInt("birth");
                         posid_old = rs.getString("posid");
                         msub_type_old = rs.getString("msub_type");
                         email2_old = rs.getString("email2");
                         phone_old = rs.getString("phone1");
                         phone2_old = rs.getString("phone2");
                         suffix_old = rs.getString("name_suf");
                         email_bounce1 = rs.getInt("email_bounced");
                         email_bounce2 = rs.getInt("email2_bounced");

                         if (useWebid == true) {                    // use webid to locate member?
                            memid_new = memid;                      // yes, get this username
                         } else {
                            webid_new = rs.getString("webid");      // no, get current webid
                         }
                      }
                      pstmt2.close();              // close the stmt


                      //
                      //  If member NOT found, then check if new member OR id has changed
                      //
                      boolean memFound = false;
                      boolean dup = false;
                      boolean userChanged = false;
                      boolean nameChanged = false;
                      String dupmship = "";

                      if (fname_old.equals( "" )) {            // if member NOT found

                         //
                         //  New member - first check if name already exists
                         //
                         pstmt2 = con.prepareStatement (
                                  "SELECT username, m_ship, memNum, webid FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                         pstmt2.clearParameters();
                         pstmt2.setString(1, lname);
                         pstmt2.setString(2, fname);
                         pstmt2.setString(3, mi);
                         rs = pstmt2.executeQuery();            // execute the prepared stmt

                         if (rs.next() && !club.equals("longcove")) {   // Allow duplicate names for Long Cove for members owning two properties at once

                            dupuser = rs.getString("username");          // get this username
                            dupmship = rs.getString("m_ship");
                            dupmnum = rs.getString("memNum");
                            dupwebid = rs.getString("webid");            // get this webid

                            //
                            //  name already exists - see if this is the same member
                            //
                            sendemail = true;                            // send a warning email to us and MF

                            if ((!dupmnum.equals( "" ) && dupmnum.equals( mNum )) || club.equals("imperialgc")) {   // if name and mNum match, then memid or webid must have changed

                               if (useWebid == true) {            // use webid to locate member?

                                  webid_new = webid;                  // set new ids
                                  memid_new = dupuser;
                                  memid = dupuser;                    // update this record

                               } else {

                                  webid_new = dupwebid;                  // set new ids
                                  memid_new = memid;
                                  memid = dupuser;                       // update this record
                                  userChanged = true;                    // indicate the username has changed
                               }

                               memFound = true;                      // update the member

                               pstmt2 = con.prepareStatement (
                                       "SELECT * FROM member2b WHERE username = ?");

                               pstmt2.clearParameters();               // clear the parms
                               pstmt2.setString(1, memid);            // put the parm in stmt
                               rs = pstmt2.executeQuery();            // execute the prepared stmt

                               if (rs.next()) {

                                   lname_old = rs.getString("name_last");
                                   fname_old = rs.getString("name_first");
                                   mi_old = rs.getString("name_mi");
                                   mship_old = rs.getString("m_ship");
                                   mtype_old = rs.getString("m_type");
                                   email_old = rs.getString("email");
                                   mNum_old = rs.getString("memNum");
                                   ghin_old = rs.getString("ghin");
                                   bag_old = rs.getString("bag");
                                   birth_old = rs.getInt("birth");
                                   posid_old = rs.getString("posid");
                                   msub_type_old = rs.getString("msub_type");
                                   email2_old = rs.getString("email2");
                                   phone_old = rs.getString("phone1");
                                   phone2_old = rs.getString("phone2");
                                   suffix_old = rs.getString("name_suf");
                                   email_bounce1 = rs.getInt("email_bounced");
                                   email_bounce2 = rs.getInt("email2_bounced");
                               }

                               //
                               //  Add this info to the email message text
                               //
                               emailMsg1 = emailMsg1 + "Name = " +fname+ " " +mi+ " " +lname+ ", ForeTees Member Id has been updated to that received.\n\n";

                            } else {

                               //
                               //  Add this info to the email message text
                               //
                               emailMsg1 = emailMsg1 + "Name = " +fname+ " " +mi+ " " +lname+ ", ForeTees username = " +dupuser+ ", ForeTees webid = " +dupwebid+ ", MF id = " +memid+ "\n\n";

                               dup = true;        // dup member - do not add
                            }

                         }
                         pstmt2.close();              // close the stmt

                      } else {      // member found

                         memFound = true;
                      }

                      //
                      //  Now, update the member record if existing member
                      //
                      if (memFound == true) {                   // if member exists

                         changed = false;                       // init change indicator

                         lname_new = lname_old;

                         // do not change lname for Saucon Valley if lname_old ends with '_*'
                         if (club.equals( "sauconvalleycc" ) && lname_old.endsWith("_*")) {

                             lname = lname_old;

                         } else if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                            lname_new = lname;         // set value from MFirst record
                            changed = true;
                            nameChanged = true;
                         }

                         fname_new = fname_old;

                         //
                         //   DO NOT change for select clubs
                         //
                         if (club.equals( "pinery" ) || club.equals( "bellerive" ) || club.equals( "greenhills" ) || club.equals( "fairbanksranch" ) ||
                             club.equals( "baltusrolgc" ) || club.equals("charlottecc") || club.equals("castlepines")) {

                            fname = fname_old;           // do not change fnames

                         } else {

                            if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                               fname_new = fname;         // set value from MFirst record
                               changed = true;
                               nameChanged = true;
                            }
                         }

                         mi_new = mi_old;

                         //
                         //   DO NOT change middle initial for ClubCorp clubs
                         //
                         if (clubcorp) {

                             mi = mi_old;

                         } else {
                             if (!mi_old.equals( mi )) {

                                mi_new = mi;         // set value from MFirst record
                                changed = true;
                                nameChanged = true;
                             }
                         }

                         mship_new = mship_old;

                         if (!mship.equals( "" ) && !mship_old.equals( mship )) {

                            mship_new = mship;         // set value from MFirst record
                            changed = true;
                         }

                         mtype_new = mtype_old;

                         if (club.equals( "greenhills" ) || club.equals("paloaltohills") ||
                            (club.equals("navesinkcc") && mtype_old.equals("Primary Male GP"))) {   // Green Hills - do not change the mtype

                            mtype = mtype_old;

                         } else {

                            if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {

                               mtype_new = mtype;         // set value from MFirst record
                               changed = true;
                            }
                         }

                         mNum_new = mNum_old;

                         if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                            mNum_new = mNum;         // set value from MFirst record
                            changed = true;
                         }

                         ghin_new = ghin_old;

                         if (!ghin.equals( "" ) && !ghin_old.equals( ghin )) {

                            ghin_new = ghin;         // set value from MFirst record
                            changed = true;
                         }

                         bag_new = bag_old;

                         if (!club.equals("edina") && !club.equals("edina2010")) {        // never change for Edina

                            if (!bag.equals( "" ) && !bag_old.equals( bag )) {

                               bag_new = bag;         // set value from MFirst record
                               changed = true;
                            }
                         }

                         posid_new = posid_old;

                         if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                            posid_new = posid;         // set value from MFirst record
                            changed = true;
                         }

                         email_new = email_old;

                         if (!club.equals("mesaverdecc")) {           // never change for Mesa Verde CC

                             if (!email_old.equals( email )) {        // if MF's email changed or was removed

                                email_new = email;                    // set value from MFirst record
                                changed = true;
                                email_bounce1 = 0;                    // reset bounced flag
                             }
                         }

                         email2_new = email2_old;

                         if (!club.equals("mesaverdecc")) {           // never change for Mesa Verde CC

                            if (!email2_old.equals( email2 )) {      // if MF's email changed or was removed

                               email2_new = email2;                  // set value from MFirst record
                               changed = true;
                               email_bounce2 = 0;                    // reset bounced flag
                            }
                         }

                         phone_new = phone_old;

                         if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                            phone_new = phone;         // set value from MFirst record
                            changed = true;
                         }

                         phone2_new = phone2_old;

                         if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                            phone2_new = phone2;         // set value from MFirst record
                            changed = true;
                         }

                         suffix_new = suffix_old;

                         if (!suffix.equals( "" ) && !suffix_old.equals( suffix )) {

                            suffix_new = suffix;         // set value from MFirst record
                            changed = true;
                         }

                         birth_new = birth_old;

                         if (!club.equals("fountaingrovegolf")) {       // Don't update birthdates for Fountain Grove

                             if (birth > 0 && birth != birth_old) {

                                birth_new = birth;         // set value from MFirst record
                                changed = true;
                             }
                         }

                         if (!mobile.equals( "" )) {    // if mobile phone provided

                            if (phone_new.equals( "" )) {        // if phone1 is empty

                               phone_new = mobile;               // use mobile number
                               changed = true;

                            } else {

                               if (phone2_new.equals( "" )) {        // if phone2 is empty

                                  phone2_new = mobile;               // use mobile number
                                  changed = true;
                               }
                            }
                         }

                         msub_type_new = msub_type_old;

                         if (!msub_type.equals( "" ) && !msub_type_old.equals( msub_type )) {

                            msub_type_new = msub_type;         // set value from MFirst record
                            changed = true;
                         }

                         // don't allow both emails to be the same
                         if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";

                         //
                         //  Update our record (always now to set the last_sync_date)
                         //
                         pstmt2 = con.prepareStatement (
                         "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                         "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                         "memNum = ?, ghin = ?, bag = ?, birth = ?, posid = ?, msub_type = ?, email2 = ?, phone1 = ?, " +
                         "phone2 = ?, name_suf = ?, webid = ?, inact = 0, last_sync_date = now(), gender = ?, pri_indicator = ?, " +
                         "email_bounced = ?, email2_bounced = ? " +
                         "WHERE username = ?");

                         pstmt2.clearParameters();        // clear the parms
                         pstmt2.setString(1, memid_new);
                         pstmt2.setString(2, lname_new);
                         pstmt2.setString(3, fname_new);
                         pstmt2.setString(4, mi_new);
                         pstmt2.setString(5, mship_new);
                         pstmt2.setString(6, mtype_new);
                         pstmt2.setString(7, email_new);
                         pstmt2.setString(8, mNum_new);
                         pstmt2.setString(9, ghin_new);
                         pstmt2.setString(10, bag_new);
                         pstmt2.setInt(11, birth_new);
                         pstmt2.setString(12, posid_new);
                         pstmt2.setString(13, msub_type_new);
                         pstmt2.setString(14, email2_new);
                         pstmt2.setString(15, phone_new);
                         pstmt2.setString(16, phone2_new);
                         pstmt2.setString(17, suffix_new);
                         pstmt2.setString(18, webid_new);
                         pstmt2.setString(19, gender);
                         pstmt2.setInt(20, pri_indicator);
                         pstmt2.setInt(21, email_bounce1);
                         pstmt2.setInt(22, email_bounce2);

                         pstmt2.setString(23, memid);
                         pstmt2.executeUpdate();

                         pstmt2.close();              // close the stmt


                      } else {                      // member NOT found - add it if we can

                         if (dup == false) {        // if not duplicate member

                            //
                            //  New member is ok - add it
                            //
                            pstmt2 = con.prepareStatement (
                               "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                               "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                               "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, webid, " +
                               "last_sync_date, gender, pri_indicator) " +
                               "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,?,?,?,?,'',?,?, now(),?,?)");

                            pstmt2.clearParameters();        // clear the parms
                            pstmt2.setString(1, memid);        // put the parm in stmt
                            pstmt2.setString(2, password);
                            pstmt2.setString(3, lname);
                            pstmt2.setString(4, fname);
                            pstmt2.setString(5, mi);
                            pstmt2.setString(6, mship);
                            pstmt2.setString(7, mtype);
                            pstmt2.setString(8, email);
                            pstmt2.setFloat(9, c_hcap);
                            pstmt2.setFloat(10, u_hcap);
                            pstmt2.setString(11, mNum);
                            pstmt2.setString(12, ghin);
                            pstmt2.setString(13, bag);
                            pstmt2.setInt(14, birth);
                            pstmt2.setString(15, posid);
                            pstmt2.setString(16, msub_type);
                            pstmt2.setString(17, email2);
                            pstmt2.setString(18, phone);
                            pstmt2.setString(19, phone2);
                            pstmt2.setString(20, suffix);
                            pstmt2.setString(21, webid);
                            pstmt2.setString(22, gender);
                            pstmt2.setInt(23, pri_indicator);
                            pstmt2.executeUpdate();          // execute the prepared stmt

                            pstmt2.close();              // close the stmt

                         } else {     // this member not found, but name already exists

                            if (dup) {
                                    errCount++;
                                    errMsg = errMsg + "\n  -Dup user found:\n" +
                                            "    new: memid = " + memid + "  :  cur: " + dupuser + "\n" +
                                            "         webid = " + webid + "  :       " + dupwebid + "\n" +
                                            "         mNum  = " + mNum  + "  :       " + dupmnum;
                            }

                            if (club.equals( "bentwaterclub" ) && !dupuser.equals("")) {

                               //
                               // Bentwater CC - Duplicate member name found.  This is not uncommon for this club.
                               //                We must accept the member record with the highest priority mship type.
                               //                Members are property owners and can own multiple properties, each with a
                               //
                               //      Order of Priority:
                               //           GPM
                               //           DOP
                               //           DCC
                               //           DOC
                               //           DGC
                               //           MGM
                               //           DOM
                               //           SCM
                               //           EMP
                               //           DSS
                               //           DCL
                               //           VSG
                               //           S
                               //
                               boolean switchMship = false;

                               if (mship.equals("GPM")) {     // if new record has highest mship value

                                  switchMship = true;         // update existing record to this mship

                               } else {

                                 if (mship.equals("DOP")) {

                                    if (!dupmship.equals("GPM")) {   // if existing mship is lower than new one

                                        switchMship = true;         // update existing record to this mship
                                    }

                                  } else {

                                    if (mship.equals("DCC")) {

                                       if (!dupmship.equals("GPM") && !dupmship.equals("DOP")) {   // if existing mship is lower than new one

                                           switchMship = true;         // update existing record to this mship
                                       }

                                     } else {

                                       if (mship.equals("DOC")) {

                                          if (!dupmship.equals("GPM") && !dupmship.equals("DOP") && !dupmship.equals("DCC")) {

                                              switchMship = true;         // update existing record to this mship
                                          }

                                        } else {

                                          if (mship.equals("DGC")) {

                                             if (!dupmship.equals("GPM") && !dupmship.equals("DOP") && !dupmship.equals("DCC") &&
                                                 !dupmship.equals("DOC")) {

                                                 switchMship = true;         // update existing record to this mship
                                             }

                                           } else {

                                             if (mship.equals("MGM")) {

                                                if (!dupmship.equals("GPM") && !dupmship.equals("DOP") && !dupmship.equals("DCC") &&
                                                    !dupmship.equals("DOC") && !dupmship.equals("DGC")) {

                                                    switchMship = true;         // update existing record to this mship
                                                }

                                              } else {

                                                if (mship.equals("DOM")) {

                                                   if (!dupmship.equals("GPM") && !dupmship.equals("DOP") && !dupmship.equals("DCC") &&
                                                       !dupmship.equals("DOC") && !dupmship.equals("DGC") && !dupmship.equals("MGM")) {

                                                       switchMship = true;         // update existing record to this mship
                                                   }

                                                 } else {

                                                    if (mship.equals("SCM")) {

                                                      if (dupmship.equals("EMP") || dupmship.equals("DSS") || dupmship.equals("DCL") ||
                                                          dupmship.equals("VSG") || dupmship.equals("S")) {

                                                          switchMship = true;         // update existing record to this mship
                                                      }

                                                    } else {

                                                       if (mship.equals("EMP")) {

                                                         if (dupmship.equals("DSS") || dupmship.equals("DCL") ||
                                                             dupmship.equals("VSG") || dupmship.equals("S")) {

                                                             switchMship = true;         // update existing record to this mship
                                                         }

                                                       } else {

                                                          if (mship.equals("DSS")) {

                                                            if (dupmship.equals("DCL") ||
                                                                dupmship.equals("VSG") || dupmship.equals("S")) {

                                                                switchMship = true;         // update existing record to this mship
                                                            }

                                                          } else {

                                                             if (mship.equals("DCL")) {

                                                               if (dupmship.equals("VSG") || dupmship.equals("S")) {

                                                                   switchMship = true;         // update existing record to this mship
                                                               }

                                                             } else {

                                                                 if (mship.equals("VSG")) {

                                                                  if (dupmship.equals("S")) {

                                                                      switchMship = true;         // update existing record to this mship
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
                               //  If we must switch the mship type, update the existing record to reflect the higher pri mship
                               //
                               if (switchMship == true) {

                                  pstmt2 = con.prepareStatement (
                                  "UPDATE member2b SET " +
                                  "username = ?, m_ship = ?, memNum = ?, posid = ?, webid = ? " +
                                  "WHERE username = ?");

                                  pstmt2.clearParameters();        // clear the parms
                                  pstmt2.setString(1, memid);      // use this username so record gets updated correctly next time
                                  pstmt2.setString(2, mship);
                                  pstmt2.setString(3, mNum);
                                  pstmt2.setString(4, posid);
                                  pstmt2.setString(5, webid);
                                  pstmt2.setString(6, dupuser);     // update existing record - keep username, change others
                                  pstmt2.executeUpdate();

                                  pstmt2.close();              // close the stmt

                                  userChanged = true;          // indicate username changed

                                  memid_new = memid;           // new username
                                  memid = dupuser;             // old username
                                  fname_new = fname;
                                  mi_new = mi;
                                  lname_new = lname;
                               }

                            }               // end of IF Bentwater Club and dup user
                         }
                      }               // end of IF Member Found

                      //
                      //  Member updated - now see if the username or name changed
                      //
                      if (userChanged == true || nameChanged == true) {        // if username or name changed

                         //
                         //  username or name changed - we must update other tables now
                         //
                         StringBuffer mem_name = new StringBuffer( fname_new );       // get the new first name

                         if (!mi_new.equals( "" )) {
                            mem_name.append(" " +mi_new);               // new mi
                         }
                         mem_name.append(" " +lname_new);               // new last name

                         String newName = mem_name.toString();          // convert to one string

                         Admin_editmem.updTeecurr(newName, memid_new, memid, con);      // update teecurr with new values

                         Admin_editmem.updTeepast(newName, memid_new, memid, con);      // update teepast with new values

                         Admin_editmem.updLreqs(newName, memid_new, memid, con);        // update lreqs with new values

                         Admin_editmem.updPartner(memid_new, memid, con);               // update partner with new values

                         Admin_editmem.updEvents(newName, memid_new, memid, con);        // update evntSignUp with new values

                         Admin_editmem.updLessons(newName, memid_new, memid, con);       // update the lesson books with new values
                      }
                  }
                  catch (Exception e3b) {
                      errCount++;
                      errMsg = errMsg + "\n  -Error2 processing roster for " +club+ "\n" +
                              "    line = " +line+ ": " + e3b.getMessage();   // build msg
                  }

               } else {

                   // Only report errors that AREN'T due to skip == true, since those were handled earlier!
                   if (!found) {
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -MEMBER NOT FOUND!";
                   }
                   if (fname.equals("")) {
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -FIRST NAME missing!";
                   }
                   if (lname.equals("")) {
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -LAST NAME missing!";
                   }
                   if (memid.equals("")) {
                       errCount++;
                       errMsg = errMsg + "\n" +
                               "  -USERNAME missing!";
                   }
               }  // end of IF skip

            }   // end of IF minimum requirements

         }   // end of IF tokens

         // log any errors and warnings that occurred
         if (errCount > 0) {
             totalErrCount += errCount;
             errList.add(errMemInfo + "\n  *" + errCount + " error(s) found*" + errMsg + "\n");
         }
         if (warnCount > 0) {
             totalWarnCount += warnCount;
             warnList.add(errMemInfo + "\n  *" + warnCount + " warning(s) found*" + warnMsg + "\n");
         }
      }   // end of while (for each record in club's file)

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {     // if we processed this club

         pstmt2 = con.prepareStatement (
           "UPDATE member2b SET inact = 1 " +
           "WHERE last_sync_date != now() AND last_sync_date != '0000-00-00'");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.executeUpdate();

         pstmt2.close();              // close the stmt
         
         
         //
         //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
         //
         setRSind(con, club);
      }


      //
      //  Send an email to us and MF support if any dup names found
      //
      if (sendemail == true) {

         Properties properties = new Properties();
         properties.put("mail.smtp.host", host);                      // set outbound host address
         properties.put("mail.smtp.port", port);                      // set outbound port
         properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

         Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

         MimeMessage message = new MimeMessage(mailSess);

         try {

            message.setFrom(new InternetAddress(efrom));                               // set from addr

            message.setSubject( subject );                                            // set subject line
            message.setSentDate(new java.util.Date());                                // set date/time sent
         }
         catch (Exception ignore) {
         }

         //message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailFT));   // set our support email addr

         message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailMF));   // add MF email addr

         emailMsg1 = emailMsg1 + emailMsg2;      // add trailer msg

         try {
            message.setText( emailMsg1 );  // put msg in email text area

            Transport.send(message);     // send it!!
         }
         catch (Exception ignore) {
         }

      }

   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster for " +club+ ": " + e3.getMessage() + "\n";   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
   }

   // Print error and warning count totals to error log
   SystemUtils.logErrorToFile("" +
           "Total Errors Found: " + totalErrCount + "\n" +
           "Total Warnings Found: " + totalWarnCount + "\n", club, true);

      // Print errors and warnings to error log
   if (totalErrCount > 0) {
       SystemUtils.logErrorToFile("" +
            "********************************************************************\n" +
            "****ERRORS FOR " + club + " (Member WAS NOT synced!)\n" +
            "********************************************************************\n", club, true);
       while (errList.size() > 0) {
           SystemUtils.logErrorToFile(errList.remove(0), club, true);
       }
   }
   if (totalWarnCount > 0) {
       SystemUtils.logErrorToFile("" +
            "********************************************************************\n" +
            "****WARNINGS FOR " + club + " (Member MAY NOT have synced!)\n" +
            "********************************************************************\n", club, true);
       while (warnList.size() > 0) {
           SystemUtils.logErrorToFile(warnList.remove(0), club, true);
       }
   }

   // Print end tiem to error log
   SystemUtils.logErrorToFile("End time: " + new java.util.Date().toString() + "\n", club, true);
   
 }    // end of MFirst


 //
 //   Method to process files from NorthStar POS system
 //
 private static void northstarSync(Connection con, InputStreamReader isr, String club) {

   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;

   // Values from NStar records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String mobile = "";
   String primary = "";

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;

   // Values from ForeTees records
   //
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;

   // Values for New ForeTees records
   //
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int rcount = 0;

   String errorMsg = "Error in Common_sync.northStarSync: ";

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean headerFound = false;
   boolean found = false;


   try {

      BufferedReader br = new BufferedReader(isr);

      //   format of each line in the file:
      //
      //     memid, mNum, fname, mi, lname, suffix, mship, mtype, gender, email, email2,
      //     phone, phone2, bag, hndcp#, uhndcp, chndcp, birth, posid, primary
      //
      //
      while (true) {

         line = br.readLine();

         if (line == null) {
            break;
         }

         //  Skip the 1st row (header row)

         if (headerFound == false) {

            headerFound = true;

         } else {

            //  Remove the dbl quotes and check for embedded commas

            line = cleanRecord( line );

            rcount++;                          // count the records

            //  parse the line to gather all the info

            StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

            if ( tok.countTokens() > 19 ) {     // enough data ?

               memid = tok.nextToken();
               mNum = tok.nextToken();
               fname = tok.nextToken();
               mi = tok.nextToken();
               lname = tok.nextToken();
               suffix = tok.nextToken();
               mship = tok.nextToken();
               mtype = tok.nextToken();
               gender = tok.nextToken();
               email = tok.nextToken();
               email2 = tok.nextToken();
               phone = tok.nextToken();
               phone2 = tok.nextToken();
               bag = tok.nextToken();
               ghin = tok.nextToken();
               u_hndcp = tok.nextToken();
               c_hndcp = tok.nextToken();
               temp = tok.nextToken();
               posid = tok.nextToken();
               primary = tok.nextToken();

               //
               //  Check for ? (not provided)
               //
               if (memid.equals( "?" )) {

                  memid = "";
               }
               if (mNum.equals( "?" )) {

                  mNum = "";
               }
               if (fname.equals( "?" )) {

                  fname = "";
               }
               if (mi.equals( "?" )) {

                  mi = "";
               }
               if (lname.equals( "?" )) {

                  lname = "";
               }
               if (suffix.equals( "?" )) {

                  suffix = "";
               }
               if (mship.equals( "?" )) {

                  mship = "";
               }
               if (mtype.equals( "?" )) {

                  mtype = "";
               }
               if (gender.equals( "?" )) {

                  gender = "";
               }
               if (email.equals( "?" )) {

                  email = "";
               }
               if (email2.equals( "?" )) {

                  email2 = "";
               }
               if (phone.equals( "?" )) {

                  phone = "";
               }
               if (phone2.equals( "?" )) {

                  phone2 = "";
               }
               if (bag.equals( "?" )) {

                  bag = "";
               }
               if (ghin.equals( "?" )) {

                  ghin = "";
               }
               if (u_hndcp.equals( "?" )) {

                  u_hndcp = "";
               }
               if (c_hndcp.equals( "?" )) {

                  c_hndcp = "";
               }
               if (temp.equals( "?" ) || temp.equals( "0" )) {

                  temp = "";
               }
               if (posid.equals( "?" )) {

                  posid = "";
               }
               if (primary.equals( "?" )) {

                  primary = "";
               }

               //
               //  Determine if we should process this record (does it meet the minimum requirements?)
               //
               if (!memid.equals( "" ) && !mNum.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                  //
                  //  Remove spaces, etc. from name fields
                  //
                  tok = new StringTokenizer( fname, " " );     // delimiters are space

                  fname = tok.nextToken();                     // remove any spaces and middle name

                  if ( tok.countTokens() > 0 ) {

                     mi = tok.nextToken();                     // over-write mi if already there
                  }

                  if (!suffix.equals( "" )) {                     // if suffix provided

                     tok = new StringTokenizer( suffix, " " );     // delimiters are space

                     suffix = tok.nextToken();                     // remove any extra (only use one value)
                  }

                  tok = new StringTokenizer( lname, " " );     // delimiters are space

                  lname = tok.nextToken();                     // remove suffix and spaces

                  if (!suffix.equals( "" )) {                  // if suffix provided

                     lname = lname + "_" + suffix;             // append suffix to last name

                  } else {                                     // sufix after last name ?

                     if ( tok.countTokens() > 0 ) {

                        suffix = tok.nextToken();
                        lname = lname + "_" + suffix;          // append suffix to last name
                     }
                  }

                  //
                  //  Determine the handicaps
                  //
                  u_hcap = -99;                    // indicate no hndcp
                  c_hcap = -99;                    // indicate no c_hndcp

                  if (!u_hndcp.equals( "" ) && !u_hndcp.equalsIgnoreCase("NH") && !u_hndcp.equalsIgnoreCase("NHL")) {

                     u_hndcp = u_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                     u_hndcp = u_hndcp.replace('H', ' ');    //         or 'H' if present
                     u_hndcp = u_hndcp.replace('N', ' ');    //         or 'N' if present
                     u_hndcp = u_hndcp.replace('J', ' ');    //         or 'J' if present
                     u_hndcp = u_hndcp.replace('R', ' ');    //         or 'R' if present
                     u_hndcp = u_hndcp.trim();

                     u_hcap = Float.parseFloat(u_hndcp);                   // usga handicap

                     if ((!u_hndcp.startsWith("+")) && (!u_hndcp.startsWith("-"))) {

                        u_hcap = 0 - u_hcap;                       // make it a negative hndcp (normal)
                     }
                  }

                  if (!c_hndcp.equals( "" ) && !c_hndcp.equalsIgnoreCase("NH") && !c_hndcp.equalsIgnoreCase("NHL")) {

                     c_hndcp = c_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                     c_hndcp = c_hndcp.replace('H', ' ');    //         or 'H' if present
                     c_hndcp = c_hndcp.replace('N', ' ');    //         or 'N' if present
                     c_hndcp = c_hndcp.replace('J', ' ');    //         or 'J' if present
                     c_hndcp = c_hndcp.replace('R', ' ');    //         or 'R' if present
                     c_hndcp = c_hndcp.trim();

                     c_hcap = Float.parseFloat(c_hndcp);                   // usga handicap

                     if ((!c_hndcp.startsWith("+")) && (!c_hndcp.startsWith("-"))) {

                        c_hcap = 0 - c_hcap;                       // make it a negative hndcp (normal)
                     }
                  }

                  //
                  //  convert birth date (yyyy-mm-dd to yyyymmdd)
                  //
                  if (!temp.equals( "" )) {

                     tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                     String b1 = tok.nextToken();
                     String b2 = tok.nextToken();
                     String b3 = tok.nextToken();

                     int yy = Integer.parseInt(b1);
                     int mm = Integer.parseInt(b2);
                     int dd = Integer.parseInt(b3);

                     birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                     if (yy < 1900) {                             // check for invalid date

                        birth = 0;
                     }

                  } else {

                     birth = 0;
                  }

                  password = lname;

                  //
                  //  if lname is less than 4 chars, fill with 1's
                  //
                  int length = password.length();

                  while (length < 4) {

                     password = password + "1";
                     length++;
                  }

                  //
                  //  Verify the email addresses
                  //
                  if (!email.equals( "" )) {      // if specified

                     email = email.trim();           // remove spaces

                     FeedBack feedback = (member.isEmailValid(email));

                     if (!feedback.isPositive()) {    // if error

                        email = "";                   // do not use it
                     }
                  }
                  if (!email2.equals( "" )) {      // if specified

                     email2 = email2.trim();           // remove spaces

                     FeedBack feedback = (member.isEmailValid(email2));

                     if (!feedback.isPositive()) {    // if error

                        email2 = "";                   // do not use it
                     }
                  }

                  // if email #1 is empty then assign email #2 to it
                  if (email.equals("")) email = email2;

                  skip = false;
                  found = false;        // default to club NOT found

                  //
                  // *********************************************************************
                  //
                  //   The following will be dependent on the club - customized
                  //
                  // *********************************************************************
                  //
                  if (club.equals( "brooklawn" )) {       // Grapevine Web Site Provider - NorthStar Roster Sync

                     found = true;        // club found

                     //
                     //  Determine if we should process this record  (per Judy Barbagallo)
                     //
                     if (!mtype.equals( "" ) && !mship.equals( "" ) && !mship.startsWith( "Special" ) &&
                         !mship.startsWith( "Other" ) && !mship.startsWith( "Resign" ) &&
                         !mship.startsWith( "Transitional" ) && !mship.startsWith( "Senior Plus" )) {


                        //  clean up mNum
                        if (!mNum.equals("")) {

                            mNum = mNum.toUpperCase();

                            if (mNum.length() > 2 && (mNum.endsWith("S1") || mNum.endsWith("C1") || mNum.endsWith("C2") || mNum.endsWith("C3") || mNum.endsWith("C4") ||
                                mNum.endsWith("C5") || mNum.endsWith("C6") || mNum.endsWith("C7") || mNum.endsWith("C8") || mNum.endsWith("C9"))) {

                                mNum = mNum.substring(0, mNum.length() - 2);

                            } else if (mNum.length() > 1 && mNum.endsWith("S")) {

                                mNum = mNum.substring(0, mNum.length() - 1);
                            }
                        }

                        //
                        //  set defaults
                        //
                        if (gender.equalsIgnoreCase( "female" )) {

                           gender = "F";               // Female

                        } else {

                           gender = "M";               // default to Male
                        }

                        suffix = "";     // done with suffix for now

                        //
                        //  Set the Member Type
                        //
                        if (mtype.equalsIgnoreCase( "primary" )) {

                           mtype = "Primary Male";

                           if (gender.equals( "F" )) {

                              mtype = "Primary Female";
                           }

                        } else if (mtype.equalsIgnoreCase( "spouse" )) {

                              mtype = "Spouse Male";

                              if (gender.equals( "F" )) {

                                 mtype = "Spouse Female";
                              }

                        } else if (!mtype.equalsIgnoreCase("maid") && !mtype.equalsIgnoreCase("other")) {

                              mtype = "Dependent";

                        } else {

                            skip = true;
                        }

                        //
                        //  Determine the age in years
                        //
                        Calendar cal = new GregorianCalendar();       // get todays date

                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH) +1;
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        year = year - 24;             // backup 24 years

                        int oldDate = (year * 10000) + (month * 100) + day;   // get date

                        if (mtype.equals("Dependent") && birth > 0 && birth < oldDate) {

                            skip = true;
                        }

                     } else {

                        skip = true;             // skip this record
                     }

                  }         // end of IF club = brooklawn


                  if (club.equals( "bellevuecc" )) {       // Bellevue CC

                     found = true;        // club found

                     //
                     //  Determine if we should process this record - skip 'House', 'House/Pool' and 'Country Clubs' (do this below!!)
                     //
                     if (!mtype.equals( "" ) && !mship.equals( "" )) {

                        //
                        //  Strip any leading zeros from username
                        //
                        if (memid.startsWith( "0" )) {

                           memid = remZeroS( memid );
                        }
                        if (memid.startsWith( "0" )) {        // in case there are 2 zeros

                           memid = remZeroS( memid );
                        }

                        //
                        //  Set the Membership Type
                        //
                        if (mship.startsWith( "Assoc" )) {          // if Associate...

                           mship = "Associate";
                        }

                        if (mship.startsWith( "Junior" )) {          // if Junior...

                           mship = "Junior";
                        }

                        if (mship.equalsIgnoreCase("House") || mship.equalsIgnoreCase("House/Pool") || mship.equalsIgnoreCase("Country Clubs")) {
                            skip = true;
                        }
                        //
                        //  set defaults
                        //
                        if (gender.equalsIgnoreCase( "female" )) {

                           gender = "F";               // Female

                        } else {

                           gender = "M";               // default to Male
                        }

                        suffix = "";     // done with suffix for now

                        //
                        //  Set the Member Type
                        //
                        if (mtype.equalsIgnoreCase( "primary" )) {

                           mtype = "Primary Male";

                           if (gender.equals( "F" )) {

                              mtype = "Primary Female";
                           }

                        } else {

                           if (mtype.equalsIgnoreCase( "spouse" )) {

                              mtype = "Spouse Male";

                              if (gender.equals( "F" )) {

                                 mtype = "Spouse Female";
                              }

                           } else {

                              mtype = "Dependent";
                           }

                           // Search or primary and use their mship
                           try {
                               String tempmNum = mNum;

                               while (tempmNum.startsWith("0")) {
                                   tempmNum = tempmNum.substring(1);
                               }

                               PreparedStatement belstmt = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                               belstmt.clearParameters();
                               belstmt.setString(1, tempmNum);

                               ResultSet belrs = belstmt.executeQuery();

                               if (belrs.next()) {
                                   mship = belrs.getString("m_ship");
                               } else {
                                   skip = true;
                               }

                               belstmt.close();

                           } catch (Exception exc) {
                               skip = true;
                           }
                        }

                     } else {

                        skip = true;             // skip this record
                     }

                  }         // end of IF club = ??

                  if (club.equals( "greenacrescountryclub" )) {       // Green Acres CC

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     if (!mtype.equals( "" ) && !mship.equals( "" )) {

                        //
                        //  Strip any leading zeros from username and member number
                        //
                        while (memid.startsWith("0")) {
                            memid = memid.substring(1);
                        }

                        while (mNum.startsWith("0")) {
                            mNum = mNum.substring(1);
                        }

                        //
                        //  Set the Membership Type
                        //
                        if (mship.equalsIgnoreCase("Regular") || mship.startsWith("Female Regular") || mship.equalsIgnoreCase("Honorairium") ||
                            mship.equalsIgnoreCase("Male Hnr") || mship.startsWith("Male Regular") || mship.equalsIgnoreCase("Spec Male Hnr 85/25") ||
                            mship.equalsIgnoreCase("Social 85/50 Years Mbrshp +") || mship.startsWith("Male 3") || mship.startsWith("Female 3")) {

                            mship = "Regular";

                        } else if (mship.equalsIgnoreCase("Female Junior") || mship.equalsIgnoreCase("Female Young Jr 21-24") || mship.equalsIgnoreCase("Male Junior") ||
                                   mship.equalsIgnoreCase("Male Limited Jr 28-29") || mship.equalsIgnoreCase("Male Young Jr 21-24") || mship.equalsIgnoreCase("Male Young Junior 25-27") ||
                                   mship.startsWith("Male Jr")) {

                            mship = "Junior";

                        } else if (mship.startsWith("Social Female") || mship.startsWith("Social Male") || mship.startsWith("Social Junior") ||
                                   mship.startsWith("Social Jr")) {

                            mship = "Social";

                        } else if (mship.startsWith("Loa Soc")) {

                            mship = "LOA Social";

                        } else if (mship.startsWith("Loa Junior") || mship.startsWith("Loa Jr") || mship.startsWith("Loa Ltd Jr") ||
                                   mship.startsWith("Loa  Jr") || mship.equalsIgnoreCase("Loa Male 37")) {

                            mship = "LOA Junior";

                        } else if (mship.equalsIgnoreCase("Loa Regular Male")) {

                            mship = "LOA Regular";

                        } else if (mship.equalsIgnoreCase("Significant Other") || mship.equalsIgnoreCase("Spouse") || mship.equalsIgnoreCase("Child")) {

                            try {
                                pstmt2 = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");

                                pstmt2.clearParameters();
                                pstmt2.setString(1, mNum);

                                rs = pstmt2.executeQuery();

                                if (rs.next()) {
                                    mship = rs.getString("m_ship");
                                } else {
                                    mship = "No Primary Found - " + mship;
                                }

                                pstmt2.close();

                            } catch (Exception exc) {

                                mship = "Error Finding Primary - " + mship;

                            }

                        } else if (mship.startsWith("Pool & Tennis")) {

                            mship = "Pool & Tennis";

                        }

                        else if (!mship.equalsIgnoreCase("Two Week Usage")) {

                            skip = true;    // Skip all others

                        }


                        //  set defaults
                        if (gender.equalsIgnoreCase( "Female" )) {
                           gender = "F";               // Female
                        } else {
                           gender = "M";               // default to Male
                        }

                        // Set mtype
                        if (mtype.equalsIgnoreCase("Primary") || mtype.equalsIgnoreCase("Spouse") || mtype.equalsIgnoreCase("Signi Other")) {
                            if (gender.equals("F")) {
                                mtype = "Adult Female";
                            } else {
                                mtype = "Adult Male";
                            }
                        } else if (mtype.equalsIgnoreCase("Child")) {
                            mtype = "Qualified Child";
                        }

                        suffix = "";     // done with suffix for now

                     } else {

                        skip = true;             // skip this record
                     }

                  }         // end of IF club = greenacrescountryclub

                  //
                  //  Ritz Carlotn CC
                  //
/*
                  if (club.equals( "ritzcarlton" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
  ???                   if (!mtype.equals( "" ) && !mship.equals( "" ) && !mship.startsWith( "????" ) &&
                         !mship.startsWith( "???" ) && !mship.startsWith( "???" )) {

                        //
                        //  set defaults
                        //
                        if (gender.equalsIgnoreCase( "female" )) {

                           gender = "F";               // Female

                        } else {

                           gender = "M";               // default to Male
                        }

                        suffix = "";     // done with suffix for now

                        //
                        //  Set the Member Type
                        //
                        if (mtype.equalsIgnoreCase( "primary" )) {

                           mtype = "Primary Male";

                           if (gender.equals( "F" )) {

                              mtype = "Primary Female";
                           }

                        } else {

                           if (mtype.equalsIgnoreCase( "spouse" )) {

                              mtype = "Spouse Male";

                              if (gender.equals( "F" )) {

                                 mtype = "Spouse Female";
                              }

                           } else {

                              mtype = "Dependent";
                           }
                        }

                        //
                        //  *** TEMP *** currently the birth dates are invalid for all members
                        //
                        birth = 0;

                        //
                        //  Verify the member id's
                        //
                        if (mtype.equalsIgnoreCase( "spouse" )) {

                           if (!memid.endsWith( "s" ) && !memid.endsWith( "S" ) && !memid.endsWith( "s1" ) && !memid.endsWith( "S1" )) {

                              skip = true;             // skip this record
                           }

                        } else {

                           if (mtype.equalsIgnoreCase( "dependent" )) {

                              if (!memid.endsWith( "C1" ) && !memid.endsWith( "C2" ) && !memid.endsWith( "C3" ) && !memid.endsWith( "C4" ) &&
                                  !memid.endsWith( "C5" ) && !memid.endsWith( "C6" ) && !memid.endsWith( "C7" ) && !memid.endsWith( "C8" )) {

                                 skip = true;             // skip this record
                              }
                           }
                        }

                     } else {

                        skip = true;             // skip this record
                     }
                  }         // end of IF club = ritzcarlton
*/


                  //
                  //******************************************************************
                  //  Common processing - add or update the member record
                  //******************************************************************
                  //
                  if (skip == false && found == true && !fname.equals("") && !lname.equals("")) {

                     //
                     //   now determine if we should update an existing record or add the new one
                     //
                     fname_old = "";
                     lname_old = "";
                     mi_old = "";
                     mship_old = "";
                     mtype_old = "";
                     email_old = "";
                     mNum_old = "";
                     ghin_old = "";
                     bag_old = "";
                     posid_old = "";
                     email2_old = "";
                     phone_old = "";
                     phone2_old = "";
                     suffix_old = "";
                     u_hcap_old = 0;
                     c_hcap_old = 0;
                     birth_old = 0;


                     //
                     //  Truncate the string values to avoid sql error
                     //
                     if (!mi.equals( "" )) {       // if mi specified

                        mi = truncate(mi, 1);           // make sure it is only 1 char
                     }
                     if (!memid.equals( "" )) {

                        memid = truncate(memid, 15);
                     }
                     if (!password.equals( "" )) {

                        password = truncate(password, 15);
                     }
                     if (!lname.equals( "" )) {

                        lname = truncate(lname, 20);
                     }
                     if (!fname.equals( "" )) {

                        fname = truncate(fname, 20);
                     }
                     if (!mship.equals( "" )) {

                        mship = truncate(mship, 30);
                     }
                     if (!mtype.equals( "" )) {

                        mtype = truncate(mtype, 30);
                     }
                     if (!email.equals( "" )) {

                        email = truncate(email, 50);
                     }
                     if (!email2.equals( "" )) {

                        email2 = truncate(email2, 50);
                     }
                     if (!mNum.equals( "" )) {

                        mNum = truncate(mNum, 10);
                     }
                     if (!ghin.equals( "" )) {

                        ghin = truncate(ghin, 16);
                     }
                     if (!bag.equals( "" )) {

                        bag = truncate(bag, 12);
                     }
                     if (!posid.equals( "" )) {

                        posid = truncate(posid, 15);
                     }
                     if (!phone.equals( "" )) {

                        phone = truncate(phone, 24);
                     }
                     if (!phone2.equals( "" )) {

                        phone2 = truncate(phone2, 24);
                     }
                     if (!suffix.equals( "" )) {

                        suffix = truncate(suffix, 4);
                     }


                     pstmt2 = con.prepareStatement (
                              "SELECT * FROM member2b WHERE username = ?");

                     pstmt2.clearParameters();               // clear the parms
                     pstmt2.setString(1, memid);            // put the parm in stmt
                     rs = pstmt2.executeQuery();            // execute the prepared stmt

                     if(rs.next()) {

                        lname_old = rs.getString("name_last");
                        fname_old = rs.getString("name_first");
                        mi_old = rs.getString("name_mi");
                        mship_old = rs.getString("m_ship");
                        mtype_old = rs.getString("m_type");
                        email_old = rs.getString("email");
                        mNum_old = rs.getString("memNum");
                        ghin_old = rs.getString("ghin");
                        bag_old = rs.getString("bag");
                        birth_old = rs.getInt("birth");
                        posid_old = rs.getString("posid");
                        email2_old = rs.getString("email2");
                        phone_old = rs.getString("phone1");
                        phone2_old = rs.getString("phone2");
                        suffix_old = rs.getString("name_suf");
                     }
                     pstmt2.close();              // close the stmt

                     if (!fname_old.equals( "" )) {            // if member found

                        changed = false;                       // init change indicator

                        lname_new = lname_old;

                        if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                           lname_new = lname;         // set value from NStar record
                           changed = true;
                        }

                        fname_new = fname_old;

                        fname = fname_old;          // DO NOT change the first names

/*
                        if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                           fname_new = fname;         // set value from NStar record
                           changed = true;
                        }
*/

                        mi_new = mi_old;

                        if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                           mi_new = mi;         // set value from NStar record
                           changed = true;
                        }

                        mship_new = mship_old;

                        if (!mship.equals( "" ) && !mship_old.equals( mship )) {

                           mship_new = mship;         // set value from NStar record
                           changed = true;
                        }

                        mtype_new = mtype_old;

                        if (!club.equals( "ritzcarlton" ) && !club.equals( "brooklawn" )) {   //  do not change mtypes for dependents

                           if (!mtype.equals( "" ) && !mtype.equals( "Dependent" ) && !mtype_old.equals( mtype )) {

                              mtype_new = mtype;         // set value from NStar record
                              changed = true;
                           }
                        }

                        birth_new = birth_old;

                        if (!club.equals( "ritzcarlton" )) {   //  do not change birthdates

                           if (birth > 0 && birth != birth_old) {

                              birth_new = birth;         // set value from NStar record
                              changed = true;
                           }
                        }

                        ghin_new = ghin_old;

                        if (!ghin.equals( "" ) && !ghin_old.equals( ghin )) {

                           ghin_new = ghin;         // set value from NStar record
                           changed = true;
                        }

                        bag_new = bag_old;

                        if (!bag.equals( "" ) && !bag_old.equals( bag )) {

                           bag_new = bag;         // set value from NStar record
                           changed = true;
                        }

                        posid_new = posid_old;

                        if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                           posid_new = posid;         // set value from NStar record
                           changed = true;
                        }

                        phone_new = phone_old;

                        if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                           phone_new = phone;         // set value from NStar record
                           changed = true;
                        }

                        phone2_new = phone2_old;

                        if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                           phone2_new = phone2;         // set value from NStar record
                           changed = true;
                        }

                        suffix_new = suffix_old;

                        if (!suffix.equals( "" ) && !suffix_old.equals( suffix )) {

                           suffix_new = suffix;         // set value from NStar record
                           changed = true;
                        }

                        email_new = email_old;        // do not change emails
                        email2_new = email2_old;

                        //
                        //  update emails if Brooklawn
                        //
                        if (club.equals( "brooklawn" )) {

                           if (!email.equals( "" )) {             // if email provided

                              email_new = email;               // set value from NStar record
                           }

                           if (!email2.equals( "" )) {             // if email provided

                              email2_new = email2;               // set value from NStar record
                           }
                        }



                        // don't allow both emails to be the same
                        if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";


                        mNum_new = mNum_old;          // do not change mNums

                        if (club.equals("brooklawn")) {     // update member numbers if brooklawn
                            if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                                mNum_new = mNum;         // set value from NStar record
                                changed = true;
                            }
                        }


                        //
                        //  Update our record if something has changed
                        //
                        pstmt2 = con.prepareStatement (
                        "UPDATE member2b SET name_last = ?, name_first = ?, " +
                        "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                        "memNum = ?, ghin = ?, bag = ?, birth = ?, posid = ?, email2 = ?, phone1 = ?, " +
                        "phone2 = ?, name_suf = ?, inact = 0, last_sync_date = now(), gender = ? " +
                        "WHERE username = ?");

                        pstmt2.clearParameters();        // clear the parms
                        pstmt2.setString(1, lname_new);
                        pstmt2.setString(2, fname_new);
                        pstmt2.setString(3, mi_new);
                        pstmt2.setString(4, mship_new);
                        pstmt2.setString(5, mtype_new);
                        pstmt2.setString(6, email_new);
                        pstmt2.setString(7, mNum_new);
                        pstmt2.setString(8, ghin_new);
                        pstmt2.setString(9, bag_new);
                        pstmt2.setInt(10, birth_new);
                        pstmt2.setString(11, posid_new);
                        pstmt2.setString(12, email2_new);
                        pstmt2.setString(13, phone_new);
                        pstmt2.setString(14, phone2_new);
                        pstmt2.setString(15, suffix_new);
                        pstmt2.setString(16, gender);
                        pstmt2.setString(17, memid);
                        pstmt2.executeUpdate();

                        pstmt2.close();              // close the stmt

                     } else {

                        //
                        //  New member - first check if name already exists
                        //
                        boolean dup = false;

                        pstmt2 = con.prepareStatement (
                                 "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                        pstmt2.clearParameters();
                        pstmt2.setString(1, lname);
                        pstmt2.setString(2, fname);
                        pstmt2.setString(3, mi);
                        rs = pstmt2.executeQuery();            // execute the prepared stmt

                        if (rs.next()) {

                           dup = true;
                        }
                        pstmt2.close();              // close the stmt

                        if (dup == false) {

                           //
                           //  New member - add it
                           //
                           pstmt2 = con.prepareStatement (
                              "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                              "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                              "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, " +
                              "webid, last_sync_date, gender) " +
                              "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,'',now(),?)");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, memid);        // put the parm in stmt
                           pstmt2.setString(2, password);
                           pstmt2.setString(3, lname);
                           pstmt2.setString(4, fname);
                           pstmt2.setString(5, mi);
                           pstmt2.setString(6, mship);
                           pstmt2.setString(7, mtype);
                           pstmt2.setString(8, email);
                           pstmt2.setFloat(9, c_hcap);
                           pstmt2.setFloat(10, u_hcap);
                           pstmt2.setString(11, mNum);
                           pstmt2.setString(12, ghin);
                           pstmt2.setString(13, bag);
                           pstmt2.setInt(14, birth);
                           pstmt2.setString(15, posid);
                           pstmt2.setString(16, email2);
                           pstmt2.setString(17, phone);
                           pstmt2.setString(18, phone2);
                           pstmt2.setString(19, suffix);
                           pstmt2.setString(20, gender);
                           pstmt2.executeUpdate();          // execute the prepared stmt

                           pstmt2.close();              // close the stmt
                        }
                     }

                  }   // end of IF skip

               }   // end of IF minimum requirements

            }   // end of IF tokens

         }   // end of IF header row

      }   // end of WHILE records in file

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {     // if we processed this club

         pstmt2 = con.prepareStatement (
           "UPDATE member2b SET inact = 1 " +
           "WHERE last_sync_date != now() AND last_sync_date != '0000-00-00'");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.executeUpdate();

         pstmt2.close();              // close the stmt
         
         
         //
         //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
         //
         setRSind(con, club);
      }

   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster (record #" +rcount+ ") for " +club+ ", line = " +line+ ": " + e3.getMessage();   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
      errorMsg = "Error in Common_sync.northStarSync: ";                               // reset the msg
   }


   //
   //  Bellevue - now change the mship types of all spouses and dependents to match the primary, then remove the unwanted mships
   //
   /*
   if (club.equals( "bellevuecc" )) {       // Bellevue CC

      try {

         //
         //  get all primary members
         //
         String mtype1 = "Primary Male";
         String mtype2 = "Primary Female";

         pstmt2 = con.prepareStatement (
                  "SELECT m_ship, memNum FROM member2b WHERE m_type = ? OR m_type = ?");

         pstmt2.clearParameters();               // clear the parms
         pstmt2.setString(1, mtype1);
         pstmt2.setString(2, mtype2);
         rs = pstmt2.executeQuery();            // execute the prepared stmt

         while(rs.next()) {

            mship = rs.getString("m_ship");
            mNum = rs.getString("memNum");

            //
            //  Set mship in all members with matching mNum
            //
            pstmt1 = con.prepareStatement (
            "UPDATE member2b SET m_ship = ? " +
            "WHERE memNum = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, mship);
            pstmt1.setString(2, mNum);
            pstmt1.executeUpdate();

            pstmt1.close();              // close the stmt

         }     // end of WHILE primary members

         pstmt2.close();              // close the stmt


         String mship1 = "House";
         String mship2 = "House/Pool";
         String mship3 = "Country Clubs";

         //
         //  Remove the 'House', 'House/Pool' and 'Country Clubs' mship types
         //
         pstmt1 = con.prepareStatement (
         "DELETE FROM member2b " +
         "WHERE m_ship = ? OR m_ship = ? OR m_ship = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, mship1);
         pstmt1.setString(2, mship2);
         pstmt1.setString(3, mship3);
         pstmt1.executeUpdate();

         pstmt1.close();              // close the stmt

      }
      catch (Exception e3) {

         errorMsg = errorMsg + " Error processing roster for " +club+ ", setting mship values: " + e3.getMessage();   // build msg
         SystemUtils.logError(errorMsg);                                                  // log it
         errorMsg = "Error in Common_sync.northStarSync: ";                               // reset the msg
      }

   }   // end of IF Bellevue
*/
   
 }     // end of NorthStar processing


 //
 //   Method to process files from ClubEssential system
 //
 private static void ceSync(Connection con, InputStreamReader isr, String club) {

   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;
   int ext = 1;

   // Values from CE records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String webid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String mobile = "";
   String primary = "";
   String active = "";
   String webid_new = "";
   String memid_new = "";
   String lnamePart2;

   String mship2 = ""; // used to tell if match was found

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;
   int inact = 0;

   // Values from ForeTees records
   //
   String memid_old = "";
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;
   int inact_old = 0;

   // Values for New ForeTees records
   //
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";
   String last_mship = "";
   String last_mnum = "";
   String openParen = "(";
   String closeParen = ")";
   String asterik = "*";
   String slash = "/";
   String backslash = "\\";

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int inact_new = 0;
   int rcount = 0;
   int pcount = 0;
   int ncount = 0;
   int ucount = 0;
   int errCount = 0;
   int warnCount = 0;
   int totalErrCount = 0;
   int totalWarnCount = 0;
   int email_bounce1 = 0;
   int email_bounce2 = 0;

   String errorMsg = "";
   String errMsg = "";
   String warnMsg = "";
   String errMemInfo = "";

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean headerFound = false;
   boolean found = false;
   boolean useWebid = false;
   boolean genderMissing = false;

   ArrayList<String> errList = new ArrayList<String>();
   ArrayList<String> warnList = new ArrayList<String>();


   // Overwrite log file with fresh one for today's logs
   SystemUtils.logErrorToFile("Clubessential: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

     Calendar caly = new GregorianCalendar();       // get todays date
     int thisYear = caly.get(Calendar.YEAR);        // get this year value

     thisYear = thisYear - 2000;                   // 2 digit value


      BufferedReader br = new BufferedReader(isr);

      //   format of each line in the file:
      //
      //     memid, mNum, fname, mi, lname, suffix, mship, mtype, gender, email, email2,
      //     phone, phone2, bag, hndcp#, uhndcp, chndcp, birth, posid, mobile, primary, act/inact
      //
      //
      while (true) {

         line = br.readLine();

         if (line == null) {
            break;
         }

         //  Skip the 1st row (header row)

         if (headerFound == false) {

            headerFound = true;

         } else {

            //  Remove the dbl quotes and check for embedded commas

            line = cleanRecord( line );

            rcount++;                          // count the records

            //  parse the line to gather all the info

            StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

            if ( tok.countTokens() > 20 ) {     // enough data ?

               memid = tok.nextToken();    // a
               mNum = tok.nextToken();     // b
               fname = tok.nextToken();    // c
               mi = tok.nextToken();       // d
               lname = tok.nextToken();    // e
               suffix = tok.nextToken();   // f
               mship = tok.nextToken();    // g
               mtype = tok.nextToken();    // h
               gender = tok.nextToken();   // i
               email = tok.nextToken();    // j
               email2 = tok.nextToken();   // k
               phone = tok.nextToken();    // l
               phone2 = tok.nextToken();   // m
               bag = tok.nextToken();      // n
               ghin = tok.nextToken();     // o
               u_hndcp = tok.nextToken();  // p
               c_hndcp = tok.nextToken();  // q
               temp = tok.nextToken();     // r
               posid = tok.nextToken();    // s
               mobile = tok.nextToken();   // t
               primary = tok.nextToken();  // u
               active = tok.nextToken();   // v

               //
               //  Check for ? (not provided)
               //
               if (memid.equals( "?" )) {

                  memid = "";
               }
               if (mNum.equals( "?" )) {

                  mNum = "";
               }
               if (fname.equals( "?" )) {

                  fname = "";
               }
               if (mi.equals( "?" )) {

                  mi = "";
               }
               if (lname.equals( "?" )) {

                  lname = "";
               }
               if (suffix.equals( "?" )) {

                  suffix = "";
               }
               if (mship.equals( "?" )) {

                  mship = "";
               }
               if (mtype.equals( "?" )) {

                  mtype = "";
               }
               if (gender.equals( "?" ) || (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F"))) {

                  if (gender.equals( "?" )) {
                      genderMissing = true;
                  } else {
                      genderMissing = false;
                  }
                  gender = "";
               }
               if (email.equals( "?" )) {

                  email = "";
               }
               if (email2.equals( "?" )) {

                  email2 = "";
               }
               if (phone.equals( "?" )) {

                  phone = "";
               }
               if (phone2.equals( "?" )) {

                  phone2 = "";
               }
               if (bag.equals( "?" )) {

                  bag = "";
               }
               if (ghin.equals( "?" )) {

                  ghin = "";
               }
               if (u_hndcp.equals( "?" )) {

                  u_hndcp = "";
               }
               if (c_hndcp.equals( "?" )) {

                  c_hndcp = "";
               }
               if (temp.equals( "?" ) || temp.equals( "0" )) {

                  temp = "";
               }
               if (posid.equals( "?" )) {

                  posid = "";
               }
               if (mobile.equals( "?" )) {

                  mobile = "";
               }
               if (primary.equals( "?" )) {

                  primary = "";
               }
               if (active.equals( "?" )) {

                  active = "";
               }


               //
               //   Westchester memids will be empty for dependents
               //
               if (club.equals( "westchester" ) && memid.equals( "" )) {

                  memid = mNum;            // use mNum
               }


               if (club.equals( "virginiacc" )) {

                  //
                  //  Make sure name is titled
                  //
                  fname = toTitleCase(fname);
                  lname = toTitleCase(lname);
               }


               //
               //  Ignore mi if not alpha
               //
               if (mi.endsWith( "0" ) || mi.endsWith( "1" ) || mi.endsWith( "2" ) || mi.endsWith( "3" ) ||
                   mi.endsWith( "4" ) || mi.endsWith( "5" ) || mi.endsWith( "6" ) || mi.endsWith( "7" ) ||
                   mi.endsWith( "8" ) || mi.endsWith( "9" )) {

                  mi = "";
               }


               tok = new StringTokenizer( lname, openParen );     // check for open paren '('

               if ( tok.countTokens() > 1 ) {

                  lname = tok.nextToken();                        // skip open paren and anything following it
               }

               tok = new StringTokenizer( lname, slash );         // check for slash

               if ( tok.countTokens() > 1 ) {

                  lname = tok.nextToken();                        // skip them and anything following it
               }

               tok = new StringTokenizer( lname, backslash );         // check for backslash

               if ( tok.countTokens() > 1 ) {

                  lname = tok.nextToken();                        // skip them and anything following it
               }

               tok = new StringTokenizer( fname, openParen );     // check for open paren '('

               if ( tok.countTokens() > 1 ) {

                  fname = tok.nextToken();                        // skip open paren and anything following it
               }

               tok = new StringTokenizer( fname, "/\\" );        // check for slash and backslash

               if ( tok.countTokens() > 1 ) {

                  fname = tok.nextToken();                        // skip them and anything following it
               }


               //
               //  Determine if we should process this record (does it meet the minimum requirements?)
               //
               if (!memid.equals( "" ) && !mNum.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                  //
                  //  Remove spaces, etc. from name fields
                  //
                  tok = new StringTokenizer( fname, " " );     // delimiters are space

                  fname = tok.nextToken();                     // remove any spaces and middle name

                  if ( tok.countTokens() > 0 ) {

                     mi = tok.nextToken();                     // over-write mi if already there

                     if (mi.startsWith("&")) {
                        mi = "";
                     }
                  }

                  if (!suffix.equals( "" )) {                     // if suffix provided

                     tok = new StringTokenizer( suffix, " " );     // delimiters are space

                     suffix = tok.nextToken();                     // remove any extra (only use one value)
                  }

                  tok = new StringTokenizer( lname, " " );     // delimiters are space

                  if ( tok.countTokens() > 0 ) {               // more than just lname?

                     lname = tok.nextToken();                  // remove suffix and spaces
                  }

                  if (suffix.equals( "" )) {                   // if suffix not provided

                     if ( tok.countTokens() > 1 ) {            // check for suffix and 2 part lname (i.e.  Van Buren)

                        lnamePart2 = tok.nextToken();

                        if (!lnamePart2.startsWith("&") && !lnamePart2.startsWith(openParen)) {   // if ok to add

                           lname = lname + lnamePart2;        // combine (i.e. VanBuren)
                        }
                     }

                     if ( tok.countTokens() > 0 ) {           // suffix?

                        suffix = tok.nextToken();

                        if (suffix.startsWith("&")) {
                           suffix = "";
                        }
                     }

                  } else {         // suffix provided in suffix field - check for 2 part lname (i.e.  Van Buren)

                     if ( tok.countTokens() > 0 ) {

                        lnamePart2 = tok.nextToken();

                        if (!lnamePart2.startsWith("&") && !lnamePart2.startsWith("(") && !lnamePart2.equals(suffix)) {   // if ok to add

                           lname = lname + lnamePart2;        // combine (i.e. VanBuren)
                        }
                     }
                  }

                  if (suffix.startsWith(openParen)) {            // if not really a suffix

                     suffix = "";
                  }


                  //
                  //  Isolate the last name in case extra info attached (i.e.  lname..yyyy/nnn)
                  //
                  if (!lname.equals( "" )) {

                     tok = new StringTokenizer( lname, asterik );     // delimiters are slashes, asterics (backslash needs 2)

                     if ( tok.countTokens() > 0 ) {

                        lname = tok.nextToken();                     // isolate lname
                     }

                     tok = new StringTokenizer( lname, slash );

                     if ( tok.countTokens() > 0 ) {

                        lname = tok.nextToken();
                     }

                     tok = new StringTokenizer( lname, backslash );

                     if ( tok.countTokens() > 0 ) {

                        lname = tok.nextToken();
                     }
                  }

                  //
                  //  Append the suffix to last name if it exists and isn't already appended
                  //
                  if (!suffix.equals("")) {

                     if (!lname.endsWith(suffix)) {

                        lname = lname + "_" + suffix;      // append it
                     }

                     suffix = "";                          // done with it now
                  }


                  //
                  //  Determine the handicaps
                  //
                  u_hcap = -99;                    // indicate no hndcp
                  c_hcap = -99;                    // indicate no c_hndcp

                  if (!u_hndcp.equals( "" ) && !u_hndcp.equalsIgnoreCase("NH") && !u_hndcp.equalsIgnoreCase("NHL")) {

                     u_hndcp = u_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                     u_hndcp = u_hndcp.replace('H', ' ');    //         or 'H' if present
                     u_hndcp = u_hndcp.replace('N', ' ');    //         or 'N' if present
                     u_hndcp = u_hndcp.replace('J', ' ');    //         or 'J' if present
                     u_hndcp = u_hndcp.replace('R', ' ');    //         or 'R' if present
                     u_hndcp = u_hndcp.trim();

                     u_hcap = Float.parseFloat(u_hndcp);                   // usga handicap

                     if ((!u_hndcp.startsWith("+")) && (!u_hndcp.startsWith("-"))) {

                        u_hcap = 0 - u_hcap;                       // make it a negative hndcp (normal)
                     }
                  }

                  if (!c_hndcp.equals( "" ) && !c_hndcp.equalsIgnoreCase("NH") && !c_hndcp.equalsIgnoreCase("NHL")) {

                     c_hndcp = c_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                     c_hndcp = c_hndcp.replace('H', ' ');    //         or 'H' if present
                     c_hndcp = c_hndcp.replace('N', ' ');    //         or 'N' if present
                     c_hndcp = c_hndcp.replace('J', ' ');    //         or 'J' if present
                     c_hndcp = c_hndcp.replace('R', ' ');    //         or 'R' if present
                     c_hndcp = c_hndcp.trim();

                     c_hcap = Float.parseFloat(c_hndcp);                   // usga handicap

                     if ((!c_hndcp.startsWith("+")) && (!c_hndcp.startsWith("-"))) {

                        c_hcap = 0 - c_hcap;                       // make it a negative hndcp (normal)
                     }
                  }

                  //
                  //  convert birth date (mm/dd/yyyy to yyyymmdd)
                  //
                  if (!temp.equals( "" )) {

                     int mm = 0;
                     int dd = 0;
                     int yy = 0;

                     tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                     if ( tok.countTokens() > 2 ) {

                        String b1 = tok.nextToken();
                        String b2 = tok.nextToken();
                        String b3 = tok.nextToken();

                        mm = Integer.parseInt(b1);
                        dd = Integer.parseInt(b2);
                        yy = Integer.parseInt(b3);

                     } else {                               // try 'Jan 20, 1951' format

                        tok = new StringTokenizer( temp, ", " );          // delimiters are comma and space

                        if ( tok.countTokens() > 2 ) {

                           String b1 = tok.nextToken();
                           String b2 = tok.nextToken();
                           String b3 = tok.nextToken();

                           if (b1.startsWith( "Jan" )) {
                              mm = 1;
                           } else {
                            if (b1.startsWith( "Feb" )) {
                               mm = 2;
                            } else {
                             if (b1.startsWith( "Mar" )) {
                                mm = 3;
                             } else {
                              if (b1.startsWith( "Apr" )) {
                                 mm = 4;
                              } else {
                               if (b1.startsWith( "May" )) {
                                  mm = 5;
                               } else {
                                if (b1.startsWith( "Jun" )) {
                                   mm = 6;
                                } else {
                                 if (b1.startsWith( "Jul" )) {
                                    mm = 7;
                                 } else {
                                  if (b1.startsWith( "Aug" )) {
                                     mm = 8;
                                  } else {
                                   if (b1.startsWith( "Sep" )) {
                                      mm = 9;
                                   } else {
                                    if (b1.startsWith( "Oct" )) {
                                       mm = 10;
                                    } else {
                                     if (b1.startsWith( "Nov" )) {
                                        mm = 11;
                                     } else {
                                      if (b1.startsWith( "Dec" )) {
                                         mm = 12;
                                      } else {
                                         mm = Integer.parseInt(b1);
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

                           dd = Integer.parseInt(b2);
                           yy = Integer.parseInt(b3);

                        } else {

                           birth = 0;
                        }
                     }

                     if (mm > 0) {                              // if birth provided

                        if (mm == 1 && dd == 1 && yy == 1) {        // skip if 1/1/0001

                           birth = 0;

                        } else {

                           if (yy < 100) {                          // if 2 digit year

                              if (yy <= thisYear) {

                                 yy += 2000;          // 20xx

                              } else {

                                 yy += 1900;          // 19xx
                              }
                           }

                           birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                           if (yy < 1900) {                             // check for invalid date

                              birth = 0;
                           }
                        }

                     } else {

                        birth = 0;
                     }

                  } else {

                     birth = 0;
                  }

                  password = lname;

                  //
                  //  if lname is less than 4 chars, fill with 1's
                  //
                  int length = password.length();

                  while (length < 4) {

                     password = password + "1";
                     length++;
                  }

                  //
                  //  Verify the email addresses
                  //
                  if (!email.equals( "" )) {      // if specified

                     email = email.trim();           // remove spaces

                     FeedBack feedback = (member.isEmailValid(email));

                     if (!feedback.isPositive()) {    // if error

                        email = "";                   // do not use it
                     }
                  }
                  if (!email2.equals( "" )) {      // if specified

                     email2 = email2.trim();           // remove spaces

                     FeedBack feedback = (member.isEmailValid(email2));

                     if (!feedback.isPositive()) {    // if error

                        email2 = "";                   // do not use it
                     }
                  }

                  // if email #1 is empty then assign email #2 to it
                  if (email.equals("")) email = email2;


                  skip = false;
                  errCount = 0;         // reset the error counter
                  warnCount = 0;        // reset warning counter
                  errMsg = "";          // reset error message
                  warnMsg = "";         // reset warning message
                  errMemInfo = "";      // reset the error member info
                  found = false;        // default to club NOT found


                  //
                  //  Set the active/inactive flag in case it is used
                  //
                  inact = 0;              // default = active
if (!club.equals("tpcwakefieldplantation")) {
                  if (active.equalsIgnoreCase( "I" )) {

                     inact = 1;           // set inactive
                  }
}

                  //
                  //  Weed out any non-members
                  //
                  if (fname.equalsIgnoreCase("admin") || lname.equalsIgnoreCase("admin") ||
                      fname.equalsIgnoreCase("test") || lname.equalsIgnoreCase("test")) {

                     inact = 1;       // skip this record
                  }

                  //
                  //  Format member info for use in error logging before club-specific manipulation
                  //
                  errMemInfo = "Member Details:\n" +
                                         "  name: " + lname + ", " + fname + " " + mi + "\n" +
                                         "  mtype: " + mtype + "  mship: " + mship + "\n" +
                                         "  memid: " + memid + "  mNum: " + mNum + "  gender: " + gender;


                  // if gender is incorrect or missing, flag a warning in the error log
                  if (gender.equals("")) {
                      warnCount++;
                      if (genderMissing) {
                          warnMsg = warnMsg + "\n" +
                                  "  -GENDER missing! (Defaulted to 'M')";
                      } else {
                          warnMsg = warnMsg + "\n" +
                                  "  -GENDER incorrect! (Defaulted to 'M')";
                      }
                      gender = "M";
                  }

                  //
                  //  Skip entries with no membership type
                  //
                  if (mship.equals("")) {
                      errCount++;
                      skip = true;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  }

                  //
                  //  Skip entries with first/last names of 'admin'
                  //
                  if (fname.equalsIgnoreCase("admin") || lname.equalsIgnoreCase("admin")) {
                      errCount++;
                      skip = true;
                      errMsg = errMsg + "\n" +
                              "  -INVALID NAME! 'Admin' or 'admin' not allowed for first or last name";
                  }

                  //
                  //  Make sure the member is not inactive - skip it it is
                  //
                  if (inact == 0 || club.equals( "congressional" )) {     // if active or Congressional

                     //
                     // *********************************************************************
                     //
                     //   The following will be dependent on the club - customized
                     //
                     // *********************************************************************
                     //
                     if (club.equals( "weeburn" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (mship.equalsIgnoreCase( "house" ) ||
                            mship.equalsIgnoreCase( "non-golf" ) || mship.equalsIgnoreCase( "non-resident house" ) ||
                            mship.equalsIgnoreCase( "non-resident non-golf" ) || mship.equalsIgnoreCase( "senior house" ) ||
                            mship.equalsIgnoreCase( "senior non-golf" ) || mship.equalsIgnoreCase( "senior non golf" )) {

                            skip = true;                          //  skip this one
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                        } else if (mship.equals( "" )) {

                            skip = true;                          //  skip this one
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: MEMBERSHIP TYPE NOT FOUND!";
                            
                        } else {

                           if (mship.startsWith( "WFG" )) {

                              mship = "WAITING FOR GOLF";          // convert mship
                           }

                           if (mship.equalsIgnoreCase( "golf" )) {

                              mship = "Golf";                      // convert mship
                           }

                           //
                           //  set defaults
                           //
                           if (gender.equalsIgnoreCase( "f" )) {

                              gender = "F";               // Female

                           } else {

                              gender = "M";               // default to Male
                           }

                           //
                           //  Set the Member Type
                           //
                           if (primary.equalsIgnoreCase( "p" )) {

                              mtype = "Primary Male";

                              if (gender.equals( "F" )) {

                                 mtype = "Primary Female";
                              }

                              posid = mNum;

                           } else if (primary.equalsIgnoreCase( "s" )) {

                               mtype = "Spouse Male";

                               if (gender.equals( "F" )) {

                                   mtype = "Spouse Female";
                               }

                                 posid = mNum + "S";

                           } else {

                               mtype = "Junior";
                           }

                           if (mship.equalsIgnoreCase( "DEPENDENT CHILD" )) {

                               //
                               //  Determine the age in years
                               //
                               Calendar cal = new GregorianCalendar();       // get todays date

                               int year = cal.get(Calendar.YEAR);
                               int month = cal.get(Calendar.MONTH) +1;
                               int day = cal.get(Calendar.DAY_OF_MONTH);

                               year = year - 18;             // backup 18 years

                               int oldDate = (year * 10000) + (month * 100) + day;   // get date

                               if (birth > oldDate) {     // if member is < 18 yrs old

                                   mtype = "Junior";

                               } else {

                                   year = year - 5;     // back up 5 more years (23 total)

                                   oldDate = (year * 10000) + (month * 100) + day;   // get date

                                   if (birth > oldDate) {     // if member is 18 - 22 yrs old (< 23)

                                       mtype = "Adult Child";

                                   } else {

                                       year = year - 6;     // back up 6 more years (29 total)

                                       oldDate = (year * 10000) + (month * 100) + day;   // get date

                                       if (birth > oldDate) {     // if member is 23 - 28 yrs old (< 29)

                                           mtype = "Extended Family";

                                       } else {

                                           skip = true;                          //  skip this one
                                           warnCount++;
                                           warnMsg = warnMsg + "\n" +
                                                   "  -SKIPPED: DEPENDENT CHILD OVER 29";
                                       }
                                   }
                               }
                           }

                           //
                           //  Try to set the correct mship type (spouses must be changed)
                           //
                           if (primary.equalsIgnoreCase( "s" ) || mship.equalsIgnoreCase( "Dependent Spouse" ) ||
                               mship.equalsIgnoreCase("DEPENDENT CHILD")) {

                              if (mNum.equals( last_mnum )) {        // if spouse of last member processed

                                 mship = last_mship;                 // get primary's mship value

                              } else {

                                 //
                                 //  Check the db for the primary's mship type
                                 //
                                 pstmt2 = con.prepareStatement (
                                          "SELECT m_ship FROM member2b WHERE memNum = ? AND m_type like 'Primary%'");

                                 pstmt2.clearParameters();
                                 pstmt2.setString(1, mNum);
                                 rs = pstmt2.executeQuery();

                                 if(rs.next()) {

                                     mship = rs.getString("m_ship");

                                 } else {

                                     skip = true;                          //  skip this one
                                     warnCount++;
                                     warnMsg = warnMsg + "\n" +
                                             "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                                 }
                                 pstmt2.close();
                              }

                           } else {        // must be primary

                              last_mnum = mNum;         // save these for spouse
                              last_mship = mship;
                           }

                        }

                     }         // end of IF club = ???


                     if (club.equals( "westchester" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (mship.equalsIgnoreCase( "courtesy" ) || mship.equalsIgnoreCase( "houseres" )) {

                            skip = true;                          //  skip this one
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                        } else if (!mship.equals( "" )) {

                           //
                           //  Set the Member & Membership Types
                           //
                           if (primary.equalsIgnoreCase( "P" )) {          // if primary member

                              mship = "Members";

                              if (gender.equalsIgnoreCase( "F" )) {

                                 mtype = "Member Female";

                              } else {

                                 mtype = "Member Male";
                              }

                           } else {                                        // all others (spouse and dependents)

                              mship = "Family Members";

                              if (primary.equalsIgnoreCase( "S" )) {          // if Spouse

                                 if (gender.equalsIgnoreCase( "F" )) {

                                    mtype = "Spouse Female";

                                 } else {

                                    mtype = "Spouse Male";
                                 }

                              } else {

                                 mtype = "Dependent";

                                 //
                                 //  Set memid for dependents
                                 //
                                 ext++;            // bump common extension value (1 - nnnnn)
                                 memid = memid + ext;
                              }
                           }
                        } else {

                            skip = true;
                        }
                     }         // end of IF club = westchester


                     //
                     //  CC of Virginia
                     //
                     if (club.equals( "virginiacc" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (!mship.equals( "" )) {

                           //
                           //  use memid as webid !! (do NOT change the username in records)
                           //
                           useWebid = true;               // use webid to locate member

                           webid = memid;                   // use webid for this club

                           //
                           //  Set the Member Types (based on age and gender)
                           //
                           if (birth == 0) {       // if age unknown

                              mtype = "Adult Male";

                              if (gender.equalsIgnoreCase( "F" )) {

                                 mtype = "Adult Female";
                              }

                           } else {

                              //
                              //  Determine the age in years
                              //
                              Calendar cal = new GregorianCalendar();       // get todays date

                              int year = cal.get(Calendar.YEAR);
                              int month = cal.get(Calendar.MONTH) +1;
                              int day = cal.get(Calendar.DAY_OF_MONTH);

                              year = year - 16;             // backup 16 years

                              int oldDate = (year * 10000) + (month * 100) + day;   // get date

                              if (birth > oldDate) {     // if member is < 16 yrs old

                                 mtype = "Junior Male";

                                 if (gender.equalsIgnoreCase( "F" )) {

                                    mtype = "Junior Female";
                                 }

                              } else {

                                 year = year - 7;             // backup 7 more years (23 total)

                                 oldDate = (year * 10000) + (month * 100) + day;   // get date

                                 if (birth > oldDate) {     // if member is 16 - 22 yrs old (< 23)

                                    mtype = "Student Male";

                                    if (gender.equalsIgnoreCase( "F" )) {

                                       mtype = "Student Female";
                                    }

                                 } else {

                                    year = year - 7;             // backup 7 more years (30 total)

                                    oldDate = (year * 10000) + (month * 100) + day;   // get date

                                    if (birth > oldDate) {     // if member is 23 - 29 yrs old (< 30)

                                       mtype = "Young Adult Male";

                                       if (gender.equalsIgnoreCase( "F" )) {

                                          mtype = "Young Adult Female";
                                       }

                                    } else {

                                       year = year - 30;             // backup 30 more years (60 total)

                                       oldDate = (year * 10000) + (month * 100) + day;   // get date

                                       if (birth > oldDate) {     // if member is 30 - 59 yrs old (< 60)

                                          mtype = "Adult Male";

                                          if (gender.equalsIgnoreCase( "F" )) {

                                             mtype = "Adult Female";
                                          }

                                       } else {

                                          mtype = "Senior Male";

                                          if (gender.equalsIgnoreCase( "F" )) {

                                             mtype = "Senior Female";
                                          }
                                       }
                                    }
                                 }
                              }
                           }

                           if (mship.equalsIgnoreCase("HONORARY-MALE")) {
                               mtype = "Honorary Male";
                           } else if (mship.equalsIgnoreCase("HONORARY-FEMALE")) {
                               mtype = "Honorary Female";
                           } else if (mship.equalsIgnoreCase("HONORARY RETIREES")) {
                               mtype = "Honorary Retirees";
                           }

                           mship = "Active";           // convert all to Active

                        } else {

                            skip = true;
                        }
                     }         // end of IF club =


                     //
                     //  The Point Lake CLub
                     //
/*
                     if (club.equals( "pointlake" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (!mship.equals( "" ) || mship.equalsIgnoreCase("CSH")) {

                           //
                           //  Set the Member Types
                           //
                           if (gender.equals( "" )) {

                              gender = "M";
                           }

                           if (mship.equalsIgnoreCase( "SPS" ) || mship.equalsIgnoreCase( "SPG" )) {    // TEMP until they fix their genders

                              gender = "F";
                           }

                           if (primary.equalsIgnoreCase( "P" )) {          // if primary member

                              if (gender.equalsIgnoreCase( "F" )) {

                                 mtype = "Primary Female";

                              } else {

                                 mtype = "Primary Male";
                              }

                           } else {                                        // spouse

                              if (gender.equalsIgnoreCase( "F" )) {

                                 mtype = "Spouse Female";

                              } else {

                                 mtype = "Spouse Male";
                              }
                           }

                        } else {                    //  no mship

                           skip = true;             // skip this record
                        }

                     }         // end of IF club =
*/

                     //
                     //  Wichita - uses mapping (webid) !!!
                     //
                     if (club.equals( "wichita" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (!mship.equals( "" )) {

                           //
                           //  use memid as webid !! (do NOT change the username in records)
                           //
                           useWebid = true;               // use webid to locate member

                           webid = memid;                   // use webid for this club


                           //
                           //  Set the Member Types
                           //
                           if (gender.equalsIgnoreCase( "F" )) {

                              mtype = "Adult Female";

                           } else {

                              mtype = "Adult Male";
                           }

                           if (mship.equalsIgnoreCase( "child" )) {

                              //
                              //  if Child, set member type according to age
                              //
                              mtype = "Juniors";          // default

                              if (birth > 0) {         // if age provided

                                 //
                                 //  Determine the age in years
                                 //
                                 Calendar cal = new GregorianCalendar();       // get todays date

                                 int year = cal.get(Calendar.YEAR);
                                 int month = cal.get(Calendar.MONTH) +1;
                                 int day = cal.get(Calendar.DAY_OF_MONTH);

                                 year = year - 18;             // backup 18 years

                                 int oldDate = (year * 10000) + (month * 100) + day;   // get date

                                 if (birth > oldDate) {     // if member is < 18 yrs old

                                    mtype = "Juniors";

                                 } else {

                                    mtype = "18-24";
                                 }
                              }
                           }

                           mship = "Golf";                // everyone is Golf

                        } else {                    //  no mship

                           skip = true;             // skip this record
                        }

                     }         // end of IF club =


                     //
                     //  Brantford
                     //
                     if (club.equals( "brantford" )) {

                        found = true;        // club found

                        // if there is no posid, then use the mNum
                        if (posid.equals("")) posid = mNum;

                        mship = mtype;        // use member type for mship

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (mship.equalsIgnoreCase( "1 day curl" ) || mship.equalsIgnoreCase( "asc crl ld" ) ||
                                mship.startsWith( "ASSC CUR" ) || mship.startsWith( "RESIGN" )) {

                            skip = true;             // skip this record
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                        } else if (!mship.equals( "" )) {                    //  mship ok

                           //
                           //  Set the Membership Types
                           //
                           if (mship.startsWith( "EXT" )) {

                              mship = "Extended Junior";
                           }

                           if (mship.startsWith( "CURL" ) || mship.equalsIgnoreCase( "lds curl" )) {

                              mship = "Curler";
                           }

                           if (mship.startsWith( "FULL" ) || mship.startsWith( "HONO" ) || mship.startsWith( "INT" ) ||
                               mship.startsWith( "LDYFULL" ) || mship.startsWith( "MST FL" ) || mship.startsWith( "MSTR FU" ) ||
                               mship.startsWith( "PLAYER" ) || mship.startsWith( "SEN" ) || mship.startsWith( "SR FULL" )) {

                              mship = "Full";
                           }

                           if (mship.startsWith( "JR" ) || mship.startsWith( "JUNIO" )) {

                              mship = "Junior";
                           }

                           if (mship.startsWith( "NOVIC" )) {

                              mship = "Novice";
                           }

                           if (mship.startsWith( "OV65" ) || mship.startsWith( "OVR 65" )) {

                              mship = "Over 65 Restricted";
                           }

                           if (mship.startsWith( "MST RST" ) || mship.startsWith( "MSTR/RE" )) {

                              mship = "Restricted";
                           }

                           if (mship.startsWith( "SOCGLF" )) {

                              mship = "Social Golf Waitlist";
                           }

                           if (mship.startsWith( "SOCIAL" ) || mship.startsWith( "MAIN" )) {

                              mship = "Social";
                           }

                           //
                           //  Now check the birth date and set anyone age 19 - 25 to 'Extended Junior'
                           //
                           if (birth > 0) {                                 // if birth date provided

                              Calendar cal = new GregorianCalendar();       // get todays date

                              int year = cal.get(Calendar.YEAR);
                              int month = cal.get(Calendar.MONTH) +1;
                              int day = cal.get(Calendar.DAY_OF_MONTH);

                              year = year - 19;             // backup 19 years

                              int oldDate1 = (year * 10000) + (month * 100) + day;   // get date

                              year = year - 7;             // backup another 7 years (26 total)

                              int oldDate2 = (year * 10000) + (month * 100) + day;   // get date

                              if (birth > oldDate2 && birth < oldDate1) {     // if member is 19 to 25 yrs old

                                 mship = "Extended Junior";
                              }
                           }


                           //
                           //  Set the Member Types
                           //
                           if (primary.equalsIgnoreCase( "P" )) {          // if primary member

                              if (gender.equalsIgnoreCase( "F" )) {

                                 mtype = "Primary Female";

                              } else {

                                 mtype = "Primary Male";
                              }

                           } else if (primary.equalsIgnoreCase( "S" )) {                                        // spouse

                              if (gender.equalsIgnoreCase( "F" )) {

                                 mtype = "Spouse Female";

                              } else {

                                 mtype = "Spouse Male";
                              }
                           } else {
                               mtype = "Dependent";
                           }

                        } else {

                            skip = true;
                        }

                     }         // end of IF club =


                     //
                     //  Congressional
                     //
                     if (club.equals( "congressional" )) {

                        found = true;        // club found

                        // if there is no posid, then use the mNum
                        if (posid.equals("")) posid = mNum;

                        if (mNum.startsWith( "0" )) {    // if starts with a zero

                           mNum = remZeroS(mNum);           // remove the leading zero
                        }

                        //
                        //  use memid as webid !! (do NOT change the username in records)
                        //
                        useWebid = true;               // use webid to locate member

                        webid = memid;                   // use webid for this club


                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (mship.equalsIgnoreCase( "SG" ) || mship.equalsIgnoreCase( "BI" )) {

                            skip = true;             // skip this record
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                        } else if (!mship.equals( "" )) {                    //  mship ok

                           if (mship.equals( "SP" )) {       // if Spouse

                              mship = "";

                              pstmt2 = con.prepareStatement (
                                    "SELECT m_ship FROM member2b WHERE username = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, mNum);                  // primary's username is the mNum
                              rs = pstmt2.executeQuery();

                              if(rs.next()) {

                                 mship = rs.getString("m_ship");            // spouse = use primary's mship type
                              }
                              pstmt2.close();
                                
                              if (mship.equals("")) { 
                                 
                                 skip = true;          // skip if mship or primary not found
                              }
                           }

                        } else {

                            skip = true;
                        }

                        if (skip == false) {

                           //
                           //  Set the Membership Types
                           //
                           if (mship.equals( "AG" )) {

                              mship = "Annual Guest";
                           }
                           if (mship.equals( "BA" )) {

                              mship = "Beneficiary Active";
                           }
                           if (mship.equals( "BS" )) {

                              mship = "Beneficiary Special";
                           }
                           if (mship.equals( "BT" )) {

                              mship = "Beneficiary Twenty";
                           }
                           if (mship.equals( "HL" )) {

                              mship = "Honorary Life";
                           }
                           if (mship.equals( "HO" )) {

                              mship = "Honorary";
                           }
                           if (mship.equals( "JA" )) {

                              mship = "Junior A";
                           }
                           if (mship.equals( "JB" )) {

                              mship = "Junior B";
                           }
                           if (mship.equals( "JC" )) {

                              mship = "Junior C";
                           }
                           if (mship.equals( "JM" )) {

                              mship = "Junior Military";
                           }
                           if (mship.equals( "JX" )) {

                              mship = "Junior Absent";
                           }
                           if (mship.equals( "NR" )) {

                              mship = "Non Resident";
                           }
                           if (mship.equals( "NS" )) {

                              mship = "Non Resident Special";
                           }
                           if (mship.equals( "RA" )) {

                              mship = "Resident Active";
                           }
                           if (mship.equals( "RI" )) {

                              mship = "Resident Inactive";
                           }
                           if (mship.equals( "RT" )) {

                              mship = "Resident Twenty";
                           }
                           if (mship.equals( "RX" )) {

                              mship = "Resident Absent";
                           }


                           //
                           //  Now check the birth date and set anyone age 19 - 25 to 'Extended Junior'
                           //
                           if (birth == 19000101) {                          // if birth date mot good

                              birth = 0;
                           }


                           //
                           //  Set the Member Types
                           //
                           if (primary.equalsIgnoreCase( "P" )) {          // if primary member

                              if (gender.equalsIgnoreCase( "F" )) {

                                 mtype = "Primary Female";

                              } else {

                                 mtype = "Primary Male";
                              }

                           } else {                                        // spouse

                              if (gender.equalsIgnoreCase( "F" )) {

                                 mtype = "Spouse Female";

                              } else {

                                 mtype = "Spouse Male";
                              }
                           }

                           //
                           //  Set the Username
                           //
                           if (mtype.startsWith( "Primary" )) {          // if primary member

                              memid = mNum;                              // use mNum

                           } else {

                              memid = mNum + "A";                        // use mNum + A
                           }

                           //
                           //  Set the password for Monagus
                           //
                           password = "jjjj";

                        }

                     }         // end of IF club =


                     //
                     //  Cherry Creek
                     //
/*
                     if (club.equals( "cherrycreek" )) {

                        found = true;        // club found

                        if (mNum.startsWith( "0" )) {    // if starts with a zero

                           mNum = remZeroS(mNum);           // remove the leading zero
                        }

                        //
                        //  use memid as webid !! (do NOT change the username in records)
                        //
                        useWebid = true;               // use webid to locate member

                        webid = memid;                   // use webid for this club


                        mship = toTitleCase(mship);

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (mship.equalsIgnoreCase( "professional member honorary" ) ||
                                mship.startsWith( "Reciprocal" ) || mship.startsWith( "Social" )) {

                            skip = true;             // skip this record
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                        } else if (!mship.equals( "" )) {                    //  mship ok

                           if (mship.startsWith( "Corp Full Golf Founders Individ" )) {

                              mship = "Corp Full Golf Founders Indiv";

                           } else {


                              /*   3-13-08 ALLOW CORP FULL GOLF FOUNDERS FAMILY TO COME ACROSS AS IS - PER LARRY
                              if (mship.startsWith( "Corp Full Golf Founders" )) {

                                 mship = "Corp Full Golf Founders";
                              }
                              */
                               /*
                           }


                           //
                           //  Set the Member Types
                           //
                           if (gender.equalsIgnoreCase( "F" )) {

                              mtype = "Adult Female";

                           } else {

                              if (gender.equalsIgnoreCase( "M" )) {

                                 mtype = "Adult Male";

                              } else {      // unknown gender - check pri/spouse

                                 if (primary.equalsIgnoreCase( "S" )) {

                                    mtype = "Adult Female";

                                 } else {

                                    mtype = "Adult Male";
                                 }
                              }
                           }


                           //
                           //  Set the Username
                           //
                           if (mtype.equals( "Adult Male" )) {          // if primary member

                              memid = mNum + "-000";

                           } else {

                              memid = mNum + "-001";
                           }

                           // if there is no posid, then use the memid
                           if (posid.equals("")) posid = memid;

                        } else {

                            skip = true;
                        }

                     }         // end of IF club =
*/

                     //
                     //  Baltimore CC
                     //
                     if (club.equals( "baltimore" )) {

                        found = true;        // club found

                        //
                        //  use memid as webid !! (do NOT change the username in records)
                        //
                        useWebid = true;               // use webid to locate member

                        webid = memid;                   // use webid for this club


                        // if there is no posid, then use the mNum
                        if (posid.equals("")) posid = mNum;


                        mship2 = mship;
                        mship = "";

                        //
                        // convert mship
                        //


                        if (mship2.equalsIgnoreCase( "A GENT GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT GOLF NON ACTIV NON RES FD" )) {

                           mship = "Non Resident No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT NON GOLF NON RES FD" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT GOLF NON ACTIV NON RES" )) {

                           mship = "Non Resident No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT NON GOLF NON RES" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF NON ACTIV NON RES FD" )) {

                           mship = "Non Resident No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES NON GOLF NON RES FD" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF NON ACTIV NON RES" )) {

                           mship = "Non Resident No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES NON GOLF NON RES" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF NON ACTIV NON RES FD" )) {

                           mship = "Non Resident No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT NON GOLF NON RES FD" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF NON ACTIV NON RES" )) {

                           mship = "Non Resident No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT NON GOLF NON RES" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF NON ACTIV NON RES FD" )) {

                           mship = "Non Resident No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES NON GOLF NON RES FD" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF NON ACTIV NON RES" )) {

                           mship = "Non Resident No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES NON GOLF NON RES" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF NON ACTIV NON RES FD" )) {

                           mship = "Non Resident No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT NON GOLF NON RES FD" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF NON ACTIV NON RES" )) {

                           mship = "Non Resident No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT NON GOLF NON RES" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF NON ACTIV NON RES FD" )) {

                           mship = "Non Resident No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES NON GOLF NON RES FD" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF NON ACTIV NON RES" )) {

                           mship = "Non Resident No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES NON GOLF NON RES" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "H GENT GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "H GENT GOLF NON ACTIV DISC" )) {

                           mship = "No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "H GENT NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "H LADIES GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "H LADIES GOLF NON ACTIV DISC" )) {

                           mship = "No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "H LADIES NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "J GENT NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "J LADIES NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "L GENT GOLF NON ACTIV" )) { // supposed to be non?

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "L GENT GOLF HONORARY" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "L GENT NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "L GENT GOLF FULL SEAS")) {

                           mship = "Discounted Season Golfer";
                           mtype = "Male Discounted Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "L LADIES GOLF HONORARY" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "L LADIES NON GOLF" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "M MINOR GENT" )) {

                           mship = "Season Golfer";
                           mtype = "Male Dependent";
                        }

                        if (mship2.equalsIgnoreCase( "M MINOR LADIES" )) {

                           mship = "Season Golfer";
                           mtype = "Female Dependent";
                        }

                        // end non online members


                        // start on members

                        if (mship2.equalsIgnoreCase( "A GENT GOLF PART SEAS NON RES FD" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT GOLF FULL SEAS NON RES FD" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT GOLF FULL SEAS NON RES" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT GOLF PART SEAS NON RES" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A GENT GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF PART SEAS NON RES FD" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF FULL SEAS NON RES FD" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF FULL SEAS NON RES" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF PART SEAS NON RES" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "A LADIES GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF PART SEAS NON RES FD" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF FULL SEAS NON RES FD" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF FULL SEAS NON RES" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF PART SEAS NON RES" )) {

                           mship = "Non Season Golfer";
                           mtype = "Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF FULL SEAS NON RES FD" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF FULL SEAS NON RES" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF PART SEAS NON RES" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF PART SEAS NON RES FD" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF FULL SEAS NON RES FD" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF FULL SEAS NON RES" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF PART SEAS NON RES" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D GENT GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF PART SEAS NON RES FD" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF FULL SEAS NON RES FD" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF FULL SEAS NON RES" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF PART SEAS NON RES" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "D LADIES GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "H GENT GOLF PART SEAS DISC" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non-Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "H GENT GOLF FULL SEAS DISC" )) {

                           mship = "Discounted Season Golfer";
                           mtype = "Male Discounted Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "H GENT GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "H GENT GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "H LADIES GOLF PART SEAS DISC" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non-Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "H LADIES GOLF FULL SEAS DISC" )) {

                           mship = "Discounted Season Golfer";
                           mtype = "Female Discounted Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "H LADIES GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "H LADIES GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "JM Gent Season" )) {

                           mship = "Season Golfer";
                           mtype = "Male Junior Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "J GENT GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Junior Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "JW Ladies Season" )) {

                           mship = "Season Golfer";
                           mtype = "Female Junior Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "J LADIES GOLF PART SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Female Junior Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "LW Ladies Golf" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "J GENT GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Male No Package";
                        }

                        if (mship2.equalsIgnoreCase( "J LADIES GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Female No Package";
                        }

                        if (mship2.equalsIgnoreCase( "J GENT GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "J LADIES GOLF FULL SEAS" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        //
                        //  New mship types (6/12/07)
                        //
                        if (mship2.equalsIgnoreCase( "J GENT GOLF COLL STUD" )) {

                           mship = "College Season Golfer";
                           mtype = "Male Season Golfer";
                        }
                        if (mship2.equalsIgnoreCase( "J LADIES GOLF COLL STUD" )) {

                           mship = "College Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "L LADIES GOLF FULL SEAS" )) {

                           mship = "Discounted Season Golfer";
                           mtype = "Female Discounted Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "L LADIES GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Female Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "M MINOR GENT GOLF ENT" )) {

                           mship = "Season Golfer";
                           mtype = "Male Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "M MINOR LADIES GOLF ENT" )) {

                           mship = "Season Golfer";
                           mtype = "Female Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "B GENT NON GOLF EXT LEGASY" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "B LADIES NON GOLF EXT LEGASY" )) {

                           mship = "Non Golf";
                           mtype = "Female Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "J JUNIOR GENT NON GOLF GE" )) {

                           mship = "Non Golf";
                           mtype = "Male Non Golf";
                        }

                        if (mship2.equalsIgnoreCase( "L GENT GOLF PART SEAS" )) {

                           mship = "Non Season Golfer";
                           mtype = "Male Non Season Golfer";
                        }

                        if (mship2.equalsIgnoreCase( "L LADIES GOLF NON ACTIV" )) {

                           mship = "No Package";
                           mtype = "Female No Package";
                        }

                        //
                        //  Skip this record if a valid mship type was not specified
                        //
                        if (mship.equals("")) {
                            skip = true;       //  skip these
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                    "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                        }

                     } // end if club == baltimore


                     //
                     //   Providence
                     //
                     if (club.equals( "providence" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (!mship.equals( "" ) && (mship.equalsIgnoreCase( "A" ) || mship.equalsIgnoreCase( "E" ) ||
                            mship.equalsIgnoreCase( "Z" ) || mship.equalsIgnoreCase( "H" ) || mship.equalsIgnoreCase( "I" ) ||
                            mship.equalsIgnoreCase( "L" ) || mship.equalsIgnoreCase( "S" ) || mship.equalsIgnoreCase( "ZA" ))) {

                           //
                           //  use memid as webid !! (do NOT change the username in records)
                           //
                           useWebid = true;               // use webid to locate member

                           webid = memid;                   // use webid for this club

                           if (posid.equals( "" )) {

                              posid = mNum;                  // default posid = mnum
                           }

                           //
                           //  Set the Member Type
                           //
                           mtype = "Primary Male";             // default

//                           if (mNum.endsWith( "B" ) ||mNum.endsWith( "C" ) ||
//                               mNum.endsWith( "D" ) || mNum.endsWith( "E" ) ||mNum.endsWith( "F" ) ||
//                               mNum.endsWith( "G" ) || mNum.endsWith( "H" ) ||mNum.endsWith( "I" )) {
//
//                              mtype = "Dependent";
//
//                           } else {

                           if (primary.equalsIgnoreCase( "S" )) {          // if Spouse

                              if (gender.equals( "M" )) {

                                 mtype = "Spouse Male";

                              } else {

                                 mtype = "Spouse Female";
                              }

                           } else {                                    // primary

                              if (primary.equalsIgnoreCase( "P" )) {          // if Primary

                                 if (gender.equals( "M" )) {

                                    mtype = "Primary Male";

                                 } else {

                                    mtype = "Primary Female";
                                 }

                              } else {

                                 mtype = "Dependent";             // all others = juniors
                              }
                           }

                           //
                           //  Set the Mship Type
                           //
                           if (mship.equalsIgnoreCase( "A" )) {

                              mship = "Active";

                           } else {

                              if (mship.equalsIgnoreCase( "E" )) {

                                 mship = "Employee";

                              } else {

                                 if (mship.equalsIgnoreCase( "H" )) {

                                    mship = "Honorary";

                                 } else {

                                    if (mship.equalsIgnoreCase( "I" )) {

                                       mship = "Inactive";

                                    } else {

                                       if (mship.equalsIgnoreCase( "L" )) {

                                          mship = "Member Resigning";

                                       } else {

                                          if (mship.equalsIgnoreCase( "S" )) {

                                             mship = "Suspended";

                                          } else {

                                             if (mship.equalsIgnoreCase( "Z" ) || mship.equalsIgnoreCase( "ZA" )) {

                                                mship = "Family Active";

                                             } else {

                                                skip = true;        // skip all others
                                                warnCount++;
                                                warnMsg = warnMsg + "\n" +
                                                      "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }

                        } else {

                           skip = true;             // skip this record

                           if (!mship.equals( "" )) {

                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                      "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                           }
                        }

                     }         // end of IF club

                     //
                     //   Algonquin
                     //
                     if (club.equals( "algonquin" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (!mship.equals( "" )) {

                           if (posid.equals( "" )) {

                              posid = mNum;                  // default posid = mnum
                           }

                           while (mNum.startsWith( "0" )) {    // if starts with a zero

                              mNum = remZeroS(mNum);           // remove the leading zero
                           }

                           //
                           //  Set the Membership and Member Types - both dependent on the mship received
                           //
                           mtype = "Primary Male";             // default

                           mship2 = "";                        // init as none

                           if (mship.equalsIgnoreCase( "Active" )) {

                              mship2 = "Active";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "Associate" )) {

                              mship2 = "Associate";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "Senior" )) {

                              mship2 = "Senior";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "non-res" )) {

                              mship2 = "Non-Res";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "nonresgolf" )) {

                              mship2 = "Nonresgolf";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "Social" )) {

                              mship2 = "Social";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "Clerical M" )) {

                              mship2 = "Clerical M";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "Junior Class A" ) || mship.equalsIgnoreCase( "Jr Class A" )) {

                              mship2 = "Jr Class A";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "Junior Class B" ) || mship.equalsIgnoreCase( "Jr Class B" )) {

                              mship2 = "Jr Class B";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "Junior Class C" ) || mship.equalsIgnoreCase( "Jr Class C" )) {

                              mship2 = "Jr Class C";

                              if (gender.equals( "F" )) {      // otherwise use default if Male

                                 mtype = "Primary Female";     // always a primary member
                              }
                           }

                           if (mship.equalsIgnoreCase( "Spouse" ) || mship.equalsIgnoreCase( "Child" )) {   // if Spouse or Dependent

                              if (mship.equalsIgnoreCase( "Child" )) {      // if Dependent

                                 mtype = "Dependent";

                              } else {

                                 if (gender.equals( "F" )) {

                                    mtype = "Spouse Female";

                                 } else {

                                    mtype = "Spouse Male";
                                 }
                              }


                              //
                              //  Get the mship type from the primary
                              //
                              pstmt2 = con.prepareStatement (
                                       "SELECT m_ship FROM member2b WHERE username != ? AND memNum = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, memid);
                              pstmt2.setString(2, mNum);
                              rs = pstmt2.executeQuery();

                              if(rs.next()) {

                                 mship2 = rs.getString("m_ship");            // spouse = use primary's mship type
                              }
                              pstmt2.close();

                           }                         // end of IF spouse or child

                           if (mship2.equals( "" )) {          // if matching mship NOT found

                              skip = true;                    // skip this one
                              warnCount++;
                              warnMsg = warnMsg + "\n" +
                                      "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                           } else {

                              mship = mship2;                 // set new mship
                           }

                        } else {                    // mship not provided

                           skip = true;             // skip this record
                        }

                     }         // end of IF Algonquin


                     //
                     //   Bent Tree
                     //
                     if (club.equals( "benttreecc" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (!mship.equalsIgnoreCase( "Active" ) && !mship.startsWith( "D" )) {   // if NOT Active or Dxx

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MSHIP invalid!";

                        } else {

                           //
                           //  use memid as webid !! (do NOT change the username in records)
                           //
                           useWebid = true;               // use webid to locate member

                           webid = memid;                   // use webid for this club

                           if (posid.equals( "" )) {

                              posid = mNum;                  // default posid = mnum
                           }


                           //
                           //  Set the Member Type
                           //
                           mtype = "Member Male";             // default

                           if (primary.equalsIgnoreCase( "S" )) {          // if Spouse

                              if (gender.equals( "M" )) {

                                 mtype = "Spouse Male";

                              } else {

                                 mtype = "Spouse Female";
                              }

                           } else {                                    // primary

                              if (primary.equalsIgnoreCase( "P" )) {          // if Primary

                                 if (gender.equals( "M" )) {

                                    mtype = "Member Male";

                                 } else {

                                    mtype = "Member Female";
                                 }

                              } else {

                                 mtype = "Junior";             // all others = juniors
                              }
                           }

                           //
                           //  Convert the mship from Dxx to real value
                           //
                           if (!mship.equalsIgnoreCase( "Active" )) {   // accept Active as is

                              if (mship.equalsIgnoreCase( "D01" )) {

                                 mship = "Resident";

                              } else if (mship.equalsIgnoreCase( "D02" )) {

                                 mship = "Young Executive";

                              } else if (mship.equalsIgnoreCase( "D03" ) || mship.equalsIgnoreCase( "D05" )) {

                                 mship = "Tennis";

                              } else if (mship.equalsIgnoreCase( "D04" ) || mship.equalsIgnoreCase( "D11" )) {

                                 mship = "Junior";

                              } else if (mship.equalsIgnoreCase( "D06" )) {

                                 mship = "Temp Non-Resident";

                              } else if (mship.equalsIgnoreCase( "D08" )) {

                                 mship = "Non-Resident";

                              } else if (mship.equalsIgnoreCase( "D09" )) {

                                 mship = "Senior";

                              } else if (mship.equalsIgnoreCase( "D12" )) {

                                 mship = "Employee";

                              } else {

                                 skip = true;
                              }
                           }
                        }     // end of IF mship ok

                     }         // end of IF club


                     //
                     //   Orchid Island
                     //
                     if (club.equals( "orchidisland" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (!mship.equals( "" )) {

                           //
                           //  use memid as webid !! (do NOT change the username in records)
                           //
                           useWebid = true;               // use webid to locate member

                           webid = memid;                   // use webid for this club

                           if (posid.equals( "" )) {

                              posid = mNum;                  // default posid = mnum
                           }

                           if (gender.equals( "" )) {

                              gender = "M";                  // default
                           }


                           //
                           //  Strip any leading zeros and extension from mNum
                           //
                           while (mNum.startsWith( "0" )) {    // if starts with a zero

                              mNum = remZeroS(mNum);           // remove the leading zero
                           }

                           memid = mNum;                            // Primary = 1234

                           if (mship.equalsIgnoreCase("Depend") ||
                               (!primary.equalsIgnoreCase("P") && !primary.equalsIgnoreCase("S"))) {

                              memid = memid + "-" + primary;      // 1234-2

                              mtype = "Dependents";

                           } else if (mship.equalsIgnoreCase( "Spouse" ) || primary.equalsIgnoreCase( "S" )) {

                               memid = memid + "-1";                 // 1234-1

                               if (gender.equals( "F" )) {

                                   mtype = "Adult Female";
                               }
                           } else {
                               
                               if (gender.equalsIgnoreCase("F")) {
                                   mtype = "Adult Female";
                               } else {
                                   mtype = "Adult Male";
                               }
                           }




                           //
                           // Set mship
                           //
                           if (mship.endsWith( "TEN" )) {

                              mship = "Beach & Tennis";

                           } else if (mship.endsWith( "GOLF" ) || mship.equalsIgnoreCase("Employee")) {

                               if (mship.equalsIgnoreCase("I GOLF")) {
                                   mship = "Invitational Golf";
                               } else {
                                   mship = "EQ Golf";
                               }

                           } else {

                              //
                              //  Spouse or Dependent - look for Primary mship type and use that
                              //
                              pstmt2 = con.prepareStatement (
                                       "SELECT m_ship FROM member2b WHERE username != ? AND (m_ship = 'EQ Golf' OR m_ship = 'Beach & Tennis' OR m_ship = 'Invitational Golf') AND memNum = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, memid);
                              pstmt2.setString(2, mNum);
                              rs = pstmt2.executeQuery();

                              if(rs.next()) {

                                 mship = rs.getString("m_ship");            // use primary mship type
                              } else {
                                  skip = true;
                              }
                              pstmt2.close();
                           }

                        } else {

                           skip = true;             // skip this record
                        }

                     }         // end of IF club


                     //
                     //   Colleton River
                     //
                     if (club.equals( "colletonriverclub" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (!mship.equals( "" )) {

                           //
                           //  use memid as webid !! (do NOT change the username in records)
                           //
                           useWebid = true;               // use webid to locate member

                           webid = memid;                   // use webid for this club

                           if (posid.equals( "" )) {

                              posid = mNum;                  // default posid = mnum
                           }

                           if (gender.equals( "" )) {

                              gender = "M";                  // default
                           }


                           //
                           //  Strip any leading zeros and extension from mNum
                           //
                           while (mNum.startsWith( "0" )) {    // if starts with a zero

                              mNum = remZeroS(mNum);           // remove the leading zero
                           }

                           memid = mNum;                            // Primary = 1234

                           mtype = "Adult Male";                    // default

                           if (!primary.equalsIgnoreCase("P") && !primary.equalsIgnoreCase("S")) {

                              memid = memid + "-" + primary;      // 1234-2

                              mtype = "Dependents";

                           } else {

                             if (primary.equalsIgnoreCase( "S" )) {

                                 memid = memid + "-1";                 // 1234-1
                              }

                              if (gender.equals( "F" )) {

                                 mtype = "Adult Female";
                              }
                           }

                           // Convert 'FULL3' mship to 'FULL'
                           if (mship.equalsIgnoreCase("FULL3")) {
                               mship = "FULL";
                           }


                           //
                           // Set mship
                           //
                           if (mship.equalsIgnoreCase( "Member" )) {

                              //mship = "Resident";            // do not change others
                              skip = true;
                              warnCount++;
                              warnMsg = warnMsg + "\n" +
                                      "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                           }

                        } else {

                           skip = true;             // skip this record
                        }

                     }         // end of IF club


                     //
                     //   Claremont CC
                     //
                     if (club.equals( "claremontcc" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (!mship.equals( "" )) {

                           //
                           //  use memid as webid !! (do NOT change the username in records)
                           //
                           useWebid = true;               // use webid to locate member

                           webid = memid;                   // use webid for this club

                           if (posid.equals( "" )) {

                              posid = mNum;                  // default posid = mnum
                           }

                           if (gender.equals( "" )) {

                              gender = "M";                  // default
                           }


                           //
                           //  Strip any leading zeros and extension from mNum
                           //
                           while (mNum.startsWith( "0" )) {    // if starts with a zero

                              mNum = remZeroS(mNum);           // remove the leading zero
                           }


                           mtype = "Adult Male";                    // default

                           if (!primary.equalsIgnoreCase("P") && !primary.equalsIgnoreCase("S")) {

                              mtype = "Juniors";

                           } else {

                              if (gender.equals( "F" )) {

                                 mtype = "Adult Female";
                              }
                           }

                           //
                           // Set mship - TEMP until CE fixes them !!!!!!!!!!!!!!!!!!!!!!!
                           //
                           if (mship.equalsIgnoreCase( "Member" )) {

                              mship = "Employee";

                           } else if (mship.equalsIgnoreCase("Exempt")) {

                               mship = "REG";

                           } else if (mship.equalsIgnoreCase("active") || mship.equalsIgnoreCase("Oak Tree") || mship.equalsIgnoreCase("Standards")) {

                              skip = true;
                              warnCount++;
                              warnMsg = warnMsg + "\n" +
                                      "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";
                           }


                        } else {

                           skip = true;             // skip this record
                        }

                     }         // end of IF club

               /*
                     //
                     //   Meridian GC
                     //
                     if (club.equals( "meridiangc" )) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (!mship.equals( "" )) {

                           //
                           //  use memid as webid !! (do NOT change the username in records)
                           //
                           useWebid = true;               // use webid to locate member

                           webid = memid;                   // use webid for this club

                           if (posid.equals( "" )) {

                              posid = mNum;                  // default posid = mnum
                           }

                           if (gender.equals( "" )) {

                              gender = "M";                  // default
                           }


                           //
                           //  Strip any leading zeros and extension from mNum
                           //
                           while (mNum.startsWith( "0" )) {    // if starts with a zero

                              mNum = remZeroS(mNum);           // remove the leading zero
                           }


                           mtype = "Primary Male";                    // default

                           if (!primary.equalsIgnoreCase("P") && !primary.equalsIgnoreCase("S")) {

                              mtype = "Dependent";

                           } else if (primary.equalsIgnoreCase("P") && gender.equalsIgnoreCase( "M" )) {

                              mtype = "Primary Male";

                           } else if (primary.equalsIgnoreCase("P") && gender.equalsIgnoreCase( "F" )) {

                              mtype = "Primary Female";

                           } else if (primary.equalsIgnoreCase("S") && gender.equalsIgnoreCase( "F" )) {

                              mtype = "Non-Primary Female";

                           } else if (primary.equalsIgnoreCase("S") && gender.equalsIgnoreCase( "M" )) {

                              mtype = "Non-Primary Male";
                           }

                        } else {

                           skip = true;             // skip this record
                        }

                     }         // end of IF club



*/
                     if (club.equals("berkeleyhall")) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (!mship.equals( "" )) {

                            posid = mNum;   // they use their member numbers as their posid
                            webid = memid;  // they use their member id (our username) as their webid

                            //
                            //  Strip any leading zeros and extension from mNum
                            //
                            while (mNum.startsWith( "0" )) {    // if starts with a zero

                               mNum = remZeroS(mNum);           // remove the leading zero
                            }

                            if (!primary.equalsIgnoreCase("P") && !primary.equalsIgnoreCase("S")) {

                                mtype = "Dependent";

                            } else if (gender.equalsIgnoreCase( "M" )) {

                                mtype = "Adult Male";

                            } else if (gender.equalsIgnoreCase( "F" )) {

                                mtype = "Adult Female";

                            } else {

                                skip = true;
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                        "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                            }

                        } else {

                           skip = true;             // skip this record
                        }

                     } // end if berkeleyhall


                     if (club.equals("indianhillscc")) {

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (memid.equals( "" )) {

                            skip = true;          // skip it
                            errCount++;
                            errMsg = errMsg + "\n" +
                                    "  -MEMID missing!";

                        } else if (mship.equalsIgnoreCase( "Social" ) || mship.equalsIgnoreCase( "Social SS" ) || mship.equalsIgnoreCase( "Spouse" ) ||
                            mship.equalsIgnoreCase( "Spouse S" ) || mship.equalsIgnoreCase( "Child" )) {

                            skip = true;
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                    "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                        } else if (!mship.equals( "" )) {

                            posid = mNum;   // they use their member numbers as their posid

                            //
                            //  use memid as webid !!
                            //
                            useWebid = true;               // use webid to locate member

                            webid = memid;                 // use webid for this club

                            //
                            //  use the mnum for memid
                            //
                            if (primary.equalsIgnoreCase("P")) {

                               memid = mNum;

                            } else {

                               if (primary.equalsIgnoreCase("S")) {

                                  memid = mNum + "-1";              // spouse

                               } else {

                                  memid = mNum + "-" + primary;     // dependents (2 and up)
                               }
                            }

                            mship = toTitleCase(mship);

                            //
                            //  Set the member types
                            //
                            if (mship.startsWith("Spouse")) {

                               if (gender.equalsIgnoreCase("M")) {

                                  mtype = "Spouse Male";

                               } else {

                                  mtype = "Spouse Female";
                               }

                            } else {

                               if (mship.startsWith("Child")) {

                                  if (gender.equalsIgnoreCase("M")) {

                                     mtype = "Junior Male";

                                  } else {

                                     mtype = "Junior Female";
                                  }

                               } else {     // all others

                                  if (gender.equalsIgnoreCase("F")) {

                                     mtype = "Primary Female";

                                  } else {

                                     mtype = "Primary Male";
                                  }
                               }
                            }

                            //
                            //  Convert some mship types
                            //
                            if (mship.equals("Found") || mship.equals("Spouse F") || mship.equals("Child F") ||
                                mship.equalsIgnoreCase("G/F SS") || mship.equalsIgnoreCase("Golf SS")) {

                               mship = "Foundation";

                            } else {

                               if (mship.equals("Interm") || mship.equals("Spouse I") || mship.equals("Child I")) {

                                  mship = "Intermediate";
                               }
                            }

                        } else {                    // missing field or mship not allowed

                           skip = true;             // skip this record
                        }

                     } // end if indianhillscc

                     /*
                     if (club.equals("rtjgc")) {          // Robert Trent Jones GC

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (!mship.equals( "" )) {

                            posid = mNum;                 // use their member numbers as their posid

                            //
                            //  use memid as webid !!
                            //
                            useWebid = true;               // use webid to locate member

                            webid = memid;                 // use webid for this club

                            //
                            //  use the mnum for memid
                            //
                            if (primary.equalsIgnoreCase("S") || gender.equalsIgnoreCase("F")) {

                               memid = mNum + "A";        // spouse or female

                            } else {

                               memid = mNum;              // primary males
                            }


                            //
                            //  Set the member type
                            //
                            if (gender.equalsIgnoreCase("F")) {

                               mtype = "Primary Female";

                            } else {

                               mtype = "Primary Male";
                            }


                            //
                            //  Set the membership type
                            //
                            if (mship.equalsIgnoreCase("HON")) {

                               mship = "Honorary";

                            } else {

                               if (mship.endsWith("IR")) {

                                  mship = "Individual Resident";

                               } else {

                                  if (mship.endsWith("SNR")) {

                                      mship = "Senior Non Resident";

                                  } else {

                                     if (mship.endsWith("SR")) {

                                        mship = "Senior";

                                     } else {

                                        if (mship.endsWith("CR")) {

                                           mship = "Corporate Resident";

                                        } else {

                                           if (mship.endsWith("CNR")) {

                                              mship = "Corporate Non Resident";

                                           } else {

                                              if (mship.endsWith("INR")) {

                                                 mship = "Individual Non Resident";

                                              } else {

                                                 if (mship.endsWith("P")) {

                                                    mship = "Playing Member";

                                                 } else {

                                                    mship = "Junior";
                                                 }
                                              }
                                           }
                                        }
                                     }
                                  }
                               }
                            }

                        } else {                    // missing field or mship not allowed

                           skip = true;             // skip this record
                        }

                     } // end if Robert Trent Jones GC
                     */

                     if (club.equals("oakhillcc")) {          // Oak Hill CC

                        found = true;        // club found

                        //
                        //  Determine if we should process this record
                        //
                        if (!mship.equals( "" )) {

                            posid = mNum;                 // use their member numbers as their posid

                            //
                            //  use memid as webid !!
                            //
                            useWebid = true;               // use webid to locate member

                            webid = memid;                 // use webid for this club

                            //
                            //  use the mnum for memid
                            //
                            if (primary.equalsIgnoreCase("S")) {

                               memid = mNum + "A";        // spouse

                            } else {

                               if (primary.equalsIgnoreCase("P")) {

                                  memid = mNum;        // Primary

                               } else {

                                  memid = mNum + "-" + primary;              // dependents (nnn-p)
                               }
                            }


                            //
                            //  Set the member type
                            //
                            if (primary.equalsIgnoreCase("P")) {

                               if (gender.equalsIgnoreCase("F")) {

                                  mtype = "Primary Female";

                               } else {

                                  mtype = "Primary Male";
                               }

                            } else {

                               if (primary.equalsIgnoreCase("S")) {

                                  if (gender.equalsIgnoreCase("F")) {

                                     mtype = "Spouse Female";

                                  } else {

                                     mtype = "Spouse Male";
                                  }

                               } else {

                                  mtype = "Dependent";
                               }
                            }

                            //
                            //  Set the membership type
                            //
                            mship = "G";       // all are Golf

                        } else {                    // missing field or mship not allowed

                           skip = true;             // skip this record
                        }

                     } // end if Oak Hill CC


                     if (club.equals("internationalcc")) {          // International CC

                        found = true;        // club found

                        mship = toTitleCase(mship);

                        //
                        //  Determine if we should process this record
                        //
                        if (mship.equalsIgnoreCase( "Corp-Social" ) || mship.equalsIgnoreCase( "Employees" ) ||
                            mship.equalsIgnoreCase( "Leave Of Absence" ) || mship.equalsIgnoreCase( "Member" ) ||
                            mship.equalsIgnoreCase( "Social" ) || mship.equalsIgnoreCase( "Social-Wci" ) ||
                            mship.equalsIgnoreCase( "Bad Addresses" ) || mship.equalsIgnoreCase( "Expelled" ) ||
                            mship.equalsIgnoreCase( "Resigned" ) || mship.equalsIgnoreCase( "Parties" ) ||
                            mship.equalsIgnoreCase( "Clubhouse" )) {

                            skip = true;
                            warnCount++;
                            warnMsg = warnMsg + "\n" +
                                    "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                        } else if (!mship.equals( "" )) {
                           
                            posid = mNum;                 // use their member numbers as their posid

                            //
                            //  use memid as webid !!
                            //
                            useWebid = true;               // use webid to locate member

                            webid = memid;                 // use webid for this club
/*
                            // Custom to handle memNum 1217 differently, as they have to have the primary and spouse flip-flopped
                            if (mNum.equals("1217")) {

                                if (primary.equalsIgnoreCase("P")) {
                                    primary = "S";
                                } else if (primary.equalsIgnoreCase("S")) {
                                    primary = "P";
                                }
                            }
*/
                            //
                            //  use the mnum for memid
                            //
                            if (primary.equalsIgnoreCase("S")) {

                               memid = mNum + "A";        // spouse

                            } else {

                               if (primary.equalsIgnoreCase("P")) {

                                  memid = mNum;        // Primary

                               } else {

                                  memid = mNum + "-" + primary;              // dependents (nnn-p)
                               }
                            }


                            //
                            //  Set the member type
                            //
                            if (primary.equalsIgnoreCase("P")) {

                               if (gender.equalsIgnoreCase("F")) {

                                  mtype = "Primary Female";

                               } else {

                                  mtype = "Primary Male";
                               }

                            } else {

                               if (primary.equalsIgnoreCase("S")) {

                                  if (gender.equalsIgnoreCase("F")) {

                                     mtype = "Spouse Female";

                                  } else {

                                     mtype = "Spouse Male";
                                  }

                               } else {

                                  mtype = "Dependent";
                               }

                               // If Spouse/Dependent, hit the database and use the Primary's membership type (if exists)
                               try {
                                   PreparedStatement pstmtTemp = null;
                                   ResultSet rsTemp = null;

                                   pstmtTemp = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                                   pstmtTemp.clearParameters();
                                   pstmtTemp.setString(1, mNum);

                                   rsTemp = pstmtTemp.executeQuery();

                                   if (rsTemp.next()) {
                                      
                                       mship = rsTemp.getString("m_ship");     // get primary's mship type
                                       
                                   } else {
                                       skip = true;
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: NO PRIMARY FOUND!";
                                   }

                                   pstmtTemp.close();
                                   
                               } catch (Exception ignore) { }
                            }


                        } else {                    // missing field or mship not allowed

                           skip = true;             // skip this record
                        }

                     } // end if International CC


                     if (club.equals("greenwich")) {          // Greenwich CC

                        found = true;        // club found

                        mship = toTitleCase(mship);

                        //
                        //  Determine if we should process this record
                        //
                        if (!mship.equals( "" )) {

                            posid = mNum;                 // use their member numbers as their posid

                            //
                            //  Strip any leading zeros and extension from mNum
                            //
                            while (mNum.startsWith( "0" )) {    // if starts with a zero

                               mNum = remZeroS(mNum);           // remove the leading zero
                            }

                            //
                            //  use memid as webid !!
                            //
                            useWebid = true;               // use webid to locate member

                            webid = memid;                 // use webid for this club


                            //
                            //  mship - ALL = Golf !!!!!!
                            //
                            mship = "Golf";


                            //
                            //  use the mnum for memid
                            //
                            if (primary.equalsIgnoreCase("S")) {

                               memid = mNum + "A";        // spouse

                            } else {

                               if (primary.equalsIgnoreCase("P")) {

                                  memid = mNum;        // Primary

                               } else {

                                  memid = mNum + "-" + primary;              // dependents (nnn-p)
                               }
                            }


                            //
                            //  Set the member type
                            //
                            if (primary.equalsIgnoreCase("P") || primary.equalsIgnoreCase("S")) {

                               if (gender.equalsIgnoreCase("F")) {

                                  mtype = "Primary Female";

                               } else {

                                  mtype = "Primary Male";
                               }

                            } else {

                               mtype = "Dependent";
                            }


                        } else {                    // missing field or mship not allowed

                           skip = true;             // skip this record
                        }

                     } // end if Greenwich CC



                     //******************************************************************
                     //   TPC Southwind
                     //******************************************************************
                     //
                     if (club.equals("tpcsouthwind")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }
                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "A";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }
                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 if (primary.equals("2")) {
                                     memid += "B";
                                 } else if (primary.equals("3")) {
                                     memid += "C";
                                 } else if (primary.equals("4")) {
                                     memid += "D";
                                 } else if (primary.equals("5")) {
                                     memid += "E";
                                 } else if (primary.equals("6")) {
                                     memid += "F";
                                 }

                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }

                     }  // end if tpcsouthwind


                     //******************************************************************
                     //   TPC Sugarloaf
                     //******************************************************************
                     //
                     if (club.equals("tpcsugarloaf")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" ) || mship.equalsIgnoreCase("Charter Social") || mship.equalsIgnoreCase("Charter Swim/Tennis")) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "A";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6") || primary.equals("7")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 if (primary.equals("2")) {
                                     memid += "B";
                                 } else if (primary.equals("3")) {
                                     memid += "C";
                                 } else if (primary.equals("4")) {
                                     memid += "D";
                                 } else if (primary.equals("5")) {
                                     memid += "E";
                                 } else if (primary.equals("6")) {
                                     memid += "F";
                                 } else if (primary.equals("7")) {
                                     memid += "G";
                                 }

                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }

                     }  // end if tpcsugarloaf


                     //******************************************************************
                     //   TPC Wakefield Plantation
                     //******************************************************************
                     //
                     if (club.equals("tpcwakefieldplantation")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" ) || mship.equalsIgnoreCase("Sports Club") || mship.equalsIgnoreCase("Sports Club Non-Resident")) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 memid = memid + primary;
                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }

                     }  // end if tpcwakefieldplantation


                     //******************************************************************
                     //   TPC River Highlands
                     //******************************************************************
                     //
                     if (club.equals("tpcriverhighlands")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 memid = memid + primary;
                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }

                     }  // end if tpcriverhighlands


                     //******************************************************************
                     //   TPC River's Bend
                     //******************************************************************
                     //
                     if (club.equals("tpcriversbend")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 memid = memid + primary;
                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }

                     }  // end if tpcriversbend

                     //******************************************************************
                     //   TPC Jasna Polana
                     //******************************************************************
                     //
                     if (club.equals("tpcjasnapolana")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 memid = memid + primary;
                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }

                     }  // end if tpcjasnapolana


                     //******************************************************************
                     //   TPC Boston
                     //******************************************************************
                     //
                     if (club.equals("tpcboston")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 memid = memid + primary;
                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }

                             if (mship.equalsIgnoreCase("CHARTER NON-REFUNDABLE")) {
                                 mship = "CHARTER";
                             }
                         }

                     }  // end if tpcboston

                     //******************************************************************
                     //   TPC Craig Ranch
                     //******************************************************************
                     //
                     if (club.equals("tpccraigranch")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 memid = memid + primary;
                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }

                     }  // end if tpccraigranch


                     //******************************************************************
                     //   TPC Potomac
                     //******************************************************************
                     //
                     if (club.equals("tpcpotomac")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                           //  memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 //memid = memid + "A";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6") || primary.equals("7")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }
/*
                                if (primary.equals("2")) {
                                    memid += "B";
                                } else if (primary.equals("3")) {
                                    memid += "C";
                                } else if (primary.equals("4")) {
                                    memid += "D";
                                } else if (primary.equals("5")) {
                                    memid += "E";
                                } else if (primary.equals("6")) {
                                    memid += "F";
                                } else if (primary.equals("7")) {
                                    memid += "G";
                                }
 */
                                primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
/*
                                 if (primary.equals("8")) {
                                     memid += "H";
                                 } else if (primary.equals("9")) {
                                     memid += "I";
                                 } else if (primary.equals("10")) {
                                     memid += "J";
                                 } else if (primary.equals("11")) {
                                     memid += "K";
                                 }
                                 */
                                 primary = "AC";
                             }
                         }

                     }  // end if tpcpotomac


                     //******************************************************************
                     //   TPC Summerlin
                     //******************************************************************
                     //
                     if (club.equals("tpcsummerlin")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6") || primary.equals("7") ||
                                         primary.equals("8")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 memid = memid + primary;
                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }

                     }  // end if tpcsummerlin


                     //******************************************************************
                     //   TPC Twin Cities
                     //******************************************************************
                     //
                     if (club.equals("tpctc")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                            mship = checkTPCmship(mship, club);       // check for non-golf and trim the mships

                            if (mship.equals( "" )) {       // if mship to be skipped

                                skip = true;       //  skip these
                                warnCount++;
                                warnMsg = warnMsg + "\n" +
                                            "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                            }
                         }

                         if (skip == false) {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("P")) {       // Primary

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }

                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse

                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }

                             } else  if (primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                         primary.equals("5") || primary.equals("6")) {             // Dependent

                                if (birth > 0) {                    // if birth date provided

                                    mtype = checkTPCkids(birth, gender);          // get mtype based on age

                                    if (mtype.equals("")) {               // if too old now (26)

                                       skip = true;                       // force them to go inactive
                                       warnCount++;
                                       warnMsg = warnMsg + "\n" +
                                               "  -SKIPPED: DEPENDENT OVER 25!";
                                    }

                                } else {

                                   if (gender.equalsIgnoreCase("F")) {      // defaults
                                       mtype = "Dependent Female";
                                   } else {
                                       mtype = "Dependent Male";
                                   }
                                }

                                 memid = memid + primary;
                                 primary = "D";

                             } else {          // Authorized Caller

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }
                                 memid = memid + primary;
                                 primary = "AC";
                             }
                         }
                      /*

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else if ((primary.equals("1") || primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                    primary.equals("5") || primary.equals("6") || primary.equals("7") || primary.equals("8")) &&
                                    birth == 0) {
                             skip = true;
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -BIRTH DATE missing for DEPENDENT!";
                         } else {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future


                             mship = toTitleCase(mship);         // make sure mship is titlecased


                             if (mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Member")) {
                                 skip = true;
                                 warnCount++;
                                 warnMsg = warnMsg + "\n" +
                                         "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                             }

                             if (primary.equals("1") || primary.equals("2") || primary.equals("3") || primary.equals("4") ||
                                 primary.equals("5") || primary.equals("6") || primary.equals("7") || primary.equals("8")) {

                                 //
                                 //  Determine the age in years
                                 //

                                 Calendar cal = new GregorianCalendar();       // get todays date

                                 int year = cal.get(Calendar.YEAR);
                                 int month = cal.get(Calendar.MONTH) +1;
                                 int day = cal.get(Calendar.DAY_OF_MONTH);

                                 year = year - 18;             // backup 16 years

                                 int oldDate = (year * 10000) + (month * 100) + day;   // get date

                                 if (birth > oldDate) {     // if member is < 18 yrs old
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Certified Dependent Female";
                                     } else {
                                         mtype = "Certified Dependent Male";
                                     }
                                 } else {
                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Dependent Female";
                                     } else {
                                         mtype = "Dependent Male";
                                     }
                                 }

                                 if (primary.equals("1")) {
                                     memid = memid + "B";
                                 } else if (primary.equals("2")) {
                                     memid = memid + "C";
                                 } else if (primary.equals("3")) {
                                     memid = memid + "D";
                                 } else if (primary.equals("4")) {
                                     memid = memid + "E";
                                 } else if (primary.equals("5")) {
                                     memid = memid + "F";
                                 } else if (primary.equals("6")) {
                                     memid = memid + "G";
                                 } else if (primary.equals("7")) {
                                     memid = memid + "H";
                                 } else if (primary.equals("8")) {
                                     memid = memid + "I";
                                 }

                                 primary = "D";

                             } else if (primary.equals("9") || primary.equals("10")) {
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Authorized Caller Female";
                                 } else {
                                     mtype = "Authorized Caller Male";
                                 }

                                 if (primary.equals("9")) {
                                     memid = memid + "J";
                                 } else if (primary.equals("10")) {
                                     memid = memid + "K";
                                 }

                             } else if (primary.equalsIgnoreCase("P")) {       // Primary
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }
                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse
                                 memid = memid + "A";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }
                             } else {
                                 skip = true;
                                 errCount++;
                                 errMsg = errMsg + "\n" +
                                         "  -UNKNOWN RELATIONSHIP TYPE!";
                             }
                         }
                         */
                     }  // end if tpctc

/*
                     //******************************************************************
                     //   Royal Oaks CC - Houston
                     //******************************************************************
                     //
                     if (club.equals("royaloakscc")) {

                         int mshipInt = 0;

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("S")) {
                                 memid = memid + "A";
                             } else if (primary.equals("2")) {
                                 mtype = "Dependent";
                                 memid = memid + "B";
                             } else if (primary.equals("3")) {
                                 mtype = "Dependent";
                                 memid = memid + "C";
                             } else if (primary.equals("4")) {
                                 mtype = "Dependent";
                                 memid = memid + "D";
                             } else if (primary.equals("5")) {
                                 mtype = "Dependent";
                                 memid = memid + "E";
                             } else if (primary.equals("6")) {
                                 mtype = "Dependent";
                                 memid = memid + "F";
                             } else if (primary.equals("7")) {
                                 mtype = "Dependent";
                                 memid = memid + "G";
                             }

                             if (gender.equalsIgnoreCase("F") && !mtype.equals("Dependent")) {
                                 mtype = "Adult Female";
                             } else if (!mtype.equals("Dependent")) {
                                 mtype = "Adult Male";
                             }

                             try {
                                 mshipInt = Integer.parseInt(mNum);
                             } catch (Exception exc) {
                                 skip = true;
                                 warnCount++;
                                 warnMsg = warnMsg + "\n" +
                                         "  -SKIPPED: Invalid Member Number!";
                             }

                             if (!skip) {
                                 if (mshipInt >= 0 && mshipInt < 200) {
                                     mship = "Honorary";
                                 } else if (mshipInt >= 500 && mshipInt < 600) {
                                     mship = "Executive Honorary";
                                 } else if (mshipInt >= 1000 && mshipInt < 2000) {
                                     mship = "Golf";
                                 } else if (mshipInt >= 2000 && mshipInt < 3000) {
                                     mship = "Executive";
                                 } else if (mshipInt >= 3000 && mshipInt < 3400) {
                                     mship = "Sports Club w/Golf";
                                 } else if (mshipInt >= 5000 && mshipInt < 5400) {
                                     mship = "Preview Golf";
                                 } else if (mshipInt >= 5400 && mshipInt < 5700) {
                                     mship = "Preview Executive";
                                 } else if (mshipInt >= 7000 && mshipInt < 7500) {
                                     mship = "Sampler Golf";
                                 } else if (mshipInt >= 7500 && mshipInt < 8000) {
                                     mship = "Sampler Executive";
                                 } else {
                                     skip = true;
                                     warnCount++;
                                     warnMsg = warnMsg + "\n" +
                                             "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                                 }
                             }
                         }
                     }  // end if royaloakscc
*/

                     //******************************************************************
                     //   TPC San Francisco Bay
                     //******************************************************************
                     //
                     if (club.equals("tpcsfbay")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (mship.equalsIgnoreCase("Social")) {
                                 skip = true;
                                 warnCount++;
                                 warnMsg = warnMsg + "\n" +
                                         "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                             }

                             if (primary.equalsIgnoreCase("P")) {       // Primary
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     mtype = "Primary Male";
                                 }
                             } else if (primary.equalsIgnoreCase("S")) {        // Spouse
                                 memid = memid + "1";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Spouse Female";
                                 } else {
                                     mtype = "Spouse Male";
                                 }
                             } else {         // Dependent
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Dependent Female";
                                 } else {
                                     mtype = "Dependent Male";
                                 }
                                 memid = memid + primary;
                                 primary = "D";
                             }
                         }
                     }  // end if tpcsfbay

/*
                     //******************************************************************
                     //   Mission Viejo - missionviejo
                     //******************************************************************
                     //
                     if (club.equals("missionviejo")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (gender.equalsIgnoreCase("F")) {
                                 gender = "F";
                                 memid += "B";
                                 mtype = "Green";
                             } else {
                                 gender = "M";
                                 memid += "A";
                                 mtype = "Gold";
                             }

                             if (mship.equalsIgnoreCase("Employee")) {
                                 mship = "Staff";
                             } else if (mship.equalsIgnoreCase("Equity") || mship.equalsIgnoreCase("Honorary") || mship.equalsIgnoreCase("Member")) {
                                 mship = "Equity";
                             } else if (mship.equalsIgnoreCase("Non-Res") || mship.equalsIgnoreCase("Non-Resident")) {
                                 mship = "Non-Res";
                             } else if (mship.equalsIgnoreCase("Senior")) {
                                 mship = "Senior";
                             } else {
                                 skip = true;
                                 warnCount++;
                                 warnMsg = warnMsg + "\n" +
                                         "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                             }
                         }
                     }  // end if missionviejo

*/

                     //******************************************************************
                     //   Cherry Hills CC - cherryhills
                     //******************************************************************
                     //
                     if (club.equals("cherryhills")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum + "-000";         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("S")) {
                                 memid += "A";
                                 mtype = "Spouse";
                             } else {
                                 mtype = "Member";
                             }
                             
                             if (mship.equalsIgnoreCase("CLG")) {
                                 mship = "Clergy";
                             } else if (mship.equalsIgnoreCase("RES")) {
                                 mship = "Resident";
                             } else if (mship.equalsIgnoreCase("SRA")) {
                                 mship = "Special Resident A";
                             } else if (mship.equalsIgnoreCase("SRB")) {
                                 mship = "Special Resident B";
                             } else if (mship.equalsIgnoreCase("SRC")) {
                                 mship = "Special Resident C";
                             } else if (mship.equalsIgnoreCase("NRE")) {
                                 mship = "Non-Resident";
                             } else if (mship.equalsIgnoreCase("SSP")) {
                                 mship = "Surviving Spouse";
                             } else if (mship.equalsIgnoreCase("FSP")) {
                                 mship = "Former Spouse";
                             } else if (mship.equalsIgnoreCase("LFE")) {
                                 mship = "Life Member";
                             } else if (mship.equalsIgnoreCase("HLF")) {
                                 mship = "Honorary Life";
                             } else if (mship.equalsIgnoreCase("SEN")) {
                                 mship = "Senior";
                             } else if (mship.equalsIgnoreCase("RE")) {
                                 mship = "Resident Emeritus";
                             } else {
                                 skip = true;
                                 warnCount++;
                                 warnMsg = warnMsg + "\n" +
                                         "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                             }
                         }
                     }  // end if cherryhills

                     //
                     //   Mission Viejo CC
                     //
                     /*
                     if (club.equals( "missionviejo" )) {

                         found = true;        // club found

                         //
                         //  Determine if we should process this record
                         //
                         if (memid.equals( "" )) {

                             skip = true;          // skip it
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMID missing!";

                         } else if (mship.equalsIgnoreCase( "Social" ) || mship.equalsIgnoreCase( "Social SS" ) || mship.equalsIgnoreCase( "Spouse" ) ||
                                 mship.equalsIgnoreCase( "Spouse S" ) || mship.equalsIgnoreCase( "Child" )) {

                             skip = true;
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: NON-GOLF MEMBERSHIP TYPE!";

                         } else if (!mship.equals( "" )) {

                         }

                     }         // end of IF missionviejo club
*/

                   //******************************************************************
                   //   Philadelphia Cricket Club
                   //******************************************************************
                   //
                   if (club.equals( "philcricket" )) {

                      found = true;    // club found

                      //
                      //  Make sure this is not an admin record or missing mship/mtype
                      //
                      if (mship.equals( "" )) {

                          skip = true;          // skip it
                          errCount++;
                          errMsg = errMsg + "\n" +
                                   "  -MEMBERSHIP TYPE missing!";

                      } else if (lname.equalsIgnoreCase( "admin" )) {

                          skip = true;         // skip it
                          warnCount++;
                          warnMsg = warnMsg + "\n" +
                                  "  -SKIPPED: 'Admin' MEMBERSHIP TYPE!";
                      } else {

                         if (mship.equalsIgnoreCase("Leave of Absence") || mship.equalsIgnoreCase("Loa") || mship.equalsIgnoreCase("Member")) {

                             skip = true;
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                         }

                         useWebid = true;      // use webid for this club
                         webid = memid;        // use memid for webid
                         memid = mNum;

                         //
                         //  Set POS Id in case they ever need it
                         //
                         posid = mNum;

                         if (primary.equalsIgnoreCase("S")) {
                             memid += "A";
                         } else if (primary.equals("2")) {
                             memid += "B";
                         } else if (primary.equals("3")) {
                             memid += "C";
                         } else if (primary.equals("4")) {
                             memid += "D";
                         } else if (primary.equals("5")) {
                             memid += "E";
                         } else if (primary.equals("6")) {
                             memid += "F";
                         } else if (primary.equals("7")) {
                             memid += "G";
                         } else if (primary.equals("8")) {
                             memid += "H";
                         } else if (primary.equals("9")) {
                             memid += "I";
                         }

                         //
                         //  determine member type
                         //
                         if (mship.equalsIgnoreCase( "golf stm family" ) || mship.equalsIgnoreCase( "golf stm ind." )) {

                            lname = lname + "*";       // add an astericks
                         }

                         // if mtype = no golf, add ^ to their last name
                         if (mship.equalsIgnoreCase( "no golf" )) {

                            lname += "^";
                         }

                         mship = toTitleCase(mship);       // mship = mtype

                      }
                   }         // end of IF club = philcricket
                     
/*
                     //******************************************************************
                     //   Sea Pines CC - seapines
                     //******************************************************************
                     //
                     if (club.equals("seapines")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                             useWebid = true;      // use webid for this club
                             webid = memid;        // use memid for webid
                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (primary.equalsIgnoreCase("S")) {
                                 memid += "A";
                             }

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Adult Female";
                             } else {
                                 mtype = "Adult Male";
                             }

                             if (mship.equalsIgnoreCase("EQUITY-NP") || mship.equalsIgnoreCase("RESIGN A")  || mship.equalsIgnoreCase("ASSOC") || mship.equalsIgnoreCase("NON-EQ-NP")) {
                                 mship = "Social";
                             } else if (mship.equalsIgnoreCase("SELECT") || mship.equalsIgnoreCase("TENNIS")) {
                                 mship = "Tennis";
                             } else if (mship.equalsIgnoreCase("GOLF") || mship.equalsIgnoreCase("GOLF-CART")) {
                                 mship = "Golf";
                             } else {
                                 skip = true;
                                 warnCount++;
                                 warnMsg = warnMsg + "\n" +
                                         "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                             }
                         }
                     }  // end if seapines
*/

                     //
                     //******************************************************************
                     //  Common processing - add or update the member record
                     //******************************************************************
                     //
                     if (skip == false && found == true && !fname.equals("") && !lname.equals("") && !memid.equals("")) {

                        //
                        //   now determine if we should update an existing record or add the new one
                        //
                        memid_old = "";
                        fname_old = "";
                        lname_old = "";
                        mi_old = "";
                        mship_old = "";
                        mtype_old = "";
                        email_old = "";
                        mNum_old = "";
                        ghin_old = "";
                        bag_old = "";
                        posid_old = "";
                        email2_old = "";
                        phone_old = "";
                        phone2_old = "";
                        suffix_old = "";
                        u_hcap_old = 0;
                        c_hcap_old = 0;
                        birth_old = 0;
                        email_bounce1 = 0;
                        email_bounce2 = 0;


                        //
                        //  Truncate the string values to avoid sql error
                        //
                        if (!mi.equals( "" )) {       // if mi specified

                           mi = truncate(mi, 1);           // make sure it is only 1 char
                        }
                        if (!memid.equals( "" )) {

                           memid = truncate(memid, 15);
                        }
                        if (!password.equals( "" )) {

                           password = truncate(password, 15);
                        }
                        if (!lname.equals( "" )) {

                           lname = truncate(lname, 20);
                        }
                        if (!fname.equals( "" )) {

                           fname = truncate(fname, 20);
                        }
                        if (!mship.equals( "" )) {

                           mship = truncate(mship, 30);
                        }
                        if (!mtype.equals( "" )) {

                           mtype = truncate(mtype, 30);
                        }
                        if (!email.equals( "" )) {

                           email = truncate(email, 50);
                        }
                        if (!email2.equals( "" )) {

                           email2 = truncate(email2, 50);
                        }
                        if (!mNum.equals( "" )) {

                           mNum = truncate(mNum, 10);
                        }
                        if (!ghin.equals( "" )) {

                           ghin = truncate(ghin, 16);
                        }
                        if (!bag.equals( "" )) {

                           bag = truncate(bag, 12);
                        }
                        if (!posid.equals( "" )) {

                           posid = truncate(posid, 15);
                        }
                        if (!phone.equals( "" )) {

                           phone = truncate(phone, 24);
                        }
                        if (!phone2.equals( "" )) {

                           phone2 = truncate(phone2, 24);
                        }
                        if (!suffix.equals( "" )) {

                           suffix = truncate(suffix, 4);
                        }
                        if (!webid.equals( "" )) {

                           webid = truncate(webid, 15);
                        }

                        //
                        //  Use try/catch here so processing will continue on rest of file if it fails
                        //
                        try {

                           if (useWebid == false) {            // use webid to locate member?

                              pstmt2 = con.prepareStatement (
                                       "SELECT * FROM member2b WHERE username = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, memid);

                           } else {                            // use webid

                              pstmt2 = con.prepareStatement (
                                       "SELECT * FROM member2b WHERE webid = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, webid);
                           }

                           rs = pstmt2.executeQuery();            // execute the prepared stmt

                           if(rs.next()) {

                              memid_old = rs.getString("username");            // get username in case we used webid
                              lname_old = rs.getString("name_last");
                              fname_old = rs.getString("name_first");
                              mi_old = rs.getString("name_mi");
                              mship_old = rs.getString("m_ship");
                              mtype_old = rs.getString("m_type");
                              email_old = rs.getString("email");
                              mNum_old = rs.getString("memNum");
                              ghin_old = rs.getString("ghin");
                              bag_old = rs.getString("bag");
                              birth_old = rs.getInt("birth");
                              posid_old = rs.getString("posid");
                              email2_old = rs.getString("email2");
                              phone_old = rs.getString("phone1");
                              phone2_old = rs.getString("phone2");
                              suffix_old = rs.getString("name_suf");
                              inact_old = rs.getInt("inact");
                              email_bounce1 = rs.getInt("email_bounced");
                              email_bounce2 = rs.getInt("email2_bounced");

                           }
                           pstmt2.close();              // close the stmt


                           boolean memFound = false;
                           boolean dup = false;
                           boolean userChanged = false;
                           boolean nameChanged = false;
                           String dupuser = "";
                           String dupmnum = "";
                           String dupwebid = "";

                           webid_new = webid;                       // default

                           if ((club.equals("tpcsouthwind") || club.equals("tpcpotomac") || club.equals("tpcsugarloaf")) && !memid.equals(memid_old)) {       // Look into making this change for ALL tpc clubs!

                               // memid has changed!  Update the username
                               memid_new = memid;
                               userChanged = true;

                           } else {
                               memid_new = memid_old;       // Don't change for old clubs
                           }

                           //
                           //  If member NOT found, then check if new member OR id has changed
                           //
                           if (fname_old.equals( "" )) {            // if member NOT found

                              //
                              //  New member - first check if name already exists
                              //
                              pstmt2 = con.prepareStatement (
                                       "SELECT username, memNum, webid FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, lname);
                              pstmt2.setString(2, fname);
                              pstmt2.setString(3, mi);
                              rs = pstmt2.executeQuery();            // execute the prepared stmt

                              if (rs.next()) {

                                 dupuser = rs.getString("username");          // get this username
                                 dupmnum = rs.getString("memNum");
                                 dupwebid = rs.getString("webid");            // get this webid

                                 if (club.equals( "virginiacc" ) || club.equals( "algonquin" ) || club.equals( "congressional" )) {

                                    dup = true;                // do not change members for these clubs

                                 } else {

                                    //
                                    //  name already exists - see if this is the same member
                                    //
                                    if (!dupmnum.equals( "" ) && dupmnum.equals( mNum )) {   // if name and mNum match, then memid or webid must have changed

                                       if (useWebid == true) {            // use webid to locate member?

                                          webid_new = webid;                  // set new ids
                                          memid_new = dupuser;
                                          memid_old = dupuser;                    // update this record

                                       } else {

                                          webid_new = dupwebid;                  // set new ids
                                          memid_new = memid;
                                          memid_old = dupuser;                       // update this record
                                          userChanged = true;                    // indicate the username has changed
                                       }

                                       memFound = true;                      // update the member

                                    } else {

                                       dup = true;        // dup member - do not add
                                    }
                                 }

                              }
                              pstmt2.close();              // close the stmt

                           } else {      // member found

                              memFound = true;
                           }

                           //
                           //  Now, update the member record if existing member
                           //
                           if (memFound == true) {                   // if member exists

                              changed = false;                       // init change indicator

                              lname_new = lname_old;

                              if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                                 lname_new = lname;         // set value from CE record
                                 changed = true;
                                 nameChanged = true;
                              }

                              fname_new = fname_old;

                              if (club.equals( "virginiacc" ) || club.equals( "algonquin" ) || club.equals( "congressional" )) {

                                 fname = fname_old;            // DO NOT change first names
                              }

                              if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                                 fname_new = fname;         // set value from CE record
                                 changed = true;
                                 nameChanged = true;
                              }

                              mi_new = mi_old;

                              if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                                 mi_new = mi;         // set value from CE record
                                 changed = true;
                                 nameChanged = true;
                              }

                              mship_new = mship_old;

                              if (!mship.equals( "" ) && !mship_old.equals( mship )) {

                                 mship_new = mship;         // set value from CE record
                                 changed = true;
                              }

                              mtype_new = mtype_old;
                              birth_new = birth_old;

                              if (club.equals( "meridiangc" )) {    // TEMP until they fix gender !!!!!!!!!!!!!!!!!!!!!

                                 mtype = mtype_old;            // DO NOT change mtype
                              }

                              if (!mtype.equals( "" ) && (club.equalsIgnoreCase("brantford") || club.equalsIgnoreCase("oakhillcc") || !mtype.equals( "Dependent" )) && !mtype_old.equals( mtype )) {

                                 mtype_new = mtype;         // set value from CE record
                                 changed = true;
                              }

                              if (birth > 0 && birth != birth_old) {

                                 birth_new = birth;         // set value from CE record
                                 changed = true;
                              }

                              ghin_new = ghin_old;

                              if (!ghin.equals( "" ) && !ghin_old.equals( ghin )) {

                                 ghin_new = ghin;         // set value from CE record
                                 changed = true;
                              }

                              bag_new = bag_old;

                              if (!bag.equals( "" ) && !bag_old.equals( bag )) {

                                 bag_new = bag;         // set value from CE record
                                 changed = true;
                              }

                              posid_new = posid_old;

                              if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                                 posid_new = posid;         // set value from CE record
                                 changed = true;
                              }

                              phone_new = phone_old;

                              if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                                 phone_new = phone;         // set value from CE record
                                 changed = true;
                              }

                              phone2_new = phone2_old;

                              if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                                 phone2_new = phone2;         // set value from CE record
                                 changed = true;
                              }

                              suffix_new = suffix_old;

                              if (!suffix.equals( "" ) && !suffix_old.equals( suffix )) {

                                 suffix_new = suffix;         // set value from CE record
                                 changed = true;
                              }

                              email_new = email_old;        // start with old emails
                              email2_new = email2_old;

                              //
                              //  Update email addresses if specified and different than current
                              //
                              if ((!email.equals( "" ) || club.startsWith("tpc")) && !email_old.equals( email )) {

                                 email_new = email;         // set value from CE record
                                 changed = true;
                                 email_bounce1 = 0;         // reset bounce flag
                              }

                              if (club.equals("colletonriverclub")) {   // don't update email2 for these clubs
                                  email2 = email2_old;
                              }

                              if ((!email2.equals( "" ) || club.startsWith("tpc")) && !email2_old.equals( email2 )) {

                                 email2_new = email2;         // set value from CE record
                                 changed = true;
                                 email_bounce2 = 0;         // reset bounce flag
                              }

                              // don't allow both emails to be the same
                              if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";


                              mNum_new = mNum_old;     // do not change mNums  (?? not sure why we do this for CE clubs ??)

                              if (club.equals( "weeburn" ) || club.equals( "benttreecc" ) || club.equals( "algonquin" ) ||
                                  club.equals( "berkeleyhall" ) || club.equals( "cherrycreek" ) || club.equals("internationalcc") ||
                                  club.startsWith( "tpc" ) || club.equals( "missionviejo" ) || club.equals("virginiacc") ||
                                  club.equals("oakhillcc") || club.equals( "orchidisland" )) {     // change mNums for some clubs

                                 if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                                    mNum_new = mNum;         // set value from CE record
                                    changed = true;
                                 }
                              }

                              inact_new = 0;                // do not change inact status for most clubs

                              if (club.equals( "congressional" )) {     // change status for Congressional

                                 if (inact_new != inact) {             // if status has changed

                                    inact_new = inact;                 // set value from CE record
                                    changed = true;
                                 }
                              }


                              if (club.equals( "benttreecc" )) {          // special processing for Bent Tree

                                 String tempM = remZeroS(mNum_old);       // strip alpha from our old mNum

                                 if ((tempM.startsWith("5") || tempM.startsWith("7")) && inact_old == 1) {

                                    //  If our mNum contains an old inactive value and the member is inactive
                                    //  then set him active and let mNum change (above).

                                    inact_new = inact;       // set value from CE record (must be active to get this far)
                                 }
                              }


                              //
                              //  Update our record if something has changed
                              //
                              pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                              "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                              "memNum = ?, ghin = ?, bag = ?, birth = ?, posid = ?, email2 = ?, phone1 = ?, " +
                              "phone2 = ?, name_suf = ?, webid = ?, inact = ?, last_sync_date = now(), gender = ?, " +
                              "email_bounced = ?, email2_bounced = ? " +
                              "WHERE username = ?");

                              pstmt2.clearParameters();        // clear the parms
                              pstmt2.setString(1, memid_new);
                              pstmt2.setString(2, lname_new);
                              pstmt2.setString(3, fname_new);
                              pstmt2.setString(4, mi_new);
                              pstmt2.setString(5, mship_new);
                              pstmt2.setString(6, mtype_new);
                              pstmt2.setString(7, email_new);
                              pstmt2.setString(8, mNum_new);
                              pstmt2.setString(9, ghin_new);
                              pstmt2.setString(10, bag_new);
                              pstmt2.setInt(11, birth_new);
                              pstmt2.setString(12, posid_new);
                              pstmt2.setString(13, email2_new);
                              pstmt2.setString(14, phone_new);
                              pstmt2.setString(15, phone2_new);
                              pstmt2.setString(16, suffix_new);
                              pstmt2.setString(17, webid_new);
                              pstmt2.setInt(18, inact_new);
                              pstmt2.setString(19, gender);
                              pstmt2.setInt(20, email_bounce1);
                              pstmt2.setInt(21, email_bounce2);

                              pstmt2.setString(22, memid_old);
                              pstmt2.executeUpdate();

                              pstmt2.close();              // close the stmt

                              ucount++;                    // count records updated

                              //
                              //  Member updated - now see if the username or name changed
                              //
                              if (userChanged == true || nameChanged == true) {        // if username or name changed

                                 //
                                 //  username or name changed - we must update other tables now
                                 //
                                 StringBuffer mem_name = new StringBuffer( fname_new );       // get the new first name

                                 if (!mi_new.equals( "" )) {
                                    mem_name.append(" " +mi_new);               // new mi
                                 }
                                 mem_name.append(" " +lname_new);               // new last name

                                 String newName = mem_name.toString();          // convert to one string

                                 Admin_editmem.updTeecurr(newName, memid_new, memid_old, con);      // update teecurr with new values

                                 Admin_editmem.updTeepast(newName, memid_new, memid_old, con);      // update teepast with new values

                                 Admin_editmem.updLreqs(newName, memid_new, memid_old, con);        // update lreqs with new values

                                 Admin_editmem.updPartner(memid_new, memid_old, con);               // update partner with new values

                                 Admin_editmem.updEvents(newName, memid_new, memid_old, con);        // update evntSignUp with new values

                                 Admin_editmem.updLessons(newName, memid_new, memid_old, con);       // update the lesson books with new values
                              }


                           } else {       // member NOT found


                              if (dup == false && !fname.equals("") && !lname.equals("")) {          // if name does not already exist

                                 //
                                 //  New member - add it
                                 //
                                 pstmt2 = con.prepareStatement (
                                    "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                                    "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                                    "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, " +
                                    "webid, last_sync_date, gender) " +
                                    "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,?,now(),?)");

                                 pstmt2.clearParameters();        // clear the parms
                                 pstmt2.setString(1, memid);        // put the parm in stmt
                                 pstmt2.setString(2, password);
                                 pstmt2.setString(3, lname);
                                 pstmt2.setString(4, fname);
                                 pstmt2.setString(5, mi);
                                 pstmt2.setString(6, mship);
                                 pstmt2.setString(7, mtype);
                                 pstmt2.setString(8, email);
                                 pstmt2.setFloat(9, c_hcap);
                                 pstmt2.setFloat(10, u_hcap);
                                 pstmt2.setString(11, mNum);
                                 pstmt2.setString(12, ghin);
                                 pstmt2.setString(13, bag);
                                 pstmt2.setInt(14, birth);
                                 pstmt2.setString(15, posid);
                                 pstmt2.setString(16, email2);
                                 pstmt2.setString(17, phone);
                                 pstmt2.setString(18, phone2);
                                 pstmt2.setString(19, suffix);
                                 pstmt2.setString(20, webid);
                                 pstmt2.setString(21, gender);
                                 pstmt2.executeUpdate();          // execute the prepared stmt

                                 pstmt2.close();              // close the stmt

                                 ncount++;                    // count records added (new)

                              } else if (dup) {
                                  errCount++;
                                  errMsg = errMsg + "\n  -Dup user found:\n" +
                                          "    new: memid = " + memid + "  :  cur: " + dupuser + "\n" +
                                          "         webid = " + webid + "  :       " + dupwebid + "\n" +
                                          "         mNum  = " + mNum  + "  :       " + dupmnum;
                              }
                           }

                           pcount++;          // count records processed (not skipped)


                        }
                        catch (Exception e3b) {
                           errCount++;
                           errMsg = errMsg + "\n  -Error2 processing roster (record #" +rcount+ ") for " +club+ "\n" +
                                   "    line = " +line+ ": " + e3b.getMessage();   // build msg
                        }

                     } else {

                         // Only report errors that AREN'T due to skip == true, since those were handled earlier!
                         if (!found) {
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBER NOT FOUND!";
                         }
                         if (fname.equals("")) {
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -FIRST NAME missing!";
                         }
                         if (lname.equals("")) {
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -LAST NAME missing!";
                         }
                         if (memid.equals("")) {
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -USERNAME missing!";
                         }

                     }   // end of IF skip

                  }   // end of IF inactive

               }   // end of IF minimum requirements

            }   // end of IF tokens

         }   // end of IF header row

         // log any errors and warnings that occurred
         if (errCount > 0) {
             totalErrCount += errCount;
             errList.add(errMemInfo + "\n  *" + errCount + " error(s) found*" + errMsg + "\n");
         }
         if (warnCount > 0) {
             totalWarnCount += warnCount;
             warnList.add(errMemInfo + "\n  *" + warnCount + " warning(s) found*" + warnMsg + "\n");
         }
      }   // end of while

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {       // if we processed this club

         pstmt2 = con.prepareStatement (
           "UPDATE member2b SET inact = 1 " +
           "WHERE last_sync_date != now() AND last_sync_date != '0000-00-00'");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.executeUpdate();

         pstmt2.close();              // close the stmt
         
         
         //
         //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
         //
         setRSind(con, club);

      }
   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster (record #" +rcount+ ") for " +club+ ", line = " +line+ ": " + e3.getMessage() + "\n";   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
      errorMsg = "Error in Common_sync.ceSync: ";            // reset msg
   }

   // Print error and warning count totals to error log
   SystemUtils.logErrorToFile("" +
           "Total Errors Found: " + totalErrCount + "\n" +
           "Total Warnings Found: " + totalWarnCount + "\n", club, true);

   // Print errors and warnings to error log
   if (totalErrCount > 0) {
       SystemUtils.logErrorToFile("" +
            "********************************************************************\n" +
            "****ERRORS FOR " + club + " (Member WAS NOT synced!)\n" +
            "********************************************************************\n", club, true);
       while (errList.size() > 0) {
           SystemUtils.logErrorToFile(errList.remove(0), club, true);
       }
   }
   if (totalWarnCount > 0) {
       SystemUtils.logErrorToFile("" +
            "********************************************************************\n" +
            "****WARNINGS FOR " + club + " (Member MAY NOT have synced!)\n" +
            "********************************************************************\n", club, true);
       while (warnList.size() > 0) {
           SystemUtils.logErrorToFile(warnList.remove(0), club, true);
       }
   }

   //  TEMP!!!!
   if (club.equals("berkeleyhall")) {

      errorMsg = " CE sync complete. Records = " +rcount+ " for " +club+ ", records processed = " +pcount+ ", records added = " +ncount+ ", records updated = " +ucount + "\n";   // build msg
      SystemUtils.logErrorToFile(errorMsg, club, true);                                                  // log it
   }

   // Print end time to error log
   SystemUtils.logErrorToFile("End time: " + new java.util.Date().toString() + "\n", club, true);
 }


 //
 //   Method to process files from FlexScape system
 //
 private static void flexSync(Connection con, InputStreamReader isr, String club) {

   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;

   // Values from Flexscape records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String webid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String mobile = "";
   String primary = "";
   String active = "";

   String mship2 = ""; // used to tell if match was found

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;

   // Values from ForeTees records
   //
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";
   String memid_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;

   // Values for New ForeTees records
   //
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";
   String memid_new = "";
   String last_mship = "";
   String last_mnum = "";

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int rcount = 0;
   int newCount = 0;
   int modCount = 0;
   int work = 0;

   String errorMsg = "Error in Common_sync.flexSync: ";

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean headerFound = false;
   boolean found = false;
   boolean memidChanged = false;


   SystemUtils.logErrorToFile("FlexScape: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

      BufferedReader br = new BufferedReader(isr);

      while (true) {

         line = br.readLine();

         if (line == null) {
            break;
         }

         //  Skip the 1st row (header row)

         if (headerFound == false) {

            headerFound = true;

         } else {

            skip = false;
            found = false;        // default to club NOT found

            //
            // *********************************************************************
            //
            //   The following will be dependent on the club - customized
            //
            // *********************************************************************
            //
            if (club.equals( "fourbridges" )) {

               found = true;        // club found

               //  Remove the dbl quotes and check for embedded commas

               line = cleanRecord4( line );
               line = cleanRecord2( line );

               rcount++;                          // count the records

               //  parse the line to gather all the info

               StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

               if ( tok.countTokens() > 10 ) {     // enough data ?

                  webid = tok.nextToken();
                  memid = tok.nextToken();
                  fname = tok.nextToken();
                  mi = tok.nextToken();
                  lname = tok.nextToken();
                  gender = tok.nextToken();
                  email = tok.nextToken();
                  phone = tok.nextToken();
                  phone2 = tok.nextToken();
                  temp = tok.nextToken();
                  primary = tok.nextToken();

                  mNum = "";
                  suffix = "";
                  mship = "";
                  mtype = "";
                  email2 = "";
                  bag = "";
                  ghin = "";
                  u_hndcp = "";
                  c_hndcp = "";
                  posid = "";
                  mobile = "";
                  active = "";

                  //
                  //  Check for ? (not provided)
                  //
                  if (webid.equals( "?" )) {

                     webid = "";
                  }
                  if (memid.equals( "?" )) {

                     memid = "";
                  }
                  if (fname.equals( "?" )) {

                     fname = "";
                  }
                  if (mi.equals( "?" )) {

                     mi = "";
                  }
                  if (lname.equals( "?" )) {

                     lname = "";
                  }
                  if (gender.equals( "?" )) {

                     gender = "";
                  }
                  if (email.equals( "?" )) {

                     email = "";
                  }
                  if (phone.equals( "?" )) {

                     phone = "";
                  }
                  if (phone2.equals( "?" )) {

                     phone2 = "";
                  }
                  if (temp.equals( "?" ) || temp.equals( "0" )) {

                     temp = "";
                  }
                  if (primary.equals( "?" )) {

                     primary = "";
                  }

                  //
                  //  Determine if we should process this record (does it meet the minimum requirements?)
                  //
                  if (!webid.equals( "" ) && !memid.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                     //
                     //  Remove spaces, etc. from name fields
                     //
                     tok = new StringTokenizer( fname, " " );     // delimiters are space

                     fname = tok.nextToken();                     // remove any spaces and middle name

                     if ( tok.countTokens() > 0 ) {

                        mi = tok.nextToken();                     // over-write mi if already there
                     }

                     if (!suffix.equals( "" )) {                     // if suffix provided

                        tok = new StringTokenizer( suffix, " " );     // delimiters are space

                        suffix = tok.nextToken();                     // remove any extra (only use one value)
                     }

                     tok = new StringTokenizer( lname, " " );     // delimiters are space

                     lname = tok.nextToken();                     // remove suffix and spaces

                     if (!suffix.equals( "" )) {                  // if suffix provided

                        lname = lname + "_" + suffix;             // append suffix to last name

                     } else {                                     // sufix after last name ?

                        if ( tok.countTokens() > 0 ) {

                           suffix = tok.nextToken();
                           lname = lname + "_" + suffix;          // append suffix to last name
                        }
                     }

                     //
                     //  Determine the handicaps
                     //
                     u_hcap = -99;                    // indicate no hndcp
                     c_hcap = -99;                    // indicate no c_hndcp


                     //
                     //  convert birth date (mm/dd/yyyy to yyyymmdd)
                     //
                     birth = 0;

                     if (!temp.equals( "" )) {

                        String b1 = "";
                        String b2 = "";
                        String b3 = "";
                        int mm = 0;
                        int dd = 0;
                        int yy = 0;

                        tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                        if ( tok.countTokens() > 2 ) {

                           b1 = tok.nextToken();
                           b2 = tok.nextToken();
                           b3 = tok.nextToken();

                           mm = Integer.parseInt(b1);
                           dd = Integer.parseInt(b2);
                           yy = Integer.parseInt(b3);

                           birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                           if (yy < 1900) {                             // check for invalid date

                              birth = 0;
                           }

                        } else {            // try 'Jan 20, 1951' format

                           tok = new StringTokenizer( temp, ", " );          // delimiters are comma and space

                           if ( tok.countTokens() > 2 ) {

                              b1 = tok.nextToken();
                              b2 = tok.nextToken();
                              b3 = tok.nextToken();

                              if (b1.startsWith( "Jan" )) {
                                 mm = 1;
                              } else {
                               if (b1.startsWith( "Feb" )) {
                                  mm = 2;
                               } else {
                                if (b1.startsWith( "Mar" )) {
                                   mm = 3;
                                } else {
                                 if (b1.startsWith( "Apr" )) {
                                    mm = 4;
                                 } else {
                                  if (b1.startsWith( "May" )) {
                                     mm = 5;
                                  } else {
                                   if (b1.startsWith( "Jun" )) {
                                      mm = 6;
                                   } else {
                                    if (b1.startsWith( "Jul" )) {
                                       mm = 7;
                                    } else {
                                     if (b1.startsWith( "Aug" )) {
                                        mm = 8;
                                     } else {
                                      if (b1.startsWith( "Sep" )) {
                                         mm = 9;
                                      } else {
                                       if (b1.startsWith( "Oct" )) {
                                          mm = 10;
                                       } else {
                                        if (b1.startsWith( "Nov" )) {
                                           mm = 11;
                                        } else {
                                         if (b1.startsWith( "Dec" )) {
                                            mm = 12;
                                         } else {
                                            mm = Integer.parseInt(b1);
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

                              dd = Integer.parseInt(b2);
                              yy = Integer.parseInt(b3);

                              birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                              if (yy < 1900) {                             // check for invalid date

                                 birth = 0;
                              }
                           }
                        }
                     }

                     password = lname;

                     //
                     //  if lname is less than 4 chars, fill with 1's
                     //
                     int length = password.length();

                     while (length < 4) {

                        password = password + "1";
                        length++;
                     }

                     //
                     //  Verify the email addresses
                     //
                     if (!email.equals( "" )) {      // if specified

                        email = email.trim();           // remove spaces

                        FeedBack feedback = (member.isEmailValid(email));

                        if (!feedback.isPositive()) {    // if error

                           email = "";                   // do not use it
                        }
                     }
                     if (!email2.equals( "" )) {      // if specified

                        email2 = email2.trim();           // remove spaces

                        FeedBack feedback = (member.isEmailValid(email2));

                        if (!feedback.isPositive()) {    // if error

                           email2 = "";                   // do not use it
                        }
                     }

                     // if email #1 is empty then assign email #2 to it
                     if (email.equals("")) email = email2;

                     //
                     //  Determine if we should process this record
                     //
                     if (!webid.equals( "" )) {                 // must have a webid

                        mNum = memid;

                        if (mNum.endsWith( "A" ) || mNum.endsWith( "a" ) || mNum.endsWith( "B" ) || mNum.endsWith( "b" ) ||
                            mNum.endsWith( "C" ) || mNum.endsWith( "c" ) || mNum.endsWith( "D" ) || mNum.endsWith( "d" ) ||
                            mNum.endsWith( "E" ) || mNum.endsWith( "e" ) || mNum.endsWith( "F" ) || mNum.endsWith( "f" )) {

                           mNum = stripA(mNum);
                        }

                        posid = mNum;


                        //
                        //  Use a version of the webid for our username value.  We cannot use the mNum for this because the mNum can
                        //  change at any time (when their mship changes).  The webid does not change for members.
                        //
                        memid = "100" + webid;             // use 100xxxx so username will never match any old usernames that were originally
                                                           // based on mNums!!


                        if (gender.equals( "" )) {

                           gender = "M";
                        }
                        if (primary.equals( "" )) {

                           primary = "0";
                        }

                        //
                        //  Determine mtype value
                        //
                        if (gender.equalsIgnoreCase( "M" )) {

                           mtype = "Primary Male";

                           if (primary.equalsIgnoreCase( "1" )) {

                              mtype = "Spouse Male";

                           }

                        } else {

                           mtype = "Primary Female";

                           if (primary.equalsIgnoreCase( "1" )) {

                              mtype = "Spouse Female";

                           }
                        }

                        //
                        //  Determine mship value
                        //
                        work = Integer.parseInt(mNum);       // create int for compares

                        mship = "";

                        if (work < 1000) {              // 0001 - 0999  (see 7xxx below also)

                           mship = "Full Family Golf";

                        } else {

                           if (work < 1500) {        // 1000 - 1499

                              mship = "Individual Golf";

                           } else {

                              if (work < 2000) {        // 1500 - 1999

                                 mship = "Individual Golf Plus";

                              } else {

                                 if (work > 2999 && work < 4000) {        // 3000 - 3999

                                    mship = "Corporate Golf";

                                 } else {

                                    if (work > 3999 && work < 5000) {        // 4000 - 4999

                                       mship = "Sports";

                                    } else {

                                       if (work > 6499 && work < 6600) {        // 6500 - 6599

                                          mship = "Player Development";

                                       } else {

                                          if (work > 7002 && work < 7011) {        // 7003 - 7010

                                             mship = "Harpors Point";

                                             if (work == 7004 || work == 7008 || work == 7009) {   // 7004, 7008 or 7009

                                                mship = "Full Family Golf";
                                             }

                                          } else {

                                             if (work > 8999 && work < 10000) {        // 9000 - 9999

                                                mship = "Employees";
                                                mtype = "Employee";         // override mtype for employees
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }

                        if (mship.equals( "" )) {

                           skip = true;            // skip record if mship not one of above
                           SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, "fourbridges", true);
                        }

                     } else {

                        skip = true;              // skip record if webid not provided
                        SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED", "fourbridges", true);
                     }

                  } else {

                     skip = true;              // skip record if memid or name not provided
                     SystemUtils.logErrorToFile("USERNAME/NAME MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + memid, "fourbridges", true);

                  }                 // end of IF minimum requirements met (memid, etc)



                  //
                  //******************************************************************
                  //  Common processing - add or update the member record
                  //******************************************************************
                  //
                  if (skip == false && found == true && !fname.equals("") && !lname.equals("")) {

                     //
                     //   now determine if we should update an existing record or add the new one
                     //
                     memid_old = "";
                     fname_old = "";
                     lname_old = "";
                     mi_old = "";
                     mship_old = "";
                     mtype_old = "";
                     email_old = "";
                     mNum_old = "";
                     posid_old = "";
                     phone_old = "";
                     phone2_old = "";
                     birth_old = 0;

                     memidChanged = false;
                     changed = false;

                     //
                     //  Truncate the string values to avoid sql error
                     //
                     if (!mi.equals( "" )) {       // if mi specified

                        mi = truncate(mi, 1);           // make sure it is only 1 char
                     }
                     if (!memid.equals( "" )) {

                        memid = truncate(memid, 15);
                     }
                     if (!password.equals( "" )) {

                        password = truncate(password, 15);
                     }
                     if (!lname.equals( "" )) {

                        lname = truncate(lname, 20);
                     }
                     if (!fname.equals( "" )) {

                        fname = truncate(fname, 20);
                     }
                     if (!mship.equals( "" )) {

                        mship = truncate(mship, 30);
                     }
                     if (!mtype.equals( "" )) {

                        mtype = truncate(mtype, 30);
                     }
                     if (!email.equals( "" )) {

                        email = truncate(email, 50);
                     }
                     if (!email2.equals( "" )) {

                        email2 = truncate(email2, 50);
                     }
                     if (!mNum.equals( "" )) {

                        mNum = truncate(mNum, 10);
                     }
                     if (!ghin.equals( "" )) {

                        ghin = truncate(ghin, 16);
                     }
                     if (!bag.equals( "" )) {

                        bag = truncate(bag, 12);
                     }
                     if (!posid.equals( "" )) {

                        posid = truncate(posid, 15);
                     }
                     if (!phone.equals( "" )) {

                        phone = truncate(phone, 24);
                     }
                     if (!phone2.equals( "" )) {

                        phone2 = truncate(phone2, 24);
                     }
                     if (!suffix.equals( "" )) {

                        suffix = truncate(suffix, 4);
                     }


                     pstmt2 = con.prepareStatement (
                              "SELECT * FROM member2b WHERE webid = ?");

                     pstmt2.clearParameters();               // clear the parms
                     pstmt2.setString(1, webid);            // put the parm in stmt
                     rs = pstmt2.executeQuery();            // execute the prepared stmt

                     if(rs.next()) {

                        memid_old = rs.getString("username");
                        lname_old = rs.getString("name_last");
                        fname_old = rs.getString("name_first");
                        mi_old = rs.getString("name_mi");
                        mship_old = rs.getString("m_ship");
                        mtype_old = rs.getString("m_type");
                        email_old = rs.getString("email");
                        mNum_old = rs.getString("memNum");
                        birth_old = rs.getInt("birth");
                        posid_old = rs.getString("posid");
                        phone_old = rs.getString("phone1");
                        phone2_old = rs.getString("phone2");
                     }
                     pstmt2.close();              // close the stmt

                     if (!fname_old.equals( "" )) {            // if member found

                        changed = false;                       // init change indicator

                        memid_new = memid_old;

                        if (!memid.equals( memid_old )) {       // if username has changed

                           memid_new = memid;                   // use new memid
                           changed = true;
                           memidChanged = true;
                        }

                        lname_new = lname_old;

                        if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                           lname_new = lname;         // set value from Flexscape record
                           changed = true;
                        }

                        fname_new = fname_old;

                        fname = fname_old;         // DO NOT change first names

/*
                        if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                           fname_new = fname;         // set value from Flexscape record
                           changed = true;
                        }
*/

                        mi_new = mi_old;

                        if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                           mi_new = mi;         // set value from Flexscape record
                           changed = true;
                        }

                        mship_new = mship_old;

                        if (mship_old.startsWith( "Founding" )) {    // do not change if Founding ....

                           mship = mship_old;

                        } else {

                           if (!mship_old.equals( mship )) {   // if the mship has changed

                              mship_new = mship;         // set value from Flexscape record
                              changed = true;
                           }
                        }

                        mtype_new = mtype_old;

                        if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {

                           mtype_new = mtype;         // set value from Flexscape record
                           changed = true;
                        }

                        birth_new = birth_old;

                        if (birth > 0 && birth != birth_old) {

                           birth_new = birth;         // set value from Flexscape record
                           changed = true;
                        }

                        posid_new = posid_old;

                        if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                           posid_new = posid;         // set value from Flexscape record
                           changed = true;
                        }

                        phone_new = phone_old;

                        if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                           phone_new = phone;         // set value from Flexscape record
                           changed = true;
                        }

                        phone2_new = phone2_old;

                        if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                           phone2_new = phone2;         // set value from Flexscape record
                           changed = true;
                        }

                        email_new = email_old;        // do not change emails
                        email2_new = email2_old;

                        // don't allow both emails to be the same
                        if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";

                        //
                        //  NOTE:  mNums can change for this club!!
                        //
                        //         DO NOT change the webid!!!!!!!!!
                        //
                        mNum_new = mNum_old;

                        if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                           mNum_new = mNum;         // set value from Flexscape record

                           //
                           //  mNum changed - change it for all records that match the old mNum.
                           //                 This is because most dependents are not included in web site roster,
                           //                 but are in our roster.
                           //
                           pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET memNum = ? " +
                           "WHERE memNum = ?");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, mNum_new);
                           pstmt2.setString(2, mNum_old);
                           pstmt2.executeUpdate();

                           pstmt2.close();              // close the stmt
                        }


                        //
                        //  Update our record
                        //
                        if (changed == true) {

                           modCount++;             // count records changed
                        }

                        try {

                           pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                           "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                           "memNum = ?, birth = ?, posid = ?, phone1 = ?, " +
                           "phone2 = ?, inact = 0, last_sync_date = now(), gender = ? " +
                           "WHERE webid = ?");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, memid_new);
                           pstmt2.setString(2, lname_new);
                           pstmt2.setString(3, fname_new);
                           pstmt2.setString(4, mi_new);
                           pstmt2.setString(5, mship_new);
                           pstmt2.setString(6, mtype_new);
                           pstmt2.setString(7, email_new);
                           pstmt2.setString(8, mNum_new);
                           pstmt2.setInt(9, birth_new);
                           pstmt2.setString(10, posid_new);
                           pstmt2.setString(11, phone_new);
                           pstmt2.setString(12, phone2_new);
                           pstmt2.setString(13, gender);
                           pstmt2.setString(14, webid);
                           pstmt2.executeUpdate();

                           pstmt2.close();              // close the stmt

                        }
                        catch (Exception e9) {

                           errorMsg = errorMsg + " Error updating record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e9.getMessage();   // build msg
                           SystemUtils.logError(errorMsg);                                                  // log it
                           errorMsg = "Error in Common_sync.flexSync: ";
                           SystemUtils.logErrorToFile("UPDATE MYSQL ERROR!", "fourbridges", true);
                        }


                        //
                        //  Now, update other tables if the username has changed
                        //
                        if (memidChanged == true) {

                           StringBuffer mem_name = new StringBuffer( fname_new );       // get the new first name

                           if (!mi_new.equals( "" )) {
                              mem_name.append(" " +mi_new);               // new mi
                           }
                           mem_name.append(" " +lname_new);               // new last name

                           String newName = mem_name.toString();          // convert to one string

                           Admin_editmem.updTeecurr(newName, memid_new, memid_old, con);      // update teecurr with new values

                           Admin_editmem.updTeepast(newName, memid_new, memid_old, con);      // update teepast with new values

                           Admin_editmem.updLreqs(newName, memid_new, memid_old, con);        // update lreqs with new values

                           Admin_editmem.updPartner(memid_new, memid_old, con);               // update partner with new values

                           Admin_editmem.updEvents(newName, memid_new, memid_old, con);        // update evntSignUp with new values

                           Admin_editmem.updLessons(newName, memid_new, memid_old, con);       // update the lesson books with new values
                        }


                     } else {

                        //
                        //  New member - first check if name already exists
                        //
                        boolean dup = false;

                        pstmt2 = con.prepareStatement (
                                 "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                        pstmt2.clearParameters();
                        pstmt2.setString(1, lname);
                        pstmt2.setString(2, fname);
                        pstmt2.setString(3, mi);
                        rs = pstmt2.executeQuery();            // execute the prepared stmt

                        if (rs.next()) {

                           dup = true;
                        }
                        pstmt2.close();              // close the stmt

                        if (dup == false) {

                           //
                           //  New member - add it
                           //
                           newCount++;             // count records added

                           try {

                              pstmt2 = con.prepareStatement (
                                 "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                                 "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                                 "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, " +
                                 "webid, last_sync_date, gender) " +
                                 "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,?,now(),?)");

                              pstmt2.clearParameters();        // clear the parms
                              pstmt2.setString(1, memid);        // put the parm in stmt
                              pstmt2.setString(2, password);
                              pstmt2.setString(3, lname);
                              pstmt2.setString(4, fname);
                              pstmt2.setString(5, mi);
                              pstmt2.setString(6, mship);
                              pstmt2.setString(7, mtype);
                              pstmt2.setString(8, email);
                              pstmt2.setFloat(9, c_hcap);
                              pstmt2.setFloat(10, u_hcap);
                              pstmt2.setString(11, mNum);
                              pstmt2.setString(12, ghin);
                              pstmt2.setString(13, bag);
                              pstmt2.setInt(14, birth);
                              pstmt2.setString(15, posid);
                              pstmt2.setString(16, email2);
                              pstmt2.setString(17, phone);
                              pstmt2.setString(18, phone2);
                              pstmt2.setString(19, suffix);
                              pstmt2.setString(20, webid);
                              pstmt2.setString(21, gender);
                              pstmt2.executeUpdate();          // execute the prepared stmt

                              pstmt2.close();              // close the stmt

                           }
                           catch (Exception e8) {

                              errorMsg = errorMsg + " Error adding record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e8.getMessage();   // build msg
                              SystemUtils.logError(errorMsg);                                                  // log it
                              errorMsg = "Error in Common_sync.flexSync: ";
                              SystemUtils.logErrorToFile("INSERT MYSQL ERROR!", "fourbridges", true);
                           }

                        } else {      // Dup name

                   //        errorMsg = errorMsg + " Duplicate Name found, name = " +fname+ " " +lname+ ", record #" +rcount+ " for " +club+ ", line = " +line;   // build msg
                   //        SystemUtils.logError(errorMsg);                                                  // log it
                           errorMsg = "Error in Common_sync.flexSync: ";
                           SystemUtils.logErrorToFile("DUPLICATE NAME!", "fourbridges", true);
                        }
                     }

                  }   // end of IF skip

               }   // end of IF record valid (enough tokens)

            } else {  // end of IF club = fourbridges

               //  Remove the dbl quotes and check for embedded commas

               line = cleanRecord4( line );
               line = cleanRecord2( line );

               rcount++;                          // count the records

               //  parse the line to gather all the info

               StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

               if ( tok.countTokens() > 10 ) {     // enough data ?

                  webid = tok.nextToken();
                  memid = tok.nextToken();
                  tok.nextToken();          // eat this value, not used
                  fname = tok.nextToken();
                  mi = tok.nextToken();
                  lname = tok.nextToken();
                  gender = tok.nextToken();
                  email = tok.nextToken();
                  phone = tok.nextToken();
                  phone2 = tok.nextToken();
                  temp = tok.nextToken();
                  primary = tok.nextToken();
                  mship = tok.nextToken();

                  mNum = "";
                  suffix = "";
                  mtype = "";
                  email2 = "";
                  bag = "";
                  ghin = "";
                  u_hndcp = "";
                  c_hndcp = "";
                  posid = "";
                  mobile = "";
                  active = "";

                  //
                  //  Check for ? (not provided)
                  //
                  if (webid.equals( "?" )) {

                     webid = "";
                  }
                  if (memid.equals( "?" )) {

                     memid = "";
                  }
                  if (fname.equals( "?" )) {

                     fname = "";
                  }
                  if (mi.equals( "?" )) {

                     mi = "";
                  }
                  if (lname.equals( "?" )) {

                     lname = "";
                  }
                  if (mship.equals( "?" )) {

                     mship = "";
                  }
                  if (gender.equals( "?" )) {

                     gender = "";
                  }
                  if (email.equals( "?" )) {

                     email = "";
                  }
                  if (phone.equals( "?" )) {

                     phone = "";
                  }
                  if (phone2.equals( "?" )) {

                     phone2 = "";
                  }
                  if (temp.equals( "?" ) || temp.equals( "0" )) {

                     temp = "";
                  }
                  if (primary.equals( "?" )) {

                     primary = "";
                  }

                  //
                  //  Determine if we should process this record (does it meet the minimum requirements?)
                  //
                  if (!webid.equals( "" ) && !memid.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                     //
                     //  Remove spaces, etc. from name fields
                     //
                     tok = new StringTokenizer( fname, " " );     // delimiters are space

                     fname = tok.nextToken();                     // remove any spaces and middle name

                     if ( tok.countTokens() > 0 ) {

                        mi = tok.nextToken();                     // over-write mi if already there
                     }

                     if (!suffix.equals( "" )) {                     // if suffix provided

                        tok = new StringTokenizer( suffix, " " );     // delimiters are space

                        suffix = tok.nextToken();                     // remove any extra (only use one value)
                     }

                     tok = new StringTokenizer( lname, " " );     // delimiters are space

                     lname = tok.nextToken();                     // remove suffix and spaces

                     if (!suffix.equals( "" )) {                  // if suffix provided

                        lname = lname + "_" + suffix;             // append suffix to last name

                     } else {                                     // sufix after last name ?

                        if ( tok.countTokens() > 0 ) {

                           suffix = tok.nextToken();
                           lname = lname + "_" + suffix;          // append suffix to last name
                        }
                     }

                     //
                     //  Determine the handicaps
                     //
                     u_hcap = -99;                    // indicate no hndcp
                     c_hcap = -99;                    // indicate no c_hndcp


                     //
                     //  convert birth date (mm/dd/yyyy to yyyymmdd)
                     //
                     birth = 0;

                     if (!temp.equals( "" )) {

                        String b1 = "";
                        String b2 = "";
                        String b3 = "";
                        int mm = 0;
                        int dd = 0;
                        int yy = 0;

                        tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                        if ( tok.countTokens() > 2 ) {

                           b1 = tok.nextToken();
                           b2 = tok.nextToken();
                           b3 = tok.nextToken();

                           mm = Integer.parseInt(b1);
                           dd = Integer.parseInt(b2);
                           yy = Integer.parseInt(b3);

                           birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                           if (yy < 1900) {                             // check for invalid date

                              birth = 0;
                           }

                        } else {            // try 'Jan 20, 1951' format

                           tok = new StringTokenizer( temp, ", " );          // delimiters are comma and space

                           if ( tok.countTokens() > 2 ) {

                              b1 = tok.nextToken();
                              b2 = tok.nextToken();
                              b3 = tok.nextToken();

                              if (b1.startsWith( "Jan" )) {
                                 mm = 1;
                              } else {
                               if (b1.startsWith( "Feb" )) {
                                  mm = 2;
                               } else {
                                if (b1.startsWith( "Mar" )) {
                                   mm = 3;
                                } else {
                                 if (b1.startsWith( "Apr" )) {
                                    mm = 4;
                                 } else {
                                  if (b1.startsWith( "May" )) {
                                     mm = 5;
                                  } else {
                                   if (b1.startsWith( "Jun" )) {
                                      mm = 6;
                                   } else {
                                    if (b1.startsWith( "Jul" )) {
                                       mm = 7;
                                    } else {
                                     if (b1.startsWith( "Aug" )) {
                                        mm = 8;
                                     } else {
                                      if (b1.startsWith( "Sep" )) {
                                         mm = 9;
                                      } else {
                                       if (b1.startsWith( "Oct" )) {
                                          mm = 10;
                                       } else {
                                        if (b1.startsWith( "Nov" )) {
                                           mm = 11;
                                        } else {
                                         if (b1.startsWith( "Dec" )) {
                                            mm = 12;
                                         } else {
                                            mm = Integer.parseInt(b1);
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

                              dd = Integer.parseInt(b2);
                              yy = Integer.parseInt(b3);

                              birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                              if (yy < 1900) {                             // check for invalid date

                                 birth = 0;
                              }
                           }
                        }
                     }

                     password = lname;

                     //
                     //  if lname is less than 4 chars, fill with 1's
                     //
                     int length = password.length();

                     while (length < 4) {

                        password = password + "1";
                        length++;
                     }

                     //
                     //  Verify the email addresses
                     //
                     if (!email.equals( "" )) {      // if specified

                        email = email.trim();           // remove spaces

                        FeedBack feedback = (member.isEmailValid(email));

                        if (!feedback.isPositive()) {    // if error

                           email = "";                   // do not use it
                        }
                     }
                     if (!email2.equals( "" )) {      // if specified

                        email2 = email2.trim();           // remove spaces

                        FeedBack feedback = (member.isEmailValid(email2));

                        if (!feedback.isPositive()) {    // if error

                           email2 = "";                   // do not use it
                        }
                     }

                     // if email #1 is empty then assign email #2 to it
                     if (email.equals("")) email = email2;


                     //*********************************************
                     //   Start of club specific processing
                     //*********************************************


                     //******************************************************************
                     //   Ocean Reef - oceanreef
                     //******************************************************************
                     //
                     if (club.equals( "oceanreef" )) {

                         found = true;        // club found
                         
                         //
                         //  Determine if we should process this record
                         //
                         if (!webid.equals( "" )) {                 // must have a webid

                             mNum = memid;
                             //

                             posid = mNum;


                             if (primary.equals( "" )) {

                                 primary = "0";
                             }

                             if (mNum.endsWith("-000")) {
                                 primary = "0";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Primary Female";
                                 } else {
                                     gender = "M";
                                     mtype = "Primary Male";
                                 }
                             } else if (mNum.endsWith("-001")) {
                                 primary = "1";
                                 if (gender.equalsIgnoreCase("M")) {
                                     mtype = "Spouse Male";
                                 } else {
                                     gender = "F";
                                     mtype = "Spouse Female";
                                 }
                             } else if (mNum.endsWith("-002")) {
                                 primary = "2";
                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Family Female";
                                 } else {
                                     gender = "M";
                                     mtype = "Family Male";
                                 }
                             } else {
                                 skip = true;
                                 SystemUtils.logErrorToFile("DEPENDENT MSHIP - SKIPPED - name: " + lname + ", " + fname + " - " + work, "oceanreef", true);
                             }

                             mNum = mNum.substring(0, mNum.length() - 4);

                             if (mship.equals("100") || mship.equals("105") || mship.equals("109") || mship.equals("110") || mship.equals("115") ||
                                     mship.equals("150") || mship.equals("159") || mship.equals("160") || mship.equals("180") || mship.equals("199") ||
                                     mship.equals("300") || mship.equals("360")) {
                                 mship = "Social";
                             } else if (mship.equals("101")) {
                                 mship = "Multi-Game Card";
                             } else if (mship.equals("102")) {
                                 mship = "Summer Option";
                             } else if (mship.equals("130")) {
                                 mship = "Social Legacy";
                             } else if (mship.equals("400") || mship.equals("450") || mship.equals("460") || mship.equals("480")) {
                                 mship = "Charter";
                             } else if (mship.equals("420") || mship.equals("430")) {
                                 mship = "Charter Legacy";
                             } else if (mship.equals("401")) {
                                 mship = "Charter w/Trail Pass";
                             } else if (mship.equals("500") || mship.equals("540") || mship.equals("580")) {
                                 mship = "Patron";
                             } else if (mship.equals("520") || mship.equals("530")) {
                                 mship = "Patron Legacy";
                             } else if (mship.equals("800") || mship.equals("801") || mship.equals("860") || mship.equals("880")) {
                                 mship = "Other";
                             } else {
                                 skip = true;
                                 SystemUtils.logErrorToFile("MSHIP NON-GOLF OR UNKNOWN TYPE - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                             }

                             if (mship.equals( "" )) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                             }

                         } else {

                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED", club, true);
                         }
                     }      // end of if oceanreef


                     //******************************************************************
                     //   Colorado Springs CC - coloradospringscountryclub
                     //******************************************************************
                     //
                     if (club.equals( "coloradospringscountryclub" )) {

                         found = true;        // club found

                         //
                         //  Determine if we should process this record
                         //
                         if (mship.equals( "" )) {

                             skip = true;            // skip record if mship not one of above
                             SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                             
                         } else if (webid.equals("")) {

                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED", club, true);
                             
                         } else {

                             while (memid.startsWith("0")) {
                                 memid = memid.substring(1);
                             }

                             mNum = memid;

                             posid = mNum;

                             while (posid.length() < 8) {
                                 posid = "0" + posid;
                             }

                             primary = mNum.substring(mNum.length() - 1);

                             if (mNum.endsWith("-000") || mNum.endsWith("-001")) {

                                 if (gender.equalsIgnoreCase("F")) {
                                     mtype = "Adult Female";
                                 } else {
                                     mtype = "Adult Male";
                                 }

                             } else if (mNum.endsWith("-002") || mNum.endsWith("-003") || mNum.endsWith("-004") || mNum.endsWith("-005") ||
                                        mNum.endsWith("-006") || mNum.endsWith("-007") || mNum.endsWith("-008") || mNum.endsWith("-009")) {

                                 mtype = "Youth";
                             }

                             mNum = mNum.substring(0, mNum.length() - 4);

                             if (mship.equalsIgnoreCase("Recreational") || mship.equalsIgnoreCase("Clubhouse")) {

                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("NON-GOLF MEMBERSHIP TYPE - SKIPPED", club, true);
                             }
                             
                         }
                     }      // end of if coloradospringscountryclub

                     //*********************************************
                     //  End of club specific processing
                     //*********************************************

                  } else {

                      skip = true;              // skip record if memid or name not provided
                      SystemUtils.logErrorToFile("USERNAME/NAME MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + memid, club, true);

                  }                 // end of IF minimum requirements met (memid, etc)



                  //
                  //******************************************************************
                  //  Common processing - add or update the member record
                  //******************************************************************
                  //
                  if (skip == false && found == true && !fname.equals("") && !lname.equals("")) {

                     //
                     //   now determine if we should update an existing record or add the new one
                     //
                     memid_old = "";
                     fname_old = "";
                     lname_old = "";
                     mi_old = "";
                     mship_old = "";
                     mtype_old = "";
                     email_old = "";
                     mNum_old = "";
                     posid_old = "";
                     phone_old = "";
                     phone2_old = "";
                     birth_old = 0;

                     memidChanged = false;
                     changed = false;

                     //
                     //  Truncate the string values to avoid sql error
                     //
                     if (!mi.equals( "" )) {       // if mi specified

                        mi = truncate(mi, 1);           // make sure it is only 1 char
                     }
                     if (!memid.equals( "" )) {

                        memid = truncate(memid, 15);
                     }
                     if (!password.equals( "" )) {

                        password = truncate(password, 15);
                     }
                     if (!lname.equals( "" )) {

                        lname = truncate(lname, 20);
                     }
                     if (!fname.equals( "" )) {

                        fname = truncate(fname, 20);
                     }
                     if (!mship.equals( "" )) {

                        mship = truncate(mship, 30);
                     }
                     if (!mtype.equals( "" )) {

                        mtype = truncate(mtype, 30);
                     }
                     if (!email.equals( "" )) {

                        email = truncate(email, 50);
                     }
                     if (!email2.equals( "" )) {

                        email2 = truncate(email2, 50);
                     }
                     if (!mNum.equals( "" )) {

                        mNum = truncate(mNum, 10);
                     }
                     if (!ghin.equals( "" )) {

                        ghin = truncate(ghin, 16);
                     }
                     if (!bag.equals( "" )) {

                        bag = truncate(bag, 12);
                     }
                     if (!posid.equals( "" )) {

                        posid = truncate(posid, 15);
                     }
                     if (!phone.equals( "" )) {

                        phone = truncate(phone, 24);
                     }
                     if (!phone2.equals( "" )) {

                        phone2 = truncate(phone2, 24);
                     }
                     if (!suffix.equals( "" )) {

                        suffix = truncate(suffix, 4);
                     }


                     pstmt2 = con.prepareStatement (
                              "SELECT * FROM member2b WHERE webid = ?");

                     pstmt2.clearParameters();               // clear the parms
                     pstmt2.setString(1, webid);            // put the parm in stmt
                     rs = pstmt2.executeQuery();            // execute the prepared stmt

                     if(rs.next()) {

                        memid_old = rs.getString("username");
                        lname_old = rs.getString("name_last");
                        fname_old = rs.getString("name_first");
                        mi_old = rs.getString("name_mi");
                        mship_old = rs.getString("m_ship");
                        mtype_old = rs.getString("m_type");
                        email_old = rs.getString("email");
                        mNum_old = rs.getString("memNum");
                        birth_old = rs.getInt("birth");
                        posid_old = rs.getString("posid");
                        phone_old = rs.getString("phone1");
                        phone2_old = rs.getString("phone2");
                     }
                     pstmt2.close();              // close the stmt

                     if (!fname_old.equals( "" )) {            // if member found

                        changed = false;                       // init change indicator

                        memid_new = memid_old;

                        if (!memid.equals( memid_old )) {       // if username has changed

                           memid_new = memid;                   // use new memid
                           changed = true;
                           memidChanged = true;
                        }

                        lname_new = lname_old;

                        if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                           lname_new = lname;         // set value from Flexscape record
                           changed = true;
                        }

                        fname_new = fname_old;

                        fname = fname_old;         // DO NOT change first names

/*
                        if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                           fname_new = fname;         // set value from Flexscape record
                           changed = true;
                        }
*/
                        mi_new = mi_old;

                        if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                           mi_new = mi;         // set value from Flexscape record
                           changed = true;
                        }

                        mship_new = mship_old;

                        if (!mship_old.equals( mship )) {   // if the mship has changed

                            mship_new = mship;         // set value from Flexscape record
                            changed = true;
                        }

                        mtype_new = mtype_old;

                        if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {
                            mtype_new = mtype;         // set value from Flexscape record
                            changed = true;
                        }

                        if (birth > 0 && birth != birth_old) {

                           birth_new = birth;         // set value from Flexscape record
                           changed = true;
                        }

                        posid_new = posid_old;

                        if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                           posid_new = posid;         // set value from Flexscape record
                           changed = true;
                        }

                        phone_new = phone_old;

                        if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                           phone_new = phone;         // set value from Flexscape record
                           changed = true;
                        }

                        phone2_new = phone2_old;

                        if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                           phone2_new = phone2;         // set value from Flexscape record
                           changed = true;
                        }

                        email_new = email_old;        // do not change emails
                        email2_new = email2_old;

                        // don't allow both emails to be the same
                        if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";

                        //
                        //  NOTE:  mNums can change for this club!!
                        //
                        //         DO NOT change the webid!!!!!!!!!
                        //
                        mNum_new = mNum_old;

                        if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                           mNum_new = mNum;         // set value from Flexscape record

                           //
                           //  mNum changed - change it for all records that match the old mNum.
                           //                 This is because most dependents are not included in web site roster,
                           //                 but are in our roster.
                           //
                           pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET memNum = ? " +
                           "WHERE memNum = ?");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, mNum_new);
                           pstmt2.setString(2, mNum_old);
                           pstmt2.executeUpdate();

                           pstmt2.close();              // close the stmt
                        }


                        //
                        //  Update our record
                        //
                        if (changed == true) {

                           modCount++;             // count records changed
                        }

                        try {

                           pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                           "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                           "memNum = ?, birth = ?, posid = ?, phone1 = ?, " +
                           "phone2 = ?, inact = 0, last_sync_date = now(), gender = ? " +
                           "WHERE webid = ?");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, memid_new);
                           pstmt2.setString(2, lname_new);
                           pstmt2.setString(3, fname_new);
                           pstmt2.setString(4, mi_new);
                           pstmt2.setString(5, mship_new);
                           pstmt2.setString(6, mtype_new);
                           pstmt2.setString(7, email_new);
                           pstmt2.setString(8, mNum_new);
                           pstmt2.setInt(9, birth_new);
                           pstmt2.setString(10, posid_new);
                           pstmt2.setString(11, phone_new);
                           pstmt2.setString(12, phone2_new);
                           pstmt2.setString(13, gender);
                           pstmt2.setString(14, webid);
                           pstmt2.executeUpdate();

                           pstmt2.close();              // close the stmt

                        }
                        catch (Exception e9) {

                           errorMsg = errorMsg + " Error updating record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e9.getMessage();   // build msg
                           SystemUtils.logError(errorMsg);                                                  // log it
                           errorMsg = "Error in Common_sync.flexSync: ";
                           SystemUtils.logErrorToFile("UPDATE MYSQL ERROR!", club, true);
                        }


                        //
                        //  Now, update other tables if the username has changed
                        //
                        if (memidChanged == true) {

                           StringBuffer mem_name = new StringBuffer( fname_new );       // get the new first name

                           if (!mi_new.equals( "" )) {
                              mem_name.append(" " +mi_new);               // new mi
                           }
                           mem_name.append(" " +lname_new);               // new last name

                           String newName = mem_name.toString();          // convert to one string

                           Admin_editmem.updTeecurr(newName, memid_new, memid_old, con);      // update teecurr with new values

                           Admin_editmem.updTeepast(newName, memid_new, memid_old, con);      // update teepast with new values

                           Admin_editmem.updLreqs(newName, memid_new, memid_old, con);        // update lreqs with new values

                           Admin_editmem.updPartner(memid_new, memid_old, con);        // update partner with new values

                           Admin_editmem.updEvents(newName, memid_new, memid_old, con);        // update evntSignUp with new values

                           Admin_editmem.updLessons(newName, memid_new, memid_old, con);       // update the lesson books with new values
                        }


                     } else {

                        //
                        //  New member - first check if name already exists
                        //
                        boolean dup = false;

                        pstmt2 = con.prepareStatement (
                                 "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                        pstmt2.clearParameters();
                        pstmt2.setString(1, lname);
                        pstmt2.setString(2, fname);
                        pstmt2.setString(3, mi);
                        rs = pstmt2.executeQuery();            // execute the prepared stmt

                        if (rs.next()) {

                           dup = true;
                        }
                        pstmt2.close();              // close the stmt

                        if (dup == false) {

                           //
                           //  New member - add it
                           //
                           newCount++;             // count records added

                           try {

                              pstmt2 = con.prepareStatement (
                                 "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                                 "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                                 "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, " +
                                 "webid, last_sync_date, gender) " +
                                 "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,?,now(),?)");

                              pstmt2.clearParameters();        // clear the parms
                              pstmt2.setString(1, memid);        // put the parm in stmt
                              pstmt2.setString(2, password);
                              pstmt2.setString(3, lname);
                              pstmt2.setString(4, fname);
                              pstmt2.setString(5, mi);
                              pstmt2.setString(6, mship);
                              pstmt2.setString(7, mtype);
                              pstmt2.setString(8, email);
                              pstmt2.setFloat(9, c_hcap);
                              pstmt2.setFloat(10, u_hcap);
                              pstmt2.setString(11, mNum);
                              pstmt2.setString(12, ghin);
                              pstmt2.setString(13, bag);
                              pstmt2.setInt(14, birth);
                              pstmt2.setString(15, posid);
                              pstmt2.setString(16, email2);
                              pstmt2.setString(17, phone);
                              pstmt2.setString(18, phone2);
                              pstmt2.setString(19, suffix);
                              pstmt2.setString(20, webid);
                              pstmt2.setString(21, gender);
                              pstmt2.executeUpdate();          // execute the prepared stmt

                              pstmt2.close();              // close the stmt

                           }
                           catch (Exception e8) {

                              errorMsg = errorMsg + " Error adding record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e8.getMessage();   // build msg
                              SystemUtils.logError(errorMsg);                                                  // log it
                              errorMsg = "Error in Common_sync.flexSync: ";
                              SystemUtils.logErrorToFile("INSERT MYSQL ERROR! - SKIPPED - name: " + lname + ", " + fname + " - " + memid, club, true);
                           }

                        } else {      // Dup name

                   //        errorMsg = errorMsg + " Duplicate Name found, name = " +fname+ " " +lname+ ", record #" +rcount+ " for " +club+ ", line = " +line;   // build msg
                   //        SystemUtils.logError(errorMsg);                                                  // log it
                           errorMsg = "Error in Common_sync.flexSync: ";
                           SystemUtils.logErrorToFile("DUPLICATE NAME! - SKIPPED - name: " + lname + ", " + fname + " - " + memid, club, true);
                        }
                     }
                  }   // end of IF skip
               }   // end of IF record valid (enough tokens)
            }

/*
            //        FIX the mship types and have them add gender!!!!!!!!!!!!!!!!!!!!!!!!
            //
            //  Scitot Reserve
            //
            if (club.equals( "sciotoreserve" )) {

               found = true;        // club found

               //  Remove the dbl quotes and check for embedded commas

               line = cleanRecord2( line );

               rcount++;                          // count the records

               //  parse the line to gather all the info

               StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

               if ( tok.countTokens() > 10 ) {     // enough data ?

                  webid = tok.nextToken();
                  memid = tok.nextToken();
                  fname = tok.nextToken();
                  mi = tok.nextToken();
                  lname = tok.nextToken();
                  gender = tok.nextToken();
                  email = tok.nextToken();
                  phone = tok.nextToken();
                  phone2 = tok.nextToken();
                  temp = tok.nextToken();
                  primary = tok.nextToken();

                  mNum = "";
                  suffix = "";
                  mship = "";
                  mtype = "";
                  email2 = "";
                  bag = "";
                  ghin = "";
                  u_hndcp = "";
                  c_hndcp = "";
                  posid = "";
                  mobile = "";
                  active = "";

                  //
                  //  Check for ? (not provided)
                  //
                  if (memid.equals( "?" )) {

                     memid = "";
                  }
                  if (fname.equals( "?" )) {

                     fname = "";
                  }
                  if (mi.equals( "?" )) {

                     mi = "";
                  }
                  if (lname.equals( "?" )) {

                     lname = "";
                  }
                  if (gender.equals( "?" )) {

                     gender = "";
                  }
                  if (email.equals( "?" )) {

                     email = "";
                  }
                  if (phone.equals( "?" )) {

                     phone = "";
                  }
                  if (phone2.equals( "?" )) {

                     phone2 = "";
                  }
                  if (temp.equals( "?" ) || temp.equals( "0" )) {

                     temp = "";
                  }
                  if (primary.equals( "?" )) {

                     primary = "";
                  }

                  //
                  //  Determine if we should process this record (does it meet the minimum requirements?)
                  //
                  if (!memid.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                     //
                     //  Remove spaces, etc. from name fields
                     //
                     tok = new StringTokenizer( fname, " " );     // delimiters are space

                     fname = tok.nextToken();                     // remove any spaces and middle name

                     if ( tok.countTokens() > 0 ) {

                        mi = tok.nextToken();                     // over-write mi if already there
                     }

                     if (!suffix.equals( "" )) {                     // if suffix provided

                        tok = new StringTokenizer( suffix, " " );     // delimiters are space

                        suffix = tok.nextToken();                     // remove any extra (only use one value)
                     }

                     tok = new StringTokenizer( lname, " " );     // delimiters are space

                     lname = tok.nextToken();                     // remove suffix and spaces

                     if (!suffix.equals( "" )) {                  // if suffix provided

                        lname = lname + "_" + suffix;             // append suffix to last name

                     } else {                                     // sufix after last name ?

                        if ( tok.countTokens() > 0 ) {

                           suffix = tok.nextToken();
                           lname = lname + "_" + suffix;          // append suffix to last name
                        }
                     }

                     //
                     //  Determine the handicaps
                     //
                     u_hcap = -99;                    // indicate no hndcp
                     c_hcap = -99;                    // indicate no c_hndcp


                     //
                     //  convert birth date (mm/dd/yyyy to yyyymmdd)
                     //
                     if (!temp.equals( "" )) {

                        tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                        String b1 = tok.nextToken();
                        String b2 = tok.nextToken();
                        String b3 = tok.nextToken();

                        int mm = Integer.parseInt(b1);
                        int dd = Integer.parseInt(b2);
                        int yy = Integer.parseInt(b3);

                        birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                        if (yy < 1900) {                             // check for invalid date

                           birth = 0;
                        }

                     } else {

                        birth = 0;
                     }

                     password = lname;

                     //
                     //  if lname is less than 4 chars, fill with 1's
                     //
                     int length = password.length();

                     while (length < 4) {

                        password = password + "1";
                        length++;
                     }

                     //
                     //  Verify the email addresses
                     //
                     if (!email.equals( "" )) {      // if specified

                        email = email.trim();           // remove spaces

                        FeedBack feedback = (member.isEmailValid(email));

                        if (!feedback.isPositive()) {    // if error

                           email = "";                   // do not use it
                        }
                     }
                     if (!email2.equals( "" )) {      // if specified

                        email2 = email2.trim();           // remove spaces

                        FeedBack feedback = (member.isEmailValid(email2));

                        if (!feedback.isPositive()) {    // if error

                           email2 = "";                   // do not use it
                        }
                     }

                     // if email #1 is empty then assign email #2 to it
                     if (email.equals("")) email = email2;

                     //
                     //  Determine if we should process this record
                     //
                     if (!webid.equals( "" )) {                 // must have a webid

                        mNum = memid;

                        posid = memid;

                        //
                        //  Strip any leading zeros and extension from mNum
                        //
                        while (mNum.startsWith( "0" )) {    // if starts with a zero

                           mNum = remZeroS(mNum);           // remove the leading zero
                        }

                        memid = mNum;                     // use mNum for username !!


                        if (gender.equals( "" )) {

                           gender = "M";
                        }
                        if (primary.equals( "" )) {

                           primary = "0";
                        }

                        //
                        //  Determine mtype value
                        //
                        if (gender.equalsIgnoreCase( "M" )) {

                           if (primary.equalsIgnoreCase( "0" )) {

                              mtype = "Main Male";

                           } else {

                              if (primary.equalsIgnoreCase( "1" )) {

                                 mtype = "Spouse Male";

                              } else {

                                 mtype = "Junior Male";
                              }
                           }

                        } else {         // Female

                           if (primary.equalsIgnoreCase( "0" )) {

                              mtype = "Main Female";

                           } else {

                              if (primary.equalsIgnoreCase( "1" )) {

                                 mtype = "Spouse Female";

                              } else {

                                 mtype = "Junior Female";
                              }
                           }
                        }

                        //
                        //  Determine mship value  ?????????????????
                        //
                        work = Integer.parseInt(mNum);       // create int for compares

                        mship = "";

                        if (work < 1000) {              // 0001 - 0999  (see 7xxx below also)

                           mship = "Full Family Golf";

                        } else {

                           if (work < 1500) {        // 1000 - 1499

                              mship = "Individual Golf";

                           } else {

                              if (work < 2000) {        // 1500 - 1999

                                 mship = "Individual Golf Plus";

                              } else {

                                 if (work > 2999 && work < 4000) {        // 3000 - 3999

                                    mship = "Corporate Golf";

                                 } else {

                                    if (work > 3999 && work < 5000) {        // 4000 - 4999

                                       mship = "Sports";

                                    } else {

                                       if (work > 6499 && work < 6600) {        // 6500 - 6599

                                          mship = "Player Development";

                                       } else {

                                          if (work > 7002 && work < 7011) {        // 7003 - 7010

                                             mship = "Harpors Point";

                                             if (work == 7004 || work == 7008 || work == 7009) {   // 7004, 7008 or 7009

                                                mship = "Full Family Golf";
                                             }

                                          } else {

                                             if (work > 8999 && work < 10000) {        // 9000 - 9999

                                                mship = "Employees";
                                                mtype = "Employee";         // override mtype for employees
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }

                        if (mship.equals( "" )) {

                           skip = true;            // skip record if mship not one of above
                        }

                     } else {

                        skip = true;              // skip record if webid not provided
                     }

                  } else {

                     skip = true;              // skip record if memid or name not provided

                  }                 // end of IF minimum requirements met (memid, etc)



                  //
                  //******************************************************************
                  //  Common processing - add or update the member record
                  //******************************************************************
                  //
                  if (skip == false && found == true && !fname.equals("") && !lname.equals("")) {

                     //
                     //   now determine if we should update an existing record or add the new one
                     //
                     fname_old = "";
                     lname_old = "";
                     mi_old = "";
                     mship_old = "";
                     mtype_old = "";
                     email_old = "";
                     mNum_old = "";
                     posid_old = "";
                     phone_old = "";
                     phone2_old = "";
                     birth_old = 0;


                     //
                     //  Truncate the string values to avoid sql error
                     //
                     if (!mi.equals( "" )) {       // if mi specified

                        mi = truncate(mi, 1);           // make sure it is only 1 char
                     }
                     if (!memid.equals( "" )) {

                        memid = truncate(memid, 15);
                     }
                     if (!password.equals( "" )) {

                        password = truncate(password, 15);
                     }
                     if (!lname.equals( "" )) {

                        lname = truncate(lname, 20);
                     }
                     if (!fname.equals( "" )) {

                        fname = truncate(fname, 20);
                     }
                     if (!mship.equals( "" )) {

                        mship = truncate(mship, 30);
                     }
                     if (!mtype.equals( "" )) {

                        mtype = truncate(mtype, 30);
                     }
                     if (!email.equals( "" )) {

                        email = truncate(email, 50);
                     }
                     if (!email2.equals( "" )) {

                        email2 = truncate(email2, 50);
                     }
                     if (!mNum.equals( "" )) {

                        mNum = truncate(mNum, 10);
                     }
                     if (!ghin.equals( "" )) {

                        ghin = truncate(ghin, 16);
                     }
                     if (!bag.equals( "" )) {

                        bag = truncate(bag, 12);
                     }
                     if (!posid.equals( "" )) {

                        posid = truncate(posid, 15);
                     }
                     if (!phone.equals( "" )) {

                        phone = truncate(phone, 24);
                     }
                     if (!phone2.equals( "" )) {

                        phone2 = truncate(phone2, 24);
                     }
                     if (!suffix.equals( "" )) {

                        suffix = truncate(suffix, 4);
                     }


                     pstmt2 = con.prepareStatement (
                              "SELECT * FROM member2b WHERE webid = ?");

                     pstmt2.clearParameters();               // clear the parms
                     pstmt2.setString(1, webid);            // put the parm in stmt
                     rs = pstmt2.executeQuery();            // execute the prepared stmt

                     if(rs.next()) {

                        lname_old = rs.getString("name_last");
                        fname_old = rs.getString("name_first");
                        mi_old = rs.getString("name_mi");
                        mship_old = rs.getString("m_ship");
                        mtype_old = rs.getString("m_type");
                        email_old = rs.getString("email");
                        mNum_old = rs.getString("memNum");
                        birth_old = rs.getInt("birth");
                        posid_old = rs.getString("posid");
                        phone_old = rs.getString("phone1");
                        phone2_old = rs.getString("phone2");
                     }
                     pstmt2.close();              // close the stmt

                     if (!fname_old.equals( "" )) {            // if member found

                        changed = false;                       // init change indicator

                        lname_new = lname_old;

                        if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                           lname_new = lname;         // set value from Flexscape record
                           changed = true;
                        }

                        fname_new = fname_old;

                        fname = fname_old;         // DO NOT change first names

                        if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                           fname_new = fname;         // set value from Flexscape record
                           changed = true;
                        }

                        mi_new = mi_old;

                        if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                           mi_new = mi;         // set value from Flexscape record
                           changed = true;
                        }

                        mship_new = mship_old;

                        if (mship_old.startsWith( "Founding" )) {    // do not change if Founding ....

                           mship = mship_old;

                        } else {

                           if (!mship_old.equals( mship )) {   // if the mship has changed

                              mship_new = mship;         // set value from Flexscape record
                              changed = true;
                           }
                        }

                        mtype_new = mtype_old;

                        if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {

                           mtype_new = mtype;         // set value from Flexscape record
                           changed = true;
                        }

                        birth_new = birth_old;

                        if (birth > 0 && birth != birth_old) {

                           birth_new = birth;         // set value from Flexscape record
                           changed = true;
                        }

                        posid_new = posid_old;

                        if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                           posid_new = posid;         // set value from Flexscape record
                           changed = true;
                        }

                        phone_new = phone_old;

                        if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                           phone_new = phone;         // set value from Flexscape record
                           changed = true;
                        }

                        phone2_new = phone2_old;

                        if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                           phone2_new = phone2;         // set value from Flexscape record
                           changed = true;
                        }

                        email_new = email_old;        // do not change emails
                        email2_new = email2_old;

                        // don't allow both emails to be the same
                        if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";

                        //
                        //  NOTE:  mNums can change for this club!!  This will also result in a different memid!!
                        //
                        //         DO NOT change the memid or webid!!!!!!!!!
                        //
                        mNum_new = mNum_old;

                        if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                           mNum_new = mNum;         // set value from Flexscape record

                           //
                           //  mNum changed - change it for all records that match the old mNum.
                           //                 This is because most dependents are not included in web site roster,
                           //                 but are in our roster.
                           //
                           pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET memNum = ? " +
                           "WHERE memNum = ?");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, mNum_new);
                           pstmt2.setString(2, mNum_old);
                           pstmt2.executeUpdate();

                           pstmt2.close();              // close the stmt
                        }


                        //
                        //  Update our record
                        //
                        if (changed == true) {

                           modCount++;             // count records changed
                        }

                        pstmt2 = con.prepareStatement (
                        "UPDATE member2b SET name_last = ?, name_first = ?, " +
                        "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                        "memNum = ?, birth = ?, posid = ?, phone1 = ?, " +
                        "phone2 = ?, inact = 0, last_sync_date = now() " +
                        "WHERE webid = ?");

                        pstmt2.clearParameters();        // clear the parms
                        pstmt2.setString(1, lname_new);
                        pstmt2.setString(2, fname_new);
                        pstmt2.setString(3, mi_new);
                        pstmt2.setString(4, mship_new);
                        pstmt2.setString(5, mtype_new);
                        pstmt2.setString(6, email_new);
                        pstmt2.setString(7, mNum_new);
                        pstmt2.setInt(8, birth_new);
                        pstmt2.setString(9, posid_new);
                        pstmt2.setString(10, phone_new);
                        pstmt2.setString(11, phone2_new);
                        pstmt2.setString(12, webid);
                        pstmt2.executeUpdate();

                        pstmt2.close();              // close the stmt

                     } else {

                        //
                        //  New member - first check if name already exists
                        //
                        boolean dup = false;

                        pstmt2 = con.prepareStatement (
                                 "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                        pstmt2.clearParameters();
                        pstmt2.setString(1, lname);
                        pstmt2.setString(2, fname);
                        pstmt2.setString(3, mi);
                        rs = pstmt2.executeQuery();            // execute the prepared stmt

                        if (rs.next()) {

                           dup = true;
                        }
                        pstmt2.close();              // close the stmt

                        if (dup == false) {

                           //
                           //  New member - add it
                           //
                           newCount++;             // count records added

                           pstmt2 = con.prepareStatement (
                              "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                              "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                              "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, webid, " +
                              "last_sync_date) " +
                              "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,?,now())");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, memid);        // put the parm in stmt
                           pstmt2.setString(2, password);
                           pstmt2.setString(3, lname);
                           pstmt2.setString(4, fname);
                           pstmt2.setString(5, mi);
                           pstmt2.setString(6, mship);
                           pstmt2.setString(7, mtype);
                           pstmt2.setString(8, email);
                           pstmt2.setFloat(9, c_hcap);
                           pstmt2.setFloat(10, u_hcap);
                           pstmt2.setString(11, mNum);
                           pstmt2.setString(12, ghin);
                           pstmt2.setString(13, bag);
                           pstmt2.setInt(14, birth);
                           pstmt2.setString(15, posid);
                           pstmt2.setString(16, email2);
                           pstmt2.setString(17, phone);
                           pstmt2.setString(18, phone2);
                           pstmt2.setString(19, suffix);
                           pstmt2.setString(20, webid);
                           pstmt2.executeUpdate();          // execute the prepared stmt

                           pstmt2.close();              // close the stmt
                        }
                     }

                  }   // end of IF skip

               }   // end of IF record valid (enough tokens)

            }   // end of IF club = ????
*/

         }   // end of IF header row

      }   // end of while

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {       // if we processed this club

          // Set anyone inactive that didn't sync, but has at one point. (Not sure why this was turned off for all flexscape clubs, fourbridges requested it be turned back on.
          if (club.equals("fourbridges") || club.equals("coloradospringscountryclub")) {
              pstmt2 = con.prepareStatement (
                      "UPDATE member2b SET inact = 1 " +
                      "WHERE last_sync_date != now() AND last_sync_date != '0000-00-00'");

              pstmt2.clearParameters();        // clear the parms
              pstmt2.executeUpdate();

              pstmt2.close();              // close the stmt
              
              
              //
              //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
              //
              setRSind(con, club);
          }
      }

   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster (record #" +rcount+ ") for " +club+ ", line = " +line+ ": " + e3.getMessage();   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
      errorMsg = "Error in Common_sync.flexSync: ";
   }

 }



 //
 //   Method to process files from Dream World system
 //
 private static void dreamworldSync(Connection con, InputStreamReader isr, String club) {

   PreparedStatement pstmt2 = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;

   // Values from Flexscape records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String webid = "";
   String mNum = "";
   String mship = "";
   String mtype = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String primary = "";

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;

   // Values from ForeTees records
   //
   String memid_old = "";
   String email_old = "";

   int birth_old = 0;

   // Values for New ForeTees records
   //
   String memid_new = "";
   String skipField = "";

   int birth_new = 0;
   int rcount = 0;
   int newCount = 0;
   int modCount = 0;
   int work = 0;
   int pri_indicator = 0;
   int email_bounce1 = 0;
   int email_bounce2 = 0;

   String errorMsg = "Error in Common_sync.dreamworldSync: ";

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean headerFound = false;
   boolean found = false;
   boolean memidChanged = false;



   try {

      BufferedReader br = new BufferedReader(isr);

      while (true) {

         line = br.readLine();

         if (line == null) {
            break;
         }

         //  Skip the 1st row (header row)

         if (headerFound == false) {

            headerFound = true;

         } else {

            skip = false;
            found = false;        // default to club NOT found

            //
            // *********************************************************************
            //
            //   The following will be dependent on the club - customized
            //
            // *********************************************************************
            //
            if (club.equals( "bracketts" )) {        // Bracketts Crossing

               found = true;        // club found

               //  Remove the dbl quotes and check for embedded commas

               line = cleanRecord2( line );
          //     line = cleanRecord3( line );

               rcount++;                          // count the records

               //  parse the line to gather all the info

               StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

               if ( tok.countTokens() > 7 ) {     // enough data ?

                  webid = tok.nextToken();
                  memid = tok.nextToken();
                  primary = tok.nextToken();
                  fname = tok.nextToken();
                  mi = tok.nextToken();
                  lname = tok.nextToken();
                  suffix = tok.nextToken();
                  mship = tok.nextToken();

                  gender = "";
                  email = "";
                  phone = "";
                  mNum = "";
                  mtype = "";
                  email2 = "";
                  bag = "";
                  ghin = "";
                  posid = "";
                  phone2 = "";
                  temp = "";

                  if ( tok.countTokens() > 0 ) {

                     gender = tok.nextToken();
                  }
                  if ( tok.countTokens() > 0 ) {

                     skipField = tok.nextToken();          // skip address fields
                  }
                  if ( tok.countTokens() > 0 ) {

                     skipField = tok.nextToken();
                  }
                  if ( tok.countTokens() > 0 ) {

                     skipField = tok.nextToken();
                  }
                  if ( tok.countTokens() > 0 ) {

                     skipField = tok.nextToken();
                  }
                  if ( tok.countTokens() > 0 ) {

                     skipField = tok.nextToken();
                  }
                  if ( tok.countTokens() > 0 ) {

                     email = tok.nextToken();
                  }
                  if ( tok.countTokens() > 0 ) {

                     phone = tok.nextToken();
                  }


                  //
                  //  Check for ? (not provided)
                  //
                  if (webid.equals( "?" )) {

                     webid = "";
                  }
                  if (memid.equals( "?" )) {

                     memid = "";
                  }
                  if (primary.equals( "?" )) {

                     primary = "";
                  }
                  if (fname.equals( "?" )) {

                     fname = "";
                  }
                  if (mi.equals( "?" )) {

                     mi = "";
                  }
                  if (lname.equals( "?" )) {

                     lname = "";
                  }
                  if (suffix.equals( "?" )) {

                     suffix = "";
                  }
                  if (mship.equals( "?" )) {

                     mship = "";
                  }
                  if (gender.equals( "?" )) {

                     gender = "";
                  }
                  if (email.equals( "?" )) {

                     email = "";
                  }
                  if (phone.equals( "?" )) {

                     phone = "";
                  }

                  //
                  //  Determine if we should process this record (does it meet the minimum requirements?)
                  //
                  if (!webid.equals( "" ) && !memid.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" ) && !mship.equals( "" )) {

                     //
                     //  Remove spaces, etc. from name fields
                     //
                     tok = new StringTokenizer( fname, " " );     // delimiters are space

                     fname = tok.nextToken();                     // remove any spaces and middle name

                     if ( tok.countTokens() > 0 ) {

                        mi = tok.nextToken();                     // over-write mi if already there
                     }

                     if (!suffix.equals( "" )) {                     // if suffix provided

                        tok = new StringTokenizer( suffix, " " );     // delimiters are space

                        suffix = tok.nextToken();                     // remove any extra (only use one value)
                     }

                     tok = new StringTokenizer( lname, " " );     // delimiters are space

                     lname = tok.nextToken();                     // remove suffix and spaces

                     if (!suffix.equals( "" )) {                  // if suffix provided

                        lname = lname + "_" + suffix;             // append suffix to last name

                     } else {                                     // sufix after last name ?

                        if ( tok.countTokens() > 0 ) {

                           suffix = tok.nextToken();
                           lname = lname + "_" + suffix;          // append suffix to last name
                        }
                     }

                     //
                     //  Determine the handicaps
                     //
                     u_hcap = -99;                    // indicate no hndcp
                     c_hcap = -99;                    // indicate no c_hndcp

                     birth = 0;

                     password = lname;

                     //
                     //  if lname is less than 4 chars, fill with 1's
                     //
                     int length = password.length();

                     while (length < 4) {

                        password = password + "1";
                        length++;
                     }

                     //
                     //  Verify the email addresses
                     //
                     if (!email.equals( "" )) {      // if specified

                        email = email.trim();           // remove spaces

                        FeedBack feedback = (member.isEmailValid(email));

                        if (!feedback.isPositive()) {    // if error

                           email = "";                   // do not use it
                        }
                     }
                     if (!email2.equals( "" )) {      // if specified

                        email2 = email2.trim();           // remove spaces

                        FeedBack feedback = (member.isEmailValid(email2));

                        if (!feedback.isPositive()) {    // if error

                           email2 = "";                   // do not use it
                        }
                     }

                     // if email #1 is empty then assign email #2 to it
                     if (email.equals("")) email = email2;

                     //
                     //  Determine if we should process this record
                     //
                     if (!webid.equals( "" )) {                 // must have a webid

                        mNum = memid;

                        posid = mNum + "-000";

                        memid = mNum;            // use mNum for primary and add alpha for others

                        if (gender.equals( "" )) {

                           gender = "1";
                        }
                        if (primary.equals( "" )) {

                           primary = "0";
                        }

                        if (primary.endsWith( "0" )) {        // convert from 000 format to 0

                           primary = "0";
                        }
                        if (primary.endsWith( "1" )) {

                           primary = "1";
                           memid = memid + "a";
                        }
                        if (primary.endsWith( "2" )) {

                           primary = "2";
                           memid = memid + "b";
                        }
                        if (primary.endsWith( "3" )) {

                           primary = "3";
                           memid = memid + "c";
                        }
                        if (primary.endsWith( "4" )) {

                           primary = "4";
                           memid = memid + "d";
                        }
                        if (primary.endsWith( "5" )) {

                           primary = "5";
                           memid = memid + "e";
                        }
                        if (primary.endsWith( "6" )) {

                           primary = "6";
                           memid = memid + "f";
                        }
                        if (primary.endsWith( "7" )) {

                           primary = "7";
                           memid = memid + "g";
                        }
                        if (primary.endsWith( "8" )) {

                           primary = "8";
                           memid = memid + "h";
                        }
                        if (primary.endsWith( "9" )) {

                           primary = "9";
                           memid = memid + "i";
                        }

                        //
                        //  Determine mtype value
                        //
                        if (primary.equalsIgnoreCase( "0" )) {

                           pri_indicator = 0;

                           if (gender.equalsIgnoreCase( "1" )) {

                              gender = "M";
                              mtype = "Member Male";

                           } else {

                              gender = "F";
                              mtype = "Member Female";
                           }

                        } else {

                           if (primary.equalsIgnoreCase( "1" )) {

                              pri_indicator = 1;

                              if (gender.equalsIgnoreCase( "1" )) {

                                 gender = "M";
                                 mtype = "Spouse Male";

                              } else {

                                 gender = "F";
                                 mtype = "Spouse Female";
                              }

                           } else {                // Dependents

                              pri_indicator = 2;

                              if (gender.equalsIgnoreCase( "1" )) {

                                 gender = "M";
                                 mtype = "Dependent Male";

                              } else {

                                 gender = "F";
                                 mtype = "Dependent Female";
                              }
                           }
                        }

                     } else {

                        skip = true;              // skip record if webid not provided
                     }

                  } else {

                     skip = true;              // skip record if memid or name not provided

                     errorMsg = errorMsg + " Required Field Missing, name = " +fname+ " " +lname+ ", webid" +webid+ ", record #" +rcount+ " for " +club+ ", line = " +line;   // build msg
                     SystemUtils.logError(errorMsg);                                                  // log it
                     errorMsg = "Error in Common_sync.dreamworldSync: ";

                  }                 // end of IF minimum requirements met (memid, etc)



                  //
                  //******************************************************************
                  //  Common processing - add or update the member record
                  //******************************************************************
                  //
                  if (skip == false && found == true && !fname.equals("") && !lname.equals("")) {

                     //
                     //   now determine if we should update an existing record or add the new one
                     //
                     memid_old = "";

                     memidChanged = false;
                     changed = false;

                     //
                     //  Truncate the string values to avoid sql error
                     //
                     if (!mi.equals( "" )) {       // if mi specified

                        mi = truncate(mi, 1);           // make sure it is only 1 char
                     }
                     if (!memid.equals( "" )) {

                        memid = truncate(memid, 15);
                     }
                     if (!password.equals( "" )) {

                        password = truncate(password, 15);
                     }
                     if (!lname.equals( "" )) {

                        lname = truncate(lname, 20);
                     }
                     if (!fname.equals( "" )) {

                        fname = truncate(fname, 20);
                     }
                     if (!mship.equals( "" )) {

                        mship = truncate(mship, 30);
                     }
                     if (!mtype.equals( "" )) {

                        mtype = truncate(mtype, 30);
                     }
                     if (!email.equals( "" )) {

                        email = truncate(email, 50);
                     }
                     if (!email2.equals( "" )) {

                        email2 = truncate(email2, 50);
                     }
                     if (!mNum.equals( "" )) {

                        mNum = truncate(mNum, 10);
                     }
                     if (!ghin.equals( "" )) {

                        ghin = truncate(ghin, 16);
                     }
                     if (!bag.equals( "" )) {

                        bag = truncate(bag, 12);
                     }
                     if (!posid.equals( "" )) {

                        posid = truncate(posid, 15);
                     }
                     if (!phone.equals( "" )) {

                        phone = truncate(phone, 24);
                     }
                     if (!suffix.equals( "" )) {

                        suffix = truncate(suffix, 4);
                     }


                     pstmt2 = con.prepareStatement (
                              "SELECT username, email, email_bounced FROM member2b WHERE webid = ?");

                     pstmt2.clearParameters();               // clear the parms
                     pstmt2.setString(1, webid);            // put the parm in stmt
                     rs = pstmt2.executeQuery();            // execute the prepared stmt

                     if(rs.next()) {

                        memid_old = rs.getString("username");
                        email_old = rs.getString("email");
                        email_bounce1 = rs.getInt("email_bounced");
                     }
                     pstmt2.close();              // close the stmt


                     if (!memid_old.equals( "" )) {            // if member found

                        memid_new = memid_old;

                        if (!memid.equals( memid_old )) {       // if username has changed

                           memid_new = memid;                   // use new memid
                           memidChanged = true;
                        }

                        if (email.equals( "" ) && !email_old.equals( "" )) {

                           email = email_old;        // do not destroy existing email
                        }

                        if (!email.equals( "" ) && !email.equals( email_old )) {        // if email has changed

                           email_bounce1 = 0;        // clear bounce flag
                        }

                        
                        try {

                           pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                           "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                           "memNum = ?, posid = ?, phone1 = ?, " +
                           "inact = 0, last_sync_date = now(), gender = ?, email_bounced = ? " +
                           "WHERE webid = ?");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, memid_new);
                           pstmt2.setString(2, lname);
                           pstmt2.setString(3, fname);
                           pstmt2.setString(4, mi);
                           pstmt2.setString(5, mship);
                           pstmt2.setString(6, mtype);
                           pstmt2.setString(7, email);
                           pstmt2.setString(8, mNum);
                           pstmt2.setString(9, posid);
                           pstmt2.setString(10, phone);
                           pstmt2.setString(11, gender);
                           pstmt2.setInt(12, email_bounce1);
                           
                           pstmt2.setString(13, webid);
                           pstmt2.executeUpdate();

                           pstmt2.close();              // close the stmt

                        }
                        catch (Exception e9) {

                           errorMsg = errorMsg + " Error updating record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e9.getMessage();   // build msg
                           SystemUtils.logError(errorMsg);                                                  // log it
                           errorMsg = "Error in Common_sync.dreamworldSync: ";
                        }


                        //
                        //  Now, update other tables if the username has changed
                        //
                        if (memidChanged == true) {

                           StringBuffer mem_name = new StringBuffer( fname );       // get the new first name

                           if (!mi.equals( "" )) {
                              mem_name.append(" " +mi);               // new mi
                           }
                           mem_name.append(" " +lname);               // new last name

                           String newName = mem_name.toString();          // convert to one string

                           Admin_editmem.updTeecurr(newName, memid_new, memid_old, con);      // update teecurr with new values

                           Admin_editmem.updTeepast(newName, memid_new, memid_old, con);      // update teepast with new values

                           Admin_editmem.updLreqs(newName, memid_new, memid_old, con);        // update lreqs with new values

                           Admin_editmem.updPartner(memid_new, memid_old, con);               // update partner with new values

                           Admin_editmem.updEvents(newName, memid_new, memid_old, con);        // update evntSignUp with new values

                           Admin_editmem.updLessons(newName, memid_new, memid_old, con);       // update the lesson books with new values
                        }


                     } else {

                        //
                        //  New member - first check if name already exists
                        //
                        boolean dup = false;

                        pstmt2 = con.prepareStatement (
                                 "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                        pstmt2.clearParameters();
                        pstmt2.setString(1, lname);
                        pstmt2.setString(2, fname);
                        pstmt2.setString(3, mi);
                        rs = pstmt2.executeQuery();            // execute the prepared stmt

                        if (rs.next()) {

                           dup = true;
                        }
                        pstmt2.close();              // close the stmt

                        if (dup == false) {

                           //
                           //  New member - add it
                           //
                           newCount++;             // count records added

                           try {

                              pstmt2 = con.prepareStatement (
                                 "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                                 "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                                 "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, " +
                                 "webid, last_sync_date, gender) " +
                                 "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,?,now(),?)");

                              pstmt2.clearParameters();        // clear the parms
                              pstmt2.setString(1, memid);        // put the parm in stmt
                              pstmt2.setString(2, password);
                              pstmt2.setString(3, lname);
                              pstmt2.setString(4, fname);
                              pstmt2.setString(5, mi);
                              pstmt2.setString(6, mship);
                              pstmt2.setString(7, mtype);
                              pstmt2.setString(8, email);
                              pstmt2.setFloat(9, c_hcap);
                              pstmt2.setFloat(10, u_hcap);
                              pstmt2.setString(11, mNum);
                              pstmt2.setString(12, ghin);
                              pstmt2.setString(13, bag);
                              pstmt2.setInt(14, birth);
                              pstmt2.setString(15, posid);
                              pstmt2.setString(16, email2);
                              pstmt2.setString(17, phone);
                              pstmt2.setString(18, phone2);
                              pstmt2.setString(19, suffix);
                              pstmt2.setString(20, webid);
                              pstmt2.setString(21, gender);
                              pstmt2.executeUpdate();          // execute the prepared stmt

                              pstmt2.close();              // close the stmt

                           }
                           catch (Exception e8) {

                              errorMsg = errorMsg + " Error adding record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e8.getMessage();   // build msg
                              SystemUtils.logError(errorMsg);                                                  // log it
                              errorMsg = "Error in Common_sync.dreamworldSync: ";
                           }

                        } else {      // Dup name

                           errorMsg = errorMsg + " Duplicate Name found, name = " +fname+ " " +lname+ ", record #" +rcount+ " for " +club+ ", line = " +line;   // build msg
                           SystemUtils.logError(errorMsg);                                                  // log it
                           errorMsg = "Error in Common_sync.dreamworldSync: ";
                        }
                     }

                  }   // end of IF skip

               }   // end of IF record valid (enough tokens)

            }   // end of IF club = ????

         }   // end of IF header row

      }   // end of while

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {       // if we processed this club

         pstmt2 = con.prepareStatement (
           "UPDATE member2b SET inact = 1 " +
           "WHERE last_sync_date != now() AND last_sync_date != '0000-00-00'");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.executeUpdate();

         pstmt2.close();              // close the stmt
         
         
         //
         //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
         //
         setRSind(con, club);
      }

   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster (record #" +rcount+ ") for " +club+ ", line = " +line+ ": " + e3.getMessage();   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
      errorMsg = "Error in Common_sync.dreamworldSync: ";
   }

 }         // end of Dream World Sync



 //
 //   Method to process files from Dream World system
 //
 private static void clubTecSync(Connection con, InputStreamReader isr, String club) {

   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;
   int inact = 0;
   int pri_indicator = 0;

   // Values from MFirst records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String msub_type = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String phone3 = "";
   String mobile = "";
   String primary = "";
   String webid = "";
   String custom1 = "";
   String custom2 = "";
   String custom3 = "";

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;

   // Values from ForeTees records
   //
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";
   String msub_type_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;

   // Values for New ForeTees records
   //
   String memid_new = "";
   String webid_new = "";
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";
   String msub_type_new = "";
   String dupuser = "";
   String dupwebid = "";
   String dupmnum = "";

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int errCount = 0;
   int warnCount = 0;
   int totalErrCount = 0;
   int totalWarnCount = 0;
   int email_bounce1 = 0;
   int email_bounce2 = 0;

   String errorMsg = "";
   String errMemInfo = "";
   String errMsg = "";
   String warnMsg = "";

   ArrayList<String> errList = new ArrayList<String>();
   ArrayList<String> warnList = new ArrayList<String>();

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean found = false;
   boolean genderMissing = false;
   boolean useWebid = false;
   boolean useWebidQuery = false;
   boolean headerFound = false;


   // Overwrite log file with fresh one for today's logs
   SystemUtils.logErrorToFile("ClubTec: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

      Calendar caly = new GregorianCalendar();       // get todays date
      int thisYear = caly.get(Calendar.YEAR);        // get this year value

      thisYear = thisYear - 2000;

      BufferedReader bfrin = new BufferedReader(isr);
      line = new String();

      //   format of each line in the file:
      //
      //     memid, mNum, fname, mi, lname, suffix, mship, mtype, gender, email, email2,
      //     phone, phone2, bag, hndcp#, uhndcp, chndcp, birth, posid, mobile, primary
      //
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text


         //  Skip the 1st row (header row)

         if (headerFound == false) {

            headerFound = true;

         } else {

             //  Remove the dbl quotes and check for embedded commas
             line = cleanRecord4( line );

             //  parse the line to gather all the info

             StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

             if ( tok.countTokens() >= 32 ) {     // enough data ?

               // Gather data from input (* indicates field is used)
               mNum = tok.nextToken();          // Col A*
               fname = tok.nextToken();         // Col B*
               lname = tok.nextToken();         // Col C*
               suffix = tok.nextToken();        // Col D*
               primary = tok.nextToken();       // Col E*
                tok.nextToken();                // Col F
               mship = tok.nextToken();         // Col G*
                tok.nextToken();                // Col H
                tok.nextToken();                // Col I
                tok.nextToken();                // Col J
                tok.nextToken();                // Col K
                tok.nextToken();                // Col L
                tok.nextToken();                // Col M
                tok.nextToken();                // Col N
               phone = tok.nextToken();         // Col O*
               email = tok.nextToken();         // Col P*
                tok.nextToken();                // Col Q
                tok.nextToken();                // Col R
                tok.nextToken();                // Col S
                tok.nextToken();                // Col T
                tok.nextToken();                // Col U
                tok.nextToken();                // Col V
                tok.nextToken();                // Col W
               phone2 = tok.nextToken();        // Col X*
               email2 = tok.nextToken();        // Col Y*
               phone3 = tok.nextToken();        // Col Z*
                tok.nextToken();                // Col AA
               mi = tok.nextToken();            // Col AB*
               gender = tok.nextToken();        // Col AC*
               temp = tok.nextToken();          // Col AD*
               // AE and AF are skipped


               //
               //  Check for ? (not provided)
               //
               if (webid.equals("?")) webid = "";
               if (memid.equals("?")) memid = "";
               if (mNum.equals("?")) mNum = "";
               if (primary.equals("?")) primary = "";
               if (fname.equals("?")) fname = "";
               if (mi.equals("?")) mi = "";
               if (lname.equals("?")) lname = "";
               if (suffix.equals("?")) suffix = "";
               if (mship.equals("?")) mship = "";
               if (email.equals("?")) email = "";
               if (email2.equals("?")) email2 = "";
               if (phone.equals("?")) phone = "";
               if (phone2.equals("?")) phone2 = "";
               if (phone3.equals("?")) phone3 = "";
               if (temp.equals("?")) temp = "";

                if (gender.equals( "?" ) || (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F"))) {

                      if (gender.equals( "?" )) {
                          genderMissing = true;
                      } else {
                          genderMissing = false;
                      }
                      gender = "";
                }

                //   trim gender in case followed be spaces
                gender = gender.trim();

                //
                //  Determine if we should process this record (does it meet the minimum requirements?)
                //
                if (!mNum.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                   //
                   //  Remove spaces, etc. from name fields
                   //
                   tok = new StringTokenizer( fname, " " );     // delimiters are space
                   fname = tok.nextToken();                     // remove any spaces and middle name

                   if ( tok.countTokens() > 0 && mi.equals( "" )) {

                        mi = tok.nextToken();
                   }

                   if (!suffix.equals( "" )) {                     // if suffix provided

                      tok = new StringTokenizer( suffix, " " );     // delimiters are space
                      suffix = tok.nextToken();                     // remove any extra (only use one value)
                   }

                   tok = new StringTokenizer( lname, " " );     // delimiters are space
                   lname = tok.nextToken();                     // remove suffix and spaces

                   if (!suffix.equals( "" ) && tok.countTokens() > 0 && club.equals("pradera")) {     // Pradera - if suffix AND 2-part lname, use both

                       String lpart2 = tok.nextToken();

                       lname = lname + "_" + lpart2;          // combine them (i.e.  Van Ess = Van_Ess)

                   } else {

                      if (suffix.equals( "" ) && tok.countTokens() > 0) {                   // if suffix not provided

                         suffix = tok.nextToken();
                      }
                   }

                   //
                   //  Make sure name is titled (most are already)
                   //
                   /*
                   fname = toTitleCase(fname);
                   lname = toTitleCase(lname);
                   */

                   if (!suffix.equals( "" )) {                  // if suffix provided

                      lname = lname + "_" + suffix;             // append suffix to last name
                   }

                   //
                   //  Determine the handicaps
                   //
                   u_hcap = -99;                    // indicate no hndcp
                   c_hcap = -99;                    // indicate no c_hndcp

                   if (!u_hndcp.equals( "" ) && !u_hndcp.equalsIgnoreCase("NH") && !u_hndcp.equalsIgnoreCase("NHL")) {

                      u_hndcp = u_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                      u_hndcp = u_hndcp.replace('H', ' ');    //         or 'H' if present
                      u_hndcp = u_hndcp.replace('N', ' ');    //         or 'N' if present
                      u_hndcp = u_hndcp.replace('J', ' ');    //         or 'J' if present
                      u_hndcp = u_hndcp.replace('R', ' ');    //         or 'R' if present
                      u_hndcp = u_hndcp.trim();

                      u_hcap = Float.parseFloat(u_hndcp);                   // usga handicap

                      if ((!u_hndcp.startsWith("+")) && (!u_hndcp.startsWith("-"))) {

                         u_hcap = 0 - u_hcap;                       // make it a negative hndcp (normal)
                      }
                   }

                   if (!c_hndcp.equals( "" ) && !c_hndcp.equalsIgnoreCase("NH") && !c_hndcp.equalsIgnoreCase("NHL")) {

                      c_hndcp = c_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                      c_hndcp = c_hndcp.replace('H', ' ');    //         or 'H' if present
                      c_hndcp = c_hndcp.replace('N', ' ');    //         or 'N' if present
                      c_hndcp = c_hndcp.replace('J', ' ');    //         or 'J' if present
                      c_hndcp = c_hndcp.replace('R', ' ');    //         or 'R' if present
                      c_hndcp = c_hndcp.trim();

                      c_hcap = Float.parseFloat(c_hndcp);                   // usga handicap

                      if ((!c_hndcp.startsWith("+")) && (!c_hndcp.startsWith("-"))) {

                         c_hcap = 0 - c_hcap;                       // make it a negative hndcp (normal)
                      }
                   }

                   password = lname;

                   //
                   //  if lname is less than 4 chars, fill with 1's
                   //
                   int length = password.length();

                   while (length < 4) {

                      password = password + "1";
                      length++;
                   }

                   // if phone #2 is empty then assign phone #3 to it
                   if (phone2.equals("")) phone2 = phone3;

                   //
                   //  Verify the email addresses
                   //
                   if (!email.equals( "" )) {      // if specified

                      email = email.trim();           // remove spaces

                      FeedBack feedback = (member.isEmailValid(email));

                      if (!feedback.isPositive()) {    // if error

                         email = "";                   // do not use it
                      }
                   }
                   if (!email2.equals( "" )) {      // if specified

                      email2 = email2.trim();           // remove spaces

                      FeedBack feedback = (member.isEmailValid(email2));

                      if (!feedback.isPositive()) {    // if error

                         email2 = "";                   // do not use it
                      }
                   }

                   // if email #1 is empty then assign email #2 to it
                   if (email.equals("")) email = email2;

                  //
                  //  convert birth date (mm/dd/yyyy to yyyymmdd)
                  //
                  if (!temp.equals( "" )) {

                     int mm = 0;
                     int dd = 0;
                     int yy = 0;

                     tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                     if ( tok.countTokens() > 2 ) {

                        String b1 = tok.nextToken();
                        String b2 = tok.nextToken();
                        String b3 = tok.nextToken();

                        mm = Integer.parseInt(b1);
                        dd = Integer.parseInt(b2);
                        yy = Integer.parseInt(b3);

                     } else {                               // try 'Jan 20, 1951' format

                        tok = new StringTokenizer( temp, ", " );          // delimiters are comma and space

                        if ( tok.countTokens() > 2 ) {

                           String b1 = tok.nextToken();
                           String b2 = tok.nextToken();
                           String b3 = tok.nextToken();

                           if (b1.startsWith( "Jan" )) {
                              mm = 1;
                           } else {
                            if (b1.startsWith( "Feb" )) {
                               mm = 2;
                            } else {
                             if (b1.startsWith( "Mar" )) {
                                mm = 3;
                             } else {
                              if (b1.startsWith( "Apr" )) {
                                 mm = 4;
                              } else {
                               if (b1.startsWith( "May" )) {
                                  mm = 5;
                               } else {
                                if (b1.startsWith( "Jun" )) {
                                   mm = 6;
                                } else {
                                 if (b1.startsWith( "Jul" )) {
                                    mm = 7;
                                 } else {
                                  if (b1.startsWith( "Aug" )) {
                                     mm = 8;
                                  } else {
                                   if (b1.startsWith( "Sep" )) {
                                      mm = 9;
                                   } else {
                                    if (b1.startsWith( "Oct" )) {
                                       mm = 10;
                                    } else {
                                     if (b1.startsWith( "Nov" )) {
                                        mm = 11;
                                     } else {
                                      if (b1.startsWith( "Dec" )) {
                                         mm = 12;
                                      } else {
                                         mm = Integer.parseInt(b1);
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

                           dd = Integer.parseInt(b2);
                           yy = Integer.parseInt(b3);

                        } else {

                           birth = 0;
                        }
                     }

                     if (mm > 0) {                              // if birth provided

                        if (mm == 1 && dd == 1 && yy == 1) {        // skip if 1/1/0001

                           birth = 0;

                        } else {

                           if (yy < 100) {                          // if 2 digit year

                              if (yy <= thisYear) {

                                 yy += 2000;          // 20xx

                              } else {

                                 yy += 1900;          // 19xx
                              }
                           }

                           birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                           if (yy < 1900) {                             // check for invalid date

                              birth = 0;
                           }
                        }

                     } else {

                        birth = 0;
                     }

                  } else {

                     birth = 0;
                  }


                   skip = false;
                   errCount = 0;        // reset error count
                   warnCount = 0;       // reset warning count
                   errMsg = "";         // reset error message
                   warnMsg = "";        // reset warning message
                   errMemInfo = "";     // reset error member info
                   found = false;    // init club found


                   //
                   //  Format member info for use in error logging before club-specific manipulation
                   //
                   errMemInfo = "Member Details:\n" +
                                          "  name: " + lname + ", " + fname + " " + mi + "\n" +
                                          "  mtype: " + mtype + "  mship: " + mship + "\n" +
                                          "  memid: " + memid + "  mNum: " + mNum + "  gender: " + gender;

                   // if gender is incorrect or missing, flag a warning in the error log
                   if (gender.equals("")) {

                       // report only if not a club that uses blank gender fields (set to true for now, change if club's want other options down the line)
                       if (true) {

                           warnCount++;
                           if (genderMissing) {
                             warnMsg = warnMsg + "\n" +
                                     "  -GENDER missing! (Defaulted to 'M')";
                           } else {
                             warnMsg = warnMsg + "\n" +
                                     "  -GENDER incorrect! (Defaulted to 'M')";
                           }

                           gender = "M";

                       } else if (false) {         // default to female instead (left in for future clubs)

                           warnCount++;
                           if (genderMissing) {
                             warnMsg = warnMsg + "\n" +
                                     "  -GENDER missing! (Defaulted to 'F')";
                           } else {
                             warnMsg = warnMsg + "\n" +
                                     "  -GENDER incorrect! (Defaulted to 'F)";
                           }

                           gender = "F";

                       } else if (false) {      // skip anyone missing gender (left in for future clubs)

                           errCount++;
                           skip = true;
                           if (genderMissing) {
                               errMsg = errMsg + "\n" +
                                       "  -SKIPPED: GENDER missing!";
                           } else {
                               errMsg = errMsg + "\n" +
                                       "  -SKIPPED: GENDER incorrect!";
                           }
                       }
                   }

                   //
                   //  Skip entries with first/last names of 'admin'
                   //
                   if (fname.equalsIgnoreCase("admin") || lname.equalsIgnoreCase("admin")) {
                       errCount++;
                       skip = true;
                       errMsg = errMsg + "\n" +
                               "  -INVALID NAME! 'Admin' or 'admin' not allowed for first or last name";
                   }

                   //
                   // *********************************************************************
                   //
                   //   The following will be dependent on the club - customized
                   //
                   // *********************************************************************
                   //

                   //******************************************************************
                   //   Rolling Hills CC - CO - rhillscc
                   //******************************************************************
                   //
                   if (club.equals("rhillscc")) {

                       found = true;      // club found

                       if (mship.equals( "" )) {

                           skip = true;       //  skip this one
                           errCount++;
                           errMsg = errMsg + "\n" +
                                   "  -MEMBERSHIP TYPE missing!";

                       } else if (mNum.equals( "" )) {

                           skip = true;                          //  skip this one
                           warnCount++;
                           warnMsg = warnMsg + "\n" +
                                   "  -SKIPPED: Member Number Missing!";

                       } else {

                           posid = mNum;                 // use their member numbers as their posid

                           while (mNum.startsWith("0")) {
                               mNum = mNum.substring(1);
                           }

                           memid = mNum;

                           StringTokenizer temptok = new StringTokenizer(mNum, "-");

                           if (temptok.countTokens() > 1) {
                               mNum = temptok.nextToken();
                           }

                           if (mship.contains("social") || mship.contains("Social")) {
                               mship = "Social";
                           } else if (mship.contains("absentee") || mship.contains("Absentee")) {
                               mship = "Absentee";
                           } else {
                               mship = "Golf";
                           }

                           if (memid.endsWith("-000")) {

                               if (gender.equalsIgnoreCase("F")) {
                                   mtype = "Primary Female";
                               } else {
                                   mtype = "Primary Male";
                               }

                           } else if (memid.endsWith("-001")) {

                               if (gender.equalsIgnoreCase("M")) {
                                   mtype = "Secondary Male";
                               } else {
                                   mtype = "Secondary Female";
                               }

                           } else {

                               mtype = "Dependent";
                           }

                       }
                   }  // end if rhillscc


                   //******************************************************************
                   //   All clubs
                   //******************************************************************
                   //
                   if (skip == false && found == true && !fname.equals("") && !lname.equals("") && !memid.equals("")) {

                      //
                      //   now determine if we should update an existing record or add the new one
                      //
                      fname_old = "";
                      lname_old = "";
                      mi_old = "";
                      mship_old = "";
                      mtype_old = "";
                      email_old = "";
                      mNum_old = "";
                      ghin_old = "";
                      bag_old = "";
                      posid_old = "";
                      email2_old = "";
                      phone_old = "";
                      phone2_old = "";
                      suffix_old = "";
                      u_hcap_old = 0;
                      c_hcap_old = 0;
                      birth_old = 0;
                      msub_type_old = "";
                      email_bounce1 = 0;
                      email_bounce2 = 0;


                      //
                      //  Truncate the string values to avoid sql error
                      //
                      if (!mi.equals( "" )) {       // if mi specified

                         mi = truncate(mi, 1);           // make sure it is only 1 char
                      }
                      if (!memid.equals( "" )) {

                         memid = truncate(memid, 15);
                      }
                      if (!password.equals( "" )) {

                         password = truncate(password, 15);
                      }
                      if (!lname.equals( "" )) {

                         lname = truncate(lname, 20);
                      }
                      if (!fname.equals( "" )) {

                         fname = truncate(fname, 20);
                      }
                      if (!mship.equals( "" )) {

                         mship = truncate(mship, 30);
                      }
                      if (!mtype.equals( "" )) {

                         mtype = truncate(mtype, 30);
                      }
                      if (!email.equals( "" )) {

                         email = truncate(email, 50);
                      }
                      if (!email2.equals( "" )) {

                         email2 = truncate(email2, 50);
                      }
                      if (!mNum.equals( "" )) {

                         mNum = truncate(mNum, 10);
                      }
                      if (!ghin.equals( "" )) {

                         ghin = truncate(ghin, 16);
                      }
                      if (!bag.equals( "" )) {

                         bag = truncate(bag, 12);
                      }
                      if (!posid.equals( "" )) {

                         posid = truncate(posid, 15);
                      }
                      if (!phone.equals( "" )) {

                         phone = truncate(phone, 24);
                      }
                      if (!phone2.equals( "" )) {

                         phone2 = truncate(phone2, 24);
                      }
                      if (!suffix.equals( "" )) {

                         suffix = truncate(suffix, 4);
                      }
                      if (!webid.equals( "" )) {

                         webid = truncate(webid, 15);
                      }
                      if (!msub_type.equals( "" )) {

                         msub_type = truncate(msub_type, 30);
                      }

                      //
                      //  Set Gender and Primary values
                      //
                      if (!gender.equalsIgnoreCase( "M" ) && !gender.equalsIgnoreCase( "F" )) {

                         gender = "";
                      }

                      pri_indicator = 0;                            // default = Primary

                      if (primary.equalsIgnoreCase( "S" )) {        // Spouse

                         pri_indicator = 1;
                      }
                      if (primary.equalsIgnoreCase( "D" )) {        // Dependent

                         pri_indicator = 2;
                      }


                      //
                      //  See if a member already exists with this id (username or webid)
                      //
                      //  **** NOTE:  memid and webid MUST be set to their proper values before we get here!!!!!!!!!!!!!!!
                      //
                      //
                      //  4/07/2010
                      //    We now use 2 booleans to indicate what to do with the webid field.
                      //    useWebid is the original boolean that was used to indicate that we should use the webid to identify the member.
                      //       We lost track of when this flag was used and why.  It was no longer used to indicate the webid should be
                      //       used in the query, so we don't know what its real purpose is.  We don't want to disrupt any clubs that are
                      //       using it, so we created a new boolean for the query.
                      //    useWebidQuery is the new flag to indicate that we should use the webid when searching for the member.  You should use
                      //       both flags if you want to use this one.
                      //
                      if (useWebid == true) {            // use webid to locate member?

                         webid_new = webid;                  // yes, set new ids
                         memid_new = "";

                      } else {                            // DO NOT use webid

                         webid_new = "";                  // set new ids
                         memid_new = memid;
                      }

                      if (useWebidQuery == false) {

                         pstmt2 = con.prepareStatement (
                                   "SELECT * FROM member2b WHERE username = ?");

                         pstmt2.clearParameters();               // clear the parms
                         pstmt2.setString(1, memid);            // put the parm in stmt

                      } else {    // use the webid field

                         pstmt2 = con.prepareStatement (
                                   "SELECT * FROM member2b WHERE webid = ?");

                         pstmt2.clearParameters();               // clear the parms
                         pstmt2.setString(1, webid);            // put the parm in stmt
                      }

                      rs = pstmt2.executeQuery();            // execute the prepared stmt

                      if (rs.next()) {

                         memid = rs.getString("username");            // get username in case we used webid (use this for existing members)
                         lname_old = rs.getString("name_last");
                         fname_old = rs.getString("name_first");
                         mi_old = rs.getString("name_mi");
                         mship_old = rs.getString("m_ship");
                         mtype_old = rs.getString("m_type");
                         email_old = rs.getString("email");
                         mNum_old = rs.getString("memNum");
                         ghin_old = rs.getString("ghin");
                         bag_old = rs.getString("bag");
                         birth_old = rs.getInt("birth");
                         posid_old = rs.getString("posid");
                         msub_type_old = rs.getString("msub_type");
                         email2_old = rs.getString("email2");
                         phone_old = rs.getString("phone1");
                         phone2_old = rs.getString("phone2");
                         suffix_old = rs.getString("name_suf");
                         email_bounce1 = rs.getInt("email_bounced");
                         email_bounce2 = rs.getInt("email2_bounced");

                         if (useWebid == true) {                    // use webid to locate member?
                            memid_new = memid;                      // yes, get this username
                         } else {
                            webid_new = rs.getString("webid");      // no, get current webid
                         }
                      }
                      pstmt2.close();              // close the stmt


                      //
                      //  If member NOT found, then check if new member OR id has changed
                      //
                      boolean memFound = false;
                      boolean dup = false;
                      boolean userChanged = false;
                      boolean nameChanged = false;
                      String dupmship = "";

                      if (fname_old.equals( "" )) {            // if member NOT found

                         //
                         //  New member - first check if name already exists
                         //
                         pstmt2 = con.prepareStatement (
                                  "SELECT username, m_ship, memNum, webid FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                         pstmt2.clearParameters();
                         pstmt2.setString(1, lname);
                         pstmt2.setString(2, fname);
                         pstmt2.setString(3, mi);
                         rs = pstmt2.executeQuery();            // execute the prepared stmt

                         if (rs.next()) {   // Allow duplicate names for Long Cove for members owning two properties at once

                            dupuser = rs.getString("username");          // get this username
                            dupmship = rs.getString("m_ship");
                            dupmnum = rs.getString("memNum");
                            dupwebid = rs.getString("webid");            // get this webid

                            //
                            //  name already exists - see if this is the same member
                            //
                            if ((!dupmnum.equals( "" ) && dupmnum.equals( mNum )) || club.equals("imperialgc")) {   // if name and mNum match, then memid or webid must have changed

                               if (useWebid == true) {            // use webid to locate member?

                                  webid_new = webid;                  // set new ids
                                  memid_new = dupuser;
                                  memid = dupuser;                    // update this record

                               } else {

                                  webid_new = dupwebid;                  // set new ids
                                  memid_new = memid;
                                  memid = dupuser;                       // update this record
                                  userChanged = true;                    // indicate the username has changed
                               }

                               memFound = true;                      // update the member

                               pstmt2 = con.prepareStatement (
                                       "SELECT * FROM member2b WHERE username = ?");

                               pstmt2.clearParameters();               // clear the parms
                               pstmt2.setString(1, memid);            // put the parm in stmt
                               rs = pstmt2.executeQuery();            // execute the prepared stmt

                               if (rs.next()) {

                                   lname_old = rs.getString("name_last");
                                   fname_old = rs.getString("name_first");
                                   mi_old = rs.getString("name_mi");
                                   mship_old = rs.getString("m_ship");
                                   mtype_old = rs.getString("m_type");
                                   email_old = rs.getString("email");
                                   mNum_old = rs.getString("memNum");
                                   ghin_old = rs.getString("ghin");
                                   bag_old = rs.getString("bag");
                                   birth_old = rs.getInt("birth");
                                   posid_old = rs.getString("posid");
                                   msub_type_old = rs.getString("msub_type");
                                   email2_old = rs.getString("email2");
                                   phone_old = rs.getString("phone1");
                                   phone2_old = rs.getString("phone2");
                                   suffix_old = rs.getString("name_suf");
                                   email_bounce1 = rs.getInt("email_bounced");
                                   email_bounce2 = rs.getInt("email2_bounced");
                               }

                            } else {
                               dup = true;        // dup member - do not add
                            }

                         }
                         pstmt2.close();              // close the stmt

                      } else {      // member found
                         memFound = true;
                      }

                      //
                      //  Now, update the member record if existing member
                      //
                      if (memFound == true) {                   // if member exists

                         changed = false;                       // init change indicator

                         lname_new = lname_old;

                         // do not change lname for Saucon Valley if lname_old ends with '_*'
                         if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                            lname_new = lname;         // set value from ClubTec record
                            changed = true;
                            nameChanged = true;
                         }

                         fname_new = fname_old;

                         if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                             fname_new = fname;         // set value from ClubTec record
                             changed = true;
                             nameChanged = true;
                         }

                         mi_new = mi_old;

                         if (!mi_old.equals( mi )) {

                             mi_new = mi;         // set value from ClubTec record
                             changed = true;
                             nameChanged = true;
                         }

                         mship_new = mship_old;

                         if (!mship.equals( "" ) && !mship_old.equals( mship )) {

                            mship_new = mship;         // set value from ClubTec record
                            changed = true;
                         }

                         mtype_new = mtype_old;

                         if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {

                             mtype_new = mtype;         // set value from ClubTec record
                             changed = true;
                         }

                         mNum_new = mNum_old;

                         if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                            mNum_new = mNum;         // set value from ClubTec record
                            changed = true;
                         }

                         ghin_new = ghin_old;

                         if (!ghin.equals( "" ) && !ghin_old.equals( ghin )) {

                            ghin_new = ghin;         // set value from ClubTec record
                            changed = true;
                         }

                         bag_new = bag_old;

                         if (!bag.equals( "" ) && !bag_old.equals( bag )) {

                             bag_new = bag;         // set value from ClubTec record
                             changed = true;
                         }

                         posid_new = posid_old;

                         if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                            posid_new = posid;         // set value from ClubTec record
                            changed = true;
                         }

                         email_new = email_old;

                         if (!email_old.equals( email )) {        // if MF's email changed or was removed

                             email_new = email;                    // set value from ClubTec record
                             changed = true;
                             email_bounce1 = 0;                    // reset bounced flag
                         }

                         email2_new = email2_old;

                         if (!email2_old.equals( email2 )) {      // if MF's email changed or was removed

                             email2_new = email2;                  // set value from ClubTec record
                             changed = true;
                             email_bounce2 = 0;                    // reset bounced flag
                         }

                         phone_new = phone_old;

                         if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                            phone_new = phone;         // set value from ClubTec record
                            changed = true;
                         }

                         phone2_new = phone2_old;

                         if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                            phone2_new = phone2;         // set value from ClubTec record
                            changed = true;
                         }

                         suffix_new = suffix_old;

                         if (!suffix.equals( "" ) && !suffix_old.equals( suffix )) {

                            suffix_new = suffix;         // set value from ClubTec record
                            changed = true;
                         }

                         birth_new = birth_old;

                         if (birth > 0 && birth != birth_old) {

                             birth_new = birth;         // set value from ClubTec record
                             changed = true;
                         }

                         if (!mobile.equals( "" )) {    // if mobile phone provided

                            if (phone_new.equals( "" )) {        // if phone1 is empty

                               phone_new = mobile;               // use mobile number
                               changed = true;

                            } else {

                               if (phone2_new.equals( "" )) {        // if phone2 is empty

                                  phone2_new = mobile;               // use mobile number
                                  changed = true;
                               }
                            }
                         }

                         msub_type_new = msub_type_old;

                         if (!msub_type.equals( "" ) && !msub_type_old.equals( msub_type )) {

                            msub_type_new = msub_type;         // set value from ClubTec record
                            changed = true;
                         }

                         // don't allow both emails to be the same
                         if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";

                         //
                         //  Update our record (always now to set the last_sync_date)
                         //
                         pstmt2 = con.prepareStatement (
                         "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                         "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                         "memNum = ?, ghin = ?, bag = ?, birth = ?, posid = ?, msub_type = ?, email2 = ?, phone1 = ?, " +
                         "phone2 = ?, name_suf = ?, webid = ?, inact = 0, last_sync_date = now(), gender = ?, pri_indicator = ?, " +
                         "email_bounced = ?, email2_bounced = ? " +
                         "WHERE username = ?");

                         pstmt2.clearParameters();        // clear the parms
                         pstmt2.setString(1, memid_new);
                         pstmt2.setString(2, lname_new);
                         pstmt2.setString(3, fname_new);
                         pstmt2.setString(4, mi_new);
                         pstmt2.setString(5, mship_new);
                         pstmt2.setString(6, mtype_new);
                         pstmt2.setString(7, email_new);
                         pstmt2.setString(8, mNum_new);
                         pstmt2.setString(9, ghin_new);
                         pstmt2.setString(10, bag_new);
                         pstmt2.setInt(11, birth_new);
                         pstmt2.setString(12, posid_new);
                         pstmt2.setString(13, msub_type_new);
                         pstmt2.setString(14, email2_new);
                         pstmt2.setString(15, phone_new);
                         pstmt2.setString(16, phone2_new);
                         pstmt2.setString(17, suffix_new);
                         pstmt2.setString(18, webid_new);
                         pstmt2.setString(19, gender);
                         pstmt2.setInt(20, pri_indicator);
                         pstmt2.setInt(21, email_bounce1);
                         pstmt2.setInt(22, email_bounce2);

                         pstmt2.setString(23, memid);
                         pstmt2.executeUpdate();

                         pstmt2.close();              // close the stmt


                      } else {                      // member NOT found - add it if we can

                         if (dup == false) {        // if not duplicate member

                            //
                            //  New member is ok - add it
                            //
                            pstmt2 = con.prepareStatement (
                               "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                               "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                               "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, webid, " +
                               "last_sync_date, gender, pri_indicator) " +
                               "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,?,?,?,?,'',?,?, now(),?,?)");

                            pstmt2.clearParameters();        // clear the parms
                            pstmt2.setString(1, memid);        // put the parm in stmt
                            pstmt2.setString(2, password);
                            pstmt2.setString(3, lname);
                            pstmt2.setString(4, fname);
                            pstmt2.setString(5, mi);
                            pstmt2.setString(6, mship);
                            pstmt2.setString(7, mtype);
                            pstmt2.setString(8, email);
                            pstmt2.setFloat(9, c_hcap);
                            pstmt2.setFloat(10, u_hcap);
                            pstmt2.setString(11, mNum);
                            pstmt2.setString(12, ghin);
                            pstmt2.setString(13, bag);
                            pstmt2.setInt(14, birth);
                            pstmt2.setString(15, posid);
                            pstmt2.setString(16, msub_type);
                            pstmt2.setString(17, email2);
                            pstmt2.setString(18, phone);
                            pstmt2.setString(19, phone2);
                            pstmt2.setString(20, suffix);
                            pstmt2.setString(21, webid);
                            pstmt2.setString(22, gender);
                            pstmt2.setInt(23, pri_indicator);
                            pstmt2.executeUpdate();          // execute the prepared stmt

                            pstmt2.close();              // close the stmt

                         } else {     // this member not found, but name already exists

                            if (dup) {
                                    errCount++;
                                    errMsg = errMsg + "\n  -Dup user found:\n" +
                                            "    new: memid = " + memid + "  :  cur: " + dupuser + "\n" +
                                            "         webid = " + webid + "  :       " + dupwebid + "\n" +
                                            "         mNum  = " + mNum  + "  :       " + dupmnum;
                            }
                         }
                      }               // end of IF Member Found

                      //
                      //  Member updated - now see if the username or name changed
                      //
                      if (userChanged == true || nameChanged == true) {        // if username or name changed

                         //
                         //  username or name changed - we must update other tables now
                         //
                         StringBuffer mem_name = new StringBuffer( fname_new );       // get the new first name

                         if (!mi_new.equals( "" )) {
                            mem_name.append(" " +mi_new);               // new mi
                         }
                         mem_name.append(" " +lname_new);               // new last name

                         String newName = mem_name.toString();          // convert to one string

                         Admin_editmem.updTeecurr(newName, memid_new, memid, con);      // update teecurr with new values

                         Admin_editmem.updTeepast(newName, memid_new, memid, con);      // update teepast with new values

                         Admin_editmem.updLreqs(newName, memid_new, memid, con);        // update lreqs with new values

                         Admin_editmem.updPartner(memid_new, memid, con);               // update partner with new values

                         Admin_editmem.updEvents(newName, memid_new, memid, con);        // update evntSignUp with new values

                         Admin_editmem.updLessons(newName, memid_new, memid, con);       // update the lesson books with new values
                      }

                   } else {

                       // Only report errors that AREN'T due to skip == true, since those were handled earlier!
                       if (!found) {
                           errCount++;
                           errMsg = errMsg + "\n" +
                                   "  -MEMBER NOT FOUND!";
                       }
                       if (fname.equals("")) {
                           errCount++;
                           errMsg = errMsg + "\n" +
                                   "  -FIRST NAME missing!";
                       }
                       if (lname.equals("")) {
                           errCount++;
                           errMsg = errMsg + "\n" +
                                   "  -LAST NAME missing!";
                       }
                       if (memid.equals("")) {
                           errCount++;
                           errMsg = errMsg + "\n" +
                                   "  -USERNAME missing!";
                       }
                   }  // end of IF skip

                }   // end of IF minimum requirements

             }   // end of IF tokens

         }   // end of IF header row

         // log any errors and warnings that occurred
         if (errCount > 0) {
             totalErrCount += errCount;
             errList.add(errMemInfo + "\n  *" + errCount + " error(s) found*" + errMsg + "\n");
         }
         if (warnCount > 0) {
             totalWarnCount += warnCount;
             warnList.add(errMemInfo + "\n  *" + warnCount + " warning(s) found*" + warnMsg + "\n");
         }
      }   // end of while (for each record in club's file)

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {     // if we processed this club

         pstmt2 = con.prepareStatement (
           "UPDATE member2b SET inact = 1 " +
           "WHERE last_sync_date != now() AND last_sync_date != '0000-00-00'");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.executeUpdate();

         pstmt2.close();              // close the stmt


         //
         //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
         //
         setRSind(con, club);
      }
   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster for " +club+ ": " + e3.getMessage() + "\n";   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
   }

   // Print error and warning count totals to error log
   SystemUtils.logErrorToFile("" +
           "Total Errors Found: " + totalErrCount + "\n" +
           "Total Warnings Found: " + totalWarnCount + "\n", club, true);

      // Print errors and warnings to error log
   if (totalErrCount > 0) {
       SystemUtils.logErrorToFile("" +
            "********************************************************************\n" +
            "****ERRORS FOR " + club + " (Member WAS NOT synced!)\n" +
            "********************************************************************\n", club, true);
       while (errList.size() > 0) {
           SystemUtils.logErrorToFile(errList.remove(0), club, true);
       }
   }
   if (totalWarnCount > 0) {
       SystemUtils.logErrorToFile("" +
            "********************************************************************\n" +
            "****WARNINGS FOR " + club + " (Member MAY NOT have synced!)\n" +
            "********************************************************************\n", club, true);
       while (warnList.size() > 0) {
           SystemUtils.logErrorToFile(warnList.remove(0), club, true);
       }
   }

   // Print end tiem to error log
   SystemUtils.logErrorToFile("End time: " + new java.util.Date().toString() + "\n", club, true);

 }    // end of ClubTec


 private static void jonasSync(Connection con, InputStreamReader isr, String club) {

 /*
   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;
   int ext = 1;

   // Values from Jonas records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String prefix = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String webid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String mobile = "";
   String primary = "";
   String active = "";
   String webid_new = "";
   String memid_new = "";
   String lnamePart2;

   String mship2 = ""; // used to tell if match was found

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;
   int inact = 0;

   // Values from ForeTees records
   //
   String memid_old = "";
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;
   int inact_old = 0;

   // Values for New ForeTees records
   //
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";
   String last_mship = "";
   String last_mnum = "";
   String openParen = "(";
   String closeParen = ")";
   String asterik = "*";
   String slash = "/";
   String backslash = "\\";

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int inact_new = 0;
   int rcount = 0;
   int pcount = 0;
   int ncount = 0;
   int ucount = 0;
   int errCount = 0;
   int warnCount = 0;
   int totalErrCount = 0;
   int totalWarnCount = 0;

   String errorMsg = "";
   String errMsg = "";
   String warnMsg = "";
   String errMemInfo = "";

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean headerFound = false;
   boolean found = false;
   boolean useWebid = false;
   boolean genderMissing = false;

   ArrayList<String> errList = new ArrayList<String>();
   ArrayList<String> warnList = new ArrayList<String>();


   // Overwrite log file with fresh one for today's logs
   SystemUtils.logErrorToFile("Jonas: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

     Calendar caly = new GregorianCalendar();       // get todays date
     int thisYear = caly.get(Calendar.YEAR);        // get this year value

     thisYear = thisYear - 2000;                   // 2 digit value


      BufferedReader br = new BufferedReader(isr);

      //   format of each line in the file:
      //
      //     memid, mNum, fname, mi, lname, suffix, mship, mtype, gender, email, email2,
      //     phone, phone2, bag, hndcp#, uhndcp, chndcp, birth, posid, mobile, primary, act/inact
      //
      //
      while (true) {

         line = br.readLine();

         if (line == null) {
            break;
         }

         //  Skip the 1st row (header row)

         if (headerFound == false) {

            headerFound = true;

         } else {

            //  Remove the dbl quotes and check for embedded commas

            line = cleanRecord( line );
            line = cleanRecord4( line );

            rcount++;                          // count the records

            //  parse the line to gather all the info

   SystemUtils.logErrorToFile("Starting Data Parse - Line " + rcount, club, false);
            StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

            if ( tok.countTokens() > 20 ) {     // enough data ?

               mNum = tok.nextToken();     // A
               memid = mNum;
               lname = tok.nextToken();    // B
               fname = tok.nextToken();    // C
               tok.nextToken(); // skip D
               prefix = tok.nextToken();   // E
               suffix = tok.nextToken();   // F

               for (int j=0; j<6; j++) {
                   tok.nextToken();     // skip cols G-L (7-12, 6 cols)
               }

               phone = tok.nextToken();    // M

               for (int j=0; j<2; j++) {
                   tok.nextToken();     // skip cols N-O (14-15, 2 cols)
               }

               gender = tok.nextToken();   // P
               temp = tok.nextToken();     // Q

               for (int j=0; j<93; j++) {
                   tok.nextToken();     // skip cols R-EF (18-110, 93 cols)
               }

               email = tok.nextToken();    // EG

               for (int j=0; j<37; j++) {
                   tok.nextToken();     // skip EH-FR (112-148, 37 cols)
               }

               email2 = tok.nextToken();   // FS

               // SKIP all the rest!! (col 150+)

   SystemUtils.logErrorToFile("Ending Data Parse - Line " + rcount, club, false);
               /*
               bag = tok.nextToken();      // n
               ghin = tok.nextToken();     // o
               u_hndcp = tok.nextToken();  // p
               c_hndcp = tok.nextToken();  // q
               posid = tok.nextToken();    // s
               mobile = tok.nextToken();   // t
               primary = tok.nextToken();  // u
               active = tok.nextToken();   // v
               */
/*
               //*****TEMP*****
               mship = "Temp";
               //*****TEMP*****


               //
               //  Check for ? (not provided)
               //
               if (memid.equals( "?" )) {

                  memid = "";
               }
               if (mNum.equals( "?" )) {

                  mNum = "";
               }
               if (fname.equals( "?" )) {

                  fname = "";
               }
               if (mi.equals( "?" )) {

                  mi = "";
               }
               if (lname.equals( "?" )) {

                  lname = "";
               }
               if (suffix.equals( "?" )) {

                  suffix = "";
               }
               if (mship.equals( "?" )) {

                  mship = "";
               }
               if (mtype.equals( "?" )) {

                  mtype = "";
               }
               if (gender.equals( "?" ) || (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F"))) {

                  if (gender.equals( "?" )) {
                      genderMissing = true;
                  } else {
                      genderMissing = false;
                  }
                  gender = "";
               }
               if (email.equals( "?" )) {

                  email = "";
               }
               if (email2.equals( "?" )) {

                  email2 = "";
               }
               if (phone.equals( "?" )) {

                  phone = "";
               }
               if (phone2.equals( "?" )) {

                  phone2 = "";
               }
               if (bag.equals( "?" )) {

                  bag = "";
               }
               if (ghin.equals( "?" )) {

                  ghin = "";
               }
               if (u_hndcp.equals( "?" )) {

                  u_hndcp = "";
               }
               if (c_hndcp.equals( "?" )) {

                  c_hndcp = "";
               }
               if (temp.equals( "?" ) || temp.equals( "0" )) {

                  temp = "";
               }
               if (posid.equals( "?" )) {

                  posid = "";
               }
               if (mobile.equals( "?" )) {

                  mobile = "";
               }
               if (primary.equals( "?" )) {

                  primary = "";
               }
               if (active.equals( "?" )) {

                  active = "";
               }

               /*
               //
               //  Ignore mi if not alpha
               //
               if (mi.endsWith( "0" ) || mi.endsWith( "1" ) || mi.endsWith( "2" ) || mi.endsWith( "3" ) ||
                   mi.endsWith( "4" ) || mi.endsWith( "5" ) || mi.endsWith( "6" ) || mi.endsWith( "7" ) ||
                   mi.endsWith( "8" ) || mi.endsWith( "9" )) {

                  mi = "";
               }
               */

/*
               tok = new StringTokenizer( lname, openParen );     // check for open paren '('

               if ( tok.countTokens() > 1 ) {

                  lname = tok.nextToken();                        // skip open paren and anything following it
               }

               tok = new StringTokenizer( lname, slash );         // check for slash

               if ( tok.countTokens() > 1 ) {

                  lname = tok.nextToken();                        // skip them and anything following it
               }

               tok = new StringTokenizer( lname, backslash );         // check for backslash

               if ( tok.countTokens() > 1 ) {

                  lname = tok.nextToken();                        // skip them and anything following it
               }

               tok = new StringTokenizer( fname, openParen );     // check for open paren '('

               if ( tok.countTokens() > 1 ) {

                  fname = tok.nextToken();                        // skip open paren and anything following it
               }

               tok = new StringTokenizer( fname, "/\\" );        // check for slash and backslash

               if ( tok.countTokens() > 1 ) {

                  fname = tok.nextToken();                        // skip them and anything following it
               }


               //
               //  Determine if we should process this record (does it meet the minimum requirements?)
               //
               if (!memid.equals( "" ) && !mNum.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                  //
                  //  Remove spaces, etc. from name fields
                  //
                  tok = new StringTokenizer( fname, " " );     // delimiters are space

                  fname = tok.nextToken();                     // remove any spaces and middle name

                  if ( tok.countTokens() > 0 ) {

                     mi = tok.nextToken();                     // over-write mi if already there

                     if (mi.startsWith("&")) {
                        mi = "";
                     }
                  }

                  if (!suffix.equals( "" )) {                     // if suffix provided

                     tok = new StringTokenizer( suffix, " " );     // delimiters are space

                     suffix = tok.nextToken();                     // remove any extra (only use one value)
                  }

                  tok = new StringTokenizer( lname, " " );     // delimiters are space

                  if ( tok.countTokens() > 0 ) {               // more than just lname?

                     lname = tok.nextToken();                  // remove suffix and spaces
                  }

                  if (suffix.equals( "" )) {                   // if suffix not provided

                     if ( tok.countTokens() > 1 ) {            // check for suffix and 2 part lname (i.e.  Van Buren)

                        lnamePart2 = tok.nextToken();

                        if (!lnamePart2.startsWith("&") && !lnamePart2.startsWith(openParen)) {   // if ok to add

                           lname = lname + lnamePart2;        // combine (i.e. VanBuren)
                        }
                     }

                     if ( tok.countTokens() > 0 ) {           // suffix?

                        suffix = tok.nextToken();

                        if (suffix.startsWith("&")) {
                           suffix = "";
                        }
                     }

                  } else {         // suffix provided in suffix field - check for 2 part lname (i.e.  Van Buren)

                     if ( tok.countTokens() > 0 ) {

                        lnamePart2 = tok.nextToken();

                        if (!lnamePart2.startsWith("&") && !lnamePart2.startsWith("(") && !lnamePart2.equals(suffix)) {   // if ok to add

                           lname = lname + lnamePart2;        // combine (i.e. VanBuren)
                        }
                     }
                  }

                  if (suffix.startsWith(openParen)) {            // if not really a suffix

                     suffix = "";
                  }


                  //
                  //  Isolate the last name in case extra info attached (i.e.  lname..yyyy/nnn)
                  //
                  if (!lname.equals( "" )) {

                     tok = new StringTokenizer( lname, asterik );     // delimiters are slashes, asterics (backslash needs 2)

                     if ( tok.countTokens() > 0 ) {

                        lname = tok.nextToken();                     // isolate lname
                     }

                     tok = new StringTokenizer( lname, slash );

                     if ( tok.countTokens() > 0 ) {

                        lname = tok.nextToken();
                     }

                     tok = new StringTokenizer( lname, backslash );

                     if ( tok.countTokens() > 0 ) {

                        lname = tok.nextToken();
                     }
                  }

                  //
                  //  Append the suffix to last name if it exists and isn't already appended
                  //
                  if (!suffix.equals("")) {

                     if (!lname.endsWith(suffix)) {

                        lname = lname + "_" + suffix;      // append it
                     }

                     suffix = "";                          // done with it now
                  }


                  //
                  //  Determine the handicaps
                  //
                  u_hcap = -99;                    // indicate no hndcp
                  c_hcap = -99;                    // indicate no c_hndcp

                  if (!u_hndcp.equals( "" ) && !u_hndcp.equalsIgnoreCase("NH") && !u_hndcp.equalsIgnoreCase("NHL")) {

                     u_hndcp = u_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                     u_hndcp = u_hndcp.replace('H', ' ');    //         or 'H' if present
                     u_hndcp = u_hndcp.replace('N', ' ');    //         or 'N' if present
                     u_hndcp = u_hndcp.replace('J', ' ');    //         or 'J' if present
                     u_hndcp = u_hndcp.replace('R', ' ');    //         or 'R' if present
                     u_hndcp = u_hndcp.trim();

                     u_hcap = Float.parseFloat(u_hndcp);                   // usga handicap

                     if ((!u_hndcp.startsWith("+")) && (!u_hndcp.startsWith("-"))) {

                        u_hcap = 0 - u_hcap;                       // make it a negative hndcp (normal)
                     }
                  }

                  if (!c_hndcp.equals( "" ) && !c_hndcp.equalsIgnoreCase("NH") && !c_hndcp.equalsIgnoreCase("NHL")) {

                     c_hndcp = c_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
                     c_hndcp = c_hndcp.replace('H', ' ');    //         or 'H' if present
                     c_hndcp = c_hndcp.replace('N', ' ');    //         or 'N' if present
                     c_hndcp = c_hndcp.replace('J', ' ');    //         or 'J' if present
                     c_hndcp = c_hndcp.replace('R', ' ');    //         or 'R' if present
                     c_hndcp = c_hndcp.trim();

                     c_hcap = Float.parseFloat(c_hndcp);                   // usga handicap

                     if ((!c_hndcp.startsWith("+")) && (!c_hndcp.startsWith("-"))) {

                        c_hcap = 0 - c_hcap;                       // make it a negative hndcp (normal)
                     }
                  }

                  //
                  //  convert birth date (mm/dd/yyyy to yyyymmdd)
                  //
                  if (!temp.equals( "" )) {

                     int mm = 0;
                     int dd = 0;
                     int yy = 0;

                     tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                     if ( tok.countTokens() > 2 ) {

                        String b1 = tok.nextToken();
                        String b2 = tok.nextToken();
                        String b3 = tok.nextToken();

                        mm = Integer.parseInt(b1);
                        dd = Integer.parseInt(b2);
                        yy = Integer.parseInt(b3);

                     } else {                               // try 'Jan 20, 1951' format

                        tok = new StringTokenizer( temp, ", " );          // delimiters are comma and space

                        if ( tok.countTokens() > 2 ) {

                           String b1 = tok.nextToken();
                           String b2 = tok.nextToken();
                           String b3 = tok.nextToken();

                           if (b1.startsWith( "Jan" )) {
                              mm = 1;
                           } else {
                            if (b1.startsWith( "Feb" )) {
                               mm = 2;
                            } else {
                             if (b1.startsWith( "Mar" )) {
                                mm = 3;
                             } else {
                              if (b1.startsWith( "Apr" )) {
                                 mm = 4;
                              } else {
                               if (b1.startsWith( "May" )) {
                                  mm = 5;
                               } else {
                                if (b1.startsWith( "Jun" )) {
                                   mm = 6;
                                } else {
                                 if (b1.startsWith( "Jul" )) {
                                    mm = 7;
                                 } else {
                                  if (b1.startsWith( "Aug" )) {
                                     mm = 8;
                                  } else {
                                   if (b1.startsWith( "Sep" )) {
                                      mm = 9;
                                   } else {
                                    if (b1.startsWith( "Oct" )) {
                                       mm = 10;
                                    } else {
                                     if (b1.startsWith( "Nov" )) {
                                        mm = 11;
                                     } else {
                                      if (b1.startsWith( "Dec" )) {
                                         mm = 12;
                                      } else {
                                         mm = Integer.parseInt(b1);
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

                           dd = Integer.parseInt(b2);
                           yy = Integer.parseInt(b3);

                        } else {

                           birth = 0;
                        }
                     }

                     if (mm > 0) {                              // if birth provided

                        if (mm == 1 && dd == 1 && yy == 1) {        // skip if 1/1/0001

                           birth = 0;

                        } else {

                           if (yy < 100) {                          // if 2 digit year

                              if (yy <= thisYear) {

                                 yy += 2000;          // 20xx

                              } else {

                                 yy += 1900;          // 19xx
                              }
                           }

                           birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                           if (yy < 1900) {                             // check for invalid date

                              birth = 0;
                           }
                        }

                     } else {

                        birth = 0;
                     }

                  } else {

                     birth = 0;
                  }

                  password = lname;

                  //
                  //  if lname is less than 4 chars, fill with 1's
                  //
                  int length = password.length();

                  while (length < 4) {

                     password = password + "1";
                     length++;
                  }

                  //
                  //  Verify the email addresses
                  //
                  if (!email.equals( "" )) {      // if specified

                     email = email.trim();           // remove spaces

                     FeedBack feedback = (member.isEmailValid(email));

                     if (!feedback.isPositive()) {    // if error

                        email = "";                   // do not use it
                     }
                  }
                  if (!email2.equals( "" )) {      // if specified

                     email2 = email2.trim();           // remove spaces

                     FeedBack feedback = (member.isEmailValid(email2));

                     if (!feedback.isPositive()) {    // if error

                        email2 = "";                   // do not use it
                     }
                  }

                  // if email #1 is empty then assign email #2 to it
                  if (email.equals("")) email = email2;


                  skip = false;
                  errCount = 0;         // reset the error counter
                  warnCount = 0;        // reset warning counter
                  errMsg = "";          // reset error message
                  warnMsg = "";         // reset warning message
                  errMemInfo = "";      // reset the error member info
                  found = false;        // default to club NOT found


                  //
                  //  Set the active/inactive flag in case it is used
                  //
                  inact = 0;              // default = active

                  if (active.equalsIgnoreCase( "I" )) {

                     inact = 1;           // set inactive
                  }


                  //
                  //  Weed out any non-members
                  //
                  if (fname.equalsIgnoreCase("admin") || lname.equalsIgnoreCase("admin") ||
                      fname.equalsIgnoreCase("test") || lname.equalsIgnoreCase("test")) {

                     inact = 1;       // skip this record
                  }

                  //
                  //  Format member info for use in error logging before club-specific manipulation
                  //
                  errMemInfo = "Member Details:\n" +
                                         "  name: " + lname + ", " + fname + " " + mi + "\n" +
                                         "  mtype: " + mtype + "  mship: " + mship + "\n" +
                                         "  memid: " + memid + "  mNum: " + mNum + "  gender: " + gender;


                  // if gender is incorrect or missing, flag a warning in the error log
                  if (gender.equals("")) {
                      warnCount++;
                      if (genderMissing) {
                          warnMsg = warnMsg + "\n" +
                                  "  -GENDER missing! (Defaulted to 'M')";
                      } else {
                          warnMsg = warnMsg + "\n" +
                                  "  -GENDER incorrect! (Defaulted to 'M')";
                      }
                      gender = "M";
                  }

                  //
                  //  Skip entries with no membership type
                  //
                  if (mship.equals("")) {
                      errCount++;
                      skip = true;
                      errMsg = errMsg + "\n" +
                              "  -MEMBERSHIP TYPE missing!";
                  }

                  //
                  //  Skip entries with first/last names of 'admin'
                  //
                  if (fname.equalsIgnoreCase("admin") || lname.equalsIgnoreCase("admin")) {
                      errCount++;
                      skip = true;
                      errMsg = errMsg + "\n" +
                              "  -INVALID NAME! 'Admin' or 'admin' not allowed for first or last name";
                  }

                  //
                  //  Make sure the member is not inactive - skip it it is
                  //
                  if (inact == 0) {     // if active

                     //
                     // *********************************************************************
                     //
                     //   The following will be dependent on the club - customized
                     //
                     // *********************************************************************
                     //
                     //******************************************************************
                     //   Royal Oaks CC, Houston - royaloakscc
                     //******************************************************************
                     //
                     if (club.equals("testJonasSync")) {

                         found = true;      // club found

                         if (mship.equals( "" )) {

                             skip = true;       //  skip this one
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBERSHIP TYPE missing!";

                         } else if (mNum.equals( "" )) {

                             skip = true;                          //  skip this one
                             warnCount++;
                             warnMsg = warnMsg + "\n" +
                                     "  -SKIPPED: Member Number Missing!";

                         } else {

                             int mshipInt = 0;

                             // Strip all leading zeroes from member number
                             while (mNum.startsWith("0")) {
                                 mNum.substring(1);
                             }

                             memid = mNum;

                             posid = mNum;         // set posid in case we need it in the future

                             if (mNum.endsWith("A") || mNum.endsWith("B") || mNum.endsWith("C") || mNum.endsWith("D") || mNum.endsWith("E") ||
                                 mNum.endsWith("F") || mNum.endsWith("C")) {

                                 if (!mNum.endsWith("A")) {        // If not spouse
                                     mtype = "Dependent";
                                 }

                                 mNum = mNum.substring(0, mNum.length() - 1);    // Remove trailing alpha char from mNum
                             }

                             if (gender.equalsIgnoreCase("F") && !mtype.equals("Dependent")) {
                                 mtype = "Adult Female";
                             } else if (!mtype.equals("Dependent")) {
                                 mtype = "Adult Male";
                             }


                             try {
                                 mshipInt = Integer.parseInt(mNum);
                             } catch (Exception exc) {
                                 skip = true;
                                 warnCount++;
                                 warnMsg = warnMsg + "\n" +
                                         "  -SKIPPED: Invalid Member Number!";
                             }

                             if (!skip) {
                                 if (mshipInt >= 0 && mshipInt < 200) {
                                     mship = "Honorary";
                                 } else if (mshipInt >= 500 && mshipInt < 600) {
                                     mship = "Executive Honorary";
                                 } else if (mshipInt >= 1000 && mshipInt < 2000) {
                                     mship = "Golf";
                                 } else if (mshipInt >= 2000 && mshipInt < 3000) {
                                     mship = "Executive";
                                 } else if (mshipInt >= 3000 && mshipInt < 3400) {
                                     mship = "Sports Club w/Golf";
                                 } else if (mshipInt >= 5000 && mshipInt < 5400) {
                                     mship = "Preview Golf";
                                 } else if (mshipInt >= 5400 && mshipInt < 5700) {
                                     mship = "Preview Executive";
                                 } else if (mshipInt >= 7000 && mshipInt < 7500) {
                                     mship = "Sampler Golf";
                                 } else if (mshipInt >= 7500 && mshipInt < 8000) {
                                     mship = "Sampler Executive";
                                 } else {
                                     skip = true;
                                     warnCount++;
                                     warnMsg = warnMsg + "\n" +
                                             "  -SKIPPED: NON-GOLF or UNKNOWN MEMBERSHIP TYPE!";
                                 }
                             }
                         }
                     }  // end if royaloakscc

                     //
                     //******************************************************************
                     //  Common processing - add or update the member record
                     //******************************************************************
                     //
                     if (skip == false && found == true && !fname.equals("") && !lname.equals("") && !memid.equals("")) {

                        //
                        //   now determine if we should update an existing record or add the new one
                        //
                        memid_old = "";
                        fname_old = "";
                        lname_old = "";
                        mi_old = "";
                        mship_old = "";
                        mtype_old = "";
                        email_old = "";
                        mNum_old = "";
                        ghin_old = "";
                        bag_old = "";
                        posid_old = "";
                        email2_old = "";
                        phone_old = "";
                        phone2_old = "";
                        suffix_old = "";
                        u_hcap_old = 0;
                        c_hcap_old = 0;
                        birth_old = 0;


                        //
                        //  Truncate the string values to avoid sql error
                        //
                        if (!mi.equals( "" )) {       // if mi specified

                           mi = truncate(mi, 1);           // make sure it is only 1 char
                        }
                        if (!memid.equals( "" )) {

                           memid = truncate(memid, 15);
                        }
                        if (!password.equals( "" )) {

                           password = truncate(password, 15);
                        }
                        if (!lname.equals( "" )) {

                           lname = truncate(lname, 20);
                        }
                        if (!fname.equals( "" )) {

                           fname = truncate(fname, 20);
                        }
                        if (!mship.equals( "" )) {

                           mship = truncate(mship, 30);
                        }
                        if (!mtype.equals( "" )) {

                           mtype = truncate(mtype, 30);
                        }
                        if (!email.equals( "" )) {

                           email = truncate(email, 50);
                        }
                        if (!email2.equals( "" )) {

                           email2 = truncate(email2, 50);
                        }
                        if (!mNum.equals( "" )) {

                           mNum = truncate(mNum, 10);
                        }
                        if (!ghin.equals( "" )) {

                           ghin = truncate(ghin, 16);
                        }
                        if (!bag.equals( "" )) {

                           bag = truncate(bag, 12);
                        }
                        if (!posid.equals( "" )) {

                           posid = truncate(posid, 15);
                        }
                        if (!phone.equals( "" )) {

                           phone = truncate(phone, 24);
                        }
                        if (!phone2.equals( "" )) {

                           phone2 = truncate(phone2, 24);
                        }
                        if (!suffix.equals( "" )) {

                           suffix = truncate(suffix, 4);
                        }
                        if (!webid.equals( "" )) {

                           webid = truncate(webid, 15);
                        }

                        //
                        //  Use try/catch here so processing will continue on rest of file if it fails
                        //
                        try {

                           if (useWebid == false) {            // use webid to locate member?

                              pstmt2 = con.prepareStatement (
                                       "SELECT * FROM member2b WHERE username = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, memid);

                           } else {                            // use webid

                              pstmt2 = con.prepareStatement (
                                       "SELECT * FROM member2b WHERE webid = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, webid);
                           }

                           rs = pstmt2.executeQuery();            // execute the prepared stmt

                           if(rs.next()) {

                              memid_old = rs.getString("username");            // get username in case we used webid
                              lname_old = rs.getString("name_last");
                              fname_old = rs.getString("name_first");
                              mi_old = rs.getString("name_mi");
                              mship_old = rs.getString("m_ship");
                              mtype_old = rs.getString("m_type");
                              email_old = rs.getString("email");
                              mNum_old = rs.getString("memNum");
                              ghin_old = rs.getString("ghin");
                              bag_old = rs.getString("bag");
                              birth_old = rs.getInt("birth");
                              posid_old = rs.getString("posid");
                              email2_old = rs.getString("email2");
                              phone_old = rs.getString("phone1");
                              phone2_old = rs.getString("phone2");
                              suffix_old = rs.getString("name_suf");
                              inact_old = rs.getInt("inact");

                           }
                           pstmt2.close();              // close the stmt


                           boolean memFound = false;
                           boolean dup = false;
                           boolean userChanged = false;
                           boolean nameChanged = false;
                           String dupuser = "";
                           String dupmnum = "";
                           String dupwebid = "";

                           webid_new = webid;                       // default

                           /*
                           if (!memid.equals("") && !memid.equals(memid_old)) {

                               // memid has changed!  Update the username
                               memid_new = memid;
                               userChanged = true;

                           } else {
                               memid_new = memid_old;       // Don't change for old clubs
                           }
                           */
/*
                           //
                           //  If member NOT found, then check if new member OR id has changed
                           //
                           if (fname_old.equals( "" )) {            // if member NOT found

                              //
                              //  New member - first check if name already exists
                              //
                              pstmt2 = con.prepareStatement (
                                       "SELECT username, memNum, webid FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                              pstmt2.clearParameters();
                              pstmt2.setString(1, lname);
                              pstmt2.setString(2, fname);
                              pstmt2.setString(3, mi);
                              rs = pstmt2.executeQuery();            // execute the prepared stmt

                              if (rs.next()) {

                                 dupuser = rs.getString("username");          // get this username
                                 dupmnum = rs.getString("memNum");
                                 dupwebid = rs.getString("webid");            // get this webid

                                 //
                                 //  name already exists - see if this is the same member
                                 //
                                 if (!dupmnum.equals( "" ) && dupmnum.equals( mNum )) {   // if name and mNum match, then memid or webid must have changed

                                    if (useWebid == true) {            // use webid to locate member?

                                       webid_new = webid;                  // set new ids
                                       memid_new = dupuser;
                                       memid_old = dupuser;                    // update this record

                                    } else {

                                       webid_new = dupwebid;                  // set new ids
                                       memid_new = memid;
                                       memid_old = dupuser;                       // update this record
                                       userChanged = true;                    // indicate the username has changed
                                    }

                                    memFound = true;                      // update the member

                                 } else {

                                    dup = true;        // dup member - do not add
                                 }

                              }
                              pstmt2.close();              // close the stmt

                           } else {      // member found

                              memFound = true;
                           }

                           //
                           //  Now, update the member record if existing member
                           //
                           if (memFound == true) {                   // if member exists

                              changed = false;                       // init change indicator

                              lname_new = lname_old;

                              if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                                 lname_new = lname;         // set value from CE record
                                 changed = true;
                                 nameChanged = true;
                              }

                              fname_new = fname_old;

                              if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                                 fname_new = fname;         // set value from CE record
                                 changed = true;
                                 nameChanged = true;
                              }

                              mi_new = mi_old;

                              if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                                 mi_new = mi;         // set value from CE record
                                 changed = true;
                                 nameChanged = true;
                              }

                              mship_new = mship_old;

                              if (!mship.equals( "" ) && !mship_old.equals( mship )) {

                                 mship_new = mship;         // set value from CE record
                                 changed = true;
                              }

                              mtype_new = mtype_old;

                              if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {

                                 mtype_new = mtype;         // set value from CE record
                                 changed = true;
                              }

                              birth_new = birth_old;

                              if (birth > 0 && birth != birth_old) {

                                 birth_new = birth;         // set value from CE record
                                 changed = true;
                              }

                              ghin_new = ghin_old;

                              if (!ghin.equals( "" ) && !ghin_old.equals( ghin )) {

                                 ghin_new = ghin;         // set value from CE record
                                 changed = true;
                              }

                              bag_new = bag_old;

                              if (!bag.equals( "" ) && !bag_old.equals( bag )) {

                                 bag_new = bag;         // set value from CE record
                                 changed = true;
                              }

                              posid_new = posid_old;

                              if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                                 posid_new = posid;         // set value from CE record
                                 changed = true;
                              }

                              phone_new = phone_old;

                              if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                                 phone_new = phone;         // set value from CE record
                                 changed = true;
                              }

                              phone2_new = phone2_old;

                              if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                                 phone2_new = phone2;         // set value from CE record
                                 changed = true;
                              }

                              suffix_new = suffix_old;

                              if (!suffix.equals( "" ) && !suffix_old.equals( suffix )) {

                                 suffix_new = suffix;         // set value from CE record
                                 changed = true;
                              }

                              email_new = email_old;

                              if (!email.equals( "" ) && !email_old.equals( email )) {

                                 email_new = email;         // set value from CE record
                                 changed = true;
                              }

                              email2_new = email2_old;

                              if (!email2.equals( "" ) && !email2_old.equals( email2 )) {

                                 email2_new = email2;         // set value from CE record
                                 changed = true;
                              }

                              // don't allow both emails to be the same
                              if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";


                              if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                                  mNum_new = mNum;         // set value from CE record
                                  changed = true;
                              }

                              inact_new = inact_old;                // do not change inact status for most clubs

                              if (club.equals( "congressional" )) {     // change status for Congressional

                                 if (inact_new != inact) {             // if status has changed

                                    inact_new = inact;                 // set value from CE record
                                    changed = true;
                                 }
                              }


                              if (club.equals( "benttreecc" )) {          // special processing for Bent Tree

                                 String tempM = remZeroS(mNum_old);       // strip alpha from our old mNum

                                 if ((tempM.startsWith("5") || tempM.startsWith("7")) && inact_old == 1) {

                                    //  If our mNum contains an old inactive value and the member is inactive
                                    //  then set him active and let mNum change (above).

                                    inact_new = inact;       // set value from CE record (must be active to get this far)
                                 }
                              }


                              //
                              //  Update our record if something has changed
                              //
                              pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                              "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                              "memNum = ?, ghin = ?, bag = ?, birth = ?, posid = ?, email2 = ?, phone1 = ?, " +
                              "phone2 = ?, name_suf = ?, webid = ?, inact = ?, last_sync_date = now(), gender = ? " +
                              "WHERE username = ?");

                              pstmt2.clearParameters();        // clear the parms
                              pstmt2.setString(1, memid_new);
                              pstmt2.setString(2, lname_new);
                              pstmt2.setString(3, fname_new);
                              pstmt2.setString(4, mi_new);
                              pstmt2.setString(5, mship_new);
                              pstmt2.setString(6, mtype_new);
                              pstmt2.setString(7, email_new);
                              pstmt2.setString(8, mNum_new);
                              pstmt2.setString(9, ghin_new);
                              pstmt2.setString(10, bag_new);
                              pstmt2.setInt(11, birth_new);
                              pstmt2.setString(12, posid_new);
                              pstmt2.setString(13, email2_new);
                              pstmt2.setString(14, phone_new);
                              pstmt2.setString(15, phone2_new);
                              pstmt2.setString(16, suffix_new);
                              pstmt2.setString(17, webid_new);
                              pstmt2.setInt(18, inact_new);
                              pstmt2.setString(19, gender);
                              pstmt2.setString(20, memid_old);
                              pstmt2.executeUpdate();

                              pstmt2.close();              // close the stmt

                              ucount++;                    // count records updated

                              //
                              //  Member updated - now see if the username or name changed
                              //
                              if (userChanged == true || nameChanged == true) {        // if username or name changed

                                 //
                                 //  username or name changed - we must update other tables now
                                 //
                                 StringBuffer mem_name = new StringBuffer( fname_new );       // get the new first name

                                 if (!mi_new.equals( "" )) {
                                    mem_name.append(" " +mi_new);               // new mi
                                 }
                                 mem_name.append(" " +lname_new);               // new last name

                                 String newName = mem_name.toString();          // convert to one string

                                 Admin_editmem.updTeecurr(newName, memid_new, memid_old, con);      // update teecurr with new values

                                 Admin_editmem.updTeepast(newName, memid_new, memid_old, con);      // update teepast with new values

                                 Admin_editmem.updLreqs(newName, memid_new, memid_old, con);        // update lreqs with new values

                                 Admin_editmem.updPartner(memid_new, memid_old, con);               // update partner with new values

                                 Admin_editmem.updEvents(newName, memid_new, memid_old, con);        // update evntSignUp with new values

                                 Admin_editmem.updLessons(newName, memid_new, memid_old, con);       // update the lesson books with new values
                              }


                           } else {       // member NOT found


                              if (dup == false && !fname.equals("") && !lname.equals("")) {          // if name does not already exist

                                 //
                                 //  New member - add it
                                 //
                                 pstmt2 = con.prepareStatement (
                                    "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                                    "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                                    "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, " +
                                    "webid, last_sync_date, gender) " +
                                    "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,?,now(),?)");

                                 pstmt2.clearParameters();        // clear the parms
                                 pstmt2.setString(1, memid);        // put the parm in stmt
                                 pstmt2.setString(2, password);
                                 pstmt2.setString(3, lname);
                                 pstmt2.setString(4, fname);
                                 pstmt2.setString(5, mi);
                                 pstmt2.setString(6, mship);
                                 pstmt2.setString(7, mtype);
                                 pstmt2.setString(8, email);
                                 pstmt2.setFloat(9, c_hcap);
                                 pstmt2.setFloat(10, u_hcap);
                                 pstmt2.setString(11, mNum);
                                 pstmt2.setString(12, ghin);
                                 pstmt2.setString(13, bag);
                                 pstmt2.setInt(14, birth);
                                 pstmt2.setString(15, posid);
                                 pstmt2.setString(16, email2);
                                 pstmt2.setString(17, phone);
                                 pstmt2.setString(18, phone2);
                                 pstmt2.setString(19, suffix);
                                 pstmt2.setString(20, webid);
                                 pstmt2.setString(21, gender);
                                 pstmt2.executeUpdate();          // execute the prepared stmt

                                 pstmt2.close();              // close the stmt

                                 ncount++;                    // count records added (new)

                              } else if (dup) {
                                  errCount++;
                                  errMsg = errMsg + "\n  -Dup user found:\n" +
                                          "    new: memid = " + memid + "  :  cur: " + dupuser + "\n" +
                                          "         webid = " + webid + "  :       " + dupwebid + "\n" +
                                          "         mNum  = " + mNum  + "  :       " + dupmnum;
                              }
                           }

                           pcount++;          // count records processed (not skipped)


                        }
                        catch (Exception e3b) {
                           errCount++;
                           errMsg = errMsg + "\n  -Error2 processing roster (record #" +rcount+ ") for " +club+ "\n" +
                                   "    line = " +line+ ": " + e3b.getMessage();   // build msg
                        }

                     } else {

                         // Only report errors that AREN'T due to skip == true, since those were handled earlier!
                         if (!found) {
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -MEMBER NOT FOUND!";
                         }
                         if (fname.equals("")) {
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -FIRST NAME missing!";
                         }
                         if (lname.equals("")) {
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -LAST NAME missing!";
                         }
                         if (memid.equals("")) {
                             errCount++;
                             errMsg = errMsg + "\n" +
                                     "  -USERNAME missing!";
                         }

                     }   // end of IF skip

                  }   // end of IF inactive

               }   // end of IF minimum requirements

            }   // end of IF tokens

         }   // end of IF header row

         // log any errors and warnings that occurred
         if (errCount > 0) {
             totalErrCount += errCount;
             errList.add(errMemInfo + "\n  *" + errCount + " error(s) found*" + errMsg + "\n");
         }
         if (warnCount > 0) {
             totalWarnCount += warnCount;
             warnList.add(errMemInfo + "\n  *" + warnCount + " warning(s) found*" + warnMsg + "\n");
         }
      }   // end of while

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {       // if we processed this club

         pstmt2 = con.prepareStatement (
           "UPDATE member2b SET inact = 1 " +
           "WHERE last_sync_date != now() AND last_sync_date != '0000-00-00'");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.executeUpdate();

         pstmt2.close();              // close the stmt
  
 
         //
         //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
         //
         setRSind(con, club);
      }
   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster (record #" +rcount+ ") for " +club+ ", line = " +line+ ": " + e3.getMessage() + "\n";   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
      errorMsg = "Error in Common_sync.jonasSync: ";            // reset msg
   }

   // Print error and warning count totals to error log
   SystemUtils.logErrorToFile("" +
           "Total Errors Found: " + totalErrCount + "\n" +
           "Total Warnings Found: " + totalWarnCount + "\n", club, true);

   // Print errors and warnings to error log
   if (totalErrCount > 0) {
       SystemUtils.logErrorToFile("" +
            "********************************************************************\n" +
            "****ERRORS FOR " + club + " (Member WAS NOT synced!)\n" +
            "********************************************************************\n", club, true);
       while (errList.size() > 0) {
           SystemUtils.logErrorToFile(errList.remove(0), club, true);
       }
   }
   if (totalWarnCount > 0) {
       SystemUtils.logErrorToFile("" +
            "********************************************************************\n" +
            "****WARNINGS FOR " + club + " (Member MAY NOT have synced!)\n" +
            "********************************************************************\n", club, true);
       while (warnList.size() > 0) {
           SystemUtils.logErrorToFile(warnList.remove(0), club, true);
       }
   }

   //  TEMP!!!!
   if (club.equals("berkeleyhall")) {

      errorMsg = " Jonas sync complete. Records = " +rcount+ " for " +club+ ", records processed = " +pcount+ ", records added = " +ncount+ ", records updated = " +ucount + "\n";   // build msg
      SystemUtils.logErrorToFile(errorMsg, club, true);                                                  // log it
   }

   // Print end time to error log
   SystemUtils.logErrorToFile("End time: " + new java.util.Date().toString() + "\n", club, true);

 }

/*
 private static void clubSoftSync(Connection con, InputStreamReader isr, String club) {


   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;
   int inact = 0;
   int pri_indicator = 0;

   // Values from MFirst records
   //
   String fname = "";
   String fname2 = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String msub_type = "";
   String bag = "";
   String wc = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String mobile = "";
   String primary = "";
   String webid = "";
   String status = "";
   String custom1 = "";
   String custom2 = "";
   String custom3 = "";

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;

   // Values from ForeTees records
   //
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";
   String msub_type_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;

   // Values for New ForeTees records
   //
   String memid_new = "";
   String webid_new = "";
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";
   String msub_type_new = "";
   String dupuser = "";
   String dupwebid = "";
   String dupmnum = "";
   String emailMF = "support@memfirst.com";
   String subject = "Roster Sync Warning from ForeTees for " +club;

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int errCount = 0;
   int warnCount = 0;
   int totalErrCount = 0;
   int totalWarnCount = 0;

   String errorMsg = "";
   String errMemInfo = "";
   String errMsg = "";
   String warnMsg = "";
   String emailMsg1 = "Duplicate names found in MembersFirst file during ForeTees Roster Sync processing for club: " +club+ ".\n\n";
   String emailMsg2 = "\nThis indicates that either 2 members have the exact same names (not allowed), or MF's member id has changed.\n\n";

   ArrayList<String> errList = new ArrayList<String>();
   ArrayList<String> warnList = new ArrayList<String>();

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean found = false;
   boolean useWebid = false;
   boolean sendemail = false;
   boolean genderMissing = false;



   // Overwrite log file with fresh one for today's logs
   SystemUtils.logErrorToFile("Members First: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //   format of each line in the file:
      //
      //     memid, mNum, fname, mi, lname, suffix, mship, mtype, gender, email, email2,
      //     phone, phone2, bag, hndcp#, uhndcp, chndcp, birth, posid, mobile, primary
      //
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         //  Remove the dbl quotes and check for embedded commas

         line = cleanRecord3( line );

         //  parse the line to gather all the info

         StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

         if ( tok.countTokens() > 22 ) {     // enough data ?

            memid = tok.nextToken();
            fname = tok.nextToken();
            lname = tok.nextToken();
            mi = tok.nextToken();
            tok.nextToken();  // skip nickname field
            tok.nextToken();  // skip salutation field
            gender = tok.nextToken();
            temp = tok.nextToken();
            fname2 = tok.nextToken();
            tok.nextToken();  // skip join date field
            status = tok.nextToken();    // active/inactive
            email = tok.nextToken();
            tok.nextToken();  // skip username field
            tok.nextToken();  // skip password field
            mship = tok.nextToken();
            wc = tok.nextToken();
            bag = tok.nextToken();
            ghin = tok.nextToken();
            tok.nextToken();  // skip street1 field
            tok.nextToken();  // skip street2 field
            tok.nextToken();  // skip citystate field
            tok.nextToken();  // skip zip field
            phone = tok.nextToken();

            if ( tok.countTokens() > 0 ) {

               custom1 = tok.nextToken();
            }
            if ( tok.countTokens() > 0 ) {

               custom2 = tok.nextToken();
            }
            if ( tok.countTokens() > 0 ) {

               custom3 = tok.nextToken();
            }


            //   trim gender in case followed be spaces
            gender = gender.trim();

            //
            //  Check for ? (not provided)
            //
            if (memid.equals( "?" )) {

               memid = "";
            }
            if (mNum.equals( "?" )) {

               mNum = "";
            }
            if (fname.equals( "?" )) {

               fname = "";
            }
            if (mi.equals( "?" )) {

               mi = "";
            }
            if (lname.equals( "?" )) {

               lname = "";
            }
            if (suffix.equals( "?" )) {

               suffix = "";
            }
            if (mship.equals( "?" )) {

               mship = "";
            }
            if (mtype.equals( "?" )) {

               mtype = "";
            }
            if (gender.equals( "?" ) || (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F"))) {

                  if (gender.equals( "?" )) {
                      genderMissing = true;
                  } else {
                      genderMissing = false;
                  }
                  gender = "";
               }
            if (email.equals( "?" )) {

               email = "";
            }
            if (email2.equals( "?" )) {

               email2 = "";
            }
            if (phone.equals( "?" )) {

               phone = "";
            }
            if (phone2.equals( "?" )) {

               phone2 = "";
            }
            if (bag.equals( "?" )) {

               bag = "";
            }
            if (ghin.equals( "?" )) {

               ghin = "";
            }
            if (u_hndcp.equals( "?" )) {

               u_hndcp = "";
            }
            if (c_hndcp.equals( "?" )) {

               c_hndcp = "";
            }
            if (temp.equals( "?" )) {

               birth = 0;

            } else {

               birth = Integer.parseInt(temp);
            }
            if (posid.equals( "?" )) {

               posid = "";
            }
            if (mobile.equals( "?" )) {

               mobile = "";
            }
            if (primary.equals( "?" )) {

               primary = "";
            }

         }     // end of if token count
      }    // end of while loop
   } catch (Exception exc) {

         }
*/
 }

 // *********************************************************
 //  Tualatin CC - check birth dates of juniors to see if mtype should change
 // *********************************************************

 private final static void checkTualatin(Connection con) {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    String user = "";
    String mtype= "";
    String mtypeNew = "";
    String mtype1 = "Primary Male";
    String mtype2 = "Spouse Male";
    String mtype3 = "Primary Female";
    String mtype4 = "Spouse Female";

    int birth = 0;

    //
    //   Get current date
    //
    Calendar cal = new GregorianCalendar();        // get todays date
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH)+1;
    int day = cal.get(Calendar.DAY_OF_MONTH);

    year = year - 18;                              // date to determine if < 18 yrs old

    int date1 = (year * 10000) + (month * 100) + day;

    year = year - 6;                               // date to determine if < 24 yrs old

    int date2 = (year * 10000) + (month * 100) + day;


    //
    //  Check each Junior to see if the mtype should be changed
    //
    try {

      pstmt = con.prepareStatement (
               "SELECT username, m_type, birth FROM member2b " +
               "WHERE m_type != ? AND m_type != ? AND m_type != ? AND m_type != ? AND birth != 0");

      pstmt.clearParameters();
      pstmt.setString(1, mtype1);
      pstmt.setString(2, mtype2);
      pstmt.setString(3, mtype3);
      pstmt.setString(4, mtype4);
      rs = pstmt.executeQuery();

      while (rs.next()) {

         user = rs.getString(1);
         mtype = rs.getString(2);
         birth = rs.getInt(3);

         mtypeNew = "";

         if (birth > date1) {                     // if < 17 yrs old

            if (mtype.endsWith("Female")) {

               mtypeNew = "Junior 17 Female";

            } else {

               mtypeNew = "Junior 17 Male";
            }

         } else {

            if (birth > date2) {                  // if < 24 yrs old

               if (mtype.endsWith("Female")) {

                  mtypeNew = "Junior 18-23 Female";

               } else {

                  mtypeNew = "Junior 18-23 Male";
               }
            }
         }

         //
         //  Update the mtype if it has changed
         //
         if (!mtypeNew.equals(mtype)) {

            pstmt2 = con.prepareStatement (
              "UPDATE member2b SET m_type = ? " +
              "WHERE username = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt.setString(1, mtypeNew);
            pstmt.setString(2, user);
            pstmt2.executeUpdate();

            pstmt2.close();              // close the stmt
         }
      }

      pstmt.close();

   }
   catch (Exception exc) {
   }

 }


 // *********************************************************
 //  Interlachen CC - check birth dates of juniors to see if mtype should change
 // *********************************************************

 private final static void checkInterlachen(Connection con) {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    String user = "";
    String mtype= "";
    String mtypeNew = "";
    String mtype1 = "Jr Ages 6 - 11";
    String mtype2 = "Jr Ages 12 - 15";
    String mtype3 = "Jr Ages 16 - 24";

    int birth = 0;
    int inact = 0;

    //
    //   Get current date
    //
    Calendar cal = new GregorianCalendar();        // get todays date
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH)+1;
    int day = cal.get(Calendar.DAY_OF_MONTH);

    year = year - 12;                              // date to determine if < 12 yrs old

    int date12 = (year * 10000) + (month * 100) + day;

    year = year - 4;                               // date to determine if < 16 yrs old

    int date16 = (year * 10000) + (month * 100) + day;

    year = year - 9;                               // date to determine if < 25 yrs old

    int date25 = (year * 10000) + (month * 100) + day;



    //
    //  Check each Junior to see if the mtype should be changed
    //
    try {

      pstmt = con.prepareStatement (
               "SELECT username, m-type, birth, inact FROM member2b " +
               "WHERE m_type = ? OR m_type = ? OR m_type = ? AND birth != 0 AND inact = 0");

      pstmt.clearParameters();
      pstmt.setString(1, mtype1);
      pstmt.setString(2, mtype2);
      pstmt.setString(3, mtype3);
      rs = pstmt.executeQuery();

      while (rs.next()) {

         user = rs.getString(1);
         mtype = rs.getString(2);
         birth = rs.getInt(3);
         inact = rs.getInt(4);

         mtypeNew = mtype;

         if (birth > date12) {                     // if < 12 yrs old

            mtypeNew = mtype1;                     // 6 - 11

         } else {

            if (birth > date16) {                  // if < 16 yrs old

               mtypeNew = mtype2;                  // 12 - 15

            } else {

               if (birth > date25) {               // if < 25 yrs old

                  mtypeNew = mtype3;               // 16 - 24

               } else {

                  inact = 1;                       // older than 24, set inactive
               }
            }
         }

         //
         //  Update the record if mtype has changed or we are setting member inactive
         //
         if (!mtypeNew.equals(mtype) || inact == 1) {

            pstmt2 = con.prepareStatement (
              "UPDATE member2b SET m_type = ?, inact = ? " +
              "WHERE username = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt.setString(1, mtypeNew);
            pstmt.setInt(2, inact);
            pstmt.setString(3, user);
            pstmt2.executeUpdate();

            pstmt2.close();              // close the stmt
         }
      }

      pstmt.close();

   }
   catch (Exception exc) {
   }

 }


 // *********************************************************
 //  Mount Vernon CC - check birth dates of juniors to see if mtype should change
 // *********************************************************

 private final static void checkMountVernon(Connection con) {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    String user = "";
    String mtype= "";
    String mtypeNew = "";
    String mtype1 = "Dependent Under 12";
    String mtype2 = "Dependent 12-17";
    String mtype3 = "Dependent 18 and Up";

    int birth = 0;
    int inact = 0;

    //
    //   Get current date
    //
    Calendar cal = new GregorianCalendar();        // get todays date
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH)+1;
    int day = cal.get(Calendar.DAY_OF_MONTH);

    year = year - 12;                              // date to determine if < 12 yrs old

    int date12 = (year * 10000) + (month * 100) + day;

    year = year - 6;                               // date to determine if < 18 yrs old

    int date18 = (year * 10000) + (month * 100) + day;



    //
    //  Check each Junior to see if the mtype should be changed
    //
    try {

      pstmt = con.prepareStatement (
               "SELECT username, m-type, birth FROM member2b " +
               "WHERE m_type = ? OR m_type = ? OR m_type = ? AND birth != 0 AND inact = 0");

      pstmt.clearParameters();
      pstmt.setString(1, mtype1);
      pstmt.setString(2, mtype2);
      pstmt.setString(3, mtype3);
      rs = pstmt.executeQuery();

      while (rs.next()) {

         user = rs.getString(1);
         mtype = rs.getString(2);
         birth = rs.getInt(3);

         mtypeNew = mtype;

         if (birth > date12) {                     // if < 12 yrs old

            mtypeNew = mtype1;                     // Under 12

         } else {

            if (birth > date18) {                  // if < 18 yrs old

               mtypeNew = mtype2;                  // 12 - 18

            } else {

               mtypeNew = mtype3;                  // 18 and Up
            }
         }

         //
         //  Update the record if mtype has changed
         //
         if (!mtypeNew.equals(mtype)) {

            pstmt2 = con.prepareStatement (
              "UPDATE member2b SET m_type = ? " +
              "WHERE username = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt.setString(1, mtypeNew);
            pstmt.setString(2, user);
            pstmt2.executeUpdate();

            pstmt2.close();              // close the stmt
         }
      }

      pstmt.close();

   }
   catch (Exception exc) {
   }

 }


 // *********************************************************
 //  Hyperion Field Club - check birth dates of juniors to see if mtype should change
 // *********************************************************

 private final static void checkHyperion(Connection con) {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    String user = "";
    String mtype= "";
    String mtypeNew = "";
    String mtype1 = "Jr 6 to 7";
    String mtype2 = "Jr 8 to 11";
    String mtype3 = "Jr 12 to 15";
    String mtype4 = "Jr 16 to 22";

    int birth = 0;
    int inact = 0;

    //
    //   Get current date
    //
    Calendar cal = new GregorianCalendar();        // get todays date
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH)+1;
    int day = cal.get(Calendar.DAY_OF_MONTH);

    year = year - 8;                              // date to determine if < 8 yrs old

    int date8 = (year * 10000) + (month * 100) + day;

    year = year - 4;                              // date to determine if < 12 yrs old

    int date12 = (year * 10000) + (month * 100) + day;

    year = year - 4;                              // date to determine if < 16 yrs old

    int date16 = (year * 10000) + (month * 100) + day;

    year = year - 7;                               // date to determine if < 23 yrs old

    int date23 = (year * 10000) + (month * 100) + day;



    //
    //  Check each Junior to see if the mtype should be changed
    //
    try {

      pstmt = con.prepareStatement (
               "SELECT username, birth FROM member2b " +
               "WHERE birth != 0 AND inact = 0");

      pstmt.clearParameters();
      rs = pstmt.executeQuery();

      while (rs.next()) {

         user = rs.getString(1);
         birth = rs.getInt(2);

         mtypeNew = "";

         if (birth > date8) {                   // if < 8 yrs old

            mtypeNew = mtype1;                  // Under 8

         } else if (birth > date12) {           // if < 12 yrs old

            mtypeNew = mtype2;                  // 8 - 11

         } else if (birth > date16) {           // if < 16 yrs old

            mtypeNew = mtype3;                  // 12 - 15

         } else if (birth > date23) {           // if < 23 yrs old

            mtypeNew = mtype4;                  // 16 - 22
         }

         //
         //  Update the record if mtype has changed
         //
         if (!mtypeNew.equals( "" )) {

            pstmt2 = con.prepareStatement (
              "UPDATE member2b SET m_type = ? " +
              "WHERE username = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt.setString(1, mtypeNew);
            pstmt.setString(2, user);
            pstmt2.executeUpdate();

            pstmt2.close();              // close the stmt
         }
      }

      pstmt.close();

   }
   catch (Exception exc) {
   }

 }
 // *********************************************************
 //  Hyperion Field Club - check birth dates of juniors to see if mtype should change
 // *********************************************************

 
 
 private final static void checkTahoeDonner(Connection con) {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    String user = "";
    String mtype= "";
    String mtypeNew = "";
    String gender = "";

    int birth = 0;
    int inact = 0;

    //
    //   Get current date
    //
    Calendar cal = new GregorianCalendar();        // get todays date
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH)+1;
    int day = cal.get(Calendar.DAY_OF_MONTH);

    year = year - 18;                              // date to determine if < 18 yrs old

    int date18 = (year * 10000) + (month * 100) + day;

    //
    //  Check each Junior to see if the mtype should be changed
    //
    try {

      pstmt = con.prepareStatement (
               "SELECT username, birth, gender FROM member2b " +
               "WHERE birth != 0 AND inact = 0");

      pstmt.clearParameters();
      rs = pstmt.executeQuery();

      while (rs.next()) {

         user = rs.getString(1);
         birth = rs.getInt(2);
         gender = rs.getString("gender");

         mtypeNew = "";

         if (birth < date18) {                   // if > 18 yrs old

             if (gender.equalsIgnoreCase("F")) {
                 mtypeNew = "Adult Female";
             } else {
                 mtypeNew = "Adult Male";
             }
         } 

         //
         //  Update the record if mtype has changed
         //
         if (!mtypeNew.equals( "" )) {

            pstmt2 = con.prepareStatement (
              "UPDATE member2b SET m_type = ? " +
              "WHERE username = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt.setString(1, mtypeNew);
            pstmt.setString(2, user);
            pstmt2.executeUpdate();

            pstmt2.close();              // close the stmt
         }
      }

      pstmt.close();

   }
   catch (Exception exc) {
   }

 }



 // *********************************************************
 //  Set Roster Sync indicator in Club table
 // *********************************************************

 private final static void setRSind(Connection con, String club) {


   //
   //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "UPDATE club5 SET rsync = 1");

      pstmt.clearParameters();
      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception exc) {
   }

 }


 // *********************************************************
 //  Remove dbl quotes and embedded commas from record
 // *********************************************************

 private final static String cleanRecord( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length];
      char letter;
      int i2 = 0;
      boolean inquotes = false;

      for ( int i=0; i<ca.length; i++ ) {
         letter = ca[i];
         if ( letter != '"' ) {            // if not a quote
            if ( letter == ',' ) {         // is it a comma?
               if (inquotes == false) {    // skip commas while in between quotes
                  ca2[i2] = letter;        // save good letter
                  i2++;
               }
            } else {                       // not a quote or a comma - keep it

               ca2[i2] = letter;        // save good letter
               i2++;
            }

         } else {                      // quote - skip it and check for 'between quotes'

            if (inquotes == true) {

               inquotes = false;       // exit 'between quotes' mode

            } else {

               inquotes = true;        // enter 'between quotes' mode
            }
         }
      }

      char[] ca3 = new char [i2];

      for ( int i=0; i<i2; i++ ) {
         letter = ca2[i];        // get from first array
         ca3[i] = letter;             // move to correct size array
      }

      return new String (ca3);

 } // end cleanRecord


 // *********************************************************
 //  Remove dbl quotes and embedded commas from record
 //  Replace 2 dbl quotes in a row with a ?
 // *********************************************************

 private final static String cleanRecord2( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length];
      char letter;
      char lastLetter = 'a';          // init for complier
      int i2 = 0;
      boolean inquotes = false;

      for ( int i=0; i<ca.length; i++ ) {
         letter = ca[i];
         if ( letter != '"' ) {            // if not a quote
            if ( letter == ',' ) {         // is it a comma?
               if (inquotes == false) {    // skip commas while in between quotes
                  ca2[i2] = letter;        // save good letter
                  i2++;
               }
            } else {                       // not a quote or a comma - keep it

               ca2[i2] = letter;        // save good letter
               i2++;
            }

         } else {                      // quote - skip it or replace it, and check for 'between quotes'

            if (lastLetter == '"') {     // if 2 quotes in a row

               ca2[i2] = '?';            // replace with a '?'
               i2++;
            }

            if (inquotes == true) {

               inquotes = false;       // exit 'between quotes' mode

            } else {

               inquotes = true;        // enter 'between quotes' mode
            }
         }
         lastLetter = letter;          // save last letter
      }

      char[] ca3 = new char [i2];

      for ( int i=0; i<i2; i++ ) {
         letter = ca2[i];        // get from first array
         ca3[i] = letter;             // move to correct size array
      }

      return new String (ca3);

 } // end cleanRecord2



 // *********************************************************
 //  Check for 2 commas in a row and insert a ? (there are no quotes)
 // *********************************************************

 private final static String cleanRecord3( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length +16];     // allow for all commas
      char letter;
      char lastLetter = 'a';          // init for complier
      int i2 = 0;

      for ( int i=0; i<ca.length; i++ ) {

         letter = ca[i];
         if ( letter == ',' ) {            // if a comma

            if (lastLetter == ',') {     // if 2 commas in a row

               ca2[i2] = '?';            // replace with a '?'
               i2++;
            }
         }

         ca2[i2] = letter;            // copy this character to work area
         i2++;

         lastLetter = letter;          // save last letter

      }   // end of loop

      char[] ca3 = new char [i2];

      for ( int i=0; i<i2; i++ ) {
         letter = ca2[i];             // get from first array
         ca3[i] = letter;             // move to correct size array
      }

      return new String (ca3);

 } // end cleanRecord3

 private final static String cleanRecord4( String s ) {

     while (s.contains(",,")) {
         s = s.replace(",,", ",?,");
     }

     return s;
 }


 // *********************************************************
 //  Strip double quotes from start and end of string
 // *********************************************************

 private final static String stripQuote( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length];
      char letter;
      int i2 = 0;

      for ( int i=0; i<ca.length; i++ ) {
         letter = ca[i];
         if ( letter != '"' ) {
            ca2[i2] = letter;        // save good letter
            i2++;
         }
      }

      char[] ca3 = new char [i2];

      for ( int i=0; i<i2; i++ ) {
         letter = ca2[i];        // get from first array
         ca3[i] = letter;             // move to correct size array
      }

      return new String (ca3);

 } // end stripQuote


 // *********************************************************
 //  Strip character from end of string
 // *********************************************************

 private final static String stripA( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<ca2.length; i++ ) {
         ca2[i] = ca[i];
      } // end for

      return new String (ca2);

 } // end stripA


 // *********************************************************
 //  Convert Upper case names to title case (Bob P...)
 // *********************************************************

 private final static String toTitleCase( String s ) {

      char[] ca = s.toCharArray();

      boolean changed = false;
      boolean capitalise = true;

      for ( int i=0; i<ca.length; i++ ) {
         char oldLetter = ca[i];
         if ( oldLetter <= '/'
              || ':' <= oldLetter && oldLetter <= '?'
              || ']' <= oldLetter && oldLetter <= '`' ) {
            /* whitespace, control chars or punctuation */
            /* Next normal char should be capitalized */
            capitalise = true;
         } else {
            char newLetter  = capitalise
                              ? Character.toUpperCase(oldLetter)
                              : Character.toLowerCase(oldLetter);
            ca[i] = newLetter;
            changed |= (newLetter != oldLetter);
            capitalise = false;
         }
      } // end for

      return new String (ca);

 } // end toTitleCase


 // *********************************************************
 //  Remove leading zeros in member id string
 // *********************************************************

 private final static String remZero( String s ) {


      int memid = 0;
      String newS = "";

      //
      //  convert string to int to drop leading zeros
      //
      try {
         memid = Integer.parseInt(s);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      newS = String.valueOf( memid );      // convert back to string

      return new String (newS);

 } // end remZero


 // *********************************************************
 //  Strip zero '0' from start of alphanumeric string
 // *********************************************************

 private final static String remZeroS( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<ca2.length; i++ ) {
         ca2[i] = ca[i+1];
      } // end for

      return new String (ca2);

 } // end remZeroS


 // *********************************************************
 //  Return a string with the specified length from a possibly longer field
 // *********************************************************

 private final static String truncate( String s, int slength ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [slength];


      if (slength < ca.length) {       // if string is longer than allowed

         for ( int i=0; i<slength; i++ ) {
            ca2[i] = ca[i];
         } // end for

      } else {

         return (s);
      }

      return new String (ca2);

 } // end truncate

 //**************************************************************************************
 //  stripDash - strips dash and following characters off a string (i.e. 1234-01 to 1234)
 //**************************************************************************************

 private final static String stripDash( String s ) {

     StringTokenizer tokDash = new StringTokenizer(s, "-");

     if (tokDash.countTokens() > 1) {
         return tokDash.nextToken();
     } else {
         return s;
     }

 }


 //**************************************************************************************
 //  checkTPCmship - check for non-golf and rim the mship values
 //**************************************************************************************

 private final static String checkTPCmship( String mship, String club ) {

    String temp = "";


    //
    //  Skip any mships that start with "(AMEN)" or "Member", remove any values in parens
    //
    if (mship.startsWith( "(AMEN)" ) || !mship.startsWith("(")) {

       mship = "";

    } else {

       //
       //  Strip all parens and content within them (i.e. "(ALLI) Honorary Member (H)"  = "Honorary Member"
       //
       if (mship.startsWith( "(" )) {

          StringTokenizer tok = new StringTokenizer( mship, " " );     // skip to first space

          if ( tok.countTokens() > 1 ) {

             temp = tok.nextToken();       // skip past parens to start of mship
             mship = tok.nextToken();      // get start of mship

             if ( tok.countTokens() > 0 ) {    // any left?

                temp = tok.nextToken();        // get next value

                if (!temp.startsWith( "(" )) {  // if not another paren

                   mship = mship + " " + temp;   // combine (i.e.  Honorary Member)

                   if ( tok.countTokens() > 0 ) {    // any more?

                      temp = tok.nextToken();        // get next value

                      if (!temp.startsWith( "(" )) {  // if not another paren

                         mship = mship + " " + temp;   // combine (i.e.  Out Of Town)
                      }
                   }
                }
             }
          }

       } else if (mship.endsWith( ")" )) {          // if ends with (xx)

          StringTokenizer tok = new StringTokenizer( mship, "(" );     // skip to open paren

          if ( tok.countTokens() > 1 ) {

             mship = tok.nextToken();      // get mship up to paren
             mship.trim();                 // trim spaces
          }
       }

       if (mship.startsWith( "Member" ) || mship.startsWith("Tennis")) {

          mship = "";
       }
    }

    //
    //  Check again for non-golf mships after any trimming above
    //
    if (mship.equalsIgnoreCase( "Member" ) || mship.equalsIgnoreCase("Dining") || mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Tennis")) {

       mship = "";
    }

    return(mship);
 }



 //**************************************************************************************
 //  checkTPCkids - check the birth date and return a mtype based on age
 //**************************************************************************************

 private final static String checkTPCkids( int birth, String gender ) {

   String mtype = "";

   //
   //   Get current date
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH)+1;
   int day = cal.get(Calendar.DAY_OF_MONTH);

   year = year - 18;                              // date to determine if < 18 yrs old

   int date18 = (year * 10000) + (month * 100) + day;

   year = year - 8;                               // date to determine if < 26 yrs old

   int date26 = (year * 10000) + (month * 100) + day;


   //
   // check the age of the member
   //
   if (birth > date18) {                     // if < 18 yrs old

         mtype = "Dependent ";                // under 18

   } else {

      if (birth > date26) {                  // if < 26 yrs old

         mtype = "Certified Dependent ";     // 18 - 25

      } else {

         mtype = "";                         // 26 or older
      }
   }

   if (!mtype.equals( "" )) {

      if (gender.equalsIgnoreCase("F")) {      // defaults

         mtype = mtype + "Female";

      } else {

         mtype = mtype + "Male";
      }
   }

   return(mtype);
 }


 //************************************************************************
 //  logError - logs error messages to a text file in the club's folder
 //************************************************************************

 private final static void logError(String msg, String club) {

   String space = "  ";
   int fail = 0;

   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" +club+ "//rsync-log.txt", true));

      //
      //  Put header line in text file
      //
      fout1.print(new java.util.Date() + space + msg);
      fout1.println();      // output the line

      fout1.close();

   }
   catch (Exception e2) {

      fail = 1;
   }

 }  // end of logError


}