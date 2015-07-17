/***************************************************************************************     
 *   Proshop_usereport:  This servlet will process the 'Generate Reports' request from Proshop's
 *                     main page.
 *
 *
 *   called by:  proshop_reports.htm (calls doGet)
 *               self (calls doPost)
 *
 *   created: 1/31/2002   Bob P.
 *
 *   last updated:
 *
 *        7/18/08   Added limited access proshop users checks
 *        9/21/05   Add Excel format option to report.
 *        9/03/05   Add more categories to the 'by age' report.
 *       12/30/03   Change name from Admin_usereport to Proshop_usereport (no longer used by admin)
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/14/03   Enhancements for Version 2 of the software.
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
import com.foretees.common.ProcessConstants;


public class Proshop_usereport extends HttpServlet {
                
 String omit = "";
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //****************************************************
 // Process the form request from doGet above (use doGet)
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   doGet(req, resp);     // call doGet processing
 }


 //*******************************************************
 // Process the initial request from the menu
 //
 //    also, doPost comes here!!!
 //*******************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession session = null;
   Statement stmt = null;
   ResultSet rs = null;

   String fname = "";
   String lname = "";
   String mname = "";
   String i_fname = "";
   String i_lname = "";
   String i_mname = "";
   String user = "";
   String username = "";
   String userThis = "";
   String proshop = "proshop";
 
   int memCount = 0;
   int birth = 0;
   int percent = 0;
   int grandTotal = 0;
   int total1 = 0;   // < 20         number of logins in this age group 
   int total2 = 0;   // 20 - 24 
   int total3 = 0;   // 25 - 29
   int total4 = 0;   // 30 - 34
   int total5 = 0;   // 35 - 39
   int total6 = 0;   // 40 - 44
   int total7 = 0;   // 45 - 49
   int total8 = 0;   // 50 - 54
   int total9 = 0;   // 55 - 59
   int total10 = 0;   // 60 - 64
   int total11 = 0;   // 65 - 69
   int total12 = 0;   // 70 - 74
   int total13 = 0;   // 75 - 79
   int total14 = 0;   // 80 +

   int num1 = 0;   // < 20           number of members in this age group
   int num2 = 0;   // 20 - 24
   int num3 = 0;   // 25 - 29
   int num4 = 0;   // 30 - 34
   int num5 = 0;   // 35 - 39
   int num6 = 0;   // 40 - 44
   int num7 = 0;   // 45 - 49
   int num8 = 0;   // 50 - 54
   int num9 = 0;   // 55 - 59
   int num10 = 0;   // 60 - 64
   int num11 = 0;   // 65 - 69
   int num12 = 0;   // 70 - 74
   int num13 = 0;   // 75 - 79
   int num14 = 0;   // 80 +
   int numTotal = 0;   // 80 +

   int date = 0;    // today        dates to calculate age group
   int date1 = 0;   // -20 yrs
   int date2 = 0;   // -25 yrs
   int date3 = 0;   // -30 yrs
   int date4 = 0;   // -35 yrs
   int date5 = 0;   // -40 yrs
   int date6 = 0;   // -45 yrs
   int date7 = 0;   // -50 yrs
   int date8 = 0;   // -55 yrs
   int date9 = 0;   // -60 yrs
   int date10 = 0;   // -65 yrs
   int date11 = 0;   // -70 yrs
   int date12 = 0;   // -75 yrs
   int date13 = 0;   // -80 yrs

   boolean excel = false;


   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;

   } else {

      userThis = (String)session.getAttribute("user");   // get username

      if (!userThis.startsWith( proshop )) {

         invalidUser(out);            // Intruder - reject
         return;
      }
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (ProcessConstants.isProshopUser(userThis)) {
       if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
           SystemUtils.restrictProshop("REPORTS", out);
           return;
       }
   }

   int all = 0;
   int only = 0;
   int ind = 0;
   int tcount = 0;
   int icount = 0;
   int count = 0;
   int mem_count = 0;
   int use_count = 0;

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);


   //
   // Get the parameters entered (if from doPost)
   //
   if (req.getParameter("all") != null) {              // all members, itemized              

      all = 1;
   }
   if (req.getParameter("only") != null) {              // all members, non-zero counts

      only = 1;
   }
   if (req.getParameter("ind") != null) {              // one member only

      ind = 1;
      username = req.getParameter("username");         //  username of member to count
   }

   try{
      if (req.getParameter("excel") != null) {         // if user requested Excel Spreadsheet Format

         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
         excel = true;                                  
      }
   }
   catch (Exception exc) {
   }

     
   if ((all == 1) || (only == 1)) {                  // if doPost and long output - build separate page

      //
      //  Build the HTML page to prompt user for report options
      //
      out.println(SystemUtils.HeadTitle("Proshop Usage Reports Page"));

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      if (excel == false) {

         out.println("<table border=\"0\" cellpadding=\"8\">");
         out.println("<tr><td align=\"center\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_usereport\" method=\"post\" target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
            if (all == 1) {           
               out.println("<input type=\"hidden\" name=\"all\" value=\"yes\">");
            }
            if (only == 1) {
               out.println("<input type=\"hidden\" name=\"only\" value=\"yes\">");
            }
            out.println("<input type=\"submit\" value=\"Excel Format\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");
         out.println("</td><td align=\"center\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("</td><td align=\"center\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");
         out.println("</td></tr>");
         out.println("</font><br>");
      }

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"8\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_usereport\" method=\"post\">");

         out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"2\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<b>Member Access Report</b></font></td></tr>");

         out.println("<tr bgcolor=\"#336633\"><td align=\"left\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("Member Name</td>");
            out.println("<td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
            out.println("Times Accessed System</td>");
            out.println("</font></td></tr>");

      count = 0;
      tcount = 0;
      mem_count = 0;

      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT name_last, name_first, name_mi, count FROM member2b " +
                                "ORDER BY name_last, name_first, name_mi");

         while (rs.next()) {

            lname = rs.getString(1);
            fname = rs.getString(2);
            mname = rs.getString(3);
            count = rs.getInt(4);
               

            if ((count != 0) || (all == 1)) {                  // skip if usage = 0 and req was 'only'

               tcount = tcount + count;                        // keep total count of usage
               mem_count++;                                    // count members being totaled

                  out.println("<tr><td align=\"left\">");
                  out.println("<font size=\"2\">");
                  out.println( lname + ", " + fname + " " + mname );
                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println( count + "</td></tr>");
            }
         }

         stmt.close();
            
         out.println("<tr><td align=\"right\">");
         out.println("<font size=\"2\">");
           
         if (all == 1) {
           
            out.println("Total for all <b>" + mem_count + "</b> members:&nbsp;&nbsp;");
         } else {
            
            out.println("Total for <b>" + mem_count + "</b> members:&nbsp;&nbsp;");
         }
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<b>" + tcount + "</b></td></tr>");

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;

      }   // end of member name list

      out.println("</font>");
      out.println("</table>");
        
      if (excel == false) {

         out.println("<br>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_usereport\">");
         out.println("<input type=\"submit\" value=\"Return to Reports\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");
         out.println("</font>");
      }
      out.println("</center></font></body></html>");


   } else {        // doGet or short output for doPost
      //
      //  Build the HTML page to prompt Proshop for report options
      //
      out.println(SystemUtils.HeadTitle("Proshop Usage Reports Page"));

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<br><b>Member Login Reports</b><br>");
      out.println("</font>");
      out.println("<br>");

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_usereport\" method=\"post\">");
      out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("Use this report generator to determine who<br>");
      out.println("is using the system, and how often.<br>");
      out.println("</font></td></tr>");

      out.println("<tr><td><br>");
      out.println("<font size=\"2\">");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stats for individual member&nbsp;&nbsp;");
      out.println("<div id=awmobject1>");
      out.println("<select size=\"1\" name=\"username\">&nbsp;&nbsp;");

      //
      // Get a list of members
      //
      try {

         count = 0;
         tcount = 0;
         icount = 0;
         mem_count = 0;
         use_count = 0;
           
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT username, name_last, name_first, name_mi, count FROM member2b " +
                                "ORDER BY name_last, name_first, name_mi");

         while (rs.next()) {

            user = rs.getString(1);
            lname = rs.getString(2);
            fname = rs.getString(3);
            mname = rs.getString(4);
            count = rs.getInt(5);

            tcount = tcount + count;       // keep total count
            mem_count++;                   // count members

            out.println("<option value=" + user + ">" + lname + ", " + fname + " " + mname + "</option>");
              
            if ((ind == 1) && (user.equalsIgnoreCase( username ))) {
             
               icount = count;        // save count and name for this member
               i_lname = lname;
               i_fname = fname;
               i_mname = mname;
            }
            if (count != 0) {
              
               use_count++;          // count all members that have used system at least once
            }
         }

         stmt.close();

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;

      }   // end of member name list

      out.println("</select>");
      out.println("</div>");
      out.println("<input type=\"submit\" name=\"ind\" value=\"Go\"><br><br>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stats for all <b>" + mem_count + "</b> members, itemized&nbsp;&nbsp;");
      out.println("<input type=\"submit\" name=\"all\" value=\"Go\"><br><br>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Stats for all members with non-zero counts&nbsp;&nbsp;");
      out.println("<input type=\"submit\" name=\"only\" value=\"Go\">");

      out.println("</font>");
      out.println("</form>");
      out.println("</td></tr>");
      out.println("<tr><td align=\"center\"><font size=\"2\"><br>");
      out.println("<b>" + use_count + " members</b> have accessed this system a total of <b>" + tcount + "</b> times.<br>");
      out.println("(an average of " + tcount / use_count + " logins per member)<br>");

      if (ind == 1) {            // if individual member display requested

         out.println("</td></tr>");
         out.println("<tr><td align=\"center\"><font size=\"2\"><br>");
         out.println("<b>" + i_fname + " " + i_mname + " " + i_lname + "</b> has accessed this system <b>" + icount + "</b> times.<br>");
      }
      out.println("</td></tr>");
      out.println("</table>");

      //
      //  Display login counts by age if there are any counts to display
      //
      if (tcount > 0) {
        
         //
         //  Get today's date and caluclate birth date values
         //
         Calendar cal = new GregorianCalendar();       // get todays date

         int year = cal.get(Calendar.YEAR);
         int month = cal.get(Calendar.MONTH) + 1;
         int day = cal.get(Calendar.DAY_OF_MONTH);

         date = (year * 10000) + (month * 100) + day;   // create a date field of yyyymmdd

         date1 = date - 200000;
         date2 = date - 250000;
         date3 = date - 300000;
         date4 = date - 350000;
         date5 = date - 400000;
         date6 = date - 450000;
         date7 = date - 500000;
         date8 = date - 550000;
         date9 = date - 600000;
         date10 = date - 650000;
         date11 = date - 700000;
         date12 = date - 750000;
         date13 = date - 800000;

         count = 0;                 // init counter
           
         try {
  
            stmt = con.createStatement();           // create a statement

            rs = stmt.executeQuery("SELECT count, birth FROM member2b WHERE count > 0 AND birth > 0");  // get # of logins

            while (rs.next()) {

               count = rs.getInt(1);
               birth = rs.getInt(2);

               memCount++;              // # of members counted

               if (birth > date1) {                                      // if under 20

                  total1 += count;           // add to total for this age group
                  num1++;                    // count # of members in group
               }
               if (birth <= date1 && birth > date2) {                      // 20 - 24

                  total2 += count;           // add to total for this age group
                  num2++;                    // count # of members in group
               }
               if (birth <= date2 && birth > date3) {                      // 25 - 29

                  total3 += count;           // add to total for this age group
                  num3++;                    // count # of members in group
               }
               if (birth <= date3 && birth > date4) {                      // 30 - 34

                  total4 += count;           // add to total for this age group
                  num4++;                    // count # of members in group
               }
               if (birth <= date4 && birth > date5) {                      // 35 - 39

                  total5 += count;           // add to total for this age group
                  num5++;                    // count # of members in group
               }
               if (birth <= date5 && birth > date6) {                      // 40 - 44

                  total6 += count;           // add to total for this age group
                  num6++;                    // count # of members in group
               }
               if (birth <= date6 && birth > date7) {                      // 45 - 49

                  total7 += count;           // add to total for this age group
                  num7++;                    // count # of members in group
               }
               if (birth <= date7 && birth > date8) {                      // 50 - 54 

                  total8 += count;           // add to total for this age group
                  num8++;                    // count # of members in group
               }
               if (birth <= date8 && birth > date9) {                      // 55 - 59

                  total9 += count;           // add to total for this age group
                  num9++;                    // count # of members in group
               }
               if (birth <= date9 && birth > date10) {                      // 60 - 64

                  total10 += count;           // add to total for this age group
                  num10++;                    // count # of members in group
               }
               if (birth <= date10 && birth > date11) {                      // 65 - 69

                  total11 += count;           // add to total for this age group
                  num11++;                    // count # of members in group
               }
               if (birth <= date11 && birth > date12) {                      // 70 - 74

                  total12 += count;           // add to total for this age group
                  num12++;                    // count # of members in group
               }
               if (birth <= date12 && birth > date13) {                      // 75 - 79

                  total13 += count;           // add to total for this age group
                  num13++;                    // count # of members in group
               }
               if (birth <= date13) {                                       // 80 +

                  total14 += count;           // add to total for this age group
                  num14++;                    // count # of members in group
               }
            }

            stmt.close();

         }
         catch (Exception ignore) {
         }

         grandTotal = total1 + total2 + total3 + total4 + total5 + total6 + total7 + total8 + total9 + total10 + total11 + total12 + total13 + total14;   // Grand Total

         //
         //  Only display the age report if there are numbers to report and 
         //  at least 1/2 of the members that used the system have birth dates entered.
         //
         if (grandTotal > 0 && (memCount > (use_count / 2))) {         // if something to display
           
            out.println("<br><br>");
            out.println("<b>Number of Logins Per Age Group</b>");
            out.println("</font><font size=\"2\">");
            out.println("<br><b>(Only Members With Birth Dates Are Included)</b>");
            out.println("<br><br>");

            out.println("<Table border=\"1\" cellpadding=\"2\" cellspacing=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
            out.println("<tr bgcolor=\"#336633\">");
            out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p>&nbsp;&nbsp;<b>Age Group</b>&nbsp;&nbsp;</p></font></td>");
            out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p>&nbsp;&nbsp;<b># of Members</b>&nbsp;&nbsp;</p></font></td>");
            out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p>&nbsp;&nbsp;<b># of Logins</b>&nbsp;&nbsp;</p></font></td>");
            out.println("<td><font color=\"#FFFFFF\" size=\"2\"><p>&nbsp;&nbsp;<b>% of Total Logins</b>&nbsp;&nbsp;</p></font></td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;Under 20</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num1+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total1+ "</font></td>");
            percent = (total1 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total1 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;20 to 24</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num2+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total2+ "</font></td>");
            percent = (total2 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total2 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;25 to 29</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num3+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total3+ "</font></td>");
            percent = (total3 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total3 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;30 to 34</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num4+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total4+ "</font></td>");
            percent = (total4 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total4 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;35 to 39</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num5+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total5+ "</font></td>");
            percent = (total5 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total5 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;40 to 44</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num6+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total6+ "</font></td>");
            percent = (total6 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total6 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;45 to 49</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num7+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total7+ "</font></td>");
            percent = (total7 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total7 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;50 to 54</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num8+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total8+ "</font></td>");
            percent = (total8 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total8 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;55 to 59</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num9+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total9+ "</font></td>");
            percent = (total9 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total9 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;60 to 64</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num10+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total10+ "</font></td>");
            percent = (total10 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total10 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;65 to 69</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num11+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total11+ "</font></td>");
            percent = (total11 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total11 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;70 to 74</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num12+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total12+ "</font></td>");
            percent = (total12 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total12 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;75 to 79</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num13+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total13+ "</font></td>");
            percent = (total13 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total13 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;80 and over&nbsp;&nbsp;</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +num14+ "</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">" +total14+ "</font></td>");
            percent = (total14 * 100) / grandTotal;   // determine percentage
            if (percent < 1 && total14 > 0) {
               out.println("<td align=\"center\"><font size=\"2\">less than 1%</font></td>");
            } else {
               out.println("<td align=\"center\"><font size=\"2\">" +percent+ "%</font></td>");
            }
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td><font size=\"2\">&nbsp;&nbsp;<b>Totals:</b></font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>" +memCount+ "</b></font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>" +grandTotal+ "</b></font></td>");
            out.println("<td align=\"center\"><font size=\"2\">&nbsp;</font></td>");
            out.println("</td></tr>");

            out.println("</TABLE>");
         }
      }

      out.println("<br><br></font>");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</font></form>");
         out.println("<br>");
      out.println("</center></body></html>");
      out.close();
 
   }  // end of if itemized request 

 }  // end of doGet


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>");
   out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
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
   out.println("</input></form></font>");
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
   out.println("</input></form></font>");
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
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

}
