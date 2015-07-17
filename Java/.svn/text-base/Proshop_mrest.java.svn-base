/***************************************************************************************     
 *   Proshop_mrest:  This servlet will process the 'Member Restrictions' request from
 *                    the Proshop's Config page.
 *
 *
 *   called by:  proshop menu (to doGet) and Proshop_mrest (to doPost from HTML built here)
 *
 *   created: 12/18/2001   Bob P.
 *
 *   last updated:
 *
 *        5/24/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        2/24/10   Added "allow_lesson" option for use for FlxRez activity restrictions.  If yes, lesson bookings can override this restriction.
 *        1/20/10   Add locations_csv support for Activities
 *        9/10/09   Now passing mrest_id when coming from the tee sheet to edit a restriction
 *        9/09/09   Now passing activity_id when the "Delete" button is clicked so the processing can identify the restriction to be removed
 *        8/19/09   Add GenRez support for Activities
 *        8/31/09   Changed grabbing of mships from club5 to mship5
 *        2/06/09   Default the year selections for the suspension form to 2009 if no other date selected
 *       12/19/08   Dynamically resize suspension iframe to fit suspension content and other display tweaks
 *       12/16/08   Modified restriction suspension processing, no longer reading 12AM as 1200 and 12PM as 2400, but 0000 and 1200
 *       10/23/08   Add restriction suspension processing (display, add/edit/delete)
 *        8/15/08   Add more years to the start and end dates until we come up with a better method.
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        1/24/05   Ver 5 - change club2 to club5.
 *        7/21/04   Correct problem where the course name was not used in doPost on copy.
 *        6/18/04   Add 'Make Copy' option to allow pro to copy an existing rest.
 *        5/12/04   Add sheet= parm for call from Proshop_sheet.
 *       12/15/03   Add Show=yes/no for Eldorado CC. Option to show or not to show restriction
 *                  on tee sheet legend.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/07/03   Enhancements for Version 2 of the software.
 *                  Add support for multi courses, memtypes and mship types and F/B option.
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


public class Proshop_mrest extends HttpServlet {


   String zero = "00";

   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

  
 //**************************************************
 // Process the initial request from Proshop_main
 //**************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   if (req.getParameter("new") != null) {

      doPost(req, resp);
      return;
   }
   
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
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }
     
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   
   //
   //  Check if call is to copy an existing restriction
   //
   if (req.getParameter("copy") != null) {

      doCopy(req, out, con, lottery, sess_activity_id);        // process the copy request
      return;
   }
   
   if (req.getParameter("susp") != null) {
       
      printSuspensions(req, sess_activity_id, con, out);       // This is a suspension request (printSuspensions)
      return;
   }
   
   //
   // Define some parms to use in the html
   //
   String name = "";       // name of restriction
   int id = 0;             // restriction id
   int s_hour = 0;         // start time hr
   int s_min = 0;          // start time min
   int e_hour = 0;         // end time hr
   int e_min = 0;          // end time min
   int s_year = 0;         // start year
   int s_month = 0;        // start month
   int s_day = 0;          // start day
   int e_year = 0;         // end year
   int e_month = 0;        // end month
   int e_day = 0;          // end day
   int multi = 0;          // day
   int activity_id = 0;    // activity uid
   int allow_lesson = 0;   // allow lessons during

   String recurr = "";     // recurrence
   String color = "";      // color for restriction displays
   String courseName = ""; // name of course
   String fb = "";         // Front/back Indicator
   String s_ampm = "";
   String e_ampm = "";
   String locations_csv = "";

   boolean b = false;

   //
   //  First, see if multi courses are supported for this club
   //
   try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi FROM club5");
        if (rs.next()) multi = rs.getInt(1);

   } catch (Exception ignore) {

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

   }

    //
    //  Build the HTML page to display the existing restrictions
    //
    out.println(SystemUtils.HeadTitle("Proshop Member Restrictions Page"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
        
      out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Member Restrictions</b><br>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>To change or remove a restriction, click on the Select button within the restriction.");
      out.println("<br></td></tr></table>");
      out.println("<br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#F5F5DC\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"10\" align=\"center\">");
      } else {
         out.println("<td colspan=\"9\" align=\"center\">");
      }
      out.println("<font size=\"3\">");
      out.println("<p align=\"center\"><b>Active Member Restrictions</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#F5F5DC\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Restriction Name</b></p>");
      out.println("</font></td>");
      if ( sess_activity_id == 0 ) {
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
          out.println("<font size=\"2\"><p><b>Locations</b></p>");
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
      out.println("<font size=\"2\"><p><b>Recurrence</b></p>");
      out.println("</font></td>");
      if (sess_activity_id != 0) {
          out.println("<td align=\"center\">");
          out.println("<font size=\"2\"><p><b>Allow<br>Lesson?</b></p>");
          out.println("</font></td>");
      }
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Color</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing restrictions (one table row per restriction)
   //
   try {

      String in = getActivity.buildInString(sess_activity_id, 1, con);

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM restriction2 WHERE activity_id IN (" + in + ") ORDER BY name");

      while (rs.next()) {

         b = true;                     // indicate restrictions exist

         id = rs.getInt("id");
         activity_id = rs.getInt("activity_id");
         name = rs.getString("name");
         s_month = rs.getInt("start_mm");
         s_day = rs.getInt("start_dd");
         s_year = rs.getInt("start_yy");
         s_hour = rs.getInt("start_hr");
         s_min = rs.getInt("start_min");
         e_month = rs.getInt("end_mm");
         e_day = rs.getInt("end_dd");
         e_year = rs.getInt("end_yy");
         e_hour = rs.getInt("end_hr");
         e_min = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         color = rs.getString("color");
         courseName = rs.getString("courseName");
         fb = rs.getString("fb");
         locations_csv = rs.getString("locations");
         allow_lesson = rs.getInt("allow_lesson");

         //
         //  some values must be converted for display
         //
         s_ampm = " AM";         // default to AM
         if (s_hour > 12) {
            s_ampm = " PM";
            s_hour = s_hour - 12;                // convert to 12 hr clock value
         }

         if (s_hour == 12) {
            s_ampm = " PM";
         }

         e_ampm = " AM";         // default to AM
         if (e_hour > 12) {
            e_ampm = " PM";
            e_hour = e_hour - 12;                  // convert to 12 hr clock value
         }

         if (e_hour == 12) {
            e_ampm = " PM";
         }


         if (color.equals( "Default" )) {

            out.println("<tr bgcolor=\"#F5F5DC\">");
         } else {
            out.println("<tr bgcolor=" + color + ">");
         }
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_mrest\" target=\"bot\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +name+ "</p>");
         out.println("</font></td>");
         if ( sess_activity_id == 0 ) {
              if (multi != 0) {
                 out.println("<td align=\"center\">");
                 out.println("<font size=\"2\"><p>" +courseName+ "</b></p>");
                 out.println("</font></td>");
              }
              out.println("<td align=\"center\">");
              out.println("<font size=\"2\"><p>" +fb+ "</p>");
              out.println("</font></td>");
         } else {
             /*
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p>" + getActivity.getActivityName(activity_id, con) + "</p>");
             out.println("</font></td>");
             */
             
             out.println("<td align=\"center\">");

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

             out.println("<font size=\"2\"><p>" + tmp + "</p></font></td>");

         }
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +s_month+ "/" + s_day + "/" + s_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +e_month+ "/" + e_day + "/" + e_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" + s_hour + ":" + Utilities.ensureDoubleDigit(s_min) + "  " + s_ampm + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" + e_hour + ":" + Utilities.ensureDoubleDigit(e_min) + "  " + e_ampm + "</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +recurr+ "</p>");
         out.println("</font></td>");
         if (sess_activity_id != 0) {
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p>" + (allow_lesson == 1 ? "Yes" : "No") + "</p>");
             out.println("</font></td>");
         }
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +color+ "</p>");
         out.println("</font></td>");

         out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");       // pass the restriction id
         out.println("<input type=\"hidden\" maxlength=\"30\" name=\"name\" value=\"" + name + "\">");    // must pass whole name!!!!
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
         out.println("<td align=\"center\">");
         out.println("<p>");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");

      }  // end of while loop

      stmt.close();

      if (!b) {
        
         out.println("</font><font size=\"2\"><p>No Member Restrictions Currently Exist</p>");
      }

   } catch (Exception exc) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</table></font>");                   // end of mrest table
   out.println("</td></tr></table>");                // end of main page table
        
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\" Return \" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font><br></body></html>");
   out.close();

 }  // end of doGet

 //
 //****************************************************************
 // Process the form request from Proshop_mrest page displayed above.
 //
 // Use the name provided to locate the restriction record and then display
 // the record to the user and prompt for edit or delete action. 
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
     
   String [] month_table = { "inv", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                             "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

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
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
     
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }

   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   int default_activity_id = sess_activity_id;

   boolean copy = false;

   int id = 0;
   long s_date = 0;
   long e_date = 0;
   int s_year = 0;
   int s_month = 0;
   int s_day = 0;
   int e_year = 0;
   int e_month = 0;
   int e_day = 0;
   int shr = 0;
   int smin = 0;
   int ehr = 0;
   int emin = 0;
   int multi = 0;        // multiple course support option
   int index = 0;
   int i = 0;
   int activity_id = 0;
   int allow_lesson = 0;

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   try { id = Integer.parseInt(req.getParameter("id")); }
   catch (Exception ignore) {}

   String color = "";
   String recur = "";
   String fb = "";
   String show = "";
   String oldCourse = "";
   String courseName = "";
   String courseName2 = "";
   String sheet = "no";                 // default to NOT a call from Proshop_sheet
   String locations_csv = "";
   
   //
   // Get the name parameter from the hidden input field
   //
   String name = ""; //req.getParameter("name");

   if (req.getParameter("susp") != null) {
       
      printSuspensions(req, sess_activity_id, con, out);       // This is a suspension request (printSuspensions)
      return;
   }
   
   if (req.getParameter("sheet") != null) {

      sheet = req.getParameter("sheet");
   }

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy below)
   }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   ArrayList<String> course = new ArrayList<String>();      // unlimited courses

   String [] mem = new String [parm.MAX_Mems+1];            // max of 24 member types
   String [] smem = new String [parm.MAX_Mems+1];          

   String [] mship = new String [parm.MAX_Mships+1];        // max of 24 mship types
   String [] smship = new String [parm.MAX_Mships+1];   

   // Init arrays
   for (int j=0; j< parm.MAX_Mships+1; j++) {
       mship[j] = "";
       smship[j] = "";
   }

   // Init arrays
   for (int j=0; j< parm.MAX_Mems+1; j++) {
       mem[j] = "";
       smem[j] = "";
   }

   //
   // Get the restriction from the restriction table
   //
   try {

      // if we are here for an existing restriction the load it up
      if (id != 0) {

          pstmt = con.prepareStatement (
                   "SELECT * FROM restriction2 WHERE id = ?");

          pstmt.clearParameters();
          pstmt.setInt(1, id);
          rs = pstmt.executeQuery();

          if ( rs.next() ) {

             //
             //  Found the restriction record - get it
             //
             name = rs.getString("name");
             locations_csv = rs.getString("locations");
             activity_id = rs.getInt("activity_id");
             s_date = rs.getLong("sdate");
             s_month = rs.getInt("start_mm");
             s_day = rs.getInt("start_dd");
             s_year = rs.getInt("start_yy");
             shr = rs.getInt("start_hr");
             smin = rs.getInt("start_min");
             e_date = rs.getLong("edate");
             e_month = rs.getInt("end_mm");
             e_day = rs.getInt("end_dd");
             e_year = rs.getInt("end_yy");
             ehr = rs.getInt("end_hr");
             emin = rs.getInt("end_min");
             recur = rs.getString("recurr");
             color = rs.getString("color");
             courseName = rs.getString("courseName");
             fb = rs.getString("fb");
             show = rs.getString("showit");
             allow_lesson = rs.getInt("allow_lesson");

             for (i=1; i<parm.MAX_Mems+1; i++) {
                mem[i] = rs.getString("mem" +i);
             }
             for (i=1; i<parm.MAX_Mships+1; i++) {
                mship[i] = rs.getString("mship" +i);
             }

          }

          pstmt.close();              // close the stmt

      } else {

          // here for new so lets set any default we want here
          Calendar cal = new GregorianCalendar();
          s_year = cal.get(Calendar.YEAR);
          e_year = s_year;
          e_day = 31;

      }

      oldCourse = courseName;    // save course name in case it changes

      //
      //  check if multi and load up the member types
      //
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi, mem1, mem2, mem3, mem4, mem5, mem6, mem7, mem8, " +
                             "mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, " +
                             "mem17, mem18, mem19, mem20, mem21, mem22, mem23, mem24 " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt("multi");
           
         for (i=1; i<parm.MAX_Mems+1; i++) {
            smem[i] = rs.getString("mem" +i);
         }
      }

      stmt.close();

      // load our membership types from the mship table
      pstmt = con.prepareStatement("SELECT mship FROM mship5 WHERE activity_id = ? LIMIT " + parm.MAX_Mships);
      pstmt.clearParameters();
      pstmt.setInt(1, sess_activity_id);

      rs = pstmt.executeQuery();

      i = 1;

      while (rs.next()) {
          smship[i] = rs.getString("mship");
          i++;
      }

      pstmt.close();

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

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

   String ssampm = "AM";     // AM or PM for display (start hour)
   String seampm = "AM";     // AM or PM for display (end hour)
   int sampm = 0;
   int eampm = 0;

   if (shr > 12) {
      
      shr = shr - 12;        // convert to 12 hour value
      ssampm = "PM";         // indicate PM
      sampm = 12;
   }

   if (shr == 12) {
      ssampm = "PM";         // indicate PM
   }

   if (ehr > 12) {

      ehr = ehr - 12;        // convert to 12 hour value
      seampm = "PM";         // indicate PM
      eampm = 12;
   }

   if (ehr == 12) {
      seampm = "PM";         // indicate PM
   }

   String alphaSmonth = month_table[s_month];  // get name for start month
   String alphaEmonth = month_table[e_month];  // get name for end month

   //
   // Database record found - output an edit page
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Member Restriction"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
        
      if (copy == false) {
          if ( id == 0 ) {
              out.println("Complete the following information for each Restriction to be added.<br>");
              out.println("Click on 'ADD' to add the Restriction.");
          } else {
              out.println("<b>Edit Member Restriction</b><br>");
              out.println("<br>Change the desired information for the restriction below.<br>");
              out.println("Click on <b>Update</b> to submit the changes.");
              out.println("<br>Click on <b>Remove</b> to delete the restriction.");
          }
      } else {
         out.println("<b>Copy Member Restriction</b><br>");
         out.println("<br>Change the desired information for the restriction below.<br>");
         out.println("Click on <b>Add</b> to create the new restriction.");
         out.println("<br><br><b>NOTE:</b> You must change the name of the restriction.");
      }
      out.println("</font></td></tr></table><br>");
      
      if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
          out.println("<font size=\"2\">");
          out.println("<table border=\"0\" align=\"center\"><tr><td>");
          out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_mrest\">");
          out.println("<input type=\"submit\" value=\"Cancel\"style=\"text-decoration:underline; background:#8B8970;\">");
          out.println("</td></tr></table>");
          out.println("</form>");
          out.println("</font>");
      }
      
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
         if (sheet.equals( "yes" )) {
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_editmrest\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"mrest_id\" value=\"" + id + "\">");
         } else {
            if (copy == true) {
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_editmrest\" method=\"post\" target=\"bot\">");
                out.println("<input type=\"hidden\" name=\"mrest_id\" value=\"0\">"); // will get processes as new
                out.println("<input type=\"hidden\" name=\"Update\" value=\"Update\">");
                //out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
            } else {
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_editmrest\" method=\"post\" target=\"bot\">");
                out.println("<input type=\"hidden\" name=\"mrest_id\" value=\"" + id + "\">");
            }
         }
      
            out.println("<tr><td>");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\">&nbsp;&nbsp;Restriction name:&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"text\" name=\"rest_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
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

               out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
            }

            out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"fb\">");
            Common_Config.buildOption("Both", "Both", fb, out);
            Common_Config.buildOption("Front", "Front", fb, out);
            Common_Config.buildOption("Back", "Back", fb, out);
            out.println("</select><br><br>");

        } else {
/*
            out.println("&nbsp;&nbsp;Select an Activity:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"activity_id\">");

            // display the currently selected activity
            if (default_activity_id != activity_id) {
                //Common_Config.buildOption(default_activity_id, getActivity.getActivityName(default_activity_id, con), activity_id, out);
            }

            // also display all the child activities of the currently selected activity
            ArrayList<Integer> result = new ArrayList<Integer>();
            try { result = getActivity.getAllChildrenForActivity(getActivity.getRootIdFromActivityId(sess_activity_id, con), con); }
            catch (Exception exc) { out.println("ERR=" + exc.toString()); }

            for (i = 0; i < result.size(); i++) {
               try { Common_Config.buildOption(result.get(i), getActivity.getActivityName(result.get(i), con), activity_id, out); }
               catch (Exception exc) {}
            }

            out.println("</select>");
            out.println("<br><br>");
*/

            out.println("&nbsp;&nbsp;Choose the locations for this Restriction:&nbsp;&nbsp;");
            Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, false, con, out);

            out.println("<br><br>");

        } // end if activity or not

       out.println("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;");
         out.println("Month:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"smonth\">");
           if (s_month == 1) {
              out.println("<option selected value=\"01\">JAN</option>");
           } else {
              out.println("<option value=\"01\">JAN</option>");
           }
           if (s_month == 2) {
              out.println("<option selected value=\"02\">FEB</option>");
           } else {
              out.println("<option value=\"02\">FEB</option>");
           }
           if (s_month == 3) {
              out.println("<option selected value=\"03\">MAR</option>");
           } else {
              out.println("<option value=\"03\">MAR</option>");
           }
           if (s_month == 4) {
              out.println("<option selected value=\"04\">APR</option>");
           } else {
              out.println("<option value=\"04\">APR</option>");
           }
           if (s_month == 5) {
              out.println("<option selected value=\"05\">MAY</option>");
           } else {
              out.println("<option value=\"05\">MAY</option>");
           }
           if (s_month == 6) {
              out.println("<option selected value=\"06\">JUN</option>");
           } else {
              out.println("<option value=\"06\">JUN</option>");
           }
           if (s_month == 7) {
              out.println("<option selected value=\"07\">JUL</option>");
           } else {
              out.println("<option value=\"07\">JUL</option>");
           }
           if (s_month == 8) {
              out.println("<option selected value=\"08\">AUG</option>");
           } else {
              out.println("<option value=\"08\">AUG</option>");
           }
           if (s_month == 9) {
              out.println("<option selected value=\"09\">SEP</option>");
           } else {
              out.println("<option value=\"09\">SEP</option>");
           }
           if (s_month == 10) {
              out.println("<option selected value=\"10\">OCT</option>");
           } else {
              out.println("<option value=\"10\">OCT</option>");
           }
           if (s_month == 11) {
              out.println("<option selected value=\"11\">NOV</option>");
           } else {
              out.println("<option value=\"11\">NOV</option>");
           }
           if (s_month == 12) {
              out.println("<option selected value=\"12\">DEC</option>");
           } else {
              out.println("<option value=\"12\">DEC</option>");
           }
         out.println("</select>");


        out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"sday\">");
        for (i = 1; i <= 31; i++) {

           Common_Config.buildOption(i, i, s_day, out);
        }

        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"syear\">");
        for (i = 2002; i <= 2014; i++) {

           Common_Config.buildOption(i, i, s_year, out);
        }

        out.println("</select><br><br>");

       out.println("&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("Month:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"emonth\">");
           if (e_month == 1) {
              out.println("<option selected value=\"01\">JAN</option>");
           } else {
              out.println("<option value=\"01\">JAN</option>");
           }
           if (e_month == 2) {
              out.println("<option selected value=\"02\">FEB</option>");
           } else {
              out.println("<option value=\"02\">FEB</option>");
           }
           if (e_month == 3) {
              out.println("<option selected value=\"03\">MAR</option>");
           } else {
              out.println("<option value=\"03\">MAR</option>");
           }
           if (e_month == 4) {
              out.println("<option selected value=\"04\">APR</option>");
           } else {
              out.println("<option value=\"04\">APR</option>");
           }
           if (e_month == 5) {
              out.println("<option selected value=\"05\">MAY</option>");
           } else {
              out.println("<option value=\"05\">MAY</option>");
           }
           if (e_month == 6) {
              out.println("<option selected value=\"06\">JUN</option>");
           } else {
              out.println("<option value=\"06\">JUN</option>");
           }
           if (e_month == 7) {
              out.println("<option selected value=\"07\">JUL</option>");
           } else {
              out.println("<option value=\"07\">JUL</option>");
           }
           if (e_month == 8) {
              out.println("<option selected value=\"08\">AUG</option>");
           } else {
              out.println("<option value=\"08\">AUG</option>");
           }
           if (e_month == 9) {
              out.println("<option selected value=\"09\">SEP</option>");
           } else {
              out.println("<option value=\"09\">SEP</option>");
           }
           if (e_month == 10) {
              out.println("<option selected value=\"10\">OCT</option>");
           } else {
              out.println("<option value=\"10\">OCT</option>");
           }
           if (e_month == 11) {
              out.println("<option selected value=\"11\">NOV</option>");
           } else {
              out.println("<option value=\"11\">NOV</option>");
           }
           if (e_month == 12) {
              out.println("<option selected value=\"12\">DEC</option>");
           } else {
              out.println("<option value=\"12\">DEC</option>");
           }
         out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"eday\">");
        for (i = 1; i <= 31; i++) {

           Common_Config.buildOption(i, i, e_day, out);
        }

        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"eyear\">");
        for (i = 2002; i <= 2014; i++) {

           Common_Config.buildOption(i, i, e_year, out);
        }

        out.println("</select><br><br>");
        out.println("&nbsp;&nbsp;Start Time:");
        out.println("&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"start_hr\">");
        for (i = 1; i <= 12; i++) {

           Common_Config.buildOption(i, i, shr, out);
        }

        out.println("</select>");
        
         out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
         out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + Utilities.ensureDoubleDigit(smin) + " name=\"start_min\">");
         out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"start_ampm\">");
           if (ssampm.equals( "AM" )) {
              out.println("<option selected value=\"00\">AM</option>");
           } else {
              out.println("<option value=\"00\">AM</option>");
           }
           if (ssampm.equals( "PM" )) {
              out.println("<option selected value=\"12\">PM</option>");
           } else {
              out.println("<option value=\"12\">PM</option>");
           }
         out.println("</select><br><br>");
       out.println("&nbsp;&nbsp;End Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"end_hr\">");
        for (i = 1; i <= 12; i++) {

           Common_Config.buildOption(i, i, ehr, out);
        }

         out.println("</select>");
         out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
         out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + Utilities.ensureDoubleDigit(emin) + " name=\"end_min\">");
         out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"end_ampm\">");
           if (seampm.equals( "AM" )) {
              out.println("<option selected value=\"00\">AM</option>");
           } else {
              out.println("<option value=\"00\">AM</option>");
           }
           if (seampm.equals( "PM" )) {
              out.println("<option selected value=\"12\">PM</option>");
           } else {
              out.println("<option value=\"12\">PM</option>");
           }
         out.println("</select><br><br>");
         out.println("&nbsp;&nbsp;Recurrence:&nbsp;&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"recurr\">");
        if (recur.equalsIgnoreCase( "every day" )) {
           out.println("<option selected value=\"Every Day\">Every Day</option>");
        } else {
           out.println("<option value=\"Every Day\">Every Day</option>");
        }
        if (recur.equalsIgnoreCase( "every sunday" )) {
           out.println("<option selected value=\"Every Sunday\">Every Sunday</option>");
        } else {
           out.println("<option value=\"Every Sunday\">Every Sunday</option>");
        }
        if (recur.equalsIgnoreCase( "every monday" )) {
           out.println("<option selected value=\"Every Monday\">Every Monday</option>");
        } else {
           out.println("<option value=\"Every Monday\">Every Monday</option>");
        }
        if (recur.equalsIgnoreCase( "every tuesday" )) {
           out.println("<option selected value=\"Every Tuesday\">Every Tuesday</option>");
        } else {
           out.println("<option value=\"Every Tuesday\">Every Tuesday</option>");
        }
        if (recur.equalsIgnoreCase( "every wednesday" )) {
           out.println("<option selected value=\"Every Wednesday\">Every Wednesday</option>");
        } else {
           out.println("<option value=\"Every Wednesday\">Every Wednesday</option>");
        }
        if (recur.equalsIgnoreCase( "every thursday" )) {
           out.println("<option selected value=\"Every Thursday\">Every Thursday</option>");
        } else {
           out.println("<option value=\"Every Thursday\">Every Thursday</option>");
        }
        if (recur.equalsIgnoreCase( "every friday" )) {
           out.println("<option selected value=\"Every Friday\">Every Friday</option>");
        } else {
           out.println("<option value=\"Every Friday\">Every Friday</option>");
        }
        if (recur.equalsIgnoreCase( "every saturday" )) {
           out.println("<option selected value=\"Every Saturday\">Every Saturday</option>");
        } else {
           out.println("<option value=\"Every Saturday\">Every Saturday</option>");
        }
        if (recur.equalsIgnoreCase( "all weekdays" )) {
           out.println("<option selected value=\"All Weekdays\">All Weekdays</option>");
        } else {
           out.println("<option value=\"All Weekdays\">All Weekdays</option>");
        }
        if (recur.equalsIgnoreCase( "all weekends" )) {
           out.println("<option selected value=\"All Weekends\">All Weekends</option>");
        } else {
           out.println("<option value=\"All Weekends\">All Weekends</option>");
        }
         out.println("</select></p>");
       out.println("<p align=\"center\">&nbsp;&nbsp;Groups to be Restricted<br>");
       out.println("<i>(specify Members to restrict or Membership Types to restrict, not both)</i></p>");

    out.println("<p align=\"left\">&nbsp;&nbsp;Members to be Restricted (select all that apply):<br>");

       //
       //  smemx = not null if this was specified in club db table (supported)
       //  memx = not null if this was specified in restriction
       //
       for (i=1; i<parm.MAX_Mems+1; i++) {

          if (!smem[i].equals( "" )) {          // if supported and

             out.println("<br>");
             out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

             if (!mem[i].equals( "" )) {        //    already checked

                out.println("<input type=\"checkbox\" checked name=\"mem" +i+ "\" value=\"" + smem[i] + "\">&nbsp;&nbsp;" + smem[i]);
             } else {
                out.println("<input type=\"checkbox\" name=\"mem" +i+ "\" value=\"" + smem[i] + "\">&nbsp;&nbsp;" + smem[i]);
             }
          }
       }

    out.println("</p><p align=\"center\"><b>-OR-</b><br></p>");

    out.println("<p align=\"left\">&nbsp;&nbsp;Membership Types to be Restricted (select all that apply):<br>");

       //
       //  smshipx = not null if this was specified in club db table (supported)
       //  mshipx = not null if this was specified in restriction
       //
       for (i=1; i<parm.MAX_Mships+1; i++) {

          if (!smship[i].equals( "" )) {          // if supported and

             out.println("<br>");
             out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

             
             out.print("<input type=\"checkbox\"");
             for (int i2 = 1; i2 < parm.MAX_Mships+1; i2++) {

                 if (mship[i2].equalsIgnoreCase( smship[i] )) {
                     out.print(" checked");
                     break;
                 }
                 
             } // end i2 loop
             out.println(" name=\"mship" +(i)+ "\" value=\"" + smship[i] + "\">&nbsp;&nbsp;" + smship[i]);
          
             
             /*
             if (!mship[i].equals( "" )) {        //    already checked

                out.println("<input type=\"checkbox\" checked name=\"mship" +i+ "\" value=\"" + smship[i] + "\">&nbsp;&nbsp;" + smship[i]);
             } else {
                out.println("<input type=\"checkbox\" name=\"mship" +i+ "\" value=\"" + smship[i] + "\">&nbsp;&nbsp;" + smship[i]);
             }*/
          }
       }

       // If not on the golf side, also ask if lessons shoudl be allowed to book over this restriction
       if (sess_activity_id != 0) {
           out.println("<br><br>&nbsp;&nbsp;Allow lessons to be booked over this restriction?:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"allow_lesson\">");
           Common_Config.buildOption(1, "Yes", allow_lesson, out);
           Common_Config.buildOption(0, "No", allow_lesson, out);
           out.println("</select>");
       } else {     // Golf Side
           out.println("<input type=\"hidden\" name=\"allow_lesson\" value=\"0\">");
       }

       out.println("<br><br>&nbsp;&nbsp;Show this restriction on Time Sheets?:&nbsp;&nbsp;");
       out.println("<select size=\"1\" name=\"show\">");
       Common_Config.buildOption("Yes", "Yes", show, out);
       Common_Config.buildOption("No", "No", show, out);
       out.println("</select><br><br>");

       out.println("&nbsp;&nbsp;If Yes, Color to make this restriction on the time sheet:&nbsp;&nbsp;");

       Common_Config.displayColorsAll(color, out);       // output the color options

       out.println("<br>");
       out.println("&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
       out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a></p>");

       out.println("<input type=\"hidden\" name=\"sheet\" value=\"" + sheet + "\">");
       //out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");

       out.println("<p align=\"center\">");

       if (copy == false) {
           
           out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + name + "\">");
           out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");

           if ( id == 0 ) {

               out.println("<input type=\"hidden\" name=\"Update\" value=\"Update\">");
               out.println("<input type=\"submit\" name=\"Add\" value=\"  Add  \">");
           
           } else {

               out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirm('Are you sure you want to delete this restriction and all corresponding suspensions?')\">");
               out.println("<br><center>(To update or remove restriction)</center>");
           
           }

       } else {

           out.println("<input type=\"hidden\" name=\"oldName\" value=\"\">"); // pass the oldName as empty so that it causes the dup name check
           out.println("<input type=\"submit\" name=\"Add\" value=\"Add\">");

       }

       // if here for an existing restriction then display the suspension iframe
       if (id > 0 && sess_activity_id == 0) {
           out.println("<br><br><hr width=\"90%\">");
           out.println("<iframe id=\"suspiframe\" src=\"/" + rev + "/servlet/Proshop_mrest?susp&mrestid=" + id + "&name=" + name + "\" style=\"width:800px\" scrolling=no frameborder=no></iframe>");
       }

       out.println("</p></font></td></tr></form></table>");
       out.println("</td></tr></table>");                       // end of main page table
        
   out.println("<font size=\"2\">");
   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_mrest\">");
      out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\" Close \" onClick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</center></font></body>");

   //
   //*******************************************************************
   //  Script for dynamic iframe resizing
   //*******************************************************************
   //
   out.println("<script type='text/javascript'>");            // Submit the form when clicking on a letter
   out.println("<!--");
   out.println("function resizeIFrame(divHeight) {");
   out.println("document.getElementById('suspiframe').style.height = divHeight;");
   out.println("}");
   out.println("// -->");
   out.println("</script>");          // End of script

   out.println("</html>");

   out.close();

 }   // end of doPost   


 // ***************************************************************************
 //  Process the copy request - display a selection list of existing rest's
 // ***************************************************************************

 private void doCopy(HttpServletRequest req, PrintWriter out, Connection con, int lottery, int activity_id) {

   Statement stmt = null;
   ResultSet rs = null;

   boolean b = false;

   //
   //  Build the HTML page to display the existing restrictions
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Restrictions Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Copy a Member Restriction</b><br>");
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to create a new restriction by copying an existing restriction.<br>");
   out.println("Select the restriction you wish to copy from the list below.");
   out.println("</font></td></tr></table>");
   out.println("<br><br>");

   //
   //  Get and display the existing restrictions 
   //
   try {

      stmt = con.createStatement();        // create a statement
      rs = stmt.executeQuery("SELECT name FROM restriction2");

      if (rs.next()) b = true;                     // indicate restrictions exist

      stmt.close();

      if (b == true) {
        
         out.println("<font size=\"2\">");
         out.println("<p>Select the restriction you wish to copy.</p>");

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_mrest\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");     // tell addmrest its a copy
         out.println("<select size=\"1\" name=\"id\">");


         String in = getActivity.buildInString(activity_id, 1, con);
         
         out.println("<!-- " + in + " -->");

         //
         //  Do again to actually get the names
         //
         stmt = con.createStatement();        // create a statement
         rs = stmt.executeQuery("SELECT id, name FROM restriction2 WHERE activity_id IN (" + in + ") ORDER BY name");

         while ( rs.next() ) {

            out.println("<option value=\"" + rs.getInt("id") + "\">" + rs.getString("name") + "</option>");

         }  // end of while loop

         out.println("</select><br><br>");

         out.println("<input type=\"submit\" name=\"Continue\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");

      } else {            // no rest's exist

         out.println("<p><font size=\"2\">No Member Restrictions Currently Exist</p>");
      }

   } catch (Exception exc) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                // end of main page table
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_mrest\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font><br></body></html>");
   out.close();
 }

 // *********************************************************
 // Print suspensions table and add/edit suspension form
 // *********************************************************
 private void printSuspensions(HttpServletRequest req, int sess_activity_id, Connection con, PrintWriter out) {
     
     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     Statement stmt = null;
     ResultSet rs = null;
     ResultSet rs2 = null;
     
     int mrest_id = 0;            // restriction id
     int id = 0;
     int id2 = 0;
     int shourInt = 0;
     int ehourInt = 0;
     int stime = 0;
     int etime = 0;
     int sdate = 0;
     int edate = 0;
     int multi = 0;
     
     String setBgColor = "";
     String name = "";
     String courseName = "";
     String sampm = "";
     String eampm = "";
     String shour = "";
     String smin = "";
     String ehour = "";
     String emin = "";
     String sday = "";
     String smonth = "";
     String syear = "";
     String eday = "";
     String emonth = "";
     String eyear = "";
     
     boolean suspExists = false;
     
     if (req.getParameter("updateSusp") != null) {
         updateSuspension(req, con, out);
         return;
     }
     
     if (req.getParameter("deleteSusp") != null) {
         deleteSuspension(req, con, out);
         return;
     }
     
     mrest_id = Integer.parseInt(req.getParameter("mrestid"));
     name = req.getParameter("name");        
     
     out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('iframediv').offsetHeight);\" bgcolor=\"#F5F5DC\" text=\"#000000\">"); //
     out.println("<div id=\"iframediv\">");
     out.println("<table border=\"0\" align=\"center\"><tr><td>");
     out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");
     out.println("<h4 align=\"center\">Active Suspensions</h4>");
     out.println("<table border=\"1\" bordercolor=\"FFFFFF\" cellpadding=\"5\" align=\"center\" style=\"font-size: 10pt; font-family: Arial;\">");

     // Print header row
     out.println("<tr style=\"background-color: #336633; color: white;\">");
     out.println("<td align=\"center\"><b>ID#</b></td>");
     out.println("<td align=\"center\"><b>Course</b></td>");
     out.println("<td align=\"center\"><b>Start Date</b></td>");
     out.println("<td align=\"center\"><b>End Date</b></td>");
     out.println("<td align=\"center\"><b>Start Time</b></td>");
     out.println("<td align=\"center\"><b>End Time</b></td>");
     out.println("<td align=\"center\"><b>Recurrence</b></td>");
     out.println("<td>&nbsp</td>");
     out.println("</tr>");

     // get all suspensions from database
     try {

         pstmt = con.prepareStatement("SELECT * FROM rest_suspend WHERE mrest_id = ? ORDER BY id");

         pstmt.clearParameters();
         pstmt.setInt(1, mrest_id);

         rs = pstmt.executeQuery();

         String tempTime = "";
         String tempDate = "";
         
         while (rs.next()) {

             suspExists = true;
             
             String recurr = getRecurrence(rs, out);

             id = rs.getInt("id");
             courseName = rs.getString("courseName");
             
             
             //
             //  some values must be converted for display
             //
             stime = rs.getInt("stime");
             
             if (stime != 0) {
                 tempTime = String.valueOf(stime);
                 
                 if (tempTime.length() > 2) {
                     shourInt = Integer.parseInt(tempTime.substring(0, tempTime.length() - 2));
                     smin = tempTime.substring(tempTime.length() - 2);
                     
                     sampm = " AM";         // default to AM
                     if (shourInt >= 12) {
                         sampm = " PM";

                         if (shourInt != 12) {
                             shourInt = shourInt - 12;                // convert to 12 hr clock value
                         }
                     }
                 } else {
                     shourInt = 12;
                     smin = tempTime;
                     if (smin.length() == 1) {
                         smin = "0" + smin;
                     }
                     sampm = " AM";
                 }


             } else {
                 sampm = " AM";
                 shourInt = 12;
                 smin = "00";
             }
             
             etime = rs.getInt("etime");
             
             if (etime != 0) {
                 tempTime = String.valueOf(etime);
                 
                 if (tempTime.length() > 2) {
                     ehourInt = Integer.parseInt(tempTime.substring(0, tempTime.length() - 2));
                     emin = tempTime.substring(tempTime.length() - 2);
                     
                     eampm = " AM";         // default to AM
                     if (ehourInt >= 12) {
                         eampm = " PM";

                         if (ehourInt != 12) {
                             ehourInt = ehourInt - 12;                // convert to 12 hr clock value
                         }
                     }
                 } else {
                     ehourInt = 12;
                     emin = tempTime;
                     if (emin.length() == 1) {
                         emin = "0" + emin;
                     }
                     eampm = " AM";
                 }

             } else {
                 eampm = " AM";
                 ehourInt = 12;
                 emin = "00";
             }
                 
             shour = String.valueOf(shourInt);
             ehour = String.valueOf(ehourInt);


             sdate = rs.getInt("sdate");
             tempDate = String.valueOf(sdate);
             syear = tempDate.substring(0, tempDate.length() - 4);
             smonth = tempDate.substring(4, tempDate.length() - 2);
             sday = tempDate.substring(6);

             edate = rs.getInt("edate");
             tempDate = String.valueOf(edate);
             eyear = tempDate.substring(0, tempDate.length() - 4);
             emonth = tempDate.substring(4, tempDate.length() - 2);
             eday = tempDate.substring(6);

             if (smonth.startsWith("0")) { smonth = smonth.substring(1); }
             if (emonth.startsWith("0")) { emonth = emonth.substring(1); }

             //
             //  Highlight the currently selected row, if any
             //
             setBgColor = "";
             if (req.getParameter("id") != null) {
                 
                 id2 = Integer.parseInt(req.getParameter("id"));
                 
                 if (id2 >= 0 && id2 == id) {
                     setBgColor = " bgcolor=\"#CCCCAA\"";
                 }
             }
             
             // print a line for each suspension
             out.println("<tr" + setBgColor + ">");
             out.println("<td>" + id + "</td>");
             out.println("<td>" + courseName + "</td>");
             out.println("<td>" + smonth + "/" + sday + "/" + syear + "</td>");
             out.println("<td>" + emonth + "/" + eday + "/" + eyear + "</td>");
             out.println("<td>" + shour + ":" + smin + " " + sampm + "</td>");
             out.println("<td>" + ehour + ":" + emin + " " + eampm + "</td>");
             out.println("<td>" + recurr + "</td>");
             out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_mrest?susp\">");
             out.println("<input type=\"hidden\" name=\"mrestid\" value=\"" + mrest_id + "\">");
             out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
             out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
             out.println("<td>");
             out.println("<input type=\"submit\" name=\"selectSusp\" value=\"Select\">");
             out.println("<input type=\"submit\" name=\"deleteSusp\" value=\"Delete\" onclick=\"return confirm('Are you sure you want to delete this suspension?')\">");
             out.println("</td>");
             out.println("</form>");
             out.println("</tr>");
         }  // end of while

         pstmt.close();
         
         if (!suspExists) {
             out.println("<tr><td colspan=\"8\" align=\"center\"><b>No active suspensions for this restriction</b></td></tr>");
         }

         out.println("</table>");
         
         
         
         //
         // Print form to add/edit suspensions
         //
         out.println("<br><br>");
         //  parm block to hold the club parameters
         parmClub parm = new parmClub(sess_activity_id, con);          // allocate a parm block
         
         int s_date = 0;
         int s_year = 0;
         int s_month = 0;
         int e_date = 0;
         int s_day = 0;
         int e_year = 0;
         int e_month = 0;
         int e_day = 0;
         int s_time = 0;
         int s_hr = 0;
         int s_min = 0;
         int e_time = 0;
         int e_hr = 0;
         int e_min = 0;
         int index = 0;
         int yearSelectIndex = 0;
         
         String s_ampm = "AM";
         String e_ampm = "AM";
         String idStr = "";
         String tempTime2 = "";
         String tempDate2 = "";
         String oldCourse = "";
         String restCourse = "";
         String courseName2 = "";
         
         boolean madeASelection = false;
         
         Calendar cal = new GregorianCalendar();         // get todays date
         int year = cal.get(Calendar.YEAR);
         
         courseName = "";
         
         String [] selectArr = new String[31];          // Array to hold selection values for form selection boxes
         int [] recurrArr = new int [8];        // index 0 = eo_week value, 1-7 = Sun-Sat values
         
         ArrayList<String> course = new ArrayList<String>();
                  
         id = -1;
         
         if (req.getParameter("id") != null && req.getParameter("returning") == null) {
             pstmt = con.prepareStatement (
                   "SELECT * FROM rest_suspend WHERE id = ?");

             id = Integer.parseInt(req.getParameter("id"));
             
             pstmt.clearParameters();        // clear the parms
             pstmt.setInt(1, id);       // put the parm in stmt
             rs = pstmt.executeQuery();      // execute the prepared stmt

             if (rs.next()) {

                 //  Found the restriction record - get it
                 courseName = rs.getString("courseName");
                 s_date = rs.getInt("sdate");
                 s_time = rs.getInt("stime");
                 e_date = rs.getInt("edate");
                 e_time = rs.getInt("etime");
                 
                 recurrArr[0] = rs.getInt("eo_week");
                 recurrArr[1] = rs.getInt("sunday");
                 recurrArr[2] = rs.getInt("monday");
                 recurrArr[3] = rs.getInt("tuesday");
                 recurrArr[4] = rs.getInt("wednesday");
                 recurrArr[5] = rs.getInt("thursday");
                 recurrArr[6] = rs.getInt("friday");
                 recurrArr[7] = rs.getInt("saturday");
                 
             }
             
             pstmt.close();
         } else {       // see if any params were passed with the request
             
             if (req.getParameter("id") != null) {
                 id = Integer.parseInt(req.getParameter("id"));
             }
             if (req.getParameter("course") != null) {
                 courseName = req.getParameter("course");
             }
             if (req.getParameter("sdate") != null) {
                 s_date = Integer.parseInt(req.getParameter("sdate"));
             }
             if (req.getParameter("stime") != null) {
                 s_time = Integer.parseInt(req.getParameter("stime"));
             }
             if (req.getParameter("edate") != null) {
                 e_date = Integer.parseInt(req.getParameter("edate"));
             }
             if (req.getParameter("etime") != null) {
                 e_time = Integer.parseInt(req.getParameter("etime"));
             }
              
             if (req.getParameter("eo_week") != null) {
                 recurrArr[0] = Integer.parseInt(req.getParameter("eo_week"));
             }
             if (req.getParameter("sunday") != null) {
                 recurrArr[1] = Integer.parseInt(req.getParameter("sunday"));
             }
             if (req.getParameter("monday") != null) {
                 recurrArr[2] = Integer.parseInt(req.getParameter("monday"));
             }
             if (req.getParameter("tuesday") != null) {
                 recurrArr[3] = Integer.parseInt(req.getParameter("tuesday"));
             }
             if (req.getParameter("wednesday") != null) {
                 recurrArr[4] = Integer.parseInt(req.getParameter("wednesday"));
             }
             if (req.getParameter("thursday") != null) {
                 recurrArr[5] = Integer.parseInt(req.getParameter("thursday"));
             }
             if (req.getParameter("friday") != null) {
                 recurrArr[6] = Integer.parseInt(req.getParameter("friday"));
             }
             if (req.getParameter("saturday") != null) {
                 recurrArr[7] = Integer.parseInt(req.getParameter("saturday"));
             }
         }
                 
         // Date/Time conversions
         
         if (s_time != 0) {
             tempTime2 = String.valueOf(s_time);
             
             if (tempTime2.length() > 2) {
                 s_hr = Integer.parseInt(tempTime2.substring(0, tempTime2.length() - 2));
                 s_min = Integer.parseInt(tempTime2.substring(tempTime2.length() - 2));
                 
                 s_ampm = "AM";         // default to AM
                 if (s_hr >= 12) {
                     s_ampm = "PM";

                     if (s_hr != 12) {
                         s_hr = s_hr - 12;                // convert to 12 hr clock value
                     }
                 }
             } else {
                 s_hr = 12;
                 s_min = Integer.parseInt(tempTime2);
                 s_ampm = "PM";
             }

         } else {
             s_ampm = "AM";
             s_hr = 12;
             s_min = 0;
         }
         
         if (e_time != 0) {
             tempTime2 = String.valueOf(e_time);
             
             if (tempTime2.length() > 2) {
                 e_hr = Integer.parseInt(tempTime2.substring(0, tempTime2.length() - 2));
                 e_min = Integer.parseInt(tempTime2.substring(tempTime2.length() - 2));
                 
                 e_ampm = "AM";         // default to AM
                 if (e_hr >= 12) {
                     e_ampm = "PM";

                     if (e_hr != 12) {
                         e_hr = e_hr - 12;                // convert to 12 hr clock value
                     }
                 } 
             } else {
                 e_hr = 12;
                 e_min = Integer.parseInt(tempTime2);
                 e_ampm = "AM";
             }

         } else {
             e_ampm = "AM";
             e_hr = 12;
             e_min = 0;
         }

         if (s_date != 0) {
             tempDate2 = String.valueOf(s_date);
             s_year = Integer.parseInt(tempDate2.substring(0, tempDate2.length() - 4));
             s_month = Integer.parseInt(tempDate2.substring(4, tempDate2.length() - 2));
             s_day = Integer.parseInt(tempDate2.substring(6));
         }

         if (e_date != 0) {
             tempDate2 = String.valueOf(e_date);
             e_year = Integer.parseInt(tempDate2.substring(0, tempDate2.length() - 4));
             e_month = Integer.parseInt(tempDate2.substring(4, tempDate2.length() - 2));
             e_day = Integer.parseInt(tempDate2.substring(6));
         }
         
         
         // Determine whether club has multiple courses
         pstmt = con.prepareStatement("SELECT multi FROM club5");
         
         rs = pstmt.executeQuery();
         
         if (id >= 0) {
             idStr = "&id=" + String.valueOf(id);
         }
         
         if (id < 0) {
             out.println("&nbsp;&nbsp;<b>Note:</b>  To <b>add</b> a suspension, complete the information below and select '<b>Submit</b>'.<br><br>");
         } else {
             out.println("&nbsp;&nbsp;<b>Note:</b>  To <b>edit</b> the selected suspension, make the desired changes below and select '<b>Submit</b>'.<br><br>");
         }
         
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_mrest\">");
         out.println("<input type=\"hidden\" name=\"susp\" value=\"true\">");
         out.println("<input type=\"hidden\" name=\"updateSusp\" value=\"true\">");
         if (id >= 0) {
             out.println("<input type=\"hidden\" name=\"id\" value=\"" + String.valueOf(id) + "\">");
         }
         
         //
         //  Print the ID# if a restriction has been selected
         //
         if (id >= 0) {
             out.println("&nbsp;&nbsp;Selected ID#:&nbsp;&nbsp;&nbsp;&nbsp;" + String.valueOf(id) + "<br><br>");
         }
         
         if (req.getParameter("mrestid") != null) {
             mrest_id = Integer.parseInt(req.getParameter("mrestid"));
         } else {
             mrest_id = -1;
         }
         
         out.println("<input type=\"hidden\" name=\"mrestid\" value=\"" + mrest_id + "\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         
         if (rs.next()) {
             multi = rs.getInt("multi");
         }
         
         if (multi != 0) {
             
             // Get the course name for the restriction to be suspended
             pstmt2 = con.prepareStatement("SELECT courseName FROM restriction2 WHERE id = ?");
             pstmt2.clearParameters();
             pstmt2.setInt(1, mrest_id);
             rs2 = pstmt2.executeQuery();

             if (rs2.next()) {
                 restCourse = rs2.getString("courseName");
             }

             pstmt2.close();
         }
         
         if (multi != 0 && restCourse.equalsIgnoreCase("-ALL-")) {

             //
             //  Multiple courses - get the names
             //
            course = Utilities.getCourseNames(con);     // get all the course names
            
            out.println("&nbsp;&nbsp;Select a Course:&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");

            if (courseName.equals( "-ALL-" ) || courseName.equals("")) {             // if same as existing name

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

            if (multi == 0) {
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
            } else {
                out.println("&nbsp;&nbsp;Course:&nbsp;&nbsp;&nbsp;&nbsp;" + restCourse);
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + restCourse + "\">");
                out.println("<br><br>");
            }
         }
         
         out.println("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;");
         
         // Print start month selection box
         out.println("Month:&nbsp;&nbsp;");
         
         for (int i=0; i<12; i++) {
             if (s_month == i + 1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("<select size=\"1\" name=\"smonth\">");
              out.println("<option" + selectArr[0] + " value=\"01\">JAN</option>");
              out.println("<option" + selectArr[1] + " value=\"02\">FEB</option>");
              out.println("<option" + selectArr[2] + " value=\"03\">MAR</option>");
              out.println("<option" + selectArr[3] + " value=\"04\">APR</option>");
              out.println("<option" + selectArr[4] + " value=\"05\">MAY</option>");
              out.println("<option" + selectArr[5] + " value=\"06\">JUN</option>");
              out.println("<option" + selectArr[6] + " value=\"07\">JUL</option>");
              out.println("<option" + selectArr[7] + " value=\"08\">AUG</option>");
              out.println("<option" + selectArr[8] + " value=\"09\">SEP</option>");
              out.println("<option" + selectArr[9] + " value=\"10\">OCT</option>");
              out.println("<option" + selectArr[10] + " value=\"11\">NOV</option>");
              out.println("<option" + selectArr[11] + " value=\"12\">DEC</option>");
         out.println("</select>");

         // Print start day selection box
         out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
         
         for (int i=0; i<31; i++) {
             if (s_day == i + 1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("<select size=\"1\" name=\"sday\">");
              out.println("<option" + selectArr[0] + " value=\"01\">1</option>");
              out.println("<option" + selectArr[1] + " value=\"02\">2</option>");
              out.println("<option" + selectArr[2] + " value=\"03\">3</option>");
              out.println("<option" + selectArr[3] + " value=\"04\">4</option>");
              out.println("<option" + selectArr[4] + " value=\"05\">5</option>");
              out.println("<option" + selectArr[5] + " value=\"06\">6</option>");
              out.println("<option" + selectArr[6] + " value=\"07\">7</option>");
              out.println("<option" + selectArr[7] + " value=\"08\">8</option>");
              out.println("<option" + selectArr[8] + " value=\"09\">9</option>");
              out.println("<option" + selectArr[9] + " value=\"10\">10</option>");
              out.println("<option" + selectArr[10] + " value=\"11\">11</option>");
              out.println("<option" + selectArr[11] + " value=\"12\">12</option>");
              out.println("<option" + selectArr[12] + " value=\"13\">13</option>");
              out.println("<option" + selectArr[13] + " value=\"14\">14</option>");
              out.println("<option" + selectArr[14] + " value=\"15\">15</option>");
              out.println("<option" + selectArr[15] + " value=\"16\">16</option>");
              out.println("<option" + selectArr[16] + " value=\"17\">17</option>");
              out.println("<option" + selectArr[17] + " value=\"18\">18</option>");
              out.println("<option" + selectArr[18] + " value=\"19\">19</option>");
              out.println("<option" + selectArr[19] + " value=\"20\">20</option>");
              out.println("<option" + selectArr[20] + " value=\"21\">21</option>");
              out.println("<option" + selectArr[21] + " value=\"22\">22</option>");
              out.println("<option" + selectArr[22] + " value=\"23\">23</option>");
              out.println("<option" + selectArr[23] + " value=\"24\">24</option>");
              out.println("<option" + selectArr[24] + " value=\"25\">25</option>");
              out.println("<option" + selectArr[25] + " value=\"26\">26</option>");
              out.println("<option" + selectArr[26] + " value=\"27\">27</option>");
              out.println("<option" + selectArr[27] + " value=\"28\">28</option>");
              out.println("<option" + selectArr[28] + " value=\"29\">29</option>");
              out.println("<option" + selectArr[29] + " value=\"30\">30</option>");
              out.println("<option" + selectArr[30] + " value=\"31\">31</option>");
         out.println("</select>");

         // Print start year selection box
         out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
         
         for (int i=0; i<11; i++) {
             
             if (year == 2008 + i) {
                 yearSelectIndex = i;
             }
             
             if (s_year == 2008 + i) {
                 selectArr[i] = " selected";
                 madeASelection = true;
             } else {
                 selectArr[i] = "";
             }
         }
         if (!madeASelection) {
             selectArr[yearSelectIndex] = " selected";
         }
         
         out.println("<select size=\"1\" name=\"syear\">");
            out.println("<option" + selectArr[0] + " value=\"2008\">2008</option>");
            out.println("<option" + selectArr[1] + " value=\"2009\">2009</option>");
            out.println("<option" + selectArr[2] + " value=\"2010\">2010</option>");
            out.println("<option" + selectArr[3] + " value=\"2011\">2011</option>");
            out.println("<option" + selectArr[4] + " value=\"2012\">2012</option>");
            out.println("<option" + selectArr[5] + " value=\"2013\">2013</option>");
            out.println("<option" + selectArr[6] + " value=\"2014\">2014</option>");
            out.println("<option" + selectArr[7] + " value=\"2015\">2015</option>");
            out.println("<option" + selectArr[8] + " value=\"2016\">2016</option>");
            out.println("<option" + selectArr[9] + " value=\"2017\">2017</option>");
            out.println("<option" + selectArr[10] + " value=\"2018\">2018</option>");
         out.println("</select><br><br>");
         
         
         out.println("&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
         
         // Print end month selection box
         out.println("Month:&nbsp;&nbsp;");
         
         for (int i=0; i<12; i++) {
             if (e_month == i + 1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("<select size=\"1\" name=\"emonth\">");
              out.println("<option" + selectArr[0] + " value=\"01\">JAN</option>");
              out.println("<option" + selectArr[1] + " value=\"02\">FEB</option>");
              out.println("<option" + selectArr[2] + " value=\"03\">MAR</option>");
              out.println("<option" + selectArr[3] + " value=\"04\">APR</option>");
              out.println("<option" + selectArr[4] + " value=\"05\">MAY</option>");
              out.println("<option" + selectArr[5] + " value=\"06\">JUN</option>");
              out.println("<option" + selectArr[6] + " value=\"07\">JUL</option>");
              out.println("<option" + selectArr[7] + " value=\"08\">AUG</option>");
              out.println("<option" + selectArr[8] + " value=\"09\">SEP</option>");
              out.println("<option" + selectArr[9] + " value=\"10\">OCT</option>");
              out.println("<option" + selectArr[10] + " value=\"11\">NOV</option>");
              out.println("<option" + selectArr[11] + " value=\"12\">DEC</option>");
         out.println("</select>");
         
         // Print end day selection box
         out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
         
         for (int i=0; i<31; i++) {
             if (e_day == i + 1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("<select size=\"1\" name=\"eday\">");
              out.println("<option" + selectArr[0] + " value=\"01\">1</option>");
              out.println("<option" + selectArr[1] + " value=\"02\">2</option>");
              out.println("<option" + selectArr[2] + " value=\"03\">3</option>");
              out.println("<option" + selectArr[3] + " value=\"04\">4</option>");
              out.println("<option" + selectArr[4] + " value=\"05\">5</option>");
              out.println("<option" + selectArr[5] + " value=\"06\">6</option>");
              out.println("<option" + selectArr[6] + " value=\"07\">7</option>");
              out.println("<option" + selectArr[7] + " value=\"08\">8</option>");
              out.println("<option" + selectArr[8] + " value=\"09\">9</option>");
              out.println("<option" + selectArr[9] + " value=\"10\">10</option>");
              out.println("<option" + selectArr[10] + " value=\"11\">11</option>");
              out.println("<option" + selectArr[11] + " value=\"12\">12</option>");
              out.println("<option" + selectArr[12] + " value=\"13\">13</option>");
              out.println("<option" + selectArr[13] + " value=\"14\">14</option>");
              out.println("<option" + selectArr[14] + " value=\"15\">15</option>");
              out.println("<option" + selectArr[15] + " value=\"16\">16</option>");
              out.println("<option" + selectArr[16] + " value=\"17\">17</option>");
              out.println("<option" + selectArr[17] + " value=\"18\">18</option>");
              out.println("<option" + selectArr[18] + " value=\"19\">19</option>");
              out.println("<option" + selectArr[19] + " value=\"20\">20</option>");
              out.println("<option" + selectArr[20] + " value=\"21\">21</option>");
              out.println("<option" + selectArr[21] + " value=\"22\">22</option>");
              out.println("<option" + selectArr[22] + " value=\"23\">23</option>");
              out.println("<option" + selectArr[23] + " value=\"24\">24</option>");
              out.println("<option" + selectArr[24] + " value=\"25\">25</option>");
              out.println("<option" + selectArr[25] + " value=\"26\">26</option>");
              out.println("<option" + selectArr[26] + " value=\"27\">27</option>");
              out.println("<option" + selectArr[27] + " value=\"28\">28</option>");
              out.println("<option" + selectArr[28] + " value=\"29\">29</option>");
              out.println("<option" + selectArr[29] + " value=\"30\">30</option>");
              out.println("<option" + selectArr[30] + " value=\"31\">31</option>");
         out.println("</select>");
         
         // Print end year selection box
         out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
         
         for (int i=0; i<11; i++) {
             
             if (year == 2008 + i) {
                 yearSelectIndex = i;
             }
             
             if (e_year == 2008 + i) {
                 selectArr[i] = " selected";
                 madeASelection = true;
             } else {
                 selectArr[i] = "";
             }
         }
         if (!madeASelection) {
             selectArr[yearSelectIndex] = " selected";
         }
         
         out.println("<select size=\"1\" name=\"eyear\">");
            out.println("<option" + selectArr[0] + " value=\"2008\">2008</option>");
            out.println("<option" + selectArr[1] + " value=\"2009\">2009</option>");
            out.println("<option" + selectArr[2] + " value=\"2010\">2010</option>");
            out.println("<option" + selectArr[3] + " value=\"2011\">2011</option>");
            out.println("<option" + selectArr[4] + " value=\"2012\">2012</option>");
            out.println("<option" + selectArr[5] + " value=\"2013\">2013</option>");
            out.println("<option" + selectArr[6] + " value=\"2014\">2014</option>");
            out.println("<option" + selectArr[7] + " value=\"2015\">2015</option>");
            out.println("<option" + selectArr[8] + " value=\"2016\">2016</option>");
            out.println("<option" + selectArr[9] + " value=\"2017\">2017</option>");
            out.println("<option" + selectArr[10] + " value=\"2018\">2018</option>");
         out.println("</select><br><br>");
         
         
         out.println("&nbsp;&nbsp;Start Time:");
         
         // Print start hour selection box
         out.println("&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
         for (int i=0; i<12; i++) {
             if (s_hr == i + 1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("<select size=\"1\" name=\"start_hr\">");
             out.println("<option" + selectArr[0] + " value=\"01\">1</option>");
             out.println("<option" + selectArr[1] + " value=\"02\">2</option>");
             out.println("<option" + selectArr[2] + " value=\"03\">3</option>");
             out.println("<option" + selectArr[3] + " value=\"04\">4</option>");
             out.println("<option" + selectArr[4] + " value=\"05\">5</option>");
             out.println("<option" + selectArr[5] + " value=\"06\">6</option>");
             out.println("<option" + selectArr[6] + " value=\"07\">7</option>");
             out.println("<option" + selectArr[7] + " value=\"08\">8</option>");
             out.println("<option" + selectArr[8] + " value=\"09\">9</option>");
             out.println("<option" + selectArr[9] + " value=\"10\">10</option>");
             out.println("<option" + selectArr[10] + " value=\"11\">11</option>");
             out.println("<option" + selectArr[11] + " value=\"12\">12</option>");
         out.println("</select>");
         
         out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
            if (s_min < 10) {
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + s_min + " name=\"start_min\">");
            } else {
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + s_min + " name=\"start_min\">");
            }
            out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
            
         out.println("<select size=\"1\" name=\"start_ampm\">");
           if (s_ampm.equals( "AM" )) {
              out.println("<option selected value=\"am\">AM</option>");
           } else {
              out.println("<option value=\"am\">AM</option>");
           }
           if (s_ampm.equals( "PM" )) {
              out.println("<option selected value=\"pm\">PM</option>");
           } else {
              out.println("<option value=\"pm\">PM</option>");
           }
         out.println("</select><br><br>");
         
         
         out.println("&nbsp;&nbsp;End Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
         
         // Print end hour selection box
         for (int i=0; i<12; i++) {
             if (e_hr == i + 1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("<select size=\"1\" name=\"end_hr\">");
             out.println("<option" + selectArr[0] + " value=\"01\">1</option>");
             out.println("<option" + selectArr[1] + " value=\"02\">2</option>");
             out.println("<option" + selectArr[2] + " value=\"03\">3</option>");
             out.println("<option" + selectArr[3] + " value=\"04\">4</option>");
             out.println("<option" + selectArr[4] + " value=\"05\">5</option>");
             out.println("<option" + selectArr[5] + " value=\"06\">6</option>");
             out.println("<option" + selectArr[6] + " value=\"07\">7</option>");
             out.println("<option" + selectArr[7] + " value=\"08\">8</option>");
             out.println("<option" + selectArr[8] + " value=\"09\">9</option>");
             out.println("<option" + selectArr[9] + " value=\"10\">10</option>");
             out.println("<option" + selectArr[10] + " value=\"11\">11</option>");
             out.println("<option" + selectArr[11] + " value=\"12\">12</option>");
         out.println("</select>");
         
         out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
            if (e_min < 10) {
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + e_min + " name=\"end_min\">");
            } else {
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + e_min + " name=\"end_min\">");
            }
            out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
            
         out.println("<select size=\"1\" name=\"end_ampm\">");
           if (e_ampm.equals( "AM" )) {
              out.println("<option selected value=\"am\">AM</option>");
           } else {
              out.println("<option value=\"am\">AM</option>");
           }
           if (e_ampm.equals( "PM" )) {
              out.println("<option selected value=\"pm\">PM</option>");
           } else {
              out.println("<option value=\"pm\">PM</option>");
           }
         out.println("</select><br><br>");
         
         
         out.println("<table border=\"0\" cellpadding=\"2\" style=\"font-size: 10pt; font-weight: normal;\">");
         out.println("<tr>");
         out.println("<td valign=\"top\">&nbsp;Recurrence: </td>");
         out.println("<td valign=\"top\">");
         
         String checkedE = "";
         String checkedEO = "";
         
         if (recurrArr[0] != 1) {
             checkedE = "checked";
         } else {
             checkedEO = "checked";
         }
         
         
         out.println("<input type=\"radio\" name=\"eo_week\" value=\"every\" " + checkedE + ">Every<br>");
         out.println("<input type=\"radio\" name=\"eo_week\" value=\"everyother\" " + checkedEO + ">Every other");
         out.println("</td><td>");
         
         String [] checked = new String[7];
         for (int i=0; i<7; i++) {
             if (recurrArr[i+1] == 1) {
                 checked[i] = "checked";
             } else {
                 checked[i] = "";
             }
         }
         out.println("<input type=\"checkbox\" name=\"day1\" value=\"yes\" " + checked[0] + ">Sunday<br>");
         out.println("<input type=\"checkbox\" name=\"day2\" value=\"yes\" " + checked[1] + ">Monday<br>");
         out.println("<input type=\"checkbox\" name=\"day3\" value=\"yes\" " + checked[2] + ">Tuesday<br>");
         out.println("<input type=\"checkbox\" name=\"day4\" value=\"yes\" " + checked[3] + ">Wednesday<br>");
         out.println("<input type=\"checkbox\" name=\"day5\" value=\"yes\" " + checked[4] + ">Thursday<br>");
         out.println("<input type=\"checkbox\" name=\"day6\" value=\"yes\" " + checked[5] + ">Friday<br>");
         out.println("<input type=\"checkbox\" name=\"day7\" value=\"yes\" " + checked[6] + ">Saturday<br>");
         
         out.println("</td></tr></table>");
         
         out.println("<table border=\"0\" cellpadding=\"5\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<br><input type=\"submit\" name=\"updateSubmit\" value=\"Submit\" align=\"center\">");
         out.println("&nbsp;&nbsp;&nbsp;<a href=\"/" + rev + "/servlet/Proshop_mrest?susp&mrestid=" + mrest_id + "\" style=\"text-decoration: none;\"><button type=\"button\">Reset</button></a>");
         out.println("</td></tr><tr><td align=\"center\" style=\"font-size: 10pt; font-weight: normal;\">");
         if (id < 0 ) {
             out.println("<center>(To Add Suspension or Clear Values)</center>");
         } else {
             out.println("<center>(To Edit Suspension or Clear Values)</center>");
         }
         out.println("</td></tr>");
         out.println("</table>");
         out.println("</form>");
         out.println("</td></tr></table>");
         out.println("</div>");
         out.println("<br></body></html>");

     } catch (Exception exc) {

         dbError(out, exc);

     } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        try { rs2.close(); }
        catch (Exception ignore) {}

        try { pstmt2.close(); }
        catch (Exception ignore) {}

   }
 }      // end of printSuspensions
 
 // *********************************************************
 // Add or make changes to a suspension in the database
 // *********************************************************
 private void updateSuspension(HttpServletRequest req, Connection con, PrintWriter out) {
     
     PreparedStatement pstmt = null;
     
     boolean daysSelected = false;
     boolean invalidMins = false;
     boolean invalidTime = false;
     boolean invalidDate = false;
     
     int mrest_id = 0;            // restriction id
     int id = 0;
     int stime = 0;
     int etime = 0;
     int sdate = 0;
     int edate = 0;
     int count = 0;
     
     String name = "";
     String courseName = "";
     String sampm = "";
     String eampm = "";
     String shour = "";
     String smin = "";
     String ehour = "";
     String emin = "";
     String sday = "";
     String smonth = "";
     String syear = "";
     String eday = "";
     String emonth = "";
     String eyear = "";
     
     int [] recurrArr = new int [8];        // index 0 = eo_week value, 1-7 = Sun-Sat values
     
     // If id != null, we know we're updating an existing suspension
     if (req.getParameter("id") != null) {
         id = Integer.parseInt(req.getParameter("id"));
     } else {
         id = -1;
     }
     
     mrest_id = Integer.parseInt(req.getParameter("mrestid"));
     
     name = req.getParameter("name");
     courseName = req.getParameter("course");
     smonth = req.getParameter("smonth");
     sday = req.getParameter("sday");
     syear = req.getParameter("syear");
     emonth = req.getParameter("emonth");
     eday = req.getParameter("eday");
     eyear = req.getParameter("eyear");
     shour = req.getParameter("start_hr");
     smin = req.getParameter("start_min");
     sampm = req.getParameter("start_ampm");
     ehour = req.getParameter("end_hr");
     emin = req.getParameter("end_min");
     eampm = req.getParameter("end_ampm");
     
     if (req.getParameter("eo_week") != null && req.getParameter("eo_week").equalsIgnoreCase("everyother")) {
         recurrArr[0] = 1;
     } else {
         recurrArr[0] = 0;
     }
     
     for (int i=1; i<8; i++) {
         if (req.getParameter("day" + String.valueOf(i)) != null) {
             recurrArr[i] = 1;
             daysSelected = true;        // so we know if any days were selected
         } else {
             recurrArr[i] = 0;
         }
     }
     
     // Construct the correctly formated dates and times for storing in the database
     if (sampm.equalsIgnoreCase("pm") && !shour.equals("12")) {
         shour = String.valueOf(Integer.parseInt(shour) + 12);
     } else if (sampm.equalsIgnoreCase("am") && shour.equals("12")) {
         shour = "0";
     }
     if (eampm.equalsIgnoreCase("pm") && !ehour.equals("12")) {
         ehour = String.valueOf(Integer.parseInt(ehour) + 12);
     } else if (eampm.equalsIgnoreCase("am") && ehour.equals("12")) {
         ehour = "0";
     }
     
     if (Integer.parseInt(smin) < 0 || Integer.parseInt(smin) > 59 ||
         Integer.parseInt(emin) < 0 || Integer.parseInt(emin) > 59) { invalidMins = true; }
     
     if (smonth.length() == 1) { smonth = "0" + smonth; }
     if (sday.length() == 1) { sday = "0" + sday; }
     if (smin.length() == 1) { smin = "0" + smin; }
     if (emonth.length() == 1) { emonth = "0" + emonth; }
     if (eday.length() == 1) { eday = "0" + eday; }
     if (emin.length() == 1) { emin = "0" + emin; }
     
     stime = Integer.parseInt(shour + smin);
     etime = Integer.parseInt(ehour + emin);
     sdate = Integer.parseInt(syear + smonth + sday);
     edate = Integer.parseInt(eyear + emonth + eday);
     
     // Perform time and date sanity checks
     if (stime >= etime) { invalidTime = true; }
     if (sdate > edate) { invalidDate = true; }
     
     if (!daysSelected || invalidMins || invalidTime || invalidDate) {            // If there was invalid data sent, reject and return them
         
         out.println(SystemUtils.HeadTitle("Invalid Data"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Invalid data entered!</H3>");
         out.println("<BR><BR>The following problems were present in the suspension you submitted:<BR>");
         out.println("<ul>");
         if (!daysSelected) {
             out.println("  <li>Must select at least one day of the week</li>");
         }
         if (invalidMins) { 
             out.println("  <li>Minutes must be between 0 and 59</li>");
         }
         if (invalidTime) {
             out.println("  <li>End time must be later than start time</li>");
         }
         if (invalidDate) {
             out.println("  <li>End date must be later than end date</li>");
         }
         out.println("</ul>");
         out.println("Please correct the above and submit the suspension again.");
         out.println("<form method=\"post\" action=\"/" + rev + "/servlet/Proshop_mrest?susp\">");
         out.println("<input type=\"hidden\" name=\"returning\" value=\"true\">");
         if (id >= 0) { out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">"); }
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"mrestid\" value=\"" + mrest_id + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
         out.println("<input type=\"hidden\" name=\"edate\" value=\"" + edate + "\">");
         out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
         out.println("<input type=\"hidden\" name=\"etime\" value=\"" + etime + "\">");
         out.println("<input type=\"hidden\" name=\"eo_week\" value=\"" + recurrArr[0] + "\">");
         out.println("<input type=\"hidden\" name=\"sunday\" value=\"" + recurrArr[1] + "\">");
         out.println("<input type=\"hidden\" name=\"monday\" value=\"" + recurrArr[2] + "\">");
         out.println("<input type=\"hidden\" name=\"tuesday\" value=\"" + recurrArr[3] + "\">");
         out.println("<input type=\"hidden\" name=\"wednesday\" value=\"" + recurrArr[4] + "\">");
         out.println("<input type=\"hidden\" name=\"thursday\" value=\"" + recurrArr[5] + "\">");
         out.println("<input type=\"hidden\" name=\"friday\" value=\"" + recurrArr[6] + "\">");
         out.println("<input type=\"hidden\" name=\"saturday\" value=\"" + recurrArr[7] + "\">");
         out.println("<br><input type=\"submit\" value=\"Return\">");
         out.println("</form>");
         out.println("</CENTER></BODY></HTML>");
         
     } else {
         
         try {
             if (id >= 0) {         // suspension exists, perform an update
                 pstmt = con.prepareStatement("UPDATE rest_suspend SET " +
                         "courseName = ?, stime = ?, etime = ?, sdate = ?, edate = ?, eo_week = ?, sunday = ?, monday = ?, tuesday = ?, wednesday = ?, " +
                         "thursday = ?, friday = ?, saturday = ? " +
                         "WHERE id = ?");
                 pstmt.clearParameters();
                 pstmt.setString(1, courseName);
                 pstmt.setInt(2, stime);
                 pstmt.setInt(3, etime);
                 pstmt.setInt(4, sdate);
                 pstmt.setInt(5, edate);
                 pstmt.setInt(6, recurrArr[0]);
                 for (int i=0; i<7; i++) {
                     pstmt.setInt(7+i, recurrArr[i + 1]);
                 }
                 pstmt.setInt(14, id);
                 count = pstmt.executeUpdate();
             } else {               // new entry
                 pstmt = con.prepareStatement("INSERT INTO rest_suspend " +
                         "(mrest_id, courseName, stime, etime, sdate, edate, eo_week, sunday, monday, tuesday, " +
                         "wednesday, thursday, friday, saturday) " +
                         "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                 pstmt.clearParameters();
                 pstmt.setInt(1, mrest_id);
                 pstmt.setString(2, courseName);
                 pstmt.setInt(3, stime);
                 pstmt.setInt(4, etime);
                 pstmt.setInt(5, sdate);
                 pstmt.setInt(6, edate);
                 pstmt.setInt(7, recurrArr[0]);
                 for (int i=0; i<7; i++) {
                     pstmt.setInt(8+i, recurrArr[i + 1]);
                 }
                 count = pstmt.executeUpdate();
             }

             String url = "/" + rev + "/servlet/Proshop_mrest?susp&mrestid=" + mrest_id + "&name=" + name;

             if (count > 0) {           // Update/Insert successful
                                  
                 out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\">");
                 out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");
                 out.println("<h4 align=\"center\">Add/Update Successful!</h4>");
                 out.println("<p align=\"center\">The suspension was added/updated successfully!");
                 out.println("<br><br><a href=\"" + url + "\" style=\"text-decoration: none;\"><button align=\"center\" type=\"button\">Continue</button></a>");
                 out.println("<meta http-equiv=\"Refresh\" content=\"1; url=" + url + "\">"); // auto-refresh
             } else {                   // Update/Insert unsuccessful

                 out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\">");
                 out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");
                 out.println("<h4 align=\"center\">Add/Update Failed!</h4>");
                 out.println("<p align=\"center\">The suspension was not added/updated.");
                 out.println("<br><br><a href=\"" + url + "\" style=\"text-decoration: none;\"><button align=\"center\" type=\"button\">Continue</button></a>");
                 out.println("<meta http-equiv=\"Refresh\" content=\"1; url=" + url + "\">"); // auto-refresh
             }

             pstmt.close();
         } catch (Exception exc) {

             dbError(out, exc);

         } finally {

          try { pstmt.close(); }
          catch (Exception ignore) {}

         }
     }
     
     return;
 }
 
 // *********************************************************
 // Remove the specified member restriction suspension
 // *********************************************************
 private void deleteSuspension(HttpServletRequest req, Connection con, PrintWriter out) {
     
     int id = 0;
     int mrest_id = 0;
     int count = 0;
     
     String name = "";
     
     PreparedStatement pstmt = null;
     
     if (req.getParameter("id") != null) {
         
         id = Integer.parseInt(req.getParameter("id"));
         mrest_id = Integer.parseInt(req.getParameter("mrestid"));
         name = req.getParameter("name");

         try {
             
             // Delete the suspension with matching id from rest_suspend
             pstmt = con.prepareStatement("DELETE FROM rest_suspend WHERE id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, id);
             count = pstmt.executeUpdate();
             
             String url = "/" + rev + "/servlet/Proshop_mrest?susp&mrestid=" + mrest_id + "&name=" + name;
             
             if (count > 0) {           // Delete successful
                 
                 out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\">");
                 out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");
                 out.println("<h4 align=\"center\">Delete Successful!</h4>");
                 out.println("<p align=\"center\">The suspension was deleted successfully!");
                 out.println("<br><br><a href=\"" + url + "\" style=\"text-decoration: none;\"><button align=\"center\" type=\"button\">Continue</button></a>");
                 out.println("<meta http-equiv=\"Refresh\" content=\"1; url=" + url + "\">"); // auto-refresh
             } else {                   // Delete unsuccessful

                 out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\">");
                 out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");
                 out.println("<h4 align=\"center\">Delete Failed!</h4>");
                 out.println("<p align=\"center\">The suspension was not deleted.");
                 out.println("<br><br><a href=\"" + url + "\" style=\"text-decoration: none;\"><button align=\"center\" type=\"button\">Continue</button></a>");
                 out.println("<meta http-equiv=\"Refresh\" content=\"1; url=" + url + "\">"); // auto-refresh
             }
             
         } catch (Exception exc) {

             dbError(out, exc);

         } finally {

             try { pstmt.close(); }
             catch (Exception ignore) {}

         }
     }
     
     return;
 }
 
 // *********************************************************
 // Get the recurrence for a given suspension
 // *********************************************************
 private String getRecurrence(ResultSet rs, PrintWriter out) {
     
     String recurr = "";
     String eo_week = "";
     String lbreak = "";
     
     try {
         if (rs.getInt("eo_week") == 0) {
             eo_week = "Every ";
         } else {
             eo_week = "Every other ";
         }

         if (rs.getInt("sunday") != 0) {
             lbreak = "<br>";
             recurr += eo_week + "Sunday" + lbreak;
         }
         if (rs.getInt("monday") != 0) {
             lbreak = "<br>";
             recurr += eo_week + "Monday" + lbreak;
         }
         if (rs.getInt("tuesday") != 0) {
             lbreak = "<br>";
             recurr += eo_week + "Tuesday" + lbreak;
         }
         if (rs.getInt("wednesday") != 0) {
             lbreak = "<br>";
             recurr += eo_week + "Wednesday" + lbreak;
         }
         if (rs.getInt("thursday") != 0) {
             lbreak = "<br>";
             recurr += eo_week + "Thursday" + lbreak;
         }
         if (rs.getInt("friday") != 0) {
             lbreak = "<br>";
             recurr += eo_week + "Friday" + lbreak;
         }
         if (rs.getInt("saturday") != 0) {
             lbreak = "<br>";
             recurr += eo_week + "Saturday" + lbreak;
         }
     } catch (Exception exc) {

         dbError(out, exc);

     }
     
     return recurr;

 } // end of getRecurrence

 
 
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
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
