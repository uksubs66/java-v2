/***************************************************************************************     
 *   Proshop_searchmem:  This servlet will process the 'search for member' request from
 *                       Proshop's searchmem page.  It will use the name provided to search
 *                       teecurr for any matches (current tee times).
 *
 *                       Caller only provides the last name, or a portion of it.
 *
 *
 *   called by:  proshop menu and self
 *
 *   created: 1/10/2002   Bob P.
 *
 *   last updated:
 *
 *        4/08/13   Adjusted search so that it will now pull back tee times even if the lottery_email flag has not been set to 0 for a tee time.
 *       11/16/12   MySQL 5.5 compatability
 *        9/10/10   Do not include event and lottery times that have been drug to the tee sheet but not approved in member search
 *        6/24/10   Updated js calls for name list to add iPad compatability
 *       10/03/09   Add searches for Activities (FlexRez changes).
 *        9/02/08   Javascript compatability updates
 *        7/18/08   Added limited access proshop users checks
 *        4/29/08   Add upcoming submitted notifications data to Notification clubs
 *        6/08/07   Los Coyotes - add mnum to end of name for display (case #1188).
 *       11/18/06   If tee time is during a shotgun event, indicate so and make submit button read 'Shotgun'.
 *        8/18/05   Add notes to the tee time display and allow pro to display them.
 *        1/24/05   Ver 5 - change club2 to club5.
 *       10/18/04   Enhancements for Version 5 - change to get control from navigation menu.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add search of lottery requests.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Remove old processing for call from Tee Sheet - no longer used.
 *
 *        1/05/03   Enhancements for Version 2 of the software.
 *                  Add support for multiple courses and 5-somes.
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.client.SystemLingo;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Proshop_searchmem extends HttpServlet {

                               
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //****************************************************
 // Process the call from the menu 
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }    
   
   Connection con = SystemUtils.getCon(session);                     // get DB connection
   
   if (con == null) {
       
       out.println(SystemUtils.HeadTitle("DB Connection Error"));
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><BR><H3>Database Connection Error</H3>");
       out.println("<BR><BR>Unable to connect to the Database.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR>");
       out.println("<a href=\"javascript:history.back(1)\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TOOLS_SEARCHTS", con, out)) {
       SystemUtils.restrictProshop("TOOLS_SEARCHTS", out);
       return;
   }
   if (!SystemUtils.verifyProAccess(req, "TS_VIEW", con, out)) {
       SystemUtils.restrictProshop("TS_VIEW", out);
       return;
   }
   
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)session.getAttribute("activity_id");   // get Activity indicator (golf=0)
    
   //
   //  See if we are in teh timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   // 
   //  Set the appropriate language for the times to search for (i.e. Tee Times, Times, etc)
   //
   String timesText = "";
   
   if (sess_activity_id == 0) {        // if Golf
      
      timesText = sysLingo.TEXT_tee_times;   // get text based on Tee Times or Notification System
      
   } else {
      
      timesText = "Times";           // generic term for FlexRez
   }
   
   
   //
   //   output the html page
   //
   out.println(SystemUtils.HeadTitle2("Proshop Seearch"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].name.focus(); }");
   out.println("// -->");
   out.println("</script>");
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><br><table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>To locate the current " + timesText + " for an individual or group, enter the name,<br>");
      out.println("or any portion of the name, as it may exist on the tee sheets.<br>");
      out.println("This will search for all names that contain the value you enter.<br>");
      out.println("You may also search for X or Guest Types if you wish.</p>");
      out.println("</font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Courier New\">");
   out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
   out.println("<br></font>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<form action=\"Proshop_searchmem\" method=\"post\" target=\"bot\" name=\"f\">");

   out.println("<input type=\"hidden\" name=\"source\" value=\"main\">");

   out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");
               out.println("<tr><td width=\"250\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>Name: &nbsp;");
                     out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                     out.println("");
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
            out.println("<table border=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
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
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();

 }


 //
 //****************************************************
 // Process the form request from doGet above
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   PreparedStatement stmt = null;
   Statement mstmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  See if we are in teh timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
    
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   String club = (String)session.getAttribute("club");               // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lotteryS = Integer.parseInt(templott);

   int sess_activity_id = (Integer)session.getAttribute("activity_id");   // get Activity indicator (golf=0)

   boolean enableAdvAssist = Utilities.enableAdvAssist(req);

   String omit = "";
   String time_zone = "";
   String ampm = "";
   String day = "";
   String submit = "";
   String sfb = "";
   String course = "";
   String event = "";
   String rest5 = "";
   String lname = "";
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
     
   String mnum = "";
   String rest = "";
   String notes = "";
   
   long date = 0;
   long lottid = 0;

   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   //int ehr = 0;
   //int emin = 0;
   int time = 0;
   int count = 0;
   int fb = 0;
   int etype = 0;
   int multi = 0;
   int lottery = 0;
   int fives = 0;
   int fiveSomes = 0;
   int slots = 0;
   int lstate = 0;
   int advance_days = 0;
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;
   int foretees_mode = 0;

   // 
   //  Set the appropriate language for the times to search for (i.e. Tee Times, Times, etc)
   //
   String timesText = "";
   
   if (sess_activity_id == 0) {        // if Golf
      
      timesText = sysLingo.TEXT_tee_times;   // get text based on Tee Times or Notification System
      
   } else {
      
      timesText = "Times";           // generic term for FlexRez
   }
   
   
   //
   // Process request according to which 'submit' button was selected
   //
   //      'search' - a search request
   //      'source' - 'main' from searchmain.htm
   //      'letter' - a request to list member names (= A - Z)
   //      'name'  -  the name, or partial name, to search for
   //
   // Get the parameters entered
   //
   String source = req.getParameter("source");    //  caller (main)

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
      out.println(SystemUtils.HeadTitle2("Proshop - Search"));
      out.println("<script type=\"text/javascript\">");
      out.println("<!-- ");
      out.println("function cursor() { document.forms['f'].name.focus(); }");
      out.println("function movename(name) {");
      out.println(" document.forms['f'].name.value = name;");            // put name selected into the search form
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script

      out.println("</head>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
      SystemUtils.getProshopSubMenu(req, out, lotteryS);        // required to allow submenus on this page
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");

         out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<p>To locate the current " + timesText + " for an individual or group, enter the name,<br>");
            out.println("or any portion of the name, as it may exist on the tee sheets.<br>");
            out.println("This will search for all names that contain the value you enter.<br>");
            out.println("You may also search for 'X' or 'Guest' if you wish (do not include quotes).</p>");
            out.println("</font>");
         out.println("</td></tr></table>");
         
         out.println("<font size=\"2\" face=\"Courier New\">");
         out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
         out.println("<br></font>");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

         out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table for 3 smaller tables

         out.println("<form action=\"Proshop_searchmem\" method=\"post\" target=\"bot\" name=\"f\">");
         out.println("<input type=\"hidden\" name=\"source\" value=\"main\">");
         out.println("<tr><td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");
               out.println("<tr><td width=\"250\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>Name: &nbsp;");
                     out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                     out.println("");
                  out.println("<br><br>");
                  out.println("<input type=\"submit\" value=\"Search\" name=\"search\">");
                  out.println("</p>");
                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table></td>");

         out.println("<td valign=\"top\" align=\"center\">");
         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Name List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");

         try {

            PreparedStatement stmt2 = con.prepareStatement (
                     "SELECT name_last, name_first, name_mi, memNum FROM member2b " +
                     "WHERE name_last LIKE ? ORDER BY name_last, memNum, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"8\" name=\"bname\" " + ((enableAdvAssist) ? "onclick" : "onchange") + "=\"movename(this.form.bname.value)\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);
               mnum = rs.getString(4);

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;
               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               //
               //  Los Coyotes - add mnum to end of name for display only
               //
               if (club.equals( "loscoyotes" )) {

                  dname = dname + " " + mnum;              // add mnum for display - do not move with name
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
               
               out.println("<table border=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
                  out.println("<tr>");
                     out.println("<td colspan=\"6\" align=\"center\" bgcolor=\"#336633\">");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
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

            out.println("<form method=\"get\" action=\"Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");

      out.println("</font></center></body></html>");


   } else {   // not a letter call
      
      //
      //   if call was for Show Notes then get the notes and display a new page
      //
      if (req.getParameter("notes") != null) {

         String sdate = req.getParameter("date");         //  date of the slot
         String stime = req.getParameter("time");         //  time of the slot
         sfb = req.getParameter("fb");             //  front/back indicator
         course = req.getParameter("course");          

         date = Long.parseLong(sdate);

         SystemUtils.displayNotes(stime, sfb, date, course, out, con);             // display the information
         return;
      }

      
      //***********************************************************************
      //  Called to search for a name - get the name and search for it
      //***********************************************************************
      //
      String name = req.getParameter("name");        //  name or portion of name

      int length = name.length();                    // get length of name requested

      //
      //  verify the required fields
      //
      if ((name.equals( omit )) || (length > 43)) {

         invData(out);    // inform the user and return
         return;
      }

      //
      //   Add a % to the name provided so search will match anything close
      //
      StringBuffer buf = new StringBuffer("%");
      buf.append( name );
      buf.append("%");
      String sname = buf.toString();

      
      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Proshop Member Search Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\" valign=\"top\"><BR><BR>");

      try {

         //
         //  Determine if multiple courses and lotteries are supported
         //
         mstmt = con.createStatement();        // create a statement

         rs = mstmt.executeQuery("SELECT multi, lottery, adv_zone, foretees_mode FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
            lottery = rs.getInt(2);
            time_zone = rs.getString(3);
            foretees_mode = rs.getInt(4);
         }
         mstmt.close();

         
         //
         //  Check if Golf is supported.  If not, then only check Activities
         //
         if (foretees_mode == 1 && sess_activity_id == 0) {           // Golf defined in system and not an Activity Pro?         

            //
            //  Determine if 5-somes are supported on any course
            //
            mstmt = con.createStatement();        // create a statement

            rs = mstmt.executeQuery("SELECT fives FROM clubparm2 WHERE first_hr != 0");

            while (rs.next()) {

               fives = rs.getInt(1);

               if (fives != 0) {

                  fiveSomes = 1;      // 5-somes supported on at least one course
               }
            }
            mstmt.close();

            //
            // use the name to search table
            //
            stmt = con.prepareStatement (
               "SELECT date, mm, dd, yy, day, hr, min, time, event, player1, player2, player3, player4, " +
               "event_type, fb, player5, notes, courseName, rest5 " +
               "FROM teecurr2 " +
               "WHERE (player1 LIKE ? OR player2 LIKE ? OR player3 LIKE ? OR player4 LIKE ? OR player5 LIKE ?) " +
               "ORDER BY date, time");

            stmt.clearParameters();        // clear the parms
            stmt.setString(1, sname);
            stmt.setString(2, sname);
            stmt.setString(3, sname);
            stmt.setString(4, sname);
            stmt.setString(5, sname);


             out.println("<font size=\"3\">");
             if (IS_TLT) {
                out.println("<b>Confirmed Notifications</b><br>");
             } else {
                out.println("<b>Current Tee Times</b><br>");
             }
             out.println("</font>");

            rs = stmt.executeQuery();      // execute the prepared stmt

            if (!rs.next()) {              // any tee times?

               out.println("<font size=\"2\">");
               out.println("<p align=\"center\">No current " + sysLingo.TEXT_tee_times + " found for " + name + ".</p>");
               out.println("</font>");

            } else {   

               out.println("<font size=\"1\">");
               out.println("<b>To select a " + sysLingo.TEXT_tee_time + "</b>:  Just click on the box containing the time (2nd column).");
               out.println("</font><font size=\"1\">");
               out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other, &nbsp;&nbsp;N = Notes");
               out.println("</font><font size=\"2\">");

               out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
                  out.println("<tr bgcolor=\"#336633\"><td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                        out.println("</font></td>");

                  if (multi != 0) {
                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                        out.println("</font></td>");
                  }

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                        out.println("</font></td>");

                  if (fiveSomes != 0) {
                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                        out.println("</font></td>");
                  }

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>N</b></u></p>");
                        out.println("</font></td>");

                     out.println("</tr>");

               //
               //  Get each record and display it
               //
               count = 0;             // number of records found

               rs = stmt.executeQuery();      // execute the prepared stmt again

               while (rs.next()) {

                  count++;

                  date = rs.getLong(1);
                  mm = rs.getInt(2);
                  dd = rs.getInt(3);
                  yy = rs.getInt(4);
                  day = rs.getString(5);
                  hr = rs.getInt(6);
                  min = rs.getInt(7);
                  time = rs.getInt(8);
                  event = rs.getString(9);
                  player1 = rs.getString(10);
                  player2 = rs.getString(11);
                  player3 = rs.getString(12);
                  player4 = rs.getString(13);
                  etype = rs.getInt(14);
                  fb = rs.getInt(15);
                  player5 = rs.getString(16);
                  notes = rs.getString(17);
                  course = rs.getString(18);
                  rest5 = rs.getString(19);

                  ampm = " AM";
                  if (hr == 12) {
                     ampm = " PM";
                  }
                  if (hr > 12) {
                     ampm = " PM";
                     hr = hr - 12;    // convert to conventional time
                  }

                  if (player1.equals( "" )) {

                     player1 = "&nbsp;";       // make it a space for table display
                  }
                  if (player2.equals( "" )) {

                     player2 = "&nbsp;";       // make it a space for table display
                  }
                  if (player3.equals( "" )) {

                     player3 = "&nbsp;";       // make it a space for table display
                  }
                  if (player4.equals( "" )) {

                     player4 = "&nbsp;";       // make it a space for table display
                  }
                  if (player5.equals( "" )) {

                     player5 = "&nbsp;";       // make it a space for table display
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
                  //  Build the HTML for each record found
                  //
                  out.println("<tr>");
                  out.println("<form action=\"Proshop_slot\" method=\"get\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                  out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
                  if (fives != 0) {
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                     if (!rest5.equals( "" )) {          // if 5-somes are restricted

                        out.println("<input type=\"hidden\" name=\"p5rest\" value=\"Yes\">");  // tell _slot
                     } else {
                        out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                     }
                  } else {
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                     out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                  }

                  out.println("<input type=\"hidden\" name=\"index\" value=888>");  // indicate from searchmain.htm
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");

                  if (!event.equals( "" ) && etype == 1) {    // if Shotgun Event

                     out.println("<input type=\"submit\" name=\"shotgun\" value=\"Shotgun\" alt=\"submit\">");
                     out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">");
                     out.println("<input type=\"hidden\" name=\"shotgunevent\" value=\"yes\">");

                  } else {

                     out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");
                  }
                  out.println("</font></td></form>");

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

                  if (fiveSomes != 0) {
                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player5 );
                     out.println("</font></td>");
                  }

                  //
                  //  Last column for 'Notes' box
                  //
                  if (!notes.equals("")) {

                     out.println("<form method=\"post\" action=\"Proshop_searchmem\" target=\"_blank\">");
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"2\">");

                     out.println("<input type=\"hidden\" name=\"notes\" value=\"yes\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                     out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"image\" src=\"/" +rev+ "/images/notes.jpg\" border=\"0\" name=\"showNotes\" title=\"Click here to view notes.\">");

                     out.println("</font></td></form>");         // end of the col

                  } else {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");         // end of the col
                  }

                  out.println("</tr>");

               }    // end of while

               out.println("</font></table>");
            }

            stmt.close();


            //
            // ****************************************************************
            // use the name to search for lottery requests (if supported)
            // ****************************************************************
            //
            if (lottery > 0) {

               out.println("</td></tr>");       // terminate previous col/row

               stmt = con.prepareStatement (
                  "SELECT name, date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
                  "player5, player6, player7, player8, player9, player10, player11, player12, player13, player14, " +
                  "player15, player16, player17, player18, player19, player20, player21, player22, player23, " +        
                  "player24, player25, fb, courseName, id " +
                  "FROM lreqs3 " +
                  "WHERE player1 LIKE ? OR player2 LIKE ? OR player3 LIKE ? OR player4 LIKE ? OR player5 LIKE ? OR " +
                  "player6 LIKE ? OR player7 LIKE ? OR player8 LIKE ? OR player9 LIKE ? OR player10 LIKE ? OR " +
                  "player11 LIKE ? OR player12 LIKE ? OR player13 LIKE ? OR player14 LIKE ? OR player15 LIKE ? OR " +
                  "player16 LIKE ? OR player17 LIKE ? OR player18 LIKE ? OR player19 LIKE ? OR player20 LIKE ? OR " +
                  "player21 LIKE ? OR player22 LIKE ? OR player23 LIKE ? OR player24 LIKE ? OR player25 LIKE ? " +
                  "ORDER BY date, time");

               stmt.clearParameters();        // clear the parms
               stmt.setString(1, sname);
               stmt.setString(2, sname);
               stmt.setString(3, sname);
               stmt.setString(4, sname);
               stmt.setString(5, sname);
               stmt.setString(6, sname);
               stmt.setString(7, sname);
               stmt.setString(8, sname);
               stmt.setString(9, sname);
               stmt.setString(10, sname);
               stmt.setString(11, sname);
               stmt.setString(12, sname);
               stmt.setString(13, sname);
               stmt.setString(14, sname);
               stmt.setString(15, sname);
               stmt.setString(16, sname);
               stmt.setString(17, sname);
               stmt.setString(18, sname);
               stmt.setString(19, sname);
               stmt.setString(20, sname);
               stmt.setString(21, sname);
               stmt.setString(22, sname);
               stmt.setString(23, sname);
               stmt.setString(24, sname);
               stmt.setString(25, sname);


               //
               //   build the table for the display
               //
               out.println("<tr><td align=\"center\" valign=\"top\">");

               out.println("<font size=\"3\"><br><br>");
               out.println("<b>Current Lottery Requests</b><br>");
               out.println("</font>");

               rs = stmt.executeQuery();      // execute the prepared stmt

               if (!rs.next()) {              // any lottery requests?

                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">No lottery requests found for " + name + ".</p>");
                  out.println("</font>");

               } else {

                  out.println("<font size=\"1\">");
                  out.println("<b>To select a lottery request</b>:  Just click on the box containing the time (2nd column).");
                  out.println("</font><font size=\"1\">");
                  out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
                  out.println("</font><font size=\"2\">");

                  out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
                     out.println("<tr bgcolor=\"#336633\"><td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                           out.println("</font></td>");

                     if (multi != 0) {
                        out.println("<td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                           out.println("</font></td>");
                     }

                        out.println("<td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                           out.println("</font></td>");

                        out.println("<td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                           out.println("</font></td>");

                     if (fiveSomes != 0) {
                        out.println("<td>");
                           out.println("<font color=\"#FFFFFF\" size=\"2\">");
                           out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                           out.println("</font></td>");
                     }

                        out.println("</tr>");

                  //
                  //  Get each record and display it
                  //
                  count = 0;             // number of records found

                  rs = stmt.executeQuery();      // execute the prepared stmt again

                  while (rs.next()) {

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

                     count++;

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }

                     if (player1.equals( "" )) {

                        player1 = " ";       // make it a space for table display
                     }
                     if (player2.equals( "" )) {

                        player2 = " ";       // make it a space for table display
                     }
                     if (player3.equals( "" )) {

                        player3 = " ";       // make it a space for table display
                     }
                     if (player4.equals( "" )) {

                        player4 = " ";       // make it a space for table display
                     }
                     if (player5.equals( "" )) {

                        player5 = " ";       // make it a space for table display
                     }
                     if (player6.equals( "" )) {

                        player6 = " ";       // make it a space for table display
                     }
                     if (player7.equals( "" )) {

                        player7 = " ";       // make it a space for table display
                     }
                     if (player8.equals( "" )) {

                        player8 = " ";       // make it a space for table display
                     }
                     if (player9.equals( "" )) {

                        player9 = " ";       // make it a space for table display
                     }
                     if (player10.equals( "" )) {

                        player10 = " ";       // make it a space for table display
                     }
                     if (player11.equals( "" )) {

                        player11 = " ";       // make it a space for table display
                     }
                     if (player12.equals( "" )) {

                        player12 = " ";       // make it a space for table display
                     }
                     if (player13.equals( "" )) {

                        player13 = " ";       // make it a space for table display
                     }
                     if (player14.equals( "" )) {

                        player14 = " ";       // make it a space for table display
                     }
                     if (player15.equals( "" )) {

                        player15 = " ";       // make it a space for table display
                     }
                     if (player16.equals( "" )) {

                        player16 = " ";       // make it a space for table display
                     }
                     if (player17.equals( "" )) {

                        player17 = " ";       // make it a space for table display
                     }
                     if (player18.equals( "" )) {

                        player18 = " ";       // make it a space for table display
                     }
                     if (player19.equals( "" )) {

                        player19 = " ";       // make it a space for table display
                     }
                     if (player20.equals( "" )) {

                        player20 = " ";       // make it a space for table display
                     }
                     if (player21.equals( "" )) {

                        player21 = " ";       // make it a space for table display
                     }
                     if (player22.equals( "" )) {

                        player22 = " ";       // make it a space for table display
                     }
                     if (player23.equals( "" )) {

                        player23 = " ";       // make it a space for table display
                     }
                     if (player24.equals( "" )) {

                        player24 = " ";       // make it a space for table display
                     }
                     if (player25.equals( "" )) {

                        player25 = " ";       // make it a space for table display
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
                     if (fiveSomes != 0) {

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
                     //  get the slots value and determine the current state for this lottery
                     //
                     PreparedStatement pstmt7d = con.prepareStatement (
                        "SELECT sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                        "FROM lottery3 WHERE name = ?");

                     pstmt7d.clearParameters();          // clear the parms
                     pstmt7d.setString(1, lname);

                     rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                     if (rs2.next()) {

                        sdays = rs2.getInt(1);         // days in advance to start taking requests
                        sdtime = rs2.getInt(2);
                        edays = rs2.getInt(3);         // ...stop taking reqs
                        edtime = rs2.getInt(4);
                        pdays = rs2.getInt(5);         // ....to process reqs
                        ptime = rs2.getInt(6);         
                        slots = rs2.getInt(7);       

                     }                  // end of while
                     pstmt7d.close();

                     //
                     //    Determine which state we are in (before req's, during req's, before process, after process)
                     //
                     //  Get the current time
                     //
                     Calendar cal3 = new GregorianCalendar();    // get the current time of the day

                     if (time_zone.equals( "Eastern" )) {         // Eastern Time = +1 hr

                        cal3.add(Calendar.HOUR_OF_DAY,1);         // roll ahead 1 hour (rest should adjust)
                     }

                     if (time_zone.equals( "Mountain" )) {        // Mountain Time = -1 hr

                        cal3.add(Calendar.HOUR_OF_DAY,-1);        // roll back 1 hour (rest should adjust)
                     }

                     if (time_zone.equals( "Pacific" )) {         // Pacific Time = -2 hrs

                        cal3.add(Calendar.HOUR_OF_DAY,-2);        // roll back 2 hours (rest should adjust)
                     }

                     int cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time - adjusted for time zone)
                     int cal_min = cal3.get(Calendar.MINUTE);
                     int curr_time = cal_hour * 100;
                     curr_time = curr_time + cal_min;                // create military time

                     //
                     //  determine the number of days in advance of the req'd tee time we currently are
                     //
                     int cal_yy = cal3.get(Calendar.YEAR);
                     int cal_mm = cal3.get(Calendar.MONTH);
                     int cal_dd = cal3.get(Calendar.DAY_OF_MONTH);

                     cal_mm++;                            // month starts at zero
                     advance_days = 0;

                     while (cal_mm != mm || cal_dd != dd || cal_yy != yy) {

                        cal3.add(Calendar.DATE,1);                // roll ahead 1 day untill a match found

                        cal_yy = cal3.get(Calendar.YEAR);
                        cal_mm = cal3.get(Calendar.MONTH);
                        cal_dd = cal3.get(Calendar.DAY_OF_MONTH);

                        cal_mm++;                            // month starts at zero
                        advance_days++;
                     }

                     //
                     //  now check the day and time values 
                     //
                     if (advance_days > sdays) {       // if we haven't reached the start day yet

                        lstate = 1;                    // before time to take requests

                     } else {

                        if (advance_days == sdays) {   // if this is the start day

                           if (curr_time >= sdtime) {   // have we reached the start time?

                              lstate = 2;              // after start time, before stop time to take requests

                           } else {

                              lstate = 1;              // before time to take requests
                           }
                        } else {                        // we are past the start day

                           lstate = 2;                 // after start time, before stop time to take requests
                        }

                        if (advance_days == edays) {   // if this is the stop day

                           if (curr_time >= edtime) {   // have we reached the stop time?

                              lstate = 3;              // after start time, before stop time to take requests
                           }
                        }

                        if (advance_days < edays) {   // if we are past the stop day

                           lstate = 3;                // after start time, before stop time to take requests
                        }
                     }

                     if (lstate == 3) {                // if we are now in state 3, check for state 4

                        if (advance_days == pdays) {   // if this is the process day

                           if (curr_time >= ptime) {    // have we reached the process time?

                              lstate = 4;              // after process time
                           }
                        }

                        if (advance_days < pdays) {   // if we are past the process day

                           lstate = 4;                // after process time
                        }
                     }

                     if (lstate == 4) {                // if we are now in state 4, check for state 5

                        PreparedStatement pstmt12 = con.prepareStatement (
                               "SELECT mm FROM lreqs3 " +
                               "WHERE name = ? AND date = ? AND courseName = ? AND state = 2");

                        pstmt12.clearParameters();        // clear the parms
                        pstmt12.setString(1, lname);
                        pstmt12.setLong(2, date);
                        pstmt12.setString(3, course);
                        rs2 = pstmt12.executeQuery();

                        if (!rs2.next()) {             // if none waiting approval

                           lstate = 5;                // state 5 - after process & approval time
                        }
                        pstmt12.close();
                     }

                     submit = "time:" + fb;       // create a name for the submit button

                     //
                     //  Build the HTML for each record found
                     //
                     out.println("<tr>");
                     out.println("<form action=\"Proshop_lott\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                     out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
                     out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
                     out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                     out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");

                     if (fives != 0) {
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                        if (!rest.equals( "" )) {          // if 5-somes are restricted

                           out.println("<input type=\"hidden\" name=\"p5rest\" value=\"Yes\">");  // tell _lott
                        } else {
                           out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                        }
                     } else {
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                        out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                     }

                     out.println("<input type=\"hidden\" name=\"index\" value=888>");  // indicate from searchmain.htm
                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                        out.println("</font></td>");

                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                     if (min < 10) {
                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
                     } else {
                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
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

                        if (fiveSomes != 0) {        // if 5-somes listed - empty slot

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

                           if (fiveSomes != 0) {        // if 5-somes listed - empty slot

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

                           if (fiveSomes != 0) {        // if 5-somes listed - empty slot

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

                           if (fiveSomes != 0) {        // if 5-somes listed - empty slot

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

                           if (fiveSomes != 0) {        // if 5-somes listed - empty slot

                              out.println("<td align=\"center\" bgcolor=\"white\">");
                              out.println("<font size=\"2\">");
                              out.println("nbsp;");
                              out.println("</font></td>");
                           }
                        }
                     }     // end of IF 5-somes
                     out.println("</form></tr>");

                  }    // end of while

                  out.println("</font></table>");

               }    // end of IF no requests

               stmt.close();

            } // end if lottery support



            if (IS_TLT) {

               out.println("<br><br>");

               out.println("<center><font size=\"3\">");
               out.println("<b>Submitted Notification</b><br>");
               out.println("</font></center>");

               out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
                  out.println("<tr bgcolor=\"#336633\"><td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                        out.println("</font></td>");

                  if (multi != 0) {
                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                        out.println("</font></td>");
                  }
   /*
                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                        out.println("</font></td>");
   */
                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                        out.println("</font></td>");

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                        out.println("</font></td>");

                  if (fiveSomes != 0) {
                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                        out.println("</font></td>");
                  }

                     out.println("<td>");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<p align=\"center\"><u><b>N</b></u></p>");
                        out.println("</font></td>");

                     out.println("</tr>");

               sfb = "";
               int nid = 0;

               // Search for Notifications
               stmt = con.prepareStatement (
                   "SELECT n.notification_id, clubparm2.courseName, " + 
                       "DATE_FORMAT(n.req_datetime, '%Y%m%d') AS date, " + 
                       "DATE_FORMAT(n.req_datetime, '%b. %e, %y') AS fdate, " +
                       "DATE_FORMAT(n.req_datetime, '%H%m') AS time, " + 
                       "DATE_FORMAT(n.req_datetime, '%W') AS day, " + 
                       "DATE_FORMAT(n.req_datetime, '%m') AS mm, " + 
                       "DATE_FORMAT(n.req_datetime, '%d') AS dd, " + 
                       "DATE_FORMAT(n.req_datetime, '%Y') AS yy " + 
                   "FROM " + 
                       "notifications n, notifications_players np, clubparm2 " + 
                   "WHERE " + 
                       "n.converted = 0 AND " + 
                       "n.course_id = clubparm2.clubparm_id AND " +
                       "n.notification_id = np.notification_id AND " + 
                       "np.player_name = ? AND " + 
                       "DATE_FORMAT(n.req_datetime, '%Y%m%d') >= DATE(now()) " +
                   "ORDER BY n.req_datetime ASC, pos");


               stmt.clearParameters();
               stmt.setString(1, name);
               rs = stmt.executeQuery();

               while (rs.next()) {

                   day = rs.getString("day");
                   course = rs.getString("courseName");
                   date = rs.getInt("date");
                   mm = rs.getInt("mm");
                   dd = rs.getInt("dd");
                   yy = rs.getInt("yy");
                   time = rs.getInt("time");
                   nid = rs.getInt("notification_id");

                   out.println("<!-- notification_id=" + nid + " -->");

                   PreparedStatement tmp_stmt = con.prepareStatement (
                           "SELECT * FROM notifications_players WHERE notification_id = ?");

                   tmp_stmt.clearParameters();
                   tmp_stmt.setInt(1, nid);
                   ResultSet tmp_rs = tmp_stmt.executeQuery();

                   player1 = " ";
                   player2 = " ";
                   player3 = " ";
                   player4 = " ";
                   player5 = " ";

                   if (tmp_rs.next()) {

                       player1 = tmp_rs.getString("player_name");
                   }
                   if (tmp_rs.next()) {

                       player2 = tmp_rs.getString("player_name");
                   }
                   if (tmp_rs.next()) {

                       player3 = tmp_rs.getString("player_name");
                   }
                   if (tmp_rs.next()) {

                       player4 = tmp_rs.getString("player_name");
                   }
                   if (tmp_rs.next()) {

                       player5 = tmp_rs.getString("player_name");
                   }



                   //
                   //  Build the HTML for each record found
                   //
                   out.println("<tr>");
                   out.println("<form action=\"ProshopTLT_slot\" method=\"get\" target=\"_top\">");
                   out.println("<input type=\"hidden\" name=\"notifyId\" value=" + nid + ">");
                   //out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                   //out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                   //out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + course + "\">");
                   out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
                   if (fives != 0) {
                      out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                      if (!rest5.equals( "" )) {          // if 5-somes are restricted

                         out.println("<input type=\"hidden\" name=\"p5rest\" value=\"Yes\">");  // tell _slot
                      } else {
                         out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                      }
                   } else {
                      out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                   }

                   out.println("<input type=\"hidden\" name=\"index\" value=888>");  // indicate from searchmain.htm
                   out.println("<td align=\"center\">");
                      out.println("<font size=\"2\">");
                      out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                      out.println("</font></td>");

                   out.println("<td align=\"center\">");
                   out.println("<font size=\"2\">");

                   out.println("<input type=\"submit\" value=\"" + SystemUtils.getSimpleTime(time) + "\">");

                   out.println("</font></td></form>");

                   if (multi != 0) {
                      out.println("<td align=\"center\">");
                      out.println("<font size=\"2\">");
                      out.println(course);
                      out.println("</font></td>");
                   }
   /*
                   out.println("<td align=\"center\">");
                   out.println("<font size=\"2\">");
                   out.println(sfb);
                   out.println("</font></td>");
   */
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

                   if (fiveSomes != 0) {
                      out.println("<td align=\"center\" bgcolor=\"white\">");
                      out.println("<font size=\"2\">");
                      out.println( player5 );
                      out.println("</font></td>");
                   }

                   out.println("<td bgcolor=\"white\" align=\"center\">");
                   out.println("<font size=\"2\">");
                   out.println("&nbsp;");
                   out.println("</font></td>");         // end of the col

                   out.println("</tr>");

               } // end while
               
               stmt.close();

               out.println("</table>");

            } // end if TLT
            
            out.println("<br><br>");

         }    // end of IF Golf included in system
         
         
         //
         //*********************************************
         //  Check for any non-Golf Activities
         //*********************************************
         //
         if (sess_activity_id > 0) {          // if this pro is logged into an Activity (id = root activity)

            boolean found = false;
            String user = SystemUtils.getUsernameFromFullName(name, con);

            if (user.equals("")) {

               out.println("<font size=\"3\">");
               out.println("<p align=\"center\">Sorry we were unable to find the username for " + name + ".</p>");
               out.println("</font>");

            } else {

                //
                // use the name to search the activities table
                //
                stmt = con.prepareStatement (
                    "SELECT a.sheet_id, a.activity_id, " +
                       "DATE_FORMAT(a.date_time, '%W, %b. %D') AS pretty_date, " +
                       "DATE_FORMAT(a.date_time, '%Y%m%d') AS dateymd, " +
                       "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time " +
                    "FROM activity_sheets a, activity_sheets_players ap " +
                    "WHERE a.sheet_id = ap.activity_sheet_id " +
                       "AND ap.username = ? " +
                       "AND DATE(a.date_time) >= DATE(now()) " +
                    "ORDER BY a.date_time");

                stmt.clearParameters();        // clear the parms
                stmt.setString(1, user);


                //out.println("<center><font size=\"3\">");
                //out.println("<b>Current Activities</b><br>");
                //out.println("</font></center>");

                rs = stmt.executeQuery();      // execute the prepared stmt - any activities?

                // if we found any then output the header row.
                rs.last();
                if (rs.getRow() > 0) {

                   found = true;

                   out.println("<font size=\"3\">");
                   out.println("<br><b>Scheduled Activities for " + name + "</b></font><br>");
                   out.println("<font size=\"2\"><br>");
                   out.println("<table border=\"0\">");

                }

                rs.beforeFirst();

                while (rs.next()) {

                   out.println("<tr><td align=\"left\"><font size=2>");
                   out.println(getActivity.getFullActivityName(rs.getInt("activity_id"), con));

                   out.println("</font></td><td>&nbsp;</td><td align=\"left\">");
                   out.print("<a href=\"javascript:void(0)\" onclick=\"top.location.href='Proshop_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=998'\"><font size=2 color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><br>");
                   //out.println("&nbsp; &nbsp; <a href=\"javascript:editActSignup(" + rs.getInt("sheet_id") + ", '" + rs.getString("pretty_time") + "')\"><font color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><br>");
                   out.println("</td></tr>");

                }

                if (found) {
                    
                    out.println("</table>");

                } else {

                    out.println("<font size=\"3\">");
                    out.println("<b>No Scheduled Activities Found for " + name + ".</b><br>");
                    out.println("</font>");

                }

                stmt.close();
            }
            
            out.println("<br><br>");

         } // end if Activities
         
         
         
         
         out.println("</td></tr></table>");                // end of main page table 
         out.println("</font>");
         out.println("<font size=\"2\">");

         out.println("<form method=\"get\" action=\"Proshop_searchmem\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");               // return to searchmain.htm
               
         out.println("</font>");

         //
         //  End of HTML page
         //
         out.println("</center></font></body></html>");
         
      } catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_searchmem\">Return</a>");  // return to searchmain.htm
         out.println("</CENTER></BODY></HTML>");

      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { stmt.close(); }
          catch (Exception ignore) {}

      }

   }        // end of call type if

 }   // end of doPost   

 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the last name, or some portion of it.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

}
