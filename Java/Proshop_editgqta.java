/***************************************************************************************     
 *   Proshop_editgqta:  This servlet will process the 'Edit Guest Quota Restriction' request from
 *                      the Proshop's Edit Guest Quota Restrictions page.
 *
 *
 *   called by:  Proshop_gqta (via doPost in HTML built by Proshop_gqta)
 *
 *   created: 3/09/2004   Bob P.
 *
 *   last updated:
 *
 *        4/16/10   Add support for unlimited guest types (guest types now stored in seperate table)
 *        4/16/10   Add locations_csv support for Activities
 *        8/12/08   Added limited access proshop users checks
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.parmClub;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Proshop_editgqta extends HttpServlet {

                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************************
 // Process the form request from Proshop_editgqta page.
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

            removeGrest(req, out, session);

         } else {

            if (req.getParameter("Yes") != null) {

               updateGrest(req, out, session);

            }
         }
      }
   }
 }   // end of doPost


   //*************************************************
   //  updateConf - Request a confirmation from user
   //*************************************************

 private void updateConf(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   Connection con = SystemUtils.getCon(sess);            // get DB connection

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
     
   //
   // Get the other parameters entered
   //
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   //int id = Integer.parseInt(req.getParameter("id"));
   //String tmp = (req.getParameter("activity_id") == null) ? "0" : req.getParameter("activity_id");
   //int activity_id = Integer.parseInt(tmp);

   String locations_csv = Common_Config.buildLocationsString(req);

   int id = Integer.parseInt(req.getParameter("id"));

   String name = req.getParameter("rest_name");
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
   String sguests = req.getParameter("guests");
   String course = req.getParameter("course");
   String oldCourse = req.getParameter("oldCourse");
   String fb = req.getParameter("fb");
   String per = req.getParameter("per");

   String color = (req.getParameter("color") != null) ? req.getParameter("color") : "";

   String recurr = "";             // NOT USED
   String ssampm = "AM";
   String seampm = "AM";

   ArrayList<String> rguest = new ArrayList<String>();

   parmClub parm = new parmClub(sess_activity_id, con);

   for (int i = 1; i <= parm.MAX_Guests ; i++) {

       if (req.getParameter("guest" + i) != null && !req.getParameter("guest" + i).equals(""))
           rguest.add(req.getParameter("guest" + i));

   }

   int guests = 0;

   int s_min = 0;
   int e_min = 0;
      
   if (sampm.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
      ssampm = "PM";
   }

   if (eampm.equals ( "12" )) {
      seampm = "PM";
   }

   try {
      s_min = Integer.parseInt(smin);
      e_min = Integer.parseInt(emin);
      guests = Integer.parseInt(sguests);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   // Make sure if this event is for an activity then it has locations assigned to it.
   //
   if (sess_activity_id != 0 && (locations_csv == null || locations_csv.equals(""))) {

       out.println(SystemUtils.HeadTitle("Data Entry Error"));
       out.println("<BODY><CENTER>");
       out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
       out.println("<BR><BR>You musy specify at least one location for this restriction.");
       out.println("<BR>Please try again.");
       out.println("<BR><BR>");
       out.println("<font size=\"2\">");
       out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form></font>");
       out.println("</CENTER></BODY></HTML>");
       out.println("<!-- sess_activity_id=" + sess_activity_id + ", locations_csv=" + locations_csv + " -->");
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
      out.println("</input></form></font>");
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
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  verify the required fields (name reqr'ed & at least one guest type - rest are automatic)
   //
   if (name.equals( "" ) || rguest.size() == 0) {

      invData(out);    // inform the user and return
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
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);
   oldName = SystemUtils.filter(oldName);

   if ( id == 0 ) {
       
       int s_hr = Integer.parseInt(shr);
       //int s_min = Integer.parseInt(smin);
       int s_ampm = Integer.parseInt(sampm);
       int e_hr = Integer.parseInt(ehr);
       //int e_min = Integer.parseInt(emin);
       int e_ampm = Integer.parseInt(eampm);
       int s_day = Integer.parseInt(sday);
       int e_day = Integer.parseInt(eday);
       int s_year = Integer.parseInt(syear);
       int e_year = Integer.parseInt(eyear);

       int s_month = Integer.parseInt(smonth);
       int e_month = Integer.parseInt(emonth);
       
       long sdate = s_year * 10000;         // create a date field from input values
       sdate = sdate + (s_month * 100);
       sdate = sdate + s_day;               // date = yyyymmdd (for comparisons)

       long edate = e_year * 10000;         // create a date field from input values
       edate = edate + (e_month * 100);
       edate = edate + e_day;               // date = yyyymmdd (for comparisons)

       if (s_hr != 12) {                    // _hr specified as 01 - 12 (_ampm = 00 or 12)

          s_hr = s_hr + s_ampm;             // convert to military time (12 is always Noon or PM)
       }

       if (e_hr != 12) {                    // ditto

          e_hr = e_hr + e_ampm;
       }

       int stime = s_hr * 100;
       stime = stime + s_min;
       int etime = e_hr * 100;
       etime = etime + e_min;

       //
       //  verify the date and time fields
       //
       if ((sdate > edate) || (stime > etime)) {

          invData(out);    // inform the user and return
          return;
       }

       // force course names and fb to empty strings if we're working with an activity
       if (sess_activity_id > 0) {

           course = "";
           oldCourse = "";
           fb = "";
       }


       //
       //  add restriction data to the database
       //
       try {

          pstmt = con.prepareStatement (
              "INSERT INTO guestqta4 " +
                "(name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, " +
                "stime, edate, end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, num_guests, " +
                "courseName, fb, color, per, activity_id, locations) " +
              "VALUES (" +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

          pstmt.clearParameters();
          pstmt.setString(1, name);
          pstmt.setLong(2, sdate);
          pstmt.setInt(3, s_month);
          pstmt.setInt(4, s_day);
          pstmt.setInt(5, s_year);
          pstmt.setInt(6, s_hr);
          pstmt.setInt(7, s_min);
          pstmt.setInt(8, stime);
          pstmt.setLong(9, edate);
          pstmt.setInt(10, e_month);
          pstmt.setInt(11, e_day);
          pstmt.setInt(12, e_year);
          pstmt.setInt(13, e_hr);
          pstmt.setInt(14, e_min);
          pstmt.setInt(15, etime);
          pstmt.setString(16, recurr);
          pstmt.setInt(17, guests);
          pstmt.setString(18, course);
          pstmt.setString(19, fb);
          pstmt.setString(20, color);
          pstmt.setString(21, per);
          pstmt.setInt(22, sess_activity_id);
          pstmt.setString(23, locations_csv);

          pstmt.executeUpdate();

          pstmt = con.prepareStatement("SELECT LAST_INSERT_ID()");
          rs = pstmt.executeQuery();
          if (rs.next()) id = rs.getInt(1);

          pstmt = con.prepareStatement (
              "INSERT INTO guestqta4_gtypes (id, guestqta_id, guest_type) VALUES (NULL, ?, ?)");

          for (int i = 0; i < rguest.size(); i++) {

              pstmt.clearParameters();
              pstmt.setInt(1, id);
              pstmt.setString(2, rguest.get(i));
              pstmt.executeUpdate();

          }

       } catch (Exception exc) {

          out.println(SystemUtils.HeadTitle("Database Error"));
          out.println("<BODY><CENTER><BR>");
          out.println("<BR><BR><H3>Database Access Error</H3>");
          out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
          out.println("<BR>Please try again later.");
          out.println("<BR>Exception:   " + exc.getMessage());
          out.println("<BR><BR>If problem persists, contact customer support.");
          out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
          out.println("</CENTER></BODY></HTML>");
          out.close();
          return;

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

       }

       //
       // Database updated - inform user
       //
       out.println(SystemUtils.HeadTitle("Proshop Add Restriction"));
       out.println("<BODY>");
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
       out.println("<CENTER><BR>");
       out.println("<BR><BR><H3>Guest Quota Restriction Has Been Added</H3>");
       out.println("<BR><BR>Thank you, the restriction has been added to the system database.");
       out.println("<BR><BR>");
       out.println("<font size=\"2\">");
       out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_gqta?new\">");
       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</input></form></font>");
       out.println("</CENTER></BODY></HTML>");
       out.close();


   
    } else {

   //
   //  If name has changed check for dup
   //
   if (!oldName.equals( name )) {    // if name has changed

      try {

         pstmt = con.prepareStatement (
                 "SELECT sdate FROM guestqta4 WHERE name = ? AND id <> ?");

         pstmt.clearParameters();
         pstmt.setString(1, name);
         pstmt.setInt(2, id);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {

            dupMem(out);    // member restriction exists - inform the user and return
            return;
         }
         
      } catch (Exception ignored) {
          
      } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
      
   }
   
   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Update Guest Quota Restriction Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<br>");
   out.println("<H3>Update Guest Quota Restriction Confirmation</H3><BR>");
   out.println("<BR>Please confirm the following parameters for the restriction:<br><b>" + name + "</b><br><br>");

   out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\">");

   if (sess_activity_id == 0) {
       
       if (!course.equals( "" )) {

          out.println("<tr><td align=\"right\">");
          out.println("Course Name:&nbsp;&nbsp;");
          out.println("</td><td align=\"left\">");
          out.println("&nbsp;&nbsp;&nbsp;" + course);
          out.println("</td></tr>");
       }

       out.println("<tr><td align=\"right\">");
       out.println("Tees:&nbsp;&nbsp;");
       out.println("</td><td align=\"left\">");
       out.println("&nbsp;&nbsp;&nbsp;" + fb);
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
       out.println( tmp );
       out.println("</td></tr>");
       
   }
   out.println("<tr><td align=\"right\">");
   out.println("Start Date:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + smonth + "/" + sday + "/" + syear);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("End Date:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + emonth + "/" + eday + "/" + eyear);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Start Time:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");

   out.println("&nbsp;&nbsp;&nbsp;" + shr + ":" + Utilities.ensureDoubleDigit(s_min) + "  " + ssampm);
     
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("End Time:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");

   out.println("&nbsp;&nbsp;&nbsp;" + ehr + ":" + Utilities.ensureDoubleDigit(e_min) + "  " + seampm);

   out.println("</td></tr>");

   out.println("<tr><td align=\"right\" valign=\"top\">");
   out.println("Guest Types Restricted:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");

   for (int i = 0; i < rguest.size(); i++)
      out.println(rguest.get(i) + "<br>");

   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Number of Guests Allowed per " +per+ ":&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + guests);
   out.println("</td></tr></table>");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_editgqta\" method=\"post\" target=\"bot\">");
   out.println("<BR>ARE YOU SURE YOU WANT TO UPDATE THIS RECORD?");
   out.println("<input type=\"hidden\" name=\"rest_name\" value = \"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"id\" value = \"" + id + "\">");
   out.println("<input type=\"hidden\" name=\"locations_csv\" value=\"" + locations_csv + "\">");
   out.println("<input type=\"hidden\" name=\"oldName\" value = \"" + oldName + "\">");
   out.println("<input type=\"hidden\" name=\"smonth\" value = " + smonth + ">");
   out.println("<input type=\"hidden\" name=\"sday\" value = " + sday + ">");
   out.println("<input type=\"hidden\" name=\"syear\" value = " + syear + ">");
   out.println("<input type=\"hidden\" name=\"emonth\" value = " + emonth + ">");
   out.println("<input type=\"hidden\" name=\"eday\" value = " + eday + ">");
   out.println("<input type=\"hidden\" name=\"eyear\" value = " + eyear + ">");
   out.println("<input type=\"hidden\" name=\"start_hr\" value = " + shr + ">");
   out.println("<input type=\"hidden\" name=\"start_min\" value = " + smin + ">");
   out.println("<input type=\"hidden\" name=\"start_ampm\" value = " + sampm + ">");
   out.println("<input type=\"hidden\" name=\"end_hr\" value = " + ehr + ">");
   out.println("<input type=\"hidden\" name=\"end_min\" value = " + emin + ">");
   out.println("<input type=\"hidden\" name=\"end_ampm\" value = " + eampm + ">");
   out.println("<input type=\"hidden\" name=\"guests\" value=\"" + sguests + "\">");

   for (int i = 0; i < rguest.size(); i++)
      out.println("<input type=\"hidden\" name=\"guest" + (i + 1) + "\" value = \"" + rguest.get(i) + "\">");
   
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
   out.println("<input type=\"hidden\" name=\"per\" value=\"" + per + "\">");
   out.println("<input type=\"hidden\" name=\"color\" value=\"" + color + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");
      
   out.println("<BR><BR>");
   out.println("<input type=\"submit\" value=\"Yes\" name=\"Yes\">");
   out.println("</form><font size=\"2\">");
      
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_gqta\">");
   out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   
    }
    
 }  // wait for confirmation (Yes)


   //*************************************************
   //  removeConf - Request a confirmation for delete
   //*************************************************

 private void removeConf(HttpServletRequest req, PrintWriter out) {


   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("rest_name");

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);

   int id = Integer.parseInt(req.getParameter("id"));

   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Delete Guest Quota Restriction Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Remove Guest Quota Restriction Confirmation</H3><BR>");
   out.println("<BR>Please confirm that you wish to remove this restriction: <b>" + name + "</b><br>");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_editgqta\" method=\"post\" target=\"bot\">");
   out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS RECORD?");
   out.println("<BR><BR>");

   out.println("<input type=\"hidden\" name=\"id\" value =\"" + id + "\">");
   out.println("<input type=\"hidden\" name=\"rest_name\" value =\"" + name + "\">");
   out.println("<input type=\"submit\" value=\"Delete\" name=\"Delete\">");
   out.println("</form><font size=\"2\">");

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_gqta\">");
   out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************************************
   //  removeGrest - Remove a restriction from restriction table - received confirmation
   //*************************************************************************

 private void removeGrest(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   Connection con = SystemUtils.getCon(sess);            // get DB connection

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

   String name = req.getParameter("rest_name");

   int id = Integer.parseInt(req.getParameter("id"));
   int count = 0;

   //
   // Delete the Quota Restriction
   //
   PreparedStatement pstmt = null;
     
   try {
       
      pstmt = con.prepareStatement ("DELETE FROM guestqta4 WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setInt(1, id);
      count = pstmt.executeUpdate();

      if (count > 0) {      // if the quota restriction was deleted

          //
          //  Delete corresponding guest types
          //
          pstmt = con.prepareStatement (
                  "DELETE FROM guestqta4_gtypes WHERE guestqta_id = ?");
          pstmt.clearParameters();
          pstmt.setInt(1, id);
          pstmt.executeUpdate();
      }

   } catch (Exception exc) {

      dbError(out, exc);
      return;
      
   } finally {

      try { pstmt.close(); }
      catch (Exception ignore) {}

   }

   //
   //  Guest Quota Restriction successfully deleted
   //
   out.println(SystemUtils.HeadTitle("Delete Guest Quota Restriction"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Quota Restriction Record Has Been Removed</H3><BR>");
   out.println("<BR><BR>Thank you, the quota restriction \"" + name + "\" has been removed from the database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_gqta\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }  // done with delete function


   //*******************************************************************
   //  updateGrest - Update a Restriction from restriction table - received a conf
   //*******************************************************************

 private void updateGrest(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   Connection con = SystemUtils.getCon(sess);            // get DB connection

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
     
   int sess_activity_id = (Integer)sess.getAttribute("activity_id");
   
   //
   // Get the parameters entered
   //
   int id = Integer.parseInt(req.getParameter("id"));
   String tmp = (req.getParameter("activity_id") == null) ? "0" : req.getParameter("activity_id");
   int activity_id = Integer.parseInt(tmp);
   
   String name = req.getParameter("rest_name");
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
   String sguests = req.getParameter("guests");            //  # of guests (0 - 2)
   String course = req.getParameter("course");             //  course name
   String fb = req.getParameter("fb");                     //  Front/Back indicator
   String per = req.getParameter("per");                   //  Member or Tee Time
   String color = req.getParameter("color");               //  color for the restriction display
   String oldCourse = req.getParameter("oldCourse");
   String recurr = "";                                     //  recurrence - NOT USED
   String locations_csv = req.getParameter("locations_csv");
   
   ArrayList<String> rguest = new ArrayList<String>();

   parmClub parm = new parmClub(sess_activity_id, con);

   for (int i = 1; i <= parm.MAX_Guests; i++) {

       if (req.getParameter("guest" + i) != null && !req.getParameter("guest" + i).equals("")) 
           rguest.add(req.getParameter("guest" + i));

   }

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
   int guests = 0;

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
      guests = Integer.parseInt(sguests);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
       out.println("<!-- err=" + e.toString() + " -->");
   } finally {
       out.println("<!-- sguests=" + sguests + " -->");
       out.println("<!-- guests=" + guests + " -->");
   }

   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;      // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;       // create a date field from input values
   edate = edate + (emonth * 100);
   edate = edate + eday;             // date = yyyymmdd (for comparisons)

   if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon or PM)
   }

   if (e_hr != 12) {                // ditto

      e_hr = e_hr + e_ampm;
   }

   int stime = s_hr * 100;
   stime = stime + s_min;
   int etime = e_hr * 100;
   etime = etime + e_min;

   //
   //  verify the date and time fields
   //
   if ((sdate > edate) || (stime > etime)) {

      invData(out);    // inform the user and return
      return;
   }

   // force course names and fb to empty strings if we're working with an activity
   if (sess_activity_id != 0) {

       course = "";
       oldCourse = "";
       fb = "";
   }
   
   //
   //  Udate the record in the restriction table
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   try {
       
      //
      //  Check the name for special characters and translate if ncessary
      //
      pstmt = con.prepareStatement (
               "SELECT sdate FROM guestqta4 WHERE name = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, oldName);       // put the parm in stmt
      pstmt.setString(2, oldCourse);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (!rs.next()) {                     // if not found

         oldName = SystemUtils.filter(oldName);   // translate the name
      }
      pstmt.close();              // close the stmt

      //
      //  Udate the record in the restriction table
      //
      pstmt = con.prepareStatement (
        "UPDATE guestqta4 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ?, " +
        "start_hr = ?, start_min = ?, stime = ?, " +
        "edate = ?, end_mm = ?, end_dd = ?, end_yy = ?, " +
        "end_hr = ?, end_min = ?, etime = ?, recurr = ?, num_guests = ?," +
        "courseName = ?, fb = ?, color = ?, per = ?, name = ?, activity_id = ?, locations = ? " +
        "WHERE id = ?");

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
      pstmt.setInt(16, guests);
      pstmt.setString(17, course);
      pstmt.setString(18, fb);
      pstmt.setString(19, color);
      pstmt.setString(20, per);
      pstmt.setString(21, name);
      pstmt.setInt(22, activity_id);
      pstmt.setString(23, locations_csv);
      pstmt.setInt(24, id);

      pstmt.executeUpdate();     // execute the prepared stmt

      // first delete any exising ones to ensure we remove any the user unselected
      pstmt = con.prepareStatement (
          "DELETE FROM guestqta4_gtypes WHERE guestqta_id = ?");
      pstmt.clearParameters();
      pstmt.setInt(1, id);
      pstmt.executeUpdate();

      // now add the restricted guest types to their table
      pstmt = con.prepareStatement (
          "INSERT INTO guestqta4_gtypes (id, guestqta_id, guest_type) VALUES (NULL, ?, ?)");

      for (int i = 0; i < rguest.size(); i++) {

          pstmt.clearParameters();
          pstmt.setInt(1, id);
          pstmt.setString(2, rguest.get(i));
          pstmt.executeUpdate();

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

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Guest Quota Restriction"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Restriction Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the restriction has been updated in the system database.");
   out.println("<BR><BR><BR>");
   out.println("<font size=\"2\">");

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_gqta\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<br><br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 // Guest Quota Restriction already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>restriction name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please change the name to a unique value.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>Exception:   " + e.getMessage());
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
 }

}
