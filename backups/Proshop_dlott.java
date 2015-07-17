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
            
      if (parm.multi != 0) {           // if multiple courses supported for this club
          
          PreparedStatement pstmt;
          
          // if no course was specified (default) then set the course variable to the course this lottery is for
          if (course.equals("")) {
              pstmt = con.prepareStatement ("" +
                    "SELECT courseName " +
                    "FROM lottery3 " +
                    "WHERE name = ?");

              pstmt.clearParameters();
              pstmt.setString(1, lott_name);
              rs = pstmt.executeQuery();

              if (rs.next()) course = rs.getString(1);
          }
          
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
                    
         if (index >= 0) {
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "\" title=\"Return to Tee Sheet\" alt=\"Return\">");
             out.println("<nobr>Return to Tee Sheet</nobr></a><br>");
         } else {
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "&oldsheets\" title=\"Return to Date Selection\" alt=\"Return\">");
             out.println("<nobr>Return to Date Selection</nobr></a><br>");
             
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?oldsheets&course=" + course + "&calDate=" + month + "/" + day + "/" + year + "\" title=\"Back\" alt=\"Back\" onclick=\"return checkCourse()\">");
             out.println("<nobr>Back</nobr></a><br>");
         }
         
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
    out.println(" class=header style=\"left: " +lcol_start[2]+ "px; width: " +lcol_width[2]+ "px\">Desired</span><span");
    out.println(" class=header style=\"left: " +lcol_start[3]+ "px; width: " +lcol_width[3]+ "px\">Assigned</span><span");
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
        
    PreparedStatement pstmt = con.prepareStatement ("" +
            "SELECT c.fives, l.* " +
            "FROM lreqs3 l, clubparm2 c " +
            "WHERE name = ? AND date = ? AND c.courseName = l.courseName" + 
            (course.equals("-ALL-") ? "" : " AND l.courseName = ?") + ";");
    
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
    int tmp_groups = 1;
    int max_players = 0;
    int sum_players = 0;
    int req_time = 0;
    String fullName = "";
    String cw = "";
    String notes = "";
    String in_use_by = "";
    String time_color = "";
    int dts_slot_index = 0; // slot index number
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
        max_players = ((rs.getInt("fives") == 0) ? 4 : 5);
        if (in_use == 1) in_use_by = rs.getString("in_use_by");
        //in_use = (rs.getString("in_use_by").equals("")) ? 0 : 1;
        
        j++; // increment the jump label index (where to jump on page)
        
        if (course.equals( "-ALL-" )) { // only display this col if multi course club
            
            for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                if (courseName.equals(courseA[tmp_i])) break;
            }
        }
        
        if (groups == 1) {
            
            dts_defaultF3Color = "white";
            buildRow(dts_slot_index, groups, course, course_color[tmp_i], bgcolor, max_players, courseCount, lcol_start, lcol_width, index, emailOpt, j, dts_defaultF3Color, rs, out);
            dts_slot_index++;
            
        } else {
            
            while (tmp_groups <= groups) {
                
                dts_defaultF3Color = "yellow";
                buildRow(dts_slot_index, tmp_groups, course, course_color[tmp_i], bgcolor, max_players, courseCount, lcol_start, lcol_width, index, emailOpt, j, dts_defaultF3Color, rs, out);
                tmp_groups++;
                dts_slot_index++;
                
            }
        }
    
    } // end rs loop of lottery requests
    
    out.println("<!-- total_lottery_slots=" + dts_slot_index + " -->");
    
/*        
        out.print("<div id=lottery_slot_"+ dts_slot_index +" time=\"" + req_time + "\" course=\"" + courseName + "\" startX=0 startY=0 lotteryId="+lottery_id+" ");
        if (in_use == 0) {
          // not in use
          out.println("class=lotterySlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
        } else {
          // in use
          out.println("class=timeSlotInUse>");
        }
        
        // col for 'insert' and 'delete' requests
        out.print(" <span id=time_slot_" + dts_slot_index + "_A class=cellDataB style=\"cursor: default; left: " + lcol_start[1] + "px; width: " + lcol_width[1] + "px; background-color: #FFFFFF\">");
        j++;                                            // increment the jump label index (where to jump on page)
        out.print("<a name=\"jump" + j + "\"></a>");    // create a jump label for returns
        
        if (in_use == 0) {
            
            // not in use
            out.print("<a href=\"/" +rev+ "/servlet/ProshopTLT_slot?index=" +index+ "&lotteryId=" +lottery_id+ "&returnCourse=" +course+ "&email=" +emailOpt+ "\" title=\"Edit notification\" alt=\"Edit notification\">");
            out.print("<img src=/" +rev+ "/images/dts_edit.gif width=13 height=13 border=0></a>");
            out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
            out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?index=" +index+ "&lotteryId=" +lottery_id+ "&returnCourse=" +course+ "&email=" +emailOpt+ "&delete=yes\" title=\"Delete notification\" alt=\"Remove notification\" onclick=\"return confirm('Are you sure you want to permanently delete this notification?');\">");
            out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");
            
        } else {
            
            // in use
            out.print("<img src=/" +rev+ "/images/busy.gif width=32 height=13 border=0 alt=\"" + in_use_by + "\" title=\"Busy\">");
            
        }
        out.println("</span>");
        
        //
        // Requested Time
        //
        if (in_use == 0) {
          out.print(" <span id=lottery_slot_" + dts_slot_index + "_time class=cellData hollow=true style=\"left: " + lcol_start[2] + "px; width: " + lcol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
        } else {
          out.print(" <span id=lottery_slot_" + dts_slot_index + "_time class=cellData style=\"cursor: default; left: " + lcol_start[2] + "px; width: " + lcol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
        }
        out.print(getTime(req_time));
        out.print("</span>");
        
        //
        // Course
        //
        if (course.equals( "-ALL-" )) { // only display this col if multi course club
            
            for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                if (courseName.equals(courseA[tmp_i])) break;
            }
            out.print("<span id=lottery_slot_" + dts_slot_index + "_course class=cellDataC style=\"cursor: default; left: " + lcol_start[3] + "px; width: " + lcol_width[3] + "px; background-color:" + course_color[tmp_i] + "\">");
            if (!courseName.equals("")) { out.print(fitName(courseName)); }
            out.print("</span>");
        }
        
        out.print("<span id=lottery_slot_" + dts_slot_index + "_members class=cellDataB value=\"\" style=\"cursor: default; left: " + lcol_start[5] + "px; width: " + lcol_width[5] + "px; text-align: left\">");
        
        tmp_found2 = false;
        
        for (int x = 0; x <= 24; x++) {
        
            fullName = rs.getString(x + 12);
            cw = rs.getString(x + 62);
            nineHole = rs.getInt(x + 136);
            eighteenHole = 1; //rs.getInt(x + );
            
            if (!fullName.equals("")) {
                if (tmp_found2) out.print(",&nbsp; "); else out.print("&nbsp;");
                out.print(fullName + " <font style=\"font-size:8px\">(" + cw + ")</font>");
                tmp_found2 = true;
                sum_players++;
            }
        }
        
        if (!notes.equals("")) {
            
            // this won't work in rl but is meant to show a dynamic popup, or we can spawn a small popup window that will show the notes
            out.println("&nbsp; &nbsp; <img src=\"/"+rev+"/images/notes.gif\" width=10 height=12 border=0 alt=\""+notes+"\">");
        }
        
        out.print("</span>");
        
        out.print("<span class=cellDataB style=\"cursor: default; left: " + lcol_start[6] + "px; width: " + lcol_width[6] + "px\">");
        out.print(sum_players);
        out.println("</span>");
        
        out.print("<span class=cellDataB style=\"cursor: default; left: " + lcol_start[7] + "px; width: " + lcol_width[7] + "px\">");
        //out.print(((nineHole == 0) ? "18" : "9"));
        if (nineHole == 1 && eighteenHole == 1) {
            out.print("mixed");
        } else if (nineHole == 1) {
            out.print("9");
        } else if (eighteenHole == 1) {
            out.print("18");
        }
        out.println("</span>");
        
        out.println("</div>");
*/        
    
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
                ((hideUnavail.equals("1") && index == 0) ? "AND time > ? " : "") + 
                "ORDER BY time, courseName, fb";
    
    out.println("<!-- index=" + index + " -->");
    out.println("<!-- date=" + date + " -->");
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
    
    if (hideUnavail.equals("1") && index == 0) {
        pstmt.setInt(parm_index, cal_time);
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
         
         if (hideUnavail.equals("1")) {
             if (fives == 0 || !rest5.equals("") ) {

                 if ( !player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("") ) blnHide = true;

             } else {

                 if ( !player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("")  && !player5.equals("")) blnHide = true;

             }
         }
         
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
                out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?index=" +index+ "&course=" +courseT+ "&returnCourse=" +course+ "&time=" +time+ "&fb=" +fb+ "&jump=" +j+ "&email=" +emailOpt+ "&first=" +first+ "&insert=yes\" title=\"Insert a time slot\" alt=\"Insert a time slot\">");
                out.print("<img src=/" +rev+ "/images/dts_newrow.gif width=13 height=13 border=0></a>");
                out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
                out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?index=" +index+ "&course=" +courseT+ "&returnCourse=" +course+ "&time=" +time+ "&fb=" +fb+ "&jump=" +j+ "&email=" +emailOpt+ "&delete=yes\" title=\"Delete time slot\" alt=\"Remove time slot\">");
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
   if (req.getParameter("convert") != null && req.getParameter("convert").equals("yes")) {

       convert(req, out, con, session, resp);
       return;
   }
   

 }  // end of doPost

 
 //
 // returns the player name but enforces a max length for staying in the width allowed
 // change the two positive values to control the output
 //
 private static String fitName(String pName) {
     
   return (pName.length() > 14) ? pName.substring(0, 13) + "..." : pName;
 }

 
 private static String getTime(int time) {
  
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
    
 }
 

 private void buildRow(int slotIndex, int group, String course, String course_color, String bgcolor, int max_players, int courseCount, int lcol_start[], int lcol_width[], int index, String emailOpt, int j, String dts_defaultF3Color, ResultSet rs, PrintWriter out) {

    // out.println("<!-- slotIndex=" + slotIndex + ", group=" + group + ", max_players=" + max_players + " -->");
     
    try {
        
    out.print("<div id=lottery_slot_"+ slotIndex +" time=\"" + rs.getInt("time") + "\" course=\"" + rs.getString("courseName") + "\" startX=0 startY=0 lotteryId=\"" + rs.getInt("id") + "\" group=\"" + group + "\" ");
    if (rs.getInt("in_use") == 0) {
        // not in use
        out.println("class=lotterySlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
    } else {
        // in use
        out.println("class=timeSlotInUse>");
    }

    // col for 'insert' and 'delete' requests
    out.print(" <span id=lottery_slot_" + slotIndex + "_A class=cellDataB style=\"cursor: default; left: " + lcol_start[1] + "px; width: " + lcol_width[1] + "px; background-color: #FFFFFF\">");
    j++;                                            // increment the jump label index (where to jump on page)
    out.print("<a name=\"jump" + j + "\"></a>");    // create a jump label for returns

    if (rs.getInt("in_use") == 0) {

        // not in use
        out.print("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&lotteryId=" + rs.getInt("id") + "&returnCourse=" +course+ "&email=" +emailOpt+ "&convert=yes\" title=\"Move Request\" alt=\"Move Request\">");
        out.print("<img src=/" +rev+ "/images/dts_move.gif width=13 height=13 border=0></a>");
        out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
        out.print("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&lotteryId=" + rs.getInt("id") + "&returnCourse=" +course+ "&email=" +emailOpt+ "&delete=yes\" title=\"Delete Request\" alt=\"Remove Request\" onclick=\"return confirm('Are you sure you want to permanently delete this lottery request?');\">");
        out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");

    } else {

        // in use
        out.print("<img src=/" +rev+ "/images/busy.gif width=32 height=13 border=0 alt=\"" + rs.getString("in_use_by") + "\" title=\"Busy\">");

    }
    out.println("</span>");

    //
    // Requested Time
    //
    if (rs.getInt("in_use") == 0) {
        out.print(" <span id=lottery_slot_" + slotIndex + "_time class=cellData hollow=true style=\"left: " + lcol_start[2] + "px; width: " + lcol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
    } else {
        out.print(" <span id=lottery_slot_" + slotIndex + "_time class=cellData style=\"cursor: default; left: " + lcol_start[2] + "px; width: " + lcol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
    }
    out.print(getTime(rs.getInt("time")));
    out.print("</span>");

    
    //
    // Assigned Time
    //
    out.print(" <span id=lottery_slot_" + slotIndex + "_assignTime class=cellData style=\"cursor: default; left: " + lcol_start[3] + "px; width: " + lcol_width[3] + "px; background-color: " +dts_defaultF3Color+ "\">");
    switch (group) {
        case 1:
            out.print(getTime(rs.getInt("atime1")));
            break;
        case 2:
            out.print(getTime(rs.getInt("atime2")));
            break;
        case 3:
            out.print(getTime(rs.getInt("atime3")));
            break;
        case 4:
            out.print(getTime(rs.getInt("atime4")));
            break;
        case 5:
            out.print(getTime(rs.getInt("atime5")));
            break;
    }
    out.print("</span>");
    
    
    //
    // Acceptable Times
    //
    String ftime = "";      // first acceptable time
    String ltime = "";      // last acceptable time
    int before = rs.getInt("minsbefore");
    int after = rs.getInt("minsafter");

    if (before > 0) ftime = getTime(SystemUtils.getFirstTime(rs.getInt("time"), before));    // get earliest time for this request
    if (after > 0) ltime = getTime(SystemUtils.getLastTime(rs.getInt("time"), after));     // get latest time for this request
    
    out.print(" <span id=lottery_slot_" + slotIndex + "_oktimes class=cellData style=\"cursor: default; left: " + lcol_start[4] + "px; width: " + lcol_width[4] + "px; background-color: " +dts_defaultF3Color+ "\">");
    if (!ftime.equals("")) out.print(ftime + " - " + ltime);
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

    boolean tmp_found2 = false;
    String fullName = "";
    String cw = "";
    int sum_players = 0;
    int x2 = 0; // player data pos in rs

    for (int x = 0; x <= max_players - 1; x++) {

        x2 = x + ((group - 1) * max_players);
    
        fullName = rs.getString(x2 + 13);
        cw = rs.getString(x2 + 63);
        //nineHole = rs.getInt(x + 136);
        //eighteenHole = 1; //rs.getInt(x + );

        if (!fullName.equals("")) {
            if (tmp_found2) out.print(",&nbsp; ");// else out.print("&nbsp;");
            out.print(fullName); // + " <font style=\"font-size:8px\">(" + cw + ")</font>");
            tmp_found2 = true;
            sum_players++;
        }
    }
    
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
    int fives = 0;
    int count = 0;
    int group = 0;
    int lottery_id = 0;
    int teecurr_id = 0;
    boolean overRideFives = false;
    
    String sindex = req.getParameter("index");          //  day index value (needed by _sheet on return)
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
    String suppressEmails = "no";
    
    String lott_name = req.getParameter("lott_name");
    if (lott_name == null) lott_name = "";
    
    String slid = "";
    String stid = "";
    String sgroup = "";
    if (req.getParameter("to_tid") != null) stid = req.getParameter("to_tid");
    if (req.getParameter("lotteryId") != null) slid = req.getParameter("lotteryId");
    if (req.getParameter("group") != null) sgroup = req.getParameter("group");
    
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
    
    //
    //  Convert the values from string to int
    //
    try {
        
        lottery_id = Integer.parseInt(slid);
        teecurr_id = Integer.parseInt(stid);
        group = Integer.parseInt(sgroup);
        index = Integer.parseInt(sindex);
    }
    catch (NumberFormatException e) {
    }
    
    //
    // Get fives value for this course (from teecurr_id)
    //
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
    int open_slots = 0;
    boolean has_players = false;
    if (slotParms.player1.equals("")) { open_slots++; } else { has_players = true; }
    if (slotParms.player2.equals("")) open_slots++;
    if (slotParms.player3.equals("")) open_slots++;
    if (slotParms.player4.equals("")) open_slots++;
    if (slotParms.player5.equals("") && slotParms.rest5.equals("") && fives == 1) open_slots++;
    
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
    boolean allowFives = (fives == 1) ? true : false;
    int tmp_added = 0;
    int field_offset = (group - 1) * ((fives == 0) ? 4 : 5); // player data pos in rs
    
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
    
    
    String fields = "";
    
    switch (group) {
        
        case 1:
            fields = "player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "p1cw = ?, p2cw = ?, p3cw = ?, p4cw = ?, " +
                     "user1 = ?, user2 = ?, user3 = ?, user4 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, " +
                     "player5 = ?, p5cw = ?, user5 = ?, userg5 = ?, p95 = ?";
            break;
            
        case 2:
            fields = "player6 = ?, player7 = ?, player8 = ?, player9 = ?, " +
                     "p6cw = ?, p7cw = ?, p8cw = ?, p9cw = ?, " +
                     "user6 = ?, user7 = ?, user8 = ?, user9 = ?, " +
                     "userg6 = ?, userg7 = ?, userg8 = ?, userg9 = ?, " +
                     "p96 = ?, p97 = ?, p98 = ?, p99 = ?, " +
                     "player10 = ?, p10cw = ?, user10 = ?, userg10 = ?, p910 = ?";
            break;
            
        case 3:
            fields = "player1 AS player_1, player2 AS player_2, player3 AS player_3, player4 AS player_4, player5 AS player_5, " +
                     "p1cw AS cw_1, p2cw AS cw_2, p3cw AS cw_3, p4cw AS cw_4, p5cw AS cw_5, " +
                     "user1 AS user_1, user2 AS user_2, user3 AS user_3, user4 AS user_4, user5 AS user_5, " +
                     "userg1 AS user_g1, userg2 AS user_g2, userg3 AS user_g3, userg4 AS user_g4, userg5 AS user_g5, " +
                     "p91 AS p9_1, p92 AS p9_2, p93 AS p9_3, p94 AS p9_4, p95 AS p9_5";
            break;
            
        case 4:
            fields = "player1 AS player_1, player2 AS player_2, player3 AS player_3, player4 AS player_4, player5 AS player_5, " +
                     "p1cw AS cw_1, p2cw AS cw_2, p3cw AS cw_3, p4cw AS cw_4, p5cw AS cw_5, " +
                     "user1 AS user_1, user2 AS user_2, user3 AS user_3, user4 AS user_4, user5 AS user_5, " +
                     "userg1 AS user_g1, userg2 AS user_g2, userg3 AS user_g3, userg4 AS user_g4, userg5 AS user_g5, " +
                     "p91 AS p9_1, p92 AS p9_2, p93 AS p9_3, p94 AS p9_4, p95 AS p9_5";
            break;
            
        case 5:
            fields = "player1 AS player_1, player2 AS player_2, player3 AS player_3, player4 AS player_4, player5 AS player_5, " +
                     "p1cw AS cw_1, p2cw AS cw_2, p3cw AS cw_3, p4cw AS cw_4, p5cw AS cw_5, " +
                     "user1 AS user_1, user2 AS user_2, user3 AS user_3, user4 AS user_4, user5 AS user_5, " +
                     "userg1 AS user_g1, userg2 AS user_g2, userg3 AS user_g3, userg4 AS user_g4, userg5 AS user_g5, " +
                     "p91 AS p9_1, p92 AS p9_2, p93 AS p9_3, p94 AS p9_4, p95 AS p9_5";
            break;
        
    } // end switch
    
    fields = "*";
    out.println("<!-- group=" + group + " -->");
    out.println("<!-- field_offset=" + field_offset + " -->");
    
    //
    // Load lottery request data
    //
    try {
        
        PreparedStatement pstmt = con.prepareStatement ("SELECT " + fields + " FROM lreqs3 WHERE id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, lottery_id);
        
        rs = pstmt.executeQuery();
        
        if ( rs.next() ) {
            
            player1 = rs.getString(12 + field_offset);
            player2 = rs.getString(13 + field_offset);
            player3 = rs.getString(14 + field_offset);
            player4 = rs.getString(15 + field_offset);
            player5 = rs.getString(16 + field_offset);
            
            p1cw = rs.getString(62 + field_offset);
            p2cw = rs.getString(63 + field_offset);
            p3cw = rs.getString(64 + field_offset);
            p4cw = rs.getString(65 + field_offset);
            p5cw = rs.getString(66 + field_offset);
            
            user1 = rs.getString(37 + field_offset);
            user2 = rs.getString(38 + field_offset);
            user3 = rs.getString(39 + field_offset);
            user4 = rs.getString(40 + field_offset);
            user5 = rs.getString(41 + field_offset);
            
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
            teeTimeFull = addPlayer(slotParms, player1, user1, p1cw, p91, allowFives);
            if (!teeTimeFull) tmp_added++;
        }
        
        if (!player2.equals("")) {
            teeTimeFull = addPlayer(slotParms, player2, user2, p2cw, p92, allowFives);
            if (!teeTimeFull) tmp_added++;
        }
        
        if (!player3.equals("")) {
            teeTimeFull = addPlayer(slotParms, player3, user3, p3cw, p93, allowFives);
            if (!teeTimeFull) tmp_added++;
        }
        
        if (!player4.equals("")) {
            teeTimeFull = addPlayer(slotParms, player4, user4, p4cw, p94, allowFives);
            if (!teeTimeFull) tmp_added++;
        }
                
        if (!player5.equals("") && allowFives) {
            teeTimeFull = addPlayer(slotParms, player5, user5, p5cw, p95, allowFives);
            if (!teeTimeFull) tmp_added++;
        }
        
    } catch(Exception exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);
    }
    
    
    out.println("<!-- lottery_id="+lottery_id+" | teecurr_id="+teecurr_id+" | tmp_added="+tmp_added+" -->");
    
    
    
    // first lets see if they are trying to fill the 5th player slot when it is restricted
    if ( !slotParms.player5.equals("") && ((!slotParms.rest5.equals("") && overRideFives == false) || fives == 0)) {
    
        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted</H3><BR>");
        out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");
        out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
        out.println("<BR><BR>");
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");

        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dlott\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"lotteryId\" value=\"" + lottery_id + "\">");
        out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
        out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    
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
        if (group == 1) {
            
            // there was one group in the request - just delete the request
            try {
                
                PreparedStatement pstmt = con.prepareStatement("DELETE FROM lreqs3 WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, lottery_id);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
            }
            
        } else {
            
            // there 
            try {
                
                PreparedStatement pstmt = con.prepareStatement("UPDATE lreqs3 SET WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, lottery_id);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
            }
            
        }
        
    }
    
    out.println("<!-- count="+count+" -->");
    
    out.println("<!--");
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
    out.println("-->");
    
    //
    // Completed update - reload page
    //
    //out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dlott?index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&lott_name=" + lott_name + "\">");

    out.close();
    return;
 }
 
 
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
 
} // end servlet