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

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
    }

    String caller = "";
    String club = "";
    String zip = "";
    String mship = "";

    boolean MAC = false;
    boolean Safari = false;
    boolean Firefox = false;

    caller = (String)session.getAttribute("caller");               // get caller (other web site?)
    club = (String)session.getAttribute("club");
    zip = (String)session.getAttribute("zipcode");
    mship = (String)session.getAttribute("mship");
    
    //
    //  See if we are in teh timeless tees mode
    //
    int tmp_tlt = (Integer)session.getAttribute("tlt");
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    //
    //  If we called ourself to switch to 'MAC' mode (old menus) - see link below "Click Here if Menus Fail"
    //
    if (req.getParameter("mac") != null) {

        MAC = true;        // set Mac mode
        session.setAttribute("mac", "yes");         // set MAC option
    } else {        // see if MAC option already set in session

        String macOpt = (String)session.getAttribute("mac");     
        if (macOpt != null && macOpt.equals( "yes" )) {
            MAC = true;        // set Mac mode
        }
    }

    //
    //  Check for a MAC user and the browser type
    //
    String ua = req.getHeader("User-Agent");               // browser settings

    if (ua != null) {                                      // if settings provided

      ua = ua.toLowerCase();                              // convert to all lower case

      if (ua.indexOf("mac") != -1) {                      // if a MAC system and

         if ((ua.indexOf("msie") != -1) || (ua.indexOf("safari") != -1)) {    // if a IE or Safari browser

            MAC = true;

            if (ua.indexOf("safari") != -1) {    // if Safari browser

               Safari = true;
            }
         }

      } else {

         if (ua.indexOf("webtv") != -1) {      // if a WebTV system

            MAC = true;                        // treat like a Mac

         } else {

            if (ua.indexOf("firefox") != -1) {      // if FireFox browser

               Firefox = true;                        // indicate so
            }
         }
      }
    }

    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    int year = cal.get(Calendar.YEAR);            // get the year


    //
    //  If IS_TLT configured club
    //
    if (IS_TLT) {
        
        //
        //  Build the HTML page (main menu) for all other users
        //
        out.println("<html><head>");

        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
        out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
        out.println("<title> \"ForeTees Member Main Title Page\"</title>");
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
        out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");

        out.println("</head>");

        out.println("<body>");

        SystemUtils.getMemberMainMenu(req, out, caller);

        out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
        out.println("<tr valign=\"top\">");

        out.print("<td width=\"12%\" align=\"center\" valign=\"middle\">");
        out.print("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
        out.print("</td>\n");

        out.print("<td valign=\"middle\" width=\"12%\" align=\"center\">");
        if (zip != null && !zip.equals( "" )) {     // if zipcode provided
           out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\">");
        } else {
           out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\">");
        }
        out.print("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
        out.print("<br><br>");

        // link in case menus do not work for user
        out.print("<a class=\"gblHref2\" href=\"/" +rev+ "/servlet/Member_maintop?mac=yes\">Click Here if Menus Fail</a>");

        out.print("</td>\n");

        out.println("<td width=\"52%\" align=\"center\">");
        out.println("<font size=\"5\">Member Course Access Management</font><br>");

        //output the Home, Logout/Exit, and Help links
        if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {  // if from foretees login page
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");     // shift it over a little
        }
        out.println("<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Member_announce\" target=\"bot\" >Home</a>");
        out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Member_help\" target=\"_blank\" >Help</a>");
        if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page
          out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Logout </a>");
          out.println("<font size=\"1\" face=\"Verdana, Arial, Helvetica, Sans-serif\">");
          out.println("&nbsp;&nbsp;&nbsp; (Please logout when done)</font>");
        } else {      // from web site (MFirst, CE, AMO, etc.)
          out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Exit</a>");
        }
        out.println("</td>");

        out.print("<td width=\"24%\" align=\"center\" valign=\"middle\">");
         if (!club.equals( "westchester" ) && !club.equals( "weeburn" )) {
            out.print("<a href=\"http://www.foretees.com\" target=\"_blank\">");
            out.print("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
         }
         out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
         out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
         out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.");
         out.print("</font>\n");

        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</body></html>");

    } else {

       //
       //  IF Cherry Hills - if member is a caddie, then limit the tabs they get
       //
       if (!club.equals( "cherryhills" ) || !mship.equalsIgnoreCase( "caddies" )) {

          //
          //  Proceed according to user type (Mac with IE or Safari can't use drop-down menus)
          //
          if (MAC == false) {

             //
             //  Build the HTML page (main menu) for all other users
             //
             out.println("<html><head>");

             out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
             out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
             out.println("<title> \"ForeTees Member Main Title Page\"</title>");
             out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
             out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");

             out.println("</head>");

             out.println("<body>");

             SystemUtils.getMemberMainMenu(req, out, caller);

             out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
             out.println("<tr valign=\"top\">");

              out.print("<td width=\"12%\" align=\"center\" valign=\"middle\">");
              out.print("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
              out.print("</td>\n");

              out.print("<td valign=\"middle\" width=\"12%\" align=\"center\">");
                if (zip != null && !zip.equals( "" )) {     // if zipcode provided
                   out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\">");
                } else {
                   out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\">");
                }
                out.print("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
                out.print("<br><br>");

                // link in case menus do not work for user
                out.print("<a class=\"gblHref2\" href=\"/" +rev+ "/servlet/Member_maintop?mac=yes\">Click Here if Menus Fail</a>");

               out.print("</td>\n");

               out.println("<td width=\"52%\" align=\"center\">");
               out.println("<font size=\"5\">Member Tee Time Management</font><br>");

               //output the Home, Logout/Exit, and Help links
               if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {  // if from foretees login page
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");     // shift it over a little
               }
               out.println("<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Member_announce\" target=\"bot\" >Home</a>");
               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Member_help\" target=\"_blank\" >Help</a>");
               if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page
                  out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Logout </a>");
                  out.println("<font size=\"1\" face=\"Verdana, Arial, Helvetica, Sans-serif\">");
                  out.println("&nbsp;&nbsp;&nbsp; (Please logout when done)</font>");
               } else {      // from web site (MFirst, CE, AMO, etc.)
                  out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Exit</a>");
               }
               out.println("</td>");

               out.print("<td width=\"24%\" align=\"center\" valign=\"middle\">");
                 if (!club.equals( "westchester" ) && !club.equals( "weeburn" )) {
                    out.print("<a href=\"http://www.foretees.com\" target=\"_blank\">");
                    out.print("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
                 }
                 out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
                 out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
                 out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.");
                 out.print("</font>\n");

               out.println("</td>");
             out.println("</tr>");
            out.println("</table>");
             out.println("</body></html>");

          } else {

             //
             //  Build the HTML page (main menu) for MAC users with IE or Safari - Use Old Style Buttons
             //
             out.println("<html><head>");

             out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
             out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
             out.println("<title> \"ForeTees Member Main Title Page\"</title>");
             out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
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
                if (zip != null && !zip.equals( "" )) {     // if zipcode provided
                   out.println("<a href=\"http://www.accuweather.com/adcbin2/local_index?zipcode=" +zip+ "\" target=\"_blank\">");
                } else {
                   out.println("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\">");
                }
                out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
               out.println("</td>");

               out.println("<td width=\"52%\" align=\"center\">");
                  out.println("<font size=\"5\">Member Tee Time Management</font><br>");

               //output the Home, Logout/Exit, and Help links
               out.println("<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Member_announce\" target=\"bot\" >Home</a>");
               if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page
                  out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Logout</a>");
               } else {      // from web site (MFirst, CE, AMO, etc.)
                  out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Exit</a>");
               }
               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/member_help.htm\" target=\"_blank\" >Help</a>");
               out.println("</td>");

                if (Safari == true) {
                   out.println("<td width=\"24%\" align=\"center\" valign=\"top\" rowspan=\"2\">");
                } else {
                   out.println("<td width=\"24%\" align=\"center\" rowspan=\"2\">");
                }
                 out.println("<p>");
                 out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
                  out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
                 out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
                 out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
                 out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.");
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
                out.println("<a href=\"/" +rev+ "/servlet/Member_select\" target=\"bot\"><img name=\"Mem_Mac_Tee_Times\" src=\"/" +rev+ "/images/Mem_Mac_Tee_Times.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"View the Tee Sheets\"></a>");
                out.println("<a href=\"/" +rev+ "/servlet/Member_teelist\" target=\"bot\"><img name=\"Mem_Mac_My_Calendar\" src=\"/" +rev+ "/images/Mem_Mac_My_Calendar.gif\" hspace=\"0\" border=\"0\" title=\"View Your Calendar To See Scheduled Tee Times, Events, Lessons\" alt=\"View Your Calendar\"></a>");
                out.println("<a href=\"/" +rev+ "/member_mac_lessons.htm\" target=\"bot\"><img name=\"Mem_Mac_Lessons\" src=\"/" +rev+ "/images/Mem_Mac_Lessons.gif\" hspace=\"0\" border=\"0\" title=\"Sign Up For Lessons or Access Your Scheduled Lessons\" alt=\"Lessons\"></a>");
                out.println("<a href=\"/" +rev+ "/servlet/Member_events\" target=\"bot\"><img name=\"Mem_Mac_Events\" src=\"/" +rev+ "/images/Mem_Mac_Events.gif\" hspace=\"0\" border=\"0\" title=\"Sing Up For Events or View Upcoming Events\" alt=\"Events\"></a>");
                out.println("<a href=\"/" +rev+ "/member_mac_search.htm\" target=\"bot\"><img name=\"Mem_Mac_Search\" src=\"/" +rev+ "/images/Mem_Mac_Search.gif\" hspace=\"0\" border=\"0\" title=\"Search For Other Members' Tee Times or Your Past Tee Times\" alt=\"Search\"></a>");
                if (!caller.equals( "MEMFIRST" ) && !caller.equals( "AMO" )) {  // if not called by other web site (MFirst)
                   out.println("<a href=\"/" +rev+ "/member_mac_email.htm\" target=\"bot\"><img name=\"Mem_Mac_Email\" src=\"/" +rev+ "/images/Mem_Mac_Email.gif\" hspace=\"0\" border=\"0\" title=\"Send Email To Other Members\" alt=\"Email\"></a>");
                }
                out.println("<a href=\"/" +rev+ "/servlet/Member_buddy\" target=\"bot\"><img name=\"Mem_Mac_Partners\" src=\"/" +rev+ "/images/Mem_Mac_Partners.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Partner List\" alt=\"\"></a>");
                out.println("<a href=\"/" +rev+ "/servlet/Member_services\" target=\"bot\"><img name=\"Mem_Mac_Settings\" src=\"/" +rev+ "/images/Mem_Mac_Settings.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Password and Other Settings\" alt=\"Settings\"></a>");
               out.println("</td>");
             out.println("</tr>");
            out.println("</table>");
             out.println("</body></html>");
          }

       } else {   // Cherry Hills Caddies

          //
          //  Build the main menu for Cherry Hills Caddies
          //
          out.println("<html><head>");

          out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
          out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
          out.println("<title> \"ForeTees Caddie Main Title Page\"</title>");
          out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
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
             if (zip != null && !zip.equals( "" )) {     // if zipcode provided
                out.println("<a href=\"http://www.accuweather.com/adcbin2/local_index?zipcode=" +zip+ "\" target=\"_blank\">");
             } else {
                out.println("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\">");
             }
             out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
            out.println("</td>");

            out.println("<td width=\"52%\" align=\"center\">");
               out.println("<font size=\"5\">Caddie Tee Time View</font><br>");

            //output the Home, Logout/Exit links
            out.println("<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Member_announce\" target=\"bot\" >Home</a>");
            if (caller.equals( "" ) || caller.equals( "none" ) || caller == null) {    // if from foretees login page
               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Logout</a>");
            } else {      // from web site (MFirst, CE, AMO, etc.)
               out.println("&nbsp;<label class=\"gblSep\">|</label>&nbsp;<a class=\"gblHref\" href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" >Exit</a>");
            }
            out.println("</td>");

             if (Safari == true) {
                out.println("<td width=\"24%\" align=\"center\" valign=\"top\" rowspan=\"2\">");
             } else {
                out.println("<td width=\"24%\" align=\"center\" rowspan=\"2\">");
             }
              out.println("<p>");
              out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
               out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
              out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
              out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
              out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.");
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
             out.println("<a href=\"/" +rev+ "/servlet/Member_select\" target=\"bot\"><img name=\"Mem_Mac_Tee_Times\" src=\"/" +rev+ "/images/Mem_Mac_Tee_Times.gif\" hspace=\"0\" border=\"0\" title=\"Make or Change Tee Times\" alt=\"View the Tee Sheets\"></a>");
    //         out.println("<a href=\"/" +rev+ "/servlet/Member_teelist\" target=\"bot\"><img name=\"Mem_Mac_My_Calendar\" src=\"/" +rev+ "/images/Mem_Mac_My_Calendar.gif\" hspace=\"0\" border=\"0\" title=\"View Your Calendar To See Scheduled Tee Times, Events, Lessons\" alt=\"View Your Calendar\"></a>");
    //         out.println("<a href=\"/" +rev+ "/member_mac_lessons.htm\" target=\"bot\"><img name=\"Mem_Mac_Lessons\" src=\"/" +rev+ "/images/Mem_Mac_Lessons.gif\" hspace=\"0\" border=\"0\" title=\"Sign Up For Lessons or Access Your Scheduled Lessons\" alt=\"Lessons\"></a>");
    //         out.println("<a href=\"/" +rev+ "/servlet/Member_events\" target=\"bot\"><img name=\"Mem_Mac_Events\" src=\"/" +rev+ "/images/Mem_Mac_Events.gif\" hspace=\"0\" border=\"0\" title=\"Sing Up For Events or View Upcoming Events\" alt=\"Events\"></a>");
    //         out.println("<a href=\"/" +rev+ "/member_mac_search.htm\" target=\"bot\"><img name=\"Mem_Mac_Search\" src=\"/" +rev+ "/images/Mem_Mac_Search.gif\" hspace=\"0\" border=\"0\" title=\"Search For Other Members' Tee Times or Your Past Tee Times\" alt=\"Search\"></a>");
    //         if (!caller.equals( "MEMFIRST" ) && !caller.equals( "AMO" )) {  // if not called by other web site (MFirst)
    //            out.println("<a href=\"/" +rev+ "/member_mac_email.htm\" target=\"bot\"><img name=\"Mem_Mac_Email\" src=\"/" +rev+ "/images/Mem_Mac_Email.gif\" hspace=\"0\" border=\"0\" title=\"Send Email To Other Members\" alt=\"Email\"></a>");
    //         }
    //         out.println("<a href=\"/" +rev+ "/servlet/Member_buddy\" target=\"bot\"><img name=\"Mem_Mac_Partners\" src=\"/" +rev+ "/images/Mem_Mac_Partners.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Partner List\" alt=\"\"></a>");
    //         out.println("<a href=\"/" +rev+ "/servlet/Member_services\" target=\"bot\"><img name=\"Mem_Mac_Settings\" src=\"/" +rev+ "/images/Mem_Mac_Settings.gif\" hspace=\"0\" border=\"0\" title=\"Access/Maintain Your Password and Other Settings\" alt=\"Settings\"></a>");
            out.println("</td>");
          out.println("</tr>");
         out.println("</table>");
          out.println("</body></html>");
       } // end if Cherry Hills Caddies
       
   } // end if IS_TLT
   
 }  // end of doGet

}
