/***************************************************************************************
 *   Support_upgrade_copy:  This servlet will process the upgrade request from Support's Upgrade page.
 *
 *
 *       Copy tee times for Island View CC
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_upgrade_copy extends HttpServlet {


 // Process the form request from support_upgrade.htm.....

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


   String club = "islandview";                                     // ************ Des Moines ***********

   try {

      con = dbConn.Connect(club);
   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
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
   int i = 0;

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

   //
   //  Dates to copy times to !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
   //
   int [] datesA = { 20090514, 20090521, 20090528, 20090604, 20090611, 20090618, 20090625, 20090702, 20090709, 20090716, 20090723, 20090730, 
                     20090806, 20090813, 20090820, 20090827, 20090903, 20090910, 20090917, 0 };


   try {

      date = 20090507;                 // get from may 7th
        
      //
      //***************************************************************
      //  Get the desired tee times from TEECURR !!!!!!!!!!!!!!!!!!!!!
      //***************************************************************
      //
      pstmt1 = con.prepareStatement (
                "SELECT * FROM teecurr2 WHERE date = ? AND time > 1000 AND time < 1700");

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


         //
         //  If the tee time is not empty, then copy it to specified date
         //
         if (!player1.equals("")) {

            i = 0;

            while (datesA[i] > 0) {                            // copy to each date in the array above        

               date = datesA[i];                               // get the date

               pstmt2 = con.prepareStatement (
                  "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                  "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                  "p2cw = ?, p3cw = ?, p4cw = ?, " +
                  "player5 = ?, username5 = ?, " +
                  "p5cw = ?, " +
                  "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                  "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                  "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

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

               pstmt2.setLong(32, date);
               pstmt2.setInt(33, time);
               pstmt2.setInt(34, fb);
               pstmt2.setString(35, course);
               pstmt2.executeUpdate();     

               pstmt2.close();

               i++;                    // do next date

            }                          // end of while
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
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Upgrade Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Copy Complete</H3>");
   out.println("<BR><BR>Tee Time Copy complete for " +club+ ".");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>");
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
   out.println("<BR><BR>Please <A HREF=\"Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

}
