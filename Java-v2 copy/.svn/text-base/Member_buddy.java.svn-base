/***************************************************************************************
 *   Member_buddy:  This servlet will allow the member to maintain a buddly list to be
 *                  used when reserving a tee time.
 *
 *
 *   called by:  member_main.htm and self
 *
 *   created: 1/11/2002   Bob P.
 *
 *   last updated:
 *
 *       12/09/09   Added redirect to Member_partner and commented out code following redirect.  Member_buddy no longer used!
 *       10/04/09   Added activity isolation to the buddy list
 *        3/10/09   Display the letter names in last, first mi order.
 *        4/06/07   Do not include members that are inactive (new inact flag in member2b).
 *       12/09/04   Ver 5 - Change Member Name Alphabit table to common table.
 *       10/06/04   Enhancements for Version 5 - allow for sub-menus.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.alphaTable;
import com.foretees.common.getActivity;


public class Member_buddy extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //
 // Process the initial call from member_main.htm
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }

 //
 //   Process form request from Member_buddy HTML page
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   // Redirect members to Member_partner, Member_buddy no longer used!
   out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_partner\">");
   return;

   /*
   Connection con = null;                 // init DB objects
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\">");
      out.println("<CENTER>");
      out.println("<BR><BR><H2>Database Connection Error</H2>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }


   String user = (String)session.getAttribute("user");   // get username
   String caller = (String)session.getAttribute("caller");

   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
   String activity_name = "Golf";
   if (sess_activity_id != 0) activity_name = getActivity.getActivityName(sess_activity_id, con);
   
   String buser = "";
   String puser = "";
   String buddy1 = "";
   String buddy2 = "";
   String buddy3 = "";
   String buddy4 = "";
   String buddy5 = "";
   String buddy6 = "";
   String buddy7 = "";
   String buddy8 = "";
   String buddy9 = "";
   String buddy10 = "";
   String buddy11 = "";
   String buddy12 = "";
   String buddy13 = "";
   String buddy14 = "";
   String buddy15 = "";
   String buddy16 = "";
   String buddy17 = "";
   String buddy18 = "";
   String buddy19 = "";
   String buddy20 = "";
   String buddy21 = "";
   String buddy22 = "";
   String buddy23 = "";
   String buddy24 = "";
   String buddy25 = "";

   String buddies [] = new String [25];  // string array to hold buddy names

   String fname [] = new String [25];    // string arrays to hold parsed names
   String lname [] = new String [25];
   String mi [] = new String [25];

   String bcw [] = new String [25];  // string array to hold buddy walk/cart pref's
   String users [] = new String [25];  // string array to hold buddy usernames

   int count = 0;
   int i = 0;
   int i2 = 0;

   //
   //  See if call is for doGet (initial) or doPost (submit request)
   //
   if (req.getParameter("user") == null) {  // if doGet

      //
      //  Get the current buddy list for this user
      //
      try {

         PreparedStatement pstmt = con.prepareStatement (
                  "SELECT * FROM buddy WHERE username = ? AND activity_id = ?");

         pstmt.clearParameters();            // clear the parms
         pstmt.setString(1, user);           // put username in statement
         pstmt.setInt(2, sess_activity_id);
         rs = pstmt.executeQuery();          // execute the prepared stmt

         if (rs.next()) {

            buddy1 = rs.getString("buddy1");
            buddy2 = rs.getString("buddy2");
            buddy3 = rs.getString("buddy3");
            buddy4 = rs.getString("buddy4");
            buddy5 = rs.getString("buddy5");
            buddy6 = rs.getString("buddy6");
            buddy7 = rs.getString("buddy7");
            buddy8 = rs.getString("buddy8");
            buddy9 = rs.getString("buddy9");
            buddy10 = rs.getString("buddy10");
            buddy11 = rs.getString("buddy11");
            buddy12 = rs.getString("buddy12");
            buddy13 = rs.getString("buddy13");
            buddy14 = rs.getString("buddy14");
            buddy15 = rs.getString("buddy15");
            buddy16 = rs.getString("buddy16");
            buddy17 = rs.getString("buddy17");
            buddy18 = rs.getString("buddy18");
            buddy19 = rs.getString("buddy19");
            buddy20 = rs.getString("buddy20");
            buddy21 = rs.getString("buddy21");
            buddy22 = rs.getString("buddy22");
            buddy23 = rs.getString("buddy23");
            buddy24 = rs.getString("buddy24");
            buddy25 = rs.getString("buddy25");

         } else {

            //
            //  No buddy list for this user - add one
            //
            PreparedStatement stmt = con.prepareStatement (
                     "INSERT INTO buddy (username, activity_id, buddy1, buddy2, buddy3, buddy4, buddy5, buddy6, " +
                        "buddy7, buddy8, buddy9, buddy10, buddy11, buddy12, buddy13, buddy14, buddy15, " +
                        "buddy16, buddy17, buddy18, buddy19, buddy20, buddy21, buddy22, buddy23, buddy24, " +
                        "buddy25) " +
                        "VALUES (?, ?, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '')");

            stmt.clearParameters();               // clear the parms
            stmt.setString(1, user);              // put the parm in stmt
            stmt.setInt(2, sess_activity_id);
            count = stmt.executeUpdate();         // execute the prepared stmt

            stmt.close();

         }

         pstmt.close();

      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\">");
         out.println("<CENTER>");
         out.println("<BR><BR><H2>Database Access Error 1</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club manager.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

   } else {   // doPost request

      //
      // Get all parameters entered
      //
      puser = req.getParameter("user");
      buddy1 = req.getParameter("buddy1");
      buddy2 = req.getParameter("buddy2");
      buddy3 = req.getParameter("buddy3");
      buddy4 = req.getParameter("buddy4");
      buddy5 = req.getParameter("buddy5");
      buddy6 = req.getParameter("buddy6");
      buddy7 = req.getParameter("buddy7");
      buddy8 = req.getParameter("buddy8");
      buddy9 = req.getParameter("buddy9");
      buddy10 = req.getParameter("buddy10");
      buddy11 = req.getParameter("buddy11");
      buddy12 = req.getParameter("buddy12");
      buddy13 = req.getParameter("buddy13");
      buddy14 = req.getParameter("buddy14");
      buddy15 = req.getParameter("buddy15");
      buddy16 = req.getParameter("buddy16");
      buddy17 = req.getParameter("buddy17");
      buddy18 = req.getParameter("buddy18");
      buddy19 = req.getParameter("buddy19");
      buddy20 = req.getParameter("buddy20");
      buddy21 = req.getParameter("buddy21");
      buddy22 = req.getParameter("buddy22");
      buddy23 = req.getParameter("buddy23");
      buddy24 = req.getParameter("buddy24");
      buddy25 = req.getParameter("buddy25");

      buddies [0] = buddy1;
      buddies [1] = buddy2;
      buddies [2] = buddy3;
      buddies [3] = buddy4;
      buddies [4] = buddy5;
      buddies [5] = buddy6;
      buddies [6] = buddy7;
      buddies [7] = buddy8;
      buddies [8] = buddy9;
      buddies [9] = buddy10;
      buddies [10] = buddy11;
      buddies [11] = buddy12;
      buddies [12] = buddy13;
      buddies [13] = buddy14;
      buddies [14] = buddy15;
      buddies [15] = buddy16;
      buddies [16] = buddy17;
      buddies [17] = buddy18;
      buddies [18] = buddy19;
      buddies [19] = buddy20;
      buddies [20] = buddy21;
      buddies [21] = buddy22;
      buddies [22] = buddy23;
      buddies [23] = buddy24;
      buddies [24] = buddy25;

      for (i = 0; i < 25; i++) {

         bcw[i] = "";
         users[i] = "";
      }

      //
      //  make sure we have the same user
      //
      if (!puser.equalsIgnoreCase( user )) {

         out.println(SystemUtils.HeadTitle("Program Error"));
         out.println("<BODY bgcolor=\"#ccccaa\">");
         out.println("<CENTER>");
         out.println("<BR><BR><H2>Program Error 101</H2>");
         out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club manager.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }      // end of IF doGet

   // ********************************************************************************
   //   If we got control from user clicking on a letter in the Member List or the initial req,
   //   then we must build the page again using the new parms.
   // ********************************************************************************
   if (req.getParameter("SubmitForm") == null) {     // if user clicked on a name letter or its the initial req (doGet)

      out.println(SystemUtils.HeadTitle2("Member Partner Page"));

      //
      //*******************************************************************
      //  User clicked on a letter - submit the form for the letter
      //*******************************************************************
      //
      out.println("<script type=\"text/javascript\">");            // Submit the form when clicking on a letter
      out.println("<!--");
      out.println("function subletter(x) {");

   //      out.println("alert(x);");
      out.println("document.forms['buddyform'].letter.value = x;");         // put the letter in the parm
      out.println("document.forms['buddyform'].submit();");        // submit the form
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*********************************************************************************
      //
      out.println("<script type=\"text/javascript\">");            // Move name script
      out.println("<!--");

      out.println("function movebuddy(name) {");

      out.println("skip = 0;");
      out.println("found = 0;");
      out.println("var f = document.forms['buddyform'];");

      for (i = 1; i < 26; i++) {

         out.println("var buddy" +i+ " = f.buddy" +i+ ".value;");

         out.println("if (name == buddy" +i+ ") {");
            out.println("skip = 1;");
         out.println("}");
      }

      out.println("if (skip == 0) {");

         for (i = 1; i < 26; i++) {

            out.println("if (buddy" +i+ " == '' && found == 0) {");     // if buddy'n' is empty
               out.println("f.buddy" +i+ ".value = name;");
               out.println("found = 1;");
            out.println("}");
         }

      out.println("}");                  // end of IF skip

      out.println("}");                  // end
      out.println("// -->");
      out.println("</script>");                               // End of script

      //
      //*********************************************************************************
      //  Erase buddy name (erase button selected next to player's name)
      //
      //    Remove the player's name and shift any other names up starting at buddy1
      //*********************************************************************************
      //
      out.println("<script type=\"text/javascript\">");            // Erase name script
      out.println("<!--");

      out.println("function erasename(pos1) {");

      out.println("eval(\"document.forms['buddyform'].\" +pos1+ \".value = '';\")");  // clear the name field
      out.println("found = 0;");

      for (i = 1; i < 26; i++) {

         out.println("var buddy" +i+ " = document.forms['buddyform'].buddy" +i+ ".value;");
      }

      for (i = 1; i < 25; i++) {                                  // do all but last

         out.println("if (buddy" +i+ " == '') {");                // if buddy'n' is empty

            out.println("found = 0;");

            for (i2 = i+1; i2 < 26; i2++) {

               out.println("if (buddy" +i2+ " != '' && found == 0) {");     // if buddy'n2' is not empty
                  out.println("document.forms['buddyform'].buddy" +i+ ".value = buddy" +i2+ ";");
                  out.println("document.forms['buddyform'].buddy" +i2+ ".value = '';");
                  out.println("found = 1;");
               out.println("}");
            }
              
            for (i2 = i+1; i2 < 26; i2++) {

               out.println("var buddy" +i2+ " = document.forms['buddyform'].buddy" +i2+ ".value;");
            }
         out.println("}");
      }

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      out.println("</HEAD>");
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
         out.println("<table border=\"0\" valign=\"top\">");       // table for main page
         out.println("<tr><td align=\"center\" valign=\"top\" width=\"85%\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<p align=\"center\">Use this page to maintain your Partner List,");
            out.println("which can be used when you are making reservations.</p>");

            out.println("<table cellpadding=\"5\" bgcolor=\"#f5f5dc\" border=\"1\"><tr><td><font size=\"2\">");
            out.println("Add/change your favorite " + activity_name.toLowerCase() + " partners' names below.  Names <b>must</b> be entered exactly as they exist in the system.");
            out.println("<br>Use the Member List table to locate a member for proper spelling and format.");
            out.println("</font></td></tr></table><br>");
         out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\" valign=\"top\">");

         out.println("<table border=\"0\" align=\"center\" valign=\"top\" cellspacing=\"8\">");  // table to contain 3 tables below

            out.println("<tr>");
            out.println("<form action=\"Member_buddy\" name=\"buddyform\" id=\"buddyform\" method=\"post\" target=\"bot\">");
            out.println("<td valign=\"top\">");

               out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" valign=\"top\">"); // table 1 for buddy list
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<tr bgcolor=\"#336633\"><td align=\"center\" width=\"260\">");
                  out.println("<font color=\"#ffffff\" size=\"2\">");
                  out.println("<p><b>Partner List</b></p>");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy1')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 1&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy1\" id=\"buddy1\" size=\"16\" maxlength=\"43\" value=\"" + buddy1 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy2')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 2&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy2\" id=\"buddy2\" size=\"16\" maxlength=\"43\" value=\"" + buddy2 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy3')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 3&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy3\" id=\"buddy3\" size=\"16\" maxlength=\"43\" value=\"" + buddy3 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy4')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 4&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy4\" id=\"buddy4\" size=\"16\" maxlength=\"43\" value=\"" + buddy4 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy5')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 5&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy5\" id=\"buddy5\" size=\"16\" maxlength=\"43\" value=\"" + buddy5 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy6')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 6&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy6\" id=\"buddy6\" size=\"16\" maxlength=\"43\" value=\"" + buddy6 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy7')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 7&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy7\" id=\"buddy7\" size=\"16\" maxlength=\"43\" value=\"" + buddy7 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy8')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 8&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy8\" id=\"buddy8\" size=\"16\" maxlength=\"43\" value=\"" + buddy8 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy9')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 9&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy9\" id=\"buddy9\" size=\"16\" maxlength=\"43\" value=\"" + buddy9 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy10')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 10&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy10\" id=\"buddy10\" size=\"16\" maxlength=\"43\" value=\"" + buddy10 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy11')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 11&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy11\" id=\"buddy11\" size=\"16\" maxlength=\"43\" value=\"" + buddy11 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy12')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 12&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy12\" id=\"buddy12\" size=\"16\" maxlength=\"43\" value=\"" + buddy12 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy13')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 13&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy13\" id=\"buddy13\" size=\"16\" maxlength=\"43\" value=\"" + buddy13 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy14')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 14&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy14\" id=\"buddy14\" size=\"16\" maxlength=\"43\" value=\"" + buddy14 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy15')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 15&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy15\" id=\"buddy15\" size=\"16\" maxlength=\"43\" value=\"" + buddy15 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy16')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 16&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy16\" id=\"buddy16\" size=\"16\" maxlength=\"43\" value=\"" + buddy16 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy17')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 17&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy17\" id=\"buddy17\" size=\"16\" maxlength=\"43\" value=\"" + buddy17 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy18')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 18&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy18\" id=\"buddy18\" size=\"16\" maxlength=\"43\" value=\"" + buddy18 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy19')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 19&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy19\" id=\"buddy19\" size=\"16\" maxlength=\"43\" value=\"" + buddy19 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy20')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 20&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy20\" id=\"buddy20\" size=\"16\" maxlength=\"43\" value=\"" + buddy20 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy21')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 21&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy21\" id=\"buddy21\" size=\"16\" maxlength=\"43\" value=\"" + buddy21 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy22')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 22&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy22\" id=\"buddy22\" size=\"16\" maxlength=\"43\" value=\"" + buddy22 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy23')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 23&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy23\" id=\"buddy23\" size=\"16\" maxlength=\"43\" value=\"" + buddy23 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy24')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 24&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy24\" id=\"buddy24\" size=\"16\" maxlength=\"43\" value=\"" + buddy24 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

               out.println("<tr><td align=\"center\" nowrap>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('buddy25')\" style=\"cursor:hand\">");
                  out.println("<font size=\"2\">&nbsp;&nbsp;Partner 25&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"buddy25\" id=\"buddy25\" size=\"16\" maxlength=\"43\" value=\"" + buddy25 +"\">");
                  out.println("");
                  out.println("</font></td><tr>");

            out.println("</font></table>");

         out.println("</td>");

      String letter = "";
        
      if (req.getParameter("letter") != null) {     // if user clicked on a name letter

         letter = req.getParameter("letter");

         out.println("<td align=\"center\" valign=\"top\">");

         letter = letter + "%";

         String first = "";
         String mid = "";
         String last = "";
         String name = "";
         String dname = "";

         try {

            PreparedStatement stmt2 = con.prepareStatement (
                     "SELECT name_last, name_first, name_mi FROM member2b " +
                     "WHERE name_last LIKE ? AND inact = 0 ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();            // execute the prepared stmt

            out.println("<table border=\"1\" width=\"140\" bgcolor=\"#f5f5dc\" valign=\"top\">");      // name list
            out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
                  out.println("<font color=\"#ffffff\" size=\"2\">");
                  out.println("<b>Name List</b>");
                  out.println("</font></td>");
            out.println("</tr><tr>");
            out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("Click on name to add");
               out.println("</font></td></tr>");
               out.println("<tr><td align=\"left\"><font size=\"2\">");
               out.println("<select size=\"16\" name=\"bname\" onClick=\"movebuddy(this.form.bname.value)\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;

               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               out.println("<option value=\"" + name + "\">" + dname + "</option>");
            }

            out.println("</select>");
            out.println("</font></td></tr></table>");

            stmt2.close();
         }
         catch (Exception exc) {

            out.println("<table border=\"0\">");          // empty table for table separation
            out.println("<tr><td>");
            out.println("</td></tr></table>");
         }

         out.println("</td>");
      }                        // end of IF letter

      out.println("<td valign=\"top\" align=\"left\" valign=\"top\">");

      //
      //   Output the Alphabit Table for Members' Last Names
      //
      alphaTable.getTable(out, "buddy");

      out.println("<br><br>");

      out.println("<p align=\"center\"><font size=\"2\">");
      out.println("<input type=\"hidden\" name=\"user\" value = " + user + ">");
      out.println("<input type=\"submit\" name=\"SubmitForm\" value=\"Submit\">");

      out.println("</form><br><form method=\"get\" action=\"Member_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
      out.println("</form></p>");

      out.println("</td>");
      out.println("</tr>");
      out.println("</table>");              // end of large table containg 3 smaller tables
      out.println("</font></td>");
      out.println("</tr></table>");         // end of table for main page

      out.println("</center></font></body></html>");
      out.close();

      //
      // ********************************************************************************
      //  end of 'letter' processing - else do 'submit' processing
      // ********************************************************************************
      //

   } else {    // not 'letter' parm, do 'submit'

      boolean self = false;

      try {
         PreparedStatement stmt = con.prepareStatement (
               "SELECT username, wc FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ? AND inact = 0");

         //
         //  Verify each name received against names in the member table
         //
         for (i = 0; i < 25; i++) {

            if (!buddies[i].equals( "" )) {

               //
               //  Parse the name to separate first, last & mi
               //
               StringTokenizer tok = new StringTokenizer( buddies[i] );     // space is the default token

               if (( tok.countTokens() > 3 ) || ( tok.countTokens() < 2 )) {    // must be 2 or 3 fields

                  invData(out, buddies[i]);                        // reject
                  return;
               }

               if ( tok.countTokens() == 2 ) {         // first name, last name

                  fname[i] = tok.nextToken();
                  lname[i] = tok.nextToken();
                  mi[i] = "";
               }

               if ( tok.countTokens() == 3 ) {         // first name, mi, last name

                  fname[i] = tok.nextToken();
                  mi[i] = tok.nextToken();
                  lname[i] = tok.nextToken();
               }

               buser = "";

               //
               //  search member table for matching name
               //
               try {
                  stmt.clearParameters();            // clear the parms
                  stmt.setString(1, lname[i]);
                  stmt.setString(2, fname[i]);
                  stmt.setString(3, mi[i]);
                  rs = stmt.executeQuery();          // execute the prepared stmt

                  if (rs.next()) {

                     buser = rs.getString(1);
                     bcw[i] = rs.getString(2);              // walk/cart preference

                     if (buser.equals( "" )) {      // if no match found - reject

                        invData(out, buddies[i]);
                        return;
                     }
                     if (buser.equalsIgnoreCase( user )) {      // did user select self?

                        self = true;                  // indicate so in message
                          
                     } else {

                        users[i] = buser;             // save buddy's username
                     }

                  } else {

                     invData(out, buddies[i]);
                     return;
                  }
               }
               catch (Exception exc) {             // SQL Error - name not found

                  invData(out, buddies[i]);
                  return;
               }
            }

         }   // end of for loop

         stmt.close();

      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\">");
         out.println("<CENTER>");
         out.println("<BR><BR><H2>Database Access Error 2</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club manager.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  If we got this far, then the names are okay - update the buddy table for this user
      //
      try {

         PreparedStatement pstmt = con.prepareStatement (
               "UPDATE buddy SET buddy1 = ?, buddy2 = ?, buddy3 = ?, buddy4 = ?, buddy5 = ?, " +
                   "buddy6 = ?, buddy7 = ?, buddy8 = ?, buddy9 = ?, buddy10 = ?, buddy11 = ?, buddy12 = ?, " +
                   "buddy13 = ?, buddy14 = ?, buddy15 = ?, buddy16 = ?, buddy17 = ?, buddy18 = ?, buddy19 = ?, " +
                   "buddy20 = ?, buddy21 = ?, buddy22 = ?, buddy23 = ?, buddy24 = ?, buddy25 = ?, " +
                   "b1cw = ?, b2cw = ?, b3cw = ?, b4cw = ?, b5cw = ?, b6cw = ?, b7cw = ?, b8cw = ?, b9cw = ?, " +
                   "b10cw = ?, b11cw = ?, b12cw = ?, b13cw = ?, b14cw = ?, b15cw = ?, b16cw = ?, b17cw = ?, " +
                   "b18cw = ?, b19cw = ?, b20cw = ?, b21cw = ?, b22cw = ?, b23cw = ?, b24cw = ?, b25cw = ?, " +
                   "user1 = ?, user2 = ?, user3 = ?, user4 = ?, user5 = ?, " +
                   "user6 = ?, user7 = ?, user8 = ?, user9 = ?, user10 = ?, user11 = ?, user12 = ?, " +
                   "user13 = ?, user14 = ?, user15 = ?, user16 = ?, user17 = ?, user18 = ?, user19 = ?, " +
                   "user20 = ?, user21 = ?, user22 = ?, user23 = ?, user24 = ?, user25 = ? " +
                   "WHERE username = ? AND activity_id = ?");


         pstmt.clearParameters();            // clear the parms
         pstmt.setString(1, buddy1);
         pstmt.setString(2, buddy2);
         pstmt.setString(3, buddy3);
         pstmt.setString(4, buddy4);
         pstmt.setString(5, buddy5);
         pstmt.setString(6, buddy6);
         pstmt.setString(7, buddy7);
         pstmt.setString(8, buddy8);
         pstmt.setString(9, buddy9);
         pstmt.setString(10, buddy10);
         pstmt.setString(11, buddy11);
         pstmt.setString(12, buddy12);
         pstmt.setString(13, buddy13);
         pstmt.setString(14, buddy14);
         pstmt.setString(15, buddy15);
         pstmt.setString(16, buddy16);
         pstmt.setString(17, buddy17);
         pstmt.setString(18, buddy18);
         pstmt.setString(19, buddy19);
         pstmt.setString(20, buddy20);
         pstmt.setString(21, buddy21);
         pstmt.setString(22, buddy22);
         pstmt.setString(23, buddy23);
         pstmt.setString(24, buddy24);
         pstmt.setString(25, buddy25);
         pstmt.setString(26, bcw[0]);
         pstmt.setString(27, bcw[1]);
         pstmt.setString(28, bcw[2]);
         pstmt.setString(29, bcw[3]);
         pstmt.setString(30, bcw[4]);
         pstmt.setString(31, bcw[5]);
         pstmt.setString(32, bcw[6]);
         pstmt.setString(33, bcw[7]);
         pstmt.setString(34, bcw[8]);
         pstmt.setString(35, bcw[9]);
         pstmt.setString(36, bcw[10]);
         pstmt.setString(37, bcw[11]);
         pstmt.setString(38, bcw[12]);
         pstmt.setString(39, bcw[13]);
         pstmt.setString(40, bcw[14]);
         pstmt.setString(41, bcw[15]);
         pstmt.setString(42, bcw[16]);
         pstmt.setString(43, bcw[17]);
         pstmt.setString(44, bcw[18]);
         pstmt.setString(45, bcw[19]);
         pstmt.setString(46, bcw[20]);
         pstmt.setString(47, bcw[21]);
         pstmt.setString(48, bcw[22]);
         pstmt.setString(49, bcw[23]);
         pstmt.setString(50, bcw[24]);
         pstmt.setString(51, users[0]);
         pstmt.setString(52, users[1]);
         pstmt.setString(53, users[2]);
         pstmt.setString(54, users[3]);
         pstmt.setString(55, users[4]);
         pstmt.setString(56, users[5]);
         pstmt.setString(57, users[6]);
         pstmt.setString(58, users[7]);
         pstmt.setString(59, users[8]);
         pstmt.setString(60, users[9]);
         pstmt.setString(61, users[10]);
         pstmt.setString(62, users[11]);
         pstmt.setString(63, users[12]);
         pstmt.setString(64, users[13]);
         pstmt.setString(65, users[14]);
         pstmt.setString(66, users[15]);
         pstmt.setString(67, users[16]);
         pstmt.setString(68, users[17]);
         pstmt.setString(69, users[18]);
         pstmt.setString(70, users[19]);
         pstmt.setString(71, users[20]);
         pstmt.setString(72, users[21]);
         pstmt.setString(73, users[22]);
         pstmt.setString(74, users[23]);
         pstmt.setString(75, users[24]);
           
         pstmt.setString(76, user);           // put username in statement
         pstmt.setInt(77, sess_activity_id);
         count = pstmt.executeUpdate();  // execute the prepared pstmt

         pstmt.close();

      }
      catch (Exception exc) {             // SQL Error

            out.println(SystemUtils.HeadTitle("DB Access Error"));
            out.println("<BODY bgcolor=\"#ccccaa\">");
            out.println("<CENTER>");
            out.println("<BR><BR><H2>Database Access Error 2</H2>");
            out.println("<BR><BR>Unable to process database change at this time.");
            out.println("<BR>Exception: "+ exc.getMessage());
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
      }

      //
      //  Inform user that buddy list has been updated
      //
      out.println(SystemUtils.HeadTitle("Member Partners Changed"));
      out.println("<BODY bgcolor=\"#ccccaa\">");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<CENTER>");
      out.println("<BR><BR><H3>Thank you, your Partner List Has Been Updated</H3>");
      out.println("<BR><BR>You can use these names when making a reservation.");
      if (self == true) {
         out.println("<BR><BR><b>NOTE:</b>  You selected yourself as a Partner.");
         out.println("<BR>If this was not intentional, you can remove your name from the Partner List.");
      }
      out.println("<BR><BR>");
      out.println("<form method=\"get\" action=\"Member_buddy\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();

   }   // end of if 'letter' parm

   //
   // Done - return.......
   //
   */
 }


 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out, String buddy) {

   out.println(SystemUtils.HeadTitle("Data Entry Error"));
   out.println("<BODY bgcolor=\"#ccccaa\">");
   out.println("<CENTER>");
   out.println("<BR><BR><H2>Data Entry Error</H2>");
   out.println("<BR><BR>Unable to locate " + buddy + " in the database.");
   out.println("<BR>Please check your names and try again.");
   out.println("<BR><BR>If problem persists, contact your club manager.");
   out.println("<BR><BR>");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
 }
}
