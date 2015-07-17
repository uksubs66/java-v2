/***************************************************************************************
 *   Proshop_dlott:   This servlet will process the Lottery requests
 *
 *
 *   called by:  
 *
 *
 *   parms passed (on doGet):  
 *
 *
 *   created: 3/22/2007   Paul S.
 *
 *   last updated:
 *
 *
 *
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.zip.*;
import java.sql.*;
import java.lang.Math;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;

// foretees imports
import com.foretees.common.parmSlot;
import com.foretees.common.parmClub;
import com.foretees.common.verifySlot;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;


public class Proshop_dlott extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
   String delim = "_";

   
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setHeader("Pragma", "no-cache");      // these 3 added to fix 'blank screen' problem
    resp.setHeader("Cache-Control", "no-cache");
    resp.setDateHeader("Expires", 0);
    resp.setContentType("text/html");
    PrintWriter out;

    PreparedStatement pstmtc = null;
    Statement stmt = null;
    Statement stmtc = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    //
    //  use GZip (compression) if supported by browser
    //
    String encodings = req.getHeader("Accept-Encoding");               // browser encodings

    if ((encodings != null) && (encodings.indexOf("gzip") != -1)) {    // if browser supports gzip

      OutputStream out1 = resp.getOutputStream();
      out = new PrintWriter(new GZIPOutputStream(out1), false);       // use compressed output stream
      resp.setHeader("Content-Encoding", "gzip");                     // indicate gzip

    } else {

      out = resp.getWriter();                                         // normal output stream
    }

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) {

      out.println(SystemUtils.HeadTitle("Access Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>System Access Error</H3>");
      out.println("<BR><BR>You have entered this site incorrectly or have lost your session cookie.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
    }

    Connection con = SystemUtils.getCon(session);                     // get DB connection

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
      out.close();
      return;
    }

    //
    //  Check for 'Insert' or 'Delete' request
    //
    if (req.getParameter("insert") != null) {

      doInsert(req, out, con, session);          // process insert request
      return;
    }
    if (req.getParameter("delete") != null) {

      if (req.getParameter("lotteryId") != null) {
        doDeleteLottery(req, out, con, session);
      } else {
        doDelete(req, out, con, session);
      }
      return;
    }
    
   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block
   
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String rest_recurr = "";
   String rest5 = "";
   String bgcolor5 = "";
   String player = "";
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
   String event_rest = "";
   String bgcolor = "";
   String stime = "";
   String sshow = "";
   String sfb = "";
   String submit = "";
   String num = "";
   String jumps = "";
   String hole = "";
   
   String event1 = "";       // for legend - max 2 events, 4 rest's, 2 lotteries
   String ecolor1 = "";
   String rest1 = "";
   String rcolor1 = "";
   String event2 = "";
   String ecolor2 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   String blocker = "";
   String bag = "";
   String conf = "";
   String orig_by = "";
   String orig_name = "";
   String errMsg = "";
   String emailOpt = "";
   String lottery_color = "";
   String lottery = "";
   String lottery_recurr = "";
   String lott = "";
   String lott1 = "";
   String lott2 = "";
   String lott3 = "";
   String lott_color = "";
   String lott_color2 = "";
   String lott_recurr = "";
   String lcolor1 = "";
   String lcolor2 = "";
   String lcolor3 = "";
   
   int jump = 0;
   int j = 0;
   int i = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int type = 0;
   int in_use = 0;
   int fives = 0;
   int fivesALL = 0;
   int teecurr_id = 0;
   int shotgun = 1;
   int g1 = 0;
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   
   short fb = 0;
   
   int lott_start_time = 0;
   int lott_end_time = 0;
   
   //
   //  Array to hold the course names
   //
   int cMax = 21;  
   String [] courseA = new String [cMax];               // max of 20 courses per club
   String courseName = "";                              // max of 20 courses per club
   String courseName1 = "";
   String courseT = "";
   int tmp_i = 0;

   int [] fivesA = new int [cMax];                      // array to hold 5-some option for each course

   String [] course_color = new String [cMax];
   
   // set default course colors  // NOTE: CHANES TO THIS ARRAY NEED TO BE DUPLICATED IN Proshop_sheet.java
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
   
   
   String hideUnavail = req.getParameter("hide");
     
   if (hideUnavail == null) hideUnavail = "";
   
   //
   //  Get the golf course name requested (changed to use the course specified for this lottery)
   //
   String course = req.getParameter("course");
   if (course == null) course = "";
   
   //
   //  Get the name of the lottery requested
   //
   String lott_name = req.getParameter("lott_name");
   if (lott_name == null) lott_name = "";
   
   //
   //    'index' contains an index value representing the date selected
   //    (0 = today, 1 = tomorrow, etc.)
   //
   int index = 0;
   num = req.getParameter("index");         // get the index value of the day selected
   
   //
   //  Convert the index value from string to int
   //
   try {
      index = Integer.parseInt(num);
   }
   catch (NumberFormatException e) { }
   
   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   cal.add(Calendar.DATE,index);                 // roll ahead 'index' days
   int cal_hour = cal.get(Calendar.HOUR_OF_DAY); // 24 hr clock (0 - 23)
   int cal_min = cal.get(Calendar.MINUTE);
   int cal_time = (cal_hour * 100) + cal_min;    // get time in hhmm format
   cal_time = SystemUtils.adjustTime(con, cal_time);

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);     // day of week (01 - 07)

   month = month + 1;                               // month starts at zero

   String day_name = day_table[day_num];            // get name for day

   long date = (year * 10000) + (month * 100) + day;  // create a date field of yyyymmdd

   String date_mysql = year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day);
   String time_mysql = (cal_time / 100) + ":" + SystemUtils.ensureDoubleDigit(cal_time % 100) + ":00"; // current time for 
   
   out.println("<!-- date_mysql=" + date_mysql + " -->");
   out.println("<!-- cal_time=" + cal_time + " -->");
   
   
   // huge try/catch that spans entire method
   try {
      
      errMsg = "Get course names.";
      
      //
      // Get the Guest Types from the club db
      //
      getClub.getParms(con, parm);        // get the club parms
      
      //
      //   Remove any guest types that are null - for tests below
      //
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name
         }
         i++;
      }         // end of while loop
      
      //
      //   Get course names if multi-course facility so we can determine if any support 5-somes
      //
      i = 0;
      int courseCount = 0;
      
      
      //if (course.equals("")) {
          PreparedStatement pstmt = con.prepareStatement ("" +
                "SELECT courseName, stime, etime " +
                "FROM lottery3 " +
                "WHERE name = ?");

          pstmt.clearParameters();
          pstmt.setString(1, lott_name);
          rs = pstmt.executeQuery();

          if (rs.next()) {

              if (parm.multi != 0 && course.equals("")) course = rs.getString(1);  // if no course was specified (default) then set the course variable to the course this lottery is for
              lott_start_time = rs.getInt(2);
              lott_end_time = rs.getInt(3);
          }

      //}
      
      if (parm.multi != 0) {           // if multiple courses supported for this club
              
         // init the course array
         while (i < cMax) {

            courseA[i] = "";       
            i++;
         }
         i = 0;
         int total = 0;
         //
         //  Get the names of all courses for this club
         //
         pstmt = con.prepareStatement("SELECT courseName FROM clubparm2;");
         pstmt.clearParameters();
         rs = pstmt.executeQuery();

         while (rs.next() && i < cMax) {

            courseName = rs.getString(1);
            courseA[i] = courseName;      // add course name to array
            i++;
         }
         pstmt.close();
         
         if (i > cMax) {               // make sure we didn't go past max

            courseCount = cMax;
              
         } else {

            courseCount = i;              // save number of courses
         }
 
         if (i > 1 && i < cMax) {
             
            courseA[i] = "-ALL-";        // add '-ALL-' option
         }
         
         //
         //  Make sure we have a course (in case we came directly from the Today's Tee Sheet menu)
         //
         if (courseName1.equals( "" )) {
           
            courseName1 = courseA[0];    // grab the first one
         }
         
      } // end if multiple courses supported

      //
      //  Get the walk/cart options available and 5-some support
      //
      i = 0;
      if (course.equals( "-ALL-" )) {

         //
         //  Check all courses for 5-some support
         //
         loopc:
         while (i < cMax) {

            courseName = courseA[i];       // get a course name

            if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

               if (courseName.equals( "" )) {      // done if null
                  break loopc;
               }
               getParms.getCourse(con, parmc, courseName);
            
               fivesA[i] = parmc.fives;      // get fivesome option
               if (fivesA[i] == 1) {
                  fives = 1;
               }
            }
            i++;
         }

      } else {       // single course requested

         getParms.getCourse(con, parmc, course);

         fives = parmc.fives;      // get fivesome option
      }
        
      fivesALL = fives;            // save 5-somes option for table display below

      i = 0;

      //
      //   Statements to find any restrictions, events or lotteries for today
      //
      String string7b = "";
      String string7c = "";
      String string7d = "";

      if (course.equals( "-ALL-" )) {
         string7b = "SELECT name, recurr, color FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND showit = 'Yes'";
      } else {
         string7b = "SELECT name, recurr, color FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes'";
      }

      if (course.equals( "-ALL-" )) {
         string7c = "SELECT name, color FROM events2b WHERE date = ?";
      } else {
         string7c = "SELECT name, color FROM events2b WHERE date = ? " +
                    "AND (courseName = ? OR courseName = '-ALL-')";
      }

      if (course.equals( "-ALL-" )) {
         string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                    "FROM lottery3 WHERE sdate <= ? AND edate >= ?";
      } else {
         string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                    "FROM lottery3 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-')";
      }

      PreparedStatement pstmt7b = con.prepareStatement (string7b);
      PreparedStatement pstmt7c = con.prepareStatement (string7c);
      PreparedStatement pstmt7d = con.prepareStatement (string7d);

      errMsg = "Scan Restrictions.";

      //
      //  Scan the events, restrictions and lotteries to build the legend
      //
      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
      if (!course.equals( "-ALL-" )) {
         pstmt7b.setString(3, course);
      }

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      while (rs.next()) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);

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
      }                  // end of while
      pstmt7b.close();

      errMsg = "Scan Events.";

      pstmt7c.clearParameters();          // clear the parms
      pstmt7c.setLong(1, date);
      if (!course.equals( "-ALL-" )) {
         pstmt7c.setString(2, course);
      }

      rs = pstmt7c.executeQuery();      // find all matching events, if any

      while (rs.next()) {

         event = rs.getString(1);
         ecolor = rs.getString(2);

         if ((!event.equals( event1 )) && (event1.equals( "" ))) {

            event1 = event;
            ecolor1 = ecolor;

            if (ecolor.equalsIgnoreCase( "default" )) {

               ecolor1 = "#F5F5DC";
            }

          } else {

            if ((!event.equals( event1 )) && (!event.equals( event2 )) && (event2.equals( "" ))) {

               event2 = event;
               ecolor2 = ecolor;

               if (ecolor.equalsIgnoreCase( "default" )) {

                  ecolor2 = "#F5F5DC";
               }
            }
         }

      }                  // end of while
      pstmt7c.close();


      //****************************************************
      // Define tee sheet size and build it
      //****************************************************

      // define our two arrays that describe the column sizes
      // index = column number, value = size in pixels
      int [] col_width = new int [15];
      int col_start[] = new int[15];                    // total width = 962 px (in dts-styles.css)
      int [] lcol_width = new int [8];
      int lcol_start[] = new int[8];
      
      lcol_width[0] = 0;                                        // unused
      lcol_width[1] = 40;                                       // +/-
      lcol_width[2] = (course.equals( "-ALL-" )) ? 71 : 80;     // desired time
      lcol_width[3] = (course.equals( "-ALL-" )) ? 71 : 80;     // assigned time
      lcol_width[4] = 120;                                      // acceptable time
      lcol_width[5] = (course.equals( "-ALL-" )) ? 90 : 0;      // course
      lcol_width[6] = 40;                                       // weight
      lcol_width[7] = (fivesALL == 0) ? 410 : 560;              // members
      
      lcol_start[1] = 0;
      lcol_start[2] = lcol_start[1] + lcol_width[1];
      lcol_start[3] = lcol_start[2] + lcol_width[2];
      lcol_start[4] = lcol_start[3] + lcol_width[3];
      lcol_start[5] = lcol_start[4] + lcol_width[4];
      lcol_start[6] = lcol_start[5] + lcol_width[5];
      lcol_start[7] = lcol_start[6] + lcol_width[6];
      
      if (course.equals( "-ALL-" )) {
         col_width[0] = 0;        // unused
         col_width[1] = 40;       // +/-
         col_width[2] = 71;       // time
         col_width[3] = 90;       // course name col 69
         col_width[4] = 31;       // f/b
         col_width[5] = 111;      // player 1
         col_width[6] = 39;       // player 1 trans opt
         col_width[7] = 111;      // player 2
         col_width[8] = 39;       // player 2 trans opt
         col_width[9] = 111;      // player 3
         col_width[10] = 39;      // player 3 trans opt
         col_width[11] = 111;     // player 4
         col_width[12] = 39;      // player 4 trans opt
         col_width[13] = 111;     // player 5
         col_width[14] = 39;      // player 5 trans opt
      } else {
         col_width[0] = 0;        // unused
         col_width[1] = 40;       // +/-
         col_width[2] = 80;       // time
         col_width[3] = 0;        // empty if no course name
         col_width[4] = 40;       // f/b
         col_width[5] = 120;      // player 1
         col_width[6] = 40;       // player 1 trans opt
         col_width[7] = 120;      // player 2
         col_width[8] = 40;       // player 2 trans opt
         col_width[9] = 120;      // player 3
         col_width[10] = 40;      // player 3 trans opt
         col_width[11] = 120;     // player 4
         col_width[12] = 40;      // player 4 trans opt
         col_width[13] = 120;     // player 5
         col_width[14] = 40;      // player 5 trans opt
      }
      col_start[1] = 0;
      col_start[2] = col_start[1] + col_width[1];
      col_start[3] = col_start[2] + col_width[2];
      col_start[4] = col_start[3] + col_width[3];
      col_start[5] = col_start[4] + col_width[4];
      col_start[6] = col_start[5] + col_width[5];
      col_start[7] = col_start[6] + col_width[6];
      col_start[8] = col_start[7] + col_width[7];
      col_start[9] = col_start[8] + col_width[8];
      col_start[10] = col_start[9] + col_width[9];
      col_start[11] = col_start[10] + col_width[10];
      col_start[12] = col_start[11] + col_width[11];
      col_start[13] = col_start[12] + col_width[12];
      col_start[14] = col_start[13] + col_width[13];

      int total_col_width = col_start[14] + col_width[14];
      
      // temp variable
      String dts_tmp = "";

      //
      //  Build the HTML page
      //
      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
      out.println("<html>\n<!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) ");
      out.println("is the proprietary property of ForeTees, LLC or its suppliers and its use, modification and distribution are protected ");
      out.println("and limited by United States copyright laws and international treaty provisions and all other applicable national laws. ");
      out.println("\nReproduction is strictly prohibited.-->");
      out.println("<head>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");

      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/dts-styles.css\">");

      out.println("<title>Proshop Tee Sheet Management Page</title>");

      out.println("<script language=\"javascript\">");          // Jump script
      out.println("<!--");
      out.println("function jumpToHref(anchorstr) {");
      out.println("if (location.href.indexOf(anchorstr)<0) {");
      out.println(" location.href=anchorstr; }");
      out.println("}");
      out.println("// -->");
      out.println("</script>");                               // End of script

      // include dts javascript source file
      out.println("<script language=\"javascript\" src=\"/" +rev+ "/dts-scripts.js\"></script>");

      out.println("</head>");

      out.println("<body onLoad='jumpToHref(\"#jump" + jump + "\");' bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // table for cmd tbl & instructions
      out.println("<tr><td align=\"left\" valign=\"middle\">");

         //
         //  Build Control Panel
         //
         out.println("<table border=\"1\" width=\"130\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"left\">");
         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"3\"><b>Control Panel</b><br>");
         out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
         
         out.println("</font></td></tr><tr><td align=\"center\" nowrap><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&course=" +course+ "&lott_name=" + lott_name + "&hide=" + hideUnavail + "\" title=\"Refresh Sheet\" alt=\"Refresh Sheet\">");
         out.println("Refresh Sheet</a><br>");
         
         out.println("</font></td></tr><tr><td align=\"center\" nowrap><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&course=" +course+ "&lott_name=" + lott_name + ((hideUnavail.equals("1") ? "" : "&hide=1")) + "\" title=\"Lottery Times Only\" alt=\"Lottery Times Only\">");
         out.println((hideUnavail.equals("1") ? "Show All Tee Times" : "Show Lottery Times Only") + "</a><br>");
         
         out.println("</font></td></tr><tr><td align=\"center\" nowrap><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&course=" +course+ "&lott_name=" + lott_name + "&hide=" + hideUnavail + "&convert=all\" title=\"Convert All Requests\" alt=\"Convert All Requests\">");
         out.println("Convert Assigned Requests</a><br>");
         
         out.println("</font></td></tr><tr><td align=\"center\" nowrap><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "\" title=\"Return to Tee Sheet\" alt=\"Return\">");
         out.println("Return to Tee Sheet</a><br>");
         
         out.println("</font></td></tr></table>"); // end Control Panel table

      out.println("</td>");                                 // end of column for control panel
      out.println("<td align=\"center\" width=\"20\">&nbsp;");     // empty column for spacer

      out.println("</td>");
      out.println("<td align=\"left\" valign=\"top\">");     // column for instructions, course selector, calendars??

         //**********************************************************
         //  Continue with instructions and tee sheet
         //**********************************************************

         out.println("<table cellpadding=\"5\" cellspacing=\"3\" width=\"80%\">");
         out.println("<tr><td align=\"center\">");
         out.println("<p align=\"center\"><font size=\"5\">Golf Shop Lottery Management</font></p>");
         out.println("</td></tr></table>");
         
         
        out.println("<table cellpadding=\"3\" width=\"80%\">");
        out.println("<tr><td bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Instructions:</b>"); 
        out.println("To <b>insert</b> a new tee time, click on the plus icon <img src=/v5/images/dts_newrow.gif width=13 height=13 border=0> in the tee time you wish to insert <i>after</i>.&nbsp; ");
        out.println("To <b>delete</b> a tee time, click on the trash can icon <img src=/v5/images/dts_trash.gif width=13 height=13 border=0> in the tee time you wish to delete.&nbsp; ");
        out.println("To <b>move an entire tee time</b>, click on the 'Time' value and drag the tee time to the new position.&nbsp; ");
        out.println("To <b>move an individual player</b>, click on the Player and drag the name to the new position.&nbsp; ");
        out.println("To <b>change</b> the F/B value or the C/W value, just click on it and make the selection.&nbsp; ");
        out.println("Empty 'player' cells indicate available positions.&nbsp; ");
        out.println("Special Events and Restrictions, if any, are colored (see legend below).");
        out.println("</font></td></tr></table>");
         
        out.println("</td></tr></table>"); // end tbl for instructions         

      out.println("<p><font size=\"2\">");
      out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
      
    if (!course.equals( "" )) {

        out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b></p>");
    }
      
      //
      //  If multiple courses, then add a drop-down box for course names
      //
      String tmp_ncount = "";
      out.println("<!-- courseCount=" + courseCount + " -->");
      
      if (parm.multi != 0) {           // if multiple courses supported for this club

         //
         //  use 2 forms so you can switch by clicking either a course or a date
         //
         if (courseCount < 5) {        // if < 5 courses, use buttons

            i = 0;
            courseName = courseA[i];      // get first course name from array
            
            out.println("<p><font size=\"2\">");
            out.println("<b>Select Course:</b>&nbsp;&nbsp;");

            while ((!courseName.equals( "" )) && (i < 6)) {    // allow one more for -ALL-

               out.println("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&course=" +courseName+ "&hide=" + hideUnavail + "&lott_name=" + lott_name + "\" style=\"color:blue\" target=\"_top\" title=\"Switch to new course\" alt=\"" +courseName+ "\">");
               out.print(courseName + "</a>");
               out.print("&nbsp;&nbsp;&nbsp;");

               i++;
               courseName = courseA[i];      // get course name from array
            }
            out.println("</p>");

         }
         else 
         {     // use drop-down menu

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" name=\"cform\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");   // use current date
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");

            i = 0;
            courseName = courseA[i];      // get first course name from array

            out.println("<b>Course:</b>&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\" onchange=\"document.cform.submit()\">");

            while ((!courseName.equals( "" )) && (i < cMax)) {

               out.println("<option " + ((courseName.equals( course )) ? "selected " : "") + "value=\"" + courseName + "\">" + courseName + "</option>");
               i++;
               if (i < cMax) courseName = courseA[i];      // get course name from array
               
            }
            
            out.println("</select>");
            out.println("</form>");
            
         } // end either display href links or drop down select
         
      } // end if multi course
      

      if (!event1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + ecolor1 + "\">" + event1 + "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!event2.equals( "" )) {

            out.println("<button type=\"button\" style=\"background:" + ecolor2 + "\">" + event2 + "</button>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         }
      }

      if (!rest1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + rcolor1 + "\">" + rest1 + "</button>");

         if (!rest2.equals( "" )) {

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<button type=\"button\" style=\"background:" + rcolor2 + "\">" + rest2 + "</button>");

            if (!rest3.equals( "" )) {

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<button type=\"button\" style=\"background:" + rcolor3 + "\">" + rest3 + "</button>");

               if ((!rest4.equals( "" )) && (event1.equals( "" ))) {   // do 4 rest's if no events

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<button type=\"button\" style=\"background:" + rcolor4 + "\">" + rest4 + "</button>");
               }
            }
         }
      }

      if (!lott1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + lcolor1 + "\">Lottery Times</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!lott2.equals( "" )) {

            out.println("<button type=\"button\" style=\"background:" + lcolor2 + "\">Lottery Times</button>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

            if (!lott3.equals( "" )) {

               out.println("<button type=\"button\" style=\"background:" + lcolor3 + "\">Lottery Times</button>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            }
         }
      }

      if (!event1.equals( "" ) || !rest1.equals( "" ) || !lott1.equals( "" )) {
         out.println("<br>");
      }

      // *** these two lines came up from after tee sheet
      out.println("</td></tr>");
      out.println("</table>");                            // end of main page table


    
    
    out.println("<br>");
    out.println("<font size=\"2\">");
    out.println("<b>Lottery Request Legend</b>");
    out.println("</font><br><font size=\"1\">");
    out.println("<b>m/-:</b>&nbsp;&nbsp;&nbsp;&nbsp;c = Move to Tee Sheet,&nbsp;&nbsp;&nbsp;minus = Delete Request,&nbsp;&nbsp;&nbsp;");
    out.println("<b>Desired:</b> Desired Time,&nbsp;&nbsp;&nbsp;<b>Assigned:</b> System Assigned Time<br>");
    out.println("<b>Acceptable:</b> Range of Acceptable Times&nbsp;&nbsp;&nbsp;<b>W:</b> Weight");
    out.println("</font>");
    out.println("</center>");
    
    //****************************************************************************
    //
    // start html for display of lottery requests
    //
    //****************************************************************************
    //
      
    
    // IMAGE MARKER FOR POSITIONING
    out.println("<img src=\"/"+rev+"/images/shim.gif\" width=1 height=1 border=0 id=imgMarker name=imgMarker>");
    
    out.println("\n<!-- START OF LOTTERY REQ SHEET HEADER -->");
    out.println(""); //  width=" + total_col_width + " align=center
    out.println("<div id=\"elHContainer2\">");
    out.println("<span class=header style=\"left: " +lcol_start[1]+ "px; width: " +lcol_width[1]+ "px\">c/-</span><span");
    out.println(" class=header style=\"left: " +lcol_start[2]+ "px; width: " +lcol_width[2]+ "px\">Assigned</span><span");
    out.println(" class=header style=\"left: " +lcol_start[3]+ "px; width: " +lcol_width[3]+ "px\">Desired</span><span");
    out.println(" class=header style=\"left: " +lcol_start[4]+ "px; width: " +lcol_width[4]+ "px\">Acceptable</span><span");
    if (course.equals( "-ALL-" )) {
        out.println(" class=header style=\"left: " +lcol_start[5]+ "px; width: " +lcol_width[5]+ "px\">Course</span><span ");
    }
    out.println(" class=header style=\"left: " +lcol_start[6]+ "px; width: " +lcol_width[6]+ "px\">W</span><span id=widthMarker2 ");
    out.println(" class=header style=\"left: " +lcol_start[7]+ "px; width: " +lcol_width[7]+ "px\">Members</span>");
    out.print("</div>\n");
    out.println("<!-- END OF LOTTERY REQ HEADER -->\n");

    
    out.println("\n<!-- START OF LOTTERY REQ SHEET BODY -->");
    out.println("<div id=\"elContainer2\">");
    
    errMsg = "Reading Lottery Requests";
    
    out.println("<!-- lott_name=" + lott_name + " -->");
    out.println("<!-- date=" + date + " -->");
        
    pstmt = con.prepareStatement ("" +
            "SELECT c.fives, l.* " +
            "FROM lreqs3 l, clubparm2 c " +
            "WHERE name = ? AND date = ? AND c.courseName = l.courseName " + 
            (course.equals("-ALL-") ? "" : " AND l.courseName = ? ") + " " +
            "ORDER BY atime1;");
    
    pstmt.clearParameters();
    pstmt.setString(1, lott_name);
    pstmt.setLong(2, date);
    if (!course.equals("-ALL-")) pstmt.setString(3, course);
        
    rs = pstmt.executeQuery();
    
    boolean tmp_found = false;
    boolean tmp_found2 = false;
    
    int lottery_id = 0;
    int nineHole = 0;
    int eighteenHole = 0;
    int friends = 0;
    int groups = 0;
    int max_players = 0;
    int sum_players = 0;
    int req_time = 0;
    int tmp_groups = 1;
    int dts_slot_index = 0; // slot index number
    int request_color = 0;
    
    String fullName = "";
    String cw = "";
    String notes = "";
    String in_use_by = "";
    String time_color = "";
    String dts_defaultF3Color = "#FFFFFF"; // default
    
    
    while (rs.next()) {
        
        tmp_found = true;
        sum_players = 0;
        nineHole = 0;
        eighteenHole = 0;
        in_use_by = "";
        
        lottery_id = rs.getInt("id");
        courseName = rs.getString("courseName");
        req_time = rs.getInt("time");
        notes = rs.getString("notes");
        groups = rs.getInt("groups");
        in_use = rs.getInt("in_use");
        max_players = ((rs.getInt("fives") == 0 || rs.getString("p5").equalsIgnoreCase("No")) ? 4 : 5);
        if (in_use == 1) in_use_by = rs.getString("in_use_by");
        
        j++; // increment the jump label index (where to jump on page)
        
        if (course.equals( "-ALL-" )) { // only display this col if multi course club
            
            for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                if (courseName.equals(courseA[tmp_i])) break;
            }
        }
        /*
        if (groups == 1) {
            
            buildRow(dts_slot_index, 1, course, course_color[tmp_i], bgcolor, max_players, courseCount, lcol_start, lcol_width, index, emailOpt, j, course_color[request_color + 13], false, hideUnavail, lott_name, rs, out);
            dts_slot_index++;
            request_color++;
            
        } else {
            */
            tmp_groups = 1;  // reset
            while (tmp_groups <= groups) {
                
                buildRow(dts_slot_index, tmp_groups, course, course_color[tmp_i], bgcolor, max_players, courseCount, lcol_start, lcol_width, index, emailOpt, j, course_color[request_color + 13], (tmp_groups > 1), hideUnavail, lott_name, rs, out);
                tmp_groups++;
                dts_slot_index++;
                
            } // end while
            
            request_color++;
            
        //} // end if multiple groups
    
    } // end rs loop of lottery requests
    
    out.println("<!-- total_lottery_slots=" + dts_slot_index + " -->");
    out.println("</div>"); // end container
    out.println("\n<!-- END OF LOTTERY REQ SHEET BODY -->");
    out.println("<p>&nbsp;</p>");
    
    int total_lottery_slots = dts_slot_index;
    dts_slot_index = 0;
    in_use = 0;
    //
    // End display notifications
    //
    
    
    
    out.println("<div id=tblLegend style=\"position:absolute\"><p align=center><font size=\"2\">");
    out.println("<b>" + ((index < 0) ? "Old " : "") + "Tee Sheet Legend</b>");
    out.println("</font><br><font size=\"1\">");
    out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;");
    out.println("O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event");
    out.println("</font></p></div>");

    
    //****************************************************************************
    //
    // start html for tee sheet
    //
    //****************************************************************************
    //
    //  To change the position of the tee sheet (static position from top):
    //
    //   Edit file 'dts-styles.css'
    //                     "top" property for elHContainer (header for the main container)
    //                     "top" property for elContainer (main container that holds the tee sheet elements)
    //                     Increment both numbers equally!!!!!!!!!!!!
    //
    //****************************************************************************
    
    String tmpCW = "C/W";
    out.println("<br>");
    out.println("\n<!-- START OF TEE SHEET HEADER -->");
    out.println(""); //  width=" + total_col_width + " align=center
    out.println("<div id=\"elHContainer\">");
    out.println("<span class=header style=\"left: " +col_start[1]+ "px; width: " +col_width[1]+ "px\">+/-</span><span");
    out.println(" class=header style=\"left: " +col_start[2]+ "px; width: " +col_width[2]+ "px\">Time</span><span");
    if (course.equals( "-ALL-" )) {
       out.println(" class=header style=\"left: " +col_start[3]+ "px; width: " +col_width[3]+ "px\">Course</span><span ");
    }
    out.println(" class=header style=\"left: " +col_start[4]+ "px; width: " +col_width[4]+ "px\">F/B</span><span ");
    out.println(" class=header style=\"left: " +col_start[5]+ "px; width: " +col_width[5]+ "px\">Player 1</span><span ");
    out.println(" class=header style=\"left: " +col_start[6]+ "px; width: " +col_width[6]+ "px\">" +tmpCW+ "</span><span ");
    out.println(" class=header style=\"left: " +col_start[7]+ "px; width: " +col_width[7]+ "px\">Player 2</span><span ");
    out.println(" class=header style=\"left: " +col_start[8]+ "px; width: " +col_width[8]+ "px\">" +tmpCW+ "</span><span ");
    out.println(" class=header style=\"left: " +col_start[9]+ "px; width: " +col_width[9]+ "px\">Player 3</span><span ");
    out.println(" class=header style=\"left: " +col_start[10]+ "px; width: " +col_width[10]+ "px\">" +tmpCW+ "</span><span ");
    out.println(" class=header style=\"left: " +col_start[11]+ "px; width: " +col_width[11]+ "px\">Player 4</span><span ");
    out.print(" class=header style=\"left: " +col_start[12]+ "px; width: " +col_width[12]+ "px\"");
  
    if (fivesALL == 0)
    {
       out.print(" id=widthMarker>" +tmpCW+"</span>");
    } else {
       out.print(">" +tmpCW+ "</span><span \n class=header style=\"left: " +col_start[13]+ "px; width: " +col_width[13]+ "px\">Player 5</span><span ");
       out.print(" \n class=header style=\"left: " +col_start[14]+ "px; width: " +col_width[14]+ "px\" id=widthMarker>" +tmpCW+ "</span>");
    }
    
    out.print("</div>\n");
    out.println("<!-- END OF TEE SHEET HEADER -->\n");
    
    String first = "yes";
    
    errMsg = "Get tee times.";
    
    //
    //  Get the tee sheet for this date
    //
    String stringTee = "";
    
    stringTee = "SELECT * " + 
                "FROM teecurr2 " + 
                "WHERE date = ? " + 
                ((course.equals( "-ALL-" )) ? "" : "AND courseName = ? ") + 
                ((hideUnavail.equals("1")) ? "AND time >= ? AND time <= ? " : "") + 
                "ORDER BY time, courseName, fb";
    
    out.println("<!-- index=" + index + " -->");
    out.println("<!-- date=" + date + " -->");
    out.println("<!-- lott_start_time=" + lott_start_time + " -->");
    out.println("<!-- lott_end_time=" + lott_end_time + " -->");
    out.println("<!-- course=" + course + " -->");
    out.println("<!-- stringTee=" + stringTee + " -->");
    
    pstmt = con.prepareStatement (stringTee);
    
    int parm_index = 2;
    int teepast_id = 0;
    
    pstmt.clearParameters();
    pstmt.setLong(1, date);
    if (!course.equals( "-ALL-" )) {
        pstmt.setString(2, course);
        parm_index = 3;
    }
    
    if (hideUnavail.equals("1")) {
        pstmt.setInt(parm_index, lott_start_time); 
        parm_index++;
        pstmt.setInt(parm_index, lott_end_time);
    }
    
    rs = pstmt.executeQuery();
    
    out.println("\n<!-- START OF TEE SHEET BODY -->");
    out.println("<div id=\"elContainer\">\n");
    
    // loop thru each of the tee times
    while (rs.next()) {
    
        player1 = rs.getString("player1");
        player2 = rs.getString("player2");
        player3 = rs.getString("player3");
        player4 = rs.getString("player4");
        player5 = rs.getString("player5");
        p1cw = rs.getString("p1cw");
        p2cw = rs.getString("p2cw");
        p3cw = rs.getString("p3cw");
        p4cw = rs.getString("p4cw");
        p5cw = rs.getString("p5cw");
        p91 = rs.getInt("p91");
        p92 = rs.getInt("p92");
        p93 = rs.getInt("p93");
        p94 = rs.getInt("p94");
        p95 = rs.getInt("p95");
        time = rs.getInt("time");
        fb = rs.getShort("fb");
        courseT = rs.getString("courseName");
        
        teecurr_id = rs.getInt("teecurr_id");
        hr = rs.getInt("hr");
        min = rs.getInt("min");
        event = rs.getString("event");
        ecolor = rs.getString("event_color");
        rest = rs.getString("restriction");
        rcolor = rs.getString("rest_color");
        conf = rs.getString("conf");
        in_use = rs.getInt("in_use");
        type = rs.getInt("event_type");
        hole = rs.getString("hole");
        blocker = rs.getString("blocker");
        rest5 = rs.getString("rest5");
        bgcolor5 = rs.getString("rest5_color");
        lottery = rs.getString("lottery");
        lottery_color = rs.getString("lottery_color");


        //
        //  If course=ALL requested, then set 'fives' option according to this course
        //
        if (course.equals( "-ALL-" )) {
            i = 0;
            loopall:
            while (i < 20) {
               if (courseT.equals( courseA[i] )) {
                  fives = fivesA[i];          // get the 5-some option for this course
                  break loopall;              // exit loop
               }
               i++;
            }
        }

        boolean blnHide = false;

// REMOVED HIDING OF FULL TEE TIMES
/*
         if (hideUnavail.equals("1")) {
             if (fives == 0 || !rest5.equals("") ) {

                 if ( !player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("") ) blnHide = true;

             } else {

                 if ( !player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("")  && !player5.equals("")) blnHide = true;

             }
         }
*/
         // only show this slot if it is NOT blocked
         // and hide it if fives are disallowed and player1-4 full (all slots full already excluded in sql)
         if ( blocker.equals( "" ) && blnHide == false) {      // continue if tee time not blocked - else skip

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            bgcolor = "#FFFFFF";               //default

            if (!event.equals("")) {
               bgcolor = ecolor;
            } else {

               if (!rest.equals("")) {
                  bgcolor = rcolor;
               } else {

                  if (!lottery_color.equals("")) {
                     bgcolor = lottery_color;
                  }
               }
            }

            if (bgcolor.equals("Default")) {
               bgcolor = "#FFFFFF";              //default
            }

            if (bgcolor5.equals("")) {
               bgcolor5 = bgcolor;               // player5 bgcolor = others if 5-somes not restricted
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
            //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
            //
            sfb = "F";       // default Front 9

            if (fb == 1) {

               sfb = "B";
            }

            if (fb == 9) {

               sfb = "O";
            }

            if (type == shotgun) {

               sfb = (!hole.equals("")) ? hole : "S";            // there's an event and its type is 'shotgun'
            }

            // set default color for first three columns
            if (in_use != 0) dts_defaultF3Color = "";
      
            //
            //**********************************
            //  Build the tee time rows
            //**********************************
            //

            if (min < 10) {
               dts_tmp = hr + ":0" + min + ampm;
            } else {
               dts_tmp = hr + ":" + min + ampm;
            }

            out.print("<div id=time_slot_"+ dts_slot_index +" time=\"" + time + "\" course=\"" + courseT + "\" startX=0 startY=0 tid="+((teecurr_id == 0) ? teepast_id : teecurr_id)+" ");
            if (in_use == 0 && index >= 0) {
              // not in use
              out.println("class=timeSlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
            } else {
              // in use
              out.println("class=timeSlotInUse>");
            }


            // col for 'insert' and 'delete' requests
            out.print(" <span id=time_slot_" + dts_slot_index + "_A class=cellDataB style=\"cursor: default; left: " + col_start[1] + "px; width: " + col_width[1] + "px; background-color: #FFFFFF\">");
            j++;                                           // increment the jump label index (where to jump on page)
            out.print("<a name=\"jump" + j + "\"></a>");     // create a jump label for returns
           
            if (in_use == 0 && index >= 0) {
                
                // not in use 
                out.print("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&course=" +courseT+ "&returnCourse=" +course+ "&time=" +time+ "&fb=" +fb+ "&jump=" +j+ "&email=" +emailOpt+ "&first=" +first+ "&lott_name=" + lott_name + "&hide=" + hideUnavail + "&insert=yes\" title=\"Insert a time slot\" alt=\"Insert a time slot\">");
                out.print("<img src=/" +rev+ "/images/dts_newrow.gif width=13 height=13 border=0></a>");
                out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
                out.print("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&course=" +courseT+ "&returnCourse=" +course+ "&time=" +time+ "&fb=" +fb+ "&jump=" +j+ "&email=" +emailOpt+ "&lott_name=" + lott_name + "&hide=" + hideUnavail + "&delete=yes\" title=\"Delete time slot\" alt=\"Remove time slot\">");
                out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");
            
            } else {
            
                // in use
                out.print("<img src=/" +rev+ "/images/busy.gif width=32 height=13 border=0 alt=\"Busy\" title=\"Busy\">");
            
            }
            out.println("</span>");

            // time column
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_time class=cellData hollow=true style=\"left: " + col_start[2] + "px; width: " + col_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_time class=cellData style=\"cursor: default; left: " + col_start[2] + "px; width: " + col_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
            }
              if (min < 10) {
                 out.print(hr + ":0" + min + ampm);
              } else {
                 out.print(hr + ":" + min + ampm);
              }
            out.println("</span>");

            //
            //  Name of Course
            //
            if (course.equals( "-ALL-" )) { // only display this col if this tee sheet is showing more than one course
                
                for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                    if (courseT.equals(courseA[tmp_i])) break;
                }
            
                out.print(" <span id=time_slot_" + dts_slot_index + "_course class=cellDataC style=\"cursor: default; left: " + col_start[3] + "px; width: " + col_width[3] + "px; background-color:" + course_color[tmp_i] + "\">");
                if (!courseT.equals("")) { out.print(fitName(courseT)); }
                out.println("</span>");
            }

            //
            //  Front/Back Indicator  (note:  do we want to display the FBO popup if it's a shotgun event)
            //
            if (in_use == 0 && hole.equals("") && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_FB class=cellData onclick=\"showFBO(this)\" value=\""+sfb+"\" style=\"cursor: hand; left: " + col_start[4] + "px; width: " + col_width[4] + "px\">"); //  background-color: " +dts_defaultF3Color+ "
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_FB class=cellDataB value=\""+sfb+"\" style=\"cursor: default; left: " + col_start[4] + "px; width: " + col_width[4] + "px\">");
            }
            out.print(sfb);
            out.println("</span>");


            //
            //  Add Player 1
            //
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_1 class=cellData drag=true startX="+col_start[5]+" playerSlot=1 style=\"left: " + col_start[5] + "px; width: " + col_width[5] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_1 class=cellData startX="+col_start[5]+" playerSlot=1 style=\"cursor: default; left: " + col_start[5] + "px; width: " + col_width[5] + "px\">");
            }
            if (!player1.equals("")) { out.print(fitName(player1)); }
            out.println("</span>");

            // Player 1 CW
            if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
               dts_tmp = p1cw;
            } else {
               dts_tmp = "";
            }
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_1_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[6] + "px; width: " + col_width[6] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_1_CW class=cellDataB style=\"cursor: default; left: " + col_start[6] + "px; width: " + col_width[6] + "px\">");
            }
            out.print(dts_tmp);
            out.println("</span>");


            //
            //  Add Player 2
            //
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_2 class=cellData drag=true startX="+col_start[7]+" playerSlot=2 style=\"left: " + col_start[7] + "px; width: " + col_width[7] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_2 class=cellData startX="+col_start[7]+" playerSlot=2 style=\"cursor: default; left: " + col_start[7] + "px; width: " + col_width[7] + "px\">");
            }
            if (!player2.equals("")) { out.print(fitName(player2)); }
            out.println(" </span>");

            // Player 2 CW
            if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
               dts_tmp = p2cw;
            } else {
               dts_tmp = "";
            }
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_2_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[8] + "px; width: " + col_width[8] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_2_CW class=cellDataB style=\"cursor: default; left: " + col_start[8] + "px; width: " + col_width[8] + "px\">");
            }
            out.print(dts_tmp);
            out.println("</span>");


            //
            //  Add Player 3
            //
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_3 class=cellData drag=true startX="+col_start[9]+" playerSlot=3 style=\"left: " + col_start[9] + "px; width: " + col_width[9] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_3 class=cellData startX="+col_start[9]+" playerSlot=3 style=\"cursor: default; left: " + col_start[9] + "px; width: " + col_width[9] + "px\">");
            }
            if (!player3.equals("")) { out.print(fitName(player3)); }
            out.println("</span>");

            // Player 3 CW
            if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
               dts_tmp = p3cw;
            } else {
               dts_tmp = "";
            }
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_3_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[10] + "px; width: " + col_width[10] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_3_CW class=cellDataB style=\"cursor: default; left: " + col_start[10] + "px; width: " + col_width[10] + "px\">");
            }
            out.print(dts_tmp);
            out.println("</span>");

            //
            //  Add Player 4
            //
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_4 class=cellData drag=true startX="+col_start[11]+" playerSlot=4 style=\"left: " + col_start[11] + "px; width: " + col_width[11] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_4 class=cellData startX="+col_start[11]+" playerSlot=4 style=\"cursor: default; left: " + col_start[11] + "px; width: " + col_width[11] + "px\">");
            }
            if (!player4.equals("")) { out.print(fitName(player4)); }
            out.println("</span>");

            // Player 4 CW
            if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
               dts_tmp = p4cw;
            } else {
               dts_tmp = "";
            }
            if (in_use == 0 && index >= 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_4_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[12] + "px; width: " + col_width[12] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_4_CW class=cellDataB style=\"cursor: default; left: " + col_start[12] + "px; width: " + col_width[12] + "px\">");
            }
            out.print(dts_tmp);
            out.println("</span>");

            //
            //  Add Player 5 if supported
            //
            if (fivesALL != 0) {        // if 5-somes on any course        (Paul - this is a new flag!!!!)
               if (fives != 0) {        // if 5-somes on this course
                 if (in_use == 0 && index >= 0) {
                   out.print(" <span id=time_slot_" + dts_slot_index + "_player_5 class=cellData drag=true startX="+col_start[13]+" playerSlot=5 style=\"left: " + col_start[13] + "px; width: " + col_width[13] + "px; background-color: " +bgcolor5+ "\">");
                 } else {
                   out.print(" <span id=time_slot_" + dts_slot_index + "_player_5 class=cellData startX="+col_start[13]+" playerSlot=5 style=\"cursor: default; left: " + col_start[13] + "px; width: " + col_width[13] + "px\">");
                 }
                 if (!player5.equals("")) { out.print(fitName(player5)); }
                 out.println("</span>");

                 // Player 5 CW
                 if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                    dts_tmp = p5cw;
                 } else {
                    dts_tmp = "";
                 }
                 if (in_use == 0 && index >= 0) {
                   out.print(" <span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[14] + "px; width: " + col_width[14] + "px; background-color: " +bgcolor5+ "\">");
                 } else {
                   out.print(" <span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB style=\"cursor: default; left: " + col_start[14] + "px; width: " + col_width[14] + "px\">");
                 }
                 out.print(dts_tmp);
                 out.println("</span>");

               } else {       // 5-somes on at least 1 course, but not this one

                 out.print(" <span id=time_slot_" + dts_slot_index + "_player_5 class=cellData startX="+col_start[13]+" playerSlot=5 style=\"cursor: default; left: " + col_start[13] + "px; width: " + col_width[13] + "px;  background-image: url('/v5/images/shade1.gif')\">");
                 out.println("</span>");

                 // Player 5 CW
                 dts_tmp = "";
                 out.print(" <span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB style=\"cursor: default; left: " + col_start[14] + "px; width: " + col_width[14] + "px; background-image: url('/v5/images/shade1.gif')\">");
                 out.print(dts_tmp);
                 out.println("</span>");

               } // end if fives
            } // end if fivesALL

            out.println("</div>"); // end timeslot container div

            dts_slot_index++;    // increment timeslot index counter
            first = "no";        // no longer first time displayed

         }  // end of IF Blocker that escapes building and displaying a particular tee time slot in the sheet

      }  // end of while

      out.println("<br>"); // spacer at bottom of tee sheet
      out.println("\n</div>"); // end main container div holding entire tee sheet
      out.println("<!-- END OF TEE SHEET BODY -->\n");
      out.println("<br><br>\n");

      pstmt.close();

    // write out form for posting tee sheet actions to the server for processing
    out.println("<form name=frmSendAction method=POST action=/" +rev+ "/servlet/Proshop_dlott>");
    out.println("<input type=hidden name=convert value=\"\">");
    out.println("<input type=hidden name=index value=\"" + index + "\">");
    out.println("<input type=hidden name=returnCourse value=\"" + course + "\">");
    out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
    out.println("<input type=hidden name=hide value=\"" + hideUnavail + "\">");
    
    out.println("<input type=hidden name=lott_name value=\"" + lott_name + "\">");
    out.println("<input type=hidden name=lotteryId value=\"\">");
    out.println("<input type=hidden name=group value=\"\">");
    
    out.println("<input type=hidden name=from_tid value=\"\">");
    out.println("<input type=hidden name=to_tid value=\"\">");
    
    out.println("<input type=hidden name=from_course value=\"\">");
    out.println("<input type=hidden name=to_course value=\"\">");
    
    out.println("<input type=hidden name=jump value=\"\">");                  // needs to be set in ....js !!!!!
    out.println("<input type=hidden name=from_time value=\"\">");
    out.println("<input type=hidden name=from_fb value=\"\">");
    out.println("<input type=hidden name=to_time value=\"\">");
    out.println("<input type=hidden name=to_fb value=\"\">");
    out.println("<input type=hidden name=from_player value=\"\">");
    out.println("<input type=hidden name=to_player value=\"\">");
    out.println("<input type=hidden name=to_from value=\"\">");
    out.println("<input type=hidden name=to_to value=\"\">");
    out.println("<input type=hidden name=changeAll value=\"\">");
    out.println("<input type=hidden name=ninehole value=\"\">");
    out.println("</form>");
    
    
    // START OF FBO POPUP WINDOW //
    out.println("<div id=elFBOPopup defaultValue=\"\" style=\"visibility: hidden\" jump=\"\">");
    out.println("<table width=100% height=100% border=0 cellpadding=0 cellspacing=2>");
    out.println("<form name=frmFBO>");
    out.println("<input type=hidden name=jump value=\"\">");
    out.println("<tr><td align=center class=smtext><b><u>Make Selection</u></b></td></tr>");
    out.println("<tr><td class=smtext><input type=radio value=F name=FBO id=FBO_1><label for=\"FBO_1\">Front</label></td></tr>");
    out.println("<tr><td class=smtext><input type=radio value=B name=FBO id=FBO_2><label for=\"FBO_2\">Back</label></td></tr>");
    out.println("<tr><td class=smtext><input type=radio value=O name=FBO id=FBO_3><label for=\"FBO_3\">Crossover</label></td></tr>");
    out.println("<tr><td align=right><a href=\"javascript: cancelFBOPopup()\" class=smtext>cancel</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"javascript: saveFBOPopup()\" class=smtext>save</a>&nbsp;</td></tr>");
    out.println("</form>");
    out.println("</table>");
    out.println("</div>");


    // START OF TRANSPORTATION POPUP WINDOW //
    //
    //  Note:  There can now be up to 16 dynamic Modes of Transportation (proshop can config).
    //         Both the Full Name/Description and the Acronym are specified by the pro.
    //         These names and acronyms will not contain the '9' to indicate 9 holes.
    //         These values can be found in:
    //
    //               parmc.tmode[i]   =  full name description   
    //               parmc.tmodea[i]  =  1 to 3 character acronym  (i = index of 0 - 15)
    //
    //
    out.println("<div id=elTOPopup defaultValue=\"\" fb=\"\" nh=\"\" jump=\"\">");
    out.println("<table width=100% height=100% border=0 cellpadding=0 cellspacing=2>");
    out.println("<form name=frmTransOpt>");
    out.println("<input type=hidden name=jump value=\"\">");
    // loop thru the array and write out a table row for each option
  
    // set tmp_cols to the # of cols this table will have
    // if the # of trans opts is less then 4 then that's the #, otherwise the max is 4
    //   tmode_limit = max number of tmodes available
    //   tmode_count = actual number of tmodes specified for this course
    int tmp_cols = 0;
    if (parmc.tmode_count < 4) {
       tmp_cols = parmc.tmode_count;
    } else {
       tmp_cols = 4;
    }
    int tmp_count = 0;

    out.println("<tr><td align=center class=smtext colspan="+tmp_cols+"><b><u>Make Selection</u></b></td></tr>");

    out.println("<tr>");
    for (int tmp_loop = 0; tmp_loop < parmc.tmode_limit; tmp_loop++) {
      if (!parmc.tmodea[tmp_loop].equals( "" ) && !parmc.tmodea[tmp_loop].equals( null )) {
        out.println("<td nowrap class=smtext><input type=radio value="+parmc.tmodea[tmp_loop]+" name=to id=to_"+tmp_loop+"><label for=\"to_"+tmp_loop+"\">"+parmc.tmode[tmp_loop]+"</label></td>");
        if (tmp_count == 3 || tmp_count == 7 || tmp_count == 11) {
          out.println("</tr><tr>");         // new row
        }
        tmp_count++;
      }
    }
    out.println("</tr>");
  
    out.println("<tr><td bgcolor=black colspan="+tmp_cols+"><img src=/" +rev+ "/images/shim.gif width=100 height=1 border=0></td></tr>");
    out.println("<tr><td class=smtext colspan="+tmp_cols+"><input type=checkbox value=yes name=9hole id=nh><label for=\"nh\">9 Hole</label></td></tr>");
    out.println("<tr><td bgcolor=black colspan="+tmp_cols+"><img src=/" +rev+ "/images/shim.gif width=100 height=1 border=0></td></tr>");
    // "CHANGE ALL" DEFAULT OPTION COULD BE SET HERE
    out.println("<tr><td class=smtext colspan="+tmp_cols+"><input type=checkbox value=yes name=changeAll id=ca><label for=\"ca\">Change All</label></td></tr>");
    out.println("<tr><td align=right colspan="+tmp_cols+"><a href=\"javascript: cancelTOPopup()\" class=smtext>cancel</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"javascript: saveTOPopup()\" class=smtext>save</a>&nbsp;</td></tr>");
    out.println("</form>");
    out.println("</table>");
    out.println("</div>");

    // FINAL JAVASCRIPT FOR THE PAGE, SET VARIABLES THAT WE DIDN'T KNOW TILL AFTER PROCESSING
    out.println("<script type=\"text/javascript\">");
    //out.println("/*if (document.getElementById(\"time_slot_0\")) {");
    //out.println(" slotHeight = document.getElementById(\"time_slot_0\").offsetHeight;");
    //out.println("} else if (document.getElementById(\"time_slot_0\")) {");
    //out.println(" slotHeight = document.getElementById(\"lottery_slot_0\").offsetHeight;");
    //out.println("}*/");
    out.println("var slotHeight = 20;");
    out.println("var g_markerY = document.getElementById(\"imgMarker\").offsetTop;");
    out.println("var g_markerX = document.getElementById(\"imgMarker\").offsetLeft;");
    out.println("var totalTimeSlots = " + (dts_slot_index) + ";");
    out.println("var totalLotterySlots = " + (total_lottery_slots) + ";");
    out.println("var g_transOptTotal = " + tmp_count + ";");
    out.println("var g_pslot1s = " + col_start[5] + ";");
    out.println("var g_pslot1e = " + col_start[6] + ";");
    out.println("var g_pslot2s = " + col_start[7] + ";");
    out.println("var g_pslot2e = " + col_start[8] + ";");
    out.println("var g_pslot3s = " + col_start[9] + ";");
    out.println("var g_pslot3e = " + col_start[10] + ";");
    out.println("var g_pslot4s = " + col_start[11] + ";");
    out.println("var g_pslot4e = " + col_start[12] + ";");
    out.println("var g_pslot5s = " + col_start[13] + ";");
    out.println("var g_pslot5e = " + col_start[14] + ";");
  
    // SIZE UP THE CONTAINER ELEMENTS AND THE TIME SLOTS
    out.println("var e = document.getElementById('widthMarker');");
    out.println("var g_slotWidth = e.offsetLeft + e.offsetWidth;");
    out.println("var e2 = document.getElementById('widthMarker2');");
    out.println("var g_lotterySlotWidth = e2.offsetLeft + e2.offsetWidth;");
    out.println("document.styleSheets[0].rules(0).style.width = (g_slotWidth + 2) + 'px';");            // elHContainer
    out.println("document.styleSheets[0].rules(1).style.width = (g_slotWidth + 2) + 'px';");            // elContainer
    out.println("document.styleSheets[0].rules(7).style.width = g_slotWidth + 'px';");                  // header 
    out.println("document.styleSheets[0].rules(8).style.width = g_slotWidth + 'px';");                  // timeslot
    out.println("document.styleSheets[0].rules(10).style.width = g_lotterySlotWidth + 'px';");           // lotterySlot
    out.println("document.styleSheets[0].rules(12).style.width = (g_lotterySlotWidth + 2) + 'px';");     // elHContainer2
    out.println("document.styleSheets[0].rules(13).style.width = (g_lotterySlotWidth + 2) + 'px';");     // elContainer2
        
    int tmp_offset1 = (total_lottery_slots == 0) ? 0 : (total_lottery_slots * 20) + 2;    // height of lottery container
    int tmp_offset2 = (dts_slot_index == 0) ? 0 : (dts_slot_index * 20) + 2;        // height of tee sheet container
    int tmp_offset3 = (total_lottery_slots == 0) ? 0 : 24; // 24 is height of header
    int tmp_offset4 = 24; // 24 is height of header
    int tmp_offset5 = (total_lottery_slots == 0) ? 40 : 80;
    
    // REPOSITION THE CONTAINERS TO THE MARKER
    out.println("document.getElementById(\"elHContainer2\").style.top=g_markerY;");
    out.println("document.getElementById(\"elHContainer2\").style.left=g_markerX;");
    out.println("document.getElementById(\"elContainer2\").style.top=g_markerY+22;");
    out.println("document.getElementById(\"elContainer2\").style.left=g_markerX;");
    
    if (total_lottery_slots == 0) {
        out.println("document.getElementById(\"elContainer2\").style.visibility = \"hidden\";");
        out.println("document.getElementById(\"elHContainer2\").style.visibility = \"hidden\";");
        out.println("document.getElementById(\"elContainer2\").style.height = \"0px\";");
        out.println("document.getElementById(\"elHContainer2\").style.height = \"0px\";");
    } else {
        // CALL THE POSITIONING CODE FOR EACH OF LOTTERY SLOTS WE CREATED
        out.println("for(x=0;x<=totalLotterySlots-1;x++) eval(\"positionElem('lottery_slot_\" + x + \"', \"+ x +\")\");");
        out.println("document.getElementById(\"elContainer2\").style.height=\"" + tmp_offset1 + "px\";");
    }
    
    // POSITION THE LEGENDS
    out.println("document.getElementById(\"tblLegend\").style.top=g_markerY + " + (tmp_offset1 + (tmp_offset3 * 2)) + ";");
    out.println("document.getElementById(\"tblLegend\").style.width=g_slotWidth;");
    //out.println("document.getElementById(\"tblLottLegend\").style.top=g_markerY;");
    //out.println("document.getElementById(\"tblLottLegend\").style.width=g_slotWidth;");
    
    // POSITION THE TEE SHEET CONTAINER
    out.println("document.getElementById(\"elContainer\").style.top=(g_markerY + " + (tmp_offset1 + tmp_offset4 + tmp_offset5) + ");");
    out.println("document.getElementById(\"elHContainer\").style.top=(g_markerY + " + (tmp_offset1 + tmp_offset5) + ");");
    
    if (dts_slot_index == 0) {
        out.println("document.getElementById(\"elContainer\").style.visibility = \"hidden\";");
    } else {
        // CALL THE POSITIONING CODE FOR EACH OF THE TIME SLOTS WE CREATED
        out.println("for(x=0;x<=totalTimeSlots-1;x++) eval(\"positionElem('time_slot_\" + x + \"', \"+ x +\")\");");
        out.println("document.getElementById(\"elContainer\").style.height=\"" + tmp_offset2 + "px\";");
    }
    out.println("</script>");
    
    out.println("<script type=\"text/javascript\">");
    out.println("function checkCourse() {");
    out.println(" var f = document.getElementById(\"frmSendAction\").returnCourse;");
    out.println(" if (f.value == \"-ALL-\") {");
    out.println("  alert(\"You must select a specific course before going back.\\nYou are currently viewing ALL courses for this day.\");");
    out.println("  return false;");
    out.println(" }");
    out.println(" return true;");
    out.println("}");
    
    out.println("function convert(lottid) {");
    out.println(" f = document.getElementById('frmSendAction');");
    out.println(" f.lotteryId.value = lottid;");
    out.println(" f.convert.value = 'auto';");
    out.println(" f.submit();");
    //out.println(" alert(f.action);");
    out.println("}");
    
    out.println("</script>");
    // END OF OUT FINAL CLIENT SIDE SCRIPT WRITING
    
    }
    catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>Error = " +errMsg);
      out.println("<BR><BR>Exception = " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</center></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  End of HTML page
   //
   out.println("<p>&nbsp;</p>");
   out.println("</body>\n</html>");
   out.close();

 }   // end of doGet


 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

     
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);           // check for intruder
   if (session == null) return;
   
   Connection con = SystemUtils.getCon(session);                    // get DB connection
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
      out.close();
      return;
   }

   
   //
   // Handle the conversion of notifications to teecurr2 entries
   //
   if (req.getParameter("convert") != null) {
       
       if (req.getParameter("convert").equals("yes")) {

           convert(req, out, con, session, resp);
           return;
           
       } else if (req.getParameter("convert").equals("auto")) {
           
           auto_convert(req, out, con, session, resp);
           return;
           
       } else if (req.getParameter("convert").equals("all")) {
           
           convert_all(req, out, con, session, resp);
           return;
           
       }
       
   }
   

   //
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block

   String changeAll = "";
   String ninehole = "";
   String dts_tmp = "";
   String prompt = "";
     
   int skip = 0;

   long date = 0;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  Get this session's username (to be saved in teecurr)
   //
   slotParms.user = (String)session.getAttribute("user");
   slotParms.club = (String)session.getAttribute("club");

try {
    
   //
   //  Get the parms passed
   //
   slotParms.jump = req.getParameter("jump");           // anchor link for page loading
   String indexs = req.getParameter("index");           // # of days ahead of current day
   slotParms.ind = Integer.parseInt(indexs);            // save index value in parm block
   String lott_name = req.getParameter("lott_name");        // lottery name
   slotParms.lottery = lott_name;
   
   //
   //  Get the optional parms
   //
   if (req.getParameter("email") != null) {

      slotParms.sendEmail = req.getParameter("email");
   } else {
      slotParms.sendEmail = "yes";
   }

   if (req.getParameter("returnCourse") != null) {

      slotParms.returnCourse = req.getParameter("returnCourse");
   } else {
      slotParms.returnCourse = "";
   }

   if (req.getParameter("from_course") != null) {

      slotParms.from_course = req.getParameter("from_course");
   } else {
      slotParms.from_course = "";
   }

   if (req.getParameter("to_course") != null) {

      slotParms.to_course = req.getParameter("to_course");
   } else {
      slotParms.to_course = "";
   }

   if (req.getParameter("from_player") != null) {
     
      dts_tmp = req.getParameter("from_player");
        
      if (!dts_tmp.equals( "" )) {
         slotParms.from_player = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_player") != null) {

      dts_tmp = req.getParameter("to_player");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_player = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("from_time") != null) {

      dts_tmp = req.getParameter("from_time");

      if (!dts_tmp.equals( "" )) {
         slotParms.from_time = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_time") != null) {

      dts_tmp = req.getParameter("to_time");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_time = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_from") != null) {

      slotParms.to_from = req.getParameter("to_from");
   }
   if (req.getParameter("to_to") != null) {

      slotParms.to_to = req.getParameter("to_to");
   }
   if (req.getParameter("changeAll") != null) {

      changeAll = req.getParameter("changeAll");
   }
   if (req.getParameter("ninehole") != null) {

      ninehole = req.getParameter("ninehole");
   }
     
   if (req.getParameter("prompt") != null) {        // if 2nd entry (return from prompt)

      prompt = req.getParameter("prompt");
        
      dts_tmp = req.getParameter("date");

      if (!dts_tmp.equals( "" )) {
         date = Integer.parseInt(dts_tmp);
      }
        
      dts_tmp = req.getParameter("to_fb");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_fb = Integer.parseInt(dts_tmp);
      }

      dts_tmp = req.getParameter("from_fb");

      if (!dts_tmp.equals( "" )) {
         slotParms.from_fb = Integer.parseInt(dts_tmp);
      }

      if (req.getParameter("skip") != null) {        // if 2nd entry and skip returned

         dts_tmp = req.getParameter("skip");

         if (!dts_tmp.equals( "" )) {
            skip = Integer.parseInt(dts_tmp);
         }
      }

   } else {
     
      if (req.getParameter("from_fb") != null) {

         dts_tmp = req.getParameter("from_fb");

//  ***************TEMP************
//      System.out.println("from_fb = " +dts_tmp);
//  ***************TEMP************

         slotParms.from_fb = 0;

         if (dts_tmp.equals( "B" )) {

            slotParms.from_fb = 1;
         }
         if (dts_tmp.equals( "O" )) {

            slotParms.from_fb = 9;
         }
      }
      if (req.getParameter("to_fb") != null) {

         dts_tmp = req.getParameter("to_fb");

//  ***************TEMP************
//      System.out.println("to_fb = " +dts_tmp);
//  ***************TEMP************

         slotParms.to_fb = 0;

         if (dts_tmp.equals( "B" )) {

            slotParms.to_fb = 1;
         }
         if (dts_tmp.equals( "O" )) {

            slotParms.to_fb = 9;
         }
      }
   }

 } catch (Exception e) {
     out.println("Error parsing input variables. " + e.toString());
 }
   if (date == 0) {
      //
      //  Get today's date and then use the value passed to locate the requested date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      if (slotParms.ind > 0) {
         cal.add(Calendar.DATE,slotParms.ind);         // roll ahead 'index' days
      }

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      int day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

      month = month + 1;                            // month starts at zero

      slotParms.dd = day;         
      slotParms.mm = month;
      slotParms.yy = year;
      slotParms.day = day_table[day_num];                // get name for day

      date = (year * 10000) + (month * 100) + day;  // create a date field of yyyymmdd
        
   } else {

      if (req.getParameter("day") != null) {

         slotParms.day = req.getParameter("day");
      }

      long lyy = date / 10000;                               // get year
      long lmm = (date - (lyy * 10000)) / 100;               // get month
      long ldd = (date - (lyy * 10000)) - (lmm * 100);       // get day

      slotParms.dd = (int)ldd;
      slotParms.mm = (int)lmm;
      slotParms.yy = (int)lyy;
   }

   //
   //  determine the type of call: Change F/B, Change C/W, Single Player Move, Whole Tee Time Move
   //
   if (!slotParms.to_from.equals( "" ) && !slotParms.to_to.equals( "" )) {

      changeCW(slotParms, changeAll, ninehole, date, req, out, con, resp);     // process Change C/W
      return;
   }

   if (slotParms.to_time == 0) {  // if not C/W and no 'to_time' specified

      changeFB(slotParms, date, req, out, con, resp);              // process Change F/B
      return;
   }

   if ((slotParms.to_player == 0) && (slotParms.from_player == 0)) {

      moveWhole(slotParms, date, prompt, skip, req, out, con, resp);  // process Move Whole Tee Time
      return;
   }

   if (slotParms.from_player > 0 && slotParms.to_player > 0) {

      moveSingle(slotParms, date, prompt, skip, req, out, con, resp);  // process Move Single Tee Time
      return;
   }
  
   //
   //  If we get here, there is an error
   //
   out.println(SystemUtils.HeadTitle("Error in Proshop_dlott"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><BR><BR><H2>Error While Editing Tee Sheet</H2>");
   out.println("<BR><BR>An error has occurred that prevents the system from completing the task.<BR>");
   out.println("<BR>Please try again.  If problem continues, contact ForeTees.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +slotParms.ind+ "&course=" +slotParms.returnCourse+ "\">");
   out.println("Return to Tee Sheet</a></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   
 }  // end of doPost

 
 //
 // returns the player name but enforces a max length for staying in the width allowed
 // change the two positive values to control the output
 //
 private static String fitName(String pName) {
     
   return (pName.length() > 14) ? pName.substring(0, 13) + "..." : pName;
 }

 
 private static String getTime(int time) {

    try {
        
        String ampm = "AM";
        int hr = time / 100;
        int min = time % (hr * 100);

        if (hr == 12) {
            ampm = "PM";
        } else if (hr > 12) {
            hr -= 12;
            ampm = "PM";
        }

        return hr + ":" + SystemUtils.ensureDoubleDigit(min) + " " + ampm;
        
    } catch (Exception ignore) {
        return "N/A";
    }
    
 }
 

 private void buildRow(int slotIndex, int group, String course, String course_color, String bgcolor, int max_players, int courseCount, int lcol_start[], int lcol_width[], int index, String emailOpt, int j, String dts_defaultF3Color, boolean child, String hideUnavail, String lott_name, ResultSet rs, PrintWriter out) {

    // out.println("<!-- slotIndex=" + slotIndex + ", group=" + group + ", max_players=" + max_players + " -->");
     
    try {
     
        
    boolean tmp_found2 = false;
    boolean moved = false;
    String fullName = "";
    String player_list = "";
    int nineHole = 0;
    int x2 = 0; // player data pos in rs
    //int sum_players = 0;

    for (int x = 0; x <= max_players - 1; x++) {

        x2 = x + ((group - 1) * max_players); // position offset
        
        fullName = rs.getString(x2 + 13);
        if (fullName.equals("MOVED")) moved = true;
        nineHole = rs.getInt(x2 + 137);
        //cw = rs.getString(x2 + 63);
        //eighteenHole = 1; //rs.getInt(x + );

        if (!fullName.equals("")) {
            if (tmp_found2) player_list = player_list + (",&nbsp; ");
            player_list = player_list + fullName + ((nineHole == 1) ? "<font style=\"font-size:8px\"> (9)</font>" : "");
            tmp_found2 = true;
            //out.print(fullName + ((nineHole == 1) ? "<font style=\"font-size:8px\"> (9)</font>" : ""));
            //sum_players++;
        }
    }
    
    
    //
    // Start Row
    //
    out.print("<div id=lottery_slot_"+ slotIndex +" time=\"" + rs.getInt("time") + "\" course=\"" + rs.getString("courseName") + "\" startX=0 startY=0 lotteryId=\"" + rs.getInt("id") + "\" group=\"" + group + "\" ");
    if (rs.getInt("in_use") == 0 && !moved) {
        // not in use
        out.println("class=lotterySlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
    } else {
        // in use
        out.println("class=timeSlotInUse>");
    }
    
    
    //
    // Col for 'move' and 'delete' requests
    //
    out.print(" <span id=lottery_slot_" + slotIndex + "_A class=cellDataB style=\"cursor: default; left: " + lcol_start[1] + "px; width: " + lcol_width[1] + "px; background-color: #FFFFFF\">");
    j++;                                            // increment the jump label index (where to jump on page)
    out.print("<a name=\"jump" + j + "\"></a>");    // create a jump label for returns

    if (rs.getInt("in_use") == 0) {

        // not in use
        if (!child) {
            //out.print("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&lotteryId=" + rs.getInt("id") + "&returnCourse=" +course+ "&email=" +emailOpt+ "&convert=yes\" title=\"Move Request\" alt=\"Move Request\">");
            if (rs.getInt("atime1") == 0) {
                // unassigned (will need to be dragged to tee sheet)
                out.print("<img src=/" +rev+ "/images/shim.gif width=13 height=13 border=0>");
            } else {
                // had an assigned time
                out.print("<a href=\"javascript:convert('" + rs.getInt("id") + "')\" onclick=\"void(0)\" title=\"Move Request\" alt=\"Move Request\">");
                out.print("<img src=/" +rev+ "/images/dts_move.gif width=13 height=13 border=0></a>");
            }
            out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
            out.print("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&lotteryId=" + rs.getInt("id") + "&returnCourse=" +course+ "&email=" +emailOpt+ "&lott_name=" +lott_name+ "&hide=" +hideUnavail+ "&delete=yes\" title=\"Delete Request\" alt=\"Remove Request\" onclick=\"return confirm('Are you sure you want to permanently delete this lottery request?');\">");
            out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");
        } else {
            out.print("<img src=/" +rev+ "/images/shim.gif width=13 height=13 border=0>");
            out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
            out.print("<img src=/" +rev+ "/images/shim.gif width=13 height=13 border=0>");
        }

    } else {

        // in use
        out.print("<img src=/" +rev+ "/images/busy.gif width=32 height=13 border=0 alt=\"" + rs.getString("in_use_by") + "\" title=\"Busy\">");

    }
    out.println("</span>");
    
    
    //
    // Assigned Time
    //
    if (rs.getInt("in_use") == 0 && !moved) {
        out.print(" <span id=lottery_slot_" + slotIndex + "_assignTime hollow=true class=cellData style=\"left: " + lcol_start[2] + "px; width: " + lcol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
        //out.print(" <span id=lottery_slot_" + slotIndex + "_time class=cellData style=\"cursor: default; left: " + lcol_start[3] + "px; width: " + lcol_width[3] + "px; background-color: " +dts_defaultF3Color+ "\">");
    } else {
        out.print(" <span id=lottery_slot_" + slotIndex + "_assignTime class=cellData style=\"cursor: default; left: " + lcol_start[2] + "px; width: " + lcol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
    }
    
    // this could cause a / by zero error is atime has not been assigned yet (done on timer)
    
    switch (group) {
        case 1:
            if (rs.getInt("atime1") == 0) {
                out.print("Unassigned");
            } else {
                out.print(getTime(rs.getInt("atime1")));
            }
            break;
        case 2:
            if (rs.getInt("atime2") == 0) {
                out.print("Unassigned");
            } else {
                out.print(getTime(rs.getInt("atime2")));
            }
            break;
        case 3:
            if (rs.getInt("atime3") == 0) {
                out.print("Unassigned");
            } else {
                out.print(getTime(rs.getInt("atime3")));
            }
            break;
        case 4:
            if (rs.getInt("atime4") == 0) {
                out.print("Unassigned");
            } else {
                out.print(getTime(rs.getInt("atime4")));
            }
            break;
        case 5:
            if (rs.getInt("atime5") == 0) {
                out.print("Unassigned");
            } else {
                out.print(getTime(rs.getInt("atime5")));
            }
            break;
    }
    out.print("</span>");
    
    
    //
    // Requested Time
    //
    out.print(" <span id=lottery_slot_" + slotIndex + "_time class=cellData style=\"cursor: default; left: " + lcol_start[3] + "px; width: " + lcol_width[3] + "px; background-color: " +dts_defaultF3Color+ "\">");
    if (!child) out.print(getTime(rs.getInt("time")));
    out.print("</span>");
    
    
    //
    // Acceptable Times
    //
    String ftime = "";      // first acceptable time
    String ltime = "";      // last acceptable time
    int before = rs.getInt("minsbefore");
    int after = rs.getInt("minsafter");

    if (before > 0) {
        ftime = getTime(SystemUtils.getFirstTime(rs.getInt("time"), before));    // get earliest time for this request
    } else {
        ftime = getTime(rs.getInt("time")); 
    }
    if (after > 0) {
        ltime = getTime(SystemUtils.getLastTime(rs.getInt("time"), after));     // get latest time for this request
    } else {
        ltime = getTime(rs.getInt("time")); 
    }
    
    out.print(" <span id=lottery_slot_" + slotIndex + "_oktimes class=cellData style=\"cursor: default; left: " + lcol_start[4] + "px; width: " + lcol_width[4] + "px; background-color: " +dts_defaultF3Color+ "\">");
    if (!child && !ftime.equals("")) out.print(ftime + " - " + ltime);
    out.print("</span>");
    
    
    //
    // Course
    //
    if (course.equals( "-ALL-" )) { // only display this col if multi course club

        out.print("<span id=lottery_slot_" + slotIndex + "_course class=cellDataC style=\"cursor: default; left: " + lcol_start[5] + "px; width: " + lcol_width[5] + "px; background-color:" + course_color + "\">");
        if (!rs.getString("courseName").equals("")) { out.print(fitName(rs.getString("courseName"))); }
        out.print("</span>");
    }
    
    
    //
    // Weight
    //
    out.print(" <span id=lottery_slot_" + slotIndex + "_weight class=cellData style=\"cursor: default; left: " + lcol_start[6] + "px; width: " + lcol_width[6] + "px;\">");
    out.print(rs.getInt("weight"));
    out.print("</span>");
    
    
    //
    // Players
    //
    out.print("<span id=lottery_slot_" + slotIndex + "_members class=cellDataB value=\"\" style=\"cursor: default; left: " + lcol_start[7] + "px; width: " + lcol_width[7] + "px; text-align: left\">&nbsp;");
    if (child) out.println("&nbsp;&#187;&nbsp;");
    //if (child) out.println("<span style=\"position: relative;text-align: left;\"><img src=\"/" +rev+ "/images/dts_child.gif\" width=12 height=12 border=0 valign=top></span>");
    out.print(player_list);
    
    if (!rs.getString("notes").equals("")) {

        // this won't work in rl but is meant to show a dynamic popup, or we can spawn a small popup window that will show the notes
        out.println("&nbsp; &nbsp; <img src=\"/"+rev+"/images/notes.gif\" width=10 height=12 border=0 alt=\"" + rs.getString("notes") + "\">");
    }

    out.print("</span>");

    /*
    out.print("<span class=cellDataB style=\"cursor: default; left: " + lcol_start[6] + "px; width: " + lcol_width[6] + "px\">");
    out.print(sum_players);
    out.println("</span>");

    out.print("<span class=cellDataB style=\"cursor: default; left: " + lcol_start[7] + "px; width: " + lcol_width[7] + "px\">");
    */
     //out.print(((nineHole == 0) ? "18" : "9"));
    /*
     *
    if (nineHole == 1 && eighteenHole == 1) {
        out.print("mixed");
    } else if (nineHole == 1) {
        out.print("9");
    } else if (eighteenHole == 1) {
        out.print("18");
    }
    */
    out.println("</span>");

    out.println("</div>");
    
    } catch (SQLException exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);  
        //out.println("</div>");  
    }
 }
 
 
 private void convert_all(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {
     
     
     
 }
 
 
 private void convert(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {
     

    Statement stmt = null;
    ResultSet rs = null;

    //
    //  Get this session's attributes
    //
    String user = "";
    String club = "";
    user = (String)session.getAttribute("user");
    club = (String)session.getAttribute("club");

    int index = 0;
    int fives = 0;      // for tee time we are dragging to
    int count = 0;
    int group = 0;
    int lottery_id = 0;
    int teecurr_id = 0;
    boolean overRideFives = false;

    String hideUnavail = req.getParameter("hide");
    if (hideUnavail == null) hideUnavail = "";
    String sindex = req.getParameter("index");          //  day index value (needed by _sheet on return)
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
    String suppressEmails = "no";
    
    String slid = "";
    String stid = "";
    String sgroup = "";
    if (req.getParameter("to_tid") != null) stid = req.getParameter("to_tid");
    if (req.getParameter("lotteryId") != null) slid = req.getParameter("lotteryId");
    if (req.getParameter("group") != null) sgroup = req.getParameter("group");
    
    String convert = req.getParameter("convert");
    if (convert == null) convert = "";
    
    String lott_name = req.getParameter("lott_name");
    if (lott_name == null) lott_name = "";
    
    if (req.getParameter("overRideFives") != null && req.getParameter("overRideFives").equals("yes")) {
        overRideFives = true;
    }
    
    if (req.getParameter("suppressEmails") != null) {             // if email parm exists
        suppressEmails = req.getParameter("suppressEmails");
    }
    
    //
    //  parm block to hold the tee time parms
    //
    parmSlot slotParms = new parmSlot();          // allocate a parm block
        
    /*
    if (convert.equals("auto")) {
        
        // lookup the teecurr_id from the date/time/fb/course that was assigned for this request
        try {
            
            PreparedStatement pstmt = con.prepareStatement("" +
                    "SELECT t.teecurr_id " +
                    "FROM teecurr2 t, lreqs3 l " +
                    "WHERE l.id = ? AND l.courseName = t.courseName AND l.atime1 = t.time AND l.afb = t.fb " +
                    "LIMIT 1");
            
            pstmt.clearParameters();
            pstmt.setInt(1, lottery_id);
            rs = pstmt.executeQuery();
            
            if ( rs.next() ) teecurr_id = rs.getInt(1);
            
        }
        catch (NumberFormatException exp) {
            SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
            return;
        }
        
    } else {
*/
    
    //
    //  Convert the values from string to int
    //
    try {

        lottery_id = Integer.parseInt(slid);
        teecurr_id = Integer.parseInt(stid);
        group = Integer.parseInt(sgroup);
        index = Integer.parseInt(sindex);
    }
    catch (NumberFormatException exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
    }
    
    
    //
    // Get fives value for this course (from teecurr_id)
    //
    if (!overRideFives) {
        try {

            PreparedStatement pstmtc = con.prepareStatement (
                "SELECT fives " + 
                "FROM clubparm2 c, teecurr2 t " + 
                "WHERE c.courseName = t.courseName AND t.teecurr_id = ?");

            pstmtc.clearParameters();
            pstmtc.setInt(1, teecurr_id);
            rs = pstmtc.executeQuery();

            if (rs.next()) fives = rs.getInt("fives");

            pstmtc.close();
        }
        catch (Exception e) {
        }
    } else {
        fives = 1;
    }
    
    slotParms.ind = index;                      // index value
    slotParms.club = club;                    // name of club
    slotParms.returnCourse = returnCourse;    // name of course for return to _sheet
    slotParms.suppressEmails = suppressEmails;
    
    //
    //  Load parameter object
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            slotParms.player1 = rs.getString("player1");
            slotParms.player2 = rs.getString("player2");
            slotParms.player3 = rs.getString("player3");
            slotParms.player4 = rs.getString("player4");
            slotParms.player5 = rs.getString("player5");
            slotParms.user1 = rs.getString("username1");
            slotParms.user2 = rs.getString("username2");
            slotParms.user3 = rs.getString("username3");
            slotParms.user4 = rs.getString("username4");
            slotParms.user5 = rs.getString("username5");
            slotParms.p1cw = rs.getString("p1cw");
            slotParms.p2cw = rs.getString("p2cw");
            slotParms.p3cw = rs.getString("p3cw");
            slotParms.p4cw = rs.getString("p4cw");
            slotParms.p5cw = rs.getString("p5cw");
            slotParms.in_use = rs.getInt("in_use");
            slotParms.in_use_by = rs.getString("in_use_by");
            slotParms.userg1 = rs.getString("userg1");
            slotParms.userg2 = rs.getString("userg2");
            slotParms.userg3 = rs.getString("userg3");
            slotParms.userg4 = rs.getString("userg4");
            slotParms.userg5 = rs.getString("userg5");
            slotParms.orig_by = rs.getString("orig_by");
            slotParms.pos1 = rs.getShort("pos1");
            slotParms.pos2 = rs.getShort("pos2");
            slotParms.pos3 = rs.getShort("pos3");
            slotParms.pos4 = rs.getShort("pos4");
            slotParms.pos5 = rs.getShort("pos5");
            slotParms.rest5 = rs.getString("rest5");         
        }
        out.println("<!-- DONE LOADING slotParms WITH teecurr2 DATA -->");
        pstmt.close();

    }
    catch (Exception e) {

        out.println("<p>Error: "+e.toString()+"</p>");
    }

    // make sure there are enough open player slots
    int max_group_size = (fives == 0 || slotParms.p5.equalsIgnoreCase("No")) ? 4 : 5;
    int open_slots = 0;
    boolean has_players = false;
    if (slotParms.player1.equals("")) { open_slots++; } else { has_players = true; }
    if (slotParms.player2.equals("")) open_slots++;
    if (slotParms.player3.equals("")) open_slots++;
    if (slotParms.player4.equals("")) open_slots++;
    if (slotParms.player5.equals("") && slotParms.rest5.equals("") && fives == 1 && slotParms.p5.equalsIgnoreCase("Yes")) open_slots++;
    
    if (slotParms.orig_by.equals( "" )) {    // if originator field still empty (allow this person to grab this tee time again)
    
        slotParms.orig_by = user;             // set this user as the originator
    }
    
    out.println("<!-- open_slots="+open_slots+" | has_players="+has_players+" -->");
    
    //
    // Check in-use indicators
    //
    if (slotParms.in_use == 1 && !slotParms.in_use_by.equalsIgnoreCase( user )) {    // if time slot in use and not by this user
    
        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
        out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system!<BR>");
        out.println("<BR>The system timed out and released the tee time.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
        }
        out.println("</form></font>");
            
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    boolean teeTimeFull = false;
    boolean allowFives = (fives == 1) ? true : false; // does the course we are dragging to allow 5-somes?
    
    String fields = "";
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
    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";
    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";
    int p91 = 0;
    int p92 = 0;
    int p93 = 0;
    int p94 = 0;
    int p95 = 0;
    int groups = 0;
    int group_size = 0;
    int group_players = 0;
    int tmp_added = 0;
    int field_offset = 0;
    
    //
    // Load lottery request data
    //
    try {
        
        PreparedStatement pstmt = con.prepareStatement ("" +
                "SELECT l.*, c.fives " +
                "FROM lreqs3 l, clubparm2 c " +
                "WHERE id = ? AND l.courseName = c.courseName;");
        
        pstmt.clearParameters();
        pstmt.setInt(1, lottery_id);
        
        rs = pstmt.executeQuery();
        
        if ( rs.next() ) {
            
            groups = rs.getInt("groups");
            group_size = (rs.getInt("fives") == 0 || !rs.getString("p5").equalsIgnoreCase("Yes")) ? 4 : 5;
            field_offset = (group - 1) * group_size; // player data pos in rs
            
            player1 = rs.getString(12 + field_offset);
            player2 = rs.getString(13 + field_offset);
            player3 = rs.getString(14 + field_offset);
            player4 = rs.getString(15 + field_offset);
            player5 = rs.getString(16 + field_offset);
            
            user1 = rs.getString(37 + field_offset);
            user2 = rs.getString(38 + field_offset);
            user3 = rs.getString(39 + field_offset);
            user4 = rs.getString(40 + field_offset);
            user5 = rs.getString(41 + field_offset);
            
            p1cw = rs.getString(62 + field_offset);
            p2cw = rs.getString(63 + field_offset);
            p3cw = rs.getString(64 + field_offset);
            p4cw = rs.getString(65 + field_offset);
            p5cw = rs.getString(66 + field_offset);
            
            userg1 = rs.getString(109 + field_offset);
            userg2 = rs.getString(110 + field_offset);
            userg3 = rs.getString(111 + field_offset);
            userg4 = rs.getString(112 + field_offset);
            userg5 = rs.getString(113 + field_offset);
            
            p91 = rs.getInt(136 + field_offset);
            p92 = rs.getInt(137 + field_offset);
            p93 = rs.getInt(138 + field_offset);
            p94 = rs.getInt(139 + field_offset);
            p95 = rs.getInt(140 + field_offset);
            
        }
        
        if (!player1.equals("")) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player1, user1, p1cw, p91, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player2.equals("")) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player2, user2, p2cw, p92, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player3.equals("")) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player3, user3, p3cw, p93, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player4.equals("")) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player4, user4, p4cw, p94, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player5.equals("") && allowFives && group_size == 5) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player5, user5, p5cw, p95, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
    } catch(Exception exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);
        //return;
    }
    
    
    // Let see if all players from this request where moved
    if ( teeTimeFull || tmp_added == 0 ) {
    
        out.println(SystemUtils.HeadTitle("Unable to Add All Players"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Unable to Add All Players</H3><BR>");
        out.println("<BR>Sorry we were not able to add all the players to this tee time.<br><br>");
        out.println("<BR><BR>No changes were made to the lottery request or tee sheet.");
        out.println("<BR><BR>");
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    out.println("<!-- field_offset=" + field_offset + " -->");
    out.println("<!-- group=" + group + " | max_group_size=" + max_group_size + " -->");
    out.println("<!-- groups=" + groups + " | group_size=" + group_size + " | group_players=" + group_players + " -->");
    out.println("<!-- lottery_id="+lottery_id+" | teecurr_id="+teecurr_id+" | tmp_added="+tmp_added+" -->");
    
    
    
    // first lets see if they are trying to fill the 5th player slot when it is unavailable for this course
    if ( !slotParms.player5.equals("") && fives == 0 ) { // ( (!slotParms.rest5.equals("") && overRideFives == false) || fives == 0)
    
        out.println(SystemUtils.HeadTitle("5-some Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted</H3><BR>");
        out.println("<BR>Sorry, <b>5-somes</b> are not allowed on this course.<br><br>");
        out.println("<BR><BR>Please move the lottery request to another course.");
        out.println("<BR><BR>");
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        
        /*
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"lotteryId\" value=\"" + lottery_id + "\">");
        out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
        out.println("<input type=\"hidden\" name=\"group\" value=\"" + group + "\">");
        out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
        */
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
        
    } // end 5-some rejection
    
    
    //
    // Update entry in teecurr2
    //
    try {

        PreparedStatement pstmt6 = con.prepareStatement (
             "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
             "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
             "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
             "hndcp4 = ?, player5 = ?, username5 = ?, " + 
             "p5cw = ?, hndcp5 = ?, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " +
             "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
             "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
             "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ? " +
             "WHERE teecurr_id = ?");

        pstmt6.clearParameters();        // clear the parms
        pstmt6.setString(1, slotParms.player1);
        pstmt6.setString(2, slotParms.player2);
        pstmt6.setString(3, slotParms.player3);
        pstmt6.setString(4, slotParms.player4);
        pstmt6.setString(5, slotParms.user1);
        pstmt6.setString(6, slotParms.user2);
        pstmt6.setString(7, slotParms.user3);
        pstmt6.setString(8, slotParms.user4);
        pstmt6.setString(9, slotParms.p1cw);
        pstmt6.setString(10, slotParms.p2cw);
        pstmt6.setString(11, slotParms.p3cw);
        pstmt6.setString(12, slotParms.p4cw);
        pstmt6.setFloat(13, slotParms.hndcp1);
        pstmt6.setFloat(14, slotParms.hndcp2);
        pstmt6.setFloat(15, slotParms.hndcp3);
        pstmt6.setFloat(16, slotParms.hndcp4);
        pstmt6.setString(17, slotParms.player5);
        pstmt6.setString(18, slotParms.user5);
        pstmt6.setString(19, slotParms.p5cw);
        pstmt6.setFloat(20, slotParms.hndcp5);
        pstmt6.setString(21, slotParms.notes);
        pstmt6.setInt(22, 0); // hide
        pstmt6.setInt(23, 0); // proNew
        pstmt6.setInt(24, 0); // proMod
        pstmt6.setString(25, slotParms.mNum1);
        pstmt6.setString(26, slotParms.mNum2);
        pstmt6.setString(27, slotParms.mNum3);
        pstmt6.setString(28, slotParms.mNum4);
        pstmt6.setString(29, slotParms.mNum5);
        pstmt6.setString(30, slotParms.userg1);
        pstmt6.setString(31, slotParms.userg2);
        pstmt6.setString(32, slotParms.userg3);
        pstmt6.setString(33, slotParms.userg4);
        pstmt6.setString(34, slotParms.userg5);
        pstmt6.setString(35, slotParms.orig_by);
        pstmt6.setString(36, slotParms.conf);
        pstmt6.setInt(37, slotParms.p91);
        pstmt6.setInt(38, slotParms.p92);
        pstmt6.setInt(39, slotParms.p93);
        pstmt6.setInt(40, slotParms.p94);
        pstmt6.setInt(41, slotParms.p95);
        pstmt6.setInt(42, slotParms.pos1);
        pstmt6.setInt(43, slotParms.pos2);
        pstmt6.setInt(44, slotParms.pos3);
        pstmt6.setInt(45, slotParms.pos4);
        pstmt6.setInt(46, slotParms.pos5);

        pstmt6.setInt(47, teecurr_id);

        count = pstmt6.executeUpdate();      // execute the prepared stmt
    } 
    catch (Exception exp) {
        
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
        return;
    }
    
    // if the tee time was updated then remove the request
    if (count == 1) {
        
        // depending on how many groups this request had, we'll either delete or modify the request
        if (groups == 1) {
            
            // there was one group in the request - just delete the request
            try {
                
                PreparedStatement pstmt = con.prepareStatement("DELETE FROM lreqs3 WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, lottery_id);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);
                return;
            }
            
        } else {
            
            // there were multiple groups in this request, lets mark the specific group
            int tmp_pos = 0;
            int tmp = 0;
            int tmp_loop = 1;
            
            // get the position of the first player for the group we moved
            if (group == 1) 
                { tmp_pos = 0; } 
            else 
                { tmp_pos = group_size * (group - 1); }
            
            // build the fields string for the sql statement
            for (tmp = tmp_pos; tmp <= tmp_pos + group_size; tmp++) {
                
                if (tmp_loop > tmp_added) break;
                
                fields = fields + " player" + (tmp + 1) + " = 'MOVED',";
                tmp_loop++;
            }
            
            // trim trailing comma
            fields=fields.substring(0, fields.length() - 1);
            
            out.println("<!-- tmp_pos=" + tmp_pos + " -->");
            out.println("<!-- fields=" + fields + " -->");
            
            try {
                
                PreparedStatement pstmt = con.prepareStatement("UPDATE lreqs3 SET " + fields + " WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, lottery_id);
                pstmt.executeUpdate();
                pstmt.close();
               
            } catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
                return;
            }
        
            
            // now check all the players in this request and if they equal 'MOVED' then we can delete this request
            try {
                
                PreparedStatement pstmt = con.prepareStatement ("" +
                        "SELECT * FROM lreqs3 WHERE id = ? AND " + 
                            "(player1 = 'MOVED' OR player1 = '') AND " + 
                            "(player2 = 'MOVED' OR player2 = '') AND " + 
                            "(player3 = 'MOVED' OR player3 = '') AND " + 
                            "(player4 = 'MOVED' OR player4 = '') AND " + 
                            "(player5 = 'MOVED' OR player5 = '') AND " + 
                            "(player6 = 'MOVED' OR player6 = '') AND " + 
                            "(player7 = 'MOVED' OR player7 = '') AND " + 
                            "(player8 = 'MOVED' OR player8 = '') AND " + 
                            "(player9 = 'MOVED' OR player9 = '') AND " + 
                            "(player10 = 'MOVED' OR player10 = '') AND " + 
                            "(player11 = 'MOVED' OR player11 = '') AND " + 
                            "(player12 = 'MOVED' OR player12 = '') AND " + 
                            "(player13 = 'MOVED' OR player13 = '') AND " + 
                            "(player14 = 'MOVED' OR player14 = '') AND " + 
                            "(player15 = 'MOVED' OR player15 = '') AND " + 
                            "(player16 = 'MOVED' OR player16 = '') AND " + 
                            "(player17 = 'MOVED' OR player17 = '') AND " + 
                            "(player18 = 'MOVED' OR player18 = '') AND " + 
                            "(player19 = 'MOVED' OR player19 = '') AND " + 
                            "(player20 = 'MOVED' OR player20 = '') AND " + 
                            "(player21 = 'MOVED' OR player21 = '') AND " + 
                            "(player22 = 'MOVED' OR player22 = '') AND " + 
                            "(player23 = 'MOVED' OR player23 = '') AND " + 
                            "(player24 = 'MOVED' OR player24 = '') AND " + 
                            "(player25 = 'MOVED' OR player25 = '');");

                pstmt.clearParameters();
                pstmt.setInt(1, lottery_id);

                rs = pstmt.executeQuery();

                if ( rs.next() ) {
                    
                    pstmt = con.prepareStatement("DELETE FROM lreqs3 WHERE id = ?");
                    pstmt.clearParameters();
                    pstmt.setInt(1, lottery_id);
                    pstmt.executeUpdate();
                    pstmt.close();
                    
                }
                
                pstmt.close();
               
            } catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
                return;
            }
        }
        
    } // end if updated teecurr2 entry
    
    out.println("<!-- count="+count+" -->");
    
    out.println("<!-- ");
    out.println("slotParms.player1=" + slotParms.player1);
    out.println("slotParms.player2=" + slotParms.player2);
    out.println("slotParms.player3=" + slotParms.player3);
    out.println("slotParms.player4=" + slotParms.player4);
    out.println("slotParms.player5=" + slotParms.player5);
    out.println("");
    out.println("slotParms.p1cw=" + slotParms.p1cw);
    out.println("slotParms.p2cw=" + slotParms.p2cw);
    out.println("slotParms.p3cw=" + slotParms.p3cw);
    out.println("slotParms.p4cw=" + slotParms.p4cw);
    out.println("slotParms.p5cw=" + slotParms.p5cw);
    out.println("");
    out.println("slotParms.p91=" + slotParms.p91);
    out.println("slotParms.p92=" + slotParms.p92);
    out.println("slotParms.p93=" + slotParms.p93);
    out.println("slotParms.p94=" + slotParms.p94);
    out.println("slotParms.p95=" + slotParms.p95);
    out.println("");
    out.println("slotParms.mNum1=" + slotParms.mNum1);
    out.println("slotParms.mNum2=" + slotParms.mNum2);
    out.println("slotParms.mNum3=" + slotParms.mNum3);
    out.println("slotParms.mNum4=" + slotParms.mNum4);
    out.println("slotParms.mNum5=" + slotParms.mNum5);
    out.println("");
    out.println(" -->");
    
    //
    // Completed update - reload page
    //
    out.println("<meta http-equiv=\"Refresh\" content=\"2; url=/" +rev+ "/servlet/Proshop_dlott?index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&lott_name=" + lott_name + "&hide="+hideUnavail+"\">");

    out.close();
    return;
 }
 
 
 
 private void auto_convert(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {
   
   Statement estmt = null;
   Statement stmtN = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmtd = null;
   PreparedStatement pstmtd2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String course = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";

   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String p6cw = "";
   String p7cw = "";
   String p8cw = "";
   String p9cw = "";
   String p10cw = "";
   String p11cw = "";
   String p12cw = "";
   String p13cw = "";
   String p14cw = "";
   String p15cw = "";
   String p16cw = "";
   String p17cw = "";
   String p18cw = "";
   String p19cw = "";
   String p20cw = "";
   String p21cw = "";
   String p22cw = "";
   String p23cw = "";
   String p24cw = "";
   String p25cw = "";

   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String user6 = "";
   String user7 = "";
   String user8 = "";
   String user9 = "";
   String user10 = "";
   String user11 = "";
   String user12 = "";
   String user13 = "";
   String user14 = "";
   String user15 = "";
   String user16 = "";
   String user17 = "";
   String user18 = "";
   String user19 = "";
   String user20 = "";
   String user21 = "";
   String user22 = "";
   String user23 = "";
   String user24 = "";
   String user25 = "";

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String userg6 = "";
   String userg7 = "";
   String userg8 = "";
   String userg9 = "";
   String userg10 = "";
   String userg11 = "";
   String userg12 = "";
   String userg13 = "";
   String userg14 = "";
   String userg15 = "";
   String userg16 = "";
   String userg17 = "";
   String userg18 = "";
   String userg19 = "";
   String userg20 = "";
   String userg21 = "";
   String userg22 = "";
   String userg23 = "";
   String userg24 = "";
   String userg25 = "";

   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";

   String color = "";
   String p5 = "";
   String type = "";
   String pref = "";
   String approve = "";
   String day = "";
   String notes = "";
   String in_use_by = "";
   String orig_by = "";
   String parm = "";
   String hndcps = "";

   String player5T = "";
   String user5T = "";
   String p5cwT = "";

   String errorMsg = "";

   String [] userA = new String [25];            // array to hold usernames

   long id = 0;
   
   int date = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int p96 = 0;
   int p97 = 0;
   int p98 = 0;
   int p99 = 0;
   int p910 = 0;
   int p911 = 0;
   int p912 = 0;
   int p913 = 0;
   int p914 = 0;
   int p915 = 0;
   int p916 = 0;
   int p917 = 0;
   int p918 = 0;
   int p919 = 0;
   int p920 = 0;
   int p921 = 0;
   int p922 = 0;
   int p923 = 0;
   int p924 = 0;
   int p925 = 0;

   int i = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int fb = 0;
   int afb = 0;
   int afb2 = 0;
   int afb3 = 0;
   int afb4 = 0;
   int afb5 = 0;
   int count = 0;
   int groups = 0;
   int time = 0;
   int rtime = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int players = 0;
   int hide = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int proxMins = 0;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;

   float hndcp1 = 99;
   float hndcp2 = 99;
   float hndcp3 = 99;
   float hndcp4 = 99;
   float hndcp5 = 99;

   boolean ok = true;

   int lottery_id = 0;
   int index = 0;
   String slid = "";
   String sindex = req.getParameter("index");
   String hideUnavail = req.getParameter("hide");
   String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
   if (req.getParameter("lotteryId") != null) slid = req.getParameter("lotteryId");
   String name = req.getParameter("lott_name");
   if (name == null) name = "";
   
    //
    //  Get the lottery_id
    //
    try {

        lottery_id = Integer.parseInt(slid);
        index = Integer.parseInt(sindex);
    }
    catch (NumberFormatException exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
        return;
    }
   

   try {

      errorMsg = "Error in Proshop_dlott:auto_convert (get lottery request): ";

      //
      //  Get the Lottery Requests for the lottery passed
      //
      pstmt = con.prepareStatement (
         "SELECT mm, dd, yy, day, time, " +
         "player1, player2, player3, player4, player5, player6, player7, player8, player9, player10, " +
         "player11, player12, player13, player14, player15, player16, player17, player18, player19, player20, " +
         "player21, player22, player23, player24, player25, " +
         "user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, " +
         "user11, user12, user13, user14, user15, user16, user17, user18, user19, user20, " +
         "user21, user22, user23, user24, user25, " +
         "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, p9cw, p10cw, " +
         "p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, p18cw, p19cw, p20cw, " +
         "p21cw, p22cw, p23cw, p24cw, p25cw, " +
         "notes, hideNotes, fb, proNew, proMod, memNew, memMod, id, groups, atime1, atime2, atime3, " +
         "atime4, atime5, afb, p5, players, userg1, userg2, userg3, userg4, userg5, userg6, userg7, userg8, " +
         "userg9, userg10, userg11, userg12, userg13, userg14, userg15, userg16, userg17, userg18, userg19, " +
         "userg20, userg21, userg22, userg23, userg24, userg25, orig_by, " +
         "p91, p92, p93, p94, p95, p96, p97, p98, p99, p910, " +
         "p911, p912, p913, p914, p915, p916, p917, p918, p919, p920, " +
         "p921, p922, p923, p924, p925, afb2, afb3, afb4, afb5, type, courseName, date " +
         "FROM lreqs3 " +
         "WHERE id = ? AND state = 2");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, lottery_id);

      rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first

      while (rs.next()) {

         mm = rs.getInt(1);
         dd = rs.getInt(2);
         yy = rs.getInt(3);
         day = rs.getString(4);
         rtime = rs.getInt(5);
         player1 = rs.getString(6);
         player2 = rs.getString(7);
         player3 = rs.getString(8);
         player4 = rs.getString(9);
         player5 = rs.getString(10);
         player6 = rs.getString(11);
         player7 = rs.getString(12);
         player8 = rs.getString(13);
         player9 = rs.getString(14);
         player10 = rs.getString(15);
         player11 = rs.getString(16);
         player12 = rs.getString(17);
         player13 = rs.getString(18);
         player14 = rs.getString(19);
         player15 = rs.getString(20);
         player16 = rs.getString(21);
         player17 = rs.getString(22);
         player18 = rs.getString(23);
         player19 = rs.getString(24);
         player20 = rs.getString(25);
         player21 = rs.getString(26);
         player22 = rs.getString(27);
         player23 = rs.getString(28);
         player24 = rs.getString(29);
         player25 = rs.getString(30);
         user1 = rs.getString(31);
         user2 = rs.getString(32);
         user3 = rs.getString(33);
         user4 = rs.getString(34);
         user5 = rs.getString(35);
         user6 = rs.getString(36);
         user7 = rs.getString(37);
         user8 = rs.getString(38);
         user9 = rs.getString(39);
         user10 = rs.getString(40);
         user11 = rs.getString(41);
         user12 = rs.getString(42);
         user13 = rs.getString(43);
         user14 = rs.getString(44);
         user15 = rs.getString(45);
         user16 = rs.getString(46);
         user17 = rs.getString(47);
         user18 = rs.getString(48);
         user19 = rs.getString(49);
         user20 = rs.getString(50);
         user21 = rs.getString(51);
         user22 = rs.getString(52);
         user23 = rs.getString(53);
         user24 = rs.getString(54);
         user25 = rs.getString(55);
         p1cw = rs.getString(56);
         p2cw = rs.getString(57);
         p3cw = rs.getString(58);
         p4cw = rs.getString(59);
         p5cw = rs.getString(60);
         p6cw = rs.getString(61);
         p7cw = rs.getString(62);
         p8cw = rs.getString(63);
         p9cw = rs.getString(64);
         p10cw = rs.getString(65);
         p11cw = rs.getString(66);
         p12cw = rs.getString(67);
         p13cw = rs.getString(68);
         p14cw = rs.getString(69);
         p15cw = rs.getString(70);
         p16cw = rs.getString(71);
         p17cw = rs.getString(72);
         p18cw = rs.getString(73);
         p19cw = rs.getString(74);
         p20cw = rs.getString(75);
         p21cw = rs.getString(76);
         p22cw = rs.getString(77);
         p23cw = rs.getString(78);
         p24cw = rs.getString(79);
         p25cw = rs.getString(80);
         notes = rs.getString(81);
         hide = rs.getInt(82);
         fb = rs.getInt(83);
         proNew = rs.getInt(84);
         proMod = rs.getInt(85);
         memNew = rs.getInt(86);
         memMod = rs.getInt(87);
         id = rs.getLong(88);
         groups = rs.getInt(89);
         atime1 = rs.getInt(90);
         atime2 = rs.getInt(91);
         atime3 = rs.getInt(92);
         atime4 = rs.getInt(93);
         atime5 = rs.getInt(94);
         afb = rs.getInt(95);
         p5 = rs.getString(96);
         players = rs.getInt(97);
         userg1 = rs.getString(98);
         userg2 = rs.getString(99);
         userg3 = rs.getString(100);
         userg4 = rs.getString(101);
         userg5 = rs.getString(102);
         userg6 = rs.getString(103);
         userg7 = rs.getString(104);
         userg8 = rs.getString(105);
         userg9 = rs.getString(106);
         userg10 = rs.getString(107);
         userg11 = rs.getString(108);
         userg12 = rs.getString(109);
         userg13 = rs.getString(110);
         userg14 = rs.getString(111);
         userg15 = rs.getString(112);
         userg16 = rs.getString(113);
         userg17 = rs.getString(114);
         userg18 = rs.getString(115);
         userg19 = rs.getString(116);
         userg20 = rs.getString(117);
         userg21 = rs.getString(118);
         userg22 = rs.getString(119);
         userg23 = rs.getString(120);
         userg24 = rs.getString(121);
         userg25 = rs.getString(122);
         orig_by = rs.getString(123);
         p91 = rs.getInt(124);
         p92 = rs.getInt(125);
         p93 = rs.getInt(126);
         p94 = rs.getInt(127);
         p95 = rs.getInt(128);
         p96 = rs.getInt(129);
         p97 = rs.getInt(130);
         p98 = rs.getInt(131);
         p99 = rs.getInt(132);
         p910 = rs.getInt(133);
         p911 = rs.getInt(134);
         p912 = rs.getInt(135);
         p913 = rs.getInt(136);
         p914 = rs.getInt(137);
         p915 = rs.getInt(138);
         p916 = rs.getInt(139);
         p917 = rs.getInt(140);
         p918 = rs.getInt(141);
         p919 = rs.getInt(142);
         p920 = rs.getInt(143);
         p921 = rs.getInt(144);
         p922 = rs.getInt(145);
         p923 = rs.getInt(146);
         p924 = rs.getInt(147);
         p925 = rs.getInt(148);
         afb2 = rs.getInt(149);
         afb3 = rs.getInt(150);
         afb4 = rs.getInt(151);
         afb5 = rs.getInt(152);
         type = rs.getString(153);
         course = rs.getString(154);
         date = rs.getInt(155);

         if (atime1 != 0) {          // only process if its assigned

            ok = SystemUtils.checkInUse(con, id);     // check if assigned tee times are currently in use

            if (ok == true) {             // if ok to proceed (no tee times are in use)

               //
               //  Save the usernames
               //
               userA[0] = user1;
               userA[1] = user2;
               userA[2] = user3;
               userA[3] = user4;
               userA[4] = user5;
               userA[5] = user6;
               userA[6] = user7;
               userA[7] = user8;
               userA[8] = user9;
               userA[9] = user10;
               userA[10] = user11;
               userA[11] = user12;
               userA[12] = user13;
               userA[13] = user14;
               userA[14] = user15;
               userA[15] = user16;
               userA[16] = user17;
               userA[17] = user18;
               userA[18] = user19;
               userA[19] = user20;
               userA[20] = user21;
               userA[21] = user22;
               userA[22] = user23;
               userA[23] = user24;
               userA[24] = user25;

               //
               //  create 1 tee time for each group requested (groups = )
               //
               time = atime1;    // time for this tee time
               hndcp1 = 99;      // init
               hndcp2 = 99;
               hndcp3 = 99;
               hndcp4 = 99;
               hndcp5 = 99;
               mNum1 = "";
               mNum2 = "";
               mNum3 = "";
               mNum4 = "";
               mNum5 = "";

               //
               //  Save area for tee time and email processing - by groups
               //
               String g1user1 = user1;
               String g1user2 = user2;
               String g1user3 = user3;
               String g1user4 = user4;
               String g1user5 = "";
               String g1player1 = player1;
               String g1player2 = player2;
               String g1player3 = player3;
               String g1player4 = player4;
               String g1player5 = "";
               String g1p1cw = p1cw;
               String g1p2cw = p2cw;
               String g1p3cw = p3cw;
               String g1p4cw = p4cw;
               String g1p5cw = "";
               String g1userg1 = userg1;
               String g1userg2 = userg2;
               String g1userg3 = userg3;
               String g1userg4 = userg4;
               String g1userg5 = "";
               int g1p91 = p91;
               int g1p92 = p92;
               int g1p93 = p93;
               int g1p94 = p94;
               int g1p95 = 0;

               String g2user1 = "";
               String g2user2 = "";
               String g2user3 = "";
               String g2user4 = "";
               String g2user5 = "";
               String g2player1 = "";
               String g2player2 = "";
               String g2player3 = "";
               String g2player4 = "";
               String g2player5 = "";
               String g2p1cw = "";
               String g2p2cw = "";
               String g2p3cw = "";
               String g2p4cw = "";
               String g2p5cw = "";
               String g2userg1 = "";
               String g2userg2 = "";
               String g2userg3 = "";
               String g2userg4 = "";
               String g2userg5 = "";
               int g2p91 = 0;
               int g2p92 = 0;
               int g2p93 = 0;
               int g2p94 = 0;
               int g2p95 = 0;

               String g3user1 = "";
               String g3user2 = "";
               String g3user3 = "";
               String g3user4 = "";
               String g3user5 = "";
               String g3player1 = "";
               String g3player2 = "";
               String g3player3 = "";
               String g3player4 = "";
               String g3player5 = "";
               String g3p1cw = "";
               String g3p2cw = "";
               String g3p3cw = "";
               String g3p4cw = "";
               String g3p5cw = "";
               String g3userg1 = "";
               String g3userg2 = "";
               String g3userg3 = "";
               String g3userg4 = "";
               String g3userg5 = "";
               int g3p91 = 0;
               int g3p92 = 0;
               int g3p93 = 0;
               int g3p94 = 0;
               int g3p95 = 0;

               String g4user1 = "";
               String g4user2 = "";
               String g4user3 = "";
               String g4user4 = "";
               String g4user5 = "";
               String g4player1 = "";
               String g4player2 = "";
               String g4player3 = "";
               String g4player4 = "";
               String g4player5 = "";
               String g4p1cw = "";
               String g4p2cw = "";
               String g4p3cw = "";
               String g4p4cw = "";
               String g4p5cw = "";
               String g4userg1 = "";
               String g4userg2 = "";
               String g4userg3 = "";
               String g4userg4 = "";
               String g4userg5 = "";
               int g4p91 = 0;
               int g4p92 = 0;
               int g4p93 = 0;
               int g4p94 = 0;
               int g4p95 = 0;

               String g5user1 = "";
               String g5user2 = "";
               String g5user3 = "";
               String g5user4 = "";
               String g5user5 = "";
               String g5player1 = "";
               String g5player2 = "";
               String g5player3 = "";
               String g5player4 = "";
               String g5player5 = "";
               String g5p1cw = "";
               String g5p2cw = "";
               String g5p3cw = "";
               String g5p4cw = "";
               String g5p5cw = "";
               String g5userg1 = "";
               String g5userg2 = "";
               String g5userg3 = "";
               String g5userg4 = "";
               String g5userg5 = "";
               int g5p91 = 0;
               int g5p92 = 0;
               int g5p93 = 0;
               int g5p94 = 0;
               int g5p95 = 0;
               
               errorMsg = "Error in SystemUtils moveReqs (get mem# and hndcp): ";

               //
               //  Get Member# and Handicap for each member
               //
               if (!user1.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user1);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum1 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user2.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user2);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum2 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user3.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user3);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum3 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user4.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user4);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum4 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (p5.equals( "Yes" )) {

                  if (!user5.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, user5);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum5 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  g1player5 = player5;
                  g1user5 = user5;
                  g1p5cw = p5cw;
                  g1userg5 = userg5;
                  g1p95 = p95;
               }

               if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                  mNum1 = "";                  // convert back to null
               }
               if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                  mNum2 = "";                  // convert back to null
               }
               if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                  mNum3 = "";                  // convert back to null
               }
               if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                  mNum4 = "";                  // convert back to null
               }
               if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                  mNum5 = "";                  // convert back to null
               }

               errorMsg = "Error in SystemUtils moveReqs (put group 1 in tee sheet): ";
               //
               //  Update the tee slot in teecurr
               //
               //  Clear the lottery name so this tee time is displayed in _sheet even though there
               //  may be some requests still outstanding (state = 4).
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                  "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                  "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                  "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                  "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                  "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                  "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                  "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt6.clearParameters();        // clear the parms
               pstmt6.setString(1, g1player1);
               pstmt6.setString(2, g1player2);
               pstmt6.setString(3, g1player3);
               pstmt6.setString(4, g1player4);
               pstmt6.setString(5, g1user1);
               pstmt6.setString(6, g1user2);
               pstmt6.setString(7, g1user3);
               pstmt6.setString(8, g1user4);
               pstmt6.setString(9, g1p1cw);
               pstmt6.setString(10, g1p2cw);
               pstmt6.setString(11, g1p3cw);
               pstmt6.setString(12, g1p4cw);
               pstmt6.setFloat(13, hndcp1);
               pstmt6.setFloat(14, hndcp2);
               pstmt6.setFloat(15, hndcp3);
               pstmt6.setFloat(16, hndcp4);
               pstmt6.setString(17, g1player5);
               pstmt6.setString(18, g1user5);
               pstmt6.setString(19, g1p5cw);
               pstmt6.setFloat(20, hndcp5);
               pstmt6.setString(21, notes);
               pstmt6.setInt(22, hide);
               pstmt6.setInt(23, proNew);
               pstmt6.setInt(24, proMod);
               pstmt6.setInt(25, memNew);
               pstmt6.setInt(26, memMod);
               pstmt6.setString(27, mNum1);
               pstmt6.setString(28, mNum2);
               pstmt6.setString(29, mNum3);
               pstmt6.setString(30, mNum4);
               pstmt6.setString(31, mNum5);
               pstmt6.setString(32, g1userg1);
               pstmt6.setString(33, g1userg2);
               pstmt6.setString(34, g1userg3);
               pstmt6.setString(35, g1userg4);
               pstmt6.setString(36, g1userg5);
               pstmt6.setString(37, orig_by);
               pstmt6.setInt(38, g1p91);
               pstmt6.setInt(39, g1p92);
               pstmt6.setInt(40, g1p93);
               pstmt6.setInt(41, g1p94);
               pstmt6.setInt(42, g1p95);

               pstmt6.setLong(43, date);
               pstmt6.setInt(44, time);
               pstmt6.setInt(45, afb);
               pstmt6.setString(46, course);

               count = pstmt6.executeUpdate();      // execute the prepared stmt

               pstmt6.close();

               //
               //  Do next group, if there is one
               //
               if (groups > 1 && count != 0) {

                  time = atime2;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g2player1 = player6;
                     g2player2 = player7;
                     g2player3 = player8;
                     g2player4 = player9;
                     g2player5 = player10;
                     g2user1 = user6;
                     g2user2 = user7;
                     g2user3 = user8;
                     g2user4 = user9;
                     g2user5 = user10;
                     g2p1cw = p6cw;
                     g2p2cw = p7cw;
                     g2p3cw = p8cw;
                     g2p4cw = p9cw;
                     g2p5cw = p10cw;
                     g2userg1 = userg6;
                     g2userg2 = userg7;
                     g2userg3 = userg8;
                     g2userg4 = userg9;
                     g2userg5 = userg10;
                     g2p91 = p96;
                     g2p92 = p97;
                     g2p93 = p98;
                     g2p94 = p99;
                     g2p95 = p910;

                  } else {

                     g2player1 = player5;
                     g2player2 = player6;
                     g2player3 = player7;
                     g2player4 = player8;
                     g2user1 = user5;
                     g2user2 = user6;
                     g2user3 = user7;
                     g2user4 = user8;
                     g2p1cw = p5cw;
                     g2p2cw = p6cw;
                     g2p3cw = p7cw;
                     g2p4cw = p8cw;
                     g2userg1 = userg5;
                     g2userg2 = userg6;
                     g2userg3 = userg7;
                     g2userg4 = userg8;
                     g2p91 = p95;
                     g2p92 = p96;
                     g2p93 = p97;
                     g2p94 = p98;
                  }

                  if (!g2user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user5.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user5);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum5 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  errorMsg = "Error in SystemUtils moveReqs (put group 2 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g2player1);
                  pstmt6.setString(2, g2player2);
                  pstmt6.setString(3, g2player3);
                  pstmt6.setString(4, g2player4);
                  pstmt6.setString(5, g2user1);
                  pstmt6.setString(6, g2user2);
                  pstmt6.setString(7, g2user3);
                  pstmt6.setString(8, g2user4);
                  pstmt6.setString(9, g2p1cw);
                  pstmt6.setString(10, g2p2cw);
                  pstmt6.setString(11, g2p3cw);
                  pstmt6.setString(12, g2p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g2player5);
                  pstmt6.setString(18, g2user5);
                  pstmt6.setString(19, g2p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g2userg1);
                  pstmt6.setString(33, g2userg2);
                  pstmt6.setString(34, g2userg3);
                  pstmt6.setString(35, g2userg4);
                  pstmt6.setString(36, g2userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g2p91);
                  pstmt6.setInt(39, g2p92);
                  pstmt6.setInt(40, g2p93);
                  pstmt6.setInt(41, g2p94);
                  pstmt6.setInt(42, g2p95);

                  pstmt6.setLong(43, date);
                  pstmt6.setInt(44, time);
                  pstmt6.setInt(45, afb2);
                  pstmt6.setString(46, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 2 && count != 0) {

                  time = atime3;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g3player1 = player11;
                     g3player2 = player12;
                     g3player3 = player13;
                     g3player4 = player14;
                     g3player5 = player15;
                     g3user1 = user11;
                     g3user2 = user12;
                     g3user3 = user13;
                     g3user4 = user14;
                     g3user5 = user15;
                     g3p1cw = p11cw;
                     g3p2cw = p12cw;
                     g3p3cw = p13cw;
                     g3p4cw = p14cw;
                     g3p5cw = p15cw;
                     g3userg1 = userg11;
                     g3userg2 = userg12;
                     g3userg3 = userg13;
                     g3userg4 = userg14;
                     g3userg5 = userg15;
                     g3p91 = p911;
                     g3p92 = p912;
                     g3p93 = p913;
                     g3p94 = p914;
                     g3p95 = p915;

                  } else {

                     g3player1 = player9;
                     g3player2 = player10;
                     g3player3 = player11;
                     g3player4 = player12;
                     g3user1 = user9;
                     g3user2 = user10;
                     g3user3 = user11;
                     g3user4 = user12;
                     g3p1cw = p9cw;
                     g3p2cw = p10cw;
                     g3p3cw = p11cw;
                     g3p4cw = p12cw;
                     g3userg1 = userg9;
                     g3userg2 = userg10;
                     g3userg3 = userg11;
                     g3userg4 = userg12;
                     g3p91 = p99;
                     g3p92 = p910;
                     g3p93 = p911;
                     g3p94 = p912;
                  }

                  if (!g3user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g3user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g3user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  errorMsg = "Error in SystemUtils moveReqs (put group 3 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g3player1);
                  pstmt6.setString(2, g3player2);
                  pstmt6.setString(3, g3player3);
                  pstmt6.setString(4, g3player4);
                  pstmt6.setString(5, g3user1);
                  pstmt6.setString(6, g3user2);
                  pstmt6.setString(7, g3user3);
                  pstmt6.setString(8, g3user4);
                  pstmt6.setString(9, g3p1cw);
                  pstmt6.setString(10, g3p2cw);
                  pstmt6.setString(11, g3p3cw);
                  pstmt6.setString(12, g3p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g3player5);
                  pstmt6.setString(18, g3user5);
                  pstmt6.setString(19, g3p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g3userg1);
                  pstmt6.setString(33, g3userg2);
                  pstmt6.setString(34, g3userg3);
                  pstmt6.setString(35, g3userg4);
                  pstmt6.setString(36, g3userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g3p91);
                  pstmt6.setInt(39, g3p92);
                  pstmt6.setInt(40, g3p93);
                  pstmt6.setInt(41, g3p94);
                  pstmt6.setInt(42, g3p95);

                  pstmt6.setLong(43, date);
                  pstmt6.setInt(44, time);
                  pstmt6.setInt(45, afb3);
                  pstmt6.setString(46, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 3 && count != 0) {

                  time = atime4;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g4player1 = player16;
                     g4player2 = player17;
                     g4player3 = player18;
                     g4player4 = player19;
                     g4player5 = player20;
                     g4user1 = user16;
                     g4user2 = user17;
                     g4user3 = user18;
                     g4user4 = user19;
                     g4user5 = user20;
                     g4p1cw = p16cw;
                     g4p2cw = p17cw;
                     g4p3cw = p18cw;
                     g4p4cw = p19cw;
                     g4p5cw = p20cw;
                     g4userg1 = userg16;
                     g4userg2 = userg17;
                     g4userg3 = userg18;
                     g4userg4 = userg19;
                     g4userg5 = userg20;
                     g4p91 = p916;
                     g4p92 = p917;
                     g4p93 = p918;
                     g4p94 = p919;
                     g4p95 = p920;

                  } else {

                     g4player1 = player13;
                     g4player2 = player14;
                     g4player3 = player15;
                     g4player4 = player16;
                     g4user1 = user13;
                     g4user2 = user14;
                     g4user3 = user15;
                     g4user4 = user16;
                     g4p1cw = p13cw;
                     g4p2cw = p14cw;
                     g4p3cw = p15cw;
                     g4p4cw = p16cw;
                     g4userg1 = userg13;
                     g4userg2 = userg14;
                     g4userg3 = userg15;
                     g4userg4 = userg16;
                     g4p91 = p913;
                     g4p92 = p914;
                     g4p93 = p915;
                     g4p94 = p916;
                  }

                  if (!g4user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g4user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g4user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  errorMsg = "Error in SystemUtils moveReqs (put group 4 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g4player1);
                  pstmt6.setString(2, g4player2);
                  pstmt6.setString(3, g4player3);
                  pstmt6.setString(4, g4player4);
                  pstmt6.setString(5, g4user1);
                  pstmt6.setString(6, g4user2);
                  pstmt6.setString(7, g4user3);
                  pstmt6.setString(8, g4user4);
                  pstmt6.setString(9, g4p1cw);
                  pstmt6.setString(10, g4p2cw);
                  pstmt6.setString(11, g4p3cw);
                  pstmt6.setString(12, g4p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g4player5);
                  pstmt6.setString(18, g4user5);
                  pstmt6.setString(19, g4p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g4userg1);
                  pstmt6.setString(33, g4userg2);
                  pstmt6.setString(34, g4userg3);
                  pstmt6.setString(35, g4userg4);
                  pstmt6.setString(36, g4userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g4p91);
                  pstmt6.setInt(39, g4p92);
                  pstmt6.setInt(40, g4p93);
                  pstmt6.setInt(41, g4p94);
                  pstmt6.setInt(42, g4p95);

                  pstmt6.setLong(43, date);
                  pstmt6.setInt(44, time);
                  pstmt6.setInt(45, afb4);
                  pstmt6.setString(46, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 4 && count != 0) {

                  time = atime5;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g5player1 = player21;
                     g5player2 = player22;
                     g5player3 = player23;
                     g5player4 = player24;
                     g5player5 = player25;
                     g5user1 = user21;
                     g5user2 = user22;
                     g5user3 = user23;
                     g5user4 = user24;
                     g5user5 = user25;
                     g5p1cw = p21cw;
                     g5p2cw = p22cw;
                     g5p3cw = p23cw;
                     g5p4cw = p24cw;
                     g5p5cw = p25cw;
                     g5userg1 = userg21;
                     g5userg2 = userg22;
                     g5userg3 = userg23;
                     g5userg4 = userg24;
                     g5userg5 = userg25;
                     g5p91 = p921;
                     g5p92 = p922;
                     g5p93 = p923;
                     g5p94 = p924;
                     g5p95 = p925;

                  } else {

                     g5player1 = player17;
                     g5player2 = player18;
                     g5player3 = player19;
                     g5player4 = player20;
                     g5user1 = user17;
                     g5user2 = user18;
                     g5user3 = user19;
                     g5user4 = user20;
                     g5p1cw = p17cw;
                     g5p2cw = p18cw;
                     g5p3cw = p19cw;
                     g5p4cw = p20cw;
                     g5userg1 = userg17;
                     g5userg2 = userg18;
                     g5userg3 = userg19;
                     g5userg4 = userg20;
                     g5p91 = p917;
                     g5p92 = p918;
                     g5p93 = p919;
                     g5p94 = p920;
                  }

                  if (!g5user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g5user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g5user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  errorMsg = "Error in SystemUtils moveReqs (put group 5 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g5player1);
                  pstmt6.setString(2, g5player2);
                  pstmt6.setString(3, g5player3);
                  pstmt6.setString(4, g5player4);
                  pstmt6.setString(5, g5user1);
                  pstmt6.setString(6, g5user2);
                  pstmt6.setString(7, g5user3);
                  pstmt6.setString(8, g5user4);
                  pstmt6.setString(9, g5p1cw);
                  pstmt6.setString(10, g5p2cw);
                  pstmt6.setString(11, g5p3cw);
                  pstmt6.setString(12, g5p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g5player5);
                  pstmt6.setString(18, g5user5);
                  pstmt6.setString(19, g5p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g5userg1);
                  pstmt6.setString(33, g5userg2);
                  pstmt6.setString(34, g5userg3);
                  pstmt6.setString(35, g5userg4);
                  pstmt6.setString(36, g5userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g5p91);
                  pstmt6.setInt(39, g5p92);
                  pstmt6.setInt(40, g5p93);
                  pstmt6.setInt(41, g5p94);
                  pstmt6.setInt(42, g5p95);

                  pstmt6.setLong(43, date);
                  pstmt6.setInt(44, time);
                  pstmt6.setInt(45, afb5);
                  pstmt6.setString(46, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

/*
               //*****************************************************************************
               //  Send an email to all in this request
               //*****************************************************************************
               //
               errorMsg = "Error in SystemUtils moveReqs (send email): ";

               String clubName = "";

               try {

                  estmt = con.createStatement();        // create a statement

                  rs2 = estmt.executeQuery("SELECT clubName " +
                                          "FROM club5 WHERE clubName != ''");

                  if (rs2.next()) {

                     clubName = rs2.getString(1);
                  }
                  estmt.close();
               }
               catch (Exception ignore) {
               }

               //
               //  Get today's date and time for email processing
               //
               Calendar ecal = new GregorianCalendar();               // get todays date
               int eyear = ecal.get(Calendar.YEAR);
               int emonth = ecal.get(Calendar.MONTH);
               int eday = ecal.get(Calendar.DAY_OF_MONTH);
               int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
               int e_min = ecal.get(Calendar.MINUTE);

               int e_time = 0;
               long e_date = 0;

               //
               //  Build the 'time' string for display
               //
               //    Adjust the time based on the club's time zone (we are Central)
               //
               e_time = (e_hourDay * 100) + e_min;

               e_time = SystemUtils.adjustTime(con, e_time);       // adjust for time zone

               if (e_time < 0) {          // if negative, then we went back or ahead one day

                  e_time = 0 - e_time;        // convert back to positive value

                  if (e_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                     //
                     // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                     //
                     ecal.add(Calendar.DATE,1);                     // get next day's date

                     eyear = ecal.get(Calendar.YEAR);
                     emonth = ecal.get(Calendar.MONTH);
                     eday = ecal.get(Calendar.DAY_OF_MONTH);

                  } else {                        // we rolled back 1 day

                     //
                     // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                     //
                     ecal.add(Calendar.DATE,-1);                     // get yesterday's date

                     eyear = ecal.get(Calendar.YEAR);
                     emonth = ecal.get(Calendar.MONTH);
                     eday = ecal.get(Calendar.DAY_OF_MONTH);
                  }
               }

               int e_hour = e_time / 100;                // get adjusted hour
               e_min = e_time - (e_hour * 100);          // get minute value
               int e_am_pm = 0;                         // preset to AM

               if (e_hour > 11) {

                  e_am_pm = 1;                // PM
                  e_hour = e_hour - 12;       // set to 12 hr clock
               }
               if (e_hour == 0) {

                  e_hour = 12;
               }

               String email_time = "";

               emonth = emonth + 1;                            // month starts at zero
               e_date = (eyear * 10000) + (emonth * 100) + eday;

               //
               //  get date/time string for email message
               //
               if (e_am_pm == 0) {
                  if (e_min < 10) {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":0" + e_min + " AM";
                  } else {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + e_min + " AM";
                  }
               } else {
                  if (e_min < 10) {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":0" + e_min + " PM";
                  } else {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + e_min + " PM";
                  }
               }

               //
               //***********************************************
               //  Send email notification if necessary
               //***********************************************
               //
               String to = "";                          // to address
               String f_b = "";
               String eampm = "";
               String etime = "";
               String enewMsg = "";
               int emailOpt = 0;                        // user's email option parm
               int ehr = 0;
               int emin = 0;
               int send = 0;

               PreparedStatement pstmte1 = null;

               //
               //  set the front/back value
               //
               f_b = "Front";

               if (afb == 1) {

                  f_b = "Back";
               }

               String enew1 = "";
               String enew2 = "";
               String subject = "";

               if (clubName.startsWith( "Old Oaks" )) {
                 
                  enew1 = "The following Tee Time has been ASSIGNED.\n\n";
                  enew2 = "The following Tee Times have been ASSIGNED.\n\n";
                  subject = "ForeTees Tee Time Assignment Notification";

               } else {

                  if (clubName.startsWith( "Westchester" )) {

                     enew1 = "The following Draw Tee Time has been ASSIGNED.\n\n";
                     enew2 = "The following Draw Tee Times have been ASSIGNED.\n\n";
                     subject = "Your Tee Time for Weekend Draw";

                  } else {

                     enew1 = "The following Lottery Tee Time has been ASSIGNED.\n\n";
                     enew2 = "The following Lottery Tee Times have been ASSIGNED.\n\n";
                     subject = "ForeTees Lottery Assignment Notification";
                  }
               }

               if (!clubName.equals( "" )) {

                  subject = subject + " - " + clubName;
               }

               Properties properties = new Properties();
               properties.put("mail.smtp.host", SystemUtils.host);                      // set outbound host address
               properties.put("mail.smtp.port", SystemUtils.port);                      // set outbound port
               properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

               Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

               MimeMessage message = new MimeMessage(mailSess);

               try {

                  message.setFrom(new InternetAddress(SystemUtils.EFROM));                               // set from addr

                  message.setSubject( subject );                                            // set subject line
                  message.setSentDate(new java.util.Date());                                // set date/time sent
               }
               catch (Exception ignore) {
               }

               //
               //  Set the recipient addresses
               //
               if (!g1user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g4user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g4user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               //
               //  send email if anyone to send it to
               //
               if (send != 0) {        // if any email addresses specified for members
                  //
                  //  Create the message content
                  //
                  if (groups > 1) {
                     if (afb == afb2 && afb == afb3 && afb == afb4 && afb == afb5) {    // if all on the same tee 
                        enewMsg = SystemUtils.header + enew2 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on the " + f_b + " tee ";
                     } else {
                        enewMsg = SystemUtils.header + enew2 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on both tees ";
                     }
                  } else {
                     if (afb == afb2 && afb == afb3 && afb == afb4 && afb == afb5) {    // if all on the same tee
                        enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on the " + f_b + " tee ";
                     } else {
                        enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on both tees ";
                     }
                  }
                  if (!course.equals( "" )) {

                     enewMsg = enewMsg + "of Course: " + course;
                  }

                  //
                  //  convert time to hour and minutes for email msg
                  //
                  time = atime1;              // time for this tee time
                  ehr = time / 100;
                  emin = time - (ehr * 100);
                  eampm = " AM";
                  if (ehr > 12) {

                     eampm = " PM";
                     ehr = ehr - 12;       // convert from military time
                  }
                  if (ehr == 12) {

                     eampm = " PM";
                  }
                  if (ehr == 0) {

                     ehr = 12;
                     eampm = " AM";
                  }

                  if (emin < 10) {

                     etime = ehr + ":0" + emin + eampm;

                  } else {

                     etime = ehr + ":" + emin + eampm;
                  }

                  enewMsg = enewMsg + "\n at " + etime + "\n";

                  if (!g1player1.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 1: " + g1player1 + "  " + g1p1cw;
                  }
                  if (!g1player2.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 2: " + g1player2 + "  " + g1p2cw;
                  }
                  if (!g1player3.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 3: " + g1player3 + "  " + g1p3cw;
                  }
                  if (!g1player4.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 4: " + g1player4 + "  " + g1p4cw;
                  }
                  if (!g1player5.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 5: " + g1player5 + "  " + g1p5cw;
                  }

                  if (groups > 1) {

                     time = atime2;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g2player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g2player1 + "  " + g2p1cw;
                     }
                     if (!g2player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g2player2 + "  " + g2p2cw;
                     }
                     if (!g2player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g2player3 + "  " + g2p3cw;
                     }
                     if (!g2player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g2player4 + "  " + g2p4cw;
                     }
                     if (!g2player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g2player5 + "  " + g2p5cw;
                     }
                  }

                  if (groups > 2) {

                     time = atime3;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g3player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g3player1 + "  " + g3p1cw;
                     }
                     if (!g3player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g3player2 + "  " + g3p2cw;
                     }
                     if (!g3player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g3player3 + "  " + g3p3cw;
                     }
                     if (!g3player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g3player4 + "  " + g3p4cw;
                     }
                     if (!g3player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g3player5 + "  " + g3p5cw;
                     }
                  }

                  if (groups > 3) {

                     time = atime4;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g4player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g4player1 + "  " + g4p1cw;
                     }
                     if (!g4player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g4player2 + "  " + g4p2cw;
                     }
                     if (!g4player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g4player3 + "  " + g4p3cw;
                     }
                     if (!g4player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g4player4 + "  " + g4p4cw;
                     }
                     if (!g4player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g4player5 + "  " + g4p5cw;
                     }
                  }

                  if (groups > 4) {

                     time = atime5;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g5player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g5player1 + "  " + g5p1cw;
                     }
                     if (!g5player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g5player2 + "  " + g5p2cw;
                     }
                     if (!g5player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g5player3 + "  " + g5p3cw;
                     }
                     if (!g5player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g5player4 + "  " + g5p4cw;
                     }
                     if (!g5player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g5player5 + "  " + g5p5cw;
                     }
                  }

                  enewMsg = enewMsg + SystemUtils.trailer;

                  try {
                     message.setText( enewMsg );  // put msg in email text area

                     Transport.send(message);     // send it!!
                  }
                  catch (Exception ignore) {
                  }
               }     // end of IF send
*/
               
               if (count == 0) {
                   
                   // we were not able to update the tee time(s) to contain all requested times in the lottery
                  out.println(SystemUtils.HeadTitle("Error Converting Lottery Request"));
                  out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                  out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<center><BR><BR><H3>Database Access Error</H3>");
                  out.println("<BR><BR>Error Converting Lottery Request");
                  out.println("<BR>The assigned tee time was not found for this lottery request.  You may have to insert the assigned tee time and try again.");
                  out.println("<BR><BR>If problem persists, contact customer support.");
                  out.println("<BR><BR>");
                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + name + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form>");
                  out.println("</center></BODY></HTML>");
                  out.close();
                  return;
                   
               } else {
               
                   //
                   // delete the request after players have been moved
                   //
                   pstmtd = con.prepareStatement (
                            "DELETE FROM lreqs3 WHERE id = ?");

                   pstmtd.clearParameters();
                   pstmtd.setLong(1, id);
                   pstmtd.executeUpdate();
                   pstmtd.close();
                           
               }
               
               
               //
               //  If lottery type = Weighted By Proximity, determine time between request and assigned
               //
               if (type.equals( "WeightedBP" )) {
                 
                  proxMins = SystemUtils.calcProxTime(rtime, atime1);      // calculate mins difference
                    
                  pstmtd2 = con.prepareStatement (
                        "INSERT INTO lassigns5 (username, lname, date, mins) " +
                        "VALUES (?, ?, ?, ?)");

                  //
                  //  Save each members' weight for this request
                  //
                  for (i=0; i<25; i++) {          // check all 25 possible players

                     if (!userA[i].equals( "" )) {     // if player is a member

                        pstmtd2.clearParameters();
                        pstmtd2.setString(1, userA[i]);
                        pstmtd2.setString(2, name);
                        pstmtd2.setLong(3, date);
                        pstmtd2.setInt(4, proxMins);

                        pstmtd2.executeUpdate();
                     }
                  }
                  pstmtd2.close();

               }                // end of IF Weighted by Proximity lottery type

            } // end of IF ok (tee times in use?)
            else {

               // we were not able to update the tee time(s) to contain all requested times in the lottery
              out.println(SystemUtils.HeadTitle("Error Converting Lottery Request"));
              out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
              out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
              out.println("<center><BR><BR><H3>Tee Time Busy or Occupied</H3>");
              out.println("<BR><BR>Error Converting Lottery Request");
              out.println("<BR>The assigned tee time for this lottery request is either busy or is already occupied with players.");
              out.println("<BR>If the lottery request has multiple groups within, it could be one of the groups assigned times that are busy or occupied.");
              out.println("<BR><BR>");
              out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
              out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
              out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
              out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + name + "\">");
              out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
              out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
              out.println("</form>");
              out.println("</center></BODY></HTML>");
              out.close();
              return;

            } // end if tee time busy or full

         } else {     // req is NOT assigned

            //
            //  Change the state to 5 (processed & approved) so _sheet will show the others
            //
            PreparedStatement pstmt7s = con.prepareStatement (
                "UPDATE lreqs3 SET state = 5 " +
                "WHERE id = ?");

            pstmt7s.clearParameters();        // clear the parms
            pstmt7s.setLong(1, id);

            pstmt7s.executeUpdate();

            pstmt7s.close();

         }     // end of IF req is assigned

      }    // end of WHILE lreqs - process next request

      pstmt.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e1.getMessage();
      SystemUtils.buildDatabaseErrMsg(e1.toString(), errorMsg, out, false); 
      return;
   }
   

    //
    // Completed update - reload page
    //
    out.println("<meta http-equiv=\"Refresh\" content=\"2; url=/" +rev+ "/servlet/Proshop_dlott?index=" + index + "&course=" + returnCourse + "&lott_name=" + name + "&hide="+hideUnavail+"\">");

    out.close();

 } // end auto_convert method
 
 
 //
 // Add player to slot
 //
 private static boolean addPlayer(parmSlot slotParms, String player_name, String username, String cw, int p9hole, boolean allowFives) {

    if (slotParms.player1.equals("")) {

        slotParms.player1 = player_name;
        slotParms.user1 = username;
        slotParms.p1cw = cw;
        slotParms.p91 = p9hole;

    } else if (slotParms.player2.equals("")) {

        slotParms.player2 = player_name;
        slotParms.user2 = username;
        slotParms.p2cw = cw;
        slotParms.p92 = p9hole;

    } else if (slotParms.player3.equals("")) {

        slotParms.player3 = player_name;
        slotParms.user3 = username;
        slotParms.p3cw = cw;
        slotParms.p93 = p9hole;

    } else if (slotParms.player4.equals("")) {

        slotParms.player4 = player_name;
        slotParms.user4 = username;
        slotParms.p4cw = cw;
        slotParms.p94 = p9hole;

    } else if (slotParms.player5.equals("") && allowFives) {

        slotParms.player5 = player_name;
        slotParms.user5 = username;
        slotParms.p5cw = cw;
        slotParms.p95 = p9hole;

    } else {
        
        return (true);
        
    }
    
     return (false);
 }
 
 
 // *********************************************************
 //  Process delete request from above
 //
 //  parms:  index         = index value for date
 //          course        = name of course
 //          returnCourse  = name of course for return to sheet
 //          jump          = jump index for return
 //          time          = time of tee time
 //          fb            = f/b indicator
 //
 // *********************************************************

 private void doDelete(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {


   ResultSet rs = null;

   //
   //  variables for this class
   //
   int index = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int fb = 0;
   int hr = 0;
   int min = 0;
   int time = 0;

   String sampm = "AM";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


   //
   //    The 'index' paramter contains an index value
   //    (0 = today, 1 = tomorrow, etc.)
   //
   String indexs = req.getParameter("index");        //  index value of the day
   String course = req.getParameter("course");       //  get the course name for this delete request
   String returnCourse = req.getParameter("returnCourse");   //  get the course name for this sheet
   String jump = req.getParameter("jump");           //  get the jump index
   String stime = req.getParameter("time");          //  get the time of tee time
   String sfb = req.getParameter("fb");              //  get the fb indicator
   String emailOpt = req.getParameter("email");      //  get the email indicator
   String hide = req.getParameter("hide");
   String lott_name = req.getParameter("lott_name");

   if (course == null) {

      course = "";    // change to null string
   }

   //
   //  Convert the index value from string to int
   //
   try {
      index = Integer.parseInt(indexs);
      fb = Integer.parseInt(sfb);
      time = Integer.parseInt(stime);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  isolate hr and min values
   //
   hr = time / 100;
   min = time - (hr * 100);

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

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   if (req.getParameter("deleteSubmit") == null) {      // if this is the first request

      //
      //   Call is from 'edit' processing above to start a delete request
      //
      //
      //  Build the HTML page to prompt user for a confirmation
      //
      out.println(SystemUtils.HeadTitle("Proshop Delete Confirmation"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
      out.println("<tr><td align=\"center\">");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\"><br><br>");

         out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
         out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"delete\" value=\"yes\">");

         out.println("<tr><td width=\"450\">");
         out.println("<font size=\"3\">");
         out.println("<p align=\"center\"><b>Delete Confirmation</b></p></font>");
         out.println("<br><font size=\"2\">");

            out.println("<font size=\"2\">");
            out.println("<p align=\"left\">");
            //
            //  Check to see if any players are in this tee time
            //
            try {

               PreparedStatement pstmt1d = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, player5 " +
                  "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1d.clearParameters();        // clear the parms
               pstmt1d.setLong(1, date);
               pstmt1d.setInt(2, time);
               pstmt1d.setInt(3, fb);
               pstmt1d.setString(4, course);

               rs = pstmt1d.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  player1 = rs.getString(1);
                  player2 = rs.getString(2);
                  player3 = rs.getString(3);
                  player4 = rs.getString(4);
                  player5 = rs.getString(5);

               } 

               pstmt1d.close();   // close the stmt

            }
            catch (Exception ignore) {   // this is good if no match found
            }
              
            if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) || !player5.equals( "" )) { 
              
               out.println("<b>Warning:</b> &nbsp;You are about to permanently remove a tee time ");
               out.println("which contains the following player(s):<br>");
                 
               if (!player1.equals( "" )) {
         
                  out.println("<br>" +player1);
               }
               if (!player2.equals( "" )) {

                  out.println("<br>" +player2);
               }
               if (!player3.equals( "" )) {

                  out.println("<br>" +player3);
               }
               if (!player4.equals( "" )) {

                  out.println("<br>" +player4);
               }
               if (!player5.equals( "" )) {

                  out.println("<br>" +player5);
               }
               out.println("<br><br>This will remove the entire tee time slot from the database. ");
               out.println(" If you wish to only remove the players, then return to the tee sheet and select the tee time to update it.");
               out.println("</p>");
            } else {
               out.println("<b>Warning:</b> &nbsp;You are about to permanently remove the following tee time.<br><br>");
               out.println("This will remove the entire tee time slot from the database. ");
               out.println(" If you wish to only remove the players, then return to the tee sheet and select the tee time to update it.");
               out.println("</p>");
            }
            //
            //  build the time string
            //
            sampm = "AM";
            if (hr > 11) {        // if PM

               sampm = "PM";
            }
            if (hr > 12) {
               hr = hr - 12;        // convert back to conventional time
            }
            out.println("<p align=\"center\">");
            if (min < 10) {
               out.println("Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":0" + min + " " + sampm + "</b>");
            } else {
               out.println("Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + min + " " + sampm + "</b>");
            }
              
         out.println("<BR><BR>Are you sure you want to delete this tee time?</p>");

            out.println("<p align=\"center\">");
              out.println("<input type=\"submit\" value=\"Yes - Delete It\" name=\"deleteSubmit\"></p>");
            out.println("</font></td></tr></form></table>");
            out.println("<br><br>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
         out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
      out.println("<input type=\"submit\" value=\"No - Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"submit\" value=\"No - Return to Tee Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");

      //
      //  End of HTML page
      //
      out.println("</td></tr></table>");                           // end of main page
      out.println("</td></tr></table>");                           // end of whole page
      out.println("</center></body></html>");
      out.close();
      
   } else { 

      //
      //   Call is from self to process a delete request (final - this is the confirmation)
      //
      //  Check to make sure a slot like this already exists
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT mm FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, date);
         pstmt1.setInt(2, time);
         pstmt1.setInt(3, fb);
         pstmt1.setString(4, course);

         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (!rs.next()) {

            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center><BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>A tee time with these date, time and F/B values does not exist.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</center></BODY></HTML>");
            out.close();
            return;

         }    // ok if we get here - matching time slot found

         pstmt1.close();   // close the stmt

      }
      catch (Exception ignore) {   // this is good if no match found
      }

      //
      //  This slot was found - delete it from the database
      //

      try {

         PreparedStatement pstmt2 = con.prepareStatement (
            "DELETE FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, date);
         pstmt2.setInt(2, time);
         pstmt2.setInt(3, fb);
         pstmt2.setString(4, course);

         int count = pstmt2.executeUpdate();      // execute the prepared stmt

         pstmt2.close();   // close the stmt

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center><BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + e1.getMessage());
         out.println("<BR><BR>");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</center></BODY></HTML>");
         out.close();
         return;
      }
      //
      //  Delete complete - inform user
      //
      sampm = "AM";
      if (hr > 11) {        // if PM

         sampm = "PM";
      }
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }

      out.println("<HTML><HEAD><title>Proshop Delete Confirmation</title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dlott?index=" +indexs+ "&course=" +returnCourse+ "&jump=" +jump+ "&email=" +emailOpt+ "&jump=" +jump+ "&hide=" +hide+ "&lott_name=" +lott_name+ "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("><BR><BR><H3>Delete Tee Time Confirmation</H3>");
      out.println("<BR><BR>Thank you, the following tee time has been removed.");
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }
      if (min < 10) {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":0" + min + " " + sampm + "</b>");
      } else {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + min + " " + sampm + "</b>");
      }
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
         out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
      out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</center></BODY></HTML>");
      out.close();
   }
 }      // end of doDelete

 
 // *********************************************************
 //  Process a delete request for a lottery
 //  
 //  Parms: lotteryId    = uid for request to be deleted
 //
 // *********************************************************
 
 private void doDeleteLottery(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {

     
    String sindex = req.getParameter("index");        //  index value of the day
    String returnCourse = req.getParameter("returnCourse");   //  get the course name for this sheet
    String jump = req.getParameter("jump");           //  get the jump index
    String emailOpt = req.getParameter("email");      //  get the email indicator
    String lott_name = req.getParameter("lott_name");
    String hide = req.getParameter("hide");
    String slid = req.getParameter("lotteryId");
    
    int index = 0;
    int lottery_id = 0;
    if (slid == null) slid = "";

    //
    //  Convert the index value from string to int
    //
    try {

        index = Integer.parseInt(sindex);
        lottery_id = Integer.parseInt(slid);
    }
    catch (NumberFormatException e) { }
    
    try {

        PreparedStatement pstmt2 = con.prepareStatement (
        "DELETE FROM lreqs3 WHERE id = ?");

        pstmt2.clearParameters();
        pstmt2.setInt(1, lottery_id);
        pstmt2.executeUpdate();
        pstmt2.close();

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center><BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</center></BODY></HTML>");
        out.close();
        return;
    }

    out.println("<HTML><HEAD><title>Proshop Delete Confirmation</title>");
    out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&course=" +returnCourse+ "&jump=" +jump+ "&email=" +emailOpt+ "&jump=" +jump+ "&hide=" +hide+ "&lott_name=" +lott_name+ "\">");
    out.println("</HEAD>");
    out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<center>");
    out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
    out.println("<BR><BR><H3>Delete Lottery Request Confirmation</H3>");
    out.println("<BR><BR>Thank you, the request has been removed.");
    out.println("<BR><BR>");
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</center></BODY></HTML>");
    out.close();
         
 }
 
 

 // *********************************************************
 //  Process insert request from above
 //
 //  parms:  index         = index value for date
 //          course        = name of course
 //          returnCourse  = name of course for return to sheet
 //          jump          = jump index for return
 //          time          = time of tee time
 //          fb            = f/b indicator
 //          insertSubmit  = if from self
 //          first         = if first tee time
 //
 // *********************************************************

 private void doInsert(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {


   ResultSet rs = null;

   //
   //  variables for this method
   //
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int hr = 0;
   int min = 0;
   int ampm = 0;
   int fb = 0;
   int time = 0;
   int otime = 0;
   int index = 0;
   int event_type = 0;
   int notify_id = 0;

   String event = "";
   String event_color = "";
   String rest = "";
   String rest2 = "";
   String rest_color = "";
   String rest_color2 = "";
   String rest_recurr = "";
   String rest5 = "";                      // default values
   String rest52 = "";
   String rest5_color = "";
   String rest5_color2 = "";
   String rest5_recurr = "";
   String lott = "";                      // lottery name
   String lott2 = "";                      // lottery name
   String lott_color = "";
   String lott_color2 = "";
   String lott_recurr = "";

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


   //
   //    The 'index' paramter contains an index value
   //    (0 = today, 1 = tomorrow, etc.)
   //
   String indexs = req.getParameter("index");         //  index value of the day
   String course = req.getParameter("course");        //  get the course name for this insert
   String returnCourse = req.getParameter("returnCourse");     //  get the course name for this sheet
   String jump = req.getParameter("jump");            //  get the jump index
   String sfb = req.getParameter("fb");
   String times = req.getParameter("time");            //  get the tee time selected (hhmm)
   String first = req.getParameter("first");           //  get the first tee time indicator (yes or no)
   String emailOpt = req.getParameter("email");        //  get the email option from _sheet
   String snid = req.getParameter("notifyId");
   String hide = req.getParameter("hide");
   String lott_name = req.getParameter("lott_name");

   if (course == null) course = "";
   if (snid == null) snid = "";
   if (times == null) times = "";
   if (sfb == null) sfb = "";
   
   //
   //  Convert the index value from string to int
   //
   try {
       
      time = Integer.parseInt(times);
      index = Integer.parseInt(indexs);
      fb = Integer.parseInt(sfb);
      notify_id = Integer.parseInt(snid);
   }
   catch (NumberFormatException e) { }

   //
   //  isolate hr and min values
   //
   hr = time / 100;
   min = time - (hr * 100);

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   cal.add(Calendar.DATE,index);                  // roll ahead (or back) 'index' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH) + 1;
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   String day_name = day_table[day_num];         // get name for day
   
   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100) + day;            // date = yyyymmdd (for comparisons)

   if (req.getParameter("insertSubmit") == null) {      // if not an insert 'submit' request (from self)

      //
      //  Process the initial call to Insert a New Tee Time
      //
      //  Build the HTML page to prompt user for a specific time slot
      //
      out.println(SystemUtils.HeadTitle("Proshop Insert Tee Time Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
      out.println("<tr><td align=\"center\">");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\">");

      out.println("<font size=\"5\">");
      out.println("<p align=\"center\"><b>Insert Tee Sheet</b></p></font>");
      out.println("<font size=\"2\">");

      out.println("<table cellpadding=\"5\" align=\"center\" width=\"450\">");
      out.println("<tr><td colspan=\"4\" bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Instructions:</b>  To insert a tee time for the date shown below, select the time");
      out.println(" and the 'front/back' values.  Select 'Insert' to add the new tee time.");
      out.println("</font></td></tr></table><br>");

      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + ">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
      out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"hidden\" name=\"insert\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");

      out.println("<tr><td width=\"450\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">");
         out.println("<b>Note:</b> &nbsp;This tee time must be unique from all others on the sheet. &nbsp;");
         out.println("Therefore, at least one of these values must be different than other tee times.");
      out.println("<p align=\"center\">Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b></p>");
      out.println("Time:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"time\">");
      //
      //  Define some variables for this processing
      //
      PreparedStatement pstmt1b = null;
      String dampm = " AM";
      int dhr = hr;
      int i = 0;
      int i2 = 0;
      int mint = min;
      int hrt = hr;
      int last = 0;
      int start = 0;
      int maxtimes = 20;
        
      //
      //  Determine time values to be used for selection 
      //
      loopt:
      while (i < maxtimes) {

         mint++;               // next minute
         if (mint > 59) {

            mint = 0;          // rotate the hour
            hrt++;
         }
         if (hrt > 23) {

            hrt = 23;
            mint = 59;
            break loopt;      // done
         }
         if (i == 0) {                        // if first time
            start = (hrt * 100) + mint;       // save first time for select
         }
         i++;
      }
      last = (hrt * 100) + mint;       // last time for select

      try {

         //
         //   Find the next time - after the time selected - use as the limit for selection list
         //
         pstmt1b = con.prepareStatement (
            "SELECT time FROM teecurr2 " +
            "WHERE date = ? AND time > ? AND time < ? AND fb = ? AND courseName = ? " +
            "ORDER BY time");

         pstmt1b.clearParameters();        // clear the parms
         pstmt1b.setLong(1, date);
         pstmt1b.setInt(2, time);
         pstmt1b.setInt(3, last);
         pstmt1b.setInt(4, fb);
         pstmt1b.setString(5, course);

         rs = pstmt1b.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            last = rs.getInt(1);          // get the first time found - use as upper limit for display
         }
         pstmt1b.close();

      }
      catch (Exception e) {
      }

      i = 0;
        
      //
      //  If first tee time on sheet, then allow 20 tee times prior to this time.
      //
      if (first.equalsIgnoreCase( "yes" )) {

         mint = min;                // get original time
         hrt = hr;

         while (i < maxtimes) {           // determine the first time

            if (mint > 0) {
               mint--;
            } else {               // assume not midnight
               hrt--;
               mint = 59;
            }
            i++;
         }
         start = (hrt * 100) + mint;       // save first time for select
         maxtimes = 40;           // new max for this request
      }

      //
      //  Start with the time selected in case they want a tee time with same time, different f/b
      //
      if (dhr > 11) {

         dampm = " PM";
      }
      if (dhr > 12) {

         dhr = dhr - 12;
      }
      if (min < 10) {
         out.println("<option value=\"" +time+ "\">" +dhr+ ":0" +min+ " " +dampm+ "</option>");
      } else {
         out.println("<option value=\"" +time+ "\">" +dhr+ ":" +min+ " " +dampm+ "</option>");
      }

      //
      //  list tee times that follow the one selected, but less than 'last'
      //
      otime = time;           // save original time value
      i = 0;
      hr = start / 100;             // get values for start time (first in select list)
      min = start - (hr * 100);

      loop1:
      while (i < maxtimes) {

         dhr = hr;            // init as same
         dampm = " AM";

         if (hr == 0) {

            dhr = 12;
         }
         if (hr > 12) {

            dampm = " PM";
            dhr = hr - 12;
         }
         if (hr == 12) {

            dampm = " PM";
         }

         time = (hr * 100) + min;      // set time value

         if (time >= last) {           // if we reached the end

            break loop1;              // done
         }

         if (time != otime) {        // if not same as original time
            if (min < 10) {
               out.println("<option value=\"" +time+ "\">" +dhr+ ":0" +min+ " " +dampm+ "</option>");
            } else {
               out.println("<option value=\"" +time+ "\">" +dhr+ ":" +min+ " " +dampm+ "</option>");
            }
         }
           
         min++;               // next minute
           
         if (min > 59) {

            min = 0;          // rotate the hour
            hr++;
         }
         if (hr > 23) {

            break loop1;      // done
         }

         i++;
      }           // end of while

      out.println("</select>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("Front/Back:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"fb\">");
        out.println("<option value=\"00\">Front</option>");
        out.println("<option value=\"01\">Back</option>");
        out.println("<option value=\"09\">Crossover</option>");
      out.println("</select>");
      out.println("<br><br></p>");
      out.println("<p align=\"center\">");
        out.println("<input type=\"submit\" value=\"Insert\" name=\"insertSubmit\"></p>");
      out.println("</font></td></tr></form></table>");
      out.println("<br><br>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + ">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" +hide+ "\">");
      out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" +lott_name+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");

      //
      //  End of HTML page
      //
      out.println("</td></tr></table>");                           // end of main page
      out.println("</td></tr></table>");                           // end of whole page
      out.println("</center></body></html>");
      out.close();
      
   } else {     // end of Insert Submit processing

      //
      //   Call is from self to process an insert request (submit)
      //
      //   Parms passed:   time = time of the tee time to be inserted
      //                   date = date of the tee sheet
      //                   fb   = the front/back value (see above)
      //                   course = course name
      //                   returnCourse = course name for return

      //
      //  Check to make sure a slot like this doesn't already exist
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT mm FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, date);
         pstmt1.setInt(2, time);
         pstmt1.setInt(3, fb);
         pstmt1.setString(4, course);

         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center><BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>A tee time with these date, time and F/B values already exists.");
            out.println("<BR>One of these values must change so the tee time is unique.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</center></BODY></HTML>");
            return;

         }    // ok if we get here - not matching time slot

         pstmt1.close();   // close the stmt

      }
      catch (Exception ignore) {   // this is good if no match found
      }

      //
      //  This slot is unique - now check for events or restrictions for this date and time
      //

      try {

         SystemUtils.insertTee(date, time, fb, course, day_name, con);     // insert new tee time

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center><BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>Error in Proshop_dlott: " + e1.getMessage());
         out.println("<BR><BR>");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</center></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Insert complete - inform user
      //
      String sampm = "AM";
      if (hr > 11) {        // if PM

         sampm = "PM";
      }

      out.println("<HTML><HEAD><Title>Proshop Insert Confirmation</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dlott?index=" +indexs+ "&course=" +returnCourse+ "&jump=" +jump+ "&email=" +emailOpt+ "&hide=" +hide+ "&lott_name=" +lott_name+ "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H3>Insert Tee Time Confirmation</H3>");
      out.println("<BR><BR>Thank you, the following tee time has been added.");
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }
      if (min < 10) {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":0" + min + " " + sampm + "</b>");
      } else {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + min + " " + sampm + "</b>");
      }
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + ">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
      out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + lott_name + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</center></BODY></HTML>");
      
      //
      // Refresh the blockers in case the tee time added is covered by a blocker
      //
      SystemUtils.doBlockers(con);
      
      
      out.close();
   }

 }      // end of doInsert

 // *********************************************************
 //  changeCW - change the C/W option for 1 or all players in tee time.  
 //
 //  parms:
 //          jump        = jump index for return
 //          from_player = player position being changed (1-5) 
 //          from_time   = tee time being changed
 //          from_fb     = f/b of tee time
 //          from_course = name of course
 //          to_from     = current C/W option 
 //          to_to       = new C/W option
 //          changeAll   = change all players in slot (true or false)
 //          ninehole    = use 9 Hole options (true or false)
 //
 // *********************************************************

 private void changeCW(parmSlot slotParms, String changeAll, String ninehole, long date, HttpServletRequest req, PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;

   int in_use = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;

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
   String newcw = "";


   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dlott.changeCW - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use
      //
      try {

         in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in changeCW. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms, req);
      return;
   }

   //
   //  Ok - get current player info from the parm block (set by checkInUse)
   //
   player1 = slotParms.player1;
   player2 = slotParms.player2;
   player3 = slotParms.player3;
   player4 = slotParms.player4;
   player5 = slotParms.player5;
   p1cw = slotParms.p1cw;
   p2cw = slotParms.p2cw;
   p3cw = slotParms.p3cw;
   p4cw = slotParms.p4cw;
   p5cw = slotParms.p5cw;
   p91 = slotParms.p91;
   p92 = slotParms.p92;
   p93 = slotParms.p93;
   p94 = slotParms.p94;
   p95 = slotParms.p95;
     
   //
   //  If '9 Hole' option selected, then change new C/W to 9 hole type
   //
   newcw = slotParms.to_to;            // get selected C/W option
     
   //
   //  Set the new C/W value for each player requested
   //
   if (!player1.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 1)) {   // change this one?

         p1cw = newcw;
         if (ninehole.equals( "true" )) {
            p91 = 1;             // make it a 9 hole type
         } else {
            p91 = 0;             // make it 18 hole 
         }
      }
   }
   if (!player2.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 2)) {   // change this one?

         p2cw = newcw;
         if (ninehole.equals( "true" )) {
            p92 = 1;             // make it a 9 hole type
         } else {
            p92 = 0;             // make it 18 hole
         }
      }
   }
   if (!player3.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 3)) {   // change this one?

         p3cw = newcw;
         if (ninehole.equals( "true" )) {
            p93 = 1;             // make it a 9 hole type
         } else {
            p93 = 0;             // make it 18 hole
         }
      }
   }
   if (!player4.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 4)) {   // change this one?

         p4cw = newcw;
         if (ninehole.equals( "true" )) {
            p94 = 1;             // make it a 9 hole type
         } else {
            p94 = 0;             // make it 18 hole
         }
      }
   }
   if (!player5.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 5)) {   // change this one?

         p5cw = newcw;
         if (ninehole.equals( "true" )) {
            p95 = 1;             // make it a 9 hole type
         } else {
            p95 = 0;             // make it 18 hole
         }
      }
   }
     
   //
   //  Update the tee time and set it no longer in use
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET " +
         "p1cw=?, p2cw=?, p3cw=?, p4cw=?, in_use=0, p5cw=?, p91=?, p92=?, p93=?, p94=?, p95=? " +
         "WHERE date=? AND time=? AND fb=? AND courseName=?");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setString(1, p1cw);      
      pstmt1.setString(2, p2cw);
      pstmt1.setString(3, p3cw);
      pstmt1.setString(4, p4cw);
      pstmt1.setString(5, p5cw);
      pstmt1.setInt(6, p91);
      pstmt1.setInt(7, p92);
      pstmt1.setInt(8, p93);
      pstmt1.setInt(9, p94);
      pstmt1.setInt(10, p95);
      pstmt1.setLong(11, date);
      pstmt1.setInt(12, slotParms.from_time);
      pstmt1.setInt(13, slotParms.from_fb);
      pstmt1.setString(14, slotParms.from_course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();
   }
   catch (Exception e1) {

      String eMsg = "Error 2 in changeCW. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return 
   //
   editDone(out, slotParms, resp, req);

 }      // end of changeCW


 // *********************************************************
 //  changeFB - change the F/B option for the tee time specified.
 //
 //  parms: 
 //          jump        = jump index for return
 //          from_time   = tee time being changed
 //          from_fb     = current f/b of tee time
 //          from_course = name of course
 //          to_fb       = new f/b of tee time
 //
 // *********************************************************

 private void changeFB(parmSlot slotParms, long date, HttpServletRequest req, PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;

   int in_use = 0;


   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dlott.changeFB - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use
      //
      try {

         in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in changeFB. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms, req);
      return;
   }

   //
   //  Ok, tee time not busy - change the F/B
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET in_use = 0, fb = ? " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setInt(1, slotParms.to_fb);
      pstmt1.setLong(2, date);
      pstmt1.setInt(3, slotParms.from_time);
      pstmt1.setInt(4, slotParms.from_fb);
      pstmt1.setString(5, slotParms.from_course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();
   }
   catch (Exception e1) {

      String eMsg = "Error 2 in changeFB. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp, req);

 }      // end of changeFB


 // *********************************************************
 //  moveWhole - move an entire tee time
 //
 //  parms:
 //          jump        = jump index for return
 //          from_time   = tee time being moved
 //          from_fb     = f/b of tee time being moved
 //          from_course = name of course of tee time being moved
 //          to_time     = tee time to move to
 //          to_fb       = f/b of tee time to move to
 //          to_course   = name of course of tee time to move to
 //
 //          prompt      = null if first call here
 //                      = 'return' if user wants to return w/o changes
 //                      = 'continue' if user wants to continue with changes
 //          skip        = verification process to skip if 2nd return
 //
 // *********************************************************

 private void moveWhole(parmSlot slotParms, long date, String prompt, int skip, HttpServletRequest req, 
                        PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;
     
   int in_use = 0;

   String hideUnavail = req.getParameter("hide");
   
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
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
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String orig_by = "";
   String conf = "";
   String notes = "";

   short pos1 = 0;
   short pos2 = 0;
   short pos3 = 0;
   short pos4 = 0;
   short pos5 = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
     
   int hide = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int fives = 0;
   int sendemail = 0;

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   boolean error = false;
     

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dlott.moveWhole - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use (the FROM tee time)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, then tee time is already busy

            in_use = 0;
              
            getTeeTimeData(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {                 // if time slot already in use

      teeBusy(out, slotParms, req);       // reject as busy
      return;
   }

   //
   //  Ok - get current 'FROM' player info from the parm block (set by checkInUse) and save it
   //
   player1 = slotParms.player1;
   player2 = slotParms.player2;
   player3 = slotParms.player3;
   player4 = slotParms.player4;
   player5 = slotParms.player5;
   p1cw = slotParms.p1cw;
   p2cw = slotParms.p2cw;
   p3cw = slotParms.p3cw;
   p4cw = slotParms.p4cw;
   p5cw = slotParms.p5cw;
   user1 = slotParms.user1;
   user2 = slotParms.user2;
   user3 = slotParms.user3;
   user4 = slotParms.user4;
   user5 = slotParms.user5;
   hndcp1 = slotParms.hndcp1;
   hndcp2 = slotParms.hndcp2;
   hndcp3 = slotParms.hndcp3;
   hndcp4 = slotParms.hndcp4;
   hndcp5 = slotParms.hndcp5;
   show1 = slotParms.show1;
   show2 = slotParms.show2;
   show3 = slotParms.show3;
   show4 = slotParms.show4;
   show5 = slotParms.show5;
   pos1 = slotParms.pos1;
   pos2 = slotParms.pos2;
   pos3 = slotParms.pos3;
   pos4 = slotParms.pos4;
   pos5 = slotParms.pos5;
   mNum1 = slotParms.mNum1;
   mNum2 = slotParms.mNum2;
   mNum3 = slotParms.mNum3;
   mNum4 = slotParms.mNum4;
   mNum5 = slotParms.mNum5;
   userg1 = slotParms.userg1;
   userg2 = slotParms.userg2;
   userg3 = slotParms.userg3;
   userg4 = slotParms.userg4;
   userg5 = slotParms.userg5;
   notes = slotParms.notes;
   hide = slotParms.hide;
   orig_by = slotParms.orig_by;
   conf = slotParms.conf;
   p91 = slotParms.p91;
   p92 = slotParms.p92;
   p93 = slotParms.p93;
   p94 = slotParms.p94;
   p95 = slotParms.p95;

   slotParms.player1 = "";       // init parmSlot player fields (verifySlot will fill) 
   slotParms.player2 = "";
   slotParms.player3 = "";
   slotParms.player4 = "";
   slotParms.player5 = "";

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.to_time == 0 || slotParms.to_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dlott.moveWhole2 - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.to_time+ ", course= " +slotParms.to_course+ ", fb= " +slotParms.to_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Now check if the 'TO' tee time is currently in use (this will put its info in slotParms)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 2 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }


   //
   //  If 'TO' tee time is in use 
   //
   if (in_use != 0) {   

      //
      //  Error - We must free up the 'FROM' tee time
      //
      in_use = 0;

      try {

         PreparedStatement pstmt4 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setInt(1, in_use);
         pstmt4.setLong(2, date);
         pstmt4.setInt(3, slotParms.from_time);
         pstmt4.setInt(4, slotParms.from_fb);
         pstmt4.setString(5, slotParms.from_course);
         pstmt4.executeUpdate();      // execute the prepared stmt
         pstmt4.close();

      }
      catch (Exception ignore) {
      }

      teeBusy(out, slotParms, req);
      return;
   }

   //
   //  If user was prompted and opted to return w/o changes, then we must clear the 'in_use' flags
   //  before returning to the tee sheet.
   //
   if (prompt.equals( "return" )) {        // if prompt specified a return

      in_use = 0;

      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.from_time);
         pstmt1.setInt(4, slotParms.from_fb);
         pstmt1.setString(5, slotParms.from_course);

         pstmt1.executeUpdate();
         pstmt1.close();

         
         pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.to_time);
         pstmt1.setInt(4, slotParms.to_fb);
         pstmt1.setString(5, slotParms.to_course);

         pstmt1.executeUpdate();      // execute the prepared stmt
         pstmt1.close();

      }
      catch (Exception ignore) {
      }

      // return to Proshop_dlott

      out.println("<HTML><HEAD><Title>Proshop Edit Lottery Complete</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dlott?index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&email=" + slotParms.sendEmail + "&jump=" + slotParms.jump + "&lott_name=" +slotParms.lottery+ "&hide=" +hideUnavail+ "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H2>Return Accepted</H2>");
      out.println("<BR><BR>Thank you, click Return' below if this does not automatically return.<BR>");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + "></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\"></input>");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
      out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } else {    // not a 'return' response from prompt 

      //
      //  This is either the first time here, or a 'Continue' reply to a prompt
      //
      //
      p1 = slotParms.player1;      // get players' names for easier reference
      p2 = slotParms.player2;
      p3 = slotParms.player3;
      p4 = slotParms.player4;
      p5 = slotParms.player5;


      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         //
         //  Check if 'TO' tee time is empty
         //
         if (!p1.equals( "" ) || !p2.equals( "" ) || !p3.equals( "" ) || !p4.equals( "" ) || !p5.equals( "" )) {

            //
            //  Tee time is occupied - inform user and ask to continue or cancel
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Tee Time is Occupied</H3><BR>");
            out.println("<BR>WARNING: The tee time you are trying to move TO is already occupied.");
            out.println("<BR><BR>If you continue, this tee time will effectively be cancelled.");
            out.println("<BR><BR>Would you like to continue and overwrite this tee time?");
            out.println("<BR><BR>");

            out.println("<BR><BR>Course = " +slotParms.to_course+ ", p1= " +p1+ ", p2= " +p2+ ", p3= " +p3+ ", p4= " +p4+ ", p5= " +p5+ ".");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }
        
      //
      //  check if we are to skip this test
      //
      if (skip < 2) {
        
         //
         // *******************************************************************************
         //  Check member restrictions in 'TO' tee time, but 'FROM' players
         //
         //     First, find all restrictions within date & time constraints on this course.
         //     Then, find the ones for this day.
         //     Then, find any for this member type or membership type (all 5 players).
         //
         // *******************************************************************************
         //

         //
         //  allocate and setup new parm block to hold the tee time parms for this process
         //
         parmSlot slotParms2 = new parmSlot();          // allocate a parm block

         slotParms2.date = date;                 // get 'TO' info
         slotParms2.time = slotParms.to_time;
         slotParms2.course = slotParms.to_course;
         slotParms2.fb = slotParms.to_fb;
         slotParms2.day = slotParms.day;
            
         slotParms2.player1 = player1;          // get 'FROM' info      
         slotParms2.player2 = player2;
         slotParms2.player3 = player3;
         slotParms2.player4 = player4;
         slotParms2.player5 = player5;

         try {

            verifySlot.parseGuests(slotParms2, con);     // check for guests and set guest types

            error = verifySlot.parseNames(slotParms2, "pro");   // get the names (lname, fname, mi)

            verifySlot.getUsers(slotParms2, con);        // get the mship and mtype info (needs lname, fname, mi)

            error = false;                               // init error indicator

            error = verifySlot.checkMemRests(slotParms2, con);      // check restrictions  

         }
         catch (Exception ignore) {
         }                           

         if (error == true) {          // if we hit on a restriction
           
            //
            //  Prompt user to see if he wants to override this violation 
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" + slotParms2.player + "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms2.rest_name + "</b><br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"2\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 3) {

         //
         // *******************************************************************************
         //  Check 5-some restrictions - use 'FROM' player5 and 'TO' tee time slot
         //
         //   If 5-somes are restricted during this tee time, warn the proshop user.
         // *******************************************************************************
         //
         if ((!player5.equals( "" )) && (!slotParms.rest5.equals( "" ))) { // if 5-somes restricted prompt user to skip test

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 4) {

         //
         // *******************************************************************************
         //  Check 5-somes allowed on 'to course' and from-player5 specified
         // *******************************************************************************
         //
         if (!player5.equals( "" )) {      // if player5 exists in 'from' slot

            fives = 0;

            try {

               PreparedStatement pstmtc = con.prepareStatement (
                  "SELECT fives " +
                  "FROM clubparm2 WHERE courseName = ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, slotParms.to_course);
               rs = pstmtc.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  fives = rs.getInt("fives");
               }
               pstmtc.close();
            }
            catch (Exception e) {
            }
           
            if (fives == 0) {      // if 5-somes not allowed on to_course

               //
               //  Prompt user to see if he wants to override this violation
               //
               out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>5-Somes Restricted</H3><BR>");
               out.println("<BR>Sorry, <b>5-somes</b> are not allowed on the course player5 is being moved to.");
               out.println("<BR>Player5 will be lost if you continue.<br><br>");
               out.println("<BR><BR>Would you like to move this tee time without player5?");
               out.println("<BR><BR>");

               //
               //  Return to _insert as directed
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
               out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
               out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
               out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"4\">");
               out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
         }
      }

   }     // end of IF 'return' reply from prompt

   //
   //  If we get here, then the  move is OK 
   //
   //   - move 'FROM' tee time info into this one (TO)
   //
   if (skip == 4) {      // if player5 being moved to course that does not allow 5-somes
     
      player5 = "";
      user5 = "";
      userg5 = "";
   }        
  
   in_use = 0;
     

   //
   //  Make sure we have the players and other info (this has failed before!!!)
   //
   if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) || !player5.equals( "" )) {

      try {

         PreparedStatement pstmt6 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
            "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
            "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = ?, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
            "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
            "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, hideNotes = ?, " +
            "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
            "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setString(1, player1);
         pstmt6.setString(2, player2);
         pstmt6.setString(3, player3);
         pstmt6.setString(4, player4);
         pstmt6.setString(5, user1);
         pstmt6.setString(6, user2);
         pstmt6.setString(7, user3);
         pstmt6.setString(8, user4);
         pstmt6.setString(9, p1cw);
         pstmt6.setString(10, p2cw);
         pstmt6.setString(11, p3cw);
         pstmt6.setString(12, p4cw);
         pstmt6.setInt(13, in_use);            // set in_use to NOT
         pstmt6.setFloat(14, hndcp1);
         pstmt6.setFloat(15, hndcp2);
         pstmt6.setFloat(16, hndcp3);
         pstmt6.setFloat(17, hndcp4);
         pstmt6.setShort(18, show1);
         pstmt6.setShort(19, show2);
         pstmt6.setShort(20, show3);
         pstmt6.setShort(21, show4);
         pstmt6.setString(22, player5);
         pstmt6.setString(23, user5);
         pstmt6.setString(24, p5cw);
         pstmt6.setFloat(25, hndcp5);
         pstmt6.setShort(26, show5);
         pstmt6.setString(27, notes);
         pstmt6.setInt(28, hide);
         pstmt6.setString(29, mNum1);
         pstmt6.setString(30, mNum2);
         pstmt6.setString(31, mNum3);
         pstmt6.setString(32, mNum4);
         pstmt6.setString(33, mNum5);
         pstmt6.setString(34, userg1);
         pstmt6.setString(35, userg2);
         pstmt6.setString(36, userg3);
         pstmt6.setString(37, userg4);
         pstmt6.setString(38, userg5);
         pstmt6.setString(39, orig_by);
         pstmt6.setString(40, conf);
         pstmt6.setInt(41, p91);
         pstmt6.setInt(42, p92);
         pstmt6.setInt(43, p93);
         pstmt6.setInt(44, p94);
         pstmt6.setInt(45, p95);
         pstmt6.setShort(46, pos1);
         pstmt6.setShort(47, pos2);
         pstmt6.setShort(48, pos3);
         pstmt6.setShort(49, pos4);
         pstmt6.setShort(50, pos5);

         pstmt6.setLong(51, date);
         pstmt6.setInt(52, slotParms.to_time);
         pstmt6.setInt(53, slotParms.to_fb);
         pstmt6.setString(54, slotParms.to_course);

         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

      }
      catch (Exception e1) {

         String eMsg = "Error 3 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }

      //
      //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
      //
      String fullName = "Edit Tsheet Moved From " + slotParms.from_time;

      //  new tee time
      SystemUtils.updateHist(date, slotParms.day, slotParms.to_time, slotParms.to_fb, slotParms.to_course, player1, player2, player3,
                             player4, player5, slotParms.user, fullName, 0, con);


      //
      //  Finally, set the 'FROM' tee time to NOT in use and clear out the players
      //
      try {

         PreparedStatement pstmt5 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = '', player2 = '', player3 = '', player4 = '', " +
            "username1 = '', username2 = '', username3 = '', username4 = '', " +
            "in_use = 0, show1 = 0, show2 = 0, show3 = 0, show4 = 0, " +
            "player5 = '', username5 = '', show5 = 0, " +
            "notes = '', " +
            "mNum1 = '', mNum2 = '', mNum3 = '', mNum4 = '', mNum5 = '', " +
            "userg1 = '', userg2 = '', userg3 = '', userg4 = '', userg5 = '', orig_by = '', conf = '', " +
            "pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0, pos5 = 0 " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt5.clearParameters();        // clear the parms
         pstmt5.setLong(1, date);
         pstmt5.setInt(2, slotParms.from_time);
         pstmt5.setInt(3, slotParms.from_fb);
         pstmt5.setString(4, slotParms.from_course);

         pstmt5.executeUpdate();      // execute the prepared stmt

         pstmt5.close();

         if (slotParms.sendEmail.equalsIgnoreCase( "yes" )) {        // if ok to send emails

            sendemail = 1;          // tee time moved - send email notification
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 4 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }

      //
      //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
      //
      String empty = "";
      fullName = "Edit Tsheet Move To " + slotParms.to_time;

      SystemUtils.updateHist(date, slotParms.day, slotParms.from_time, slotParms.from_fb, slotParms.from_course, empty, empty, empty,
                             empty, empty, slotParms.user, fullName, 1, con);

   } else {
     
      //
      //  save message in error log
      //
      String msg = "Error in Proshop_dlott.moveWhole - Player names lost " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      teeBusy(out, slotParms, req);        // pretend its busy
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp, req);

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   if (sendemail != 0) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "moveWhole";         // type = Move Whole tee time
      parme.date = date;
      parme.time = 0;
      parme.to_time = slotParms.to_time;
      parme.from_time = slotParms.from_time;
      parme.fb = 0;
      parme.to_fb = slotParms.to_fb;
      parme.from_fb = slotParms.from_fb;
      parme.to_course = slotParms.to_course;
      parme.from_course = slotParms.from_course;
      parme.mm = slotParms.mm;
      parme.dd = slotParms.dd;
      parme.yy = slotParms.yy;

      parme.user = slotParms.user;
      parme.emailNew = 0;
      parme.emailMod = 0;
      parme.emailCan = 0;

      parme.p91 = p91;
      parme.p92 = p92;
      parme.p93 = p93;
      parme.p94 = p94;
      parme.p95 = p95;

      parme.day = slotParms.day;

      parme.player1 = player1;
      parme.player2 = player2;
      parme.player3 = player3;
      parme.player4 = player4;
      parme.player5 = player5;

      parme.user1 = user1;
      parme.user2 = user2;
      parme.user3 = user3;
      parme.user4 = user4;
      parme.user5 = user5;

      parme.pcw1 = p1cw;
      parme.pcw2 = p2cw;
      parme.pcw3 = p3cw;
      parme.pcw4 = p4cw;
      parme.pcw5 = p5cw;

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

   }     // end of IF sendemail
 }      // end of moveWhole


 // *********************************************************
 //  moveSingle - move a single player
 //
 //  parms:
 //          jump        = jump index for return
 //          from_player = player position being moved (1-5)
 //          from_time   = tee time being moved
 //          from_fb     = f/b of tee time being moved
 //          from_course = name of course
 //          to_player   = player position to move to (1-5)
 //          to_time     = tee time to move to
 //          to_fb       = f/b of tee time to move to
 //          to_course   = name of course
 //
 //          prompt      = null if first call here
 //                      = 'return' if user wants to return w/o changes
 //                      = 'continue' if user wants to continue with changes
 //          skip        = verification process to skip if 2nd return
 //
 // *********************************************************

 private void moveSingle(parmSlot slotParms, long date, String prompt, int skip, HttpServletRequest req, 
                         PrintWriter out, Connection con, HttpServletResponse resp) {


   PreparedStatement pstmt6 = null;
   ResultSet rs = null;

   int fives = 0;
   int in_use = 0;
   int from = slotParms.from_player;     // get the player positions (1-5)
   int fr = from;                        // save original value 
   int to = slotParms.to_player;
   int sendemail = 0;

   //
   //  arrays to hold the player info (FROM tee time)
   //
   String [] p = new String [5];
   String [] player = new String [5];
   String [] pcw = new String [5];
   String [] user = new String [5];
   String [] mNum = new String [5];
   String [] userg = new String [5];
   short [] show = new short [5];
   short [] pos = new short [5];
   float [] hndcp = new float [5];
   int [] p9 = new int [5];
     
   boolean error = false;

   String fullName = "Proshop Edit Lottery";      // for tee time history

   String hide = req.getParameter("hide");

   //
   //  adjust the player positions so they can be used for array indexes
   //
   if (to > 0 && to < 6) {
     
      to--;
        
   } else {
     
      to = 1;    // prevent big problem
   }
   if (from > 0 && from < 6) {

      from--;

   } else {

      from = 1;    // prevent big problem
   }

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dlott.moveSingle - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use (the FROM tee time)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in moveSingle. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms, req);       // first call here - reject as busy
      return;
   }

   //
   //  Ok - get current 'FROM' player info from the parm block (set by checkInUse) and save it
   //
   player[0] = slotParms.player1;
   player[1] = slotParms.player2;
   player[2] = slotParms.player3;
   player[3] = slotParms.player4;
   player[4] = slotParms.player5;
   pcw[0] = slotParms.p1cw;
   pcw[1] = slotParms.p2cw;
   pcw[2] = slotParms.p3cw;
   pcw[3] = slotParms.p4cw;
   pcw[4] = slotParms.p5cw;
   user[0] = slotParms.user1;
   user[1] = slotParms.user2;
   user[2] = slotParms.user3;
   user[3] = slotParms.user4;
   user[4] = slotParms.user5;
   hndcp[0] = slotParms.hndcp1;
   hndcp[1] = slotParms.hndcp2;
   hndcp[2] = slotParms.hndcp3;
   hndcp[3] = slotParms.hndcp4;
   hndcp[4] = slotParms.hndcp5;
   show[0] = slotParms.show1;
   show[1] = slotParms.show2;
   show[2] = slotParms.show3;
   show[3] = slotParms.show4;
   show[4] = slotParms.show5;
   mNum[0] = slotParms.mNum1;
   mNum[1] = slotParms.mNum2;
   mNum[2] = slotParms.mNum3;
   mNum[3] = slotParms.mNum4;
   mNum[4] = slotParms.mNum5;
   userg[0] = slotParms.userg1;
   userg[1] = slotParms.userg2;
   userg[2] = slotParms.userg3;
   userg[3] = slotParms.userg4;
   userg[4] = slotParms.userg5;
   p9[0] = slotParms.p91;
   p9[1] = slotParms.p92;
   p9[2] = slotParms.p93;
   p9[3] = slotParms.p94;
   p9[4] = slotParms.p95;
   pos[0] = slotParms.pos1;
   pos[1] = slotParms.pos2;
   pos[2] = slotParms.pos3;
   pos[3] = slotParms.pos4;
   pos[4] = slotParms.pos5;


   slotParms.player1 = "";       // init parmSlot player fields (verifySlot will fill)
   slotParms.player2 = "";
   slotParms.player3 = "";
   slotParms.player4 = "";
   slotParms.player5 = "";

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.to_time == 0 || slotParms.to_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dlott.moveSingle2 - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.to_time+ ", course= " +slotParms.to_course+ ", fb= " +slotParms.to_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Now check if the 'TO' tee time is currently in use (this will put its info in slotParms)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 2 in moveSingle. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   //
   //  If 'TO' tee time is in use 
   //
   if (in_use != 0) { 

      //
      //  Error - We must free up the 'FROM' tee time
      //
      try {

         PreparedStatement pstmt4 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = 0 " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setLong(1, date);
         pstmt4.setInt(2, slotParms.from_time);
         pstmt4.setInt(3, slotParms.from_fb);
         pstmt4.setString(4, slotParms.from_course);
         pstmt4.executeUpdate();      // execute the prepared stmt
         pstmt4.close();

      }
      catch (Exception ignore) {
      }

      teeBusy(out, slotParms, req);
      return;
   }

   //
   //  If user was prompted and opted to return w/o changes, then we must clear the 'in_use' flags
   //  before returning to the tee sheet.
   //
   if (prompt.equals( "return" )) {        // if prompt specified a return

      in_use = 0;

      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.from_time);
         pstmt1.setInt(4, slotParms.from_fb);
         pstmt1.setString(5, slotParms.from_course);

         pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

         pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.to_time);
         pstmt1.setInt(4, slotParms.to_fb);
         pstmt1.setString(5, slotParms.to_course);

         pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

      }
      catch (Exception ignore) {
      }

      // return to Proshop_dlott

      out.println("<HTML><HEAD><Title>Proshop Edit Lottery Complete</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dlott?index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&email=" + slotParms.sendEmail + "&jump=" + slotParms.jump + "&hide=" +hide+ "&lott_name=" +slotParms.lottery+ "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H2>Return Accepted</H2>");
      out.println("<BR><BR>Thank you, click Return' below if this does not automatically return.<BR>");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + "></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\"></input>");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
      out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } else {    // not a 'return' response from prompt

      //
      //  This is either the first time here, or a 'Continue' reply to a prompt
      //
      p[0] = slotParms.player1;    // save 'TO' player names
      p[1] = slotParms.player2;
      p[2] = slotParms.player3;
      p[3] = slotParms.player4;
      p[4] = slotParms.player5;


      //
      //  Make sure there are no duplicate names
      //
      if (!user[from].equals( "" )) {         // if player is a member
        
         if ((player[from].equalsIgnoreCase( p[0] )) || (player[from].equalsIgnoreCase( p[1] )) ||
             (player[from].equalsIgnoreCase( p[2] )) || (player[from].equalsIgnoreCase( p[3] )) ||
             (player[from].equalsIgnoreCase( p[4] ))) {

            //
            //  Error - name already exists
            //
            in_use = 0;

            try {

               PreparedStatement pstmt1 = con.prepareStatement (
                  "UPDATE teecurr2 SET in_use = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setInt(1, in_use);
               pstmt1.setLong(2, date);
               pstmt1.setInt(3, slotParms.from_time);
               pstmt1.setInt(4, slotParms.from_fb);
               pstmt1.setString(5, slotParms.from_course);

               pstmt1.executeUpdate();      // execute the prepared stmt

               pstmt1.close();

               pstmt1 = con.prepareStatement (
                  "UPDATE teecurr2 SET in_use = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setInt(1, in_use);
               pstmt1.setLong(2, date);
               pstmt1.setInt(3, slotParms.to_time);
               pstmt1.setInt(4, slotParms.to_fb);
               pstmt1.setString(5, slotParms.to_course);

               pstmt1.executeUpdate();      // execute the prepared stmt

               pstmt1.close();

            }
            catch (Exception ignore) {
            }

            out.println(SystemUtils.HeadTitle("Player Move Error"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Player Move Error</H3>");
            out.println("<BR><BR>Sorry, but the selected player is already scheduled at the time you are moving to.");
            out.println("<BR><BR>");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + "></input>");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\"></input>");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
               out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         //
         //  Check if 'TO' tee time position is empty
         //
         if (!p[to].equals( "" )) {

            //
            //  Tee time is occupied - inform user and ask to continue or cancel
            //
            out.println(SystemUtils.HeadTitle("Edit Lottery - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Tee Time Position is Occupied</H3><BR>");
            out.println("<BR>WARNING: The tee time position you are trying to move TO is already occupied.");
            out.println("<BR><BR>If you continue, the current player in this position (" +p[to]+ ") will be replaced.");
            out.println("<BR><BR>Would you like to continue and replace this player?");
            out.println("<BR><BR>");
               
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
           
         //
         // *******************************************************************************
         //  Check 5-somes allowed on 'to course' if moving to player5 slot
         // *******************************************************************************
         //
         if (to == 4) {      // if player being moved to player5 slot

            fives = 0;

            try {

               PreparedStatement pstmtc = con.prepareStatement (
                  "SELECT fives " +
                  "FROM clubparm2 WHERE courseName = ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, slotParms.to_course);
               rs = pstmtc.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  fives = rs.getInt("fives");
               }
               pstmtc.close();
            }
            catch (Exception e) {
            }

            if (fives == 0) {      // if 5-somes not allowed on to_course

               out.println(SystemUtils.HeadTitle("Player Move Error"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player Move Error</H3>");
               out.println("<BR><BR>Sorry, but the course you are moving the player to does not support 5-somes.");
               out.println("<BR><BR>");
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
               out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
               out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
         }

      }
        
      //
      //  check if we are to skip this test
      //
      if (skip < 2) {

         //
         // *******************************************************************************
         //  Check member restrictions in 'TO' tee time, but 'FROM' players
         //
         //     First, find all restrictions within date & time constraints on this course.
         //     Then, find the ones for this day.
         //     Then, find any for this member type or membership type (all 5 players).
         //
         // *******************************************************************************
         //

         //
         //  allocate and setup new parm block to hold the tee time parms for this process
         //
         parmSlot slotParms2 = new parmSlot();          // allocate a parm block

         slotParms2.date = date;                 // get 'TO' info
         slotParms2.time = slotParms.to_time;
         slotParms2.course = slotParms.to_course;
         slotParms2.fb = slotParms.to_fb;
         slotParms2.day = slotParms.day;

         slotParms2.player1 = player[from];          // get 'FROM' player (only check this player)

         try {

            verifySlot.parseGuests(slotParms2, con);     // check for guest and set guest type

            error = verifySlot.parseNames(slotParms2, "pro");   // get the name (lname, fname, mi)

            verifySlot.getUsers(slotParms2, con);        // get the mship and mtype info (needs lname, fname, mi)

            error = false;                               // init error indicator

            error = verifySlot.checkMemRests(slotParms2, con);      // check restrictions

         }
         catch (Exception ignore) {
         }

         if (error == true) {          // if we hit on a restriction

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Lottery - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" +player[from]+ "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms2.rest_name + "</b><br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"2\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 3) {

         //
         // *******************************************************************************
         //  Check 5-some restrictions - use 'FROM' player5 and 'TO' tee time slot
         //
         //   If moving to position 5 & 5-somes are restricted during this tee time, warn the proshop user.
         // *******************************************************************************
         //
         if ((to == 4) && (!slotParms.rest5.equals( "" ))) { // if 5-somes restricted prompt user to skip test

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Lottery - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

   }     // end of IF 'return' reply from prompt


   //
   //  OK to move player - move 'FROM' player info into this tee time (TO position)
   //
   to++;      // change index back to position value

   String moveP1 = "UPDATE teecurr2 SET player" +to+ " = ?, username" +to+ " = ?, p" +to+ "cw = ?, in_use = 0, hndcp" +to+ " = ?, show" +to+ " = ?, mNum" +to+ " = ?, userg" +to+ " = ?, p9" +to+ " = ?, pos" +to+ " = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
     
   try {

      pstmt6 = con.prepareStatement (moveP1);

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setString(1, player[from]);
      pstmt6.setString(2, user[from]);
      pstmt6.setString(3, pcw[from]);
      pstmt6.setFloat(4, hndcp[from]);
      pstmt6.setShort(5, show[from]);
      pstmt6.setString(6, mNum[from]);
      pstmt6.setString(7, userg[from]);
      pstmt6.setInt(8, p9[from]);
      pstmt6.setShort(9, pos[from]);

      pstmt6.setLong(10, date);
      pstmt6.setInt(11, slotParms.to_time);
      pstmt6.setInt(12, slotParms.to_fb);
      pstmt6.setString(13, slotParms.to_course);

      pstmt6.executeUpdate();      // execute the prepared stmt

      pstmt6.close();


      if (slotParms.sendEmail.equalsIgnoreCase( "yes" )) {        // if ok to send emails

         sendemail = 1;       // send email notification
      }

      //
      //  Track the history of this tee time - make entry in 'teehist' table (first, get the new players)
      //
      pstmt6 = con.prepareStatement (
      "SELECT player1, player2, player3, player4, player5 " +
      "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setLong(1, date);
      pstmt6.setInt(2, slotParms.to_time);
      pstmt6.setInt(3, slotParms.to_fb);
      pstmt6.setString(4, slotParms.to_course);
      rs = pstmt6.executeQuery();

      if (rs.next()) {

         player[0] = rs.getString(1);
         player[1] = rs.getString(2);
         player[2] = rs.getString(3);
         player[3] = rs.getString(4);
         player[4] = rs.getString(5);
      }
      pstmt6.close();

      fullName = "Edit Tsheet Move Player From " +slotParms.from_time;
      
      SystemUtils.updateHist(date, slotParms.day, slotParms.to_time, slotParms.to_fb, slotParms.to_course, player[0], player[1], player[2],
                             player[3], player[4], slotParms.user, fullName, 1, con);

   }
   catch (Exception e1) {

      String eMsg = "Error 3 in moveSingle. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }


   //
   //  Finally, set the 'FROM' tee time to NOT in use and clear out the player info
   //
   String moveP2 = "UPDATE teecurr2 SET player" +fr+ " = '', username" +fr+ " = '', p" +fr+ "cw = '', in_use = 0, show" +fr+ " = 0, mNum" +fr+ " = '', userg" +fr+ " = '', pos" +fr+ " = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";

   try {

      PreparedStatement pstmt5 = con.prepareStatement (moveP2);

      pstmt5.clearParameters();        // clear the parms
      pstmt5.setLong(1, date);
      pstmt5.setInt(2, slotParms.from_time);
      pstmt5.setInt(3, slotParms.from_fb);
      pstmt5.setString(4, slotParms.from_course);

      pstmt5.executeUpdate();      // execute the prepared stmt

      pstmt5.close();


      //
      //  Track the history of this tee time - make entry in 'teehist' table (first get the new player list)
      //
      pstmt6 = con.prepareStatement (
      "SELECT player1, player2, player3, player4, player5 " +
      "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setLong(1, date);
      pstmt6.setInt(2, slotParms.from_time);
      pstmt6.setInt(3, slotParms.from_fb);
      pstmt6.setString(4, slotParms.from_course);
      rs = pstmt6.executeQuery();

      if (rs.next()) {

         player[0] = rs.getString(1);
         player[1] = rs.getString(2);
         player[2] = rs.getString(3);
         player[3] = rs.getString(4);
         player[4] = rs.getString(5);
      }
      pstmt6.close();

      fullName = "Edit Tsheet Move Player To " +slotParms.to_time; 
      
      SystemUtils.updateHist(date, slotParms.day, slotParms.from_time, slotParms.from_fb, slotParms.from_course, player[0], player[1], player[2],
                             player[3], player[4], slotParms.user, fullName, 1, con);


   }
   catch (Exception e1) {

      String eMsg = "Error 4 in moveSingle. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp, req);

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   if (sendemail != 0) {

      try {                 // get the new 'to' tee time values

         PreparedStatement pstmt5b = con.prepareStatement (
         "SELECT player1, player2, player3, player4, username1, username2, username3, " +
         "username4, p1cw, p2cw, p3cw, p4cw, " +
         "player5, username5, p5cw, p91, p92, p93, p94, p95 " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt5b.clearParameters();        // clear the parms
         pstmt5b.setLong(1, date);
         pstmt5b.setInt(2, slotParms.to_time);
         pstmt5b.setInt(3, slotParms.to_fb);
         pstmt5b.setString(4, slotParms.to_course);
         rs = pstmt5b.executeQuery();      

         if (rs.next()) {

            player[0] = rs.getString(1);
            player[1] = rs.getString(2);
            player[2] = rs.getString(3);
            player[3] = rs.getString(4);
            user[0] = rs.getString(5);
            user[1] = rs.getString(6);
            user[2] = rs.getString(7);
            user[3] = rs.getString(8);
            pcw[0] = rs.getString(9);
            pcw[1] = rs.getString(10);
            pcw[2] = rs.getString(11);
            pcw[3] = rs.getString(12);
            player[4] = rs.getString(13);
            user[4] = rs.getString(14);
            pcw[4] = rs.getString(15);
            p9[0] = rs.getInt(16);
            p9[1] = rs.getInt(17);
            p9[2] = rs.getInt(18);
            p9[3] = rs.getInt(19);
            p9[4] = rs.getInt(20);
         }
         pstmt5b.close();

      }
      catch (Exception ignoree) {
      }

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "tee";         // type = Move Single tee time (use tee and emailMod) 
      parme.date = date;
      parme.time = slotParms.to_time;
      parme.to_time = 0;
      parme.from_time = 0;
      parme.fb = slotParms.to_fb;
      parme.to_fb = 0;
      parme.from_fb = 0;
      parme.to_course = slotParms.to_course;
      parme.from_course = slotParms.from_course;
      parme.mm = slotParms.mm;
      parme.dd = slotParms.dd;
      parme.yy = slotParms.yy;

      parme.user = slotParms.user;
      parme.emailNew = 0;
      parme.emailMod = 1;
      parme.emailCan = 0;

      parme.p91 = p9[0];
      parme.p92 = p9[1];
      parme.p93 = p9[2];
      parme.p94 = p9[3];
      parme.p95 = p9[4];

      parme.day = slotParms.day;

      parme.player1 = player[0];
      parme.player2 = player[1];
      parme.player3 = player[2];
      parme.player4 = player[3];
      parme.player5 = player[4];

      parme.oldplayer1 = slotParms.player1;
      parme.oldplayer2 = slotParms.player2;
      parme.oldplayer3 = slotParms.player3;
      parme.oldplayer4 = slotParms.player4;
      parme.oldplayer5 = slotParms.player5;

      parme.user1 = user[0];
      parme.user2 = user[1];
      parme.user3 = user[2];
      parme.user4 = user[3];
      parme.user5 = user[4];

      parme.olduser1 = slotParms.user1;
      parme.olduser2 = slotParms.user2;
      parme.olduser3 = slotParms.user3;
      parme.olduser4 = slotParms.user4;
      parme.olduser5 = slotParms.user5;

      parme.pcw1 = pcw[0];
      parme.pcw2 = pcw[1];
      parme.pcw3 = pcw[2];
      parme.pcw4 = pcw[3];
      parme.pcw5 = pcw[4];

      parme.oldpcw1 = slotParms.p1cw;
      parme.oldpcw2 = slotParms.p2cw;
      parme.oldpcw3 = slotParms.p3cw;
      parme.oldpcw4 = slotParms.p4cw;
      parme.oldpcw5 = slotParms.p5cw;

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

   }     // end of IF sendemail
 }      // end of moveSingle


/**
 //************************************************************************
 //
 //   Get tee time data
 //
 //************************************************************************
 **/

 private void getTeeTimeData(long date, int time, int fb, String course, parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;


   try {

      pstmt = con.prepareStatement (
         "SELECT * " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         slotParms.player1 = rs.getString( "player1" );
         slotParms.player2 = rs.getString( "player2" );
         slotParms.player3 = rs.getString( "player3" );
         slotParms.player4 = rs.getString( "player4" );
         slotParms.user1 = rs.getString( "username1" );
         slotParms.user2 = rs.getString( "username2" );
         slotParms.user3 = rs.getString( "username3" );
         slotParms.user4 = rs.getString( "username4" );
         slotParms.p1cw = rs.getString( "p1cw" );
         slotParms.p2cw = rs.getString( "p2cw" );
         slotParms.p3cw = rs.getString( "p3cw" );
         slotParms.p4cw = rs.getString( "p4cw" );
         slotParms.last_user = rs.getString( "in_use_by" );
         slotParms.hndcp1 = rs.getFloat( "hndcp1" );
         slotParms.hndcp2 = rs.getFloat( "hndcp2" );
         slotParms.hndcp3 = rs.getFloat( "hndcp3" );
         slotParms.hndcp4 = rs.getFloat( "hndcp4" );
         slotParms.show1 = rs.getShort( "show1" );
         slotParms.show2 = rs.getShort( "show2" );
         slotParms.show3 = rs.getShort( "show3" );
         slotParms.show4 = rs.getShort( "show4" );
         slotParms.player5 = rs.getString( "player5" );
         slotParms.user5 = rs.getString( "username5" );
         slotParms.p5cw = rs.getString( "p5cw" );
         slotParms.hndcp5 = rs.getFloat( "hndcp5" );
         slotParms.show5 = rs.getShort( "show5" );
         slotParms.notes = rs.getString( "notes" );
         slotParms.hide = rs.getInt( "hideNotes" );
         slotParms.rest5 = rs.getString( "rest5" );
         slotParms.mNum1 = rs.getString( "mNum1" );
         slotParms.mNum2 = rs.getString( "mNum2" );
         slotParms.mNum3 = rs.getString( "mNum3" );
         slotParms.mNum4 = rs.getString( "mNum4" );
         slotParms.mNum5 = rs.getString( "mNum5" );
         slotParms.userg1 = rs.getString( "userg1" );
         slotParms.userg2 = rs.getString( "userg2" );
         slotParms.userg3 = rs.getString( "userg3" );
         slotParms.userg4 = rs.getString( "userg4" );
         slotParms.userg5 = rs.getString( "userg5" );
         slotParms.orig_by = rs.getString( "orig_by" );
         slotParms.conf = rs.getString( "conf" );
         slotParms.p91 = rs.getInt( "p91" );
         slotParms.p92 = rs.getInt( "p92" );
         slotParms.p93 = rs.getInt( "p93" );
         slotParms.p94 = rs.getInt( "p94" );
         slotParms.p95 = rs.getInt( "p95" );
         slotParms.pos1 = rs.getShort( "pos1" );
         slotParms.pos2 = rs.getShort( "pos2" );
         slotParms.pos3 = rs.getShort( "pos3" );
         slotParms.pos4 = rs.getShort( "pos4" );
         slotParms.pos5 = rs.getShort( "pos5" );
      }

      pstmt.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting tee time data - Proshop_dlott.getTeeTimeData - Exception: " + e.getMessage());
   }

 }


 // *********************************************************
 //  Done
 // *********************************************************

 private void editDone(PrintWriter out, parmSlot slotParms, HttpServletResponse resp, HttpServletRequest req) {
   
   String hide = req.getParameter("hide");
   
   try {

       String url="/" +rev+ "/servlet/Proshop_dlott?index=" +slotParms.ind+ "&course=" + slotParms.returnCourse + "&jump=" + slotParms.jump + "&email=" + slotParms.sendEmail + "&hide=" +hide+ "&lott_name=" + slotParms.lottery;
       resp.sendRedirect(url);

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>System Error</H3>");
      out.println("<BR><BR>A system error occurred while trying to return to the edit tee sheet.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +slotParms.ind+ "&course=" +slotParms.returnCourse+ "\">");
      out.println("Return to Tee Sheet</a></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
 }


 // *********************************************************
 //  Tee Time Busy Error
 // *********************************************************

 private void teeBusy(PrintWriter out, parmSlot slotParms, HttpServletRequest req) {

    String hide = req.getParameter("hide");

    out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<CENTER><BR><BR><H2>Tee Time Slot Busy</H2>");
    out.println("<BR><BR>Sorry, but this tee time slot is currently busy.");
    out.println("<BR><BR>If you are attempting to move a player to another position within the same tee time,");
    out.println("<BR>you will have to Return to the Tee Sheet and select that tee time to update it.");
    out.println("<BR><BR>Otherwise, please select another time or try again later.");
    out.println("<BR><BR>");
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
     out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + "></input>");
     out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\"></input>");
     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
     out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
     out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
     out.println("<input type=\"hidden\" name=\"lott_name\" value=\"" + slotParms.lottery + "\">");
    out.println("<input type=\"submit\" value=\"Back to Lottery\" style=\"text-decoration:underline; background:#8B8970\"></form>");

    out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
     out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
     out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
     out.println("<input type=\"submit\" value=\"Return to Tee Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1, int index, String course, String eMsg) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>Error in dbError in Proshop_dlott:");
      out.println("<BR><BR>" + eMsg + " Exc= " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "\">");
      out.println("Return to Tee Sheet</a></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }

 
 // *********************************************************
 //  Send any unsent emails
 // *********************************************************
 
 private void sendLotteryEmails2(String clubName, PrintWriter out, Connection con) {

    Statement estmt = null;
    Statement stmtN = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmtd = null;
    PreparedStatement pstmtd2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    String errorMsg = "";
    String course = "";
    String day = "";

    int time = 0;
    int atime1 = 0;
    int atime2 = 0;
    int atime3 = 0;
    int atime4 = 0;
    int atime5 = 0;
    int groups = 0;
    int dd = 0;
    int mm = 0;
    int yy = 0;
    int afb = 0;
    int afb2 = 0;
    int afb3 = 0;
    int afb4 = 0;
    int afb5 = 0;

    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";

    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";

    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";

    String p1cw = "";
    String p2cw = "";
    String p3cw = "";
    String p4cw = "";
    String p5cw = "";
    
    int p91 = 0;
    int p92 = 0;
    int p93 = 0;
    int p94 = 0;
    int p95 = 0;

    
    String g1user1 = user1;
    String g1user2 = user2;
    String g1user3 = user3;
    String g1user4 = user4;
    String g1user5 = "";
    String g1player1 = player1;
    String g1player2 = player2;
    String g1player3 = player3;
    String g1player4 = player4;
    String g1player5 = "";
    String g1p1cw = p1cw;
    String g1p2cw = p2cw;
    String g1p3cw = p3cw;
    String g1p4cw = p4cw;
    String g1p5cw = "";
    String g1userg1 = userg1;
    String g1userg2 = userg2;
    String g1userg3 = userg3;
    String g1userg4 = userg4;
    String g1userg5 = "";
    int g1p91 = p91;
    int g1p92 = p92;
    int g1p93 = p93;
    int g1p94 = p94;
    int g1p95 = 0;

    String g2user1 = "";
    String g2user2 = "";
    String g2user3 = "";
    String g2user4 = "";
    String g2user5 = "";
    String g2player1 = "";
    String g2player2 = "";
    String g2player3 = "";
    String g2player4 = "";
    String g2player5 = "";
    String g2p1cw = "";
    String g2p2cw = "";
    String g2p3cw = "";
    String g2p4cw = "";
    String g2p5cw = "";
    String g2userg1 = "";
    String g2userg2 = "";
    String g2userg3 = "";
    String g2userg4 = "";
    String g2userg5 = "";
    int g2p91 = 0;
    int g2p92 = 0;
    int g2p93 = 0;
    int g2p94 = 0;
    int g2p95 = 0;

    String g3user1 = "";
    String g3user2 = "";
    String g3user3 = "";
    String g3user4 = "";
    String g3user5 = "";
    String g3player1 = "";
    String g3player2 = "";
    String g3player3 = "";
    String g3player4 = "";
    String g3player5 = "";
    String g3p1cw = "";
    String g3p2cw = "";
    String g3p3cw = "";
    String g3p4cw = "";
    String g3p5cw = "";
    String g3userg1 = "";
    String g3userg2 = "";
    String g3userg3 = "";
    String g3userg4 = "";
    String g3userg5 = "";
    int g3p91 = 0;
    int g3p92 = 0;
    int g3p93 = 0;
    int g3p94 = 0;
    int g3p95 = 0;

    String g4user1 = "";
    String g4user2 = "";
    String g4user3 = "";
    String g4user4 = "";
    String g4user5 = "";
    String g4player1 = "";
    String g4player2 = "";
    String g4player3 = "";
    String g4player4 = "";
    String g4player5 = "";
    String g4p1cw = "";
    String g4p2cw = "";
    String g4p3cw = "";
    String g4p4cw = "";
    String g4p5cw = "";
    String g4userg1 = "";
    String g4userg2 = "";
    String g4userg3 = "";
    String g4userg4 = "";
    String g4userg5 = "";
    int g4p91 = 0;
    int g4p92 = 0;
    int g4p93 = 0;
    int g4p94 = 0;
    int g4p95 = 0;

    String g5user1 = "";
    String g5user2 = "";
    String g5user3 = "";
    String g5user4 = "";
    String g5user5 = "";
    String g5player1 = "";
    String g5player2 = "";
    String g5player3 = "";
    String g5player4 = "";
    String g5player5 = "";
    String g5p1cw = "";
    String g5p2cw = "";
    String g5p3cw = "";
    String g5p4cw = "";
    String g5p5cw = "";
    String g5userg1 = "";
    String g5userg2 = "";
    String g5userg3 = "";
    String g5userg4 = "";
    String g5userg5 = "";
    int g5p91 = 0;
    int g5p92 = 0;
    int g5p93 = 0;
    int g5p94 = 0;
    int g5p95 = 0;



    //*****************************************************************************
    //  Send an email to all in this request
    //*****************************************************************************
    //

    try {

        estmt = con.createStatement();        // create a statement

        rs = estmt.executeQuery("" +
            "SELECT * " +
            "FROM teecurr2 " +
            "WHERE lottery = ? AND lottery_email <> 0;");

        while (rs.next()) {
            
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            user5 = rs.getString("username5");
            
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");
            
            //
            //  Get today's date and time for email processing
            //
            Calendar ecal = new GregorianCalendar();               // get todays date
            int eyear = ecal.get(Calendar.YEAR);
            int emonth = ecal.get(Calendar.MONTH);
            int eday = ecal.get(Calendar.DAY_OF_MONTH);
            int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
            int e_min = ecal.get(Calendar.MINUTE);

            int e_time = 0;
            long e_date = 0;

               //
               //  Build the 'time' string for display
               //
               //    Adjust the time based on the club's time zone (we are Central)
               //
               e_time = (e_hourDay * 100) + e_min;

               e_time = SystemUtils.adjustTime(con, e_time);       // adjust for time zone

               if (e_time < 0) {          // if negative, then we went back or ahead one day

                  e_time = 0 - e_time;        // convert back to positive value

                  if (e_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                     //
                     // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                     //
                     ecal.add(Calendar.DATE,1);                     // get next day's date

                     eyear = ecal.get(Calendar.YEAR);
                     emonth = ecal.get(Calendar.MONTH);
                     eday = ecal.get(Calendar.DAY_OF_MONTH);

                  } else {                        // we rolled back 1 day

                     //
                     // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                     //
                     ecal.add(Calendar.DATE,-1);                     // get yesterday's date

                     eyear = ecal.get(Calendar.YEAR);
                     emonth = ecal.get(Calendar.MONTH);
                     eday = ecal.get(Calendar.DAY_OF_MONTH);
                  }
               }

               int e_hour = e_time / 100;                // get adjusted hour
               e_min = e_time - (e_hour * 100);          // get minute value
               int e_am_pm = 0;                         // preset to AM

               if (e_hour > 11) {

                  e_am_pm = 1;                // PM
                  e_hour = e_hour - 12;       // set to 12 hr clock
               }
               if (e_hour == 0) {

                  e_hour = 12;
               }

               String email_time = "";

               emonth = emonth + 1;                            // month starts at zero
               e_date = (eyear * 10000) + (emonth * 100) + eday;

               //
               //  get date/time string for email message
               //
               if (e_am_pm == 0) {
                  if (e_min < 10) {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":0" + e_min + " AM";
                  } else {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + e_min + " AM";
                  }
               } else {
                  if (e_min < 10) {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":0" + e_min + " PM";
                  } else {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + e_min + " PM";
                  }
               }

               //
               //***********************************************
               //  Send email notification if necessary
               //***********************************************
               //
               String to = "";                          // to address
               String f_b = "";
               String eampm = "";
               String etime = "";
               String enewMsg = "";
               int emailOpt = 0;                        // user's email option parm
               int ehr = 0;
               int emin = 0;
               int send = 0;

               PreparedStatement pstmte1 = null;

               //
               //  set the front/back value
               //
               f_b = "Front";

               if (afb == 1) {

                  f_b = "Back";
               }

               String enew1 = "";
               String enew2 = "";
               String subject = "";

               if (clubName.startsWith( "Old Oaks" )) {
                 
                  enew1 = "The following Tee Time has been ASSIGNED.\n\n";
                  enew2 = "The following Tee Times have been ASSIGNED.\n\n";
                  subject = "ForeTees Tee Time Assignment Notification";

               } else {

                  if (clubName.startsWith( "Westchester" )) {

                     enew1 = "The following Draw Tee Time has been ASSIGNED.\n\n";
                     enew2 = "The following Draw Tee Times have been ASSIGNED.\n\n";
                     subject = "Your Tee Time for Weekend Draw";

                  } else {

                     enew1 = "The following Lottery Tee Time has been ASSIGNED.\n\n";
                     enew2 = "The following Lottery Tee Times have been ASSIGNED.\n\n";
                     subject = "ForeTees Lottery Assignment Notification";
                  }
               }

               if (!clubName.equals( "" )) {

                  subject = subject + " - " + clubName;
               }

               Properties properties = new Properties();
               properties.put("mail.smtp.host", SystemUtils.host);                      // set outbound host address
               properties.put("mail.smtp.port", SystemUtils.port);                      // set outbound port
               properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

               Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

               MimeMessage message = new MimeMessage(mailSess);

               try {

                  message.setFrom(new InternetAddress(SystemUtils.EFROM));                               // set from addr

                  message.setSubject( subject );                                            // set subject line
                  message.setSentDate(new java.util.Date());                                // set date/time sent
               }
               catch (Exception ignore) {
               }

               //
               //  Set the recipient addresses
               //
               if (!g1user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g4user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g4user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               //
               //  send email if anyone to send it to
               //
               if (send != 0) {        // if any email addresses specified for members
                  //
                  //  Create the message content
                  //
                  if (groups > 1) {
                     if (afb == afb2 && afb == afb3 && afb == afb4 && afb == afb5) {    // if all on the same tee 
                        enewMsg = SystemUtils.header + enew2 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on the " + f_b + " tee ";
                     } else {
                        enewMsg = SystemUtils.header + enew2 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on both tees ";
                     }
                  } else {
                     if (afb == afb2 && afb == afb3 && afb == afb4 && afb == afb5) {    // if all on the same tee
                        enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on the " + f_b + " tee ";
                     } else {
                        enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on both tees ";
                     }
                  }
                  if (!course.equals( "" )) {

                     enewMsg = enewMsg + "of Course: " + course;
                  }

                  //
                  //  convert time to hour and minutes for email msg
                  //
                  time = atime1;              // time for this tee time
                  ehr = time / 100;
                  emin = time - (ehr * 100);
                  eampm = " AM";
                  if (ehr > 12) {

                     eampm = " PM";
                     ehr = ehr - 12;       // convert from military time
                  }
                  if (ehr == 12) {

                     eampm = " PM";
                  }
                  if (ehr == 0) {

                     ehr = 12;
                     eampm = " AM";
                  }

                  if (emin < 10) {

                     etime = ehr + ":0" + emin + eampm;

                  } else {

                     etime = ehr + ":" + emin + eampm;
                  }

                  enewMsg = enewMsg + "\n at " + etime + "\n";

                  if (!g1player1.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 1: " + g1player1 + "  " + g1p1cw;
                  }
                  if (!g1player2.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 2: " + g1player2 + "  " + g1p2cw;
                  }
                  if (!g1player3.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 3: " + g1player3 + "  " + g1p3cw;
                  }
                  if (!g1player4.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 4: " + g1player4 + "  " + g1p4cw;
                  }
                  if (!g1player5.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 5: " + g1player5 + "  " + g1p5cw;
                  }

                  if (groups > 1) {

                     time = atime2;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g2player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g2player1 + "  " + g2p1cw;
                     }
                     if (!g2player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g2player2 + "  " + g2p2cw;
                     }
                     if (!g2player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g2player3 + "  " + g2p3cw;
                     }
                     if (!g2player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g2player4 + "  " + g2p4cw;
                     }
                     if (!g2player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g2player5 + "  " + g2p5cw;
                     }
                  }

                  if (groups > 2) {

                     time = atime3;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g3player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g3player1 + "  " + g3p1cw;
                     }
                     if (!g3player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g3player2 + "  " + g3p2cw;
                     }
                     if (!g3player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g3player3 + "  " + g3p3cw;
                     }
                     if (!g3player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g3player4 + "  " + g3p4cw;
                     }
                     if (!g3player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g3player5 + "  " + g3p5cw;
                     }
                  }

                  if (groups > 3) {

                     time = atime4;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g4player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g4player1 + "  " + g4p1cw;
                     }
                     if (!g4player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g4player2 + "  " + g4p2cw;
                     }
                     if (!g4player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g4player3 + "  " + g4p3cw;
                     }
                     if (!g4player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g4player4 + "  " + g4p4cw;
                     }
                     if (!g4player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g4player5 + "  " + g4p5cw;
                     }
                  }

                  if (groups > 4) {

                     time = atime5;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g5player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g5player1 + "  " + g5p1cw;
                     }
                     if (!g5player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g5player2 + "  " + g5p2cw;
                     }
                     if (!g5player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g5player3 + "  " + g5p3cw;
                     }
                     if (!g5player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g5player4 + "  " + g5p4cw;
                     }
                     if (!g5player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g5player5 + "  " + g5p5cw;
                     }
                  }

                  enewMsg = enewMsg + SystemUtils.trailer;

                  try {
                     message.setText( enewMsg );  // put msg in email text area

                     Transport.send(message);     // send it!!
                  }
                  catch (Exception ignore) {
                  }
               }     // end of IF send
       
               
        } // end while loop for teecurr2
        
        estmt.close();
    }
    catch (Exception ignore) {
    }

 }
 
 
 private void sendLotteryEmails(String clubName, PrintWriter out, Connection con) {

    parmEmail parme = new parmEmail();          // allocate an Email parm block
    
    ResultSet rs = null;
    Statement stmt = null;
    
    
   
}

} // end servlet