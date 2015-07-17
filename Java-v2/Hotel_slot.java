/***************************************************************************************
 *   Hotel_slot:  This servlet will process the 'Reserve Tee Time' request from
 *                    the Hotel's Sheet page.
 *
 *
 *   called by:  Hotel_sheet (doPost)
 *               self on cancel request
 *               Hotel_search
 *
 *
 *   created: 11/19/2003   Bob P.
 *
 *   last updated:             ******* keep this accurate *******
 *
 *       3/11/13   Cape Cod National (capecodnational) - Fixed issue where custom was preventing hotel users from canceling more than 7 days in advance, when it should have been for less than 7 days (case 2168).
 *      11/27/12   Tweak iframe resize code
 *       9/17/12   Cape Cod National (capecodnational) - Updated custom so hotel users can cancel times up until 7 days in advance (case 2168).
 *       7/12/12   Cape Cod National (capecodnational) - Added custom to prevent hotel users from canceling tee times (case 2168).
 *       9/21/10   Added guest tracking processing
 *       5/19/10   Cape Cod National - call to verifyCustom in verify so we check guest quotas (case 1828).
 *       4/19/10   Changes for unlimited guest types
 *       5/06/09   TPC - allow hotel users to view notes even if hide is on.  Also, enable the hide option if hotel user
 *                       adds notes.
 *       4/24/09   Add tee time history entry for hotel users (case 1632).
 *      12/09/08   Added guest restriction suspension handling
 *      12/01/08   Bay Hill - display member notices (case 1563).
 *       8/19/08   Remove 'synchronized' block of code to check in_use - set in_use first to check.
 *      10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *       9/20/04   V5 - change getClub from SystemUtils to common.
 *       2/09/04   Add separate 9-hole option.
 *       2/06/04   Add support for configurable transportation modes.
 *      01/07/04   JAG  Modified to match new color scheme
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Hotel_slot extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the request from Hotel_select
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   HttpSession session = SystemUtils.verifyHotel(req, out);    // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Get this session's username (to be saved in teecurr)
   //
   String user = (String)session.getAttribute("user");
   String name = (String)session.getAttribute("name");               // get users full name
   String club = (String)session.getAttribute("club");               // get club name

   //
   // Process request according to which 'submit' button was selected
   //
   //      'time:fb' - a request from Hotel_sheet
   //      'cancel'  - a cancel request from user via Hotel_slot (return with no changes)
   //      'submit'  - a reservation request (from self)
   //      'remove'  - a 'cancel reservation' request (from self - Cancel Tee Time)
   //      'return'  - a return from verify
   //      'print-conf'  - print a confirmation for hotel user
   //
   if (req.getParameter("cancel") != null) {

      cancel(req, out, con);                      // process cancel request
      return;
   }

   if (req.getParameter("print-conf") != null) {

      printConf(req, out, con);                      // process cancel request
      return;
   }

   if ((req.getParameter("submit") != null) || (req.getParameter("remove") != null)) {

      verify(req, out, con, session, resp);                 // process reservation requests request

      return;

   }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);

   //
   // Get the Multiple Course Option
   //
   try {
      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception ignore) {
   }

   String jump = "0";                     // jump index - default to zero (for _sheet)

   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }

   //
   //   Submit = 'time:fb'
   //
   int count = 0;
   int in_use = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;
   long temp = 0;
   long date = 0;
   int fb = 0;
   //int visits = 0;
   int x = 0;
   int xCount = 0;
   int i = 0;
   int i2 = 0;
   int hide = 0;
   //int nowc = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String pcw = "";

   String pname = "";

   String sdate = "";
   String stime = "";
   String ampm = "";
   String sfb = "";
   String conf = "";
   String notes = "";
   String hides = "";
   String msg = "";
   String rcourse = "";
   String rest_fb = "";
   String grest_recurr = "";

   //String [] rguest = new String [parm.MAX_Guests];    // array to hold the Guest Restriction guest names
   ArrayList<String> rguest = new ArrayList<String>();
   ArrayList<String> hguest = new ArrayList<String>();
   ArrayList<Integer> use_guestdb = new ArrayList<Integer>();
   
   boolean use_guestdb_values = Utilities.isGuestTrackingConfigured(0, con);

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   // Get all the parameters entered
   //
   String day_name = req.getParameter("day");       //  name of the day
   String index = req.getParameter("index");        //  index value of day (needed by Hotel_sheet when returning)
   String p5 = req.getParameter("p5");              //  5-somes supported
   String course = req.getParameter("course");      //  Name of Course

   if (req.getParameter("return") != null || (req.getParameter("memNotice") != null)) {  // if this is a return from verify - time = hhmm

      sdate = req.getParameter("sdate");
      stime = req.getParameter("stime");
      sfb = req.getParameter("fb");

   } else {

      sdate = req.getParameter("date");              //  date of tee time requested (yyyymmdd)

      //
      //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
      //
      Enumeration enum1 = req.getParameterNames();     // get the parm name passed

      while (enum1.hasMoreElements()) {

         pname = (String) enum1.nextElement();             // get parm name

         if (pname.startsWith( "time" )) {

            stime = req.getParameter(pname);              //  value = time of tee time requested (hh:mm AM/PM)

            StringTokenizer tok = new StringTokenizer( pname, ":" );     // separate name around the colon

            sfb = tok.nextToken();                        // skip past 'time '
            sfb = tok.nextToken();                        // get the front/back indicator value
         }
      }
   }

   //
   //  Convert the values from string to int
   //
   try {
      date = Long.parseLong(sdate);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      fb = Integer.parseInt(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  isolate yy, mm, dd
   //
   yy = date / 10000;
   temp = yy * 10000;
   mm = date - temp;
   temp = mm / 100;
   temp = temp * 100;
   dd = mm - temp;
   mm = mm / 100;

   if (req.getParameter("return") != null || (req.getParameter("memNotice") != null)) {     // if this is a return from verify - time = hhmm

      player1 = req.getParameter("player1");     // get the player info from the parms passed
      player2 = req.getParameter("player2");
      player3 = req.getParameter("player3");
      player4 = req.getParameter("player4");
      player5 = req.getParameter("player5");
      p1cw = req.getParameter("p1cw");
      p2cw = req.getParameter("p2cw");
      p3cw = req.getParameter("p3cw");
      p4cw = req.getParameter("p4cw");
      p5cw = req.getParameter("p5cw");
      notes = req.getParameter("notes");
      hides = req.getParameter("hide");
      conf = req.getParameter("conf");
      guest_id1 = (req.getParameter("guest_id1") != null) ? Integer.parseInt(req.getParameter("guest_id1")) : 0;
      guest_id2 = (req.getParameter("guest_id2") != null) ? Integer.parseInt(req.getParameter("guest_id2")) : 0;
      guest_id3 = (req.getParameter("guest_id3") != null) ? Integer.parseInt(req.getParameter("guest_id3")) : 0;
      guest_id4 = (req.getParameter("guest_id4") != null) ? Integer.parseInt(req.getParameter("guest_id4")) : 0;
      guest_id5 = (req.getParameter("guest_id5") != null) ? Integer.parseInt(req.getParameter("guest_id5")) : 0;

      String p9s = "";

      if (req.getParameter("p91") != null) {
         p9s = req.getParameter("p91");
         p91 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p92") != null) {
         p9s = req.getParameter("p92");
         p92 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p93") != null) {
         p9s = req.getParameter("p93");
         p93 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p94") != null) {
         p9s = req.getParameter("p94");
         p94 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p95") != null) {
         p9s = req.getParameter("p95");
         p95 = Integer.parseInt(p9s);
      }

      //
      //  Convert hide from string to int
      //
      hide = 0;                       // init to No
      if (!hides.equals( "0" )) {     // if not zero
         hide = 1;
      }

      try {
         time = Integer.parseInt(stime);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      //  create a time string for display
      //
      hr = time / 100;
      min = time - (hr * 100);

      ampm = " AM";

      if (hr > 11) {

         ampm = " PM";

         if (hr > 12) {

            hr = hr - 12;
         }
      }
      if (min < 10) {
         stime = hr + ":0" + min + ampm;
      } else {
         stime = hr + ":" + min + ampm;
      }

   } else {

      //
      //  Parse the time parm to separate hh, mm, am/pm and convert to military time
      //  (received as 'hh:mm xx'   where xx = am or pm)
      //
      StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token

      String shr = tok.nextToken();
      String smin = tok.nextToken();
      ampm = tok.nextToken();

      //
      //  Convert the values from string to int
      //
      try {
         hr = Integer.parseInt(shr);
      }
      catch (NumberFormatException e) {
         // ignore error
      }
      try {
         min = Integer.parseInt(smin);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      if (ampm.equalsIgnoreCase ( "PM" )) {

         if (hr != 12) {                    // 12xx will be PM, 00xx will be midnight

            hr = hr + 12;
         }
      }

      time = hr * 100;
      time = time + min;          // military time

      //
      //  Get the players' names and check if this tee slot is already in use
      //
      //  Set the tee time in use to see if it is already in use
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = 1, in_use_by = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND in_use = 0");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, user);         // put the parm in pstmt1
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, time);
         pstmt1.setInt(4, fb);
         pstmt1.setString(5, course);

         count = pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

      }
      catch (Exception e2) {

         dbError(out, e2, "Set tee time busy. ");
         return;
      }


      if (count == 0) {              // if time slot already in use

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<CENTER><BR><BR><H1>Tee Time Slot Busy</H1>");
         out.println("<BR><BR>Sorry, but this tee time slot is currently busy.<BR>");
         out.println("<BR>Please select another time or try again later.");
         out.println("<BR><BR>");
         if (index.equals( "999" )) {       // if from Hotel_search (doGet)

            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchmy.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");

         } else {                           // from tee sheet

            if (index.equals( "888" )) {       // if from Hotel_search (doPost)

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchm.htm\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
               out.println("</form></font>");

            } else {                           // from tee sheet

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_selmain.htm\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
               out.println("</form></font>");
            }
         }
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      try {

         PreparedStatement pstmt = con.prepareStatement (
            "SELECT player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, in_use, " +
            "player5, p5cw, notes, hideNotes, conf, p91, p92, p93, p94, p95, " +
            "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 " +
            "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ? ");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt
         pstmt.setInt(2, time);
         pstmt.setInt(3, fb);
         pstmt.setString(4, course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            player1 = rs.getString(1);
            player2 = rs.getString(2);
            player3 = rs.getString(3);
            player4 = rs.getString(4);
            p1cw = rs.getString(5);
            p2cw = rs.getString(6);
            p3cw = rs.getString(7);
            p4cw = rs.getString(8);
            in_use = rs.getInt(9);
            player5 = rs.getString(10);
            p5cw = rs.getString(11);
            notes = rs.getString(12);
            hide = rs.getInt(13);
            conf = rs.getString(14);
            p91 = rs.getInt(15);
            p92 = rs.getInt(16);
            p93 = rs.getInt(17);
            p94 = rs.getInt(18);
            p95 = rs.getInt(19);
            guest_id1 = rs.getInt("guest_id1");
            guest_id2 = rs.getInt("guest_id2");
            guest_id3 = rs.getInt("guest_id3");
            guest_id4 = rs.getInt("guest_id4");
            guest_id5 = rs.getInt("guest_id5");
         }

         pstmt.close();

      }
      catch (Exception e1) {

         dbError(out, e1, "Time in use check. ");
         return;
      }
      
      
      //
      //**********************************************
      //   Check for Member Notice from Pro
      //**********************************************
      //
      String memNotice = verifySlot.checkMemNotice(date, time, fb, course, day_name, "teetime", false, con);
      
      if (!memNotice.equals( "" )) {      // if message to display

         //
         //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
         //
         out.println("<HTML><HEAD>");
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
         out.println("<title>Member Notice For Tee Time Request</Title>");
         out.println("</HEAD>");

         out.println("<BODY><CENTER>");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
            out.println("<tr><td valign=\"top\" align=\"center\">");
               out.println("<p>&nbsp;&nbsp;</p>");
               out.println("<p>&nbsp;&nbsp;</p>");
               out.println("<font size=\"3\">");
               out.println("<b>NOTICE FROM YOUR GOLF SHOP</b><br><br><br></font>");

            out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
               out.println("<tr>");
               out.println("<td width=\"580\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<br>" + memNotice);
               out.println("</font></td></tr>");
               out.println("</table><br>");

               out.println("</font><font size=\"2\">");
               out.println("<br>Would you like to continue with this request?<br>");
               out.println("<br><b>Please select from the following. DO NOT use you browser's BACK button!</b><br><br>");

               out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
               out.println("<tr><td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<form action=\"Hotel_slot\" method=\"post\" name=\"can\">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
               out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
               out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\">");

               out.println("</form></font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
                  out.println("<form action=\"Hotel_slot\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
                  out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
                  out.println("<input type=\"submit\" value=\"YES - Continue\">");
                  out.println("</form></font></td></tr>");
               out.println("</table>");

               out.println("</td>");
               out.println("</tr>");
            out.println("</table>");
         out.println("</font></center></body></html>");
         out.close();
         return;
      }

   }

   //
   //  Get the walk/cart options available
   //
   try {

      getParms.getCourse(con, parmc, course);
   }
   catch (Exception e1) {

      msg = "Get wc options. ";

      dbError(out, e1, msg);
      return;
   }


   //
   //  Build the HTML page to prompt user for names
   //
   out.println("<HTML><HEAD><Title>Hotel Tee Slot Page</Title>");

   //  Add script code to allow modal windows to be used
   out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->" +
           "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>" +
           "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>" +
           "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function resizeIFrame(divHeight, iframeName) {");
   out.println("document.getElementById(iframeName).height = divHeight;");
   out.println("}");
   out.println("// -->");
   out.println("</script>");

   //
   //*********************************************************************************
   //  Erase player name (erase button selected next to player's name)
   //
   //    Remove the player's name and shift any other names up starting at player1
   //*********************************************************************************
   //
   out.println("<script type=\"text/javascript\">");            // Erase name script
   out.println("<!--");

   out.println("function erasename(pos1) {");

   out.println("document.playerform[pos1].value = '';");           // clear the player field

   out.println("var pos2 = pos1.replace('player', 'guest_id');");
   out.println("document.playerform[pos2].value = '0';");

   out.println("var f = document.forms['playerform'];");

   out.println("var player1 = f.player1.value;");  // get values after erase
   out.println("var player2 = f.player2.value;");  //   and shift them all up
   out.println("var player3 = f.player3.value;");
   out.println("var player4 = f.player4.value;");
   out.println("var cw1 = f.p1cw.value;");
   out.println("var cw2 = f.p2cw.value;");
   out.println("var cw3 = f.p3cw.value;");
   out.println("var cw4 = f.p4cw.value;");
   out.println("var guest_id1 = f.guest_id1.value;");
   out.println("var guest_id2 = f.guest_id2.value;");
   out.println("var guest_id3 = f.guest_id3.value;");
   out.println("var guest_id4 = f.guest_id4.value;");
   if (p5.equals( "Yes" )) {
      out.println("var player5 = f.player5.value;");
      out.println("var cw5 = f.p5cw.value;");
      out.println("var guest_id5 = f.guest_id5.value;");
   }

   out.println("if (player1 == '') {");                    // if player1 is empty
      out.println("if (player2 != '') {");                    // if player2 is not empty
         out.println("f.player1.value = player2;");
         out.println("f.p1cw.value = cw2;");
         out.println("f.guest_id1.value = guest_id2;");
         out.println("f.player2.value = '';");
         out.println("f.guest_id2.value = '0';");
      out.println("} else {");

      out.println("if (player3 != '') {");                    // else if player3 is not empty
         out.println("f.player1.value = player3;");
         out.println("f.p1cw.value = cw3;");
         out.println("f.guest_id1.value = guest_id3;");
         out.println("f.player3.value = '';");
         out.println("f.guest_id3.value = '0';");
      out.println("} else {");

      out.println("if (player4 != '') {");                    // else if player4 is not empty
         out.println("f.player1.value = player4;");
         out.println("f.p1cw.value = cw4;");
         out.println("f.guest_id1.value = guest_id4;");
         out.println("f.player4.value = '';");
         out.println("f.guest_id4.value = '0';");

   if (p5.equals( "Yes" )) {
      out.println("} else {");
      out.println("if (player5 != '') {");                    // else if player5 is not empty
         out.println("f.player1.value = player5;");
         out.println("f.p1cw.value = cw5;");
         out.println("f.guest_id1.value = guest_id5;");
         out.println("f.player5.value = '';");
         out.println("f.guest_id5.value = '0';");
      out.println("}");
   }
      out.println("}");
      out.println("}");
      out.println("}");
   out.println("}");

   out.println("player2 = f.player2.value;");    // get the new values
   out.println("player3 = f.player3.value;");
   out.println("player4 = f.player4.value;");
   out.println("cw2 = f.p2cw.value;");
   out.println("cw3 = f.p3cw.value;");
   out.println("cw4 = f.p4cw.value;");
   out.println("guest_id2 = f.guest_id2.value;");
   out.println("guest_id3 = f.guest_id3.value;");
   out.println("guest_id4 = f.guest_id4.value;");
   if (p5.equals( "Yes" )) {
      out.println("player5 = f.player5.value;");
      out.println("cw5 = f.p5cw.value;");
      out.println("guest_id5 = f.guest_id5.value;");
   }

   out.println("if (player2 == '') {");                    // if player2 is empty
      out.println("if (player3 != '') {");                    // if player3 is not empty
         out.println("f.player2.value = player3;");
         out.println("f.p2cw.value = cw3;");
         out.println("f.guest_id2.value = guest_id3;");
         out.println("f.player3.value = '';");
         out.println("f.guest_id3.value = '0';");
      out.println("} else {");

      out.println("if (player4 != '') {");                    // else if player4 is not empty
         out.println("f.player2.value = player4;");
         out.println("f.p2cw.value = cw4;");
         out.println("f.guest_id2.value = guest_id4;");
         out.println("f.player4.value = '';");
         out.println("f.guest_id4.value = '0';");

   if (p5.equals( "Yes" )) {
      out.println("} else {");
      out.println("if (player5 != '') {");                    // else if player5 is not empty
         out.println("f.player2.value = player5;");
         out.println("f.p2cw.value = cw5;");
         out.println("f.guest_id2.value = guest_id5;");
         out.println("f.player5.value = '';");
         out.println("f.guest_id5.value = '0';");
      out.println("}");
   }
      out.println("}");
      out.println("}");
   out.println("}");

   out.println("player3 = f.player3.value;");    // get the new values
   out.println("player4 = f.player4.value;");
   out.println("cw3 = f.p3cw.value;");
   out.println("cw4 = f.p4cw.value;");
   out.println("guest_id3 = f.guest_id3.value;");
   out.println("guest_id4 = f.guest_id4.value;");

   if (p5.equals( "Yes" )) {
      out.println("player5 = f.player5.value;");
      out.println("cw5 = f.p5cw.value;");
      out.println("guest_id5 = f.guest_id5.value;");
   }

   out.println("if (player3 == '') {");                    // if player3 is empty
      out.println("if (player4 != '') {");                    // if player4 is not empty
         out.println("f.player3.value = player4;");
         out.println("f.p3cw.value = cw4;");
         out.println("f.guest_id3.value = guest_id4;");
         out.println("f.player4.value = '';");
         out.println("f.guest_id4.value = '0';");

   if (p5.equals( "Yes" )) {
      out.println("} else {");
      out.println("if (player5 != '') {");                    // else if player5 is not empty
         out.println("f.player3.value = player5;");
         out.println("f.p3cw.value = cw5;");
         out.println("f.guest_id3.value = guest_id5;");
         out.println("f.player5.value = '';");
         out.println("f.guest_id5.value = '0';");
      out.println("}");
   }
      out.println("}");
   out.println("}");

   if (p5.equals( "Yes" )) {
      out.println("player4 = f.player4.value;");
      out.println("player5 = f.player5.value;");
      out.println("cw4 = f.p4cw.value;");
      out.println("cw5 = f.p5cw.value;");
      out.println("guest_id4 = f.guest_id4.value;");
      out.println("guest_id5 = f.guest_id5.value;");

      out.println("if (player4 == '') {");                    // if player4 is empty
         out.println("if (player5 != '') {");                    // if player5 is not empty
            out.println("f.player4.value = player5;");
            out.println("f.p4cw.value = cw5;");
            out.println("f.guest_id4.value = guest_id5;");
            out.println("f.player5.value = '';");
            out.println("f.guest_id5.value = '0';");
         out.println("}");
      out.println("}");
   }

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   //
   //*******************************************************************
   //  Erase text area - (Notes)      erasetext and movenotes
   //*******************************************************************
   //
   out.println("<script type=\"text/javascript\">");            // Erase text area script
   out.println("<!--");
   out.println("function erasetext(pos1) {");
   out.println("document.playerform[pos1].value = '';");           // clear the text field
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   out.println("<script type=\"text/javascript\">");             // Move Notes into textarea
   out.println("<!--");
   out.println("function movenotes() {");
   out.println("var oldnotes = document.playerform.oldnotes.value;");
   out.println("document.playerform.notes.value = oldnotes;");   // put notes in text area
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   //
   //*******************************************************************
   //  Move a Guest Name or 'X' into the tee slot
   //*******************************************************************
   //
   out.println("<script type=\"text/javascript\">");            // Move Guest Name script
   out.println("<!--");

   out.println("var guestid_slot;");
   out.println("var player_slot;");

   out.println("function moveguest(namewc) {");

   out.println("var f = document.forms['playerform'];");
   out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
   out.println("var name = array[0];");
   out.println("var use_guestdb = array[1];");

   out.println("var player1 = f.player1.value;");
   out.println("var player2 = f.player2.value;");
   out.println("var player3 = f.player3.value;");
   out.println("var player4 = f.player4.value;");

   if (p5.equals( "Yes" )) {
      out.println("var player5 = f.player5.value;");
   }

   // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
   out.println("if (use_guestdb == 1 && (player1 == '' || player2 == '' || player3 == '' || player4 == ''" + (p5.equals("Yes") ? " || player5 == ''" : "") + ")) {");
   out.println("  loadmodal(0);");
   out.println("}");

   //  set spc to ' ' if name to move isn't an 'X'
   out.println("var spc = '';");
   out.println("if (name != 'X' && name != 'x') {");
   out.println("   spc = ' ';");
   out.println("}");
   
      out.println("if (player1 == '') {");                    // if player1 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player1;");
            out.println("guestid_slot = f.guest_id1;");
            out.println("f.player1.value = name + spc;");
         out.println("} else {");
            out.println("f.player1.value = name + spc;");
         out.println("}");
      out.println("} else {");

      out.println("if (player2 == '') {");                    // if player2 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player2;");
            out.println("guestid_slot = f.guest_id2;");
            out.println("f.player2.value = name + spc;");
         out.println("} else {");
            out.println("f.player2.value = name + spc;");
         out.println("}");
      out.println("} else {");

      out.println("if (player3 == '') {");                    // if player3 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player3;");
            out.println("guestid_slot = f.guest_id3;");
            out.println("f.player3.value = name + spc;");
         out.println("} else {");
            out.println("f.player3.value = name + spc;");
         out.println("}");
      out.println("} else {");

      out.println("if (player4 == '') {");                    // if player4 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player4;");
            out.println("guestid_slot = f.guest_id4;");
            out.println("f.player4.value = name + spc;");
         out.println("} else {");
            out.println("f.player4.value = name + spc;");
         out.println("}");

   if (p5.equals( "Yes" )) {
      out.println("} else {");
      out.println("if (player5 == '') {");                    // if player5 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player5;");
            out.println("guestid_slot = f.guest_id5;");
            out.println("f.player5.value = name + spc;");
         out.println("} else {");
            out.println("f.player5.value = name + spc;");
         out.println("}");
      out.println("}");
    }

      out.println("}");
      out.println("}");
      out.println("}");
      out.println("}");

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");                               // End of script

   //*******************************************************************

   out.println("</HEAD>");
   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<table border=\"0\" width=\"100%\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"336633\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
     out.println("</td>");

     out.println("<td align=\"center\">");
     out.println("<font color=\"ffffff\" size=\"5\">Hotel Guest Reservation</font>");
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> 2002 All rights reserved.");
     out.println("</font><font size=\"3\">");
      out.println("<br><br><a href=\"/" +rev+ "/hotel_help.htm\" target=\"_blank\"><b>Help</b></a>");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table width=\"100%\" border=\"0\" align=\"center\">");          // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this reservation.");
            out.println("&nbsp; If you want to return without completing a reservation, <b>do not ");
            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
            out.println("option below.");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<font size=\"2\">");
      out.println("<br><br>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
        out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Tee Time:&nbsp;&nbsp;<b>" + stime + "</b>");
        if (!course.equals( "" )) {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
        }
        out.println("<br></font>");

      out.println("<table border=\"0\" cellpadding=\"5\" cellspacing=\"5\" align=\"center\">"); // table to contain 2 tables below

         out.println("<tr>");
         out.println("<td align=\"center\">");         // col for Instructions and Go Back button

            out.println("<font size=\"1\" color=\"#000000\">");
            out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/hotel_help_slot_instruct.htm', 'newwindow', config='Height=370, width=600, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=\"0\">");
            out.println("<br>Click for Help</a>");

            out.println("</font><font size=\"2\">");
            out.println("<br><br><br>");
            out.println("<form action=\"Hotel_slot\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
            out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
            out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("Return<br>w/o Changes:<br>");
            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
         out.println("</font></td>");

         out.println("<form action=\"Hotel_slot\" method=\"post\" name=\"playerform\" id=\"playerform\">");

         out.println("<td align=\"center\" valign=\"top\">");

            out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"370\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"2\">");
               out.println("<b>Add or Remove Players</b><br>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\">");
               out.println("<font size=\"2\">");

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes</b><br>");

               // Print hidden guest_id inputs
               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

               out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player1')\" style=\"cursor:hand\">");
               out.println("1:&nbsp;<input type=\"text\" id=\"player1\" name=\"player1\" value=\""+player1+"\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\" id=\"p1cw\">");
                 if (!p1cw.equals( "" )) {
                    out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p1cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p91 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p91\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p91\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player2')\" style=\"cursor:hand\">");
               out.println("2:&nbsp;<input type=\"text\" id=\"player2\" name=\"player2\" value=\""+player2+"\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\" id=\"p2cw\">");
                 if (!p2cw.equals( "" )) {
                    out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p2cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p92 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p92\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p92\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player3')\" style=\"cursor:hand\">");
               out.println("3:&nbsp;<input type=\"text\" id=\"player3\" name=\"player3\" value=\""+player3+"\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\" id=\"p3cw\">");
                 if (!p3cw.equals( "" )) {
                    out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p3cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p93 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p93\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p93\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player4')\" style=\"cursor:hand\">");
               out.println("4:&nbsp;<input type=\"text\" id=\"player4\" name=\"player4\" value=\""+player4+"\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\" id=\"p4cw\">");
                 if (!p4cw.equals( "" )) {
                    out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p4cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p94 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p94\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p94\" value=\"1\">");
                 }

            if (p5.equals( "Yes" )) {

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player5')\" style=\"cursor:hand\">");
               out.println("5:&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\""+player5+"\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");
                 if (!p5cw.equals( "" )) {
                    out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p5cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p95 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p95\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p95\" value=\"1\">");
                 }
            } else {

               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
            }

               out.println("<p align=\"left\">&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('conf')\" style=\"cursor:hand\">");
               out.println("Confirmation # or Id (optional):&nbsp;<input type=\"text\" id=\"conf\" name=\"conf\" value=\""+conf+"\" size=\"15\" maxlength=\"15\">");
               out.println("</p>");

               //
               //   Notes
               //
               //   Script will put any existing notes in the textarea (value= doesn't work)
               //
               out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

               if (hide != 0 && !club.startsWith("tpc")) {      // if proshop wants to hide the notes, do not display the text box or notes

                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">"); // pass existing notes

               } else {

                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
                  out.println("Notes to Pro:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"22\" rows=\"2\">");
                  out.println("</textarea><br>");
               }
               out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=" + sdate + ">");
               out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
               out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=" + mm + ">");
               out.println("<input type=\"hidden\" name=\"yy\" value=" + yy + ">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"no\">");
               out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=" + p5 + ">");
               out.println("<input type=\"hidden\" name=\"hide\" value=" + hide + ">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");

               out.println("<font size=\"1\">");
               for (i=0; i<16; i++) {
                  if (!parmc.tmodea[i].equals( "" )) {
                     out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
                  }
               }
               out.println("</font><br>");
               
               int tempIndex = 0;
               
               try {
                   tempIndex = Integer.parseInt(index);
               } catch (Exception exc) {
                   tempIndex = 0;
               }
               
               // Do not display the Cancel Tee Time button for Cape Cod National's hotel users if more than 7 days in advance.
               if (!club.equals("capecodnational") || tempIndex <= 7) {
                   out.println("<input type=submit value=\"Cancel Tee Time\" name=\"remove\">&nbsp;&nbsp;&nbsp;");
               }
               
               out.println("<input type=submit value=\"Submit\" name=\"submit\">");
               out.println("</font></td></tr>");
               out.println("</table>");
         out.println("</td>");                                // end of table and column
         out.println("<td align=\"center\" valign=\"top\">");

            out.println("<table border=\"1\" width=\"200\" bgcolor=\"#f5f5dc\">");      // guest list
            out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
            out.println("<font color=\"ffffff\" size=\"2\">");
            out.println("<b>Guest List</b>");
            out.println("</font></td>");
            out.println("</tr><tr>");
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("Click on guest type to add, ");
            out.println("then enter guests names immediately after guest type.");
            out.println("</font></td></tr>");
            //
            //  add a table row for 'guest'
            //
            //     Check the club db table for Guest parms specified by proshop
            //
            //Statement stmtx2 = null;

            try {

               // now look up the guest types for this restriction
               PreparedStatement pstmt3 = con.prepareStatement (
                       "SELECT h.guest_type, g.use_guestdb FROM hotel3_gtypes h " +
                       "INNER JOIN guest5 g ON g.guest = h.guest_type " +
                       "WHERE h.username = ?");

               pstmt3.clearParameters();
               pstmt3.setString(1, user);

               rs3 = pstmt3.executeQuery();

               while ( rs3.next() ) {

                   hguest.add(rs3.getString("h.guest_type"));

                   if (use_guestdb_values) {
                       use_guestdb.add(rs3.getInt("g.use_guestdb"));
                   } else {
                       use_guestdb.add(0);
                   }

               }
               pstmt3.close();

/*
               PreparedStatement pstmtx2 = con.prepareStatement (
                  "SELECT * " +
                  "FROM hotel3 WHERE username = ?");

               pstmtx2.clearParameters();         // clear the parms
               pstmtx2.setString(1, user);        // put the username field in statement
               rs = pstmtx2.executeQuery();       // execute the prepared stmt
*/
               out.println("<!-- HOTEL GUESTS FOUND: " + hguest.size() + " -->");

               if (hguest.size() > 0) {
/*
                  parm.guest[0] = rs.getString("guest1");
                  parm.guest[1] = rs.getString("guest2");
                  parm.guest[2] = rs.getString("guest3");
                  parm.guest[3] = rs.getString("guest4");
                  parm.guest[4] = rs.getString("guest5");
                  parm.guest[5] = rs.getString("guest6");
                  parm.guest[6] = rs.getString("guest7");
                  parm.guest[7] = rs.getString("guest8");
                  parm.guest[8] = rs.getString("guest9");
                  parm.guest[9] = rs.getString("guest10");
                  parm.guest[10] = rs.getString("guest11");
                  parm.guest[11] = rs.getString("guest12");
                  parm.guest[12] = rs.getString("guest13");
                  parm.guest[13] = rs.getString("guest14");
                  parm.guest[14] = rs.getString("guest15");
                  parm.guest[15] = rs.getString("guest16");
                  parm.guest[16] = rs.getString("guest17");
                  parm.guest[17] = rs.getString("guest18");
                  parm.guest[18] = rs.getString("guest19");
                  parm.guest[19] = rs.getString("guest20");
                  parm.guest[20] = rs.getString("guest21");
                  parm.guest[21] = rs.getString("guest22");
                  parm.guest[22] = rs.getString("guest23");
                  parm.guest[23] = rs.getString("guest24");
                  parm.guest[24] = rs.getString("guest25");
                  parm.guest[25] = rs.getString("guest26");
                  parm.guest[26] = rs.getString("guest27");
                  parm.guest[27] = rs.getString("guest28");
                  parm.guest[28] = rs.getString("guest29");
                  parm.guest[29] = rs.getString("guest30");
                  parm.guest[30] = rs.getString("guest31");
                  parm.guest[31] = rs.getString("guest32");
                  parm.guest[32] = rs.getString("guest33");
                  parm.guest[33] = rs.getString("guest34");
                  parm.guest[34] = rs.getString("guest35");
                  parm.guest[35] = rs.getString("guest36");
*/
                  //
                  //  Process the F/B parm    0 = Front 9, 1 = Back 9
                  //
                  sfb = "Front";       // default Front 9

                  if (fb == 1) {

                     sfb = "Back";
                  }
                  //
                  //  determine if any of these types are restricted during this time
                  //
                  PreparedStatement pstmt5 = con.prepareStatement (
                     "SELECT * " +
                     "FROM guestres2 WHERE sdate <= ? AND edate >= ? AND " +
                     "stime <= ? AND etime >= ? AND activity_id = 0");

                  pstmt5.clearParameters();        // clear the parms
                  pstmt5.setLong(1, date);
                  pstmt5.setLong(2, date);
                  pstmt5.setInt(3, time);
                  pstmt5.setInt(4, time);
                  rs2 = pstmt5.executeQuery();      // execute the prepared stmt

                  while (rs2.next()) {

                     grest_recurr = rs2.getString("recurr");
                     rcourse = rs2.getString("courseName");
                     rest_fb = rs2.getString("fb");

                     // now look up the guest types for this restriction
                     PreparedStatement pstmt2 = con.prepareStatement (
                             "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

                     pstmt2.clearParameters();
                     pstmt2.setInt(1, rs2.getInt("id"));

                     rs3 = pstmt2.executeQuery();

                     rguest.clear();

                     while ( rs3.next() ) {

                        rguest.add(rs3.getString("guest_type"));

                     }

                     out.println("<!-- APPLICABLE REST FOUND: Name=" + rs2.getString("name") + ", stime=" + rs2.getInt("stime") + ", etime=" + rs2.getInt("etime") + ", recurr=" + grest_recurr + ", " + rguest.size() + " -->");

                     //
                     //  Check if course matches that specified in restriction
                     //
                     if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

                        //
                        //  We must check the recurrence for this day (Monday, etc.) and guest types
                        //
                        //     parm.guest[x] = guest types specified for this hotel user
                        //     rguest[x] = guest types from restriction gotten above
                        //
                        if ((grest_recurr.equalsIgnoreCase( "every " + day_name )) ||
                            (grest_recurr.equalsIgnoreCase( "every day" )) ||
                            ((grest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                             (!day_name.equalsIgnoreCase( "saturday" )) &&
                             (!day_name.equalsIgnoreCase( "sunday" ))) ||
                            ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                             (day_name.equalsIgnoreCase( "saturday" ))) ||
                            ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                             (day_name.equalsIgnoreCase( "sunday" )))) {

                           //
                           //  Now check if F/B matches
                           //
                           if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                              if (!verifySlot.checkRestSuspend(-99, rs2.getInt("id"), 0, (int)date, time, day_name, course, con)) {        // check if this guest restriction is suspended for this time)
                              
                                  i = 0;
                                  while (i < hguest.size()) {     // check all guest types for this hotel user

                                     if (!hguest.get(i).equals( "" )) {  // if guest type specified for user (must be at least 1)

                                        i2 = 0;
                                        ploop1:
                                        while (i2 < rguest.size()) {     // check all guest types for this restriction

                                           if ( rguest.get(i2).equals( hguest.get(i) )) {
                                              out.println("<!-- FOUND RESTRCTED GUEST: i=" + i + ", " + hguest.get(i) + " -->");
                                              hguest.set(i, "");         // guest type is restricted - do not show
                                              break ploop1;
                                           }
                                           i2++;
                                        }
                                     }
                                     i++;
                                  }
                              }
                           }  // end of IF f/b matches
                        }
                     }      // end of IF course matches
                  }   // end of while loop (while guest restrictions exist)

                  pstmt5.close();

                  //
                  //  count how many fields there will be
                  //
                  xCount = 0; // hguest.size();

                  for (i = 0; i < hguest.size(); i++) {

                     if (!hguest.get(i).equals( "" )) xCount++;

                  }
                  out.println("<!-- NON RESTRCTED GUESTS REMAINING: xCount=" + xCount + " -->");

                  i = 0;
                  if (xCount != 0) {                    // add X and/or guest names to list

                     if (xCount < 2) {

                        xCount = 2;             // set size to at least 2
                     }
                     out.println("<tr><td align=\"left\">");
                     out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                     out.println("<select size=\"" + xCount + "\" name=\"xname\" onClick=\"moveguest(this.form.xname.value)\" style=\"cursor:hand\">");
                     if (xCount != 0) {

                        for (i = 0; i < hguest.size(); i++) {

                           if (!hguest.get(i).equals( "" )) {

                              out.println("<option value=\"" + hguest.get(i) + "|" + use_guestdb.get(i) + "\">" + hguest.get(i) + "</option>");
                           }
                        }
                     }
                     out.println("</select>");
                     out.println("</font></td></tr></table>");      // end of this table and column

                  } else {

                     out.println("</table>");      // end the table and column if none specified
                  }
               } else {

                  out.println("</table>");      // end the table and column if none specified
               }
               //pstmtx2.close();

            }
            catch (Exception exc) {             // SQL Error - ignore guest

               out.println("</table><!-- " + exc.getMessage() + " -->");
            }

         out.println("</td></tr>");
         out.println("</form>");     // end of playerform
      out.println("</table>");      // end of large table containing 2 smaller tables (columns)

   out.println("</font></td></tr>");
   out.println("</table>");                      // end of main page table
   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table>");                      // end of whole page table
   out.println("</font></body></html>");

 }  // end of doPost


 // *********************************************************
 //  Process reservation request from Hotel_slot (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  Get this session's user name
   //
   String user = (String)session.getAttribute("user");
   String fullName = (String)session.getAttribute("name");
   String club = (String)session.getAttribute("club");

   //
   // init all variables
   //
   //int reject = 0;
   int count = 0;
   int in_use = 0;
   int time = 0;
   //int time2 = 0;
   int dd = 0;
   int mm = 0;
   int yy = 0;
   int guests = 0;
   //int grest_num = 0;
   int fb = 0;
   //int fb2 = 0;
   //int t_fb = 0;
   //int members = 0;
   //int x = 0;
   //int xhrs = 0;
   //int calYear = 0;
   //int calMonth = 0;
   //int calDay = 0;
   //int calHr = 0;
   //int calMin = 0;
   //int adv_time = 0;
   //int adv_date = 0;
   int hotelNew = 0;
   int hotelMod = 0;
   int i = 0;
   int i2 = 0;
   //int ind = 0;
   //int xcount = 0;
   //int year = 0;
   //int month = 0;
   //int dayNum = 0;
   //int mtimes = 0;
   //int mems = 0;
   //int rest_stime = 0;
   //int rest_etime = 0;
   //int players = 0;
   //int oldplayers = 0;
   //int gi = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;
   int oldguest_id1 = 0;
   int oldguest_id2 = 0;
   int oldguest_id3 = 0;
   int oldguest_id4 = 0;
   int oldguest_id5 = 0;
   int hide = 0;
   int teecurr_id = 0;

   long temp = 0;
   long ldd = 0;
   long date = 0;
   //long dateStart = 0;
   //long dateEnd = 0;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;

   float hndcp1 = 99;
   float hndcp2 = 99;
   float hndcp3 = 99;
   float hndcp4 = 99;
   float hndcp5 = 99;

   String day = "";
   //String player = "";
   String gplayer = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String oldPlayer1 = "";
   String oldPlayer2 = "";
   String oldPlayer3 = "";
   String oldPlayer4 = "";
   String oldPlayer5 = "";
   String oldUser1 = "";
   String oldUser2 = "";
   String oldUser3 = "";
   String oldUser4 = "";
   String oldUser5 = "";
   String oldp1cw = "";
   String oldp2cw = "";
   String oldp3cw = "";
   String oldp4cw = "";
   String oldp5cw = "";
   String oldNotes = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String sfb = "";
   //String course2 = "";
   String notes = "";
   //String notes2 = "";
   String rcourse = "";
   String rest_fb = "";
   String grest_recurr = "";
   String g1 = "";
   String g2 = "";
   String g3 = "";
   String g4 = "";
   String g5 = "";/*
   String period = "";
   String mperiod = "";
   String plyr1 = "";
   String plyr2 = "";
   String plyr3 = "";
   String plyr4 = "";
   String plyr5 = "";*/
   String msg = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   //String memberName = "";
   String in_use_by = "";
   String orig_by = "";
   String p9s = "";

   boolean guestdbTbaAllowed = false;
   boolean invalidTrackedGuest = false;
   boolean use_guestdb_values = Utilities.isGuestTrackingConfigured(0, con);

   //boolean check = false;
   //boolean hit = false;
   //boolean hit2 = false;
   //boolean guestError = false;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);

   //
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block (used for customs)

   //
   // Get the Multiple Course Option
   //
   try {
      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception ignore) {
   }

   //  If guest tracking is in use, determine whether names are optional or required
   if (Utilities.isGuestTrackingConfigured(0, con) && Utilities.isGuestTrackingTbaAllowed(0, true, con)) {
       guestdbTbaAllowed = true;
   }

   //String [] rguest = new String [parm.MAX_Guests];    // array to hold the Guest Restriction guest names
   ArrayList<String> rguest = new ArrayList<String>();
   ArrayList<String> hguest = new ArrayList<String>();   // available guests this hotel user can access (was xguests[])
   ArrayList<Integer> use_guestdb = new ArrayList<Integer>();       // use_guestdb values for each hotel guest type
   String [] gstA = new String [5];       // guests

   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String smm = req.getParameter("mm");               //  month of tee time
   String syy = req.getParameter("yy");               //  year of tee time
   String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
   String p5 = req.getParameter("p5");                //  5-somes supported for this slot
   String course = req.getParameter("course");        //  name of course
   player1 = req.getParameter("player1");
   player2 = req.getParameter("player2");
   player3 = req.getParameter("player3");
   player4 = req.getParameter("player4");
   player5 = req.getParameter("player5");
   p1cw = req.getParameter("p1cw");
   p2cw = req.getParameter("p2cw");
   p3cw = req.getParameter("p3cw");
   p4cw = req.getParameter("p4cw");
   p5cw = req.getParameter("p5cw");
   guest_id1 = Integer.parseInt(req.getParameter("guest_id1"));
   guest_id2 = Integer.parseInt(req.getParameter("guest_id2"));
   guest_id3 = Integer.parseInt(req.getParameter("guest_id3"));
   guest_id4 = Integer.parseInt(req.getParameter("guest_id4"));
   guest_id5 = Integer.parseInt(req.getParameter("guest_id5"));
   day = req.getParameter("day");                      // name of day
   sfb = req.getParameter("fb");                       // Front/Back indicator
   notes = req.getParameter("notes");                  // Notes
   
   String hides = "";
   String conf = "";
   
   if (req.getParameter("hide") != null) {        
      hides = req.getParameter("hide");
      hide = Integer.parseInt(hides);           // get the hide option
   }
   if (req.getParameter("conf") != null) {        
      conf = req.getParameter("conf");
   }
   

   //
   //  set 9-hole options
   //
   if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      p91 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      p92 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      p93 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      p94 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      p95 = Integer.parseInt(p9s);
   }

   //
   //  Convert date & time from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      mm = Integer.parseInt(smm);
      yy = Integer.parseInt(syy);
      fb = Integer.parseInt(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   String jump = "0";                     // jump index - default to zero (for _sheet)

   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }

   //
   //  Get the length of Notes (max length of 254 chars)
   //
   int notesL = 0;

   if (!notes.equals( "" )) {

      notesL = notes.length();       // get length of notes
   }

   
   
   //
   //   use yy and mm and date to determine dd (from tee time's date)
   //
   temp = yy * 10000;
   temp = temp + (mm * 100);
   ldd = date - temp;            // get day of month from date

   dd = (int) ldd;               // convert to int

   //
   //  Check if this tee slot is still 'in use' and still in use by this user??
   //
   //  This is necessary because the user may have gone away while holding this slot.  If the
   //  slot timed out (system timer), the slot would be marked 'not in use' and another
   //  user could pick it up.  The original holder could be trying to use it now.
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT " +
            "player1, player2, player3, player4, username1, username2, username3, " +
            "username4, p1cw, p2cw, p3cw, p4cw, in_use, in_use_by, " +
            "show1, show2, show3, show4, player5, username5, p5cw, show5, hotelNew, hotelMod, " +
            "orig_by, notes, teecurr_id, guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 " +
         "FROM teecurr2 " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();
      pstmt.setLong(1, date);
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, course);
      rs = pstmt.executeQuery();

      if (rs.next()) {

         oldPlayer1 = rs.getString(1);
         oldPlayer2 = rs.getString(2);
         oldPlayer3 = rs.getString(3);
         oldPlayer4 = rs.getString(4);
         oldUser1 = rs.getString(5);
         oldUser2 = rs.getString(6);
         oldUser3 = rs.getString(7);
         oldUser4 = rs.getString(8);
         oldp1cw = rs.getString(9);
         oldp2cw = rs.getString(10);
         oldp3cw = rs.getString(11);
         oldp4cw = rs.getString(12);
         in_use = rs.getInt(13);
         in_use_by = rs.getString(14);
         show1 = rs.getShort(15);
         show2 = rs.getShort(16);
         show3 = rs.getShort(17);
         show4 = rs.getShort(18);
         oldPlayer5 = rs.getString(19);
         oldUser5 = rs.getString(20);
         oldp5cw = rs.getString(21);
         show5 = rs.getShort(22);
         hotelNew = rs.getInt(23);
         hotelMod = rs.getInt(24);
         orig_by = rs.getString(25);
         oldNotes = rs.getString("notes");
         teecurr_id = rs.getInt("teecurr_id");
         oldguest_id1 = rs.getInt("guest_id1");
         oldguest_id2 = rs.getInt("guest_id2");
         oldguest_id3 = rs.getInt("guest_id3");
         oldguest_id4 = rs.getInt("guest_id4");
         oldguest_id5 = rs.getInt("guest_id5");
      }
      pstmt.close();

      if (orig_by.equals( "" )) {    // if originator field still empty

         orig_by = user;             // set this user as the originator
      }

      if ((in_use == 0) || (!in_use_by.equals( user ))) {    // if time slot in use and not by this user

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<Body bgcolor=\"#ccccaa\"><CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
         out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system.<BR>");
         out.println("<BR>The system timed out and released the tee time.");
         out.println("<BR><BR>date= " +date+ ", time= " +time+ ", fb= " +fb+ ", course= " + course);
         out.println("<BR><BR>");
         if (index.equals( "999" )) {      // if from Hotel_search (doGet)

            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchmy.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");

         } else {

            if (index.equals( "888" )) {      // if from Hotel_search (doPost)

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchm.htm\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
               out.println("</form></font>");

            } else {

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_selmain.htm\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
               out.println("</form></font>");
            }
         }
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }
   catch (Exception e) {

      msg = "Check if busy. ";

      dbError(out, e, msg);
      return;
   }

   //
   //  If request is to 'Cancel This Res', then clear all fields for this slot
   //
   //  First, make sure user is already on tee slot
   //
   if (req.getParameter("remove") != null) {

      //
      //  Now see if this action has been confirmed yet
      //
      if (req.getParameter("ack_remove") != null) {  // if remove has been confirmed

         player1 = "";                  // set reservation fields to null
         player2 = "";
         player3 = "";
         player4 = "";
         player5 = "";
         p1cw = "";
         p2cw = "";
         p3cw = "";
         p4cw = "";
         p5cw = "";
         user1 = "";
         user2 = "";
         user3 = "";
         user4 = "";
         user5 = "";
         show1 = 0;
         show2 = 0;
         show3 = 0;
         show4 = 0;
         show5 = 0;
         guest_id1 = 0;
         guest_id2 = 0;
         guest_id3 = 0;
         guest_id4 = 0;
         guest_id5 = 0;
         notes = "";
         orig_by = "";
         conf = "";
         hide = 0;

         hotelMod++;      // increment number of mods for reports

      } else {    // not acked yet - display confirmation page

         out.println(SystemUtils.HeadTitle("Cancel Tee Time Confirmation Prompt"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><font size=\"6\" color=\"red\"><b>***WARNING***</b><BR>");
         out.println("</font><font size=\"3\"><BR>This will remove ALL players from the tee time.<BR>");
         out.println("<BR>If this is what you want to do, then click on 'Continue' below.<BR>");
         out.println("<BR>");

         out.println("<form action=\"Hotel_slot\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"remove\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"ack_remove\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
         out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
         out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
         out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
         out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
         out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
         out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
         out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
         out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
         out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
         out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
         out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
         out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
         out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
         out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
         out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
         out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
         out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
         out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"submit\" value=\"Continue\" name=\"submit\"></form>");

         out.println("<BR>If you only want to remove a portion of the players,<BR>");
         out.println("click on 'Return' below. Then use the 'erase' and 'Submit' buttons<BR>");
         out.println("to remove only those players you wish to remove.<BR>");
         out.println("<BR>");

            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"Hotel_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;    // wait for acknowledgement
      }

   } else {        //  not a 'Cancel Tee Time' request

      //
      //  Normal request -
      //
      //  Shift players up if any empty spots
      //
      if (player1.equals( "" )) {    // if empty

         if (!player2.equals( "" )) {    // if not empty

            player1 = player2;
            p1cw = p2cw;
            p91 = p92;
            guest_id1 = guest_id2;
            player2 = "";
            guest_id2 = 0;

         } else {

            if (!player3.equals( "" )) {    // if not empty

               player1 = player3;
               p1cw = p3cw;
               p91 = p93;
               guest_id1 = guest_id3;
               player3 = "";
               guest_id3 = 0;

            } else {

               if (!player4.equals( "" )) {    // if not empty

                  player1 = player4;
                  p1cw = p4cw;
                  p91 = p94;
                  guest_id1 = guest_id4;
                  player4 = "";
                  guest_id4 = 0;

               } else {

                  if (!player5.equals( "" )) {    // if not empty

                     player1 = player5;
                     p1cw = p5cw;
                     p91 = p95;
                     guest_id1 = guest_id5;
                     player5 = "";
                     guest_id5 = 0;
                  }
               }
            }
         }
      }
      if (player2.equals( "" )) {    // if empty

         if (!player3.equals( "" )) {    // if not empty

            player2 = player3;
            p2cw = p3cw;
            p92 = p93;
            guest_id2 = guest_id3;
            player3 = "";
            guest_id3 = 0;

         } else {

            if (!player4.equals( "" )) {    // if not empty

               player2 = player4;
               p2cw = p4cw;
               p92 = p94;
               guest_id2 = guest_id4;
               player4 = "";
               guest_id4 = 0;

            } else {

               if (!player5.equals( "" )) {    // if not empty

                  player2 = player5;
                  p2cw = p5cw;
                  p92 = p95;
                  guest_id2 = guest_id5;
                  player5 = "";
                  guest_id5 = 0;
               }
            }
         }
      }
      if (player3.equals( "" )) {    // if empty

         if (!player4.equals( "" )) {    // if not empty

            player3 = player4;
            p3cw = p4cw;
            p93 = p94;
            guest_id3 = guest_id4;
            player4 = "";
            guest_id4 = 0;

         } else {

            if (!player5.equals( "" )) {    // if not empty

               player3 = player5;
               p3cw = p5cw;
               p93 = p95;
               guest_id3 = guest_id5;
               player5 = "";
               guest_id5 = 0;
            }
         }
      }
      if (player4.equals( "" )) {    // if empty

         if (!player5.equals( "" )) {    // if not empty

            player4 = player5;
            p4cw = p5cw;
            p94 = p95;
            guest_id4 = guest_id5;
            player5 = "";
            guest_id5 = 0;
         }
      }

      //
      //   Get the guest names specified for this hotel user
      //
      try {



         // now look up the guest types for this restriction
         PreparedStatement pstmt3 = con.prepareStatement (
                 "SELECT h.guest_type, g.use_guestdb FROM hotel3_gtypes h " +
                 "LEFT OUTER JOIN guest5 g ON g.guest = h.guest_type " +
                 "WHERE h.username = ?");

         pstmt3.clearParameters();
         pstmt3.setString(1, user);

         ResultSet rs3 = pstmt3.executeQuery();

         hguest.clear();

         while ( rs3.next() ) {

             hguest.add(rs3.getString("h.guest_type"));

             if (use_guestdb_values) {
                 use_guestdb.add(rs3.getInt("g.use_guestdb"));
             } else {
                 use_guestdb.add(0);
             }

         }
         pstmt3.close();

/*
         PreparedStatement pstmtx2 = con.prepareStatement (
            "SELECT * " +
            "FROM hotel3 WHERE username = ?");

         pstmtx2.clearParameters();         // clear the parms
         pstmtx2.setString(1, user);        // put the username field in statement
         rs = pstmtx2.executeQuery();       // execute the prepared stmt

         if (rs.next()) {
*/
         if (hguest.size() > 0) {
             
            //
            //  Process the F/B parm    0 = Front 9, 1 = Back 9
            //
            sfb = "Front";       // default Front 9

            if (fb == 1) {

               sfb = "Back";
            }
            //
            //  determine if any of these types are restricted during this time
            //
            PreparedStatement pstmt5 = con.prepareStatement (
               "SELECT * " +
               "FROM guestres2 WHERE sdate <= ? AND edate >= ? AND " +
               "stime <= ? AND etime >= ? AND activity_id = 0");

            pstmt5.clearParameters();        // clear the parms
            pstmt5.setLong(1, date);
            pstmt5.setLong(2, date);
            pstmt5.setInt(3, time);
            pstmt5.setInt(4, time);
            rs2 = pstmt5.executeQuery();      // execute the prepared stmt

            while (rs2.next()) {

               grest_recurr = rs2.getString("recurr");
               rcourse = rs2.getString("courseName");
               rest_fb = rs2.getString("fb");
               rguest.clear();

               // now look up the guest types for this restriction
               PreparedStatement pstmt2 = con.prepareStatement (
                       "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

               pstmt2.clearParameters();
               pstmt2.setInt(1, rs2.getInt("id"));

               rs3 = pstmt2.executeQuery();

               while ( rs3.next() ) {

                  rguest.add(rs3.getString("guest_type"));

               }

               pstmt2.close();

               //
               //  Check if course matches that specified in restriction
               //
               if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

                  //
                  //  We must check the recurrence for this day (Monday, etc.) and guest types
                  //
                  //     guestx = guest types specified for this hotel user
                  //     rguestx = guest types from restriction gotten above
                  //
                  if ((grest_recurr.equalsIgnoreCase( "every " + day )) ||
                      (grest_recurr.equalsIgnoreCase( "every day" )) ||
                      ((grest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                       (!day.equalsIgnoreCase( "saturday" )) &&
                       (!day.equalsIgnoreCase( "sunday" ))) ||
                      ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                       (day.equalsIgnoreCase( "saturday" ))) ||
                      ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                       (day.equalsIgnoreCase( "sunday" )))) {

                     //
                     //  Now check if F/B matches
                     //
                     if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                        if (!verifySlot.checkRestSuspend(-99, rs2.getInt("id"), 0, (int)date, time, day, course, con)) {        // check if this guest restriction is suspended for this time)

                            i = 0;
                            while (i < hguest.size()) {     // check all guest types for this hotel user

                               if (!hguest.get(i).equals( "" )) {  // if guest type specified for user (must be at least 1)

                                  i2 = 0;
                                  ploop1:
                                  while (i2 < rguest.size()) {     // check all guest types for this restriction

                                     if ( rguest.get(i2).equals( hguest.get(i) )) {

                                        hguest.set(i, "");     // guest type is restricted - do not show
                                        break ploop1;
                                     }
                                     i2++;
                                  }
                               }
                               i++;
                            }
                        }
                     }  // end of IF f/b matches
                  }
               }      // end of IF course matches
            }   // end of while loop (while guest restrictions exist)

            pstmt5.close();

         }
         //pstmtx2.close();
      }
      catch (Exception ignore) {
      }

      //
      //   Remove any guest types that are null - for tests below
      //
/*
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player field
         }
         i++;
      }         // end of while loop
*/
      //
      //  Check if any player names are not valid guest names
      //
      gstA[0] = "";    // init guest array and indicators
      gstA[1] = "";
      gstA[2] = "";
      gstA[3] = "";
      gstA[4] = "";
      g1 = "";
      g2 = "";
      g3 = "";
      g4 = "";
      g5 = "";
      gplayer = "";
      invalidTrackedGuest = false;

      if (!player1.equals( "" )) {

         gplayer = player1;          // preset to invalid player
         i = 0;
         loop1:
         while (i < hguest.size()) {

             if (!hguest.get(i).equals("") && player1.startsWith(hguest.get(i))) {

               g1 = hguest.get(i);       // indicate player is a guest name and save name
               gstA[0] = player1;    // save guest value
               guests++;             // increment number of guests this slot
               gplayer = "";         // player ok

               // If tracked guest, validate that they are legitimate
               if (use_guestdb.get(i) == 1) {
                   
                   if (!guestdbTbaAllowed || guest_id1 != 0 || !player1.equals(hguest.get(i) + " TBA")) {

                       if (guest_id1 == 0 || verifySlot.checkTrackedGuestName(player1, guest_id1, hguest.get(i), club, con)) {
                           gplayer = player1;
                           invalidTrackedGuest = true;
                       }
                   }
               }
               break loop1;
            }
            i++;
            
         }         // end of while loop
      }
      if (!player2.equals( "" ) && gplayer.equals( "" )) {

         gplayer = player2;          // preset to invalid player
         i = 0;
         loop2:
         while (i < hguest.size()) {

             if (!hguest.get(i).equals("") && player2.startsWith(hguest.get(i))) {

               g2 = hguest.get(i);       // indicate player is a guest name and save name
               gstA[1] = player2;    // save guest value
               guests++;             // increment number of guests this slot
               gplayer = "";         // player ok

               // If tracked guest, validate that they are legitimate
               if (use_guestdb.get(i) == 1) {

                   if (!guestdbTbaAllowed || guest_id2 != 0 || !player2.equals(hguest.get(i) + " TBA")) {

                       if (guest_id2 == 0 || verifySlot.checkTrackedGuestName(player2, guest_id2, hguest.get(i), club, con)) {
                           gplayer = player2;
                           invalidTrackedGuest = true;
                       }
                   }
               }
               break loop2;
            }
            i++;
         }         // end of while loop
      }
      if (!player3.equals( "" ) && gplayer.equals( "" )) {

         gplayer = player3;          // preset to invalid player
         i = 0;
         loop3:
         while (i < hguest.size()) {

             if (!hguest.get(i).equals("") && player3.startsWith(hguest.get(i))) {

               g3 = hguest.get(i);       // indicate player is a guest name and save name
               gstA[2] = player3;    // save guest value
               guests++;             // increment number of guests this slot
               gplayer = "";         // player ok

               // If tracked guest, validate that they are legitimate
               if (use_guestdb.get(i) == 1) {

                   if (!guestdbTbaAllowed || guest_id3 != 0 || !player3.equals(hguest.get(i) + " TBA")) {

                       if (guest_id3 == 0 || verifySlot.checkTrackedGuestName(player3, guest_id3, hguest.get(i), club, con)) {
                           gplayer = player3;
                           invalidTrackedGuest = true;
                       }
                   }
               }
               break loop3;
            }
            i++;
         }         // end of while loop
      }
      if (!player4.equals( "" ) && gplayer.equals( "" )) {

         gplayer = player4;          // preset to invalid player
         i = 0;
         loop4:
         while (i < hguest.size()) {

             if (!hguest.get(i).equals("") && player4.startsWith(hguest.get(i))) {

               g4 = hguest.get(i);       // indicate player is a guest name and save name
               gstA[3] = player4;    // save guest value
               guests++;             // increment number of guests this slot
               gplayer = "";         // player ok

               // If tracked guest, validate that they are legitimate
               if (use_guestdb.get(i) == 1) {

                   if (!guestdbTbaAllowed || guest_id4 != 0 || !player4.equals(hguest.get(i) + " TBA")) {

                       if (guest_id4 == 0 || verifySlot.checkTrackedGuestName(player4, guest_id4, hguest.get(i), club, con)) {
                           gplayer = player4;
                           invalidTrackedGuest = true;
                       }
                   }
               }
               break loop4;
            }
            i++;
         }         // end of while loop
      }
      if (!player5.equals( "" ) && gplayer.equals( "" )) {

         gplayer = player5;          // preset to invalid player
         i = 0;
         loop5:
         while (i < hguest.size()) {

             if (!hguest.get(i).equals("") && player5.startsWith(hguest.get(i))) {

               g5 = hguest.get(i);       // indicate player is a guest name and save name
               gstA[4] = player5;    // save guest value
               guests++;             // increment number of guests this slot
               gplayer = "";         // player ok

               // If tracked guest, validate that they are legitimate
               if (use_guestdb.get(i) == 1) {

                   if (!guestdbTbaAllowed || guest_id5 != 0 || !player5.equals(hguest.get(i) + " TBA")) {

                       if (guest_id5 == 0 || verifySlot.checkTrackedGuestName(player5, guest_id5, hguest.get(i), club, con)) {
                           gplayer = player5;
                           invalidTrackedGuest = true;
                       }
                   }
               }
               break loop5;
            }
            i++;
         }         // end of while loop
      }

      //
      //  Reject if any player was a guest type that is not allowed for this hotel
      //
      if (!gplayer.equals( "" )) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");

         // Output different message depending on the reason failed
         if (invalidTrackedGuest) {
             out.println("<BR><BR><H3>Data Entry Error</H3>");
             out.println("<BR><BR><b>" + gplayer + "</b> appears to have been manually entered or " +
                     "<br>modified after selecting a different guest from the Guest Selection window.");
             out.println("<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' ");
             out.println("<BR>next to the current guest's name, then click the desired guest type from the Guest ");
             out.println("<BR>Types list, and finally select a guest from the displayed guest selection window.");
             out.println("<BR><BR>");
         } else {
             out.println("<BR><BR><H3>Data Entry Error</H3>");
             out.println("<BR><BR><b>" + gplayer + "</b> contains a name or Guest Type that is not allowed for your use.");
             out.println("<BR><BR>If entering a guest's name, you must first select the guest type and then type");
             out.println("<BR>a space followed immediately by the player's name.");
             out.println("<BR><BR>Please correct this and try again.");
             out.println("<BR><BR>");
         }
         //
         //  Return to _slot to change the player order
         //
         out.println("<font size=\"2\">");
         out.println("<form action=\"Hotel_slot\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
         out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
         out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
         out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
         out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
         out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
         out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
         out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
         out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
         out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
         out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
         out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
         out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
         out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
         out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
         out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
         out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
         out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
         out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#336633\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      //  Make sure there is at least one guest
      //
      if (player1.equals( "" ) && player2.equals( "" ) && player3.equals( "" ) && player4.equals( "" ) &&
          player5.equals( "" )) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>At least one player field must contains a guest name.");
         out.println("<BR>You must select the 'guest type' first and then enter the guest's name immediately after that.");
         out.println("<BR><BR>");
         out.println("Please return and correct this. Use the 'Go Back' button if you want to return without changes.");
         out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"Hotel_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      //  At least 1 Player is present - Make sure a C/W was specified for all players
      //
      if (((!player1.equals( "" )) && (p1cw.equals( "" ))) ||
          ((!player2.equals( "" )) && (p2cw.equals( "" ))) ||
          ((!player3.equals( "" )) && (p3cw.equals( "" ))) ||
          ((!player4.equals( "" )) && (p4cw.equals( "" ))) ||
          ((!player5.equals( "" )) && (p5cw.equals( "" )))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
         out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"Hotel_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; \">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }


      //
      //***************************************************************************************************
      //
      //  CUSTOMS - check all possible customs here 
      //
      //***************************************************************************************************
      //
      boolean error = false;
      
      if (club.equals("capecodnational")) {     // Cape Cod National
         
         //
         //   Put the tee time values in slotParms for verifyCustom
         //
         slotParms.date = date;
         slotParms.time = time;
         slotParms.day = day;
         slotParms.player1 = player1;
         slotParms.player2 = player2;
         slotParms.player3 = player3;
         slotParms.player4 = player4;
         slotParms.teecurr_id = teecurr_id;
         
         

          //  Check for "Wequassett Guest" (Hotel) type guests and enforce the club's limitations
          error = verifyCustom.checkCapeCodGsts(slotParms, con);

          if (error) {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><H3>Wequassett Guest Quota Reached</H3>");
               out.println("<BR><BR>Sorry, but the quota for Wequassett Guests has already been reached for this day and time.");
               out.println("<BR><BR>Please select a different time of the day or a different day.");
               out.println("<BR><BR>");
                  //
                  //  Return to _slot to change the player order
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"Hotel_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; \">");
                  out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               return;
          }

      }    // end of Cape Cod custom

      
      
      
      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************

      //
      //  If players changed, then init the no-show flag, else use the old no-show value
      //
      if (!player1.equals( oldPlayer1 )) {

         show1 = 0;        // init no-show flag
      }

      if (!player2.equals( oldPlayer2 )) {

         show2 = 0;        // init no-show flag
      }

      if (!player3.equals( oldPlayer3 )) {

         show3 = 0;        // init no-show flag
      }

      if (!player4.equals( oldPlayer4 )) {

         show4 = 0;        // init no-show flag
      }

      if (!player5.equals( oldPlayer5 )) {

         show5 = 0;        // init no-show flag
      }

      //
      //    bump stats counters for reports
      //
      if ((!oldPlayer1.equals( "" )) || (!oldPlayer2.equals( "" )) || (!oldPlayer3.equals( "" )) ||
          (!oldPlayer4.equals( "" )) || (!oldPlayer5.equals( "" ))) {

         hotelMod++;      // increment number of mods
              
      } else {

         hotelNew++;      // increment number of new tee times
         
      }
      
      if (club.startsWith("tpc")) {
         
         //
         //  TPC  - set the hide flag if the user added to the notes
         //
         if (notesL > 0 && !notes.equals(oldNotes)) {     // if notes provided and not the same as the original
            
            hide = 1;
         }         
      }

   }  // end of 'cancel this res' if - cancel will contain empty player fields

   //
   //  Verification complete -
   //  Update the tee slot in teecurr
   //
   try {

      PreparedStatement pstmt6 = con.prepareStatement (
         "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
         "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
         "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
         "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
         "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, " +
         "mNum1 = '', mNum2 = '', mNum3 = '', mNum4 = '', mNum5 = '', " +
         "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, hotelNew = ?, hotelMod = ?, " +
         "orig_by = ?, conf = ?, p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, hideNotes = ?, " +
         "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setString(1, player1);
      pstmt6.setString(2, player2);
      pstmt6.setString(3, player3);
      pstmt6.setString(4, player4);
      pstmt6.setString(5, user1);
      pstmt6.setString(6, user2);
      pstmt6.setString(7, user3);
      pstmt6.setString(8, user4);
      pstmt6.setString(9, p1cw);
      pstmt6.setString(10, p2cw);
      pstmt6.setString(11, p3cw);
      pstmt6.setString(12, p4cw);
      pstmt6.setFloat(13, hndcp1);
      pstmt6.setFloat(14, hndcp2);
      pstmt6.setFloat(15, hndcp3);
      pstmt6.setFloat(16, hndcp4);
      pstmt6.setShort(17, show1);
      pstmt6.setShort(18, show2);
      pstmt6.setShort(19, show3);
      pstmt6.setShort(20, show4);
      pstmt6.setString(21, player5);
      pstmt6.setString(22, user5);
      pstmt6.setString(23, p5cw);
      pstmt6.setFloat(24, hndcp5);
      pstmt6.setShort(25, show5);
      pstmt6.setString(26, notes);
      pstmt6.setString(27, userg1);
      pstmt6.setString(28, userg2);
      pstmt6.setString(29, userg3);
      pstmt6.setString(30, userg4);
      pstmt6.setString(31, userg5);
      pstmt6.setInt(32, hotelNew);
      pstmt6.setInt(33, hotelMod);
      pstmt6.setString(34, orig_by);
      pstmt6.setString(35, conf);
      pstmt6.setInt(36, p91);
      pstmt6.setInt(37, p92);
      pstmt6.setInt(38, p93);
      pstmt6.setInt(39, p94);
      pstmt6.setInt(40, p95);
      pstmt6.setInt(41, hide);
      pstmt6.setInt(42, guest_id1);
      pstmt6.setInt(43, guest_id2);
      pstmt6.setInt(44, guest_id3);
      pstmt6.setInt(45, guest_id4);
      pstmt6.setInt(46, guest_id5);

      pstmt6.setLong(47, date);
      pstmt6.setInt(48, time);
      pstmt6.setInt(49, fb);
      pstmt6.setString(50, course);
      count = pstmt6.executeUpdate();      // execute the prepared stmt

      pstmt6.close();

   }
   catch (Exception e6) {

      msg = "Update Hotel Tee Time. ";

      dbError(out, e6, msg);
      return;
   }

   
   //
   //    Record this in the tee time history
   //
   if ((!oldPlayer1.equals( "" )) || (!oldPlayer2.equals( "" )) || (!oldPlayer3.equals( "" )) ||
       (!oldPlayer4.equals( "" )) || (!oldPlayer5.equals( "" ))) {

      //  update tee time - add history entry
      SystemUtils.updateHist(date, day, time, fb, course, player1, player2, player3,
                             player4, player5, user, fullName, 1, con);

   } else {

      //  new tee time - add history entry
      SystemUtils.updateHist(date, day, time, fb, course, player1, player2, player3,  
                             player4, player5, user, fullName, 0, con);
   }


   //
   //  Build the HTML page to confirm reservation for user
   //
   out.println(SystemUtils.HeadTitle("Hotel Tee Slot Page"));
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (req.getParameter("remove") != null) {

      out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The reservation has been cancelled.</p>");
   } else {

      out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your reservation has been accepted and processed.</p>");

      if (notesL > 254) {

      out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
      }

      out.println("<font size=\"2\">");
      out.println("<form method=\"post\" action=\"Hotel_slot\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
      out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
      out.println("<input type=\"hidden\" name=\"print-conf\" value=\"print_conf\">");
      out.println("<input type=\"image\" src=\"/" +rev+ "/images/print_conf.gif\" alt=\"Print a Confirmation\">");
      out.println("</form></font>");
   }

   out.println("<p>&nbsp;</p></font>");

   if (index.equals( "888" )) {         // if came from Hotel_search (doPost)

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchm.htm\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
      out.println("</form></font>");

   } else {

      if (index.equals( "999" )) {         // if came from Hotel_search (doGet)

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchmy.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

      } else {                                // return to Hotel_sheet - must rebuild frames first

         out.println("<font size=\"2\">");
         out.println("<form action=\"Hotel_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("</form></font>");
      }
   }

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");


   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

 }       // end of verify


 // ************************************************************************
 //  Process cancel request (Return w/o changes) from Hotel_slot (HTML)
 // ************************************************************************

 private void cancel(HttpServletRequest req, PrintWriter out, Connection con) {


   int count = 0;
   int time  = 0;
   int fb  = 0;
   long date  = 0;

   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String sfb = req.getParameter("fb");               //  front/back indicator
   String index = req.getParameter("index");          //  index value of day (needed by Hotel_sheet when returning)
   String course = req.getParameter("course");        //  name of course (needed by Hotel_sheet when returning)

   //
   //  Convert the values from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      fb = Integer.parseInt(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Clear the 'in_use' flag for this time slot in teecurr
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET in_use = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, date);         // put the parm in pstmt1
      pstmt1.setInt(2, time);
      pstmt1.setInt(3, fb);
      pstmt1.setString(4, course);
      count = pstmt1.executeUpdate();      // execute the prepared stmt

      pstmt1.close();

   }
   catch (Exception ignore) {

   }

   //
   //  Prompt user to return to Hotel_sheet or Hotel_search
   //
   out.println("<HTML><HEAD><Title>Hotel Tee Slot Page</Title>");

   if (index.equals( "999" )) {       // if from Hotel_search (doGet)

      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/hotel_searchmy.htm\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
      out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchmy.htm\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
      out.println("</form></font>");

   } else {

      if (index.equals( "888" )) {       // if from Hotel_search (doPost)

         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/hotel_searchm.htm\">");
         out.println("</HEAD>");
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
         out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
         out.println("<BR><BR>");

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchm.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

      } else {

         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Hotel_jump?index=" + index + "&course=" + course + "\">");
         out.println("</HEAD>");
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
         out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
         out.println("<BR><BR>");

         out.println("<font size=\"2\">");
         out.println("<form action=\"Hotel_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
         out.println("</form></font>");
      }
   }
   out.println("</CENTER></BODY></HTML>");
 }


 // ************************************************************************
 //  Process a Confirmation Print Request
 // ************************************************************************

 private void printConf(HttpServletRequest req, PrintWriter out, Connection con) {


   int time  = 0;
   int fb  = 0;
   long date  = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;
   long temp = 0;

   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("sdate");        //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("stime");        //  time of tee time requested (hhmm)
   String sfb = req.getParameter("fb");             //  front/back indicator
   String index = req.getParameter("index");        //  index value of day (needed by Hotel_sheet when returning)
   String course = req.getParameter("course");      //  name of course (needed by Hotel_sheet when returning)
   String player1 = req.getParameter("player1");
   String player2 = req.getParameter("player2");
   String player3 = req.getParameter("player3");
   String player4 = req.getParameter("player4");
   String player5 = req.getParameter("player5");
   String p1cw = req.getParameter("p1cw");
   String p2cw = req.getParameter("p2cw");
   String p3cw = req.getParameter("p3cw");
   String p4cw = req.getParameter("p4cw");
   String p5cw = req.getParameter("p5cw");
   String jump = req.getParameter("jump");
   String day = req.getParameter("day");

   //
   //  Convert the values from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      fb = Integer.parseInt(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  isolate yy, mm, dd
   //
   yy = date / 10000;
   temp = yy * 10000;
   mm = date - temp;
   temp = mm / 100;
   temp = temp * 100;
   dd = mm - temp;
   mm = mm / 100;

   //
   //  create a time string for display
   //
   int hr = time / 100;
   int min = time - (hr * 100);

   String ampm = " AM";

   if (hr > 11) {

      ampm = " PM";

      if (hr > 12) {

         hr = hr - 12;
      }
   }
   if (min < 10) {
      stime = hr + ":0" + min + ampm;
   } else {
      stime = hr + ":" + min + ampm;
   }

   //
   //  Build the HTML page to display a confirmation
   //
   out.println(SystemUtils.HeadTitle("Hotel Tee Slot Page"));
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><hr width=\"40%\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<br><br>");
   out.println("<H3>Guest Tee Time Confirmation</H3>");
   out.println("<br>Date:&nbsp;&nbsp;<b>" + day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
   out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Tee Time:&nbsp;&nbsp;<b>" + stime + "</b>");
   if (!course.equals( "" )) {
      out.println(" &nbsp;&nbsp;&nbsp;&nbsp;On Course:&nbsp;&nbsp;<b>" + course + "</b>");
   }
   out.println("</font>");
   out.println("<br><br>");

   out.println("<table border=\"2\" cellpadding=\"5\" cellspacing=\"5\" align=\"center\">");
   out.println("<tr><td>");
      out.println("<font size=\"2\">");
      out.println("Player 1: ");
      out.println("</font>");
   out.println("</td><td>");
      out.println("<font size=\"2\">");
      out.println("&nbsp;&nbsp;&nbsp;" +player1+ "&nbsp;&nbsp;&nbsp;");
      out.println("</font>");
   out.println("</td></tr>");

   if (!player2.equals( "" )) {
      out.println("<tr><td>");
         out.println("<font size=\"2\">");
         out.println("Player 2: ");
         out.println("</font>");
      out.println("</td><td>");
         out.println("<font size=\"2\">");
         out.println("&nbsp;&nbsp;&nbsp;" +player2+ "&nbsp;&nbsp;&nbsp;");
         out.println("</font>");
      out.println("</td></tr>");
   }

   if (!player3.equals( "" )) {
      out.println("<tr><td>");
         out.println("<font size=\"2\">");
         out.println("Player 3: ");
         out.println("</font>");
      out.println("</td><td>");
         out.println("<font size=\"2\">");
         out.println("&nbsp;&nbsp;&nbsp;" +player3+ "&nbsp;&nbsp;&nbsp;");
         out.println("</font>");
      out.println("</td></tr>");
   }

   if (!player4.equals( "" )) {
      out.println("<tr><td>");
         out.println("<font size=\"2\">");
         out.println("Player 4: ");
         out.println("</font>");
      out.println("</td><td>");
         out.println("<font size=\"2\">");
         out.println("&nbsp;&nbsp;&nbsp;" +player4+ "&nbsp;&nbsp;&nbsp;");
         out.println("</font>");
      out.println("</td></tr>");
   }

   if (!player5.equals( "" )) {
      out.println("<tr><td>");
         out.println("<font size=\"2\">");
         out.println("Player 5: ");
         out.println("</font>");
      out.println("</td><td>");
         out.println("<font size=\"2\">");
         out.println("&nbsp;&nbsp;&nbsp;" +player5+ "&nbsp;&nbsp;&nbsp;");
         out.println("</font>");
      out.println("</td></tr>");
   }
   out.println("</table><br>");

   out.println("<br><br>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"link\" action=\"javascript:self.print()\">");
   out.println("<input type=\"image\" src=\"/" +rev+ "/images/print.gif\" alt=\"Print\">");
   out.println("</form></font>");


   if (index.equals( "888" )) {         // if came from Hotel_search (doPost)

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchm.htm\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
      out.println("</form></font>");

   } else {

      if (index.equals( "999" )) {         // if came from Hotel_search (doGet)

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_searchmy.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
         out.println("</form></font>");

      } else {                                // return to Hotel_sheet - must rebuild frames first

         out.println("<font size=\"2\">");
         out.println("<form action=\"Hotel_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; \">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("</form></font>");
      }
   }
   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
 }

 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out, String p1, String p2, String p3, String p4, String p5) {


   if (p1.equals( "" )) {

      p1 = " ";             // use space instead of null
   }

   if (p2.equals( "" )) {

      p2 = " ";             // use space instead of null
   }

   if (p3.equals( "" )) {

      p3 = " ";             // use space instead of null
   }

   if (p4.equals( "" )) {

      p4 = " ";             // use space instead of null
   }

   if (p5.equals( "" )) {

      p5 = " ";             // use space instead of null
   }

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
   out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "',&nbsp;&nbsp;&nbsp;'" + p2 + "',&nbsp;&nbsp;&nbsp;'" + p3 + "',&nbsp;&nbsp;&nbsp;'" + p4 + "',&nbsp;&nbsp;&nbsp;'" + p5 + "'");
   out.println("<BR><BR>");
   out.println("Please check the names and try again.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   return;
 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1, String msg) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>Process: " + msg + "<br>  Exception: " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

 }

}
