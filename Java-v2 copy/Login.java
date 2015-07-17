/***************************************************************************************     
 *   Login:  This servlet will process the initial login page's form request.
 *           It will process the 4 types of logins; support, admin, proshop and members.
 *
 *   called by:  login.jsp, mlogin.jsp and directly
 *
 *
 *   created: 11/20/2001   Bob P.
 *
 *
 *   last updated:       ******* keep this accurate *******
 *
 *        3/06/14   REMOVED 2/24 change so that "FLEXWEBFT" will no longer stick in the club5 seamless_caller field, as this was causing issues.
 *        3/04/14   Remove mobile member message and the AOL message.
 *        2/26/14   Add CLUBSTERWEB as a caller for Clubster website (non-mobile).  Remove custom mobile message for demotom.
 *        2/24/14   The "seamless_caller" field will no longer get overwritten once it's been set to the premier caller ("FLEXWEBFT").
 *        2/19/14   dining_user will now be plugged into the session, if found in the request. This will allow us to differentiate between dining users from a single club for use with customs.
 *        2/14/14   Pine Orchard Y & CC (poycc) - Added custom to require that members add at least one email address before accessing the system (case 2365).
 *        2/10/14   Change proshop login message (getMessageBoard) to inform user of new features; recurring events, and sent email history.
 *        1/30/14   Check if Member Access is blocked when processing an External Login.
 *                  Also, add a new proshop login message for Premier.
 *        1/14/14   Changed session timeout for support users from 30 to 60 mins.
 *        1/13/14   Added support for the 'supportpro' limited user on the support side.
 *       12/18/13   Add a new caller id (GENID79623) to use for the other website providers.  This new id will be blocked in doGet so they are sure
 *                  to use doPost.
 *       12/16/13   Add google and yahoo to the referrer list of sites that we reject when a login is received in doGet.
 *       12/13/13   San Gabriel CC (sangabrielcc) - Added three emails to send a notification to if a member logs in from an unathorized IP, or any IP and the proshop1 address (case 2334).
 *       12/12/13   San Gabriel CC (sangabrielcc) - Added custom to prevent anyone from logging in to the "proshopgs" and proshopbr" users from any IP address other than 67.120.181.121 (club's IP) (case 2334).
 *       12/12/13   Added a catch and rejection to block any logins coming from a referrer of "www.bing.com".
 *       12/05/13   Add an error log message when a caller (website) comes in on doGet.
 *       12/02/13   Add a proshop login message for the new member email settings (email options).
 *       11/25/13   Myclub - Updated interface to not direct members into the mobile site when an activity_id value is included.
 *       11/19/13   Add patch for Clubster's demo site that uses demov4.
 *       11/13/13   Added proshop message regarding the upcoming backend changes (expires 11/18/13).
 *       11/05/13   Add more checks to checkLastName so we match names more often (Clubster processing).
 *       10/30/13   Add processing for a new parm from Clubster in remoteUser.  Clubster will pass the user's last name as it exists in Clubster so that we can
 *                  verify that the member number and last name match a record(s) in FT.  This should prevent someone from using another member's member number.
 *                  A clubster user can change their member number in the Clubster website, but not in the app.  
 *       10/29/13   Output mobile formatted error message in remoteUser if mobile user.  Also, use special message if Clubster user and member number invalid.
 *       10/28/13   Log all login attempts when site is inactive.
 *       10/22/13   Added proshop message regarding the 'Make Tee Time Private' feature to be displayed through the end of 10/27/13.
 *       10/18/13   Royal Oaks CC - Dallas (roccdallas) - Added custom to use mobile_user instead of username when logging in via the standard login page (case 2315).
 *       10/08/13   Denver CC (denvercc) - Added to custom to display the activity selection screen when entering the mobile version (hide fitness activity).
 *        9/17/13   Get member sub-type (msub_type) from member2b and save in the session for members.
 *        9/16/13   Add message regarding new 'Member View' feature for proshop users - getMessageBoard.
 *        9/03/13   In promptIpad indicate that the mobile site is for Golf so FlxRez or Dining users don't go to our mobile site.
 *        8/07/13   Turned off AOL email notice.
 *        8/06/13   Added AOL Email notice to inform members that emails are currently being throttled by AOL. Will only display when displayAolNotice is set to true and member has one email that ends with "aol.com".
 *        7/30/13   Royal Oaks CC - Dallas (roccdallas) - Added custom to sort family members via posid on the promptPrimary screen.
 *        7/23/13   Use CONNECT_ID in ProcessConstants.
 *        7/22/13   Fixed issue with 7/18's change that was causing users to get sent to FlxRez when an activity_id of "0" was passed.
 *        7/18/13   Fixed issue with the activity_id not getting recognized properly for primary seamless logins when no additional family members are present.
 *        7/11/13   Add Clubster (ClubTec Mobile App) as a mobile caller in remoteUser.
 *        6/26/13   Remove unused interface options in flexWebUser.
 *        5/30/13   PDGMOBILE calls will now default to a primary member number setup.
 *        5/14/13   Update the Memorial Day message for proshop users for 2013 (getMessageBoard).
 *        5/10/13   Lake Shore CC (lakeshorecc) - Added IP: 70.199.5.165 to the IP ban processing.
 *        5/09/13   Added a check in memberUser to ban specific IPs from accessing any ForeTees site as a member.
 *        4/24/13   Interlachen CC (interlachen) - Added to custom so that members will get the prompt to select their desired activity when they access the mobile site.
 *        4/23/13   Fort Collins CC (fortcollins) - Updated processing to strip off "S1" from the end of user names passed.
 *        4/22/13   Fort Collins CC (fortcollins) - Updated so AMO caller also applies the primary/mNum setup, and "FC" will be added to the passed user_name value for caller "AMO".
 *        4/18/13   FTCP - Added additional directives to the landing parameter
 *        4/12/13   Add checks in memberUser and remoteUser for memberInact in club5 to see if all member access is to be blocked.
 *        4/11/13   Add custom options for Minikahda members to route to Golf or Dining (because CE charges too much for a dining link).
 *        4/09/13   Updated systemTest() for remote AMSU call
 *        4/05/13   If mobile device detected in memberUser prompt the user regardless of the default activity id.
 *        4/04/13   Set the activity id to Connect if club uses FT Connect so Member_announce will route to Connect's home page.
 *        3/28/13   Get the zipcode from club5 if not passed in memberUser.  FT Connect websites will not pass this.
 *                  Also, get the ftConnect flag from club5 and set accordingly in the session in memberUser (for clubs that will use FT Connect).
 *        3/28/13   Change the output in promptIpad to use the mobile stylesheets and format.
 *        3/14/13   Updated remZero() and remZero2() methods to use simplified methods of removing all leading zeroes.  remZero() in particular was using unsafe methodology that was causing errors.
 *        3/06/13   Updated remoteUser so that autorefresh will not get set false for an email/ical prompt if an eventName is present, since we end up bypassing the prompt message anyway.
 *        3/06/13   Fixed issue with improper forwarding when there are messages to be displayed and an eventName of "clubessential" or "none" is passed.
 *        2/28/13   Passing an eventname parameter value of "partners" will now bring members to the partner list page for the passed activity (golf if no activity passed).
 *        2/13/13   Rehoboth Beach CC (rehobothbeachcc) - Email prompt will now only display the first two times a member logs in, regardless of whether or not they have emails set in ForeTees (case 2220).
 *        2/05/13   Rehoboth Beach CC (rehobothbeachcc) - Adjusted custom to only display the error message and email prompt one additional time, and then allow members to move on (case 2220).
 *        2/04/13   Rehoboth Beach CC (rehobothbeachcc) - Added to custom to require that members enter at least one email address (case 2220).
 *        2/04/13   Do not show Welcome message in remoteUser if no message to show.  Simply jump to Member_announce.
 *        1/31/13   Disabled the option for outdated browsers to access the Old Skin version of the site.
 *        1/28/13   Skip welcome message for Flexscape-Connect Premier callers.
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *        1/10/13   Add Mobile prompt to remoteUser for Philly Cricket.
 *        1/09/13   Temp check for Philly Cricket for mobile changes - remove check in promptMobileUser later.
 *       12/12/12   Provide menu of activities for mobile users if club has more than golf.
 *       12/20/12   Updated IE6 compatibility message to indicate that the ForeTees Old Version will be discontinued effective 2/1/2013.
 *       12/20/12   Added custom to ensure Mobile message will be always be displayed on demotom when logging in as a member.
 *       12/06/12   Add flexWebUser method to handle SSO logins from ForeTees - Flexscape websites.
 *       12/05/12   Hillwood CC (hillwoodcc) - Allow members to update their emails despite using roster sync.
 *       11/15/12   Passing an eventname parameter value of "lessons" will now bring the member to the Individual Lessons page for the passed activity (except dining).
 *       10/26/12   Passing an eventname parameter value of "calendar" will now bring you to the calendar page for the passed activity (golf if no activity passed).
 *       10/23/12   Fixed issue with Jonas caller being able to pass through the mobile parameter successfully.
 *       10/16/12   Successful mobile logins will now be denoted as such in the session log.
 *       10/10/12   Added support for GJONAS caller to pass mobile = 1 to hook into the mobile site.
 *       10/09/12   Greeley CC (fortcollins) - Added custom processing to handle Greeley CC members being funneled into the Fort Collins site.
 *       10/09/12   Added support for PDGMOBILE caller for generic use by websites/apps wanting to connect to our mobile interface.
 *       10/05/12   Reject login from mobile login page if user is proshop.
 *        9/20/12   Commented out processing which was using emails passed from MembersFirst to update members' email addresses upon login since it's no longer needed and creates confusion.
 *        9/20/12   Updated "Remote Login Successful" message to be clearer about which of the listed usernames is the one passed from the website.
 *        9/20/12   stripAlpha will now only occur if username string is not blank.
 *        9/18/12   Implemented a couple safety net try/catch blocks when attempting to strip an alpha or alphanum character sequence off the end of a remote user's user_name value.
 *        9/12/12   Reduced default auro-refresh delay back to 1 seconds instead of 2 seconds.
 *        9/10/12   Expanded passing an eventname of "reservations" to work for FlxRez and ForeTees links as well.
 *        9/04/12   Updated meta refresh and forms for jumping remote users directly to the Dining Event Listing page and popping up the event if an id is passed in
 *        9/04/12   Added mention of Mobile User to Invalid Login error message, so it's easier to tell if a member is attempting to log in via mobile or standard.
 *        9/04/12   Added code to allow an eventName of "reservations" to be passed to direct members directly to the dining reservations page.
 *        8/29/12   Shady Canyon GC (shadycanyongolfclub) - Added custom to order the list of names on the primary prompt so they are ordered by username.
 *        8/24/12   Tonto Verde (tontoverde) - Updated the auto-refresh time to be 0 when coming in from a website event link.
 *        8/14/12   Golfcrest CC (golfcrestcc) - Moved "gccc" strip custom so that it occurs before the stripping of leading zeros.
 *        8/09/12   Fix to prevent links directing to Golf from sending members to FlxRez.
 *        8/08/12   Updates to allow website event links to work for clubs that use a primary interface.
 *        7/25/12   Wee Burn CC (weeburn) - Added custom to use the stripAlphaNum option for their Northstar seamless.
 *        7/25/12   Added stripNumAlpha boolean for clubs that need a 2-char alphanumeric string stripped from seamless-passed usernames (e.g. 'S1', 'C1', 'C2'). Must be set via custom for the time being.
 *        7/20/12   Added initial work for seamless Dining Event Links. For now a debug log entry will be made and the user will be brought to the event listings page.
 *        7/17/12   Post a message to proshop users that use the GHIN Interface - inform them of the No-Post feature.
 *        7/07/12   Add External Login processing for proshop users to view their invoices (from email built by Support_invoicing).
 *        7/12/12   Golfcrest CC (golfcrestcc) - Added a custom to strip "gccc" off the front of passed usernames. IMAVEX is supposed to not be including that, but some members still have it.
 *        6/20/12   No welcome message delay for Monterey Peninsula.
 *        6/13/12   Added a new message for proshop users regarding the maintenance on the GHIN interface.
 *        6/07/12   Count mobile devices if they come in without the mobile parm - when we detect them and prompt user for mobile i/f or standard.
 *        5/22/12   Add a new message for proshop users - new skin warning.
 *        5/15/12   Update the external login to the new skin.
 *        5/08/12   Add an internal login type to proshopUser from the Dining Admin.  This will be used to route the admin user to the Send Email process.
 *        4/30/12   Enable the Memorial Day message for golf proshop users for 2012.
 *        4/03/12   Fix for new skin so an eventname of "clubessential" or "none" will correctly direct members to the event listings page.
 *        3/30/12   Improve the error messages when a website link is broken - point issue to the website.
 *        3/26/12   Added a change so that clubs with no golf but FlxRez enabled will automatically get defaulted to an activity, even if their default_activity_id is set to 0.
 *        3/16/12   Fix member login for Winged Foot for new skin.  They use a custom to go to their guest quota report instead of their announcement page.
 *        3/14/12   Fixed div overlapping issue.  Instead of shimming now clear the float for both member & remote logins
 *        3/12/12   Re-enable the iPad prompt when member enters from an iPad - offer both Mobile site and normal site.
 *        3/08/12   Reflection Ridge GC (reflectionridgegolf) - Added to custom to require that members enter at least one email address (case 2130).
 *        3/07/12   Add mobile= parm for CE remote users.
 *        2/25/12   Move new skin setting in remoteUser until after the username is determined.
 *        2/16/12   Reduce default delay for login welcome from 3 secs to 2 secs on new skin.
 *        2/10/12   Add a 'Site Under Construction' process for times when we need to block access while we work on a site (userlock flag in club5).
 *        2/03/12   Remove p;d browser information from the Help page.
 *        2/01/12   In memberUser and remoteUser set the new_skin boolean if the member subtype value is Employee.  This will allow staff members to test
 *                  the enw skin.
 *        1/20/12   Remove the Partner List message - only display the Mobile msg, if mobile supported.
 *        1/20/12   New Skin - add meta refresh to login messages.
 *        1/19/12   Updated processExtLogin so that the member's middle initial gets plugged in, if present. This was causing errors for members with a middle initial in the system.
 *        1/11/12   If an event name of "none" is passed, direct the member to the event listing page.
 *        1/06/12   processExtLogin - move the check for new_skin to after successful login when we have a connection.
 *       11/18/11   Castle Pines - add custom proshop user (proshoppace) and route to Proshop_custom_pace_display.
 *       11/10/11   New skin changes, added new_skin session var to all member sessions
 *        9/06/11   For Club Essential clubs, if they pass an eventname parameter of "clubessential" it will direct members to the general event listing page, instead of a specific event.
 *        9/06/11   Change latest_message to msg003 to trigger a resend of the mobile annoucement if user has not used it yet.
 *        9/02/11   Add processing in processExtLogin to allow a login request from an email for a Dining Event.
 *        8/30/11   Add processing for an auto login from the Dining system for the Admin user to link over to ForeTees to configure the Dining system (comes in as a proshop user).
 *        8/22/11   Forest Hills CC (MO) - MyClub users - custom to turn off the primary setting if username ends with an alpha char (spouses).  Map to webid instead.
 *        8/19/11   Add support for ForeTees Dining - route to dining if activity id is dining.
 *        8/18/11   Allow the eventname parameter from any website provider (was only supported for MF).
 *        7/21/11   John's Island Club (johnsisland) - Added stripSpace custom to strip a space from the middle of username values being passed, if found.  Can add a club5 variable later if other clubs need.
 *        7/14/11   Rolling Hills GC - SA (rollinghillsgc) - Added custom to display a system maintenance warning prior to, as well as a custom message during the nightly database backup period,
 *                  instead of the standard db error message. This period runs roughly 11:00am to 11:30am their time, and the new messages are to help avoid member confusion and calls to the shop.
 *        7/01/11   Dallas Athletic Club (dallasathleticclub) - Added custom to require the members enter at least one email address (case 2002).
 *        6/29/11   Corrected a typo in the Limited Access Member welcome message.
 *        6/28/11   Charlotte CC (charlottecc) - Do not update email addresses based on information passed from MembersFirst.
 *        6/21/11   Changed the order of the stripDash and stripAlpha checks so that the dash-values are handled first.
 *        6/14/11   Olympic Club (olyclub) - Added custom error message for when a member isn't found during remote login.
 *        6/04/11   Denver CC - MyClub users - custom to turn off the primary setting if username ends with an alpha char (spouses).
 *        5/02/11   Fixed issue with remote FlxRez logins when primary interface in use but no family members are found. activity_id was not getting set back to a positive value.
 *        4/26/11   Fox Hill CC (fortcollins) - Added custom processing to override seamless settings and prefixed the passed member numbers with "FOX".
 *        4/26/11   Add seamless interface for Mobile devices from MFirst - use caller MFMOBILE (smart phone App).
 *        3/25/11   Talbot CC (talbotcc) - Custom to require the members enter at least one email address when prompted (case 1954).
 *        2/24/11   Do not prompt a mobile user in remoteUser if already detected as a mobile device (iPad prompt).
 *        2/23/11   In remoteUser if eventname is passed (MF), get the activity_id of that event and use that for the user's session.
 *        2/15/11   Add external login processing for events from emails with custom content.
 *        2/09/11   Add seamless interface for Mobile devices from MyClub (smart phone App).
 *        1/24/11   Fix for getOrganizationId() being called with a null connection object.
 *        1/20/11   Added organization_id to the member session block (for dining system)
 *        1/12/11   Add a messaging feature for proshop users.
 *       10/04/10   Increase member timeout period from 15 to 30 minutes.  Updated corresponding messages to display 30 minutes as well.
 *        9/28/10   Prompt seamless interface users regarding bounced email flags (case 1720).
 *        9/22/10   Add prompt for iPad users to allow them to switch to the mobile interface.
 *        9/17/10   Added a default activity_id of 0 to Hotel user session.
 *        9/10/10   TPC Clubs - route Hotel users to hotel_home.htm so they can switch clubs on the fly.
 *        6/29/10   Change the error message in remoteUser if a username is not provided.  Inform user that they must login to their website.
 *        6/29/10   Added stripAlpha and stripDash options for handling remote users. stripAlpha will remove a non-numeric character from
 *                  the end of the username, and stripDash will remove a dash and anything that follows it from the username
 *        5/21/10   Add club name and other info to remoteUser error message (Username not provided).
 *        5/11/10   Stone Oak CC (stoneoakcountryclub) - strip the spouse extension from the member number for Jonas.
 *        4/21/10   Mayfield Sandridge - strip the spouse extension from the member number for Jonas i/f.
 *        4/21/10   Reject remote caller if username not provided.
 *        4/05/10   If default_activity_id has been set to -999 but primary interface does NOT break out to Member_msg (no family members present),
 *                  set default_activity_id back to 0
 *        4/05/10   Added a new caller (TAHOEDONNERCA530) for Tahoe Donner for seamless interface with their site
 *        3/10/10   Change to activity parameter.  Activity_id of desired default_activity will now be passed.  If not default is passed, then the member's default is used
 *        2/16/10   If activity=tennis param passed with remote user and this user will use primary interface, store the activity id as a negative value in the session
 *        2/11/10   Updated remoteUser to look for 'activity=tennis' and if present default them into FlxRez
 *        1/26/10   Member users - check if Mobile supported (new flag in club5).
 *        1/25/10   In remoteUser - save the caller in club5 if not already set so we know which clubs are using the seamless
 *                  interface and which website they use.
 *        1/22/10   Denver CC - skip the Mobile announcement (msg002) - they don't want members to bypass website. (** Replaced by mobile check)
 *        1/21/10   Update the Help process from the Login pages, and add help for mobile users.
 *        1/15/10   Add message #2 to announce the Mobile Interface.
 *        1/14/10   Change the login for a Mobile member user to check the mobile credentials in member2b. Also,
 *                  bump counter if member mobile login and send mobile password to member if prompted.
 *       12/18/09   Reset the member message feature to display a message regarding the partner list updates.
 *       11/24/09   Allow more values in the mobile parm so we can indicate the level of support for the device.
 *       11/06/09   Remove logerror call in doGet when member enters incorrectly - flooding the log.
 *                  We can address this later - not a big deal.
 *       10/22/09   Do not display iCal prompt for members of The Reserve Club
 *       10/19/09   Remove the Monson website clubs from the filter in doGet as Kelly fixed all his links to not use Get.
 *                  Also in remoteUser add check for caller=ForeTees1298 and if so, force the semaless settings to Primary
 *                  mode so we can bypass the CE links while the lcubs uses both interfaces to test.
 *       10/05/09   Allow certain clubs' websites to access doGet because the don't know how to change it to doPost.
 *        9/28/09   Change check in remoteUser for email prompt from MF to rsync.
 *        9/28/09   Change forms that call Login from within to use post instead of get.
 *        9/21/09   Add processing doGet to allow links from CE websites (CE Bypass) that are not forms.
 *        9/16/09   Woodlands CC - leave the welcome message a little longer.
 *        8/18/09   Add users default activity_id to session block (modified 9/14/09)
 *        7/24/09   Add support for mobile devices - from Mobile Login page.
 *        7/22/09   Enable event processing from Members First
 *        7/17/09   When asking for iCal preferences upon login, only set emailOpt to 1 if user has iCal1 or iCal2 set to yes
 *        5/29/09   Pass additional information if logging in via an email dining link
 *        5/22/09   Add one-time prompting for iCalendar attachments during login
 *        5/08/09   Add new 2 minute timer call in init method.  Start with 3 minutes to offset with 
 *                  original 2 minute timer.
 *        3/05/08   Add new external login method for process logins from our email messages (processExtLogin)
 *       10/14/08   Brooklawn - prompt remote member to verify/change email address if it has bounced (case 1568) - on hold.
 *        9/29/08   Removed Temp Royal Oaks Dallas (roccdallas) changes
 *        9/22/08   Added temp login fix for Royal Oaks Dallas (roccdallas), to be removed after their site maintenence is complete
 *        8/13/08   Added new logging method to track login attemps
 *        7/17/08   Mesa Verde (MF Roster Sync) - allow remote users to add/change their email addresses.
 *        7/11/08   Added check for inactive proshop users and restrict access if inactive
 *        6/26/08   Move the clubcorp_ conversion up to doPost so we can pass the clubname test there.
 *        6/17/08   MF remoteUser - add conversion of ClubCorp club names (i.e.  clubcorp_132 = trophyclub).
 *        6/05/08   Add systemTest method (check for db connectivity and nfs access)
 *        6/03/08   Add TimerGHIN to init method (new timer for GHIN updates).
 *        6/02/08   Move member timeout value to SystemUtils.MEMBER_TIMEOUT
 *        5/28/08   Add debug output to Login when club name not found in v5 table
 *        5/19/08   Enhanced email bounced notification.  Now prompts user to enter new address and will clear their bounced indicator
 *        4/20/08   Added development logins to demo sites - member4tea any pass will login as random member
 *        4/10/08   WingedFoot - direct members to their guest quota report after login Case# 1415
 *        4/02/08   Remote Logins - add parms passed from web site to the session log entry.
 *        3/27/08   Brookings CC - Add support for their last names which may contain a trailing # sign in their last names
 *        3/03/08   Add support for checking v5.clubs.inactive flag
 *        2/07/08   Comment out called to checkName for MEMFIRST clubs in remoteUser method
 *       11/02/07   Merion - move custom code from CE area to common area (not sure why it was there to begin with).       
 *       10/14/07   MF - add eventname= parm to interface so user can select an event on their web site calendar
 *                       and go directly to our event signup (added, but not used yet). 
 *       10/02/07   Merion - strip extensions from member numbers in remoteuser (A140-1).       
 *        8/12/07   New Canaan - Add trap for failed logins to proshop
 *        7/19/07   Check new 'billable' flag in member2b.  Do not allow if not set (member is excluded).
 *        7/17/07   Add the club's Roster Sync indicator to the session for admin users.
 *        7/14/07   Add an option in remoteUser that will strip any end characters from the username.
 *        6/29/07   Add try/catches to init method when setting timers.  Log error and throw exception if one received.
 *        6/11/07   Remove BUZWEB form remote userlist.
 *                  Also, remove call to scanTee when pros log in.
 *        6/05/07   Changed remoteUser processing to get some interfaceparms from club5 instead of the web site.
 *        5/09/07   Removed building of daysArray - no longer stored in session block
 *        5/08/07   Improve the remote user failure session logging - add more info.
 *        5/01/07   Add new interface parm to strip the leading zeros from the username (stripzero=).
 *        4/19/07   Strip the leading zeros from the username for IntraClub caller (for Sunset Ridge).
 *        4/09/07   Congressional - custom for primary interface - do not include names of dependents (case #1020).
 *        4/06/07   Check new 'inact' flag in member2b.  Do not allow if inactive.
 *        4/03/07   Correct the sql statement in processEmail to prevent an email address of zero.
 *        3/28/07   Add a generic web site id for small one time web sites (seamless interface).
 *        2/15/07   Comment out the member welcome message processing as we don't need it now (save for future).
 *        2/15/07   Greeley CC - add a 'G' to the member number received from web site.
 *        2/06/07   Change index2.htm to index.htm to allow for new login pages.
 *        1/30/07   Add support for South Bay Design - Hillcrest GC in St. Paul.
 *        1/28/07   Allow member to add their email addresses, if none, on Login.
 *        1/11/07   El Niguel - change days in adv parms for Adult Females on Tues.
 *       10/20/06   Do not display tee times message if AGT member.
 *       10/16/06   Remove call to initLogins in init method as this can hang the init sequence
 *                  and therefore it can hang all processing.
 *       10/13/06   Add more information to error msg when remote user fails.
 *       10/09/06   Changes for bounced email address flagging
 *        9/12/06   Add support for Legendary Marketing - Harkers Hollow.
 *        9/12/06   Add support for Keating - Glen Oak CC.
 *        8/29/06   Add support for City Star - Colorado Springs CC.
 *        7/20/06   Add session vars for TLT system and for mobile users.
 *        7/19/06   Save timer value for X timer in init method (safety check for timers).
 *        7/12/06   Strip the leading zeros from the username for Jonas caller (for Mendakota).
 *        6/24/06   Improve some of the error messages returned to user, especially remote users.
 *        6/21/06   Added recordLoginStat method for new tracking of login statsistics
 *        6/15/06   Move call to scanTee on proshop user method so it runs in background.
 *        6/14/06   Scioto - custom days in adv for Spouse member types.
 *        6/01/06   In remoteUser, if primary=yes make sure the session gets built properly in case
 *                  there is only one member in the family - get the username and save it in the session.
 *                  Also, build and set the daysArray.
 *        5/02/06   Remove synchronized statements.
 *        5/02/06   Change calls to sessionLog to not include the pw if successful.
 *        5/01/06   Add support for Jay Van Vark caller (web site provider for Rancho Bernardo).
 *        4/25/06   Add support for Winding Oak caller (web site provider for Wayzata).
 *        4/10/06   Add support for Lightedge - Davenport CC.
 *        3/08/06   Add 2 new fields to the proshop session for member classes (mtypeOpt and mshipOpt).
 *        3/07/06   Oswego Lake (ZSmart) - trim the username to a max length of 15 chars (was 10).
 *        3/06/06   Change calls to sessionLog to include the connection.
 *        3/01/06   Strip the leading zeros from the memNum for CSG caller (for Hurstbourne).
 *        2/17/06   Add support for LogiSoft - Locust Hill CC.
 *        2/07/06   Add webid field to member2b for web site interface.  If web site interface is added
 *                  after our site is running, they can provide us with their member ids and we map them.
 *       11/09/05   Add support for MeritSoft (BuzWeb) - Providence CC.
 *       10/18/05   Add support for Cherry Hills caller (they do their own).
 *       10/06/05   Add support for Nakoma caller (web site provider for Nakoma - they do their own).
 *        9/29/05   Add support for Sedona Management Group caller (web site provider for Fairwood).
 *        9/22/05   Add a login log to track all logins.
 *        9/10/05   Medinah - allow Social Regular and Social Reg Probationary members to login.
 *        9/07/05   Oswego Lake (ZSmart) - trim the username to a max length of 10 chars (to match limit of password).
 *        9/02/05   Validate member email addresses when they login.  Post warning if invalid.
 *        7/07/05   Add support for email parms from MFirst.
 *        6/16/05   Add support for Grapevine caller (web site provider for Brooklawn).
 *        6/13/05   Add support for Flexscape caller (web site provider for Sciotto & Bishops Bay).
 *        6/13/05   Add support for ZSmart Web Marketing caller (web site provider for Oswego Lake).
 *        5/21/05   Custom for Medinah CC - If ARR member, change days in adv to 30 for all days of week.
 *        5/20/05   Medinah - do not allow dependents or social members to login.
 *        5/01/05   Add counters for logins - in SystemUtils.
 *        4/26/05   Add mtype to parms saved in the session for members.
 *        4/18/05   Add sales credentials and login, change support pw.
 *        4/10/05   Wichita - change web site providers from Gardner to JCook.
 *        4/07/05   Add mnum=yes for primary interface so web site can provide mNum instead of username.
 *        3/14/05   Add support for Gary Jonas Webs caller (web site provider for Interlachen & Meadowbrook).
 *        3/03/05   Add support for Gold Star Webs caller (web site provider for Rochester).
 *        2/21/05   Add V5 Changes page to welcome for members.
 *        2/14/05   Change SystemUtils.Connect to dbConn.Connect to allow 2 seperate servers.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/21/05   Strip the leading zeros from the memNum for Web Sites 2000 caller.
 *        1/16/05   Add support for Web Sites 2000 caller (web site provider for Ironwood).
 *        1/06/05   Add support for Hidden Valley S/W caller (web site provider for North Ridge).
 *        1/04/05   Ver 5 - allow for more than one admin login.
 *       12/15/04   Add trace for Admin and Proshop Logins from Old Oaks.
 *       11/22/04   Add support for Primary-Only logins from CE.  Only the primary member resides
 *                  in their database so we must prompt for the family member.
 *       10/28/04   Add support for FELIX caller (web site provider for White Manor).
 *       10/25/04   Add support for MEDIACURRENT caller (web site provider for Piedmont Driving Club).
 *       10/11/04   Ver 5 - save lottery support indicator in the session for proshop users.
 *        9/22/04   Add new parm from clubs' index2.htm - zipcode= (for weather link).
 *        9/16/04   Ver 5 - change getClub and getDaysInAdv from SytemUtils to common.
 *        9/03/04   Save the club's POS Type for _slot processing.
 *        5/24/04   Save the member's mode of trans preference for Member_slot.
 *        5/19/04   Change member timeout to 5 minutes (from 8) and pro from 8 hrs to 4 hrs.
 *        5/05/04   Add INTRACLUB for Old Oaks and Bellerive.  Modify no-cache settings.
 *        5/04/04   Add 'cache-control' to responses to prevent session mangling.  Some proxy
 *                  servers were caching the pages and therefore interfering with the cookies.
 *        4/27/04   Add support for VELOTEL caller (web site provider for Hazeltine).
 *        4/22/04   RDP Add custom processing for Hazeltine Natl.  Allow women 14 days in adv.
 *        4/14/04   Change member session timeouts from 10 mins to 8 mins.
 *        4/08/04   Add support for GARDNER caller (web site provider for Wichita).
 *        4/07/04   Add support for PRIVATEGOLF caller (web site provider for Fort Collins).
 *        4/07/04   Add support for CLUBESSENTIAL caller (web site provider for Wakonda).
 *        2/09/04   Add support for NEMEX caller (web site provider for Forest Hills).
 *        1/21/04   Allow for 'days in adv' parms on a per membership and per day basis.
 *        1/08/04   Add DaysAdv array processing for members - establish and save
 *                  the days in advance values to make calendar building quicker.
 *       12/31/03   Add MONSON web site caller.
 *       11/18/03   Version 3 - add hotel user logins.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/29/02   Init the connection holder field in Support's session block so new
 *                  connection is made when logging into new club.
 *
 *       12/04/02   Enhancements for Version 2 of the software.
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.medinahCustom;
import com.foretees.common.FeedBack;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.StringEncrypter;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Labels;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json


public class Login extends HttpServlet {

    
 static String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 String support = SystemUtils.support;    // class variables that never change
 String supportpro = SystemUtils.supportpro;    // class variables that never change
 String sales = SystemUtils.sales;
 String admin = SystemUtils.admin;
 String proshop = SystemUtils.proshop;
 String id = SystemUtils.id;
        
 //
 // The following password must be maintained here so we can
 //  login and initialize the databases when the site is installed.
 //
 String passwordSup = SystemUtils.passwordSup;       // password for support (dev) login...
 String passwordSupPro = SystemUtils.passwordSupPro; // password for support (prosupport) login...
 String passwordSales = SystemUtils.passwordSales;   // password for sales login...

 String omit = "";             // ommitted

 String iCalNotice = "<p><b>Notice:</b> We've added a new feature that allows you to receive iCalendar files along with your email notifications.&nbsp; " +
                     "You may choose to receive them at either email address using the options below, or at anytime by clicking on the Settings tab from within ForeTees.&nbsp; " +
                     "<a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">Click here for more information regarding iCalendar.</a>" +
                     "<br><br><u>BlackBerry Users:</u> If your tee time notifications from ForeTees appear to have an empty message body, you will need to disable iCal attachments to that email address.&nbsp; " +
                     "</p>";
 
 //
 //  The following is used for member messages to be displayed once when a member logs in. (******* See also Member_msg *********)
 //
 String previous_message = "msg001";      // previous message that was shown
 String latest_message = "msg003";        // message we want to show now ** NOTE:  Bump this number to force members to see the latest message again !!!


 
 static final int MEMBER_TIMEOUT = SystemUtils.MEMBER_TIMEOUT;
 
 static int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System  
 
 
 int CONNECT_ID = ProcessConstants.CONNECT_ID;                           // Act id for FT Connect 
  

 static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System
 static String DINING_PW = ProcessConstants.DINING_PW;                   // Dining password for Admin user

 

 //*****************************************************
 // Perform initialization processing when server loads
 // this servlet for the first time.
 //*****************************************************
 //
 public void init()
         throws ServletException {


   String errorMsg = "";
     
  
   //
   //  set a 2 minute system timer to check teecurr for inactive sessions
   //
   try {

      minTimer t2_timer = new minTimer();

      minTimer2 t2_timer2 = new minTimer2();      // start a 2nd timer to split the load

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting minTimer. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }

   //
   //  set a 60 minute system timer to check teecurr for X's
   //
   try {

      min60Timer t4_timer = new min60Timer();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting min60Timer. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }

   //
   //   set timer to make sure we keep building new sheets daily
   //
   try {

      TeeTimer t_timer = new TeeTimer();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting TeeTimer. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }


   //
   //   set timer to send current tee sheet to Pro Shops (2 times per day)
   //
   try {

      TsheetTimer t3_timer = new TsheetTimer();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting TsheetTimer. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }

   //
   //  Set the Roster Sync Timer for tonight (4:45 AM - check for MFirst Rosters)
   //
   try {

      TimerSync sync_timer = new TimerSync();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting TimerSync. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }


   //
   //  Set the GHIN Timer for tonight (3:45 AM)
   //
   try {

      TimerGHIN ghin_timer = new TimerGHIN();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting TimerGHIN. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);   // log it and continue
   }


   //
   //  Set the date/time when the 60 min timer (Xtimer) should expire by next (safety check to ensure timers keep running)
   //
   Calendar cal = new GregorianCalendar();   // get todays date

   cal.add(Calendar.MINUTE,90);              // roll ahead 90 minutes to give plenty of time

   long year = cal.get(Calendar.YEAR);
   long month = cal.get(Calendar.MONTH) +1;
   long day = cal.get(Calendar.DAY_OF_MONTH);
   long hr = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
   long min = cal.get(Calendar.MINUTE);

   //
   //  create date & time stamp value (yyyymmddhhmm) for compares
   //
   SystemUtils.min60Time = (year * 100000000) + (month * 1000000) + (day * 10000) + (hr * 100) + min;   // save date/time stamp
   

   //
   //  Throw an exception if one received above
   //
   if (!errorMsg.equals( "" )) {     
  
      throw new ServletException("ForeTees - Error setting timers in Login Init");
   } else {
       
       SystemUtils.logError("Tomcat Startup Completed."); 
   }
     
 }


 //
 // Process external logins from our emails - currently used for dining requests and unsubscribes, and for proshop users to view Invoices
 //
 public void processExtLogin(HttpServletRequest req, HttpServletResponse resp) {

   
    resp.setHeader("P3P","CP=\"NOI DSP COR NID\"");
    resp.setHeader("Pragma","no-cache");                                      // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);    // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = null;

    try {
        out = resp.getWriter();
    } catch (Exception ignore) {
        return;
    }

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement stmt = null;
    
    String club = "";
    String user = "";
    String mtype = "";
    String mship = "";
    String subtype = "";
    String caller = "";
    String jumpTo = "";
    String name = "";
    String wc = "";
    String errMsg = "";
    String els = "";        // encoded login string
    String rls = "";        // raw (dencoded) login string
    String date = "";        // contains the date passed from the email dining link (date of booking)
    String customId = "0";   // would contain the custom messsage id from the dining link that was clicked on
    String event_id = "";    // if link to an event - the event id
    
    int activity_id = 0;   
    
    boolean fatalError = false;
    boolean new_skin = false;
            
    // caller indicates what feature this link is for (unsubscribe email link, dinning, ?)
    if (req.getParameter("caller") != null) {

        caller = req.getParameter("caller");
    }

    
    // if customId is here, lets retain pass it through
    
    if (caller.equals("dining")) {       // Dining Request system       
        
        if (req.getParameter("customId") != null && !req.getParameter("customId").equals("")) {

            customId = req.getParameter("customId");
        }
        
        if (req.getParameter("date") != null && !req.getParameter("date").equals("")) {
            
            date = req.getParameter("date");
        }
        
    } else if (caller.equals("event")) {      // from email link for an event (all events)
        
        if (req.getParameter("event") != null && !req.getParameter("event").equals("")) {

            event_id = req.getParameter("event");
        }
        
        if (req.getParameter("act_id") != null && !req.getParameter("act_id").equals("")) {
            
           try { activity_id = Integer.parseInt(req.getParameter("act_id")); }
           catch (Exception ignore) {}
        }        
        
    } else if (caller.equals("invoice")) {      // from Invoice email - proshop users to view invoices (email built in Support_invoicing)
        
        if (req.getParameter("act_id") != null && !req.getParameter("act_id").equals("")) {
            
           try { activity_id = Integer.parseInt(req.getParameter("act_id")); }
           catch (Exception ignore) {}
        }        
    }

    if (req.getParameter("els") != null) {         // encrypted club name and member id (or proshop) 

        els = req.getParameter("els");       // get the encoded login string

        if (req.getParameter("dining") != null && req.getParameter("dining").equals("ft")) {   
           
            // Unsubscribe request from dining email notification

            try {

                int organization_id = 0;
                int person_id = 0;

                StringTokenizer tok = new StringTokenizer( els, "|" );
                
                organization_id = Integer.parseInt(tok.nextToken().replaceAll( "[^\\d]", "" ));
                person_id = Integer.parseInt(tok.nextToken().replaceAll( "[^\\d]", "" ));
                
                if (organization_id == 0 || person_id == 0) {

                    out.println("<p>It appears the link contained in your email is not formatted properly. Please contact support@foretees.com for assistance.</p>");
                    return;
                }

                club = Utilities.getClubFromOrgId(organization_id);

                try {
                    con = dbConn.Connect(club);
                } catch (Exception exc) {

                    out.println("<p>Cannot validate club correctly. Please contact support@foretees.com for assistance.</p>");
                    return;
                }

                user = Utilities.getUsernameFromPersonId(person_id, con);

            } catch (Exception exc) {

                // if we got here then the decrypted string didn't have an colon in it or was null.
                out.println("ELS Token Error: " + exc.getMessage() );

                out.println("<p>It appears the link contained in your email is not formatted properly. Please contact support@foretees.com for assistance.</p>");

                return;
        
            } finally {

                 try { con.close(); }
                 catch (SQLException ignored) {}
            }

        } else {     // NOT an Unsubscribe for Dining

            try {

                StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );
                rls = encrypter.decrypt( els );

            } catch (Exception e) {
                out.println("Decrypt ELS Error: " + e.getMessage() );
            }

            try {

                StringTokenizer tok = new StringTokenizer( rls, ":" );

                club = tok.nextToken();
                user = tok.nextToken();

            } catch (Exception exc) {

                // if we got here then the decrypted string didn't have an colon in it or was null.
                out.println("ELS Token Error: " + exc.getMessage() );

                out.println("<p>It appears the link contained in your email is not formatted properly.  Please contact support@foretees.com for assistance.</p>");

                return;
            }
            
        }         // end of IF Dining Unsubscribe

    }
    
    /* else {

        //
        // TEMP - pass user & club to build ELS for testing
        //

        user = req.getParameter("user");
        club = req.getParameter("club");

        rls = club + ":" + user;

        try {

            StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );
            els = encrypter.encrypt( rls );

            out.println("Encrypted");

        } catch (Exception e) {
            out.println("Encrypt Error: " + e.getMessage() );
        }

    }
    
    out.println("els=" + els);
    out.println("rls=" + rls);
    out.println("club=" + club);
    out.println("user=" + user);    
    */

    //
    //  Make sure the club requested is not inactive
    //
    try {

        con = dbConn.Connect(rev);       // get a connection for this version level

        stmt = con.prepareStatement (
            "SELECT inactive FROM clubs WHERE clubname = ?");

        stmt.clearParameters();        // clear the parms
        stmt.setString(1, club);
        rs = stmt.executeQuery();

        if (rs.next()) {          // if club found in this version - is it active?
            
           if (rs.getInt("inactive") == 1) {    // if inactive

              errMsg = "Error in Login.processExtLogin - Club " + club + " no longer authorized to use ForeTees.";
              fatalError = true;
           }
            
        } else {    // club not found

            errMsg = "Error in Login.processExtLogin - club name invalid, club=" +club+ " was received for " +user;
            fatalError = true;
        }
        stmt.close();              // close the stmt

    } catch (Exception exc) {
        
        errMsg = "Error in Login.processExtLogin - Unable to Connect to Database.  Could be an invalid clubname (club=" +club+ ", user=" +user+ "). Error: " + exc.toString();
        fatalError = true;

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}

        try { con.close(); }
        catch (SQLException ignored) {}
    }


    //
    // Load the JDBC Driver and connect to DB for this club
    //
    try {
        con = dbConn.Connect(club);
    } catch (Exception exc) {

        errMsg = "Login.processExtLogin - Unable to Connect to club: " +club+ "). Error: " + exc.toString();
        fatalError = true;
    }

    //
    //   Check if club is under construction
    //
    boolean construction = checkConstruction(club, "EXT", activity_id, out, con, req);

    if (construction) return;       // exit if site under construction

    
    //
    //   Check if members are blocked from this site
    //
    boolean memberInact = checkMemberInact(club, "EXT", activity_id, out, con, req);

    if (memberInact) return;       // exit if members are blocked
   
       
    
    if (!user.equals("proshop")) {       // if NOT proshop user
       
       //
       //  Make sure the member is valid and active
       //
       try {

           stmt = con.prepareStatement (
               "SELECT CONCAT(name_first, IF(LENGTH(name_mi) = 0, '', CONCAT(' ', name_mi)), ' ', name_last) AS fullName, m_ship, m_type, wc, msub_type FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");

           stmt.clearParameters();        // clear the parms
           stmt.setString(1, user);
           rs = stmt.executeQuery();

           if (!rs.next()) {          // if user not found

               errMsg = "Error in Login.processExtLogin - user not found or inactive: user=" +user+ ", club=" +club;
               fatalError = true;
           } else {
               name = rs.getString("fullname");
               mship = rs.getString("m_ship");       // Get mship type
               mtype = rs.getString("m_type");       // Get member type
               wc = rs.getString("wc");              // Mode of Trans preference
               subtype = rs.getString("msub_type");  // Member sub_type
           }
           stmt.close();              // close the stmt

       } catch (Exception exc) {

           errMsg = "Error in Login.processExtLogin - Unable to verify user. (club=" +club+ ", user=" +user+ ", els=" +els+ ", rls=" +rls+ "). Error: " + exc.toString();
           fatalError = true;

       } finally {

           try { rs.close(); }
           catch (SQLException ignored) {}

           try { stmt.close(); }
           catch (SQLException ignored) {}
       }
    }


    //
    // Alert user if login processing failed
    //
    if (fatalError) {

        out.println(SystemUtils.HeadTitle("Invalid Login"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<p>&nbsp;</p><p>&nbsp;</p>");
        out.println("<BR><H2>Access Rejected</H2><BR>");
        out.println("<BR>Sorry, we cannot complete the connection to ForeTees due to an error.");
        out.println("<BR>Exception: " +errMsg+ "<BR>");
        out.println("<BR>Please contact support@foretees.com or try again later.  Thank you.<BR>");
        out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
        out.println("</CENTER></BODY></HTML>");
        out.close();

        SystemUtils.logError(errMsg);                           // log it

        if (con != null) {

            try { con.close(); }
            catch (Exception exp) { }
        }

        return;
        
        //SystemUtils.sessionLog("Member External Login Failed: " + rls, user, "", club, omit, con);
        //return;
    }


    //
    // IF HERE THEN LOGIN SUCCESSFUL
    //



    //
    //  Check if this club is using the new skin
    //
    new_skin = Utilities.isNewSkinActive(club, con);


    //
    //  Get the remote host id (for tracing client)
    //
    String remote_ip = req.getHeader("x-forwarded-for");
    if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();

    // new stats logging routine
    recordLoginStat(1);
    
    //
    //   Check for proshop user
    //
    if (user.equals("proshop") && caller.equals("invoice")) {      // proshop user for invoicing
       
       invoiceExtLogin(user, club, activity_id, out, req, con);    // go to Invoice page for proshop user (Proshop_invoicing)
       out.close();
       return;
    }
    

    //
    //  Trace all login attempts
    //
    SystemUtils.sessionLog("Member External Login Successful", user, "", club, omit, con);         // log it - no pw

    recordLogin(user, "", club, remote_ip, 1);

    HttpSession session = Utilities.getNewSession(req);     // Create a new session object

    ConnHolder holder = new ConnHolder(con);        // create a new holder from ConnHolder class

    session.setAttribute("connect", holder);        // save DB connection holder
    //session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
    session.setAttribute("ext-user", user);         // save username as ext-user (so that verifyMem would fail)
    session.setAttribute("name", name);             // save user's full name
    session.setAttribute("wc", wc);                 // save user's MOT preference
    session.setAttribute("club", club);             // save club name
    session.setAttribute("mship", mship);           // save member's mship type
    session.setAttribute("mtype", mtype);           // save member's mtype
    session.setAttribute("msubtype", subtype);      // save member's sub_type
    session.setAttribute("activity_id", activity_id);  // activity indicator
    session.setAttribute("caller", "none");            // save caller's name - NOT from a website
    session.setMaxInactiveInterval(5 * 60);         // set the timeout to 5 minutes
    session.setAttribute("new_skin", (new_skin) ? "1" : "0");  // new skin flag


    // based upon caller - jump to desired page
    String base_url = Utilities.getBaseUrl(req, activity_id, club);
    /*
     String base_url = "../" + club + "_golf/";
     if (activity_id == dining_activity_id) {
         base_url = "../" + club + "_dining/";
     } else if (activity_id > 0) {
         base_url = "../" + club + "_flxrez" + activity_id + "/";
     }
     * 
     */
     if (!new_skin) {
         base_url = "";
     }
    
    if (caller.equals("dining")) {

        jumpTo = base_url + "Member_dining";       // Dining Request system

    } else if (caller.equals("event")) {

        //
        //  Check if user is coming from a link in a Dining email for a Dining Event Registration
        //
        if (activity_id == dining_activity_id) {     // if dining

           jumpTo = base_url + "Dining_home";       // FT Dining
           
        } else {
            
           jumpTo = base_url + "Member_events2";    // NOT Dining
        }

    } else {
        
           jumpTo = base_url + "Unsubscribe"; 
    }

    if (new_skin) {
      
      Common_skin.outputHeader(club, activity_id, "Member External Login", true, out, req, 0, "");     // output the page start
      
      out.println("<body>");
      out.println("<div id=\"wrapper_login\" align=\"center\">");
      out.println("<div id=\"title\">Member External Login Page</div>");
      out.println("<div id=\"main_login\" align=\"center\">");
      out.println("<h2>Limited Member Access</h2>");
      out.println("<div class=\"main_message\">");
      out.println("<h4>Welcome <b>" + name + "</b></h4><br /><br />");
      out.println("<center><div class=\"sub_instructions\">");

      out.println("<BR>Please note that this session will terminate if inactive for more than <b>5</b> minutes.");      
      out.println("<br /></div>");
      
      if (caller.equals("event") && activity_id != dining_activity_id) {
          
         
          //
          //  Member clicked on an event link in an email (event is ForeTees or FlxRez)
          //  Member_events2 will display the event info in a smaller modal window, so
          //  we must leave this window open.  Json requires an event map and special button.
          //
          Gson gson_obj = new Gson();                                          // Create Json response
          Map<String, Object> event_map = new LinkedHashMap<String, Object>(); // Create hashmap response
          
          session.setAttribute("user", user);         // *******TEST************ remove this once we get the new skin working properly w/o it

          String ename = "";
          String course = "";
          int e_id = 0;

          try {
                e_id = Integer.parseInt(event_id);
          } catch (Exception ignore) {
          }
          
          //  we need the event name and course
          
          try {

                stmt = con.prepareStatement (
                    "SELECT name, courseName FROM events2b WHERE event_id = ?");

                stmt.clearParameters();        // clear the parms
                stmt.setInt(1, e_id);
                rs = stmt.executeQuery();

                if (rs.next()) {          // if user not found

                    ename = rs.getString("name");
                    course = rs.getString("courseName");     
                }

          } catch (Exception exc) {

                errMsg = "Error in Login.processExtLogin - Unable to locate event. (club=" +club+ ", user=" +user+ ", els=" +els+ ", rls=" +rls+ "). Error: " + exc.toString();
                SystemUtils.logError(errMsg);                           // log it

          } finally {

                try { rs.close(); }
                catch (SQLException ignored) {}

                try { stmt.close(); }
                catch (SQLException ignored) {}
          }
          
          
          //  create the link that will open the modal window and call Member_events2
          
          event_map.clear();
          event_map.put("type", "Member_events2");
          event_map.put("name", ename);
          event_map.put("course", course);
          event_map.put("ext-dReq", "yes");
          out.print("<a href=\"#\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(event_map)) + "\" class=\"standard_button event_button\" style=\"color:black; background-color:lightblue;\">Continue</a>");
          
          //  add an exit button so they can logout
          
          out.println("<BR><BR><a href=\"Logout\" target=\"_top\" class=\"standard_button\" style=\"color:black; background-color:lightgreen;\">Exit</a><BR><BR>");
          
           //  old parms:
           //    out.println("<input type=hidden name=\"usr\" value=\"" + user + "\">");
           //    out.println("<input type=hidden name=\"event_id\" value=\"" + event_id + "\">");
           //    out.println("<input type=hidden name=\"ext-dReq\" value=\"\">");                   // indicate from external source (email) 
           
          
          
          
      } else {
         
         //jumpTo = "http://dev.foretees.com/v5/servlet/Unsubscribe";    // TEMP !!!!!!!!!!!!!!!!!!!!!!!  For testing changes to Unsubscribe
         
          out.println("<form method=\"get\" action=\"" + jumpTo + "\">");

          if (caller.equals("dining")) {   // if older dining request system

                out.println("<input type=hidden name=\"usr\" value=\"" + user + "\">");
                out.println("<input type=hidden name=\"ext-dReq\" value=\"\">"); // caller?
                out.println("<input type=hidden name=\"dReq\">"); // caller?
                out.println("<input type=hidden name=\"date\" value=\"" + date + "\">");
                out.println("<input type=hidden name=\"caller\" value=\"email\">");
                out.println("<input type=hidden name=\"customId\" value=\"" + customId + "\">");

          } else if (caller.equals("event")) {

                if (activity_id == dining_activity_id) {     // if ForeTees Dining

                   out.println("<input type=hidden name=\"event\" value=\"\">");
                   out.println("<input type=hidden name=\"event_id\" value=\"" + event_id + "\">");
                   out.println("<input type=hidden name=\"ext-dReq\" value=\"\">");                   // indicate from external source (email) 
                }
          }

          if (req.getParameter("dining") != null && req.getParameter("dining").equals("ft")) {      // Unsubscribe for Dining

                out.println("<input type=hidden name=\"dining\" value=\"ft\">");
          }

          out.println("<input type=hidden name=\"jsonMode\" value=\"yes\">");
          out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\"></form>");
      }
      
      out.println("</center></div></div>");
        
      Common_skin.outputPageEnd(club, activity_id, out, req);    // finish the page       
      
      
    } else {
      
        out.println("<HTML><HEAD><TITLE>Member External Login Page</TITLE>");

        //out.println("<meta http-equiv=\"Refresh\" content=\"2; url=" + jumpTo + "\">");

        out.println("</HEAD>");
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><H2>Limited Member Access Accepted</H2><BR>");
        out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

        out.println("<font size=\"3\">");
        out.println("<BR>Welcome <b>" + name );

        out.println("</b><BR><BR>");
        out.println("Please note that this session will terminate if inactive for more than <b>5</b> minutes.<BR><BR>");
        //out.println("<br><br>");

        out.println("<form method=\"get\" action=\"" + jumpTo + "\">");

        if (caller.equals("dining")) {   // if dining request system

            out.println("<input type=hidden name=\"usr\" value=\"" + user + "\">");
            out.println("<input type=hidden name=\"ext-dReq\" value=\"\">"); // caller?
            out.println("<input type=hidden name=\"dReq\">"); // caller?
            out.println("<input type=hidden name=\"date\" value=\"" + date + "\">");
            out.println("<input type=hidden name=\"caller\" value=\"email\">");
            out.println("<input type=hidden name=\"customId\" value=\"" + customId + "\">");

        } else if (caller.equals("event")) {

            if (activity_id == dining_activity_id) {     // if dining

               out.println("<input type=hidden name=\"event\" value=\"\">");
               out.println("<input type=hidden name=\"event_id\" value=\"" + event_id + "\">");
               out.println("<input type=hidden name=\"ext-dReq\" value=\"\">");                   // indicate from external source (email) 

            } else {     // not dining

               out.println("<input type=hidden name=\"usr\" value=\"" + user + "\">");
               out.println("<input type=hidden name=\"event_id\" value=\"" + event_id + "\">");
               out.println("<input type=hidden name=\"ext-dReq\" value=\"\">");                   // indicate from external source (email) 
            }
        }

        if (req.getParameter("dining") != null && req.getParameter("dining").equals("ft")) {      // Unsubscribe for Dining

            out.println("<input type=hidden name=\"dining\" value=\"ft\">");
        }

        out.println("</font></td></tr></table>");
        out.println("<br>");
        out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
    }
     
    out.close();

 }


 // *********************************************************
 //  External Login for proshop user to view Invoices
 // *********************************************************

 private void invoiceExtLogin(String user, String club, int activity_id, PrintWriter out, HttpServletRequest req, Connection con) {

    
    HttpSession session = Utilities.getNewSession(req);     // Create a new session object

    ConnHolder holder = new ConnHolder(con);        // create a new holder from ConnHolder class

    session.setAttribute("connect", holder);        // save DB connection holder
    session.setAttribute("ext-user", user);         // save username as ext-user (so that verifyPro will fail)
    session.setAttribute("club", club);             // save club name
    session.setAttribute("mobile", 0);              // not mobile user
    session.setAttribute("tlt", 0);                 // not timeless tees indicator
    session.setAttribute("caller", "none");         // save caller's name - NOT from a website
    session.setAttribute("lottery", "0");           // no lottery support indicator
    session.setAttribute("activity_id", activity_id);  // activity indicator
    session.setMaxInactiveInterval(60*60);        // set inactivity timer for this session (1 hr)
            
     out.println("<HTML><HEAD><TITLE>Proshop External Login Page</TITLE>");

     out.println("<meta http-equiv=\"Refresh\" content=\"2; url=Proshop_invoicing?ext-dReq\">");

     out.println("</HEAD>");
     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
     out.println("<hr width=\"40%\">");
     out.println("<BR><H2>Limited Access Login Accepted</H2><BR>");
     out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

     out.println("<font size=\"3\">");
     out.println("<BR>Welcome to the ForeTees Invoice Viewer<BR><BR>");

     out.println("<form method=\"post\" action=\"Proshop_invoicing\">");

     out.println("<input type=hidden name=\"ext-dReq\" value=\"\">");                   // indicate from external source (email)     
     out.println("</font></td></tr></table>");
     out.println("<br>");
     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("</form></font>");
     out.println("</CENTER></BODY></HTML>");
    
 }   // end of invoiceExtLogin
  
  
 
 
 
 //*****************************************************
 //  Process doGet - request for help
 //*****************************************************
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //
   // Check to see if we have an incoming external login
   //
   if (req.getParameter("extlogin") != null) {

       processExtLogin(req, resp);
       return;
   }

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   if (req.getParameter("verify") != null) {      // Paul's verify script - for testing after a bounce

      systemTest(req, out);
      return;
   }
        
   //
   //  Check if this is a mobile user that has selected a FlxRez Activity and needs to be routed to FlxRez
   //
   if (req.getParameter("mobile") != null && req.getParameter("reroute") != null) {
       
      mobileFlexRoute(req, resp, out);
      return;
   }
     
   //
   //  Check if this is a request for help
   //
   if (req.getParameter("help") != null) {
     
      //
      //  Help requested - Check if call from Member_services to process a request to send email with Mobile password
      //
      if (req.getParameter("mobile") != null) {         // if request for mobile pw

         doPost(req, resp);                             // call doPost processing
         return;
      }      
      

      String club = req.getParameter("clubname");       // which club request came from

      String club2 = club;                             // normally the same value

      if (req.getParameter("club2") != null) {         // if club2 specified (Greeley CC)

         club2 = req.getParameter("club2");            // get it (name of club site to return to)
      }

      
      if (req.getParameter("mobilehelp") == null) {         // if request is NOT from a Mobile login page

         //
         //  Output a page to provide help and a form to request the member's password
         //
         out.println(SystemUtils.HeadTitle("Login Help"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<br><br>");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("<p><b>");
         out.println("Forgot your User Name (Login Id)?<br><br>");
         out.println("</b></p>");
         out.println("</td>");
         out.println("<td width=\"10\">&nbsp;</td>");
         out.println("<td valign=\"top\" height=\"230\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
         out.println("<td width=\"10\">&nbsp;</td>");

         out.println("<td align=\"left\" width=\"280\">");
         out.println("<p>");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("The User Name is normally either the <b>Local Number</b> assigned to you and used to enter scores on the Handicap System, ");
         out.println("or it is the <b>Member Number</b> that is used for billing purposes.");
         out.println("<br><br>");
         out.println("Your password should have been provided to you by the club.  You have the ability to change this once you are logged in.");
         out.println("<br><br>");
         out.println("Please contact your club's professional staff for further assistance.");
         out.println("</font></p>");
         out.println("<A HREF=\"/" +club2+ "\">Back to Login</A><br><br>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("<hr width=\"40%\">");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("<p><b>");
         out.println("Forgot your password?<br><br>");
         out.println("Use the form on the right to have the system email your password. ");
         out.println("For this to work you must have already entered your email address in the system via the 'Settings' tab.");
         out.println("<br><br>");
         out.println("If you have not already entered your email address, please contact your club's professional staff for assistance.");
         out.println("</b></p>");
         out.println("</td>");
         out.println("<td width=\"10\">&nbsp;</td>");
         out.println("<td valign=\"top\" height=\"220\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
         out.println("<td width=\"10\">&nbsp;</td>");

         out.println("<td align=\"left\" width=\"280\">");
         out.println("<p>");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("Enter your User Name (login id) and click on 'Send Password'. ");
         out.println("You will then receive an email containing your password.<br><br>");
         out.println("<form method=\"post\" action=\"Login\">");
         out.println("<b>User Name:</b><br>");
         out.println("<input type=\"text\" name=\"user_name\" size=\"15\" maxlength=\"15\">");
         out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +club+ "\">");
         out.println("<input type=\"hidden\" name=\"club2\" value=\"" +club2+ "\">");
         out.println("<input type=\"hidden\" name=\"help\" value=\"yes\">");
         out.println("<br><br>");
         out.println("<input type=\"submit\" value=\"Send Password\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</font></p>");
         out.println("<A HREF=\"/" +club2+ "\">Back to Login</A>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("<hr width=\"40%\">");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("<p><b>");
         out.println("Able to login, but cannot access the tee sheets?");
         out.println("<br><br>");
         out.println("Receiving an 'Access Error' after logging in?");
         out.println("</b></p>");
         out.println("</td>");
         out.println("<td width=\"10\">&nbsp;</td>");
         out.println("<td valign=\"top\" height=\"330\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
         out.println("<td width=\"10\">&nbsp;</td>");

         out.println("<td align=\"left\" width=\"280\">");
         out.println("<p>");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("There are several issues that can cause this problem:  ");
         out.println("<br><br>");
         out.println("<b>A.</b> Your browser's security setting is set too high (blocks all cookies). ");
         out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
         out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
         out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
         out.println(" Please refer to your browser's Help panels for instructions on changing this setting. ");
         out.println("<br><br><br>");
         out.println("<b>B.</b> It's possible that one or more of your internet files have been corrupted. ");
         out.println("These are files that your browser saves for quicker access to web sites that you frequent. ");
         out.println("To correct this problem, search your browser's Help for 'clearing cache'.");
         out.println("<br><br><br>");
         out.println("<b>C.</b> You may have a Firewall installed in your computer or network. ");
         out.println("If so, check the firewall settings to make sure that it is not blocking the use of cookies. ");
         out.println("<br><br>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("<hr width=\"40%\">");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("<p><b>");
         out.println("Have other problems, want to contact ForeTees?");
         out.println("</b></p>");
         out.println("</td>");
         out.println("<td width=\"10\">&nbsp;</td>");
         out.println("<td valign=\"top\" height=\"300\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
         out.println("<td width=\"10\">&nbsp;</td>");

         out.println("<td align=\"left\" width=\"280\">");
         out.println("<p>");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("We welcome all correspondence. ");
         out.println("Please let us know if you are having problems with ForeTees, or have any suggestions ");
         out.println("on how we might improve the system.");
         out.println("<br><br>If reporting a problem with the system, please include your name, club name and a detailed description");
         out.println("of the problem.");
         out.println("<br><br>Please DO NOT send us emails regarding your tee times.  Contact your golf shop for tee time related issues.");
         out.println("<br><br>");
         out.println("To contact ForeTees, please send an email to:<br><br>");
         out.println(" <a href=\"mailto:support@foretees.com\">support@foretees.com</a>");
         out.println("<br><br>");
         out.println("If you would like to see ForeTees at another club, please send an email to:<br><br>");
         out.println(" <a href=\"mailto:sales@foretees.com\">sales@foretees.com</a><br>");
         out.println("</font></p>");
         out.println("<br><A HREF=\"/" +club2+ "\">Back to Login</A>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("</CENTER></BODY></HTML>");
         out.close();
         
      } else {
         
         out.println(SystemUtils.HeadTitleMobile("Login Help"));
         out.println("<div class=\"headertext\"> ForeTees Mobile Help </div>");
         out.println("<div class=\"smheadertext\">");
         
         out.println("<table width=\"75%\"><tr><td>");           
         out.println("You must use your Mobile username and password to login to this site.");
         out.println("</td></tr>");           
         
         out.println("<tr><td>");           
         out.println("If you have an email address in ForeTees and forgot your Mobile password, enter your Mobile username below and " +
                     "select 'Send Password'.<BR><BR>");
         
         out.println("<form method=\"post\" action=\"Login\">");
         out.println("<b>User Name:</b><br>");
         out.println("<input type=\"text\" name=\"user_name\" size=\"15\" maxlength=\"15\">");
         out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +club+ "\">");
         out.println("<input type=\"hidden\" name=\"club2\" value=\"" +club2+ "\">");
         out.println("<input type=\"hidden\" name=\"help\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"mobile\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"mobilehelp\" value=\"yes\">");
         out.println("<br><br>");
         out.println("<input type=\"submit\" value=\"Send Password\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</td></tr>");           
         
         out.println("<tr><td>");                            
         out.println("If you do not have Mobile credentials, or" +
                     " you cannot remember your Mobile username, then you must login to ForeTees from your PC and select the 'Settings' tab.");
         out.println("</td></tr>");           
         
         out.println("<tr><td>");                                     
         out.println("If you require further assistance, then please email ForeTees at support@foretees.com.  Please include a description " +
                     "of the problem and identify your mobile device.<br>");
         out.println("<br>Thank you!");
         out.println("</td></tr></table>");           
         
         out.println("<br><br><A HREF=\"http://m.foretees.com/" +club2+ "\">Back to Login</A>");
         out.println("</div></body></html>");           
         out.close();
         
      }           // end of IF mobile help
      

   } else {      // NOT a Help request
      
      //
      //  Login attempt is not from our login page or a formal website link (posted form).
      //  Make sure that this is only from a ForeTees sponsored link.
      //
      String club = "";
      String caller = "";
      String user = "";
      String referer = "";
      
      if (req.getParameter("clubname") != null) {

         club = req.getParameter("clubname");       // which club login came from
      }
      if (req.getParameter("caller") != null) {

         caller = req.getParameter("caller");
      }
      if (req.getParameter("user_name") != null) {

         user = req.getParameter("user_name");
      }
      if (req.getHeader("referer") != null) {

         referer = req.getHeader("referer");
      }

      //
      //  Now make sure they specified a club and caller
      //
      if (club.equals("") || caller.equals("")) {  
         
         rejectCaller(out);          // reject if unknown entry
         return;
      }

       
      /*
       * 
       * No-op this for now.  CE and some other website providers have not been able to correct all their 
       * links.  Also, some members enter w/o a referer because their system strips it.  We have eliminated 
       * most of the violators and will allow these for now.
       * 
      boolean rejectLogin = true;      // default to reject this login request
         
      //
      //  Now check for any callers that do not specify a club, or are not authorized to enter here (ForeTees1298 is the CE Bypass Link!!!)
      //
      if (caller.equals("ForeTees1298") || club.equals("whitemanor") || club.equals("edgewood") || 
              club.equals("mountvernoncc") || club.equals("ridgewaygolf") || club.equals("hazeltine")) {  

         rejectLogin = false;      // these are ok for now
      }
         
      if (rejectLogin == true) {  
        
         //
         //  referrer is the url of the website that initiated the link (if null, then user bookmarked the link)
         //
         //  REMOVED - some users were coming in w/o a referer even though they did indeed come from their website - this is not worth the trouble!
         //
         if (referer.equals("") || referer == null) {  

             rejectReferer(out);          // reject if not from a website or our login page
             return;
         }
      
         String logMsg = "Login Failed - Invalid Access to Login.doGet: Caller=" +caller+ ", Club=" +club+ ", Referer=" +referer;
         SystemUtils.logError(logMsg);                           // log it

         // (allow websites to fix their links)
         //rejectCaller(out);          // reject if unknown entry 
         //return;
      }
       */
      //
      //  Log all seamless entries via doGet so we can see who is doig this.  They should be using doPost.
      //
      String logMsg = "*ALERT* Invalid Access to Login.doGet: Caller=" +caller+ ", Club=" +club+ ", Referer=" +referer;
      Utilities.logError(logMsg);                           // log it
      
      if (referer.contains("www.bing.com") || referer.contains("www.google.com") || referer.contains("search.yahoo.com")) {
          rejectReferer(out);          // reject if from a search site
          return;
      }

      
      //
      //  Block all entries from caller=GENID79623.  This id was added in Dec 2013 so we can force new website providers thru doPost.
      //
      if (caller.equalsIgnoreCase("GENID79623")) {
         
          invalidRemote(true, 0, "Invalid request by website. Please have your website provider contact ForeTees for assistance.", req, out, null, 0);
          return;
      }      
      
      
      
      doPost(req, resp);      // call doPost processing
   }
 }


 //*****************************************************
 // Perform doPost processing - someone is logging in
 //*****************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
   
    
   //
   //  Set a P3P Compact Policy Statement in the HTTP Header.
   //  This informs the client of our intended use of the persistent cookie
   //  used to manage this session.
   //
   //  Definitions:
   //
   //      ALL (access=ALL):         We will provide access to ALL information collected.
   //      NOI (access=):            Web site does not collect identified data
   //      DSP (disputes=);          We will settle any disputes.
   //      COR (remedies=):          We will 'correct' any disputes
   //      NID (non-identifiable=):  This session cookie does not collect data and cannot identify the individual person  
   //      CURa (purpose=):          The information is used to complete the activity of the service (N/A if NID)          
   //      OUR (recipient=)          Only our service will process information received in cookie (N/A if NID)
   //      STP (retention=)          Information is retained for the 'stated purpose' (N/A if NID)
   //      PUR (categories=)         We may use the informaiton used to purchase a product or service (N/A if NID)
   //
//   resp.setHeader("P3P","CP=\"ALL DSP COR NID CURa OUR STP PUR\"");       // old version
   //resp.setHeader("P3P","CP=\"NOI DSP COR NID\"");
     
   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");                                      // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);    // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
     
   Connection con = null;                 // init DB objects
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   if (req.getParameter("verify") != null) {

      systemTest(req, out);
      return;
   }
   
   if (req.getParameter("login_message") != null) {

      doLoginMsg(req, out);      // we displayed a login message to a proshop user and they want to disable the message (refer to getMessageBoard)
      return;
   }

   
   //
   // Get the username and password entered.........  
   //
   String username = "";
   String userpw = "";
   String club = "";
   String errMsg = "";
   String caller = "";
   
   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");       // which club login came from
   }
   if (req.getParameter("user_name") != null) {

      username = req.getParameter("user_name").trim();
   }

   //
   //   Check if member prompted for email address
   //                                                                           
   if (req.getParameter("message") != null && req.getParameter("email") != null) {

      processEmail(req, out);     // go process member 'continue' with possible email addresses
      return;
   }

   //
   //   Check if user is asking for password help
   //
   if (req.getParameter("help") != null) {
      
      boolean mobile = false;            // flag for request for mobile password   

      if (req.getParameter("mobile") != null) {

         mobile = true;        // indicate request is for mobile password (from Member_services)
      }

      userHelp(req, out, username, club, mobile);  // go process 'help' request
      return;
   }

   //
   //  normal foretees user
   //
   if (req.getParameter("password") != null) {

      userpw = req.getParameter("password").trim();
   }
      
   //
   // Make sure both were entered.......
   //
   if (req.getParameter("caller") == null && (username.equals( "" ) || userpw.equals( "" ))) {
     
     errMsg = "Username or Password not provided.";
     
     invalidLogin(errMsg, req, out, null);
     return;
   }
   
   if (username.startsWith(support)) {

     supportUser(req, out, userpw, username, club);   // go process 'support' user......

     return;
   }

   if (username.startsWith(sales)) {

     salesUser(req, out, userpw, username, club);   // go process 'sales' user......

     return;
   }

   //
   //  Make sure the club requested is currently running this version of ForeTees.
   //  The user may need to refresh the login page so they pull up the new page.
   //
   try {
       
      con = dbConn.Connect(rev);       // get a connection for this version level

      //
      //  If MFirst and a ClubCorp club, then we must convert the name (i.e. clubcorp_132 = trophyclubcc)
      //
      if (club.startsWith( "clubcorp_")) {       // if 'MembersFirst' and ClubCorp
     
         pstmt = con.prepareStatement (
                  "SELECT ft_name FROM clubcorp WHERE cc_name = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, club);
         rs = pstmt.executeQuery();

         if (rs.next()) {               

            club = rs.getString(1);    // get our name for this club (if not found it will fail below)
         }
         
         pstmt.close();             
      }


      pstmt = con.prepareStatement (
               "SELECT fullname, inactive FROM clubs WHERE clubname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, club);
      rs = pstmt.executeQuery();

      if (!rs.next()) {          // if club not found in this version

         out.println(SystemUtils.HeadTitle("Invalid Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<p>&nbsp;</p><p>&nbsp;</p>");
         out.println("<BR><H2>Login Rejected</H2><BR>");
         if (req.getParameter("caller") != null) {         // if came from a website
             out.println("<BR>Sorry, we cannot complete the connection to ForeTees due to an error in your website's link.");
             out.println("<BR>The clubname value is incorrect or missing.<BR>");
             out.println("<BR>Please contact your club's website administrator (and provide this information) or try again later.  Thank you.<BR>");
             out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
         } else {
             out.println("<BR>Your club is not yet authorized to access ForeTees.");
             out.println("<BR>The site must be completed before you can proceed.<BR>");
             out.println("<BR>Please try again later.  Thank you.<BR>");
             out.println("<BR><BR>Please <A HREF=\"javascript:history.back(1)\">Return</A>.");
         }
         out.println("</CENTER></BODY></HTML>");
         out.println("<!-- " + club + " -->");
         out.close();
         pstmt.close();           // close the stmt
         con.close();
         return;                  // exit

      } else {

          // allow special proshop & adimn users to access inactive clubs
          if (rs.getInt("inactive") == 1 && !username.equalsIgnoreCase("proshop4tea") && !username.equalsIgnoreCase("admin4tea")) {
            
             pstmt.close();           // close the stmt
             con.close();
             
             try {

                 con = dbConn.Connect(club);    // get a connection to this club's db

             } catch (Exception exc) {
             }
             
             if (req.getParameter("caller") != null) {

                caller = req.getParameter("caller");
             }
      
             //
             //  Trace the login attempt
             //
             String logMsg = "Login Attempted on Inactive Site";
             SystemUtils.sessionLog(logMsg, username, "", club, caller, con);         // log it - no pw

             out.println(SystemUtils.HeadTitle("Invalid Login"));
             out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
             out.println("<hr width=\"40%\">");
             out.println("<p>&nbsp;</p><p>&nbsp;</p>");
             out.println("<BR><H2>Site Unavailable</H2><BR>");
             out.println("<BR>Sorry, but your club is no longer authorized to access ForeTees.");
             out.println("<BR>Please contact your club management for more information.<br><br>Thank you.<BR>");
             out.println("</CENTER></BODY></HTML>");
             out.close();
             
             con.close();
             return;                  // exit
          }
      }

      pstmt.close();              // close the stmt
      con.close();                // close the connection
      
   }
   catch (Exception exc) {
      // Error connecting to db....
      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A>.");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }


   //
   //   Check if user came from Club's Web Site provider (AMO or MembersFirst, etc.)
   //
   if (req.getParameter("caller") != null) {
       
       if (req.getParameter("caller").equals(ProcessConstants.FT_PREMIER_CALLER)) {  // ForeTees Website (Flexscape)
           
           flexWebUser(req, out, username, club);        // go process direct access user request
           
       } else {
           
           remoteUser(req, out, username, club);   // All others - go process 'remote' user......
       }
       return;
   }

   if (username.startsWith( admin )) {

     adminUser(req, out, userpw, username, club);   // go process 'admin' user ........
     return;
   }

   if (username.startsWith( proshop )) {

     proshopUser(req, resp, out, userpw, username, club);  // go process 'proshop' user......
     return;

   } else {

     memberUser(req, out, username, userpw, club);   // go process 'member' user.......
   }
 }

 // *********************************************************
 // Process user = SUPPORT...(DB's may not exist yet!!!).
 // *********************************************************

 private void supportUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   if ((user.equals(support) && pw.equals(passwordSup)) || (user.equals(supportpro) && pw.equals(passwordSupPro))) {

     HttpSession session = Utilities.getNewSession(req);  // Create a session object

     ConnHolder holder = null;                   // no con yet, get new one when needed

     session.setAttribute("connect", holder);    // clear connection holder so new one is allocated
     session.setAttribute("user", user);         // save username
     session.setAttribute("club", club);         // save club name
     session.setAttribute("caller", "none");     // save caller's name
     // set inactivity timer for this session (60 mins)
     session.setMaxInactiveInterval(60*60);

     out.println("<HTML><HEAD><Title>Support Login Page</Title>");
     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Support_main\">");
     out.println("</HEAD>");
     out.println("<BODY><CENTER><p>&nbsp;</p><H2>Login Accepted</H2><BR>");
     out.println("<p>&nbsp;</p>");
     out.println("<BR>Welcome Support!");
     out.println("<br><br><font size=\"2\">");
     out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_main\">");
     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("</form></font>");
     out.println("</CENTER></BODY></HTML>");
     out.close();

   } else {

     invalidLogin("Invalid Password.", req, out, null);   // process invalid login information.....
   }
 }   

 // *********************************************************
 // Process user = Sales
 // *********************************************************

 private void salesUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   if (pw.equals( passwordSales )) {

     HttpSession session = Utilities.getNewSession(req);  // Create a session object

     ConnHolder holder = null;                   // no con yet, get new one when needed

     session.setAttribute("connect", holder);    // clear connection holder so new one is allocated
     session.setAttribute("user", user);         // save username
     session.setAttribute("club", club);         // save club name
     session.setAttribute("caller", "none");     // save caller's name
     // set inactivity timer for this session (30 mins)
     session.setMaxInactiveInterval(30*60);

     out.println("<HTML><HEAD><Title>Sales Login Page</Title>");
     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/sales_main.htm\">");
     out.println("</HEAD>");
     out.println("<BODY><CENTER><p>&nbsp;</p><H2>Login Accepted</H2><BR>");
     out.println("<p>&nbsp;</p>");
     out.println("<BR>Welcome Sales!");
     out.println("<br><br><font size=\"2\">");
     out.println("<form method=\"get\" action=\"/" +rev+ "/sales_main.htm\">");
     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("</form></font>");
     out.println("</CENTER></BODY></HTML>");
     out.close();

   } else {

     invalidLogin("Invalid Password.", req, out, null);   // process invalid login information.....
   }
 }

 // *********************************************************
 // Process user = ADMIN....
 // *********************************************************

 private void adminUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;

   String errorMsg = "";
   int activity_id = 0;

   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
       
      con = dbConn.Connect(club);

   } catch (Exception exc) {

     Connerror(out, exc, con);              // go process connection error......
     return;
   }

   if (con == null) {
     Connerror2(out);              // go process connection error......
     return;
   }

   //
   //   Check if club is under construction
   //
   if (!user.equalsIgnoreCase("admin4tea")) {
       
       boolean construction = checkConstruction(club, "", activity_id, out, con, req);

       if (construction) return;       // exit if site under construction
   }
   
   
   //
   //  Get the remote host id (for tracing client)
   //
   String remote_ip = req.getHeader("x-forwarded-for");
   if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();

   //
   // Check password entered against password in DB.........
   //
   try {

      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT password FROM login2 WHERE username = ?");

      pstmt2.clearParameters();         // clear the parms
      pstmt2.setString(1, user);        // put the username field in statement
      rs = pstmt2.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         if (pw.equals( rs.getString("password") )) {

            //
            //  Count the number of users logged in
            //
            countLogin("admin", con);

            // new stats logging routine
            recordLoginStat(4);
               
            //
            //  Trace all login attempts
            //
            errorMsg = "Admin Login Successful, IP=" + remote_ip + "";
            SystemUtils.sessionLog(errorMsg, user, "", club, omit, con);        // log it - no pw

            recordLogin(user, pw, club, remote_ip, 1);
            
            // Save the connection in the session block for later use.......
            
            HttpSession session = Utilities.getNewSession(req);  // Create a session object
            
            ConnHolder holder = new ConnHolder(con);     // create a new holder from ConnHolder class
            
            //
            //  Get TLT indicator
            //
            int tlt = (getTLT(con)) ? 1 : 0;
            
            //
            //  Get Roster Sync indicator
            //
            int rsync = getRS(con);

            session.setAttribute("connect", holder);    // save DB connection holder
            session.setAttribute("user", user);         // save username
            session.setAttribute("club", club);         // save club name
            session.setAttribute("caller", "none");     // save caller's name
            session.setAttribute("tlt", tlt);           // timeless tees indicator
            session.setAttribute("rsync", rsync);       // Roster Sync indicator
            // set inactivity timer for this session (1 hr)
            session.setMaxInactiveInterval(60*60);
            
            out.println("<HTML><HEAD><Title>Admin Login Page</Title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/admin_welcome.htm\">");
            out.println("</HEAD>");
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<p>&nbsp;</p>");
            out.println("<BR><BR><H2>Login Accepted</H2><BR>");
            out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\">");
            out.println("Welcome <b>System Administrator");
            out.println("</b>");
            out.println("</td></tr></table><br>");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" + rev + "/admin_welcome.htm\">");
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();

         } else {

            String errMsg = "Invalid Password. IP=" + remote_ip + "";

            invalidLogin(errMsg, req, out, con);   // process invalid login information.....

            recordLogin(user, pw, club, remote_ip, 0);

            if (con != null) {
               try {
                  con.close();       // Close the db connection........
               }
               catch (SQLException ignored) {
               }
            }
         }    // end of if pw matches
      }       // end of if username found
      pstmt2.close();
   }
   catch (SQLException exc) {
      
      Connerror(out, exc, con);
      return;
   } 
 }

 // *********************************************************
 // Process user = PROSHOP....
 // *********************************************************

 private void proshopUser(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String pw, String user, String club) {

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;

   boolean b = false;
   boolean error = false;
   
   String message_board = "";
   String login_message = "";
   String zipcode = "";
   String errorMsg = "";
   String logMsg = "";
   String tempS = "";

   int mobile = 0;              // flag for mobile user
   int activity_id = 0;         // inticator for default activity (0=golf)
   //int default_activity_id = 0;
      
   // Load the JDBC Driver and connect to DB.........

   try {
      con = dbConn.Connect(club);           // get connection to this club's db
   }
   catch (Exception exc) {

     Connerror(out, exc, con);              // go process connection error......
     return;
   }

   if (con == null) {
     Connerror2(out);                       // go process connection error......
     return;
   }

   //
   //   Check if club is under construction
   //
   if (!user.equalsIgnoreCase("proshop4tea")) {
       
       boolean construction = checkConstruction(club, "", activity_id, out, con, req);

       if (construction) return;       // exit if site under construction
   }
   
   
   String errMsg = "Invalid Password.";

   
   Calendar cal = new GregorianCalendar();        // get todays date
   int yy = cal.get(Calendar.YEAR);
   int mm = cal.get(Calendar.MONTH) +1;
   int dd = cal.get(Calendar.DAY_OF_MONTH);

   int today_date = (yy * 10000) + (mm * 100) + dd;     // save it

   
   
   //
   //  Get the remote host id (for tracing client)
   //
   String remote_ip = req.getHeader("x-forwarded-for");
   if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();
   
   if (club.equals("sangabrielcc") && (user.equalsIgnoreCase("proshopgs") || user.equalsIgnoreCase("proshopbr")) && !remote_ip.equals("67.120.181.121")) {
       
       error = true;
       Utilities.logError("Proshop login attempt by unauthorized IP address (" + remote_ip + ") at " + club + " using Username: " + user + " and Password: " + pw);
       
       ArrayList<String> addresses = new ArrayList<String>();
       addresses.add("aphilip@sangabrielcc.com");
       addresses.add("wjones@sangabrielcc.com");
       addresses.add("bart.kessel@tuckerellis.com");
       
       // Send a notification email to the member support address
       sendEmail.sendFTNotification(addresses, "", "", "Login attempt by unauthorized IP address (" + remote_ip + ") at " + club, 
               "Login attempt by unauthorized IP address (" + remote_ip + ") at " + club + " using Username: " + user + " and Password: " + pw, club, con);
   }
   
   if (club.equals("sangabrielcc") && user.equalsIgnoreCase("proshop1")) {
       ArrayList<String> addresses = new ArrayList<String>();
       addresses.add("aphilip@sangabrielcc.com");
       addresses.add("wjones@sangabrielcc.com");
       addresses.add("bart.kessel@tuckerellis.com");
       
       // Send a notification email to the member support address
       sendEmail.sendFTNotification(addresses, "", "", "Login attempt by unauthorized IP address (" + remote_ip + ") at " + club, 
               "Login attempt by unauthorized IP address (" + remote_ip + ") at " + club + " using Username: " + user + " and Password: " + pw, club, con);
   }

   errorMsg = "Proshop Login Rejected - User/PW & Club = ";

   //
   //  Get the club's zipcode if passed (for weather link)
   //
   if (req.getParameter("zipcode") != null) {

      zipcode = req.getParameter("zipcode");
   }
      
   //
   //  check for mobile device flag
   //
   if (req.getParameter("mobile") != null) {

      tempS = req.getParameter("mobile");

      mobile = Integer.parseInt(tempS);       // mobile indicator (1 = basic mobile support, 2 = some javascript supported, 3 = ??, etc)
      
      
      //  No mobile support for proshop users - reject this request
      
      errMsg = "Mobile currently not supported for proshop users.";

      invalidLogin(errMsg, req, out, con);   // process invalid login information

      recordLogin(user, pw, club, remote_ip, 1);

      if (con != null) {
        try {
            con.close();       // Close the db connection........
      }
        catch (SQLException ignored) { }
      }
      return;   
   }
   
   //
   //  Get TLT indicator
   //
   int tlt = (getTLT(con)) ? 1 : 0;   
   
   
   //
   //  Get club's POS Type for Proshop_slot processing
   //
   String posType = getPOS(con);

   //
   //  Get lottery support indicator for club (for menu processing)
   //
   String lottery = getLottery(con);
   

   //
   //  Check for Auto Login parms from the Dining System - Admin user can link over to configure dining items in ForeTees
   //
   boolean edit_announce = (req.getParameter("edit_announce") != null && req.getParameter("edit_announce").equals("1"));
   boolean manage_email_content = (req.getParameter("manage_email_content") != null && req.getParameter("manage_email_content").equals("1"));
   boolean manage_member_prompts = (req.getParameter("manage_member_prompts") != null && req.getParameter("manage_member_prompts").equals("1"));
   boolean send_email = (req.getParameter("send_email") != null && req.getParameter("send_email").equals("1"));
   boolean auto_dining = false;

   if ((edit_announce == true || manage_email_content == true || manage_member_prompts == true || send_email == true) && 
        user.equals(DINING_USER) && pw.equals(DINING_PW)) {       // if any of these
    
      //
      //  Make sure that Dining is configured for this club
      //
      try {

           stmt = con.createStatement();
           rs = stmt.executeQuery("SELECT organization_id FROM club5 WHERE clubName <> '';");

           if (rs.next()) {
              
               if (rs.getInt(1) > 0) {      // if Dining configured
                  
                  auto_dining = true;    // this is an Dining Admin user linking over to configure some dining items
               }
           }

      } catch (Exception exc) {

           Utilities.logError("Login.proshopUser: Error checking dining config. Club=" + club + ", err=" + exc.toString());

      } finally {

           try { rs.close(); }
           catch (Exception ignore) {}

           try { stmt.close(); }
           catch (Exception ignore) {}
      }
   }
   
   if (auto_dining == true) {      // if this is the dining admin user and dining is configured, go directly to the appropriate page (no frames).
      
       String dining_user = "";
       
       logMsg = "Dining Admin Auto Login Successful";
       SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw
       
       activity_id = 0;         // always use golf
       
       if (send_email == true) {
       
           activity_id = dining_activity_id;       // Dining Admin
       }

       if (req.getParameter("dining_user") != null) {
           dining_user = req.getParameter("dining_user");
       }
       
       // Save the connection in the session block for later use.......

       HttpSession session = Utilities.getNewSession(req);     // Create a session object

       ConnHolder holder = new ConnHolder(con);        // create a new holder from ConnHolder class

       session.setAttribute("connect", holder);        // save DB connection holder
       session.setAttribute("user", user);             // save username
       session.setAttribute("dining_user", dining_user); // dining user id
       session.setAttribute("club", club);             // save club name
       session.setAttribute("mobile", mobile);         // mobile user
       session.setAttribute("tlt", tlt);               // timeless tees indicator
       session.setAttribute("lottery", lottery);       // save club's lottery support indicator
       session.setAttribute("activity_id", activity_id);  // activity indicator
       session.setMaxInactiveInterval(4*60*60);        // set inactivity timer for this session (4 hrs)
                
       out.println("<html><head><title>Dining Admin Link Page</title>");
       if (manage_email_content == true) {
          out.println("<meta http-equiv=\"Refresh\" content=\"0; url=Proshop_content\">");
       } else if (manage_member_prompts == true) {
          out.println("<meta http-equiv=\"Refresh\" content=\"0; url=Proshop_dining\">");
       } else if (send_email == true) {
          out.println("<meta http-equiv=\"Refresh\" content=\"0; url=Proshop_dining_sendEmail\">");
       }
       out.println("</head>");
       out.println("<body bgcolor=\"white\"><center><img src=\"/" +rev+ "/images/foretees.gif\"><br>");
       out.println("<hr width=\"40%\">");
       out.println("<p>&nbsp;</p>");
       out.println("<h2>Dining Admin Access Accepted</h2><br>");

          out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\"><p>");
          out.println("Welcome to the ForeTees Dining Configuration");
          out.println("</p></td></tr></table><br>");             

       out.println("<br><br><font size=\"2\">");
       if (edit_announce == true) {
          out.println("<form method=\"post\" action=\"Proshop_announce?menu=yes\">");   // must use Post - they will have to click Continue!!
       } else if (manage_email_content == true) {
          out.println("<form method=\"get\" action=\"Proshop_content\">");
       } else if (send_email == true) {
          out.println("<form method=\"get\" action=\"Proshop_dining_sendEmail\">");
       } else {
          out.println("<form method=\"get\" action=\"Proshop_dining\">");
       }
       out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form></font>");
       out.println("</center></body></html>");
       out.close();
       return;
   }
   
   
   //
   //  Normal proshop user
   //
   
   // Check inactive first, if not inactive check password entered against password in DB.........
   try {
     
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT password, message, inact, activity_id FROM login2 WHERE username = ? ORDER BY default_entry DESC");

      pstmt2.clearParameters();         // clear the parms
      pstmt2.setString(1, user);        // put the username field in statement
      rs = pstmt2.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         if (rs.getInt("inact") != 1) {             // proshop user is not inactive
          
             if (pw.equals( rs.getString("password") ) && !error) {

                login_message = rs.getString("message");      // get last login message if any
                activity_id = rs.getInt("activity_id");       // this is the id for the users root activity they are working on (can only access this and below)
                 
                // temp to force proshop22 user to tennis for testing
                if (club.equals("admiralscove") && user.equals("proshop22")) activity_id = 1;

                //
                //  Count the number of users logged in
                //
                countLogin("pro", con);

                // new stats logging routine
                recordLoginStat(3);

                recordLogin(user, "", club, remote_ip, 1);
            
                //
                //  Trace all login attempts
                //
                logMsg = "Pro Login Successful";
                SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

                // Save the connection in the session block for later use.......

                HttpSession session = Utilities.getNewSession(req);     // Create a session object

                ConnHolder holder = new ConnHolder(con);        // create a new holder from ConnHolder class

                session.setAttribute("connect", holder);        // save DB connection holder
                session.setAttribute("user", user);             // save username
                session.setAttribute("club", club);             // save club name
                session.setAttribute("caller", "none");         // save caller's name
                session.setAttribute("posType", posType);       // save club's POS Type
                session.setAttribute("zipcode", zipcode);       // save club's ZIP Code
                session.setAttribute("lottery", lottery);       // save club's lottery support indicator
                session.setAttribute("mtypeOpt", "ALL");        // init member classes for name list (Proshop_slot, etc.)
                session.setAttribute("mshipOpt", "ALL");
                session.setAttribute("mobile", mobile);         // mobile user
                session.setAttribute("tlt", tlt);               // timeless tees indicator
                session.setAttribute("activity_id", activity_id);  // activity indicator
                session.setMaxInactiveInterval(4*60*60);        // set inactivity timer for this session (4 hrs)
                
                //
                //  Custom for Castle Pines to display a pace of play report only (no menus) - for outside tv display
                //
                if (club.equals("castlepines") && user.equals("proshoppace")) {
                            
                   out.println("<html><head><title>Proshop Login Page</title>");
                   out.println("<meta http-equiv=\"Refresh\" content=\"0; url=Proshop_custom_pace_display\">");
                   out.println("</head>");
                   out.println("<body bgcolor=\"white\"><center><img src=\"/" +rev+ "/images/foretees.gif\"><br>");
                   out.println("<hr width=\"40%\">");
                   out.println("<p>&nbsp;</p>");
                   out.println("<h2>Login Accepted</h2><br>");
                      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\"><p>");
                      out.println("Welcome <b>Proshop</b>");
                      out.println("</p></td></tr></table><br>");
                   out.println("<br><br><font size=\"2\">");
                   out.println("<form method=\"get\" action=\"Proshop_custom_pace_display\">");
                   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                   out.println("</form></font>");
                   out.println("</center></body></html>");
                   out.close();
                   
                } else {
                   
                   //
                   //  All other proshop users - Determine if there are any messages to display for proshop users today
                   //
                   message_board = getMessageBoard(today_date, club, activity_id, login_message, con);



                   out.println("<html><head><title>Proshop Login Page</title>");
                   if (message_board.equals("")) {
                      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" + rev + "/" + ((mobile > 0) ? "proshop_mobile_home.htm" : "proshop_welcome.htm") + "\">");
                   }
                   out.println("</head>");
                   out.println("<body bgcolor=\"white\"><center><img src=\"/" +rev+ "/images/foretees.gif\"><br>");
                   out.println("<hr width=\"40%\">");
                   out.println("<p>&nbsp;</p>");
                   out.println("<h2>Login Accepted</h2><br>");

                   if (!message_board.equals("")) {
                      out.println("<font size=\"5\">*** Important Notice From ForeTees ***<BR><BR></font>");
                      out.println(message_board);
                   } else {
                      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\"><p>");
                      out.println("Welcome <b>Proshop</b>");
                      out.println("</p></td></tr></table><br>");
                   }                 

                   out.println("<br><font size=\"2\">");
                   out.println("<form method=\"get\" action=\"/" + rev + "/proshop_welcome.htm\">");
                   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                   out.println("</form></font>");
                   out.println("</center></body></html>");
                   out.close();
                }

            /*
                //
                //  6/11/07 Removed the following call to scantee to prevent bottlenecks after servers have been bounced.
                //          If we need to do this here, then we need to save the date in the club5 table on a per club basis.
                //          This current method uses one global date for all clubs and does not work well.
                //

                //
                //   Call scanTee to make sure we have current tee sheets (if it hasn't already run today)
                //
                if (today_date != SystemUtils.scanDate) {             // if not already run today

                   try {

                      resp.flushBuffer();                   // force the repsonse to complete

                      b = SystemUtils.scanTee(con, club);   // check the tee sheets

                   }
                   catch (Exception ignore) {
                   }
                }
             */


             } else {
                 
                // pw does not match
                errorMsg = errorMsg + user + ", " + pw + ", " + club + "  Invalid PW"; 

                errMsg = "Invalid Password.";

                invalidLogin(errMsg, req, out, con);
                
                recordLogin(user, pw, club, remote_ip, 0);
                
                if (con != null) {
                   try {
                      con.close();
                   }
                   catch (SQLException ignored) {
                   }
                }
             }                 // end of if password matches
             
         } else {
             
             errMsg = "Inactive Proshop Account.";
             
             invalidLogin(errMsg, req, out, con);   // process invalid login information
             
             recordLogin(user, pw, club, remote_ip, 0);
             
             if (con != null) {
                try {
                   con.close();       // Close the db connection........
                }
                catch (SQLException ignored) { }
             }
         }
            
      } else {
          
         // no match found in database
          
         errorMsg = errorMsg + user + ", " + pw + ", " + club + "  Invalid Username";

         errMsg = "Invalid Username.";

         invalidLogin(errMsg, req, out, con);   // process invalid login information.....
         
         recordLogin(user, pw, club, remote_ip, 0);

         if (con != null) {
            try {
               con.close();       // Close the db connection........
            }
            catch (SQLException ignored) {
            }
         }
      }         // end of if username found
        
      pstmt2.close();
   }
   catch (SQLException exc) {

      Connerror(out, exc, con);
      return;
   }
 }

 // *********************************************************
 // Process user = Club Member or Hotel User
 // *********************************************************

 private void memberUser(HttpServletRequest req,
                 PrintWriter out, String user, String pw, String club) {

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;    
   ResultSet rs = null;

   Member member = new Member();

   String errorMsg = "Member Login Rejected - User/PW & Club = ";
   String logMsg = "";
   String clubName = "";
   String mship = "";
   String mtype = "";
   String wc = "";
   String email = "";
   String email2 = "";
   String emailErr = "";
   String email2Err = "";
   String zipcode = "";
   String errMsg = "";
   String tempS = "";
   String subtype = "";

   int email_bounced = 0;
   int email2_bounced = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   int default_activity_id = 0;         // inticator for default activity (0=golf)
   int requested_activity_id = -1;      // activity id requested from login page - for stand alone systems (0=golf)


     
   //DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'

   int hotel = 0;
   boolean error = false;           // init error indicator
   boolean allowMobile = false;
   boolean aolEmailUser = false;
   boolean displayAolNotice = false;    // This is to be toggled on and off depending on if we want to display the AOL email throttling/blacklist message for members with "aol.com" email addresses
   int mobile = 0;                  // mobile user indicator
   int mobile_login = 0;
   int ftConnect = 0;               // FT Connect web site indicator

   Calendar cal = new GregorianCalendar();       // get todays date

   int cal_hour = cal.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)
   int cal_min = cal.get(Calendar.MINUTE);

   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   int cal_time = (cal_hour * 100) + cal_min;     // get time in hhmm format

   cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time
   
   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
      con = dbConn.Connect(club);          // get a connection
   }
   catch (Exception exc) {

       if (club.equals("rollinghillsgc") && cal_time >= 300 && cal_time <= 330) {

           String maint_time = Utilities.getSimpleTime(Utilities.adjustTime("Saudi", 300));

           out.println(SystemUtils.HeadTitle("System Maintenance"));
           out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><H2>System Maintenance</H2><BR>");
           out.println("<BR>Sorry, the system is currently undergoing routine maintenance.");
           out.println("<BR><BR>This process begins at " + maint_time + " and takes approximately 30 minutes to complete. During this time the system will not be available.");
           out.println("<BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A> later.");
           out.println("</CENTER></BODY></HTML>");
           out.close();

       } else {

           Connerror(out, exc, con);              // go process connection error......
           return;
       }
   }
   
   if (con == null) {
     Connerror2(out);              // go process connection error......
     return;
   }

   //
   //   Check if club is under construction
   //
   if (!user.equalsIgnoreCase("member4tea")) {
       
       boolean construction = checkConstruction(club, "", default_activity_id, out, con, req);

       if (construction) return;       // exit if site under construction
   }
   
   
   //
   //   Check if members are blocked from this site
   //
   if (!user.equalsIgnoreCase("member4tea")) {
       
       boolean memberInact = checkMemberInact(club, "", default_activity_id, out, con, req);

       if (memberInact) return;       // exit if members are blocked
   }
   
   
   //
   //  Get the club's zipcode if passed (for weather link)
   //
   if (req.getParameter("zipcode") != null) {

      zipcode = req.getParameter("zipcode");
      
    } else {     //  FT Connect websites do not pass the zip - get it from our db

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT zipcode FROM club5");

            if (rs.next()) zipcode = rs.getString(1);

        } catch (Exception exc) {

            Utilities.logError("Login.memberUser - " + club + " - Error looking up zipcode in club5 - ERR: " + exc.toString());
            
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}
        }
   }
   
   
   //
   //  Get FT Connect Web Site Flag from club5 to see if this site uses FT Connect
   //  Note:  Its best to use a flag in club5 rather than have the website pass this
   //         so we can have more control over it.
   //
   try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT ftConnect FROM club5");

        if (rs.next()) {
            
            ftConnect = rs.getInt(1);
        }

   } catch (Exception exc) {

        Utilities.logError("Login.memberUser - " + club + " - Error looking up ftConnect in club5 - ERR: " + exc.toString());
            
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}
   }
   
   if (ftConnect == 1) {                     // if ForeTees Conenct club
       
       requested_activity_id = CONNECT_ID;   // go to Connect's Home Page
   }
   

   //
   //  Check for mobile device flag
   //
   if (req.getParameter("mobile") != null) {

      tempS = req.getParameter("mobile");

      mobile = Integer.parseInt(tempS);       // mobile indicator (1 = basic mobile support, 2 = some javascript supported, 3 = ??, etc)
   }
   
   mobile_login = mobile;           // indicate that user came from mobile login if mobile was specified (used in Logout)
   
       
   //
   //  Check for a specific activity
   //
   if (req.getParameter("activity_id") != null) {        // Activity Id (for stand alone systems)

      tempS = req.getParameter("activity_id");

      requested_activity_id = Integer.parseInt(tempS);      
   }
   
       
    //
    //  Check if Mobile is allowed (for messages)
    //
    allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages?
 
    
   //
   //  Check if Mobile user and Mobile not supported
   //
   if (mobile > 0 && allowMobile == false) {
      
      invalidLogin("Mobile not supported at your club.", req, out, con, mobile);   // process invalid login information..... 
      return;
   }
  
    
   //
   //  Get club's POS Type for Proshop_slot processing
   //
   String posType = getPOS(con);

   int organization_id = Utilities.getOrganizationId(con);

   boolean new_skin = true;     // always true now
   
   //
   //  Get TLT indicator
   //
   int tlt = (getTLT(con)) ? 1 : 0;
   

   //
   //  Get the remote host id (for tracing client)
   //
   String remote_ip = req.getHeader("x-forwarded-for");
   if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();
   
   boolean allow_member4tea = false;
   if (
       (club.startsWith("demo") || club.startsWith("notify")) && user.equals("member4tea") || 
       (Common_Server.SERVER_ID == 4 && user.equals("member4tea"))
      ) { allow_member4tea = true; }
   
   // These IPs have been *BANNED* from accessing ForeTees due to unauthorized access and/or suspected foul play
   if (remote_ip.equals("24.93.205.161") || remote_ip.equals("76.84.94.203") || remote_ip.equals("70.199.5.165")) {
       error = true;
       Utilities.logError("Login attempt by unauthorized IP address (" + remote_ip + ") at " + club + " using Username: " + user + " and Password: " + pw);
       Utilities.logDebug("BSK", "Login attempt by unauthorized IP address (" + remote_ip + ") at " + club + " using Username: " + user + " and Password: " + pw);
       
       ArrayList<String> addresses = new ArrayList<String>();
       addresses.add("support@foretees.com");
       addresses.add("prosupport@foretees.com");
       
       // Send a notification email to the member support address
       sendEmail.sendFTNotification(addresses, "", "", "Login attempt by unauthorized IP address (" + remote_ip + ") at " + club, 
               "Login attempt by unauthorized IP address (" + remote_ip + ") at " + club + " using Username: " + user + " and Password: " + pw, club, con);
   }
   
   //
   // use a prepared statement to find username (string) in the DB..
   //
   try {

      if ( allow_member4tea ) {
          
          // get a random member that won't get emails
          pstmt = con.prepareStatement (
             "SELECT password, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced, username, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone, msub_type " +
             "FROM member2b " +
             "WHERE (emailOpt = 0 OR (email = '' AND email2 = '')) AND email <> ? AND " +
                "inact = 0 AND billable = 1 " +
             "ORDER BY RAND() LIMIT 1");
          
      } else {
         
         if (mobile == 0 && !club.equals("roccdallas")) {                // if NOT a Mobile user
            
             pstmt = con.prepareStatement (
                "SELECT password, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone, msub_type, username " +
                "FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");

         } else {                   // Mobile user - check mobile credentials
            
             pstmt = con.prepareStatement (
                "SELECT mobile_pass, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone, msub_type, username " +
                "FROM member2b WHERE mobile_user = ? AND inact = 0 AND billable = 1");
         }
      }
      // Get user's pw if there is a matching user...

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, user);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         // fake the credentials for member4tea on demo sites
         if ( allow_member4tea ) {
             user = rs.getString("username");
             pw = rs.getString("password");
         } 
          
         //
         // Check password entered against password in DB.........
         //
         if (pw.equalsIgnoreCase(rs.getString(1)) && !error) {        // password MUST be the first field in query above !!

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

            String mi = rs.getString("name_mi");                                // middle initial
            if (!mi.equals( omit )) {
               mem_name.append(" ");
               mem_name.append(mi);
            }
            mem_name.append(" " + rs.getString("name_last"));                     // last name

            String name = mem_name.toString();                          // convert to one string

            // Get the member's mship type and member type

            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type

            // Get the member's email addresses
            user = rs.getString("username");

            email = rs.getString("email");       
            email2 = rs.getString("email2");
            email_bounced = rs.getInt("email_bounced");       
            email2_bounced = rs.getInt("email2_bounced");
            iCal1 = rs.getInt("iCal1");
            iCal2 = rs.getInt("iCal2");
            default_activity_id = rs.getInt("default_activity_id");
            
            if (email.toLowerCase().endsWith("aol.com") || email2.toLowerCase().endsWith("aol.com")) {
                aolEmailUser = true;
            }
            
            //
            //   Check for stand alone system (activity id requested from login page
            //
            if (requested_activity_id != -1) {
               
               default_activity_id = requested_activity_id;      // use the activity id for this system
               
            } else if (!getActivity.isGolfEnabled(con) && getActivity.isConfigured(con) && default_activity_id == 0) {      // If Golf not enabled, but FlxRez is, default to one of the FlxRez activities
                
                Statement stmtx = null;
                ResultSet rsx = null;
                
                try {
                    
                    stmtx = con.createStatement();
                    
                    rsx = stmtx.executeQuery("SELECT activity_id FROM activities WHERE parent_id = 0 AND enabled = 1 ORDER BY activity_id LIMIT 1");
                    
                    if (rsx.next()) {
                        default_activity_id = rsx.getInt(1);
                    }
                    
                } catch (Exception exc) {
                    Utilities.logError("Login.memberUser - " + club + " - Error looking up default FlxRez Activity - ERR: " + exc.toString());
                } finally {
                    
                    try { rsx.close(); }
                    catch (Exception ignore) { }
                    
                    try { stmtx.close(); }
                    catch (Exception ignore) { }
                }
            }
            
            //
            //  If Mobile, then only Golf is supported !!!!!!!!!!!!!!
            //
            int mobile_count = rs.getInt("mobile_count");     // get # of mobile logins for this ueer
            int mobile_iphone = rs.getInt("mobile_iphone");   // get # of iphone logins for this ueer
            subtype = rs.getString("msub_type");              // get member's sub_type if any
            
            if (mobile > 0) {
               
               user = rs.getString("username");       // use normal username for the session
              // default_activity_id = 0;               // make sure it is Golf
               
               countMobile(mobile_count, mobile_iphone, user, req, con);      // bump mobile counter and track mobile device 
            }
            

            // Get the number of visits and update it...

            int count = rs.getInt("count");       // Get count
            count++;                              // bump counter..
            
              
            //  Get wc and last message displayed at login
            wc = rs.getString("wc");          // Get w/c pref
            String message = rs.getString("message");       // last message this member viewed
            
            //
            //  see if we should display a message to this member
            //
            if (message.equals( latest_message )) {      // if newest message was already displayed
              
               message = "";                              // no message to send
               
            } else {
               
               if (allowMobile == true && mobile_count == 0 && mobile == 0) {   // if mobile allowed for this club and user has not used it yet
                  
                  //message = "msg001";      // mobile allowed - show this messages
                  message = "";
                  
               } else {      // do not show the mobile message
                  
                 // if (message.equals( "" )) {
                     
                 //    message = "msg001";
                     
                 // } else {
                     
                     message = "";
                 // }
               }
            }
                        

            PreparedStatement stmt2 = con.prepareStatement (
               "UPDATE member2b SET count = ? WHERE username = ?");

            stmt2.clearParameters();          
            stmt2.setInt(1, count);            // new login count 
            stmt2.setString(2, user);          // username 
            stmt2.executeUpdate();

            stmt2.close();
            
                        
            
            //
            //  If Medinah CC - do not allow certain members to login
            //
            /*
            if (club.equals( "medinahcc" ) && error == false) {
              
               if (mship.equals( "Social" ) || mship.startsWith( "Social Pro" ) || mtype.startsWith( "FM " ) || mtype.startsWith( "Fam Member" )) { 
  
                  error = true;     // do not allow
  
                  errMsg = "Membership Class Not Allowed.";
               }
            }  
            * 
            */
            
            if (error == false) {
              
               //
               //  Count the number of users logged in
               //
               countLogin("mem", con);

               // new stats logging routine
               recordLoginStat(1);
            
               //
               //  Trace all login attempts
               //
              // logMsg = "Member Login Successful. UA=[" +req.getHeader("User-Agent")+ "] ";     // use this one when we need to see the browser/OS info
               if (mobile == 0) {
                   logMsg = "Member Login Successful ";
               } else {
                   logMsg = "Member Mobile Login Successful ";
               }
               SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

               recordLogin(user, pw, club, remote_ip, 1);
            
               // Save the connection in the session block for later use.......
               HttpSession session = Utilities.getNewSession(req);     // Create a new session object
               
               

               ConnHolder holder = new ConnHolder(con);    // create a new holder from ConnHolder class

               session.setAttribute("connect", holder);      // save DB connection holder
               session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
               session.setAttribute("user", user);           // save username
               session.setAttribute("name", name);           // save members full name
               session.setAttribute("club", club);           // save club name
               session.setAttribute("caller", "none");       // save caller's name
               session.setAttribute("mship", mship);         // save member's mship type
               session.setAttribute("mtype", mtype);         // save member's mtype
               session.setAttribute("msubtype", subtype);    // save member's sub-type
               session.setAttribute("wc", wc);               // save member's walk/cart pref (for _slot)
               session.setAttribute("posType", posType);     // save club's POS Type
               session.setAttribute("zipcode", zipcode);     // save club's zipcode
               session.setAttribute("mobile", mobile);       // mobile user
               session.setAttribute("mobile_login", mobile_login);       // user come from mobile login page?
               session.setAttribute("tlt", tlt);             // timeless tees indicator
               session.setAttribute("activity_id", default_activity_id);  // activity indicator
               session.setAttribute("organization_id", organization_id);  // organization_id (set if using ForeTeesDining system)
               session.setAttribute("new_skin", (new_skin) ? "1" : "0");  // new skin flag
               session.setAttribute("ftConnect", ftConnect);             // FT Connect Web Site indicator

               //
               // set inactivity timer for this session
               //  use 10 mins to prevent user from hanging a tee slot and connection too long
               //
               if ( (club.startsWith("demo") || club.startsWith("admiralscove")) && Common_Server.SERVER_ID == 4 ) {
                   session.setMaxInactiveInterval(4*60*60);  // dev account
               } else if (club.equals("oakleycountryclub")) {
                   session.setMaxInactiveInterval(30*60);   // set inactive timeout to 30 min for Oakley CC
               } else {
                   session.setMaxInactiveInterval( MEMBER_TIMEOUT );
               }
               
               String base_url = Utilities.getBaseUrl(req, default_activity_id, club);
                /*
                String base_url = "../"+club+"_golf/";
                if(default_activity_id == dining_activity_id){
                    base_url = "../"+club+"_dining/";
                } else if(default_activity_id > 0) {
                    base_url = "../"+club+"_flxrez"+default_activity_id+"/";
                }
                 * 
                 */
                if(!new_skin){
                    base_url = "";
                }
               
               
               //
               //  Output the welcome message based on device type (Mobile or Standard)
               //
               if (mobile == 0) {        // if standard device/browser
                  
                  //
                  //   Check for iPad (or like) device and prompt for standard or mobile access.
                  //
                  boolean enableAdvAssist = Utilities.enableAdvAssist(req);     // check for iPad (or like) device - false indicates iPad

                  //if (!new_skin && enableAdvAssist == false && allowMobile == true && default_activity_id == 0) {   // if Golf, iPad and mobile ok for this site
                  //if (enableAdvAssist == false && allowMobile == true && default_activity_id == 0) {   // if Golf, iPad and mobile ok for this site
                  if (enableAdvAssist == false && allowMobile == true) {   // if mobile device detected and mobile ok for this site
                     
                     promptIpad(club, name, new_skin, default_activity_id, out, req);                 // prompt user for mobile or standard interface
               
                     countMobile(mobile_count, mobile_iphone, user, req, con);      // bump mobile counter and track mobile device (this wasn't done above) 
                     return;
                  }
               
                  //
                  //  Check for IE 6 browser
                  //
                  if (new_skin) {
                  
                      boolean oldBrowser = checkOldBrowser(club, name, out, req);
          
                      if (oldBrowser == true) {

                          session.setAttribute("new_skin", "0");       // revert back to old skin in case user wants to continue                    
                          return;
                      }   
                  }
                  
                  
                  //
                  //   Not iPad and Not mobile device - Validate the email addresses
                  //
                  if (!email.equals( "" )) {                   // if specified

                     FeedBack feedback = (member.isEmailValid(email));

                     if (!feedback.isPositive()) {              // if error

                        emailErr = feedback.get(0);             // get error message
                     }
                  }
                  if (!email2.equals( "" )) {                   // if specified

                     FeedBack feedback = (member.isEmailValid(email2));

                     if (!feedback.isPositive()) {              // if error

                        email2Err = feedback.get(0);             // get error message
                     }
                  }


                  //
                  //   Determine if auto-refresh to be used
                  //
                  boolean autoRefresh = true;

                  if (count == 0 || email.equals( "" ) || email_bounced > 0 || email2_bounced > 0 || iCal1 == -1 || iCal2 == -1) {

                     autoRefresh = false;
                  }

                  if (!emailErr.equals( "" ) || !email2Err.equals( "" )) {

                     autoRefresh = false;
                  }
                  
                  
                  int delay = 1;    // default pause for welcome message
                  
                  if (club.equals("woodlandscountryclub")) {
                     
                     delay = 3;      // 3 seconds for them
                  }
                  
                  
                  String msg1 = "Please note that this session will terminate if inactive for more than " + (MEMBER_TIMEOUT / 60) + " minutes.";

                  if (club.equals("oakleycountryclub")) {
                      msg1 = "Please note that this session will terminate if inactive for more than 30 minutes.";      // Print separate message for Oakley CC due to custom inactive period
                  }

                  if (new_skin) {
                     
                      int refresh_val = 0;
                      String refresh_url = "";

                      if (autoRefresh == true && (!club.equals("rollinghillsgc") || cal_time < 230 && cal_time > 300)) {
                         
                         if (message.equals( "" ) && (!aolEmailUser || !displayAolNotice)) {      // if no message to display
                            if (club.equals("wingedfoot")) {
                               refresh_val = delay;
                               refresh_url = base_url + "Member_searchpast?subtee=gquota";       // wfmember_welcome.htm
                            } else {
                               if (default_activity_id == dining_activity_id) {     // if dining
                                  refresh_val = delay;
                                  refresh_url = base_url + "Dining_home";
                               } else {
                                  refresh_val = delay;
                                  refresh_url = base_url + "Member_announce";
                               }
                            }
                         } else {
                            refresh_val = delay;
                            refresh_url = base_url + "Member_msg";
                         }
                      }                      

                      // new skin (no meta refresh - use js instead)
                      Common_skin.outputHeader(club, default_activity_id, "Member Login Page", true, out, req, refresh_val, refresh_url);
                                            

                      try {

                          clubName = Utilities.getClubName(con);        // get the full name of this club

                      } catch (Exception exc) {}

                      Common_skin.outputBody(club, default_activity_id, out, req);

                      out.println("<div id=\"wrapper_login\">");
                      out.println("<div id=\"title\">" + clubName + "</div>");

                      //Common_skin.outputPageStart(club, default_activity_id, out, req);
                      out.println("<div id=\"main_login\">");

                      // display welcome message
                      out.println("<div id=\"login_welcome\">");
                      out.println("<p>" + msg1 + "</p><br>");
                      if (count > 1) {
                         out.println("<p>You've now logged in " + count + " times.</p>");
                      } else {
                         out.println("<p>This is your first login.</p>");
                      }
                      out.println("</div>");

                      out.println("<h1>Login Accepted</h1>");

                      out.println("<h2 style=\"padding:10px\">Welcome " + ((count > 1) ? "back " : "") + name + ".</h2>");

                      if (autoRefresh == false) out.println("<div class=\"main_instructions\">");    // create a colored box for any messages that might follow
                      
                  } else {

                      //
                      //   Output welcome page (old skin)
                      //
                      out.println("<HTML><HEAD><Title>Member Login Page</Title>");

                      if (autoRefresh == true && (!club.equals("rollinghillsgc") || cal_time < 230 && cal_time > 300)) {

                         if (message.equals( "" )) {      // if no message to display
                            if (club.equals("wingedfoot")) {
                               //out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_searchpast?subtee=gquota\">");
                               out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/wfmember_welcome.htm\">");
                            } else {
                               if (default_activity_id == dining_activity_id) {     // if dining
                                  out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url="+base_url+"Dining_home\">");
                               } else {
                                  out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url=/" + rev + "/member_welcome.htm\">");
                               }
                            }
                         } else {
                            out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url="+base_url+"Member_msg\">");
                         }
                      }

                      out.println("</HEAD>");

                      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                      out.println("<hr width=\"40%\">");

                      out.println("<BR><H2>Login Accepted</H2><BR>");

                      out.println("<table width=\"70%\" border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
                      out.println("<font size=\"3\">");
                      out.println("<p>Welcome <b>" + name + "</b></p>");
                      out.println("<p>" + msg1 + "</p>");

                  }

                  // now see what else we have to display on the page

                  if (club.equals("rollinghillsgc") && cal_time >= 230 && cal_time <= 300) {

                      String maint_time = Utilities.getSimpleTime(Utilities.adjustTime("Saudi", 300));
                      
                      out.println("<p><b>Notice</b>: The system will be undergoing routine maintenance starting at <b>" + maint_time + "</b> and lasting for approximately <b>30 minutes</b>." +
                              "<br>During this time the system will be unavailable.<br></p>");
                  }

                  //
                  // if first login - display password change notice
                  //
                  if (count == 1) {

                     // TODO: UPDATE THESE INSTRUCTIONS
                     out.print("<p>");
                     out.println("<b>Notice:</b>  Since this is your first visit, we strongly recommend that you <b>change your password</b>. ");
                     out.println("To do this, select the 'Settings' tab from the navigation bar on the top of the page after this login completes.");
                     out.println("</p>");
                  }
/*
                  // if iPad (or other tablet user once we test them) then offer mobile site
                  if (enableAdvAssist == false && allowMobile == true && default_activity_id == 0) {

                     out.print("<p>");
                     out.print("We have detected that you are using a mobile device to access ForeTees. ");
                     out.print("Please be advised that our standard site is not yet optimized for this device, so you may encounter problems. ");
                     out.print("We suggest that you use our Mobile site to ensure full functionality.");
                     out.println("</p>");

                     out.println("<form method=\"get\" action=\"Member_msg\">");

                     out.println("<input type=\"submit\" name=\"useMobile\" value=\"Go To Mobile Site\" style=\"text-decoration:underline; background:#8B8970\">");
                     out.println("<BR><BR>");
                     out.println("<input type=\"submit\" name=\"useStandard\" value=\"Go To Standard Site\" style=\"text-decoration:underline; background:#8B8970\">");

                     out.println("</form>");

                  }
*/

                  //
                  // Check for various email problems
                  //
                  if (email_bounced == 1 || email2_bounced == 1) {


                     //
                     // if one of their bounce flags are set then display notice and allow reset
                     //
                     out.println("<h3><b>WARNING: Email bouncing!!</b></h3>");
                     out.println("We recently tried to send you an email at ");

                     if (email_bounced == 1) {

                        out.print(email);

                     } else {

                        out.print(email2);
                     }
                     out.print(" and it bounced back to us.<br>" +
                               "We've had to temporarily disable sending you any emails until you resolve this problem.");
                     out.println("<BR><BR>To correct this, update your email below or select the 'Settings' tab from the<br>" +
                                 "navigation bar on the top of most pages and follow the insructions in the email<br>" +
                                 "section next to the word 'Important'.");
                     out.println("<br><br>If the current email is correct, simply click 'Continue' below and ForeTees will<br>" +
                             "attempt to continue using the same email.  If you would like to remove the current email<br>" +
                             "address all together, erase it from the field below and click 'Continue'.");

                     out.println("<br><br>");

                     //if (autoRefresh == false) {

                     if (email.equals( "" )) {
                        out.println("Please add at least one valid email address below.");
                     } else {
                        out.println("Please verify and/or change the email address(es) below.");
                     }
                     out.print("&nbsp;&nbsp;");
                     out.print("Thank you!");
                     out.print("<br><br>");

                     out.println("<form method=\"post\" action=\""+base_url+"Login\">");
                     out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");
                     out.println("<input type=\"hidden\" name=\"bounced\" value=\"" + ((email_bounced == 1) ? "email" : "email2") + "\">");

                     out.println("<b>Email Address " + ((email_bounced == 1) ? "1" : "2") + ":</b>&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"email\" value=\"" + ((email_bounced == 1) ? email : email2) + "\" size=\"40\" maxlength=\"50\">");
                     out.println("<br><br>");

                             
                  } else if (autoRefresh == false) {

                      
                      
                      // this block here was right above outside this if block.  it should be able to be in here
                      // though since at least one of these conditions is true if we are inside the if
                      
                      if (!emailErr.equals( "" ) || !email2Err.equals( "" )) {   // problem with email address?

                         if (!emailErr.equals( "" )) {   // problem with email1 address?

                            out.println("<b>Warning:</b>  Your email address (" +email+ ") is invalid.");
                            out.println("<BR>" +emailErr);

                         } else {

                            out.println("<b>Warning:</b>  Your email address (" +email2+ ") is invalid.");
                            out.println("<BR>" +email2Err);
                         }
                         out.println("<BR><BR>To correct this, please change it below.");
                         out.println("<BR>Please verify and/or change the email address(es) below.");

                      } else if (email.equals( "" )) {

                            out.println("<BR><b>Notice:</b> In order to receive email notifications and to stay informed,");
                            out.println("<BR>you must maintain a current, working email address.");
                            out.println("<BR>Please add at least one valid email address below.");

                      } else if (iCal1 == -1 || iCal2 == -1) {

                            out.println(iCalNotice);
                            out.println("<BR>Please select your iCalendar preferences.");
                      }
/*
                      out.println("<BR>");
                  
                      
                     if (email.equals( "" )) {
                        out.println("Please add at least one valid email address below.");
                     } else if (iCal1 == -1 || iCal2 == -1) {
                        out.println("Please select your iCalendar preferences.");
                     } else {
                        out.println("Please verify and/or change the email address(es) below.");
                     }
*/                   
                      
                     out.print("&nbsp;&nbsp;");
                     out.print("Thank you!");
                     out.print("<br><br>");

                     
                     out.println("<form method=\"post\" action=\""+base_url+"Login\">");
                     out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");

                     out.println("<b>Email Address 1:</b>&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"email\" value=\"" +email+ "\" size=\"40\" maxlength=\"50\">");

                     out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">iCal attachments</a> at this email address? ");
                     out.println("<select size=\"1\" name=\"iCal1\">");
                      out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
                      out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
                     out.println("</select>");

                     out.println("<br><br>");
                     out.println("<b>Email Address 2:</b>&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"email2\" value=\"" +email2+ "\" size=\"40\" maxlength=\"50\">");

                     out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">iCal attachments</a> at this email address? ");
                     out.println("<select size=\"1\" name=\"iCal2\">");
                      out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
                      out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
                     out.println("</select>");

                     out.println("<br><br>");

                  } else {
                      
                     //
                     // there was nothing requiring user intervention
                     // if there's a message to display then go there
                     // otherwise route according to activty
                     //
                     if (message.equals( "" )) {      // if no message to display
                        /*if (default_activity_id == dining_activity_id) {     // if dining
                            out.println("<form method=\"get\" action=\""+base_url+"Dining_home\">");
                        } else*/ if (!new_skin) {
                            out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
                        } else {
                            out.println("<form method=\"get\" action=\""+base_url+"Member_announce\">");
                        }
                     } else {
                        // go display message
                        out.println("<form method=\"get\" action=\""+base_url+"Member_msg\">");
                     }
                        

                     // add spacer (no other fill for box to stretch it)
                     if (new_skin) {

                         out.println("<div style=\"height:50px\"></div>");
                     }

                  } // end of check for messages to display


                  //
                  // close the page
                  //
                  if (new_skin) {

                      if (autoRefresh == false) out.println("</div>");    // end colored message box and reposition for Continue button                     
  
                      out.println("<input type=\"submit\" value=\"Continue\" class=\"login_button_lg\">");
                      out.println("</form>");

                      out.println("<div class=\"clearfloat\"></div>");
                      
                      out.println("</div>");    // end wrapper_login 

                      Common_skin.outputPageEnd(club, default_activity_id, out, req);
                              
                  } else {

                      out.println("</font></td></tr></table>");

                      out.println("<br>");
                      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                      out.println("</form></font>");
                      out.println("</CENTER></BODY></HTML>");

                  }

                  out.close();
              
               
               } else {
                   
                    //
                    //  Mobile Device
                    //
                    promptMobileUser(club, name, session, out, con);   // prompt mobile user to see how they want to proceed
                    
               }                          // end of IF mobile device
               
            }              // end of IF no error

         } else {          // password did not match

            error = true; // indicate an error occurred

            if (mobile == 0) {
               errMsg = "Invalid Password.";
            } else {
               errMsg = "Invalid Password for Mobile User.";
            }
            errorMsg = errorMsg + user + " " + pw + " " + club + " invalid PW (IP=" + remote_ip + ")";    // build error msg
              
         } // end of if pw matches
           
      } else {        // member username not found in member2b table


         //
         // HOTEL USERS
         //
          
         // if this club supports hotel users, lets check the hotel3 table to
         // see if this username exists
          
         //
         //  Check if Hotels are supported for this club
         //
         PreparedStatement pstmth1 = con.prepareStatement (
            "SELECT clubName, hotel " +
            "FROM club5");

         pstmth1.clearParameters();         // clear the parms
         rs = pstmth1.executeQuery();       // execute the prepared stmt

         if (rs.next()) {

            clubName = rs.getString(1);       // Get club's name
            hotel = rs.getInt(2);             // Get hotel indicator
         }
         pstmth1.close();
         
         if (hotel > 0) {          // if hotels supported

            pstmth1 = con.prepareStatement (
               "SELECT password, name_last, name_first, name_mi, message " +
               "FROM hotel3 WHERE username = ?");

            // Get user's pw if there is a matching user...

            pstmth1.clearParameters();         // clear the parms
            pstmth1.setString(1, user);        // put the username field in statement
            rs = pstmth1.executeQuery();       // execute the prepared stmt

            if (rs.next()) {

               //
               // Check password entered against password in DB.........
               //
               if (pw.equals( rs.getString(1) )) {

                  //
                  //  Count the number of users logged in
                  //
                  countLogin("hotel", con);

                  recordLogin(user, pw, club, remote_ip, 1);
                  
                  //
                  //  Trace all login attempts
                  //
                  logMsg = "Hotel Login Successful";
                  SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

                  // Get the member's full name.......

                  StringBuffer mem_name = new StringBuffer(rs.getString(3));  // get first name

                  String mi = rs.getString(4);                                // middle initial
                  if (!mi.equals( omit )) {
                     mem_name.append(" ");
                     mem_name.append(mi);
                  }
                  mem_name.append(" " + rs.getString(2));                     // last name

                  String name = mem_name.toString();                          // convert to one string

                  // Save the connection in the session block for later use.......
                  HttpSession session = Utilities.getNewSession(req);     // Create a new session object

                  ConnHolder holder = new ConnHolder(con);    // create a new holder from ConnHolder class

                  session.setAttribute("connect", holder);    // save DB connection holder
                  session.setAttribute("sess_id", id);        // set session id for validation ("foretees")
                  session.setAttribute("user", user);         // save username
                  session.setAttribute("name", name);         // save members full name
                  session.setAttribute("club", club);         // save club name
                  session.setAttribute("caller", "none");     // save caller's name
                  session.setAttribute("zipcode", zipcode);   // save club's zipcode
                  session.setAttribute("tlt", tlt);           // timeless tees indicator
                  session.setAttribute("activity_id", 0);     // activity_id (default to 0 for now)
                  session.setAttribute("new_skin", (new_skin) ? "1" : "0");  // new skin flag
                  
                  //
                  // set inactivity timer for this session (2 hrs)
                  //
                  session.setMaxInactiveInterval(2*60*60);

                  out.println("<HTML><HEAD><Title>Hotel Login Page</Title>");
                  if (club.startsWith("tpc")) {
                     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/hotel_home.htm\">");
                  } else {
                     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/hotel_selmain.htm\">");
                  }
                  out.println("</HEAD>");
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<p>&nbsp;</p>");
                  out.println("<BR><H2>Login Accepted</H2><BR>");
                  out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

                  out.println("<BR>Welcome to the ForeTees Tee Time Reservation system<br>for " + clubName );

                  out.println("<BR><BR>");
                  out.println("</td></tr></table>");
                  out.println("<br><br><font size=\"2\">");
                  if (club.startsWith("tpc")) {
                     out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_home.htm\">");
                  } else {
                     out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_selmain.htm\">");
                  }
                  out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();

               } else {                     // password does not match

                  error = true;             // indicate an error occurred

                  errMsg = "Invalid Password.";

                  errorMsg = errorMsg + user + " " + pw + " " + club + " invalid PW";    // build error msg

               }     // end of if hotel pw matches

            } else {                     // member & hotel username not found

               error = true;             // indicate an error occurred

               errMsg = "Invalid Username.";

               errorMsg = errorMsg + user + " " + pw + " " + club + " invalid user";   // build error msg

            }
            pstmth1.close();

         } else {                     // member username not found and hotels not supported

            error = true;             // indicate an error occurred

            errMsg = "Invalid Username.";

            errorMsg = errorMsg + user + " " + pw + " " + club + " invalid user";   // build error msg

         }     // end of if hotels supported 
         
      }        // end of if member username found

      pstmt.close();

      if (error == true) {         // if login failed   

         invalidLogin(errMsg, req, out, con, mobile);   // process invalid login information.....
         
         recordLogin(user, pw, club, remote_ip, 0);

         if (con != null) {
            try {
               con.close();       // Close the db connection........
            }
            catch (SQLException ignored) {
            }
         }
      }        // end of IF error
   }
   catch (SQLException exc) {

      Connerror(out, exc, con);
      return;
      
   }/* finally {

       if (con != null) {
          try {
             con.close();
          } catch (SQLException ignored) {}
       }
   }*/
   
 }


 // *********************************************************
 // Process user = remote web site....
 // *********************************************************

 private void remoteUser(HttpServletRequest req,
                 PrintWriter out, String user, String club) {

   Connection con = null;                  // init DB objects
   ResultSet rs = null;
   PreparedStatement stmt = null;

   Member member = new Member();

   String lname = "";
   String username = "";
   String mship = "";
   String mtype = "";
   String wc = "";
   String zipcode = "";
   String primary = "No";
   String mNumParm = "No";
   String mNum = "";
   String oldemail1 = "";
   String oldemail2 = "";
   String email1 = "";
   String email2 = "";
   String emailErr = "";
   String email2Err = "";
   String logMsg = "";
   String mapping = "No";
   String stripZero = "No";
   String errMsg = "";
   String eventName = "";
   String courseName = "";
   String caller = "";
   String seamless_caller = "";               // caller value in club5
   String clubName = "";
   String subtype = "";
     
   int rsynci = 0;                      // values from club5 table for this club
   int seamless = 0;
   int primaryif = 0;
   int mnumi = 0;
   int mappingi = 0;
   int stripzeroi = 0;
   int signUp = 0;
   int stripAlpha = 0; 
   int stripAlphaNum = 0;               // Used to signify that alpha-numeric strings ending a member number should be stripped (C1, C2, C3, etc).
   int stripDash = 0;
   int stripSpace = 0;

   int default_activity_id = 0;         // inticator for default activity (0=golf)
   int event_activity_id = 0;           // activity id for event, if event name is passed (0=golf)
   int organization_id = 0;
   int email_bounced = 0;
   int email2_bounced = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   
   int mobile = 0;                     // mobile user indicator
   int mobile_count = 0;
   int mobile_iphone = 0;
     
   boolean rsync = false;
   boolean primaryCE = false;
   boolean stripEnd = false;
   boolean fatalError = false;
   boolean allowMobile = false;
   boolean CEmobile = false;
   boolean mobileApp = false;
   boolean aolEmailUser = false;
   boolean skipClubSettings = false;
   boolean displayAolNotice = false;    // This is to be toggled on and off depending on if we want to display the AOL email throttling/blacklist message for members with "aol.com" email addresses

   //DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'

   //
   //  Get the caller's id
   //
   if (req.getParameter("caller") != null) {

      caller = req.getParameter("caller");
      
   } else {
      
      errMsg = "Error in Login.remoteUser - Caller parm not provided by web site.";
      fatalError = true;
   }
    
   if (fatalError == false) {

      if (user == null || user.equals("")) {      // if username not provided

         out.println(SystemUtils.HeadTitle("Invalid Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<p>&nbsp;</p><p>&nbsp;</p>");
         out.println("<BR><H2>Access Rejected</H2><BR>");
         out.println("<BR>Sorry, critical information is missing so we cannot complete the connection to ForeTees.");
         out.println("<BR><BR>Username or Member Number was not provided. This is often the result if you do not first");
         out.println("<BR>login to your club's website.  Please be sure to login before attempting to access ForeTees.");
         out.println("<BR><BR>If problem persists, please contact your club (and provide this information) or try again later.  Thank you.<BR>");
         out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
         out.println("<BR><BR>Or <A HREF=\"javascript:history.back(1)\">Go Back</A> (if the Exit fails)");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }
    
   if (fatalError == false) {

      //
      //  Make sure the club requested is currently running this version of ForeTees.
      //  The user may need to refresh the login page so they pull up the new page.
      //
      try {

         con = dbConn.Connect(rev);       // get a connection for this version level

         //
         //  Make sure club exists in our system
         //
         stmt = con.prepareStatement (
                  "SELECT fullname FROM clubs WHERE clubname = ?");

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, club);
         rs = stmt.executeQuery();

         if (!rs.next()) {          // if club not found in this version

            errMsg = "Error in Login.remoteUser - club name invalid, club=" +club+ " was received from web site.";        
            fatalError = true;
         }

      }
      catch (Exception exc) {
         // Error connecting to db....
         errMsg = "Error in Login.remoteUser - Unable to Connect to Database.  Could be an invalid clubname (club=" +club+ "). Error: " + exc.toString();    
         fatalError = true;
         
      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { stmt.close(); }
          catch (Exception ignore) {}

          try { con.close(); }
          catch (Exception ignore) {}
      }
   }

   if (fatalError == false) {

      //
      // Load the JDBC Driver and connect to DB for this club
      //
      try {
         con = dbConn.Connect(club);          // get a connection
      }
      catch (Exception exc) {

         errMsg = "Login.remoteUser - Unable to Connect to club: " +club+ ", User = " +user+ ". Error: " + exc.toString();
         fatalError = true;
      }
      
      //
      //   Check if club is under construction
      //
      boolean construction = checkConstruction(club, caller, default_activity_id, out, con, req);

      if (construction) return;       // exit if site under construction
      
      //
      //   Check if members are blocked from this site
      //
      boolean memberInact = checkMemberInact(club, caller, default_activity_id, out, con, req);

      if (memberInact) return;       // exit if members are blocked   
   }


   //
   //  If any of above failed, return error message to user and log error in v5 error log table
   //
   if (fatalError == true) {

      out.println(SystemUtils.HeadTitle("Invalid Login"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>Access Rejected</H2><BR>");
      out.println("<BR>Sorry, we cannot complete the connection to ForeTees due to an error in your website's link.");
      out.println("<BR>Exception: " +errMsg+ "<BR>");
      out.println("<BR>Please contact your club's website administrator (and provide this information) or try again later.  Thank you.<BR>");
      out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      
      SystemUtils.logError(errMsg);                           // log it

      if (con != null) {

         try {
            con.close();                // close the connection
         }
         catch (Exception exp) {
         }
      }      
      return;
   }

   
    organization_id = Utilities.getOrganizationId(con);

    //boolean new_skin = Utilities.isNewSkinActive(club, con);
    boolean new_skin = true;
    
    //
    //  CLUBSTER is the ClubTec mobile app (similar to MyClub)
    //
    if (caller.equals("PDGMOBILE") || caller.equals("CLUBSTER") || (caller.equals("GJONAS74912") && req.getParameter("mobile") != null)) {
        
        mobileApp = true;
    }


    //
    //  Check if Mobile is allowed (for messages)
    //
    allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages?
 
    
    // skip the settings if coming from a Mobile App or a Clubster Website since the club can have a web interface and an interface with others.
    
    if (caller.equals( "MYCLUB" ) || mobileApp == true || caller.equals( "CLUBSTERWEB" )) { 
       
       skipClubSettings = true;
    }
       
    if (skipClubSettings == false) {     // skip the settings if mobileApp or secondary website
       
      //
      //****************************************************
      //  Get the I/F options from club5 (set via support) 
      //****************************************************
      //
      try {

         stmt = con.prepareStatement (
                  "SELECT rsync, seamless, zipcode, primaryif, mnum, mapping, stripzero, seamless_caller, stripalpha, stripdash " +
                  "FROM club5");

         stmt.clearParameters();        // clear the parms
         rs = stmt.executeQuery();

         if (rs.next()) {          // get club options 

            rsynci = rs.getInt(1);
            seamless = rs.getInt(2);
            zipcode = rs.getString(3);
            primaryif = rs.getInt(4);
            mnumi = rs.getInt(5);
            mappingi = rs.getInt(6);
            stripzeroi = rs.getInt(7);
            seamless_caller = rs.getString("seamless_caller");       // Caller value saved for this club
            stripAlpha = rs.getInt("stripalpha");
            stripDash = rs.getInt("stripdash");
         }
         stmt.close();              // close the stmt

      }
      catch (Exception exc) {

         invalidRemote(new_skin, default_activity_id, "Unable to Connect to Club Database for options. Error: " + exc.getMessage(), req, out, con);
         return;
      }
      
    } else if (mobileApp && caller.equals("PDGMOBILE")) {    // Default all PDGMOBILE calls to primary member number interfaces.
        seamless = 1;
        primaryif = 1;
        mnumi = 1;
        stripzeroi = 1;
        stripAlpha = 1;
      
    } else if (caller.equals("CLUBSTERWEB")) {    // Default all CLUBSTER website (non-mobile) calls to primary interface
        seamless = 1;
        primaryif = 1;
        mnumi = 1;
        stripzeroi = 1;
        
    } else if (mobileApp && caller.equals("CLUBSTER")) {    // Default all CLUBSTER Mobile calls to primary interface
        seamless = 1;
        primaryif = 1;
        mnumi = 1;
        stripzeroi = 1;
        //stripAlpha = 1;   // not sure about this one!!
        
        lname = "";
        
        if (req.getParameter("last_name") != null && !club.equals("demov4")) {

           lname = req.getParameter("last_name");       // get user's last name as provided by Clubster
        }          
    }
    
    if (club.equals("weeburn")) {
        stripAlphaNum = 1;
    }
   
   // Fox Hill CC seamless login to fortcollins site - override settings
   if (club.equals("fortcollins") && (caller.equals("PDG4735") || caller.equals( "CSG7463" ) || caller.equals("AMO"))) {
       seamless = 1;
       primaryif = 1;
       mnumi = 1;
       
       if (caller.equals("AMO") && (user.endsWith("S1") || user.endsWith("s1"))) {
           user = user.substring(0, user.length() - 2);
       }
   }
   
   if (caller.equals("ForeTees1298")) {   // if this is a ForeTees Provided Link - CE Bypass
      
      //
      //  We provided the website link (originally added for CE websites because they charge too much), then
      //  force the settings so we can run this interface while the old CE link is still in place.
      //
      seamless = 1;
      primaryif = 1;
      mnumi = 1;
      mappingi = 1;
      stripzeroi = 1;
   }
   
   
   //
   //  Set internal 'strip' flag for specific clubs
   //
   if (club.equals( "portage" ) || club.equals("mayfieldsr") || club.equals("stoneoakcountryclub")) {

      stripEnd = true;            // strip trailing end character on username (or mNum)
   }

   if (club.equals("johnsisland")) {

      stripSpace = 1;
   }


   //
   //**********************************************************************
   //  If the I/F options have not been set, get them from web site parms
   //**********************************************************************
   //
   if (seamless == 0) {           // if seamless not set, then others won't be so get parms passed

      //
      //  Get the club's zipcode if passed (for weather link)
      //
      if (req.getParameter("zipcode") != null) {

         zipcode = req.getParameter("zipcode");
      }

      //
      //  Get the 'primary' parm if passed (used by web site to indicate the member is primary - we must prompt)
      //
      if (req.getParameter("primary") != null) {

         primary = req.getParameter("primary");
      }

      if (req.getParameter("mnum") != null) {

         mNumParm = req.getParameter("mnum");
      }

      //
      //  See if user wants to use Mapping - maps username value to our 'webid'
      //
      if (req.getParameter("mapping") != null) {

         mapping = req.getParameter("mapping");      // get mapping parm - used to map member ids
      }

      //
      //  See if user wants to Strip Leading Zeros from the username value
      //
      if (req.getParameter("stripzero") != null) {

         stripZero = req.getParameter("stripzero");
      }
        
   } else {

      //
      //  Set parms based on club options
      //
      if (primaryif == 1) {
        
         primary = "Yes";
           
         if (caller.equals( "CLUBESSENTIAL" )) {   // CE provides their unique id in user_name - we must get mnum

            primaryCE = true;                       // indicate CE primmary i/f
            mNumParm = "Yes";                       // make sure we use mNum (use webid to get it)
         }
      }
        
      if (mnumi == 1) {

         mNumParm = "Yes";
      }

      if (mappingi == 1) {

         mapping = "Yes";
      }

      if (stripzeroi == 1) {

         stripZero = "Yes";
      }

      if (rsynci == 1) {

         rsync = true;
      }
   }


   //
   //  Strip end char off user if required
   //
   if (stripEnd == true) {
     
      if (!user.endsWith( "0" ) && !user.endsWith( "1" ) && !user.endsWith( "2" ) && !user.endsWith( "3" ) &&
          !user.endsWith( "4" ) && !user.endsWith( "5" ) && !user.endsWith( "6" ) && !user.endsWith( "7" ) &&
          !user.endsWith( "8" ) && !user.endsWith( "9" )) {

         user = stripA2( user );        // remove trailing alpha char
      }
   }

    
   //
   //  Denver CC MyClub users - custom to process spouse requests as NOT primary mode
   //
   if (caller.equals( "MYCLUB" ) && (club.equals("denvercc") || club.equals("foresthills"))) {
     
      if (!user.endsWith( "0" ) && !user.endsWith( "1" ) && !user.endsWith( "2" ) && !user.endsWith( "3" ) &&
          !user.endsWith( "4" ) && !user.endsWith( "5" ) && !user.endsWith( "6" ) && !user.endsWith( "7" ) &&
          !user.endsWith( "8" ) && !user.endsWith( "9" )) {

         primary = "No";            // map to username
         
         if (club.equals("foresthills")) {
         
            mapping = "Yes";        // map to webid
            mNumParm = "No";        // username is not the mNum
         }
      }
   }
    
    
   //
   //  If CE - check for mobile user
   //
   if (caller.equals( "CLUBESSENTIAL" )) {    // CE 
  
      if (req.getParameter("mobile") != null) CEmobile = true;        // mobile user
   }
    

    
   //
   //  See if request is for event signup - users can select event from their calendar!!
   //
   //   NOTICE:  The name of the event MUST match our event name exactly!!!!!
   //
   if (req.getParameter("eventname") != null) {

      eventName = req.getParameter("eventname");      // event name must match our name exactly!!!!!
   }

   if (eventName == null) eventName = ""; 

   if (!eventName.equals("")) {         // if event specified, verify name and get course name for event
       
      try {

         if (req.getParameter("activity") != null && req.getParameter("activity").equals(String.valueOf(dining_activity_id))) {  // Dining event
                            
             event_activity_id = dining_activity_id;
             courseName = "";
             
         } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none") || eventName.equalsIgnoreCase("reservations") || eventName.equalsIgnoreCase("calendar") 
                 || eventName.equalsIgnoreCase("lessons") || eventName.equalsIgnoreCase("partners")) {
  
             if (req.getParameter("activity") != null && !req.getParameter("activity").equals("") && !req.getParameter("activity").equals("0") 
                     && !req.getParameter("activity").equals(String.valueOf(dining_activity_id))) {
                 try {
                     event_activity_id = Integer.parseInt(req.getParameter("activity"));
                 } catch (Exception exc) {
                     event_activity_id = 0;
                 }
             } else {
                 event_activity_id = 0;
             }
             
             courseName = "";
             signUp = 0;

         } else {

             stmt = con.prepareStatement (
                "SELECT activity_id, courseName, signUp " +
                "FROM events2b WHERE name = ?");

             stmt.clearParameters();
             stmt.setString(1, eventName);
             rs = stmt.executeQuery();

             if (rs.next()) {

                event_activity_id = rs.getInt("activity_id");   // activity id for this event
                courseName = rs.getString("courseName");        // get course name for Member_events2
                signUp = rs.getInt("signUp");                   // signUp indicator for members

             } else {           // not found - must be invalid name from MF

                eventName = "";           // remove it, direct member to announce page instead
             }

             stmt.close();

             if (signUp == 0) {           // if members are not allowed to register for this event

                eventName = "";           // remove it, direct member to announce page instead
             }
         }
      }
      catch (Exception exc) {

         eventName = "";           // remove it, direct member to announce page instead
      }  
   }
    
      
   //
   //  Special processing for MFirst
   //
   if (caller.equals( "MEMFIRST" ) || caller.equals("MFMOBILE")) {       // if 'MembersFirst'
     
      lname = req.getParameter("lname");    // get last name value that was passed
        
      username = getUser(user);             // go extract user (decrypt it)
  
      if (username.equals( "" )) {
        
         out.println(SystemUtils.HeadTitle("Connection Error - Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H2>Invalid Credentials Received</H2><BR>");
         out.println("<BR>Sorry, some required information is either missing or invalid.<BR>");
         out.println("<BR>Exception: Username Not Received");
         out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
         out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
     
         logMsg = "Remote Login Failed (MFirst) - Invalid Username: " +user;
         SystemUtils.sessionLog(logMsg, user, omit, club, omit, con);                   // log it
         return;
      }

      /*        // Removed this processing.  It's no longer necessary and only confuses members.
      //
      //  Get email address(es) if supplied
      //
      if (!club.equals("mesaverdecc") && !club.equals("charlottecc")) {     // if not Mesa Verde (do not update emails for Mesa Verde)

         if (req.getParameter("email1") != null) {

            email1 = req.getParameter("email1");
         }
         if (req.getParameter("email2") != null) {

            email2 = req.getParameter("email2");
         }
      }
       */
        

   } else {     // not MEMFIRST
      
      username = user;
        
      //
      //  Set Roster Sync indicator based on club name
      //
      if (club.equals( "fourbridges" )) {     // Flexscape

         rsync = true;                        
      }
      
      if (caller.equals( "CLUBESSENTIAL" )) {    // CE
  
         if (primaryCE == true) {       // if CE primary interface, get mNum by matching user to webid
           
            try {

               stmt = con.prepareStatement (
                  "SELECT memNum " +
                  "FROM member2b WHERE webid = ? AND inact = 0 AND billable = 1");

               stmt.clearParameters();         // clear the parms
               stmt.setString(1, user);        // put the username field in statement
               rs = stmt.executeQuery();       // execute the prepared stmt

               while (rs.next()) {

                  username = rs.getString(1);     // use mNum to get real username
               }

               stmt.close();

            }
            catch (Exception exc) {

              errMsg = "Unable to get member number for this user.";

              invalidRemote(new_skin, default_activity_id, errMsg, req, out, null);              // go process connection error......
              return;
            }  
         }
         
      }              // end of IF CE
   }
   

   //
   //  Special processing for Merion (CSG) - parse the username field to extract the mNum (primary i/f)
   //
   if (club.equals("merion")) {

      //
      //  Merion - web site is CSG - strip the "-nn" extension from the username to get the member number
      //
      StringTokenizer tok = new StringTokenizer( username, "-" );     // delimiters are dash

      if ( tok.countTokens() > 1 ) {                  // must be at least 2 (nnnn-ee)

         username = tok.nextToken();                  // get member number only
      }          
   }
   
 
   //
   //  *** IMPORTANT: DON'T USE CALLER'S LONGER THAN 24 CHARACTERS IN LENGTH! (length of club5.seamless_caller field)
   //
   //  PDG4735 = Generic Web Site ID
   //
   //  ForeTees1298 - for the CE Bypass links on CE websites !!!!!!!!!
   //
   //  MYCLUB - Mobile App provider - mobile devices only!!
   //  MFMOBILE - Mobile App provider - mobile devices only!! (MembersFirst)
   //
   //  CLUBSTERWEB - Clubster's website (not their mobile app)
   //
   //  South Bay Design = Hillcrest GC in St. Paul
   //  Legendary Marketing = Harkers Hollow GC
   //  Joe Keating (joetechonline.com) = Glen Oak CC
   //  City Star = Colorado Springs CC
   //  Jay Van Vark = CC of Rancho Bernardo
   //  Winding Oak = Wayzata CC
   //  Lightedge =  Davenport CC
   //  Club Systems Group (CSG7463) =  Hurstbourne CC
   //  LogiSoft (LOGISOFT7482) =  Locust Hill CC
   //  MeritSoft (BUZWEB4937) =  Providence CC (*** Removed - changed to CE 6/11/07 *****)
   //  Cherry Hills (CHCC0475) = Cherry Hills CC
   //  Nakoma (NAKOMA3273) = Fairwood
   //  Sedona Management Group (SEDONA3973) = Fairwood
   //  Flexscape (FLEXSCAPE4865) = Oswego Lake
   //  ZSmart (ZSMART3573) = Oswego Lake
   //  Jake Cook (JCOOK78439) = Wichita
   //  SLG Development (SLGDEV4673) = Pine Hills
   //  Gary Jonas (GJONAS74912) = Interlachen & Meadowbrook
   //  Gold Star Webs (GOLDSTAR) = Rochester CC
   //  Web Sites 2000 (WEBS2000) = Ironwood CC
   //  Hidden Valley = North Ridge
   //  FELIX = White Manor
   //  MediaCurrent = Piedmont Driving Club (Atlanta)
   //  Monson now = 'GCWH'
   //  Hooker = Southview CC
   //  Velotel = Hazeltine (Mark Josefson)
   //  Intraclub = Skokie, Bellerive (Paul Niebuhr 203-984-9887)
   //  TAHOEDONNERCA530 = Tahoe Donner (tahoedonner)
   //  GENID79623 = Generic id added Dec 2013 so we can block doGet calls
   //
   if (caller.equals( "AMO" ) || caller.equals( "MEMFIRST" ) || caller.equals( "GCWH" ) ||
       caller.equals( "NEMEX" ) || caller.equals( "HOOKER" ) || caller.equals( "PRIVATEGOLF" ) ||
       caller.equals( "CLUBESSENTIAL" ) || caller.equals( "JCOOK78439" ) || caller.equals( "VELOTEL" ) ||
       caller.equals( "INTRACLUB" ) || caller.equals( "MEDIACURRENT" ) || caller.equals( "FELIX" ) ||
       caller.equals( "HIDDENVALLEY" ) || caller.equals( "WEBS2000" ) || caller.equals( "GOLDSTAR" ) ||
       caller.equals( "GJONAS74912" ) || caller.equals( "SLGDEV4673" ) || caller.equals( "ZSMART3573" ) ||
       caller.equals( "FLEXSCAPE4865" ) || caller.equals( "GRAPEVINE2947" ) || caller.equals( "SEDONA3973" ) ||
       caller.equals( "NAKOMA3273" ) || caller.equals( "CHCC0475" ) || 
       caller.equals( "LOGISOFT7482" ) || caller.equals( "CSG7463" ) || caller.equals( "LIGHTEDGE" ) ||
       caller.equals( "WINDINGOAK" ) || caller.equals( "VANVARK2754" ) || caller.equals( "CITYSTAR3976" ) ||
       caller.equals( "KEATING385" ) || caller.equals( "LEGENDARY294" ) || caller.equals( "SOUTHBAY" ) ||
       caller.equals( "PDG4735" ) || caller.equals( "ForeTees1298" ) || caller.equals( "MYCLUB" ) || 
       caller.equals( "MFMOBILE" ) || caller.equals( "TAHOEDONNERCA530" ) || caller.equals( "GENID79623" ) || 
       caller.equals("CLUBSTERWEB") || mobileApp) {

       // || caller.equals( "BUZWEB4937" ) ||        (removed 6/11/07)
      
      
      //
      //  if from MYCLUB, then set mobile user since all users from MyClub are mobile
      //
      if ( (caller.equals("MYCLUB") && req.getParameter("activity") == null) || caller.equals( "MFMOBILE" ) || CEmobile || mobileApp) {
                
         mobile = 1;
      }

      
      //
      //   Save caller value in club5 so we can identify clubs that use this interface
      //
      if (!caller.equals( seamless_caller )) {          // if this caller is different than the caller saved in club5
         
         try {

            PreparedStatement pstmt = con.prepareStatement (
               "UPDATE club5 SET seamless_caller = ?");

            pstmt.clearParameters();           
            pstmt.setString(1, caller);             // save this caller

            pstmt.executeUpdate();  

            pstmt.close();

         }
         catch (Exception exc) {
         }
      }
      
      
      //
      //  Get club's POS Type for _slot processing
      //
      String posType = getPOS(con);

      //
      //  Get TLT indicator
      //
      int tlt = (getTLT(con)) ? 1 : 0;
         
      //
      //  Strip leading zeros in username (member #) if came from Web Sites 2000 (Ironwood CC) or CSG (Hurstbourne)
      //
      if (caller.equals( "WEBS2000" ) || caller.equals( "CSG7463" )) {     // if caller is Web Sites 2000 or CSG

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero(username);      // strip them
         }
      }

      //
      //  Strip leading zeros in username (member #) if came from St. Albans CC or Sunset Ridge
      //
      if (caller.equals( "INTRACLUB" ) && (club.equals( "stalbans" ) || club.equals( "sunsetridge" ))) {     

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
         }
      }

      // If Golfcrest CC and "gccc" is included in the username passed, strip it off
      if (club.equals("golfcrestcc") && username.startsWith("gccc")) {
          
          username = username.substring(4);
      }


      //
      //  Strip leading zeros in user requested it (NEW option)
      //
      if (stripZero.equalsIgnoreCase( "yes" )) {

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
         }
      }

      //  If option is set, strip dash and anything following it from username (e.g. 1234-001 -> 1234)
      if (stripDash == 1) {

          StringTokenizer dashTok = new StringTokenizer(username, "-");

          if (dashTok.countTokens() > 1) {
              username = dashTok.nextToken();
          }
      }

      //  If option is set, strip any non-numeric character form the end of username (e.g. 1234A -> 1234)
      if (stripAlpha == 1 && !username.equals("")) {

          if (!username.endsWith("0") && !username.endsWith("1") && !username.endsWith("2") &&
              !username.endsWith("3") && !username.endsWith("4") && !username.endsWith("5") &&
              !username.endsWith("6") && !username.endsWith("7") && !username.endsWith("8") &&
              !username.endsWith("9")) {

              try {
                  username = username.substring(0, username.length()-1);
              } catch (Exception exc) {
                  Utilities.logError("Login.remoteUser - " + club + " - Error stripping Alpha character - ERR: " + exc.toString());
              }
          }
      }
      
      if (stripAlphaNum == 1) {
          
          if (username.endsWith("C1") || username.endsWith("C2") || username.endsWith("C3") || username.endsWith("C4") 
           || username.endsWith("C5") || username.endsWith("C6") || username.endsWith("C7") || username.endsWith("C8") 
           || username.endsWith("C9") || username.endsWith("S1") || username.endsWith("S2") || username.endsWith("S3") 
           || username.endsWith("S4") || username.endsWith("S5") || username.endsWith("S6") || username.endsWith("S7") 
           || username.endsWith("S8") || username.endsWith("S9")) {
              
              try {
                  username = username.substring(0, username.length()-2);   
              } catch (Exception exc) {
                  Utilities.logError("Login.remoteUser - " + club + " - Error stripping AlphaNum character - ERR: " + exc.toString());
              }
          }
      }

      // Strip spaces from the middle of username values
      if (stripSpace == 1) {

          StringTokenizer spaceTok = new StringTokenizer(username, " ");

          if (spaceTok.countTokens() > 1) {
              username = spaceTok.nextToken() + spaceTok.nextToken();
          }
      }


      //
      //  Convert username field for Bishops Bay (Flexscape)
      //
      if (caller.equals( "FLEXSCAPE4865" ) && club.equals( "bishopsbay" )) {     // if Bishops Bay CC

         username = convertFlex(username);      // convert from xxxx-00n to xxxxn
      }

      //
      //  Add a 'G' to the username field (actually member number) for Greeley CC (CSG site)
      //
      if (caller.equals( "CSG7463" ) && club.equals( "fortcollins" )) {     // Greeley shares Fort Collins site

         username = "G" + username;    // Gnnn indicates Greeley member
      }

      //
      //  Add "FOX" to the username field (actually member number) for Fox Hill CC (CyberGolf Site)
      //
      if (club.equals("fortcollins") && caller.equals("PDG4735")) {

         username = "FOX" + username;
      }
      
      if (club.equals("fortcollins") && caller.equals("AMO")) {
          
         username = "FC" + username;
      }


      //
      //  Trim the username for Oswego Lake (ZSMART) - they cannot pass us the member number - they can
      //  only pass us their username, which is last name + first initial (i.e. pariseb).  We set this
      //  value in our password field and use it to locate the member number (for the primary interface).
      //
      //  Our password is defined as 15 chars max, so we must trim if more than 15.  This is a little risky
      //  because there might be more than one family with a last name that matches the first 15 chars.
      //
      if (caller.equals( "ZSMART3573" ) && club.equals( "oswegolake" )) {     // if Oswego Lake CC

         username = trimZsmart(username);      // 15 chars at max
      }

      String remote_ip = req.getHeader("x-forwarded-for");
      if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();
      
      //
      // use a prepared statement to find username (string) in the DB..
      //
      try {

         String stmtString = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                             "memNum, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, " +
                             "mobile_iphone, msub_type " +
                             "FROM member2b " +
                             "WHERE inact = 0 AND billable = 1 AND ";
           
         if (mNumParm.equalsIgnoreCase( "yes" )) {  // username = mNum (or webid for oswego)

            //
            //  If Oswego Lake (ZSmart), then the mNum is actually a member id that we save in our password.
            //
            if (caller.equals( "ZSMART3573" ) && club.equals( "oswegolake" )) {     // if Oswego Lake CC

               stmtString += "password = ?";

            } else {

               stmtString += "memNum = ?";
            }

         } else {

            if (mapping.equalsIgnoreCase( "yes" )) {    // map username to web id for match ?

               stmtString += "webid = ?";

            } else {
              
               stmtString += "username = ?";
            }
         }

         PreparedStatement pstmt = con.prepareStatement (stmtString);

         // Get user's pw if there is a matching user...

         pstmt.clearParameters();         // clear the parms
         pstmt.setString(1, username);    // put the username field in statement
         rs = pstmt.executeQuery();       // execute the prepared stmt

         if (rs.next()) {

            if (mapping.equalsIgnoreCase( "yes" )) {    // map username to web id for match ?

               username = rs.getString("username");                           // get our username
            }
              
            if (mNumParm.equalsIgnoreCase( "yes" )) {     // if mNum was supplied in username

               username = rs.getString("username");                           // get our username
            }
              
            String lastName = rs.getString("name_last");                           // get last name

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

            String mi = rs.getString("name_mi");                                // middle initial
            if (!mi.equals( omit )) {
               mem_name.append(" ");
               mem_name.append(mi);
            }
            mem_name.append(" " + rs.getString("name_last"));                     // last name

            String name = mem_name.toString();                          // convert to one string

            
            // Get the member's membership type

            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type
            subtype = rs.getString("msub_type");  // get member's sub_type if any

            // Get the member's email addresses

            oldemail1 = rs.getString("email");
            oldemail2 = rs.getString("email2");

            email_bounced = rs.getInt("email_bounced");       
            email2_bounced = rs.getInt("email2_bounced");

            iCal1 = rs.getInt("iCal1");
            iCal2 = rs.getInt("iCal2");
            
            if (oldemail1.toLowerCase().endsWith("aol.com") || oldemail2.toLowerCase().endsWith("aol.com")) {
                aolEmailUser = true;
            }

            //
            // if 'activity' is being passed and its not an Event Signup request, then use the activity being requested
            //
            if (req.getParameter("activity") != null && eventName.equals("")) {

                int temp_activity_id = 0;

                // Stanwich was built before the common system and passes 'tennis' instead of an activity_id
                if (club.equals("stanwichclub")) {
                    if (req.getParameter("activity").equals("tennis")) {
                        temp_activity_id = 1;
                    }
                } else {
                   try {
                       temp_activity_id = Integer.parseInt(req.getParameter("activity"));
                   }
                   catch (Exception exc) {
                      temp_activity_id = 0;
                   }  
                }

                if (primary.equalsIgnoreCase("yes")) {       // if primary interface
                    if (temp_activity_id == 0) {
                        default_activity_id = -999;
                    } else {
                        default_activity_id = temp_activity_id * -1;
                    }
                } else {
                    default_activity_id = temp_activity_id;
                }

            } else {
               
               if (!eventName.equals("")) {                  
                   default_activity_id = event_activity_id;      // if user going to an event signup, then use that activity id (from the event)
               } else {
                   default_activity_id = rs.getInt("default_activity_id");
               }
            }
            

            // Set iCal values to 0 for The Reserve Club so message doesn't display
            if (club.equals("thereserveclub")) {
                iCal1 = 0;
                iCal2 = 0;
            }


            // Get the number of visits and update it...

            int count = rs.getInt("count");         // Get count
//            boolean b = rs.wasNull();       // If null, change to zero
//            if (b) {
//               count = 0;
//            }
            count++;                        // bump counter..

            //  Get wc and last message displayed at login

            wc = rs.getString("wc");                            // w/c pref
            String message = rs.getString("message");       // message
            mNum = rs.getString("memNum");                      // member #
            mobile_count = rs.getInt("mobile_count");     // get # of mobile logins for this ueer
            mobile_iphone = rs.getInt("mobile_iphone");     // get # of iphone logins for this ueer

            
            pstmt.close();       //done with statement - close it
            
            
            //
            //  see if we should display a message to this member
            //
            if (message.equals( latest_message )) {      // if newest message was already displayed
              
               message = "";                              // no message to send
               
            } else {
               
               if (allowMobile == true && mobile_count == 0) {  // if mobile ok for club and user has not used it yet
                  
                  //message = "msg001";      // mobile allowed - show the mobile message messages
                  message = "";
                  
               } else {      // no mobile - no message
                  
                //  if (message.equals( "" )) {
                     
                //     message = "msg001";
                     
                //  } else {
                     
                     message = "";
                //  }
               }
            }
            
            
            //
            //  If Clubster user, verify that the last name provided matches the last name in our record for any of the family members.
            //  This is done to try to prevent a Clubster user from changing his/her member number in Clubster and attemptig to get
            //  into FT as a different member.  Clubster provides both a member number and last name, but the member does not know this.
            //
            if (mobileApp && caller.equals("CLUBSTER") && !lname.equals("")) {    // if Clubster mobile user and last name provided
               
               boolean matchFound = verifyLastName(club, caller, lname, lastName, mNum, con);     // verify the last name
               
               if (matchFound == false) {          // if error
                  
                  errMsg = "Clubster information provided does not match the ForeTees profile.";
                                    
                  invalidRemote(new_skin, default_activity_id, errMsg, req, out, con, mobile);
                  return;
               }               
            }
                                   

            //
            //  If Medinah CC - do not allow certain members to login
            //
            /*
            if (club.equals( "medinahcc" )) {

               if (mship.equals( "Social" ) || mship.startsWith( "Social Pro" ) || mtype.startsWith( "FM " ) || mtype.startsWith( "Fam Member" )) {

                  errMsg = "Membership Class Not Allowed.";

                  invalidRemote(new_skin, default_activity_id, errMsg, req, out, con);
                  return;
               }
            }
            *     // no longer a customer
            */

            
            
            //
            //  Trace good logins - display parms passed for verification purposes
            //
            logMsg = "Remote Login Successful: user_name from website=" +user+ ", Primary=" +primary+ ", mNum=" +mNumParm+ ", Mapping=" +mapping+ ", IP=" + remote_ip + " ";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it

            recordLogin(user, "", club, remote_ip, 1);
               
            // Save the connection in the session block for later use.......
            HttpSession session = Utilities.getNewSession(req);   // Create a session object

            ConnHolder holder = new ConnHolder(con);      // create a new holder from ConnHolder class

            session.setAttribute("connect", holder);      // save DB connection holder
            session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
            session.setAttribute("user", username);       // save username
            session.setAttribute("name", name);           // save members full name
            session.setAttribute("club", club);           // save club name
            session.setAttribute("caller", caller);       // save caller's name
            session.setAttribute("mship", mship);         // save member's mship type
            session.setAttribute("mtype", mtype);         // save member's mtype
            session.setAttribute("msubtype", subtype);    // save member's sub_type
            session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
            session.setAttribute("posType", posType);     // save club's POS Type
            session.setAttribute("zipcode", zipcode);     // save club's zipcode
            session.setAttribute("tlt", tlt);             // timeless tees indicator
            session.setAttribute("mobile", mobile);       // set mobile indicator (0 = NOT, 1 = Mobile)
            session.setAttribute("activity_id", default_activity_id);  // activity indicator
            session.setAttribute("organization_id", organization_id);  // organization_id (set if using ForeTeesDining system)
            session.setAttribute("new_skin", (new_skin) ? "1" : "0");  // new skin flag
            
            //
            // set inactivity timer for this session
            //  use 10 mins to prevent user from hanging a tee slot too long
            //
            if (club.equals("oakleycountryclub")) {
                session.setMaxInactiveInterval(30*60);    // set inactive timeout to 30 min for Oakley CC
            } else {
                session.setMaxInactiveInterval( MEMBER_TIMEOUT );
            }
            
            String base_url = Utilities.getBaseUrl(req, default_activity_id, club);
            /*
            String base_url = "../"+club+"_golf/";
            if(default_activity_id == dining_activity_id || default_activity_id == (dining_activity_id * -1)){
                base_url = "../"+club+"_dining/";
            } else if(default_activity_id != 0 && default_activity_id != -999) {
                if (default_activity_id > 0) {
                    base_url = "../"+club+"_flxrez"+default_activity_id+"/";
                } else {
                    base_url = "../"+club+"_flxrez"+(default_activity_id * -1)+"/";
                }
            }
             * 
             */

          
            //
            //  Check for PRIMARY I/F
            //
            //  If this is the primary member and primary=yes, then we must prompt the user to see which
            //  family member this is.  Member_msg will process the reply.
            //
            if (primary.equalsIgnoreCase( "yes" ) && !mNum.equals( "" )) {

               boolean primaryDone = promptPrimary(mobile, mNum, message, club, new_skin, default_activity_id, out, req, con);

               if (primaryDone == true) {   // if we prompted (if more than one member)

                  return;                   // reply handled by Member_msg
               }

               // If no family members were found, change default_activity_id back from -999 to 0 and re-apply the activity_id session attribute
               if (default_activity_id == -999) {
                   default_activity_id = 0;
                   session.setAttribute("activity_id", default_activity_id);  // activity indicator
               } else if (default_activity_id < 0) {
                   default_activity_id = default_activity_id * -1;
                   session.setAttribute("activity_id", default_activity_id);  // activity indicator
               }
            }

            
            
            //
            //  Check if email addresses should be updated (only MFirst [excluding mesaverdecc] passes this)
            //
            if (!email1.equals( "" )) {         // if email address provided by caller (MFirst)
              
               FeedBack feedback = (member.isEmailValid(email1));    // validate it

               if (!feedback.isPositive()) {    // if error
                 
                  email1 = "";                  // ignore it
                    
               } else {

                  if (email2.equals( "" ) || email2 == null) {   // if 2nd address NOT provided

                     email2 = oldemail2;           // then set it equal to current email2 so not over-written
                       
                  } else {

                     feedback = (member.isEmailValid(email2));    // validate it

                     if (!feedback.isPositive()) {    // if error

                        email2 = "";                  // ignore it
                     }
                  }
               }
            }
               
            //
            //  Update the member record if email address provided by caller, 
            //  and it's different then what is in the database 
            //  currently only MFirst is passing the email1&2 parameters with the login
            //  no changes to iCal pref - we'll assume that they want the same settings
            //  note:  the roster sync performs this functionality for all new setups
            //
            
            int tmp_updateType = 0;
            String tmp_sql = "UPDATE member2b SET count = ?";
            
            if (!email1.equals( "" ) && !email1.equalsIgnoreCase(oldemail1)) {
                tmp_updateType++;
                tmp_sql += ", email = ?, email_bounced = 0";
            }
            
            if (!email2.equals( "" ) && !email2.equalsIgnoreCase(oldemail2)) {
                tmp_updateType = tmp_updateType + 2;
                tmp_sql += ", email2 = ?, email2_bounced = 0";
            }
            
            tmp_sql += " WHERE username = ?";
            
            stmt = con.prepareStatement ( tmp_sql );
            stmt.clearParameters();
            stmt.setInt(1, count);            // put the new count in statement
           // stmt.setString(2, message);       // put in the message displayed (now done in Member_msg!!)
            
            switch (tmp_updateType) {
                
                case 0:
                    
                    stmt.setString(2, username);
                    break;
                    
                case 1:        // clear the parms
                    
                    stmt.setString(2, email1);        // set email address
                    stmt.setString(3, username); 
                    break;
                    
                case 2:        // clear the parms
                    
                    stmt.setString(2, email2);        // set email address
                    stmt.setString(3, username); 
                    break;
                    
                case 3:
                    
                    stmt.setString(2, email1);        // set email address
                    stmt.setString(3, email2);        // set email address 2
                    stmt.setString(4, username); 
                    break;
            }
            stmt.executeUpdate();
            stmt.close();
               
                       
            if ((!caller.equals( "MEMFIRST" ) && !caller.equals("MFMOBILE")) || club.equals("mesaverdecc") || club.equals("charlottecc")) {     // if not MFirst

               //
               //   Validate the email addresses
               //
               if (!oldemail1.equals( "" )) {                   // if specified

                  FeedBack feedback = (member.isEmailValid(oldemail1));

                  if (!feedback.isPositive()) {              // if error

                     emailErr = feedback.get(0);             // get error message
                  }
               }
               if (!oldemail2.equals( "" )) {                   // if specified

                  FeedBack feedback = (member.isEmailValid(oldemail2));

                  if (!feedback.isPositive()) {              // if error

                     email2Err = feedback.get(0);             // get error message
                  }
               }

            }

            //
            //  Count the number of users logged in
            //
            countLogin("mem", con);

            // new stats logging routine
            recordLoginStat(2);
               
            if (mobile > 0) {

               countMobile(mobile_count, mobile_iphone, username, req, con);      // bump mobile counter and track mobile device 
            }
         
            //
            //  Check if this club was upgraded from V4 to V5.  If so, offer a list of changes.
            //
//            boolean upgrade = checkUpgrade(con);

            

            //
            //   Check for iPad (or like) device and prompt for standard or mobile access.
            //
            boolean enableAdvAssist = Utilities.enableAdvAssist(req);     // check for iPad (or like) device - false indicates iPad

            //if (!new_skin && enableAdvAssist == false && allowMobile == true && default_activity_id == 0 && mobile == 0) {     // if Golf, iPad and mobile ok for this site, and user not already detected as mobile
            if (enableAdvAssist == false && allowMobile == true && default_activity_id == 0 && mobile == 0) {     // if Golf, iPad and mobile ok for this site, and user not already detected as mobile

               promptIpad(club, name, new_skin, default_activity_id, out, req);                 // prompt user for mobile or standard interface

               countMobile(mobile_count, mobile_iphone, username, req, con);      // bump mobile counter and track mobile device (this was not done above)
               return;
            }

                             
            //
            //  Check for IE 6 browser
            //
            if (new_skin) {

                boolean oldBrowser = checkOldBrowser(club, name, out, req);

                if (oldBrowser == true) {

                    session.setAttribute("new_skin", "0");       // revert back to old skin in case user wants to continue                    
                    return;
                }   
            }
                  
                  
            //
            //   Determine if auto-refresh to be used
            //
            boolean autoRefresh = true;
            boolean skipMsgs = false;        // skip welcome messages and go directly to Member_announce

           // if (oldemail1.equals( "" ) && (!caller.equals( "MEMFIRST" ) || club.equals("mesaverdecc"))) {  // if email not present and NOT MF
            if (oldemail1.equals( "" ) && (rsync == false || club.equals("mesaverdecc") || club.equals("charlottecc") || club.equals("hillwoodcc")) && (!club.equals("rehobothbeachcc") || count <= 2)){  // if email not present and NOT MF

               autoRefresh = false;                                        // do not refresh
            }

            if ((!emailErr.equals( "" ) || !email2Err.equals( "" ) || iCal1 == -1 || iCal2 == -1 || email_bounced == 1 || email2_bounced == 1) && eventName.equals("")) {

               autoRefresh = false;
            }

                                                
            if (club.equals("minikahda")) {     // allow for routing message (Dining or Golf)
                          
               autoRefresh = false;
            }

                                                
            int delay = 1;    // use new default delay for the new skin so users can see the # of times thay have loggedin

            if (club.equals("woodlandscountryclub")) {

               delay = 3;      // 3 seconds for them
               
            } else if (club.equals("mpccpb")) {    // if Monterey Peninsula
                
               delay = 0;      // no delay for them
               
            } else if (club.equals("tontoverde") && !eventName.equals("")) {
                
               delay = 0;
                
            }


            //
            //  Output the response and route to system
            //
            if (mobile == 0) {
               
               if (new_skin) {

                   String msg1 = "Please note that this session will terminate if inactive for more than " + (MEMBER_TIMEOUT / 60) + " minutes.";

                   if (club.equals("oakleycountryclub")) {
                       msg1 = "Please note that this session will terminate if inactive for more than 30 minutes.";      // Print separate message for Oakley CC due to custom inactive period
                   }

                   int refresh_val = 0;
                   String refresh_url = "";

                   if (autoRefresh == true) {

                     if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)

                         refresh_val = 0;
                         skipMsgs = true;      // skip welcome msg and go directly to event signup
                         
                         if (event_activity_id == dining_activity_id) {
                             
                             int event_id = 0;
                             
                             // Check to make sure a valid event_id was passed as the eventName parameter.  If not, route them to the event listing instead.
                             try {
                                 event_id = Integer.parseInt(eventName);
                             } catch (Exception exc) {
                                 event_id = 0;
                             }
                             /*
                             if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none") || event_id == 0) {
                                 refresh_url = base_url + "Dining_home?view_events";
                             } else {
                                 refresh_url = base_url + "Dining_home?nowrap&event_popup&event_id=" + event_id;
                             }
                              */
                             
                             if (eventName.equalsIgnoreCase("reservations")) {
                                 refresh_url = base_url + "Dining_slot?action=new";
                             } else if (eventName.equalsIgnoreCase("calendar")) {
                                 refresh_url = base_url + "Member_teelist";  
                             } else if (eventName.equalsIgnoreCase("partners")) {
                                 refresh_url = base_url + "Member_partner";  
                             } else {
                                 // eventName should contain a numerical event id
                                 refresh_url = base_url + "Dining_home?view_events&event_id=" + eventName;
                             }
                             
                         } else {
                             
                             if (eventName.equalsIgnoreCase("reservations")) {
                                 if (event_activity_id != 0 && event_activity_id != dining_activity_id) {
                                     refresh_url = base_url + "Member_gensheets";
                                 } else {
                                     refresh_url = base_url + "Member_select";
                                 }
                             } else if (eventName.equalsIgnoreCase("lessons")) {
                                 refresh_url = base_url + "Member_lesson";  
                             } else if (eventName.equalsIgnoreCase("calendar")) {
                                 refresh_url = base_url + "Member_teelist"; 
                             } else if (eventName.equalsIgnoreCase("partners")) {
                                 refresh_url = base_url + "Member_partner";  
                             } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none")) {
                                 refresh_url = base_url + "Member_events";
                             } else {
                                 refresh_url = base_url + "Member_events?name=" +eventName+ "&course=" +courseName; // was Member_events2
                             }
                         }
                        
                     } else {

                        if (message.equals( "" ) && (!aolEmailUser || !displayAolNotice)) {      // if no message to display or not an aol user an
                            
                            if (autoRefresh == true && delay < 2) {
                                
                                delay = 0;
                                skipMsgs = true;     // skip welcome msgs and go directly to Member_announce
                            }

                            refresh_val = delay;
                            refresh_url = base_url + "Member_announce";

                         } else {

                            refresh_val = delay;
                            refresh_url = base_url + "Member_msg";
                         }
                     }
                   }                      

                   // new skin (no meta refresh - use js instead)
                   Common_skin.outputHeader(club, default_activity_id, "Member Login Page", true, out, req, refresh_val, refresh_url);


                   try {

                       clubName = Utilities.getClubName(con);        // get the full name of this club

                   } catch (Exception exc) {}

                   Common_skin.outputBody(club, default_activity_id, out, req);

                   out.println("<div id=\"wrapper_login\">");
                   out.println("<div id=\"title\">" + clubName + "</div>");

                   //Common_skin.outputPageStart(club, default_activity_id, out, req);
                   out.println("<div id=\"main_login\">");
                   
                   if (skipMsgs == false) {

                        // display welcome message
                        out.println("<div id=\"login_welcome\">");
                        out.println("<p>" + msg1 + "</p><br>");
                        if (count > 1) {
                            out.println("<p>You've now logged in " + count + " times.</p>");
                        } else {
                            out.println("<p>This is your first login.</p>");
                        }
                        out.println("</div>");

                        out.println("<h1>Login Accepted</h1>");

                        out.println("<h2 style=\"padding:10px\">Welcome " + ((count > 1) ? "back " : "") + name + ".</h2>");
                   }

                   if (autoRefresh == false) out.println("<div class=\"main_instructions\">");    // create a colored box for any messages that might follow
                      
               } else {

                   //
                   //   Output welcome page (old skin)
                   //
                  out.println("<HTML><HEAD><Title>Member Login Page</Title>");

                  if (autoRefresh == true) {

                     if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)

                        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=" + base_url + "Member_jump?name=" +eventName+ "&course=" +courseName+ "\">");

                     } else {

                        if (message.equals( "" )) {      // if no message to display
                           out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url=/" + rev + "/member_welcome.htm\">");
                        } else {
                           out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url=" + base_url + "Member_msg\">");
                        }
                     }
                  }
                  out.println("</HEAD>");
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H2>Member Access Accepted</H2><BR>");
                  out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

                  out.println("<font size=\"3\">");
                  out.println("<BR>Welcome <b>" + name );

                  out.println("</b><BR><BR>");
                  if (club.equals("oakleycountryclub")) {
                      out.println("Please note that this session will terminate if inactive for more than 30 minutes.<BR><BR>");      // Print separate message for Oakley CC due to custom inactive period
                  } else {
                      out.println("Please note that this session will terminate if inactive for more than " + (MEMBER_TIMEOUT / 60) + " minutes.<BR><BR>");
                  }
                  out.println("<br><br>");
                  
               }  // end of Old Skin

               if (email_bounced == 1 || email2_bounced == 1) {

                   out.println("<h3><b>WARNING: Email bouncing!!</b></h3>");
                   out.println("We recently tried to send you an email at ");

                   if (email_bounced == 1) {

                       out.print(oldemail1);

                   } else {

                       out.print(oldemail2);
                   }
                   out.print(" and it bounced back to us.<br>" +
                           "We've had to temporarily disable sending you any emails until you resolve this problem.");
                   if (rsync) {
                       out.println("<BR><BR>To correct this, please verify that the following email addresses are correct.<br>" +
                               "If changes are needed, please contact the golf shop staff at your club to request the change<br>" +
                               " be made in their club records.");
                   } else {
                       out.println("<BR><BR>To correct this, update your email below, or select the 'Settings' tab from the<br>" +
                               "navigation bar on the top of most pages and follow the insructions in the email<br>" +
                               "section next to the word 'Important'.");
                   }
                   out.println("<br><br>If the current email is correct, simply click 'Continue' below and ForeTees will<br>" +
                           "attempt to continue using the same email.  If you would like to remove the current email<br>" +
                           "address all together, " + (rsync ? "please notify the golf shop staff at your club that it should be removed." : "erase it from the field below and click 'Continue'."));

                   out.println("<br><br>");

                   if (rsync) {
                       if (oldemail1.equals( "" )) {
                           out.println("Please contact the golf shop staff at your club to add email addresses.");
                       } else {
                           out.println("Please verify the email address(es) below.");
                       }
                   } else {
                       if (oldemail1.equals( "" )) {
                           out.println("Please add at least one valid email address below.");
                       } else {
                           out.println("Please verify and/or change the email address(es) below.");
                       }
                   }
                   out.print("&nbsp;&nbsp;");
                   out.print("Thank you!");
                   out.print("<br><br>");

                   out.println("<form method=\"post\" action=\"" + base_url + "Login\">");
                   out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");
                   out.println("<input type=\"hidden\" name=\"bounced\" value=\"" + (email_bounced == 1 ? "email" : "email2") + "\">");

                   out.println("<b>Email Address " + (email_bounced == 1 ? "1" : "2") + ":</b>&nbsp;&nbsp;");
                   if (rsync) {
                       out.println("<i>" + (email_bounced == 1 ? oldemail1 : oldemail2) + "</i>");
                       out.println("<input type=\"hidden\" name=\"email\" value=\"" + (email_bounced == 1 ? oldemail1 : oldemail2) + "\">");
                   } else {
                       out.println("<input type=\"text\" name=\"email\" value=\"" + (email_bounced == 1 ? oldemail1 : oldemail2) + "\" size=\"40\" maxlength=\"50\">");
                   }
                   out.println("<br><br>");

                   //
                   // close the page
                   //
                   if (new_skin) {

                      if (autoRefresh == false) out.println("</div>");    // close colored box for any messages                    
                      //out.println("<div style=\"height:20px\"></div>");
                      out.println("<input type=\"submit\" value=\"Continue\" class=\"login_button_lg\">");
                      out.println("</form>");
                      out.println("<div class=\"clearfloat\"></div>");
                      out.println("</div>");    // end wrapper_login 

                      Common_skin.outputPageEnd(club, default_activity_id, out, req);
                              
                   } else {

                      out.println("</font></td></tr></table>");
                      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                      out.println("</form></font>");
                      out.println("</CENTER></BODY></HTML>");
                   }

               } else if (club.equals("minikahda")) {    // Minikahda - prompt to route to Golf or Dining
                   
                    out.println("Would you like to go to Golf or Dining?<br><br>");

                    out.println("<form method=\"get\" action=\""+base_url+"Member_announce\">");
                    out.println("<input type=\"submit\" value=\"Continue to Golf\" class=\"login_button_lg\">");
                    out.println("</form><br>");

                    out.println("<form method=\"get\" action=\""+base_url+"Dining_home\">");
                    out.println("<input type=\"submit\" value=\"Continue to Dining\" class=\"login_button_lg\">");
                    out.println("</form><br>");
                               
               } else {     // emails not bounced

                  if ((rsync == false || club.equals("mesaverdecc") || club.equals("charlottecc") || club.equals("hillwoodcc")) && (!club.equals("rehobothbeachcc") || count <= 2)) {       // if not a roster sync club

                     if (!emailErr.equals( "" ) || !email2Err.equals( "" )) {   // problem with email address?

                         if (!emailErr.equals( "" )) {   // problem with email1 address?

                             out.println("<b>Warning:</b>  Your email address (" +oldemail1+ ") is invalid.");
                             out.println("<BR>" +emailErr);

                         } else {

                             out.println("<b>Warning:</b>  Your email address (" +oldemail2+ ") is invalid.");
                             out.println("<BR>" +email2Err);
                         }

                         out.println("<BR><BR>To correct this, please change it below.");

                     } else if (oldemail1.equals( "" )) {

                         out.println("<b>Notice:</b> In order to receive email notifications and to stay informed,");
                         out.print("<br>you must maintain a current, working email address.");

                     }

                     out.println("<br><br>");

                     if (autoRefresh == false) {

                        if (oldemail1.equals( "" )) {

                            out.println("Please add at least one valid email address below.");

                        } else if (iCal1 == -1 || iCal2 == -1) {

                            out.println(iCalNotice);

                        } else {

                            out.println("Please verify and/or change the email address(es) below.");

                        }

                        out.print("&nbsp;&nbsp;");
                        out.print("Thank you!");
                        out.print("<br><br>");

                        out.println("<form method=\"post\" action=\"" + base_url + "Login\">");
                        out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");

                        out.println("<b>Email Address 1:</b>&nbsp;&nbsp;");
                        out.println("<input type=\"text\" name=\"email\" value=\"" +oldemail1+ "\" size=\"40\" maxlength=\"50\">");

                        out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">iCal attachments</a> at this email address? ");
                        out.println("<select size=\"1\" name=\"iCal1\">");
                         out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
                         out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
                        out.println("</select>");

                        out.println("<br><br>");

                        out.println("<b>Email Address 2:</b>&nbsp;&nbsp;");
                        out.println("<input type=\"text\" name=\"email2\" value=\"" +oldemail2+ "\" size=\"40\" maxlength=\"50\">");

                        out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">iCal attachments</a> at this email address? ");
                        out.println("<select size=\"1\" name=\"iCal2\">");
                         out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
                         out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
                        out.println("</select>");

                        out.println("<br><br>");

                     } else {     // autorefresh path - no email problems      

                        if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)
                           if (new_skin) {
                               if (default_activity_id == dining_activity_id) {
                                   if (eventName.equalsIgnoreCase("reservations")) {
                                       out.println("<form method=\"post\" action=\"" + base_url + "Dining_slot?action=new\">");
                                   } else if (eventName.equalsIgnoreCase("calendar")) {
                                       out.println("<form method=\"post\" action=\"" + base_url + "Member_teelist\">");
                                   } else if (eventName.equalsIgnoreCase("partners")) {
                                       out.println("<form method=\"post\" action=\"" + base_url + "Member_partner\">");
                                   } else {
                                       out.println("<form method=\"post\" action=\"" + base_url + "Dining_home\">");
                                       out.println("<input type=\"hidden\" name=\"view_events\" value=\"\">");
                                       out.println("<input type=\"hidden\" name=\"event_id\" value=\"" +eventName+ "\">");
                                   }
                               } else {  
                                   if (eventName.equalsIgnoreCase("reservations")) {
                                       if (default_activity_id > 0 && default_activity_id != dining_activity_id) {
                                           out.println("<form method=\"get\" action=\"" + base_url + "Member_gensheets\">"); 
                                       } else {
                                           out.println("<form method=\"get\" action=\"" + base_url + "Member_select\">"); 
                                       }
                                   } else if (eventName.equalsIgnoreCase("lessons")) {
                                       out.println("<form method=\"get\" action=\"" + base_url + "Member_lesson\">"); 
                                   } else if (eventName.equalsIgnoreCase("calendar")) {
                                       out.println("<form method=\"post\" action=\"" + base_url + "Member_teelist\">");
                                   } else if (eventName.equalsIgnoreCase("partners")) {
                                       out.println("<form method=\"post\" action=\"" + base_url + "Member_partner\">");
                                   } else {
                                       out.println("<form method=\"post\" action=\"" + base_url + "Member_events\">"); // was Member_events2
                                   }
                               }
                           } else {
                              out.println("<form method=\"post\" action=\"" + base_url + "Member_jump\">");
                           }
                           out.println("<input type=\"hidden\" name=\"name\" value=\"" +eventName+ "\">");
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName+ "\">");
                           
                        } else {   // no event name specified
                            
                           if (message.equals( "" )) {      // if no message to display
                               
                                if (new_skin) {
                                    out.println("<form method=\"get\" action=\"" + base_url + "Member_announce\">");
                                } else {
                                    out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
                                }
                              
                           } else {
                               
                              out.println("<form method=\"get\" action=\"" + base_url + "Member_msg\">");
                           }
                        }
                     }

                  } else {    // Roster Sync club

                     if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)                           
                        if (new_skin) {
                           if (default_activity_id == dining_activity_id) {
                               if (eventName.equalsIgnoreCase("reservations")) {
                                   out.println("<form method=\"post\" action=\"" + base_url + "Dining_slot?action=new\">");
                               } else if (eventName.equalsIgnoreCase("calendar")) {
                                   out.println("<form method=\"post\" action=\"" + base_url + "Member_teelist\">");
                               } else if (eventName.equalsIgnoreCase("partners")) {
                                   out.println("<form method=\"post\" action=\"" + base_url + "Member_partner\">");
                               } else {
                                   out.println("<form method=\"get\" action=\"" + base_url + "Dining_home\">");
                                   out.println("<input type=\"hidden\" name=\"view_events\" value=\"\">");
                                   out.println("<input type=\"hidden\" name=\"event_id\" value=\"" +eventName+ "\">");
                               }
                           } else {            
                               if (eventName.equalsIgnoreCase("reservations")) {
                                       if (default_activity_id > 0 && default_activity_id != dining_activity_id) {
                                           out.println("<form method=\"get\" action=\"" + base_url + "Member_gensheets\">"); 
                                       } else {
                                           out.println("<form method=\"get\" action=\"" + base_url + "Member_select\">"); 
                                       }
                               } else if (eventName.equalsIgnoreCase("lessons")) {
                                   out.println("<form method=\"post\" action=\"" + base_url + "Member_lesson\">");
                               } else if (eventName.equalsIgnoreCase("calendar")) {
                                   out.println("<form method=\"post\" action=\"" + base_url + "Member_teelist\">");
                               } else if (eventName.equalsIgnoreCase("partners")) {
                                   out.println("<form method=\"post\" action=\"" + base_url + "Member_partner\">");
                               } else if (eventName.equalsIgnoreCase("clubessential") || eventName.equalsIgnoreCase("none")) {
                                   out.println("<form method=\"post\" action=\"" + base_url + "Member_events\">");
                               } else {               
                                   out.println("<form method=\"post\" action=\"" + base_url + "Member_events2\">");
                               }
                           }
                        } else {
                           out.println("<form method=\"post\" action=\"" + base_url + "Member_jump\">");
                        }
                        out.println("<input type=\"hidden\" name=\"name\" value=\"" +eventName+ "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName+ "\">");

                     } else if (iCal1 == -1 || iCal2 == -1) {        
                         
                        out.println("<form method=\"post\" action=\"" + base_url + "Login\">");
                        out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");

                         out.println(iCalNotice);

                        out.println("<b>Email Address 1:</b>&nbsp;&nbsp;"+oldemail1);
                        out.println("<input type=\"hidden\" name=\"email\" value=\"" +oldemail1+ "\" size=\"40\" maxlength=\"50\">");

                        out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">iCal attachments</a> at this email address? ");
                        out.println("<select size=\"1\" name=\"iCal1\">");
                         out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
                         out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
                        out.println("</select>");

                        out.println("<br><br>");

                        out.println("<b>Email Address 2:</b>&nbsp;&nbsp;"+oldemail2);
                        out.println("<input type=\"hidden\" name=\"email2\" value=\"" +oldemail2+ "\" size=\"40\" maxlength=\"50\">");

                        out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">iCal attachments</a> at this email address? ");
                        out.println("<select size=\"1\" name=\"iCal2\">");
                         out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
                         out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
                        out.println("</select>");

                     } else {

                       if (message.equals( "" )) {      // if no message to display
                          if (new_skin) {
                             out.println("<form method=\"get\" action=\"" + base_url + "Member_announce\">");
                          } else {
                             out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
                          }
                        } else {
                           out.println("<form method=\"get\" action=\"" + base_url + "Member_msg\">");
                        }
                     }

                  }     // end of IF rsync

                  //
                  // add Continue button and close the page
                  //
                  if (new_skin) {

                      if (autoRefresh == false) out.println("</div>");    // close colored box for any messages                    
           
                      if (!club.equals("minikahda")) {    // NOT Minikahda
                               
                          out.println("<input type=\"submit\" value=\"Continue\" class=\"login_button_lg\">");
                          out.println("</form>");
                      }

                      out.println("<div class=\"clearfloat\"></div>");

                      out.println("</div>");    // end wrapper_login 

                      Common_skin.outputPageEnd(club, default_activity_id, out, req);
                              
                  } else {

                     out.println("</font></td></tr></table>");
                     out.println("<br>");
                     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                     out.println("</form></font></CENTER>");
                     out.println("</BODY></HTML>");
                  }
                  
               }               // end of IF email bounced
               out.close();
               
            } else {
                
                //
                //  Mobile Device
                //
                promptMobileUser(club, name, session, out, con);   // prompt mobile user to see how they want to proceed
                    
            }


         } else {                               // username not found

            pstmt.close();                      // close the statement
            
            //
            //  Trace all failed login attempts
            //
            logMsg = "Remote Login Failed - Invalid User (msg#1) IP=" + remote_ip + " ";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it twice to get all info
            
            //
            //  Trace additional information and output error reply
            //
            if (caller.equals("CLUBSTER")) {
               
                logMsg = "Member Number (" +username+ ") Not Found. Please contact the Clubster Admin at your club.";
               
            } else {
               
                logMsg = "Invalid Username Received. User Id " +username+ " does not exist in the ForeTees roster.";
            }
            
            invalidRemote(new_skin, default_activity_id, logMsg, req, out, con, mobile);

            recordLogin(user, "", club, remote_ip, 0);

            if (con != null) {
               try {
                  con.close();       // Close the db connection........
               }
               catch (SQLException ignored) {
               }
            }

         }        // end of if username found
      }
      catch (SQLException exc) {

         errMsg = "DB Error. Exception: " +exc.getMessage();

         invalidRemote(new_skin, default_activity_id, errMsg, req, out, con);
         recordLogin(user, "", club, remote_ip, 0);

         try { con.close(); }
         catch (Exception e) {}

         return;
      }

   } else {    // caller is not valid

      errMsg = "Invalid Parameter Received - Web Site Id Not Allowed.";

      invalidRemote(new_skin, default_activity_id, errMsg, req, out, con);
   }
   
 }      // end of remoteUser

 
 

 // *********************************************************
 // Process user from FlexWeb - ForeTees Website (direct connect)
 // *********************************************************

 private void flexWebUser(HttpServletRequest req,
                 PrintWriter out, String user, String club) {

   Connection con = null;                  // init DB objects
   ResultSet rs = null;
   PreparedStatement stmt = null;

   Member member = new Member();

   String lname = "";
   String username = user;
   String mship = "";
   String mtype = "";
   String wc = "";
   String zipcode = "";
   String mNum = "";
   String logMsg = "";
   String mapping = "No";        
   String stripZero = "No";
   String errMsg = "";
   String eventName = "";
   String courseName = "";
   String caller = ProcessConstants.FT_PREMIER_CALLER;
   String seamless_caller = "";               // caller value in club5
   String clubName = "";
   String subtype = "";

   String landing_page = (req.getParameter("landing") != null) ? req.getParameter("landing") : "";

   int stripzeroi = 0;             // values from club5 table for this club
   int signUp = 0;
   int stripAlpha = 0; 
   int stripAlphaNum = 0;               // Used to signify that alpha-numeric strings ending a member number should be stripped (C1, C2, C3, etc).
   int stripDash = 0;
   int stripSpace = 0;

   int default_activity_id = 0;         // inticator for default activity (0=golf)
   int event_activity_id = 0;           // activity id for event, if event name is passed (0=golf)
   int organization_id = 0;
   
   int mobile = 0;                     // mobile user indicator
   int mobile_count = 0;
   int mobile_iphone = 0;
     
   boolean rsync = true;               // we always use roster sync for Premier
   boolean stripEnd = false;
   boolean fatalError = false;
   boolean allowMobile = false;
   boolean mobileApp = false;

   boolean new_skin = true;       // always for this 


   //
   //  See if request is for event signup - users can select event from their calendar!!
   //
   //   NOTICE:  The name of the event MUST match our event name exactly!!!!!
   //
   if (req.getParameter("eventname") != null) {

      eventName = req.getParameter("eventname");      // event name must match our name exactly!!!!!
   }

   if (eventName == null) eventName = ""; 
   

    //
    //  Make sure the club requested is currently running this version of ForeTees.
    //  The user may need to refresh the login page so they pull up the new page.
    //
    try {

        con = dbConn.Connect(rev);       // get a connection for this version level

        //
        //  Make sure club exists in our system
        //
        stmt = con.prepareStatement (
                "SELECT fullname FROM clubs WHERE clubname = ?");

        stmt.clearParameters();        // clear the parms
        stmt.setString(1, club);
        rs = stmt.executeQuery();

        if (!rs.next()) {          // if club not found in this version

           errMsg = "Error in Login.remoteUser - club name invalid, club=" +club+ " was received from web site.";        
           fatalError = true;
        }

    }
    catch (Exception exc) {
        // Error connecting to db....
        errMsg = "Error in Login.remoteUser - Unable to Connect to Database.  Could be an invalid clubname (club=" +club+ "). Error: " + exc.toString();    
        fatalError = true;
         
    } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { stmt.close(); }
          catch (Exception ignore) {}

          try { con.close(); }
          catch (Exception ignore) {}
    }
 

   if (fatalError == false) {

      //
      // Load the JDBC Driver and connect to DB for this club
      //
      try {
         con = dbConn.Connect(club);          // get a connection
      }
      catch (Exception exc) {

         errMsg = "Login.remoteUser - Unable to Connect to club: " +club+ ", User = " +user+ ". Error: " + exc.toString();
         fatalError = true;
      }
      
      //
      //   Check if club is under construction
      //
      boolean construction = checkConstruction(club, caller, default_activity_id, out, con, req);

      if (construction) return;       // exit if site under construction
   }


   //
   //  If any of above failed, return error message to user and log error in v5 error log table
   //
   if (fatalError == true) {

      out.println(SystemUtils.HeadTitle("Invalid Login"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>System Error</H2><BR>");
      out.println("<BR>Sorry, we cannot proceed due to an internal problem.");
      out.println("<BR>Exception: " +errMsg+ "<BR>");
      out.println("<BR>Please contact your club's website administrator (and provide this information) or try again later.  Thank you.<BR>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      
      SystemUtils.logError(errMsg);                           // log it

      if (con != null) {

         try {
            con.close();                // close the connection
         }
         catch (Exception exp) {
         }
      }      
      return;
   }

   
    organization_id = Utilities.getOrganizationId(con);

    
    if (req.getParameter("mobile") != null) {        // mobile ???
        
        mobileApp = true;
        mobile = 1;        
    }

    //
    //  Check if Mobile is allowed (for messages)
    //
    allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages?
 
          
    
    if (!mobileApp) {     // skip the settings if coming from a Mobile App since the club can have a web interface
       
      //
      //****************************************************
      //  Get the I/F options from club5 (set via support) 
      //****************************************************
      //
      try {

         stmt = con.prepareStatement (
                  "SELECT rsync, seamless, zipcode, primaryif, mnum, mapping, stripzero, seamless_caller, stripalpha, stripdash " +
                  "FROM club5");

         stmt.clearParameters();        // clear the parms
         rs = stmt.executeQuery();

         if (rs.next()) {          // get club options 

            zipcode = rs.getString("zipcode");
            stripzeroi = rs.getInt("stripzero");
            seamless_caller = rs.getString("seamless_caller");       // Caller value saved for this club
            stripAlpha = rs.getInt("stripalpha");
            stripDash = rs.getInt("stripdash");
         }
         stmt.close();              // close the stmt

      }
      catch (Exception exc) {

         invalidRemote(new_skin, default_activity_id, "Unable to Connect to Club Database for options. Error: " + exc.getMessage(), req, out, con);
         return;
      }
    }
    
    
    //
    //  Set parms based on club options
    //
    if (stripzeroi == 1) {

        stripZero = "Yes";
    }

        
    //
    //   Save caller value in club5 so we can identify clubs that use this interface
    //
    if (!caller.equals( seamless_caller )) {          // if this caller is different than the caller saved in club5

        try {

        PreparedStatement pstmt = con.prepareStatement (
            "UPDATE club5 SET seamless_caller = ?");

        pstmt.clearParameters();           
        pstmt.setString(1, caller);             // save this caller

        pstmt.executeUpdate();  

        pstmt.close();

        }
        catch (Exception exc) {
        }
    }


    //
    //  Get club's POS Type for _slot processing
    //
    String posType = getPOS(con);

    //
    //  Get TLT indicator
    //
    int tlt = (getTLT(con)) ? 1 : 0;
         
    //
    //  Strip leading zeros in user requested it (NEW option)
    //
    if (stripZero.equalsIgnoreCase( "yes" )) {

        if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
        }
    }

    String remote_ip = req.getHeader("x-forwarded-for");
    if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();


    //
    // use a prepared statement to find username (string) in the DB..
    //
    try {

        //
        // NOTE: Per conversation with BP, we may want to ignore inactive or non-billable users for this interface.
        //

        String stmtString = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                            "memNum, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, " + 
                            "mobile_iphone, msub_type " +
                            "FROM member2b " +
                            "WHERE inact = 0 AND billable = 1 AND flexid = ?";

        PreparedStatement pstmt = con.prepareStatement (stmtString);

        // Get user's pw if there is a matching user...

        pstmt.clearParameters();         // clear the parms
        pstmt.setString(1, username);    // put the username field in statement
        rs = pstmt.executeQuery();       // execute the prepared stmt

        if (rs.next()) {

            username = rs.getString("username");                           // get our username

            String lastName = rs.getString("name_last");                           // get last name

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

            String mi = rs.getString("name_mi");                                // middle initial
            if (!mi.equals( omit )) {
                mem_name.append(" ");
                mem_name.append(mi);
            }
            mem_name.append(" " + rs.getString("name_last"));                     // last name

            String name = mem_name.toString();                          // convert to one string


            // Get the member's membership type

            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type
            subtype = rs.getString("msub_type");  // Get member sub_type

            //
            // if 'activity' is being passed and its not an Event Signup request, then use the activity being requested
            //
            if (req.getParameter("activity_id") != null) {

                try {

                    default_activity_id = Integer.parseInt(req.getParameter("activity_id"));
                    
                } catch (Exception ignore) { }

            } else if (req.getParameter("activity") != null && eventName.equals("")) {

                int temp_activity_id = 0;

                try {
                    temp_activity_id = Integer.parseInt(req.getParameter("activity"));
                }
                catch (Exception exc) {
                    temp_activity_id = 0;
                }  

                default_activity_id = temp_activity_id;

            } else {

                if (!eventName.equals("")) {                  
                    default_activity_id = event_activity_id;      // if user going to an event signup, then use that activity id (from the event)
                } else {
                    default_activity_id = rs.getInt("default_activity_id");
                }
            }


            // Get the number of visits and update it...

            int count = rs.getInt("count");         // Get count
            count++;                                // bump counter..

            //  Get wc and last message displayed at login

            wc = rs.getString("wc");                            // w/c pref
            String message = rs.getString("message");           // message
            mNum = rs.getString("memNum");                      // member #
            mobile_count = rs.getInt("mobile_count");           // get # of mobile logins for this ueer
            mobile_iphone = rs.getInt("mobile_iphone");         // get # of iphone logins for this ueer


            pstmt.close();       //done with statement - close it


            message = "";    // for now


            //
            //  Trace good logins - display parms passed for verification purposes
            //
            logMsg = "Remote Login Successful: user_name from website=" +user+ ", Primary=No, mNum=No, Mapping=" +mapping+ ", IP=" + remote_ip + " ";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it

            recordLogin(user, "", club, remote_ip, 1);

            // Save the connection in the session block for later use.......
            HttpSession session = Utilities.getNewSession(req);   // Create a session object

            ConnHolder holder = new ConnHolder(con);      // create a new holder from ConnHolder class

            session.setAttribute("connect", holder);      // save DB connection holder
            session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
            session.setAttribute("user", username);       // save username
            session.setAttribute("name", name);           // save members full name
            session.setAttribute("club", club);           // save club name
            session.setAttribute("caller", caller);       // save caller's name
            session.setAttribute("mship", mship);         // save member's mship type
            session.setAttribute("mtype", mtype);         // save member's mtype
            session.setAttribute("msubtype", subtype);    // save member's sub_type
            session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
            session.setAttribute("posType", posType);     // save club's POS Type
            session.setAttribute("zipcode", zipcode);     // save club's zipcode
            session.setAttribute("tlt", tlt);             // timeless tees indicator
            session.setAttribute("mobile", mobile);       // set mobile indicator (0 = NOT, 1 = Mobile)
            session.setAttribute("activity_id", default_activity_id);  // activity indicator
            session.setAttribute("organization_id", organization_id);  // organization_id (set if using ForeTeesDining system)
            session.setAttribute("new_skin", (new_skin) ? "1" : "0");  // new skin flag
            session.setAttribute("premier_referrer", req.getHeader("referer"));       // referrer

            //
            // set inactivity timer for this session
            //
            session.setMaxInactiveInterval( MEMBER_TIMEOUT );
            
            
            String base_url = Utilities.getBaseUrl(req, default_activity_id, club);
            /*
            String base_url = "../"+club+"_golf/";
            if(default_activity_id == dining_activity_id){
                base_url = "../"+club+"_dining/";
            } else if(default_activity_id > 0) {
                base_url = "../"+club+"_flxrez"+default_activity_id+"/";
            }
             * 
             */
            if(!new_skin){
                base_url = "";
            }

          
            //
            //  Count the number of users logged in
            //
            countLogin("mem", con);

            // new stats logging routine
            recordLoginStat(2);
               
            if (mobile > 0) {

               countMobile(mobile_count, mobile_iphone, username, req, con);      // bump mobile counter and track mobile device 
            }


            //if (landing_page.equals("")) return;
            

            //
            //   Check for iPad (or like) device and prompt for standard or mobile access.
            //
            /*
            boolean enableAdvAssist = Utilities.enableAdvAssist(req);     // check for iPad (or like) device - false indicates iPad

            if (enableAdvAssist == false && allowMobile == true && default_activity_id == 0 && mobile == 0) {     // if Golf, iPad and mobile ok for this site, and user not already detected as mobile

               promptIpad(club, name, new_skin, default_activity_id, out, req);    // prompt user for mobile or standard interface

               countMobile(mobile_count, mobile_iphone, username, req, con);      // bump mobile counter and track mobile device (this was not done above)
               return;
            }
            */


            //
            //  Output the response and route to system
            //
            if (mobile == 0) {
               
                String refresh_url = "";

                if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)

                    if (event_activity_id == dining_activity_id) {
                             
                        int event_id = 0;

                        // Check to make sure a valid event_id was passed as the eventName parameter.  If not, route them to the event listing instead.
                        try {
                            event_id = Integer.parseInt(eventName);
                        } catch (Exception exc) {
                            event_id = 0;
                        }

                        if (eventName.equalsIgnoreCase("reservations")) {
                            refresh_url = base_url + "Dining_slot?action=new";
                        } else if (eventName.equalsIgnoreCase("calendar")) {
                            refresh_url = base_url + "Member_teelist";  
                        } else if (eventName.equalsIgnoreCase("partners")) {
                            refresh_url = base_url + "Member_partner";  
                        } else {
                            // eventName should contain a numerical event id
                            refresh_url = base_url + "Dining_home?view_events&event_id=" + eventName;
                        }
                             
                    } else {
                             
                        if (eventName.equalsIgnoreCase("reservations")) {
                            if (event_activity_id != 0 && event_activity_id != dining_activity_id) {
                                refresh_url = base_url + "Member_gensheets";
                            } else {
                                refresh_url = base_url + "Member_select";
                            }
                        } else if (eventName.equalsIgnoreCase("lessons")) {
                            refresh_url = base_url + "Member_lesson";  
                        } else if (eventName.equalsIgnoreCase("calendar")) {
                            refresh_url = base_url + "Member_teelist"; 
                        } else if (eventName.equalsIgnoreCase("partners")) {
                            refresh_url = base_url + "Member_partner"; 
                        } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none")) {
                            refresh_url = base_url + "Member_events";
                        } else {
                            refresh_url = base_url + "Member_events?name=" +eventName+ "&course=" +courseName; // was Member_events2
                        }
                    }
                        
                } else {    // no event

                    if (message.equals( "" )) {      // if no message to display

                        refresh_url = base_url + "Member_announce";

                    } else {

                        refresh_url = base_url + "Member_msg";
                    }

                    if (landing_page.equals("select")) {

                        if (default_activity_id == 0) {

                            // Golf
                            refresh_url = base_url + "Member_select";

                        } else if (default_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                            // Dining
                            refresh_url = base_url + "Dining_slot?action=new";

                        } else {

                            // FlxRez
                            refresh_url = base_url + "Member_gensheets";

                        }

                    } else if (landing_page.equals("sheet")) {

                        if (default_activity_id == 0) {

                            // Golf
                            refresh_url = base_url + "Member_sheet?index=0";

                        } else if (default_activity_id != ProcessConstants.DINING_ACTIVITY_ID) {

                            // FlxRez
                            refresh_url = base_url + "Member_gensheets";

                        }

                    } else if (landing_page.equals("event_listing")) {

                        if (default_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                            // Dining
                            refresh_url = base_url + "Dining_home?view_events";

                        } else {

                            // Golf or FlxRez
                            refresh_url = base_url + "Member_events";

                        }

                    } else if (landing_page.equals("lessons")) {

                        if (default_activity_id == 0) {

                            // Golf
                            refresh_url = base_url + "Member_lesson";

                        } else if (default_activity_id != ProcessConstants.DINING_ACTIVITY_ID) {

                            // FlxRez
                            refresh_url = base_url + "Member_lesson";

                        }

                    } else if (landing_page.equals("group_lessons")) {

                        if (default_activity_id == 0) {

                            // Golf
                            refresh_url = base_url + "Member_lesson?group=yes";

                        } else if (default_activity_id != ProcessConstants.DINING_ACTIVITY_ID) {

                            // FlxRez
                            refresh_url = base_url + "Member_lesson?group=yes";

                        }

                    } else if (landing_page.equals("lesson_bios")) {

                        if (default_activity_id == 0) {

                            // Golf
                            refresh_url = base_url + "Member_lesson?bio=yes";

                        } else if (default_activity_id != ProcessConstants.DINING_ACTIVITY_ID) {

                            // FlxRez
                            refresh_url = base_url + "Member_lesson?bio=yes";

                        }

                    } else if (landing_page.equals("partners")) {

                        // same for all
                        refresh_url = base_url + "Member_partner";

                    } else if (landing_page.equals("settings")) {

                        // same for all
                        refresh_url = base_url + "Member_services";

                    } else if (landing_page.equals("mytimes_list")) {

                        // same for all
                        refresh_url = base_url + "Member_teelist_list";

                    } else if (landing_page.equals("announce")) {

                        // same for all
                        refresh_url = base_url + "Member_announce";

                    }

                }                      

                // new skin (no meta refresh - use js instead)
                Common_skin.outputHeader(club, default_activity_id, "Member Login Page", true, out, req, 0, refresh_url);
                  
                out.close();
               
            } else {
               
               //
               //  Mobile Device - go to mobile menu page
               //
               out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>Member Welcome Page</Title>");
               out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/mobile/member_mobile_home.html\">");
               out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
               out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
               out.println("<BODY><CENTER>");
               out.println("<BR>Welcome " + name );                                           // Member's Name
               out.println("<BR><BR></div>");
               out.println("<form method=\"get\" action=\"/" + rev + "/mobile/member_mobile_home.html\">");
               out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form>");
               out.println("</CENTER></BODY></HTML>");
               out.close();              
            }


        } else {                               // username not found

            pstmt.close();                      // close the statement

            //
            //  Trace all failed login attempts
            //
            logMsg = "Remote Login Failed - Invalid User (msg#1) IP=" + remote_ip + " ";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it twice to get all info

            //
            //  Trace additional information and output error reply
            //
            logMsg = "Invalid Username Received. User Id " +username+ " does not exist in the ForeTees roster.";
            invalidRemote(new_skin, default_activity_id, logMsg, req, out, con);

            recordLogin(user, "", club, remote_ip, 0);

            if (con != null) {
                try {
                    con.close();       // Close the db connection........
                }
                catch (SQLException ignored) {
                }
            }

        }        // end of if username found
    }
    catch (SQLException exc) {

        errMsg = "DB Error. Exception: " +exc.getMessage();

        invalidRemote(new_skin, default_activity_id, errMsg, req, out, con);
        recordLogin(user, "", club, remote_ip, 0);

        try { con.close(); }
        catch (Exception e) {}

        return;
    }
 
 }    // end of flexWebUser 
 
 
 
 
 // *********************************************************
 //   Extract the username from user field (encrypted)
 // *********************************************************

 private String getUser(String user) {


   char[] ca = user.toCharArray();         // put user value in char array
   int x = 0;
   int x2 = 0;
   int i = 0;
   int i2 = 0;
   int d1 = 0;
   int d2 = 0;
   int d3 = 0;
   int d4 = 0;
   int d5 = 0;
   int d6 = 99;
   int d7 = 99;
   int d8 = 99;
   int d9 = 99;
   int d10 = 99;
   int f1 = 0;
   int f2 = 0;
   int f3 = 0;
   int f4 = 0;
   int f5 = 0;
   int f6 = 0;
   int f7 = 0;
   int f8 = 0;
   int f9 = 0;
   int f10 = 0;
   int f11 = 0;

   String sd4 = "";
   String sd5 = "";
   String sd6 = "";
   String sd7 = "";
   String sd8 = "";
   String sd9 = "";
   String sd10 = "";
     
   String sf5 = "";
   String sf6 = "";
   String sf7 = "";
   String sf8 = "";
   String sf9 = "";
   String sf10 = "";
   String sf11 = "";

   int length = user.length();

   user = "";                            // init user field

   if (length < 8 || length > 22) {
     
      return (user);                     // return null if invalid length
   }

   //
   //  user (member's local #, is encrypted - we must decrypt it)
   //
   //      X F 1 F 2 F 3 F 4 F 5 F, etc.
   //
   //          X = 10 - # of digits in local# (min of 3, max of 10)
   //          F = filler (10-((prev digit + 5)/2)) 
   //          1, 2, 3, etc. = local# digits
   //
   //   Isolate each digit -
   //
   char [] charx = new char [1];
   charx[0] = ca[0];
   String sx = new String (charx);       // get x value (# of digits)

   charx[0] = ca[1];
   String sf1 = new String (charx);       // get filler 

   charx[0] = ca[2];
   String sd1 = new String (charx);       // get local# digit #1

   charx[0] = ca[3];
   String sf2 = new String (charx);       // get filler

   charx[0] = ca[4];
   String sd2 = new String (charx);       // get local# digit #2

   charx[0] = ca[5];
   String sf3 = new String (charx);       // get filler

   charx[0] = ca[6];
   String sd3 = new String (charx);       // get local# digit #3

   charx[0] = ca[7];
   String sf4 = new String (charx);       // get filler

   if (length > 8) {

      charx[0] = ca[8];
      sd4 = new String (charx);       // get local# digit #4

      charx[0] = ca[9];
      sf5 = new String (charx);       // get filler
   }
   if (length > 10) {

      charx[0] = ca[10];
      sd5 = new String (charx);       // get local# digit #5

      charx[0] = ca[11];
      sf6 = new String (charx);       // get filler
   }
   if (length > 12) {

      charx[0] = ca[12];
      sd6 = new String (charx);       // get local# digit #6

      charx[0] = ca[13];
      sf7 = new String (charx);       // get filler
   }
   if (length > 14) {

      charx[0] = ca[14];
      sd7 = new String (charx);       // get local# digit #7

      charx[0] = ca[15];
      sf8 = new String (charx);       // get filler
   }
   if (length > 16) {

      charx[0] = ca[16];
      sd8 = new String (charx);       // get local# digit #8

      charx[0] = ca[17];
      sf9 = new String (charx);       // get filler
   }
   if (length > 18) {

      charx[0] = ca[18];
      sd9 = new String (charx);       // get local# digit #9

      charx[0] = ca[19];
      sf10 = new String (charx);       // get filler
   }
   if (length > 20) {

      charx[0] = ca[20];
      sd10 = new String (charx);       // get local# digit #10

      charx[0] = ca[21];
      sf11 = new String (charx);       // get filler
   }

   try {
      x = Integer.parseInt(sx);          // get int value of X
      f1 = Integer.parseInt(sf1);        // get filler values          
      f2 = Integer.parseInt(sf2);
      f3 = Integer.parseInt(sf3);
      f4 = Integer.parseInt(sf4);
      d1 = Integer.parseInt(sd1);          // get local# digits 
      d2 = Integer.parseInt(sd2);
      d3 = Integer.parseInt(sd3);

      if (length > 8) {
         f5 = Integer.parseInt(sf5);
         d4 = Integer.parseInt(sd4);
      }
      if (length > 10) {
         f6 = Integer.parseInt(sf6);
         d5 = Integer.parseInt(sd5);
      }
      if (length > 12) {
         d6 = Integer.parseInt(sd6);
      }
      if (length > 14) {
         d7 = Integer.parseInt(sd7);
      }
      if (length > 16) {
         d8 = Integer.parseInt(sd8);
      }
      if (length > 18) {
         d9 = Integer.parseInt(sd9);
      }
      if (length > 20) {
         d10 = Integer.parseInt(sd10);
      }

   }
   catch (NumberFormatException e) {
      return (user);                     // return null user if failed
   }

   x2 = x;                              // save x value
   x = 10 - x;                          // get actual # of digits in username
   i = i + 2;                           // bump to first digit

   char[] ca2 = new char [x];           // empty array to move x chars

   while (x > 0) {

      ca2[i2] = ca[i];                  // move digits into ca2

      i2++;
      i = i + 2;
      x = x - 1;
   }

   //
   //  Verify all digits received -- filler must equal (10 - ((prev digit + 5) / 2))
   //
   //   ****** just verify first 3 for now **********
   //
   x2 = 10 - ((x2 + 5)/2);
   d1 = 10 - ((d1 + 5)/2);
   d2 = 10 - ((d2 + 5)/2);
   d3 = 10 - ((d3 + 5)/2);
     
   if (x2 != f1) {
      return (user);                     // return null user if failed
   }
   if (d1 != f2) {
      return (user);                     // return null user if failed
   }
   if (d2 != f3) {
      return (user);                     // return null user if failed
   }
   if (d3 != f4) {
      return (user);                     // return null user if failed
   }

   return new String (ca2);             // return extracted user
 }


 // *********************************************************
 //   Verify that the last name received from MemFirst matches the last name in db
 //
 //      Only check up to 4 letters in case MemFirst's name does not
 //      match exactly what we have in our db.  Also, there could be
 //      some extraneous characters in last name.
 //
 // *********************************************************

 private String checkName(String lname, String lastName) {


   char[] ca1 = lname.toCharArray();         // put lname value in char array
   char[] ca2 = lastName.toCharArray();      // put lastName value in char array
   int recLength = lname.length(); 
   int ourLength = lastName.length();
   int move = 4;
   int i = 0;
   int i2 = 0;

   if (recLength > ourLength) {
     
      return (lname);              // error
   }
   if (recLength < 2) {

      return (lname);              // error
   }
   if (recLength < 4) {

      move = recLength;     
   }
   
   char[] rec = new char [move];           // empty arrays to move names
   char[] our = new char [move];         

   while (i < move) {

      //  do not copy spaces or underscores
      if (ca1[i2] != ' ' && ca1[i2] != '_') {
         rec[i] = ca1[i2];                  // move letters into received name
         i++;
      }
      i2++;
   }

   i = 0;
   i2 = 0;

   while (i < move) {

      //  do not copy spaces or underscores
      if (ca2[i2] != ' ' && ca2[i2] != '_') {
         our[i] = ca2[i2];                  // move letters into our name
         i++;
      }
      i2++;
   }

   lname = new String (rec);       // get new lname value
   lastName = new String (our);    // get new lastName value

   if (lname.equalsIgnoreCase( lastName )) {
     
      lname = "";                  // return null = ok
   }

   return (lname);
 }



 // *********************************************************
 // Process prompt user request - prompt for member to process
 // *********************************************************

 private boolean promptPrimary(int mobile, String mNum, String message, String club, boolean new_skin, int activity_id, 
                               PrintWriter out, HttpServletRequest req, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean done = false;
   boolean addit = false;
     
   String user = "";
   String fname = "";
   String mi = "";
   String lname = "";
   String mtype = "";
   String courseName = "";
   String eventName = "";
   String eventName_param = "";
   String sortBy = "";

   int count = 0;
   int signUp = 0;
   int temp_activity_id = activity_id;
     
   if (activity_id < 0 && activity_id != -999) {
       temp_activity_id = activity_id * -1;
   }
   
   String base_url = Utilities.getBaseUrl(req, temp_activity_id, club, 0);
   /*
     String base_url = "../" + club + "_golf/";
     if (temp_activity_id == dining_activity_id) {
         base_url = "../" + club + "_dining/";
     } else if (temp_activity_id > 0) {
         base_url = "../" + club + "_flxrez" + temp_activity_id + "/";
     }
    * 
    */
     if (!new_skin) {
         base_url = "";
     }
     
     if (req.getParameter("eventname") != null && !req.getParameter("eventname").equals("")) {
         
         eventName = req.getParameter("eventname");

          try {

              if (temp_activity_id == dining_activity_id) {  // Dining event
               
                  courseName = "";

              } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none") || eventName.equalsIgnoreCase("reservations") || eventName.equalsIgnoreCase("calendar") 
                      || eventName.equalsIgnoreCase("lessons") || eventName.equalsIgnoreCase("partners")) {

                  courseName = "";
                  signUp = 0;

              } else {

                  pstmt = con.prepareStatement(
                          "SELECT courseName, signUp "
                          + "FROM events2b WHERE name = ?");

                  pstmt.clearParameters();
                  pstmt.setString(1, eventName);
                  rs = pstmt.executeQuery();

                  if (rs.next()) {

                      courseName = rs.getString("courseName");        // get course name for Member_events2
                      signUp = rs.getInt("signUp");                   // signUp indicator for members

                  } else {           // not found - must be invalid name from MF

                      eventName = "";           // remove it, direct member to announce page instead
                  }

                  if (signUp == 0) {           // if members are not allowed to register for this event

                      eventName = "";           // remove it, direct member to announce page instead
                  }
              }
          } catch (Exception exc) {
              eventName = "";           // remove it, direct member to announce page instead
          } finally {
              try { rs.close(); }
              catch (Exception ignore) {}
              
              try { pstmt.close(); }
              catch (Exception ignore) {}
          }

          // If we have an eventname, build the parameter for the link.  
          if (!eventName.equals("")) {
              
              eventName_param = "&eventname=" + eventName;
              
              if (!courseName.equals("")) {
                  eventName_param += "&course=" + courseName;
              }
          }
     }

   //
   // find all members with this member# to display a selection list
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT m_type " +
         "FROM member2b WHERE memNum = ? AND inact = 0 AND billable = 1");

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, mNum);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      while (rs.next()) {

         mtype = rs.getString(1);

         if (club.equals( "cherryhills" )) {       // weed out juniors if Cherry Hills CC
           
            if (!mtype.startsWith( "Junior" )) {   // if not a junior

               count++;                            // count the number of members with this member#
            }

         } else {

            if (club.equals( "congressional" )) {       // weed out Dependents if Congressional

               if (!mtype.startsWith( "Dependent" ) && !mtype.endsWith( "Dependent" )) {   // if not a dependent

                  count++;                            // count the number of members with this member#
               }

            } else {

               count++;                   // count the number of members with this member#
            }
         }
      }

      pstmt.close();

      //
      //  If more than one member, then prompt for the name
      //
      if (count > 1) {

         //
         //  Output page to prompt for real user
         //
         if (mobile == 0) {       // if NOT mobile user
            
             if (new_skin) {
                 
                String title = "Member Identification";

                Common_skin.outputHeader(club, activity_id, title, true, out, req);     // output the page start

                out.println("<body>");
                out.println("<div id=\"wrapper_login\" align=\"center\">");
                out.println("<div id=\"title\">Welcome</div>");
                out.println("<div id=\"main_login\" align=\"center\">");
                out.println("<h1>Login Accepted</h1>");
                out.println("<div class=\"main_message\">");
                out.println("<h2>Member Identification Required</h2><br /><br />");
                out.println("<center><div class=\"sub_instructions\">");
                out.println("Please select your name from the following list:");
                out.println("<br /></div>");
                 
             } else {
                 
                out.println("<HTML><HEAD><Title>Member Login Page</Title>");
                out.println("</HEAD>");
                out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<p>&nbsp;</p>");
                out.println("<BR><H2>Member Identification Required</H2><BR>");
                out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

                out.println("<BR>Welcome to ForeTees");
                out.println("<font size=\"2\">");
                out.println("<BR><BR>");
                out.println("Please select your name from the following list:<BR><BR>");
             }
            
         } else {        // mobile user
            
            out.println(SystemUtils.HeadTitleMobile("ForeTees Mobile Name Prompt"));

            out.println("<div class=\"smheadertext\"><BR>Member Identification Required</div>");
            out.println("<div class=\"headertext\"><BR>Welcome to ForeTees" +
                        "<BR><BR>Please select your name.<BR><BR></div>");

            out.println("<div class=\"content\"><ul>");
         }
         
         if (club.equals("shadycanyongolfclub")) {
             sortBy = " ORDER BY username";
         } else if (club.equals("roccdallas")) {
             sortBy = " ORDER BY posid";
         }
           
         pstmt = con.prepareStatement (
            "SELECT username, name_last, name_first, name_mi, m_type " +
            "FROM member2b WHERE memNum = ? AND inact = 0 AND billable = 1" + sortBy);

         pstmt.clearParameters();         // clear the parms
         pstmt.setString(1, mNum);        // put the username field in statement
         rs = pstmt.executeQuery();       // execute the prepared stmt

         while (rs.next()) {

            user = rs.getString(1);                         
            lname = rs.getString(2);
            fname = rs.getString(3);
            mi = rs.getString(4);
            mtype = rs.getString(5);

            addit = true;                // default to ok (use boolean in case we add more checks here)
              
            //
            // weed out juniors if Cherry Hills CC
            //
            if (club.equals( "cherryhills" ) && mtype.startsWith( "Junior" )) {  

               addit = false;                             // do not add this member

            } else {

               if (club.equals( "congressional" )) {       // weed out Dependents if Congressional

                  if (mtype.startsWith( "Dependent" ) || mtype.endsWith( "Dependent" )) {   // if a dependent

                     addit = false;                             // do not add this member
                  }
               }
            }

            if (addit == true) {

               //
               //  Build the name
               //
               StringBuffer mem_name = new StringBuffer(fname);  // get first name

               if (!mi.equals( "" )) {
                  mem_name.append(" " + mi);             // add mi
               }
               mem_name.append(" " + lname);             // add last name

               String name = mem_name.toString();        // convert to one string
               String url_name = name;
               
               // add for brookings cc, but any clubs with a trailing # in their last name will need this
               if (name.endsWith("#")) {
                   
                   url_name = name.substring(0, name.length() - 1) + "%23";
               }
               
               //
               //   Output a link with the member's name
               //
               if (mobile == 0) {

                 if (new_skin) {
                     out.println("<a href=\"" + base_url + "Member_msg?user=" +user+ "&name=" +url_name+ "&message=" + message + eventName_param + "\" style=\"color:#336633\" alt=\"" +name+ "\">");
                     out.println(name + "</a><br>");
                 } else {
                     out.println("<a href=\"Member_msg?user=" +user+ "&name=" +url_name+ "&message=" + message + eventName_param + "\" style=\"color:#336633\" alt=\"" +name+ "\">");
                     out.println(name + "</a><br>");
                 }

               } else {

                  out.println("<li><a href=\"Member_msg?user=" +user+ "&name=" +url_name+ "&message=" +message+ "\" style=\"color:#336633\" alt=\"" +name+ "\">" +name+ ". . . . . . . .</a></li>");
               }
            }
         }

         pstmt.close();

         if (mobile == 0) {
     
             if (new_skin) {
                 
                out.println("</center></div></div>");
                Common_skin.outputPageEnd(club, activity_id, out, req);    // finish the page       
                
             } else {
                 
                 out.println("</font>");
                 out.println("</td></tr></table><br></CENTER>");
                 out.println("</BODY></HTML>");
             }
         
         } else {
         
            out.println("</ul></div>");
            out.println("</BODY></HTML>");
         }
         
         out.close();

         done = true;        // set return indicator

      }    // end of IF count 

   }
   catch (SQLException exc) {
   }
     
   return(done);
 }

  
 
 // *********************************************************
 // Process user Help request - get password
 // *********************************************************

 private void userHelp(HttpServletRequest req,
                 PrintWriter out, String user, String club, boolean mobile) {

   Connection con = null;                  // init DB objects
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String password = "";
   String email = "";
   String email2 = "";
   
   int email_bounced = 0;   

   String errMsg = "Required parameters not provided.";

   //
   //  Verify parms
   //
   if (user.equals( "" ) || club.equals( "" )) {     // if user or club not provided

      invalidLogin(errMsg, req, out, con);   // process invalid login information.....
      return;
   }

   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
      con = dbConn.Connect(club);          // get a connection
   }
   catch (Exception exc) {

     con = null;
     invalidLogin(errMsg, req, out, con);   // process invalid login information.....
     return;
   }

   String club2 = club;                             // normally the same value

   if (req.getParameter("club2") != null) {         // if club2 specified (Greeley CC)

      club2 = req.getParameter("club2");            // get it (name of club site to return to)
   }
   
   //
   //  if from Mobile Login page, then convert mobile username to normal username
   //
   if (req.getParameter("mobilehelp") != null) {
      
      try {
         
         pstmt = con.prepareStatement (
            "SELECT username " +
            "FROM member2b WHERE mobile_user = ?");

         pstmt.clearParameters();         
         pstmt.setString(1, user);        // put the mobile username field in statement
         rs = pstmt.executeQuery();       

         if (rs.next()) {

            user = rs.getString(1);     // get username
         }
         
         pstmt.close();

      }
      catch (SQLException exc) {
      }  
   }
      

   //
   // use a prepared statement to find username (string) in the DB..
   //
   try {

      if (mobile == false) {
         
         pstmt = con.prepareStatement (
            "SELECT password, name_last, name_first, name_mi, email, email2, email_bounced " +
            "FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");

      } else {   // request is for mobile password (from Member_services)
         
         pstmt = con.prepareStatement (
            "SELECT mobile_pass, name_last, name_first, name_mi, email, email2, email_bounced " +
            "FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");
      }
         
         
      // Get user's pw if there is a matching user...

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, user);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         password = rs.getString(1);                                  // get password or mobile pw
         String lastName = rs.getString(2);                           // get last name
         email = rs.getString("email");                               // get email address
         email2 = rs.getString("email2");                             // get email2 address
         email_bounced = rs.getInt("email_bounced");

         // Get the member's full name.......

         StringBuffer mem_name = new StringBuffer(rs.getString(3));  // get first name

         String mi = rs.getString(4);                                // middle initial
         if (!mi.equals( omit )) {
            mem_name.append(" ");
            mem_name.append(mi);
         }
         mem_name.append(" " + rs.getString(2));                     // last name

         String name = mem_name.toString();                          // convert to one string
         
         if (email.equals( "" ) || email_bounced != 0) {
            
            email = email2;             // try email 2
         }
         

         //
         //  If email address specified, send the user his/her password
         //
         if (!password.equals( "" ) && !email.equals( "" )) {     // if password & email provided

            //
            //  allocate a parm block to hold the email parms
            //
            parmEmail parme = new parmEmail();          // allocate an Email parm block

            //
            //  Set the values in the email parm block
            //
            parme.type = "password";         // type = tee time
            parme.name = name;
            parme.user = user;
            parme.password = password;
            parme.email = email;

            //
            //  Send the email
            //
            sendEmail.sendIt(parme, con);      // in common

            //
            //  Done - reply ok
            //
            if (req.getParameter("mobilehelp") == null) {    // if NOT Mobile user
      
               out.println(SystemUtils.HeadTitle("Help Reply"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H2>Login Credentials Have Been Emailed</H2><BR>");
               out.println("<BR>Thank you " +name+ ". You should receive an email shortly.<BR>");
               if (mobile == false) {
                  out.println("<form method=\"get\" action=\"/" +club2+ "\">");
               } else {
                  out.println("<form method=\"get\" action=\"Member_services\">");
               }
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form>");
               out.println("</CENTER></BODY></HTML>");

            } else {

               out.println(SystemUtils.HeadTitleMobile("Login Help"));
               out.println("<div class=\"headertext\"> Password Has Been Emailed </div>");
               out.println("<div class=\"smheadertext\">");

               out.println("Thank you.<BR><BR>You should receive your password via email shortly.");
               
               out.println("<br><br><A HREF=\"http://m.foretees.com/" +club2+ "\">Back to Login</A>");
               out.println("</div></body></html>");           
            }
            out.close();

         } else {  // password or email address not found

            if (req.getParameter("mobilehelp") == null) {    // if NOT Mobile user
      
               out.println(SystemUtils.HeadTitle("Help Reply"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H2>Unable to Email Credentials</H2><BR>");
               out.println("<BR>Sorry " +name+ ". We are unable to email your password<BR>");
               out.println("because you have not provided a valid email address.<BR>");
               out.println("<BR>Please contact your club's golf professionals for assistance.<BR>");
               if (mobile == false) {
                  out.println("<form method=\"get\" action=\"/" +club2+ "\">");
               } else {
                  out.println("<form method=\"get\" action=\"Member_services\">");
               }
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form>");
               out.println("</CENTER></BODY></HTML>");
               
            } else {

               out.println(SystemUtils.HeadTitleMobile("Login Help"));
               out.println("<div class=\"headertext\"> Unable to Send Password </div>");
               out.println("<div class=\"smheadertext\">");

               out.println("Sorry, you do not have a valid email address in ForeTees.<BR><BR>Please login with your PC and select the Settings tab to add/change your mobile credentials. ");
               
               out.println("<br><br><A HREF=\"http://m.foretees.com/" +club2+ "\">Back to Login</A>");
               out.println("</div></body></html>");           
            }
            out.close();
         }

      } else {    // no match on username

         invalidLogin(errMsg, req, out, con);   // process invalid login information.....
      }

      pstmt.close();

   }
   catch (SQLException exc) {

      invalidLogin(errMsg, req, out, con);   // process invalid login information.....
   }
 }


 // ***************************************************************
 //  countLogin
 //
 //      Track the number of users logged in for each club.
 //
 // ***************************************************************

 public static void countLogin(String type, Connection con) {


    //
    //  Keep internal counts in SystemUtils - these will reset each time tomcat is bounced
    //
    if (type.equals( "pro" ) || type.equals( "mem" )) {      // skip admin and hotel users

        if (SystemUtils.startDate.equals( "" )) {             // if first login since bounce

            SystemUtils.startDate = String.valueOf( new java.util.Date() );   // set new date & time                  
        }

        //
        //  Get the hour of day (24 hr clock)
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int hour = cal.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)

        //
        //  Increment the counter for this hour
        //
        if (type.equals( "pro" )) {   

            SystemUtils.loginCountsPro[hour]++;
        }

        if (type.equals( "mem" )) {

            SystemUtils.loginCountsMem[hour]++;
        }
    }
     
 }


 // ***************************************************************
 //  initLogins  (not used 10/16/06)
 //
 //      Reset the number of users logged in for each club.
 //
 // ***************************************************************
/*
 public static void initLogins() {


   Connection con = null;                 // init DB objects
   Connection con2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs2 = null;

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "v5";

   try {

      con2 = dbConn.Connect(club);
   }
   catch (Exception exc) {
      return;
   }

   //
   //   Get each club's login count and total them
   //
   try {
      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name

         con = dbConn.Connect(club);         // get a connection to this club's db

         stmt = con.createStatement();           // create a statement

         stmt.executeUpdate("UPDATE club5 SET logins = 0");          // reset login count

         stmt.close();

         con.close();                           // close the connection to the club db
      }                                         // do all clubs
      stmt2.close();
      con2.close();
   }
   catch (Exception ignore) {
   }

 }
*/
 
 // ***************************************************************
 //  getTLT(Connection con)
 //
 //      Get the club's time-less tees support indicator.
 //
 // ***************************************************************

 private boolean getTLT(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;
    int tlt = 0;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT no_reservations FROM club5");
        if (rs.next()) tlt = rs.getInt(1);
        stmt.close();
        
    } catch (Exception ignore) {
        
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }
    
    return ((tlt == 1) ? true : false);
 }
 

 // ***************************************************************
 //  getRS(Connection con)
 //
 //      Get the club's Roster Sync support indicator.
 //
 // ***************************************************************

 private int getRS(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;
    int rsync = 0;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT rsync FROM club5");
        if (rs.next()) rsync = rs.getInt(1);
        stmt.close();
      
    } catch (Exception ignore) {
       
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }

        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return (rsync);
 }


 // ***************************************************************
 //  getLottery
 //
 //      Get the club's Lottery Support Indicator.
 //
 // ***************************************************************

 private String getLottery(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    int lottery = 0;
    String lotteryS = "0";

    try {
        
        stmt = con.createStatement();        // create a statement
        rs = stmt.executeQuery("SELECT lottery FROM club5");          // get lottery flag

        if (rs.next()) lottery = rs.getInt("lottery");
        
        stmt.close();

    } catch (Exception ignore) {

    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }
     
    if (lottery > 0) {

        lotteryS = "1";        // return 'lottery supported' indicator
    }

    return(lotteryS);
 }


 // ***************************************************************
 //  getNSdate
 //
 //      Get the club's New Skin Date.   ****** Only needed prior to 6/01/2012 ********
 //
 // ***************************************************************

 private static int getNSdate(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    int nsDate = 0;

    try {
        
        stmt = con.createStatement();        // create a statement
        rs = stmt.executeQuery("SELECT new_skin_date FROM club5");          // get lottery flag

        if (rs.next()) nsDate = rs.getInt("new_skin_date");
        
        stmt.close();

    } catch (Exception ignore) {

    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }
     
    return(nsDate);
 }


 // ***************************************************************
 //  getPOS
 //
 //      Get the club's POS Type.
 //
 // ***************************************************************

 public static String getPOS(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    String posType = "";

    try {
        
        stmt = con.createStatement();        // create a statement
        rs = stmt.executeQuery("SELECT posType FROM club5");          // get pos type

        if (rs.next()) posType = rs.getString("posType");
        
        stmt.close();

    } catch (Exception ignore) {
       
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return(posType);
 }


 // *********************************************************
 //  Convert username from xxxx-00n to xxxxn
 // *********************************************************

 private final static String convertFlex( String s ) {

   String part1 = s;
   String part2 = "";
     
   int suf = 0;

   //
   //  parse the string to get the '-00n'
   //
   StringTokenizer tok = new StringTokenizer( s, "-" );

   if ( tok.countTokens() > 1 ) {

      part1 = tok.nextToken();
      part2 = tok.nextToken();

      suf = Integer.parseInt(part2);     // convert to int to get absolute number (i.e.  001 = 1)
   }

   part1 = part1 + suf;                  // combine (default = xxxx0)

   return (part1);

 } // end convertFlex


 // ********************************************************************************
 //  Trim the username if more than 15 characters - ZSmart Web - Oswego Lake CC
 // ********************************************************************************

 private final static String trimZsmart( String s ) {

   char[] ca = s.toCharArray();

   char[] ca2 = new char [15];      // new char array

   String user = s;
     
   if (ca.length > 15) {
     
      for (int i=0; i < 15; i++) {

         ca2[i] = ca[i];               // copy first 15 chars
      }

      user = new String (ca2);         // return trimmed down username
   }

   return (user);

 } // end trimZsmart


 // *********************************************************
 //  Remove leading zeros in member id string
 // *********************************************************

 private final static String remZero( String s ) {
/*
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
 * 
 */
     
     while (s.startsWith("0")) {
         s = s.substring(1);
     }
     
     return (s);

 } // end remZero


 // *********************************************************
 //  Remove leading zeros in member id string
 // *********************************************************

 private final static String remZero2( String s ) {

     /*
      int i = 0;
      int count = 0;
      String s2 = s;
      char[] ca = s.toCharArray();

      loop1:
      while (i < ca.length) {
        
         char letter = ca[i];
         if ( letter == '0' ) {        // if leading zero
            count++;                   // count them           
         } else {
            break loop1;               // else exit
         }
         i++;
      }
        
      if (count > 0) {
           
         char[] ca2 = new char [ca.length - count];      // new char array
           
         i = 0;
         while (count < ca.length) {

            char letter = ca[count];                     // set new string w/o zeros
            ca2[i] = letter;
            i++;
            count++;
         }           
         s2 = new String (ca2);
      }
      
      return (s2);
      * 
      */
     
     while (s.startsWith("0")) {
         s = s.substring(1);
     }
     
     return(s);

 } // end remZero2


 // *********************************************************
 //  Strip last letter from end of string
 // *********************************************************

 private final static String stripA2( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i];
         ca2[i] = oldLetter;
      }

      return new String (ca2);

 } // end stripA2


 // *********************************************************
 // Connection error received - inform user to try again....
 // *********************************************************

 private void Connerror(PrintWriter out, Exception exc, Connection con) {

   out.println(SystemUtils.HeadTitle("Connection Error - Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Connection Error</H2><BR>");
   out.println("<BR>Sorry, we are unable to connect to the system database at this time.<BR>");
   out.println("<BR>Exception: " + exc.getMessage());
   out.println("<BR>Please <A HREF=\"javascript:history.back(1)\">try again</A> later.");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   if (con != null) {
      try {
         con.close();
      }
      catch (SQLException ignored) {
      }
   }
 }

 private void Connerror2(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Connection Error - Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Connection Error2</H2><BR>");
   out.println("<BR>Sorry, we are unable to connect to the system database at this time.<BR>");
   out.println("<BR>Please <A HREF=\"javascript:history.back(1)\">try again</A> later.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Invalid Login data received - inform user to try again....
 // *********************************************************

 private void invalidLogin(String errMsg, HttpServletRequest req, PrintWriter out, Connection con) {

    
    invalidLogin(errMsg, req, out, con, 0);          // must not be a mobile user
 }
  
 private void invalidLogin(String errMsg, HttpServletRequest req, PrintWriter out, Connection con, int mobile) {

  
   String user = "";
   String club = "";
   String pw = "";
     
   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");    
   }
   if (req.getParameter("user_name") != null) {

      user = req.getParameter("user_name");
   }

   if (req.getParameter("password") != null) {

      pw = req.getParameter("password");
   }

   //
   //  Trace all login attempts
   //
   if (con != null) {
     
      String logMsg = "Login Failed - Invalid Login " + (mobile != 0 ? "for Mobile User " : "") + "- Error: " +errMsg;
      SystemUtils.sessionLog(logMsg, user, pw, club, omit, con);                   // log it
   }

   if ( club.equals("newcanaan") && user.startsWith("proshop") ) {
   
       out.println(SystemUtils.HeadTitle("Invalid Login"));
       out.println("<script>window.location.href='http://216.243.184.83:8080/login.htm';</script>");
       out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
       out.println("<hr width=\"40%\">");
       out.println("<p>&nbsp;</p><p>&nbsp;</p>");
       out.println("<BR><H2>Login Rejected</H2><BR>");
       out.println("<BR>The login information you submitted was either missing or invalid.");
       out.println("<BR><BR>Error: " +errMsg);
       out.println("<BR><BR><BR>Please <A HREF=\"javascript:void(0)\">try again</A>.");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       
   } else if (mobile > 0) {

      //
      //  Mobile user
      //
      out.println(SystemUtils.HeadTitleMobile("Invalid Login"));
      out.println("<div class=\"headertext\"> Login Rejected </div>");
      out.println("<div class=\"smheadertext\">");
      if (errMsg.startsWith("Mobile not supported")) {          
         out.println("Mobile Not Supported");
         out.println("<BR><BR>Sorry, but your club does not allow direct access from a mobile device.");
         out.println("<BR><BR>Please <A HREF=\"javascript:history.back(1)\">Return</A>.");
      } else {
         out.println("The login information you submitted<BR>was either missing or invalid.");
         out.println("<BR><BR>Error: " +errMsg);
         if ( !user.startsWith("proshop") ) {
             out.println("<BR><BR>NOTE: Remember to use your MOBILE credentials.<BR>You can set or change these<BR>by logging in from your PC and<BR>clicking on the Settings tab.");
         }
         out.println("<BR><BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A>.");
      }
      out.println("</div></body></html>");       
      out.close();
      
   } else {
 
       out.println(SystemUtils.HeadTitle("Invalid Login"));
       out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
       out.println("<hr width=\"40%\">");
       out.println("<p>&nbsp;</p><p>&nbsp;</p>");
       out.println("<BR><H2>Login Rejected</H2><BR>");
       out.println("<BR>The login information you submitted was either missing or invalid.");
       out.println("<BR><BR>Error: " +errMsg);
       out.println("<BR><BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A>.");
       out.println("</CENTER></BODY></HTML>");
       out.close();
   }
   
 }

 // *********************************************************
 // Invalid call from remote user - reject
 // *********************************************************

 private void invalidRemote(boolean new_skin, int activity_id, String errMsg, HttpServletRequest req, PrintWriter out, Connection con) {
    
     invalidRemote(new_skin, activity_id, errMsg, req, out, con, 0);
 }


 private void invalidRemote(boolean new_skin, int activity_id, String errMsg, HttpServletRequest req, PrintWriter out, Connection con, int mobile) {


   String user = "";
   String club = "";
   String pw = "";
   String caller = "";
   String primary = "";
   String mNum = "";
   String mapping = "";
   String strip = "";
   String lname = "";

   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");
   }
   if (req.getParameter("user_name") != null) {

      user = req.getParameter("user_name");
   }

   if (req.getParameter("last_name") != null) {

      lname = req.getParameter("last_name");
   }

   if (req.getParameter("password") != null) {

      pw = req.getParameter("password");
   }

   if (req.getParameter("caller") != null) {

      caller = req.getParameter("caller");
   }

   if (req.getParameter("primary") != null) {

      primary = req.getParameter("primary");
   }

   if (req.getParameter("mnum") != null) {

      mNum = req.getParameter("mnum");
   }

   if (req.getParameter("mapping") != null) {

      mapping = req.getParameter("mapping");      // get mapping parm - used to map member ids
   }
     
   if (req.getParameter("stripzero") != null) {

      strip = req.getParameter("stripzero");   
   }

   //
   //  Trace all login attempts
   //
   if (con != null) {

      String logMsg = "Login Failed - Invalid Remote - Error: " +errMsg;
      logMsg = logMsg + " Primary=" +primary+ ", mNum=" +mNum+ ", mapping=" +mapping+ ", stripzero=" +strip+ ", ";
      
      if (caller.equals("CLUBSTER")) {
         
          logMsg = logMsg + "lname=" +lname+ ", ";
      }
      
      SystemUtils.sessionLog(logMsg, user, pw, club, caller, con);                   // log it
        
      try {

         con.close();                // close the connection

      }
      catch (Exception exp) {
      }

   }

   if (mobile > 0) {
      
      //  mobile user
      
      out.println(SystemUtils.HeadTitleMobile("Invalid Login"));
      out.println("<div class=\"headertext\"> Access Rejected </div>");
      out.println("<div class=\"smheadertext\">");
      out.println("Unable to connect to ForeTees.");
      out.println("<BR><BR>" +errMsg+ "<BR><BR>Please contact your club for assistance.");
      out.println("</div></body></html>");           
      
   } else if (new_skin) {
      
      String clubName = Utilities.getClubName(con, true);        // get the full name of this club
   
      String msgtext = "Sorry, there is a problem with the connection request." +
               "<BR><BR>Some information provided was either missing or invalid." + 
               "<BR><BR>Error: " + errMsg;

      if (club.equals("olyclub")) {
          errMsg += "<BR><BR>Contact your Web Site Administrator at <a href=\"mailto:website@olyclub.com\">website@olyclub.com</a> for assistance (provide this message).";
      } else {
          errMsg += "<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).";
      }
      
      Common_skin.outputError(club, clubName, activity_id, "Access Rejected", msgtext, "close", out, req);  
      
   } else {
      
      out.println(SystemUtils.HeadTitle("Invalid Login"));
      out.println("<BODY><CENTER><img src=\"/" + ProcessConstants.REV + "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>Access Rejected</H2><BR>");
      out.println("<BR>Some information provided was either missing or invalid.");
      out.println("<BR><BR>Error: " +errMsg);

      if (club.equals("olyclub")) {
          out.println("<BR><BR>Contact your Web Site Administrator at <a href=\"mailto:website@olyclub.com\">website@olyclub.com</a> for assistance (provide this message).");
      } else {
          out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
      }
      out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
      out.println("</CENTER></BODY></HTML>");
   }
   out.close();

 }


 private void rejectCaller(PrintWriter out) {
 
      out.println(SystemUtils.HeadTitle("Invalid Request"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>Invalid Access</H2><BR>");
      out.println("<BR>Sorry, some required information is missing.");
      out.println("<BR>Please return to your club's website and try again, or contact your club's website administrator.  Thank you.<BR>");
      out.println("<BR><BR>");
      out.println("<form><input type=\"button\" value=\"Close\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();      
 }
 
 
 private void rejectReferer(PrintWriter out) {
 
      out.println(SystemUtils.HeadTitle("Invalid Request"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>Invalid Access</H2><BR>");
      out.println("<BR>Sorry, you are not authorized to access ForeTees without going through your club's website or the ForeTees login.");
      out.println("<BR>Please return to your club's website, or contact your club's website administrator.");
      out.println("<BR><BR>If you feel you recevied this message in error, please contact ForeTees at support@foretees.com.");
      out.println("<BR>Include the name of your club, your name and your member number. Thank you.");
      out.println("<BR><BR>");
      out.println("<form><input type=\"button\" value=\"Close\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();      
 }
 
 
 // *********************************************************
 //  Mmember 'Continue' with possible emaill addresses
 // *********************************************************

 private void processEmail(HttpServletRequest req, PrintWriter out) {

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) return;

   con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) return;

   Member member = new Member();

   String club = (String)session.getAttribute("club");   // get user's club
   String caller = (String)session.getAttribute("caller");   // get caller (mfirst, etc.)
   String user = (String)session.getAttribute("user");   // get user's username value
   boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String message = "";
   String email = "";
   String email2 = "";
   String bounced = "";
   String title = "";
   String replyMsg = "You will now be directed to ForeTees.";

   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
   
   int pause = 0;
   int iCal1 = 0;
   int iCal2 = 0;

   boolean rsync = true;
     
   //
   //  Get club message and possible emails
   //
   if (req.getParameter("message") != null) {

      message = req.getParameter("message");
   }
   if (req.getParameter("email") != null) {

      email = req.getParameter("email").trim();
   }
   if (req.getParameter("email2") != null) {

      email2 = req.getParameter("email2").trim();
   }
   if (req.getParameter("bounced") != null) {
       
      bounced = req.getParameter("bounced");
   }
   if (req.getParameter("iCal1") != null) {

       iCal1 = (req.getParameter("iCal1").equals("1")) ? 1 : 0;
   }
   if (req.getParameter("iCal2") != null) {

       iCal2 = (req.getParameter("iCal2").equals("1")) ? 1 : 0;
   }
   
   email = email.trim();              // remove any spaces
   email2 = email2.trim();            // remove any spaces

            
   //
   //  Check if either emaill addresses were added
   //
   if (!email.equals( "" ) || !email2.equals( "" )) {

      pause = 5;

      //
      //  Verify the email address(es)
      //
      if (!email.equals( "" )) {                   // if specified

         FeedBack feedback = (member.isEmailValid(email));

         if (!feedback.isPositive()) {              // if error

            email = "";                             // do not add
         }
      }
      if (!email2.equals( "" )) {                   // if specified

         FeedBack feedback = (member.isEmailValid(email2));

         if (!feedback.isPositive()) {              // if error

            email2 = "";                            // do not add
         }
      }

      if (email.equals( "" ) && email2.equals( "" )) {    // if failed

         replyMsg = "The email address you entered is not valid and has not been added to the system.<br>You will now be directed to ForeTees.";
      }
   }
   
   // based upon caller - jump to desired page
    
   String base_url = Utilities.getBaseUrl(req, sess_activity_id, club);
   /*
     String base_url = "../" + club + "_golf/";
     if (sess_activity_id == dining_activity_id) {
         base_url = "../" + club + "_dining/";
     } else if (sess_activity_id > 0) {
         base_url = "../" + club + "_flxrez" + sess_activity_id + "/";
     }
    * 
    */
     if (!new_skin) {
         base_url = "";
     }
   
  if ((club.equals("talbotcc") || club.equals("dallasathleticclub") || club.equals("reflectionridgegolf") || club.equals("poycc") 
          || (club.equals("rehobothbeachcc") && req.getParameter("return") == null)) && email.equals("") && email2.equals("")) {

      if (new_skin) {

         title = "Member Email Update";

         Common_skin.outputHeader(club, sess_activity_id, title, true, out, req);     // output the page start

         out.println("<body>");
         out.println("<div id=\"wrapper_login\" align=\"center\">");
         out.println("<div id=\"title\">" +clubName+ "</div>");
         out.println("<div id=\"main_login\" align=\"center\">");
         out.println("<h1>Email Update</h1>");
         out.println("<div class=\"main_message\">");
         out.println("<h2>Notice:</h2><br /><br />");
         out.println("<center><div class=\"sub_instructions\">");

          out.println("An email address was not entered, or was not a properly formatted email address." +
                  "<br><br>In order to ensure that tee time notifications and club communications are received, " +
                  "<br>your club has requested that email addresses be required for all members.");
          out.println("<br><br>");
          out.println("Please add at least one valid email address below, " +
                  "<br>or contact the Golf Shop staff at your club if you have any questions.");

      } else {     // old skin
      
          out.println("<HTML><HEAD><Title>Member Login Page</Title></HEAD>");
          out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
          out.println("<hr width=\"40%\">");
          out.println("<p>&nbsp;</p>");
          out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

          out.println("<br><font color=\"red\"><b>Notice:</b></font> An email address was not entered, or was not a properly formatted email address." +
                  "<br><br>In order to ensure that tee time notifications and club communications are received, " +
                  "<br>your club has requested that email addresses be required for all members.");
          out.println("<br><br>");
          out.println("Please add at least one valid email address below, " +
                  "<br>or contact the Golf Shop staff at your club if you have any questions.");
       }
          
       out.println("<br><br>");
       out.println("Thank you!");

       out.println("<form method=\"post\" action=\"Login\">");
       out.println("<input type=\"hidden\" name=\"return\">");
       out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");

       out.println("<b>Email Address 1:</b>&nbsp;&nbsp;");
       out.println("<input type=\"text\" name=\"email\" value=\"" +email+ "\" size=\"40\" maxlength=\"50\">");

       out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">iCal attachments</a> at this email address? ");
       out.println("<select size=\"1\" name=\"iCal1\">");
       out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
       out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
       out.println("</select>");

       out.println("<br><br>");

       out.println("<b>Email Address 2:</b>&nbsp;&nbsp;");
       out.println("<input type=\"text\" name=\"email2\" value=\"" +email2+ "\" size=\"40\" maxlength=\"50\">");

       out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=\"memberHelpFT\">iCal attachments</a> at this email address? ");
       out.println("<select size=\"1\" name=\"iCal2\">");
       out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
       out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
       out.println("</select>");

       out.println("<br><br>");
      
      if (new_skin) {      
      
         out.println("</div>");
         out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\">");
         out.println("</form></center></div></div>");

         Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       

      } else {
      
          out.println("</td></tr></table><br>");
          out.println("<br><br>");
          out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form>");
          out.println("</CENTER></BODY></HTML>");
       }
       out.close();
  }

   // if we're here to handle to bounced email response - let's handle that first
   if (!bounced.equals("")) {

       try {
           stmt = con.createStatement();

           rs = stmt.executeQuery("SELECT rsync FROM club5");

           if (rs.next()) {
               if (rs.getInt("rsync") == 0) rsync = false;
           }

           stmt.close();

       } catch (Exception exc) {
           rsync = true;
       }
       
       try {
           
           pstmt = con.prepareStatement("SELECT email, email2 FROM member2b WHERE username = ?");
           pstmt.clearParameters();
           pstmt.setString(1, user);
           rs = pstmt.executeQuery();
              
           if (rs.next()) {
           
               if (email.equalsIgnoreCase(rs.getString("email")) || email.equalsIgnoreCase(rs.getString("email2"))) {

                   pstmt = con.prepareStatement("UPDATE member2b SET " + bounced + "_bounced='0' WHERE username = ?");
                   pstmt.clearParameters();
                   pstmt.setString(1, user);
                   pstmt.executeUpdate();
                   
                   replyMsg = (!rsync ? "A new email was not specified, " : "Your ") + "current email is no longer flagged as bouncing.<br>" +
                           "If the email is still incorrect, it will be flagged again after the next bounced email.<br>" +
                           "You will now be directed to ForeTees.";
               } else {

                   try {

                       pstmt = con.prepareStatement("UPDATE member2b SET " + bounced + "= ?, " +
                               bounced + "_bounced='0' WHERE username = ?");
                       pstmt.clearParameters();;
                       pstmt.setString(1, email);
                       pstmt.setString(2, user);
                       pstmt.executeUpdate();

                       replyMsg = "Your email address has been changed in the system.<br>You will now be directed to ForeTees.";
                   } catch (Exception exc) {

                        SystemUtils.buildDatabaseErrMsg("DB error in Login.processEmail.", exc.toString(), out, true);
                        replyMsg = "There was a problem adding your email address to the system.<br>You will now be directed to ForeTees.";
                   }
               }
               
               // Make sure emails are set up correctly
               Support_errorlog.ensureCorrectEmailSetup(user, club, out);
           }
       } catch (Exception exc) {
           
           SystemUtils.buildDatabaseErrMsg("DB error in Login.processEmail.", exc.toString(), out, true);
           replyMsg = "There was a problem accessing your database.<br>You will now be directed to ForeTees.";
       
       } finally {

          if (rs != null) {
             try {
                rs.close();
             } catch (SQLException ignored) {}
          }

          if (pstmt != null) {
             try {
                pstmt.close();
             } catch (SQLException ignored) {}
          }
       }
       
   } else if (!email.equals( "" ) || !email2.equals( "" )) {
   
      replyMsg = "Your email address has been added to the system.<br>You will now be directed to ForeTees.";
        
      try {

         //
         //  Store the email address(es)
         //
         if (!email.equals( "" ) && !email2.equals( "" )) {    // if both provided and ok

            pstmt = con.prepareStatement( "UPDATE member2b SET " +  ((iCal1 == 1 || iCal2 == 1) ? "emailOpt = 1, " : "") + "email = ?, email2 = ?, iCal1 = ?, iCal2 = ? WHERE username = ?" );
            pstmt.clearParameters();
            pstmt.setString(1, email);
            pstmt.setString(2, email2);
            pstmt.setInt(3, iCal1);
            pstmt.setInt(4, iCal2);
            pstmt.setString(5, user);

         } else {
        
            if (!email.equals( "" )) {    // if one provided and ok

               pstmt = con.prepareStatement( "UPDATE member2b SET " +  (iCal1 == 1 ? "emailOpt = 1, " : "") + "email = ?, iCal1 = ?, iCal2 = 0 WHERE username = ?" );
               pstmt.clearParameters();
               pstmt.setString(1, email);
               pstmt.setInt(2, iCal1);
               pstmt.setString(3, user);

            } else {
                
               pstmt = con.prepareStatement( "UPDATE member2b SET " +  (iCal2 == 1 ? "emailOpt = 1, " : "") + "email2 = ?, iCal2 = ?, iCal1 = 0 WHERE username = ?" );
               pstmt.clearParameters();
               pstmt.setString(1, email2);
               pstmt.setInt(2, iCal2);
               pstmt.setString(3, user);
            }
         }
           
         pstmt.executeUpdate();
         pstmt.close();
            
      } catch (Exception exp) {
      
         SystemUtils.buildDatabaseErrMsg("DB error in Login.processEmail.", exp.toString(), out, true);
         replyMsg = "There was a problem adding your email address to the system.<br>You will now be directed to ForeTees.";
      
      } finally {

          if (pstmt != null) {
             try {
                pstmt.close();
             } catch (SQLException ignored) {}
          }
       }

   } else {

       // both email address were empty - user just skipped past and did not enter an address
       // so lets clear their iCal fields and set them to zero so we don't hit on this again during their subsequent login
       try {

           pstmt = con.prepareStatement( "UPDATE member2b SET iCal1 = 0, iCal2 = 0 WHERE username = ?" );
           pstmt.clearParameters();
           pstmt.setString(1, user);
           pstmt.executeUpdate();
           pstmt.close();

       } catch (Exception exp) {
           
           SystemUtils.buildDatabaseErrMsg("DB error #2 in Login.processEmail.", exp.toString(), out, true);
           replyMsg = "There was a problem clearing your iCal flag.<br>You will now be directed to ForeTees.";

       } finally {

          try { pstmt.close();
          } catch (SQLException ignore) {}

       }
   }

   //
   //   Output continue page
   //
   if (new_skin) {
      
      String refresh_url = "";

      if (message.equals( "" )) {      // if no message to display
         if (club.equals("wingedfoot")) {
            refresh_url = base_url + "Member_searchpast?subtee=gquota"; // wfmember_welcome.htm
         } else {
            if (sess_activity_id == dining_activity_id) {     // if dining
               refresh_url = base_url + "Dining_home";
            } else {
               refresh_url = base_url + "Member_announce";
            }
         }
      } else {
         refresh_url = base_url + "Member_msg";
      }

      title = "Member Email Update";
      
      Common_skin.outputHeader(club, sess_activity_id, title, true, out, req, 2, refresh_url);     // output the page start
      
      out.println("<body>");
      out.println("<div id=\"wrapper_login\" align=\"center\">");
      out.println("<div id=\"title\">" +clubName+ "</div>");
      out.println("<div id=\"main_login\" align=\"center\">");
      out.println("<h1>Email Update</h1>");
      out.println("<div class=\"main_message\">");
      out.println("<h2>Thank you.</h2><br /><br />");
      out.println("<center><div class=\"sub_instructions\">");

      out.println("<BR>" +replyMsg );      
      out.println("<br /></div>");

      if (message.equals( "" )) {      // if no message to display
         out.println("<form method=\"get\" action=\"" + base_url + "Member_announce\">");
      } else {
         out.println("<form method=\"get\" action=\"" + base_url + "Member_msg\">");
      }
      
      out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\">");
      out.println("</form></center></div></div>");
        
      Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       
      
      
   } else {

      // OLD SKIN

      out.println("<HTML><HEAD><Title>Member Login Page 2</Title>");

      if (message.equals( "" )) {      // if no message to display
         out.println("<meta http-equiv=\"Refresh\" content=\"" +pause+ "; url=/" + rev + "/member_welcome.htm\">");
      } else {
         out.println("<meta http-equiv=\"Refresh\" content=\"" +pause+ "; url=" + base_url + "Member_msg\">");
      }
      out.println("</HEAD>");
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H2>Thank You!</H2><BR>");
      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
      out.println("<font size=\"3\">");
      out.println("<BR>" +replyMsg );

      out.println("<br><br>");
      out.println("</font></td></tr></table>");

      if (message.equals( "" )) {      // if no message to display
         out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
      } else {
         out.println("<form method=\"get\" action=\"Member_msg\">");
      }
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
   }
   
   out.close();
   
   return;
 }

 
 
 // ***************************************************************************
 //  Prompt iPad user to see if they want to use Mobile or Standard Interface
 // ***************************************************************************
 private void promptIpad(String club, String name, boolean new_skin, int activity_id, PrintWriter out, HttpServletRequest req) {
          

     String base_url = Utilities.getBaseUrl(req, activity_id, club, 0);
     /*
    String base_url = "../" + club + "_golf/";
    if (activity_id == dining_activity_id) {
        base_url = "../" + club + "_dining/";
    } else if (activity_id > 0) {
        base_url = "../" + club + "_flxrez" + activity_id + "/";
    }
      * 
      */
    if (!new_skin) {
        base_url = "";
    }
     
    out.println(SystemUtils.HeadTitleMobile("ForeTees Mobile Login Prompt"));
    out.println("" +
        "<div id=\"header\">" +
        "<div id=\"logo\">" +
        "<div id=\"logout\"> </div>" +
        "<div id=\"home\">Welcome</div>" +
        "</div>" +
        "</div>\n");
    out.println("<div class=\"headertext\">Welcome " + name + "</div>");
    
    out.println("<div class=\"smheadertext\">");        
    
        out.println("We have detected that you are using a mobile device to access ForeTees.");
        out.println("<br />Our standard site supports mobile devices,<br />however it may be faster and easier<br />to use our Mobile site.");
        out.println("<br /><br />If you do not require the full suite of features, then we recommend our Mobile site.");
        out.println("<br /><br />");
        
    out.println("</div><div class=\"content\"><ul>");
        out.println("<li><a href=\"" + base_url + "Member_msg?useMobile\">Go To Mobile Site (Golf only). . . </a></li>");                
        out.println("<li><a href=\"" + base_url + "Member_msg?useStandard\">Go To Standard Site . . . . . . . </a></li>");                
        out.println("</ul>");
        
    out.println("</div><div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)
    out.println("</body></html>");

  }
 
 
 
 
 // ***************************************************************************
 //  Mobile User logged in - prompt based on activities available
 // ***************************************************************************
 public static void promptMobileUser(String club, String name, HttpSession session, PrintWriter out, Connection con) {
  
     
    Statement stmt2 = null;   
    ResultSet rs = null;
    
    int count = 0;
    int total = 0;
    int i = 0;
    int foretees_mode = 0;    
    int dining_mode = 0;
    int flxrez_staging = 0;
    int dining_staging = 0;
    
    //
    //  Check for activities supported at this club
    //
    try {

        // Get foretees_mode, dining_mode and # of activities from database
        stmt2 = con.createStatement();
        rs = stmt2.executeQuery("SELECT foretees_mode, organization_id, flxrez_staging, dining_staging FROM club5 WHERE clubName <> '';");

        if (rs.next()) {
            foretees_mode = rs.getInt(1) > 0 ? 1 : 0;
            dining_mode = rs.getInt(2) > 0 ? 1 : 0;
            flxrez_staging = rs.getInt(3) > 0 ? 1 : 0;
            dining_staging = rs.getInt(4) > 0 ? 1 : 0;
        }

        // only query activities is club is not is staging mode
        if (flxrez_staging == 0) {

            rs = stmt2.executeQuery(""
                    + "SELECT activity_id "
                    + "FROM activities "
                    + "WHERE parent_id = 0 AND enabled != 0");

            if (rs.next()) {

                rs.last();
                count = rs.getRow();   // get the number of activities found (excluding golf)
            }

        }

    } catch (Exception exc) {

        Utilities.logError("Login.promptMobileUser: Error loading activity count. club=" + club + ", err=" + exc.toString());

    } finally {

        try {
            rs.close();
        } catch (Exception ignore) {
        }

        try {
            stmt2.close();
        } catch (Exception ignore) {
        }
    }

    // count will be zero if flxrez staging is enabled
    total = foretees_mode + dining_mode + count;  // (dining_mode == 1 && dining_staging == 1)

    String[] activity_names = new String[count];
    int[] activity_ids = new int[count];

    if (count > 0) {

        try {

            // Get activity names from database 
            stmt2 = con.createStatement();
            rs = stmt2.executeQuery(""
                    + "SELECT activity_name, activity_id "
                    + "FROM activities "
                    + "WHERE parent_id = 0 AND enabled != 0 "
                    + "ORDER BY sort_by, activity_name");

            while (rs.next()) {

                activity_names[i] = rs.getString("activity_name");
                activity_ids[i] = rs.getInt("activity_id");
                i++;

            }

        } catch (Exception exc) {

            Utilities.logError("Login.promptMobileUser: Error loading activity names. club=" + club + ", err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                stmt2.close();
            } catch (Exception ignore) {
            }
        }
    }
        
    // *** TEMP ***********
    if (!club.equals("philcricket") && !club.equals("interlachen") && !club.equals("denvercc") && !club.equals("demov4")) {      // remove this later for all clubs

        count = 0;           // not Philly Cricket - Golf Only for Mobile (until we make this live)
        dining_mode = 0;
    }         // end of TEMP test for Philly Cricket
    

    //  Process according to the number and type of activities available

    if (foretees_mode == 1 && count == 0 && (dining_mode == 0 || (dining_mode == 1 && dining_staging == 1))) {   // if Golf ONLY
    
        // Golf Only - flash a quick message and route to Mobile Site

        out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>Member Welcome Page</Title>");
        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_mobile_home\">");
        out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
        out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
        out.println("<BODY><CENTER>");
        out.println("<div class=\"headertext\">Login Accepted<BR><BR>Welcome " + name + "</div>");
        out.println("<BR><BR>");
        out.println("<form method=\"get\" action=\"Member_mobile_home\">");
        out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</CENTER></BODY></HTML>");
    
    } else {      // more than just golf 

        //  Start the welcome page

        out.println(SystemUtils.HeadTitleMobile("ForeTees Member Welcome"));
        out.println("" +
            "<div id=\"header\">" +
            "<div id=\"logo\">" +
            "<div id=\"logout\"><a href=\"Logout\">Log Out</a></div>" +
            "<div id=\"home\">Home</div>" +
            "</div>" +
            "</div>\n");
        out.println("<div class=\"headertext\">Welcome " + name + "</div>");
        out.println("<div class=\"smheadertext\">How would you like to proceed?</div>");        
        out.println("<div class=\"content\"><ul>");
        
        if (foretees_mode == 1) {

            out.println("<li><a href=\"Login?mobile=yes&reroute=yes&activity=0\">Golf . . . . . . . . . . . . . . . . . .</a></li>");                
        }

        if (dining_mode == 1 && dining_staging == 0) {   // if Dining and ready for members

            out.println("<li><a href=\"Login?mobile=no&reroute=yes&activity=" +dining_activity_id+ "\">Dining . . . . . . . . . . . . . . . . .</a></li>");
        }

        //  Now add the FlxRez activities, if any

        if (count > 0) {

            for (i=0; i< count; i++) {

                if (!club.equals("denvercc") || activity_ids[i] != 3) {
                    out.println("<li><a href=\"Login?mobile=no&reroute=yes&activity=" +activity_ids[i]+ "\">" +activity_names[i]+ " . . . . . . . . . . .</a></li>");                
                }
            }   
        }

        out.println("</ul></div>");
        out.println("<div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)
        out.println("</body></html>");
    }

    out.close();
     
 }    // end of promptMobileUser
 
 
 
 // ***************************************************************************
 //  Mobile User - wants to route to a ForeTees Activity (from promptMobileUser via doGet)
 // ***************************************************************************
 private void mobileFlexRoute(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
  
     
   HttpSession session = null;
           
   session = SystemUtils.verifyMem(req, out);             // check for intruder 
   
   if (session == null) return;          // exit if error detected (msg already displayed)
   
   String activityURL = "Member_announce";    // default destination
   String mobile = "no";
   
   if (req.getParameter("mobile") != null) {

      mobile = req.getParameter("mobile");
   }   
   
   if (mobile.equalsIgnoreCase("no")) {
       
       session.setAttribute("mobile", 0);  // clear mobile flag if we are going to full site 
   }

   int activity_id = Integer.parseInt(req.getParameter("activity"));  // get the activity requested
     
   session.setAttribute("activity_id", activity_id);  // set the selected activity so we are routed to it
   
   if (activity_id == 0 && mobile.equalsIgnoreCase("yes")) {      // if Golf and Mobile site selected
       
       activityURL = "Member_mobile_home";
       
   } else if (activity_id == dining_activity_id) {     // if Dining
       
       activityURL = "Dining_home";  
   }
   
   // flash a quick message and route to FlxRez
   
   out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>Member Welcome Page</Title>");
   out.println("<meta http-equiv=\"Refresh\" content=\"0; url=" +activityURL+ "\">");
   out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
   out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><div class=\"headertext\">Thank you.  We will now route you to your desired activity.");
   out.println("<BR><BR></div>");
   out.println("<form method=\"get\" action=\"" +activityURL+ "\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");
   out.println("</CENTER></BODY></HTML>");
   out.close();    
     
 }    // end of mobileFlexRoute
 
 
 // ***************************************************************************
 //  New Skin - check if user is on IE6 browser.  If so, ask them to upgrade or switch to old skin
 // ***************************************************************************
 private boolean checkOldBrowser(String club, String name, PrintWriter out, HttpServletRequest req) {
          
    //
    //  Get the browser info from this request
    //
    String userAgent = req.getHeader("User-Agent");
    
    //SystemUtils.logError("*** TEST *** Login - check browser. User-Agent = " +userAgent);            // TEMP

    boolean oldBrowser = false;
    
    if (userAgent.indexOf("MSIE 6") > 0) {     // IE 6 found anywhere in the string ?
        
        oldBrowser = true;                     // yes, indicate old browser found
        
        //
        //  We found that some browsers will report 2 versions of IE, so we need to check for a newer version also in the string
        //
        //  Sample: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1); 
        //           Embedded Web Browser from: http://bsalsa.com/; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; 
        //           .NET4.0C; BRI/2; BOIE8;ENUSMSCOM)
        //
        String data = "";
        StringTokenizer tok2 = null;
        StringTokenizer tok = new StringTokenizer( userAgent, ";" );     // use semi-colon

        loop1:
        while ( tok.countTokens() > 0 ) {              // check all tokens for MSIE x

            data = tok.nextToken();

            if (data.startsWith(" MSIE")) {
                
                tok2 = new StringTokenizer( data, " " );     // use space
                
                if ( tok2.countTokens() == 2 ) {             // should be MSIE x.x

                    data = tok2.nextToken();                // get MSIE
                    data = tok2.nextToken();                // get version
                    
                    if (!data.startsWith("6")) {            // if NOT IE 6
                        
                        oldBrowser = false;       
                        break loop1;
                    }
                }
            }
        }           // end of WHILE tokens in UA
    }               // end of IF MSIE 6 found
    
    if (oldBrowser) {

        out.println("<HTML><HEAD><Title>Unsupported Browser Detected</Title>");
        out.println("</HEAD>");
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><H2>Login Accepted</H2><BR>");
        out.println("<table width=\"70%\" border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
        out.println("<font size=\"3\">");
        out.println("<BR>Welcome <b>" + name + "</b><BR><BR>");
        out.println("<b>NOTICE:</b>&nbsp; We have detected that you are using an older browser that is not supported by the new version of ForeTees.");
        out.println("<BR><BR>We suggest that you use a different browser, or upgrade your current browser in order to ensure full functionality.");
        out.println("<BR>Microsoft's Internet Explorer 8 is significantly faster and supports the newer web technologies.");
        out.println("<BR><BR>");

        // add link to Microsoft's IE Upgrade site
        
        out.println("<A HREF=\"http://www.microsoft.com/en-us/download/ie.aspx?q=internet+explorer&WT.mc_id=MSCOM_EN_US_DLC_ICONNAV_121LSUS007796\">Click Here to Upgrade Your Browser</A>");

        out.println("<BR><BR>");
        
        out.println("<b>NOTICE:</b>&nbsp; The ForeTees Old Version has been discontinued as of 2/1/2013.");
        out.println("<BR>Please update your browser or hardware in order to access the ForeTees system.");
        out.println("<BR>We apologize for any inconveience this may cause.");

        out.println("</font></td></tr></table>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
    }
    
    return(oldBrowser);     // on return:  if true, set session to Old Skin and go away !!!!!!!!!!!!!!!!!!

  }
 
 
 // *********************************************************
 // Increment the login counts in the Vx login_stats table
 // *********************************************************
 private void recordLoginStat(int user_type_id) {
     
    Connection con = null;
    Statement stmt = null;
    
    int server_id = Common_Server.SERVER_ID;            
    
    try {
        
        con = dbConn.Connect(rev);
        stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO login_stats (entry_date, hour, node, user_type_id, login_count) VALUES (now(), DATE_FORMAT(now(), \"%H\"), \"" + server_id + "\", \"" + user_type_id + "\", 1) ON DUPLICATE KEY UPDATE login_count = login_count + 1");
        
    } catch (Exception ignore) {
        
    } finally {
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
        
        if (con != null) {
           try {
              con.close();
           } catch (SQLException ignored) {}
        }
    }
    
    con = null;
    stmt = null;
    
 }
 
 
 // *********************************************************
 // Record the detils of this login for record keeping
 // *********************************************************
 public static void recordLogin(String user, String pass, String club, String ip, int success) {

    Connection con = null;
    PreparedStatement pstmt = null;
    
    try {
        
        con = dbConn.Connect(rev);
        pstmt = con.prepareStatement ("" +
                "INSERT INTO logins (club, username, password, ip, node, success, datetime) " +
                "VALUES (?, ?, ?, ?, ?, ?, now());");
        pstmt.clearParameters();
        pstmt.setString(1, club);
        pstmt.setString(2, user);
        pstmt.setString(3, pass);
        pstmt.setString(4, ip);
        pstmt.setInt(5, Common_Server.SERVER_ID);
        pstmt.setInt(6, success);
           
        pstmt.executeUpdate();
        
    } catch (Exception e) {
        
        SystemUtils.logError(e.getMessage());
        
    } finally {
        
        if (pstmt != null) {
           try {
              pstmt.close();
           } catch (SQLException ignored) {}
        }
        
        if (con != null) {
           try {
              con.close();
           } catch (SQLException ignored) {}
        }
    }
    
    pstmt = null;
    con = null;
    
 }
 
 
 //
 //  Proshop user wants to disable a login message (from getMessageBoard)
 //
 private static void doLoginMsg(HttpServletRequest req, PrintWriter out) {
     
    PreparedStatement pstmt = null;
    
    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) {

       return;
    }

    String club = (String)session.getAttribute("club");   // get club name
    String user = (String)session.getAttribute("user");
    int activity_id = (Integer)session.getAttribute("activity_id");
    int mobile = (Integer)session.getAttribute("mobile");
   
    Connection con = SystemUtils.getCon(session);

    if (con == null) {

       return;
    }

    String message = "";
    
   //
   // Get the name of the message to disable 
   //
   if (req.getParameter("login_message") != null) {

      message = req.getParameter("login_message");
   }
   
   if (!message.equals("")) {
      
      try {
      
         pstmt = con.prepareStatement (
            "UPDATE login2 SET message = ? WHERE username = ?");

         pstmt.clearParameters();          
         pstmt.setString(1, message);       // message to disable
         pstmt.setString(2, user);          // username 
         pstmt.executeUpdate();      
        
      } catch (Exception e) {

      } finally {

         if (pstmt != null) {
            try {
               pstmt.close();
            } catch (SQLException ignored) {}
         }

      }
   }
   
   out.println("<html><head><title>Proshop Login Page</title>");
   out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" + rev + "/" + ((mobile > 0) ? "proshop_mobile_home.htm" : "proshop_welcome.htm") + "\">");
   out.println("</head>");
   out.println("<body bgcolor=\"white\"><center><img src=\"/" +rev+ "/images/foretees.gif\"><br>");
   out.println("<hr width=\"40%\">");
   out.println("<p>&nbsp;</p>");
   out.println("<h2>Login Accepted</h2><br>");
   out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\"><p>");
   out.println("Welcome <b>Proshop</b>");
   out.println("</p></td></tr></table><br>");
   out.println("<br><br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" + rev + "/proshop_welcome.htm\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></body></html>");
   out.close();
    
 }    // end of doLoginMsg
 
 
 private static void systemTest(HttpServletRequest req, PrintWriter out) {
     
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    int pcount = 0;
    
    StringBuilder result = new StringBuilder();
    
    boolean monitor_result = false;
    
    try {
        
        con = dbConn.Connect("demov4");
        stmt = con.createStatement();
        rs = stmt.executeQuery("SHOW PROCESSLIST"); // SHOW STATUS LIKE 'Threads_connected'
        while ( rs.next() ) {
            
            //if ( rs.getString("Host").startsWith(tmp) ) pcount++;
            pcount++;
        }
        
    } catch (Exception exc) { 

        pcount = -1;

    } finally {

         try { rs.close(); }
         catch (Exception ignored) {}

         try { stmt.close(); }
         catch (Exception ignored) {}
      
         try { con.close(); }
         catch (Exception ignored) {}
    }
    
    result.append("N" + Common_Server.SERVER_ID + ":" + ((pcount < 1) ? "DB-FAIL" : "DB"+pcount));
    
    monitor_result = (pcount > 0);

    boolean nfs_failed = false; // default result fail


    // Check Roster File NFS
    if (!checkFile("/home/rosters/tmp/test.txt")) nfs_failed = true;

    // Check Announcement Page NFS
    if (!nfs_failed && !checkFile("/mnt/announce/announce_test.txt")) nfs_failed = true;

    // Check AEImages NFS
    if (!nfs_failed && !checkFile("/mnt/AEimages/AEimages_test.txt")) nfs_failed = true;


    // output the result
    if (req.getParameter("amsu") != null) {

        if (monitor_result && !nfs_failed) {

            out.println("OK");

        } else {

            out.println("FAIL");

        }

    } else {

        result.append(":" + ((!nfs_failed) ? "NFS" : "NFS-FAIL"));
        out.print(result.toString());
        out.println("");

    }

 }


 private static boolean checkFile(String filepath) {


    File f = null;
    FileReader fr = null;
    BufferedReader br = null;
    boolean result = false;

    if (filepath == null || filepath.equals("")) {

        filepath = "/home/rosters/tmp/test.txt";
    }

    try {

        f = new File(filepath);
        fr = new FileReader(f);
        br = new BufferedReader(fr);

        if (f.isFile()) result = true;

    } catch (Exception ignore) {

    } finally {

      if (br != null) {
         try {
            br.close();
         } catch (Exception ignored) {}
      }

      if (fr != null) {
         try {
            fr.close();
         } catch (Exception ignored) {}
      }

      if (f != null) f = null;

    }

    return result;
 }

 
 //
 //  Mobile count method - bump counter and gather mobile device data
 //
 private static void countMobile(int mobile_count, int mobile_iphone, String user, HttpServletRequest req, Connection con) {
     
 
   PreparedStatement stmt = null;
   
   boolean iphone = false;
 
 
   //
   //  Gather the User Agent String from the request header
   //
   String ua = req.getHeader("user-agent").toLowerCase();

   if (ua.indexOf("iphone") > -1 || ua.indexOf("ipod") > -1) {

	// found an iphone or ipod
      iphone = true;

   } else if(ua.indexOf("ipad") > -1) {                   // checks for future stats !!!!!!!!!!!

	// found an iPad

   } else if(ua.indexOf("android") > -1) {

	// found an android device

   } else if(ua.indexOf("blackberry") > -1) {

	// found a blackberry device

   } else if(ua.indexOf("opera mini") > -1) {

	// found opera mini browser

   } else if(ua.indexOf("windows ce") > -1 || ua.indexOf("smartphone") > -1 || ua.indexOf("iemobile") > -1) {

	// found windows mobile device
   }

   
   
   //
   //   Increment the mobile counter for this member and update the account
   //
   mobile_count++;
   
   if (iphone == true) mobile_iphone++;     // bump iphone counter if its an iPhone
 
   try {
 
      stmt = con.prepareStatement (
         "UPDATE member2b SET mobile_count = ?, mobile_iphone = ? WHERE username = ?");

      stmt.clearParameters();          
      stmt.setInt(1, mobile_count);     // new mobile count  
      stmt.setInt(2, mobile_iphone);    // new iphone count  
      stmt.setString(3, user);          // username 
      stmt.executeUpdate();
            
   } catch (Exception ignore) { 
        
   } finally {

     if (stmt != null) {
        try {
           stmt.close();
        } catch (SQLException ignored) {}
     }      
   }
            
 }       // end of countMobile
 

 
 //
 //  Build a custom message for proshop users - announcements, etc.
 //
 private static String getMessageBoard(int today_date, String club, int activity_id, String last_message, Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     String message = "";

     //
     //  Check if we should display a message (refer to doLoginMsg to see how it gets stopped)
     //
     if (today_date < 20140301 && !last_message.equals("newfeature1")) {     // Premier Promotion

         message = "<style type=\"text/css\">"
                 + "<!--"
                 + ".style2 {font-family: Georgia, 'Times New Roman', Times, serif}"
                 + ".style5 {font-size: 21px}"
                 + "-->"
                 + "</style>"
                  + "<table cellpadding=0 cellspacing=0 border=0 bgcolor=\"#f1f1f1\" width=\"760\"><tr><td>"
                  + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:20px; text-align:center;\">"
                  + "<BR><i><strong>New Features From ForeTees</strong></i></div>"
                  + "<div style=\"width:700px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:left;\">"
                  + "<i><strong>Recurring Events</strong></i></div>"
                  + "<div style=\"width:700px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                  + "We have made it much easier to configure an event that occurs multiple times during the season (a Recurring Event).&nbsp; To take advantage of this new feature, "
                  + "do as follows:<br><br>"
                  + "&nbsp;&nbsp;&nbsp;&nbsp;1.&nbsp; Create the first instance of the event and verify that it is built as desired.<br>"
                  + "&nbsp;&nbsp;&nbsp;&nbsp;2.&nbsp; Go to System Config - Event Setup - Recur an Existing Event.<br>"
                  + "&nbsp;&nbsp;&nbsp;&nbsp;3.&nbsp; Locate and select the event that you wish to recur, then hit Continue.<br>"
                  + "&nbsp;&nbsp;&nbsp;&nbsp;4.&nbsp; Select the date(s) to recur the event and complete the remainder of the form.<br>"
                  + "&nbsp;&nbsp;&nbsp;&nbsp;5.&nbsp; Hit Continue to create the additional events.&nbsp; Be sure to verify that they were built correctly.<br>"
                  + "</div>"
                  + "<div style=\"width:700px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:left;\">"
                  + "<br><i><strong>New 'Sent Email History' Report</strong></i></div>"
                  + "<div style=\"width:700px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                  + "You can now view information on email messages that have been sent using the Send Email feature over the past 60 days.&nbsp; The system will track all email "
                  + "messages that are sent to members by the staff.&nbsp; After sending an email you will be able to view a summary of the email in a history log.&nbsp; To access more "
                  + "detailed information, including the recipients and the message itself, simply click on the email.&nbsp; To take advantage of this new feature, "
                  + "go to <strong>Tools - Email - Sent Email History</strong>."
                  + "<BR><BR><strong>Note:</strong>&nbsp; You can see the above information any time by selecting the New Features link from the ForeTees navigation panel."
                  + "</div>"
                  + "<div style=\"width:700px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:left;\">"
                  + "<br><i><strong>Reminder</strong></i></div>"
                  + "<div style=\"width:700px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                 + "Please share the following video with your GM and/or Website Administrator:"
                 + "<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;http://vimeo.com/user12200989/review/85393067/8a286d8f68"
                 + "<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"http://vimeo.com/user12200989/review/85393067/8a286d8f68\" target=\"_blank\">View ForeTees Premier Video</a>"
                  + "<br><br>"
                  + "Thank you for using ForeTees!</span></p>"
                  + "<p style=\"margin-bottom: 0;\"> <span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">Please "
                  + "contact ForeTees Support at <a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a> if you need assistance.</span>"
                  + "</div>"
                  + "</td></tr></table>"
                 + "<br><form method=\"post\" action=\"/" + rev + "/servlet/Login\">"
                 + "<input type=hidden name=\"login_message\" value=\"newfeature1\">"           //  MESSAGE NAME to STOP IT
                 + "<input type=\"submit\" value=\"Continue & Stop This Message\" style=\"text-decoration:underline; background:#8B8970\">"
                 + "</form>";
        
        
        
     /*
     } else if (today_date < 20110126) {       // PGA Show Message?

         message = "<style type=\"text/css\">"
                 + "<!--"
                 + ".style2 {font-family: Georgia, 'Times New Roman', Times, serif}"
                 + ".style5 {font-size: 21px}"
                 + "-->"
                 + "</style>"
                 + "<table cellpadding=0 cellspacing=0 border=0 bgcolor=\"#f1f1f1\" width=\"700\"><tr><td>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:center;\">"
                 + "<BR><i>2011 User Conference at the PGA Show</i></div>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                 + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">Again this year we will be hosting a <strong>User Conference at the PGA  Show</strong> "
                 + "in Orlando.&nbsp;&nbsp;This 90+ minute  presentation will be invaluable as we demonstrate our newest features and answer any questions you may have about our industry-leading "
                 + "system to ensure that you are using it to its full potential.&nbsp;&nbsp;Hosted by Support Specialist, Ben Haubach,  and V.P. of Marketing, Jock Olson, this conference received rave "
                 + "reviews last year."
                 + "<BR><BR>"
                 + "It will take place on <strong>Friday, January 28<sup>th</sup>, from 1:15 to 3:00 p.m</strong>.&nbsp;&nbsp;The location will be in <strong>Room W109.&nbsp;&nbsp; "
                 + "</strong>This is in the lower level of the  convention center, outside of the Equipment Section of the show."
                 + "<BR><BR>"
                 + "When you arrive be sure to <strong>register for the iPad that we will be giving away</strong> at the end of the show.&nbsp;&nbsp;The iPad is a great way to access "
                 + "ForeTees while on the course, range or first tee."
                 + "</span></p>"
                 + "<p style=\"margin-bottom: 0;\"> <span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">Reservations for this  conference are preferred due to limited space.&nbsp; Please "
                 + "contact Jock Olson at <a href=\"mailto:jock@foretees.com\">jock@foretees.com</a> to guarantee your spot.</span>"
                 + "<br><br>"
                 + "<i>Hope to see you there!</i>"
                 + "<BR><BR></p>"
                 + "</div>"
                 + "</td></tr></table>";

     } else if (today_date < 20130520 && activity_id == 0) {             // Memorial Day Message for golf shop

         message = "<style type=\"text/css\">"
                 + "<!--"
                 + ".style2 {font-family: Georgia, 'Times New Roman', Times, serif}"
                 + ".style5 {font-size: 21px}"
                 + "-->"
                 + "</style>"
                 + "<table cellpadding=0 cellspacing=0 border=0 bgcolor=\"#f1f1f1\" width=\"700\"><tr><td>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:center;\">"
                 + "<BR><i>Memorial Day is Approaching!</i></div>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                 + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                 + "Now is the time to get your tee sheets ready. Be sure to check your restrictions and blockers for Monday, May 27th."
                 + "</span></p>"
                 + "<p style=\"margin-bottom: 0;\"> <span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">Please "
                 + "contact ForeTees Support at <a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a> if you need assistance.</span>"
                 + "<br><br>"
                 + "<i>Have a great Memorial Weekend!</i>"
                 + "<BR><BR></p>"
                 + "</div>"
                 + "</td></tr></table>";

     } else if (today_date < 20120722 && activity_id == 0) {

         boolean isGhin = Utilities.isHandicapSysConfigured(activity_id, con);

         if (isGhin) {
             message = "<style type=\"text/css\">"
                     + "<!--"
                     + ".style2 {font-family: Georgia, 'Times New Roman', Times, serif}"
                     + ".style5 {font-size: 21px}"
                     + "-->"
                     + "</style>"
                     + "<table cellpadding=0 cellspacing=0 border=0 bgcolor=\"#f1f1f1\" width=\"700\"><tr><td>"
                     + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:center;\">"
                     + "<BR><i><strong>New 'No-Post' Feature for the Handicap Interface</strong></i></div>"
                     + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                     + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                     + "We have added a new feature for the Handicap Interface that allows you to indicate when a member round should NOT require a score posting. "
                     + "&nbsp;Any round with this indicator will not be counted in the scores posted vs rounds played report.<br><br>"
                     + "You should notice a new column on the tee sheet pages (current and old sheets) on the far right side with a heading of NP (No-Post). "
                     + "&nbsp;In this column you will find an image similar to the check-in box. &nbsp;Click on this image to set or clear the No-Post indicators for "
                     + "the members in the tee time. Hold your mouse over the image for details. &nbsp;You can also set/clear the indicators for individual members when updating a tee time. "
                     + "&nbsp;Look for the NP check boxes to the right of the player boxes.</p>"
                     + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                     + "<BR><BR>Thank you for using ForeTees!</span></p>"
                     + "<p style=\"margin-bottom: 0;\"> <span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">Please "
                     + "contact ForeTees Support at <a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a> if you need assistance.</span>"
                     + "<BR><BR></p>"
                     + "</div>"
                     + "</td></tr></table>";
         }


     } else if (today_date < 20130930) {       // new feature notice

         message = "<style type=\"text/css\">"
                 + "<!--"
                 + ".style2 {font-family: Georgia, 'Times New Roman', Times, serif}"
                 + ".style5 {font-size: 21px}"
                 + "-->"
                 + "</style>"
                 + "<table cellpadding=0 cellspacing=0 border=0 bgcolor=\"#f1f1f1\" width=\"700\"><tr><td>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:center;\">"
                 + "<BR><i><strong>Handicap Posting Report Enhancement</strong></i></div>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                 + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                 + "We have enhanced some of the Score Posting Reports, making it much easier to send emails to those members "
                 + "that may have failed to post scores. &nbsp;When viewing these reports you will notice an 'email' link attached "
                 + "to the entries with members that have not posted. &nbsp;This link will open the email tool with that member's "
                 + "email address preloaded, so all you have to do is enter the message. &nbsp;You will also notice a link on the report "
                 + "that will allow you to send an email to all members in the report that have not posted a score. "
                 + "<BR><BR>These enhancements have also been added to the score posting reports available to your member(s) "
                 + "that have been designated as a Handicap Chair. &nbsp;We have also added a new score posting report for the "
                 + "handicap chair. &nbsp;If you have a member designated as a Handicap Chair, then you can use the Member View "
                 + "feature defined below to view the handicap reports available to them. &nbsp;We hope you will find these enhancements useful and efficient."
                 + "</p></div><div style=\"width:600px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:center;\">"
                 + "<BR><i><strong>New 'Member View' Feature</strong></i></div>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                 + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                 + "We have added a new feature that will allow you to access the ForeTees Member portal directly from the Proshop User's navigation.<BR><BR>"
                 + "Selecting the new <strong>'Member View'</strong> link in the navigation frame at the top of the next page (directly above the System Config tab) will prompt you "
                 + "to select the classification of member you would like to emulate. &nbsp;You will then access ForeTees in a new window just as that member would, seeing just "
                 + "what he or she would see. &nbsp;This can be very useful for verifying restrictions, events, member notices, etc.<BR><BR>"
                 + "<strong>PLEASE NOTE:</strong> &nbsp;You will NOT be logged in as a real member and therefore will not be allowed to book reservations."
                 + "</p><p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                 + "<BR>Thank you for using ForeTees!</span></p>"
                 + "<p style=\"margin-bottom: 0;\"> <span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">Please "
                 + "contact ForeTees Support at <a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a> if you need assistance.</span>"
                 + "<BR><BR>This message will expire on 9/30/2013.</p><BR>"
                 + "</div>"
                 + "</td></tr></table>";

     } else if (today_date < 20131028 && activity_id == 0) {       // new feature notice

         int hidenames = 0;

         try {

             stmt = con.createStatement();

             rs = stmt.executeQuery("SELECT hidenames FROM club5");

             if (rs.next()) {
                 hidenames = rs.getInt("hidenames");
             }

         } catch (Exception exc) {
             Utilities.logError("Login.getMessageBoard - " + club + " - Error looking up hidenames option - ERR: " + exc.toString());
         } finally {

             try { rs.close(); } 
             catch (Exception ignore) {}

             try { stmt.close(); } 
             catch (Exception ignore) {}
         }

         message = "<style type=\"text/css\">"
                 + "<!--"
                 + ".style2 {font-family: Georgia, 'Times New Roman', Times, serif}"
                 + ".style5 {font-size: 21px}"
                 + "-->"
                 + "</style>"
                 + "<table cellpadding=0 cellspacing=0 border=0 bgcolor=\"#f1f1f1\" width=\"700\"><tr><td>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 30px 17px 30px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:18px; text-align:center;\">"
                 + "<BR><span style=\"font-weight:bold; font-style:italic;\">New 'Make Tee Time Private' Feature</span></div>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                 + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                 + "We have added a new feature that will allow Proshop Users to hide details from the member view of the tee sheet for specific tee times (members will not have access to these options).<BR><BR>"
                 + "Selecting one of the new <span style=\"font-weight:bold\">'Make tee time private'</span> options when booking or modifying a tee time, found beneath the \'Suppress Email Notifications\' "
                 + "option, will allow you to hide either the player names, or the tee time as a whole, from the member view of the tee sheet for that tee time only.<br><br>"
                 + "The following options are available:<br><br>"
                 + "<img style=\"display:block; margin-left:auto; margin-right:auto;\" src=\"http://www1.foretees.com/v5/images/make_tee_time_private_1.png\">"
                 + "<ul>"
                 + "<li><span style=\"font-weight:bold;\">No</span>: This tee time will display normally on the member view (default)</li>"
                 + "<li><span style=\"font-weight:bold;\">Hide Names</span>: Member and Guest names for this tee time will be displayed as \"Member\" and \"Guest\" on the member view</li>"
                 + "<li><span style=\"font-weight:bold;\">Hide Tee Time</span>: This tee time will not be displayed on the member view</li>"
                 + "</ul>";
         
         if (hidenames > 0) {
             message += "<span style=\"font-weight:bold; color:red;\">*Notice*</span>: Since your club currently has the <span style=\"font-weight:bold\">'Hide Member Names'</span> option enabled in Club Options, "
                     + "the <span style=\"font-weight:bold\">\'No\'</span> option described above will not be present when booking a tee time, and instead the <span style=\"font-weight:bold\">'Hide Names'</span> "
                     + "option will be selected by default. The <span style=\"font-weight:bold\">'Hide Tee Time'</span> option can then be used as desired.<br><br>";
         }
         
         message += "The option selected will be reflected in the history entries for that tee time (member bookings/modifications will display \"N/A\").</p>"
                 + "<p style=\"margin-bottom: 0;\"> <span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">Please "
                 + "contact ForeTees Support at <a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a> if you need assistance.</span>"
                 + "<BR><BR>This message will expire on 10/28/2013.</p><BR>"
                 + "</div>"
                 + "</td></tr></table>";
         
     } else if (today_date < 20131118) {       // new feature notice

         message = "<style type=\"text/css\">"
                 + "<!--"
                 + ".style2 {font-family: Georgia, 'Times New Roman', Times, serif}"
                 + ".style5 {font-size: 21px}"
                 + "-->"
                 + "</style>"
                 + "<table cellpadding=0 cellspacing=0 border=0 bgcolor=\"#f1f1f1\" width=\"700\"><tr><td>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                 + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                 + "<br>At approximately 9:00 PM CST on Sunday, Nov 17th we will be updating the ForeTees software on our servers.  "
                 + "This update will provide some important changes to the backend of the system, which are required for some "
                 + "significant improvements to our mobile support that will be coming in the near future.  "
                 + "<br><br>You and your members should not notice any changes to the system, however there is always the chance "
                 + "of an unexpected problem when the software is changed.  If you experience a problem, or any members report a "
                 + "problem, please contact our pro support at <a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a> or 651-765-6006."
                 + "<br><br>Thank you."
                 + "</div>"
                 + "</td></tr></table>";

     } else if (today_date < 20131205) {       // new feature notice

         message = "<style type=\"text/css\">"
                 + "<!--"
                 + ".style2 {font-family: Georgia, 'Times New Roman', Times, serif}"
                 + ".style5 {font-size: 21px}"
                 + "-->"
                 + "</style>"
                 + "<table cellpadding=0 cellspacing=0 border=0 bgcolor=\"#f1f1f1\" width=\"700\"><tr><td>"
                 + "<div style=\"width:600px; background-color:#f1f1f1; padding:0px 50px 0px 50px; font-family:Georgia, Times New Roman, Times, serif; color:#333333; font-size:14px; text-align:left;\">"
                 + "<p style=\"margin-top: 0\"><span style=\"font-family:Georgia, 'Times New Roman', Times, serif;\">"
                 + "<br>At approximately 10:00 PM CST on Wednesday, Dec 4th we will be updating the ForeTees software.  "
                 + "<br><br>In order to comply with email messaging standards we will be adding some <strong>new email options for your members</strong>.&nbsp; "
                 + "With these new options your members will be able to opt in or out "
                 + "of the following emails:<br><ul>"
                 + "<li>Notifications received to confirm a reservation, event signup, lesson, etc.</li>"
                 + "<li>Communications received from the club staff (when you send emails to your membership via ForeTees).</li>"
                 + "<li>Communications received from other members (when members send emails via ForeTees).</li></ul>"
                 + "Members will be allowed to select these options under the Settings tab in ForeTees.&nbsp; "
                 + "The following screenshot illustrates the new settings (as seen by the members):<br><br>"
                 + "<img style=\"display:block; margin-left:auto; margin-right:auto;\" src=\"http://www.foretees.com/images/member_email_opts.png\">"
                 + "<br>Additionally, there will be an 'unsubscribe' link included at the bottom of all emails sent through ForeTees to the members.&nbsp;  "
                 + "By clicking this link, members will be automatically logged into ForeTees and allowed to change these settings.  "
                 + "<br><br><Strong>What this means for you:</strong><br><br>"
                 + "If you have been maintaining one or more distribution lists as a way to control which members receive club communications, you will now "
                 + "have the ability to let your members control this themselves.&nbsp;  Simply point them to the Settings tab in ForeTees or suggest that they click "
                 + "the 'unsubscribe' link at the bottom of any email they receive through ForeTees.&nbsp;  Once a member opts out of an email type they will no "
                 + "longer receive those emails at the email address(es) they unchecked.<br><br>"
                 + "When you select members to add to a distribution list or to add as recipients for an email message, you will only see members in the selection "
                 + "list that have at least one valid email address and they have checked the 'Receive Club Communications' option under that email address."
                 + "<br><br>We hope these new settings will benefit you and your members."
                 + "<br><br>If you have any questions or experience any problems, or any members report a "
                 + "problem, please contact our pro support at <a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a> or 651-765-6006."
                 + "<br><br>Thank you.<br>"
                 + "</div>"
                 + "</td></tr></table>";

         */
     }

     return (message);

 }       // end of getMessageBoard


 
 // ***************************************************************
 //  checkEmployee
 //
 //      Get the memebr's subtype and see if it is 'Employee'.
 //      This is for testing the new skin.
 //
 // ***************************************************************

 /*
 private boolean checkEmployee(String club, String user, Connection con) {

    PreparedStatement stmt = null;
    ResultSet rs = null;
    String subtype = "";
    boolean employee = false;

    try {

        stmt = con.prepareStatement("SELECT msub_type FROM member2b WHERE username = ?");
        stmt.clearParameters();
        stmt.setString(1, user);        
        rs = stmt.executeQuery();       // execute the prepared stmt
        
        if (rs.next()) {
            
            subtype = rs.getString(1);
        }
        stmt.close();
        
    } catch (Exception ignore) {
        
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }
    
    if (subtype.equals("Employee")) employee = true;
    
    return (employee);
 }
 * 
 */
 
 
 // ***************************************************************
 //  checkConstruction
 //
 //        Check to see if this club's site is under construction
 //        (we are working on it).  If so, display a message.
 //
 // ***************************************************************

 private boolean checkConstruction(String club, String caller, int activity_id, PrintWriter out, Connection con, HttpServletRequest req) {

    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    int userlock = 0;
    boolean construction = false;

    try {

        stmt = con.prepareStatement("SELECT userlock FROM club5");
        stmt.clearParameters();
        rs = stmt.executeQuery();       // execute the prepared stmt
        
        if (rs.next()) {
            
            userlock = rs.getInt("userlock");
        }
        stmt.close();
        
    } catch (Exception ignore) {
        
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }
    
    if (userlock > 0) {      // if users locked out
        
        construction = true;     // inform caller
        
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club
        String title = "ForeTees Alert";
      
        Common_skin.outputHeader(club, activity_id, title, true, out, req);     // output the page start
    
        out.println("<body>");
        out.println("<div id=\"wrapper_login\" align=\"center\">");
        out.println("<div id=\"title\">" +clubName+ "</div>");
        out.println("<div id=\"main_login\" align=\"center\">");
        out.println("<h1>Under Construction</h1>");
        out.println("<div class=\"main_message\">");
        out.println("<h2>Sorry, this site is currently being updated.</h2><br /><br />");
        out.println("<center><div class=\"sub_instructions\">");
        out.println("Please try again later.  Thank you for your patience.");
        out.println("<br /></div>");
        if (!caller.equals("")) {
            out.println("<form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");           
        } else {
            out.println("<form action=\"javascript:history.back(1)\" method=\"get\"><input type=\"submit\" value=\"Return\" id=\"submit\"/></form>");
        }
        out.println("</center></div></div>");
        
        Common_skin.outputPageEnd(club, activity_id, out, req);    // finish the page       

        out.close();
    }
    
    return (construction);
 }
 

 // ***************************************************************
 //  checkMemberInact
 //
 //        Check to see if members are being blocked on this site.
 //
 // ***************************************************************

 private boolean checkMemberInact(String club, String caller, int activity_id, PrintWriter out, Connection con, HttpServletRequest req) {

    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    int memberInact = 0;
    boolean blocked = false;

    try {

        stmt = con.prepareStatement("SELECT memberInact FROM club5");
        stmt.clearParameters();
        rs = stmt.executeQuery();       // execute the prepared stmt
        
        if (rs.next()) {
            
            memberInact = rs.getInt("memberInact");
        }
        stmt.close();
        
    } catch (Exception ignore) {
        
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }
    
    if (memberInact > 0) {      // if members locked out
        
        blocked = true;     // inform caller
        
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club
        String title = "ForeTees Alert";
      
        Common_skin.outputHeader(club, activity_id, title, true, out, req);     // output the page start
    
        out.println("<body>");
        out.println("<div id=\"wrapper_login\" align=\"center\">");
        out.println("<div id=\"title\">" +clubName+ "</div>");
        out.println("<div id=\"main_login\" align=\"center\">");
        out.println("<h2>Access Not Allowed</h2>");
        out.println("<div class=\"main_message\">");
        out.println("<h3>Sorry, this site is not ready for member access.</h3><br /><br />");
        out.println("<center><div class=\"sub_instructions\">");
        out.println("Please try again later or check with your club staff.");
        out.println("<br /></div>");
        if (!caller.equals("")) {
            out.println("<form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");           
        } else {
            out.println("<form action=\"javascript:history.back(1)\" method=\"get\"><input type=\"submit\" value=\"Return\" id=\"submit\"/></form>");
        }
        out.println("</center></div></div>");
        
        Common_skin.outputPageEnd(club, activity_id, out, req);    // finish the page       

        out.close();
    }
    
    return (blocked);
 }                        // end of checkMemberInact
 
 
 
                
 // ***************************************************************
 //  verifyLastName
 //
 //        Check to see if the last name provided matches any of the members with the provided member number.
 //
 //        lname = last name as provided by caller
 //        lastName = last name in our member record (from query in remoteUser above)
 //
 // ***************************************************************

 private boolean verifyLastName(String club, String caller, String lname, String lastName, String mNum, Connection con) {

    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    boolean matchFound = false;
 
    if (lname.equalsIgnoreCase(lastName)) {        // if the 2 names match - its good
       
       matchFound = true;
       
    } else {
       
       //
       //  Get the last name of each family member and check to see if it matches the name provided by the caller (Clubster)
       //
       try {

           stmt = con.prepareStatement("SELECT name_last FROM member2b WHERE memNum = ?");
           stmt.clearParameters();
           stmt.setString(1, mNum);
           rs = stmt.executeQuery();    

           while (rs.next() && matchFound == false) {   // check until we find a match or run out of family members

               lastName = rs.getString("name_last");
               
               matchFound = checkLastName(club, caller, lname, lastName);                
           }
           stmt.close();

       } catch (Exception ignore) {

       } finally {

           if (rs != null) {
               try {
                  rs.close();
               } catch (SQLException ignored) {}
           }

           if (stmt != null) {
               try {
                  stmt.close();
               } catch (SQLException ignored) {}
           }
       }
    }
 
    return (matchFound);
 }                        // end of verifyLastName
 
 
 //  method to check names for a match - called by verifyLastName above
 
 private boolean checkLastName(String club, String caller, String lname, String lastName) {

    boolean matchFound = false;
    
    String shortName1 = lname;         // caller's name
    String shortName2 = lastName;      // FT name
 
    if (lname.equalsIgnoreCase(lastName)) {        // if the 2 names match - its good
       
       matchFound = true;
       
    } else {
       
       // Parse the names in case one or both contain a space, hyphen, underscore or other separators
       
       StringTokenizer tok1 = new StringTokenizer( lname, "-_:;()~ /'" );         // parse caller's name on special chars and space

       if ( tok1.countTokens() > 1 ) {              // if a separator was found

          shortName1 = tok1.nextToken();            // get the first part
       }
       
       StringTokenizer tok2 = new StringTokenizer( lastName, "-_~ /" );        // parse FT name on common special chars (spaces will not exist in FT db)

       if ( tok2.countTokens() > 1 ) {              // if a separator was found

          shortName2 = tok2.nextToken();            // get the first part
       }
       
       if (shortName1.equalsIgnoreCase(shortName2)) {
          
          matchFound = true;                      // all good
          
       } else if ( tok1.countTokens() > 0 ) {          // if another token exists for Caller's name
          
          shortName1 = shortName1 + tok1.nextToken();  // combine them without the special character or space
                   
          if (shortName1.equalsIgnoreCase(shortName2) || shortName1.startsWith(shortName2)) {   // should be enough characters now to see if they are similar

             matchFound = true;                      // all good
          }
       }
    }
 
    return (matchFound);
 }                        // end of checkName
 
 
 
 
 // *********************************************************
 // Process all calls from FlexWeb website.
 // *********************************************************

 // NOT USED!!!
 
 private void flexWebSSO(HttpServletRequest req,
                 PrintWriter out, String user, String club) {

   Connection con = null;                  // init DB objects
   ResultSet rs = null;
   PreparedStatement stmt = null;

   Member member = new Member();

   String lname = "";
   String username = user;
   String mship = "";
   String mtype = "";
   String wc = "";
   String zipcode = "";
   String mNum = "";
   String logMsg = "";
   String mapping = "No";
   String stripZero = "No";
   String errMsg = "";
   String eventName = "";
   String courseName = "";
   String caller = "";
   String seamless_caller = "";               // caller value in club5
   String clubName = "";

   int rsynci = 0;                      // values from club5 table for this club
   int seamless = 0;
   int primaryif = 0;
   int mnumi = 0;
   int mappingi = 0;
   int stripzeroi = 0;
   int signUp = 0;
   int stripAlpha = 0;
   int stripAlphaNum = 0;               // Used to signify that alpha-numeric strings ending a member number should be stripped (C1, C2, C3, etc).
   int stripDash = 0;
   int stripSpace = 0;

   int default_activity_id = 0;         // inticator for default activity (0=golf)
   int event_activity_id = 0;           // activity id for event, if event name is passed (0=golf)
   int organization_id = 0;

   int mobile = 0;                     // mobile user indicator
   int mobile_count = 0;
   int mobile_iphone = 0;

   boolean rsync = false;
   boolean stripEnd = false;
   boolean fatalError = false;
   boolean allowMobile = false;
   boolean mobileApp = false;

   //boolean new_skin = true;       // always for this


   caller = req.getParameter("caller");         // should be FLEXWEBFT (checked above)

   //
   //  See if request is for event signup - users can select event from their calendar!!
   //
   //   NOTICE:  The name of the event MUST match our event name exactly!!!!!
   //
   if (req.getParameter("eventname") != null) {

      eventName = req.getParameter("eventname");      // event name must match our name exactly!!!!!
   }

   if (eventName == null) eventName = "";


    //
    //  Make sure the club requested is currently running this version of ForeTees.
    //  The user may need to refresh the login page so they pull up the new page.
    //
    try {

        con = dbConn.Connect(ProcessConstants.REV);       // get a connection for this version level

        //
        //  Make sure club exists and is active in our system
        //
        stmt = con.prepareStatement (
                "SELECT fullname FROM clubs WHERE clubname = ? WHERE inactive = 0");

        stmt.clearParameters();
        stmt.setString(1, club);
        rs = stmt.executeQuery();

        if (!rs.next()) {          // if club not found in this version

           errMsg = "Error in Login.remoteUser - club name invalid, club=" +club+ " was received from web site.";
           fatalError = true;
        }

    } catch (Exception exc) {

        // Error connecting to db....
        errMsg = "Error in Login.remoteUser - Unable to Connect to Database.  Could be an invalid clubname (club=" +club+ "). Error: " + exc.toString();
        fatalError = true;

    } finally {

        try { stmt.close(); }
        catch (Exception ignore) {}

    }


   if (fatalError == false) {

      // get a connection to the club's db
      try {

         con = dbConn.Connect(club);

      } catch (Exception exc) {

         errMsg = "Login.remoteUser - Unable to Connect to club: " +club+ ", User = " +user+ ". Error: " + exc.toString();
         fatalError = true;
      }

      //
      //   Check if club is under construction and exit if it is
      //
      if (!fatalError && checkConstruction(club, caller, default_activity_id, out, con, req)) return;

   }


   //
   //  If any of above failed, return error message to user
   //
   if (fatalError == true) {

      out.println(SystemUtils.HeadTitle("Invalid Login"));
      out.println("<BODY><CENTER><img src=\"/" + ProcessConstants.REV + "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>System Error</H2><BR>");
      out.println("<BR>Sorry, we cannot proceed due to an internal problem.");
      out.println("<BR>Exception: " + errMsg + "<BR>");
      out.println("<BR>Please try again later and if problem persists please contact ForeTees and provide this information.  Thank you.<BR>");
      out.println("</CENTER></BODY></HTML>");
      out.close();

      // log error in v5 error log table
      Utilities.logError(errMsg);

      if (con != null) {

         try { con.close(); }
         catch (Exception ignore) {}

      }

      return;
   }


    organization_id = Utilities.getOrganizationId(con);


    if (req.getParameter("mobile") != null) {        // mobile ???

        mobileApp = true;
    }

    //
    //  Check if Mobile is allowed (for messages)
    //
    allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages?


    //
    //  if from MYCLUB, then set mobile user since all users from MyClub are mobile
    //
    //if ( caller.equals( "MYCLUB" ) || caller.equals( "MFMOBILE" ) || CEmobile || mobileApp) {
    if (mobileApp) {

        mobile = 1;            // ?????????????????????
    }


    //
    //    MyClub ?????????????
    //
    //if (!caller.equals( "MYCLUB" ) && !mobileApp) { // skip the settings if coming from a Mobile App since the club can have a web interface and an interface with MyClub.

    if (!mobileApp) { // skip the settings if coming from a Mobile App since the club can have a web interface

      //
      //****************************************************
      //  Get the I/F options from club5 (set via support)
      //****************************************************
      //
      try {

         stmt = con.prepareStatement (
                  "SELECT rsync, seamless, zipcode, primaryif, mnum, mapping, stripzero, seamless_caller, stripalpha, stripdash " +
                  "FROM club5");

         stmt.clearParameters();        // clear the parms
         rs = stmt.executeQuery();

         if (rs.next()) {          // get club options

            rsynci = rs.getInt(1);
            seamless = rs.getInt(2);
            zipcode = rs.getString(3);
            primaryif = rs.getInt(4);
            mnumi = rs.getInt(5);
            mappingi = rs.getInt(6);
            stripzeroi = rs.getInt(7);
            seamless_caller = rs.getString("seamless_caller");       // Caller value saved for this club
            stripAlpha = rs.getInt("stripalpha");
            stripDash = rs.getInt("stripdash");
         }

      } catch (Exception exc) {

         invalidRemote(true, default_activity_id, "Unable to Connect to Club Database for options. Error: " + exc.getMessage(), req, out, con);
         return;

      } finally {

        try { stmt.close(); }
        catch (Exception ignore) {}

      }

    } // end if not mobileApp


    //
    //  Set parms based on club options
    //
    /*
    if (primaryif == 1) {

        primary = "Yes";
    }

    if (mnumi == 1) {

        mNumParm = "Yes";
    }
    *
    */                             // not used for now

    if (mappingi == 1) {

        mapping = "Yes";
    }

    if (stripzeroi == 1) {

        stripZero = "Yes";
    }

    if (rsynci == 1) {

        rsync = true;
    }


   //
   //    Event processing - add this !!!!!!!!!
   //
   /*
   if (!eventName.equals("")) {         // if event specified, verify name and get course name for event

      try {

         if (req.getParameter("activity") != null && req.getParameter("activity").equals(String.valueOf(dining_activity_id))) {  // Dining event

             event_activity_id = dining_activity_id;
             courseName = "";

         } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none") || eventName.equalsIgnoreCase("reservations") || eventName.equalsIgnoreCase("calendar") || eventName.equalsIgnoreCase("lessons")) {

             if (req.getParameter("activity") != null && !req.getParameter("activity").equals("") && !req.getParameter("activity").equals("0")
                     && !req.getParameter("activity").equals(String.valueOf(dining_activity_id))) {
                 try {
                     event_activity_id = Integer.parseInt(req.getParameter("activity"));
                 } catch (Exception exc) {
                     event_activity_id = 0;
                 }
             } else {
                 event_activity_id = 0;
             }

             courseName = "";
             signUp = 0;

         } else {

             stmt = con.prepareStatement (
                "SELECT activity_id, courseName, signUp " +
                "FROM events2b WHERE name = ?");

             stmt.clearParameters();
             stmt.setString(1, eventName);
             rs = stmt.executeQuery();

             if (rs.next()) {

                event_activity_id = rs.getInt("activity_id");   // activity id for this event
                courseName = rs.getString("courseName");        // get course name for Member_events2
                signUp = rs.getInt("signUp");                   // signUp indicator for members

             } else {           // not found - must be invalid name from MF

                eventName = "";           // remove it, direct member to announce page instead
             }

             stmt.close();

             if (signUp == 0) {           // if members are not allowed to register for this event

                eventName = "";           // remove it, direct member to announce page instead
             }
         }
      }
      catch (Exception exc) {

         eventName = "";           // remove it, direct member to announce page instead
      }
   }
   *
   */


    //
    //  Get club's POS Type for _slot processing
    //
    String posType = getPOS(con);

    //
    //  Get TLT indicator
    //
    int tlt = (getTLT(con)) ? 1 : 0;

    //
    //  Strip leading zeros in user requested it (NEW option)
    //
    if (stripZero.equalsIgnoreCase( "yes" )) {

        if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
        }
    }

    String remote_ip = req.getHeader("x-forwarded-for");
    if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();


    //
    // use a prepared statement to find username (string) in the DB..
    //
    PreparedStatement pstmt = null;
    boolean close_con = false;
    try {

        String stmtString = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                            "memNum, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone " +
                            "FROM member2b " +
                            "WHERE inact = 0 AND billable = 1 AND flexid = ?";
        
        pstmt = con.prepareStatement (stmtString);

        // Get user's pw if there is a matching user...
        pstmt.clearParameters();
        pstmt.setString(1, username);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            if (mapping.equalsIgnoreCase( "yes" )) {    // map username to web id for match ?

                username = rs.getString("username");
            }

            // Build the member's full name from its parts
            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));

            if (!rs.getString("name_mi").equals( omit )) {
                mem_name.append(" ");
                mem_name.append(rs.getString("name_mi"));
            }
            mem_name.append(" " + rs.getString("name_last"));

            String name = mem_name.toString();


            // Get the member's membership type
            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type

            //
            // if 'activity' is being passed and its not an Event Signup request, then use the activity being requested
            //
            if (req.getParameter("activity") != null && eventName.equals("")) {

                int temp_activity_id = 0;

                try {
                    temp_activity_id = Integer.parseInt(req.getParameter("activity"));
                }
                catch (Exception exc) {
                    temp_activity_id = 0;
                }

                default_activity_id = temp_activity_id;

            } else {

                if (!eventName.equals("")) {
                    default_activity_id = event_activity_id;      // if user going to an event signup, then use that activity id (from the event)
                } else {
                    default_activity_id = rs.getInt("default_activity_id");
                }
            }

            


            // Get the number of visits and update it...
            int count = rs.getInt("count");         // Get count
            count++;                                // bump counter..

            //  Get wc and last message displayed at login
            wc = rs.getString("wc");                            // w/c pref
            String message = rs.getString("message");       // message
            mNum = rs.getString("memNum");                      // member #
            mobile_count = rs.getInt("mobile_count");     // get # of mobile logins for this ueer
            mobile_iphone = rs.getInt("mobile_iphone");     // get # of iphone logins for this ueer

            pstmt.close();   //done with statement - close it

            message = "";    // for now


            //
            //  Trace good logins - display parms passed for verification purposes
            //
            logMsg = "Remote Login Successful: user_name from website=" +user+ ", Primary=No, mNum=No, Mapping=" +mapping+ ", IP=" + remote_ip + " ";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it

            recordLogin(user, "", club, remote_ip, 1);

            // Save the connection in the session block for later use.......
            HttpSession session = Utilities.getNewSession(req);   // Create a session object

            ConnHolder holder = new ConnHolder(con);      // create a new holder from ConnHolder class

            session.setAttribute("connect", holder);      // save DB connection holder
            session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
            session.setAttribute("user", username);       // save username
            session.setAttribute("name", name);           // save members full name
            session.setAttribute("club", club);           // save club name
            session.setAttribute("caller", caller);       // save caller's name
            session.setAttribute("mship", mship);         // save member's mship type
            session.setAttribute("mtype", mtype);         // save member's mtype
            session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
            session.setAttribute("posType", posType);     // save club's POS Type
            session.setAttribute("zipcode", zipcode);     // save club's zipcode
            session.setAttribute("tlt", tlt);             // timeless tees indicator
            session.setAttribute("mobile", mobile);       // set mobile indicator (0 = NOT, 1 = Mobile)
            session.setAttribute("activity_id", default_activity_id);  // activity indicator
            session.setAttribute("organization_id", organization_id);  // organization_id (set if using ForeTeesDining system)
            session.setAttribute("new_skin", "1");        // new skin flag

            //
            // set inactivity timer for this session
            //
            session.setMaxInactiveInterval( MEMBER_TIMEOUT );
            
            // build our base url string
            String base_url = Utilities.getBaseUrl(req, default_activity_id, club);
            /*
            String base_url = "../" + club + "_golf/"; // default
            if (default_activity_id == dining_activity_id) {

                base_url = "../" + club + "_dining/";

            } else if (default_activity_id > 0) {

                base_url = "../" + club + "_flxrez" + default_activity_id + "/";
                
            }
             * 
             */


            //
            //  Count the number of users logged in
            //
            countLogin("mem", con);

            // new stats logging routine
            recordLoginStat(2);

            if (mobile > 0) {

               countMobile(mobile_count, mobile_iphone, username, req, con);      // bump mobile counter and track mobile device
            }

            //
            //   Check for iPad (or like) device and prompt for standard or mobile access.
            //
            boolean enableAdvAssist = Utilities.enableAdvAssist(req);     // check for iPad (or like) device - false indicates iPad

            if (enableAdvAssist == false && allowMobile == true && default_activity_id == 0 && mobile == 0) {     // if Golf, iPad and mobile ok for this site, and user not already detected as mobile

               promptIpad(club, name, true, default_activity_id, out, req);     // prompt user for mobile or standard interface

               countMobile(mobile_count, mobile_iphone, username, req, con);    // bump mobile counter and track mobile device (this was not done above)
               return;
            }

            //
            //  Output the response and route to system
            //
            if (mobile == 0) {

                String refresh_url = "";

                if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)

                    if (event_activity_id == dining_activity_id) {

                        int event_id = 0;

                        // Check to make sure a valid event_id was passed as the eventName parameter.  If not, route them to the event listing instead.
                        try {
                            event_id = Integer.parseInt(eventName);
                        } catch (Exception exc) {
                            event_id = 0;
                        }

                        if (eventName.equalsIgnoreCase("reservations")) {
                            refresh_url = base_url + "Dining_slot?action=new";
                        } else if (eventName.equalsIgnoreCase("calendar")) {
                            refresh_url = base_url + "Member_teelist";
                        } else {
                            // eventName should contain a numerical event id
                            refresh_url = base_url + "Dining_home?view_events&event_id=" + eventName;
                        }

                    } else {

                        if (eventName.equalsIgnoreCase("reservations")) {
                            if (event_activity_id != 0 && event_activity_id != dining_activity_id) {
                                refresh_url = base_url + "Member_gensheets";
                            } else {
                                refresh_url = base_url + "Member_select";
                            }
                        } else if (eventName.equalsIgnoreCase("lessons")) {
                            refresh_url = base_url + "Member_lesson";
                        } else if (eventName.equalsIgnoreCase("calendar")) {
                            refresh_url = base_url + "Member_teelist";
                        } else if (eventName.equals("clubessential") || eventName.equalsIgnoreCase("none")) {
                            refresh_url = base_url + "Member_events";
                        } else {
                            refresh_url = base_url + "Member_events?name=" +eventName+ "&course=" +courseName; // was Member_events2
                        }
                    }

                } else {    // no event

                    if (message.equals( "" )) {      // if no message to display

                        refresh_url = base_url + "Member_announce";

                    } else {

                        refresh_url = base_url + "Member_msg";
                    }
                }

                // new skin (no meta refresh - use js instead)
                Common_skin.outputHeader(club, default_activity_id, "Member Login Page", true, out, req, 0, refresh_url);


                out.println("<h1>Login Accepted</h1>");

                out.println("<h2 style=\"padding:10px\">Welcome " + ((count > 1) ? "back " : "") + name + ".</h2>");

                out.close();

            } else {

               //
               //  Mobile Device - go to mobile menu page
               //
               out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>Member Welcome Page</Title>");
               out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/mobile/member_mobile_home.html\">");
               out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
               out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
               out.println("<BODY><CENTER>");
               out.println("<BR>Welcome " + name );                                           // Member's Name
               out.println("<BR><BR></div>");
               out.println("<form method=\"get\" action=\"/" + rev + "/mobile/member_mobile_home.html\">");
               out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
            }

        } else {

            //
            // USER NOT FOUND
            //
            
            // trace all failed login attempts
            logMsg = "Remote Login Failed - Invalid User (msg#1) IP=" + remote_ip + " ";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it twice to get all info

            // trace additional information and output error reply
            logMsg = "Invalid Username Received. User Id " + username + " does not exist in the ForeTees roster.";
            invalidRemote(true, default_activity_id, logMsg, req, out, con);

            recordLogin(user, "", club, remote_ip, 0);

            close_con = true;

        } // end of if username found

    } catch (SQLException exc) {

        errMsg = "Unexpected Error. Exception: " +exc.getMessage();

        invalidRemote(true, default_activity_id, errMsg, req, out, con);
        recordLogin(user, "", club, remote_ip, 0);
        close_con = true;

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

        // if the login process failed then lets close the con
        if (close_con) {
            try { con.close(); }
            catch (Exception ignore) {}
        }
    }

 } // end of flexWebUser

}

