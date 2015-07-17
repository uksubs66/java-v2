/***************************************************************************************     
 *   Proshop_lott_history:  This servlet will process the Lottery History reports when
 *                          the Lottery-History menu items are selected.
 *
 *
 *   called by:  proshop menu and self
 *
 *   created: 11/17/2010   Bob P.
 *
 *   last updated:
 *
 *       10/06/11   Modified alphaTable.nameList() calls to pass an additional parameter used for displaying inact members when modifying past tee times.
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
import com.foretees.common.alphaTable;
import com.foretees.common.parmCourse;
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Connect;


public class Proshop_lott_history extends HttpServlet {

                               
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
   
   Connection con = Connect.getCon(req);                     // get DB connection
   
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
   
   // Check Feature Access Rights for current proshop user - use the REPORTS option for this
   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
       return;
   }

   String club = (String)session.getAttribute("club");               // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   // int sess_activity_id = (Integer)session.getAttribute("activity_id");   // get Activity indicator (golf=0)
    
   
   //
   //  Process according to which menu item was selected:
   //
   //     member - user wants a report for an individual member
   //     bydate - user wants a report for a specific day
   //
   if (req.getParameter("member") != null) {     // if member report requested
      
      goMember(lottery, club, req, out, con);  
      
   } else if (req.getParameter("bydate") != null) {     // if date report requested

      goDate(lottery, club, req, out, con);  
   
   } else {
   
       out.println(SystemUtils.HeadTitle("Procedure Error"));
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><BR><H3>Procedure Error</H3>");
       out.println("<BR><BR>Unknown request - invalid entry.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR>");
       out.println("<form method=\"get\" action=\"Proshop_announce\">");
       out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form>");
       out.println("</CENTER></BODY></HTML>");
       out.close();      
   }
   
 }       // end of doGet
   
   
   
 //
 //****************************************************
 // Member Report - Prompt the user for a member name
 //****************************************************
 //
 private void goMember(int lottery, String club, HttpServletRequest req, PrintWriter out, Connection con) {

   
   parmCourse parmc = null;          // must pass one to alphaTable, but can be null
    
   boolean enableAdvAssist = Utilities.enableAdvAssist(req);
   
   //
   //   output the prompt for a member name
   //
   out.println(SystemUtils.HeadTitle2("Proshop Lottery History"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['playerform'].DYN_search.focus(); }");
   out.println("// -->");
   out.println("</script>");
   
   //
   //*******************************************************************
   //  Erase player name (erase button selected next to player's name)
   //
   //    Remove the player's name and shift any other names up starting at player1
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");            // Erase name script
   out.println("<!--");
   out.println("function erasename(pos1) {");
   out.println("document.playerform[pos1].value = '';");     // clear the player field
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   //
   //*******************************************************************
   //  Move a member name into the text slot
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");            // Move name script
   out.println("<!--");
   out.println("function movename(namewc) {");

   out.println("del = ':';");                               // deliminator is a colon
   out.println("array = namewc.split(del);");               // split string into 2 pieces (name, wc)
   out.println("var name = array[0];");
   out.println("var wc = array[1];");

   out.println("var f = document.forms['playerform'];");

   out.println("var player = f.player.value;");

      out.println("f.player.value = name;");     // put player name in text box - overwrite any existing name

   out.println("f.DYN_search.focus();");
   out.println("f.DYN_search.select();");

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");                               // End of script
   
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><br><table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<font size=\"3\"><p><b>Lottery History Report for a Member</b></p></font>");
   out.println("<p>Select the member and a length of time. </p>");
   out.println("</font></td></tr></table><br>");

  // out.println("<p align=center><b>Note:</b> Any requests processed prior to 11/18/2010 will not contain all the data.</p>");
         
   out.println("<form action=\"Proshop_lott_history\" method=\"post\" target=\"bot\" name=\"playerform\">");
   out.println("<input type=\"hidden\" name=\"report_type\" value=\"member\">");               // specify that report_type=member

   out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\"><font size=\"2\">");         
            out.println("<br><br><br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player')\" style=\"cursor:hand\">");
            out.println("Member:&nbsp;&nbsp;<input type=\"text\" name=\"player\" value=\"\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">&nbsp;&nbsp;&nbsp;&nbsp;");
            
            out.println("<br><br><br><p><b>Search the past:</b><br><br>");
            out.println("<input type=\"radio\" name=\"days\" value=\"30\">30 days&nbsp;&nbsp;<BR>");
            out.println("<input type=\"radio\" name=\"days\" value=\"60\">60 days&nbsp;&nbsp;<BR>");
            out.println("<input checked type=\"radio\" name=\"days\" value=\"90\">90 days&nbsp;&nbsp;<BR>");   // use a default
            out.println("<input type=\"radio\" name=\"days\" value=\"120\">120 days<BR>");
            out.println("<input type=\"radio\" name=\"days\" value=\"180\">180 days<BR>");
            out.println("<input type=\"radio\" name=\"days\" value=\"365\">1 year&nbsp;&nbsp;&nbsp;&nbsp;");   
         out.println("</font></td>");
         out.println("<td valign=\"top\" align=\"center\">&nbsp;&nbsp;&nbsp;&nbsp;");

            //
            //   Output the List of Names
            //
            alphaTable.nameList(club, "%", "", "", false, parmc, enableAdvAssist, false, out, con);
         
         //out.println("</td>");   // this is added in alphaTable      
      out.println("</tr>");    
   out.println("</table>");
   
   out.println("<BR><input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   
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
 }               // end of goMember
   
   
   
 //
 //****************************************************
 // By Date Report - Prompt the user for a date
 //****************************************************
 //
 private void goDate(int lottery, String club, HttpServletRequest req, PrintWriter out, Connection con) {

   
   Statement stmt = null;
   ResultSet rs = null;
     
   parmCourse parmc = null;          // must pass one to alphaTable, but can be null
    
   boolean enableAdvAssist = Utilities.enableAdvAssist(req);
   
   int multi = 0;
   
   long old_date = 0;
   long new_date = 0;
   
   //
   // Get the 'Multiple Course' option from the club db
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
      }
      stmt.close();

      //
      //  Get the oldest date with lottery stats
      //
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT MIN(date) " +
                             "FROM lott_stats");

      if (rs.next()) {

         old_date = rs.getLong(1);
      }
      
      //  get the latest date      
      rs = stmt.executeQuery("SELECT MAX(date) " +
                             "FROM lott_stats");

      if (rs.next()) {

         new_date = rs.getLong(1);
      }
      
      stmt.close();

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (old_date == 0) {      // if no entries found
      
      out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H3>Data Not Available</H3><BR>");
      out.println("<BR><BR>Sorry, there has not been any history data collected yet.<BR>");
      out.println("Please try again after a lottery has been processed.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   

   //
   //  Determine oldest and newest date values - month, day, year
   //
   int old_yy = (int)old_date / 10000;
   int old_mm = (int)(old_date - (old_yy * 10000)) / 100;
   int old_dd = (int)old_date - ((old_yy * 10000) + (old_mm * 100));
   
   int new_year = (int)new_date / 10000;
   int new_month = (int)(new_date - (new_year * 10000)) / 100;
   int new_day = (int)new_date - ((new_year * 10000) + (new_month * 100));
   
   //
   //   output the prompt for a member name
   //
   out.println(SystemUtils.HeadTitle2("Proshop Lottery History"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

   // include files for dynamic calendars
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
   out.println("<script language=\"javascript\" src=\"/" +rev+ "/cal-scripts-old.js\"></script>");

   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><br><table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<font size=\"3\"><p><b>Lottery History for a Specific Day</b></p></font>");
   out.println("<p>Select the desired date below.</p>");
   out.println("</font></td></tr></table><br>");

   //out.println("<p align=center><b>Note:</b> Any requests processed prior to 11/18/2010 will not contain all the data.</p>");
         
   //out.println("<p align=center>old_date = " +old_date+ " new_date = " +new_date+ "</p>");
         
   out.println("<form action=\"Proshop_lott_history\" method=\"post\" target=\"bot\" name=\"frmLoadDay\">");
   out.println("<input type=\"hidden\" name=\"report_type\" value=\"bydate\">");               // specify that report_type=bydate
   out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");              // value set by script
   out.println("<input type=\"hidden\" name=\"old_mm\" value=\"" +old_mm+ "\">");   // oldest month (for js)
   out.println("<input type=\"hidden\" name=\"old_dd\" value=\"" +old_dd+ "\">");   // oldest day
   out.println("<input type=\"hidden\" name=\"old_yy\" value=\"" +old_yy+ "\">");   // oldest year

   out.println("</form>");
   out.println("</font>");

   //  Calendar
  
   out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");  

   out.println(" <div id=calendar0 style=\"width: 180px\"></div>");

   out.println("</td></tr>\n</table>");

   out.println("<script type=\"text/javascript\">");
   out.println("var iEndingDay = " + new_day + ";");
   out.println("var iEndingYear = " + new_year + ";");
   out.println("var iEndingMonth = " + new_month + ";");
   out.println("var iStartingMonth = " + old_mm + ";");
   out.println("var iStartingDay = " + old_dd + ";");
   out.println("var iStartingYear = " + old_yy + ";");
   if (new_day == 0) {
       new_month--;
       if (new_month == 0) { new_month = 12; new_year--; }
   }
   out.println("doCalendar('" + new_month + "', '" + new_year + "');");
   out.println("</script>");
   
   
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }               // end of goDate


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
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

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
   
   String club = (String)session.getAttribute("club");           // get club name
   String temp = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(temp);

   // int sess_activity_id = (Integer)session.getAttribute("activity_id");   // get Activity indicator (golf=0)

   boolean enableAdvAssist = Utilities.enableAdvAssist(req);
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only report

   boolean excel = false;
    
   if (req.getParameter("excel") != null) {
       
      excel = true;
   }
       
   //
   //   Get multi option
   //
   try {

      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception ignore) {
   }

   
   //
   // Process request according to which report was selected
   //
   //   report_type =
   //
   //      'member' - a lottery history report for 1 member
   //      'bydate' - a lottery history report for 1 day
   //
   //   if bydate then
   //
   //      summary report (default) or
   //      detailed report (detailed = yes)
   //
   // Get the parameters entered
   //
   String report_type = req.getParameter("report_type");  
   
   if (report_type.equals("bydate")) {
      
      //if (req.getParameter("detailed") != null) {   // if detailed report
         
          // Decided to put all info in one report    
      
      //} else {        // summary report
              
         goSummary(lottery, excel, club, req, resp, out, con);  
   
      //}
      
   } else if (report_type.equals("member")) {
      
      //
      //  parm block to hold the name so we can use existing methods in verifySlot
      //
      parmSlot slotParms = new parmSlot();         

      String member = "";
      String user = "";
      String lname = "";
      String course_req = "";
      String course_assign = "";
      String errorMsg = "";
      
      int days = 0;
      int count = 0;
      int time_req = 0;
      int time_assign = 0;
      int mins = 0;
      int grp_weight = 0;
      int weight = 0;
      
      int date = 0;
      
      boolean error = false;
      
      if (req.getParameter("player") != null) {
       
         member = req.getParameter("player");  
         
      } else {
         
         errorMsg = "Member not selected.  You must select a member from the name list.";
      }
   
      if (req.getParameter("days") != null) {
       
         temp = req.getParameter("days");  
         
      } else {
         
         errorMsg = "Number of days not selected.  You must select the number of days for the report.";
      }
         
      if (!errorMsg.equals("")) {       // if not found
           
         out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>Invalid Entry</H3><BR>");
         out.println("<BR><BR>Some data is missing.<BR>");
         out.println("<b>" +errorMsg+ "</b>");
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
      
      
      days = Integer.parseInt(temp);      // number of days to go back

      slotParms.player1 = member;         // init the player fields for verifySlot
      slotParms.player = "";
      slotParms.player2 = "";
      slotParms.player3 = "";
      slotParms.player4 = "";
      slotParms.player5 = "";
      slotParms.user1 = "";
      
      //
      //  Parse the name to separate first, last & mi
      //
      try {

         error = verifySlot.parseNames(slotParms, "mem");

      }
      catch (Exception ignore) {
      }

      if ( error == false) {         // if ok

         //
         //  Get the username if matching name found
         //
         try {

            verifySlot.getUsers(slotParms, con);

         }
         catch (Exception ignore) {
         }

         user = slotParms.user1;     // get the username of the member 
      
      }

      if (user.equals("") || error == true) {       // if not found
           
         out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>Member Not Found</H3><BR>");
         out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
         out.println("The name <b>" +member+ "</b> was not found in the active member roster.");
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
      
      //
      //  Determine the start date for the query
      //
      int cdays = 0 - days;                             // create negative number

      Calendar cal = new GregorianCalendar();          // get todays date
      cal.add(Calendar.DATE,cdays);                  // roll back 'days' days
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) +1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long sdate = (year * 10000) + (month * 100) + day;
      
      //
      //  See if there are any entries for this member in the specified date range
      //
      try {
      
         stmt = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM lassigns5 " +
            "WHERE username = ? AND date >= ?");

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, user);
         stmt.setLong(2, sdate);

         rs = stmt.executeQuery();     

         if (rs.next()) {              // any entries?

            count = rs.getInt(1);
         }
         stmt.close();

      } catch (Exception exc) {

         count = 0;
         
      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { stmt.close(); }
          catch (Exception ignore) {}
      }
      
      if (count == 0) {
         
         out.println(SystemUtils.HeadTitle("Not found - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>No Entries Found</H3><BR>");
         out.println("<BR><BR>Sorry, there are no entries for the name and date range you entered.<BR>");
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
      
      if (excel == true) {     // if user requested Excel Spreadsheet Format

         try{
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\"" + club + ".xls\"");
         }
         catch (Exception ignore) {
         }
      }

      //
      //  Prepare the report for this member
      //
      if (excel == false) {
         out.println(SystemUtils.HeadTitle("Proshop Member Search Page"));
      }
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      if (excel == false) {
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      }
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\" valign=\"top\"><BR><BR>");
      out.println("<font size=\"3\">");
      out.println("Tee Time Request History for <b>" +member+ "</b> for the past " +days+ " days.<br>");
      out.println("</font><font size=\"2\">(There were " +count+ " requests processed during this period.)<br><br>");

      out.println("<b>Diff</b> = difference between time requested and time assigned - in minutes<BR><BR>");

      //out.println("<b>Note:</b> Any requests processed prior to 11/18/2010 will not contain all the data.<BR><BR>");
         
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<tr bgcolor=\"#336633\">");
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Lottery Name</b></p>");
               out.println("</font></td>");
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Date</b></p>");
               out.println("</font></td>");
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Time Req'd</b></p>");
               out.println("</font></td>");
            if (parm.multi != 0) {
               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>Course Req'd</b></p>");
                  out.println("</font></td>");
            }
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Time Assigned</b></p>");
               out.println("</font></td>");
            if (parm.multi != 0) {
               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>Course Assigned</b></p>");
                  out.println("</font></td>");
            }
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Diff</b></p>");
               out.println("</font></td>");
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Grp Weight</b></p>");
               out.println("</font></td>");
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Mem Weight</b></p>");
               out.println("</font></td>");
         out.println("</tr>");

         //
         //  See if there are any entries for this member in the specified date range
         //
         try {

            stmt = con.prepareStatement (
               "SELECT * " +
               "FROM lassigns5 " +
               "WHERE username = ? AND date >= ? " +
               "ORDER BY date, time_req");

            stmt.clearParameters();        // clear the parms
            stmt.setString(1, user);
            stmt.setLong(2, sdate);

            rs = stmt.executeQuery();     

            while (rs.next()) {          

               lname = rs.getString("lname");
               date = rs.getInt("date");
               mins = rs.getInt("mins");
               time_req = rs.getInt("time_req");
               course_req = rs.getString("course_req");
               time_assign = rs.getInt("time_assign");
               course_assign = rs.getString("course_assign");
               weight = rs.getInt("weight");
               grp_weight = rs.getInt("grp_weight");
               
               if (course_req.equals( "" )) {
                  course_req = " ";       // make it a space for table display
               }
               if (course_assign.equals( "" )) {
                  course_assign = " ";       // make it a space for table display
               }
               
               //
               //  Break down the date
               //
               int yy = date / 10000;
               int mm = (date - (yy * 10000)) / 100;
               int dd = (date - (yy * 10000)) - (mm * 100);
               
               
               out.println("<tr>");
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +lname+ "</p></font>");
               out.println("</td>");
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +mm + "/" + dd + "/" + yy+ "</p></font>");
               out.println("</td>");
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +time_req+ "</p></font>");
               out.println("</td>");
               if (parm.multi != 0) {
                  out.println("<td>");
                  out.println("<font size=\"2\"><p align=\"center\">" +course_req+ "</p></font>");
                  out.println("</td>");
               }
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +time_assign+ "</p></font>");
               out.println("</td>");
               if (parm.multi != 0) {
                  out.println("<td>");
                  out.println("<font size=\"2\"><p align=\"center\">" +course_assign+ "</p></font>");
                  out.println("</td>");
               }
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +mins+ "</p></font>");
               out.println("</td>");
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +grp_weight+ "</p></font>");
               out.println("</td>");
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +weight+ "</p></font>");
               out.println("</td>");
               out.println("</tr>");
            }
            stmt.close();
      
         } catch (Exception e) {

            out.println("<BR><BR>Error encountered while gathering the history.<br>");
            out.println("Please inform ForeTees Pro Support.<br><br>");

            SystemUtils.logError("Error in Proshop_lott_history.doPost for club " +club+ ". Exception = " + e.getMessage());   // log it and continue
      
         } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { stmt.close(); }
             catch (Exception ignore) {}
         }
         
         out.println("</table><br>");                // end of report table 

         
      out.println("</font></td></tr></table>");                // end of main page table 

      if (excel == false) {
         
         out.println("<BR><BR><table border=\"0\" align=\"center\" width=\"200\">");
         out.println("<tr><td align=\"center\">");
         
         out.println("<form method=\"get\" action=\"Proshop_lott_history\">");
         out.println("<input type=\"hidden\" name=\"member\" value=\"yes\">");               // specify that report_type=member
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");               // return to searchmain.htm       
         out.println("</td><td align=\"center\">");             
         out.println("<form method=\"post\" action=\"Proshop_lott_history\" target=\"_blank\">");
         out.println("<input type=\"hidden\" name=\"report_type\" value=\"member\">");               // specify that report_type=member
         out.println("<input type=\"hidden\" name=\"player\" value=\"" +member+ "\">");           
         out.println("<input type=\"hidden\" name=\"days\" value=\"" +days+ "\">");           
         out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");           
         out.println("<input type=\"submit\" value=\"Export to Excel\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");               // return to searchmain.htm
         
         out.println("</td></tr></table>");             
      }

      out.println("</center></font></body></html>");
      out.close();     
   }
   
 }   // end of doPost   
 
 
 
 //
 //****************************************************
 // By Date Summary Report
 //****************************************************
 //
 private void goSummary(int lottery, boolean excel, String club, HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con) {

    
   PreparedStatement stmt = null;
   PreparedStatement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   int year = 0;
   int month = 0;
   int day = 0;
   int count = 0;
   int mins = 0;
   int lowdev = 0;
   int highdev = 0;
   int avgdev = 0;
   int totdev = 0;
   int reqs = 0;
   int tees = 0;
   int multi = 0;
   int prev_id = 0;
   int bgindex = 0;
    
   long date = 0;

   String num = "";
   String calDate = "";
   String sort_by = "dev";
   String sort_by_query = "mins, lreq_id, time_req";
  
   String [] bgcolorA = { "#CCCCAA", "#F5F5DC" };
   

   //
   //  Get the parms passed and build a date field to search for (yyyymmdd)
   //  
   if (req.getParameter("calDate") != null) {

      calDate = req.getParameter("calDate");

      //
      //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
      //
      StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

      num = tok.nextToken();                   // get the mm value

      month = Integer.parseInt(num);

      num = tok.nextToken();                   // get the dd value

      day = Integer.parseInt(num);

      num = tok.nextToken();                   // get the yyyy value

      year = Integer.parseInt(num);

      date = year * 10000;                     // create a date field of yyyymmdd
      date = date + (month * 100);
      date = date + day;                       // date = yyyymmdd (for comparisons)
   }
   
   if (req.getParameter("sort_by") != null) {
       sort_by = req.getParameter("sort_by");
   }
   
   if (sort_by.equals("lreq_id")) {
       sort_by_query = "lreq_id, time_req";
   } else {
       sort_by_query = "mins, lreq_id, time_req";
   }
   
   
   //
   //  First, lets see if there is any data for this date
   //
   try {

      Statement stmt1 = con.createStatement();        // create a statement

      rs = stmt1.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
      }
      stmt1.close();

      
      stmt = con.prepareStatement (
         "SELECT COUNT(*) " +
         "FROM lassigns5 " +
         "WHERE date = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setLong(1, date);

      rs = stmt.executeQuery();     

      if (rs.next()) {              // any entries?

         count = rs.getInt(1);
      }
      stmt.close();
      
      if (count == 0) {
      
         stmt = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM lott_stats " +
            "WHERE date = ?");

         stmt.clearParameters();        // clear the parms
         stmt.setLong(1, date);

         rs = stmt.executeQuery();     

         if (rs.next()) {              // any entries?

            count = rs.getInt(1);
         }
         stmt.close();
      }

   } catch (Exception exc) {

      count = 0;

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { stmt.close(); }
       catch (Exception ignore) {}
   }

   if (count == 0) {

      out.println(SystemUtils.HeadTitle("Not found - Reject"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H3>No Entries Found</H3><BR>");
      out.println("<BR><BR>Sorry, there are no entries for the name and date range you entered.<BR>");
      out.println("<BR><BR>");
      out.println("<form method=\"get\" action=\"Proshop_lott_history\">");
      out.println("<input type=\"hidden\" name=\"bydate\" value=\"yes\">");               // specify that report_type=bydate
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");               // return to searchmain.htm       
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   
   if (excel == true) {     // if user requested Excel Spreadsheet Format

      try{
         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
         resp.setHeader("Content-Disposition", "attachment;filename=\"" + club + ".xls\"");
      }
      catch (Exception ignore) {
      }
   }

   //
   //  Gather the summary stats for this date
   //
   count = 0;
   
   try {

      stmt = con.prepareStatement (
         "SELECT mins " +
         "FROM lassigns5 " +
         "WHERE date = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setLong(1, date);

      rs = stmt.executeQuery();     

      while (rs.next()) {          

         mins = rs.getInt("mins");

         totdev += mins;       // add to total
         
         if (mins < lowdev) {
            
            lowdev = mins;
         }
         if (mins > highdev) {
            
            highdev = mins;
         }
         count++;
      }
      stmt.close();
      
      if (count > 0) {
         
         avgdev = totdev/count;
      }
      
      stmt = con.prepareStatement (
         "SELECT requests, teetimes " +
         "FROM lott_stats " +
         "WHERE date = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setLong(1, date);

      rs = stmt.executeQuery();     

      while (rs.next()) {          

         count = rs.getInt("requests");

         reqs += count;       // add to total requests
         
         count = rs.getInt("teetimes");

         tees += count;       // add to total tee times
      }
      stmt.close();

   } catch (Exception e) {

      SystemUtils.logError("Error in Proshop_lott_history.goSummary for club " +club+ ". Exception = " + e.getMessage()); 

      out.println(SystemUtils.HeadTitle("Not found - Reject"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H3>Data Access Error</H3><BR>");
      out.println("<BR><BR>Error encountered while gathering the history.<br>");
      out.println("Please inform ForeTees Pro Support.<br><br>");
      out.println("<BR><BR>");
      out.println("<form method=\"get\" action=\"Proshop_lott_history\">");
      out.println("<input type=\"hidden\" name=\"bydate\" value=\"yes\">");               // specify that report_type=bydate
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");               // return to searchmain.htm       
      out.println("</CENTER></BODY></HTML>");
      return;

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { stmt.close(); }
       catch (Exception ignore) {}
   }
         
   
   
   //
   //  Prepare the Summary report for this date
   //
   if (excel == false) {
      out.println(SystemUtils.HeadTitle("Proshop Lottery History Page"));
   }
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   if (excel == false) {
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   }
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\" valign=\"top\"><BR><BR>");
   out.println("<font size=\"3\">");
   out.println("<b>Tee Time Request History for " +calDate+ "</b>.<br><br>");

   if (excel == false) {

      out.println("<table border=\"0\" align=\"center\" width=\"200\">");
      out.println("<tr><td align=\"center\">");
      out.println("<form method=\"get\" action=\"Proshop_lott_history\">");
      out.println("<input type=\"hidden\" name=\"bydate\" value=\"yes\">");               // specify that report_type=bydate
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");               // return to searchmain.htm       
      out.println("</td><td align=\"center\">");             
      out.println("<form method=\"post\" action=\"Proshop_lott_history\" target=\"_blank\">");
      out.println("<input type=\"hidden\" name=\"report_type\" value=\"bydate\">");               // specify that report_type=member
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
      out.println("<input type=\"hidden\" name=\"sort_by\" value=\"" + sort_by + "\">");
      out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");           
      out.println("<input type=\"submit\" value=\"Export to Excel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");               // return to searchmain.htm
      out.println("</td></tr></table><br>");             
   }

   //
   //   Summary table
   //
   out.println("<b>Summary For This Date</b><br>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
      out.println("<tr bgcolor=\"#336633\">");
      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><b># of Requests</b></p>");
         out.println("</font></td>");
      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><b>Available Tee Times</b></p>");
         out.println("</font></td>");
      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><b>Low Deviation</b></p>");
         out.println("</font></td>");
      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><b>High Deviation</b></p>");
         out.println("</font></td>");
      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><b>Avg Deviation</b></p>");
         out.println("</font></td>");
      out.println("</tr>");
      
      out.println("<tr>");
      out.println("<td>");
      out.println("<font size=\"2\"><p align=\"center\">" +reqs+ "</p></font>");
      out.println("</td>");
      out.println("<td>");
      out.println("<font size=\"2\"><p align=\"center\">" +tees+ "</p></font>");
      out.println("</td>");
      out.println("<td>");
      out.println("<font size=\"2\"><p align=\"center\">" +lowdev+ "</p></font>");
      out.println("</td>");
      out.println("<td>");
      out.println("<font size=\"2\"><p align=\"center\">" +highdev+ "</p></font>");
      out.println("</td>");
      out.println("<td>");
      out.println("<font size=\"2\"><p align=\"center\">" +avgdev+ "</p></font>");
      out.println("</td>");
      out.println("</tr>");
   out.println("</table><br><br>");                // end of summary table 

   out.println("</font><font size=\"2\">");
   out.println("<b>Dev</b> = Deviation (difference, in minutes, from the time requested and time assigned).<br>");
   out.println("<b>Grp Weight</b> = The weight determined for the entire group at the time this draw was processed.<br>");
   out.println("<b>Mem Weight</b> = The weight determined for this individual member at the time this draw was processed.<br><br>");
   out.println("</font><font size=\"3\">");
   
   out.println("<table style=\"border: 0x; bgcolor: #FFFFFF; margin-left: auto; margin-right: auto;\"><tr>");
   out.println("<td>");
   out.println("<form action=\"Proshop_lott_history\" method=\"post\" target=\"bot\" name=\"frmLoadDay\">");
   out.println("<input type=\"hidden\" name=\"report_type\" value=\"bydate\">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
   out.println("<input type=\"hidden\" name=\"sort_by\" value=\"dev\">");
   out.println("<input type=\"submit\" value=\"Sort By Deviation\">");
   out.println("</form>");
   out.println("</td>");
   out.println("<td>");
   out.println("<form action=\"Proshop_lott_history\" method=\"post\" target=\"bot\" name=\"frmLoadDay\">");
   out.println("<input type=\"hidden\" name=\"report_type\" value=\"bydate\">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
   out.println("<input type=\"hidden\" name=\"sort_by\" value=\"lreq_id\">");
   out.println("<input type=\"submit\" value=\"Sort By Request\">");
   out.println("</form>");
   out.println("</td>");
   out.println("</tr></table><br>");

         
   //
   //   Individual Member Report table
   //
   out.println("<b>Individual Requests (background color indicates groupings)</b><br>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
      out.println("<tr bgcolor=\"#336633\">");
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><b>Lottery Name</b></p>");
            out.println("</font></td>");
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><b>Member</b></p>");
            out.println("</font></td>");
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><b>Time Req'd</b></p>");
            out.println("</font></td>");
         if (multi != 0) {
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Course Req'd</b></p>");
               out.println("</font></td>");
         }
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><b>Time Assigned</b></p>");
            out.println("</font></td>");
         if (multi != 0) {
            out.println("<td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"center\"><b>Course Assigned</b></p>");
               out.println("</font></td>");
         }
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><b>Dev</b></p>");
            out.println("</font></td>");
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><b>Grp Weight</b></p>");
            out.println("</font></td>");
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><b>Mem Weight</b></p>");
            out.println("</font></td>");
      out.println("</tr>");

      //
      //  See if there are any entries for this member in the specified date range
      //
      try {

         stmt = con.prepareStatement (
            "SELECT * " +
            "FROM lassigns5 " +
            "WHERE date = ? " +
            "ORDER BY " + sort_by_query);

         stmt.clearParameters();        // clear the parms
         stmt.setLong(1, date);

         rs = stmt.executeQuery();     

         while (rs.next()) {          

            String username = rs.getString("username");
            String lname = rs.getString("lname");
            mins = rs.getInt("mins");
            int time_req = rs.getInt("time_req");
            String course_req = rs.getString("course_req");
            int time_assign = rs.getInt("time_assign");
            String course_assign = rs.getString("course_assign");
            int weight = rs.getInt("weight");
            int grp_weight = rs.getInt("grp_weight");
            int lreq_id = rs.getInt("lreq_id");
            
            String name = "";

            stmt2 = con.prepareStatement (
               "SELECT name_last, name_first, name_mi " +
               "FROM member2b " +
               "WHERE username = ?");

            stmt2.clearParameters();        // clear the parms
            stmt2.setString(1, username);
            rs2 = stmt2.executeQuery();     

            if (rs2.next()) {          
               
               StringBuffer mem_name = new StringBuffer(rs2.getString("name_first"));  // get first name

               String mi = rs2.getString("name_mi");                                // middle initial
               if (!mi.equals( "" )) {
                  mem_name.append(" ");
                  mem_name.append(mi);
               }
               mem_name.append(" " + rs2.getString("name_last"));                     // last name

               name = mem_name.toString();                          // convert to one string
            }
            stmt2.close();

            if (course_req.equals( "" )) {
               course_req = " ";       // make it a space for table display
            }
            if (course_assign.equals( "" )) {
               course_assign = " ";       // make it a space for table display
            }
            
            if (lreq_id != prev_id) {      // if new group
               
               if (bgindex == 0) {         // toggle the bg color for this row(s)                
                  bgindex = 1;                
               } else {
                  bgindex = 0;
               }       
               prev_id = lreq_id;
            }

            out.println("<tr bgcolor=\"" +bgcolorA[bgindex]+ "\">");
            out.println("<td>");
            out.println("<font size=\"2\"><p align=\"center\">" +lname+ "</p></font>");
            out.println("</td>");
            out.println("<td>");
            out.println("<font size=\"2\"><p align=\"center\">" +name+ "</p></font>");
            out.println("</td>");
            out.println("<td>");
            out.println("<font size=\"2\"><p align=\"center\">" +time_req+ "</p></font>");
            out.println("</td>");
            if (multi != 0) {
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +course_req+ "</p></font>");
               out.println("</td>");
            }
            out.println("<td>");
            out.println("<font size=\"2\"><p align=\"center\">" +time_assign+ "</p></font>");
            out.println("</td>");
            if (multi != 0) {
               out.println("<td>");
               out.println("<font size=\"2\"><p align=\"center\">" +course_assign+ "</p></font>");
               out.println("</td>");
            }
            out.println("<td>");
            out.println("<font size=\"2\"><p align=\"center\">" +mins+ "</p></font>");
            out.println("</td>");
            out.println("<td>");
            out.println("<font size=\"2\"><p align=\"center\">" +grp_weight+ "</p></font>");
            out.println("</td>");
            out.println("<td>");
            out.println("<font size=\"2\"><p align=\"center\">" +weight+ "</p></font>");
            out.println("</td>");
            out.println("</tr>");
         }
         stmt.close();

      } catch (Exception e) {

         out.println("<BR><BR>Error encountered while gathering the history.<br>");
         out.println("Please inform ForeTees Pro Support.<br><br>");

         SystemUtils.logError("Error in Proshop_lott_history.goSummary for club " +club+ ". Exception = " + e.getMessage());   // log it and continue

      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { stmt.close(); }
          catch (Exception ignore) {}
      }

   out.println("</table><br>");                // end of report table 

         
   out.println("</font></td></tr></table>");                // end of main page table 

   if (excel == false) {

      out.println("<BR><table border=\"0\" align=\"center\" width=\"200\">");
      out.println("<tr><td align=\"center\">");
      out.println("<form method=\"get\" action=\"Proshop_lott_history\">");
      out.println("<input type=\"hidden\" name=\"bydate\" value=\"yes\">");               // specify that report_type=bydate
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");               // return to searchmain.htm       
      out.println("</td><td align=\"center\">");             
      out.println("<form method=\"post\" action=\"Proshop_lott_history\" target=\"_blank\">");
      out.println("<input type=\"hidden\" name=\"report_type\" value=\"bydate\">");               // specify that report_type=member
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");           
      out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");           
      out.println("<input type=\"submit\" value=\"Export to Excel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");               // return to searchmain.htm
      out.println("</td></tr></table>");             
   }

   out.println("</center></font></body></html>");
   out.close();     
    
 }
 
}
