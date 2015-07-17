/***************************************************************************************
 *   Member_announce:  This servlet will process the 'View Club Announcements' request
 *                     from the Member's main page.
 *
 *
 *   called by:  member_main.htm (doGet)
 *
 *   created: 10/01/2002   Bob P.
 *
 *   last updated:            ******* keep this accurate *******
 *
 *        3/13/14   Denver CC (denvercc) - Removed custom to bypass the golf announcement page (case 1333).
 *        2/28/14   Fixed issue with previous two AGC customs so that they properly apply to the golf-side announcement page bypass.
 *        2/21/14   Gettysvue CC (gettysvuecc) - The AGC standard of bypassing the announcement page will no longer apply to them.
 *        2/14/14   Canyon Oaks CC (canyonoakscc) - The AGC standard of bypassing the announcement page will no longer apply to them.
 *       12/04/13   Updated Utilities.isAGCClub calls to use the new Utilities.isClubInGroup method instead.
 *       10/21/13   Dove Canyon Club (dovecanyonclub) - Removed the custom to bypass the announcement page, as it's no longer desired.
 *        9/13/13   Idlewild CC (idlewildcc) - Bypass the announcement page and bring members directly to the Member_select page instead.
 *        7/23/13   Use CONNECT_ID in ProcessConstants.
 *        6/20/13   American Golf (all AGC clubs) - Updated announcement page bypass custom to forward differently based on the current activity.
 *        6/13/13   Philly Cricket Club - Recip Site (philcricketrecip) - Added to custom to bypass the announcement page.
 *        5/29/13   Desert Mountain (desertmountain) - Removed from custom to bypass announcement page. Members will now be brought to the announcement page when logging in.
 *        4/18/13   Dove Canyon Club (dovecanyonclub) - Separated dovecanyonclub from AGC club processing since they are no longer managed by AGC.
 *        4/10/13   Skokie CC (skokie) - Reactivated new-skin intro message for them only.
 *        3/27/13   Cape Cod National (capecodnational) - Bypass the announcement page and bring members directly to Member_select instead.
 *        1/28/13   Do not show the ForeTees logos (Common_skin.outputLogo) if user came from Flexscape - Connect Premier site.
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *       12/21/12   Desert Mountain (desertmountain) - Bypass the announcement page and bring members directly to Member_select instead.
 *       12/04/12   Add parm check that allows a club to bypass the test to skip the announcement page.
 *                  This allows the member to see the announcement page by clicking the Home link when their club opts
 *                  to bypass the announcement page when logging in.
 *       11/27/12   Remove New Skin notice.
 *       11/20/12   All American Golf (AGC) clubs - Added custom to bypass the announcement page.
 *       10/26/12   Do not output the FT logo for Connect home page.
 *       10/12/12   Do not display the breadcrumb on the announcement page (home page).
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        6/22/12   Monterey Peninsula CC (mpccpb) - Bypass the announcement page and bring members directly to Member_select instead.
 *        4/20/12   Add check for Notification System and do not send intro message if so (new skin).
 *        4/05/12   Add a member message to introduce the new skin to all members that have logged in prior to the new skin.
 *        2/06/12   Allow for a unique announcement page for new skin to help in the transition.
 *        1/22/12   New Skin - use Common_skin for bread crumb and log display.
 *       11/16/11   Added center alignment to div surrounding entire announcement page to force pages to center align, even if the announcement page is left aligned.
 *       11/11/11   Changes for new skin
 *       11/06/11   Change paths for announcement pages - now using NFS and discrete club folders
 *        3/20/11   Add @SuppressWarnings annotations to applicable methods
 *       10/28/10   TEMP - add iframe to pull in an event calendar - just testing to see how it would look!!!!
 *        6/09/10   Add a div tag with align=center to force all announcement pages to center alignment.
 *        2/24/10   By Pass Announcement Page for Palo Alto Hills - Case# 1792
 *        9/25/09   Add support for Activity announcement pages
 *       12/21/07   By Pass Announcement Page for Denver CC - Case# 1333
 *        4/14/06   Changed to read in the announcement page from disk then output
 *                  it to the user instead of loading it in an iframe and having
 *                  the client request the page.
 *       10/05/04   Changes for Version 5.
 *        1/16/04   Changes for Version 4.
 *        7/18/03   Enhancements for Version 3 of the software.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;


public class Member_announce extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;                 // Software Revision Level (Version)
 
 int CONNECT_ID = ProcessConstants.CONNECT_ID;      // Act id for FT Connect 


 //*****************************************************
 // Process the initial request from member_main.htm
 //*****************************************************
 //
 @SuppressWarnings("deprecation")
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

   PreparedStatement pstmt = null;
   
   HttpSession session = SystemUtils.verifyMem(req, out);

   if (session == null) return;

   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   int activity_id = (Integer)session.getAttribute("activity_id");


   
   if (req.getParameter("override") == null) {   // skip if Home link in bread crumb set to override the following test

       // By Pass Announcement Page for Denver CC & Palo Alto Hills - Case# 1333 & 1792
       // new skin simply redirects to member_select
       if (((club.equals( "paloaltohills" ) || club.equals("capecodnational") || club.equals("mpccpb") 
               || (!club.equals("canyonoakscc") && !club.equals("gettysvuecc") && Utilities.isClubInGroup(club, "AGC"))) && activity_id == 0) 
               || club.equals("philcricketrecip") || club.equals("idlewildcc")) {

           Member_select memSelect = new Member_select();

           memSelect.doGet(req, resp);
           return;
       } else if (!club.equals("canyonoakscc") && !club.equals("gettysvuecc") && Utilities.isClubInGroup(club, "AGC") && activity_id != 0 && activity_id != 9999) {
           
           Member_gensheets memGen = new Member_gensheets();
           
           memGen.doGet(req, resp);
           return;
       }
   }

   int tmp_tlt = (Integer)session.getAttribute("tlt");      // check for Notification system
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;

   // get a database connection
   Connection con = SystemUtils.getCon(session);

   String caller = (String)session.getAttribute("caller");

   boolean new_skin = ((String)session.getAttribute("new_skin")).equals("1");

   if (club.equals( "" )) {

      invalidUser(out);            // Error - reject
      return;
   }
   
   String temp = "";
   boolean ftConnect_act = false;
   
   if (req.getParameter("activity") != null) {   // if FT Connect site and user clicked the club name (see Common_skin.outputBanner)

       temp = req.getParameter("activity");

       if (temp.equals("connect")) {
               
           ftConnect_act = true;
       }
   }
   
   if (ftConnect_act == true) activity_id = CONNECT_ID;    // Display the Connect Home Page
   

   // TEMP - FOR TESTING NEW SKIN
   if (req.getParameter("test_new_skin") != null) {

       session.setAttribute("new_skin", "1");  // new skin flag
       new_skin = true;
   }
   
   
   //
   // PERFORM PREMIER REDIRECTS
   //
   if (caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {
     
        String landing_page = "";
            
        if (club.equals("tontoverde")) {

            if (activity_id == 0) {

                // golf landing page
                landing_page = "http://www.tontoverde.org/Golf/Golf-Home-269.html";

            } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                // dining landing page
                landing_page = "http://www.tontoverde.org/Dining-157.html";

            }

        } else if (club.equals("ccyork")) {

            if (activity_id == 0) {

                // golf landing page
                landing_page = "http://www.ccyork.org/Golf/Golf-Home-653.html";

            } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                // dining landing page
                landing_page = "http://www.ccyork.org/Dining-and-Events/Dining-and-Events-Home-604.html";

            }

        } else if (club.equals("demoroger")) {
            
            //boolean rwd = Utilities.getRequestBoolean(req,ProcessConstants.RQA_RWD,false);
            
            if (activity_id == 0) {

                // golf landing page
                landing_page = "http://foretees.flexdemo.com/Golf-1753.html";

            } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                // dining landing page
                landing_page = "http://foretees.flexdemo.com/Dining-and-Social-1362.html";

            }

        } else if (club.equals("missionviejo")) {
            
            if (activity_id == 0) {

                // golf landing page
                landing_page = "http://www.missionviejocc.com/Members/Golf/Golf-Home-87.html";

            } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                // dining landing page
                landing_page = "http://www.missionviejocc.com/Members/Social-and-Dining/Social-and-Dining-150.html";

            }

        } else if (club.equals("pinehurstcountryclub")) {
            
            if (activity_id == 0) {

                // golf landing page
                landing_page = "http://www.pinehurstcountryclub.com/Members-Only/Left-Nav/Golf-86.html";

            } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

                // dining landing page
                landing_page = "http://www.pinehurstcountryclub.com/Members-Only/Right-Nav/Dining-and-Social-117.html";

            } else if (activity_id == 1) {
                
                // tennie landing page
                landing_page = "http://www.pinehurstcountryclub.com/Members-Only/Left-Nav/Tennis-110.html";
            }

        }
        
        // IF WE FOUND A REDIRECT PAGE THEN GO THERE NOW AND TERMINATE
        if (!landing_page.equals("")) {
            
            // OUTPUT REDIRECT CODE
             out.println("<script type=\"text/javascript\">");
             out.println("<!-- ");
             out.println("top.window.location.href=\"" + landing_page + "\";");
             out.println("// -->");
             out.println("</script>");
             
             out.close();
             return;
        }
        
   } // END IF PREMIER CLUB
   
   

    if (new_skin) {

        //       NO LONGER NEEDED as of 11/27/12 BP - reactivated as custom for Skokie only
        if (club.equals("skokie") && activity_id != ProcessConstants.DINING_ACTIVITY_ID && IS_TLT == false) {    // no message if Dining or Notofication System

           //  Display an instructional page on the new skin if member has not seen it yet

            if (req.getParameter("new_skin_intro") == null) {    // if this is not a return from showing the intro message

                boolean intro_sent = sendIntro(club, user, activity_id, req, out, con);

                if (intro_sent) return;       // exit and wait for member to continue (will return here with new_skin_intro passed)

            } else {

                String new_skin_intro = req.getParameter("new_skin_intro");

                if (new_skin_intro.equals("stop")) {

                    //  Member does not want to see the msg again

                    try {

                        pstmt = con.prepareStatement (
                              "UPDATE member2b SET read_login_msg = 5 WHERE username = ?");

                        pstmt.clearParameters();            
                        pstmt.setString(1, user);        
                        pstmt.executeUpdate(); 

                    } catch (Exception exc) {

                    } finally {

                         try { pstmt.close(); }
                         catch (Exception ignore) {}
                    }              
                }
            }      // end of IF new_skin_intro
        }          // end of IF NOT Dining

        String clubName = "";
        try {

            clubName = Utilities.getClubName(con);        // get the full name of this club

        } catch (Exception exc) {}

        // start the html output
        Common_skin.outputHeader(club, activity_id, "Welcome to ForeTees", true, out, req);

        Common_skin.outputBody(club, activity_id, out, req);

        Common_skin.outputTopNav(req, club, activity_id, out, con);

        Common_skin.outputBanner(club, activity_id, clubName, (String)session.getAttribute("zipcode"), out, req);

        Common_skin.outputSubNav(club, activity_id, out, con, req);

        Common_skin.outputPageStart(club, activity_id, out, req);

        //Common_skin.outputBreadCrumb(club, activity_id, out, "");   // not needed !

        if (activity_id != CONNECT_ID && !caller.equals("FLEXWEBFT")) Common_skin.outputLogo(club, activity_id, out, req);

        out.println("<div class=\"preContentFix\"></div>"); // clear the float

        out.println("<div class=\"announcement_container\">");

    } else {

        // Old Skin

        out.println("<html><head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
        out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
        out.println("<title> \"ForeTees Member Announcement Page\"</title>");
     //   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");

        //out.println("<style type=\"text/css\"> body {width: 80%; margin-right: auto; margin-left: auto;} </style>");      // so body will align on center
        out.println("<style type=\"text/css\"> body {text-align:center} </style>");      // so body will align on center

        out.println("</head>");
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

        SystemUtils.getMemberSubMenu(req, out, caller);



        out.println("<div style=\"margin:0px auto;\" align=\"center\">");
        /*
        out.println("<iframe src=\"Common_calendar\" frameborder=\"1\" class=\"announce\" marginwidth=\"0\" marginheight=\"3\" scrolling=\"yes\" height=\"88\" width=\"770\" allowtransparency=\"true\">");
        out.println("</iframe>");
        out.println("</div>");


        out.println("<div style=\"align:center; margin:0px auto;\">");
        out.println("<BR>");            // space between calendar and announcement page
         */
    }


    //
    // Output announcement page for this activity
    //

    File f;
    FileReader fr = null;
    BufferedReader br = null;
    String path = req.getRealPath("") + "/announce/" + club + "/";
    String tmp = "";

    //boolean failed = false;
    //String tmp = "/announce/" +club+ "/" +club+ "_announce" + ((activity_id == 0) ? "" : "_" + activity_id) + ".htm";
    //
    //  If new skin - look for a new skin announcement page (not always there, only if we build it for them)
    // 
    /*
    if (new_skin) {

        tmp = "/announce/" +club+ "/" +club+ "_announce_ns" + ((activity_id == 0) ? "" : "_" + activity_id) + ".htm";
    }
    */

    String filename = "";

    if (new_skin) filename = club + "_announce_ns" + ((activity_id == 0) ? "" : (activity_id == ProcessConstants.DINING_ACTIVITY_ID) ? "_dining" : "_" + activity_id) + ".htm";

    try {

        // TEMP - look for a new skin announcement page (not always there, only if we build it for them)
        f = new File(path + filename);

        if (!f.isFile()) {

            // not found, now get real filename
            filename = Utilities.getAnnouncementPageFileName(activity_id, "", club, "", false);
            f = new File(path + filename);  // (path + Utilities.getAnnouncementPageFileName(activity_id, "", club, "", false));

        }

        if (f.isFile()) {

            fr = new FileReader(f);
            br = new BufferedReader(fr);

                while( (tmp = br.readLine()) != null )
                    out.println(tmp);

        } else {

            // debug
            out.println("<!-- NOT A FILE (" + path + filename + ") -->");

        }

    } catch (FileNotFoundException ignore) {

        out.println("<p>&nbsp;</p><p align=center><i>Missing Announcement Page.</i></p>");

    } catch (SecurityException ignore) {

        out.println("<p>&nbsp;</p><p align=center><i>Access Denied.</i></p>");

    } catch (Exception exc) {

        out.println("<!-- ERROR: " + exc.toString() + " -->");

    } finally {

        try {
            br.close();
        } catch (Exception ignore) {}
        try {
            fr.close();
        } catch (Exception ignore) {}

    }


    /*
    out.println("<iframe src=\"/" +rev+ "/announce/" +club+ "_announce.htm\" frameborder=\"0\" class=\"announce\" marginwidth=\"0\" marginheight=\"15\" scrolling=\"auto\" height=\"100%\" width=\"100%\" allowtransparency=\"true\">");
    out.println("<!-- Alternate content for non-supporting browsers -->");
    out.println("<H2>The browser you are using does not support frames</H2>");
    out.println("</iframe>");
    */

    if (new_skin) {

        out.println("<div class=\"clearfloat\"></div>");
        out.println("</div><!-- closing announcement_container -->");

        Common_skin.outputPageEnd(club, activity_id, out, req);

        //try { con.close(); }
        //catch (Exception ignore) {}

    } else {

        out.println("</div></BODY></HTML>");

    }
    
   
   
   out.close();
       
 }  // end of doGet

 
 // ***********************************************************************
 //   Display New Skin Intro Message if not already shown to this member
 // ***********************************************************************

 private boolean sendIntro(String club, String user, int activity_id, HttpServletRequest req, PrintWriter out, Connection con) {

   Connection con2 = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   int startdate = 0;
   int count = 0;
   int read_login_msg = 0;
   
   boolean intro_sent = false;
       
   
   if (club.equals("skokie")) {     // skip these clubs
       
       try {
          con2 = dbConn.Connect("v5");     // coonect to V5 db

          //
          //   Get the start date for this club (site created)
          //
          pstmt = con2.prepareStatement ("SELECT startdate FROM clubs WHERE clubname = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, club);
          rs = pstmt.executeQuery();      // execute the prepared stmt

          if (rs.next()) {

              startdate = rs.getInt(1);
          }

       } catch (Exception exc) {

       } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

            try { con2.close(); }
            catch (Exception ignore) {}
       }

       if (startdate < 20120101) {        // if club has been using the old skin

           //  check if member has already viewed this message

           try {

              pstmt = con.prepareStatement ("SELECT count, read_login_msg FROM member2b WHERE username = ?");

              pstmt.clearParameters();        // clear the parms
              pstmt.setString(1, user);
              rs = pstmt.executeQuery();      // execute the prepared stmt

              if (rs.next()) {

                  count = rs.getInt(1);
                  read_login_msg = rs.getInt(2);
              }

           } catch (Exception exc) {

           } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}
           }

           if (read_login_msg < 4 && count > 1) {   // if member has not viewed this msg 4 times yet and has logged in more than once

               //  output the intro msg

                Common_skin.outputHeader(club, activity_id, "Member Intro", true, out, req);     // output the page start

                out.println("<body>");
                out.println("<div id=\"wrapper_login\" align=\"left\">");
                out.println("<div id=\"title\"></div>");
                out.println("<div id=\"main_login\" align=\"left\">");
                out.println("<h1>Important Message from ForeTees</h1>");
                out.println("<div class=\"main_message\">");
                out.println("<h2>Please Read the Following Introduction</h2><br /><br />");
                out.println("<div class=\"sub_instructions\">");
                out.println("We would like to welcome you to the exciting new look of ForeTees! &nbsp;While we've made some significant improvements on the 'look and feel', " +
                            "we've gone to great lengths to make sure that very few changes were made to any key feature layouts, functionality, or navigation. " +
                            "&nbsp;We believe the end result delivers the same system you've become accustomed to with a far more modern and enhanced appearance.<br /><br />");
                out.println("Please take a moment to view the sample page below and read the corresponding numbered descriptions just under the image.");
                out.println("</div>");

               out.print("<br /><img src=\"/" + rev + "/assets/images/new_skin_intro.jpg\" alt=\"ForeTees Intro\" width=\"734\" height=\"311\" /><br /><br />");

                out.println("<div class=\"main_message\">");
                out.println("<strong>1. The Navigation Menu.</strong> &nbsp;As you can see, the navigation menu has been moved to the top of the page. The menu items have " +
                            "not changed and are in the same order as before, so they should be very familiar to you.<br /><br />");
                out.println("<strong>2. The Home Link.</strong> &nbsp;We have moved the Home link so that it is more accessible throughout the site. &nbsp;Clicking on this will " +
                            "always bring you back to the landing page.<br /><br />");
                out.println("<strong>3. Activity Tabs.</strong> &nbsp;If your club only uses ForeTees for one activity, such as Golf, then you will not see these tabs. " +
                            "&nbsp;However, if your club also uses our Court Reservation System (FlxRez) and/or ForeTees Dining, then these tabs will provide a quick and easy " +
                            "way for you to switch between the reservation systems.  &nbsp;Each system utilizes the same easy-to-use navigation as well as the same look " +
                            "and feel.<br /><br />");

                out.println("<strong>Why did we do this?</strong> &nbsp;The primary reason for the change was to better " +
                            "utilize newer technologies to improve the overall user experience. &nbsp;" +
                            "This new version of ForeTees adheres to the latest standards in website design " +
                            "which allowed us to better prepare for the future by " +
                            "incorporating technologies that are more open to mobile and other touch screen devices. &nbsp;" +
                            "Additionally, the modern appearance will make for a smoother transition when accessing ForeTees from your club's website." +
                            "<br /><br />");
                
                out.println("We hope you enjoy the new look. &nbsp;Thank you for using ForeTees!" +
                            "<br /><br />");
                
                out.println("</div>");
            
               out.println("<center><table align=\"center\"><tr><td align=\"center\">");
               out.println("<form method=\"get\" action=\"Member_announce\">");
               out.println("<input type=\"hidden\" name=\"new_skin_intro\" value=\"yes\">");
               out.println("<input type=\"submit\" value=\"Continue, Show This Again Later\" id=\"submit\" style=\"width:280px;\">");
               out.println("</form></td>");
               out.println("<td align=\"center\">");
               out.println("<form method=\"get\" action=\"Member_announce\">");
               out.println("<input type=\"hidden\" name=\"new_skin_intro\" value=\"stop\">");
               out.println("<input type=\"submit\" value=\"Continue, Do NOT Show This Again\" id=\"submit\" style=\"width:280px;\">");
               out.println("</form></td></tr></table>");
               
               

               out.println("</center></div></div>");
               Common_skin.outputPageEnd(club, activity_id, out, req);    // finish the page       

               out.close();       

               //  flag this member as having read this msg

               intro_sent = true;            // inform caller that we sent the msg
               
               read_login_msg++;             // increment number of times msg displayed
                             
           }     // end of IF read_login_msg
           
           if (intro_sent == true || count < 2) {     // should we update the intro_sent count for this member?
               
               if (count < 2) read_login_msg = 5;     // new user - set message as read enough so we don't display it later

               try {

                   pstmt = con.prepareStatement (
                         "UPDATE member2b SET read_login_msg = ? WHERE username = ?");

                   pstmt.clearParameters();            
                   pstmt.setInt(1, read_login_msg);        
                   pstmt.setString(2, user);        
                   pstmt.executeUpdate(); 

               } catch (Exception exc) {

               } finally {

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
               }
              
           }    // end of IF intro_sent or count < 2

       }     // end of IF startdate 
       
   }       // end of IF club already using new skin
 
   return(intro_sent);
 }
 

 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR>This site requires the use of Cookies for security purposes.  We use them to verify");
   out.println("<BR>your session and prevent unauthorized access.  Please check your 'Privacy' settings,");
   out.println("<BR>under 'Tools', 'Internet Options' (for MS Internet Explorer).  This must be set to");
   out.println("<BR>'Medium High' or lower.  Thank you.");
   out.println("<BR><BR>");
   out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

}
