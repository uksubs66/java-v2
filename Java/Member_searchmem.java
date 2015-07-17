/***************************************************************************************
 *   Member_searchmem:  This servlet will process the 'search for member' request from
 *                      Member's searchmain page.  It will use the name provided to search
 *                      teecurr for any matches (current tee times).
 *
 *                      Caller only provides the last name, or a portion of it.
 *
 *
 *   called by:  Member_maintop
 *
 *   created: 1/10/2002   Bob P.
 *
 *   last updated:
 *
 *       11/20/09   Black Diamond Ranch - they changed their minds after members complained.
 *       11/11/09   Black Diamond Ranch - disable this feature - they don't want members to see pre-booked times.
 *       11/11/09   Limit the search to member times - do not include guests (so members cannot search for guest times).
 *       10/29/09   Add checks and processing for activities.
 *        9/03/09   Do not show a tee time that was pre-booked while part of a lottery - hide from members (case 1703).
 *       10/03/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        9/02/08   Javascript compatability updates
 *        9/25/06   Enhancements for TLT version - Add SystemLingo support
 *        9/25/06   commented out noMem method - unused
 *        3/30/06   Forest Highlands - allow this feature even though club does not wish to display member names.
 *        2/28/06   Merion - do not show member and guest names whenever there is a guest in the tee time.
 *        1/30/06   Do not allow this feature if club does not wish to display member names.
 *        1/24/05   Ver 5 - change club2 to club5.
 *       10/06/04   Ver 5 - add sub-menu support.  Add doGet processing for call from menu.
 *        1/13/04   JAG Modifications to match new color scheme.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add lottery processing.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.client.SystemLingo;
import com.foretees.common.getActivity;


public class Member_searchmem extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //****************************************************
 // Process the call from Member_maintop (menu call)
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   //Statement stmt = null;
   //ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   String club = (String)session.getAttribute("club");              // get club name
   String caller = (String)session.getAttribute("caller");        

   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   
   int hideNames = 0;


   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   try {

      //
      // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm, sess_activity_id);        // get the club parms

      hideNames = parm.hiden;

   }
   catch (Exception exc) {
   }

  // if ((hideNames == 0 || club.equals( "foresthighlands" )) && !club.equals( "blackdiamondranch" )) {
   if (hideNames == 0 || club.equals( "foresthighlands" )) {

      //
      //  Output a page to prompt user for the name to search for
      //
      out.println(SystemUtils.HeadTitle2("Member Search"));
      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function cursor() { document.forms['f'].name.focus(); }");
      out.println("// -->");
      out.println("</script>");
      out.println("</head>");
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" onload=cursor()>");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<BR><table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"ffffff\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      
      if (sess_activity_id == 0) {    // if this user is under Golf
      
         out.println("<p>To locate the current tee times for an individual or group, enter the name,<br>");
         out.println("or any portion of the name, as it may exist on the tee sheets.<br>");
         
      } else {      // user under an activity other than golf
         
         out.println("<p>To locate the current reservations for an individual or group, enter the name,<br>");
         out.println("or any portion of the name, as it may exist in the reservations.<br>");
      }
         
      out.println("This will search for all names that contain the value you enter.</p>");
      out.println("</font>");
      out.println("</td></tr></table>");

      out.println("<font size=\"2\" face=\"Courier New\">");
      out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
      out.println("<br></font>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<form action=\"/v5/servlet/Member_searchmem\" method=\"post\" target=\"bot\" name=\"f\">");

      out.println("<input type=\"hidden\" name=\"source\" value=\"main\">");

      out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr>");
            out.println("<td valign=\"top\" align=\"center\">");

               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\">");
                  out.println("<tr><td width=\"250\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\"><br>Name: &nbsp;");
                        out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                        out.println("</input>");
                     out.println("<br><br>");
                     out.println("<input type=\"submit\" value=\"Search\" name=\"search\">");
                     out.println("</p>");
                     out.println("</font>");
                  out.println("</td></tr>");
               out.println("</table>");

            out.println("</td>");

            out.println("<td width=\"30\">");
               out.println("<p>&nbsp;</p>");
            out.println("</td>");

            out.println("<td>");
               out.println("<table border=\"2\" align=\"center\" bgcolor=\"#f5f5dc\">");
                  out.println("<tr>");
                     out.println("<td colspan=\"6\" align=\"center\" bgcolor=\"#336633\">");
                        out.println("<font color=\"ffffff\"  size=\"2\">");
                        out.println("<b>Member List</b>");
                        out.println("</font>");
                     out.println("</td>");
                  out.println("</tr>");
                  out.println("<tr>");
                     out.println("<td colspan=\"6\" align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("Name begins with:");
                        out.println("</font>");
                     out.println("</td>");
                  out.println("</tr>");
                  out.println("<tr>");
                     out.println("<td align=\"center\"><font size=\"1\">");
                        out.println("<input type=\"submit\" value=\"A\" name=\"letter\"></font></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"B\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"C\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"D\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"E\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"F\" name=\"letter\"></td>");
                  out.println("</tr>");

                  out.println("<tr>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"G\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"H\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"I\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"J\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"K\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"L\" name=\"letter\"></td>");
                  out.println("</tr>");

                  out.println("<tr>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"M\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"N\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"O\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"P\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"Q\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"R\" name=\"letter\"></td>");
                  out.println("</tr>");

                  out.println("<tr>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"S\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"T\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"U\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"V\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"W\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"X\" name=\"letter\"></td>");
                  out.println("</tr>");

                  out.println("<tr>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"Y\" name=\"letter\"></td>");
                     out.println("<td align=\"center\">");
                        out.println("<input type=\"submit\" value=\"Z\" name=\"letter\"></td>");
                     out.println("<td align=\"center\"></td>");
                     out.println("<td align=\"center\"></td>");
                     out.println("<td align=\"center\"></td>");
                     out.println("<td align=\"center\"></td>");
                  out.println("</tr>");
               out.println("</table>");
            out.println("</td>");
         out.println("</tr>");
      out.println("</table>");
      out.println("</form>");
      out.println("</font>");

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/v5/servlet/Member_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
      out.println("</input></form></font>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");
      out.close();

   } else {
     
      //
      //  Club does not want member names displayed.  Therefore we cannot allow members to search
      //  for other members' tee times.
      //
      out.println(SystemUtils.HeadTitle("Member Search"));
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<font size=\"2\"><BR><BR>");
      out.println("<p>Sorry, but your club does not support this feature.</p><br>");
      out.println("<form method=\"get\" action=\"/v5/servlet/Member_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
      out.println("</input></form></font>");

      out.println("</center></font></body></html>");
      out.close();
   }
  
 }


 //
 //****************************************************
 // Process the form request from member_searchmain page
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   String club = (String)session.getAttribute("club");              // get club name
   String user = (String)session.getAttribute("user");
   String caller = (String)session.getAttribute("caller");

   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   
   String omit = "";
   String time_zone = "";
   String ampm = "";
   String day = "";
   String lname = "";
   String submit = "";
   String sfb = "";
   String course = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String lotteryText = "";
   String lotteryName = "";

   String rest = "";

   long date = 0;
   long lottid = 0;

   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int count = 0;
   int fb = 0;
   int multi = 0;
   int lottery = 0;
   int fives = 0;
   int fivesomes = 0;
   int slots = 0;
   int lstate = 0;
   //int advance_days = 0;
   //int sdays = 0;
   //int sdtime = 0;
   //int edays = 0;
   //int edtime = 0;
   //int pdays = 0;
   //int ptime = 0;
   int groups = 0;
   int playerCount = 0;
   int maxPlayers = 0;

   boolean available = false;

   //
   // Process request according to which 'submit' button was selected
   //
   //      'search' - a search request
   //      'letter' - a request to list member names (= A - Z)
   //      'name'  -  the name, or partial name, to search for
   //
   // Get the parameters entered
   //
   if (req.getParameter("letter") != null) {     // if user clicked on a name letter

      String letter = req.getParameter("letter");      // get the letter
      letter = letter + "%";

      String first = "";
      String mid = "";
      String last = "";
      String name = "";
      String wname = "";
      String dname = "";

      //
      //   letter call - display the search page with a table to display member names
      //
      out.println(SystemUtils.HeadTitle2("Member Search"));
      out.println("<script type=\"text/javascript\">");
      out.println("<!-- ");
      out.println("function cursor() { document.forms['f'].name.focus(); }");
      out.println("function movename(name) {");
      out.println(" document.forms['f'].name.value = name;");            // put name selected into the search form
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script

      out.println("</head>");

      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" onload=cursor()>");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");

      out.println("<BR><table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#ffffff\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      
      if (sess_activity_id == 0) {    // if this user is under Golf
      
         out.println("<p>To locate the current tee times for an individual or group, enter the name,<br>");
         out.println("or any portion of the name, as it may exist on the tee sheets.<br>");
         
      } else {      // user under an activity other than golf
         
         out.println("<p>To locate the current reservations for an individual or group, enter the name,<br>");
         out.println("or any portion of the name, as it may exist in the reservations.<br>");
      }
         
      out.println("This will search for all names that contain the value you enter.</p>");
      out.println("</font>");
      out.println("</td></tr></table>");

      out.println("<font size=\"2\" face=\"Courier New\">");
      out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
      out.println("<br></font>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table for 3 smaller tables

      out.println("<form action=\"/" +rev+ "/servlet/Member_searchmem\" method=\"post\" name=\"f\">");

      out.println("<tr><td valign=\"top\" align=\"center\">");

         out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\">");
            out.println("<tr><td width=\"250\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"center\"><br>Name: &nbsp;");
                  out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                  out.println("</input>");
               out.println("<br><br>");
               out.println("<input type=\"submit\" value=\"Search\" name=\"search\">");
               out.println("</p>");
               out.println("</font>");
            out.println("</td></tr>");
         out.println("</table></td>");

      out.println("<td valign=\"top\" align=\"center\">");
      out.println("<table border=\"1\" width=\"140\" bgcolor=\"#f5f5dc\">");      // name list
      out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
            out.println("<font color=\"#ffffff\" size=\"2\">");
            out.println("<b>Name List</b>");
            out.println("</font></td>");
      out.println("</tr><tr>");
      out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("Click on name to add");
         out.println("</font></td></tr>");

      try {

         PreparedStatement stmt2 = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi FROM member2b " +
                  "WHERE name_last LIKE ? ORDER BY name_last, name_first, name_mi");

         stmt2.clearParameters();               // clear the parms
         stmt2.setString(1, letter);            // put the parm in stmt
         rs = stmt2.executeQuery();             // execute the prepared stmt

         out.println("<tr><td align=\"left\"><font size=\"2\">");
         out.println("<select size=\"8\" name=\"bname\" onClick=\"movename(this.form.bname.value)\">");

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
         out.println("</font></td></tr>");

         stmt2.close();
      }
      catch (Exception ignore) {

      }
      out.println("</table>");

      out.println("</td><td valign=\"top\" align=\"center\">");   // end of name list column

            out.println("<table border=\"2\" align=\"center\" bgcolor=\"#f5f5dc\">");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#ffffff\" size=\"2\">");
                     out.println("<b>Member List</b>");
                  out.println("</font></td>");
               out.println("</tr><tr>");
                  out.println("<td colspan=\"6\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("Name begins with:");
                  out.println("</font></td>");
               out.println("</tr><tr>");
                  out.println("<td align=\"center\"><font size=\"1\">");
                     out.println("<input type=\"submit\" value=\"A\" name=\"letter\"></font></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"B\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"C\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"D\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"E\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"F\" name=\"letter\"></td>");
               out.println("</tr><tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"G\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"H\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"I\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"J\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"K\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"L\" name=\"letter\"></td>");
               out.println("</tr><tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"M\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"N\" name=\"letter\"></td>");
                 out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"O\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"P\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Q\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"R\" name=\"letter\"></td>");
               out.println("</tr><tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"S\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"T\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"U\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"V\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"W\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"X\" name=\"letter\"></td>");
               out.println("</tr><tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Y\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Z\" name=\"letter\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
            out.println("</tr></table>");
            out.println("</td></tr></form></table></font>");
         out.println("<font size=\"2\">");

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_searchmem\">");
         out.println("<input type=\"submit\" value=\"Back to Search\" style=\"text-decoration:underline;\">");
         out.println("</input></form>");

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
         out.println("</input></form>");

      out.println("</font></center></body></html>");
      out.close();


   } else {   // not a letter call

      //
      //  Called to search for a name - get the name and search for it
      //
      String name = req.getParameter("name");        //  name or portion of name

      int length = name.length();                    // get length of name requested

      //
      //  verify the required fields
      //
      if ((name.equals( omit )) || (length > 20)) { // why is this capd at 20?

         invData(out);    // inform the user and return
         return;
      }

      //
      // see if 5-somes are supported on any course at this club
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT fives FROM clubparm2");

         while (rs.next()) {

            fives = rs.getInt(1);

            if (fives != 0) {

               fivesomes = 1;          // set 5-somes supported for this club
            }
         }

         stmt.close();

         //
         //  get the multiple course parm for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT multi, lottery, adv_zone FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
            lottery = rs.getInt(2);
            time_zone = rs.getString(3);

         }

         stmt.close();


         //
         //   Add a % to the name provided so search will match anything close
         //
         StringBuffer buf = new StringBuffer("%");
         buf.append( name );
         buf.append("%");
         String sname = buf.toString();
         PreparedStatement pstmt1 = null;
         
         //
         //   build the HTML page for the display
         //
         out.println(SystemUtils.HeadTitle("Member Search Page"));
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
         SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");

         //
         //  Process according to the activity
         //
         if (sess_activity_id == 0) {    // if Golf  
         
            //
            // use the name to search table
            //
            pstmt1 = con.prepareStatement (
               "SELECT * " +
               "FROM teecurr2 " +
               "WHERE (player1 LIKE ? AND username1 != '') OR (player2 LIKE ? AND username2 != '') OR " +
               "(player3 LIKE ? AND username3 != '') OR (player4 LIKE ? AND username4 != '') OR (player5 LIKE ? AND username5 != '') " +
               "ORDER BY date, time");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, sname);
            pstmt1.setString(2, sname);
            pstmt1.setString(3, sname);
            pstmt1.setString(4, sname);
            pstmt1.setString(5, sname);

            rs = pstmt1.executeQuery();      // execute the prepared stmt


            out.println("<font size=\"3\">");
            out.println("<p><b>Search Results for</b> " + name + "</p>");
            out.println("<br><b>Tee Times</b>.");
            out.println("</font><font size=\"2\">");
            out.println("<br><br><b>To join an open tee time</b>:  Just click on the box containing the time (2nd column).");
            out.println("</font><font size=\"1\">");
            out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
            out.println("<br><br></font><font size=\"2\">");

               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\">");
                  out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<tr bgcolor=\"#336633\"><td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                        out.println("</font></td>");

                     if (multi != 0) {

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<u><b>Course Name</b></u>");
                        out.println("</font></td>");
                     }

                     out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                        out.println("</font></td>");

                     if (fivesomes != 0) {

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<u><b>Player 5</b></u>");
                        out.println("</font></td>");
                     }
                     out.println("</tr>");

            //
            //  Get each record and display it
            //
            count = 0;             // number of records found

            while (rs.next()) {

               date = rs.getLong("date");
               mm = rs.getInt("mm");
               dd = rs.getInt("dd");
               yy = rs.getInt("yy");
               day = rs.getString("day");
               hr = rs.getInt("hr");
               min = rs.getInt("min");
               time = rs.getInt("time");
               player1 = rs.getString("player1");
               player2 = rs.getString("player2");
               player3 = rs.getString("player3");
               player4 = rs.getString("player4");
               user1 = rs.getString("username1");
               user2 = rs.getString("username2");
               user3 = rs.getString("username3");
               user4 = rs.getString("username4");
               fb = rs.getInt("fb");
               player5 = rs.getString("player5");
               user5 = rs.getString("username5");
               lotteryName = rs.getString("lottery");
               course = rs.getString("courseName");
               rest = rs.getString("rest5");
               userg1 = rs.getString("userg1");
               userg2 = rs.getString("userg2");
               userg3 = rs.getString("userg3");
               userg4 = rs.getString("userg4");
               userg5 = rs.getString("userg5");

               boolean skip = false;

               if (club.equals( "merion" )) {

                  //
                  //  Merion - do not show tee time whenever there is a guest in the tee time (and its not this member's time)
                  //
                  if (!userg1.equals( "" ) || !userg2.equals( "" ) || !userg3.equals( "" ) || !userg4.equals( "" ) || !userg5.equals( "" )) {

                     skip = true;        // skip this guest time

                     if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3) ||
                         user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

                        skip = false;     // do not skip when member is included
                     }
                  }
               } 

               //
               //  Check for lottery time
               //
               if (!lotteryName.equals("")) {

                  //
                  //  Get the current state of this lottery on the day of this tee time
                  //
                  lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lotteryName, course, con);

                  if (lstate < 5) {

                     skip = true;        // do not allow access to this tee time (pre-booked lottery time)
                  }
               }


               if (skip == false) {

                  count++;

                  //
                  //  check if 5-somes allowed on this course
                  //
                  PreparedStatement pstmt3 = con.prepareStatement (
                     "SELECT fives FROM clubparm2 WHERE courseName = ?");

                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, course);
                  rs2 = pstmt3.executeQuery();      // execute the prepared pstmt3

                  if (rs2.next()) {

                     fives = rs2.getInt(1);
                  }

                  pstmt3.close();

                  ampm = " AM";
                  if (hr == 12) {
                     ampm = " PM";
                  }
                  if (hr > 12) {
                     ampm = " PM";
                     hr = hr - 12;    // convert to conventional time
                  }

                  available = false;

                  if (player1.equals( "" )) {

                     player1 = "&nbsp;";       // make it a space for table display
                     available = true;         // tee slot is available for others
                  }

                  if (player2.equals( "" )) {

                     player2 = "&nbsp;";       // make it a space for table display
                     available = true;         // tee slot is available for others
                  }

                  if (player3.equals( "" )) {

                     player3 = "&nbsp;";       // make it a space for table display
                     available = true;         // tee slot is available for others
                  }

                  if (player4.equals( "" )) {

                     player4 = "&nbsp;";       // make it a space for table display
                     available = true;         // tee slot is available for others
                  }

                  if (fives != 0) {            // 5-somes supported on this course

                     if (player5.equals( "" )) {

                        if (rest.equals( "" )) {          // if 5-somes are not restricted

                           available = true;         // tee slot is available for others
                           player5 = "&nbsp;";       // make it a space for table display
                        } else {
                           player5 = "N/A";          // player 5 N/A
                        }
                     }
                  }

                  //
                  //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                  //
                  sfb = "O";       // default Other

                  if (fb == 1) {

                     sfb = "B";
                  }

                  if (fb == 0) {

                     sfb = "F";
                  }

                  submit = "time:" + fb;       // create a name for the submit button


                  //
                  //  Build the HTML for each record found
                  //
                  out.println("<tr>");
                  out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                  out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"888\">");  // from here
                     if (fives != 0 && rest.equals( "" )) {
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                     } else {
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                     }

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");

                  if (available) {                      // if there is an empty slot available

                     if (min < 10) {
                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
                     } else {
                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
                     }
                  } else {

                     if (min < 10) {
                        out.println(hr + ":0" + min + ampm);
                     } else {
                        out.println(hr + ":" + min + ampm);
                     }
                  }
                     out.println("</font></td>");

                  if (multi != 0) {

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println(course);
                     out.println("</font></td>");
                  }

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println(sfb);
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player1 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player2 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player3 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player4 );
                     out.println("</font></td>");

                  if (fivesomes != 0) {

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player5 );
                     out.println("</font></td>");
                  }
                     out.println("</form></tr>");

               }   // end of IF skip

            }    // end of while

            pstmt1.close();

            out.println("</font></table>");

            if (count == 0) {

               out.println("<p align=\"center\">No records found for " + name + ".</p>");

            }

            //
            // ****************************************************************
            // use the name to search for lottery requests (if supported)
            // ****************************************************************
            //
            if (lottery > 0) {

               //
               //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
               //
               lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  


               out.println("</td></tr>");       // terminate previous col/row

               pstmt1 = con.prepareStatement (
                  "SELECT name, date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
                  "player5, player6, player7, player8, player9, player10, player11, player12, player13, player14, " +
                  "player15, player16, player17, player18, player19, player20, player21, player22, player23, " +
                  "player24, player25, fb, courseName, id, groups " +
                  "FROM lreqs3 " +
                  "WHERE player1 LIKE ? OR player2 LIKE ? OR player3 LIKE ? OR player4 LIKE ? OR player5 LIKE ? OR " +
                  "player6 LIKE ? OR player7 LIKE ? OR player8 LIKE ? OR player9 LIKE ? OR player10 LIKE ? OR " +
                  "player11 LIKE ? OR player12 LIKE ? OR player13 LIKE ? OR player14 LIKE ? OR player15 LIKE ? OR " +
                  "player16 LIKE ? OR player17 LIKE ? OR player18 LIKE ? OR player19 LIKE ? OR player20 LIKE ? OR " +
                  "player21 LIKE ? OR player22 LIKE ? OR player23 LIKE ? OR player24 LIKE ? OR player25 LIKE ? " +
                  "ORDER BY date, time");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, sname);
               pstmt1.setString(2, sname);
               pstmt1.setString(3, sname);
               pstmt1.setString(4, sname);
               pstmt1.setString(5, sname);
               pstmt1.setString(6, sname);
               pstmt1.setString(7, sname);
               pstmt1.setString(8, sname);
               pstmt1.setString(9, sname);
               pstmt1.setString(10, sname);
               pstmt1.setString(11, sname);
               pstmt1.setString(12, sname);
               pstmt1.setString(13, sname);
               pstmt1.setString(14, sname);
               pstmt1.setString(15, sname);
               pstmt1.setString(16, sname);
               pstmt1.setString(17, sname);
               pstmt1.setString(18, sname);
               pstmt1.setString(19, sname);
               pstmt1.setString(20, sname);
               pstmt1.setString(21, sname);
               pstmt1.setString(22, sname);
               pstmt1.setString(23, sname);
               pstmt1.setString(24, sname);
               pstmt1.setString(25, sname);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               //
               //   build the table for the display
               //
               out.println("<tr><td align=\"center\" valign=\"top\">");

               out.println("<font size=\"3\"><br><br>");
               if (club.equals( "oldoaks" )) {
                  out.println("<b>Current Tee Time Requests</b><br>");
                  out.println("</font><font size=\"2\">");
                  out.println("<br>If the tee time request is eligible for change, you will be able to click on the box containing the time (2nd column).");
               } else if (!lotteryText.equals("")) {
                  out.println("<b>Current " +lotteryText+ "s</b><br>");
                  out.println("</font><font size=\"2\">");
                  out.println("<br>If the " +lotteryText+ " is eligible for change, you will be able to click on the box containing the time (2nd column).");
               } else {
                  out.println("<b>Current Lottery Requests</b><br>");
                  out.println("</font><font size=\"2\">");
                  out.println("<br>If the lottery request is eligible for change, you will be able to click on the box containing the time (2nd column).");
               }
               out.println("</font><font size=\"1\">");
               out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
               out.println("</font><font size=\"2\">");

                  out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\">");
                     out.println("<tr bgcolor=\"#336633\"><td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                           out.println("</font></td>");

                     if (multi != 0) {
                        out.println("<td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                           out.println("</font></td>");
                     }

                        out.println("<td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                           out.println("</font></td>");

                     if (fivesomes != 0) {
                        out.println("<td>");
                           out.println("<font size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                           out.println("</font></td>");
                     }

                        out.println("</tr>");

               //
               //  Get each record and display it
               //
               count = 0;             // number of records found

               while (rs.next()) {

                  count++;

                  lname = rs.getString(1);
                  date = rs.getLong(2);
                  mm = rs.getInt(3);
                  dd = rs.getInt(4);
                  yy = rs.getInt(5);
                  day = rs.getString(6);
                  hr = rs.getInt(7);
                  min = rs.getInt(8);
                  time = rs.getInt(9);
                  player1 = rs.getString(10);
                  player2 = rs.getString(11);
                  player3 = rs.getString(12);
                  player4 = rs.getString(13);
                  player5 = rs.getString(14);
                  player6 = rs.getString(15);
                  player7 = rs.getString(16);
                  player8 = rs.getString(17);
                  player9 = rs.getString(18);
                  player10 = rs.getString(19);
                  player11 = rs.getString(20);
                  player12 = rs.getString(21);
                  player13 = rs.getString(22);
                  player14 = rs.getString(23);
                  player15 = rs.getString(24);
                  player16 = rs.getString(25);
                  player17 = rs.getString(26);
                  player18 = rs.getString(27);
                  player19 = rs.getString(28);
                  player20 = rs.getString(29);
                  player21 = rs.getString(30);
                  player22 = rs.getString(31);
                  player23 = rs.getString(32);
                  player24 = rs.getString(33);
                  player25 = rs.getString(34);
                  fb = rs.getInt(35);
                  course = rs.getString(36);
                  lottid = rs.getLong(37);
                  groups = rs.getInt(38);

                  ampm = " AM";
                  if (hr == 12) {
                     ampm = " PM";
                  }
                  if (hr > 12) {
                     ampm = " PM";
                     hr = hr - 12;    // convert to conventional time
                  }

                  playerCount = 0;         // init # of players in request

                  if (player1.equals( "" )) {

                     player1 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player2.equals( "" )) {

                     player2 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player3.equals( "" )) {

                     player3 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player4.equals( "" )) {

                     player4 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player5.equals( "" )) {

                     player5 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player6.equals( "" )) {

                     player6 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player7.equals( "" )) {

                     player7 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player8.equals( "" )) {

                     player8 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player9.equals( "" )) {

                     player9 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player10.equals( "" )) {

                     player10 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player11.equals( "" )) {

                     player11 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player12.equals( "" )) {

                     player12 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player13.equals( "" )) {

                     player13 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player14.equals( "" )) {

                     player14 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player15.equals( "" )) {

                     player15 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player16.equals( "" )) {

                     player16 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player17.equals( "" )) {

                     player17 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player18.equals( "" )) {

                     player18 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player19.equals( "" )) {

                     player19 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player20.equals( "" )) {

                     player20 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player21.equals( "" )) {

                     player21 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player22.equals( "" )) {

                     player22 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player23.equals( "" )) {

                     player23 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player24.equals( "" )) {

                     player24 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }
                  if (player25.equals( "" )) {

                     player25 = " ";       // make it a space for table display

                  } else {

                     playerCount++;       // bump # of players
                  }

                  //
                  //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                  //
                  sfb = "O";       // default Other

                  if (fb == 1) {

                     sfb = "B";
                  }

                  if (fb == 0) {

                     sfb = "F";
                  }

                  //
                  //  Check if 5-somes supported for this course
                  //
                  fives = 0;        // init

                  PreparedStatement pstmtc = con.prepareStatement (
                     "SELECT fives " +
                     "FROM clubparm2 WHERE first_hr != 0 AND courseName = ?");

                  pstmtc.clearParameters();        // clear the parms
                  pstmtc.setString(1, course);
                  rs2 = pstmtc.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     fives = rs2.getInt(1);          // 5-somes
                  }
                  pstmtc.close();

                  //
                  //  check if 5-somes restricted for this time
                  //
                  if (fives != 0) {

                     PreparedStatement pstmtr = con.prepareStatement (
                        "SELECT rest5 " +
                        "FROM teecurr2 WHERE date = ? AND time =? AND fb = ? AND courseName = ?");

                     pstmtr.clearParameters();        // clear the parms
                     pstmtr.setLong(1, date);
                     pstmtr.setInt(2, time);
                     pstmtr.setInt(3, fb);
                     pstmtr.setString(4, course);
                     rs2 = pstmtr.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        rest = rs2.getString(1);
                     }
                     pstmtr.close();
                  }

                  //
                  //  Determine the max # of players allowed in this request
                  //
                  if (fives > 0 && rest.equals( "" )) {       // if 5-somes allowed

                     maxPlayers = (groups * 5);

                  } else {

                     maxPlayers = (groups * 4);
                  }

                  //
                  //  get the slots value and determine the current state for this lottery
                  //
                  PreparedStatement pstmt7d = con.prepareStatement (
                     "SELECT slots " +
                     "FROM lottery3 WHERE name = ?");

                  pstmt7d.clearParameters();          // clear the parms
                  pstmt7d.setString(1, lname);

                  rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                  if (rs2.next()) {

                     slots = rs2.getInt(1);

                  }                  // end of while
                  pstmt7d.close();


                  //
                  //  Get the current state of this lottery on the day of this tee time
                  //
                  lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lname, course, con);


                  submit = "time:" + fb;       // create a name for the submit button

                  //
                  //  Build the HTML for each record found
                  //
                  out.println("<tr>");
                  out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                  out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
                  out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
                  out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                  out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");

                  if (fives != 0) {                     // if 5-somes on this course
                     if (!rest.equals( "" )) {          // if 5-somes are restricted
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                     } else {
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                     }
                  } else {
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                  }

                  out.println("<input type=\"hidden\" name=\"index\" value=888>");  // indicate from searchmain.htm
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     if (lstate == 2 && playerCount < maxPlayers) {    // if state allows change & room for players
                        if (min < 10) {
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
                        } else {
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
                        }
                     } else {
                        if (min < 10) {
                           out.println(hr + ":0" + min + ampm);
                        } else {
                           out.println(hr + ":" + min + ampm);
                        }
                     }
                  out.println("</font></td>");

                  if (multi != 0) {
                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println(course);
                     out.println("</font></td>");
                  }

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println(sfb);
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player1 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player2 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player3 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player4 );
                     out.println("</font></td>");

                  if (fives != 0) {
                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player5 );
                     out.println("</font></td>");

                     //
                     //  check if there are more than 5 players registered
                     //
                     if (!player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " ) || !player9.equals( " " ) || !player10.equals( " " )) {

                        out.println("</tr><tr>");
                        out.println("<td align=\"center\">");      // date col
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\">");      // time col
                        out.println("&nbsp;</td>");

                        if (multi != 0) {
                           out.println("<td align=\"center\">");   // course
                           out.println("&nbsp;</td>");
                        }

                        out.println("<td align=\"center\">");       // f/b
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player6 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player7 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player8 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player9 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player10 );
                        out.println("</font></td>");
                     }

                     if (!player11.equals( " " ) || !player12.equals( " " ) || !player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " )) {

                        out.println("</tr><tr>");
                        out.println("<td align=\"center\">");      // date col
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\">");      // time col
                        out.println("&nbsp;</td>");

                        if (multi != 0) {
                           out.println("<td align=\"center\">");   // course
                           out.println("&nbsp;</td>");
                        }

                        out.println("<td align=\"center\">");       // f/b
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player11 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player12 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player13 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player14 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player15 );
                        out.println("</font></td>");
                     }

                     if (!player16.equals( " " ) || !player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

                        out.println("</tr><tr>");
                        out.println("<td align=\"center\">");      // date col
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\">");      // time col
                        out.println("&nbsp;</td>");

                        if (multi != 0) {
                           out.println("<td align=\"center\">");   // course
                           out.println("&nbsp;</td>");
                        }

                        out.println("<td align=\"center\">");       // f/b
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player16 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player17 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player18 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player19 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player20 );
                        out.println("</font></td>");
                     }

                     if (!player21.equals( " " ) || !player22.equals( " " ) || !player23.equals( " " ) || !player24.equals( " " ) || !player25.equals( " " )) {

                        out.println("</tr><tr>");
                        out.println("<td align=\"center\">");      // date col
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\">");      // time col
                        out.println("&nbsp;</td>");

                        if (multi != 0) {
                           out.println("<td align=\"center\">");   // course
                           out.println("&nbsp;</td>");
                        }

                        out.println("<td align=\"center\">");       // f/b
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player21 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player22 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player23 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player24 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player25 );
                        out.println("</font></td>");
                     }

                  } else {   // no 5-somes on this course

                     if (fivesomes != 0) {        // if 5-somes listed - empty slot

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println("nbsp;");
                        out.println("</font></td>");
                     }
                     //
                     //  check if there are more than 4 players registered
                     //
                     if (!player5.equals( " " ) || !player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " )) {

                        out.println("</tr><tr>");
                        out.println("<td align=\"center\">");      // date col
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\">");      // time col
                        out.println("&nbsp;</td>");

                        if (multi != 0) {
                           out.println("<td align=\"center\">");   // course
                           out.println("&nbsp;</td>");
                        }

                        out.println("<td align=\"center\">");       // f/b
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player5 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player6 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player7 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player8 );
                        out.println("</font></td>");

                        if (fivesomes != 0) {        // if 5-somes listed - empty slot

                           out.println("<td align=\"center\" bgcolor=\"white\">");
                           out.println("<font size=\"2\">");
                           out.println("nbsp;");
                           out.println("</font></td>");
                        }
                     }

                     if (!player9.equals( " " ) || !player10.equals( " " ) || !player11.equals( " " ) || !player12.equals( " " )) {

                        out.println("</tr><tr>");
                        out.println("<td align=\"center\">");      // date col
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\">");      // time col
                        out.println("&nbsp;</td>");

                        if (multi != 0) {
                           out.println("<td align=\"center\">");   // course
                           out.println("&nbsp;</td>");
                        }

                        out.println("<td align=\"center\">");       // f/b
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player9 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player10 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player11 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player12 );
                        out.println("</font></td>");

                        if (fivesomes != 0) {        // if 5-somes listed - empty slot

                           out.println("<td align=\"center\" bgcolor=\"white\">");
                           out.println("<font size=\"2\">");
                           out.println("nbsp;");
                           out.println("</font></td>");
                        }
                     }

                     if (!player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " ) || !player16.equals( " " )) {

                        out.println("</tr><tr>");
                        out.println("<td align=\"center\">");      // date col
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\">");      // time col
                        out.println("&nbsp;</td>");

                        if (multi != 0) {
                           out.println("<td align=\"center\">");   // course
                           out.println("&nbsp;</td>");
                        }

                        out.println("<td align=\"center\">");       // f/b
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player13 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player14 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player15 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player16 );
                        out.println("</font></td>");

                        if (fivesomes != 0) {        // if 5-somes listed - empty slot

                           out.println("<td align=\"center\" bgcolor=\"white\">");
                           out.println("<font size=\"2\">");
                           out.println("nbsp;");
                           out.println("</font></td>");
                        }
                     }

                     if (!player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

                        out.println("</tr><tr>");
                        out.println("<td align=\"center\">");      // date col
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\">");      // time col
                        out.println("&nbsp;</td>");

                        if (multi != 0) {
                           out.println("<td align=\"center\">");   // course
                           out.println("&nbsp;</td>");
                        }

                        out.println("<td align=\"center\">");       // f/b
                        out.println("&nbsp;</td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player17 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player18 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player19 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println( player20 );
                        out.println("</font></td>");

                        if (fivesomes != 0) {        // if 5-somes listed - empty slot

                           out.println("<td align=\"center\" bgcolor=\"white\">");
                           out.println("<font size=\"2\">");
                           out.println("nbsp;");
                           out.println("</font></td>");
                        }
                     }
                  }     // end of IF 5-somes
                  out.println("</form></tr>");

               }    // end of while

               pstmt1.close();

               out.println("</font></table>");

               if (count == 0) {

                  out.println("<font size=\"2\">");
                  if (club.equals( "oldoaks" )) {
                     out.println("<p align=\"center\">No tee time requests found for " + name + ".</p>");
                  } else if (!lotteryText.equals("")) {
                     out.println("<p align=\"center\">No " +lotteryText+ "s found for " + name + ".</p>");
                  } else {
                     out.println("<p align=\"center\">No lottery requests found for " + name + ".</p>");
                  }
                  out.println("</font>");

               }

            } // end if lottery search

            
         } else {   // NOT Golf
         
            //***********************************************************
            //  ACTIVITY Processing
            //***********************************************************
            //
            PreparedStatement pstmt2 = null;
            
            //
            // non-Golf Activity - find any reservations for the selected name
            //
            pstmt1 = con.prepareStatement (
                "SELECT *, " +
                   "DATE_FORMAT(a.date_time, '%W, %b. %D') AS pretty_date, " +
                   "DATE_FORMAT(a.date_time, '%Y%m%d') AS dateymd, " +
                   "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time " +
                "FROM activity_sheets a, activity_sheets_players ap " +
                "LEFT OUTER JOIN activities t2 ON t2.activity_id = a.activity_id " +
                "WHERE a.sheet_id = ap.activity_sheet_id " +
                   "AND ap.player_name LIKE ? " +
                   "AND DATE(a.date_time) >= DATE(now()) " +
                "ORDER BY a.date_time");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, sname);

            rs = pstmt1.executeQuery();      // execute the prepared stmt

            // if we found any then output the header row.
            rs.last();
            if (rs.getRow() > 0) {

               out.println("<font size=\"3\">");
               out.println("<p><BR><b>Search Results for</b> " + name + "</p>");
               out.println("<br><b>Reservations</b>.");
               out.println("</font><font size=\"2\">");
               out.println("<br><br><b>To join an open reservation</b>:  Just click on the date & time (if green & underlined).<br><br>");

               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<tr bgcolor=\"#336633\"><td>");
               out.println("<font size=\"2\">");
               out.println("<p align=\"center\"><u><b>Date/Time</b></u></p>");
               out.println("</font></td>");

               out.println("<td>");
               out.println("<font size=\"2\">");
               out.println("<p align=\"center\"><u><b>Location</b></u></p>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<u><b>Players</b></u>");
               out.println("</font></td>");

               out.println("</tr>");
            }
 
            rs.beforeFirst();       // back up

            //
            //  Get each record and display it
            //
            count = 0;             // number of records found

            while (rs.next()) {
               
               String playerList = "";
               count++;
               int pcount = 0;

               //
               //   Get the players 
               //
               pstmt2 = con.prepareStatement("" +
                      "SELECT *, (" +
                          "SELECT COUNT(*) AS players " +
                          "FROM activity_sheets_players " +
                          "WHERE activity_sheet_id = ?) AS part_of " +
                      "FROM activity_sheets_players " +
                      "WHERE activity_sheet_id = ? " +
                      "ORDER BY pos");
               pstmt2.clearParameters();
               pstmt2.setInt(1, rs.getInt("sheet_id"));
               pstmt2.setInt(2, rs.getInt("sheet_id"));
               rs2 = pstmt2.executeQuery();

               while ( rs2.next() ) {

                  pcount++;          // count number of players
                  
                  playerList = playerList + rs2.getString("player_name") + "<BR>";     // add player to playerlist
               }

               //
               //  Build the HTML for each record found
               //
               out.println("<tr>");

               out.println("<td align=\"center\">");
               if (rs.getInt("disallow_joins") == 0 && pcount < rs.getInt("max_players")) {      // if member can join
                  out.print("<a href=\"javascript:void(0)\" onclick=\"top.location.href='/" + rev + "/servlet/Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=888'\"><font size=2 color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a>"); 
               } else {
                  out.print("<font size=2>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font>"); 
               }
               out.println("</td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println(getActivity.getFullActivityName(rs.getInt("activity_id"), con));
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( playerList );
               out.println("</font></td>");
               out.println("</tr>");          

               pstmt2.close();

            }    // end of while

            pstmt1.close();

            out.println("</font></table>");

            if (count == 0) {

               out.println("<p align=\"center\">No records found for " + name + ".</p>");
            }               
                 
         }    // end of IF GOLF
            
            
         out.println("</td></tr></table></td>");                // end of main page table & column
         out.println("</font>");
         out.println("<font size=\"2\">");

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_searchmem\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</input></form>");               // return to searchmain.htm

         out.println("</font>");

         //
         //  End of HTML page
         //
         out.println("</center></font></body></html>");
         out.close();


      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club manager.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Member_searchmem\">Return</a>");  // return to searchmain.htm
         out.println("</CENTER></BODY></HTML>");
         out.close();

      }     // end of search function

   }        // end of call type if

 }   // end of doPost


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the player's name, or some portion of it.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Member does not exists - LEGACY FUNCTION
 // *********************************************************
/*
 private void noMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>No Tee Times Found</H3><BR>");
   out.println("<BR><BR>The member you specified has no tee times scheduled for the next 30 days.<BR>");
   out.println("<BR>Please check your data and try again if you wish.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 } */

}
