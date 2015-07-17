/***************************************************************************************
 *   Proshop_sheet_reports:  This servlet will process the 'View Tee Sheet' request from
 *                    the Proshop's Select page.
 *
 *
 *   called by:  Proshop_sheet - Report Menu to output a report
 *
 *
 *   created: 10/04/2006   Bob P. - copied Proshop_sheet to isolate report processing
 *
 *   last updated:
 *
 *       2/04/14    El Camino CC (elcaminocc) - Sort the tee sheet by fb first, then time (case 2362).
 *       1/28/14    Rio Verde CC (rioverdecc) - Added initial version of custom export for use with their closed-channel TV network.
 *      12/02/13    Boca Woods CC (bocawoodscc) - Added custom tee sheet export for them to use with their TV display program.
 *      10/25/13    Boca Woods CC (bocawoodscc) - Do not display the Index column on the Alphabetical Lists > List by Member and Times print option.
 *       2/13/13    Fixed issue with back-side times not displaying hole assignments for shotgun event times.
 *       2/12/13    Adjusted SFB value for shotgun events to not print spaces before and after the hole number.
 *      12/03/12    Correct the 'display mNum' custom for The Lakes.  Add check for custom when displaying the player names.
 *       3/10/11    Updated reports to not print out check-in status checkbox images when outputting in excel mode (they were showing up as broken images)
 *       3/09/11    Added hour seperator to Quick View report.
 *       1/20/11    Add the 'Quick View' tee sheet report.
 *      12/01/10    The Lakes CC (lakes) - split mNums into a separate column for tee sheet reports (case 1909).
 *       5/24/10    Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *       4/14/10    Don't round handicap indexes before displaying them
 *       4/08/10    Fixes for CSV output (updated menus too)
 *      12/09/09    When looking for events only check those that are active.
 *       9/25/09    Add a CSV option for any report that opts to be output in Excel/CSV format (requires csv parm in menu).
 *       7/01/09    If Shotgun and holes have been assigned, show the hole assignment in the F/B column.
 *       6/03/09    The Ridge Club - custom to add the GHIN number and index tot he alphabetical report (case 1679).
 *      11/26/08    Changed format for checking suspensions for mrest display on the tee sheet legend.
 *       9/02/08    Removed limited access REPORTS restriction since servlet
 *       8/19/08    Added handling for limited access proshop user Tee Sheet options (hdcp, mnum, bag)
 *       7/18/08    Added limited access proshop users checks
 *       5/06/08    Add tflags to player names when printing the tee sheet (case 1357).
 *      11/26/07    Add F/B column to the Alpha List of Members & Times  (Case# 1304)
 *      09/24/07    Fort Wayne - Added two columns to the Alphabetical list by Trans mode report
 *                              one for tee time and a blank one to put in a caddies name.  (Case #1093)
 *      10/24/06    Display 'Shotgun' in time column for shotgun times in Alphabetical list.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.zip.*;
import java.sql.*;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.ProcessConstants;
import com.foretees.common.verifySlot;
import com.foretees.common.Utilities;


public class Proshop_sheet_reports extends HttpServlet {


   static String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
   
   private final static String PC_color = "#FF9999";
   private final static String FC_color = "#FF3333";
   private final static String PM_color = "#00CCFF";
   private final static String FM_color = "#0066FF";
   private final static String PG_color = "#00CC66";
   private final static String FG_color = "#009900";
 
 //****************************************************************************
 // Process the call from Proshop_sheet for the 'Quick View' tee sheet report
 //****************************************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    
     
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    
    PrintWriter out = resp.getWriter();

    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    // set response content type - EXCEL NOT CURRENTLY USED (for possible future add)
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        } else {
            resp.setContentType("text/html");
        }
    }
    catch (Exception exc) {
    }
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = SystemUtils.getCon(session);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    if (req.getParameter("exportBocaWoods") != null) {
        exportBocaWoods(con, resp);
        return;
    }
    
    if (req.getParameter("exportRioVerde") != null && req.getParameter("date") != null && !req.getParameter("date").equals("")) {
        
        long tempDate = Long.parseLong(req.getParameter("date"));
        
        exportRioVerde(tempDate, con, resp);
        return;
    }
    
    String club = (String)session.getAttribute("club");
    
    boolean oldsheets = false;
    
    oldsheets = (req.getParameter("oldsheets") == null) ? false : true;

    String course = (req.getParameter("course") == null) ? "" : req.getParameter("course");
    String temp = (req.getParameter("date") == null) ? "" : req.getParameter("date");
    
    long date = Long.parseLong(temp);                 // get the date for this tee sheet
    
    /*
    if (req.getParameter("custom2") != null) {        // if custom date range entered (from allMems below)
       
       todo = "allMems2";
    }
     */

    int yy = (int)date / 10000;                     
    int mm = (int)(date - (yy * 10000)) / 100;
    int dd = (int)(date - ((yy * 10000) + (mm * 100)));
    String sdate = mm + "/" + dd + "/" + yy;
                   
    ArrayList<String> courses = new ArrayList<String>();      // unlimited courses
   
    
    
    // START PAGE OUTPUT
    out.println(SystemUtils.HeadTitle("Proshop Quick View"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\"><center>");
    
    out.println("<p align=center><font size=5>Tee Sheet Quick View<BR>For " +sdate+ "</font></p>");
    
    out.println("<p align=center><font size=3>Legend</font></p>");
    
    out.println("<table align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#FFFFFF>"); 
    out.println("<tr>" +
                    "<td align=center width=80 bgcolor=\"yellow\"><font size=2>Time of Day<br>(hour)</font></td>" +
                    "<td align=center width=80><font size=2>Open</font></td>" +
                    "<td align=center width=80 bgcolor=\"" + PM_color + "\"><font size=2>Partial<BR>Member<BR>(<b>PM</b>)</font></td>" +
                    "<td align=center width=80 bgcolor=\"" + FM_color + "\"><font size=2>Full<BR>Member<BR>(<b>FM</b>)</font></td>" +
                    "<td align=center width=80 bgcolor=\"" + PG_color + "\"><font size=2>Partial<BR>Guest<BR>(<b>PG</b>)</font></td>" +
                    "<td align=center width=80 bgcolor=\"" + FG_color + "\"><font size=2>Full<BR>Guest<BR>(<b>FG</b>)</font></td>" +
                    "<td align=center width=80 bgcolor=\"" + PC_color + "\"><font size=2>Partial<BR>Combination<BR>(<b>PC</b>)</font></td>" +
                    "<td align=center width=80 bgcolor=\"" + FC_color + "\"><font size=2>Full<BR>Combination<BR>(<b>FC</b>)</font></td>" +
                "</tr></table><BR>");
    
    out.println("<p align=center><font size=2>Hold the mouse pointer over the tee time cell to see the time and content.");
    out.println("</font></p>");
    
    //
    //   Do each course individually
    //
    if (course.equals("-ALL-")) {
       
       try {
          
         courses = Utilities.getCourseNames(con);     // get all the course names

      }
      catch (Exception exp) {
      }
         
         
      int courseCount = courses.size();            // save number of courses
 
      String courseName = "";
      int i =0;

      while (i < courseCount) {

         courseName = courses.get(i);              // get a course name
         
         displayCourse(date, courseName, oldsheets, out, con);     // do this course
                
         i++;        
      }
    
    } else {       // single course
              
      displayCourse(date, course, oldsheets, out, con);     // do this course
    }
    
    out.println("<form><p align=center><br>");
    out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
    out.println("</p></form>");
    
    
    out.println("</center></body></html>");
    out.close();
    
 }     // end of doPost
 
    

 //*****************************************************
 // Process the call from Proshop_sheet
 //*****************************************************
 //
 public static void prtReport(String report_type, HttpServletRequest req, HttpServletResponse resp) 
         throws ServletException, IOException {

   //
   //  Prevent caching so sessions are not mangled (do not do here so the images can be cached!!!)
   //
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   PrintWriter out;

   //PreparedStatement pstmtc = null;
   Statement stmt = null;
   //Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   ByteArrayOutputStream buf = null;
   String encodings = "";               // browser encodings

   boolean Gzip = false;        // default = no gzip

   String excel = "";
   String csv = "";
   String orderBy = "";

   //
   //  If report or print and excel format requested
   //
   if (req.getParameter("excel") != null) {

      excel = req.getParameter("excel");
      if (excel.equalsIgnoreCase("csv")) csv = "yes";
   }
/*
   if (req.getParameter("csv") != null) {

      csv = req.getParameter("csv");
   }
*/

   //
   //  use GZip (compression) if supported by browser
   //
   try {

      encodings = req.getHeader("Accept-Encoding");               // browser encodings

      if ((encodings != null) && (encodings.indexOf("gzip") != -1) && !csv.equalsIgnoreCase( "yes" )) {    // if browser supports gzip

         Gzip = true;
         resp.setHeader("Content-Encoding", "gzip");                     // indicate gzip

         buf = new ByteArrayOutputStream();

         GZIPOutputStream gzipOut = new GZIPOutputStream(buf);
         OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipOut, "UTF-8");
         out = new PrintWriter(outputStreamWriter);

      } else {
         out = resp.getWriter();                                         // normal output stream
      }
   }
   catch (Exception exp) {
      out = resp.getWriter();                                         // normal output stream
   }



   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      resp.setContentType("text/html");

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }
   
   resp.setContentType("text/html");      // ok to set content type now

   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");      // get club name
   String user = (String)session.getAttribute("user");      // get user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   //int lotteryS = Integer.parseInt(templott);


   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
   //                       "September", "October", "November", "December" };

   //
   //  Num of days in each month
   //
   int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

   //int count = 0;
   int courseCount = 0;
   //int p = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int type = 0;                         
   //int in_use = 0;                // event type
   int shotgun = 1;                 // event type = shotgun
   //int noSubmit = 0;
   int numCaddies = 0;              // Interlachen - number of caddies assigned, Cordillera - forecaddie indicator
   //int k = 0;                     // Interlachen & Cordillera & Pecan Plantation - form counter
   //int teecurr_id = 0;
   //int pace_status_id = 0;
   int paceofplay = 0;              // hold indicator of wether of not PoP is enabled
   //int curr_time = 0;
     
   short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;
   String courseNameT = "";
   //String courseTemp = "";
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String rest_recurr = "";
   //String rest5 = "";
   String bgcolor5 = "";
   //String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String mnum1 = "";
   String mnum2 = "";
   String mnum3 = "";
   String mnum4 = "";
   String mnum5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String ampm = "";
   //String event_rest = "";
   String bgcolor = "";
   //String stime = "";
   //String sshow = "";
   String sfb = "";
   String sfb2 = "";
   //String submit = "";
   String num = "";
   //String jumps = "";
   String hole = ""; // hole assignment

   String event1 = "";       // for legend - max 4 events, 4 rest's, 2 lotteries
   String ecolor1 = "";
   String event2 = "";
   String ecolor2 = "";
   String event3 = "";
   String ecolor3 = "";
   String event4 = "";
   String ecolor4 = "";
   String rest1 = "";
   String rcolor1 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   String blocker = "";
   //String notes = "";
   String bag = "";
   //String conf = "";
   //String orig_by = "";
   //String orig_name = "";
   String lname = "";
   String fname = "";
   String mi = "";
   String tmode = "";
   String order = "";
   String tflag1 = "";
   String tflag2 = "";
   String tflag3 = "";
   String tflag4 = "";
   String tflag5 = "";
     
   //String cordRestColor = "burlywood";        // color for Cordillera's custom member restriction

   String lottery = "";
   String lottery_color = "";
   //String lottery_recurr = "";
   String csvRow = "";
     

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   int hndcp = 0;
   int j = 0;
   //int jump = 0;

   int index = 0;
   int multi = 0;               // multiple course support
   int i = 0;
   int fives = 0;
   int fivesALL = 0;
   int g1 = 0;                  // guest indicators (1 per player)
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   //int st2 = 2;
   
   //boolean charges = false;
   //boolean noShow = false;
   //boolean checkedOut = false;
   boolean disp_hndcp = true;
   boolean disp_mnum = false;
   boolean disp_bag = false;

   boolean splitmNums = false;

   //boolean medinahMemTime = false;        // for Medinah Custom
   //boolean restrictAll = false;
   boolean suspend = false;            // Member restriction suspension
   boolean dispHdcpOption = SystemUtils.verifyProAccess(req, "display_hdcp", con, out);
   boolean dispMnumOption = SystemUtils.verifyProAccess(req, "display_mnum", con, out);
   boolean dispBagOption = SystemUtils.verifyProAccess(req, "display_bag", con, out);

   //
   //  Array to hold the course names
   //
   String courseName = "";
   
   // ArrayList<Integer> fivesA = new ArrayList<Integer> ();        // array list to hold 5-some option for each course
     
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses
   
   //
   //  parm block to hold the Course Colors
   //
   parmCourseColors colors = new parmCourseColors();          // allocate a parm block

   int colorMax = colors.colorMax;             // max number of colors defined
   
   
   int tmp_i = 0; // counter for course[], shading of course field
   
   
   /*   moved to parmCourseColors
    * 
   // set default course colors  // NOTE: CHANES TO THIS ARRAY NEED TO BE DUPLICATED IN Proshop_dsheet.java:doBlockers()
   course_color[0] = "#F5F5DC";   // beige shades
   course_color[1] = "#DDDDBE";
   course_color[2] = "#B3B392";
   course_color[3] = "#8B8970";
   course_color[4] = "#E7F0E7";   // greens shades
   course_color[5] = "#C6D3C6";
   course_color[6] = "#648A64";
   course_color[7] = "#407340";
   course_color[8] = "#FFE4C4";   // bisque
   course_color[9] = "#95B795";
   course_color[10] = "#66CDAA";  // medium aquamarine
   course_color[11] = "#20B2AA";  // light seagreen
   course_color[12] = "#3CB371";  // medium seagreen
   course_color[13] = "#F5DEB3";  // wheat
   course_color[14] = "#D2B48C";  // tan
   course_color[15] = "#999900";  //
   course_color[16] = "#FF9900";  // red-orange??
   course_color[17] = "#33FF66";  //
   course_color[18] = "#7FFFD4";  // aquamarine
   course_color[19] = "#33FFFF";  //
   course_color[20] = "#FFFFFF";  // white
    */


   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  parm block to hold the member restrictions for this date and member
   //
   parmRest parmr = new parmRest();          // allocate a parm block

   //
   //  Get the golf course name requested
   //
   String courseName1 = "";

   if (req.getParameter("course") != null) {

      courseName1 = req.getParameter("course");
   } else {
       
      // Custom to default to -ALL- courses
      if ( (club.equals("lakewoodranch") || club.equals("pelicansnest") || club.equals("fairbanksranch")) ) courseName1 = "-ALL-";
   }

   if (club.equals("lakes")) {
       splitmNums = true;
   }

   try {   
   
      //
      // Get the Multiple Course Option from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

      multi = parm.multi;
      paceofplay = parm.paceofplay;

      //
      //   Get course names if multi-course facility
      //
      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names

         courseCount = course.size();              // save number of courses
 
         course.add ("-ALL-");                     // add '-ALL-' option
           
         //
         //  Make sure we have a course (in case we came directly from the Today's Tee Sheet menu)
         //
         if (courseName1.equals( "" )) {
           
            courseName1 = course.get(0);    // grab the first one
              
            if (club.equals( "foresthighlands" )) {         // change for Forest Highlands 

               if (user.equalsIgnoreCase( "proshop3" ) || user.equalsIgnoreCase( "proshop4" )) {

                  courseName1 = "Meadow";      // setup default course (top of the list)

               } else {

                  courseName1 = "Canyon";
               }
            }
         }
      }
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database at this time (get multi parms).");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }


/*
   if (!report_type.equals( "" )) {         

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H3>TEMP</H3>");
      out.println("<BR><BR>Report Type = " +report_type);
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }
*/



   //
   //    'index' contains an index value representing the date selected
   //    (0 = today, 1 = tomorrow, etc.)
   //
   num = req.getParameter("index");         // get the index value of the day selected

   //
   //  Convert the index value from string to int
   //
   if (num == null || num.equals( "" ) || num.equalsIgnoreCase( "null" )) {
     
      index = 0;
      num = "0";
        
   } else {
     
      index = Integer.parseInt(num);
   }

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   cal.add(Calendar.DATE,index);                  // roll ahead 'index' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   month = month + 1;                            // month starts at zero

   String day_name = day_table[day_num];         // get name for day

   long date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd


   //
   //  Determine if this club wants to display handicaps for the members  (changed - converted if block 3-2-05 paul)
   //
   disp_hndcp = (parm.hndcpProSheet != 0);


   // all computation and db queries are wrapped inside this one try/catch block
   // the end of this block is the end of doPost
   try {

      //
      //  Get the System Parameters for this Course
      //
      if (courseName1.equals( "-ALL-" )) {

         //
         //  Check all courses for 5-some support
         //
         i = 0;
         loopc:
         while (i < courseCount) {

            courseName = course.get(i);       // get a course name

            if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

               if (courseName.equals( "" ) || courseName == null) {      // done if null
                  break loopc;
               }
               getParms.getCourse(con, parmc, courseName);
               
              // fivesA.add (parmc.fives);      // get fivesome option
               if (parmc.fives == 1) {
                  fives = 1;
               }
            }
            i++;
         }

      } else {       // single course requested

         getParms.getCourse(con, parmc, courseName1);

         fives = parmc.fives;      // get fivesome option
      }

      fivesALL = fives;            // save 5-somes option for table display below

      //
      //   Remove any guest types that are null - for tests below
      //
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name  (hey that's my cousins name!)
         }
         i++;
      }         // end of while loop

      //
      //  Get all restrictions for this day and user (for use when checking each tee time below)
      //
      parmr.user = user;
      parmr.mship = "";
      parmr.mtype = "";
      parmr.date = date;
      parmr.day = day_name;
      parmr.course = courseName1;

      getRests.getAll(con, parmr);       // get the restrictions
      
      //
      //   Statements to find any restrictions, events or lotteries for today
      //
      String string7b = "";
      String string7c = "";
      //String string7d = "";

      if (courseName1.equals( "-ALL-" )) {
         string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND showit = 'Yes' ORDER BY stime";
      } else {
         string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes' ORDER BY stime";
      }

      if (courseName1.equals( "-ALL-" )) {
         string7c = "SELECT name, color FROM events2b WHERE date = ? AND inactive = 0 ORDER BY stime";
      } else {
         string7c = "SELECT name, color FROM events2b WHERE date = ? AND inactive = 0 " +
                    "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
      }

      PreparedStatement pstmt7b = con.prepareStatement (string7b);
      PreparedStatement pstmt7c = con.prepareStatement (string7c);

      //
      //  Scan the events, restrictions and lotteries to build the legend
      //
      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
        
      if (!courseName1.equals( "-ALL-" )) {
         pstmt7b.setString(3, courseName1);
      }

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      while (rs.next()) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);

         boolean showRest = getRests.showRest(rs.getInt("id"), -99, rs.getInt("stime"), rs.getInt("etime"), date, day_name, courseName1, con);
         
         if (showRest) {    // Only display on legend if not suspended for entire day

             //
             //  We must check the recurrence for this day (Monday, etc.)
             //
             if ((rest_recurr.equals( "Every " + day_name )) ||          // if this day
                 (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                 ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                   (!day_name.equalsIgnoreCase( "saturday" )) &&
                   (!day_name.equalsIgnoreCase( "sunday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                  (day_name.equalsIgnoreCase( "saturday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                  (day_name.equalsIgnoreCase( "sunday" )))) {


                if ((!rest.equals( rest1 )) && (rest1.equals( "" ))) {

                   rest1 = rest;
                   rcolor1 = rcolor;

                   if (rcolor.equalsIgnoreCase( "default" )) {

                      rcolor1 = "#F5F5DC";
                   }

                } else {

                   if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (rest2.equals( "" ))) {

                      rest2 = rest;
                      rcolor2 = rcolor;

                      if (rcolor.equalsIgnoreCase( "default" )) {

                         rcolor2 = "#F5F5DC";
                      }

                   } else {

                      if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (rest3.equals( "" ))) {

                         rest3 = rest;
                         rcolor3 = rcolor;

                         if (rcolor.equalsIgnoreCase( "default" )) {

                            rcolor3 = "#F5F5DC";
                         }

                      } else {

                         if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) &&
                             (!rest.equals( rest4 )) && (rest4.equals( "" ))) {

                            rest4 = rest;
                            rcolor4 = rcolor;

                            if (rcolor.equalsIgnoreCase( "default" )) {

                               rcolor4 = "#F5F5DC";
                            }
                         }
                      }
                   }
                }
             }
         }
      }                  // end of while
      pstmt7b.close();

      pstmt7c.clearParameters();          // clear the parms
      pstmt7c.setLong(1, date);

      if (!courseName1.equals( "-ALL-" )) {
         pstmt7c.setString(2, courseName1);
      }

      rs = pstmt7c.executeQuery();      // find all matching events, if any

      while (rs.next()) {

         event = rs.getString(1);
         ecolor = rs.getString(2);

         if (!event.equals( event1 ) && event1.equals( "" )) {

            event1 = event;
            ecolor1 = ecolor;

            if (ecolor.equalsIgnoreCase( "default" )) {

               ecolor1 = "#F5F5DC";
            }

          } else {

            if (!event.equals( event1 ) && !event.equals( event2 ) && event2.equals( "" )) {

               event2 = event;
               ecolor2 = ecolor;

               if (ecolor.equalsIgnoreCase( "default" )) {

                  ecolor2 = "#F5F5DC";
               }

             } else {

               if (!event.equals( event1 ) && !event.equals( event2 ) && !event.equals( event3 ) && 
                   event3.equals( "" )) {

                  event3 = event;
                  ecolor3 = ecolor;

                  if (ecolor.equalsIgnoreCase( "default" )) {

                     ecolor3 = "#F5F5DC";
                  }

                } else {

                  if (!event.equals( event1 ) && !event.equals( event2 ) && !event.equals( event3 ) &&
                      !event.equals( event4 ) && event4.equals( "" )) {

                     event4 = event;
                     ecolor4 = ecolor;

                     if (ecolor.equalsIgnoreCase( "default" )) {

                        ecolor4 = "#F5F5DC";
                     }
                  }
               }
            }
         }
      }                  // end of while
      pstmt7c.close();


      //
      //************************************************************************
      //  Build page for printing the sheet
      //************************************************************************
      //
      
      if (!disp_hndcp) { disp_hndcp = dispHdcpOption; }      // possibly set from above, make sure it hasn't been
      disp_mnum = dispMnumOption;
      disp_bag = dispBagOption;

      if (splitmNums) disp_mnum = true;
      
      String fsz = "1";
      String double_line = ""; // flag for seeing if a double_line report was requested

      if (req.getParameter("fsz") != null) {

         fsz = req.getParameter("fsz");       // get font size if requested
      }

      if (report_type.equals( "alpha" )) {            // if Alphabetical Member List Requested

         if (req.getParameter("order") != null) {     // if user requested the alpha order (member or trans)

            order = req.getParameter("order");
         }
      }

      if (excel.equalsIgnoreCase( "yes" )) {                  // if user requested Excel Spreadsheet Format

            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format

      } else if (excel.equalsIgnoreCase( "csv" )) {

            resp.setContentType("text/csv");                                                    // text file
            resp.setHeader("Content-Disposition", "attachment;filename=\"foretees.csv\"");      // default file name

      }

      //
      // see if double lines have been requested - added by Paul S.
      //
      if (req.getParameter("double_line") != null) double_line = req.getParameter("double_line");  // get double lines request

      if (!double_line.equals("1")) double_line = "0";         // enforce two possible values

      int dbl_row_colspan = (fives != 0) ? 12 : 10;            // used for html colspan property for the double (empty) line

      if (courseName1.equals("-ALL-")) dbl_row_colspan++;      // if all courses then add one more column for the course name


      //
      //  Output the page
      //
      if (!excel.equalsIgnoreCase( "yes" ) && !csv.equalsIgnoreCase( "yes" )) {        // if normal request

         out.println(SystemUtils.HeadTitle("Proshop Print Tee Sheet"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         // debug - output the report options being used
         out.println("<!-- report_type=" + report_type + " -->");
         out.println("<!-- dispHdcpOption=" + dispHdcpOption + " -->");
         out.println("<!-- dispMnumOption=" + dispMnumOption + " -->");
         out.println("<!-- dispBagOption=" + dispBagOption + " -->");

         out.println("<table border=\"0\" align=\"center\"><tr><td align=\"center\">");
         out.println("<form method=\"link\" action=\"javascript:self.print()\">");

         if (report_type.equals( "print" )) {

            out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Sheet</button>");

         } else {

            out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Report</button>");
         }
         out.println("</form></td>");

         out.println("<td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");     // gap between buttons
         out.println("</td>");

         out.println("<td align=\"left\"><form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" value=\"Close\" onClick='self.close()' alt=\"Close\">");
         out.println("</form></td></tr></table>");

         if (report_type.equals( "notes" )) {              // if NOTES report (display all notes in sheet)

            out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<br><b>Notes Report</b><br><br>");
            out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

         } else {

            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            if (fives != 0 && !report_type.equals( "alpha" ) && !report_type.equals( "alphat" )) {
               out.println("<b>Hint:</b> Print in Landscape Mode.<br><br>");
            }
         }         
      }

      if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

         out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
         if (!courseName1.equals( "" )) {
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + courseName1 + "</b>");
         }
         out.println("</font><font size=\"2\"><br><br>");
      }

      //
      //  Process according to the report type requested
      //
      if (report_type.equals( "notes" )) {              // if NOTES report (display all notes in sheet)

         notesReport(date, courseName1, out, con);      // do report
           
         out.close();
         if (Gzip == true) {
            resp.setContentLength(buf.size());                 // set output length
            resp.getOutputStream().write(buf.toByteArray());
         }
         return;
      }

      if (!report_type.equals( "alpha" ) && !report_type.equals( "alphat" )) {   // if NOT alphabetical list

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"85%\">");
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>Time</b></u>");
               out.println("</font></td>");

               if (courseName1.equals( "-ALL-" )) {

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"1\">");
                     out.println("<u><b>Course</b></u>");
                     out.println("</font></td>");
               }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>F/B</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>Player 1</b></u> ");

               if (report_type.equals( "print" )) {
                  if (disp_hndcp == true) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                  }
                  if (disp_mnum == true && !splitmNums) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Member#</u>");
                  }
                  if (disp_bag == true) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Bag#</u>");
                  }
               } else {
                  if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                      if (!splitmNums) {
                          out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                      }
                  } else {
                     out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                  }
               }
               out.println("</font></td>");

               if (splitmNums && disp_mnum == true) {
                   out.println("<td nowrap><font color=\"#FFFFFF\" size=\"1\">Member #</font></td>");
               }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>Player 2</b></u> ");

               if (report_type.equals( "print" )) {
                  if (disp_hndcp == true) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                  }
                  if (disp_mnum == true && !splitmNums) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Member#</u>");
                  }
                  if (disp_bag == true) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Bag#</u>");
                  }
               } else {
                  if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                      if (!splitmNums) {
                          out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                      }
                  } else {
                     out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                  }
               }
               out.println("</font></td>");

               if (splitmNums && disp_mnum == true) {
                   out.println("<td nowrap><font color=\"#FFFFFF\" size=\"1\">Member #</font></td>");
               }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>Player 3</b></u> ");

               if (report_type.equals( "print" )) {
                  if (disp_hndcp == true) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                  }
                  if (disp_mnum == true && !splitmNums) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Member#</u>");
                  }
                  if (disp_bag == true) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Bag#</u>");
                  }
               } else {
                  if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                      if (!splitmNums) {
                          out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                      }
                  } else {
                     out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                  }
               }
               out.println("</font></td>");

               if (splitmNums && disp_mnum == true) {
                   out.println("<td nowrap><font color=\"#FFFFFF\" size=\"1\">Member #</font></td>");
               }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>Player 4</b></u> ");

               if (report_type.equals( "print" )) {
                  if (disp_hndcp == true) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                  }
                  if (disp_mnum == true && !splitmNums) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Member#</u>");
                  }
                  if (disp_bag == true) {
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Bag#</u>");
                  }
               } else {
                  if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                      if (!splitmNums) {
                          out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                      }
                  } else {
                     out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                  }
               }
               out.println("</font></td>");

               if (splitmNums && disp_mnum == true) {
                   out.println("<td nowrap><font color=\"#FFFFFF\" size=\"1\">Member #</font></td>");
               }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            if (fives != 0 ) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>Player 5</b></u> ");

                  if (report_type.equals( "print" )) {
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                     }
                     if (disp_mnum == true && !splitmNums) {
                         out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Member#</u>");
                     }
                     if (disp_bag == true) {
                         out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>Bag#</u>");
                     }
                  } else {
                     if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                        if (!splitmNums) {
                            out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                        }
                     } else {
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                     }
                  }
                  out.println("</font></td>");

               if (splitmNums && disp_mnum == true) {
                   out.println("<td nowrap><font color=\"#FFFFFF\" size=\"1\">Member #</font></td>");
               }

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>C/W</b></u>");
                  out.println("</font></td>");
            }

            if (club.equals( "cordillera" )) {          // if Cordillera

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>FC</b></u>");
                  out.println("</font></td>");
            }

            out.println("</tr>");
         //
         //  Done outputting header row


         //
         //  Get the tee sheet for this date
         //
         String stringTee2 = "";

         if (courseName1.equals( "-ALL-" )) {
             
             orderBy = "time, courseName, fb";

             stringTee2 = "SELECT " +
                     "hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                     "player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                     "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, " +
                     "fb, player5, username5, p5cw, hndcp5, show5, lottery, courseName, blocker, " +
                     "mNum1, mNum2, mNum3, mNum4, mNum5, " +
                     "lottery_color, p91, p92, p93, p94, p95, pos5, hole, tflag1, tflag2, tflag3, tflag4, tflag5 " +
                     "FROM teecurr2 WHERE date = ? ORDER BY " + orderBy;
         } else {
         
             if (club.equals("elcaminocc")) {
                 orderBy = "fb, time";
             } else {
                 orderBy = "time, fb";
             }
             
             stringTee2 = "SELECT " +
                     "hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                     "player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                     "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, " +
                     "fb, player5, username5, p5cw, hndcp5, show5, lottery, courseName, blocker, " +
                     "mNum1, mNum2, mNum3, mNum4, mNum5, " +
                     "lottery_color, p91, p92, p93, p94, p95, pos5, hole, tflag1, tflag2, tflag3, tflag4, tflag5 " +
                     "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY " + orderBy;
         }
         PreparedStatement pstmt = con.prepareStatement (stringTee2);

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt

         if (!courseName1.equals( "-ALL-" )) {
            pstmt.setString(2, courseName1);
         }
         rs = pstmt.executeQuery();      // execute the prepared stmt

         // loop thru the rs and display each timeslot as a row
         while (rs.next()) {

            hr = rs.getInt(1);
            min = rs.getInt(2);
            time = rs.getInt(3);
            event = rs.getString(4);
            ecolor = rs.getString(5);
            rest = rs.getString(6);
            rcolor = rs.getString(7);
            player1 = rs.getString(8);
            player2 = rs.getString(9);
            player3 = rs.getString(10);
            player4 = rs.getString(11);
            user1 = rs.getString(12);
            user2 = rs.getString(13);
            user3 = rs.getString(14);
            user4 = rs.getString(15);
            p1cw = rs.getString(16);
            p2cw = rs.getString(17);
            p3cw = rs.getString(18);
            p4cw = rs.getString(19);
            type = rs.getInt(20);
            hndcp1 = rs.getFloat(21);
            hndcp2 = rs.getFloat(22);
            hndcp3 = rs.getFloat(23);
            hndcp4 = rs.getFloat(24);
            show1 = rs.getShort(25);
            show2 = rs.getShort(26);
            show3 = rs.getShort(27);
            show4 = rs.getShort(28);
            fb = rs.getShort(29);
            player5 = rs.getString(30);
            user5 = rs.getString(31);
            p5cw = rs.getString(32);
            hndcp5 = rs.getFloat(33);
            show5 = rs.getShort(34);
            lottery = rs.getString(35);
            courseNameT = rs.getString(36);
            blocker = rs.getString(37);
            mnum1 = rs.getString(38);
            mnum2 = rs.getString(39);
            mnum3 = rs.getString(40);
            mnum4 = rs.getString(41);
            mnum5 = rs.getString(42);
            lottery_color = rs.getString(43);
            p91 = rs.getInt(44);
            p92 = rs.getInt(45);
            p93 = rs.getInt(46);
            p94 = rs.getInt(47);
            p95 = rs.getInt(48);
            if (club.equals( "cordillera" )) {          // If Cordillera - get forecaddie indicator (saved in pos5)
               numCaddies = rs.getInt("pos5");          // use this field since not used by Cordillera
            }
            hole = rs.getString("hole");
            tflag1 = rs.getString("tflag1");
            tflag2 = rs.getString("tflag2");
            tflag3 = rs.getString("tflag3");
            tflag4 = rs.getString("tflag4");
            tflag5 = rs.getString("tflag5");


            if (blocker.equals( "" )) {    // continue if tee time not blocked - else skip

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               }
               if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }

               bgcolor = "#F5F5DC";               //default

               if (!event.equals("")) {
                  bgcolor = ecolor;
               } else {

                  if (!rest.equals("")) {
                     bgcolor = rcolor;
                  } else {

                     if (!lottery.equals("")) {
                        bgcolor = lottery_color;
                     }
                  }
               }

               if (bgcolor.equals("Default")) {
                  bgcolor = "#F5F5DC";              //default
               }

               if (p91 == 1) {          // if 9 hole round
                  p1cw = p1cw + "9";
               }
               if (p92 == 1) {
                  p2cw = p2cw + "9";
               }
               if (p93 == 1) {
                  p3cw = p3cw + "9";
               }
               if (p94 == 1) {
                  p4cw = p4cw + "9";
               }
               if (p95 == 1) {
                  p5cw = p5cw + "9";
               }

               if (player1.equals("")) {
                  p1cw = "";
               }
               if (player2.equals("")) {
                  p2cw = "";
               }
               if (player3.equals("")) {
                  p3cw = "";
               }
               if (player4.equals("")) {
                  p4cw = "";
               }
               if (player5.equals("")) {
                  p5cw = "";
               }

               g1 = 0;     // init guest indicators
               g2 = 0;
               g3 = 0;
               g4 = 0;
               g5 = 0;

               //
               //  Check if any player names are guest names
               //
               if (!player1.equals( "" )) {

                  i = 0;
                  ploop1:
                  while (i < parm.MAX_Guests) {
                     if (player1.startsWith( parm.guest[i] )) {

                        g1 = 1;       // indicate player1 is a guest name
                        break ploop1;
                     }
                     i++;
                  }
               }
               if (!player2.equals( "" )) {

                  i = 0;
                  ploop2:
                  while (i < parm.MAX_Guests) {
                     if (player2.startsWith( parm.guest[i] )) {

                        g2 = 1;       // indicate player2 is a guest name
                        break ploop2;
                     }
                     i++;
                  }
               }
               if (!player3.equals( "" )) {

                  i = 0;
                  ploop3:
                  while (i < parm.MAX_Guests) {
                     if (player3.startsWith( parm.guest[i] )) {

                        g3 = 1;       // indicate player3 is a guest name
                        break ploop3;
                     }
                     i++;
                  }
               }
               if (!player4.equals( "" )) {

                  i = 0;
                  ploop4:
                  while (i < parm.MAX_Guests) {
                     if (player4.startsWith( parm.guest[i] )) {

                        g4 = 1;       // indicate player4 is a guest name
                        break ploop4;
                     }
                     i++;
                  }
               }
               if (!player5.equals( "" )) {

                  i = 0;
                  ploop5:
                  while (i < parm.MAX_Guests) {
                     if (player5.startsWith( parm.guest[i] )) {

                        g5 = 1;       // indicate player5 is a guest name
                        break ploop5;
                     }
                     i++;
                  }
               }
               
               
               //
               //   Add tflags to player names if present (tflags are from member2b and mship5 tables)
               //
               if (!player1.equals("") && !player1.equalsIgnoreCase("x") && !tflag1.equals("")) {
                  
                  player1 = player1 + " " + tflag1;     // add it to player name
               }
               if (!player2.equals("") && !player2.equalsIgnoreCase("x") && !tflag2.equals("")) {
                  
                  player2 = player2 + " " + tflag2;     // add it to player name
               }
               if (!player3.equals("") && !player3.equalsIgnoreCase("x") && !tflag3.equals("")) {
                  
                  player3 = player3 + " " + tflag3;     // add it to player name
               }
               if (!player4.equals("") && !player4.equalsIgnoreCase("x") && !tflag4.equals("")) {
                  
                  player4 = player4 + " " + tflag4;     // add it to player name
               }
               if (!player5.equals("") && !player5.equalsIgnoreCase("x") && !tflag5.equals("")) {
                  
                  player5 = player5 + " " + tflag5;     // add it to player name
               }
               
               

               //
               //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
               //
               sfb = (fb == 1) ? "B" : (fb == 9) ? "0" : (type == shotgun) ? "S" : "F";

               if (sfb.equals("F")) {
                   sfb2 = "Front";
               } else if (sfb.equals("B")) {
                   sfb2 = "Back";
               } else {
                   sfb2 = "O";
               }
               
                //
                // if restriction for this slot and its not the first time for a lottery, check restriction for this member
                //
                if (!rest.equals("") && !rcolor.equals("")) {

                    int ind = 0;
                    while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {

                        if (parmr.restName[ind].equals(rest)) {

                            // Check to make sure no suspensions apply
                            suspend = false;                        
                            for (int m=0; m<parmr.MAX; m++) {

                                if (parmr.susp[ind][m][0] == 0 && parmr.susp[ind][m][1] == 0) {
                                    m = parmr.MAX;   // don't bother checking any more
                                } else if (parmr.susp[ind][m][0] <= time && parmr.susp[ind][m][1] >= time) {    //time falls within a suspension
                                    suspend = true;                       
                                    m = parmr.MAX;     // don't bother checking any more
                                }
                            }      // end of for loop

                            if (suspend) {

                                if ((parmr.courseName[ind].equals( "-ALL-" )) || (parmr.courseName[ind].equals( courseNameT ))) {  // course ?

                                    if ((parmr.fb[ind].equals( "Both" )) || (parmr.fb[ind].equals( sfb2 ))) {    // matching f/b ?

                                        //
                                        //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                        //
                                        if (event.equals("") && lottery.equals("")) {           // change color back to default if no event

                                            // Search for the first non-suspended color to apply, or default if non found
                                            bgcolor = "#F5F5DC";   // default color

                                            int ind2 = 0;
                                            while (ind2 < parmr.MAX && !parmr.restName[ind2].equals("")) {

                                                // make sure it's not the default restriction/color, and has a non-blank, non-default color
                                                // and applies to this time
                                                if (!parmr.restName[ind2].equals(rest) && !parmr.color[ind2].equals("") && !parmr.color[ind2].equalsIgnoreCase("Default") && 
                                                        parmr.stime[ind2] <= time && parmr.etime[ind2] >= time) {      

                                                    // Check to make sure no suspensions apply
                                                    suspend = false;                        
                                                    for (int m=0; m<parmr.MAX; m++) {

                                                        if (parmr.susp[ind2][m][0] == 0 && parmr.susp[ind2][m][1] == 0) {
                                                            m = parmr.MAX;   // don't bother checking any more
                                                        } else if (parmr.susp[ind2][m][0] <= time && parmr.susp[ind2][m][1] >= time) {    //time falls within a suspension
                                                            suspend = true;                       
                                                            m = parmr.MAX;     // don't bother checking any more
                                                        }
                                                    }

                                                    if (!suspend) {

                                                        if ((parmr.courseName[ind2].equals( "-ALL-" )) || (parmr.courseName[ind2].equals( courseNameT ))) {  // course ?

                                                            if ((parmr.fb[ind2].equals( "Both" )) || (parmr.fb[ind2].equals( sfb2 ))) {    // matching f/b ?

                                                                //
                                                                //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                                //
                                                                if (event.equals("") && lottery.equals("")) {           // change color if no event

                                                                    if (!parmr.color[ind2].equals("Default")) {     // if not default

                                                                        bgcolor = parmr.color[ind2];
                                                                        ind2 = parmr.MAX;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                ind2++;
                                            }

                                            if (bgcolor5.equals( "" )) {
                                                bgcolor5 = bgcolor;         // same as others if not specified
                                            }
                                        }
                                    }
                                 }
                             }
                         }
                         ind++;
                     }      // end of while loop
                }     // end of if rest exists in teecurr
               
               //
               // Start a html row for this tee time slot
               //
               out.println("<tr>");
               out.println("<td align=\"center\" nowrap>&nbsp;");
               out.println("<font size=\"" +fsz+ "\">");
               if (type == shotgun) {                             // if Shotgun Event
                  out.println("Shotgun");
               } else {
                  out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
               }
               out.println("&nbsp;</font></td>");

               if (courseName1.equals( "-ALL-" )) {

                  //
                  //  Course Name
                  //
                  // set tmp_i equal to course index #
                  //
                  for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                      if (courseNameT.equals(course.get(tmp_i))) break;      // find index value for this course
                  }

                  out.println("<td bgcolor=\"" + colors.course_color[tmp_i] + "\" align=\"center\">");  // assign a bg color

                  out.println("<font size=\"2\">");
                  out.println(courseNameT);
                  out.println("</font></td>");
               }

               out.println("<td bgcolor=\"white\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  // display if there is something in hole, then display that instead of the fb
                  out.println((!hole.equals("")) ? "  " + hole + "  " : sfb);
                  out.println("</font></td>");

               if (!player1.equals("")) {

                  if (player1.equalsIgnoreCase("x")) {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player1);
                     out.println("</font></td>");

                  } else {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");

                     p1 = player1;                                       // copy name only

                     if (g1 == 0) {        // if not guest then add hndcp

                        if (!report_type.equals( "print" )) {   // get bag# if this is report call

                           if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                              if (!mnum1.equals( "" ) && !splitmNums) {
                                 p1 = (p1 + "  #" + mnum1);
                              }
                           } else {

                              bag = getBag(user1, con);         // get user's bag #

                              if (!bag.equals( "" )) {

                                 p1 = (p1 + "  #" + bag);
                              }
                           }

                        } else {   // call for print sheet

                           if (disp_hndcp == true) {
                              if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                 p1 = p1 + "  NH";
                              } else {
                                 if (hndcp1 <= 0) {
                                    hndcp1 = 0 - hndcp1;                          // convert to non-negative
                                    p1 = (p1 + "  " + hndcp1);
                                 } else {
                                    p1 = (p1 + "  +" + hndcp1);
                                 }
                              }
                           }
                           if (disp_mnum == true && !splitmNums) {
                               
                              if (!mnum1.equals( "" )) {
                                 p1 = (p1 + "  " + mnum1);
                              }
                           }
                           if (disp_bag == true) {
                               
                              bag = getBag(user1, con);         // get user's bag #
                              
                              if (!bag.equals( "" )) {
                                  
                                  p1 = (p1 + "  " + bag);
                              }
                           }
                        }    // end of IF report
                     }       // end of IF guest

                     if (!excel.equals("yes")) out.println(getShowImageForPrint(show1));
                     out.println("&nbsp;" + p1);
                     out.println("</font></td>");

                     if (splitmNums && disp_mnum == true) {
                         out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"" +fsz+ "\">" + (!mnum1.equals("") ? mnum1 : "&nbsp;") + "</font></td>");
                     }
                  }

               } else {
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");

                  if (splitmNums && disp_mnum == true) {
                      out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"1\">&nbsp;</font></td>");
                  }
               }

               if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
                  out.println("<td bgcolor=\"white\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println(p1cw);
               } else {
                  out.println("<td bgcolor=\"white\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println("&nbsp;");
               }
                  out.println("</font></td>");

               if (!player2.equals("")) {

                  if (player2.equalsIgnoreCase("x")) {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player2);
                     out.println("</font></td>");

                  } else {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");

                     p2 = player2;                                       // copy name only

                     if (g2 == 0) {         // if not guest then add ndcp

                        if (!report_type.equals( "print" )) {   // get bag# if this is report call

                           if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                              if (!mnum2.equals( "" ) && !splitmNums) {
                                 p2 = (p2 + "  #" + mnum2);
                              }
                           } else {

                              bag = getBag(user2, con);         // get user's bag #

                              if (!bag.equals( "" )) {

                                 p2 = (p2 + "  #" + bag);
                              }
                           }

                        } else {   // call for print sheet

                           if (disp_hndcp == true) {
                              if ((hndcp2 == 99) || (hndcp2 == -99)) {
                                 p2 = p2 + "  NH";
                              } else {
                                 if (hndcp2 <= 0) {
                                    hndcp2 = 0 - hndcp2;                          // convert to non-negative
                                    p2 = (p2 + "  " + hndcp2);
                                 } else {
                                    p2 = (p2 + "  +" + hndcp2);
                                 }
                              }
                           }
                           if (disp_mnum == true && !splitmNums) {
                               
                              if (!mnum2.equals( "" )) {
                                 p2 = (p2 + "  " + mnum2);
                              }
                           }
                           if (disp_bag == true) {
                               
                              bag = getBag(user2, con);         // get user's bag #
                              
                              if (!bag.equals( "" )) {
                                  
                                  p2 = (p2 + "  " + bag);
                              }
                           }
                        }
                     }
                     if (!excel.equals("yes")) out.println(getShowImageForPrint(show2));
                     out.println("&nbsp;" + p2);
                     out.println("</font></td>");

                     if (splitmNums && disp_mnum == true) {
                         out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"" +fsz+ "\">" + (!mnum2.equals("") ? mnum2 : "&nbsp;") + "</font></td>");
                     }
                  }
               } else {
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");

                  if (splitmNums && disp_mnum == true) {
                      out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"1\">&nbsp;</font></td>");
                  }
               }

               if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
                  out.println("<td bgcolor=\"white\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println(p2cw);
               } else {
                  out.println("<td bgcolor=\"white\">");
                  out.println("<font size=\"1\">");
                  out.println("&nbsp;");
               }
                  out.println("</font></td>");

               if (!player3.equals("")) {

                  if (player3.equalsIgnoreCase("x")) {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player3);
                     out.println("</font></td>");

                  } else {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");

                     p3 = player3;                                       // copy name only

                     if (g3 == 0) {         // if not guest then add hndcp

                        if (!report_type.equals( "print" )) {   // get bag# if this is report call

                           if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                              if (!mnum3.equals( "" ) && !splitmNums) {
                                 p3 = (p3 + "  #" + mnum3);
                              }
                           } else {

                              bag = getBag(user3, con);         // get user's bag #

                              if (!bag.equals( "" )) {

                                 p3 = (p3 + "  #" + bag);
                              }
                           }

                        } else {   // call for print sheet

                           if (disp_hndcp == true) {
                              if ((hndcp3 == 99) || (hndcp3 == -99)) {
                                 p3 = p3 + "  NH";
                              } else {
                                 if (hndcp3 <= 0) {
                                    hndcp3 = 0 - hndcp3;                          // convert to non-negative
                                    p3 = (p3 + "  " + hndcp3);
                                 } else {
                                    p3 = (p3 + "  +" + hndcp3);
                                 }
                              }
                           }
                           if (disp_mnum == true && !splitmNums) {
                               
                              if (!mnum3.equals( "" )) {
                                 p3 = (p3 + "  " + mnum3);
                              }
                           }
                           if (disp_bag == true) {
                               
                              bag = getBag(user3, con);         // get user's bag #
                              
                              if (!bag.equals( "" )) {
                                  
                                  p3 = (p3 + "  " + bag);
                              }
                           }
                        }
                     }
                     if (!excel.equals("yes")) out.println(getShowImageForPrint(show3));
                     out.println("&nbsp;" + p3);
                     out.println("</font></td>");

                     if (splitmNums && disp_mnum == true) {
                         out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"" +fsz+ "\">" + (!mnum3.equals("") ? mnum3 : "&nbsp;") + "</font></td>");
                     }
                  }

               } else {
                  out.println("<td bgcolor=\"" + bgcolor + "\">");
                  out.println("<font size=\"1\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");

                  if (splitmNums && disp_mnum == true) {
                      out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"1\">&nbsp;</font></td>");
                  }
               }

               if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
                  out.println("<td bgcolor=\"white\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println(p3cw);
               } else {
                  out.println("<td bgcolor=\"white\">");
                  out.println("<font size=\"1\">");
                  out.println("&nbsp;");
               }
                  out.println("</font></td>");

               if (!player4.equals("")) {

                  if (player4.equalsIgnoreCase("x")) {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player4);
                     out.println("</font></td>");

                  } else {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");

                     p4 = player4;                                       // copy name only

                     if (g4 == 0) {         // if not guest then add hndcp

                        if (!report_type.equals( "print" )) {   // get bag# if this is report call

                           if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                              if (!mnum4.equals( "" ) && !splitmNums) {
                                 p4 = (p4 + "  #" + mnum4);
                              }
                           } else {

                              bag = getBag(user4, con);         // get user's bag #

                              if (!bag.equals( "" )) {

                                 p4 = (p4 + "  #" + bag);
                              }
                           }

                        } else {   // call for print sheet

                           if (disp_hndcp == true) {
                              if ((hndcp4 == 99) || (hndcp4 == -99)) {
                                 p4 = p4 + "  NH";
                              } else {
                                 if (hndcp4 <= 0) {
                                    hndcp4 = 0 - hndcp4;                          // convert to non-negative
                                    p4 = (p4 + "  " + hndcp4);
                                 } else {
                                    p4 = (p4 + "  +" + hndcp4);
                                 }
                              }
                           }
                           if (disp_mnum == true && !splitmNums) {
                               
                              if (!mnum4.equals( "" )) {
                                 p4 = (p4 + "  " + mnum4);
                              }
                           }
                           if (disp_bag == true) {
                               
                              bag = getBag(user4, con);         // get user's bag #
                              
                              if (!bag.equals( "" )) {
                                  
                                  p4 = (p4 + "  " + bag);
                              }
                           }
                        }
                     }
                     if (!excel.equals("yes")) out.println(getShowImageForPrint(show4));
                     out.println("&nbsp;" + p4);
                     out.println("</font></td>");

                     if (splitmNums && disp_mnum == true) {
                         out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"" +fsz+ "\">" + (!mnum4.equals("") ? mnum4 : "&nbsp;") + "</font></td>");
                     }
                  }
               } else {
                  out.println("<td bgcolor=\"" + bgcolor + "\">");
                  out.println("<font size=\"1\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");

                  if (splitmNums && disp_mnum == true) {
                      out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"1\">&nbsp;</font></td>");
                  }
               }

               if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
                  out.println("<td bgcolor=\"white\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println(p4cw);
               } else {
                  out.println("<td bgcolor=\"white\" align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println("&nbsp;");
               }
               out.println("</font></td>");

               if (fives != 0) {

                  if (!player5.equals("")) {

                     if (player5.equalsIgnoreCase("x")) {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(player5);
                        out.println("</font></td>");

                     } else {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");

                        p5 = player5;                                       // copy name only

                        if (g5 == 0) {

                           if (!report_type.equals( "print" )) {   // get bag# if this is report call

                              if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                                 if (!mnum5.equals( "" ) && !splitmNums) {
                                    p5 = (p5 + "  #" + mnum5);
                                 }
                              } else {

                                 bag = getBag(user5, con);         // get user's bag #

                                 if (!bag.equals( "" )) {

                                    p5 = (p5 + "  #" + bag);
                                 }
                              }

                           } else {   // call for print sheet

                              if (disp_hndcp == true) {
                                 if ((hndcp5 == 99) || (hndcp5 == -99)) {
                                    p5 = p5 + "  NH";
                                 } else {
                                    if (hndcp5 <= 0) {
                                       hndcp5 = 0 - hndcp5;                          // convert to non-negative
                                       p5 = (p5 + "  " + hndcp5);
                                    } else {
                                       p5 = (p5 + "  +" + hndcp5);
                                    }
                                 }
                              }
                              if (disp_mnum == true && !splitmNums) {
                                  
                                 if (!mnum5.equals( "" )) {
                                     p5 = (p5 + "  " + mnum5);
                                 }
                              }
                              if (disp_bag == true) {
                                  
                                 bag = getBag(user5, con);         // get user's bag #
                                 
                                 if (!bag.equals( "" )) {
                                     
                                     p5 = (p5 + "  " + bag);
                                 }
                              }
                           }
                        }
                        if (!excel.equals("yes")) out.println(getShowImageForPrint(show5));
                        out.println("&nbsp;" + p5);
                        out.println("</font></td>");

                        if (splitmNums && disp_mnum == true) {
                            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"" +fsz+ "\">" + (!mnum5.equals("") ? mnum5 : "&nbsp;") + "</font></td>");
                        }
                     }
                  } else {
                     out.println("<td bgcolor=\"" + bgcolor + "\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");

                     if (splitmNums && disp_mnum == true) {
                         out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\"><font size=\"1\">&nbsp;</font></td>");
                     }
                  }

                  if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println(p5cw);
                  } else {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                  }
                  out.println("</font></td>");
               }

               if (club.equals( "cordillera" )) {         // if Cordillera - add ForeCaddie Assigned col

                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"" +fsz+ "\">");
                  if (numCaddies == 0) {
                     out.println("&nbsp;");
                  } else {
                     out.println("Y");
                  }
                  out.println("</font></td>");
               }

               out.println("</tr>");


               // if a blank line (double_line) was requested then add one   [Paul S]
               if (double_line.equals("1")) {

                   out.println("<tr><td colspan=" + dbl_row_colspan + ">&nbsp;</td></tr>");
               }

            }  // end of IF Blocker

         }  // end of while

         pstmt.close();
         out.println("</td></tr></table>");                  // end of whole print page

      } else {

         //
         //  Request is for an alphabetical list of members on this day's tee sheet
         //
         if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

            out.println("<font size=\"3\">");

            if (report_type.equals( "alphat" )) {   // if list by member and tee time
               out.println("<b>Alphabetical List of Members and Tee Times</b><br>");
            } else {
               if (order.equals( "trans" )) {
                  out.println("<b>Alphabetical List of Members and Guests by Mode of Transportation</b>");
               } else {
                  out.println("<b>Alphabetical List of Members and Guests</b>");
               }
            }
            out.println("</font>");
            out.println("<br><font size=\"2\">");
            if (report_type.equals( "alpha" )) {
               out.println("<b>(Guests are listed directly under their host.)</b>");
               out.println("<br><br></font>");
            }

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellspacing=\"3\" cellpadding=\"3\">");
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Member Name</b></u>");
               out.println("</font></td>");

            if (report_type.equals( "alpha" )) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Guest</b></u> ");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Member #</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Mode of Trans</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Checked In?</b></u>");
                  out.println("</font></td>");

               if (club.equals("fortwayne") && order.equals( "trans" )) {

                   out.println("<td align=\"center\">");
                      out.println("<font color=\"#FFFFFF\" size=\"2\">");
                      out.println("<u><b>Tee Time</b></u>");
                      out.println("</font></td>");

                   out.println("<td align=\"center\">");
                      out.println("<font color=\"#FFFFFF\" size=\"2\">");
                      out.println("<u><b>Caddies' Name</b></u>");
                      out.println("</font></td>");
               }

            } else {      // list by member & tee times

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Tee Time</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>F / B</b></u>");
                  out.println("</font></td>");

               if (!courseName1.equals( "" )) {         // if course specified or ALL, then include

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Course</b></u>");
                     out.println("</font></td>");
               }

               //if (club.equals( "ridgeclub" )) {         // if The Ridge Club - add GHIN # and index

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>GHIN #</b></u>");
                     out.println("</font></td>");

                  if (!club.equals("bocawoodscc")) {
                      out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"2\">");
                         out.println("<u><b>Index</b></u>");
                         out.println("</font></td>");
                  }
               //}
            }

            out.println("</tr>");
         }

         //
         //  Gather all the members from the tee sheet and save the info in teereport4 for processing
         //
         PreparedStatement pstmta1 = null;
         String stringTee4 = "";
         String omit = "";
         String currCW = "";
         String lastCW = "";
         String rowColor = "#F5F5DC";

         if (courseName1.equals( "-ALL-" )) {

            stringTee4 = "SELECT " +
                     "time, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                     "event_type, show1, show2, show3, show4, " +
                     "username5, p5cw, show5, courseName, mNum1, mNum2, mNum3, mNum4, mNum5, " +
                     "userg1, userg2, userg3, userg4, userg5, p91, p92, p93, p94, p95, fb, hole " +
                     "FROM teecurr2 WHERE date = ? AND " +
                     "(username1 != ? OR username2 != ? OR username3 != ? OR username4 != ? OR username5 != ? OR " +
                     "userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?)";
         } else {
            stringTee4 = "SELECT " +
                     "time, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                     "event_type, show1, show2, show3, show4, " +
                     "username5, p5cw, show5, courseName, mNum1, mNum2, mNum3, mNum4, mNum5, " +
                     "userg1, userg2, userg3, userg4, userg5, p91, p92, p93, p94, p95, fb, hole " +
                     "FROM teecurr2 WHERE date = ? AND " +
                     "(username1 != ? OR username2 != ? OR username3 != ? OR username4 != ? OR username5 != ? OR " +
                     "userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?) " +
                     "AND courseName = ?";
         }
         PreparedStatement pstmt = con.prepareStatement (stringTee4);

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt
         pstmt.setString(2, omit);
         pstmt.setString(3, omit);
         pstmt.setString(4, omit);
         pstmt.setString(5, omit);
         pstmt.setString(6, omit);
         pstmt.setString(7, omit);
         pstmt.setString(8, omit);
         pstmt.setString(9, omit);
         pstmt.setString(10, omit);
         pstmt.setString(11, omit);

         if (!courseName1.equals( "-ALL-" )) {
            pstmt.setString(12, courseName1);
         }
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            time = rs.getInt(1);
            user1 = rs.getString(2);
            user2 = rs.getString(3);
            user3 = rs.getString(4);
            user4 = rs.getString(5);
            p1cw = rs.getString(6);
            p2cw = rs.getString(7);
            p3cw = rs.getString(8);
            p4cw = rs.getString(9);
            type = rs.getInt(10);
            show1 = rs.getShort(11);
            show2 = rs.getShort(12);
            show3 = rs.getShort(13);
            show4 = rs.getShort(14);
            user5 = rs.getString(15);
            p5cw = rs.getString(16);
            show5 = rs.getShort(17);
            courseNameT = rs.getString(18);
            mnum1 = rs.getString(19);
            mnum2 = rs.getString(20);
            mnum3 = rs.getString(21);
            mnum4 = rs.getString(22);
            mnum5 = rs.getString(23);
            userg1 = rs.getString(24);
            userg2 = rs.getString(25);
            userg3 = rs.getString(26);
            userg4 = rs.getString(27);
            userg5 = rs.getString(28);
            p91 = rs.getInt(29);
            p92 = rs.getInt(30);
            p93 = rs.getInt(31);
            p94 = rs.getInt(32);
            p95 = rs.getInt(33);
            fb = rs.getShort(34);
            hole = rs.getString(35);

            if (p91 == 1) {          // if 9 hole round
               p1cw = p1cw + "9";
            }
            if (p92 == 1) {
               p2cw = p2cw + "9";
            }
            if (p93 == 1) {
               p3cw = p3cw + "9";
            }
            if (p94 == 1) {
               p4cw = p4cw + "9";
            }
            if (p95 == 1) {
               p5cw = p5cw + "9";
            }

            if (type == shotgun) {        // if Shotgun Event

               time = 99999;              // indicate shotgun
            }

            sfb = "F";       // default Front 9
            
            if (type == shotgun) {
               if (!hole.equals("")) {
                  sfb = hole;
               } else {
                  sfb = "S";
               }
            } else if (fb == 1) {
                sfb = "B";
            } else if (fb == 9) {
                sfb = "O";
            }
            
            //
            //  Save each member's info for later
            //
            if (!user1.equals( "" )) {

               lname = "";
               fname = "";
               mi = "";

               pstmta1 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi " +
                        "FROM member2b WHERE username = ?");

               pstmta1.clearParameters();          // clear the parms
               pstmta1.setString(1, user1);
               rs2 = pstmta1.executeQuery();      // execute the prepared stmt

               if (rs2.next()) {

                  lname = rs2.getString(1);
                  fname = rs2.getString(2);
                  mi = rs2.getString(3);
               }
               pstmta1.close();                   // close the stmt

               if (!lname.equals( "" )) {       // if name found

                  //
                  // save info in teereport4 (temporary)
                  //
                  pstmta1 = con.prepareStatement (
                    "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time, fb) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, lname);
                  pstmta1.setString(2, fname);
                  pstmta1.setString(3, mi);
                  pstmta1.setString(4, user1);
                  pstmta1.setString(5, p1cw);
                  pstmta1.setString(6, mnum1);
                  pstmta1.setShort(7, show1);
                  pstmta1.setString(8, courseNameT);
                  pstmta1.setInt(9, time);
                  pstmta1.setString(10, sfb);
                  pstmta1.executeUpdate();          // execute the prepared stmt

                  pstmta1.close();   // close the stmt
               }
            }      // end of if user

            if (!user2.equals( "" )) {

               lname = "";
               fname = "";
               mi = "";

               pstmta1 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi " +
                        "FROM member2b WHERE username = ?");

               pstmta1.clearParameters();        // clear the parms
               pstmta1.setString(1, user2);
               rs2 = pstmta1.executeQuery();      // execute the prepared stmt

               if (rs2.next()) {

                  lname = rs2.getString(1);
                  fname = rs2.getString(2);
                  mi = rs2.getString(3);
               }
               pstmta1.close();                  // close the stmt

               if (!lname.equals( "" )) {       // if name found

                  //
                  // save info in teereport4 (temporary)
                  //
                  pstmta1 = con.prepareStatement (
                    "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time, fb) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, lname);
                  pstmta1.setString(2, fname);
                  pstmta1.setString(3, mi);
                  pstmta1.setString(4, user2);
                  pstmta1.setString(5, p2cw);
                  pstmta1.setString(6, mnum2);
                  pstmta1.setShort(7, show2);
                  pstmta1.setString(8, courseNameT);
                  pstmta1.setInt(9, time);
                  pstmta1.setString(10, sfb);
                  pstmta1.executeUpdate();          // execute the prepared stmt

                  pstmta1.close();   // close the stmt
               }
            }      // end of if user

            if (!user3.equals( "" )) {

               lname = "";
               fname = "";
               mi = "";

               pstmta1 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi " +
                        "FROM member2b WHERE username = ?");

               pstmta1.clearParameters();        // clear the parms
               pstmta1.setString(1, user3);
               rs2 = pstmta1.executeQuery();      // execute the prepared stmt

               if (rs2.next()) {

                  lname = rs2.getString(1);
                  fname = rs2.getString(2);
                  mi = rs2.getString(3);
               }
               pstmta1.close();                  // close the stmt

               if (!lname.equals( "" )) {       // if name found

                  //
                  // save info in teereport4 (temporary)
                  //
                  pstmta1 = con.prepareStatement (
                    "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time, fb) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, lname);
                  pstmta1.setString(2, fname);
                  pstmta1.setString(3, mi);
                  pstmta1.setString(4, user3);
                  pstmta1.setString(5, p3cw);
                  pstmta1.setString(6, mnum3);
                  pstmta1.setShort(7, show3);
                  pstmta1.setString(8, courseNameT);
                  pstmta1.setInt(9, time);
                  pstmta1.setString(10, sfb);
                  pstmta1.executeUpdate();          // execute the prepared stmt

                  pstmta1.close();   // close the stmt
               }
            }      // end of if user

            if (!user4.equals( "" )) {

               lname = "";
               fname = "";
               mi = "";

               pstmta1 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi " +
                        "FROM member2b WHERE username = ?");

               pstmta1.clearParameters();        // clear the parms
               pstmta1.setString(1, user4);
               rs2 = pstmta1.executeQuery();      // execute the prepared stmt

               if (rs2.next()) {

                  lname = rs2.getString(1);
                  fname = rs2.getString(2);
                  mi = rs2.getString(3);
               }
               pstmta1.close();                  // close the stmt

               if (!lname.equals( "" )) {       // if name found

                  //
                  // save info in teereport4 (temporary)
                  //
                  pstmta1 = con.prepareStatement (
                    "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time, fb) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, lname);
                  pstmta1.setString(2, fname);
                  pstmta1.setString(3, mi);
                  pstmta1.setString(4, user4);
                  pstmta1.setString(5, p4cw);
                  pstmta1.setString(6, mnum4);
                  pstmta1.setShort(7, show4);
                  pstmta1.setString(8, courseNameT);
                  pstmta1.setInt(9, time);
                  pstmta1.setString(10, sfb);
                  pstmta1.executeUpdate();          // execute the prepared stmt

                  pstmta1.close();   // close the stmt
               }
            }      // end of if user

            if (!user5.equals( "" )) {

               lname = "";
               fname = "";
               mi = "";

               pstmta1 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi " +
                        "FROM member2b WHERE username = ?");

               pstmta1.clearParameters();        // clear the parms
               pstmta1.setString(1, user5);
               rs2 = pstmta1.executeQuery();      // execute the prepared stmt

               if (rs2.next()) {

                  lname = rs2.getString(1);
                  fname = rs2.getString(2);
                  mi = rs2.getString(3);
               }
               pstmta1.close();                  // close the stmt

               if (!lname.equals( "" )) {       // if name found

                  //
                  // save info in teereport4 (temporary)
                  //
                  pstmta1 = con.prepareStatement (
                    "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time, fb) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, lname);
                  pstmta1.setString(2, fname);
                  pstmta1.setString(3, mi);
                  pstmta1.setString(4, user5);
                  pstmta1.setString(5, p5cw);
                  pstmta1.setString(6, mnum5);
                  pstmta1.setShort(7, show5);
                  pstmta1.setString(8, courseNameT);
                  pstmta1.setInt(9, time);
                  pstmta1.setString(10, sfb);
                  pstmta1.executeUpdate();          // execute the prepared stmt

                  pstmta1.close();   // close the stmt
               }
            }      // end of if user

         }      // end of WHILE

         pstmt.close();                  // close the stmt

         //
         //  All members are now saved in teereport4 - list them and their guests alphabetically by mode of trans
         //
         String userT = "";
         String csvShow = "";

         if (club.equals( "blackhawk" ) && order.equals( "trans" )) {

            String bhwk1 = "";
            String bhwk9 = "";

            //
            //  Blackhawk CC - they want a list by mode of trans in the order they specify them in club setup
            //
            for (int blk=0; blk<16; blk++) {         // check all 16 modes of trans

               if (!parmc.tmodea[blk].equals( "" )) {      // if tmode specified

                  bhwk1 = parmc.tmodea[blk];            // get tmode
                  bhwk9 = bhwk1 + "9";                  // get 9-hole version

                  pstmt = con.prepareStatement (
                        "SELECT lname, fname, mi, username, tmode, mNum, checkIn " +
                        "FROM teereport4 WHERE tmode = ? OR tmode = ? " +
                        "ORDER BY lname, fname, mi");

                  pstmt.clearParameters();        // clear the parms
                  pstmt.setString(1, bhwk1);
                  pstmt.setString(2, bhwk9);
                  rs = pstmt.executeQuery();      // execute the prepared pstmt

                  while (rs.next()) {

                     lname = rs.getString(1);
                     fname = rs.getString(2);
                     mi = rs.getString(3);
                     userT = rs.getString(4);
                     tmode = rs.getString(5);
                     mnum1 = rs.getString(6);
                     show1 = rs.getShort(7);

                     StringBuffer mem_name = new StringBuffer(lname);  // get last name

                     mem_name.append(", ");
                     mem_name.append(fname);
                     if (!mi.equals( "" )) {
                        mem_name.append(" ");
                        mem_name.append(mi);
                     }
                     lname = mem_name.toString();                      // convert to one string

                     //
                     //  Determine color to use in table - alternate for each new mode of trans or member
                     //
                     if (order.equals( "trans" )) {

                        currCW = tmode;                         // get this mode of trans

                        if (currCW.endsWith( "9" )) {            // if 9-hole CW

                           currCW = strip9( currCW );            // remove the '9'
                        }

                        if (lastCW.equals( "" )) {     // if first time

                           lastCW = currCW;             // set this as current, use current color

                        } else {

                           if (!currCW.equals( lastCW )) {   // if tmode has changed

                              lastCW = currCW;               // set this as current and switch color

                              if (rowColor.equals( "#F5F5DC" )) {

                                 rowColor = "#CDCDB4";         // switch

                              } else {

                                 rowColor = "#F5F5DC";         // switch
                              }
                           }
                        }

                     } else {   // change per member

                        if (rowColor.equals( "#F5F5DC" )) {

                           rowColor = "#CDCDB4";         // switch

                        } else {

                           rowColor = "#F5F5DC";         // switch
                        }

                     }

                     if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

                        out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"left\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(lname);                               // Member name
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println("n/a");                                // Guest - empty in this row
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(mnum1);                                // Member Number
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(tmode);                                // T Mode
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");

                        //
                        //  Changed for pre-checkin feature support on 02-28-05 [Paul S]
                        //
                        out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                        out.println("</font></td></tr>");       // end of row
                        
                     } else {     // CSV format
                        
                        csvShow = "No";
                        
                        if (show1 == 1) csvShow = "Yes";
                                   
                        // CSV format - output the row
                        out.println(
                                  "\"" + lname + "\"," +     // member name
                                  "\"n/a\"," +               // no guest for this row
                                  "\"" + mnum1 + "\"," +     // member number
                                  "\"" + tmode + "\"," +     // mode of trans
                                  "\"" + csvShow + "\"");    // checked in? 
                     }

                     //
                     //  Now see if there are any guests for this member in the tee sheet
                     //
                     if (courseName1.equals( "-ALL-" )) {

                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)";
                     } else {
                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                                 "AND courseName = ?";
                     }
                     PreparedStatement pstmt2 = con.prepareStatement (stringTee4);

                     pstmt2.clearParameters();        // clear the parms
                     pstmt2.setLong(1, date);         // put the parm in pstmt2
                     pstmt2.setString(2, userT);
                     pstmt2.setString(3, userT);
                     pstmt2.setString(4, userT);
                     pstmt2.setString(5, userT);
                     pstmt2.setString(6, userT);

                     if (!courseName1.equals( "-ALL-" )) {
                        pstmt2.setString(7, courseName1);
                     }
                     rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                     while (rs2.next()) {

                        player1 = rs2.getString(1);
                        player2 = rs2.getString(2);
                        player3 = rs2.getString(3);
                        player4 = rs2.getString(4);
                        p1cw = rs2.getString(5);
                        p2cw = rs2.getString(6);
                        p3cw = rs2.getString(7);
                        p4cw = rs2.getString(8);
                        show1 = rs2.getShort(9);
                        show2 = rs2.getShort(10);
                        show3 = rs2.getShort(11);
                        show4 = rs2.getShort(12);
                        player5 = rs2.getString(13);
                        p5cw = rs2.getString(14);
                        show5 = rs2.getShort(15);
                        user1 = rs2.getString(16);
                        user2 = rs2.getString(17);
                        user3 = rs2.getString(18);
                        user4 = rs2.getString(19);
                        user5 = rs2.getString(20);
                        p91 = rs2.getInt(21);
                        p92 = rs2.getInt(22);
                        p93 = rs2.getInt(23);
                        p94 = rs2.getInt(24);
                        p95 = rs2.getInt(25);

                        if (p91 == 1) {          // if 9 hole round
                           p1cw = p1cw + "9";
                        }
                        if (p92 == 1) {
                           p2cw = p2cw + "9";
                        }
                        if (p93 == 1) {
                           p3cw = p3cw + "9";
                        }
                        if (p94 == 1) {
                           p4cw = p4cw + "9";
                        }
                        if (p95 == 1) {
                           p5cw = p5cw + "9";
                        }

                        //
                        //  Add a row for each guest of this member
                        //
                        if (user1.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player1);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p1cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                              
                           } else {    // CSV format
                              
                              csvShow = "No";

                              if (show1 == 1) csvShow = "Yes";

                              // CSV format - output the row
                              out.println(
                                        "\" \"," +                 // member name - blankfor guests
                                        "\"" + player1 + "\"," +   // guest 
                                        "\"" + mnum1 + "\"," +     // member number
                                        "\"" + p1cw + "\"," +      // mode of trans
                                        "\"" + csvShow + "\"");    // checked in? 
                           }
                        }

                        if (user2.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player2);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p2cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show2 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                              
                           } else {    // CSV format
                              
                              csvShow = "No";

                              if (show2 == 1) csvShow = "Yes";

                              // CSV format - output the row
                              out.println(
                                        "\" \"," +                 // member name - blankfor guests
                                        "\"" + player2 + "\"," +   // guest 
                                        "\"" + mnum1 + "\"," +     // member number
                                        "\"" + p2cw + "\"," +      // mode of trans
                                        "\"" + csvShow + "\"");    // checked in? 
                           }
                        }

                        if (user3.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player3);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p3cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show3 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                              
                           } else {    // CSV format
                              
                              csvShow = "No";

                              if (show3 == 1) csvShow = "Yes";

                              // CSV format - output the row
                              out.println(
                                        "\" \"," +                 // member name - blankfor guests
                                        "\"" + player3 + "\"," +   // guest 
                                        "\"" + mnum1 + "\"," +     // member number
                                        "\"" + p3cw + "\"," +      // mode of trans
                                        "\"" + csvShow + "\"");    // checked in? 
                           }
                        }

                        if (user4.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player4);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p4cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show4 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                              
                           } else {    // CSV format
                              
                              csvShow = "No";

                              if (show4 == 1) csvShow = "Yes";

                              // CSV format - output the row
                              out.println(
                                        "\" \"," +                 // member name - blankfor guests
                                        "\"" + player4 + "\"," +   // guest 
                                        "\"" + mnum1 + "\"," +     // member number
                                        "\"" + p4cw + "\"," +      // mode of trans
                                        "\"" + csvShow + "\"");    // checked in? 
                           }
                        }

                        if (user5.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player5);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p5cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show5 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                              
                           } else {    // CSV format
                              
                              csvShow = "No";

                              if (show5 == 1) csvShow = "Yes";

                              // CSV format - output the row
                              out.println(
                                        "\" \"," +                 // member name - blankfor guests
                                        "\"" + player5 + "\"," +   // guest 
                                        "\"" + mnum1 + "\"," +     // member number
                                        "\"" + p5cw + "\"," +      // mode of trans
                                        "\"" + csvShow + "\"");    // checked in? 
                           }
                        }
                     }               // end of WHILE guests
                     pstmt2.close();

                  }               // end of WHILE members
                  pstmt.close();
               }
            }         // end of FOR loop for Blackhawk

         } else {   // NOT Blackhawk - do normal lists

            if (order.equals( "trans" )) {

               pstmt = con.prepareStatement (
                  "SELECT lname, fname, mi, username, tmode, mNum, checkIn, course, time, fb " +
                  "FROM teereport4 " +
                  "ORDER BY tmode, lname, fname, mi");

            } else {   // order by member only

               pstmt = con.prepareStatement (
                  "SELECT lname, fname, mi, username, tmode, mNum, checkIn, course, time, fb " +
                  "FROM teereport4 " +
                  "ORDER BY lname, fname, mi");
            }

            pstmt.clearParameters();        // clear the parms
            rs = pstmt.executeQuery();      // execute the prepared pstmt

            while (rs.next()) {

               lname = rs.getString(1);
               fname = rs.getString(2);
               mi = rs.getString(3);
               userT = rs.getString(4);
               tmode = rs.getString(5);
               mnum1 = rs.getString(6);
               show1 = rs.getShort(7);
               courseNameT = rs.getString(8);
               time = rs.getInt(9);
               sfb = rs.getString(10);

               StringBuffer mem_name = new StringBuffer(lname);  // get last name

               mem_name.append(", ");
               mem_name.append(fname);
               if (!mi.equals( "" )) {
                  mem_name.append(" ");
                  mem_name.append(mi);
               }
               lname = mem_name.toString();                      // convert to one string

               //
               //  Determine color to use in table - alternate for each new mode of trans
               //
               if (order.equals( "trans" )) {

                  currCW = tmode;                         // get this mode of trans

                  if (currCW.endsWith( "9" )) {            // if 9-hole CW

                     currCW = strip9( currCW );            // remove the '9'
                  }

                  if (lastCW.equals( "" )) {     // if first time

                     lastCW = currCW;             // set this as current, use current color

                  } else {

                     if (!currCW.equals( lastCW )) {   // if tmode has changed

                        lastCW = currCW;               // set this as current and switch color

                        if (rowColor.equals( "#F5F5DC" )) {

                           rowColor = "#CDCDB4";         // switch

                        } else {

                           rowColor = "#F5F5DC";         // switch
                        }
                     }
                  }

               } else {   // change per member

                  if (rowColor.equals( "#F5F5DC" )) {

                     rowColor = "#CDCDB4";         // switch

                  } else {

                     rowColor = "#F5F5DC";         // switch
                  }

               }

               String timeString = "";

               int temp1 = 0;      
               int temp2 = 0;   

               if (time < 99999) {                // if not a shotgun event

                  temp1 = time / 100;             // get hour
                  temp2 = time - (temp1 * 100);   // get min

                  if (temp1 == 12) {

                     timeString = " PM";

                  } else {

                     if (temp1 > 12) {

                        temp1 -= 12;
                        timeString = " PM";

                     } else {

                        timeString = " AM";
                     }
                  }
               }

               if (!csv.equalsIgnoreCase( "yes" )) {                 // if csv format NOT selected

                  out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"left\">");
                  out.println("<font size=\"" +fsz+ "\">");
                  out.println(lname);                               // Member name
                  out.println("</font></td>");


                  if (report_type.equals( "alphat" )) {          // Members & Tee Times report?

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     if (time == 99999) {                           // if a shotgun event
                        out.println("Shotgun");      
                     } else {
                        if (temp2 < 10) {
                           out.println(temp1+ ":0" +temp2+ timeString);        // Time
                        } else {
                           out.println(temp1+ ":" +temp2+ timeString);        // Time
                        }
                     }
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println( sfb );                                                    // fb
                     out.println("</font></td>");

                     if (!courseNameT.equals( "" )) {

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println( courseNameT );                               // Course
                        out.println("</font></td>");
                     }

                     //if (club.equals( "ridgeclub" )) {         // if The Ridge Club - add GHIN # and index

                        String ghinNum = " ";
                        float ghinIndex = -99;

                        if (!userT.equals( "" )) {              // if member - get GHIN info

                           pstmta1 = con.prepareStatement (
                                    "SELECT ghin, g_hancap " +
                                    "FROM member2b WHERE username = ?");

                           pstmta1.clearParameters();        // clear the parms
                           pstmta1.setString(1, userT);
                           rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                           if (rs2.next()) {

                              ghinNum = rs2.getString(1);
                              ghinIndex = rs2.getFloat(2);
                           }
                           pstmta1.close();                  // close the stmt

                        }

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println( ghinNum );                                                    // fb
                        out.println("</font></td>");

                        if (!club.equals("bocawoodscc")) {
                            out.println("<td align=\"center\">");
                            out.println("<font size=\"" +fsz+ "\">");
                            out.println( ghinIndex );                                                    // fb
                            out.println("</font></td>");
                        }

                     //}
                     out.println("</tr>");

                  } else {

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("n/a");                                // Guest - empty in this row
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(mnum1);                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(tmode);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td>");       // end of row

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         out.println("<td align=\"center\">");        // Time
                         out.println("<font size=\"" +fsz+ "\">");
                         if (temp2 < 10) {
                             out.println(temp1+ ":0" +temp2+ timeString);
                         } else {
                             out.println(temp1+ ":" +temp2+ timeString);
                         }
                         out.println("</font></td>");

                         out.println("<td>&nbsp;</td>");             // blank for caddie name
                     }
                     out.println("</tr>");

                     //
                     //  Now see if there are any guests for this member in the tee sheet
                     //
                     if (courseName1.equals( "-ALL-" )) {

                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)";
                     } else {
                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                                 "AND courseName = ?";
                     }
                     PreparedStatement pstmt2 = con.prepareStatement (stringTee4);

                     pstmt2.clearParameters();        // clear the parms
                     pstmt2.setLong(1, date);         // put the parm in pstmt2
                     pstmt2.setString(2, userT);
                     pstmt2.setString(3, userT);
                     pstmt2.setString(4, userT);
                     pstmt2.setString(5, userT);
                     pstmt2.setString(6, userT);

                     if (!courseName1.equals( "-ALL-" )) {
                        pstmt2.setString(7, courseName1);
                     }
                     rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                     while (rs2.next()) {

                        player1 = rs2.getString(1);
                        player2 = rs2.getString(2);
                        player3 = rs2.getString(3);
                        player4 = rs2.getString(4);
                        p1cw = rs2.getString(5);
                        p2cw = rs2.getString(6);
                        p3cw = rs2.getString(7);
                        p4cw = rs2.getString(8);
                        show1 = rs2.getShort(9);
                        show2 = rs2.getShort(10);
                        show3 = rs2.getShort(11);
                        show4 = rs2.getShort(12);
                        player5 = rs2.getString(13);
                        p5cw = rs2.getString(14);
                        show5 = rs2.getShort(15);
                        user1 = rs2.getString(16);
                        user2 = rs2.getString(17);
                        user3 = rs2.getString(18);
                        user4 = rs2.getString(19);
                        user5 = rs2.getString(20);
                        p91 = rs2.getInt(21);
                        p92 = rs2.getInt(22);
                        p93 = rs2.getInt(23);
                        p94 = rs2.getInt(24);
                        p95 = rs2.getInt(25);

                        if (p91 == 1) {          // if 9 hole round
                           p1cw = p1cw + "9";
                        }
                        if (p92 == 1) {
                           p2cw = p2cw + "9";
                        }
                        if (p93 == 1) {
                           p3cw = p3cw + "9";
                        }
                        if (p94 == 1) {
                           p4cw = p4cw + "9";
                        }
                        if (p95 == 1) {
                           p5cw = p5cw + "9";
                        }

                        //
                        //  Add a row for each guest of this member
                        //
                        if (user1.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player1);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p1cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                        }

                        if (user2.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player2);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p2cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show2 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                        }

                        if (user3.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player3);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p3cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show3 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                        }

                        if (user4.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player4);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p4cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show4 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                        }

                        if (user5.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player5);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p5cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show5 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                        }
                     }               // end of WHILE guests
                     pstmt2.close();

                  }                  // end of IF alpha (not alphat)
                  
               } else {
                  
                  //
                  //  CSV format requested
                  //
                  csvRow = lname;        // start row with Member Name
                  
                  if (report_type.equals( "alphat" )) {          // Members & Tee Times report?

                     if (time == 99999) {                           // if a shotgun event
                        csvRow += ", Shotgun";      
                     } else {
                        if (temp2 < 10) {
                           csvRow += ", " +temp1+ ":0" +temp2+ timeString;        // Time
                        } else {
                           csvRow += ", " +temp1+ ":" +temp2+ timeString;       
                        }
                     }

                     csvRow += ", " +sfb;                                      // fb

                     if (!courseNameT.equals( "" )) {

                        csvRow += ", " +courseNameT;                          // Course
                     }

                     //if (club.equals( "ridgeclub" )) {         // if The Ridge Club - add GHIN # and index

                        String ghinNum = " ";
                        float ghinIndex = -99;

                        if (!userT.equals( "" )) {              // if member - get GHIN info

                           pstmta1 = con.prepareStatement (
                                    "SELECT ghin, g_hancap " +
                                    "FROM member2b WHERE username = ?");

                           pstmta1.clearParameters();        // clear the parms
                           pstmta1.setString(1, userT);
                           rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                           if (rs2.next()) {

                              ghinNum = rs2.getString(1);
                              ghinIndex = rs2.getFloat(2);
                           }
                           pstmta1.close();                  // close the stmt

                        }

                        csvRow += ", " +ghinNum;                               // GHIN #

                        csvRow += ", " +ghinIndex;                             // GHIN Index
                     //}

                     out.println(csvRow);                    // Add the row to the file
                     
                  } else {
                     
                     csvShow = "No";
                     if (show1 == 1) csvShow = "Yes";

                     csvRow += ", n/a";                             // Guest - empty in this row
                     csvRow += ", " +mnum1;                          // Member Number
                     csvRow += ", " +tmode;                          // T Mode
                     csvRow += ", " +csvShow;                        // Checked In ?

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         if (temp2 < 10) {
                             csvRow += ", " +temp1+ ":0" +temp2+ timeString;    // Time
                         } else {
                             csvRow += ", " +temp1+ ":" +temp2+ timeString;
                         }
                     }
                     out.println(csvRow);                    // Add the row to the file

                     //
                     //  Now see if there are any guests for this member in the tee sheet
                     //
                     if (courseName1.equals( "-ALL-" )) {

                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)";
                     } else {
                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                                 "AND courseName = ?";
                     }
                     PreparedStatement pstmt2 = con.prepareStatement (stringTee4);

                     pstmt2.clearParameters();        // clear the parms
                     pstmt2.setLong(1, date);         // put the parm in pstmt2
                     pstmt2.setString(2, userT);
                     pstmt2.setString(3, userT);
                     pstmt2.setString(4, userT);
                     pstmt2.setString(5, userT);
                     pstmt2.setString(6, userT);

                     if (!courseName1.equals( "-ALL-" )) {
                        pstmt2.setString(7, courseName1);
                     }
                     rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                     while (rs2.next()) {

                        player1 = rs2.getString(1);
                        player2 = rs2.getString(2);
                        player3 = rs2.getString(3);
                        player4 = rs2.getString(4);
                        p1cw = rs2.getString(5);
                        p2cw = rs2.getString(6);
                        p3cw = rs2.getString(7);
                        p4cw = rs2.getString(8);
                        show1 = rs2.getShort(9);
                        show2 = rs2.getShort(10);
                        show3 = rs2.getShort(11);
                        show4 = rs2.getShort(12);
                        player5 = rs2.getString(13);
                        p5cw = rs2.getString(14);
                        show5 = rs2.getShort(15);
                        user1 = rs2.getString(16);
                        user2 = rs2.getString(17);
                        user3 = rs2.getString(18);
                        user4 = rs2.getString(19);
                        user5 = rs2.getString(20);
                        p91 = rs2.getInt(21);
                        p92 = rs2.getInt(22);
                        p93 = rs2.getInt(23);
                        p94 = rs2.getInt(24);
                        p95 = rs2.getInt(25);

                        if (p91 == 1) {          // if 9 hole round
                           p1cw = p1cw + "9";
                        }
                        if (p92 == 1) {
                           p2cw = p2cw + "9";
                        }
                        if (p93 == 1) {
                           p3cw = p3cw + "9";
                        }
                        if (p94 == 1) {
                           p4cw = p4cw + "9";
                        }
                        if (p95 == 1) {
                           p5cw = p5cw + "9";
                        }

                        //
                        //  Add a row for each guest of this member
                        //
                        if (user1.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above
                           
                           csvShow = "No";
                           if (show1 == 1) csvShow = "Yes";

                           csvRow = " ," +player1;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p1cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }

                        if (user2.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           csvShow = "No";
                           if (show2 == 1) csvShow = "Yes";

                           csvRow = " ," +player2;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p2cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }

                        if (user3.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           csvShow = "No";
                           if (show3 == 1) csvShow = "Yes";

                           csvRow = " ," +player3;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p3cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }

                        if (user4.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           csvShow = "No";
                           if (show4 == 1) csvShow = "Yes";

                           csvRow = " ," +player4;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p4cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }

                        if (user5.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           csvShow = "No";
                           if (show5 == 1) csvShow = "Yes";

                           csvRow = " ," +player5;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p5cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }
                     }               // end of WHILE guests
                     pstmt2.close();

                  }                  // end of IF alpha (not alphat)
                  
               }            // end of IF CSV format

            }               // end of WHILE members
            pstmt.close();
         }                    // end of IF Blackhawk

         //
         //  Delete the entries from the temporary table
         //
         pstmt = con.prepareStatement (
            "DELETE FROM teereport4");

         pstmt.clearParameters();        // clear the parms
         pstmt.executeUpdate();          // execute the prepared pstmt

         pstmt.close();

         if (report_type.equals( "alpha" )) {

            //
            //  Now check for Unaccompanied Guests (first, list those that have a member sponsor)
            //
            boolean foundSome = false;

            if (courseName1.equals( "-ALL-" ) || courseName1.equals( "" )) {

               stringTee4 = "SELECT " +
                        "userg1, userg2, userg3, userg4, userg5 " +
                        "FROM teecurr2 WHERE date = ? " +
                        "AND (username1 = ? AND username2 = ? AND username3 = ? AND username4 = ? AND username5 = ?) " +
                        "AND (userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?)";
            } else {
               stringTee4 = "SELECT " +
                        "userg1, userg2, userg3, userg4, userg5 " +
                        "FROM teecurr2 WHERE date = ? " +
                        "AND (username1 = ? AND username2 = ? AND username3 = ? AND username4 = ? AND username5 = ?) " +
                        "AND (userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?) " +
                        "AND courseName = ?";
            }
            pstmt = con.prepareStatement (stringTee4);

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt
            pstmt.setString(2, omit);
            pstmt.setString(3, omit);
            pstmt.setString(4, omit);
            pstmt.setString(5, omit);
            pstmt.setString(6, omit);
            pstmt.setString(7, omit);
            pstmt.setString(8, omit);
            pstmt.setString(9, omit);
            pstmt.setString(10, omit);
            pstmt.setString(11, omit);

            if (!courseName1.equals( "-ALL-" ) && !courseName1.equals( "" )) {
               pstmt.setString(12, courseName1);
            }
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               userg1 = rs.getString(1);
               userg2 = rs.getString(2);
               userg3 = rs.getString(3);
               userg4 = rs.getString(4);
               userg5 = rs.getString(5);

               //
               //  Save each member's info for later
               //
               if (!userg1.equals( "" )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT lname " +
                           "FROM teereport4 WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, userg1);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                  }
                  pstmta1.close();                  // close the stmt

                  if (lname.equals( "" )) {        // if user not already saved

                     pstmta1 = con.prepareStatement (
                              "SELECT name_last, name_first, name_mi, memNum " +
                              "FROM member2b WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg1);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                        fname = rs2.getString(2);
                        mi = rs2.getString(3);
                        mnum1 = rs2.getString(4);
                     }
                     pstmta1.close();                  // close the stmt

                     if (!lname.equals( "" )) {       // if name found

                        //
                        // save info in teereport4 (temporary)
                        //
                        pstmta1 = con.prepareStatement (
                          "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                          "VALUES (?,?,?,?,'',?,0,'',0)");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, lname);
                        pstmta1.setString(2, fname);
                        pstmta1.setString(3, mi);
                        pstmta1.setString(4, userg1);
                        pstmta1.setString(5, mnum1);
                        pstmta1.executeUpdate();          // execute the prepared stmt

                        pstmta1.close();   // close the stmt

                        foundSome = true;                  // found at least one
                     }
                  }
               }      // end of if userg

               if (!userg2.equals( "" ) && !userg2.equalsIgnoreCase( userg1 )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT lname " +
                           "FROM teereport4 WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, userg2);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                  }
                  pstmta1.close();                  // close the stmt

                  if (lname.equals( "" )) {        // if user not already saved

                     pstmta1 = con.prepareStatement (
                              "SELECT name_last, name_first, name_mi, memNum " +
                              "FROM member2b WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg2);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                        fname = rs2.getString(2);
                        mi = rs2.getString(3);
                        mnum2 = rs2.getString(4);
                     }
                     pstmta1.close();                  // close the stmt

                     if (!lname.equals( "" )) {       // if name found

                        //
                        // save info in teereport4 (temporary)
                        //
                        pstmta1 = con.prepareStatement (
                          "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                          "VALUES (?,?,?,?,'',?,0,'',0)");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, lname);
                        pstmta1.setString(2, fname);
                        pstmta1.setString(3, mi);
                        pstmta1.setString(4, userg2);
                        pstmta1.setString(5, mnum2);
                        pstmta1.executeUpdate();          // execute the prepared stmt

                        pstmta1.close();   // close the stmt

                        foundSome = true;                  // found at least one
                     }
                  }
               }      // end of if userg

               if (!userg3.equals( "" ) && !userg3.equalsIgnoreCase( userg1 ) && !userg3.equalsIgnoreCase( userg2 )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT lname " +
                           "FROM teereport4 WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, userg3);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                  }
                  pstmta1.close();                  // close the stmt

                  if (lname.equals( "" )) {        // if user not already saved

                     pstmta1 = con.prepareStatement (
                              "SELECT name_last, name_first, name_mi, memNum " +
                              "FROM member2b WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg3);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                        fname = rs2.getString(2);
                        mi = rs2.getString(3);
                        mnum3 = rs2.getString(4);
                     }
                     pstmta1.close();                  // close the stmt

                     if (!lname.equals( "" )) {       // if name found

                        //
                        // save info in teereport4 (temporary)
                        //
                        pstmta1 = con.prepareStatement (
                          "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                          "VALUES (?,?,?,?,'',?,0,'',0)");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, lname);
                        pstmta1.setString(2, fname);
                        pstmta1.setString(3, mi);
                        pstmta1.setString(4, userg3);
                        pstmta1.setString(5, mnum3);
                        pstmta1.executeUpdate();          // execute the prepared stmt

                        pstmta1.close();   // close the stmt

                        foundSome = true;                  // found at least one
                     }
                  }
               }      // end of if userg

               if (!userg4.equals( "" ) && !userg4.equalsIgnoreCase( userg1 ) && !userg4.equalsIgnoreCase( userg2 ) &&
                   !userg4.equalsIgnoreCase( userg3 )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT lname " +
                           "FROM teereport4 WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, userg4);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                  }
                  pstmta1.close();                  // close the stmt

                  if (lname.equals( "" )) {        // if user not already saved

                     pstmta1 = con.prepareStatement (
                              "SELECT name_last, name_first, name_mi, memNum " +
                              "FROM member2b WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg4);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                        fname = rs2.getString(2);
                        mi = rs2.getString(3);
                        mnum4 = rs2.getString(4);
                     }
                     pstmta1.close();                  // close the stmt

                     if (!lname.equals( "" )) {       // if name found

                        //
                        // save info in teereport4 (temporary)
                        //
                        pstmta1 = con.prepareStatement (
                          "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                          "VALUES (?,?,?,?,'',?,0,'',0)");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, lname);
                        pstmta1.setString(2, fname);
                        pstmta1.setString(3, mi);
                        pstmta1.setString(4, userg4);
                        pstmta1.setString(5, mnum4);
                        pstmta1.executeUpdate();          // execute the prepared stmt

                        pstmta1.close();   // close the stmt

                        foundSome = true;                  // found at least one
                     }
                  }
               }      // end of if userg

               if (!userg5.equals( "" ) && !userg5.equalsIgnoreCase( userg1 ) && !userg5.equalsIgnoreCase( userg2 ) &&
                   !userg5.equalsIgnoreCase( userg3 ) && !userg5.equalsIgnoreCase( userg4 )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT lname " +
                           "FROM teereport4 WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, userg5);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                  }
                  pstmta1.close();                  // close the stmt

                  if (lname.equals( "" )) {        // if user not already saved

                     pstmta1 = con.prepareStatement (
                              "SELECT name_last, name_first, name_mi, memNum " +
                              "FROM member2b WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg5);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                        fname = rs2.getString(2);
                        mi = rs2.getString(3);
                        mnum5 = rs2.getString(4);
                     }
                     pstmta1.close();                  // close the stmt

                     if (!lname.equals( "" )) {       // if name found

                        //
                        // save info in teereport4 (temporary)
                        //
                        pstmta1 = con.prepareStatement (
                          "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                          "VALUES (?,?,?,?,'',?,0,'',0)");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, lname);
                        pstmta1.setString(2, fname);
                        pstmta1.setString(3, mi);
                        pstmta1.setString(4, userg5);
                        pstmta1.setString(5, mnum5);
                        pstmta1.executeUpdate();          // execute the prepared stmt

                        pstmta1.close();   // close the stmt

                        foundSome = true;                  // found at least one
                     }
                  }
               }      // end of if user

            }      // end of WHILE

            pstmt.close();                  // close the stmt

            if (foundSome == true) {            // if we found some unaccompanied guest times above
               
               csvRow = "";

               if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                  //
                  //  add a header row
                  //
                  out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"5\">");
                  out.println("<font color=\"#FFFFFF\" size=\"" +fsz+ "\">");
                  out.println("Unaccompanied Guests");
                  out.println("</font></td></tr>");       // end of row
               }


               //
               //  All members are now saved in teereport4 - list them and their guests alphabetically by mode of trans
               //
               userT = "";

               pstmt = con.prepareStatement (
                  "SELECT lname, fname, mi, username, mNum " +
                  "FROM teereport4 " +
                  "ORDER BY lname, fname, mi");

               pstmt.clearParameters();        // clear the parms
               rs = pstmt.executeQuery();      // execute the prepared pstmt

               while (rs.next()) {

                  lname = rs.getString(1);
                  fname = rs.getString(2);
                  mi = rs.getString(3);
                  userT = rs.getString(4);
                  mnum1 = rs.getString(5);

                  StringBuffer mem_name = new StringBuffer(lname);  // get last name

                  mem_name.append(", ");
                  mem_name.append(fname);
                  if (!mi.equals( "" )) {
                     mem_name.append(" ");
                     mem_name.append(mi);
                  }
                  lname = mem_name.toString();                      // convert to one string

                  //
                  //  Determine color to use in table - alternate
                  //
                  if (rowColor.equals( "#F5F5DC" )) {

                     rowColor = "#CDCDB4";         // switch

                  } else {

                     rowColor = "#F5F5DC";         // switch
                  }

                  if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"left\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(lname);                               // Member name
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("n/a");                                // Guest - empty in this row
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(mnum1);                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");       // end of row
   /*
                     if (club.equals("fortwayne")) {

                         out.println("<td align=\"center\">");        // Time
                         out.println("<font size=\"" +fsz+ "\">");
                         if (temp2 < 10) {
                             out.println(temp1+ ":0" +temp2+ timeString);
                         } else {
                             out.println(temp1+ ":" +temp2+ timeString);
                         }
                         out.println("</font></td>");

                         out.println("<td>&nbsp;</td>");             // blank for caddie name
                     }
   */
                     out.println("</tr>");       // end of row
                     
                  } else {       // CSV format
                     
                     // CSV format - output the row
                     out.println(
                               "\"" + lname + "\",n/a," +   // Member name, no guest 
                               "\"" + mnum1 + "\", , ");     // member number, no mode of trans, not check in
                  }
                  
                  //
                  //  Now see if there are any guests for this member in the tee sheet
                  //
                  if (courseName1.equals( "-ALL-" )) {

                     stringTee4 = "SELECT " +
                              "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                              "show1, show2, show3, show4, " +
                              "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                              "p91, p92, p93, p94, p95, time " +
                              "FROM teecurr2 WHERE date = ? AND " +
                              "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)";
                  } else {
                     stringTee4 = "SELECT " +
                              "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                              "show1, show2, show3, show4, " +
                              "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                              "p91, p92, p93, p94, p95, time " +
                              "FROM teecurr2 WHERE date = ? AND " +
                              "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                              "AND courseName = ?";
                  }
                  PreparedStatement pstmt2 = con.prepareStatement (stringTee4);

                  pstmt2.clearParameters();        // clear the parms
                  pstmt2.setLong(1, date);         // put the parm in pstmt2
                  pstmt2.setString(2, userT);
                  pstmt2.setString(3, userT);
                  pstmt2.setString(4, userT);
                  pstmt2.setString(5, userT);
                  pstmt2.setString(6, userT);

                  if (!courseName1.equals( "-ALL-" )) {
                     pstmt2.setString(7, courseName1);
                  }
                  rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                  while (rs2.next()) {

                     player1 = rs2.getString(1);
                     player2 = rs2.getString(2);
                     player3 = rs2.getString(3);
                     player4 = rs2.getString(4);
                     p1cw = rs2.getString(5);
                     p2cw = rs2.getString(6);
                     p3cw = rs2.getString(7);
                     p4cw = rs2.getString(8);
                     show1 = rs2.getShort(9);
                     show2 = rs2.getShort(10);
                     show3 = rs2.getShort(11);
                     show4 = rs2.getShort(12);
                     player5 = rs2.getString(13);
                     p5cw = rs2.getString(14);
                     show5 = rs2.getShort(15);
                     user1 = rs2.getString(16);
                     user2 = rs2.getString(17);
                     user3 = rs2.getString(18);
                     user4 = rs2.getString(19);
                     user5 = rs2.getString(20);
                     p91 = rs2.getInt(21);
                     p92 = rs2.getInt(22);
                     p93 = rs2.getInt(23);
                     p94 = rs2.getInt(24);
                     p95 = rs2.getInt(25);
                     time = rs2.getInt(26);
                     
                      String timeString = "";

                      int temp1 = 0;      
                      int temp2 = 0;   

                      if (time < 99999) {                // if not a shotgun event

                         temp1 = time / 100;             // get hour
                         temp2 = time - (temp1 * 100);   // get min

                         if (temp1 == 12) {

                            timeString = " PM";

                         } else {

                            if (temp1 > 12) {

                               temp1 -= 12;
                               timeString = " PM";

                            } else {

                               timeString = " AM";
                            }
                         }
                      }
                  
                     if (p91 == 1) {          // if 9 hole round
                        p1cw = p1cw + "9";
                     }
                     if (p92 == 1) {
                        p2cw = p2cw + "9";
                     }
                     if (p93 == 1) {
                        p3cw = p3cw + "9";
                     }
                     if (p94 == 1) {
                        p4cw = p4cw + "9";
                     }
                     if (p95 == 1) {
                        p5cw = p5cw + "9";
                     }

                     //
                     //  Add a row for each guest of this member
                     //
                     if (user1.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                        if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player1);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p1cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                        
                        } else {
                           
                           // CSV format - output the row
                           csvShow = "No";
                        
                           if (show1 == 1) csvShow = "Yes";
                                   
                           csvRow = " ," +player1;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +tmode;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }
                     }

                     if (user2.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                        if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player2);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p2cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show2 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                        
                        } else {
                           
                           // CSV format - output the row
                           csvShow = "No";
                        
                           if (show2 == 1) csvShow = "Yes";
                                   
                           csvRow = " ," +player2;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p2cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }
                     }

                     if (user3.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                        if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player3);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p3cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show3 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                        
                        } else {
                           
                           // CSV format - output the row
                           csvShow = "No";
                        
                           if (show3 == 1) csvShow = "Yes";
                                   
                           csvRow = " ," +player3;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p3cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }
                     }

                     if (user4.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                        if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player4);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p4cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show4 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td>");       // end of row

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               out.println("<td align=\"center\">");        // Time
                               out.println("<font size=\"" +fsz+ "\">");
                               if (temp2 < 10) {
                                   out.println(temp1+ ":0" +temp2+ timeString);
                               } else {
                                   out.println(temp1+ ":" +temp2+ timeString);
                               }
                               out.println("</font></td>");

                               out.println("<td>&nbsp;</td>");             // blank for caddie name
                           }
                           out.println("</tr>");       // end of row
                       
                        } else {
                           
                           // CSV format - output the row
                           csvShow = "No";
                        
                           if (show4 == 1) csvShow = "Yes";
                                   
                           csvRow = " ," +player4;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p4cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }
                     }

                     if (user5.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                        if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                        out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println("''");                               // Member name - blank for guests
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(player5);                                // Guest
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(mnum1);                                // Member Number
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(p5cw);                                // T Mode
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println((show5 == 1) ? "Yes" : "No");         // Checked In ?
                        out.println("</font></td>");       // end of row
                        
                        if (club.equals("fortwayne") && order.equals( "trans" )) {

                            out.println("<td align=\"center\">");        // Time
                            out.println("<font size=\"" +fsz+ "\">");
                            if (temp2 < 10) {
                                out.println(temp1+ ":0" +temp2+ timeString);
                            } else {
                                out.println(temp1+ ":" +temp2+ timeString);
                            }
                            out.println("</font></td>");

                            out.println("<td>&nbsp;</td>");             // blank for caddie name
                        }
                        out.println("</tr>");       // end of row
                       
                        } else {
                           
                           // CSV format - output the row
                           csvShow = "No";
                        
                           if (show5 == 1) csvShow = "Yes";
                                   
                           csvRow = " ," +player5;         // empty Member Name, then Guest

                           csvRow += " ," +mnum1;                                // Member Number
                           csvRow += " ," +p5cw;                                 // T Mode
                           csvRow += " ," +csvShow;                              // checked in ?

                           if (club.equals("fortwayne") && order.equals( "trans" )) {

                               if (temp2 < 10) {
                                   csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                               } else {
                                   csvRow += " ," +temp1+ ":" +temp2+ timeString;
                               }
                           }

                           out.println(csvRow);                    // Add the row to the file
                        }
                     }
                  }               // end of WHILE guests
                  pstmt2.close();

               }               // end of WHILE members
               pstmt.close();

            }                  // end of IF foundSome

            //
            //  Delete the entries from the temporary table
            //
            pstmt = con.prepareStatement (
               "DELETE FROM teereport4");

            pstmt.clearParameters();        // clear the parms
            pstmt.executeUpdate();          // execute the prepared pstmt

            pstmt.close();


            //
            //  Now check for Unaccompanied Guests that have no sponsor member
            //
            foundSome = false;        // init flag

            if (courseName1.equals( "-ALL-" ) || courseName1.equals( "" )) {

               stringTee4 = "SELECT " +
                        "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                        "show1, show2, show3, show4, player5, p5cw, show5, " +
                        "p91, p92, p93, p94, p95, time " +
                        "FROM teecurr2 WHERE date = ? " +
                        "AND (player1 != ? OR player2 != ? OR player3 != ? OR player4 != ? OR player5 != ?) " +
                        "AND (username1 = ? AND username2 = ? AND username3 = ? AND username4 = ? AND username5 = ?) " +
                        "AND (userg1 = ? AND userg2 = ? AND userg3 = ? AND userg4 = ? AND userg5 = ?)";
            } else {
               stringTee4 = "SELECT " +
                        "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                        "show1, show2, show3, show4, player5, p5cw, show5, " +
                        "p91, p92, p93, p94, p95, time " +
                        "FROM teecurr2 WHERE date = ? " +
                        "AND (player1 != ? OR player2 != ? OR player3 != ? OR player4 != ? OR player5 != ?) " +
                        "AND (username1 = ? AND username2 = ? AND username3 = ? AND username4 = ? AND username5 = ?) " +
                        "AND (userg1 = ? AND userg2 = ? AND userg3 = ? AND userg4 = ? AND userg5 = ?) " +
                        "AND courseName = ?";
            }
            pstmt = con.prepareStatement (stringTee4);

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt
            pstmt.setString(2, omit);
            pstmt.setString(3, omit);
            pstmt.setString(4, omit);
            pstmt.setString(5, omit);
            pstmt.setString(6, omit);
            pstmt.setString(7, omit);
            pstmt.setString(8, omit);
            pstmt.setString(9, omit);
            pstmt.setString(10, omit);
            pstmt.setString(11, omit);
            pstmt.setString(12, omit);
            pstmt.setString(13, omit);
            pstmt.setString(14, omit);
            pstmt.setString(15, omit);
            pstmt.setString(16, omit);

            if (!courseName1.equals( "-ALL-" ) && !courseName1.equals( "" )) {
               pstmt.setString(17, courseName1);
            }
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               player1 = rs.getString(1);
               player2 = rs.getString(2);
               player3 = rs.getString(3);
               player4 = rs.getString(4);
               p1cw = rs.getString(5);
               p2cw = rs.getString(6);
               p3cw = rs.getString(7);
               p4cw = rs.getString(8);
               show1 = rs.getShort(9);
               show2 = rs.getShort(10);
               show3 = rs.getShort(11);
               show4 = rs.getShort(12);
               player5 = rs.getString(13);
               p5cw = rs.getString(14);
               show5 = rs.getShort(15);
               p91 = rs.getInt(16);
               p92 = rs.getInt(17);
               p93 = rs.getInt(18);
               p94 = rs.getInt(19);
               p95 = rs.getInt(20);
               time = rs.getInt(21);

              String timeString = "";

              int temp1 = 0;      
              int temp2 = 0;   

              if (time < 99999) {                // if not a shotgun event

                 temp1 = time / 100;             // get hour
                 temp2 = time - (temp1 * 100);   // get min

                 if (temp1 == 12) {

                    timeString = " PM";

                 } else {

                    if (temp1 > 12) {

                       temp1 -= 12;
                       timeString = " PM";

                    } else {

                       timeString = " AM";
                    }
                 }
              }
                  
               if (p91 == 1) {          // if 9 hole round
                  p1cw = p1cw + "9";
               }
               if (p92 == 1) {
                  p2cw = p2cw + "9";
               }
               if (p93 == 1) {
                  p3cw = p3cw + "9";
               }
               if (p94 == 1) {
                  p4cw = p4cw + "9";
               }
               if (p95 == 1) {
                  p5cw = p5cw + "9";
               }

               if (foundSome == false) {            // if we haven't found some unaccompanied guest times above or here yet

                  if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                     //
                     //  add a header row
                     //
                     out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\""+ ((club.equals("fortwayne") && order.equals("trans")) ? "7" : "5") +"\">");
                     out.println("<font color=\"#FFFFFF\" size=\"" +fsz+ "\">");
                     out.println("Non-Sponsored Guests");
                     out.println("</font></td></tr>");       // end of row
                  }

                  foundSome = true;
               }

               //
               //  Determine color to use in table - alternate
               //
               if (rowColor.equals( "#F5F5DC" )) {

                  rowColor = "#CDCDB4";         // switch

               } else {

                  rowColor = "#F5F5DC";         // switch
               }


               //
               //  Add a row for each unsponsored guest
               //
               if (!player1.equals( "" )) {        // if guest exists

                  if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player1);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p1cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td>");       // end of row

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         out.println("<td align=\"center\">");        // Time
                         out.println("<font size=\"" +fsz+ "\">");
                         if (temp2 < 10) {
                             out.println(temp1+ ":0" +temp2+ timeString);
                         } else {
                             out.println(temp1+ ":" +temp2+ timeString);
                         }
                         out.println("</font></td>");

                         out.println("<td>&nbsp;</td>");             // blank for caddie name
                     }
                     out.println("</tr>");       // end of row
                       
                  } else {

                     // CSV format - output the row
                     csvShow = "No";

                     if (show1 == 1) csvShow = "Yes";

                     csvRow = " ," +player1+ " ," +p1cw;         // empty Member Name, then Guest, no mNum, then mode of trans
                     csvRow += "," +csvShow;                     // checked in ?

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         if (temp2 < 10) {
                             csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                         } else {
                             csvRow += " ," +temp1+ ":" +temp2+ timeString;
                         }
                     }
                     out.println(csvRow);                    // Add the row to the file
                  }
               }

               if (!player2.equals( "" )) {

                  if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player2);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p2cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show2 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td>");       // end of row

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         out.println("<td align=\"center\">");        // Time
                         out.println("<font size=\"" +fsz+ "\">");
                         if (temp2 < 10) {
                             out.println(temp1+ ":0" +temp2+ timeString);
                         } else {
                             out.println(temp1+ ":" +temp2+ timeString);
                         }
                         out.println("</font></td>");

                         out.println("<td>&nbsp;</td>");             // blank for caddie name
                     }
                     out.println("</tr>");       // end of row
                       
                  } else {

                     // CSV format - output the row
                     csvShow = "No";

                     if (show2 == 1) csvShow = "Yes";

                     csvRow = " ," +player2+ " ," +p2cw;         // empty Member Name, then Guest, no mNum, then mode of trans
                     csvRow += "," +csvShow;                     // checked in ?

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         if (temp2 < 10) {
                             csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                         } else {
                             csvRow += " ," +temp1+ ":" +temp2+ timeString;
                         }
                     }
                     out.println(csvRow);                    // Add the row to the file
                  }
               }

               if (!player3.equals( "" )) {

                  if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player3);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p3cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show3 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td>");       // end of row

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         out.println("<td align=\"center\">");        // Time
                         out.println("<font size=\"" +fsz+ "\">");
                         if (temp2 < 10) {
                             out.println(temp1+ ":0" +temp2+ timeString);
                         } else {
                             out.println(temp1+ ":" +temp2+ timeString);
                         }
                         out.println("</font></td>");

                         out.println("<td>&nbsp;</td>");             // blank for caddie name
                     }
                     out.println("</tr>");       // end of row
                       
                  } else {

                     // CSV format - output the row
                     csvShow = "No";

                     if (show3 == 1) csvShow = "Yes";

                     csvRow = " ," +player3+ " ," +p3cw;         // empty Member Name, then Guest, no mNum, then mode of trans
                     csvRow += "," +csvShow;                     // checked in ?

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         if (temp2 < 10) {
                             csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                         } else {
                             csvRow += " ," +temp1+ ":" +temp2+ timeString;
                         }
                     }
                     out.println(csvRow);                    // Add the row to the file
                  }
               }

               if (!player4.equals( "" )) {

                  if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player4);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p4cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show4 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td>");       // end of row

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         out.println("<td align=\"center\">");        // Time
                         out.println("<font size=\"" +fsz+ "\">");
                         if (temp2 < 10) {
                             out.println(temp1+ ":0" +temp2+ timeString);
                         } else {
                             out.println(temp1+ ":" +temp2+ timeString);
                         }
                         out.println("</font></td>");

                         out.println("<td>&nbsp;</td>");             // blank for caddie name
                     }
                     out.println("</tr>");       // end of row
                       
                  } else {

                     // CSV format - output the row
                     csvShow = "No";

                     if (show4 == 1) csvShow = "Yes";

                     csvRow = " ," +player4+ " ," +p4cw;         // empty Member Name, then Guest, no mNum, then mode of trans
                     csvRow += "," +csvShow;                     // checked in ?

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         if (temp2 < 10) {
                             csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                         } else {
                             csvRow += " ," +temp1+ ":" +temp2+ timeString;
                         }
                     }
                     out.println(csvRow);                    // Add the row to the file
                  }
               }

               if (!player5.equals( "" )) {

                  if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player5);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p5cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show5 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td>");       // end of row

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         out.println("<td align=\"center\">");        // Time
                         out.println("<font size=\"" +fsz+ "\">");
                         if (temp2 < 10) {
                             out.println(temp1+ ":0" +temp2+ timeString);
                         } else {
                             out.println(temp1+ ":" +temp2+ timeString);
                         }
                         out.println("</font></td>");

                         out.println("<td>&nbsp;</td>");             // blank for caddie name
                     }
                     out.println("</tr>");       // end of row
                       
                  } else {

                     // CSV format - output the row
                     csvShow = "No";

                     if (show5 == 1) csvShow = "Yes";

                     csvRow = " ," +player5+ " ," +p5cw;         // empty Member Name, then Guest, no mNum, then mode of trans
                     csvRow += "," +csvShow;                     // checked in ?

                     if (club.equals("fortwayne") && order.equals( "trans" )) {

                         if (temp2 < 10) {
                             csvRow += " ," +temp1+ ":0" +temp2+ timeString;   // Time
                         } else {
                             csvRow += " ," +temp1+ ":" +temp2+ timeString;
                         }
                     }
                     out.println(csvRow);                    // Add the row to the file
                  }
               }

            }               // end of WHILE unsponsored guest tee times
            pstmt.close();

         }                  // end of if alpha (not alphat)

         if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
            out.println("</table>");                  // end of alphabetical list page
         }

      }  // end of report type if

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>" + e1.toString());
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }

   //
   //  End of HTML page
   //
   if (!csv.equalsIgnoreCase("Yes")) {       // if NOT csv format
                  
      out.println("</center></body></html>");
   }
   
   out.close();
   if (Gzip == true) {
      resp.setContentLength(buf.size());                 // set output length
      resp.getOutputStream().write(buf.toByteArray());
   }

 }  // end of doPost


 // *********************************************************
 //  Display Notes Report in new pop-up window
 // *********************************************************

 private static void notesReport(long date, String course, PrintWriter out, Connection con) {

   ResultSet rs = null;
   PreparedStatement pstmt = null;

   String fbs = "";
   String notes = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String ampm = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String courseNameT = "";

   int hr = 0;
   int min = 0;
   int fb = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;

   //
   //  Display all tee times that contain notes
   //
   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
   out.println("<tr bgcolor=\"#336633\">");
   out.println("<td align=\"left\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>Time</b></u> ");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>F/B</b></u> ");
   out.println("</font></td>");
   if (course.equals( "-ALL-" )) {
      out.println("<td align=\"left\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<u><b>Course</b></u> ");
      out.println("</font></td>");
   }
   out.println("<td align=\"left\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>Players</b></u> ");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>C/W</b></u> ");
   out.println("</font></td>");
   if (course.equals( "-ALL-" )) {
      out.println("<td align=\"left\" width=\"300\">");
   } else {
      out.println("<td align=\"left\" width=\"380\">");
   }
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>Notes</b></u> ");
   out.println("</font></td>");
   out.println("</tr>");

   try {

      //
      //  Get the tee sheet for this date
      //
      String stringTee2 = "";

      if (course.equals( "-ALL-" )) {

         stringTee2 = "SELECT " +
                  "hr, min, player1, player2, player3, player4, " +
                  "p1cw, p2cw, p3cw, p4cw, " +
                  "fb, player5, p5cw, notes, courseName, " +
                  "p91, p92, p93, p94, p95 " +
                  "FROM teecurr2 WHERE date = ? AND notes != '' ORDER BY time, courseName, fb";
      } else {
         
         stringTee2 = "SELECT " +
                  "hr, min, player1, player2, player3, player4, " +
                  "p1cw, p2cw, p3cw, p4cw, " +
                  "fb, player5, p5cw, notes, courseName, " +
                  "p91, p92, p93, p94, p95 " +
                  "FROM teecurr2 WHERE date = ? AND courseName = ? AND notes != '' ORDER BY time, fb";
      }
        
      pstmt = con.prepareStatement (stringTee2);

      pstmt.clearParameters();
      pstmt.setLong(1, date);

      if (!course.equals( "-ALL-" )) {
         pstmt.setString(2, course);
      }
      rs = pstmt.executeQuery();

      while (rs.next()) {

         hr = rs.getInt(1);
         min = rs.getInt(2);
         player1 = rs.getString(3);
         player2 = rs.getString(4);
         player3 = rs.getString(5);
         player4 = rs.getString(6);
         p1cw = rs.getString(7);
         p2cw = rs.getString(8);
         p3cw = rs.getString(9);
         p4cw = rs.getString(10);
         fb = rs.getInt(11);
         player5 = rs.getString(12);
         p5cw = rs.getString(13);
         notes = rs.getString(14);
         courseNameT = rs.getString(15);
         p91 = rs.getInt(16);
         p92 = rs.getInt(17);
         p93 = rs.getInt(18);
         p94 = rs.getInt(19);
         p95 = rs.getInt(20);

         if (!notes.equals( "" )) {        // if notes

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            if (p91 == 1) {          // if 9 hole round
               p1cw = p1cw + "9";
            }
            if (p92 == 1) {
               p2cw = p2cw + "9";
            }
            if (p93 == 1) {
               p3cw = p3cw + "9";
            }
            if (p94 == 1) {
               p4cw = p4cw + "9";
            }
            if (p95 == 1) {
               p5cw = p5cw + "9";
            }

            if (player1.equals("")) {
               p1cw = "";
            }
            if (player2.equals("")) {
               p2cw = "";
            }
            if (player3.equals("")) {
               p3cw = "";
            }
            if (player4.equals("")) {
               p4cw = "";
            }
            if (player5.equals("")) {
               p5cw = "";
            }

            fbs = "F";

            if (fb == 1) {

               fbs = "B";
            }

            //
            //  Display the tee time info with notes
            //
            out.println("<tr>");
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println(hr + ":" + Utilities.ensureDoubleDigit(min) + ampm);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(fbs);
            out.println("</font></td>");
            if (course.equals( "-ALL-" )) {
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(courseNameT);
               out.println("</font></td>");
            }
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            if (!player1.equals( "" )) {
               out.println(player1+ "<br>");
            }
            if (!player2.equals( "" )) {
               out.println(player2+ "<br>");
            }
            if (!player3.equals( "" )) {
               out.println(player3+ "<br>");
            }
            if (!player4.equals( "" )) {
               out.println(player4+ "<br>");
            }
            if (!player5.equals( "" )) {
               out.println(player5+ "<br>");
            }
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (!player1.equals( "" )) {
               out.println(p1cw+ "<br>");
            }
            if (!player2.equals( "" )) {
               out.println(p2cw+ "<br>");
            }
            if (!player3.equals( "" )) {
               out.println(p3cw+ "<br>");
            }
            if (!player4.equals( "" )) {
               out.println(p4cw+ "<br>");
            }
            if (!player5.equals( "" )) {
               out.println(p5cw+ "<br>");
            }
            out.println("</font></td>");
            if (course.equals( "-ALL-" )) {
               out.println("<td align=\"left\" width=\"300\">");
            } else {
               out.println("<td align=\"left\" width=\"380\">");
            }
            out.println("<font size=\"2\">");
            out.println(notes);
            out.println("</font></td></tr>");
         }

      } // end of WHILE tee times

      out.println("</table>");              // end of player table
      out.println("</center></font></body></html>");

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    out.close();
   
 }    // end of notesReport


 
 // *********************************************************
 //  Display the Quick View Tee Time Tables for one Course
 // *********************************************************

 private static void displayCourse(long date, String course, boolean oldsheets, PrintWriter out, Connection con) {

   ResultSet rs = null;
   PreparedStatement pstmt = null;

   String ampm = "";

   int fives = 0;
   int groupsize = 4;      // default to 4-somes only
   
   
   try {

       //
       //  Get the 5-some indicator for club
       //
       pstmt = con.prepareStatement (
          "SELECT fives " +
          "FROM clubparm2 " +
          "WHERE courseName = ?");

       pstmt.clearParameters();
       pstmt.setString(1, course);
       rs = pstmt.executeQuery();

       if (rs.next()) {

          fives = rs.getInt(1);          // 5-somes
       }
       pstmt.close();

   } catch (Exception exc) {

      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we encountered the following error.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
   

   //  Display notice if we came from Old Sheets and 5-somes allowed
   
   if (fives > 0 && oldsheets == true) {
      
      out.println("<p align=center><BR><font size=3><strong>NOTICE: </strong>On past tee sheets the group is considered full if 4 players.</font></p>");
   }

   //  Display the Course Name, if provided
   
   if (!course.equals("")) {
      
      out.println("<p align=center><BR><font size=4><strong>" +course+ "</strong></font></p>");
   }

   out.println("<table border=\"0\" align=\"center\" valign=\"top\">");     // table for page
   out.println("<tr><td align=\"center\">");

   out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" valign=\"top\">");   // table for morning times
   out.println("<tr>");
   out.println("<td align=\"center\" bgcolor=yellow><font size=\"2\"><b>AM</b></font></td>");

   
    if (fives > 0 && oldsheets == false) groupsize = 5;   // assume group size at 4 if came from Old Tee Sheets (we don't have 5-some restriction data)

    //
    //  Get the tee sheet for this date (morning times only)
    //
    ampm = "AM";

    displayTimes(date, groupsize, course, oldsheets, ampm, out, con);       // display the morning times


    //
    //  Now do the afternoon times
    //
    out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" valign=\"top\">");   // table for afternoon times
    out.println("<tr>");
    out.println("<td align=\"center\" bgcolor=yellow><font size=\"2\"><b>PM</b></font></td>");

    ampm = "PM";

    displayTimes(date, groupsize, course, oldsheets, ampm, out, con);       // display the afternoon times

      
    out.println("</td></tr></table>");         // end of course table
      
 }       // end of displayCOurse


 
 // *********************************************************
 //  Display the Tee Time Table for the Quick View
 // *********************************************************

 private static void displayTimes(long date, int groupsize, String course, boolean oldsheets, String ampm, PrintWriter out, Connection con) {

   ResultSet rs = null;
   PreparedStatement pstmt = null;

   String timefb = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String bgcolor = "";
   String stringTee2 = "";
   String etype = "";

   int hr = 0;
   int min = 0;
   int fb = 0;
   int guests = 0;
   int members = 0;
   int players = 0;
   int last_hr = 0;
   
   
   try {
      
      if (oldsheets == false) {
   
         if (ampm.equals("AM")) {

            stringTee2 = "SELECT " +
                     "hr, min, player1, player2, player3, player4, username1, username2, username3, username4, " +
                     "fb, player5, username5 " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND time < 1200 AND courseName = ? AND blocker = '' ORDER BY time, fb";

         } else {

            stringTee2 = "SELECT " +
                     "hr, min, player1, player2, player3, player4, username1, username2, username3, username4, " +
                     "fb, player5, username5 " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND time > 1159 AND courseName = ? AND blocker = '' ORDER BY time, fb";
         }
         
      } else {     // old tee sheets
         
         if (ampm.equals("AM")) {

            stringTee2 = "SELECT " +
                     "hr, min, player1, player2, player3, player4, username1, username2, username3, username4, " +
                     "fb, player5, username5 " +
                     "FROM teepast2 " +
                     "WHERE date = ? AND time < 1200 AND courseName = ? ORDER BY time, fb";

         } else {

            stringTee2 = "SELECT " +
                     "hr, min, player1, player2, player3, player4, username1, username2, username3, username4, " +
                     "fb, player5, username5 " +
                     "FROM teepast2 " +
                     "WHERE date = ? AND time > 1159 AND courseName = ? ORDER BY time, fb";
         }
      }
        
      pstmt = con.prepareStatement (stringTee2);

      pstmt.clearParameters();
      pstmt.setLong(1, date);
      pstmt.setString(2, course);

      rs = pstmt.executeQuery();

      while (rs.next()) {

         hr = rs.getInt("hr");
         min = rs.getInt("min");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         player5 = rs.getString("player5");
         fb = rs.getInt("fb");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         user5 = rs.getString("username5");
         
         members = 0;
         guests = 0;
         players = 0;
         bgcolor = "#FFFFFF";

         if (!player1.equals( "" )) {
            
            players++;
            
            if (!player1.equalsIgnoreCase( "x" )) {      
            
               if (!user1.equals( "" )) {    

                  members++;

               } else {

                  guests++;
               }
            }
               
         } else {
            
            player1 = " ";      // for display
         }

         if (!player2.equals( "" )) {
            
            players++;
            
            if (!player2.equalsIgnoreCase( "x" )) {      
            
               if (!user2.equals( "" )) {    

                  members++;

               } else {

                  guests++;
               }
            }
               
         } else {
            
            player2 = " ";      // for display
         }

         if (!player3.equals( "" )) {
            
            players++;
            
            if (!player3.equalsIgnoreCase( "x" )) {      
            
               if (!user3.equals( "" )) {    

                  members++;

               } else {

                  guests++;
               }
            }
               
         } else {
            
            player3 = " ";      // for display
         }

         if (!player4.equals( "" )) {
            
            players++;
            
            if (!player4.equalsIgnoreCase( "x" )) {      
            
               if (!user4.equals( "" )) {    

                  members++;

               } else {

                  guests++;
               }
            }
               
         } else {
            
            player4 = " ";      // for display
         }

         if (!player5.equals( "" )) {
            
            players++;
            
            if (!player5.equalsIgnoreCase( "x" )) {      
            
               if (!user5.equals( "" )) {    

                  members++;

               } else {

                  guests++;
               }
            }
               
         } else {
            
            player5 = " ";      // for display
         }
         
         
         //
         //  Create a time string with f/b for this tee time
         //
         if (hr == 0) {
            
            hr = 12;
            
         } else if (hr > 12) {
            
            hr -= 12;
         }

         timefb = hr+ ":" + SystemUtils.ensureDoubleDigit(min) + " " +ampm;
         
         if (fb == 1) {

            timefb += " B ";
            
         } else {
            
            timefb += " F ";
         }
         
         //
         //  Determine the color to use for this tee time cell (default is white)
         //
         etype = "&nbsp;&nbsp;";               // default to empty time
         
         if (players > 0) {
          
            if (players < groupsize) {         // if NOT full
               
               if (members > 0 && guests > 0) {
                  
                  bgcolor = PC_color;    // partial combination (light red)
                  etype = "PC";
                  
               } else if (guests > 0) {
                  
                  bgcolor = PG_color;    // partial guest time (light green)
                  etype = "PG";
                  
               } else {
                  
                  bgcolor = PM_color;    // partial member time (light blue)
                  etype = "PM";
               }
               
            } else {                    // tee time is full
               
               if (members > 0 && guests > 0) {
                  
                  bgcolor = FC_color;    // full combination (dark red)
                  etype = "FC";
                  
               } else if (guests > 0) {
                  
                  bgcolor = FG_color;    // full guest time (dark green)
                  etype = "FG";
                  
               } else {
                  
                  bgcolor = FM_color;    // full member time (dark blue)
                  etype = "FM";
               }               
            }    
         }
         
         if (!player2.equals(" ")) {         // add separators between players
            
            player2 = "| " + player2;
         }
         if (!player3.equals(" ")) {
            
            player3 = "| " + player3;
         }
         if (!player4.equals(" ")) {
            
            player4 = "| " + player4;
         }
         if (!player5.equals(" ")) {
            
            player5 = "| " + player5;
         }
            
         if (last_hr != hr) {
             
             last_hr = hr;
             out.println("<td bgcolor=yellow><font size=\"2\">" + hr + "</font></td>");
         }

         //
         //  Display the tee time info
         //
         out.println("<td bgcolor=" +bgcolor+ " nowrap>");
         out.println("<font size=\"2\"><b>");
         out.print("<a href=\"javascript: void(0)\" " +
                      "style=\"text-decoration:none; color: black\" " +
                      "title=\"" +timefb+ "&nbsp;&nbsp;" +player1+ "&nbsp;&nbsp;" +player2+ "&nbsp;&nbsp;" +player3+ "&nbsp;&nbsp;" +player4+ "&nbsp;&nbsp;" +player5+ "\">" +etype+ "</a>");
         out.println("</b></font></td>");
         
      } // end of WHILE tee times

      out.println("</tr></table>");              // end of morning or afternoon table
       
   
   } catch (Exception exc) {

      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we encountered the following error.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
   
 }       // end of displayTimes
 
 public static void exportBocaWoods(Connection con, HttpServletResponse resp) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     PrintWriter out = null;
     
     
     try { 
         out = resp.getWriter();
         
         resp.setContentType("text/csv");
         resp.setHeader("Content-Disposition", "attachment;filename=\"bwa.txt\"");
         
     } catch (Exception exc) {
         Utilities.logError("Proshop_sheet_reports.exportBocaWoods - bocawoodscc - Error getting writer object - ERR: " + exc.toString());
     }
     
     int time = 0;
     
     long date1 = Utilities.getDate(con);
     long date2 = Utilities.getDate(con, 1);
     long date3 = Utilities.getDate(con, 2);
     
     String player = "";
     String day = "";
     String courseName = "";
     String timeS = "";
     String hole = "";
     
     try {
         
         pstmt = con.prepareStatement(
                 "(SELECT CONCAT(name_last, '/', name_first) AS playerName, SUBSTR(day, 1, 3) AS 'day', time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username1 "
                 + "WHERE (date = ? OR date = ? OR date = ?) AND username1 <> '') "
                 + "UNION ALL"
                 + "(SELECT CONCAT(name_last, '/', name_first) AS playerName, SUBSTR(day, 1, 3) AS 'day', time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username2 "
                 + "WHERE (date = ? OR date = ? OR date = ?) AND username2 <> '') "
                 + "UNION ALL"
                 + "(SELECT CONCAT(name_last, '/', name_first) AS playerName, SUBSTR(day, 1, 3) AS 'day', time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username3 "
                 + "WHERE (date = ? OR date = ? OR date = ?) AND username3 <> '') "
                 + "UNION ALL"
                 + "(SELECT CONCAT(name_last, '/', name_first) AS playerName, SUBSTR(day, 1, 3) AS 'day', time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username4 "
                 + "WHERE (date = ? OR date = ? OR date = ?) AND username4 <> '') "
                 + "UNION ALL"
                 + "(SELECT CONCAT(name_last, '/', name_first) AS playerName, SUBSTR(day, 1, 3) AS 'day', time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username5 "
                 + "WHERE (date = ? OR date = ? OR date = ?) AND username5 <> '') "
                 + "ORDER BY playerName, date");
         
         pstmt.clearParameters();
         pstmt.setLong(1, date1);
         pstmt.setLong(2, date2);
         pstmt.setLong(3, date3);
         pstmt.setLong(4, date1);
         pstmt.setLong(5, date2);
         pstmt.setLong(6, date3);
         pstmt.setLong(7, date1);
         pstmt.setLong(8, date2);
         pstmt.setLong(9, date3);
         pstmt.setLong(10, date1);
         pstmt.setLong(11, date2);
         pstmt.setLong(12, date3);
         pstmt.setLong(13, date1);
         pstmt.setLong(14, date2);
         pstmt.setLong(15, date3);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
             player = rs.getString("playerName").toUpperCase();
             day = rs.getString("day");
             time = rs.getInt("time");
             courseName = rs.getString("courseName");
             hole = rs.getString("hole");
             
             if (time >= 1300) {
                 time = time - 1200;
             }
             
             timeS = String.valueOf(time);
             
             timeS = timeS.substring(0, timeS.length() - 2) + ":" + timeS.substring(timeS.length() - 2);
             
             out.println(player + "," + day + "," + timeS + "," + courseName + "," + hole);
         }
         
     } catch (Exception exc) {
         Utilities.logError("Proshop_sheet_reports.exportBocaWoods - bocawoodscc - Error looking up tee time data - ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { pstmt.close(); }
         catch (Exception ignore) {}
     }
 }
 
 public static void exportRioVerde(long date, Connection con, HttpServletResponse resp) {
     
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     PrintWriter out = null;
     
     
     try { 
         out = resp.getWriter();
         
         resp.setContentType("application/vnd.ms-excel");
         resp.setHeader("Content-Disposition", "attachment;filename=\"rioverdecc_" + date + ".csv\"");
         
     } catch (Exception exc) {
         Utilities.logError("Proshop_sheet_reports.exportRioVerde - rioverdecc - Error getting writer object - ERR: " + exc.toString());
     }
     
     int time = 0;
     long yy = date / 10000;
     long mm = (date - (yy * 10000)) / 100;
     long dd = date - ((yy * 10000) + (mm * 100));
     
     String player = "";
     String courseName = "";
     String timeS = "";
     String hole = "";
     
     out.println("Space 1,,,,,,,,");
     out.println("Space 2,,,,,,,,");
     out.println(",,,,,Rio Verde CC - " + mm + "/" + dd + "/" + yy + " Tee Sheet,,,");
     out.println("Space 4,,,,,,,,");
     out.println(",,Alpha,,,,,,");
     out.println(",,,,,,,,");
     
     try {
         
         pstmt = con.prepareStatement(
                 "(SELECT IF(username1 <> '', CONCAT('\"', name_last, ', ', name_first, '\"'), player1) AS playerName, event, time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username1 "
                 + "WHERE date = ? AND username1 <> '') "
                 + "UNION ALL"
                 + "(SELECT IF(username2 <> '', CONCAT('\"', name_last, ', ', name_first, '\"'), player2) AS playerName, event, time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username2 "
                 + "WHERE date = ? AND username2 <> '') "
                 + "UNION ALL"
                 + "(SELECT IF(username3 <> '', CONCAT('\"', name_last, ', ', name_first, '\"'), player3) AS playerName, event, time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username3 "
                 + "WHERE date = ? AND username3 <> '') "
                 + "UNION ALL"
                 + "(SELECT IF(username4 <> '', CONCAT('\"', name_last, ', ', name_first, '\"'), player4) AS playerName, event, time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username4 "
                 + "WHERE date = ? AND username4 <> '') "
                 + "UNION ALL"
                 + "(SELECT IF(username5 <> '', CONCAT('\"', name_last, ', ', name_first, '\"'), player5) AS playerName, event, time, courseName, IF(fb = 0, '01', '10') AS hole, date "
                 + "FROM teecurr2 t "
                 + "LEFT OUTER JOIN member2b m ON m.username = t.username5 "
                 + "WHERE date = ? AND username5 <> '') "
                 + "ORDER BY playerName, date");
         
         pstmt.clearParameters();
         pstmt.setLong(1, date);
         pstmt.setLong(2, date);
         pstmt.setLong(3, date);
         pstmt.setLong(4, date);
         pstmt.setLong(5, date);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
             player = rs.getString("playerName");
             time = rs.getInt("time");
             courseName = rs.getString("courseName");
             hole = rs.getString("hole");
             
             if (!rs.getString("event").equals("")) {
                 time = Utilities.getEventTime(rs.getString("event"), con);
             }
             
             if (time >= 1300) {
                 time = time - 1200;
             }
             
             timeS = String.valueOf(time);
             
             timeS = timeS.substring(0, timeS.length() - 2) + ":" + timeS.substring(timeS.length() - 2);
             
             out.println("," + player + ",,,," + timeS + "," + courseName + ",," + hole);
         }
         
     } catch (Exception exc) {
         Utilities.logError("Proshop_sheet_reports.exportRioVerde - rioverdecc - Error looking up tee time data - ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { pstmt.close(); }
         catch (Exception ignore) {}
     }
 }
 
   
   
   
 // ******************************************************************************
 //  Get a member's bag slot number
 // ******************************************************************************
 private static String getBag(String user, Connection con) {


    String bag = "";

    if (!user.equals( "" )) {
        
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {

            pstmt = con.prepareStatement (
                "SELECT bag FROM member2b WHERE username = ?");

            pstmt.clearParameters();
            pstmt.setString(1, user);
            rs = pstmt.executeQuery();

            if (rs.next()) bag = rs.getString(1);         // user's bag room slot#

       } catch (Exception ignore) {

       } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    } // end if user empty (shouldn't ever happen though...)
        
    return(bag);

 } // end of getBag


 // *********************************************************
 //  Output the image based on the check-in value
 // *********************************************************
 private static String getShowImageForPrint(short pShow) {

    String tmp;

    switch (pShow) {
    case 1:
        tmp = "<img src=\"/" +rev+ "/images/xboxsm.gif\" border=\"2\">";
        break;
    case 2:
        tmp = "<img src=\"/" +rev+ "/images/ymtboxsm.gif\" border=\"2\">";
        break;
    default: // if not 1 or 2 then assume 0 and return the small empty image box
        tmp = "<img src=\"/" +rev+ "/images/mtboxsm.gif\" border=\"2\">";
    }

    return tmp;
 }


 // *********************************************************
 //  Strip the '9' from end of string
 // *********************************************************
 private static final String strip9( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i];
         ca2[i] = oldLetter;
      } 

      return new String (ca2);

 } // end strip9



 // *********************************************************
 //  Strip 1 char from the start of a string
 // *********************************************************
 private static final String stripOne( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i+1];
         ca2[i] = oldLetter;
      } 

      return new String (ca2);

 } // end stripOne

 
 private static void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
     
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<br><br><form>");
    out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
    out.println("</form>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
    
 }

}
