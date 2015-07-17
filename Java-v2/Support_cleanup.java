/***************************************************************************************     
 *   Support_cleanup:  This servlet will clean up the teecurr db table.  It will prompt
 *                     the user for the date and course name, and then list the tee times.
 *                     The user can then select which times to delete.
 *
 *     
 *     For use when duplicate times exist.
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;



public class Support_cleanup extends HttpServlet {
                           
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                 // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
     
   HttpSession session = null; 

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = (String)session.getAttribute("club");   // get club name

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      if (user.startsWith( "sales" )) {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

    // Define parms
   String courseName = "";        // course names
   int multi = 0;               // multiple course support
   int index= 0;
   long date = 0;

   //
   //  Array to hold the course names
   //
   String [] course = new String [20];                     // max of 20 courses per club

   //
   // Get info from the club db
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);

      } else {
        // Parms do not exist yet
         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>The Club Setup has not been completed.");
         out.println("<BR>Please go to 'System Config' and select 'Club Setup'.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         if (user.startsWith( "sales" )) {
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
         } else {
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_main\">Home</a>");
         }
         out.println("</CENTER></BODY></HTML>");
         return;
      }
      stmt.close();

      if (multi != 0) {           // if multiple courses supported for this club

         while (index < 20) {

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

            course[index] = courseName;      // add course name to array (up to 20)
            index++;
         }
         stmt.close();
      }

      //
      //  Build the HTML page to prompt user for a specific date
      //
      out.println(SystemUtils.HeadTitle2("Support Select Course Page"));
      // include files for dynamic calendars
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
      out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script></head>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");

      out.println("<br><p align=\"center\"><font size=\"4\">");
      out.println("Tee Sheet Cleanup</p>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"560\">");
      out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<p align=\"center\">Select the date and course you wish to process and then click on 'Continue' below.");
      out.println("</td></tr></font></table>");
      out.println("<font size=\"2\"><br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<br><br>");

      // this is the form that gets submitted when the user selects a day from the calendar
      out.println("<form action=\"Support_cleanup\" method=\"post\" name=\"frmLoadDay\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");

      if (multi != 0) {           // if multiple courses supported for this club

         index = 0;
         courseName = course[index];      // get first course name from array

         out.println("<b>Course:</b>&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"course\">");
         out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");

         index++;
         courseName = course[index];      // get course name from array

         while ((!courseName.equals( "" )) && (index < 20)) {

            out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            index++;
            courseName = course[index];      // get course name from array
         }
         out.println("</select>");
         out.println("<br><br>");
        
      } else {
         
         out.println("<input type=\"hidden\" name=\"course\" value=\"\">");   // no course names
      }

      //
      //  Display calendar
      //
      out.println("</form>");

      out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!
      out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");
      out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");
      out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");
      out.println("</td>\n<tr>\n</table>");

      Calendar cal_date = new GregorianCalendar();
      int cal_year = cal_date.get(Calendar.YEAR);
      int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
      int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

      out.println("<script type=\"text/javascript\">");

       out.println("var g_cal_bg_color = '#F5F5DC';");
       out.println("var g_cal_header_color = '#8B8970';");
       out.println("var g_cal_border_color = '#8B8970';");

       out.println("var g_cal_count = 2;"); // number of calendars on this page
       out.println("var g_cal_year = new Array(g_cal_count - 1);");
       out.println("var g_cal_month = new Array(g_cal_count - 1);");
       out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
       out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
       out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
       out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
       out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
       out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

       // set calendar date parts
       out.println("g_cal_month[0] = " + cal_month + ";");
       out.println("g_cal_year[0] = " + cal_year + ";");
       out.println("g_cal_beginning_month[0] = " + cal_month + ";");
       out.println("g_cal_beginning_year[0] = " + cal_year + ";");
       out.println("g_cal_beginning_day[0] = " + cal_day + ";");
       out.println("g_cal_ending_month[0] = " + cal_month + ";");
       out.println("g_cal_ending_day[0] = 31;");
       out.println("g_cal_ending_year[0] = " + cal_year + ";");

       cal_date.add(Calendar.MONTH, 1); // add a month
       cal_year = cal_date.get(Calendar.YEAR);
       cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

       out.println("g_cal_month[1] = " + cal_month + ";");
       out.println("g_cal_year[1] = " + cal_year + ";");
       out.println("g_cal_beginning_month[1] = " + cal_month + ";");
       out.println("g_cal_beginning_year[1] = " + cal_year + ";");
       out.println("g_cal_beginning_day[1] = 0;");

       cal_date.add(Calendar.MONTH, -1); // subtract a month
       cal_date.add(Calendar.YEAR, 1); // add a year
       cal_year = cal_date.get(Calendar.YEAR);
       cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
       out.println("g_cal_ending_month[1] = " + cal_month + ";");
       out.println("g_cal_ending_day[1] = " + cal_day + ";");
       out.println("g_cal_ending_year[1] = " + cal_year + ";");

      out.println("</script>");

      out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>");
      out.println("<script language=\"javascript\">\ndoCalendar('1');\n</script>");


      out.println("<br><br>");
      if (user.startsWith( "sales" )) {
         out.println("<form method=\"get\" action=\"/" +rev+ "/sales_main.htm\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
      } else {
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_main\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
      }

      //
      //  End of HTML page
      //
      out.println("</td></tr>");
      out.println("</table>");
      out.println("</center></font></body></html>");

      return;                        // exit and wait for 3rd call

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      if (user.startsWith( "sales" )) {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_main\">Home</a>");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }
 }
   

 //
 //  doPost processing - process call from above and from self
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = (String)session.getAttribute("club");   // get club name

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      if (user.startsWith( "sales" )) {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  If 2nd call then go process the clean up request
   //
   if (req.getParameter("step2") != null) {   

      cleanUp(req, out, con);
      return;
   }

   //
   //  1st call - output the tee sheet
   //
   String num = "";
   String calDate = "";
   String course = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String ecolor = "";
   String rcolor = "";
   String lcolor = "";
   String bgcolor = "";
   String blocker = "";
   String ampm = "";
   String sfb = "";
     
   int day_num = 0;
   int ind = 0;
   int teecurr_id = 0;
   int time = 0;
   int fb = 0;
   int hr = 0;
   int min = 0;
   int count = 0;
     
   long year = 0;
   long month = 0;
   long day = 0;
   long date = 0;


   calDate = req.getParameter("calDate");       //  get the date requested (mm/dd/yyyy)

   course = req.getParameter("course");         //  get the course

   //
   //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
   //
   StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

   num = tok.nextToken();                    // get the mm value
   month = Long.parseLong(num);

   num = tok.nextToken();                    // get the dd value
   day = Long.parseLong(num);

   num = tok.nextToken();                    // get the yyyy value
   year = Long.parseLong(num);

   date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd

   try {
     
      //
      //  Get all the lottery requests for the selected date and course
      //
      pstmt = con.prepareStatement (
         "SELECT teecurr_id, time, event_color, rest_color, " +
         "player1, player2, player3, player4, " +
         "fb, blocker, lottery_color " +
         "FROM teecurr2 " +
         "WHERE date = ? AND courseName = ? " +
         "ORDER BY time, fb");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Support Cleanup Tee Sheet Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<font size=\"3\">");
      out.println("<b>Cleanup Tee Sheet</b><br><br>");
      out.println("</font>");

      out.println("<table border=\"0\" align=\"center\" bgcolor=\"#336633\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("Select the tee times to delete and then click on 'Submit' below.");
         out.println("</font>");
      out.println("</td></tr></table>");

      out.println("<font size=\"2\">");

      if (course.equals( "" )) {
         out.println("<p align=\"center\">Tee Sheet For:&nbsp;&nbsp;<b>" + month + "/" + day + "/" + year + "</b></p>");
      } else {
         out.println("<p align=\"center\">Tee Sheet For:&nbsp;&nbsp;<b>" + month + "/" + day + "/" + year + "</b>");
         out.println("  on Course: <b>" + course + "</b></p>");
      }

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
      out.println("<tr bgcolor=\"#336633\"><td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><u><b>Id</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><u><b>Time F/B</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><u><b>Delete?</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
         out.println("</font></td>");

      out.println("<td width=\"80\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\"><u><b>Blocker</b></u></p>");
         out.println("</font></td>");

      out.println("</tr>");
      out.println("<form method=\"post\" action=\"Support_cleanup\">");
      out.println("<input type=\"hidden\" name=\"step2\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");

      //
      //  Get each record and display it
      //
      count = 0;             // number of records found

      while (rs.next()) {

         count++;                        // also used as index value for parms

         teecurr_id = rs.getInt(1);
         time = rs.getInt(2);
         ecolor = rs.getString(3);
         rcolor = rs.getString(4);
         player1 = rs.getString(5);
         player2 = rs.getString(6);
         player3 = rs.getString(7);
         player4 = rs.getString(8);
         fb = rs.getInt(9);
         blocker = rs.getString(10);
         lcolor = rs.getString(11);

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

         hr = time / 100;
         min = time - (hr * 100);

         ampm = " AM";
         if (hr == 12) {
            ampm = " PM";
         }
         if (hr > 12) {
            ampm = " PM";
            hr = hr - 12;    // convert to conventional time
         }

         //
         //  Process the F/B parm    0 = Front 9, 1 = Back 9
         //
         sfb = "  F";       // default Other

         if (fb == 1) {

            sfb = "  B";
         }

         bgcolor = "#F5F5DC";       // default color
           
         if (!ecolor.equals( "" )) {
           
            bgcolor = ecolor;
              
         } else {

            if (!rcolor.equals( "" )) {

               bgcolor = rcolor;

            } else {

               if (!lcolor.equals( "" )) {

                  bgcolor = lcolor;
               }
            }
         }

         //
         //  Build the HTML for each record found
         //
         out.println("<tr bgcolor=\"" +bgcolor+ "\">");
         out.println("<td align=\"center\">");                  // acceptable time range
            out.println("<font size=\"2\">");
            out.println(teecurr_id);
            out.println("</font></td>");

         out.println("<td align=\"center\">");                  // requested time/tee
            out.println("<font size=\"2\">");
         if (min < 10) {
            out.println(hr + ":0" + min + ampm + sfb);
         } else {
            out.println(hr + ":" + min + ampm + sfb);
         }
         out.println("</font></td>");

         out.println("<td align=\"center\">");      // check box to delete
         out.println("<font size=\"2\">");
         out.println("<input type=\"checkbox\" name=\"delete" + count + "\" value=\"1\">");
         out.println("</font></td>");

         out.println("<td align=\"center\">");        // players
         out.println("<font size=\"2\">");
         out.println( player1 );
         out.println("</font></td>");

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println( player2 );
         out.println("</font></td>");

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println( player3 );
         out.println("</font></td>");

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println( player4 );
         out.println("</font></td>");

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println( blocker );
         out.println("</font></td>");

         out.println("<input type=\"hidden\" name=\"tid" + count + "\" value=\"" +teecurr_id+ "\">");

         out.println("</tr>");

      }    // end of while

      pstmt.close();

      out.println("</font></table>");

      out.println("<input type=\"hidden\" name=\"count\" value=\"" + count + "\">");

      if (count == 0) {

         out.println("<font size=\"2\">");
         out.println("<p align=\"center\">No tee times found for the date selected.</p>");
         out.println("</font>");
      }

      out.println("</td></tr></table>");                // end of main page table
      out.println("<br></font>");

      out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td>");
         out.println("<font size=\"2\">");
         out.println("Are you sure?&nbsp;&nbsp;");
         out.println("<input type=\"submit\" value=\"Submit\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</font>");
         out.println("</td><td>");
         out.println("<font size=\"4\">");        // add a space between the buttons
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>");
         out.println("<form method=\"get\" action=\"Support_cleanup\">");
         out.println("</td><td>");
         out.println("<font size=\"2\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</font>");
      out.println("</td></tr></table>");

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
      out.println("<br><br><a href=\"Support_cleanup\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
   }     // end of search function

 }   // end of doPost   
                                 

 // ***************************************************************************
 //  Process the Delete Request from doPost
 // ***************************************************************************

 private void cleanUp(HttpServletRequest req, PrintWriter out, Connection con) {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;


   String temp = "";
   long date = 0;

   int i = 0;
   int i2 = 0;
   int count = 0;
   int tid = 0;
   int delete = 0;

   //
   //  Arrays to hold the requests (max = 300)
   //
   int [] tidA = new int [300];         // request id (teecurr_id)
   int [] deleteA = new int [300];        // delete indicator

   //
   //  get parms that were passed
   //
   String course = req.getParameter("course");
   String sdate = req.getParameter("date");      // date of these requests
   String scount = req.getParameter("count");      // get number of entries to process

   try {
      count = Integer.parseInt(scount);
      date = Long.parseLong(sdate);
   }
   catch (NumberFormatException e) {
   }

   //
   //  Get all the parms that were passed - 'count' determines the number of requests that are included
   //
   String si = "";
   String parm = "";
   String parm1 = "delete";      // set parm names
   String parm2 = "tid";
   i = 1;                        // init index
   i2 = 0;

   loop1:
   while (count > 0 && i < 300) {

      si = String.valueOf( i );         // create string index
      parm = parm1 + si;                // create parm name - delete_

      if (req.getParameter( parm ) != null) {

         //
         //  Process delete option
         //
         temp = req.getParameter( parm );       // get the delete parm

         delete = Integer.parseInt(temp);     // get the option

         if (delete > 0) {                       // only process those selected

            deleteA[i2] = delete;                 // put in array

            //
            //  Process teecurr id
            //
            parm = parm2 + si;                       // create parm name - tid_

            if (req.getParameter( parm ) != null) {  // if it was included

               temp = req.getParameter( parm );       // get the id

               tidA[i2] = Integer.parseInt(temp);   // save matching tid
            }
              
            i2++;       // bump array index
         }
      }
      i++;                // bump index
      count--;
   }

   //
   //  Now process all delete requests
   //
   i = 0;                 // init index
   i2 = 0;

   loop2:
   while (i < 300) {

      tid = tidA[i];       // get teecurr id
        
      if (tid == 0) { 
        
         break loop2;     // done
      }           
 
      delete = deleteA[i];    // get delete option
        
      if (delete > 0) {       // if selected then delete this tee time
        
         try {
            //
            //  delete the tee time
            //
            pstmt = con.prepareStatement (
                "DELETE FROM teecurr2 " +
                "WHERE teecurr_id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, tid);
            pstmt.executeUpdate();

            pstmt.close();

            i2++;                  // count number of req's updated

         }
         catch (Exception exc) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER><BR>");
            out.println("<BR><BR><H3>Database Access Error</H3>");
            out.println("<BR><BR>Error in cleanUp deleting the tee times.");
            out.println("<BR>Error:" + exc.getMessage());
            out.println("<BR><BR>Please try again later.");
            out.println("<br><br><a href=\"Support_cleanup\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }
      i++;
        
   }        // end of loop2 WHILE

   out.println(SystemUtils.HeadTitle("Support Cleanup Done"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Tee Times Delete</H3>");
   out.println("<BR><BR>The selected tee times have been successfully deleted.");
   out.println("<BR><BR>There were " +i2+ " tee times deleted.");
   out.println("<br><br><a href=\"Support_cleanup\">Return</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }        // end of cleanUp


 //************************************************************************
 //  logError - logs error messages to a text file in the club's folder
 //************************************************************************

 private void logError(String msg, String club) {

   String space = "  ";
   int fail = 0;

   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" +club+ "//error.txt", true));

      //
      //  Put header line in text file
      //
      fout1.print(new java.util.Date() + space + msg);
      fout1.println();      // output the line

      fout1.close();

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      try {
         //
         //  dir path for test pc
         //
         PrintWriter fout = new PrintWriter(new FileWriter("c:\\java\\tomcat\\webapps\\" +club+ "\\error.txt", true));

         //
         //  Put header line in text file
         //
         fout.print(new java.util.Date() + space + msg);
         fout.println();      // output the line

         fout.close();
      }
      catch (Exception ignore) {
      }
   }
 }  // end of logError


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR><BR>" + e.getMessage());
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

}
