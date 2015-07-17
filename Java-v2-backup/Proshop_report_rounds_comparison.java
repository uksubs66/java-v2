/***************************************************************************************
 *   Proshop_report_rounds_comparison: This servlet will generate a report showing the number
 *                                     of rounds per month for 2 years and then compare them.
 *
 *   Called by:     menu
 *
 * 
 *
 *   Created:       1/30/2009 by Bob
 *
 *
 *   Last Updated:  
 *
 *        5/25/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *       12/04/09   Add custom dates so southern clubs can compare seasons (case 1732).
 *        6/02/09   Add menu code so that drop-downs work on the config page
 *        4/15/09   Use new fields in teepast for guest type, guest rev.
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
import com.foretees.common.Connect;

public class Proshop_report_rounds_comparison extends HttpServlet {

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
    
    int year1 = 0;
    int year2 = 0;
    int month1 = 0;
    int month2 = 0;
    
    
    if (req.getParameter("excel") != null) {
       
       excel = true;
    }
    
    // handle excel output
    try{
        if (excel == true) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\"Proshop_report_rounds_comparison.xls\"");
        }
    }
    catch (Exception exc) {
    }
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    
    Connection con = Connect.getCon(req);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not establish connection.", "", out);
        return;
    }
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
        SystemUtils.restrictProshop("REPORTS", out);
        return;
    }
    
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

    //
    //  Get user parms if provided (if return)
    //
    String temp = (req.getParameter("year1") != null) ? req.getParameter("year1")  : "";
      
    if (!temp.equals("")) {

       year1 = Integer.parseInt(temp);
    }

    temp = (req.getParameter("year2") != null) ? req.getParameter("year2")  : "";
      
    if (!temp.equals("")) {

       year2 = Integer.parseInt(temp);
    }

    temp = (req.getParameter("month1") != null) ? req.getParameter("month1")  : "";
      
    if (!temp.equals("")) {

       month1 = Integer.parseInt(temp);
    }

    temp = (req.getParameter("month2") != null) ? req.getParameter("month2")  : "";
      
    if (!temp.equals("")) {

       month2 = Integer.parseInt(temp);
    }

    String course = (req.getParameter("course") != null) ? req.getParameter("course")  : "";
      
  
    // 
    //  First time here - display warning message to inform user that this report can take several minutes!!!!
    //
    if (req.getParameter("continue") == null) {

       out.println("<script language=\"javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
       out.println("<BODY>");
       SystemUtils.getProshopSubMenu(req, out, lottery);
       
       displayPrompt(year1, year2, month1, month2, out, parm, con);                  // prompt user for years to report
       return;                                   // exit and wait for continue  
    }
    

    //
    //  User already prompted for Years - now build the report
    //
    int yr1totalmem = 0;
    int yr1totalgst = 0;
    int yr1totalrevgst = 0;
    int yr1totalother = 0;
    int yr2totalmem = 0;
    int yr2totalgst = 0;
    int yr2totalrevgst = 0;
    int yr2totalother = 0;
    int yr1total = 0;
    int yr2total = 0;
    int month = 0; 
    //int year = 0;
    int i = 0;
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
    int year1save = year1;
    int year2save = year2;
        
    int [] year1memA = new int [13];            // year 1 member rounds (1 per month - start with 1)
    int [] year1gstrevA = new int [13];         // year 1 revenue gst rounds 
    int [] year1gstnorevA = new int [13];       // year 1 non revenue gst rounds
    int [] year1otherA = new int [13];          // year 1 other rounds
    
    int [] year2memA = new int [13];            // year 2 member rounds (1 per month + total)
    int [] year2gstrevA = new int [13];         // year 2 revenue gst rounds
    int [] year2gstnorevA = new int [13];       // year 2 non revenue gst rounds
    int [] year2otherA = new int [13];          // year 2 other rounds

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
    String month1s = "";
    String month2s = "";
  
   
    String [] mm_table = { "inv", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                          "Sep", "Oct", "Nov", "Dec" };

    //  get names for the selected months
    
    month1s = mm_table[month1];              
    month2s = mm_table[month2];                  
   

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
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><br><b>Rounds Comparison Report - " +month1s+ " " +year1+ " and " +month2s+ " " +year2+ " ");

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
          
          sqlString = "SELECT player1, player2, player3, player4, player5, username1, username2, username3, username4, username5, " +
                  "show1, show2, show3, show4, show5, gtype1, gtype2, gtype3, gtype4, gtype5, grev1, grev2, grev3, grev4, grev5 " +
                  "FROM teepast2 WHERE yy = ? AND mm = ?";
          
       } else {
          
          sqlString = "SELECT player1, player2, player3, player4, player5, username1, username2, username3, username4, username5, " +
                  "show1, show2, show3, show4, show5, gtype1, gtype2, gtype3, gtype4, gtype5, grev1, grev2, grev3, grev4, grev5 " +
                  "FROM teepast2 WHERE yy = ? AND mm = ? AND courseName = ?";
       }
       

       pstmtc2 = con.prepareStatement (sqlString);

       
       //
       // start table output for year 1
       //
       out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" border=\"1\">");
       out.println("<tr align=\"center\" bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.7em\">" +
                   "<td><b>" +month1+ "/" +year1+ " -> </b></td>");

       month = month1;                  // start with the first starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td><b>" +mm_table[month]+ "</b></td>");
          
          month++;
       }
       out.println("<td><b>Total</b></td></tr>");

       //
       //  gather stats for the first period
       //
       month = month1;                  // start at start of first range
       
       loop1:
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) {
             
             month = 1;    // reset to Jan of next year
             year1++;       
          }
          
          pstmtc2.clearParameters();

          pstmtc2.setInt(1, year1);
          pstmtc2.setInt(2, month);

          if (!course.equals( "" ) && !course.equals( "-ALL-" )) {
             
             pstmtc2.setString(3, course);
          }
          
          rs2 = pstmtc2.executeQuery();      // execute the prepared stmt

          while (rs2.next()) {               // get all tee times for this month and year1

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
             
             if (!player1.equals("") && !player1.equalsIgnoreCase("x") && show1 == 1) {       // if player exists and they played
              
                if (!user1.equals("")) {        // if member
                   
                   year1memA[month]++;                  // count member rounds
                   yr1totalmem++;               
             
                } else {
                   
                   if (!gtype1.equals("")) {        // if guest
                      
                      if (grev1 == 1) {             // if rev guest
                         
                         year1gstrevA[month]++;            // count revenue guest rounds
                         yr1totalrevgst++;
                         
                      } else {
                      
                         year1gstnorevA[month]++;               // count guest rounds
                         yr1totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year1otherA[month]++;                // count other rounds
                      yr1totalother++;
                   }
                }
             }
             
             if (!player2.equals("") && !player2.equalsIgnoreCase("x") && show2 == 1) {       // if player exists and they played
              
                if (!user2.equals("")) {        // if member
                   
                   year1memA[month]++;                  // count member rounds
                   yr1totalmem++;               
             
                } else {
                   
                   if (!gtype2.equals("")) {        // if guest
                      
                      if (grev2 == 1) {             // if rev guest
                         
                         year1gstrevA[month]++;            // count revenue guest rounds
                         yr1totalrevgst++;
             
                      } else {
                      
                         year1gstnorevA[month]++;               // count guest rounds
                         yr1totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year1otherA[month]++;                // count other rounds
                      yr1totalother++;
                   }
                }
             }
             
             if (!player3.equals("") && !player3.equalsIgnoreCase("x") && show3 == 1) {       // if player exists and they played
              
                if (!user3.equals("")) {        // if member
                   
                   year1memA[month]++;                  // count member rounds
                   yr1totalmem++;               
             
                } else {
                   
                   if (!gtype3.equals("")) {        // if guest
                      
                      if (grev3 == 1) {             // if rev guest
                         
                         year1gstrevA[month]++;            // count revenue guest rounds
                         yr1totalrevgst++;
             
                      } else {
                      
                         year1gstnorevA[month]++;               // count guest rounds
                         yr1totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year1otherA[month]++;                // count other rounds
                      yr1totalother++;
                   }
                }
             }
             
             if (!player4.equals("") && !player4.equalsIgnoreCase("x") && show4 == 1) {       // if player exists and they played
              
                if (!user4.equals("")) {        // if member
                   
                   year1memA[month]++;                  // count member rounds
                   yr1totalmem++;               
             
                } else {
                   
                   if (!gtype4.equals("")) {        // if guest
                      
                      if (grev4 == 1) {             // if rev guest
                         
                         year1gstrevA[month]++;            // count revenue guest rounds
                         yr1totalrevgst++;
             
                      } else {
                      
                         year1gstnorevA[month]++;               // count guest rounds
                         yr1totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year1otherA[month]++;                // count other rounds
                      yr1totalother++;
                   }
                }
             }
             
             if (!player5.equals("") && !player5.equalsIgnoreCase("x") && show5 == 1) {       // if player exists and they played
              
                if (!user5.equals("")) {        // if member
                   
                   year1memA[month]++;                  // count member rounds
                   yr1totalmem++;               
             
                } else {
                   
                   if (!gtype5.equals("")) {        // if guest
                      
                      if (grev5 == 1) {             // if rev guest
                         
                         year1gstrevA[month]++;            // count revenue guest rounds
                         yr1totalrevgst++;
             
                      } else {
                      
                         year1gstnorevA[month]++;               // count guest rounds
                         yr1totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year1otherA[month]++;                // count other rounds
                      yr1totalother++;
                   }
                }
             }
             
          }   // end of WHILE for this month
          
          month++;
                    
       }      // end of FOR loop (does each month) - loop1


     //  yr1totalmem = (year1memA[1] + year1memA[2] + year1memA[3] + year1memA[4] + year1memA[5] + year1memA[6] + year1memA[7] + year1memA[8] + year1memA[9] + year1memA[10] + year1memA[11] + year1memA[12]);
     //  yr1totalrevgst = (year1gstrevA[1] + year1gstrevA[2] + year1gstrevA[3] + year1gstrevA[4] + year1gstrevA[5] + year1gstrevA[6] + year1gstrevA[7] + year1gstrevA[8] + year1gstrevA[9] + year1gstrevA[10] + year1gstrevA[11] + year1gstrevA[12]);
     //  yr1totalgst = (year1gstnorevA[1] + year1gstnorevA[2] + year1gstnorevA[3] + year1gstnorevA[4] + year1gstnorevA[5] + year1gstnorevA[6] + year1gstnorevA[7] + year1gstnorevA[8] + year1gstnorevA[9] + year1gstnorevA[10] + year1gstnorevA[11] + year1gstnorevA[12]);
     //  yr1totalother = (year1otherA[1] + year1otherA[2] + year1otherA[3] + year1otherA[4] + year1otherA[5] + year1otherA[6] + year1otherA[7] + year1otherA[8] + year1otherA[9] + year1otherA[10] + year1otherA[11] + year1otherA[12]);
       
       
       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Member Rounds</b></td>");
       
       month = month1;                  // start with the first starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +year1memA[month]+ "</td>");
          
          month++;
       }     
       out.println("<td bgcolor=\"#CCCCAA\">" +yr1totalmem+ "</td>");
       out.println("</tr>");

       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Guest Rounds (Rev)</b></td>");
       
       month = month1;                  // start with the first starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +year1gstrevA[month]+ "</td>");
          
          month++;
       }       
       out.println("<td bgcolor=\"#CCCCAA\">" +yr1totalrevgst+ "</td>");
       out.println("</tr>");

       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Guest Rounds (Non-Rev)</b></td>");
       
       month = month1;                  // start with the first starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +year1gstnorevA[month]+ "</td>");
          
          month++;
       }       
       out.println("<td bgcolor=\"#CCCCAA\">" +yr1totalgst+ "</td>");
       out.println("</tr>");

       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Other Rounds (Unkown)</b></td>");
       
       month = month1;                  // start with the first starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +year1otherA[month]+ "</td>");
          
          month++;
       }       
       out.println("<td bgcolor=\"#CCCCAA\">" +yr1totalother+ "</td>");
       out.println("</tr>");

       out.println("<tr align=\"center\" bgcolor=\"#CCCCAA\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Totals</b></td>");
       
       month = month1;                  // start with the first starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +(year1gstnorevA[month] + year1gstrevA[month] + year1memA[month] + year1otherA[month])+ "</td>");
          
          month++;
       }       
       out.println("<td>" +(yr1totalmem + yr1totalrevgst + yr1totalgst + yr1totalother)+ "</td>");       // grand total for year
       out.println("</tr></table><br><br>");

    
       //
       //  Output the 2nd year table
       //
       out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" border=\"1\">");
       out.println("<tr align=\"center\" bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.7em\">" +
                   "<td><b>" +month2+ "/" +year2+ " -> </b></td>");
       
       month = month2;                  // start with the 2nd starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td><b>" +mm_table[month]+ "</b></td>");
          
          month++;
       }
       out.println("<td><b>Total</b></td></tr>");


       //
       //  gather stats for this year
       //
       month = month2;                  // start at start of 2nd range
       
       loop2:
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) {
             
             month = 1;    // reset to Jan of next year
             year2++;       
          }
          
          pstmtc2.clearParameters();

          pstmtc2.setInt(1, year2);
          pstmtc2.setInt(2, month);

          if (!course.equals( "" ) && !course.equals( "-ALL-" )) {
             
             pstmtc2.setString(3, course);
          }
          
          rs2 = pstmtc2.executeQuery();      // execute the prepared stmt

          while (rs2.next()) {

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
             
             if (!player1.equals("") && !player1.equalsIgnoreCase("x") && show1 == 1) {       // if player exists and they played
              
                if (!user1.equals("")) {        // if member
                   
                   year2memA[month]++;                  // count member rounds
                   yr2totalmem++;
             
                } else {
                   
                   if (!gtype1.equals("")) {        // if guest
                      
                      if (grev1 == 1) {             // if rev guest
                         
                         year2gstrevA[month]++;            // count revenue guest rounds
                         yr2totalrevgst++;
             
                      } else {
                      
                         year2gstnorevA[month]++;               // count guest rounds
                         yr2totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year2otherA[month]++;                // count other rounds
                      yr2totalother++;
                   }
                }
             }
             
             if (!player2.equals("") && !player2.equalsIgnoreCase("x") && show2 == 1) {       // if player exists and they played
              
                if (!user2.equals("")) {        // if member
                   
                   year2memA[month]++;                  // count member rounds
                   yr2totalmem++;
             
                } else {
                   
                   if (!gtype2.equals("")) {        // if guest
                      
                      if (grev2 == 1) {             // if rev guest
                         
                         year2gstrevA[month]++;            // count revenue guest rounds
                         yr2totalrevgst++;
             
                      } else {
                      
                         year2gstnorevA[month]++;               // count guest rounds
                         yr2totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year2otherA[month]++;                // count other rounds
                      yr2totalother++;
                   }
                }
             }
             
             if (!player3.equals("") && !player3.equalsIgnoreCase("x") && show3 == 1) {       // if player exists and they played
              
                if (!user3.equals("")) {        // if member
                   
                   year2memA[month]++;                  // count member rounds
                   yr2totalmem++;
             
                } else {
                   
                   if (!gtype3.equals("")) {        // if guest
                      
                      if (grev3 == 1) {             // if rev guest
                         
                         year2gstrevA[month]++;            // count revenue guest rounds
                         yr2totalrevgst++;
             
                      } else {
                      
                         year2gstnorevA[month]++;               // count guest rounds
                         yr2totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year2otherA[month]++;                // count other rounds
                      yr2totalother++;
                   }
                }
             }
             
             if (!player4.equals("") && !player4.equalsIgnoreCase("x") && show4 == 1) {       // if player exists and they played
              
                if (!user4.equals("")) {        // if member
                   
                   year2memA[month]++;                  // count member rounds
                   yr2totalmem++;
             
                } else {
                   
                   if (!gtype4.equals("")) {        // if guest
                      
                      if (grev4 == 1) {             // if rev guest
                         
                         year2gstrevA[month]++;            // count revenue guest rounds
                         yr2totalrevgst++;
             
                      } else {
                      
                         year2gstnorevA[month]++;               // count guest rounds
                         yr2totalgst++;
                      }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year2otherA[month]++;                // count other rounds
                      yr2totalother++;
                   }
                }
             }
             
             if (!player5.equals("") && !player5.equalsIgnoreCase("x") && show5 == 1) {       // if player exists and they played
              
                if (!user5.equals("")) {        // if member
                   
                   year2memA[month]++;                  // count member rounds
                   yr2totalmem++;
             
                } else {
                   
                   if (!gtype5.equals("")) {        // if guest
                      
                      if (grev5 == 1) {             // if rev guest
                         
                         year2gstrevA[month]++;            // count revenue guest rounds
                         yr2totalrevgst++;
             
                      } else {
                      
                         year2gstnorevA[month]++;               // count guest rounds
                         yr2totalgst++;
                     }
                      
                   } else {                        // not member, not guest, count as Other
                   
                      year2otherA[month]++;                // count other rounds
                      yr2totalother++;
                   }
                }
             }
             
          }   // end of WHILE for this month
          
          month++;

       }      // end of FOR loop (does each month) - loop2


      // yr2totalmem = (year2memA[1] + year2memA[2] + year2memA[3] + year2memA[4] + year2memA[5] + year2memA[6] + year2memA[7] + year2memA[8] + year2memA[9] + year2memA[10] + year2memA[11] + year2memA[12]);
      // yr2totalrevgst = (year2gstrevA[1] + year2gstrevA[2] + year2gstrevA[3] + year2gstrevA[4] + year2gstrevA[5] + year2gstrevA[6] + year2gstrevA[7] + year2gstrevA[8] + year2gstrevA[9] + year2gstrevA[10] + year2gstrevA[11] + year2gstrevA[12]);
      // yr2totalgst = (year2gstnorevA[1] + year2gstnorevA[2] + year2gstnorevA[3] + year2gstnorevA[4] + year2gstnorevA[5] + year2gstnorevA[6] + year2gstnorevA[7] + year2gstnorevA[8] + year2gstnorevA[9] + year2gstnorevA[10] + year2gstnorevA[11] + year2gstnorevA[12]);
      // yr2totalother = (year2otherA[1] + year2otherA[2] + year2otherA[3] + year2otherA[4] + year2otherA[5] + year2otherA[6] + year2otherA[7] + year2otherA[8] + year2otherA[9] + year2otherA[10] + year2otherA[11] + year2otherA[12]);
       
       
       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Member Rounds</b></td>");
       
       month = month2;                  // start with the 2nd starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +year2memA[month]+ "</td>");
          
          month++;
       }
       out.println("<td bgcolor=\"#CCCCAA\">" +yr2totalmem+ "</td>");
       out.println("</tr>");

       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Guest Rounds (Rev)</b></td>");
       
       month = month2;                  // start with the 2nd starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +year2gstrevA[month]+ "</td>");
          
          month++;
       }
       out.println("<td bgcolor=\"#CCCCAA\">" +yr2totalrevgst+ "</td>");
       out.println("</tr>");

       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Guest Rounds (Non-Rev)</b></td>");
       
       month = month2;                  // start with the 2nd starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +year2gstnorevA[month]+ "</td>");
          
          month++;
       }
       out.println("<td bgcolor=\"#CCCCAA\">" +yr2totalgst+ "</td>");
       out.println("</tr>");

       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Other Rounds (Unkown)</b></td>");
       
       month = month2;                  // start with the 2nd starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +year2otherA[month]+ "</td>");
          
          month++;
       }
       out.println("<td bgcolor=\"#CCCCAA\">" +yr2totalother+ "</td>");
       out.println("</tr>");

       out.println("<tr align=\"center\" bgcolor=\"#CCCCAA\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Totals</b></td>");
       
       month = month2;                  // start with the 2nd starting month
       
       for (i=0; i<12; i++) {           // do each month of this year

          if (month > 12) month = 1;    // reset to Jan
          
          out.println("<td>" +(year2gstnorevA[month] + year2gstrevA[month] + year2memA[month] + year2otherA[month])+ "</td>");
          
          month++;
       }
       out.println("<td>" +(yr2totalmem + yr2totalrevgst + yr2totalgst + yr2totalother)+ "</td>");       // grand total for year
       out.println("</tr></table><br><br>");
       
            
       pstmtc2.close();

       
       out.println("The following table compares the 2nd year against the 1st year.<br>");
       out.println("If the value is <b>negative</b>, then the percentage represents the amount the 2nd year is <b>down</b> from the 1st year.<br>");
       out.println("If the value is <b>positive</b>, then the percentage represents the amount the 2nd year is <b>up</b> from the 1st year.<br>");
       out.println("<br>");
       
       
       //
       //  Output the Comparison table
       //
       out.println("<table align=\"center\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" border=\"1\">");
       out.println("<tr align=\"center\" bgcolor=\"#336633\" style=\"font-family:verdana;color:white;font-size:.7em\">" +
                   "<td><b>Comparisons</b></td>" +
                   "<td><b>Month 1</b></td>" +
                   "<td><b>Month 2</b></td>" +
                   "<td><b>Month 3</b></td>" +
                   "<td><b>Month 4</b></td>" +
                   "<td><b>Month 5</b></td>" +
                   "<td><b>Month 6</b></td>" +
                   "<td><b>Month 7</b></td>" +
                   "<td><b>Month 8</b></td>" +
                   "<td><b>Month 9</b></td>" +
                   "<td><b>Month 10</b></td>" +
                   "<td><b>Month 11</b></td>" +
                   "<td><b>Month 12</b></td>" +
                   "<td><b>Total</b></td>");
       out.println("</tr>");

       //
       //  display the comparison rows
       //
       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Member Rounds</b></td>");
       
       int mm1 = month1;
       int mm2 = month2;
       
       for (i=1; i<13; i++) {              // do each month

          if (mm1 > 12) mm1 = 1;           // wrap the month if necessary

          if (mm2 > 12) mm2 = 1;

          if (year1memA[mm1] < 1 && year2memA[mm2] < 1) {     // if both months are zero
          
             out.println("<td> - </td>");
             
          } else {
             
             if (year1memA[mm1] < year2memA[mm2]) {     // if year 1 is lower

                if (year1memA[mm1] < 1) {
                   
                   out.println("<td> +" +year2memA[mm2]+ " (+100%) </td>");

                } else {
                   
                   if ((((year2memA[mm2] - year1memA[mm1]) * 100)/year1memA[mm1]) < 1) {

                      out.println("<td> +" +(year2memA[mm2] - year1memA[mm1])+ " (+<1%) </td>");

                   } else {

                      out.println("<td> +" +(year2memA[mm2] - year1memA[mm1])+ " (+" +(((year2memA[mm2] - year1memA[mm1]) * 100)/year1memA[mm1])+ "%) </td>");
                   }
                }
                
             } else {                // year 1 is more than year 2
             
                if ((((year1memA[mm1] - year2memA[mm2]) * 100)/year1memA[mm1]) < 1) {

                   out.println("<td> -" +(year1memA[mm1] - year2memA[mm2])+ " (-<1%) </td>");

                } else {
             
                   out.println("<td> -" +(year1memA[mm1] - year2memA[mm2])+ " (-" +(((year1memA[mm1] - year2memA[mm2]) * 100)/year1memA[mm1])+ "%) </td>");
                }
             }             
          }  
          
          mm1++;
          mm2++;
       }
          
       //  do total 
       
       if (yr1totalmem < 1 && yr2totalmem < 1) {     // if both are zero

          out.println("<td bgcolor=\"#CCCCAA\"> - </td>");

       } else {

          if (yr1totalmem < yr2totalmem) {     // if year 1 is lower

             if (yr1totalmem < 1) {

                out.println("<td bgcolor=\"#CCCCAA\"> +" +yr2totalmem+ " (+100%) </td>");

             } else {
                   
                if ((((yr2totalmem - yr1totalmem) * 100)/yr1totalmem) < 1) {

                   out.println("<td bgcolor=\"#CCCCAA\"> +" +(yr2totalmem - yr1totalmem)+ " (+<1%) </td>");

                } else {

                   out.println("<td bgcolor=\"#CCCCAA\"> +" +(yr2totalmem - yr1totalmem)+ " (+" +(((yr2totalmem - yr1totalmem) * 100)/yr1totalmem)+ "%) </td>");
                }
             }

          } else {                // year 1 is more than year 2

             if ((((yr1totalmem - yr2totalmem) * 100)/yr1totalmem) < 1) {

                out.println("<td bgcolor=\"#CCCCAA\"> -" +(yr1totalmem - yr2totalmem)+ " (-<1%) </td>");

             } else {

                out.println("<td bgcolor=\"#CCCCAA\"> -" +(yr1totalmem - yr2totalmem)+ " (-" +(((yr1totalmem - yr2totalmem) * 100)/yr1totalmem)+ "%) </td>");
             }
          }             
       }
       
       out.println("</tr>");

       
       //  Revenue Guest Comparisons
       
       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Guest Rounds (Rev)</b></td>");
       
       mm1 = month1;
       mm2 = month2;
       
       for (i=1; i<13; i++) {                       // do each month
          
          if (mm1 > 12) mm1 = 1;           // wrap the month if necessary

          if (mm2 > 12) mm2 = 1;

          if (year1gstrevA[mm1] < 1 && year2gstrevA[mm2] < 1) {     // if both months are zero
          
             out.println("<td> - </td>");
             
          } else {
             
             if (year1gstrevA[mm1] < year2gstrevA[mm2]) {     // if year 1 is lower

                if (year1gstrevA[mm1] < 1) {
                   
                   out.println("<td> +" +year2gstrevA[mm2]+ " (+100%) </td>");

                } else {
                   
                   if ((((year2gstrevA[mm2] - year1gstrevA[mm1]) * 100)/year1gstrevA[mm1]) < 1) {

                      out.println("<td> +" +(year2gstrevA[mm2] - year1gstrevA[mm1])+ " (+<1%) </td>");

                   } else {

                      out.println("<td> +" +(year2gstrevA[mm2] - year1gstrevA[mm1])+ " (+" +(((year2gstrevA[mm2] - year1gstrevA[mm1]) * 100)/year1gstrevA[mm1])+ "%) </td>");
                   }
                }
                
             } else {                // year 1 is more than year 2
             
                if ((((year1gstrevA[mm1] - year2gstrevA[mm2]) * 100)/year1gstrevA[mm1]) < 1) {

                   out.println("<td> -" +(year1gstrevA[mm1] - year2gstrevA[mm2])+ " (-<1%) </td>");

                } else {
             
                   out.println("<td> -" +(year1gstrevA[mm1] - year2gstrevA[mm2])+ " (-" +(((year1gstrevA[mm1] - year2gstrevA[mm2]) * 100)/year1gstrevA[mm1])+ "%) </td>");
                }
             }             
          }  
          
          mm1++;
          mm2++;
       }
          
       //  do total 
       
       if (yr1totalrevgst < 1 && yr2totalrevgst < 1) {     // if both are zero

          out.println("<td bgcolor=\"#CCCCAA\"> - </td>");

       } else {

          if (yr1totalrevgst < yr2totalrevgst) {     // if year 1 is lower

             if (yr1totalrevgst < 1) {

                out.println("<td bgcolor=\"#CCCCAA\"> +" +yr2totalrevgst+ " (+100%) </td>");

             } else {
                   
                if ((((yr2totalrevgst - yr1totalrevgst) * 100)/yr1totalrevgst) < 1) {

                   out.println("<td bgcolor=\"#CCCCAA\"> +" +(yr2totalrevgst - yr1totalrevgst)+ " (+<1%) </td>");

                } else {

                   out.println("<td bgcolor=\"#CCCCAA\"> +" +(yr2totalrevgst - yr1totalrevgst)+ " (+" +(((yr2totalrevgst - yr1totalrevgst) * 100)/yr1totalrevgst)+ "%) </td>");
                }
             }

          } else {                // year 1 is more than year 2

             if ((((yr1totalrevgst - yr2totalrevgst) * 100)/yr1totalrevgst) < 1) {

                out.println("<td bgcolor=\"#CCCCAA\"> -" +(yr1totalrevgst - yr2totalrevgst)+ " (-<1%) </td>");

             } else {

                out.println("<td bgcolor=\"#CCCCAA\"> -" +(yr1totalrevgst - yr2totalrevgst)+ " (-" +(((yr1totalrevgst - yr2totalrevgst) * 100)/yr1totalrevgst)+ "%) </td>");
             }
          }             
       }
       
       out.println("</tr>");

       
       
       //  Non-Revenue Guest Comparisons
       
       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Guest Rounds (Non-Rev)</b></td>");
       
       mm1 = month1;
       mm2 = month2;
       
       for (i=1; i<13; i++) {                       // do each month
          
          if (mm1 > 12) mm1 = 1;           // wrap the month if necessary

          if (mm2 > 12) mm2 = 1;

          if (year1gstnorevA[mm1] < 1 && year2gstnorevA[mm2] < 1) {     // if both months are zero
          
             out.println("<td> - </td>");
             
          } else {
             
             if (year1gstnorevA[mm1] < year2gstnorevA[mm2]) {     // if year 1 is lower

                if (year1gstnorevA[mm1] < 1) {
                   
                   out.println("<td> +" +year2gstnorevA[mm2]+ " (+100%) </td>");

                } else {
                   
                   if ((((year2gstnorevA[mm2] - year1gstnorevA[mm1]) * 100)/year1gstnorevA[mm1]) < 1) {

                      out.println("<td> +" +(year2gstnorevA[mm2] - year1gstnorevA[mm1])+ " (+<1%) </td>");

                   } else {

                      out.println("<td> +" +(year2gstnorevA[mm2] - year1gstnorevA[mm1])+ " (+" +(((year2gstnorevA[mm2] - year1gstnorevA[mm1]) * 100)/year1gstnorevA[mm1])+ "%) </td>");
                   }
                }
                
             } else {                // year 1 is more than year 2
             
                if ((((year1gstnorevA[mm1] - year2gstnorevA[mm2]) * 100)/year1gstnorevA[mm1]) < 1) {

                   out.println("<td> -" +(year1gstnorevA[mm1] - year2gstnorevA[mm2])+ " (-<1%) </td>");

                } else {
             
                   out.println("<td> -" +(year1gstnorevA[mm1] - year2gstnorevA[mm2])+ " (-" +(((year1gstnorevA[mm1] - year2gstnorevA[mm2]) * 100)/year1gstnorevA[mm1])+ "%) </td>");
                }
             }             
          }  
          
          mm1++;
          mm2++;
       }
          
       //  do total 
       
       if (yr1totalgst < 1 && yr2totalgst < 1) {     // if both are zero

          out.println("<td bgcolor=\"#CCCCAA\"> - </td>");

       } else {

          if (yr1totalgst < yr2totalgst) {     // if year 1 is lower

             if (yr1totalgst < 1) {

                out.println("<td bgcolor=\"#CCCCAA\"> +" +yr2totalgst+ " (+100%) </td>");

             } else {
                   
                if ((((yr2totalgst - yr1totalgst) * 100)/yr1totalgst) < 1) {

                   out.println("<td bgcolor=\"#CCCCAA\"> +" +(yr2totalgst - yr1totalgst)+ " (+<1%) </td>");

                } else {

                   out.println("<td bgcolor=\"#CCCCAA\"> +" +(yr2totalgst - yr1totalgst)+ " (+" +(((yr2totalgst - yr1totalgst) * 100)/yr1totalgst)+ "%) </td>");
                }
             }

          } else {                // year 1 is more than year 2

             if ((((yr1totalgst - yr2totalgst) * 100)/yr1totalgst) < 1) {

                out.println("<td bgcolor=\"#CCCCAA\"> -" +(yr1totalgst - yr2totalgst)+ " (-<1%) </td>");

             } else {

                out.println("<td bgcolor=\"#CCCCAA\"> -" +(yr1totalgst - yr2totalgst)+ " (-" +(((yr1totalgst - yr2totalgst) * 100)/yr1totalgst)+ "%) </td>");
             }
          }             
       }
       
       out.println("</tr>");

       
       
       //  'Other' Comparisons
       
       out.println("<tr align=\"center\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Other Rounds (Unkown)</b></td>");
       
       mm1 = month1;
       mm2 = month2;
       
       for (i=1; i<13; i++) {                       // do each month
          
          if (mm1 > 12) mm1 = 1;           // wrap the month if necessary

          if (mm2 > 12) mm2 = 1;

          if (year1otherA[mm1] < 1 && year2otherA[mm2] < 1) {     // if both months are zero
          
             out.println("<td> - </td>");
             
          } else {
             
             if (year1otherA[mm1] < year2otherA[mm2]) {     // if year 1 is lower

                if (year1otherA[mm1] < 1) {
                   
                   out.println("<td> +" +year2otherA[mm2]+ " (+100%) </td>");

                } else {
                   
                   if ((((year2otherA[mm2] - year1otherA[mm1]) * 100)/year1otherA[mm1]) < 1) {

                      out.println("<td> +" +(year2otherA[mm2] - year1otherA[mm1])+ " (+<1%) </td>");

                   } else {

                      out.println("<td> +" +(year2otherA[mm2] - year1otherA[mm1])+ " (+" +(((year2otherA[mm2] - year1otherA[mm1]) * 100)/year1otherA[mm1])+ "%) </td>");
                   }
                }
                
             } else {                // year 1 is more than year 2
             
                if ((((year1otherA[mm1] - year2otherA[mm2]) * 100)/year1otherA[mm1]) < 1) {

                   out.println("<td> -" +(year1otherA[mm1] - year2otherA[mm2])+ " (-<1%) </td>");

                } else {
             
                   out.println("<td> -" +(year1otherA[mm1] - year2otherA[mm2])+ " (-" +(((year1otherA[mm1] - year2otherA[mm2]) * 100)/year1otherA[mm1])+ "%) </td>");
                }
             }             
          }  
          
          mm1++;
          mm2++;
       }
          
       //  do total 
       
       if (yr1totalother < 1 && yr2totalother < 1) {     // if both are zero

          out.println("<td bgcolor=\"#CCCCAA\"> - </td>");

       } else {

          if (yr1totalother < yr2totalother) {     // if year 1 is lower

             if (yr1totalother < 1) {

                out.println("<td bgcolor=\"#CCCCAA\"> +" +yr2totalother+ " (+100%) </td>");

             } else {
                   
                if ((((yr2totalother - yr1totalother) * 100)/yr1totalother) < 1) {

                   out.println("<td bgcolor=\"#CCCCAA\"> +" +(yr2totalother - yr1totalother)+ " (+<1%) </td>");

                } else {

                   out.println("<td bgcolor=\"#CCCCAA\"> +" +(yr2totalother - yr1totalother)+ " (+" +(((yr2totalother - yr1totalother) * 100)/yr1totalother)+ "%) </td>");
                }
             }

          } else {                // year 1 is more than year 2

             if ((((yr1totalother - yr2totalother) * 100)/yr1totalother) < 1) {

                out.println("<td bgcolor=\"#CCCCAA\"> -" +(yr1totalother - yr2totalother)+ " (-<1%) </td>");

             } else {

                out.println("<td bgcolor=\"#CCCCAA\"> -" +(yr1totalother - yr2totalother)+ " (-" +(((yr1totalother - yr2totalother) * 100)/yr1totalother)+ "%) </td>");
             }
          }             
       }
       
       out.println("</tr>");

       
              
       //  Monthly Total Comparisons
       
       out.println("<tr align=\"center\" bgcolor=\"#CCCCAA\" style=\"font-family:verdana;color:black;font-size:.7em\">");
       out.println("<td><b>Totals</b></td>");
       
       mm1 = month1;
       mm2 = month2;
       
       for (i=1; i<13; i++) {                       // do each month
          
          if (mm1 > 12) mm1 = 1;           // wrap the month if necessary

          if (mm2 > 12) mm2 = 1;

          yr1total = year1gstnorevA[mm1] + year1gstrevA[mm1] + year1memA[mm1] + year1otherA[mm1];     // get this month's total for each year
          yr2total = year2gstnorevA[mm2] + year2gstrevA[mm2] + year2memA[mm2] + year2otherA[mm2];
          
          if (yr1total < 1 && yr2total < 1) {     // if both months are zero
          
             out.println("<td> - </td>");
             
          } else {
             
             if (yr1total < yr2total) {     // if year 1 is lower

                if (yr1total < 1) {
                   
                   out.println("<td> +" +yr2total+ " (+100%) </td>");

                } else {
                   
                   if ((((yr2total - yr1total) * 100)/yr1total) < 1) {

                      out.println("<td> +" +(yr2total - yr1total)+ " (+<1%) </td>");

                   } else {

                      out.println("<td> +" +(yr2total - yr1total)+ " (+" +(((yr2total - yr1total) * 100)/yr1total)+ "%) </td>");
                   }
                }
                
             } else {                // year 1 is more than year 2
             
                if ((((yr1total - yr2total) * 100)/yr1total) < 1) {

                   out.println("<td> -" +(yr1total - yr2total)+ " (-<1%) </td>");

                } else {
             
                   out.println("<td> -" +(yr1total - yr2total)+ " (-" +(((yr1total - yr2total) * 100)/yr1total)+ "%) </td>");
                }
             }             
          }  
          
          mm1++;
          mm2++;
       }
          
       //  do Grand Total 
       
       yr1total = yr1totalmem + yr1totalrevgst + yr1totalgst + yr1totalother;       // grand total for year 1
       yr2total = yr2totalmem + yr2totalrevgst + yr2totalgst + yr2totalother;       // grand total for year 2
       
       if (yr1total < 1 && yr2total < 1) {     // if both are zero

          out.println("<td> - </td>");

       } else {

          if (yr1total < yr2total) {     // if year 1 is lower

             if (yr1total < 1) {

                out.println("<td> +" +yr2total+ " (+100%) </td>");

             } else {
                   
                if ((((yr2total - yr1total) * 100)/yr1total) < 1) {

                   out.println("<td> +" +(yr2total - yr1total)+ " (+<1%) </td>");

                } else {

                   out.println("<td> +" +(yr2total - yr1total)+ " (+" +(((yr2total - yr1total) * 100)/yr1total)+ "%) </td>");
                }
             }

          } else {                // year 1 is more than year 2

             if ((((yr1total - yr2total) * 100)/yr1total) < 1) {

                out.println("<td> -" +(yr1total - yr2total)+ " (-<1%) </td>");

             } else {

                out.println("<td> -" +(yr1total - yr2total)+ " (-" +(((yr1total - yr2total) * 100)/yr1total)+ "%) </td>");
             }
          }             
       }
       
       out.println("</tr>");       
       out.println("</table><br><br>");

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
       out.println("<form method=\"post\" action=\"Proshop_report_rounds_comparison\" target=\"_blank\">");
       out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"year1\" value=\"" + year1save + "\">");
       out.println("<input type=\"hidden\" name=\"year2\" value=\"" + year2save + "\">");
       out.println("<input type=\"hidden\" name=\"month1\" value=\"" + month1 + "\">");
       out.println("<input type=\"hidden\" name=\"month2\" value=\"" + month2 + "\">");
       out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
       out.println("<input type=\"hidden\" name=\"continue\" value=\"yes\">");
       out.println("<td>");
       out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
       out.println("</td>");
       out.println("</form>");
       out.println("<form method=\"post\" action=\"Proshop_report_rounds_comparison\">");
       out.println("<input type=\"hidden\" name=\"year1\" value=\"" + year1save + "\">");
       out.println("<input type=\"hidden\" name=\"year2\" value=\"" + year2save + "\">");
       out.println("<input type=\"hidden\" name=\"month1\" value=\"" + month1 + "\">");
       out.println("<input type=\"hidden\" name=\"month2\" value=\"" + month2 + "\">");
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
 private void displayPrompt(int year1, int year2, int month1, int month2, PrintWriter out, parmClub parm, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   
   int oldyear = 0;
   int oldmonth = 0;
   int thisyear = 0;
   int yy = 0;
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

   if (year1 == 0 || year2 == 0) {               // if years not provided
      
      year2 = thisyear;  
      year1 = thisyear;
      year1--;                                   //  year defaults 
   }
   
   // set calendar vars
   int cal_year = cal.get(Calendar.YEAR);
   int cal_month = cal.get(Calendar.MONTH) + 1; // month is zero based
   int cal_day = cal.get(Calendar.DAY_OF_MONTH);

      
   //
   //  Get the oldest tee times for this club to determine how far back they can select
   //
   try {

      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT mm, yy FROM teepast2 ORDER BY date ASC LIMIT 1");

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

   if (oldyear == thisyear) {            // if they don't have more than one year to compare - reject
      
       out.println(SystemUtils.HeadTitle("Procedure Error"));
       out.println("<BODY><CENTER>");
       out.println("<BR><BR><H2>Procedure Error</H2>");
       out.println("<BR><BR>Sorry, we are unable to process this report as there is not enough history for the comparison.");
       out.println("<BR>This report requires more than one year of tee time history.");
       out.println("<BR><BR>Please try again next year.");
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
   
    out.println("<CENTER><BR>");
    out.println("<H2>Rounds Comparison Report</H2>");
    out.println("This report will compare the rounds played, by month, of the 2 years selected below.");
    out.println("<BR><BR>");

    if (parm.multi != 0) {           // if multiple courses supported for this club

       out.println("Please select the 12 month periods to compare and the course.");

    } else {

       out.println("Please select the 12 month periods to compare.");
    }

    out.println("<form method=\"post\" action=\"Proshop_report_rounds_comparison\">");

    out.println("<BR><BR>");
    out.println("First 12 Month Period Beginning:&nbsp;&nbsp;");
    
    out.println("Month:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"month1\">");
    if (month1 == 0 || month1 == 1) {
       out.println("<option selected value=\"01\">JAN</option>");
    } else {
       out.println("<option value=\"01\">JAN</option>");
    }
    if (month1 == 2) {
       out.println("<option selected value=\"02\">FEB</option>");
    } else {
       out.println("<option value=\"02\">FEB</option>");
    }
    if (month1 == 3) {
       out.println("<option selected value=\"03\">MAR</option>");
    } else {
       out.println("<option value=\"03\">MAR</option>");
    }
    if (month1 == 4) {
       out.println("<option selected value=\"04\">APR</option>");
    } else {
       out.println("<option value=\"04\">APR</option>");
    }
    if (month1 == 5) {
       out.println("<option selected value=\"05\">MAY</option>");
    } else {
       out.println("<option value=\"05\">MAY</option>");
    }
    if (month1 == 6) {
       out.println("<option selected value=\"06\">JUN</option>");
    } else {
       out.println("<option value=\"06\">JUN</option>");
    }
    if (month1 == 7) {
       out.println("<option selected value=\"07\">JUL</option>");
    } else {
       out.println("<option value=\"07\">JUL</option>");
    }
    if (month1 == 8) {
       out.println("<option selected value=\"08\">AUG</option>");
    } else {
       out.println("<option value=\"08\">AUG</option>");
    }
    if (month1 == 9) {
       out.println("<option selected value=\"09\">SEP</option>");
    } else {
       out.println("<option value=\"09\">SEP</option>");
    }
    if (month1 == 10) {
       out.println("<option selected value=\"10\">OCT</option>");
    } else {
       out.println("<option value=\"10\">OCT</option>");
    }
    if (month1 == 11) {
       out.println("<option selected value=\"11\">NOV</option>");
    } else {
       out.println("<option value=\"11\">NOV</option>");
    }
    if (month1 == 12) {
       out.println("<option selected value=\"12\">DEC</option>");
    } else {
       out.println("<option value=\"12\">DEC</option>");
    }
    out.println("</select>");
    
    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"year1\">");

    yy = oldyear;        // start with the oldest year

    while (yy < thisyear) {   

       if (yy == year1) {
          out.println("<option selected value=\"" + yy + "\">" + yy + "</option>");
       } else {
          out.println("<option value=\"" + yy + "\">" + yy + "</option>");
       }
       yy++;
    }
    out.println("</select>");

    out.println("<BR><BR>Second 12 Month Period beginning:&nbsp;&nbsp;");
        
    out.println("Month:&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"month2\">");
    if (month2 == 0 || month2 == 1) {
       out.println("<option selected value=\"01\">JAN</option>");
    } else {
       out.println("<option value=\"01\">JAN</option>");
    }
    if (month2 == 2) {
       out.println("<option selected value=\"02\">FEB</option>");
    } else {
       out.println("<option value=\"02\">FEB</option>");
    }
    if (month2 == 3) {
       out.println("<option selected value=\"03\">MAR</option>");
    } else {
       out.println("<option value=\"03\">MAR</option>");
    }
    if (month2 == 4) {
       out.println("<option selected value=\"04\">APR</option>");
    } else {
       out.println("<option value=\"04\">APR</option>");
    }
    if (month2 == 5) {
       out.println("<option selected value=\"05\">MAY</option>");
    } else {
       out.println("<option value=\"05\">MAY</option>");
    }
    if (month2 == 6) {
       out.println("<option selected value=\"06\">JUN</option>");
    } else {
       out.println("<option value=\"06\">JUN</option>");
    }
    if (month2 == 7) {
       out.println("<option selected value=\"07\">JUL</option>");
    } else {
       out.println("<option value=\"07\">JUL</option>");
    }
    if (month2 == 8) {
       out.println("<option selected value=\"08\">AUG</option>");
    } else {
       out.println("<option value=\"08\">AUG</option>");
    }
    if (month2 == 9) {
       out.println("<option selected value=\"09\">SEP</option>");
    } else {
       out.println("<option value=\"09\">SEP</option>");
    }
    if (month2 == 10) {
       out.println("<option selected value=\"10\">OCT</option>");
    } else {
       out.println("<option value=\"10\">OCT</option>");
    }
    if (month2 == 11) {
       out.println("<option selected value=\"11\">NOV</option>");
    } else {
       out.println("<option value=\"11\">NOV</option>");
    }
    if (month2 == 12) {
       out.println("<option selected value=\"12\">DEC</option>");
    } else {
       out.println("<option value=\"12\">DEC</option>");
    }
    out.println("</select>");
    
    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
    
    out.println("<select size=\"1\" name=\"year2\">");

    yy = oldyear + 1;        // start with the oldest year +1

    while (yy <= thisyear) {   

       if (yy == year2) {
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
