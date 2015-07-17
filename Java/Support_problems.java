/***************************************************************************************     
 *   Support_problems:  This servlet will process all problem reporting requests from
 *                      sales and support presonnel.
 *
 *
 *   called by:  support_reqMenu.htm
 *               sales_reqMenu.htm
 *
 *   created: 4/18/2005   Bob P.
 *
 *   last updated:
 * 
 *   9/24/08  Added "Reply-To" for email messages
 *   9/22/08  Adjustments to setup
 *   9/17/08  Revamped request system to be used as communication between pro and tech support
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;


public class Support_problems extends HttpServlet {
 
       
   //********************************************************
   // Some constants for emails sent within this class
   //********************************************************
   //
   static String host = SystemUtils.HOST;

   static String efrom = SystemUtils.EFROM;

   static String header = SystemUtils.HEADER;


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 String support = "support";             // valid username
 String sales = "sales";             

 String support1 = "Ben";       
 String support2 = "Brock";
 String dev1 = "Paul";
 String dev2 = "Bob";
 String dev3 = "Brad";
 //String sales1 = "Larry";
 //String sales2 = "Tom";

 String emailSupport1 = "ben@foretees.com";
 String emailSupport2 = "bweiss@foretees.com";
 String emailDev1 = "psindelar@foretees.com";
 String emailDev2 = "bparise@foretees.com";
 String emailDev3 = "brad@foretees.com";
 //String emailSales1 = "brad@foretees.com";
 //String emailSales2 = "brad@foretees.com";


 //*********************************************************************************************
 // doGet - Process the form request from *_reqMenu.htm or self to edit the record
 //*********************************************************************************************

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                  // init DB objects
   Connection con2 = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
     
   HttpSession session = null; 

   String errorMsg = "";
     

   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support ) && !user.equals ("sales3") && !user.equals("sales4")) {

      noAccessUser(user, out);            // Intruder - reject
      return;
   }


   // Load the JDBC Driver and connect to DB.........
     
   try {
      con = dbConn.Connect("support");

   }
   catch (Exception exc) {

      // Error connecting to db....
      errorMsg = "Unable to connect to the DB.";
      dbError(errorMsg, exc, out);         
      return;
   }

   int count = 0;
   int id = 0;

   String date_curr = "";
   String date_open = "";
   String date_closed = "";
   String date_projected = "";
   String orig_by = "";
   String owner = "";
   String club = "";
   String status = "";
   String descript = "";
   String resolution = "";
   String prob_category = "";
   String priority = "";
   String temp = "";
  

   //
   //**********************************************
   //  Check if call is to list problem reports
   //**********************************************
   //
   if (req.getParameter("list") != null) {

      listReports(user, req, out, con);             // display the reports
      return;
   }    

   //
   //**********************************************
   //  Call is for 'new' or 'edit'
   //**********************************************
   //
   if (req.getParameter("id") != null) {       // if id provided, then call is edit a record

      temp = req.getParameter("id");            //  get the record id to edit

      id = Integer.parseInt(temp);

      //
      //  get the record info
      //
      try {

         PreparedStatement pstmt = con.prepareStatement (
           "SELECT * FROM probreports2 " +
           "WHERE id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);
         pstmt.executeQuery();          // execute the prepared stmt

         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            date_open = rs.getString( "date_open" );
            date_projected = rs.getString( "date_projected" );
            date_closed = rs.getString( "date_closed" );
            orig_by = rs.getString( "orig_by" );
            owner = rs.getString( "owner" );
            club = rs.getString( "club" );
            status = rs.getString( "status" );
            prob_category = rs.getString( "prob_category" );
            priority = rs.getString( "priority" );
            descript = rs.getString( "descript" );
            resolution = rs.getString( "resolution" );

         } else {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER><BR>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>The Problem Report Does Not Exist.");
            out.println("<BR>Please try again.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
         pstmt.close();   // close the stmt

      }
      catch (Exception exc) {

         errorMsg = "Error getting the problem record.";
         dbError(errorMsg, exc, out);
         return;
      }
   }     // end of IF id provided

   //
   //  Now prompt user for the record info to be added or updated
   //
   out.println(SystemUtils.HeadTitle("Support Edit Problem Reports"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");
   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Problem Report/Change Request</b><br>");
         out.println("<br>Change the desired information below.<br>");
         out.println("Click on <b>Submit</b> to submit the record.");
         out.println("</font></td></tr>");
         out.println("<form action=\"/" +rev+ "/servlet/Support_problems\" method=\"post\">");
         out.println("<tr><td>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\"><br>");

         if (id > 0) {     // if record already exists
            out.println("&nbsp;&nbsp;Record Id:&nbsp;&nbsp;&nbsp;<b>" +id+ "</b>");
            out.println("<br><br>");
         }

         out.println("&nbsp;&nbsp;Club:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"club\">");

         out.println("<option selected value=\"" + club + "\">" + club + "</option>");
         out.println("<option value=\"-ALL-\">-ALL-</option>");

         //
         // Get the 'clubs' table
         //
         try {

            con2 = dbConn.Connect(rev);           // connect to v_ db

            stmt = con2.createStatement();        // create a statement

            rs2 = stmt.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

            while(rs2.next()) {

               String clubname = rs2.getString(1);
                 
               if (!clubname.equals( club )) {             // if not same as existing name

                  out.println("<option value=\"" + clubname + "\">" + clubname + "</option>");
               }
            }
            stmt.close();

         }
         catch (Exception exc) {             // SQL Error
         }

         if (con2 != null) {
            try {
               con2.close();       // Close the db connection........
            }
            catch (SQLException ignored) {
            }
         }

         out.println("</select>");
         out.println("<br><br>");

         if (user.equals( support ) && !date_open.equals( "" )) {     // if support (do not allow for sales)
           
            out.println("&nbsp;&nbsp;Date Opened (yyyy-mm-dd):&nbsp;&nbsp;");
            out.println("<input type=\"text\" size=\"12\" maxlength=\"12\" value=\"" +date_open+ "\" name=\"date_open\">");
            out.println("<br><br>");
         }

         if (user.equals( support )) {     // if support (do not allow for sales)

            out.println("&nbsp;&nbsp;Projected Date (yyyy-mm-dd):&nbsp;&nbsp;");
            out.println("<input type=\"text\" size=\"12\" maxlength=\"12\" value=\"" +date_projected+ "\" name=\"date_projected\">");
            out.println("<br><br>");
         }

         if (user.equals( support )) {     // if support (do not allow for sales)

            out.println("&nbsp;&nbsp;Date Resolved (yyyy-mm-dd):&nbsp;&nbsp;");
            out.println("<input type=\"text\" size=\"12\" maxlength=\"12\" value=\"" +date_closed+ "\" name=\"date_closed\">");
            out.println("<br><br>");
         }

         if (user.equals( support )) {     // if support (do not allow for sales)

            out.println("&nbsp;&nbsp;Originated By:&nbsp;&nbsp;");
              out.println("<select size=\"1\" name=\"orig_by\">");
              if (orig_by.equals( support1 )) {
                 out.println("<option selected value=\"" +support1+ "\">" +support1+ "</option>");
              } else {
                 out.println("<option value=\"" +support1+ "\">" +support1+ "</option>");
              }
              if (orig_by.equals( support2 )) {
                 out.println("<option selected value=\"" +support2+ "\">" +support2+ "</option>");
              } else {
                 out.println("<option value=\"" +support2+ "\">" +support2+ "</option>");
              }
              if (orig_by.equals( dev1 )) {
                 out.println("<option selected value=\"" +dev1+ "\">" +dev1+ "</option>");
              } else {
                 out.println("<option value=\"" +dev1+ "\">" +dev1+ "</option>");
              }
              if (orig_by.equals( dev2 )) {
                 out.println("<option selected value=\"" +dev2+ "\">" +dev2+ "</option>");
              } else {
                 out.println("<option value=\"" +dev2+ "\">" +dev2+ "</option>");
              }
              if (orig_by.equals( dev3 ) || (user.equals("support") && orig_by.equals(""))) {
                 out.println("<option selected value=\"" +dev3+ "\">" +dev3+ "</option>");
              } else {
                 out.println("<option value=\"" +dev3+ "\">" +dev3+ "</option>");
              }
              /*
              if (orig_by.equals( sales1 )) {
                 out.println("<option selected value=\"" +sales1+ "\">" +sales1+ "</option>");
              } else {
                 out.println("<option value=\"" +sales1+ "\">" +sales1+ "</option>");
              }
              if (orig_by.equals( sales2 )) {
                 out.println("<option selected value=\"" +sales2+ "\">" +sales2+ "</option>");
              } else {
                 out.println("<option value=\"" +sales2+ "\">" +sales2+ "</option>");
              }
              */
            out.println("</select><br><br>");

            out.println("&nbsp;&nbsp;Current Owner:&nbsp;&nbsp;");
              out.println("<select size=\"1\" name=\"owner\">");
              if (owner.equals( support1 )) {
                 out.println("<option selected value=\"" +support1+ "\">" +support1+ "</option>");
              } else {
                 out.println("<option value=\"" +support1+ "\">" +support1+ "</option>");
              }
              if (owner.equals( support2 )) {
                 out.println("<option selected value=\"" +support2+ "\">" +support2+ "</option>");
              } else {
                 out.println("<option value=\"" +support2+ "\">" +support2+ "</option>");
              }
              if (owner.equals( dev1 )) {
                 out.println("<option selected value=\"" +dev1+ "\">" +dev1+ "</option>");
              } else {
                 out.println("<option value=\"" +dev1+ "\">" +dev1+ "</option>");
              }
              if (owner.equals( dev2 )) {
                 out.println("<option selected value=\"" +dev2+ "\">" +dev2+ "</option>");
              } else {
                 out.println("<option value=\"" +dev2+ "\">" +dev2+ "</option>");
              }
              if (owner.equals( dev3 ) || (user.equals("support") && owner.equals(""))) {
                 out.println("<option selected value=\"" +dev3+ "\">" +dev3+ "</option>");
              } else {
                 out.println("<option value=\"" +dev3+ "\">" +dev3+ "</option>");
              }
              /*
              if (owner.equals( sales1 )) {
                 out.println("<option selected value=\"" +sales1+ "\">" +sales1+ "</option>");
              } else {
                 out.println("<option value=\"" +sales1+ "\">" +sales1+ "</option>");
              }
              if (owner.equals( sales2 )) {
                 out.println("<option selected value=\"" +sales2+ "\">" +sales2+ "</option>");
              } else {
                 out.println("<option value=\"" +sales2+ "\">" +sales2+ "</option>");
              }
              */
            out.println("</select><br><br>");

            out.println("&nbsp;&nbsp;Current Status:&nbsp;&nbsp;");
              out.println("<select size=\"1\" name=\"status\">");
              if (status.equals( "Open" )) {
                 out.println("<option selected value=\"Open\">Open</option>");
              } else {
                 out.println("<option value=\"Open\">Open</option>");
              }
              if (status.equals( "In Progress" )) {
                 out.println("<option selected value=\"In Progress\">In Progress</option>");
              } else {
                 out.println("<option value=\"In Progress\">In Progress</option>");
              }
              if (status.equals( "Resolved" )) {
                 out.println("<option selected value=\"Resolved\">Resolved</option>");
              } else {
                 out.println("<option value=\"Resolved\">Resolved</option>");
              }
              if (status.equals( "Rejected" )) {
                 out.println("<option selected value=\"Rejected\">Rejected</option>");
              } else {
                 out.println("<option value=\"Rejected\">Rejected</option>");
              }
            out.println("</select><br><br>");
         }

         out.println("&nbsp;&nbsp;Report Subject:&nbsp;&nbsp;");
         if (user.equals(support) || (!user.equals(support) && id == 0)) {
             out.println("<input type=\"text\" size=\"50\" value=\"" +prob_category+ "\" name=\"prob_category\">");
         } else {
             out.println(prob_category);
         }
         out.println("<br><br>");

         out.println("&nbsp;&nbsp;Priority:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"priority\">");
           out.println("<option selected value=\"" +priority+ "\">" +priority+ "</option>");
           if (!priority.equals( "1 - Critical" )) {
              out.println("<option value=\"1 - Critical\">1 - Critical</option>");
           }
           if (!priority.equals( "2 - High" )) {
              out.println("<option value=\"2 - High\">2 - High</option>");
           }
           if (!priority.equals( "3 - Medium" )) {
              out.println("<option value=\"3 - Medium\">3 - Medium</option>");
           }
           if (!priority.equals( "4 - Low" )) {
              out.println("<option value=\"4 - Low\">4 - Low</option>");
           }
         out.println("</select>");
         out.println("&nbsp;&nbsp;<b>NOTE: Critical</b> means the club cannot function!!");
         out.println("<br><br>");

         out.println("&nbsp;&nbsp;Description:&nbsp;&nbsp;(<b>Provide Details!!!</b>)<br>");
           out.println("&nbsp;&nbsp;<textarea name=descript cols=60 rows=8>" +descript+ "</textarea>");


         if (user.equals( support )) {     // if support (do not allow for sales)

            out.println("<br><br>");
            out.println("&nbsp;&nbsp;Resolution:&nbsp;&nbsp;(<b>Provide Details!!!</b>)<br>");
              out.println("&nbsp;&nbsp;<textarea name=resolution cols=60 rows=8>" +resolution+ "</textarea>");

         } else {

            if (status.equals( "Resolved" )) {         // if resolved

               out.println("<br><br>");
               out.println("&nbsp;&nbsp;Resolution:&nbsp;&nbsp;");
                 out.println(resolution);
            }
         }
         out.println("</p>");

         out.println("<input type=\"hidden\" name=\"id\" value=\"" +id+ "\">");   // id

       out.println("<p align=\"center\">");
       out.println("<input type=\"submit\" name=\"Submit\" value=\"Submit\">");
      out.println("</p></font></td></tr></form></table>");
      out.println("</td></tr></table>");                       // end of main page table

   out.println("<font size=\"2\">");
   if (user.equals( support )) {     // if support (do not allow for sales)
      out.println("<form method=\"get\" action=\"/" +rev+ "/support_reqMenu.htm\">");
   } else {
      out.println("<form method=\"get\" action=\"/" +rev+ "/sales_reqMenu.htm\">");
   }
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");

   out.println("</center></font></body></html>");
   out.close();

   if (con != null) {
      try {
         con.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }
 }   


 //*********************************************************************************************
 // doPost - Process the form request from above - add or update the record
 //*********************************************************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   HttpSession session = null;

   String errorMsg = "";


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support ) && !user.equals("sales3") && !user.equals("sales4")) {

      noAccessUser(user, out);            // Intruder - reject
      return;
   }


   // Load the JDBC Driver and connect to DB.........

   try {
      con = dbConn.Connect("support");

   }
   catch (Exception exc) {

      // Error connecting to db....
      errorMsg = "Unable to connect to the DB.";
      dbError(errorMsg, exc, out);
      return;
   }

   int count = 0;
   int id = 0;                 // if zero then we are adding, if > 0 then we are updating

   String date_curr = "";
   String date_future = "";
   String date_open = "";
   String date_closed = "";
   String date_projected = "";
   String orig_by = "";
   String owner = "";
   String club = "";
   String status = "";
   String descript = "";
   String resolution = "";
   String prob_category = "";
   String priority = "";
   String temp = "";

   String old_date_curr = "";
   String old_date_open = "";
   String old_date_closed = "";
   String old_date_projected = "";
   String old_orig_by = "";
   String old_owner = "";
   String old_club = "";
   String old_status = "";
   String old_descript = "";
   String old_resolution = "";
   String old_prob_category = "";
   String old_priority = "";
   String emailSubject = "";
   String emailTo = "";
     

   //
   //**********************************************
   //  Call is for 'add' or 'update'
   //**********************************************
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) + 1;
   int day = cal.get(Calendar.DAY_OF_MONTH);

   date_curr = year + "-" + month + "-" + day;   // date in mysql format

   cal.add(Calendar.DATE,90);                     // get date 90 days from now
   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);

   date_future = year + "-" + month + "-" + day;   // future date 

   //
   //  Get the parms passed
   //
   temp = req.getParameter("id");
  
   id = Integer.parseInt(temp);

   if (req.getParameter("date_open") != null) {

      date_open = req.getParameter("date_open");
   }
   if (req.getParameter("date_projected") != null) {

      date_projected = req.getParameter("date_projected");
   }
   if (req.getParameter("date_closed") != null) {

      date_closed = req.getParameter("date_closed");
   }
   if (req.getParameter("orig_by") != null) {

      orig_by = req.getParameter("orig_by");
   }
   if (req.getParameter("owner") != null) {

      owner = req.getParameter("owner");
   }
   if (req.getParameter("club") != null) {

      club = req.getParameter("club");
   }
   if (req.getParameter("status") != null) {

      status = req.getParameter("status");
   }
   if (req.getParameter("prob_category") != null) {

      prob_category = req.getParameter("prob_category");
   }
   if (req.getParameter("priority") != null) {

      priority = req.getParameter("priority");
   }
   if (req.getParameter("descript") != null) {

      descript = req.getParameter("descript");
   }
   if (req.getParameter("resolution") != null) {

      resolution = req.getParameter("resolution");
   }

   //
   //  verify minimum parms received
   //
   if (owner.equals( "" ) && user.equals( "support" )) {

      errorMsg = "Owner was not specified - required.";
   }
   if (club.equals( "" )) {

      errorMsg = "Club Name was not specified - required.";
   }
   if (status.equals( "" ) && user.equals( "support" )) {

      errorMsg = "Current Status was not specified - required.";
   }
   if (prob_category.equals( "" )) {

      errorMsg = "Problem Type was not specified - required.";
   }
   if (priority.equals( "" )) {

      errorMsg = "Priority was not specified - required.";
   }
   if (descript.equals( "" )) {

      errorMsg = "Description was not specified - required.";
   }
   if (status.equals( "Resolved" ) && resolution.equals( "" )) {

      errorMsg = "Resolution Text was not specified - required (only Support can do this).";
   }

   if (!errorMsg.equals( "" )) {      // if error - reject
     
      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Data Entry Error</H3>");
      out.println("<BR><BR>" +errorMsg);
      out.println("<BR>Please try again.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   if (id == 0) {

      date_open = date_curr;      // use today's date
   }
      
   if (status.equals( "Resolved" ) && date_closed.equals( "" )) {

      date_closed = date_curr;      // use today's date
   }

   if (date_closed.equals( "" )) {

      date_closed = date_future;      // use future date for default
   }
   if (date_projected.equals( "" )) {

      date_projected = date_future;      // use future date for default
   }

   //
   //  add or update the record
   //
   try {

      if (id == 0) {      // add new record 
        
         if (orig_by.equals( "" )) {
           
            if (user.equals("sales3")) {
                orig_by = support1;
            } else if (user.equals("sales4")) {
                orig_by = support2;
            } else {
                orig_by = dev3;               // default
            }
         }
  
         if (owner.equals( "" )) {
             
             owner = dev3;               // all new problem records assigned to dev3
         }

         if (status.equals( "" )) {

            status = "Open";               // default
         }
  
         pstmt = con.prepareStatement (
           "INSERT INTO probreports2 (id, date_open, date_projected, date_closed, orig_by, owner, club, status, " +
           "prob_category, priority, descript, resolution) VALUES " +
           "(NULL,?,?,?,?,?,?,?,?,?,?,?)");

         pstmt.clearParameters();             // clear the parms
         pstmt.setString(1, date_open);
         pstmt.setString(2, date_projected);
         pstmt.setString(3, date_closed);
         pstmt.setString(4, orig_by);
         pstmt.setString(5, owner);
         pstmt.setString(6, club);
         pstmt.setString(7, status);
         pstmt.setString(8, prob_category);
         pstmt.setString(9, priority);
         pstmt.setString(10, descript);
         pstmt.setString(11, resolution);

         pstmt.executeUpdate();          // execute the prepared stmt

         pstmt.close();   // close the stmt

      } else {
        
         //
         //  Get the old values
         //
         pstmt = con.prepareStatement (
                 "SELECT * FROM probreports2 WHERE id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            old_date_open = rs.getString("date_open");
            old_date_projected = rs.getString("date_projected");
            old_date_closed = rs.getString("date_closed");
            old_orig_by = rs.getString("orig_by");
            old_owner = rs.getString("owner");
            old_club = rs.getString("club");
            old_status = rs.getString("status");
            old_prob_category = rs.getString("prob_category");
            old_priority = rs.getString("priority");
            old_descript = rs.getString("descript");
            old_resolution = rs.getString("resolution");

         }
         pstmt.close();              // close the stmt

         if (date_open.equals( "" )) {
           
            date_open = old_date_open;
         }
         if (orig_by.equals( "" )) {

            orig_by = old_orig_by;
         }
         if (owner.equals( "" )) {

            owner = old_owner;
         }
         if (status.equals( "" )) {

            status = old_status;
         }
         if (prob_category.equals( "" )) {

            prob_category = old_prob_category;
         }

         //
         //   Update the record
         //
         pstmt = con.prepareStatement (
             "UPDATE probreports2 " +
             "SET date_open = ?, date_projected = ?, date_closed = ?, orig_by = ?, " +
             "owner = ?, club = ?, status = ?, prob_category = ?, priority = ?, descript = ?, resolution = ? " +
             "WHERE id = ?");
           
         pstmt.clearParameters();
         pstmt.setString(1, date_open);
         pstmt.setString(2, date_projected);
         pstmt.setString(3, date_closed);
         pstmt.setString(4, orig_by);
         pstmt.setString(5, owner);
         pstmt.setString(6, club);
         pstmt.setString(7, status);
         pstmt.setString(8, prob_category);
         pstmt.setString(9, priority);
         pstmt.setString(10, descript);
         pstmt.setString(11, resolution);
         pstmt.setInt(12, id);

         pstmt.executeUpdate();                // execute the prepared stmt

         pstmt.close();
      }
        
   }
   catch (Exception exc) {

      // Error connecting to db....
      errorMsg = "Unable to connect to the DB.";
      dbError(errorMsg, exc, out);
      return;
   }

   // Add or update complete - inform support....

   out.println("<HTML><HEAD><TITLE>Add or Update Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Request Record Added or Updated</H3>");
   out.println("<BR><BR>Request added or updated for support requests.");
   if (user.equals( support )) {     // if support (do not allow for sales)
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_reqMenu.htm\">Return</A>");
   } else {
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_reqMenu.htm\">Return</A>");
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();

   //
   // done
   //
   if (con != null) {
      try {
         con.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }

   //
   //  Send email if necessary
   //
   if (id == 0 && !orig_by.equals( dev3 )) {       // if new record and not entered by support 
     
      emailSubject = priority + " - " + orig_by + " - " + club + " - " + prob_category;
      emailTo = emailDev3;
   }

   if (status.equals( "Rejected" ) && !old_status.equals( "Rejected" )) {  // if problem rejected

      emailSubject = "ForeTees - Request # " +id+ " Rejected by Support - Please Correct (" + prob_category + ")";

      if (orig_by.equals( support1 )) {
         emailTo = emailSupport1;
      }
      if (orig_by.equals( support2 )) {
         emailTo = emailSupport2;
      }
      if (orig_by.equals( dev1 )) {
         emailTo = emailDev1;
      }
      if (orig_by.equals( dev2 )) {
         emailTo = emailDev2;
      }
      if (orig_by.equals( dev3 )) {
         emailTo = emailDev3;
      }
      /*
      if (orig_by.equals( sales1 )) {
         emailTo = emailSales1;
      }
      if (orig_by.equals( sales2 )) {
         emailTo = emailSales2;
      }
      */
   }

   if (status.equals( "Resolved" ) && !old_status.equals( "Resolved" )) {  // if problem resolved

      emailSubject = "ForeTees - Request # " +id+ " Resolved by Support (" + prob_category + ")";

      if (orig_by.equals( support1 )) {
         emailTo = emailSupport1;
      }
      if (orig_by.equals( support2 )) {
         emailTo = emailSupport2;
      }
      if (orig_by.equals( dev1 )) {
         emailTo = emailDev1;
      }
      if (orig_by.equals( dev2 )) {
         emailTo = emailDev2;
      }
      if (orig_by.equals( dev3 )) {
         emailTo = emailDev3;
      }
      /*
      if (orig_by.equals( sales1 )) {
         emailTo = emailSales1;
      }
      if (orig_by.equals( sales2 )) {
         emailTo = emailSales2;
      }
      */
   }
     
   //
   //  Send the email if necessary
   //
   if (!emailSubject.equals( "" ) && !emailTo.equals( "" )) {
     
      Properties properties = new Properties();
      properties.put("mail.smtp.host", host);                      // set outbound host address
      properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

      Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

      MimeMessage message = new MimeMessage(mailSess);
      
      InternetAddress[] addresses = new InternetAddress[1];

      try {

         message.setFrom(new InternetAddress(efrom));                                    // set From addr
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));   // set To addr

         message.setSubject( emailSubject );                                        // set subject line
         message.setSentDate(new java.util.Date());                                 // set date/time sent
         
         if (status.equals("Rejected") && !old_status.equals("Rejected")) {
             message.setText("Problem # " + id + " Rejected - Please check your Record List");
             if (owner.equals(dev1)) {
                 addresses[0] = new InternetAddress(emailDev1);
                 message.setReplyTo(addresses);
             } else if (owner.equals(dev2)) {
                 addresses[0] = new InternetAddress(emailDev2);
                 message.setReplyTo(addresses);
             } else {
                 addresses[0] = new InternetAddress(emailDev3);
                 message.setReplyTo(addresses);
             }
             
         } else if (status.equals("Resolved") && !old_status.equals("Resolved")) {
             message.setText(
                     "Resolution (" + owner + "): " + 
                     "\n\n" + resolution + 
                     "\n\n\n--------------------------------------------------------" + 
                     "\n\nOriginal Request Description (" + orig_by + "):" +
                     "\n\n" + descript + 
                     "\n\n--------------------------------------------------------");
             
             if (owner.equals(dev1)) {
                 addresses[0] = new InternetAddress(emailDev1);
                 message.setReplyTo(addresses);
             } else if (owner.equals(dev2)) {
                 addresses[0] = new InternetAddress(emailDev2);
                 message.setReplyTo(addresses);
             } else {
                 addresses[0] = new InternetAddress(emailDev3);
                 message.setReplyTo(addresses);
             }
             
         } else if (id == 0) {
             message.setText(descript);
             
             if (orig_by.equals(support1)) {
                 addresses[0] = new InternetAddress(emailSupport1);
                 message.setReplyTo(addresses);
             } else if (orig_by.equals(support2)) {
                 addresses[0] = new InternetAddress(emailSupport2);
                 message.setReplyTo(addresses);
             } else if (orig_by.equals(dev1)) {
                 addresses[0] = new InternetAddress(emailDev1);
                 message.setReplyTo(addresses);
             } else if (orig_by.equals(dev2)) {
                 addresses[0] = new InternetAddress(emailDev2);
                 message.setReplyTo(addresses);
             } else {
                 addresses[0] = new InternetAddress(emailDev3);
                 message.setReplyTo(addresses);
             }
             
         } else {
             message.setText( "Problem Record Added or Changed - Please Check Your Record List" ); // add msg text             
         }

         Transport.send(message);                     // send it!!

      }
      catch (Exception e1) {
      }
   }

 }        // end of doPost
    

 // ************************************************************************
 //  Process getAuthenticator for email authentication
 // ************************************************************************

 private static Authenticator getAuthenticator() {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }


 // *********************************************************
 //  Process the 'List' request
 // *********************************************************

 public void listReports(String user, HttpServletRequest req, PrintWriter out, Connection con) {

   ResultSet rs = null;
   PreparedStatement pstmt = null;


   int id = 0;                 // if zero then we are adding, if > 0 then we are updating
   int count = 1;

   String date_curr = "";
   String date_open = "";
   String date_closed = "";
   String date_projected = "";
   String orig_by = "";
   String owner = "";
   String club = "";
   String status = "";
   String descript = "";
   String resolution = "";
   String prob_category = "";
   String priority = "";
   String this_owner = "";
     
   String bgcolor = "#F5F5DC";
   String color1 = "#F5F5DC";
   String color2 = "#CCCCAA";

   if (user.equals( "sales3")) {
       this_owner = support1;
   } else if (user.equals( "sales4" )) {
       this_owner = support2;
   } else if (user.equals( "support" )) {
       this_owner = dev3;
   }

   String list_type = req.getParameter("list");    //  get the list type requested
   String sort_type = "";
   
   if (req.getParameter("sort") != null) {
       sort_type = req.getParameter("sort");
   }
   
   String sort_type_next = "";
   
   if (!sort_type.endsWith("x")) {
       sort_type_next = "x";
   } else {
       sort_type_next = "";
   }
   
   //
   //  Display a list of problem reports based on the list_type requested
   //
   //     types:  myopen, allopen, myclosed, allclosed
   //
   try {
     
      if (list_type.equals( "allopen" )) {
        
          if (sort_type.equals("id")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY id, date_open");              
          } else if (sort_type.equals("idx")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY id DESC, date_open");              
          } else if (sort_type.equals("opendate")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY date_open, id");              
          } else if (sort_type.equals("opendatex")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY date_open DESC, id");              
          } else if (sort_type.equals("closedate")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY date_closed, id");  
          } else if (sort_type.equals("closedatex")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY date_closed DESC, id");  
          } else if (sort_type.equals("orig")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY orig_by, date_open");              
          } else if (sort_type.equals("origx")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY orig_by DESC, date_open");              
          } else if (sort_type.equals("own")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY owner, date_open");              
          } else if (sort_type.equals("ownx")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY owner DESC, date_open");              
          } else if (sort_type.equals("club")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY club, date_open");              
          } else if (sort_type.equals("clubx")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY club DESC, date_open");              
          } else if (sort_type.equals("status")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY status, date_open");              
          } else if (sort_type.equals("statusx")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY status DESC, date_open");              
          } else if (sort_type.equals("pri")) {
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY priority, date_open");
          } else if (sort_type.equals("prix")) { 
              pstmt = con.prepareStatement(
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY priority DESC, date_open");
          } else {
              pstmt = con.prepareStatement (
                      "SELECT * FROM probreports2 WHERE status != ? ORDER BY id");
          }
          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, "Resolved");
      }

      if (list_type.equals( "allclosed" )) {

         pstmt = con.prepareStatement (
                 "SELECT * FROM probreports2 WHERE status = ? ORDER BY date_closed DESC");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, "Resolved");
      }

      if (list_type.equals( "myopen" )) {

         pstmt = con.prepareStatement (
                 "SELECT * FROM probreports2 WHERE orig_by = ? AND status != ? ORDER BY date_open");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, this_owner);
         pstmt.setString(2, "Resolved");
      }

      if (list_type.equals( "myclosed" )) {

         pstmt = con.prepareStatement (
                 "SELECT * FROM probreports2 WHERE orig_by = ? AND status = ? ORDER BY date_closed DESC");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, this_owner);
         pstmt.setString(2, "Resolved");
      }

      rs = pstmt.executeQuery();      // execute the prepared pstmt

      out.println(SystemUtils.HeadTitle("Display Problem Reports"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><H2>Problem Reports</H2>");
      if (user.equals( support )) {
         out.println("<A HREF=\"/" +rev+ "/support_reqMenu.htm\">Return</A>");
      } else {
         out.println("<A HREF=\"/" +rev+ "/sales_reqMenu.htm\">Return</A>");
      }
      out.println("<br><br><Table border=\"1\" align=\"center\" bgcolor=\"#F5F5DC\" width=\"80%\">");
      out.println("<tr bgcolor=\"#336633\">");
      out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b>Count</b></p></font></td>");
      out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b><a href=\"/" +rev+ "/servlet/Support_problems?list=" + list_type + "&sort=id" + ((sort_type.startsWith("id")) ? sort_type_next : "") + "\" style=\"text-decoration: none; color: white\">Record Id</a></b></p></font></td>");
      if (list_type.equals( "myopen" ) || list_type.equals( "allopen" )) {
         out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b><a href=\"/" +rev+ "/servlet/Support_problems?list=" + list_type + "&sort=opendate" + ((sort_type.startsWith("opendate")) ? sort_type_next : "") + "\" style=\"text-decoration: none; color: white\">Date Opened</a></b></p></font></td>");
      } else {
         out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b><a href=\"/" +rev+ "/servlet/Support_problems?list=" + list_type + "&sort=closedate" + ((sort_type.startsWith("closedate")) ? sort_type_next : "") + "\" style=\"text-decoration: none; color: white\">Date Closed</a></b></p></font></td>");
      }
      out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b><a href=\"/" +rev+ "/servlet/Support_problems?list=" + list_type + "&sort=orig" + ((sort_type.startsWith("orig")) ? sort_type_next : "") + "\" style=\"text-decoration: none; color: white\">Opened By</a></b></p></font></td>");
      out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b><a href=\"/" +rev+ "/servlet/Support_problems?list=" + list_type + "&sort=assign" + ((sort_type.startsWith("assign")) ? sort_type_next : "") + "\" style=\"text-decoration: none; color: white\">Assigned To</a></b></p></font></td>");
      out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b><a href=\"/" +rev+ "/servlet/Support_problems?list=" + list_type + "&sort=club" + ((sort_type.startsWith("club")) ? sort_type_next : "") + "\" style=\"text-decoration: none; color: white\">Club</a></b></p></font></td>");
      out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b><a href=\"/" +rev+ "/servlet/Support_problems?list=" + list_type + "&sort=status" + ((sort_type.startsWith("status")) ? sort_type_next : "") + "\" style=\"text-decoration: none; color: white\">Status</a></b></p></font></td>");
      out.println("<td><font color=\"#FFFFFF\" size=\"3\"><p><b><a href=\"/" +rev+ "/servlet/Support_problems?list=" + list_type + "&sort=pri" + ((sort_type.startsWith("pri")) ? sort_type_next : "") + "\" style=\"text-decoration: none; color: white\">Priority</a></b></p></font></td>");
      out.println("</td></tr>");
        
      out.println("<tr bgcolor=\"#336633\">");
      if (list_type.equals( "myopen" ) || list_type.equals( "allopen" )) {
         out.println("<td colspan=\"8\"><font color=\"#FFFFFF\" size=\"3\">");
         out.println("<p><b>Problem Description</b></p></font></td>");
      } else {
         out.println("<td colspan=\"8\"><font color=\"#FFFFFF\" size=\"3\">");
         out.println("<p><b>Problem Resolution</b></p></font></td>");
      }
      out.println("</tr>");

      while (rs.next()) {

         id = rs.getInt("id");
         date_open = rs.getString("date_open");
         date_projected = rs.getString("date_projected");
         date_closed = rs.getString("date_closed");
         orig_by = rs.getString("orig_by");
         owner = rs.getString("owner");
         club = rs.getString("club");
         status = rs.getString("status");
         prob_category = rs.getString("prob_category");
         priority = rs.getString("priority");
         descript = rs.getString("descript");
         resolution = rs.getString("resolution");

         out.println("<tr bgcolor=\"" +bgcolor+ "\">");
         out.println("<td>" + count);
         out.println("</td><td>Id# <b>" + id + "</b>");
         if (list_type.equals( "myopen" ) || list_type.equals( "allopen" )) {
            out.println("</td><td>" +date_open);
         } else {
            out.println("</td><td>" +date_closed);
         }
         out.println("</td><td>" +orig_by);
         out.println("</td><td>" +owner);
         out.println("</td><td>" +club);
         out.println("</td><td>" +status);
         if (priority.equalsIgnoreCase( "1 - Critical" )) {
            out.println("</td><td bgcolor=\"red\">");
         } else if (priority.equalsIgnoreCase( "2 - High" )) {
             out.println("</td><td bgcolor=\"orange\">");
         } else if (priority.equalsIgnoreCase("3 - Medium")) {
             out.println("</td><td bgcolor=\"yellow\">");
         } else {
             out.println("</td><td bgcolor=\"lime\">");
         }
         out.println(priority+ "</td>");

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_problems\">");
         out.println("<input type=\"hidden\" name=\"id\" value=\"" +id+ "\">");

         out.println("<td rowspan=\"2\">");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td>");
         out.println("</form>");
         out.println("</tr>");

         out.println("<tr bgcolor=\"" +bgcolor+ "\">");
         if (list_type.equals( "myopen" ) || list_type.equals( "allopen" )) {
            out.println("<td colspan=\"8\">" +descript);
         } else {
            out.println("<td colspan=\"8\">" +resolution);
         }
         out.println("</td>");
         out.println("</tr>");
           
         if (bgcolor.equals( color1 )) {
           
            bgcolor = color2;    // switch
              
         } else {

            bgcolor = color1;    // switch
         }
         count++;

      }
      pstmt.close();              // close the stmt

      out.println("</TABLE><BR><BR>");
      if (user.equals( support )) {   
         out.println("<A HREF=\"/" +rev+ "/support_reqMenu.htm\">Return</A>");
      } else {
         out.println("<A HREF=\"/" +rev+ "/sales_reqMenu.htm\">Return</A>");
      }
      out.println("</CENTER></BODY></HTML>");
      out.close();

   }
   catch (Exception exc) {

      // Error connecting to db....
      String errorMsg = "Error processing report list.";
      dbError(errorMsg, exc, out);
      return;
   }

   //
   // Done - return.......
   //
   if (con != null) {
      try {
         con.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }
     
 }        // end of listReports


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(String msg, Exception exc, PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER><BR>");
    out.println("<BR><BR><H3>Database Access Error</H3>");
    out.println("<BR><BR>" +msg);
    out.println("<BR>Exception:   " + exc.getMessage());
    out.println("<BR><BR>");
    out.println("<font size=\"2\">");
    out.println("<form method=\"get\" action=\"/" +rev+ "/support_main.htm\">");
    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
    out.println("</input></form></font>");
    out.println("</CENTER></BODY></HTML>");
 }

 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }
 
  private void noAccessUser(String user, PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you do not have access to this feature.<BR>");
   if (user.equals("support")) {
       out.println("<BR><BR><a href=\"/" +rev+ "/support_main.htm\">Return</a>");
   } else {
       out.println("<BR><BR><a href=\"/" +rev+ "/sales_main.htm\">Return</a>");
   }
   out.println("</CENTER></BODY></HTML>");

 }

}
