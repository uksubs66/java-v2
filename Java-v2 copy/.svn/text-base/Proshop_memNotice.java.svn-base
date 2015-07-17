/***************************************************************************************     
 *   Proshop_memNotice:  This servlet will process the 'Member Notices' request from
 *                       the Proshop's Config page.
 *
 *
 *   called by:  proshop menu (to doGet) and Proshop_memNotice (to doPost from HTML built here)
 *
 *   created: 2/22/2007   Bob P.
 *
 *   last updated:
 *
 *           9/09/13  Tee Time Calendar Page option will now only be displayed on the Golf side of the system.
 *           3/22/13  Added bgcolor selection back in since clubs are reporting their colors going away.
 *           3/05/13  Add type=teetime_cal option to display mem notice on Member_select page.  Also, no need for background color - didn't work with new skin and editor.
 *           3/05/13  Add tinyMCE editor for the message content.
 *           5/03/12  Fixed issue where selecting anytime between 12:00 and 12:59 PM would get saved as AM instead.
 *           4/12/11  Fixed issue where start and end hour were getting converted into military time twice, resulting in start and end times above 2400.
 *           3/31/11  Fixed issue where course and fb values were getting set to blank strings if the activity_id was 0 (golf). It will now do this if the activity_id is NOT 0 instead.
 *           5/24/10  Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *          04/21/10  Added a check while loading courses to not attempt to read the name of a 21st course.
 *          03/16/09  Added note to clarify how to add line breaks to member notice text
 *          09/05/08  Added Tee Sheet/Background Color option for member notice configuration
 *          08/07/08  Modified limited access proshop user restrictions
 *          07/24/08  Added limited access proshop users checks
 *          08/20/07  Added proside field to config for making notices appear on proside
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
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Proshop_memNotice extends HttpServlet {

    
   String zero = "00";
   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

  
 //************************************************************************
 //
 // Process the initial request from Proshop menus
 //
 //  doGet:  Get control from menus to list all current Member Notices,
 //          or to copy an existing mem notice (from list).
 //
 //************************************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_MEMBERNOTICES", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_MEMBERNOTICES", out);
   }
     
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
    
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Check if call is to copy an existing notice
   //
   if (req.getParameter("copy") != null) {

      doCopy(req, out, con, lottery, sess_activity_id);        // process the copy request
      return;
   }

   //
   // Define some parms to use in the html
   //
   int id = 0;             // record id
   int stime = 0;          // start time
   int etime = 0;          // end time
   int s_hour = 0;         // start time hr
   int s_min = 0;          // start time min
   int e_hour = 0;         // end time hr
   int e_min = 0;          // end time min
   int teetime = 0;
   int event = 0;
   int proside = 0;
   int teesheet = 0;
   int teetime_cal = 0;
   //int overrideColors = 0;
     
   long sdate = 0;
   long edate = 0;
   long s_year = 0;         // start year
   long s_month = 0;        // start month
   long s_day = 0;          // start day
   long e_year = 0;         // end year
   long e_month = 0;        // end month
   long e_day = 0;          // end day
       
   String name = "";       // name of notice
   String courseName = ""; // name of course
   String fb = "";         // Front/back Indicator
   String s_ampm = "";
   String e_ampm = "";
   String bgColor = "";
   String locations_csv = "";
   //String color = "";      // color for notice displays
   //String per = "";

   boolean b = false;

   //
   //  First, see if multi courses for this club
   //
   int multi = Utilities.getMulti(con);

   //
   //  Build the HTML page to display the existing notices
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Notices Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<table cellpadding=\"5\" border=\"0\" bgcolor=\"#336633\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Member Notices</b><br>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>To change or remove a member notice, click on the Select button within the member notice.");

      out.println("<br>");
      out.println("</font></td></tr></table><br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#8B8970\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"13\" align=\"center\">");
      } else {
         out.println("<td colspan=\"12\" align=\"center\">");
      }
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><b>Active Member Notices</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\" nowrap>");
      out.println("<font size=\"2\"><p><b>Name of Member<br>Notice</b></p>");
      out.println("</font></td>");

      if (sess_activity_id == 0) {
          if (multi != 0) {
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p><b>Course</b></p>");
             out.println("</font></td>");
          }
          out.println("<td align=\"center\">");
          out.println("<font size=\"2\"><p><b>Tees</b></p>");
          out.println("</font></td>");
      } else {
          out.println("<td align=\"center\">");
          out.println("<font size=\"2\"><p><b>Applicable<br>Locations</b></p>");
          out.println("</font></td>");
      }

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>" + ((sess_activity_id == 0) ? "Tee" : getActivity.getCommonName(sess_activity_id, con)) + " Times</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>For Events</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>On " + ((sess_activity_id == 0) ? "Tee" : "Time") + " Sheet</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>On Tee Time Calendar</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>" + ((sess_activity_id == 0) ? "Proshop" : "Staff") + " Side</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing notices (one table row per notice)
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM mem_notice WHERE activity_id = " + sess_activity_id + " ORDER BY name");

      while (rs.next()) {

         b = true;                     // indicate notices exist

         id = rs.getInt("mem_notice_id");
         name = rs.getString("name");
         sdate = rs.getLong("sdate");
         edate = rs.getLong("edate");
         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         teetime = rs.getInt("teetime");
         event = rs.getInt("event");
         courseName = rs.getString("courseName");
         fb = rs.getString("fb");
         proside = rs.getInt("proside");
         teesheet = rs.getInt("teesheet");
         bgColor = rs.getString("bgColor");
         locations_csv = rs.getString("locations");
         teetime_cal = rs.getInt("teetime_cal");
         
         
         //
         //  some values must be converted for display
         //
         s_hour = stime / 100;             // get hour
         s_min = stime - (s_hour * 100);   // get minute
           
         s_ampm = " AM";                   // default to AM
         if (s_hour > 12) {
            s_ampm = " PM";
            s_hour = s_hour - 12;                // convert to 12 hr clock value
         }

         if (s_hour == 12) {
            s_ampm = " PM";
         }

         e_hour = etime / 100;             // get hour
         e_min = etime - (e_hour * 100);   // get minute

         e_ampm = " AM";                   // default to AM
         if (e_hour > 12) {
            e_ampm = " PM";
            e_hour = e_hour - 12;                  // convert to 12 hr clock value
         }

         if (e_hour == 12) {
            e_ampm = " PM";
         }

         s_year = sdate / 10000;                                // get year
         s_month = (sdate - (s_year * 10000)) / 100;            // get month 
         s_day = sdate - ((s_year * 10000) + (s_month * 100));  // get day

         e_year = edate / 10000;                                // get year
         e_month = (edate - (e_year * 10000)) / 100;            // get month
         e_day = edate - ((e_year * 10000) + (e_month * 100));  // get day


         if (!bgColor.equals("")) {
             out.println("<tr bgcolor=\"" + bgColor + "\">");
         } else {
             out.println("<tr bgcolor=\"#F5F5DC\">");
         }
         
         out.println("<form method=\"post\" action=\"Proshop_memNotice\" target=\"bot\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +name+ "</p>");
         out.println("</font></td>");
         if (sess_activity_id == 0) {
             if (multi != 0) {
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\"><p>" +courseName+ "</b></p>");
                out.println("</font></td>");
             }
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p>" +fb+ "</p>");
             out.println("</font></td>");
         } else {

             out.println("<td align=\"center\" nowrap>");
             //out.println("<font size=\"2\"><p>" +getActivity.getActivityName(actId, con)+ "</p>");

             String tmp = "";
             ArrayList<Integer> locations = new ArrayList<Integer>();
             locations.clear();
             StringTokenizer tok = new StringTokenizer( locations_csv, "," );
             while ( tok.hasMoreTokens() ) {
                tmp = tok.nextToken();
                try {
                    locations.add(Integer.parseInt(tmp));
                } catch (Exception exc) {
                    out.println("<!-- locations_csv=" + locations_csv + ", tmp=" + tmp  + ", size=" + locations.size() + ", err=" + exc.toString() + " -->");
                    return;
                }
             }
             tmp = "";
             int i2 = 0;
             for (int i = 0; i < locations.size(); i++) {

                 tmp += getActivity.getActivityName(locations.get(i), con) + ", ";
                 i2++;
                 if (i2 == 3) { tmp += "<br>"; i2 = 0; }
             }

             if (tmp.endsWith("<br>")) {
                 tmp = tmp.substring(0, tmp.length() - 6);
             } else if (!tmp.equals("")) {
                 tmp = tmp.substring(0, tmp.length() - 2);
             }

             out.println("<font size=\"2\"><p>" + tmp + "</p></font></td>");

         }
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +s_month+ "/" + s_day + "/" + s_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +e_month+ "/" + e_day + "/" + e_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\" nowrap>");
         out.println("<font size=\"2\"><p>" + s_hour + ":" + Utilities.ensureDoubleDigit(s_min) + "  " + s_ampm + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\" nowrap>");
         out.println("<font size=\"2\"><p>" + e_hour + ":" + Utilities.ensureDoubleDigit(e_min) + "  " + e_ampm + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (teetime > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (event > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (teesheet > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (teetime_cal > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (proside > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");

         out.println("<input type=\"hidden\" name=\"notice_id\" value=\"" + id + "\">");
         out.println("<td align=\"center\">");
         out.println("<p>");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");

      }  // end of while loop

      if (!b) {
        
         out.println("<p>No Member Notices Currently Exist</p>");
      }

   } catch (Exception e2) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR><BR>Exception: " + e2);
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { stmt.close(); }
       catch (Exception ignore) {}

   }

   //
   //  End of HTML page
   //
   out.println("</table></font>");                   // end of memNotice table
   out.println("</td></tr></table>");                // end of main page table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
  
   out.println("</center></font></body></html>");

 }  // end of doGet



 //
 //****************************************************************
 //
 //  doPost:  Get control from above to edit an existing mem notice,
 //           or from menus to add or copy a mem notice.
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;
   PreparedStatement pstmt = null;
     
   //String [] month_table = { "inv", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
   //                          "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

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

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_MEMBERNOTICES", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_MEMBERNOTICES", out);
   }

   boolean copy = false;

   long sdate = 0;
   long edate = 0;
   long s_year = 0;
   long s_month = 0;
   long s_day = 0;
   long e_year = 0;
   long e_month = 0;
   long e_day = 0;
     
   int stime = 0;
   int etime = 0;
   int shr = 0;
   int smin = 0;
   int ehr = 0;
   int emin = 0;
   int multi = 0;        // multiple course support option
   int index = 0;
   int id = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int teetime = 0;
   int teetime_cal = 0;
   int event = 0;
   int i = 0;
   //int i2 = 0;
   int proside = 0;
   int teesheet = 0;
   //int overrideColors = 0;        // Removed for now, may implement at a later date

   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String name = "";                // name of notice
   //String oldName = "";           // original name
   String notice_id = "";
   //String temp = "";
   String fb = "";
   String message = "";
   String courseName = "";
   String bgColor = "";
   String common_name = (sess_activity_id == 0) ? "Tee" : getActivity.getCommonName(sess_activity_id, con);

   //
   //  Arrays to hold the course names and guest types
   //
   ArrayList<String> course = new ArrayList<String>();

   
   //
   // Get the record id, if present call is for copy or edit (if not, then its an 'add')
   //
   if (req.getParameter("notice_id") != null) {
     
      notice_id = req.getParameter("notice_id");
   }

   if (!notice_id.equals( "" )) {        // if id provided (not an 'add')

      try {
         id = Integer.parseInt(notice_id);
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }
   }

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy below)
   }

   if (req.getParameter("step2") != null) {    // if call is from this doPost processing

      if (id > 0) {       // if record id provided
        
         doEdit(id, sess_activity_id, req, out, con);         // go process the edit/delete request
           
      } else {
        
         doAdd(copy, sess_activity_id, req, out, con);        // go process the add/copy request
      }
      return;          // done
   }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only feature

   try {

      getClub.getParms(con, parm);        //  get the club parameters (multi, guest types, hotel)
   } catch (Exception ignore) { }


   String locations_csv = "";

   try {

      //
      // Get the notice from the notice table
      //
      if (id > 0) {        // if edit or copy

         pstmt = con.prepareStatement (
                  "SELECT * FROM mem_notice WHERE mem_notice_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);       // put the parm in stmt
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            //
            //  Found the notice record - get it
            //
            name = rs.getString("name");
            sdate = rs.getLong("sdate");
            stime = rs.getInt("stime");
            edate = rs.getLong("edate");
            etime = rs.getInt("etime");
            mon = rs.getInt("mon");
            tue = rs.getInt("tue");
            wed = rs.getInt("wed");
            thu = rs.getInt("thu");
            fri = rs.getInt("fri");
            sat = rs.getInt("sat");
            sun = rs.getInt("sun");
            teetime = rs.getInt("teetime");
            event = rs.getInt("event");
            courseName = rs.getString("courseName");
            fb = rs.getString("fb");
            message = rs.getString("message");
            proside = rs.getInt("proside");
            teesheet = rs.getInt("teesheet");
            bgColor = rs.getString("bgColor");
            locations_csv = rs.getString("locations");
            teetime_cal = rs.getInt("teetime_cal");
            //overrideColors = rs.getInt("overrideColors");        // Removed for now, may implement at a later date
              
            //
            //  some values must be converted for display
            //
            shr = stime / 100;             // get hour
            smin = stime - (shr * 100);   // get minute

            ehr = etime / 100;             // get hour
            emin = etime - (ehr * 100);   // get minute

            s_year = sdate / 10000;                                // get year
            s_month = (sdate - (s_year * 10000)) / 100;            // get month
            s_day = sdate - ((s_year * 10000) + (s_month * 100));  // get day

            e_year = edate / 10000;                                // get year
            e_month = (edate - (e_year * 10000)) / 100;            // get month
            e_day = edate - ((e_year * 10000) + (e_month * 100));  // get day

         }
           
      }

      multi = parm.multi;

      if (multi != 0) {
        
         course = Utilities.getCourseNames(con);     // get all the course names
      }

   } catch (Exception exc) {

      dbError(out, exc);
      return;

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   String ssampm = "AM";     // AM or PM for display (start hour)
   String seampm = "AM";     // AM or PM for display (end hour)

   if (shr > 12) {
      
      shr = shr - 12;        // convert to 12 hour value
      ssampm = "PM";         // indicate PM
   }

   if (shr == 12) {
      ssampm = "PM";         // indicate PM
   }

   if (ehr > 12) {

      ehr = ehr - 12;        // convert to 12 hour value
      seampm = "PM";         // indicate PM
   }

   if (ehr == 12) {
      seampm = "PM";         // indicate PM
   }

   i = 0;
     
   //
   // Database record found - output an edit page
   //
   //out.println(SystemUtils.HeadTitle("Proshop Edit Member Notice"));
   out.println(SystemUtils.HeadTitleEditor("Proshop Edit Member Notice"));
   
    //out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/assets/jquery/tiny_mce/tiny_mce.js\"></script>");

    out.println("<script type=\"text/javascript\">");
    out.println("tinyMCE.init({");

    // General options
    out.println("relative_urls : false,");      // convert all URLs to absolute URLs
    out.println("remove_script_host : false,"); // don't strip the protocol and host part of the URLs
    out.println("document_base_url : \"http://www1.foretees.com/\",");
    out.println("valid_children : \"+body[style]\",");
    
    out.println("mode : \"textareas\",");
    out.println("theme : \"advanced\",");
    out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager\",");

    out.println("theme_advanced_buttons1 : \"save,|,cut,copy,paste,pastetext,pasteword,|,undo,redo,|,tablecontrols,|,hr,advhr,|,link,unlink,anchor,image,insertimage,|,code\",");
    out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote\",");
    out.println("theme_advanced_buttons3 : \"\",");
    out.println("theme_advanced_buttons4 : \"\",");
    
    out.println("theme_advanced_fonts : \"Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Palatino Linotype=palatino linotype,palatino,book antiqua;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats\",");

    out.println("theme_advanced_toolbar_location : \"top\",");
    out.println("theme_advanced_toolbar_align : \"left\",");
    out.println("theme_advanced_resizing : true,");

    // Drop lists for link/image/media/template dialogs
    out.println("template_external_list_url : \"js/template_list.js\",");
    out.println("external_link_list_url : \"js/link_list.js\",");
    out.println("external_image_list_url : \"js/image_list.js\",");
    out.println("media_external_list_url : \"js/media_list.js\",");

    // Replace values for the template plugin
    out.println("template_replace_values : {");
    out.println("username : \"Some User\",");
    out.println("staffid : \"991234\"");

    out.println("}");

    out.println("});");

    out.println("</script>");
    out.println("<style type=\"text/css\"> body {text-align:center} </style>");      // so body will align on center
    out.println("</head>");   
   

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
              
         out.println("<table cellpadding=\"5\" border=\"1\" bgcolor=\"#336633\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
           
         if (copy == false && id > 0) {
            out.println("<b>Edit Member Notice</b><br>");
            out.println("<br>Change the desired information for the member notice below.");
            out.println("<br>Click on <b>Update</b> to submit the changes.");
            out.println("<br>Click on <b>Remove</b> to delete the notice.");
         } else {
            if (copy == false) {
               out.println("<b>Add Member Notice</b><br>");
               out.println("<br>Set the desired information for the notice below.<br>");
               out.println("Click on <b>Add</b> to create the new notice.");
            } else {
               out.println("<b>Copy Member Notice</b><br>");
               out.println("<br>Change the desired information for the notice below.<br>");
               out.println("Click on <b>Add</b> to create the new notice.");
               out.println("<br><br><b>NOTE:</b> You must change the name of the notice.");
            }
         }
         out.println("</font></td></tr></table><br>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
            out.println("<form action=\"Proshop_memNotice\" method=\"post\" target=\"bot\">");
            if (copy == true) {
               out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
            } else {
               if (id > 0) {    // if edit
                  out.println("<input type=\"hidden\" name=\"notice_id\" value=\"" + id + "\">");  // notice to edit
               }
            }
            out.println("<input type=\"hidden\" name=\"step2\" value=\"yes\">");  // notice to edit
            out.println("<tr><td width=\"500\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\">&nbsp;&nbsp;Member Notice Name:&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"text\" name=\"notice_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
               if (copy == false) {
                  out.println("<br>&nbsp;&nbsp;&nbsp;* Must be unique");
               } else {
                  out.println("<br>&nbsp;&nbsp;&nbsp;* Must be changed!!");
               }
               out.println("<br><br>");


            if (sess_activity_id == 0) {

                if (multi != 0) {

                   out.println("&nbsp;&nbsp;Select a Course:&nbsp;&nbsp;");
                   out.println("<select size=\"1\" name=\"course\">");

                   if (courseName.equals( "-ALL-" )) {             // if same as existing name

                      out.println("<option selected value=\"-ALL-\">ALL</option>");
                   } else {
                      out.println("<option value=\"-ALL-\">ALL</option>");
                   }

                   for (index=0; index < course.size(); index++) {

                      if (course.get(index).equals( courseName )) {             // if same as existing name

                         out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                      } else {
                         out.println("<option value=\"" + course.get(index) + "\">" + course.get(index) + "</option>");
                      }
                   }
                   out.println("</select>");
                   out.println("<br><br>");

                } else {

                   out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
                }

                Common_Config.displayTees(fb, out);                           // display the Tees option (f/b)

            }

            Common_Config.displayStartDate(s_month, s_day, s_year, out);  // display the Start Date prompt

            Common_Config.displayEndDate(e_month, e_day, e_year, out);    // display the End Date prompt

            Common_Config.displayStartTime(shr, smin, ssampm, out);       // display the Start Time prompt

            Common_Config.displayEndTime(ehr, emin, seampm, out);         // display the End Time prompt

            Common_Config.displayRecurr(mon, tue, wed, thu, fri, sat, sun, out);  // display Recurr prompt

            out.println("&nbsp;&nbsp;Display For (select all that apply):<br>");

            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

            out.println("<input type=\"checkbox\"" + (((teetime > 0)) ? " checked" : "") + " name=\"teetime\" value=\"1\">" +
                    "&nbsp;&nbsp;" + common_name + " Times - display notice when making/changing " + common_name.toLowerCase() + " times");


            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

            out.println("<input type=\"checkbox\"" + (((event > 0)) ? " checked" : "") + " name=\"event\" value=\"1\">" +
                    "&nbsp;&nbsp;Events - display notice when making/changing event registrations");

            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

            out.println("<input type=\"checkbox\" " + (((teesheet > 0)) ? " checked" : "") + " name=\"teesheet\" value=\"1\">" +
                    "&nbsp;&nbsp;" + ((sess_activity_id == 0) ? "Tee" : "Time") + " Sheet - display notice above " + ((sess_activity_id == 0) ? "tee" : "time") + " sheet in banner");

            if (sess_activity_id == 0) {
                out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

                out.println("<input type=\"checkbox\" " + (((teetime_cal > 0)) ? " checked" : "") + " name=\"teetime_cal\" value=\"1\">" +
                        "&nbsp;&nbsp;Tee Time Calendar Page (Make, Change, View Tee Times page) - display notice above calendars");
            }
            
            
            /*  Removed for now, may implement at a later date
            out.println("<br>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\"" + (((overrideColors > 0)) ? " checked" : "") + " name=\"overrideColors\" value=\"1\">&nbsp;&nbsp;Override Event & Restrictions colors on tee sheet (Tee Sheet only)");
            */

            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"checkbox\" " + (((proside > 0)) ? " checked" : "") + " name=\"proside\" value=\"1\">" +
                    "&nbsp;&nbsp;" + ((sess_activity_id == 0) ? "Proshop" : "Staff") + " Side - display notice for " + ((sess_activity_id == 0) ? "proshop" : "staff") + " users too");

            out.println("<br><br>");

            // output the selection boxes for each location
            if (sess_activity_id != 0) {

                // FlxRez
                out.println("&nbsp;&nbsp;Choose the locations for this Member Notice:&nbsp;&nbsp;");
                Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, false, con, out);

                out.println("<br><br>");

            }

            /**/
            out.println("&nbsp;&nbsp;Background Color of banner and " + ((sess_activity_id == 0) ? "tee" : common_name.toLowerCase()) + " times (for " + ((sess_activity_id == 0) ? "Tee" : "Time") + " Sheet option only):");

            out.println("<br><br>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            Common_Config.displayColorsAll(bgColor, out);       // output the color options

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
            out.println("<br><br>");

            /*
            out.println("&nbsp;&nbsp;Message to Display: <br>&nbsp;&nbsp;(Text will automatically wrap.  To force a line-break<br>&nbsp;&nbsp;please insert &lt;br&gt; into your message)");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<textarea name=\"message\" cols=\"40\" rows=\"8\">" + message + "</textarea>");
            */
            out.println("<input type=\"hidden\" name=\"color\" value=\"\">");       // no bg color - use editor

            
            out.println("<br>&nbsp;&nbsp;Message to Display:");

            out.println("<center><textarea name=\"message\" style=\"width:480px;height:240px\">");

            out.println(message);

            out.println("</textarea></center>");

            out.println("</p>");
            out.println("<p align=center><font size=\"3\">Click here for <a href=\"/" +rev+ "/web utilities/tiny_mce/TinyMCE-Content-User-Guide.pdf\" target=\"_blank\">Help with the Editor</a></font></p><BR>");  
    
              
            
            
            
            
         out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + name + "\">");

         out.println("<p align=\"center\">");
         if (copy == false && id > 0) {
            out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"submit\" name=\"Remove\" value=\"Remove\">");
         } else {
            out.println("<input type=\"submit\" name=\"Add\" value=\"Add\">");
         }
         out.println("</p>");
                 
      out.println("</font></td></tr></form></table>");
      out.println("</td></tr></table>");                       // end of main page table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_memNotice\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font></body></html>");

 }   // end of doPost   


 // ***************************************************************************
 //  Process the copy request from doGet - display a selection list of existing notices
 // ***************************************************************************

 private void doCopy(HttpServletRequest req, PrintWriter out, Connection con, int lottery, int sess_activity_id) {

   Statement stmt = null;
   ResultSet rs = null;

   String name = "";         // name of notice

   int id = 0;

   //
   //  Build the HTML page to display the existing notices
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Notices Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Copy a Member Notice</b><br>");
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to create a new Member Notice by copying an existing notice.<br>");
   out.println("Select the notice you wish to copy from the list below.");
   out.println("</font></td></tr></table>");
   out.println("<br><br>");

   //
   //  Get and display the existing notices
   //
   try {

      stmt = con.createStatement();

      rs = stmt.executeQuery("SELECT mem_notice_id, name FROM mem_notice WHERE activity_id = " + sess_activity_id + " ORDER BY name");

      if (rs.next()) {

         out.println("<font size=\"2\">");
         out.println("<p>Select the Member Notice you wish to copy.</p>");

         out.println("<form action=\"Proshop_memNotice\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");     // tell dopost its a copy
         out.println("<select size=\"1\" name=\"notice_id\">");

         // move cursor to begining of result set
         rs.beforeFirst();

         while (rs.next()) {

            id = rs.getInt("mem_notice_id");
            name = rs.getString("name");

            out.println("<option value=\"" +id+ "\">" +name+ "</option>");

         }  // end of while loop

         out.println("</select><br><br>");

         out.println("<input type=\"submit\" name=\"Continue\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");

      } else {            // no rest's exist

         out.println("<p><font size=\"2\">No Member Notices Currently Exist</p>");
      }

   } catch (Exception exc) {

       out.println("<BR><BR><H1>Database Access Error</H1>");
       out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");

   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { stmt.close(); }
       catch (Exception ignore) {}

   } // end of try


   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                // end of main page table
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_memNotice\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

 }


 // ***************************************************************************
 //  Process the add/copy request from doPost 
 // ***************************************************************************

 private void doAdd(boolean copy, int sess_activity_id, HttpServletRequest req, PrintWriter out, Connection con) {


   PreparedStatement pstmt = null;

   String name = "";
   String course = "";
   String fb = "";
   String message = "";
   String ssampm = "AM";
   String seampm = "AM";
   String bgColor = "";

   String temp = "";
   String msg = "";

   int count = 0;
   int teetime = 0;
   int event = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int smonth = 0;
   int sday = 0;
   int syear = 0;
   int shr = 0;
   int smin = 0;
   int sampm = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int ehr = 0;
   int emin = 0;
   int eampm = 0;
   int proside = 0;
   int teesheet = 0;
   int teetime_cal = 0;
   int overrideColors = 0;

   String locations_csv = Common_Config.buildLocationsString(req);

   //
   //  User wishes to edit the record - get parms passed
   //
   name = req.getParameter("notice_name");
   course = (req.getParameter("course") != null) ? req.getParameter("course") : "";
   fb = (req.getParameter("fb") != null) ? req.getParameter("fb") : "";
   
   message = (req.getParameter("message") != null) ? req.getParameter("message").trim() : "";

   proside = (req.getParameter("proside") != null && req.getParameter("proside").equals("1")) ? 1 : 0;
   teesheet = (req.getParameter("teesheet") != null && req.getParameter("teesheet").equals("1")) ? 1 : 0;
   //overrideColors = (req.getParameter("overrideColors") != null && req.getParameter("overrideColors").equals("1")) ? 1 : 0;   // Removed for now, may implement at a later date
   temp = req.getParameter("smonth");
   bgColor = req.getParameter("color");

   try {
      smonth = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("sday");

   try {
      sday = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("syear");

   try {
      syear = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("emonth");

   try {
      emonth = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("eday");

   try {
      eday = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("eyear");

   try {
      eyear = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("start_hr");

   try {
      shr = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("start_min");

   try {
      smin = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("end_hr");

   try {
      ehr = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("end_min");

   try {
      emin = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }


   temp = req.getParameter("start_ampm");

   if (temp.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
      ssampm = "PM";
   }

   try {
      sampm = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("end_ampm");

   if (temp.equals ( "12" )) {
      seampm = "PM";
   }

   try {
      eampm = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   if (req.getParameter("teetime") != null) {        // tee times ?

      teetime = 1;
   }
   if (req.getParameter("event") != null) {        //  events ?

      event = 1;
   }
   if (req.getParameter("teetime_cal") != null) {        // tee time calendar page ?

      teetime_cal = 1;
   }
   if (req.getParameter("mon") != null) {

      mon = 1;           // recurr check box selected
   }
   if (req.getParameter("tue") != null) {

      tue = 1;           // recurr check box selected
   }
   if (req.getParameter("wed") != null) {

      wed = 1;           // recurr check box selected
   }
   if (req.getParameter("thu") != null) {

      thu = 1;           // recurr check box selected
   }
   if (req.getParameter("fri") != null) {

      fri = 1;           // recurr check box selected
   }
   if (req.getParameter("sat") != null) {

      sat = 1;           // recurr check box selected
   }
   if (req.getParameter("sun") != null) {

      sun = 1;           // recurr check box selected
   }

   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;       // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;       // create a date field from input values
   edate = edate + (emonth * 100);
   edate = edate + eday;             // date = yyyymmdd (for comparisons)

   if (shr < 12) {                  // _hr specified as 01 - 12 (_ampm = 00 or 12)

      shr = shr + sampm;             // convert to military time (12 is always Noon or PM)
   }

   if (ehr < 12) {                 // ditto

      ehr = ehr + eampm;
   }

   int stime = shr * 100;
   stime = stime + smin;
   int etime = ehr * 100;
   etime = etime + emin;

   //
   //  verify the date and time fields
   //
   if (sdate > edate) {

      msg = "Sorry, the Start Date and End Date values are incorrect.";       // error message
      invData(msg, out);    // inform the user and return
      return;
   }
   if (stime > etime) {

      msg = "Sorry, the Start Time and End Time values are incorrect.";       // error message
      invData(msg, out);    // inform the user and return
      return;
   }

   //
   //  Validate minute values
   //
   if ((smin < 0) || (smin > 59)) {

      msg = "Start Minute parameter must be in the range of 00 - 59. " +
            " You entered:" + smin;
      invData(msg, out);    // inform the user and return
      return;
   }

   if ((emin < 0) || (emin > 59)) {

      msg = "End Minute parameter must be in the range of 00 - 59. " +
            " You entered:" + emin;
      invData(msg, out);    // inform the user and return
      return;
   }

   //
   //  Validate new name if it has changed
   //
   boolean error = SystemUtils.scanQuote(name);           // check for single quote

   if (error == true) {

      msg = "Apostrophes (single quotes) cannot be part of the Name.";
      invData(msg, out);    // inform the user and return
      return;
   }

   if (mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0) {     // if no recurr

      msg = "You must select at least one Recurrence.";
      invData(msg, out);
      return;
   }

   if (teetime == 0 && event == 0 && teesheet == 0 && teetime_cal == 0) {     // at least one must be selected

      msg = "You must select either Tee Times, Events, or Tee Sheet, Tee Time Calendar, or any combination of the these.";
      invData(msg, out);
      return;
   }

   if (message.equals( "" )) {     // if no message text

      msg = "You must specify a message.";
      invData(msg, out);
      return;
   }

   // make sure bgColor isn't "Default"
   if (bgColor.equalsIgnoreCase("Default")) {
       bgColor = "";
   }   


   //
   // Make sure if this notice is for an activity that it has locations assigned to it.
   //
   if (sess_activity_id != 0 && (locations_csv == null || locations_csv.equals(""))) {

       out.println("<!-- sess_activity_id=" + sess_activity_id + ", locations_csv=" + locations_csv + " -->");
       invData("You musy specify at least one location for this Member Notice.", out);
       return;

   }

 
   //
   //  add notice data to the database
   //

   
   try {

      pstmt = con.prepareStatement (
        "INSERT INTO mem_notice " +
            "(" +
                "name, sdate, stime, edate, etime, mon, tue, wed, thu, fri, sat, sun, teetime, event, " +
                "courseName, fb, message, proside, teesheet, bgColor, activity_id, locations, teetime_cal" +
            ") " +
        "VALUES " +
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in pstmt
      pstmt.setLong(2, sdate);
      pstmt.setInt(3, stime);
      pstmt.setLong(4, edate);
      pstmt.setInt(5, etime);
      pstmt.setInt(6, mon);
      pstmt.setInt(7, tue);
      pstmt.setInt(8, wed);
      pstmt.setInt(9, thu);
      pstmt.setInt(10, fri);
      pstmt.setInt(11, sat);
      pstmt.setInt(12, sun);
      pstmt.setInt(13, teetime);
      pstmt.setInt(14, event);
      pstmt.setString(15, course);
      pstmt.setString(16, fb);
      pstmt.setString(17, message);
      pstmt.setInt(18, proside);
      pstmt.setInt(19, teesheet);
      //pstmt.setInt(20, overrideColors);        // Removed for now, may implement at a later date
      pstmt.setString(20, bgColor);
      pstmt.setInt(21, sess_activity_id);
      pstmt.setString(22, locations_csv);
      pstmt.setInt(23, teetime_cal);

      pstmt.executeUpdate();

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR>Exception:   " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   } finally {

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Member Notice"));
   out.println("<BODY>");
//   SystemUtils.getProshopSubMenu(req, out, lottery);
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Member Notice Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the Member Notice has been added to the system database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   if (copy == false) {
      out.println("<form method=\"post\" action=\"Proshop_memNotice\">");
   } else {
      out.println("<form method=\"get\" action=\"Proshop_memNotice\">");
      out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
   }
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");

   out.close();

 }


 // ***************************************************************************
 //  Process the edit/delete request from doPost
 // ***************************************************************************

 private void doEdit(int id, int sess_activity_id, HttpServletRequest req, PrintWriter out, Connection con) {

   PreparedStatement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;


   String name = "";
   String oldName = "";
   String course = "";
   String fb = "";
   String message = "";
   String ssampm = "AM";
   String seampm = "AM";
   String bgColor = "";

   String temp = "";
   String msg = "";
     
   int count = 0;
   int teetime = 0;
   int event = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int smonth = 0;
   int sday = 0;
   int syear = 0;
   int shr = 0;
   int smin = 0;
   int sampm = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int ehr = 0;
   int emin = 0;
   int eampm = 0;
   int proside = 0;
   int teesheet = 0;
   int teetime_cal = 0;
   //int overrideColors = 0;        // Removed for now, may implement at a later date
     

   //
   //  Verify that we received an id
   //
   if (id < 1) {
     
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H2>System Sequence Error</H2>");
      out.println("<BR><BR>Sorry, we are unable to identify the record you wish to process.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;  
   }

   if (req.getParameter("Remove") != null) {       // user wish to delete the record??

      if (req.getParameter("oldName") != null) {       // is this the first call??

         //
         //  First call for delete - request a confirmation
         //
         name = req.getParameter("oldName");           // get the name of the notice

         out.println(SystemUtils.HeadTitle("Delete Notice Confirmation"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H3>Remove Member Notice Confirmation</H3><BR>");
         out.println("<BR>Please confirm that you wish to remove this Member Notice: <b>" + name + "</b><br>");

         out.println("<form action=\"Proshop_memNotice\" method=\"post\" target=\"bot\">");
         out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS RECORD?");

         out.println("<input type=\"hidden\" name=\"notice_id\" value =\"" + id + "\">");
         out.println("<input type=\"hidden\" name=\"step2\" value =\"yes\">");
         out.println("<br><br><input type=\"submit\" value=\"Yes - Delete\" name=\"Remove\">");
         out.println("</form><font size=\"2\">");

         out.println("<form method=\"get\" action=\"Proshop_memNotice\">");
         out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");

      } else {         // delete confirmed - delete it

         //
         //  User wishes to delete the member notice
         //
         try {

             stmt = con.prepareStatement (
                     "DELETE FROM mem_notice WHERE mem_notice_id = ?");

             stmt.clearParameters();
             stmt.setInt(1, id);

             count = stmt.executeUpdate();

         } catch (Exception exc) {

             dbError(out, exc);

         } finally {

             try { stmt.close(); }
             catch (Exception ignore) {}

         }


         out.println(SystemUtils.HeadTitle("Delete Notice Confirmation"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");

         if (count > 0) {       // if notice deleted

            out.println("<BR><H3>Member Notice Has Been Removed</H3><BR>");
            out.println("<BR><BR>Thank you, the member notice has been removed from the database.");

         } else {

            out.println("<BR><H3>Member Notice Removal Failed</H3><BR>");
            out.println("<BR><BR>Sorry, we were not able to remove the member notice at this time.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact customer support.");
         }

         out.println("<BR><BR>");
         out.println("<font size=\"2\">");

         out.println("<form method=\"get\" action=\"Proshop_memNotice\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
      }

   } else {
     
      //
      //  User wishes to edit the record - get parms passed
      //
      name = req.getParameter("notice_name");
      course = req.getParameter("course");             //  course name
      fb = req.getParameter("fb");                     //  Front/Back indicator
      message = req.getParameter("message");
      proside = (req.getParameter("proside") != null && req.getParameter("proside").equals("1")) ? 1 : 0;
      teesheet = (req.getParameter("teesheet") != null && req.getParameter("teesheet").equals("1")) ? 1 : 0;
      //overrideColors = (req.getParameter("overrideColors") != null && req.getParameter("overrideColors").equals("1")) ? 1 : 0;   // Removed for now, may implement at a later date
      temp = req.getParameter("smonth");
      bgColor = req.getParameter("color");
      
      message = message.trim();

      try {
         smonth = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("sday");

      try {
         sday = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("syear");

      try {
         syear = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("emonth");

      try {
         emonth = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("eday");

      try {
         eday = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("eyear");

      try {
         eyear = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("start_hr");

      try {
         shr = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("start_min");

      try {
         smin = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("end_hr");

      try {
         ehr = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("end_min");

      try {
         emin = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }


      temp = req.getParameter("start_ampm");

      if (temp.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
         ssampm = "PM";
      }

      try {
         sampm = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("end_ampm");

      if (temp.equals ( "12" )) {
         seampm = "PM";
      }

      try {
         eampm = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      if (req.getParameter("teetime") != null) {        // tee times ?

         teetime = 1;          
      }
      if (req.getParameter("teetime_cal") != null) {        // tee time calendar page ?  (Member_select)

         teetime_cal = 1;          
      }
      if (req.getParameter("event") != null) {        //  events ?

         event = 1;
      }
      if (req.getParameter("mon") != null) {

         mon = 1;           // recurr check box selected
      }
      if (req.getParameter("tue") != null) {

         tue = 1;           // recurr check box selected
      }
      if (req.getParameter("wed") != null) {

         wed = 1;           // recurr check box selected
      }
      if (req.getParameter("thu") != null) {

         thu = 1;           // recurr check box selected
      }
      if (req.getParameter("fri") != null) {

         fri = 1;           // recurr check box selected
      }
      if (req.getParameter("sat") != null) {

         sat = 1;           // recurr check box selected
      }
      if (req.getParameter("sun") != null) {

         sun = 1;           // recurr check box selected
      }

      //
      //  adjust some values for the table
      //
      long sdate = syear * 10000;       // create a date field from input values
      sdate = sdate + (smonth * 100);
      sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

      long edate = eyear * 10000;       // create a date field from input values
      edate = edate + (emonth * 100);
      edate = edate + eday;             // date = yyyymmdd (for comparisons)

      int temp_shr = 0;
      int temp_ehr = 0;

      if (shr != 12) {                  // _hr specified as 01 - 12 (_ampm = 00 or 12)

         temp_shr = shr + sampm;             // convert to military time (12 is always Noon or PM)
         
      } else if (sampm == 12) {
          
         temp_shr = shr;
      }

      if (ehr != 12) {                 // ditto

         temp_ehr = ehr + eampm;
         
      } else if (eampm == 12) {
         
         temp_ehr = ehr;
      }

      int stime = temp_shr * 100;
      stime = stime + smin;
      int etime = temp_ehr * 100;
      etime = etime + emin;

      //
      //  verify the date and time fields
      //
      if (sdate > edate) {

         msg = "Sorry, the Start Date and End Date values are incorrect.";       // error message
         invData(msg, out);    // inform the user and return
         return;
      }
      if (stime > etime) {

         msg = "Sorry, the Start Time and End Time values are incorrect.";       // error message
         invData(msg, out);    // inform the user and return
         return;
      }

      //
      //  Validate minute values
      //
      if ((smin < 0) || (smin > 59)) {

         msg = "Start Minute parameter must be in the range of 00 - 59. " +
               " You entered:" + smin;
         invData(msg, out);    // inform the user and return
         return;
      }

      if ((emin < 0) || (emin > 59)) {

         msg = "End Minute parameter must be in the range of 00 - 59. " +
               " You entered:" + emin;
         invData(msg, out);    // inform the user and return
         return;
      }

      //
      //  Validate new name if it has changed
      //
      boolean error = SystemUtils.scanQuote(name);           // check for single quote

      if (error == true) {

         msg = "Apostrophes (single quotes) cannot be part of the Name.";
         invData(msg, out);    // inform the user and return
         return;
      }

      if (mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0) {     // if no recurr

         msg = "You must select at least one Recurrence.";
         invData(msg, out);
         return;
      }

      if (teetime == 0 && event == 0 && teesheet == 0 && teetime_cal == 0) {     // at least one must be selected

         msg = "You must select either Tee Times, Events, or Tee Sheet, Tee Time Calendar Page, or any combination of the these.";
         invData(msg, out);
         return;
      }

      if (message.equals( "" )) {     // if no message text

         msg = "You must specify a message.";
         invData(msg, out);
         return;
      }


      String locations_csv = Common_Config.buildLocationsString(req);


      //
      //  Determine if the user has been prompted for confirmation yet
      //
      if (req.getParameter("oldName") != null) {        // is this the first call??

         oldName = req.getParameter("oldName");         // get the name of the notice

         //
         // Make sure if this notice is for an activity that it has locations assigned to it.
         //
         if (sess_activity_id != 0 && (locations_csv == null || locations_csv.equals(""))) {

             out.println("<!-- sess_activity_id=" + sess_activity_id + ", locations_csv=" + locations_csv + " -->");
             invData("You musy specify at least one location for this Member Notice.", out);
             return;
         }

         //
         //  If name has changed check for dup
         //
         if (!oldName.equals( name )) {    // if name has changed

            try {

               pstmt = con.prepareStatement (
                       "SELECT mon FROM mem_notice WHERE name = ?");

               pstmt.clearParameters();
               pstmt.setString(1, name);

               rs = pstmt.executeQuery();

               if (rs.next()) {

                  dupMem(out);    // member notice exists - inform the user and return
                  return;
               }

            } catch (Exception ignored) {

            } finally {

               try { pstmt.close(); }
               catch (Exception ignore) {}

            }

         }

         //
         //  First call for this - prompt user for confirmation
         //
         out.println(SystemUtils.HeadTitle("Update Notice Confirmation"));
         out.println("<BODY><CENTER><BR>");
         out.println("<br>");
         out.println("<H3>Update Member Notice Confirmation</H3><BR>");
         out.println("<BR>Please confirm the following updated parameters for the Notice:<br><b>" + name + "</b><br><br>");

         out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"7\">");

         if (sess_activity_id == 0) {

             if (!course.equals( "" )) {

                out.println("<tr><td align=\"right\">");           // all cols were 200 wide !!!
                out.println("Course Name:");
                out.println("</td><td align=\"left\">");
                out.println(course);
                out.println("</td></tr>");
             }

             out.println("<tr><td align=\"right\">");
             out.println("Tees:");
             out.println("</td><td align=\"left\">");
             out.println(fb);
             out.println("</td></tr>");
         
         } else {

           String tmp = "";

           ArrayList<Integer> locations = new ArrayList<Integer>();
           locations.clear();
           StringTokenizer tok = new StringTokenizer( locations_csv, "," );
           while ( tok.hasMoreTokens() ) {
              tmp = tok.nextToken();
              try {
                  locations.add(Integer.parseInt(tmp));
              } catch (Exception exc) {
                  out.println("<!-- locations_csv=" + locations_csv + ", tmp=" + tmp  + ", size=" + locations.size() + ", err=" + exc.toString() + " -->");
                  return;
              }
           }
           tmp = "";
           int i2 = 0;
           for (int i = 0; i < locations.size(); i++) {

              tmp += getActivity.getActivityName(locations.get(i), con) + ", ";
              i2++;
              if (i2 == 4) { tmp += "<br>"; i2 = 0; }
           }

           if (tmp.endsWith("<br>")) {
               tmp = tmp.substring(0, tmp.length() - 6);
           } else if (!tmp.equals("")) {
               tmp = tmp.substring(0, tmp.length() - 2);
           }

           out.println("<tr><td align=\"right\">");
           out.println("Locations:");
           out.println("</td><td align=\"left\">");
           out.println(tmp);
           out.println("</td></tr>");

           course = "";
           fb = "";

         }

         out.println("<tr><td align=\"right\">");
         out.println("Start Date:");
         out.println("</td><td align=\"left\">");
         out.println(smonth + "/" + sday + "/" + syear);
         out.println("</td></tr>");

         out.println("<tr><td align=\"right\">");
         out.println("End Date:");
         out.println("</td><td align=\"left\">");
         out.println(emonth + "/" + eday + "/" + eyear);
         out.println("</td></tr>");

         out.println("<tr><td align=\"right\">");
         out.println("Start Time:");
         out.println("</td><td align=\"left\">");

         out.println(shr + ":" + Utilities.ensureDoubleDigit(smin) + "  " + ssampm);

         out.println("</td></tr>");

         out.println("<tr><td align=\"right\">");
         out.println("End Time:");
         out.println("</td><td align=\"left\">");

         out.println(ehr + ":" + Utilities.ensureDoubleDigit(emin) + "  " + seampm);

         out.println("</td></tr>");

         out.println("<tr><td align=\"right\">");
         out.println("Recurrence:");
         out.println("</td><td align=\"left\">");
         if (mon > 0) {
            out.println("Monday&nbsp;&nbsp;&nbsp;");
         }
         if (tue > 0) {
            out.println("Tuesday&nbsp;&nbsp;&nbsp;");
         }
         if (wed > 0) {
            out.println("Wednesday&nbsp;&nbsp;&nbsp;");
         }
         if (thu > 0) {
            out.println("Thursday&nbsp;&nbsp;&nbsp;");
         }
         if (fri > 0) {
            out.println("Friday&nbsp;&nbsp;&nbsp;");
         }
         if (sat > 0) {
            out.println("Saturday&nbsp;&nbsp;&nbsp;");
         }
         if (sun > 0) {
            out.println("Sunday");
         }
         out.println("</td></tr>");

         out.println("<tr><td align=\"right\">");
         out.println("Display Message For:");
         out.println("</td><td align=\"left\">");
         if (teetime > 0) {
            out.println("Tee Times&nbsp;&nbsp;&nbsp;");
         }
         if (event > 0) {
            out.println("Events&nbsp;&nbsp;&nbsp;");
         }
         if (teesheet > 0) {
            out.println("Tee Sheet&nbsp;&nbsp;&nbsp;");
         }
         if (teetime_cal > 0) {
            out.println("Tee Time Calendar Page&nbsp;&nbsp;&nbsp;");
         }
         if (proside > 0) {
            out.println("<nobr>Proshop Side</nobr>");
         }
         out.println("</td></tr>");
         
         /*     // Removed for now, may implement at a later date
         if (teesheet > 0) {    // message only applicable if tee sheet option selected
             out.println("<tr><td width=\"200\" align=\"right\">");
             out.println("<nobr>&nbsp;&nbsp;Override Event & Restriction Colors:&nbsp;&nbsp;</nobr>");
             out.println("</td><td width=\"200\" align=\"left\">");
             if (overrideColors > 0) {
                out.println("&nbsp;&nbsp;&nbsp;Yes");
             } else {
                out.println("&nbsp;&nbsp;&nbsp;No");
             }
             out.println("</td></tr>");
         }
         */
         
                 // no longer needed - use editor
         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("Background Color:");
         out.println("</td>");
         if (!bgColor.equals("") && !bgColor.equals("Default")) {     // if color not default
             out.println("<td width=\"200\" bgcolor=\"" + bgColor + "\" align=\"left\">" + bgColor);
         } else {
             out.println("<td width=\"200\" align=\"left\">Default (None)");
         }
         out.println("</td></tr>");
         

         out.println("<form action=\"Proshop_memNotice\" method=\"post\" target=\"bot\">");
         
         out.println("<tr><td align=\"right\">");
         out.println("Message:");
         out.println("</td><td align=\"left\">");
         out.println("<textarea name=\"message\" style=\"width:480px;height:240px\">");
         out.println(message);
         out.println("</textarea>");
         out.println("</td></tr>");

         out.println("</table>");

         //out.println("<form action=\"Proshop_memNotice\" method=\"post\" target=\"bot\">");
         out.println("<BR>ARE YOU SURE YOU WANT TO UPDATE THIS RECORD?");
         out.println("<input type=\"hidden\" name=\"notice_id\" value = \"" + id + "\">");
         out.println("<input type=\"hidden\" name=\"locations_csv\" value = \"" + locations_csv + "\">");
         out.println("<input type=\"hidden\" name=\"notice_name\" value = \"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"smonth\" value = \"" + smonth + "\">");
         out.println("<input type=\"hidden\" name=\"sday\" value = \"" + sday + "\">");
         out.println("<input type=\"hidden\" name=\"syear\" value = \"" + syear + "\">");
         out.println("<input type=\"hidden\" name=\"emonth\" value = \"" + emonth + "\">");
         out.println("<input type=\"hidden\" name=\"eday\" value = \"" + eday + "\">");
         out.println("<input type=\"hidden\" name=\"eyear\" value = \"" + eyear + "\">");
         out.println("<input type=\"hidden\" name=\"start_hr\" value = \"" + shr + "\">");
         out.println("<input type=\"hidden\" name=\"start_min\" value = \"" + smin + "\">");
         out.println("<input type=\"hidden\" name=\"start_ampm\" value = \"" + sampm + "\">");
         out.println("<input type=\"hidden\" name=\"end_hr\" value = \"" + ehr + "\">");
         out.println("<input type=\"hidden\" name=\"end_min\" value = \"" + emin + "\">");
         out.println("<input type=\"hidden\" name=\"end_ampm\" value = \"" + eampm + "\">");
         if (mon > 0) {
            out.println("<input type=\"hidden\" name=\"mon\" value = \"" + mon + "\">");
         }
         if (tue > 0) {
            out.println("<input type=\"hidden\" name=\"tue\" value = \"" + tue + "\">");
         }
         if (wed > 0) {
            out.println("<input type=\"hidden\" name=\"wed\" value = \"" + wed + "\">");
         }
         if (thu > 0) {
            out.println("<input type=\"hidden\" name=\"thu\" value = \"" + thu + "\">");
         }
         if (fri > 0) {
            out.println("<input type=\"hidden\" name=\"fri\" value = \"" + fri + "\">");
         }
         if (sat > 0) {
            out.println("<input type=\"hidden\" name=\"sat\" value = \"" + sat + "\">");
         }
         if (sun > 0) {
            out.println("<input type=\"hidden\" name=\"sun\" value = \"" + sun + "\">");
         }
         if (teetime > 0) {
            out.println("<input type=\"hidden\" name=\"teetime\" value = \"" + teetime + "\">");
         }
         if (teetime_cal > 0) {
            out.println("<input type=\"hidden\" name=\"teetime_cal\" value = \"" + teetime_cal + "\">");
         }
         if (event > 0) {
            out.println("<input type=\"hidden\" name=\"event\" value = \"" + event + "\">");
         }
         if (teesheet > 0) {
            out.println("<input type=\"hidden\" name=\"teesheet\" value = \"" + teesheet + "\">");
            
            /*        // Removed for now, may implement at a later date
            if (overrideColors > 0) {
                out.println("<input type=\"hidden\" name=\"overrideColors\" value = \"" + overrideColors + "\">");
            }
            */
         }
         out.println("<input type=\"hidden\" name=\"proside\" value = \"" + proside + "\">");
         
         if (bgColor.equals("") || bgColor.equals("Default")) {
             out.println("<input type=\"hidden\" name=\"color\" value=\"\">");
         } else {
             out.println("<input type=\"hidden\" name=\"color\" value=\"" + bgColor + "\">");
         }
         out.println("<input type=\"hidden\" name=\"course\" value = \"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value = \"" + fb + "\">");
         //out.println("<input type=\"hidden\" name=\"message\" value = \"" + message + "\">");
         out.println("<input type=\"hidden\" name=\"step2\" value = \"yes\">");

         out.println("<br><input type=\"submit\" value=\"Yes - Continue\" name=\"Update\">");
         out.println("</form><font size=\"2\">");

         out.println("<form method=\"get\" action=\"Proshop_memNotice\">");
         out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
           
  
      } else {
        
         //
         //  Confirmation received - update the record
         //
         
         // make sure bgColor isn't "Default"
         if (bgColor.equalsIgnoreCase("Default")) {
             bgColor = "";
         }

         if (sess_activity_id != 0) {

             course = "";
             fb = "";
             locations_csv = req.getParameter("locations_csv");
         }

         try {

            //
            //  Update the record in the member notice table
            //
            pstmt = con.prepareStatement (
              "UPDATE mem_notice " +
              "SET " +
                  "name = ?, sdate = ?, stime = ?, edate = ?, etime = ?, " +
                  "mon = ?, tue = ?, wed = ?, thu = ?, fri = ?, sat = ?, sun = ?, " +
                  "teetime = ?, event = ?, courseName = ?, fb = ?, message = ?, proside = ?, " +
                  "teesheet = ?, bgColor = ?, locations = ?, teetime_cal = ? " +
              "WHERE mem_notice_id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, name);
            pstmt.setLong(2, sdate);
            pstmt.setInt(3, stime);
            pstmt.setLong(4, edate);
            pstmt.setInt(5, etime);
            pstmt.setInt(6, mon);
            pstmt.setInt(7, tue);
            pstmt.setInt(8, wed);
            pstmt.setInt(9, thu);
            pstmt.setInt(10, fri);
            pstmt.setInt(11, sat);
            pstmt.setInt(12, sun);
            pstmt.setInt(13, teetime);
            pstmt.setInt(14, event);
            pstmt.setString(15, course);
            pstmt.setString(16, fb);
            pstmt.setString(17, message);
            pstmt.setInt(18, proside);
            pstmt.setInt(19, teesheet);
          //pstmt.setInt(20, overrideColors);        // Removed for now, may implement at a later date
            pstmt.setString(20, bgColor);
            pstmt.setString(21, locations_csv);
            pstmt.setInt(22, teetime_cal);
            pstmt.setInt(23, id);

            pstmt.executeUpdate();

         } catch (Exception exc) {

            dbError(out, exc);

         } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

         }

         //
         // Database updated - inform user
         //
         out.println(SystemUtils.HeadTitle("Proshop Update Member Notice"));
         out.println("<BODY>");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Member Notice Has Been Updated</H3>");
         out.println("<BR><BR>Thank you, the Member Notice has been updated.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"Proshop_memNotice\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
      }
  
   } // end here to update and not delete
     
   out.close();

 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception exc) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Exception: " + exc);
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(String msg, PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>" + msg + "<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

 // *********************************************************
 // Notice already exists
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

}
