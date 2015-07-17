/***************************************************************************************     
 *   Proshop_editlottery:  This servlet will process the 'Edit Lottery' request from
 *                         the Proshop's Lottery page.
 *
 *
 *   called by:  Proshop_lottery (via doPost in HTML built by Proshop_lottery)
 *
 *   created: 7/22/2003   Bob P.
 *
 *   last updated:
 *
 *       10/10/13   Add max days for recurrence options.
 *        9/05/13   Add options to allow proshop users and members to recur the lottery requests.
 *        1/08/12   Limit the X's allowed to 0-4 per tee time request (case 2104).
 *        2/23/12   Custom for Oakland Hills CC to add a 'max # of members' setting to limit how many members per request (case 2119).
 *        3/23/11   Add options for allowing X's in requests (cases 1202 & 1941).
 *       11/23/10   Change the order that we process the date and time for processing a lottery - do date first so we can determine if it is in DST or not.
 *        8/12/08   Added limited access proshop users checks
 *        4/22/08   Add mins-before and mins-after options (case #1459).
 *        6/22/06   Comment out the option to give preference to full groups (we don't do this).
 *        6/06/05   Add lottery type of 'Weighted by Proximity'.
 *        5/12/04   Add sheet= parm for call from Proshop_sheet.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.dateOpts;
import com.foretees.common.Connect;

public class Proshop_editlottery extends HttpServlet {

                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************************
 // Process the form request from Proshop_lottery page.
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   //
   // Process request according to which 'submit' button was selected
   //
   //      Update - update the record - prompt user for conf
   //      Remove - delete the record - prompt user for conf
   //      Yes (value = update) - process the update confirmation
   //      Delete (value = remove) - process the delete confirmation
   //
   if (req.getParameter("Update") != null) {

      updateConf(req, out, session);

   } else {

      if (req.getParameter("Remove") != null) {

         removeConf(req, out);

      } else {

         if (req.getParameter("Delete") != null) {

            removelottery(req, out, session);

         } else {

            if (req.getParameter("Yes") != null) {

               updatelottery(req, out, session, resp);

            }
         }
      }
   }
 }   // end of doPost


   //*************************************************
   //  updateConf - Request a confirmation from user
   //*************************************************

 private void updateConf(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   ResultSet rs = null;
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

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_LOTTERY", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_LOTTERY", out);
   }
     
   //
   //   get name of club for this user
   //
   String club = (String)sess.getAttribute("club");      // get club name
   
   
   //
   // Get the other parameters entered
   //
   String name = req.getParameter("lottery_name");
   String oldName = req.getParameter("oldName");
   String smonth = req.getParameter("smonth");        
   String sday = req.getParameter("sday");      
   String syear = req.getParameter("syear");    
   String emonth = req.getParameter("emonth");
   String eday = req.getParameter("eday");
   String eyear = req.getParameter("eyear");
   String shr = req.getParameter("start_hr");      
   String smin = req.getParameter("start_min");    
   String sampm = req.getParameter("start_ampm");  
   String ehr = req.getParameter("end_hr");      
   String emin = req.getParameter("end_min");    
   String eampm = req.getParameter("end_ampm");     
   String recurr = req.getParameter("recurr");
   String color = req.getParameter("color");
   String course = req.getParameter("course");
   String oldCourse = req.getParameter("oldCourse");
   String fb = req.getParameter("fb");
   String ssdays = req.getParameter("sdays");
   String ssd_hr = req.getParameter("sd_hr");
   String ssd_min = req.getParameter("sd_min");
   String sdampm = req.getParameter("sdampm");
   String sedays = req.getParameter("edays");
   String sed_hr = req.getParameter("ed_hr");
   String sed_min = req.getParameter("ed_min");
   String edampm = req.getParameter("edampm");
   String spdays = req.getParameter("pdays");
   String sp_hr = req.getParameter("p_hr");
   String sp_min = req.getParameter("p_min");
   String pampm = req.getParameter("pampm");
   String type = req.getParameter("type");
   String adays = req.getParameter("adays");
   String wdpts = req.getParameter("wdpts");
   String wepts = req.getParameter("wepts");
   String evpts = req.getParameter("evpts");
   String gpts = req.getParameter("gpts");
   String nopts = req.getParameter("nopts");
   String sselection = req.getParameter("selection");
   String sguest = req.getParameter("guest");
   String slots = req.getParameter("slots");
   String smembers = req.getParameter("members");
   String splayers = req.getParameter("players");
   String approve = req.getParameter("approve");
   String sheet = req.getParameter("sheet");
   String oldpdays = req.getParameter("oldpdays");
   String oldptime = req.getParameter("oldptime");
   String allowmins = req.getParameter("allowmins");
   String minsbefore = req.getParameter("minsbefore");
   String minsafter = req.getParameter("minsafter");

   String pref = "No";

   if (req.getParameter("pref") != null) {

      pref = req.getParameter("pref");
   }

   String maxmems = "0";
   
   if (req.getParameter("maxmems") != null) {

      maxmems = req.getParameter("maxmems");      // custom parm for Oakland Hills
   }

   
   String temp = "";
   String allow_x = "";
   int x_value = 0;
    
   if (req.getParameter("allowx") != null) {     

      allow_x = req.getParameter("allowx");
   }

   if (req.getParameter("xvalue") != null) {     

      temp = req.getParameter("xvalue");
   
      try {
           x_value = Integer.parseInt(temp);
      } catch (NumberFormatException ignore) {}
   }

   String recurrpro = "No";
   
   if (req.getParameter("recurrpro") != null) {     

      recurrpro = req.getParameter("recurrpro");
   }

   String recurrmem = "No";

   if (req.getParameter("recurrmem") != null) {     

      recurrmem = req.getParameter("recurrmem");
   }

   String recur_days = "0";

   if (req.getParameter("recur_days") != null) {     

      recur_days = req.getParameter("recur_days");
   }

   
   String ssampm = "AM";
   String seampm = "AM";
   String ssdampm = "AM";
   String sedampm = "AM";
   String spampm = "AM";

   int s_hr = 0;
   int e_hr = 0;
   int sd_hr = 0;
   int ed_hr = 0;
   int p_hr = 0;
   int s_min = 0;
   int e_min = 0;
   int sd_min = 0;
   int ed_min = 0;
   int p_min = 0;
   int selection = 0;
   int guest = 0;
   int sdays = 0;
   int edays = 0;
   int pdays = 0;
   int d2ampm = 0;
   int p2ampm = 0;
   int members = 0;
   int players = 0;
   int mins_before = 0;
   int mins_after = 0;

   if (sampm.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
      ssampm = "PM";
   }

   if (eampm.equals ( "12" )) {
      seampm = "PM";
   }

   if (sdampm.equals ( "12" )) {
      ssdampm = "PM";
   }

   if (edampm.equals ( "12" )) {
      sedampm = "PM";
   }

   if (pampm.equals ( "12" )) {
      spampm = "PM";
   }

   try {
      s_hr = Integer.parseInt(shr);
      e_hr = Integer.parseInt(ehr);
      sd_hr = Integer.parseInt(ssd_hr);
      ed_hr = Integer.parseInt(sed_hr);
      p_hr = Integer.parseInt(sp_hr);
      s_min = Integer.parseInt(smin);
      e_min = Integer.parseInt(emin);
      sd_min = Integer.parseInt(ssd_min);
      ed_min = Integer.parseInt(sed_min);
      p_min = Integer.parseInt(sp_min);
      sdays = Integer.parseInt(ssdays);
      edays = Integer.parseInt(sedays);
      pdays = Integer.parseInt(spdays);
      selection = Integer.parseInt(sselection);
      guest = Integer.parseInt(sguest);
      d2ampm = Integer.parseInt(edampm);
      p2ampm = Integer.parseInt(pampm);
      members = Integer.parseInt(smembers);
      players = Integer.parseInt(splayers);
      mins_before = Integer.parseInt(minsbefore);
      mins_after = Integer.parseInt(minsafter);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  create time values for verification
   //
   int ed2_hr = ed_hr;
   int p2_hr = p_hr;
     
   if (ed2_hr != 12) {                // time to stop taking requests

      ed2_hr = ed2_hr + d2ampm;
   }

   if (ed2_hr == 12 && d2ampm == 0) {

      ed2_hr = 0;
   }

   if (p2_hr != 12) {                // ditto for time to process requests

      p2_hr = p2_hr + p2ampm;
   }

   if (p2_hr == 12 && p2ampm == 0) {

      p2_hr = 0;
   }

   int edtime = (ed2_hr * 100) + ed_min;
   int ptime = (p2_hr * 100) + p_min;

   //
   //  Validate # of members per request & # of players per request
   //
   if (members > players) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Number of Members per Request cannot be greater than the Number of Players per Request.");
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Validate minute values
   //
   if ((s_min < 0) || (s_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Start Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + s_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((e_min < 0) || (e_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>End Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + e_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((sd_min < 0) || (sd_min > 59) || (ed_min < 0) || (ed_min > 59) || (p_min < 0) || (p_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Minute values must be in the range of 00 - 59.");
      out.println("<BR>Please correct the parameter and try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  verify the required fields (name reqr'ed - rest are automatic)
   //
   if (name.equals( "" )) {

      String errMsg = "The name field is required.";
      invData(errMsg, out);    // inform the user and return
      return;
   }

   //
   //  Verify the lottery advance day values
   //
   if ((edays >= sdays) || (pdays > edays)) {

      invData2(out);    // inform the user and return
      return;
   }

   if ((pdays == edays) && (ptime <= edtime)) {

      invData3(out);    // inform the user and return
      return;
   }

   //
   //  Validate new name if it has changed
   //
   boolean error = SystemUtils.scanQuote(name);           // check for single quote

   if (error == true) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Single quotes cannot be part of the Name.");
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);
   oldName = SystemUtils.filter(oldName);

   //
   //  If name has changed check for dup
   //
   if (!oldName.equals( name )) {    // if name has changed

      try {

         PreparedStatement stmt = con.prepareStatement (
                 "SELECT sdate FROM lottery3 WHERE name = ?");

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);       // put the parm in stmt
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            dupMem(out);    // member restriction exists - inform the user and return
            stmt.close();
            return;
         }
         stmt.close();
      }
      catch (Exception ignored) {
      }
   }

   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Update Lottery Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<br>");
   out.println("<H3>Update Lottery Confirmation</H3><BR>");
   out.println("Please confirm the following updated parameters for the lottery:<br><b>" + name + "</b><br><br>");

   out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\">");

   if (!course.equals( "" )) {

      out.println("<tr><td align=\"right\">");
      out.println("Course Name:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + course);
      out.println("</td></tr>");
   }

   out.println("<tr><td align=\"right\">");
   out.println("Tees:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + fb);
   out.println("</td></tr>");
     
   out.println("<tr><td align=\"right\">");
   out.println("Start Date:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + smonth + "/" + sday + "/" + syear);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + emonth + "/" + eday + "/" + eyear);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Start Time:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");

   if (s_hr == 0) {
     
      if (s_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;12:0" + s_min + "  AM");
      } else {
         out.println("&nbsp;&nbsp;&nbsp;12:" + s_min + "  AM");
      }
     
   } else {

      if (s_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;" + shr + ":0" + s_min + "  " + ssampm);
      } else {
         out.println("&nbsp;&nbsp;&nbsp;" + shr + ":" + s_min + "  " + ssampm);
      }
   }

   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("End Time:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");

   if (e_hr == 0) {

      if (e_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;12:0" + e_min + "  AM");
      } else {
         out.println("&nbsp;&nbsp;&nbsp;12:" + e_min + "  AM");
      }

   } else {

      if (e_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;" + ehr + ":0" + e_min + "  " + seampm);
      } else {
         out.println("&nbsp;&nbsp;&nbsp;" + ehr + ":" + e_min + "  " + seampm);
      }
   }

   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Recurrence:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + recurr);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Color:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + color);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Days in advance to start taking requests:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + sdays);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Time of day to start taking requests:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   if (sd_hr == 0) {

      if (sd_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;12:0" + sd_min + "  AM");
      } else {
         out.println("&nbsp;&nbsp;&nbsp;12:" + sd_min + "  AM");
      }
   } else {
      if (sd_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;" + sd_hr + ":0" + sd_min + "  " + ssdampm);
      } else {
         out.println("&nbsp;&nbsp;&nbsp;" + sd_hr + ":" + sd_min + "  " + ssdampm);
      }
   }
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Days in advance to stop taking requests:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + edays);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Time of day to stop taking requests:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   if (ed_hr == 0) {

      if (ed_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;12:0" + ed_min + "  AM");
      } else {
         out.println("&nbsp;&nbsp;&nbsp;12:" + ed_min + "  AM");
      }
   } else {
      if (ed_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;" + ed_hr + ":0" + ed_min + "  " + sedampm);
      } else {
         out.println("&nbsp;&nbsp;&nbsp;" + ed_hr + ":" + ed_min + "  " + sedampm);
      }
   }
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Days in advance to process the requests:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + pdays);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Time of day to process the requests:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   if (p_hr == 0) {

      if (p_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;12:0" + p_min + "  AM");
      } else {
         out.println("&nbsp;&nbsp;&nbsp;12:" + p_min + "  AM");
      }
   } else {
      if (p_min < 10) {
         out.println("&nbsp;&nbsp;&nbsp;" + p_hr + ":0" + p_min + "  " + spampm);
      } else {
         out.println("&nbsp;&nbsp;&nbsp;" + p_hr + ":" + p_min + "  " + spampm);
      }
   }
   out.println("</td></tr>");
                                  
   out.println("<tr><td align=\"right\">");
   out.println("Number of consecutive tee times member can request:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + slots);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Minimum number of players per request:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + players);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Minimum number of members per request:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + members);
   out.println("</td></tr>");

/*
   out.println("<tr><td align=\"right\">");
   out.println("Give preference to full groups?:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + pref);
   out.println("</td></tr>");
*/

   out.println("<tr><td align=\"right\">");
   out.println("Allow members to specify Minutes Before & After requested time?:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + allowmins);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Default Minutes Before requested time to check:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + mins_before);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Default Minutes After requested time to check:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + mins_after);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Allow members to use an X to hold a position in the request? (# to allow):&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + allow_x);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Allow proshop users to recur requests?:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + recurrpro);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Allow members to recur requests?:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + recurrmem);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Review & approve tee times prior to posting?:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + approve);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Type of Lottery:&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   if (type.equals( "WeightedBR" )) {
      out.println("&nbsp;&nbsp;&nbsp;Weighted By Rounds");
   } else {
      if (type.equals( "WeightedBP" )) {
         out.println("&nbsp;&nbsp;&nbsp;Weighted By Proximity");
      } else {
         if (type.equals( "Random" )) {
            out.println("&nbsp;&nbsp;&nbsp;Random");
         } else {
            out.println("&nbsp;&nbsp;&nbsp;Proshop");
         }
      }
   }
   out.println("</td></tr>");

   
   if (type.equals( "WeightedBR" )) {
      
      out.println("<tr><td align=\"right\">");
      out.println("# of days to accumulate members' points:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + adays);
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Points to count for a weekday round:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + wdpts);
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Points to count for a weekend round:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + wepts);
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Points to count for an event round:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + evpts);
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Points to count for a guest round:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + gpts);
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Points to count for a no-show:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + nopts);
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Selection to be based on:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      if (selection == 1) {
         out.println("&nbsp;&nbsp;&nbsp;Total points for group");
      }
      if (selection == 2) {
         out.println("&nbsp;&nbsp;&nbsp;Average points for group");
      }
      if (selection == 3) {
         out.println("&nbsp;&nbsp;&nbsp;Highest player's points");
      }
      if (selection == 4) {
         out.println("&nbsp;&nbsp;&nbsp;Lowest player's points");
      }
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Each guest in the group will count as:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      if (guest == 0) {
         out.println("&nbsp;&nbsp;&nbsp;Will not be counted");
      }
      if (guest == 1) {
         out.println("&nbsp;&nbsp;&nbsp;Same as highest member");
      }
      if (guest == 2) {
         out.println("&nbsp;&nbsp;&nbsp;Same as lowest member");
      }
      out.println("</td></tr>");

      if (!allow_x.equals("0")) {
         
         out.println("<tr><td align=\"right\">");
         out.println("Each X in the group will count as:&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("</td><td align=\"left\">");
         if (x_value == 0) {
            out.println("&nbsp;&nbsp;&nbsp;Will not be counted");
         }
         if (x_value == 1) {
            out.println("&nbsp;&nbsp;&nbsp;Same as highest member");
         }
         if (x_value == 2) {
            out.println("&nbsp;&nbsp;&nbsp;Same as lowest member");
         }
         out.println("</td></tr>");
      }
      
   } else if (type.equals( "WeightedBP" )) {
      
      out.println("<tr><td align=\"right\">");
      out.println("# of days to accumulate members' points:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + adays);
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Selection to be based on:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      if (selection == 1) {
         out.println("&nbsp;&nbsp;&nbsp;Total points for group");
      }
      if (selection == 2) {
         out.println("&nbsp;&nbsp;&nbsp;Average points for group");
      }
      if (selection == 3) {
         out.println("&nbsp;&nbsp;&nbsp;Highest player's points");
      }
      if (selection == 4) {
         out.println("&nbsp;&nbsp;&nbsp;Lowest player's points");
      }
      out.println("</td></tr>");

      out.println("<tr><td align=\"right\">");
      out.println("Each guest in group will count as:&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      if (guest == 0) {
         out.println("&nbsp;&nbsp;&nbsp;Will not be counted");
      }
      if (guest == 1) {
         out.println("&nbsp;&nbsp;&nbsp;Same as highest member");
      }
      if (guest == 2) {
         out.println("&nbsp;&nbsp;&nbsp;Same as lowest member");
      }
      out.println("</td></tr>");

      if (!allow_x.equals("0")) {
         
         out.println("<tr><td align=\"right\">");
         out.println("Each X in the group will count as:&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("</td><td align=\"left\">");
         if (x_value == 0) {
            out.println("&nbsp;&nbsp;&nbsp;Will not be counted");
         }
         if (x_value == 1) {
            out.println("&nbsp;&nbsp;&nbsp;Same as highest member");
         }
         if (x_value == 2) {
            out.println("&nbsp;&nbsp;&nbsp;Same as lowest member");
         }
         out.println("</td></tr>");
      }
   }

   out.println("</table>");

   if (sheet.equals( "yes" )) {
      out.println("<form action=\"Proshop_editlottery\" method=\"post\">");
   } else {
      out.println("<form action=\"Proshop_editlottery\" method=\"post\" target=\"bot\">");
   }
   out.println("<BR>ARE YOU SURE YOU WANT TO UPDATE THIS RECORD?");
   out.println("<input type=\"hidden\" name=\"lottery_name\" value=\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"oldName\" value = \"" + oldName + "\">");
   out.println("<input type=\"hidden\" name=\"smonth\" value=\"" + smonth + "\">");
   out.println("<input type=\"hidden\" name=\"sday\" value=\"" + sday + "\">");
   out.println("<input type=\"hidden\" name=\"syear\" value=\"" + syear + "\">");
   out.println("<input type=\"hidden\" name=\"emonth\" value=\"" + emonth + "\">");
   out.println("<input type=\"hidden\" name=\"eday\" value=\"" + eday + "\">");
   out.println("<input type=\"hidden\" name=\"eyear\" value=\"" + eyear + "\">");
   out.println("<input type=\"hidden\" name=\"start_hr\" value=\"" + shr + "\">");
   out.println("<input type=\"hidden\" name=\"start_min\" value=\"" + smin + "\">");
   out.println("<input type=\"hidden\" name=\"start_ampm\" value=\"" + sampm + "\">");
   out.println("<input type=\"hidden\" name=\"end_hr\" value=\"" + ehr + "\">");
   out.println("<input type=\"hidden\" name=\"end_min\" value=\"" + emin + "\">");
   out.println("<input type=\"hidden\" name=\"end_ampm\" value=\"" + eampm + "\">");
   out.println("<input type=\"hidden\" name=\"recurr\" value=\"" + recurr + "\">");
   out.println("<input type=\"hidden\" name=\"color\" value=\"" + color + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
   out.println("<input type=\"hidden\" name=\"sdays\" value=\"" + sdays + "\">");
   out.println("<input type=\"hidden\" name=\"sd_hr\" value=\"" + sd_hr + "\">");
   out.println("<input type=\"hidden\" name=\"sd_min\" value=\"" + sd_min + "\">");
   out.println("<input type=\"hidden\" name=\"sd_ampm\" value=\"" + sdampm + "\">");
   out.println("<input type=\"hidden\" name=\"edays\" value=\"" + edays + "\">");
   out.println("<input type=\"hidden\" name=\"ed_hr\" value=\"" + ed_hr + "\">");
   out.println("<input type=\"hidden\" name=\"ed_min\" value=\"" + ed_min + "\">");
   out.println("<input type=\"hidden\" name=\"ed_ampm\" value=\"" + edampm + "\">");
   out.println("<input type=\"hidden\" name=\"pdays\" value=\"" + pdays + "\">");
   out.println("<input type=\"hidden\" name=\"p_hr\" value=\"" + p_hr + "\">");
   out.println("<input type=\"hidden\" name=\"p_min\" value=\"" + p_min + "\">");
   out.println("<input type=\"hidden\" name=\"p_ampm\" value=\"" + pampm + "\">");
   out.println("<input type=\"hidden\" name=\"type\" value=\"" + type + "\">");
   out.println("<input type=\"hidden\" name=\"adays\" value=\"" + adays + "\">");
   out.println("<input type=\"hidden\" name=\"wdpts\" value=\"" + wdpts + "\">");
   out.println("<input type=\"hidden\" name=\"wepts\" value=\"" + wepts + "\">");
   out.println("<input type=\"hidden\" name=\"evpts\" value=\"" + evpts + "\">");
   out.println("<input type=\"hidden\" name=\"gpts\" value=\"" + gpts + "\">");
   out.println("<input type=\"hidden\" name=\"nopts\" value=\"" + nopts + "\">");
   out.println("<input type=\"hidden\" name=\"selection\" value=\"" + selection + "\">");
   out.println("<input type=\"hidden\" name=\"guest\" value=\"" + guest + "\">");
   out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
   out.println("<input type=\"hidden\" name=\"players\" value=\"" + players + "\">");
   out.println("<input type=\"hidden\" name=\"members\" value=\"" + members + "\">");
   out.println("<input type=\"hidden\" name=\"pref\" value=\"" + pref + "\">");
   out.println("<input type=\"hidden\" name=\"approve\" value=\"" + approve + "\">");
   out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");
   out.println("<input type=\"hidden\" name=\"sheet\" value = \"" + sheet + "\">");
   out.println("<input type=\"hidden\" name=\"oldpdays\" value=\"" + oldpdays + "\">");
   out.println("<input type=\"hidden\" name=\"oldptime\" value=\"" + oldptime + "\">");
   out.println("<input type=\"hidden\" name=\"allowmins\" value=\"" + allowmins + "\">");
   out.println("<input type=\"hidden\" name=\"minsbefore\" value=\"" + mins_before + "\">");
   out.println("<input type=\"hidden\" name=\"minsafter\" value=\"" + mins_after + "\">");
   out.println("<input type=\"hidden\" name=\"allowx\" value=\"" + allow_x + "\">");
   out.println("<input type=\"hidden\" name=\"xvalue\" value=\"" + x_value + "\">");
   out.println("<input type=\"hidden\" name=\"maxmems\" value=\"" + maxmems + "\">");
   out.println("<input type=\"hidden\" name=\"recurrpro\" value=\"" + recurrpro + "\">");
   out.println("<input type=\"hidden\" name=\"recurrmem\" value=\"" + recurrmem + "\">");
   out.println("<input type=\"hidden\" name=\"recur_days\" value=\"" + recur_days + "\">");
      
   out.println("<br><input type=\"submit\" value=\"Yes\" name=\"Yes\">");
   out.println("</form><font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_lottery\">");
      out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\"No - Cancel\" onClick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************
   //  removeConf - Request a confirmation for delete
   //*************************************************

 private void removeConf(HttpServletRequest req, PrintWriter out) {


   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("lottery_name");
   String course = req.getParameter("course");
   String sheet = req.getParameter("sheet");

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);


   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Delete Lottery Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Remove Lottery Confirmation</H3><BR>");
   out.println("<BR>Please confirm that you wish to remove this lottery: <b>" + name + "</b><br>");

   if (sheet.equals( "yes" )) {
      out.println("<form action=\"Proshop_editlottery\" method=\"post\">");
   } else {
      out.println("<form action=\"Proshop_editlottery\" method=\"post\" target=\"bot\">");
   }
   out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS RECORD?");
      
   out.println("<input type=\"hidden\" name=\"lottery_name\" value =\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value =\"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"sheet\" value =\"" + sheet + "\">");

   out.println("<br><br><input type=\"submit\" value=\"Delete\" name=\"Delete\">");
   out.println("</form><font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_lottery\">");
      out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\"No - Cancel\" onClick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************************************
   //  removelottery - Remove a lottery from lottery table - received confirmation
   //*************************************************************************

 private void removelottery(HttpServletRequest req, PrintWriter out, HttpSession sess) {


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

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_LOTTERY", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_LOTTERY", out);
   }
     
   String omit = "";
   
   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("lottery_name");
   String course = req.getParameter("course");
   String sheet = req.getParameter("sheet");

   //
   // Delete the lottery from the lottery table
   //
   PreparedStatement stmt = null;
   ResultSet rs = null;
   int count = 0;

   try {
      //
      //  Check the name for special characters and translate if ncessary
      //
      stmt = con.prepareStatement (
               "SELECT sdate FROM lottery3 WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);       // put the parm in stmt
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (!rs.next()) {                     // if not found

         name = SystemUtils.filter(name);   // translate the name
      }
      stmt.close();              // close the stmt

      //
      //  Delete the rest
      //
      stmt = con.prepareStatement (
               "Delete FROM lottery3 WHERE name = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, name);              // put the parm in stmt
      count = stmt.executeUpdate();         // execute the prepared stmt

      stmt.close();

      stmt = con.prepareStatement (                     // ************ TEMP ???***************
               "Delete FROM actlott3 WHERE name = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, name);              // put the parm in stmt
      count = stmt.executeUpdate();         // execute the prepared stmt

      stmt.close();
   }
   catch (Exception exc) {

      dbError(out);
      return;
   }

   //
   // Remove the lottery from any tee sheets in teecurr table
   //
   try {

      if (!course.equals( "" ) && !course.equals( "-ALL-" )) {

         PreparedStatement stmt1 = con.prepareStatement (
           "UPDATE teecurr2 SET lottery = '', lottery_color = '' WHERE lottery = ? AND courseName = ?");

         stmt1.clearParameters();               // clear the parms
         stmt1.setString(1, name);              // put the parm in stmt
         stmt1.setString(2, course);
         count = stmt1.executeUpdate();         // execute the prepared stmt

         stmt1.close();

      } else {

         PreparedStatement stmt2 = con.prepareStatement (
           "UPDATE teecurr2 SET lottery = '', lottery_color = '' WHERE lottery = ?");

         stmt2.clearParameters();               // clear the parms
         stmt2.setString(1, name);              // put the parm in stmt
         count = stmt2.executeUpdate();         // execute the prepared stmt

         stmt2.close();
      }
   }
   catch (Exception ignore) {

   }

   //
   //  Lottery successfully deleted
   //
   out.println(SystemUtils.HeadTitle("Delete Lottery Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Lottery Record Has Been Removed</H3><BR>");
   out.println("<BR><BR>Thank you, the lottery has been removed from the database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_lottery\">");
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\"Done\" onClick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</CENTER></BODY></HTML>");

 }  // done with delete function


   //*******************************************************************
   //  updatelottery - Update a lottery from lottery table - received a conf
   //*******************************************************************

 private void updatelottery(HttpServletRequest req, PrintWriter out, HttpSession sess, HttpServletResponse resp) {


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

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_LOTTERY", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_LOTTERY", out);
   }
     
   String club = (String)sess.getAttribute("club");

   //
   // Get the parameters entered
   //
   String name = req.getParameter("lottery_name");
   String oldName = req.getParameter("oldName");
   String s_month = req.getParameter("smonth");             //  month (00 - 12)
   String s_day = req.getParameter("sday");                 //  day (01 - 31)
   String s_year = req.getParameter("syear");               //  year (2002 - 20xx)
   String e_month = req.getParameter("emonth");             
   String e_day = req.getParameter("eday");                 
   String e_year = req.getParameter("eyear");               
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String end_hr = req.getParameter("end_hr");             //   .
   String end_min = req.getParameter("end_min");           //   .
   String end_ampm = req.getParameter("end_ampm");         //   .
   String recurr = req.getParameter("recurr");             //  recurrence
   String color = req.getParameter("color");               //  color for the lottery display
   String course = req.getParameter("course");             //  course name
   String fb = req.getParameter("fb");                     //  Front/Back indicator
   String oldCourse = req.getParameter("oldCourse");
   String ssdays = req.getParameter("sdays");
   String ssd_hr = req.getParameter("sd_hr");
   String ssd_min = req.getParameter("sd_min");
   String ssdampm = req.getParameter("sd_ampm");
   String sedays = req.getParameter("edays");
   String sed_hr = req.getParameter("ed_hr");
   String sed_min = req.getParameter("ed_min");
   String sedampm = req.getParameter("ed_ampm");
   String spdays = req.getParameter("pdays");
   String sp_hr = req.getParameter("p_hr");
   String sp_min = req.getParameter("p_min");
   String spampm = req.getParameter("p_ampm");
   String type = req.getParameter("type");
   String sadays = req.getParameter("adays");
   String swdpts = req.getParameter("wdpts");
   String swepts = req.getParameter("wepts");
   String sevpts = req.getParameter("evpts");
   String sgpts = req.getParameter("gpts");
   String snopts = req.getParameter("nopts");
   String sselection = req.getParameter("selection");
   String sguest = req.getParameter("guest");
   String sslots = req.getParameter("slots");
   String splayers = req.getParameter("players");
   String smembers = req.getParameter("members");
   String approve = req.getParameter("approve");
   String sheet = req.getParameter("sheet");                 //  From Proshop_sheet?
   String soldpdays = req.getParameter("oldpdays");
   String soldptime = req.getParameter("oldptime");
   String allowmins = req.getParameter("allowmins");
   String minsbefore = req.getParameter("minsbefore");
   String minsafter = req.getParameter("minsafter");

   String pref = "No";

   if (req.getParameter("pref") != null) {

      pref = req.getParameter("pref");
   }

   String maxmems = "0";
   
   if (req.getParameter("maxmems") != null) {

      maxmems = req.getParameter("maxmems");      // custom parm for Oakland Hills
   }

   
   String temp = "";
   int allow_x = 0;
    
   if (req.getParameter("allowx") != null) {     

      temp = req.getParameter("allowx");
   
      try {
           allow_x = Integer.parseInt(temp);
      } catch (NumberFormatException ignore) {}
   }

   temp = "";
   int x_value = 0;

   if (req.getParameter("xvalue") != null) {     

      temp = req.getParameter("xvalue");
   
      try {
           x_value = Integer.parseInt(temp);
      } catch (NumberFormatException ignore) {}
   }

   
   int recurrpro = 0;

   if (req.getParameter("recurrpro") != null) {     

      if (req.getParameter("recurrpro").equalsIgnoreCase("yes")) recurrpro = 1;
   }

   int recurrmem = 0;

   if (req.getParameter("recurrmem") != null) {     

      if (req.getParameter("recurrmem").equalsIgnoreCase("yes")) recurrmem = 1;
   }

   int recur_days = 0;
   
   if (req.getParameter("recur_days") != null) {     

      recur_days = Integer.parseInt(req.getParameter("recur_days"));
   }

   
   String omit = "";
   String errMsg = "";
   
   int smonth = 0;
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int sd_hr = 0;
   int ed_hr = 0;
   int p_hr = 0;
   int sd_min = 0;
   int ed_min = 0;
   int p_min = 0;
   int selection = 0;
   int guest = 0;
   int sdampm = 0;
   int edampm = 0;
   int pampm = 0;
   int adays = 0;
   int wdpts = 0;
   int wepts = 0;
   int evpts = 0;
   int gpts = 0;
   int nopts = 0;
   int slots = 0;
   int members = 0;
   int players = 0;
   int sdays = 0;
   int edays = 0;
   int pdays = 0;
   int oldpdays = 0;
   int oldptime = 0;
   int allow_mins = 0;
   int mins_before = 0;
   int mins_after = 0;
   int maxmem = 0;
   int custom_int = 0;

   long oldEdate = 0;
   long oldSdate = 0;
     
   boolean found = false;

   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      emonth = Integer.parseInt(e_month);
      eday = Integer.parseInt(e_day);
      eyear = Integer.parseInt(e_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
      sd_hr = Integer.parseInt(ssd_hr);
      ed_hr = Integer.parseInt(sed_hr);
      p_hr = Integer.parseInt(sp_hr);
      sd_min = Integer.parseInt(ssd_min);
      ed_min = Integer.parseInt(sed_min);
      p_min = Integer.parseInt(sp_min);
      sdays = Integer.parseInt(ssdays);
      edays = Integer.parseInt(sedays);
      pdays = Integer.parseInt(spdays);
      selection = Integer.parseInt(sselection);
      guest = Integer.parseInt(sguest);
      sdampm = Integer.parseInt(ssdampm);
      edampm = Integer.parseInt(sedampm);
      pampm = Integer.parseInt(spampm);
      adays = Integer.parseInt(sadays);
      wdpts = Integer.parseInt(swdpts);
      wepts = Integer.parseInt(swepts);
      evpts = Integer.parseInt(sevpts);
      gpts = Integer.parseInt(sgpts);
      nopts = Integer.parseInt(snopts);
      slots = Integer.parseInt(sslots);
      members = Integer.parseInt(smembers);
      players = Integer.parseInt(splayers);
      oldpdays = Integer.parseInt(soldpdays);
      oldptime = Integer.parseInt(soldptime);
      mins_before = Integer.parseInt(minsbefore);
      mins_after = Integer.parseInt(minsafter);
      maxmem = Integer.parseInt(maxmems);
   }
   catch (Exception ignore) {
   }

   
   if (allowmins.equals("No")) {
      
      allow_mins = 1;          // 1 means to NOT allow members to change mins before and mins after !!!
   }


   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;      // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;      // create a date field from input values
   edate = edate + (emonth * 100);
   edate = edate + eday;             // date = yyyymmdd (for comparisons)

   if (s_hr != 12) {              // _hr specified as 00 - 12 (_ampm = 00 or 12)

      s_hr = s_hr + s_ampm;       // convert to military time (12 is always Noon or PM)
   }

   if (e_hr != 12) {              // ditto

      e_hr = e_hr + e_ampm;
   }

   if (sd_hr != 12) {              // ditto

      sd_hr = sd_hr + sdampm;
   }

   if (ed_hr != 12) {              // ditto

      ed_hr = ed_hr + edampm;
   }

   if (p_hr != 12) {              // ditto

      p_hr = p_hr + pampm;
   }

   int stime = s_hr * 100;
   stime = stime + s_min;
   int etime = e_hr * 100;
   etime = etime + e_min;
   int sdtime = sd_hr * 100;
   sdtime = sdtime + sd_min;
   int edtime = ed_hr * 100;
   edtime = edtime + ed_min;
   int ptime = p_hr * 100;
   ptime = ptime + p_min;

   //
   //  verify the date and time fields
   //
   if ((sdate > edate) || (stime > etime)) {

      errMsg = "The start date and start time values cannot be greater than the end date and end time values.";
      invData(errMsg, out);    // inform the user and return
      return;
   }

   //
   //  Verify the recur_days value - do not allow negative and do not allow too many days
   //
   if (recur_days < 0) recur_days = 0;
   
   if (recur_days > 365) recur_days = 365;
   
   
   //
   //  Udate the record in the lottery table
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   int count = 0;

   try {
      //
      //  Check the name for special characters and translate if ncessary
      //
      pstmt = con.prepareStatement (
               "SELECT sdate FROM lottery3 WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, oldName);       // put the parm in stmt
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (!rs.next()) {                     // if not found

         oldName = SystemUtils.filter(oldName);   // translate the name
      }
      pstmt.close();              // close the stmt

      //
      //  if the start date or end date has changed, check if there are any requests that will be lost
      //
      pstmt = con.prepareStatement (
               "SELECT sdate, edate FROM lottery3 WHERE name = ?");

      pstmt.clearParameters();       
      pstmt.setString(1, oldName);   
      rs = pstmt.executeQuery();     

      if (rs.next()) {                

         oldSdate = rs.getLong("sdate");
         oldEdate = rs.getLong("edate");
      }
      pstmt.close();              // close the stmt

      if (oldSdate > 0 && oldSdate < sdate) {           // if new start date is later than old - check lreqs

         pstmt = con.prepareStatement (
                "SELECT mm FROM lreqs3 WHERE name = ? AND date >= ? AND date < ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, oldName);
         pstmt.setLong(2, oldSdate);
         pstmt.setLong(3, sdate);
         rs = pstmt.executeQuery();

         if (rs.next()) {

            found = true;     // lreqs found
         }
         pstmt.close();

         if (found == true) {           // if lottery requests will be lost - warn pro

            errMsg = "There are outstanding lottery requests that will be lost due to the new start date.  Please process or cancel the requests before changing this lottery."; 
            invData(errMsg, out);    // inform the user and return
            return;
         }
      }

      if (oldEdate > 0 && oldEdate > edate) {           // if new end date is earlier than old - check lreqs

         pstmt = con.prepareStatement (
                "SELECT mm FROM lreqs3 WHERE name = ? AND date <= ? AND date > ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, oldName);
         pstmt.setLong(2, oldEdate);
         pstmt.setLong(3, edate);
         rs = pstmt.executeQuery();

         if (rs.next()) {

            found = true;     // lreqs found
         }
         pstmt.close();

         if (found == true) {           // if lottery requests will be lost - warn pro

            errMsg = "There are outstanding lottery requests that will be lost due to the new end date.  Please process or cancel the requests before changing this lottery.";
            invData(errMsg, out);    // inform the user and return
            return;
         }
      }
      
      if (club.equals("oaklandhills") && maxmem > 0) {
          
          custom_int = maxmem;       // save custom value
      }
      

      //
      //  Udate the record in the restriction table
      //
      pstmt = con.prepareStatement (
        "UPDATE lottery3 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ?, " +
        "start_hr = ?, start_min = ?, stime = ?, " +
        "edate = ?, end_mm = ?, end_dd = ?, end_yy = ?, " +
        "end_hr = ?, end_min = ?, etime = ?, recurr = ?, " +
        "color=?, courseName=?, fb=?, sdays=?, sdtime=?, sd_hr=?, sd_min=?, edays=?, " +
        "edtime=?, ed_hr=?, ed_min=?, pdays=?, ptime=?, p_hr=?, p_min=?, type=?, adays=?, " +
        "wdpts=?, wepts=?, evpts=?, gpts=?, nopts=?, selection=?, guest=?, slots=?, pref=?, approve=?, " +
        "members=?, players=?, name = ?, minsbefore = ?, minsafter = ?, allowmins = ?, allowx = ?, xvalue = ?, " +
        "custom_int = ?, recurrpro = ?, recurrmem = ?, recur_days = ? " +
        "WHERE name = ?");
          
      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, sdate);
      pstmt.setInt(2, smonth);
      pstmt.setInt(3, sday);
      pstmt.setInt(4, syear);
      pstmt.setInt(5, s_hr);
      pstmt.setInt(6, s_min);
      pstmt.setInt(7, stime);
      pstmt.setLong(8, edate);
      pstmt.setInt(9, emonth);
      pstmt.setInt(10, eday);
      pstmt.setInt(11, eyear);
      pstmt.setInt(12, e_hr);         
      pstmt.setInt(13, e_min);
      pstmt.setInt(14, etime);
      pstmt.setString(15, recurr);
      pstmt.setString(16, color);
      pstmt.setString(17, course);
      pstmt.setString(18, fb);
      pstmt.setInt(19, sdays);
      pstmt.setInt(20, sdtime);
      pstmt.setInt(21, sd_hr);
      pstmt.setInt(22, sd_min);
      pstmt.setInt(23, edays);
      pstmt.setInt(24, edtime);
      pstmt.setInt(25, ed_hr);
      pstmt.setInt(26, ed_min);
      pstmt.setInt(27, pdays);
      pstmt.setInt(28, ptime);
      pstmt.setInt(29, p_hr);
      pstmt.setInt(30, p_min);
      pstmt.setString(31, type);
      pstmt.setInt(32, adays);
      pstmt.setInt(33, wdpts);
      pstmt.setInt(34, wepts);
      pstmt.setInt(35, evpts);
      pstmt.setInt(36, gpts);
      pstmt.setInt(37, nopts);
      pstmt.setInt(38, selection);
      pstmt.setInt(39, guest);
      pstmt.setInt(40, slots);
      pstmt.setString(41, pref);
      pstmt.setString(42, approve);
      pstmt.setInt(43, members);
      pstmt.setInt(44, players);
      pstmt.setString(45, name);
      pstmt.setInt(46, mins_before);
      pstmt.setInt(47, mins_after);
      pstmt.setInt(48, allow_mins);
      pstmt.setInt(49, allow_x);
      pstmt.setInt(50, x_value);
      pstmt.setInt(51, custom_int);
      pstmt.setInt(52, recurrpro);
      pstmt.setInt(53, recurrmem);
      pstmt.setInt(54, recur_days);
        
      pstmt.setString(55, oldName);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();
   }
   catch (Exception exc) {

      dbError(out);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Lottery"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Lottery Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the lottery has been updated in the system database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_lottery\">");
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\"Done\" onClick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }


   //
   // Remove the lottery from any tee sheets in teecurr table (in case they now need changing)
   //
   try {

      PreparedStatement pstmt1 = null;

      if (!oldCourse.equals( "" ) && !oldCourse.equals( "-ALL-" )) {

         pstmt1 = con.prepareStatement (
           "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE lottery = ? AND courseName = ?");

         pstmt1.clearParameters();               // clear the parms
         pstmt1.setString(1,omit);
         pstmt1.setString(2,omit);
         pstmt1.setString(3, oldName);              // put the parm in pstmt
         pstmt1.setString(4, oldCourse);
         count = pstmt1.executeUpdate();         // execute the prepared pstmt

         pstmt1.close();

      } else {

         PreparedStatement pstmt2 = con.prepareStatement (
           "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE lottery = ?");

         pstmt2.clearParameters();               // clear the parms
         pstmt2.setString(1,omit);
         pstmt2.setString(2,omit);
         pstmt2.setString(3, oldName);              // put the parm in pstmt
         count = pstmt2.executeUpdate();         // execute the prepared pstmt

         pstmt2.close();
      }

      // 
      //  Change lottery name in lreqs3 and actlott3 in case name was changed
      //
      pstmt1 = con.prepareStatement (
        "UPDATE lreqs3 SET name = ? WHERE name = ?");

      pstmt1.clearParameters();               // clear the parms
      pstmt1.setString(1, name);
      pstmt1.setString(2, oldName);
      count = pstmt1.executeUpdate();         // execute the prepared pstmt

      pstmt1.close();

      pstmt1 = con.prepareStatement (
        "UPDATE actlott3 SET name = ? WHERE name = ?");

      pstmt1.clearParameters();               // clear the parms
      pstmt1.setString(1, name);
      pstmt1.setString(2, oldName);
      count = pstmt1.executeUpdate();         // execute the prepared pstmt

      pstmt1.close();

      //
      //  Check if the Processing date and/or time has changed - If so, change in actlott3
      //
      if (oldpdays != pdays || oldptime != ptime) {       // if pdays or ptime has changed 

         int phr = 0;
         int pdays2 = pdays;     // save original
         int ptime2 = ptime;
         long pyy = 0;
         long pmm = 0;
         long pdd = 0;

         //
         //  Get any entries in actlott3 for this lottery
         //
         pstmt = con.prepareStatement (
                  "SELECT date FROM actlott3 WHERE name = ?"); 

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, name);       // put the parm in stmt
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {                

            edate = rs.getLong(1);                          // get date of this entry

            pyy = edate / 10000;                            // get year
            pmm = (edate - (pyy * 10000)) / 100;            // month
            pdd = edate - ((pyy * 10000) + (pmm * 100));    // day

            pdays = pdays2;                                 // restore originals
            ptime = ptime2;

            //
            //  use the lottery's date and then roll back 'pdays' to determine the processing date
            //
            while (pdays > 0) {

               pdd--;                  // go back one day

               if (pdd == 0) {         // adjust month and year if necessary

                  if (pmm == 1) {      // must go back to 12/31 of prev year

                     pyy--;
                     pmm = 12;
                     pdd = 31;

                  } else {

                     if (pmm == 2 || pmm == 4 || pmm == 6 || pmm == 8 || pmm == 9 || pmm == 11) { // new month has 31 days

                        pmm--;
                        pdd = 31;

                     } else {

                        if (pmm == 5 || pmm == 7 || pmm == 10 || pmm == 12) {     // new month has 30 days

                           pmm--;
                           pdd = 30;

                        } else {

                           if (pmm == 3) {     // new month to be Feb

                              pmm = 2;
                              pdd = 28;

                              if (pyy == 2004 ||  pyy == 2008 ||  pyy == 2012 ||  pyy == 2016 ||  pyy == 2020 ||  pyy == 2024 ||
                                  pyy == 2028 ||  pyy == 2032 ||  pyy == 2036 ||  pyy == 2040) {

                                 pdd = 29;           // leap year
                              }
                           }
                        }
                     }
                  }
               }
               pdays--;                  // continue for specified days in advance
            }                       // end of while

            sdate = (pyy * 10000) + (pmm * 100) + pdd;    // processing date = yyyymmdd

            //
            //  adjust the time for the club's time zone (moved from before date because we need the date for this!!)
            //
            ptime = SystemUtils.adjustTimeBack(con, ptime, sdate);

            if (ptime < 0) {          // if negative, then roll back one day or ahead one day

               ptime = 0 - ptime;     // convert back to positive

               SystemUtils.logError("WARNING: " +club+ " has a lottery defined (" +name+ ") that processes near midnight.");   // shouldn't happen - log this
            }

            pstmt1 = con.prepareStatement (
              "UPDATE actlott3 SET pdate = ?, ptime = ? WHERE name = ? AND date = ?");

            pstmt1.clearParameters();               // clear the parms
            pstmt1.setLong(1, sdate);
            pstmt1.setInt(2, ptime);
            pstmt1.setString(3, name);
            pstmt1.setLong(4, edate);
            pstmt1.executeUpdate();         // execute the prepared pstmt

            pstmt1.close();
              
         }     // end of while actlott3 entries with this name

         pstmt.close();              // close the stmt
      }

   }
   catch (Exception ignore) {

   }

   //
   //  Now, call utility to scan the lottery table and update tee slots in teecurr accordingly
   //
   SystemUtils.do1Lottery(con, name);

 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(String message, PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>" +message+ "<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 //  Advance days entered incorrectly
 // *********************************************************

 private void invData2(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>The 'days in advance to stop taking request' MUST BE LESS THAN");
   out.println("<BR>the 'days in advance to start taking requests'.");
   out.println("<BR>The 'days in advance to process the requests' MUST BE LESS THAN or EQUAL TO");
   out.println("<BR>the 'days in advance to stop taking requests'.");
   out.println("<BR><BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }
   
 // *********************************************************
 // Lottery already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please change the name to a unique value.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

 private void invData3(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>The 'days in advance to process the requests' is EQUAL TO");
   out.println("<BR>the 'days in advance to stop taking requests'. This is ok.");
   out.println("<BR>However, when that's the case, the 'time of day to process requests' MUST BE");
   out.println("<BR>GREATER THAN the 'time of day to stop taking requests'.");
   out.println("<BR><BR>");
   out.println("<BR>Please correct this and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

}
