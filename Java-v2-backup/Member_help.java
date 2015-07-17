/***************************************************************************************
 *   Member_help:  This servlet will process the 'Help' request from Member_maintop.
 *
 *
 *   called by:  Member_maintop
 *
 *   created: 4/25/2006   Bob P.
 *
 *   last updated:      ******* keep this accurate *******
 *
 *       5/21/14  Do not include the breadcrumb or home link if Premier user.
 *       5/08/14  Add 2 more videos - tee times and notifications for on a desktop.
 *       4/29/14  Add more videos.
 *       4/26/14  Add more videos.
 *       4/2/14   Do not show Lesson videos if lesson book not used for the activity.
 *       4/18/14  Add RWD Member Videos.
 *       2/06/14  AGC - Updated previous vimeo link to not require that a PDF file be present, and to apply to all non-lottery AGC clubs.
 *       1/31/14  AGC - Plantation CC (plantationcc) - Added vimeo link to a help video. Will add more AGC clubs to this at a later date.
 *       1/15/14  Removed the IE Reset info from the help pag,e since it's no longer a problem members are experiencing.
 *      12/03/13  Tonto Verde (tontoverde) - Removed custom to hide the link to mobile help.
 *      10/31/13  Tonto Verde (tontoverde) - Added custom to hide the link to mobile help.
 *       1/17/13  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *       9/06/12  Updated outputTopNav calls to also pass the HttpServletRequest object.
 *       6/19/12  Add instructions on how to reset IE for new skin problem.
 *       4/20/12  A link to the dining instruction pdf will now be displayed if the file exists (filname = clubname + "_dining.pdf").
 *       4/02/12  Added FlxRez specific member instruction messages.
 *       2/21/12  Allow for Dining
 *       1/19/12  New skin changes.
 *      10/28/09  Allow for Activity instructions.
 *       2/29/08  Add check for member instructions pdf file.  If there add link for it.
 *      10/03/07  Troon CC - Custom message w/ golfshop phone number (Case #1280)
 *
 *
 ***************************************************************************************
 */

import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.getActivity;
import com.foretees.common.Connect;
import com.foretees.common.mobileAPI;
import com.foretees.common.memberUtil;

public class Member_help extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 // Process the initial call from Member_maintop
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   Connection con = null;                 // init DB objects

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

    con = Connect.getCon(req);            // get DB connection
    if (con == null) return;

   //
   //  get the club name
   //
   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");   // get caller's name
   String msubtype = (String)session.getAttribute("msubtype");   // get caller's member sub-type
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   boolean new_skin = ((String)session.getAttribute("new_skin")).equals("1");
 
     

   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
   

   boolean failed = false;
   boolean dining = false;
   boolean flxrez = false;
   boolean lessons = false;
   int tlt = 0;
   int foretees_mode = 0;
   
   if (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {    // check for Dining
      
      dining = true;
      
   } else if (sess_activity_id > 0) {
       
      flxrez = true;
   }


   File f;
   FileReader fr;
   BufferedReader br;
   String tmp = "";
   String path = "";
   String fname = club;
   
   
    //
    //  Check if Mobile is allowed (for messages)
    //
    boolean allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages? - no longer used (now use RWD)

    //  Check club/member RWD settings
    boolean allowRwd = Utilities.isResponsiveAllowed(con);
    
    if (dining == false) {
       
       lessons = Utilities.isLessonBookConfigured(sess_activity_id, con);
    }
            
    //
    //  Get TLT and ForeTees indicators
    //
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT no_reservations, foretees_mode FROM club5");
        if (rs.next()) {
           
           tlt = rs.getInt(1);
           foretees_mode = rs.getInt("foretees_mode");    // golf indicator
        }

    } catch (Exception exc) {

         Utilities.logError("Member_help - Error getting club settings - Club= " +club+ ", ERR: " + exc.toString());
         
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


   
   
   //
   //  Determine the name of the file - use club name for golf, club name plus activity id for others
   //
   if (sess_activity_id > 0) {
      
      fname = club + sess_activity_id;
   }
   
   if (dining) {
       
      fname = club + "_dining";
   }


   //
   //  Output the help page
   //
   if (dining) {
       
       //
       //   Build the Dining Home page
       //
       Common_skin.outputHeader(club, sess_activity_id, "Member Help", true, out, req);

       Common_skin.outputBody(club, sess_activity_id, out, req);

       Common_skin.outputTopNav(req, club, sess_activity_id, out, con);

       Common_skin.outputBanner(club, sess_activity_id, clubName, "", out, req);    // no zip code for Dining

       Common_skin.outputSubNav(club, sess_activity_id, out, con, req);

       Common_skin.outputPageStart(club, sess_activity_id, out, req);
       
       if (!caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {   // do not include breacrumb button if Premier
     
           out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / Help</div>");
       }

       out.println("<div id=\"tt1_left\">");
       out.println("<p><strong>System Help</strong></p></div>");
       out.println("<center><BR><BR><H1>ForeTees Assistance</H1>");
   
   } else {
       
      //
      //  Build the top of the page
      //
      Common_skin.outputHeader(club, sess_activity_id, "Member Help", true, out, req);
      Common_skin.outputBody(club, sess_activity_id, out, req);
      Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
      Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);  
      Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
      Common_skin.outputPageStart(club, sess_activity_id, out, req);
      if (!caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {   // do not include breacrumb button if Premier  
          Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Help", req);
      }
      Common_skin.outputLogo(club, sess_activity_id, out, req);

      out.println("<div class=\"preContentFix\"></div>"); // clear the float
      out.println("<div class=sub_main_tan>");
      out.println("<center><BR><H1>ForeTees Assistance</H1>");
   }
   
   
   //
   //  Check if call is for ClubCentral help
   //
   if (req.getParameter("ClubCentral") != null) {         // if help for App

       out.println("<div class=\"main_instructions pageHelp\" data-ftHelpTitle=\"Instructions\">Welcome to the ForeTees Mobile App (ClubCentral) Help Page.<br><br>"
               + "The ClubCentral App is easy to use and a convenient way to communicate with your club as well as access reservations and events.<BR>"
               + "The App is available for all Apple (IOS) and Android Mobile Devices through the App Stores."
               + "</div>");
   
       
       out.println("<BR>Select the appropriate video below for information on how to download and login to the app.");
       
       out.println("<BR><BR><strong>NOTICE: &nbsp;You can also view these videos by selecting the Help tab in the top navigation.</strong>");
       
      out.println("<BR><BR><HR width=\"75%\">");
       
      out.println("<BR><BR><strong>VIDEO FOR APPLE (IOS) DEVICES</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/121139472\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      
      out.println("<BR><BR><HR width=\"75%\">");
       
      // Help Video for Android Devices
      out.println("<BR><BR><strong>VIDEO FOR ANDROID DEVICES</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/121277001\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      
      out.println("<BR><BR><HR width=\"75%\">");
       
       
       out.println("<BR><BR><BR><div align=\"center\">");
       out.println("<form method=\"get\" action=\"Member_services#jumpToApp\" autocomplete=\"off\">");
       out.println("<input type=\"submit\" value=\"Get Username and Password for App\">");
       out.println("</form></div>");
       
       out.println("<BR><BR>Thank you for using ForeTees!<BR><BR>" +
                   "Please contact your club staff if you have any questions or problems.<br><br>");
       out.println("</div>");

       if (!caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {   // do not include Home button if Premier

            out.println("<div id=\"tt2_left\" align=\"center\">");
            out.println("<form method=\"get\" action=\"Member_announce\" autocomplete=\"off\">");
            out.println("<input type=\"submit\" value=\"Home\" id=\"submit\">");
            out.println("</form></div>");
       }
       out.println("</center></div></div>");
       Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page        
       out.close(); 
       return;
   }   
   
   
   //
   //   Check if ClubCentral App help videos should be included
   //
   boolean clubCentral = mobileAPI.isMobileAppEnabledForClub(club, 1, con);         // ClubCentral app used by this club?
   boolean clubCentralStaging = mobileAPI.isMobileAppStagingForClub(club, 1, con);  // ClubCentral app in staging mode for this club?
   boolean showCCHelp = false;

   if (clubCentralStaging) {    // club in Staging mode for app

        if (msubtype.equals("App Tester")) {

            showCCHelp = true;     // ok to show help
        }

   } else if (clubCentral) {    // if club uses app and its open to all members

        showCCHelp = true;     // ok to show help
   }
   
   
   //
   // check for member instrcutions pdf file
   //
   try {
   
      // path = req.getRealPath("");                       // deprecated - use getServletContext
      path = getServletContext().getRealPath("");          //   get the path to our servlets
      tmp = "/instructions/" + fname + ".pdf";             //   /rev/instructions/club.pdf (is it there?)
      f = new File(path + tmp);
      fr = new FileReader(f);
      
   } catch (Exception e2) {
      
      failed = true;              // file not found
   }
   
   if (Utilities.isClubInGroup(club, "AGC") && Utilities.isLotteryClub(con) == 0 && sess_activity_id == 0) {
       
       out.println("<BR><BR><a href=\"http://vimeo.com/user12200989/review/85572854/5bb70ccd70\" target=\"_blank\"><i>Click Here For Help With Tee Times</i></a>");
       
   } else if (failed == false) {         // if pdf exists
       
       if (dining == true) {
           
           //out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\" target=\"_blank\"><i>Click Here For Help With Dining Reservations</i></a>");
           
       } else if (flxrez) {

           out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\" target=\"_blank\"><i>Click Here For Help With " + getActivity.getActivityName(sess_activity_id, con) + " Reservations</i></a>");
           
       } else {

           out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\" target=\"_blank\"><i>Click Here For Help With Tee Times</i></a>");               
       }
   }
       
   if (tlt == 0 && foretees_mode > 0 && allowMobile == true && dining == false) {   // if this is a golf tee time system
       /*
       out.println("<BR><BR><a href=\"Common_mobile_help\" target=\"_blank\"><i>Click Here For Help With Mobile Access</i></a><BR><BR>");   
       *     // do not show old mobile help any longer
       */
   }
   
   
   
   //
   //   Include Videos Here
   //
   out.println("<BR><BR><HR width=\"75%\"></HR><BR><BR><H3>ForeTees Instructional Videos</H3>");
   
   out.println("<BR><strong>Note:</strong> &nbsp;The following videos demonstrate the ForeTees system on a mobile device.<br>While the layout varies on other devices, "
             + "such as a desktop computer, the process is very much the same.");
   out.println("<BR><BR><HR width=\"75%\"></HR>");
   
   //
   //  Add a menu of videos so user can easily locate and select
   //
   out.println("<BR><BR><H3>Select a Video to View</H3><BR>");
   
   if (tlt == 0) {   // if this is NOT a Notification system
      
      out.println("<A href=\"#intro\">Introduction to ForeTees Mobile</A><BR><BR>");
      
   } else {
      
      out.println("<A href=\"#introNotify\">Introduction to ForeTees Mobile</A><BR><BR>");
   }
   
   if (showCCHelp) {
       
        out.println("<A href=\"#clubcentralios\">How to Download and Login to the ClubCentral App (Apple Devices)</A><BR><BR>");   // ClubCentral App Videos

        out.println("<A href=\"#clubcentralandroid\">How to Download and Login to the ClubCentral App (Android Devices)</A><BR><BR>");    
   }

   if (sess_activity_id == 0 && tlt == 0) {    // if user on Golf 
      
      out.println("<A href=\"#partner\">How to Manage Your Partner Lists</A><BR><BR>");   
      
      out.println("<A href=\"#teetimedt\">How to Book a Tee Time (desktop version)</A><BR><BR>");      
      
      out.println("<A href=\"#teetime\">How to Book a Tee Time (mobile version)</A><BR><BR>");      
      
      out.println("<A href=\"#edittee\">How to Update a Tee Time</A><BR><BR>");      
      
      if (Utilities.isLotteryClub(con) > 0) {    // if club uses the lottery feature

         out.println("<A href=\"#lottery\">How to Request a Tee Time</A><BR><BR>");    
      }
  
      out.println("<A href=\"#event\">How to Register for an Event</A><BR><BR>");      
      
      if (lessons == true) {
         
         out.println("<A href=\"#lesson\">How to Book a Lesson</A><BR><BR>");                        
      }
   
      out.println("<A href=\"#settings\">How to Manage Your Personal Settings/Profile</A><BR><BR>");      
      
      
   } else if (sess_activity_id == 0 && tlt > 0) {      // if Notification System and user is on Golf
            
      out.println("<A href=\"#partnernotify\">How to Manage Your Partner Lists</A><BR><BR>");   
      
      out.println("<A href=\"#notificationdt\">How to Notify the Golf Shop That You Plan to Play (desktop version)</A><BR><BR>");      
      
      out.println("<A href=\"#notification\">How to Notify the Golf Shop That You Plan to Play (mobile version)</A><BR><BR>");      
      
      out.println("<A href=\"#editnotify\">How to Update a Notification</A><BR><BR>");   
      
      out.println("<A href=\"#event\">How to Register for an Event</A><BR><BR>");      
      
      if (lessons == true) {
         
         out.println("<A href=\"#lesson\">How to Book a Lesson</A><BR><BR>");                        
      }
   
      out.println("<A href=\"#settings\">How to Manage Your Personal Settings/Profile</A><BR><BR>");      
      
      
   } else if (dining == true) {         // if user is on Dining
      
      out.println("<A href=\"#partner\">How to Manage Your Partner Lists</A><BR><BR>");   
      
      out.println("<A href=\"#diningres\">How to Make a Dining Reservation</A><BR><BR>");      
      
      out.println("<A href=\"#diningedit\">How to Change or Cancel a Dining Reservation</A><BR><BR>");      
      
      out.println("<A href=\"#diningevent\">How to Register for a Dining Event</A><BR><BR>");            
      
      out.println("<A href=\"#settings\">How to Manage Your Personal Settings/Profile</A><BR><BR>");      
      
      
   } else if (flxrez == true) {         // if user is on FlxRez
      
      out.println("<A href=\"#partner\">How to Manage Your Partner Lists</A><BR><BR>");   
      
      out.println("<A href=\"#flexres\">How to Make a Reservation</A><BR><BR>");      

      out.println("<A href=\"#editflexres\">How to Update a Reservation</A><BR><BR>");   
      
      out.println("<A href=\"#flexevent\">How to Register for an Event</A><BR><BR>");            
      
      if (lessons == true) {
         
         out.println("<A href=\"#lesson\">How to Book a Lesson</A><BR><BR>");                        
      }
   
      out.println("<A href=\"#settings\">How to Manage Your Personal Settings/Profile</A><BR><BR>");      
   }
   
         
   out.println("<HR width=\"75%\"></HR>");
             
   
   //************************************************
   //  Add the videos
   //************************************************
   //
   if (tlt == 0) {   // if this is NOT a Notification system

      // Intro to RWD
      out.println("<a name=\"intro\">&nbsp;</a><BR><BR><strong>Introduction to ForeTees Mobile</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92281843?title=0&amp;byline=0&amp;portrait=0\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
   } else {
      
      // Intro to RWD for Notification System
      out.println("<a name=\"introNotify\">&nbsp;</a><BR><BR><strong>Introduction to ForeTees Mobile</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/93278601?title=0&amp;byline=0&amp;portrait=0\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
   }
   
   //
   //  ClubCentral App Videos
   //
   if (showCCHelp) {
            
      out.println("<a name=\"clubcentralios\">&nbsp;</a><BR><BR><strong>How to Download and Login to the ClubCentral App (Apple Devices)</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/121139472\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

        out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
        out.println("<BR><BR><HR width=\"75%\"></HR>");
           
      out.println("<a name=\"clubcentralandroid\">&nbsp;</a><BR><BR><strong>How to Download and Login to the ClubCentral App (Android Devices)</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/121277001\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

        out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
        out.println("<BR><BR><HR width=\"75%\"></HR>");   
   }
   
    
      
   if (sess_activity_id == 0 && tlt == 0) {    // if user on Golf 
      
      // Partner List
      out.println("<a name=\"partner\">&nbsp;</a><BR><BR><strong>How to Manage Your Partner Lists</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92350948\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");      
      
      // Make a Tee Time (desktop)
      out.println("<a name=\"teetimedt\">&nbsp;</a><BR><BR><strong>How to Book a Tee Time (desktop version)</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/94529792\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      // Make a Tee Time (mobile)
      out.println("<a name=\"teetime\">&nbsp;</a><BR><BR><strong>How to Book a Tee Time (mobile version)</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92354563\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      // Edit a Tee Time
      out.println("<a name=\"edittee\">&nbsp;</a><BR><BR><strong>How to Update a Tee Time</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/93281635\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      
      if (Utilities.isLotteryClub(con) > 0) {    // if club uses the lottery feature

         // Lottery
         out.println("<a name=\"lottery\">&nbsp;</a><BR><BR><strong>How to Request a Tee Time</strong><br><br>"
               + "<iframe src=\"//player.vimeo.com/video/92863305\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

         out.println("<BR>To view in full screen mode, click on the expansion image "
                  + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
         out.println("<BR><BR><HR width=\"75%\"></HR>");
      }
  
      // Register for an Event
      out.println("<a name=\"event\">&nbsp;</a><BR><BR><strong>How to Register for an Event</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92355044\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
   
      
      if (lessons == true) {
         
         // Book a Lesson
         out.println("<a name=\"lesson\">&nbsp;</a><BR><BR><strong>How to Book a Lesson</strong><br><br>"
               + "<iframe src=\"//player.vimeo.com/video/92355191\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

         out.println("<BR>To view in full screen mode, click on the expansion image "
                  + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
         out.println("<BR><BR><HR width=\"75%\"></HR>");
      }
   
      // Settings
      out.println("<a name=\"settings\">&nbsp;</a><BR><BR><strong>How to Manage Your Personal Settings/Profile</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92354798\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
   
      
   } else if (sess_activity_id == 0 && tlt > 0) {      // if Notification System and user is on Golf
                
      // Partner List for Notify
      out.println("<a name=\"partnernotify\">&nbsp;</a><BR><BR><strong>How to Manage Your Partner Lists</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/93278368\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");      
      
      // Notification (desktop)
      out.println("<a name=\"notificationdt\">&nbsp;</a><BR><BR><strong>How to Notify the Golf Shop That You Plan to Play (desktop version)</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/94534991\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      // Notification (mobile)
      out.println("<a name=\"notification\">&nbsp;</a><BR><BR><strong>How to Notify the Golf Shop That You Plan to Play (mobile version)</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92863940\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      // Edit a Notification
      out.println("<a name=\"editnotify\">&nbsp;</a><BR><BR><strong>How to Update a Notification</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/93299915\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      // Register for an Event
      out.println("<a name=\"event\">&nbsp;</a><BR><BR><strong>How to Register for an Event</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92355044\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
   
      if (lessons == true) {
         
         // Book a Lesson
         out.println("<a name=\"lesson\">&nbsp;</a><BR><BR><strong>How to Book a Lesson</strong><br><br>"
               + "<iframe src=\"//player.vimeo.com/video/92355191\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

         out.println("<BR>To view in full screen mode, click on the expansion image "
                  + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
         out.println("<BR><BR><HR width=\"75%\"></HR>");
      }
   
      // Settings
      out.println("<a name=\"settings\">&nbsp;</a><BR><BR><strong>How to Manage Your Personal Settings/Profile</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92354798\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
   
      
   } else if (dining == true) {         // if user is on Dining
      
      // Partner List
      out.println("<a name=\"partner\">&nbsp;</a><BR><BR><strong>How to Manage Your Partner Lists</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92350948\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");      
      
      // Make a Dining Reservation
      out.println("<a name=\"diningres\">&nbsp;</a><BR><BR><strong>How to Make a Dining Reservation</strong><br><br>"
            + "<iframe src=\"//player.vimeo.com/video/92364182\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      // Change/Cancel a Dining Reservation
      out.println("<a name=\"diningedit\">&nbsp;</a><BR><BR><strong>How to Change or Cancel a Dining Reservation</strong><br><br>"
            + "<iframe src=\"//player.vimeo.com/video/92752846\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      //  Dining Event
      out.println("<a name=\"diningevent\">&nbsp;</a><BR><BR><strong>How to Register for a Dining Event</strong><br><br>"
            + "<iframe src=\"//player.vimeo.com/video/92883874\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
      
      // Settings
      out.println("<a name=\"settings\">&nbsp;</a><BR><BR><strong>How to Manage Your Personal Settings/Profile</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92354798\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");
         
      
   } else if (flxrez == true) {         // if user is on FlxRez
      
      // Partner List
      out.println("<a name=\"partner\">&nbsp;</a><BR><BR><strong>How to Manage Your Partner Lists</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92350948\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");      
      
      // Court Reservation
      out.println("<a name=\"flexres\">&nbsp;</a><BR><BR><strong>How to Make a Reservation</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92875961\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");      
      
      // Edit a Court Reservation
      out.println("<a name=\"editflexres\">&nbsp;</a><BR><BR><strong>How to Update a Reservation</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/93283894\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");      
      
      // FlxRez Event
      out.println("<a name=\"flexevent\">&nbsp;</a><BR><BR><strong>How to Register for an Event</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92877844\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
      
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");      

      if (lessons == true) {
         
         // Book a Lesson
         out.println("<a name=\"lesson\">&nbsp;</a><BR><BR><strong>How to Book a Lesson</strong><br><br>"
               + "<iframe src=\"//player.vimeo.com/video/92355191\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  

         out.println("<BR>To view in full screen mode, click on the expansion image "
                  + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
         out.println("<BR><BR><HR width=\"75%\"></HR>");
      }
   
      // Settings
      out.println("<a name=\"settings\">&nbsp;</a><BR><BR><strong>How to Manage Your Personal Settings/Profile</strong><br><br>"
              + "<iframe src=\"//player.vimeo.com/video/92354798\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>");  
   
      out.println("<BR>To view in full screen mode, click on the expansion image "
                + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner of the video.");
      out.println("<BR><BR><HR width=\"75%\"></HR>");   
   }
   
      
    
   //  Add instructions to reset IE for new skin problem with "Unable to load page" error
    /*
   out.println("<BR><BR><strong>***** 'TIME OUT' or 'UNABLE TO LOAD PAGE' Error *****</strong>");   
   out.println("<p align=left>");
   out.println("<BR>If you are receiving one of these errors and you are using the Internet Explorer browser, please do the following:");   
   out.println("<BR><BR>&nbsp;&nbsp;&nbsp;1. Close all Internet Explorer and Windows Explorer windows that are currently open.");
   out.println("<BR><BR>&nbsp;&nbsp;&nbsp;2. Open Internet Explorer by clicking the Start button. In the search box, type Internet Explorer, and then click Internet Explorer.");
   out.println("<BR><BR>&nbsp;&nbsp;&nbsp;3. Click the Tools button, and then click Internet options.");
   out.println("<BR><BR>&nbsp;&nbsp;&nbsp;4. Click the Advanced tab, and then click Reset.");
   out.println("<BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; DO NOT check the Delete Personal Settings box, since checking this box will delete all favorites and stored passwords.");
   out.println("<BR><BR>&nbsp;&nbsp;&nbsp;5. In the Reset Internet Explorer Settings dialog box, click Reset.");
   out.println("<BR><BR>&nbsp;&nbsp;&nbsp;6. When Internet Explorer finishes applying default settings, click Close, and then click OK.");
   out.println("<BR><BR>&nbsp;&nbsp;&nbsp;7. Close Internet Explorer.");
   out.println("<BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Your changes will take effect the next time you open Internet Explorer.");
   out.println("<BR><BR><a href=\"http://windows.microsoft.com/en-US/windows7/Reset-Internet-Explorer-settings-in-Internet-Explorer-9\" target=\"_blank\">Click Here</a> to get Microsoft's full set of instructions.");   
   out.println("<BR><BR>NOTE: &nbsp;To print these instructions, right click on this page and select Print from the options provided.</p>");   
    
    */
    
   
   if (club.equals( "trooncc" )) {
       
      out.println("<BR><BR>Please contact the golfshop at 480-585-0540, or send an email to <a href=\"mailto:golfshop@trooncc.com\">golfshop@trooncc.com</a>.");
      
   } else {
       
      out.println("<BR><BR>Please contact your club staff for further assistance with ForeTees.");
   }
   
   if (club.equals( "foresthighlands" )) {

      out.println("<BR><BR>Canyon Golf Shop:  928.525.9000 or 888.470.4607");
      out.println("<BR><BR>Meadow Golf Shop:  928.525.5250 or 866.470.4608");
   }

   out.println("<BR><BR>Thank you!<BR><BR>");
   
   
   out.println("</div>");

   if (!caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {   // do not include Home button if Premier
     
      out.println("<div id=\"tt2_left\" align=\"center\">");
      out.println("<form method=\"get\" action=\"Member_announce\" autocomplete=\"off\">");
      out.println("<input type=\"submit\" value=\"Home\" id=\"submit\">");
      out.println("</form></div>");
   }
   out.println("</center></div></div>");
        
   Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page        
      
   out.close();

 }

}
