/***************************************************************************************
 *   Proshop_diary: This servlet will ouput the course statistics report
 *
 *
 *   Called by:     called by main menu options
 *
 *
 *   Created:       3/23/2005 by Paul
 *
 *
 *   Last Updated:  4/12/2005
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
//import java.lang.Math;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
//import com.foretees.client.action.ActionHelper;


public class Proshop_report_course_rounds extends HttpServlet {
    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    //DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
 //****************************************************
 // Process the get method on this page as a post call
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                                              // call doPost processing

 } // end of doGet routine
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    
    PrintWriter out = resp.getWriter();                             // normal output stream
    
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    // set response content type
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        } else {
            resp.setContentType("text/html");
        }
    }
    catch (Exception exc) {
    }
    
    // handle excel output
    //try {
    //    if (excel.equals("yes")) resp.setContentType("application/vnd.ms-excel");
    //} catch (Exception e) { /* ignore failure */ }
    
    //HttpSession session = req.getSession(false);
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    
    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not establish connection.", "", out);
        return;
    }
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    int report_type = 0;
    
    // start ouput
    out.println(SystemUtils.HeadTitle("Proshop - Rounds Played Report"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    
    //if (req.getParameter("round") != null) { report_type = 0; }
    if (req.getParameter("today") != null) { report_type = 1; }
    if (req.getParameter("custom") != null) { report_type = 2; }
    
    if (req.getParameter("detail") != null) { report_type += 10; }
    
    switch(report_type) {
        
        case 1:
            doToday(req, out, con, session);                        // go process Number of Rounds Played Today report
            break;
        case 2:
            doCustomDate(req, out, con, session);                   // go process Number of Rounds Played Custom Date Range report
            break;
        case 10:
            //doRoundDetail(req, resp, out, session, con);                  // go process Number of Rounds Played Last Month, MTD, YTD report
            break;
        case 11:
            doTodayDetail(req, out, con, session);                        // go process Number of Rounds Played Today report
            break;
        case 12:
            //doCustomDateDetail(req, out, con, session);                   // go process Number of Rounds Played Custom Date Range report
            break;
        default:
            doRound(req, resp, out, session, con);                  // go process Number of Rounds Played Last Month, MTD, YTD report
        
    }
    
    out.println("</body></html>");
    out.close();
    
 } // end doPost
 
 
 //**************************************
 // Number of Rounds Played Today Report
 //**************************************
 //
 private void doToday(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {
   
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
   //  get the club name and lottery info
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

   boolean guest = false;
   boolean new_row = false;

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
   String [] course = new String [20];

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
         displayDatabaseErrMsg("Error loading course names.", "", out);
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
   
   // start the main tables that holds each course table
   out.println("<table cellspacing=10>");
   
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

         //
         // Loop thru rs of tee times for today from teecurr2
         //
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
         
         // display fatal database error message
         displayDatabaseErrMsg(exc.getMessage(), error, out);
         return;
         
      }

            //
            // start report html output
            //
            
            new_row = (new_row == false);
            
            out.println((new_row == true) ? "</td></tr><tr><td>" : "</td><td>");
            
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

               //
               // add course name header if multi
               //
               if (!courseName.equals( "" )) {

                  out.println("<tr bgcolor=\"#336633\"><td colspan=\"2\">");
                  out.println("<font color=\"#FFFFFF\" size=\"3\">");
                  out.println("<p align=\"center\"><b>" + courseName + " Summary</b></p>");
                  out.println("</font></td></tr>");
               }

               //
               //  Header row
               //
               out.println("<tr bgcolor=\"#336633\">");
                  out.println("<td>");
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
               out.println("<td>&nbsp;</td><td>&nbsp;</td>");

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
               out.println("<td>&nbsp;</td><td>&nbsp;</td>");

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
               out.println("<td>&nbsp;</td><td>&nbsp;</td>");

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
                  out.println("<td>&nbsp;</td><td>&nbsp;</td>");
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
               out.println("<td>&nbsp;</td><td>&nbsp;</td>");

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

   out.println("</table>");
   
   out.println("</td></tr></table>");                // end of main page table & column

   out.println("<table align=center cellspacing=7><tr><td>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"  Home  \" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");
   out.println("</td><td>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
   out.println("<input type=\"hidden\" name=\"today\" value=\"yes\">");
   out.println("<input type=\"hidden\" name=\"detail\" value=\"yes\">");
   out.println("<input type=\"submit\" value=\" Detail \" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></td></tr></table></font>");
   
   //
   //  End of report
   //
   out.println("</center></font>");

 }  // end of doToday
  
 
 //**************************************************
 // Number of Rounds Played Custom Date Range Report
 //**************************************************
 //
 private void doCustomDate(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {
    
    // 
    //  Declare our local variables
    //
    int start_year;
    int start_month;
    int start_day;
    int end_year;
    int end_month;
    int end_day;
    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    
    // check to see if the date is here, and if not then jump to display calendar routine
    if (start_date.equals("") || end_date.equals("")) {
        getCustomDate(req, out, con); 
        return;
    }
    
    // make sure the dates here are valid, if not redisplay the calendars
    try {
    
        int dash1 = start_date.indexOf("-");
        int dash2 = start_date.indexOf("-", dash1 + 1);
        start_year = Integer.parseInt(start_date.substring(0, 4));
        start_month = Integer.parseInt(start_date.substring(dash1 + 1, dash2));
        start_day = Integer.parseInt(start_date.substring(dash2 + 1));

        dash1 = end_date.indexOf("-");
        dash2 = end_date.indexOf("-", dash1 + 1);
        end_year = Integer.parseInt(end_date.substring(0, 4));
        end_month = Integer.parseInt(end_date.substring(dash1 + 1, dash2));
        end_day = Integer.parseInt(end_date.substring(dash2 + 1));
    
    } catch (Exception e) {
        // invalid dates here, bailout and call form again
        getCustomDate(req, out, con);
        return;
    }
    
    //
    // IF WE ARE STILL HERE THEN WE HAVE A VALID DATE RANGE SUPPLIED BY THE USER
    //
        
    
    // 
    //  Declare more local variables
    //
    Statement stmt = null;
    //PreparedStatement pstmtc = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    Calendar cal1 = new GregorianCalendar(start_year, start_month - 1, start_day);
    Calendar cal2 = new GregorianCalendar(end_year, end_month - 1, end_day);
    
    
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block
   
   String club = (String)sess.getAttribute("club");      // get club name
   
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

   sdate = start_year * 10000;                    // create a date field of yyyymmdd
   sdate = sdate + (start_month * 100);
   sdate = sdate + start_day;
   
   edate = end_year * 10000;                      // create a date field of yyyymmdd
   edate = edate + end_month * 100;
   edate = edate + end_day;
   
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
         displayDatabaseErrMsg("Error loading course names.", exc.getMessage(), out);
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
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

         //out.println(sdate+"->"+edate);
         
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

         displayDatabaseErrMsg("Error loading data for report.", exc.getMessage(), out);
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
            out.println("<font color=\"#FFFFFF\" face=\"verdana\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
            out.println("</font></td></tr>");
         }

         out.println("<tr bgcolor=\"#336633\"><td>");
               out.println("<font face=\"verdana\" color=\"#FFFFFF\" size=\"2\">");
               out.println("<p align=\"left\"><b>Stat</b></p>");
               out.println("</font></td>");

            out.println("<td>");
               out.println("<font size=\"2\" color=\"white\" face=\"verdana\">");
               out.println("<p align=\"center\"><b>From " + start_month + "/" + start_day + "/" + start_year + " to");
               out.println(" " + end_month + "/" + end_day + "/" + end_year + "</b></p>");
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
         /*
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

         */
         
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
         /*
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
         */
         
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

/*
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
         */
         out.println("<tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("</tr><!--<tr>");
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<b>Number of Member No-Shows:</b>");
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(nshowRounds1);
            out.println("<br></font></td>");

      out.println("</font></tr>--></table><br>");

      count2--;                         // decrement number of courses
      index++;
      courseName = course[index];      // get next course name, if more

   }       // end of while Courses - do all courses

   out.println("</td></tr></table>");                // end of main page table & column

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   
 } // end of doCustomDate
 
 
 //**************************************************
 // Get Date Range for Number of Rounds Played Report
 //**************************************************
 //
 private void getCustomDate(HttpServletRequest req, PrintWriter out, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    String fname = "";
    String lname = "";
    String mname = "";
    String user = "";

    // our oldest date variables (how far back calendars go)
    int oldest_mm = 0;
    int oldest_dd = 0;
    int oldest_yy = 0;
    
    // lookup oldest date in teepast2
    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT mm,dd,yy FROM teepast2 ORDER BY date ASC LIMIT 1");

        while (rs.next()) {
            oldest_mm = rs.getInt(1);
            oldest_dd = rs.getInt(2);
            oldest_yy = rs.getInt(3);
        }
        
    } catch (Exception e) {
        displayDatabaseErrMsg("Error looking up oldest teetime.", e.getMessage(), out);
        return;
    }
    
    // set calendar vars
    Calendar cal_date = new GregorianCalendar();
    int cal_year = cal_date.get(Calendar.YEAR);
    int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
    
    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

    //out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

    // start main table for this page
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td>");
    
    // output instructions
    out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font face=\"verdana\" color=\"#FFFFFF\" size=\"2\">");
    out.println("<font size=\"3\"><b>Number of Rounds Played Report</b></font><br>");
    out.println("<br>Select the date range below.<br>");
    out.println("<b>Note:</b>  Only rounds before today will be included in the counts.<br><br>");
    out.println("Click on <b>Go</b> to generate the report.</font></td></tr>");
    out.println("</table><br>");

    // start date submission form
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" method=\"post\">");
     out.println("<input type=hidden name=custom value=1>");

    // output table that hold calendars and their related text boxes
    out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
     out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_0 id=cal_box_0>");
     out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
     out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_1 id=cal_box_1>");
    out.println("</td>\n</tr></table>\n");   

    // report button (go)
    out.println("<p align=\"center\"><input type=\"submit\" value=\"  Go  \"></p>");
    
    // end date submission form
    out.println("</form>");
    
    // output back button form
    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
     out.println("<p align=\"center\"><input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\"></p>");
    out.println("</form>");
    
    // end of main page table
    out.println("</td></tr></table>");
    
    // start calendar javascript setup code
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

    
     // set calendar date parts in js
     out.println("g_cal_month[0] = " + cal_month + ";");
     out.println("g_cal_year[0] = " + cal_year + ";");
     out.println("g_cal_beginning_month[0] = " + oldest_mm + ";");
     out.println("g_cal_beginning_year[0] = " + oldest_yy + ";");
     out.println("g_cal_beginning_day[0] = " + oldest_dd + ";");
     out.println("g_cal_ending_month[0] = " + cal_month + ";");
     out.println("g_cal_ending_day[0] = " + cal_day + ";");
     out.println("g_cal_ending_year[0] = " + cal_year + ";"); 
    
     out.println("g_cal_month[1] = " + cal_month + ";");
     out.println("g_cal_year[1] = " + cal_year + ";");
     out.println("g_cal_beginning_month[1] = " + oldest_mm + ";");
     out.println("g_cal_beginning_year[1] = " + oldest_yy + ";");
     out.println("g_cal_beginning_day[1] = " + oldest_dd + ";");
     out.println("g_cal_ending_month[1] = " + cal_month + ";");
     out.println("g_cal_ending_day[1] = " + cal_day + ";");
     out.println("g_cal_ending_year[1] = " + cal_year + ";");

     out.println("function sd(pCal, pMonth, pDay, pYear) {");
     out.println(" f = document.getElementById(\"cal_box_\"+pCal);");
     out.println(" f.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
     out.println("}");
   
    out.println("</script>");

    out.println("<script type=\"text/javascript\">\n doCalendar('0');\n doCalendar('1');\n</script>");

    //out.println("</center></font>");
 }
 
 
 //*****************************************************
 // Number of Rounds Played Last Month, MTD, YTD Report
 //*****************************************************
 //
 private void doRound(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession sess, Connection con) {
     // HttpServletRequest req, PrintWriter out, Connection con
     // HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession sess, Connection con
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

            
            
            
            
            
            
            
            
            
            
         
            
            
         int tot9Rounds1;
         int tot9Rounds2;
         int tot9Rounds4;
         int tot18Rounds1;
         int tot18Rounds2;
         int tot18Rounds4;
         tot9Rounds1 = mem9Rounds1 + gst9Rounds1;
         tot9Rounds2 = mem9Rounds2 + gst9Rounds2;
         tot9Rounds4 = mem9Rounds4 + gst9Rounds4;
         tot18Rounds1 = mem18Rounds1 + gst18Rounds1;
         tot18Rounds2 = mem18Rounds2 + gst18Rounds2;
         tot18Rounds4 = mem18Rounds4 + gst18Rounds4;
         

         out.println("</tr><tr>");                     // Total 9 Hole Rounds
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Total 9 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (tot9Rounds4 < 1 || totRounds4 < 1) {
            out.println(tot9Rounds4);
         } else {
            out.println(tot9Rounds4 + " (" + (tot9Rounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (tot9Rounds2 < 1 || totRounds2 < 1) {
            out.println(tot9Rounds2);
         } else {
            out.println(tot9Rounds2 + " (" + (tot9Rounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (tot9Rounds1 < 1 || totRounds1 < 1) {
            out.println(tot9Rounds1);
         } else {
            out.println(tot9Rounds1 + " (" + (tot9Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // Total 18 Hole Rounds
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Total 18 Hole Rounds:");
            out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (tot18Rounds4 < 1 || totRounds4 < 1) {
            out.println(tot18Rounds4);
         } else {
            out.println(tot18Rounds4 + " (" + (tot18Rounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (tot18Rounds2 < 1 || totRounds2 < 1) {
            out.println(tot18Rounds2);
         } else {
            out.println(tot18Rounds2 + " (" + (tot18Rounds2 * 100)/totRounds2 + "%)");
         }
         out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (tot18Rounds1 < 1 || totRounds1 < 1) {
            out.println(tot18Rounds1);
         } else {
            out.println(tot18Rounds1 + " (" + (tot18Rounds1 * 100)/totRounds1 + "%)");
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
            
            
            
            
            
         found = false;                              // init flag
         
         /*
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
         } // end of member type loop
         


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


          */
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

         /*
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
         */

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
         //out.println("</tr>");

         //
         //  Check all the Transportation Modes - now 16 configurable modes (V4)
         //
         /*
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

         */
         
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
     
 } // end of doRound
 
 
 private void doTodayDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {


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
   //HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

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
   boolean new_row = false;

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

   // new vars for expanded detail report
   int tot9Rounds = 0;
   int tot18Rounds = 0;
   
   String [] mem_type9 = new String [20];
   String [] mem_type18 = new String [20];
   String [] memship_type9 = new String [20];
   String [] memship_type18 = new String [20];
   
   
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
         displayDatabaseErrMsg("Can not establish connection.", "", out);
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
   
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

   // start the main tables that holds each course table
   out.println("<table cellspacing=10>");
   
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
         displayDatabaseErrMsg(exc.getMessage(), error, out);
         return;
      }

            new_row = (new_row == false);
            out.println((new_row == true) ? "</td></tr><tr><td>" : "</td><td>");
            
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

               //
               // add course name header if multi
               //
               if (!courseName.equals( "" )) {

                  out.println("<tr bgcolor=\"#336633\"><td colspan=\"2\">");
                  out.println("<font color=\"#FFFFFF\" size=\"3\">");
                  out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
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
   
   out.println("</table>");

   out.println("</td></tr></table>");                // end of main page table & column

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   
 }  // end of doTodayDetail
 
 
 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }
} // end servlet
 