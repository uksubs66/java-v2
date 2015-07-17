/***************************************************************************************     
 *   Proshop_reports:  This servlet will process a request from Proshop's
 *                     reports page.
 *
 *
 *   called by:  proshop_reports.htm 
 *               self (calls doPost)
 *
 *
 *   parms passed by proshop_reports.htm:   
 *
 *               round=all  for Number of Rounds Played Report (this month, MTD, YTD)
 *               today=yes  for Number of Rounds Played Today Report
 *               custom=yes for Number of Rounds Played Report by custom date range
 *               noshow=yes for No-Show report by date & member
 *               subtee=cal for a List of a member's past tee times for calendar year
 *               subtee=year for a List of a member's past tee times for past 12 months
 *               subtee=forever for a List of a member's past tee times since inception
 *               teetimes=yes for a list of who is making tee times (proshop vs members)
 *
 *   parms passed by self:
 *
 *               gotee='subtee value' for Tee Times List
 *                                    (username)
 *               noshow2=yes  for No-Show report - call from here to generate report
 *                            (smonth, sday, syear, emonth, eday, eyear, username)
 *
 *               custom2=yes  for Number of Rounds report (custom date) - call from here to generate report
 *                            (smonth, sday, syear, emonth, eday, eyear)
 *
 *
 *   created: 2/21/2002   Bob P.
 *
 *   last updated:
 *
 *        3/10/05   Ver 5 -   (Paul S)
 *        3/02/05   Ver 5 - modified some conditional statements for precheckin support, show1-5  (Paul S)
 *        1/25/05   Ver 5 - correct names specified for stats5 table.
 *        1/24/05   Ver 5 - change club2 to club5 and stats2 to stats5.
 *        9/16/04   Ver 5 - change getClub from SystemUtils to common.
 *        7/07/04   Add 'List All Members' option for Member Tee Time reports.
 *        3/01/04   Add 'Excel Spreadsheet' option for reports.
 *        2/06/04   Add new Modes of Transportation stats
 *        1/06/04   Add Hotel counts to Logins Stats
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
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
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;


public class Proshop_reports extends HttpServlet {
                
 String omit = "";

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*******************************************************
 // Process the initial request from proshop menu
 //  or the request from itself.
 //*******************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   Statement stmt = null;
   ResultSet rs = null;

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   Connection con = SystemUtils.getCon(sess);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   String rep = "";

   //
   // Check which parameters were received - process accordingly
   //
   //   Initial calls from proshop_reports.htm
   //
   if (req.getParameter("round") != null) {
    
      goRounds(req, resp, out, sess, con);                   // go process Number of Rounds Played request
      return;
   }

   if (req.getParameter("today") != null) {

      goToday(req, out, con);                   // go process Number of Rounds Played request
      return;
   }

   if (req.getParameter("teetimes") != null) {

      goTeetimes(req, out, con);                   // go process Tee Time Report request
      return;
   }

   if (req.getParameter("custom") != null) {

      goCustom(req, out, con);                   // go process Number of Rounds Played request (custom date)
      return;
   }

   if (req.getParameter("noshow") != null) {

      noShows(req, out, con);                   // go process No_shows request
      return;
   }

   if (req.getParameter("subtee") != null) {

      teeTime(req, out, con);                   // go process member tee time request
      return;
   }

   if (req.getParameter("noshow2") != null) {

      noShow2(req, out, con);                  // go process No-Show Report 2nd request
      return;
   }

   if (req.getParameter("custom2") != null) {

      Custom2(req, out, con);                  // go process Number of Rounds Report, Custom Date - 2nd request
      return;
   }

 }  // end of doPost


 // *********************************************************
 //  Report Type = Number of Rounds Played (round=all)
 // *********************************************************

 private void goRounds(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession sess, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  get the club name from the session
   //
   String club = (String)sess.getAttribute("club");      // get club name
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //   Get multi option, member types, and guest types
   //
   try {

      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception ignore) {
   }

   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;

   int memUnknown1 = 0;
   int memUnknown2 = 0;
   int memUnknown4 = 0;
   int memUnknown9 = 0;
   int memUnknown18 = 0;
   int mshipUnknown1 = 0;
   int mshipUnknown2 = 0;
   int mshipUnknown4 = 0;
   int mshipUnknown9 = 0;
   int mshipUnknown18 = 0;

   int [] tmodeR1 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R1 = new int [parm.MAX_Tmodes];
   int [] tmode18R1 = new int [parm.MAX_Tmodes];

   int tmodeOldR91 = 0;
   int tmodeOldR181 = 0;

   int nshowRounds1 = 0;
   int nshow9Rounds1 = 0;
   int nshow18Rounds1 = 0;
   int mnshowRounds1 = 0;
   int mnshow9Rounds1 = 0;
   int mnshow18Rounds1 = 0;
   int gnshowRounds1 = 0;
   int gnshow9Rounds1 = 0;
   int gnshow18Rounds1 = 0;

   int otherRounds1 = 0;
   int other9Rounds1 = 0;
   int other18Rounds1 = 0;

   int totRounds1 = 0;

   int memRounds1 = 0;
   int [] memxRounds1 = new int [parm.MAX_Mems];       // use arrays for the mem types

   int mem9Rounds1 = 0;
   int mem18Rounds1 = 0;
     
   int gstRounds1 = 0;
   int [] gstRnds1 = new int [parm.MAX_Guests];       // use array for the 36 guest types
     
   int gst9Rounds1 = 0;
   int gst18Rounds1 = 0;

   int mshipRounds1 = 0;
   int [] mshipxRounds1 = new int [parm.MAX_Mships];       // use arrays for the mship types
     
   int mship9Rounds1 = 0;
   int mship18Rounds1 = 0;

   int [] memxRounds9 = new int [parm.MAX_Mems];       // use arrays for the mem types
   int [] memxRounds18 = new int [parm.MAX_Mems];      // use arrays for the mem types
     
   int [] mshipxRounds9 = new int [parm.MAX_Mships];       // use arrays for the mship types
   int [] mshipxRounds18 = new int [parm.MAX_Mships];      // use arrays for the mship types

   int [] gst1Rnds9 = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] gst1Rnds18 = new int [parm.MAX_Guests];

   int [] tmodeR2 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R2 = new int [parm.MAX_Tmodes];
   int [] tmode18R2 = new int [parm.MAX_Tmodes];

   int tmodeOldR92 = 0;
   int tmodeOldR182 = 0;

   int nshowRounds2 = 0;
   int nshow9Rounds2 = 0;
   int nshow18Rounds2 = 0;
   int mnshowRounds2 = 0;
   int mnshow9Rounds2 = 0;
   int mnshow18Rounds2 = 0;
   int gnshowRounds2 = 0;
   int gnshow9Rounds2 = 0;
   int gnshow18Rounds2 = 0;

   int otherRounds2 = 0;
   int other9Rounds2 = 0;
   int other18Rounds2 = 0;

   int totRounds2 = 0;

   int memRounds2 = 0;
   int [] memxRounds2 = new int [parm.MAX_Mems];       // use arrays for the mem types

   int mem9Rounds2 = 0;
   int mem18Rounds2 = 0;
     
   int gstRounds2 = 0;
   int [] gstRnds2 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gst9Rounds2 = 0;
   int gst18Rounds2 = 0;

   int mshipRounds2 = 0;
   int [] mshipxRounds2 = new int [parm.MAX_Mships];       // use arrays for the mship types

   int mship9Rounds2 = 0;
   int mship18Rounds2 = 0;

   int [] tmodeR4 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R4 = new int [parm.MAX_Tmodes];
   int [] tmode18R4 = new int [parm.MAX_Tmodes];

   int tmodeOldR94 = 0;
   int tmodeOldR184 = 0;

   int nshowRounds4 = 0;
   int nshow9Rounds4 = 0;
   int nshow18Rounds4 = 0;
   int mnshowRounds4 = 0;
   int mnshow9Rounds4 = 0;
   int mnshow18Rounds4 = 0;
   int gnshowRounds4 = 0;
   int gnshow9Rounds4 = 0;
   int gnshow18Rounds4 = 0;

   int otherRounds4 = 0;
   int other9Rounds4 = 0;
   int other18Rounds4 = 0;

   int totRounds4 = 0;

   int memRounds4 = 0;
   int [] memxRounds4 = new int [parm.MAX_Mems];       // use arrays for the mem types

   int mem9Rounds4 = 0;
   int mem18Rounds4 = 0;

   int gstRounds4 = 0;
   int [] gstRnds4 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gst9Rounds4 = 0;
   int gst18Rounds4 = 0;

   int mshipRounds4 = 0;
   int [] mshipxRounds4 = new int [parm.MAX_Mships];       // use arrays for the mship types

   int mship9Rounds4 = 0;
   int mship18Rounds4 = 0;

   long edate = 0;                             // today's date
   long mtddate = 0;                           // MTD start date
   long ytddate = 0;                           // YTD start date
   long lmsdate = 0;                           // Last Month start date
   long lmedate = 0;                           // Last Month end date
   int year = 0;
   int month = 0;
   int day = 0;
     
   int multi = 0;                 // multiple course support
   int index = 0;
   int i = 0;
   int i2 = 0;
   int count = 0;                 // number of courses

   //
   //  ints to hold stats from db table
   //
   int [] memxr9 = new int [parm.MAX_Mems];       // use arrays for the mem types
   int [] memxr18 = new int [parm.MAX_Mems];      // use arrays for the mem types

   int [] mshipxr9 = new int [parm.MAX_Mships];   // use arrays for the mship types
   int [] mshipxr18 = new int [parm.MAX_Mships];  // use arrays for the mship types

   int [] gstr9 = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] gstr18 = new int [parm.MAX_Guests];

   int other9 = 0;
   int other18 = 0;
   int cart9 = 0;
   int cart18 = 0;
   int cady9 = 0;
   int cady18 = 0;
   int pc9 = 0;
   int pc18 = 0;
   int wa9 = 0;
   int wa18 = 0;
   int memnshow9 = 0;
   int memnshow18 = 0;
   int gstnshow9 = 0;
   int gstnshow18 = 0;
   int memunk9 = 0;
   int memunk18 = 0;
   int mshipunk9 = 0;
   int mshipunk18 = 0;

   int [] tmode9 = new int [parm.MAX_Tmodes];
   int [] tmode18 = new int [parm.MAX_Tmodes];

   int tmodeOldR9 = 0;
   int tmodeOldR18 = 0;

   //
   //  Array to hold the course names
   //
   String [] course = new String [20];                     // max of 20 courses per club

   String courseName = "";        // course names

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";

   String error = "None";

   boolean found = false;

   //
   //  Get today's date and current time and calculate date & time values 
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_am_pm = cal.get(Calendar.AM_PM);        // current time
   int cal_hour = cal.get(Calendar.HOUR);
   int cal_min = cal.get(Calendar.MINUTE);

   int curr_time = cal_hour;
   if (cal_am_pm == 1) {                       // if PM

      curr_time = curr_time + 12;              // convert to military time
   }

   curr_time = curr_time * 100;                // create current time value for compare
   curr_time = curr_time + cal_min;

   month = month + 1;                           // month starts at zero

   edate = year * 10000;                        // create a edate field of yyyymmdd (for today)
   edate = edate + (month * 100);
   edate = edate + day;                         // date = yyyymmdd (for comparisons)

   mtddate = year * 10000;                      // create a MTD date
   mtddate = mtddate + (month * 100);
   mtddate = mtddate + 01;

   ytddate = year * 10000;                      // create a YTD date
   ytddate = ytddate + 100;
   ytddate = ytddate + 01;

   month = month - 1;                           // last month

   if (month == 0) {
      
      month = 12;
      year = year - 1;
   }
     
   lmsdate = year * 10000;                      // create a Last Month Start date
   lmsdate = lmsdate + (month * 100);
   lmsdate = lmsdate + 01;
   
   lmedate = lmsdate + 30;                      // create a Last Month End date


   //
   //   Get multi option, member types, and guest types
   //
   multi = parm.multi;

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

   //
   // Check for multiple courses
   //
   count = 1;                  // init to 1 course

   if (multi != 0) {           // if multiple courses supported for this club

      while (index< 20) {

         course[index] = "";       // init the course array
         index++;
      }

      index = 0;

      try {

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
         count = index;                      // number of courses

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
   //
   try{
      if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
      }
   }
   catch (Exception exc) {
   }

   out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   if (req.getParameter("excel") == null) {     // if normal request
      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      out.println("<p><b>Course Statistics</b><br></font><font size=\"2\">");
      out.println("<b>Note:</b> Percentages are rounded down to whole number.<br>");
      out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
      out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
  
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_reports\" target=\"_blank\">");
      out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"round\" value=\"all\">");
      out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</font>");
   }

   courseName = "";            // init as not multi
   index = 0;

   if (multi != 0) {           // if multiple courses supported for this club

      courseName = course[index];      // get first course name 
   }

   //
   // execute searches and display for each course
   //
   while (count > 0) {

      //
      //  init count fields for each course
      //
      memUnknown1 = 0;
      memUnknown2 = 0;
      memUnknown4 = 0;
      memUnknown9 = 0;
      memUnknown18 = 0;
      mshipUnknown1 = 0;
      mshipUnknown2 = 0;
      mshipUnknown4 = 0;
      mshipUnknown9 = 0;
      mshipUnknown18 = 0;

      nshowRounds1 = 0;
      nshow9Rounds1 = 0;
      nshow18Rounds1 = 0;
      mnshowRounds1 = 0;
      mnshow9Rounds1 = 0;
      mnshow18Rounds1 = 0;
      gnshowRounds1 = 0;
      gnshow9Rounds1 = 0;
      gnshow18Rounds1 = 0;

      otherRounds1 = 0;
      other9Rounds1 = 0;
      other18Rounds1 = 0;

      totRounds1 = 0;
      memRounds1 = 0;
      mem9Rounds1 = 0;
      mem18Rounds1 = 0;
        
      gstRounds1 = 0;
      gst9Rounds1 = 0;
      gst18Rounds1 = 0;

      mship9Rounds1 = 0;
      mship18Rounds1 = 0;

      nshowRounds2 = 0;
      nshow9Rounds2 = 0;
      nshow18Rounds2 = 0;
      mnshowRounds2 = 0;
      mnshow9Rounds2 = 0;
      mnshow18Rounds2 = 0;
      gnshowRounds2 = 0;
      gnshow9Rounds2 = 0;
      gnshow18Rounds2 = 0;

      otherRounds2 = 0;
      other9Rounds2 = 0;
      other18Rounds2 = 0;

      totRounds2 = 0;
      memRounds2 = 0;
      mem9Rounds2 = 0;
      mem18Rounds2 = 0;

      gstRounds2 = 0;
      gst9Rounds2 = 0;
      gst18Rounds2 = 0;

      mship9Rounds2 = 0;
      mship18Rounds2 = 0;

      nshowRounds4 = 0;
      nshow9Rounds4 = 0;
      nshow18Rounds4 = 0;
      mnshowRounds4 = 0;
      mnshow9Rounds4 = 0;
      mnshow18Rounds4 = 0;
      gnshowRounds4 = 0;
      gnshow9Rounds4 = 0;
      gnshow18Rounds4 = 0;

      otherRounds4 = 0;
      other9Rounds4 = 0;
      other18Rounds4 = 0;

      totRounds4 = 0;
      memRounds4 = 0;
      mem9Rounds4 = 0;
      mem18Rounds4 = 0;

      gstRounds4 = 0;
      gst9Rounds4 = 0;
      gst18Rounds4 = 0;

      mship9Rounds4 = 0;
      mship18Rounds4 = 0;

      tmodeOldR9 = 0;
      tmodeOldR18 = 0;
      tmodeOldR91 = 0;
      tmodeOldR181 = 0;
      tmodeOldR92 = 0;
      tmodeOldR182 = 0;
      tmodeOldR94 = 0;
      tmodeOldR184 = 0;

      //
      //  Init the Modes of Trans arrays
      //
      for (i = 0; i < parm.MAX_Tmodes; i++) {
         tmodeR1[i] = 0;
         tmodeR2[i] = 0;
         tmodeR4[i] = 0;
         tmode9R1[i] = 0;
         tmode9R2[i] = 0;
         tmode9R4[i] = 0;
         tmode18R1[i] = 0;
         tmode18R2[i] = 0;
         tmode18R4[i] = 0;
      }

      //
      //  Init the Guest arrays
      //
      for (i = 0; i < parm.MAX_Guests; i++) {
         gstRnds4[i] = 0;
         gstRnds2[i] = 0;
         gst1Rnds9[i] = 0;
         gst1Rnds18[i] = 0;
         gstRnds1[i] = 0;
      }
        
      //
      //  Init the Mem Type arrays
      //
      for (i = 0; i < parm.MAX_Mems; i++) {
         memxRounds1[i] = 0;
         memxRounds2[i] = 0;
         memxRounds4[i] = 0;
         memxRounds9[i] = 0;
         memxRounds18[i] = 0;
      }

      //
      //  Init the Mship Type arrays
      //
      for (i = 0; i < parm.MAX_Mships; i++) {
         mshipxRounds1[i] = 0;
         mshipxRounds2[i] = 0;
         mshipxRounds4[i] = 0;
         mshipxRounds9[i] = 0;
         mshipxRounds18[i] = 0;
      }

      //
      // use the dates provided to search the stats table
      //
      try {

         //
         //  Get the System Parameters for this Course
         //
         getParms.getCourse(con, parmc, courseName);

         //
         //  Statement for MTD & YTD counts 
         //
         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT * " +
            "FROM stats5 WHERE date >= ? AND course = ?");

         //
         //  Statement for Last Month's counts 
         //
         PreparedStatement pstmt3 = con.prepareStatement (
            "SELECT * " +
            "FROM stats5 WHERE date >= ? AND date <= ? AND course = ?");

         error = "Get YTD Counts";
         
         //
         //  Get YTD counts
         //
         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, ytddate);
         pstmt1.setString(2, courseName);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            i2 = 1;
            for (i = 0; i < parm.MAX_Mems; i++) {
               memxr9[i] = rs.getInt("mem" +i2+ "Rounds9"); 
               memxr18[i] = rs.getInt("mem" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Mships; i++) {
               mshipxr9[i] = rs.getInt("mship" +i2+ "Rounds9");
               mshipxr18[i] = rs.getInt("mship" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Guests; i++) {
               gstr9[i] = rs.getInt("gst" +i2+ "Rounds9");
               gstr18[i] = rs.getInt("gst" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Tmodes; i++) {
               tmode9[i] = rs.getInt("tmode" +i2+ "R9");
               tmode18[i] = rs.getInt("tmode" +i2+ "R18");
               i2++;
            }
            other9 = rs.getInt("otherRounds9");
            other18 = rs.getInt("otherRounds18");
            cart9 = rs.getInt("cartsRounds9");
            cart18 = rs.getInt("cartsRounds18");
            cady9 = rs.getInt("caddyRounds9");
            cady18 = rs.getInt("caddyRounds18");
            pc9 = rs.getInt("pullcartRounds9");
            pc18 = rs.getInt("pullcartRounds18");
            wa9 = rs.getInt("walkRounds9");
            wa18 = rs.getInt("walkRounds18");
            memnshow9 = rs.getInt("memnoshow9");
            memnshow18 = rs.getInt("memnoshow18");
            gstnshow9 = rs.getInt("gstnoshow9");
            gstnshow18 = rs.getInt("gstnoshow18");
            memunk9 = rs.getInt("mem9unknown");
            memunk18 = rs.getInt("mem18unknown");
            mshipunk9 = rs.getInt("mship9unknown");
            mshipunk18 = rs.getInt("mship18unknown");
            tmodeOldR9 = rs.getInt("tmodeOldR9");
            tmodeOldR18 = rs.getInt("tmodeOldR18");

            //
            //  got the stats for one day - add them to the running totals
            //
            //     Get Member Rounds
            //
            for (i = 0; i < parm.MAX_Mems; i++) {
              
               mem9Rounds1 = mem9Rounds1 + memxr9[i];
               mem18Rounds1 = mem18Rounds1 + memxr18[i];
               memxRounds9[i] = memxRounds9[i] + memxr9[i];        // member 9 hole rounds per mem type
               memxRounds18[i] = memxRounds18[i] + memxr18[i];     // member 18 hole rounds per mem type
               memxRounds1[i] = memxRounds18[i] + memxRounds9[i];  // individual member type totals
            }

            memRounds1 = mem9Rounds1 + mem18Rounds1;                 // rounds by members

            memUnknown9 = memUnknown9 + memunk9;                     // unknown member types
            memUnknown18 = memUnknown18 + memunk18;

            memUnknown1 = memUnknown9 + memUnknown18;

            //
            //  Get membership Rounds
            //
            for (i = 0; i < parm.MAX_Mems; i++) {

               mshipxRounds9[i] = mshipxRounds9[i] + mshipxr9[i];        // 9 hole rounds per mship type
               mshipxRounds18[i] = mshipxRounds18[i] + mshipxr18[i];     // 18 hole rounds per mship type
               mshipxRounds1[i] = mshipxRounds18[i] + mshipxRounds9[i];  // individual membership type totals
            }

            mshipUnknown9 = mshipUnknown9 + mshipunk9;                     // unknown membership types
            mshipUnknown18 = mshipUnknown18 + mshipunk18;
            mshipUnknown1 = mshipUnknown9 + mshipUnknown18;

            //
            //  Get Guest Rounds
            //
            for (i = 0; i < parm.MAX_Guests; i++) {
               gst1Rnds9[i] = gst1Rnds9[i] + gstr9[i];        // guest 9 hole rounds
               gst1Rnds18[i] = gst1Rnds18[i] + gstr18[i];     // guest 18 hole rounds
               gstRnds1[i] = gst1Rnds18[i] + gst1Rnds9[i];    // individual guest totals
               gst9Rounds1 = gst9Rounds1 + gstr9[i];          // total guest 9 hole rounds
               gst18Rounds1 = gst18Rounds1 + gstr18[i];       // total guest 18 hole rounds
            }

            gstRounds1 = gst9Rounds1 + gst18Rounds1;          // total guest rounds (9 & 18)

            //
            //  Get Rounds by Others (not members, not guests)
            //
            other9Rounds1 = other9Rounds1 + other9;
            other18Rounds1 = other18Rounds1 + other18;

            otherRounds1 = other9Rounds1 + other18Rounds1;                 // total Other Rounds

            //
            //  No-Show Rounds by members and guests
            //
            mnshow9Rounds1 = mnshow9Rounds1 + memnshow9;                     // member no-shows
            mnshow18Rounds1 = mnshow18Rounds1 + memnshow18;

            gnshow9Rounds1 = gnshow9Rounds1 + gstnshow9;                     // guest no-shows
            gnshow18Rounds1 = gnshow18Rounds1 + gstnshow18;

            nshow9Rounds1 = mnshow9Rounds1 + gnshow9Rounds1;
            nshow18Rounds1 = mnshow18Rounds1 + gnshow18Rounds1;

            mnshowRounds1 = mnshow9Rounds1 + mnshow18Rounds1;
            gnshowRounds1 = gnshow9Rounds1 + gnshow18Rounds1;
            nshowRounds1 = nshow9Rounds1 + nshow18Rounds1;                // total nshow Rounds

            for (i=0; i<parm.MAX_Tmodes; i++) {                 // do all the new trans mode types
              
               tmode9R1[i] += tmode9[i]; 
               tmode18R1[i] += tmode18[i];
               tmodeR1[i] = tmode9R1[i] + tmode18R1[i];
            }
              
            tmodeOldR91 += tmodeOldR9;                      // old trans mode counts
            tmodeOldR181 += tmodeOldR18;                 

         }         // end of while for YTD

         //
         //   Grand Total # of Rounds
         //
         totRounds1 = totRounds1 + memRounds1 + gstRounds1 + otherRounds1;     // total # of rounds played

         //
         //  init some work vars
         //
         for (i = 0; i < parm.MAX_Mems; i++) {
            memxRounds9[i] = 0;
            memxRounds18[i] = 0;
         }
           
         for (i = 0; i < parm.MAX_Mships; i++) {
            mshipxRounds9[i] = 0;
            mshipxRounds18[i] = 0;
         }

         for (i = 0; i < parm.MAX_Guests; i++) {
            gst1Rnds9[i] = 0;
            gst1Rnds18[i] = 0;
         }

         error = "Get MTD counts";

         //
         //  Get MTD counts - use same statement with different date
         //
         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, mtddate);
         pstmt1.setString(2, courseName);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            i2 = 1;
            for (i = 0; i < parm.MAX_Mems; i++) {
               memxr9[i] = rs.getInt("mem" +i2+ "Rounds9");
               memxr18[i] = rs.getInt("mem" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Mships; i++) {
               mshipxr9[i] = rs.getInt("mship" +i2+ "Rounds9");
               mshipxr18[i] = rs.getInt("mship" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Guests; i++) {
               gstr9[i] = rs.getInt("gst" +i2+ "Rounds9");
               gstr18[i] = rs.getInt("gst" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Tmodes; i++) {
               tmode9[i] = rs.getInt("tmode" +i2+ "R9");
               tmode18[i] = rs.getInt("tmode" +i2+ "R18");
               i2++;
            }
            other9 = rs.getInt("otherRounds9");
            other18 = rs.getInt("otherRounds18");
            cart9 = rs.getInt("cartsRounds9");
            cart18 = rs.getInt("cartsRounds18");
            cady9 = rs.getInt("caddyRounds9");
            cady18 = rs.getInt("caddyRounds18");
            pc9 = rs.getInt("pullcartRounds9");
            pc18 = rs.getInt("pullcartRounds18");
            wa9 = rs.getInt("walkRounds9");
            wa18 = rs.getInt("walkRounds18");
            memnshow9 = rs.getInt("memnoshow9");
            memnshow18 = rs.getInt("memnoshow18");
            gstnshow9 = rs.getInt("gstnoshow9");
            gstnshow18 = rs.getInt("gstnoshow18");
            memunk9 = rs.getInt("mem9unknown");
            memunk18 = rs.getInt("mem18unknown");
            mshipunk9 = rs.getInt("mship9unknown");
            mshipunk18 = rs.getInt("mship18unknown");
            tmodeOldR9 = rs.getInt("tmodeOldR9");
            tmodeOldR18 = rs.getInt("tmodeOldR18");

            //
            //  got the stats for one day - add them to the running totals
            //
            //     Get Member Rounds
            //
            for (i = 0; i < parm.MAX_Mems; i++) {

               mem9Rounds2 = mem9Rounds2 + memxr9[i];
               mem18Rounds2 = mem18Rounds2 + memxr18[i];
               memxRounds9[i] = memxRounds9[i] + memxr9[i];        // member 9 hole rounds per mem type
               memxRounds18[i] = memxRounds18[i] + memxr18[i];     // member 18 hole rounds per mem type
               memxRounds2[i] = memxRounds18[i] + memxRounds9[i];  // individual member type totals
            }

            memRounds2 = mem9Rounds2 + mem18Rounds2;                 // rounds by members

            memUnknown9 = memUnknown9 + memunk9;                     // unknown member types
            memUnknown18 = memUnknown18 + memunk18;

            memUnknown1 = memUnknown9 + memUnknown18;

            //
            //  Get membership Rounds
            //
            for (i = 0; i < parm.MAX_Mems; i++) {

               mshipxRounds9[i] = mshipxRounds9[i] + mshipxr9[i];        // 9 hole rounds per mship type
               mshipxRounds18[i] = mshipxRounds18[i] + mshipxr18[i];     // 18 hole rounds per mship type
               mshipxRounds2[i] = mshipxRounds18[i] + mshipxRounds9[i];  // individual membership type totals
            }

            mshipUnknown9 = mshipUnknown9 + mshipunk9;                     // unknown membership types
            mshipUnknown18 = mshipUnknown18 + mshipunk18;
            mshipUnknown1 = mshipUnknown9 + mshipUnknown18;

            //
            //  Get Guest Rounds
            //
            for (i = 0; i < parm.MAX_Guests; i++) {
               gst1Rnds9[i] = gst1Rnds9[i] + gstr9[i];        // guest 9 hole rounds
               gst1Rnds18[i] = gst1Rnds18[i] + gstr18[i];     // guest 18 hole rounds
               gstRnds2[i] = gst1Rnds18[i] + gst1Rnds9[i];    // individual guest totals
               gst9Rounds2 = gst9Rounds2 + gstr9[i];          // total guest 9 hole rounds
               gst18Rounds2 = gst18Rounds2 + gstr18[i];       // total guest 18 hole rounds
            }

            gstRounds2 = gst9Rounds2 + gst18Rounds2;                   // total guest rounds

            //
            //  Get Rounds by Others (not members, not guests)
            //
            other9Rounds2 = other9Rounds2 + other9;
            other18Rounds2 = other18Rounds2 + other18;

            otherRounds2 = other9Rounds2 + other18Rounds2;                 // total Other Rounds

            //
            //  No-Show Rounds by members and guests
            //
            mnshow9Rounds2 = mnshow9Rounds2 + memnshow9;                     // member no-shows
            mnshow18Rounds2 = mnshow18Rounds2 + memnshow18;

            gnshow9Rounds2 = gnshow9Rounds2 + gstnshow9;                     // guest no-shows
            gnshow18Rounds2 = gnshow18Rounds2 + gstnshow18;

            nshow9Rounds2 = mnshow9Rounds2 + gnshow9Rounds2;
            nshow18Rounds2 = mnshow18Rounds2 + gnshow18Rounds2;

            mnshowRounds2 = mnshow9Rounds2 + mnshow18Rounds2;
            gnshowRounds2 = gnshow9Rounds2 + gnshow18Rounds2;
            nshowRounds2 = nshow9Rounds2 + nshow18Rounds2;                // total nshow Rounds

            for (i=0; i<parm.MAX_Tmodes; i++) {                 // do all the new trans mode types

               tmode9R2[i] += tmode9[i];
               tmode18R2[i] += tmode18[i];
               tmodeR2[i] = tmode9R2[i] + tmode18R2[i];
            }

            tmodeOldR92 += tmodeOldR9;                      // old trans mode counts
            tmodeOldR182 += tmodeOldR18;

         }         // end of while for MTD

         pstmt1.close();

         //
         //   Grand Total # of Rounds
         //
         totRounds2 = totRounds2 + memRounds2 + gstRounds2 + otherRounds2;     // total # of rounds played

         //
         //  init some work vars
         //
         for (i = 0; i < parm.MAX_Mems; i++) {
            memxRounds9[i] = 0;
            memxRounds18[i] = 0;
         }

         for (i = 0; i < parm.MAX_Mships; i++) {
            mshipxRounds9[i] = 0;
            mshipxRounds18[i] = 0;
         }

         for (i = 0; i < parm.MAX_Guests; i++) {
            gst1Rnds9[i] = 0;
            gst1Rnds18[i] = 0;
         }

         error = "Get Last Month's counts (pstmt3)";

         //
         //  Get Last Month's counts (pstmt3)
         //
         pstmt3.clearParameters();        // clear the parms
         pstmt3.setLong(1, lmsdate);
         pstmt3.setLong(2, lmedate);
         pstmt3.setString(3, courseName);
         rs = pstmt3.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            i2 = 1;
            for (i = 0; i < parm.MAX_Mems; i++) {
               memxr9[i] = rs.getInt("mem" +i2+ "Rounds9");
               memxr18[i] = rs.getInt("mem" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Mships; i++) {
               mshipxr9[i] = rs.getInt("mship" +i2+ "Rounds9");
               mshipxr18[i] = rs.getInt("mship" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Guests; i++) {
               gstr9[i] = rs.getInt("gst" +i2+ "Rounds9");
               gstr18[i] = rs.getInt("gst" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Tmodes; i++) {
               tmode9[i] = rs.getInt("tmode" +i2+ "R9");
               tmode18[i] = rs.getInt("tmode" +i2+ "R18");
               i2++;
            }
            other9 = rs.getInt("otherRounds9");
            other18 = rs.getInt("otherRounds18");
            cart9 = rs.getInt("cartsRounds9");
            cart18 = rs.getInt("cartsRounds18");
            cady9 = rs.getInt("caddyRounds9");
            cady18 = rs.getInt("caddyRounds18");
            pc9 = rs.getInt("pullcartRounds9");
            pc18 = rs.getInt("pullcartRounds18");
            wa9 = rs.getInt("walkRounds9");
            wa18 = rs.getInt("walkRounds18");
            memnshow9 = rs.getInt("memnoshow9");
            memnshow18 = rs.getInt("memnoshow18");
            gstnshow9 = rs.getInt("gstnoshow9");
            gstnshow18 = rs.getInt("gstnoshow18");
            memunk9 = rs.getInt("mem9unknown");
            memunk18 = rs.getInt("mem18unknown");
            mshipunk9 = rs.getInt("mship9unknown");
            mshipunk18 = rs.getInt("mship18unknown");
            tmodeOldR9 = rs.getInt("tmodeOldR9");
            tmodeOldR18 = rs.getInt("tmodeOldR18");

            //
            //  got the stats for one day - add them to the running totals
            //
            //     Get Member Rounds
            //
            for (i = 0; i < parm.MAX_Mems; i++) {

               mem9Rounds4 = mem9Rounds4 + memxr9[i];
               mem18Rounds4 = mem18Rounds4 + memxr18[i];
               memxRounds9[i] = memxRounds9[i] + memxr9[i];        // member 9 hole rounds per mem type
               memxRounds18[i] = memxRounds18[i] + memxr18[i];     // member 18 hole rounds per mem type
               memxRounds4[i] = memxRounds18[i] + memxRounds9[i];  // individual member type totals
            }

            memRounds4 = mem9Rounds4 + mem18Rounds4;                 // rounds by members

            memUnknown9 = memUnknown9 + memunk9;                     // unknown member types
            memUnknown18 = memUnknown18 + memunk18;

            memUnknown1 = memUnknown9 + memUnknown18;

            //
            //  Get membership Rounds
            //
            for (i = 0; i < parm.MAX_Mems; i++) {

               mshipxRounds9[i] = mshipxRounds9[i] + mshipxr9[i];        // 9 hole rounds per mship type
               mshipxRounds18[i] = mshipxRounds18[i] + mshipxr18[i];     // 18 hole rounds per mship type
               mshipxRounds4[i] = mshipxRounds18[i] + mshipxRounds9[i];  // individual membership type totals
            }

            mshipUnknown9 = mshipUnknown9 + mshipunk9;                     // unknown membership types
            mshipUnknown18 = mshipUnknown18 + mshipunk18;
            mshipUnknown1 = mshipUnknown9 + mshipUnknown18;

            //
            //  Get Guest Rounds
            //
            for (i = 0; i < parm.MAX_Guests; i++) {
               gst1Rnds9[i] = gst1Rnds9[i] + gstr9[i];        // guest 9 hole rounds
               gst1Rnds18[i] = gst1Rnds18[i] + gstr18[i];     // guest 18 hole rounds
               gstRnds4[i] = gst1Rnds18[i] + gst1Rnds9[i];    // individual guest totals
               gst9Rounds4 = gst9Rounds4 + gstr9[i];          // total guest 9 hole rounds
               gst18Rounds4 = gst18Rounds4 + gstr18[i];       // total guest 18 hole rounds
            }

            gstRounds4 = gst9Rounds4 + gst18Rounds4;                   // total guest rounds

            //
            //  Get Rounds by Others (not members, not guests)
            //
            other9Rounds4 = other9Rounds4 + other9;
            other18Rounds4 = other18Rounds4 + other18;

            otherRounds4 = other9Rounds4 + other18Rounds4;                 // total Other Rounds

            //
            //  No-Show Rounds by members and guests
            //
            mnshow9Rounds4 = mnshow9Rounds4 + memnshow9;                     // member no-shows
            mnshow18Rounds4 = mnshow18Rounds4 + memnshow18;

            gnshow9Rounds4 = gnshow9Rounds4 + gstnshow9;                     // guest no-shows
            gnshow18Rounds4 = gnshow18Rounds4 + gstnshow18;

            nshow9Rounds4 = mnshow9Rounds4 + gnshow9Rounds4;
            nshow18Rounds4 = mnshow18Rounds4 + gnshow18Rounds4;

            mnshowRounds4 = mnshow9Rounds4 + mnshow18Rounds4;
            gnshowRounds4 = gnshow9Rounds4 + gnshow18Rounds4;
            nshowRounds4 = nshow9Rounds4 + nshow18Rounds4;                // total nshow Rounds

            for (i=0; i<parm.MAX_Tmodes; i++) {                 // do all the new trans mode types

               tmode9R4[i] += tmode9[i];
               tmode18R4[i] += tmode18[i];
               tmodeR4[i] = tmode9R4[i] + tmode18R4[i];
            }

            tmodeOldR94 += tmodeOldR9;                      // old trans mode counts
            tmodeOldR184 += tmodeOldR18;

         }         // end of while for Last Month

         pstmt3.close();

         //
         //   Grand Total # of Rounds
         //
         totRounds4 = totRounds4 + memRounds4 + gstRounds4 + otherRounds4;     // total # of rounds played

      }
      catch (Exception exc) {

         out.println("<BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Exception:" + exc.getMessage());
         out.println("<BR><BR>Error:" + error);
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
         out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" cols=\"4\">");
      } else {
         out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" cols=\"4\">");
      }
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");


      String bgrndcolor = "#336633";      // default
      String fontcolor = "#FFFFFF";      // default

      if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

         bgrndcolor = "#FFFFFF";      // white for excel
         fontcolor = "#000000";      // black for excel
      }
      //
      // add course name header if multi
      //
      if (!courseName.equals( "" )) {

         out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"4\">");
         out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
         out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
         out.println("</font></td></tr>");
      }

      //
      //  Header row
      //
      out.println("<tr bgcolor=\"" +bgrndcolor+ "\">");
         out.println("<td>");
            out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
            out.println("<p align=\"left\"><b>Stat</b></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
            out.println("<p align=\"center\"><b>Last Month</b><br>(" + month + "/" + year + ")</p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
            out.println("<p align=\"center\"><b>Month To Date</b><br>(excludes today)</p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
            out.println("<p align=\"center\"><b>Year To Date</b><br>(excludes today)</p>");
            out.println("</font></td>");

         //
         //  Build the HTML for each stat gathered above
         //
         out.println("</tr><tr>");                       // Grand totals
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\"><br>");
            out.println("<b>Total Rounds Played:</b>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(totRounds4);
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(totRounds2);
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(totRounds1);
            out.println("</b></font></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("</tr><tr>");                     // Total Rounds for Members
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\"><b>Rounds by Members:</b></p>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (memRounds4 < 1 || totRounds4 < 1) {
            out.println(memRounds4);
         } else {
            out.println(memRounds4 + " (" + (memRounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (memRounds2 < 1 || totRounds2 < 1) {
            out.println(memRounds2);
         } else {
            out.println(memRounds2 + " (" + (memRounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (memRounds1 < 1 || totRounds1 < 1) {
            out.println(memRounds1);
         } else {
            out.println(memRounds1 + " (" + (memRounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</b></font></td>");

         found = false;                              // init flag
           
         for (i=0; i<parm.MAX_Mems; i++) {           // do all member types
           
            if (!parm.mem[i].equals( "" )) {

               out.println("</tr><tr>");                     // Rounds for Member Type 
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  if (found == false) {
                     out.println("<u>by Member Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     out.println("<br>");
                  }
                  out.println(parm.mem[i] + ":");
                  out.println("</font></td>");

               found = true;             // indicate a member type has been found (only display heading once)

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
               if (memxRounds4[i] < 1 || memRounds4 < 1) {
                  out.println(memxRounds4[i]);
               } else {
                  out.println(memxRounds4[i] + " (" + (memxRounds4[i] * 100)/memRounds4 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
               if (memxRounds2[i] < 1 || memRounds2 < 1) {
                  out.println(memxRounds2[i]);
               } else {
                  out.println(memxRounds2[i] + " (" + (memxRounds2[i] * 100)/memRounds2 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
               if (memxRounds1[i] < 1 || memRounds1 < 1) {
                  out.println(memxRounds1[i]);
               } else {
                  out.println(memxRounds1[i] + " (" + (memxRounds1[i] * 100)/memRounds1 + "%)");
               }
                  out.println("</font></td>");
            }
         }


         //
         //  check for rounds with no member type (member has been deleted from db since round was played)
         //
         if (memUnknown1 != 0 || memUnknown2 != 0 || memUnknown4 != 0) {

            out.println("</tr><tr>");                     // Rounds for Unknown Member Type
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (memUnknown4 < 1 || memRounds4 < 1) {
               out.println(memUnknown4);
            } else {
               out.println(memUnknown4 + " (" + (memUnknown4 * 100)/memRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (memUnknown2 < 1 || memRounds2 < 1) {
               out.println(memUnknown2);
            } else {
               out.println(memUnknown2 + " (" + (memUnknown2 * 100)/memRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (memUnknown1 < 1 || memRounds1 < 1) {
               out.println(memUnknown1);
            } else {
               out.println(memUnknown1 + " (" + (memUnknown1 * 100)/memRounds1 + "%)");
            }
               out.println("</font></td>");
         }


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         found = false;                                // init flag

         for (i=0; i<parm.MAX_Mships; i++) {           // do all membership types

            if (!parm.mship[i].equals( "" )) {

               out.println("</tr><tr>");                     // Rounds for Membership Type 1
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  if (found == false) {
                     out.println("<u>by Membership Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     out.println("<br>");
                  }
                  out.println(parm.mship[i] + ":");
                  out.println("</font></td>");

               found = true;         // indicate mship found - no heading needed
                 
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
               if (mshipxRounds4[i] < 1 || memRounds4 < 1) {
                  out.println(mshipxRounds4[i]);
               } else {
                  out.println(mshipxRounds4[i] + " (" + (mshipxRounds4[i] * 100)/memRounds4 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
               if (mshipxRounds2[i] < 1 || memRounds2 < 1) {
                  out.println(mshipxRounds2[i]);
               } else {
                  out.println(mshipxRounds2[i] + " (" + (mshipxRounds2[i] * 100)/memRounds2 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
               if (mshipxRounds1[i] < 1 || memRounds1 < 1) {
                  out.println(mshipxRounds1[i]);
               } else {
                  out.println(mshipxRounds1[i] + " (" + (mshipxRounds1[i] * 100)/memRounds1 + "%)");
               }
                  out.println("</font></td>");
            }
         }


         //
         //  check for rounds with no member type (member has been deleted from db since round was played)
         //
         if (mshipUnknown1 != 0 || mshipUnknown2 != 0 || mshipUnknown4 != 0) {

            out.println("</tr><tr>");                     // Rounds for Unknown Membership Type
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (mshipUnknown4 < 1 || memRounds4 < 1) {
               out.println(mshipUnknown4);
            } else {
               out.println(mshipUnknown4 + " (" + (mshipUnknown4 * 100)/memRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (mshipUnknown2 < 1 || memRounds2 < 1) {
               out.println(mshipUnknown2);
            } else {
               out.println(mshipUnknown2 + " (" + (mshipUnknown2 * 100)/memRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (mshipUnknown1 < 1 || memRounds1 < 1) {
               out.println(mshipUnknown1);
            } else {
               out.println(mshipUnknown1 + " (" + (mshipUnknown1 * 100)/memRounds1 + "%)");
            }
               out.println("</font></td>");
         }


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                     // 9 Hole Rounds for Members
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Member 9 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem9Rounds4 < 1 || totRounds4 < 1) {
            out.println(mem9Rounds4);
         } else {
            out.println(mem9Rounds4 + " (" + (mem9Rounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem9Rounds2 < 1 || totRounds2 < 1) {
            out.println(mem9Rounds2);
         } else {
            out.println(mem9Rounds2 + " (" + (mem9Rounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem9Rounds1 < 1 || totRounds1 < 1) {
            out.println(mem9Rounds1);
         } else {
            out.println(mem9Rounds1 + " (" + (mem9Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // 18 Hole Rounds for Members
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Member 18 Hole Rounds:");
            out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem18Rounds4 < 1 || totRounds4 < 1) {
            out.println(mem18Rounds4);
         } else {
            out.println(mem18Rounds4 + " (" + (mem18Rounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem18Rounds2 < 1 || totRounds2 < 1) {
            out.println(mem18Rounds2);
         } else {
            out.println(mem18Rounds2 + " (" + (mem18Rounds2 * 100)/totRounds2 + "%)");
         }
         out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem18Rounds1 < 1 || totRounds1 < 1) {
            out.println(mem18Rounds1);
         } else {
            out.println(mem18Rounds1 + " (" + (mem18Rounds1 * 100)/totRounds1 + "%)");
         }
         out.println("</font><br></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                      // Total Rounds by Guests
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\"><b>Rounds by Guests:</b></p>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (gstRounds4 < 1 || totRounds4 < 1) {
            out.println(gstRounds4);
         } else {
            out.println(gstRounds4 + " (" + (gstRounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (gstRounds2 < 1 || totRounds2 < 1) {
            out.println(gstRounds2);
         } else {
            out.println(gstRounds2 + " (" + (gstRounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (gstRounds1 < 1 || totRounds1 < 1) {
            out.println(gstRounds1);
         } else {
            out.println(gstRounds1 + " (" + (gstRounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</b></font></td>");

         for (i = 0; i < parm.MAX_Guests; i++) {          // chack all 36 guest types

            if (!parm.guest[i].equals( "" ) && !parm.guest[i].equals( "$@#!^&*" )) {

               out.println("</tr><tr>");                     // Rounds for Guest Type 1
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\">" + parm.guest[i] + ":</p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gstRnds4[i] < 1 || gstRounds4 < 1) {
                  out.println(gstRnds4[i]);
               } else {
                  out.println(gstRnds4[i] + " (" + (gstRnds4[i] * 100)/gstRounds4 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gstRnds2[i] < 1 || gstRounds2 < 1) {
                  out.println(gstRnds2[i]);
               } else {
                  out.println(gstRnds2[i] + " (" + (gstRnds2[i] * 100)/gstRounds2 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gstRnds1[i] < 1 || gstRounds1 < 1) {
                  out.println(gstRnds1[i]);
               } else {
                  out.println(gstRnds1[i] + " (" + (gstRnds1[i] * 100)/gstRounds1 + "%)");
               }
                  out.println("</font></td>");
            }
         }

         out.println("</tr><tr>");                     // 9 Hole Rounds for Guests
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\"><br>");
            out.println("Guest 9 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br>");
         if (gst9Rounds4 < 1 || totRounds4 < 1) {
            out.println(gst9Rounds4);
         } else {
            out.println(gst9Rounds4 + " (" + (gst9Rounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br>");
         if (gst9Rounds2 < 1 || totRounds2 < 1) {
            out.println(gst9Rounds2);
         } else {
            out.println(gst9Rounds2 + " (" + (gst9Rounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br>");
         if (gst9Rounds1 < 1 || totRounds1 < 1) {
            out.println(gst9Rounds1);
         } else {
            out.println(gst9Rounds1 + " (" + (gst9Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // 18 Hole Rounds for Guests
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Guest 18 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (gst18Rounds4 < 1 || totRounds4 < 1) {
            out.println(gst18Rounds4);
         } else {
            out.println(gst18Rounds4 + " (" + (gst18Rounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (gst18Rounds2 < 1 || totRounds2 < 1) {
            out.println(gst18Rounds2);
         } else {
            out.println(gst18Rounds2 + " (" + (gst18Rounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (gst18Rounds1 < 1 || totRounds1 < 1) {
            out.println(gst18Rounds1);
         } else {
            out.println(gst18Rounds1 + " (" + (gst18Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         //
         //  Only display 'Others' if there were some found (non-members, non-guests)
         //
         if (otherRounds1 > 0 || otherRounds2 > 0 || otherRounds4 > 0) {

            out.println("</tr><tr>");                      // Total Rounds by Others
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\"><b>Rounds by Others:</b></p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><b>");
            if (otherRounds4 < 1 || totRounds4 < 1) {
               out.println(otherRounds4);
            } else {
               out.println(otherRounds4 + " (" + (otherRounds4 * 100)/totRounds4 + "%)");
            }
               out.println("</b></font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><b>");
            if (otherRounds2 < 1 || totRounds2 < 1) {
               out.println(otherRounds2);
            } else {
               out.println(otherRounds2 + " (" + (otherRounds2 * 100)/totRounds2 + "%)");
            }
               out.println("</b></font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><b>");
            if (otherRounds1 < 1 || totRounds1 < 1) {
               out.println(otherRounds1);
            } else {
               out.println(otherRounds1 + " (" + (otherRounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</b></font></td>");

            out.println("</tr><tr>");                     // 9 Hole Rounds for Others
            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Other 9 Hole Rounds:");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other9Rounds4 < 1 || totRounds4 < 1) {
               out.println(other9Rounds4);
            } else {
               out.println(other9Rounds4 + " (" + (other9Rounds4 * 100)/totRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other9Rounds2 < 1 || totRounds2 < 1) {
               out.println(other9Rounds2);
            } else {
               out.println(other9Rounds2 + " (" + (other9Rounds2 * 100)/totRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other9Rounds1 < 1 || totRounds1 < 1) {
               out.println(other9Rounds1);
            } else {
               out.println(other9Rounds1 + " (" + (other9Rounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");

            out.println("</tr><tr>");                     // 18 Hole Rounds for Others
            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Other 18 Hole Rounds:");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other18Rounds4 < 1 || totRounds4 < 1) {
               out.println(other18Rounds4);
            } else {
               out.println(other18Rounds4 + " (" + (other18Rounds4 * 100)/totRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other18Rounds2 < 1 || totRounds2 < 1) {
               out.println(other18Rounds2);
            } else {
               out.println(other18Rounds2 + " (" + (other18Rounds2 * 100)/totRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other18Rounds1 < 1 || totRounds1 < 1) {
               out.println(other18Rounds1);
            } else {
               out.println(other18Rounds1 + " (" + (other18Rounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");


            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");
         }
         out.println("</tr>");

         //
         //  Check all the Transportation Modes - now 16 configurable modes (V4)
         //
         for (i=0; i<parm.MAX_Tmodes; i++) {

            if (tmodeR1[i] > 0 || tmodeR2[i] > 0 || tmodeR4[i] > 0) {

               out.println("<tr>");
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\"><b>" +parmc.tmode[i]+ " Rounds:</b></p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (tmodeR4[i] < 1 || totRounds4 < 1) {
                  out.println(tmodeR4[i]);
               } else {
                  out.println(tmodeR4[i] + " (" + (tmodeR4[i] * 100)/totRounds4 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (tmodeR2[i] < 1 || totRounds2 < 1) {
                  out.println(tmodeR2[i]);
               } else {
                  out.println(tmodeR2[i] + " (" + (tmodeR2[i] * 100)/totRounds2 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (tmodeR1[i] < 1 || totRounds1 < 1) {
                  out.println(tmodeR1[i]);
               } else {
                  out.println(tmodeR1[i] + " (" + (tmodeR1[i] * 100)/totRounds1 + "%)");
               }
                  out.println("</font></td>");
               out.println("</tr>");
            }
         }

         if (tmodeOldR91 > 0 || tmodeOldR92 > 0 || tmodeOldR94 > 0 ||
             tmodeOldR181 > 0 || tmodeOldR182 > 0 || tmodeOldR184 > 0) {

            out.println("<tr>");
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\"><b>Rounds From Modes No Longer Used:</b></p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if ((tmodeOldR94 + tmodeOldR184) < 1 || totRounds4 < 1) {
               out.println(tmodeOldR94 + tmodeOldR184);
            } else {
               out.println((tmodeOldR94 + tmodeOldR184) + " (" + ((tmodeOldR94 + tmodeOldR184) * 100)/totRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if ((tmodeOldR92 + tmodeOldR182) < 1 || totRounds2 < 1) {
               out.println(tmodeOldR92 + tmodeOldR182);
            } else {
               out.println((tmodeOldR92 + tmodeOldR182) + " (" + ((tmodeOldR92 + tmodeOldR182) * 100)/totRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if ((tmodeOldR91 + tmodeOldR181) < 1 || totRounds1 < 1) {
               out.println(tmodeOldR91 + tmodeOldR181);
            } else {
               out.println((tmodeOldR91 + tmodeOldR181) + " (" + ((tmodeOldR91 + tmodeOldR181) * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");
            out.println("</tr>");
         }

         out.println("<tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("</tr><tr>");
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<b>Number of Member No-Shows:</b>");
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(nshowRounds4);
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(nshowRounds2);
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(nshowRounds1);
            out.println("<br></font></td>");

      out.println("</font></tr></table><br>");
              
      count--;                         // decrement number of courses
      index++;
      courseName = course[index];      // get next course name, if more

   }       // end of while Courses - do all courses
     
   if (req.getParameter("excel") == null) {     // if normal request

      out.println("</td></tr></table>");                // end of main page table & column

      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   }

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");

 }  // end of goRounds


 // *********************************************************
 //  Report Type = Number of Rounds Played Today (today=yes)
 // *********************************************************

 private void goToday(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String club = (String)sess.getAttribute("club");      // get club name
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //   Get multi option, member types, and guest types
   //
   try {

      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception ignore) {
   }

   boolean guest = false;

   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;

   int memRounds3 = 0;         // Today's counts
   int [] memxRounds3 = new int [parm.MAX_Mems];       // use arrays for the mem types
     
   int mem9Rounds3 = 0;
   int mem18Rounds3 = 0;
   int otherRounds3 = 0;
   int other9Rounds3 = 0;
   int other18Rounds3 = 0;
     
   int gstRounds3 = 0;

   int [] gstRnds3 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gst9Rounds3 = 0;
   int gst18Rounds3 = 0;

   int [] mshipxRounds3 = new int [parm.MAX_Mships];       // use arrays for the mship types
     
   int totRounds3 = 0;
   int nshowRounds3 = 0;
   int memUnknown3 = 0;
   int mshipUnknown3 = 0;

   int [] tmodeR3 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R3 = new int [parm.MAX_Tmodes];
   int [] tmode18R3 = new int [parm.MAX_Tmodes];

   long edate = 0;                             // today's date
   int year = 0;
   int month = 0;
   int day = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;

   int multi = 0;                 // multiple course support
   int index = 0;
   int i = 0;
   int count = 0;                 // number of courses

   //
   //  Array to hold the course names
   //
   String [] course = new String [20];                     // max of 20 courses per club

   String courseName = "";        // course names

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";

   String error = "None";
     
   boolean found = false;

   //
   //  Get today's date and current time and calculate date & time values
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_hour = cal.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
   int cal_min = cal.get(Calendar.MINUTE);

   int curr_time = (cal_hour * 100) + cal_min;    // get time in hhmm format

   curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

   if (curr_time < 0) {          // if negative, then we went back or ahead one day

      curr_time = 0 - curr_time;        // convert back to positive value

      if (curr_time < 100) {           // if hour is zero, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
      }
   }

   month = month + 1;                           // month starts at zero

   edate = year * 10000;                        // create a edate field of yyyymmdd (for today)
   edate = edate + (month * 100);
   edate = edate + day;                         // date = yyyymmdd (for comparisons)

   multi = parm.multi;

   //
   //   Remove any guest types that are null - for tests below
   //
   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

   //
   // Check for multiple courses
   //
   count = 1;                  // init to 1 course

   if (multi != 0) {           // if multiple courses supported for this club

      while (index< 20) {

         course[index] = "";       // init the course array
         index++;
      }

      index = 0;

      try {

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
         count = index;                      // number of courses

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
   out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      out.println("<p><b>Course Statistics for Today</b><br></font><font size=\"2\">");
      out.println("<b>Note:</b> Percentages are rounded down to whole number.<br>");
      out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
      out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
      out.println("</font>");

   courseName = "";            // init as not multi
   index = 0;

   if (multi != 0) {           // if multiple courses supported for this club

      courseName = course[index];      // get first course name
   }

   //
   // execute searches and display for each course
   //
   while (count > 0) {

      //
      //  init count fields for each course
      //
      for (i = 0; i < parm.MAX_Mems; i++) {
         memxRounds3[i] = 0;
      }

      memRounds3 = 0;         // Today's counts
      mem9Rounds3 = 0;
      mem18Rounds3 = 0;
      otherRounds3 = 0;
      other9Rounds3 = 0;
      other18Rounds3 = 0;

      for (i = 0; i < parm.MAX_Guests; i++) {
         gstRnds3[i] = 0;
      }

      gstRounds3 = 0;
      gst9Rounds3 = 0;
      gst18Rounds3 = 0;

      for (i = 0; i < parm.MAX_Mships; i++) {
         mshipxRounds3[i] = 0;
      }

      totRounds3 = 0;
      nshowRounds3 = 0;
      memUnknown3 = 0;
      mshipUnknown3 = 0;

      //
      //  Init the Modes of Trans arrays
      //
      for (i = 0; i < parm.MAX_Tmodes; i++) {
         tmodeR3[i] = 0;
         tmode9R3[i] = 0;
         tmode18R3[i] = 0;
      }

      //
      // use the dates provided to search the tee times tables
      //
      try {

         //
         //  Get the System Parameters for this Course
         //
         getParms.getCourse(con, parmc, courseName);

         //
         //  Statement for Today's counts (from teecurr)
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "SELECT player1, player2, player3, player4, username1, username2, username3, username4, " +
            "p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, " +
            "player5, username5, p5cw, show5, p91, p92, p93, p94, p95 " +
            "FROM teecurr2 WHERE date = ? AND time <= ? AND courseName = ?");

         error = "Get Today's counts";

         //
         //  Get Today's counts - use teecurr for today
         //
         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, edate);
         pstmt2.setInt(2, curr_time);
         pstmt2.setString(3, courseName);
         rs = pstmt2.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            player1 = rs.getString(1);
            player2 = rs.getString(2);
            player3 = rs.getString(3);
            player4 = rs.getString(4);
            username1 = rs.getString(5);
            username2 = rs.getString(6);
            username3 = rs.getString(7);
            username4 = rs.getString(8);
            p1cw = rs.getString(9);
            p2cw = rs.getString(10);
            p3cw = rs.getString(11);
            p4cw = rs.getString(12);
            show1 = rs.getInt(13);
            show2 = rs.getInt(14);
            show3 = rs.getInt(15);
            show4 = rs.getInt(16);
            player5 = rs.getString(17);
            username5 = rs.getString(18);
            p5cw = rs.getString(19);
            show5 = rs.getInt(20);
            p91 = rs.getInt(21);
            p92 = rs.getInt(22);
            p93 = rs.getInt(23);
            p94 = rs.getInt(24);
            p95 = rs.getInt(25);

            if ((!player1.equals( "" )) && (!player1.equalsIgnoreCase( "x"))) {

               guest = false;
               i = 0;

               ploop1:
               while (i < parm.MAX_Guests) {

                  if (player1.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop1;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show1 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p91 == 1) {                          // 9 holes

                        gst9Rounds3++;

                     } else {

                        gst18Rounds3++;                       // 18 holes
                     }

                  }
               } else {

                  if (show1 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username1.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p91 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p91 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }
                 
               // 
               // check all modes of trans
               //
               i = 0;
               loop1a:
               while (i < parm.MAX_Tmodes) {
                  if ((p1cw.equals( parmc.tmodea[i] )) && (show1 != 0)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop1a;
                  }
                  i++;
               }
            }
            if ((!player2.equals( "" )) && (!player2.equalsIgnoreCase( "x"))) {

               guest = false;
               i = 0;

               ploop2:
               while (i < parm.MAX_Guests) {

                  if (player2.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop2;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show2 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p92 == 1) {                          // 9 holes

                        gst9Rounds3++;

                     } else {

                        gst18Rounds3++;                       // 18 holes
                     }
                  }
               } else {

                  if (show2 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username2.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p92 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p92 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop2a:
               while (i < parm.MAX_Tmodes) {
                  if ((p2cw.equals( parmc.tmodea[i] )) && (show2 != 0)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop2a;
                  }
                  i++;
               }
            }
            if ((!player3.equals( "" )) && (!player3.equalsIgnoreCase( "x"))) {

               guest = false;
               i = 0;

               ploop3:
               while (i < parm.MAX_Guests) {

                  if (player3.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop3;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show3 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p93 == 1) {                          // 9 holes

                        gst9Rounds3++;

                     } else {

                        gst18Rounds3++;                       // 18 holes
                     }
                  }
               } else {

                  if (show3 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username3.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p93 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p93 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop3a:
               while (i < parm.MAX_Tmodes) {
                  if ((p3cw.equals( parmc.tmodea[i] )) && (show3 != 0)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop3a;
                  }
                  i++;
               }
            }
            if ((!player4.equals( "" )) && (!player4.equalsIgnoreCase( "x" ))) {

               guest = false;
               i = 0;

               ploop4:
               while (i < parm.MAX_Guests) {

                  if (player4.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop4;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show4 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p94 == 1) {                          // 9 holes

                        gst9Rounds3++;

                     } else {

                        gst18Rounds3++;                       // 18 holes
                     }
                  }
               } else {

                  if (show4 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username4.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p94 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p94 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop4a:
               while (i < parm.MAX_Tmodes) {
                  if ((p4cw.equals( parmc.tmodea[i] )) && (show4 != 0)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop4a;
                  }
                  i++;
               }
            }
            if ((!player5.equals( "" )) && (!player5.equalsIgnoreCase( "x" ))) {

               guest = false;
               i = 0;

               ploop5:
               while (i < parm.MAX_Guests) {

                  if (player5.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop5;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show5 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p95 == 1) {                          // 9 holes

                        gst9Rounds3++;

                     } else {

                        gst18Rounds3++;                       // 18 holes
                     }
                  }
               } else {

                  if (show5 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username5.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p95 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p95 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop5a:
               while (i < parm.MAX_Tmodes) {
                  if ((p5cw.equals( parmc.tmodea[i] )) && (show5 != 0)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop5a;
                  }
                  i++;
               }
            }

            error = "Count rounds per Member Type - 3";

            //
            // Count rounds per Member Type
            //
            user1 = "";        // init username fields
            user2 = "";
            user3 = "";
            user4 = "";
            user5 = "";
            if ((!username1.equals( "" )) && (show1 == 1)) {

               user1 = username1;
            }
            if ((!username2.equals( "" )) && (show2 == 1)) {

               user2 = username2;
            }
            if ((!username3.equals( "" )) && (show3 == 1)) {

               user3 = username3;
            }
            if ((!username4.equals( "" )) && (show4 == 1)) {

               user4 = username4;
            }
            if ((!username5.equals( "" )) && (show5 == 1)) {

               user5 = username5;
            }

            if (!user1.equals( "" ) || !user2.equals( "" ) || !user3.equals( "" ) ||
                !user4.equals( "" ) || !user5.equals( "" )) {

               for (i=0; i<parm.MAX_Mems; i++) {         // check all mem types

                  if (!parm.mem[i].equals( "" )) {          

                     //
                     //  Statement for Member Types
                     //
                     PreparedStatement pstmt4 = con.prepareStatement (
                        "SELECT password FROM member2b WHERE " +
                        "(username = ? OR username = ? OR username = ? OR username = ? OR username = ?) AND m_type = ?");

                     pstmt4.clearParameters();
                     pstmt4.setString(1, user1);
                     pstmt4.setString(2, user2);
                     pstmt4.setString(3, user3);
                     pstmt4.setString(4, user4);
                     pstmt4.setString(5, user5);
                     pstmt4.setString(6, parm.mem[i]);
                     rs2 = pstmt4.executeQuery();

                     while (rs2.next()) {

                        memxRounds3[i]++;  
                     }
                     pstmt4.close();
                  }
               }
                 
               error =  "Count rounds per Membership Type - 3";

               //
               // Count rounds per Membership Type
               //
               for (i=0; i<parm.MAX_Mships; i++) {         // check all mem types

                  if (!parm.mship[i].equals( "" )) {          

                     //
                     //  Statement for Membership Types
                     //
                     PreparedStatement pstmt5 = con.prepareStatement (
                        "SELECT password FROM member2b WHERE " +
                        "(username = ? OR username = ? OR username = ? OR username = ? OR username = ?) AND m_ship = ?");

                     pstmt5.clearParameters();
                     pstmt5.setString(1, user1);
                     pstmt5.setString(2, user2);
                     pstmt5.setString(3, user3);
                     pstmt5.setString(4, user4);
                     pstmt5.setString(5, user5);
                     pstmt5.setString(6, parm.mship[i]);
                     rs2 = pstmt5.executeQuery();

                     while (rs2.next()) {

                        mshipxRounds3[i]++;       
                     }
                     pstmt5.close();
                  }
               }
            }
         }         // end of while for Today

         pstmt2.close();

      }
      catch (Exception exc) {

         out.println("<BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Exception:" + exc.getMessage());
         out.println("<BR><BR>Error:" + error);
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

               //
               // add course name header if multi
               //
               if (!courseName.equals( "" )) {

                  out.println("<tr bgcolor=\"#336633\"><td colspan=\"2\">");
                  out.println("<font color=\"#FFFFFF\" size=\"3\">");
                  out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
                  out.println("</font></td></tr>");
               }

               //
               //  Header row
               //
               out.println("<tr bgcolor=\"#336633\"><td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"left\"><b>Stat</b></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><b>Today</b> (thus far)</p>");
                     out.println("</font></td>");

               //
               //  Build the HTML for each stat gathered above
               //
               out.println("</tr><tr>");                       // Grand totals
               out.println("<td align=\"left\">");
                  out.println("<font size=\"2\"><br>");
                  out.println("<b>Total Rounds Played:</b>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br><b>");
                  out.println(totRounds3);
                  out.println("</b></font></td>");

               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("</tr><tr>");                     // Total Rounds for Members
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\"><b>Rounds by Members:</b></p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><b>");
               if (memRounds3 < 1 || totRounds3 < 1) {
                  out.println(memRounds3);
               } else {
                  out.println(memRounds3 + " (" + (memRounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</b></font></td>");


               //
               // Rounds per Member Type
               //
               found = false;
               int memtemp = 0;
               for (i=0; i<parm.MAX_Mems; i++) {         // check all mem types

                  if (!parm.mem[i].equals( "" )) {

                     out.println("</tr><tr>");                     // Rounds for Member Type
                     out.println("<td align=\"right\">");
                     out.println("<font size=\"2\">");
                     if (found == false) {
                        out.println("<u>by Member Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("<br>");
                     }
                     out.println(parm.mem[i] + ":");
                     out.println("</font></td>");
 
                     found = true;

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\"><br>");
                     if (memxRounds3[i] < 1 || memRounds3 < 1) {
                        out.println(memxRounds3[i]);
                     } else {
                        out.println(memxRounds3[i] + " (" + (memxRounds3[i] * 100)/memRounds3 + "%)");
                     }
                     out.println("</font></td>");
                       
                     memtemp = memtemp + memxRounds3[i];    // keep total
                  }
               }


               //
               //  check for rounds with no member type (member has been deleted from db since round was played)
               //
               memUnknown3 = memRounds3 - memtemp;

               if (memUnknown3 != 0) {

                  out.println("</tr><tr>");                     // Rounds for Unknown Member Type
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                  if (memUnknown3 < 1 || memRounds3 < 1) {
                     out.println(memUnknown3);
                  } else {
                     out.println(memUnknown3 + " (" + (memUnknown3 * 100)/memRounds3 + "%)");
                  }
                     out.println("</font></td>");
               }


               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");


               //
               // Rounds per Membership Type
               //
               found = false;
               int mshiptemp = 0;
               for (i=0; i<parm.MAX_Mships; i++) {         // check all mship types

                  if (!parm.mship[i].equals( "" )) {

                     out.println("</tr><tr>");                     // Rounds for Membership Type
                     out.println("<td align=\"right\">");
                     out.println("<font size=\"2\">");
                     if (found == false) {
                        out.println("<u>by Membership Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("<br>");
                     }
                     out.println(parm.mship[i] + ":");
                     out.println("</font></td>");

                     found = true;

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\"><br>");
                     if (mshipxRounds3[i] < 1 || memRounds3 < 1) {
                        out.println(mshipxRounds3[i]);
                     } else {
                        out.println(mshipxRounds3[i] + " (" + (mshipxRounds3[i] * 100)/memRounds3 + "%)");
                     }
                     out.println("</font></td>");
                       
                     mshiptemp = mshiptemp + mshipxRounds3[i];   // keep total
                  }
               }


               //
               //  check for rounds with no member type (member has been deleted from db since round was played)
               //
               mshipUnknown3 = memRounds3 - mshiptemp;

               if (mshipUnknown3 != 0) {

                  out.println("</tr><tr>");                     // Rounds for Unknown Membership Type
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                  if (mshipUnknown3 < 1 || memRounds3 < 1) {
                     out.println(mshipUnknown3);
                  } else {
                     out.println(mshipUnknown3 + " (" + (mshipUnknown3 * 100)/memRounds3 + "%)");
                  }
                     out.println("</font></td>");
               }


               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");


               out.println("</tr><tr>");                     // 9 Hole Rounds for Members
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  out.println("Member 9 Hole Rounds:");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (mem9Rounds3 < 1 || totRounds3 < 1) {
                  out.println(mem9Rounds3);
               } else {
                  out.println(mem9Rounds3 + " (" + (mem9Rounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</font></td>");

               out.println("</tr><tr>");                     // 18 Hole Rounds for Members
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  out.println("Member 18 Hole Rounds:");
                  out.println("</font><br></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (mem18Rounds3 < 1 || totRounds3 < 1) {
                  out.println(mem18Rounds3);
               } else {
                  out.println(mem18Rounds3 + " (" + (mem18Rounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</font><br></td>");

               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("</tr><tr>");                      // Total Rounds by Guests
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\"><b>Rounds by Guests:</b></p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><b>");
               if (gstRounds3 < 1 || totRounds3 < 1) {
                  out.println(gstRounds3);
               } else {
                  out.println(gstRounds3 + " (" + (gstRounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</b></font></td>");

               for (i = 0; i < parm.MAX_Guests; i++) {          // chack all 36 guest types

                  if (!parm.guest[i].equals( "" ) && !parm.guest[i].equals( "$@#!^&*" )) {

                     out.println("</tr><tr>");                     // Rounds for Guest Type
                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"right\">" + parm.guest[i] + ":</p>");
                        out.println("</font></td>");

                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                     if (gstRnds3[i] < 1 || gstRounds3 < 1) {
                        out.println(gstRnds3[i]);
                     } else {
                        out.println(gstRnds3[i] + " (" + (gstRnds3[i] * 100)/gstRounds3 + "%)");
                     }
                        out.println("</font></td>");
                  }
               }

               out.println("</tr><tr>");                     // 9 Hole Rounds for Guests
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\"><br>");
                  out.println("Guest 9 Hole Rounds:");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
               if (gst9Rounds3 < 1 || totRounds3 < 1) {
                  out.println(gst9Rounds3);
               } else {
                  out.println(gst9Rounds3 + " (" + (gst9Rounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</font></td>");

               out.println("</tr><tr>");                     // 18 Hole Rounds for Guests
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  out.println("Guest 18 Hole Rounds:");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gst18Rounds3 < 1 || totRounds3 < 1) {
                  out.println(gst18Rounds3);
               } else {
                  out.println(gst18Rounds3 + " (" + (gst18Rounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</font></td>");

               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               //
               //  Only display 'Others' if there were some found (non-members, non-guests)
               //
               if (otherRounds3 > 0) {

                  out.println("</tr><tr>");                      // Total Rounds by Others
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"left\"><b>Rounds by Others:</b></p>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\"><b>");
                  if (otherRounds3 < 1 || totRounds3 < 1) {
                     out.println(otherRounds3);
                  } else {
                     out.println(otherRounds3 + " (" + (otherRounds3 * 100)/totRounds3 + "%)");
                  }
                     out.println("</b></font></td>");

                  out.println("</tr><tr>");                     // 9 Hole Rounds for Others
                  out.println("<td align=\"right\">");
                     out.println("<font size=\"2\">");
                     out.println("Other 9 Hole Rounds:");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                  if (other9Rounds3 < 1 || totRounds3 < 1) {
                     out.println(other9Rounds3);
                  } else {
                     out.println(other9Rounds3 + " (" + (other9Rounds3 * 100)/totRounds3 + "%)");
                  }
                     out.println("</font></td>");

                  out.println("</tr><tr>");                     // 18 Hole Rounds for Others
                  out.println("<td align=\"right\">");
                     out.println("<font size=\"2\">");
                     out.println("Other 18 Hole Rounds:");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                  if (other18Rounds3 < 1 || totRounds3 < 1) {
                     out.println(other18Rounds3);
                  } else {
                     out.println(other18Rounds3 + " (" + (other18Rounds3 * 100)/totRounds3 + "%)");
                  }
                     out.println("</font></td>");

                  out.println("</tr><tr>");                          // blank row for divider
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">&nbsp;");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">&nbsp;");
                     out.println("</font></td>");
               }
               out.println("</tr>");


               //
               //  Check all the Transportation Modes - now 16 configurable modes (V4)
               //
               for (i=0; i<parm.MAX_Tmodes; i++) {

                  if (tmodeR3[i] > 0) {

                     out.println("<tr>");
                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"left\"><b>" +parmc.tmode[i]+ " Rounds:</b></p>");
                        out.println("</font></td>");

                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                     if (tmodeR3[i] < 1 || totRounds3 < 1) {
                        out.println(tmodeR3[i]);
                     } else {
                        out.println(tmodeR3[i] + " (" + (tmodeR3[i] * 100)/totRounds3 + "%)");
                     }
                        out.println("</font></td>");
                     out.println("</tr>");                          // blank row for divider
                  }
               }

               out.println("<tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("</tr><tr>");
               out.println("<td align=\"left\">");
                  out.println("<font size=\"2\">");
                  out.println("<b>Number of Member No-Shows:</b>");
                  out.println("<br></font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println(nshowRounds3);
                  out.println("<br></font></td>");

            out.println("</font></tr></table><br>");

      count--;                         // decrement number of courses
      index++;
      courseName = course[index];      // get next course name, if more

   }       // end of while Courses - do all courses

   out.println("</td></tr></table>");                // end of main page table & column

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");

 }  // end of goRounds


 // *********************************************************
 //  Report Type = Tee Times (proshop vs members)
 // *********************************************************

 private void goTeetimes(HttpServletRequest req, PrintWriter out, Connection con) {

   ResultSet rs = null;

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);


   long proNewlmn = 0;         // pro values
   long proModlmn = 0;
   long proNewthmn = 0;
   long proModthmn = 0;
   long proNewthyr = 0;
   long proModthyr = 0;
     
   long memNewlmn = 0;         // member values
   long memModlmn = 0;
   long memNewthmn = 0;
   long memModthmn = 0;
   long memNewthyr = 0;
   long memModthyr = 0;
     
   long hotelNewlmn = 0;         // hotel values
   long hotelModlmn = 0;
   long hotelNewthmn = 0;
   long hotelModthmn = 0;
   long hotelNewthyr = 0;
   long hotelModthyr = 0;

   long totProlmn = 0;         // sub-totals
   long totProthmn = 0;
   long totProthyr = 0;
   long totMemlmn = 0;
   long totMemthmn = 0;
   long totMemthyr = 0;
   long totHotellmn = 0;
   long totHotelthmn = 0;
   long totHotelthyr = 0;

   long totLastmn = 0;         // grand totals
   long totThismn = 0;
   long totThisyr = 0;
     
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int hotelNew = 0;
   int hotelMod = 0;
   int year = 0;
   int month = 0;
   int lastMonth = 0;
   int lastMonthYr = 0;
   int hotel = 0;

   //
   //  Get today's date and calculate date & time values
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);

   lastMonth = month;      // month starts at zero
   lastMonthYr = year;  
     
   if (month == 0) {       // if current month is Jan
     
      lastMonth = 12;      // last month is Dec
      lastMonthYr--;       // adjust year
   }
     
   month = month + 1;      // adjust current month value

   //
   // use the dates provided to search the tee times tables
   //
   try {

      //
      //  First, see if Hotels are supported
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT hotel " +
         "FROM club5");

      pstmt2.clearParameters();        // clear the parms
      rs = pstmt2.executeQuery();      // execute the prepared stmt for teepast2

      if (rs.next()) {

         hotel = rs.getInt(1);
      }
      pstmt2.close();

      //
      //  Get Last Month counts
      //
      pstmt2 = con.prepareStatement (
         "SELECT proNew, proMod, memNew, memMod, hotelNew, hotelMod " +
         "FROM teepast2 WHERE mm = ? AND yy = ?");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setInt(1, lastMonth);
      pstmt2.setInt(2, lastMonthYr);
      rs = pstmt2.executeQuery();      // execute the prepared stmt for teepast2

      while (rs.next()) {

         proNew = rs.getInt(1);
         proMod = rs.getInt(2);
         memNew = rs.getInt(3);
         memMod = rs.getInt(4);
         hotelNew = rs.getInt(5);
         hotelMod = rs.getInt(6);

         // prevent incorrect count
         if (hotelNew > 1) {
            hotelNew = 1;
         }

         proNewlmn += proNew;       // gather totals for Last Month
         proModlmn += proMod;
         memNewlmn += memNew;
         memModlmn += memMod;
         hotelNewlmn += hotelNew;
         hotelModlmn += hotelMod;
      }
      pstmt2.close();

      //
      //  Get This Month counts
      //
      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT proNew, proMod, memNew, memMod, hotelNew, hotelMod " +
         "FROM teecurr2 WHERE mm = ? AND yy = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, month);
      pstmt1.setInt(2, year);
      rs = pstmt1.executeQuery();      // execute the prepared stmt for teecurr2

      while (rs.next()) {

         proNew = rs.getInt(1);
         proMod = rs.getInt(2);
         memNew = rs.getInt(3);
         memMod = rs.getInt(4);
         hotelNew = rs.getInt(5);
         hotelMod = rs.getInt(6);

         // prevent incorrect count
         if (hotelNew > 1) {
            hotelNew = 1;
         }

         proNewthmn += proNew;       // gather totals for This Month
         proModthmn += proMod;
         memNewthmn += memNew;
         memModthmn += memMod;
         hotelNewthmn += hotelNew;
         hotelModthmn += hotelMod;
      }
      pstmt1.close();

      PreparedStatement pstmt2a = con.prepareStatement (
         "SELECT proNew, proMod, memNew, memMod, hotelNew, hotelMod " +
         "FROM teepast2 WHERE mm = ? AND yy = ?");

      pstmt2a.clearParameters();        // clear the parms
      pstmt2a.setInt(1, month);
      pstmt2a.setInt(2, year);
      rs = pstmt2a.executeQuery();      // execute the prepared stmt for teepast2

      while (rs.next()) {

         proNew = rs.getInt(1);
         proMod = rs.getInt(2);
         memNew = rs.getInt(3);
         memMod = rs.getInt(4);
         hotelNew = rs.getInt(5);
         hotelMod = rs.getInt(6);

         // prevent incorrect count
         if (hotelNew > 1) {
            hotelNew = 1;
         }

         proNewthmn += proNew;       // gather totals for This Month
         proModthmn += proMod;
         memNewthmn += memNew;
         memModthmn += memMod;
         hotelNewthmn += hotelNew;
         hotelModthmn += hotelMod;
      }
      pstmt2a.close();

      //
      //  Get This Year counts
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT proNew, proMod, memNew, memMod, hotelNew, hotelMod " +
         "FROM teecurr2 WHERE yy = ?");

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setInt(1, year);
      rs = pstmt3.executeQuery();      // execute the prepared stmt for teecurr2

      while (rs.next()) {

         proNew = rs.getInt(1);
         proMod = rs.getInt(2);
         memNew = rs.getInt(3);
         memMod = rs.getInt(4);
         hotelNew = rs.getInt(5);
         hotelMod = rs.getInt(6);

         // prevent incorrect count
         if (hotelNew > 1) {
            hotelNew = 1;
         }

         proNewthyr += proNew;       // gather totals for This Year
         proModthyr += proMod;
         memNewthyr += memNew;
         memModthyr += memMod;
         hotelNewthyr += hotelNew;
         hotelModthyr += hotelMod;
      }
      pstmt3.close();

      PreparedStatement pstmt4 = con.prepareStatement (
         "SELECT proNew, proMod, memNew, memMod, hotelNew, hotelMod " +
         "FROM teepast2 WHERE yy = ?");

      pstmt4.clearParameters();        // clear the parms
      pstmt4.setInt(1, year);
      rs = pstmt4.executeQuery();      // execute the prepared stmt for teepast2

      while (rs.next()) {

         proNew = rs.getInt(1);
         proMod = rs.getInt(2);
         memNew = rs.getInt(3);
         memMod = rs.getInt(4);
         hotelNew = rs.getInt(5);
         hotelMod = rs.getInt(6);

         // prevent incorrect count
         if (hotelNew > 1) {
            hotelNew = 1;
         }

         proNewthyr += proNew;       // gather totals for This Year
         proModthyr += proMod;
         memNewthyr += memNew;
         memModthyr += memMod;
         hotelNewthyr += hotelNew;
         hotelModthyr += hotelMod;
      }
      pstmt4.close();

      totProlmn = proNewlmn;                     // Last month Pro sub-totals
      totProthmn = proNewthmn;                   // This month Pro sub-totals
      totProthyr = proNewthyr;                   // This year Pro sub-totals

      totMemlmn = memNewlmn;                     // Last month Mem sub-totals
      totMemthmn = memNewthmn;                   // This month Mem sub-totals
      totMemthyr = memNewthyr;                   // This year Mem sub-totals

      totHotellmn = hotelNewlmn;                 // Last month hotel sub-totals
      totHotelthmn = hotelNewthmn;               // This month hotel sub-totals
      totHotelthyr = hotelNewthyr;               // This year hotel sub-totals

      if (hotel > 0) {             // if hotel supported

         totLastmn = totProlmn + totMemlmn + totHotellmn;          // Last month grand total
         totThismn = totProthmn + totMemthmn + totHotelthmn;       // This month grand total
         totThisyr = totProthyr + totMemthyr + totHotelthyr;       // This year grand total

      } else {                  // no hotels

         totLastmn = totProlmn + totMemlmn;                    // Last month grand total
         totThismn = totProthmn + totMemthmn;                  // This month grand total
         totThisyr = totProthyr + totMemthyr;                  // This year grand total
      }

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Build the HTML page to display search results
   //
   out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      out.println("<p><b>Tee Time Statistics</b></p>");
      out.println("</font>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<tr bgcolor=\"#336633\"><td colspan=\"4\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\">This report identifies how many times the Pro Shop made");
                  if (hotel > 0) {
                     out.println("<br>tee times, Hotel users made tee times and Members made tee times.");
                  } else {
                     out.println("<br>tee times versus how many times Members made tee times.");
                  }
                  out.println("<br><br>Percentages represent new tee times only and");
                  out.println("<br>are rounded down to the nearest whole number.</p>");
                  out.println("</font></td></tr>");

            out.println("<tr bgcolor=\"#336633\"><td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"left\">&nbsp;</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>Last Month</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>This Month</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>This Year</b></p>");
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Pro Shop New Tee Times:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (proNewlmn < 1 || totLastmn < 1) {
                     out.println("<p align=\"center\">" + proNewlmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + proNewlmn + " (" + (proNewlmn * 100)/totLastmn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (proNewthmn < 1 || totThismn < 1) {
                     out.println("<p align=\"center\">" + proNewthmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + proNewthmn + " (" + (proNewthmn * 100)/totThismn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (proNewthyr < 1 || totThisyr < 1) {
                     out.println("<p align=\"center\">" + proNewthyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + proNewthyr + " (" + (proNewthyr * 100)/totThisyr + "%)</p>");
                  }
                  out.println("</font></td>");

            out.println("</tr><tr>");

            if (hotel > 0) {

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Hotel User New Tee Times:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (hotelNewlmn < 1 || totLastmn < 1) {
                     out.println("<p align=\"center\">" + hotelNewlmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + hotelNewlmn + " (" + (hotelNewlmn * 100)/totLastmn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (hotelNewthmn < 1 || totThismn < 1) {
                     out.println("<p align=\"center\">" + hotelNewthmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + hotelNewthmn + " (" + (hotelNewthmn * 100)/totThismn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (hotelNewthyr < 1 || totThisyr < 1) {
                     out.println("<p align=\"center\">" + hotelNewthyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + hotelNewthyr + " (" + (hotelNewthyr * 100)/totThisyr + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("</tr><tr>");
            }
               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Member New Tee Times:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (memNewlmn < 1 || totLastmn < 1) {
                     out.println("<p align=\"center\">" + memNewlmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + memNewlmn + " (" + (memNewlmn * 100)/totLastmn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (memNewthmn < 1 || totThismn < 1) {
                     out.println("<p align=\"center\">" + memNewthmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + memNewthmn + " (" + (memNewthmn * 100)/totThismn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (memNewthyr < 1 || totThisyr < 1) {
                     out.println("<p align=\"center\">" + memNewthyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + memNewthyr + " (" + (memNewthyr * 100)/totThisyr + "%)</p>");
                  }
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><br><b>Total New Tee Times:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + totLastmn + "</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + totThismn + "</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + totThisyr + "</b></p>");
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><br><b>Member Modified Tee Times:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModlmn + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModthmn + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModthyr + "</p>");
                  out.println("</font></td>");

            out.println("</tr>");
         out.println("</font></table>");
      out.println("</td></tr></table>");                // end of main page table & column

      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");

 }  // end of goTeetimes


 // *********************************************************
 //  Report Type = Custom Date Range (custom=yes) - from proshop_reports.htm
 // *********************************************************

 private void goCustom(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   String fname = "";
   String lname = "";
   String mname = "";
   String user = "";

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Prompt user for date range
   //
   out.println(SystemUtils.HeadTitle("Proshop Custom Date Report"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Number of Rounds Played Report</b><br>");
      out.println("<br>Select the date range below.<br>");
      out.println("<b>Note:</b>  Only rounds before today will be included in the counts.<br><br>");
      out.println("Click on <b>Go</b> to generate the report.");
      out.println("</font></td></tr></table><br>");

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_reports\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"custom2\" value=\"yes\"></input>");
            
            out.println("<tr><td>");
               out.println("<font size=\"2\">");
               out.println("<div id=\"awmobject1\">");        // allow menus to show over this box
               out.println("Start Date:&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"smonth\">");
                      out.println("<option selected value=\"01\">JAN</option>");
                      out.println("<option value=\"02\">FEB</option>");
                      out.println("<option value=\"03\">MAR</option>");
                      out.println("<option value=\"04\">APR</option>");
                      out.println("<option value=\"05\">MAY</option>");
                      out.println("<option value=\"06\">JUN</option>");
                      out.println("<option value=\"07\">JUL</option>");
                      out.println("<option value=\"08\">AUG</option>");
                      out.println("<option value=\"09\">SEP</option>");
                      out.println("<option value=\"10\">OCT</option>");
                      out.println("<option value=\"11\">NOV</option>");
                      out.println("<option value=\"12\">DEC</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"sday\">");
                      out.println("<option selected selected value=\"01\">1</option>");
                      out.println("<option value=\"02\">2</option>");
                      out.println("<option value=\"03\">3</option>");
                      out.println("<option value=\"04\">4</option>");
                      out.println("<option value=\"05\">5</option>");
                      out.println("<option value=\"06\">6</option>");
                      out.println("<option value=\"07\">7</option>");
                      out.println("<option value=\"08\">8</option>");
                      out.println("<option value=\"09\">9</option>");
                      out.println("<option value=\"10\">10</option>");
                      out.println("<option value=\"11\">11</option>");
                      out.println("<option value=\"12\">12</option>");
                      out.println("<option value=\"13\">13</option>");
                      out.println("<option value=\"14\">14</option>");
                      out.println("<option value=\"15\">15</option>");
                      out.println("<option value=\"16\">16</option>");
                      out.println("<option value=\"17\">17</option>");
                      out.println("<option value=\"18\">18</option>");
                      out.println("<option value=\"19\">19</option>");
                      out.println("<option value=\"20\">20</option>");
                      out.println("<option value=\"21\">21</option>");
                      out.println("<option value=\"22\">22</option>");
                      out.println("<option value=\"23\">23</option>");
                      out.println("<option value=\"24\">24</option>");
                      out.println("<option value=\"25\">25</option>");
                      out.println("<option value=\"26\">26</option>");
                      out.println("<option value=\"27\">27</option>");
                      out.println("<option value=\"28\">28</option>");
                      out.println("<option value=\"29\">29</option>");
                      out.println("<option value=\"30\">30</option>");
                      out.println("<option value=\"31\">31</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"syear\">");
                      out.println("<option value=\"2002\">2002</option>");
                      out.println("<option selected value=\"2003\">2003</option>");
                      out.println("<option value=\"2004\">2004</option>");
                      out.println("<option value=\"2005\">2005</option>");
                      out.println("<option value=\"2006\">2006</option>");
                      out.println("<option value=\"2007\">2007</option>");
                      out.println("<option value=\"2008\">2008</option>");
                      out.println("<option value=\"2009\">2009</option>");
                      out.println("<option value=\"2010\">2010</option>");
                 out.println("</select></div><br><br>");
                 out.println("<div id=\"awmobject2\">");        // allow menus to show over this box
               out.println("End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"emonth\">");
                      out.println("<option value=\"01\">JAN</option>");
                      out.println("<option value=\"02\">FEB</option>");
                      out.println("<option value=\"03\">MAR</option>");
                      out.println("<option value=\"04\">APR</option>");
                      out.println("<option value=\"05\">MAY</option>");
                      out.println("<option value=\"06\">JUN</option>");
                      out.println("<option value=\"07\">JUL</option>");
                      out.println("<option value=\"08\">AUG</option>");
                      out.println("<option value=\"09\">SEP</option>");
                      out.println("<option value=\"10\">OCT</option>");
                      out.println("<option value=\"11\">NOV</option>");
                      out.println("<option selected value=\"12\">DEC</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eday\">");
                      out.println("<option selected selected value=\"01\">1</option>");
                      out.println("<option value=\"02\">2</option>");
                      out.println("<option value=\"03\">3</option>");
                      out.println("<option value=\"04\">4</option>");
                      out.println("<option value=\"05\">5</option>");
                      out.println("<option value=\"06\">6</option>");
                      out.println("<option value=\"07\">7</option>");
                      out.println("<option value=\"08\">8</option>");
                      out.println("<option value=\"09\">9</option>");
                      out.println("<option value=\"10\">10</option>");
                      out.println("<option value=\"11\">11</option>");
                      out.println("<option value=\"12\">12</option>");
                      out.println("<option value=\"13\">13</option>");
                      out.println("<option value=\"14\">14</option>");
                      out.println("<option value=\"15\">15</option>");
                      out.println("<option value=\"16\">16</option>");
                      out.println("<option value=\"17\">17</option>");
                      out.println("<option value=\"18\">18</option>");
                      out.println("<option value=\"19\">19</option>");
                      out.println("<option value=\"20\">20</option>");
                      out.println("<option value=\"21\">21</option>");
                      out.println("<option value=\"22\">22</option>");
                      out.println("<option value=\"23\">23</option>");
                      out.println("<option value=\"24\">24</option>");
                      out.println("<option value=\"25\">25</option>");
                      out.println("<option value=\"26\">26</option>");
                      out.println("<option value=\"27\">27</option>");
                      out.println("<option value=\"28\">28</option>");
                      out.println("<option value=\"29\">29</option>");
                      out.println("<option value=\"30\">30</option>");
                      out.println("<option value=\"31\">31</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eyear\">");
                      out.println("<option value=\"2002\">2002</option>");
                      out.println("<option selected value=\"2003\">2003</option>");
                      out.println("<option value=\"2004\">2004</option>");
                      out.println("<option value=\"2005\">2005</option>");
                      out.println("<option value=\"2006\">2006</option>");
                      out.println("<option value=\"2007\">2007</option>");
                      out.println("<option value=\"2008\">2008</option>");
                      out.println("<option value=\"2009\">2009</option>");
                      out.println("<option value=\"2010\">2010</option>");
                 out.println("</select></div><br><br>");

      out.println("<p align=\"center\"><input type=\"submit\" value=\"Go\"></p>");
      out.println("</td></tr></table>");
      out.println("</font></td></tr></form></table>");         // end of main page table

      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   out.println("</center></font></body></html>");

 }  // end of Custom


 // *****************************************************************************************
 //  Report Type = Number of Rounds, Custom Date (custom2=yes) - from self (goCustom above)
 // *****************************************************************************************

 private void Custom2(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;


   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String club = (String)sess.getAttribute("club");      // get club name
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  get the club parameters
   //
   try {
      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception e) {
   }

   long sdate = 0;
   long edate = 0;
   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int count = 0;

   int memUnknown = 0;
   int memUnknown9 = 0;
   int memUnknown18 = 0;
   int mshipUnknown = 0;
   int mshipUnknown9 = 0;
   int mshipUnknown18 = 0;

   int [] tmodeR1 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R1 = new int [parm.MAX_Tmodes];
   int [] tmode18R1 = new int [parm.MAX_Tmodes];

   int tmodeOldR91 = 0;
   int tmodeOldR181 = 0;

   int nshowRounds1 = 0;
   int nshow9Rounds1 = 0;
   int nshow18Rounds1 = 0;
   int mnshowRounds1 = 0;
   int mnshow9Rounds1 = 0;
   int mnshow18Rounds1 = 0;
   int gnshowRounds1 = 0;
   int gnshow9Rounds1 = 0;
   int gnshow18Rounds1 = 0;

   int otherRounds1 = 0;
   int other9Rounds1 = 0;
   int other18Rounds1 = 0;

   int totRounds1 = 0;
   int memRounds1 = 0;
     
   int [] memxRounds1 = new int [parm.MAX_Mems];       // use array for the Member types
   int mem9Rounds1 = 0;
   int mem18Rounds1 = 0;

   int [] memxRounds9 = new int [parm.MAX_Mems];
   int [] memxRounds18 = new int [parm.MAX_Mems];

   int [] mshipxRounds1 = new int [parm.MAX_Mships];       // use array for the Membership types
   int mship9Rounds1 = 0;
   int mship18Rounds1 = 0;

   int [] mshipxRounds9 = new int [parm.MAX_Mships];
   int [] mshipxRounds18 = new int [parm.MAX_Mships];

   int [] gstRnds1 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gstRounds1 = 0;
   int gst9Rounds1 = 0;
   int gst18Rounds1 = 0;

   int [] gst1Rnds9 = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] gst1Rnds18 = new int [parm.MAX_Guests];

   int multi = 0;                 // multiple course support
   int index = 0;
   int i = 0;
   int i2 = 0;
   int count2 = 0;                 // number of courses

   //
   //  ints to hold stats from db table
   //
   int [] memxr9 = new int [parm.MAX_Mems];
   int [] memxr18 = new int [parm.MAX_Mems];

   int [] mshipxr9 = new int [parm.MAX_Mships];
   int [] mshipxr18 = new int [parm.MAX_Mships];

   int [] gstr9 = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] gstr18 = new int [parm.MAX_Guests];

   int other9 = 0;
   int other18 = 0;
   int cart9 = 0;
   int cart18 = 0;
   int cady9 = 0;
   int cady18 = 0;
   int pc9 = 0;
   int pc18 = 0;
   int wa9 = 0;
   int wa18 = 0;
   int memnshow9 = 0;
   int memnshow18 = 0;
   int gstnshow9 = 0;
   int gstnshow18 = 0;
   int memunk9 = 0;
   int memunk18 = 0;
   int mshipunk9 = 0;
   int mshipunk18 = 0;

   int tmodeOldR9 = 0;
   int tmodeOldR18 = 0;

   int [] tmode9 = new int [parm.MAX_Tmodes];
   int [] tmode18 = new int [parm.MAX_Tmodes];

   //
   //  Array to hold the course names
   //
   String [] course = new String [20];                     // max of 20 courses per club

   String courseName = "";        // course names

   String error = "None";

   boolean found = false;

   //
   // Process request according to the dates 
   //
   // Get the parameters entered
   //
   String smonth = req.getParameter("smonth");
   String sday = req.getParameter("sday");
   String syear = req.getParameter("syear");

   String emonth = req.getParameter("emonth");
   String eday = req.getParameter("eday");
   String eyear = req.getParameter("eyear");

   //
   //  Convert the string values to int's
   //
   try {
      mm = Integer.parseInt(smonth);
      dd = Integer.parseInt(sday);
      yy = Integer.parseInt(syear);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   sdate = yy * 10000;                            // create a date field of yyyymmdd
   sdate = sdate + (mm * 100);
   sdate = sdate + dd;

   try {
      mm = Integer.parseInt(emonth);
      dd = Integer.parseInt(eday);
      yy = Integer.parseInt(eyear);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   edate = yy * 10000;                            // create a date field of yyyymmdd
   edate = edate + (mm * 100);
   edate = edate + dd;

   //
   //   Get multi option, member types, and guest types
   //
   multi = parm.multi;

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

   count2 = 1;                  // init to 1 course

   //
   //   Check for multiple courses
   //
   if (multi != 0) {           // if multiple courses supported for this club

      while (index< 20) {

         course[index] = "";       // init the course array
         index++;
      }

      index = 0;

      try {

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
         count2 = index;                      // number of courses

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
   out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      out.println("<p><b>Course Statistics</b><br></font><font size=\"2\">");
      out.println("<b>Note:</b> Today's counts are not included. Percentages are rounded down to whole number.<br>");
      out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
      out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
      out.println("</font>");

   courseName = "";            // init as not multi
   index = 0;

   if (multi != 0) {           // if multiple courses supported for this club

      courseName = course[index];      // get first course name
   }

   //
   // execute searches and display for each course
   //
   while (count2 > 0) {

      //
      //  init count fields for each course
      //
      for (i = 0; i < parm.MAX_Mems; i++) {
         memxRounds1[i] = 0;
         memxRounds9[i] = 0;
         memxRounds18[i] = 0;
      }

      for (i = 0; i < parm.MAX_Mships; i++) {
         mshipxRounds1[i] = 0;
         mshipxRounds9[i] = 0;
         mshipxRounds18[i] = 0;
      }

      for (i = 0; i < parm.MAX_Guests; i++) {
         gstRnds1[i] = 0;
         gst1Rnds9[i] = 0;
         gst1Rnds18[i] = 0;
      }

      memRounds1 = 0;
      mem9Rounds1 = 0;
      mem18Rounds1 = 0;
      otherRounds1 = 0;
      other9Rounds1 = 0;
      other18Rounds1 = 0;

      gstRounds1 = 0;
      gst9Rounds1 = 0;
      gst18Rounds1 = 0;
         
      mship9Rounds1 = 0;
      mship18Rounds1 = 0;
      totRounds1 = 0;
      nshowRounds1 = 0;
      nshow9Rounds1 = 0;
      nshow18Rounds1 = 0;
      mnshowRounds1 = 0;
      mnshow9Rounds1 = 0;
      mnshow18Rounds1 = 0;
      gnshowRounds1 = 0;
      gnshow9Rounds1 = 0;
      gnshow18Rounds1 = 0;
      memUnknown = 0;
      memUnknown9 = 0;
      memUnknown18 = 0;
      mshipUnknown = 0;
      mshipUnknown9 = 0;
      mshipUnknown18 = 0;

      //
      //  Init the Modes of Trans arrays
      //
      for (i = 0; i < parm.MAX_Tmodes; i++) {
         tmodeR1[i] = 0;
         tmode9R1[i] = 0;
         tmode18R1[i] = 0;
      }

      tmodeOldR9 = 0;
      tmodeOldR18 = 0;
      tmodeOldR91 = 0;
      tmodeOldR181 = 0;

      error = " Stats Table Access Error";

      //
      // use the dates provided to search the stats table
      //
      try {

         //
         //  Get the System Parameters for this Course
         //
         getParms.getCourse(con, parmc, courseName);


         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT * " +
            "FROM stats5 WHERE date >= ? AND date <= ? AND course = ?");

         //
         //  Get the counts for the dates entered
         //
         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, sdate);
         pstmt1.setLong(2, edate);
         pstmt1.setString(3, courseName);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {
           
            i2 = 1;
            for (i = 0; i < parm.MAX_Mems; i++) {
               memxr9[i] = rs.getInt("mem" +i2+ "Rounds9");
               memxr18[i] = rs.getInt("mem" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Mships; i++) {
               mshipxr9[i] = rs.getInt("mship" +i2+ "Rounds9");
               mshipxr18[i] = rs.getInt("mship" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Guests; i++) {
               gstr9[i] = rs.getInt("gst" +i2+ "Rounds9");
               gstr18[i] = rs.getInt("gst" +i2+ "Rounds18");
               i2++;
            }
            i2 = 1;
            for (i = 0; i < parm.MAX_Tmodes; i++) {
               tmode9[i] = rs.getInt("tmode" +i2+ "R9");
               tmode18[i] = rs.getInt("tmode" +i2+ "R18");
               i2++;
            }
            other9 = rs.getInt("otherRounds9");
            other18 = rs.getInt("otherRounds18");
            cart9 = rs.getInt("cartsRounds9");
            cart18 = rs.getInt("cartsRounds18");
            cady9 = rs.getInt("caddyRounds9");
            cady18 = rs.getInt("caddyRounds18");
            pc9 = rs.getInt("pullcartRounds9");
            pc18 = rs.getInt("pullcartRounds18");
            wa9 = rs.getInt("walkRounds9");
            wa18 = rs.getInt("walkRounds18");
            memnshow9 = rs.getInt("memnoshow9");
            memnshow18 = rs.getInt("memnoshow18");
            gstnshow9 = rs.getInt("gstnoshow9");
            gstnshow18 = rs.getInt("gstnoshow18");
            memunk9 = rs.getInt("mem9unknown");
            memunk18 = rs.getInt("mem18unknown");
            mshipunk9 = rs.getInt("mship9unknown");
            mshipunk18 = rs.getInt("mship18unknown");
            tmodeOldR9 = rs.getInt("tmodeOldR9");
            tmodeOldR18 = rs.getInt("tmodeOldR18");

            //
            //  got the stats for one day - add them to the running totals 
            //
            //     Get Member Rounds
            //
            for (i = 0; i < parm.MAX_Mems; i++) {
               mem9Rounds1 = mem9Rounds1 + memxr9[i];
               mem18Rounds1 = mem18Rounds1 + memxr18[i];
               memxRounds9[i] = memxRounds9[i] + memxr9[i];         // member 9 hole rounds
               memxRounds18[i] = memxRounds18[i] + memxr18[i];      // member 18 hole rounds
               memxRounds1[i] = memxRounds18[i] + memxRounds9[i];   // individual member type totals
            }

            memRounds1 = mem9Rounds1 + mem18Rounds1;                 // rounds by members
              
            memUnknown9 = memUnknown9 + memunk9;                     // unknown member types
            memUnknown18 = memUnknown18 + memunk18;

            memUnknown = memUnknown9 + memUnknown18;

            //
            //  Get membership Rounds
            //
            for (i = 0; i < parm.MAX_Mems; i++) {
               mshipxRounds9[i] = mshipxRounds9[i] + mshipxr9[i];         // mship 9 hole rounds
               mshipxRounds18[i] = mshipxRounds18[i] + mshipxr18[i];      // mship 18 hole rounds
               mshipxRounds1[i] = mshipxRounds18[i] + mshipxRounds9[i];   // individual mship type totals
            }

            mshipUnknown9 = mshipUnknown9 + mshipunk9;                     // unknown membership types
            mshipUnknown18 = mshipUnknown18 + mshipunk18;
            mshipUnknown = mshipUnknown9 + mshipUnknown18;

            //
            //  Get Guest Rounds
            //
            for (i = 0; i < parm.MAX_Guests; i++) {
               gst1Rnds9[i] = gst1Rnds9[i] + gstr9[i];        // guest 9 hole rounds
               gst1Rnds18[i] = gst1Rnds18[i] + gstr18[i];     // guest 18 hole rounds
               gstRnds1[i] = gst1Rnds18[i] + gst1Rnds9[i];    // individual guest totals
               gst9Rounds1 = gst9Rounds1 + gstr9[i];          // total guest 9 hole rounds
               gst18Rounds1 = gst18Rounds1 + gstr18[i];       // total guest 18 hole rounds
            }

            gstRounds1 = gst9Rounds1 + gst18Rounds1;          // total guest rounds (9 & 18)

            //
            //  Get Rounds by Others (not members, not guests)
            //
            other9Rounds1 = other9Rounds1 + other9;
            other18Rounds1 = other18Rounds1 + other18;
              
            otherRounds1 = other9Rounds1 + other18Rounds1;                 // total Other Rounds
              
            //
            //  No-Show Rounds by members and guests
            //
            mnshow9Rounds1 = mnshow9Rounds1 + memnshow9;                     // member no-shows
            mnshow18Rounds1 = mnshow18Rounds1 + memnshow18;

            gnshow9Rounds1 = gnshow9Rounds1 + gstnshow9;                     // guest no-shows
            gnshow18Rounds1 = gnshow18Rounds1 + gstnshow18;

            nshow9Rounds1 = mnshow9Rounds1 + gnshow9Rounds1;
            nshow18Rounds1 = mnshow18Rounds1 + gnshow18Rounds1;

            mnshowRounds1 = mnshow9Rounds1 + mnshow18Rounds1;
            gnshowRounds1 = gnshow9Rounds1 + gnshow18Rounds1;
            nshowRounds1 = nshow9Rounds1 + nshow18Rounds1;                // total nshow Rounds

            for (i=0; i<parm.MAX_Tmodes; i++) {                 // do all the new trans mode types

               tmode9R1[i] += tmode9[i];
               tmode18R1[i] += tmode18[i];
               tmodeR1[i] = tmode9R1[i] + tmode18R1[i];
            }
              
            tmodeOldR91 += tmodeOldR9;
            tmodeOldR181 += tmodeOldR18;

         }         // end of while date range for this course

         pstmt1.close();

         //
         //   Grand Total # of Rounds
         //
         totRounds1 = totRounds1 + memRounds1 + gstRounds1 + otherRounds1;     // total # of rounds played


      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Exception:" + exc.getMessage());
         out.println("<BR><BR>Error:" + error);
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      //  Build a table for each course
      //
      out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

         //
         // add course name header if multi
         //
         if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"#336633\"><td colspan=\"2\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
            out.println("</font></td></tr>");
         }

         out.println("<tr bgcolor=\"#336633\"><td>");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"left\"><b>Stat</b></p>");
               out.println("</font></td>");

            out.println("<td>");
               out.println("<font size=\"2\">");
               out.println("<p align=\"center\"><b>From " + smonth + "/" + sday + "/" + syear + " to");
               out.println(" " + emonth + "/" + eday + "/" + eyear + "</b></p>");
               out.println("</font></td>");

         //
         //  Build the HTML for each stat gathered above
         //
         out.println("</tr><tr>");                       // Grand totals
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\"><br>");
            out.println("<b>Total Rounds Played:</b>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(totRounds1);
            out.println("</b></font></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                     // Total Rounds for members
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\"><b>Rounds by Members:</b></p>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (memRounds1 < 1 || totRounds1 < 1) {
            out.println(memRounds1);
         } else {
            out.println(memRounds1 + " (" + (memRounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</b></font></td>");

         found = false;
         for (i=0; i<parm.MAX_Mems; i++) {      // do all mem types

            if (!parm.mem[i].equals( "" )) {

               out.println("</tr><tr>");                     // Rounds for Member Types
               out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               if (found == false) {
                  out.println("<u>by Member Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<br>");
               }
               out.println(parm.mem[i] + ":");
               out.println("</font></td>");
  
               found = true;

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><br>");
               if (memxRounds1[i] < 1 || memRounds1 < 1) {
                  out.println(memxRounds1[i]);
               } else {
                  out.println(memxRounds1[i] + " (" + (memxRounds1[i] * 100)/memRounds1 + "%)");
               }
               out.println("</font></td>");
            }
         }


         //
         //  check for rounds with no member type (member has been deleted from db since round was played)
         //
         if (memUnknown != 0) {
           
            out.println("</tr><tr>");                     // Rounds for Unknown Member Type
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(memUnknown + " (" + (memUnknown * 100)/memRounds1 + "%)");
               out.println("</font></td>");
         }

         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         found = false;
         for (i=0; i<parm.MAX_Mships; i++) {        // do all mship types

            if (!parm.mship[i].equals( "" )) {

               out.println("</tr><tr>");                     // Rounds for Membership Types
               out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               if (found == false) {
                  out.println("<u>by Membership Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<br>");
               }
               out.println(parm.mship[i] + ":");
               out.println("</font></td>");
         
               found = true;

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><br>");
               if (mshipxRounds1[i] < 1 || memRounds1 < 1) {
                  out.println(mshipxRounds1[i]);
               } else {
                  out.println(mshipxRounds1[i] + " (" + (mshipxRounds1[i] * 100)/memRounds1 + "%)");
               }
               out.println("</font></td>");
            }
         }


         //
         //  check for rounds with no membership type (member has been deleted from db since round was played)
         //
         if (mshipUnknown != 0) {

            out.println("</tr><tr>");                     // Rounds for Unknown Membership
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(mshipUnknown + " (" + (mshipUnknown * 100)/memRounds1 + "%)");
               out.println("</font></td>");
         }


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                     // 9 Hole Rounds for Members
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Member 9 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem9Rounds1 < 1 || totRounds1 < 1) {
            out.println(mem9Rounds1);
         } else {
            out.println(mem9Rounds1 + " (" + (mem9Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // 18 Hole Rounds for Members
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Member 18 Hole Rounds:");
            out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem18Rounds1 < 1 || totRounds1 < 1) {
            out.println(mem18Rounds1);
         } else {
            out.println(mem18Rounds1 + " (" + (mem18Rounds1 * 100)/totRounds1 + "%)");
         }
         out.println("</font><br></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                      // Total Rounds by Guests
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\"><b>Rounds by Guests:</b></p>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (gstRounds1 < 1 || totRounds1 < 1) {
            out.println(gstRounds1);
         } else {
            out.println(gstRounds1 + " (" + (gstRounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</b></font></td>");

         for (i = 0; i < parm.MAX_Guests; i++) {          // chack all 36 guest types

            if (!parm.guest[i].equals( "" ) && !parm.guest[i].equals( "$@#!^&*" )) {

               out.println("</tr><tr>");                     // Rounds for all 36 Guest Types
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\">" + parm.guest[i] + ":</p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gstRnds1[i] < 1 || gstRounds1 < 1) {
                  out.println(gstRnds1[i]);
               } else {
                  out.println(gstRnds1[i] + " (" + (gstRnds1[i] * 100)/gstRounds1 + "%)");
               }
                  out.println("</font></td>");
            }
         }

         out.println("</tr><tr>");                     // 9 Hole Rounds for Guests
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\"><br>");
            out.println("Guest 9 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br>");
         if (gst9Rounds1 < 1 || totRounds1 < 1) {
            out.println(gst9Rounds1);
         } else {
            out.println(gst9Rounds1 + " (" + (gst9Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // 18 Hole Rounds for Guests
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Guest 18 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (gst18Rounds1 < 1 || totRounds1 < 1) {
            out.println(gst18Rounds1);
         } else {
            out.println(gst18Rounds1 + " (" + (gst18Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         //
         //  Only display 'Others' if there were some found (non-members, non-guests)
         //
         if (otherRounds1 > 0) {

            out.println("</tr><tr>");                      // Total Rounds by Others
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\"><b>Rounds by Others:</b></p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><b>");
            if (otherRounds1 < 1 || totRounds1 < 1) {
               out.println(otherRounds1);
            } else {
               out.println(otherRounds1 + " (" + (otherRounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</b></font></td>");

            out.println("</tr><tr>");                     // 9 Hole Rounds for Others
            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Other 9 Hole Rounds:");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><br>");
            if (other9Rounds1 < 1 || totRounds1 < 1) {
               out.println(other9Rounds1);
            } else {
               out.println(other9Rounds1 + " (" + (other9Rounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");

            out.println("</tr><tr>");                     // 18 Hole Rounds for Others
            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Other 18 Hole Rounds:");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other18Rounds1 < 1 || totRounds1 < 1) {
               out.println(other18Rounds1);
            } else {
               out.println(other18Rounds1 + " (" + (other18Rounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");

            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");
         }
         out.println("</tr>");


         //
         //  Check all the Transportation Modes - now 16 configurable modes (V4)
         //
         for (i=0; i<parm.MAX_Tmodes; i++) {

            if (tmodeR1[i] > 0) {

               out.println("<tr>");
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\"><b>" +parmc.tmode[i]+ " Rounds:</b></p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (tmodeR1[i] < 1 || totRounds1 < 1) {
                  out.println(tmodeR1[i]);
               } else {
                  out.println(tmodeR1[i] + " (" + (tmodeR1[i] * 100)/totRounds1 + "%)");
               }
                  out.println("</font></td>");
               out.println("</tr>");
            }
         }

         if ((tmodeOldR91 + tmodeOldR181) > 0) {

            out.println("<tr>");
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\"><b>Rounds From Modes No Longer Used:</b></p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if ((tmodeOldR91 + tmodeOldR181) < 1 || totRounds1 < 1) {
               out.println(tmodeOldR91 + tmodeOldR181);
            } else {
               out.println((tmodeOldR91 + tmodeOldR181) + " (" + ((tmodeOldR91 + tmodeOldR181) * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");
            out.println("</tr>");
         }
           
         out.println("<tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("</tr><tr>");
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<b>Number of Member No-Shows:</b>");
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(nshowRounds1);
            out.println("<br></font></td>");

      out.println("</font></tr></table><br>");

      count2--;                         // decrement number of courses
      index++;
      courseName = course[index];      // get next course name, if more

   }       // end of while Courses - do all courses

   out.println("</td></tr></table>");                // end of main page table & column

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");

 }  // end of Custom2


 // *********************************************************
 //  Report Type = No-Shows (noshow=yes) 
 // *********************************************************

 private void noShows(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   String fname = "";
   String lname = "";
   String mname = "";
   String user = "";

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Prompt user for dates and members
   //
   out.println(SystemUtils.HeadTitle("Proshop No-Show Report Select Dates"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>No-Show Report</b><br>");
      out.println("<br>Select the date range and member name(s) below.<br>");
      out.println("Click on <b>Go</b> to generate the report.");
      out.println("</font></td></tr></table><br>");

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_reports\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"noshow2\" value=\"yes\"></input>");

            out.println("<tr><td>");
               out.println("<font size=\"2\">");
               out.println("<div id=\"awmobject1\">");        // allow menus to show over this box
               out.println("Start Date:&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"smonth\">");
                      out.println("<option selected value=\"01\">JAN</option>");
                      out.println("<option value=\"02\">FEB</option>");
                      out.println("<option value=\"03\">MAR</option>");
                      out.println("<option value=\"04\">APR</option>");
                      out.println("<option value=\"05\">MAY</option>");
                      out.println("<option value=\"06\">JUN</option>");
                      out.println("<option value=\"07\">JUL</option>");
                      out.println("<option value=\"08\">AUG</option>");
                      out.println("<option value=\"09\">SEP</option>");
                      out.println("<option value=\"10\">OCT</option>");
                      out.println("<option value=\"11\">NOV</option>");
                      out.println("<option value=\"12\">DEC</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"sday\">");
                      out.println("<option selected value=\"01\">1</option>");
                      out.println("<option value=\"02\">2</option>");
                      out.println("<option value=\"03\">3</option>");
                      out.println("<option value=\"04\">4</option>");
                      out.println("<option value=\"05\">5</option>");
                      out.println("<option value=\"06\">6</option>");
                      out.println("<option value=\"07\">7</option>");
                      out.println("<option value=\"08\">8</option>");
                      out.println("<option value=\"09\">9</option>");
                      out.println("<option value=\"10\">10</option>");
                      out.println("<option value=\"11\">11</option>");
                      out.println("<option value=\"12\">12</option>");
                      out.println("<option value=\"13\">13</option>");
                      out.println("<option value=\"14\">14</option>");
                      out.println("<option value=\"15\">15</option>");
                      out.println("<option value=\"16\">16</option>");
                      out.println("<option value=\"17\">17</option>");
                      out.println("<option value=\"18\">18</option>");
                      out.println("<option value=\"19\">19</option>");
                      out.println("<option value=\"20\">20</option>");
                      out.println("<option value=\"21\">21</option>");
                      out.println("<option value=\"22\">22</option>");
                      out.println("<option value=\"23\">23</option>");
                      out.println("<option value=\"24\">24</option>");
                      out.println("<option value=\"25\">25</option>");
                      out.println("<option value=\"26\">26</option>");
                      out.println("<option value=\"27\">27</option>");
                      out.println("<option value=\"28\">28</option>");
                      out.println("<option value=\"29\">29</option>");
                      out.println("<option value=\"30\">30</option>");
                      out.println("<option value=\"31\">31</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"syear\">");
                      out.println("<option selected value=\"2002\">2002</option>");
                      out.println("<option value=\"2003\">2003</option>");
                      out.println("<option value=\"2004\">2004</option>");
                      out.println("<option value=\"2005\">2005</option>");
                      out.println("<option value=\"2006\">2006</option>");
                      out.println("<option value=\"2007\">2007</option>");
                      out.println("<option value=\"2008\">2008</option>");
                      out.println("<option value=\"2009\">2009</option>");
                      out.println("<option value=\"2010\">2010</option>");
                 out.println("</select></div><br><br>");
                   
                 out.println("<div id=\"awmobject2\">");        // allow menus to show over this box
               out.println("End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"emonth\">");
                      out.println("<option value=\"01\">JAN</option>");
                      out.println("<option value=\"02\">FEB</option>");
                      out.println("<option value=\"03\">MAR</option>");
                      out.println("<option value=\"04\">APR</option>");
                      out.println("<option value=\"05\">MAY</option>");
                      out.println("<option value=\"06\">JUN</option>");
                      out.println("<option value=\"07\">JUL</option>");
                      out.println("<option value=\"08\">AUG</option>");
                      out.println("<option value=\"09\">SEP</option>");
                      out.println("<option value=\"10\">OCT</option>");
                      out.println("<option value=\"11\">NOV</option>");
                      out.println("<option selected value=\"12\">DEC</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eday\">");
                      out.println("<option selected selected value=\"01\">1</option>");
                      out.println("<option value=\"02\">2</option>");
                      out.println("<option value=\"03\">3</option>");
                      out.println("<option value=\"04\">4</option>");
                      out.println("<option value=\"05\">5</option>");
                      out.println("<option value=\"06\">6</option>");
                      out.println("<option value=\"07\">7</option>");
                      out.println("<option value=\"08\">8</option>");
                      out.println("<option value=\"09\">9</option>");
                      out.println("<option value=\"10\">10</option>");
                      out.println("<option value=\"11\">11</option>");
                      out.println("<option value=\"12\">12</option>");
                      out.println("<option value=\"13\">13</option>");
                      out.println("<option value=\"14\">14</option>");
                      out.println("<option value=\"15\">15</option>");
                      out.println("<option value=\"16\">16</option>");
                      out.println("<option value=\"17\">17</option>");
                      out.println("<option value=\"18\">18</option>");
                      out.println("<option value=\"19\">19</option>");
                      out.println("<option value=\"20\">20</option>");
                      out.println("<option value=\"21\">21</option>");
                      out.println("<option value=\"22\">22</option>");
                      out.println("<option value=\"23\">23</option>");
                      out.println("<option value=\"24\">24</option>");
                      out.println("<option value=\"25\">25</option>");
                      out.println("<option value=\"26\">26</option>");
                      out.println("<option value=\"27\">27</option>");
                      out.println("<option value=\"28\">28</option>");
                      out.println("<option value=\"29\">29</option>");
                      out.println("<option value=\"30\">30</option>");
                      out.println("<option value=\"31\">31</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eyear\">");
                      out.println("<option selected value=\"2002\">2002</option>");
                      out.println("<option value=\"2003\">2003</option>");
                      out.println("<option value=\"2004\">2004</option>");
                      out.println("<option value=\"2005\">2005</option>");
                      out.println("<option value=\"2006\">2006</option>");
                      out.println("<option value=\"2007\">2007</option>");
                      out.println("<option value=\"2008\">2008</option>");
                      out.println("<option value=\"2009\">2009</option>");
                      out.println("<option value=\"2010\">2010</option>");
                 out.println("</select></div><br><br>");

                  out.println("Member Name:&nbsp;&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"username\">");
                  out.println("<option selected value=\"All Members\">All Members</option>");

   //
   // Get a list of members
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT username, name_last, name_first, name_mi FROM member2b " +
                             "ORDER BY name_last, name_first, name_mi");

      while (rs.next()) {

         user = rs.getString(1);
         lname = rs.getString(2);
         fname = rs.getString(3);
         mname = rs.getString(4);

         out.println("<option value=" + user + ">" + lname + ", " + fname + " " + mname + "</option>");

      }

      stmt.close();

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }

   if (user.equals( omit )) {          // if no members in db yet

      out.println("</select><br><br>");
      out.println("There are no members in the database at this time.<br><br>");
      out.println("</font>");
      out.println("</td></tr></table>");

   } else {

      out.println("</select><br>");
      out.println("<p align=\"center\"><input type=\"submit\" value=\"Go\"></p>");
      out.println("</td></tr></table>");

   }
      out.println("</font></td></tr></form></table>");         // end of main page table

      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   out.println("</center></font></body></html>");

 }  // end of noShows


 // *********************************************************
 //  Report Type = No-Show (noshow2=yes) - from self (noShows above)
 // *********************************************************

 private void noShow2(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   String user = "";
   String name = "";
   String fname = "";
   String mname = "";
   String lname = "";

   String omit = "";
   String ampm = "";
   String day = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String submit = "";
   String sfb = "";
   long sdate = 0;
   long edate = 0;
   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int count = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int fb = 0;

   int multi = 0;                 // multiple course support
   int index = 0;
   int i = 0;
   int count2 = 0;                 // number of courses
   int fives = 0;                 // 5-somes

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  Array to hold the course names
   //
   String [] course = new String [20];                     // max of 20 courses per club

   String courseName = "";        // course names

   boolean go = false;
   boolean guest = false;

   //
   // Process request according to the dates and member name selected (a name or 'All Members')
   //
   // Get the parameters entered
   //
   user = req.getParameter("username");    

   String smonth = req.getParameter("smonth");  
   String sday = req.getParameter("sday");
   String syear = req.getParameter("syear");

   String emonth = req.getParameter("emonth");
   String eday = req.getParameter("eday");
   String eyear = req.getParameter("eyear");

   //
   //  Convert the string values to int's
   //
   try {
      mm = Integer.parseInt(smonth);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      dd = Integer.parseInt(sday);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      yy = Integer.parseInt(syear);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   sdate = yy * 10000;                            // create a date field of yyyymmdd
   sdate = sdate + (mm * 100);
   sdate = sdate + dd;                         

   try {
      mm = Integer.parseInt(emonth);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      dd = Integer.parseInt(eday);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      yy = Integer.parseInt(eyear);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   edate = yy * 10000;                            // create a date field of yyyymmdd
   edate = edate + (mm * 100);
   edate = edate + dd;

   //
   //   Get club parms
   //
   try {

      getClub.getParms(con, parm);        // get the club parms

      multi = parm.multi;

      //
      //   Remove any guest types that are null - for tests below
      //
      for (i = 0; i < parm.MAX_Guests; i++) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name
         }
      }

      count2 = 1;                  // init to 1 course

      //
      //   Check for multiple courses
      //
      if (multi != 0) {           // if multiple courses supported for this club

         while (index< 20) {

            course[index] = "";       // init the course array
            index++;
         }

         index = 0;

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
         count2 = index;                      // number of courses
      }

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Build the HTML page to display search results
   //
   out.println(SystemUtils.HeadTitle("Proshop No-Show Report"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");          // main page table
   out.println("<tr><td align=\"center\" valign=\"top\">");

   out.println("<font size=\"3\">");
   out.println("<p><b>No-Show Report for " + name + "</b></p>");
   out.println("</font><font size=\"2\">");
   out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other<br><br>");
     
   courseName = "";            // init as not multi
   index = 0;

   if (multi != 0) {           // if multiple courses supported for this club

      courseName = course[index];      // get first course name
   }

   //
   // execute searches and display for each course
   //
   while (count2 > 0) {

      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4, " +
            "username1, username2, username3, username4, show1, show2, show3, show4, fb, " +
            "player5, username5, show5 FROM teepast2 " +
            "WHERE ((show1=0 OR show2=0 OR show3=0 OR show4=0 OR show5=0) AND (date >= ? AND date <= ?) AND courseName = ?) " +
            "ORDER BY date, time");

         PreparedStatement pstmt2 = con.prepareStatement (
            "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4, " +
            "username1, username2, username3, username4, show1, show2, show3, show4, fb, " +
            "player5, username5, show5 FROM teepast2 " +
            "WHERE ((username1 LIKE ? OR username2 LIKE ? OR username3 LIKE ? OR username4 LIKE ? OR username5 LIKE ?) " +
            "AND (show1<>1 OR show<>=1 OR show3<>1 OR show4<>1 OR show5<>1) AND (date >= ? AND date <= ?) AND courseName = ?) " +
            "ORDER BY date, time");

         PreparedStatement pstmt3 = con.prepareStatement (
            "SELECT name_last, name_first, name_mi FROM member2b " +
            "WHERE username = ?");
               
         PreparedStatement pstmtc = con.prepareStatement (
            "SELECT fives " +
            "FROM clubparm2 WHERE first_hr != 0 AND courseName = ?");


         pstmtc.clearParameters();        // clear the parms
         pstmtc.setString(1, courseName);
         rs = pstmtc.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            fives = rs.getInt(1);          // 5-somes
         }
         pstmtc.close();

         //
         //  Get the name to search for
         //
         if (user.equals( "All Members" )) {

            name = user;

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setLong(1, sdate);
            pstmt1.setLong(2, edate);
            pstmt1.setString(3, courseName);
            rs = pstmt1.executeQuery();      // execute the prepared stmt
                         
         } else {
            pstmt3.clearParameters();        // clear the parms
            pstmt3.setString(1, user);
            rs = pstmt3.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               lname = rs.getString(1);
               fname = rs.getString(2);
               mname = rs.getString(3);

               if (mname.equals( "" )) {

                  name = fname + " " + lname;

               } else {

                  name = fname + " " + mname + " " + lname;
               }
            }
            pstmt3.close();

            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, user);
            pstmt2.setString(2, user);
            pstmt2.setString(3, user);
            pstmt2.setString(4, user);
            pstmt2.setString(5, user);
            pstmt2.setLong(6, sdate);
            pstmt2.setLong(7, edate);
            pstmt2.setString(8, courseName);
            rs = pstmt2.executeQuery();      // execute the prepared stmt

         }

         //
         //   build the HTML page for the display
         //

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<tr bgcolor=\"#336633\"><td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Date</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Time</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
            out.println("</font></td>");

         if (fives != 0) {
           
            out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
            out.println("</font></td>");
         }
         out.println("</tr>");

         //
         //  Get each record and display it
         //
         count = 0;             // number of records found

         while (rs.next()) {

            mm = rs.getInt(1);
            dd = rs.getInt(2);
            yy = rs.getInt(3);
            day = rs.getString(4);
            hr = rs.getInt(5);
            min = rs.getInt(6);
            player1 = rs.getString(7);
            player2 = rs.getString(8);
            player3 = rs.getString(9);
            player4 = rs.getString(10);
            username1 = rs.getString(11);
            username2 = rs.getString(12);
            username3 = rs.getString(13);
            username4 = rs.getString(14);
            show1 = rs.getInt(15);
            show2 = rs.getInt(16);
            show3 = rs.getInt(17);
            show4 = rs.getInt(18);
            fb = rs.getInt(19);
            player5 = rs.getString(20);
            username5 = rs.getString(21);
            show5 = rs.getInt(22);

            go = false;                  // init flag for tests

            if (user.equals( "All Members" )) {

               if ((!player1.equals( "" )) && 
                   (!player1.equalsIgnoreCase( "x" )) && (show1 != 1)) {

                  guest = false;
                  i = 0;

                  ploop1:
                  while (i < parm.MAX_Guests) {

                     if (player1.startsWith( parm.guest[i] )) {

                        guest = true;
                        break ploop1;
                     }
                     i++;
                  }
                  if (guest == false) {

                     go = true;              // indicate at least one player/member is a no-show
                  }
               }

               if ((!player2.equals( "" )) && 
                   (!player2.equalsIgnoreCase( "x" )) && (show2 != 1)) {

                  guest = false;
                  i = 0;

                  ploop2:
                  while (i < parm.MAX_Guests) {

                     if (player2.startsWith( parm.guest[i] )) {

                        guest = true;
                        break ploop2;
                     }
                     i++;
                  }
                  if (guest == false) {

                     go = true;              // indicate at least one player/member is a no-show
                  }
               }

               if ((!player3.equals( "" )) && 
                   (!player3.equalsIgnoreCase( "x" )) && (show3 != 1)) {

                  guest = false;
                  i = 0;

                  ploop3:
                  while (i < parm.MAX_Guests) {

                     if (player3.startsWith( parm.guest[i] )) {

                        guest = true;
                        break ploop3;
                     }
                     i++;
                  }
                  if (guest == false) {

                     go = true;              // indicate at least one player/member is a no-show
                  }
               }

               if ((!player4.equals( "" )) && 
                   (!player4.equalsIgnoreCase( "x" )) && (show4 != 1)) {

                  guest = false;
                  i = 0;

                  ploop4:
                  while (i < parm.MAX_Guests) {

                     if (player4.startsWith( parm.guest[i] )) {

                        guest = true;
                        break ploop4;
                     }
                     i++;
                  }
                  if (guest == false) {

                     go = true;              // indicate at least one player/member is a no-show
                  }
               }
                 
               if ((!player5.equals( "" )) && 
                   (!player5.equalsIgnoreCase( "x" )) && (show5 != 1)) {

                  guest = false;
                  i = 0;

                  ploop5:
                  while (i < parm.MAX_Guests) {

                     if (player5.startsWith( parm.guest[i] )) {

                        guest = true;
                        break ploop5;
                     }
                     i++;
                  }
                  if (guest == false) {

                     go = true;              // indicate at least one player/member is a no-show
                  }
               }
            } else {

               if ((username1.equals(user) && show1 != 1) || (username2.equals(user) && show2 != 1) ||
                   (username3.equals(user) && show3 != 1) || (username4.equals(user) && show4 != 1) ||
                   (username5.equals(user) && show5 != 1)) {

                  go = true;
               }
            }

            if (go) {

               count++;

               if (day.equals( "Sunday" )) {

                  day = "Sun";
               }
               if (day.equals( "Monday" )) {

                  day = "Mon";
               }
               if (day.equals( "Tuesday" )) {

                  day = "Tue";
               }
               if (day.equals( "Wednesday" )) {

                  day = "Wed";
               }
               if (day.equals( "Thursday" )) {

                  day = "Thu";
               }
               if (day.equals( "Friday" )) {

                  day = "Fri";
               }
               if (day.equals( "Saturday" )) {

                  day = "Sat";
               }

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               }
               if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }

               if (player1.equals( "" )) {

                  player1 = " ";       // make it a space for table display
               }
               if (player2.equals( "" )) {

                  player2 = " ";       // make it a space for table display
               }
               if (player3.equals( "" )) {

                  player3 = " ";       // make it a space for table display
               }
               if (player4.equals( "" )) {

                  player4 = " ";       // make it a space for table display
               }
               if (player5.equals( "" )) {

                  player5 = " ";       // make it a space for table display
               }

               //
               //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
               //
               sfb = "O";       // default Other

               if (fb == 1) {

                  sfb = "B";
               }

               if (fb == 0) {

                  sfb = "F";
               }

               //
               //  Build the HTML for each record found
               //
               out.println("<tr>");
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (min < 10) {
                  out.println( hr + ":0" + min + ampm );
               } else {
                  out.println( hr + ":" + min + ampm );
               }
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(sfb);
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if ((!player1.equals( " " )) && (!player1.equalsIgnoreCase( "x" ))) {
                  if (show1 == 1) {                                   // if player is a no-show
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                  }
               }
               out.println("&nbsp;" + player1);
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if ((!player2.equals( " " )) && (!player2.equalsIgnoreCase( "x" ))) {
                  if (show2 == 1) {                                   // if player is a no-show
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                  }
               }
               out.println("&nbsp;" + player2);
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if ((!player3.equals( " " )) && (!player3.equalsIgnoreCase( "x" ))) {
                  if (show3 == 1) {                                   // if player is a no-show
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                  }
               }
               out.println("&nbsp;" + player3);
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if ((!player4.equals( " " )) && (!player4.equalsIgnoreCase( "x" ))) {
                  if (show4 == 1) {                                   // if player is a no-show
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                  }
               }
               out.println("&nbsp;" + player4);
               out.println("</font></td>");

               if (fives != 0) {

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  if ((!player5.equals( " " )) && (!player5.equalsIgnoreCase( "x" ))) {
                     if (show5 == 1) {                                   // if player is a no-show
                        out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                     } else {
                        out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                     }
                  }
                  out.println("&nbsp;" + player5);
                  out.println("</font></td>");
               }

               out.println("</tr>");

            }    // end of if
         }    // end of while records to process

         pstmt1.close();
         pstmt2.close();

         out.println("</font></table>");

         if (count == 0) {

            out.println("<p align=\"center\">No records found for " + name + ".</p>");
         }

         if (count != 0) {

            if (name.equals( "All Members" )) {

               out.println("<br><font size=\"2\">");
               out.println("<p align=\"center\">There were " + count + " tee times with at least one No-Show during this period.</p>");
               out.println("</font>");

            } else {

               out.println("<br><font size=\"2\">");
               out.println("<p align=\"center\">" + name + " had " + count + " No-Shows during this period.</p>");
               out.println("</font>");
            }
         }
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;

      }     // end of search function

      count2--;                        // decrement number of courses
      index++;
      courseName = course[index];      // get next course name, if more

   }     // end of while courses

   out.println("</td></tr></table>");                // end of main page table
   out.println("</font>");
   out.println("<br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");


 }   // end of noShow2


 // *********************************************************
 //  Report Type = Tee Times
 // *********************************************************

 private void teeTime(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String name = "";

   if (req.getParameter("name") != null) {        // if user specified a name to search for

      name = req.getParameter("name");     // name to search for

      if (!name.equals( "" )) {

         goTee(req, out, con, name);              // go process tee time search request
         return;
      }
   }

   if (req.getParameter("allmems") != null) {        // if user requested a list of all members

      goTee(req, out, con, name);              // go process tee time search request
      return;
   }

   String subtee = req.getParameter("subtee");         //  tee time report subtype

   //
   //  Build the HTML page to prompt Proshop for member name
   //
   out.println(SystemUtils.HeadTitle2("Proshop Reports"));
   out.println("<script>");
   out.println("<!--");
   out.println("function cursor(){document.f.name.focus();}");
   out.println("// -->");
   out.println("</script>");

      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(name) {");

      out.println("document.f.name.value = name;");            // put name selected into the search form

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onLoad=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>To locate the past tee times for an individual or group, enter the name,<br>");
      out.println("or any portion of the name, as it may exist on the tee sheets.<br>");
      out.println("This will search for all names that contain the value you enter.<br>");
      out.println("You may also search for Guests if you wish.</p>");
      out.println("</font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Courier New\">");
   out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
   out.println("<br></font>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_reports\" method=\"post\" target=\"bot\" name=\"f\">");
   out.println("<input type=\"hidden\" name=\"subtee\" value=" + subtee + ">");

   out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");
               out.println("<tr><td width=\"250\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>Name: &nbsp;");
                     out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                     out.println("</input>");
                  out.println("<br><br>");
                  out.println("<input type=\"submit\" value=\"Search\" name=\"search\">");
                  out.println("</p>");
                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table>");

            // add a button to list all members tee times
            //
            out.println("<br><br>");
            out.println("<input type=\"submit\" value=\"List All Members\" name=\"allmems\">");

         out.println("</td>");

   if (req.getParameter("letter") != null) {     // if user clicked on a name letter

      String letter = req.getParameter("letter");      // get the letter
      letter = letter + "%";

      String first = "";
      String mid = "";
      String last = "";
      name = "";
      String wname = "";
      String dname = "";

         out.println("<td valign=\"top\" align=\"center\">");
         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Name List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");

         try {

            PreparedStatement stmt2 = con.prepareStatement (
                     "SELECT name_last, name_first, name_mi FROM member2b " +
                     "WHERE name_last LIKE ? ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"8\" name=\"bname\" onClick=\"movename(this.form.bname.value)\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;
               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               out.println("<option value=\"" + name + "\">" + dname + "</option>");
            }

            out.println("</select>");
            out.println("</font></td></tr>");

            stmt2.close();
         }
         catch (Exception ignore) {

         }
         out.println("</table>");

         out.println("</td>");   // end of name list column
           
   } else {

      out.println("<td valign=\"top\" width=\"30\">");
      out.println("&nbsp;");  
      out.println("</td>");   // end of empty column
     
   }  // end of if Letter

         out.println("<td valign=\"top\" align=\"center\">");  
            out.println("<table border=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<b>Member List</b>");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\">");
                     out.println("<font size=\"2\">Name begins with:");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("<tr>");
                  out.println("<td align=\"center\"><font size=\"1\">");
                     out.println("<input type=\"submit\" value=\"A\" name=\"letter\"></font></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"B\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"C\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"D\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"E\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"F\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"G\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"H\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"I\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"J\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"K\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"L\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"M\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"N\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"O\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"P\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Q\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"R\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"S\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"T\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"U\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"V\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"W\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"X\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Y\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Z\" name=\"letter\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
               out.println("</tr>");
            out.println("</table>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</table>");
      out.println("</select></font>");
      out.println("</td></tr></table>");
      out.println("</form>");

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

    out.println("</center></font></body></html>");
   //
   //  exit and wait for return with selected name or a letter
   //
 }


 // *********************************************************
 //  Report Type = Past Tee Times (2nd request - from above)
 // *********************************************************

 private void goTee(HttpServletRequest req, PrintWriter out, Connection con, String name) {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String sday = "";
   String ampm = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String course = "";
   String memName = "";
   String username = "";
   String lname = "";
   String fname = "";
   String mname = "";
       
   long date = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int fb = 0;
   int count = 0;
   int multi = 0;
   int fives = 0;
   int fiveSomes = 0;

   long sdate = 20020101;       // default date = 01/01/2002 (forever)
   int year = 0;
   int month = 0;
   int day = 0;
   int length = 0;                    // length of name requested
  

   String subtee = req.getParameter("subtee");         //  tee time report subtype (cal, year, forever)

   //
   //  Get today's date and use it for the end date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);

   month = month + 1;                           // month starts at zero

   long edate = year * 10000;                   // create a edate field of yyyymmdd
   edate = edate + (month * 100);
   edate = edate + day;                         // date = yyyymmdd (for comparisons)

   if (subtee.equals( "cal" )) {                //  if for calendar year

      sdate = year * 10000;
      sdate = sdate + 0101;                     // sdate = 01/01/yyyy
        
   } else {

      if (subtee.equals( "year" )) {            //  if for past 12 months

         sdate = year - 1;
         sdate = sdate * 10000;
         sdate = sdate + (month * 100);
         sdate = sdate + day;                   // sdate = yyyymmdd (yyyy is 1 yr ago)
      }
   }        // else use default (forever)

   try {

      //
      //  Check if call is to list all members
      //
      if (req.getParameter("allmems") != null) {

         //
         //  Build the HTML page to display search results
         //
         out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");

         out.println("<font size=\"2\">");
         out.println("<p>Tee Times for <b>All Members</b></p>");
         out.println("</font>");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<tr bgcolor=\"#336633\"><td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Name</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b># of Times</b></u></p>");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");

         //
         //  Get each member and count the number of tee times
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT username, name_last, name_first, name_mi FROM member2b ORDER BY name_last, name_first");

         while (rs.next()) {

            username = rs.getString(1);
            lname = rs.getString(2);
            fname = rs.getString(3);
            mname = rs.getString(4);

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(lname);  // get last name

            mem_name.append(", " + fname);                     // first name

            if (!mname.equals( "" )) {
               mem_name.append(" " +mname);                        // mi
            }
            memName = mem_name.toString();                    // convert to one string

            //
            //  Count the number of tee times for this member
            //
            pstmt = con.prepareStatement (
               "SELECT COUNT(*) FROM teepast2 " +
               "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) " +
               "AND (date >= ? AND date <= ?)");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            pstmt.setString(3, username);
            pstmt.setString(4, username);
            pstmt.setString(5, username);
            pstmt.setLong(6, sdate);
            pstmt.setLong(7, edate);
            rs2 = pstmt.executeQuery();      // execute the prepared stmt

            if (rs2.next()) {

               count = rs2.getInt(1);

            } else {

               count = 0;
            }

            pstmt.close();

            //
            //  Build the HTML for each record found
            //
            out.println("<tr>");
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println( memName );
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println( count );
               out.println("</font></td>");
            out.println("</tr>");

         }    // end of while more members
        
         stmt.close();

         out.println("</font></table>");
         out.println("</td></tr></table>");                // end of main page table & column

      } else {  // call is for one member

         //
         //  verify the required fields
         //
         length = name.length();                    // get length of name requested

         if ((name.equals( "" )) || (length > 20)) {

            invData(out);    // inform the user and return
            return;
         }

         //
         //   Add a % to the name provided so search will match anything close
         //
         StringBuffer buf = new StringBuffer("%");
         buf.append( name );
         buf.append("%");
         String sname = buf.toString();

         //
         //   See if multiple courses or 5-somes are supported
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT multi " +
                                "FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
         }
         stmt.close();

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT fives FROM clubparm2 WHERE first_hr != 0");

         while (rs.next()) {

            fiveSomes = rs.getInt(1);

            if (fiveSomes != 0) {

               fives = 1;      // 5-somes supported on at least one course
            }
         }
         stmt.close();

         //
         // use the name and dates provided to search table
         //
         pstmt = con.prepareStatement (
            "SELECT date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
            "p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, " +
            "player5, p5cw, show5, courseName FROM teepast2 " +
            "WHERE (player1 LIKE ? OR player2 LIKE ? OR player3 LIKE ? OR player4 LIKE ? OR player5 LIKE ?) " +
            "AND (date >= ? AND date <= ?) " +
            "ORDER BY date, time");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, sname);
         pstmt.setString(2, sname);
         pstmt.setString(3, sname);
         pstmt.setString(4, sname);
         pstmt.setString(5, sname);
         pstmt.setLong(6, sdate);
         pstmt.setLong(7, edate);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         //
         //  Build the HTML page to display search results
         //
         out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");

         out.println("<font size=\"2\">");
         out.println("<p>Tee Times Located for <b>" + name + "</b></p>");
         out.println("</font>");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<tr bgcolor=\"#336633\"><td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                     out.println("</font></td>");

                  if (multi != 0) {

                     out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                     out.println("</font></td>");
                  }

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                     out.println("</font></td>");

               if (fives !=0) {
                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                     out.println("</font></td>");
               }
                  out.println("</tr>");

            count = 0;
            //
            //  Get each record and display it
            //
            while (rs.next()) {

               date = rs.getLong(1);
               mm = rs.getInt(2);
               dd = rs.getInt(3);
               yy = rs.getInt(4);
               sday = rs.getString(5);
               hr = rs.getInt(6);
               min = rs.getInt(7);
               time = rs.getInt(8);
               player1 = rs.getString(9);
               player2 = rs.getString(10);
               player3 = rs.getString(11);
               player4 = rs.getString(12);
               p1cw = rs.getString(13);
               p2cw = rs.getString(14);
               p3cw = rs.getString(15);
               p4cw = rs.getString(16);
               show1 = rs.getInt(17);
               show2 = rs.getInt(18);
               show3 = rs.getInt(19);
               show4 = rs.getInt(20);
               fb = rs.getInt(21);
               player5 = rs.getString(22);
               p5cw = rs.getString(23);
               show5 = rs.getInt(24);
               course = rs.getString(25);

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               }
               if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }

               if (player1.equals( "" ) || player1.equalsIgnoreCase( "X" )) {

                  player1 = "-";       // change it for table display
                  p1cw = "-";
               }
               if (player2.equals( "" ) || player2.equalsIgnoreCase( "X" )) {

                  player2 = "-";       // change it for table display
                  p2cw = "-";
               }
               if (player3.equals( "" ) || player3.equalsIgnoreCase( "X" )) {

                  player3 = "-";       // change it for table display
                  p3cw = "-";
               }
               if (player4.equals( "" ) || player4.equalsIgnoreCase( "X" )) {

                  player4 = "-";       // change it for table display
                  p4cw = "-";
               }
               if (player5.equals( "" ) || player5.equalsIgnoreCase( "X" )) {

                  player5 = "-";       // change it for table display
                  p5cw = "-";
               }

               if (course.equals( "" )) {

                  course = " ";       // make it a spece for table display
               }

               if (sday.equalsIgnoreCase( "sunday" )) {

                  sday = "Sun";
               }
               if (sday.equalsIgnoreCase( "monday" )) {

                  sday = "Mon";
               }
               if (sday.equalsIgnoreCase( "tuesday" )) {

                  sday = "Tues";
               }
               if (sday.equalsIgnoreCase( "wednesday" )) {

                  sday = "Wed";
               }
               if (sday.equalsIgnoreCase( "thursday" )) {

                  sday = "Thurs";
               }
               if (sday.equalsIgnoreCase( "friday" )) {

                  sday = "Fri";
               }
               if (sday.equalsIgnoreCase( "saturday" )) {

                  sday = "Sat";
               }

               //
               //  Build the HTML for each record found
               //
               out.println("<tr>");
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println( sday + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (min < 10) {
                  out.println(hr + ":0" + min + ampm);
               } else {
                  out.println(hr + ":" + min + ampm);
               }
                  out.println("</font></td>");

               if (multi != 0) {
                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println( course );
                  out.println("</font></td>");
               }

                  out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                  out.println("<font size=\"2\">");
                  if (fb == 0) {
                     out.println("F");
                  } else {
                     out.println("B");
                  }
                  out.println("</font></td>");

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (!player1.equals( "-")) {
                     if (show1 == 1) {                                   // if player has not checked in yet
                        out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
                     } else {
                        out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
                     }
                  }
                  if (player1.equals( name )) {
                     out.println("<b>" + player1 + "</b>");
                  } else {
                     out.println( player1 );
                  }
                  out.println("</font></td>");

                  out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                  out.println("<font size=\"2\">");
                  out.println( p1cw );
                  out.println("</font></td>");

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (!player2.equals( "-")) {
                     if (show2 == 1) {                                   // if player has not checked in yet
                        out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
                     } else {
                        out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
                     }
                     if (player2.equals( name )) {
                        out.println("<b>" + player2 + "</b>");
                     } else {
                        out.println( player2 );
                     }
                  }
                  out.println("</font></td>");

                  out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                  out.println("<font size=\"2\">");
                  out.println( p2cw );
                  out.println("</font></td>");

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (!player3.equals( "-")) {
                     if (show3 == 1) {                                   // if player has not checked in yet
                        out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
                     } else {
                        out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
                     }
                  }
                  if (player3.equals( name )) {
                     out.println("<b>" + player3 + "</b>");
                  } else {
                     out.println( player3 );
                  }
                  out.println("</font></td>");

                  out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                  out.println("<font size=\"2\">");
                  out.println( p3cw );
                  out.println("</font></td>");

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (!player4.equals( "-")) {
                     if (show4 == 1) {                                   // if player has not checked in yet
                        out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
                     } else {
                        out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
                     }
                  }
                  if (player4.equals( name )) {
                     out.println("<b>" + player4 + "</b>");
                  } else {
                     out.println( player4 );
                  }
                  out.println("</font></td>");

                  out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                  out.println("<font size=\"2\">");
                  out.println( p4cw );
                  out.println("</font></td>");

                  if (fives != 0) {

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     if (!player5.equals( "-")) {
                        if (show5 == 1) {                                   // if player has not checked in yet
                           out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
                        } else {
                           out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
                        }
                     }
                     if (player5.equals( name )) {
                        out.println("<b>" + player5 + "</b>");
                     } else {
                        out.println( player5 );
                     }
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                     out.println("<font size=\"2\">");
                     out.println( p5cw );
                     out.println("</font></td>");
                  }
                  out.println("</tr>");

            count += 1;

         }    // end of while

         pstmt.close();

            out.println("</font></table>");
         out.println("</td></tr></table>");                // end of main page table & column
         out.println("<font size=\"2\"><BR>");
         out.println("<p><b>" + name + "</b> had a total of <b>" + count + "</b> tee times during the specified period.</p>");
         out.println("</td>");
         out.println("</font>");
      }

      out.println("<br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
   }

 }


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
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
 // Member does not exists
 // *********************************************************

 private void noMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the member you specified does exist in the database.<BR>");
   out.println("<BR>Please check your data and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

}
