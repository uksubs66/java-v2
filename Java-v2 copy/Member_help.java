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

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.getActivity;


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

    con = SystemUtils.getCon(session);            // get DB connection
    if (con == null) return;

   //
   //  get the club name
   //
   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");   // get caller's name
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   boolean new_skin = ((String)session.getAttribute("new_skin")).equals("1");
 
     

   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
   

   boolean failed = false;
   boolean dining = false;
   boolean flxrez = false;
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
    boolean allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages?

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
       
        out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / Help</div>");

        out.println("<div id=\"tt1_left\">");
        out.println("<p><strong>System Help</strong></p></div>");
        out.println("<center><BR><BR><H3>ForeTees Dining Assistance</H3>");

       
   } else if (new_skin) {
       
       //  Add whole new page (menus, etc.) - use Home for return button at bottom!!
       
      //
      //  Build the top of the page
      //
      Common_skin.outputHeader(club, sess_activity_id, "Member Help", true, out, req);
      Common_skin.outputBody(club, sess_activity_id, out, req);
      Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
      Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);  
      Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
      Common_skin.outputPageStart(club, sess_activity_id, out, req);
      Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Help", req);
      Common_skin.outputLogo(club, sess_activity_id, out, req);

      out.println("<div class=\"preContentFix\"></div>"); // clear the float
      out.println("<div class=sub_main_tan>");
      out.println("<center><BR><BR><H3>ForeTees Assistance</H3>");
      
   } else {

       out.println(SystemUtils.HeadTitle("Member Help Page"));
       out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
       out.println("<font size=\"2\" face=\"Arial, Times New Roman, Helvetica\">");
       out.println("<BR><BR><H3>ForeTees Assistance</H3>");
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
           
           out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\" target=\"_blank\"><i>Click Here For Help With Dining Reservations</i></a>");
           
       } else if (flxrez) {

           if (new_skin) {
              out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\" target=\"_blank\"><i>Click Here For Help With " + getActivity.getActivityName(sess_activity_id, con) + " Reservations</i></a>");
           } else {
              out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\">Click Here For Help With " + getActivity.getActivityName(sess_activity_id, con) + " Reservations</a>");
           }
           
       } else {

           if (new_skin) {
               out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\" target=\"_blank\"><i>Click Here For Help With Tee Times</i></a>");               
           } else {
               out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\">Click Here For Help With Tee Times</a>");
           }
       }
   }
       
   if (tlt == 0 && foretees_mode > 0 && allowMobile == true && dining == false) {   // if this is a golf tee time system

       if (new_skin) {       
           out.println("<BR><BR><a href=\"Common_mobile_help\" target=\"_blank\"><i>Click Here For Help With Mobile Access</i></a><BR><BR>");   
       } else {  
           out.println("<BR><BR><a href=\"Common_mobile_help\">Click Here For Help With Mobile Access</a>");   
       }
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
   
   if (dining) {
       
      out.println("</div><div id=\"tt2_left\" align=\"center\">" +
                 "<input type=\"button\" value=\"Exit\" id=\"back\" onclick=\"window.location.href='Dining_home'\">" +
                 "</div>");

      out.println("</center></div></div>");
       
   } else if (new_skin) {
       
      out.println("</div><div id=\"tt2_left\" align=\"center\">" +
                 "<input type=\"button\" value=\"Exit\" id=\"back\" onclick=\"window.location.href='Member_announce'\">" +
                 "</div>");

      out.println("</center></div></div>");
        
      Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page           
       
   } else {
       out.println("<input type=\"button\" value=\"OK\" onClick='self.close();'>");
       out.println("</font></CENTER></BODY></HTML>");
   }
   out.close();

 }

}
