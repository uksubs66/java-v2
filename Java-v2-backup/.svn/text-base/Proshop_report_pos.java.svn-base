/***************************************************************************************     
 *   Proshop_report_pos:  This servlet will process the 'POS Report' request from the
 *                        Proshop tee sheet (today and old sheets).
 *
 *   Note:  This servlet is called from a new window and will close the window on exit.
 *
 *   called by:  Proshop_sheet
 *               Proshop_oldsheets
 *               self (for excel option)
 *
 *   created: 7/16/2008   Bob P.
 *
 *   last updated:
 *
 *          4/11/14  Added support for advanced PayNow options to be displayed.
 *         12/06/12  Check for history entries to determine if we should list them.  Do not rely on pos flags since charges 
 *                   may have been sent, but failed.
 *          8/22/11  Only display the pos_hist entries that are not waiting for a response (prevents duplicate entries in report).
 *          6/01/11  Fixed null exception when pos_hist price field was blank.
 *          5/31/11  Add Price column for CSG.
 *          6/30/09  Add a check for the date of the old tee sheet and check for a POS 
 *                   value of 1 if prior to the following change.  
 *          6/20/09  Change the value of the pos fields that indicate "charges sent" 
 *                   from 1 to 3 (1 now indicates "processed, but not sent".
 *          8/26/08  Added Limited Access Proshop restrictions
 *          8/12/08  Update getNoShowCount method to query for noshow = 0 or 2 (not 1).
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
import java.math.BigDecimal; 
import java.math.RoundingMode;

import com.foretees.common.Connect;
import com.foretees.common.Utilities;

public class Proshop_report_pos extends HttpServlet {
                
 String omit = "";
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*******************************************************
 // Process the initial request  
 //*******************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
    
    doPost(req, resp);  
 }


 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   HttpSession session = null;
   ResultSet rs = null;
   Statement stmt = null;
   PreparedStatement pstmt = null;
   

   String course = "";
   String courseN = "";
   String memid = "";
   String player = "";
   String item_num = "";
   String item_num_curr = "";
   String item_name = "";
   String price = "";
   String salestax = "";
   String user = "";
   String temp = "";
   String posType = "";
   String dbtable = "";
   String times = "";
   String ampm = "";
   String fbs = "";
   String holess = "";
   
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
 
   String queryString = "";
   String orderBy = "time, course, fb, member_id";

   int player_count = 0;         // total # of players on tee sheet
   int noshow_count = 0;         // # of players NOT checked in
   int sent_count = 0;           // # of individaul charges sent to POS
   int not_sent_count = 0;       // # of players that are checked in but charges NOT sent
   int paid_count = 0;           // # of players Paid at counter
   int paid_count_cc = 0;
   int paid_count_cash = 0;
   int paid_count_check = 0;
   int paid_count_other = 0;
   int paid_count_total = 0;
   int count = 0;
   
   int time = 0;
   int hr = 0;
   int min = 0;
   int fb = 0;
   int holes = 0;
   int paynow = 0;
   int paynow_adv = 0;
   int cols = 0;
   
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;

   long mm = 0;
   long dd = 0;
   long yy = 0;
   long date = 0;

   boolean oldSheet = false;
   boolean dispHist = false;
   boolean dispPayNow = false;
   boolean dispPrice = false;
   boolean excel = false;
   boolean sortByItemNum = req.getParameter("sortByItemNum") != null;
   
   BigDecimal bd = null;
   BigDecimal bd_tax = null;
   BigDecimal bd_total = null;
   BigDecimal bd_sum = null;
   BigDecimal bd_tax_sum = null;
   BigDecimal bd_total_sum = null;
   
   List<Integer> validPayNowOptions = new ArrayList<Integer>();
   validPayNowOptions.add(2);    // Default PayNow - No payment method
   validPayNowOptions.add(4);    // Credit Card
   validPayNowOptions.add(5);    // Cash
   validPayNowOptions.add(6);    // Check
   validPayNowOptions.add(7);    // Other
       
   if (req.getParameter("excel") != null) {
       excel = true;
   }

    
   PrintWriter out = resp.getWriter();
   
   
   session = SystemUtils.verifyPro(req, out);          // check for intruder
   
   if (session == null) {
    
      invalidUser(out);            // Intruder - reject
      return;
   }
   String club = (String)session.getAttribute("club");
    
    // handle excel output
    try{
        if (excel) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\""+club+".xls\"");
        }
    }
    catch (Exception exc) {
    }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
   }

   //
   // Get the parameters entered (if from doPost)
   //
   if (req.getParameter("oldSheet") != null) {              // came from Old Tee Sheets           

      oldSheet = true;
   }
   if (req.getParameter("date") != null) {         

      temp = req.getParameter("date");
      date = Long.parseLong(temp);
   }
   if (req.getParameter("course") != null) {         

      course = req.getParameter("course");
   }


   //  break down date
   yy = date / 10000;
   mm = (date - (yy * 10000)) / 100;
   dd = date - ((yy * 10000) + (mm * 100));
   
   
   dbtable = "teecurr2";          // determine which tee sheet table to use
   
   if (oldSheet == true) {
      
      dbtable = "teepast2";
   }
   
   //      See if club supports the Pay Now pos option
   try {

      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT posType, pos_paynow, pos_paynow_adv FROM club5");  

      if (rs.next()) {

         posType = rs.getString("posType");
         paynow = rs.getInt("pos_paynow");
         paynow_adv = rs.getInt("pos_paynow_adv");
      }
      stmt.close();            

   }
   catch (Exception ignore) {       
   }

   dispPayNow = paynow_adv == 1;
   
   if (posType.equals("ClubSystems Group") || posType.equals("Jonas Generic")) {
       dispPrice = true;
   }
   
   //
   //  Gather some counts for report - get total players (members & guests) on this tee sheet
   //
   for (int i=1; i<6; i++) { 
      
      count = getPlayerCount(i, dbtable, course, date, con);   // get total players in this position
      
      player_count += count;        // total all 5
   }
   
   //
   //  Get total No-Shows on this tee sheet
   //
   for (int i=1; i<6; i++) { 
      
      count = getNoShowCount(i, dbtable, course, date, con);   // get total no-shows in this position
      
      noshow_count += count;        // total all 5
   }
   
   //
   //  Get total # of players that had Charges Sent to POS on this tee sheet
   //
   for (int i=1; i<6; i++) { 
      
      count = getSentCount(i, dbtable, course, date, con);   // get total players that had charges sent
      
      sent_count += count;        // total all 5
   }
   
   //
   //  Get total # of players that DID NOT have Charges Sent to POS on this tee sheet
   //
   for (int i=1; i<6; i++) { 
      
      count = getNotSentCount(i, dbtable, course, date, con);   // get total players that had NO charges sent
      
      not_sent_count += count;        // total all 5
   }
   
   //
   //  Get total # of players that PAID at counter on this tee sheet
   //
   for (int i=1; i<6; i++) { 
      
      paid_count += getPaidCount(i, dbtable, course, date, 2, con);   // get total players that paid at counter
      
      if (dispPayNow) {
          paid_count_cc += getPaidCount(i, dbtable, course, date, 4, con);
          paid_count_cash += getPaidCount(i, dbtable, course, date, 5, con);
          paid_count_check += getPaidCount(i, dbtable, course, date, 6, con);
          paid_count_other += getPaidCount(i, dbtable, course, date, 7, con);
      }
   }
   
   paid_count_total = paid_count + paid_count_cc + paid_count_cash + paid_count_check + paid_count_other;
   
   //
   //   See if there are any POS History entries to show below
   //
   try {
       
        if (sortByItemNum) {
            orderBy = "item_num, time, course, fb, member_id";
        } else {
            orderBy = "time, course, fb, member_id";
        }

        if (course.equals("") || course.equals("-ALL-")) {

            queryString = "SELECT * FROM pos_hist WHERE date = ? AND waiting = 0 ORDER BY " + orderBy;

        } else {

            queryString = "SELECT * FROM pos_hist WHERE date = ? AND course = ? AND waiting = 0 ORDER BY " + orderBy;
        }


        //
        //   Get each charge sent for this date/course
        //
        pstmt = con.prepareStatement (queryString);
        pstmt.clearParameters();                  
        pstmt.setLong(1, date); 

        if (!course.equals("") && !course.equals("-ALL-")) {

            pstmt.setString(2, course); 
        }

        rs = pstmt.executeQuery();          

        if (rs.next()) {
            
            dispHist = true;      // entries to display
        }
        pstmt.close();

   } catch (Exception e) {

         String errorMsg = "Error in Proshop_report_pos - get charges sent data. Exception = " + e.getMessage();
         SystemUtils.logError(errorMsg);                                       // log it and continue
   }  
        
        

   //
   //  Build and output the report
   //
   if (!excel) {                // if not Excel 
      
      out.println(SystemUtils.HeadTitle("Proshop POS Reports Page"));
      
   } else {
      
      out.println("<HTML><HEAD><TITLE>Proshop POS Reports Page</TITLE></HEAD>");
   }

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"3\">");
   out.println("<b>Daily POS Report</b>");
   out.println("</font><font size=\"2\"><BR><BR>");
   out.println("This report provides information pertaining to POS charges for the selected date.<br>");
   
   if (dispPrice) {
       out.println("<br><span style=\"font-weight:bold\">Note:</span> sales tax that accompanied a charge will be included in the displayed price.<br>");
       
       if (date < 20140717) {
           out.println("<br>For charges sent <span style=\"font-weight:bold\">prior to 7/17/2014</span>, sales tax may not be included on this report.");
       }
   }
   out.println("</font></td></tr></table><br>");

   out.println("<br>Date: <b>" +mm+ "/" +dd+ "/" +yy+ "</b>");
   if (!course.equals("")) {
      out.println("&nbsp;&nbsp;&nbsp;Course: <b>" +course+ "</b>");
   }
   out.println("<br><br>");
   out.println("</font><font size=\"2\">");

   if (!excel) {                // if not Excel 
      
      out.println("<table border=\"0\" align=\"center\"><tr><td align=\"center\">");
      out.println("<form method=\"link\" action=\"javascript:self.print()\">");
      out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Report</button>");
      out.println("</form></td>");
      out.println("<td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");     // gap between buttons
      out.println("</td>");
      out.println("<td align=\"left\"><form>");
      out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" value=\"Close\" onClick='self.close()' alt=\"Close\">");
      out.println("</form></td>");
      out.println("<td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");     // gap between buttons
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<form method=\"post\" action=\"Proshop_report_pos\">");
      out.println("<input type=\"hidden\" name=\"posType\" value=\"" +posType+ "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
      if (!course.equals("")) {
         out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
      }
      if (oldSheet) {
         out.println("<input type=\"hidden\" name=\"oldSheet\" value=\"yes\">");
      }
      if (sortByItemNum) {
          out.println("<input type=\"hidden\" name=\"sortByItemNum\">");
      }
      out.println("<input type=\"submit\" name=\"excel\" value=\"Export To Excel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></td>");
      out.println("<td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");     // gap between buttons
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<form method=\"post\" action=\"Proshop_report_pos\">");
      out.println("<input type=\"hidden\" name=\"posType\" value=\"" +posType+ "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
      if (!course.equals("")) {
         out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
      }
      if (oldSheet) {
         out.println("<input type=\"hidden\" name=\"oldSheet\" value=\"yes\">");
      }
      if (dispPrice) {
          if (sortByItemNum) {
              out.println("<input type=\"submit\" name=\"sortByTime\" value=\"Sort By Time\" style=\"text-decoration:underline; background:#8B8970\">");
          } else {
              out.println("<input type=\"submit\" name=\"sortByItemNum\" value=\"Sort By Item Number\" style=\"text-decoration:underline; background:#8B8970\">");
          }
      }
      out.println("</form></td></tr></table>");
   }
   
   
   //
   //  Display counts here
   //
   out.println("<br>");
   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
   out.println("<tr><td align=\"center\" colspan=\"2\" bgcolor=\"#336633\">");
   out.println("<font size=\"2\" color=\"#FFFFFF\">General Information For This Date");
   out.println("</font></td></tr>");
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\">" +player_count );
   out.println("</font></td>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">Total number of players (members & guests).");
   out.println("</font></td></tr>");
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\">" +sent_count );
   out.println("</font></td>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">Number of players that had charges sent to POS.");
   out.println("</font></td></tr>");
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\">" +not_sent_count );
   out.println("</font></td>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">Number of players checked in, but charges NOT sent to POS.");
   if (paynow == 1) {
      out.println("<br>Note: Includes those that Paid at counter.");
   }
   out.println("</font></td></tr>");
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\">" +noshow_count );
   out.println("</font></td>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">Number of players NOT checked in.");
   out.println("</font></td></tr>");
   if (paynow == 1) {
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">" +paid_count_total );
      out.println("</font></td>");
      out.println("<td align=\"left\">");
      out.println("<font size=\"2\">" + (dispPayNow ? "TOTAL " : "") + "Number of players PAID at counter.");
      out.println("</font></td></tr>");
      
      if (dispPayNow) {
          if (paid_count != 0) {
              out.println("<tr><td align=\"center\">");
              out.println("<font size=\"2\">" +paid_count );
              out.println("</font></td>");
              out.println("<td align=\"left\">");
              out.println("<font size=\"2\">Number of players PAID at counter (Unspecified).");
              out.println("</font></td></tr>");
          }
          if (paid_count_cc != 0) {
              out.println("<tr><td align=\"center\">");
              out.println("<font size=\"2\">" +paid_count_cc );
              out.println("</font></td>");
              out.println("<td align=\"left\">");
              out.println("<font size=\"2\">Number of players PAID at counter (Credit Card).");
              out.println("</font></td></tr>");
          }
          if (paid_count_cash != 0) {
              out.println("<tr><td align=\"center\">");
              out.println("<font size=\"2\">" +paid_count_cash );
              out.println("</font></td>");
              out.println("<td align=\"left\">");
              out.println("<font size=\"2\">Number of players PAID at counter (Cash).");
              out.println("</font></td></tr>");
          }
          if (paid_count_check != 0) {
              out.println("<tr><td align=\"center\">");
              out.println("<font size=\"2\">" +paid_count_check );
              out.println("</font></td>");
              out.println("<td align=\"left\">");
              out.println("<font size=\"2\">Number of players PAID at counter (Check).");
              out.println("</font></td></tr>");
          }
          if (paid_count_other != 0) {
              out.println("<tr><td align=\"center\">");
              out.println("<font size=\"2\">" +paid_count_other );
              out.println("</font></td>");
              out.println("<td align=\"left\">");
              out.println("<font size=\"2\">Number of players PAID at counter (Other).");
              out.println("</font></td></tr>");
          }
      }
   }
   out.println("</table>");
   out.println("<br><br>");

   //
   //   Display the charges sent for this date/course
   //
   if (dispHist == true) {
      
      cols = 11;                 // # of columns if all used

      if (course.equals("")) {   // if course name not needed

         cols--;
      }

      if (!posType.equals( "IBS" ) && !dispPrice) {                // if not IBS 

         cols--;    // subtract one for "Price" column
         
         if (!dispPrice) {
             cols -= 2;    // subtract 2 more for "Base Price" and "Sales Tax" columns
         }
      }  

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");    // report table

      out.println("<tr><td bgcolor=\"#336633\" colspan=\"" +cols+ "\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Individual Charges Sent to POS</b></font></td></tr>");

      out.println("<tr>");       // heading row
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Time</b></font></td>");
      if (course.equals("-ALL-")) {
         out.println("<td bgcolor=\"#336633\" align=left><font size=\"2\" color=\"FFFFFF\"><b>Course</b></font></td>");
      }
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>F/B</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Holes</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Mem ID</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=left><font size=\"2\" color=\"FFFFFF\"><b>Player</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Item #</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=left><font size=\"2\" color=\"FFFFFF\"><b>Description</b></font></td>");
      if (posType.equals("IBS") || dispPrice) {    // if IBS or CSG 
          out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Price</b></font></td>");
          
          if (dispPrice) {
              out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Sales Tax</b></font></td>");
              out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Total</b></font></td>");
          }
      }
      out.println("</tr>");

      try {
         
         //
         //   Get each charge sent for this date/course
         //
         pstmt = con.prepareStatement (queryString);

         pstmt.clearParameters();                  
         pstmt.setLong(1, date); 

         if (!course.equals("") && !course.equals("-ALL-")) {
 
            pstmt.setString(2, course); 
         }

         rs = pstmt.executeQuery();          

         while (rs.next()) {

            time = rs.getInt("time");
            courseN = rs.getString("course");
            fb = rs.getInt("fb");
            memid = rs.getString("member_id");
            player = rs.getString("player");
            item_num = rs.getString("item_num");
            item_name = rs.getString("item_name");
            price = rs.getString("price");
            salestax = rs.getString("salestax").trim();
            holes = rs.getInt("p9");
            
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
            if (min < 10) {
               times = hr + ":0" + min + ampm;
            } else {
               times = hr + ":" + min + ampm;
            }

            fbs = "F";
            if (fb > 0) {
               fbs = "B";
            }
            
            holess = "18";
            if (holes > 0) {
               holess = "9";
               
               if (item_name.equals("Greens Fee") || item_name.equals("Guest Fee")) {
                   item_name += " 9";
               } else {
                   item_name += "9";
               }
            }
            
            if (price.equals("")) {
                price = "0.00";
            }

            if (dispPrice) {        // if price is set to be displayed 
            
               price = price.trim();        // remove any possible spaces
               
               bd = new BigDecimal(price);    // convert fee to a big decimal value
               bd = bd.setScale(2, RoundingMode.HALF_UP);   // force value to 2 decimal digits, rounded up
               
               // if tax was included for this charge, add it to the price to be displayed
               if (!salestax.equals("")) {
                   bd_tax = new BigDecimal(salestax);
                   bd_tax = bd_tax.setScale(2, RoundingMode.HALF_UP);
                   
                   bd_total = bd.add(bd_tax);
               } else {
                   bd_tax = new BigDecimal("0.00");
                   bd_tax = bd_tax.setScale(2, RoundingMode.HALF_UP);
                   
                   bd_total = new BigDecimal(bd.doubleValue());
               }
               
               if (sortByItemNum) {
                  
                   if (item_num_curr.equals(item_num)) {
                       bd_sum = bd_sum.add(bd);
                       bd_tax_sum = bd_tax_sum.add(bd_tax);
                       bd_total_sum = bd_total_sum.add(bd_total);
                   } else {
                       
                       if (!item_num_curr.equals("") && bd_sum != null && bd_tax_sum != null && bd_total_sum != null) {
                           out.println("<tr>");       // heading row
                           out.println("<td colspan=\"" + (cols - 3) + "\" style=\"text-align:right; font-weight:bold;\">Totals:</td>");
                           out.println("<td style=\"text-align:center; font-weight:bold;\">$" + bd_sum + "</td>");
                           out.println("<td style=\"text-align:center; font-weight:bold;\">$" + bd_tax_sum + "</td>");
                           out.println("<td style=\"text-align:center; font-weight:bold;\">$" + bd_total_sum + "</td>");
                           out.println("</tr>");
                       }
                       
                       item_num_curr = item_num;
                       
                       bd_sum = new BigDecimal(bd.doubleValue());
                       bd_sum = bd_sum.setScale(2, RoundingMode.HALF_UP);
                       
                       bd_tax_sum = new BigDecimal(bd_tax.doubleValue());
                       bd_tax_sum = bd_tax_sum.setScale(2, RoundingMode.HALF_UP);
                       
                       bd_total_sum = new BigDecimal(bd_total.doubleValue());
                       bd_total_sum = bd_total_sum.setScale(2, RoundingMode.HALF_UP);
                   }
               }
            }
            
            
            //
            //  output the row of data
            //
            out.println("<tr>");       // heading row
            out.println("<td align=center><font size=\"2\">" + times + "</font></td>");
            if (course.equals("-ALL-")) {
               out.println("<td align=left><font size=\"2\">" + courseN + "</font></td>");
            }
            out.println("<td align=center><font size=\"2\">" + fbs + "</font></td>");
            out.println("<td align=center><font size=\"2\">" + holess + "</font></td>");
            out.println("<td align=center><font size=\"2\">" + memid + "</font></td>");
            out.println("<td align=left><font size=\"2\">" + player + "</font></td>");
            out.println("<td align=center><font size=\"2\">" + item_num + "</font></td>");
            out.println("<td align=left><font size=\"2\">" + item_name + "</font></td>");
            if (posType.equals( "IBS" )) {               // if IBS
               out.println("<td align=center><font size=\"2\">" + price + "</font></td>"); 
            } else if (dispPrice) {        // if CSG 
               out.println("<td align=center><font size=\"2\">$" + bd + "</font></td>");
               out.println("<td align=center><font size=\"2\">$" + bd_tax + "</font></td>");
               out.println("<td align=center><font size=\"2\">$" + bd_total + "</font></td>");
            }
            out.println("</tr>");
            
         }
         pstmt.close();
         
         if (sortByItemNum && bd_sum != null && bd_tax_sum != null && bd_total_sum != null) {
             
             out.println("<tr>");       // heading row
             out.println("<td colspan=\"" + (cols - 3) + "\" style=\"text-align:right; font-weight:bold;\">Totals:</td>");
             out.println("<td style=\"text-align:center; font-weight:bold;\">$" + bd_sum + "</td>");
             out.println("<td style=\"text-align:center; font-weight:bold;\">$" + bd_tax_sum + "</td>");
             out.println("<td style=\"text-align:center; font-weight:bold;\">$" + bd_total_sum + "</td>");
             out.println("</tr>");
         }

      }
      catch (Exception e) {

         String errorMsg = "Error in Proshop_report_pos - get charges sent data. Exception = " + e.getMessage();
         SystemUtils.logError(errorMsg);                                       // log it and continue
      }  
   
      out.println("</TABLE>");               // done with report
      out.println("<br><br>");   
   }
   
   
   //
   //  Display table of PAID players if any
   //
   if (paynow == 1 && paid_count > 0) {
      
      cols = 7;                 // # of columns if all used

      if (course.equals("")) {   // if course name not needed

         cols--;
      }

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");    // report table

      out.println("<tr><td bgcolor=\"#336633\" colspan=\"" +cols+ "\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Players That Paid at Counter</b></font></td></tr>");

      out.println("<tr>");       // heading row
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Time</b></font></td>");
      if (course.equals("-ALL-")) {
         out.println("<td bgcolor=\"#336633\" align=left><font size=\"2\" color=\"FFFFFF\"><b>Course</b></font></td>");
      }
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>F/B</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Holes</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=center><font size=\"2\" color=\"FFFFFF\"><b>Mem ID</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=left><font size=\"2\" color=\"FFFFFF\"><b>Player</b></font></td>");
      out.println("<td bgcolor=\"#336633\" align=left><font size=\"2\" color=\"FFFFFF\"><b>Mode of Trans</b></font></td>");
      if (dispPayNow) out.println("<td bgcolor=\"#336633\" align=left><font size=\"2\" color=\"FFFFFF\"><b>Payment Method</b></font></td>");
      out.println("</tr>");

      if (course.equals("") || course.equals("-ALL-")) {

         queryString = "SELECT time, player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, fb, player5, p5cw, " +
                       "courseName, mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, " +
                       "p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5 " +
                       "FROM " +dbtable+ " " +
                       "WHERE date = ? AND (pos1 IN (2,4,5,6,7) OR pos2 IN (2,4,5,6,7) OR pos3 IN (2,4,5,6,7) OR pos4 IN (2,4,5,6,7) OR pos5 IN (2,4,5,6,7)) " +
                       "ORDER BY time, courseName, fb";
      } else {
         
         queryString = "SELECT time, player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, fb, player5, p5cw, " +
                       "courseName, mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, " +
                       "p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5 " +
                       "FROM " +dbtable+ " " +
                       "WHERE date = ? AND courseName = ? AND (pos1 IN (2,4,5,6,7) OR pos2 IN (2,4,5,6,7) OR pos3 IN (2,4,5,6,7)OR pos4 IN (2,4,5,6,7) OR pos5 IN (2,4,5,6,7)) " +
                       "ORDER BY time, courseName, fb";
      }
   
      try {
         
         //
         //   Get each charge sent for this date/course
         //
         pstmt = con.prepareStatement (queryString);

         pstmt.clearParameters();                  
         pstmt.setLong(1, date); 

         if (!course.equals("") && !course.equals("-ALL-")) {
 
            pstmt.setString(2, course); 
         }

         rs = pstmt.executeQuery();          

         while (rs.next()) {

            time = rs.getInt("time");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            fb = rs.getInt("fb");
            player5 = rs.getString("player5");
            p5cw = rs.getString("p5cw");
            courseN = rs.getString("courseName");
            mNum1 = rs.getString("mNum1");
            mNum2 = rs.getString("mNum2");
            mNum3 = rs.getString("mNum3");
            mNum4 = rs.getString("mNum4");
            mNum5 = rs.getString("mNum5");
            userg1 = rs.getString("userg1");
            userg2 = rs.getString("userg2");
            userg3 = rs.getString("userg3");
            userg4 = rs.getString("userg4");
            userg5 = rs.getString("userg5");
            p91 = rs.getInt("p91");
            p92 = rs.getInt("p92");
            p93 = rs.getInt("p93");
            p94 = rs.getInt("p94");
            p95 = rs.getInt("p95");
            pos1 = rs.getInt("pos1");
            pos2 = rs.getInt("pos2");
            pos3 = rs.getInt("pos3");
            pos4 = rs.getInt("pos4");
            pos5 = rs.getInt("pos5");
            
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
            if (min < 10) {
               times = hr + ":0" + min + ampm;
            } else {
               times = hr + ":" + min + ampm;
            }

            fbs = "F";
            if (fb > 0) {
               fbs = "B";
            }
            
            //
            // check each player for PAID
            //
            if (validPayNowOptions.contains(pos1)) {
            
               holess = "18";
               if (p91 > 0) {
                  holess = "9";
               }
            
               if (mNum1.equals( "" )) {       // if not member
                  
                  if (!userg1.equals( "" )) {    // if associated member
                     
                     mNum1 = getmNum(userg1, con);     // get the member number for this member
                  }
               }
               
               // output the row
               printRow(times, courseN, fbs, holess, mNum1, player1, p1cw, course, pos1, dispPayNow, out);
            }
               
            if (validPayNowOptions.contains(pos2)) {
            
               holess = "18";
               if (p92 > 0) {
                  holess = "9";
               }
            
               if (mNum2.equals( "" )) {       // if not member
                  
                  if (!userg2.equals( "" )) {    // if associated member
                     
                     mNum2 = getmNum(userg2, con);     // get the member number for this member
                  }
               }
               
               // output the row
               printRow(times, courseN, fbs, holess, mNum2, player2, p2cw, course, pos2, dispPayNow, out);
            }
               
            if (validPayNowOptions.contains(pos3)) {
            
               holess = "18";
               if (p93 > 0) {
                  holess = "9";
               }
            
               if (mNum3.equals( "" )) {       // if not member
                  
                  if (!userg3.equals( "" )) {    // if associated member
                     
                     mNum3 = getmNum(userg3, con);     // get the member number for this member
                  }
               }
               
               // output the row
               printRow(times, courseN, fbs, holess, mNum3, player3, p3cw, course, pos3, dispPayNow, out);
            }
               
            if (validPayNowOptions.contains(pos4)) {
            
               holess = "18";
               if (p94 > 0) {
                  holess = "9";
               }
            
               if (mNum4.equals( "" )) {       // if not member
                  
                  if (!userg4.equals( "" )) {    // if associated member
                     
                     mNum4 = getmNum(userg4, con);     // get the member number for this member
                  }
               }
               
               // output the row
               printRow(times, courseN, fbs, holess, mNum4, player4, p4cw, course, pos4, dispPayNow, out);
            }
               
            if (validPayNowOptions.contains(pos5)) {
            
               holess = "18";
               if (p95 > 0) {
                  holess = "9";
               }
            
               if (mNum5.equals( "" )) {       // if not member
                  
                  if (!userg5.equals( "" )) {    // if associated member
                     
                     mNum5 = getmNum(userg5, con);     // get the member number for this member
                  }
               }
               
               // output the row
               printRow(times, courseN, fbs, holess, mNum5, player5, p5cw, course, pos5, dispPayNow, out);
            }
               
         }
         pstmt.close();

      }
      catch (Exception e) {

         String errorMsg = "Error in Proshop_report_pos - get charges sent data. Exception = " + e.getMessage();
         SystemUtils.logError(errorMsg);                                       // log it and continue
      }  
   
      out.println("</TABLE>");               // done with report
      out.println("<br><br>");   
   }
   
   

   if (!excel) {                // if not Excel 
      
      out.println("<table border=\"0\" align=\"center\"><tr><td align=\"center\">");
      out.println("<form method=\"link\" action=\"javascript:self.print()\">");
      out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Report</button>");
      out.println("</form></td>");
      out.println("<td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");     // gap between buttons
      out.println("</td>");
      out.println("<td align=\"left\"><form>");
      out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" value=\"Close\" onClick='self.close()' alt=\"Close\">");
      out.println("</form></td>");
      out.println("<td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");     // gap between buttons
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<form method=\"post\" action=\"Proshop_report_pos\">");
      out.println("<input type=\"hidden\" name=\"posType\" value=\"" +posType+ "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
      if (!course.equals("")) {
         out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
      }
      if (oldSheet) {
         out.println("<input type=\"hidden\" name=\"oldSheet\" value=\"yes\">");
      }
      if (sortByItemNum) {
          out.println("<input type=\"hidden\" name=\"sortByItemNum\">");
      }
      out.println("<input type=\"submit\" name=\"excel\" value=\"Export To Excel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></td>");
      out.println("<td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");     // gap between buttons
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<form method=\"post\" action=\"Proshop_report_pos\">");
      out.println("<input type=\"hidden\" name=\"posType\" value=\"" +posType+ "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
      if (!course.equals("")) {
         out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
      }
      if (oldSheet) {
         out.println("<input type=\"hidden\" name=\"oldSheet\" value=\"yes\">");
      }
      if (dispPrice) {
          if (sortByItemNum) {
              out.println("<input type=\"submit\" name=\"sortByTime\" value=\"Sort By Time\" style=\"text-decoration:underline; background:#8B8970\">");
          } else {
              out.println("<input type=\"submit\" name=\"sortByItemNum\" value=\"Sort By Item Number\" style=\"text-decoration:underline; background:#8B8970\">");
          }
      }
      out.println("</form></td></tr></table>");
   }
   
   out.println("</center></body></html>");
   out.close();
 
 }  // end of doPost

 
      
 // *********************************************************
 //   Get total players in player specified position and tee sheet
 // *********************************************************
 private int getPlayerCount(int i, String dbtable, String course, long date, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   String queryString = "";
   
   int p_count = 0;
   

   if (course.equals("") || course.equals("-ALL-")) {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X'";

   } else {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND courseName = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X'";
   }
   
   
   try {
      
      pstmt = con.prepareStatement (queryString);

      pstmt.clearParameters();                  
      pstmt.setLong(1, date); 

      if (!course.equals("") && !course.equals("-ALL-")) {

         pstmt.setString(2, course); 
      }

      rs = pstmt.executeQuery();          

      while (rs.next()) {

         p_count = rs.getInt("COUNT(*)");
      }
      pstmt.close();

   }
   catch (Exception e) {

      String errorMsg = "Error in Proshop_report_pos.getPlayerCount. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }  
   
   return(p_count);
 }

      
 // *********************************************************
 //   Get total no-shows in player specified position and tee sheet
 // *********************************************************
 private int getNoShowCount(int i, String dbtable, String course, long date, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   String queryString = "";
   
   int p_count = 0;
   

   if (course.equals("") || course.equals("-ALL-")) {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X' " +
                    "AND show" +i+ " != 1";

   } else {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND courseName = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X' " +
                    "AND show" +i+ " != 1";
   }
   
   
   try {
      
      pstmt = con.prepareStatement (queryString);

      pstmt.clearParameters();                  
      pstmt.setLong(1, date); 

      if (!course.equals("") && !course.equals("-ALL-")) {

         pstmt.setString(2, course); 
      }

      rs = pstmt.executeQuery();          

      while (rs.next()) {

         p_count = rs.getInt("COUNT(*)");
      }
      pstmt.close();

   }
   catch (Exception e) {

      String errorMsg = "Error in Proshop_report_pos.getNoShowCount. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }  
   
   return(p_count);
 }

      
 // *********************************************************
 //   Get # of players that had Charges Sent for player specified position and tee sheet
 // *********************************************************
 private int getSentCount(int i, String dbtable, String course, long date, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   String queryString = "";
   
   int p_count = 0;
   int pos_value = 3;      // new POS value to indicate that charges were sent
   
   if (date < 20090626) {
      
      pos_value = 1;       // use old value (pos sent indicator)
   }
   

   if (course.equals("") || course.equals("-ALL-")) {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X' " +
                    "AND pos" +i+ " = ?";

   } else {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND courseName = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X' " +
                    "AND pos" +i+ " = ?";
   }
   
   
   try {
      
      pstmt = con.prepareStatement (queryString);

      pstmt.clearParameters();                  
      pstmt.setLong(1, date); 

      if (!course.equals("") && !course.equals("-ALL-")) {

         pstmt.setString(2, course); 
         pstmt.setInt(3, pos_value); 
         
      } else {
         
         pstmt.setInt(2, pos_value); 
      }

      rs = pstmt.executeQuery();          

      while (rs.next()) {

         p_count = rs.getInt("COUNT(*)");
      }
      pstmt.close();

   }
   catch (Exception e) {

      String errorMsg = "Error in Proshop_report_pos.getSentCount. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }  
   
   return(p_count);
 }

      
 // *********************************************************
 //   Get count of players checked in but charges NOT sent for specified position and tee sheet
 // *********************************************************
 private int getNotSentCount(int i, String dbtable, String course, long date, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   String queryString = "";
   
   int p_count = 0;
   int pos_value = 3;      // new POS value to indicate that charges were sent
   
   if (date < 20090626) {
      
      pos_value = 1;       // use old value (pos sent indicator)
   }
   
   

   if (course.equals("") || course.equals("-ALL-")) {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X' " +
                    "AND show" +i+ " = 1 AND pos" +i+ " != ?";

   } else {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND courseName = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X' " +
                    "AND show" +i+ " = 1 AND pos" +i+ " != ?";
   }
   
   
   try {
      
      pstmt = con.prepareStatement (queryString);

      pstmt.clearParameters();                  
      pstmt.setLong(1, date); 

      if (!course.equals("") && !course.equals("-ALL-")) {

         pstmt.setString(2, course); 
         pstmt.setInt(3, pos_value); 
         
      } else {
         
         pstmt.setInt(2, pos_value); 
      }

      rs = pstmt.executeQuery();          

      while (rs.next()) {

         p_count = rs.getInt("COUNT(*)");
      }
      pstmt.close();

   }
   catch (Exception e) {

      String errorMsg = "Error in Proshop_report_pos.getNotSentCount. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }  
   
   return(p_count);
 }

      
 // *********************************************************
 //   Get count of players PAID at counter for specified position and tee sheet
 // *********************************************************
 private int getPaidCount(int i, String dbtable, String course, long date, int posVal, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   String queryString = "";
   
   int p_count = 0;
   

   /* For PayNow Options:
    * 
    *  2 - Default PayNow - No payment method indicated
    *  4 - Credit Card
    *  5 - Cash
    *  6 - Check
    *  7 - Other
    * 
    *  Other, non PayNow values
    * 
    *  0 - Charges not sent
    *  1 - Charges sent, no charges to process for this member
    *  3 - Charges sent & processed
    */
   
   if (course.equals("") || course.equals("-ALL-")) {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X' " +
                    "AND pos" +i+ " = ?";

   } else {

      queryString = "SELECT COUNT(*) FROM " +dbtable+ " WHERE date = ? AND courseName = ? AND " +
                    "player" +i+ " != '' AND player" +i+ " != 'x' AND player" +i+ " != 'X' " +
                    "AND pos" +i+ " = ?";
   }
   
   
   try {
      
      pstmt = con.prepareStatement (queryString);

      pstmt.clearParameters();                  
      pstmt.setLong(1, date); 

      if (!course.equals("") && !course.equals("-ALL-")) {

         pstmt.setString(2, course); 
         pstmt.setInt(3, posVal);
         
      } else {
          
         pstmt.setInt(2, posVal);
      }

      rs = pstmt.executeQuery();          

      while (rs.next()) {

         p_count = rs.getInt("COUNT(*)");
      }
      pstmt.close();

   }
   catch (Exception e) {

      String errorMsg = "Error in Proshop_report_pos.getPaidCount. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }  
   
   return(p_count);
 }

      
 // *********************************************************
 //   Get mNum or posid of member for a guest
 // *********************************************************
 private String getmNum(String user, Connection con) {


   PreparedStatement pstmtc = null;
   ResultSet rs = null;
   
   String mNum = "";
   String posid = "";
   
   
   try {
      
      pstmtc = con.prepareStatement (
         "SELECT memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         mNum = rs.getString(1);
         posid = rs.getString(2);
      }
      pstmtc.close();
      
      if (!posid.equals("")) {
         
         mNum = posid;             // return posid if present
      }

   }
   catch (Exception e) {

      String errorMsg = "Error in Proshop_report_pos.getmNum. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }  
   
   return(mNum);
 }

      
 // *********************************************************
 //   Print a row for PAID table
 // *********************************************************
 private void printRow(String times, String courseN, String fbs, String holess, String memid, 
                       String player, String pcw, String course, int pos, boolean dispPayNow, PrintWriter out) {

   String payment_method = "";
   
   switch (pos) {
       case 2:
           payment_method = "N/A";
           break;
       case 4:
           payment_method = "Credit Card";
           break;
       case 5:
           payment_method = "Cash";
           break;
       case 6:
           payment_method = "Check";
           break;
       case 7:
           payment_method = "Other";
           break;
       default:
           payment_method = "N/A";
           break;
   }
     
   //
   //  output the row of data
   //
   out.println("<tr>");       // heading row
   out.println("<td align=center><font size=\"2\">" +times+ "</font></td>");
   if (course.equals("-ALL-")) {
      out.println("<td align=left><font size=\"2\">" +courseN+ "</font></td>");
   }
   out.println("<td align=center><font size=\"2\">" +fbs+ "</font></td>");
   out.println("<td align=center><font size=\"2\">" +holess+ "</font></td>");
   out.println("<td align=center><font size=\"2\">" +memid+ "</font></td>");
   out.println("<td align=left><font size=\"2\">" +player+ "</font></td>");
   out.println("<td align=center><font size=\"2\">" +pcw+ "</font></td>");
   if (dispPayNow) out.println("<td align=center><font size=\"2\">" +payment_method+ "</font></td>");
   out.println("</tr>");

 }

      
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>");
   out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data is missing or invalid.<BR>");
   out.println("<BR>You must select at least one option.<BR>");
   out.println("If you select 'Individual Member', then you must also select the member name.<BR>");
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
   out.println("<BR><BR>Sorry, the member you specified does not exist in the database.<BR>");
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
