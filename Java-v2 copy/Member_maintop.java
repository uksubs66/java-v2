/***************************************************************************************
 *   Member_maintop:  This servlet will display the Member's navigation bar (Top of Page).
 *
 *
 *   called by:  member_main.htm
 *
 *   created:  7/22/2003   Bob P.
 *
 *
 *   last updated:
 *
 *     11/14/11  Add test link for new skin
 *      9/14/11  Make sure the Golf activity is not included in the navigation when golf is not included (FlxRez stand alone).
 *      8/18/11  Add a text description under each icon (weather, dining, mobile) and change the sizes of the images.
 *      6/14/11  Updated user-agent string checks to also search for "msntv" for old menus. 
 *      6/10/11  Added an exclusion for a specific WebTV user to force the old menus to display.
 *      5/25/11  Added an exclusion for a specific WebTV user to force the old menus to display.
 *      5/19/11  If WebTV user detected, display the old "Click here if menus fail" link, as it is needed for them to book tee times.
 *      5/17/11  Activities link will be hidden on the golf side if FlxRez Staging mode is turned on.
 *      4/20/11  Golf Club of Georgia (gcgeorgia) - Updated custom weather link.
 *      3/02/11  Updated code to hide the mobile link so that it properly hides on Notification sites.
 *      9/02/10  Replace moble image with new icon to match the dining and weather icons
 *      9/01/10  Added ability to track hits to the external weather links
 *      8/12/10  Add temp option for testing ad space for clubs to sell advertising to member businesses.
 *      4/19/10  Removed Temporary link to guest tracking system for Demo/TPC/Desert Forest.  Now accessed via Partners page.
 *      4/18/10  Temporary link to guest tracking system for Demo/TPC/Desert Forest
 *      2/04/10  Added support for fast-activity-switching if golf + one activity or no golf and two activities
 *               fast-activity-switching bypasses the Member_activities (activity selection) servlet
 *      2/04/10  Add Single Signon parms to link for Club Dining (pass mNum).
 *      1/22/10  Denver CC - hide the Mobile link - they don't want members to bypass website.
 *      1/14/10  Add Mobile image/link for golf tee time users - mobile help.
 *      9/30/09  Add the FlxRez logo to be used in place of the ForeTees logo for activities.
 *      8/19/09  Change main header to reflect the current default activity and add activity link
 *      4/15/09  Make the upper left-hand logo a link to Midland Hills' club website
 *      4/09/09  Use the old weather icon for Pine Hills CC (pinehills)
 *      1/27/09  Add new weather/dining icons
 *      1/06/09  Remove the size restrictions for the ForeTees logo.
 *     12/22/08  Use logo.png instead of logo.jpg for desert highlands (for transparency reasons)
 *     10/27/08  Use logo.gif instead of logo.jpg for new TPC clubs
 *      8/13/08  Add a title tag to the anchor for the ForeTees logo to identify the server id.
 *      7/29/08  Added seperate weather link for GC of Georgia (gcgeorgia)
 *      6/26/08  Baltusrol - add custom navigation tabs - very limited.
 *      4/09/08  Updated browser detection - force safari v3.1 users to old menus
 *      3/12/08  Updated browser detection to allow OSX & Safari users to use drop down menus
 *      3/05/08  Member menus - remove check for web site callers so all members get email tab.  Email was disabled
 *               for CE and MF sites originally because the web site companies requested that we do it.
 *      2/13/08  Remove AGT specific menus and move the menu call for TLT menu to before Table tag.
 *      8/19/07  Mirasol CC - Do not show the weather or ForeTees links
 *      7/15/07  Disallow the email tab for Cherry Hills. It caused problems - tee time menu would not display.
 *      7/14/07  Allow the email tab for Cherry Hills.
 *      2/28/07  Adjusted positioning of right most cell (logo & copyright), and replaced please log out w/ menus fail link
 *      1/19/07  Create unique navigation frame for Interlachen's Spa Services (use lesson book).
 *     10/18/06  Create unique navigation frame for AGT (Training Center - Lessons Only).
 *     07/20/06  Changes for TLT System
 *      4/07/06  Wee Burn and Westchester - do not display the ForeTees Logo.
 *      4/07/06  Cherry Hills - block access to most features for mship type of Caddies.
 *      4/18/05  Add checks for MAC Safari users and Windows FireFox users - change format accordingly.
 *      4/12/05  Add checks for MAC and WebTV users - do not use drop-down menus for them.
 *     10/05/04  Ver 5 - Change layout, add drop-down menu.
 *      9/22/04  RDP  Add zipcode for weather.
 *      4/30/04  RDP  Add club logo and move ForeTees logo.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// Foretees imports
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;


public class Member_maintop extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyMem2(req, out);             // check for intruder

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
        return;
    }
    
    String caller = "";
    String menuCaller = "";
    String club = "";
    String zip = "";
    String mship = "";
    String user = "";

    boolean golf_mode = false;
    boolean oldMenus = false;
    boolean Safari = false;
    boolean showMobile = true;                                  // show Mobile Help Link?  Default = Yes
    boolean menusDetected = false;
    //boolean Firefox = false;  // not using?

    
    boolean useLocalAd = false;                                 // Club wants to use Local Advertising Space
    
    // ***** TEMP - turn on Local Ad for testing
    //useLocalAd = true;
    
    
    caller = (String)session.getAttribute("caller");            // get caller (other web site?)
    club = (String)session.getAttribute("club");
    zip = (String)session.getAttribute("zipcode");
    mship = (String)session.getAttribute("mship");
    user = (String)session.getAttribute("user");
    
    int tmp_tlt = 0;

    int server_id = Common_Server.SERVER_ID;                    // get the id of the server we are running in!!!!

    //
    // See what activity mode we are in
    //
    int sess_activity_id = 0;

    try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
    catch (Exception ignore) { }

    int organization_id = 0;

    try { organization_id = (Integer)session.getAttribute("organization_id"); }
    catch (Exception ignore) { }

    //
    //  See if we are in the timeless tees mode
    //
    try { tmp_tlt = (Integer)session.getAttribute("tlt"); }
    catch (Exception ignore) { }
      
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    
    //
    //  Check if we should show the Mobile Help link (if the club does not want members to know about the mobile)
    //
    if (sess_activity_id > 0 || IS_TLT) {

        showMobile = false;
        
    } else {
        
        showMobile = Utilities.checkMobileSupport (con);      //  show the Mobile Help link?
    }
 
    
    
    //
    //  If we called ourself to switch to 'oldMenus' mode - see link below "Click Here if Menus Fail"
    //
    if (req.getParameter("mac") != null) {                  // if mac being passed then act accordingly

        if (req.getParameter("mac").equals("no")) {
            
            session.setAttribute("mac", "no");              // disable oldMenus option
            menusDetected = true;
            
        } else {
            
            oldMenus = true;                                // set Mac mode
            session.setAttribute("mac", "yes");             // enable oldMenus option
            menusDetected = true;
        }
        
    } else {                                                // see if oldMenus option already set in session

        String macOpt = (String)session.getAttribute("mac");
        if (macOpt != null && macOpt.equals( "yes" )) {
            oldMenus = true;                                // set Mac mode
            menusDetected = true;
        }
    }

    //
    //  Check for a old Mac user and the browser type
    //
    String ua = req.getHeader("User-Agent");                // browser settings

    if (ua != null && menusDetected == false) {             // if settings provided

      ua = ua.toLowerCase();                                // convert to all lower case

      if ( ua.indexOf("mac") != -1 ) {                      // if a MAC system

         if ( ua.indexOf("mac os x") == -1 ) {              // if OS X assume they are using a modern browser
            
            if ( ua.indexOf("msie 5") == -1 ) {             // msie 5 on OSX doesn't contain the 'os x' string
                
                oldMenus = true;
            }
         }

      } else {

         if (ua.indexOf("webtv") != -1 || ua.indexOf("msntv") != -1) {                   // if a WebTV/MSNTV system

            oldMenus = true;                                // treat like a old Mac

         } /* else {

            if (ua.indexOf("firefox") != -1) {              // if FireFox browser

               Firefox = true;                              // indicate so
            }
         } */
      }
      
      // moved safari detection to here from inside the mac block above since
      // it is now available for Windows
      if (ua.indexOf("safari") != -1) {                     // if Safari browser

         Safari = true;
         /*
         // force 3.1 safari users default to old menus - something broke in 3.1 (3.0, 3.2 ok)
         if (ua.indexOf("3.1 safari") != -1) {
             
            oldMenus = true;
         }
          **/
      }
      

        if (!oldMenus) {

            session.setAttribute("mac", "no");              // disable oldMenus option

        } else {
                               // set Mac mode
            session.setAttribute("mac", "yes");             // enable oldMenus option
        }
      
    } // end if ua not null

    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    int year = cal.get(Calendar.YEAR);            // get the year


    //
    //  Check for Interlacen Spa Services - Lessons Only - unique nav bar
    //
    if (club.equals( "interlachenspa" )) {

       doSPAmenu(resp, out, club, zip, year, Safari);
       return;
    }


    //
    //  Check for Baltusrol - limited nav bar
    //
    if (club.equals( "baltusrolgc" )) {

       doBaltmenu(resp, out, club, zip, year, Safari, caller);
       return;
    }


    //
    //  If IS_TLT configured club
    //
    if (IS_TLT) {
        
        //
        //  Build the HTML page (main menu) for Notification System
        //
        out.println("<html><head>");

        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
        out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
        out.println("<title> \"ForeTees Member Main Title Page\"</title>");
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/jquery.js\"></script>");

        out.println("</head>");
        
        String tmp_grn = (club.startsWith("notify")) ? "-grn" : "";
        
        out.println("<script type=\"text/javascript\">");
        
        out.println("imgD1 = new Image(46,69);");
        out.println("imgD2 = new Image(46,69);");
        out.println("imgW1 = new Image(46,69);");
        out.println("imgW2 = new Image(46,69);");
        out.println("imgM1 = new Image(46,69);");
        out.println("imgM2 = new Image(46,69);");
        
        if (Utilities.checkDiningLink("mem_main", con)) {
            out.println("imgD1.src = '/"+rev+"/images/dining"+tmp_grn+"-off.gif';");
            out.println("imgD2.src = '/"+rev+"/images/dining"+tmp_grn+"-on.gif';");
        }
        out.println("imgW1.src = '/"+rev+"/images/weather"+tmp_grn+"-off.gif';");
        out.println("imgW2.src = '/"+rev+"/images/weather"+tmp_grn+"-on.gif';");
        out.println("imgM1.src = '/"+rev+"/mobile/images/mobile-off.gif';");
        out.println("imgM2.src = '/"+rev+"/mobile/images/mobile-on.gif';");
        
        out.println("function swapImage(imgName, imgObj) {");
        out.println(" if (document.images) {");
        out.println("  document.images[imgName].src=eval(imgObj+'.src');");
        out.println(" }");
        out.println("}");

        out.println("function countHit(provider) {");
        out.println(" $.get('stats?countHit&provider='+provider, function(data) { });");
        out.println("}");

        out.println("</script>");
        
        out.println("<body>");

        SystemUtils.getMemberMainMenu(req, out, caller);     // build dynamic menus       
        
        out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");

        out.println("<tr valign=\"top\">");
        out.print("<td width=\"12%\" align=\"center\" valign=\"middle\">");
        out.print("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
        out.print("</td>\n");

        out.print("<td valign=\"middle\" width=\"12%\" align=\"center\">");

        if (club.equals("gcgeorgia")) {

            out.print("<a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1039173\" target=\"_blank\">");

        } else {

            // ALL OTHER CLUBS
            if (zip != null && !zip.equals( "" )) {     // if zipcode provided

                out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" " +
                          "onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\" " +
                          "onclick=\"countHit('ACCU')\">");
            } else {

                out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\" " +
                          "onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\" " +
                          "onclick=\"countHit('ACCU')\">");
            }
        }
        
        out.print("<img name=img1 src=\"/" +rev+ "/images/weather"+tmp_grn+"-off.gif\" width=46 height=47 border=0 " +
                  "title=\"Check your local weather.\"></a>&nbsp;&nbsp;");

        
        if (Utilities.checkDiningLink("mem_main", con)) {

             if (organization_id != 0) { // TLT Dining Link

                out.print("&nbsp;&nbsp;<a href=\"Dining_home\" target=\"_top\" " +
                          "onmouseover=\"swapImage('img2', 'imgD2')\" onmouseout=\"swapImage('img2', 'imgD1')\">" +
                          "<img name=img2 src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=\"46\" height=\"69\" " +
                          "border=\"0\" title=\"Access ForeTees Dining w/ new skin.\"></a>");
/*
                out.print("&nbsp;&nbsp;<a href=\"http://dining.foretees.com/self_service/reservations/member_login?username=" +user+ "&organization_id=" + organization_id + "\" " + // mNum
                          "onmouseover=\"swapImage('img2', 'imgD2')\" onmouseout=\"swapImage('img2', 'imgD1')\" target=\"_dining\" onclick=\"countHit('FTDINE')\">" +
                          "<img name=img2 src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=\"46\" height=\"47\" " +
                          "border=\"0\" title=\"Access ForeTees Dining.\"></a>");
*/
             } else {

                out.print("&nbsp;&nbsp;<a href=\"Member_dining?dReq&caller=main\" target=\"bot\" " +
                          "onmouseover=\"swapImage('img2', 'imgD2')\" onmouseout=\"swapImage('img2', 'imgD1')\">" +
                          "<img name=\"img2\" src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=\"46\" height=\"47\" " +
                          "border=\"0\" title=\"Make a dining request.\"></a>");
             }

        } // end if dining configured

        /*
        out.print("&nbsp;&nbsp;<a href=\"Common_mobile_help\" target=\"_blank\" onmouseover=\"swapImage('img3', 'imgM2')\" onmouseout=\"swapImage('img3', 'imgM1')\">");
        out.print("<img name=img3 src=\"/" +rev+ "/mobile/images/mobile-off.gif\" width=46 height=46 border=0 title=\"Mobile User Help\" style=\"position:relative;top:-1px\"></a><br><br>");
        */

        out.print("</td>\n");

        out.println("<td width=\"52%\" align=\"center\">");
        out.println("<font size=\"5\">Member Notification Management</font><br>");

        //output the Home, Logout/Exit, and Help links
        if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {  // if from foretees login page
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");     // shift it over a little
        }
        out.println("<a class=\"gblHref\" href=\"Member_announce\" target=\"bot\" >Home</a>");

        // TEMP LINK TO ACTIVATE NEW SKIN
        String base_url = "../" + club + "_golf/";
        if(sess_activity_id > 0){
            base_url = "../" + club + "_flxrez"+sess_activity_id+"/";
        }
        
        if (Common_Server.SERVER_ID == 4 || club.startsWith("notify")) {
           out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\""+base_url+"Member_announce?test_new_skin\" target=\"_top\" >New Skin (beta)</a>");
        }

        out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Member_help\" target=\"_blank\" >Help</a>");
        if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page
          out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout </a>");
          //out.println("<font size=\"1\" face=\"Verdana, Arial, Helvetica, Sans-serif\">");
          //out.println("&nbsp;&nbsp;&nbsp; (Please logout when done)</font>");
        } else {      // from web site (MFirst, CE, AMO, etc.)
          out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Exit</a>");
            }

        // If old menu required user detected, display menu fail link.
        if (oldMenus) out.print("&nbsp; &nbsp; &nbsp; &nbsp; <a class=\"gblHref2\" href=\"Member_maintop?mac=yes\">(Click Here if Menus Fail)</a>");

        out.print("</td>");

        out.print("<td width=\"24%\" align=\"right\" valign=\"middle\">");
        out.print("<a href=\"http://www.foretees.com\" target=\"_blank\">");
        out.print("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>&nbsp;");
        out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
        out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
        out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.&nbsp;");
        out.print("</font>\n");

        out.print("</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</body></html>");
        out.close();

    } else {     // Not a Notification Site (normal Tee Times or FlxRez)

       //
       //  IF Cherry Hills - if member is a caddie, then limit the tabs they get
       //
       if (!club.equals( "cherryhills" ) || !mship.equalsIgnoreCase( "caddies" )) {   // if NOT Cherry Hills Caddies

          //
          //  Proceed according to user type (Mac with IE or Safari can't use drop-down menus)
          //
          if (oldMenus == false && (!club.equals("missionvalleycc") || (!user.equals("1698") && !user.startsWith("1887"))) && (!club.equals("martindowns") || !user.equals("dsimms"))) {

             //
             //  Build the HTML page (main menu) for all other users
             //
             out.println("<html><head>");

             out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
             out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
             out.println("<title> \"ForeTees Member Main Title Page\"</title>");
             out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
             out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
             out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/jquery.js\"></script>");

             out.println("</head>");
        
             String tmp_grn = (club.startsWith("notify")) ? "-grn" : "";

             out.println("<script type=\"text/javascript\">");

             out.println("imgD1 = new Image(46,69);");
             out.println("imgD2 = new Image(46,69);");
             out.println("imgW1 = new Image(46,69);");
             out.println("imgW2 = new Image(46,69);");
             out.println("imgM1 = new Image(46,69);");
             out.println("imgM2 = new Image(46,69);");

             if (Utilities.checkDiningLink("mem_main", con)) {
                 out.println("imgD1.src = '/"+rev+"/images/dining"+tmp_grn+"-off.gif';");
                 out.println("imgD2.src = '/"+rev+"/images/dining"+tmp_grn+"-on.gif';");
             }
             out.println("imgW1.src = '/"+rev+"/images/weather"+tmp_grn+"-off.gif';");
             out.println("imgW2.src = '/"+rev+"/images/weather"+tmp_grn+"-on.gif';");
             out.println("imgM1.src = '/"+rev+"/mobile/images/mobile-off.gif';");
             out.println("imgM2.src = '/"+rev+"/mobile/images/mobile-on.gif';");

             out.println("function swapImage(imgName, imgObj) {");
             out.println(" if (document.images) {");
             out.println("  document.images[imgName].src=eval(imgObj+'.src');");
             out.println(" }");
             out.println("}");

             out.println("function countHit(provider) {");
             out.println(" $.get('stats?countHit&provider='+provider, function(data) { });");
             out.println("}");

             out.println("</script>");
        
             out.println("<body>");

             SystemUtils.getMemberMainMenu(req, out, caller);

             out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
             out.println("<tr valign=\"top\">");
             

              out.print("<td width=\"12%\" align=\"center\" valign=\"middle\">");
              if (club.equals("tpcpotomac") || club.equals("tpcsouthwind") || club.equals("tpcsugarloaf") || club.equals("tpcwakefieldplantation") || 
                  club.equals("tpcboston") || club.equals("tpcjasnapolana") || club.equals("tpcriverhighlands") || club.equals("tpcriversbend") ||
                  club.equals("tpccraigranch") || club.equals("tpcsummerlin") || club.equals("tpcsanfranciscobay")) {
                  out.print("<img src=\"/" +club+ "/images/logo.gif\" border=0>");
              } else if (club.equals("deserthighlands")) {
                  out.print("<img src=\"/" +club+ "/images/logo.png\" border=0>");
              } else if (club.equals("midland")) {
                  out.print("<a href=\"http://www.midlandhillscc.org/\" target=\"_blank\"><img src=\"/" +club+ "/images/logo.jpg\" border=0></a>");
              } else {
                  out.print("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
              }
              out.print("</td>\n");
              

              out.print("<td valign=\"top\" width=\"12%\" align=\"center\" nowrap>");

              // supress external links for mirasol
              if (!club.equals("mirasolcc")) {
                  
                  if (club.equals("pinehills")) {

                      // Display the old weather image
                      out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" onclick=\"countHit('ACCU')\">");
                      out.print("<img src=\"/" +rev+ "/images/weather.gif\" border=\"0\"></a>");
                      
                  } else {

                      if (club.equals("gcgeorgia")) {
                          out.print("<a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1039173\" target=\"_blank\">");
                      } else {
                          if (zip != null && !zip.equals( "" )) {     // if zipcode provided
                              out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" " +
                                        "onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\" onclick=\"countHit('ACCU')\">");
                          } else {
                              out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\" " +
                                        "onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\" onclick=\"countHit('ACCU')\">");
                          }
                      }

                      out.print("<img name=img1 src=\"/" +rev+ "/images/weather"+tmp_grn+"-off.gif\" width=\"46\" height=\"69\" border=\"0\" " +
                                "title=\"Check your local weather.\"></a>");

                      if(Utilities.checkDiningLink("mem_main", con)) {

                         if (organization_id != 0) {      // club.equals("demov4") && !mNum.equals("")
                            
                            if (club.startsWith("demo") || (club.equals("interlachen") && Common_Server.SERVER_ID == 4)) {   // TEST *************** New Dining Skin ************************
                               
                               out.print("&nbsp;&nbsp;<a href=\"Dining_home\" target=\"_top\" " + 
                                         "onmouseover=\"swapImage('img2', 'imgD2')\" onmouseout=\"swapImage('img2', 'imgD1')\">" +
                                         "<img name=img2 src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=\"46\" height=\"69\" " +
                                         "border=\"0\" title=\"Access ForeTees Dining w/ new skin.\"></a>");

                               
                            }// else {
/*
 *
                   out.print("&nbsp;&nbsp;<a href=\"http://dining.foretees.com/self_service/reservations/member_login?username=" +user+ "&organization_id=" + organization_id + "\" " + // mNum
                             "onmouseover=\"swapImage('img3', 'imgD2')\" onmouseout=\"swapImage('img3', 'imgD1')\" target=\"_dining\" onclick=\"countHit('FTDINE')\">" +
                             "<img name=img4 src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=\"46\" height=\"69\" " +
                             "border=\"0\" title=\"Access ForeTees Dining.\"></a>");
 *
 *
                            if (club.equals("demov4")) {
                               out.print("&nbsp;&nbsp;<a href=\"http://dining.foretees.com/self_service/reservations/member_login?username=" +user+ "&organization_id=" + organization_id + "\" " + // mNum
                                         "onmouseover=\"swapImage('img12', 'imgD2')\" onmouseout=\"swapImage('img12', 'imgD1')\" target=\"_dining\" onclick=\"countHit('FTDINE')\">" +
                                         "<img name=img12 src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=\"46\" height=\"47\" " +
                                         "border=\"0\" title=\"Access ForeTees Dining w/ OE interface.\"></a>");
                            }

                               out.print("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"window.open (" +
                                         "'http://dining.foretees.com/self_service/reservations/member_login?" +
                                         "username=" +mNum+ "', 'newwindow', 'height=900, width=880, toolbar=no, menubar=no, scrollbars=yes, " +
                                         "resizable=yes, location=no directories=no, status=no')\" " +
                                         "onmouseover=\"swapImage('img2', 'imgD2')\" onmouseout=\"swapImage('img2', 'imgD1')\">" +
                                         "<img name=img2 src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=46 height=47 " +
                                         "border=0 title=\"Make a dining request via Club Dining.\"></a>");
*/
                            //}
                            
                         } else {

                            out.print("&nbsp;&nbsp;<a href=\"Member_dining?dReq&caller=main\" target=\"bot\" " +
                                      "onmouseover=\"swapImage('img2', 'imgD2')\" onmouseout=\"swapImage('img2', 'imgD1')\">" +
                                      "<img name=\"img2\" src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=\"46\" height=\"69\" " +
                                      "border=\"0\" title=\"Make a dining request.\"></a>");
                         }

                      } // end if dining configured

                  } // end if club NOT pinehills
        
              } else {

                // mirasolcc - no external links
                out.println("&nbsp;");

              }
              
              if (!showMobile) {    //skip Mobile image if Notification or FlxRez system
                 
                  out.print("<br><br>");
                 
              } else {
/*
                 if (Common_Server.SERVER_ID == 4) {
                     
                    out.print("&nbsp;&nbsp;<a href=\"http://dining.foretees.com/self_service/reservations/member_login?username=" +user+ "&organization_id=" + organization_id + "\" target=\"_blank\" onmouseover=\"swapImage('img3', 'imgM2')\" onmouseout=\"swapImage('img3', 'imgM1')\">");
                    out.print("<img name=\"img3\" src=\"/" +rev+ "/mobile/images/mobile-off.gif\" width=\"46\" height=\"69\" border=\"0\" title=\"Mobile User Help\" style=\"position:relative;top:-1px\"></a>");
                     
                 } else {
*/
                    out.print("&nbsp;&nbsp;<a href=\"Common_mobile_help\" target=\"_blank\" onmouseover=\"swapImage('img3', 'imgM2')\" onmouseout=\"swapImage('img3', 'imgM1')\">");
                    out.print("<img name=\"img3\" src=\"/" +rev+ "/mobile/images/mobile-off.gif\" width=\"46\" height=\"69\" border=\"0\" title=\"Mobile User Help\" style=\"position:relative;top:-1px\"></a>");
                             
                     
//                 }

              }
                 
              out.println("</td>");      // end of column for weather, dining & Mobile images
              
              

              String tmp_title = "Member Tee Time Management";
                            
              //int root_id = 0;

              if (sess_activity_id > 0) {

                 try { tmp_title = getActivity.getActivityName(sess_activity_id, con); }  // root_id
                 catch (Exception ignore) {}

                 tmp_title += " Time Management System";
                 
              } else {    // Golf

                 if (useLocalAd == true && !club.equals( "westchester" ) && !club.equals( "weeburn" ) && !club.equals("mirasolcc")) {

                    tmp_title = "Tee Time Management";
                 }
              }
              
              
              
              out.println("<td width=\"52%\" align=\"center\"><font size=\"5\">");

              if (useLocalAd == true) {

                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

                 if (!club.equals( "westchester" ) && !club.equals( "weeburn" ) && !club.equals("mirasolcc")) {
                 
                    out.print("<a href=\"http://www.foretees.com\" title=\"Server #" +server_id+ "\" target=\"_blank\">");
                    if (sess_activity_id > 0) {
                       out.print("<img src=\"/" +rev+ "/images/FlxRez_nav.gif\" border=0></a>&nbsp;");
                    } else {
                       out.print("<img src=\"/" +rev+ "/images/foretees_nav_sm.jpg\" border=0></a>&nbsp;");
                    }
                 }
                         
                 out.println(tmp_title);
                 out.println("</font><font size=\"3\"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sponsored By:</i>");
                 
              } else {
                 
                 out.println(tmp_title);
              }
               
              out.println("</font><br>");
                             
               //output the Home, Logout/Exit, and Help links
               if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {  // if from foretees login page
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");     // shift it over a little
               }
               out.println("<a class=\"gblHref\" href=\"Member_announce\" target=\"bot\" >Home</a>");


               //
               // See if we are going to check for fast-activity-switching
               //
               if (getActivity.isConfigured(con) && (!getActivity.isStagingMode(con) || sess_activity_id != 0)) {

                   // check to see if they have just one other activity
                   // if so then allow fast-activity-switching and display the activity name as the link
                   // if multiple activities then display an 'Activities' link to take to the selection page

                   int foretees_mode = 0;
                   int count = 0;
                   String[] activity_names = new String[2];
                   int[] activity_ids = new int[2];

                   try {

                       // Get foretees_mode and # of activities from database
                       stmt = con.createStatement();
                       rs = stmt.executeQuery(
                               "SELECT foretees_mode, activity_id, activity_name " +
                               "FROM club5, activities " +
                               "WHERE parent_id = 0");

                       rs.last();

                       count = rs.getRow(); // get the number of activities found (excluding golf)
                         
                       rs.beforeFirst();

                       if (rs.next()) {

                           if (rs.getInt("foretees_mode") == 1) {
                              
                              golf_mode = true;      // indicate Golf is included
                           }

                           if (golf_mode == true && count >= 2) {

                               out.println("<!-- NO FAST SWITCH: golf enabled and more than 1 other activity defined -->");

                               // they have golf AND more than one activity
                               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" " +
                                           "href=\"Member_activities\" target=\"bot\">Activities</a>");

                           } else {

                               if (golf_mode == true) {

                                   out.println("<!-- USE FAST SWITCH: golf enabled -->");

                                   // they have golf so add it
                                   activity_names[0] = "Golf";
                                   activity_ids[0] = 0;

                                   // add the other activity
                                   activity_names[1] = rs.getString("activity_name");
                                   activity_ids[1] = rs.getInt("activity_id");

                               } else {

                                   out.println("<!-- USE FAST SWITCH: no golf enabled -->");

                                   // no golf so might be two different root activities

                                   activity_names[0] = rs.getString("activity_name");
                                   activity_ids[0] = rs.getInt("activity_id");

                                   if (rs.next()) {

                                       activity_names[1] = rs.getString("activity_name");
                                       activity_ids[1] = rs.getInt("activity_id");

                                   }
                               }

                               if (golf_mode == true || count > 1) {     // skip if No Golf and only 1 Activity

                                  // they have golf + one activity (or no golf and two activities)
                                  out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" " +
                                              "href=\"Member_jump?switch&" +
                                              "activity_id=" + activity_ids[(sess_activity_id == 0) ? 1 : 0] + "\" target=\"_top\"" +
                                              ">" + activity_names[(sess_activity_id == 0) ? 1 : 0] + "</a>");
                               }
                           }
                       }

                   } catch (Exception exc) {

                       Utilities.logError("Member_maintop: Error loading activity count & names. club=" + club + ", err=" + exc.toString());
                         
                   } finally {

                       try { rs.close(); }
                       catch (Exception ignore) {}

                       try { stmt.close(); }
                       catch (Exception ignore) {}
                   }

               } // end isConfigured()
               
              String base_url = "../" + club + "_golf/";
              if (sess_activity_id > 0) {
                  base_url = "../" + club + "_flxrez" + sess_activity_id + "/";
              }

               // TEMP LINK TO ACTIVATE NEW SKIN
               if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
                   out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\""+base_url+"Member_announce?test_new_skin\" target=\"_top\" >New Skin (beta)</a>");
               }

               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Member_help\" target=\"_blank\" >Help</a>");
               if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page
                  out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout </a>");
                  //out.println("<font size=\"1\" face=\"Verdana, Arial, Helvetica, Sans-serif\">");
                  //out.println("&nbsp;&nbsp;&nbsp; (Please logout when done)</font>");
               } else {      // from web site (MFirst, CE, AMO, etc.)
                  out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Exit</a>");
               }

               //  If old menu required user detected, display menu fail link.
               if (oldMenus) out.print("&nbsp; &nbsp; &nbsp; &nbsp; <a class=\"gblHref2\" href=\"Member_maintop?mac=yes\">(Click Here if Menus Fail)</a>");

               out.println("</td>");
               
               
               //
               //  Insert member Ad here if club uses advertising
               //
               if (useLocalAd == true) {
                  
                  out.print("<td width=\"24%\" align=\"left\" valign=\"middle\">");
               
                  out.print("<a href=\"http://www.foretees.com\" target=\"_blank\">");
                  out.print("<img src=\"/" +club+ "/images/test_ad.jpg\" border=1></a>");
                  /*
                  out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
                  out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
                  out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;&nbsp;" +year+ " All rights reserved.&nbsp;");
                  out.print("</font>\n");
                   */
                  
               } else {      // no ads

                  out.print("<td width=\"24%\" align=\"right\" valign=\"middle\">");
               
                  if (!club.equals( "westchester" ) && !club.equals( "weeburn" ) && !club.equals("mirasolcc")) {
                       out.print("<a href=\"http://www.foretees.com\" title=\"Server #" +server_id+ "\" target=\"_blank\">");
                       if (sess_activity_id > 0) {
                          out.print("<img src=\"/" +rev+ "/images/FlxRez_nav.gif\" border=0></a>&nbsp;");
                       } else {
                          out.print("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>&nbsp;");
                       }
                  }
                  
                  out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
                  out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
                  out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.&nbsp;");
                  out.print("</font>\n");
               }                           
               out.println("</td>");

               
               out.print("<td width=\"2%\">&nbsp;");       // pad the right side
               out.println("</td>");
               
               
             out.println("</tr>");
             out.println("</table>");
             out.println("</body></html>");
             out.close();

          } else {   // old Mac user  ********* NOTE:  We should be bale to remove this?????????

             //
             //  Build the HTML page (main menu) for old MAC users with IE or Safari - Use Old Style Buttons
             //
             out.println("<html><head>");

             out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
             out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
             out.println("<title> \"ForeTees Member Main Title Page\"</title>");
             out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
             out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/jquery.js\"></script>");

             out.println("<script type=\"text/javascript\">");
             out.println("function countHit(provider) {");
             out.println(" $.get('stats?countHit&provider='+provider, function(data) { });");
             out.println("}");
             out.println("</script>");

             out.println("</head>");

             out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

             out.println("<table width=\"100%\" height=\"82\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");

             out.println("<tr valign=\"top\" height=\"41\">");
             if (Safari == true) {
                out.println("<td width=\"12%\" align=\"center\" rowspan=\"2\">");
             } else {
                out.println("<td width=\"12%\" align=\"center\" valign=\"middle\" rowspan=\"2\">");
             }
             if (club.equals("tpcpotomac") || club.equals("tpcsouthwind") || club.equals("tpcsugarloaf") || club.equals("tpcwakefieldplantation") || 
                 club.equals("tpcboston") || club.equals("tpcjasnapolana") || club.equals("tpcriverhighlands") || club.equals("tpcriversbend") ||
                 club.equals("tpccraigranch") || club.equals("tpcsummerlin") || club.equals("tpcsanfranciscobay")) {
                 out.println("<img src=\"/" +club+ "/images/logo.gif\" border=0>");
             } else {
                 out.println("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
             }
               out.println("</td>");

             out.println("<td valign=\"middle\" width=\"12%\" align=\"left\" rowspan=\"2\">");
             if (club.equals("gcgeorgia")) {
                 out.print("<a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1039173\" target=\"_blank\">");
             } else {
                 if (zip != null && !zip.equals( "" )) {     // if zipcode provided
                     out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" onclick=\"countHit('ACCU')\">");
                 } else {
                     out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\" onclick=\"countHit('ACCU')\">");
                 }
             }
                out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
               out.println("</td>");

               out.println("<td width=\"52%\" align=\"center\">");
                  out.println("<font size=\"5\">Member Tee Time Management</font><br>");

               //output the Home, Logout/Exit, and Help links
               out.println("<a class=\"gblHref\" href=\"Member_announce\" target=\"bot\" >Home</a>");
               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/member_help.htm\" target=\"_blank\" >Help</a>");
               if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page
                   
                  out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout</a>");
               
               } else {      // from web site (MFirst, CE, AMO, etc.)
               
                   out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Exit</a>");
               
               }
               out.print("&nbsp; &nbsp; &nbsp; &nbsp; <a class=\"gblHref2\" href=\"Member_maintop?mac=no\">(Click Here For New Menus)</a>");
               out.println("</td>");

                if (Safari == true) {
                   out.println("<td width=\"24%\" align=\"right\" valign=\"top\" rowspan=\"2\">");
                } else {
                   out.println("<td width=\"24%\" align=\"right\" rowspan=\"2\">");
                }
                 out.println("<p>");
                 out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
                  out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>&nbsp;");
                 out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
                 out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
                 out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.&nbsp;");
                 out.println("</font></p>");
               out.println("</td>");
             out.println("</tr>");

             if (Safari == true) {
                out.println("<tr height=\"41\" valign=\"bottom\">"); // was top
             } else {
                out.println("<tr height=\"41\" valign=\"bottom\">");
             }
               out.println("<td align=\"center\" nowrap>");
               out.println("<nobr>");
                out.println("<a href=\"Member_select\" target=\"bot\"><img name=\"Mem_Mac_Tee_Times\" src=\"/" +rev+ "/images/Mem_Mac_Tee_Times.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"View the Tee Sheets\"></a>");
                out.println("<a href=\"Member_teelist\" target=\"bot\"><img name=\"Mem_Mac_My_Calendar\" src=\"/" +rev+ "/images/Mem_Mac_My_Calendar.gif\" hspace=\"0\" border=\"0\" title=\"View Your Calendar To See Scheduled Tee Times, Events, Lessons\" alt=\"View Your Calendar\"></a>");
                out.println("<a href=\"/" +rev+ "/member_mac_lessons.htm\" target=\"bot\"><img name=\"Mem_Mac_Lessons\" src=\"/" +rev+ "/images/Mem_Mac_Lessons.gif\" hspace=\"0\" border=\"0\" title=\"Sign Up For Lessons or Access Your Scheduled Lessons\" alt=\"Lessons\"></a>");
                out.println("<a href=\"Member_events\" target=\"bot\"><img name=\"Mem_Mac_Events\" src=\"/" +rev+ "/images/Mem_Mac_Events.gif\" hspace=\"0\" border=\"0\" title=\"Sing Up For Events or View Upcoming Events\" alt=\"Events\"></a>");
                out.println("<a href=\"/" +rev+ "/member_mac_search.htm\" target=\"bot\"><img name=\"Mem_Mac_Search\" src=\"/" +rev+ "/images/Mem_Mac_Search.gif\" hspace=\"0\" border=\"0\" title=\"Search For Other Members' Tee Times or Your Past Tee Times\" alt=\"Search\"></a>");
                out.println("<a href=\"/" +rev+ "/member_mac_email.htm\" target=\"bot\"><img name=\"Mem_Mac_Email\" src=\"/" +rev+ "/images/Mem_Mac_Email.gif\" hspace=\"0\" border=\"0\" title=\"Send Email To Other Members\" alt=\"Email\"></a>");
                out.println("<a href=\"Member_buddy\" target=\"bot\"><img name=\"Mem_Mac_Partners\" src=\"/" +rev+ "/images/Mem_Mac_Partners.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Partner List\" alt=\"\"></a>");
                out.println("<a href=\"Member_services\" target=\"bot\"><img name=\"Mem_Mac_Settings\" src=\"/" +rev+ "/images/Mem_Mac_Settings.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Password and Other Settings\" alt=\"Settings\"></a>");
               out.println("</nobr></td>");
             out.println("</tr>");
             out.println("</table>");
             out.println("</body></html>");
             out.close();
          }

       } else {   // Cherry Hills Caddies

          //
          //  Build the main menu for Cherry Hills Caddies   ***** NOTE:  We can probably remove this now but will check (9/14/2011)
          //
          out.println("<html><head>");

          out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
          out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
          out.println("<title> \"ForeTees Caddie Main Title Page\"</title>");
          out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
          out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/jquery.js\"></script>");
          out.println("</head>");

          out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

          out.println("<table width=\"100%\" height=\"82\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");

          out.println("<tr valign=\"top\" height=\"41\">");
          if (Safari == true) {
             out.println("<td width=\"12%\" align=\"center\" rowspan=\"2\">");
          } else {
             out.println("<td width=\"12%\" align=\"center\" valign=\"middle\" rowspan=\"2\">");
          }
             out.println("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
            out.println("</td>");

          out.println("<td valign=\"middle\" width=\"12%\" align=\"left\" rowspan=\"2\">");
          if (club.equals("gcgeorgia")) {
              out.print("<a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1039173\" target=\"_blank\">");
          } else {
              if (zip != null && !zip.equals( "" )) {     // if zipcode provided
                  out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" onclick=\"countHit('ACCU')\">");
              } else {
                  out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\" onclick=\"countHit('ACCU')\">");
              }
          }
             out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
            out.println("</td>");

            out.println("<td width=\"52%\" align=\"center\">");
               out.println("<font size=\"5\">Caddie Tee Time View</font><br>");

            //output the Home, Logout/Exit links
            out.println("<a class=\"gblHref\" href=\"Member_announce\" target=\"bot\" >Home</a>");
            if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page
               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout</a>");
            } else {      // from web site (MFirst, CE, AMO, etc.)
               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Exit</a>");
            }
            out.println("</td>");

             if (Safari == true) {
                out.println("<td width=\"24%\" align=\"right\" valign=\"top\" rowspan=\"2\">");
             } else {
                out.println("<td width=\"24%\" align=\"right\" rowspan=\"2\">");
             }
              out.println("<p>");
              out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
               out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>&nbsp;");
              out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
              out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
              out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.&nbsp;");
              out.println("</font></p>");
            out.println("</td>");
          out.println("</tr>");

          if (Safari == true) {
             out.println("<tr height=\"41\" valign=\"top\">");
          } else {
             out.println("<tr height=\"41\" valign=\"bottom\">");
          }
            out.println("<td align=\"center\">");
            out.println("<!-- Begin code to display images horizontally. -->");
             out.println("<a href=\"Member_select\" target=\"bot\"><img name=\"Mem_Mac_Tee_Times\" src=\"/" +rev+ "/images/Mem_Mac_Tee_Times.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"View the Tee Sheets\"></a>");
            out.println("</td>");
          out.println("</tr>");
         out.println("</table>");
         out.println("</body></html>");
         out.close();
       } // end if Cherry Hills Caddies
       
   } // end if IS_TLT
     
 }  // end of doGet


//*************************************************************
//  doBaltmenu - build a unique nav bar for SPA Services - Lessons Only
//*************************************************************

 private void doBaltmenu(HttpServletResponse resp, PrintWriter out, String club, String zip, int year, boolean Safari, String caller) {

  
       out.println("<html><head>");

       out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
       out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
       out.println("<title> \"ForeTees Member Main Title Page\"</title>");
       out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
       out.println("</head>");

       out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

       out.println("<table width=\"100%\" height=\"82\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");

       out.println("<tr valign=\"top\" height=\"41\">");
       if (Safari == true) {
          out.println("<td width=\"12%\" align=\"center\" rowspan=\"2\">");
       } else {
          out.println("<td width=\"12%\" align=\"center\" valign=\"middle\" rowspan=\"2\">");
       }
          out.println("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
         out.println("</td>");

       out.println("<td valign=\"middle\" width=\"12%\" align=\"left\" rowspan=\"2\">");
       if (club.equals("gcgeorgia")) {
           out.print("<a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1039173\" target=\"_blank\">");
       } else {
           if (zip != null && !zip.equals( "" )) {     // if zipcode provided
               out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\">");
           } else {
               out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\">");
           }
       }
          out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
         out.println("</td>");

         out.println("<td width=\"52%\" align=\"center\">");
            out.println("<font size=\"5\">Member Tee Time Management</font><br>");

         //output the Home, Logout/Exit, and Help links
         out.println("<a class=\"gblHref\" href=\"Member_announce\" target=\"bot\" >Home</a>");
         out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/member_help.htm\" target=\"_blank\" >Help</a>");
         if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page

            out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout</a>");

         } else {      // from web site (MFirst, CE, AMO, etc.)

             out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Exit</a>");

         }
         out.print("&nbsp; &nbsp; &nbsp; &nbsp; <a class=\"gblHref2\" href=\"Member_maintop?mac=no\">(Click Here For New Menus)</a>");
         out.println("</td>");

          if (Safari == true) {
             out.println("<td width=\"24%\" align=\"right\" valign=\"top\" rowspan=\"2\">");
          } else {
             out.println("<td width=\"24%\" align=\"right\" rowspan=\"2\">");
          }
           out.println("<p>");
           out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
            out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>&nbsp;");
           out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
           out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
           out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.&nbsp;");
           out.println("</font></p>");
         out.println("</td>");
       out.println("</tr>");

       if (Safari == true) {
          out.println("<tr height=\"41\" valign=\"bottom\">"); // was top
       } else {
          out.println("<tr height=\"41\" valign=\"bottom\">");
       }
         out.println("<td align=\"center\" nowrap>");
         out.println("<nobr>");
          out.println("<a href=\"Member_select\" target=\"bot\"><img name=\"Mem_Mac_Tee_Times\" src=\"/" +rev+ "/images/Mem_Mac_Tee_Times.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"View the Tee Sheets\"></a>");
          out.println("<a href=\"Member_teelist\" target=\"bot\"><img name=\"Mem_Mac_My_Calendar\" src=\"/" +rev+ "/images/Mem_Mac_My_Calendar.gif\" hspace=\"0\" border=\"0\" title=\"View Your Calendar To See Scheduled Tee Times, Events, Lessons\" alt=\"View Your Calendar\"></a>");
       //   out.println("<a href=\"/" +rev+ "/member_mac_lessons.htm\" target=\"bot\"><img name=\"Mem_Mac_Lessons\" src=\"/" +rev+ "/images/Mem_Mac_Lessons.gif\" hspace=\"0\" border=\"0\" title=\"Sign Up For Lessons or Access Your Scheduled Lessons\" alt=\"Lessons\"></a>");
       //   out.println("<a href=\"Member_events\" target=\"bot\"><img name=\"Mem_Mac_Events\" src=\"/" +rev+ "/images/Mem_Mac_Events.gif\" hspace=\"0\" border=\"0\" title=\"Sing Up For Events or View Upcoming Events\" alt=\"Events\"></a>");
          out.println("<a href=\"/" +rev+ "/member_balt_search.htm\" target=\"bot\"><img name=\"Mem_Mac_Search\" src=\"/" +rev+ "/images/Mem_Mac_Search.gif\" hspace=\"0\" border=\"0\" title=\"Search For Your Past Tee Times\" alt=\"Search\"></a>");
       //   out.println("<a href=\"/" +rev+ "/member_mac_email.htm\" target=\"bot\"><img name=\"Mem_Mac_Email\" src=\"/" +rev+ "/images/Mem_Mac_Email.gif\" hspace=\"0\" border=\"0\" title=\"Send Email To Other Members\" alt=\"Email\"></a>");
       //   out.println("<a href=\"Member_buddy\" target=\"bot\"><img name=\"Mem_Mac_Partners\" src=\"/" +rev+ "/images/Mem_Mac_Partners.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Partner List\" alt=\"\"></a>");
          out.println("<a href=\"Member_services\" target=\"bot\"><img name=\"Mem_Mac_Settings\" src=\"/" +rev+ "/images/Mem_Mac_Settings.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Password and Other Settings\" alt=\"Settings\"></a>");
         out.println("</nobr></td>");
       out.println("</tr>");
      out.println("</table>");
      out.println("</body></html>");
      out.close();
 }
    
    

//*************************************************************
//  doSPAmenu - build a unique nav bar for SPA Services - Lessons Only
//*************************************************************

 private void doSPAmenu(HttpServletResponse resp, PrintWriter out, String club, String zip, int year, boolean Safari) {


   out.println("<html><head>");

   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title> \"ForeTees Member Main Title Page\"</title>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
   out.println("</head>");

   out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

   out.println("<hr class=\"gblHdr\">");           // puts header row under tabs (seperates frames)!!

   out.println("<table width=\"100%\" height=\"82\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");

   out.println("<tr valign=\"top\" height=\"41\">");
   if (Safari == true) {
      out.println("<td width=\"12%\" align=\"center\" rowspan=\"2\">");
   } else {
      out.println("<td width=\"12%\" align=\"center\" valign=\"middle\" rowspan=\"2\">");
   }
      out.println("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
     out.println("</td>");

   out.println("<td valign=\"middle\" width=\"12%\" align=\"left\" rowspan=\"2\">");
   if (club.equals("gcgeorgia")) {
       out.print("<a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1039173\" target=\"_blank\">");
   } else {
       if (zip != null && !zip.equals( "" )) {     // if zipcode provided
           out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\">");
       } else {
           out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\">");
       }
   }
      out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
     out.println("</td>");

     out.println("<td width=\"52%\" align=\"center\">");
     out.println("<font size=\"5\">Member SPA Services Management</font><br>");

     //output the Home, Logout/Exit, and Help links
     out.println("<a class=\"gblHref\" href=\"Member_announce\" target=\"bot\" >Home</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout</a>");
     out.println("</font></td>");

      if (Safari == true) {
         out.println("<td width=\"24%\" align=\"right\" valign=\"top\" rowspan=\"2\">");
      } else {
         out.println("<td width=\"24%\" align=\"right\" rowspan=\"2\">");
      }
       out.println("<p>");
       out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
        out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>&nbsp;");
       out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
       out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
       out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.&nbsp;");
       out.println("</font></p>");
     out.println("</td>");
   out.println("</tr>");

   if (Safari == true) {
      out.println("<tr height=\"41\" valign=\"top\">");
   } else {
      out.println("<tr>");
   }
   out.print("<td align=\"center\">");
   out.print("<!-- Begin code to display images horizontally. -->");
   out.print("<a href=\"Member_teelist\" target=\"bot\"><img name=\"Mem_Mac_My_Calendar\" src=\"/" +rev+ "/images/Mem_Mac_My_Calendar.gif\" hspace=\"0\" border=\"0\" title=\"View Your Calendar To See Scheduled Lessons\" alt=\"View Your Calendar\"></a>");
   out.print("&nbsp;");
   out.print("<a href=\"Member_lesson\" target=\"bot\"><img name=\"Mem_Spa_Schedule\" src=\"/" +rev+ "/images/Mem_Spa_Schedule.gif\" hspace=\"0\" border=\"0\" title=\"Schedule Spa Services at Your Club\" alt=\"Schedule Spa Services\"></a>");
   out.print("&nbsp;");
   out.print("<a href=\"Member_lesson?bio=yes\" target=\"bot\"><img name=\"Mem_Spa_ViewBio\" src=\"/" +rev+ "/images/Mem_Spa_ViewBio.gif\" hspace=\"0\" border=\"0\" title=\"View Spa Service Descriptions\" alt=\"View Spa Service Descriptions\"></a>");
   out.print("&nbsp;");
   out.print("<a href=\"Member_services\" target=\"bot\"><img name=\"Mem_Mac_Settings\" src=\"/" +rev+ "/images/Mem_Mac_Settings.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Password and Other Settings\" alt=\"Settings\"></a>");
   out.print("</td>");
   out.println("</tr>");
   out.println("</table>");
   out.println("</body></html>");
   out.close();

 }  // end of doSPAmenu

}