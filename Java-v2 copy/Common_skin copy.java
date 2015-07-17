
/***************************************************************************************
 *   Common_skin:  This servlet will build portions of the member pages (new skin).
 *
 *
 *
 *   created:  5/03/2011   
 *
 *   last updated:       ******* keep this accurate *******
 *
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
    
    

    static final String sitewide_css =      (ProcessConstants.SERVER_ID == 4) ? "sitewide.css" : "sitewide-20130908.css";
    static final String sitewide_rwd_css =      (ProcessConstants.SERVER_ID == 4) ? "sitewide_rwd.css" : "sitewide_rwd-20130908.css";
    static final String dining_css =        (ProcessConstants.SERVER_ID == 4) ? "sitewide_dining.css" : "sitewide_dining-20130908.css";
    static final String dining_rwd_css =        (ProcessConstants.SERVER_ID == 4) ? "sitewide_dining_rwd.css" : "sitewide_dining_rwd.css";
    static final String flxrez_css =        (ProcessConstants.SERVER_ID == 4) ? "sitewide_flxrez.css" : "sitewide_flxrez-20130410.css";
    static final String flxrez_rwd_css =        (ProcessConstants.SERVER_ID == 4) ? "sitewide_flxrez_rwd.css" : "sitewide_flxrez_rwd.css";
    static final String premier_css =        (ProcessConstants.SERVER_ID == 4) ? "premier.css" : "premier.css";
    static final String global_js =         (ProcessConstants.SERVER_ID == 4) ? "foretees-global.js" : "foretees-global-20130908.js";
    static final String foreTeesMemberCalendar_js = (ProcessConstants.SERVER_ID == 4) ? "jquery.foreTeesMemberCalendar.js" : "jquery.foreTeesMemberCalendar-20130908.js";
    static final String foreTeesServerClock_js = (ProcessConstants.SERVER_ID == 4) ? "jquery.foreTeesServerClock.js" : "jquery.foreTeesServerClock-20120627.js";
    static final String foreTeesModal_js = (ProcessConstants.SERVER_ID == 4) ? "jquery.foreTeesModal.js" : "jquery.foreTeesModal-20130908.js";
    static final String foreTeesSlot_js = (ProcessConstants.SERVER_ID == 4) ? "jquery.foreTeesSlot.js" : "jquery.foreTeesSlot-20130908.js";
    static final String activity_indicator_js = (ProcessConstants.SERVER_ID == 4) ? "jquery.activity-indicator-1.0.0.min.js" : "jquery.activity-indicator-1.0.0.min-20120702.js";
    static final String jQuery_js = (ProcessConstants.SERVER_ID == 4) ? "jquery-1.10.2.min.js" : "jquery-1.10.2.min.js"; // jquery-1.7.1.min.js
  //static final String jQueryMigrate_js = (ProcessConstants.SERVER_ID == 4) ? "jquery-migrate-1.2.1.min.js" : "jquery-migrate-1.2.1.min.js";
    static final String jQueryMigrate_js = (ProcessConstants.SERVER_ID == 4) ? "jquery-migrate-1.2.1.js" : "jquery-migrate-1.2.1.min.js";
    static final String jQueryUI_js = (ProcessConstants.SERVER_ID == 4) ? "jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js" : "jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min.js";
    
    // "jquery.ui.touch-punch.min.js" is used to add touch support to some jqueryui elements. 
    // see: http://touchpunch.furf.com
    //  It will be needed until JqueryUI adds touch
    static final String jQueryUITouch_js = (ProcessConstants.SERVER_ID == 4) ? "jquery.ui.touch-punch.min.js" : "jquery.ui.touch-punch.min.js";
    static final String jQueryUI_css = (ProcessConstants.SERVER_ID == 4) ? "jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.min.css" : "jquery-ui-1.10.3.custom/css/smoothness/jquery-ui-1.10.3.custom.min.css"; // jquery-ui/css/smoothness/jquery-ui-1.8.16.custom.css
    
    static final String jQueryMobile_js = (ProcessConstants.SERVER_ID == 4) ? "jquery.mobile-1.3.2.custom/js/jquery-ui-1.10.3.custom.min.js" : "jquery.mobile-1.3.2.custom/js/jquery-ui-1.10.3.custom.min.js";
    static final String jQueryMobile_css = (ProcessConstants.SERVER_ID == 4) ? "jquery.mobile-1.3.2.custom/css/smoothness/jquery-ui-1.10.3.custom.min.css" : "jquery.mobile-1.3.2.custom/css/smoothness/jquery-ui-1.10.3.custom.min.css";
    
    static final String slash = "/";

    
    /**
     ***************************************************************************************
     *
     * getScripts
     *
     * This method will output the HTML tags that pull in the tylesheets, and scripts.
     *
     ***************************************************************************************
     **/
    public static String getScripts(String club, int activity_id, HttpSession session, HttpServletRequest req, boolean includeEditor) {
        
        Connection con = null;

        String CDN = Common_Server.getContentServer();

        String cssStart = "<link rel=\"stylesheet\" href=\"" + CDN;
        String cssEnd = "\" type=\"text/css\" />\n";
        String jsStart = "<script type=\"text/javascript\" src=\"" + CDN;
        String jsEnd = "\"></script>\n";

        String servletPath = "";
        String servletFile = "";
        String basePath = "/usr/local/tomcat/webapps";
        if (Common_Server.SERVER_ID == 11 || Common_Server.SERVER_ID == 12 || Common_Server.SERVER_ID == 13 ||
            Common_Server.SERVER_ID == 21 || Common_Server.SERVER_ID == 22 || Common_Server.SERVER_ID == 23 ||
            Common_Server.SERVER_ID == 31 || Common_Server.SERVER_ID == 32 || Common_Server.SERVER_ID == 33 ) basePath = "/srv/webapps";
        
        String baseCssPath = "/assets/stylesheets";
        String baseJsPath = "/assets/scripts";
        String basejQuery = "/assets/jquery";

        if (req != null) {
            servletPath = req.getServletPath();
            //basePath = req.getRealPath("");
            String temp[];
            temp = servletPath.split(slash);
            servletFile = temp[temp.length - 1];
        }


        String cssPath = slash + rev + baseCssPath;
        String clubCssPath = slash + club + baseCssPath;
        String jsPath = slash + rev + baseJsPath;
        String clubJsPath = slash + club + baseJsPath;
        String customPath = cssPath + "/custom";        // custom templates

        String servletStyles = "";
        String clubStyles = "";
        //String clubDinningStyles = "";
        //String clubActivityStyles = "";
        String clubServletStyles = "";
        String stylesheet = "";
        String stylesheetOverride = "";
        String flxrezStyles = "";
        String diningStyles = "";
        String customStyles = "";
        String premierStyles = "";

        String clubJs = "";
        String servletJs = "";
        String diningJs = "";
        String clubDiningJs = "";
        String flxRezJs = "";
        String clubFlxRezJs = "";
        String clubServletJs = "";
        String JqueryMobileJs = "";
        String JqueryMobileCss = "";
        String JqueryMigrateJs = "";
        String JqueryTouchJs = "";
        String caller = "";
        
        String editorInclude = "";
        
        String user = "";
        
        Boolean rwd = false;

        if(session != null) {
            
            con = SystemUtils.getCon(session);         // get DB connection to check for custom css template for this club
            
            user = (String) session.getAttribute("user");
            rwd = (Boolean) session.getAttribute("rwd");
            caller = (String)session.getAttribute("caller");

            if (caller == null) {
                caller = "";
            }

            if (rwd == null) { 
                rwd = false;
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
        if(includeEditor){
            editorInclude = "<script type=\"text/javascript\" src=\"" + ((CDN.equals("")) ? "" : CDN) + "/" + rev + "/assets/jquery/tiny_mce/jquery.tinymce.js\"></script>\n";
          //editorInclude = "<script type=\"text/javascript\" src=\"../assets/jquery/tiny_mce/jquery.tinymce.js\"></script>\n";
        }
        
        String proshopCss = "";
        if(user.startsWith("proshop")){
            proshopCss = "<link rel=\"stylesheet\" href=\"" + ((CDN.equals("")) ? "" : CDN) + "/" + rev + "/assets/stylesheets/proshop_transitional.css\" type=\"text/css\" />\n";
          //proshopCss = "<link rel=\"stylesheet\" href=\"../assets/stylesheets/proshop_transitional.css\" type=\"text/css\" />\n";
        }

        // get servlet golf css file, if any
        servletStyles = Utilities.findFileToString(basePath, cssPath, "servlet_" + servletFile + ".css", cssStart, cssEnd);

        // include the base css
        if(rwd){
            stylesheet += cssStart + cssPath + slash + sitewide_rwd_css + cssEnd;
            JqueryMobileJs = jsStart + slash + rev + basejQuery + slash + jQueryMobile_js + jsEnd;
            JqueryMobileCss = cssStart + cssPath + slash + jQueryMobile_css + cssEnd;
        } else {
            stylesheet += cssStart + cssPath + slash + sitewide_css + cssEnd;
        }
        
        // Check if we should include jquery migrate
        if(jQueryMigrate_js.length() > 0){
            JqueryMigrateJs = jsStart + slash + rev + basejQuery + slash + jQueryMigrate_js + jsEnd;
        }
        
        // Check if we should include jquery migrate
        if(jQueryUITouch_js.length() > 0){
            JqueryTouchJs = jsStart + slash + rev + basejQuery + slash + jQueryUITouch_js + jsEnd;
        }

        // get club golf css file, if any
        clubStyles = Utilities.findFileToString(basePath, clubCssPath, "club.css", cssStart, cssEnd);
        // get club servlet golf css file, if any
        clubServletStyles = Utilities.findFileToString(basePath, clubCssPath, "servlet_" + servletFile + ".css", cssStart, cssEnd);
        // get per club golf/sitewide override css file, if any
        stylesheetOverride = Utilities.findFileToString(basePath, clubCssPath, "override.css", cssStart, cssEnd);
        clubJs = Utilities.findFileToString(basePath, clubJsPath, "club.js", jsStart, jsEnd);

        servletJs += Utilities.findFileToString(basePath, jsPath, "servlet_" + servletFile + ".js", jsStart, jsEnd);
        clubServletJs += Utilities.findFileToString(basePath, clubJsPath, "servlet_" + servletFile + ".js", jsStart, jsEnd);
        
        //  get custom template from club5 if selected for this club
        if (con != null) {

            customStyles = Utilities.getCustomStyles(con);  // get file name from club5 (change name if styles are updated!!!!)
            
            if (!customStyles.equals("")) {
                
                customStyles = cssStart + customPath + slash + customStyles + cssEnd;  // currently = v5/assets/stylesheets/custom/xyz.css
            }
        }

        if (activity_id == dining_activity_id || user.equals(ProcessConstants.DINING_USER)) {

            // include the base css
            //stylesheet += cssStart + cssPath + slash + sitewide_css + cssEnd;
            // get standard Dining stylesheet
            if(rwd){
                diningStyles += cssStart + cssPath + slash + dining_rwd_css + cssEnd;
            }else{
                diningStyles += cssStart + cssPath + slash + dining_css + cssEnd;
            }
            // get dining servlet css file, if any
            servletStyles += Utilities.findFileToString(basePath, cssPath, "servlet_" + servletFile + "_dining_.css", cssStart, cssEnd);
            // get club dinning css file, if any
            clubStyles += Utilities.findFileToString(basePath, clubCssPath, "dining.css", cssStart, cssEnd);
            // get club servlet dinning css file, if any
            clubServletStyles += Utilities.findFileToString(basePath, clubCssPath, "servlet_" + servletFile + "_dining.css", cssStart, cssEnd);
            // get per club dinning override css fil, if any
            stylesheetOverride += Utilities.findFileToString(basePath, clubCssPath, "override_dining.css", cssStart, cssEnd);
            // Get dining js, if any
            diningJs = Utilities.findFileToString(basePath, jsPath, "dining.js", jsStart, jsEnd);
            clubDiningJs = Utilities.findFileToString(basePath, clubJsPath, "club_dining.js", jsStart, jsEnd);

        } else if (activity_id > 0) {

            // If FlxRez, include standard stylesheet for it
            // include the base css
            //stylesheet += cssStart + cssPath + slash + sitewide_css + cssEnd;
            //String flxres_stylesheet = "";
            if(rwd){
                flxrezStyles += cssStart + cssPath + slash + flxrez_rwd_css + cssEnd;
            }else{
                flxrezStyles += cssStart + cssPath + slash + flxrez_css + cssEnd;
            }
            
            // get FlxRez servlet css files, if any
            servletStyles += Utilities.findFileToString(basePath, cssPath, "servlet_" + servletFile + "_flxrez_.css", cssStart, cssEnd);
            // get club servlet FlxRez css file, if any
            clubServletStyles += Utilities.findFileToString(basePath, clubCssPath, "servlet_" + servletFile + "_flxrez.css", cssStart, cssEnd);
            // get club activity_id css file, if any
            clubStyles += Utilities.findFileToString(basePath, clubCssPath, "flxrez_" + activity_id + ".css", cssStart, cssEnd);
            // get per club FlxRez override css file, if any
            stylesheetOverride += Utilities.findFileToString(basePath, clubCssPath, "override_flxrez.css", cssStart, cssEnd);
            // Get FlxRez js, if any
            //clubJs += Utilities.findFileToString(basePath, clubJsPath, "club.js", jsStart, jsEnd);
            flxRezJs = Utilities.findFileToString(basePath, jsPath, "flxrez.js", jsStart, jsEnd);
            clubFlxRezJs = Utilities.findFileToString(basePath, clubJsPath, "club_flxrez.js", jsStart, jsEnd);

        } //else {
        
        if (caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {
            premierStyles += cssStart + cssPath + slash + premier_css + cssEnd;
            premierStyles += Utilities.findFileToString(basePath, clubCssPath, "premier.css", cssStart, cssEnd);
        }
        
        
            


        //}

        

        // If the base stylesheet was overriden, do it.
        if (stylesheetOverride.length() > 0) {
            stylesheet = stylesheetOverride;
            flxrezStyles = ""; // if we had a supplemental flxrez sitesheet, turn it off and use the override.
        }
        
        String metaVars = "";
        java.util.Date serverTime = new java.util.Date();
        Long timeStamp = serverTime.getTime();
        // If we have a session, set some parameters for use in javascript
        if (session != null) {
            
            Map<String, Object> assetsNameMap = new LinkedHashMap<String, Object>();
            assetsNameMap.put("sitewide_css", sitewide_css);
            assetsNameMap.put("dining_css", dining_css);
            assetsNameMap.put("member_calendar_js", foreTeesMemberCalendar_js);
            assetsNameMap.put("fortees_global_js", global_js);

            Map<String, Object> metaVarsMap = new LinkedHashMap<String, Object>();
            metaVarsMap.put("user", (String) session.getAttribute("user"));
            metaVarsMap.put("caller", (String) session.getAttribute("caller"));
            metaVarsMap.put("club", club);
            metaVarsMap.put("zipcode", (String) session.getAttribute("zipcode"));
            metaVarsMap.put("session_timeout", session.getMaxInactiveInterval());
            metaVarsMap.put("session_time", timeStamp);
            metaVarsMap.put("assets_name_map", assetsNameMap);
            //metaVarsMap.put("jsid", session.getId());
            if (session.getAttribute("premier_referrer") != null) metaVarsMap.put("premier_referrer", (String) session.getAttribute("premier_referrer"));
            if (session.getAttribute("sso_tpa_mode") != null) metaVarsMap.put("sso_tpa_mode", (String) session.getAttribute("sso_tpa_mode"));
            if (session.getAttribute("app_mode") != null) metaVarsMap.put("app_mode", (Integer) session.getAttribute("app_mode"));
            
            Gson gson_obj = new Gson();
            metaVars = "<meta name=\"ft-session-parms\" content=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(metaVarsMap)) + "\" />\n";
        }
        
        //
        // *** Include CSS (order is important)
        // Plugin CSS first

        String scripts = ""
                + "<meta name=\"application-name\" content=\"ForeTees\" />\n"
                + "<meta name=\"ft-server-id\" content=\"" + ProcessConstants.SERVER_ID + "\" />\n"
                + "<meta name=\"ft-activity-id\" content=\"" + activity_id + "\" />\n"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n"
                + "<meta name=\"viewport\" content=\"width=device-width, height=device-height\" />\n"
                + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" />\n" // Force IE9 to use latest rendering mode and enable Chrome Frame if user has it installed
                //+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\">" // try to work-around IOS5 caching bug
                //+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">\n" // try to work-around IOS5 caching bug
                + metaVars
                + cssStart + slash + rev + basejQuery + slash + jQueryUI_css + cssEnd
                + JqueryMobileCss
                + cssStart + slash + rev + basejQuery + slash + "fancybox/jquery.fancybox-1.3.4.css" + cssEnd
                + cssStart + slash + rev + basejQuery + slash + "jquery.loading.1.6.css" + cssEnd
                /*
                + "<link rel=\"stylesheet\" href=\"/" + rev + "/assets/jquery/"+jQueryUI_css+"\" type=\"text/css\" />\n"
                + "<link rel=\"stylesheet\" href=\"/" + rev + "/assets/jquery/fancybox/jquery.fancybox-1.3.4.css\" type=\"text/css\" />\n"
                + "<link rel=\"stylesheet\" href=\"/" + rev + "/assets/jquery/jquery.loading.1.6.css\" type=\"text/css\" />\n"
                */
                // Then the base "sitewide" (then dining, flxrez, and the club ovveride of each) CSS
                + stylesheet
                // Then FlxRez css, if applicable
                + flxrezStyles
                // Then Dining css, if applicable
                + diningStyles
                // Then per servlet css, if any (using FlxRez per servlet css if applicable)
                + servletStyles
                // Then a custom template if selected for this club (from club5 table)
                + customStyles
                // Then per club css, if any (using FlxRez+activity_id per club css if applicable)
                + clubStyles
                // Then per club, per servlet css, if any (using FlxRez+activity_id per club, per servlet css if applicable)
                + clubServletStyles
                // Proshop transitional CSS
                + proshopCss
                // global and optional club css if we're in pemier mode
                + premierStyles
                //  *** Include javascript (order is important)
                // IE 8 and below HTML5 tag compatibility shim
                //+ "<!--[if lt IE 9]>\n"
                //+ "<script src=\"//html5shim.googlecode.com/svn/trunk/html5.js\"></script>\n"
                //+ "<![endif]-->\n" 
                //+ "<!--[if lt IE 7]>\n"
                //+ "<script src=\"http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE7.js\"></script>\n"
                //+ "<![endif]-->\n" 
                // Load Jquery core first
                // TODO: We could load this from google's CDN, test for it and then load it locally if google failed
                // "<script>!window.jQuery && document.write('<script src=\"/assets/jquery/jquery-1.7.1.min.js\"><\/script>')</script>\n"
                + jsStart + slash + rev + basejQuery + slash + jQuery_js + jsEnd
                + JqueryMigrateJs
                + jsStart + slash + rev + basejQuery + slash + jQueryUI_js + jsEnd
                + JqueryTouchJs
                + JqueryMobileJs
              //+ jsStart + ".." + basejQuery + "/" + jQuery_js + jsEnd
              //+ "<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/jquery-1.7.1.min.js\"></script>\n"

                // Then json compatibility for older browsers
                + jsStart + slash + rev + baseJsPath + slash + "json2.js" + jsEnd
                // Then Jquery plugins
                + jsStart + slash + rev + basejQuery + slash + "jquery.loading.1.6.4.js" + jsEnd
                + jsStart + slash + rev + basejQuery + slash + "fancybox/jquery.fancybox-1.3.4.pack.js" + jsEnd
                + jsStart + slash + rev + basejQuery + slash + "fancybox/jquery.easing-1.3.pack.js" + jsEnd
                + jsStart + slash + rev + basejQuery + slash + "jquery.cycle.js" + jsEnd
                + jsStart + slash + rev + basejQuery + slash + activity_indicator_js + jsEnd
                //+ "<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/jquery.idle-timer.js\"></script>\n"
                //+ "<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/jquery.idletimeout.js\"></script>\n"
                // Then foreTees' custom Jquery plugins
                + jsStart + slash + rev + basejQuery + slash + foreTeesMemberCalendar_js + jsEnd
                + jsStart + slash + rev + basejQuery + slash + foreTeesServerClock_js + jsEnd
                + jsStart + slash + rev + basejQuery + slash + foreTeesModal_js + jsEnd
                + jsStart + slash + rev + basejQuery + slash + foreTeesSlot_js + jsEnd

                /*
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/"+jQueryUI_js+"\"></script>\n"
                // Then json compatibility for older browsers
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/scripts/json2.js\"></script>\n"
                // Then Jquery plugins
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/jquery.loading.1.6.4.js\"></script>\n"
                //+ "<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/jquery.idle-timer.js\"></script>\n"
                //+ "<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/jquery.idletimeout.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/fancybox/jquery.fancybox-1.3.4.pack.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/fancybox/jquery.easing-1.3.pack.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/jquery.cycle.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/" + activity_indicator_js + "\"></script>\n"
                // Then foreTees' custom Jquery plugins
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/" + foreTeesMemberCalendar_js + "\"></script>\n"
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/" + foreTeesServerClock_js +  "\"></script>\n"
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/" + foreTeesModal_js + "\"></script>\n"
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/jquery/" + foreTeesSlot_js + "\"></script>\n"
                */

                // Then foreTees default javascript 
                //"<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/ftms.js\"></script>\n" +
                // ** NOTE: should contents of "web utilities/foretees.js" be moved to "/assets/scripts/foretees-global.js"?
                + "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/web%20utilities/foretees.js\"></script>\n"
                + editorInclude
                + jsStart + slash + rev + baseJsPath + slash + global_js + jsEnd
              //+ "<script type=\"text/javascript\" src=\"" + CDN + "/" + rev + "/assets/scripts/" + global_js + "\"></script>\n"
                + flxRezJs
                + diningJs
                // Then foreTees per servlet javascript override, if any
                + servletJs
                // Then per club javascript override, if any
                + clubJs
                + clubDiningJs
                + clubFlxRezJs
                + "<!--[if lt IE 7]>\n"
                + jsStart + slash + rev + baseJsPath + slash + "ie6_compatibility.js" + jsEnd
              //+ "<script src=\"/" + rev + "/assets/scripts/ie6_compatibility.js\"></script>\n"
                + cssStart + cssPath + slash + "ie6_compatibility.css" + cssEnd
              //+ "<link rel=\"stylesheet\" href=\"/" + rev + "/assets/stylesheets/ie6_compatibility.css\" type=\"text/css\" />\n"
                + "<![endif]-->\n"
                + "<!--[if lt IE 8]>\n"
                + jsStart + slash + rev + baseJsPath + slash + "ie7_compatibility.js" + jsEnd
              //+ "<script src=\"/" + rev + "/assets/scripts/ie6_compatibility.js\"></script>\n"
              //  + cssStart + cssPath + slash + "ie6_compatibility.css" + cssEnd
              //+ "<link rel=\"stylesheet\" href=\"/" + rev + "/assets/stylesheets/ie6_compatibility.css\" type=\"text/css\" />\n"
                + "<![endif]-->\n";;
        
        return scripts;

    }
    
    /**
     ***************************************************************************************
     *
     * outputHeader
     *
     * This method will output the start of the page (doctype, html, head, title,
     * stylesheets, and scripts)
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param title the value to use in the title tag for the page being built
     * @param out the PrintWriter to write out the html
     *
     ***************************************************************************************
     **/
    public static void outputHeader(String club, int activity_id, String title, PrintWriter out) {

        outputHeader(club, activity_id, title, true, out, null, 0, "");

    }

    /**
     ***************************************************************************************
     *
     * outputHeader
     *
     * This method will output the start of the page (doctype, html, head, title,
     * stylesheets, and scripts) and can conditionally leave the head element open
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param title the value to use in the title tag for the page being built
     * @param close_head boolean flag to indicate if the head element should be closed or left open
     * @param out the PrintWriter to write out the html
     *
     ***************************************************************************************
     **/
    public static void outputHeader(String club, int activity_id, String title, boolean close_head, PrintWriter out) {

        outputHeader(club, activity_id, title, close_head, out, null, 0, "");

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
    public static void outputBody(String club, int activity_id, PrintWriter out) {

        String wrapper = "<div id=\"top\"></div>"
                + "<div data-role=\"page\" id=\"wrapper\">";       // this is terminated in outputPageEnd below          align=\"center\"

        out.println("<body>");
        out.println(wrapper);
    }


    /**
     ***************************************************************************************
     *
     * outputTopNav
     *
     * This method will output the top navigation panel (main menus) for the Dining system.
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param out the PrintWriter to write out the html
     * @param con the database connection for the specified club
     *
     ***************************************************************************************
     **/
    public static void outputTopNav(HttpServletRequest req, String club, int activity_id, PrintWriter out, Connection con) {
        
        HttpSession session = null;

        if (req != null) {
            session = req.getSession(false);  // Get user's session object (no new one)
        }
                
        outputTopNav(req, session, club, activity_id, out, con);
    }
        
    public static void outputTopNav(HttpServletRequest req, HttpSession session, String club, int activity_id, PrintWriter out, Connection con) {
        
        if(Utilities.getBitFromSession(session,"app_mode",ProcessConstants.APPMODE_HIDE_TOP_NAV)){
            return;
        }
        
        String navHtml = "";

        String caller = Utilities.getSessionString(session, "caller", "");   // get caller's name
        String mship = Utilities.getSessionString(session, "mship", "");    // get member's mship type
        String mtype = Utilities.getSessionString(session, "mtype", "");    // get member type
        
        boolean rwd = Utilities.getSessionBoolean(session, "rwd", false);    // get Responsive state

        
        List<Map> activityNavList = getActivityNavList(req, activity_id, con, club);
        // Only show menu if there are menu items to show
        if(activityNavList.size() > 0){
            navHtml = "<div id=\"topnav_container\"><div id=\"topnav\"><div class=\"topnav_open\"></div><ul>"
                    +getNavListHtml(activityNavList, activity_id, club, "<li class=\"topnav_separation\"></li>")+
                    "</ul><div class=\"topnav_close\"></div><div style=\"clear:both;\"></div></div></div>";
        }
        
        out.print(navHtml);

    }
    
    public static List<Map> getActivityNavList(HttpServletRequest req, int activity_id, Connection con, String club) {

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
        
        String caller = Utilities.getSessionString(req, "caller", "");   // get caller's name
        String mship = Utilities.getSessionString(req, "mship", "");    // get member's mship type
        String mtype = Utilities.getSessionString(req, "mtype", "");    // get member type
        
        boolean rwd = Utilities.getSessionBoolean(req, "rwd", false);    // get Responsive state
        
        List<Map> navList = new ArrayList<Map>();
        
        String rootItemCss = "topnav_item";
        String rightItemCss = "topnav_right_item";
        
        
        if (caller.equals(ProcessConstants.FT_PREMIER_CALLER)) showLogout = false;      // do not include the Logout tab if FT Connect Premier (Flexscape)

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
            
            if (activity_id == 7 || activity_id == 8 || activity_id == 18) {    // Mbr Services
                showReservationMenu = false;
            }
        }
        
        if (club.equals("overlakegcc") || club.equals("wingedfoot") || club.equals("austincountryclub") 
                || (club.equals("ballantyne") && activity_id == 1) 
                || (club.equals("minikahda") && activity_id == dining_activity_id)) {
            
            showEventsMenu = false;
        }
        
        if (club.equals("roccdallas") || club.equals("blackhawk") || club.equals("rollinghillsgc") || (club.equals("mayfieldsr") && activity_id == 1)) {
            showLessonMenu = false;
        }
        
        if (club.equals("willowridgecc") || club.equals("blackstone") || club.equals("coloradospringscountryclub") || club.equals("moselemsprings")) {
            showEmailMenu = false;
        }
        
        if (club.equals("stcloudcc")) {
            showCalendarMenu = true;
        }
        
        if (club.equals("ccyork")) {
            showHandicapMenu = false;
        }
        
        if (club.equals("interlachen") && activity_id == 11) {
            blockAllAccess = true;
        }
        
        if (club.equals("hillwoodcc") && activity_id != 1 && mship.equals("Whitworth - Athletic")) {
            blockAllAccess = true;
        }
        
        if (!Utilities.checkMemberAccess(mship, mtype, activity_id, con)) {
            blockAllAccess = true;
        }
        
        
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
        String msubtype = "";

        if (club.equals("philcricket") || (activity_id == 0 && showHandicapMenu == true)) {  // Philly Cricket or Golf user - get member subtype
            
            //if (session != null) {         // get username of member, then get member subtype 
                username = Utilities.getSessionString(req, "user", username);
                msubtype = Utilities.getSubtypeFromUsername(username, con);
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
            List<Map> itemList = new ArrayList<Map>();
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
            }
            eventsMenu = getNavItemMap(events_title, events_link, rootItemCss);
            
        }
        
        // Lessons Menu
        Map<String,Object> lessonMenu = new LinkedHashMap<String, Object>();
        {
            List<Map> itemList = new ArrayList<Map>();
            itemList.add(getNavItemMap("Individual Lessons", "Member_lesson"));
            if (!club.equals("castlepines")) {
                itemList.add(getNavItemMap("Group Lessons", "Member_lesson?group=yes"));
            }
            itemList.add(getNavItemMap("View Pros' Bios", "Member_lesson?bio=yes"));
            lessonMenu = getNavItemMap("Lessons", itemList, rootItemCss);
        }
        
        // Search menu
        Map<String,Object> searchMenu = new LinkedHashMap<String, Object>();
        if(activity_id == dining_activity_id) {
            searchMenu = getNavItemMap("Search", "Dining_home?search", rootItemCss);
        } else {
            List<Map> itemList = new ArrayList<Map>();
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
            List<Map> itemList = new ArrayList<Map>();
            itemList.add(getNavItemMap("Send Email", "Member_email"));
            itemList.add(getNavItemMap("Manage Distribution Lists", "Member_email?manage_distribution_lists"));
            emailMenu = getNavItemMap("Email", itemList, rootItemCss);
        }
        
        // Partner menu
        Map<String,Object> partnerMenu = new LinkedHashMap<String, Object>();
        if (Utilities.isGuestTrackingConfigured(activity_id, con) && activity_id == 0) {
            List<Map> itemList = new ArrayList<Map>();
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
                    List<Map> itemList = new ArrayList<Map>();
                    itemList.add(getNavItemMap("Make, Change, or View Notifications", "Member_select"));
                    itemList.add(getNavItemMap("Today's Notifications", "MemberTLT_sheet?index=0"));
                    itemList.add(getNavItemMap("My Activites / Calendar", "Member_teelist"));
                    itemList.add(getNavItemMap("My Activites / List", "Member_teelist_list"));
                    navList.add(getNavItemMap("Notifications", itemList, rootItemCss));
                } else if (!isTLT && showTeeTimesMenu) {
                    List<Map> itemList = new ArrayList<Map>();
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
                    } else {
                        navList.add(lessonMenu);
                    }
                }

                if (showHandicapMenu) {
                    List<Map> itemList = new ArrayList<Map>();
                    itemList.add(getNavItemMap("Post a Score", "Member_handicaps?todo=post"));
                    itemList.add(getNavItemMap("View Handicaps and Scores", "Member_handicaps?todo=view"));
                    if (showHandicapReports) {
                        itemList.add(getNavItemMap("Peer Review Report", "Proshop_report_handicap"));
                        itemList.add(getNavItemMap("Missed Postings Report", "Proshop_report_handicap?todo=nonPosters"));
                    }
                    navList.add(getNavItemMap("Handicaps", itemList, rootItemCss));

                }

                break; // End of Golf Menus

            // Dining Menus
            case dining_activity_id:
                
                if (showReservationMenu) {
                    List<Map> itemList = new ArrayList<Map>();
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
                    List<Map> itemList = new ArrayList<Map>();
                    itemList.add(getNavItemMap("Make, Change, or View Reservations<", "Member_gensheets"));
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
        
        if (showMobileHelp) {
            navList.add(getNavItemMap("Mobile?", "Common_mobile_help", rightItemCss, new String[]{"target", "_blank"}));
        }
        if (showHelpMenu) {
            navList.add(getNavItemMap("Help?", "Member_help", rightItemCss, new String[]{"target", "_blank"}));
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
     *
     ***************************************************************************************
     **/
    public static void outputBanner(String club, int activity_id, String clubName, String zip, PrintWriter out) {  // DO NOT USE THIS GOING FORWARD!!!

        HttpServletRequest req = null;

        outputBanner(club, activity_id, clubName, zip, out, req);
    }
        
    public static void outputBanner(String club, int activity_id, String clubName, String zip, PrintWriter out, HttpServletRequest req) {

        HttpSession session = null;
        
        int ftConnect = 0;                    // FT Connect club indicator

        if (req != null) {
            session = req.getSession(false);  // Get user's session object (no new one)
        }
        
        String caller = "";            // website caller
        String fullName = "";          // user's full name
        
        if (session != null) {
            caller = (String)session.getAttribute("caller");   // get caller's name
            fullName = (String)session.getAttribute("name");   // get user's name
            
            if (session.getAttribute("ftConnect") != null) {   //  if ftConnect included in session (set in Login.memberUser only)
                
                ftConnect = (Integer)session.getAttribute("ftConnect");   // get value (1 = yes)
            }
        }
         
        out.print("<div class=\"banner_container\">");
        
        if (caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {      // if FT Connect Premier (Flexscape)
            
            out.print("<BR /><BR />");         // skip the club name/link
            
        } else {
        
            if (club.equals("mirabel")) {

                out.println("<BR /><div id=\"title\">Welcome to FlexRez, your source for everything Mirabel.</div>");   // This is exactly what they wanted (mis-spelled FlxRez and all) for all activities - Golf, FlxRez & Dining!

            } else {

                //out.println("<div id=\"title\"> " + clubName + " </div>");

                if (ftConnect == 1) {      //  if club uses FT Connect

                        out.println("<div><a id=\"title\" href=\"Member_announce?activity=connect\">" + clubName + "</a></div>");

                } else {

                    //if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                        //out.println("<div><a id=\"title\" href=\"Dining_home\">" + clubName + "</a></div>");
                    //} else {

                        out.println("<div><a id=\"title\" href=\"Member_announce\">" + clubName + "</a></div>");
                    //}
                }
            }

            out.println("<div id=\"title\" style=\"font-size: 14px; text_align: bottom\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Welcome, " +fullName+ "</div>");
                  
            
            if (activity_id == dining_activity_id) {                  // if Dining - display the dining logo

                //out.println("<div id=\"dining_logo\"> </div>");        // pull in the dining logo - defined in the stylesheet (see outputlogo below)

            } else {

                if (club.equals("philcricket")) {       // Use a custom weather link for Phily Cricket

                    out.println("<div id=\"weather\"><a href=\"http://www.weather.com/weather/today/Flourtown+PA+19031\" target=\"_blank\">View Current Weather</a></div>");

                } else if (club.equals("denvercc")) {

                    out.println("<div id=\"weather\"><a href=\"http://weather.weatherbug.com/CO/Denver-weather/local-forecast/7-day-forecast.html?zcode=z5545&units=0\" target=\"_blank\">View Current Weather</a></div>");

                } else if (club.equals("mesaverdecc")) {

                    out.println("<div id=\"weather\"><a href=\"http://www.weatherlink.com/user/mesaverdecc\" target=\"_blank\">View Current Weather</a></div>");
                    
                } else if (club.equals("desertmountain")) {

                    out.println("<div id=\"weather\"><a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1048342\" target=\"_blank\">View Current Weather</a></div>");

                } else if (zip != null && !zip.equals("") && activity_id != ft_connect_home_id) {     // if zipcode provided    (also add the ForeTees or FlxRez logo??????)

                    // out.println("<div id=\"weather\"><a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" onclick=\"countHit('ACCU')\">View Current Weather</a></div>");
                    out.println("<div id=\"weather\"><a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&amp;zipcode=" + zip + "\" target=\"_blank\">View Current Weather</a></div>");
                }
            }
    
            if (club.equals("mirabel")) out.print("<BR /><BR /><BR />");
            
        }         // end of IF caller=FLEXWEBFT

        out.print("<div class=\"clearfloat\"></div>");
        out.print("</div>");

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
     *
     ***************************************************************************************
     **/
    public static void outputSubNav(String club, int activity_id, PrintWriter out, Connection con) {  // DO NOT USE THIS GOING FORWARD!!!

        HttpServletRequest req = null;

        outputSubNav(club, activity_id, out, con, req);
    }

    public static void outputSubNav(String club, int activity_id, PrintWriter out, Connection con, HttpServletRequest req) {
        
        HttpSession session = null;

        if (req != null) {
            session = req.getSession(false);  // Get user's session object (no new one)
        }
        
        if(Utilities.getBitFromSession(session,"app_mode",ProcessConstants.APPMODE_HIDE_SUB_NAV)){
            return;
        }
        
        String navHtml = "";

        boolean rwd = false;

        String caller = "";            // website caller
        
        if (session != null) {
            caller = (String)session.getAttribute("caller");   // get caller's name
            rwd = (Boolean) session.getAttribute("rwd");    // use Responsive?
        }

        //  Do not show the tabs if FT Connect Premier (Flexscape)
        
        if (!caller.equals(ProcessConstants.FT_PREMIER_CALLER) && !club.equals("ccofnc")) {
            List<Map> activityList = getActivityList(club, con, req);
            // Only show tabs if there is more than one tab, or if a single tab has children ("action" is List, not String)
            if(activityList.size() > 1 || (activityList.size() == 1 && activityList.get(0).get("action") instanceof List)){
                navHtml = "<div id=\"subnav\"><ul>"+getNavListHtml(activityList, activity_id, club)+"</ul></div>";
            }
        }        // end of IF caller=FLEXWEBFT
        
        out.print(navHtml);
    }
    
    private static String getNavListHtml(List<Map> navList, int activity_id, String club){
        return getNavListHtml(navList, activity_id, club, "");
    }
    
    private static String getNavListHtml(List<Map> navList, int activity_id, String club, String spacer){
        List<String> navHtmlList = new ArrayList<String>();
        for (Map navItemMap : navList){
            if(navItemMap.get("action") instanceof String){
                // The action is a string, so must be a url.
                navHtmlList.add(getItemHtml(navItemMap, activity_id));
            } else {
                // The action must be another list of navigation elements
                // Recursivly process them
                navHtmlList.add("<li class=\""+
                    StringUtils.join(new String[]{getItemClass(navItemMap), getItemActivityClass(navItemMap, activity_id)}, " ")+
                    "\">"+getNodeLinkHtml ((String) navItemMap.get("name"), "#", navItemMap, activity_id)+
                    "<ul>"+getNavListHtml((List) navItemMap.get("action"), activity_id, club, "")+"</ul></li>");
            }
        }
        return StringUtils.join(navHtmlList.toArray(new String[navHtmlList.size()]), spacer);
    }
    
    private static String getItemHtml(Map<String, Object> navItemMap, int activity_id){

        return "<li class=\""+
            StringUtils.join(new String[]{getItemClass(navItemMap), getItemActivityClass(navItemMap, activity_id)}, " ")+
            "\">"+getNodeLinkHtml((String) navItemMap.get("name"), (String) navItemMap.get("action"), navItemMap, activity_id)+
            "</li>";
        
    }
    
    private static String getNodeLinkHtml(String name, String url, Map<String, Object> navItemMap, Integer activity_id){

        return "<a href=\""+url+"\" class=\""+getItemActivityClass(navItemMap, activity_id)+"\""+getItemAttributes(navItemMap)+"><span class=\""+getItemClass(navItemMap)+"\">"+name+"</span></a>";
       
    }
    
    private static String getItemClass(Map<String, Object> navItemMap){
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
            aClass.append("actId_"+item_activity);
            if(activity_id != null && item_activity.equals(activity_id) ){
                aClass.append(" selected");
            }
        }
        return aClass.toString();
    }
    
    private static String getItemAttributes(Map<String, Object> navItemMap){
        Map<String, String> attributes = (LinkedHashMap<String, String>) navItemMap.get("actionAttributes");
        List<String> attrList = new ArrayList<String>();
        if(attributes != null && attributes.size() > 0){
            attrList.add(""); // Empty string to add space at begining of attribute list
            Iterator it = attributes.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry item = (Map.Entry)it.next();
                attrList.add(item.getKey() + "=\"" + item.getValue() + "\"");
            }
        }
        return StringUtils.join(attrList.toArray(new String[attrList.size()]), " ");
    }
    
    /**
     ***************************************************************************************
     *
     * getActivityList
     * 
     * This method create a list containing navItem maps for sub-navigation tabs (Golf, Tennis, Dining, etc).
     *
     * @param club the site name of the club currently being processed
     * @param activity_id the activity id that the member is currently using
     * @param con the database connection for the specified club
     * 
     *
     ***************************************************************************************
     **/
    public static List<Map> getActivityList(String club, Connection con, HttpServletRequest req) {

        Statement stmt = null;
        ResultSet rs = null;

        HttpSession session = null;

        if (req != null) {
            session = req.getSession(false);  // Get user's session object (no new one)
        }

        int count = 0;
        int i = 0;
        int foretees_mode = 0;
        int dining_mode = 0;
        int flxrez_staging = 0;
        int dining_staging = 0;
        int ftConnect = 0;

        boolean memberDir = false;      // FT Connect tab indicators
        boolean staffDir = false;
        boolean statements = false;
        boolean billpay = false;
        boolean newsletters = false;
        
        List<Map> activityList = new ArrayList<Map>();
        
        String sGolf = "Golf";
        String sDining = "Dining";
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

        // Club overrides
        if (club.equals("tontoverde")) {
            sGolf = "Golf Home ";
            sDining = "Club Events";
        }
        

        if (session != null) {
            if (session.getAttribute("ftConnect") != null) {   //  if ftConnect included in session (set in Login.memberUser only)
                ftConnect = (Integer) session.getAttribute("ftConnect");   // get value (1 = yes)
            }
        }

        if (ftConnect == 1) {      //  if club uses FT Connect (Standard)

            memberDir = true;
            staffDir = true;
            statements = true;
            billpay = true;
            newsletters = true;
        }

        try {

            // Get foretees_mode, dining_mode and # of activities from database
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT foretees_mode, organization_id, flxrez_staging, dining_staging FROM club5 WHERE clubName <> '';");

            if (rs.next()) {
                foretees_mode = rs.getInt(1) > 0 ? 1 : 0;
                dining_mode = rs.getInt(2) > 0 ? 1 : 0;
                flxrez_staging = rs.getInt(3) > 0 ? 1 : 0;
                dining_staging = rs.getInt(4) > 0 ? 1 : 0;
            }

            // only query activities is club is not is staging mode
            if (flxrez_staging == 0) {

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
        if (memberDir == true || staffDir == true) {       // add Directory tab?
            if (memberDir == true && staffDir == true) {    // both directories ?
                List<Map> subList = new ArrayList<Map>();
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
                List<Map> subList = new ArrayList<Map>();
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
        if(classes != null && classes.size() > 0){
            itemMap.put("classes", classes); // Optional list of CSS classes for this item
        }
        if(attributes != null){
            Map<String, String> attrMap = new LinkedHashMap<String,String>();
            for(int i = 0; i < attributes.length; i+=2){
                attrMap.put(attributes[i], attributes[i+1]);
            }
            itemMap.put("actionAttributes", attrMap);
        }
        return itemMap;
        
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
     *
     ***************************************************************************************
     **/
    public static void outputPageStart(String club, int activity_id, PrintWriter out) {

        out.println("<div id=\"main_clear\" style=\"clear:both;\"></div>");
        out.println("<div id=\"main\" data-role=\"content\">");    // this div is terminated in outputPageEnd below                align=\"center\"

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
     *
     ***************************************************************************************
     **/
    public static void outputPageEnd(String club, int activity_id, PrintWriter out) {   // DO NOT USE THIS GOING FORWARD !!

        HttpServletRequest req = null;

        outputPageEnd(club, activity_id, out, req);        
    }
        
    public static void outputPageEnd(String club, int activity_id, PrintWriter out, HttpServletRequest req) {

        HttpSession session = null;

        if (req != null) {
            session = req.getSession(false);  // Get user's session object (no new one)
        }

        String caller = "";            // website caller
        
        if (session != null) {
            
            caller = (String)session.getAttribute("caller");   // get caller's name
        }
            
        out.println("</div><!-- end main div -->");             // terminates div tag in outputPageStart above

        if (!caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {      // do not include the copyright & logo if FT Connect Premier (Flexscape)
         
            Calendar cal = new GregorianCalendar();       // get todays date
            int year = cal.get(Calendar.YEAR);

            out.print("<div id=\"footer\">");
            out.print("Copyright &copy; " + year + " ForeTees, LLC. &nbsp;All rights reserved.<br /> <br />");
            out.println("</div>");      // terminates the 'footer' div
        }

        out.println("</div><!-- end wrapper div -->");    // terminates the 'wrapper' div in outputBody above

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
     *
     ***************************************************************************************
     **/
    public static void outputBreadCrumb(String club, int activity_id, PrintWriter out, String crumb) {
        outputBreadCrumb(club, activity_id, out, crumb, null);
    }
    
    public static void outputBreadCrumb(String club, int activity_id, PrintWriter out, String crumb, HttpServletRequest req) {
        
        String home_link = "<a href=\"Member_announce\">Home</a> / ";

        /*if (activity_id == dining_activity_id) {

            // breadcrumb
            out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / " + crumb + "</div>");

        } else*/ 
        
        if (activity_id == ft_connect_home_id || activity_id == ft_connect_directory_id || 
                   activity_id == ft_connect_statements_id || activity_id == ft_connect_newsletters_id) {
            
            home_link = "<a href=\"Member_announce?activity=connect\">Home</a> / ";
            
    
            
        } else {

            //String homeParm = "";
            
            if (club.equals("tanoan")) {    // AGC club that wants to use the Announce Page
                
                home_link = "<a href=\"Member_announce?override=yes\">Home</a> / ";
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
     *
     ***************************************************************************************
     **/
    public static void outputLogo(String club, int activity_id, PrintWriter out) {


        String CDN = Common_Server.getContentServer();

        if (club.equals("mpccpb")) {
            
            out.println("<div id=\"main_ftlogo\"><img src=\"/mpccpb/images/logo2.jpg\" alt=\"Monterey Peninsula CC\"></div>");
            
        } else {
            
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
        out.println("<div class=\"after_logo_fix\"></div>");
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
