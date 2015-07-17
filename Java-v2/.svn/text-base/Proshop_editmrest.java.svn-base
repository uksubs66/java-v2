/***************************************************************************************     
 *   Proshop_editmrest:  This servlet will process the 'Edit Member Restriction' request from
 *                      the Proshop's Edit Member Restrictions page.
 *
 *
 *   called by:  Proshop_mrest (via doPost in HTML built by Proshop_mrest)
 *
 *   created: 12/28/2001   Bob P.
 *
 *   last updated:
 *
 *        5/20/10   Run mrest name through SystemUtils.filter() before trying to apply updates
 *        2/24/10   Added "allow_lesson" option for use for FlxRez activity restrictions.  If yes, lesson bookings can override this restriction.
 *        1/20/10   Add locations_csv support for Activities
 *       11/03/09   Remove check for 'showit' if activity as this prevents the colors on the sheet - needed.
 *                  If showit=yes, then do not show restriction in legend, but still show on sheet (like tee times).
 *       11/02/09   If activity and user does not want to show restriciton on sheets, do not call do1Rest.
 *        8/19/09   Add GenRez support for Activities
 *       11/06/08   Delete all corresponding suspensions when deleting a member restriction
 *        8/12/08   Added limited access proshop users checks
 *        5/12/04   Add sheet= parm for call from Proshop_sheet.
 *       12/15/03   Add Show=yes/no for Eldorado CC. Option to show or not to show restriction
 *                  on tee sheet legend.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/07/03   Enhancements for Version 2 of the software.
 *                  Add support for multiple courses, F/B option, mem types and mship types.
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

public class Proshop_editmrest extends HttpServlet {

                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************************
 // Process the form request from Proshop_editmrest page.
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

         removeConf(req, out); // not used any longer - doing confirmation in js

      } else {

         if (req.getParameter("Delete") != null) {

            removeMrest(req, out, session);

         } else {

            if (req.getParameter("Yes") != null) {

               updateMrest(req, out, session, resp);

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

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   //
   // Get the other parameters entered
   //
   String locations_csv = Common_Config.buildLocationsString(req);

   int mrest_id = Integer.parseInt(req.getParameter("mrest_id"));
   int allow_lesson = Integer.parseInt(req.getParameter("allow_lesson"));
   //String tmp = (req.getParameter("activity_id") == null) ? "0" : req.getParameter("activity_id");
   //int activity_id = Integer.parseInt(tmp);

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
   String recurr = req.getParameter("recurr");
   String color = req.getParameter("color");
   String course = req.getParameter("course");
   String oldCourse = req.getParameter("oldCourse");
   String fb = req.getParameter("fb");
   String show = req.getParameter("show");
   String sheet = req.getParameter("sheet");

   String ssampm = "AM";
   String seampm = "AM";

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   String [] mem = new String [parm.MAX_Mems+1];            // max of 24 member types

   String [] mship = new String [parm.MAX_Mships+1];        // max of 24 mship types


   int i = 0;
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

   //
   //  indicate parm specified for those that were
   //
   for (i=1; i<parm.MAX_Mems+1; i++) {

      if (req.getParameter("mem" +i) != null) {
         mem[i] = req.getParameter("mem" +i);             // Member Types selected
      } else {
         mem[i] = "";        
      }
   }

   for (i=1; i<parm.MAX_Mships+1; i++) {

      if (req.getParameter("mship" +i) != null) {
         mship[i] = req.getParameter("mship" +i);             // Membership Types selected
      } else {
         mship[i] = "";
      }
   }


   //
   //  verify the required fields (name reqr'ed & at least one member or memship type - rest are automatic)
   //
   if ((name.equals( "" )) ||
       ((mem[1].equals( "" )) && (mem[2].equals( "" )) && (mem[3].equals( "" )) && (mem[4].equals( "" )) &&
        (mem[5].equals( "" )) && (mem[6].equals( "" )) && (mem[7].equals( "" )) && (mem[8].equals( "" )) &&
        (mem[9].equals( "" )) && (mem[10].equals( "" )) && (mem[11].equals( "" )) && (mem[12].equals( "" )) &&
        (mem[13].equals( "" )) && (mem[14].equals( "" )) && (mem[15].equals( "" )) && (mem[16].equals( "" )) &&
        (mem[17].equals( "" )) && (mem[18].equals( "" )) && (mem[19].equals( "" )) && (mem[20].equals( "" )) &&
        (mem[21].equals( "" )) && (mem[22].equals( "" )) && (mem[23].equals( "" )) && (mem[24].equals( "" )) &&
        (mship[1].equals( "" )) && (mship[2].equals( "" )) && (mship[3].equals( "" )) && (mship[4].equals( "" )) &&
        (mship[5].equals( "" )) && (mship[6].equals( "" )) && (mship[7].equals( "" )) && (mship[8].equals( "" )) &&
        (mship[9].equals( "" )) && (mship[10].equals( "" )) && (mship[11].equals( "" )) && (mship[12].equals( "" )) &&
        (mship[13].equals( "" )) && (mship[14].equals( "" )) && (mship[15].equals( "" )) && (mship[16].equals( "" )) &&
        (mship[17].equals( "" )) && (mship[18].equals( "" )) && (mship[19].equals( "" )) && (mship[20].equals( "" )) &&
        (mship[21].equals( "" )) && (mship[22].equals( "" )) && (mship[23].equals( "" )) && (mship[24].equals( "" )))) {

      invData(out);    // inform the user and return
      return;
   }

   //
   //  can't have both member & memship type
   //
   if (((!mem[1].equals( "" )) || (!mem[2].equals( "" )) || (!mem[3].equals( "" )) || (!mem[4].equals( "" )) ||
        (!mem[5].equals( "" )) || (!mem[6].equals( "" )) || (!mem[7].equals( "" )) || (!mem[8].equals( "" )) ||
        (!mem[9].equals( "" )) || (!mem[10].equals( "" )) || (!mem[11].equals( "" )) || (!mem[12].equals( "" )) ||
        (!mem[13].equals( "" )) || (!mem[14].equals( "" )) || (!mem[15].equals( "" )) || (!mem[16].equals( "" )) ||
        (!mem[17].equals( "" )) || (!mem[18].equals( "" )) || (!mem[19].equals( "" )) || (!mem[20].equals( "" )) ||
        (!mem[21].equals( "" )) || (!mem[22].equals( "" )) || (!mem[23].equals( "" )) || (!mem[24].equals( "" ))) &&
        ((!mship[1].equals( "" )) || (!mship[2].equals( "" )) || (!mship[3].equals( "" )) || (!mship[4].equals( "" )) ||
         (!mship[5].equals( "" )) || (!mship[6].equals( "" )) || (!mship[7].equals( "" )) || (!mship[8].equals( "" )) ||
         (!mship[9].equals( "" )) || (!mship[10].equals( "" )) || (!mship[11].equals( "" )) || (!mship[12].equals( "" )) ||
         (!mship[13].equals( "" )) || (!mship[14].equals( "" )) || (!mship[15].equals( "" )) || (!mship[16].equals( "" )) ||
         (!mship[17].equals( "" )) || (!mship[18].equals( "" )) || (!mship[19].equals( "" )) || (!mship[20].equals( "" )) ||
         (!mship[21].equals( "" )) || (!mship[22].equals( "" )) || (!mship[23].equals( "" )) || (!mship[24].equals( "" )))) {

      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><H3>Input Error</H3><BR>");
      out.println("<BR><BR>You cannot specify both Member Type and Membership Type.<BR>");
      out.println("Only one type needs to be specified for the restriction.<BR>");
      out.println("<BR>Please try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
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
      out.println("<BR><BR>Apostrophes (single quotes) cannot be part of the Name.");
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
        
         pstmt = con.prepareStatement (
                 "SELECT sdate FROM restriction2 WHERE name = ? AND activity_id = ?");

         pstmt.clearParameters();
         pstmt.setString(1, name);
         pstmt.setInt(2, sess_activity_id);
         rs = pstmt.executeQuery();

         if (rs.next()) {

            dupMem(out);
            pstmt.close();
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


   if (mrest_id == 0) {          // if New Restriction

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
            "INSERT INTO restriction2 (name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, " +
            "edate, end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, " +
            "mem1, mem2, mem3, mem4, mem5, mem6, mem7, mem8, " +
            "mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, " +
            "mem17, mem18, mem19, mem20, mem21, mem22, mem23, mem24, " +
            "mship1, mship2, mship3, mship4, mship5, mship6, mship7, mship8, " +
            "mship9, mship10, mship11, mship12, mship13, mship14, mship15, mship16, " +
            "mship17, mship18, mship19, mship20, mship21, mship22, mship23, mship24, " +
            "color, courseName, fb, showit, activity_id, locations, allow_lesson) VALUES " +
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, name);       // put the parm in pstmt
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
          for (i=1; i<parm.MAX_Mems+1; i++) {
             pstmt.setString(16+i, mem[i]);
          }
          for (i=1; i<parm.MAX_Mships+1; i++) {
             pstmt.setString(40+i, mship[i]);
          }
          pstmt.setString(65, color);
          pstmt.setString(66, course);
          pstmt.setString(67, fb);
          pstmt.setString(68, show);
          pstmt.setInt(69, sess_activity_id);
          pstmt.setString(70, locations_csv);
          pstmt.setInt(71, allow_lesson);

          pstmt.executeUpdate();

          pstmt = con.prepareStatement("SELECT LAST_INSERT_ID()");
          rs = pstmt.executeQuery();
          if (rs.next()) mrest_id = rs.getInt(1);

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

           try { rs.close(); }
           catch (Exception ignore) {}

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
       out.println("<BR><BR><H3>Member Restriction Has Been Added</H3>");
       out.println("<BR><BR>Thank you, the restriction has been added to the system database.");
       out.println("<BR><BR>");
       out.println("<font size=\"2\">");
       //if (copy == false) {
       //   out.println("<form method=\"get\" action=\"Proshop_addmrest\">");
       //} else {
          out.println("<form method=\"get\" action=\"Proshop_mrest\">");
          //out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
       //}
       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form></font>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
/*
       try {
          resp.flushBuffer();      // force the repsonse to complete
       }
       catch (Exception ignore) {
       }
*/
       //
       //  Now, call utility to scan the restriction table and update tee slots in teecurr accordingly
       //
       // if (activity_id == 0 || show.equalsIgnoreCase("yes")) {   // if Golf or pro wants to show the restriction on the sheets

          SystemUtils.do1Rest(con, mrest_id);          // go update the sheets
       // }



   } else {     // existing Restriction - update it

      
      //
      //  Prompt user for confirmation
      //
      out.println(SystemUtils.HeadTitle("Update Member Restriction Confirmation"));
      out.println("<BODY><CENTER>");
      out.println("<H3>Update Member Restriction Confirmation</H3>");
      out.println("<BR>Please confirm the following updated parameters for the restriction:<br><b>" + name + "</b><br><br>");

      out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\">");

      if (sess_activity_id == 0) {

          if (!course.equals( "" )) {

             out.println("<tr><td width=\"200\" align=\"right\">");
             out.println("Course Name:&nbsp;&nbsp;");
             out.println("</td><td width=\"200\" align=\"left\">");
             out.println("&nbsp;&nbsp;&nbsp;" + course);
             out.println("</td></tr>");
          }

          out.println("<tr><td width=\"200\" align=\"right\">");
          out.println("Tees:&nbsp;&nbsp;");
          out.println("</td><td width=\"200\" align=\"left\">");
          out.println("&nbsp;&nbsp;&nbsp;" + fb);
          out.println("</td></tr>");

      } else {
/*
          out.println("<tr><td width=\"200\" align=\"right\">");
          out.println("Locations:&nbsp;&nbsp;");
          out.println("</td><td width=\"200\" align=\"left\">");
          out.println("&nbsp;&nbsp;&nbsp;" + getActivity.getActivityName(activity_id, con));
          out.println("</td></tr>");
*/
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
           for (i = 0; i < locations.size(); i++) {

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

      out.println("<tr><td width=\"200\" align=\"right\">");
      out.println("Start Date:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + smonth + "/" + sday + "/" + syear);
      out.println("</td></tr>");

      out.println("<tr><td width=\"200\" align=\"right\">");
      out.println("End Date:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + emonth + "/" + eday + "/" + eyear);
      out.println("</td></tr>");

      out.println("<tr><td width=\"200\" align=\"right\">");
      out.println("Start Time:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + shr + ":" + Utilities.ensureDoubleDigit(s_min) + "  " + ssampm);

      out.println("</td></tr>");

      out.println("<tr><td width=\"200\" align=\"right\">");
      out.println("End Time:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + ehr + ":" + Utilities.ensureDoubleDigit(e_min) + "  " + seampm);

      out.println("</td></tr>");

      out.println("<tr><td width=\"200\" align=\"right\">");
      out.println("Recurrence:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + recurr);
      out.println("</td></tr>");

      out.println("<tr><td width=\"200\" align=\"right\" valign=\"top\">");
      out.println("Members Restricted:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");

      for (i=1; i<parm.MAX_Mems+1; i++) {

         if (!mem[i].equals( "" )) {

            out.println("&nbsp;&nbsp;&nbsp;" + mem[i] + "<br>");
         }
      }
      out.println("</td></tr>");

      out.println("<tr><td width=\"200\" align=\"right\" valign=\"top\">");
      out.println("Membership Types Restricted:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");

      for (i=1; i<parm.MAX_Mships+1; i++) {

         if (!mship[i].equals( "" )) {

            out.println("&nbsp;&nbsp;&nbsp;" + mship[i] + "<br>");
         }
      }
      out.println("</td></tr>");

      // If not Golf side, display whether or not lessons can book over this restriction
      if (sess_activity_id != 0) {
          out.println("<tr><td width=\"200\" align=\"right\">");
          out.println("Allow lesson bookings:&nbsp;&nbsp;");
          out.println("</td><td width=\"200\" align=\"left\">");
          out.println("&nbsp;&nbsp;&nbsp;" + (allow_lesson == 1 ? "Yes" : "No"));
          out.println("</td></tr>");
      }

      out.println("<tr><td width=\"200\" align=\"right\">");
      out.println("Show on " + ((sess_activity_id == 0) ? "Tee" : "Time") + " Sheet:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + show);
      out.println("</td></tr>");

      out.println("<tr><td width=\"200\" align=\"right\">");
      out.println("Color:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + color);
      out.println("</td></tr></table>");

      if (sheet.equals( "yes" )) {
         out.println("<form action=\"Proshop_editmrest\" method=\"post\">");
      } else {
         out.println("<form action=\"Proshop_editmrest\" method=\"post\" target=\"bot\">");
      }
      out.println("<p>ARE YOU SURE YOU WANT TO UPDATE THIS RECORD?</p>");
      out.println("<input type=\"hidden\" name=\"rest_name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"mrest_id\" value=" + mrest_id + ">");
      out.println("<input type=\"hidden\" name=\"locations_csv\" value=\"" + locations_csv + "\">");
      out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + oldName + "\">");
      out.println("<input type=\"hidden\" name=\"smonth\" value=" + smonth + ">");
      out.println("<input type=\"hidden\" name=\"sday\" value=" + sday + ">");
      out.println("<input type=\"hidden\" name=\"syear\" value=" + syear + ">");
      out.println("<input type=\"hidden\" name=\"emonth\" value=" + emonth + ">");
      out.println("<input type=\"hidden\" name=\"eday\" value=" + eday + ">");
      out.println("<input type=\"hidden\" name=\"eyear\" value=" + eyear + ">");
      out.println("<input type=\"hidden\" name=\"start_hr\" value=" + shr + ">");
      out.println("<input type=\"hidden\" name=\"start_min\" value=" + smin + ">");
      out.println("<input type=\"hidden\" name=\"start_ampm\" value=" + sampm + ">");
      out.println("<input type=\"hidden\" name=\"end_hr\" value=" + ehr + ">");
      out.println("<input type=\"hidden\" name=\"end_min\" value=" + emin + ">");
      out.println("<input type=\"hidden\" name=\"end_ampm\" value=" + eampm + ">");
      out.println("<input type=\"hidden\" name=\"recurr\" value=\"" + recurr + "\">");

      for (i=1; i<parm.MAX_Mems+1; i++) {

         out.println("<input type=\"hidden\" name=\"mem" +i+ "\" value=\"" + mem[i] + "\">");
      }
      for (i=1; i<parm.MAX_Mships+1; i++) {

         out.println("<input type=\"hidden\" name=\"mship" +i+ "\" value=\"" + mship[i] + "\">");
      }
      out.println("<input type=\"hidden\" name=\"color\" value=\"" + color + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
      out.println("<input type=\"hidden\" name=\"show\" value=\"" + show + "\">");
      out.println("<input type=\"hidden\" name=\"allow_lesson\" value=\"" + allow_lesson + "\">");
      out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"" + sheet + "\">");

      out.println("<input type=\"submit\" value=\"Update\" name=\"Yes\" style=\"width:100px\">");
      out.println("</form><font size=\"2\">");

      if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
         out.println("<form method=\"get\" action=\"Proshop_mrest\">");
         out.println("<input type=\"submit\" value=\"Do Not Update\">");
         out.println("</form>");
      } else {
         out.println("<form><input type=\"button\" value=\"Do Not Update\" onClick=\"self.close();\">");
         out.println("</form>");
      }
      out.println("</font>");
      out.println("</CENTER><br><br></BODY></HTML>");
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
   String course = req.getParameter("course");
   String sheet = req.getParameter("sheet");

   int mrest_id = Integer.parseInt(req.getParameter("mrest_id"));

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);

   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Delete Member Restriction Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Remove Member Restriction Confirmation</H3><BR>");
   out.println("<BR>Please confirm that you wish to remove this restriction: <b>" + name + "</b><br>");

   if (sheet.equals( "yes" )) {
      out.println("<form action=\"Proshop_editmrest\" method=\"post\">");
   } else {
      out.println("<form action=\"Proshop_editmrest\" method=\"post\" target=\"bot\">");
   }
   out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS RECORD?");

   out.println("<input type=\"hidden\" name=\"mrest_id\" value =\"" + mrest_id + "\">");
   out.println("<input type=\"hidden\" name=\"rest_name\" value =\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value =\"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"sheet\" value =\"" + sheet + "\">");
     
   out.println("<br><br><input type=\"submit\" value=\"Delete\" name=\"Delete\">");
   out.println("</form><font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_mrest\">");
      out.println("<input type=\"submit\" value=\"Do Not Delete\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\"Do Not Delete\" onClick=\"self.close();\">");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************************************
   //  removeMrest - Remove a restriction from restriction table - received confirmation
   //*************************************************************************

 private void removeMrest(HttpServletRequest req, PrintWriter out, HttpSession sess) {


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
     
   String omit = "";
   
   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("rest_name");
   String course = req.getParameter("course");
   String sheet = req.getParameter("sheet");
   
   int mrest_id = Integer.parseInt(req.getParameter("mrest_id"));
   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   //
   // Delete the Restriction from the restriction table
   //
   PreparedStatement pstmt = null;
   int count = 0;
     
   try {
     
      //
      //  Delete the rest
      //
      pstmt = con.prepareStatement (
               "DELETE FROM restriction2 WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setInt(1, mrest_id);
      count = pstmt.executeUpdate();

      pstmt.close();
      
      if (count > 0) {
          //
          //  Delete corresponding suspensions
          //
          pstmt = con.prepareStatement (
                   "DELETE FROM rest_suspend WHERE mrest_id = ?");
          pstmt.clearParameters();
          pstmt.setInt(1, mrest_id);

          count = pstmt.executeUpdate();

          pstmt.close();
      }
      
   } catch (Exception exc) {

       dbError(out);
       return;

   } finally {

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   if ( sess_activity_id == 0 ) {       // if Golf
      
       //
       // Remove the Restriction from any tee sheets in teecurr table
       //
       try {

          if (!course.equals( "" ) && !course.equals( "-ALL-" )) {

             pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE restriction = ? AND courseName = ?");

             pstmt.clearParameters();
             pstmt.setString(1, omit);
             pstmt.setString(2, omit);
             pstmt.setString(3, name);
             pstmt.setString(4, course);         
             count = pstmt.executeUpdate();
             
             //
             //  Check the name for special characters and translate if ncessary
             //
             if (count == 0) {                     // if no records updated, then try translated name

                name = SystemUtils.filter(name);   // translate the name and try again
              
                pstmt = con.prepareStatement (
                  "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE restriction = ? AND courseName = ?");

                pstmt.clearParameters();
                pstmt.setString(1, omit);
                pstmt.setString(2, omit);
                pstmt.setString(3, name);
                pstmt.setString(4, course);         
                count = pstmt.executeUpdate();                
             }

          } else {

             pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE restriction = ?");

             pstmt.clearParameters();
             pstmt.setString(1, omit);
             pstmt.setString(2, omit);
             pstmt.setString(3, name);
             count = pstmt.executeUpdate();

             //
             //  Check the name for special characters and translate if ncessary
             //
             if (count == 0) {                     // if no records updated, then try translated name

                name = SystemUtils.filter(name);   // translate the name and try again
              
                pstmt = con.prepareStatement (
                  "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE restriction = ?");

                pstmt.clearParameters();
                pstmt.setString(1, omit);
                pstmt.setString(2, omit);
                pstmt.setString(3, name);
                count = pstmt.executeUpdate();                
             }
          }

       } catch (Exception ignore) {

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

       }

   } else {

       // remove this restriction from activity_sheets
       try {

           if (mrest_id > 0) {

              pstmt = con.prepareStatement (
                 "UPDATE activity_sheets " +
                 "SET rest_id = 0 " +
                 "WHERE rest_id = ?");

               pstmt.clearParameters();
               pstmt.setInt(1, mrest_id);
               pstmt.executeUpdate();
           }

        } catch (Exception exc) {

            Utilities.logError("Error in Proshop_editmrest.removeMrest(): mrest_id=" + mrest_id + ", err=" + exc.toString());

        } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

        }
   }

   //
   //  Member Restriction successfully deleted
   //
   out.println(SystemUtils.HeadTitle("Delete Member Restriction Confirmation"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Restriction Record Has Been Removed</H3><BR>");
   out.println("<BR><BR>Thank you, the restriction has been removed from the database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_mrest\">");
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
   //  updateMrest - Update a Restriction from restriction table - received a conf
   //*******************************************************************

 private void updateMrest(HttpServletRequest req, PrintWriter out, HttpSession sess, HttpServletResponse resp) {


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
     
   int mrest_id = Integer.parseInt(req.getParameter("mrest_id"));
   int sess_activity_id = (Integer)sess.getAttribute("activity_id");
   
   int i = 0;
     
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   String [] mem = new String [parm.MAX_Mems+1];            // max of 24 member types

   String [] mship = new String [parm.MAX_Mships+1];        // max of 24 mship types

   //
   // Get the parameters entered
   //
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
   String recurr = req.getParameter("recurr");             //  recurrence
   String color = req.getParameter("color");               //  color for the restriction display
   String course = req.getParameter("course");             //  course name
   String fb = req.getParameter("fb");                     //  Front/Back indicator
   String show = req.getParameter("show");
   String oldCourse = req.getParameter("oldCourse");
   String sheet = req.getParameter("sheet");                 //  From Proshop_sheet?
   String locations_csv = req.getParameter("locations_csv");

   int allow_lesson = Integer.parseInt(req.getParameter("allow_lesson"));

   for (i=1; i<parm.MAX_Mems+1; i++) {

      if (req.getParameter("mem" +i) != null) {
         mem[i] = req.getParameter("mem" +i);             // Member Types selected
      } else {
         mem[i] = "";
      }
   }

   for (i=1; i<parm.MAX_Mships+1; i++) {

      if (req.getParameter("mship" +i) != null) {
         mship[i] = req.getParameter("mship" +i);             // Membership Types selected
      } else {
         mship[i] = "";
      }
   }


   String omit = "";
   
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

   } catch (NumberFormatException ignore) {}

   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;      // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;      // create a date field from input values
   edate = edate + (emonth * 100);
   edate = edate + eday;             // date = yyyymmdd (for comparisons)

   if (s_hr != 12) {              // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr = s_hr + s_ampm;       // convert to military time (12 is always Noon or PM)
   }

   if (e_hr != 12) {              // ditto

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

   PreparedStatement pstmt = null;
   ResultSet rs = null;
     
   try {

      name = SystemUtils.filter(name);      // filter the name

      //
      //  Udate the record in the restriction table
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 " +
        "SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ?, " +
            "start_hr = ?, start_min = ?, stime = ?, " +
            "edate = ?, end_mm = ?, end_dd = ?, end_yy = ?, " +
            "end_hr = ?, end_min = ?, etime = ?, recurr = ?, " +
            "mem1 = ?, mem2 = ?, mem3 = ?, mem4 = ?, mem5 = ?, mem6 = ?, mem7 = ?, mem8 = ?, " +
            "mem9 = ?, mem10 = ?, mem11 = ?, mem12 = ?, mem13 = ?, mem14 = ?, mem15 = ?, mem16 = ?, " +
            "mem17 = ?, mem18 = ?, mem19 = ?, mem20 = ?, mem21 = ?, mem22 = ?, mem23 = ?, mem24 = ?, " +
            "mship1 = ?, mship2 = ?, mship3 = ?, mship4 = ?, mship5 = ?, mship6 = ?, mship7 = ?, mship8 = ?, " +
            "mship9 = ?, mship10 = ?, mship11 = ?, mship12 = ?, mship13 = ?, mship14 = ?, mship15 = ?, mship16 = ?, " +
            "mship17 = ?, mship18 = ?, mship19 = ?, mship20 = ?, mship21 = ?, mship22 = ?, mship23 = ?, mship24 = ?, " +
            "color = ?, courseName = ?, fb = ?, showit = ?, name = ?, locations = ?, allow_lesson = ? " +
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
      for (i=1; i<parm.MAX_Mems+1; i++) {

         pstmt.setString(15+i, mem[i]);
      }
      for (i=1; i<parm.MAX_Mships+1; i++) {

         pstmt.setString(39+i, mship[i]);
      }
      pstmt.setString(64, color);
      pstmt.setString(65, course);
      pstmt.setString(66, fb);
      pstmt.setString(67, show);
      pstmt.setString(68, name);
        
      pstmt.setString(69, locations_csv);
      pstmt.setInt(70, allow_lesson);
      
      pstmt.setInt(71, mrest_id);

      pstmt.executeUpdate();
      
   } catch (Exception exc) {

      dbError(out);
      return;

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}

   }

   //
   // Database updated - inform user (do this now to prevent delay).
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Member Restriction"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Restriction Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the restriction has been updated in the system database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");

   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"Proshop_mrest\">");
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

   } catch (Exception ignore) {}

   
   if ( sess_activity_id == 0 ) {      // if Golf
      
       int updCount = 0;

       //
       // Remove the Restriction from any tee sheets in teecurr table (in case they now need changing)
       //
       try {

          if (!oldCourse.equals( "" ) && !oldCourse.equals( "-ALL-" )) {

             pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE restriction = ? AND courseName = ?");

             pstmt.clearParameters();               // clear the parms
             pstmt.setString(1,omit);
             pstmt.setString(2,omit);
             pstmt.setString(3, oldName);              // put the parm in pstmt
             pstmt.setString(4, oldCourse);
             
             updCount = pstmt.executeUpdate();         // execute the prepared pstmt
             
             
             //
             //  Check the name for special characters and translate if ncessary
             //
             if (updCount == 0) {                     // if no records updated, then try translated name

                oldName = SystemUtils.filter(oldName);   // translate the name
               
                pstmt = con.prepareStatement (
                  "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE restriction = ? AND courseName = ?");

                pstmt.clearParameters();               // clear the parms
                pstmt.setString(1,omit);
                pstmt.setString(2,omit);
                pstmt.setString(3, oldName);              // put the parm in pstmt
                pstmt.setString(4, oldCourse);

                pstmt.executeUpdate();         // execute the prepared pstmt               
             }
             pstmt.close();              // close the stmt

          } else {

             pstmt = con.prepareStatement (
               "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE restriction = ?");

             pstmt.clearParameters();               // clear the parms
             pstmt.setString(1,omit);
             pstmt.setString(2,omit);
             pstmt.setString(3, oldName);              // put the parm in pstmt
             
             updCount = pstmt.executeUpdate();         // execute the prepared pstmt
             
             
             //
             //  Check the name for special characters and translate if ncessary
             //
             if (updCount == 0) {                     // if no records updated, then try translated name

                oldName = SystemUtils.filter(oldName);   // translate the name
               
                pstmt = con.prepareStatement (
                  "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE restriction = ?");

                pstmt.clearParameters();               // clear the parms
                pstmt.setString(1,omit);
                pstmt.setString(2,omit);
                pstmt.setString(3, oldName);              // put the parm in pstmt

                pstmt.executeUpdate();         // execute the prepared pstmt               
             }
             pstmt.close();              // close the stmt
          }

       } catch (Exception ignore) {

       } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

       }

       //
       //  Now, call utility to scan the Restriction table and update tee slots in teecurr accordingly
       //
       SystemUtils.do1Rest(con, mrest_id);

   } else {

      //
      //  Activity - remove the restriction from any activity sheets, then add it back in using the new config
      //
      try {
      
         if (mrest_id > 0) {
            
            // remove this restriction from sheets
            pstmt = con.prepareStatement (
                "UPDATE activity_sheets " +
                "SET rest_id = 0 " +
                "WHERE rest_id = ?");

             pstmt.clearParameters();
             pstmt.setInt(1, mrest_id);
             pstmt.executeUpdate();
         }
          
       } catch (Exception ignore) {
           
       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

       }
      
      //   Put Restriction back in sheets
      // if (show.equalsIgnoreCase("yes")) {   // if pro wants to show the restriction on the sheets

         SystemUtils.do1Rest(con, mrest_id);
      // }
   }

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

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }
   
  
 // *********************************************************
 // Member Restriction already exists
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
