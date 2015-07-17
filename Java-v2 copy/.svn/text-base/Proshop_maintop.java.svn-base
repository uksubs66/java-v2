/***************************************************************************************
 *   Proshop_maintop:  This servlet will display the Proshop's navigation bar (Top of Page).
 *
 *
 *   called by:  proshop_welms
 *
 *   created:  7/22/2003   Bob P.
 *
 *
 *   last updated:
 *
 *     10/23/13  Fixed Activities link so it will display if a club has no golf and enough activities, and adjusted how the quick-switch links for clubs with no golf and 2 activities.
 *      9/09/13  Add 'Member View' link so proshop users can open a new window as a member user.
 *      7/16/13  Royal Oaks CC - Dallas (roccdallas) - Added custom to use an alternate club logo image in the proshop menu frame.
 *      5/29/13  Desert Mountain Club (desertmountain) - Added custom weather link.
 *      3/11/13  Add custom link for Interlachen to check member guest quotas.
 *     12/19/12  Mesa Verde CC (mesaverdecc) - Added custom weather link for Proshop users.
 *     11/21/12  Fixes for new doctype decleration - removed the previous IE9 mode meta tag
 *     11/14/12  Added meta tag to force IE9 rendering mode - fix for IE10 proshop menus
 *      9/12/12  Restricted the demosandbox site from seeing most of the non-drop down proshop menus.
 *      7/18/12  Denver CC (denvercc) - Added custom weather link for Proshop users.
 *      6/18/12  Add Boxgroove logo if confgured for seamless link to their network.
 *      8/30/11  Adjust the size of the weather and mobile images.
 *      5/17/11  Activities link will be hidden on the golf side if FlxRez Staging mode is turned on.
 *      4/20/11  Golf Club of Georgia (gcgeorgia) - Updated custom weather link.
 *      2/22/11  Implemented fast-activity-switching mechanism used on the member side to skip the activities selection page when only golf
 *               and 1 activity (or no golf and 2 activities) are found in the system.
 *      9/02/10  Replace moble image with new icon to match the dining and weather icons
 *      9/01/10  Added ability to track hits to the external weather links
 *      4/18/10  Temporary link to guest tracking system for Demo/TPC/Desert Forest
 *      2/17/10  Add "Newsletters" link.
 *      2/03/10  Denver CC - do not show the Mobile link (they don't want members using it yet).
 *      1/26/10  Add Mobile image/link for golf tee time users - mobile help.
 *     10/31/09  Add yesRez logo for Activity systems (FlxRez_nav.gif).
 *      8/19/09  Change main header to reflect the current default activity
 *      4/15/09  Make the upper left-hand logo a link to Midland Hills' club website
 *      4/09/09  Use the old weather icon for Pine Hills CC (pinehills)
 *      1/27/09  Add new weather/dining icons
 *     12/22/08  Use logo.png instead of logo.jpg for desert highlands (for transparency reasons)
 *     10/27/08  Use logo.gif instead of logo.jpg for new TPC clubs
 *     10/08/08  Added new javascript clock to display server time
 *      8/13/08  Add a title tag to the anchor for the ForeTees logo to identify the server id.
 *      7/29/08  Added seperate weather link for GC of Georgia (gcgeorgia)
 *     04/29/08  Add New Features link.  
 *     02/27/08  Restrict proshop5 user from demov4 site to only the tee sheet to allow ProshopKeeper 
 *               access for testing their POS interface.
 *     04/14/07  Congressional - add limited login for proshop9 (Tony Monagus - case #1112).
 *     07/20/06  Changes for TLT System
 *     10/11/04  Ver 5 - Change format to provide drop-down menus.
 *      4/30/04  RDP  Add club logo and move ForeTees logo.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// ForeTees imports
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;

public class Proshop_maintop extends HttpServlet {

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

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder
    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    // Define parms
    int lottery = 0;             // lottery support = no

    //int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!

    String club = "";
    String zip = "";
    String user = "";
    
    // Check proshop user feature access for appropriate access rights
    boolean diningAccess = SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out);
    boolean showMobile = true;                                  // show Mobile Help Link?  Default = Yes

    club = (String)session.getAttribute("club");
    user = (String)session.getAttribute("user");
    zip = (String)session.getAttribute("zipcode");
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    
    //
    // See what activity mode we are in
    //
    int sess_activity_id = 0;

    try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
    catch (Exception ignore) { }
    
    //
    //  See if we are in teh timeless tees mode
    //
    int tmp_tlt = (Integer)session.getAttribute("tlt");
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    if (templott != null && !templott.equals( "" )) {

        lottery = Integer.parseInt(templott);
    }
    
    String boxgroove = "";
    String boxUser = "";
    String boxPW = "";
    String club_logo_name = "logo.jpg";

    //  See if this club uses the old dining request system or Boxgroove and wants a link to that network
    
    boolean oldDining = Utilities.checkDiningLink("pro_main", con);
    boxgroove = Utilities.checkBoxgroove(con);
    
    if (!boxgroove.equals("")) {     // if Boxgroove configured
        
        StringTokenizer tok = new StringTokenizer( boxgroove, "|" );

        if ( tok.countTokens() == 2 ) {         // username and password

            boxUser = tok.nextToken();      
            boxPW = tok.nextToken();        
        }
    }
    
    //
    //  Check if we should show the Mobile Help link (if the club does not want members to know about the mobile)
    //
    //showMobile = Utilities.checkMobileSupport (con);      //  show the Mobile Help link?
 
    
    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    int year = cal.get(Calendar.YEAR);            // get the year

    //
    //  Build the HTML page (main menu)
    //
    if (club.equals( "demov4" ) && user.equalsIgnoreCase( "proshop5" )) {     // if proshop5 on demov4 site
      
       doPSKmenu(resp, out, club, zip, year);
       return;
    }

    if (club.equals( "congressional" ) && user.equalsIgnoreCase( "proshop9" )) {     // if proshop9 at Congressional
            
       doCongmenu(resp, out, club, zip, year);
       return;
    }
    
    if (club.equals("roccdallas")) {
        club_logo_name = "logo2.jpg";
    }

/*
    //out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    out.println("<!DOCTYPE html>");
    out.println("<html>\n<head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
  //out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
  //out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\">");  // force IE9 rendering mode - fix for IE10 proshop menus  EmulateIE9
    out.println("<title>\"ForeTees Proshop Software\"</title>");

    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web%20utilities/foretees.css\" type=\"text/css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
  //out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/jquery.js\"></script>");
  //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/proshop.css\" type=\"text/css\">"); // put any new css in here? doctype fixes?
*/

    out.println( SystemUtils.HeadTitle2("ForeTees Proshop Software") );
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web%20utilities/foretees.css\" type=\"text/css\">");


    String tmp_grn = (club.startsWith("notify")) ? "-grn" : "";

    out.println("<script type=\"text/javascript\">");
    
    out.println("imgD1 = new Image(46,69);");
    out.println("imgD2 = new Image(46,69);");
    out.println("imgW1 = new Image(46,69);");
    out.println("imgW2 = new Image(46,69);");
    out.println("imgM1 = new Image(46,69);");
    out.println("imgM2 = new Image(46,69);");
    out.println("imgB1 = new Image(46,69);");        // Boxgroove
    
    if (oldDining && diningAccess) {
        out.println("imgD1.src = '/"+rev+"/images/dining"+tmp_grn+"-off.gif';");
        out.println("imgD2.src = '/"+rev+"/images/dining"+tmp_grn+"-on.gif';");
    }
    out.println("imgW1.src = '/"+rev+"/images/weather"+tmp_grn+"-off.gif';");
    out.println("imgW2.src = '/"+rev+"/images/weather"+tmp_grn+"-on.gif';");
    out.println("imgM1.src = '/"+rev+"/mobile/images/mobile-off.gif';");
    out.println("imgM2.src = '/"+rev+"/mobile/images/mobile-on.gif';");
    if (!boxPW.equals("")) {                                 // if Boxgroove configured
        out.println("imgB1.src = '/"+rev+"/images/boxgroove-link.gif';");
    }
    
    out.println("function swapImage(imgName, imgObj) {");
    out.println(" if (document.images) {");
    out.println("  document.images[imgName].src=eval(imgObj+'.src');");
    out.println(" }");
    out.println("}");
    
    out.println("function countHit(provider) {");
    out.println(" $.get('stats?countHit&provider='+provider, function(data) { });");
    out.println("}");

    out.println("</script>");

    out.println("<style>");
    out.println("" +
            "body {" +
             "margin:0px;" +
             "padding:0px;" +
            "}" +
            "img {" +
             "border:0px;" +
            "}" +
            ".mainTable{" +
             "border:0;" +
             "width:100%;" +
             "padding:0px;" +
             "background-color:#CCCCAA;" +
             "" +
            "}");
    out.println("</style>");

    out.println("</head>");

    //out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");
    out.println("<body>");

    SystemUtils.getProshopMainMenu(req, out, lottery);

     out.println("<table id=\"mainTable\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
     out.print("<tr valign=\"top\"><td valign=\"middle\" width=\"10%\" align=\"left\">");
     if (club.equals("tpcpotomac") || club.equals("tpcsouthwind") || club.equals("tpcsugarloaf") || club.equals("tpcwakefieldplantation") || 
         club.equals("tpcboston") || club.equals("tpcjasnapolana") || club.equals("tpcriverhighlands") || club.equals("tpcriversbend") ||
         club.equals("tpccraigranch") || club.equals("tpcsummerlin") || club.equals("tpcsanfranciscobay")){
         out.print("&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.gif\">");
     } else if (club.equals("deserthighlands")) {
         out.print("&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.png\">");  
     } else if (club.equals("midland")) {
         out.print("<a href=\"http://www.midlandhillscc.org/\" target=\"_blank\"><img src=\"/" +club+ "/images/logo.jpg\"></a>");
     } else {
         out.print("&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/" + club_logo_name + "\">");
     }
     out.print("</td>");

     out.print("<td valign=\"top\" width=\"12%\" align=\"left\" nowrap>&nbsp;&nbsp;");
     if (club.equals("pinehills")) {
         
         // Display the old weather image
         out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&amp;zipcode=" +zip+ "\" target=\"_blank\">");
         out.print("<img src=\"/" +rev+ "/images/weather.gif\"></a>");
         
     } else if (club.equals("gcgeorgia")) {

         out.print("<a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1039173\" target=\"_blank\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");

     } else if (club.equals("denvercc")) {

         out.print("<a href=\"http://weather.weatherbug.com/CO/Denver-weather/local-forecast/7-day-forecast.html?zcode=z5545&amp;units=0\" target=\"_blank\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");

     } else if (club.equals("mesaverdecc")) {

         out.print("<a href=\"http://www.weatherlink.com/user/mesaverdecc\" target=\"_blank\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");

     } else if (club.equals("desertmountain")) {
         
         out.print("<a href=\"http://www.weather.com/outlook/recreation/golf/weather/weekend/?clubId=1048342\" target=\"_blank\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");
         
     } else {

          // ALL OTHER CLUBS
          if (zip != null && !zip.equals( "" )) {     // if zipcode provided
             out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" onclick=\"countHit('ACCU')\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");
          } else {
             out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\" onclick=\"countHit('ACCU')\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");
          }
     }

     out.print("<img name=img1 src=\"/" +rev+ "/images/weather"+tmp_grn+"-off.gif\" width=46 height=69 border=0 title=\"Check your local weather.\" alt=\"\"></a>&nbsp;&nbsp;");
     //out.print("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a><br><br>");
     

     //if(Utilities.checkDiningLink("pro_main", con) && diningAccess) {
     if (oldDining && diningAccess) {
        out.print("<a href=\"Proshop_dining?dReq&caller=main\" target=\"bot\" onmouseover=\"swapImage('img2', 'imgD2')\" onmouseout=\"swapImage('img2', 'imgD1')\">");
        out.print("<img name=img2 src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=46 height=69 border=0 title=\"Make a dining request.\"></a>");
     }

     if (IS_TLT == false && sess_activity_id == 0 && !club.equals("denvercc")) {    //skip Mobile image if Notification or FlxRez system 
     
        out.print("&nbsp;&nbsp;<a href=\"Common_mobile_help\" target=\"_blank\" onmouseover=\"swapImage('img3', 'imgM2')\" onmouseout=\"swapImage('img3', 'imgM1')\">");
        out.print("<img name=img3 src=\"/" +rev+ "/mobile/images/mobile-off.gif\" width=46 height=69 border=0 title=\"Mobile User Help\" style=\"position:relative;top:-1px\"></a>");

        //out.print("<a href=\"Common_mobile_help\" target=\"_blank\">");    // link to Mobile Instructions
        //out.print("<img src=\"/" +rev+ "/mobile/images/MobileNav.gif\" border=0></a>");
        
        //  Golf and Tee Time Club - check for Boxgroove link
        if (!boxPW.equals("")) {
         
            out.print("&nbsp;&nbsp;<a href=\"https://www.boxgroove.com?CoreFObserver=login&amp;username=" +boxUser+ "&amp;pass=" +boxPW+ "\" target=\"_blank\">");
            out.print("<img name=img4 src=\"/" +rev+ "/images/boxgroove-link.gif\" width=46 height=69 border=0 title=\"Link to Boxgroove Network\" style=\"position:relative;top:-1px\"></a>");     
        }
     }
                 
     
     out.print("<br></td>");

     out.println("<td width=\"54%\" align=\"center\">");

     if (user.equals("proshopfb")) {
     
        out.println("<font size=\"5\">ForeTees Dining Integration Management</font><br>");

     } else {

         String tmp_title = "Golf Shop Tee Time Management";

         // the activity_id stored in the session block is now always a root id
         if (sess_activity_id > 0) {

             try { tmp_title = getActivity.getActivityName(sess_activity_id, con); }
             catch (Exception ignore) {}

             tmp_title += " Management System";

         } else {

            tmp_title = "Golf Shop Course Management";

         }

         out.println("<font size=\"5\">" + tmp_title + "</font><br>");

     }

     //output the Home, Logout/Exit, and Help links
     out.println("<a class=\"gblHref\" href=\"Proshop_announce\" target=\"bot\" >Home</a>");
     //if ( getActivity.isConfigured(con) ) out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Proshop_activities\" target=\"bot\" >Activities</a>"); // server_id == 4 &&


     //
     // See if we are going to check for fast-activity-switching
     //
     if (getActivity.isConfigured(con) && (!getActivity.isStagingMode(con) || sess_activity_id != 0) && !club.equals("demosandbox")) {

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

                 if ((rs.getInt("foretees_mode") == 1 && count >= 2) || (rs.getInt("foretees_mode") == 0 && count >= 3)) {

                     out.println("<!-- NO FAST SWITCH: golf enabled and more than 1 other activity defined -->");

                     // they have golf AND more than one activity
                     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" " +
                                 "href=\"Proshop_activities\" target=\"bot\">Activities</a>");

                 } else {

                     if (rs.getInt("foretees_mode") == 1) {

                         out.println("<!-- USE FAST SWITCH: golf enabled -->");

                         // they have golf so add it
                         activity_names[0] = "Golf";
                         activity_ids[0] = 0;

                         // add the other activity
                         activity_names[1] = rs.getString("activity_name");
                         activity_ids[1] = rs.getInt("activity_id");

                         // they have golf + one activity (or no golf and two activities)
                         out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" " +
                                     "href=\"Proshop_jump?switch&" +
                                     "activity_id=" + activity_ids[(sess_activity_id == 0) ? 1 : 0] + "\" target=\"_top\"" +
                                     ">" + activity_names[(sess_activity_id == 0) ? 1 : 0] + "</a>");

                     } else {

                         out.println("<!-- USE FAST SWITCH: no golf enabled -->");

                         // no golf so must be two different root activities

                         activity_names[0] = rs.getString("activity_name");
                         activity_ids[0] = rs.getInt("activity_id");

                         if (rs.next()) {

                             activity_names[1] = rs.getString("activity_name");
                             activity_ids[1] = rs.getInt("activity_id");
                             
                         }
                         
                         // they have golf + one activity (or no golf and two activities)
                         out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" " +
                                     "href=\"Proshop_jump?switch&" +
                                     "activity_id=" + activity_ids[(activity_ids[0] == sess_activity_id ? 1 : 0)] + "\" target=\"_top\"" +
                                     ">" + activity_names[(activity_ids[0] == sess_activity_id ? 1 : 0)] + "</a>");
                     }
                 }
             }

         } catch (Exception exc) {

             Utilities.logError("Proshop_maintop: Error loading activity count & names. club=" + club + ", err=" + exc.toString());

         } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { stmt.close(); }
             catch (Exception ignore) {}

         }
     }  // end isConfigured()

     if (!club.equals("demosandbox")) {
         out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Proshop_probs\" target=\"bot\">Support</a>");
     }
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout</a>");
     
     if (!club.equals("demosandbox")) {
         /*
         if (club.equals("demobrad")) {
             out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Proshop_lesson?group=yes\" target=\"bot\" >Group Lessons</a>");
         }*/
         out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref3\" href=\"Proshop_features\" target=\"bot\" >New Features</a>");
         out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref3\" href=\"/" +rev+ "/proshop_newsletters.htm\" target=\"bot\" >Newsletters</a>");
         if (Utilities.isGuestTrackingConfigured(sess_activity_id, con) || club.startsWith("demo") || club.startsWith("tpc") || club.equals("desertforestgolfclub")) out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Common_guestdb\" target=\"bot\" >Guests</a>");
         out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/proshop_help.htm\" target=\"_blank\" >Help</a>");
     }
     
   //  if (club.startsWith("demo") || club.equals("bentcreek")) {
     if (club.startsWith("demo") && !club.equals("demosandbox")) {
         
       // out.println("&nbsp;&nbsp;&nbsp;&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Proshop_invoicing\" target=\"bot\" >Billing</a>");
     }
     
     
     //   Add Member View Link
     out.println("&nbsp;&nbsp;&nbsp;&nbsp;<label class=\"gblSep\">|</label>&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"gblHref\" href=\"Proshop_memberView\" target=\"_top\" >Member View</a>");

     
     if (club.equals("interlachen")) {        // link to check members' guest quotas
         
        out.println("&nbsp;&nbsp;&nbsp;&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Proshop_custom_memlist\" target=\"bot\" >Quotas</a>");
     }
         

     // temp!!!
     //out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"gblHref\" href=\"Proshop_report_rounds_comparison\" target=\"bot\" >Comparison Report</a>");

     out.println("<br><br><br></td>");
      out.print("<td width=\"24%\" align=\"center\" class=\"header_right_end\">");
       out.print("<a href=\"http://www.foretees.com\" title=\"Server #" + Common_Server.SERVER_ID + "\" target=\"_blank\">");
       if (sess_activity_id > 0) {
           out.print("<img src=\"/" +rev+ "/images/FlxRez_nav.gif\" border=0></a>&nbsp;");
       } else {
           out.print("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
       }
       out.print("<div class=\"mini_text\">Copyright &#169; ForeTees, LLC<br></div><div class=\"mini_text\">" +year+ " All rights reserved.</div>");
       
       /*
       out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
       out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
       out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.</font>");
        *
        */

       out.print("<iframe src=\"clock?club=" + club + "\" id=ifClock style=\"width:100px;height:16px\" scrolling=\"no\" frameborder=\"no\"></iframe>");
       
    out.print("</td>");
    out.println("</tr>");
    out.println("</table>");
    out.println("</body></html>");
    out.close();

 }  // end of doGet


//*************************************************************
//  doPSKmenu - build a unique nav bar for ProshopKeeper - Tee Times Only
//*************************************************************
 private void doPSKmenu(HttpServletResponse resp, PrintWriter out, String club, String zip, int year) {
 
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title> \"ForeTees Proshop Main Title Page\"</title>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web%20utilities/foretees.css\" type=\"text/css\">");
    out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
    out.println("</head>");
    out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

     out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
     out.println("<tr valign=\"top\" height=\"41\"><td valign=\"middle\" width=\"10%\" align=\"left\" rowspan=\"2\">");
     if (club.equals("tpcpotomac") || club.equals("tpcsouthwind") || club.equals("tpcsugarloaf") || club.equals("tpcwakefieldplantation") || 
         club.equals("tpcboston") || club.equals("tpcjasnapolana") || club.equals("tpcriverhighlands") || club.equals("tpcriversbend") ||
         club.equals("tpccraigranch") || club.equals("tpcsummerlin") || club.equals("tpcsanfranciscobay")) {
         out.println("<p>&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.gif\" border=0></p>");
     } else {
         out.println("<p>&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.jpg\" border=0></p>");
     }
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
      out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a><br><br>");
     out.println("</td>");
     out.println("<td width=\"54%\" align=\"center\">");
        out.println("<font size=\"5\">Golf Shop Tee Time Management</font><br>");

     //output the Home, Logout/Exit, and Help links
     out.println("<a class=\"gblHref\" href=\"Proshop_announce\" target=\"bot\" >Home</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Proshop_probs\" target=\"bot\">Support</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/proshop_help.htm\" target=\"_blank\" >Help</a>");

     out.println("</td>");
      out.println("<td width=\"24%\"align=\"middle\" rowspan=\"2\">");
       out.println("<p>");
       out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
       out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
       out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
       out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
       out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.");
       out.println("</font></p>");
     out.println("</td>");
    out.println("</tr>");
       out.println("<tr height=\"41\" valign=\"bottom\">");
         out.println("<td align=\"center\">");
         out.println("<!-- Begin code to display images horizontally. -->");
          out.println("<a href=\"Proshop_select\" target=\"bot\"><img name=\"Proshop Tee Times\" src=\"/" +rev+ "/images/ProTeeSheetsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"Access the Tee Sheets\"></a>");
         out.println("</td>");
       out.println("</tr>");
    out.println("</table>");
    out.println("</body></html>");   
    out.close();
 }

//*************************************************************
//  doCongmenu - build a unique nav bar for Congressional
//*************************************************************
 private void doCongmenu(HttpServletResponse resp, PrintWriter out, String club, String zip, int year) {

    //
    //  Congressional - proshop9 (Tony Monagus call center) - display limited menu.
    //  
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title> \"ForeTees Proshop Main Title Page\"</title>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web%20utilities/foretees.css\" type=\"text/css\">");
    out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
    out.println("</head>");
    out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

     out.println("<p>&nbsp;&nbsp;&nbsp;No Longer Supported</p>");
    
    /*
     out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
     out.println("<tr valign=\"top\" height=\"41\"><td valign=\"middle\" width=\"10%\" align=\"left\" rowspan=\"2\">");
     out.println("<p>&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.jpg\" border=0></p>");
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
      out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a><br><br>");
     out.println("</td>");
     out.println("<td width=\"54%\" align=\"center\">");
        out.println("<font size=\"5\">Golf Shop Tee Time Management</font><br>");

     //output the Home, Logout/Exit, and Help links
     out.println("<a class=\"gblHref\" href=\"Proshop_announce\" target=\"bot\" >Home</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Proshop_probs\" target=\"bot\">Support</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"Logout\" target=\"_top\" >Logout</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/proshop_help.htm\" target=\"_blank\" >Help</a>");

     out.println("</td>");
      out.println("<td width=\"24%\"align=\"middle\" rowspan=\"2\">");
       out.println("<p>");
       out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
       out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
       out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
       out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
       out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.");
       out.println("</font></p>");
     out.println("</td>");
    out.println("</tr>");
       out.println("<tr height=\"41\" valign=\"bottom\">");
         out.println("<td align=\"center\">");
         out.println("<!-- Begin code to display images horizontally. -->");
          out.println("<a href=\"Proshop_select\" target=\"bot\"><img name=\"Proshop Tee Times\" src=\"/" +rev+ "/images/ProTeeSheetsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"Access the Tee Sheets\"></a>");
          out.println("<a href=\"Proshop_events1\" target=\"bot\"><img name=\"Proshop Events\" src=\"/" +rev+ "/images/ProEventsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"View Events or Register Members for Events\" alt=\"Access the Events\"></a>");
          out.println("<a href=\"Proshop_lesson\" target=\"bot\"><img name=\"Proshop Lessons\" src=\"/" +rev+ "/images/ProLessonsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"Manage Lessons\" alt=\"Manage Lessons\"></a>");
          out.println("<a href=\"Proshop_services\" target=\"bot\"><img name=\"Proshop Services\" src=\"/" +rev+ "/images/ProSettingsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"Change User Settings\" alt=\"Proshop Settings\"></a>");
         out.println("</td>");
       out.println("</tr>");
    out.println("</table>");
     */
    out.println("</body></html>");    
    out.close();
 }

}
