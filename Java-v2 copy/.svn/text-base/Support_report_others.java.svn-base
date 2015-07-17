/***************************************************************************************
 *   Support_report_others: 
 *
 *
 *       List all players for a specified date range that show up as "Others" in rounds report.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;



public class Support_report_others extends HttpServlet {


 //
 //   Get control from the Support menu
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


    doPost(req, resp);    // future - change to prompt for date range!!!
 }
    
 
 //
 //  Generate the report
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   //Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt4 = null;


   String support = "support";             // valid username

   HttpSession session = null;
   
   int rsync = 0;


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
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //   Get multi option, member types, and guest types
   //
   try {
      getClub.getParms(con, parm);
   }
   catch (Exception e1) {
   }

   boolean guest = false;
   boolean found = false;

   long year = 0;
   long month = 0;
   long day = 0;
   long date = 0;
   
   long sdate = 20080100;              // Start Date for Date Range ******** Change this *********
   long edate = 20081232;
   
   String courseName = "";             //  ********** Change for multi courses *****************
     
   int time = 0;
   int i  = 0;
   int i2  = 0;
   int smm  = 0;
   int sdd = 0;
   int syy = 0;
   int emm  = 0;
   int edd = 0;
   int eyy = 0;
   int hr = 0;
   int min = 0;
   int count = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;

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
   String checked = "";

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

   //
   //   Get date parms for records
   //
   year = sdate / 10000;                                 // get year from Start Date
   month = (sdate - (year * 10000)) / 100;               // get month
   day = (sdate - (year * 10000)) - (month * 100);       // get day

   smm = (int)month;
   sdd = (int)day;
   syy = (int)year;

   year = edate / 10000;                                 // get year from End Date
   month = (edate - (year * 10000)) / 100;               // get month
   day = (edate - (year * 10000)) - (month * 100);       // get day

   emm = (int)month;
   edd = (int)day;
   eyy = (int)year;


   out.println("<HTML><HEAD><TITLE>RSYNC List</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Players Reported as 'Other'</H3>");

   out.println("<br>Players Reported as 'Other'<br>");
   out.println("Date Range: " +smm+ "/" +sdd+ "/" +syy+ " to " +emm+ "/" +edd+ "/" +eyy+ "<br><br>");
      
 
   //
   // use the dates to search the tee times tables
   //

   //
   //   Get tee times for the dates provided
   //
   try {

      //
      //  gather stats from teepast
      //
      pstmt1 = con.prepareStatement (
            "SELECT date, time, player1, player2, player3, player4, username1, username2, username3, username4, " +
            "show1, show2, show3, show4, " +
            "player5, username5, show5 " +
            "FROM teepast2 WHERE date > ? AND date < ? AND courseName = ? ORDER BY date, time");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, sdate);
      pstmt1.setLong(2, edate);
      pstmt1.setString(3, courseName);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         date = rs.getLong(1);
         time = rs.getInt(2);
         player1 = rs.getString(3);
         player2 = rs.getString(4);
         player3 = rs.getString(5);
         player4 = rs.getString(6);
         username1 = rs.getString(7);
         username2 = rs.getString(8);
         username3 = rs.getString(9);
         username4 = rs.getString(10);
         show1 = rs.getInt(11);
         show2 = rs.getInt(12);
         show3 = rs.getInt(13);
         show4 = rs.getInt(14);
         player5 = rs.getString(15);
         username5 = rs.getString(16);
         show5 = rs.getInt(17);

         if ((!player1.equals( "" )) && (!player1.equalsIgnoreCase( "x" ))) {

            found = false;
            i = 0;

            ploop1:
            while (i < parm.MAX_Guests) {

               if (player1.startsWith( parm.guest[i] )) {

                  found = true;
                  break ploop1;
               }
               i++;
            }

            if (found == false) {

               //
               //  player must be a member
               //
               if (!username1.equals( "" )) {               // if member

                  //
                  //  Member - get member type and membership type for this member
                  //
                  pstmt4 = con.prepareStatement (
                     "SELECT m_ship, m_type FROM member2b WHERE " +
                     "username = ?");

                  pstmt4.clearParameters();
                  pstmt4.setString(1, username1);
                  rs2 = pstmt4.executeQuery();

                  if (rs2.next()) {

                     found = true;
                  }
                  pstmt4.close();
               }
            }
            
            if (found == false) {         // if not found
               
               checked = "No";
               
               if (show1 == 1) {
                  
                  checked = "Yes";
               }
               
               out.println("<br><br>Player: " +player1+ ", Username: " +username1+ ", Date: " +date+ ", Time: " +time+ ", Checked-In: " +checked);               
            }
         }        // end of player
                     
         if ((!player2.equals( "" )) && (!player2.equalsIgnoreCase( "x" ))) {

            found = false;
            i = 0;

            ploop2:
            while (i < parm.MAX_Guests) {

               if (player2.startsWith( parm.guest[i] )) {

                  found = true;
                  break ploop2;
               }
               i++;
            }

            if (found == false) {

               //
               //  player must be a member
               //
               if (!username2.equals( "" )) {               // if member

                  //
                  //  Member - get member type and membership type for this member
                  //
                  pstmt4 = con.prepareStatement (
                     "SELECT m_ship, m_type FROM member2b WHERE " +
                     "username = ?");

                  pstmt4.clearParameters();
                  pstmt4.setString(1, username2);
                  rs2 = pstmt4.executeQuery();

                  if (rs2.next()) {

                     found = true;
                  }
                  pstmt4.close();
               }
            }
            
            if (found == false) {         // if not found
               
               checked = "No";
               
               if (show2 == 1) {
                  
                  checked = "Yes";
               }
               
               out.println("<br><br>Player: " +player2+ ", Username: " +username2+ ", Date: " +date+ ", Time: " +time+ ", Checked-In: " +checked);               
            }
         }        // end of player
                     
         if ((!player3.equals( "" )) && (!player3.equalsIgnoreCase( "x" ))) {

            found = false;
            i = 0;

            ploop3:
            while (i < parm.MAX_Guests) {

               if (player3.startsWith( parm.guest[i] )) {

                  found = true;
                  break ploop3;
               }
               i++;
            }

            if (found == false) {

               //
               //  player must be a member
               //
               if (!username3.equals( "" )) {               // if member

                  //
                  //  Member - get member type and membership type for this member
                  //
                  pstmt4 = con.prepareStatement (
                     "SELECT m_ship, m_type FROM member2b WHERE " +
                     "username = ?");

                  pstmt4.clearParameters();
                  pstmt4.setString(1, username3);
                  rs2 = pstmt4.executeQuery();

                  if (rs2.next()) {

                     found = true;
                  }
                  pstmt4.close();
               }
            }
            
            if (found == false) {         // if not found
               
               checked = "No";
               
               if (show3 == 1) {
                  
                  checked = "Yes";
               }
               
               out.println("<br><br>Player: " +player3+ ", Username: " +username3+ ", Date: " +date+ ", Time: " +time+ ", Checked-In: " +checked);               
            }
         }        // end of player
                     
         if ((!player4.equals( "" )) && (!player4.equalsIgnoreCase( "x" ))) {

            found = false;
            i = 0;

            ploop4:
            while (i < parm.MAX_Guests) {

               if (player4.startsWith( parm.guest[i] )) {

                  found = true;
                  break ploop4;
               }
               i++;
            }

            if (found == false) {

               //
               //  player must be a member
               //
               if (!username4.equals( "" )) {               // if member

                  //
                  //  Member - get member type and membership type for this member
                  //
                  pstmt4 = con.prepareStatement (
                     "SELECT m_ship, m_type FROM member2b WHERE " +
                     "username = ?");

                  pstmt4.clearParameters();
                  pstmt4.setString(1, username4);
                  rs2 = pstmt4.executeQuery();

                  if (rs2.next()) {

                     found = true;
                  }
                  pstmt4.close();
               }
            }
            
            if (found == false) {         // if not found
               
               checked = "No";
               
               if (show4 == 1) {
                  
                  checked = "Yes";
               }
               
               out.println("<br><br>Player: " +player4+ ", Username: " +username4+ ", Date: " +date+ ", Time: " +time+ ", Checked-In: " +checked);               
            }
         }        // end of player
                     
         if ((!player5.equals( "" )) && (!player5.equalsIgnoreCase( "x" ))) {

            found = false;
            i = 0;

            ploop5:
            while (i < parm.MAX_Guests) {

               if (player5.startsWith( parm.guest[i] )) {

                  found = true;
                  break ploop5;
               }
               i++;
            }

            if (found == false) {

               //
               //  player must be a member
               //
               if (!username5.equals( "" )) {               // if member

                  //
                  //  Member - get member type and membership type for this member
                  //
                  pstmt4 = con.prepareStatement (
                     "SELECT m_ship, m_type FROM member2b WHERE " +
                     "username = ?");

                  pstmt4.clearParameters();
                  pstmt4.setString(1, username5);
                  rs2 = pstmt4.executeQuery();

                  if (rs2.next()) {

                     found = true;
                  }
                  pstmt4.close();
               }
            }
            
            if (found == false) {         // if not found
               
               checked = "No";
               
               if (show5 == 1) {
                  
                  checked = "Yes";
               }
               
               out.println("<br><br>Player: " +player5+ ", Username: " +username5+ ", Date: " +date+ ", Time: " +time+ ", Checked-In: " +checked);               
            }
         }        // end of player
                     
      }         // end of while this day and course

      pstmt1.close();
   
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      
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
