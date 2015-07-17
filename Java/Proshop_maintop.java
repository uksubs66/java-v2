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

// Foretees imports
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

    //Statement stmt = null;
    //ResultSet rs = null;

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
        out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
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


    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title> \"ForeTees Proshop Main Title Page\"</title>");

    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
    out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");

    out.println("</head>");

    String tmp_grn = (club.startsWith("notify")) ? "-grn" : "";

    out.println("<script type=\"text/javascript\">");
    
    out.println("imgD1 = new Image(46,47);");
    out.println("imgD2 = new Image(46,47);");
    out.println("imgW1 = new Image(46,47);");
    out.println("imgW2 = new Image(46,47);");
    
    if (Utilities.checkDiningLink("pro_main", con) && diningAccess) {
        out.println("imgD1.src = '/"+rev+"/images/dining"+tmp_grn+"-off.gif';");
        out.println("imgD2.src = '/"+rev+"/images/dining"+tmp_grn+"-on.gif';");
    }
    out.println("imgW1.src = '/"+rev+"/images/weather"+tmp_grn+"-off.gif';");
    out.println("imgW2.src = '/"+rev+"/images/weather"+tmp_grn+"-on.gif';");
    
    out.println("function swapImage(imgName, imgObj) {");
    out.println(" if (document.images) {");
    out.println("  document.images[imgName].src=eval(imgObj+'.src');");
    out.println(" }");
    out.println("}");
    
    out.println("</script>");

    out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

    SystemUtils.getProshopMainMenu(req, out, lottery);

     out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
     out.print("<tr><td valign=\"middle\" width=\"10%\" align=\"left\">");
     if (club.equals("tpcpotomac") || club.equals("tpcsouthwind") || club.equals("tpcsugarloaf") || club.equals("tpcwakefieldplantation") || 
         club.equals("tpcboston") || club.equals("tpcjasnapolana") || club.equals("tpcriverhighlands") || club.equals("tpcriversbend") ||
         club.equals("tpccraigranch") || club.equals("tpcsummerlin") || club.equals("tpcsanfranciscobay")){
         out.print("&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.gif\" border=0>");
     } else if (club.equals("deserthighlands")) {
         out.print("&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.png\" border=0>");  
     } else if (club.equals("midland")) {
         out.print("<a href=\"http://www.midlandhillscc.org/\" target=\"_blank\"><img src=\"/" +club+ "/images/logo.jpg\" border=0></a>");
     } else {
         out.print("&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
     }
     out.print("</td>");

     out.print("<td valign=\"middle\" width=\"12%\" align=\"left\" nowrap>&nbsp;&nbsp;");
     if (club.equals("pinehills")) {
         
         // Display the old weather image
         out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\">");
         out.print("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
         
     } else {
         if (club.equals("gcgeorgia")) {

             out.print("<a href=\"http://168.29.150.40/aemn/cgi-bin/AEMN.pl?site=GAAP&report=c\" target=\"_blank\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");

         } else {
              if (zip != null && !zip.equals( "" )) {     // if zipcode provided
                 out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");
              } else {
                 out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\" onmouseover=\"swapImage('img1', 'imgW2')\" onmouseout=\"swapImage('img1', 'imgW1')\">");
              }
         }

         out.print("<img name=img1 src=\"/" +rev+ "/images/weather"+tmp_grn+"-off.gif\" width=46 height=47 border=0 title=\"Check your local weather.\"></a>&nbsp;&nbsp;");
         //out.print("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a><br><br>");
     }

     if(Utilities.checkDiningLink("pro_main", con) && diningAccess) {
        out.print("<a href=\"/"+rev+"/servlet/Proshop_dining?dReq&caller=main\" target=\"bot\" onmouseover=\"swapImage('img2', 'imgD2')\" onmouseout=\"swapImage('img2', 'imgD1')\">");
        out.print("<img name=img2 src=\"/" +rev+ "/images/dining"+tmp_grn+"-off.gif\" width=46 height=47 border=0 title=\"Make a dining request.\"></a><br>");
     }
     
     
     if (IS_TLT == true || sess_activity_id > 0 || club.equals("denvercc")) {    //skip Mobile image if Notification or FlxRez system 

        out.print("<br>");

     } else {       

        out.print("<a href=\"/"+rev+"/servlet/Common_mobile_help\" target=\"_blank\">");    // link to Mobile Instructions
        out.print("<img src=\"/" +rev+ "/mobile/images/MobileNav.gif\" border=0></a>");
     }
                 
     
     out.print("</td>");

     out.println("<td width=\"54%\" align=\"center\">");
     
     String tmp_title = "Golf Shop Tee Time Management";
     //int root_id = 0;
     
     // the activity_id stored in the session block is now always a root id
     if (sess_activity_id > 0) {
         
         //try { root_id = getActivity.getRootIdFromActivityId(sess_activity_id, con); }
         //catch (Exception ignore) {}
         
         try { tmp_title = getActivity.getActivityName(sess_activity_id, con); } // root_id
         catch (Exception ignore) {}
         
         tmp_title += " Management System";
         
     }
     
     out.println("<font size=\"5\">" + ((IS_TLT) ? "Golf Shop Course Management" : tmp_title) + "</font><br>");

     //output the Home, Logout/Exit, and Help links
     out.println("<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_announce\" target=\"bot\" >Home</a>");
     if ( getActivity.isConfigured(con) ) out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_activities\" target=\"bot\" >Activities</a>"); // server_id == 4 && 
     //if (server_id == 4) out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"bot\" >Demo Clubs</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_probs\" target=\"bot\">Support</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Logout</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref3\" href=\"/" +rev+ "/servlet/Proshop_features\" target=\"bot\" >New Features</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref3\" href=\"/" +rev+ "/proshop_newsletters.htm\" target=\"bot\" >Newsletters</a>");
     if (Utilities.isGuestTrackingConfigured(sess_activity_id, con) || club.startsWith("demo") || club.startsWith("tpc") || club.equals("desertforestgolfclub")) out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Common_guestdb\" target=\"bot\" >Guests</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/proshop_help.htm\" target=\"_blank\" >Help</a>");

     // temp!!!
     //out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_report_rounds_comparison\" target=\"bot\" >Comparison Report</a>");

     out.println("<br><br><br></td>");
      out.print("<td width=\"24%\"align=\"middle\">");
       out.print("<a href=\"http://www.foretees.com\" title=\"Server #" + Common_Server.SERVER_ID + "\" target=\"_blank\">");
       if (sess_activity_id > 0) {
           out.print("<img src=\"/" +rev+ "/images/FlxRez_nav.gif\" border=0></a>&nbsp;");
       } else {
           out.print("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
       }
       out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
       out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
       out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.</font>");
       
       out.println("<br><iframe src=\"/v5/servlet/clock?club=" + club + "\" id=ifClock style=\"width:80px;height:16px\" scrolling=no frameborder=no></iframe>");
       
     out.print("</td>");
    out.print("</tr>");
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
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
    out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
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
         out.print("<a href=\"http://168.29.150.40/aemn/cgi-bin/AEMN.pl?site=GAAP&report=c\" target=\"_blank\">");
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
     out.println("<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_announce\" target=\"bot\" >Home</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_probs\" target=\"bot\">Support</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Logout</a>");
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
          out.println("<a href=\"/" +rev+ "/servlet/Proshop_select\" target=\"bot\"><img name=\"Proshop Tee Times\" src=\"/" +rev+ "/images/ProTeeSheetsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"Access the Tee Sheets\"></a>");
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
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
    out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("</head>");
    out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

     out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
     out.println("<tr valign=\"top\" height=\"41\"><td valign=\"middle\" width=\"10%\" align=\"left\" rowspan=\"2\">");
     out.println("<p>&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.jpg\" border=0></p>");
     out.println("</td>");

     out.println("<td valign=\"middle\" width=\"12%\" align=\"left\" rowspan=\"2\">");
     if (club.equals("gcgeorgia")) {
         out.print("<a href=\"http://168.29.150.40/aemn/cgi-bin/AEMN.pl?site=GAAP&report=c\" target=\"_blank\">");
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
     out.println("<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_announce\" target=\"bot\" >Home</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Proshop_probs\" target=\"bot\">Support</a>");
     out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Logout</a>");
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
          out.println("<a href=\"/" +rev+ "/servlet/Proshop_select\" target=\"bot\"><img name=\"Proshop Tee Times\" src=\"/" +rev+ "/images/ProTeeSheetsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"Access the Tee Sheets\"></a>");
          out.println("<a href=\"/" +rev+ "/servlet/Proshop_events1\" target=\"bot\"><img name=\"Proshop Events\" src=\"/" +rev+ "/images/ProEventsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"View Events or Register Members for Events\" alt=\"Access the Events\"></a>");
          out.println("<a href=\"/" +rev+ "/servlet/Proshop_lesson\" target=\"bot\"><img name=\"Proshop Lessons\" src=\"/" +rev+ "/images/ProLessonsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"Manage Lessons\" alt=\"Manage Lessons\"></a>");
          out.println("<a href=\"/" +rev+ "/servlet/Proshop_services\" target=\"bot\"><img name=\"Proshop Services\" src=\"/" +rev+ "/images/ProSettingsNoDrop.gif\" hspace=\"0\" border=\"0\" title=\"Change User Settings\" alt=\"Proshop Settings\"></a>");
         out.println("</td>");
       out.println("</tr>");
    out.println("</table>");
    out.println("</body></html>");    
    out.close();
 }

}
