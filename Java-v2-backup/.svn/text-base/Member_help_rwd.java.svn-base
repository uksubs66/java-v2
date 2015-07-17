/***************************************************************************************
 *   Member_help_rwd:  This servlet will process the 'Help' request when in RWD mode.
 *
 *
 *   called by:  Common_skin - Help menu item
 *
 *   created: 4/23/2014   Bob P.
 *
 *   last updated:      ******* keep this accurate *******
 *
 *       5/21/14  Do not include the breadcrumb or home link if Premier user.
 *       4/29/14  Add more videos.
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
import com.foretees.common.uaUtil;
import com.foretees.common.mobileAPI;

public class Member_help_rwd extends HttpServlet {


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
   
   // Check if the device is probably mobile
   boolean isMobileClient = uaUtil.isMobile(req);

   // check for Dining
   if (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {    
      
      dining = true;
      
   } else if (sess_activity_id > 0) {
       
      flxrez = true;
   }


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

         Utilities.logError("Member_help_rwd - Error getting club settings - Club= " +club+ ", ERR: " + exc.toString());
         
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
        out.println("<center><BR><BR><H1>ForeTees Dining Assistance</H1>");
   
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
      out.println("<center><BR><BR><H1>ForeTees Assistance</H1>");
   }
    
   
   out.println("<div class=\"main_instructions\"><strong>Please select a help topic:</strong></div>");
   out.println("<div class=\"rwdCompact\">");
    
      
   out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - Intro to ForeTees Mobile\">");    // add Intro video
   
   if (tlt == 0) {   // if this is NOT a Notification system (video mentions tee times) - show for all others
   
      out.println("<p><iframe src=\"//player.vimeo.com/video/92281843?title=0&amp;byline=0&amp;portrait=0\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");

   } else {
      
      out.println("<p><iframe src=\"//player.vimeo.com/video/93278601?title=0&amp;byline=0&amp;portrait=0\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
   }
   if (isMobileClient == false) {   // if not a mobile device
      out.println("<p>To view in full screen mode, click on the expansion image "
                  + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
   }
   out.println("</div>");
   
   
   if (showCCHelp) {   // show ClubCentral App Videos ?
       
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Download and Activate the ClubCentral App (Apple Devices)\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/121139472\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Download and Activate the ClubCentral App (Android Devices)\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/121277001\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
   }
   
   
    
   if (sess_activity_id == 0 && tlt == 0) {    // if user on Golf and NOT Notification

      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Manage Your Partner Lists\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92350948\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      if (Utilities.isClubInGroup(club, "AGC") && Utilities.isLotteryClub(con) == 0) {

         out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"How Do I Book a Tee Time?\">");
         //out.println("<p><a href=\"http://vimeo.com/user12200989/review/85572854/5bb70ccd70\" target=\"_blank\"><i>Click Here For Help With Tee Times</i></a></p>");
         out.println("<p><iframe src=\"//player.vimeo.com/video/85572854\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
         if (isMobileClient == false) {   // if not a mobile device
            out.println("<p>To view in full screen mode, click on the expansion image "
                        + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
         }
         out.println("</div>");
         
      } else {
         
         out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How To Book a Tee Time\">");
         out.println("<p><iframe src=\"//player.vimeo.com/video/92354563\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
         if (isMobileClient == false) {   // if not a mobile device
            out.println("<p>To view in full screen mode, click on the expansion image "
                        + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
         }
         out.println("</div>");
      }
      
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How To Update a Tee Time\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/93281635\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");


      if (Utilities.isLotteryClub(con) > 0) {    // if club uses the lottery feature

         out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How To Request a Tee Time\">");
         out.println("<p><iframe src=\"//player.vimeo.com/video/92863305\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
         if (isMobileClient == false) {   // if not a mobile device
            out.println("<p>To view in full screen mode, click on the expansion image "
                        + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
         }
         out.println("</div>");
      }
      
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Manage Your Personal Settings/Profile\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92354798\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Register for an Event\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92355044\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      if (lessons == true) {     // if club uses the lesson book
         
         out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Book a Lesson\">");
         out.println("<p><iframe src=\"//player.vimeo.com/video/92355191\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
         if (isMobileClient == false) {   // if not a mobile device
            out.println("<p>To view in full screen mode, click on the expansion image "
                        + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
         }
         out.println("</div>");
      }
    
      
   } else if (sess_activity_id == 0 && tlt > 0) {      // if Notification System and user is on Golf
      
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Manage Your Partner Lists\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/93278368\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How To Notify The Golf Shop That You Plan To Play\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92863940\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How To Update a Notification\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/93299915\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Register for an Event\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92879228\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Manage Your Personal Settings/Profile\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92354798\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      if (lessons == true) {     // if club uses the lesson book
         
         out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Book a Lesson\">");
         out.println("<p><iframe src=\"//player.vimeo.com/video/92355191\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
         if (isMobileClient == false) {   // if not a mobile device
            out.println("<p>To view in full screen mode, click on the expansion image "
                        + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
         }
         out.println("</div>");
      }
    
      
   } else if (dining == true) {         // if user is on Dining
      
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Manage Your Partner Lists\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92350948\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Make a Dining Reservation\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92364182\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Change or Cancel a Dining Reservation\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92752846\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Register for a Dining Event\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92883874\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Manage Your Personal Settings/Profile\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92354798\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      
   } else if (flxrez == true) {         // if user is on FlxRez
      
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Manage Your Partner Lists\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92350948\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Make a Court Reservation\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92875961\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Update a Court Reservation\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/93283894\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Register for an Event\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92877844\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
    
      if (lessons == true) {     // if club uses the lesson book
         
         out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Book a Lesson\">");
         out.println("<p><iframe src=\"//player.vimeo.com/video/92355191\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
         if (isMobileClient == false) {   // if not a mobile device
            out.println("<p>To view in full screen mode, click on the expansion image "
                        + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
         }
         out.println("</div>");
      }
    
      out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Video - How to Manage Your Personal Settings/Profile\">");
      out.println("<p><iframe src=\"//player.vimeo.com/video/92354798\" width=\"250\" height=\"140\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></p>");
      if (isMobileClient == false) {   // if not a mobile device
         out.println("<p>To view in full screen mode, click on the expansion image "
                     + " <img src=\"/" +rev+ "/images/expand-video.png\"> in the lower right corner.</p>");
      }
      out.println("</div>");
   } 
      
    
    out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Why does everything look different?\">");
        out.println("<p>You are currently using ForeTees' new \"Mobile\" interface.  You may have selected it when logging in to ForeTees, " +
            "or your club may have chosen to use this new interface as a default for your device.</p>");
        out.println("<p>While this interface is called \"Mobile\", it is designed to operate on a wide variety of modern devices, " +
            "including tablets, phones, laptop and desktop computers.</p>");
        out.println("<p>If your club has chosen to keep the previous 'Desktop' view enabled, you should find a link for " +
            "'Switch to Desktop View' at the bottom of every page.  Clicking that link should return you the previous interface.</p>");
    out.println("</div>");
    
    out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"Navigation seems slow\">");
        out.println("<p>Performance of ForeTees should be acceptable if you are using a tablet or phone built in the last 2 years, " +
            "or a personal computer built in the last 5 years.</p>");
        out.println("<p>If you are using a device fitting the above criteria, yet ForeTees is unacceptably slow, " +
            "please review the following:</p>");
        out.println("<p>Slow network performance can give the appearance of a slow device or application.  Please check that " +
            "your mobile or wireless network is working well.</p>");
        out.println("<p>Viruses or malware can substantially impact the performance of your device.  Please make sure your system " +
            "has been cleared of all malicious software. Please note that tablets and mobile phones are susceptible " +
            "to viruses, just like personal computers.</p>");
    out.println("</div>");
    
    out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"What are 'TIME OUT' or 'UNABLE TO LOAD PAGE' errors?\">");
        out.println("<p>These errors are usually the result of a configuration issue in Microsoft Internet Explorer.</p>");
        out.println("<p>If you are using the Internet Explorer browser and are seeing these errors while using ForeTees, please try the following:</p>");
        out.println("<ol>");
            out.println("<li>Close all Internet Explorer and Windows Explorer windows that are currently open.</li>");

            out.println("<li>Open Internet Explorer by clicking the Start button. In the search box, type Internet Explorer, " +
                "and then click Internet Explorer.</li> ");

            out.println("<li>Click the Tools button, and then click Internet options.</li>");

            out.println("<li>Click the Advanced tab, and then click Reset.<br>" +
                "<b>DO NOT check the Delete Personal Settings box</b>, since checking this box will delete all favorites " +
                "and stored passwords.</li>");

            out.println("<li>In the Reset Internet Explorer Settings dialog box, click Reset.</li>");

            out.println("<li>When Internet Explorer finishes applying default settings, click Close, and then click OK.</li>");

            out.println("<li>Close Internet Explorer.<br>" +
                "Your changes will take effect the next time you open Internet Explorer.</li>");
        out.println("</ol>");
        out.println("<p><a href=\"http://windows.microsoft.com/en-US/windows7/Reset-Internet-Explorer-settings-in-Internet-Explorer-9\" target=\"_blank\">Click Here</a>" +
            "to get Microsoft's full set of instructions.</p>"); 
    out.println("</div>");
    
    out.println("<div class=\"sub_instructions pageHelp\" data-fthelptitle=\"I'm still having trouble.\">");
        out.println("<p>Please contact your club for further assistance with ForeTees.</p>");
        out.println("<p>Thank you!</p>");
    out.println("</div></div>");
    
   if (club.equals( "trooncc" )) {
       
      out.println("<BR><BR>For further assistance, please contact the golfshop at 480-585-0540, or send an email to <a href=\"mailto:golfshop@trooncc.com\">golfshop@trooncc.com</a>.");
      
   } else {
       
      out.println("<BR><BR>Please contact your club staff for further assistance with ForeTees.");
   }
   
   if (club.equals( "foresthighlands" )) {

      out.println("<BR><BR>Canyon Golf Shop:  928.525.9000 or 888.470.4607");
      out.println("<BR><BR>Meadow Golf Shop:  928.525.5250 or 866.470.4608");
   }

   out.println("<BR><BR>Thank you!<BR><BR>");
   
   out.println("</div>");

   /*
   out.println("<div id=\"tt2_left\" align=\"center\">");
   out.println("<form method=\"get\" action=\"Member_announce\" autocomplete=\"off\">");
   out.println("<input type=\"submit\" value=\"Home\" id=\"submit\">");
   out.println("</form></div>");
   *                                // don't use this for mobile
   */
   out.println("</center></div></div>");
        
   Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page        
      
   out.close();

 }

}
