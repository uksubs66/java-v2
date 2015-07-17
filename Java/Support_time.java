/***************************************************************************************
 *   Support_time:  This class will display the server's current date & time.
 *                  Also, other functions are processed here from support_main2.htm.
 *
 *
 *   called by:  support_main.htm
 *
 *   created: 3/07/2002   Bob P.
 *
 *   last updated:
 *
 *       4/29/06    Changed the displayLogins method to allow anonymous viewing
 *       9/02/05    Change the verifyEmail method to use the common checkEmail method in Member.
 *       1/24/05    Ver 5 - change club2 to club5.
 *        6/01/04   Add updateDB adn optimizeDB methods for new commands.
 *        5/20/04   Add method to restart the 2 minute timer (SystemUtils.inactTimer).
 *        5/19/04   Add 'Display # of users logged in' method.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


// foretees imports
import com.foretees.common.FeedBack;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;


public class Support_time extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   //
   //  Check for call to display number of users logged in
   //
   if (req.getParameter("login") != null) {

      displayLogins(req, out);             // display the information
      return;
   }
     
   //
   //  Check for call to restart the tee time scan timer 
   //
   if (req.getParameter("timer") != null) {

      startTimer(req, out);             // display the information
      return;
   }

   //
   //  Check for call to process a DB command 
   //
   if (req.getParameter("update") != null) {

      updateDB(req, out);             // process it
      return;
   }

   //
   //  Check for call to Optimize the DB
   //
   if (req.getParameter("optimize") != null) {

      optimizeDB(req, out);             // go opt the db
      return;
   }

   //
   //  Check if call is to fix all email addresses
   //
   if (req.getParameter("email") != null) {

      verifyEmails(req, out);             // go verify email addresses
      return;
   }

   //
   //   Call is to display the server's current date and time.
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   java.util.Date date = cal.getTime();

    out.println("<HTML><HEAD><TITLE>Support Display Time</TITLE></HEAD>");
    out.println("<BODY><CENTER><H3>Display Date & Time</H3>");
    out.println("<BR><BR>The current date and time are:");
    out.println("<BR>"+ date);
    out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
    out.println("</CENTER></BODY></HTML>");
    return;
 }


 // ********************************************************************
 //  Process the 'Display # of Users Logged In' Request
 // ********************************************************************

 private void displayLogins(HttpServletRequest req, PrintWriter out) {


   Connection con = null;                 // init DB objects
   Connection con2 = null;             
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
     
   int count = 0;
   int total = 0;
   int num = 0;
   int reset = 0;
   int in_use = 0;

   HttpSession session = null;
   
   // added to allow unauthenticated users to view a dumb data page
   boolean summary = (req.getParameter("summary") != null) ? true : false;
   
   String user = "";
   
   if (!summary) {
       
   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   user = (String)session.getAttribute("user");   // get username

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   //  Check if we should reset the counts
   //
   if (req.getParameter("reset") != null) {

      reset = 1;
   }
   
   } // end skip session checking for summary results
   
   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "v5";

   try {

      con2 = dbConn.Connect(club);
   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      if (user.startsWith( "sales" )) {
         out.println("<BR><BR> <A HREF=\"/v5/sales_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/v5/support_main2.htm\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //   Display the counts
   //
   if (!summary) {
       
       out.println("<HTML><HEAD><TITLE>Support Display Logins</TITLE></HEAD>");
       out.println("<BODY><CENTER><H3>Display Number of Users Logged In</H3><BR>");
       if (user.startsWith( "sales" )) {
          out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A><BR><BR>");
       } else {
          out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A><BR><BR>");
       }
       out.println("<table border=\"0\" align=\"center\">");
       
   }
   
   //
   //   Get each club's login count and total them
   //
   try {
       
      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name

         count = 0;

         con = dbConn.Connect(club);         // get a connection to this club's db

         stmt = con.createStatement();           // create a statement

         if (reset == 1 && summary == false) {                       // if reset not requested

            try {
               stmt.executeUpdate("UPDATE club5 SET logins = 0");          // reset login count

            }
            catch (Exception ignore) {
            }

         } else {                      // reset requested - set all counts to 0

            try {
               rs = stmt.executeQuery("SELECT logins FROM club5");          // get # of users logged in

               if (rs.next()) {

                  count = rs.getInt("logins");
               }
              
            }
            catch (Exception ignore) {
            }
            
         }

         stmt.close();

         //  calc totals
         total = total + count;
         num++;                    // count the clubs
         if (count > 0) in_use++;  // only inc the in_use if there is someone logged into a club
         
         if (!summary) {
             out.println("<tr><td align=\"left\">");
             out.println("<b>" +count+ "</b> for " +club);
             out.println("</td></tr>");    
         }
         
         con.close();                           // close the connection to the club db
         
      } // end while loop for all clubs
      
      stmt2.close();
      con2.close();
      
   }
   catch (Exception ignore) {
   }

   if (!summary) {
       
       out.println("</TABLE>");
       out.println("<BR><BR>The Total number of users logged into all <b>" +num+ "</b> clubs is: <b>" +total+ "</b>");
       if (user.startsWith( "sales" )) {
          out.println("<BR><BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
       } else {
          out.println("<BR><BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
       }
       out.println("</CENTER></BODY></HTML>");
       
   } else {
   
        out.println("<html>");
        out.println("<head>");
        out.println(" <meta http-equiv=\"refresh\" content=\"60\">");
        out.println("</head>");
        out.println("<body>");
        out.println("Created: " + num + "<br>");
        out.println("Used: " + in_use + "<br>");
        out.println("On: " + total + "<br>");
        out.println("</body></html>");
        
   }
   out.close();
 }


 // ********************************************************************
 //  Process the 'Optimize DB' command
 // ********************************************************************

 private void optimizeDB(HttpServletRequest req, PrintWriter out) {


   Connection con = null;                 // init DB objects
   Connection con2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   int count = 0;
   int total = 0;
   int num = 0;
   int reset = 0;
     
   boolean b = false;

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

   if (!user.equals( "support" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "v5";

   try {

      con2 = dbConn.Connect(club);
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

   //
   // Get the club names from the 'clubs' table
   //
   //  Process each club in the table
   //
   try {

      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name

         try {

            con = dbConn.Connect(club);                   // get a connection to this club's db

            //
            //  Optimize the tables
            //
            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE member2b");          // member2b

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE teecurr2");          // teecurr2

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE events2b");          // events2b

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE evntsup2b");          // evntsup2b

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE restriction2");          // restriction2

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE fives2");          // fives2

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE block2");          // block2

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE guestres2");          // guestres2

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE dbltee2");          // dbltee2

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE buddy");          // buddy

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE lottery3");          // lottery3

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE lreqs3");          // lreqs3

            stmt.close();

            stmt = con.createStatement();           // create a statement

            b = stmt.execute("OPTIMIZE TABLE actlott3");          // actlott3

            stmt.close();

            con.close();                           // close the connection to the club db
         }
         catch (Exception ignore) {
         }
              
      }                                         // do all clubs
      stmt2.close();
      con2.close();
   }
   catch (Exception exc) {
      out.println("<HTML><HEAD><TITLE>DB Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Error</H3>");
      out.println("<BR><BR>Error from the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>DB Opt Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Done</H3>");
   out.println("<BR><BR>Database Optimization Complete for All Clubs</b>");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'update DB' request
 // ********************************************************************

 private void updateDB(HttpServletRequest req, PrintWriter out) {


   Connection con = null;                 // init DB objects
   Statement stmt = null;
   ResultSet rs = null;

   int count = 0;
   int total = 0;
   int num = 0;
   int reset = 0;

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
   String club = (String)session.getAttribute("club");   // get club name

   if (!user.equals( "support" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
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

   String command = req.getParameter("update");         // get the command requested

   //
   //   Use the string received to create the db command 
   //
   try {
     
      stmt = con.createStatement();           // create a statement

      stmt.executeUpdate( command );          // execute the command

      stmt.close();

      con.close();                           // close the connection to the club db
   }
   catch (Exception exc) {
      out.println("<HTML><HEAD><TITLE>DB Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Error</H3>");
      out.println("<BR><BR>Error from the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   out.println("<HTML><HEAD><TITLE>Support Update DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>DB Command Complete</H3><BR>");
   out.println("<BR><BR>The following DB command has been completed: " +command);
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'check emails' request
 // ********************************************************************

 private void verifyEmails(HttpServletRequest req, PrintWriter out) {


   Connection con = null;                 // init DB objects
   Connection con2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;


   int count = 0;
   int total = 0;
   int num = 0;
   int reset = 0;

   boolean invalid = false;
   boolean atFound = false;
   boolean periodFound = false;

   String errormsg = "";
   String fname = "";
   String lname = "";
   String email = "";
   String email2 = "";


   HttpSession session = null;

   Member member = new Member();

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( "support" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "v5";

   try {

      con2 = dbConn.Connect(club);
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

   out.println("<HTML><HEAD><TITLE>Support Repair Email Addresses</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Verify Email Addresses Command</H3><BR>");

   //
   // Get the club names from the 'clubs' table
   //
   //  Process each club in the table
   //
   try {

      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name

         try {

            con = dbConn.Connect(club);                   // get a connection to this club's db

            //
            //  Check each email address for this club
            //
            stmt = con.createStatement();           // create a statement

            rs = stmt.executeQuery("SELECT name_last, name_first, email, email2 FROM member2b");  // get user and email address

//
//  The following statement does not compile - it doesn't like the periods in the REGEXP 
//
//            rs = stmt.executeQuery("SELECT name_last, name_first, email, email2 FROM member2b " +
//                                   "WHERE email NOT REGEXP '^[a-zA-Z0-9\'\._%-]+@[a-zA-Z0-9\._%-]+\.[a-zA-Z]{2,4}$' AND email <> ''");

            while (rs.next()) {

               lname = rs.getString(1);      // get last name
               fname = rs.getString(2);      // get first name
               email = rs.getString(3);      // get the email addresses
               email2 = rs.getString(4);

               if (!email.equals( "" )) {

                  FeedBack feedback = (member.isEmailValid(email));   // verify the address

                  if (!feedback.isPositive()) {              // if error

                     errormsg = feedback.get(0);             // get error message

                     out.println("<BR>"+errormsg+ " <b>Email:</b> " +email+ " <b>Name:</b> " +fname+ " " +lname+ " <b>Club:</b> " +club);
                  }
               }

               if (!email2.equals( "" )) {

                  FeedBack feedback = (member.isEmailValid(email2));   // verify the address

                  if (!feedback.isPositive()) {              // if error

                     errormsg = feedback.get(0);             // get error message

                     out.println("<BR>"+errormsg+ " <b>Email2:</b> " +email2+ " <b>Name:</b> " +fname+ " " +lname+ " <b>Club:</b> " +club);
                  }
               }

            }      // end of while

            stmt.close();

            con.close();                           // close the connection to the club db
         }
         catch (Exception ignore) {
         }

      }                                         // do all clubs
      stmt2.close();
      con2.close();
   }
   catch (Exception exc) {
      out.println("<BR><BR>Error from the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<BR><BR>The email addresses of all members in all clubs have been verified.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the request to restart the 2 minute timer to scan tee times
 // ********************************************************************

 private void startTimer(HttpServletRequest req, PrintWriter out) {


   SystemUtils.inactTimer();

   //
   //   Display the counts
   //
   out.println("<HTML><HEAD><TITLE>Support Display Logins</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Timer Restarted</H3><BR>");
   out.println("<BR><BR>The 2 Minute Timer to Scan Tee Times has been Restarted.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
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
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }

}
