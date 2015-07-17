/***************************************************************************************
 *   Proshop_report_rounds_by_month: This servlet will generate a report showing the number
 *                                     of rounds per day for the month and year selected.
 *
 *   Called by:     menu
 *
 * 
 *
 *   Created:       5/07/2009 by Bob
 *
 *
 *   Last Updated:  
 * 
 *        9/13/12   Custom for Sierra View to show rounds by mship type (removed - they changed their minds).
 *        5/25/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
//import java.lang.Math;
//import java.text.SimpleDateFormat;

// foretees imports
import com.foretees.common.getClub;
import com.foretees.common.parmClub;
import com.foretees.common.Utilities;


public class Proshop_report_rounds_by_month extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
 //*****************************************************
 //  doGet - process as doPost
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                                              // call doPost processing

 } // end of doGet routine

//
//***************************************************************************
//  doPost - prompt user for the 2 years to compare, then proces the report
//***************************************************************************
//
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");                               
    
    PrintWriter out = resp.getWriter();                             // normal output stream
    
    boolean excel = false;
    
    if (req.getParameter("excel") != null) {
       
       excel = true;
    }
    
    // handle excel output
    try{
        if (excel == true) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        }
    }
    catch (Exception exc) {
    }
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    
    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not establish connection.", "", out);
        return;
    }
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
        SystemUtils.restrictProshop("REPORTS", out);
        return;
    }
    
    String club = (String)session.getAttribute("club");    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
       
    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con); // golf only report

    //
    //   Get guest types
    //
    try {
       getClub.getParms(con, parm);
    }
    catch (Exception e1) {
    }

    
    String period1 = (req.getParameter("month") != null) ? req.getParameter("month")  : "";
      
    String period2 = (req.getParameter("year") != null) ? req.getParameter("year")  : "";
      
    String course = (req.getParameter("course") != null) ? req.getParameter("course")  : "";
      
    int month = 0;
    int year = 0;
    
    //
    //  User selected the years and format - process report
    //
    if (!period1.equals("")) {

       month = Integer.parseInt(period1);
    }

    if (!period2.equals("")) {

       year = Integer.parseInt(period2);
    }
  
    // 
    //  First time here - display warning message to inform user that this report can take several minutes!!!!
    //
    if (req.getParameter("continue") == null) {

       displayPrompt(month, year, out, parm, con);                  // prompt user for yearss to report
       return;                                   // exit and wait for continue  
    }
    

    //
    //  User already prompted for Years - now build the report
    //
    int totalmem = 0;
    int totalgst = 0;
    int totalrevgst = 0;
    int totalother = 0;
    int i = 0;
    int dd = 0;
    int mm = 0;
    int show1 = 0;
    int show2 = 0;
    int show3 = 0;
    int show4 = 0;
    int show5 = 0;
    int grev1 = 0;
    int grev2 = 0;
    int grev3 = 0;
    int grev4 = 0;
    int grev5 = 0;
    int max = 0;
    int count = 0;
    
    if (club.equals("xyzcc")) count = 32;     // use mship arrays for this club (was sierraviewcc)
        
    int [] memA = new int [32];            //  member rounds (1 per day - start with 1)
    int [] gstrevA = new int [32];         //  revenue gst rounds 
    int [] gstnorevA = new int [32];       //  non revenue gst rounds
    int [] otherA = new int [32];          //  other rounds

    int [] mship1A = new int [count];      //  mship type counts (custom)      
    int [] mship2A = new int [count];        
    int [] mship3A = new int [count];        
    int [] mship4A = new int [count];        
    int [] mship5A = new int [count];   
    
    int totalMship1 = 0;         // totals for mship types we are tracking (custom)
    int totalMship2 = 0;
    int totalMship3 = 0;
    int totalMship4 = 0;
    int totalMship5 = 0;
    
    
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
    String gtype1 = "";
    String gtype2 = "";
    String gtype3 = "";
    String gtype4 = "";
    String gtype5 = "";
    String mship1 = "";
    String mship2 = "";
    String mship3 = "";
    String mship4 = "";
    String mship5 = "";
    
   
    String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

    String months = mm_table[month];    // get the month selected

    //
    //  Declare our local variables
    //
    PreparedStatement pstmtc2 = null;
    ResultSet rs2 = null;
    
    
    
    // start page output
    startPageOutput(excel, out);
    
    if (excel == false) {                // if not Excel Spreadsheet Format

       SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    }
    
         
    // output report title
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><br><b>Daily Rounds By Month Report - For " +months+ " " +year+ " ");

    if (course.equals( "-ALL-" )) {
       
       out.println("For All Courses");
       
    } else {
       
       if (!course.equals( "" )) {
        
          out.println("For Course: " +course);
       }
    }
          
    out.println("</b><BR><BR>");
    out.println("<font size=2>");
    out.println("<b>Note: </b> Rounds represent both 18-hole rounds and 9-hole rounds.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;To see a breakdown, please use the Rounds Played Reports.<BR><BR>");

    try { 
       
       // 
       //  Determine if course was specified and select query accordingly
       //
       String sqlString = "";
       
       if (course.equals( "" ) || course.equals( "-ALL-" )) {
          
          sqlString = "SELECT dd, player1, player2, player3, player4, player5, username1, username2, username3, username4, username5, " +
                  "show1, show2, show3, show4, show5, gtype1, gtype2, gtype3, gtype4, gtype5, grev1, grev2, grev3, grev4, grev5, " +
                  "mship1, mship2, mship3, mship4, mship5 " +
                  "FROM teepast2 WHERE yy = ? AND mm = ? ORDER BY dd";
          
       } else {
          
          sqlString = "SELECT dd, player1, player2, player3, player4, player5, username1, username2, username3, username4, username5, " +
                  "show1, show2, show3, show4, show5, gtype1, gtype2, gtype3, gtype4, gtype5, grev1, grev2, grev3, grev4, grev5, " +
                  "mship1, mship2, mship3, mship4, mship5 " +
                  "FROM teepast2 WHERE yy = ? AND mm = ? AND courseName = ? ORDER BY dd";
       }
       

       pstmtc2 = con.prepareStatement (sqlString);

       
       //
       // start table output for year 1
       //
       out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" border=\"1\">");
       out.println("<tr align=\"center\" bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.7em\">" +
                   "<td><b>Day</b></td>" +
                   "<td><b>Member Rounds</b></td>");
               
       if (club.equals("xyzcc")) {
           
            out.println("<td><b>Social</b></td>" +
                        "<td><b>T Couple</b></td>" +
                        "<td><b>T Family</b></td>" +
                        "<td><b>T Jr</b></td>" +
                        "<td><b>T Single</b></td>");           
       }        
               
       out.println("<td><b>Guest Rounds (Rev)</b></td>" +
                   "<td><b>Guest Rounds (non-Rev)</b></td>" +
                   "<td><b>Other Rounds (Unknown)</b></td>" +
                   "<td><b>Total</b></td>");
       out.println("</tr>");

       //
       //  gather stats for this month
       //
       pstmtc2.clearParameters();

       pstmtc2.setInt(1, year);
       pstmtc2.setInt(2, month);

       if (!course.equals( "" ) && !course.equals( "-ALL-" )) {

          pstmtc2.setString(3, course);
       }

       rs2 = pstmtc2.executeQuery();      // execute the prepared stmt

       while (rs2.next()) {               // get all tee times for this month 

          dd = rs2.getInt("dd");     
          player1 = rs2.getString("player1");     
          player2 = rs2.getString("player2");     
          player3 = rs2.getString("player3");     
          player4 = rs2.getString("player4");     
          player5 = rs2.getString("player5");     
          user1 = rs2.getString("username1");     
          user2 = rs2.getString("username2");     
          user3 = rs2.getString("username3");     
          user4 = rs2.getString("username4");     
          user5 = rs2.getString("username5");     
          show1 = rs2.getInt("show1");     
          show2 = rs2.getInt("show2");     
          show3 = rs2.getInt("show3");     
          show4 = rs2.getInt("show4");     
          show5 = rs2.getInt("show5");     
          gtype1 = rs2.getString("gtype1");     
          gtype2 = rs2.getString("gtype2");     
          gtype3 = rs2.getString("gtype3");     
          gtype4 = rs2.getString("gtype4");     
          gtype5 = rs2.getString("gtype5");     
          grev1 = rs2.getInt("grev1");     
          grev2 = rs2.getInt("grev2");     
          grev3 = rs2.getInt("grev3");     
          grev4 = rs2.getInt("grev4");     
          grev5 = rs2.getInt("grev5");   
          mship1 = rs2.getString("mship1");     
          mship2 = rs2.getString("mship2");     
          mship3 = rs2.getString("mship3");     
          mship4 = rs2.getString("mship4");     
          mship5 = rs2.getString("mship5");     
          
          if (dd >max) max = dd;          //track highest day this month

          if (!player1.equals("") && !player1.equalsIgnoreCase("x") && show1 == 1) {       // if player exists and they played

             if (!user1.equals("")) {        // if member

                memA[dd]++;                  // count member rounds
                totalmem++;               
             
                if (club.equals("xyzcc")) {

                    if (mship1.equalsIgnoreCase("Social")) {

                        mship1A[dd]++;
                        totalMship1++;

                    } else if (mship1.equalsIgnoreCase("Tennis Couple")) {

                        mship2A[dd]++;
                        totalMship2++;

                    } else if (mship1.equalsIgnoreCase("Tennis Family")) {

                        mship3A[dd]++;
                        totalMship3++;

                    } else if (mship1.equalsIgnoreCase("Tennis Junior")) {

                        mship4A[dd]++;
                        totalMship4++;

                    } else if (mship1.equalsIgnoreCase("Tennis Single")) {

                        mship5A[dd]++;
                        totalMship5++;                    
                    }
                }

             } else {

                if (!gtype1.equals("")) {        // if guest

                   if (grev1 == 1) {             // if rev guest

                      gstrevA[dd]++;            // count revenue guest rounds
                      totalrevgst++;

                   } else {

                      gstnorevA[dd]++;               // count guest rounds
                      totalgst++;
                   }

                } else {                        // not member, not guest, count as Other

                   otherA[dd]++;                // count other rounds
                   totalother++;
                }
             }
          }

          if (!player2.equals("") && !player2.equalsIgnoreCase("x") && show2 == 1) {       // if player exists and they played

             if (!user2.equals("")) {        // if member

                memA[dd]++;                  // count member rounds
                totalmem++;               
             
                if (club.equals("xyzcc")) {

                    if (mship2.equalsIgnoreCase("Social")) {

                        mship1A[dd]++;
                        totalMship1++;

                    } else if (mship2.equalsIgnoreCase("Tennis Couple")) {

                        mship2A[dd]++;
                        totalMship2++;

                    } else if (mship2.equalsIgnoreCase("Tennis Family")) {

                        mship3A[dd]++;
                        totalMship3++;

                    } else if (mship2.equalsIgnoreCase("Tennis Junior")) {

                        mship4A[dd]++;
                        totalMship4++;

                    } else if (mship2.equalsIgnoreCase("Tennis Single")) {

                        mship5A[dd]++;
                        totalMship5++;                    
                    }
                }

             } else {

                if (!gtype2.equals("")) {        // if guest

                   if (grev2 == 1) {             // if rev guest

                      gstrevA[dd]++;            // count revenue guest rounds
                      totalrevgst++;

                   } else {

                      gstnorevA[dd]++;               // count guest rounds
                      totalgst++;
                   }

                } else {                        // not member, not guest, count as Other

                   otherA[dd]++;                // count other rounds
                   totalother++;
                }
             }
          }

          if (!player3.equals("") && !player3.equalsIgnoreCase("x") && show3 == 1) {       // if player exists and they played

             if (!user3.equals("")) {        // if member

                memA[dd]++;                  // count member rounds
                totalmem++;               

                if (club.equals("xyzcc")) {

                    if (mship3.equalsIgnoreCase("Social")) {

                        mship1A[dd]++;
                        totalMship1++;

                    } else if (mship3.equalsIgnoreCase("Tennis Couple")) {

                        mship2A[dd]++;
                        totalMship2++;

                    } else if (mship3.equalsIgnoreCase("Tennis Family")) {

                        mship3A[dd]++;
                        totalMship3++;

                    } else if (mship3.equalsIgnoreCase("Tennis Junior")) {

                        mship4A[dd]++;
                        totalMship4++;

                    } else if (mship3.equalsIgnoreCase("Tennis Single")) {

                        mship5A[dd]++;
                        totalMship5++;                    
                    }
                }

             } else {

                if (!gtype3.equals("")) {        // if guest

                   if (grev3 == 1) {             // if rev guest

                      gstrevA[dd]++;            // count revenue guest rounds
                      totalrevgst++;

                   } else {

                      gstnorevA[dd]++;               // count guest rounds
                      totalgst++;
                   }

                } else {                        // not member, not guest, count as Other

                   otherA[dd]++;                // count other rounds
                   totalother++;
                }
             }
          }

          if (!player4.equals("") && !player4.equalsIgnoreCase("x") && show4 == 1) {       // if player exists and they played

             if (!user4.equals("")) {        // if member

                memA[dd]++;                  // count member rounds
                totalmem++;               

                if (club.equals("xyzcc")) {

                    if (mship4.equalsIgnoreCase("Social")) {

                        mship1A[dd]++;
                        totalMship1++;

                    } else if (mship4.equalsIgnoreCase("Tennis Couple")) {

                        mship2A[dd]++;
                        totalMship2++;

                    } else if (mship4.equalsIgnoreCase("Tennis Family")) {

                        mship3A[dd]++;
                        totalMship3++;

                    } else if (mship4.equalsIgnoreCase("Tennis Junior")) {

                        mship4A[dd]++;
                        totalMship4++;

                    } else if (mship4.equalsIgnoreCase("Tennis Single")) {

                        mship5A[dd]++;
                        totalMship5++;                    
                    }
                }

             } else {

                if (!gtype4.equals("")) {        // if guest

                   if (grev4 == 1) {             // if rev guest

                      gstrevA[dd]++;            // count revenue guest rounds
                      totalrevgst++;

                   } else {

                      gstnorevA[dd]++;               // count guest rounds
                      totalgst++;
                   }

                } else {                        // not member, not guest, count as Other

                   otherA[dd]++;                // count other rounds
                   totalother++;
                }
             }
          }

          if (!player5.equals("") && !player5.equalsIgnoreCase("x") && show5 == 1) {       // if player exists and they played

             if (!user5.equals("")) {        // if member

                memA[dd]++;                  // count member rounds
                totalmem++;               

                if (club.equals("xyzcc")) {

                    if (mship5.equalsIgnoreCase("Social")) {

                        mship1A[dd]++;
                        totalMship1++;

                    } else if (mship5.equalsIgnoreCase("Tennis Couple")) {

                        mship2A[dd]++;
                        totalMship2++;

                    } else if (mship5.equalsIgnoreCase("Tennis Family")) {

                        mship3A[dd]++;
                        totalMship3++;

                    } else if (mship5.equalsIgnoreCase("Tennis Junior")) {

                        mship4A[dd]++;
                        totalMship4++;

                    } else if (mship5.equalsIgnoreCase("Tennis Single")) {

                        mship5A[dd]++;
                        totalMship5++;                    
                    }
                }

             } else {

                if (!gtype5.equals("")) {        // if guest

                   if (grev5 == 1) {             // if rev guest

                      gstrevA[dd]++;            // count revenue guest rounds
                      totalrevgst++;

                   } else {

                      gstnorevA[dd]++;               // count guest rounds
                      totalgst++;
                   }

                } else {                        // not member, not guest, count as Other

                   otherA[dd]++;                // count other rounds
                   totalother++;
                }
             }
          }

       }   // end of WHILE for this month
                    
     
       for (i=1; i<(max+1); i++) {          // do each day
       
          out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
          out.println("<td>" +i+ "</td>");
          out.println("<td>" +memA[i]+ "</td>");
          if (club.equals("xyzcc")) {
              out.println("<td>" +mship1A[i]+ "</td>");    // Social
              out.println("<td>" +mship2A[i]+ "</td>");    // Tennis Couple
              out.println("<td>" +mship3A[i]+ "</td>");    // Tennis Family
              out.println("<td>" +mship4A[i]+ "</td>");    // Tennis Junior
              out.println("<td>" +mship5A[i]+ "</td>");    // Tennis Single
          }
          out.println("<td>" +gstrevA[i]+ "</td>");
          out.println("<td>" +gstnorevA[i]+ "</td>");
          out.println("<td>" +otherA[i]+ "</td>");
          out.println("<td bgcolor=\"#CCCCAA\">" + (memA[i] + gstrevA[i] + gstnorevA[i] + otherA[i]) + "</td>");
          out.println("</tr>");
       }

       out.println("<tr align=\"center\" bgcolor=\"#CCCCAA\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Totals</b></td>");
       out.println("<td>" +totalmem+ "</td>");
       if (club.equals("xyzcc")) {
           out.println("<td>" +totalMship1+ "</td>");
           out.println("<td>" +totalMship2+ "</td>");
           out.println("<td>" +totalMship3+ "</td>");
           out.println("<td>" +totalMship4+ "</td>");
           out.println("<td>" +totalMship5+ "</td>");
       }
       out.println("<td>" +totalrevgst+ "</td>");
       out.println("<td>" +totalgst+ "</td>");
       out.println("<td>" +totalother+ "</td>");
       out.println("<td>" +(totalmem + totalrevgst + totalgst + totalother)+ "</td>");       // grand total for month
       out.println("</tr></table><br><br>");

    
    }
    catch (Exception e) {
        displayDatabaseErrMsg("Error loading member information for report.", e.getMessage(), out);
        return;    
    }
    
    
    if (excel == false) {                // if not Excel Spreadsheet Format

       out.println("<table border=\"0\" align=\"center\">");
       out.println("<tr><td>");
       out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print();\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("<form method=\"post\" action=\"Proshop_report_rounds_by_month\" target=\"_blank\">");
       out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"month\" value=\"" + month + "\">");
       out.println("<input type=\"hidden\" name=\"year\" value=\"" + year + "\">");
       out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
       out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("</form>");
       out.println("<form method=\"post\" action=\"Proshop_report_rounds_by_month\">");
       out.println("<input type=\"hidden\" name=\"month\" value=\"" + month + "\">");
       out.println("<input type=\"hidden\" name=\"year\" value=\"" + year + "\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</td></tr></table><br>");
    }
    
    endPageOutput(out);
    
 } // end of doPost routine
 

//
//  Start of Page
//
 private void startPageOutput(boolean excel, PrintWriter out) {
    
    if (excel == false) {                // if not Excel Spreadsheet Format

       out.println(SystemUtils.HeadTitle("Member Rounds Report"));
       
    } else {
    
       out.println("<html><head><title>Member Rounds Report</title></head>");
    }
    
    out.println("<BODY bgcolor=white><CENTER>");
 }
 

//
//  End of Page
//
 private void endPageOutput(PrintWriter out) {
    
    out.println("</center></body></html>");
    out.close();
 }
 

//
//   Error Message
//
 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H2>Database Access Error</H2>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }


//
//   Warning Message
//
 private void displayPrompt(int month, int year, PrintWriter out, parmClub parm, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   
   int oldyear = 0;
   int oldmonth = 0;
   int thisyear = 0;
   int thismonth = 0;
   int yy = 0;
   int mm = 0;
   int index = 0;
   
   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses


   //
   //  Get the current year
   //
   Calendar cal = new GregorianCalendar();      
   thisyear = cal.get(Calendar.YEAR);            // current year
   thismonth = cal.get(Calendar.MONTH) +1;       // current month

   if (month== 0 || year == 0) {               // if year or month not provided
      
      year = thisyear;  
      month = thismonth;
   }
   
   //
   //  Get the oldest tee times for this club to determine how far back they can select
   //
   try {
      
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT MIN(mm), MIN(yy) " +
                             "FROM teepast2");

      if (rs.next()) {

         oldmonth = rs.getInt(1);
         oldyear = rs.getInt(2);
      }
      stmt.close();

   }
   catch (Exception exc) {

        displayDatabaseErrMsg("Error loading tee time information for report.", exc.getMessage(), out);
        return;
   }

   if (oldyear == 0) {            // if they don't have more than one year to compare - reject
      
       out.println(SystemUtils.HeadTitle("Procedure Error"));
       out.println("<BODY><CENTER>");
       out.println("<BR><BR><H2>Procedure Error</H2>");
       out.println("<BR><BR>Sorry, we are unable to process this report as there is not enough history for this report.");
       out.println("<BR>This report requires at least one month of tee time history.");
       out.println("<BR><BR>Please try again later.");
       out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
       out.println("</CENTER></BODY></HTML>");
   }
  
   //
   // Check for multiple courses
   //
   int count = 1;                  // init to 1 course

   if (parm.multi != 0) {           // if multiple courses supported for this club

      try {

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names

         count = course.size();                      // number of courses

      }
      catch (Exception exc) {

         displayDatabaseErrMsg("Error loading course information for report.", exc.getMessage(), out);
         return;
      }
   }

   
    out.println(SystemUtils.HeadTitle("Report Prompt"));
    out.println("<BODY><CENTER>");
    out.println("<BR><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<BR><H2>Daily Rounds By Month Report</H2>");
    out.println("This report will display the rounds per day for the month and year selected below.");
    out.println("<BR><BR>");
    
    if (parm.multi != 0) {           // if multiple courses supported for this club

       out.println("Please select the month, year and the course.");
       
    } else {

       out.println("Please select the month and year for the report.");
    }
    out.println("<BR><BR>");
    out.println("<form method=\"post\" action=\"Proshop_report_rounds_by_month\">");
    out.println("Month:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"month\">");
    
    if (month == 1) {
       out.println("<option selected value=\"1\">Jan</option>");
    } else {
       out.println("<option value=\"1\">Jan</option>");
    }
    if (month == 2) {
       out.println("<option selected value=\"2\">Feb</option>");
    } else {
       out.println("<option value=\"2\">Feb</option>");
    }
    if (month == 3) {
       out.println("<option selected value=\"3\">Mar</option>");
    } else {
       out.println("<option value=\"3\">Mar</option>");
    }
    if (month == 4) {
       out.println("<option selected value=\"4\">Apr</option>");
    } else {
       out.println("<option value=\"4\">Apr</option>");
    }
    if (month == 5) {
       out.println("<option selected value=\"5\">May</option>");
    } else {
       out.println("<option value=\"5\">May</option>");
    }
    if (month == 6) {
       out.println("<option selected value=\"6\">Jun</option>");
    } else {
       out.println("<option value=\"6\">Jun</option>");
    }
    if (month == 7) {
       out.println("<option selected value=\"7\">Jul</option>");
    } else {
       out.println("<option value=\"7\">Jul</option>");
    }
    if (month == 8) {
       out.println("<option selected value=\"8\">Aug</option>");
    } else {
       out.println("<option value=\"8\">Aug</option>");
    }
    if (month == 9) {
       out.println("<option selected value=\"9\">Sep</option>");
    } else {
       out.println("<option value=\"9\">Sep</option>");
    }
    if (month == 10) {
       out.println("<option selected value=\"10\">Oct</option>");
    } else {
       out.println("<option value=\"10\">Oct</option>");
    }
    if (month == 11) {
       out.println("<option selected value=\"11\">Nov</option>");
    } else {
       out.println("<option value=\"11\">Nov</option>");
    }
    if (month == 12) {
       out.println("<option selected value=\"12\">Dec</option>");
    } else {
       out.println("<option value=\"12\">Dec</option>");
    }
  
    out.println("</select>");

    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"year\">");
   
    // yy = oldyear + 1;        // start with the oldest year +1   ????????
    yy = oldyear;        // start with the oldest year
      
    while (yy <= thisyear) {   
      
       if (yy == year) {
          out.println("<option selected value=\"" + yy + "\">" + yy + "</option>");
       } else {
          out.println("<option value=\"" + yy + "\">" + yy + "</option>");
       }
       yy++;
    }
    out.println("</select>");

    if (parm.multi != 0) {           // if multiple courses supported for this club

       out.println("<BR><BR>");
         out.println("<b>Course:</b>&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"course\">");

         out.println("<option selected value=\"-ALL-\">All Courses</option>");
         
         for (index=0; index < count; index++) {

            out.println("<option value=\"" + course.get(index) + "\">" + course.get(index) + "</option>");
         }
         out.println("</select>");
    }
       
    out.println("<BR><BR>");
    out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
    out.println("<input type=\"submit\" value=\"Run Report\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("<BR>");
    out.println("<form method=\"get\" action=\"Proshop_announce\">");
    out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</CENTER></BODY></HTML>");
 }

} // end servlet public class
