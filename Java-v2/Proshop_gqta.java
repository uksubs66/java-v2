/***************************************************************************************     
 *   Proshop_gqta:  This servlet will process the 'Guest Quota Restrictions' request from
 *                  the Proshop's Config page.
 *
 *
 *   called by:  proshop menu (to doGet) and Proshop_gqta (to doPost from HTML built here)
 *
 *   created: 3/09/2004   Bob P.
 *
 *   last updated:
 *
 *        5/24/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        4/17/10   Add new sort by options
 *        4/16/10   Add support for unlimited guest types (guest types now stored in seperate table)
 *        4/16/10   Add locations_csv support for Activities
 *        8/19/09   Add GenRez support for Activities
 *        8/15/08   Add more years to the start and end dates until we come up with a better method.
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        6/09/06   Added javascript confirmation to delete button
 *        3/07/06   Correct the initial index value for guest parm id (i2 - was zero, now 1).
 *        1/24/05   Ver 5 - change club2 to club5.
 *        9/18/04   Ver 5 - Change getClub from SystemUtils to common.
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
import com.foretees.common.Connect;

public class Proshop_gqta extends HttpServlet {

    
   String zero = "00";
   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

  
 //**************************************************
 // Process the initial request from Proshop_main
 //**************************************************
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

   Connection con = Connect.getCon(req);                      // get DB connection

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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //int activity_id = (Integer)session.getAttribute("activity_id");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

    // Define some parms to use in the html
    //
   String name = "";       // name of restriction
   int id = 0;             // uid of restriction
   int actId = 0;          // id of activity the restriction is for
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
   int guests = 0;         // # of guests
   int multi = SystemUtils.getMulti(con);
   int local_date = (int)Utilities.getDate(con);

   String courseName = ""; // name of course
   String fb = "";         // Front/back Indicator
   String s_ampm = "";
   String e_ampm = "";
   String color = "";      // color for restriction displays (NOT USED)
   String per = "";
   String locations_csv = "";

   boolean b = false;

   int sort_by = 0;

   String sortby = "";
   if (req.getParameter("sortby") != null) {
       sortby = req.getParameter("sortby");
       if (sortby.equals("edate")) {
           sort_by = 1;
       }
   }

   
   //
   //  Build the HTML page to display the existing restrictions
   //
   out.println(SystemUtils.HeadTitle("Proshop Guest Quota Restrictions Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<table cellpadding=\"5\" border=\"0\" bgcolor=\"#336633\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>" + getActivity.getActivityName(sess_activity_id, con) + " Guest Quota Restrictions</b><br>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>To change or remove a restriction, click on the Select button within the restriction.");
      out.println("<br>");
      out.println("</font></td></tr></table><br>");
      out.println("<br><br>");

      out.println("<center>");
      out.println("<form method=\"get\" action=\"Proshop_gqta\" name=\"frmSortBy\">");
      out.println("<font size=2><b>Sort by: </font>");
      out.println("<select size=1 name=sortby onchange=\"document.forms['frmSortBy'].submit();\">");
      out.println("<option value=\"name\"" + ((sort_by == 0) ? " selected" : "") + ">Restriction Name");
      out.println("<option value=\"edate\"" + ((sort_by == 1) ? " selected" : "") + ">Ending Date, Restriction Name");
      out.println("</select>");
      out.println("</form>");
      out.println("</center>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#8B8970\">");
      out.println("<td colspan=\"" + ((multi == 0) ? 9 : 10) + "\" align=\"center\">");
      out.println("<font size=\"3\">");
      out.println("<p align=\"center\"><b>Active Guest Quota Restrictions</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Restriction Name</b></p>");
      out.println("</font></td>");
      if (sess_activity_id == 0) {
          if (multi != 0) {
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p><b>Course</b></p>");
             out.println("</font></td>");
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p><b>Tees</b></p>");
             out.println("</font></td>");
          }
      } else {
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Activity</b></p>");
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
      out.println("<font size=\"2\"><p><b># of Guests</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Per</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing restrictions (one table row per restriction)
   //
   try {

      boolean started_old = false;
      boolean curr_found = false;
      String sql = "";

      if (sort_by == 0) {
          sql = "SELECT * FROM guestqta4 WHERE activity_id = '" + sess_activity_id + "' ORDER BY name";
          // SELECT * FROM guestqta4 WHERE activity_id IN (" + in + ") ORDER BY name
      } else if (sort_by == 1) {
          sql = "SELECT * FROM guestqta4 WHERE activity_id = 0 ORDER BY edate < " + local_date + ", name";
      }

      stmt = con.createStatement();
      rs = stmt.executeQuery(sql);

      while ( rs.next() ) {

         b = true;                     // indicate restrictions exist

         id = rs.getInt("id");
         actId = rs.getInt("activity_id");
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
         guests = rs.getInt("num_guests");
         courseName = rs.getString("courseName");
         fb = rs.getString("fb");
         color = rs.getString("color");
         per = rs.getString("per");
         locations_csv = rs.getString("locations");

         if (sort_by == 1 && local_date > rs.getInt("edate")) {

             if (!started_old && curr_found) {
                 out.println("<tr bgcolor=\"#8B8970\"><td align=center colspan=" + ((multi == 0) ? 9 : 10) + "><font size=\"2\"><b>Expired Restrictions</b></font></td></tr>");
                 started_old = true;
             }

         } else {

             curr_found = true;

         }

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

         if (!color.equals( "" )) {        // if specified (hotels only)

            if (color.equals( "Default" )) {

               out.println("<tr bgcolor=\"#F5F5DC\">");
            } else {
               out.println("<tr bgcolor=\"" + color + "\">");
            }
         } else {
            out.println("<tr bgcolor=\"#F5F5DC\">");
         }
         out.println("<form method=\"post\" action=\"Proshop_gqta\" target=\"bot\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +name+ "</p>");
         out.println("</font></td>");
         if (sess_activity_id == 0) {
             if (multi != 0) {
                 out.println("<td align=\"center\">");
                 out.println("<font size=\"2\"><p>" +courseName+ "</b></p>");
                 out.println("</font></td>");
                 out.println("<td align=\"center\">");
                 out.println("<font size=\"2\"><p>" +fb+ "</p>");
                 out.println("</font></td>");
             }
         } else {
            /*
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" + getActivity.getActivityName(actId, con) + "</b></p>");
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
         out.println("<font size=\"2\"><p>" + s_hour + ":" + Utilities.ensureDoubleDigit(s_min) + " " + s_ampm + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" + e_hour + ":" + Utilities.ensureDoubleDigit(e_min) + " " + e_ampm + "</b></p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +guests+ "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +per+ "</p>");
         out.println("</font></td>");

         out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
         out.println("<input type=\"hidden\" maxlength=\"30\" name=\"name\" value=\"" + name + "\">");    // must pass whole name!!!!
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
         out.println("<td align=\"center\">");
         //out.println("<p>");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");

      }  // end of while loop
      
      if (!b) {
        
         out.println("<p>No Guest Quota Restrictions Currently Exist</p>");
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

    }  // end of try

   //
   //  End of HTML page
   //
   out.println("</table></font>");                   // end of gqta table
   out.println("</td></tr></table>");                // end of main page table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
  
   out.println("</center></font></body></html>");



 }  // end of doGet

 //
 //****************************************************************
 // Process the form request from Proshop_gqta page displayed above.
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

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
/*
    String [] month_table = { "inv", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                             "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
*/
    HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

    if (session == null) return;

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
    if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
        SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
    }

    //
    // Get the name parameter from the hidden input field
    //
    String name = ""; // req.getParameter("name");
    String courseName = "";
    if (req.getParameter("course") != null) {

        courseName = req.getParameter("course");
    }

    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    String oldCourse = courseName;    // save course name in case it changes
    String oldName = name;                // save original event name

    //long s_date = 0;
    //long e_date = 0;
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
    int guests = 0;
    int index = 0;
    int i = 0;
    int i2 = 0;
    int id = 0;

    //String recur = "";             // NOT USED
    String fb = "";
    //String courseName2 = "";
    //String color = "";             // NOT USED
    String per = "";
    String locations_csv = "";

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(sess_activity_id, con);

    // populate it
    try { getClub.getParms(con, parm, sess_activity_id); }
    catch (Exception exc) { }

    try { id = Integer.parseInt(req.getParameter("id")); }
    catch (Exception ignore) {}

    //
    //  Arrays to hold the course names and guest types
    //
    ArrayList<String> course = new ArrayList<String>();

    ArrayList<String> rguest = new ArrayList<String>();

    //
    // Get the restriction from the restriction table
    //

    // if we are here for an existing restriction the load it up
    if (id != 0) {

        try {

            pstmt = con.prepareStatement ("SELECT * FROM guestqta4 WHERE id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                name = rs.getString("name");
                //s_date = rs.getLong("sdate");
                s_month = rs.getInt("start_mm");
                s_day = rs.getInt("start_dd");
                s_year = rs.getInt("start_yy");
                shr = rs.getInt("start_hr");
                smin = rs.getInt("start_min");
                //e_date = rs.getLong("edate");
                e_month = rs.getInt("end_mm");
                e_day = rs.getInt("end_dd");
                e_year = rs.getInt("end_yy");
                ehr = rs.getInt("end_hr");
                emin = rs.getInt("end_min");
                fb = rs.getString("fb");
                guests = rs.getInt("num_guests");
                per = rs.getString("per");
                locations_csv = rs.getString("locations");

                // now look up the guest types for this guest quota
                pstmt2 = con.prepareStatement (
                        "SELECT guest_type FROM guestqta4_gtypes WHERE guestqta_id = ?");

                pstmt2.clearParameters();
                pstmt2.setInt(1, rs.getInt("id"));

                rs2 = pstmt2.executeQuery();

                while ( rs2.next() ) {

                   rguest.add(rs2.getString("guest_type"));

                }
                pstmt2.close();
                
            } // end if rs

        } catch (Exception exc) {

          out.println("<!-- ERROR LOADING RESTRICTION #" + id + " -->");
          dbError(out, exc);
          return;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { rs2.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

            try { pstmt2.close(); }
            catch (Exception ignore) {}

        }
            // done loading restriction

    } else {

        // here for new so lets set any default we want here
        Calendar cal = new GregorianCalendar();
        s_year = cal.get(Calendar.YEAR);
        e_year = s_year;
        e_day = 31;

    }

    if (sess_activity_id == 0) {

        if (parm.multi != 0) {

            try {

               course = Utilities.getCourseNames(con);     // get all the course names

            } catch (Exception exc) {

              out.println("<!-- ERROR LOADING COURSES -->");
              dbError(out, exc);
              return;
            }

        } // end multi course

    } // end if golf
    
    String ssampm = "AM";     // AM or PM for display (start hour)
    String seampm = "AM";     // AM or PM for display (end hour)
    //int sampm = 0;
    //int eampm = 0;

    if (shr > 12) {

      shr = shr - 12;        // convert to 12 hour value
      ssampm = "PM";         // indicate PM
      //sampm = 12;
    }

    if (shr == 12) {
      ssampm = "PM";         // indicate PM
    }

    if (ehr > 12) {

      ehr = ehr - 12;        // convert to 12 hour value
      seampm = "PM";         // indicate PM
      //eampm = 12;
    }

    if (ehr == 12) {
      seampm = "PM";         // indicate PM
    }

    //String alphaSmonth = month_table[s_month];  // get name for start month
    //String alphaEmonth = month_table[e_month];  // get name for end month

    //
    // Database record found - output an edit page
    //
    out.println(SystemUtils.HeadTitle("Proshop Edit Guest Quota Restriction"));

    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<table cellpadding=\"5\" border=\"1\" bgcolor=\"#336633\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    if (id == 0) {
        out.println("Complete the following information for each Restriction to be added.<br>");
        out.println("Click on 'Add' to add the Restriction.");
    } else {
        out.println("<b>Edit Guest Quota Restriction</b><br>");
        out.println("<br>Change the desired information for the restriction below.");
        out.println("<br>Click on <b>Update</b> to submit the changes.");
        out.println("<br>Click on <b>Remove</b> to delete the restriction.");
    }
    out.println("</font></td></tr></table><br>");

    // only show the cancel button when here to edit/copy
    if ( id != 0 ) {

        out.println("<font size=\"2\">");
        out.println("<table border=\"0\" align=\"center\"><tr><td>");
        out.println("<form method=\"get\" action=\"Proshop_gqta\">");
        out.println("<input type=\"submit\" value=\"Cancel\"style=\"text-decoration:underline; background:#8B8970;\">");
        out.println("</td></tr></table>");
        out.println("</form>");
        out.println("</font>");

    }

    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
    out.println("<form action=\"Proshop_editgqta\" method=\"post\" target=\"bot\">");
    out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
    out.println("<tr><td width=\"500\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\">&nbsp;&nbsp;Restriction name:&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"text\" name=\"rest_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
    out.println("<br>&nbsp;&nbsp;&nbsp;* Must be unique");
    out.println("<br><br>");

    if (sess_activity_id == 0) {

        if (parm.multi != 0) {

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

        out.println("&nbsp;&nbsp;Choose the locations for this Restriction:&nbsp;&nbsp;");
        Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, false, con, out);

        out.println("<br><br>");

    } // end if activity or not

    
    Calendar cal = new GregorianCalendar();       // get todays date
    int thisYear = cal.get(Calendar.YEAR);        // get the year
    int thisMonth = cal.get(Calendar.MONTH) + 1;
    int thisDay = cal.get(Calendar.DAY_OF_MONTH);
    
    out.println("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;");

    out.println("Month:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"smonth\">");
    Common_Config.buildOption(1, "JAN", s_month, out);
    Common_Config.buildOption(2, "FEB", s_month, out);
    Common_Config.buildOption(3, "MAR", s_month, out);
    Common_Config.buildOption(4, "APR", s_month, out);
    Common_Config.buildOption(5, "MAY", s_month, out);
    Common_Config.buildOption(6, "JUN", s_month, out);
    Common_Config.buildOption(7, "JUL", s_month, out);
    Common_Config.buildOption(8, "AUG", s_month, out);
    Common_Config.buildOption(9, "SEP", s_month, out);
    Common_Config.buildOption(10, "OCT", s_month, out);
    Common_Config.buildOption(11, "NOV", s_month, out);
    Common_Config.buildOption(12, "DEC", s_month, out);
    out.println("</select>");

    out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"sday\">");
    for (i = 1; i <= 31; i++) {

        Common_Config.buildOption(i, i, s_day, out);
    }
    out.println("</select>");

    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"syear\">");
    
    
     if (s_year < thisYear) {
         Common_Config.buildOption(s_year, s_year, s_year, out);
     }
     for (i = thisYear; i <= (thisYear + 5); i++) {

         Common_Config.buildOption(i, i, s_year, out);
     }
     
    out.println("</select><br><br>");

    out.println("&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

    out.println("Month:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"emonth\">");
    Common_Config.buildOption(1, "JAN", e_month, out);
    Common_Config.buildOption(2, "FEB", e_month, out);
    Common_Config.buildOption(3, "MAR", e_month, out);
    Common_Config.buildOption(4, "APR", e_month, out);
    Common_Config.buildOption(5, "MAY", e_month, out);
    Common_Config.buildOption(6, "JUN", e_month, out);
    Common_Config.buildOption(7, "JUL", e_month, out);
    Common_Config.buildOption(8, "AUG", e_month, out);
    Common_Config.buildOption(9, "SEP", e_month, out);
    Common_Config.buildOption(10, "OCT", e_month, out);
    Common_Config.buildOption(11, "NOV", e_month, out);
    Common_Config.buildOption(12, "DEC", e_month, out);
    out.println("</select>");

    out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"eday\">");
    for (i = 1; i <= 31; i++) {

        Common_Config.buildOption(i, i, e_day, out);
    }

    out.println("</select>");

    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"eyear\">");
    
     if (e_year < thisYear) {
         Common_Config.buildOption(e_year, e_year, e_year, out);
     }
     for (i = thisYear; i <= (thisYear + 5); i++) {

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

    out.println("&nbsp;&nbsp;Guest Types to be Restricted (select all that apply):<br>");

    //
    //  parm.guest[x] = not null if this was specified in club db table (supported)
    //  resguest[x] = not null if this was specified in restriction
    //
    //i2 = 1;
    for (i = 0; i < parm.MAX_Guests; i++) {

      if (!parm.guest[i].equals( "" )) { // if defined

         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.print("<input type=\"checkbox\"");
         for (i2 = 0; i2 < rguest.size(); i2++) {

             if (parm.guest[i].equalsIgnoreCase( rguest.get(i2) )) out.print(" checked");

         } // end i2 loop
         out.println(" name=\"guest" +(i+1)+ "\" value=\"" + parm.guest[i] + "\">&nbsp;&nbsp;" + parm.guest[i]);
      } // end if guest(#) definied
    } // end i loop
    
    out.println("<br><br>");
    out.println("&nbsp;&nbsp;Number of Guests Allowed:&nbsp;&nbsp;");
    out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" +guests+ "\" name=\"guests\">");
    out.println("&nbsp;&nbsp;Per:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"per\">");
    Common_Config.buildOption("Member", "Member", per, out);
    Common_Config.buildOption("Membership Number", "Membership Number", per, out);
    out.println("</select></p><br>");

    out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + oldName + "\">");
    out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");

    out.println("<p align=\"center\">");

    if ( id == 0 ) {

        out.println("<input type=\"hidden\" name=\"Update\" value=\"Update\">");
        out.println("<input type=\"submit\" name=\"Add\" value=\"  Add  \">");

    } else {

        out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        out.println("<input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirm('Are you sure you want to delete this restriction?')\">");
        out.println("<br><center>(To update or remove restriction)</center>");

    }

    //out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    //out.println("<input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirm('Are you sure you want to delete this restriction?')\">");

    out.println("</p></font></td></tr></form></table>");
    out.println("</td></tr></table>");                       // end of main page table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_gqta\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font></body></html>");

 }   // end of doPost   


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

}
