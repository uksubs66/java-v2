/***************************************************************************************
 *   Proshop_custom_pace_display:  This CUSTOM servlet will display the current day's tee sheet with
 *                                 pace information.  It is used to show members and staff how the groups   
 *                                 are moving that day.
 *
 *   called by:  Proshop menu (doGet)
 *
 *
 *   created: 11/18/2011 by BP for case # ?????
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.lang.Math;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.parmSlot;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.BigDate;
import com.foretees.common.Utilities;

import com.foretees.common.Connect;

public class Proshop_custom_pace_display extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the call from Login or other
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }  // end of doGet processing


 //*****************************************************
 //  doPost
 //*****************************************************
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   PreparedStatement pstmt2 = null;
   ResultSet rs2 = null;

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
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  See if we are in the timeless tees mode
   //
   boolean IS_TLT = ((Integer)session.getAttribute("tlt") == 1) ? true : false;
  
   int count = 0;
   int p = 0;
   int fives = 0;
   int index = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int teepast_id = 0;
   int pace_status_id = 0;
   int teecurr_id = 0;
   int invert = 0;
   int hole_num = 0;
   
   long date = 0;
     
   short fb = 0;
     
   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String ampm = "";
   String bgcolor = "";
   String stime = "";
   String sshow = "";
   String name = "";
   String sfb = "";
   String num = "";
   String jumps = "";
   String course = "";

   String calDate = "";
   String pace_time = "";

   boolean noShow = false;


   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH)+1;
   day = cal.get(Calendar.DAY_OF_MONTH);

   date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                       // date = yyyymmdd (for comparisons)
  
   

   //
   // Load PoP status colors into an array for quick access
   //

   String tmp_color = "";
   String [] aryPopStatusColors = new String [5];
   int tmp_id = 0;

   try {

      Statement stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT * FROM pace_status ORDER BY pace_status_sort");

      String tmp_name = "";

      while (rs.next()) {

          tmp_id = rs.getInt("pace_status_id");
          tmp_color = rs.getString("pace_status_color");
          aryPopStatusColors[tmp_id] = tmp_color;
      }

   } catch (Exception e) {

      SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
   }


   try {

      //
      //  Build the Pace of Play Dispay for today  -  use auto refresh!!!
      //
      out.println(SystemUtils.HeadTitle2("Proshop - Pace of Play Display"));
      out.println("<meta http-equiv=\"Refresh\" content=\"600; url=Proshop_custom_pace_display\">");   // Auto Refresh every 10 minutes !!!!!!!
      out.println("</HEAD>");
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#336633\" vlink=\"#336633\" alink=\"#FF0000\">");
    //  SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      //
      //************************************************************************
      //  Build page to display or print the old sheet
      //************************************************************************
      //
      out.println("<H3>Pace of Play on " + month + "/" + day + "/" + year + "</H3>");
          
     // out.println("<br><br>");
   
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"85%\">");
      out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Time</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>F/B</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Player 1</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Player 2</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Player 3</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Player 4</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\" nowrap>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<u><b>Pace</b></u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("</font></td></tr>");

      //
      //  Get the tee sheet for this date
      //
      pstmt = con.prepareStatement (
         "SELECT hr, min, time, player1, player2, " +
         "player3, player4, " +
         "fb, p91, p92, p93, p94, pace_status_id, teecurr_id " +
         "FROM teecurr2 WHERE date = ? AND blocker = '' ORDER BY time, fb");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      rs = pstmt.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         hr = rs.getInt(1);
         min = rs.getInt(2);
         time = rs.getInt(3);
         player1 = rs.getString(4);
         player2 = rs.getString(5);
         player3 = rs.getString(6);
         player4 = rs.getString(7);
         fb = rs.getShort(8);
         p91 = rs.getInt(9);
         p92 = rs.getInt(10);
         p93 = rs.getInt(11);
         p94 = rs.getInt(12);
         pace_status_id = rs.getInt("pace_status_id");
         teecurr_id = rs.getInt("teecurr_id");

        //    NOTE:  They want to show all tee times now, but may change their minds
        // if (!player1.equals("") && !player1.equalsIgnoreCase("x")) {       // if someone in the tee time
  
            //
            //  get the pace time for this tee time if it exists
            //
            pace_time = "N/A";        // init

            pstmt2 = con.prepareStatement (
                "SELECT " +
                    "HOUR(SUBTIME((SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? AND hole_number = 0), (SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? ORDER BY invert DESC LIMIT 1))) AS hr, " +
                    "MINUTE(SUBTIME((SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? AND hole_number = 0), (SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? ORDER BY invert DESC LIMIT 1))) AS min, " +
                    "invert, hole_number FROM pace_entries WHERE teecurr_id = ? ORDER BY invert DESC LIMIT 1;");

            pstmt2.clearParameters();
            pstmt2.setInt(1, teecurr_id);
            pstmt2.setInt(2, teecurr_id);
            pstmt2.setInt(3, teecurr_id);
            pstmt2.setInt(4, teecurr_id);
            pstmt2.setInt(5, teecurr_id);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {
                if (!(rs2.getInt("hr") == 0 && rs2.getInt("min") == 0)) {
                   pace_time = rs2.getInt("hr") + "h " + rs2.getInt("min") + "min";
                   hole_num = rs2.getInt("hole_number");
                   invert = rs2.getInt("invert");
                   if (invert != 18) pace_time += "<br><font size=1>(thru " + hole_num + ")</font>";
                }               
            }

            rs2.close();
            pstmt2 = null;


            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            bgcolor = "#F5F5DC";               //default

            //
            //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
            //
            sfb = "F";       // default Front 9

            if (fb == 1) {

               sfb = "B";
            }

            if (fb == 9) {

               sfb = "O";
            }

            out.println("<tr>");
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            if (min < 10) {                                 // if min value is only 1 digit
               out.println(hr + ":0" + min + ampm);
            } else {                                  
               out.println(hr + ":" + min + ampm);
            }
            out.println("</font></td></form>");

            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sfb);
            out.println("</font></td>");

            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(player1);
            out.println("</font></td>");

            if (!player2.equals("")) {

               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(player2);
               out.println("</font></td>");
            } else {
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            }

            if (!player3.equals("")) {
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(player3);
               out.println("</font></td>");
            } else {
               out.println("<td bgcolor=\"" + bgcolor + "\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            }

            if (!player4.equals("")) {
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(player4);
               out.println("</font></td>");
            } else {
               out.println("<td bgcolor=\"" + bgcolor + "\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            }

            //
            //  Next column for Pace of Play time value for this group
            //
            tmp_color = bgcolor;
            if (pace_status_id != 0) {

               tmp_color = aryPopStatusColors[pace_status_id];             // get PoP status color for this tee time
            }
            out.println("<td bgcolor=\"" + tmp_color + "\" align=\"center\" nowrap>");
            out.println("<font size=\"2\">");

            if (!pace_time.equals( "" )) {        // if pace time exists for this tee time

                  out.println(pace_time);         // show it
            } else {
                  out.println("&nbsp;");
            }
            out.println("</font></td>");
            out.println("</tr>");
       //  }          // end of IF player1
         
      }  // end of while

      pstmt.close();

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</BODY></HTML>");
      return;
   }

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                           // end of whole page
   out.println("</center></body></html>");

 }  // end of doPost



 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(HttpServletRequest req, PrintWriter out, Exception exc, int lottery) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>" + exc.getMessage());
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
   out.println("Please check the names and try again.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
 }

 // *********************************************************
 // Process
 // *********************************************************
 
 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }
 
}
