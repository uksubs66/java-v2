/***************************************************************************************     
 *   Proshop_block:  This servlet will process the 'Block Tee Times' request from
 *                    the Proshop's Config page.
 *
 *
 *   called by:  proshop menu (to doGet) and Proshop_block (to doPost from HTML built here)
 *
 *   created: 12/18/2002   Bob P. (V2)
 *
 *   last updated: 
 *
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *       11/02/09   Added locations_csv support for Activities
 *       10/08/09   Changed copy feature db query to pull the blocker id (was being referenced later but not grabbed from db)
 *        9/25/09   Add support for Activities
 *       11/20/08   Changed form to call to Common_Config for date/time/recurr elements
 *        8/15/08   Add more years to the start and end dates until we come up with a better method.
 *        8/11/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        1/24/05   Ver 5 - change club2 to club5.
 *        9/20/04   V5 - change getClub from SystemUtils to common.
 *        7/21/04   Correct problem where the course name was not used in doPost on copy.
 *        6/28/04   Add 'Make Copy' option to allow pro to copy an existing blocker.
 *        7/18/03   Enhancements for Version 3 of the software.
 *
 *                   Add V2 changes - add multiple courses and F/B option 
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


public class Proshop_block extends HttpServlet {


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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
   }
     
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
    // Define some parms to use in the html
    //
   String name = "";        // name of tee block
   String courseName = "";  // name of course
   int s_hour = 0;          // start time hr
   int s_min = 0;           // start time min
   int e_hour = 0;          // end time hr
   int e_min = 0;           // end time min
   int s_year = 0;          // start year
   int s_month = 0;         // start month
   int s_day = 0;           // start day
   int e_year = 0;          // end year
   int e_month = 0;         // end month
   int e_day = 0;           // end day
   int multi = 0;           // day
   int id = 0;              // uid in table
   String recurr = "";      // recurrence
   String fb = "";          // Front/back Indicator
   String locations_csv = "";
       
   String s_ampm = "";
   String e_ampm = "";

   boolean b = false;

   String act_name = getActivity.getActivityName(sess_activity_id, con);
   
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Check if call is to copy an existing blocker
   //
   if (req.getParameter("copy") != null) {

      doCopy(lottery, req, out, con, sess_activity_id);        // process the copy request
      return;
   }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   //
   // Get the Multiple Course Option
   //
   try {
      getClub.getParms(con, parm);        // get the club parms
      multi = parm.multi;
   }
   catch (Exception ignore) {
   }

   //
   //  Build the HTML page to display the existing blockers
   //
   out.println(SystemUtils.HeadTitle("Proshop Blocker Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
        
      out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      if (sess_activity_id == 0) {
        out.println("<b>Block Tee Times</b><br>");
      } else {
          out.println("<b>" + act_name + " Time Blockers</b>");
      }
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>To change or remove a blocker, click on the Select button within the blocker.");
      out.println("<br></td></tr></table>");
      out.println("<br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#8B8970\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"9\" align=\"center\">");
      } else {
         out.println("<td colspan=\"8\" align=\"center\">");
      }
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><b>Active " + ((sess_activity_id == 0) ? "Tee" : act_name) + " Blockers</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Blocker Name</b></p>");
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
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing blockers (one table row per blocker)
   //
   try {

      String in = getActivity.buildInString(sess_activity_id, 1, con);
      
      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT * FROM block2 WHERE activity_id IN (" + in + ") ORDER BY name");

      while ( rs.next() ) {

         b = true;                     // indicate blockers exist

         id = rs.getInt("id");
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
         courseName = rs.getString("courseName");
         fb = rs.getString("fb");
         locations_csv = rs.getString("locations");

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

         out.println("<tr bgcolor=\"#F5F5DC\">");
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_block\" target=\"bot\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +name+ "</p>");
         out.println("</font></td>");
         if (sess_activity_id == 0) {
             if (multi != 0) {
                out.println("<td align=\"center\">");
                out.println("<font size=\"2\"><p>" +courseName+ "</b></p>");
                out.println("</font></td>");
             }
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p>" +fb+ "</p>");
             out.println("</font></td>");

         } else {

             out.println("<td align=\"center\">");
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

         out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");       // pass the blocker id
         out.println("<input type=\"hidden\" maxlength=\"30\" name=\"name\" value=\"" + name + "\">");    // must pass whole name!!!!
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
         out.println("<td align=\"center\">");
         out.println("<p>");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");

      }  // end of while loop

      stmt.close();

      if (!b) {
        
         out.println("</font><font size=\"3\"><p><b>No Blockers Currently Exist</b></p>");
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
   out.println("</table></font>");                   // end of block table
   out.println("</td></tr></table>");                // end of main page table
        
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font></body></html>");



 }  // end of doGet

 //
 //****************************************************************
 // Process the form request from Proshop_block page displayed above.
 //
 // Use the name provided to locate the blocker record and then display
 // the record to the user and prompt for edit or delete action. 
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
   
   PreparedStatement pstmt = null;
   Statement stmt = null;
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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
   }
     
   boolean copy = false;

   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   int id = 0;          // uid of the grest we are looking for
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
   int index = 0;
   int multi = 0;        // multiple course support option
   
   long s_date = 0;
   long e_date = 0;

   String oldCourse = "";
   String courseName = "";
   String courseName2 = "";
   String recur = "";
   String fb = "";
   String locations_csv = "";

   String act_name = getActivity.getActivityName(sess_activity_id, con);
   
   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   try { id = Integer.parseInt(req.getParameter("id")); }
   catch (Exception ignore) {}
   
   //
   // Get the name parameter from the hidden input field
   //
   String name = ""; //req.getParameter("name");

   //String oldName = name;                // save original event name

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy below)
   }

   //
   // Get the blocker from the blocker table
   //
   try {

      pstmt = con.prepareStatement (
               "SELECT * FROM block2 WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setInt(1, id);      
      rs = pstmt.executeQuery();

      if ( rs.next() ) {

         id = rs.getInt("id");
         name = rs.getString("name");
         locations_csv = rs.getString("locations");
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
         courseName = rs.getString("courseName");
         fb = rs.getString("fb");

      }
      
      pstmt.close();              // close the stmt

      if ( sess_activity_id == 0 ) {

          oldCourse = courseName;    // save course name in case it changes

          //
          //  check if multi-couse is yes
          //
          stmt = con.createStatement();        // create a statement

          rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

          if (rs.next()) {

             multi = rs.getInt(1);
          }

          stmt.close();

          if (multi != 0) {
             //
             //  Multiple courses - get the names for later
             //
             course = Utilities.getCourseNames(con);     // get all the course names
          }
      
      } // end if activity is golf
      
   } catch (Exception exc) {

      dbError(out);
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
   out.println(SystemUtils.HeadTitle("Proshop Blocker Configuration"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

    out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");

    if (sess_activity_id == 0) {
        out.println("<b>Block Tee Times</b><br>");
    } else {
        out.println("<b>" + act_name + " Time Blockers</b>");
    }

    if (copy == false) {

      if (id == 0) {

          out.println("<b>Create New Blocker</b><br>");
          out.println("Complete the following information for each Blocker to be added.<br>");
          out.println("Click on 'ADD' to add the Blocker.");

      } else {

          out.println("<b>Edit Existing Blocker</b><br>");
          out.println("<br>Change the desired information for the Blocker below.<br>");
          out.println("Click on <b>Update</b> to submit the changes.");
          out.println("<br>Click on <b>Remove</b> to delete the Blocker.");

      }

    } else {

      out.println("<b>Copy Blocker</b><br>");
      out.println("<br>Change the desired information for the Blocker below.<br>");
      out.println("Click on <b>Add</b> to create the new Blocker.");
      out.println("<br><br><b>NOTE:</b> You must change the name of the Blocker.");

    }
    out.println("</font></td></tr></table><br>");


    // only show the cancel button when here to edit/copy
    if ( id != 0 ) {

     out.println("<font size=\"2\">");
     out.println("<table border=\"0\" align=\"center\"><tr><td>");
     out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_block\">");
     out.println("<input type=\"submit\" value=\"Cancel\"style=\"text-decoration:underline; background:#8B8970;\">");
     out.println("</td></tr></table>");
     out.println("</form>");
     out.println("</font>");

    }
      
    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");

    if (copy == true) {
       out.println("<form action=\"/" +rev+ "/servlet/Proshop_editblock\" method=\"post\" target=\"bot\">");
       out.println("<input type=\"hidden\" name=\"id\" value=\"0\">"); // will get processes as new
       out.println("<input type=\"hidden\" name=\"Update\" value=\"Update\">");
       //out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
    } else {
       out.println("<form action=\"/" +rev+ "/servlet/Proshop_editblock\" method=\"post\" target=\"bot\">");
       out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
    }

    out.println("<tr><td>");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\">&nbsp;&nbsp;Blocker Name:&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"text\" name=\"block_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
        
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

           for (index=0; index<course.size(); index++) {

               courseName2 = course.get(index);                    // get course name from array
               
              if (courseName2.equals( courseName )) {             // if same as existing name

                 out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
              } else {
                 out.println("<option value=\"" + courseName2 + "\">" + courseName2 + "</option>");
              }             
           }
           out.println("</select>");
           out.println("<br><br>");

        } else {

           out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
           
        }
        
        out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"fb\">");
        if (fb.equals( "Both" )) {
           out.println("<option selected value=\"Both\">Both</option>");
        } else {
           out.println("<option value=\"Both\">Both</option>");
        }
        if (fb.equals( "Front" )) {
           out.println("<option selected value=\"Front\">Front</option>");
        } else {
           out.println("<option value=\"Front\">Front</option>");
        }
        if (fb.equals( "Back" )) {
           out.println("<option selected value=\"Back\">Back</option>");
        } else {
           out.println("<option value=\"Back\">Back</option>");
        }
        
        out.println("</select><br><br>");

    } else {

        out.println("&nbsp;&nbsp;Choose the locations for this Blocker:&nbsp;&nbsp;");
        Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, false, con, out);

        out.println("<br><br>");

    } // end if activity or not
          
      
    Common_Config.displayStartDate(s_month, s_day, s_year, out);      // display the Start Date prompt

    Common_Config.displayEndDate(e_month, e_day, e_year, out);        // display the End Time prompt

    Common_Config.displayStartTime(shr, smin, ssampm, out);       // display the Start Time prompt

    Common_Config.displayEndTime(ehr, emin, seampm, out);         // display the End Time prompt

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

   out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");

   out.println("<p align=\"center\">");
     /*
     if (copy == false) {
        out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        out.println("<input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirm('Are you sure you want to delete this tee blocker?')\">");
     } else {
        out.println("<input type=\"submit\" name=\"Add\" value=\"Add\">");
     }*/

   if (copy == false) {

       if ( id == 0 ) {

           out.println("<input type=\"hidden\" name=\"Update\" value=\"Update\">");
           out.println("<input type=\"submit\" name=\"Add\" value=\"  Add  \">");

       } else {

           out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"submit\" name=\"Remove\" value=\"Remove\">");
           out.println("<br><center>(To update or remove blocker)</center>");

       }
   } else {
      out.println("<input type=\"submit\" name=\"Add\" value=\"  Add  \">");
   }

   out.println("</p></font></td></tr></form></table>");
   out.println("</td></tr></table>");                       // end of main page table
        
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_block\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font></body></html>");

 }   // end of doPost   


 // ***************************************************************************
 //  Process the copy request - display a selection list of existing blockers
 // ***************************************************************************

 private void doCopy(int lottery, HttpServletRequest req, PrintWriter out, Connection con, int sess_activity_id) {

   Statement stmt = null;
   ResultSet rs = null;

   //
   //  Build the HTML page to display the existing blockers
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Blockers Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Copy a " + ((sess_activity_id == 0) ? "Tee Time" : getActivity.getActivityName(sess_activity_id, con) + " Time") + " Blocker</b><br>");
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to create a new blocker by copying an existing blocker.<br>");
   out.println("Select the blocker you wish to copy from the list below.");
   out.println("</font></td></tr></table>");
   out.println("<br>");

   //
   //  Get and display the existing blockers
   //
   try {

      //String in = getActivity.buildInString(activity_id, 1, con);
      
      stmt = con.createStatement();        // create a statement
      rs = stmt.executeQuery("SELECT id, name FROM block2 WHERE activity_id = '" + sess_activity_id + "' ORDER BY name");

      
      // if we found any then output the form.
      rs.last();
      
      if (rs.getRow() > 0) {

          out.println("<br><font size=\"2\">");
          out.println("<p>Select the blocker you wish to copy.</p>");

          out.println("<form action=\"/" +rev+ "/servlet/Proshop_block\" method=\"post\" target=\"bot\">");
          out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");     // tell addblock its a copy
          out.println("<select size=\"1\" name=\"id\">");

          rs.beforeFirst();
          
          while (rs.next()) {
              
              out.println("<option value=\"" +rs.getInt("id")+ "\">" +rs.getString("name")+ "</option>");

          }

          stmt.close();
 
          out.println("</select><br><br>");

          out.println("<input type=\"submit\" name=\"Continue\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form>");   
          
      } else {            // no blockers exist

         out.println("<p><font size=\"3\"><b>No Blockers Currently Exist</b></p><br>");
      }

   } catch (Exception exc) {

       out.println("<BR><BR><H1>Database Access Error</H1>");
       out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR>Error: " + exc.toString());
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
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_block\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();
   
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
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
