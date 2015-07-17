
/***************************************************************************************
 *   Common_skin:  This servlet will build portions of the member pages (new skin).
 *
 *
 *
 *   created:  5/03/2011   
 *
 *   last updated:       ******* keep this accurate *******
 * 
 *         3/12/14  Wee Burn CC (weeburn) - Added custom to display the "Lessons" menu tab as "Indoor Golf Studio" (case 2384).
 *         3/11/14  La Grange CC (lagrangecc) - Hide the "Handicaps" menu from the member side.
 *         3/05/14  Add a custom for Kopplin and Kuebler (Greg DeRosa) to use the lesson book for booking interview meetings.
 *         3/04/14  Interlachen CC (interlachen) - Allow Events and Lessons tabs for "Junior Pool" activity, and change the text for both.
 *         2/25/14  Add a Home tab and some minor changes for Connect sites.
 *         1/29/14  GAA of Orlando (gaaorlando) - No longer hide the 'Settings' menu (members) (case 2352).
 *         1/29/14  GAA of Orlando (gaaorlando) - Also display the 'My Calendar' menu (members) (case 2352).
 *         1/17/14  GAA of Orlando (gaaorlando) - Only show the 'Events', 'Lessons', and 'Email' menus for students (members) (case 2352).
 *         1/13/14  CC of North Carolina (ccofnc) - Removed custom to hide menu tabs, since they are now live with Golf.
 *        12/23/13  Mirabel (mirabel) - Hide the email menu from members.
 *        12/03/13  Remove custom that skipped the Mobile Help link for Tonto Verde.
 *        11/19/13  Belle Meade CC (bellemeadecc) - Do not display the Lesson member for members.
 *        10/17/13  Quechee Club Tennis (quecheeclubtennis) - Added custom to limit the top nav for FlxRez activities based on msub-type (set by roster sync).
 *         9/30/13  Large changes to navigation markup generation and script/css include generation.  Added HttpServletRequest object to every output method.  Removed deprecated output methods.
 *         9/20/13  Get member sub-type from session.
 *         9/19/13  Added Handicap Report for Scores Posted to member's Handicap menu if member is the Handicap Chair.
 *         9/17/13  Updated jquery.foreTeesSlot.js and jquery.foreTeesModal.js filenames to jquery.foreTeesXXXX-20130917.js.
 *         9/12/13  Updated getActivityList to also take genrez_mode into consideration when determining whether to display FlxRez activity tabs in the navigation.
 *         9/10/13  Moselem Springs GC (moselemsprings) - Don't display the Email menu on the member side of the system (case 2297).
 *         8/23/13  Updated foreTeesSlot.js filename to jquery.foreTeesSlot-20130823.js.
 *         7/23/13  Move Connect Ids to ProcessConstants.
 *         7/11/13  CC of North Carolina (ccofnc) - Lesson menu tab will now display "Bios" and bring members directly to the bios page on FlxRez (tennis) (case 2280).
 *         7/11/13  Austin CC (austincountryclub) - Don't display the "Events" menu tab on the member side (case 2281).
 *         6/19/13  CC of North Carolina (ccofnc) - Applied a fix for 6/18's change, since it wasn't working properly.
 *         6/18/13  CC of North Carolina (ccofnc) - Don't display any of the subnav tabs for their club.
 *         5/30/13  If guest tracking is configured and active, the Partners menu will instead be labeled "Partners/Guests" and will be a drop-down menu with "Manage Partners" and "Manage Guests" options.
 *         5/29/13  Desert Mountain Club (desertmountain) - Added custom weather link.
 *         5/22/13  Updated foreTeesSlot.js filename to jquery.foreTeesSlot-20130522.js.
 *         5/02/13  Add initial support for CDN utilization
 *         4/30/13  Rolling Hills GC - SA (rollinghillsgc) - Hide the "Lessons" menu on the member side.
 *         4/15/13  Minikahda CC (minikahda) - Do not show the Events menu for dining.
 *         4/09/13  Hillwood CC (hillwoodcc) - Added custom to hide all menu items on Golf and Dining for "Whitworth - Athletic" members (case 2253).
 *         4/02/13  Belle Meade CC (bellemeadecc) - FlxRez "Lessons" menu will now be a clickable link that will bring members directly to the Individual Lesson pro selection. 
 *         3/28/13  Check session flag for ftConnect to see if club uses FT Connect website system.
 *                  Also, update sitewide.css (name extension).
 *         3/28/12  Belle Meade CC (bellemeadecc) - Removed custom to hide the "Lesson" menu tab from members (case 2163).
 *         3/14/13  CC of Buffalo (ccofbuffalo) - Lesson menu tab will now display "Simulator", and will take members directly to the individual lessons page for the Simulator Room pro.
 *         3/12/13  Mayfield Sand Ridge (mayfieldsr) - Don't display the "Lessons" menu on the member side of the system.
 *         3/12/13  Overlake G & CC (overlakegcc) - Don't display the "Events" menu on the member side of the system (case 2243).
 *         3/07/13  St Cloud CC (stcloudcc) - Always display the "My Calendar" tab for members.
 *         3/05/13  Increase the font size of the "Welcome Jon Doe" text in outputBanner (was 11px, now 14px).
 *         3/01/13  CC at Castle Pines (castlepines) - Added custom to hide the "Group Lessons" menu item from the member side of the system.
 *         2/27/13  Denver CC (denvercc) - Added menu boolean showCalListMenu to include the Member_teelist_list tab for some FlxRez activities.
 *         2/27/13  Denver CC (denvercc) - Updated custom so "Lessons" will be displayed again isntead of "Programs" for the Junior activity.
 *         2/27/13  Updated foreTeesSlot.js filename to jquery.foreTeesSlot-20130227.js.
 *         2/07/13  CC of York (ccyork) - Hide Handicap menu on the member side.
 *         2/04/13  Change Logout menu item to Exit if user came from a website.
 *         1/29/13  Interlachen CC (interlachen) - Hide all menu tabs for the "Juniors" activity.
 *         1/28/13  Royal Oaks CC - Dallas (roccdallas) - Hide the Lesson menu on the member side.
 *         1/17/13  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *         1/17/13  Make some changes for users coming from FT Connect Premier (Flexscape) websites.
 *         1/16/13  Merion Recip Site (merionrecip) - Added custom to hide all menus aside from Tee Times, Search, Settings, Help from member view.
 *         1/16/13  Blackhawk CC (blackhawk) - Do not display the Lessons menu to members.
 *         1/??/13  Updated outputTopNav - add Mobile Help tab.   ******** activate below - showMobileHelp ********)
 *         1/02/13  Added Proshop Handicap Report menu items for Handicap Chair members (subtype).
 *        12/27/12  Updated outputTopNav for Desert Mountain - change Lesson tab to link to 3rd party lesson book (case 2210).
 *        12/25/12  Updated foreTeesSlot.js filename to jquery.foreTeesSlot-20121225.js.
 *        12/19/12  Mesa Verde CC (mesaverdecc) - Added custom weather link for Members.
 *        12/14/12  Pinehurst CC (pinehurstcountryclub) - Do not display the option to search for other members' reservations on Tennis.
 *        12/05/12  Add custom to outputBreadCrumb to allow a club to bypass the test to skip the announcement page.
 *                  This allows the member to see the announcement page by clicking the Home link when their club opts
 *                  to bypass the announcement page when logging in.
 *        12/06/12  Denver CC (denvercc) - Display the events menu again (activity_id = 2).
 *        12/04/12  Fixed issue where the color of the club name link to the announcement page in outputBanner was not using the color defined by the selected color scheme.
 *        11/26/12  Philly Cricket Club (philcricket) - Added custom to limit the top nav for FlxRez activities based on msub-type (set by roster sync).
 *        11/19/12  Mirabel (mirabel) - Hide the option to search for other members' reservations from Tennis as well.
 *        11/14/12  The My Calendar menu can now be used for Golf and Dining.
 *        11/13/12  Mirabel (mirabel) - Show calendar menu for ALL activites, regardless of whether the Reservations menu is hidden or not.
 *        11/08/12  Updated demoroger and denver customs to display the My Calendar menu.
 *        11/08/12  Philly Cricket Club (philcricket) - Added custom to label the Lessons menu "Clinics" and have it go straight to group lessons for their only Tennis lesson pro.
 *        11/05/12  If no children activities are found for a root activity, replace the Tee Times/Reservations menu with the "My Calendar" menu item, which links members directly to the calendar page.
 *        11/03/12  Mirabel (mirabel) - Added custom to display Golf tab as "Golf Home", and the Events menu for golf as "Golf Events".
 *        10/26/12  Change outputBanner to make the club name a link to the home page.
 *        10/25/12  Add FT Connect tabs in outputSubNav for testing with democonnect only.   This is for planning purposes.
 *        10/25/12  Mirabel (mirabel) - Do not display the Reservations menu for the Spa or Fitness activities, and also hide the ability to search other member's reservations.
 *        10/17/12  demoroger - Updated menus being hidden for Juniors activity - allow the Events tab now.
 *        10/17/12  Mirabel (mirabel) - Added custom to hide the Reservations menu for the "Mbr Services" activity (id = 18).
 *        10/01/12  demoroger - Updated menus being hidden for Juniors activity.
 *        10/12/12  Mirabel - custom to replace the clubname with a message - outputBanner.
 *        10/11/12  Update outputLogo - change the FlxRez logo from a gif to a png so it can have a transparent background.
 *        10/10/12  Denver CC (denvercc) - Hide the Events menu for the Juniors activity (activity_id = 2).
 *        10/10/12  Willow Ridge CC (willowridgecc) - Hide the Email menu from members (case 2193).
 *        10/04/12  Indian Ridge CC (indianridgecc) - Hide the Lesson menu from members.
 *        10/01/12  demoroger - Updated menus being hidden for Fitness activity
 *         9/25/12  Moved the SystemUtils.getCon call in getScripts into a portion of the code that only runs if the session it's passed isn't null.
 *         9/18/12  Move the dining logo into the white portion of the page so we don't have to change colors when the background changes.
 *         9/12/12  Block all access for members in the demosandbox site.
 *         9/10/12  Add support for custom style sheet templates that will change the color scheme for specific clubs.
 *         9/07/12  Fixed issue with outputSubNav pulling and pontentially printing FlxRez tabs that are disabled.
 *         9/06/12  Denver CC (denvercc) - Added custom to replace the standard FlxRez Lessons menu with direct links labeled "Programs" or "Classes" depending on the activity.
 *         9/04/12  Colorado Springs CC (coloradospringscountryclub) - Added custom to hide the Email menu from members (case 2184).
 *         8/30/12  Added blockAllAccess boolean to outputTopNav which, when set, will remove ALL menu items for the member for the current activity.
 *         8/16/12  Tonto Verde (tontoverde) - Added custom to used "Club Events" for the Dining tab instead of "Dining".
 *         8/16/12  Added missing menu separator between "Events" and "Search" in the Dining menus.
 *         7/24/12  Denver CC (denvercc) - Added custom to hide numerous menu items for both the Juniors (id=2) and Fitness (id=3) activities.
 *         7/17/12  Belle Meade CC (bellemeadecc) - Updated the custom menus for their Tennis activity.
 *         7/16/12  Denver CC (denvercc) - Added a custom weather URL to the weather link on member side.
 *         7/02/12  Added versioning to the jquery.activity-indicator js file
 *         6/29/12  Removed html shiv for ie8 that was no longer needed; Added IE6 conditional javascript and CSS
 *         6/29/12  Monterey Peninsula CC (mpccpb) - The 'Logout' menu link will now say "Home" for members. Functionality remains the same.
 *         6/26/12  Monterey Peninsula CC (mpccpb) - Updated custom to use an alternate logo image with a white background for displaying on the member side of the system.
 *         6/21/12  Updated the date on sitewide.css (decreased the length of the select list box for course name).
 *         6/20/12  Monterey Peninsula CC (mpccpb) - Added custom to display their club logo in place of the ForeTees logo on the member side.
 *         6/20/12  Belle Meade CC (bellemeadecc) - Updated custom so that the Lesson menu tab is no longer hidden, but is a single-click button labeled "Clinics" to bring them to group lessons (case 2163).
 *         6/11/12  Added additional menu hiding booleans.
 *         6/06/12  Belle Meade CC (bellemeadecc) - Do not display the "Lesson" menu for FlxRez members (case 2163).
 *         5/16/12  Fort Collins CC (fortcollins) - Custom menu changes to display "Ball Machine" instead of the standard Lesson menu.
 *         5/16/12  Sierra View CC (sierraviewcc) - Custom menu changes to display "Ball Machine" instead of the standard Lesson menu.
 *         5/10/12  Update getScripts to get the external username value from the sessions if user is null.
 *         4/13/12  Blackstone CC (blackstone) - Added custom to hide the Email menu from members.
 *         4/05/12  Aronimink GC (aronimink) - Added custom to hide the Notifications, Email, Partners and the 'Search other members' notifications' option from the Search menu.
 *         4/05/12  Added showNotificationMenu, showEmailMenu, showPartnerMenu booleans for easier custom menu control.
 *         4/03/12  Change outputTopNav to add spacers between each menu item.
 *         4/02/12  Custom menu changes for ballantyne flxrez
 *         3/21/12  Phily Cricket Club - Added a custom weather link.
 *         3/16/12  Hide both the events and the lesson tabs for wingedfoot
 *         3/08/12  Added versioning support for key assest files
 *         2/27/12  Moved script generation from outputHeader to getScripts method
 *         2/19/12  Updates for outputHeader, outputTopNav is consolidated (outputTopNavDining now defunct) and dining is more
 *         1/24/12  Update per servlet_*.js path
 *         1/24/12  Change Dining calendar to use Memeber_teelist
 *         1/22/12  Replace 'Tee Times' with Reservations in FlxRez Search menu.
 *         1/20/12  Updated outputTopNav to only include Lesson & Handicap menus if club has them configured
 *         1/19/12  Update outputLogo for FlxRez & Dining
 *         1/14/12  Add outputError for a common error page.
 *         1/12/12  Add Notification menus
 *        12/28/11  FlxRez now uses sitewide.css
 *        12/25/11  Update outputTopNav and outputTopNavDining for use with new css files
 *        12/19/11  Update to jquery 1.7.1
 *        11/10/11  Changes for new skin
 *         8/25/11  Completed the Dining and Activity links in outputSubNav.
 *
 ***************************************************************************************
 */
import java.io.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// ForeTees imports
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;
import com.foretees.common.ProcessConstants;

public class Common_skin {

    static final String rev = ProcessConstants.REV;                               // Software Revision Level (Version)
    static final int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System
    
    //  FT Connect Ids 
    static final int ft_connect_home_id = ProcessConstants.CONNECT_ID;                    // FT Connect General (Home)
    static final int ft_connect_directory_id = ProcessConstants.CONNECT_DIRECTORY_ID;     // FT Connect Directory Tab
    static final int ft_connect_statements_id = ProcessConstants.CONNECT_STATEMENTS_ID;   // FT Connect Statements Tab
    static final int ft_connect_newsletters_id = ProcessConstants.CONNECT_NEWSLETTERS_ID; // FT Connect Newsletters Tab

    // "Desktop" scripts (non-responsive mode)
    static final String defaults_desktop_css =  "defaults_desktop.css";
    static final String navigation_desktop_css = "navigation_desktop.css";
    static final String unsorted_desktop_css =      "unsorted_desktop.css";
    static final String dining_desktop_css =        "dining_desktop.css";
    static final String flxrez_desktop_css =        "flxrez_desktop.css";
    static final String desktop_js =         "foretees-desktop.js";  // Functions only used for Desktop mode
    static final String initDesktop_js =         "init-desktop.js";  // Script initialization only used for Desktop mode
    
    // "Responsive/Mobile" scripts (responsive web design (rwd))
    static final String defaults_rwd_css =  "defaults_rwd.css";
    static final String navigation_rwd_css = "navigation_rwd.css";
    static final String buttons_rwd_css =  "buttons_rwd.css";
    static final String unsorted_rwd_css =  "unsorted_rwd.css";
    static final String dining_rwd_css =    "dining_rwd.css";
    static final String flxrez_rwd_css =    "flxrez_rwd.css";
    static final String screen_rwd_css =  "screen_rwd.css";
    
    static final String rwd_js =         "foretees-rwd.js"; // Functions only used for Responsive mode
    static final String initRwd_js =         "init-rwd.js"; // Script initialization only used for Responsive mode

    // "Global" scripts (both "Desktop" and Responsive)
    static final String premier_css =       "premier.css";
    static final String announce_css = "announce.css";
    static final String announce_editor_css = "announce_editor.css";
    static final String common_css = "common.css";
    static final String dateFormat_js =         "dateFormat.js";
    static final String global_js =         "foretees-global.js";
    static final String initGlobal_js =         "init-global.js";
    
    // Jquery / Plugins (some global, some not)
    static final String foreTeesListSearch_js = "jquery.foreTeesListSearch.js";
    static final String foreTeesSession_js = "jquery.foreTeesSession.js";
    static final String jQuerySimpleWeather_js = "jquery.simpleWeather.js";
    static final String foreTeesMemberCalendar_js = "jquery.foreTeesMemberCalendar.js";
    static final String foreTeesServerClock_js = "jquery.foreTeesServerClock.js";
    static final String foreTeesModal_js = "jquery.foreTeesModal.js";
    static final String foreTeesSlot_js = "jquery.foreTeesSlot.js";
    static final String foreTeesEmail_js = "jquery.foreTeesEmail.js";
    
    static final String jQuery_js = "jquery-1.10.2.min.js"; // jquery-1.7.1.min.js
    static final String jQueryMigrate_js = "jquery-migrate-1.2.1.min.js";
    //static final String jQueryMigrate_js = (ProcessConstants.SERVER_ID == 4) ? "jquery-migrate-1.2.1.js" : "jquery-migrate-1.2.1.min.js";
    static final String jQueryUI_js = "jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js";
    static final String json_js = "json2.js";
    //static final String jQueryLoading_js = "jquery.loading.1.6.4.js";
    //static final String jQueryLoading_css = "jquery.loading.1.6.css";
    
    //static final String jQueryFancyBox_js = "fancybox/jquery.fancybox-1.3.4.pack.js";
    //static final String jQueryFancyBox_css = "fancybox/jquery.fancybox-1.3.4.css";
    
    static final String jQueryFancyBox_js = "fancybox-2.1.5/jquery.fancybox.js";
    static final String jQueryFancyBox_css = "fancybox-2.1.5/jquery.fancybox.css";
    
    static final String jQueryActivity_js = "jquery.activity-indicator-1.0.0.js";
    static final String jQueryEasing_js = "fancybox/jquery.easing-1.3.pack.js";
    static final String jQueryCycle_js = "jquery.cycle.js";
    
    //static final String jQueryTinyMce_js = "tiny_mce-3.5.10/jquery.tinymce.js";
    //static final String tinyMce_js = "tiny_mce-3.5.10/tiny_mce.js";
    static final String jQueryTinyMce_js = "tiny_mce/jquery.tinymce.js"; // 3.5.8
    static final String tinyMce_js = "tiny_mce/tiny_mce.js";
    //static final String jQueryTinyMce_js = "tinymce-4.0.10/jquery.tinymce.min.js";
    //static final String tinyMce_js = "tinymce-4.0.10/tinymce.min.js";

    // "jquery.ui.touch-punch.min.js" is used to add touch support to some jqueryui elements. 
    // see: http://touchpunch.furf.com
    //  It will be needed until JqueryUI adds touch
    static final String jQueryUITouch_js = "jquery.ui.touch-punch.min.js";
    static final String jQueryUI_css = "jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.css"; // jquery-ui/css/smoothness/jquery-ui-1.8.16.custom.css
    
    static final String jQueryMobileConfig_js = "jquery.mobileConfig.js";
    //static final String jQueryMobile_js = "jquery.mobile-1.3.2/jquery.mobile-1.3.2.min.js";
    //static final String jQueryMobile_css = "jquery.mobile-1.3.2/jquery.mobile-1.3.2.css";
    
    //static final String jQueryPageSlide_js = "jquery.jpanelmenu-1.3.0-custom.js";
    //static final String jQueryPageSlide_js = "sidr-1.1.1/jquery.sidr.min.js";
    //static final String jQueryPageSlide_css = "sidr-1.1.1/stylesheets/jquery.sidr.dark.css";
    
    //Misc scripts
    static final String initEditor_js =         "init-editor.js";
    static final String legacy_js = "web utilities/foretees.js"; // Supports legacy function for dining, Lessons, proshop, etc.
    static final String legacy_css = "web utilities/foretees.css"; // Supports legacy css for proshop, etc.
    static final String legacy2_css = "web utilities/foretees2.css"; // Supports legacy css for proshop, etc.
    static final String proshop_css = "proshop_transitional.css";
    static final String proshop_js = "proshop_transitional.js";
    
    // Path definitions
    static final String slash = "/";
    
    public static final String basePath = (Common_Server.SERVER_ID == 11 || Common_Server.SERVER_ID == 12 || Common_Server.SERVER_ID == 13 ||
            Common_Server.SERVER_ID == 21 || Common_Server.SERVER_ID == 22 || Common_Server.SERVER_ID == 23 ||
            Common_Server.SERVER_ID == 31 || Common_Server.SERVER_ID == 32 || Common_Server.SERVER_ID == 33 ) ? "/srv/webapps" : "/usr/local/tomcat/webapps";

    static final String baseCssPath = "/assets/stylesheets";
    static final String baseJsPath = "/assets/scripts";
    static final String basejQuery = "/assets/jquery";
    
    static final String baseVersionFile = slash + rev + "/assets/script.ver";
    
    static final String cssPath = slash + rev + baseCssPath;
    
    static final String jsPath = slash + rev + baseJsPath;
    
    static final String jQueryPath = slash + rev + basejQuery;
    
    static final String coreScriptPath = slash + rev + "/assets/core";
    
    static final String attrHasBanner = "skin_hasBanner";
    static final String attrHasSubNav = "skin_hasSubNav";
    static final String attrHasMainNav = "skin_hasMainNav";
    static final String attrSubNavSize = "skin_subNavSize";
    
    static final String attrPageHeader = "skin_pageHeader";
    
    /**
     ***************************************************************************************
     *
     * getCoreCssList
     *
     * This method will return a list of "core" css files, used by external build script to build core files
     *
     * @param basePath the base file system path (used to get files modification date)
     * @param scriptMode 0 = "new skin" mode, 1 = responsive/"new mobile" mode
     * @param activity_id Activity ID
     * @param baseVersion Base version for all scripts (or null to skip versioning)
     *
     ***************************************************************************************
     */
    
    public static List<String> getCoreCssList(String basePath, int scriptMode, int activity_id, String baseVersion) {
        
        List<String> coreScripts = new ArrayList<String>();
        
        switch (scriptMode){
            
            case ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLE2: // Proshop HeadTitle2
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy2_css, baseVersion, true);
                break;
                
            case ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLEADMIN: // Proshop / Admin
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_css, baseVersion, true);
                break;
                
            case ProcessConstants.SCRIPT_MODE_PROSHOP_EDITOR_IFRAME: // Proshop_announce / tinymce editor's iframe
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, defaults_desktop_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, announce_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, announce_editor_css, baseVersion, true);
                break;
                
            case ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLEEDITOR: // Proshop / Tinymce editor
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_css, baseVersion, true);
                break;
                
            case ProcessConstants.SCRIPT_MODE_PROSHOP_TRANSITIONAL: // Proshop / Jquery, Jquery UI, transitional css/js
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryUI_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, proshop_css, baseVersion, true);
                break;
            
            case ProcessConstants.SCRIPT_MODE_NEWSKIN: // "New Skin" 
                
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryUI_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryFancyBox_css, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryLoading_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, defaults_desktop_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, navigation_desktop_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, announce_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, common_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, unsorted_desktop_css, baseVersion, true);
                
                switch (activity_id) {
                    case 0:
                        // Golf
                        break;
                        
                    case dining_activity_id:
                        // Dining
                        Utilities.addIfFileExists(coreScripts, basePath, cssPath, dining_desktop_css, baseVersion, true);
                        break;
                        
                    default:
                        // FlexRez
                        Utilities.addIfFileExists(coreScripts, basePath, cssPath, flxrez_desktop_css, baseVersion, true);
                        break;
                }
                
                break;
                
            case ProcessConstants.SCRIPT_MODE_RWD: // Responsive / New mobile (rwd)
                
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryUI_css, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryFancyBox_css, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryLoading_css, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryMobile_css, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryPageSlide_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, defaults_rwd_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, navigation_rwd_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, buttons_rwd_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, announce_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, common_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, unsorted_rwd_css, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, cssPath, screen_rwd_css, baseVersion, true);
                

                switch (activity_id) {
                    case 0:
                        // Golf
                        break;
                        
                    case dining_activity_id:
                        // Dining
                        Utilities.addIfFileExists(coreScripts, basePath, cssPath, dining_rwd_css, baseVersion, true);
                        break;
                        
                    default:
                        // FlexRez
                        Utilities.addIfFileExists(coreScripts, basePath, cssPath, flxrez_rwd_css, baseVersion, true);
                        break;
                }
                
                break;
 
        }
        
        return coreScripts;
        
    }
    
    /**
     ***************************************************************************************
     *
     * getCoreJsList
     *
     * This method will return a list of "core" js files, used by external build script to build core files
     *
     * @param basePath the base file system path (used to get files modification date)
     * @param scriptMode 0 = "new skin" mode, 1 = responsive/"new mobile" mode
     * @param baseVersion Base version for all scripts (or null to skip versioning)
     *
     ***************************************************************************************
     */
    
    public static List<String> getCoreJsList(String basePath, int scriptMode, String baseVersion) {
        
        List<String> coreScripts = new ArrayList<String>();
        
        switch (scriptMode){
            
            case ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLE2: // Proshop HeadTitle2
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQuery_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryMigrate_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_js, baseVersion, true);
                break;
                
            case ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLEADMIN: // Proshop / Admin
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_js, baseVersion, true);
                break;
                
            case ProcessConstants.SCRIPT_MODE_PROSHOP_HEADTITLEEDITOR: // Proshop HeadTitle2
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQuery_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryMigrate_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryTinyMce_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, tinyMce_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_js, baseVersion, true);
                break;
                
            case ProcessConstants.SCRIPT_MODE_PROSHOP_TRANSITIONAL: // Proshop / Jquery, Jquery UI, transitional css/js
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQuery_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryMigrate_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryUI_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, dateFormat_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, proshop_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_js, baseVersion, true);
                break;
            
            case ProcessConstants.SCRIPT_MODE_NEWSKIN: // "New Skin" 

                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQuery_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryMigrate_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, json_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, dateFormat_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryUI_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryUITouch_js, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryLoading_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryFancyBox_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryEasing_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryCycle_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryActivity_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesSession_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesListSearch_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesMemberCalendar_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesServerClock_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesModal_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesSlot_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesEmail_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, global_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, desktop_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, initGlobal_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, initDesktop_js, baseVersion, true);
                
                break;
                
            case ProcessConstants.SCRIPT_MODE_RWD: // Responsive / New mobile (rwd)
                
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQuery_js, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryMigrate_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, json_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, dateFormat_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryUI_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryUITouch_js, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryMobileConfig_js, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryMobile_js, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryPageSlide_js, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryLoading_js, baseVersion, true);
                //Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryFancyBox_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryEasing_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryCycle_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, jQueryActivity_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesSession_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesListSearch_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesMemberCalendar_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesServerClock_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesModal_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesSlot_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jQueryPath, foreTeesEmail_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, slash+rev, legacy_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, global_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, rwd_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, initGlobal_js, baseVersion, true);
                Utilities.addIfFileExists(coreScripts, basePath, jsPath, initRwd_js, baseVersion, true);
                
                break;
        }
        
        return coreScripts;
        
    }
    
    /**
     ***************************************************************************************
     *
     * getProshopScripts
     * @@ scriptMode
     *
     * This method will return string of HTML tags that pull in the Style sheets, and scripts.
     *
     ***************************************************************************************
     **/
    public static String getProshopScripts(int scriptMode) {
        String CDN = Common_Server.getContentServer();
        
        
        StringBuilder scripts = new StringBuilder();

        List<String> cssList = getProshopCSS(scriptMode, 0);
        List<String> jsList = getProshopJS(scriptMode);
        
        // CSS
        for(String script : cssList){
            scripts.append(getCssTag(script,CDN,true));
        }
        // JS
        for(String script : jsList){
            scripts.append(getJsTag(script,CDN));
        }
        
        return scripts.toString();
    }
    
    public static List<String> getProshopCSS(int scriptMode, int activity_id) {
        String baseVersion = getBaseVersion();
        return getCoreCssList(basePath, scriptMode, activity_id, baseVersion);
    }
    
    public static List<String> getProshopJS(int scriptMode) {
        String baseVersion = getBaseVersion();
        return getCoreJsList(basePath, scriptMode, baseVersion);
    }
    
    /**
     ***************************************************************************************
     *
     * getScripts
     *
     * This method will return string of HTML tags that pull in the Style sheets, and scripts.
     *
     ***************************************************************************************
     **/
    public static String getScripts(String club, int activity_id, HttpSession session, HttpServletRequest req, boolean includeEditor) {
        
        Connection con = null;

        String CDN = Common_Server.getContentServer();

        String servletFile = getServletName(req);

        String clubCssPath = slash + club + baseCssPath;
        String clubJsPath = slash + club + baseJsPath;
        String customPath = cssPath + "/custom";        // custom templates

        String caller = "";
        
        String user = "";
        
        String customStyles = "";
        
        String baseVersion = getBaseVersion();
        
        StringBuilder scripts = new StringBuilder();
        
        boolean rwd = false;
        
        boolean scriptDebug = (ProcessConstants.SERVER_ID == 4); // Default to scrip debug if on dev
 
        if(session != null) {
            
            con = SystemUtils.getCon(session);         // get DB connection to check for custom css template for this club
            
            user = (String) session.getAttribute("user");
            caller = (String)session.getAttribute("caller");
            rwd = Utilities.getSessionBoolean(session, "rwd", rwd);
            if(user != null && user.startsWith("proshop")){
                // Always turn off rwd in proshop
                rwd = false;
            }
            scriptDebug = Utilities.getSessionBoolean(session, "script_debug", scriptDebug);

            if (caller == null) {
                caller = "";
            }

            if (user == null) {                
                user = (String) session.getAttribute("ext-user");    // must be an external request so get username from external place holder
            }
            if(!user.startsWith("proshop") && !user.startsWith("admin") && req != null && req.getParameter("s_a") == null && !servletFile.equals("Login")){
                Utilities.logDebug( "JGK", "URL Context Error: club and activity not embedded in URL! Referrer=" +  req.getHeader("referer") + ", club=" + club + ", user=" + user + ", servlet=" + servletFile);
            } else if (!user.startsWith("proshop") && !user.startsWith("admin") && req == null) {
                Utilities.logDebug( "JGK","Request object not passed to output header" );
            }
        } else {
            Utilities.logDebug( "JGK","Session object not passed to output header" );
        }
        
        if(user.equals(ProcessConstants.DINING_USER)){
            activity_id = dining_activity_id;
        }
        
        if(req != null){
            // Check if we can set script debug mode from req, just incase we don't have a valid session yet (login?) 
           if(req.getParameter("s_sdbg") != null){
               scriptDebug = req.getParameter("s_sdbg").equals("on");
           }
        }
        
        int scriptMode = ProcessConstants.SCRIPT_MODE_NEWSKIN;
        if(rwd) {
            scriptMode = ProcessConstants.SCRIPT_MODE_RWD;
        }

        List<String> cssList = new ArrayList<String>();
        List<String> jsList = new ArrayList<String>();
        
        // Lists for ie7 compatibility
        List<String> ie7JsList = new ArrayList<String>();
        List<String> ie7CssList = new ArrayList<String>();
        
        Utilities.addIfFileExists(ie7JsList, basePath, jsPath, "ie7_compatibility.js", baseVersion);
        Utilities.addIfFileExists(ie7CssList, basePath, cssPath, "ie7_compatibility.css", baseVersion);
        
        // Lists for ie8 compatibility
        List<String> ie8JsList = new ArrayList<String>();
        List<String> ie8CssList = new ArrayList<String>();
        
        Utilities.addIfFileExists(ie8JsList, basePath, jsPath, "ie8_compatibility.js", baseVersion);
        Utilities.addIfFileExists(ie8CssList, basePath, cssPath, "ie8_compatibility.css", baseVersion);
        
        // If not debug mode, try to load core (compiled) scripts
        if(!scriptDebug){
            if(!rwd){
                Utilities.addIfFileExists(jsList, basePath, coreScriptPath, "core.js", baseVersion);
                switch (activity_id){
                    case 0:
                        // Golf
                        Utilities.addIfFileExists(cssList, basePath, coreScriptPath, "core.css", baseVersion);
                        break;
                        
                    case dining_activity_id:
                        // Dining
                        Utilities.addIfFileExists(cssList, basePath, coreScriptPath, "core_dining.css", baseVersion);
                        break;
                        
                    default:
                        // FlexRez
                        Utilities.addIfFileExists(cssList, basePath, coreScriptPath, "core_flexrez.css", baseVersion);
                        break;
                }
            } else {
                Utilities.addIfFileExists(jsList, basePath, coreScriptPath, "core_rwd.js", baseVersion);
                switch (activity_id){
                    case 0:
                        // Golf
                        Utilities.addIfFileExists(cssList, basePath, coreScriptPath, "core_rwd.css", baseVersion);
                        break;
                                
                    case dining_activity_id:
                        // Dining
                        Utilities.addIfFileExists(cssList, basePath, coreScriptPath, "core_dining_rwd.css", baseVersion);
                        break;
                        
                    default:
                        // FlexRez
                        Utilities.addIfFileExists(cssList, basePath, coreScriptPath, "core_flexrez_rwd.css", baseVersion);
                        break;
                }
            }
        }

        // If we did not load precompiled/minified javascript or css, load the full package
        // This would be because we're in debug mode, or because the packages were not compiled
        if(cssList.size() < 1){
            cssList = getCoreCssList(basePath, scriptMode, activity_id, baseVersion);
        }
        if(jsList.size() < 1){
            jsList = getCoreJsList(basePath, scriptMode, baseVersion);
        }
        
        if(includeEditor){
            Utilities.addIfFileExists(jsList, basePath, jQueryPath, jQueryTinyMce_js, baseVersion, true);
            Utilities.addIfFileExists(jsList, basePath, jsPath, initEditor_js, baseVersion, true);
            //jsList.add(jsPath+slash+initEditor_js);
            //jsList.add(jQueryPath+slash+jQueryTinyMce_js);
        }
        
        if(user.startsWith("proshop")){
            Utilities.addIfFileExists(cssList, basePath, cssPath, proshop_css, baseVersion, true);
        }
        
        // get servlet js/css file, if any
        Utilities.addIfFileExists(cssList, basePath, cssPath, "servlet_" + servletFile + ".css", baseVersion);
        Utilities.addIfFileExists(jsList, basePath, jsPath, "servlet_" + servletFile + ".js", baseVersion);

        // get club golf css file, if any
        Utilities.addIfFileExists(cssList, basePath, clubCssPath, "club.css", baseVersion);

        // get club servlet js/css file, if any
        Utilities.addIfFileExists(cssList, basePath, clubCssPath, "servlet_" + servletFile + ".css", baseVersion);
        Utilities.addIfFileExists(jsList, basePath, clubJsPath, "servlet_" + servletFile + ".js", baseVersion);

        // get club js file, if any
        Utilities.addIfFileExists(jsList, basePath, clubJsPath, "club.js", baseVersion);

        switch (activity_id){
            
            case 0:
                // Golf
                break;
                
            case dining_activity_id:
                // Dining

                // get dining servlet css file, if any
                Utilities.addIfFileExists(cssList, basePath, cssPath, "servlet_" + servletFile + "_dining.css", baseVersion);

                // get club servlet dinning css file, if any
                Utilities.addIfFileExists(cssList, basePath, clubCssPath, "servlet_" + servletFile + "_dining.css", baseVersion);

                // get club dining css/js file, if any
                Utilities.addIfFileExists(cssList, basePath, clubCssPath, "dining.css", baseVersion);
                Utilities.addIfFileExists(jsList, basePath, clubJsPath, "club_dining.js", baseVersion);

                // Get dining js, if any
                Utilities.addIfFileExists(jsList, basePath, jsPath, "dining.js", baseVersion);
                break;
            
            default:
                // FlexRez

                // get FlxRez servlet css files, if any
                Utilities.addIfFileExists(cssList, basePath, cssPath, "servlet_" + servletFile + "_flxrez.css", baseVersion);

                // get club servlet FlxRez css file, if any
                Utilities.addIfFileExists(cssList, basePath, clubCssPath, "servlet_" + servletFile + "_flxrez.css", baseVersion);

                // get club activity_id css file, if any
                Utilities.addIfFileExists(cssList, basePath, clubCssPath, "flxrez_" + activity_id + ".css", baseVersion);

                // Get FlxRez js, if any
                Utilities.addIfFileExists(jsList, basePath, jsPath, "flxrez.js", baseVersion);
                Utilities.addIfFileExists(jsList, basePath, clubJsPath, "club_flxrez.js", baseVersion);
                break;

        }
        
        // get custom template from club5 if selected for this club
        if (con != null) {
            customStyles = Utilities.getCustomStyles(con);  // get file name from club5 (change name if styles are updated!!!!)
            if (!customStyles.equals("")) {
                //customStyles = cssStart + customPath + slash + customStyles + cssEnd;  // currently = v5/assets/stylesheets/custom/xyz.css
                //cssList.add(customPath + slash + customStyles);
                // Add custom styles regardless.  If file does not exists, there will be no version number.
                /*
                String[] csArray = customStyles.split(";");
                boolean isRwdCs = false;
                for (int i = 0; i < csArray.length; i++){
                    csArray[i] = csArray[i].trim();
                    if(csArray[i].length() > 0){
                        isRwdCs = csArray[i].matches("_rwd.css$");
                        if(rwd && isRwdCs){
                            Utilities.addIfFileExists(cssList, basePath, customPath, csArray[i], baseVersion, true);
                        } else if (!rwd && !isRwdCs) {
                            Utilities.addIfFileExists(cssList, basePath, customPath, csArray[i], baseVersion, true);
                        }   
                    }
                }
                 * 
                 */
                if(rwd){
                    Utilities.addIfFileExists(cssList, basePath, customPath, customStyles.replaceAll("\\.css$","_rwd.css"), baseVersion, true);
                } else {
                    Utilities.addIfFileExists(cssList, basePath, customPath, customStyles, baseVersion, true);
                }
                
            }
        }
        
        if (caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {
            Utilities.addIfFileExists(cssList, basePath, cssPath, "premier.css", baseVersion);
            Utilities.addIfFileExists(cssList, basePath, clubCssPath, "premier.css", baseVersion);
        }

        // Meta vars
        scripts.append(getMetaTag("application-name","ForeTees"));
        scripts.append(getMetaTag("ft-server-id", String.valueOf(ProcessConstants.SERVER_ID) ));
        scripts.append(getMetaTag("ft-activity-id", String.valueOf(activity_id) ));
        if(rwd){
            // this is now set in javascript, not here
            //scripts.append(getMetaTag("viewport","width=device-width, maximum-scale=1, minimum-scale=1, initial-scale=1, user-scalable=no"));
        } else {
            scripts.append(getMetaTag("viewport","width=device-width, height=device-height"));
        }
        scripts.append(getMetaTag("X-UA-Compatible","IE=edge,chrome=1", true)); // Force IE9 to use latest rendering mode and enable Chrome Frame if user has it installed);
        scripts.append(getMetaTag("content-type", "text/html; charset=UTF-8", true));
        
        // If we have a session, set some parameters for use in javascript
        if (session != null) {
            java.util.Date serverTime = new java.util.Date();
            Long timeStamp = serverTime.getTime();
            
            /*
            Map<String, Object> assetsNameMap = new LinkedHashMap<String, Object>();
            assetsNameMap.put("sitewide_css", sitewide_css);
            assetsNameMap.put("dining_css", dining_css);
            assetsNameMap.put("member_calendar_js", foreTeesMemberCalendar_js);
            assetsNameMap.put("fortees_global_js", global_js);
             */
            
            Map<String, Object> metaVarsMap = new LinkedHashMap<String, Object>();
            metaVarsMap.put("user", (String) session.getAttribute("user"));
            metaVarsMap.put("caller", (String) session.getAttribute("caller"));
            metaVarsMap.put("club", club);
            metaVarsMap.put("zipcode", (String) session.getAttribute("zipcode"));
            metaVarsMap.put("session_timeout", session.getMaxInactiveInterval());
            metaVarsMap.put("session_time", timeStamp);
            //metaVarsMap.put("assets_name_map", assetsNameMap);
            //metaVarsMap.put("jsid", session.getId());
            if (session.getAttribute("premier_referrer") != null) metaVarsMap.put("premier_referrer", (String) session.getAttribute("premier_referrer"));
            if (session.getAttribute("sso_tpa_mode") != null) metaVarsMap.put("sso_tpa_mode", (String) session.getAttribute("sso_tpa_mode"));
            if (session.getAttribute("app_mode") != null) metaVarsMap.put("app_mode", (Integer) session.getAttribute("app_mode"));
            
            if(includeEditor){
                metaVarsMap.put("editor_path",jQueryPath+slash+tinyMce_js);
            }
            
            Gson gson_obj = new Gson();
            scripts.append(getMetaTag("ft-session-parms",gson_obj.toJson(metaVarsMap)));
        }

        // CSS
        for(String script : cssList){
            scripts.append(getCssTag(script,CDN));
        }
        // JS
        for(String script : jsList){
            scripts.append(getJsTag(script,CDN));
        }
        
        // Then IE8 specific JS/CSS hacks.    
        scripts.append("<!--[if lt IE 9]>\n");
        for(String script : ie8CssList){
            scripts.append(getCssTag(script,CDN));
        }
        for(String script : ie8JsList){
            scripts.append(getJsTag(script,CDN));
        }
        scripts.append("<![endif]-->\n");
        
        // Then IE7 specific JS/CSS hacks.    
        scripts.append("<!--[if lt IE 8]>\n");
        for(String script : ie7CssList){
            scripts.append(getCssTag(script,CDN));
        }
        for(String script : ie7JsList){
            scripts.append(getJsTag(script,CDN));
        }
        scripts.append("<![endif]-->\n");

        return scripts.toString();

    }
    
    public static String getJsTag(String script, String host) {
        if(host == null){
            host = "";
        }
        return "<script type=\"text/javascript\" src=\"" + host + script + "\"></script>\n";
    }
    
    public static String getCssTag(String script, String host) {
        return getCssTag(script, host, false);
    }
    
    public static String getCssTag(String script, String host, boolean noCloseTag) {
        if(host == null){
            host = "";
        }
        String closeTag = " /";
        if(noCloseTag){
            closeTag = "";
        }
        return "<link rel=\"stylesheet\" href=\"" + host + script + "\" type=\"text/css\""+closeTag+">\n";
    }
    
    public static String getMetaTag(String name, String content) {
        return getMetaTag(name, content, false);
    }
    
    public static String getMetaTag(String name, String content, Boolean httpequiv) {
        if(httpequiv == null || httpequiv == false){
            return "<meta name=\""+StringEscapeUtils.escapeHtml(name)+"\" content=\""+StringEscapeUtils.escapeHtml(content)+"\" />\n";
        } else {
            return "<meta http-equiv=\""+StringEscapeUtils.escapeHtml(name)+"\" content=\""+StringEscapeUtils.escapeHtml(content)+"\" />\n";
        }
    }
    
    private static String getServletName(HttpServletRequest req) {
        
        if (req != null) {
            String temp[];
            temp = req.getServletPath().split(slash);
            return temp[temp.length - 1];
        } else {
            return "";
        }
        
    }
    
    private static String getBaseVersion(){
        // Get base version (Appended to each script version)
        File checkFile = new File(basePath + baseVersionFile);
         if(checkFile != null && checkFile.isFile()){
            return "_" + (checkFile.lastModified()/1000);
         } else {
             return "_";
         }
    }

    /**
     ***************************************************************************************
     *
     * outputHeader
     *
     * This method will output the start of the page (doctype, html, head, title,
     * stylesheets, and scripts) and can conditionally leave the head element open
     * with a valid req object the proper js files, css and customs can be included
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param title the value to use in the title tag for the page being built
     * @param close_head boolean flag to indicate if the head element should be closed or left open
     * @param out the PrintWriter to write out the html
     * @param req the HttpServletRequest from the calling servlet
     *
     ***************************************************************************************
     **/
    public static void outputHeader(String club, int activity_id, String title, boolean close_head, PrintWriter out, HttpServletRequest req) {

        outputHeader(club, activity_id, title, close_head, out, req, 0, "");

    }

    /**
     ***************************************************************************************
     *
     * outputHeader
     *
     * This method will output the start of the page (doctype, html, head, title,
     * stylesheets, and scripts) and can conditionally leave the head element open
     * with a valid req object the proper js files, css and customs can be included
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param title the value to use in the title tag for the page being built
     * @param close_head boolean flag to indicate if the head element should be closed or left open
     * @param out the PrintWriter to write out the html
     * @param req the HttpServletRequest from the calling servlet
     * @param refresh_val number of seconds to refresh the page
     * @param refresh_url the url to get control on the refresh
     *
     ***************************************************************************************
     **/
    public static void outputHeader(String club, int activity_id, String title, boolean close_head, PrintWriter out, HttpServletRequest req,
            int refresh_val, String refresh_url) {

        /*
        String doctype = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>";
        
        String begin = "<HTML xmlns='http://www.w3.org/1999/xhtml'><!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) " +
        "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected " +
        "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. " +
        "\nReproduction is strictly prohibited.-->\n";
         */
        
        HttpSession session = null;
        
        if (req != null) {
            session = req.getSession(false);  // Get user's session object (no new one)
        }
        

        String metaRefresh = "";
        if (!refresh_url.equals("")) {
            
            if (session != null) {
                refresh_url = setUrlJsid(refresh_url, session);
            }

            metaRefresh = "<meta http-equiv=\"Refresh\" content=\"" + refresh_val + "; url=" + refresh_url + "\">";
        }
        
        String doctype = "<!DOCTYPE html>";
        //String doctype = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\" lang=\"en\">";

        String begin = "<!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) "
                + "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected "
                + "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. "
                + "\nReproduction is strictly prohibited. -->\n";


        String header = "<html lang=\"en-US\">\n<head>\n"
                + "<title>" + title + "</title>\n"
                + metaRefresh
                + getScripts(club, activity_id, session, req, false)
                + ((close_head) ? "</head>" : "") + "\n";


        //
        //  Output the start of the page (doctype, html, head, and title tags)
        //
        out.println(doctype);
        out.println(begin);
        out.println("<!-- activity_id=" + activity_id + ",  -->");
        out.println(header);

        //out.println("<script type=\"text/javascript\" src=\"http://gettopup.com/releases/latest/top_up-min.js\"></script>");

    }

    /**
     ***************************************************************************************
     *
     * outputBody
     * 
     * This method will build the body tag and the wrapper.
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param out the PrintWriter to write out the html
     *
     ***************************************************************************************
     **/
    public static void outputBody(String club, int activity_id, PrintWriter out, HttpServletRequest req) {
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);
        StringBuffer bodyStart = new StringBuffer();
        
        bodyStart.append("<body class=\"");
        bodyStart.append(getServletName(req));
        bodyStart.append("\">");
        bodyStart.append("<div id=\"top\"></div>");
        if(rwd){
            bodyStart.append("<div id=\"rwdScreenMode\"></div>");
        }
        bodyStart.append("<div id=\"wrapper\">");// this is terminated in outputPageEnd below
        if(rwd){
            bodyStart.append("<div id=\"rwd_wrapper\">");// this is terminated in outputPageEnd below
        }
        
        out.println(bodyStart.toString());
    }


    /**
     ***************************************************************************************
     *
     * outputTopNav
     *
     * This method will output the top navigation panel (main menus) for the Dining system.
     *
     * @param req HttpServletRequest object
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param out the PrintWriter to write out the html
     * @param con the database connection for the specified club
     *
     ***************************************************************************************
     **/

    public static void outputTopNav(HttpServletRequest req, String club, int activity_id, PrintWriter out, Connection con) {
        
        if(Utilities.getBitFromSession(req,"app_mode",ProcessConstants.APPMODE_HIDE_TOP_NAV)){
            return;
        }
        
        StringBuilder navHtml = Utilities.getRequestStringBuilder(req, attrPageHeader, new StringBuilder());
        
        
        // NOTE:  Try to make any club specific changes using club css before modifying markup.
        
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);    // get Responsive state

        // Standard navigation
        List<Map<String,Object>> activityNavList = getActivityNavList(req, activity_id, con, club);
        if(activityNavList.size() > 0){
            req.setAttribute(attrHasMainNav,true);
        } 
        req.setAttribute(attrHasBanner,true);
        if(!rwd){
            
            // Only show menu if there are menu items to show
            if(activityNavList.size() > 0){
                navHtml.append("<div id=\"topnav_container\"><div id=\"topnav\"><div class=\"topnav_open\"></div><ul>");
                navHtml.append(getNavListHtml(activityNavList, activity_id, club, "<li class=\"topnav_separation\"></li>"));
                navHtml.append("</ul><div class=\"topnav_close\"></div><div class=\"clearFix\"></div></div></div>");
            }
        } else {
            // Responsive/Mobile nav (under development)
            
            //List<Map<String,Object>> activityList = getActivityList(club, con, req);
            
            // Only show tabs if there is more than one tab, or if a single tab has children ("action" is List, not String)
            navHtml.append("<div id=\"rwdNavBlock\">");
            navHtml.append("<div id=\"rwdNavLogo\" class=\"");
            navHtml.append(getLogoClass(activity_id));
            navHtml.append("\"></div>");
            
            if(activityNavList.size() > 0){

                navHtml.append("<div id=\"rwdNav\" role=\"navigation\">");
                navHtml.append("<ul>");
                navHtml.append(getNavListHtml(activityNavList, activity_id, club, "\n"));
                navHtml.append("</ul>");
                navHtml.append("</div>");
                navHtml.append("<div id=\"rwdNav2\" role=\"navigation\">");
                navHtml.append("<a id=\"rwdNavButton\" href=\"#rwdNav2\" title=\"Menu\" class=\"ftB-36-White ftB-36-Menu\"><span></span><span>Menu</span></a>");
                navHtml.append("<ul>");
                navHtml.append(getNavListHtml(activityNavList, activity_id, club, "\n"));
                navHtml.append("</ul>");
                navHtml.append("<a id=\"rwdHelpButton\" href=\"#rwdNav2\" title=\"Menu\" class=\"ftB-36-White ftB-36-Help ftB-36-Right\"><span></span><span>Help</span></a>");
                navHtml.append("</div>");

            }
            
            
            navHtml.append("</div>");
        }

        if(!rwd){
            // If in desktop mode, just output the result now
            out.print(navHtml.toString());
        } else {
            // Else, if in resposive mode, buffer, and we'll output in "pageStart"
            req.setAttribute(attrPageHeader, navHtml);
        }

    }
    
    private static String getLogoClass(int activity_id){
        switch (activity_id) {
            case 0: // Golf
                return "golfLogo";
            case dining_activity_id: // Dining
                return "diningLogo";
            default: // Flxrez
                return "flxrezLogo";
        }
    }
    
    public static List<Map<String,Object>> getActivityNavList(HttpServletRequest req, int activity_id, Connection con, String club) {

        boolean isTLT = Utilities.isNotificiationClub(con);
        boolean blockAllAccess = false;
        boolean showTeeTimesMenu = true;
        boolean showReservationMenu = true;
        boolean showNotificationMenu = true;
        boolean showCalendarMenu = false;    // Intended to be used as a replacement for showReservationMenu, when reservations are not used for an activity.
        boolean showCalListMenu = false;     // Used in conjunction with the showCalendarMenu - will also show Reservations List option.
        boolean showEventsMenu = true;
        boolean showLessonMenu = false;     // Default to false, since we'll only display it if they have lessons configured
        boolean showSearchMenu = true;
        boolean showEmailMenu = true;
        boolean showPartnerMenu = true;
        boolean showHandicapMenu = Utilities.isHandicapSysConfigured(activity_id, con);
        boolean showSettingsMenu = true;
        boolean showHelpMenu = true;
        boolean showMobileHelp = false;
        boolean showHandicapReports = false;
        boolean showLogout = true;
        
        boolean showOtherMemberSearch = true;
        
        String caller = Utilities.getSessionString(req, "caller", "");          // get caller's name
        String mship = Utilities.getSessionString(req, "mship", "");            // get member's mship type
        String mtype = Utilities.getSessionString(req, "mtype", "");            // get member type
        String msubtype = Utilities.getSessionString(req, "msubtype", "");      // get member sub-type
        
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);           // get Responsive state
        
        List<Map<String,Object>> navList = new ArrayList<Map<String,Object>>();
        
        String rootItemCss = "topnav_item";
        String rightItemCss = "topnav_right_item";
        
        
        if (caller.equals(ProcessConstants.FT_PREMIER_CALLER)) showLogout = false;      // do not include the Logout tab if FT Premier (Flexscape)

        // add clubs here that do not want to display the Lesson Book tab
        if (!club.equals("wingedfoot") && !club.equals("indianridgecc")) {
            showLessonMenu = Utilities.isLessonBookConfigured(activity_id, con);
        }
        
        // If not golf or dining, see if this is a childless activity, and replace the Reservations menu with the Calendar menu if so.
        if (activity_id != 0 && activity_id != dining_activity_id && getActivity.isChildlessActivity(activity_id, con)) {
            showReservationMenu = false;
            showCalendarMenu = true;
        }
        
        if (club.equals("demosandbox")) {
            blockAllAccess = true;
            showHelpMenu = false;
        }
        
        // Aronimink GC - Hide the Notifications, Email, and Partner menus.
        if (club.equals("aronimink")) {
            showNotificationMenu = false;
            showEmailMenu = false;
            showPartnerMenu = false;
        }
        
        if (club.equals("demoroger") || club.equals("democonnect")) {
            
            if (activity_id == 2) {                 // Tennis
                showReservationMenu = false;
                showCalendarMenu = true;
                showLessonMenu = false;
                showHandicapMenu = false;
            }
            
            if (activity_id == 30) {                // Juniors
                showReservationMenu = false;
                showCalendarMenu = true;
                showLessonMenu = false;
                showEmailMenu = false;
                showHandicapMenu = false;
                //showEventsMenu = false;    // this is ok now that we added a custom to allow adults to see and edit depenedents' event registrations
            }
        }
        
        if (club.equals("denvercc") && (activity_id == 2 || activity_id == 3)) {   // if Denver and Juniors or Fitness
            
            showReservationMenu = false;
            showCalendarMenu = true;
            showCalListMenu = true;
            showSearchMenu = false;
            showEmailMenu = false;
            showHandicapMenu = false;
        }
        
        if (club.equals("merionrecip")) {
            showEventsMenu = false;
            showLessonMenu = false;
            showEmailMenu = false;
            showPartnerMenu = false;
            showHandicapMenu = false;
        }
        
        if (club.equals("mirabel")) {
            
            showCalendarMenu = true;    // Show calendar menu for ALL activities
            showEmailMenu = false;
            
            if (activity_id == 7 || activity_id == 8 || activity_id == 18) {    // Mbr Services
                showReservationMenu = false;
            }
        }
        
        if (club.equals("overlakegcc") || club.equals("wingedfoot") || club.equals("austincountryclub") 
                || (club.equals("ballantyne") && activity_id == 1) 
                || (club.equals("minikahda") && activity_id == dining_activity_id)) {
            
            showEventsMenu = false;
        }
        
        if (club.equals("roccdallas") || club.equals("blackhawk") || club.equals("rollinghillsgc") || ((club.equals("mayfieldsr") || club.equals("bellemeadecc")) && activity_id == 1)) {
            showLessonMenu = false;
        }
        
        if (club.equals("willowridgecc") || club.equals("blackstone") || club.equals("coloradospringscountryclub") || club.equals("moselemsprings")) {
            showEmailMenu = false;
        }
        
        if (club.equals("stcloudcc")) {
            showCalendarMenu = true;
        }
        
        if (club.equals("ccyork") || club.equals("lagrangecc")) {
            showHandicapMenu = false;
        }
        
        if (club.equals("gaaorlando")) {
            showCalendarMenu = true;
            showTeeTimesMenu = false;
            showReservationMenu = false;
            showNotificationMenu = false;
            showSearchMenu = false;
            showPartnerMenu = false;
            showHandicapMenu = false;
            showHelpMenu = false;
        }
        
        if (club.equals("interlachen") && activity_id == 11) {
            showTeeTimesMenu = false;
            showReservationMenu = false;
            showNotificationMenu = false;
            showSearchMenu = false;
            showEmailMenu = false;
            showPartnerMenu = false;
            showHandicapMenu = false;
            showSettingsMenu = false;
            showHelpMenu = false;
        }
        
        if (club.equals("hillwoodcc") && activity_id != 1 && mship.equals("Whitworth - Athletic")) {
            blockAllAccess = true;
        }
        /*
        if (!Utilities.checkMemberAccess(mship, mtype, activity_id, con)) {
            blockAllAccess = true;
        }*/
        
        
        // Check to see if the current member's mship has access to this activity_id.
        
        
        // Access to the current activity is universally denied for this member.
        if (blockAllAccess) {
            showTeeTimesMenu = false;
            showReservationMenu = false;
            showNotificationMenu = false;
            showEventsMenu = false;
            showLessonMenu = false;
            showSearchMenu = false;
            showEmailMenu = false;
            showPartnerMenu = false;
            showHandicapMenu = false;
            showSettingsMenu = false;
            showHelpMenu = false;
        }
        
        
        //  FT Connect - if a Connect-only tab, then only show the Settings, Help and Logout items
        
        if (activity_id == ft_connect_home_id || activity_id == ft_connect_directory_id || 
            activity_id == ft_connect_statements_id || activity_id == ft_connect_newsletters_id) {
            
            showTeeTimesMenu = false;
            showReservationMenu = false;
            showNotificationMenu = false;
            showEventsMenu = false;
            showLessonMenu = false;
            showSearchMenu = false;
            showEmailMenu = false;
            showPartnerMenu = false;
            showHandicapMenu = false;
            
            // only show the logout tab if Connect Home Page
            
            if (activity_id == ft_connect_home_id) {
               
                //showCalendarMenu = false;
                showSettingsMenu = false;
                showHelpMenu = false;               
            }
        }
        
        
        //
        //  Determine if we should include the Mobile Help tab
        //
        if (showHelpMenu == true && isTLT == false) {
            
            boolean allowMobile = Utilities.checkMobileSupport (con);  // Mobile allowed?
            
   // add later         if (allowMobile) showMobileHelp = true;    // yes, include the Mobile Help tab on all activities
            if (club.equals("philcricket") || (activity_id == 0 && allowMobile == true)) {      // TEMP
                
                showMobileHelp = true;
            }   
        }
        
        
        String username = "";

        if (club.equals("philcricket") || (activity_id == 0 && showHandicapMenu == true)) {  // Philly Cricket or Golf user - get member subtype
            
            //if (session != null) {         // get username of member, then get member subtype 
                username = Utilities.getSessionString(req, "user", username);
                if (msubtype.equals("")) {
                   msubtype = Utilities.getSubtypeFromUsername(username, con);
                }
            //}
        }

        
        if (club.equals("philcricket") && activity_id > 0 && activity_id < 4) {    //  Philly Cricket custom to limit activity access based on subtype
            
            //  Activity Ids:  1 = Tennis, 2 = Squash, 3 = Paddle
            
            String accessCode = "";

            if (activity_id == 1) {      // if Tennis

                accessCode = "T";        // set access code for this activity
            }

            if (activity_id == 2) {      // if Squash

                accessCode = "S";        // set access code for this activity
            }

            if (activity_id == 3) {      // if Paddle
                
                accessCode = "P";        // set access code for this activity
            }

            if (!msubtype.contains(accessCode)) {    // if member does not have access to this activity

                showEventsMenu = false;              // do not include the menus
                showReservationMenu = false;
                showCalendarMenu = false;
                showSearchMenu = false;
                showEmailMenu = false;
                showHandicapMenu = false;
                showLessonMenu = false;
                showPartnerMenu = false;
            }
          
        }        // end of IF Philly Cricket
        
        if (club.equals("quecheeclubtennis") && (activity_id == 1 || activity_id == 13 || activity_id == 16)) {    //  Philly Cricket custom to limit activity access based on subtype
            
            //  Activity Ids:  1 = Tennis, 2 = Squash, 3 = Paddle
            
            String accessCode = "";

            if (activity_id == 1) {      // if Tennis

                accessCode = "T";        // set access code for this activity
            }

            if (activity_id == 13) {      // if Paddle
                
                accessCode = "P";        // set access code for this activity
            }

            if (activity_id == 16) {      // if Squash

                accessCode = "S";        // set access code for this activity
            }

            if (!msubtype.contains(accessCode)) {    // if member does not have access to this activity

                showEventsMenu = false;              // do not include the menus
                showReservationMenu = false;
                showCalendarMenu = false;
                showSearchMenu = false;
                showEmailMenu = false;
                showHandicapMenu = false;
                showLessonMenu = false;
                showPartnerMenu = false;
            }
          
        }        // end of IF quecheeclubtennis
        
        
        if (
            (club.equals("pinehurstcountryclub") && activity_id == 1) 
            ||
            (club.equals("mirabel") && (activity_id == 1 || activity_id == 7 || activity_id == 8))
            ||
            (club.equals("aronimink") && isTLT && activity_id == 0)
        ){
            showOtherMemberSearch = false;
        }
        
        //  if Golf - check if member has access to Handicap Reports
        
        if (activity_id == 0 && showHandicapMenu == true) {
            if (msubtype.equals("Handicap Chair")) {    // if member has access to reports
                showHandicapReports = true;
            }
        }
        
        //Set up menus that could be used by any activity
        
        // Calendar menu
        Map<String,Object> calendarMenu = new LinkedHashMap<String, Object>();
        if (showCalListMenu) {
            List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
            itemList.add(getNavItemMap("My Calendar", "Member_teelist"));
            itemList.add(getNavItemMap("My Activities List", "Member_teelist_list"));
            calendarMenu = getNavItemMap("My Calendar", itemList, rootItemCss);
        } else {
            calendarMenu = getNavItemMap("My Calendar", "Member_teelist", rootItemCss);
        }
        
        // Events Menu
        Map<String,Object> eventsMenu = new LinkedHashMap<String, Object>();
        {
            String events_link = "Member_events";
            String events_title = "Events";
            if(activity_id == dining_activity_id){
                events_link = "Dining_home?view_events";
            } else if(activity_id > 0 && club.equals("bellemeadecc")){
                events_title = "Events / Clinics";
            } else if(activity_id == 0 && club.equals("tontoverde")){
                events_title = "Golf Events";
            } else if(activity_id == 11 && club.equals("interlachen")) {
                events_title = "Teams & Group Lessons";
            }
            eventsMenu = getNavItemMap(events_title, events_link, rootItemCss);
            
        }
        
        // Lessons Menu
        Map<String,Object> lessonMenu = new LinkedHashMap<String, Object>();
        {
            String lessons_title = "Lessons";
            List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
            if (club.equals("kopplinandkuebler")) {
               lessons_title = "Meetings";
               itemList.add(getNavItemMap("Schedule a Meeting", "Member_lesson"));
               itemList.add(getNavItemMap("View Search Executives' Bios", "Member_lesson?bio=yes"));               
            } else {
               itemList.add(getNavItemMap("Individual Lessons", "Member_lesson"));
               if (!club.equals("castlepines")) {
                  itemList.add(getNavItemMap("Group Lessons", "Member_lesson?group=yes"));
               }
               itemList.add(getNavItemMap("View Pros' Bios", "Member_lesson?bio=yes"));
               if (activity_id == 11 && club.equals("interlachen")) {
                  lessons_title = "Individual Lessons";
               }
            }
            lessonMenu = getNavItemMap(lessons_title, itemList, rootItemCss);
        }
        
        // Search menu
        Map<String,Object> searchMenu = new LinkedHashMap<String, Object>();
        if(activity_id == dining_activity_id) {
            searchMenu = getNavItemMap("Search", "Dining_home?search", rootItemCss);
        } else {
            List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
            String sType = "Your Past Tee Times";
            String sOmType = "Tee Times";
            if(activity_id == 0 && isTLT){
                sType = "My Past Rounds";
                sOmType = "Notifications";
            } else if (activity_id != 0){
                sType = "Your Past Reservations";
                sOmType = "Reservations";
            }
            if(showOtherMemberSearch){
                itemList.add(getNavItemMap("Other Members' " + sOmType, "Member_searchmem"));
            }
            itemList.add(getNavItemMap(sType+" - This Calendar Year", "Member_searchpast?subtee=cal"));
            itemList.add(getNavItemMap(sType+" - Past 12 Months", "Member_searchpast?subtee=year"));
            itemList.add(getNavItemMap(sType+" - Since Inception", "Member_searchpast?subtee=forever"));
            searchMenu = getNavItemMap("Search", itemList, rootItemCss);
        }
        
        // Email Menu
        Map<String,Object> emailMenu = new LinkedHashMap<String, Object>();
        {
            List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
            itemList.add(getNavItemMap("Send Email", "Member_email"));
            itemList.add(getNavItemMap("Manage Distribution Lists", "Member_email?manage_distribution_lists"));
            emailMenu = getNavItemMap("Email", itemList, rootItemCss);
        }
        
        // Partner menu
        Map<String,Object> partnerMenu = new LinkedHashMap<String, Object>();
        if (Utilities.isGuestTrackingConfigured(activity_id, con) && activity_id == 0) {
            List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
            itemList.add(getNavItemMap("Manage Partners", "Member_partner"));
            itemList.add(getNavItemMap("Manage Guests", "Common_guestdb"));
            partnerMenu = getNavItemMap("Partners/Guests", itemList, rootItemCss);
        } else {
            partnerMenu = getNavItemMap("Partners", "Member_partner", rootItemCss);
        }
        
        // Settings menu
        Map<String,Object> settingsMenu = new LinkedHashMap<String, Object>();
        if(activity_id == dining_activity_id){
            settingsMenu = getNavItemMap("Settings", "Dining_home?settings", rootItemCss);
        } else {
            settingsMenu = getNavItemMap("Settings", "Member_services", rootItemCss);
        }
        
        // Generate menu based on activity ID
        switch(activity_id){
            
            // Golf Menus
            case 0:

                if (isTLT && showNotificationMenu) {
                    List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
                    itemList.add(getNavItemMap("Make, Change, or View Notifications", "Member_select"));
                    itemList.add(getNavItemMap("Today's Notifications", "MemberTLT_sheet?index=0"));
                    itemList.add(getNavItemMap("My Activites / Calendar", "Member_teelist"));
                    itemList.add(getNavItemMap("My Activites / List", "Member_teelist_list"));
                    navList.add(getNavItemMap("Notifications", itemList, rootItemCss));
                } else if (!isTLT && showTeeTimesMenu) {
                    List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
                    itemList.add(getNavItemMap("Make, Change, or View Tee Times", "Member_select"));
                    itemList.add(getNavItemMap("Today's Tee Sheet", "Member_sheet?index=0"));
                    itemList.add(getNavItemMap("My Tee Times / Calendar", "Member_teelist"));
                    itemList.add(getNavItemMap("My Tee Times / List", "Member_teelist_list"));
                    navList.add(getNavItemMap("Tee Times", itemList, rootItemCss));
                }

                if (showCalendarMenu) {
                     navList.add(calendarMenu);
                }

                if (showEventsMenu) {
                    navList.add(eventsMenu);
                }

                if (showLessonMenu) {
                    if (club.equals("desertmountain")) {
                        //  Desert Mountain - route lesson tab to 3rd party lesson book (open a new window)                        
                        navList.add(getNavItemMap("Lessons", "https://www.smarterlessons.com/prod/pub/sl_iframe.php?m=b2ab92dab715bebf3a531b7ee2e895b1", rootItemCss, new String[]{"target", "_blank"}));
                    } else if (club.equals("ccofbuffalo")) {
                        navList.add(getNavItemMap("Simulator", "Member_lesson?proid=1", rootItemCss));
                    } else if (club.equals("weeburn")) {
                        navList.add(getNavItemMap("Indoor Golf Studio", "Member_lesson?proid=1", rootItemCss));
                    } else {
                        navList.add(lessonMenu);
                    }
                }

                if (showHandicapMenu) {
                    List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
                    itemList.add(getNavItemMap("Post a Score", "Member_handicaps?todo=post"));
                    itemList.add(getNavItemMap("View Handicaps and Scores", "Member_handicaps?todo=view"));
                    if (showHandicapReports) {
                        itemList.add(getNavItemMap("Peer Review Report", "Proshop_report_handicap"));
                        itemList.add(getNavItemMap("Missed Postings Report", "Proshop_report_handicap?todo=nonPosters"));
                        itemList.add(getNavItemMap("Posted Scores by Day", "Proshop_report_handicap?todo=view"));
                    }
                    navList.add(getNavItemMap("Handicaps", itemList, rootItemCss));
                }

                break; // End of Golf Menus
               

            // Dining Menus
            case dining_activity_id:
                
                if (showReservationMenu) {
                    List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
                    itemList.add(getNavItemMap("Make a Reservation", "Dining_slot?action=new"));
                    itemList.add(getNavItemMap("My Reservations / Calendar", "Member_teelist"));
                    itemList.add(getNavItemMap("My Reservations / List", "Member_teelist_list"));
                    navList.add(getNavItemMap("Reservations", itemList, rootItemCss));
                }
                
                if (showCalendarMenu) {
                    navList.add(calendarMenu);
                }
                
                if (showEventsMenu) {
                    navList.add(eventsMenu);
                }

                break; // End of Dining Menus
               

            // FlxRez menus
            default:
                
                if (showReservationMenu) { 
                    List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
                    itemList.add(getNavItemMap("Make, Change, or View Reservations", "Member_gensheets"));
                    itemList.add(getNavItemMap("My Reservations / Calendar", "Member_teelist"));
                    itemList.add(getNavItemMap("My Reservations / List", "Member_teelist_list"));
                    navList.add(getNavItemMap("Reservations", itemList, rootItemCss));
                }
                
                if (showCalendarMenu) {
                    navList.add(calendarMenu);
                }
                
                if (showEventsMenu) {
                    navList.add(eventsMenu);
                }
                
                if (showLessonMenu) {
                    
                    String ballMachineUri = null;
                    if (club.equals("ballantyne")) {
                        ballMachineUri = "?proid=1";
                    } else if (club.equals("sierraviewcc")) {
                        ballMachineUri = "?proid=4";
                    } else if (club.equals("fortcollins")) {
                        ballMachineUri = "?proid=15";
                    }
                    
                    if(ballMachineUri != null){
                        navList.add(getNavItemMap("Ball Machine", "Member_lesson" + ballMachineUri, rootItemCss));
                    } else if (club.equals("philcricket")) {
                        navList.add(getNavItemMap("Clinics", "Member_lesson?group=yes&proid=18", rootItemCss));
                    } else if (club.equals("ccofnc") && activity_id == 1) {
                        navList.add(getNavItemMap("Bios", "Member_lesson?bio=yes", rootItemCss));
                    } else if (club.equals("bellemeadecc") && activity_id == 1) {
                        navList.add(getNavItemMap("Lessons", "Member_lesson", rootItemCss));
                    } else if (club.equals("denvercc")) {
                        String customMenuName = "";
                        if (activity_id == 1) {
                            customMenuName = "Programs";
                        } else if (activity_id == 2) {
                            customMenuName = "Lessons";  
                        } else {
                            customMenuName = "Classes";
                        }
                        navList.add(getNavItemMap(customMenuName, "Member_lesson?group=yes", rootItemCss));
                    } else {
                        navList.add(lessonMenu);
                    }

                }
                
                break; // End of FlexRez Menus
                
        } // End switch
        
        // Add menus for all activites
        
        if (showSearchMenu) {
            navList.add(searchMenu);
        }

        if (showEmailMenu) {
            navList.add(emailMenu);
        }

        if (showPartnerMenu) {
            navList.add(partnerMenu);
        }

        if (showSettingsMenu) {
            navList.add(settingsMenu);
        }
        
        // Add "right link" menus
        
        if (showMobileHelp && !rwd) {
            navList.add(getNavItemMap("Mobile?", "Common_mobile_help", rightItemCss, new String[]{"target", "_blank"}));
        }
        
        if (showHelpMenu && !rwd) {
            navList.add(getNavItemMap("Help?", "Member_help", rightItemCss));
        } else if (showHelpMenu && rwd){
            navList.add(getNavItemMap("Help", "#help", rightItemCss + " rwdHelpMenu"));
        }
        
        if (showLogout) {
            if (club.equals("mpccpb")) {
                navList.add(getNavItemMap("Home", "Logout", rightItemCss));
            } else if (caller.equals("") || caller.equals("none")) {
                navList.add(getNavItemMap("Logout", "Logout", rightItemCss));
            } else {
                navList.add(getNavItemMap("Exit", "Logout", rightItemCss));
            }
        }
        
        return navList;
        
    }


    /**
     ***************************************************************************************
     *
     * outputBanner
     * 
     * This method will output the banner portion of the main page (club name and weather link).
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param zip the zip code for the specified club (for weather link)
     * @param out the PrintWriter to write out the html
     * @param req HttpServletRequest object
     *
     ***************************************************************************************
     **/
    public static void outputBanner(String club, int activity_id, String clubName, String zip, PrintWriter out, HttpServletRequest req) {
        
        int ftConnect = Utilities.getSessionInteger(req, "ftConnect", 0);       // FT Connect club indicator
        String caller = Utilities.getSessionString(req, "caller", "");          // website caller
        String fullName = Utilities.getSessionString(req, "name", "");          // user's full name
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);           // Responsive mode
        StringBuilder banner = Utilities.getRequestStringBuilder(req, attrPageHeader, new StringBuilder());
          
        banner.append("<div class=\"banner_container\">");
        
        if(rwd){
            banner.append("<div class=\"rwd_banner\">");
        }
        
        if (caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {      // if FT Connect Premier (Flexscape)
            
            if(!rwd){
                banner.append("<span class=\"br_hack\"><BR /><BR /></span>");         // skip the club name/link
            }
            
        } else {
        
            if (club.equals("mirabel")) {
                
                if(!rwd){
                    banner.append("<span class=\"br_hack\"><BR /></span>");         // skip the club name/link
                }

                banner.append("<div id=\"title\" class=\"banner_title\"><span>Welcome to FlexRez,</span> <span>your source for everything Mirabel.</span></div>");   // This is exactly what they wanted (mis-spelled FlxRez and all) for all activities - Golf, FlxRez & Dining!

            } else {

                //out.println("<div id=\"title\"> " + clubName + " </div>");

                if (ftConnect == 1) {      //  if club uses FT Connect

                        banner.append("<div class=\"banner_title\"><a id=\"title\" href=\"Member_announce?activity=connect\">" + clubName + "</a></div>");

                } else {

                    //if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                        //out.println("<div><a id=\"title\" href=\"Dining_home\">" + clubName + "</a></div>");
                    //} else {

                        banner.append("<div class=\"banner_title\"><a id=\"title\" href=\"Member_announce\">" + clubName + "</a></div>");
                    //}
                }
            }

            banner.append("<div class=\"welcome_member\"><span>Welcome,</span> <span>" +fullName+ "</span></div>");
                  
            
            if (activity_id == dining_activity_id) {                  // if Dining - display the dining logo

                //out.println("<div id=\"dining_logo\"> </div>");        // pull in the dining logo - defined in the stylesheet (see outputlogo below)

            } else {
                
                String weather_url = null;
                String weather_text = "View Current Weather";

                if (club.equals("philcricket")) {       // Use a custom weather link for Phily Cricket

                    //out.print("<div id=\"weather\"><a href=\"http://www.weather.com/weather/today/Flourtown+PA+19031\" target=\"_blank\">View Current Weather</a></div>");
                    weather_url = "http://www.weather.com/weather/today/Flourtown+PA+19031";

                } else if (club.equals("denvercc")) {

                    //out.print("<div id=\"weather\"><a href=\"http://weather.weatherbug.com/CO/Denver-weather/local-forecast/7-day-forecast.html?zcode=z5545&units=0\" target=\"_blank\">View Current Weather</a></div>");
                    weather_url = "http://weather.weatherbug.com/CO/Denver-weather/local-forecast/7-day-forecast.html?zcode=z5545&units=0";

                } else if (club.equals("mesaverdecc")) {

                    //out.print("<div id=\"weather\"><a href=\"http://www.weatherlink.com/user/mesaverdecc\" target=\"_blank\">View Current Weather</a></div>");
                    weather_url = "http://www.weatherlink.com/user/mesaverdecc";
                    
                } else if (club.equals("desertmountain")) {

                    //out.print("<div id=\"weather\"><a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1048342\" target=\"_blank\">View Current Weather</a></div>");
                    weather_url = "http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1048342";

                } else if (zip != null && !zip.equals("") && activity_id != ft_connect_home_id) {     // if zipcode provided    (also add the ForeTees or FlxRez logo??????)

                    // out.println("<div id=\"weather\"><a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" onclick=\"countHit('ACCU')\">View Current Weather</a></div>");
                    //out.print("<div id=\"weather\"><a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&amp;zipcode=" + zip + "\" target=\"_blank\">View Current Weather</a></div>");
                    weather_url = "http://wwwa.accuweather.com/forecast.asp?partner=&amp;zipcode=" + zip ;
                }
                
                if(weather_url != null){
                    banner.append("<div id=\"weather\"><a href=\"" + weather_url + "\" target=\"_blank\"><span>" + weather_text + "</span></a></div>");
                }
            }
    
            if (!rwd && club.equals("mirabel")) banner.append("<span class=\"br_hack\"><BR /><BR /><BR /></span>");
            
        }         // end of IF caller=FLEXWEBFT
        if(!rwd){
            banner.append("<div class=\"clearfloat\"></div>");
        } else {
            banner.append("<div id=\"rwdBannerLogo\" class=\"");
            banner.append(getLogoClass(activity_id));
            banner.append("\"></div>"); // Rwd may place a logo here, depending on screen size
            banner.append("</div>"); // close rwd_banner
        }
        req.setAttribute(attrHasBanner,true);
        banner.append("</div>"); // close banner_container
        
        if(!rwd){
            // If in desktop mode, just output the result now
            out.print(banner.toString());
        } else {
            // Else, if in resposive mode, buffer, and we'll output in "pageStart"
            req.setAttribute(attrPageHeader, banner);
        }
        
    }

    
    /**
     ***************************************************************************************
     *
     * outputSubNav
     * 
     * This method will output the sub-navigation tabs (Golf, Tennis, Dining, etc).
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param out the PrintWriter to write out the html
     * @param con the database connection for the specified club
     * @param req HttpServletRequest object
     * 
     ***************************************************************************************
     **/
    public static void outputSubNav(String club, int activity_id, PrintWriter out, Connection con, HttpServletRequest req) {
        
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);    // get Responsive state
        
        StringBuilder navHtml = Utilities.getRequestStringBuilder(req, attrPageHeader, new StringBuilder());
        
        // NOTE: getActivityList method is where decision to build menu or not is located.  Not here.
        List<Map<String,Object>> activityList = getActivityList(club, con, req);
        req.setAttribute(attrSubNavSize, activityList.size());
        // Only show tabs if there is more than one tab, or if a single tab has children ("action" is List, not String)
        if(activityList.size() > 1 || (activityList.size() == 1 && activityList.get(0).get("action") instanceof List)){
            req.setAttribute(attrHasSubNav,true);
            navHtml.append("<div id=\"subnav\"><ul>");
            navHtml.append(getNavListHtml(activityList, activity_id, club));
            navHtml.append("</ul>");
            // Build Activity select list
            if(rwd){
                navHtml.append("<div id=\"subNavSelect\"><label><span>Activity:</span><select>");
                navHtml.append(getNavOptionListHtml(activityList, activity_id, club));
                navHtml.append("</select></label></div>");
            }
            navHtml.append("</div>");
        }
        if(!rwd){
            // If in desktop mode, just output the result now
            out.print(navHtml.toString());
        } else {
            // Else, if in resposive mode, buffer, and we'll output in "pageStart"
            req.setAttribute(attrPageHeader, navHtml);
        }
    }
    
    private static String getSizeClass(int size){
        if(size > 6){
            return "large";
        } else if(size > 3){
            return "medium";
        } else if(size > 0){
            return "small";
        } else {
            return "empty";
        }
    }
    
    /**
     ***************************************************************************************
     *
     * getNavListHtml
     * 
     * This method will return HTML for navigation lists
     *
     * @param navList List of navigation items
     * @param activity_id the activity id that the member is currently using
     * @param club club name
     * 
     ***************************************************************************************
     **/    
    private static String getNavListHtml(List<Map<String,Object>> navList, int activity_id, String club){
        return getNavListHtml(navList, activity_id, club, "", "", "", false);
    }
    
    /**
     ***************************************************************************************
     *
     * getNavListHtml
     * 
     * This method will return HTML for navigation lists
     *
     * @param navList List of navigation items
     * @param activity_id the activity id that the member is currently using
     * @param club club name
     * @param spacer html to place between menu <li> elements
     * 
     ***************************************************************************************
     **/  
    private static String getNavListHtml(List<Map<String,Object>> navList, int activity_id, String club, String spacer){
        return getNavListHtml(navList, activity_id, club, spacer, "", "", false);
    }
    
    /**
     ***************************************************************************************
     *
     * getNavListHtml
     * 
     * This method will return HTML for navigation lists
     *
     * @param navList List of navigation items
     * @param activity_id the activity id that the member is currently using
     * @param club club name
     * @param spacer html to place between menu <li> elements
     * @param linkPre html to place after <a>, but before <span> inside of anchor tag.
     * @param linkPost html to place before </a>, but after </span> inside of anchor tag.
     * 
     ***************************************************************************************
     **/ 
    private static String getNavListHtml(List<Map<String,Object>> navList, int activity_id, String club, String spacer, String linkPre, String linkPost){
        return getNavListHtml(navList, activity_id, club, spacer, linkPre, linkPost, false);
    }
    
    /**
     ***************************************************************************************
     *
     * getNavListHtml
     * 
     * This method will return HTML for navigation lists
     *
     * @param navList List of navigation items
     * @param activity_id the activity id that the member is currently using
     * @param club club name
     * @param spacer html to place between menu <li> elements
     * @param linkPre html to place after <a>, but before <span> inside of anchor tag.
     * @param linkPost html to place before </a>, but after </span> inside of anchor tag.
     * @param recurseMod If false (default), spacer, linkPre and linkPost will only be used for topmost list
     * 
     ***************************************************************************************
     **/ 
    private static String getNavListHtml(List<Map<String,Object>> navList, int activity_id, String club, String spacer, String linkPre, String linkPost, boolean recurseMod){
        List<String> navHtmlList = new ArrayList<String>();
        int navListCount = 0;
        int navListSize = navList.size();
        String tspacer = spacer;
        String tlinkPre = linkPre;
        String tlinkPost = linkPost;
        if(!recurseMod){
            spacer = "";
            linkPre = "";
            linkPost = "";
            recurseMod = true;
        }
        for (Map<String, Object> navItemMap : (List<Map<String,Object>>) navList){
            navListCount ++;
            if(navItemMap.get("action") instanceof String){
                // The action is a string, so must be a url.
                navHtmlList.add(getItemHtml(navItemMap, activity_id, navListSize, navListCount, tlinkPre, tlinkPost));
            } else if (navItemMap.get("action") instanceof List){
                // The action must be another list of navigation elements
                // Recursivly process them
                StringBuilder liClass = new StringBuilder();
                if(navListSize == navListCount){
                    liClass.append(" lastItem");  // IE8 doesn't support last-child, so we add this for IE8 compatibility
                }
                @SuppressWarnings("unchecked")
                    List<Map<String,Object>> action = (List<Map<String,Object>>) navItemMap.get("action");
                navHtmlList.add("<li aria-haspopup=\"true\" class=\""+
                    StringUtils.join(new String[]{getItemClass(navItemMap), getItemActivityClass(navItemMap, activity_id)}, " ")+liClass.toString()+
                    "\">"+getNodeLinkHtml ((String) navItemMap.get("name"), "#", navItemMap, activity_id, tlinkPre, tlinkPost)+
                    "<ul>"+getNavListHtml(action, activity_id, club, spacer, linkPre, linkPost, recurseMod)+"</ul></li>");
            }
        }
        return Utilities.implode(navHtmlList, tspacer);
    }
    
    private static String getNavOptionListHtml(List<Map<String,Object>> navList, int activity_id, String club){
        List<String> navHtmlList = new ArrayList<String>();
        for (Map<String, Object> navItemMap : (List<Map<String,Object>>) navList){
            if(navItemMap.get("action") instanceof String){
                // The action is a string, so must be a url.
                Integer item_activity = (Integer) navItemMap.get("id");
                StringBuilder option = new StringBuilder();
                option.append("<option data-fturl=\"");
                option.append(StringEscapeUtils.escapeHtml((String) navItemMap.get("action")));
                option.append("\"");
                if( item_activity != null){
                    option.append(" value=\"");
                    option.append(item_activity.toString());
                    option.append("\"");
                    if(item_activity.equals(activity_id)){
                        option.append(" selected");
                    }
                }
                option.append(">");
                option.append(StringEscapeUtils.escapeHtml((String) navItemMap.get("name")));
                option.append("</option>");
                navHtmlList.add(option.toString());
            } else if (navItemMap.get("action") instanceof List){
                // We don't support sub items for activity lists
            }
        }
        return StringUtils.join(navHtmlList.toArray(new String[navHtmlList.size()]), "");
    }
    
    private static String getItemHtml(Map<String, Object> navItemMap, int activity_id, int navListSize, int navListCount, String linkPre, String linkPost){
        StringBuilder liClass = new StringBuilder();
        if(navListSize == navListCount){
            liClass.append(" lastItem");  // IE8 doesn't support last-child, so we add this for IE8 compatibility
        }
        return "<li aria-haspopup=\"false\" class=\""+getItemClass(navItemMap)+liClass.toString()+
            "\">"+getNodeLinkHtml((String) navItemMap.get("name"), (String) navItemMap.get("action"), navItemMap, activity_id, linkPre, linkPost)+
            "</li>";
    }
    
    private static String getNodeLinkHtml(String name, String url, Map<String, Object> navItemMap, Integer activity_id, String linkPre, String linkPost){
        return "<a href=\""+StringEscapeUtils.escapeHtml(url)+"\" class=\""+getItemActivityClass(navItemMap, activity_id)+"\""+getItemAttributes(navItemMap)+">"+linkPre+"<span class=\""+getItemClass(navItemMap)+"\">"+StringEscapeUtils.escapeHtml(name)+"</span>"+linkPost+"</a>";
    }
    
    private static String getItemClass(Map<String, Object> navItemMap){
        @SuppressWarnings("unchecked")
            List<String> classes = (List<String>) navItemMap.get("classes");
        StringBuilder sClass = new StringBuilder();
        if(classes != null){
            sClass.append(StringUtils.join(classes.toArray(new String[classes.size()]), " "));
        }
        return sClass.toString();
    }
    
    private static String getItemActivityClass(Map<String, Object> navItemMap, Integer activity_id){
        StringBuilder aClass = new StringBuilder();

        Integer item_activity = (Integer) navItemMap.get("id");
        
        if( item_activity != null ){
            aClass.append("actId_");
            aClass.append(item_activity);
            if(activity_id != null && item_activity.equals(activity_id) ){
                aClass.append(" ");
                aClass.append((String) navItemMap.get("ifActive"));
            }
        }
        return aClass.toString();
    }
    
    private static String getItemAttributes(Map<String, Object> navItemMap){
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = (LinkedHashMap<String, String>) navItemMap.get("actionAttributes");
        List<String> attrList = new ArrayList<String>();
        if(attributes != null && attributes.size() > 0){
            attrList.add(""); // Empty string to add space at begining of attribute list
            Iterator it = attributes.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry item = (Map.Entry)it.next();
                attrList.add(item.getKey() + "=\"" + StringEscapeUtils.escapeHtml((String) item.getValue()) + "\"");
            }
        }
        return StringUtils.join(attrList.toArray(new String[attrList.size()]), " ");
    }
    
    private static void addItemAttribute(Map<String, Object> navItemMap, String attr, String value){
        @SuppressWarnings("unchecked")
        Map<String, String> attributes = (LinkedHashMap<String, String>) navItemMap.get("actionAttributes");
        if(attributes == null){
            attributes = new LinkedHashMap<String, String>();
        }
        attributes.put(attr, value);
        navItemMap.put("actionAttributes", attributes);
    }
    
    /**
     ***************************************************************************************
     *
     * getActivityList
     * 
     * This method creates a list containing navItem maps for sub-navigation tabs (Golf, Tennis, Dining, etc).
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param con the database connection for the specified club
     * 
     *
     ***************************************************************************************
     **/
    public static List<Map<String,Object>> getActivityList(String club, Connection con, HttpServletRequest req) {

        Statement stmt = null;
        ResultSet rs = null;

        int count = 0;
        int i = 0;
        int foretees_mode = 0;
        int dining_mode = 0;
        int genrez_mode = 0;
        int flxrez_staging = 0;
        int dining_staging = 0;
        int ftConnect = Utilities.getSessionInteger(req, "ftConnect", 0); // get value (1 = yes)

        boolean memberDir = false;      // FT Connect tab indicators
        boolean staffDir = false;
        boolean showHome = false;
        boolean statements = false;
        boolean billpay = false;
        boolean newsletters = false;
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);    // get Responsive state
        
        List<Map<String,Object>> activityList = new ArrayList<Map<String,Object>>();
        
        String sGolf = "Golf";
        String sDining = "Dining";
        String sHome = "Home";
        String sHomeUrl = "Member_announce?activity=connect";     // Home tab for Connect Sites
        String sDirectories = "Directories";
        String sDirectory = "Directory";
        String sMemberDirectory = "Member Directory";
        String sMemberUrl = "Member_directory?list=member";
        String sStaffDirectory = "Staff Directory";
        String sStaffUrl = "Member_directory?list=staff";
        String sPayments = "Payments";
        String sMakePayments = "Make Payments";
        String sPaymentUrl = "Member_statements?type=billpay";
        String sStatements = "Statements";
        String sViewStatements = "View Statements";
        String sStatementsUrl = "Member_statements?type=statements";
        String sNewsletters = "Newsletters";
        String sNewslettersUrl = "Member_newsletters";

        String defaultHome = "Member_announce";
        
        String caller = Utilities.getSessionString(req, "caller", "");   // get caller's name
        
        // Club overrides
        if (club.equals("tontoverde")) {
            sGolf = "Golf Home ";
            sDining = "Club Events";
        }

        // Check if we should not create an activity menu
        //  Do not show menu tabs if FT Connect Premier (Flexscape)
        if (caller.equals(ProcessConstants.FT_PREMIER_CALLER) 
                || Utilities.getBitFromSession(req,"app_mode",ProcessConstants.APPMODE_HIDE_SUB_NAV)
                ) {
            return activityList;
        }
        
        if (ftConnect == 1) {      //  if club uses FT Connect (Standard)
            showHome = true;
            memberDir = true;
            staffDir = true;
            statements = true;
            billpay = true;
            newsletters = true;
        }

        try {

            // Get foretees_mode, dining_mode and # of activities from database
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT foretees_mode, organization_id, genrez_mode, flxrez_staging, dining_staging FROM club5 WHERE clubName <> '';");

            if (rs.next()) {
                foretees_mode = rs.getInt(1) > 0 ? 1 : 0;
                dining_mode = rs.getInt(2) > 0 ? 1 : 0;
                genrez_mode = rs.getInt(3) > 0 ? 1 : 0;
                flxrez_staging = rs.getInt(4) > 0 ? 1 : 0;
                dining_staging = rs.getInt(5) > 0 ? 1 : 0;
            }

            // only query activities is club is not is staging mode
            if (genrez_mode == 1 && flxrez_staging == 0) {

                rs = stmt.executeQuery(""
                        + "SELECT activity_id "
                        + "FROM activities "
                        + "WHERE parent_id = 0 AND enabled != 0");

                if (rs.next()) {

                    rs.last();
                    count = rs.getRow();   // get the number of activities found (excluding golf)
                }

            }

        } catch (Exception exc) {

            Utilities.logError("Common_skin: Error loading activity count. club=" + club + ", err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                stmt.close();
            } catch (Exception ignore) {
            }
        }

        String[] activity_names = new String[count];
        int[] activity_ids = new int[count];

        if (count > 0) {

            try {

                // Get activity names from database 
                stmt = con.createStatement();
                rs = stmt.executeQuery(""
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

                Utilities.logError("Common_skin: Error loading activity names. club=" + club + ", err=" + exc.toString());

            } finally {

                try {
                    rs.close();
                } catch (Exception ignore) {
                }

                try {
                    stmt.close();
                } catch (Exception ignore) {
                }
            }
        }
        
        
        //  Add the Home tab as the first one if Connect
        
        if (showHome == true) {      //  if Home tab required
           
            activityList.add(getNavItemMap(sHome, Utilities.getBaseUrl(req, ft_connect_home_id, club) + sHomeUrl, ft_connect_home_id));
        }
                

        //  add the tabs for each activity defined

        if (foretees_mode > 0) {
            // Add Golf activity, if we have Golf
            activityList.add(getNavItemMap(sGolf, Utilities.getBaseUrl(req, 0, club) + defaultHome, 0));
        }

        if (count > 0) {    // if any flex activities defined
            for (i = 0; i < count; i++) {
                activityList.add(getNavItemMap(activity_names[i], Utilities.getBaseUrl(req, activity_ids[i], club) + defaultHome, activity_ids[i]));
            }
        }

        if (dining_mode > 0 && dining_staging == 0) {             // if Dining in system
            // Add Dining activity
            activityList.add(getNavItemMap(sDining, Utilities.getBaseUrl(req, dining_activity_id, club) + defaultHome, dining_activity_id));
        }


        //****************************************************************************************************
        // Tabs for FT Connect testing
        //
        //     refer to neon custom style sheet for styles !!!!!!!!!!!!!
        //
        //
        //  If Connect site, then show the Home tab
        //
        if (memberDir == true || staffDir == true) {       // add Directory tab?
            if (memberDir == true && staffDir == true) {    // both directories ?
                List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>();
                subList.add(getNavItemMap(sMemberDirectory, Utilities.getBaseUrl(req, ft_connect_directory_id, club) + sMemberUrl));
                subList.add(getNavItemMap(sStaffDirectory, Utilities.getBaseUrl(req, ft_connect_directory_id, club) + sStaffUrl));
                activityList.add(getNavItemMap(sDirectories, subList, ft_connect_directory_id));
            } else {
                if (memberDir == true) {    // Member directory only ?
                    activityList.add(getNavItemMap(sDirectory, Utilities.getBaseUrl(req, ft_connect_directory_id, club) + sMemberUrl, ft_connect_directory_id));
                } else { // Staff directory only ?
                    activityList.add(getNavItemMap(sDirectory, Utilities.getBaseUrl(req, ft_connect_directory_id, club) + sStaffUrl, ft_connect_directory_id));
                }
            }
        }        // end of DIRECTORY Tab

        if (statements == true || billpay == true) {       // add Statements tab?
            if (statements == true && billpay == true) {    // both ?
                List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>();
                subList.add(getNavItemMap(sViewStatements, Utilities.getBaseUrl(req, ft_connect_statements_id, club) + sStatementsUrl));
                subList.add(getNavItemMap(sMakePayments, Utilities.getBaseUrl(req, ft_connect_statements_id, club) + sPaymentUrl));
                activityList.add(getNavItemMap(sStatements, subList, ft_connect_statements_id));
            } else {
                if (statements == true) {    // View Statements only ?
                    activityList.add(getNavItemMap(sStatements, Utilities.getBaseUrl(req, ft_connect_statements_id, club) + sStatementsUrl, ft_connect_statements_id));
                } else {
                    activityList.add(getNavItemMap(sPayments, Utilities.getBaseUrl(req, ft_connect_statements_id, club) + sPaymentUrl, ft_connect_statements_id));
                }
            }
        }        // end of STATEMENTS Tab

        if (newsletters == true) {
            activityList.add(getNavItemMap(sNewsletters, Utilities.getBaseUrl(req, ft_connect_newsletters_id, club) + sNewslettersUrl, ft_connect_newsletters_id));
        }

        return activityList;
    }
    
    
    /**
     ***************************************************************************************
     *
     * getNavItemMap
     * 
     * Used to build individual items for a navigation map.
     *
     * @name Name of the navigation item
     * @action A String (if this is a link), or a List containing more navItemMaps if this is sub navigation
     * @id activity_id, or null if no activity id (used for marking "selected")
     * @classes Optional List of Strings class names.
     *
     ***************************************************************************************
     **/
    
    
    private static Map<String, Object> getNavItemMap(String name, Object action ){
        String[] nstring = null;
        return getNavItemMap(name, action, null, nstring);
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action, String[] attributes){
        return getNavItemMap(name, action, null, null, attributes );
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action, int id ){
        List<String> classes = new ArrayList<String>();
        return getNavItemMap(name, action, id, classes );
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action, int id, String[] attributes ){
        List<String> classes = new ArrayList<String>();
        return getNavItemMap(name, action, id, classes, attributes );
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action , String sClasses){
        List<String> classes = Arrays.asList(sClasses.split(" "));
        return getNavItemMap(name, action, null, classes);
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action , String sClasses, String[] attributes){
        List<String> classes = new ArrayList<String>();
        if(sClasses != null){
            classes = Arrays.asList(sClasses.split(" "));
        }
        return getNavItemMap(name, action, null, classes, attributes);
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action, int id, String sClasses ){
        List<String> classes = new ArrayList<String>();
        if(sClasses != null){
            classes = Arrays.asList(sClasses.split(" "));
        }
        return getNavItemMap(name, action, id, classes);
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action, int id, String sClasses, String[] attributes ){
        List<String> classes = new ArrayList<String>();
        if(sClasses != null){
            classes = Arrays.asList(sClasses.split(" "));
        }
        return getNavItemMap(name, action, id, classes, attributes);
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action, Integer id, List<String> classes ){
        return getNavItemMap(name, action, id, classes, null);
    }
    
    private static Map<String, Object> getNavItemMap(String name, Object action, Integer id, List<String> classes, String[] attributes ){
        
        Map<String,Object> itemMap = new LinkedHashMap<String, Object>();
        itemMap.put("name", name); // Name (Title) of the item.
        itemMap.put("action", action); // Could be a String (url) or List (another level deep for navigation)
                                       // We do not allow an item to be both a url and have sub items
                                       // Because touch devices do not have hover!
        if(id != null){
            itemMap.put("id", id); // Could be an activity id, or null.  Used to check if this item is selected
        }
        if(classes == null){
            classes = new ArrayList<String>();
        }
        itemMap.put("classes", classes); // Optional list of CSS classes for this item
        itemMap.put("ifActive", "selected"); // Class to use, if active
        if(attributes != null){
            Map<String, String> attrMap = new LinkedHashMap<String,String>();
            for(int i = 0; i < attributes.length; i+=2){
                attrMap.put(attributes[i], attributes[i+1]);
            }
            itemMap.put("actionAttributes", attrMap);
        }
        return itemMap;
        
    }
    
    // Accepts a List<String> and returns it with the added string.
    // If the object passed is not a list or is null, a new list is created and returned with the string added.
    private static List<String> addToStringList(Object listObj, String data) {
        List<String> list = new ArrayList<String>();
        if(listObj != null && listObj instanceof List){
            @SuppressWarnings("unchecked")
            List<String> sentlist = (List<String>) listObj;
            list = sentlist;
        }
        if(data != null){
            list.add(data);
        }
        return list;
    } 
    

    /**
     ***************************************************************************************
     *
     * outputPageStart
     * 
     * This method will output the div tag for the main page.  This starts the white section.
     *
     * @param club the site name of the club currently being processed
     * @param out the PrintWriter to write out the html
     * @param req HttpServletRequest object
     *
     ***************************************************************************************
     **/
    public static void outputPageStart(String club, int activity_id, PrintWriter out, HttpServletRequest req) {
        
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);    // get Responsive state

        if(rwd){
            // If in resposive mode, output html that we've buffered
            StringBuilder pageHeadHtml = Utilities.getRequestStringBuilder(req, attrPageHeader, new StringBuilder());
            ArrayList<String> headerClass = new ArrayList<String>();
            out.print("<div id=\"pageHeader\" class=\"");
            headerClass.add(getSizeClass(Utilities.getRequestInteger(req, attrSubNavSize, 0)) + "ActivityList");
            headerClass.add(Utilities.getRequestBoolean(req, attrHasBanner, false)?"hasBanner":"noBanner");
            headerClass.add(Utilities.getRequestBoolean(req, attrHasMainNav, false)?"hasMainNav":"noMainNav");
            headerClass.add(Utilities.getRequestBoolean(req, attrHasSubNav, false)?"hasSubNav":"noSubNav");
            out.print(Utilities.implode(headerClass, " "));
            out.print("\">");
            out.print(pageHeadHtml.toString());
            out.print("</div>");
        }
        out.println("<div id=\"main_clear\"></div>");
        out.println("<div id=\"main\">");    // this div is terminated in outputPageEnd below                align=\"center\"

    }

    /**
     ***************************************************************************************
     *
     * outputPageEnd
     * 
     * This method will output the copyright notice, corporate logo and end tags to complete the page.
     *
     * @param club the site name of the club currently being processed
     * @param out the PrintWriter to write out the html
     * @param req HttpServletRequest object
     *
     ***************************************************************************************
     **/  
    public static void outputPageEnd(String club, int activity_id, PrintWriter out, HttpServletRequest req) {

        String caller = Utilities.getSessionString(req, "caller", ""); // get caller's name
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false); // responsive?

        out.print("</div>");             // terminates div tag in outputPageStart above
        if (!caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {      // do not include copyright & logo if FT Connect Premier (Flexscape)
            Calendar cal = new GregorianCalendar();       // get todays date
            int year = cal.get(Calendar.YEAR);
                out.print("<div id=\"footer\">");
                out.print("Copyright &copy; " + year + " ForeTees, LLC.&nbsp; All rights reserved.");
                if(!rwd){
                    out.print("<br /><br />"); // Leave br hack for "desktop" mode?
                }
                out.print("</div>");      // terminates the 'footer' div
        }
        if(rwd){
            out.println("</div>");    // terminates 'rwd_wrapper' div in outputBody above
        }
        out.print("</div>");    // terminates 'wrapper' div in outputBody above

        out.println("</body>");
        out.println("</html>");

    }

    public static void jsRedirect(String destination, PrintWriter out) {

        out.println("<script type=\"text/javascript\">");
        out.println("<!-- ");
        out.println("window.location.href=\"" + destination + "\";");
        out.println("// -->");
        out.println("</script>");

    }
    
    /**
     ***************************************************************************************
     *
     * outputBreadCrumb
     * 
     * This method will output the bread crumb  (Golf, Tennis, Dining, etc).
     *
     * @param club the site name of the club currently being processed
     * @param activity_id (Golf, Tennis, Dining, etc).
     * @param out the PrintWriter to write out the html
     * @param crumb the text to place in this crumb
     * @param req HttpServletRequest object
     *
     ***************************************************************************************
     **/
    public static void outputBreadCrumb(String club, int activity_id, PrintWriter out, String crumb, HttpServletRequest req) {
        
        String caller = Utilities.getSessionString(req, "caller", "");
        
        String home_link = "<a href=\"Member_announce\">Home</a><span>/</span>";

        /*if (activity_id == dining_activity_id) {

            // breadcrumb
            out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / " + crumb + "</div>");

        } else*/ 
        
        if (activity_id == ft_connect_home_id || activity_id == ft_connect_directory_id || 
                   activity_id == ft_connect_statements_id || activity_id == ft_connect_newsletters_id) {
            
            home_link = "<a href=\"Member_announce?activity=connect\">Home</a><span>/</span>";
            
    
            
        } else {

            //String homeParm = "";
            
            if (club.equals("tanoan")) {    // AGC club that wants to use the Announce Page
                
                home_link = "<a href=\"Member_announce?override=yes\">Home</a><span>/</span>";
                
            } else if (club.equals("tontoverde") && caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {
                
                if (activity_id == dining_activity_id) {
                    
                    home_link = "<a href=\"http://www.tontoverde.org/Dining-157.html\" target=\"_top\">Home</a><span>/</span>";
                
                } else {
                    
                    home_link = "<a href=\"http://www.tontoverde.org/Golf/Golf-Home-269.html\" target=\"_top\">Home</a><span>/</span>";
                
                }
                
            }
            
            
            //out.println("<div id=\"breadcrumb\"><a href=\"Member_announce" +homeParm+ "\">Home</a> / " + crumb + "</div>");
        }
        
        if(Utilities.getBitFromSession(req,"app_mode",ProcessConstants.APPMODE_HIDE_HOME_LINKS)){
            home_link = "";
        }
        
        out.println("<div id=\"breadcrumb\">" + home_link + crumb + "</div>");

    }

    /**
     ***************************************************************************************
     *
     * outputLogo
     * 
     * This method will output the foretees logo (Golf, Tennis, Dining, etc).
     *
     * @param club the site name of the club currently being processed
     * @param activity_id (Golf, Tennis, Dining, etc).
     * @param out the PrintWriter to write out the html
     * @param req HttpServletRequest object
     *
     ***************************************************************************************
     **/
    public static void outputLogo(String club, int activity_id, PrintWriter out, HttpServletRequest req) {
        
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);

        if(rwd) {
            // Respondive/Mobile mode
            out.print("<div id=\"main_ftlogo\" class=\"");
            out.print(getLogoClass(activity_id));
            out.print(" newLogo club_"+club);
            out.print("\"></div>");
            out.print("<div class=\"after_logo_fix\"></div>");
        } else {
            // Desktop/Newskin mode
            if (club.equals("mpccpb")) {

                out.println("<div id=\"main_ftlogo\"><img src=\"/mpccpb/images/logo2.jpg\" alt=\"Monterey Peninsula CC\"></div>");

            } else {
                String CDN = Common_Server.getContentServer();
                if (activity_id == 0) {

                    // ForeTees
                    out.println("<div id=\"main_ftlogo\"><img src=\"" + CDN + "/" + rev + "/assets/images/foretees_logo.png\" alt=\"ForeTees\"></div>");

                } else if (activity_id == dining_activity_id) {

                    // Dining
                    //out.println("<div id=\"main_ftlogo\"><img src=\"/v5/assets/images/foretees_logo.png\" alt=\"ForeTees\"></div>");
                    //out.println("<div id=\"dining_logo\"> </div><BR>");        // pull in the dining logo - defined in the stylesheet
                    out.println("<div id=\"dining_logo\"> </div>");

                } else {

                    // FlxRez
                    out.println("<div id=\"main_ftlogo\"><img src=\"" + CDN + "/" + rev + "/assets/images/FlxRezWebLogo.png\" alt=\"ForeTees\"></div>");

                }
            }        
            out.print("<div class=\"after_logo_fix\"></div>");
        }
    }

    /**
     ***************************************************************************************
     *
     * outputError
     * 
     * This method will output a common error page with a message supplied by caller.
     *
     * @param club the site name of the club currently being processed
     * @param clubName the full name of the club - used in the heading
     * @param activity_id (Golf, Tennis, Dining, etc).
     * @param heading the error heading (general description) to display
     * @param msg the custom error message (details) to display
     * @param action the action to peform or the url for the return (available action is 'close' - to close the page)
     * @param out the PrintWriter to write out the html
     * @param req the HttpServletRequest from the calling servlet (needed by outputHeader)
     *
     ***************************************************************************************
     **/
    public static void outputError(String club, String clubName, int activity_id, String heading, String msg,
            String action, PrintWriter out, HttpServletRequest req) {

        String title = "ForeTees Alert";

        outputHeader(club, activity_id, title, true, out, req);     // output the page start

        out.println("<body>");
        out.println("<div id=\"wrapper_login\" align=\"center\">");
        out.println("<div id=\"title\">" + clubName + "</div>");
        out.println("<div id=\"main_login\" align=\"center\">");
        out.println("<h1>Your request cannot be processed</h1>");
        out.println("<div class=\"main_message\">");
        out.println("<h2>" + heading + "</h2><br /><br />");
        out.println("<center><div class=\"sub_instructions\">");
        out.println(msg);
        out.println("<br /></div>");
        if (action.equalsIgnoreCase("close")) {
            out.println("<form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
        } else {
            out.println("<form action=\"" + action + "\" method=\"get\"><input type=\"submit\" value=\"Return\" id=\"submit\"/></form>");
        }
        out.println("</center></div></div>");

        outputPageEnd(club, activity_id, out, req);    // finish the page       
    }


    private static String getContentServerDEAD() {


        String baseURL = "";

        if (Common_Server.SERVER_ID == 50) {

            // development enviroment
            baseURL = "http://192.30.32.146";

        } else {

            // production enviroment


        }


        return baseURL;

    }
    
    public static String setUrlJsid(String url, HttpSession session) {
        /*
        if(session != null && session.getAttribute("premier_referrer") != null){
            String[] parts = url.split("\\?");
            String[] parts2 = parts[0].split("#");
            parts2[0] += ";jsessionid=" + session.getId();
            if(parts.length > 1){
                parts2[0] += "?" + parts[1];
            }
            if(parts2.length > 1){
                parts2[0] += "#" + parts2[1];
            }
            return parts2[0];
        }
         * 
         */
        return url;
    }

}
