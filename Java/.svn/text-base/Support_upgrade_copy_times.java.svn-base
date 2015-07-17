/***************************************************************************************
 *   Support_upgrade_copy_times:  This servlet will process the upgrade request from Support's Upgrade page.
 *
 *
 *       Copy tee time for Rolling Hills CC in Kansas
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_upgrade_copy_times extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the doGet as a post
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing
 }


 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   PreparedStatement pstmt;
   PreparedStatement pstmt1;
   PreparedStatement pstmt2;

   Statement stmt = null;
   ResultSet rs = null;

   String support = "support";             // valid username

   HttpSession session = null;


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String userName = (String)session.getAttribute("user");   // get username

   if (!userName.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }





   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "rollinghillscc";                        // ************ Rolling Hills ***********


   
   if (req.getParameter("continue") == null) {     // if first time thru here

      //
      //  Prompt user to make sure this is what they want to do !!!!!!!!!!!!!!!!
      //
      out.println("<HTML><HEAD><TITLE>Upgrade Prompt User</TITLE></HEAD>");     // **** Change this MESSAGE **********
      out.println("<BODY><CENTER><H3>Support Upgrade</H3>");
      out.println("<BR><BR>This job is for: " +club);
      out.println(" and it will COPY tee times from/to specific tee sheets.");     // change this line!
      out.println("<BR><BR>Do you wish to continue?");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Support_upgrade_copy_times\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"continue\" value=\"continue\">");
      out.println("<input type=\"submit\" value=\"Yes - Continue\"></form>");
      
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">No - Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
     


   try {

      con = dbConn.Connect(club);                             
   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   long date = 0;
   int time = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int guest_id1;
   int guest_id2;
   int guest_id3;
   int guest_id4;
   int guest_id5;

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
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String course = "";
   String orig_by = "";

   short fb = 0;


   try {

      //
      //***************************************************************
      //  Do Saturday
      //***************************************************************
      //
      date = 20100506;                 // get from May 23
        
      //
      //  Get the desired tee times from TEECURR !!!!!!!!!!!!!!!!!!!!!
      //
      pstmt1 = con.prepareStatement (
                "SELECT * FROM teecurr2 WHERE date = ? AND player1 != ''");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, date); 
      rs = pstmt1.executeQuery();    

      while (rs.next()) {

         time = rs.getInt("time");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         fb = rs.getShort("fb");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         p5cw = rs.getString("p5cw");
         course = rs.getString("courseName");
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
         orig_by = rs.getString("orig_by");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         guest_id1 = rs.getInt("guest_id1");
         guest_id2 = rs.getInt("guest_id2");
         guest_id3 = rs.getInt("guest_id3");
         guest_id4 = rs.getInt("guest_id4");
         guest_id5 = rs.getInt("guest_id5");

         //
         //  If the tee time is not empty, then copy it to specified date
         //
         if (!player1.equals("")) {

            //  copy forward

            pstmt2 = con.prepareStatement (
               "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
               "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
               "p2cw = ?, p3cw = ?, p4cw = ?, " +
               "player5 = ?, username5 = ?, " +
               "p5cw = ?, " +
               "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
               "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
               "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
               "WHERE (date = 20100520 OR date = 20100603 OR date = 20100701 OR date = 20100715 OR date = 20100729 OR date = 20100812) AND " +
               "time = ? AND fb = ? AND courseName = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, player1);
            pstmt2.setString(2, player2);
            pstmt2.setString(3, player3);
            pstmt2.setString(4, player4);
            pstmt2.setString(5, user1);
            pstmt2.setString(6, user2);
            pstmt2.setString(7, user3);
            pstmt2.setString(8, user4);
            pstmt2.setString(9, p1cw);
            pstmt2.setString(10, p2cw);
            pstmt2.setString(11, p3cw);
            pstmt2.setString(12, p4cw);
            pstmt2.setString(13, player5);
            pstmt2.setString(14, user5);
            pstmt2.setString(15, p5cw);
            pstmt2.setString(16, mNum1);
            pstmt2.setString(17, mNum2);
            pstmt2.setString(18, mNum3);
            pstmt2.setString(19, mNum4);
            pstmt2.setString(20, mNum5);
            pstmt2.setString(21, userg1);
            pstmt2.setString(22, userg2);
            pstmt2.setString(23, userg3);
            pstmt2.setString(24, userg4);
            pstmt2.setString(25, userg5);
            pstmt2.setString(26, orig_by);
            pstmt2.setInt(27, p91);
            pstmt2.setInt(28, p92);
            pstmt2.setInt(29, p93);
            pstmt2.setInt(30, p94);
            pstmt2.setInt(31, p95);
            pstmt2.setInt(32, guest_id1);
            pstmt2.setInt(33, guest_id2);
            pstmt2.setInt(34, guest_id3);
            pstmt2.setInt(35, guest_id4);
            pstmt2.setInt(36, guest_id5);

            pstmt2.setInt(37, time);
            pstmt2.setInt(38, fb);
            pstmt2.setString(39, course);
            pstmt2.executeUpdate();      // execute the prepared stmt

            pstmt2.close();

         }
      }

      pstmt1.close();



      //
      //***************************************************************
      //  Do Sunday
      //***************************************************************
      //

      date = 20100513;                 // get from May 24

      //
      //  Get the desired tee times from TEECURR !!!!!!!!!!!!!!!!!!!!!
      //
      pstmt1 = con.prepareStatement (
                "SELECT * FROM teecurr2 WHERE date = ? AND player1 != ''");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, date);
      rs = pstmt1.executeQuery();

      while (rs.next()) {

         time = rs.getInt("time");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         fb = rs.getShort("fb");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         p5cw = rs.getString("p5cw");
         course = rs.getString("courseName");
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
         orig_by = rs.getString("orig_by");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         guest_id1 = rs.getInt("guest_id1");
         guest_id2 = rs.getInt("guest_id2");
         guest_id3 = rs.getInt("guest_id3");
         guest_id4 = rs.getInt("guest_id4");
         guest_id5 = rs.getInt("guest_id5");

         //
         //  If the tee time is not empty, then copy it to specified date
         //
         if (!player1.equals("")) {

            //  copy forward

            pstmt2 = con.prepareStatement (
               "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
               "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
               "p2cw = ?, p3cw = ?, p4cw = ?, " +
               "player5 = ?, username5 = ?, " +
               "p5cw = ?, " +
               "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
               "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
               "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
               "WHERE (date = 20100527 OR date = 20100610 OR date = 20100624 OR date = 20100708 OR date = 20100722 OR date = 20100805 OR date = 20100819) AND " +
               "time = ? AND fb = ? AND courseName = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, player1);
            pstmt2.setString(2, player2);
            pstmt2.setString(3, player3);
            pstmt2.setString(4, player4);
            pstmt2.setString(5, user1);
            pstmt2.setString(6, user2);
            pstmt2.setString(7, user3);
            pstmt2.setString(8, user4);
            pstmt2.setString(9, p1cw);
            pstmt2.setString(10, p2cw);
            pstmt2.setString(11, p3cw);
            pstmt2.setString(12, p4cw);
            pstmt2.setString(13, player5);
            pstmt2.setString(14, user5);
            pstmt2.setString(15, p5cw);
            pstmt2.setString(16, mNum1);
            pstmt2.setString(17, mNum2);
            pstmt2.setString(18, mNum3);
            pstmt2.setString(19, mNum4);
            pstmt2.setString(20, mNum5);
            pstmt2.setString(21, userg1);
            pstmt2.setString(22, userg2);
            pstmt2.setString(23, userg3);
            pstmt2.setString(24, userg4);
            pstmt2.setString(25, userg5);
            pstmt2.setString(26, orig_by);
            pstmt2.setInt(27, p91);
            pstmt2.setInt(28, p92);
            pstmt2.setInt(29, p93);
            pstmt2.setInt(30, p94);
            pstmt2.setInt(31, p95);
            pstmt2.setInt(32, guest_id1);
            pstmt2.setInt(33, guest_id2);
            pstmt2.setInt(34, guest_id3);
            pstmt2.setInt(35, guest_id4);
            pstmt2.setInt(36, guest_id5);

            pstmt2.setInt(37, time);
            pstmt2.setInt(38, fb);
            pstmt2.setString(39, course);
            pstmt2.executeUpdate();      // execute the prepared stmt

            pstmt2.close();

         }
      }

      pstmt1.close();

      con.close();                           // close the connection to the club db

   }
   catch (Exception e2) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB - error2.");
      out.println("<BR>Exception: "+ e2.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Upgrade Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Copy Complete</H3>");
   out.println("<BR><BR>Tee Time Copy complete for " +club+ ".");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/v5/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>Please <A HREF=\"/v5/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

}
